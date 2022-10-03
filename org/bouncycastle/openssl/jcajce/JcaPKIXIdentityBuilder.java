package org.bouncycastle.openssl.jcajce;

import java.io.FileNotFoundException;
import java.security.PrivateKey;
import java.security.cert.X509Certificate;
import org.bouncycastle.cert.X509CertificateHolder;
import java.util.ArrayList;
import org.bouncycastle.asn1.pkcs.PrivateKeyInfo;
import org.bouncycastle.openssl.PEMKeyPair;
import java.io.Reader;
import org.bouncycastle.openssl.PEMParser;
import java.io.InputStreamReader;
import java.security.cert.CertificateException;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import org.bouncycastle.pkix.jcajce.JcaPKIXIdentity;
import java.io.File;
import java.security.Provider;
import org.bouncycastle.cert.jcajce.JcaX509CertificateConverter;

public class JcaPKIXIdentityBuilder
{
    private JcaPEMKeyConverter keyConverter;
    private JcaX509CertificateConverter certConverter;
    
    public JcaPKIXIdentityBuilder() {
        this.keyConverter = new JcaPEMKeyConverter();
        this.certConverter = new JcaX509CertificateConverter();
    }
    
    public JcaPKIXIdentityBuilder setProvider(final Provider provider) {
        this.keyConverter = this.keyConverter.setProvider(provider);
        this.certConverter = this.certConverter.setProvider(provider);
        return this;
    }
    
    public JcaPKIXIdentityBuilder setProvider(final String s) {
        this.keyConverter = this.keyConverter.setProvider(s);
        this.certConverter = this.certConverter.setProvider(s);
        return this;
    }
    
    public JcaPKIXIdentity build(final File file, final File file2) throws IOException, CertificateException {
        this.checkFile(file);
        this.checkFile(file2);
        final FileInputStream fileInputStream = new FileInputStream(file);
        final FileInputStream fileInputStream2 = new FileInputStream(file2);
        final JcaPKIXIdentity build = this.build(fileInputStream, fileInputStream2);
        fileInputStream.close();
        fileInputStream2.close();
        return build;
    }
    
    public JcaPKIXIdentity build(final InputStream inputStream, final InputStream inputStream2) throws IOException, CertificateException {
        final Object object = new PEMParser(new InputStreamReader(inputStream)).readObject();
        PrivateKey privateKey;
        if (object instanceof PEMKeyPair) {
            privateKey = this.keyConverter.getPrivateKey(((PEMKeyPair)object).getPrivateKeyInfo());
        }
        else {
            if (!(object instanceof PrivateKeyInfo)) {
                throw new IOException("unrecognised private key file");
            }
            privateKey = this.keyConverter.getPrivateKey((PrivateKeyInfo)object);
        }
        final PEMParser pemParser = new PEMParser(new InputStreamReader(inputStream2));
        final ArrayList list = new ArrayList();
        Object object2;
        while ((object2 = pemParser.readObject()) != null) {
            list.add(this.certConverter.getCertificate((X509CertificateHolder)object2));
        }
        return new JcaPKIXIdentity(privateKey, (X509Certificate[])list.toArray(new X509Certificate[list.size()]));
    }
    
    private void checkFile(final File file) throws IOException {
        if (!file.canRead()) {
            return;
        }
        if (file.exists()) {
            throw new IOException("Unable to open file " + file.getPath() + " for reading.");
        }
        throw new FileNotFoundException("Unable to open " + file.getPath() + ": it does not exist.");
    }
}
