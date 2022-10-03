package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.impl.values.NamespaceContext;
import org.apache.xmlbeans.XmlQName;
import org.apache.xmlbeans.XmlAnySimpleType;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.SchemaType;
import javax.xml.namespace.QName;
import org.apache.xmlbeans.QNameSet;
import java.math.BigInteger;
import org.apache.xmlbeans.SchemaParticle;

public class SchemaParticleImpl implements SchemaParticle
{
    private int _particleType;
    private BigInteger _minOccurs;
    private BigInteger _maxOccurs;
    private SchemaParticle[] _particleChildren;
    private boolean _isImmutable;
    private QNameSet _startSet;
    private QNameSet _excludeNextSet;
    private boolean _isSkippable;
    private boolean _isDeterministic;
    private int _intMinOccurs;
    private int _intMaxOccurs;
    private QNameSet _wildcardSet;
    private int _wildcardProcess;
    private String _defaultText;
    private boolean _isDefault;
    private boolean _isFixed;
    private QName _qName;
    private boolean _isNillable;
    private SchemaType.Ref _typeref;
    protected XmlObject _parseObject;
    private Object _userData;
    private XmlValueRef _defaultValue;
    private static final BigInteger _maxint;
    
    protected void mutate() {
        if (this._isImmutable) {
            throw new IllegalStateException();
        }
    }
    
    public void setImmutable() {
        this.mutate();
        this._isImmutable = true;
    }
    
    public boolean hasTransitionRules() {
        return this._startSet != null;
    }
    
    public boolean hasTransitionNotes() {
        return this._excludeNextSet != null;
    }
    
    public void setTransitionRules(final QNameSet start, final boolean isSkippable) {
        this._startSet = start;
        this._isSkippable = isSkippable;
    }
    
    public void setTransitionNotes(final QNameSet excludeNext, final boolean isDeterministic) {
        this._excludeNextSet = excludeNext;
        this._isDeterministic = isDeterministic;
    }
    
    @Override
    public boolean canStartWithElement(final QName name) {
        return name != null && this._startSet.contains(name);
    }
    
    @Override
    public QNameSet acceptedStartNames() {
        return this._startSet;
    }
    
    public QNameSet getExcludeNextSet() {
        return this._excludeNextSet;
    }
    
    @Override
    public boolean isSkippable() {
        return this._isSkippable;
    }
    
    public boolean isDeterministic() {
        return this._isDeterministic;
    }
    
    @Override
    public int getParticleType() {
        return this._particleType;
    }
    
    public void setParticleType(final int pType) {
        this.mutate();
        this._particleType = pType;
    }
    
    @Override
    public boolean isSingleton() {
        return this._maxOccurs != null && this._maxOccurs.compareTo(BigInteger.ONE) == 0 && this._minOccurs.compareTo(BigInteger.ONE) == 0;
    }
    
    @Override
    public BigInteger getMinOccurs() {
        return this._minOccurs;
    }
    
    public void setMinOccurs(final BigInteger min) {
        this.mutate();
        this._minOccurs = min;
        this._intMinOccurs = pegBigInteger(min);
    }
    
    @Override
    public int getIntMinOccurs() {
        return this._intMinOccurs;
    }
    
    @Override
    public BigInteger getMaxOccurs() {
        return this._maxOccurs;
    }
    
    @Override
    public int getIntMaxOccurs() {
        return this._intMaxOccurs;
    }
    
    public void setMaxOccurs(final BigInteger max) {
        this.mutate();
        this._maxOccurs = max;
        this._intMaxOccurs = pegBigInteger(max);
    }
    
    @Override
    public SchemaParticle[] getParticleChildren() {
        if (this._particleChildren != null) {
            final SchemaParticle[] result = new SchemaParticle[this._particleChildren.length];
            System.arraycopy(this._particleChildren, 0, result, 0, this._particleChildren.length);
            return result;
        }
        assert this._particleType != 1 && this._particleType != 3 && this._particleType != 2;
        return null;
    }
    
    public void setParticleChildren(final SchemaParticle[] children) {
        this.mutate();
        this._particleChildren = children;
    }
    
    @Override
    public SchemaParticle getParticleChild(final int i) {
        return this._particleChildren[i];
    }
    
    @Override
    public int countOfParticleChild() {
        return (this._particleChildren == null) ? 0 : this._particleChildren.length;
    }
    
    public void setWildcardSet(final QNameSet set) {
        this.mutate();
        this._wildcardSet = set;
    }
    
    @Override
    public QNameSet getWildcardSet() {
        return this._wildcardSet;
    }
    
    public void setWildcardProcess(final int process) {
        this.mutate();
        this._wildcardProcess = process;
    }
    
    @Override
    public int getWildcardProcess() {
        return this._wildcardProcess;
    }
    
    private static final int pegBigInteger(final BigInteger bi) {
        if (bi == null) {
            return Integer.MAX_VALUE;
        }
        if (bi.signum() <= 0) {
            return 0;
        }
        if (bi.compareTo(SchemaParticleImpl._maxint) >= 0) {
            return Integer.MAX_VALUE;
        }
        return bi.intValue();
    }
    
    @Override
    public QName getName() {
        return this._qName;
    }
    
    public void setNameAndTypeRef(final QName formname, final SchemaType.Ref typeref) {
        this.mutate();
        this._qName = formname;
        this._typeref = typeref;
    }
    
    public boolean isTypeResolved() {
        return this._typeref != null;
    }
    
    public void resolveTypeRef(final SchemaType.Ref typeref) {
        if (this._typeref != null) {
            throw new IllegalStateException();
        }
        this._typeref = typeref;
    }
    
    public boolean isAttribute() {
        return false;
    }
    
    @Override
    public SchemaType getType() {
        if (this._typeref == null) {
            return null;
        }
        return this._typeref.get();
    }
    
    @Override
    public String getDefaultText() {
        return this._defaultText;
    }
    
    @Override
    public boolean isDefault() {
        return this._isDefault;
    }
    
    @Override
    public boolean isFixed() {
        return this._isFixed;
    }
    
    public void setDefault(final String deftext, final boolean isFixed, final XmlObject parseObject) {
        this.mutate();
        this._defaultText = deftext;
        this._isDefault = (deftext != null);
        this._isFixed = isFixed;
        this._parseObject = parseObject;
    }
    
    @Override
    public boolean isNillable() {
        return this._isNillable;
    }
    
    public void setNillable(final boolean nillable) {
        this.mutate();
        this._isNillable = nillable;
    }
    
    @Override
    public XmlAnySimpleType getDefaultValue() {
        if (this._defaultValue != null) {
            return this._defaultValue.get();
        }
        if (this._defaultText != null && XmlAnySimpleType.type.isAssignableFrom(this.getType())) {
            if (this._parseObject != null && XmlQName.type.isAssignableFrom(this.getType())) {
                try {
                    NamespaceContext.push(new NamespaceContext(this._parseObject));
                    return this.getType().newValue(this._defaultText);
                }
                finally {
                    NamespaceContext.pop();
                }
            }
            return this.getType().newValue(this._defaultText);
        }
        return null;
    }
    
    public void setDefaultValue(final XmlValueRef defaultRef) {
        this.mutate();
        this._defaultValue = defaultRef;
    }
    
    public Object getUserData() {
        return this._userData;
    }
    
    public void setUserData(final Object data) {
        this._userData = data;
    }
    
    static {
        _maxint = BigInteger.valueOf(2147483647L);
    }
}
