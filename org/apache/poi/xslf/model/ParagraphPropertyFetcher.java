package org.apache.poi.xslf.model;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraph;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextParagraphProperties;
import org.apache.poi.xslf.usermodel.XSLFShape;
import javax.xml.namespace.QName;

public abstract class ParagraphPropertyFetcher<T> extends PropertyFetcher<T>
{
    static final String PML_NS = "http://schemas.openxmlformats.org/presentationml/2006/main";
    static final String DML_NS = "http://schemas.openxmlformats.org/drawingml/2006/main";
    private static final QName[] TX_BODY;
    private static final QName[] LST_STYLE;
    int _level;
    
    public ParagraphPropertyFetcher(final int level) {
        this._level = level;
    }
    
    @Override
    public boolean fetch(final XSLFShape shape) {
        final QName[] lvlProp = { new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lvl" + (this._level + 1) + "pPr") };
        CTTextParagraphProperties props = null;
        try {
            props = shape.selectProperty(CTTextParagraphProperties.class, ParagraphPropertyFetcher::parse, new QName[][] { ParagraphPropertyFetcher.TX_BODY, ParagraphPropertyFetcher.LST_STYLE, lvlProp });
            return props != null && this.fetch(props);
        }
        catch (final XmlException e) {
            return false;
        }
    }
    
    private static CTTextParagraphProperties parse(final XMLStreamReader reader) throws XmlException {
        final CTTextParagraph para = CTTextParagraph.Factory.parse(reader);
        return (para != null && para.isSetPPr()) ? para.getPPr() : null;
    }
    
    public abstract boolean fetch(final CTTextParagraphProperties p0);
    
    static {
        TX_BODY = new QName[] { new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "txBody") };
        LST_STYLE = new QName[] { new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "lstStyle") };
    }
}
