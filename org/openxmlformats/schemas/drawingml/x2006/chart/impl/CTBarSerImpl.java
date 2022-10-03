package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTShape;
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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.main.CTShapeProperties;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTSerTx;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBarSerImpl extends XmlComplexContentImpl implements CTBarSer
{
    private static final long serialVersionUID = 1L;
    private static final QName IDX$0;
    private static final QName ORDER$2;
    private static final QName TX$4;
    private static final QName SPPR$6;
    private static final QName INVERTIFNEGATIVE$8;
    private static final QName PICTUREOPTIONS$10;
    private static final QName DPT$12;
    private static final QName DLBLS$14;
    private static final QName TRENDLINE$16;
    private static final QName ERRBARS$18;
    private static final QName CAT$20;
    private static final QName VAL$22;
    private static final QName SHAPE$24;
    private static final QName EXTLST$26;
    
    public CTBarSerImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTUnsignedInt getIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTBarSerImpl.IDX$0, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setIdx(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTBarSerImpl.IDX$0, 0, (short)1);
    }
    
    public CTUnsignedInt addNewIdx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTBarSerImpl.IDX$0);
        }
    }
    
    public CTUnsignedInt getOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTBarSerImpl.ORDER$2, 0);
            if (ctUnsignedInt == null) {
                return null;
            }
            return ctUnsignedInt;
        }
    }
    
    public void setOrder(final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTBarSerImpl.ORDER$2, 0, (short)1);
    }
    
    public CTUnsignedInt addNewOrder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTBarSerImpl.ORDER$2);
        }
    }
    
    public CTSerTx getTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSerTx ctSerTx = (CTSerTx)this.get_store().find_element_user(CTBarSerImpl.TX$4, 0);
            if (ctSerTx == null) {
                return null;
            }
            return ctSerTx;
        }
    }
    
    public boolean isSetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.TX$4) != 0;
        }
    }
    
    public void setTx(final CTSerTx ctSerTx) {
        this.generatedSetterHelperImpl((XmlObject)ctSerTx, CTBarSerImpl.TX$4, 0, (short)1);
    }
    
    public CTSerTx addNewTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSerTx)this.get_store().add_element_user(CTBarSerImpl.TX$4);
        }
    }
    
    public void unsetTx() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.TX$4, 0);
        }
    }
    
    public CTShapeProperties getSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShapeProperties ctShapeProperties = (CTShapeProperties)this.get_store().find_element_user(CTBarSerImpl.SPPR$6, 0);
            if (ctShapeProperties == null) {
                return null;
            }
            return ctShapeProperties;
        }
    }
    
    public boolean isSetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.SPPR$6) != 0;
        }
    }
    
    public void setSpPr(final CTShapeProperties ctShapeProperties) {
        this.generatedSetterHelperImpl((XmlObject)ctShapeProperties, CTBarSerImpl.SPPR$6, 0, (short)1);
    }
    
    public CTShapeProperties addNewSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShapeProperties)this.get_store().add_element_user(CTBarSerImpl.SPPR$6);
        }
    }
    
    public void unsetSpPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.SPPR$6, 0);
        }
    }
    
    public CTBoolean getInvertIfNegative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTBarSerImpl.INVERTIFNEGATIVE$8, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetInvertIfNegative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.INVERTIFNEGATIVE$8) != 0;
        }
    }
    
    public void setInvertIfNegative(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTBarSerImpl.INVERTIFNEGATIVE$8, 0, (short)1);
    }
    
    public CTBoolean addNewInvertIfNegative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTBarSerImpl.INVERTIFNEGATIVE$8);
        }
    }
    
    public void unsetInvertIfNegative() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.INVERTIFNEGATIVE$8, 0);
        }
    }
    
    public CTPictureOptions getPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPictureOptions ctPictureOptions = (CTPictureOptions)this.get_store().find_element_user(CTBarSerImpl.PICTUREOPTIONS$10, 0);
            if (ctPictureOptions == null) {
                return null;
            }
            return ctPictureOptions;
        }
    }
    
    public boolean isSetPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.PICTUREOPTIONS$10) != 0;
        }
    }
    
    public void setPictureOptions(final CTPictureOptions ctPictureOptions) {
        this.generatedSetterHelperImpl((XmlObject)ctPictureOptions, CTBarSerImpl.PICTUREOPTIONS$10, 0, (short)1);
    }
    
    public CTPictureOptions addNewPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPictureOptions)this.get_store().add_element_user(CTBarSerImpl.PICTUREOPTIONS$10);
        }
    }
    
    public void unsetPictureOptions() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.PICTUREOPTIONS$10, 0);
        }
    }
    
    public List<CTDPt> getDPtList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DPtList extends AbstractList<CTDPt>
            {
                @Override
                public CTDPt get(final int n) {
                    return CTBarSerImpl.this.getDPtArray(n);
                }
                
                @Override
                public CTDPt set(final int n, final CTDPt ctdPt) {
                    final CTDPt dPtArray = CTBarSerImpl.this.getDPtArray(n);
                    CTBarSerImpl.this.setDPtArray(n, ctdPt);
                    return dPtArray;
                }
                
                @Override
                public void add(final int n, final CTDPt ctdPt) {
                    CTBarSerImpl.this.insertNewDPt(n).set((XmlObject)ctdPt);
                }
                
                @Override
                public CTDPt remove(final int n) {
                    final CTDPt dPtArray = CTBarSerImpl.this.getDPtArray(n);
                    CTBarSerImpl.this.removeDPt(n);
                    return dPtArray;
                }
                
                @Override
                public int size() {
                    return CTBarSerImpl.this.sizeOfDPtArray();
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
            this.get_store().find_all_element_users(CTBarSerImpl.DPT$12, (List)list);
            final CTDPt[] array = new CTDPt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDPt getDPtArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDPt ctdPt = (CTDPt)this.get_store().find_element_user(CTBarSerImpl.DPT$12, n);
            if (ctdPt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctdPt;
        }
    }
    
    public int sizeOfDPtArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.DPT$12);
        }
    }
    
    public void setDPtArray(final CTDPt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBarSerImpl.DPT$12);
    }
    
    public void setDPtArray(final int n, final CTDPt ctdPt) {
        this.generatedSetterHelperImpl((XmlObject)ctdPt, CTBarSerImpl.DPT$12, n, (short)2);
    }
    
    public CTDPt insertNewDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().insert_element_user(CTBarSerImpl.DPT$12, n);
        }
    }
    
    public CTDPt addNewDPt() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDPt)this.get_store().add_element_user(CTBarSerImpl.DPT$12);
        }
    }
    
    public void removeDPt(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.DPT$12, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTBarSerImpl.DLBLS$14, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.DLBLS$14) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTBarSerImpl.DLBLS$14, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTBarSerImpl.DLBLS$14);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.DLBLS$14, 0);
        }
    }
    
    public List<CTTrendline> getTrendlineList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TrendlineList extends AbstractList<CTTrendline>
            {
                @Override
                public CTTrendline get(final int n) {
                    return CTBarSerImpl.this.getTrendlineArray(n);
                }
                
                @Override
                public CTTrendline set(final int n, final CTTrendline ctTrendline) {
                    final CTTrendline trendlineArray = CTBarSerImpl.this.getTrendlineArray(n);
                    CTBarSerImpl.this.setTrendlineArray(n, ctTrendline);
                    return trendlineArray;
                }
                
                @Override
                public void add(final int n, final CTTrendline ctTrendline) {
                    CTBarSerImpl.this.insertNewTrendline(n).set((XmlObject)ctTrendline);
                }
                
                @Override
                public CTTrendline remove(final int n) {
                    final CTTrendline trendlineArray = CTBarSerImpl.this.getTrendlineArray(n);
                    CTBarSerImpl.this.removeTrendline(n);
                    return trendlineArray;
                }
                
                @Override
                public int size() {
                    return CTBarSerImpl.this.sizeOfTrendlineArray();
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
            this.get_store().find_all_element_users(CTBarSerImpl.TRENDLINE$16, (List)list);
            final CTTrendline[] array = new CTTrendline[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTrendline getTrendlineArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTrendline ctTrendline = (CTTrendline)this.get_store().find_element_user(CTBarSerImpl.TRENDLINE$16, n);
            if (ctTrendline == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTrendline;
        }
    }
    
    public int sizeOfTrendlineArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.TRENDLINE$16);
        }
    }
    
    public void setTrendlineArray(final CTTrendline[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBarSerImpl.TRENDLINE$16);
    }
    
    public void setTrendlineArray(final int n, final CTTrendline ctTrendline) {
        this.generatedSetterHelperImpl((XmlObject)ctTrendline, CTBarSerImpl.TRENDLINE$16, n, (short)2);
    }
    
    public CTTrendline insertNewTrendline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrendline)this.get_store().insert_element_user(CTBarSerImpl.TRENDLINE$16, n);
        }
    }
    
    public CTTrendline addNewTrendline() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTrendline)this.get_store().add_element_user(CTBarSerImpl.TRENDLINE$16);
        }
    }
    
    public void removeTrendline(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.TRENDLINE$16, n);
        }
    }
    
    public CTErrBars getErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTErrBars ctErrBars = (CTErrBars)this.get_store().find_element_user(CTBarSerImpl.ERRBARS$18, 0);
            if (ctErrBars == null) {
                return null;
            }
            return ctErrBars;
        }
    }
    
    public boolean isSetErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.ERRBARS$18) != 0;
        }
    }
    
    public void setErrBars(final CTErrBars ctErrBars) {
        this.generatedSetterHelperImpl((XmlObject)ctErrBars, CTBarSerImpl.ERRBARS$18, 0, (short)1);
    }
    
    public CTErrBars addNewErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTErrBars)this.get_store().add_element_user(CTBarSerImpl.ERRBARS$18);
        }
    }
    
    public void unsetErrBars() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.ERRBARS$18, 0);
        }
    }
    
    public CTAxDataSource getCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTAxDataSource ctAxDataSource = (CTAxDataSource)this.get_store().find_element_user(CTBarSerImpl.CAT$20, 0);
            if (ctAxDataSource == null) {
                return null;
            }
            return ctAxDataSource;
        }
    }
    
    public boolean isSetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.CAT$20) != 0;
        }
    }
    
    public void setCat(final CTAxDataSource ctAxDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctAxDataSource, CTBarSerImpl.CAT$20, 0, (short)1);
    }
    
    public CTAxDataSource addNewCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTAxDataSource)this.get_store().add_element_user(CTBarSerImpl.CAT$20);
        }
    }
    
    public void unsetCat() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.CAT$20, 0);
        }
    }
    
    public CTNumDataSource getVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumDataSource ctNumDataSource = (CTNumDataSource)this.get_store().find_element_user(CTBarSerImpl.VAL$22, 0);
            if (ctNumDataSource == null) {
                return null;
            }
            return ctNumDataSource;
        }
    }
    
    public boolean isSetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.VAL$22) != 0;
        }
    }
    
    public void setVal(final CTNumDataSource ctNumDataSource) {
        this.generatedSetterHelperImpl((XmlObject)ctNumDataSource, CTBarSerImpl.VAL$22, 0, (short)1);
    }
    
    public CTNumDataSource addNewVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumDataSource)this.get_store().add_element_user(CTBarSerImpl.VAL$22);
        }
    }
    
    public void unsetVal() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.VAL$22, 0);
        }
    }
    
    public CTShape getShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShape ctShape = (CTShape)this.get_store().find_element_user(CTBarSerImpl.SHAPE$24, 0);
            if (ctShape == null) {
                return null;
            }
            return ctShape;
        }
    }
    
    public boolean isSetShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.SHAPE$24) != 0;
        }
    }
    
    public void setShape(final CTShape ctShape) {
        this.generatedSetterHelperImpl((XmlObject)ctShape, CTBarSerImpl.SHAPE$24, 0, (short)1);
    }
    
    public CTShape addNewShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape)this.get_store().add_element_user(CTBarSerImpl.SHAPE$24);
        }
    }
    
    public void unsetShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.SHAPE$24, 0);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTBarSerImpl.EXTLST$26, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarSerImpl.EXTLST$26) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTBarSerImpl.EXTLST$26, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTBarSerImpl.EXTLST$26);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarSerImpl.EXTLST$26, 0);
        }
    }
    
    static {
        IDX$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "idx");
        ORDER$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "order");
        TX$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "tx");
        SPPR$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "spPr");
        INVERTIFNEGATIVE$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "invertIfNegative");
        PICTUREOPTIONS$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "pictureOptions");
        DPT$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dPt");
        DLBLS$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        TRENDLINE$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "trendline");
        ERRBARS$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "errBars");
        CAT$20 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "cat");
        VAL$22 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "val");
        SHAPE$24 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "shape");
        EXTLST$26 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
