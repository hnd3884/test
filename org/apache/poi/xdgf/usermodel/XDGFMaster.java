package org.apache.poi.xdgf.usermodel;

import org.apache.poi.util.Internal;
import com.microsoft.schemas.office.visio.x2012.main.MasterType;

public class XDGFMaster
{
    private MasterType _master;
    protected XDGFMasterContents _content;
    protected XDGFSheet _pageSheet;
    
    public XDGFMaster(final MasterType master, final XDGFMasterContents content, final XDGFDocument document) {
        this._master = master;
        (this._content = content).setMaster(this);
        if (master.isSetPageSheet()) {
            this._pageSheet = new XDGFPageSheet(master.getPageSheet(), document);
        }
    }
    
    @Internal
    protected MasterType getXmlObject() {
        return this._master;
    }
    
    @Override
    public String toString() {
        return "<Master ID=\"" + this.getID() + "\" " + this._content + ">";
    }
    
    public long getID() {
        return this._master.getID();
    }
    
    public String getName() {
        return this._master.getName();
    }
    
    public XDGFMasterContents getContent() {
        return this._content;
    }
    
    public XDGFSheet getPageSheet() {
        return this._pageSheet;
    }
}
