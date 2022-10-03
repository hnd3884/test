package sun.security.krb5.internal.ktab;

import java.io.UnsupportedEncodingException;
import java.io.IOException;
import java.io.OutputStream;
import sun.security.krb5.internal.util.KrbDataOutputStream;

public class KeyTabOutputStream extends KrbDataOutputStream implements KeyTabConstants
{
    private KeyTabEntry entry;
    private int keyType;
    private byte[] keyValue;
    public int version;
    
    public KeyTabOutputStream(final OutputStream outputStream) {
        super(outputStream);
    }
    
    public void writeVersion(final int version) throws IOException {
        this.write16(this.version = version);
    }
    
    public void writeEntry(final KeyTabEntry keyTabEntry) throws IOException {
        this.write32(keyTabEntry.entryLength());
        final String[] nameStrings = keyTabEntry.service.getNameStrings();
        final int length = nameStrings.length;
        if (this.version == 1281) {
            this.write16(length + 1);
        }
        else {
            this.write16(length);
        }
        byte[] bytes = null;
        try {
            bytes = keyTabEntry.service.getRealmString().getBytes("8859_1");
        }
        catch (final UnsupportedEncodingException ex) {}
        this.write16(bytes.length);
        this.write(bytes);
        for (int i = 0; i < length; ++i) {
            try {
                this.write16(nameStrings[i].getBytes("8859_1").length);
                this.write(nameStrings[i].getBytes("8859_1"));
            }
            catch (final UnsupportedEncodingException ex2) {}
        }
        this.write32(keyTabEntry.service.getNameType());
        this.write32((int)(keyTabEntry.timestamp.getTime() / 1000L));
        this.write8(keyTabEntry.keyVersion % 256);
        this.write16(keyTabEntry.keyType);
        this.write16(keyTabEntry.keyblock.length);
        this.write(keyTabEntry.keyblock);
    }
}
