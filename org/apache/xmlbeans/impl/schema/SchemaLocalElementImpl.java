package org.apache.xmlbeans.impl.schema;

import org.apache.xmlbeans.SchemaIdentityConstraint;
import org.apache.xmlbeans.soap.SOAPArrayType;
import org.apache.xmlbeans.SchemaAnnotation;
import org.apache.xmlbeans.soap.SchemaWSDLArrayType;
import org.apache.xmlbeans.SchemaLocalElement;

public class SchemaLocalElementImpl extends SchemaParticleImpl implements SchemaLocalElement, SchemaWSDLArrayType
{
    private boolean _blockExt;
    private boolean _blockRest;
    private boolean _blockSubst;
    protected boolean _abs;
    private SchemaAnnotation _annotation;
    private SOAPArrayType _wsdlArrayType;
    private SchemaIdentityConstraint.Ref[] _constraints;
    
    public SchemaLocalElementImpl() {
        this._constraints = new SchemaIdentityConstraint.Ref[0];
        this.setParticleType(4);
    }
    
    @Override
    public boolean blockExtension() {
        return this._blockExt;
    }
    
    @Override
    public boolean blockRestriction() {
        return this._blockRest;
    }
    
    @Override
    public boolean blockSubstitution() {
        return this._blockSubst;
    }
    
    @Override
    public boolean isAbstract() {
        return this._abs;
    }
    
    public void setAbstract(final boolean abs) {
        this._abs = abs;
    }
    
    public void setBlock(final boolean extension, final boolean restriction, final boolean substitution) {
        this.mutate();
        this._blockExt = extension;
        this._blockRest = restriction;
        this._blockSubst = substitution;
    }
    
    public void setAnnotation(final SchemaAnnotation ann) {
        this._annotation = ann;
    }
    
    public void setWsdlArrayType(final SOAPArrayType arrayType) {
        this._wsdlArrayType = arrayType;
    }
    
    @Override
    public SchemaAnnotation getAnnotation() {
        return this._annotation;
    }
    
    @Override
    public SOAPArrayType getWSDLArrayType() {
        return this._wsdlArrayType;
    }
    
    public void setIdentityConstraints(final SchemaIdentityConstraint.Ref[] constraints) {
        this.mutate();
        this._constraints = constraints;
    }
    
    @Override
    public SchemaIdentityConstraint[] getIdentityConstraints() {
        final SchemaIdentityConstraint[] result = new SchemaIdentityConstraint[this._constraints.length];
        for (int i = 0; i < result.length; ++i) {
            result[i] = this._constraints[i].get();
        }
        return result;
    }
    
    public SchemaIdentityConstraint.Ref[] getIdentityConstraintRefs() {
        final SchemaIdentityConstraint.Ref[] result = new SchemaIdentityConstraint.Ref[this._constraints.length];
        System.arraycopy(this._constraints, 0, result, 0, result.length);
        return result;
    }
}
