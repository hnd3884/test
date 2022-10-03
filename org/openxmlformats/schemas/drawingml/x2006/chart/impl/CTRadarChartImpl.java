package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarSer;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarStyle;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTRadarChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTRadarChartImpl extends XmlComplexContentImpl implements CTRadarChart
{
    private static final long serialVersionUID = 1L;
    private static final QName RADARSTYLE$0;
    private static final QName VARYCOLORS$2;
    private static final QName SER$4;
    private static final QName DLBLS$6;
    private static final QName AXID$8;
    private static final QName EXTLST$10;
    
    public CTRadarChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTRadarStyle getRadarStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRadarStyle ctRadarStyle = (CTRadarStyle)this.get_store().find_element_user(CTRadarChartImpl.RADARSTYLE$0, 0);
            if (ctRadarStyle == null) {
                return null;
            }
            return ctRadarStyle;
        }
    }
    
    public void setRadarStyle(final CTRadarStyle ctRadarStyle) {
        this.generatedSetterHelperImpl((XmlObject)ctRadarStyle, CTRadarChartImpl.RADARSTYLE$0, 0, (short)1);
    }
    
    public CTRadarStyle addNewRadarStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRadarStyle)this.get_store().add_element_user(CTRadarChartImpl.RADARSTYLE$0);
        }
    }
    
    public CTBoolean getVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTRadarChartImpl.VARYCOLORS$2, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRadarChartImpl.VARYCOLORS$2) != 0;
        }
    }
    
    public void setVaryColors(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTRadarChartImpl.VARYCOLORS$2, 0, (short)1);
    }
    
    public CTBoolean addNewVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTRadarChartImpl.VARYCOLORS$2);
        }
    }
    
    public void unsetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRadarChartImpl.VARYCOLORS$2, 0);
        }
    }
    
    public List<CTRadarSer> getSerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerList extends AbstractList<CTRadarSer>
            {
                @Override
                public CTRadarSer get(final int n) {
                    return CTRadarChartImpl.this.getSerArray(n);
                }
                
                @Override
                public CTRadarSer set(final int n, final CTRadarSer ctRadarSer) {
                    final CTRadarSer serArray = CTRadarChartImpl.this.getSerArray(n);
                    CTRadarChartImpl.this.setSerArray(n, ctRadarSer);
                    return serArray;
                }
                
                @Override
                public void add(final int n, final CTRadarSer ctRadarSer) {
                    CTRadarChartImpl.this.insertNewSer(n).set((XmlObject)ctRadarSer);
                }
                
                @Override
                public CTRadarSer remove(final int n) {
                    final CTRadarSer serArray = CTRadarChartImpl.this.getSerArray(n);
                    CTRadarChartImpl.this.removeSer(n);
                    return serArray;
                }
                
                @Override
                public int size() {
                    return CTRadarChartImpl.this.sizeOfSerArray();
                }
            }
            return new SerList();
        }
    }
    
    @Deprecated
    public CTRadarSer[] getSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTRadarChartImpl.SER$4, (List)list);
            final CTRadarSer[] array = new CTRadarSer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRadarSer getSerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRadarSer ctRadarSer = (CTRadarSer)this.get_store().find_element_user(CTRadarChartImpl.SER$4, n);
            if (ctRadarSer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctRadarSer;
        }
    }
    
    public int sizeOfSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRadarChartImpl.SER$4);
        }
    }
    
    public void setSerArray(final CTRadarSer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRadarChartImpl.SER$4);
    }
    
    public void setSerArray(final int n, final CTRadarSer ctRadarSer) {
        this.generatedSetterHelperImpl((XmlObject)ctRadarSer, CTRadarChartImpl.SER$4, n, (short)2);
    }
    
    public CTRadarSer insertNewSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRadarSer)this.get_store().insert_element_user(CTRadarChartImpl.SER$4, n);
        }
    }
    
    public CTRadarSer addNewSer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRadarSer)this.get_store().add_element_user(CTRadarChartImpl.SER$4);
        }
    }
    
    public void removeSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRadarChartImpl.SER$4, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTRadarChartImpl.DLBLS$6, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRadarChartImpl.DLBLS$6) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTRadarChartImpl.DLBLS$6, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTRadarChartImpl.DLBLS$6);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRadarChartImpl.DLBLS$6, 0);
        }
    }
    
    public List<CTUnsignedInt> getAxIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AxIdList extends AbstractList<CTUnsignedInt>
            {
                @Override
                public CTUnsignedInt get(final int n) {
                    return CTRadarChartImpl.this.getAxIdArray(n);
                }
                
                @Override
                public CTUnsignedInt set(final int n, final CTUnsignedInt ctUnsignedInt) {
                    final CTUnsignedInt axIdArray = CTRadarChartImpl.this.getAxIdArray(n);
                    CTRadarChartImpl.this.setAxIdArray(n, ctUnsignedInt);
                    return axIdArray;
                }
                
                @Override
                public void add(final int n, final CTUnsignedInt ctUnsignedInt) {
                    CTRadarChartImpl.this.insertNewAxId(n).set((XmlObject)ctUnsignedInt);
                }
                
                @Override
                public CTUnsignedInt remove(final int n) {
                    final CTUnsignedInt axIdArray = CTRadarChartImpl.this.getAxIdArray(n);
                    CTRadarChartImpl.this.removeAxId(n);
                    return axIdArray;
                }
                
                @Override
                public int size() {
                    return CTRadarChartImpl.this.sizeOfAxIdArray();
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
            this.get_store().find_all_element_users(CTRadarChartImpl.AXID$8, (List)list);
            final CTUnsignedInt[] array = new CTUnsignedInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnsignedInt getAxIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTRadarChartImpl.AXID$8, n);
            if (ctUnsignedInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnsignedInt;
        }
    }
    
    public int sizeOfAxIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRadarChartImpl.AXID$8);
        }
    }
    
    public void setAxIdArray(final CTUnsignedInt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTRadarChartImpl.AXID$8);
    }
    
    public void setAxIdArray(final int n, final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTRadarChartImpl.AXID$8, n, (short)2);
    }
    
    public CTUnsignedInt insertNewAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().insert_element_user(CTRadarChartImpl.AXID$8, n);
        }
    }
    
    public CTUnsignedInt addNewAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTRadarChartImpl.AXID$8);
        }
    }
    
    public void removeAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRadarChartImpl.AXID$8, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTRadarChartImpl.EXTLST$10, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTRadarChartImpl.EXTLST$10) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTRadarChartImpl.EXTLST$10, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTRadarChartImpl.EXTLST$10);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTRadarChartImpl.EXTLST$10, 0);
        }
    }
    
    static {
        RADARSTYLE$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "radarStyle");
        VARYCOLORS$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "varyColors");
        SER$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        DLBLS$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        AXID$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId");
        EXTLST$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
