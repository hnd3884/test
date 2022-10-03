package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrBars;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTrendline;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTPictureOptions;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAreaSer;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTAreaSerImpl extends XmlComplexContentImpl implements CTAreaSer
{
    private static final long serialVersionUID = 1L;
    private static final QName IDX$0;
    private static final QName ORDER$2;
    private static final QName TX$4;
    private static final QName SPPR$6;
    private static final QName PICTUREOPTIONS$8;
    private static final QName DPT$10;
    private static final QName DLBLS$12;
    private static final QName TRENDLINE$14;
    private static final QName ERRBARS$16;
    private static final QName CAT$18;
    private static final QName VAL$20;
    private static final QName EXTLST$22;
    
    public CTAreaSerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTUnsignedInt getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTAreaSerImpl.IDX$0, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setIdx(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTAreaSerImpl.IDX$0, 0, (short)1);
    }
    
    public CTUnsignedInt addNewIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTAreaSerImpl.IDX$0);
        }
    }
    
    public CTUnsignedInt getOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTAreaSerImpl.ORDER$2, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setOrder(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTAreaSerImpl.ORDER$2, 0, (short)1);
    }
    
    public CTUnsignedInt addNewOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTAreaSerImpl.ORDER$2);
        }
    }
    
    public CTSerTx getTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSerTx ctSerTx = (CTSerTx)this.get_store().find_element_user(CTAreaSerImpl.TX$4, 0);
            if (ctSerTx == null) {
                return null;
            }
            return ctSerTx;
        }
    }
    
    public boolean isSetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.TX$4) != 0;
        }
    }
    
    public void setTx(final CTSerTx ctSerTx) {
        this.generatedSetterHelperImpl((XmlObject)ctSerTx, CTAreaSerImpl.TX$4, 0, (short)1);
    }
    
    public CTSerTx addNewTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSerTx)this.get_store().add_element_user(CTAreaSerImpl.TX$4);
        }
    }
    
    public void unsetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.TX$4, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTAreaSerImpl.SPPR$6, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.SPPR$6) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTAreaSerImpl.SPPR$6, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTAreaSerImpl.SPPR$6);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.SPPR$6, 0);
        }
    }
    
    public CTPictureOptions getPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPictureOptions ctPictureOptions = (CTPictureOptions)this.get_store().find_element_user(CTAreaSerImpl.PICTUREOPTIONS$8, 0);
            if (ctPictureOptions == null) {
                return null;
            }
            return ctPictureOptions;
        }
    }
    
    public boolean isSetPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.PICTUREOPTIONS$8) != 0;
        }
    }
    
    public void setPictureOptions(final CTPictureOptions ctPictureOptions) {
        this.generatedSetterHelperImpl((XmlObject)ctPictureOptions, CTAreaSerImpl.PICTUREOPTIONS$8, 0, (short)1);
    }
    
    public CTPictureOptions addNewPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPictureOptions)this.get_store().add_element_user(CTAreaSerImpl.PICTUREOPTIONS$8);
        }
    }
    
    public void unsetPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.PICTUREOPTIONS$8, 0);
        }
    }
    
    public List<CTDPt> getDPtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DPtList extends AbstractList<CTDPt>
            {
                @Override
                public CTDPt get(final int n) {
                    return CTAreaSerImpl.this.getDPtArray(n);
                }
                
                @Override
                public CTDPt set(final int n, final CTDPt ctdPt) {
                    final CTDPt dPtArray = CTAreaSerImpl.this.getDPtArray(n);
                    CTAreaSerImpl.this.setDPtArray(n, ctdPt);
                    return dPtArray;
                }
                
                @Override
                public void add(final int n, final CTDPt ctdPt) {
                    CTAreaSerImpl.this.insertNewDPt(n).set((XmlObject)ctdPt);
                }
                
                @Override
                public CTDPt remove(final int n) {
                    final CTDPt dPtArray = CTAreaSerImpl.this.getDPtArray(n);
                    CTAreaSerImpl.this.removeDPt(n);
                    return dPtArray;
                }
                
                @Override
                public int size() {
                    return CTAreaSerImpl.this.sizeOfDPtArray();
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
            this.get_store().find_all_element_users(CTAreaSerImpl.DPT$10, (List)list);
            final CTDPt[] array = new CTDPt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDPt getDPtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDPt ctdPt = (CTDPt)this.get_store().find_element_user(CTAreaSerImpl.DPT$10, n);
            if (ctdPt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctdPt;
        }
    }
    
    public int sizeOfDPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.DPT$10);
        }
    }
    
    public void setDPtArray(final CTDPt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTAreaSerImpl.DPT$10);
    }
    
    public void setDPtArray(final int n, final CTDPt ctdPt) {
        this.generatedSetterHelperImpl((XmlObject)ctdPt, CTAreaSerImpl.DPT$10, n, (short)2);
    }
    
    public CTDPt insertNewDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().insert_element_user(CTAreaSerImpl.DPT$10, n);
        }
    }
    
    public CTDPt addNewDPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().add_element_user(CTAreaSerImpl.DPT$10);
        }
    }
    
    public void removeDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.DPT$10, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTAreaSerImpl.DLBLS$12, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.DLBLS$12) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTAreaSerImpl.DLBLS$12, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTAreaSerImpl.DLBLS$12);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.DLBLS$12, 0);
        }
    }
    
    public List<CTTrendline> getTrendlineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TrendlineList extends AbstractList<CTTrendline>
            {
                @Override
                public CTTrendline get(final int n) {
                    return CTAreaSerImpl.this.getTrendlineArray(n);
                }
                
                @Override
                public CTTrendline set(final int n, final CTTrendline ctTrendline) {
                    final CTTrendline trendlineArray = CTAreaSerImpl.this.getTrendlineArray(n);
                    CTAreaSerImpl.this.setTrendlineArray(n, ctTrendline);
                    return trendlineArray;
                }
                
                @Override
                public void add(final int n, final CTTrendline ctTrendline) {
                    CTAreaSerImpl.this.insertNewTrendline(n).set((XmlObject)ctTrendline);
                }
                
                @Override
                public CTTrendline remove(final int n) {
                    final CTTrendline trendlineArray = CTAreaSerImpl.this.getTrendlineArray(n);
                    CTAreaSerImpl.this.removeTrendline(n);
                    return trendlineArray;
                }
                
                @Override
                public int size() {
                    return CTAreaSerImpl.this.sizeOfTrendlineArray();
                }
            }
            return new TrendlineList();
        }
    }
    
    @Deprecated
    public CTTrendline[] getTrendlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTAreaSerImpl.TRENDLINE$14, (List)list);
            final CTTrendline[] array = new CTTrendline[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrendline getTrendlineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrendline ctTrendline = (CTTrendline)this.get_store().find_element_user(CTAreaSerImpl.TRENDLINE$14, n);
            if (ctTrendline == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrendline;
        }
    }
    
    public int sizeOfTrendlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.TRENDLINE$14);
        }
    }
    
    public void setTrendlineArray(final CTTrendline[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTAreaSerImpl.TRENDLINE$14);
    }
    
    public void setTrendlineArray(final int n, final CTTrendline ctTrendline) {
        this.generatedSetterHelperImpl((XmlObject)ctTrendline, CTAreaSerImpl.TRENDLINE$14, n, (short)2);
    }
    
    public CTTrendline insertNewTrendline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrendline)this.get_store().insert_element_user(CTAreaSerImpl.TRENDLINE$14, n);
        }
    }
    
    public CTTrendline addNewTrendline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrendline)this.get_store().add_element_user(CTAreaSerImpl.TRENDLINE$14);
        }
    }
    
    public void removeTrendline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.TRENDLINE$14, n);
        }
    }
    
    public List<CTErrBars> getErrBarsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ErrBarsList extends AbstractList<CTErrBars>
            {
                @Override
                public CTErrBars get(final int n) {
                    return CTAreaSerImpl.this.getErrBarsArray(n);
                }
                
                @Override
                public CTErrBars set(final int n, final CTErrBars ctErrBars) {
                    final CTErrBars errBarsArray = CTAreaSerImpl.this.getErrBarsArray(n);
                    CTAreaSerImpl.this.setErrBarsArray(n, ctErrBars);
                    return errBarsArray;
                }
                
                @Override
                public void add(final int n, final CTErrBars ctErrBars) {
                    CTAreaSerImpl.this.insertNewErrBars(n).set((XmlObject)ctErrBars);
                }
                
                @Override
                public CTErrBars remove(final int n) {
                    final CTErrBars errBarsArray = CTAreaSerImpl.this.getErrBarsArray(n);
                    CTAreaSerImpl.this.removeErrBars(n);
                    return errBarsArray;
                }
                
                @Override
                public int size() {
                    return CTAreaSerImpl.this.sizeOfErrBarsArray();
                }
            }
            return new ErrBarsList();
        }
    }
    
    @Deprecated
    public CTErrBars[] getErrBarsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTAreaSerImpl.ERRBARS$16, (List)list);
            final CTErrBars[] array = new CTErrBars[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTErrBars getErrBarsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTErrBars ctErrBars = (CTErrBars)this.get_store().find_element_user(CTAreaSerImpl.ERRBARS$16, n);
            if (ctErrBars == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctErrBars;
        }
    }
    
    public int sizeOfErrBarsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.ERRBARS$16);
        }
    }
    
    public void setErrBarsArray(final CTErrBars[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTAreaSerImpl.ERRBARS$16);
    }
    
    public void setErrBarsArray(final int n, final CTErrBars ctErrBars) {
        this.generatedSetterHelperImpl((XmlObject)ctErrBars, CTAreaSerImpl.ERRBARS$16, n, (short)2);
    }
    
    public CTErrBars insertNewErrBars(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTErrBars)this.get_store().insert_element_user(CTAreaSerImpl.ERRBARS$16, n);
        }
    }
    
    public CTErrBars addNewErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTErrBars)this.get_store().add_element_user(CTAreaSerImpl.ERRBARS$16);
        }
    }
    
    public void removeErrBars(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.ERRBARS$16, n);
        }
    }
    
    public CTAxDataSource getCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAxDataSource ctAxDataSource = (CTAxDataSource)this.get_store().find_element_user(CTAreaSerImpl.CAT$18, 0);
            if (ctAxDataSource == null) {
                return null;
            }
            return ctAxDataSource;
        }
    }
    
    public boolean isSetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.CAT$18) != 0;
        }
    }
    
    public void setCat(final CTAxDataSource ctAxDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctAxDataSource, CTAreaSerImpl.CAT$18, 0, (short)1);
    }
    
    public CTAxDataSource addNewCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAxDataSource)this.get_store().add_element_user(CTAreaSerImpl.CAT$18);
        }
    }
    
    public void unsetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.CAT$18, 0);
        }
    }
    
    public CTNumDataSource getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumDataSource ctNumDataSource = (CTNumDataSource)this.get_store().find_element_user(CTAreaSerImpl.VAL$20, 0);
            if (ctNumDataSource == null) {
                return null;
            }
            return ctNumDataSource;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.VAL$20) != 0;
        }
    }
    
    public void setVal(final CTNumDataSource ctNumDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctNumDataSource, CTAreaSerImpl.VAL$20, 0, (short)1);
    }
    
    public CTNumDataSource addNewVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumDataSource)this.get_store().add_element_user(CTAreaSerImpl.VAL$20);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.VAL$20, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTAreaSerImpl.EXTLST$22, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTAreaSerImpl.EXTLST$22) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTAreaSerImpl.EXTLST$22, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTAreaSerImpl.EXTLST$22);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTAreaSerImpl.EXTLST$22, 0);
        }
    }
    
    static {
        IDX$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "idx");
        ORDER$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "order");
        TX$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "tx");
        SPPR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        PICTUREOPTIONS$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pictureOptions");
        DPT$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dPt");
        DLBLS$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        TRENDLINE$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "trendline");
        ERRBARS$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "errBars");
        CAT$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "cat");
        VAL$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "val");
        EXTLST$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
