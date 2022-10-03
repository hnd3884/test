package org.apache.poi.xslf.model;

import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import javax.xml.stream.XMLStreamReader;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBodyProperties;
import org.apache.poi.xslf.usermodel.XSLFShape;
import javax.xml.namespace.QName;

public abstract class TextBodyPropertyFetcher<T> extends PropertyFetcher<T>
{
    private static final QName[] TX_BODY;
    private static final QName[] BODY_PR;
    
    @Override
    public boolean fetch(final XSLFShape shape) {
        CTTextBodyProperties props = null;
        try {
            props = shape.selectProperty(CTTextBodyProperties.class, TextBodyPropertyFetcher::parse, new QName[][] { TextBodyPropertyFetcher.TX_BODY, TextBodyPropertyFetcher.BODY_PR });
            return props != null && this.fetch(props);
        }
        catch (final XmlException e) {
            return false;
        }
    }
    
    private static CTTextBodyProperties parse(final XMLStreamReader reader) throws XmlException {
        final CTTextBody body = CTTextBody.Factory.parse(reader);
        return (body != null) ? body.getBodyPr() : null;
    }
    
    public abstract boolean fetch(final CTTextBodyProperties p0);
    
    static {
        TX_BODY = new QName[] { new QName("http://schemas.openxmlformats.org/presentationml/2006/main", "txBody") };
        BODY_PR = new QName[] { new QName("http://schemas.openxmlformats.org/drawingml/2006/main", "bodyPr") };
    }
}
