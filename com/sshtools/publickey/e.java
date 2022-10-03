package com.sshtools.publickey;

import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.components.ComponentManager;
import com.maverick.ssh.components.SshCipher;
import com.maverick.util.Base64;
import java.io.IOException;
import java.io.Reader;
import java.util.Hashtable;
import java.io.LineNumberReader;

class e extends b
{
    private LineNumberReader f;
    private String g;
    private Hashtable i;
    private byte[] h;
    
    public e(final Reader reader) throws IOException {
        this.f = new LineNumberReader(reader);
        this.d();
    }
    
    private void d() throws IOException {
        String line;
        while ((line = this.f.readLine()) != null) {
            if (line.startsWith("-----") && line.endsWith("-----")) {
                if (line.startsWith("-----BEGIN ")) {
                    this.g = line.substring("-----BEGIN ".length(), line.length() - "-----".length());
                    break;
                }
                throw new IOException("Invalid PEM boundary at line " + this.f.getLineNumber() + ": " + line);
            }
        }
        this.i = new Hashtable();
        String line2;
        while ((line2 = this.f.readLine()) != null) {
            final int index = line2.indexOf(58);
            if (index == -1) {
                break;
            }
            final String trim = line2.substring(0, index).trim();
            if (line2.endsWith("\\")) {
                final StringBuffer sb = new StringBuffer(line2.substring(index + 1, line2.length() - 1).trim());
                String line3;
                while ((line3 = this.f.readLine()) != null) {
                    if (!line3.endsWith("\\")) {
                        sb.append(" ").append(line3.trim());
                        break;
                    }
                    sb.append(" ").append(line3.substring(0, line3.length() - 1).trim());
                }
            }
            else {
                this.i.put(trim, line2.substring(index + 1).trim());
            }
        }
        if (line2 == null) {
            throw new IOException("The key format is invalid! OpenSSH formatted keys must begin with -----BEGIN RSA or -----BEGIN DSA");
        }
        final StringBuffer sb2 = new StringBuffer(line2);
        String line4;
        while ((line4 = this.f.readLine()) != null) {
            if (line4.startsWith("-----") && line4.endsWith("-----")) {
                if (line4.startsWith("-----END " + this.g)) {
                    break;
                }
                throw new IOException("Invalid PEM end boundary at line " + this.f.getLineNumber() + ": " + line4);
            }
            else {
                sb2.append(line4);
            }
        }
        this.h = Base64.decode(sb2.toString());
    }
    
    public Hashtable c() {
        return this.i;
    }
    
    public String b() {
        return this.g;
    }
    
    public byte[] c(final String s) throws IOException {
        try {
            final String s2 = this.i.get("DEK-Info");
            if (s2 == null) {
                return this.h;
            }
            final int index = s2.indexOf(44);
            final String substring = s2.substring(0, index);
            if (!"DES-EDE3-CBC".equalsIgnoreCase(substring) && !"AES-128-CBC".equalsIgnoreCase(substring)) {
                throw new IOException("Unsupported passphrase algorithm: " + substring);
            }
            final String substring2 = s2.substring(index + 1);
            final byte[] array = new byte[substring2.length() / 2];
            for (int i = 0; i < substring2.length(); i += 2) {
                array[i / 2] = (byte)Integer.parseInt(substring2.substring(i, i + 2), 16);
            }
            byte[] array2 = null;
            SshCipher sshCipher = null;
            if ("DES-EDE3-CBC".equalsIgnoreCase(substring)) {
                array2 = com.sshtools.publickey.b.b(s, array, 24);
                sshCipher = (SshCipher)ComponentManager.getInstance().supportedSsh2CiphersCS().getInstance("3des-cbc");
            }
            else if ("AES-128-CBC".equalsIgnoreCase(substring)) {
                array2 = com.sshtools.publickey.b.b(s, array, 16);
                sshCipher = (SshCipher)ComponentManager.getInstance().supportedSsh2CiphersCS().getInstance("aes128-cbc");
            }
            sshCipher.init(1, array, array2);
            final byte[] array3 = new byte[this.h.length];
            sshCipher.transform(this.h, 0, array3, 0, array3.length);
            return array3;
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
}
