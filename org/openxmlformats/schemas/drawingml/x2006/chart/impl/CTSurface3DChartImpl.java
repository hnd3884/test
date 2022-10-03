package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBandFmts;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurfaceSer;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSurface3DChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSurface3DChartImpl extends XmlComplexContentImpl implements CTSurface3DChart
{
    private static final long serialVersionUID = 1L;
    private static final QName WIREFRAME$0;
    private static final QName SER$2;
    private static final QName BANDFMTS$4;
    private static final QName AXID$6;
    private static final QName EXTLST$8;
    
    public CTSurface3DChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBoolean getWireframe() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTSurface3DChartImpl.WIREFRAME$0, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetWireframe() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurface3DChartImpl.WIREFRAME$0) != 0;
        }
    }
    
    public void setWireframe(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTSurface3DChartImpl.WIREFRAME$0, 0, (short)1);
    }
    
    public CTBoolean addNewWireframe() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTSurface3DChartImpl.WIREFRAME$0);
        }
    }
    
    public void unsetWireframe() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurface3DChartImpl.WIREFRAME$0, 0);
        }
    }
    
    public List<CTSurfaceSer> getSerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerList extends AbstractList<CTSurfaceSer>
            {
                @Override
                public CTSurfaceSer get(final int n) {
                    return CTSurface3DChartImpl.this.getSerArray(n);
                }
                
                @Override
                public CTSurfaceSer set(final int n, final CTSurfaceSer ctSurfaceSer) {
                    final CTSurfaceSer serArray = CTSurface3DChartImpl.this.getSerArray(n);
                    CTSurface3DChartImpl.this.setSerArray(n, ctSurfaceSer);
                    return serArray;
                }
                
                @Override
                public void add(final int n, final CTSurfaceSer ctSurfaceSer) {
                    CTSurface3DChartImpl.this.insertNewSer(n).set((XmlObject)ctSurfaceSer);
                }
                
                @Override
                public CTSurfaceSer remove(final int n) {
                    final CTSurfaceSer serArray = CTSurface3DChartImpl.this.getSerArray(n);
                    CTSurface3DChartImpl.this.removeSer(n);
                    return serArray;
                }
                
                @Override
                public int size() {
                    return CTSurface3DChartImpl.this.sizeOfSerArray();
                }
            }
            return new SerList();
        }
    }
    
    @Deprecated
    public CTSurfaceSer[] getSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSurface3DChartImpl.SER$2, (List)list);
            final CTSurfaceSer[] array = new CTSurfaceSer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSurfaceSer getSerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSurfaceSer ctSurfaceSer = (CTSurfaceSer)this.get_store().find_element_user(CTSurface3DChartImpl.SER$2, n);
            if (ctSurfaceSer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSurfaceSer;
        }
    }
    
    public int sizeOfSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurface3DChartImpl.SER$2);
        }
    }
    
    public void setSerArray(final CTSurfaceSer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSurface3DChartImpl.SER$2);
    }
    
    public void setSerArray(final int n, final CTSurfaceSer ctSurfaceSer) {
        this.generatedSetterHelperImpl((XmlObject)ctSurfaceSer, CTSurface3DChartImpl.SER$2, n, (short)2);
    }
    
    public CTSurfaceSer insertNewSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSurfaceSer)this.get_store().insert_element_user(CTSurface3DChartImpl.SER$2, n);
        }
    }
    
    public CTSurfaceSer addNewSer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSurfaceSer)this.get_store().add_element_user(CTSurface3DChartImpl.SER$2);
        }
    }
    
    public void removeSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurface3DChartImpl.SER$2, n);
        }
    }
    
    public CTBandFmts getBandFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBandFmts ctBandFmts = (CTBandFmts)this.get_store().find_element_user(CTSurface3DChartImpl.BANDFMTS$4, 0);
            if (ctBandFmts == null) {
                return null;
            }
            return ctBandFmts;
        }
    }
    
    public boolean isSetBandFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurface3DChartImpl.BANDFMTS$4) != 0;
        }
    }
    
    public void setBandFmts(final CTBandFmts ctBandFmts) {
        this.generatedSetterHelperImpl((XmlObject)ctBandFmts, CTSurface3DChartImpl.BANDFMTS$4, 0, (short)1);
    }
    
    public CTBandFmts addNewBandFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBandFmts)this.get_store().add_element_user(CTSurface3DChartImpl.BANDFMTS$4);
        }
    }
    
    public void unsetBandFmts() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurface3DChartImpl.BANDFMTS$4, 0);
        }
    }
    
    public List<CTUnsignedInt> getAxIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AxIdList extends AbstractList<CTUnsignedInt>
            {
                @Override
                public CTUnsignedInt get(final int n) {
                    return CTSurface3DChartImpl.this.getAxIdArray(n);
                }
                
                @Override
                public CTUnsignedInt set(final int n, final CTUnsignedInt ctUnsignedInt) {
                    final CTUnsignedInt axIdArray = CTSurface3DChartImpl.this.getAxIdArray(n);
                    CTSurface3DChartImpl.this.setAxIdArray(n, ctUnsignedInt);
                    return axIdArray;
                }
                
                @Override
                public void add(final int n, final CTUnsignedInt ctUnsignedInt) {
                    CTSurface3DChartImpl.this.insertNewAxId(n).set((XmlObject)ctUnsignedInt);
                }
                
                @Override
                public CTUnsignedInt remove(final int n) {
                    final CTUnsignedInt axIdArray = CTSurface3DChartImpl.this.getAxIdArray(n);
                    CTSurface3DChartImpl.this.removeAxId(n);
                    return axIdArray;
                }
                
                @Override
                public int size() {
                    return CTSurface3DChartImpl.this.sizeOfAxIdArray();
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
            this.get_store().find_all_element_users(CTSurface3DChartImpl.AXID$6, (List)list);
            final CTUnsignedInt[] array = new CTUnsignedInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnsignedInt getAxIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTSurface3DChartImpl.AXID$6, n);
            if (ctUnsignedInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnsignedInt;
        }
    }
    
    public int sizeOfAxIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurface3DChartImpl.AXID$6);
        }
    }
    
    public void setAxIdArray(final CTUnsignedInt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSurface3DChartImpl.AXID$6);
    }
    
    public void setAxIdArray(final int n, final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTSurface3DChartImpl.AXID$6, n, (short)2);
    }
    
    public CTUnsignedInt insertNewAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().insert_element_user(CTSurface3DChartImpl.AXID$6, n);
        }
    }
    
    public CTUnsignedInt addNewAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTSurface3DChartImpl.AXID$6);
        }
    }
    
    public void removeAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurface3DChartImpl.AXID$6, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTSurface3DChartImpl.EXTLST$8, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSurface3DChartImpl.EXTLST$8) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSurface3DChartImpl.EXTLST$8, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTSurface3DChartImpl.EXTLST$8);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSurface3DChartImpl.EXTLST$8, 0);
        }
    }
    
    static {
        WIREFRAME$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "wireframe");
        SER$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        BANDFMTS$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "bandFmts");
        AXID$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId");
        EXTLST$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
