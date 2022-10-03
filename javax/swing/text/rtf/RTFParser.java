package javax.swing.text.rtf;

import java.io.IOException;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;

abstract class RTFParser extends AbstractFilter
{
    public int level;
    private int state;
    private StringBuffer currentCharacters;
    private String pendingKeyword;
    private int pendingCharacter;
    private long binaryBytesLeft;
    ByteArrayOutputStream binaryBuf;
    private boolean[] savedSpecials;
    protected PrintStream warnings;
    private final int S_text = 0;
    private final int S_backslashed = 1;
    private final int S_token = 2;
    private final int S_parameter = 3;
    private final int S_aftertick = 4;
    private final int S_aftertickc = 5;
    private final int S_inblob = 6;
    static final boolean[] rtfSpecialsTable;
    
    public abstract boolean handleKeyword(final String p0);
    
    public abstract boolean handleKeyword(final String p0, final int p1);
    
    public abstract void handleText(final String p0);
    
    public void handleText(final char c) {
        this.handleText(String.valueOf(c));
    }
    
    public abstract void handleBinaryBlob(final byte[] p0);
    
    public abstract void begingroup();
    
    public abstract void endgroup();
    
    public RTFParser() {
        this.currentCharacters = new StringBuffer();
        this.state = 0;
        this.pendingKeyword = null;
        this.level = 0;
        this.specialsTable = RTFParser.rtfSpecialsTable;
    }
    
    public void writeSpecial(final int n) throws IOException {
        this.write((char)n);
    }
    
    protected void warning(final String s) {
        if (this.warnings != null) {
            this.warnings.println(s);
        }
    }
    
    @Override
    public void write(String substring) throws IOException {
        if (this.state != 0) {
            int n;
            int length;
            for (n = 0, length = substring.length(); n < length && this.state != 0; ++n) {
                this.write(substring.charAt(n));
            }
            if (n >= length) {
                return;
            }
            substring = substring.substring(n);
        }
        if (this.currentCharacters.length() > 0) {
            this.currentCharacters.append(substring);
        }
        else {
            this.handleText(substring);
        }
    }
    
    public void write(final char c) throws IOException {
        switch (this.state) {
            case 0: {
                if (c == '\n') {
                    break;
                }
                if (c == '\r') {
                    break;
                }
                if (c == '{') {
                    if (this.currentCharacters.length() > 0) {
                        this.handleText(this.currentCharacters.toString());
                        this.currentCharacters = new StringBuffer();
                    }
                    ++this.level;
                    this.begingroup();
                    break;
                }
                if (c == '}') {
                    if (this.currentCharacters.length() > 0) {
                        this.handleText(this.currentCharacters.toString());
                        this.currentCharacters = new StringBuffer();
                    }
                    if (this.level == 0) {
                        throw new IOException("Too many close-groups in RTF text");
                    }
                    this.endgroup();
                    --this.level;
                    break;
                }
                else {
                    if (c == '\\') {
                        if (this.currentCharacters.length() > 0) {
                            this.handleText(this.currentCharacters.toString());
                            this.currentCharacters = new StringBuffer();
                        }
                        this.state = 1;
                        break;
                    }
                    this.currentCharacters.append(c);
                    break;
                }
                break;
            }
            case 1: {
                if (c == '\'') {
                    this.state = 4;
                    break;
                }
                if (!Character.isLetter(c)) {
                    final char[] array = { c };
                    if (!this.handleKeyword(new String(array))) {
                        this.warning("Unknown keyword: " + (Object)array + " (" + (byte)c + ")");
                    }
                    this.state = 0;
                    this.pendingKeyword = null;
                    break;
                }
                this.state = 2;
            }
            case 2: {
                if (Character.isLetter(c)) {
                    this.currentCharacters.append(c);
                    break;
                }
                this.pendingKeyword = this.currentCharacters.toString();
                this.currentCharacters = new StringBuffer();
                if (Character.isDigit(c) || c == '-') {
                    this.state = 3;
                    this.currentCharacters.append(c);
                    break;
                }
                if (!this.handleKeyword(this.pendingKeyword)) {
                    this.warning("Unknown keyword: " + this.pendingKeyword);
                }
                this.pendingKeyword = null;
                this.state = 0;
                if (!Character.isWhitespace(c)) {
                    this.write(c);
                    break;
                }
                break;
            }
            case 3: {
                if (Character.isDigit(c)) {
                    this.currentCharacters.append(c);
                    break;
                }
                if (this.pendingKeyword.equals("bin")) {
                    final long long1 = Long.parseLong(this.currentCharacters.toString());
                    this.pendingKeyword = null;
                    this.state = 6;
                    this.binaryBytesLeft = long1;
                    if (this.binaryBytesLeft > 2147483647L) {
                        this.binaryBuf = new ByteArrayOutputStream(Integer.MAX_VALUE);
                    }
                    else {
                        this.binaryBuf = new ByteArrayOutputStream((int)this.binaryBytesLeft);
                    }
                    this.savedSpecials = this.specialsTable;
                    this.specialsTable = RTFParser.allSpecialsTable;
                    break;
                }
                if (!this.handleKeyword(this.pendingKeyword, Integer.parseInt(this.currentCharacters.toString()))) {
                    this.warning("Unknown keyword: " + this.pendingKeyword + " (param " + (Object)this.currentCharacters + ")");
                }
                this.pendingKeyword = null;
                this.currentCharacters = new StringBuffer();
                this.state = 0;
                if (!Character.isWhitespace(c)) {
                    this.write(c);
                }
                break;
            }
            case 4: {
                if (Character.digit(c, 16) == -1) {
                    this.state = 0;
                    break;
                }
                this.pendingCharacter = Character.digit(c, 16);
                this.state = 5;
                break;
            }
            case 5: {
                this.state = 0;
                if (Character.digit(c, 16) == -1) {
                    break;
                }
                this.pendingCharacter = this.pendingCharacter * 16 + Character.digit(c, 16);
                final char c2 = this.translationTable[this.pendingCharacter];
                if (c2 != '\0') {
                    this.handleText(c2);
                    break;
                }
                break;
            }
            case 6: {
                this.binaryBuf.write(c);
                --this.binaryBytesLeft;
                if (this.binaryBytesLeft == 0L) {
                    this.state = 0;
                    this.specialsTable = this.savedSpecials;
                    this.savedSpecials = null;
                    this.handleBinaryBlob(this.binaryBuf.toByteArray());
                    this.binaryBuf = null;
                    break;
                }
                break;
            }
        }
    }
    
    @Override
    public void flush() throws IOException {
        super.flush();
        if (this.state == 0 && this.currentCharacters.length() > 0) {
            this.handleText(this.currentCharacters.toString());
            this.currentCharacters = new StringBuffer();
        }
    }
    
    @Override
    public void close() throws IOException {
        this.flush();
        if (this.state != 0 || this.level > 0) {
            this.warning("Truncated RTF file.");
            while (this.level > 0) {
                this.endgroup();
                --this.level;
            }
        }
        super.close();
    }
    
    static {
        (rtfSpecialsTable = RTFParser.noSpecialsTable.clone())[10] = true;
        RTFParser.rtfSpecialsTable[13] = true;
        RTFParser.rtfSpecialsTable[123] = true;
        RTFParser.rtfSpecialsTable[125] = true;
        RTFParser.rtfSpecialsTable[92] = true;
    }
}
