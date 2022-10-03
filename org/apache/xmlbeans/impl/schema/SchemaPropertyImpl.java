package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.XmlAnySimpleType;
import java.util.Set;
import org.apache.xmlbeans.QNameSet;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.SchemaProperty;

public class SchemaPropertyImpl implements SchemaProperty
{
    private QName _name;
    private SchemaType.Ref _typeref;
    private boolean _isAttribute;
    private SchemaType.Ref _containerTypeRef;
    private String _javaPropertyName;
    private BigInteger _minOccurs;
    private BigInteger _maxOccurs;
    private int _hasNillable;
    private int _hasDefault;
    private int _hasFixed;
    private String _defaultText;
    private boolean _isImmutable;
    private SchemaType.Ref _javaBasedOnTypeRef;
    private boolean _extendsSingleton;
    private boolean _extendsArray;
    private boolean _extendsOption;
    private int _javaTypeCode;
    private QNameSet _javaSetterDelimiter;
    private XmlValueRef _defaultValue;
    private Set _acceptedNames;
    
    private void mutate() {
        if (this._isImmutable) {
            throw new IllegalStateException();
        }
    }
    
    public void setImmutable() {
        this.mutate();
        this._isImmutable = true;
    }
    
    @Override
    public SchemaType getContainerType() {
        return this._containerTypeRef.get();
    }
    
    public void setContainerTypeRef(final SchemaType.Ref typeref) {
        this.mutate();
        this._containerTypeRef = typeref;
    }
    
    @Override
    public QName getName() {
        return this._name;
    }
    
    public void setName(final QName name) {
        this.mutate();
        this._name = name;
    }
    
    @Override
    public String getJavaPropertyName() {
        return this._javaPropertyName;
    }
    
    public void setJavaPropertyName(final String name) {
        this.mutate();
        this._javaPropertyName = name;
    }
    
    @Override
    public boolean isAttribute() {
        return this._isAttribute;
    }
    
    public void setAttribute(final boolean isAttribute) {
        this.mutate();
        this._isAttribute = isAttribute;
    }
    
    @Override
    public boolean isReadOnly() {
        return false;
    }
    
    @Override
    public SchemaType getType() {
        return this._typeref.get();
    }
    
    public void setTypeRef(final SchemaType.Ref typeref) {
        this.mutate();
        this._typeref = typeref;
    }
    
    @Override
    public SchemaType javaBasedOnType() {
        return (this._javaBasedOnTypeRef == null) ? null : this._javaBasedOnTypeRef.get();
    }
    
    @Override
    public boolean extendsJavaSingleton() {
        return this._extendsSingleton;
    }
    
    @Override
    public boolean extendsJavaArray() {
        return this._extendsArray;
    }
    
    @Override
    public boolean extendsJavaOption() {
        return this._extendsOption;
    }
    
    public void setExtendsJava(final SchemaType.Ref javaBasedOnTypeRef, final boolean singleton, final boolean option, final boolean array) {
        this.mutate();
        this._javaBasedOnTypeRef = javaBasedOnTypeRef;
        this._extendsSingleton = singleton;
        this._extendsOption = option;
        this._extendsArray = array;
    }
    
    @Override
    public QNameSet getJavaSetterDelimiter() {
        if (this._isAttribute) {
            return QNameSet.EMPTY;
        }
        if (this._javaSetterDelimiter == null) {
            ((SchemaTypeImpl)this.getContainerType()).assignJavaElementSetterModel();
        }
        assert this._javaSetterDelimiter != null;
        return this._javaSetterDelimiter;
    }
    
    void setJavaSetterDelimiter(final QNameSet set) {
        this._javaSetterDelimiter = set;
    }
    
    @Override
    public QName[] acceptedNames() {
        if (this._acceptedNames == null) {
            return new QName[] { this._name };
        }
        return this._acceptedNames.toArray(new QName[this._acceptedNames.size()]);
    }
    
    public void setAcceptedNames(final Set set) {
        this.mutate();
        this._acceptedNames = set;
    }
    
    public void setAcceptedNames(final QNameSet set) {
        this.mutate();
        this._acceptedNames = set.includedQNamesInExcludedURIs();
    }
    
    @Override
    public BigInteger getMinOccurs() {
        return this._minOccurs;
    }
    
    public void setMinOccurs(final BigInteger min) {
        this.mutate();
        this._minOccurs = min;
    }
    
    @Override
    public BigInteger getMaxOccurs() {
        return this._maxOccurs;
    }
    
    public void setMaxOccurs(final BigInteger max) {
        this.mutate();
        this._maxOccurs = max;
    }
    
    @Override
    public int hasNillable() {
        return this._hasNillable;
    }
    
    public void setNillable(final int when) {
        this.mutate();
        this._hasNillable = when;
    }
    
    @Override
    public int hasDefault() {
        return this._hasDefault;
    }
    
    public void setDefault(final int when) {
        this.mutate();
        this._hasDefault = when;
    }
    
    @Override
    public int hasFixed() {
        return this._hasFixed;
    }
    
    public void setFixed(final int when) {
        this.mutate();
        this._hasFixed = when;
    }
    
    @Override
    public String getDefaultText() {
        return this._defaultText;
    }
    
    public void setDefaultText(final String val) {
        this.mutate();
        this._defaultText = val;
    }
    
    @Override
    public XmlAnySimpleType getDefaultValue() {
        if (this._defaultValue != null) {
            return this._defaultValue.get();
        }
        return null;
    }
    
    public void setDefaultValue(final XmlValueRef defaultRef) {
        this.mutate();
        this._defaultValue = defaultRef;
    }
    
    @Override
    public int getJavaTypeCode() {
        return this._javaTypeCode;
    }
    
    public void setJavaTypeCode(final int code) {
        this.mutate();
        this._javaTypeCode = code;
    }
}
