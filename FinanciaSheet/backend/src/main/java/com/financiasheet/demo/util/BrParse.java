package com.financiasheet.demo.util;

import java.math.BigDecimal;
import java.text.Normalizer;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

public final class BrParse {
    private BrParse(){}

    // remove acentos e baixa
    public static String norm(String s) {
        if (s == null) return "";
        String n = Normalizer.normalize(s, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        return n.toLowerCase(Locale.ROOT).trim();
    }

    // R$ 1.234,56  |  1,234.56  |  1234.56  |  -10,70
    public static BigDecimal money(String raw) {
        if (raw == null) return BigDecimal.ZERO;
        String s = raw.replace("R$", "")
                .replace(" ", "")
                .replace("\u00A0", "")
                .trim();
        if (s.isEmpty() || s.equals("-")) return BigDecimal.ZERO;

        int lastComma = s.lastIndexOf(',');
        int lastDot = s.lastIndexOf('.');

        if (lastComma >= 0 && lastDot >= 0) {
            if (lastComma > lastDot) {
                // Formato BR: 1.234,56
                s = s.replace(".", "").replace(",", ".");
            } else {
                // Formato US com milhares: 1,234.56
                s = s.replace(",", "");
            }
        } else if (lastComma >= 0) {
            // Apenas virgula -> decimal BR (10,70)
            s = s.replace(".", "").replace(",", ".");
        } else if (lastDot >= 0) {
            // Apenas ponto. Se tiver mais de um, trata como separador de milhares
            if (s.indexOf('.') != lastDot) {
                s = s.replace(".", "");
            }
            s = s.replace(",", "");
        } else {
            // Numero inteiro sem separadores
            s = s.replace(",", "").replace(".", "");
        }

        return new BigDecimal(s);
    }

    // aceita "2025-09-09" | "01/08/2025" | "25/08/25 as 12:22:01"
    public static LocalDate date(String raw) {
        if (raw == null) return null;
        String s = raw.replace("às", "as").replace("AS", "as");
        s = s.split(" as ")[0].trim();  // corta hora se existir
        DateTimeFormatter[] fmts = new DateTimeFormatter[] {
                DateTimeFormatter.ofPattern("yyyy-MM-dd"),
                DateTimeFormatter.ofPattern("dd/MM/yyyy"),
                DateTimeFormatter.ofPattern("dd/MM/yy")
        };
        for (DateTimeFormatter f : fmts) {
            try { return LocalDate.parse(s, f); } catch (Exception ignore) {}
        }
        throw new IllegalArgumentException("Data invalida: " + raw);
    }
}
