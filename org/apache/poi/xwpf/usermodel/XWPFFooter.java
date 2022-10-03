package org.apache.poi.xwpf.usermodel;

import java.io.InputStream;
import org.apache.poi.ooxml.POIXMLException;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtBlock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.FtrDocument;
import java.io.OutputStream;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTNumbering;
import org.apache.xmlbeans.XmlOptions;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.apache.poi.ooxml.POIXMLDocumentPart;
import java.io.IOException;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlCursor;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTbl;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTP;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHdrFtr;

public class XWPFFooter extends XWPFHeaderFooter
{
    public XWPFFooter() {
    }
    
    public XWPFFooter(final XWPFDocument doc, final CTHdrFtr hdrFtr) throws IOException {
        super(doc, hdrFtr);
        final XmlCursor cursor = this.headerFooter.newCursor();
        cursor.selectPath("./*");
        while (cursor.toNextSelection()) {
            final XmlObject o = cursor.getObject();
            if (o instanceof CTP) {
                final XWPFParagraph p = new XWPFParagraph((CTP)o, this);
                this.paragraphs.add(p);
                this.bodyElements.add(p);
            }
            if (o instanceof CTTbl) {
                final XWPFTable t = new XWPFTable((CTTbl)o, this);
                this.tables.add(t);
                this.bodyElements.add(t);
            }
        }
        cursor.dispose();
    }
    
    public XWPFFooter(final POIXMLDocumentPart parent, final PackagePart part) throws IOException {
        super(parent, part);
    }
    
    @Override
    protected void commit() throws IOException {
        final XmlOptions xmlOptions = new XmlOptions(POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
        xmlOptions.setSaveSyntheticDocumentElement(new QName(CTNumbering.type.getName().getNamespaceURI(), "ftr"));
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        super._getHdrFtr().save(out, xmlOptions);
        out.close();
    }
    
    @Override
    protected void onDocumentRead() throws IOException {
        super.onDocumentRead();
        FtrDocument ftrDocument = null;
        try (final InputStream is = this.getPackagePart().getInputStream()) {
            ftrDocument = FtrDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.headerFooter = ftrDocument.getFtr();
            final XmlCursor cursor = this.headerFooter.newCursor();
            cursor.selectPath("./*");
            while (cursor.toNextSelection()) {
                final XmlObject o = cursor.getObject();
                if (o instanceof CTP) {
                    final XWPFParagraph p = new XWPFParagraph((CTP)o, this);
                    this.paragraphs.add(p);
                    this.bodyElements.add(p);
                }
                if (o instanceof CTTbl) {
                    final XWPFTable t = new XWPFTable((CTTbl)o, this);
                    this.tables.add(t);
                    this.bodyElements.add(t);
                }
                if (o instanceof CTSdtBlock) {
                    final XWPFSDT c = new XWPFSDT((CTSdtBlock)o, this);
                    this.bodyElements.add(c);
                }
            }
            cursor.dispose();
        }
        catch (final Exception e) {
            throw new POIXMLException(e);
        }
    }
    
    @Override
    public BodyType getPartType() {
        return BodyType.FOOTER;
    }
}
