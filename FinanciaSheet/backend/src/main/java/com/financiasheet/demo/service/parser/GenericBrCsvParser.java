package com.financiasheet.demo.service.parser;

import com.financiasheet.demo.util.BrParse;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.util.*;

public class GenericBrCsvParser implements TransactionCsvParser {

    @Override
    public List<TransactionDraft> parse(InputStream in, Charset charset) throws Exception {
        // detecta separador (muito simples: conta ; e , na primeira linha)
        in.mark(8192);
        Scanner sc = new Scanner(in, charset).useDelimiter("\\A");
        String content = sc.hasNext() ? sc.next() : "";
        in.reset();
        char sep = guessSep(content);

        CSVFormat fmt = CSVFormat.DEFAULT.builder()
                .setDelimiter(sep)
                .setTrim(true)
                .setIgnoreEmptyLines(true)
                .setHeader()
                .setSkipHeaderRecord(true)
                .build();

        CSVParser parser = new CSVParser(new InputStreamReader(in, charset), fmt);
        Map<String, Integer> headerMap = parser.getHeaderMap();

        // normaliza headers para mapear campos
        Map<String, String> normHeader = new HashMap<>();
        for (String h : headerMap.keySet()) {
            normHeader.put(BrParse.norm(h), h);
        }

        String hDate = pickFirst(normHeader,
                "date", "data", "data da compra", "data do lancamento", "data lancamento", "data de pagamento", "data compra");
        List<String> descCols = pickAll(normHeader,
                "description", "descricao", "descricao do lancamento", "detalhe", "detalhes", "historico", "historico do lancamento",
                "title", "titulo", "lancamento", "estabelecimento", "merchant", "portador", "observacao", "categoria", "detalhes da fatura");
        String hVal = pickFirst(normHeader,
                "amount", "valor", "valor (r$)", "valor da parcela", "vlr", "valor total", "total", "valor pago", "valor compra");
        List<String> idCols = pickAll(normHeader,
                "identificador", "id", "doc", "documento", "controle", "nsu", "transacao", "numero", "parcela", "descricao curta");

        if (hDate == null || descCols.isEmpty() || hVal == null) {
            throw new IllegalArgumentException("CSV sem colunas esperadas. Precisa ter Data/Descricao/Valor (qualquer variacao BR).");
        }

        List<TransactionDraft> out = new ArrayList<>();
        for (CSVRecord r : parser) {
            String rawDate = r.get(hDate);
            String rawVal = r.get(hVal);
            String rawDesc = buildDescription(r, descCols);
            String rawId = firstNonBlank(r, idCols);

            if ((rawDate == null || rawDate.isBlank()) && (rawDesc == null || rawDesc.isBlank())) {
                continue;
            }

            TransactionDraft d = new TransactionDraft();
            d.date = BrParse.date(rawDate);
            d.description = rawDesc == null ? "" : rawDesc.trim();
            d.amount = parseAmountPreservingSign(rawVal, d.description);
            d.externalId = rawId;

            out.add(d);
        }
        parser.close();
        return out;
    }

    private static char guessSep(String s) {
        int sc = count(s, ';');
        int cc = count(s, ',');
        // se tem ; suficiente (arquivos BR) usa ';', senao ','
        return (sc > cc && sc > 2) ? ';' : ',';
    }

    private static int count(String s, char c) {
        int n = 0;
        for (char ch : s.toCharArray()) {
            if (ch == c) n++;
        }
        return n;
    }

    private static String pickFirst(Map<String, String> norm, String... keys) {
        List<String> all = pickAll(norm, keys);
        return all.isEmpty() ? null : all.get(0);
    }

    private static List<String> pickAll(Map<String, String> norm, String... keys) {
        List<String> out = new ArrayList<>();
        Set<String> seen = new LinkedHashSet<>();
        for (String k : keys) {
            for (Map.Entry<String, String> e : norm.entrySet()) {
                if (matches(e.getKey(), k) && seen.add(e.getValue())) {
                    out.add(e.getValue());
                }
            }
        }
        return out;
    }

    private static boolean matches(String header, String key) {
        if (header.equals(key) || header.startsWith(key)) return true;
        if (key.length() > 3 && header.contains(key)) return true;
        return false;
    }

    private static String buildDescription(CSVRecord r, List<String> columns) {
        List<String> parts = new ArrayList<>();
        for (String col : columns) {
            if (!r.isSet(col)) continue;
            String value = r.get(col);
            if (value == null) continue;
            String trimmed = value.trim();
            if (trimmed.isEmpty()) continue;
            if (!parts.contains(trimmed)) {
                parts.add(trimmed);
            }
        }
        if (parts.isEmpty()) {
            return null;
        }
        return String.join(" - ", parts);
    }

    private static String firstNonBlank(CSVRecord r, List<String> columns) {
        for (String col : columns) {
            if (!r.isSet(col)) continue;
            String value = r.get(col);
            if (value == null) continue;
            String trimmed = value.trim();
            if (!trimmed.isEmpty()) {
                return trimmed;
            }
        }
        return null;
    }

    // Nubank/XP as vezes sinalizam por texto ("Pagamento de fatura" => negativo/positivo)
    private static BigDecimal parseAmountPreservingSign(String raw, String desc) {
        BigDecimal v = BrParse.money(raw);
        // Caso queira inverter sinais por palavras-chave, avaliamos descricao aqui (nao usado por enquanto)
        if (desc != null) {
            BrParse.norm(desc); // mantém extensibilidade futura sem warnings de variavel nao usada
        }
        return v;
    }
}
