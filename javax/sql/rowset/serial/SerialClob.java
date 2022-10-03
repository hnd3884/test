package javax.sql.rowset.serial;

import java.io.ObjectOutputStream;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.io.Writer;
import java.io.OutputStream;
import java.io.CharArrayReader;
import java.io.InputStream;
import java.io.Reader;
import java.io.IOException;
import java.io.BufferedReader;
import java.sql.SQLException;
import java.io.Serializable;
import java.sql.Clob;

public class SerialClob implements Clob, Serializable, Cloneable
{
    private char[] buf;
    private Clob clob;
    private long len;
    private long origLen;
    static final long serialVersionUID = -1662519690087375313L;
    
    public SerialClob(final char[] array) throws SerialException, SQLException {
        this.len = array.length;
        this.buf = new char[(int)this.len];
        for (int n = 0; n < this.len; ++n) {
            this.buf[n] = array[n];
        }
        this.origLen = this.len;
        this.clob = null;
    }
    
    public SerialClob(final Clob clob) throws SerialException, SQLException {
        if (clob == null) {
            throw new SQLException("Cannot instantiate a SerialClob object with a null Clob object");
        }
        this.len = clob.length();
        this.clob = clob;
        this.buf = new char[(int)this.len];
        int n = 0;
        try (final Reader characterStream = clob.getCharacterStream()) {
            if (characterStream == null) {
                throw new SQLException("Invalid Clob object. The call to getCharacterStream returned null which cannot be serialized.");
            }
            try (final InputStream asciiStream = clob.getAsciiStream()) {
                if (asciiStream == null) {
                    throw new SQLException("Invalid Clob object. The call to getAsciiStream returned null which cannot be serialized.");
                }
            }
            try (final BufferedReader bufferedReader = new BufferedReader(characterStream)) {
                int i;
                do {
                    i = bufferedReader.read(this.buf, n, (int)(this.len - n));
                    n += i;
                } while (i > 0);
            }
        }
        catch (final IOException ex) {
            throw new SerialException("SerialClob: " + ex.getMessage());
        }
        this.origLen = this.len;
    }
    
    @Override
    public long length() throws SerialException {
        this.isValid();
        return this.len;
    }
    
    @Override
    public Reader getCharacterStream() throws SerialException {
        this.isValid();
        return new CharArrayReader(this.buf);
    }
    
    @Override
    public InputStream getAsciiStream() throws SerialException, SQLException {
        this.isValid();
        if (this.clob != null) {
            return this.clob.getAsciiStream();
        }
        throw new SerialException("Unsupported operation. SerialClob cannot return a the CLOB value as an ascii stream, unless instantiated with a fully implemented Clob object.");
    }
    
    @Override
    public String getSubString(final long n, final int n2) throws SerialException {
        this.isValid();
        if (n < 1L || n > this.length()) {
            throw new SerialException("Invalid position in SerialClob object set");
        }
        if (n - 1L + n2 > this.length()) {
            throw new SerialException("Invalid position and substring length");
        }
        try {
            return new String(this.buf, (int)n - 1, n2);
        }
        catch (final StringIndexOutOfBoundsException ex) {
            throw new SerialException("StringIndexOutOfBoundsException: " + ex.getMessage());
        }
    }
    
    @Override
    public long position(final String s, final long n) throws SerialException, SQLException {
        this.isValid();
        if (n < 1L || n > this.len) {
            return -1L;
        }
        final char[] charArray = s.toCharArray();
        int n2 = (int)n - 1;
        int n3 = 0;
        final long n4 = charArray.length;
        while (n2 < this.len) {
            if (charArray[n3] == this.buf[n2]) {
                if (n3 + 1 == n4) {
                    return n2 + 1 - (n4 - 1L);
                }
                ++n3;
                ++n2;
            }
            else {
                if (charArray[n3] == this.buf[n2]) {
                    continue;
                }
                ++n2;
            }
        }
        return -1L;
    }
    
    @Override
    public long position(final Clob clob, final long n) throws SerialException, SQLException {
        this.isValid();
        return this.position(clob.getSubString(1L, (int)clob.length()), n);
    }
    
    @Override
    public int setString(final long n, final String s) throws SerialException {
        return this.setString(n, s, 0, s.length());
    }
    
    @Override
    public int setString(long n, final String s, final int n2, final int n3) throws SerialException {
        this.isValid();
        final char[] charArray = s.substring(n2).toCharArray();
        if (n2 < 0 || n2 > s.length()) {
            throw new SerialException("Invalid offset in byte array set");
        }
        if (n < 1L || n > this.length()) {
            throw new SerialException("Invalid position in Clob object set");
        }
        if (n3 > this.origLen) {
            throw new SerialException("Buffer is not sufficient to hold the value");
        }
        if (n3 + n2 > s.length()) {
            throw new SerialException("Invalid OffSet. Cannot have combined offset  and length that is greater that the Blob buffer");
        }
        int n4 = 0;
        --n;
        while (n4 < n3 || n2 + n4 + 1 < s.length() - n2) {
            this.buf[(int)n + n4] = charArray[n2 + n4];
            ++n4;
        }
        return n4;
    }
    
    @Override
    public OutputStream setAsciiStream(final long asciiStream) throws SerialException, SQLException {
        this.isValid();
        if (this.clob != null) {
            return this.clob.setAsciiStream(asciiStream);
        }
        throw new SerialException("Unsupported operation. SerialClob cannot return a writable ascii stream\n unless instantiated with a Clob object that has a setAsciiStream() implementation");
    }
    
    @Override
    public Writer setCharacterStream(final long characterStream) throws SerialException, SQLException {
        this.isValid();
        if (this.clob != null) {
            return this.clob.setCharacterStream(characterStream);
        }
        throw new SerialException("Unsupported operation. SerialClob cannot return a writable character stream\n unless instantiated with a Clob object that has a setCharacterStream implementation");
    }
    
    @Override
    public void truncate(final long len) throws SerialException {
        this.isValid();
        if (len > this.len) {
            throw new SerialException("Length more than what can be truncated");
        }
        this.len = len;
        if (this.len == 0L) {
            this.buf = new char[0];
        }
        else {
            this.buf = this.getSubString(1L, (int)this.len).toCharArray();
        }
    }
    
    @Override
    public Reader getCharacterStream(final long n, final long n2) throws SQLException {
        this.isValid();
        if (n < 1L || n > this.len) {
            throw new SerialException("Invalid position in Clob object set");
        }
        if (n - 1L + n2 > this.len) {
            throw new SerialException("Invalid position and substring length");
        }
        if (n2 <= 0L) {
            throw new SerialException("Invalid length specified");
        }
        return new CharArrayReader(this.buf, (int)n, (int)n2);
    }
    
    @Override
    public void free() throws SQLException {
        if (this.buf != null) {
            this.buf = null;
            if (this.clob != null) {
                this.clob.free();
            }
            this.clob = null;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SerialClob) {
            final SerialClob serialClob = (SerialClob)o;
            if (this.len == serialClob.len) {
                return Arrays.equals(this.buf, serialClob.buf);
            }
        }
        return false;
    }
    
    @Override
    public int hashCode() {
        return ((31 + Arrays.hashCode(this.buf)) * 31 + (int)this.len) * 31 + (int)this.origLen;
    }
    
    public Object clone() {
        try {
            final SerialClob serialClob = (SerialClob)super.clone();
            serialClob.buf = (char[])((this.buf != null) ? Arrays.copyOf(this.buf, (int)this.len) : null);
            serialClob.clob = null;
            return serialClob;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final char[] array = (char[])fields.get("buf", null);
        if (array == null) {
            throw new InvalidObjectException("buf is null and should not be!");
        }
        this.buf = array.clone();
        this.len = fields.get("len", 0L);
        if (this.buf.length != this.len) {
            throw new InvalidObjectException("buf is not the expected size");
        }
        this.origLen = fields.get("origLen", 0L);
        this.clob = (Clob)fields.get("clob", null);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("buf", this.buf);
        putFields.put("len", this.len);
        putFields.put("origLen", this.origLen);
        putFields.put("clob", (this.clob instanceof Serializable) ? this.clob : null);
        objectOutputStream.writeFields();
    }
    
    private void isValid() throws SerialException {
        if (this.buf == null) {
            throw new SerialException("Error: You cannot call a method on a SerialClob instance once free() has been called.");
        }
    }
}
