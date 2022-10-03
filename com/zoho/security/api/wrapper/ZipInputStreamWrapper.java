package com.zoho.security.api.wrapper;

import java.util.zip.ZipEntry;
import java.io.IOException;
import java.util.logging.Level;
import com.adventnet.iam.security.IAMSecurityException;
import java.io.FileInputStream;
import com.adventnet.iam.security.ZSecConstants;
import com.zoho.security.validator.zip.ZSecZipFile;
import com.zoho.security.validator.zip.ZSecZipSanitizer;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.File;
import com.zoho.security.validator.zip.ZipSanitizerRule;
import java.util.zip.ZipInputStream;
import java.util.logging.Logger;

public class ZipInputStreamWrapper
{
    private static final Logger LOGGER;
    private ZipInputStream safeInputStream;
    ZipSanitizerRule sanitizerRule;
    File safeZipDest;
    
    public ZipInputStreamWrapper(final InputStream in) {
        this(in, StandardCharsets.UTF_8);
    }
    
    public ZipInputStreamWrapper(final InputStream in, final ZipSanitizerRule rule) {
        this(in, StandardCharsets.UTF_8, rule);
    }
    
    public ZipInputStreamWrapper(final InputStream in, final Charset charset) {
        this(in, charset, new ZipSanitizerRule());
    }
    
    public ZipInputStreamWrapper(InputStream in, final Charset charset, final ZipSanitizerRule rule) {
        this.safeZipDest = null;
        this.sanitizerRule = rule;
        try {
            ZSecZipSanitizer.copyStreamToFile(this.safeZipDest = File.createTempFile("SFZEW_FIS" + System.currentTimeMillis(), ".zip"), in);
            if ("sanitize".equals(this.sanitizerRule.getAction())) {
                ZSecZipSanitizer.extract(this.sanitizerRule, new ZSecZipFile(this.safeZipDest, charset, false), null, ZSecConstants.DESTINATION_TYPE.ZIP);
            }
            else {
                ZSecZipSanitizer.extract(this.sanitizerRule, new ZSecZipFile(this.safeZipDest, charset, false), null, ZSecConstants.DESTINATION_TYPE.NONE);
            }
            in = new FileInputStream(this.safeZipDest);
            this.safeInputStream = new ZipInputStream(in, charset);
        }
        catch (final IAMSecurityException e) {
            throw e;
        }
        catch (final IOException e2) {
            ZipInputStreamWrapper.LOGGER.log(Level.SEVERE, " IOException while creating ZipInputStream : {0}", e2.getMessage());
            throw new IAMSecurityException("ZIPSANITIZER_ERROR");
        }
        finally {
            if (this.safeZipDest != null && this.safeZipDest.exists()) {
                this.safeZipDest.delete();
            }
        }
    }
    
    public ZipEntry getNextEntry() throws IOException {
        if (this.isValidInputStream()) {
            return this.safeInputStream.getNextEntry();
        }
        return null;
    }
    
    public void closeEntry() throws IOException {
        if (this.isValidInputStream()) {
            this.safeInputStream.closeEntry();
        }
    }
    
    public int available() throws IOException {
        if (this.isValidInputStream()) {
            return this.safeInputStream.available();
        }
        return 0;
    }
    
    public int read(final byte[] b, final int off, final int len) throws IOException {
        if (this.isValidInputStream()) {
            return this.safeInputStream.read(b, off, len);
        }
        return -1;
    }
    
    public long skip(final long n) throws IOException {
        if (this.isValidInputStream()) {
            this.safeInputStream.skip(n);
        }
        return -1L;
    }
    
    public void close() throws IOException {
        if (this.isValidInputStream()) {
            this.safeInputStream.close();
        }
        if (this.safeZipDest != null) {
            this.safeZipDest.delete();
        }
    }
    
    private boolean isValidInputStream() {
        if (this.safeInputStream == null) {
            ZipInputStreamWrapper.LOGGER.log(Level.SEVERE, " SafeInputStream instance is null  ");
            throw new IAMSecurityException("ZIPSANITIZER_ERROR");
        }
        return true;
    }
    
    public ZipInputStream getStream() {
        return this.safeInputStream;
    }
    
    static {
        LOGGER = Logger.getLogger(ZipFileWrapper.class.getName());
    }
}
