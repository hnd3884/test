package javax.sql.rowset.serial;

import java.io.ObjectOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;
import java.io.OutputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.SQLException;
import java.io.Serializable;
import java.sql.Blob;

public class SerialBlob implements Blob, Serializable, Cloneable
{
    private byte[] buf;
    private Blob blob;
    private long len;
    private long origLen;
    static final long serialVersionUID = -8144641928112860441L;
    
    public SerialBlob(final byte[] array) throws SerialException, SQLException {
        this.len = array.length;
        this.buf = new byte[(int)this.len];
        for (int n = 0; n < this.len; ++n) {
            this.buf[n] = array[n];
        }
        this.origLen = this.len;
    }
    
    public SerialBlob(final Blob blob) throws SerialException, SQLException {
        if (blob == null) {
            throw new SQLException("Cannot instantiate a SerialBlob object with a null Blob object");
        }
        this.len = blob.length();
        this.buf = blob.getBytes(1L, (int)this.len);
        this.blob = blob;
        this.origLen = this.len;
    }
    
    @Override
    public byte[] getBytes(long n, int n2) throws SerialException {
        this.isValid();
        if (n2 > this.len) {
            n2 = (int)this.len;
        }
        if (n < 1L || this.len - n < 0L) {
            throw new SerialException("Invalid arguments: position cannot be less than 1 or greater than the length of the SerialBlob");
        }
        --n;
        final byte[] array = new byte[n2];
        for (int i = 0; i < n2; ++i) {
            array[i] = this.buf[(int)n];
            ++n;
        }
        return array;
    }
    
    @Override
    public long length() throws SerialException {
        this.isValid();
        return this.len;
    }
    
    @Override
    public InputStream getBinaryStream() throws SerialException {
        this.isValid();
        return new ByteArrayInputStream(this.buf);
    }
    
    @Override
    public long position(final byte[] array, final long n) throws SerialException, SQLException {
        this.isValid();
        if (n < 1L || n > this.len) {
            return -1L;
        }
        int n2 = (int)n - 1;
        int n3 = 0;
        final long n4 = array.length;
        while (n2 < this.len) {
            if (array[n3] == this.buf[n2]) {
                if (n3 + 1 == n4) {
                    return n2 + 1 - (n4 - 1L);
                }
                ++n3;
                ++n2;
            }
            else {
                if (array[n3] == this.buf[n2]) {
                    continue;
                }
                ++n2;
            }
        }
        return -1L;
    }
    
    @Override
    public long position(final Blob blob, final long n) throws SerialException, SQLException {
        this.isValid();
        return this.position(blob.getBytes(1L, (int)blob.length()), n);
    }
    
    @Override
    public int setBytes(final long n, final byte[] array) throws SerialException, SQLException {
        return this.setBytes(n, array, 0, array.length);
    }
    
    @Override
    public int setBytes(long n, final byte[] array, final int n2, final int n3) throws SerialException, SQLException {
        this.isValid();
        if (n2 < 0 || n2 > array.length) {
            throw new SerialException("Invalid offset in byte array set");
        }
        if (n < 1L || n > this.length()) {
            throw new SerialException("Invalid position in BLOB object set");
        }
        if (n3 > this.origLen) {
            throw new SerialException("Buffer is not sufficient to hold the value");
        }
        if (n3 + n2 > array.length) {
            throw new SerialException("Invalid OffSet. Cannot have combined offset and length that is greater that the Blob buffer");
        }
        int n4 = 0;
        --n;
        while (n4 < n3 || n2 + n4 + 1 < array.length - n2) {
            this.buf[(int)n + n4] = array[n2 + n4];
            ++n4;
        }
        return n4;
    }
    
    @Override
    public OutputStream setBinaryStream(final long binaryStream) throws SerialException, SQLException {
        this.isValid();
        if (this.blob != null) {
            return this.blob.setBinaryStream(binaryStream);
        }
        throw new SerialException("Unsupported operation. SerialBlob cannot return a writable binary stream, unless instantiated with a Blob object that provides a setBinaryStream() implementation");
    }
    
    @Override
    public void truncate(final long n) throws SerialException {
        this.isValid();
        if (n > this.len) {
            throw new SerialException("Length more than what can be truncated");
        }
        if ((int)n == 0) {
            this.buf = new byte[0];
            this.len = n;
        }
        else {
            this.len = n;
            this.buf = this.getBytes(1L, (int)this.len);
        }
    }
    
    @Override
    public InputStream getBinaryStream(final long n, final long n2) throws SQLException {
        this.isValid();
        if (n < 1L || n > this.length()) {
            throw new SerialException("Invalid position in BLOB object set");
        }
        if (n2 < 1L || n2 > this.len - n + 1L) {
            throw new SerialException("length is < 1 or pos + length > total number of bytes");
        }
        return new ByteArrayInputStream(this.buf, (int)n - 1, (int)n2);
    }
    
    @Override
    public void free() throws SQLException {
        if (this.buf != null) {
            this.buf = null;
            if (this.blob != null) {
                this.blob.free();
            }
            this.blob = null;
        }
    }
    
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof SerialBlob) {
            final SerialBlob serialBlob = (SerialBlob)o;
            if (this.len == serialBlob.len) {
                return Arrays.equals(this.buf, serialBlob.buf);
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
            final SerialBlob serialBlob = (SerialBlob)super.clone();
            serialBlob.buf = (byte[])((this.buf != null) ? Arrays.copyOf(this.buf, (int)this.len) : null);
            serialBlob.blob = null;
            return serialBlob;
        }
        catch (final CloneNotSupportedException ex) {
            throw new InternalError();
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        final ObjectInputStream.GetField fields = objectInputStream.readFields();
        final byte[] array = (byte[])fields.get("buf", null);
        if (array == null) {
            throw new InvalidObjectException("buf is null and should not be!");
        }
        this.buf = array.clone();
        this.len = fields.get("len", 0L);
        if (this.buf.length != this.len) {
            throw new InvalidObjectException("buf is not the expected size");
        }
        this.origLen = fields.get("origLen", 0L);
        this.blob = (Blob)fields.get("blob", null);
    }
    
    private void writeObject(final ObjectOutputStream objectOutputStream) throws IOException, ClassNotFoundException {
        final ObjectOutputStream.PutField putFields = objectOutputStream.putFields();
        putFields.put("buf", this.buf);
        putFields.put("len", this.len);
        putFields.put("origLen", this.origLen);
        putFields.put("blob", (this.blob instanceof Serializable) ? this.blob : null);
        objectOutputStream.writeFields();
    }
    
    private void isValid() throws SerialException {
        if (this.buf == null) {
            throw new SerialException("Error: You cannot call a method on a SerialBlob instance once free() has been called.");
        }
    }
}
