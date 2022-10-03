package com.sshtools.publickey;

import java.io.Writer;
import java.io.StringWriter;
import com.maverick.ssh.components.SshRsaPrivateCrtKey;
import com.maverick.ssh.components.SshDsaPrivateKey;
import com.maverick.util.SimpleASNWriter;
import com.maverick.ssh.components.SshDsaPublicKey;
import com.maverick.ssh.SshException;
import com.maverick.ssh.SshIOException;
import com.maverick.ssh.components.SshPrivateKey;
import com.maverick.ssh.components.SshPublicKey;
import com.maverick.ssh.components.ComponentManager;
import java.math.BigInteger;
import com.maverick.util.SimpleASNReader;
import java.io.Reader;
import java.io.StringReader;
import com.maverick.ssh.components.SshKeyPair;
import java.io.IOException;

class j implements SshPrivateKeyFile
{
    byte[] l;
    
    j(final byte[] l) throws IOException {
        if (!d(l)) {
            throw new IOException("Formatted key data is not a valid OpenSSH key format");
        }
        this.l = l;
    }
    
    j(final SshKeyPair sshKeyPair, final String s) throws IOException {
        this.l = this.b(sshKeyPair, s);
    }
    
    public boolean isPassphraseProtected() {
        try {
            return new e(new StringReader(new String(this.l, "US-ASCII"))).c().containsKey("DEK-Info");
        }
        catch (final IOException ex) {
            return true;
        }
    }
    
    public String getType() {
        return "OpenSSH";
    }
    
    public boolean supportsPassphraseChange() {
        return true;
    }
    
    public SshKeyPair toKeyPair(final String s) throws IOException, InvalidPassphraseException {
        final e e = new e(new StringReader(new String(this.l, "US-ASCII")));
        final SimpleASNReader simpleASNReader = new SimpleASNReader(e.c(s));
        try {
            if ("DSA PRIVATE KEY".equals(e.b())) {
                return this.b(simpleASNReader);
            }
            if ("RSA PRIVATE KEY".equals(e.b())) {
                return this.c(simpleASNReader);
            }
            throw new IOException("Unsupported type: " + e.b());
        }
        catch (final IOException ex) {
            throw new InvalidPassphraseException(ex);
        }
    }
    
    SshKeyPair c(final SimpleASNReader simpleASNReader) throws IOException {
        try {
            simpleASNReader.assertByte(48);
            simpleASNReader.getLength();
            simpleASNReader.assertByte(2);
            simpleASNReader.getData();
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger2 = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger3 = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger4 = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger5 = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger6 = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger7 = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger8 = new BigInteger(simpleASNReader.getData());
            final SshKeyPair sshKeyPair = new SshKeyPair();
            sshKeyPair.setPublicKey(ComponentManager.getInstance().createRsaPublicKey(bigInteger, bigInteger2, 2));
            sshKeyPair.setPrivateKey(ComponentManager.getInstance().createRsaPrivateCrtKey(bigInteger, bigInteger2, bigInteger3, bigInteger4, bigInteger5, bigInteger6, bigInteger7, bigInteger8));
            return sshKeyPair;
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    SshKeyPair b(final SimpleASNReader simpleASNReader) throws IOException {
        try {
            simpleASNReader.assertByte(48);
            simpleASNReader.getLength();
            simpleASNReader.assertByte(2);
            simpleASNReader.getData();
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger2 = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger3 = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger4 = new BigInteger(simpleASNReader.getData());
            simpleASNReader.assertByte(2);
            final BigInteger bigInteger5 = new BigInteger(simpleASNReader.getData());
            final SshKeyPair sshKeyPair = new SshKeyPair();
            final SshDsaPublicKey dsaPublicKey = ComponentManager.getInstance().createDsaPublicKey(bigInteger, bigInteger2, bigInteger3, bigInteger4);
            sshKeyPair.setPublicKey(dsaPublicKey);
            sshKeyPair.setPrivateKey(ComponentManager.getInstance().createDsaPrivateKey(bigInteger, bigInteger2, bigInteger3, bigInteger5, dsaPublicKey.getY()));
            return sshKeyPair;
        }
        catch (final SshException ex) {
            throw new SshIOException(ex);
        }
    }
    
    void b(final SimpleASNWriter simpleASNWriter, final SshDsaPrivateKey sshDsaPrivateKey, final SshDsaPublicKey sshDsaPublicKey) {
        final SimpleASNWriter simpleASNWriter2 = new SimpleASNWriter();
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(new byte[1]);
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshDsaPublicKey.getP().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshDsaPublicKey.getQ().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshDsaPublicKey.getG().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshDsaPublicKey.getY().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshDsaPrivateKey.getX().toByteArray());
        final byte[] byteArray = simpleASNWriter2.toByteArray();
        simpleASNWriter.writeByte(48);
        simpleASNWriter.writeData(byteArray);
    }
    
    void b(final SimpleASNWriter simpleASNWriter, final SshRsaPrivateCrtKey sshRsaPrivateCrtKey) {
        final SimpleASNWriter simpleASNWriter2 = new SimpleASNWriter();
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(new byte[1]);
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshRsaPrivateCrtKey.getModulus().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshRsaPrivateCrtKey.getPublicExponent().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshRsaPrivateCrtKey.getPrivateExponent().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshRsaPrivateCrtKey.getPrimeP().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshRsaPrivateCrtKey.getPrimeQ().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshRsaPrivateCrtKey.getPrimeExponentP().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshRsaPrivateCrtKey.getPrimeExponentQ().toByteArray());
        simpleASNWriter2.writeByte(2);
        simpleASNWriter2.writeData(sshRsaPrivateCrtKey.getCrtCoefficient().toByteArray());
        final byte[] byteArray = simpleASNWriter2.toByteArray();
        simpleASNWriter.writeByte(48);
        simpleASNWriter.writeData(byteArray);
    }
    
    public byte[] b(final SshKeyPair sshKeyPair, final String s) throws IOException {
        final c c = new c();
        final SimpleASNWriter simpleASNWriter = new SimpleASNWriter();
        byte[] array;
        if (sshKeyPair.getPublicKey() instanceof SshDsaPublicKey) {
            this.b(simpleASNWriter, (SshDsaPrivateKey)sshKeyPair.getPrivateKey(), (SshDsaPublicKey)sshKeyPair.getPublicKey());
            array = simpleASNWriter.toByteArray();
            c.b("DSA PRIVATE KEY");
        }
        else {
            if (!(sshKeyPair.getPrivateKey() instanceof SshRsaPrivateCrtKey)) {
                throw new IOException(sshKeyPair.getPublicKey().getAlgorithm() + " is not supported");
            }
            this.b(simpleASNWriter, (SshRsaPrivateCrtKey)sshKeyPair.getPrivateKey());
            array = simpleASNWriter.toByteArray();
            c.b("RSA PRIVATE KEY");
        }
        c.b(array, s);
        final StringWriter stringWriter = new StringWriter();
        c.b(stringWriter);
        return stringWriter.toString().getBytes("UTF-8");
    }
    
    public void changePassphrase(final String s, final String s2) throws IOException, InvalidPassphraseException {
        this.l = this.b(this.toKeyPair(s), s2);
    }
    
    public byte[] getFormattedKey() {
        return this.l;
    }
    
    public static boolean d(final byte[] array) {
        try {
            new e(new StringReader(new String(array, "UTF-8")));
            return true;
        }
        catch (final IOException ex) {
            return false;
        }
    }
}
