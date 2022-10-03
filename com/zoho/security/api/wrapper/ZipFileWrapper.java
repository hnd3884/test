package com.zoho.security.api.wrapper;

import java.util.stream.Stream;
import org.apache.commons.compress.archivers.sevenz.SevenZArchiveEntry;
import java.io.InputStream;
import java.util.zip.ZipEntry;
import java.util.Enumeration;
import com.adventnet.iam.security.IAMSecurityException;
import java.util.logging.Level;
import com.zoho.security.validator.zip.ZSecZipSanitizer;
import com.adventnet.iam.security.ZSecConstants;
import com.adventnet.iam.security.SecurityUtil;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.IOException;
import java.io.File;
import com.zoho.security.validator.zip.ZipSanitizerRule;
import com.zoho.security.validator.zip.ZSecZipFile;
import java.util.logging.Logger;

public class ZipFileWrapper
{
    private static final Logger LOGGER;
    ZSecZipFile zipfile;
    ZipSanitizerRule sanitizerRule;
    private String name;
    private ZSecZipFile safeZipFile;
    File safeZipDest;
    
    public ZipFileWrapper(final File file) throws IOException {
        this(file, 1);
    }
    
    public ZipFileWrapper(final File file, final ZipSanitizerRule rule) throws IOException {
        this(file, 1, StandardCharsets.UTF_8, rule);
    }
    
    public ZipFileWrapper(final File file, final Charset charset) throws IOException {
        this(file, 1, charset);
    }
    
    public ZipFileWrapper(final File file, final int mode) throws IOException {
        this(file, mode, StandardCharsets.UTF_8);
    }
    
    public ZipFileWrapper(final String name) throws IOException {
        this(new File(name), 1);
    }
    
    public ZipFileWrapper(final String name, final Charset charset) throws IOException {
        this(new File(name), 1, charset);
    }
    
    public ZipFileWrapper(final File file, final int mode, final Charset charset) throws IOException {
        this(file, mode, charset, new ZipSanitizerRule());
    }
    
    public ZipFileWrapper(final File file, final int mode, final Charset charset, final ZipSanitizerRule rule) throws IOException {
        this(file, mode, charset, rule, false);
    }
    
    public ZipFileWrapper(final File file, final int mode, final Charset charset, final ZipSanitizerRule rule, final boolean validate7zipFile) {
        this.safeZipFile = null;
        this.safeZipDest = null;
        this.name = file.getName();
        this.sanitizerRule = rule;
        try {
            final boolean is7Zip = validate7zipFile && SecurityUtil.is7Zip(SecurityUtil.getMimeTypeUsingTika(file, this.name));
            this.zipfile = new ZSecZipFile(file, mode, charset, is7Zip);
            if ("sanitize".equals(this.sanitizerRule.getAction())) {
                this.safeZipDest = File.createTempFile("SFZEW_FW" + System.currentTimeMillis(), this.name);
                ZSecZipSanitizer.extract(this.sanitizerRule, this.zipfile, this.safeZipDest, ZSecConstants.DESTINATION_TYPE.ZIP);
                this.safeZipFile = new ZSecZipFile(this.safeZipDest, 5, is7Zip);
            }
            else {
                ZSecZipSanitizer.extract(this.sanitizerRule, this.zipfile, null, ZSecConstants.DESTINATION_TYPE.NONE);
                this.safeZipFile = this.zipfile;
            }
        }
        catch (final Exception e) {
            if (this.zipfile != null) {
                try {
                    this.zipfile.close();
                }
                catch (final IOException ioEx) {
                    ZipFileWrapper.LOGGER.log(Level.SEVERE, "Unable to close the ZIP File, exception : {0}", ioEx.getMessage());
                }
            }
            if (e instanceof IAMSecurityException) {
                throw (IAMSecurityException)e;
            }
            ZipFileWrapper.LOGGER.log(Level.SEVERE, " IOException while creating ZipFile Wrapper : {0}", e.getMessage());
            throw new IAMSecurityException("ZIPSANITIZER_ERROR");
        }
        finally {
            if (this.safeZipDest != null && this.safeZipDest.exists()) {
                this.safeZipDest.delete();
            }
        }
    }
    
    public void close() throws IOException {
        if (this.isValidZip()) {
            this.safeZipFile.close();
        }
        if (this.safeZipDest != null) {
            this.safeZipDest.delete();
        }
    }
    
    public Enumeration entries() throws IOException {
        if (this.isValidZip()) {
            return this.safeZipFile.entries();
        }
        return null;
    }
    
    public String getName() {
        return this.zipfile.getName();
    }
    
    public String getComment() {
        if (this.isValidZip() && this.safeZipFile.getZipFile() != null) {
            return this.safeZipFile.getZipFile().getComment();
        }
        return null;
    }
    
    public ZipEntry getEntry(final String name) {
        if (this.isValidZip() && this.safeZipFile.getZipFile() != null) {
            return this.safeZipFile.getZipFile().getEntry(name);
        }
        return null;
    }
    
    public InputStream getInputStream(final Object entry) throws IOException {
        if (this.isValidZip()) {
            if (entry instanceof ZipEntry) {
                final ZipEntry zipentry = (ZipEntry)entry;
                if (this.safeZipFile.getZipFile() != null) {
                    return this.safeZipFile.getZipFile().getInputStream(zipentry);
                }
            }
            else if (entry instanceof SevenZArchiveEntry) {
                final SevenZArchiveEntry sevenZipEntry = (SevenZArchiveEntry)entry;
                if (this.safeZipFile.get7ZipFile() != null) {
                    return this.safeZipFile.get7ZipFile().getInputStream(sevenZipEntry);
                }
            }
        }
        return null;
    }
    
    public int size() {
        if (this.isValidZip() && this.safeZipFile.getZipFile() != null) {
            return this.safeZipFile.getZipFile().size();
        }
        return 0;
    }
    
    public Stream<? extends ZipEntry> stream() {
        if (this.isValidZip() && this.safeZipFile.getZipFile() != null) {
            return this.safeZipFile.getZipFile().stream();
        }
        return null;
    }
    
    private boolean isValidZip() {
        if (this.safeZipFile == null) {
            ZipFileWrapper.LOGGER.log(Level.SEVERE, " SafeZip Instance is null  ");
            throw new IAMSecurityException("ZIPSANITIZER_ERROR");
        }
        return true;
    }
    
    static {
        LOGGER = Logger.getLogger(ZipFileWrapper.class.getName());
    }
}
