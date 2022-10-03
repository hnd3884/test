package sun.awt.windows;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.io.BufferedInputStream;
import java.io.InputStream;

class HTMLCodec extends InputStream
{
    public static final String ENCODING = "UTF-8";
    public static final String VERSION = "Version:";
    public static final String START_HTML = "StartHTML:";
    public static final String END_HTML = "EndHTML:";
    public static final String START_FRAGMENT = "StartFragment:";
    public static final String END_FRAGMENT = "EndFragment:";
    public static final String START_SELECTION = "StartSelection:";
    public static final String END_SELECTION = "EndSelection:";
    public static final String START_FRAGMENT_CMT = "<!--StartFragment-->";
    public static final String END_FRAGMENT_CMT = "<!--EndFragment-->";
    public static final String SOURCE_URL = "SourceURL:";
    public static final String DEF_SOURCE_URL = "about:blank";
    public static final String EOLN = "\r\n";
    private static final String VERSION_NUM = "1.0";
    private static final int PADDED_WIDTH = 10;
    private final BufferedInputStream bufferedStream;
    private boolean descriptionParsed;
    private boolean closed;
    public static final int BYTE_BUFFER_LEN = 8192;
    public static final int CHAR_BUFFER_LEN = 2730;
    private static final String FAILURE_MSG = "Unable to parse HTML description: ";
    private static final String INVALID_MSG = " invalid";
    private long iHTMLStart;
    private long iHTMLEnd;
    private long iFragStart;
    private long iFragEnd;
    private long iSelStart;
    private long iSelEnd;
    private String stBaseURL;
    private String stVersion;
    private long iStartOffset;
    private long iEndOffset;
    private long iReadCount;
    private EHTMLReadMode readMode;
    
    private static String toPaddedString(final int n, final int n2) {
        String s = "" + n;
        final int length = s.length();
        if (n >= 0 && length < n2) {
            final char[] array = new char[n2 - length];
            Arrays.fill(array, '0');
            final StringBuffer sb = new StringBuffer(n2);
            sb.append(array);
            sb.append(s);
            s = sb.toString();
        }
        return s;
    }
    
    public static byte[] convertToHTMLFormat(final byte[] array) {
        String string = "";
        String string2 = "";
        final String upperCase = new String(array).toUpperCase();
        if (-1 == upperCase.indexOf("<HTML")) {
            string = "<HTML>";
            string2 = "</HTML>";
            if (-1 == upperCase.indexOf("<BODY")) {
                string += "<BODY>";
                string2 = "</BODY>" + string2;
            }
        }
        final String s = "about:blank";
        final int n = "Version:".length() + "1.0".length() + "\r\n".length() + "StartHTML:".length() + 10 + "\r\n".length() + "EndHTML:".length() + 10 + "\r\n".length() + "StartFragment:".length() + 10 + "\r\n".length() + "EndFragment:".length() + 10 + "\r\n".length() + "SourceURL:".length() + s.length() + "\r\n".length();
        final int n2 = n + string.length();
        final int n3 = n2 + array.length - 1;
        final int n4 = n3 + string2.length();
        final StringBuilder sb = new StringBuilder(n2 + "<!--StartFragment-->".length());
        sb.append("Version:");
        sb.append("1.0");
        sb.append("\r\n");
        sb.append("StartHTML:");
        sb.append(toPaddedString(n, 10));
        sb.append("\r\n");
        sb.append("EndHTML:");
        sb.append(toPaddedString(n4, 10));
        sb.append("\r\n");
        sb.append("StartFragment:");
        sb.append(toPaddedString(n2, 10));
        sb.append("\r\n");
        sb.append("EndFragment:");
        sb.append(toPaddedString(n3, 10));
        sb.append("\r\n");
        sb.append("SourceURL:");
        sb.append(s);
        sb.append("\r\n");
        sb.append(string);
        Object bytes = null;
        Object bytes2 = null;
        try {
            bytes = sb.toString().getBytes("UTF-8");
            bytes2 = string2.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {}
        final byte[] array2 = new byte[bytes.length + array.length + bytes2.length];
        System.arraycopy(bytes, 0, array2, 0, bytes.length);
        System.arraycopy(array, 0, array2, bytes.length, array.length - 1);
        System.arraycopy(bytes2, 0, array2, bytes.length + array.length - 1, bytes2.length);
        array2[array2.length - 1] = 0;
        return array2;
    }
    
    public HTMLCodec(final InputStream inputStream, final EHTMLReadMode readMode) throws IOException {
        this.descriptionParsed = false;
        this.closed = false;
        this.bufferedStream = new BufferedInputStream(inputStream, 8192);
        this.readMode = readMode;
    }
    
    public synchronized String getBaseURL() throws IOException {
        if (!this.descriptionParsed) {
            this.parseDescription();
        }
        return this.stBaseURL;
    }
    
    public synchronized String getVersion() throws IOException {
        if (!this.descriptionParsed) {
            this.parseDescription();
        }
        return this.stVersion;
    }
    
    private void parseDescription() throws IOException {
        this.stBaseURL = null;
        this.stVersion = null;
        final long n = -1L;
        this.iSelStart = n;
        this.iSelEnd = n;
        this.iFragStart = n;
        this.iFragEnd = n;
        this.iHTMLStart = n;
        this.iHTMLEnd = n;
        this.bufferedStream.mark(8192);
        final String[] array = { "Version:", "StartHTML:", "EndHTML:", "StartFragment:", "EndFragment:", "StartSelection:", "EndSelection:", "SourceURL:" };
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(this.bufferedStream, "UTF-8"), 2730);
        long ihtmlStart = 0L;
        final long n2 = "\r\n".length();
        for (int length = array.length, i = 0; i < length; ++i) {
            final String line = bufferedReader.readLine();
            if (null == line) {
                break;
            }
            while (i < length) {
                if (line.startsWith(array[i])) {
                    ihtmlStart += line.length() + n2;
                    final String trim = line.substring(array[i].length()).trim();
                    if (null == trim) {
                        break;
                    }
                    try {
                        switch (i) {
                            case 0: {
                                this.stVersion = trim;
                                break;
                            }
                            case 1: {
                                this.iHTMLStart = Integer.parseInt(trim);
                                break;
                            }
                            case 2: {
                                this.iHTMLEnd = Integer.parseInt(trim);
                                break;
                            }
                            case 3: {
                                this.iFragStart = Integer.parseInt(trim);
                                break;
                            }
                            case 4: {
                                this.iFragEnd = Integer.parseInt(trim);
                                break;
                            }
                            case 5: {
                                this.iSelStart = Integer.parseInt(trim);
                                break;
                            }
                            case 6: {
                                this.iSelEnd = Integer.parseInt(trim);
                                break;
                            }
                            case 7: {
                                this.stBaseURL = trim;
                                break;
                            }
                        }
                        break;
                    }
                    catch (final NumberFormatException ex) {
                        throw new IOException("Unable to parse HTML description: " + array[i] + " value " + ex + " invalid");
                    }
                }
                ++i;
            }
        }
        if (-1L == this.iHTMLStart) {
            this.iHTMLStart = ihtmlStart;
        }
        if (-1L == this.iFragStart) {
            this.iFragStart = this.iHTMLStart;
        }
        if (-1L == this.iFragEnd) {
            this.iFragEnd = this.iHTMLEnd;
        }
        if (-1L == this.iSelStart) {
            this.iSelStart = this.iFragStart;
        }
        if (-1L == this.iSelEnd) {
            this.iSelEnd = this.iFragEnd;
        }
        switch (this.readMode) {
            case HTML_READ_ALL: {
                this.iStartOffset = this.iHTMLStart;
                this.iEndOffset = this.iHTMLEnd;
                break;
            }
            case HTML_READ_FRAGMENT: {
                this.iStartOffset = this.iFragStart;
                this.iEndOffset = this.iFragEnd;
                break;
            }
            default: {
                this.iStartOffset = this.iSelStart;
                this.iEndOffset = this.iSelEnd;
                break;
            }
        }
        this.bufferedStream.reset();
        if (-1L == this.iStartOffset) {
            throw new IOException("Unable to parse HTML description: invalid HTML format.");
        }
        int n3;
        for (n3 = 0; n3 < this.iStartOffset; n3 += (int)this.bufferedStream.skip(this.iStartOffset - n3)) {}
        this.iReadCount = n3;
        if (this.iStartOffset != this.iReadCount) {
            throw new IOException("Unable to parse HTML description: Byte stream ends in description.");
        }
        this.descriptionParsed = true;
    }
    
    @Override
    public synchronized int read() throws IOException {
        if (this.closed) {
            throw new IOException("Stream closed");
        }
        if (!this.descriptionParsed) {
            this.parseDescription();
        }
        if (-1L != this.iEndOffset && this.iReadCount >= this.iEndOffset) {
            return -1;
        }
        final int read = this.bufferedStream.read();
        if (read == -1) {
            return -1;
        }
        ++this.iReadCount;
        return read;
    }
    
    @Override
    public synchronized void close() throws IOException {
        if (!this.closed) {
            this.closed = true;
            this.bufferedStream.close();
        }
    }
}
