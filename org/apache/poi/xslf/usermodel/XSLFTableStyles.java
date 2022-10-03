package org.apache.poi.xslf.usermodel;

import java.util.Collections;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import java.util.Iterator;
import java.io.InputStream;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyle;
import java.util.ArrayList;
import org.openxmlformats.schemas.drawingml.x2006.main.TblStyleLstDocument;
import org.apache.poi.openxml4j.opc.PackagePart;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTableStyleList;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class XSLFTableStyles extends POIXMLDocumentPart implements Iterable<XSLFTableStyle>
{
    private CTTableStyleList _tblStyleLst;
    private List<XSLFTableStyle> _styles;
    
    public XSLFTableStyles() {
    }
    
    public XSLFTableStyles(final PackagePart part) throws IOException, XmlException {
        super(part);
        TblStyleLstDocument styleDoc;
        try (final InputStream is = this.getPackagePart().getInputStream()) {
            styleDoc = TblStyleLstDocument.Factory.parse(is);
        }
        this._tblStyleLst = styleDoc.getTblStyleLst();
        final List<CTTableStyle> tblStyles = this._tblStyleLst.getTblStyleList();
        this._styles = new ArrayList<XSLFTableStyle>(tblStyles.size());
        for (final CTTableStyle c : tblStyles) {
            this._styles.add(new XSLFTableStyle(c));
        }
    }
    
    public CTTableStyleList getXmlObject() {
        return this._tblStyleLst;
    }
    
    @Override
    public Iterator<XSLFTableStyle> iterator() {
        return this._styles.iterator();
    }
    
    public List<XSLFTableStyle> getStyles() {
        return Collections.unmodifiableList((List<? extends XSLFTableStyle>)this._styles);
    }
}
