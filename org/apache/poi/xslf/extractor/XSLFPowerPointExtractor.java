package org.apache.poi.xslf.extractor;

import org.apache.poi.sl.usermodel.Slide;
import org.apache.poi.xslf.usermodel.XSLFSlide;
import java.io.IOException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.xslf.usermodel.XSLFSlideShow;
import org.apache.poi.sl.usermodel.SlideShow;
import org.apache.poi.ooxml.POIXMLDocument;
import org.apache.poi.xslf.usermodel.XMLSlideShow;
import org.apache.poi.xslf.usermodel.XSLFTextParagraph;
import org.apache.poi.xslf.usermodel.XSLFShape;
import org.apache.poi.sl.extractor.SlideShowExtractor;
import org.apache.poi.xslf.usermodel.XSLFRelation;
import org.apache.poi.util.Removal;
import org.apache.poi.ooxml.extractor.POIXMLTextExtractor;

@Deprecated
@Removal(version = "5.0.0")
public class XSLFPowerPointExtractor extends POIXMLTextExtractor
{
    public static final XSLFRelation[] SUPPORTED_TYPES;
    private final SlideShowExtractor<XSLFShape, XSLFTextParagraph> delegate;
    private boolean slidesByDefault;
    private boolean notesByDefault;
    private boolean commentsByDefault;
    private boolean masterByDefault;
    
    public XSLFPowerPointExtractor(final XMLSlideShow slideShow) {
        super(slideShow);
        this.slidesByDefault = true;
        this.delegate = (SlideShowExtractor<XSLFShape, XSLFTextParagraph>)new SlideShowExtractor((SlideShow)slideShow);
    }
    
    public XSLFPowerPointExtractor(final XSLFSlideShow slideShow) {
        this(new XMLSlideShow(slideShow.getPackage()));
    }
    
    public XSLFPowerPointExtractor(final OPCPackage container) throws XmlException, OpenXML4JException, IOException {
        this(new XSLFSlideShow(container));
    }
    
    public static void main(final String[] args) throws Exception {
        if (args.length < 1) {
            System.err.println("Use:");
            System.err.println("  XSLFPowerPointExtractor <filename.pptx>");
            System.exit(1);
        }
        final POIXMLTextExtractor extractor = new XSLFPowerPointExtractor(new XSLFSlideShow(args[0]));
        System.out.println(extractor.getText());
        extractor.close();
    }
    
    public void setSlidesByDefault(final boolean slidesByDefault) {
        this.slidesByDefault = slidesByDefault;
        this.delegate.setSlidesByDefault(slidesByDefault);
    }
    
    public void setNotesByDefault(final boolean notesByDefault) {
        this.notesByDefault = notesByDefault;
        this.delegate.setNotesByDefault(notesByDefault);
    }
    
    public void setCommentsByDefault(final boolean commentsByDefault) {
        this.commentsByDefault = commentsByDefault;
        this.delegate.setCommentsByDefault(commentsByDefault);
    }
    
    public void setMasterByDefault(final boolean masterByDefault) {
        this.masterByDefault = masterByDefault;
        this.delegate.setMasterByDefault(masterByDefault);
    }
    
    public String getText() {
        return this.delegate.getText();
    }
    
    public String getText(final boolean slideText, final boolean notesText) {
        return this.getText(slideText, notesText, this.commentsByDefault, this.masterByDefault);
    }
    
    public String getText(final boolean slideText, final boolean notesText, final boolean masterText) {
        return this.getText(slideText, notesText, this.commentsByDefault, masterText);
    }
    
    public String getText(final boolean slideText, final boolean notesText, final boolean commentText, final boolean masterText) {
        this.delegate.setSlidesByDefault(slideText);
        this.delegate.setNotesByDefault(notesText);
        this.delegate.setCommentsByDefault(commentText);
        this.delegate.setMasterByDefault(masterText);
        try {
            return this.delegate.getText();
        }
        finally {
            this.delegate.setSlidesByDefault(this.slidesByDefault);
            this.delegate.setNotesByDefault(this.notesByDefault);
            this.delegate.setCommentsByDefault(this.commentsByDefault);
            this.delegate.setMasterByDefault(this.masterByDefault);
        }
    }
    
    public static String getText(final XSLFSlide slide, final boolean slideText, final boolean notesText, final boolean masterText) {
        return getText(slide, slideText, notesText, false, masterText);
    }
    
    public static String getText(final XSLFSlide slide, final boolean slideText, final boolean notesText, final boolean commentText, final boolean masterText) {
        final SlideShowExtractor<XSLFShape, XSLFTextParagraph> ex = (SlideShowExtractor<XSLFShape, XSLFTextParagraph>)new SlideShowExtractor((SlideShow)slide.getSlideShow());
        ex.setSlidesByDefault(slideText);
        ex.setNotesByDefault(notesText);
        ex.setCommentsByDefault(commentText);
        ex.setMasterByDefault(masterText);
        return ex.getText((Slide)slide);
    }
    
    static {
        SUPPORTED_TYPES = new XSLFRelation[] { XSLFRelation.MAIN, XSLFRelation.MACRO, XSLFRelation.MACRO_TEMPLATE, XSLFRelation.PRESENTATIONML, XSLFRelation.PRESENTATIONML_TEMPLATE, XSLFRelation.PRESENTATION_MACRO };
    }
}
