package org.apache.poi.ooxml;

import org.apache.poi.util.POILogFactory;
import java.io.Closeable;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.Internal;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.net.URI;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xddf.usermodel.chart.XDDFChart;
import org.apache.poi.xwpf.usermodel.XWPFRelation;
import org.apache.poi.openxml4j.exceptions.PartAlreadyExistsException;
import org.apache.poi.openxml4j.opc.PackagingURIHelper;
import java.util.Set;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePartName;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import org.apache.poi.openxml4j.opc.TargetMode;
import java.util.Collection;
import java.util.Iterator;
import java.util.Collections;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import java.util.LinkedHashMap;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.util.Removal;
import java.util.Map;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.util.POILogger;

public class POIXMLDocumentPart
{
    private static final POILogger logger;
    private String coreDocumentRel;
    private PackagePart packagePart;
    private POIXMLDocumentPart parent;
    private Map<String, RelationPart> relations;
    private boolean isCommitted;
    private int relationCounter;
    
    @Removal(version = "5.0.0")
    @Deprecated
    public boolean isCommited() {
        return this.isCommitted();
    }
    
    public boolean isCommitted() {
        return this.isCommitted;
    }
    
    @Removal(version = "5.0.0")
    @Deprecated
    public void setCommited(final boolean isCommitted) {
        this.isCommitted = isCommitted;
    }
    
    public void setCommitted(final boolean isCommitted) {
        this.isCommitted = isCommitted;
    }
    
    int incrementRelationCounter() {
        return ++this.relationCounter;
    }
    
    int decrementRelationCounter() {
        return --this.relationCounter;
    }
    
    int getRelationCounter() {
        return this.relationCounter;
    }
    
    public POIXMLDocumentPart(final OPCPackage pkg) {
        this(pkg, "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument");
    }
    
    public POIXMLDocumentPart(final OPCPackage pkg, final String coreDocumentRel) {
        this(getPartFromOPCPackage(pkg, coreDocumentRel));
        this.coreDocumentRel = coreDocumentRel;
    }
    
    public POIXMLDocumentPart() {
        this.coreDocumentRel = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument";
        this.relations = new LinkedHashMap<String, RelationPart>();
        this.isCommitted = false;
    }
    
    public POIXMLDocumentPart(final PackagePart part) {
        this(null, part);
    }
    
    public POIXMLDocumentPart(final POIXMLDocumentPart parent, final PackagePart part) {
        this.coreDocumentRel = "http://schemas.openxmlformats.org/officeDocument/2006/relationships/officeDocument";
        this.relations = new LinkedHashMap<String, RelationPart>();
        this.isCommitted = false;
        this.packagePart = part;
        this.parent = parent;
    }
    
    protected final void rebase(final OPCPackage pkg) throws InvalidFormatException {
        final PackageRelationshipCollection cores = this.packagePart.getRelationshipsByType(this.coreDocumentRel);
        if (cores.size() != 1) {
            throw new IllegalStateException("Tried to rebase using " + this.coreDocumentRel + " but found " + cores.size() + " parts of the right type");
        }
        this.packagePart = this.packagePart.getRelatedPart(cores.getRelationship(0));
    }
    
    public final PackagePart getPackagePart() {
        return this.packagePart;
    }
    
    public final List<POIXMLDocumentPart> getRelations() {
        final List<POIXMLDocumentPart> l = new ArrayList<POIXMLDocumentPart>();
        for (final RelationPart rp : this.relations.values()) {
            l.add(rp.getDocumentPart());
        }
        return Collections.unmodifiableList((List<? extends POIXMLDocumentPart>)l);
    }
    
    public final List<RelationPart> getRelationParts() {
        final List<RelationPart> l = new ArrayList<RelationPart>(this.relations.values());
        return Collections.unmodifiableList((List<? extends RelationPart>)l);
    }
    
    public final POIXMLDocumentPart getRelationById(final String id) {
        final RelationPart rp = this.getRelationPartById(id);
        return (rp == null) ? null : rp.getDocumentPart();
    }
    
    public final RelationPart getRelationPartById(final String id) {
        return this.relations.get(id);
    }
    
    public final String getRelationId(final POIXMLDocumentPart part) {
        for (final RelationPart rp : this.relations.values()) {
            if (rp.getDocumentPart() == part) {
                return rp.getRelationship().getId();
            }
        }
        return null;
    }
    
    public final RelationPart addRelation(final String relId, final POIXMLRelation relationshipType, final POIXMLDocumentPart part) {
        PackageRelationship pr = this.packagePart.findExistingRelation(part.getPackagePart());
        if (pr == null) {
            final PackagePartName ppn = part.getPackagePart().getPartName();
            final String relType = relationshipType.getRelation();
            pr = this.packagePart.addRelationship(ppn, TargetMode.INTERNAL, relType, relId);
        }
        this.addRelation(pr, part);
        return new RelationPart(pr, part);
    }
    
    private void addRelation(final PackageRelationship pr, final POIXMLDocumentPart part) {
        this.relations.put(pr.getId(), new RelationPart(pr, part));
        part.incrementRelationCounter();
    }
    
    protected final void removeRelation(final POIXMLDocumentPart part) {
        this.removeRelation(part, true);
    }
    
    protected final boolean removeRelation(final POIXMLDocumentPart part, final boolean removeUnusedParts) {
        final String id = this.getRelationId(part);
        return this.removeRelation(id, removeUnusedParts);
    }
    
    protected final void removeRelation(final String partId) {
        this.removeRelation(partId, true);
    }
    
    private final boolean removeRelation(final String partId, final boolean removeUnusedParts) {
        final RelationPart rp = this.relations.get(partId);
        if (rp == null) {
            return false;
        }
        final POIXMLDocumentPart part = rp.getDocumentPart();
        part.decrementRelationCounter();
        this.getPackagePart().removeRelationship(partId);
        this.relations.remove(partId);
        if (removeUnusedParts && part.getRelationCounter() == 0) {
            try {
                part.onDocumentRemove();
            }
            catch (final IOException e) {
                throw new POIXMLException(e);
            }
            this.getPackagePart().getPackage().removePart(part.getPackagePart());
        }
        return true;
    }
    
    public final POIXMLDocumentPart getParent() {
        return this.parent;
    }
    
    @Override
    public String toString() {
        return (this.packagePart == null) ? "" : this.packagePart.toString();
    }
    
    protected void commit() throws IOException {
    }
    
    protected final void onSave(final Set<PackagePart> alreadySaved) throws IOException {
        if (this.isCommitted) {
            return;
        }
        this.prepareForCommit();
        this.commit();
        alreadySaved.add(this.getPackagePart());
        for (final RelationPart rp : this.relations.values()) {
            final POIXMLDocumentPart p = rp.getDocumentPart();
            if (!alreadySaved.contains(p.getPackagePart())) {
                p.onSave(alreadySaved);
            }
        }
    }
    
    protected void prepareForCommit() {
        final PackagePart part = this.getPackagePart();
        if (part != null) {
            part.clear();
        }
    }
    
    public final POIXMLDocumentPart createRelationship(final POIXMLRelation descriptor, final POIXMLFactory factory) {
        return this.createRelationship(descriptor, factory, -1, false).getDocumentPart();
    }
    
    public final POIXMLDocumentPart createRelationship(final POIXMLRelation descriptor, final POIXMLFactory factory, final int idx) {
        return this.createRelationship(descriptor, factory, idx, false).getDocumentPart();
    }
    
    protected final int getNextPartNumber(final POIXMLRelation descriptor, final int minIdx) {
        final OPCPackage pkg = this.packagePart.getPackage();
        try {
            String name = descriptor.getDefaultFileName();
            if (name.equals(descriptor.getFileName(9999))) {
                final PackagePartName ppName = PackagingURIHelper.createPartName(name);
                if (pkg.containPart(ppName)) {
                    return -1;
                }
                return 0;
            }
            else {
                for (int idx = (minIdx < 0) ? 1 : minIdx, maxIdx = minIdx + pkg.getParts().size(); idx <= maxIdx; ++idx) {
                    name = descriptor.getFileName(idx);
                    final PackagePartName ppName2 = PackagingURIHelper.createPartName(name);
                    if (!pkg.containPart(ppName2)) {
                        return idx;
                    }
                }
            }
        }
        catch (final InvalidFormatException e) {
            throw new POIXMLException(e);
        }
        return -1;
    }
    
    public final RelationPart createRelationship(final POIXMLRelation descriptor, final POIXMLFactory factory, final int idx, final boolean noRelation) {
        try {
            final PackagePartName ppName = PackagingURIHelper.createPartName(descriptor.getFileName(idx));
            PackageRelationship rel = null;
            final PackagePart part = this.packagePart.getPackage().createPart(ppName, descriptor.getContentType());
            if (!noRelation) {
                rel = this.packagePart.addRelationship(ppName, TargetMode.INTERNAL, descriptor.getRelation());
            }
            final POIXMLDocumentPart doc = factory.newDocumentPart(descriptor);
            doc.packagePart = part;
            doc.parent = this;
            if (!noRelation) {
                this.addRelation(rel, doc);
            }
            return new RelationPart(rel, doc);
        }
        catch (final PartAlreadyExistsException pae) {
            throw pae;
        }
        catch (final Exception e) {
            throw new POIXMLException(e);
        }
    }
    
    protected void read(final POIXMLFactory factory, final Map<PackagePart, POIXMLDocumentPart> context) throws OpenXML4JException {
        final PackagePart pp = this.getPackagePart();
        if (pp.getContentType().equals(XWPFRelation.GLOSSARY_DOCUMENT.getContentType())) {
            POIXMLDocumentPart.logger.log(5, new Object[] { "POI does not currently support template.main+xml (glossary) parts.  Skipping this part for now." });
            return;
        }
        final POIXMLDocumentPart otherChild = context.put(pp, this);
        if (otherChild != null && otherChild != this) {
            throw new POIXMLException("Unique PackagePart-POIXMLDocumentPart relation broken!");
        }
        if (!pp.hasRelationships()) {
            return;
        }
        final PackageRelationshipCollection rels = this.packagePart.getRelationships();
        final List<POIXMLDocumentPart> readLater = new ArrayList<POIXMLDocumentPart>();
        for (final PackageRelationship rel : rels) {
            if (rel.getTargetMode() == TargetMode.INTERNAL) {
                final URI uri = rel.getTargetURI();
                PackagePartName relName;
                if (uri.getRawFragment() != null) {
                    relName = PackagingURIHelper.createPartName(uri.getPath());
                }
                else {
                    relName = PackagingURIHelper.createPartName(uri);
                }
                final PackagePart p = this.packagePart.getPackage().getPart(relName);
                if (p == null) {
                    POIXMLDocumentPart.logger.log(7, new Object[] { "Skipped invalid entry " + rel.getTargetURI() });
                }
                else {
                    POIXMLDocumentPart childPart = context.get(p);
                    if (childPart == null) {
                        childPart = factory.createDocumentPart(this, p);
                        if (this instanceof XDDFChart && childPart instanceof XSSFWorkbook) {
                            ((XDDFChart)this).setWorkbook((XSSFWorkbook)childPart);
                        }
                        childPart.parent = this;
                        context.put(p, childPart);
                        readLater.add(childPart);
                    }
                    this.addRelation(rel, childPart);
                }
            }
        }
        for (final POIXMLDocumentPart childPart2 : readLater) {
            childPart2.read(factory, context);
        }
    }
    
    protected PackagePart getTargetPart(final PackageRelationship rel) throws InvalidFormatException {
        return this.getPackagePart().getRelatedPart(rel);
    }
    
    protected void onDocumentCreate() throws IOException {
    }
    
    protected void onDocumentRead() throws IOException {
    }
    
    protected void onDocumentRemove() throws IOException {
    }
    
    @Internal
    @Deprecated
    public static void _invokeOnDocumentRead(final POIXMLDocumentPart part) throws IOException {
        part.onDocumentRead();
    }
    
    private static PackagePart getPartFromOPCPackage(final OPCPackage pkg, final String coreDocumentRel) {
        PackageRelationship coreRel = pkg.getRelationshipsByType(coreDocumentRel).getRelationship(0);
        if (coreRel != null) {
            final PackagePart pp = pkg.getPart(coreRel);
            if (pp == null) {
                IOUtils.closeQuietly((Closeable)pkg);
                throw new POIXMLException("OOXML file structure broken/invalid - core document '" + coreRel.getTargetURI() + "' not found.");
            }
            return pp;
        }
        else {
            coreRel = pkg.getRelationshipsByType("http://purl.oclc.org/ooxml/officeDocument/relationships/officeDocument").getRelationship(0);
            if (coreRel != null) {
                IOUtils.closeQuietly((Closeable)pkg);
                throw new POIXMLException("Strict OOXML isn't currently supported, please see bug #57699");
            }
            IOUtils.closeQuietly((Closeable)pkg);
            throw new POIXMLException("OOXML file structure broken/invalid - no core document found!");
        }
    }
    
    static {
        logger = POILogFactory.getLogger((Class)POIXMLDocumentPart.class);
    }
    
    public static class RelationPart
    {
        private final PackageRelationship relationship;
        private final POIXMLDocumentPart documentPart;
        
        RelationPart(final PackageRelationship relationship, final POIXMLDocumentPart documentPart) {
            this.relationship = relationship;
            this.documentPart = documentPart;
        }
        
        public PackageRelationship getRelationship() {
            return this.relationship;
        }
        
        public <T extends POIXMLDocumentPart> T getDocumentPart() {
            return (T)this.documentPart;
        }
    }
}
