package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUpDownBars;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGrouping;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLineChartImpl extends XmlComplexContentImpl implements CTLineChart
{
    private static final long serialVersionUID = 1L;
    private static final QName GROUPING$0;
    private static final QName VARYCOLORS$2;
    private static final QName SER$4;
    private static final QName DLBLS$6;
    private static final QName DROPLINES$8;
    private static final QName HILOWLINES$10;
    private static final QName UPDOWNBARS$12;
    private static final QName MARKER$14;
    private static final QName SMOOTH$16;
    private static final QName AXID$18;
    private static final QName EXTLST$20;
    
    public CTLineChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGrouping getGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGrouping ctGrouping = (CTGrouping)this.get_store().find_element_user(CTLineChartImpl.GROUPING$0, 0);
            if (ctGrouping == null) {
                return null;
            }
            return ctGrouping;
        }
    }
    
    public void setGrouping(final CTGrouping ctGrouping) {
        this.generatedSetterHelperImpl((XmlObject)ctGrouping, CTLineChartImpl.GROUPING$0, 0, (short)1);
    }
    
    public CTGrouping addNewGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrouping)this.get_store().add_element_user(CTLineChartImpl.GROUPING$0);
        }
    }
    
    public CTBoolean getVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTLineChartImpl.VARYCOLORS$2, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.VARYCOLORS$2) != 0;
        }
    }
    
    public void setVaryColors(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTLineChartImpl.VARYCOLORS$2, 0, (short)1);
    }
    
    public CTBoolean addNewVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTLineChartImpl.VARYCOLORS$2);
        }
    }
    
    public void unsetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.VARYCOLORS$2, 0);
        }
    }
    
    public List<CTLineSer> getSerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerList extends AbstractList<CTLineSer>
            {
                @Override
                public CTLineSer get(final int n) {
                    return CTLineChartImpl.this.getSerArray(n);
                }
                
                @Override
                public CTLineSer set(final int n, final CTLineSer ctLineSer) {
                    final CTLineSer serArray = CTLineChartImpl.this.getSerArray(n);
                    CTLineChartImpl.this.setSerArray(n, ctLineSer);
                    return serArray;
                }
                
                @Override
                public void add(final int n, final CTLineSer ctLineSer) {
                    CTLineChartImpl.this.insertNewSer(n).set((XmlObject)ctLineSer);
                }
                
                @Override
                public CTLineSer remove(final int n) {
                    final CTLineSer serArray = CTLineChartImpl.this.getSerArray(n);
                    CTLineChartImpl.this.removeSer(n);
                    return serArray;
                }
                
                @Override
                public int size() {
                    return CTLineChartImpl.this.sizeOfSerArray();
                }
            }
            return new SerList();
        }
    }
    
    @Deprecated
    public CTLineSer[] getSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTLineChartImpl.SER$4, (List)list);
            final CTLineSer[] array = new CTLineSer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLineSer getSerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineSer ctLineSer = (CTLineSer)this.get_store().find_element_user(CTLineChartImpl.SER$4, n);
            if (ctLineSer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLineSer;
        }
    }
    
    public int sizeOfSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.SER$4);
        }
    }
    
    public void setSerArray(final CTLineSer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTLineChartImpl.SER$4);
    }
    
    public void setSerArray(final int n, final CTLineSer ctLineSer) {
        this.generatedSetterHelperImpl((XmlObject)ctLineSer, CTLineChartImpl.SER$4, n, (short)2);
    }
    
    public CTLineSer insertNewSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineSer)this.get_store().insert_element_user(CTLineChartImpl.SER$4, n);
        }
    }
    
    public CTLineSer addNewSer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineSer)this.get_store().add_element_user(CTLineChartImpl.SER$4);
        }
    }
    
    public void removeSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.SER$4, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTLineChartImpl.DLBLS$6, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.DLBLS$6) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTLineChartImpl.DLBLS$6, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTLineChartImpl.DLBLS$6);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.DLBLS$6, 0);
        }
    }
    
    public CTChartLines getDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartLines ctChartLines = (CTChartLines)this.get_store().find_element_user(CTLineChartImpl.DROPLINES$8, 0);
            if (ctChartLines == null) {
                return null;
            }
            return ctChartLines;
        }
    }
    
    public boolean isSetDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.DROPLINES$8) != 0;
        }
    }
    
    public void setDropLines(final CTChartLines ctChartLines) {
        this.generatedSetterHelperImpl((XmlObject)ctChartLines, CTLineChartImpl.DROPLINES$8, 0, (short)1);
    }
    
    public CTChartLines addNewDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartLines)this.get_store().add_element_user(CTLineChartImpl.DROPLINES$8);
        }
    }
    
    public void unsetDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.DROPLINES$8, 0);
        }
    }
    
    public CTChartLines getHiLowLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartLines ctChartLines = (CTChartLines)this.get_store().find_element_user(CTLineChartImpl.HILOWLINES$10, 0);
            if (ctChartLines == null) {
                return null;
            }
            return ctChartLines;
        }
    }
    
    public boolean isSetHiLowLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.HILOWLINES$10) != 0;
        }
    }
    
    public void setHiLowLines(final CTChartLines ctChartLines) {
        this.generatedSetterHelperImpl((XmlObject)ctChartLines, CTLineChartImpl.HILOWLINES$10, 0, (short)1);
    }
    
    public CTChartLines addNewHiLowLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartLines)this.get_store().add_element_user(CTLineChartImpl.HILOWLINES$10);
        }
    }
    
    public void unsetHiLowLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.HILOWLINES$10, 0);
        }
    }
    
    public CTUpDownBars getUpDownBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUpDownBars ctUpDownBars = (CTUpDownBars)this.get_store().find_element_user(CTLineChartImpl.UPDOWNBARS$12, 0);
            if (ctUpDownBars == null) {
                return null;
            }
            return ctUpDownBars;
        }
    }
    
    public boolean isSetUpDownBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.UPDOWNBARS$12) != 0;
        }
    }
    
    public void setUpDownBars(final CTUpDownBars ctUpDownBars) {
        this.generatedSetterHelperImpl((XmlObject)ctUpDownBars, CTLineChartImpl.UPDOWNBARS$12, 0, (short)1);
    }
    
    public CTUpDownBars addNewUpDownBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUpDownBars)this.get_store().add_element_user(CTLineChartImpl.UPDOWNBARS$12);
        }
    }
    
    public void unsetUpDownBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.UPDOWNBARS$12, 0);
        }
    }
    
    public CTBoolean getMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTLineChartImpl.MARKER$14, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.MARKER$14) != 0;
        }
    }
    
    public void setMarker(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTLineChartImpl.MARKER$14, 0, (short)1);
    }
    
    public CTBoolean addNewMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTLineChartImpl.MARKER$14);
        }
    }
    
    public void unsetMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.MARKER$14, 0);
        }
    }
    
    public CTBoolean getSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTLineChartImpl.SMOOTH$16, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.SMOOTH$16) != 0;
        }
    }
    
    public void setSmooth(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTLineChartImpl.SMOOTH$16, 0, (short)1);
    }
    
    public CTBoolean addNewSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTLineChartImpl.SMOOTH$16);
        }
    }
    
    public void unsetSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.SMOOTH$16, 0);
        }
    }
    
    public List<CTUnsignedInt> getAxIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AxIdList extends AbstractList<CTUnsignedInt>
            {
                @Override
                public CTUnsignedInt get(final int n) {
                    return CTLineChartImpl.this.getAxIdArray(n);
                }
                
                @Override
                public CTUnsignedInt set(final int n, final CTUnsignedInt ctUnsignedInt) {
                    final CTUnsignedInt axIdArray = CTLineChartImpl.this.getAxIdArray(n);
                    CTLineChartImpl.this.setAxIdArray(n, ctUnsignedInt);
                    return axIdArray;
                }
                
                @Override
                public void add(final int n, final CTUnsignedInt ctUnsignedInt) {
                    CTLineChartImpl.this.insertNewAxId(n).set((XmlObject)ctUnsignedInt);
                }
                
                @Override
                public CTUnsignedInt remove(final int n) {
                    final CTUnsignedInt axIdArray = CTLineChartImpl.this.getAxIdArray(n);
                    CTLineChartImpl.this.removeAxId(n);
                    return axIdArray;
                }
                
                @Override
                public int size() {
                    return CTLineChartImpl.this.sizeOfAxIdArray();
                }
            }
            return new AxIdList();
        }
    }
    
    @Deprecated
    public CTUnsignedInt[] getAxIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTLineChartImpl.AXID$18, (List)list);
            final CTUnsignedInt[] array = new CTUnsignedInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnsignedInt getAxIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTLineChartImpl.AXID$18, n);
            if (ctUnsignedInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnsignedInt;
        }
    }
    
    public int sizeOfAxIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.AXID$18);
        }
    }
    
    public void setAxIdArray(final CTUnsignedInt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTLineChartImpl.AXID$18);
    }
    
    public void setAxIdArray(final int n, final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTLineChartImpl.AXID$18, n, (short)2);
    }
    
    public CTUnsignedInt insertNewAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().insert_element_user(CTLineChartImpl.AXID$18, n);
        }
    }
    
    public CTUnsignedInt addNewAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTLineChartImpl.AXID$18);
        }
    }
    
    public void removeAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.AXID$18, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTLineChartImpl.EXTLST$20, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineChartImpl.EXTLST$20) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTLineChartImpl.EXTLST$20, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTLineChartImpl.EXTLST$20);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineChartImpl.EXTLST$20, 0);
        }
    }
    
    static {
        GROUPING$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "grouping");
        VARYCOLORS$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "varyColors");
        SER$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        DLBLS$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        DROPLINES$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dropLines");
        HILOWLINES$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "hiLowLines");
        UPDOWNBARS$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "upDownBars");
        MARKER$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "marker");
        SMOOTH$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "smooth");
        AXID$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId");
        EXTLST$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
