package org.apache.poi;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.Internal;
import java.io.File;
import org.apache.poi.hpsf.WritingNotSupportedException;
import java.io.ByteArrayInputStream;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.poi.poifs.crypt.Encryptor;
import java.security.GeneralSecurityException;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIEncryptor;
import java.util.List;
import org.apache.poi.poifs.filesystem.DocumentInputStream;
import org.apache.poi.util.IOUtils;
import java.io.InputStream;
import org.apache.poi.poifs.crypt.cryptoapi.CryptoAPIDecryptor;
import org.apache.poi.poifs.crypt.EncryptionInfo;
import org.apache.poi.hpsf.PropertySet;
import java.io.IOException;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.POILogger;
import org.apache.poi.poifs.filesystem.DirectoryNode;
import org.apache.poi.hpsf.DocumentSummaryInformation;
import org.apache.poi.hpsf.SummaryInformation;
import java.io.Closeable;

public abstract class POIDocument implements Closeable
{
    private SummaryInformation sInf;
    private DocumentSummaryInformation dsInf;
    private DirectoryNode directory;
    private static final POILogger logger;
    private boolean initialized;
    
    protected POIDocument(final DirectoryNode dir) {
        this.directory = dir;
    }
    
    protected POIDocument(final POIFSFileSystem fs) {
        this(fs.getRoot());
    }
    
    public DocumentSummaryInformation getDocumentSummaryInformation() {
        if (!this.initialized) {
            this.readProperties();
        }
        return this.dsInf;
    }
    
    public SummaryInformation getSummaryInformation() {
        if (!this.initialized) {
            this.readProperties();
        }
        return this.sInf;
    }
    
    public void createInformationProperties() {
        if (!this.initialized) {
            this.readProperties();
        }
        if (this.sInf == null) {
            this.sInf = PropertySetFactory.newSummaryInformation();
        }
        if (this.dsInf == null) {
            this.dsInf = PropertySetFactory.newDocumentSummaryInformation();
        }
    }
    
    protected void readProperties() {
        if (this.initialized) {
            return;
        }
        final DocumentSummaryInformation dsi = this.readPropertySet(DocumentSummaryInformation.class, "\u0005DocumentSummaryInformation");
        if (dsi != null) {
            this.dsInf = dsi;
        }
        final SummaryInformation si = this.readPropertySet(SummaryInformation.class, "\u0005SummaryInformation");
        if (si != null) {
            this.sInf = si;
        }
        this.initialized = true;
    }
    
    private <T> T readPropertySet(final Class<T> clazz, final String name) {
        final String localName = clazz.getName().substring(clazz.getName().lastIndexOf(46) + 1);
        try {
            final PropertySet ps = this.getPropertySet(name);
            if (clazz.isInstance(ps)) {
                return (T)ps;
            }
            if (ps != null) {
                POIDocument.logger.log(5, localName + " property set came back with wrong class - " + ps.getClass().getName());
            }
            else {
                POIDocument.logger.log(5, localName + " property set came back as null");
            }
        }
        catch (final IOException e) {
            POIDocument.logger.log(7, "can't retrieve property set", e);
        }
        return null;
    }
    
    protected PropertySet getPropertySet(final String setName) throws IOException {
        return this.getPropertySet(setName, this.getEncryptionInfo());
    }
    
    protected PropertySet getPropertySet(final String setName, final EncryptionInfo encryptionInfo) throws IOException {
        DirectoryNode dirNode = this.directory;
        POIFSFileSystem encPoifs = null;
        String step = "getting";
        try {
            if (encryptionInfo != null && encryptionInfo.isDocPropsEncrypted()) {
                step = "getting encrypted";
                final String encryptedStream = this.getEncryptedPropertyStreamName();
                if (!dirNode.hasEntry(encryptedStream)) {
                    throw new EncryptedDocumentException("can't find encrypted property stream '" + encryptedStream + "'");
                }
                final CryptoAPIDecryptor dec = (CryptoAPIDecryptor)encryptionInfo.getDecryptor();
                encPoifs = dec.getSummaryEntries(dirNode, encryptedStream);
                dirNode = encPoifs.getRoot();
            }
            if (dirNode == null || !dirNode.hasEntry(setName)) {
                return null;
            }
            step = "getting";
            try (final DocumentInputStream dis = dirNode.createDocumentInputStream(dirNode.getEntry(setName))) {
                step = "creating";
                return PropertySetFactory.create(dis);
            }
        }
        catch (final IOException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new IOException("Error " + step + " property set with name " + setName, e2);
        }
        finally {
            IOUtils.closeQuietly(encPoifs);
        }
    }
    
    protected void writeProperties() throws IOException {
        this.validateInPlaceWritePossible();
        this.writeProperties(this.directory.getFileSystem(), null);
    }
    
    protected void writeProperties(final POIFSFileSystem outFS) throws IOException {
        this.writeProperties(outFS, null);
    }
    
    protected void writeProperties(final POIFSFileSystem outFS, final List<String> writtenEntries) throws IOException {
        final EncryptionInfo ei = this.getEncryptionInfo();
        final boolean encryptProps = ei != null && ei.isDocPropsEncrypted();
        try (final POIFSFileSystem tmpFS = new POIFSFileSystem()) {
            final POIFSFileSystem fs = encryptProps ? tmpFS : outFS;
            this.writePropertySet("\u0005SummaryInformation", this.getSummaryInformation(), fs, writtenEntries);
            this.writePropertySet("\u0005DocumentSummaryInformation", this.getDocumentSummaryInformation(), fs, writtenEntries);
            if (!encryptProps) {
                return;
            }
            this.writePropertySet("\u0005DocumentSummaryInformation", PropertySetFactory.newDocumentSummaryInformation(), outFS);
            if (outFS.getRoot().hasEntry("\u0005SummaryInformation")) {
                outFS.getRoot().getEntry("\u0005SummaryInformation").delete();
            }
            final Encryptor encGen = ei.getEncryptor();
            if (!(encGen instanceof CryptoAPIEncryptor)) {
                throw new EncryptedDocumentException("Using " + ei.getEncryptionMode() + " encryption. Only CryptoAPI encryption supports encrypted property sets!");
            }
            final CryptoAPIEncryptor enc = (CryptoAPIEncryptor)encGen;
            try {
                enc.setSummaryEntries(outFS.getRoot(), this.getEncryptedPropertyStreamName(), fs);
            }
            catch (final GeneralSecurityException e) {
                throw new IOException(e);
            }
        }
    }
    
    private void writePropertySet(final String name, final PropertySet ps, final POIFSFileSystem outFS, final List<String> writtenEntries) throws IOException {
        if (ps == null) {
            return;
        }
        this.writePropertySet(name, ps, outFS);
        if (writtenEntries != null) {
            writtenEntries.add(name);
        }
    }
    
    private void writePropertySet(final String name, final PropertySet set, final POIFSFileSystem outFS) throws IOException {
        try {
            final PropertySet mSet = new PropertySet(set);
            final ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            mSet.write(bOut);
            final byte[] data = bOut.toByteArray();
            final ByteArrayInputStream bIn = new ByteArrayInputStream(data);
            outFS.createOrUpdateDocument(bIn, name);
            POIDocument.logger.log(3, "Wrote property set " + name + " of size " + data.length);
        }
        catch (final WritingNotSupportedException ignored) {
            POIDocument.logger.log(7, "Couldn't write property set with name " + name + " as not supported by HPSF yet");
        }
    }
    
    protected void validateInPlaceWritePossible() throws IllegalStateException {
        if (this.directory == null) {
            throw new IllegalStateException("Newly created Document, cannot save in-place");
        }
        if (this.directory.getParent() != null) {
            throw new IllegalStateException("This is not the root Document, cannot save embedded resource in-place");
        }
        if (this.directory.getFileSystem() == null || !this.directory.getFileSystem().isInPlaceWriteable()) {
            throw new IllegalStateException("Opened read-only or via an InputStream, a Writeable File is required");
        }
    }
    
    public abstract void write() throws IOException;
    
    public abstract void write(final File p0) throws IOException;
    
    public abstract void write(final OutputStream p0) throws IOException;
    
    @Override
    public void close() throws IOException {
        if (this.directory != null && this.directory.getFileSystem() != null) {
            this.directory.getFileSystem().close();
            this.clearDirectory();
        }
    }
    
    @Internal
    public DirectoryNode getDirectory() {
        return this.directory;
    }
    
    @Internal
    protected void clearDirectory() {
        this.directory = null;
    }
    
    @Internal
    protected boolean initDirectory() {
        if (this.directory == null) {
            this.directory = new POIFSFileSystem().getRoot();
            return true;
        }
        return false;
    }
    
    @Internal
    protected void replaceDirectory(final DirectoryNode newDirectory) {
        this.directory = newDirectory;
    }
    
    protected String getEncryptedPropertyStreamName() {
        return "encryption";
    }
    
    public EncryptionInfo getEncryptionInfo() throws IOException {
        return null;
    }
    
    static {
        logger = POILogFactory.getLogger(POIDocument.class);
    }
}
