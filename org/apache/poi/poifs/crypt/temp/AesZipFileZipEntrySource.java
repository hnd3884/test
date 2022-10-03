package org.apache.poi.poifs.crypt.temp;

import org.apache.poi.util.POILogFactory;
import javax.crypto.CipherOutputStream;
import java.io.FilterOutputStream;
import org.apache.commons.compress.archivers.ArchiveEntry;
import java.io.OutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import java.io.FileOutputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveInputStream;
import java.security.Key;
import org.apache.poi.poifs.crypt.CryptoFunctions;
import org.apache.poi.poifs.crypt.ChainingMode;
import javax.crypto.spec.SecretKeySpec;
import org.apache.poi.poifs.crypt.CipherAlgorithm;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.TempFile;
import java.security.SecureRandom;
import javax.crypto.CipherInputStream;
import java.io.InputStream;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import java.util.Enumeration;
import java.io.IOException;
import javax.crypto.Cipher;
import org.apache.commons.compress.archivers.zip.ZipFile;
import java.io.File;
import org.apache.poi.util.POILogger;
import org.apache.poi.openxml4j.util.ZipEntrySource;

public final class AesZipFileZipEntrySource implements ZipEntrySource
{
    private static final POILogger LOG;
    private static final String PADDING = "PKCS5Padding";
    private final File tmpFile;
    private final ZipFile zipFile;
    private final Cipher ci;
    private boolean closed;
    
    private AesZipFileZipEntrySource(final File tmpFile, final Cipher ci) throws IOException {
        this.tmpFile = tmpFile;
        this.zipFile = new ZipFile(tmpFile);
        this.ci = ci;
        this.closed = false;
    }
    
    @Override
    public Enumeration<? extends ZipArchiveEntry> getEntries() {
        return this.zipFile.getEntries();
    }
    
    @Override
    public ZipArchiveEntry getEntry(final String path) {
        return this.zipFile.getEntry(path);
    }
    
    @Override
    public InputStream getInputStream(final ZipArchiveEntry entry) throws IOException {
        final InputStream is = this.zipFile.getInputStream(entry);
        return new CipherInputStream(is, this.ci);
    }
    
    @Override
    public void close() throws IOException {
        if (!this.closed) {
            this.zipFile.close();
            if (!this.tmpFile.delete()) {
                AesZipFileZipEntrySource.LOG.log(5, new Object[] { this.tmpFile.getAbsolutePath() + " can't be removed (or was already removed." });
            }
        }
        this.closed = true;
    }
    
    @Override
    public boolean isClosed() {
        return this.closed;
    }
    
    public static AesZipFileZipEntrySource createZipEntrySource(final InputStream is) throws IOException {
        final SecureRandom sr = new SecureRandom();
        final byte[] ivBytes = new byte[16];
        final byte[] keyBytes = new byte[16];
        sr.nextBytes(ivBytes);
        sr.nextBytes(keyBytes);
        final File tmpFile = TempFile.createTempFile("protectedXlsx", ".zip");
        copyToFile(is, tmpFile, keyBytes, ivBytes);
        IOUtils.closeQuietly((Closeable)is);
        return fileToSource(tmpFile, keyBytes, ivBytes);
    }
    
    private static void copyToFile(final InputStream is, final File tmpFile, final byte[] keyBytes, final byte[] ivBytes) throws IOException {
        final SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, CipherAlgorithm.aes128.jceId);
        final Cipher ciEnc = CryptoFunctions.getCipher((Key)skeySpec, CipherAlgorithm.aes128, ChainingMode.cbc, ivBytes, 1, "PKCS5Padding");
        try (final ZipArchiveInputStream zis = new ZipArchiveInputStream(is);
             final FileOutputStream fos = new FileOutputStream(tmpFile);
             final ZipArchiveOutputStream zos = new ZipArchiveOutputStream((OutputStream)fos)) {
            ZipArchiveEntry ze;
            while ((ze = zis.getNextZipEntry()) != null) {
                final ZipArchiveEntry zeNew = new ZipArchiveEntry(ze.getName());
                zeNew.setComment(ze.getComment());
                zeNew.setExtra(ze.getExtra());
                zeNew.setTime(ze.getTime());
                zos.putArchiveEntry((ArchiveEntry)zeNew);
                final FilterOutputStream fos2 = new FilterOutputStream(zos) {
                    @Override
                    public void close() {
                    }
                };
                final CipherOutputStream cos = new CipherOutputStream(fos2, ciEnc);
                IOUtils.copy((InputStream)zis, (OutputStream)cos);
                cos.close();
                fos2.close();
                zos.closeArchiveEntry();
            }
        }
    }
    
    private static AesZipFileZipEntrySource fileToSource(final File tmpFile, final byte[] keyBytes, final byte[] ivBytes) throws IOException {
        final SecretKeySpec skeySpec = new SecretKeySpec(keyBytes, CipherAlgorithm.aes128.jceId);
        final Cipher ciDec = CryptoFunctions.getCipher((Key)skeySpec, CipherAlgorithm.aes128, ChainingMode.cbc, ivBytes, 2, "PKCS5Padding");
        return new AesZipFileZipEntrySource(tmpFile, ciDec);
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)AesZipFileZipEntrySource.class);
    }
}
