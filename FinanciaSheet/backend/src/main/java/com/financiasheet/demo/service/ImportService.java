package com.financiasheet.demo.service;

import com.financiasheet.demo.dto.ImportResponse;
import com.financiasheet.demo.entity.ImportBatch;
import com.financiasheet.demo.entity.Transaction;
import com.financiasheet.demo.entity.User;
import com.financiasheet.demo.repository.ImportBatchRepository;
import com.financiasheet.demo.repository.TransactionRepository;
import com.financiasheet.demo.service.parser.GenericBrCsvParser;
import com.financiasheet.demo.service.parser.TransactionCsvParser;
import com.financiasheet.demo.service.parser.TransactionDraft;
import com.financiasheet.demo.util.BrParse;
import com.financiasheet.demo.util.HashUtil;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.time.LocalDate;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Service
public class ImportService {

    private final TransactionRepository txRepo;
    private final ImportBatchRepository batchRepo;

    public ImportService(TransactionRepository txRepo, ImportBatchRepository batchRepo) {
        this.txRepo = txRepo;
        this.batchRepo = batchRepo;
    }

    public ImportResponse importCsv(User user, String kind, String source, String account, MultipartFile file) {
        int imported = 0, duplicates = 0, errors = 0;
        TransactionCsvParser parser = new GenericBrCsvParser();

        List<TransactionDraft> drafts;       try (InputStream raw = file.getInputStream();            InputStream in = ensureMarkSupported(raw)) {
            drafts = parser.parse(in, charsetFor(file));
        } catch (IOException e) {           throw new RuntimeException("Falha ao ler CSV: " + e.getMessage(), e);
        } catch (Exception e) {
            throw new RuntimeException("Falha ao ler CSV: " + e.getMessage(), e);       }

        List<Transaction> toSave = new ArrayList<>();
        Set<String> seenFingerprints = new HashSet<>();
        for (TransactionDraft d : drafts) {
            try {
                Transaction tx = new Transaction();
                tx.setUser(user);
                tx.setDate(d.date != null ? d.date : LocalDate.now());
                tx.setAmount(adjustAmountByKind(d.amount, kind, d.description));
                tx.setDescription(d.description);
                tx.setAccount(account != null ? account : source);
                tx.setKind(kind);
                tx.setSource(source);
                tx.setExternalId(d.externalId);

                String fp = fingerprint(user, tx);
                tx.setFingerprint(fp);

                if (!seenFingerprints.add(fp)) {
                    duplicates++;
                } else if (txRepo.findByFingerprint(fp).isPresent()) {
                    duplicates++;
                } else {
                    toSave.add(tx);
                }
            } catch (Exception ex) {
                errors++;
            }
        }

        if (!toSave.isEmpty()) {
            txRepo.saveAll(toSave);
            imported += toSave.size();
        }

        ImportBatch b = new ImportBatch();
        b.setUser(user);
        b.setFilename(file.getOriginalFilename() != null ? file.getOriginalFilename() : "upload.csv");
        b.setKind(kind);
        b.setSource(source);
        b.setImportedCount(imported);
        b.setDuplicateCount(duplicates);
        b.setErrorCount(errors);
        batchRepo.save(b);

        return new ImportResponse(b.getId(), imported, duplicates, errors);
    }

    private static BigDecimal adjustAmountByKind(BigDecimal amount, String kind, String description) {
        if (amount == null) {
            return BigDecimal.ZERO;
        }

        if (kind != null && "FATURA".equalsIgnoreCase(kind)) {
            BigDecimal abs = amount.abs();
            String descNorm = description != null ? BrParse.norm(description) : "";

            if (containsAny(descNorm,
                    "pagamento", "pagto", "pago", "pagou",
                    "estorno", "estornado", "estorn",
                    "credito", "credit",
                    "reembolso", "refund",
                    "ajuste", "ajust",
                    "ressarc")) {
                return abs;
            }
            return abs.negate();
        }

        return amount;
    }

    private static boolean containsAny(String text, String... needles) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        for (String n : needles) {
            if (text.contains(n)) {
                return true;
            }
        }
        return false;
    }

    private static String fingerprint(User u, Transaction tx) {
        String key = String.join("|",
                u.getId() != null ? u.getId().toString() : "",
                tx.getDate() != null ? tx.getDate().toString() : "",
                tx.getAmount() != null ? tx.getAmount().toPlainString() : "",
                tx.getDescription() != null ? tx.getDescription().trim().toLowerCase() : "",
                tx.getAccount() != null ? tx.getAccount().toLowerCase() : "",
                tx.getKind() != null ? tx.getKind() : "",
                tx.getExternalId() != null ? tx.getExternalId() : ""
        );
        return HashUtil.sha256(key);
    }

    private static InputStream ensureMarkSupported(InputStream in) { return in.markSupported() ? in : new BufferedInputStream(in);
    } private static Charset charsetFor(MultipartFile f) {
        return StandardCharsets.UTF_8;
    }
}



