package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.apache.xmlbeans.XmlString;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLblPos;
import org.openxmlformats.schemas.drawingml.x2006.main.CTTextBody;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumFmt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbl;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTDLblsImpl extends XmlComplexContentImpl implements CTDLbls
{
    private static final long serialVersionUID = 1L;
    private static final QName DLBL$0;
    private static final QName DELETE$2;
    private static final QName NUMFMT$4;
    private static final QName SPPR$6;
    private static final QName TXPR$8;
    private static final QName DLBLPOS$10;
    private static final QName SHOWLEGENDKEY$12;
    private static final QName SHOWVAL$14;
    private static final QName SHOWCATNAME$16;
    private static final QName SHOWSERNAME$18;
    private static final QName SHOWPERCENT$20;
    private static final QName SHOWBUBBLESIZE$22;
    private static final QName SEPARATOR$24;
    private static final QName SHOWLEADERLINES$26;
    private static final QName LEADERLINES$28;
    private static final QName EXTLST$30;
    
    public CTDLblsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTDLbl> getDLblList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DLblList extends AbstractList<CTDLbl>
            {
                @Override
                public CTDLbl get(final int n) {
                    return CTDLblsImpl.this.getDLblArray(n);
                }
                
                @Override
                public CTDLbl set(final int n, final CTDLbl ctdLbl) {
                    final CTDLbl dLblArray = CTDLblsImpl.this.getDLblArray(n);
                    CTDLblsImpl.this.setDLblArray(n, ctdLbl);
                    return dLblArray;
                }
                
                @Override
                public void add(final int n, final CTDLbl ctdLbl) {
                    CTDLblsImpl.this.insertNewDLbl(n).set((XmlObject)ctdLbl);
                }
                
                @Override
                public CTDLbl remove(final int n) {
                    final CTDLbl dLblArray = CTDLblsImpl.this.getDLblArray(n);
                    CTDLblsImpl.this.removeDLbl(n);
                    return dLblArray;
                }
                
                @Override
                public int size() {
                    return CTDLblsImpl.this.sizeOfDLblArray();
                }
            }
            return new DLblList();
        }
    }
    
    @Deprecated
    public CTDLbl[] getDLblArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTDLblsImpl.DLBL$0, (List)list);
            final CTDLbl[] array = new CTDLbl[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDLbl getDLblArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbl ctdLbl = (CTDLbl)this.get_store().find_element_user(CTDLblsImpl.DLBL$0, n);
            if (ctdLbl == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctdLbl;
        }
    }
    
    public int sizeOfDLblArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.DLBL$0);
        }
    }
    
    public void setDLblArray(final CTDLbl[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTDLblsImpl.DLBL$0);
    }
    
    public void setDLblArray(final int n, final CTDLbl ctdLbl) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbl, CTDLblsImpl.DLBL$0, n, (short)2);
    }
    
    public CTDLbl insertNewDLbl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbl)this.get_store().insert_element_user(CTDLblsImpl.DLBL$0, n);
        }
    }
    
    public CTDLbl addNewDLbl() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbl)this.get_store().add_element_user(CTDLblsImpl.DLBL$0);
        }
    }
    
    public void removeDLbl(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.DLBL$0, n);
        }
    }
    
    public CTBoolean getDelete() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTDLblsImpl.DELETE$2, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetDelete() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.DELETE$2) != 0;
        }
    }
    
    public void setDelete(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTDLblsImpl.DELETE$2, 0, (short)1);
    }
    
    public CTBoolean addNewDelete() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTDLblsImpl.DELETE$2);
        }
    }
    
    public void unsetDelete() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.DELETE$2, 0);
        }
    }
    
    public CTNumFmt getNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumFmt ctNumFmt = (CTNumFmt)this.get_store().find_element_user(CTDLblsImpl.NUMFMT$4, 0);
            if (ctNumFmt == null) {
                return null;
            }
            return ctNumFmt;
        }
    }
    
    public boolean isSetNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.NUMFMT$4) != 0;
        }
    }
    
    public void setNumFmt(final CTNumFmt ctNumFmt) {
        this.generatedSetterHelperImpl((XmlObject)ctNumFmt, CTDLblsImpl.NUMFMT$4, 0, (short)1);
    }
    
    public CTNumFmt addNewNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumFmt)this.get_store().add_element_user(CTDLblsImpl.NUMFMT$4);
        }
    }
    
    public void unsetNumFmt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.NUMFMT$4, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTDLblsImpl.SPPR$6, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.SPPR$6) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTDLblsImpl.SPPR$6, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTDLblsImpl.SPPR$6);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.SPPR$6, 0);
        }
    }
    
    public CTTextBody getTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTextBody ctTextBody = (CTTextBody)this.get_store().find_element_user(CTDLblsImpl.TXPR$8, 0);
            if (ctTextBody == null) {
                return null;
            }
            return ctTextBody;
        }
    }
    
    public boolean isSetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.TXPR$8) != 0;
        }
    }
    
    public void setTxPr(final CTTextBody ctTextBody) {
        this.generatedSetterHelperImpl((XmlObject)ctTextBody, CTDLblsImpl.TXPR$8, 0, (short)1);
    }
    
    public CTTextBody addNewTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTextBody)this.get_store().add_element_user(CTDLblsImpl.TXPR$8);
        }
    }
    
    public void unsetTxPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.TXPR$8, 0);
        }
    }
    
    public CTDLblPos getDLblPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLblPos ctdLblPos = (CTDLblPos)this.get_store().find_element_user(CTDLblsImpl.DLBLPOS$10, 0);
            if (ctdLblPos == null) {
                return null;
            }
            return ctdLblPos;
        }
    }
    
    public boolean isSetDLblPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.DLBLPOS$10) != 0;
        }
    }
    
    public void setDLblPos(final CTDLblPos ctdLblPos) {
        this.generatedSetterHelperImpl((XmlObject)ctdLblPos, CTDLblsImpl.DLBLPOS$10, 0, (short)1);
    }
    
    public CTDLblPos addNewDLblPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLblPos)this.get_store().add_element_user(CTDLblsImpl.DLBLPOS$10);
        }
    }
    
    public void unsetDLblPos() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.DLBLPOS$10, 0);
        }
    }
    
    public CTBoolean getShowLegendKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTDLblsImpl.SHOWLEGENDKEY$12, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetShowLegendKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.SHOWLEGENDKEY$12) != 0;
        }
    }
    
    public void setShowLegendKey(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTDLblsImpl.SHOWLEGENDKEY$12, 0, (short)1);
    }
    
    public CTBoolean addNewShowLegendKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTDLblsImpl.SHOWLEGENDKEY$12);
        }
    }
    
    public void unsetShowLegendKey() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.SHOWLEGENDKEY$12, 0);
        }
    }
    
    public CTBoolean getShowVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTDLblsImpl.SHOWVAL$14, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetShowVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.SHOWVAL$14) != 0;
        }
    }
    
    public void setShowVal(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTDLblsImpl.SHOWVAL$14, 0, (short)1);
    }
    
    public CTBoolean addNewShowVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTDLblsImpl.SHOWVAL$14);
        }
    }
    
    public void unsetShowVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.SHOWVAL$14, 0);
        }
    }
    
    public CTBoolean getShowCatName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTDLblsImpl.SHOWCATNAME$16, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetShowCatName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.SHOWCATNAME$16) != 0;
        }
    }
    
    public void setShowCatName(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTDLblsImpl.SHOWCATNAME$16, 0, (short)1);
    }
    
    public CTBoolean addNewShowCatName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTDLblsImpl.SHOWCATNAME$16);
        }
    }
    
    public void unsetShowCatName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.SHOWCATNAME$16, 0);
        }
    }
    
    public CTBoolean getShowSerName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTDLblsImpl.SHOWSERNAME$18, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetShowSerName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.SHOWSERNAME$18) != 0;
        }
    }
    
    public void setShowSerName(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTDLblsImpl.SHOWSERNAME$18, 0, (short)1);
    }
    
    public CTBoolean addNewShowSerName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTDLblsImpl.SHOWSERNAME$18);
        }
    }
    
    public void unsetShowSerName() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.SHOWSERNAME$18, 0);
        }
    }
    
    public CTBoolean getShowPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTDLblsImpl.SHOWPERCENT$20, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetShowPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.SHOWPERCENT$20) != 0;
        }
    }
    
    public void setShowPercent(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTDLblsImpl.SHOWPERCENT$20, 0, (short)1);
    }
    
    public CTBoolean addNewShowPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTDLblsImpl.SHOWPERCENT$20);
        }
    }
    
    public void unsetShowPercent() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.SHOWPERCENT$20, 0);
        }
    }
    
    public CTBoolean getShowBubbleSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTDLblsImpl.SHOWBUBBLESIZE$22, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetShowBubbleSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.SHOWBUBBLESIZE$22) != 0;
        }
    }
    
    public void setShowBubbleSize(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTDLblsImpl.SHOWBUBBLESIZE$22, 0, (short)1);
    }
    
    public CTBoolean addNewShowBubbleSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTDLblsImpl.SHOWBUBBLESIZE$22);
        }
    }
    
    public void unsetShowBubbleSize() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.SHOWBUBBLESIZE$22, 0);
        }
    }
    
    public String getSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTDLblsImpl.SEPARATOR$24, 0);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlString xgetSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlString)this.get_store().find_element_user(CTDLblsImpl.SEPARATOR$24, 0);
        }
    }
    
    public boolean isSetSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.SEPARATOR$24) != 0;
        }
    }
    
    public void setSeparator(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_element_user(CTDLblsImpl.SEPARATOR$24, 0);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_element_user(CTDLblsImpl.SEPARATOR$24);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetSeparator(final XmlString xmlString) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlString xmlString2 = (XmlString)this.get_store().find_element_user(CTDLblsImpl.SEPARATOR$24, 0);
            if (xmlString2 == null) {
                xmlString2 = (XmlString)this.get_store().add_element_user(CTDLblsImpl.SEPARATOR$24);
            }
            xmlString2.set((XmlObject)xmlString);
        }
    }
    
    public void unsetSeparator() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.SEPARATOR$24, 0);
        }
    }
    
    public CTBoolean getShowLeaderLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTDLblsImpl.SHOWLEADERLINES$26, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetShowLeaderLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.SHOWLEADERLINES$26) != 0;
        }
    }
    
    public void setShowLeaderLines(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTDLblsImpl.SHOWLEADERLINES$26, 0, (short)1);
    }
    
    public CTBoolean addNewShowLeaderLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTDLblsImpl.SHOWLEADERLINES$26);
        }
    }
    
    public void unsetShowLeaderLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.SHOWLEADERLINES$26, 0);
        }
    }
    
    public CTChartLines getLeaderLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartLines ctChartLines = (CTChartLines)this.get_store().find_element_user(CTDLblsImpl.LEADERLINES$28, 0);
            if (ctChartLines == null) {
                return null;
            }
            return ctChartLines;
        }
    }
    
    public boolean isSetLeaderLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.LEADERLINES$28) != 0;
        }
    }
    
    public void setLeaderLines(final CTChartLines ctChartLines) {
        this.generatedSetterHelperImpl((XmlObject)ctChartLines, CTDLblsImpl.LEADERLINES$28, 0, (short)1);
    }
    
    public CTChartLines addNewLeaderLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartLines)this.get_store().add_element_user(CTDLblsImpl.LEADERLINES$28);
        }
    }
    
    public void unsetLeaderLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.LEADERLINES$28, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTDLblsImpl.EXTLST$30, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTDLblsImpl.EXTLST$30) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTDLblsImpl.EXTLST$30, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTDLblsImpl.EXTLST$30);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTDLblsImpl.EXTLST$30, 0);
        }
    }
    
    static {
        DLBL$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbl");
        DELETE$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "delete");
        NUMFMT$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "numFmt");
        SPPR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        TXPR$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "txPr");
        DLBLPOS$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLblPos");
        SHOWLEGENDKEY$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "showLegendKey");
        SHOWVAL$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "showVal");
        SHOWCATNAME$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "showCatName");
        SHOWSERNAME$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "showSerName");
        SHOWPERCENT$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "showPercent");
        SHOWBUBBLESIZE$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "showBubbleSize");
        SEPARATOR$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "separator");
        SHOWLEADERLINES$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "showLeaderLines");
        LEADERLINES$28 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "leaderLines");
        EXTLST$30 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
