package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaSer;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGrouping;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAreaChartImpl extends XmlComplexContentImpl implements CTAreaChart
{
    private static final long serialVersionUID = 1L;
    private static final QName GROUPING$0;
    private static final QName VARYCOLORS$2;
    private static final QName SER$4;
    private static final QName DLBLS$6;
    private static final QName DROPLINES$8;
    private static final QName AXID$10;
    private static final QName EXTLST$12;
    
    public CTAreaChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTGrouping getGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGrouping ctGrouping = (CTGrouping)this.get_store().find_element_user(CTAreaChartImpl.GROUPING$0, 0);
            if (ctGrouping == null) {
                return null;
            }
            return ctGrouping;
        }
    }
    
    public boolean isSetGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaChartImpl.GROUPING$0) != 0;
        }
    }
    
    public void setGrouping(final CTGrouping ctGrouping) {
        this.generatedSetterHelperImpl((XmlObject)ctGrouping, CTAreaChartImpl.GROUPING$0, 0, (short)1);
    }
    
    public CTGrouping addNewGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGrouping)this.get_store().add_element_user(CTAreaChartImpl.GROUPING$0);
        }
    }
    
    public void unsetGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaChartImpl.GROUPING$0, 0);
        }
    }
    
    public CTBoolean getVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTAreaChartImpl.VARYCOLORS$2, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaChartImpl.VARYCOLORS$2) != 0;
        }
    }
    
    public void setVaryColors(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTAreaChartImpl.VARYCOLORS$2, 0, (short)1);
    }
    
    public CTBoolean addNewVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTAreaChartImpl.VARYCOLORS$2);
        }
    }
    
    public void unsetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaChartImpl.VARYCOLORS$2, 0);
        }
    }
    
    public List<CTAreaSer> getSerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerList extends AbstractList<CTAreaSer>
            {
                @Override
                public CTAreaSer get(final int n) {
                    return CTAreaChartImpl.this.getSerArray(n);
                }
                
                @Override
                public CTAreaSer set(final int n, final CTAreaSer ctAreaSer) {
                    final CTAreaSer serArray = CTAreaChartImpl.this.getSerArray(n);
                    CTAreaChartImpl.this.setSerArray(n, ctAreaSer);
                    return serArray;
                }
                
                @Override
                public void add(final int n, final CTAreaSer ctAreaSer) {
                    CTAreaChartImpl.this.insertNewSer(n).set((XmlObject)ctAreaSer);
                }
                
                @Override
                public CTAreaSer remove(final int n) {
                    final CTAreaSer serArray = CTAreaChartImpl.this.getSerArray(n);
                    CTAreaChartImpl.this.removeSer(n);
                    return serArray;
                }
                
                @Override
                public int size() {
                    return CTAreaChartImpl.this.sizeOfSerArray();
                }
            }
            return new SerList();
        }
    }
    
    @Deprecated
    public CTAreaSer[] getSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTAreaChartImpl.SER$4, (List)list);
            final CTAreaSer[] array = new CTAreaSer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTAreaSer getSerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAreaSer ctAreaSer = (CTAreaSer)this.get_store().find_element_user(CTAreaChartImpl.SER$4, n);
            if (ctAreaSer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctAreaSer;
        }
    }
    
    public int sizeOfSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaChartImpl.SER$4);
        }
    }
    
    public void setSerArray(final CTAreaSer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTAreaChartImpl.SER$4);
    }
    
    public void setSerArray(final int n, final CTAreaSer ctAreaSer) {
        this.generatedSetterHelperImpl((XmlObject)ctAreaSer, CTAreaChartImpl.SER$4, n, (short)2);
    }
    
    public CTAreaSer insertNewSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAreaSer)this.get_store().insert_element_user(CTAreaChartImpl.SER$4, n);
        }
    }
    
    public CTAreaSer addNewSer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAreaSer)this.get_store().add_element_user(CTAreaChartImpl.SER$4);
        }
    }
    
    public void removeSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaChartImpl.SER$4, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTAreaChartImpl.DLBLS$6, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaChartImpl.DLBLS$6) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTAreaChartImpl.DLBLS$6, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTAreaChartImpl.DLBLS$6);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaChartImpl.DLBLS$6, 0);
        }
    }
    
    public CTChartLines getDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartLines ctChartLines = (CTChartLines)this.get_store().find_element_user(CTAreaChartImpl.DROPLINES$8, 0);
            if (ctChartLines == null) {
                return null;
            }
            return ctChartLines;
        }
    }
    
    public boolean isSetDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaChartImpl.DROPLINES$8) != 0;
        }
    }
    
    public void setDropLines(final CTChartLines ctChartLines) {
        this.generatedSetterHelperImpl((XmlObject)ctChartLines, CTAreaChartImpl.DROPLINES$8, 0, (short)1);
    }
    
    public CTChartLines addNewDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartLines)this.get_store().add_element_user(CTAreaChartImpl.DROPLINES$8);
        }
    }
    
    public void unsetDropLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaChartImpl.DROPLINES$8, 0);
        }
    }
    
    public List<CTUnsignedInt> getAxIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AxIdList extends AbstractList<CTUnsignedInt>
            {
                @Override
                public CTUnsignedInt get(final int n) {
                    return CTAreaChartImpl.this.getAxIdArray(n);
                }
                
                @Override
                public CTUnsignedInt set(final int n, final CTUnsignedInt ctUnsignedInt) {
                    final CTUnsignedInt axIdArray = CTAreaChartImpl.this.getAxIdArray(n);
                    CTAreaChartImpl.this.setAxIdArray(n, ctUnsignedInt);
                    return axIdArray;
                }
                
                @Override
                public void add(final int n, final CTUnsignedInt ctUnsignedInt) {
                    CTAreaChartImpl.this.insertNewAxId(n).set((XmlObject)ctUnsignedInt);
                }
                
                @Override
                public CTUnsignedInt remove(final int n) {
                    final CTUnsignedInt axIdArray = CTAreaChartImpl.this.getAxIdArray(n);
                    CTAreaChartImpl.this.removeAxId(n);
                    return axIdArray;
                }
                
                @Override
                public int size() {
                    return CTAreaChartImpl.this.sizeOfAxIdArray();
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
            this.get_store().find_all_element_users(CTAreaChartImpl.AXID$10, (List)list);
            final CTUnsignedInt[] array = new CTUnsignedInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnsignedInt getAxIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTAreaChartImpl.AXID$10, n);
            if (ctUnsignedInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnsignedInt;
        }
    }
    
    public int sizeOfAxIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaChartImpl.AXID$10);
        }
    }
    
    public void setAxIdArray(final CTUnsignedInt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTAreaChartImpl.AXID$10);
    }
    
    public void setAxIdArray(final int n, final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTAreaChartImpl.AXID$10, n, (short)2);
    }
    
    public CTUnsignedInt insertNewAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().insert_element_user(CTAreaChartImpl.AXID$10, n);
        }
    }
    
    public CTUnsignedInt addNewAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTAreaChartImpl.AXID$10);
        }
    }
    
    public void removeAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaChartImpl.AXID$10, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTAreaChartImpl.EXTLST$12, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaChartImpl.EXTLST$12) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTAreaChartImpl.EXTLST$12, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTAreaChartImpl.EXTLST$12);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaChartImpl.EXTLST$12, 0);
        }
    }
    
    static {
        GROUPING$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "grouping");
        VARYCOLORS$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "varyColors");
        SER$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        DLBLS$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        DROPLINES$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dropLines");
        AXID$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId");
        EXTLST$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
