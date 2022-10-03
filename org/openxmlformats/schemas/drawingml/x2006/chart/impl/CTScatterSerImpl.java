package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTNumDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTAxDataSource;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTErrBars;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTTrendline;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDPt;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTMarker;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTScatterSer;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTScatterSerImpl extends XmlComplexContentImpl implements CTScatterSer
{
    private static final long serialVersionUID = 1L;
    private static final QName IDX$0;
    private static final QName ORDER$2;
    private static final QName TX$4;
    private static final QName SPPR$6;
    private static final QName MARKER$8;
    private static final QName DPT$10;
    private static final QName DLBLS$12;
    private static final QName TRENDLINE$14;
    private static final QName ERRBARS$16;
    private static final QName XVAL$18;
    private static final QName YVAL$20;
    private static final QName SMOOTH$22;
    private static final QName EXTLST$24;
    
    public CTScatterSerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTUnsignedInt getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTScatterSerImpl.IDX$0, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setIdx(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTScatterSerImpl.IDX$0, 0, (short)1);
    }
    
    public CTUnsignedInt addNewIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTScatterSerImpl.IDX$0);
        }
    }
    
    public CTUnsignedInt getOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTScatterSerImpl.ORDER$2, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setOrder(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTScatterSerImpl.ORDER$2, 0, (short)1);
    }
    
    public CTUnsignedInt addNewOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTScatterSerImpl.ORDER$2);
        }
    }
    
    public CTSerTx getTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSerTx ctSerTx = (CTSerTx)this.get_store().find_element_user(CTScatterSerImpl.TX$4, 0);
            if (ctSerTx == null) {
                return null;
            }
            return ctSerTx;
        }
    }
    
    public boolean isSetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.TX$4) != 0;
        }
    }
    
    public void setTx(final CTSerTx ctSerTx) {
        this.generatedSetterHelperImpl((XmlObject)ctSerTx, CTScatterSerImpl.TX$4, 0, (short)1);
    }
    
    public CTSerTx addNewTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSerTx)this.get_store().add_element_user(CTScatterSerImpl.TX$4);
        }
    }
    
    public void unsetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.TX$4, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTScatterSerImpl.SPPR$6, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.SPPR$6) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTScatterSerImpl.SPPR$6, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTScatterSerImpl.SPPR$6);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.SPPR$6, 0);
        }
    }
    
    public CTMarker getMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarker ctMarker = (CTMarker)this.get_store().find_element_user(CTScatterSerImpl.MARKER$8, 0);
            if (ctMarker == null) {
                return null;
            }
            return ctMarker;
        }
    }
    
    public boolean isSetMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.MARKER$8) != 0;
        }
    }
    
    public void setMarker(final CTMarker ctMarker) {
        this.generatedSetterHelperImpl((XmlObject)ctMarker, CTScatterSerImpl.MARKER$8, 0, (short)1);
    }
    
    public CTMarker addNewMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarker)this.get_store().add_element_user(CTScatterSerImpl.MARKER$8);
        }
    }
    
    public void unsetMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.MARKER$8, 0);
        }
    }
    
    public List<CTDPt> getDPtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DPtList extends AbstractList<CTDPt>
            {
                @Override
                public CTDPt get(final int n) {
                    return CTScatterSerImpl.this.getDPtArray(n);
                }
                
                @Override
                public CTDPt set(final int n, final CTDPt ctdPt) {
                    final CTDPt dPtArray = CTScatterSerImpl.this.getDPtArray(n);
                    CTScatterSerImpl.this.setDPtArray(n, ctdPt);
                    return dPtArray;
                }
                
                @Override
                public void add(final int n, final CTDPt ctdPt) {
                    CTScatterSerImpl.this.insertNewDPt(n).set((XmlObject)ctdPt);
                }
                
                @Override
                public CTDPt remove(final int n) {
                    final CTDPt dPtArray = CTScatterSerImpl.this.getDPtArray(n);
                    CTScatterSerImpl.this.removeDPt(n);
                    return dPtArray;
                }
                
                @Override
                public int size() {
                    return CTScatterSerImpl.this.sizeOfDPtArray();
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
            this.get_store().find_all_element_users(CTScatterSerImpl.DPT$10, (List)list);
            final CTDPt[] array = new CTDPt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDPt getDPtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDPt ctdPt = (CTDPt)this.get_store().find_element_user(CTScatterSerImpl.DPT$10, n);
            if (ctdPt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctdPt;
        }
    }
    
    public int sizeOfDPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.DPT$10);
        }
    }
    
    public void setDPtArray(final CTDPt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScatterSerImpl.DPT$10);
    }
    
    public void setDPtArray(final int n, final CTDPt ctdPt) {
        this.generatedSetterHelperImpl((XmlObject)ctdPt, CTScatterSerImpl.DPT$10, n, (short)2);
    }
    
    public CTDPt insertNewDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().insert_element_user(CTScatterSerImpl.DPT$10, n);
        }
    }
    
    public CTDPt addNewDPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().add_element_user(CTScatterSerImpl.DPT$10);
        }
    }
    
    public void removeDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.DPT$10, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTScatterSerImpl.DLBLS$12, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.DLBLS$12) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTScatterSerImpl.DLBLS$12, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTScatterSerImpl.DLBLS$12);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.DLBLS$12, 0);
        }
    }
    
    public List<CTTrendline> getTrendlineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TrendlineList extends AbstractList<CTTrendline>
            {
                @Override
                public CTTrendline get(final int n) {
                    return CTScatterSerImpl.this.getTrendlineArray(n);
                }
                
                @Override
                public CTTrendline set(final int n, final CTTrendline ctTrendline) {
                    final CTTrendline trendlineArray = CTScatterSerImpl.this.getTrendlineArray(n);
                    CTScatterSerImpl.this.setTrendlineArray(n, ctTrendline);
                    return trendlineArray;
                }
                
                @Override
                public void add(final int n, final CTTrendline ctTrendline) {
                    CTScatterSerImpl.this.insertNewTrendline(n).set((XmlObject)ctTrendline);
                }
                
                @Override
                public CTTrendline remove(final int n) {
                    final CTTrendline trendlineArray = CTScatterSerImpl.this.getTrendlineArray(n);
                    CTScatterSerImpl.this.removeTrendline(n);
                    return trendlineArray;
                }
                
                @Override
                public int size() {
                    return CTScatterSerImpl.this.sizeOfTrendlineArray();
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
            this.get_store().find_all_element_users(CTScatterSerImpl.TRENDLINE$14, (List)list);
            final CTTrendline[] array = new CTTrendline[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrendline getTrendlineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrendline ctTrendline = (CTTrendline)this.get_store().find_element_user(CTScatterSerImpl.TRENDLINE$14, n);
            if (ctTrendline == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrendline;
        }
    }
    
    public int sizeOfTrendlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.TRENDLINE$14);
        }
    }
    
    public void setTrendlineArray(final CTTrendline[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScatterSerImpl.TRENDLINE$14);
    }
    
    public void setTrendlineArray(final int n, final CTTrendline ctTrendline) {
        this.generatedSetterHelperImpl((XmlObject)ctTrendline, CTScatterSerImpl.TRENDLINE$14, n, (short)2);
    }
    
    public CTTrendline insertNewTrendline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrendline)this.get_store().insert_element_user(CTScatterSerImpl.TRENDLINE$14, n);
        }
    }
    
    public CTTrendline addNewTrendline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrendline)this.get_store().add_element_user(CTScatterSerImpl.TRENDLINE$14);
        }
    }
    
    public void removeTrendline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.TRENDLINE$14, n);
        }
    }
    
    public List<CTErrBars> getErrBarsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ErrBarsList extends AbstractList<CTErrBars>
            {
                @Override
                public CTErrBars get(final int n) {
                    return CTScatterSerImpl.this.getErrBarsArray(n);
                }
                
                @Override
                public CTErrBars set(final int n, final CTErrBars ctErrBars) {
                    final CTErrBars errBarsArray = CTScatterSerImpl.this.getErrBarsArray(n);
                    CTScatterSerImpl.this.setErrBarsArray(n, ctErrBars);
                    return errBarsArray;
                }
                
                @Override
                public void add(final int n, final CTErrBars ctErrBars) {
                    CTScatterSerImpl.this.insertNewErrBars(n).set((XmlObject)ctErrBars);
                }
                
                @Override
                public CTErrBars remove(final int n) {
                    final CTErrBars errBarsArray = CTScatterSerImpl.this.getErrBarsArray(n);
                    CTScatterSerImpl.this.removeErrBars(n);
                    return errBarsArray;
                }
                
                @Override
                public int size() {
                    return CTScatterSerImpl.this.sizeOfErrBarsArray();
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
            this.get_store().find_all_element_users(CTScatterSerImpl.ERRBARS$16, (List)list);
            final CTErrBars[] array = new CTErrBars[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTErrBars getErrBarsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTErrBars ctErrBars = (CTErrBars)this.get_store().find_element_user(CTScatterSerImpl.ERRBARS$16, n);
            if (ctErrBars == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctErrBars;
        }
    }
    
    public int sizeOfErrBarsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.ERRBARS$16);
        }
    }
    
    public void setErrBarsArray(final CTErrBars[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTScatterSerImpl.ERRBARS$16);
    }
    
    public void setErrBarsArray(final int n, final CTErrBars ctErrBars) {
        this.generatedSetterHelperImpl((XmlObject)ctErrBars, CTScatterSerImpl.ERRBARS$16, n, (short)2);
    }
    
    public CTErrBars insertNewErrBars(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTErrBars)this.get_store().insert_element_user(CTScatterSerImpl.ERRBARS$16, n);
        }
    }
    
    public CTErrBars addNewErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTErrBars)this.get_store().add_element_user(CTScatterSerImpl.ERRBARS$16);
        }
    }
    
    public void removeErrBars(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.ERRBARS$16, n);
        }
    }
    
    public CTAxDataSource getXVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAxDataSource ctAxDataSource = (CTAxDataSource)this.get_store().find_element_user(CTScatterSerImpl.XVAL$18, 0);
            if (ctAxDataSource == null) {
                return null;
            }
            return ctAxDataSource;
        }
    }
    
    public boolean isSetXVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.XVAL$18) != 0;
        }
    }
    
    public void setXVal(final CTAxDataSource ctAxDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctAxDataSource, CTScatterSerImpl.XVAL$18, 0, (short)1);
    }
    
    public CTAxDataSource addNewXVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAxDataSource)this.get_store().add_element_user(CTScatterSerImpl.XVAL$18);
        }
    }
    
    public void unsetXVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.XVAL$18, 0);
        }
    }
    
    public CTNumDataSource getYVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumDataSource ctNumDataSource = (CTNumDataSource)this.get_store().find_element_user(CTScatterSerImpl.YVAL$20, 0);
            if (ctNumDataSource == null) {
                return null;
            }
            return ctNumDataSource;
        }
    }
    
    public boolean isSetYVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.YVAL$20) != 0;
        }
    }
    
    public void setYVal(final CTNumDataSource ctNumDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctNumDataSource, CTScatterSerImpl.YVAL$20, 0, (short)1);
    }
    
    public CTNumDataSource addNewYVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumDataSource)this.get_store().add_element_user(CTScatterSerImpl.YVAL$20);
        }
    }
    
    public void unsetYVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.YVAL$20, 0);
        }
    }
    
    public CTBoolean getSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTScatterSerImpl.SMOOTH$22, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.SMOOTH$22) != 0;
        }
    }
    
    public void setSmooth(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTScatterSerImpl.SMOOTH$22, 0, (short)1);
    }
    
    public CTBoolean addNewSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTScatterSerImpl.SMOOTH$22);
        }
    }
    
    public void unsetSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.SMOOTH$22, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTScatterSerImpl.EXTLST$24, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTScatterSerImpl.EXTLST$24) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTScatterSerImpl.EXTLST$24, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTScatterSerImpl.EXTLST$24);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTScatterSerImpl.EXTLST$24, 0);
        }
    }
    
    static {
        IDX$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "idx");
        ORDER$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "order");
        TX$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "tx");
        SPPR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        MARKER$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "marker");
        DPT$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dPt");
        DLBLS$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        TRENDLINE$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "trendline");
        ERRBARS$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "errBars");
        XVAL$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "xVal");
        YVAL$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "yVal");
        SMOOTH$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "smooth");
        EXTLST$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
