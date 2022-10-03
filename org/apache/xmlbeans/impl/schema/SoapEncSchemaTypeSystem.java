package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.Filer;
import java.io.File;
import org.apache.xmlbeans.SchemaComponent;
import java.io.InputStream;
import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaAttributeModel;
import org.apache.xmlbeans.SchemaLocalAttribute;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.XmlObject;
import java.util.Set;
import java.util.Collections;
import java.util.HashSet;
import org.apache.xmlbeans.SchemaParticle;
import org.apache.xmlbeans.QNameSet;
import java.math.BigInteger;
import javax.xml.namespace.QName;
import java.util.HashMap;
import java.util.Map;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.SchemaAttributeGroup;
import org.apache.xmlbeans.SchemaModelGroup;
import org.apache.xmlbeans.SchemaGlobalElement;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.SchemaTypeSystem;

public class SoapEncSchemaTypeSystem extends SchemaTypeLoaderBase implements SchemaTypeSystem
{
    public static final String SOAPENC = "http://schemas.xmlsoap.org/soap/encoding/";
    public static final String SOAP_ARRAY = "Array";
    public static final String ARRAY_TYPE = "arrayType";
    private static final String ATTR_ID = "id";
    private static final String ATTR_HREF = "href";
    private static final String ATTR_OFFSET = "offset";
    private static final SchemaType[] EMPTY_SCHEMATYPE_ARRAY;
    private static final SchemaGlobalElement[] EMPTY_SCHEMAELEMENT_ARRAY;
    private static final SchemaModelGroup[] EMPTY_SCHEMAMODELGROUP_ARRAY;
    private static final SchemaAttributeGroup[] EMPTY_SCHEMAATTRIBUTEGROUP_ARRAY;
    private static final SchemaAnnotation[] EMPTY_SCHEMAANNOTATION_ARRAY;
    private static SoapEncSchemaTypeSystem _global;
    private SchemaTypeImpl soapArray;
    private SchemaGlobalAttributeImpl arrayType;
    private Map _handlesToObjects;
    private String soapArrayHandle;
    private SchemaContainer _container;
    
    public static SchemaTypeSystem get() {
        return SoapEncSchemaTypeSystem._global;
    }
    
    private SoapEncSchemaTypeSystem() {
        this._handlesToObjects = new HashMap();
        (this._container = new SchemaContainer("http://schemas.xmlsoap.org/soap/encoding/")).setTypeSystem(this);
        this.soapArray = new SchemaTypeImpl(this._container, true);
        this._container.addGlobalType(this.soapArray.getRef());
        this.soapArray.setName(new QName("http://schemas.xmlsoap.org/soap/encoding/", "Array"));
        this.soapArrayHandle = "Array".toLowerCase() + "type";
        this.soapArray.setComplexTypeVariety(3);
        this.soapArray.setBaseTypeRef(BuiltinSchemaTypeSystem.ST_ANY_TYPE.getRef());
        this.soapArray.setBaseDepth(1);
        this.soapArray.setDerivationType(2);
        this.soapArray.setSimpleTypeVariety(0);
        final SchemaParticleImpl contentModel = new SchemaParticleImpl();
        contentModel.setParticleType(3);
        contentModel.setMinOccurs(BigInteger.ZERO);
        contentModel.setMaxOccurs(BigInteger.ONE);
        contentModel.setTransitionRules(QNameSet.ALL, true);
        final SchemaParticleImpl[] children = { null };
        contentModel.setParticleChildren(children);
        final SchemaParticleImpl contentModel2 = new SchemaParticleImpl();
        contentModel2.setParticleType(5);
        contentModel2.setWildcardSet(QNameSet.ALL);
        contentModel2.setWildcardProcess(2);
        contentModel2.setMinOccurs(BigInteger.ZERO);
        contentModel2.setMaxOccurs(null);
        contentModel2.setTransitionRules(QNameSet.ALL, true);
        children[0] = contentModel2;
        final SchemaAttributeModelImpl attrModel = new SchemaAttributeModelImpl();
        attrModel.setWildcardProcess(2);
        final HashSet excludedURI = new HashSet();
        excludedURI.add("http://schemas.xmlsoap.org/soap/encoding/");
        attrModel.setWildcardSet(QNameSet.forSets(excludedURI, null, Collections.EMPTY_SET, Collections.EMPTY_SET));
        SchemaLocalAttributeImpl attr = new SchemaLocalAttributeImpl();
        attr.init(new QName("", "id"), BuiltinSchemaTypeSystem.ST_ID.getRef(), 2, null, null, null, false, null, null, null);
        attrModel.addAttribute(attr);
        attr = new SchemaLocalAttributeImpl();
        attr.init(new QName("", "href"), BuiltinSchemaTypeSystem.ST_ANY_URI.getRef(), 2, null, null, null, false, null, null, null);
        attrModel.addAttribute(attr);
        attr = new SchemaLocalAttributeImpl();
        attr.init(new QName("http://schemas.xmlsoap.org/soap/encoding/", "arrayType"), BuiltinSchemaTypeSystem.ST_STRING.getRef(), 2, null, null, null, false, null, null, null);
        attrModel.addAttribute(attr);
        attr = new SchemaLocalAttributeImpl();
        attr.init(new QName("http://schemas.xmlsoap.org/soap/encoding/", "offset"), BuiltinSchemaTypeSystem.ST_STRING.getRef(), 2, null, null, null, false, null, null, null);
        attrModel.addAttribute(attr);
        this.soapArray.setContentModel(contentModel, attrModel, Collections.EMPTY_MAP, Collections.EMPTY_MAP, false);
        this.arrayType = new SchemaGlobalAttributeImpl(this._container);
        this._container.addGlobalAttribute(this.arrayType.getRef());
        this.arrayType.init(new QName("http://schemas.xmlsoap.org/soap/encoding/", "arrayType"), BuiltinSchemaTypeSystem.ST_STRING.getRef(), 2, null, null, null, false, null, null, null);
        this._handlesToObjects.put(this.soapArrayHandle, this.soapArray);
        this._handlesToObjects.put("arrayType".toLowerCase() + "attribute", this.arrayType);
        this._container.setImmutable();
    }
    
    @Override
    public String getName() {
        return "schema.typesystem.soapenc.builtin";
    }
    
    @Override
    public SchemaType findType(final QName qName) {
        if ("http://schemas.xmlsoap.org/soap/encoding/".equals(qName.getNamespaceURI()) && "Array".equals(qName.getLocalPart())) {
            return this.soapArray;
        }
        return null;
    }
    
    @Override
    public SchemaType findDocumentType(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaType findAttributeType(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaGlobalElement findElement(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaGlobalAttribute findAttribute(final QName qName) {
        if ("http://schemas.xmlsoap.org/soap/encoding/".equals(qName.getNamespaceURI()) && "arrayType".equals(qName.getLocalPart())) {
            return this.arrayType;
        }
        return null;
    }
    
    @Override
    public SchemaModelGroup findModelGroup(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaAttributeGroup findAttributeGroup(final QName qName) {
        return null;
    }
    
    @Override
    public boolean isNamespaceDefined(final String string) {
        return "http://schemas.xmlsoap.org/soap/encoding/".equals(string);
    }
    
    @Override
    public SchemaType.Ref findTypeRef(final QName qName) {
        final SchemaType type = this.findType(qName);
        return (type == null) ? null : type.getRef();
    }
    
    @Override
    public SchemaType.Ref findDocumentTypeRef(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaType.Ref findAttributeTypeRef(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaGlobalElement.Ref findElementRef(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaGlobalAttribute.Ref findAttributeRef(final QName qName) {
        final SchemaGlobalAttribute attr = this.findAttribute(qName);
        return (attr == null) ? null : attr.getRef();
    }
    
    @Override
    public SchemaModelGroup.Ref findModelGroupRef(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaAttributeGroup.Ref findAttributeGroupRef(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaIdentityConstraint.Ref findIdentityConstraintRef(final QName qName) {
        return null;
    }
    
    @Override
    public SchemaType typeForClassname(final String string) {
        return null;
    }
    
    @Override
    public InputStream getSourceAsStream(final String string) {
        return null;
    }
    
    @Override
    public ClassLoader getClassLoader() {
        return SoapEncSchemaTypeSystem.class.getClassLoader();
    }
    
    @Override
    public void resolve() {
    }
    
    @Override
    public SchemaType[] globalTypes() {
        return new SchemaType[] { this.soapArray };
    }
    
    @Override
    public SchemaType[] documentTypes() {
        return SoapEncSchemaTypeSystem.EMPTY_SCHEMATYPE_ARRAY;
    }
    
    @Override
    public SchemaType[] attributeTypes() {
        return SoapEncSchemaTypeSystem.EMPTY_SCHEMATYPE_ARRAY;
    }
    
    @Override
    public SchemaGlobalElement[] globalElements() {
        return SoapEncSchemaTypeSystem.EMPTY_SCHEMAELEMENT_ARRAY;
    }
    
    @Override
    public SchemaGlobalAttribute[] globalAttributes() {
        return new SchemaGlobalAttribute[] { this.arrayType };
    }
    
    @Override
    public SchemaModelGroup[] modelGroups() {
        return SoapEncSchemaTypeSystem.EMPTY_SCHEMAMODELGROUP_ARRAY;
    }
    
    @Override
    public SchemaAttributeGroup[] attributeGroups() {
        return SoapEncSchemaTypeSystem.EMPTY_SCHEMAATTRIBUTEGROUP_ARRAY;
    }
    
    @Override
    public SchemaAnnotation[] annotations() {
        return SoapEncSchemaTypeSystem.EMPTY_SCHEMAANNOTATION_ARRAY;
    }
    
    public String handleForType(final SchemaType type) {
        if (this.soapArray.equals(type)) {
            return this.soapArrayHandle;
        }
        return null;
    }
    
    @Override
    public SchemaComponent resolveHandle(final String string) {
        return this._handlesToObjects.get(string);
    }
    
    @Override
    public SchemaType typeForHandle(final String string) {
        return this._handlesToObjects.get(string);
    }
    
    @Override
    public void saveToDirectory(final File file) {
        throw new UnsupportedOperationException("The builtin soap encoding schema type system cannot be saved.");
    }
    
    @Override
    public void save(final Filer filer) {
        throw new UnsupportedOperationException("The builtin soap encoding schema type system cannot be saved.");
    }
    
    static {
        EMPTY_SCHEMATYPE_ARRAY = new SchemaType[0];
        EMPTY_SCHEMAELEMENT_ARRAY = new SchemaGlobalElement[0];
        EMPTY_SCHEMAMODELGROUP_ARRAY = new SchemaModelGroup[0];
        EMPTY_SCHEMAATTRIBUTEGROUP_ARRAY = new SchemaAttributeGroup[0];
        EMPTY_SCHEMAANNOTATION_ARRAY = new SchemaAnnotation[0];
        SoapEncSchemaTypeSystem._global = new SoapEncSchemaTypeSystem();
    }
}
