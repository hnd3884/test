package org.apache.poi.ooxml;

import org.apache.xmlbeans.XmlException;
import org.apache.poi.util.Internal;
import org.apache.poi.util.POILogFactory;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePartName;
import java.util.Iterator;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import java.io.InputStream;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.POILogger;

public abstract class POIXMLRelation
{
    private static final POILogger log;
    private String _type;
    private String _relation;
    private String _defaultName;
    private final NoArgConstructor noArgConstructor;
    private final PackagePartConstructor packagePartConstructor;
    private final ParentPartConstructor parentPartConstructor;
    
    protected POIXMLRelation(final String type, final String rel, final String defaultName, final NoArgConstructor noArgConstructor, final PackagePartConstructor packagePartConstructor, final ParentPartConstructor parentPartConstructor) {
        this._type = type;
        this._relation = rel;
        this._defaultName = defaultName;
        this.noArgConstructor = noArgConstructor;
        this.packagePartConstructor = packagePartConstructor;
        this.parentPartConstructor = parentPartConstructor;
    }
    
    protected POIXMLRelation(final String type, final String rel, final String defaultName) {
        this(type, rel, defaultName, null, null, null);
    }
    
    public String getContentType() {
        return this._type;
    }
    
    public String getRelation() {
        return this._relation;
    }
    
    public String getDefaultFileName() {
        return this._defaultName;
    }
    
    public String getFileName(final int index) {
        if (!this._defaultName.contains("#")) {
            return this.getDefaultFileName();
        }
        return this._defaultName.replace("#", Integer.toString(index));
    }
    
    public Integer getFileNameIndex(final POIXMLDocumentPart part) {
        final String regex = this._defaultName.replace("#", "(\\d+)");
        return Integer.valueOf(part.getPackagePart().getPartName().getName().replaceAll(regex, "$1"));
    }
    
    public NoArgConstructor getNoArgConstructor() {
        return this.noArgConstructor;
    }
    
    public PackagePartConstructor getPackagePartConstructor() {
        return this.packagePartConstructor;
    }
    
    public ParentPartConstructor getParentPartConstructor() {
        return this.parentPartConstructor;
    }
    
    public InputStream getContents(final PackagePart corePart) throws IOException, InvalidFormatException {
        final PackageRelationshipCollection prc = corePart.getRelationshipsByType(this.getRelation());
        final Iterator<PackageRelationship> it = prc.iterator();
        if (it.hasNext()) {
            final PackageRelationship rel = it.next();
            final PackagePartName relName = PackagingURIHelper.createPartName(rel.getTargetURI());
            final PackagePart part = corePart.getPackage().getPart(relName);
            return part.getInputStream();
        }
        POIXMLRelation.log.log(5, new Object[] { "No part " + this.getDefaultFileName() + " found" });
        return null;
    }
    
    static {
        log = POILogFactory.getLogger((Class)POIXMLRelation.class);
    }
    
    @Internal
    public interface ParentPartConstructor
    {
        POIXMLDocumentPart init(final POIXMLDocumentPart p0, final PackagePart p1) throws IOException, XmlException;
    }
    
    @Internal
    public interface PackagePartConstructor
    {
        POIXMLDocumentPart init(final PackagePart p0) throws IOException, XmlException;
    }
    
    @Internal
    public interface NoArgConstructor
    {
        POIXMLDocumentPart init();
    }
}
