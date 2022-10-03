package javax.swing.text.rtf;

import java.io.Reader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

abstract class AbstractFilter extends OutputStream
{
    protected char[] translationTable;
    protected boolean[] specialsTable;
    static final char[] latin1TranslationTable;
    static final boolean[] noSpecialsTable;
    static final boolean[] allSpecialsTable;
    
    public void readFromStream(final InputStream inputStream) throws IOException {
        final byte[] array = new byte[16384];
        while (true) {
            final int read = inputStream.read(array);
            if (read < 0) {
                break;
            }
            this.write(array, 0, read);
        }
    }
    
    public void readFromReader(final Reader reader) throws IOException {
        final char[] array = new char[2048];
        while (true) {
            final int read = reader.read(array);
            if (read < 0) {
                break;
            }
            for (int i = 0; i < read; ++i) {
                this.write(array[i]);
            }
        }
    }
    
    public AbstractFilter() {
        this.translationTable = AbstractFilter.latin1TranslationTable;
        this.specialsTable = AbstractFilter.noSpecialsTable;
    }
    
    @Override
    public void write(int n) throws IOException {
        if (n < 0) {
            n += 256;
        }
        if (this.specialsTable[n]) {
            this.writeSpecial(n);
        }
        else {
            final char c = this.translationTable[n];
            if (c != '\0') {
                this.write(c);
            }
        }
    }
    
    @Override
    public void write(final byte[] array, int n, int i) throws IOException {
        StringBuilder sb = null;
        while (i > 0) {
            short n2 = array[n];
            if (n2 < 0) {
                n2 += 256;
            }
            if (this.specialsTable[n2]) {
                if (sb != null) {
                    this.write(sb.toString());
                    sb = null;
                }
                this.writeSpecial(n2);
            }
            else {
                final char c = this.translationTable[n2];
                if (c != '\0') {
                    if (sb == null) {
                        sb = new StringBuilder();
                    }
                    sb.append(c);
                }
            }
            --i;
            ++n;
        }
        if (sb != null) {
            this.write(sb.toString());
        }
    }
    
    public void write(final String s) throws IOException {
        for (int length = s.length(), i = 0; i < length; ++i) {
            this.write(s.charAt(i));
        }
    }
    
    protected abstract void write(final char p0) throws IOException;
    
    protected abstract void writeSpecial(final int p0) throws IOException;
    
    static {
        noSpecialsTable = new boolean[256];
        for (int i = 0; i < 256; ++i) {
            AbstractFilter.noSpecialsTable[i] = false;
        }
        allSpecialsTable = new boolean[256];
        for (int j = 0; j < 256; ++j) {
            AbstractFilter.allSpecialsTable[j] = true;
        }
        latin1TranslationTable = new char[256];
        for (int k = 0; k < 256; ++k) {
            AbstractFilter.latin1TranslationTable[k] = (char)k;
        }
    }
}
