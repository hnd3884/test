package org.openxmlformats.schemas.drawingml.x2006.chart.impl;

import org.openxmlformats.schemas.drawingml.x2006.chart.CTExtensionList;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTUnsignedInt;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTChartLines;
import org.openxmlformats.schemas.drawingml.x2006.chart.CTOverlap;
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
import org.openxmlformats.schemas.drawingml.x2006.chart.CTBarChart;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTBarChartImpl extends XmlComplexContentImpl implements CTBarChart
{
    private static final long serialVersionUID = 1L;
    private static final QName BARDIR$0;
    private static final QName GROUPING$2;
    private static final QName VARYCOLORS$4;
    private static final QName SER$6;
    private static final QName DLBLS$8;
    private static final QName GAPWIDTH$10;
    private static final QName OVERLAP$12;
    private static final QName SERLINES$14;
    private static final QName AXID$16;
    private static final QName EXTLST$18;
    
    public CTBarChartImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public CTBarDir getBarDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBarDir ctBarDir = (CTBarDir)this.get_store().find_element_user(CTBarChartImpl.BARDIR$0, 0);
            if (ctBarDir == null) {
                return null;
            }
            return ctBarDir;
        }
    }
    
    public void setBarDir(final CTBarDir ctBarDir) {
        this.generatedSetterHelperImpl((XmlObject)ctBarDir, CTBarChartImpl.BARDIR$0, 0, (short)1);
    }
    
    public CTBarDir addNewBarDir() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarDir)this.get_store().add_element_user(CTBarChartImpl.BARDIR$0);
        }
    }
    
    public CTBarGrouping getGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBarGrouping ctBarGrouping = (CTBarGrouping)this.get_store().find_element_user(CTBarChartImpl.GROUPING$2, 0);
            if (ctBarGrouping == null) {
                return null;
            }
            return ctBarGrouping;
        }
    }
    
    public boolean isSetGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarChartImpl.GROUPING$2) != 0;
        }
    }
    
    public void setGrouping(final CTBarGrouping ctBarGrouping) {
        this.generatedSetterHelperImpl((XmlObject)ctBarGrouping, CTBarChartImpl.GROUPING$2, 0, (short)1);
    }
    
    public CTBarGrouping addNewGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarGrouping)this.get_store().add_element_user(CTBarChartImpl.GROUPING$2);
        }
    }
    
    public void unsetGrouping() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarChartImpl.GROUPING$2, 0);
        }
    }
    
    public CTBoolean getVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTBarChartImpl.VARYCOLORS$4, 0);
            if (ctBoolean == null) {
                return null;
            }
            return ctBoolean;
        }
    }
    
    public boolean isSetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarChartImpl.VARYCOLORS$4) != 0;
        }
    }
    
    public void setVaryColors(final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTBarChartImpl.VARYCOLORS$4, 0, (short)1);
    }
    
    public CTBoolean addNewVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTBarChartImpl.VARYCOLORS$4);
        }
    }
    
    public void unsetVaryColors() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarChartImpl.VARYCOLORS$4, 0);
        }
    }
    
    public List<CTBarSer> getSerList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerList extends AbstractList<CTBarSer>
            {
                @Override
                public CTBarSer get(final int n) {
                    return CTBarChartImpl.this.getSerArray(n);
                }
                
                @Override
                public CTBarSer set(final int n, final CTBarSer ctBarSer) {
                    final CTBarSer serArray = CTBarChartImpl.this.getSerArray(n);
                    CTBarChartImpl.this.setSerArray(n, ctBarSer);
                    return serArray;
                }
                
                @Override
                public void add(final int n, final CTBarSer ctBarSer) {
                    CTBarChartImpl.this.insertNewSer(n).set((XmlObject)ctBarSer);
                }
                
                @Override
                public CTBarSer remove(final int n) {
                    final CTBarSer serArray = CTBarChartImpl.this.getSerArray(n);
                    CTBarChartImpl.this.removeSer(n);
                    return serArray;
                }
                
                @Override
                public int size() {
                    return CTBarChartImpl.this.sizeOfSerArray();
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
            this.get_store().find_all_element_users(CTBarChartImpl.SER$6, (List)list);
            final CTBarSer[] array = new CTBarSer[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBarSer getSerArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBarSer ctBarSer = (CTBarSer)this.get_store().find_element_user(CTBarChartImpl.SER$6, n);
            if (ctBarSer == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBarSer;
        }
    }
    
    public int sizeOfSerArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarChartImpl.SER$6);
        }
    }
    
    public void setSerArray(final CTBarSer[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBarChartImpl.SER$6);
    }
    
    public void setSerArray(final int n, final CTBarSer ctBarSer) {
        this.generatedSetterHelperImpl((XmlObject)ctBarSer, CTBarChartImpl.SER$6, n, (short)2);
    }
    
    public CTBarSer insertNewSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarSer)this.get_store().insert_element_user(CTBarChartImpl.SER$6, n);
        }
    }
    
    public CTBarSer addNewSer() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBarSer)this.get_store().add_element_user(CTBarChartImpl.SER$6);
        }
    }
    
    public void removeSer(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarChartImpl.SER$6, n);
        }
    }
    
    public CTDLbls getDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDLbls ctdLbls = (CTDLbls)this.get_store().find_element_user(CTBarChartImpl.DLBLS$8, 0);
            if (ctdLbls == null) {
                return null;
            }
            return ctdLbls;
        }
    }
    
    public boolean isSetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarChartImpl.DLBLS$8) != 0;
        }
    }
    
    public void setDLbls(final CTDLbls ctdLbls) {
        this.generatedSetterHelperImpl((XmlObject)ctdLbls, CTBarChartImpl.DLBLS$8, 0, (short)1);
    }
    
    public CTDLbls addNewDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDLbls)this.get_store().add_element_user(CTBarChartImpl.DLBLS$8);
        }
    }
    
    public void unsetDLbls() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarChartImpl.DLBLS$8, 0);
        }
    }
    
    public CTGapAmount getGapWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTGapAmount ctGapAmount = (CTGapAmount)this.get_store().find_element_user(CTBarChartImpl.GAPWIDTH$10, 0);
            if (ctGapAmount == null) {
                return null;
            }
            return ctGapAmount;
        }
    }
    
    public boolean isSetGapWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarChartImpl.GAPWIDTH$10) != 0;
        }
    }
    
    public void setGapWidth(final CTGapAmount ctGapAmount) {
        this.generatedSetterHelperImpl((XmlObject)ctGapAmount, CTBarChartImpl.GAPWIDTH$10, 0, (short)1);
    }
    
    public CTGapAmount addNewGapWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTGapAmount)this.get_store().add_element_user(CTBarChartImpl.GAPWIDTH$10);
        }
    }
    
    public void unsetGapWidth() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarChartImpl.GAPWIDTH$10, 0);
        }
    }
    
    public CTOverlap getOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOverlap ctOverlap = (CTOverlap)this.get_store().find_element_user(CTBarChartImpl.OVERLAP$12, 0);
            if (ctOverlap == null) {
                return null;
            }
            return ctOverlap;
        }
    }
    
    public boolean isSetOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarChartImpl.OVERLAP$12) != 0;
        }
    }
    
    public void setOverlap(final CTOverlap ctOverlap) {
        this.generatedSetterHelperImpl((XmlObject)ctOverlap, CTBarChartImpl.OVERLAP$12, 0, (short)1);
    }
    
    public CTOverlap addNewOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOverlap)this.get_store().add_element_user(CTBarChartImpl.OVERLAP$12);
        }
    }
    
    public void unsetOverlap() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarChartImpl.OVERLAP$12, 0);
        }
    }
    
    public List<CTChartLines> getSerLinesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SerLinesList extends AbstractList<CTChartLines>
            {
                @Override
                public CTChartLines get(final int n) {
                    return CTBarChartImpl.this.getSerLinesArray(n);
                }
                
                @Override
                public CTChartLines set(final int n, final CTChartLines ctChartLines) {
                    final CTChartLines serLinesArray = CTBarChartImpl.this.getSerLinesArray(n);
                    CTBarChartImpl.this.setSerLinesArray(n, ctChartLines);
                    return serLinesArray;
                }
                
                @Override
                public void add(final int n, final CTChartLines ctChartLines) {
                    CTBarChartImpl.this.insertNewSerLines(n).set((XmlObject)ctChartLines);
                }
                
                @Override
                public CTChartLines remove(final int n) {
                    final CTChartLines serLinesArray = CTBarChartImpl.this.getSerLinesArray(n);
                    CTBarChartImpl.this.removeSerLines(n);
                    return serLinesArray;
                }
                
                @Override
                public int size() {
                    return CTBarChartImpl.this.sizeOfSerLinesArray();
                }
            }
            return new SerLinesList();
        }
    }
    
    @Deprecated
    public CTChartLines[] getSerLinesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTBarChartImpl.SERLINES$14, (List)list);
            final CTChartLines[] array = new CTChartLines[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTChartLines getSerLinesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTChartLines ctChartLines = (CTChartLines)this.get_store().find_element_user(CTBarChartImpl.SERLINES$14, n);
            if (ctChartLines == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctChartLines;
        }
    }
    
    public int sizeOfSerLinesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarChartImpl.SERLINES$14);
        }
    }
    
    public void setSerLinesArray(final CTChartLines[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBarChartImpl.SERLINES$14);
    }
    
    public void setSerLinesArray(final int n, final CTChartLines ctChartLines) {
        this.generatedSetterHelperImpl((XmlObject)ctChartLines, CTBarChartImpl.SERLINES$14, n, (short)2);
    }
    
    public CTChartLines insertNewSerLines(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartLines)this.get_store().insert_element_user(CTBarChartImpl.SERLINES$14, n);
        }
    }
    
    public CTChartLines addNewSerLines() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTChartLines)this.get_store().add_element_user(CTBarChartImpl.SERLINES$14);
        }
    }
    
    public void removeSerLines(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarChartImpl.SERLINES$14, n);
        }
    }
    
    public List<CTUnsignedInt> getAxIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AxIdList extends AbstractList<CTUnsignedInt>
            {
                @Override
                public CTUnsignedInt get(final int n) {
                    return CTBarChartImpl.this.getAxIdArray(n);
                }
                
                @Override
                public CTUnsignedInt set(final int n, final CTUnsignedInt ctUnsignedInt) {
                    final CTUnsignedInt axIdArray = CTBarChartImpl.this.getAxIdArray(n);
                    CTBarChartImpl.this.setAxIdArray(n, ctUnsignedInt);
                    return axIdArray;
                }
                
                @Override
                public void add(final int n, final CTUnsignedInt ctUnsignedInt) {
                    CTBarChartImpl.this.insertNewAxId(n).set((XmlObject)ctUnsignedInt);
                }
                
                @Override
                public CTUnsignedInt remove(final int n) {
                    final CTUnsignedInt axIdArray = CTBarChartImpl.this.getAxIdArray(n);
                    CTBarChartImpl.this.removeAxId(n);
                    return axIdArray;
                }
                
                @Override
                public int size() {
                    return CTBarChartImpl.this.sizeOfAxIdArray();
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
            this.get_store().find_all_element_users(CTBarChartImpl.AXID$16, (List)list);
            final CTUnsignedInt[] array = new CTUnsignedInt[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTUnsignedInt getAxIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTUnsignedInt ctUnsignedInt = (CTUnsignedInt)this.get_store().find_element_user(CTBarChartImpl.AXID$16, n);
            if (ctUnsignedInt == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctUnsignedInt;
        }
    }
    
    public int sizeOfAxIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarChartImpl.AXID$16);
        }
    }
    
    public void setAxIdArray(final CTUnsignedInt[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTBarChartImpl.AXID$16);
    }
    
    public void setAxIdArray(final int n, final CTUnsignedInt ctUnsignedInt) {
        this.generatedSetterHelperImpl((XmlObject)ctUnsignedInt, CTBarChartImpl.AXID$16, n, (short)2);
    }
    
    public CTUnsignedInt insertNewAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().insert_element_user(CTBarChartImpl.AXID$16, n);
        }
    }
    
    public CTUnsignedInt addNewAxId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTUnsignedInt)this.get_store().add_element_user(CTBarChartImpl.AXID$16);
        }
    }
    
    public void removeAxId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarChartImpl.AXID$16, n);
        }
    }
    
    public CTExtensionList getExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTExtensionList list = (CTExtensionList)this.get_store().find_element_user(CTBarChartImpl.EXTLST$18, 0);
            if (list == null) {
                return null;
            }
            return list;
        }
    }
    
    public boolean isSetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTBarChartImpl.EXTLST$18) != 0;
        }
    }
    
    public void setExtLst(final CTExtensionList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTBarChartImpl.EXTLST$18, 0, (short)1);
    }
    
    public CTExtensionList addNewExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTExtensionList)this.get_store().add_element_user(CTBarChartImpl.EXTLST$18);
        }
    }
    
    public void unsetExtLst() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTBarChartImpl.EXTLST$18, 0);
        }
    }
    
    static {
        BARDIR$0 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "barDir");
        GROUPING$2 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "grouping");
        VARYCOLORS$4 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "varyColors");
        SER$6 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "ser");
        DLBLS$8 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "dLbls");
        GAPWIDTH$10 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "gapWidth");
        OVERLAP$12 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "overlap");
        SERLINES$14 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "serLines");
        AXID$16 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "axId");
        EXTLST$18 = new QName("http://schemas.openxmlformats.org/drawingml/2006/chart", "extLst");
    }
}
