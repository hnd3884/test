package org.bouncycastle.openssl.jcajce;

import org.bouncycastle.util.io.pem.PemGenerationException;
import org.bouncycastle.util.io.pem.PemObjectGenerator;
import java.io.IOException;
import org.bouncycastle.openssl.PEMEncryptor;
import java.io.Writer;
import org.bouncycastle.util.io.pem.PemWriter;

public class JcaPEMWriter extends PemWriter
{
    public JcaPEMWriter(final Writer writer) {
        super(writer);
    }
    
    public void writeObject(final Object o) throws IOException {
        this.writeObject(o, null);
    }
    
    public void writeObject(final Object o, final PEMEncryptor pemEncryptor) throws IOException {
        try {
            super.writeObject((PemObjectGenerator)new JcaMiscPEMGenerator(o, pemEncryptor));
        }
        catch (final PemGenerationException ex) {
            if (ex.getCause() instanceof IOException) {
                throw (IOException)ex.getCause();
            }
            throw ex;
        }
    }
    
    public void writeObject(final PemObjectGenerator pemObjectGenerator) throws IOException {
        super.writeObject(pemObjectGenerator);
    }
}
