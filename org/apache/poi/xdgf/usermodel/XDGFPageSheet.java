package org.apache.poi.xdgf.usermodel;

import com.microsoft.schemas.office.visio.x2012.main.SheetType;
import com.microsoft.schemas.office.visio.x2012.main.PageSheetType;

public class XDGFPageSheet extends XDGFSheet
{
    PageSheetType _pageSheet;
    
    public XDGFPageSheet(final PageSheetType sheet, final XDGFDocument document) {
        super((SheetType)sheet, document);
        this._pageSheet = sheet;
    }
    
    PageSheetType getXmlObject() {
        return this._pageSheet;
    }
}
