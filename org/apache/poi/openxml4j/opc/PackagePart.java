package org.apache.poi.openxml4j.opc;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.io.OutputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.internal.ContentType;

public abstract class PackagePart implements RelationshipSource, Comparable<PackagePart>
{
    protected OPCPackage _container;
    protected PackagePartName _partName;
    protected ContentType _contentType;
    private boolean _isRelationshipPart;
    private boolean _isDeleted;
    private PackageRelationshipCollection _relationships;
    
    protected PackagePart(final OPCPackage pack, final PackagePartName partName, final ContentType contentType) throws InvalidFormatException {
        this(pack, partName, contentType, true);
    }
    
    protected PackagePart(final OPCPackage pack, final PackagePartName partName, final ContentType contentType, final boolean loadRelationships) throws InvalidFormatException {
        this._partName = partName;
        this._contentType = contentType;
        this._container = pack;
        this._isRelationshipPart = this._partName.isRelationshipPartURI();
        if (loadRelationships) {
            this.loadRelationships();
        }
    }
    
    public PackagePart(final OPCPackage pack, final PackagePartName partName, final String contentType) throws InvalidFormatException {
        this(pack, partName, new ContentType(contentType));
    }
    
    public PackageRelationship findExistingRelation(final PackagePart packagePart) {
        return this._relationships.findExistingInternalRelation(packagePart);
    }
    
    @Override
    public PackageRelationship addExternalRelationship(final String target, final String relationshipType) {
        return this.addExternalRelationship(target, relationshipType, null);
    }
    
    @Override
    public PackageRelationship addExternalRelationship(final String target, final String relationshipType, final String id) {
        if (target == null) {
            throw new IllegalArgumentException("target is null for type " + relationshipType);
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        if (this._relationships == null) {
            this._relationships = new PackageRelationshipCollection();
        }
        URI targetURI;
        try {
            targetURI = new URI(target);
        }
        catch (final URISyntaxException e) {
            throw new IllegalArgumentException("Invalid target - " + e);
        }
        return this._relationships.addRelationship(targetURI, TargetMode.EXTERNAL, relationshipType, id);
    }
    
    @Override
    public PackageRelationship addRelationship(final PackagePartName targetPartName, final TargetMode targetMode, final String relationshipType) {
        return this.addRelationship(targetPartName, targetMode, relationshipType, null);
    }
    
    @Override
    public PackageRelationship addRelationship(final PackagePartName targetPartName, final TargetMode targetMode, final String relationshipType, final String id) {
        this._container.throwExceptionIfReadOnly();
        if (targetPartName == null) {
            throw new IllegalArgumentException("targetPartName");
        }
        if (targetMode == null) {
            throw new IllegalArgumentException("targetMode");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        if (this._isRelationshipPart || targetPartName.isRelationshipPartURI()) {
            throw new InvalidOperationException("Rule M1.25: The Relationships part shall not have relationships to any other part.");
        }
        if (this._relationships == null) {
            this._relationships = new PackageRelationshipCollection();
        }
        return this._relationships.addRelationship(targetPartName.getURI(), targetMode, relationshipType, id);
    }
    
    public PackageRelationship addRelationship(final URI targetURI, final TargetMode targetMode, final String relationshipType) {
        return this.addRelationship(targetURI, targetMode, relationshipType, null);
    }
    
    public PackageRelationship addRelationship(final URI targetURI, final TargetMode targetMode, final String relationshipType, final String id) {
        this._container.throwExceptionIfReadOnly();
        if (targetURI == null) {
            throw new IllegalArgumentException("targetPartName");
        }
        if (targetMode == null) {
            throw new IllegalArgumentException("targetMode");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        if (this._isRelationshipPart || PackagingURIHelper.isRelationshipPartURI(targetURI)) {
            throw new InvalidOperationException("Rule M1.25: The Relationships part shall not have relationships to any other part.");
        }
        if (this._relationships == null) {
            this._relationships = new PackageRelationshipCollection();
        }
        return this._relationships.addRelationship(targetURI, targetMode, relationshipType, id);
    }
    
    @Override
    public void clearRelationships() {
        if (this._relationships != null) {
            this._relationships.clear();
        }
    }
    
    @Override
    public void removeRelationship(final String id) {
        this._container.throwExceptionIfReadOnly();
        if (this._relationships != null) {
            this._relationships.removeRelationship(id);
        }
    }
    
    @Override
    public PackageRelationshipCollection getRelationships() throws InvalidFormatException {
        return this.getRelationshipsCore(null);
    }
    
    @Override
    public PackageRelationship getRelationship(final String id) {
        return this._relationships.getRelationshipByID(id);
    }
    
    @Override
    public PackageRelationshipCollection getRelationshipsByType(final String relationshipType) throws InvalidFormatException {
        this._container.throwExceptionIfWriteOnly();
        return this.getRelationshipsCore(relationshipType);
    }
    
    private PackageRelationshipCollection getRelationshipsCore(final String filter) throws InvalidFormatException {
        this._container.throwExceptionIfWriteOnly();
        if (this._relationships == null) {
            this.throwExceptionIfRelationship();
            this._relationships = new PackageRelationshipCollection(this);
        }
        return new PackageRelationshipCollection(this._relationships, filter);
    }
    
    @Override
    public boolean hasRelationships() {
        return !this._isRelationshipPart && this._relationships != null && this._relationships.size() > 0;
    }
    
    @Override
    public boolean isRelationshipExists(final PackageRelationship rel) {
        return rel != null && this._relationships.getRelationshipByID(rel.getId()) != null;
    }
    
    public PackagePart getRelatedPart(final PackageRelationship rel) throws InvalidFormatException {
        if (!this.isRelationshipExists(rel)) {
            throw new IllegalArgumentException("Relationship " + rel + " doesn't start with this part " + this._partName);
        }
        URI target = rel.getTargetURI();
        if (target.getFragment() != null) {
            final String t = target.toString();
            try {
                target = new URI(t.substring(0, t.indexOf(35)));
            }
            catch (final URISyntaxException e) {
                throw new InvalidFormatException("Invalid target URI: " + target);
            }
        }
        final PackagePartName relName = PackagingURIHelper.createPartName(target);
        final PackagePart part = this._container.getPart(relName);
        if (part == null) {
            throw new IllegalArgumentException("No part found for relationship " + rel);
        }
        return part;
    }
    
    public InputStream getInputStream() throws IOException {
        final InputStream inStream = this.getInputStreamImpl();
        if (inStream == null) {
            throw new IOException("Can't obtain the input stream from " + this._partName.getName());
        }
        return inStream;
    }
    
    public OutputStream getOutputStream() {
        OutputStream outStream;
        if (this instanceof ZipPackagePart) {
            this._container.removePart(this._partName);
            final PackagePart part = this._container.createPart(this._partName, this._contentType.toString(), false);
            if (part == null) {
                throw new InvalidOperationException("Can't create a temporary part !");
            }
            part._relationships = this._relationships;
            outStream = part.getOutputStreamImpl();
        }
        else {
            outStream = this.getOutputStreamImpl();
        }
        return outStream;
    }
    
    private void throwExceptionIfRelationship() throws InvalidOperationException {
        if (this._isRelationshipPart) {
            throw new InvalidOperationException("Can do this operation on a relationship part !");
        }
    }
    
    void loadRelationships() throws InvalidFormatException {
        if (this._relationships == null && !this._isRelationshipPart) {
            this.throwExceptionIfRelationship();
            this._relationships = new PackageRelationshipCollection(this);
        }
    }
    
    public PackagePartName getPartName() {
        return this._partName;
    }
    
    public String getContentType() {
        return this._contentType.toString();
    }
    
    public ContentType getContentTypeDetails() {
        return this._contentType;
    }
    
    public void setContentType(final String contentType) throws InvalidFormatException {
        if (this._container == null) {
            this._contentType = new ContentType(contentType);
        }
        else {
            this._container.unregisterPartAndContentType(this._partName);
            this._contentType = new ContentType(contentType);
            this._container.registerPartAndContentType(this);
        }
    }
    
    public OPCPackage getPackage() {
        return this._container;
    }
    
    public boolean isRelationshipPart() {
        return this._isRelationshipPart;
    }
    
    public boolean isDeleted() {
        return this._isDeleted;
    }
    
    public void setDeleted(final boolean isDeleted) {
        this._isDeleted = isDeleted;
    }
    
    public long getSize() {
        return -1L;
    }
    
    @Override
    public String toString() {
        return "Name: " + this._partName + " - Content Type: " + this._contentType;
    }
    
    @Override
    public int compareTo(final PackagePart other) {
        if (other == null) {
            return -1;
        }
        return PackagePartName.compare(this._partName, other._partName);
    }
    
    protected abstract InputStream getInputStreamImpl() throws IOException;
    
    protected abstract OutputStream getOutputStreamImpl();
    
    public abstract boolean save(final OutputStream p0) throws OpenXML4JException;
    
    public abstract boolean load(final InputStream p0) throws InvalidFormatException;
    
    public abstract void close();
    
    public abstract void flush();
    
    public void clear() {
    }
}
