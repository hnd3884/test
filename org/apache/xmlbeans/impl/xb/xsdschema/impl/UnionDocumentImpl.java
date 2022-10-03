package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.impl.values.XmlListImpl;
import org.apache.xmlbeans.impl.xb.xsdschema.UnionDocument.Union;
import org.apache.xmlbeans.SimpleValue;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.LocalSimpleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.UnionDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class UnionDocumentImpl extends XmlComplexContentImpl implements UnionDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName UNION$0;
    
    public UnionDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Union getUnion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Union target = null;
            target = (Union)this.get_store().find_element_user(UnionDocumentImpl.UNION$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setUnion(final Union union) {
        this.generatedSetterHelperImpl(union, UnionDocumentImpl.UNION$0, 0, (short)1);
    }
    
    @Override
    public Union addNewUnion() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Union target = null;
            target = (Union)this.get_store().add_element_user(UnionDocumentImpl.UNION$0);
            return target;
        }
    }
    
    static {
        UNION$0 = new QName("http://www.w3.org/2001/XMLSchema", "union");
    }
    
    public static class UnionImpl extends AnnotatedImpl implements Union
    {
        private static final long serialVersionUID = 1L;
        private static final QName SIMPLETYPE$0;
        private static final QName MEMBERTYPES$2;
        
        public UnionImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public LocalSimpleType[] getSimpleTypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(UnionImpl.SIMPLETYPE$0, targetList);
                final LocalSimpleType[] result = new LocalSimpleType[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public LocalSimpleType getSimpleTypeArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)this.get_store().find_element_user(UnionImpl.SIMPLETYPE$0, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfSimpleTypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(UnionImpl.SIMPLETYPE$0);
            }
        }
        
        @Override
        public void setSimpleTypeArray(final LocalSimpleType[] simpleTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(simpleTypeArray, UnionImpl.SIMPLETYPE$0);
        }
        
        @Override
        public void setSimpleTypeArray(final int i, final LocalSimpleType simpleType) {
            this.generatedSetterHelperImpl(simpleType, UnionImpl.SIMPLETYPE$0, i, (short)2);
        }
        
        @Override
        public LocalSimpleType insertNewSimpleType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)this.get_store().insert_element_user(UnionImpl.SIMPLETYPE$0, i);
                return target;
            }
        }
        
        @Override
        public LocalSimpleType addNewSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                LocalSimpleType target = null;
                target = (LocalSimpleType)this.get_store().add_element_user(UnionImpl.SIMPLETYPE$0);
                return target;
            }
        }
        
        @Override
        public void removeSimpleType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(UnionImpl.SIMPLETYPE$0, i);
            }
        }
        
        @Override
        public List getMemberTypes() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(UnionImpl.MEMBERTYPES$2);
                if (target == null) {
                    return null;
                }
                return target.getListValue();
            }
        }
        
        @Override
        public MemberTypes xgetMemberTypes() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                MemberTypes target = null;
                target = (MemberTypes)this.get_store().find_attribute_user(UnionImpl.MEMBERTYPES$2);
                return target;
            }
        }
        
        @Override
        public boolean isSetMemberTypes() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(UnionImpl.MEMBERTYPES$2) != null;
            }
        }
        
        @Override
        public void setMemberTypes(final List memberTypes) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(UnionImpl.MEMBERTYPES$2);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(UnionImpl.MEMBERTYPES$2);
                }
                target.setListValue(memberTypes);
            }
        }
        
        @Override
        public void xsetMemberTypes(final MemberTypes memberTypes) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                MemberTypes target = null;
                target = (MemberTypes)this.get_store().find_attribute_user(UnionImpl.MEMBERTYPES$2);
                if (target == null) {
                    target = (MemberTypes)this.get_store().add_attribute_user(UnionImpl.MEMBERTYPES$2);
                }
                target.set(memberTypes);
            }
        }
        
        @Override
        public void unsetMemberTypes() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(UnionImpl.MEMBERTYPES$2);
            }
        }
        
        static {
            SIMPLETYPE$0 = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
            MEMBERTYPES$2 = new QName("", "memberTypes");
        }
        
        public static class MemberTypesImpl extends XmlListImpl implements MemberTypes
        {
            private static final long serialVersionUID = 1L;
            
            public MemberTypesImpl(final SchemaType sType) {
                super(sType, false);
            }
            
            protected MemberTypesImpl(final SchemaType sType, final boolean b) {
                super(sType, b);
            }
        }
    }
}
