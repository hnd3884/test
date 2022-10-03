package sun.tools.jar;

import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.ByteArrayOutputStream;
import sun.security.x509.AlgorithmId;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Enumeration;
import java.util.Locale;
import java.security.MessageDigest;
import java.util.Hashtable;
import sun.security.pkcs.PKCS7;
import sun.net.www.MessageHeader;
import java.util.Vector;

public class SignatureFile
{
    static final boolean debug = false;
    private Vector<MessageHeader> entries;
    static final String[] hashes;
    private Manifest manifest;
    private String rawName;
    private PKCS7 signatureBlock;
    private Hashtable<String, MessageDigest> digests;
    
    static final void debug(final String s) {
    }
    
    private SignatureFile(final String s) throws JarException {
        this.entries = new Vector<MessageHeader>();
        this.digests = new Hashtable<String, MessageDigest>();
        this.entries = new Vector<MessageHeader>();
        if (s != null) {
            if (s.length() > 8 || s.indexOf(46) != -1) {
                throw new JarException("invalid file name");
            }
            this.rawName = s.toUpperCase(Locale.ENGLISH);
        }
    }
    
    private SignatureFile(final String s, final boolean b) throws JarException {
        this(s);
        if (b) {
            final MessageHeader messageHeader = new MessageHeader();
            messageHeader.set("Signature-Version", "1.0");
            this.entries.addElement(messageHeader);
        }
    }
    
    public SignatureFile(final Manifest manifest, final String s) throws JarException {
        this(s, true);
        this.manifest = manifest;
        final Enumeration<MessageHeader> entries = manifest.entries();
        while (entries.hasMoreElements()) {
            final String value = entries.nextElement().findValue("Name");
            if (value != null) {
                this.add(value);
            }
        }
    }
    
    public SignatureFile(final Manifest manifest, final String[] array, final String s) throws JarException {
        this(s, true);
        this.manifest = manifest;
        this.add(array);
    }
    
    public SignatureFile(final InputStream inputStream, final String s) throws IOException {
        this(s);
        while (inputStream.available() > 0) {
            this.entries.addElement(new MessageHeader(inputStream));
        }
    }
    
    public SignatureFile(final InputStream inputStream) throws IOException {
        this(inputStream, null);
    }
    
    public SignatureFile(final byte[] array) throws IOException {
        this(new ByteArrayInputStream(array));
    }
    
    public String getName() {
        return "META-INF/" + this.rawName + ".SF";
    }
    
    public String getBlockName() {
        String name = "DSA";
        if (this.signatureBlock != null) {
            name = this.signatureBlock.getSignerInfos()[0].getDigestEncryptionAlgorithmId().getName();
            final String encAlgFromSigAlg = AlgorithmId.getEncAlgFromSigAlg(name);
            if (encAlgFromSigAlg != null) {
                name = encAlgFromSigAlg;
            }
        }
        return "META-INF/" + this.rawName + "." + name;
    }
    
    public PKCS7 getBlock() {
        return this.signatureBlock;
    }
    
    public void setBlock(final PKCS7 signatureBlock) {
        this.signatureBlock = signatureBlock;
    }
    
    public void add(final String[] array) throws JarException {
        for (int i = 0; i < array.length; ++i) {
            this.add(array[i]);
        }
    }
    
    public void add(final String s) throws JarException {
        final MessageHeader entry = this.manifest.getEntry(s);
        if (entry == null) {
            throw new JarException("entry " + s + " not in manifest");
        }
        MessageHeader computeEntry;
        try {
            computeEntry = this.computeEntry(entry);
        }
        catch (final IOException ex) {
            throw new JarException(ex.getMessage());
        }
        this.entries.addElement(computeEntry);
    }
    
    public MessageHeader getEntry(final String s) {
        final Enumeration<MessageHeader> entries = this.entries();
        while (entries.hasMoreElements()) {
            final MessageHeader messageHeader = entries.nextElement();
            if (s.equals(messageHeader.findValue("Name"))) {
                return messageHeader;
            }
        }
        return null;
    }
    
    public MessageHeader entryAt(final int n) {
        return this.entries.elementAt(n);
    }
    
    public Enumeration<MessageHeader> entries() {
        return this.entries.elements();
    }
    
    private MessageHeader computeEntry(final MessageHeader messageHeader) throws IOException {
        final MessageHeader messageHeader2 = new MessageHeader();
        final String value = messageHeader.findValue("Name");
        if (value == null) {
            return null;
        }
        messageHeader2.set("Name", value);
        try {
            for (int i = 0; i < SignatureFile.hashes.length; ++i) {
                final MessageDigest digest = this.getDigest(SignatureFile.hashes[i]);
                final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                messageHeader.print(new PrintStream(byteArrayOutputStream));
                messageHeader2.set(SignatureFile.hashes[i] + "-Digest", Base64.getMimeEncoder().encodeToString(digest.digest(byteArrayOutputStream.toByteArray())));
            }
            return messageHeader2;
        }
        catch (final NoSuchAlgorithmException ex) {
            throw new JarException(ex.getMessage());
        }
    }
    
    private MessageDigest getDigest(final String s) throws NoSuchAlgorithmException {
        MessageDigest instance = this.digests.get(s);
        if (instance == null) {
            instance = MessageDigest.getInstance(s);
            this.digests.put(s, instance);
        }
        instance.reset();
        return instance;
    }
    
    public void stream(final OutputStream outputStream) throws IOException {
        final MessageHeader messageHeader = this.entries.elementAt(0);
        if (messageHeader.findValue("Signature-Version") == null) {
            throw new JarException("Signature file requires Signature-Version: 1.0 in 1st header");
        }
        final PrintStream printStream = new PrintStream(outputStream);
        messageHeader.print(printStream);
        for (int i = 1; i < this.entries.size(); ++i) {
            this.entries.elementAt(i).print(printStream);
        }
    }
    
    static {
        hashes = new String[] { "SHA" };
    }
}
