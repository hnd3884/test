package au.com.bytecode.opencsv;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.io.Reader;
import java.io.BufferedReader;

public class CSVReader
{
    private BufferedReader br;
    private boolean hasNext;
    private char separator;
    private char quotechar;
    private int skipLines;
    private boolean linesSkiped;
    public static final char DEFAULT_SEPARATOR = ',';
    public static final char DEFAULT_QUOTE_CHARACTER = '\"';
    public static final int DEFAULT_SKIP_LINES = 0;
    
    public CSVReader(final Reader reader) {
        this(reader, ',');
    }
    
    public CSVReader(final Reader reader, final char c) {
        this(reader, c, '\"');
    }
    
    public CSVReader(final Reader reader, final char c, final char c2) {
        this(reader, c, c2, 0);
    }
    
    public CSVReader(final Reader reader, final char separator, final char quotechar, final int skipLines) {
        this.hasNext = true;
        this.br = new BufferedReader(reader);
        this.separator = separator;
        this.quotechar = quotechar;
        this.skipLines = skipLines;
    }
    
    public List readAll() throws IOException {
        final ArrayList list = new ArrayList();
        while (this.hasNext) {
            final String[] next = this.readNext();
            if (next != null) {
                list.add(next);
            }
        }
        return list;
    }
    
    public String[] readNext() throws IOException {
        final String nextLine = this.getNextLine();
        return (String[])(this.hasNext ? this.parseLine(nextLine) : null);
    }
    
    private String getNextLine() throws IOException {
        if (!this.linesSkiped) {
            for (int i = 0; i < this.skipLines; ++i) {
                this.br.readLine();
            }
            this.linesSkiped = true;
        }
        final String line = this.br.readLine();
        if (line == null) {
            this.hasNext = false;
        }
        return this.hasNext ? line : null;
    }
    
    private String[] parseLine(String nextLine) throws IOException {
        if (nextLine == null) {
            return null;
        }
        final ArrayList list = new ArrayList();
        StringBuffer sb = new StringBuffer();
        boolean b = false;
        do {
            if (b) {
                sb.append("\n");
                nextLine = this.getNextLine();
                if (nextLine == null) {
                    break;
                }
            }
            for (int i = 0; i < nextLine.length(); ++i) {
                final char char1 = nextLine.charAt(i);
                if (char1 == this.quotechar) {
                    if (b && nextLine.length() > i + 1 && nextLine.charAt(i + 1) == this.quotechar) {
                        sb.append(nextLine.charAt(i + 1));
                        ++i;
                    }
                    else {
                        b = !b;
                    }
                }
                else if (char1 == this.separator && !b) {
                    list.add(sb.toString());
                    sb = new StringBuffer();
                }
                else {
                    sb.append(char1);
                }
            }
        } while (b);
        list.add(sb.toString());
        return (String[])list.toArray(new String[0]);
    }
    
    public void close() throws IOException {
        this.br.close();
    }
}
