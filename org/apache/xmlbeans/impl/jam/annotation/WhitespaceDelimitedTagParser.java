package org.apache.xmlbeans.impl.jam.annotation;

import java.util.Enumeration;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotation;
import java.util.Properties;
import com.sun.javadoc.Tag;
import org.apache.xmlbeans.impl.jam.mutable.MAnnotatedElement;

public class WhitespaceDelimitedTagParser extends JavadocTagParser
{
    @Override
    public void parse(final MAnnotatedElement target, final Tag tag) {
        final MAnnotation[] anns = this.createAnnotations(target, tag);
        String tagText = tag.text();
        if (tagText == null) {
            return;
        }
        tagText = tagText.trim();
        if (tagText.length() == 0) {
            return;
        }
        final Properties props = new Properties();
        this.parseAssignments(props, tagText);
        if (props.size() > 0) {
            final Enumeration names = props.propertyNames();
            while (names.hasMoreElements()) {
                final String name = names.nextElement();
                this.setValue(anns, name, props.getProperty(name));
            }
        }
        else {
            this.setSingleValueText(anns, tag);
        }
    }
    
    private void parseAssignments(final Properties out, String line) {
        this.getLogger().verbose("PARSING LINE " + line, this);
        final String originalLine = line;
        line = this.removeComments(line);
        while (null != line && -1 != line.indexOf("=")) {
            int keyStart = -1;
            int keyEnd = -1;
            int ind;
            char c;
            for (ind = 0, c = line.charAt(ind); this.isBlank(c); c = line.charAt(ind)) {
                ++ind;
            }
            keyStart = ind;
            while (this.isLegal(line.charAt(ind))) {
                ++ind;
            }
            keyEnd = ind;
            final String key = line.substring(keyStart, keyEnd);
            ind = line.indexOf("=");
            if (ind == -1) {
                return;
            }
            ++ind;
            try {
                c = line.charAt(ind);
            }
            catch (final StringIndexOutOfBoundsException ex) {
                ex.printStackTrace();
            }
            while (this.isBlank(c)) {
                ++ind;
                c = line.charAt(ind);
            }
            int valueStart = -1;
            int valueEnd = -1;
            if (c == '\"') {
                valueStart = ++ind;
                while ('\"' != line.charAt(ind)) {
                    if (++ind >= line.length()) {
                        this.getLogger().verbose("missing double quotes on line " + line, this);
                    }
                }
                valueEnd = ind;
            }
            else {
                for (valueStart = ind++; ind < line.length() && this.isLegal(line.charAt(ind)); ++ind) {}
                valueEnd = ind;
            }
            final String value = line.substring(valueStart, valueEnd);
            if (ind < line.length()) {
                line = line.substring(ind + 1);
            }
            else {
                line = null;
            }
            this.getLogger().verbose("SETTING KEY:" + key + " VALUE:" + value, this);
            out.setProperty(key, value);
        }
    }
    
    private String removeComments(final String value) {
        String result = "";
        final int size = value.length();
        String current = value;
        int currentIndex = 0;
        int beginning = current.indexOf("//");
        final int doubleQuotesIndex = current.indexOf("\"");
        if (-1 != doubleQuotesIndex && doubleQuotesIndex < beginning) {
            result = value;
        }
        else {
            while (currentIndex < size && beginning != -1) {
                beginning = value.indexOf("//", currentIndex);
                if (-1 != beginning) {
                    if (beginning > 0 && value.charAt(beginning - 1) == ':') {
                        currentIndex = beginning + 2;
                    }
                    else {
                        int end = value.indexOf(10, beginning);
                        if (-1 == end) {
                            end = size;
                        }
                        result = result + value.substring(currentIndex, beginning).trim() + "\n";
                        current = value.substring(end);
                        currentIndex = end;
                    }
                }
            }
            result += current;
        }
        return result.trim();
    }
    
    private boolean isBlank(final char c) {
        return c == ' ' || c == '\t' || c == '\n';
    }
    
    private boolean isLegal(final char c) {
        return !this.isBlank(c) && c != '=';
    }
}
