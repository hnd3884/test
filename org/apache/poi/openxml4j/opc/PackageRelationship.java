package org.apache.poi.openxml4j.opc;

import java.net.URISyntaxException;
import java.util.Objects;
import java.net.URI;

public final class PackageRelationship
{
    private static URI containerRelationshipPart;
    public static final String ID_ATTRIBUTE_NAME = "Id";
    public static final String RELATIONSHIPS_TAG_NAME = "Relationships";
    public static final String RELATIONSHIP_TAG_NAME = "Relationship";
    public static final String TARGET_ATTRIBUTE_NAME = "Target";
    public static final String TARGET_MODE_ATTRIBUTE_NAME = "TargetMode";
    public static final String TYPE_ATTRIBUTE_NAME = "Type";
    private String id;
    private OPCPackage container;
    private String relationshipType;
    private PackagePart source;
    private TargetMode targetMode;
    private URI targetUri;
    
    public PackageRelationship(final OPCPackage pkg, final PackagePart sourcePart, final URI targetUri, final TargetMode targetMode, final String relationshipType, final String id) {
        if (pkg == null) {
            throw new IllegalArgumentException("pkg");
        }
        if (targetUri == null) {
            throw new IllegalArgumentException("targetUri");
        }
        if (relationshipType == null) {
            throw new IllegalArgumentException("relationshipType");
        }
        if (id == null) {
            throw new IllegalArgumentException("id");
        }
        this.container = pkg;
        this.source = sourcePart;
        this.targetUri = targetUri;
        this.targetMode = targetMode;
        this.relationshipType = relationshipType;
        this.id = id;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (!(obj instanceof PackageRelationship)) {
            return false;
        }
        final PackageRelationship rel = (PackageRelationship)obj;
        return this.id.equals(rel.id) && this.relationshipType.equals(rel.relationshipType) && (rel.source == null || rel.source.equals(this.source)) && this.targetMode == rel.targetMode && this.targetUri.equals(rel.targetUri);
    }
    
    @Override
    public int hashCode() {
        return Objects.hash(this.id, this.relationshipType, this.source, this.targetMode, this.targetUri);
    }
    
    public static URI getContainerPartRelationship() {
        return PackageRelationship.containerRelationshipPart;
    }
    
    public OPCPackage getPackage() {
        return this.container;
    }
    
    public String getId() {
        return this.id;
    }
    
    public String getRelationshipType() {
        return this.relationshipType;
    }
    
    public PackagePart getSource() {
        return this.source;
    }
    
    public URI getSourceURI() {
        if (this.source == null) {
            return PackagingURIHelper.PACKAGE_ROOT_URI;
        }
        return this.source._partName.getURI();
    }
    
    public TargetMode getTargetMode() {
        return this.targetMode;
    }
    
    public URI getTargetURI() {
        if (this.targetMode == TargetMode.EXTERNAL) {
            return this.targetUri;
        }
        if (!this.targetUri.toASCIIString().startsWith("/")) {
            return PackagingURIHelper.resolvePartUri(this.getSourceURI(), this.targetUri);
        }
        return this.targetUri;
    }
    
    @Override
    public String toString() {
        return ((this.id == null) ? "id=null" : ("id=" + this.id)) + ((this.container == null) ? " - container=null" : (" - container=" + this.container)) + ((this.relationshipType == null) ? " - relationshipType=null" : (" - relationshipType=" + this.relationshipType)) + ((this.source == null) ? " - source=null" : (" - source=" + this.getSourceURI().toASCIIString())) + ((this.targetUri == null) ? " - target=null" : (" - target=" + this.getTargetURI().toASCIIString())) + ((this.targetMode == null) ? ",targetMode=null" : (",targetMode=" + this.targetMode));
    }
    
    static {
        try {
            PackageRelationship.containerRelationshipPart = new URI("/_rels/.rels");
        }
        catch (final URISyntaxException ex) {}
    }
}
