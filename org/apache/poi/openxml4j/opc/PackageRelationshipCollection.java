package org.apache.poi.openxml4j.opc;

import org.apache.poi.util.POILogFactory;
import java.util.ArrayList;
import org.w3c.dom.Attr;
import org.w3c.dom.NodeList;
import org.w3c.dom.Document;
import java.net.URISyntaxException;
import java.util.Locale;
import org.w3c.dom.Element;
import org.apache.poi.ooxml.util.DocumentHelper;
import java.net.URI;
import org.apache.poi.openxml4j.exceptions.InvalidOperationException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.util.Iterator;
import java.util.HashMap;
import java.util.TreeMap;
import org.apache.poi.util.POILogger;

public final class PackageRelationshipCollection implements Iterable<PackageRelationship>
{
    private static final POILogger logger;
    private final TreeMap<String, PackageRelationship> relationshipsByID;
    private final TreeMap<String, PackageRelationship> relationshipsByType;
    private HashMap<String, PackageRelationship> internalRelationshipsByTargetName;
    private PackagePart relationshipPart;
    private PackagePart sourcePart;
    private PackagePartName partName;
    private OPCPackage container;
    private int nextRelationshipId;
    
    PackageRelationshipCollection() {
        this.relationshipsByID = new TreeMap<String, PackageRelationship>();
        this.relationshipsByType = new TreeMap<String, PackageRelationship>();
        this.internalRelationshipsByTargetName = new HashMap<String, PackageRelationship>();
        this.nextRelationshipId = -1;
    }
    
    public PackageRelationshipCollection(final PackageRelationshipCollection coll, final String filter) {
        this();
        for (final PackageRelationship rel : coll.relationshipsByID.values()) {
            if (filter == null || rel.getRelationshipType().equals(filter)) {
                this.addRelationship(rel);
            }
        }
    }
    
    public PackageRelationshipCollection(final OPCPackage container) throws InvalidFormatException {
        this(container, null);
    }
    
    public PackageRelationshipCollection(final PackagePart part) throws InvalidFormatException {
        this(part._container, part);
    }
    
    public PackageRelationshipCollection(final OPCPackage container, final PackagePart part) throws InvalidFormatException {
        this.relationshipsByID = new TreeMap<String, PackageRelationship>();
        this.relationshipsByType = new TreeMap<String, PackageRelationship>();
        this.internalRelationshipsByTargetName = new HashMap<String, PackageRelationship>();
        this.nextRelationshipId = -1;
        if (container == null) {
            throw new IllegalArgumentException("container needs to be specified");
        }
        if (part != null && part.isRelationshipPart()) {
            throw new IllegalArgumentException("part");
        }
        this.container = container;
        this.sourcePart = part;
        this.partName = getRelationshipPartName(part);
        if (container.getPackageAccess() != PackageAccess.WRITE && container.containPart(this.partName)) {
            this.parseRelationshipsPart(this.relationshipPart = container.getPart(this.partName));
        }
    }
    
    private static PackagePartName getRelationshipPartName(final PackagePart part) throws InvalidOperationException {
        PackagePartName partName;
        if (part == null) {
            partName = PackagingURIHelper.PACKAGE_ROOT_PART_NAME;
        }
        else {
            partName = part.getPartName();
        }
        return PackagingURIHelper.getRelationshipPartName(partName);
    }
    
    public void addRelationship(final PackageRelationship relPart) {
        if (relPart == null || relPart.getId() == null || relPart.getId().isEmpty()) {
            throw new IllegalArgumentException("invalid relationship part/id");
        }
        this.relationshipsByID.put(relPart.getId(), relPart);
        this.relationshipsByType.put(relPart.getRelationshipType(), relPart);
    }
    
    public PackageRelationship addRelationship(final URI targetUri, final TargetMode targetMode, final String relationshipType, String id) {
        if (id == null) {
            if (this.nextRelationshipId == -1) {
                this.nextRelationshipId = this.size() + 1;
            }
            do {
                id = "rId" + this.nextRelationshipId++;
            } while (this.relationshipsByID.get(id) != null);
        }
        final PackageRelationship rel = new PackageRelationship(this.container, this.sourcePart, targetUri, targetMode, relationshipType, id);
        this.addRelationship(rel);
        if (targetMode == TargetMode.INTERNAL) {
            this.internalRelationshipsByTargetName.put(targetUri.toASCIIString(), rel);
        }
        return rel;
    }
    
    public void removeRelationship(final String id) {
        final PackageRelationship rel = this.relationshipsByID.get(id);
        if (rel != null) {
            this.relationshipsByID.remove(rel.getId());
            this.relationshipsByType.values().remove(rel);
            this.internalRelationshipsByTargetName.values().remove(rel);
        }
    }
    
    public PackageRelationship getRelationship(final int index) {
        if (index < 0 || index > this.relationshipsByID.values().size()) {
            throw new IllegalArgumentException("index");
        }
        int i = 0;
        for (final PackageRelationship rel : this.relationshipsByID.values()) {
            if (index == i++) {
                return rel;
            }
        }
        return null;
    }
    
    public PackageRelationship getRelationshipByID(final String id) {
        return this.relationshipsByID.get(id);
    }
    
    public int size() {
        return this.relationshipsByID.values().size();
    }
    
    public void parseRelationshipsPart(final PackagePart relPart) throws InvalidFormatException {
        try {
            PackageRelationshipCollection.logger.log(1, new Object[] { "Parsing relationship: " + relPart.getPartName() });
            final Document xmlRelationshipsDoc = DocumentHelper.readDocument(relPart.getInputStream());
            final Element root = xmlRelationshipsDoc.getDocumentElement();
            boolean fCorePropertiesRelationship = false;
            final NodeList nodeList = root.getElementsByTagNameNS("http://schemas.openxmlformats.org/package/2006/relationships", "Relationship");
            for (int nodeCount = nodeList.getLength(), i = 0; i < nodeCount; ++i) {
                final Element element = (Element)nodeList.item(i);
                final String id = element.getAttribute("Id");
                final String type = element.getAttribute("Type");
                if (type.equals("http://schemas.openxmlformats.org/package/2006/relationships/metadata/core-properties")) {
                    if (fCorePropertiesRelationship) {
                        throw new InvalidFormatException("OPC Compliance error [M4.1]: there is more than one core properties relationship in the package !");
                    }
                    fCorePropertiesRelationship = true;
                }
                final Attr targetModeAttr = element.getAttributeNode("TargetMode");
                TargetMode targetMode = TargetMode.INTERNAL;
                if (targetModeAttr != null) {
                    targetMode = (targetModeAttr.getValue().toLowerCase(Locale.ROOT).equals("internal") ? TargetMode.INTERNAL : TargetMode.EXTERNAL);
                }
                URI target = PackagingURIHelper.toURI("http://invalid.uri");
                final String value = element.getAttribute("Target");
                try {
                    target = PackagingURIHelper.toURI(value);
                }
                catch (final URISyntaxException e) {
                    PackageRelationshipCollection.logger.log(7, new Object[] { "Cannot convert " + value + " in a valid relationship URI-> dummy-URI used", e });
                }
                this.addRelationship(target, targetMode, type, id);
            }
        }
        catch (final Exception e2) {
            PackageRelationshipCollection.logger.log(7, new Object[] { e2 });
            throw new InvalidFormatException(e2.getMessage());
        }
    }
    
    public PackageRelationshipCollection getRelationships(final String typeFilter) {
        return new PackageRelationshipCollection(this, typeFilter);
    }
    
    @Override
    public Iterator<PackageRelationship> iterator() {
        return this.relationshipsByID.values().iterator();
    }
    
    public Iterator<PackageRelationship> iterator(final String typeFilter) {
        final ArrayList<PackageRelationship> retArr = new ArrayList<PackageRelationship>();
        for (final PackageRelationship rel : this.relationshipsByID.values()) {
            if (rel.getRelationshipType().equals(typeFilter)) {
                retArr.add(rel);
            }
        }
        return retArr.iterator();
    }
    
    public void clear() {
        this.relationshipsByID.clear();
        this.relationshipsByType.clear();
        this.internalRelationshipsByTargetName.clear();
    }
    
    public PackageRelationship findExistingInternalRelation(final PackagePart packagePart) {
        return this.internalRelationshipsByTargetName.get(packagePart.getPartName().getName());
    }
    
    @Override
    public String toString() {
        String str = this.relationshipsByID.size() + " relationship(s) = [";
        if (this.relationshipPart != null && this.relationshipPart._partName != null) {
            str += this.relationshipPart._partName;
        }
        else {
            str += "relationshipPart=null";
        }
        if (this.sourcePart != null && this.sourcePart._partName != null) {
            str = str + "," + this.sourcePart._partName;
        }
        else {
            str += ",sourcePart=null";
        }
        if (this.partName != null) {
            str = str + "," + this.partName;
        }
        else {
            str += ",uri=null)";
        }
        return str + "]";
    }
    
    static {
        logger = POILogFactory.getLogger((Class)PackageRelationshipCollection.class);
    }
}
