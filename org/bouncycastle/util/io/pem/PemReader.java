package org.bouncycastle.util.io.pem;

import java.util.List;
import org.bouncycastle.util.encoders.Base64;
import java.util.ArrayList;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;

public class PemReader extends BufferedReader
{
    private static final String BEGIN = "-----BEGIN ";
    private static final String END = "-----END ";
    
    public PemReader(final Reader reader) {
        super(reader);
    }
    
    public PemObject readPemObject() throws IOException {
        String s;
        for (s = this.readLine(); s != null && !s.startsWith("-----BEGIN "); s = this.readLine()) {}
        if (s != null) {
            final String substring = s.substring("-----BEGIN ".length());
            final int index = substring.indexOf(45);
            final String substring2 = substring.substring(0, index);
            if (index > 0) {
                return this.loadObject(substring2);
            }
        }
        return null;
    }
    
    private PemObject loadObject(final String s) throws IOException {
        final String string = "-----END " + s;
        final StringBuffer sb = new StringBuffer();
        final ArrayList list = new ArrayList();
        String line;
        while ((line = this.readLine()) != null) {
            if (line.indexOf(":") >= 0) {
                final int index = line.indexOf(58);
                list.add(new PemHeader(line.substring(0, index), line.substring(index + 1).trim()));
            }
            else {
                if (line.indexOf(string) != -1) {
                    break;
                }
                sb.append(line.trim());
            }
        }
        if (line == null) {
            throw new IOException(string + " not found");
        }
        return new PemObject(s, list, Base64.decode(sb.toString()));
    }
}
