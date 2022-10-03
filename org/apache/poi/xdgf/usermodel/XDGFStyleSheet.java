package org.apache.poi.xdgf.usermodel;

import org.apache.poi.util.Internal;
import com.microsoft.schemas.office.visio.x2012.main.SheetType;
import com.microsoft.schemas.office.visio.x2012.main.StyleSheetType;

public class XDGFStyleSheet extends XDGFSheet
{
    public XDGFStyleSheet(final StyleSheetType styleSheet, final XDGFDocument document) {
        super((SheetType)styleSheet, document);
    }
    
    @Internal
    public StyleSheetType getXmlObject() {
        return (StyleSheetType)this._sheet;
    }
}
