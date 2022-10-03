package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterSer;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTScatterChartImpl extends XmlComplexContentImpl implements CTScatterChart
{
    private static final long serialVersionUID = 1L;
    private static final QName SCATTERSTYLE$0;
    private static final QName VARYCOLORS$2;
    private static final QName SER$4;
    private static final QName DLBLS$6;
    private static final QName AXID$8;
    private static final QName EXTLST$10;
    
    public CTScatterChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTScatterStyle getScatterStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScatterStyle ctScatterStyle = (CTScatterStyle)this.get_store().find_element_user(CTScatterChartImpl.SCATTERSTYLE$0, 0);
            if (ctScatterStyle == null) {
                return null;
            }
            return ctScatterStyle;
        }
    }
    
    public void setScatterStyle(final CTScatterStyle ctScatterStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctScatterStyle, CTScatterChartImpl.SCATTERSTYLE$0, 0, (short)1);
    }
    
    public CTScatterStyle addNewScatterStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScatterStyle)this.get_store().add_element_user(CTScatterChartImpl.SCATTERSTYLE$0);
        }
    }
    
    public CTBoolean getVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTScatterChartImpl.VARYCOLORS$2, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterChartImpl.VARYCOLORS$2) != 0;
        }
    }
    
    public void setVaryColors(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTScatterChartImpl.VARYCOLORS$2, 0, (short)1);
    }
    
    public CTBoolean addNewVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTScatterChartImpl.VARYCOLORS$2);
        }
    }
    
    public void unsetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterChartImpl.VARYCOLORS$2, 0);
        }
    }
    
    public List<CTScatterSer> getSerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerList extends AbstractList<CTScatterSer>
            {
                @Override
                public CTScatterSer get(final int n) {
                    return CTScatterChartImpl.this.getSerArray(n);
                }
                
                @Override
                public CTScatterSer set(final int n, final CTScatterSer ctScatterSer) {
                    final CTScatterSer serArray = CTScatterChartImpl.this.getSerArray(n);
                    CTScatterChartImpl.this.setSerArray(n, ctScatterSer);
                    return serArray;
                }
                
                @Override
                public void add(final int n, final CTScatterSer ctScatterSer) {
                    CTScatterChartImpl.this.insertNewSer(n).set((XmlObject)ctScatterSer);
                }
                
                @Override
                public CTScatterSer remove(final int n) {
                    final CTScatterSer serArray = CTScatterChartImpl.this.getSerArray(n);
                    CTScatterChartImpl.this.removeSer(n);
                    return serArray;
                }
                
                @Override
                public int size() {
                    return CTScatterChartImpl.this.sizeOfSerArray();
                }
            }
            return new SerList();
        }
    }
    
    @Deprecated
    public CTScatterSer[] getSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTScatterChartImpl.SER$4, (List)list);
            final CTScatterSer[] array = new CTScatterSer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTScatterSer getSerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTScatterSer ctScatterSer = (CTScatterSer)this.get_store().find_element_user(CTScatterChartImpl.SER$4, n);
            if (ctScatterSer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctScatterSer;
        }
    }
    
    public int sizeOfSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterChartImpl.SER$4);
        }
    }
    
    public void setSerArray(final CTScatterSer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScatterChartImpl.SER$4);
    }
    
    public void setSerArray(final int n, final CTScatterSer ctScatterSer) {
        this.generatedSetterHelperImpl((XmlObject)ctScatterSer, CTScatterChartImpl.SER$4, n, (short)2);
    }
    
    public CTScatterSer insertNewSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScatterSer)this.get_store().insert_element_user(CTScatterChartImpl.SER$4, n);
        }
    }
    
    public CTScatterSer addNewSer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTScatterSer)this.get_store().add_element_user(CTScatterChartImpl.SER$4);
        }
    }
    
    public void removeSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterChartImpl.SER$4, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTScatterChartImpl.DLBLS$6, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterChartImpl.DLBLS$6) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTScatterChartImpl.DLBLS$6, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTScatterChartImpl.DLBLS$6);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterChartImpl.DLBLS$6, 0);
        }
    }
    
    public List<CTUnsignedInt> getAxIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AxIdList extends AbstractList<CTUnsignedInt>
            {
                @Override
                public CTUnsignedInt get(final int n) {
                    return CTScatterChartImpl.this.getAxIdArray(n);
                }
                
                @Override
                public CTUnsignedInt set(final int n, final CTUnsignedInt ctUnsignedInt) {
                    final CTUnsignedInt axIdArray = CTScatterChartImpl.this.getAxIdArray(n);
                    CTScatterChartImpl.this.setAxIdArray(n, ctUnsignedInt);
                    return axIdArray;
                }
                
                @Override
                public void add(final int n, final CTUnsignedInt ctUnsignedInt) {
                    CTScatterChartImpl.this.insertNewAxId(n).set((XmlObject)ctUnsignedInt);
                }
                
                @Override
                public CTUnsignedInt remove(final int n) {
                    final CTUnsignedInt axIdArray = CTScatterChartImpl.this.getAxIdArray(n);
                    CTScatterChartImpl.this.removeAxId(n);
                    return axIdArray;
                }
                
                @Override
                public int size() {
                    return CTScatterChartImpl.this.sizeOfAxIdArray();
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
            this.get_store().find_all_element_users(CTScatterChartImpl.AXID$8, (List)list);
            final CTUnsignedInt[] array = new CTUnsignedInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnsignedInt getAxIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTScatterChartImpl.AXID$8, n);
            if (ctUnsignedInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnsignedInt;
        }
    }
    
    public int sizeOfAxIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterChartImpl.AXID$8);
        }
    }
    
    public void setAxIdArray(final CTUnsignedInt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScatterChartImpl.AXID$8);
    }
    
    public void setAxIdArray(final int n, final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTScatterChartImpl.AXID$8, n, (short)2);
    }
    
    public CTUnsignedInt insertNewAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().insert_element_user(CTScatterChartImpl.AXID$8, n);
        }
    }
    
    public CTUnsignedInt addNewAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTScatterChartImpl.AXID$8);
        }
    }
    
    public void removeAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterChartImpl.AXID$8, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTScatterChartImpl.EXTLST$10, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterChartImpl.EXTLST$10) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTScatterChartImpl.EXTLST$10, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTScatterChartImpl.EXTLST$10);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterChartImpl.EXTLST$10, 0);
        }
    }
    
    static {
        SCATTERSTYLE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "scatterStyle");
        VARYCOLORS$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "varyColors");
        SER$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        DLBLS$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        AXID$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId");
        EXTLST$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
