package org.apache.xmlbeans.impl.xb.xsdschema.impl;

import org.apache.xmlbeans.XmlLanguage;
import org.apache.xmlbeans.XmlID;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import org.apache.xmlbeans.impl.xb.xsdschema.BlockSet;
import org.apache.xmlbeans.impl.xb.xsdschema.FullDerivationSet;
import org.apache.xmlbeans.XmlToken;
import org.apache.xmlbeans.XmlAnyURI;
import org.apache.xmlbeans.SimpleValue;
import org.apache.xmlbeans.impl.xb.xsdschema.NotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.AnnotationDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.RedefineDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;
import java.util.List;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;

public class SchemaDocumentImpl extends XmlComplexContentImpl implements SchemaDocument
{
    private static final long serialVersionUID = 1L;
    private static final QName SCHEMA$0;
    
    public SchemaDocumentImpl(final SchemaType sType) {
        super(sType);
    }
    
    @Override
    public Schema getSchema() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Schema target = null;
            target = (Schema)this.get_store().find_element_user(SchemaDocumentImpl.SCHEMA$0, 0);
            if (target == null) {
                return null;
            }
            return target;
        }
    }
    
    @Override
    public void setSchema(final Schema schema) {
        this.generatedSetterHelperImpl(schema, SchemaDocumentImpl.SCHEMA$0, 0, (short)1);
    }
    
    @Override
    public Schema addNewSchema() {
        synchronized (this.monitor()) {
            this.check_orphaned();
            Schema target = null;
            target = (Schema)this.get_store().add_element_user(SchemaDocumentImpl.SCHEMA$0);
            return target;
        }
    }
    
    static {
        SCHEMA$0 = new QName("http://www.w3.org/2001/XMLSchema", "schema");
    }
    
    public static class SchemaImpl extends OpenAttrsImpl implements Schema
    {
        private static final long serialVersionUID = 1L;
        private static final QName INCLUDE$0;
        private static final QName IMPORT$2;
        private static final QName REDEFINE$4;
        private static final QName ANNOTATION$6;
        private static final QName SIMPLETYPE$8;
        private static final QName COMPLEXTYPE$10;
        private static final QName GROUP$12;
        private static final QName ATTRIBUTEGROUP$14;
        private static final QName ELEMENT$16;
        private static final QName ATTRIBUTE$18;
        private static final QName NOTATION$20;
        private static final QName TARGETNAMESPACE$22;
        private static final QName VERSION$24;
        private static final QName FINALDEFAULT$26;
        private static final QName BLOCKDEFAULT$28;
        private static final QName ATTRIBUTEFORMDEFAULT$30;
        private static final QName ELEMENTFORMDEFAULT$32;
        private static final QName ID$34;
        private static final QName LANG$36;
        
        public SchemaImpl(final SchemaType sType) {
            super(sType);
        }
        
        @Override
        public IncludeDocument.Include[] getIncludeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.INCLUDE$0, targetList);
                final IncludeDocument.Include[] result = new IncludeDocument.Include[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public IncludeDocument.Include getIncludeArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                IncludeDocument.Include target = null;
                target = (IncludeDocument.Include)this.get_store().find_element_user(SchemaImpl.INCLUDE$0, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfIncludeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(SchemaImpl.INCLUDE$0);
            }
        }
        
        @Override
        public void setIncludeArray(final IncludeDocument.Include[] includeArray) {
            this.check_orphaned();
            this.arraySetterHelper(includeArray, SchemaImpl.INCLUDE$0);
        }
        
        @Override
        public void setIncludeArray(final int i, final IncludeDocument.Include include) {
            this.generatedSetterHelperImpl(include, SchemaImpl.INCLUDE$0, i, (short)2);
        }
        
        @Override
        public IncludeDocument.Include insertNewInclude(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                IncludeDocument.Include target = null;
                target = (IncludeDocument.Include)this.get_store().insert_element_user(SchemaImpl.INCLUDE$0, i);
                return target;
            }
        }
        
        @Override
        public IncludeDocument.Include addNewInclude() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                IncludeDocument.Include target = null;
                target = (IncludeDocument.Include)this.get_store().add_element_user(SchemaImpl.INCLUDE$0);
                return target;
            }
        }
        
        @Override
        public void removeInclude(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.INCLUDE$0, i);
            }
        }
        
        @Override
        public ImportDocument.Import[] getImportArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.IMPORT$2, targetList);
                final ImportDocument.Import[] result = new ImportDocument.Import[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public ImportDocument.Import getImportArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                ImportDocument.Import target = null;
                target = (ImportDocument.Import)this.get_store().find_element_user(SchemaImpl.IMPORT$2, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfImportArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(SchemaImpl.IMPORT$2);
            }
        }
        
        @Override
        public void setImportArray(final ImportDocument.Import[] ximportArray) {
            this.check_orphaned();
            this.arraySetterHelper(ximportArray, SchemaImpl.IMPORT$2);
        }
        
        @Override
        public void setImportArray(final int i, final ImportDocument.Import ximport) {
            this.generatedSetterHelperImpl(ximport, SchemaImpl.IMPORT$2, i, (short)2);
        }
        
        @Override
        public ImportDocument.Import insertNewImport(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                ImportDocument.Import target = null;
                target = (ImportDocument.Import)this.get_store().insert_element_user(SchemaImpl.IMPORT$2, i);
                return target;
            }
        }
        
        @Override
        public ImportDocument.Import addNewImport() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                ImportDocument.Import target = null;
                target = (ImportDocument.Import)this.get_store().add_element_user(SchemaImpl.IMPORT$2);
                return target;
            }
        }
        
        @Override
        public void removeImport(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.IMPORT$2, i);
            }
        }
        
        @Override
        public RedefineDocument.Redefine[] getRedefineArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.REDEFINE$4, targetList);
                final RedefineDocument.Redefine[] result = new RedefineDocument.Redefine[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public RedefineDocument.Redefine getRedefineArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                RedefineDocument.Redefine target = null;
                target = (RedefineDocument.Redefine)this.get_store().find_element_user(SchemaImpl.REDEFINE$4, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfRedefineArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(SchemaImpl.REDEFINE$4);
            }
        }
        
        @Override
        public void setRedefineArray(final RedefineDocument.Redefine[] redefineArray) {
            this.check_orphaned();
            this.arraySetterHelper(redefineArray, SchemaImpl.REDEFINE$4);
        }
        
        @Override
        public void setRedefineArray(final int i, final RedefineDocument.Redefine redefine) {
            this.generatedSetterHelperImpl(redefine, SchemaImpl.REDEFINE$4, i, (short)2);
        }
        
        @Override
        public RedefineDocument.Redefine insertNewRedefine(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                RedefineDocument.Redefine target = null;
                target = (RedefineDocument.Redefine)this.get_store().insert_element_user(SchemaImpl.REDEFINE$4, i);
                return target;
            }
        }
        
        @Override
        public RedefineDocument.Redefine addNewRedefine() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                RedefineDocument.Redefine target = null;
                target = (RedefineDocument.Redefine)this.get_store().add_element_user(SchemaImpl.REDEFINE$4);
                return target;
            }
        }
        
        @Override
        public void removeRedefine(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.REDEFINE$4, i);
            }
        }
        
        @Override
        public AnnotationDocument.Annotation[] getAnnotationArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.ANNOTATION$6, targetList);
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
                target = (AnnotationDocument.Annotation)this.get_store().find_element_user(SchemaImpl.ANNOTATION$6, i);
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
                return this.get_store().count_elements(SchemaImpl.ANNOTATION$6);
            }
        }
        
        @Override
        public void setAnnotationArray(final AnnotationDocument.Annotation[] annotationArray) {
            this.check_orphaned();
            this.arraySetterHelper(annotationArray, SchemaImpl.ANNOTATION$6);
        }
        
        @Override
        public void setAnnotationArray(final int i, final AnnotationDocument.Annotation annotation) {
            this.generatedSetterHelperImpl(annotation, SchemaImpl.ANNOTATION$6, i, (short)2);
        }
        
        @Override
        public AnnotationDocument.Annotation insertNewAnnotation(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AnnotationDocument.Annotation target = null;
                target = (AnnotationDocument.Annotation)this.get_store().insert_element_user(SchemaImpl.ANNOTATION$6, i);
                return target;
            }
        }
        
        @Override
        public AnnotationDocument.Annotation addNewAnnotation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                AnnotationDocument.Annotation target = null;
                target = (AnnotationDocument.Annotation)this.get_store().add_element_user(SchemaImpl.ANNOTATION$6);
                return target;
            }
        }
        
        @Override
        public void removeAnnotation(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.ANNOTATION$6, i);
            }
        }
        
        @Override
        public TopLevelSimpleType[] getSimpleTypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.SIMPLETYPE$8, targetList);
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
                target = (TopLevelSimpleType)this.get_store().find_element_user(SchemaImpl.SIMPLETYPE$8, i);
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
                return this.get_store().count_elements(SchemaImpl.SIMPLETYPE$8);
            }
        }
        
        @Override
        public void setSimpleTypeArray(final TopLevelSimpleType[] simpleTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(simpleTypeArray, SchemaImpl.SIMPLETYPE$8);
        }
        
        @Override
        public void setSimpleTypeArray(final int i, final TopLevelSimpleType simpleType) {
            this.generatedSetterHelperImpl(simpleType, SchemaImpl.SIMPLETYPE$8, i, (short)2);
        }
        
        @Override
        public TopLevelSimpleType insertNewSimpleType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelSimpleType target = null;
                target = (TopLevelSimpleType)this.get_store().insert_element_user(SchemaImpl.SIMPLETYPE$8, i);
                return target;
            }
        }
        
        @Override
        public TopLevelSimpleType addNewSimpleType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelSimpleType target = null;
                target = (TopLevelSimpleType)this.get_store().add_element_user(SchemaImpl.SIMPLETYPE$8);
                return target;
            }
        }
        
        @Override
        public void removeSimpleType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.SIMPLETYPE$8, i);
            }
        }
        
        @Override
        public TopLevelComplexType[] getComplexTypeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.COMPLEXTYPE$10, targetList);
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
                target = (TopLevelComplexType)this.get_store().find_element_user(SchemaImpl.COMPLEXTYPE$10, i);
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
                return this.get_store().count_elements(SchemaImpl.COMPLEXTYPE$10);
            }
        }
        
        @Override
        public void setComplexTypeArray(final TopLevelComplexType[] complexTypeArray) {
            this.check_orphaned();
            this.arraySetterHelper(complexTypeArray, SchemaImpl.COMPLEXTYPE$10);
        }
        
        @Override
        public void setComplexTypeArray(final int i, final TopLevelComplexType complexType) {
            this.generatedSetterHelperImpl(complexType, SchemaImpl.COMPLEXTYPE$10, i, (short)2);
        }
        
        @Override
        public TopLevelComplexType insertNewComplexType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelComplexType target = null;
                target = (TopLevelComplexType)this.get_store().insert_element_user(SchemaImpl.COMPLEXTYPE$10, i);
                return target;
            }
        }
        
        @Override
        public TopLevelComplexType addNewComplexType() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelComplexType target = null;
                target = (TopLevelComplexType)this.get_store().add_element_user(SchemaImpl.COMPLEXTYPE$10);
                return target;
            }
        }
        
        @Override
        public void removeComplexType(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.COMPLEXTYPE$10, i);
            }
        }
        
        @Override
        public NamedGroup[] getGroupArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.GROUP$12, targetList);
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
                target = (NamedGroup)this.get_store().find_element_user(SchemaImpl.GROUP$12, i);
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
                return this.get_store().count_elements(SchemaImpl.GROUP$12);
            }
        }
        
        @Override
        public void setGroupArray(final NamedGroup[] groupArray) {
            this.check_orphaned();
            this.arraySetterHelper(groupArray, SchemaImpl.GROUP$12);
        }
        
        @Override
        public void setGroupArray(final int i, final NamedGroup group) {
            this.generatedSetterHelperImpl(group, SchemaImpl.GROUP$12, i, (short)2);
        }
        
        @Override
        public NamedGroup insertNewGroup(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedGroup target = null;
                target = (NamedGroup)this.get_store().insert_element_user(SchemaImpl.GROUP$12, i);
                return target;
            }
        }
        
        @Override
        public NamedGroup addNewGroup() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedGroup target = null;
                target = (NamedGroup)this.get_store().add_element_user(SchemaImpl.GROUP$12);
                return target;
            }
        }
        
        @Override
        public void removeGroup(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.GROUP$12, i);
            }
        }
        
        @Override
        public NamedAttributeGroup[] getAttributeGroupArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.ATTRIBUTEGROUP$14, targetList);
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
                target = (NamedAttributeGroup)this.get_store().find_element_user(SchemaImpl.ATTRIBUTEGROUP$14, i);
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
                return this.get_store().count_elements(SchemaImpl.ATTRIBUTEGROUP$14);
            }
        }
        
        @Override
        public void setAttributeGroupArray(final NamedAttributeGroup[] attributeGroupArray) {
            this.check_orphaned();
            this.arraySetterHelper(attributeGroupArray, SchemaImpl.ATTRIBUTEGROUP$14);
        }
        
        @Override
        public void setAttributeGroupArray(final int i, final NamedAttributeGroup attributeGroup) {
            this.generatedSetterHelperImpl(attributeGroup, SchemaImpl.ATTRIBUTEGROUP$14, i, (short)2);
        }
        
        @Override
        public NamedAttributeGroup insertNewAttributeGroup(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedAttributeGroup target = null;
                target = (NamedAttributeGroup)this.get_store().insert_element_user(SchemaImpl.ATTRIBUTEGROUP$14, i);
                return target;
            }
        }
        
        @Override
        public NamedAttributeGroup addNewAttributeGroup() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NamedAttributeGroup target = null;
                target = (NamedAttributeGroup)this.get_store().add_element_user(SchemaImpl.ATTRIBUTEGROUP$14);
                return target;
            }
        }
        
        @Override
        public void removeAttributeGroup(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.ATTRIBUTEGROUP$14, i);
            }
        }
        
        @Override
        public TopLevelElement[] getElementArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.ELEMENT$16, targetList);
                final TopLevelElement[] result = new TopLevelElement[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public TopLevelElement getElementArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelElement target = null;
                target = (TopLevelElement)this.get_store().find_element_user(SchemaImpl.ELEMENT$16, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfElementArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(SchemaImpl.ELEMENT$16);
            }
        }
        
        @Override
        public void setElementArray(final TopLevelElement[] elementArray) {
            this.check_orphaned();
            this.arraySetterHelper(elementArray, SchemaImpl.ELEMENT$16);
        }
        
        @Override
        public void setElementArray(final int i, final TopLevelElement element) {
            this.generatedSetterHelperImpl(element, SchemaImpl.ELEMENT$16, i, (short)2);
        }
        
        @Override
        public TopLevelElement insertNewElement(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelElement target = null;
                target = (TopLevelElement)this.get_store().insert_element_user(SchemaImpl.ELEMENT$16, i);
                return target;
            }
        }
        
        @Override
        public TopLevelElement addNewElement() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelElement target = null;
                target = (TopLevelElement)this.get_store().add_element_user(SchemaImpl.ELEMENT$16);
                return target;
            }
        }
        
        @Override
        public void removeElement(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.ELEMENT$16, i);
            }
        }
        
        @Override
        public TopLevelAttribute[] getAttributeArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.ATTRIBUTE$18, targetList);
                final TopLevelAttribute[] result = new TopLevelAttribute[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public TopLevelAttribute getAttributeArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelAttribute target = null;
                target = (TopLevelAttribute)this.get_store().find_element_user(SchemaImpl.ATTRIBUTE$18, i);
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
                return this.get_store().count_elements(SchemaImpl.ATTRIBUTE$18);
            }
        }
        
        @Override
        public void setAttributeArray(final TopLevelAttribute[] attributeArray) {
            this.check_orphaned();
            this.arraySetterHelper(attributeArray, SchemaImpl.ATTRIBUTE$18);
        }
        
        @Override
        public void setAttributeArray(final int i, final TopLevelAttribute attribute) {
            this.generatedSetterHelperImpl(attribute, SchemaImpl.ATTRIBUTE$18, i, (short)2);
        }
        
        @Override
        public TopLevelAttribute insertNewAttribute(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelAttribute target = null;
                target = (TopLevelAttribute)this.get_store().insert_element_user(SchemaImpl.ATTRIBUTE$18, i);
                return target;
            }
        }
        
        @Override
        public TopLevelAttribute addNewAttribute() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                TopLevelAttribute target = null;
                target = (TopLevelAttribute)this.get_store().add_element_user(SchemaImpl.ATTRIBUTE$18);
                return target;
            }
        }
        
        @Override
        public void removeAttribute(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.ATTRIBUTE$18, i);
            }
        }
        
        @Override
        public NotationDocument.Notation[] getNotationArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                final List targetList = new ArrayList();
                this.get_store().find_all_element_users(SchemaImpl.NOTATION$20, targetList);
                final NotationDocument.Notation[] result = new NotationDocument.Notation[targetList.size()];
                targetList.toArray(result);
                return result;
            }
        }
        
        @Override
        public NotationDocument.Notation getNotationArray(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NotationDocument.Notation target = null;
                target = (NotationDocument.Notation)this.get_store().find_element_user(SchemaImpl.NOTATION$20, i);
                if (target == null) {
                    throw new IndexOutOfBoundsException();
                }
                return target;
            }
        }
        
        @Override
        public int sizeOfNotationArray() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().count_elements(SchemaImpl.NOTATION$20);
            }
        }
        
        @Override
        public void setNotationArray(final NotationDocument.Notation[] notationArray) {
            this.check_orphaned();
            this.arraySetterHelper(notationArray, SchemaImpl.NOTATION$20);
        }
        
        @Override
        public void setNotationArray(final int i, final NotationDocument.Notation notation) {
            this.generatedSetterHelperImpl(notation, SchemaImpl.NOTATION$20, i, (short)2);
        }
        
        @Override
        public NotationDocument.Notation insertNewNotation(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NotationDocument.Notation target = null;
                target = (NotationDocument.Notation)this.get_store().insert_element_user(SchemaImpl.NOTATION$20, i);
                return target;
            }
        }
        
        @Override
        public NotationDocument.Notation addNewNotation() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                NotationDocument.Notation target = null;
                target = (NotationDocument.Notation)this.get_store().add_element_user(SchemaImpl.NOTATION$20);
                return target;
            }
        }
        
        @Override
        public void removeNotation(final int i) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_element(SchemaImpl.NOTATION$20, i);
            }
        }
        
        @Override
        public String getTargetNamespace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.TARGETNAMESPACE$22);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlAnyURI xgetTargetNamespace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(SchemaImpl.TARGETNAMESPACE$22);
                return target;
            }
        }
        
        @Override
        public boolean isSetTargetNamespace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(SchemaImpl.TARGETNAMESPACE$22) != null;
            }
        }
        
        @Override
        public void setTargetNamespace(final String targetNamespace) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.TARGETNAMESPACE$22);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(SchemaImpl.TARGETNAMESPACE$22);
                }
                target.setStringValue(targetNamespace);
            }
        }
        
        @Override
        public void xsetTargetNamespace(final XmlAnyURI targetNamespace) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlAnyURI target = null;
                target = (XmlAnyURI)this.get_store().find_attribute_user(SchemaImpl.TARGETNAMESPACE$22);
                if (target == null) {
                    target = (XmlAnyURI)this.get_store().add_attribute_user(SchemaImpl.TARGETNAMESPACE$22);
                }
                target.set(targetNamespace);
            }
        }
        
        @Override
        public void unsetTargetNamespace() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(SchemaImpl.TARGETNAMESPACE$22);
            }
        }
        
        @Override
        public String getVersion() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.VERSION$24);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlToken xgetVersion() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlToken target = null;
                target = (XmlToken)this.get_store().find_attribute_user(SchemaImpl.VERSION$24);
                return target;
            }
        }
        
        @Override
        public boolean isSetVersion() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(SchemaImpl.VERSION$24) != null;
            }
        }
        
        @Override
        public void setVersion(final String version) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.VERSION$24);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(SchemaImpl.VERSION$24);
                }
                target.setStringValue(version);
            }
        }
        
        @Override
        public void xsetVersion(final XmlToken version) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlToken target = null;
                target = (XmlToken)this.get_store().find_attribute_user(SchemaImpl.VERSION$24);
                if (target == null) {
                    target = (XmlToken)this.get_store().add_attribute_user(SchemaImpl.VERSION$24);
                }
                target.set(version);
            }
        }
        
        @Override
        public void unsetVersion() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(SchemaImpl.VERSION$24);
            }
        }
        
        @Override
        public Object getFinalDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.FINALDEFAULT$26);
                if (target == null) {
                    target = (SimpleValue)this.get_default_attribute_value(SchemaImpl.FINALDEFAULT$26);
                }
                if (target == null) {
                    return null;
                }
                return target.getObjectValue();
            }
        }
        
        @Override
        public FullDerivationSet xgetFinalDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                FullDerivationSet target = null;
                target = (FullDerivationSet)this.get_store().find_attribute_user(SchemaImpl.FINALDEFAULT$26);
                if (target == null) {
                    target = (FullDerivationSet)this.get_default_attribute_value(SchemaImpl.FINALDEFAULT$26);
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetFinalDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(SchemaImpl.FINALDEFAULT$26) != null;
            }
        }
        
        @Override
        public void setFinalDefault(final Object finalDefault) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.FINALDEFAULT$26);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(SchemaImpl.FINALDEFAULT$26);
                }
                target.setObjectValue(finalDefault);
            }
        }
        
        @Override
        public void xsetFinalDefault(final FullDerivationSet finalDefault) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                FullDerivationSet target = null;
                target = (FullDerivationSet)this.get_store().find_attribute_user(SchemaImpl.FINALDEFAULT$26);
                if (target == null) {
                    target = (FullDerivationSet)this.get_store().add_attribute_user(SchemaImpl.FINALDEFAULT$26);
                }
                target.set(finalDefault);
            }
        }
        
        @Override
        public void unsetFinalDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(SchemaImpl.FINALDEFAULT$26);
            }
        }
        
        @Override
        public Object getBlockDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.BLOCKDEFAULT$28);
                if (target == null) {
                    target = (SimpleValue)this.get_default_attribute_value(SchemaImpl.BLOCKDEFAULT$28);
                }
                if (target == null) {
                    return null;
                }
                return target.getObjectValue();
            }
        }
        
        @Override
        public BlockSet xgetBlockDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                BlockSet target = null;
                target = (BlockSet)this.get_store().find_attribute_user(SchemaImpl.BLOCKDEFAULT$28);
                if (target == null) {
                    target = (BlockSet)this.get_default_attribute_value(SchemaImpl.BLOCKDEFAULT$28);
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetBlockDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(SchemaImpl.BLOCKDEFAULT$28) != null;
            }
        }
        
        @Override
        public void setBlockDefault(final Object blockDefault) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.BLOCKDEFAULT$28);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(SchemaImpl.BLOCKDEFAULT$28);
                }
                target.setObjectValue(blockDefault);
            }
        }
        
        @Override
        public void xsetBlockDefault(final BlockSet blockDefault) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                BlockSet target = null;
                target = (BlockSet)this.get_store().find_attribute_user(SchemaImpl.BLOCKDEFAULT$28);
                if (target == null) {
                    target = (BlockSet)this.get_store().add_attribute_user(SchemaImpl.BLOCKDEFAULT$28);
                }
                target.set(blockDefault);
            }
        }
        
        @Override
        public void unsetBlockDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(SchemaImpl.BLOCKDEFAULT$28);
            }
        }
        
        @Override
        public FormChoice.Enum getAttributeFormDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.ATTRIBUTEFORMDEFAULT$30);
                if (target == null) {
                    target = (SimpleValue)this.get_default_attribute_value(SchemaImpl.ATTRIBUTEFORMDEFAULT$30);
                }
                if (target == null) {
                    return null;
                }
                return (FormChoice.Enum)target.getEnumValue();
            }
        }
        
        @Override
        public FormChoice xgetAttributeFormDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                FormChoice target = null;
                target = (FormChoice)this.get_store().find_attribute_user(SchemaImpl.ATTRIBUTEFORMDEFAULT$30);
                if (target == null) {
                    target = (FormChoice)this.get_default_attribute_value(SchemaImpl.ATTRIBUTEFORMDEFAULT$30);
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetAttributeFormDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(SchemaImpl.ATTRIBUTEFORMDEFAULT$30) != null;
            }
        }
        
        @Override
        public void setAttributeFormDefault(final FormChoice.Enum attributeFormDefault) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.ATTRIBUTEFORMDEFAULT$30);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(SchemaImpl.ATTRIBUTEFORMDEFAULT$30);
                }
                target.setEnumValue(attributeFormDefault);
            }
        }
        
        @Override
        public void xsetAttributeFormDefault(final FormChoice attributeFormDefault) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                FormChoice target = null;
                target = (FormChoice)this.get_store().find_attribute_user(SchemaImpl.ATTRIBUTEFORMDEFAULT$30);
                if (target == null) {
                    target = (FormChoice)this.get_store().add_attribute_user(SchemaImpl.ATTRIBUTEFORMDEFAULT$30);
                }
                target.set(attributeFormDefault);
            }
        }
        
        @Override
        public void unsetAttributeFormDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(SchemaImpl.ATTRIBUTEFORMDEFAULT$30);
            }
        }
        
        @Override
        public FormChoice.Enum getElementFormDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.ELEMENTFORMDEFAULT$32);
                if (target == null) {
                    target = (SimpleValue)this.get_default_attribute_value(SchemaImpl.ELEMENTFORMDEFAULT$32);
                }
                if (target == null) {
                    return null;
                }
                return (FormChoice.Enum)target.getEnumValue();
            }
        }
        
        @Override
        public FormChoice xgetElementFormDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                FormChoice target = null;
                target = (FormChoice)this.get_store().find_attribute_user(SchemaImpl.ELEMENTFORMDEFAULT$32);
                if (target == null) {
                    target = (FormChoice)this.get_default_attribute_value(SchemaImpl.ELEMENTFORMDEFAULT$32);
                }
                return target;
            }
        }
        
        @Override
        public boolean isSetElementFormDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(SchemaImpl.ELEMENTFORMDEFAULT$32) != null;
            }
        }
        
        @Override
        public void setElementFormDefault(final FormChoice.Enum elementFormDefault) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.ELEMENTFORMDEFAULT$32);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(SchemaImpl.ELEMENTFORMDEFAULT$32);
                }
                target.setEnumValue(elementFormDefault);
            }
        }
        
        @Override
        public void xsetElementFormDefault(final FormChoice elementFormDefault) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                FormChoice target = null;
                target = (FormChoice)this.get_store().find_attribute_user(SchemaImpl.ELEMENTFORMDEFAULT$32);
                if (target == null) {
                    target = (FormChoice)this.get_store().add_attribute_user(SchemaImpl.ELEMENTFORMDEFAULT$32);
                }
                target.set(elementFormDefault);
            }
        }
        
        @Override
        public void unsetElementFormDefault() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(SchemaImpl.ELEMENTFORMDEFAULT$32);
            }
        }
        
        @Override
        public String getId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.ID$34);
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
                target = (XmlID)this.get_store().find_attribute_user(SchemaImpl.ID$34);
                return target;
            }
        }
        
        @Override
        public boolean isSetId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(SchemaImpl.ID$34) != null;
            }
        }
        
        @Override
        public void setId(final String id) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.ID$34);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(SchemaImpl.ID$34);
                }
                target.setStringValue(id);
            }
        }
        
        @Override
        public void xsetId(final XmlID id) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlID target = null;
                target = (XmlID)this.get_store().find_attribute_user(SchemaImpl.ID$34);
                if (target == null) {
                    target = (XmlID)this.get_store().add_attribute_user(SchemaImpl.ID$34);
                }
                target.set(id);
            }
        }
        
        @Override
        public void unsetId() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(SchemaImpl.ID$34);
            }
        }
        
        @Override
        public String getLang() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.LANG$36);
                if (target == null) {
                    return null;
                }
                return target.getStringValue();
            }
        }
        
        @Override
        public XmlLanguage xgetLang() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlLanguage target = null;
                target = (XmlLanguage)this.get_store().find_attribute_user(SchemaImpl.LANG$36);
                return target;
            }
        }
        
        @Override
        public boolean isSetLang() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                return this.get_store().find_attribute_user(SchemaImpl.LANG$36) != null;
            }
        }
        
        @Override
        public void setLang(final String lang) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                SimpleValue target = null;
                target = (SimpleValue)this.get_store().find_attribute_user(SchemaImpl.LANG$36);
                if (target == null) {
                    target = (SimpleValue)this.get_store().add_attribute_user(SchemaImpl.LANG$36);
                }
                target.setStringValue(lang);
            }
        }
        
        @Override
        public void xsetLang(final XmlLanguage lang) {
            synchronized (this.monitor()) {
                this.check_orphaned();
                XmlLanguage target = null;
                target = (XmlLanguage)this.get_store().find_attribute_user(SchemaImpl.LANG$36);
                if (target == null) {
                    target = (XmlLanguage)this.get_store().add_attribute_user(SchemaImpl.LANG$36);
                }
                target.set(lang);
            }
        }
        
        @Override
        public void unsetLang() {
            synchronized (this.monitor()) {
                this.check_orphaned();
                this.get_store().remove_attribute(SchemaImpl.LANG$36);
            }
        }
        
        static {
            INCLUDE$0 = new QName("http://www.w3.org/2001/XMLSchema", "include");
            IMPORT$2 = new QName("http://www.w3.org/2001/XMLSchema", "import");
            REDEFINE$4 = new QName("http://www.w3.org/2001/XMLSchema", "redefine");
            ANNOTATION$6 = new QName("http://www.w3.org/2001/XMLSchema", "annotation");
            SIMPLETYPE$8 = new QName("http://www.w3.org/2001/XMLSchema", "simpleType");
            COMPLEXTYPE$10 = new QName("http://www.w3.org/2001/XMLSchema", "complexType");
            GROUP$12 = new QName("http://www.w3.org/2001/XMLSchema", "group");
            ATTRIBUTEGROUP$14 = new QName("http://www.w3.org/2001/XMLSchema", "attributeGroup");
            ELEMENT$16 = new QName("http://www.w3.org/2001/XMLSchema", "element");
            ATTRIBUTE$18 = new QName("http://www.w3.org/2001/XMLSchema", "attribute");
            NOTATION$20 = new QName("http://www.w3.org/2001/XMLSchema", "notation");
            TARGETNAMESPACE$22 = new QName("", "targetNamespace");
            VERSION$24 = new QName("", "version");
            FINALDEFAULT$26 = new QName("", "finalDefault");
            BLOCKDEFAULT$28 = new QName("", "blockDefault");
            ATTRIBUTEFORMDEFAULT$30 = new QName("", "attributeFormDefault");
            ELEMENTFORMDEFAULT$32 = new QName("", "elementFormDefault");
            ID$34 = new QName("", "id");
            LANG$36 = new QName("http://www.w3.org/XML/1998/namespace", "lang");
        }
    }
}
