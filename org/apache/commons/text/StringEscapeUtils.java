package org.apache.commons.text;

import java.io.IOException;
import java.io.Writer;
import org.apache.commons.text.translate.NumericEntityUnescaper;
import org.apache.commons.text.translate.UnicodeUnescaper;
import org.apache.commons.text.translate.OctalUnescaper;
import org.apache.commons.text.translate.CsvTranslators;
import org.apache.commons.text.translate.UnicodeUnpairedSurrogateRemover;
import org.apache.commons.text.translate.NumericEntityEscaper;
import org.apache.commons.text.translate.AggregateTranslator;
import org.apache.commons.text.translate.JavaUnicodeEscaper;
import org.apache.commons.text.translate.EntityArrays;
import org.apache.commons.text.translate.LookupTranslator;
import java.util.Map;
import java.util.Collections;
import java.util.HashMap;
import org.apache.commons.text.translate.CharSequenceTranslator;

public class StringEscapeUtils
{
    public static final CharSequenceTranslator ESCAPE_JAVA;
    public static final CharSequenceTranslator ESCAPE_ECMASCRIPT;
    public static final CharSequenceTranslator ESCAPE_JSON;
    public static final CharSequenceTranslator ESCAPE_XML10;
    public static final CharSequenceTranslator ESCAPE_XML11;
    public static final CharSequenceTranslator ESCAPE_HTML3;
    public static final CharSequenceTranslator ESCAPE_HTML4;
    public static final CharSequenceTranslator ESCAPE_CSV;
    public static final CharSequenceTranslator ESCAPE_XSI;
    public static final CharSequenceTranslator UNESCAPE_JAVA;
    public static final CharSequenceTranslator UNESCAPE_ECMASCRIPT;
    public static final CharSequenceTranslator UNESCAPE_JSON;
    public static final CharSequenceTranslator UNESCAPE_HTML3;
    public static final CharSequenceTranslator UNESCAPE_HTML4;
    public static final CharSequenceTranslator UNESCAPE_XML;
    public static final CharSequenceTranslator UNESCAPE_CSV;
    public static final CharSequenceTranslator UNESCAPE_XSI;
    
    public static Builder builder(final CharSequenceTranslator translator) {
        return new Builder(translator);
    }
    
    public static final String escapeJava(final String input) {
        return StringEscapeUtils.ESCAPE_JAVA.translate(input);
    }
    
    public static final String escapeEcmaScript(final String input) {
        return StringEscapeUtils.ESCAPE_ECMASCRIPT.translate(input);
    }
    
    public static final String escapeJson(final String input) {
        return StringEscapeUtils.ESCAPE_JSON.translate(input);
    }
    
    public static final String unescapeJava(final String input) {
        return StringEscapeUtils.UNESCAPE_JAVA.translate(input);
    }
    
    public static final String unescapeEcmaScript(final String input) {
        return StringEscapeUtils.UNESCAPE_ECMASCRIPT.translate(input);
    }
    
    public static final String unescapeJson(final String input) {
        return StringEscapeUtils.UNESCAPE_JSON.translate(input);
    }
    
    public static final String escapeHtml4(final String input) {
        return StringEscapeUtils.ESCAPE_HTML4.translate(input);
    }
    
    public static final String escapeHtml3(final String input) {
        return StringEscapeUtils.ESCAPE_HTML3.translate(input);
    }
    
    public static final String unescapeHtml4(final String input) {
        return StringEscapeUtils.UNESCAPE_HTML4.translate(input);
    }
    
    public static final String unescapeHtml3(final String input) {
        return StringEscapeUtils.UNESCAPE_HTML3.translate(input);
    }
    
    public static String escapeXml10(final String input) {
        return StringEscapeUtils.ESCAPE_XML10.translate(input);
    }
    
    public static String escapeXml11(final String input) {
        return StringEscapeUtils.ESCAPE_XML11.translate(input);
    }
    
    public static final String unescapeXml(final String input) {
        return StringEscapeUtils.UNESCAPE_XML.translate(input);
    }
    
    public static final String escapeCsv(final String input) {
        return StringEscapeUtils.ESCAPE_CSV.translate(input);
    }
    
    public static final String unescapeCsv(final String input) {
        return StringEscapeUtils.UNESCAPE_CSV.translate(input);
    }
    
    public static final String escapeXSI(final String input) {
        return StringEscapeUtils.ESCAPE_XSI.translate(input);
    }
    
    public static final String unescapeXSI(final String input) {
        return StringEscapeUtils.UNESCAPE_XSI.translate(input);
    }
    
    static {
        final Map<CharSequence, CharSequence> escapeJavaMap = new HashMap<CharSequence, CharSequence>();
        escapeJavaMap.put("\"", "\\\"");
        escapeJavaMap.put("\\", "\\\\");
        ESCAPE_JAVA = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(Collections.unmodifiableMap((Map<? extends CharSequence, ? extends CharSequence>)escapeJavaMap)), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE), JavaUnicodeEscaper.outsideOf(32, 127) });
        final Map<CharSequence, CharSequence> escapeEcmaScriptMap = new HashMap<CharSequence, CharSequence>();
        escapeEcmaScriptMap.put("'", "\\'");
        escapeEcmaScriptMap.put("\"", "\\\"");
        escapeEcmaScriptMap.put("\\", "\\\\");
        escapeEcmaScriptMap.put("/", "\\/");
        ESCAPE_ECMASCRIPT = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(Collections.unmodifiableMap((Map<? extends CharSequence, ? extends CharSequence>)escapeEcmaScriptMap)), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE), JavaUnicodeEscaper.outsideOf(32, 127) });
        final Map<CharSequence, CharSequence> escapeJsonMap = new HashMap<CharSequence, CharSequence>();
        escapeJsonMap.put("\"", "\\\"");
        escapeJsonMap.put("\\", "\\\\");
        escapeJsonMap.put("/", "\\/");
        ESCAPE_JSON = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(Collections.unmodifiableMap((Map<? extends CharSequence, ? extends CharSequence>)escapeJsonMap)), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_ESCAPE), JavaUnicodeEscaper.outsideOf(32, 126) });
        final Map<CharSequence, CharSequence> escapeXml10Map = new HashMap<CharSequence, CharSequence>();
        escapeXml10Map.put("\u0000", "");
        escapeXml10Map.put("\u0001", "");
        escapeXml10Map.put("\u0002", "");
        escapeXml10Map.put("\u0003", "");
        escapeXml10Map.put("\u0004", "");
        escapeXml10Map.put("\u0005", "");
        escapeXml10Map.put("\u0006", "");
        escapeXml10Map.put("\u0007", "");
        escapeXml10Map.put("\b", "");
        escapeXml10Map.put("\u000b", "");
        escapeXml10Map.put("\f", "");
        escapeXml10Map.put("\u000e", "");
        escapeXml10Map.put("\u000f", "");
        escapeXml10Map.put("\u0010", "");
        escapeXml10Map.put("\u0011", "");
        escapeXml10Map.put("\u0012", "");
        escapeXml10Map.put("\u0013", "");
        escapeXml10Map.put("\u0014", "");
        escapeXml10Map.put("\u0015", "");
        escapeXml10Map.put("\u0016", "");
        escapeXml10Map.put("\u0017", "");
        escapeXml10Map.put("\u0018", "");
        escapeXml10Map.put("\u0019", "");
        escapeXml10Map.put("\u001a", "");
        escapeXml10Map.put("\u001b", "");
        escapeXml10Map.put("\u001c", "");
        escapeXml10Map.put("\u001d", "");
        escapeXml10Map.put("\u001e", "");
        escapeXml10Map.put("\u001f", "");
        escapeXml10Map.put("\ufffe", "");
        escapeXml10Map.put("\uffff", "");
        ESCAPE_XML10 = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(EntityArrays.BASIC_ESCAPE), new LookupTranslator(EntityArrays.APOS_ESCAPE), new LookupTranslator(Collections.unmodifiableMap((Map<? extends CharSequence, ? extends CharSequence>)escapeXml10Map)), NumericEntityEscaper.between(127, 132), NumericEntityEscaper.between(134, 159), new UnicodeUnpairedSurrogateRemover() });
        final Map<CharSequence, CharSequence> escapeXml11Map = new HashMap<CharSequence, CharSequence>();
        escapeXml11Map.put("\u0000", "");
        escapeXml11Map.put("\u000b", "&#11;");
        escapeXml11Map.put("\f", "&#12;");
        escapeXml11Map.put("\ufffe", "");
        escapeXml11Map.put("\uffff", "");
        ESCAPE_XML11 = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(EntityArrays.BASIC_ESCAPE), new LookupTranslator(EntityArrays.APOS_ESCAPE), new LookupTranslator(Collections.unmodifiableMap((Map<? extends CharSequence, ? extends CharSequence>)escapeXml11Map)), NumericEntityEscaper.between(1, 8), NumericEntityEscaper.between(14, 31), NumericEntityEscaper.between(127, 132), NumericEntityEscaper.between(134, 159), new UnicodeUnpairedSurrogateRemover() });
        ESCAPE_HTML3 = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(EntityArrays.BASIC_ESCAPE), new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE) });
        ESCAPE_HTML4 = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(EntityArrays.BASIC_ESCAPE), new LookupTranslator(EntityArrays.ISO8859_1_ESCAPE), new LookupTranslator(EntityArrays.HTML40_EXTENDED_ESCAPE) });
        ESCAPE_CSV = new CsvTranslators.CsvEscaper();
        final Map<CharSequence, CharSequence> escapeXsiMap = new HashMap<CharSequence, CharSequence>();
        escapeXsiMap.put("|", "\\|");
        escapeXsiMap.put("&", "\\&");
        escapeXsiMap.put(";", "\\;");
        escapeXsiMap.put("<", "\\<");
        escapeXsiMap.put(">", "\\>");
        escapeXsiMap.put("(", "\\(");
        escapeXsiMap.put(")", "\\)");
        escapeXsiMap.put("$", "\\$");
        escapeXsiMap.put("`", "\\`");
        escapeXsiMap.put("\\", "\\\\");
        escapeXsiMap.put("\"", "\\\"");
        escapeXsiMap.put("'", "\\'");
        escapeXsiMap.put(" ", "\\ ");
        escapeXsiMap.put("\t", "\\\t");
        escapeXsiMap.put("\r\n", "");
        escapeXsiMap.put("\n", "");
        escapeXsiMap.put("*", "\\*");
        escapeXsiMap.put("?", "\\?");
        escapeXsiMap.put("[", "\\[");
        escapeXsiMap.put("#", "\\#");
        escapeXsiMap.put("~", "\\~");
        escapeXsiMap.put("=", "\\=");
        escapeXsiMap.put("%", "\\%");
        ESCAPE_XSI = new LookupTranslator(Collections.unmodifiableMap((Map<? extends CharSequence, ? extends CharSequence>)escapeXsiMap));
        final Map<CharSequence, CharSequence> unescapeJavaMap = new HashMap<CharSequence, CharSequence>();
        unescapeJavaMap.put("\\\\", "\\");
        unescapeJavaMap.put("\\\"", "\"");
        unescapeJavaMap.put("\\'", "'");
        unescapeJavaMap.put("\\", "");
        UNESCAPE_JAVA = new AggregateTranslator(new CharSequenceTranslator[] { new OctalUnescaper(), new UnicodeUnescaper(), new LookupTranslator(EntityArrays.JAVA_CTRL_CHARS_UNESCAPE), new LookupTranslator(Collections.unmodifiableMap((Map<? extends CharSequence, ? extends CharSequence>)unescapeJavaMap)) });
        UNESCAPE_ECMASCRIPT = StringEscapeUtils.UNESCAPE_JAVA;
        UNESCAPE_JSON = StringEscapeUtils.UNESCAPE_JAVA;
        UNESCAPE_HTML3 = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(EntityArrays.BASIC_UNESCAPE), new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE), new NumericEntityUnescaper(new NumericEntityUnescaper.OPTION[0]) });
        UNESCAPE_HTML4 = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(EntityArrays.BASIC_UNESCAPE), new LookupTranslator(EntityArrays.ISO8859_1_UNESCAPE), new LookupTranslator(EntityArrays.HTML40_EXTENDED_UNESCAPE), new NumericEntityUnescaper(new NumericEntityUnescaper.OPTION[0]) });
        UNESCAPE_XML = new AggregateTranslator(new CharSequenceTranslator[] { new LookupTranslator(EntityArrays.BASIC_UNESCAPE), new LookupTranslator(EntityArrays.APOS_UNESCAPE), new NumericEntityUnescaper(new NumericEntityUnescaper.OPTION[0]) });
        UNESCAPE_CSV = new CsvTranslators.CsvUnescaper();
        UNESCAPE_XSI = new XsiUnescaper();
    }
    
    static class XsiUnescaper extends CharSequenceTranslator
    {
        private static final char BACKSLASH = '\\';
        
        @Override
        public int translate(final CharSequence input, final int index, final Writer out) throws IOException {
            if (index != 0) {
                throw new IllegalStateException("XsiUnescaper should never reach the [1] index");
            }
            final String s = input.toString();
            int segmentStart = 0;
            int searchOffset = 0;
            while (true) {
                final int pos = s.indexOf(92, searchOffset);
                if (pos == -1) {
                    break;
                }
                if (pos > segmentStart) {
                    out.write(s.substring(segmentStart, pos));
                }
                segmentStart = pos + 1;
                searchOffset = pos + 2;
            }
            if (segmentStart < s.length()) {
                out.write(s.substring(segmentStart));
            }
            return Character.codePointCount(input, 0, input.length());
        }
    }
    
    public static final class Builder
    {
        private final StringBuilder sb;
        private final CharSequenceTranslator translator;
        
        private Builder(final CharSequenceTranslator translator) {
            this.sb = new StringBuilder();
            this.translator = translator;
        }
        
        public Builder escape(final String input) {
            this.sb.append(this.translator.translate(input));
            return this;
        }
        
        public Builder append(final String input) {
            this.sb.append(input);
            return this;
        }
        
        @Override
        public String toString() {
            return this.sb.toString();
        }
    }
}
