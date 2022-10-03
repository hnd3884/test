package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPieSer;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTPieSerImpl extends XmlComplexContentImpl implements CTPieSer
{
    private static final long serialVersionUID = 1L;
    private static final QName IDX$0;
    private static final QName ORDER$2;
    private static final QName TX$4;
    private static final QName SPPR$6;
    private static final QName EXPLOSION$8;
    private static final QName DPT$10;
    private static final QName DLBLS$12;
    private static final QName CAT$14;
    private static final QName VAL$16;
    private static final QName EXTLST$18;
    
    public CTPieSerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTUnsignedInt getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTPieSerImpl.IDX$0, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setIdx(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTPieSerImpl.IDX$0, 0, (short)1);
    }
    
    public CTUnsignedInt addNewIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTPieSerImpl.IDX$0);
        }
    }
    
    public CTUnsignedInt getOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTPieSerImpl.ORDER$2, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setOrder(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTPieSerImpl.ORDER$2, 0, (short)1);
    }
    
    public CTUnsignedInt addNewOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTPieSerImpl.ORDER$2);
        }
    }
    
    public CTSerTx getTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSerTx ctSerTx = (CTSerTx)this.get_store().find_element_user(CTPieSerImpl.TX$4, 0);
            if (ctSerTx == null) {
                return null;
            }
            return ctSerTx;
        }
    }
    
    public boolean isSetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPieSerImpl.TX$4) != 0;
        }
    }
    
    public void setTx(final CTSerTx ctSerTx) {
        this.generatedSetterHelperImpl((XmlObject)ctSerTx, CTPieSerImpl.TX$4, 0, (short)1);
    }
    
    public CTSerTx addNewTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSerTx)this.get_store().add_element_user(CTPieSerImpl.TX$4);
        }
    }
    
    public void unsetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPieSerImpl.TX$4, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTPieSerImpl.SPPR$6, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPieSerImpl.SPPR$6) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTPieSerImpl.SPPR$6, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTPieSerImpl.SPPR$6);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPieSerImpl.SPPR$6, 0);
        }
    }
    
    public CTUnsignedInt getExplosion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTPieSerImpl.EXPLOSION$8, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public boolean isSetExplosion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPieSerImpl.EXPLOSION$8) != 0;
        }
    }
    
    public void setExplosion(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTPieSerImpl.EXPLOSION$8, 0, (short)1);
    }
    
    public CTUnsignedInt addNewExplosion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTPieSerImpl.EXPLOSION$8);
        }
    }
    
    public void unsetExplosion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPieSerImpl.EXPLOSION$8, 0);
        }
    }
    
    public List<CTDPt> getDPtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DPtList extends AbstractList<CTDPt>
            {
                @Override
                public CTDPt get(final int n) {
                    return CTPieSerImpl.this.getDPtArray(n);
                }
                
                @Override
                public CTDPt set(final int n, final CTDPt ctdPt) {
                    final CTDPt dPtArray = CTPieSerImpl.this.getDPtArray(n);
                    CTPieSerImpl.this.setDPtArray(n, ctdPt);
                    return dPtArray;
                }
                
                @Override
                public void add(final int n, final CTDPt ctdPt) {
                    CTPieSerImpl.this.insertNewDPt(n).set((XmlObject)ctdPt);
                }
                
                @Override
                public CTDPt remove(final int n) {
                    final CTDPt dPtArray = CTPieSerImpl.this.getDPtArray(n);
                    CTPieSerImpl.this.removeDPt(n);
                    return dPtArray;
                }
                
                @Override
                public int size() {
                    return CTPieSerImpl.this.sizeOfDPtArray();
                }
            }
            return new DPtList();
        }
    }
    
    @Deprecated
    public CTDPt[] getDPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTPieSerImpl.DPT$10, (List)list);
            final CTDPt[] array = new CTDPt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDPt getDPtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDPt ctdPt = (CTDPt)this.get_store().find_element_user(CTPieSerImpl.DPT$10, n);
            if (ctdPt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctdPt;
        }
    }
    
    public int sizeOfDPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPieSerImpl.DPT$10);
        }
    }
    
    public void setDPtArray(final CTDPt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTPieSerImpl.DPT$10);
    }
    
    public void setDPtArray(final int n, final CTDPt ctdPt) {
        this.generatedSetterHelperImpl((XmlObject)ctdPt, CTPieSerImpl.DPT$10, n, (short)2);
    }
    
    public CTDPt insertNewDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().insert_element_user(CTPieSerImpl.DPT$10, n);
        }
    }
    
    public CTDPt addNewDPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().add_element_user(CTPieSerImpl.DPT$10);
        }
    }
    
    public void removeDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPieSerImpl.DPT$10, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTPieSerImpl.DLBLS$12, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPieSerImpl.DLBLS$12) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTPieSerImpl.DLBLS$12, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTPieSerImpl.DLBLS$12);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPieSerImpl.DLBLS$12, 0);
        }
    }
    
    public CTAxDataSource getCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAxDataSource ctAxDataSource = (CTAxDataSource)this.get_store().find_element_user(CTPieSerImpl.CAT$14, 0);
            if (ctAxDataSource == null) {
                return null;
            }
            return ctAxDataSource;
        }
    }
    
    public boolean isSetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPieSerImpl.CAT$14) != 0;
        }
    }
    
    public void setCat(final CTAxDataSource ctAxDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctAxDataSource, CTPieSerImpl.CAT$14, 0, (short)1);
    }
    
    public CTAxDataSource addNewCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAxDataSource)this.get_store().add_element_user(CTPieSerImpl.CAT$14);
        }
    }
    
    public void unsetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPieSerImpl.CAT$14, 0);
        }
    }
    
    public CTNumDataSource getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumDataSource ctNumDataSource = (CTNumDataSource)this.get_store().find_element_user(CTPieSerImpl.VAL$16, 0);
            if (ctNumDataSource == null) {
                return null;
            }
            return ctNumDataSource;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPieSerImpl.VAL$16) != 0;
        }
    }
    
    public void setVal(final CTNumDataSource ctNumDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctNumDataSource, CTPieSerImpl.VAL$16, 0, (short)1);
    }
    
    public CTNumDataSource addNewVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumDataSource)this.get_store().add_element_user(CTPieSerImpl.VAL$16);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPieSerImpl.VAL$16, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTPieSerImpl.EXTLST$18, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTPieSerImpl.EXTLST$18) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTPieSerImpl.EXTLST$18, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTPieSerImpl.EXTLST$18);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTPieSerImpl.EXTLST$18, 0);
        }
    }
    
    static {
        IDX$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "idx");
        ORDER$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "order");
        TX$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "tx");
        SPPR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        EXPLOSION$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "explosion");
        DPT$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dPt");
        DLBLS$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        CAT$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "cat");
        VAL$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "val");
        EXTLST$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
