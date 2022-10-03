package org.apache.poi.xddf.usermodel.text;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextShapeAutofit;

public class XDDFShapeAutoFit implements XDDFAutoFit
{
    private CTTextShapeAutofit autofit;
    
    public XDDFShapeAutoFit() {
        this(CTTextShapeAutofit.Factory.newInstance());
    }
    
    @Internal
    protected XDDFShapeAutoFit(final CTTextShapeAutofit autofit) {
        this.autofit = autofit;
    }
    
    @Internal
    protected CTTextShapeAutofit getXmlObject() {
        return this.autofit;
    }
    
    @Override
    public int getFontScale() {
        return 100000;
    }
    
    @Override
    public int getLineSpaceReduction() {
        return 0;
    }
}
