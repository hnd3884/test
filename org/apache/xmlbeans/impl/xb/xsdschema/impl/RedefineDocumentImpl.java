package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.RedefineDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class RedefineDocumentImpl extends XmlComplexContentImpl implements RedefineDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName REDEFINE$0;
    
    public RedefineDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Redefine getRedefine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Redefine target = null;
            target = (Redefine)this.get_store().find_element_user(RedefineDocumentImpl.REDEFINE$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setRedefine(final Redefine redefine) {
        this.generatedSetterHelperImpl(redefine, RedefineDocumentImpl.REDEFINE$0, 0, (short)1);
    }
    
    @Override
    public Redefine addNewRedefine() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Redefine target = null;
            target = (Redefine)this.get_store().add_element_user(RedefineDocumentImpl.REDEFINE$0);
            return target;
        }
    }
    
    static {
        REDEFINE$0 = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
    }
    
    public static class RedefineImpl extends OpenAttrsImpl implements Redefine
    {
        private static final long serialVersionUID = 1L;
        private static final QName ANNOTATION$0;
        private static final QName SIMPLETYPE$2;
        private static final QName COMPLEXTYPE$4;
        private static final QName GROUP$6;
        private static final QName ATTRIBUTEGROUP$8;
        private static final QName SCHEMALOCATION$10;
        private static final QName ID$12;
        
        public RedefineImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public AnnotationDocument.Annotation[] getAnnotationArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RedefineImpl.ANNOTATION$0, targetList);
                final AnnotationDocument.Annotation[] result = new AnnotationDocument.Annotation[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public AnnotationDocument.Annotation getAnnotationArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AnnotationDocument.Annotation target = null;
                target = (AnnotationDocument.Annotation)this.get_store().find_element_user(RedefineImpl.ANNOTATION$0, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfAnnotationArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RedefineImpl.ANNOTATION$0);
            }
        }
        
        @Override
        public void setAnnotationArray(final AnnotationDocument.Annotation[] annotationArray) {
            this.check_orphaned();
            this.arraySetterHelper(annotationArray, RedefineImpl.ANNOTATION$0);
        }
        
        @Override
        public void setAnnotationArray(final int i, final AnnotationDocument.Annotation annotation) {
            this.generatedSetterHelperImpl(annotation, RedefineImpl.ANNOTATION$0, i, (short)2);
        }
        
        @Override
        public AnnotationDocument.Annotation insertNewAnnotation(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AnnotationDocument.Annotation target = null;
                target = (AnnotationDocument.Annotation)this.get_store().insert_element_user(RedefineImpl.ANNOTATION$0, i);
                return target;
            }
        }
        
        @Override
        public AnnotationDocument.Annotation addNewAnnotation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AnnotationDocument.Annotation target = null;
                target = (AnnotationDocument.Annotation)this.get_store().add_element_user(RedefineImpl.ANNOTATION$0);
                return target;
            }
        }
        
        @Override
        public void removeAnnotation(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RedefineImpl.ANNOTATION$0, i);
            }
        }
        
        @Override
        public TopLevelSimpleType[] getSimpleTypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RedefineImpl.SIMPLETYPE$2, targetList);
                final TopLevelSimpleType[] result = new TopLevelSimpleType[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public TopLevelSimpleType getSimpleTypeArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelSimpleType target = null;
                target = (TopLevelSimpleType)this.get_store().find_element_user(RedefineImpl.SIMPLETYPE$2, i);
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
                return this.get_store().count_elements(RedefineImpl.SIMPLETYPE$2);
            }
        }
        
        @Override
        public void setSimpleTypeArray(final TopLevelSimpleType[] simpleTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(simpleTypeArray, RedefineImpl.SIMPLETYPE$2);
        }
        
        @Override
        public void setSimpleTypeArray(final int i, final TopLevelSimpleType simpleType) {
            this.generatedSetterHelperImpl(simpleType, RedefineImpl.SIMPLETYPE$2, i, (short)2);
        }
        
        @Override
        public TopLevelSimpleType insertNewSimpleType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelSimpleType target = null;
                target = (TopLevelSimpleType)this.get_store().insert_element_user(RedefineImpl.SIMPLETYPE$2, i);
                return target;
            }
        }
        
        @Override
        public TopLevelSimpleType addNewSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelSimpleType target = null;
                target = (TopLevelSimpleType)this.get_store().add_element_user(RedefineImpl.SIMPLETYPE$2);
                return target;
            }
        }
        
        @Override
        public void removeSimpleType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RedefineImpl.SIMPLETYPE$2, i);
            }
        }
        
        @Override
        public TopLevelComplexType[] getComplexTypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RedefineImpl.COMPLEXTYPE$4, targetList);
                final TopLevelComplexType[] result = new TopLevelComplexType[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public TopLevelComplexType getComplexTypeArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelComplexType target = null;
                target = (TopLevelComplexType)this.get_store().find_element_user(RedefineImpl.COMPLEXTYPE$4, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfComplexTypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RedefineImpl.COMPLEXTYPE$4);
            }
        }
        
        @Override
        public void setComplexTypeArray(final TopLevelComplexType[] complexTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(complexTypeArray, RedefineImpl.COMPLEXTYPE$4);
        }
        
        @Override
        public void setComplexTypeArray(final int i, final TopLevelComplexType complexType) {
            this.generatedSetterHelperImpl(complexType, RedefineImpl.COMPLEXTYPE$4, i, (short)2);
        }
        
        @Override
        public TopLevelComplexType insertNewComplexType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelComplexType target = null;
                target = (TopLevelComplexType)this.get_store().insert_element_user(RedefineImpl.COMPLEXTYPE$4, i);
                return target;
            }
        }
        
        @Override
        public TopLevelComplexType addNewComplexType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelComplexType target = null;
                target = (TopLevelComplexType)this.get_store().add_element_user(RedefineImpl.COMPLEXTYPE$4);
                return target;
            }
        }
        
        @Override
        public void removeComplexType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RedefineImpl.COMPLEXTYPE$4, i);
            }
        }
        
        @Override
        public NamedGroup[] getGroupArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RedefineImpl.GROUP$6, targetList);
                final NamedGroup[] result = new NamedGroup[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public NamedGroup getGroupArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedGroup target = null;
                target = (NamedGroup)this.get_store().find_element_user(RedefineImpl.GROUP$6, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfGroupArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(RedefineImpl.GROUP$6);
            }
        }
        
        @Override
        public void setGroupArray(final NamedGroup[] groupArray) {
            this.check_orphaned();
            this.arraySetterHelper(groupArray, RedefineImpl.GROUP$6);
        }
        
        @Override
        public void setGroupArray(final int i, final NamedGroup group) {
            this.generatedSetterHelperImpl(group, RedefineImpl.GROUP$6, i, (short)2);
        }
        
        @Override
        public NamedGroup insertNewGroup(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedGroup target = null;
                target = (NamedGroup)this.get_store().insert_element_user(RedefineImpl.GROUP$6, i);
                return target;
            }
        }
        
        @Override
        public NamedGroup addNewGroup() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedGroup target = null;
                target = (NamedGroup)this.get_store().add_element_user(RedefineImpl.GROUP$6);
                return target;
            }
        }
        
        @Override
        public void removeGroup(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RedefineImpl.GROUP$6, i);
            }
        }
        
        @Override
        public NamedAttributeGroup[] getAttributeGroupArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(RedefineImpl.ATTRIBUTEGROUP$8, targetList);
                final NamedAttributeGroup[] result = new NamedAttributeGroup[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public NamedAttributeGroup getAttributeGroupArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedAttributeGroup target = null;
                target = (NamedAttributeGroup)this.get_store().find_element_user(RedefineImpl.ATTRIBUTEGROUP$8, i);
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
                return this.get_store().count_elements(RedefineImpl.ATTRIBUTEGROUP$8);
            }
        }
        
        @Override
        public void setAttributeGroupArray(final NamedAttributeGroup[] attributeGroupArray) {
            this.check_orphaned();
            this.arraySetterHelper(attributeGroupArray, RedefineImpl.ATTRIBUTEGROUP$8);
        }
        
        @Override
        public void setAttributeGroupArray(final int i, final NamedAttributeGroup attributeGroup) {
            this.generatedSetterHelperImpl(attributeGroup, RedefineImpl.ATTRIBUTEGROUP$8, i, (short)2);
        }
        
        @Override
        public NamedAttributeGroup insertNewAttributeGroup(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedAttributeGroup target = null;
                target = (NamedAttributeGroup)this.get_store().insert_element_user(RedefineImpl.ATTRIBUTEGROUP$8, i);
                return target;
            }
        }
        
        @Override
        public NamedAttributeGroup addNewAttributeGroup() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedAttributeGroup target = null;
                target = (NamedAttributeGroup)this.get_store().add_element_user(RedefineImpl.ATTRIBUTEGROUP$8);
                return target;
            }
        }
        
        @Override
        public void removeAttributeGroup(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(RedefineImpl.ATTRIBUTEGROUP$8, i);
            }
        }
        
        @Override
        public String getSchemaLocation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(RedefineImpl.SCHEMALOCATION$10);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlAnyURI xgetSchemaLocation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(RedefineImpl.SCHEMALOCATION$10);
                return target;
            }
        }
        
        @Override
        public void setSchemaLocation(final String schemaLocation) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(RedefineImpl.SCHEMALOCATION$10);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(RedefineImpl.SCHEMALOCATION$10);
                }
                target.setStringValue(schemaLocation);
            }
        }
        
        @Override
        public void xsetSchemaLocation(final XmlAnyURI schemaLocation) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(RedefineImpl.SCHEMALOCATION$10);
                if (target == null) {
                    target = (XmlAnyURI)this.get_store().add_attribute_user(RedefineImpl.SCHEMALOCATION$10);
                }
                target.set(schemaLocation);
            }
        }
        
        @Override
        public String getId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(RedefineImpl.ID$12);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlID xgetId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlID target = null;
                target = (XmlID)this.get_store().find_attribute_user(RedefineImpl.ID$12);
                return target;
            }
        }
        
        @Override
        public boolean isSetId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(RedefineImpl.ID$12) != null;
            }
        }
        
        @Override
        public void setId(final String id) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(RedefineImpl.ID$12);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(RedefineImpl.ID$12);
                }
                target.setStringValue(id);
            }
        }
        
        @Override
        public void xsetId(final XmlID id) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlID target = null;
                target = (XmlID)this.get_store().find_attribute_user(RedefineImpl.ID$12);
                if (target == null) {
                    target = (XmlID)this.get_store().add_attribute_user(RedefineImpl.ID$12);
                }
                target.set(id);
            }
        }
        
        @Override
        public void unsetId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(RedefineImpl.ID$12);
            }
        }
        
        static {
            ANNOTATION$0 = new QName("http://www.w3.org/2001/XMLSchema", "annotation");
            SIMPLETYPE$2 = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
            COMPLEXTYPE$4 = new QName("http://www.w3.org/2001/XMLSchema", "complexType");
            GROUP$6 = new QName("http://www.w3.org/2001/XMLSchema", "group");
            ATTRIBUTEGROUP$8 = new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup");
            SCHEMALOCATION$10 = new QName("", "schemaLocation");
            ID$12 = new QName("", "id");
        }
    }
}
