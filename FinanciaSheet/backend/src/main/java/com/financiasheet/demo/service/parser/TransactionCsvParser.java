package com.financiasheet.demo.service.parser;

import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.List;

public interface TransactionCsvParser {
    List<TransactionDraft> parse(InputStream in, Charset charset) throws Exception;
}
