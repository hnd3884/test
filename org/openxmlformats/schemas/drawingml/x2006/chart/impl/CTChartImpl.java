package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDispBlanksAs;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLegend;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPlotArea;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTView3D;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotFmts;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTChartImpl extends XmlComplexContentImpl implements CTChart
{
    private static final long serialVersionUID = 1L;
    private static final QName TITLE$0;
    private static final QName AUTOTITLEDELETED$2;
    private static final QName PIVOTFMTS$4;
    private static final QName VIEW3D$6;
    private static final QName FLOOR$8;
    private static final QName SIDEWALL$10;
    private static final QName BACKWALL$12;
    private static final QName PLOTAREA$14;
    private static final QName LEGEND$16;
    private static final QName PLOTVISONLY$18;
    private static final QName DISPBLANKSAS$20;
    private static final QName SHOWDLBLSOVERMAX$22;
    private static final QName EXTLST$24;
    
    public CTChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTTitle getTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTitle ctTitle = (CTTitle)this.get_store().find_element_user(CTChartImpl.TITLE$0, 0);
            if (ctTitle == null) {
                return null;
            }
            return ctTitle;
        }
    }
    
    public boolean isSetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.TITLE$0) != 0;
        }
    }
    
    public void setTitle(final CTTitle ctTitle) {
        this.generatedSetterHelperImpl((XmlObject)ctTitle, CTChartImpl.TITLE$0, 0, (short)1);
    }
    
    public CTTitle addNewTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTitle)this.get_store().add_element_user(CTChartImpl.TITLE$0);
        }
    }
    
    public void unsetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.TITLE$0, 0);
        }
    }
    
    public CTBoolean getAutoTitleDeleted() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTChartImpl.AUTOTITLEDELETED$2, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetAutoTitleDeleted() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.AUTOTITLEDELETED$2) != 0;
        }
    }
    
    public void setAutoTitleDeleted(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTChartImpl.AUTOTITLEDELETED$2, 0, (short)1);
    }
    
    public CTBoolean addNewAutoTitleDeleted() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTChartImpl.AUTOTITLEDELETED$2);
        }
    }
    
    public void unsetAutoTitleDeleted() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.AUTOTITLEDELETED$2, 0);
        }
    }
    
    public CTPivotFmts getPivotFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotFmts ctPivotFmts = (CTPivotFmts)this.get_store().find_element_user(CTChartImpl.PIVOTFMTS$4, 0);
            if (ctPivotFmts == null) {
                return null;
            }
            return ctPivotFmts;
        }
    }
    
    public boolean isSetPivotFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.PIVOTFMTS$4) != 0;
        }
    }
    
    public void setPivotFmts(final CTPivotFmts ctPivotFmts) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotFmts, CTChartImpl.PIVOTFMTS$4, 0, (short)1);
    }
    
    public CTPivotFmts addNewPivotFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotFmts)this.get_store().add_element_user(CTChartImpl.PIVOTFMTS$4);
        }
    }
    
    public void unsetPivotFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.PIVOTFMTS$4, 0);
        }
    }
    
    public CTView3D getView3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTView3D ctView3D = (CTView3D)this.get_store().find_element_user(CTChartImpl.VIEW3D$6, 0);
            if (ctView3D == null) {
                return null;
            }
            return ctView3D;
        }
    }
    
    public boolean isSetView3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.VIEW3D$6) != 0;
        }
    }
    
    public void setView3D(final CTView3D ctView3D) {
        this.generatedSetterHelperImpl((XmlObject)ctView3D, CTChartImpl.VIEW3D$6, 0, (short)1);
    }
    
    public CTView3D addNewView3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTView3D)this.get_store().add_element_user(CTChartImpl.VIEW3D$6);
        }
    }
    
    public void unsetView3D() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.VIEW3D$6, 0);
        }
    }
    
    public CTSurface getFloor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSurface ctSurface = (CTSurface)this.get_store().find_element_user(CTChartImpl.FLOOR$8, 0);
            if (ctSurface == null) {
                return null;
            }
            return ctSurface;
        }
    }
    
    public boolean isSetFloor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.FLOOR$8) != 0;
        }
    }
    
    public void setFloor(final CTSurface ctSurface) {
        this.generatedSetterHelperImpl((XmlObject)ctSurface, CTChartImpl.FLOOR$8, 0, (short)1);
    }
    
    public CTSurface addNewFloor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSurface)this.get_store().add_element_user(CTChartImpl.FLOOR$8);
        }
    }
    
    public void unsetFloor() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.FLOOR$8, 0);
        }
    }
    
    public CTSurface getSideWall() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSurface ctSurface = (CTSurface)this.get_store().find_element_user(CTChartImpl.SIDEWALL$10, 0);
            if (ctSurface == null) {
                return null;
            }
            return ctSurface;
        }
    }
    
    public boolean isSetSideWall() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.SIDEWALL$10) != 0;
        }
    }
    
    public void setSideWall(final CTSurface ctSurface) {
        this.generatedSetterHelperImpl((XmlObject)ctSurface, CTChartImpl.SIDEWALL$10, 0, (short)1);
    }
    
    public CTSurface addNewSideWall() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSurface)this.get_store().add_element_user(CTChartImpl.SIDEWALL$10);
        }
    }
    
    public void unsetSideWall() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.SIDEWALL$10, 0);
        }
    }
    
    public CTSurface getBackWall() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSurface ctSurface = (CTSurface)this.get_store().find_element_user(CTChartImpl.BACKWALL$12, 0);
            if (ctSurface == null) {
                return null;
            }
            return ctSurface;
        }
    }
    
    public boolean isSetBackWall() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.BACKWALL$12) != 0;
        }
    }
    
    public void setBackWall(final CTSurface ctSurface) {
        this.generatedSetterHelperImpl((XmlObject)ctSurface, CTChartImpl.BACKWALL$12, 0, (short)1);
    }
    
    public CTSurface addNewBackWall() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSurface)this.get_store().add_element_user(CTChartImpl.BACKWALL$12);
        }
    }
    
    public void unsetBackWall() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.BACKWALL$12, 0);
        }
    }
    
    public CTPlotArea getPlotArea() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPlotArea ctPlotArea = (CTPlotArea)this.get_store().find_element_user(CTChartImpl.PLOTAREA$14, 0);
            if (ctPlotArea == null) {
                return null;
            }
            return ctPlotArea;
        }
    }
    
    public void setPlotArea(final CTPlotArea ctPlotArea) {
        this.generatedSetterHelperImpl((XmlObject)ctPlotArea, CTChartImpl.PLOTAREA$14, 0, (short)1);
    }
    
    public CTPlotArea addNewPlotArea() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPlotArea)this.get_store().add_element_user(CTChartImpl.PLOTAREA$14);
        }
    }
    
    public CTLegend getLegend() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLegend ctLegend = (CTLegend)this.get_store().find_element_user(CTChartImpl.LEGEND$16, 0);
            if (ctLegend == null) {
                return null;
            }
            return ctLegend;
        }
    }
    
    public boolean isSetLegend() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.LEGEND$16) != 0;
        }
    }
    
    public void setLegend(final CTLegend ctLegend) {
        this.generatedSetterHelperImpl((XmlObject)ctLegend, CTChartImpl.LEGEND$16, 0, (short)1);
    }
    
    public CTLegend addNewLegend() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLegend)this.get_store().add_element_user(CTChartImpl.LEGEND$16);
        }
    }
    
    public void unsetLegend() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.LEGEND$16, 0);
        }
    }
    
    public CTBoolean getPlotVisOnly() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTChartImpl.PLOTVISONLY$18, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetPlotVisOnly() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.PLOTVISONLY$18) != 0;
        }
    }
    
    public void setPlotVisOnly(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTChartImpl.PLOTVISONLY$18, 0, (short)1);
    }
    
    public CTBoolean addNewPlotVisOnly() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTChartImpl.PLOTVISONLY$18);
        }
    }
    
    public void unsetPlotVisOnly() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.PLOTVISONLY$18, 0);
        }
    }
    
    public CTDispBlanksAs getDispBlanksAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDispBlanksAs ctDispBlanksAs = (CTDispBlanksAs)this.get_store().find_element_user(CTChartImpl.DISPBLANKSAS$20, 0);
            if (ctDispBlanksAs == null) {
                return null;
            }
            return ctDispBlanksAs;
        }
    }
    
    public boolean isSetDispBlanksAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.DISPBLANKSAS$20) != 0;
        }
    }
    
    public void setDispBlanksAs(final CTDispBlanksAs ctDispBlanksAs) {
        this.generatedSetterHelperImpl((XmlObject)ctDispBlanksAs, CTChartImpl.DISPBLANKSAS$20, 0, (short)1);
    }
    
    public CTDispBlanksAs addNewDispBlanksAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDispBlanksAs)this.get_store().add_element_user(CTChartImpl.DISPBLANKSAS$20);
        }
    }
    
    public void unsetDispBlanksAs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.DISPBLANKSAS$20, 0);
        }
    }
    
    public CTBoolean getShowDLblsOverMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTChartImpl.SHOWDLBLSOVERMAX$22, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetShowDLblsOverMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.SHOWDLBLSOVERMAX$22) != 0;
        }
    }
    
    public void setShowDLblsOverMax(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTChartImpl.SHOWDLBLSOVERMAX$22, 0, (short)1);
    }
    
    public CTBoolean addNewShowDLblsOverMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTChartImpl.SHOWDLBLSOVERMAX$22);
        }
    }
    
    public void unsetShowDLblsOverMax() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.SHOWDLBLSOVERMAX$22, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTChartImpl.EXTLST$24, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartImpl.EXTLST$24) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTChartImpl.EXTLST$24, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTChartImpl.EXTLST$24);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartImpl.EXTLST$24, 0);
        }
    }
    
    static {
        TITLE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "title");
        AUTOTITLEDELETED$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "autoTitleDeleted");
        PIVOTFMTS$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pivotFmts");
        VIEW3D$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "view3D");
        FLOOR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "floor");
        SIDEWALL$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "sideWall");
        BACKWALL$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "backWall");
        PLOTAREA$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "plotArea");
        LEGEND$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "legend");
        PLOTVISONLY$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "plotVisOnly");
        DISPBLANKSAS$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dispBlanksAs");
        SHOWDLBLSOVERMAX$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "showDLblsOverMax");
        EXTLST$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
