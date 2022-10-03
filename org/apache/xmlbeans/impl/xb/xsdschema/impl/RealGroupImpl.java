package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.xb.xsdschema.ExplicitGroup;
import org.apache.xmlbeans.XmlObject;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.All;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.RealGroup;

public class RealGroupImpl extends GroupImpl implements RealGroup
{
    private static final long serialVersionUID = 1L;
    private static final QName ALL$0;
    private static final QName CHOICE$2;
    private static final QName SEQUENCE$4;
    
    public RealGroupImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public All[] getAllArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RealGroupImpl.ALL$0, targetList);
            final All[] result = new All[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public All getAllArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().find_element_user(RealGroupImpl.ALL$0, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfAllArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RealGroupImpl.ALL$0);
        }
    }
    
    @Override
    public void setAllArray(final All[] allArray) {
        this.check_orphaned();
        this.arraySetterHelper(allArray, RealGroupImpl.ALL$0);
    }
    
    @Override
    public void setAllArray(final int i, final All all) {
        this.generatedSetterHelperImpl(all, RealGroupImpl.ALL$0, i, (short)2);
    }
    
    @Override
    public All insertNewAll(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().insert_element_user(RealGroupImpl.ALL$0, i);
            return target;
        }
    }
    
    @Override
    public All addNewAll() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            All target = null;
            target = (All)this.get_store().add_element_user(RealGroupImpl.ALL$0);
            return target;
        }
    }
    
    @Override
    public void removeAll(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RealGroupImpl.ALL$0, i);
        }
    }
    
    @Override
    public ExplicitGroup[] getChoiceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RealGroupImpl.CHOICE$2, targetList);
            final ExplicitGroup[] result = new ExplicitGroup[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public ExplicitGroup getChoiceArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(RealGroupImpl.CHOICE$2, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfChoiceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RealGroupImpl.CHOICE$2);
        }
    }
    
    @Override
    public void setChoiceArray(final ExplicitGroup[] choiceArray) {
        this.check_orphaned();
        this.arraySetterHelper(choiceArray, RealGroupImpl.CHOICE$2);
    }
    
    @Override
    public void setChoiceArray(final int i, final ExplicitGroup choice) {
        this.generatedSetterHelperImpl(choice, RealGroupImpl.CHOICE$2, i, (short)2);
    }
    
    @Override
    public ExplicitGroup insertNewChoice(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().insert_element_user(RealGroupImpl.CHOICE$2, i);
            return target;
        }
    }
    
    @Override
    public ExplicitGroup addNewChoice() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(RealGroupImpl.CHOICE$2);
            return target;
        }
    }
    
    @Override
    public void removeChoice(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RealGroupImpl.CHOICE$2, i);
        }
    }
    
    @Override
    public ExplicitGroup[] getSequenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            final List targetList = new ArrayList();
            this.get_store().find_all_element_users(RealGroupImpl.SEQUENCE$4, targetList);
            final ExplicitGroup[] result = new ExplicitGroup[targetList.size()];
            targetList.toArray(result);
            return result;
        }
    }
    
    @Override
    public ExplicitGroup getSequenceArray(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().find_element_user(RealGroupImpl.SEQUENCE$4, i);
            if (target == null) {
                throw new IndexOutOfBoundsException();
            }
            return target;
        }
    }
    
    @Override
    public int sizeOfSequenceArray() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            return this.get_store().count_elements(RealGroupImpl.SEQUENCE$4);
        }
    }
    
    @Override
    public void setSequenceArray(final ExplicitGroup[] sequenceArray) {
        this.check_orphaned();
        this.arraySetterHelper(sequenceArray, RealGroupImpl.SEQUENCE$4);
    }
    
    @Override
    public void setSequenceArray(final int i, final ExplicitGroup sequence) {
        this.generatedSetterHelperImpl(sequence, RealGroupImpl.SEQUENCE$4, i, (short)2);
    }
    
    @Override
    public ExplicitGroup insertNewSequence(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().insert_element_user(RealGroupImpl.SEQUENCE$4, i);
            return target;
        }
    }
    
    @Override
    public ExplicitGroup addNewSequence() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            ExplicitGroup target = null;
            target = (ExplicitGroup)this.get_store().add_element_user(RealGroupImpl.SEQUENCE$4);
            return target;
        }
    }
    
    @Override
    public void removeSequence(final int i) {
        synchronized (this.monitor()) {
            this.check_orphaned();
            this.get_store().remove_element(RealGroupImpl.SEQUENCE$4, i);
        }
    }
    
    static {
        ALL$0 = new QName("http://www.w3.org/2001/XMLSchema", "all");
        CHOICE$2 = new QName("http://www.w3.org/2001/XMLSchema", "choice");
        SEQUENCE$4 = new QName("http://www.w3.org/2001/XMLSchema", "sequence");
    }
}
