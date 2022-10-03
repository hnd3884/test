package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.Wildcard;
import org.apache.xmlbeans.impl.xb.xsdschema.AttributeGroupRef;
import org.apache.xmlbeans.impl.xb.xsdschema.Attribute;
import org.apache.xmlbeans.impl.xb.xsdschema.PatternDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.WhiteSpaceDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.NoFixedFacet;
import org.apache.xmlbeans.impl.xb.xsdschema.NumFacet;
import org.apache.xmlbeans.impl.xb.xsdschema.TotalDigitsDocument;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.Facet;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.GroupRef;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.RestrictionType;

public class RestrictionTypeImpl extends AnnotatedImpl implements RestrictionType
{
    private static final long serialVersionUID = 1L;
    private static final QName GROUP$0;
    private static final QName ALL$2;
    private static final QName CHOICE$4;
    private static final QName SEQUENCE$6;
    private static final QName SIMPLETYPE$8;
    private static final QName MINEXCLUSIVE$10;
    private static final QName MININCLUSIVE$12;
    private static final QName MAXEXCLUSIVE$14;
    private static final QName MAXINCLUSIVE$16;
    private static final QName TOTALDIGITS$18;
    private static final QName FRACTIONDIGITS$20;
    private static final QName LENGTH$22;
    private static final QName MINLENGTH$24;
    private static final QName MAXLENGTH$26;
    private static final QName ENUMERATION$28;
    private static final QName WHITESPACE$30;
    private static final QName PATTERN$32;
    private static final QName ATTRIBUTE$34;
    private static final QName ATTRIBUTEGROUP$36;
    private static final QName ANYATTRIBUTE$38;
    private static final QName BASE$40;
    
    public RestrictionTypeImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public GroupRef getGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)this.get_store().find_element_user(RestrictionTypeImpl.GROUP$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RestrictionTypeImpl.GROUP$0) != 0;
        }
    }
    
    @Override
    public void setGroup(final GroupRef group) {
        this.generatedSetterHelperImpl(group, RestrictionTypeImpl.GROUP$0, 0, (short)1);
    }
    
    @Override
    public GroupRef addNewGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            GroupRef target = null;
            target = (GroupRef)this.get_store().add_element_user(RestrictionTypeImpl.GROUP$0);
            return target;
        }
    }
    
    @Override
    public void unsetGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.GROUP$0, 0);
        }
    }
    
    @Override
    public All getAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().find_element_user(RestrictionTypeImpl.ALL$2, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RestrictionTypeImpl.ALL$2) != 0;
        }
    }
    
    @Override
    public void setAll(final All all) {
        this.generatedSetterHelperImpl(all, RestrictionTypeImpl.ALL$2, 0, (short)1);
    }
    
    @Override
    public All addNewAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().add_element_user(RestrictionTypeImpl.ALL$2);
            return target;
        }
    }
    
    @Override
    public void unsetAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.ALL$2, 0);
        }
    }
    
    @Override
    public ExplicitGroup getChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(RestrictionTypeImpl.CHOICE$4, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RestrictionTypeImpl.CHOICE$4) != 0;
        }
    }
    
    @Override
    public void setChoice(final ExplicitGroup choice) {
        this.generatedSetterHelperImpl(choice, RestrictionTypeImpl.CHOICE$4, 0, (short)1);
    }
    
    @Override
    public ExplicitGroup addNewChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(RestrictionTypeImpl.CHOICE$4);
            return target;
        }
    }
    
    @Override
    public void unsetChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.CHOICE$4, 0);
        }
    }
    
    @Override
    public ExplicitGroup getSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(RestrictionTypeImpl.SEQUENCE$6, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RestrictionTypeImpl.SEQUENCE$6) != 0;
        }
    }
    
    @Override
    public void setSequence(final ExplicitGroup sequence) {
        this.generatedSetterHelperImpl(sequence, RestrictionTypeImpl.SEQUENCE$6, 0, (short)1);
    }
    
    @Override
    public ExplicitGroup addNewSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(RestrictionTypeImpl.SEQUENCE$6);
            return target;
        }
    }
    
    @Override
    public void unsetSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.SEQUENCE$6, 0);
        }
    }
    
    @Override
    public LocalSimpleType getSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalSimpleType target = null;
            target = (LocalSimpleType)this.get_store().find_element_user(RestrictionTypeImpl.SIMPLETYPE$8, 0);
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
            return this.get_store().count_elements(RestrictionTypeImpl.SIMPLETYPE$8) != 0;
        }
    }
    
    @Override
    public void setSimpleType(final LocalSimpleType simpleType) {
        this.generatedSetterHelperImpl(simpleType, RestrictionTypeImpl.SIMPLETYPE$8, 0, (short)1);
    }
    
    @Override
    public LocalSimpleType addNewSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            LocalSimpleType target = null;
            target = (LocalSimpleType)this.get_store().add_element_user(RestrictionTypeImpl.SIMPLETYPE$8);
            return target;
        }
    }
    
    @Override
    public void unsetSimpleType() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.SIMPLETYPE$8, 0);
        }
    }
    
    @Override
    public Facet[] getMinExclusiveArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.MINEXCLUSIVE$10, targetList);
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
            target = (Facet)this.get_store().find_element_user(RestrictionTypeImpl.MINEXCLUSIVE$10, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.MINEXCLUSIVE$10);
        }
    }
    
    @Override
    public void setMinExclusiveArray(final Facet[] minExclusiveArray) {
        this.check_orphaned();
        this.arraySetterHelper(minExclusiveArray, RestrictionTypeImpl.MINEXCLUSIVE$10);
    }
    
    @Override
    public void setMinExclusiveArray(final int i, final Facet minExclusive) {
        this.generatedSetterHelperImpl(minExclusive, RestrictionTypeImpl.MINEXCLUSIVE$10, i, (short)2);
    }
    
    @Override
    public Facet insertNewMinExclusive(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().insert_element_user(RestrictionTypeImpl.MINEXCLUSIVE$10, i);
            return target;
        }
    }
    
    @Override
    public Facet addNewMinExclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().add_element_user(RestrictionTypeImpl.MINEXCLUSIVE$10);
            return target;
        }
    }
    
    @Override
    public void removeMinExclusive(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.MINEXCLUSIVE$10, i);
        }
    }
    
    @Override
    public Facet[] getMinInclusiveArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.MININCLUSIVE$12, targetList);
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
            target = (Facet)this.get_store().find_element_user(RestrictionTypeImpl.MININCLUSIVE$12, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.MININCLUSIVE$12);
        }
    }
    
    @Override
    public void setMinInclusiveArray(final Facet[] minInclusiveArray) {
        this.check_orphaned();
        this.arraySetterHelper(minInclusiveArray, RestrictionTypeImpl.MININCLUSIVE$12);
    }
    
    @Override
    public void setMinInclusiveArray(final int i, final Facet minInclusive) {
        this.generatedSetterHelperImpl(minInclusive, RestrictionTypeImpl.MININCLUSIVE$12, i, (short)2);
    }
    
    @Override
    public Facet insertNewMinInclusive(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().insert_element_user(RestrictionTypeImpl.MININCLUSIVE$12, i);
            return target;
        }
    }
    
    @Override
    public Facet addNewMinInclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().add_element_user(RestrictionTypeImpl.MININCLUSIVE$12);
            return target;
        }
    }
    
    @Override
    public void removeMinInclusive(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.MININCLUSIVE$12, i);
        }
    }
    
    @Override
    public Facet[] getMaxExclusiveArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.MAXEXCLUSIVE$14, targetList);
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
            target = (Facet)this.get_store().find_element_user(RestrictionTypeImpl.MAXEXCLUSIVE$14, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.MAXEXCLUSIVE$14);
        }
    }
    
    @Override
    public void setMaxExclusiveArray(final Facet[] maxExclusiveArray) {
        this.check_orphaned();
        this.arraySetterHelper(maxExclusiveArray, RestrictionTypeImpl.MAXEXCLUSIVE$14);
    }
    
    @Override
    public void setMaxExclusiveArray(final int i, final Facet maxExclusive) {
        this.generatedSetterHelperImpl(maxExclusive, RestrictionTypeImpl.MAXEXCLUSIVE$14, i, (short)2);
    }
    
    @Override
    public Facet insertNewMaxExclusive(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().insert_element_user(RestrictionTypeImpl.MAXEXCLUSIVE$14, i);
            return target;
        }
    }
    
    @Override
    public Facet addNewMaxExclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().add_element_user(RestrictionTypeImpl.MAXEXCLUSIVE$14);
            return target;
        }
    }
    
    @Override
    public void removeMaxExclusive(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.MAXEXCLUSIVE$14, i);
        }
    }
    
    @Override
    public Facet[] getMaxInclusiveArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.MAXINCLUSIVE$16, targetList);
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
            target = (Facet)this.get_store().find_element_user(RestrictionTypeImpl.MAXINCLUSIVE$16, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.MAXINCLUSIVE$16);
        }
    }
    
    @Override
    public void setMaxInclusiveArray(final Facet[] maxInclusiveArray) {
        this.check_orphaned();
        this.arraySetterHelper(maxInclusiveArray, RestrictionTypeImpl.MAXINCLUSIVE$16);
    }
    
    @Override
    public void setMaxInclusiveArray(final int i, final Facet maxInclusive) {
        this.generatedSetterHelperImpl(maxInclusive, RestrictionTypeImpl.MAXINCLUSIVE$16, i, (short)2);
    }
    
    @Override
    public Facet insertNewMaxInclusive(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().insert_element_user(RestrictionTypeImpl.MAXINCLUSIVE$16, i);
            return target;
        }
    }
    
    @Override
    public Facet addNewMaxInclusive() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Facet target = null;
            target = (Facet)this.get_store().add_element_user(RestrictionTypeImpl.MAXINCLUSIVE$16);
            return target;
        }
    }
    
    @Override
    public void removeMaxInclusive(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.MAXINCLUSIVE$16, i);
        }
    }
    
    @Override
    public TotalDigitsDocument.TotalDigits[] getTotalDigitsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.TOTALDIGITS$18, targetList);
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
            target = (TotalDigitsDocument.TotalDigits)this.get_store().find_element_user(RestrictionTypeImpl.TOTALDIGITS$18, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.TOTALDIGITS$18);
        }
    }
    
    @Override
    public void setTotalDigitsArray(final TotalDigitsDocument.TotalDigits[] totalDigitsArray) {
        this.check_orphaned();
        this.arraySetterHelper(totalDigitsArray, RestrictionTypeImpl.TOTALDIGITS$18);
    }
    
    @Override
    public void setTotalDigitsArray(final int i, final TotalDigitsDocument.TotalDigits totalDigits) {
        this.generatedSetterHelperImpl(totalDigits, RestrictionTypeImpl.TOTALDIGITS$18, i, (short)2);
    }
    
    @Override
    public TotalDigitsDocument.TotalDigits insertNewTotalDigits(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TotalDigitsDocument.TotalDigits target = null;
            target = (TotalDigitsDocument.TotalDigits)this.get_store().insert_element_user(RestrictionTypeImpl.TOTALDIGITS$18, i);
            return target;
        }
    }
    
    @Override
    public TotalDigitsDocument.TotalDigits addNewTotalDigits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            TotalDigitsDocument.TotalDigits target = null;
            target = (TotalDigitsDocument.TotalDigits)this.get_store().add_element_user(RestrictionTypeImpl.TOTALDIGITS$18);
            return target;
        }
    }
    
    @Override
    public void removeTotalDigits(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.TOTALDIGITS$18, i);
        }
    }
    
    @Override
    public NumFacet[] getFractionDigitsArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.FRACTIONDIGITS$20, targetList);
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
            target = (NumFacet)this.get_store().find_element_user(RestrictionTypeImpl.FRACTIONDIGITS$20, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.FRACTIONDIGITS$20);
        }
    }
    
    @Override
    public void setFractionDigitsArray(final NumFacet[] fractionDigitsArray) {
        this.check_orphaned();
        this.arraySetterHelper(fractionDigitsArray, RestrictionTypeImpl.FRACTIONDIGITS$20);
    }
    
    @Override
    public void setFractionDigitsArray(final int i, final NumFacet fractionDigits) {
        this.generatedSetterHelperImpl(fractionDigits, RestrictionTypeImpl.FRACTIONDIGITS$20, i, (short)2);
    }
    
    @Override
    public NumFacet insertNewFractionDigits(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().insert_element_user(RestrictionTypeImpl.FRACTIONDIGITS$20, i);
            return target;
        }
    }
    
    @Override
    public NumFacet addNewFractionDigits() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().add_element_user(RestrictionTypeImpl.FRACTIONDIGITS$20);
            return target;
        }
    }
    
    @Override
    public void removeFractionDigits(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.FRACTIONDIGITS$20, i);
        }
    }
    
    @Override
    public NumFacet[] getLengthArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.LENGTH$22, targetList);
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
            target = (NumFacet)this.get_store().find_element_user(RestrictionTypeImpl.LENGTH$22, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.LENGTH$22);
        }
    }
    
    @Override
    public void setLengthArray(final NumFacet[] lengthArray) {
        this.check_orphaned();
        this.arraySetterHelper(lengthArray, RestrictionTypeImpl.LENGTH$22);
    }
    
    @Override
    public void setLengthArray(final int i, final NumFacet length) {
        this.generatedSetterHelperImpl(length, RestrictionTypeImpl.LENGTH$22, i, (short)2);
    }
    
    @Override
    public NumFacet insertNewLength(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().insert_element_user(RestrictionTypeImpl.LENGTH$22, i);
            return target;
        }
    }
    
    @Override
    public NumFacet addNewLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().add_element_user(RestrictionTypeImpl.LENGTH$22);
            return target;
        }
    }
    
    @Override
    public void removeLength(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.LENGTH$22, i);
        }
    }
    
    @Override
    public NumFacet[] getMinLengthArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.MINLENGTH$24, targetList);
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
            target = (NumFacet)this.get_store().find_element_user(RestrictionTypeImpl.MINLENGTH$24, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.MINLENGTH$24);
        }
    }
    
    @Override
    public void setMinLengthArray(final NumFacet[] minLengthArray) {
        this.check_orphaned();
        this.arraySetterHelper(minLengthArray, RestrictionTypeImpl.MINLENGTH$24);
    }
    
    @Override
    public void setMinLengthArray(final int i, final NumFacet minLength) {
        this.generatedSetterHelperImpl(minLength, RestrictionTypeImpl.MINLENGTH$24, i, (short)2);
    }
    
    @Override
    public NumFacet insertNewMinLength(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().insert_element_user(RestrictionTypeImpl.MINLENGTH$24, i);
            return target;
        }
    }
    
    @Override
    public NumFacet addNewMinLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().add_element_user(RestrictionTypeImpl.MINLENGTH$24);
            return target;
        }
    }
    
    @Override
    public void removeMinLength(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.MINLENGTH$24, i);
        }
    }
    
    @Override
    public NumFacet[] getMaxLengthArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.MAXLENGTH$26, targetList);
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
            target = (NumFacet)this.get_store().find_element_user(RestrictionTypeImpl.MAXLENGTH$26, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.MAXLENGTH$26);
        }
    }
    
    @Override
    public void setMaxLengthArray(final NumFacet[] maxLengthArray) {
        this.check_orphaned();
        this.arraySetterHelper(maxLengthArray, RestrictionTypeImpl.MAXLENGTH$26);
    }
    
    @Override
    public void setMaxLengthArray(final int i, final NumFacet maxLength) {
        this.generatedSetterHelperImpl(maxLength, RestrictionTypeImpl.MAXLENGTH$26, i, (short)2);
    }
    
    @Override
    public NumFacet insertNewMaxLength(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().insert_element_user(RestrictionTypeImpl.MAXLENGTH$26, i);
            return target;
        }
    }
    
    @Override
    public NumFacet addNewMaxLength() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NumFacet target = null;
            target = (NumFacet)this.get_store().add_element_user(RestrictionTypeImpl.MAXLENGTH$26);
            return target;
        }
    }
    
    @Override
    public void removeMaxLength(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.MAXLENGTH$26, i);
        }
    }
    
    @Override
    public NoFixedFacet[] getEnumerationArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.ENUMERATION$28, targetList);
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
            target = (NoFixedFacet)this.get_store().find_element_user(RestrictionTypeImpl.ENUMERATION$28, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.ENUMERATION$28);
        }
    }
    
    @Override
    public void setEnumerationArray(final NoFixedFacet[] enumerationArray) {
        this.check_orphaned();
        this.arraySetterHelper(enumerationArray, RestrictionTypeImpl.ENUMERATION$28);
    }
    
    @Override
    public void setEnumerationArray(final int i, final NoFixedFacet enumeration) {
        this.generatedSetterHelperImpl(enumeration, RestrictionTypeImpl.ENUMERATION$28, i, (short)2);
    }
    
    @Override
    public NoFixedFacet insertNewEnumeration(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NoFixedFacet target = null;
            target = (NoFixedFacet)this.get_store().insert_element_user(RestrictionTypeImpl.ENUMERATION$28, i);
            return target;
        }
    }
    
    @Override
    public NoFixedFacet addNewEnumeration() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            NoFixedFacet target = null;
            target = (NoFixedFacet)this.get_store().add_element_user(RestrictionTypeImpl.ENUMERATION$28);
            return target;
        }
    }
    
    @Override
    public void removeEnumeration(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.ENUMERATION$28, i);
        }
    }
    
    @Override
    public WhiteSpaceDocument.WhiteSpace[] getWhiteSpaceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.WHITESPACE$30, targetList);
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
            target = (WhiteSpaceDocument.WhiteSpace)this.get_store().find_element_user(RestrictionTypeImpl.WHITESPACE$30, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.WHITESPACE$30);
        }
    }
    
    @Override
    public void setWhiteSpaceArray(final WhiteSpaceDocument.WhiteSpace[] whiteSpaceArray) {
        this.check_orphaned();
        this.arraySetterHelper(whiteSpaceArray, RestrictionTypeImpl.WHITESPACE$30);
    }
    
    @Override
    public void setWhiteSpaceArray(final int i, final WhiteSpaceDocument.WhiteSpace whiteSpace) {
        this.generatedSetterHelperImpl(whiteSpace, RestrictionTypeImpl.WHITESPACE$30, i, (short)2);
    }
    
    @Override
    public WhiteSpaceDocument.WhiteSpace insertNewWhiteSpace(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            WhiteSpaceDocument.WhiteSpace target = null;
            target = (WhiteSpaceDocument.WhiteSpace)this.get_store().insert_element_user(RestrictionTypeImpl.WHITESPACE$30, i);
            return target;
        }
    }
    
    @Override
    public WhiteSpaceDocument.WhiteSpace addNewWhiteSpace() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            WhiteSpaceDocument.WhiteSpace target = null;
            target = (WhiteSpaceDocument.WhiteSpace)this.get_store().add_element_user(RestrictionTypeImpl.WHITESPACE$30);
            return target;
        }
    }
    
    @Override
    public void removeWhiteSpace(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.WHITESPACE$30, i);
        }
    }
    
    @Override
    public PatternDocument.Pattern[] getPatternArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.PATTERN$32, targetList);
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
            target = (PatternDocument.Pattern)this.get_store().find_element_user(RestrictionTypeImpl.PATTERN$32, i);
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
            return this.get_store().count_elements(RestrictionTypeImpl.PATTERN$32);
        }
    }
    
    @Override
    public void setPatternArray(final PatternDocument.Pattern[] patternArray) {
        this.check_orphaned();
        this.arraySetterHelper(patternArray, RestrictionTypeImpl.PATTERN$32);
    }
    
    @Override
    public void setPatternArray(final int i, final PatternDocument.Pattern pattern) {
        this.generatedSetterHelperImpl(pattern, RestrictionTypeImpl.PATTERN$32, i, (short)2);
    }
    
    @Override
    public PatternDocument.Pattern insertNewPattern(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            PatternDocument.Pattern target = null;
            target = (PatternDocument.Pattern)this.get_store().insert_element_user(RestrictionTypeImpl.PATTERN$32, i);
            return target;
        }
    }
    
    @Override
    public PatternDocument.Pattern addNewPattern() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            PatternDocument.Pattern target = null;
            target = (PatternDocument.Pattern)this.get_store().add_element_user(RestrictionTypeImpl.PATTERN$32);
            return target;
        }
    }
    
    @Override
    public void removePattern(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.PATTERN$32, i);
        }
    }
    
    @Override
    public Attribute[] getAttributeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.ATTRIBUTE$34, targetList);
            final Attribute[] result = new Attribute[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public Attribute getAttributeArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().find_element_user(RestrictionTypeImpl.ATTRIBUTE$34, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfAttributeArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RestrictionTypeImpl.ATTRIBUTE$34);
        }
    }
    
    @Override
    public void setAttributeArray(final Attribute[] attributeArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeArray, RestrictionTypeImpl.ATTRIBUTE$34);
    }
    
    @Override
    public void setAttributeArray(final int i, final Attribute attribute) {
        this.generatedSetterHelperImpl(attribute, RestrictionTypeImpl.ATTRIBUTE$34, i, (short)2);
    }
    
    @Override
    public Attribute insertNewAttribute(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().insert_element_user(RestrictionTypeImpl.ATTRIBUTE$34, i);
            return target;
        }
    }
    
    @Override
    public Attribute addNewAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Attribute target = null;
            target = (Attribute)this.get_store().add_element_user(RestrictionTypeImpl.ATTRIBUTE$34);
            return target;
        }
    }
    
    @Override
    public void removeAttribute(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.ATTRIBUTE$34, i);
        }
    }
    
    @Override
    public AttributeGroupRef[] getAttributeGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RestrictionTypeImpl.ATTRIBUTEGROUP$36, targetList);
            final AttributeGroupRef[] result = new AttributeGroupRef[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public AttributeGroupRef getAttributeGroupArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().find_element_user(RestrictionTypeImpl.ATTRIBUTEGROUP$36, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfAttributeGroupArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RestrictionTypeImpl.ATTRIBUTEGROUP$36);
        }
    }
    
    @Override
    public void setAttributeGroupArray(final AttributeGroupRef[] attributeGroupArray) {
        this.check_orphaned();
        this.arraySetterHelper(attributeGroupArray, RestrictionTypeImpl.ATTRIBUTEGROUP$36);
    }
    
    @Override
    public void setAttributeGroupArray(final int i, final AttributeGroupRef attributeGroup) {
        this.generatedSetterHelperImpl(attributeGroup, RestrictionTypeImpl.ATTRIBUTEGROUP$36, i, (short)2);
    }
    
    @Override
    public AttributeGroupRef insertNewAttributeGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().insert_element_user(RestrictionTypeImpl.ATTRIBUTEGROUP$36, i);
            return target;
        }
    }
    
    @Override
    public AttributeGroupRef addNewAttributeGroup() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            AttributeGroupRef target = null;
            target = (AttributeGroupRef)this.get_store().add_element_user(RestrictionTypeImpl.ATTRIBUTEGROUP$36);
            return target;
        }
    }
    
    @Override
    public void removeAttributeGroup(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.ATTRIBUTEGROUP$36, i);
        }
    }
    
    @Override
    public Wildcard getAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().find_element_user(RestrictionTypeImpl.ANYATTRIBUTE$38, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public boolean isSetAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RestrictionTypeImpl.ANYATTRIBUTE$38) != 0;
        }
    }
    
    @Override
    public void setAnyAttribute(final Wildcard anyAttribute) {
        this.generatedSetterHelperImpl(anyAttribute, RestrictionTypeImpl.ANYATTRIBUTE$38, 0, (short)1);
    }
    
    @Override
    public Wildcard addNewAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Wildcard target = null;
            target = (Wildcard)this.get_store().add_element_user(RestrictionTypeImpl.ANYATTRIBUTE$38);
            return target;
        }
    }
    
    @Override
    public void unsetAnyAttribute() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RestrictionTypeImpl.ANYATTRIBUTE$38, 0);
        }
    }
    
    @Override
    public QName getBase() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(RestrictionTypeImpl.BASE$40);
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
            target = (XmlQName)this.get_store().find_attribute_user(RestrictionTypeImpl.BASE$40);
            return target;
        }
    }
    
    @Override
    public void setBase(final QName base) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            SimpleValue target = null;
            target = (SimpleValue)this.get_store().find_attribute_user(RestrictionTypeImpl.BASE$40);
            if (target == null) {
                target = (SimpleValue)this.get_store().add_attribute_user(RestrictionTypeImpl.BASE$40);
            }
            target.setQNameValue(base);
        }
    }
    
    @Override
    public void xsetBase(final XmlQName base) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            XmlQName target = null;
            target = (XmlQName)this.get_store().find_attribute_user(RestrictionTypeImpl.BASE$40);
            if (target == null) {
                target = (XmlQName)this.get_store().add_attribute_user(RestrictionTypeImpl.BASE$40);
            }
            target.set(base);
        }
    }
    
    static {
        GROUP$0 = new QName("http://www.w3.org/2001/XMLSchema", "group");
        ALL$2 = new QName("http://www.w3.org/2001/XMLSchema", "all");
        CHOICE$4 = new QName("http://www.w3.org/2001/XMLSchema", "choice");
        SEQUENCE$6 = new QName("http://www.w3.org/2001/XMLSchema", "sequence");
        SIMPLETYPE$8 = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
        MINEXCLUSIVE$10 = new QName("http://www.w3.org/2001/XMLSchema", "minExclusive");
        MININCLUSIVE$12 = new QName("http://www.w3.org/2001/XMLSchema", "minInclusive");
        MAXEXCLUSIVE$14 = new QName("http://www.w3.org/2001/XMLSchema", "maxExclusive");
        MAXINCLUSIVE$16 = new QName("http://www.w3.org/2001/XMLSchema", "maxInclusive");
        TOTALDIGITS$18 = new QName("http://www.w3.org/2001/XMLSchema", "totalDigits");
        FRACTIONDIGITS$20 = new QName("http://www.w3.org/2001/XMLSchema", "fractionDigits");
        LENGTH$22 = new QName("http://www.w3.org/2001/XMLSchema", "length");
        MINLENGTH$24 = new QName("http://www.w3.org/2001/XMLSchema", "minLength");
        MAXLENGTH$26 = new QName("http://www.w3.org/2001/XMLSchema", "maxLength");
        ENUMERATION$28 = new QName("http://www.w3.org/2001/XMLSchema", "enumeration");
        WHITESPACE$30 = new QName("http://www.w3.org/2001/XMLSchema", "whiteSpace");
        PATTERN$32 = new QName("http://www.w3.org/2001/XMLSchema", "pattern");
        ATTRIBUTE$34 = new QName("http://www.w3.org/2001/XMLSchema", "attribute");
        ATTRIBUTEGROUP$36 = new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup");
        ANYATTRIBUTE$38 = new QName("http://www.w3.org/2001/XMLSchema", "anyAttribute");
        BASE$40 = new QName("", "base");
    }
}
