package org.apache.xml.security.encryption;

import org.w3c.dom.NamedNodeMap;
import java.io.IOException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.Reader;
import org.xml.sax.InputSource;
import java.io.StringReader;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.OutputStream;
import java.util.HashMap;
import org.apache.xml.security.transforms.TransformationException;
import org.w3c.dom.Attr;
import org.apache.xml.security.utils.ElementProxy;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import org.apache.xml.security.utils.XMLUtils;
import org.apache.xml.security.transforms.InvalidTransformException;
import org.apache.xml.security.signature.XMLSignatureException;
import org.apache.xml.security.exceptions.XMLSecurityException;
import org.apache.commons.logging.LogFactory;
import java.security.InvalidAlgorithmParameterException;
import java.security.spec.AlgorithmParameterSpec;
import javax.crypto.spec.IvParameterSpec;
import org.apache.xml.security.keys.keyresolver.KeyResolverException;
import org.apache.xml.security.keys.keyresolver.KeyResolverSpi;
import org.apache.xml.security.keys.keyresolver.implementations.EncryptedKeyResolver;
import org.w3c.dom.DocumentFragment;
import org.apache.xml.security.keys.KeyInfo;
import org.w3c.dom.NodeList;
import org.apache.xml.utils.URI;
import org.apache.xml.security.utils.Base64;
import java.io.UnsupportedEncodingException;
import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import java.io.ByteArrayOutputStream;
import java.security.InvalidKeyException;
import java.io.InputStream;
import org.w3c.dom.Node;
import org.w3c.dom.Element;
import java.security.NoSuchProviderException;
import javax.crypto.NoSuchPaddingException;
import java.security.NoSuchAlgorithmException;
import org.apache.xml.security.algorithms.JCEMapper;
import org.apache.xml.security.c14n.InvalidCanonicalizerException;
import java.security.Key;
import org.w3c.dom.Document;
import org.apache.xml.security.c14n.Canonicalizer;
import javax.crypto.Cipher;
import org.apache.commons.logging.Log;

public class XMLCipher
{
    private static Log logger;
    public static final String TRIPLEDES = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc";
    public static final String AES_128 = "http://www.w3.org/2001/04/xmlenc#aes128-cbc";
    public static final String AES_256 = "http://www.w3.org/2001/04/xmlenc#aes256-cbc";
    public static final String AES_192 = "http://www.w3.org/2001/04/xmlenc#aes192-cbc";
    public static final String RSA_v1dot5 = "http://www.w3.org/2001/04/xmlenc#rsa-1_5";
    public static final String RSA_OAEP = "http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p";
    public static final String DIFFIE_HELLMAN = "http://www.w3.org/2001/04/xmlenc#dh";
    public static final String TRIPLEDES_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-tripledes";
    public static final String AES_128_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes128";
    public static final String AES_256_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes256";
    public static final String AES_192_KeyWrap = "http://www.w3.org/2001/04/xmlenc#kw-aes192";
    public static final String SHA1 = "http://www.w3.org/2000/09/xmldsig#sha1";
    public static final String SHA256 = "http://www.w3.org/2001/04/xmlenc#sha256";
    public static final String SHA512 = "http://www.w3.org/2001/04/xmlenc#sha512";
    public static final String RIPEMD_160 = "http://www.w3.org/2001/04/xmlenc#ripemd160";
    public static final String XML_DSIG = "http://www.w3.org/2000/09/xmldsig#";
    public static final String N14C_XML = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315";
    public static final String N14C_XML_WITH_COMMENTS = "http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments";
    public static final String EXCL_XML_N14C = "http://www.w3.org/2001/10/xml-exc-c14n#";
    public static final String EXCL_XML_N14C_WITH_COMMENTS = "http://www.w3.org/2001/10/xml-exc-c14n#WithComments";
    public static final String BASE64_ENCODING = "http://www.w3.org/2000/09/xmldsig#base64";
    public static final int ENCRYPT_MODE = 1;
    public static final int DECRYPT_MODE = 2;
    public static final int UNWRAP_MODE = 4;
    public static final int WRAP_MODE = 3;
    private static final String ENC_ALGORITHMS = "http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\n";
    private Cipher _contextCipher;
    private int _cipherMode;
    private String _algorithm;
    private String _requestedJCEProvider;
    private Canonicalizer _canon;
    private Document _contextDocument;
    private Factory _factory;
    private Serializer _serializer;
    private Key _key;
    private Key _kek;
    private EncryptedKey _ek;
    private EncryptedData _ed;
    
    private XMLCipher() {
        this._cipherMode = Integer.MIN_VALUE;
        this._algorithm = null;
        this._requestedJCEProvider = null;
        XMLCipher.logger.debug((Object)"Constructing XMLCipher...");
        this._factory = new Factory();
        this._serializer = new Serializer();
    }
    
    private static boolean isValidEncryptionAlgorithm(final String s) {
        return s.equals("http://www.w3.org/2001/04/xmlenc#tripledes-cbc") || s.equals("http://www.w3.org/2001/04/xmlenc#aes128-cbc") || s.equals("http://www.w3.org/2001/04/xmlenc#aes256-cbc") || s.equals("http://www.w3.org/2001/04/xmlenc#aes192-cbc") || s.equals("http://www.w3.org/2001/04/xmlenc#rsa-1_5") || s.equals("http://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p") || s.equals("http://www.w3.org/2001/04/xmlenc#kw-tripledes") || s.equals("http://www.w3.org/2001/04/xmlenc#kw-aes128") || s.equals("http://www.w3.org/2001/04/xmlenc#kw-aes256") || s.equals("http://www.w3.org/2001/04/xmlenc#kw-aes192");
    }
    
    public static XMLCipher getInstance(final String algorithm) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Getting XMLCipher...");
        if (null == algorithm) {
            XMLCipher.logger.error((Object)"Transformation unexpectedly null...");
        }
        if (!isValidEncryptionAlgorithm(algorithm)) {
            XMLCipher.logger.warn((Object)"Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\n");
        }
        final XMLCipher xmlCipher = new XMLCipher();
        xmlCipher._algorithm = algorithm;
        xmlCipher._key = null;
        xmlCipher._kek = null;
        try {
            xmlCipher._canon = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        }
        catch (final InvalidCanonicalizerException ex) {
            throw new XMLEncryptionException("empty", ex);
        }
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(algorithm);
        try {
            xmlCipher._contextCipher = Cipher.getInstance(translateURItoJCEID);
            XMLCipher.logger.debug((Object)("cihper.algoritm = " + xmlCipher._contextCipher.getAlgorithm()));
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new XMLEncryptionException("empty", ex2);
        }
        catch (final NoSuchPaddingException ex3) {
            throw new XMLEncryptionException("empty", ex3);
        }
        return xmlCipher;
    }
    
    public static XMLCipher getInstance(final String s, final String s2) throws XMLEncryptionException {
        final XMLCipher instance = getInstance(s);
        if (s2 != null) {
            try {
                instance._canon = Canonicalizer.getInstance(s2);
            }
            catch (final InvalidCanonicalizerException ex) {
                throw new XMLEncryptionException("empty", ex);
            }
        }
        return instance;
    }
    
    public static XMLCipher getProviderInstance(final String algorithm, final String requestedJCEProvider) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Getting XMLCipher...");
        if (null == algorithm) {
            XMLCipher.logger.error((Object)"Transformation unexpectedly null...");
        }
        if (null == requestedJCEProvider) {
            XMLCipher.logger.error((Object)"Provider unexpectedly null..");
        }
        if ("" == requestedJCEProvider) {
            XMLCipher.logger.error((Object)"Provider's value unexpectedly not specified...");
        }
        if (!isValidEncryptionAlgorithm(algorithm)) {
            XMLCipher.logger.warn((Object)"Algorithm non-standard, expected one of http://www.w3.org/2001/04/xmlenc#tripledes-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes128-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes256-cbc\nhttp://www.w3.org/2001/04/xmlenc#aes192-cbc\nhttp://www.w3.org/2001/04/xmlenc#rsa-1_5\nhttp://www.w3.org/2001/04/xmlenc#rsa-oaep-mgf1p\nhttp://www.w3.org/2001/04/xmlenc#kw-tripledes\nhttp://www.w3.org/2001/04/xmlenc#kw-aes128\nhttp://www.w3.org/2001/04/xmlenc#kw-aes256\nhttp://www.w3.org/2001/04/xmlenc#kw-aes192\n");
        }
        final XMLCipher xmlCipher = new XMLCipher();
        xmlCipher._algorithm = algorithm;
        xmlCipher._requestedJCEProvider = requestedJCEProvider;
        xmlCipher._key = null;
        xmlCipher._kek = null;
        try {
            xmlCipher._canon = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        }
        catch (final InvalidCanonicalizerException ex) {
            throw new XMLEncryptionException("empty", ex);
        }
        try {
            xmlCipher._contextCipher = Cipher.getInstance(JCEMapper.translateURItoJCEID(algorithm), requestedJCEProvider);
            XMLCipher.logger.debug((Object)("cipher._algorithm = " + xmlCipher._contextCipher.getAlgorithm()));
            XMLCipher.logger.debug((Object)("provider.name = " + requestedJCEProvider));
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new XMLEncryptionException("empty", ex2);
        }
        catch (final NoSuchProviderException ex3) {
            throw new XMLEncryptionException("empty", ex3);
        }
        catch (final NoSuchPaddingException ex4) {
            throw new XMLEncryptionException("empty", ex4);
        }
        return xmlCipher;
    }
    
    public static XMLCipher getProviderInstance(final String s, final String s2, final String s3) throws XMLEncryptionException {
        final XMLCipher providerInstance = getProviderInstance(s, s2);
        if (s3 != null) {
            try {
                providerInstance._canon = Canonicalizer.getInstance(s3);
            }
            catch (final InvalidCanonicalizerException ex) {
                throw new XMLEncryptionException("empty", ex);
            }
        }
        return providerInstance;
    }
    
    public static XMLCipher getInstance() throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Getting XMLCipher for no transformation...");
        final XMLCipher xmlCipher = new XMLCipher();
        xmlCipher._algorithm = null;
        xmlCipher._requestedJCEProvider = null;
        xmlCipher._key = null;
        xmlCipher._kek = null;
        xmlCipher._contextCipher = null;
        try {
            xmlCipher._canon = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        }
        catch (final InvalidCanonicalizerException ex) {
            throw new XMLEncryptionException("empty", ex);
        }
        return xmlCipher;
    }
    
    public static XMLCipher getProviderInstance(final String requestedJCEProvider) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Getting XMLCipher, provider but no transformation");
        if (null == requestedJCEProvider) {
            XMLCipher.logger.error((Object)"Provider unexpectedly null..");
        }
        if ("" == requestedJCEProvider) {
            XMLCipher.logger.error((Object)"Provider's value unexpectedly not specified...");
        }
        final XMLCipher xmlCipher = new XMLCipher();
        xmlCipher._algorithm = null;
        xmlCipher._requestedJCEProvider = requestedJCEProvider;
        xmlCipher._key = null;
        xmlCipher._kek = null;
        xmlCipher._contextCipher = null;
        try {
            xmlCipher._canon = Canonicalizer.getInstance("http://www.w3.org/TR/2001/REC-xml-c14n-20010315#WithComments");
        }
        catch (final InvalidCanonicalizerException ex) {
            throw new XMLEncryptionException("empty", ex);
        }
        return xmlCipher;
    }
    
    public void init(final int cipherMode, final Key key) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Initializing XMLCipher...");
        this._ek = null;
        this._ed = null;
        switch (cipherMode) {
            case 1: {
                XMLCipher.logger.debug((Object)"opmode = ENCRYPT_MODE");
                this._ed = this.createEncryptedData(1, "NO VALUE YET");
                break;
            }
            case 2: {
                XMLCipher.logger.debug((Object)"opmode = DECRYPT_MODE");
                break;
            }
            case 3: {
                XMLCipher.logger.debug((Object)"opmode = WRAP_MODE");
                this._ek = this.createEncryptedKey(1, "NO VALUE YET");
                break;
            }
            case 4: {
                XMLCipher.logger.debug((Object)"opmode = UNWRAP_MODE");
                break;
            }
            default: {
                XMLCipher.logger.error((Object)"Mode unexpectedly invalid");
                throw new XMLEncryptionException("Invalid mode in init");
            }
        }
        this._cipherMode = cipherMode;
        this._key = key;
    }
    
    public EncryptedData getEncryptedData() {
        XMLCipher.logger.debug((Object)"Returning EncryptedData");
        return this._ed;
    }
    
    public EncryptedKey getEncryptedKey() {
        XMLCipher.logger.debug((Object)"Returning EncryptedKey");
        return this._ek;
    }
    
    public void setKEK(final Key kek) {
        this._kek = kek;
    }
    
    public Element martial(final EncryptedData encryptedData) {
        return this._factory.toElement(encryptedData);
    }
    
    public Element martial(final EncryptedKey encryptedKey) {
        return this._factory.toElement(encryptedKey);
    }
    
    public Element martial(final Document contextDocument, final EncryptedData encryptedData) {
        this._contextDocument = contextDocument;
        return this._factory.toElement(encryptedData);
    }
    
    public Element martial(final Document contextDocument, final EncryptedKey encryptedKey) {
        this._contextDocument = contextDocument;
        return this._factory.toElement(encryptedKey);
    }
    
    private Document encryptElement(final Element element) throws Exception {
        XMLCipher.logger.debug((Object)"Encrypting element...");
        if (null == element) {
            XMLCipher.logger.error((Object)"Element unexpectedly null...");
        }
        if (this._cipherMode != 1) {
            XMLCipher.logger.debug((Object)"XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        if (this._algorithm == null) {
            throw new XMLEncryptionException("XMLCipher instance without transformation specified");
        }
        this.encryptData(this._contextDocument, element, false);
        element.getParentNode().replaceChild(this._factory.toElement(this._ed), element);
        return this._contextDocument;
    }
    
    private Document encryptElementContent(final Element element) throws Exception {
        XMLCipher.logger.debug((Object)"Encrypting element content...");
        if (null == element) {
            XMLCipher.logger.error((Object)"Element unexpectedly null...");
        }
        if (this._cipherMode != 1) {
            XMLCipher.logger.debug((Object)"XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        if (this._algorithm == null) {
            throw new XMLEncryptionException("XMLCipher instance without transformation specified");
        }
        this.encryptData(this._contextDocument, element, true);
        final Element element2 = this._factory.toElement(this._ed);
        this.removeContent(element);
        element.appendChild(element2);
        return this._contextDocument;
    }
    
    public Document doFinal(final Document contextDocument, final Document document) throws Exception {
        XMLCipher.logger.debug((Object)"Processing source document...");
        if (null == contextDocument) {
            XMLCipher.logger.error((Object)"Context document unexpectedly null...");
        }
        if (null == document) {
            XMLCipher.logger.error((Object)"Source document unexpectedly null...");
        }
        this._contextDocument = contextDocument;
        Document document2 = null;
        switch (this._cipherMode) {
            case 2: {
                document2 = this.decryptElement(document.getDocumentElement());
                break;
            }
            case 1: {
                document2 = this.encryptElement(document.getDocumentElement());
                break;
            }
            case 4: {
                break;
            }
            case 3: {
                break;
            }
            default: {
                throw new XMLEncryptionException("empty", new IllegalStateException());
            }
        }
        return document2;
    }
    
    public Document doFinal(final Document contextDocument, final Element element) throws Exception {
        XMLCipher.logger.debug((Object)"Processing source element...");
        if (null == contextDocument) {
            XMLCipher.logger.error((Object)"Context document unexpectedly null...");
        }
        if (null == element) {
            XMLCipher.logger.error((Object)"Source element unexpectedly null...");
        }
        this._contextDocument = contextDocument;
        Document document = null;
        switch (this._cipherMode) {
            case 2: {
                document = this.decryptElement(element);
                break;
            }
            case 1: {
                document = this.encryptElement(element);
                break;
            }
            case 4: {
                break;
            }
            case 3: {
                break;
            }
            default: {
                throw new XMLEncryptionException("empty", new IllegalStateException());
            }
        }
        return document;
    }
    
    public Document doFinal(final Document contextDocument, final Element element, final boolean b) throws Exception {
        XMLCipher.logger.debug((Object)"Processing source element...");
        if (null == contextDocument) {
            XMLCipher.logger.error((Object)"Context document unexpectedly null...");
        }
        if (null == element) {
            XMLCipher.logger.error((Object)"Source element unexpectedly null...");
        }
        this._contextDocument = contextDocument;
        Document document = null;
        switch (this._cipherMode) {
            case 2: {
                if (b) {
                    document = this.decryptElementContent(element);
                    break;
                }
                document = this.decryptElement(element);
                break;
            }
            case 1: {
                if (b) {
                    document = this.encryptElementContent(element);
                    break;
                }
                document = this.encryptElement(element);
                break;
            }
            case 4: {
                break;
            }
            case 3: {
                break;
            }
            default: {
                throw new XMLEncryptionException("empty", new IllegalStateException());
            }
        }
        return document;
    }
    
    public EncryptedData encryptData(final Document document, final Element element) throws Exception {
        return this.encryptData(document, element, false);
    }
    
    public EncryptedData encryptData(final Document document, final String s, final InputStream inputStream) throws Exception {
        XMLCipher.logger.debug((Object)"Encrypting element...");
        if (null == document) {
            XMLCipher.logger.error((Object)"Context document unexpectedly null...");
        }
        if (null == inputStream) {
            XMLCipher.logger.error((Object)"Serialized data unexpectedly null...");
        }
        if (this._cipherMode != 1) {
            XMLCipher.logger.debug((Object)"XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        return this.encryptData(document, null, s, inputStream);
    }
    
    public EncryptedData encryptData(final Document document, final Element element, final boolean b) throws Exception {
        XMLCipher.logger.debug((Object)"Encrypting element...");
        if (null == document) {
            XMLCipher.logger.error((Object)"Context document unexpectedly null...");
        }
        if (null == element) {
            XMLCipher.logger.error((Object)"Element unexpectedly null...");
        }
        if (this._cipherMode != 1) {
            XMLCipher.logger.debug((Object)"XMLCipher unexpectedly not in ENCRYPT_MODE...");
        }
        if (b) {
            return this.encryptData(document, element, "http://www.w3.org/2001/04/xmlenc#Content", null);
        }
        return this.encryptData(document, element, "http://www.w3.org/2001/04/xmlenc#Element", null);
    }
    
    private EncryptedData encryptData(final Document contextDocument, final Element element, final String s, final InputStream inputStream) throws Exception {
        this._contextDocument = contextDocument;
        if (this._algorithm == null) {
            throw new XMLEncryptionException("XMLCipher instance without transformation specified");
        }
        String s2 = null;
        if (inputStream == null) {
            if (s == "http://www.w3.org/2001/04/xmlenc#Content") {
                final NodeList childNodes = element.getChildNodes();
                if (null == childNodes) {
                    throw new XMLEncryptionException("empty", new Object[] { "Element has no content." });
                }
                s2 = this._serializer.serialize(childNodes);
            }
            else {
                s2 = this._serializer.serialize(element);
            }
            XMLCipher.logger.debug((Object)("Serialized octets:\n" + s2));
        }
        Cipher cipher;
        if (this._contextCipher == null) {
            final String translateURItoJCEID = JCEMapper.translateURItoJCEID(this._algorithm);
            XMLCipher.logger.debug((Object)("alg = " + translateURItoJCEID));
            try {
                if (this._requestedJCEProvider == null) {
                    cipher = Cipher.getInstance(translateURItoJCEID);
                }
                else {
                    cipher = Cipher.getInstance(translateURItoJCEID, this._requestedJCEProvider);
                }
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new XMLEncryptionException("empty", ex);
            }
            catch (final NoSuchProviderException ex2) {
                throw new XMLEncryptionException("empty", ex2);
            }
            catch (final NoSuchPaddingException ex3) {
                throw new XMLEncryptionException("empty", ex3);
            }
        }
        else {
            cipher = this._contextCipher;
        }
        try {
            cipher.init(this._cipherMode, this._key);
        }
        catch (final InvalidKeyException ex4) {
            throw new XMLEncryptionException("empty", ex4);
        }
        byte[] array2;
        try {
            if (inputStream != null) {
                final byte[] array = new byte[8192];
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                int read;
                while ((read = inputStream.read(array)) != -1) {
                    byteArrayOutputStream.write(cipher.update(array, 0, read));
                }
                byteArrayOutputStream.write(cipher.doFinal());
                array2 = byteArrayOutputStream.toByteArray();
            }
            else {
                array2 = cipher.doFinal(s2.getBytes("UTF-8"));
                XMLCipher.logger.debug((Object)("Expected cipher.outputSize = " + Integer.toString(cipher.getOutputSize(s2.getBytes().length))));
            }
            XMLCipher.logger.debug((Object)("Actual cipher.outputSize = " + Integer.toString(array2.length)));
        }
        catch (final IllegalStateException ex5) {
            throw new XMLEncryptionException("empty", ex5);
        }
        catch (final IllegalBlockSizeException ex6) {
            throw new XMLEncryptionException("empty", ex6);
        }
        catch (final BadPaddingException ex7) {
            throw new XMLEncryptionException("empty", ex7);
        }
        catch (final UnsupportedEncodingException ex8) {
            throw new XMLEncryptionException("empty", ex8);
        }
        final byte[] iv = cipher.getIV();
        final byte[] array3 = new byte[iv.length + array2.length];
        System.arraycopy(iv, 0, array3, 0, iv.length);
        System.arraycopy(array2, 0, array3, iv.length, array2.length);
        final String encode = Base64.encode(array3);
        XMLCipher.logger.debug((Object)("Encrypted octets:\n" + encode));
        XMLCipher.logger.debug((Object)("Encrypted octets length = " + encode.length()));
        try {
            this._ed.getCipherData().getCipherValue().setValue(encode);
            this._ed.setType(new URI(s).toString());
            this._ed.setEncryptionMethod(this._factory.newEncryptionMethod(new URI(this._algorithm).toString()));
        }
        catch (final URI.MalformedURIException ex9) {
            throw new XMLEncryptionException("empty", (Exception)ex9);
        }
        return this._ed;
    }
    
    public EncryptedData loadEncryptedData(final Document contextDocument, final Element element) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Loading encrypted element...");
        if (null == contextDocument) {
            XMLCipher.logger.error((Object)"Context document unexpectedly null...");
        }
        if (null == element) {
            XMLCipher.logger.error((Object)"Element unexpectedly null...");
        }
        if (this._cipherMode != 2) {
            XMLCipher.logger.error((Object)"XMLCipher unexpectedly not in DECRYPT_MODE...");
        }
        this._contextDocument = contextDocument;
        return this._ed = this._factory.newEncryptedData(element);
    }
    
    public EncryptedKey loadEncryptedKey(final Document contextDocument, final Element element) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Loading encrypted key...");
        if (null == contextDocument) {
            XMLCipher.logger.error((Object)"Context document unexpectedly null...");
        }
        if (null == element) {
            XMLCipher.logger.error((Object)"Element unexpectedly null...");
        }
        if (this._cipherMode != 4 && this._cipherMode != 2) {
            XMLCipher.logger.debug((Object)"XMLCipher unexpectedly not in UNWRAP_MODE or DECRYPT_MODE...");
        }
        this._contextDocument = contextDocument;
        return this._ek = this._factory.newEncryptedKey(element);
    }
    
    public EncryptedKey loadEncryptedKey(final Element element) throws XMLEncryptionException {
        return this.loadEncryptedKey(element.getOwnerDocument(), element);
    }
    
    public EncryptedKey encryptKey(final Document contextDocument, final Key key) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Encrypting key ...");
        if (null == key) {
            XMLCipher.logger.error((Object)"Key unexpectedly null...");
        }
        if (this._cipherMode != 3) {
            XMLCipher.logger.debug((Object)"XMLCipher unexpectedly not in WRAP_MODE...");
        }
        if (this._algorithm == null) {
            throw new XMLEncryptionException("XMLCipher instance without transformation specified");
        }
        this._contextDocument = contextDocument;
        Cipher cipher;
        if (this._contextCipher == null) {
            final String translateURItoJCEID = JCEMapper.translateURItoJCEID(this._algorithm);
            XMLCipher.logger.debug((Object)("alg = " + translateURItoJCEID));
            try {
                if (this._requestedJCEProvider == null) {
                    cipher = Cipher.getInstance(translateURItoJCEID);
                }
                else {
                    cipher = Cipher.getInstance(translateURItoJCEID, this._requestedJCEProvider);
                }
            }
            catch (final NoSuchAlgorithmException ex) {
                throw new XMLEncryptionException("empty", ex);
            }
            catch (final NoSuchProviderException ex2) {
                throw new XMLEncryptionException("empty", ex2);
            }
            catch (final NoSuchPaddingException ex3) {
                throw new XMLEncryptionException("empty", ex3);
            }
        }
        else {
            cipher = this._contextCipher;
        }
        byte[] wrap;
        try {
            cipher.init(3, this._key);
            wrap = cipher.wrap(key);
        }
        catch (final InvalidKeyException ex4) {
            throw new XMLEncryptionException("empty", ex4);
        }
        catch (final IllegalBlockSizeException ex5) {
            throw new XMLEncryptionException("empty", ex5);
        }
        final String encode = Base64.encode(wrap);
        XMLCipher.logger.debug((Object)("Encrypted key octets:\n" + encode));
        XMLCipher.logger.debug((Object)("Encrypted key octets length = " + encode.length()));
        this._ek.getCipherData().getCipherValue().setValue(encode);
        try {
            this._ek.setEncryptionMethod(this._factory.newEncryptionMethod(new URI(this._algorithm).toString()));
        }
        catch (final URI.MalformedURIException ex6) {
            throw new XMLEncryptionException("empty", (Exception)ex6);
        }
        return this._ek;
    }
    
    public Key decryptKey(final EncryptedKey encryptedKey, final String s) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Decrypting key from previously loaded EncryptedKey...");
        if (this._cipherMode != 4) {
            XMLCipher.logger.debug((Object)"XMLCipher unexpectedly not in UNWRAP_MODE...");
        }
        if (s == null) {
            throw new XMLEncryptionException("Cannot decrypt a key without knowing the algorithm");
        }
        if (this._key == null) {
            XMLCipher.logger.debug((Object)"Trying to find a KEK via key resolvers");
            final KeyInfo keyInfo = encryptedKey.getKeyInfo();
            if (keyInfo != null) {
                try {
                    this._key = keyInfo.getSecretKey();
                }
                catch (final Exception ex) {}
            }
            if (this._key == null) {
                XMLCipher.logger.error((Object)"XMLCipher::decryptKey called without a KEK and cannot resolve");
                throw new XMLEncryptionException("Unable to decrypt without a KEK");
            }
        }
        final byte[] bytes = new XMLCipherInput(encryptedKey).getBytes();
        final String jceKeyAlgorithmFromURI = JCEMapper.getJCEKeyAlgorithmFromURI(s);
        Cipher cipher;
        if (this._contextCipher == null) {
            final String translateURItoJCEID = JCEMapper.translateURItoJCEID(encryptedKey.getEncryptionMethod().getAlgorithm());
            XMLCipher.logger.debug((Object)("JCE Algorithm = " + translateURItoJCEID));
            try {
                if (this._requestedJCEProvider == null) {
                    cipher = Cipher.getInstance(translateURItoJCEID);
                }
                else {
                    cipher = Cipher.getInstance(translateURItoJCEID, this._requestedJCEProvider);
                }
            }
            catch (final NoSuchAlgorithmException ex2) {
                throw new XMLEncryptionException("empty", ex2);
            }
            catch (final NoSuchProviderException ex3) {
                throw new XMLEncryptionException("empty", ex3);
            }
            catch (final NoSuchPaddingException ex4) {
                throw new XMLEncryptionException("empty", ex4);
            }
        }
        else {
            cipher = this._contextCipher;
        }
        Key unwrap;
        try {
            cipher.init(4, this._key);
            unwrap = cipher.unwrap(bytes, jceKeyAlgorithmFromURI, 3);
        }
        catch (final InvalidKeyException ex5) {
            throw new XMLEncryptionException("empty", ex5);
        }
        catch (final NoSuchAlgorithmException ex6) {
            throw new XMLEncryptionException("empty", ex6);
        }
        XMLCipher.logger.debug((Object)("Decryption of key type " + s + " OK"));
        return unwrap;
    }
    
    public Key decryptKey(final EncryptedKey encryptedKey) throws XMLEncryptionException {
        return this.decryptKey(encryptedKey, this._ed.getEncryptionMethod().getAlgorithm());
    }
    
    private void removeContent(final Node node) {
        final NodeList childNodes = node.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); ++i) {
            final Node item = childNodes.item(i);
            if (item != null) {
                item.getParentNode().removeChild(item);
            }
        }
    }
    
    private Document decryptElement(final Element element) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Decrypting element...");
        if (this._cipherMode != 2) {
            XMLCipher.logger.error((Object)"XMLCipher unexpectedly not in DECRYPT_MODE...");
        }
        String s;
        try {
            s = new String(this.decryptToByteArray(element), "UTF-8");
        }
        catch (final UnsupportedEncodingException ex) {
            throw new XMLEncryptionException("empty", ex);
        }
        XMLCipher.logger.debug((Object)("Decrypted octets:\n" + s));
        final Node parentNode = element.getParentNode();
        final DocumentFragment deserialize = this._serializer.deserialize(s, parentNode);
        if (parentNode instanceof Document) {
            this._contextDocument.removeChild(this._contextDocument.getDocumentElement());
            this._contextDocument.appendChild(deserialize);
        }
        else {
            parentNode.replaceChild(deserialize, element);
        }
        return this._contextDocument;
    }
    
    private Document decryptElementContent(final Element element) throws XMLEncryptionException {
        final Element element2 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptedData").item(0);
        if (null == element2) {
            throw new XMLEncryptionException("No EncryptedData child element.");
        }
        return this.decryptElement(element2);
    }
    
    public byte[] decryptToByteArray(final Element element) throws XMLEncryptionException {
        XMLCipher.logger.debug((Object)"Decrypting to ByteArray...");
        if (this._cipherMode != 2) {
            XMLCipher.logger.error((Object)"XMLCipher unexpectedly not in DECRYPT_MODE...");
        }
        final EncryptedData encryptedData = this._factory.newEncryptedData(element);
        if (this._key == null) {
            final KeyInfo keyInfo = encryptedData.getKeyInfo();
            if (keyInfo != null) {
                try {
                    keyInfo.registerInternalKeyResolver(new EncryptedKeyResolver(encryptedData.getEncryptionMethod().getAlgorithm(), this._kek));
                    this._key = keyInfo.getSecretKey();
                }
                catch (final KeyResolverException ex) {}
            }
            if (this._key == null) {
                XMLCipher.logger.error((Object)"XMLCipher::decryptElement called without a key and unable to resolve");
                throw new XMLEncryptionException("encryption.nokey");
            }
        }
        final byte[] bytes = new XMLCipherInput(encryptedData).getBytes();
        final String translateURItoJCEID = JCEMapper.translateURItoJCEID(encryptedData.getEncryptionMethod().getAlgorithm());
        Cipher cipher;
        try {
            if (this._requestedJCEProvider == null) {
                cipher = Cipher.getInstance(translateURItoJCEID);
            }
            else {
                cipher = Cipher.getInstance(translateURItoJCEID, this._requestedJCEProvider);
            }
        }
        catch (final NoSuchAlgorithmException ex2) {
            throw new XMLEncryptionException("empty", ex2);
        }
        catch (final NoSuchProviderException ex3) {
            throw new XMLEncryptionException("empty", ex3);
        }
        catch (final NoSuchPaddingException ex4) {
            throw new XMLEncryptionException("empty", ex4);
        }
        final int blockSize = cipher.getBlockSize();
        final byte[] array = new byte[blockSize];
        System.arraycopy(bytes, 0, array, 0, blockSize);
        final IvParameterSpec ivParameterSpec = new IvParameterSpec(array);
        try {
            cipher.init(this._cipherMode, this._key, ivParameterSpec);
        }
        catch (final InvalidKeyException ex5) {
            throw new XMLEncryptionException("empty", ex5);
        }
        catch (final InvalidAlgorithmParameterException ex6) {
            throw new XMLEncryptionException("empty", ex6);
        }
        byte[] doFinal;
        try {
            doFinal = cipher.doFinal(bytes, blockSize, bytes.length - blockSize);
        }
        catch (final IllegalBlockSizeException ex7) {
            throw new XMLEncryptionException("empty", ex7);
        }
        catch (final BadPaddingException ex8) {
            throw new XMLEncryptionException("empty", ex8);
        }
        return doFinal;
    }
    
    public EncryptedData createEncryptedData(final int n, final String s) throws XMLEncryptionException {
        EncryptedData encryptedData = null;
        switch (n) {
            case 2: {
                final CipherReference cipherReference = this._factory.newCipherReference(s);
                final CipherData cipherData = this._factory.newCipherData(n);
                cipherData.setCipherReference(cipherReference);
                encryptedData = this._factory.newEncryptedData(cipherData);
                break;
            }
            case 1: {
                final CipherValue cipherValue = this._factory.newCipherValue(s);
                final CipherData cipherData2 = this._factory.newCipherData(n);
                cipherData2.setCipherValue(cipherValue);
                encryptedData = this._factory.newEncryptedData(cipherData2);
                break;
            }
        }
        return encryptedData;
    }
    
    public EncryptedKey createEncryptedKey(final int n, final String s) throws XMLEncryptionException {
        EncryptedKey encryptedKey = null;
        switch (n) {
            case 2: {
                final CipherReference cipherReference = this._factory.newCipherReference(s);
                final CipherData cipherData = this._factory.newCipherData(n);
                cipherData.setCipherReference(cipherReference);
                encryptedKey = this._factory.newEncryptedKey(cipherData);
                break;
            }
            case 1: {
                final CipherValue cipherValue = this._factory.newCipherValue(s);
                final CipherData cipherData2 = this._factory.newCipherData(n);
                cipherData2.setCipherValue(cipherValue);
                encryptedKey = this._factory.newEncryptedKey(cipherData2);
                break;
            }
        }
        return encryptedKey;
    }
    
    public AgreementMethod createAgreementMethod(final String s) {
        return this._factory.newAgreementMethod(s);
    }
    
    public CipherData createCipherData(final int n) {
        return this._factory.newCipherData(n);
    }
    
    public CipherReference createCipherReference(final String s) {
        return this._factory.newCipherReference(s);
    }
    
    public CipherValue createCipherValue(final String s) {
        return this._factory.newCipherValue(s);
    }
    
    public EncryptionMethod createEncryptionMethod(final String s) {
        return this._factory.newEncryptionMethod(s);
    }
    
    public EncryptionProperties createEncryptionProperties() {
        return this._factory.newEncryptionProperties();
    }
    
    public EncryptionProperty createEncryptionProperty() {
        return this._factory.newEncryptionProperty();
    }
    
    public ReferenceList createReferenceList(final int n) {
        return this._factory.newReferenceList(n);
    }
    
    public Transforms createTransforms() {
        return this._factory.newTransforms();
    }
    
    public Transforms createTransforms(final Document document) {
        return this._factory.newTransforms(document);
    }
    
    static {
        XMLCipher.logger = LogFactory.getLog(XMLCipher.class.getName());
    }
    
    private class Factory
    {
        private final /* synthetic */ XMLCipher this$0;
        
        AgreementMethod newAgreementMethod(final String s) {
            return new AgreementMethodImpl(s);
        }
        
        CipherData newCipherData(final int n) {
            return new CipherDataImpl(n);
        }
        
        CipherReference newCipherReference(final String s) {
            return new CipherReferenceImpl(s);
        }
        
        CipherValue newCipherValue(final String s) {
            return new CipherValueImpl(s);
        }
        
        EncryptedData newEncryptedData(final CipherData cipherData) {
            return new EncryptedDataImpl(cipherData);
        }
        
        EncryptedKey newEncryptedKey(final CipherData cipherData) {
            return new EncryptedKeyImpl(cipherData);
        }
        
        EncryptionMethod newEncryptionMethod(final String s) {
            return new EncryptionMethodImpl(s);
        }
        
        EncryptionProperties newEncryptionProperties() {
            return new EncryptionPropertiesImpl();
        }
        
        EncryptionProperty newEncryptionProperty() {
            return new EncryptionPropertyImpl();
        }
        
        ReferenceList newReferenceList(final int n) {
            return new ReferenceListImpl(n);
        }
        
        Transforms newTransforms() {
            return new TransformsImpl();
        }
        
        Transforms newTransforms(final Document document) {
            return new TransformsImpl(document);
        }
        
        AgreementMethod newAgreementMethod(final Element element) throws XMLEncryptionException {
            if (null == element) {
                throw new NullPointerException("element is null");
            }
            final AgreementMethod agreementMethod = this.newAgreementMethod(element.getAttributeNS(null, "Algorithm"));
            final Element element2 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KA-Nonce").item(0);
            if (null != element2) {
                agreementMethod.setKANonce(element2.getNodeValue().getBytes());
            }
            final Element element3 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "OriginatorKeyInfo").item(0);
            if (null != element3) {
                try {
                    agreementMethod.setOriginatorKeyInfo(new KeyInfo(element3, null));
                }
                catch (final XMLSecurityException ex) {
                    throw new XMLEncryptionException("empty", ex);
                }
            }
            final Element element4 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "RecipientKeyInfo").item(0);
            if (null != element4) {
                try {
                    agreementMethod.setRecipientKeyInfo(new KeyInfo(element4, null));
                }
                catch (final XMLSecurityException ex2) {
                    throw new XMLEncryptionException("empty", ex2);
                }
            }
            return agreementMethod;
        }
        
        CipherData newCipherData(final Element element) throws XMLEncryptionException {
            if (null == element) {
                throw new NullPointerException("element is null");
            }
            int n = 0;
            Element element2 = null;
            if (element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").getLength() > 0) {
                n = 1;
                element2 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherValue").item(0);
            }
            else if (element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").getLength() > 0) {
                n = 2;
                element2 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherReference").item(0);
            }
            final CipherData cipherData = this.newCipherData(n);
            if (n == 1) {
                cipherData.setCipherValue(this.newCipherValue(element2));
            }
            else if (n == 2) {
                cipherData.setCipherReference(this.newCipherReference(element2));
            }
            return cipherData;
        }
        
        CipherReference newCipherReference(final Element element) throws XMLEncryptionException {
            final CipherReferenceImpl cipherReferenceImpl = new CipherReferenceImpl(element.getAttributeNodeNS(null, "URI"));
            final Element element2 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "Transforms").item(0);
            if (element2 != null) {
                XMLCipher.logger.debug((Object)"Creating a DSIG based Transforms element");
                try {
                    cipherReferenceImpl.setTransforms(new TransformsImpl(element2));
                }
                catch (final XMLSignatureException ex) {
                    throw new XMLEncryptionException("empty", ex);
                }
                catch (final InvalidTransformException ex2) {
                    throw new XMLEncryptionException("empty", ex2);
                }
                catch (final XMLSecurityException ex3) {
                    throw new XMLEncryptionException("empty", ex3);
                }
            }
            return cipherReferenceImpl;
        }
        
        CipherValue newCipherValue(final Element element) {
            return this.newCipherValue(XMLUtils.getFullTextChildrenFromElement(element));
        }
        
        EncryptedData newEncryptedData(final Element element) throws XMLEncryptionException {
            final NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
            final EncryptedData encryptedData = this.newEncryptedData(this.newCipherData((Element)elementsByTagNameNS.item(elementsByTagNameNS.getLength() - 1)));
            try {
                encryptedData.setId(element.getAttributeNS(null, "Id"));
                encryptedData.setType(new URI(element.getAttributeNS(null, "Type")).toString());
                encryptedData.setMimeType(element.getAttributeNS(null, "MimeType"));
                encryptedData.setEncoding(new URI(element.getAttributeNS(null, "Encoding")).toString());
            }
            catch (final URI.MalformedURIException ex) {}
            final Element element2 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
            if (null != element2) {
                encryptedData.setEncryptionMethod(this.newEncryptionMethod(element2));
            }
            final Element element3 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
            if (null != element3) {
                try {
                    encryptedData.setKeyInfo(new KeyInfo(element3, null));
                }
                catch (final XMLSecurityException ex2) {
                    throw new XMLEncryptionException("Error loading Key Info", ex2);
                }
            }
            final Element element4 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
            if (null != element4) {
                encryptedData.setEncryptionProperties(this.newEncryptionProperties(element4));
            }
            return encryptedData;
        }
        
        EncryptedKey newEncryptedKey(final Element element) throws XMLEncryptionException {
            final NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CipherData");
            final EncryptedKey encryptedKey = this.newEncryptedKey(this.newCipherData((Element)elementsByTagNameNS.item(elementsByTagNameNS.getLength() - 1)));
            try {
                encryptedKey.setId(element.getAttributeNS(null, "Id"));
                encryptedKey.setType(new URI(element.getAttributeNS(null, "Type")).toString());
                encryptedKey.setMimeType(element.getAttributeNS(null, "MimeType"));
                encryptedKey.setEncoding(new URI(element.getAttributeNS(null, "Encoding")).toString());
                encryptedKey.setRecipient(element.getAttributeNS(null, "Recipient"));
            }
            catch (final URI.MalformedURIException ex) {}
            final Element element2 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod").item(0);
            if (null != element2) {
                encryptedKey.setEncryptionMethod(this.newEncryptionMethod(element2));
            }
            final Element element3 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2000/09/xmldsig#", "KeyInfo").item(0);
            if (null != element3) {
                try {
                    encryptedKey.setKeyInfo(new KeyInfo(element3, null));
                }
                catch (final XMLSecurityException ex2) {
                    throw new XMLEncryptionException("Error loading Key Info", ex2);
                }
            }
            final Element element4 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties").item(0);
            if (null != element4) {
                encryptedKey.setEncryptionProperties(this.newEncryptionProperties(element4));
            }
            final Element element5 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "ReferenceList").item(0);
            if (null != element5) {
                encryptedKey.setReferenceList(this.newReferenceList(element5));
            }
            final Element element6 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName").item(0);
            if (null != element6) {
                encryptedKey.setCarriedName(element6.getFirstChild().getNodeValue());
            }
            return encryptedKey;
        }
        
        EncryptionMethod newEncryptionMethod(final Element element) {
            final EncryptionMethod encryptionMethod = this.newEncryptionMethod(element.getAttributeNS(null, "Algorithm"));
            final Element element2 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeySize").item(0);
            if (null != element2) {
                encryptionMethod.setKeySize(Integer.valueOf(element2.getFirstChild().getNodeValue()));
            }
            final Element element3 = (Element)element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "OAEPparams").item(0);
            if (null != element3) {
                encryptionMethod.setOAEPparams(element3.getNodeValue().getBytes());
            }
            return encryptionMethod;
        }
        
        EncryptionProperties newEncryptionProperties(final Element element) {
            final EncryptionProperties encryptionProperties = this.newEncryptionProperties();
            encryptionProperties.setId(element.getAttributeNS(null, "Id"));
            final NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "EncryptionProperty");
            for (int i = 0; i < elementsByTagNameNS.getLength(); ++i) {
                final Node item = elementsByTagNameNS.item(i);
                if (null != item) {
                    encryptionProperties.addEncryptionProperty(this.newEncryptionProperty((Element)item));
                }
            }
            return encryptionProperties;
        }
        
        EncryptionProperty newEncryptionProperty(final Element element) {
            final EncryptionProperty encryptionProperty = this.newEncryptionProperty();
            try {
                encryptionProperty.setTarget(new URI(element.getAttributeNS(null, "Target")).toString());
            }
            catch (final URI.MalformedURIException ex) {}
            encryptionProperty.setId(element.getAttributeNS(null, "Id"));
            return encryptionProperty;
        }
        
        ReferenceList newReferenceList(final Element element) {
            int n = 0;
            if (null != element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference").item(0)) {
                n = 1;
            }
            else if (null != element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference").item(0)) {
                n = 2;
            }
            final ReferenceListImpl referenceListImpl = new ReferenceListImpl(n);
            switch (n) {
                case 1: {
                    final NodeList elementsByTagNameNS = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "DataReference");
                    for (int i = 0; i < elementsByTagNameNS.getLength(); ++i) {
                        referenceListImpl.add(referenceListImpl.newDataReference(((Element)elementsByTagNameNS.item(i)).getAttribute("URI")));
                    }
                    break;
                }
                case 2: {
                    final NodeList elementsByTagNameNS2 = element.getElementsByTagNameNS("http://www.w3.org/2001/04/xmlenc#", "KeyReference");
                    for (int j = 0; j < elementsByTagNameNS2.getLength(); ++j) {
                        referenceListImpl.add(referenceListImpl.newKeyReference(((Element)elementsByTagNameNS2.item(j)).getAttribute("URI")));
                    }
                    break;
                }
            }
            return referenceListImpl;
        }
        
        Transforms newTransforms(final Element element) {
            return null;
        }
        
        Element toElement(final AgreementMethod agreementMethod) {
            return ((AgreementMethodImpl)agreementMethod).toElement();
        }
        
        Element toElement(final CipherData cipherData) {
            return ((CipherDataImpl)cipherData).toElement();
        }
        
        Element toElement(final CipherReference cipherReference) {
            return ((CipherReferenceImpl)cipherReference).toElement();
        }
        
        Element toElement(final CipherValue cipherValue) {
            return ((CipherValueImpl)cipherValue).toElement();
        }
        
        Element toElement(final EncryptedData encryptedData) {
            return ((EncryptedDataImpl)encryptedData).toElement();
        }
        
        Element toElement(final EncryptedKey encryptedKey) {
            return ((EncryptedKeyImpl)encryptedKey).toElement();
        }
        
        Element toElement(final EncryptionMethod encryptionMethod) {
            return ((EncryptionMethodImpl)encryptionMethod).toElement();
        }
        
        Element toElement(final EncryptionProperties encryptionProperties) {
            return ((EncryptionPropertiesImpl)encryptionProperties).toElement();
        }
        
        Element toElement(final EncryptionProperty encryptionProperty) {
            return ((EncryptionPropertyImpl)encryptionProperty).toElement();
        }
        
        Element toElement(final ReferenceList list) {
            return ((ReferenceListImpl)list).toElement();
        }
        
        Element toElement(final Transforms transforms) {
            return ((TransformsImpl)transforms).toElement();
        }
        
        private class AgreementMethodImpl implements AgreementMethod
        {
            private byte[] kaNonce;
            private List agreementMethodInformation;
            private KeyInfo originatorKeyInfo;
            private KeyInfo recipientKeyInfo;
            private String algorithmURI;
            
            public AgreementMethodImpl(final String s) {
                this.kaNonce = null;
                this.agreementMethodInformation = null;
                this.originatorKeyInfo = null;
                this.recipientKeyInfo = null;
                this.algorithmURI = null;
                this.agreementMethodInformation = new LinkedList();
                URI uri = null;
                try {
                    uri = new URI(s);
                }
                catch (final URI.MalformedURIException ex) {}
                this.algorithmURI = uri.toString();
            }
            
            public byte[] getKANonce() {
                return this.kaNonce;
            }
            
            public void setKANonce(final byte[] kaNonce) {
                this.kaNonce = kaNonce;
            }
            
            public Iterator getAgreementMethodInformation() {
                return this.agreementMethodInformation.iterator();
            }
            
            public void addAgreementMethodInformation(final Element element) {
                this.agreementMethodInformation.add(element);
            }
            
            public void revoveAgreementMethodInformation(final Element element) {
                this.agreementMethodInformation.remove(element);
            }
            
            public KeyInfo getOriginatorKeyInfo() {
                return this.originatorKeyInfo;
            }
            
            public void setOriginatorKeyInfo(final KeyInfo originatorKeyInfo) {
                this.originatorKeyInfo = originatorKeyInfo;
            }
            
            public KeyInfo getRecipientKeyInfo() {
                return this.recipientKeyInfo;
            }
            
            public void setRecipientKeyInfo(final KeyInfo recipientKeyInfo) {
                this.recipientKeyInfo = recipientKeyInfo;
            }
            
            public String getAlgorithm() {
                return this.algorithmURI;
            }
            
            public void setAlgorithm(final String s) {
                URI uri = null;
                try {
                    uri = new URI(s);
                }
                catch (final URI.MalformedURIException ex) {}
                this.algorithmURI = uri.toString();
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "AgreementMethod");
                elementForFamily.setAttributeNS(null, "Algorithm", this.algorithmURI);
                if (null != this.kaNonce) {
                    elementForFamily.appendChild(ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "KA-Nonce")).appendChild(XMLCipher.this._contextDocument.createTextNode(new String(this.kaNonce)));
                }
                if (!this.agreementMethodInformation.isEmpty()) {
                    final Iterator iterator = this.agreementMethodInformation.iterator();
                    while (iterator.hasNext()) {
                        elementForFamily.appendChild((Node)iterator.next());
                    }
                }
                if (null != this.originatorKeyInfo) {
                    elementForFamily.appendChild(this.originatorKeyInfo.getElement());
                }
                if (null != this.recipientKeyInfo) {
                    elementForFamily.appendChild(this.recipientKeyInfo.getElement());
                }
                return elementForFamily;
            }
        }
        
        private class CipherDataImpl implements CipherData
        {
            private static final String valueMessage = "Data type is reference type.";
            private static final String referenceMessage = "Data type is value type.";
            private CipherValue cipherValue;
            private CipherReference cipherReference;
            private int cipherType;
            
            public CipherDataImpl(final int cipherType) {
                this.cipherValue = null;
                this.cipherReference = null;
                this.cipherType = Integer.MIN_VALUE;
                this.cipherType = cipherType;
            }
            
            public CipherValue getCipherValue() {
                return this.cipherValue;
            }
            
            public void setCipherValue(final CipherValue cipherValue) throws XMLEncryptionException {
                if (this.cipherType == 2) {
                    throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is reference type."));
                }
                this.cipherValue = cipherValue;
            }
            
            public CipherReference getCipherReference() {
                return this.cipherReference;
            }
            
            public void setCipherReference(final CipherReference cipherReference) throws XMLEncryptionException {
                if (this.cipherType == 1) {
                    throw new XMLEncryptionException("empty", new UnsupportedOperationException("Data type is value type."));
                }
                this.cipherReference = cipherReference;
            }
            
            public int getDataType() {
                return this.cipherType;
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CipherData");
                if (this.cipherType == 1) {
                    elementForFamily.appendChild(((CipherValueImpl)this.cipherValue).toElement());
                }
                else if (this.cipherType == 2) {
                    elementForFamily.appendChild(((CipherReferenceImpl)this.cipherReference).toElement());
                }
                return elementForFamily;
            }
        }
        
        private class CipherReferenceImpl implements CipherReference
        {
            private String referenceURI;
            private Transforms referenceTransforms;
            private Attr referenceNode;
            
            public CipherReferenceImpl(final String referenceURI) {
                this.referenceURI = null;
                this.referenceTransforms = null;
                this.referenceNode = null;
                this.referenceURI = referenceURI;
                this.referenceNode = null;
            }
            
            public CipherReferenceImpl(final Attr referenceNode) {
                this.referenceURI = null;
                this.referenceTransforms = null;
                this.referenceNode = null;
                this.referenceURI = referenceNode.getNodeValue();
                this.referenceNode = referenceNode;
            }
            
            public String getURI() {
                return this.referenceURI;
            }
            
            public Attr getURIAsAttr() {
                return this.referenceNode;
            }
            
            public Transforms getTransforms() {
                return this.referenceTransforms;
            }
            
            public void setTransforms(final Transforms referenceTransforms) {
                this.referenceTransforms = referenceTransforms;
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CipherReference");
                elementForFamily.setAttributeNS(null, "URI", this.referenceURI);
                if (null != this.referenceTransforms) {
                    elementForFamily.appendChild(((TransformsImpl)this.referenceTransforms).toElement());
                }
                return elementForFamily;
            }
        }
        
        private class TransformsImpl extends org.apache.xml.security.transforms.Transforms implements Transforms
        {
            public TransformsImpl() {
                super(Factory.this.this$0._contextDocument);
            }
            
            public TransformsImpl(final Document doc) {
                if (doc == null) {
                    throw new RuntimeException("Document is null");
                }
                super._doc = doc;
                super._state = 0;
                super._constructionElement = this.createElementForFamilyLocal(super._doc, this.getBaseNamespace(), this.getBaseLocalName());
            }
            
            public TransformsImpl(final Element element) throws XMLSignatureException, InvalidTransformException, XMLSecurityException, TransformationException {
                super(element, "");
            }
            
            public Element toElement() {
                if (super._doc == null) {
                    super._doc = XMLCipher.this._contextDocument;
                }
                return this.getElement();
            }
            
            public org.apache.xml.security.transforms.Transforms getDSTransforms() {
                return this;
            }
            
            public String getBaseNamespace() {
                return "http://www.w3.org/2001/04/xmlenc#";
            }
        }
        
        private class CipherValueImpl implements CipherValue
        {
            private String cipherValue;
            
            public CipherValueImpl(final String cipherValue) {
                this.cipherValue = null;
                this.cipherValue = cipherValue;
            }
            
            public String getValue() {
                return this.cipherValue;
            }
            
            public void setValue(final String cipherValue) {
                this.cipherValue = cipherValue;
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CipherValue");
                elementForFamily.appendChild(XMLCipher.this._contextDocument.createTextNode(this.cipherValue));
                return elementForFamily;
            }
        }
        
        private class EncryptedDataImpl extends EncryptedTypeImpl implements EncryptedData
        {
            public EncryptedDataImpl(final CipherData cipherData) {
                super(cipherData);
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedData");
                if (null != super.getId()) {
                    elementForFamily.setAttributeNS(null, "Id", super.getId());
                }
                if (null != super.getType()) {
                    elementForFamily.setAttributeNS(null, "Type", super.getType());
                }
                if (null != super.getMimeType()) {
                    elementForFamily.setAttributeNS(null, "MimeType", super.getMimeType());
                }
                if (null != super.getEncoding()) {
                    elementForFamily.setAttributeNS(null, "Encoding", super.getEncoding());
                }
                if (null != super.getEncryptionMethod()) {
                    elementForFamily.appendChild(((EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
                }
                if (null != super.getKeyInfo()) {
                    elementForFamily.appendChild(super.getKeyInfo().getElement());
                }
                elementForFamily.appendChild(((CipherDataImpl)super.getCipherData()).toElement());
                if (null != super.getEncryptionProperties()) {
                    elementForFamily.appendChild(((EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
                }
                return elementForFamily;
            }
        }
        
        private abstract class EncryptedTypeImpl
        {
            private String id;
            private String type;
            private String mimeType;
            private String encoding;
            private EncryptionMethod encryptionMethod;
            private KeyInfo keyInfo;
            private CipherData cipherData;
            private EncryptionProperties encryptionProperties;
            
            protected EncryptedTypeImpl(final CipherData cipherData) {
                this.id = null;
                this.type = null;
                this.mimeType = null;
                this.encoding = null;
                this.encryptionMethod = null;
                this.keyInfo = null;
                this.cipherData = null;
                this.encryptionProperties = null;
                this.cipherData = cipherData;
            }
            
            public String getId() {
                return this.id;
            }
            
            public void setId(final String id) {
                this.id = id;
            }
            
            public String getType() {
                return this.type;
            }
            
            public void setType(final String s) {
                URI uri = null;
                try {
                    uri = new URI(s);
                }
                catch (final URI.MalformedURIException ex) {}
                this.type = uri.toString();
            }
            
            public String getMimeType() {
                return this.mimeType;
            }
            
            public void setMimeType(final String mimeType) {
                this.mimeType = mimeType;
            }
            
            public String getEncoding() {
                return this.encoding;
            }
            
            public void setEncoding(final String s) {
                URI uri = null;
                try {
                    uri = new URI(s);
                }
                catch (final URI.MalformedURIException ex) {}
                this.encoding = uri.toString();
            }
            
            public EncryptionMethod getEncryptionMethod() {
                return this.encryptionMethod;
            }
            
            public void setEncryptionMethod(final EncryptionMethod encryptionMethod) {
                this.encryptionMethod = encryptionMethod;
            }
            
            public KeyInfo getKeyInfo() {
                return this.keyInfo;
            }
            
            public void setKeyInfo(final KeyInfo keyInfo) {
                this.keyInfo = keyInfo;
            }
            
            public CipherData getCipherData() {
                return this.cipherData;
            }
            
            public EncryptionProperties getEncryptionProperties() {
                return this.encryptionProperties;
            }
            
            public void setEncryptionProperties(final EncryptionProperties encryptionProperties) {
                this.encryptionProperties = encryptionProperties;
            }
        }
        
        private class EncryptionMethodImpl implements EncryptionMethod
        {
            private String algorithm;
            private int keySize;
            private byte[] oaepParams;
            private List encryptionMethodInformation;
            
            public EncryptionMethodImpl(final String s) {
                this.algorithm = null;
                this.keySize = Integer.MIN_VALUE;
                this.oaepParams = null;
                this.encryptionMethodInformation = null;
                URI uri = null;
                try {
                    uri = new URI(s);
                }
                catch (final URI.MalformedURIException ex) {}
                this.algorithm = uri.toString();
                this.encryptionMethodInformation = new LinkedList();
            }
            
            public String getAlgorithm() {
                return this.algorithm;
            }
            
            public int getKeySize() {
                return this.keySize;
            }
            
            public void setKeySize(final int keySize) {
                this.keySize = keySize;
            }
            
            public byte[] getOAEPparams() {
                return this.oaepParams;
            }
            
            public void setOAEPparams(final byte[] oaepParams) {
                this.oaepParams = oaepParams;
            }
            
            public Iterator getEncryptionMethodInformation() {
                return this.encryptionMethodInformation.iterator();
            }
            
            public void addEncryptionMethodInformation(final Element element) {
                this.encryptionMethodInformation.add(element);
            }
            
            public void removeEncryptionMethodInformation(final Element element) {
                this.encryptionMethodInformation.remove(element);
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptionMethod");
                elementForFamily.setAttributeNS(null, "Algorithm", this.algorithm);
                if (this.keySize > 0) {
                    elementForFamily.appendChild(ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "KeySize").appendChild(XMLCipher.this._contextDocument.createTextNode(String.valueOf(this.keySize))));
                }
                if (null != this.oaepParams) {
                    elementForFamily.appendChild(ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "OAEPparams").appendChild(XMLCipher.this._contextDocument.createTextNode(new String(this.oaepParams))));
                }
                if (!this.encryptionMethodInformation.isEmpty()) {
                    elementForFamily.appendChild(this.encryptionMethodInformation.iterator().next());
                }
                return elementForFamily;
            }
        }
        
        private class EncryptionPropertiesImpl implements EncryptionProperties
        {
            private String id;
            private List encryptionProperties;
            
            public EncryptionPropertiesImpl() {
                this.id = null;
                this.encryptionProperties = null;
                this.encryptionProperties = new LinkedList();
            }
            
            public String getId() {
                return this.id;
            }
            
            public void setId(final String id) {
                this.id = id;
            }
            
            public Iterator getEncryptionProperties() {
                return this.encryptionProperties.iterator();
            }
            
            public void addEncryptionProperty(final EncryptionProperty encryptionProperty) {
                this.encryptionProperties.add(encryptionProperty);
            }
            
            public void removeEncryptionProperty(final EncryptionProperty encryptionProperty) {
                this.encryptionProperties.remove(encryptionProperty);
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptionProperties");
                if (null != this.id) {
                    elementForFamily.setAttributeNS(null, "Id", this.id);
                }
                final Iterator encryptionProperties = this.getEncryptionProperties();
                while (encryptionProperties.hasNext()) {
                    elementForFamily.appendChild(((EncryptionPropertyImpl)encryptionProperties.next()).toElement());
                }
                return elementForFamily;
            }
        }
        
        private class EncryptionPropertyImpl implements EncryptionProperty
        {
            private String target;
            private String id;
            private HashMap attributeMap;
            private List encryptionInformation;
            
            public EncryptionPropertyImpl() {
                this.target = null;
                this.id = null;
                this.attributeMap = new HashMap();
                this.encryptionInformation = null;
                this.encryptionInformation = new LinkedList();
            }
            
            public String getTarget() {
                return this.target;
            }
            
            public void setTarget(final String s) {
                URI uri = null;
                try {
                    uri = new URI(s);
                }
                catch (final URI.MalformedURIException ex) {}
                this.target = uri.toString();
            }
            
            public String getId() {
                return this.id;
            }
            
            public void setId(final String id) {
                this.id = id;
            }
            
            public String getAttribute(final String s) {
                return this.attributeMap.get(s);
            }
            
            public void setAttribute(final String s, final String s2) {
                this.attributeMap.put(s, s2);
            }
            
            public Iterator getEncryptionInformation() {
                return this.encryptionInformation.iterator();
            }
            
            public void addEncryptionInformation(final Element element) {
                this.encryptionInformation.add(element);
            }
            
            public void removeEncryptionInformation(final Element element) {
                this.encryptionInformation.remove(element);
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptionProperty");
                if (null != this.target) {
                    elementForFamily.setAttributeNS(null, "Target", this.target);
                }
                if (null != this.id) {
                    elementForFamily.setAttributeNS(null, "Id", this.id);
                }
                return elementForFamily;
            }
        }
        
        private class EncryptedKeyImpl extends EncryptedTypeImpl implements EncryptedKey
        {
            private String keyRecipient;
            private ReferenceList referenceList;
            private String carriedName;
            
            public EncryptedKeyImpl(final CipherData cipherData) {
                super(cipherData);
                this.keyRecipient = null;
                this.referenceList = null;
                this.carriedName = null;
            }
            
            public String getRecipient() {
                return this.keyRecipient;
            }
            
            public void setRecipient(final String keyRecipient) {
                this.keyRecipient = keyRecipient;
            }
            
            public ReferenceList getReferenceList() {
                return this.referenceList;
            }
            
            public void setReferenceList(final ReferenceList referenceList) {
                this.referenceList = referenceList;
            }
            
            public String getCarriedName() {
                return this.carriedName;
            }
            
            public void setCarriedName(final String carriedName) {
                this.carriedName = carriedName;
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "EncryptedKey");
                if (null != super.getId()) {
                    elementForFamily.setAttributeNS(null, "Id", super.getId());
                }
                if (null != super.getType()) {
                    elementForFamily.setAttributeNS(null, "Type", super.getType());
                }
                if (null != super.getMimeType()) {
                    elementForFamily.setAttributeNS(null, "MimeType", super.getMimeType());
                }
                if (null != super.getEncoding()) {
                    elementForFamily.setAttributeNS(null, "Encoding", super.getEncoding());
                }
                if (null != this.getRecipient()) {
                    elementForFamily.setAttributeNS(null, "Recipient", this.getRecipient());
                }
                if (null != super.getEncryptionMethod()) {
                    elementForFamily.appendChild(((EncryptionMethodImpl)super.getEncryptionMethod()).toElement());
                }
                if (null != super.getKeyInfo()) {
                    elementForFamily.appendChild(super.getKeyInfo().getElement());
                }
                elementForFamily.appendChild(((CipherDataImpl)super.getCipherData()).toElement());
                if (null != super.getEncryptionProperties()) {
                    elementForFamily.appendChild(((EncryptionPropertiesImpl)super.getEncryptionProperties()).toElement());
                }
                if (this.referenceList != null && !this.referenceList.isEmpty()) {
                    elementForFamily.appendChild(((ReferenceListImpl)this.getReferenceList()).toElement());
                }
                if (null != this.carriedName) {
                    final Element elementForFamily2 = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "CarriedKeyName");
                    elementForFamily2.appendChild(XMLCipher.this._contextDocument.createTextNode(this.carriedName));
                    elementForFamily.appendChild(elementForFamily2);
                }
                return elementForFamily;
            }
        }
        
        private class ReferenceListImpl implements ReferenceList
        {
            private Class sentry;
            private List references;
            
            public ReferenceListImpl(final int n) {
                if (n == 1) {
                    this.sentry = DataReference.class;
                }
                else {
                    if (n != 2) {
                        throw new IllegalArgumentException();
                    }
                    this.sentry = KeyReference.class;
                }
                this.references = new LinkedList();
            }
            
            public void add(final Reference reference) {
                if (!reference.getClass().equals(this.sentry)) {
                    throw new IllegalArgumentException();
                }
                this.references.add(reference);
            }
            
            public void remove(final Reference reference) {
                if (!reference.getClass().equals(this.sentry)) {
                    throw new IllegalArgumentException();
                }
                this.references.remove(reference);
            }
            
            public int size() {
                return this.references.size();
            }
            
            public boolean isEmpty() {
                return this.references.isEmpty();
            }
            
            public Iterator getReferences() {
                return this.references.iterator();
            }
            
            Element toElement() {
                final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", "ReferenceList");
                final Iterator iterator = this.references.iterator();
                while (iterator.hasNext()) {
                    elementForFamily.appendChild(((ReferenceImpl)iterator.next()).toElement());
                }
                return elementForFamily;
            }
            
            public Reference newDataReference(final String s) {
                return new DataReference(s);
            }
            
            public Reference newKeyReference(final String s) {
                return new KeyReference(s);
            }
            
            private class DataReference extends ReferenceImpl
            {
                DataReference(final String s) {
                    super(s);
                }
                
                public Element toElement() {
                    return super.toElement("DataReference");
                }
            }
            
            private abstract class ReferenceImpl implements Reference
            {
                private String uri;
                private List referenceInformation;
                
                ReferenceImpl(final String uri) {
                    this.uri = uri;
                    this.referenceInformation = new LinkedList();
                }
                
                public String getURI() {
                    return this.uri;
                }
                
                public Iterator getElementRetrievalInformation() {
                    return this.referenceInformation.iterator();
                }
                
                public void setURI(final String uri) {
                    this.uri = uri;
                }
                
                public void removeElementRetrievalInformation(final Element element) {
                    this.referenceInformation.remove(element);
                }
                
                public void addElementRetrievalInformation(final Element element) {
                    this.referenceInformation.add(element);
                }
                
                public abstract Element toElement();
                
                Element toElement(final String s) {
                    final Element elementForFamily = ElementProxy.createElementForFamily(XMLCipher.this._contextDocument, "http://www.w3.org/2001/04/xmlenc#", s);
                    elementForFamily.setAttribute("URI", this.uri);
                    return elementForFamily;
                }
            }
            
            private class KeyReference extends ReferenceImpl
            {
                KeyReference(final String s) {
                    super(s);
                }
                
                public Element toElement() {
                    return super.toElement("KeyReference");
                }
            }
        }
    }
    
    private class Serializer
    {
        Serializer() {
        }
        
        String serialize(final Document document) throws Exception {
            return this.canonSerialize(document);
        }
        
        String serialize(final Element element) throws Exception {
            return this.canonSerialize(element);
        }
        
        String serialize(final NodeList list) throws Exception {
            final ByteArrayOutputStream writer = new ByteArrayOutputStream();
            XMLCipher.this._canon.setWriter(writer);
            XMLCipher.this._canon.notReset();
            for (int i = 0; i < list.getLength(); ++i) {
                XMLCipher.this._canon.canonicalizeSubtree(list.item(i));
            }
            writer.close();
            return writer.toString("UTF-8");
        }
        
        String canonSerialize(final Node node) throws Exception {
            final ByteArrayOutputStream writer = new ByteArrayOutputStream();
            XMLCipher.this._canon.setWriter(writer);
            XMLCipher.this._canon.notReset();
            XMLCipher.this._canon.canonicalizeSubtree(node);
            writer.close();
            return writer.toString("UTF-8");
        }
        
        DocumentFragment deserialize(final String s, final Node node) throws XMLEncryptionException {
            final StringBuffer sb = new StringBuffer();
            sb.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?><fragment");
            for (Node parentNode = node; parentNode != null; parentNode = parentNode.getParentNode()) {
                final NamedNodeMap attributes = parentNode.getAttributes();
                int length;
                if (attributes != null) {
                    length = attributes.getLength();
                }
                else {
                    length = 0;
                }
                for (int i = 0; i < length; ++i) {
                    final Node item = attributes.item(i);
                    if (item.getNodeName().startsWith("xmlns:") || item.getNodeName().equals("xmlns")) {
                        Node parentNode2 = node;
                        boolean b = false;
                        while (parentNode2 != parentNode) {
                            final NamedNodeMap attributes2 = parentNode2.getAttributes();
                            if (attributes2 != null && attributes2.getNamedItem(item.getNodeName()) != null) {
                                b = true;
                                break;
                            }
                            parentNode2 = parentNode2.getParentNode();
                        }
                        if (!b) {
                            sb.append(" " + item.getNodeName() + "=\"" + item.getNodeValue() + "\"");
                        }
                    }
                }
            }
            sb.append(">" + s + "</" + "fragment" + ">");
            final String string = sb.toString();
            DocumentFragment documentFragment;
            try {
                final DocumentBuilderFactory instance = DocumentBuilderFactory.newInstance();
                instance.setNamespaceAware(true);
                instance.setAttribute("http://xml.org/sax/features/namespaces", Boolean.TRUE);
                final Element element = (Element)XMLCipher.this._contextDocument.importNode(instance.newDocumentBuilder().parse(new InputSource(new StringReader(string))).getDocumentElement(), true);
                documentFragment = XMLCipher.this._contextDocument.createDocumentFragment();
                for (Node node2 = element.getFirstChild(); node2 != null; node2 = element.getFirstChild()) {
                    element.removeChild(node2);
                    documentFragment.appendChild(node2);
                }
            }
            catch (final SAXException ex) {
                throw new XMLEncryptionException("empty", ex);
            }
            catch (final ParserConfigurationException ex2) {
                throw new XMLEncryptionException("empty", ex2);
            }
            catch (final IOException ex3) {
                throw new XMLEncryptionException("empty", ex3);
            }
            return documentFragment;
        }
    }
}
