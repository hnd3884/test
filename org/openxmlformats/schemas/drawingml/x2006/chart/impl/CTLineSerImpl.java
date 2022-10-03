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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTLineSer;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTLineSerImpl extends XmlComplexContentImpl implements CTLineSer
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
    private static final QName CAT$18;
    private static final QName VAL$20;
    private static final QName SMOOTH$22;
    private static final QName EXTLST$24;
    
    public CTLineSerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTUnsignedInt getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTLineSerImpl.IDX$0, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setIdx(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTLineSerImpl.IDX$0, 0, (short)1);
    }
    
    public CTUnsignedInt addNewIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTLineSerImpl.IDX$0);
        }
    }
    
    public CTUnsignedInt getOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTLineSerImpl.ORDER$2, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setOrder(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTLineSerImpl.ORDER$2, 0, (short)1);
    }
    
    public CTUnsignedInt addNewOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTLineSerImpl.ORDER$2);
        }
    }
    
    public CTSerTx getTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSerTx ctSerTx = (CTSerTx)this.get_store().find_element_user(CTLineSerImpl.TX$4, 0);
            if (ctSerTx == null) {
                return null;
            }
            return ctSerTx;
        }
    }
    
    public boolean isSetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.TX$4) != 0;
        }
    }
    
    public void setTx(final CTSerTx ctSerTx) {
        this.generatedSetterHelperImpl((XmlObject)ctSerTx, CTLineSerImpl.TX$4, 0, (short)1);
    }
    
    public CTSerTx addNewTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSerTx)this.get_store().add_element_user(CTLineSerImpl.TX$4);
        }
    }
    
    public void unsetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.TX$4, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTLineSerImpl.SPPR$6, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.SPPR$6) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTLineSerImpl.SPPR$6, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTLineSerImpl.SPPR$6);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.SPPR$6, 0);
        }
    }
    
    public CTMarker getMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMarker ctMarker = (CTMarker)this.get_store().find_element_user(CTLineSerImpl.MARKER$8, 0);
            if (ctMarker == null) {
                return null;
            }
            return ctMarker;
        }
    }
    
    public boolean isSetMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.MARKER$8) != 0;
        }
    }
    
    public void setMarker(final CTMarker ctMarker) {
        this.generatedSetterHelperImpl((XmlObject)ctMarker, CTLineSerImpl.MARKER$8, 0, (short)1);
    }
    
    public CTMarker addNewMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMarker)this.get_store().add_element_user(CTLineSerImpl.MARKER$8);
        }
    }
    
    public void unsetMarker() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.MARKER$8, 0);
        }
    }
    
    public List<CTDPt> getDPtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DPtList extends AbstractList<CTDPt>
            {
                @Override
                public CTDPt get(final int n) {
                    return CTLineSerImpl.this.getDPtArray(n);
                }
                
                @Override
                public CTDPt set(final int n, final CTDPt ctdPt) {
                    final CTDPt dPtArray = CTLineSerImpl.this.getDPtArray(n);
                    CTLineSerImpl.this.setDPtArray(n, ctdPt);
                    return dPtArray;
                }
                
                @Override
                public void add(final int n, final CTDPt ctdPt) {
                    CTLineSerImpl.this.insertNewDPt(n).set((XmlObject)ctdPt);
                }
                
                @Override
                public CTDPt remove(final int n) {
                    final CTDPt dPtArray = CTLineSerImpl.this.getDPtArray(n);
                    CTLineSerImpl.this.removeDPt(n);
                    return dPtArray;
                }
                
                @Override
                public int size() {
                    return CTLineSerImpl.this.sizeOfDPtArray();
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
            this.get_store().find_all_element_users(CTLineSerImpl.DPT$10, (List)list);
            final CTDPt[] array = new CTDPt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDPt getDPtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDPt ctdPt = (CTDPt)this.get_store().find_element_user(CTLineSerImpl.DPT$10, n);
            if (ctdPt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctdPt;
        }
    }
    
    public int sizeOfDPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.DPT$10);
        }
    }
    
    public void setDPtArray(final CTDPt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTLineSerImpl.DPT$10);
    }
    
    public void setDPtArray(final int n, final CTDPt ctdPt) {
        this.generatedSetterHelperImpl((XmlObject)ctdPt, CTLineSerImpl.DPT$10, n, (short)2);
    }
    
    public CTDPt insertNewDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().insert_element_user(CTLineSerImpl.DPT$10, n);
        }
    }
    
    public CTDPt addNewDPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().add_element_user(CTLineSerImpl.DPT$10);
        }
    }
    
    public void removeDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.DPT$10, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTLineSerImpl.DLBLS$12, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.DLBLS$12) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTLineSerImpl.DLBLS$12, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTLineSerImpl.DLBLS$12);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.DLBLS$12, 0);
        }
    }
    
    public List<CTTrendline> getTrendlineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TrendlineList extends AbstractList<CTTrendline>
            {
                @Override
                public CTTrendline get(final int n) {
                    return CTLineSerImpl.this.getTrendlineArray(n);
                }
                
                @Override
                public CTTrendline set(final int n, final CTTrendline ctTrendline) {
                    final CTTrendline trendlineArray = CTLineSerImpl.this.getTrendlineArray(n);
                    CTLineSerImpl.this.setTrendlineArray(n, ctTrendline);
                    return trendlineArray;
                }
                
                @Override
                public void add(final int n, final CTTrendline ctTrendline) {
                    CTLineSerImpl.this.insertNewTrendline(n).set((XmlObject)ctTrendline);
                }
                
                @Override
                public CTTrendline remove(final int n) {
                    final CTTrendline trendlineArray = CTLineSerImpl.this.getTrendlineArray(n);
                    CTLineSerImpl.this.removeTrendline(n);
                    return trendlineArray;
                }
                
                @Override
                public int size() {
                    return CTLineSerImpl.this.sizeOfTrendlineArray();
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
            this.get_store().find_all_element_users(CTLineSerImpl.TRENDLINE$14, (List)list);
            final CTTrendline[] array = new CTTrendline[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrendline getTrendlineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrendline ctTrendline = (CTTrendline)this.get_store().find_element_user(CTLineSerImpl.TRENDLINE$14, n);
            if (ctTrendline == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrendline;
        }
    }
    
    public int sizeOfTrendlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.TRENDLINE$14);
        }
    }
    
    public void setTrendlineArray(final CTTrendline[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTLineSerImpl.TRENDLINE$14);
    }
    
    public void setTrendlineArray(final int n, final CTTrendline ctTrendline) {
        this.generatedSetterHelperImpl((XmlObject)ctTrendline, CTLineSerImpl.TRENDLINE$14, n, (short)2);
    }
    
    public CTTrendline insertNewTrendline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrendline)this.get_store().insert_element_user(CTLineSerImpl.TRENDLINE$14, n);
        }
    }
    
    public CTTrendline addNewTrendline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrendline)this.get_store().add_element_user(CTLineSerImpl.TRENDLINE$14);
        }
    }
    
    public void removeTrendline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.TRENDLINE$14, n);
        }
    }
    
    public CTErrBars getErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTErrBars ctErrBars = (CTErrBars)this.get_store().find_element_user(CTLineSerImpl.ERRBARS$16, 0);
            if (ctErrBars == null) {
                return null;
            }
            return ctErrBars;
        }
    }
    
    public boolean isSetErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.ERRBARS$16) != 0;
        }
    }
    
    public void setErrBars(final CTErrBars ctErrBars) {
        this.generatedSetterHelperImpl((XmlObject)ctErrBars, CTLineSerImpl.ERRBARS$16, 0, (short)1);
    }
    
    public CTErrBars addNewErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTErrBars)this.get_store().add_element_user(CTLineSerImpl.ERRBARS$16);
        }
    }
    
    public void unsetErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.ERRBARS$16, 0);
        }
    }
    
    public CTAxDataSource getCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAxDataSource ctAxDataSource = (CTAxDataSource)this.get_store().find_element_user(CTLineSerImpl.CAT$18, 0);
            if (ctAxDataSource == null) {
                return null;
            }
            return ctAxDataSource;
        }
    }
    
    public boolean isSetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.CAT$18) != 0;
        }
    }
    
    public void setCat(final CTAxDataSource ctAxDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctAxDataSource, CTLineSerImpl.CAT$18, 0, (short)1);
    }
    
    public CTAxDataSource addNewCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAxDataSource)this.get_store().add_element_user(CTLineSerImpl.CAT$18);
        }
    }
    
    public void unsetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.CAT$18, 0);
        }
    }
    
    public CTNumDataSource getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumDataSource ctNumDataSource = (CTNumDataSource)this.get_store().find_element_user(CTLineSerImpl.VAL$20, 0);
            if (ctNumDataSource == null) {
                return null;
            }
            return ctNumDataSource;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.VAL$20) != 0;
        }
    }
    
    public void setVal(final CTNumDataSource ctNumDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctNumDataSource, CTLineSerImpl.VAL$20, 0, (short)1);
    }
    
    public CTNumDataSource addNewVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumDataSource)this.get_store().add_element_user(CTLineSerImpl.VAL$20);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.VAL$20, 0);
        }
    }
    
    public CTBoolean getSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTLineSerImpl.SMOOTH$22, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.SMOOTH$22) != 0;
        }
    }
    
    public void setSmooth(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTLineSerImpl.SMOOTH$22, 0, (short)1);
    }
    
    public CTBoolean addNewSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTLineSerImpl.SMOOTH$22);
        }
    }
    
    public void unsetSmooth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.SMOOTH$22, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTLineSerImpl.EXTLST$24, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTLineSerImpl.EXTLST$24) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTLineSerImpl.EXTLST$24, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTLineSerImpl.EXTLST$24);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTLineSerImpl.EXTLST$24, 0);
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
        CAT$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "cat");
        VAL$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "val");
        SMOOTH$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "smooth");
        EXTLST$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
