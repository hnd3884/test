package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.etsi.uri.x01903.v13.AnyType;
import org.etsi.uri.x01903.v13.EncapsulatedPKIDataType;
import org.w3.x2000.x09.xmldsig.CanonicalizationMethodType;
import org.etsi.uri.x01903.v13.ReferenceInfoType;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.etsi.uri.x01903.v13.IncludeType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.GenericTimeStampType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class GenericTimeStampTypeImpl extends XmlComplexContentImpl implements GenericTimeStampType
{
    private static final long serialVersionUID = 1L;
    private static final QName INCLUDE$0;
    private static final QName REFERENCEINFO$2;
    private static final QName CANONICALIZATIONMETHOD$4;
    private static final QName ENCAPSULATEDTIMESTAMP$6;
    private static final QName XMLTIMESTAMP$8;
    private static final QName ID$10;
    
    public GenericTimeStampTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<IncludeType> getIncludeList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class IncludeList extends AbstractList<IncludeType>
            {
                @Override
                public IncludeType get(final int n) {
                    return GenericTimeStampTypeImpl.this.getIncludeArray(n);
                }
                
                @Override
                public IncludeType set(final int n, final IncludeType includeType) {
                    final IncludeType includeArray = GenericTimeStampTypeImpl.this.getIncludeArray(n);
                    GenericTimeStampTypeImpl.this.setIncludeArray(n, includeType);
                    return includeArray;
                }
                
                @Override
                public void add(final int n, final IncludeType includeType) {
                    GenericTimeStampTypeImpl.this.insertNewInclude(n).set((XmlObject)includeType);
                }
                
                @Override
                public IncludeType remove(final int n) {
                    final IncludeType includeArray = GenericTimeStampTypeImpl.this.getIncludeArray(n);
                    GenericTimeStampTypeImpl.this.removeInclude(n);
                    return includeArray;
                }
                
                @Override
                public int size() {
                    return GenericTimeStampTypeImpl.this.sizeOfIncludeArray();
                }
            }
            return new IncludeList();
        }
    }
    
    @Deprecated
    public IncludeType[] getIncludeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(GenericTimeStampTypeImpl.INCLUDE$0, (List)list);
            final IncludeType[] array = new IncludeType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public IncludeType getIncludeArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final IncludeType includeType = (IncludeType)this.get_store().find_element_user(GenericTimeStampTypeImpl.INCLUDE$0, n);
            if (includeType == null) {
                throw new IndexOutOfBoundsException();
            }
            return includeType;
        }
    }
    
    public int sizeOfIncludeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GenericTimeStampTypeImpl.INCLUDE$0);
        }
    }
    
    public void setIncludeArray(final IncludeType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, GenericTimeStampTypeImpl.INCLUDE$0);
    }
    
    public void setIncludeArray(final int n, final IncludeType includeType) {
        this.generatedSetterHelperImpl((XmlObject)includeType, GenericTimeStampTypeImpl.INCLUDE$0, n, (short)2);
    }
    
    public IncludeType insertNewInclude(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (IncludeType)this.get_store().insert_element_user(GenericTimeStampTypeImpl.INCLUDE$0, n);
        }
    }
    
    public IncludeType addNewInclude() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (IncludeType)this.get_store().add_element_user(GenericTimeStampTypeImpl.INCLUDE$0);
        }
    }
    
    public void removeInclude(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GenericTimeStampTypeImpl.INCLUDE$0, n);
        }
    }
    
    public List<ReferenceInfoType> getReferenceInfoList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ReferenceInfoList extends AbstractList<ReferenceInfoType>
            {
                @Override
                public ReferenceInfoType get(final int n) {
                    return GenericTimeStampTypeImpl.this.getReferenceInfoArray(n);
                }
                
                @Override
                public ReferenceInfoType set(final int n, final ReferenceInfoType referenceInfoType) {
                    final ReferenceInfoType referenceInfoArray = GenericTimeStampTypeImpl.this.getReferenceInfoArray(n);
                    GenericTimeStampTypeImpl.this.setReferenceInfoArray(n, referenceInfoType);
                    return referenceInfoArray;
                }
                
                @Override
                public void add(final int n, final ReferenceInfoType referenceInfoType) {
                    GenericTimeStampTypeImpl.this.insertNewReferenceInfo(n).set((XmlObject)referenceInfoType);
                }
                
                @Override
                public ReferenceInfoType remove(final int n) {
                    final ReferenceInfoType referenceInfoArray = GenericTimeStampTypeImpl.this.getReferenceInfoArray(n);
                    GenericTimeStampTypeImpl.this.removeReferenceInfo(n);
                    return referenceInfoArray;
                }
                
                @Override
                public int size() {
                    return GenericTimeStampTypeImpl.this.sizeOfReferenceInfoArray();
                }
            }
            return new ReferenceInfoList();
        }
    }
    
    @Deprecated
    public ReferenceInfoType[] getReferenceInfoArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(GenericTimeStampTypeImpl.REFERENCEINFO$2, (List)list);
            final ReferenceInfoType[] array = new ReferenceInfoType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public ReferenceInfoType getReferenceInfoArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ReferenceInfoType referenceInfoType = (ReferenceInfoType)this.get_store().find_element_user(GenericTimeStampTypeImpl.REFERENCEINFO$2, n);
            if (referenceInfoType == null) {
                throw new IndexOutOfBoundsException();
            }
            return referenceInfoType;
        }
    }
    
    public int sizeOfReferenceInfoArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GenericTimeStampTypeImpl.REFERENCEINFO$2);
        }
    }
    
    public void setReferenceInfoArray(final ReferenceInfoType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, GenericTimeStampTypeImpl.REFERENCEINFO$2);
    }
    
    public void setReferenceInfoArray(final int n, final ReferenceInfoType referenceInfoType) {
        this.generatedSetterHelperImpl((XmlObject)referenceInfoType, GenericTimeStampTypeImpl.REFERENCEINFO$2, n, (short)2);
    }
    
    public ReferenceInfoType insertNewReferenceInfo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ReferenceInfoType)this.get_store().insert_element_user(GenericTimeStampTypeImpl.REFERENCEINFO$2, n);
        }
    }
    
    public ReferenceInfoType addNewReferenceInfo() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (ReferenceInfoType)this.get_store().add_element_user(GenericTimeStampTypeImpl.REFERENCEINFO$2);
        }
    }
    
    public void removeReferenceInfo(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GenericTimeStampTypeImpl.REFERENCEINFO$2, n);
        }
    }
    
    public CanonicalizationMethodType getCanonicalizationMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CanonicalizationMethodType canonicalizationMethodType = (CanonicalizationMethodType)this.get_store().find_element_user(GenericTimeStampTypeImpl.CANONICALIZATIONMETHOD$4, 0);
            if (canonicalizationMethodType == null) {
                return null;
            }
            return canonicalizationMethodType;
        }
    }
    
    public boolean isSetCanonicalizationMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GenericTimeStampTypeImpl.CANONICALIZATIONMETHOD$4) != 0;
        }
    }
    
    public void setCanonicalizationMethod(final CanonicalizationMethodType canonicalizationMethodType) {
        this.generatedSetterHelperImpl((XmlObject)canonicalizationMethodType, GenericTimeStampTypeImpl.CANONICALIZATIONMETHOD$4, 0, (short)1);
    }
    
    public CanonicalizationMethodType addNewCanonicalizationMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CanonicalizationMethodType)this.get_store().add_element_user(GenericTimeStampTypeImpl.CANONICALIZATIONMETHOD$4);
        }
    }
    
    public void unsetCanonicalizationMethod() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GenericTimeStampTypeImpl.CANONICALIZATIONMETHOD$4, 0);
        }
    }
    
    public List<EncapsulatedPKIDataType> getEncapsulatedTimeStampList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class EncapsulatedTimeStampList extends AbstractList<EncapsulatedPKIDataType>
            {
                @Override
                public EncapsulatedPKIDataType get(final int n) {
                    return GenericTimeStampTypeImpl.this.getEncapsulatedTimeStampArray(n);
                }
                
                @Override
                public EncapsulatedPKIDataType set(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
                    final EncapsulatedPKIDataType encapsulatedTimeStampArray = GenericTimeStampTypeImpl.this.getEncapsulatedTimeStampArray(n);
                    GenericTimeStampTypeImpl.this.setEncapsulatedTimeStampArray(n, encapsulatedPKIDataType);
                    return encapsulatedTimeStampArray;
                }
                
                @Override
                public void add(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
                    GenericTimeStampTypeImpl.this.insertNewEncapsulatedTimeStamp(n).set((XmlObject)encapsulatedPKIDataType);
                }
                
                @Override
                public EncapsulatedPKIDataType remove(final int n) {
                    final EncapsulatedPKIDataType encapsulatedTimeStampArray = GenericTimeStampTypeImpl.this.getEncapsulatedTimeStampArray(n);
                    GenericTimeStampTypeImpl.this.removeEncapsulatedTimeStamp(n);
                    return encapsulatedTimeStampArray;
                }
                
                @Override
                public int size() {
                    return GenericTimeStampTypeImpl.this.sizeOfEncapsulatedTimeStampArray();
                }
            }
            return new EncapsulatedTimeStampList();
        }
    }
    
    @Deprecated
    public EncapsulatedPKIDataType[] getEncapsulatedTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(GenericTimeStampTypeImpl.ENCAPSULATEDTIMESTAMP$6, (List)list);
            final EncapsulatedPKIDataType[] array = new EncapsulatedPKIDataType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public EncapsulatedPKIDataType getEncapsulatedTimeStampArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final EncapsulatedPKIDataType encapsulatedPKIDataType = (EncapsulatedPKIDataType)this.get_store().find_element_user(GenericTimeStampTypeImpl.ENCAPSULATEDTIMESTAMP$6, n);
            if (encapsulatedPKIDataType == null) {
                throw new IndexOutOfBoundsException();
            }
            return encapsulatedPKIDataType;
        }
    }
    
    public int sizeOfEncapsulatedTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GenericTimeStampTypeImpl.ENCAPSULATEDTIMESTAMP$6);
        }
    }
    
    public void setEncapsulatedTimeStampArray(final EncapsulatedPKIDataType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, GenericTimeStampTypeImpl.ENCAPSULATEDTIMESTAMP$6);
    }
    
    public void setEncapsulatedTimeStampArray(final int n, final EncapsulatedPKIDataType encapsulatedPKIDataType) {
        this.generatedSetterHelperImpl((XmlObject)encapsulatedPKIDataType, GenericTimeStampTypeImpl.ENCAPSULATEDTIMESTAMP$6, n, (short)2);
    }
    
    public EncapsulatedPKIDataType insertNewEncapsulatedTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (EncapsulatedPKIDataType)this.get_store().insert_element_user(GenericTimeStampTypeImpl.ENCAPSULATEDTIMESTAMP$6, n);
        }
    }
    
    public EncapsulatedPKIDataType addNewEncapsulatedTimeStamp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (EncapsulatedPKIDataType)this.get_store().add_element_user(GenericTimeStampTypeImpl.ENCAPSULATEDTIMESTAMP$6);
        }
    }
    
    public void removeEncapsulatedTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GenericTimeStampTypeImpl.ENCAPSULATEDTIMESTAMP$6, n);
        }
    }
    
    public List<AnyType> getXMLTimeStampList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class XMLTimeStampList extends AbstractList<AnyType>
            {
                @Override
                public AnyType get(final int n) {
                    return GenericTimeStampTypeImpl.this.getXMLTimeStampArray(n);
                }
                
                @Override
                public AnyType set(final int n, final AnyType anyType) {
                    final AnyType xmlTimeStampArray = GenericTimeStampTypeImpl.this.getXMLTimeStampArray(n);
                    GenericTimeStampTypeImpl.this.setXMLTimeStampArray(n, anyType);
                    return xmlTimeStampArray;
                }
                
                @Override
                public void add(final int n, final AnyType anyType) {
                    GenericTimeStampTypeImpl.this.insertNewXMLTimeStamp(n).set((XmlObject)anyType);
                }
                
                @Override
                public AnyType remove(final int n) {
                    final AnyType xmlTimeStampArray = GenericTimeStampTypeImpl.this.getXMLTimeStampArray(n);
                    GenericTimeStampTypeImpl.this.removeXMLTimeStamp(n);
                    return xmlTimeStampArray;
                }
                
                @Override
                public int size() {
                    return GenericTimeStampTypeImpl.this.sizeOfXMLTimeStampArray();
                }
            }
            return new XMLTimeStampList();
        }
    }
    
    @Deprecated
    public AnyType[] getXMLTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(GenericTimeStampTypeImpl.XMLTIMESTAMP$8, (List)list);
            final AnyType[] array = new AnyType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public AnyType getXMLTimeStampArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final AnyType anyType = (AnyType)this.get_store().find_element_user(GenericTimeStampTypeImpl.XMLTIMESTAMP$8, n);
            if (anyType == null) {
                throw new IndexOutOfBoundsException();
            }
            return anyType;
        }
    }
    
    public int sizeOfXMLTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(GenericTimeStampTypeImpl.XMLTIMESTAMP$8);
        }
    }
    
    public void setXMLTimeStampArray(final AnyType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, GenericTimeStampTypeImpl.XMLTIMESTAMP$8);
    }
    
    public void setXMLTimeStampArray(final int n, final AnyType anyType) {
        this.generatedSetterHelperImpl((XmlObject)anyType, GenericTimeStampTypeImpl.XMLTIMESTAMP$8, n, (short)2);
    }
    
    public AnyType insertNewXMLTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (AnyType)this.get_store().insert_element_user(GenericTimeStampTypeImpl.XMLTIMESTAMP$8, n);
        }
    }
    
    public AnyType addNewXMLTimeStamp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (AnyType)this.get_store().add_element_user(GenericTimeStampTypeImpl.XMLTIMESTAMP$8);
        }
    }
    
    public void removeXMLTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(GenericTimeStampTypeImpl.XMLTIMESTAMP$8, n);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(GenericTimeStampTypeImpl.ID$10);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(GenericTimeStampTypeImpl.ID$10);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(GenericTimeStampTypeImpl.ID$10) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(GenericTimeStampTypeImpl.ID$10);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(GenericTimeStampTypeImpl.ID$10);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(GenericTimeStampTypeImpl.ID$10);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(GenericTimeStampTypeImpl.ID$10);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(GenericTimeStampTypeImpl.ID$10);
        }
    }
    
    static {
        INCLUDE$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "Include");
        REFERENCEINFO$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "ReferenceInfo");
        CANONICALIZATIONMETHOD$4 = new QName("http://www.w3.org/2000/09/xmldsig#", "CanonicalizationMethod");
        ENCAPSULATEDTIMESTAMP$6 = new QName("http://uri.etsi.org/01903/v1.3.2#", "EncapsulatedTimeStamp");
        XMLTIMESTAMP$8 = new QName("http://uri.etsi.org/01903/v1.3.2#", "XMLTimeStamp");
        ID$10 = new QName("", "Id");
    }
}
