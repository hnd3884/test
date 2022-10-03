package org.jfree.util;

import java.util.Iterator;
import java.io.Writer;
import java.io.OutputStreamWriter;
import java.util.List;
import java.util.Collections;
import java.util.ArrayList;
import java.io.IOException;
import java.io.OutputStream;
import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.File;

public class SortedConfigurationWriter
{
    private static final int ESCAPE_KEY = 0;
    private static final int ESCAPE_VALUE = 1;
    private static final int ESCAPE_COMMENT = 2;
    private static final String END_OF_LINE;
    private static final char[] HEX_CHARS;
    
    static {
        END_OF_LINE = StringUtils.getLineSeparator();
        HEX_CHARS = new char[] { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
    }
    
    protected String getDescription(final String key) {
        return null;
    }
    
    public void save(final File file, final Configuration config) throws IOException {
        final BufferedOutputStream out = new BufferedOutputStream(new FileOutputStream(file));
        this.save(out, config);
        out.close();
    }
    
    public void save(final OutputStream outStream, final Configuration config) throws IOException {
        final ArrayList names = new ArrayList();
        final Iterator defaults = config.findPropertyKeys("");
        while (defaults.hasNext()) {
            final String key = defaults.next();
            names.add(key);
        }
        Collections.sort((List<Comparable>)names);
        final OutputStreamWriter out = new OutputStreamWriter(outStream, "iso-8859-1");
        for (int i = 0; i < names.size(); ++i) {
            final String key2 = names.get(i);
            final String value = config.getConfigProperty(key2);
            final String description = this.getDescription(key2);
            if (description != null) {
                this.writeDescription(description, out);
            }
            this.saveConvert(key2, 0, out);
            out.write("=");
            this.saveConvert(value, 1, out);
            out.write(SortedConfigurationWriter.END_OF_LINE);
        }
        out.flush();
    }
    
    public void save(final String filename, final Configuration config) throws IOException {
        this.save(new File(filename), config);
    }
    
    private void saveConvert(final String text, final int escapeMode, final Writer writer) throws IOException {
        final char[] string = text.toCharArray();
        for (int x = 0; x < string.length; ++x) {
            final char aChar = string[x];
            switch (aChar) {
                case ' ': {
                    if (escapeMode != 2 && (x == 0 || escapeMode == 0)) {
                        writer.write(92);
                    }
                    writer.write(32);
                    break;
                }
                case '\\': {
                    writer.write(92);
                    writer.write(92);
                    break;
                }
                case '\t': {
                    if (escapeMode == 2) {
                        writer.write(aChar);
                        break;
                    }
                    writer.write(92);
                    writer.write(116);
                    break;
                }
                case '\n': {
                    writer.write(92);
                    writer.write(110);
                    break;
                }
                case '\r': {
                    writer.write(92);
                    writer.write(114);
                    break;
                }
                case '\f': {
                    if (escapeMode == 2) {
                        writer.write(aChar);
                        break;
                    }
                    writer.write(92);
                    writer.write(102);
                    break;
                }
                case '!':
                case '\"':
                case '#':
                case ':':
                case '=': {
                    if (escapeMode == 2) {
                        writer.write(aChar);
                        break;
                    }
                    writer.write(92);
                    writer.write(aChar);
                    break;
                }
                default: {
                    if (aChar < ' ' || aChar > '~') {
                        writer.write(92);
                        writer.write(117);
                        writer.write(SortedConfigurationWriter.HEX_CHARS[aChar >> 12 & 0xF]);
                        writer.write(SortedConfigurationWriter.HEX_CHARS[aChar >> 8 & 0xF]);
                        writer.write(SortedConfigurationWriter.HEX_CHARS[aChar >> 4 & 0xF]);
                        writer.write(SortedConfigurationWriter.HEX_CHARS[aChar & '\u000f']);
                        break;
                    }
                    writer.write(aChar);
                    break;
                }
            }
        }
    }
    
    private void writeDescription(final String text, final Writer writer) throws IOException {
        if (text.length() == 0) {
            return;
        }
        writer.write("# ");
        writer.write(SortedConfigurationWriter.END_OF_LINE);
        final LineBreakIterator iterator = new LineBreakIterator(text);
        while (iterator.hasNext()) {
            writer.write("# ");
            this.saveConvert((String)iterator.next(), 2, writer);
            writer.write(SortedConfigurationWriter.END_OF_LINE);
        }
    }
}
