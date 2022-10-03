package org.apache.poi.xddf.usermodel.chart;

import org.apache.poi.util.Internal;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;

public class XDDFChartExtensionList
{
    private CTExtensionList list;
    
    public XDDFChartExtensionList() {
        this(CTExtensionList.Factory.newInstance());
    }
    
    @Internal
    protected XDDFChartExtensionList(final CTExtensionList list) {
        this.list = list;
    }
    
    @Internal
    public CTExtensionList getXmlObject() {
        return this.list;
    }
}
