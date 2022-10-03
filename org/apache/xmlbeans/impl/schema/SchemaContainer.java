package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaComponent;
import java.util.Collections;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import java.util.ArrayList;
import java.util.List;
import org.apache.xmlbeans.SchemaTypeSystem;

class SchemaContainer
{
    private String _namespace;
    private SchemaTypeSystem _typeSystem;
    boolean _immutable;
    private List _globalElements;
    private List _globalAttributes;
    private List _modelGroups;
    private List _redefinedModelGroups;
    private List _attributeGroups;
    private List _redefinedAttributeGroups;
    private List _globalTypes;
    private List _redefinedGlobalTypes;
    private List _documentTypes;
    private List _attributeTypes;
    private List _identityConstraints;
    private List _annotations;
    
    SchemaContainer(final String namespace) {
        this._globalElements = new ArrayList();
        this._globalAttributes = new ArrayList();
        this._modelGroups = new ArrayList();
        this._redefinedModelGroups = new ArrayList();
        this._attributeGroups = new ArrayList();
        this._redefinedAttributeGroups = new ArrayList();
        this._globalTypes = new ArrayList();
        this._redefinedGlobalTypes = new ArrayList();
        this._documentTypes = new ArrayList();
        this._attributeTypes = new ArrayList();
        this._identityConstraints = new ArrayList();
        this._annotations = new ArrayList();
        this._namespace = namespace;
    }
    
    String getNamespace() {
        return this._namespace;
    }
    
    synchronized SchemaTypeSystem getTypeSystem() {
        return this._typeSystem;
    }
    
    synchronized void setTypeSystem(final SchemaTypeSystem typeSystem) {
        this._typeSystem = typeSystem;
    }
    
    synchronized void setImmutable() {
        this._immutable = true;
    }
    
    synchronized void unsetImmutable() {
        this._immutable = false;
    }
    
    private void check_immutable() {
        if (this._immutable) {
            throw new IllegalStateException("Cannot add components to immutable SchemaContainer");
        }
    }
    
    void addGlobalElement(final SchemaGlobalElement.Ref e) {
        this.check_immutable();
        this._globalElements.add(e);
    }
    
    List globalElements() {
        return this.getComponentList(this._globalElements);
    }
    
    void addGlobalAttribute(final SchemaGlobalAttribute.Ref a) {
        this.check_immutable();
        this._globalAttributes.add(a);
    }
    
    List globalAttributes() {
        return this.getComponentList(this._globalAttributes);
    }
    
    void addModelGroup(final SchemaModelGroup.Ref g) {
        this.check_immutable();
        this._modelGroups.add(g);
    }
    
    List modelGroups() {
        return this.getComponentList(this._modelGroups);
    }
    
    void addRedefinedModelGroup(final SchemaModelGroup.Ref g) {
        this.check_immutable();
        this._redefinedModelGroups.add(g);
    }
    
    List redefinedModelGroups() {
        return this.getComponentList(this._redefinedModelGroups);
    }
    
    void addAttributeGroup(final SchemaAttributeGroup.Ref g) {
        this.check_immutable();
        this._attributeGroups.add(g);
    }
    
    List attributeGroups() {
        return this.getComponentList(this._attributeGroups);
    }
    
    void addRedefinedAttributeGroup(final SchemaAttributeGroup.Ref g) {
        this.check_immutable();
        this._redefinedAttributeGroups.add(g);
    }
    
    List redefinedAttributeGroups() {
        return this.getComponentList(this._redefinedAttributeGroups);
    }
    
    void addGlobalType(final SchemaType.Ref t) {
        this.check_immutable();
        this._globalTypes.add(t);
    }
    
    List globalTypes() {
        return this.getComponentList(this._globalTypes);
    }
    
    void addRedefinedType(final SchemaType.Ref t) {
        this.check_immutable();
        this._redefinedGlobalTypes.add(t);
    }
    
    List redefinedGlobalTypes() {
        return this.getComponentList(this._redefinedGlobalTypes);
    }
    
    void addDocumentType(final SchemaType.Ref t) {
        this.check_immutable();
        this._documentTypes.add(t);
    }
    
    List documentTypes() {
        return this.getComponentList(this._documentTypes);
    }
    
    void addAttributeType(final SchemaType.Ref t) {
        this.check_immutable();
        this._attributeTypes.add(t);
    }
    
    List attributeTypes() {
        return this.getComponentList(this._attributeTypes);
    }
    
    void addIdentityConstraint(final SchemaIdentityConstraint.Ref c) {
        this.check_immutable();
        this._identityConstraints.add(c);
    }
    
    List identityConstraints() {
        return this.getComponentList(this._identityConstraints);
    }
    
    void addAnnotation(final SchemaAnnotation a) {
        this.check_immutable();
        this._annotations.add(a);
    }
    
    List annotations() {
        return Collections.unmodifiableList((List<?>)this._annotations);
    }
    
    private List getComponentList(final List referenceList) {
        final List result = new ArrayList();
        for (int i = 0; i < referenceList.size(); ++i) {
            result.add(referenceList.get(i).getComponent());
        }
        return Collections.unmodifiableList((List<?>)result);
    }
}
