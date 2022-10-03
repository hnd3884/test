package org.apache.poi.xslf.usermodel;

import org.openxmlformats.schemas.presentationml.x2006.main.CmLstDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.CTCommentList;
import org.openxmlformats.schemas.presentationml.x2006.main.NotesDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.CTNotesSlide;
import org.apache.poi.openxml4j.opc.PackageRelationshipCollection;
import org.openxmlformats.schemas.presentationml.x2006.main.SldDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlide;
import org.openxmlformats.schemas.presentationml.x2006.main.SldMasterDocument;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMaster;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdListEntry;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideMasterIdList;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdList;
import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.presentationml.x2006.main.CTPresentation;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import java.util.Iterator;
import org.openxmlformats.schemas.presentationml.x2006.main.CTSlideIdListEntry;
import org.apache.poi.openxml4j.opc.TargetMode;
import org.apache.poi.openxml4j.opc.PackageRelationship;
import java.util.LinkedList;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.List;
import org.openxmlformats.schemas.presentationml.x2006.main.PresentationDocument;
import org.apache.poi.ooxml.POIXMLDocument;

public class XSLFSlideShow extends POIXMLDocument
{
    private PresentationDocument presentationDoc;
    private List<PackagePart> embedds;
    
    public XSLFSlideShow(final OPCPackage container) throws OpenXML4JException, IOException, XmlException {
        super(container);
        if (this.getCorePart().getContentType().equals(XSLFRelation.THEME_MANAGER.getContentType())) {
            this.rebase(this.getPackage());
        }
        this.presentationDoc = PresentationDocument.Factory.parse(this.getCorePart().getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        this.embedds = new LinkedList<PackagePart>();
        for (final CTSlideIdListEntry ctSlide : this.getSlideReferences().getSldIdArray()) {
            final PackagePart corePart = this.getCorePart();
            final PackagePart slidePart = corePart.getRelatedPart(corePart.getRelationship(ctSlide.getId2()));
            for (final PackageRelationship rel : slidePart.getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/oleObject")) {
                if (TargetMode.EXTERNAL == rel.getTargetMode()) {
                    continue;
                }
                this.embedds.add(slidePart.getRelatedPart(rel));
            }
            for (final PackageRelationship rel : slidePart.getRelationshipsByType("http://schemas.openxmlformats.org/officeDocument/2006/relationships/package")) {
                this.embedds.add(slidePart.getRelatedPart(rel));
            }
        }
    }
    
    public XSLFSlideShow(final String file) throws OpenXML4JException, IOException, XmlException {
        this(POIXMLDocument.openPackage(file));
    }
    
    @Internal
    public CTPresentation getPresentation() {
        return this.presentationDoc.getPresentation();
    }
    
    @Internal
    public CTSlideIdList getSlideReferences() {
        if (!this.getPresentation().isSetSldIdLst()) {
            this.getPresentation().setSldIdLst(CTSlideIdList.Factory.newInstance());
        }
        return this.getPresentation().getSldIdLst();
    }
    
    @Internal
    public CTSlideMasterIdList getSlideMasterReferences() {
        return this.getPresentation().getSldMasterIdLst();
    }
    
    public PackagePart getSlideMasterPart(final CTSlideMasterIdListEntry master) throws IOException, XmlException {
        try {
            final PackagePart corePart = this.getCorePart();
            return corePart.getRelatedPart(corePart.getRelationship(master.getId2()));
        }
        catch (final InvalidFormatException e) {
            throw new XmlException((Throwable)e);
        }
    }
    
    @Internal
    public CTSlideMaster getSlideMaster(final CTSlideMasterIdListEntry master) throws IOException, XmlException {
        final PackagePart masterPart = this.getSlideMasterPart(master);
        final SldMasterDocument masterDoc = SldMasterDocument.Factory.parse(masterPart.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        return masterDoc.getSldMaster();
    }
    
    public PackagePart getSlidePart(final CTSlideIdListEntry slide) throws IOException, XmlException {
        try {
            final PackagePart corePart = this.getCorePart();
            return corePart.getRelatedPart(corePart.getRelationship(slide.getId2()));
        }
        catch (final InvalidFormatException e) {
            throw new XmlException((Throwable)e);
        }
    }
    
    @Internal
    public CTSlide getSlide(final CTSlideIdListEntry slide) throws IOException, XmlException {
        final PackagePart slidePart = this.getSlidePart(slide);
        final SldDocument slideDoc = SldDocument.Factory.parse(slidePart.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        return slideDoc.getSld();
    }
    
    public PackagePart getNodesPart(final CTSlideIdListEntry parentSlide) throws IOException, XmlException {
        final PackagePart slidePart = this.getSlidePart(parentSlide);
        PackageRelationshipCollection notes;
        try {
            notes = slidePart.getRelationshipsByType(XSLFRelation.NOTES.getRelation());
        }
        catch (final InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
        if (notes.size() == 0) {
            return null;
        }
        if (notes.size() > 1) {
            throw new IllegalStateException("Expecting 0 or 1 notes for a slide, but found " + notes.size());
        }
        try {
            return slidePart.getRelatedPart(notes.getRelationship(0));
        }
        catch (final InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Internal
    public CTNotesSlide getNotes(final CTSlideIdListEntry slide) throws IOException, XmlException {
        final PackagePart notesPart = this.getNodesPart(slide);
        if (notesPart == null) {
            return null;
        }
        final NotesDocument notesDoc = NotesDocument.Factory.parse(notesPart.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        return notesDoc.getNotes();
    }
    
    @Internal
    public CTCommentList getSlideComments(final CTSlideIdListEntry slide) throws IOException, XmlException {
        final PackagePart slidePart = this.getSlidePart(slide);
        PackageRelationshipCollection commentRels;
        try {
            commentRels = slidePart.getRelationshipsByType(XSLFRelation.COMMENTS.getRelation());
        }
        catch (final InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
        if (commentRels.size() == 0) {
            return null;
        }
        if (commentRels.size() > 1) {
            throw new IllegalStateException("Expecting 0 or 1 comments for a slide, but found " + commentRels.size());
        }
        try {
            final PackagePart cPart = slidePart.getRelatedPart(commentRels.getRelationship(0));
            final CmLstDocument commDoc = CmLstDocument.Factory.parse(cPart.getInputStream(), POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            return commDoc.getCmLst();
        }
        catch (final InvalidFormatException e) {
            throw new IllegalStateException(e);
        }
    }
    
    @Override
    public List<PackagePart> getAllEmbeddedParts() throws OpenXML4JException {
        return this.embedds;
    }
}
