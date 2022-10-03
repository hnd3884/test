package org.bouncycastle.crypto.tls;

import org.bouncycastle.util.Arrays;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.IOException;
import org.bouncycastle.util.io.Streams;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Vector;

public class ServerNameList
{
    protected Vector serverNameList;
    
    public ServerNameList(final Vector serverNameList) {
        if (serverNameList == null) {
            throw new IllegalArgumentException("'serverNameList' must not be null");
        }
        this.serverNameList = serverNameList;
    }
    
    public Vector getServerNameList() {
        return this.serverNameList;
    }
    
    public void encode(final OutputStream outputStream) throws IOException {
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        short[] checkNameType = new short[0];
        for (int i = 0; i < this.serverNameList.size(); ++i) {
            final ServerName serverName = this.serverNameList.elementAt(i);
            checkNameType = checkNameType(checkNameType, serverName.getNameType());
            if (checkNameType == null) {
                throw new TlsFatalAlert((short)80);
            }
            serverName.encode(byteArrayOutputStream);
        }
        TlsUtils.checkUint16(byteArrayOutputStream.size());
        TlsUtils.writeUint16(byteArrayOutputStream.size(), outputStream);
        Streams.writeBufTo(byteArrayOutputStream, outputStream);
    }
    
    public static ServerNameList parse(final InputStream inputStream) throws IOException {
        final int uint16 = TlsUtils.readUint16(inputStream);
        if (uint16 < 1) {
            throw new TlsFatalAlert((short)50);
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(TlsUtils.readFully(uint16, inputStream));
        short[] checkNameType = new short[0];
        final Vector vector = new Vector();
        while (byteArrayInputStream.available() > 0) {
            final ServerName parse = ServerName.parse(byteArrayInputStream);
            checkNameType = checkNameType(checkNameType, parse.getNameType());
            if (checkNameType == null) {
                throw new TlsFatalAlert((short)47);
            }
            vector.addElement(parse);
        }
        return new ServerNameList(vector);
    }
    
    private static short[] checkNameType(final short[] array, final short n) {
        if (!NameType.isValid(n) || Arrays.contains(array, n)) {
            return null;
        }
        return Arrays.append(array, n);
    }
}
