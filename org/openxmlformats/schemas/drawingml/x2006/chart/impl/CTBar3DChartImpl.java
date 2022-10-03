package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTShape;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTGapAmount;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTDLbls;
import java.util.ArrayList;
import java.util.AbstractList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarSer;
import java.util.List;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBoolean;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarGrouping;
import org.apache.xmlbeans.XmlObject;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarDir;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBar3DChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBar3DChartImpl extends XmlComplexContentImpl implements CTBar3DChart
{
    private static final long serialVersionUID = 1L;
    private static final QName BARDIR$0;
    private static final QName GROUPING$2;
    private static final QName VARYCOLORS$4;
    private static final QName SER$6;
    private static final QName DLBLS$8;
    private static final QName GAPWIDTH$10;
    private static final QName GAPDEPTH$12;
    private static final QName SHAPE$14;
    private static final QName AXID$16;
    private static final QName EXTLST$18;
    
    public CTBar3DChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBarDir getBarDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBarDir ctBarDir = (CTBarDir)this.get_store().find_element_user(CTBar3DChartImpl.BARDIR$0, 0);
            if (ctBarDir == null) {
                return null;
            }
            return ctBarDir;
        }
    }
    
    public void setBarDir(final CTBarDir ctBarDir) {
        this.generatedSetterHelperImpl((XmlObject)ctBarDir, CTBar3DChartImpl.BARDIR$0, 0, (short)1);
    }
    
    public CTBarDir addNewBarDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarDir)this.get_store().add_element_user(CTBar3DChartImpl.BARDIR$0);
        }
    }
    
    public CTBarGrouping getGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBarGrouping ctBarGrouping = (CTBarGrouping)this.get_store().find_element_user(CTBar3DChartImpl.GROUPING$2, 0);
            if (ctBarGrouping == null) {
                return null;
            }
            return ctBarGrouping;
        }
    }
    
    public boolean isSetGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBar3DChartImpl.GROUPING$2) != 0;
        }
    }
    
    public void setGrouping(final CTBarGrouping ctBarGrouping) {
        this.generatedSetterHelperImpl((XmlObject)ctBarGrouping, CTBar3DChartImpl.GROUPING$2, 0, (short)1);
    }
    
    public CTBarGrouping addNewGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarGrouping)this.get_store().add_element_user(CTBar3DChartImpl.GROUPING$2);
        }
    }
    
    public void unsetGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBar3DChartImpl.GROUPING$2, 0);
        }
    }
    
    public CTBoolean getVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTBar3DChartImpl.VARYCOLORS$4, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBar3DChartImpl.VARYCOLORS$4) != 0;
        }
    }
    
    public void setVaryColors(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTBar3DChartImpl.VARYCOLORS$4, 0, (short)1);
    }
    
    public CTBoolean addNewVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTBar3DChartImpl.VARYCOLORS$4);
        }
    }
    
    public void unsetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBar3DChartImpl.VARYCOLORS$4, 0);
        }
    }
    
    public List<CTBarSer> getSerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerList extends AbstractList<CTBarSer>
            {
                @Override
                public CTBarSer get(final int n) {
                    return CTBar3DChartImpl.this.getSerArray(n);
                }
                
                @Override
                public CTBarSer set(final int n, final CTBarSer ctBarSer) {
                    final CTBarSer serArray = CTBar3DChartImpl.this.getSerArray(n);
                    CTBar3DChartImpl.this.setSerArray(n, ctBarSer);
                    return serArray;
                }
                
                @Override
                public void add(final int n, final CTBarSer ctBarSer) {
                    CTBar3DChartImpl.this.insertNewSer(n).set((XmlObject)ctBarSer);
                }
                
                @Override
                public CTBarSer remove(final int n) {
                    final CTBarSer serArray = CTBar3DChartImpl.this.getSerArray(n);
                    CTBar3DChartImpl.this.removeSer(n);
                    return serArray;
                }
                
                @Override
                public int size() {
                    return CTBar3DChartImpl.this.sizeOfSerArray();
                }
            }
            return new SerList();
        }
    }
    
    @Deprecated
    public CTBarSer[] getSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBar3DChartImpl.SER$6, (List)list);
            final CTBarSer[] array = new CTBarSer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBarSer getSerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBarSer ctBarSer = (CTBarSer)this.get_store().find_element_user(CTBar3DChartImpl.SER$6, n);
            if (ctBarSer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBarSer;
        }
    }
    
    public int sizeOfSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBar3DChartImpl.SER$6);
        }
    }
    
    public void setSerArray(final CTBarSer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBar3DChartImpl.SER$6);
    }
    
    public void setSerArray(final int n, final CTBarSer ctBarSer) {
        this.generatedSetterHelperImpl((XmlObject)ctBarSer, CTBar3DChartImpl.SER$6, n, (short)2);
    }
    
    public CTBarSer insertNewSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarSer)this.get_store().insert_element_user(CTBar3DChartImpl.SER$6, n);
        }
    }
    
    public CTBarSer addNewSer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarSer)this.get_store().add_element_user(CTBar3DChartImpl.SER$6);
        }
    }
    
    public void removeSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBar3DChartImpl.SER$6, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTBar3DChartImpl.DLBLS$8, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBar3DChartImpl.DLBLS$8) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTBar3DChartImpl.DLBLS$8, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTBar3DChartImpl.DLBLS$8);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBar3DChartImpl.DLBLS$8, 0);
        }
    }
    
    public CTGapAmount getGapWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGapAmount ctGapAmount = (CTGapAmount)this.get_store().find_element_user(CTBar3DChartImpl.GAPWIDTH$10, 0);
            if (ctGapAmount == null) {
                return null;
            }
            return ctGapAmount;
        }
    }
    
    public boolean isSetGapWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBar3DChartImpl.GAPWIDTH$10) != 0;
        }
    }
    
    public void setGapWidth(final CTGapAmount ctGapAmount) {
        this.generatedSetterHelperImpl((XmlObject)ctGapAmount, CTBar3DChartImpl.GAPWIDTH$10, 0, (short)1);
    }
    
    public CTGapAmount addNewGapWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGapAmount)this.get_store().add_element_user(CTBar3DChartImpl.GAPWIDTH$10);
        }
    }
    
    public void unsetGapWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBar3DChartImpl.GAPWIDTH$10, 0);
        }
    }
    
    public CTGapAmount getGapDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGapAmount ctGapAmount = (CTGapAmount)this.get_store().find_element_user(CTBar3DChartImpl.GAPDEPTH$12, 0);
            if (ctGapAmount == null) {
                return null;
            }
            return ctGapAmount;
        }
    }
    
    public boolean isSetGapDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBar3DChartImpl.GAPDEPTH$12) != 0;
        }
    }
    
    public void setGapDepth(final CTGapAmount ctGapAmount) {
        this.generatedSetterHelperImpl((XmlObject)ctGapAmount, CTBar3DChartImpl.GAPDEPTH$12, 0, (short)1);
    }
    
    public CTGapAmount addNewGapDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGapAmount)this.get_store().add_element_user(CTBar3DChartImpl.GAPDEPTH$12);
        }
    }
    
    public void unsetGapDepth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBar3DChartImpl.GAPDEPTH$12, 0);
        }
    }
    
    public CTShape getShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTShape ctShape = (CTShape)this.get_store().find_element_user(CTBar3DChartImpl.SHAPE$14, 0);
            if (ctShape == null) {
                return null;
            }
            return ctShape;
        }
    }
    
    public boolean isSetShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBar3DChartImpl.SHAPE$14) != 0;
        }
    }
    
    public void setShape(final CTShape ctShape) {
        this.generatedSetterHelperImpl((XmlObject)ctShape, CTBar3DChartImpl.SHAPE$14, 0, (short)1);
    }
    
    public CTShape addNewShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTShape)this.get_store().add_element_user(CTBar3DChartImpl.SHAPE$14);
        }
    }
    
    public void unsetShape() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBar3DChartImpl.SHAPE$14, 0);
        }
    }
    
    public List<CTUnsignedInt> getAxIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AxIdList extends AbstractList<CTUnsignedInt>
            {
                @Override
                public CTUnsignedInt get(final int n) {
                    return CTBar3DChartImpl.this.getAxIdArray(n);
                }
                
                @Override
                public CTUnsignedInt set(final int n, final CTUnsignedInt ctUnsignedInt) {
                    final CTUnsignedInt axIdArray = CTBar3DChartImpl.this.getAxIdArray(n);
                    CTBar3DChartImpl.this.setAxIdArray(n, ctUnsignedInt);
                    return axIdArray;
                }
                
                @Override
                public void add(final int n, final CTUnsignedInt ctUnsignedInt) {
                    CTBar3DChartImpl.this.insertNewAxId(n).set((XmlObject)ctUnsignedInt);
                }
                
                @Override
                public CTUnsignedInt remove(final int n) {
                    final CTUnsignedInt axIdArray = CTBar3DChartImpl.this.getAxIdArray(n);
                    CTBar3DChartImpl.this.removeAxId(n);
                    return axIdArray;
                }
                
                @Override
                public int size() {
                    return CTBar3DChartImpl.this.sizeOfAxIdArray();
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
            this.get_store().find_all_element_users(CTBar3DChartImpl.AXID$16, (List)list);
            final CTUnsignedInt[] array = new CTUnsignedInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnsignedInt getAxIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTBar3DChartImpl.AXID$16, n);
            if (ctUnsignedInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnsignedInt;
        }
    }
    
    public int sizeOfAxIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBar3DChartImpl.AXID$16);
        }
    }
    
    public void setAxIdArray(final CTUnsignedInt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBar3DChartImpl.AXID$16);
    }
    
    public void setAxIdArray(final int n, final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTBar3DChartImpl.AXID$16, n, (short)2);
    }
    
    public CTUnsignedInt insertNewAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().insert_element_user(CTBar3DChartImpl.AXID$16, n);
        }
    }
    
    public CTUnsignedInt addNewAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTBar3DChartImpl.AXID$16);
        }
    }
    
    public void removeAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBar3DChartImpl.AXID$16, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTBar3DChartImpl.EXTLST$18, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBar3DChartImpl.EXTLST$18) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTBar3DChartImpl.EXTLST$18, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTBar3DChartImpl.EXTLST$18);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBar3DChartImpl.EXTLST$18, 0);
        }
    }
    
    static {
        BARDIR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "barDir");
        GROUPING$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "grouping");
        VARYCOLORS$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "varyColors");
        SER$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        DLBLS$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        GAPWIDTH$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "gapWidth");
        GAPDEPTH$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "gapDepth");
        SHAPE$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "shape");
        AXID$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId");
        EXTLST$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
