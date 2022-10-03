package org.apache.poi.xddf.usermodel;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.main.CTGeomGuide;

public class XDDFGeometryGuide
{
    private CTGeomGuide guide;
    
    @Internal
    protected XDDFGeometryGuide(final CTGeomGuide guide) {
        this.guide = guide;
    }
    
    @Internal
    protected CTGeomGuide getXmlObject() {
        return this.guide;
    }
    
    public String getFormula() {
        return this.guide.getFmla();
    }
    
    public void setFormula(final String formula) {
        this.guide.setFmla(formula);
    }
    
    public String getName() {
        return this.guide.getName();
    }
    
    public void setName(final String name) {
        this.guide.setName(name);
    }
}
