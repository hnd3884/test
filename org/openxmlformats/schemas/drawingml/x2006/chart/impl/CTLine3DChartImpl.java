package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGapAmount;
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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLine3DChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLine3DChartImpl extends XmlComplexContentImpl implements CTLine3DChart
{
    private static final long serialVersionUID = 1L;
    private static final QName GROUPING$0;
    private static final QName VARYCOLORS$2;
    private static final QName SER$4;
    private static final QName DLBLS$6;
    private static final QName DROPLINES$8;
    private static final QName GAPDEPTH$10;
    private static final QName AXID$12;
    private static final QName EXTLST$14;
    
    public CTLine3DChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGrouping getGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGrouping ctGrouping = (CTGrouping)this.get_store().find_element_user(CTLine3DChartImpl.GROUPING$0, 0);
            if (ctGrouping == null) {
                return null;
            }
            return ctGrouping;
        }
    }
    
    public void setGrouping(final CTGrouping ctGrouping) {
        this.generatedSetterHelperImpl((XmlObject)ctGrouping, CTLine3DChartImpl.GROUPING$0, 0, (short)1);
    }
    
    public CTGrouping addNewGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrouping)this.get_store().add_element_user(CTLine3DChartImpl.GROUPING$0);
        }
    }
    
    public CTBoolean getVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTLine3DChartImpl.VARYCOLORS$2, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLine3DChartImpl.VARYCOLORS$2) != 0;
        }
    }
    
    public void setVaryColors(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTLine3DChartImpl.VARYCOLORS$2, 0, (short)1);
    }
    
    public CTBoolean addNewVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTLine3DChartImpl.VARYCOLORS$2);
        }
    }
    
    public void unsetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLine3DChartImpl.VARYCOLORS$2, 0);
        }
    }
    
    public List<CTLineSer> getSerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerList extends AbstractList<CTLineSer>
            {
                @Override
                public CTLineSer get(final int n) {
                    return CTLine3DChartImpl.this.getSerArray(n);
                }
                
                @Override
                public CTLineSer set(final int n, final CTLineSer ctLineSer) {
                    final CTLineSer serArray = CTLine3DChartImpl.this.getSerArray(n);
                    CTLine3DChartImpl.this.setSerArray(n, ctLineSer);
                    return serArray;
                }
                
                @Override
                public void add(final int n, final CTLineSer ctLineSer) {
                    CTLine3DChartImpl.this.insertNewSer(n).set((XmlObject)ctLineSer);
                }
                
                @Override
                public CTLineSer remove(final int n) {
                    final CTLineSer serArray = CTLine3DChartImpl.this.getSerArray(n);
                    CTLine3DChartImpl.this.removeSer(n);
                    return serArray;
                }
                
                @Override
                public int size() {
                    return CTLine3DChartImpl.this.sizeOfSerArray();
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
            this.get_store().find_all_element_users(CTLine3DChartImpl.SER$4, (List)list);
            final CTLineSer[] array = new CTLineSer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLineSer getSerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLineSer ctLineSer = (CTLineSer)this.get_store().find_element_user(CTLine3DChartImpl.SER$4, n);
            if (ctLineSer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLineSer;
        }
    }
    
    public int sizeOfSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLine3DChartImpl.SER$4);
        }
    }
    
    public void setSerArray(final CTLineSer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTLine3DChartImpl.SER$4);
    }
    
    public void setSerArray(final int n, final CTLineSer ctLineSer) {
        this.generatedSetterHelperImpl((XmlObject)ctLineSer, CTLine3DChartImpl.SER$4, n, (short)2);
    }
    
    public CTLineSer insertNewSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineSer)this.get_store().insert_element_user(CTLine3DChartImpl.SER$4, n);
        }
    }
    
    public CTLineSer addNewSer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLineSer)this.get_store().add_element_user(CTLine3DChartImpl.SER$4);
        }
    }
    
    public void removeSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLine3DChartImpl.SER$4, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTLine3DChartImpl.DLBLS$6, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLine3DChartImpl.DLBLS$6) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTLine3DChartImpl.DLBLS$6, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTLine3DChartImpl.DLBLS$6);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLine3DChartImpl.DLBLS$6, 0);
        }
    }
    
    public CTChartLines getDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartLines ctChartLines = (CTChartLines)this.get_store().find_element_user(CTLine3DChartImpl.DROPLINES$8, 0);
            if (ctChartLines == null) {
                return null;
            }
            return ctChartLines;
        }
    }
    
    public boolean isSetDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLine3DChartImpl.DROPLINES$8) != 0;
        }
    }
    
    public void setDropLines(final CTChartLines ctChartLines) {
        this.generatedSetterHelperImpl((XmlObject)ctChartLines, CTLine3DChartImpl.DROPLINES$8, 0, (short)1);
    }
    
    public CTChartLines addNewDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartLines)this.get_store().add_element_user(CTLine3DChartImpl.DROPLINES$8);
        }
    }
    
    public void unsetDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLine3DChartImpl.DROPLINES$8, 0);
        }
    }
    
    public CTGapAmount getGapDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGapAmount ctGapAmount = (CTGapAmount)this.get_store().find_element_user(CTLine3DChartImpl.GAPDEPTH$10, 0);
            if (ctGapAmount == null) {
                return null;
            }
            return ctGapAmount;
        }
    }
    
    public boolean isSetGapDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLine3DChartImpl.GAPDEPTH$10) != 0;
        }
    }
    
    public void setGapDepth(final CTGapAmount ctGapAmount) {
        this.generatedSetterHelperImpl((XmlObject)ctGapAmount, CTLine3DChartImpl.GAPDEPTH$10, 0, (short)1);
    }
    
    public CTGapAmount addNewGapDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGapAmount)this.get_store().add_element_user(CTLine3DChartImpl.GAPDEPTH$10);
        }
    }
    
    public void unsetGapDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLine3DChartImpl.GAPDEPTH$10, 0);
        }
    }
    
    public List<CTUnsignedInt> getAxIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AxIdList extends AbstractList<CTUnsignedInt>
            {
                @Override
                public CTUnsignedInt get(final int n) {
                    return CTLine3DChartImpl.this.getAxIdArray(n);
                }
                
                @Override
                public CTUnsignedInt set(final int n, final CTUnsignedInt ctUnsignedInt) {
                    final CTUnsignedInt axIdArray = CTLine3DChartImpl.this.getAxIdArray(n);
                    CTLine3DChartImpl.this.setAxIdArray(n, ctUnsignedInt);
                    return axIdArray;
                }
                
                @Override
                public void add(final int n, final CTUnsignedInt ctUnsignedInt) {
                    CTLine3DChartImpl.this.insertNewAxId(n).set((XmlObject)ctUnsignedInt);
                }
                
                @Override
                public CTUnsignedInt remove(final int n) {
                    final CTUnsignedInt axIdArray = CTLine3DChartImpl.this.getAxIdArray(n);
                    CTLine3DChartImpl.this.removeAxId(n);
                    return axIdArray;
                }
                
                @Override
                public int size() {
                    return CTLine3DChartImpl.this.sizeOfAxIdArray();
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
            this.get_store().find_all_element_users(CTLine3DChartImpl.AXID$12, (List)list);
            final CTUnsignedInt[] array = new CTUnsignedInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnsignedInt getAxIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTLine3DChartImpl.AXID$12, n);
            if (ctUnsignedInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnsignedInt;
        }
    }
    
    public int sizeOfAxIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLine3DChartImpl.AXID$12);
        }
    }
    
    public void setAxIdArray(final CTUnsignedInt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTLine3DChartImpl.AXID$12);
    }
    
    public void setAxIdArray(final int n, final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTLine3DChartImpl.AXID$12, n, (short)2);
    }
    
    public CTUnsignedInt insertNewAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().insert_element_user(CTLine3DChartImpl.AXID$12, n);
        }
    }
    
    public CTUnsignedInt addNewAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTLine3DChartImpl.AXID$12);
        }
    }
    
    public void removeAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLine3DChartImpl.AXID$12, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTLine3DChartImpl.EXTLST$14, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLine3DChartImpl.EXTLST$14) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTLine3DChartImpl.EXTLST$14, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTLine3DChartImpl.EXTLST$14);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLine3DChartImpl.EXTLST$14, 0);
        }
    }
    
    static {
        GROUPING$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "grouping");
        VARYCOLORS$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "varyColors");
        SER$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        DLBLS$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        DROPLINES$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dropLines");
        GAPDEPTH$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "gapDepth");
        AXID$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId");
        EXTLST$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
