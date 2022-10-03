package org.openxmlformats.schemas.spreadsheetml.x2006.main.impl;

import org.apache.xmlbeans.XmlUnsignedInt;
import org.apache.xmlbeans.XmlDateTime;
import java.util.Calendar;
import org.apache.xmlbeans.XmlDouble;
import org.apache.xmlbeans.XmlBoolean;
import org.apache.xmlbeans.SimpleValue;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTDateTime;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTString;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTError;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTBoolean;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTNumber;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTMissing;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.openxmlformats.schemas.spreadsheetml.x2006.main.CTSharedItems;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class CTSharedItemsImpl extends XmlComplexContentImpl implements CTSharedItems
{
    private static final long serialVersionUID = 1L;
    private static final QName M$0;
    private static final QName N$2;
    private static final QName B$4;
    private static final QName E$6;
    private static final QName S$8;
    private static final QName D$10;
    private static final QName CONTAINSSEMIMIXEDTYPES$12;
    private static final QName CONTAINSNONDATE$14;
    private static final QName CONTAINSDATE$16;
    private static final QName CONTAINSSTRING$18;
    private static final QName CONTAINSBLANK$20;
    private static final QName CONTAINSMIXEDTYPES$22;
    private static final QName CONTAINSNUMBER$24;
    private static final QName CONTAINSINTEGER$26;
    private static final QName MINVALUE$28;
    private static final QName MAXVALUE$30;
    private static final QName MINDATE$32;
    private static final QName MAXDATE$34;
    private static final QName COUNT$36;
    private static final QName LONGTEXT$38;
    
    public CTSharedItemsImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CTMissing> getMList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class MList extends AbstractList<CTMissing>
            {
                @Override
                public CTMissing get(final int n) {
                    return CTSharedItemsImpl.this.getMArray(n);
                }
                
                @Override
                public CTMissing set(final int n, final CTMissing ctMissing) {
                    final CTMissing mArray = CTSharedItemsImpl.this.getMArray(n);
                    CTSharedItemsImpl.this.setMArray(n, ctMissing);
                    return mArray;
                }
                
                @Override
                public void add(final int n, final CTMissing ctMissing) {
                    CTSharedItemsImpl.this.insertNewM(n).set((XmlObject)ctMissing);
                }
                
                @Override
                public CTMissing remove(final int n) {
                    final CTMissing mArray = CTSharedItemsImpl.this.getMArray(n);
                    CTSharedItemsImpl.this.removeM(n);
                    return mArray;
                }
                
                @Override
                public int size() {
                    return CTSharedItemsImpl.this.sizeOfMArray();
                }
            }
            return new MList();
        }
    }
    
    @Deprecated
    public CTMissing[] getMArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSharedItemsImpl.M$0, (List)list);
            final CTMissing[] array = new CTMissing[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTMissing getMArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTMissing ctMissing = (CTMissing)this.get_store().find_element_user(CTSharedItemsImpl.M$0, n);
            if (ctMissing == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctMissing;
        }
    }
    
    public int sizeOfMArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSharedItemsImpl.M$0);
        }
    }
    
    public void setMArray(final CTMissing[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSharedItemsImpl.M$0);
    }
    
    public void setMArray(final int n, final CTMissing ctMissing) {
        this.generatedSetterHelperImpl((XmlObject)ctMissing, CTSharedItemsImpl.M$0, n, (short)2);
    }
    
    public CTMissing insertNewM(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMissing)this.get_store().insert_element_user(CTSharedItemsImpl.M$0, n);
        }
    }
    
    public CTMissing addNewM() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTMissing)this.get_store().add_element_user(CTSharedItemsImpl.M$0);
        }
    }
    
    public void removeM(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSharedItemsImpl.M$0, n);
        }
    }
    
    public List<CTNumber> getNList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class NList extends AbstractList<CTNumber>
            {
                @Override
                public CTNumber get(final int n) {
                    return CTSharedItemsImpl.this.getNArray(n);
                }
                
                @Override
                public CTNumber set(final int n, final CTNumber ctNumber) {
                    final CTNumber nArray = CTSharedItemsImpl.this.getNArray(n);
                    CTSharedItemsImpl.this.setNArray(n, ctNumber);
                    return nArray;
                }
                
                @Override
                public void add(final int n, final CTNumber ctNumber) {
                    CTSharedItemsImpl.this.insertNewN(n).set((XmlObject)ctNumber);
                }
                
                @Override
                public CTNumber remove(final int n) {
                    final CTNumber nArray = CTSharedItemsImpl.this.getNArray(n);
                    CTSharedItemsImpl.this.removeN(n);
                    return nArray;
                }
                
                @Override
                public int size() {
                    return CTSharedItemsImpl.this.sizeOfNArray();
                }
            }
            return new NList();
        }
    }
    
    @Deprecated
    public CTNumber[] getNArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSharedItemsImpl.N$2, (List)list);
            final CTNumber[] array = new CTNumber[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTNumber getNArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTNumber ctNumber = (CTNumber)this.get_store().find_element_user(CTSharedItemsImpl.N$2, n);
            if (ctNumber == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctNumber;
        }
    }
    
    public int sizeOfNArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSharedItemsImpl.N$2);
        }
    }
    
    public void setNArray(final CTNumber[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSharedItemsImpl.N$2);
    }
    
    public void setNArray(final int n, final CTNumber ctNumber) {
        this.generatedSetterHelperImpl((XmlObject)ctNumber, CTSharedItemsImpl.N$2, n, (short)2);
    }
    
    public CTNumber insertNewN(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumber)this.get_store().insert_element_user(CTSharedItemsImpl.N$2, n);
        }
    }
    
    public CTNumber addNewN() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTNumber)this.get_store().add_element_user(CTSharedItemsImpl.N$2);
        }
    }
    
    public void removeN(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSharedItemsImpl.N$2, n);
        }
    }
    
    public List<CTBoolean> getBList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class BList extends AbstractList<CTBoolean>
            {
                @Override
                public CTBoolean get(final int n) {
                    return CTSharedItemsImpl.this.getBArray(n);
                }
                
                @Override
                public CTBoolean set(final int n, final CTBoolean ctBoolean) {
                    final CTBoolean bArray = CTSharedItemsImpl.this.getBArray(n);
                    CTSharedItemsImpl.this.setBArray(n, ctBoolean);
                    return bArray;
                }
                
                @Override
                public void add(final int n, final CTBoolean ctBoolean) {
                    CTSharedItemsImpl.this.insertNewB(n).set((XmlObject)ctBoolean);
                }
                
                @Override
                public CTBoolean remove(final int n) {
                    final CTBoolean bArray = CTSharedItemsImpl.this.getBArray(n);
                    CTSharedItemsImpl.this.removeB(n);
                    return bArray;
                }
                
                @Override
                public int size() {
                    return CTSharedItemsImpl.this.sizeOfBArray();
                }
            }
            return new BList();
        }
    }
    
    @Deprecated
    public CTBoolean[] getBArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSharedItemsImpl.B$4, (List)list);
            final CTBoolean[] array = new CTBoolean[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTBoolean getBArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTBoolean ctBoolean = (CTBoolean)this.get_store().find_element_user(CTSharedItemsImpl.B$4, n);
            if (ctBoolean == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctBoolean;
        }
    }
    
    public int sizeOfBArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSharedItemsImpl.B$4);
        }
    }
    
    public void setBArray(final CTBoolean[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSharedItemsImpl.B$4);
    }
    
    public void setBArray(final int n, final CTBoolean ctBoolean) {
        this.generatedSetterHelperImpl((XmlObject)ctBoolean, CTSharedItemsImpl.B$4, n, (short)2);
    }
    
    public CTBoolean insertNewB(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().insert_element_user(CTSharedItemsImpl.B$4, n);
        }
    }
    
    public CTBoolean addNewB() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTBoolean)this.get_store().add_element_user(CTSharedItemsImpl.B$4);
        }
    }
    
    public void removeB(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSharedItemsImpl.B$4, n);
        }
    }
    
    public List<CTError> getEList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EList extends AbstractList<CTError>
            {
                @Override
                public CTError get(final int n) {
                    return CTSharedItemsImpl.this.getEArray(n);
                }
                
                @Override
                public CTError set(final int n, final CTError ctError) {
                    final CTError eArray = CTSharedItemsImpl.this.getEArray(n);
                    CTSharedItemsImpl.this.setEArray(n, ctError);
                    return eArray;
                }
                
                @Override
                public void add(final int n, final CTError ctError) {
                    CTSharedItemsImpl.this.insertNewE(n).set((XmlObject)ctError);
                }
                
                @Override
                public CTError remove(final int n) {
                    final CTError eArray = CTSharedItemsImpl.this.getEArray(n);
                    CTSharedItemsImpl.this.removeE(n);
                    return eArray;
                }
                
                @Override
                public int size() {
                    return CTSharedItemsImpl.this.sizeOfEArray();
                }
            }
            return new EList();
        }
    }
    
    @Deprecated
    public CTError[] getEArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSharedItemsImpl.E$6, (List)list);
            final CTError[] array = new CTError[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTError getEArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTError ctError = (CTError)this.get_store().find_element_user(CTSharedItemsImpl.E$6, n);
            if (ctError == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctError;
        }
    }
    
    public int sizeOfEArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSharedItemsImpl.E$6);
        }
    }
    
    public void setEArray(final CTError[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSharedItemsImpl.E$6);
    }
    
    public void setEArray(final int n, final CTError ctError) {
        this.generatedSetterHelperImpl((XmlObject)ctError, CTSharedItemsImpl.E$6, n, (short)2);
    }
    
    public CTError insertNewE(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTError)this.get_store().insert_element_user(CTSharedItemsImpl.E$6, n);
        }
    }
    
    public CTError addNewE() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTError)this.get_store().add_element_user(CTSharedItemsImpl.E$6);
        }
    }
    
    public void removeE(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSharedItemsImpl.E$6, n);
        }
    }
    
    public List<CTString> getSList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SList extends AbstractList<CTString>
            {
                @Override
                public CTString get(final int n) {
                    return CTSharedItemsImpl.this.getSArray(n);
                }
                
                @Override
                public CTString set(final int n, final CTString ctString) {
                    final CTString sArray = CTSharedItemsImpl.this.getSArray(n);
                    CTSharedItemsImpl.this.setSArray(n, ctString);
                    return sArray;
                }
                
                @Override
                public void add(final int n, final CTString ctString) {
                    CTSharedItemsImpl.this.insertNewS(n).set((XmlObject)ctString);
                }
                
                @Override
                public CTString remove(final int n) {
                    final CTString sArray = CTSharedItemsImpl.this.getSArray(n);
                    CTSharedItemsImpl.this.removeS(n);
                    return sArray;
                }
                
                @Override
                public int size() {
                    return CTSharedItemsImpl.this.sizeOfSArray();
                }
            }
            return new SList();
        }
    }
    
    @Deprecated
    public CTString[] getSArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSharedItemsImpl.S$8, (List)list);
            final CTString[] array = new CTString[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTString getSArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTString ctString = (CTString)this.get_store().find_element_user(CTSharedItemsImpl.S$8, n);
            if (ctString == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctString;
        }
    }
    
    public int sizeOfSArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSharedItemsImpl.S$8);
        }
    }
    
    public void setSArray(final CTString[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSharedItemsImpl.S$8);
    }
    
    public void setSArray(final int n, final CTString ctString) {
        this.generatedSetterHelperImpl((XmlObject)ctString, CTSharedItemsImpl.S$8, n, (short)2);
    }
    
    public CTString insertNewS(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().insert_element_user(CTSharedItemsImpl.S$8, n);
        }
    }
    
    public CTString addNewS() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTString)this.get_store().add_element_user(CTSharedItemsImpl.S$8);
        }
    }
    
    public void removeS(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSharedItemsImpl.S$8, n);
        }
    }
    
    public List<CTDateTime> getDList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class DList extends AbstractList<CTDateTime>
            {
                @Override
                public CTDateTime get(final int n) {
                    return CTSharedItemsImpl.this.getDArray(n);
                }
                
                @Override
                public CTDateTime set(final int n, final CTDateTime ctDateTime) {
                    final CTDateTime dArray = CTSharedItemsImpl.this.getDArray(n);
                    CTSharedItemsImpl.this.setDArray(n, ctDateTime);
                    return dArray;
                }
                
                @Override
                public void add(final int n, final CTDateTime ctDateTime) {
                    CTSharedItemsImpl.this.insertNewD(n).set((XmlObject)ctDateTime);
                }
                
                @Override
                public CTDateTime remove(final int n) {
                    final CTDateTime dArray = CTSharedItemsImpl.this.getDArray(n);
                    CTSharedItemsImpl.this.removeD(n);
                    return dArray;
                }
                
                @Override
                public int size() {
                    return CTSharedItemsImpl.this.sizeOfDArray();
                }
            }
            return new DList();
        }
    }
    
    @Deprecated
    public CTDateTime[] getDArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(CTSharedItemsImpl.D$10, (List)list);
            final CTDateTime[] array = new CTDateTime[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CTDateTime getDArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CTDateTime ctDateTime = (CTDateTime)this.get_store().find_element_user(CTSharedItemsImpl.D$10, n);
            if (ctDateTime == null) {
                throw new IndexOutOfBoundsException();
            }
            return ctDateTime;
        }
    }
    
    public int sizeOfDArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(CTSharedItemsImpl.D$10);
        }
    }
    
    public void setDArray(final CTDateTime[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, CTSharedItemsImpl.D$10);
    }
    
    public void setDArray(final int n, final CTDateTime ctDateTime) {
        this.generatedSetterHelperImpl((XmlObject)ctDateTime, CTSharedItemsImpl.D$10, n, (short)2);
    }
    
    public CTDateTime insertNewD(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDateTime)this.get_store().insert_element_user(CTSharedItemsImpl.D$10, n);
        }
    }
    
    public CTDateTime addNewD() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CTDateTime)this.get_store().add_element_user(CTSharedItemsImpl.D$10);
        }
    }
    
    public void removeD(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(CTSharedItemsImpl.D$10, n);
        }
    }
    
    public boolean getContainsSemiMixedTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetContainsSemiMixedTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetContainsSemiMixedTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12) != null;
        }
    }
    
    public void setContainsSemiMixedTypes(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetContainsSemiMixedTypes(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetContainsSemiMixedTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.CONTAINSSEMIMIXEDTYPES$12);
        }
    }
    
    public boolean getContainsNonDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNONDATE$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSNONDATE$14);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetContainsNonDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNONDATE$14);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSNONDATE$14);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetContainsNonDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNONDATE$14) != null;
        }
    }
    
    public void setContainsNonDate(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNONDATE$14);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSNONDATE$14);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetContainsNonDate(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNONDATE$14);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSNONDATE$14);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetContainsNonDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.CONTAINSNONDATE$14);
        }
    }
    
    public boolean getContainsDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSDATE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSDATE$16);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetContainsDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSDATE$16);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSDATE$16);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetContainsDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSDATE$16) != null;
        }
    }
    
    public void setContainsDate(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSDATE$16);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSDATE$16);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetContainsDate(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSDATE$16);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSDATE$16);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetContainsDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.CONTAINSDATE$16);
        }
    }
    
    public boolean getContainsString() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSTRING$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSSTRING$18);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetContainsString() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSTRING$18);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSSTRING$18);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetContainsString() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSTRING$18) != null;
        }
    }
    
    public void setContainsString(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSTRING$18);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSSTRING$18);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetContainsString(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSSTRING$18);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSSTRING$18);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetContainsString() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.CONTAINSSTRING$18);
        }
    }
    
    public boolean getContainsBlank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSBLANK$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSBLANK$20);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetContainsBlank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSBLANK$20);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSBLANK$20);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetContainsBlank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSBLANK$20) != null;
        }
    }
    
    public void setContainsBlank(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSBLANK$20);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSBLANK$20);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetContainsBlank(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSBLANK$20);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSBLANK$20);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetContainsBlank() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.CONTAINSBLANK$20);
        }
    }
    
    public boolean getContainsMixedTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetContainsMixedTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetContainsMixedTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22) != null;
        }
    }
    
    public void setContainsMixedTypes(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetContainsMixedTypes(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetContainsMixedTypes() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.CONTAINSMIXEDTYPES$22);
        }
    }
    
    public boolean getContainsNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNUMBER$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSNUMBER$24);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetContainsNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNUMBER$24);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSNUMBER$24);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetContainsNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNUMBER$24) != null;
        }
    }
    
    public void setContainsNumber(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNUMBER$24);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSNUMBER$24);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetContainsNumber(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSNUMBER$24);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSNUMBER$24);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetContainsNumber() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.CONTAINSNUMBER$24);
        }
    }
    
    public boolean getContainsInteger() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSINTEGER$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSINTEGER$26);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetContainsInteger() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSINTEGER$26);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSharedItemsImpl.CONTAINSINTEGER$26);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetContainsInteger() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSINTEGER$26) != null;
        }
    }
    
    public void setContainsInteger(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSINTEGER$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSINTEGER$26);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetContainsInteger(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.CONTAINSINTEGER$26);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSharedItemsImpl.CONTAINSINTEGER$26);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetContainsInteger() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.CONTAINSINTEGER$26);
        }
    }
    
    public double getMinValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.MINVALUE$28);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetMinValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTSharedItemsImpl.MINVALUE$28);
        }
    }
    
    public boolean isSetMinValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.MINVALUE$28) != null;
        }
    }
    
    public void setMinValue(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.MINVALUE$28);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.MINVALUE$28);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetMinValue(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTSharedItemsImpl.MINVALUE$28);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTSharedItemsImpl.MINVALUE$28);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetMinValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.MINVALUE$28);
        }
    }
    
    public double getMaxValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.MAXVALUE$30);
            if (simpleValue == null) {
                return 0.0;
            }
            return simpleValue.getDoubleValue();
        }
    }
    
    public XmlDouble xgetMaxValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDouble)this.get_store().find_attribute_user(CTSharedItemsImpl.MAXVALUE$30);
        }
    }
    
    public boolean isSetMaxValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.MAXVALUE$30) != null;
        }
    }
    
    public void setMaxValue(final double doubleValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.MAXVALUE$30);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.MAXVALUE$30);
            }
            simpleValue.setDoubleValue(doubleValue);
        }
    }
    
    public void xsetMaxValue(final XmlDouble xmlDouble) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDouble xmlDouble2 = (XmlDouble)this.get_store().find_attribute_user(CTSharedItemsImpl.MAXVALUE$30);
            if (xmlDouble2 == null) {
                xmlDouble2 = (XmlDouble)this.get_store().add_attribute_user(CTSharedItemsImpl.MAXVALUE$30);
            }
            xmlDouble2.set((XmlObject)xmlDouble);
        }
    }
    
    public void unsetMaxValue() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.MAXVALUE$30);
        }
    }
    
    public Calendar getMinDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.MINDATE$32);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public XmlDateTime xgetMinDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().find_attribute_user(CTSharedItemsImpl.MINDATE$32);
        }
    }
    
    public boolean isSetMinDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.MINDATE$32) != null;
        }
    }
    
    public void setMinDate(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.MINDATE$32);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.MINDATE$32);
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetMinDate(final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_attribute_user(CTSharedItemsImpl.MINDATE$32);
            if (xmlDateTime2 == null) {
                xmlDateTime2 = (XmlDateTime)this.get_store().add_attribute_user(CTSharedItemsImpl.MINDATE$32);
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public void unsetMinDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.MINDATE$32);
        }
    }
    
    public Calendar getMaxDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.MAXDATE$34);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getCalendarValue();
        }
    }
    
    public XmlDateTime xgetMaxDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlDateTime)this.get_store().find_attribute_user(CTSharedItemsImpl.MAXDATE$34);
        }
    }
    
    public boolean isSetMaxDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.MAXDATE$34) != null;
        }
    }
    
    public void setMaxDate(final Calendar calendarValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.MAXDATE$34);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.MAXDATE$34);
            }
            simpleValue.setCalendarValue(calendarValue);
        }
    }
    
    public void xsetMaxDate(final XmlDateTime xmlDateTime) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlDateTime xmlDateTime2 = (XmlDateTime)this.get_store().find_attribute_user(CTSharedItemsImpl.MAXDATE$34);
            if (xmlDateTime2 == null) {
                xmlDateTime2 = (XmlDateTime)this.get_store().add_attribute_user(CTSharedItemsImpl.MAXDATE$34);
            }
            xmlDateTime2.set((XmlObject)xmlDateTime);
        }
    }
    
    public void unsetMaxDate() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.MAXDATE$34);
        }
    }
    
    public long getCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.COUNT$36);
            if (simpleValue == null) {
                return 0L;
            }
            return simpleValue.getLongValue();
        }
    }
    
    public XmlUnsignedInt xgetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlUnsignedInt)this.get_store().find_attribute_user(CTSharedItemsImpl.COUNT$36);
        }
    }
    
    public boolean isSetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.COUNT$36) != null;
        }
    }
    
    public void setCount(final long longValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.COUNT$36);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.COUNT$36);
            }
            simpleValue.setLongValue(longValue);
        }
    }
    
    public void xsetCount(final XmlUnsignedInt xmlUnsignedInt) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlUnsignedInt xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().find_attribute_user(CTSharedItemsImpl.COUNT$36);
            if (xmlUnsignedInt2 == null) {
                xmlUnsignedInt2 = (XmlUnsignedInt)this.get_store().add_attribute_user(CTSharedItemsImpl.COUNT$36);
            }
            xmlUnsignedInt2.set((XmlObject)xmlUnsignedInt);
        }
    }
    
    public void unsetCount() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.COUNT$36);
        }
    }
    
    public boolean getLongText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.LONGTEXT$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_default_attribute_value(CTSharedItemsImpl.LONGTEXT$38);
            }
            return simpleValue != null && simpleValue.getBooleanValue();
        }
    }
    
    public XmlBoolean xgetLongText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.LONGTEXT$38);
            if (xmlBoolean == null) {
                xmlBoolean = (XmlBoolean)this.get_default_attribute_value(CTSharedItemsImpl.LONGTEXT$38);
            }
            return xmlBoolean;
        }
    }
    
    public boolean isSetLongText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(CTSharedItemsImpl.LONGTEXT$38) != null;
        }
    }
    
    public void setLongText(final boolean booleanValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(CTSharedItemsImpl.LONGTEXT$38);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(CTSharedItemsImpl.LONGTEXT$38);
            }
            simpleValue.setBooleanValue(booleanValue);
        }
    }
    
    public void xsetLongText(final XmlBoolean xmlBoolean) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlBoolean xmlBoolean2 = (XmlBoolean)this.get_store().find_attribute_user(CTSharedItemsImpl.LONGTEXT$38);
            if (xmlBoolean2 == null) {
                xmlBoolean2 = (XmlBoolean)this.get_store().add_attribute_user(CTSharedItemsImpl.LONGTEXT$38);
            }
            xmlBoolean2.set((XmlObject)xmlBoolean);
        }
    }
    
    public void unsetLongText() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(CTSharedItemsImpl.LONGTEXT$38);
        }
    }
    
    static {
        M$0 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "m");
        N$2 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "n");
        B$4 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "b");
        E$6 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "e");
        S$8 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "s");
        D$10 = new QName("http://schemas.openxmlformats.org/spreadsheetml/2006/main", "d");
        CONTAINSSEMIMIXEDTYPES$12 = new QName("", "containsSemiMixedTypes");
        CONTAINSNONDATE$14 = new QName("", "containsNonDate");
        CONTAINSDATE$16 = new QName("", "containsDate");
        CONTAINSSTRING$18 = new QName("", "containsString");
        CONTAINSBLANK$20 = new QName("", "containsBlank");
        CONTAINSMIXEDTYPES$22 = new QName("", "containsMixedTypes");
        CONTAINSNUMBER$24 = new QName("", "containsNumber");
        CONTAINSINTEGER$26 = new QName("", "containsInteger");
        MINVALUE$28 = new QName("", "minValue");
        MAXVALUE$30 = new QName("", "maxValue");
        MINDATE$32 = new QName("", "minDate");
        MAXDATE$34 = new QName("", "maxDate");
        COUNT$36 = new QName("", "count");
        LONGTEXT$38 = new QName("", "longText");
    }
}
