package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import java.util.List;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPie3DChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPie3DChartImpl extends XmlComplexContentImpl implements CTPie3DChart
{
    private static final long serialVersionUID = 1L;
    private static final QName VARYCOLORS$0;
    private static final QName SER$2;
    private static final QName DLBLS$4;
    private static final QName EXTLST$6;
    
    public CTPie3DChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBoolean getVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTPie3DChartImpl.VARYCOLORS$0, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPie3DChartImpl.VARYCOLORS$0) != 0;
        }
    }
    
    public void setVaryColors(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTPie3DChartImpl.VARYCOLORS$0, 0, (short)1);
    }
    
    public CTBoolean addNewVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTPie3DChartImpl.VARYCOLORS$0);
        }
    }
    
    public void unsetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPie3DChartImpl.VARYCOLORS$0, 0);
        }
    }
    
    public List<CTPieSer> getSerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerList extends AbstractList<CTPieSer>
            {
                @Override
                public CTPieSer get(final int n) {
                    return CTPie3DChartImpl.this.getSerArray(n);
                }
                
                @Override
                public CTPieSer set(final int n, final CTPieSer ctPieSer) {
                    final CTPieSer serArray = CTPie3DChartImpl.this.getSerArray(n);
                    CTPie3DChartImpl.this.setSerArray(n, ctPieSer);
                    return serArray;
                }
                
                @Override
                public void add(final int n, final CTPieSer ctPieSer) {
                    CTPie3DChartImpl.this.insertNewSer(n).set((XmlObject)ctPieSer);
                }
                
                @Override
                public CTPieSer remove(final int n) {
                    final CTPieSer serArray = CTPie3DChartImpl.this.getSerArray(n);
                    CTPie3DChartImpl.this.removeSer(n);
                    return serArray;
                }
                
                @Override
                public int size() {
                    return CTPie3DChartImpl.this.sizeOfSerArray();
                }
            }
            return new SerList();
        }
    }
    
    @Deprecated
    public CTPieSer[] getSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPie3DChartImpl.SER$2, (List)list);
            final CTPieSer[] array = new CTPieSer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPieSer getSerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPieSer ctPieSer = (CTPieSer)this.get_store().find_element_user(CTPie3DChartImpl.SER$2, n);
            if (ctPieSer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPieSer;
        }
    }
    
    public int sizeOfSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPie3DChartImpl.SER$2);
        }
    }
    
    public void setSerArray(final CTPieSer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPie3DChartImpl.SER$2);
    }
    
    public void setSerArray(final int n, final CTPieSer ctPieSer) {
        this.generatedSetterHelperImpl((XmlObject)ctPieSer, CTPie3DChartImpl.SER$2, n, (short)2);
    }
    
    public CTPieSer insertNewSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPieSer)this.get_store().insert_element_user(CTPie3DChartImpl.SER$2, n);
        }
    }
    
    public CTPieSer addNewSer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPieSer)this.get_store().add_element_user(CTPie3DChartImpl.SER$2);
        }
    }
    
    public void removeSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPie3DChartImpl.SER$2, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTPie3DChartImpl.DLBLS$4, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPie3DChartImpl.DLBLS$4) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTPie3DChartImpl.DLBLS$4, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTPie3DChartImpl.DLBLS$4);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPie3DChartImpl.DLBLS$4, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTPie3DChartImpl.EXTLST$6, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPie3DChartImpl.EXTLST$6) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPie3DChartImpl.EXTLST$6, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTPie3DChartImpl.EXTLST$6);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPie3DChartImpl.EXTLST$6, 0);
        }
    }
    
    static {
        VARYCOLORS$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "varyColors");
        SER$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        DLBLS$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        EXTLST$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
