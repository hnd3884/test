package org.apache.poi.xssf.usermodel;

import org.openxmlformats.schemas.drawingml.x2006.main.CTRegularTextRun;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextCharacterProperties;

class XSSFLineBreak extends XSSFTextRun
{
    private final CTTextCharacterProperties _brProps;
    
    XSSFLineBreak(final CTRegularTextRun r, final XSSFTextParagraph p, final CTTextCharacterProperties brProps) {
        super(r, p);
        this._brProps = brProps;
    }
    
    @Override
    protected CTTextCharacterProperties getRPr() {
        return this._brProps;
    }
    
    @Override
    public void setText(final String text) {
        throw new IllegalStateException("You cannot change text of a line break, it is always '\\n'");
    }
}
