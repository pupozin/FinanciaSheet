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
        Map<String,Integer> headerMap = parser.getHeaderMap();

        // normaliza headers para mapear campos
        Map<String,String> normHeader = new HashMap<>();
        for (String h : headerMap.keySet()) normHeader.put(BrParse.norm(h), h);

        String hDate  = pick(normHeader, "date", "data");
        String hDesc  = pick(normHeader, "title", "descricao", "descrição", "estabelecimento", "descricao*", "historico", "historico*", "descricao*");
        String hVal   = pick(normHeader, "amount", "valor", "valor (r$)");
        String hId    = pick(normHeader, "identificador", "id", "doc", "documento");

        if (hDate == null || hDesc == null || hVal == null) {
            throw new IllegalArgumentException("CSV sem colunas esperadas. Precisa ter Data/Descricao/Valor (qualquer variacao BR).");
        }

        List<TransactionDraft> out = new ArrayList<>();
        for (CSVRecord r : parser) {
            String rawDate = r.get(hDate);
            String rawDesc = r.get(hDesc);
            String rawVal  = r.get(hVal);
            String rawId   = (hId != null && r.isSet(hId)) ? r.get(hId) : null;

            if ((rawDate == null || rawDate.isBlank()) && (rawDesc == null || rawDesc.isBlank())) continue;

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

    private static int count(String s, char c){ int n=0; for(char ch: s.toCharArray()) if (ch==c) n++; return n; }

    private static String pick(Map<String,String> norm, String... keys) {
        for (String k : keys) {
            for (Map.Entry<String,String> e : norm.entrySet()) {
                if (e.getKey().startsWith(k)) return e.getValue();
            }
        }
        return null;
    }

    // Nubank/XP às vezes sinalizam por texto ("Pagamento de fatura" => negativo/positivo)
    private static BigDecimal parseAmountPreservingSign(String raw, String desc) {
        BigDecimal v = BrParse.money(raw);
        // Caso queira inverter sinais por palavras-chave:
        String n = BrParse.norm(desc);
        // aqui deixamos "valor de saída" negativo como está no arquivo BR? Regra: despesas negativas, receitas positivas.
        // Muitos CSV BR trazem "R$ 10,70" sem sinal e o sentido vem do contexto. Se precisar, ajuste aqui.
        return v;
    }
}
