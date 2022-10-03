package au.com.bytecode.opencsv;

import java.io.Reader;
import java.sql.Clob;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.ResultSetMetaData;
import java.util.Iterator;
import java.util.List;
import java.io.PrintWriter;
import java.io.Writer;

public class CSVWriter
{
    private Writer rawWriter;
    private PrintWriter pw;
    private char separator;
    private char quotechar;
    public static final char ESCAPE_CHARACTER = '\"';
    public static final char DEFAULT_SEPARATOR = ',';
    public static final char DEFAULT_QUOTE_CHARACTER = '\"';
    public static final char NO_QUOTE_CHARACTER = '\0';
    
    public CSVWriter(final Writer writer) {
        this(writer, ',');
    }
    
    public CSVWriter(final Writer writer, final char c) {
        this(writer, c, '\"');
    }
    
    public CSVWriter(final Writer rawWriter, final char separator, final char quotechar) {
        this.rawWriter = rawWriter;
        this.pw = new PrintWriter(rawWriter);
        this.separator = separator;
        this.quotechar = quotechar;
    }
    
    public void writeAll(final List list) {
        final Iterator iterator = list.iterator();
        while (iterator.hasNext()) {
            this.writeNext((String[])iterator.next());
        }
    }
    
    protected void writeColumnNames(final ResultSetMetaData resultSetMetaData) throws SQLException {
        final int columnCount = resultSetMetaData.getColumnCount();
        final String[] array = new String[columnCount];
        for (int i = 0; i < columnCount; ++i) {
            array[i] = resultSetMetaData.getColumnName(i + 1);
        }
        this.writeNext(array);
    }
    
    public void writeAll(final ResultSet set, final boolean b) throws SQLException, IOException {
        final ResultSetMetaData metaData = set.getMetaData();
        if (b) {
            this.writeColumnNames(metaData);
        }
        final int columnCount = metaData.getColumnCount();
        while (set.next()) {
            final String[] array = new String[columnCount];
            for (int i = 0; i < columnCount; ++i) {
                array[i] = getColumnValue(set, metaData.getColumnType(i + 1), i + 1);
            }
            this.writeNext(array);
        }
    }
    
    private static String getColumnValue(final ResultSet set, final int n, final int n2) throws SQLException, IOException {
        String s = "";
        switch (n) {
            case -7: {
                s = String.valueOf(set.getObject(n2));
                break;
            }
            case 16: {
                final boolean boolean1 = set.getBoolean(n2);
                if (!set.wasNull()) {
                    s = Boolean.valueOf(boolean1).toString();
                    break;
                }
                break;
            }
            case 2005: {
                s = read(set.getClob(n2));
                break;
            }
            case -5:
            case 2:
            case 3:
            case 6:
            case 7:
            case 8: {
                s = "" + set.getBigDecimal(n2).doubleValue();
                break;
            }
            case -6:
            case 4:
            case 5: {
                final int int1 = set.getInt(n2);
                if (!set.wasNull()) {
                    s = "" + int1;
                    break;
                }
                break;
            }
            case 2000: {
                final Object object = set.getObject(n2);
                if (object != null) {
                    s = String.valueOf(object);
                    break;
                }
                break;
            }
            case 91: {
                s = set.getDate(n2).toString();
                break;
            }
            case 92: {
                s = set.getTime(n2).toString();
                break;
            }
            case 93: {
                s = set.getTimestamp(n2).toString();
                break;
            }
            case -1:
            case 1:
            case 12: {
                s = set.getString(n2);
                break;
            }
            default: {
                s = "";
                break;
            }
        }
        return s;
    }
    
    private static String read(final Clob clob) throws SQLException, IOException {
        final StringBuffer sb = new StringBuffer((int)clob.length());
        final Reader characterStream = clob.getCharacterStream();
        final char[] array = new char[2048];
        int read;
        while ((read = characterStream.read(array, 0, array.length)) != -1) {
            if (read > 0) {
                sb.append(array, 0, read);
            }
        }
        return sb.toString();
    }
    
    public void writeNext(final String[] array) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < array.length; ++i) {
            if (i != 0) {
                sb.append(this.separator);
            }
            final String s = array[i];
            if (s != null) {
                if (this.quotechar != '\0') {
                    sb.append(this.quotechar);
                }
                for (int j = 0; j < s.length(); ++j) {
                    final char char1 = s.charAt(j);
                    if (char1 == this.quotechar) {
                        sb.append('\"').append(char1);
                    }
                    else if (char1 == '\"') {
                        sb.append('\"').append(char1);
                    }
                    else {
                        sb.append(char1);
                    }
                }
                if (this.quotechar != '\0') {
                    sb.append(this.quotechar);
                }
            }
        }
        sb.append('\n');
        this.pw.write(sb.toString());
    }
    
    public void close() throws IOException {
        this.pw.flush();
        this.pw.close();
        this.rawWriter.close();
    }
}
