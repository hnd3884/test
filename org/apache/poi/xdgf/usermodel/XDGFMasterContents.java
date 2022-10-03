package org.apache.poi.xdgf.usermodel;

import org.apache.poi.ooxml.POIXMLDocumentPart;
import org.apache.poi.xdgf.exceptions.XDGFException;
import java.io.IOException;
import org.apache.xmlbeans.XmlException;
import org.apache.poi.ooxml.POIXMLException;
import com.microsoft.schemas.office.visio.x2012.main.MasterContentsDocument;
import org.apache.poi.openxml4j.opc.PackagePart;

public class XDGFMasterContents extends XDGFBaseContents
{
    protected XDGFMaster _master;
    
    public XDGFMasterContents(final PackagePart part) {
        super(part);
    }
    
    @Override
    protected void onDocumentRead() {
        try {
            try {
                this._pageContents = MasterContentsDocument.Factory.parse(this.getPackagePart().getInputStream()).getMasterContents();
            }
            catch (final XmlException | IOException e) {
                throw new POIXMLException(e);
            }
            super.onDocumentRead();
        }
        catch (final POIXMLException e2) {
            throw XDGFException.wrap(this, e2);
        }
    }
    
    public XDGFMaster getMaster() {
        return this._master;
    }
    
    protected void setMaster(final XDGFMaster master) {
        this._master = master;
    }
}
