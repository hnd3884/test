package org.apache.xmlbeans.impl.jam.annotation;

import java.io.StringWriter;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotation;
import java.util.StringTokenizer;
import com.sun.javadoc.Tag;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotatedElement;

public class LineDelimitedTagParser extends JavadocTagParser
{
    private static final String VALUE_QUOTE = "\"";
    private static final String LINE_DELIMS = "\n\f\r";
    
    @Override
    public void parse(final MAnnotatedElement target, final Tag tag) {
        if (target == null) {
            throw new IllegalArgumentException("null tagText");
        }
        if (tag == null) {
            throw new IllegalArgumentException("null tagName");
        }
        final MAnnotation[] anns = this.createAnnotations(target, tag);
        final String tagText = tag.text();
        final StringTokenizer st = new StringTokenizer(tagText, "\n\f\r");
        while (st.hasMoreTokens()) {
            final String pair = st.nextToken();
            final int eq = pair.indexOf(61);
            if (eq <= 0) {
                continue;
            }
            final String name = pair.substring(0, eq).trim();
            if (eq >= pair.length() - 1) {
                continue;
            }
            String value = pair.substring(eq + 1).trim();
            if (value.startsWith("\"")) {
                value = this.parseQuotedValue(value.substring(1), st);
            }
            this.setValue(anns, name, value);
        }
    }
    
    private String parseQuotedValue(String line, final StringTokenizer st) {
        final StringWriter out = new StringWriter();
        while (true) {
            final int endQuote = line.indexOf("\"");
            if (endQuote != -1) {
                out.write(line.substring(0, endQuote).trim());
                return out.toString();
            }
            out.write(line);
            if (!st.hasMoreTokens()) {
                return out.toString();
            }
            out.write(10);
            line = st.nextToken().trim();
        }
    }
}
