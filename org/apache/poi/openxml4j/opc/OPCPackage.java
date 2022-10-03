package org.apache.poi.openxml4j.opc;

import org.apache.poi.util.POILogFactory;
import java.io.FileOutputStream;
import org.apache.poi.util.NotImplemented;
import java.net.URISyntaxException;
import java.net.URI;
import java.io.ByteArrayOutputStream;
import org.apache.poi.openxml4j.exceptions.PartAlreadyExistsException;
import org.apache.poi.openxml4j.opc.internal.unmarshallers.UnmarshallContext;
import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.List;
import java.util.Collections;
import java.util.Iterator;
import java.util.ArrayList;
import java.io.FileInputStream;
import java.util.Optional;
import java.util.Date;
import org.apache.poi.openxml4j.opc.internal.ZipContentTypeManager;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.util.IOUtils;
import org.apache.poi.openxml4j.util.ZipEntrySource;
import java.io.File;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JRuntimeException;
import org.apache.poi.openxml4j.opc.internal.marshallers.ZipPackagePropertiesMarshaller;
import org.apache.poi.openxml4j.opc.internal.unmarshallers.PackagePropertiesUnmarshaller;
import org.apache.poi.openxml4j.opc.internal.marshallers.DefaultMarshaller;
import java.util.HashMap;
import java.io.OutputStream;
import org.apache.poi.openxml4j.opc.internal.ContentTypeManager;
import org.apache.poi.openxml4j.opc.internal.PackagePropertiesPart;
import org.apache.poi.openxml4j.opc.internal.PartUnmarshaller;
import org.apache.poi.openxml4j.opc.internal.PartMarshaller;
import org.apache.poi.openxml4j.opc.internal.ContentType;
import java.util.Map;
import org.apache.poi.util.POILogger;
import java.io.Closeable;

public abstract class OPCPackage implements RelationshipSource, Closeable
{
    private static final POILogger logger;
    protected static final PackageAccess defaultPackageAccess;
    private final PackageAccess packageAccess;
    private PackagePartCollection partList;
    protected PackageRelationshipCollection relationships;
    protected final Map<ContentType, PartMarshaller> partMarshallers;
    protected final PartMarshaller defaultPartMarshaller;
    protected final Map<ContentType, PartUnmarshaller> partUnmarshallers;
    protected PackagePropertiesPart packageProperties;
    protected ContentTypeManager contentTypeManager;
    protected boolean isDirty;
    protected String originalPackagePath;
    protected OutputStream output;
    
    OPCPackage(final PackageAccess access) {
        this.partMarshallers = new HashMap<ContentType, PartMarshaller>(5);
        this.defaultPartMarshaller = new DefaultMarshaller();
        this.partUnmarshallers = new HashMap<ContentType, PartUnmarshaller>(2);
        if (this.getClass() != ZipPackage.class) {
            throw new IllegalArgumentException("PackageBase may not be subclassed");
        }
        this.packageAccess = access;
        final ContentType contentType = newCorePropertiesPart();
        this.partUnmarshallers.put(contentType, new PackagePropertiesUnmarshaller());
        this.partMarshallers.put(contentType, new ZipPackagePropertiesMarshaller());
    }
    
    private static ContentType newCorePropertiesPart() {
        try {
            return new ContentType("application/vnd.openxmlformats-package.core-properties+xml");
        }
        catch (final InvalidFormatException e) {
            throw new OpenXML4JRuntimeException("Package.init() : this exception should never happen, if you read this message please send a mail to the developers team. : " + e.getMessage(), e);
        }
    }
    
    public static OPCPackage open(final String path) throws InvalidFormatException {
        return open(path, OPCPackage.defaultPackageAccess);
    }
    
    public static OPCPackage open(final File file) throws InvalidFormatException {
        return open(file, OPCPackage.defaultPackageAccess);
    }
    
    public static OPCPackage open(final ZipEntrySource zipEntry) throws InvalidFormatException {
        final OPCPackage pack = new ZipPackage(zipEntry, PackageAccess.READ);
        try {
            if (pack.partList == null) {
                pack.getParts();
            }
            return pack;
        }
        catch (final InvalidFormatException | RuntimeException e) {
            IOUtils.closeQuietly((Closeable)pack);
            throw e;
        }
    }
    
    public static OPCPackage open(final String path, final PackageAccess access) throws InvalidFormatException, InvalidOperationException {
        if (path == null || path.trim().isEmpty()) {
            throw new IllegalArgumentException("'path' must be given");
        }
        final File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            throw new IllegalArgumentException("path must not be a directory");
        }
        final OPCPackage pack = new ZipPackage(path, access);
        boolean success = false;
        if (pack.partList == null && access != PackageAccess.WRITE) {
            try {
                pack.getParts();
                success = true;
            }
            finally {
                if (!success) {
                    IOUtils.closeQuietly((Closeable)pack);
                }
            }
        }
        pack.originalPackagePath = new File(path).getAbsolutePath();
        return pack;
    }
    
    public static OPCPackage open(final File file, final PackageAccess access) throws InvalidFormatException {
        if (file == null) {
            throw new IllegalArgumentException("'file' must be given");
        }
        if (file.exists() && file.isDirectory()) {
            throw new IllegalArgumentException("file must not be a directory");
        }
        final OPCPackage pack = new ZipPackage(file, access);
        try {
            if (pack.partList == null && access != PackageAccess.WRITE) {
                pack.getParts();
            }
            pack.originalPackagePath = file.getAbsolutePath();
            return pack;
        }
        catch (final InvalidFormatException | RuntimeException e) {
            IOUtils.closeQuietly((Closeable)pack);
            throw e;
        }
    }
    
    public static OPCPackage open(final InputStream in) throws InvalidFormatException, IOException {
        final OPCPackage pack = new ZipPackage(in, PackageAccess.READ_WRITE);
        try {
            if (pack.partList == null) {
                pack.getParts();
            }
        }
        catch (final InvalidFormatException | RuntimeException e) {
            IOUtils.closeQuietly((Closeable)pack);
            throw e;
        }
        return pack;
    }
    
    public static OPCPackage openOrCreate(final File file) throws InvalidFormatException {
        if (file.exists()) {
            return open(file.getAbsolutePath());
        }
        return create(file);
    }
    
    public static OPCPackage create(final String path) {
        return create(new File(path));
    }
    
    public static OPCPackage create(final File file) {
        if (file == null || (file.exists() && file.isDirectory())) {
            throw new IllegalArgumentException("file");
        }
        if (file.exists()) {
            throw new InvalidOperationException("This package (or file) already exists : use the open() method or delete the file.");
        }
        final OPCPackage pkg = new ZipPackage();
        pkg.originalPackagePath = file.getAbsolutePath();
        configurePackage(pkg);
        return pkg;
    }
    
    public static OPCPackage create(final OutputStream output) {
        final OPCPackage pkg = new ZipPackage();
        pkg.originalPackagePath = null;
        pkg.output = output;
        configurePackage(pkg);
        return pkg;
    }
    
    private static void configurePackage(final OPCPackage pkg) {
        try {
            (pkg.contentTypeManager = new ZipContentTypeManager(null, pkg)).addContentType(PackagingURIHelper.createPartName(PackagingURIHelper.PACKAGE_RELATIONSHIPS_ROOT_URI), "application/vnd.openxmlformats-package.relationships+xml");
            pkg.contentTypeManager.addContentType(PackagingURIHelper.createPartName("/default.xml"), "application/xml");
            (pkg.packageProperties = new PackagePropertiesPart(pkg, PackagingURIHelper.CORE_PROPERTIES_PART_NAME)).setCreatorProperty("Generated by Apache POI OpenXML4J");
            pkg.packageProperties.setCreatedProperty(Optional.of(new Date()));
        }
        catch (final InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
    }
    
    public void flush() {
        this.throwExceptionIfReadOnly();
        if (this.packageProperties != null) {
            this.packageProperties.flush();
        }
        this.flushImpl();
    }
    
    @Override
    public void close() throws IOException {
        if (this.packageAccess == PackageAccess.READ) {
            OPCPackage.logger.log(5, new Object[] { "The close() method is intended to SAVE a package. This package is open in READ ONLY mode, use the revert() method instead !" });
            this.revert();
            return;
        }
        if (this.contentTypeManager == null) {
            OPCPackage.logger.log(5, new Object[] { "Unable to call close() on a package that hasn't been fully opened yet" });
            this.revert();
            return;
        }
        if (this.originalPackagePath != null && !this.originalPackagePath.trim().isEmpty()) {
            final File targetFile = new File(this.originalPackagePath);
            if (!targetFile.exists() || !this.originalPackagePath.equalsIgnoreCase(targetFile.getAbsolutePath())) {
                this.save(targetFile);
            }
            else {
                this.closeImpl();
            }
        }
        else if (this.output != null) {
            this.save(this.output);
            this.output.close();
        }
        this.revert();
        this.contentTypeManager.clearAll();
    }
    
    public void revert() {
        this.revertImpl();
    }
    
    public void addThumbnail(final String path) throws IOException {
        if (path == null || path.isEmpty()) {
            throw new IllegalArgumentException("path");
        }
        final String name = path.substring(path.lastIndexOf(File.separatorChar) + 1);
        try (final FileInputStream is = new FileInputStream(path)) {
            this.addThumbnail(name, is);
        }
    }
    
    public void addThumbnail(final String filename, final InputStream data) throws IOException {
        if (filename == null || filename.isEmpty()) {
            throw new IllegalArgumentException("filename");
        }
        final String contentType = ContentTypes.getContentTypeFromFileExtension(filename);
        PackagePartName thumbnailPartName;
        try {
            thumbnailPartName = PackagingURIHelper.createPartName("/docProps/" + filename);
        }
        catch (final InvalidFormatException e) {
            final String partName = "/docProps/thumbnail" + filename.substring(filename.lastIndexOf(".") + 1);
            try {
                thumbnailPartName = PackagingURIHelper.createPartName(partName);
            }
            catch (final InvalidFormatException e2) {
                throw new InvalidOperationException("Can't add a thumbnail file named '" + filename + "'", e2);
            }
        }
        if (this.getPart(thumbnailPartName) != null) {
            throw new InvalidOperationException("You already add a thumbnail named '" + filename + "'");
        }
        final PackagePart thumbnailPart = this.createPart(thumbnailPartName, contentType, false);
        this.addRelationship(thumbnailPartName, TargetMode.INTERNAL, "http://schemas.openxmlformats.org/package/2006/relationships/metadata/thumbnail");
        StreamHelper.copyStream(data, thumbnailPart.getOutputStream());
    }
    
    void throwExceptionIfReadOnly() throws InvalidOperationException {
        if (this.packageAccess == PackageAccess.READ) {
            throw new InvalidOperationException("Operation not allowed, document open in read only mode!");
        }
    }
    
    void throwExceptionIfWriteOnly() throws InvalidOperationException {
        if (this.packageAccess == PackageAccess.WRITE) {
            throw new InvalidOperationException("Operation not allowed, document open in write only mode!");
        }
    }
    
    public PackageProperties getPackageProperties() throws InvalidFormatException {
        this.throwExceptionIfWriteOnly();
        if (this.packageProperties == null) {
            this.packageProperties = new PackagePropertiesPart(this, PackagingURIHelper.CORE_PROPERTIES_PART_NAME);
        }
        return this.packageProperties;
    }
    
    public PackagePart getPart(final PackagePartName partName) {
        this.throwExceptionIfWriteOnly();
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if (this.partList == null) {
            try {
                this.getParts();
            }
            catch (final InvalidFormatException e) {
                return null;
            }
        }
        return this.partList.get(partName);
    }
    
    public ArrayList<PackagePart> getPartsByContentType(final String contentType) {
        final ArrayList<PackagePart> retArr = new ArrayList<PackagePart>();
        for (final PackagePart part : this.partList.sortedValues()) {
            if (part.getContentType().equals(contentType)) {
                retArr.add(part);
            }
        }
        return retArr;
    }
    
    public ArrayList<PackagePart> getPartsByRelationshipType(final String relationshipType) {
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        final ArrayList<PackagePart> retArr = new ArrayList<PackagePart>();
        for (final PackageRelationship rel : this.getRelationshipsByType(relationshipType)) {
            final PackagePart part = this.getPart(rel);
            if (part != null) {
                retArr.add(part);
            }
        }
        Collections.sort(retArr);
        return retArr;
    }
    
    public List<PackagePart> getPartsByName(final Pattern namePattern) {
        if (namePattern == null) {
            throw new IllegalArgumentException("name pattern must not be null");
        }
        final Matcher matcher = namePattern.matcher("");
        final ArrayList<PackagePart> result = new ArrayList<PackagePart>();
        for (final PackagePart part : this.partList.sortedValues()) {
            final PackagePartName partName = part.getPartName();
            if (matcher.reset(partName.getName()).matches()) {
                result.add(part);
            }
        }
        return result;
    }
    
    public PackagePart getPart(final PackageRelationship partRel) {
        PackagePart retPart = null;
        this.ensureRelationships();
        for (final PackageRelationship rel : this.relationships) {
            if (rel.getRelationshipType().equals(partRel.getRelationshipType())) {
                try {
                    retPart = this.getPart(PackagingURIHelper.createPartName(rel.getTargetURI()));
                    break;
                }
                catch (final InvalidFormatException e) {}
            }
        }
        return retPart;
    }
    
    public ArrayList<PackagePart> getParts() throws InvalidFormatException {
        this.throwExceptionIfWriteOnly();
        if (this.partList == null) {
            boolean hasCorePropertiesPart = false;
            boolean needCorePropertiesPart = true;
            this.partList = this.getPartsImpl();
            for (final PackagePart part : new ArrayList(this.partList.sortedValues())) {
                part.loadRelationships();
                if ("application/vnd.openxmlformats-package.core-properties+xml".equals(part.getContentType())) {
                    if (!hasCorePropertiesPart) {
                        hasCorePropertiesPart = true;
                    }
                    else {
                        OPCPackage.logger.log(5, new Object[] { "OPC Compliance error [M4.1]: there is more than one core properties relationship in the package! POI will use only the first, but other software may reject this file." });
                    }
                }
                final PartUnmarshaller partUnmarshaller = this.partUnmarshallers.get(part._contentType);
                if (partUnmarshaller != null) {
                    final UnmarshallContext context = new UnmarshallContext(this, part._partName);
                    try {
                        final PackagePart unmarshallPart = partUnmarshaller.unmarshall(context, part.getInputStream());
                        this.partList.remove(part.getPartName());
                        this.partList.put(unmarshallPart._partName, unmarshallPart);
                        if (!(unmarshallPart instanceof PackagePropertiesPart) || !hasCorePropertiesPart || !needCorePropertiesPart) {
                            continue;
                        }
                        this.packageProperties = (PackagePropertiesPart)unmarshallPart;
                        needCorePropertiesPart = false;
                    }
                    catch (final IOException ioe) {
                        OPCPackage.logger.log(5, new Object[] { "Unmarshall operation : IOException for " + part._partName });
                    }
                    catch (final InvalidOperationException invoe) {
                        throw new InvalidFormatException(invoe.getMessage(), invoe);
                    }
                }
            }
        }
        return new ArrayList<PackagePart>(this.partList.sortedValues());
    }
    
    public PackagePart createPart(final PackagePartName partName, final String contentType) {
        return this.createPart(partName, contentType, true);
    }
    
    PackagePart createPart(final PackagePartName partName, final String contentType, final boolean loadRelationships) {
        this.throwExceptionIfReadOnly();
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        if (contentType == null || contentType.isEmpty()) {
            throw new IllegalArgumentException("contentType");
        }
        if (this.partList.containsKey(partName) && !this.partList.get(partName).isDeleted()) {
            throw new PartAlreadyExistsException("A part with the name '" + partName.getName() + "' already exists : Packages shall not contain equivalent part names and package implementers shall neither create nor recognize packages with equivalent part names. [M1.12]");
        }
        if (contentType.equals("application/vnd.openxmlformats-package.core-properties+xml") && this.packageProperties != null) {
            throw new InvalidOperationException("OPC Compliance error [M4.1]: you try to add more than one core properties relationship in the package !");
        }
        final PackagePart part = this.createPartImpl(partName, contentType, loadRelationships);
        try {
            PackagePartName ppn = PackagingURIHelper.createPartName("/.xml");
            this.contentTypeManager.addContentType(ppn, "application/xml");
            ppn = PackagingURIHelper.createPartName("/.rels");
            this.contentTypeManager.addContentType(ppn, "application/vnd.openxmlformats-package.relationships+xml");
        }
        catch (final InvalidFormatException e) {
            throw new InvalidOperationException("unable to create default content-type entries.", e);
        }
        this.contentTypeManager.addContentType(partName, contentType);
        this.partList.put(partName, part);
        this.isDirty = true;
        return part;
    }
    
    public PackagePart createPart(final PackagePartName partName, final String contentType, final ByteArrayOutputStream content) {
        final PackagePart addedPart = this.createPart(partName, contentType);
        if (addedPart == null) {
            return null;
        }
        if (content != null) {
            try {
                final OutputStream partOutput = addedPart.getOutputStream();
                if (partOutput == null) {
                    return null;
                }
                partOutput.write(content.toByteArray(), 0, content.size());
                partOutput.close();
                return addedPart;
            }
            catch (final IOException ioe) {
                return null;
            }
            return null;
        }
        return null;
    }
    
    protected PackagePart addPackagePart(final PackagePart part) {
        this.throwExceptionIfReadOnly();
        if (part == null) {
            throw new IllegalArgumentException("part");
        }
        if (this.partList.containsKey(part._partName)) {
            if (!this.partList.get(part._partName).isDeleted()) {
                throw new InvalidOperationException("A part with the name '" + part._partName.getName() + "' already exists : Packages shall not contain equivalent part names and package implementers shall neither create nor recognize packages with equivalent part names. [M1.12]");
            }
            part.setDeleted(false);
            this.partList.remove(part._partName);
        }
        this.partList.put(part._partName, part);
        this.isDirty = true;
        return part;
    }
    
    public void removePart(final PackagePart part) {
        if (part != null) {
            this.removePart(part.getPartName());
        }
    }
    
    public void removePart(final PackagePartName partName) {
        this.throwExceptionIfReadOnly();
        if (partName == null || !this.containPart(partName)) {
            throw new IllegalArgumentException("partName");
        }
        if (this.partList.containsKey(partName)) {
            this.partList.get(partName).setDeleted(true);
            this.removePartImpl(partName);
            this.partList.remove(partName);
        }
        else {
            this.removePartImpl(partName);
        }
        this.contentTypeManager.removeContentType(partName);
        if (partName.isRelationshipPartURI()) {
            final URI sourceURI = PackagingURIHelper.getSourcePartUriFromRelationshipPartUri(partName.getURI());
            PackagePartName sourcePartName;
            try {
                sourcePartName = PackagingURIHelper.createPartName(sourceURI);
            }
            catch (final InvalidFormatException e) {
                OPCPackage.logger.log(7, new Object[] { "Part name URI '" + sourceURI + "' is not valid ! This message is not intended to be displayed !" });
                return;
            }
            if (sourcePartName.getURI().equals(PackagingURIHelper.PACKAGE_ROOT_URI)) {
                this.clearRelationships();
            }
            else if (this.containPart(sourcePartName)) {
                final PackagePart part = this.getPart(sourcePartName);
                if (part != null) {
                    part.clearRelationships();
                }
            }
        }
        this.isDirty = true;
    }
    
    public void removePartRecursive(final PackagePartName partName) throws InvalidFormatException {
        final PackagePart relPart = this.partList.get(PackagingURIHelper.getRelationshipPartName(partName));
        final PackagePart partToRemove = this.partList.get(partName);
        if (relPart != null) {
            final PackageRelationshipCollection partRels = new PackageRelationshipCollection(partToRemove);
            for (final PackageRelationship rel : partRels) {
                final PackagePartName partNameToRemove = PackagingURIHelper.createPartName(PackagingURIHelper.resolvePartUri(rel.getSourceURI(), rel.getTargetURI()));
                this.removePart(partNameToRemove);
            }
            this.removePart(relPart._partName);
        }
        this.removePart(partToRemove._partName);
    }
    
    public void deletePart(final PackagePartName partName) {
        if (partName == null) {
            throw new IllegalArgumentException("partName");
        }
        this.removePart(partName);
        this.removePart(PackagingURIHelper.getRelationshipPartName(partName));
    }
    
    public void deletePartRecursive(final PackagePartName partName) {
        if (partName == null || !this.containPart(partName)) {
            throw new IllegalArgumentException("partName");
        }
        final PackagePart partToDelete = this.getPart(partName);
        this.removePart(partName);
        try {
            for (final PackageRelationship relationship : partToDelete.getRelationships()) {
                final PackagePartName targetPartName = PackagingURIHelper.createPartName(PackagingURIHelper.resolvePartUri(partName.getURI(), relationship.getTargetURI()));
                this.deletePartRecursive(targetPartName);
            }
        }
        catch (final InvalidFormatException e) {
            OPCPackage.logger.log(5, new Object[] { "An exception occurs while deleting part '" + partName.getName() + "'. Some parts may remain in the package. - " + e.getMessage() });
            return;
        }
        final PackagePartName relationshipPartName = PackagingURIHelper.getRelationshipPartName(partName);
        if (relationshipPartName != null && this.containPart(relationshipPartName)) {
            this.removePart(relationshipPartName);
        }
    }
    
    public boolean containPart(final PackagePartName partName) {
        return this.getPart(partName) != null;
    }
    
    @Override
    public PackageRelationship addRelationship(final PackagePartName targetPartName, final TargetMode targetMode, final String relationshipType, final String relID) {
        if (relationshipType.equals("http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties") && this.packageProperties != null) {
            throw new InvalidOperationException("OPC Compliance error [M4.1]: can't add another core properties part ! Use the built-in package method instead.");
        }
        if (targetPartName.isRelationshipPartURI()) {
            throw new InvalidOperationException("Rule M1.25: The Relationships part shall not have relationships to any other part.");
        }
        this.ensureRelationships();
        final PackageRelationship retRel = this.relationships.addRelationship(targetPartName.getURI(), targetMode, relationshipType, relID);
        this.isDirty = true;
        return retRel;
    }
    
    @Override
    public PackageRelationship addRelationship(final PackagePartName targetPartName, final TargetMode targetMode, final String relationshipType) {
        return this.addRelationship(targetPartName, targetMode, relationshipType, null);
    }
    
    @Override
    public PackageRelationship addExternalRelationship(final String target, final String relationshipType) {
        return this.addExternalRelationship(target, relationshipType, null);
    }
    
    @Override
    public PackageRelationship addExternalRelationship(final String target, final String relationshipType, final String id) {
        if (target == null) {
            throw new IllegalArgumentException("target");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        URI targetURI;
        try {
            targetURI = new URI(target);
        }
        catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Invalid target - " + e);
        }
        this.ensureRelationships();
        final PackageRelationship retRel = this.relationships.addRelationship(targetURI, TargetMode.EXTERNAL, relationshipType, id);
        this.isDirty = true;
        return retRel;
    }
    
    @Override
    public void removeRelationship(final String id) {
        if (this.relationships != null) {
            this.relationships.removeRelationship(id);
            this.isDirty = true;
        }
    }
    
    @Override
    public PackageRelationshipCollection getRelationships() {
        return this.getRelationshipsHelper(null);
    }
    
    @Override
    public PackageRelationshipCollection getRelationshipsByType(final String relationshipType) {
        this.throwExceptionIfWriteOnly();
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        return this.getRelationshipsHelper(relationshipType);
    }
    
    private PackageRelationshipCollection getRelationshipsHelper(final String id) {
        this.throwExceptionIfWriteOnly();
        this.ensureRelationships();
        return this.relationships.getRelationships(id);
    }
    
    @Override
    public void clearRelationships() {
        if (this.relationships != null) {
            this.relationships.clear();
            this.isDirty = true;
        }
    }
    
    public void ensureRelationships() {
        if (this.relationships == null) {
            try {
                this.relationships = new PackageRelationshipCollection(this);
            }
            catch (final InvalidFormatException e) {
                this.relationships = new PackageRelationshipCollection();
            }
        }
    }
    
    @Override
    public PackageRelationship getRelationship(final String id) {
        return this.relationships.getRelationshipByID(id);
    }
    
    @Override
    public boolean hasRelationships() {
        return this.relationships.size() > 0;
    }
    
    @Override
    public boolean isRelationshipExists(final PackageRelationship rel) {
        for (final PackageRelationship r : this.relationships) {
            if (r == rel) {
                return true;
            }
        }
        return false;
    }
    
    public void addMarshaller(final String contentType, final PartMarshaller marshaller) {
        try {
            this.partMarshallers.put(new ContentType(contentType), marshaller);
        }
        catch (final InvalidFormatException e) {
            OPCPackage.logger.log(5, new Object[] { "The specified content type is not valid: '" + e.getMessage() + "'. The marshaller will not be added !" });
        }
    }
    
    public void addUnmarshaller(final String contentType, final PartUnmarshaller unmarshaller) {
        try {
            this.partUnmarshallers.put(new ContentType(contentType), unmarshaller);
        }
        catch (final InvalidFormatException e) {
            OPCPackage.logger.log(5, new Object[] { "The specified content type is not valid: '" + e.getMessage() + "'. The unmarshaller will not be added !" });
        }
    }
    
    public void removeMarshaller(final String contentType) {
        try {
            this.partMarshallers.remove(new ContentType(contentType));
        }
        catch (final InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }
    
    public void removeUnmarshaller(final String contentType) {
        try {
            this.partUnmarshallers.remove(new ContentType(contentType));
        }
        catch (final InvalidFormatException e) {
            throw new RuntimeException(e);
        }
    }
    
    public PackageAccess getPackageAccess() {
        return this.packageAccess;
    }
    
    @NotImplemented
    public boolean validatePackage(final OPCPackage pkg) throws InvalidFormatException {
        throw new InvalidOperationException("Not implemented yet !!!");
    }
    
    public void save(final File targetFile) throws IOException {
        if (targetFile == null) {
            throw new IllegalArgumentException("targetFile");
        }
        this.throwExceptionIfReadOnly();
        if (targetFile.exists() && targetFile.getAbsolutePath().equals(this.originalPackagePath)) {
            throw new InvalidOperationException("You can't call save(File) to save to the currently open file. To save to the current file, please just call close()");
        }
        try (final FileOutputStream fos = new FileOutputStream(targetFile)) {
            this.save(fos);
        }
    }
    
    public void save(final OutputStream outputStream) throws IOException {
        this.throwExceptionIfReadOnly();
        this.saveImpl(outputStream);
    }
    
    protected abstract PackagePart createPartImpl(final PackagePartName p0, final String p1, final boolean p2);
    
    protected abstract void removePartImpl(final PackagePartName p0);
    
    protected abstract void flushImpl();
    
    protected abstract void closeImpl() throws IOException;
    
    protected abstract void revertImpl();
    
    protected abstract void saveImpl(final OutputStream p0) throws IOException;
    
    protected abstract PackagePartCollection getPartsImpl() throws InvalidFormatException;
    
    public boolean replaceContentType(final String oldContentType, final String newContentType) {
        boolean success = false;
        final ArrayList<PackagePart> list = this.getPartsByContentType(oldContentType);
        for (final PackagePart packagePart : list) {
            if (packagePart.getContentType().equals(oldContentType)) {
                final PackagePartName partName = packagePart.getPartName();
                this.contentTypeManager.addContentType(partName, newContentType);
                try {
                    packagePart.setContentType(newContentType);
                }
                catch (final InvalidFormatException e) {
                    throw new OpenXML4JRuntimeException("invalid content type - " + newContentType, e);
                }
                success = true;
                this.isDirty = true;
            }
        }
        return success;
    }
    
    public void registerPartAndContentType(final PackagePart part) {
        this.addPackagePart(part);
        this.contentTypeManager.addContentType(part.getPartName(), part.getContentType());
        this.isDirty = true;
    }
    
    public void unregisterPartAndContentType(final PackagePartName partName) {
        this.removePart(partName);
        this.contentTypeManager.removeContentType(partName);
        this.isDirty = true;
    }
    
    public int getUnusedPartIndex(final String nameTemplate) throws InvalidFormatException {
        return this.partList.getUnusedPartIndex(nameTemplate);
    }
    
    static {
        logger = POILogFactory.getLogger((Class)OPCPackage.class);
        defaultPackageAccess = PackageAccess.READ_WRITE;
    }
}
