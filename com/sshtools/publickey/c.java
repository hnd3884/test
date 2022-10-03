package com.sshtools.publickey;

import java.io.IOException;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.components.SshCipher;
import com.maverick.ssh.components.ComponentManager;
import java.util.Enumeration;
import com.maverick.util.Base64;
import java.io.PrintWriter;
import java.io.Writer;
import java.util.Hashtable;

class c extends b
{
    private String c;
    private Hashtable e;
    private byte[] d;
    
    public c() {
        this.e = new Hashtable();
    }
    
    public void b(final Writer writer) {
        final PrintWriter printWriter = new PrintWriter(writer, true);
        printWriter.println("-----BEGIN " + this.c + "-----");
        if (!this.e.isEmpty()) {
            final Enumeration keys = this.e.keys();
            while (keys.hasMoreElements()) {
                final String s = (String)keys.nextElement();
                final String s2 = this.e.get(s);
                printWriter.print(s + ": ");
                if (s.length() + s2.length() + 2 > 75) {
                    int i = Math.max(75 - s.length() - 2, 0);
                    printWriter.println(s2.substring(0, i) + "\\");
                    while (i < s2.length()) {
                        if (i + 75 >= s2.length()) {
                            printWriter.println(s2.substring(i));
                        }
                        else {
                            printWriter.println(s2.substring(i, i + 75) + "\\");
                        }
                        i += 75;
                    }
                }
                else {
                    printWriter.println(s2);
                }
            }
            printWriter.println();
        }
        printWriter.println(Base64.encodeBytes(this.d, false));
        printWriter.println("-----END " + this.c + "-----");
    }
    
    public void b(byte[] array, final String s) throws IOException {
        try {
            if (s == null || s.length() == 0) {
                this.b(array);
                return;
            }
            final byte[] array2 = new byte[16];
            ComponentManager.getInstance().getRND().nextBytes(array2);
            final StringBuffer sb = new StringBuffer(16);
            for (int i = 0; i < array2.length; ++i) {
                sb.append(com.sshtools.publickey.b.b[array2[i] >>> 4 & 0xF]);
                sb.append(com.sshtools.publickey.b.b[array2[i] & 0xF]);
            }
            this.e.put("DEK-Info", System.getProperty("maverick.privatekey.encryption", "AES-128-CBC") + "," + (Object)sb);
            this.e.put("Proc-Type", "4,ENCRYPTED");
            final byte[] b = com.sshtools.publickey.b.b(s, array2, 16);
            final SshCipher sshCipher = (SshCipher)ComponentManager.getInstance().supportedSsh2CiphersCS().getInstance("aes128-cbc");
            sshCipher.init(0, array2, b);
            final int n = sshCipher.getBlockSize() - array.length % sshCipher.getBlockSize();
            if (n > 0) {
                final byte[] array3 = new byte[array.length + n];
                System.arraycopy(array, 0, array3, 0, array.length);
                for (int j = array.length; j < array3.length; ++j) {
                    array3[j] = (byte)n;
                }
                array = array3;
            }
            sshCipher.transform(array, 0, array, 0, array.length);
            this.b(array);
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    public void b(final byte[] d) {
        this.d = d;
    }
    
    public void b(final String c) {
        this.c = c;
    }
}
