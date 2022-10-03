package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRelId;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPrintSettings;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTExternalData;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChart;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTProtection;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPivotSource;
import org.openxmlformats.schemas.drawingml.x2006.main.CTColorMapping;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTStyle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTextLanguageID;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartSpace;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTChartSpaceImpl extends XmlComplexContentImpl implements CTChartSpace
{
    private static final long serialVersionUID = 1L;
    private static final QName DATE1904$0;
    private static final QName LANG$2;
    private static final QName ROUNDEDCORNERS$4;
    private static final QName STYLE$6;
    private static final QName CLRMAPOVR$8;
    private static final QName PIVOTSOURCE$10;
    private static final QName PROTECTION$12;
    private static final QName CHART$14;
    private static final QName SPPR$16;
    private static final QName TXPR$18;
    private static final QName EXTERNALDATA$20;
    private static final QName PRINTSETTINGS$22;
    private static final QName USERSHAPES$24;
    private static final QName EXTLST$26;
    
    public CTChartSpaceImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBoolean getDate1904() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTChartSpaceImpl.DATE1904$0, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetDate1904() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.DATE1904$0) != 0;
        }
    }
    
    public void setDate1904(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTChartSpaceImpl.DATE1904$0, 0, (short)1);
    }
    
    public CTBoolean addNewDate1904() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTChartSpaceImpl.DATE1904$0);
        }
    }
    
    public void unsetDate1904() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.DATE1904$0, 0);
        }
    }
    
    public CTTextLanguageID getLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextLanguageID ctTextLanguageID = (CTTextLanguageID)this.get_store().find_element_user(CTChartSpaceImpl.LANG$2, 0);
            if (ctTextLanguageID == null) {
                return null;
            }
            return ctTextLanguageID;
        }
    }
    
    public boolean isSetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.LANG$2) != 0;
        }
    }
    
    public void setLang(final CTTextLanguageID ctTextLanguageID) {
        this.generatedSetterHelperImpl((XmlObject)ctTextLanguageID, CTChartSpaceImpl.LANG$2, 0, (short)1);
    }
    
    public CTTextLanguageID addNewLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextLanguageID)this.get_store().add_element_user(CTChartSpaceImpl.LANG$2);
        }
    }
    
    public void unsetLang() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.LANG$2, 0);
        }
    }
    
    public CTBoolean getRoundedCorners() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTChartSpaceImpl.ROUNDEDCORNERS$4, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetRoundedCorners() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.ROUNDEDCORNERS$4) != 0;
        }
    }
    
    public void setRoundedCorners(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTChartSpaceImpl.ROUNDEDCORNERS$4, 0, (short)1);
    }
    
    public CTBoolean addNewRoundedCorners() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTChartSpaceImpl.ROUNDEDCORNERS$4);
        }
    }
    
    public void unsetRoundedCorners() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.ROUNDEDCORNERS$4, 0);
        }
    }
    
    public CTStyle getStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTStyle ctStyle = (CTStyle)this.get_store().find_element_user(CTChartSpaceImpl.STYLE$6, 0);
            if (ctStyle == null) {
                return null;
            }
            return ctStyle;
        }
    }
    
    public boolean isSetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.STYLE$6) != 0;
        }
    }
    
    public void setStyle(final CTStyle ctStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctStyle, CTChartSpaceImpl.STYLE$6, 0, (short)1);
    }
    
    public CTStyle addNewStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTStyle)this.get_store().add_element_user(CTChartSpaceImpl.STYLE$6);
        }
    }
    
    public void unsetStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.STYLE$6, 0);
        }
    }
    
    public CTColorMapping getClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTColorMapping ctColorMapping = (CTColorMapping)this.get_store().find_element_user(CTChartSpaceImpl.CLRMAPOVR$8, 0);
            if (ctColorMapping == null) {
                return null;
            }
            return ctColorMapping;
        }
    }
    
    public boolean isSetClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.CLRMAPOVR$8) != 0;
        }
    }
    
    public void setClrMapOvr(final CTColorMapping ctColorMapping) {
        this.generatedSetterHelperImpl((XmlObject)ctColorMapping, CTChartSpaceImpl.CLRMAPOVR$8, 0, (short)1);
    }
    
    public CTColorMapping addNewClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTColorMapping)this.get_store().add_element_user(CTChartSpaceImpl.CLRMAPOVR$8);
        }
    }
    
    public void unsetClrMapOvr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.CLRMAPOVR$8, 0);
        }
    }
    
    public CTPivotSource getPivotSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPivotSource ctPivotSource = (CTPivotSource)this.get_store().find_element_user(CTChartSpaceImpl.PIVOTSOURCE$10, 0);
            if (ctPivotSource == null) {
                return null;
            }
            return ctPivotSource;
        }
    }
    
    public boolean isSetPivotSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.PIVOTSOURCE$10) != 0;
        }
    }
    
    public void setPivotSource(final CTPivotSource ctPivotSource) {
        this.generatedSetterHelperImpl((XmlObject)ctPivotSource, CTChartSpaceImpl.PIVOTSOURCE$10, 0, (short)1);
    }
    
    public CTPivotSource addNewPivotSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPivotSource)this.get_store().add_element_user(CTChartSpaceImpl.PIVOTSOURCE$10);
        }
    }
    
    public void unsetPivotSource() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.PIVOTSOURCE$10, 0);
        }
    }
    
    public CTProtection getProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTProtection ctProtection = (CTProtection)this.get_store().find_element_user(CTChartSpaceImpl.PROTECTION$12, 0);
            if (ctProtection == null) {
                return null;
            }
            return ctProtection;
        }
    }
    
    public boolean isSetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.PROTECTION$12) != 0;
        }
    }
    
    public void setProtection(final CTProtection ctProtection) {
        this.generatedSetterHelperImpl((XmlObject)ctProtection, CTChartSpaceImpl.PROTECTION$12, 0, (short)1);
    }
    
    public CTProtection addNewProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTProtection)this.get_store().add_element_user(CTChartSpaceImpl.PROTECTION$12);
        }
    }
    
    public void unsetProtection() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.PROTECTION$12, 0);
        }
    }
    
    public CTChart getChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChart ctChart = (CTChart)this.get_store().find_element_user(CTChartSpaceImpl.CHART$14, 0);
            if (ctChart == null) {
                return null;
            }
            return ctChart;
        }
    }
    
    public void setChart(final CTChart ctChart) {
        this.generatedSetterHelperImpl((XmlObject)ctChart, CTChartSpaceImpl.CHART$14, 0, (short)1);
    }
    
    public CTChart addNewChart() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChart)this.get_store().add_element_user(CTChartSpaceImpl.CHART$14);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTChartSpaceImpl.SPPR$16, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.SPPR$16) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTChartSpaceImpl.SPPR$16, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTChartSpaceImpl.SPPR$16);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.SPPR$16, 0);
        }
    }
    
    public CTTextBody getTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBody ctTextBody = (CTTextBody)this.get_store().find_element_user(CTChartSpaceImpl.TXPR$18, 0);
            if (ctTextBody == null) {
                return null;
            }
            return ctTextBody;
        }
    }
    
    public boolean isSetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.TXPR$18) != 0;
        }
    }
    
    public void setTxPr(final CTTextBody ctTextBody) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBody, CTChartSpaceImpl.TXPR$18, 0, (short)1);
    }
    
    public CTTextBody addNewTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBody)this.get_store().add_element_user(CTChartSpaceImpl.TXPR$18);
        }
    }
    
    public void unsetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.TXPR$18, 0);
        }
    }
    
    public CTExternalData getExternalData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExternalData ctExternalData = (CTExternalData)this.get_store().find_element_user(CTChartSpaceImpl.EXTERNALDATA$20, 0);
            if (ctExternalData == null) {
                return null;
            }
            return ctExternalData;
        }
    }
    
    public boolean isSetExternalData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.EXTERNALDATA$20) != 0;
        }
    }
    
    public void setExternalData(final CTExternalData ctExternalData) {
        this.generatedSetterHelperImpl((XmlObject)ctExternalData, CTChartSpaceImpl.EXTERNALDATA$20, 0, (short)1);
    }
    
    public CTExternalData addNewExternalData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExternalData)this.get_store().add_element_user(CTChartSpaceImpl.EXTERNALDATA$20);
        }
    }
    
    public void unsetExternalData() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.EXTERNALDATA$20, 0);
        }
    }
    
    public CTPrintSettings getPrintSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPrintSettings ctPrintSettings = (CTPrintSettings)this.get_store().find_element_user(CTChartSpaceImpl.PRINTSETTINGS$22, 0);
            if (ctPrintSettings == null) {
                return null;
            }
            return ctPrintSettings;
        }
    }
    
    public boolean isSetPrintSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.PRINTSETTINGS$22) != 0;
        }
    }
    
    public void setPrintSettings(final CTPrintSettings ctPrintSettings) {
        this.generatedSetterHelperImpl((XmlObject)ctPrintSettings, CTChartSpaceImpl.PRINTSETTINGS$22, 0, (short)1);
    }
    
    public CTPrintSettings addNewPrintSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPrintSettings)this.get_store().add_element_user(CTChartSpaceImpl.PRINTSETTINGS$22);
        }
    }
    
    public void unsetPrintSettings() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.PRINTSETTINGS$22, 0);
        }
    }
    
    public CTRelId getUserShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRelId ctRelId = (CTRelId)this.get_store().find_element_user(CTChartSpaceImpl.USERSHAPES$24, 0);
            if (ctRelId == null) {
                return null;
            }
            return ctRelId;
        }
    }
    
    public boolean isSetUserShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.USERSHAPES$24) != 0;
        }
    }
    
    public void setUserShapes(final CTRelId ctRelId) {
        this.generatedSetterHelperImpl((XmlObject)ctRelId, CTChartSpaceImpl.USERSHAPES$24, 0, (short)1);
    }
    
    public CTRelId addNewUserShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRelId)this.get_store().add_element_user(CTChartSpaceImpl.USERSHAPES$24);
        }
    }
    
    public void unsetUserShapes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.USERSHAPES$24, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTChartSpaceImpl.EXTLST$26, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTChartSpaceImpl.EXTLST$26) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTChartSpaceImpl.EXTLST$26, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTChartSpaceImpl.EXTLST$26);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTChartSpaceImpl.EXTLST$26, 0);
        }
    }
    
    static {
        DATE1904$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "date1904");
        LANG$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "lang");
        ROUNDEDCORNERS$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "roundedCorners");
        STYLE$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "style");
        CLRMAPOVR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "clrMapOvr");
        PIVOTSOURCE$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pivotSource");
        PROTECTION$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "protection");
        CHART$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "chart");
        SPPR$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        TXPR$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "txPr");
        EXTERNALDATA$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "externalData");
        PRINTSETTINGS$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "printSettings");
        USERSHAPES$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "userShapes");
        EXTLST$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
