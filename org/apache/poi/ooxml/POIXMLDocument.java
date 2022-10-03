package org.apache.poi.ooxml;

import java.util.Set;
import java.util.HashSet;
import java.io.OutputStream;
import org.apache.poi.openxml4j.opc.PackageAccess;
import java.util.Map;
import java.util.HashMap;
import org.apache.poi.util.Removal;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.util.List;
import java.util.Iterator;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import java.io.IOException;
import org.apache.xmlbeans.impl.common.SystemCache;
import org.apache.poi.openxml4j.opc.OPCPackage;
import java.io.Closeable;

public abstract class POIXMLDocument extends POIXMLDocumentPart implements Closeable
{
    public static final String DOCUMENT_CREATOR = "Apache POI";
    public static final String OLE_OBJECT_REL_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject";
    public static final String PACK_OBJECT_REL_TYPE = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/package";
    private OPCPackage pkg;
    private POIXMLProperties properties;
    
    protected POIXMLDocument(final OPCPackage pkg) {
        super(pkg);
        this.init(pkg);
    }
    
    protected POIXMLDocument(final OPCPackage pkg, final String coreDocumentRel) {
        super(pkg, coreDocumentRel);
        this.init(pkg);
    }
    
    private void init(final OPCPackage p) {
        this.pkg = p;
        SystemCache.get().setSaxLoader((Object)null);
    }
    
    public static OPCPackage openPackage(final String path) throws IOException {
        try {
            return OPCPackage.open(path);
        }
        catch (final InvalidFormatException e) {
            throw new IOException(e.toString(), e);
        }
    }
    
    public OPCPackage getPackage() {
        return this.pkg;
    }
    
    protected PackagePart getCorePart() {
        return this.getPackagePart();
    }
    
    protected PackagePart[] getRelatedByType(final String contentType) throws InvalidFormatException {
        final PackageRelationshipCollection partsC = this.getPackagePart().getRelationshipsByType(contentType);
        final PackagePart[] parts = new PackagePart[partsC.size()];
        int count = 0;
        for (final PackageRelationship rel : partsC) {
            parts[count] = this.getPackagePart().getRelatedPart(rel);
            ++count;
        }
        return parts;
    }
    
    public POIXMLProperties getProperties() {
        if (this.properties == null) {
            try {
                this.properties = new POIXMLProperties(this.pkg);
            }
            catch (final Exception e) {
                throw new POIXMLException(e);
            }
        }
        return this.properties;
    }
    
    @Deprecated
    @Removal(version = "4.2")
    public List<PackagePart> getAllEmbedds() throws OpenXML4JException {
        return this.getAllEmbeddedParts();
    }
    
    public abstract List<PackagePart> getAllEmbeddedParts() throws OpenXML4JException;
    
    protected final void load(final POIXMLFactory factory) throws IOException {
        final Map<PackagePart, POIXMLDocumentPart> context = new HashMap<PackagePart, POIXMLDocumentPart>();
        try {
            this.read(factory, context);
        }
        catch (final OpenXML4JException e) {
            throw new POIXMLException(e);
        }
        this.onDocumentRead();
        context.clear();
    }
    
    @Override
    public void close() throws IOException {
        if (this.pkg != null) {
            if (this.pkg.getPackageAccess() == PackageAccess.READ) {
                this.pkg.revert();
            }
            else {
                this.pkg.close();
            }
            this.pkg = null;
        }
    }
    
    public final void write(final OutputStream stream) throws IOException {
        final OPCPackage p = this.getPackage();
        if (p == null) {
            throw new IOException("Cannot write data, document seems to have been closed already");
        }
        final Set<PackagePart> context = new HashSet<PackagePart>();
        this.onSave(context);
        context.clear();
        this.getProperties().commit();
        p.save(stream);
    }
}
