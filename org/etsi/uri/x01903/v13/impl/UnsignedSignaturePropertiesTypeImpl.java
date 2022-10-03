package org.etsi.uri.x01903.v13.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.SimpleValue;
import org.etsi.uri.x01903.v13.RevocationValuesType;
import org.etsi.uri.x01903.v13.CertificateValuesType;
import org.etsi.uri.x01903.v13.CompleteRevocationRefsType;
import org.etsi.uri.x01903.v13.CompleteCertificateRefsType;
import org.etsi.uri.x01903.v13.XAdESTimeStampType;
import java.util.ArrayList;
import org.apache.xmlbeans.XmlObject;
import java.util.AbstractList;
import org.etsi.uri.x01903.v13.CounterSignatureType;
import java.util.List;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.etsi.uri.x01903.v13.UnsignedSignaturePropertiesType;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class UnsignedSignaturePropertiesTypeImpl extends XmlComplexContentImpl implements UnsignedSignaturePropertiesType
{
    private static final long serialVersionUID = 1L;
    private static final QName COUNTERSIGNATURE$0;
    private static final QName SIGNATURETIMESTAMP$2;
    private static final QName COMPLETECERTIFICATEREFS$4;
    private static final QName COMPLETEREVOCATIONREFS$6;
    private static final QName ATTRIBUTECERTIFICATEREFS$8;
    private static final QName ATTRIBUTEREVOCATIONREFS$10;
    private static final QName SIGANDREFSTIMESTAMP$12;
    private static final QName REFSONLYTIMESTAMP$14;
    private static final QName CERTIFICATEVALUES$16;
    private static final QName REVOCATIONVALUES$18;
    private static final QName ATTRAUTHORITIESCERTVALUES$20;
    private static final QName ATTRIBUTEREVOCATIONVALUES$22;
    private static final QName ARCHIVETIMESTAMP$24;
    private static final QName ID$26;
    
    public UnsignedSignaturePropertiesTypeImpl(final SchemaType schemaType) {
        super(schemaType);
    }
    
    public List<CounterSignatureType> getCounterSignatureList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CounterSignatureList extends AbstractList<CounterSignatureType>
            {
                @Override
                public CounterSignatureType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getCounterSignatureArray(n);
                }
                
                @Override
                public CounterSignatureType set(final int n, final CounterSignatureType counterSignatureType) {
                    final CounterSignatureType counterSignatureArray = UnsignedSignaturePropertiesTypeImpl.this.getCounterSignatureArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setCounterSignatureArray(n, counterSignatureType);
                    return counterSignatureArray;
                }
                
                @Override
                public void add(final int n, final CounterSignatureType counterSignatureType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewCounterSignature(n).set((XmlObject)counterSignatureType);
                }
                
                @Override
                public CounterSignatureType remove(final int n) {
                    final CounterSignatureType counterSignatureArray = UnsignedSignaturePropertiesTypeImpl.this.getCounterSignatureArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeCounterSignature(n);
                    return counterSignatureArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfCounterSignatureArray();
                }
            }
            return new CounterSignatureList();
        }
    }
    
    @Deprecated
    public CounterSignatureType[] getCounterSignatureArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.COUNTERSIGNATURE$0, (List)list);
            final CounterSignatureType[] array = new CounterSignatureType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CounterSignatureType getCounterSignatureArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CounterSignatureType counterSignatureType = (CounterSignatureType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.COUNTERSIGNATURE$0, n);
            if (counterSignatureType == null) {
                throw new IndexOutOfBoundsException();
            }
            return counterSignatureType;
        }
    }
    
    public int sizeOfCounterSignatureArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.COUNTERSIGNATURE$0);
        }
    }
    
    public void setCounterSignatureArray(final CounterSignatureType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.COUNTERSIGNATURE$0);
    }
    
    public void setCounterSignatureArray(final int n, final CounterSignatureType counterSignatureType) {
        this.generatedSetterHelperImpl((XmlObject)counterSignatureType, UnsignedSignaturePropertiesTypeImpl.COUNTERSIGNATURE$0, n, (short)2);
    }
    
    public CounterSignatureType insertNewCounterSignature(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CounterSignatureType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.COUNTERSIGNATURE$0, n);
        }
    }
    
    public CounterSignatureType addNewCounterSignature() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CounterSignatureType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.COUNTERSIGNATURE$0);
        }
    }
    
    public void removeCounterSignature(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.COUNTERSIGNATURE$0, n);
        }
    }
    
    public List<XAdESTimeStampType> getSignatureTimeStampList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SignatureTimeStampList extends AbstractList<XAdESTimeStampType>
            {
                @Override
                public XAdESTimeStampType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getSignatureTimeStampArray(n);
                }
                
                @Override
                public XAdESTimeStampType set(final int n, final XAdESTimeStampType xAdESTimeStampType) {
                    final XAdESTimeStampType signatureTimeStampArray = UnsignedSignaturePropertiesTypeImpl.this.getSignatureTimeStampArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setSignatureTimeStampArray(n, xAdESTimeStampType);
                    return signatureTimeStampArray;
                }
                
                @Override
                public void add(final int n, final XAdESTimeStampType xAdESTimeStampType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewSignatureTimeStamp(n).set((XmlObject)xAdESTimeStampType);
                }
                
                @Override
                public XAdESTimeStampType remove(final int n) {
                    final XAdESTimeStampType signatureTimeStampArray = UnsignedSignaturePropertiesTypeImpl.this.getSignatureTimeStampArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeSignatureTimeStamp(n);
                    return signatureTimeStampArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfSignatureTimeStampArray();
                }
            }
            return new SignatureTimeStampList();
        }
    }
    
    @Deprecated
    public XAdESTimeStampType[] getSignatureTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.SIGNATURETIMESTAMP$2, (List)list);
            final XAdESTimeStampType[] array = new XAdESTimeStampType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XAdESTimeStampType getSignatureTimeStampArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XAdESTimeStampType xAdESTimeStampType = (XAdESTimeStampType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.SIGNATURETIMESTAMP$2, n);
            if (xAdESTimeStampType == null) {
                throw new IndexOutOfBoundsException();
            }
            return xAdESTimeStampType;
        }
    }
    
    public int sizeOfSignatureTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.SIGNATURETIMESTAMP$2);
        }
    }
    
    public void setSignatureTimeStampArray(final XAdESTimeStampType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.SIGNATURETIMESTAMP$2);
    }
    
    public void setSignatureTimeStampArray(final int n, final XAdESTimeStampType xAdESTimeStampType) {
        this.generatedSetterHelperImpl((XmlObject)xAdESTimeStampType, UnsignedSignaturePropertiesTypeImpl.SIGNATURETIMESTAMP$2, n, (short)2);
    }
    
    public XAdESTimeStampType insertNewSignatureTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XAdESTimeStampType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.SIGNATURETIMESTAMP$2, n);
        }
    }
    
    public XAdESTimeStampType addNewSignatureTimeStamp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XAdESTimeStampType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.SIGNATURETIMESTAMP$2);
        }
    }
    
    public void removeSignatureTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.SIGNATURETIMESTAMP$2, n);
        }
    }
    
    public List<CompleteCertificateRefsType> getCompleteCertificateRefsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CompleteCertificateRefsList extends AbstractList<CompleteCertificateRefsType>
            {
                @Override
                public CompleteCertificateRefsType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getCompleteCertificateRefsArray(n);
                }
                
                @Override
                public CompleteCertificateRefsType set(final int n, final CompleteCertificateRefsType completeCertificateRefsType) {
                    final CompleteCertificateRefsType completeCertificateRefsArray = UnsignedSignaturePropertiesTypeImpl.this.getCompleteCertificateRefsArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setCompleteCertificateRefsArray(n, completeCertificateRefsType);
                    return completeCertificateRefsArray;
                }
                
                @Override
                public void add(final int n, final CompleteCertificateRefsType completeCertificateRefsType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewCompleteCertificateRefs(n).set((XmlObject)completeCertificateRefsType);
                }
                
                @Override
                public CompleteCertificateRefsType remove(final int n) {
                    final CompleteCertificateRefsType completeCertificateRefsArray = UnsignedSignaturePropertiesTypeImpl.this.getCompleteCertificateRefsArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeCompleteCertificateRefs(n);
                    return completeCertificateRefsArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfCompleteCertificateRefsArray();
                }
            }
            return new CompleteCertificateRefsList();
        }
    }
    
    @Deprecated
    public CompleteCertificateRefsType[] getCompleteCertificateRefsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.COMPLETECERTIFICATEREFS$4, (List)list);
            final CompleteCertificateRefsType[] array = new CompleteCertificateRefsType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CompleteCertificateRefsType getCompleteCertificateRefsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CompleteCertificateRefsType completeCertificateRefsType = (CompleteCertificateRefsType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.COMPLETECERTIFICATEREFS$4, n);
            if (completeCertificateRefsType == null) {
                throw new IndexOutOfBoundsException();
            }
            return completeCertificateRefsType;
        }
    }
    
    public int sizeOfCompleteCertificateRefsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.COMPLETECERTIFICATEREFS$4);
        }
    }
    
    public void setCompleteCertificateRefsArray(final CompleteCertificateRefsType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.COMPLETECERTIFICATEREFS$4);
    }
    
    public void setCompleteCertificateRefsArray(final int n, final CompleteCertificateRefsType completeCertificateRefsType) {
        this.generatedSetterHelperImpl((XmlObject)completeCertificateRefsType, UnsignedSignaturePropertiesTypeImpl.COMPLETECERTIFICATEREFS$4, n, (short)2);
    }
    
    public CompleteCertificateRefsType insertNewCompleteCertificateRefs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CompleteCertificateRefsType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.COMPLETECERTIFICATEREFS$4, n);
        }
    }
    
    public CompleteCertificateRefsType addNewCompleteCertificateRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CompleteCertificateRefsType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.COMPLETECERTIFICATEREFS$4);
        }
    }
    
    public void removeCompleteCertificateRefs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.COMPLETECERTIFICATEREFS$4, n);
        }
    }
    
    public List<CompleteRevocationRefsType> getCompleteRevocationRefsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CompleteRevocationRefsList extends AbstractList<CompleteRevocationRefsType>
            {
                @Override
                public CompleteRevocationRefsType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getCompleteRevocationRefsArray(n);
                }
                
                @Override
                public CompleteRevocationRefsType set(final int n, final CompleteRevocationRefsType completeRevocationRefsType) {
                    final CompleteRevocationRefsType completeRevocationRefsArray = UnsignedSignaturePropertiesTypeImpl.this.getCompleteRevocationRefsArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setCompleteRevocationRefsArray(n, completeRevocationRefsType);
                    return completeRevocationRefsArray;
                }
                
                @Override
                public void add(final int n, final CompleteRevocationRefsType completeRevocationRefsType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewCompleteRevocationRefs(n).set((XmlObject)completeRevocationRefsType);
                }
                
                @Override
                public CompleteRevocationRefsType remove(final int n) {
                    final CompleteRevocationRefsType completeRevocationRefsArray = UnsignedSignaturePropertiesTypeImpl.this.getCompleteRevocationRefsArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeCompleteRevocationRefs(n);
                    return completeRevocationRefsArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfCompleteRevocationRefsArray();
                }
            }
            return new CompleteRevocationRefsList();
        }
    }
    
    @Deprecated
    public CompleteRevocationRefsType[] getCompleteRevocationRefsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.COMPLETEREVOCATIONREFS$6, (List)list);
            final CompleteRevocationRefsType[] array = new CompleteRevocationRefsType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CompleteRevocationRefsType getCompleteRevocationRefsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CompleteRevocationRefsType completeRevocationRefsType = (CompleteRevocationRefsType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.COMPLETEREVOCATIONREFS$6, n);
            if (completeRevocationRefsType == null) {
                throw new IndexOutOfBoundsException();
            }
            return completeRevocationRefsType;
        }
    }
    
    public int sizeOfCompleteRevocationRefsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.COMPLETEREVOCATIONREFS$6);
        }
    }
    
    public void setCompleteRevocationRefsArray(final CompleteRevocationRefsType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.COMPLETEREVOCATIONREFS$6);
    }
    
    public void setCompleteRevocationRefsArray(final int n, final CompleteRevocationRefsType completeRevocationRefsType) {
        this.generatedSetterHelperImpl((XmlObject)completeRevocationRefsType, UnsignedSignaturePropertiesTypeImpl.COMPLETEREVOCATIONREFS$6, n, (short)2);
    }
    
    public CompleteRevocationRefsType insertNewCompleteRevocationRefs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CompleteRevocationRefsType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.COMPLETEREVOCATIONREFS$6, n);
        }
    }
    
    public CompleteRevocationRefsType addNewCompleteRevocationRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CompleteRevocationRefsType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.COMPLETEREVOCATIONREFS$6);
        }
    }
    
    public void removeCompleteRevocationRefs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.COMPLETEREVOCATIONREFS$6, n);
        }
    }
    
    public List<CompleteCertificateRefsType> getAttributeCertificateRefsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AttributeCertificateRefsList extends AbstractList<CompleteCertificateRefsType>
            {
                @Override
                public CompleteCertificateRefsType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getAttributeCertificateRefsArray(n);
                }
                
                @Override
                public CompleteCertificateRefsType set(final int n, final CompleteCertificateRefsType completeCertificateRefsType) {
                    final CompleteCertificateRefsType attributeCertificateRefsArray = UnsignedSignaturePropertiesTypeImpl.this.getAttributeCertificateRefsArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setAttributeCertificateRefsArray(n, completeCertificateRefsType);
                    return attributeCertificateRefsArray;
                }
                
                @Override
                public void add(final int n, final CompleteCertificateRefsType completeCertificateRefsType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewAttributeCertificateRefs(n).set((XmlObject)completeCertificateRefsType);
                }
                
                @Override
                public CompleteCertificateRefsType remove(final int n) {
                    final CompleteCertificateRefsType attributeCertificateRefsArray = UnsignedSignaturePropertiesTypeImpl.this.getAttributeCertificateRefsArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeAttributeCertificateRefs(n);
                    return attributeCertificateRefsArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfAttributeCertificateRefsArray();
                }
            }
            return new AttributeCertificateRefsList();
        }
    }
    
    @Deprecated
    public CompleteCertificateRefsType[] getAttributeCertificateRefsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTECERTIFICATEREFS$8, (List)list);
            final CompleteCertificateRefsType[] array = new CompleteCertificateRefsType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CompleteCertificateRefsType getAttributeCertificateRefsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CompleteCertificateRefsType completeCertificateRefsType = (CompleteCertificateRefsType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTECERTIFICATEREFS$8, n);
            if (completeCertificateRefsType == null) {
                throw new IndexOutOfBoundsException();
            }
            return completeCertificateRefsType;
        }
    }
    
    public int sizeOfAttributeCertificateRefsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTECERTIFICATEREFS$8);
        }
    }
    
    public void setAttributeCertificateRefsArray(final CompleteCertificateRefsType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.ATTRIBUTECERTIFICATEREFS$8);
    }
    
    public void setAttributeCertificateRefsArray(final int n, final CompleteCertificateRefsType completeCertificateRefsType) {
        this.generatedSetterHelperImpl((XmlObject)completeCertificateRefsType, UnsignedSignaturePropertiesTypeImpl.ATTRIBUTECERTIFICATEREFS$8, n, (short)2);
    }
    
    public CompleteCertificateRefsType insertNewAttributeCertificateRefs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CompleteCertificateRefsType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTECERTIFICATEREFS$8, n);
        }
    }
    
    public CompleteCertificateRefsType addNewAttributeCertificateRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CompleteCertificateRefsType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTECERTIFICATEREFS$8);
        }
    }
    
    public void removeAttributeCertificateRefs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTECERTIFICATEREFS$8, n);
        }
    }
    
    public List<CompleteRevocationRefsType> getAttributeRevocationRefsList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AttributeRevocationRefsList extends AbstractList<CompleteRevocationRefsType>
            {
                @Override
                public CompleteRevocationRefsType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getAttributeRevocationRefsArray(n);
                }
                
                @Override
                public CompleteRevocationRefsType set(final int n, final CompleteRevocationRefsType completeRevocationRefsType) {
                    final CompleteRevocationRefsType attributeRevocationRefsArray = UnsignedSignaturePropertiesTypeImpl.this.getAttributeRevocationRefsArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setAttributeRevocationRefsArray(n, completeRevocationRefsType);
                    return attributeRevocationRefsArray;
                }
                
                @Override
                public void add(final int n, final CompleteRevocationRefsType completeRevocationRefsType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewAttributeRevocationRefs(n).set((XmlObject)completeRevocationRefsType);
                }
                
                @Override
                public CompleteRevocationRefsType remove(final int n) {
                    final CompleteRevocationRefsType attributeRevocationRefsArray = UnsignedSignaturePropertiesTypeImpl.this.getAttributeRevocationRefsArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeAttributeRevocationRefs(n);
                    return attributeRevocationRefsArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfAttributeRevocationRefsArray();
                }
            }
            return new AttributeRevocationRefsList();
        }
    }
    
    @Deprecated
    public CompleteRevocationRefsType[] getAttributeRevocationRefsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONREFS$10, (List)list);
            final CompleteRevocationRefsType[] array = new CompleteRevocationRefsType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CompleteRevocationRefsType getAttributeRevocationRefsArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CompleteRevocationRefsType completeRevocationRefsType = (CompleteRevocationRefsType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONREFS$10, n);
            if (completeRevocationRefsType == null) {
                throw new IndexOutOfBoundsException();
            }
            return completeRevocationRefsType;
        }
    }
    
    public int sizeOfAttributeRevocationRefsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONREFS$10);
        }
    }
    
    public void setAttributeRevocationRefsArray(final CompleteRevocationRefsType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONREFS$10);
    }
    
    public void setAttributeRevocationRefsArray(final int n, final CompleteRevocationRefsType completeRevocationRefsType) {
        this.generatedSetterHelperImpl((XmlObject)completeRevocationRefsType, UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONREFS$10, n, (short)2);
    }
    
    public CompleteRevocationRefsType insertNewAttributeRevocationRefs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CompleteRevocationRefsType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONREFS$10, n);
        }
    }
    
    public CompleteRevocationRefsType addNewAttributeRevocationRefs() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CompleteRevocationRefsType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONREFS$10);
        }
    }
    
    public void removeAttributeRevocationRefs(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONREFS$10, n);
        }
    }
    
    public List<XAdESTimeStampType> getSigAndRefsTimeStampList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class SigAndRefsTimeStampList extends AbstractList<XAdESTimeStampType>
            {
                @Override
                public XAdESTimeStampType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getSigAndRefsTimeStampArray(n);
                }
                
                @Override
                public XAdESTimeStampType set(final int n, final XAdESTimeStampType xAdESTimeStampType) {
                    final XAdESTimeStampType sigAndRefsTimeStampArray = UnsignedSignaturePropertiesTypeImpl.this.getSigAndRefsTimeStampArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setSigAndRefsTimeStampArray(n, xAdESTimeStampType);
                    return sigAndRefsTimeStampArray;
                }
                
                @Override
                public void add(final int n, final XAdESTimeStampType xAdESTimeStampType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewSigAndRefsTimeStamp(n).set((XmlObject)xAdESTimeStampType);
                }
                
                @Override
                public XAdESTimeStampType remove(final int n) {
                    final XAdESTimeStampType sigAndRefsTimeStampArray = UnsignedSignaturePropertiesTypeImpl.this.getSigAndRefsTimeStampArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeSigAndRefsTimeStamp(n);
                    return sigAndRefsTimeStampArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfSigAndRefsTimeStampArray();
                }
            }
            return new SigAndRefsTimeStampList();
        }
    }
    
    @Deprecated
    public XAdESTimeStampType[] getSigAndRefsTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.SIGANDREFSTIMESTAMP$12, (List)list);
            final XAdESTimeStampType[] array = new XAdESTimeStampType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XAdESTimeStampType getSigAndRefsTimeStampArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XAdESTimeStampType xAdESTimeStampType = (XAdESTimeStampType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.SIGANDREFSTIMESTAMP$12, n);
            if (xAdESTimeStampType == null) {
                throw new IndexOutOfBoundsException();
            }
            return xAdESTimeStampType;
        }
    }
    
    public int sizeOfSigAndRefsTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.SIGANDREFSTIMESTAMP$12);
        }
    }
    
    public void setSigAndRefsTimeStampArray(final XAdESTimeStampType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.SIGANDREFSTIMESTAMP$12);
    }
    
    public void setSigAndRefsTimeStampArray(final int n, final XAdESTimeStampType xAdESTimeStampType) {
        this.generatedSetterHelperImpl((XmlObject)xAdESTimeStampType, UnsignedSignaturePropertiesTypeImpl.SIGANDREFSTIMESTAMP$12, n, (short)2);
    }
    
    public XAdESTimeStampType insertNewSigAndRefsTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XAdESTimeStampType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.SIGANDREFSTIMESTAMP$12, n);
        }
    }
    
    public XAdESTimeStampType addNewSigAndRefsTimeStamp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XAdESTimeStampType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.SIGANDREFSTIMESTAMP$12);
        }
    }
    
    public void removeSigAndRefsTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.SIGANDREFSTIMESTAMP$12, n);
        }
    }
    
    public List<XAdESTimeStampType> getRefsOnlyTimeStampList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RefsOnlyTimeStampList extends AbstractList<XAdESTimeStampType>
            {
                @Override
                public XAdESTimeStampType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getRefsOnlyTimeStampArray(n);
                }
                
                @Override
                public XAdESTimeStampType set(final int n, final XAdESTimeStampType xAdESTimeStampType) {
                    final XAdESTimeStampType refsOnlyTimeStampArray = UnsignedSignaturePropertiesTypeImpl.this.getRefsOnlyTimeStampArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setRefsOnlyTimeStampArray(n, xAdESTimeStampType);
                    return refsOnlyTimeStampArray;
                }
                
                @Override
                public void add(final int n, final XAdESTimeStampType xAdESTimeStampType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewRefsOnlyTimeStamp(n).set((XmlObject)xAdESTimeStampType);
                }
                
                @Override
                public XAdESTimeStampType remove(final int n) {
                    final XAdESTimeStampType refsOnlyTimeStampArray = UnsignedSignaturePropertiesTypeImpl.this.getRefsOnlyTimeStampArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeRefsOnlyTimeStamp(n);
                    return refsOnlyTimeStampArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfRefsOnlyTimeStampArray();
                }
            }
            return new RefsOnlyTimeStampList();
        }
    }
    
    @Deprecated
    public XAdESTimeStampType[] getRefsOnlyTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.REFSONLYTIMESTAMP$14, (List)list);
            final XAdESTimeStampType[] array = new XAdESTimeStampType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XAdESTimeStampType getRefsOnlyTimeStampArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XAdESTimeStampType xAdESTimeStampType = (XAdESTimeStampType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.REFSONLYTIMESTAMP$14, n);
            if (xAdESTimeStampType == null) {
                throw new IndexOutOfBoundsException();
            }
            return xAdESTimeStampType;
        }
    }
    
    public int sizeOfRefsOnlyTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.REFSONLYTIMESTAMP$14);
        }
    }
    
    public void setRefsOnlyTimeStampArray(final XAdESTimeStampType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.REFSONLYTIMESTAMP$14);
    }
    
    public void setRefsOnlyTimeStampArray(final int n, final XAdESTimeStampType xAdESTimeStampType) {
        this.generatedSetterHelperImpl((XmlObject)xAdESTimeStampType, UnsignedSignaturePropertiesTypeImpl.REFSONLYTIMESTAMP$14, n, (short)2);
    }
    
    public XAdESTimeStampType insertNewRefsOnlyTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XAdESTimeStampType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.REFSONLYTIMESTAMP$14, n);
        }
    }
    
    public XAdESTimeStampType addNewRefsOnlyTimeStamp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XAdESTimeStampType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.REFSONLYTIMESTAMP$14);
        }
    }
    
    public void removeRefsOnlyTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.REFSONLYTIMESTAMP$14, n);
        }
    }
    
    public List<CertificateValuesType> getCertificateValuesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class CertificateValuesList extends AbstractList<CertificateValuesType>
            {
                @Override
                public CertificateValuesType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getCertificateValuesArray(n);
                }
                
                @Override
                public CertificateValuesType set(final int n, final CertificateValuesType certificateValuesType) {
                    final CertificateValuesType certificateValuesArray = UnsignedSignaturePropertiesTypeImpl.this.getCertificateValuesArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setCertificateValuesArray(n, certificateValuesType);
                    return certificateValuesArray;
                }
                
                @Override
                public void add(final int n, final CertificateValuesType certificateValuesType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewCertificateValues(n).set((XmlObject)certificateValuesType);
                }
                
                @Override
                public CertificateValuesType remove(final int n) {
                    final CertificateValuesType certificateValuesArray = UnsignedSignaturePropertiesTypeImpl.this.getCertificateValuesArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeCertificateValues(n);
                    return certificateValuesArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfCertificateValuesArray();
                }
            }
            return new CertificateValuesList();
        }
    }
    
    @Deprecated
    public CertificateValuesType[] getCertificateValuesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.CERTIFICATEVALUES$16, (List)list);
            final CertificateValuesType[] array = new CertificateValuesType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CertificateValuesType getCertificateValuesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CertificateValuesType certificateValuesType = (CertificateValuesType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.CERTIFICATEVALUES$16, n);
            if (certificateValuesType == null) {
                throw new IndexOutOfBoundsException();
            }
            return certificateValuesType;
        }
    }
    
    public int sizeOfCertificateValuesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.CERTIFICATEVALUES$16);
        }
    }
    
    public void setCertificateValuesArray(final CertificateValuesType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.CERTIFICATEVALUES$16);
    }
    
    public void setCertificateValuesArray(final int n, final CertificateValuesType certificateValuesType) {
        this.generatedSetterHelperImpl((XmlObject)certificateValuesType, UnsignedSignaturePropertiesTypeImpl.CERTIFICATEVALUES$16, n, (short)2);
    }
    
    public CertificateValuesType insertNewCertificateValues(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CertificateValuesType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.CERTIFICATEVALUES$16, n);
        }
    }
    
    public CertificateValuesType addNewCertificateValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CertificateValuesType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.CERTIFICATEVALUES$16);
        }
    }
    
    public void removeCertificateValues(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.CERTIFICATEVALUES$16, n);
        }
    }
    
    public List<RevocationValuesType> getRevocationValuesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class RevocationValuesList extends AbstractList<RevocationValuesType>
            {
                @Override
                public RevocationValuesType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getRevocationValuesArray(n);
                }
                
                @Override
                public RevocationValuesType set(final int n, final RevocationValuesType revocationValuesType) {
                    final RevocationValuesType revocationValuesArray = UnsignedSignaturePropertiesTypeImpl.this.getRevocationValuesArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setRevocationValuesArray(n, revocationValuesType);
                    return revocationValuesArray;
                }
                
                @Override
                public void add(final int n, final RevocationValuesType revocationValuesType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewRevocationValues(n).set((XmlObject)revocationValuesType);
                }
                
                @Override
                public RevocationValuesType remove(final int n) {
                    final RevocationValuesType revocationValuesArray = UnsignedSignaturePropertiesTypeImpl.this.getRevocationValuesArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeRevocationValues(n);
                    return revocationValuesArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfRevocationValuesArray();
                }
            }
            return new RevocationValuesList();
        }
    }
    
    @Deprecated
    public RevocationValuesType[] getRevocationValuesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.REVOCATIONVALUES$18, (List)list);
            final RevocationValuesType[] array = new RevocationValuesType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public RevocationValuesType getRevocationValuesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final RevocationValuesType revocationValuesType = (RevocationValuesType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.REVOCATIONVALUES$18, n);
            if (revocationValuesType == null) {
                throw new IndexOutOfBoundsException();
            }
            return revocationValuesType;
        }
    }
    
    public int sizeOfRevocationValuesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.REVOCATIONVALUES$18);
        }
    }
    
    public void setRevocationValuesArray(final RevocationValuesType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.REVOCATIONVALUES$18);
    }
    
    public void setRevocationValuesArray(final int n, final RevocationValuesType revocationValuesType) {
        this.generatedSetterHelperImpl((XmlObject)revocationValuesType, UnsignedSignaturePropertiesTypeImpl.REVOCATIONVALUES$18, n, (short)2);
    }
    
    public RevocationValuesType insertNewRevocationValues(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RevocationValuesType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.REVOCATIONVALUES$18, n);
        }
    }
    
    public RevocationValuesType addNewRevocationValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RevocationValuesType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.REVOCATIONVALUES$18);
        }
    }
    
    public void removeRevocationValues(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.REVOCATIONVALUES$18, n);
        }
    }
    
    public List<CertificateValuesType> getAttrAuthoritiesCertValuesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AttrAuthoritiesCertValuesList extends AbstractList<CertificateValuesType>
            {
                @Override
                public CertificateValuesType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getAttrAuthoritiesCertValuesArray(n);
                }
                
                @Override
                public CertificateValuesType set(final int n, final CertificateValuesType certificateValuesType) {
                    final CertificateValuesType attrAuthoritiesCertValuesArray = UnsignedSignaturePropertiesTypeImpl.this.getAttrAuthoritiesCertValuesArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setAttrAuthoritiesCertValuesArray(n, certificateValuesType);
                    return attrAuthoritiesCertValuesArray;
                }
                
                @Override
                public void add(final int n, final CertificateValuesType certificateValuesType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewAttrAuthoritiesCertValues(n).set((XmlObject)certificateValuesType);
                }
                
                @Override
                public CertificateValuesType remove(final int n) {
                    final CertificateValuesType attrAuthoritiesCertValuesArray = UnsignedSignaturePropertiesTypeImpl.this.getAttrAuthoritiesCertValuesArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeAttrAuthoritiesCertValues(n);
                    return attrAuthoritiesCertValuesArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfAttrAuthoritiesCertValuesArray();
                }
            }
            return new AttrAuthoritiesCertValuesList();
        }
    }
    
    @Deprecated
    public CertificateValuesType[] getAttrAuthoritiesCertValuesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.ATTRAUTHORITIESCERTVALUES$20, (List)list);
            final CertificateValuesType[] array = new CertificateValuesType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public CertificateValuesType getAttrAuthoritiesCertValuesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final CertificateValuesType certificateValuesType = (CertificateValuesType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRAUTHORITIESCERTVALUES$20, n);
            if (certificateValuesType == null) {
                throw new IndexOutOfBoundsException();
            }
            return certificateValuesType;
        }
    }
    
    public int sizeOfAttrAuthoritiesCertValuesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.ATTRAUTHORITIESCERTVALUES$20);
        }
    }
    
    public void setAttrAuthoritiesCertValuesArray(final CertificateValuesType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.ATTRAUTHORITIESCERTVALUES$20);
    }
    
    public void setAttrAuthoritiesCertValuesArray(final int n, final CertificateValuesType certificateValuesType) {
        this.generatedSetterHelperImpl((XmlObject)certificateValuesType, UnsignedSignaturePropertiesTypeImpl.ATTRAUTHORITIESCERTVALUES$20, n, (short)2);
    }
    
    public CertificateValuesType insertNewAttrAuthoritiesCertValues(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CertificateValuesType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRAUTHORITIESCERTVALUES$20, n);
        }
    }
    
    public CertificateValuesType addNewAttrAuthoritiesCertValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (CertificateValuesType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRAUTHORITIESCERTVALUES$20);
        }
    }
    
    public void removeAttrAuthoritiesCertValues(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.ATTRAUTHORITIESCERTVALUES$20, n);
        }
    }
    
    public List<RevocationValuesType> getAttributeRevocationValuesList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class AttributeRevocationValuesList extends AbstractList<RevocationValuesType>
            {
                @Override
                public RevocationValuesType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getAttributeRevocationValuesArray(n);
                }
                
                @Override
                public RevocationValuesType set(final int n, final RevocationValuesType revocationValuesType) {
                    final RevocationValuesType attributeRevocationValuesArray = UnsignedSignaturePropertiesTypeImpl.this.getAttributeRevocationValuesArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setAttributeRevocationValuesArray(n, revocationValuesType);
                    return attributeRevocationValuesArray;
                }
                
                @Override
                public void add(final int n, final RevocationValuesType revocationValuesType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewAttributeRevocationValues(n).set((XmlObject)revocationValuesType);
                }
                
                @Override
                public RevocationValuesType remove(final int n) {
                    final RevocationValuesType attributeRevocationValuesArray = UnsignedSignaturePropertiesTypeImpl.this.getAttributeRevocationValuesArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeAttributeRevocationValues(n);
                    return attributeRevocationValuesArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfAttributeRevocationValuesArray();
                }
            }
            return new AttributeRevocationValuesList();
        }
    }
    
    @Deprecated
    public RevocationValuesType[] getAttributeRevocationValuesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONVALUES$22, (List)list);
            final RevocationValuesType[] array = new RevocationValuesType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public RevocationValuesType getAttributeRevocationValuesArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final RevocationValuesType revocationValuesType = (RevocationValuesType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONVALUES$22, n);
            if (revocationValuesType == null) {
                throw new IndexOutOfBoundsException();
            }
            return revocationValuesType;
        }
    }
    
    public int sizeOfAttributeRevocationValuesArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONVALUES$22);
        }
    }
    
    public void setAttributeRevocationValuesArray(final RevocationValuesType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONVALUES$22);
    }
    
    public void setAttributeRevocationValuesArray(final int n, final RevocationValuesType revocationValuesType) {
        this.generatedSetterHelperImpl((XmlObject)revocationValuesType, UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONVALUES$22, n, (short)2);
    }
    
    public RevocationValuesType insertNewAttributeRevocationValues(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RevocationValuesType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONVALUES$22, n);
        }
    }
    
    public RevocationValuesType addNewAttributeRevocationValues() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (RevocationValuesType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONVALUES$22);
        }
    }
    
    public void removeAttributeRevocationValues(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.ATTRIBUTEREVOCATIONVALUES$22, n);
        }
    }
    
    public List<XAdESTimeStampType> getArchiveTimeStampList() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final class ArchiveTimeStampList extends AbstractList<XAdESTimeStampType>
            {
                @Override
                public XAdESTimeStampType get(final int n) {
                    return UnsignedSignaturePropertiesTypeImpl.this.getArchiveTimeStampArray(n);
                }
                
                @Override
                public XAdESTimeStampType set(final int n, final XAdESTimeStampType xAdESTimeStampType) {
                    final XAdESTimeStampType archiveTimeStampArray = UnsignedSignaturePropertiesTypeImpl.this.getArchiveTimeStampArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.setArchiveTimeStampArray(n, xAdESTimeStampType);
                    return archiveTimeStampArray;
                }
                
                @Override
                public void add(final int n, final XAdESTimeStampType xAdESTimeStampType) {
                    UnsignedSignaturePropertiesTypeImpl.this.insertNewArchiveTimeStamp(n).set((XmlObject)xAdESTimeStampType);
                }
                
                @Override
                public XAdESTimeStampType remove(final int n) {
                    final XAdESTimeStampType archiveTimeStampArray = UnsignedSignaturePropertiesTypeImpl.this.getArchiveTimeStampArray(n);
                    UnsignedSignaturePropertiesTypeImpl.this.removeArchiveTimeStamp(n);
                    return archiveTimeStampArray;
                }
                
                @Override
                public int size() {
                    return UnsignedSignaturePropertiesTypeImpl.this.sizeOfArchiveTimeStampArray();
                }
            }
            return new ArchiveTimeStampList();
        }
    }
    
    @Deprecated
    public XAdESTimeStampType[] getArchiveTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final ArrayList list = new ArrayList();
            this.get_store().find_all_element_users(UnsignedSignaturePropertiesTypeImpl.ARCHIVETIMESTAMP$24, (List)list);
            final XAdESTimeStampType[] array = new XAdESTimeStampType[list.size()];
            list.toArray(array);
            return array;
        }
    }
    
    public XAdESTimeStampType getArchiveTimeStampArray(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final XAdESTimeStampType xAdESTimeStampType = (XAdESTimeStampType)this.get_store().find_element_user(UnsignedSignaturePropertiesTypeImpl.ARCHIVETIMESTAMP$24, n);
            if (xAdESTimeStampType == null) {
                throw new IndexOutOfBoundsException();
            }
            return xAdESTimeStampType;
        }
    }
    
    public int sizeOfArchiveTimeStampArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(UnsignedSignaturePropertiesTypeImpl.ARCHIVETIMESTAMP$24);
        }
    }
    
    public void setArchiveTimeStampArray(final XAdESTimeStampType[] array) {
        this.check_orphaned();
        this.arraySetterHelper((XmlObject[])array, UnsignedSignaturePropertiesTypeImpl.ARCHIVETIMESTAMP$24);
    }
    
    public void setArchiveTimeStampArray(final int n, final XAdESTimeStampType xAdESTimeStampType) {
        this.generatedSetterHelperImpl((XmlObject)xAdESTimeStampType, UnsignedSignaturePropertiesTypeImpl.ARCHIVETIMESTAMP$24, n, (short)2);
    }
    
    public XAdESTimeStampType insertNewArchiveTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XAdESTimeStampType)this.get_store().insert_element_user(UnsignedSignaturePropertiesTypeImpl.ARCHIVETIMESTAMP$24, n);
        }
    }
    
    public XAdESTimeStampType addNewArchiveTimeStamp() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XAdESTimeStampType)this.get_store().add_element_user(UnsignedSignaturePropertiesTypeImpl.ARCHIVETIMESTAMP$24);
        }
    }
    
    public void removeArchiveTimeStamp(final int n) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(UnsignedSignaturePropertiesTypeImpl.ARCHIVETIMESTAMP$24, n);
        }
    }
    
    public String getId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(UnsignedSignaturePropertiesTypeImpl.ID$26);
            if (simpleValue == null) {
                return null;
            }
            return simpleValue.getStringValue();
        }
    }
    
    public XmlID xgetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return (XmlID)this.get_store().find_attribute_user(UnsignedSignaturePropertiesTypeImpl.ID$26);
        }
    }
    
    public boolean isSetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().find_attribute_user(UnsignedSignaturePropertiesTypeImpl.ID$26) != null;
        }
    }
    
    public void setId(final String stringValue) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue simpleValue = (SimpleValue)this.get_store().find_attribute_user(UnsignedSignaturePropertiesTypeImpl.ID$26);
            if (simpleValue == null) {
                simpleValue = (SimpleValue)this.get_store().add_attribute_user(UnsignedSignaturePropertiesTypeImpl.ID$26);
            }
            simpleValue.setStringValue(stringValue);
        }
    }
    
    public void xsetId(final XmlID xmlID) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlID xmlID2 = (XmlID)this.get_store().find_attribute_user(UnsignedSignaturePropertiesTypeImpl.ID$26);
            if (xmlID2 == null) {
                xmlID2 = (XmlID)this.get_store().add_attribute_user(UnsignedSignaturePropertiesTypeImpl.ID$26);
            }
            xmlID2.set((XmlObject)xmlID);
        }
    }
    
    public void unsetId() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_attribute(UnsignedSignaturePropertiesTypeImpl.ID$26);
        }
    }
    
    static {
        COUNTERSIGNATURE$0 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CounterSignature");
        SIGNATURETIMESTAMP$2 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SignatureTimeStamp");
        COMPLETECERTIFICATEREFS$4 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CompleteCertificateRefs");
        COMPLETEREVOCATIONREFS$6 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CompleteRevocationRefs");
        ATTRIBUTECERTIFICATEREFS$8 = new QName("http://uri.etsi.org/01903/v1.3.2#", "AttributeCertificateRefs");
        ATTRIBUTEREVOCATIONREFS$10 = new QName("http://uri.etsi.org/01903/v1.3.2#", "AttributeRevocationRefs");
        SIGANDREFSTIMESTAMP$12 = new QName("http://uri.etsi.org/01903/v1.3.2#", "SigAndRefsTimeStamp");
        REFSONLYTIMESTAMP$14 = new QName("http://uri.etsi.org/01903/v1.3.2#", "RefsOnlyTimeStamp");
        CERTIFICATEVALUES$16 = new QName("http://uri.etsi.org/01903/v1.3.2#", "CertificateValues");
        REVOCATIONVALUES$18 = new QName("http://uri.etsi.org/01903/v1.3.2#", "RevocationValues");
        ATTRAUTHORITIESCERTVALUES$20 = new QName("http://uri.etsi.org/01903/v1.3.2#", "AttrAuthoritiesCertValues");
        ATTRIBUTEREVOCATIONVALUES$22 = new QName("http://uri.etsi.org/01903/v1.3.2#", "AttributeRevocationValues");
        ARCHIVETIMESTAMP$24 = new QName("http://uri.etsi.org/01903/v1.3.2#", "ArchiveTimeStamp");
        ID$26 = new QName("", "Id");
    }
}
