package org.apache.poi.openxml4j.opc;

import org.apache.poi.openxml4j.opc.internal.ContentTypeManager;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.internal.PartMarshaller;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPartMarshaller;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import java.io.OutputStream;
import org.apache.poi.util.TempFile;
import org.apache.poi.openxml4j.opc.internal.FileHelper;
import org.apache.poi.openxml4j.opc.internal.MemoryPackagePart;
import java.util.Iterator;
import org.apache.commons.compress.archivers.zip.ZipArchiveEntry;
import org.apache.poi.openxml4j.exceptions.NotOfficeXmlFileException;
import org.apache.poi.openxml4j.exceptions.ODFNotOfficeXmlFileException;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collections;
import java.util.List;
import org.apache.poi.UnsupportedFileFormatException;
import java.io.FileNotFoundException;
import java.io.FileInputStream;
import org.apache.commons.compress.archivers.zip.ZipFile;
import org.apache.poi.openxml4j.util.ZipFileZipEntrySource;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import java.io.File;
import org.apache.poi.openxml4j.util.ZipArchiveThresholdInputStream;
import java.io.IOException;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import org.apache.poi.openxml4j.util.ZipInputStreamZipEntrySource;
import org.apache.poi.openxml4j.opc.internal.ZipHelper;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.internal.ZipContentTypeManager;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import org.apache.poi.util.POILogger;

public final class ZipPackage extends OPCPackage
{
    private static final String MIMETYPE = "mimetype";
    private static final String SETTINGS_XML = "settings.xml";
    private static final POILogger LOG;
    private final ZipEntrySource zipArchive;
    
    public ZipPackage() {
        super(ZipPackage.defaultPackageAccess);
        this.zipArchive = null;
        try {
            this.contentTypeManager = new ZipContentTypeManager(null, this);
        }
        catch (final InvalidFormatException e) {
            ZipPackage.LOG.log(5, new Object[] { "Could not parse ZipPackage", e });
        }
    }
    
    ZipPackage(final InputStream in, final PackageAccess access) throws IOException {
        super(access);
        final ZipArchiveThresholdInputStream zis = ZipHelper.openZipStream(in);
        try {
            this.zipArchive = new ZipInputStreamZipEntrySource(zis);
        }
        catch (final IOException e) {
            IOUtils.closeQuietly((Closeable)zis);
            throw e;
        }
    }
    
    ZipPackage(final String path, final PackageAccess access) throws InvalidOperationException {
        this(new File(path), access);
    }
    
    ZipPackage(final File file, final PackageAccess access) throws InvalidOperationException {
        super(access);
        ZipEntrySource ze;
        try {
            final ZipFile zipFile = ZipHelper.openZipFile(file);
            ze = new ZipFileZipEntrySource(zipFile);
        }
        catch (final IOException e) {
            if (access == PackageAccess.WRITE) {
                throw new InvalidOperationException("Can't open the specified file: '" + file + "'", e);
            }
            ZipPackage.LOG.log(7, new Object[] { "Error in zip file " + file + " - falling back to stream processing (i.e. ignoring zip central directory)" });
            ze = openZipEntrySourceStream(file);
        }
        this.zipArchive = ze;
    }
    
    private static ZipEntrySource openZipEntrySourceStream(final File file) throws InvalidOperationException {
        FileInputStream fis;
        try {
            fis = new FileInputStream(file);
        }
        catch (final FileNotFoundException e) {
            throw new InvalidOperationException("Can't open the specified file input stream from file: '" + file + "'", e);
        }
        try {
            return openZipEntrySourceStream(fis);
        }
        catch (final InvalidOperationException | UnsupportedFileFormatException e2) {
            IOUtils.closeQuietly((Closeable)fis);
            throw e2;
        }
        catch (final Exception e3) {
            IOUtils.closeQuietly((Closeable)fis);
            throw new InvalidOperationException("Failed to read the file input stream from file: '" + file + "'", e3);
        }
    }
    
    private static ZipEntrySource openZipEntrySourceStream(final FileInputStream fis) throws InvalidOperationException {
        ZipArchiveThresholdInputStream zis;
        try {
            zis = ZipHelper.openZipStream(fis);
        }
        catch (final IOException e) {
            throw new InvalidOperationException("Could not open the file input stream", e);
        }
        try {
            return openZipEntrySourceStream(zis);
        }
        catch (final InvalidOperationException | UnsupportedFileFormatException e2) {
            IOUtils.closeQuietly((Closeable)zis);
            throw e2;
        }
        catch (final Exception e3) {
            IOUtils.closeQuietly((Closeable)zis);
            throw new InvalidOperationException("Failed to read the zip entry source stream", e3);
        }
    }
    
    private static ZipEntrySource openZipEntrySourceStream(final ZipArchiveThresholdInputStream zis) throws InvalidOperationException {
        try {
            return new ZipInputStreamZipEntrySource(zis);
        }
        catch (final IOException e) {
            throw new InvalidOperationException("Could not open the specified zip entry source stream", e);
        }
    }
    
    ZipPackage(final ZipEntrySource zipEntry, final PackageAccess access) {
        super(access);
        this.zipArchive = zipEntry;
    }
    
    @Override
    protected PackagePartCollection getPartsImpl() throws InvalidFormatException {
        final PackagePartCollection newPartList = new PackagePartCollection();
        if (this.zipArchive == null) {
            return newPartList;
        }
        final ZipArchiveEntry contentTypeEntry = this.zipArchive.getEntry("[Content_Types].xml");
        Label_0089: {
            if (contentTypeEntry != null) {
                if (this.contentTypeManager != null) {
                    throw new InvalidFormatException("ContentTypeManager can only be created once. This must be a cyclic relation?");
                }
                Label_0186: {
                    try {
                        this.contentTypeManager = new ZipContentTypeManager(this.zipArchive.getInputStream(contentTypeEntry), this);
                        break Label_0186;
                    }
                    catch (final IOException e) {
                        throw new InvalidFormatException(e.getMessage(), e);
                    }
                    break Label_0089;
                }
                final List<EntryTriple> entries = Collections.list(this.zipArchive.getEntries()).stream().map(zae -> new EntryTriple(zae, this.contentTypeManager)).filter(mm -> mm.partName != null).sorted().collect((Collector<? super Object, ?, List<EntryTriple>>)Collectors.toList());
                for (final EntryTriple et : entries) {
                    et.register(newPartList);
                }
                return newPartList;
            }
        }
        final boolean hasMimetype = this.zipArchive.getEntry("mimetype") != null;
        final boolean hasSettingsXML = this.zipArchive.getEntry("settings.xml") != null;
        if (hasMimetype && hasSettingsXML) {
            throw new ODFNotOfficeXmlFileException("The supplied data appears to be in ODF (Open Document) Format. Formats like these (eg ODS, ODP) are not supported, try Apache ODFToolkit");
        }
        if (!this.zipArchive.getEntries().hasMoreElements()) {
            throw new NotOfficeXmlFileException("No valid entries or contents found, this is not a valid OOXML (Office Open XML) file");
        }
        throw new InvalidFormatException("Package should contain a content type part [M1.13]");
    }
    
    @Override
    protected PackagePart createPartImpl(final PackagePartName partName, final String contentType, final boolean loadRelationships) {
        if (contentType == null) {
            throw new IllegalArgumentException("contentType");
        }
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        try {
            return new MemoryPackagePart(this, partName, contentType, loadRelationships);
        }
        catch (final InvalidFormatException e) {
            ZipPackage.LOG.log(5, new Object[] { e });
            return null;
        }
    }
    
    @Override
    protected void removePartImpl(final PackagePartName partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partUri");
        }
    }
    
    @Override
    protected void flushImpl() {
    }
    
    @Override
    protected void closeImpl() throws IOException {
        this.flush();
        if (this.originalPackagePath == null || this.originalPackagePath.isEmpty()) {
            return;
        }
        final File targetFile = new File(this.originalPackagePath);
        if (!targetFile.exists()) {
            throw new InvalidOperationException("Can't close a package not previously open with the open() method !");
        }
        final String tempFileName = this.generateTempFileName(FileHelper.getDirectory(targetFile));
        final File tempFile = TempFile.createTempFile(tempFileName, ".tmp");
        boolean success = false;
        try {
            this.save(tempFile);
            success = true;
        }
        finally {
            IOUtils.closeQuietly((Closeable)this.zipArchive);
            try {
                if (success) {
                    FileHelper.copyFile(tempFile, targetFile);
                }
            }
            finally {
                if (!tempFile.delete()) {
                    ZipPackage.LOG.log(5, new Object[] { "The temporary file: '" + targetFile.getAbsolutePath() + "' cannot be deleted ! Make sure that no other application use it." });
                }
            }
        }
    }
    
    private synchronized String generateTempFileName(final File directory) {
        File tmpFilename;
        do {
            tmpFilename = new File(directory.getAbsoluteFile() + File.separator + "OpenXML4J" + System.nanoTime());
        } while (tmpFilename.exists());
        return FileHelper.getFilename(tmpFilename.getAbsoluteFile());
    }
    
    @Override
    protected void revertImpl() {
        try {
            if (this.zipArchive != null) {
                this.zipArchive.close();
            }
        }
        catch (final IOException ex) {}
    }
    
    public void saveImpl(final OutputStream outputStream) {
        this.throwExceptionIfReadOnly();
        final ZipArchiveOutputStream zos = (outputStream instanceof ZipArchiveOutputStream) ? outputStream : new ZipArchiveOutputStream(outputStream);
        try {
            if (this.getPartsByRelationshipType("http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties").size() == 0 && this.getPartsByRelationshipType("http://schemas.openxmlformats.org/officedocument/2006/relationships/metadata/core-properties").size() == 0) {
                ZipPackage.LOG.log(1, new Object[] { "Save core properties part" });
                this.getPackageProperties();
                this.addPackagePart(this.packageProperties);
                this.relationships.addRelationship(this.packageProperties.getPartName().getURI(), TargetMode.INTERNAL, "http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties", null);
                if (!this.contentTypeManager.isContentTypeRegister("application/vnd.openxmlformats-package.core-properties+xml")) {
                    this.contentTypeManager.addContentType(this.packageProperties.getPartName(), "application/vnd.openxmlformats-package.core-properties+xml");
                }
            }
            ZipPackage.LOG.log(1, new Object[] { "Save content types part" });
            this.contentTypeManager.save((OutputStream)zos);
            ZipPackage.LOG.log(1, new Object[] { "Save package relationships" });
            ZipPartMarshaller.marshallRelationshipPart(this.getRelationships(), PackagingURIHelper.PACKAGE_RELATIONSHIPS_ROOT_PART_NAME, zos);
            for (final PackagePart part : this.getParts()) {
                if (part.isRelationshipPart()) {
                    continue;
                }
                final PackagePartName ppn = part.getPartName();
                ZipPackage.LOG.log(1, new Object[] { "Save part '" + ZipHelper.getZipItemNameFromOPCName(ppn.getName()) + "'" });
                final PartMarshaller marshaller = this.partMarshallers.get(part._contentType);
                final PartMarshaller pm = (marshaller != null) ? marshaller : this.defaultPartMarshaller;
                if (!pm.marshall(part, (OutputStream)zos)) {
                    final String errMsg = "The part " + ppn.getURI() + " failed to be saved in the stream with marshaller ";
                    throw new OpenXML4JException(errMsg + pm);
                }
            }
            zos.finish();
        }
        catch (final OpenXML4JRuntimeException e) {
            throw e;
        }
        catch (final Exception e2) {
            throw new OpenXML4JRuntimeException("Fail to save: an error occurs while saving the package : " + e2.getMessage(), e2);
        }
    }
    
    public ZipEntrySource getZipArchive() {
        return this.zipArchive;
    }
    
    static {
        LOG = POILogFactory.getLogger((Class)ZipPackage.class);
    }
    
    private class EntryTriple implements Comparable<EntryTriple>
    {
        final ZipArchiveEntry zipArchiveEntry;
        final PackagePartName partName;
        final String contentType;
        
        EntryTriple(final ZipArchiveEntry zipArchiveEntry, final ContentTypeManager contentTypeManager) {
            this.zipArchiveEntry = zipArchiveEntry;
            final String entryName = zipArchiveEntry.getName();
            PackagePartName ppn = null;
            try {
                ppn = ("[Content_Types].xml".equalsIgnoreCase(entryName) ? null : PackagingURIHelper.createPartName(ZipHelper.getOPCNameFromZipItemName(entryName)));
            }
            catch (final Exception e) {
                ZipPackage.LOG.log(5, new Object[] { "Entry " + entryName + " is not valid, so this part won't be add to the package.", e });
            }
            this.partName = ppn;
            this.contentType = ((ppn == null) ? null : contentTypeManager.getContentType(this.partName));
        }
        
        void register(final PackagePartCollection partList) throws InvalidFormatException {
            if (this.contentType == null) {
                throw new InvalidFormatException("The part " + this.partName.getURI().getPath() + " does not have any content type ! Rule: Package require content types when retrieving a part from a package. [M.1.14]");
            }
            if (partList.containsKey(this.partName)) {
                throw new InvalidFormatException("A part with the name '" + this.partName + "' already exist : Packages shall not contain equivalent part names and package implementers shall neither create nor recognize packages with equivalent part names. [M1.12]");
            }
            try {
                partList.put(this.partName, new ZipPackagePart(ZipPackage.this, this.zipArchiveEntry, this.partName, this.contentType, false));
            }
            catch (final InvalidOperationException e) {
                throw new InvalidFormatException(e.getMessage(), e);
            }
        }
        
        @Override
        public int compareTo(final EntryTriple o) {
            final int contentTypeOrder1 = "application/vnd.openxmlformats-package.relationships+xml".equals(this.contentType) ? -1 : 1;
            final int contentTypeOrder2 = "application/vnd.openxmlformats-package.relationships+xml".equals(o.contentType) ? -1 : 1;
            final int cmpCT = Integer.compare(contentTypeOrder1, contentTypeOrder2);
            return (cmpCT != 0) ? cmpCT : this.partName.compareTo(o.partName);
        }
    }
}
