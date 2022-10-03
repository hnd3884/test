package javax.crypto;

import sun.misc.SharedSecrets;
import java.io.InputStream;
import java.io.ByteArrayInputStream;
import java.security.InvalidAlgorithmParameterException;
import java.security.AlgorithmParameters;
import java.io.ObjectInputStream;
import java.security.InvalidKeyException;
import java.security.NoSuchProviderException;
import java.security.NoSuchAlgorithmException;
import java.security.Key;
import java.io.IOException;
import java.io.OutputStream;
import java.io.ObjectOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.Serializable;

public class SealedObject implements Serializable
{
    static final long serialVersionUID = 4482838265551344752L;
    private byte[] encryptedContent;
    private String sealAlg;
    private String paramsAlg;
    protected byte[] encodedParams;
    
    public SealedObject(final Serializable s, final Cipher cipher) throws IOException, IllegalBlockSizeException {
        this.encryptedContent = null;
        this.sealAlg = null;
        this.paramsAlg = null;
        this.encodedParams = null;
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        final ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
        byte[] byteArray;
        try {
            objectOutputStream.writeObject(s);
            objectOutputStream.flush();
            byteArray = byteArrayOutputStream.toByteArray();
        }
        finally {
            objectOutputStream.close();
        }
        try {
            this.encryptedContent = cipher.doFinal(byteArray);
        }
        catch (final BadPaddingException ex) {}
        if (cipher.getParameters() != null) {
            this.encodedParams = cipher.getParameters().getEncoded();
            this.paramsAlg = cipher.getParameters().getAlgorithm();
        }
        this.sealAlg = cipher.getAlgorithm();
    }
    
    protected SealedObject(final SealedObject sealedObject) {
        this.encryptedContent = null;
        this.sealAlg = null;
        this.paramsAlg = null;
        this.encodedParams = null;
        this.encryptedContent = sealedObject.encryptedContent.clone();
        this.sealAlg = sealedObject.sealAlg;
        this.paramsAlg = sealedObject.paramsAlg;
        if (sealedObject.encodedParams != null) {
            this.encodedParams = sealedObject.encodedParams.clone();
        }
        else {
            this.encodedParams = null;
        }
    }
    
    public final String getAlgorithm() {
        return this.sealAlg;
    }
    
    public final Object getObject(final Key key) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, InvalidKeyException {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        try {
            return this.unseal(key, null);
        }
        catch (final NoSuchProviderException ex) {
            throw new NoSuchAlgorithmException("algorithm not found");
        }
        catch (final IllegalBlockSizeException ex2) {
            throw new InvalidKeyException(ex2.getMessage());
        }
        catch (final BadPaddingException ex3) {
            throw new InvalidKeyException(ex3.getMessage());
        }
    }
    
    public final Object getObject(final Cipher cipher) throws IOException, ClassNotFoundException, IllegalBlockSizeException, BadPaddingException {
        final ObjectInputStream extObjectInputStream = this.getExtObjectInputStream(cipher);
        try {
            return extObjectInputStream.readObject();
        }
        finally {
            extObjectInputStream.close();
        }
    }
    
    public final Object getObject(final Key key, final String s) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException {
        if (key == null) {
            throw new NullPointerException("key is null");
        }
        if (s == null || s.length() == 0) {
            throw new IllegalArgumentException("missing provider");
        }
        try {
            return this.unseal(key, s);
        }
        catch (final IllegalBlockSizeException | BadPaddingException ex) {
            throw new InvalidKeyException(((Throwable)ex).getMessage());
        }
    }
    
    private Object unseal(final Key key, final String s) throws IOException, ClassNotFoundException, NoSuchAlgorithmException, NoSuchProviderException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException {
        AlgorithmParameters algorithmParameters = null;
        if (this.encodedParams != null) {
            try {
                if (s != null) {
                    algorithmParameters = AlgorithmParameters.getInstance(this.paramsAlg, s);
                }
                else {
                    algorithmParameters = AlgorithmParameters.getInstance(this.paramsAlg);
                }
            }
            catch (final NoSuchProviderException ex) {
                if (s == null) {
                    throw new NoSuchAlgorithmException(this.paramsAlg + " not found");
                }
                throw new NoSuchProviderException(ex.getMessage());
            }
            algorithmParameters.init(this.encodedParams);
        }
        Cipher cipher;
        try {
            if (s != null) {
                cipher = Cipher.getInstance(this.sealAlg, s);
            }
            else {
                cipher = Cipher.getInstance(this.sealAlg);
            }
        }
        catch (final NoSuchPaddingException ex2) {
            throw new NoSuchAlgorithmException("Padding that was used in sealing operation not available");
        }
        catch (final NoSuchProviderException ex3) {
            if (s == null) {
                throw new NoSuchAlgorithmException(this.sealAlg + " not found");
            }
            throw new NoSuchProviderException(ex3.getMessage());
        }
        try {
            if (algorithmParameters != null) {
                cipher.init(2, key, algorithmParameters);
            }
            else {
                cipher.init(2, key);
            }
        }
        catch (final InvalidAlgorithmParameterException ex4) {
            throw new RuntimeException(ex4.getMessage());
        }
        final ObjectInputStream extObjectInputStream = this.getExtObjectInputStream(cipher);
        try {
            return extObjectInputStream.readObject();
        }
        finally {
            extObjectInputStream.close();
        }
    }
    
    private void readObject(final ObjectInputStream objectInputStream) throws IOException, ClassNotFoundException {
        objectInputStream.defaultReadObject();
        if (this.encryptedContent != null) {
            this.encryptedContent = this.encryptedContent.clone();
        }
        if (this.encodedParams != null) {
            this.encodedParams = this.encodedParams.clone();
        }
    }
    
    private ObjectInputStream getExtObjectInputStream(final Cipher cipher) throws BadPaddingException, IllegalBlockSizeException, IOException {
        return new extObjectInputStream(new ByteArrayInputStream(cipher.doFinal(this.encryptedContent)));
    }
    
    static {
        SharedSecrets.setJavaxCryptoSealedObjectAccess((sealedObject, cipher) -> sealedObject.getExtObjectInputStream(cipher));
    }
}
