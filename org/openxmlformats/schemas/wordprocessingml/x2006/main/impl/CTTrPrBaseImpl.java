package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTJc;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTHeight;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTblWidth;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTCnf;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTTrPrBase;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTTrPrBaseImpl extends XmlComplexContentImpl implements CTTrPrBase
{
    private static final long serialVersionUID = 1L;
    private static final QName CNFSTYLE$0;
    private static final QName DIVID$2;
    private static final QName GRIDBEFORE$4;
    private static final QName GRIDAFTER$6;
    private static final QName WBEFORE$8;
    private static final QName WAFTER$10;
    private static final QName CANTSPLIT$12;
    private static final QName TRHEIGHT$14;
    private static final QName TBLHEADER$16;
    private static final QName TBLCELLSPACING$18;
    private static final QName JC$20;
    private static final QName HIDDEN$22;
    
    public CTTrPrBaseImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTCnf> getCnfStyleList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CnfStyleList extends AbstractList<CTCnf>
            {
                @Override
                public CTCnf get(final int n) {
                    return CTTrPrBaseImpl.this.getCnfStyleArray(n);
                }
                
                @Override
                public CTCnf set(final int n, final CTCnf ctCnf) {
                    final CTCnf cnfStyleArray = CTTrPrBaseImpl.this.getCnfStyleArray(n);
                    CTTrPrBaseImpl.this.setCnfStyleArray(n, ctCnf);
                    return cnfStyleArray;
                }
                
                @Override
                public void add(final int n, final CTCnf ctCnf) {
                    CTTrPrBaseImpl.this.insertNewCnfStyle(n).set((XmlObject)ctCnf);
                }
                
                @Override
                public CTCnf remove(final int n) {
                    final CTCnf cnfStyleArray = CTTrPrBaseImpl.this.getCnfStyleArray(n);
                    CTTrPrBaseImpl.this.removeCnfStyle(n);
                    return cnfStyleArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfCnfStyleArray();
                }
            }
            return new CnfStyleList();
        }
    }
    
    @Deprecated
    public CTCnf[] getCnfStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.CNFSTYLE$0, (List)list);
            final CTCnf[] array = new CTCnf[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTCnf getCnfStyleArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTCnf ctCnf = (CTCnf)this.get_store().find_element_user(CTTrPrBaseImpl.CNFSTYLE$0, n);
            if (ctCnf == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctCnf;
        }
    }
    
    public int sizeOfCnfStyleArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.CNFSTYLE$0);
        }
    }
    
    public void setCnfStyleArray(final CTCnf[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.CNFSTYLE$0);
    }
    
    public void setCnfStyleArray(final int n, final CTCnf ctCnf) {
        this.generatedSetterHelperImpl((XmlObject)ctCnf, CTTrPrBaseImpl.CNFSTYLE$0, n, (short)2);
    }
    
    public CTCnf insertNewCnfStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCnf)this.get_store().insert_element_user(CTTrPrBaseImpl.CNFSTYLE$0, n);
        }
    }
    
    public CTCnf addNewCnfStyle() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTCnf)this.get_store().add_element_user(CTTrPrBaseImpl.CNFSTYLE$0);
        }
    }
    
    public void removeCnfStyle(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.CNFSTYLE$0, n);
        }
    }
    
    public List<CTDecimalNumber> getDivIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DivIdList extends AbstractList<CTDecimalNumber>
            {
                @Override
                public CTDecimalNumber get(final int n) {
                    return CTTrPrBaseImpl.this.getDivIdArray(n);
                }
                
                @Override
                public CTDecimalNumber set(final int n, final CTDecimalNumber ctDecimalNumber) {
                    final CTDecimalNumber divIdArray = CTTrPrBaseImpl.this.getDivIdArray(n);
                    CTTrPrBaseImpl.this.setDivIdArray(n, ctDecimalNumber);
                    return divIdArray;
                }
                
                @Override
                public void add(final int n, final CTDecimalNumber ctDecimalNumber) {
                    CTTrPrBaseImpl.this.insertNewDivId(n).set((XmlObject)ctDecimalNumber);
                }
                
                @Override
                public CTDecimalNumber remove(final int n) {
                    final CTDecimalNumber divIdArray = CTTrPrBaseImpl.this.getDivIdArray(n);
                    CTTrPrBaseImpl.this.removeDivId(n);
                    return divIdArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfDivIdArray();
                }
            }
            return new DivIdList();
        }
    }
    
    @Deprecated
    public CTDecimalNumber[] getDivIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.DIVID$2, (List)list);
            final CTDecimalNumber[] array = new CTDecimalNumber[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDecimalNumber getDivIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTTrPrBaseImpl.DIVID$2, n);
            if (ctDecimalNumber == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDecimalNumber;
        }
    }
    
    public int sizeOfDivIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.DIVID$2);
        }
    }
    
    public void setDivIdArray(final CTDecimalNumber[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.DIVID$2);
    }
    
    public void setDivIdArray(final int n, final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTTrPrBaseImpl.DIVID$2, n, (short)2);
    }
    
    public CTDecimalNumber insertNewDivId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().insert_element_user(CTTrPrBaseImpl.DIVID$2, n);
        }
    }
    
    public CTDecimalNumber addNewDivId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTTrPrBaseImpl.DIVID$2);
        }
    }
    
    public void removeDivId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.DIVID$2, n);
        }
    }
    
    public List<CTDecimalNumber> getGridBeforeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GridBeforeList extends AbstractList<CTDecimalNumber>
            {
                @Override
                public CTDecimalNumber get(final int n) {
                    return CTTrPrBaseImpl.this.getGridBeforeArray(n);
                }
                
                @Override
                public CTDecimalNumber set(final int n, final CTDecimalNumber ctDecimalNumber) {
                    final CTDecimalNumber gridBeforeArray = CTTrPrBaseImpl.this.getGridBeforeArray(n);
                    CTTrPrBaseImpl.this.setGridBeforeArray(n, ctDecimalNumber);
                    return gridBeforeArray;
                }
                
                @Override
                public void add(final int n, final CTDecimalNumber ctDecimalNumber) {
                    CTTrPrBaseImpl.this.insertNewGridBefore(n).set((XmlObject)ctDecimalNumber);
                }
                
                @Override
                public CTDecimalNumber remove(final int n) {
                    final CTDecimalNumber gridBeforeArray = CTTrPrBaseImpl.this.getGridBeforeArray(n);
                    CTTrPrBaseImpl.this.removeGridBefore(n);
                    return gridBeforeArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfGridBeforeArray();
                }
            }
            return new GridBeforeList();
        }
    }
    
    @Deprecated
    public CTDecimalNumber[] getGridBeforeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.GRIDBEFORE$4, (List)list);
            final CTDecimalNumber[] array = new CTDecimalNumber[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDecimalNumber getGridBeforeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTTrPrBaseImpl.GRIDBEFORE$4, n);
            if (ctDecimalNumber == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDecimalNumber;
        }
    }
    
    public int sizeOfGridBeforeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.GRIDBEFORE$4);
        }
    }
    
    public void setGridBeforeArray(final CTDecimalNumber[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.GRIDBEFORE$4);
    }
    
    public void setGridBeforeArray(final int n, final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTTrPrBaseImpl.GRIDBEFORE$4, n, (short)2);
    }
    
    public CTDecimalNumber insertNewGridBefore(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().insert_element_user(CTTrPrBaseImpl.GRIDBEFORE$4, n);
        }
    }
    
    public CTDecimalNumber addNewGridBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTTrPrBaseImpl.GRIDBEFORE$4);
        }
    }
    
    public void removeGridBefore(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.GRIDBEFORE$4, n);
        }
    }
    
    public List<CTDecimalNumber> getGridAfterList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GridAfterList extends AbstractList<CTDecimalNumber>
            {
                @Override
                public CTDecimalNumber get(final int n) {
                    return CTTrPrBaseImpl.this.getGridAfterArray(n);
                }
                
                @Override
                public CTDecimalNumber set(final int n, final CTDecimalNumber ctDecimalNumber) {
                    final CTDecimalNumber gridAfterArray = CTTrPrBaseImpl.this.getGridAfterArray(n);
                    CTTrPrBaseImpl.this.setGridAfterArray(n, ctDecimalNumber);
                    return gridAfterArray;
                }
                
                @Override
                public void add(final int n, final CTDecimalNumber ctDecimalNumber) {
                    CTTrPrBaseImpl.this.insertNewGridAfter(n).set((XmlObject)ctDecimalNumber);
                }
                
                @Override
                public CTDecimalNumber remove(final int n) {
                    final CTDecimalNumber gridAfterArray = CTTrPrBaseImpl.this.getGridAfterArray(n);
                    CTTrPrBaseImpl.this.removeGridAfter(n);
                    return gridAfterArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfGridAfterArray();
                }
            }
            return new GridAfterList();
        }
    }
    
    @Deprecated
    public CTDecimalNumber[] getGridAfterArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.GRIDAFTER$6, (List)list);
            final CTDecimalNumber[] array = new CTDecimalNumber[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDecimalNumber getGridAfterArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTTrPrBaseImpl.GRIDAFTER$6, n);
            if (ctDecimalNumber == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDecimalNumber;
        }
    }
    
    public int sizeOfGridAfterArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.GRIDAFTER$6);
        }
    }
    
    public void setGridAfterArray(final CTDecimalNumber[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.GRIDAFTER$6);
    }
    
    public void setGridAfterArray(final int n, final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTTrPrBaseImpl.GRIDAFTER$6, n, (short)2);
    }
    
    public CTDecimalNumber insertNewGridAfter(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().insert_element_user(CTTrPrBaseImpl.GRIDAFTER$6, n);
        }
    }
    
    public CTDecimalNumber addNewGridAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTTrPrBaseImpl.GRIDAFTER$6);
        }
    }
    
    public void removeGridAfter(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.GRIDAFTER$6, n);
        }
    }
    
    public List<CTTblWidth> getWBeforeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class WBeforeList extends AbstractList<CTTblWidth>
            {
                @Override
                public CTTblWidth get(final int n) {
                    return CTTrPrBaseImpl.this.getWBeforeArray(n);
                }
                
                @Override
                public CTTblWidth set(final int n, final CTTblWidth ctTblWidth) {
                    final CTTblWidth wBeforeArray = CTTrPrBaseImpl.this.getWBeforeArray(n);
                    CTTrPrBaseImpl.this.setWBeforeArray(n, ctTblWidth);
                    return wBeforeArray;
                }
                
                @Override
                public void add(final int n, final CTTblWidth ctTblWidth) {
                    CTTrPrBaseImpl.this.insertNewWBefore(n).set((XmlObject)ctTblWidth);
                }
                
                @Override
                public CTTblWidth remove(final int n) {
                    final CTTblWidth wBeforeArray = CTTrPrBaseImpl.this.getWBeforeArray(n);
                    CTTrPrBaseImpl.this.removeWBefore(n);
                    return wBeforeArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfWBeforeArray();
                }
            }
            return new WBeforeList();
        }
    }
    
    @Deprecated
    public CTTblWidth[] getWBeforeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.WBEFORE$8, (List)list);
            final CTTblWidth[] array = new CTTblWidth[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTblWidth getWBeforeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTrPrBaseImpl.WBEFORE$8, n);
            if (ctTblWidth == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTblWidth;
        }
    }
    
    public int sizeOfWBeforeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.WBEFORE$8);
        }
    }
    
    public void setWBeforeArray(final CTTblWidth[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.WBEFORE$8);
    }
    
    public void setWBeforeArray(final int n, final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTrPrBaseImpl.WBEFORE$8, n, (short)2);
    }
    
    public CTTblWidth insertNewWBefore(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().insert_element_user(CTTrPrBaseImpl.WBEFORE$8, n);
        }
    }
    
    public CTTblWidth addNewWBefore() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTrPrBaseImpl.WBEFORE$8);
        }
    }
    
    public void removeWBefore(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.WBEFORE$8, n);
        }
    }
    
    public List<CTTblWidth> getWAfterList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class WAfterList extends AbstractList<CTTblWidth>
            {
                @Override
                public CTTblWidth get(final int n) {
                    return CTTrPrBaseImpl.this.getWAfterArray(n);
                }
                
                @Override
                public CTTblWidth set(final int n, final CTTblWidth ctTblWidth) {
                    final CTTblWidth wAfterArray = CTTrPrBaseImpl.this.getWAfterArray(n);
                    CTTrPrBaseImpl.this.setWAfterArray(n, ctTblWidth);
                    return wAfterArray;
                }
                
                @Override
                public void add(final int n, final CTTblWidth ctTblWidth) {
                    CTTrPrBaseImpl.this.insertNewWAfter(n).set((XmlObject)ctTblWidth);
                }
                
                @Override
                public CTTblWidth remove(final int n) {
                    final CTTblWidth wAfterArray = CTTrPrBaseImpl.this.getWAfterArray(n);
                    CTTrPrBaseImpl.this.removeWAfter(n);
                    return wAfterArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfWAfterArray();
                }
            }
            return new WAfterList();
        }
    }
    
    @Deprecated
    public CTTblWidth[] getWAfterArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.WAFTER$10, (List)list);
            final CTTblWidth[] array = new CTTblWidth[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTblWidth getWAfterArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTrPrBaseImpl.WAFTER$10, n);
            if (ctTblWidth == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTblWidth;
        }
    }
    
    public int sizeOfWAfterArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.WAFTER$10);
        }
    }
    
    public void setWAfterArray(final CTTblWidth[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.WAFTER$10);
    }
    
    public void setWAfterArray(final int n, final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTrPrBaseImpl.WAFTER$10, n, (short)2);
    }
    
    public CTTblWidth insertNewWAfter(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().insert_element_user(CTTrPrBaseImpl.WAFTER$10, n);
        }
    }
    
    public CTTblWidth addNewWAfter() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTrPrBaseImpl.WAFTER$10);
        }
    }
    
    public void removeWAfter(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.WAFTER$10, n);
        }
    }
    
    public List<CTOnOff> getCantSplitList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CantSplitList extends AbstractList<CTOnOff>
            {
                @Override
                public CTOnOff get(final int n) {
                    return CTTrPrBaseImpl.this.getCantSplitArray(n);
                }
                
                @Override
                public CTOnOff set(final int n, final CTOnOff ctOnOff) {
                    final CTOnOff cantSplitArray = CTTrPrBaseImpl.this.getCantSplitArray(n);
                    CTTrPrBaseImpl.this.setCantSplitArray(n, ctOnOff);
                    return cantSplitArray;
                }
                
                @Override
                public void add(final int n, final CTOnOff ctOnOff) {
                    CTTrPrBaseImpl.this.insertNewCantSplit(n).set((XmlObject)ctOnOff);
                }
                
                @Override
                public CTOnOff remove(final int n) {
                    final CTOnOff cantSplitArray = CTTrPrBaseImpl.this.getCantSplitArray(n);
                    CTTrPrBaseImpl.this.removeCantSplit(n);
                    return cantSplitArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfCantSplitArray();
                }
            }
            return new CantSplitList();
        }
    }
    
    @Deprecated
    public CTOnOff[] getCantSplitArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.CANTSPLIT$12, (List)list);
            final CTOnOff[] array = new CTOnOff[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOnOff getCantSplitArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTTrPrBaseImpl.CANTSPLIT$12, n);
            if (ctOnOff == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOnOff;
        }
    }
    
    public int sizeOfCantSplitArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.CANTSPLIT$12);
        }
    }
    
    public void setCantSplitArray(final CTOnOff[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.CANTSPLIT$12);
    }
    
    public void setCantSplitArray(final int n, final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTTrPrBaseImpl.CANTSPLIT$12, n, (short)2);
    }
    
    public CTOnOff insertNewCantSplit(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().insert_element_user(CTTrPrBaseImpl.CANTSPLIT$12, n);
        }
    }
    
    public CTOnOff addNewCantSplit() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTTrPrBaseImpl.CANTSPLIT$12);
        }
    }
    
    public void removeCantSplit(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.CANTSPLIT$12, n);
        }
    }
    
    public List<CTHeight> getTrHeightList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TrHeightList extends AbstractList<CTHeight>
            {
                @Override
                public CTHeight get(final int n) {
                    return CTTrPrBaseImpl.this.getTrHeightArray(n);
                }
                
                @Override
                public CTHeight set(final int n, final CTHeight ctHeight) {
                    final CTHeight trHeightArray = CTTrPrBaseImpl.this.getTrHeightArray(n);
                    CTTrPrBaseImpl.this.setTrHeightArray(n, ctHeight);
                    return trHeightArray;
                }
                
                @Override
                public void add(final int n, final CTHeight ctHeight) {
                    CTTrPrBaseImpl.this.insertNewTrHeight(n).set((XmlObject)ctHeight);
                }
                
                @Override
                public CTHeight remove(final int n) {
                    final CTHeight trHeightArray = CTTrPrBaseImpl.this.getTrHeightArray(n);
                    CTTrPrBaseImpl.this.removeTrHeight(n);
                    return trHeightArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfTrHeightArray();
                }
            }
            return new TrHeightList();
        }
    }
    
    @Deprecated
    public CTHeight[] getTrHeightArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.TRHEIGHT$14, (List)list);
            final CTHeight[] array = new CTHeight[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTHeight getTrHeightArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTHeight ctHeight = (CTHeight)this.get_store().find_element_user(CTTrPrBaseImpl.TRHEIGHT$14, n);
            if (ctHeight == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctHeight;
        }
    }
    
    public int sizeOfTrHeightArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.TRHEIGHT$14);
        }
    }
    
    public void setTrHeightArray(final CTHeight[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.TRHEIGHT$14);
    }
    
    public void setTrHeightArray(final int n, final CTHeight ctHeight) {
        this.generatedSetterHelperImpl((XmlObject)ctHeight, CTTrPrBaseImpl.TRHEIGHT$14, n, (short)2);
    }
    
    public CTHeight insertNewTrHeight(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHeight)this.get_store().insert_element_user(CTTrPrBaseImpl.TRHEIGHT$14, n);
        }
    }
    
    public CTHeight addNewTrHeight() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTHeight)this.get_store().add_element_user(CTTrPrBaseImpl.TRHEIGHT$14);
        }
    }
    
    public void removeTrHeight(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.TRHEIGHT$14, n);
        }
    }
    
    public List<CTOnOff> getTblHeaderList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TblHeaderList extends AbstractList<CTOnOff>
            {
                @Override
                public CTOnOff get(final int n) {
                    return CTTrPrBaseImpl.this.getTblHeaderArray(n);
                }
                
                @Override
                public CTOnOff set(final int n, final CTOnOff ctOnOff) {
                    final CTOnOff tblHeaderArray = CTTrPrBaseImpl.this.getTblHeaderArray(n);
                    CTTrPrBaseImpl.this.setTblHeaderArray(n, ctOnOff);
                    return tblHeaderArray;
                }
                
                @Override
                public void add(final int n, final CTOnOff ctOnOff) {
                    CTTrPrBaseImpl.this.insertNewTblHeader(n).set((XmlObject)ctOnOff);
                }
                
                @Override
                public CTOnOff remove(final int n) {
                    final CTOnOff tblHeaderArray = CTTrPrBaseImpl.this.getTblHeaderArray(n);
                    CTTrPrBaseImpl.this.removeTblHeader(n);
                    return tblHeaderArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfTblHeaderArray();
                }
            }
            return new TblHeaderList();
        }
    }
    
    @Deprecated
    public CTOnOff[] getTblHeaderArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.TBLHEADER$16, (List)list);
            final CTOnOff[] array = new CTOnOff[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOnOff getTblHeaderArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTTrPrBaseImpl.TBLHEADER$16, n);
            if (ctOnOff == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOnOff;
        }
    }
    
    public int sizeOfTblHeaderArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.TBLHEADER$16);
        }
    }
    
    public void setTblHeaderArray(final CTOnOff[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.TBLHEADER$16);
    }
    
    public void setTblHeaderArray(final int n, final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTTrPrBaseImpl.TBLHEADER$16, n, (short)2);
    }
    
    public CTOnOff insertNewTblHeader(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().insert_element_user(CTTrPrBaseImpl.TBLHEADER$16, n);
        }
    }
    
    public CTOnOff addNewTblHeader() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTTrPrBaseImpl.TBLHEADER$16);
        }
    }
    
    public void removeTblHeader(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.TBLHEADER$16, n);
        }
    }
    
    public List<CTTblWidth> getTblCellSpacingList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TblCellSpacingList extends AbstractList<CTTblWidth>
            {
                @Override
                public CTTblWidth get(final int n) {
                    return CTTrPrBaseImpl.this.getTblCellSpacingArray(n);
                }
                
                @Override
                public CTTblWidth set(final int n, final CTTblWidth ctTblWidth) {
                    final CTTblWidth tblCellSpacingArray = CTTrPrBaseImpl.this.getTblCellSpacingArray(n);
                    CTTrPrBaseImpl.this.setTblCellSpacingArray(n, ctTblWidth);
                    return tblCellSpacingArray;
                }
                
                @Override
                public void add(final int n, final CTTblWidth ctTblWidth) {
                    CTTrPrBaseImpl.this.insertNewTblCellSpacing(n).set((XmlObject)ctTblWidth);
                }
                
                @Override
                public CTTblWidth remove(final int n) {
                    final CTTblWidth tblCellSpacingArray = CTTrPrBaseImpl.this.getTblCellSpacingArray(n);
                    CTTrPrBaseImpl.this.removeTblCellSpacing(n);
                    return tblCellSpacingArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfTblCellSpacingArray();
                }
            }
            return new TblCellSpacingList();
        }
    }
    
    @Deprecated
    public CTTblWidth[] getTblCellSpacingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.TBLCELLSPACING$18, (List)list);
            final CTTblWidth[] array = new CTTblWidth[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTTblWidth getTblCellSpacingArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTTblWidth ctTblWidth = (CTTblWidth)this.get_store().find_element_user(CTTrPrBaseImpl.TBLCELLSPACING$18, n);
            if (ctTblWidth == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctTblWidth;
        }
    }
    
    public int sizeOfTblCellSpacingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.TBLCELLSPACING$18);
        }
    }
    
    public void setTblCellSpacingArray(final CTTblWidth[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.TBLCELLSPACING$18);
    }
    
    public void setTblCellSpacingArray(final int n, final CTTblWidth ctTblWidth) {
        this.generatedSetterHelperImpl((XmlObject)ctTblWidth, CTTrPrBaseImpl.TBLCELLSPACING$18, n, (short)2);
    }
    
    public CTTblWidth insertNewTblCellSpacing(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().insert_element_user(CTTrPrBaseImpl.TBLCELLSPACING$18, n);
        }
    }
    
    public CTTblWidth addNewTblCellSpacing() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTTblWidth)this.get_store().add_element_user(CTTrPrBaseImpl.TBLCELLSPACING$18);
        }
    }
    
    public void removeTblCellSpacing(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.TBLCELLSPACING$18, n);
        }
    }
    
    public List<CTJc> getJcList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class JcList extends AbstractList<CTJc>
            {
                @Override
                public CTJc get(final int n) {
                    return CTTrPrBaseImpl.this.getJcArray(n);
                }
                
                @Override
                public CTJc set(final int n, final CTJc ctJc) {
                    final CTJc jcArray = CTTrPrBaseImpl.this.getJcArray(n);
                    CTTrPrBaseImpl.this.setJcArray(n, ctJc);
                    return jcArray;
                }
                
                @Override
                public void add(final int n, final CTJc ctJc) {
                    CTTrPrBaseImpl.this.insertNewJc(n).set((XmlObject)ctJc);
                }
                
                @Override
                public CTJc remove(final int n) {
                    final CTJc jcArray = CTTrPrBaseImpl.this.getJcArray(n);
                    CTTrPrBaseImpl.this.removeJc(n);
                    return jcArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfJcArray();
                }
            }
            return new JcList();
        }
    }
    
    @Deprecated
    public CTJc[] getJcArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.JC$20, (List)list);
            final CTJc[] array = new CTJc[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTJc getJcArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTJc ctJc = (CTJc)this.get_store().find_element_user(CTTrPrBaseImpl.JC$20, n);
            if (ctJc == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctJc;
        }
    }
    
    public int sizeOfJcArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.JC$20);
        }
    }
    
    public void setJcArray(final CTJc[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.JC$20);
    }
    
    public void setJcArray(final int n, final CTJc ctJc) {
        this.generatedSetterHelperImpl((XmlObject)ctJc, CTTrPrBaseImpl.JC$20, n, (short)2);
    }
    
    public CTJc insertNewJc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTJc)this.get_store().insert_element_user(CTTrPrBaseImpl.JC$20, n);
        }
    }
    
    public CTJc addNewJc() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTJc)this.get_store().add_element_user(CTTrPrBaseImpl.JC$20);
        }
    }
    
    public void removeJc(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.JC$20, n);
        }
    }
    
    public List<CTOnOff> getHiddenList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class HiddenList extends AbstractList<CTOnOff>
            {
                @Override
                public CTOnOff get(final int n) {
                    return CTTrPrBaseImpl.this.getHiddenArray(n);
                }
                
                @Override
                public CTOnOff set(final int n, final CTOnOff ctOnOff) {
                    final CTOnOff hiddenArray = CTTrPrBaseImpl.this.getHiddenArray(n);
                    CTTrPrBaseImpl.this.setHiddenArray(n, ctOnOff);
                    return hiddenArray;
                }
                
                @Override
                public void add(final int n, final CTOnOff ctOnOff) {
                    CTTrPrBaseImpl.this.insertNewHidden(n).set((XmlObject)ctOnOff);
                }
                
                @Override
                public CTOnOff remove(final int n) {
                    final CTOnOff hiddenArray = CTTrPrBaseImpl.this.getHiddenArray(n);
                    CTTrPrBaseImpl.this.removeHidden(n);
                    return hiddenArray;
                }
                
                @Override
                public int size() {
                    return CTTrPrBaseImpl.this.sizeOfHiddenArray();
                }
            }
            return new HiddenList();
        }
    }
    
    @Deprecated
    public CTOnOff[] getHiddenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTTrPrBaseImpl.HIDDEN$22, (List)list);
            final CTOnOff[] array = new CTOnOff[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOnOff getHiddenArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTTrPrBaseImpl.HIDDEN$22, n);
            if (ctOnOff == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOnOff;
        }
    }
    
    public int sizeOfHiddenArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTTrPrBaseImpl.HIDDEN$22);
        }
    }
    
    public void setHiddenArray(final CTOnOff[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTTrPrBaseImpl.HIDDEN$22);
    }
    
    public void setHiddenArray(final int n, final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTTrPrBaseImpl.HIDDEN$22, n, (short)2);
    }
    
    public CTOnOff insertNewHidden(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().insert_element_user(CTTrPrBaseImpl.HIDDEN$22, n);
        }
    }
    
    public CTOnOff addNewHidden() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTTrPrBaseImpl.HIDDEN$22);
        }
    }
    
    public void removeHidden(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTTrPrBaseImpl.HIDDEN$22, n);
        }
    }
    
    static {
        CNFSTYLE$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cnfStyle");
        DIVID$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "divId");
        GRIDBEFORE$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "gridBefore");
        GRIDAFTER$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "gridAfter");
        WBEFORE$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "wBefore");
        WAFTER$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "wAfter");
        CANTSPLIT$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "cantSplit");
        TRHEIGHT$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "trHeight");
        TBLHEADER$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblHeader");
        TBLCELLSPACING$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tblCellSpacing");
        JC$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "jc");
        HIDDEN$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "hidden");
    }
}
