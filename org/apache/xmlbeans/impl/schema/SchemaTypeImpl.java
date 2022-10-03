package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.impl.values.XmlIntRestriction;
import org.apache.xmlbeans.impl.values.XmlLongRestriction;
import org.apache.xmlbeans.impl.values.XmlIntegerRestriction;
import org.apache.xmlbeans.impl.values.XmlStringRestriction;
import org.apache.xmlbeans.impl.values.XmlStringEnumeration;
import org.apache.xmlbeans.impl.values.XmlDecimalRestriction;
import org.apache.xmlbeans.impl.values.XmlDoubleRestriction;
import org.apache.xmlbeans.impl.values.XmlFloatRestriction;
import org.apache.xmlbeans.impl.values.XmlNotationRestriction;
import org.apache.xmlbeans.impl.values.XmlQNameRestriction;
import org.apache.xmlbeans.impl.values.XmlAnyUriRestriction;
import org.apache.xmlbeans.impl.values.XmlHexBinaryRestriction;
import org.apache.xmlbeans.impl.values.XmlBase64BinaryRestriction;
import org.apache.xmlbeans.impl.values.XmlBooleanRestriction;
import org.apache.xmlbeans.impl.values.XmlUnionImpl;
import org.apache.xmlbeans.impl.values.XmlListImpl;
import org.apache.xmlbeans.impl.values.XmlAnySimpleTypeRestriction;
import org.apache.xmlbeans.impl.values.XmlComplexContentImpl;
import org.apache.xmlbeans.impl.values.XmlNmTokensImpl;
import org.apache.xmlbeans.impl.values.XmlNmTokenImpl;
import org.apache.xmlbeans.impl.values.XmlEntitiesImpl;
import org.apache.xmlbeans.impl.values.XmlEntityImpl;
import org.apache.xmlbeans.impl.values.XmlIdRefsImpl;
import org.apache.xmlbeans.impl.values.XmlIdRefImpl;
import org.apache.xmlbeans.impl.values.XmlIdImpl;
import org.apache.xmlbeans.impl.values.XmlLanguageImpl;
import org.apache.xmlbeans.impl.values.XmlNCNameImpl;
import org.apache.xmlbeans.impl.values.XmlNameImpl;
import org.apache.xmlbeans.impl.values.XmlTokenImpl;
import org.apache.xmlbeans.impl.values.XmlNormalizedStringImpl;
import org.apache.xmlbeans.impl.values.XmlUnsignedByteImpl;
import org.apache.xmlbeans.impl.values.XmlUnsignedShortImpl;
import org.apache.xmlbeans.impl.values.XmlUnsignedIntImpl;
import org.apache.xmlbeans.impl.values.XmlUnsignedLongImpl;
import org.apache.xmlbeans.impl.values.XmlPositiveIntegerImpl;
import org.apache.xmlbeans.impl.values.XmlNonNegativeIntegerImpl;
import org.apache.xmlbeans.impl.values.XmlNegativeIntegerImpl;
import org.apache.xmlbeans.impl.values.XmlNonPositiveIntegerImpl;
import org.apache.xmlbeans.impl.values.XmlByteImpl;
import org.apache.xmlbeans.impl.values.XmlShortImpl;
import org.apache.xmlbeans.impl.values.XmlIntImpl;
import org.apache.xmlbeans.impl.values.XmlLongImpl;
import org.apache.xmlbeans.impl.values.XmlIntegerImpl;
import org.apache.xmlbeans.impl.values.XmlGMonthImpl;
import org.apache.xmlbeans.impl.values.XmlGDayImpl;
import org.apache.xmlbeans.impl.values.XmlGMonthDayImpl;
import org.apache.xmlbeans.impl.values.XmlGYearImpl;
import org.apache.xmlbeans.impl.values.XmlGYearMonthImpl;
import org.apache.xmlbeans.impl.values.XmlDateImpl;
import org.apache.xmlbeans.impl.values.XmlTimeImpl;
import org.apache.xmlbeans.impl.values.XmlDateTimeImpl;
import org.apache.xmlbeans.impl.values.XmlDurationImpl;
import org.apache.xmlbeans.impl.values.XmlStringImpl;
import org.apache.xmlbeans.impl.values.XmlDecimalImpl;
import org.apache.xmlbeans.impl.values.XmlDoubleImpl;
import org.apache.xmlbeans.impl.values.XmlFloatImpl;
import org.apache.xmlbeans.impl.values.XmlNotationImpl;
import org.apache.xmlbeans.impl.values.XmlQNameImpl;
import org.apache.xmlbeans.impl.values.XmlAnyUriImpl;
import org.apache.xmlbeans.impl.values.XmlHexBinaryImpl;
import org.apache.xmlbeans.impl.values.XmlBase64BinaryImpl;
import org.apache.xmlbeans.impl.values.XmlBooleanImpl;
import org.apache.xmlbeans.impl.values.XmlAnySimpleTypeImpl;
import org.apache.xmlbeans.impl.values.XmlAnyTypeImpl;
import org.apache.xmlbeans.impl.values.XmlObjectBase;
import org.apache.xmlbeans.impl.values.XmlValueOutOfRangeException;
import org.apache.xmlbeans.impl.values.TypeStoreUser;
import org.apache.xmlbeans.QNameSetSpecification;
import org.apache.xmlbeans.QNameSetBuilder;
import org.apache.xmlbeans.impl.values.StringEnumValue;
import org.apache.xmlbeans.StringEnumAbstractBase;
import org.apache.xmlbeans.XmlAnySimpleType;
import java.util.Arrays;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.util.LinkedHashSet;
import java.math.BigInteger;
import java.util.Iterator;
import java.util.Collection;
import java.util.HashMap;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SchemaTypeElementSequencer;
import org.apache.xmlbeans.SchemaLocalAttribute;
import org.apache.xmlbeans.SchemaGlobalAttribute;
import org.apache.xmlbeans.SchemaGlobalElement;
import java.util.ArrayList;
import java.util.Collections;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaProperty;
import org.apache.xmlbeans.SchemaStringEnumEntry;
import java.util.List;
import org.apache.xmlbeans.impl.regex.RegularExpression;
import org.apache.xmlbeans.SchemaAttributeModel;
import java.util.Set;
import org.apache.xmlbeans.QNameSet;
import java.util.Map;
import org.apache.xmlbeans.SchemaLocalElement;
import org.apache.xmlbeans.SchemaParticle;
import java.lang.reflect.Constructor;
import org.apache.xmlbeans.PrePostExtension;
import org.apache.xmlbeans.InterfaceExtension;
import org.apache.xmlbeans.SchemaField;
import org.apache.xmlbeans.SchemaComponent;
import org.apache.xmlbeans.SchemaAnnotation;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.impl.values.TypeStoreUserFactory;
import org.apache.xmlbeans.SchemaType;

public final class SchemaTypeImpl implements SchemaType, TypeStoreUserFactory
{
    private QName _name;
    private SchemaAnnotation _annotation;
    private int _resolvePhase;
    private static final int UNRESOLVED = 0;
    private static final int RESOLVING_SGS = 1;
    private static final int RESOLVED_SGS = 2;
    private static final int RESOLVING = 3;
    private static final int RESOLVED = 4;
    private static final int JAVAIZING = 5;
    private static final int JAVAIZED = 6;
    private Ref _outerSchemaTypeRef;
    private volatile SchemaComponent.Ref _containerFieldRef;
    private volatile SchemaField _containerField;
    private volatile int _containerFieldCode;
    private volatile int _containerFieldIndex;
    private volatile QName[] _groupReferenceContext;
    private Ref[] _anonymousTyperefs;
    private boolean _isDocumentType;
    private boolean _isAttributeType;
    private boolean _isCompiled;
    private String _shortJavaName;
    private String _fullJavaName;
    private String _shortJavaImplName;
    private String _fullJavaImplName;
    private InterfaceExtension[] _interfaces;
    private PrePostExtension _prepost;
    private volatile Class _javaClass;
    private volatile Class _javaEnumClass;
    private volatile Class _javaImplClass;
    private volatile Constructor _javaImplConstructor;
    private volatile Constructor _javaImplConstructor2;
    private volatile boolean _implNotAvailable;
    private volatile Class _userTypeClass;
    private volatile Class _userTypeHandlerClass;
    private volatile Object _userData;
    private final Object[] _ctrArgs;
    private SchemaContainer _container;
    private String _filename;
    private SchemaParticle _contentModel;
    private volatile SchemaLocalElement[] _localElts;
    private volatile Map _eltToIndexMap;
    private volatile Map _attrToIndexMap;
    private Map _propertyModelByElementName;
    private Map _propertyModelByAttributeName;
    private boolean _hasAllContent;
    private boolean _orderSensitive;
    private QNameSet _typedWildcardElements;
    private QNameSet _typedWildcardAttributes;
    private boolean _hasWildcardElements;
    private boolean _hasWildcardAttributes;
    private Set _validSubstitutions;
    private int _complexTypeVariety;
    private SchemaAttributeModel _attributeModel;
    private int _builtinTypeCode;
    private int _simpleTypeVariety;
    private boolean _isSimpleType;
    private Ref _baseTyperef;
    private int _baseDepth;
    private int _derivationType;
    private String _userTypeName;
    private String _userTypeHandler;
    private Ref _contentBasedOnTyperef;
    private XmlValueRef[] _facetArray;
    private boolean[] _fixedFacetArray;
    private int _ordered;
    private boolean _isFinite;
    private boolean _isBounded;
    private boolean _isNumeric;
    private boolean _abs;
    private boolean _finalExt;
    private boolean _finalRest;
    private boolean _finalList;
    private boolean _finalUnion;
    private boolean _blockExt;
    private boolean _blockRest;
    private int _whiteSpaceRule;
    private boolean _hasPatterns;
    private RegularExpression[] _patterns;
    private XmlValueRef[] _enumerationValues;
    private Ref _baseEnumTyperef;
    private boolean _stringEnumEnsured;
    private volatile Map _lookupStringEnum;
    private volatile List _listOfStringEnum;
    private volatile Map _lookupStringEnumEntry;
    private SchemaStringEnumEntry[] _stringEnumEntries;
    private Ref _listItemTyperef;
    private boolean _isUnionOfLists;
    private Ref[] _unionMemberTyperefs;
    private int _anonymousUnionMemberOrdinal;
    private volatile SchemaType[] _unionConstituentTypes;
    private volatile SchemaType[] _unionSubTypes;
    private volatile SchemaType _unionCommonBaseType;
    private Ref _primitiveTypeRef;
    private int _decimalSize;
    private volatile boolean _unloaded;
    private QName _sg;
    private List _sgMembers;
    private static final SchemaProperty[] NO_PROPERTIES;
    private XmlObject _parseObject;
    private String _parseTNS;
    private String _elemFormDefault;
    private String _attFormDefault;
    private boolean _chameleon;
    private boolean _redefinition;
    private Ref _selfref;
    
    public boolean isUnloaded() {
        return this._unloaded;
    }
    
    public void finishLoading() {
        this._unloaded = false;
    }
    
    SchemaTypeImpl(final SchemaContainer container) {
        this._ctrArgs = new Object[] { this };
        this._validSubstitutions = Collections.EMPTY_SET;
        this._sgMembers = new ArrayList();
        this._selfref = new Ref(this);
        this._container = container;
    }
    
    SchemaTypeImpl(final SchemaContainer container, final boolean unloaded) {
        this._ctrArgs = new Object[] { this };
        this._validSubstitutions = Collections.EMPTY_SET;
        this._sgMembers = new ArrayList();
        this._selfref = new Ref(this);
        this._container = container;
        this._unloaded = unloaded;
        if (unloaded) {
            this.finishQuick();
        }
    }
    
    public boolean isSGResolved() {
        return this._resolvePhase >= 2;
    }
    
    public boolean isSGResolving() {
        return this._resolvePhase >= 1;
    }
    
    public boolean isResolved() {
        return this._resolvePhase >= 4;
    }
    
    public boolean isResolving() {
        return this._resolvePhase == 3;
    }
    
    public boolean isUnjavaized() {
        return this._resolvePhase < 6;
    }
    
    public boolean isJavaized() {
        return this._resolvePhase == 6;
    }
    
    public void startResolvingSGs() {
        if (this._resolvePhase != 0) {
            throw new IllegalStateException();
        }
        this._resolvePhase = 1;
    }
    
    public void finishResolvingSGs() {
        if (this._resolvePhase != 1) {
            throw new IllegalStateException();
        }
        this._resolvePhase = 2;
    }
    
    public void startResolving() {
        if ((this._isDocumentType && this._resolvePhase != 2) || (!this._isDocumentType && this._resolvePhase != 0)) {
            throw new IllegalStateException();
        }
        this._resolvePhase = 3;
    }
    
    public void finishResolving() {
        if (this._resolvePhase != 3) {
            throw new IllegalStateException();
        }
        this._resolvePhase = 4;
    }
    
    public void startJavaizing() {
        if (this._resolvePhase != 4) {
            throw new IllegalStateException();
        }
        this._resolvePhase = 5;
    }
    
    public void finishJavaizing() {
        if (this._resolvePhase != 5) {
            throw new IllegalStateException();
        }
        this._resolvePhase = 6;
    }
    
    private void finishQuick() {
        this._resolvePhase = 6;
    }
    
    private void assertUnresolved() {
        if (this._resolvePhase != 0 && !this._unloaded) {
            throw new IllegalStateException();
        }
    }
    
    private void assertSGResolving() {
        if (this._resolvePhase != 1 && !this._unloaded) {
            throw new IllegalStateException();
        }
    }
    
    private void assertSGResolved() {
        if (this._resolvePhase != 2 && !this._unloaded) {
            throw new IllegalStateException();
        }
    }
    
    private void assertResolving() {
        if (this._resolvePhase != 3 && !this._unloaded) {
            throw new IllegalStateException();
        }
    }
    
    private void assertResolved() {
        if (this._resolvePhase != 4 && !this._unloaded) {
            throw new IllegalStateException();
        }
    }
    
    private void assertJavaizing() {
        if (this._resolvePhase != 5 && !this._unloaded) {
            throw new IllegalStateException();
        }
    }
    
    @Override
    public QName getName() {
        return this._name;
    }
    
    public void setName(final QName name) {
        this.assertUnresolved();
        this._name = name;
    }
    
    @Override
    public String getSourceName() {
        if (this._filename != null) {
            return this._filename;
        }
        if (this.getOuterType() != null) {
            return this.getOuterType().getSourceName();
        }
        final SchemaField field = this.getContainerField();
        if (field != null) {
            if (field instanceof SchemaGlobalElement) {
                return ((SchemaGlobalElement)field).getSourceName();
            }
            if (field instanceof SchemaGlobalAttribute) {
                return ((SchemaGlobalAttribute)field).getSourceName();
            }
        }
        return null;
    }
    
    public void setFilename(final String filename) {
        this.assertUnresolved();
        this._filename = filename;
    }
    
    @Override
    public int getComponentType() {
        return 0;
    }
    
    @Override
    public boolean isAnonymousType() {
        return this._name == null;
    }
    
    @Override
    public boolean isDocumentType() {
        return this._isDocumentType;
    }
    
    @Override
    public boolean isAttributeType() {
        return this._isAttributeType;
    }
    
    @Override
    public QName getDocumentElementName() {
        if (this._isDocumentType) {
            final SchemaParticle sp = this.getContentModel();
            if (sp != null) {
                return sp.getName();
            }
        }
        return null;
    }
    
    @Override
    public QName getAttributeTypeAttributeName() {
        if (this._isAttributeType) {
            final SchemaAttributeModel sam = this.getAttributeModel();
            if (sam != null) {
                final SchemaLocalAttribute[] slaArray = sam.getAttributes();
                if (slaArray != null && slaArray.length > 0) {
                    final SchemaLocalAttribute sla = slaArray[0];
                    return sla.getName();
                }
            }
        }
        return null;
    }
    
    public void setAnnotation(final SchemaAnnotation ann) {
        this.assertUnresolved();
        this._annotation = ann;
    }
    
    @Override
    public SchemaAnnotation getAnnotation() {
        return this._annotation;
    }
    
    public void setDocumentType(final boolean isDocument) {
        this.assertUnresolved();
        this._isDocumentType = isDocument;
    }
    
    public void setAttributeType(final boolean isAttribute) {
        this.assertUnresolved();
        this._isAttributeType = isAttribute;
    }
    
    @Override
    public int getContentType() {
        return this._complexTypeVariety;
    }
    
    public void setComplexTypeVariety(final int complexTypeVariety) {
        this.assertResolving();
        this._complexTypeVariety = complexTypeVariety;
    }
    
    @Override
    public SchemaTypeElementSequencer getElementSequencer() {
        if (this._complexTypeVariety == 0) {
            return new SequencerImpl((SchemaTypeVisitorImpl)null);
        }
        return new SequencerImpl(new SchemaTypeVisitorImpl(this._contentModel));
    }
    
    void setAbstractFinal(final boolean abs, final boolean finalExt, final boolean finalRest, final boolean finalList, final boolean finalUnion) {
        this.assertResolving();
        this._abs = abs;
        this._finalExt = finalExt;
        this._finalRest = finalRest;
        this._finalList = finalList;
        this._finalUnion = finalUnion;
    }
    
    void setSimpleFinal(final boolean finalRest, final boolean finalList, final boolean finalUnion) {
        this.assertResolving();
        this._finalRest = finalRest;
        this._finalList = finalList;
        this._finalUnion = finalUnion;
    }
    
    void setBlock(final boolean blockExt, final boolean blockRest) {
        this.assertResolving();
        this._blockExt = blockExt;
        this._blockRest = blockRest;
    }
    
    @Override
    public boolean blockRestriction() {
        return this._blockRest;
    }
    
    @Override
    public boolean blockExtension() {
        return this._blockExt;
    }
    
    @Override
    public boolean isAbstract() {
        return this._abs;
    }
    
    @Override
    public boolean finalExtension() {
        return this._finalExt;
    }
    
    @Override
    public boolean finalRestriction() {
        return this._finalRest;
    }
    
    @Override
    public boolean finalList() {
        return this._finalList;
    }
    
    @Override
    public boolean finalUnion() {
        return this._finalUnion;
    }
    
    @Override
    public synchronized SchemaField getContainerField() {
        if (this._containerFieldCode != -1) {
            final SchemaType outer = this.getOuterType();
            if (this._containerFieldCode == 0) {
                this._containerField = ((this._containerFieldRef == null) ? null : ((SchemaField)this._containerFieldRef.getComponent()));
            }
            else if (this._containerFieldCode == 1) {
                this._containerField = outer.getAttributeModel().getAttributes()[this._containerFieldIndex];
            }
            else {
                this._containerField = ((SchemaTypeImpl)outer).getLocalElementByIndex(this._containerFieldIndex);
            }
            this._containerFieldCode = -1;
        }
        return this._containerField;
    }
    
    public void setContainerField(final SchemaField field) {
        this.assertUnresolved();
        this._containerField = field;
        this._containerFieldCode = -1;
    }
    
    public void setContainerFieldRef(final SchemaComponent.Ref ref) {
        this.assertUnresolved();
        this._containerFieldRef = ref;
        this._containerFieldCode = 0;
    }
    
    public void setContainerFieldIndex(final short code, final int index) {
        this.assertUnresolved();
        this._containerFieldCode = code;
        this._containerFieldIndex = index;
    }
    
    void setGroupReferenceContext(final QName[] groupNames) {
        this.assertUnresolved();
        this._groupReferenceContext = groupNames;
    }
    
    QName[] getGroupReferenceContext() {
        return this._groupReferenceContext;
    }
    
    @Override
    public SchemaType getOuterType() {
        return (this._outerSchemaTypeRef == null) ? null : this._outerSchemaTypeRef.get();
    }
    
    public void setOuterSchemaTypeRef(final Ref typeref) {
        this.assertUnresolved();
        this._outerSchemaTypeRef = typeref;
    }
    
    @Override
    public boolean isCompiled() {
        return this._isCompiled;
    }
    
    public void setCompiled(final boolean f) {
        this.assertJavaizing();
        this._isCompiled = f;
    }
    
    @Override
    public boolean isSkippedAnonymousType() {
        final SchemaType outerType = this.getOuterType();
        return outerType != null && (outerType.getBaseType() == this || outerType.getContentBasedOnType() == this);
    }
    
    @Override
    public String getShortJavaName() {
        return this._shortJavaName;
    }
    
    public void setShortJavaName(final String name) {
        this.assertResolved();
        this._shortJavaName = name;
        SchemaType outer;
        for (outer = this._outerSchemaTypeRef.get(); outer.getFullJavaName() == null; outer = outer.getOuterType()) {}
        this._fullJavaName = outer.getFullJavaName() + "$" + this._shortJavaName;
    }
    
    @Override
    public String getFullJavaName() {
        return this._fullJavaName;
    }
    
    public void setFullJavaName(final String name) {
        this.assertResolved();
        this._fullJavaName = name;
        final int index = Math.max(this._fullJavaName.lastIndexOf(36), this._fullJavaName.lastIndexOf(46)) + 1;
        this._shortJavaName = this._fullJavaName.substring(index);
    }
    
    public void setShortJavaImplName(final String name) {
        this.assertResolved();
        this._shortJavaImplName = name;
        SchemaType outer;
        for (outer = this._outerSchemaTypeRef.get(); outer.getFullJavaImplName() == null; outer = outer.getOuterType()) {}
        this._fullJavaImplName = outer.getFullJavaImplName() + "$" + this._shortJavaImplName;
    }
    
    public void setFullJavaImplName(final String name) {
        this.assertResolved();
        this._fullJavaImplName = name;
        final int index = Math.max(this._fullJavaImplName.lastIndexOf(36), this._fullJavaImplName.lastIndexOf(46)) + 1;
        this._shortJavaImplName = this._fullJavaImplName.substring(index);
    }
    
    @Override
    public String getFullJavaImplName() {
        return this._fullJavaImplName;
    }
    
    @Override
    public String getShortJavaImplName() {
        return this._shortJavaImplName;
    }
    
    public String getUserTypeName() {
        return this._userTypeName;
    }
    
    public void setUserTypeName(final String userTypeName) {
        this._userTypeName = userTypeName;
    }
    
    public String getUserTypeHandlerName() {
        return this._userTypeHandler;
    }
    
    public void setUserTypeHandlerName(final String typeHandler) {
        this._userTypeHandler = typeHandler;
    }
    
    public void setInterfaceExtensions(final InterfaceExtension[] interfaces) {
        this.assertResolved();
        this._interfaces = interfaces;
    }
    
    public InterfaceExtension[] getInterfaceExtensions() {
        return this._interfaces;
    }
    
    public void setPrePostExtension(final PrePostExtension prepost) {
        this.assertResolved();
        this._prepost = prepost;
    }
    
    public PrePostExtension getPrePostExtension() {
        return this._prepost;
    }
    
    @Override
    public Object getUserData() {
        return this._userData;
    }
    
    public void setUserData(final Object data) {
        this._userData = data;
    }
    
    SchemaContainer getContainer() {
        return this._container;
    }
    
    void setContainer(final SchemaContainer container) {
        this._container = container;
    }
    
    @Override
    public SchemaTypeSystem getTypeSystem() {
        return this._container.getTypeSystem();
    }
    
    @Override
    public SchemaParticle getContentModel() {
        return this._contentModel;
    }
    
    private static void buildEltList(final List eltList, final SchemaParticle contentModel) {
        if (contentModel == null) {
            return;
        }
        switch (contentModel.getParticleType()) {
            case 4: {
                eltList.add(contentModel);
                return;
            }
            case 1:
            case 2:
            case 3: {
                for (int i = 0; i < contentModel.countOfParticleChild(); ++i) {
                    buildEltList(eltList, contentModel.getParticleChild(i));
                }
            }
            default: {}
        }
    }
    
    private void buildLocalElts() {
        final List eltList = new ArrayList();
        buildEltList(eltList, this._contentModel);
        this._localElts = eltList.toArray(new SchemaLocalElement[eltList.size()]);
    }
    
    public SchemaLocalElement getLocalElementByIndex(final int i) {
        SchemaLocalElement[] elts = this._localElts;
        if (elts == null) {
            this.buildLocalElts();
            elts = this._localElts;
        }
        return elts[i];
    }
    
    public int getIndexForLocalElement(final SchemaLocalElement elt) {
        Map localEltMap = this._eltToIndexMap;
        if (localEltMap == null) {
            if (this._localElts == null) {
                this.buildLocalElts();
            }
            localEltMap = new HashMap();
            for (int i = 0; i < this._localElts.length; ++i) {
                localEltMap.put(this._localElts[i], new Integer(i));
            }
            this._eltToIndexMap = localEltMap;
        }
        return localEltMap.get(elt);
    }
    
    public int getIndexForLocalAttribute(final SchemaLocalAttribute attr) {
        Map localAttrMap = this._attrToIndexMap;
        if (localAttrMap == null) {
            localAttrMap = new HashMap();
            final SchemaLocalAttribute[] attrs = this._attributeModel.getAttributes();
            for (int i = 0; i < attrs.length; ++i) {
                localAttrMap.put(attrs[i], new Integer(i));
            }
            this._attrToIndexMap = localAttrMap;
        }
        return localAttrMap.get(attr);
    }
    
    @Override
    public SchemaAttributeModel getAttributeModel() {
        return this._attributeModel;
    }
    
    @Override
    public SchemaProperty[] getProperties() {
        if (this._propertyModelByElementName == null) {
            return this.getAttributeProperties();
        }
        if (this._propertyModelByAttributeName == null) {
            return this.getElementProperties();
        }
        final List list = new ArrayList();
        list.addAll(this._propertyModelByElementName.values());
        list.addAll(this._propertyModelByAttributeName.values());
        return list.toArray(new SchemaProperty[list.size()]);
    }
    
    @Override
    public SchemaProperty[] getDerivedProperties() {
        final SchemaType baseType = this.getBaseType();
        if (baseType == null) {
            return this.getProperties();
        }
        final List results = new ArrayList();
        if (this._propertyModelByElementName != null) {
            results.addAll(this._propertyModelByElementName.values());
        }
        if (this._propertyModelByAttributeName != null) {
            results.addAll(this._propertyModelByAttributeName.values());
        }
        final Iterator it = results.iterator();
        while (it.hasNext()) {
            final SchemaProperty prop = it.next();
            final SchemaProperty baseProp = prop.isAttribute() ? baseType.getAttributeProperty(prop.getName()) : baseType.getElementProperty(prop.getName());
            if (baseProp != null && eq(prop.getMinOccurs(), baseProp.getMinOccurs()) && eq(prop.getMaxOccurs(), baseProp.getMaxOccurs()) && prop.hasNillable() == baseProp.hasNillable() && eq(prop.getDefaultText(), baseProp.getDefaultText())) {
                it.remove();
            }
        }
        return results.toArray(new SchemaProperty[results.size()]);
    }
    
    private static boolean eq(final BigInteger a, final BigInteger b) {
        return (a == null && b == null) || (a != null && b != null && a.equals(b));
    }
    
    private static boolean eq(final String a, final String b) {
        return (a == null && b == null) || (a != null && b != null && a.equals(b));
    }
    
    @Override
    public SchemaProperty[] getElementProperties() {
        if (this._propertyModelByElementName == null) {
            return SchemaTypeImpl.NO_PROPERTIES;
        }
        return (SchemaProperty[])this._propertyModelByElementName.values().toArray(new SchemaProperty[this._propertyModelByElementName.size()]);
    }
    
    @Override
    public SchemaProperty[] getAttributeProperties() {
        if (this._propertyModelByAttributeName == null) {
            return SchemaTypeImpl.NO_PROPERTIES;
        }
        return (SchemaProperty[])this._propertyModelByAttributeName.values().toArray(new SchemaProperty[this._propertyModelByAttributeName.size()]);
    }
    
    @Override
    public SchemaProperty getElementProperty(final QName eltName) {
        return (this._propertyModelByElementName == null) ? null : this._propertyModelByElementName.get(eltName);
    }
    
    @Override
    public SchemaProperty getAttributeProperty(final QName attrName) {
        return (this._propertyModelByAttributeName == null) ? null : this._propertyModelByAttributeName.get(attrName);
    }
    
    @Override
    public boolean hasAllContent() {
        return this._hasAllContent;
    }
    
    @Override
    public boolean isOrderSensitive() {
        return this._orderSensitive;
    }
    
    public void setOrderSensitive(final boolean sensitive) {
        this.assertJavaizing();
        this._orderSensitive = sensitive;
    }
    
    public void setContentModel(final SchemaParticle contentModel, final SchemaAttributeModel attrModel, final Map propertyModelByElementName, final Map propertyModelByAttributeName, final boolean isAll) {
        this.assertResolving();
        this._contentModel = contentModel;
        this._attributeModel = attrModel;
        this._propertyModelByElementName = propertyModelByElementName;
        this._propertyModelByAttributeName = propertyModelByAttributeName;
        this._hasAllContent = isAll;
        if (this._propertyModelByElementName != null) {
            this._validSubstitutions = new LinkedHashSet();
            final Collection eltProps = this._propertyModelByElementName.values();
            for (final SchemaProperty prop : eltProps) {
                final QName[] names = prop.acceptedNames();
                for (int i = 0; i < names.length; ++i) {
                    if (!this._propertyModelByElementName.containsKey(names[i])) {
                        this._validSubstitutions.add(names[i]);
                    }
                }
            }
        }
    }
    
    private boolean containsElements() {
        return this.getContentType() == 3 || this.getContentType() == 4;
    }
    
    @Override
    public boolean hasAttributeWildcards() {
        return this._hasWildcardAttributes;
    }
    
    @Override
    public boolean hasElementWildcards() {
        return this._hasWildcardElements;
    }
    
    @Override
    public boolean isValidSubstitution(final QName name) {
        return this._validSubstitutions.contains(name);
    }
    
    @Override
    public SchemaType getElementType(final QName eltName, final QName xsiType, final SchemaTypeLoader wildcardTypeLoader) {
        if (this.isSimpleType() || !this.containsElements() || this.isNoType()) {
            return BuiltinSchemaTypeSystem.ST_NO_TYPE;
        }
        SchemaType type = null;
        final SchemaProperty prop = this._propertyModelByElementName.get(eltName);
        if (prop != null) {
            type = prop.getType();
        }
        else {
            if (wildcardTypeLoader == null) {
                return BuiltinSchemaTypeSystem.ST_NO_TYPE;
            }
            if (this._typedWildcardElements.contains(eltName) || this._validSubstitutions.contains(eltName)) {
                final SchemaGlobalElement elt = wildcardTypeLoader.findElement(eltName);
                if (elt == null) {
                    return BuiltinSchemaTypeSystem.ST_NO_TYPE;
                }
                type = elt.getType();
            }
            else if (type == null) {
                return BuiltinSchemaTypeSystem.ST_NO_TYPE;
            }
        }
        if (xsiType != null && wildcardTypeLoader != null) {
            final SchemaType itype = wildcardTypeLoader.findType(xsiType);
            if (itype != null && type.isAssignableFrom(itype)) {
                return itype;
            }
        }
        return type;
    }
    
    @Override
    public SchemaType getAttributeType(final QName attrName, final SchemaTypeLoader wildcardTypeLoader) {
        if (this.isSimpleType() || this.isNoType()) {
            return BuiltinSchemaTypeSystem.ST_NO_TYPE;
        }
        if (this.isURType()) {
            return BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        final SchemaProperty prop = this._propertyModelByAttributeName.get(attrName);
        if (prop != null) {
            return prop.getType();
        }
        if (!this._typedWildcardAttributes.contains(attrName) || wildcardTypeLoader == null) {
            return BuiltinSchemaTypeSystem.ST_NO_TYPE;
        }
        final SchemaGlobalAttribute attr = wildcardTypeLoader.findAttribute(attrName);
        if (attr == null) {
            return BuiltinSchemaTypeSystem.ST_NO_TYPE;
        }
        return attr.getType();
    }
    
    public XmlObject createElementType(final QName eltName, final QName xsiType, final SchemaTypeLoader wildcardTypeLoader) {
        SchemaType type = null;
        SchemaProperty prop = null;
        if (this.isSimpleType() || !this.containsElements() || this.isNoType()) {
            type = BuiltinSchemaTypeSystem.ST_NO_TYPE;
        }
        else {
            prop = this._propertyModelByElementName.get(eltName);
            if (prop != null) {
                type = prop.getType();
            }
            else if (this._typedWildcardElements.contains(eltName) || this._validSubstitutions.contains(eltName)) {
                final SchemaGlobalElement elt = wildcardTypeLoader.findElement(eltName);
                if (elt != null) {
                    type = elt.getType();
                    final SchemaType docType = wildcardTypeLoader.findDocumentType(eltName);
                    if (docType != null) {
                        prop = docType.getElementProperty(eltName);
                    }
                }
                else {
                    type = BuiltinSchemaTypeSystem.ST_NO_TYPE;
                }
            }
            else if (type == null) {
                type = BuiltinSchemaTypeSystem.ST_NO_TYPE;
            }
            if (xsiType != null) {
                final SchemaType itype = wildcardTypeLoader.findType(xsiType);
                if (itype != null && type.isAssignableFrom(itype)) {
                    type = itype;
                }
            }
        }
        if (type != null) {
            return ((SchemaTypeImpl)type).createUnattachedNode(prop);
        }
        return null;
    }
    
    public XmlObject createAttributeType(final QName attrName, final SchemaTypeLoader wildcardTypeLoader) {
        SchemaTypeImpl type = null;
        SchemaProperty prop = null;
        if (this.isSimpleType() || this.isNoType()) {
            type = BuiltinSchemaTypeSystem.ST_NO_TYPE;
        }
        else if (this.isURType()) {
            type = BuiltinSchemaTypeSystem.ST_ANY_SIMPLE;
        }
        else {
            prop = this._propertyModelByAttributeName.get(attrName);
            if (prop != null) {
                type = (SchemaTypeImpl)prop.getType();
            }
            else if (!this._typedWildcardAttributes.contains(attrName)) {
                type = BuiltinSchemaTypeSystem.ST_NO_TYPE;
            }
            else {
                final SchemaGlobalAttribute attr = wildcardTypeLoader.findAttribute(attrName);
                if (attr != null) {
                    type = (SchemaTypeImpl)attr.getType();
                }
                else {
                    type = BuiltinSchemaTypeSystem.ST_NO_TYPE;
                }
            }
        }
        if (type != null) {
            return type.createUnattachedNode(prop);
        }
        return null;
    }
    
    public void setWildcardSummary(final QNameSet elementSet, final boolean haswcElt, final QNameSet attributeSet, final boolean haswcAtt) {
        this.assertResolving();
        this._typedWildcardElements = elementSet;
        this._hasWildcardElements = haswcElt;
        this._typedWildcardAttributes = attributeSet;
        this._hasWildcardAttributes = haswcAtt;
    }
    
    @Override
    public SchemaType[] getAnonymousTypes() {
        final SchemaType[] result = new SchemaType[this._anonymousTyperefs.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this._anonymousTyperefs[i].get();
        }
        return result;
    }
    
    public void setAnonymousTypeRefs(final Ref[] anonymousTyperefs) {
        this._anonymousTyperefs = anonymousTyperefs;
    }
    
    private static SchemaType[] staCopy(final SchemaType[] a) {
        if (a == null) {
            return null;
        }
        final SchemaType[] result = new SchemaType[a.length];
        System.arraycopy(a, 0, result, 0, a.length);
        return result;
    }
    
    private static boolean[] boaCopy(final boolean[] a) {
        if (a == null) {
            return null;
        }
        final boolean[] result = new boolean[a.length];
        System.arraycopy(a, 0, result, 0, a.length);
        return result;
    }
    
    public void setSimpleTypeVariety(final int variety) {
        this.assertResolving();
        this._simpleTypeVariety = variety;
    }
    
    @Override
    public int getSimpleVariety() {
        return this._simpleTypeVariety;
    }
    
    @Override
    public boolean isURType() {
        return this._builtinTypeCode == 1 || this._builtinTypeCode == 2;
    }
    
    @Override
    public boolean isNoType() {
        return this == BuiltinSchemaTypeSystem.ST_NO_TYPE;
    }
    
    @Override
    public boolean isSimpleType() {
        return this._isSimpleType;
    }
    
    public void setSimpleType(final boolean f) {
        this.assertUnresolved();
        this._isSimpleType = f;
    }
    
    public boolean isUnionOfLists() {
        return this._isUnionOfLists;
    }
    
    public void setUnionOfLists(final boolean f) {
        this.assertResolving();
        this._isUnionOfLists = f;
    }
    
    @Override
    public SchemaType getPrimitiveType() {
        return (this._primitiveTypeRef == null) ? null : this._primitiveTypeRef.get();
    }
    
    public void setPrimitiveTypeRef(final Ref typeref) {
        this.assertResolving();
        this._primitiveTypeRef = typeref;
    }
    
    @Override
    public int getDecimalSize() {
        return this._decimalSize;
    }
    
    public void setDecimalSize(final int bits) {
        this.assertResolving();
        this._decimalSize = bits;
    }
    
    @Override
    public SchemaType getBaseType() {
        return (this._baseTyperef == null) ? null : this._baseTyperef.get();
    }
    
    public void setBaseTypeRef(final Ref typeref) {
        this.assertResolving();
        this._baseTyperef = typeref;
    }
    
    public int getBaseDepth() {
        return this._baseDepth;
    }
    
    public void setBaseDepth(final int depth) {
        this.assertResolving();
        this._baseDepth = depth;
    }
    
    @Override
    public SchemaType getContentBasedOnType() {
        return (this._contentBasedOnTyperef == null) ? null : this._contentBasedOnTyperef.get();
    }
    
    public void setContentBasedOnTypeRef(final Ref typeref) {
        this.assertResolving();
        this._contentBasedOnTyperef = typeref;
    }
    
    @Override
    public int getDerivationType() {
        return this._derivationType;
    }
    
    public void setDerivationType(final int type) {
        this.assertResolving();
        this._derivationType = type;
    }
    
    @Override
    public SchemaType getListItemType() {
        return (this._listItemTyperef == null) ? null : this._listItemTyperef.get();
    }
    
    public void setListItemTypeRef(final Ref typeref) {
        this.assertResolving();
        this._listItemTyperef = typeref;
    }
    
    @Override
    public SchemaType[] getUnionMemberTypes() {
        final SchemaType[] result = new SchemaType[(this._unionMemberTyperefs == null) ? 0 : this._unionMemberTyperefs.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this._unionMemberTyperefs[i].get();
        }
        return result;
    }
    
    public void setUnionMemberTypeRefs(final Ref[] typerefs) {
        this.assertResolving();
        this._unionMemberTyperefs = typerefs;
    }
    
    @Override
    public int getAnonymousUnionMemberOrdinal() {
        return this._anonymousUnionMemberOrdinal;
    }
    
    public void setAnonymousUnionMemberOrdinal(final int i) {
        this.assertUnresolved();
        this._anonymousUnionMemberOrdinal = i;
    }
    
    @Override
    public synchronized SchemaType[] getUnionConstituentTypes() {
        if (this._unionCommonBaseType == null) {
            this.computeFlatUnionModel();
        }
        return staCopy(this._unionConstituentTypes);
    }
    
    private void setUnionConstituentTypes(final SchemaType[] types) {
        this._unionConstituentTypes = types;
    }
    
    @Override
    public synchronized SchemaType[] getUnionSubTypes() {
        if (this._unionCommonBaseType == null) {
            this.computeFlatUnionModel();
        }
        return staCopy(this._unionSubTypes);
    }
    
    private void setUnionSubTypes(final SchemaType[] types) {
        this._unionSubTypes = types;
    }
    
    @Override
    public synchronized SchemaType getUnionCommonBaseType() {
        if (this._unionCommonBaseType == null) {
            this.computeFlatUnionModel();
        }
        return this._unionCommonBaseType;
    }
    
    private void setUnionCommonBaseType(final SchemaType type) {
        this._unionCommonBaseType = type;
    }
    
    private void computeFlatUnionModel() {
        if (this.getSimpleVariety() != 2) {
            throw new IllegalStateException("Operation is only supported on union types");
        }
        final Set constituentMemberTypes = new LinkedHashSet();
        final Set allSubTypes = new LinkedHashSet();
        SchemaType commonBaseType = null;
        allSubTypes.add(this);
        for (int i = 0; i < this._unionMemberTyperefs.length; ++i) {
            final SchemaTypeImpl mImpl = (SchemaTypeImpl)this._unionMemberTyperefs[i].get();
            switch (mImpl.getSimpleVariety()) {
                case 3: {
                    constituentMemberTypes.add(mImpl);
                    allSubTypes.add(mImpl);
                    commonBaseType = mImpl.getCommonBaseType(commonBaseType);
                    break;
                }
                case 2: {
                    constituentMemberTypes.addAll(Arrays.asList(mImpl.getUnionConstituentTypes()));
                    allSubTypes.addAll(Arrays.asList(mImpl.getUnionSubTypes()));
                    final SchemaType otherCommonBaseType = mImpl.getUnionCommonBaseType();
                    if (otherCommonBaseType != null) {
                        commonBaseType = otherCommonBaseType.getCommonBaseType(commonBaseType);
                        break;
                    }
                    break;
                }
                case 1: {
                    constituentMemberTypes.add(mImpl);
                    allSubTypes.add(mImpl);
                    commonBaseType = mImpl.getCommonBaseType(commonBaseType);
                    break;
                }
                default: {
                    assert false;
                    break;
                }
            }
        }
        this.setUnionConstituentTypes(constituentMemberTypes.toArray(StscState.EMPTY_ST_ARRAY));
        this.setUnionSubTypes(allSubTypes.toArray(StscState.EMPTY_ST_ARRAY));
        this.setUnionCommonBaseType(commonBaseType);
    }
    
    public QName getSubstitutionGroup() {
        return this._sg;
    }
    
    public void setSubstitutionGroup(final QName sg) {
        this.assertSGResolving();
        this._sg = sg;
    }
    
    public void addSubstitutionGroupMember(final QName member) {
        this.assertSGResolved();
        this._sgMembers.add(member);
    }
    
    public QName[] getSubstitutionGroupMembers() {
        final QName[] result = new QName[this._sgMembers.size()];
        return this._sgMembers.toArray(result);
    }
    
    @Override
    public int getWhiteSpaceRule() {
        return this._whiteSpaceRule;
    }
    
    public void setWhiteSpaceRule(final int ws) {
        this.assertResolving();
        this._whiteSpaceRule = ws;
    }
    
    @Override
    public XmlAnySimpleType getFacet(final int facetCode) {
        if (this._facetArray == null) {
            return null;
        }
        final XmlValueRef ref = this._facetArray[facetCode];
        if (ref == null) {
            return null;
        }
        return ref.get();
    }
    
    @Override
    public boolean isFacetFixed(final int facetCode) {
        return this._fixedFacetArray[facetCode];
    }
    
    public XmlAnySimpleType[] getBasicFacets() {
        final XmlAnySimpleType[] result = new XmlAnySimpleType[12];
        for (int i = 0; i <= 11; ++i) {
            result[i] = this.getFacet(i);
        }
        return result;
    }
    
    public boolean[] getFixedFacets() {
        return boaCopy(this._fixedFacetArray);
    }
    
    public void setBasicFacets(final XmlValueRef[] values, final boolean[] fixed) {
        this.assertResolving();
        this._facetArray = values;
        this._fixedFacetArray = fixed;
    }
    
    @Override
    public int ordered() {
        return this._ordered;
    }
    
    public void setOrdered(final int ordering) {
        this.assertResolving();
        this._ordered = ordering;
    }
    
    @Override
    public boolean isBounded() {
        return this._isBounded;
    }
    
    public void setBounded(final boolean f) {
        this.assertResolving();
        this._isBounded = f;
    }
    
    @Override
    public boolean isFinite() {
        return this._isFinite;
    }
    
    public void setFinite(final boolean f) {
        this.assertResolving();
        this._isFinite = f;
    }
    
    @Override
    public boolean isNumeric() {
        return this._isNumeric;
    }
    
    public void setNumeric(final boolean f) {
        this.assertResolving();
        this._isNumeric = f;
    }
    
    @Override
    public boolean hasPatternFacet() {
        return this._hasPatterns;
    }
    
    public void setPatternFacet(final boolean hasPatterns) {
        this.assertResolving();
        this._hasPatterns = hasPatterns;
    }
    
    @Override
    public boolean matchPatternFacet(final String s) {
        if (!this._hasPatterns) {
            return true;
        }
        if (this._patterns != null && this._patterns.length > 0) {
            int i;
            for (i = 0; i < this._patterns.length && !this._patterns[i].matches(s); ++i) {}
            if (i >= this._patterns.length) {
                return false;
            }
        }
        return this.getBaseType().matchPatternFacet(s);
    }
    
    @Override
    public String[] getPatterns() {
        if (this._patterns == null) {
            return new String[0];
        }
        final String[] patterns = new String[this._patterns.length];
        for (int i = 0; i < this._patterns.length; ++i) {
            patterns[i] = this._patterns[i].getPattern();
        }
        return patterns;
    }
    
    public RegularExpression[] getPatternExpressions() {
        if (this._patterns == null) {
            return new RegularExpression[0];
        }
        final RegularExpression[] result = new RegularExpression[this._patterns.length];
        System.arraycopy(this._patterns, 0, result, 0, this._patterns.length);
        return result;
    }
    
    public void setPatterns(final RegularExpression[] list) {
        this.assertResolving();
        this._patterns = list;
    }
    
    @Override
    public XmlAnySimpleType[] getEnumerationValues() {
        if (this._enumerationValues == null) {
            return null;
        }
        final XmlAnySimpleType[] result = new XmlAnySimpleType[this._enumerationValues.length];
        for (int i = 0; i < result.length; ++i) {
            final XmlValueRef ref = this._enumerationValues[i];
            result[i] = ((ref == null) ? null : ref.get());
        }
        return result;
    }
    
    public void setEnumerationValues(final XmlValueRef[] a) {
        this.assertResolving();
        this._enumerationValues = a;
    }
    
    @Override
    public StringEnumAbstractBase enumForString(final String s) {
        this.ensureStringEnumInfo();
        if (this._lookupStringEnum == null) {
            return null;
        }
        return this._lookupStringEnum.get(s);
    }
    
    @Override
    public StringEnumAbstractBase enumForInt(final int i) {
        this.ensureStringEnumInfo();
        if (this._listOfStringEnum == null || i < 0 || i >= this._listOfStringEnum.size()) {
            return null;
        }
        return this._listOfStringEnum.get(i);
    }
    
    @Override
    public SchemaStringEnumEntry enumEntryForString(final String s) {
        this.ensureStringEnumInfo();
        if (this._lookupStringEnumEntry == null) {
            return null;
        }
        return this._lookupStringEnumEntry.get(s);
    }
    
    @Override
    public SchemaType getBaseEnumType() {
        return (this._baseEnumTyperef == null) ? null : this._baseEnumTyperef.get();
    }
    
    public void setBaseEnumTypeRef(final Ref baseEnumTyperef) {
        this._baseEnumTyperef = baseEnumTyperef;
    }
    
    @Override
    public SchemaStringEnumEntry[] getStringEnumEntries() {
        if (this._stringEnumEntries == null) {
            return null;
        }
        final SchemaStringEnumEntry[] result = new SchemaStringEnumEntry[this._stringEnumEntries.length];
        System.arraycopy(this._stringEnumEntries, 0, result, 0, result.length);
        return result;
    }
    
    public void setStringEnumEntries(final SchemaStringEnumEntry[] sEnums) {
        this.assertJavaizing();
        this._stringEnumEntries = sEnums;
    }
    
    private void ensureStringEnumInfo() {
        if (this._stringEnumEnsured) {
            return;
        }
        final SchemaStringEnumEntry[] sEnums = this._stringEnumEntries;
        if (sEnums == null) {
            this._stringEnumEnsured = true;
            return;
        }
        final Map lookupStringEnum = new HashMap(sEnums.length);
        final List listOfStringEnum = new ArrayList(sEnums.length + 1);
        final Map lookupStringEnumEntry = new HashMap(sEnums.length);
        for (int i = 0; i < sEnums.length; ++i) {
            lookupStringEnumEntry.put(sEnums[i].getString(), sEnums[i]);
        }
        Class jc = this._baseEnumTyperef.get().getEnumJavaClass();
        if (jc != null) {
            try {
                final StringEnumAbstractBase.Table table = (StringEnumAbstractBase.Table)jc.getField("table").get(null);
                for (int j = 0; j < sEnums.length; ++j) {
                    final int k = sEnums[j].getIntValue();
                    final StringEnumAbstractBase enumVal = table.forInt(k);
                    lookupStringEnum.put(sEnums[j].getString(), enumVal);
                    while (listOfStringEnum.size() <= k) {
                        listOfStringEnum.add(null);
                    }
                    listOfStringEnum.set(k, enumVal);
                }
            }
            catch (final Exception e) {
                System.err.println("Something wrong: could not locate enum table for " + jc);
                jc = null;
                lookupStringEnum.clear();
                listOfStringEnum.clear();
            }
        }
        if (jc == null) {
            for (int l = 0; l < sEnums.length; ++l) {
                final int m = sEnums[l].getIntValue();
                final String s = sEnums[l].getString();
                final StringEnumAbstractBase enumVal = new StringEnumValue(s, m);
                lookupStringEnum.put(s, enumVal);
                while (listOfStringEnum.size() <= m) {
                    listOfStringEnum.add(null);
                }
                listOfStringEnum.set(m, enumVal);
            }
        }
        synchronized (this) {
            if (!this._stringEnumEnsured) {
                this._lookupStringEnum = lookupStringEnum;
                this._listOfStringEnum = listOfStringEnum;
                this._lookupStringEnumEntry = lookupStringEnumEntry;
            }
        }
        synchronized (this) {
            this._stringEnumEnsured = true;
        }
    }
    
    @Override
    public boolean hasStringEnumValues() {
        return this._stringEnumEntries != null;
    }
    
    public void copyEnumerationValues(final SchemaTypeImpl baseImpl) {
        this.assertResolving();
        this._enumerationValues = baseImpl._enumerationValues;
        this._baseEnumTyperef = baseImpl._baseEnumTyperef;
    }
    
    @Override
    public int getBuiltinTypeCode() {
        return this._builtinTypeCode;
    }
    
    public void setBuiltinTypeCode(final int builtinTypeCode) {
        this.assertResolving();
        this._builtinTypeCode = builtinTypeCode;
    }
    
    synchronized void assignJavaElementSetterModel() {
        final SchemaProperty[] eltProps = this.getElementProperties();
        final SchemaParticle contentModel = this.getContentModel();
        final Map state = new HashMap();
        final QNameSet allContents = computeAllContainedElements(contentModel, state);
        for (int i = 0; i < eltProps.length; ++i) {
            final SchemaPropertyImpl sImpl = (SchemaPropertyImpl)eltProps[i];
            final QNameSet nde = computeNondelimitingElements(sImpl.getName(), contentModel, state);
            final QNameSetBuilder builder = new QNameSetBuilder(allContents);
            builder.removeAll(nde);
            sImpl.setJavaSetterDelimiter(builder.toQNameSet());
        }
    }
    
    private static QNameSet computeNondelimitingElements(final QName target, final SchemaParticle contentModel, final Map state) {
        final QNameSet allContents = computeAllContainedElements(contentModel, state);
        if (!allContents.contains(target)) {
            return QNameSet.EMPTY;
        }
        if (contentModel.getMaxOccurs() == null || contentModel.getMaxOccurs().compareTo(BigInteger.ONE) > 0) {
            return allContents;
        }
        switch (contentModel.getParticleType()) {
            default: {
                return allContents;
            }
            case 5: {
                return QNameSet.singleton(target);
            }
            case 2: {
                final QNameSetBuilder builder = new QNameSetBuilder();
                for (int i = 0; i < contentModel.countOfParticleChild(); ++i) {
                    final QNameSet childContents = computeAllContainedElements(contentModel.getParticleChild(i), state);
                    if (childContents.contains(target)) {
                        builder.addAll(computeNondelimitingElements(target, contentModel.getParticleChild(i), state));
                    }
                }
                return builder.toQNameSet();
            }
            case 3: {
                final QNameSetBuilder builder = new QNameSetBuilder();
                boolean seenTarget = false;
                int j = contentModel.countOfParticleChild();
                while (j > 0) {
                    --j;
                    final QNameSet childContents2 = computeAllContainedElements(contentModel.getParticleChild(j), state);
                    if (seenTarget) {
                        builder.addAll(childContents2);
                    }
                    else {
                        if (!childContents2.contains(target)) {
                            continue;
                        }
                        builder.addAll(computeNondelimitingElements(target, contentModel.getParticleChild(j), state));
                        seenTarget = true;
                    }
                }
                return builder.toQNameSet();
            }
        }
    }
    
    private static QNameSet computeAllContainedElements(final SchemaParticle contentModel, final Map state) {
        QNameSet result = state.get(contentModel);
        if (result != null) {
            return result;
        }
        switch (contentModel.getParticleType()) {
            default: {
                final QNameSetBuilder builder = new QNameSetBuilder();
                for (int i = 0; i < contentModel.countOfParticleChild(); ++i) {
                    builder.addAll(computeAllContainedElements(contentModel.getParticleChild(i), state));
                }
                result = builder.toQNameSet();
                break;
            }
            case 5: {
                result = contentModel.getWildcardSet();
                break;
            }
            case 4: {
                result = ((SchemaLocalElementImpl)contentModel).acceptedStartNames();
                break;
            }
        }
        state.put(contentModel, result);
        return result;
    }
    
    @Override
    public Class getJavaClass() {
        if (this._javaClass == null && this.getFullJavaName() != null) {
            try {
                this._javaClass = Class.forName(this.getFullJavaName(), false, this.getTypeSystem().getClassLoader());
            }
            catch (final ClassNotFoundException e) {
                this._javaClass = null;
            }
        }
        return this._javaClass;
    }
    
    public Class getJavaImplClass() {
        if (this._implNotAvailable) {
            return null;
        }
        if (this._javaImplClass == null) {
            try {
                if (this.getFullJavaImplName() != null) {
                    this._javaImplClass = Class.forName(this.getFullJavaImplName(), false, this.getTypeSystem().getClassLoader());
                }
                else {
                    this._implNotAvailable = true;
                }
            }
            catch (final ClassNotFoundException e) {
                this._implNotAvailable = true;
            }
        }
        return this._javaImplClass;
    }
    
    public Class getUserTypeClass() {
        if (this._userTypeClass == null && this.getUserTypeName() != null) {
            try {
                this._userTypeClass = Class.forName(this._userTypeName, false, this.getTypeSystem().getClassLoader());
            }
            catch (final ClassNotFoundException e) {
                this._userTypeClass = null;
            }
        }
        return this._userTypeClass;
    }
    
    public Class getUserTypeHandlerClass() {
        if (this._userTypeHandlerClass == null && this.getUserTypeHandlerName() != null) {
            try {
                this._userTypeHandlerClass = Class.forName(this._userTypeHandler, false, this.getTypeSystem().getClassLoader());
            }
            catch (final ClassNotFoundException e) {
                this._userTypeHandlerClass = null;
            }
        }
        return this._userTypeHandlerClass;
    }
    
    public Constructor getJavaImplConstructor() {
        if (this._javaImplConstructor == null && !this._implNotAvailable) {
            final Class impl = this.getJavaImplClass();
            if (impl == null) {
                return null;
            }
            try {
                this._javaImplConstructor = impl.getConstructor(SchemaType.class);
            }
            catch (final NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return this._javaImplConstructor;
    }
    
    public Constructor getJavaImplConstructor2() {
        if (this._javaImplConstructor2 == null && !this._implNotAvailable) {
            final Class impl = this.getJavaImplClass();
            if (impl == null) {
                return null;
            }
            try {
                this._javaImplConstructor2 = impl.getDeclaredConstructor(SchemaType.class, Boolean.TYPE);
            }
            catch (final NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        return this._javaImplConstructor2;
    }
    
    @Override
    public Class getEnumJavaClass() {
        if (this._javaEnumClass == null && this.getBaseEnumType() != null) {
            try {
                this._javaEnumClass = Class.forName(this.getBaseEnumType().getFullJavaName() + "$Enum", false, this.getTypeSystem().getClassLoader());
            }
            catch (final ClassNotFoundException e) {
                this._javaEnumClass = null;
            }
        }
        return this._javaEnumClass;
    }
    
    public void setJavaClass(final Class javaClass) {
        this.assertResolved();
        this._javaClass = javaClass;
        this.setFullJavaName(javaClass.getName());
    }
    
    @Override
    public boolean isPrimitiveType() {
        return this.getBuiltinTypeCode() >= 2 && this.getBuiltinTypeCode() <= 21;
    }
    
    @Override
    public boolean isBuiltinType() {
        return this.getBuiltinTypeCode() != 0;
    }
    
    public XmlObject createUnwrappedNode() {
        final XmlObject result = this.createUnattachedNode(null);
        return result;
    }
    
    @Override
    public TypeStoreUser createTypeStoreUser() {
        return (TypeStoreUser)this.createUnattachedNode(null);
    }
    
    public XmlAnySimpleType newValidatingValue(final Object obj) {
        return this.newValue(obj, true);
    }
    
    @Override
    public XmlAnySimpleType newValue(final Object obj) {
        return this.newValue(obj, false);
    }
    
    public XmlAnySimpleType newValue(final Object obj, final boolean validateOnSet) {
        if (!this.isSimpleType() && this.getContentType() != 2) {
            throw new XmlValueOutOfRangeException();
        }
        final XmlObjectBase result = (XmlObjectBase)this.createUnattachedNode(null);
        if (validateOnSet) {
            result.setValidateOnSet();
        }
        if (obj instanceof XmlObject) {
            result.set_newValue((XmlObject)obj);
        }
        else {
            result.objectSet(obj);
        }
        result.check_dated();
        result.setImmutable();
        return (XmlAnySimpleType)result;
    }
    
    private XmlObject createUnattachedNode(final SchemaProperty prop) {
        XmlObject result = null;
        if (!this.isBuiltinType() && !this.isNoType()) {
            final Constructor ctr = this.getJavaImplConstructor();
            if (ctr != null) {
                try {
                    return ctr.newInstance(this._ctrArgs);
                }
                catch (final Exception e) {
                    System.out.println("Exception trying to instantiate impl class.");
                    e.printStackTrace();
                }
            }
        }
        else {
            result = this.createBuiltinInstance();
        }
        for (SchemaType sType = this; result == null; result = ((SchemaTypeImpl)sType).createUnattachedSubclass(this), sType = sType.getBaseType()) {}
        ((XmlObjectBase)result).init_flags(prop);
        return result;
    }
    
    private XmlObject createUnattachedSubclass(final SchemaType sType) {
        if (!this.isBuiltinType() && !this.isNoType()) {
            final Constructor ctr = this.getJavaImplConstructor2();
            if (ctr != null) {
                final boolean accessible = ctr.isAccessible();
                try {
                    ctr.setAccessible(true);
                    try {
                        return ctr.newInstance(sType, sType.isSimpleType() ? Boolean.FALSE : Boolean.TRUE);
                    }
                    catch (final Exception e) {
                        System.out.println("Exception trying to instantiate impl class.");
                        e.printStackTrace();
                    }
                    finally {
                        try {
                            ctr.setAccessible(accessible);
                        }
                        catch (final SecurityException ex) {}
                    }
                }
                catch (final Exception e) {
                    System.out.println("Exception trying to instantiate impl class.");
                    e.printStackTrace();
                }
            }
            return null;
        }
        return this.createBuiltinSubclass(sType);
    }
    
    private XmlObject createBuiltinInstance() {
        switch (this.getBuiltinTypeCode()) {
            case 0: {
                return new XmlAnyTypeImpl(BuiltinSchemaTypeSystem.ST_NO_TYPE);
            }
            case 1: {
                return new XmlAnyTypeImpl();
            }
            case 2: {
                return new XmlAnySimpleTypeImpl();
            }
            case 3: {
                return new XmlBooleanImpl();
            }
            case 4: {
                return new XmlBase64BinaryImpl();
            }
            case 5: {
                return new XmlHexBinaryImpl();
            }
            case 6: {
                return new XmlAnyUriImpl();
            }
            case 7: {
                return new XmlQNameImpl();
            }
            case 8: {
                return new XmlNotationImpl();
            }
            case 9: {
                return new XmlFloatImpl();
            }
            case 10: {
                return new XmlDoubleImpl();
            }
            case 11: {
                return new XmlDecimalImpl();
            }
            case 12: {
                return new XmlStringImpl();
            }
            case 13: {
                return new XmlDurationImpl();
            }
            case 14: {
                return new XmlDateTimeImpl();
            }
            case 15: {
                return new XmlTimeImpl();
            }
            case 16: {
                return new XmlDateImpl();
            }
            case 17: {
                return new XmlGYearMonthImpl();
            }
            case 18: {
                return new XmlGYearImpl();
            }
            case 19: {
                return new XmlGMonthDayImpl();
            }
            case 20: {
                return new XmlGDayImpl();
            }
            case 21: {
                return new XmlGMonthImpl();
            }
            case 22: {
                return new XmlIntegerImpl();
            }
            case 23: {
                return new XmlLongImpl();
            }
            case 24: {
                return new XmlIntImpl();
            }
            case 25: {
                return new XmlShortImpl();
            }
            case 26: {
                return new XmlByteImpl();
            }
            case 27: {
                return new XmlNonPositiveIntegerImpl();
            }
            case 28: {
                return new XmlNegativeIntegerImpl();
            }
            case 29: {
                return new XmlNonNegativeIntegerImpl();
            }
            case 30: {
                return new XmlPositiveIntegerImpl();
            }
            case 31: {
                return new XmlUnsignedLongImpl();
            }
            case 32: {
                return new XmlUnsignedIntImpl();
            }
            case 33: {
                return new XmlUnsignedShortImpl();
            }
            case 34: {
                return new XmlUnsignedByteImpl();
            }
            case 35: {
                return new XmlNormalizedStringImpl();
            }
            case 36: {
                return new XmlTokenImpl();
            }
            case 37: {
                return new XmlNameImpl();
            }
            case 38: {
                return new XmlNCNameImpl();
            }
            case 39: {
                return new XmlLanguageImpl();
            }
            case 40: {
                return new XmlIdImpl();
            }
            case 41: {
                return new XmlIdRefImpl();
            }
            case 42: {
                return new XmlIdRefsImpl();
            }
            case 43: {
                return new XmlEntityImpl();
            }
            case 44: {
                return new XmlEntitiesImpl();
            }
            case 45: {
                return new XmlNmTokenImpl();
            }
            case 46: {
                return new XmlNmTokensImpl();
            }
            default: {
                throw new IllegalStateException("Unrecognized builtin type: " + this.getBuiltinTypeCode());
            }
        }
    }
    
    private XmlObject createBuiltinSubclass(final SchemaType sType) {
        final boolean complex = !sType.isSimpleType();
        switch (this.getBuiltinTypeCode()) {
            case 0: {
                return new XmlAnyTypeImpl(BuiltinSchemaTypeSystem.ST_NO_TYPE);
            }
            case 1:
            case 2: {
                switch (sType.getSimpleVariety()) {
                    case 0: {
                        return new XmlComplexContentImpl(sType);
                    }
                    case 1: {
                        return new XmlAnySimpleTypeRestriction(sType, complex);
                    }
                    case 3: {
                        return new XmlListImpl(sType, complex);
                    }
                    case 2: {
                        return new XmlUnionImpl(sType, complex);
                    }
                    default: {
                        throw new IllegalStateException();
                    }
                }
                break;
            }
            case 3: {
                return new XmlBooleanRestriction(sType, complex);
            }
            case 4: {
                return new XmlBase64BinaryRestriction(sType, complex);
            }
            case 5: {
                return new XmlHexBinaryRestriction(sType, complex);
            }
            case 6: {
                return new XmlAnyUriRestriction(sType, complex);
            }
            case 7: {
                return new XmlQNameRestriction(sType, complex);
            }
            case 8: {
                return new XmlNotationRestriction(sType, complex);
            }
            case 9: {
                return new XmlFloatRestriction(sType, complex);
            }
            case 10: {
                return new XmlDoubleRestriction(sType, complex);
            }
            case 11: {
                return new XmlDecimalRestriction(sType, complex);
            }
            case 12: {
                if (sType.hasStringEnumValues()) {
                    return new XmlStringEnumeration(sType, complex);
                }
                return new XmlStringRestriction(sType, complex);
            }
            case 13: {
                return new XmlDurationImpl(sType, complex);
            }
            case 14: {
                return new XmlDateTimeImpl(sType, complex);
            }
            case 15: {
                return new XmlTimeImpl(sType, complex);
            }
            case 16: {
                return new XmlDateImpl(sType, complex);
            }
            case 17: {
                return new XmlGYearMonthImpl(sType, complex);
            }
            case 18: {
                return new XmlGYearImpl(sType, complex);
            }
            case 19: {
                return new XmlGMonthDayImpl(sType, complex);
            }
            case 20: {
                return new XmlGDayImpl(sType, complex);
            }
            case 21: {
                return new XmlGMonthImpl(sType, complex);
            }
            case 22: {
                return new XmlIntegerRestriction(sType, complex);
            }
            case 23: {
                return new XmlLongRestriction(sType, complex);
            }
            case 24: {
                return new XmlIntRestriction(sType, complex);
            }
            case 25: {
                return new XmlShortImpl(sType, complex);
            }
            case 26: {
                return new XmlByteImpl(sType, complex);
            }
            case 27: {
                return new XmlNonPositiveIntegerImpl(sType, complex);
            }
            case 28: {
                return new XmlNegativeIntegerImpl(sType, complex);
            }
            case 29: {
                return new XmlNonNegativeIntegerImpl(sType, complex);
            }
            case 30: {
                return new XmlPositiveIntegerImpl(sType, complex);
            }
            case 31: {
                return new XmlUnsignedLongImpl(sType, complex);
            }
            case 32: {
                return new XmlUnsignedIntImpl(sType, complex);
            }
            case 33: {
                return new XmlUnsignedShortImpl(sType, complex);
            }
            case 34: {
                return new XmlUnsignedByteImpl(sType, complex);
            }
            case 35: {
                return new XmlNormalizedStringImpl(sType, complex);
            }
            case 36: {
                return new XmlTokenImpl(sType, complex);
            }
            case 37: {
                return new XmlNameImpl(sType, complex);
            }
            case 38: {
                return new XmlNCNameImpl(sType, complex);
            }
            case 39: {
                return new XmlLanguageImpl(sType, complex);
            }
            case 40: {
                return new XmlIdImpl(sType, complex);
            }
            case 41: {
                return new XmlIdRefImpl(sType, complex);
            }
            case 42: {
                return new XmlIdRefsImpl(sType, complex);
            }
            case 43: {
                return new XmlEntityImpl(sType, complex);
            }
            case 44: {
                return new XmlEntitiesImpl(sType, complex);
            }
            case 45: {
                return new XmlNmTokenImpl(sType, complex);
            }
            case 46: {
                return new XmlNmTokensImpl(sType, complex);
            }
            default: {
                throw new IllegalStateException("Unrecognized builtin type: " + this.getBuiltinTypeCode());
            }
        }
    }
    
    @Override
    public SchemaType getCommonBaseType(final SchemaType type) {
        if (this == BuiltinSchemaTypeSystem.ST_ANY_TYPE || type == null || type.isNoType()) {
            return this;
        }
        if (type == BuiltinSchemaTypeSystem.ST_ANY_TYPE || this.isNoType()) {
            return type;
        }
        SchemaTypeImpl sImpl1;
        for (sImpl1 = (SchemaTypeImpl)type; sImpl1.getBaseDepth() > this.getBaseDepth(); sImpl1 = (SchemaTypeImpl)sImpl1.getBaseType()) {}
        SchemaTypeImpl sImpl2;
        for (sImpl2 = this; sImpl2.getBaseDepth() > sImpl1.getBaseDepth(); sImpl2 = (SchemaTypeImpl)sImpl2.getBaseType()) {}
        while (!sImpl1.equals(sImpl2)) {
            sImpl1 = (SchemaTypeImpl)sImpl1.getBaseType();
            sImpl2 = (SchemaTypeImpl)sImpl2.getBaseType();
            assert sImpl1 != null && sImpl2 != null;
        }
        return sImpl1;
    }
    
    @Override
    public boolean isAssignableFrom(SchemaType type) {
        if (type == null || type.isNoType()) {
            return true;
        }
        if (this.isNoType()) {
            return false;
        }
        if (this.getSimpleVariety() == 2) {
            final SchemaType[] members = this.getUnionMemberTypes();
            for (int i = 0; i < members.length; ++i) {
                if (members[i].isAssignableFrom(type)) {
                    return true;
                }
            }
        }
        int depth = ((SchemaTypeImpl)type).getBaseDepth() - this.getBaseDepth();
        if (depth < 0) {
            return false;
        }
        while (depth > 0) {
            type = type.getBaseType();
            --depth;
        }
        return type != null && type.equals(this);
    }
    
    @Override
    public String toString() {
        if (this.getName() != null) {
            return "T=" + QNameHelper.pretty(this.getName());
        }
        if (this.isDocumentType()) {
            return "D=" + QNameHelper.pretty(this.getDocumentElementName());
        }
        if (this.isAttributeType()) {
            return "R=" + QNameHelper.pretty(this.getAttributeTypeAttributeName());
        }
        String prefix;
        if (this.getContainerField() != null) {
            prefix = ((this.getContainerField().getName().getNamespaceURI().length() > 0) ? (this.getContainerField().isAttribute() ? "Q=" : "E=") : (this.getContainerField().isAttribute() ? "A=" : "U=")) + this.getContainerField().getName().getLocalPart();
            if (this.getOuterType() == null) {
                return prefix + "@" + this.getContainerField().getName().getNamespaceURI();
            }
        }
        else {
            if (this.isNoType()) {
                return "N=";
            }
            if (this.getOuterType() == null) {
                return "noouter";
            }
            if (this.getOuterType().getBaseType() == this) {
                prefix = "B=";
            }
            else if (this.getOuterType().getContentBasedOnType() == this) {
                prefix = "S=";
            }
            else if (this.getOuterType().getSimpleVariety() == 3) {
                prefix = "I=";
            }
            else if (this.getOuterType().getSimpleVariety() == 2) {
                prefix = "M=" + this.getAnonymousUnionMemberOrdinal();
            }
            else {
                prefix = "strange=";
            }
        }
        return prefix + "|" + this.getOuterType().toString();
    }
    
    public void setParseContext(final XmlObject parseObject, final String targetNamespace, final boolean chameleon, final String elemFormDefault, final String attFormDefault, final boolean redefinition) {
        this._parseObject = parseObject;
        this._parseTNS = targetNamespace;
        this._chameleon = chameleon;
        this._elemFormDefault = elemFormDefault;
        this._attFormDefault = attFormDefault;
        this._redefinition = redefinition;
    }
    
    public XmlObject getParseObject() {
        return this._parseObject;
    }
    
    public String getTargetNamespace() {
        return this._parseTNS;
    }
    
    public boolean isChameleon() {
        return this._chameleon;
    }
    
    public String getElemFormDefault() {
        return this._elemFormDefault;
    }
    
    public String getAttFormDefault() {
        return this._attFormDefault;
    }
    
    public String getChameleonNamespace() {
        return this._chameleon ? this._parseTNS : null;
    }
    
    public boolean isRedefinition() {
        return this._redefinition;
    }
    
    @Override
    public Ref getRef() {
        return this._selfref;
    }
    
    @Override
    public SchemaComponent.Ref getComponentRef() {
        return this.getRef();
    }
    
    @Override
    public QNameSet qnameSetForWildcardElements() {
        final SchemaParticle model = this.getContentModel();
        final QNameSetBuilder wildcardSet = new QNameSetBuilder();
        computeWildcardSet(model, wildcardSet);
        final QNameSetBuilder qnsb = new QNameSetBuilder(wildcardSet);
        final SchemaProperty[] props = this.getElementProperties();
        for (int i = 0; i < props.length; ++i) {
            final SchemaProperty prop = props[i];
            qnsb.remove(prop.getName());
        }
        return qnsb.toQNameSet();
    }
    
    private static void computeWildcardSet(final SchemaParticle model, final QNameSetBuilder result) {
        if (model.getParticleType() == 5) {
            final QNameSet cws = model.getWildcardSet();
            result.addAll(cws);
            return;
        }
        for (int i = 0; i < model.countOfParticleChild(); ++i) {
            final SchemaParticle child = model.getParticleChild(i);
            computeWildcardSet(child, result);
        }
    }
    
    @Override
    public QNameSet qnameSetForWildcardAttributes() {
        final SchemaAttributeModel model = this.getAttributeModel();
        final QNameSet wildcardSet = model.getWildcardSet();
        if (wildcardSet == null) {
            return QNameSet.EMPTY;
        }
        final QNameSetBuilder qnsb = new QNameSetBuilder(wildcardSet);
        final SchemaProperty[] props = this.getAttributeProperties();
        for (int i = 0; i < props.length; ++i) {
            final SchemaProperty prop = props[i];
            qnsb.remove(prop.getName());
        }
        return qnsb.toQNameSet();
    }
    
    static {
        NO_PROPERTIES = new SchemaProperty[0];
    }
    
    private static class SequencerImpl implements SchemaTypeElementSequencer
    {
        private SchemaTypeVisitorImpl _visitor;
        
        private SequencerImpl(final SchemaTypeVisitorImpl visitor) {
            this._visitor = visitor;
        }
        
        @Override
        public boolean next(final QName elementName) {
            return this._visitor != null && this._visitor.visit(elementName);
        }
        
        @Override
        public boolean peek(final QName elementName) {
            return this._visitor != null && this._visitor.testValid(elementName);
        }
    }
}
