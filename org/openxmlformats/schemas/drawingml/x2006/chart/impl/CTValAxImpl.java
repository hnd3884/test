package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDispUnits;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxisUnit;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrossBetween;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDouble;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTCrosses;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickLblPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTickMark;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTitle;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxPos;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScaling;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTValAx;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTValAxImpl extends XmlComplexContentImpl implements CTValAx
{
    private static final long serialVersionUID = 1L;
    private static final QName AXID$0;
    private static final QName SCALING$2;
    private static final QName DELETE$4;
    private static final QName AXPOS$6;
    private static final QName MAJORGRIDLINES$8;
    private static final QName MINORGRIDLINES$10;
    private static final QName TITLE$12;
    private static final QName NUMFMT$14;
    private static final QName MAJORTICKMARK$16;
    private static final QName MINORTICKMARK$18;
    private static final QName TICKLBLPOS$20;
    private static final QName SPPR$22;
    private static final QName TXPR$24;
    private static final QName CROSSAX$26;
    private static final QName CROSSES$28;
    private static final QName CROSSESAT$30;
    private static final QName CROSSBETWEEN$32;
    private static final QName MAJORUNIT$34;
    private static final QName MINORUNIT$36;
    private static final QName DISPUNITS$38;
    private static final QName EXTLST$40;
    
    public CTValAxImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTUnsignedInt getAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTValAxImpl.AXID$0, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setAxId(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTValAxImpl.AXID$0, 0, (short)1);
    }
    
    public CTUnsignedInt addNewAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTValAxImpl.AXID$0);
        }
    }
    
    public CTScaling getScaling() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScaling ctScaling = (CTScaling)this.get_store().find_element_user(CTValAxImpl.SCALING$2, 0);
            if (ctScaling == null) {
                return null;
            }
            return ctScaling;
        }
    }
    
    public void setScaling(final CTScaling ctScaling) {
        this.generatedSetterHelperImpl((XmlObject)ctScaling, CTValAxImpl.SCALING$2, 0, (short)1);
    }
    
    public CTScaling addNewScaling() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScaling)this.get_store().add_element_user(CTValAxImpl.SCALING$2);
        }
    }
    
    public CTBoolean getDelete() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTValAxImpl.DELETE$4, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetDelete() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.DELETE$4) != 0;
        }
    }
    
    public void setDelete(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTValAxImpl.DELETE$4, 0, (short)1);
    }
    
    public CTBoolean addNewDelete() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTValAxImpl.DELETE$4);
        }
    }
    
    public void unsetDelete() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.DELETE$4, 0);
        }
    }
    
    public CTAxPos getAxPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAxPos ctAxPos = (CTAxPos)this.get_store().find_element_user(CTValAxImpl.AXPOS$6, 0);
            if (ctAxPos == null) {
                return null;
            }
            return ctAxPos;
        }
    }
    
    public void setAxPos(final CTAxPos ctAxPos) {
        this.generatedSetterHelperImpl((XmlObject)ctAxPos, CTValAxImpl.AXPOS$6, 0, (short)1);
    }
    
    public CTAxPos addNewAxPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAxPos)this.get_store().add_element_user(CTValAxImpl.AXPOS$6);
        }
    }
    
    public CTChartLines getMajorGridlines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartLines ctChartLines = (CTChartLines)this.get_store().find_element_user(CTValAxImpl.MAJORGRIDLINES$8, 0);
            if (ctChartLines == null) {
                return null;
            }
            return ctChartLines;
        }
    }
    
    public boolean isSetMajorGridlines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.MAJORGRIDLINES$8) != 0;
        }
    }
    
    public void setMajorGridlines(final CTChartLines ctChartLines) {
        this.generatedSetterHelperImpl((XmlObject)ctChartLines, CTValAxImpl.MAJORGRIDLINES$8, 0, (short)1);
    }
    
    public CTChartLines addNewMajorGridlines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartLines)this.get_store().add_element_user(CTValAxImpl.MAJORGRIDLINES$8);
        }
    }
    
    public void unsetMajorGridlines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.MAJORGRIDLINES$8, 0);
        }
    }
    
    public CTChartLines getMinorGridlines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartLines ctChartLines = (CTChartLines)this.get_store().find_element_user(CTValAxImpl.MINORGRIDLINES$10, 0);
            if (ctChartLines == null) {
                return null;
            }
            return ctChartLines;
        }
    }
    
    public boolean isSetMinorGridlines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.MINORGRIDLINES$10) != 0;
        }
    }
    
    public void setMinorGridlines(final CTChartLines ctChartLines) {
        this.generatedSetterHelperImpl((XmlObject)ctChartLines, CTValAxImpl.MINORGRIDLINES$10, 0, (short)1);
    }
    
    public CTChartLines addNewMinorGridlines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartLines)this.get_store().add_element_user(CTValAxImpl.MINORGRIDLINES$10);
        }
    }
    
    public void unsetMinorGridlines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.MINORGRIDLINES$10, 0);
        }
    }
    
    public CTTitle getTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTitle ctTitle = (CTTitle)this.get_store().find_element_user(CTValAxImpl.TITLE$12, 0);
            if (ctTitle == null) {
                return null;
            }
            return ctTitle;
        }
    }
    
    public boolean isSetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.TITLE$12) != 0;
        }
    }
    
    public void setTitle(final CTTitle ctTitle) {
        this.generatedSetterHelperImpl((XmlObject)ctTitle, CTValAxImpl.TITLE$12, 0, (short)1);
    }
    
    public CTTitle addNewTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTitle)this.get_store().add_element_user(CTValAxImpl.TITLE$12);
        }
    }
    
    public void unsetTitle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.TITLE$12, 0);
        }
    }
    
    public CTNumFmt getNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumFmt ctNumFmt = (CTNumFmt)this.get_store().find_element_user(CTValAxImpl.NUMFMT$14, 0);
            if (ctNumFmt == null) {
                return null;
            }
            return ctNumFmt;
        }
    }
    
    public boolean isSetNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.NUMFMT$14) != 0;
        }
    }
    
    public void setNumFmt(final CTNumFmt ctNumFmt) {
        this.generatedSetterHelperImpl((XmlObject)ctNumFmt, CTValAxImpl.NUMFMT$14, 0, (short)1);
    }
    
    public CTNumFmt addNewNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumFmt)this.get_store().add_element_user(CTValAxImpl.NUMFMT$14);
        }
    }
    
    public void unsetNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.NUMFMT$14, 0);
        }
    }
    
    public CTTickMark getMajorTickMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTickMark ctTickMark = (CTTickMark)this.get_store().find_element_user(CTValAxImpl.MAJORTICKMARK$16, 0);
            if (ctTickMark == null) {
                return null;
            }
            return ctTickMark;
        }
    }
    
    public boolean isSetMajorTickMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.MAJORTICKMARK$16) != 0;
        }
    }
    
    public void setMajorTickMark(final CTTickMark ctTickMark) {
        this.generatedSetterHelperImpl((XmlObject)ctTickMark, CTValAxImpl.MAJORTICKMARK$16, 0, (short)1);
    }
    
    public CTTickMark addNewMajorTickMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTickMark)this.get_store().add_element_user(CTValAxImpl.MAJORTICKMARK$16);
        }
    }
    
    public void unsetMajorTickMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.MAJORTICKMARK$16, 0);
        }
    }
    
    public CTTickMark getMinorTickMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTickMark ctTickMark = (CTTickMark)this.get_store().find_element_user(CTValAxImpl.MINORTICKMARK$18, 0);
            if (ctTickMark == null) {
                return null;
            }
            return ctTickMark;
        }
    }
    
    public boolean isSetMinorTickMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.MINORTICKMARK$18) != 0;
        }
    }
    
    public void setMinorTickMark(final CTTickMark ctTickMark) {
        this.generatedSetterHelperImpl((XmlObject)ctTickMark, CTValAxImpl.MINORTICKMARK$18, 0, (short)1);
    }
    
    public CTTickMark addNewMinorTickMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTickMark)this.get_store().add_element_user(CTValAxImpl.MINORTICKMARK$18);
        }
    }
    
    public void unsetMinorTickMark() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.MINORTICKMARK$18, 0);
        }
    }
    
    public CTTickLblPos getTickLblPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTickLblPos ctTickLblPos = (CTTickLblPos)this.get_store().find_element_user(CTValAxImpl.TICKLBLPOS$20, 0);
            if (ctTickLblPos == null) {
                return null;
            }
            return ctTickLblPos;
        }
    }
    
    public boolean isSetTickLblPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.TICKLBLPOS$20) != 0;
        }
    }
    
    public void setTickLblPos(final CTTickLblPos ctTickLblPos) {
        this.generatedSetterHelperImpl((XmlObject)ctTickLblPos, CTValAxImpl.TICKLBLPOS$20, 0, (short)1);
    }
    
    public CTTickLblPos addNewTickLblPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTickLblPos)this.get_store().add_element_user(CTValAxImpl.TICKLBLPOS$20);
        }
    }
    
    public void unsetTickLblPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.TICKLBLPOS$20, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTValAxImpl.SPPR$22, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.SPPR$22) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTValAxImpl.SPPR$22, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTValAxImpl.SPPR$22);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.SPPR$22, 0);
        }
    }
    
    public CTTextBody getTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBody ctTextBody = (CTTextBody)this.get_store().find_element_user(CTValAxImpl.TXPR$24, 0);
            if (ctTextBody == null) {
                return null;
            }
            return ctTextBody;
        }
    }
    
    public boolean isSetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.TXPR$24) != 0;
        }
    }
    
    public void setTxPr(final CTTextBody ctTextBody) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBody, CTValAxImpl.TXPR$24, 0, (short)1);
    }
    
    public CTTextBody addNewTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBody)this.get_store().add_element_user(CTValAxImpl.TXPR$24);
        }
    }
    
    public void unsetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.TXPR$24, 0);
        }
    }
    
    public CTUnsignedInt getCrossAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTValAxImpl.CROSSAX$26, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setCrossAx(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTValAxImpl.CROSSAX$26, 0, (short)1);
    }
    
    public CTUnsignedInt addNewCrossAx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTValAxImpl.CROSSAX$26);
        }
    }
    
    public CTCrosses getCrosses() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCrosses ctCrosses = (CTCrosses)this.get_store().find_element_user(CTValAxImpl.CROSSES$28, 0);
            if (ctCrosses == null) {
                return null;
            }
            return ctCrosses;
        }
    }
    
    public boolean isSetCrosses() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.CROSSES$28) != 0;
        }
    }
    
    public void setCrosses(final CTCrosses ctCrosses) {
        this.generatedSetterHelperImpl((XmlObject)ctCrosses, CTValAxImpl.CROSSES$28, 0, (short)1);
    }
    
    public CTCrosses addNewCrosses() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCrosses)this.get_store().add_element_user(CTValAxImpl.CROSSES$28);
        }
    }
    
    public void unsetCrosses() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.CROSSES$28, 0);
        }
    }
    
    public CTDouble getCrossesAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDouble ctDouble = (CTDouble)this.get_store().find_element_user(CTValAxImpl.CROSSESAT$30, 0);
            if (ctDouble == null) {
                return null;
            }
            return ctDouble;
        }
    }
    
    public boolean isSetCrossesAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.CROSSESAT$30) != 0;
        }
    }
    
    public void setCrossesAt(final CTDouble ctDouble) {
        this.generatedSetterHelperImpl((XmlObject)ctDouble, CTValAxImpl.CROSSESAT$30, 0, (short)1);
    }
    
    public CTDouble addNewCrossesAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDouble)this.get_store().add_element_user(CTValAxImpl.CROSSESAT$30);
        }
    }
    
    public void unsetCrossesAt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.CROSSESAT$30, 0);
        }
    }
    
    public CTCrossBetween getCrossBetween() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCrossBetween ctCrossBetween = (CTCrossBetween)this.get_store().find_element_user(CTValAxImpl.CROSSBETWEEN$32, 0);
            if (ctCrossBetween == null) {
                return null;
            }
            return ctCrossBetween;
        }
    }
    
    public boolean isSetCrossBetween() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.CROSSBETWEEN$32) != 0;
        }
    }
    
    public void setCrossBetween(final CTCrossBetween ctCrossBetween) {
        this.generatedSetterHelperImpl((XmlObject)ctCrossBetween, CTValAxImpl.CROSSBETWEEN$32, 0, (short)1);
    }
    
    public CTCrossBetween addNewCrossBetween() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCrossBetween)this.get_store().add_element_user(CTValAxImpl.CROSSBETWEEN$32);
        }
    }
    
    public void unsetCrossBetween() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.CROSSBETWEEN$32, 0);
        }
    }
    
    public CTAxisUnit getMajorUnit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAxisUnit ctAxisUnit = (CTAxisUnit)this.get_store().find_element_user(CTValAxImpl.MAJORUNIT$34, 0);
            if (ctAxisUnit == null) {
                return null;
            }
            return ctAxisUnit;
        }
    }
    
    public boolean isSetMajorUnit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.MAJORUNIT$34) != 0;
        }
    }
    
    public void setMajorUnit(final CTAxisUnit ctAxisUnit) {
        this.generatedSetterHelperImpl((XmlObject)ctAxisUnit, CTValAxImpl.MAJORUNIT$34, 0, (short)1);
    }
    
    public CTAxisUnit addNewMajorUnit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAxisUnit)this.get_store().add_element_user(CTValAxImpl.MAJORUNIT$34);
        }
    }
    
    public void unsetMajorUnit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.MAJORUNIT$34, 0);
        }
    }
    
    public CTAxisUnit getMinorUnit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAxisUnit ctAxisUnit = (CTAxisUnit)this.get_store().find_element_user(CTValAxImpl.MINORUNIT$36, 0);
            if (ctAxisUnit == null) {
                return null;
            }
            return ctAxisUnit;
        }
    }
    
    public boolean isSetMinorUnit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.MINORUNIT$36) != 0;
        }
    }
    
    public void setMinorUnit(final CTAxisUnit ctAxisUnit) {
        this.generatedSetterHelperImpl((XmlObject)ctAxisUnit, CTValAxImpl.MINORUNIT$36, 0, (short)1);
    }
    
    public CTAxisUnit addNewMinorUnit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAxisUnit)this.get_store().add_element_user(CTValAxImpl.MINORUNIT$36);
        }
    }
    
    public void unsetMinorUnit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.MINORUNIT$36, 0);
        }
    }
    
    public CTDispUnits getDispUnits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDispUnits ctDispUnits = (CTDispUnits)this.get_store().find_element_user(CTValAxImpl.DISPUNITS$38, 0);
            if (ctDispUnits == null) {
                return null;
            }
            return ctDispUnits;
        }
    }
    
    public boolean isSetDispUnits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.DISPUNITS$38) != 0;
        }
    }
    
    public void setDispUnits(final CTDispUnits ctDispUnits) {
        this.generatedSetterHelperImpl((XmlObject)ctDispUnits, CTValAxImpl.DISPUNITS$38, 0, (short)1);
    }
    
    public CTDispUnits addNewDispUnits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDispUnits)this.get_store().add_element_user(CTValAxImpl.DISPUNITS$38);
        }
    }
    
    public void unsetDispUnits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.DISPUNITS$38, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTValAxImpl.EXTLST$40, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTValAxImpl.EXTLST$40) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTValAxImpl.EXTLST$40, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTValAxImpl.EXTLST$40);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTValAxImpl.EXTLST$40, 0);
        }
    }
    
    static {
        AXID$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId");
        SCALING$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "scaling");
        DELETE$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "delete");
        AXPOS$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axPos");
        MAJORGRIDLINES$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "majorGridlines");
        MINORGRIDLINES$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "minorGridlines");
        TITLE$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "title");
        NUMFMT$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "numFmt");
        MAJORTICKMARK$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "majorTickMark");
        MINORTICKMARK$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "minorTickMark");
        TICKLBLPOS$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "tickLblPos");
        SPPR$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        TXPR$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "txPr");
        CROSSAX$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "crossAx");
        CROSSES$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "crosses");
        CROSSESAT$30 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "crossesAt");
        CROSSBETWEEN$32 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "crossBetween");
        MAJORUNIT$34 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "majorUnit");
        MINORUNIT$36 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "minorUnit");
        DISPUNITS$38 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dispUnits");
        EXTLST$40 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
