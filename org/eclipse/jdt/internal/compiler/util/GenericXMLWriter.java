package org.eclipse.jdt.internal.compiler.util;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;
import java.util.HashMap;
import java.io.Writer;
import java.io.OutputStream;
import java.io.PrintWriter;

public class GenericXMLWriter extends PrintWriter
{
    private static final String XML_VERSION = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>";
    private String lineSeparator;
    private int tab;
    
    private static void appendEscapedChar(final StringBuffer buffer, final char c) {
        final String replacement = getReplacement(c);
        if (replacement != null) {
            buffer.append('&');
            buffer.append(replacement);
            buffer.append(';');
        }
        else {
            buffer.append(c);
        }
    }
    
    private static String getEscaped(final String s) {
        final StringBuffer result = new StringBuffer(s.length() + 10);
        for (int i = 0; i < s.length(); ++i) {
            appendEscapedChar(result, s.charAt(i));
        }
        return result.toString();
    }
    
    private static String getReplacement(final char c) {
        switch (c) {
            case '<': {
                return "lt";
            }
            case '>': {
                return "gt";
            }
            case '\"': {
                return "quot";
            }
            case '\'': {
                return "apos";
            }
            case '&': {
                return "amp";
            }
            default: {
                return null;
            }
        }
    }
    
    public GenericXMLWriter(final OutputStream stream, final String lineSeparator, final boolean printXmlVersion) {
        this(new PrintWriter(stream), lineSeparator, printXmlVersion);
    }
    
    public GenericXMLWriter(final Writer writer, final String lineSeparator, final boolean printXmlVersion) {
        super(writer);
        this.tab = 0;
        this.lineSeparator = lineSeparator;
        if (printXmlVersion) {
            this.print("<?xml version=\"1.0\" encoding=\"UTF-8\"?>");
            this.print(this.lineSeparator);
        }
    }
    
    public void endTag(final String name, final boolean insertTab, final boolean insertNewLine) {
        --this.tab;
        this.printTag(String.valueOf('/') + name, null, insertTab, insertNewLine, false);
    }
    
    public void printString(final String string, final boolean insertTab, final boolean insertNewLine) {
        if (insertTab) {
            this.printTabulation();
        }
        this.print(string);
        if (insertNewLine) {
            this.print(this.lineSeparator);
        }
    }
    
    private void printTabulation() {
        for (int i = 0; i < this.tab; ++i) {
            this.print('\t');
        }
    }
    
    public void printTag(final String name, final HashMap parameters, final boolean insertTab, final boolean insertNewLine, final boolean closeTag) {
        if (insertTab) {
            this.printTabulation();
        }
        this.print('<');
        this.print(name);
        if (parameters != null) {
            final int length = parameters.size();
            final Map.Entry[] entries = new Map.Entry[length];
            parameters.entrySet().toArray(entries);
            Arrays.sort(entries, new Comparator() {
                @Override
                public int compare(final Object o1, final Object o2) {
                    final Map.Entry entry1 = (Map.Entry)o1;
                    final Map.Entry entry2 = (Map.Entry)o2;
                    return entry1.getKey().compareTo((String)entry2.getKey());
                }
            });
            for (int i = 0; i < length; ++i) {
                this.print(' ');
                this.print(entries[i].getKey());
                this.print("=\"");
                this.print(getEscaped(String.valueOf(entries[i].getValue())));
                this.print('\"');
            }
        }
        if (closeTag) {
            this.print("/>");
        }
        else {
            this.print(">");
        }
        if (insertNewLine) {
            this.print(this.lineSeparator);
        }
        if (parameters != null && !closeTag) {
            ++this.tab;
        }
    }
    
    public void startTag(final String name, final boolean insertTab) {
        this.printTag(name, null, insertTab, true, false);
        ++this.tab;
    }
}
