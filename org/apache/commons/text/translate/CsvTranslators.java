package org.apache.commons.text.translate;

import java.io.IOException;
import org.apache.commons.lang3.StringUtils;
import java.io.Writer;

public final class CsvTranslators
{
    private static final char CSV_DELIMITER = ',';
    private static final char CSV_QUOTE = '\"';
    private static final String CSV_QUOTE_STR;
    private static final String CSV_ESCAPED_QUOTE_STR;
    private static final char[] CSV_SEARCH_CHARS;
    
    private CsvTranslators() {
    }
    
    static {
        CSV_QUOTE_STR = String.valueOf('\"');
        CSV_ESCAPED_QUOTE_STR = CsvTranslators.CSV_QUOTE_STR + CsvTranslators.CSV_QUOTE_STR;
        CSV_SEARCH_CHARS = new char[] { ',', '\"', '\r', '\n' };
    }
    
    public static class CsvEscaper extends SinglePassTranslator
    {
        @Override
        void translateWhole(final CharSequence input, final Writer out) throws IOException {
            final String inputSting = input.toString();
            if (StringUtils.containsNone((CharSequence)inputSting, CsvTranslators.CSV_SEARCH_CHARS)) {
                out.write(inputSting);
            }
            else {
                out.write(34);
                out.write(StringUtils.replace(inputSting, CsvTranslators.CSV_QUOTE_STR, CsvTranslators.CSV_ESCAPED_QUOTE_STR));
                out.write(34);
            }
        }
    }
    
    public static class CsvUnescaper extends SinglePassTranslator
    {
        @Override
        void translateWhole(final CharSequence input, final Writer out) throws IOException {
            if (input.charAt(0) != '\"' || input.charAt(input.length() - 1) != '\"') {
                out.write(input.toString());
                return;
            }
            final String quoteless = input.subSequence(1, input.length() - 1).toString();
            if (StringUtils.containsAny((CharSequence)quoteless, CsvTranslators.CSV_SEARCH_CHARS)) {
                out.write(StringUtils.replace(quoteless, CsvTranslators.CSV_ESCAPED_QUOTE_STR, CsvTranslators.CSV_QUOTE_STR));
            }
            else {
                out.write(input.toString());
            }
        }
    }
}
