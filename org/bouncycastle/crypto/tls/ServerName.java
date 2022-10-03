package org.bouncycastle.crypto.tls;

import java.io.InputStream;
import java.io.IOException;
import java.io.OutputStream;

public class ServerName
{
    protected short nameType;
    protected Object name;
    
    public ServerName(final short nameType, final Object name) {
        if (!isCorrectType(nameType, name)) {
            throw new IllegalArgumentException("'name' is not an instance of the correct type");
        }
        this.nameType = nameType;
        this.name = name;
    }
    
    public short getNameType() {
        return this.nameType;
    }
    
    public Object getName() {
        return this.name;
    }
    
    public String getHostName() {
        if (!isCorrectType((short)0, this.name)) {
            throw new IllegalStateException("'name' is not a HostName string");
        }
        return (String)this.name;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        TlsUtils.writeUint8(this.nameType, outputStream);
        switch (this.nameType) {
            case 0: {
                final byte[] bytes = ((String)this.name).getBytes("ASCII");
                if (bytes.length < 1) {
                    throw new TlsFatalAlert((short)80);
                }
                TlsUtils.writeOpaque16(bytes, outputStream);
                return;
            }
            default: {
                throw new TlsFatalAlert((short)80);
            }
        }
    }
    
    public static ServerName parse(final InputStream inputStream) throws IOException {
        final short uint8 = TlsUtils.readUint8(inputStream);
        switch (uint8) {
            case 0: {
                final byte[] opaque16 = TlsUtils.readOpaque16(inputStream);
                if (opaque16.length < 1) {
                    throw new TlsFatalAlert((short)50);
                }
                return new ServerName(uint8, new String(opaque16, "ASCII"));
            }
            default: {
                throw new TlsFatalAlert((short)50);
            }
        }
    }
    
    protected static boolean isCorrectType(final short n, final Object o) {
        switch (n) {
            case 0: {
                return o instanceof String;
            }
            default: {
                throw new IllegalArgumentException("'nameType' is an unsupported NameType");
            }
        }
    }
}
