package org.openxmlformats.schemas.wordprocessingml.x2006.main.impl;

import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtText;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDropDownList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDocPart;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtDate;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtComboBox;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTEmpty;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDecimalNumber;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTDataBinding;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTOnOff;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTPlaceholder;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTLock;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTString;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTRPr;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.wordprocessingml.x2006.main.CTSdtPr;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSdtPrImpl extends XmlComplexContentImpl implements CTSdtPr
{
    private static final long serialVersionUID = 1L;
    private static final QName RPR$0;
    private static final QName ALIAS$2;
    private static final QName LOCK$4;
    private static final QName PLACEHOLDER$6;
    private static final QName SHOWINGPLCHDR$8;
    private static final QName DATABINDING$10;
    private static final QName TEMPORARY$12;
    private static final QName ID$14;
    private static final QName TAG$16;
    private static final QName EQUATION$18;
    private static final QName COMBOBOX$20;
    private static final QName DATE$22;
    private static final QName DOCPARTOBJ$24;
    private static final QName DOCPARTLIST$26;
    private static final QName DROPDOWNLIST$28;
    private static final QName PICTURE$30;
    private static final QName RICHTEXT$32;
    private static final QName TEXT$34;
    private static final QName CITATION$36;
    private static final QName GROUP$38;
    private static final QName BIBLIOGRAPHY$40;
    
    public CTSdtPrImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTRPr> getRPrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RPrList extends AbstractList<CTRPr>
            {
                @Override
                public CTRPr get(final int n) {
                    return CTSdtPrImpl.this.getRPrArray(n);
                }
                
                @Override
                public CTRPr set(final int n, final CTRPr ctrPr) {
                    final CTRPr rPrArray = CTSdtPrImpl.this.getRPrArray(n);
                    CTSdtPrImpl.this.setRPrArray(n, ctrPr);
                    return rPrArray;
                }
                
                @Override
                public void add(final int n, final CTRPr ctrPr) {
                    CTSdtPrImpl.this.insertNewRPr(n).set((XmlObject)ctrPr);
                }
                
                @Override
                public CTRPr remove(final int n) {
                    final CTRPr rPrArray = CTSdtPrImpl.this.getRPrArray(n);
                    CTSdtPrImpl.this.removeRPr(n);
                    return rPrArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfRPrArray();
                }
            }
            return new RPrList();
        }
    }
    
    @Deprecated
    public CTRPr[] getRPrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.RPR$0, (List)list);
            final CTRPr[] array = new CTRPr[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTRPr getRPrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTRPr ctrPr = (CTRPr)this.get_store().find_element_user(CTSdtPrImpl.RPR$0, n);
            if (ctrPr == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctrPr;
        }
    }
    
    public int sizeOfRPrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.RPR$0);
        }
    }
    
    public void setRPrArray(final CTRPr[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.RPR$0);
    }
    
    public void setRPrArray(final int n, final CTRPr ctrPr) {
        this.generatedSetterHelperImpl((XmlObject)ctrPr, CTSdtPrImpl.RPR$0, n, (short)2);
    }
    
    public CTRPr insertNewRPr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPr)this.get_store().insert_element_user(CTSdtPrImpl.RPR$0, n);
        }
    }
    
    public CTRPr addNewRPr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTRPr)this.get_store().add_element_user(CTSdtPrImpl.RPR$0);
        }
    }
    
    public void removeRPr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.RPR$0, n);
        }
    }
    
    public List<CTString> getAliasList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AliasList extends AbstractList<CTString>
            {
                @Override
                public CTString get(final int n) {
                    return CTSdtPrImpl.this.getAliasArray(n);
                }
                
                @Override
                public CTString set(final int n, final CTString ctString) {
                    final CTString aliasArray = CTSdtPrImpl.this.getAliasArray(n);
                    CTSdtPrImpl.this.setAliasArray(n, ctString);
                    return aliasArray;
                }
                
                @Override
                public void add(final int n, final CTString ctString) {
                    CTSdtPrImpl.this.insertNewAlias(n).set((XmlObject)ctString);
                }
                
                @Override
                public CTString remove(final int n) {
                    final CTString aliasArray = CTSdtPrImpl.this.getAliasArray(n);
                    CTSdtPrImpl.this.removeAlias(n);
                    return aliasArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfAliasArray();
                }
            }
            return new AliasList();
        }
    }
    
    @Deprecated
    public CTString[] getAliasArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.ALIAS$2, (List)list);
            final CTString[] array = new CTString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTString getAliasArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSdtPrImpl.ALIAS$2, n);
            if (ctString == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctString;
        }
    }
    
    public int sizeOfAliasArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.ALIAS$2);
        }
    }
    
    public void setAliasArray(final CTString[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.ALIAS$2);
    }
    
    public void setAliasArray(final int n, final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSdtPrImpl.ALIAS$2, n, (short)2);
    }
    
    public CTString insertNewAlias(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().insert_element_user(CTSdtPrImpl.ALIAS$2, n);
        }
    }
    
    public CTString addNewAlias() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSdtPrImpl.ALIAS$2);
        }
    }
    
    public void removeAlias(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.ALIAS$2, n);
        }
    }
    
    public List<CTLock> getLockList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class LockList extends AbstractList<CTLock>
            {
                @Override
                public CTLock get(final int n) {
                    return CTSdtPrImpl.this.getLockArray(n);
                }
                
                @Override
                public CTLock set(final int n, final CTLock ctLock) {
                    final CTLock lockArray = CTSdtPrImpl.this.getLockArray(n);
                    CTSdtPrImpl.this.setLockArray(n, ctLock);
                    return lockArray;
                }
                
                @Override
                public void add(final int n, final CTLock ctLock) {
                    CTSdtPrImpl.this.insertNewLock(n).set((XmlObject)ctLock);
                }
                
                @Override
                public CTLock remove(final int n) {
                    final CTLock lockArray = CTSdtPrImpl.this.getLockArray(n);
                    CTSdtPrImpl.this.removeLock(n);
                    return lockArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfLockArray();
                }
            }
            return new LockList();
        }
    }
    
    @Deprecated
    public CTLock[] getLockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.LOCK$4, (List)list);
            final CTLock[] array = new CTLock[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTLock getLockArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTLock ctLock = (CTLock)this.get_store().find_element_user(CTSdtPrImpl.LOCK$4, n);
            if (ctLock == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctLock;
        }
    }
    
    public int sizeOfLockArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.LOCK$4);
        }
    }
    
    public void setLockArray(final CTLock[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.LOCK$4);
    }
    
    public void setLockArray(final int n, final CTLock ctLock) {
        this.generatedSetterHelperImpl((XmlObject)ctLock, CTSdtPrImpl.LOCK$4, n, (short)2);
    }
    
    public CTLock insertNewLock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLock)this.get_store().insert_element_user(CTSdtPrImpl.LOCK$4, n);
        }
    }
    
    public CTLock addNewLock() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTLock)this.get_store().add_element_user(CTSdtPrImpl.LOCK$4);
        }
    }
    
    public void removeLock(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.LOCK$4, n);
        }
    }
    
    public List<CTPlaceholder> getPlaceholderList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PlaceholderList extends AbstractList<CTPlaceholder>
            {
                @Override
                public CTPlaceholder get(final int n) {
                    return CTSdtPrImpl.this.getPlaceholderArray(n);
                }
                
                @Override
                public CTPlaceholder set(final int n, final CTPlaceholder ctPlaceholder) {
                    final CTPlaceholder placeholderArray = CTSdtPrImpl.this.getPlaceholderArray(n);
                    CTSdtPrImpl.this.setPlaceholderArray(n, ctPlaceholder);
                    return placeholderArray;
                }
                
                @Override
                public void add(final int n, final CTPlaceholder ctPlaceholder) {
                    CTSdtPrImpl.this.insertNewPlaceholder(n).set((XmlObject)ctPlaceholder);
                }
                
                @Override
                public CTPlaceholder remove(final int n) {
                    final CTPlaceholder placeholderArray = CTSdtPrImpl.this.getPlaceholderArray(n);
                    CTSdtPrImpl.this.removePlaceholder(n);
                    return placeholderArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfPlaceholderArray();
                }
            }
            return new PlaceholderList();
        }
    }
    
    @Deprecated
    public CTPlaceholder[] getPlaceholderArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.PLACEHOLDER$6, (List)list);
            final CTPlaceholder[] array = new CTPlaceholder[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTPlaceholder getPlaceholderArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTPlaceholder ctPlaceholder = (CTPlaceholder)this.get_store().find_element_user(CTSdtPrImpl.PLACEHOLDER$6, n);
            if (ctPlaceholder == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctPlaceholder;
        }
    }
    
    public int sizeOfPlaceholderArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.PLACEHOLDER$6);
        }
    }
    
    public void setPlaceholderArray(final CTPlaceholder[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.PLACEHOLDER$6);
    }
    
    public void setPlaceholderArray(final int n, final CTPlaceholder ctPlaceholder) {
        this.generatedSetterHelperImpl((XmlObject)ctPlaceholder, CTSdtPrImpl.PLACEHOLDER$6, n, (short)2);
    }
    
    public CTPlaceholder insertNewPlaceholder(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPlaceholder)this.get_store().insert_element_user(CTSdtPrImpl.PLACEHOLDER$6, n);
        }
    }
    
    public CTPlaceholder addNewPlaceholder() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTPlaceholder)this.get_store().add_element_user(CTSdtPrImpl.PLACEHOLDER$6);
        }
    }
    
    public void removePlaceholder(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.PLACEHOLDER$6, n);
        }
    }
    
    public List<CTOnOff> getShowingPlcHdrList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ShowingPlcHdrList extends AbstractList<CTOnOff>
            {
                @Override
                public CTOnOff get(final int n) {
                    return CTSdtPrImpl.this.getShowingPlcHdrArray(n);
                }
                
                @Override
                public CTOnOff set(final int n, final CTOnOff ctOnOff) {
                    final CTOnOff showingPlcHdrArray = CTSdtPrImpl.this.getShowingPlcHdrArray(n);
                    CTSdtPrImpl.this.setShowingPlcHdrArray(n, ctOnOff);
                    return showingPlcHdrArray;
                }
                
                @Override
                public void add(final int n, final CTOnOff ctOnOff) {
                    CTSdtPrImpl.this.insertNewShowingPlcHdr(n).set((XmlObject)ctOnOff);
                }
                
                @Override
                public CTOnOff remove(final int n) {
                    final CTOnOff showingPlcHdrArray = CTSdtPrImpl.this.getShowingPlcHdrArray(n);
                    CTSdtPrImpl.this.removeShowingPlcHdr(n);
                    return showingPlcHdrArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfShowingPlcHdrArray();
                }
            }
            return new ShowingPlcHdrList();
        }
    }
    
    @Deprecated
    public CTOnOff[] getShowingPlcHdrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.SHOWINGPLCHDR$8, (List)list);
            final CTOnOff[] array = new CTOnOff[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOnOff getShowingPlcHdrArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSdtPrImpl.SHOWINGPLCHDR$8, n);
            if (ctOnOff == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOnOff;
        }
    }
    
    public int sizeOfShowingPlcHdrArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.SHOWINGPLCHDR$8);
        }
    }
    
    public void setShowingPlcHdrArray(final CTOnOff[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.SHOWINGPLCHDR$8);
    }
    
    public void setShowingPlcHdrArray(final int n, final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSdtPrImpl.SHOWINGPLCHDR$8, n, (short)2);
    }
    
    public CTOnOff insertNewShowingPlcHdr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().insert_element_user(CTSdtPrImpl.SHOWINGPLCHDR$8, n);
        }
    }
    
    public CTOnOff addNewShowingPlcHdr() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSdtPrImpl.SHOWINGPLCHDR$8);
        }
    }
    
    public void removeShowingPlcHdr(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.SHOWINGPLCHDR$8, n);
        }
    }
    
    public List<CTDataBinding> getDataBindingList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DataBindingList extends AbstractList<CTDataBinding>
            {
                @Override
                public CTDataBinding get(final int n) {
                    return CTSdtPrImpl.this.getDataBindingArray(n);
                }
                
                @Override
                public CTDataBinding set(final int n, final CTDataBinding ctDataBinding) {
                    final CTDataBinding dataBindingArray = CTSdtPrImpl.this.getDataBindingArray(n);
                    CTSdtPrImpl.this.setDataBindingArray(n, ctDataBinding);
                    return dataBindingArray;
                }
                
                @Override
                public void add(final int n, final CTDataBinding ctDataBinding) {
                    CTSdtPrImpl.this.insertNewDataBinding(n).set((XmlObject)ctDataBinding);
                }
                
                @Override
                public CTDataBinding remove(final int n) {
                    final CTDataBinding dataBindingArray = CTSdtPrImpl.this.getDataBindingArray(n);
                    CTSdtPrImpl.this.removeDataBinding(n);
                    return dataBindingArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfDataBindingArray();
                }
            }
            return new DataBindingList();
        }
    }
    
    @Deprecated
    public CTDataBinding[] getDataBindingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.DATABINDING$10, (List)list);
            final CTDataBinding[] array = new CTDataBinding[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDataBinding getDataBindingArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDataBinding ctDataBinding = (CTDataBinding)this.get_store().find_element_user(CTSdtPrImpl.DATABINDING$10, n);
            if (ctDataBinding == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDataBinding;
        }
    }
    
    public int sizeOfDataBindingArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.DATABINDING$10);
        }
    }
    
    public void setDataBindingArray(final CTDataBinding[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.DATABINDING$10);
    }
    
    public void setDataBindingArray(final int n, final CTDataBinding ctDataBinding) {
        this.generatedSetterHelperImpl((XmlObject)ctDataBinding, CTSdtPrImpl.DATABINDING$10, n, (short)2);
    }
    
    public CTDataBinding insertNewDataBinding(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataBinding)this.get_store().insert_element_user(CTSdtPrImpl.DATABINDING$10, n);
        }
    }
    
    public CTDataBinding addNewDataBinding() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDataBinding)this.get_store().add_element_user(CTSdtPrImpl.DATABINDING$10);
        }
    }
    
    public void removeDataBinding(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.DATABINDING$10, n);
        }
    }
    
    public List<CTOnOff> getTemporaryList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TemporaryList extends AbstractList<CTOnOff>
            {
                @Override
                public CTOnOff get(final int n) {
                    return CTSdtPrImpl.this.getTemporaryArray(n);
                }
                
                @Override
                public CTOnOff set(final int n, final CTOnOff ctOnOff) {
                    final CTOnOff temporaryArray = CTSdtPrImpl.this.getTemporaryArray(n);
                    CTSdtPrImpl.this.setTemporaryArray(n, ctOnOff);
                    return temporaryArray;
                }
                
                @Override
                public void add(final int n, final CTOnOff ctOnOff) {
                    CTSdtPrImpl.this.insertNewTemporary(n).set((XmlObject)ctOnOff);
                }
                
                @Override
                public CTOnOff remove(final int n) {
                    final CTOnOff temporaryArray = CTSdtPrImpl.this.getTemporaryArray(n);
                    CTSdtPrImpl.this.removeTemporary(n);
                    return temporaryArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfTemporaryArray();
                }
            }
            return new TemporaryList();
        }
    }
    
    @Deprecated
    public CTOnOff[] getTemporaryArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.TEMPORARY$12, (List)list);
            final CTOnOff[] array = new CTOnOff[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTOnOff getTemporaryArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTOnOff ctOnOff = (CTOnOff)this.get_store().find_element_user(CTSdtPrImpl.TEMPORARY$12, n);
            if (ctOnOff == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctOnOff;
        }
    }
    
    public int sizeOfTemporaryArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.TEMPORARY$12);
        }
    }
    
    public void setTemporaryArray(final CTOnOff[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.TEMPORARY$12);
    }
    
    public void setTemporaryArray(final int n, final CTOnOff ctOnOff) {
        this.generatedSetterHelperImpl((XmlObject)ctOnOff, CTSdtPrImpl.TEMPORARY$12, n, (short)2);
    }
    
    public CTOnOff insertNewTemporary(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().insert_element_user(CTSdtPrImpl.TEMPORARY$12, n);
        }
    }
    
    public CTOnOff addNewTemporary() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTOnOff)this.get_store().add_element_user(CTSdtPrImpl.TEMPORARY$12);
        }
    }
    
    public void removeTemporary(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.TEMPORARY$12, n);
        }
    }
    
    public List<CTDecimalNumber> getIdList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class IdList extends AbstractList<CTDecimalNumber>
            {
                @Override
                public CTDecimalNumber get(final int n) {
                    return CTSdtPrImpl.this.getIdArray(n);
                }
                
                @Override
                public CTDecimalNumber set(final int n, final CTDecimalNumber ctDecimalNumber) {
                    final CTDecimalNumber idArray = CTSdtPrImpl.this.getIdArray(n);
                    CTSdtPrImpl.this.setIdArray(n, ctDecimalNumber);
                    return idArray;
                }
                
                @Override
                public void add(final int n, final CTDecimalNumber ctDecimalNumber) {
                    CTSdtPrImpl.this.insertNewId(n).set((XmlObject)ctDecimalNumber);
                }
                
                @Override
                public CTDecimalNumber remove(final int n) {
                    final CTDecimalNumber idArray = CTSdtPrImpl.this.getIdArray(n);
                    CTSdtPrImpl.this.removeId(n);
                    return idArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfIdArray();
                }
            }
            return new IdList();
        }
    }
    
    @Deprecated
    public CTDecimalNumber[] getIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.ID$14, (List)list);
            final CTDecimalNumber[] array = new CTDecimalNumber[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDecimalNumber getIdArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDecimalNumber ctDecimalNumber = (CTDecimalNumber)this.get_store().find_element_user(CTSdtPrImpl.ID$14, n);
            if (ctDecimalNumber == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDecimalNumber;
        }
    }
    
    public int sizeOfIdArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.ID$14);
        }
    }
    
    public void setIdArray(final CTDecimalNumber[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.ID$14);
    }
    
    public void setIdArray(final int n, final CTDecimalNumber ctDecimalNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctDecimalNumber, CTSdtPrImpl.ID$14, n, (short)2);
    }
    
    public CTDecimalNumber insertNewId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().insert_element_user(CTSdtPrImpl.ID$14, n);
        }
    }
    
    public CTDecimalNumber addNewId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDecimalNumber)this.get_store().add_element_user(CTSdtPrImpl.ID$14);
        }
    }
    
    public void removeId(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.ID$14, n);
        }
    }
    
    public List<CTString> getTagList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TagList extends AbstractList<CTString>
            {
                @Override
                public CTString get(final int n) {
                    return CTSdtPrImpl.this.getTagArray(n);
                }
                
                @Override
                public CTString set(final int n, final CTString ctString) {
                    final CTString tagArray = CTSdtPrImpl.this.getTagArray(n);
                    CTSdtPrImpl.this.setTagArray(n, ctString);
                    return tagArray;
                }
                
                @Override
                public void add(final int n, final CTString ctString) {
                    CTSdtPrImpl.this.insertNewTag(n).set((XmlObject)ctString);
                }
                
                @Override
                public CTString remove(final int n) {
                    final CTString tagArray = CTSdtPrImpl.this.getTagArray(n);
                    CTSdtPrImpl.this.removeTag(n);
                    return tagArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfTagArray();
                }
            }
            return new TagList();
        }
    }
    
    @Deprecated
    public CTString[] getTagArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.TAG$16, (List)list);
            final CTString[] array = new CTString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTString getTagArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSdtPrImpl.TAG$16, n);
            if (ctString == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctString;
        }
    }
    
    public int sizeOfTagArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.TAG$16);
        }
    }
    
    public void setTagArray(final CTString[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.TAG$16);
    }
    
    public void setTagArray(final int n, final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSdtPrImpl.TAG$16, n, (short)2);
    }
    
    public CTString insertNewTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().insert_element_user(CTSdtPrImpl.TAG$16, n);
        }
    }
    
    public CTString addNewTag() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSdtPrImpl.TAG$16);
        }
    }
    
    public void removeTag(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.TAG$16, n);
        }
    }
    
    public List<CTEmpty> getEquationList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EquationList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTSdtPrImpl.this.getEquationArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty equationArray = CTSdtPrImpl.this.getEquationArray(n);
                    CTSdtPrImpl.this.setEquationArray(n, ctEmpty);
                    return equationArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTSdtPrImpl.this.insertNewEquation(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty equationArray = CTSdtPrImpl.this.getEquationArray(n);
                    CTSdtPrImpl.this.removeEquation(n);
                    return equationArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfEquationArray();
                }
            }
            return new EquationList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getEquationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.EQUATION$18, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getEquationArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTSdtPrImpl.EQUATION$18, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfEquationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.EQUATION$18);
        }
    }
    
    public void setEquationArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.EQUATION$18);
    }
    
    public void setEquationArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTSdtPrImpl.EQUATION$18, n, (short)2);
    }
    
    public CTEmpty insertNewEquation(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTSdtPrImpl.EQUATION$18, n);
        }
    }
    
    public CTEmpty addNewEquation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTSdtPrImpl.EQUATION$18);
        }
    }
    
    public void removeEquation(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.EQUATION$18, n);
        }
    }
    
    public List<CTSdtComboBox> getComboBoxList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ComboBoxList extends AbstractList<CTSdtComboBox>
            {
                @Override
                public CTSdtComboBox get(final int n) {
                    return CTSdtPrImpl.this.getComboBoxArray(n);
                }
                
                @Override
                public CTSdtComboBox set(final int n, final CTSdtComboBox ctSdtComboBox) {
                    final CTSdtComboBox comboBoxArray = CTSdtPrImpl.this.getComboBoxArray(n);
                    CTSdtPrImpl.this.setComboBoxArray(n, ctSdtComboBox);
                    return comboBoxArray;
                }
                
                @Override
                public void add(final int n, final CTSdtComboBox ctSdtComboBox) {
                    CTSdtPrImpl.this.insertNewComboBox(n).set((XmlObject)ctSdtComboBox);
                }
                
                @Override
                public CTSdtComboBox remove(final int n) {
                    final CTSdtComboBox comboBoxArray = CTSdtPrImpl.this.getComboBoxArray(n);
                    CTSdtPrImpl.this.removeComboBox(n);
                    return comboBoxArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfComboBoxArray();
                }
            }
            return new ComboBoxList();
        }
    }
    
    @Deprecated
    public CTSdtComboBox[] getComboBoxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.COMBOBOX$20, (List)list);
            final CTSdtComboBox[] array = new CTSdtComboBox[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtComboBox getComboBoxArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtComboBox ctSdtComboBox = (CTSdtComboBox)this.get_store().find_element_user(CTSdtPrImpl.COMBOBOX$20, n);
            if (ctSdtComboBox == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtComboBox;
        }
    }
    
    public int sizeOfComboBoxArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.COMBOBOX$20);
        }
    }
    
    public void setComboBoxArray(final CTSdtComboBox[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.COMBOBOX$20);
    }
    
    public void setComboBoxArray(final int n, final CTSdtComboBox ctSdtComboBox) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtComboBox, CTSdtPrImpl.COMBOBOX$20, n, (short)2);
    }
    
    public CTSdtComboBox insertNewComboBox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtComboBox)this.get_store().insert_element_user(CTSdtPrImpl.COMBOBOX$20, n);
        }
    }
    
    public CTSdtComboBox addNewComboBox() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtComboBox)this.get_store().add_element_user(CTSdtPrImpl.COMBOBOX$20);
        }
    }
    
    public void removeComboBox(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.COMBOBOX$20, n);
        }
    }
    
    public List<CTSdtDate> getDateList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DateList extends AbstractList<CTSdtDate>
            {
                @Override
                public CTSdtDate get(final int n) {
                    return CTSdtPrImpl.this.getDateArray(n);
                }
                
                @Override
                public CTSdtDate set(final int n, final CTSdtDate ctSdtDate) {
                    final CTSdtDate dateArray = CTSdtPrImpl.this.getDateArray(n);
                    CTSdtPrImpl.this.setDateArray(n, ctSdtDate);
                    return dateArray;
                }
                
                @Override
                public void add(final int n, final CTSdtDate ctSdtDate) {
                    CTSdtPrImpl.this.insertNewDate(n).set((XmlObject)ctSdtDate);
                }
                
                @Override
                public CTSdtDate remove(final int n) {
                    final CTSdtDate dateArray = CTSdtPrImpl.this.getDateArray(n);
                    CTSdtPrImpl.this.removeDate(n);
                    return dateArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfDateArray();
                }
            }
            return new DateList();
        }
    }
    
    @Deprecated
    public CTSdtDate[] getDateArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.DATE$22, (List)list);
            final CTSdtDate[] array = new CTSdtDate[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtDate getDateArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtDate ctSdtDate = (CTSdtDate)this.get_store().find_element_user(CTSdtPrImpl.DATE$22, n);
            if (ctSdtDate == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtDate;
        }
    }
    
    public int sizeOfDateArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.DATE$22);
        }
    }
    
    public void setDateArray(final CTSdtDate[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.DATE$22);
    }
    
    public void setDateArray(final int n, final CTSdtDate ctSdtDate) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtDate, CTSdtPrImpl.DATE$22, n, (short)2);
    }
    
    public CTSdtDate insertNewDate(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtDate)this.get_store().insert_element_user(CTSdtPrImpl.DATE$22, n);
        }
    }
    
    public CTSdtDate addNewDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtDate)this.get_store().add_element_user(CTSdtPrImpl.DATE$22);
        }
    }
    
    public void removeDate(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.DATE$22, n);
        }
    }
    
    public List<CTSdtDocPart> getDocPartObjList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DocPartObjList extends AbstractList<CTSdtDocPart>
            {
                @Override
                public CTSdtDocPart get(final int n) {
                    return CTSdtPrImpl.this.getDocPartObjArray(n);
                }
                
                @Override
                public CTSdtDocPart set(final int n, final CTSdtDocPart ctSdtDocPart) {
                    final CTSdtDocPart docPartObjArray = CTSdtPrImpl.this.getDocPartObjArray(n);
                    CTSdtPrImpl.this.setDocPartObjArray(n, ctSdtDocPart);
                    return docPartObjArray;
                }
                
                @Override
                public void add(final int n, final CTSdtDocPart ctSdtDocPart) {
                    CTSdtPrImpl.this.insertNewDocPartObj(n).set((XmlObject)ctSdtDocPart);
                }
                
                @Override
                public CTSdtDocPart remove(final int n) {
                    final CTSdtDocPart docPartObjArray = CTSdtPrImpl.this.getDocPartObjArray(n);
                    CTSdtPrImpl.this.removeDocPartObj(n);
                    return docPartObjArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfDocPartObjArray();
                }
            }
            return new DocPartObjList();
        }
    }
    
    @Deprecated
    public CTSdtDocPart[] getDocPartObjArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.DOCPARTOBJ$24, (List)list);
            final CTSdtDocPart[] array = new CTSdtDocPart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtDocPart getDocPartObjArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtDocPart ctSdtDocPart = (CTSdtDocPart)this.get_store().find_element_user(CTSdtPrImpl.DOCPARTOBJ$24, n);
            if (ctSdtDocPart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtDocPart;
        }
    }
    
    public int sizeOfDocPartObjArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.DOCPARTOBJ$24);
        }
    }
    
    public void setDocPartObjArray(final CTSdtDocPart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.DOCPARTOBJ$24);
    }
    
    public void setDocPartObjArray(final int n, final CTSdtDocPart ctSdtDocPart) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtDocPart, CTSdtPrImpl.DOCPARTOBJ$24, n, (short)2);
    }
    
    public CTSdtDocPart insertNewDocPartObj(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtDocPart)this.get_store().insert_element_user(CTSdtPrImpl.DOCPARTOBJ$24, n);
        }
    }
    
    public CTSdtDocPart addNewDocPartObj() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtDocPart)this.get_store().add_element_user(CTSdtPrImpl.DOCPARTOBJ$24);
        }
    }
    
    public void removeDocPartObj(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.DOCPARTOBJ$24, n);
        }
    }
    
    public List<CTSdtDocPart> getDocPartListList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DocPartListList extends AbstractList<CTSdtDocPart>
            {
                @Override
                public CTSdtDocPart get(final int n) {
                    return CTSdtPrImpl.this.getDocPartListArray(n);
                }
                
                @Override
                public CTSdtDocPart set(final int n, final CTSdtDocPart ctSdtDocPart) {
                    final CTSdtDocPart docPartListArray = CTSdtPrImpl.this.getDocPartListArray(n);
                    CTSdtPrImpl.this.setDocPartListArray(n, ctSdtDocPart);
                    return docPartListArray;
                }
                
                @Override
                public void add(final int n, final CTSdtDocPart ctSdtDocPart) {
                    CTSdtPrImpl.this.insertNewDocPartList(n).set((XmlObject)ctSdtDocPart);
                }
                
                @Override
                public CTSdtDocPart remove(final int n) {
                    final CTSdtDocPart docPartListArray = CTSdtPrImpl.this.getDocPartListArray(n);
                    CTSdtPrImpl.this.removeDocPartList(n);
                    return docPartListArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfDocPartListArray();
                }
            }
            return new DocPartListList();
        }
    }
    
    @Deprecated
    public CTSdtDocPart[] getDocPartListArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.DOCPARTLIST$26, (List)list);
            final CTSdtDocPart[] array = new CTSdtDocPart[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtDocPart getDocPartListArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtDocPart ctSdtDocPart = (CTSdtDocPart)this.get_store().find_element_user(CTSdtPrImpl.DOCPARTLIST$26, n);
            if (ctSdtDocPart == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtDocPart;
        }
    }
    
    public int sizeOfDocPartListArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.DOCPARTLIST$26);
        }
    }
    
    public void setDocPartListArray(final CTSdtDocPart[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.DOCPARTLIST$26);
    }
    
    public void setDocPartListArray(final int n, final CTSdtDocPart ctSdtDocPart) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtDocPart, CTSdtPrImpl.DOCPARTLIST$26, n, (short)2);
    }
    
    public CTSdtDocPart insertNewDocPartList(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtDocPart)this.get_store().insert_element_user(CTSdtPrImpl.DOCPARTLIST$26, n);
        }
    }
    
    public CTSdtDocPart addNewDocPartList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtDocPart)this.get_store().add_element_user(CTSdtPrImpl.DOCPARTLIST$26);
        }
    }
    
    public void removeDocPartList(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.DOCPARTLIST$26, n);
        }
    }
    
    public List<CTSdtDropDownList> getDropDownListList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DropDownListList extends AbstractList<CTSdtDropDownList>
            {
                @Override
                public CTSdtDropDownList get(final int n) {
                    return CTSdtPrImpl.this.getDropDownListArray(n);
                }
                
                @Override
                public CTSdtDropDownList set(final int n, final CTSdtDropDownList list) {
                    final CTSdtDropDownList dropDownListArray = CTSdtPrImpl.this.getDropDownListArray(n);
                    CTSdtPrImpl.this.setDropDownListArray(n, list);
                    return dropDownListArray;
                }
                
                @Override
                public void add(final int n, final CTSdtDropDownList list) {
                    CTSdtPrImpl.this.insertNewDropDownList(n).set((XmlObject)list);
                }
                
                @Override
                public CTSdtDropDownList remove(final int n) {
                    final CTSdtDropDownList dropDownListArray = CTSdtPrImpl.this.getDropDownListArray(n);
                    CTSdtPrImpl.this.removeDropDownList(n);
                    return dropDownListArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfDropDownListArray();
                }
            }
            return new DropDownListList();
        }
    }
    
    @Deprecated
    public CTSdtDropDownList[] getDropDownListArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.DROPDOWNLIST$28, (List)list);
            final CTSdtDropDownList[] array = new CTSdtDropDownList[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtDropDownList getDropDownListArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtDropDownList list = (CTSdtDropDownList)this.get_store().find_element_user(CTSdtPrImpl.DROPDOWNLIST$28, n);
            if (list == null) {
                throw new IndexOutOfBoundsException();
            }
            return list;
        }
    }
    
    public int sizeOfDropDownListArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.DROPDOWNLIST$28);
        }
    }
    
    public void setDropDownListArray(final CTSdtDropDownList[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.DROPDOWNLIST$28);
    }
    
    public void setDropDownListArray(final int n, final CTSdtDropDownList list) {
        this.generatedSetterHelperImpl((XmlObject)list, CTSdtPrImpl.DROPDOWNLIST$28, n, (short)2);
    }
    
    public CTSdtDropDownList insertNewDropDownList(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtDropDownList)this.get_store().insert_element_user(CTSdtPrImpl.DROPDOWNLIST$28, n);
        }
    }
    
    public CTSdtDropDownList addNewDropDownList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtDropDownList)this.get_store().add_element_user(CTSdtPrImpl.DROPDOWNLIST$28);
        }
    }
    
    public void removeDropDownList(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.DROPDOWNLIST$28, n);
        }
    }
    
    public List<CTEmpty> getPictureList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class PictureList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTSdtPrImpl.this.getPictureArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty pictureArray = CTSdtPrImpl.this.getPictureArray(n);
                    CTSdtPrImpl.this.setPictureArray(n, ctEmpty);
                    return pictureArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTSdtPrImpl.this.insertNewPicture(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty pictureArray = CTSdtPrImpl.this.getPictureArray(n);
                    CTSdtPrImpl.this.removePicture(n);
                    return pictureArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfPictureArray();
                }
            }
            return new PictureList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getPictureArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.PICTURE$30, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getPictureArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTSdtPrImpl.PICTURE$30, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfPictureArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.PICTURE$30);
        }
    }
    
    public void setPictureArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.PICTURE$30);
    }
    
    public void setPictureArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTSdtPrImpl.PICTURE$30, n, (short)2);
    }
    
    public CTEmpty insertNewPicture(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTSdtPrImpl.PICTURE$30, n);
        }
    }
    
    public CTEmpty addNewPicture() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTSdtPrImpl.PICTURE$30);
        }
    }
    
    public void removePicture(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.PICTURE$30, n);
        }
    }
    
    public List<CTEmpty> getRichTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RichTextList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTSdtPrImpl.this.getRichTextArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty richTextArray = CTSdtPrImpl.this.getRichTextArray(n);
                    CTSdtPrImpl.this.setRichTextArray(n, ctEmpty);
                    return richTextArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTSdtPrImpl.this.insertNewRichText(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty richTextArray = CTSdtPrImpl.this.getRichTextArray(n);
                    CTSdtPrImpl.this.removeRichText(n);
                    return richTextArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfRichTextArray();
                }
            }
            return new RichTextList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getRichTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.RICHTEXT$32, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getRichTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTSdtPrImpl.RICHTEXT$32, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfRichTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.RICHTEXT$32);
        }
    }
    
    public void setRichTextArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.RICHTEXT$32);
    }
    
    public void setRichTextArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTSdtPrImpl.RICHTEXT$32, n, (short)2);
    }
    
    public CTEmpty insertNewRichText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTSdtPrImpl.RICHTEXT$32, n);
        }
    }
    
    public CTEmpty addNewRichText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTSdtPrImpl.RICHTEXT$32);
        }
    }
    
    public void removeRichText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.RICHTEXT$32, n);
        }
    }
    
    public List<CTSdtText> getTextList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class TextList extends AbstractList<CTSdtText>
            {
                @Override
                public CTSdtText get(final int n) {
                    return CTSdtPrImpl.this.getTextArray(n);
                }
                
                @Override
                public CTSdtText set(final int n, final CTSdtText ctSdtText) {
                    final CTSdtText textArray = CTSdtPrImpl.this.getTextArray(n);
                    CTSdtPrImpl.this.setTextArray(n, ctSdtText);
                    return textArray;
                }
                
                @Override
                public void add(final int n, final CTSdtText ctSdtText) {
                    CTSdtPrImpl.this.insertNewText(n).set((XmlObject)ctSdtText);
                }
                
                @Override
                public CTSdtText remove(final int n) {
                    final CTSdtText textArray = CTSdtPrImpl.this.getTextArray(n);
                    CTSdtPrImpl.this.removeText(n);
                    return textArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfTextArray();
                }
            }
            return new TextList();
        }
    }
    
    @Deprecated
    public CTSdtText[] getTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.TEXT$34, (List)list);
            final CTSdtText[] array = new CTSdtText[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTSdtText getTextArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTSdtText ctSdtText = (CTSdtText)this.get_store().find_element_user(CTSdtPrImpl.TEXT$34, n);
            if (ctSdtText == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctSdtText;
        }
    }
    
    public int sizeOfTextArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.TEXT$34);
        }
    }
    
    public void setTextArray(final CTSdtText[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.TEXT$34);
    }
    
    public void setTextArray(final int n, final CTSdtText ctSdtText) {
        this.generatedSetterHelperImpl((XmlObject)ctSdtText, CTSdtPrImpl.TEXT$34, n, (short)2);
    }
    
    public CTSdtText insertNewText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtText)this.get_store().insert_element_user(CTSdtPrImpl.TEXT$34, n);
        }
    }
    
    public CTSdtText addNewText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTSdtText)this.get_store().add_element_user(CTSdtPrImpl.TEXT$34);
        }
    }
    
    public void removeText(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.TEXT$34, n);
        }
    }
    
    public List<CTEmpty> getCitationList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CitationList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTSdtPrImpl.this.getCitationArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty citationArray = CTSdtPrImpl.this.getCitationArray(n);
                    CTSdtPrImpl.this.setCitationArray(n, ctEmpty);
                    return citationArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTSdtPrImpl.this.insertNewCitation(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty citationArray = CTSdtPrImpl.this.getCitationArray(n);
                    CTSdtPrImpl.this.removeCitation(n);
                    return citationArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfCitationArray();
                }
            }
            return new CitationList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getCitationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.CITATION$36, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getCitationArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTSdtPrImpl.CITATION$36, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfCitationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.CITATION$36);
        }
    }
    
    public void setCitationArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.CITATION$36);
    }
    
    public void setCitationArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTSdtPrImpl.CITATION$36, n, (short)2);
    }
    
    public CTEmpty insertNewCitation(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTSdtPrImpl.CITATION$36, n);
        }
    }
    
    public CTEmpty addNewCitation() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTSdtPrImpl.CITATION$36);
        }
    }
    
    public void removeCitation(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.CITATION$36, n);
        }
    }
    
    public List<CTEmpty> getGroupList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class GroupList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTSdtPrImpl.this.getGroupArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty groupArray = CTSdtPrImpl.this.getGroupArray(n);
                    CTSdtPrImpl.this.setGroupArray(n, ctEmpty);
                    return groupArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTSdtPrImpl.this.insertNewGroup(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty groupArray = CTSdtPrImpl.this.getGroupArray(n);
                    CTSdtPrImpl.this.removeGroup(n);
                    return groupArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfGroupArray();
                }
            }
            return new GroupList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.GROUP$38, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getGroupArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTSdtPrImpl.GROUP$38, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.GROUP$38);
        }
    }
    
    public void setGroupArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.GROUP$38);
    }
    
    public void setGroupArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTSdtPrImpl.GROUP$38, n, (short)2);
    }
    
    public CTEmpty insertNewGroup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTSdtPrImpl.GROUP$38, n);
        }
    }
    
    public CTEmpty addNewGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTSdtPrImpl.GROUP$38);
        }
    }
    
    public void removeGroup(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.GROUP$38, n);
        }
    }
    
    public List<CTEmpty> getBibliographyList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BibliographyList extends AbstractList<CTEmpty>
            {
                @Override
                public CTEmpty get(final int n) {
                    return CTSdtPrImpl.this.getBibliographyArray(n);
                }
                
                @Override
                public CTEmpty set(final int n, final CTEmpty ctEmpty) {
                    final CTEmpty bibliographyArray = CTSdtPrImpl.this.getBibliographyArray(n);
                    CTSdtPrImpl.this.setBibliographyArray(n, ctEmpty);
                    return bibliographyArray;
                }
                
                @Override
                public void add(final int n, final CTEmpty ctEmpty) {
                    CTSdtPrImpl.this.insertNewBibliography(n).set((XmlObject)ctEmpty);
                }
                
                @Override
                public CTEmpty remove(final int n) {
                    final CTEmpty bibliographyArray = CTSdtPrImpl.this.getBibliographyArray(n);
                    CTSdtPrImpl.this.removeBibliography(n);
                    return bibliographyArray;
                }
                
                @Override
                public int size() {
                    return CTSdtPrImpl.this.sizeOfBibliographyArray();
                }
            }
            return new BibliographyList();
        }
    }
    
    @Deprecated
    public CTEmpty[] getBibliographyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSdtPrImpl.BIBLIOGRAPHY$40, (List)list);
            final CTEmpty[] array = new CTEmpty[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTEmpty getBibliographyArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTEmpty ctEmpty = (CTEmpty)this.get_store().find_element_user(CTSdtPrImpl.BIBLIOGRAPHY$40, n);
            if (ctEmpty == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctEmpty;
        }
    }
    
    public int sizeOfBibliographyArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSdtPrImpl.BIBLIOGRAPHY$40);
        }
    }
    
    public void setBibliographyArray(final CTEmpty[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSdtPrImpl.BIBLIOGRAPHY$40);
    }
    
    public void setBibliographyArray(final int n, final CTEmpty ctEmpty) {
        this.generatedSetterHelperImpl((XmlObject)ctEmpty, CTSdtPrImpl.BIBLIOGRAPHY$40, n, (short)2);
    }
    
    public CTEmpty insertNewBibliography(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().insert_element_user(CTSdtPrImpl.BIBLIOGRAPHY$40, n);
        }
    }
    
    public CTEmpty addNewBibliography() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTEmpty)this.get_store().add_element_user(CTSdtPrImpl.BIBLIOGRAPHY$40);
        }
    }
    
    public void removeBibliography(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSdtPrImpl.BIBLIOGRAPHY$40, n);
        }
    }
    
    static {
        RPR$0 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "rPr");
        ALIAS$2 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "alias");
        LOCK$4 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "lock");
        PLACEHOLDER$6 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "placeholder");
        SHOWINGPLCHDR$8 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "showingPlcHdr");
        DATABINDING$10 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dataBinding");
        TEMPORARY$12 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "temporary");
        ID$14 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "id");
        TAG$16 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "tag");
        EQUATION$18 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "equation");
        COMBOBOX$20 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "comboBox");
        DATE$22 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "date");
        DOCPARTOBJ$24 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docPartObj");
        DOCPARTLIST$26 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "docPartList");
        DROPDOWNLIST$28 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "dropDownList");
        PICTURE$30 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "picture");
        RICHTEXT$32 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "richText");
        TEXT$34 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "text");
        CITATION$36 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "citation");
        GROUP$38 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "group");
        BIBLIOGRAPHY$40 = new QName("http://schemas.openxmlformats.org/wordprocessingml/2006/main", "bibliography");
    }
}
