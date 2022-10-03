package org.apache.poi.xssf.model;

import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcCell;
import java.io.OutputStream;
import org.apache.xmlbeans.XmlException;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CalcChainDocument;
import org.apache.poi.ooxml.POIXMLTypeLoader;
import java.io.InputStream;
import java.io.IOException;
import org.apache.poi.openxml4j.opc.PackagePart;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTCalcChain;
import org.apache.poi.ooxml.POIXMLDocumentPart;

public class CalculationChain extends POIXMLDocumentPart
{
    private CTCalcChain chain;
    
    public CalculationChain() {
        this.chain = CTCalcChain.Factory.newInstance();
    }
    
    public CalculationChain(final PackagePart part) throws IOException {
        super(part);
        this.readFrom(part.getInputStream());
    }
    
    public void readFrom(final InputStream is) throws IOException {
        try {
            final CalcChainDocument doc = CalcChainDocument.Factory.parse(is, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
            this.chain = doc.getCalcChain();
        }
        catch (final XmlException e) {
            throw new IOException(e.getLocalizedMessage());
        }
    }
    
    public void writeTo(final OutputStream out) throws IOException {
        final CalcChainDocument doc = CalcChainDocument.Factory.newInstance();
        doc.setCalcChain(this.chain);
        doc.save(out, POIXMLTypeLoader.DEFAULT_XML_OPTIONS);
    }
    
    @Override
    protected void commit() throws IOException {
        final PackagePart part = this.getPackagePart();
        final OutputStream out = part.getOutputStream();
        this.writeTo(out);
        out.close();
    }
    
    public CTCalcChain getCTCalcChain() {
        return this.chain;
    }
    
    public void removeItem(final int sheetId, final String ref) {
        int id = -1;
        final CTCalcCell[] c = this.chain.getCArray();
        for (int i = 0; i < c.length; ++i) {
            if (c[i].isSetI()) {
                id = c[i].getI();
            }
            if (id == sheetId && c[i].getR().equals(ref)) {
                if (c[i].isSetI() && i < c.length - 1 && !c[i + 1].isSetI()) {
                    c[i + 1].setI(id);
                }
                this.chain.removeC(i);
                break;
            }
        }
    }
}
