package com.sshtools.publickey;

import com.maverick.ssh.components.SshDsaPublicKey;
import java.math.BigInteger;
import com.maverick.ssh.components.SshPrivateKey;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.components.Digest;
import com.maverick.ssh.components.SshCipher;
import com.maverick.util.ByteArrayReader;
import com.maverick.util.Base64;
import com.maverick.ssh.components.SshKeyPair;
import com.maverick.ssh.components.ComponentManager;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;

class g implements SshPrivateKeyFile
{
    byte[] g;
    
    g(final byte[] g) throws IOException {
        if (!c(g)) {
            throw new IOException("Key is not formatted in the PuTTY key format!");
        }
        this.g = g;
    }
    
    public boolean supportsPassphraseChange() {
        return false;
    }
    
    public String getType() {
        return "PuTTY";
    }
    
    public boolean isPassphraseProtected() {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.g)));
        try {
            final String line = bufferedReader.readLine();
            if (line != null && (line.startsWith("PuTTY-User-Key-File-2:") || line.equals("PuTTY-User-Key-File-1:"))) {
                final String line2 = bufferedReader.readLine();
                if (line2 != null && line2.startsWith("Encryption:")) {
                    final String trim = line2.substring(line2.indexOf(":") + 1).trim();
                    if (trim.equals("aes256-cbc")) {
                        return ComponentManager.getInstance().supportedSsh2CiphersCS().contains(trim);
                    }
                }
            }
        }
        catch (final Exception ex) {}
        return false;
    }
    
    public static boolean c(final byte[] array) {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(array)));
        try {
            final String line = bufferedReader.readLine();
            return line != null && (line.startsWith("PuTTY-User-Key-File-2:") || line.equals("PuTTY-User-Key-File-1:"));
        }
        catch (final IOException ex) {
            return false;
        }
    }
    
    public SshKeyPair toKeyPair(final String s) throws IOException, InvalidPassphraseException {
        final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new ByteArrayInputStream(this.g)));
        boolean b = false;
        try {
            final String line = bufferedReader.readLine();
            if (line != null && (line.startsWith("PuTTY-User-Key-File-2:") || line.equals("PuTTY-User-Key-File-1:"))) {
                final int n = line.startsWith("PuTTY-User-Key-File-2:") ? 2 : 1;
                final String trim = line.substring(line.indexOf(":") + 1).trim();
                final String line2 = bufferedReader.readLine();
                if (line2 != null && line2.startsWith("Encryption:")) {
                    final String trim2 = line2.substring(line2.indexOf(":") + 1).trim();
                    final String line3 = bufferedReader.readLine();
                    if (line3 != null && line3.startsWith("Comment:")) {
                        final String line4 = bufferedReader.readLine();
                        if (line4 != null && line4.startsWith("Public-Lines:")) {
                            try {
                                final int int1 = Integer.parseInt(line4.substring(line4.indexOf(":") + 1).trim());
                                String string = "";
                                for (int i = 0; i < int1; ++i) {
                                    final String line5 = bufferedReader.readLine();
                                    if (line5 == null) {
                                        throw new IOException("Corrupt public key data in PuTTY private key");
                                    }
                                    string += line5;
                                }
                                final ByteArrayReader byteArrayReader = new ByteArrayReader(Base64.decode(string));
                                final String line6 = bufferedReader.readLine();
                                if (line6 != null && line6.startsWith("Private-Lines:")) {
                                    final int int2 = Integer.parseInt(line6.substring(line6.indexOf(":") + 1).trim());
                                    String string2 = "";
                                    for (int j = 0; j < int2; ++j) {
                                        final String line7 = bufferedReader.readLine();
                                        if (line7 == null) {
                                            throw new IOException("Corrupt private key data in PuTTY private key");
                                        }
                                        string2 += line7;
                                    }
                                    final byte[] decode = Base64.decode(string2);
                                    if (trim2.equals("aes256-cbc")) {
                                        final SshCipher sshCipher = (SshCipher)ComponentManager.getInstance().supportedSsh2CiphersCS().getInstance(trim2);
                                        final byte[] array = new byte[40];
                                        final byte[] array2 = new byte[40];
                                        final Digest digest = (Digest)ComponentManager.getInstance().supportedDigests().getInstance("SHA-1");
                                        digest.putInt(0);
                                        digest.putBytes(s.getBytes());
                                        final byte[] doFinal = digest.doFinal();
                                        digest.putInt(1);
                                        digest.putBytes(s.getBytes());
                                        final byte[] doFinal2 = digest.doFinal();
                                        System.arraycopy(doFinal, 0, array2, 0, 20);
                                        System.arraycopy(doFinal2, 0, array2, 20, 20);
                                        sshCipher.init(1, array, array2);
                                        sshCipher.transform(decode);
                                        b = true;
                                    }
                                    final ByteArrayReader byteArrayReader2 = new ByteArrayReader(decode);
                                    if (trim.equals("ssh-dss")) {
                                        byteArrayReader.readString();
                                        final BigInteger bigInteger = byteArrayReader.readBigInteger();
                                        final BigInteger bigInteger2 = byteArrayReader.readBigInteger();
                                        final BigInteger bigInteger3 = byteArrayReader.readBigInteger();
                                        final BigInteger bigInteger4 = byteArrayReader.readBigInteger();
                                        final BigInteger bigInteger5 = byteArrayReader2.readBigInteger();
                                        if (n == 1) {}
                                        final SshKeyPair sshKeyPair = new SshKeyPair();
                                        final SshDsaPublicKey dsaPublicKey = ComponentManager.getInstance().createDsaPublicKey(bigInteger, bigInteger2, bigInteger3, bigInteger4);
                                        sshKeyPair.setPublicKey(dsaPublicKey);
                                        sshKeyPair.setPrivateKey(ComponentManager.getInstance().createDsaPrivateKey(bigInteger, bigInteger2, bigInteger3, bigInteger5, dsaPublicKey.getY()));
                                        return sshKeyPair;
                                    }
                                    if (trim.equals("ssh-rsa")) {
                                        byteArrayReader.readString();
                                        final BigInteger bigInteger6 = byteArrayReader.readBigInteger();
                                        final BigInteger bigInteger7 = byteArrayReader.readBigInteger();
                                        final BigInteger bigInteger8 = byteArrayReader2.readBigInteger();
                                        final SshKeyPair sshKeyPair2 = new SshKeyPair();
                                        sshKeyPair2.setPublicKey(ComponentManager.getInstance().createRsaPublicKey(bigInteger7, bigInteger6, 2));
                                        sshKeyPair2.setPrivateKey(ComponentManager.getInstance().createRsaPrivateKey(bigInteger7, bigInteger8));
                                        return sshKeyPair2;
                                    }
                                    throw new IOException("Unexpected key type " + trim);
                                }
                            }
                            catch (final NumberFormatException ex) {}
                            catch (final OutOfMemoryError outOfMemoryError) {}
                        }
                    }
                }
            }
        }
        catch (final Throwable t) {
            if (!b) {
                throw new IOException("The PuTTY key could not be read! " + t.getMessage());
            }
        }
        if (b) {
            throw new InvalidPassphraseException();
        }
        throw new IOException("The PuTTY key could not be read! Invalid format");
    }
    
    public void changePassphrase(final String s, final String s2) throws IOException {
        throw new IOException("Changing passphrase is not supported by the PuTTY key format engine");
    }
    
    public byte[] getFormattedKey() throws IOException {
        return this.g;
    }
}
