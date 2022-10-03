package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.PatternDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.WhiteSpaceDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;
import org.apache.xmlbeans.impl.xb.xsdschema.NumFacet;
import org.apache.xmlbeans.impl.xb.xsdschema.TotalDigitsDocument;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class RestrictionDocumentImpl extends XmlComplexContentImpl implements RestrictionDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName RESTRICTION$0;
    
    public RestrictionDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Restriction getRestriction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Restriction target = null;
            target = (Restriction)this.get_store().find_element_user(RestrictionDocumentImpl.RESTRICTION$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setRestriction(final Restriction restriction) {
        this.generatedSetterHelperImpl(restriction, RestrictionDocumentImpl.RESTRICTION$0, 0, (short)1);
    }
    
    @Override
    public Restriction addNewRestriction() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Restriction target = null;
            target = (Restriction)this.get_store().add_element_user(RestrictionDocumentImpl.RESTRICTION$0);
            return target;
        }
    }
    
    static {
        RESTRICTION$0 = new QName("http://www.w3.org/2001/XMLSchema", "restriction");
    }
    
    public static class RestrictionImpl extends AnnotatedImpl implements Restriction
    {
        private static final long serialVersionUID = 1L;
        private static final QName SIMPLETYPE$0;
        private static final QName MINEXCLUSIVE$2;
        private static final QName MININCLUSIVE$4;
        private static final QName MAXEXCLUSIVE$6;
        private static final QName MAXINCLUSIVE$8;
        private static final QName TOTALDIGITS$10;
        private static final QName FRACTIONDIGITS$12;
        private static final QName LENGTH$14;
        private static final QName MINLENGTH$16;
        private static final QName MAXLENGTH$18;
        private static final QName ENUMERATION$20;
        private static final QName WHITESPACE$22;
        private static final QName PATTERN$24;
        private static final QName BASE$26;
        
        public RestrictionImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public LocalSimpleType getSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)this.get_store().find_element_user(RestrictionImpl.SIMPLETYPE$0, 0);
                if (target == null) {
                    return null;
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.SIMPLETYPE$0) != 0;
            }
        }
        
        @Override
        public void setSimpleType(final LocalSimpleType simpleType) {
            this.generatedSetterHelperImpl(simpleType, RestrictionImpl.SIMPLETYPE$0, 0, (short)1);
        }
        
        @Override
        public LocalSimpleType addNewSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)this.get_store().add_element_user(RestrictionImpl.SIMPLETYPE$0);
                return target;
            }
        }
        
        @Override
        public void unsetSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.SIMPLETYPE$0, 0);
            }
        }
        
        @Override
        public Facet[] getMinExclusiveArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.MINEXCLUSIVE$2, targetList);
                final Facet[] result = new Facet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public Facet getMinExclusiveArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().find_element_user(RestrictionImpl.MINEXCLUSIVE$2, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfMinExclusiveArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.MINEXCLUSIVE$2);
            }
        }
        
        @Override
        public void setMinExclusiveArray(final Facet[] minExclusiveArray) {
            this.check_orphaned();
            this.arraySetterHelper(minExclusiveArray, RestrictionImpl.MINEXCLUSIVE$2);
        }
        
        @Override
        public void setMinExclusiveArray(final int i, final Facet minExclusive) {
            this.generatedSetterHelperImpl(minExclusive, RestrictionImpl.MINEXCLUSIVE$2, i, (short)2);
        }
        
        @Override
        public Facet insertNewMinExclusive(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().insert_element_user(RestrictionImpl.MINEXCLUSIVE$2, i);
                return target;
            }
        }
        
        @Override
        public Facet addNewMinExclusive() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().add_element_user(RestrictionImpl.MINEXCLUSIVE$2);
                return target;
            }
        }
        
        @Override
        public void removeMinExclusive(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.MINEXCLUSIVE$2, i);
            }
        }
        
        @Override
        public Facet[] getMinInclusiveArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.MININCLUSIVE$4, targetList);
                final Facet[] result = new Facet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public Facet getMinInclusiveArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().find_element_user(RestrictionImpl.MININCLUSIVE$4, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfMinInclusiveArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.MININCLUSIVE$4);
            }
        }
        
        @Override
        public void setMinInclusiveArray(final Facet[] minInclusiveArray) {
            this.check_orphaned();
            this.arraySetterHelper(minInclusiveArray, RestrictionImpl.MININCLUSIVE$4);
        }
        
        @Override
        public void setMinInclusiveArray(final int i, final Facet minInclusive) {
            this.generatedSetterHelperImpl(minInclusive, RestrictionImpl.MININCLUSIVE$4, i, (short)2);
        }
        
        @Override
        public Facet insertNewMinInclusive(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().insert_element_user(RestrictionImpl.MININCLUSIVE$4, i);
                return target;
            }
        }
        
        @Override
        public Facet addNewMinInclusive() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().add_element_user(RestrictionImpl.MININCLUSIVE$4);
                return target;
            }
        }
        
        @Override
        public void removeMinInclusive(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.MININCLUSIVE$4, i);
            }
        }
        
        @Override
        public Facet[] getMaxExclusiveArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.MAXEXCLUSIVE$6, targetList);
                final Facet[] result = new Facet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public Facet getMaxExclusiveArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().find_element_user(RestrictionImpl.MAXEXCLUSIVE$6, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfMaxExclusiveArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.MAXEXCLUSIVE$6);
            }
        }
        
        @Override
        public void setMaxExclusiveArray(final Facet[] maxExclusiveArray) {
            this.check_orphaned();
            this.arraySetterHelper(maxExclusiveArray, RestrictionImpl.MAXEXCLUSIVE$6);
        }
        
        @Override
        public void setMaxExclusiveArray(final int i, final Facet maxExclusive) {
            this.generatedSetterHelperImpl(maxExclusive, RestrictionImpl.MAXEXCLUSIVE$6, i, (short)2);
        }
        
        @Override
        public Facet insertNewMaxExclusive(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().insert_element_user(RestrictionImpl.MAXEXCLUSIVE$6, i);
                return target;
            }
        }
        
        @Override
        public Facet addNewMaxExclusive() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().add_element_user(RestrictionImpl.MAXEXCLUSIVE$6);
                return target;
            }
        }
        
        @Override
        public void removeMaxExclusive(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.MAXEXCLUSIVE$6, i);
            }
        }
        
        @Override
        public Facet[] getMaxInclusiveArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.MAXINCLUSIVE$8, targetList);
                final Facet[] result = new Facet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public Facet getMaxInclusiveArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().find_element_user(RestrictionImpl.MAXINCLUSIVE$8, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfMaxInclusiveArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.MAXINCLUSIVE$8);
            }
        }
        
        @Override
        public void setMaxInclusiveArray(final Facet[] maxInclusiveArray) {
            this.check_orphaned();
            this.arraySetterHelper(maxInclusiveArray, RestrictionImpl.MAXINCLUSIVE$8);
        }
        
        @Override
        public void setMaxInclusiveArray(final int i, final Facet maxInclusive) {
            this.generatedSetterHelperImpl(maxInclusive, RestrictionImpl.MAXINCLUSIVE$8, i, (short)2);
        }
        
        @Override
        public Facet insertNewMaxInclusive(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().insert_element_user(RestrictionImpl.MAXINCLUSIVE$8, i);
                return target;
            }
        }
        
        @Override
        public Facet addNewMaxInclusive() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                Facet target = null;
                target = (Facet)this.get_store().add_element_user(RestrictionImpl.MAXINCLUSIVE$8);
                return target;
            }
        }
        
        @Override
        public void removeMaxInclusive(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.MAXINCLUSIVE$8, i);
            }
        }
        
        @Override
        public TotalDigitsDocument.TotalDigits[] getTotalDigitsArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.TOTALDIGITS$10, targetList);
                final TotalDigitsDocument.TotalDigits[] result = new TotalDigitsDocument.TotalDigits[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public TotalDigitsDocument.TotalDigits getTotalDigitsArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TotalDigitsDocument.TotalDigits target = null;
                target = (TotalDigitsDocument.TotalDigits)this.get_store().find_element_user(RestrictionImpl.TOTALDIGITS$10, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfTotalDigitsArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.TOTALDIGITS$10);
            }
        }
        
        @Override
        public void setTotalDigitsArray(final TotalDigitsDocument.TotalDigits[] totalDigitsArray) {
            this.check_orphaned();
            this.arraySetterHelper(totalDigitsArray, RestrictionImpl.TOTALDIGITS$10);
        }
        
        @Override
        public void setTotalDigitsArray(final int i, final TotalDigitsDocument.TotalDigits totalDigits) {
            this.generatedSetterHelperImpl(totalDigits, RestrictionImpl.TOTALDIGITS$10, i, (short)2);
        }
        
        @Override
        public TotalDigitsDocument.TotalDigits insertNewTotalDigits(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TotalDigitsDocument.TotalDigits target = null;
                target = (TotalDigitsDocument.TotalDigits)this.get_store().insert_element_user(RestrictionImpl.TOTALDIGITS$10, i);
                return target;
            }
        }
        
        @Override
        public TotalDigitsDocument.TotalDigits addNewTotalDigits() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TotalDigitsDocument.TotalDigits target = null;
                target = (TotalDigitsDocument.TotalDigits)this.get_store().add_element_user(RestrictionImpl.TOTALDIGITS$10);
                return target;
            }
        }
        
        @Override
        public void removeTotalDigits(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.TOTALDIGITS$10, i);
            }
        }
        
        @Override
        public NumFacet[] getFractionDigitsArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.FRACTIONDIGITS$12, targetList);
                final NumFacet[] result = new NumFacet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public NumFacet getFractionDigitsArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().find_element_user(RestrictionImpl.FRACTIONDIGITS$12, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfFractionDigitsArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.FRACTIONDIGITS$12);
            }
        }
        
        @Override
        public void setFractionDigitsArray(final NumFacet[] fractionDigitsArray) {
            this.check_orphaned();
            this.arraySetterHelper(fractionDigitsArray, RestrictionImpl.FRACTIONDIGITS$12);
        }
        
        @Override
        public void setFractionDigitsArray(final int i, final NumFacet fractionDigits) {
            this.generatedSetterHelperImpl(fractionDigits, RestrictionImpl.FRACTIONDIGITS$12, i, (short)2);
        }
        
        @Override
        public NumFacet insertNewFractionDigits(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().insert_element_user(RestrictionImpl.FRACTIONDIGITS$12, i);
                return target;
            }
        }
        
        @Override
        public NumFacet addNewFractionDigits() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().add_element_user(RestrictionImpl.FRACTIONDIGITS$12);
                return target;
            }
        }
        
        @Override
        public void removeFractionDigits(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.FRACTIONDIGITS$12, i);
            }
        }
        
        @Override
        public NumFacet[] getLengthArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.LENGTH$14, targetList);
                final NumFacet[] result = new NumFacet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public NumFacet getLengthArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().find_element_user(RestrictionImpl.LENGTH$14, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfLengthArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.LENGTH$14);
            }
        }
        
        @Override
        public void setLengthArray(final NumFacet[] lengthArray) {
            this.check_orphaned();
            this.arraySetterHelper(lengthArray, RestrictionImpl.LENGTH$14);
        }
        
        @Override
        public void setLengthArray(final int i, final NumFacet length) {
            this.generatedSetterHelperImpl(length, RestrictionImpl.LENGTH$14, i, (short)2);
        }
        
        @Override
        public NumFacet insertNewLength(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().insert_element_user(RestrictionImpl.LENGTH$14, i);
                return target;
            }
        }
        
        @Override
        public NumFacet addNewLength() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().add_element_user(RestrictionImpl.LENGTH$14);
                return target;
            }
        }
        
        @Override
        public void removeLength(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.LENGTH$14, i);
            }
        }
        
        @Override
        public NumFacet[] getMinLengthArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.MINLENGTH$16, targetList);
                final NumFacet[] result = new NumFacet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public NumFacet getMinLengthArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().find_element_user(RestrictionImpl.MINLENGTH$16, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfMinLengthArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.MINLENGTH$16);
            }
        }
        
        @Override
        public void setMinLengthArray(final NumFacet[] minLengthArray) {
            this.check_orphaned();
            this.arraySetterHelper(minLengthArray, RestrictionImpl.MINLENGTH$16);
        }
        
        @Override
        public void setMinLengthArray(final int i, final NumFacet minLength) {
            this.generatedSetterHelperImpl(minLength, RestrictionImpl.MINLENGTH$16, i, (short)2);
        }
        
        @Override
        public NumFacet insertNewMinLength(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().insert_element_user(RestrictionImpl.MINLENGTH$16, i);
                return target;
            }
        }
        
        @Override
        public NumFacet addNewMinLength() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().add_element_user(RestrictionImpl.MINLENGTH$16);
                return target;
            }
        }
        
        @Override
        public void removeMinLength(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.MINLENGTH$16, i);
            }
        }
        
        @Override
        public NumFacet[] getMaxLengthArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.MAXLENGTH$18, targetList);
                final NumFacet[] result = new NumFacet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public NumFacet getMaxLengthArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().find_element_user(RestrictionImpl.MAXLENGTH$18, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfMaxLengthArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.MAXLENGTH$18);
            }
        }
        
        @Override
        public void setMaxLengthArray(final NumFacet[] maxLengthArray) {
            this.check_orphaned();
            this.arraySetterHelper(maxLengthArray, RestrictionImpl.MAXLENGTH$18);
        }
        
        @Override
        public void setMaxLengthArray(final int i, final NumFacet maxLength) {
            this.generatedSetterHelperImpl(maxLength, RestrictionImpl.MAXLENGTH$18, i, (short)2);
        }
        
        @Override
        public NumFacet insertNewMaxLength(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().insert_element_user(RestrictionImpl.MAXLENGTH$18, i);
                return target;
            }
        }
        
        @Override
        public NumFacet addNewMaxLength() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NumFacet target = null;
                target = (NumFacet)this.get_store().add_element_user(RestrictionImpl.MAXLENGTH$18);
                return target;
            }
        }
        
        @Override
        public void removeMaxLength(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.MAXLENGTH$18, i);
            }
        }
        
        @Override
        public NoFixedFacet[] getEnumerationArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.ENUMERATION$20, targetList);
                final NoFixedFacet[] result = new NoFixedFacet[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public NoFixedFacet getEnumerationArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NoFixedFacet target = null;
                target = (NoFixedFacet)this.get_store().find_element_user(RestrictionImpl.ENUMERATION$20, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfEnumerationArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.ENUMERATION$20);
            }
        }
        
        @Override
        public void setEnumerationArray(final NoFixedFacet[] enumerationArray) {
            this.check_orphaned();
            this.arraySetterHelper(enumerationArray, RestrictionImpl.ENUMERATION$20);
        }
        
        @Override
        public void setEnumerationArray(final int i, final NoFixedFacet enumeration) {
            this.generatedSetterHelperImpl(enumeration, RestrictionImpl.ENUMERATION$20, i, (short)2);
        }
        
        @Override
        public NoFixedFacet insertNewEnumeration(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NoFixedFacet target = null;
                target = (NoFixedFacet)this.get_store().insert_element_user(RestrictionImpl.ENUMERATION$20, i);
                return target;
            }
        }
        
        @Override
        public NoFixedFacet addNewEnumeration() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NoFixedFacet target = null;
                target = (NoFixedFacet)this.get_store().add_element_user(RestrictionImpl.ENUMERATION$20);
                return target;
            }
        }
        
        @Override
        public void removeEnumeration(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.ENUMERATION$20, i);
            }
        }
        
        @Override
        public WhiteSpaceDocument.WhiteSpace[] getWhiteSpaceArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.WHITESPACE$22, targetList);
                final WhiteSpaceDocument.WhiteSpace[] result = new WhiteSpaceDocument.WhiteSpace[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public WhiteSpaceDocument.WhiteSpace getWhiteSpaceArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                WhiteSpaceDocument.WhiteSpace target = null;
                target = (WhiteSpaceDocument.WhiteSpace)this.get_store().find_element_user(RestrictionImpl.WHITESPACE$22, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfWhiteSpaceArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.WHITESPACE$22);
            }
        }
        
        @Override
        public void setWhiteSpaceArray(final WhiteSpaceDocument.WhiteSpace[] whiteSpaceArray) {
            this.check_orphaned();
            this.arraySetterHelper(whiteSpaceArray, RestrictionImpl.WHITESPACE$22);
        }
        
        @Override
        public void setWhiteSpaceArray(final int i, final WhiteSpaceDocument.WhiteSpace whiteSpace) {
            this.generatedSetterHelperImpl(whiteSpace, RestrictionImpl.WHITESPACE$22, i, (short)2);
        }
        
        @Override
        public WhiteSpaceDocument.WhiteSpace insertNewWhiteSpace(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                WhiteSpaceDocument.WhiteSpace target = null;
                target = (WhiteSpaceDocument.WhiteSpace)this.get_store().insert_element_user(RestrictionImpl.WHITESPACE$22, i);
                return target;
            }
        }
        
        @Override
        public WhiteSpaceDocument.WhiteSpace addNewWhiteSpace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                WhiteSpaceDocument.WhiteSpace target = null;
                target = (WhiteSpaceDocument.WhiteSpace)this.get_store().add_element_user(RestrictionImpl.WHITESPACE$22);
                return target;
            }
        }
        
        @Override
        public void removeWhiteSpace(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.WHITESPACE$22, i);
            }
        }
        
        @Override
        public PatternDocument.Pattern[] getPatternArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RestrictionImpl.PATTERN$24, targetList);
                final PatternDocument.Pattern[] result = new PatternDocument.Pattern[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public PatternDocument.Pattern getPatternArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                PatternDocument.Pattern target = null;
                target = (PatternDocument.Pattern)this.get_store().find_element_user(RestrictionImpl.PATTERN$24, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfPatternArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RestrictionImpl.PATTERN$24);
            }
        }
        
        @Override
        public void setPatternArray(final PatternDocument.Pattern[] patternArray) {
            this.check_orphaned();
            this.arraySetterHelper(patternArray, RestrictionImpl.PATTERN$24);
        }
        
        @Override
        public void setPatternArray(final int i, final PatternDocument.Pattern pattern) {
            this.generatedSetterHelperImpl(pattern, RestrictionImpl.PATTERN$24, i, (short)2);
        }
        
        @Override
        public PatternDocument.Pattern insertNewPattern(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                PatternDocument.Pattern target = null;
                target = (PatternDocument.Pattern)this.get_store().insert_element_user(RestrictionImpl.PATTERN$24, i);
                return target;
            }
        }
        
        @Override
        public PatternDocument.Pattern addNewPattern() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                PatternDocument.Pattern target = null;
                target = (PatternDocument.Pattern)this.get_store().add_element_user(RestrictionImpl.PATTERN$24);
                return target;
            }
        }
        
        @Override
        public void removePattern(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RestrictionImpl.PATTERN$24, i);
            }
        }
        
        @Override
        public QName getBase() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(RestrictionImpl.BASE$26);
                if (target == null) {
                    return null;
                }
                return target.getQNameValue();
            }
        }
        
        @Override
        public XmlQName xgetBase() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlQName target = null;
                target = (XmlQName)this.get_store().find_attribute_user(RestrictionImpl.BASE$26);
                return target;
            }
        }
        
        @Override
        public boolean isSetBase() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(RestrictionImpl.BASE$26) != null;
            }
        }
        
        @Override
        public void setBase(final QName base) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(RestrictionImpl.BASE$26);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(RestrictionImpl.BASE$26);
                }
                target.setQNameValue(base);
            }
        }
        
        @Override
        public void xsetBase(final XmlQName base) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlQName target = null;
                target = (XmlQName)this.get_store().find_attribute_user(RestrictionImpl.BASE$26);
                if (target == null) {
                    target = (XmlQName)this.get_store().add_attribute_user(RestrictionImpl.BASE$26);
                }
                target.set(base);
            }
        }
        
        @Override
        public void unsetBase() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(RestrictionImpl.BASE$26);
            }
        }
        
        static {
            SIMPLETYPE$0 = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
            MINEXCLUSIVE$2 = new QName("http://www.w3.org/2001/XMLSchema", "minExclusive");
            MININCLUSIVE$4 = new QName("http://www.w3.org/2001/XMLSchema", "minInclusive");
            MAXEXCLUSIVE$6 = new QName("http://www.w3.org/2001/XMLSchema", "maxExclusive");
            MAXINCLUSIVE$8 = new QName("http://www.w3.org/2001/XMLSchema", "maxInclusive");
            TOTALDIGITS$10 = new QName("http://www.w3.org/2001/XMLSchema", "totalDigits");
            FRACTIONDIGITS$12 = new QName("http://www.w3.org/2001/XMLSchema", "fractionDigits");
            LENGTH$14 = new QName("http://www.w3.org/2001/XMLSchema", "length");
            MINLENGTH$16 = new QName("http://www.w3.org/2001/XMLSchema", "minLength");
            MAXLENGTH$18 = new QName("http://www.w3.org/2001/XMLSchema", "maxLength");
            ENUMERATION$20 = new QName("http://www.w3.org/2001/XMLSchema", "enumeration");
            WHITESPACE$22 = new QName("http://www.w3.org/2001/XMLSchema", "whiteSpace");
            PATTERN$24 = new QName("http://www.w3.org/2001/XMLSchema", "pattern");
            BASE$26 = new QName("", "base");
        }
    }
}
