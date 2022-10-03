package org.eclipse.jdt.internal.compiler.apt.model;

import org.eclipse.jdt.core.compiler.CharOperation;
import javax.lang.model.element.Name;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.Modifier;
import java.util.Set;
import javax.lang.model.element.ElementKind;
import org.eclipse.jdt.internal.compiler.lookup.AptBinaryLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.AptSourceLocalVariableBinding;
import org.eclipse.jdt.internal.compiler.lookup.FieldBinding;
import java.util.Collections;
import javax.lang.model.element.Element;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.TypeBinding;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import javax.lang.model.element.ElementVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.VariableBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.element.VariableElement;

public class VariableElementImpl extends ElementImpl implements VariableElement
{
    VariableElementImpl(final BaseProcessingEnvImpl env, final VariableBinding binding) {
        super(env, binding);
    }
    
    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitVariable(this, p);
    }
    
    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        return ((VariableBinding)this._binding).getAnnotations();
    }
    
    @Override
    public Object getConstantValue() {
        final VariableBinding variableBinding = (VariableBinding)this._binding;
        final Constant constant = variableBinding.constant();
        if (constant == null || constant == Constant.NotAConstant) {
            return null;
        }
        final TypeBinding type = variableBinding.type;
        switch (type.id) {
            case 5: {
                return constant.booleanValue();
            }
            case 3: {
                return constant.byteValue();
            }
            case 2: {
                return constant.charValue();
            }
            case 8: {
                return constant.doubleValue();
            }
            case 9: {
                return constant.floatValue();
            }
            case 10: {
                return constant.intValue();
            }
            case 11: {
                return constant.stringValue();
            }
            case 7: {
                return constant.longValue();
            }
            case 4: {
                return constant.shortValue();
            }
            default: {
                return null;
            }
        }
    }
    
    @Override
    public List<? extends Element> getEnclosedElements() {
        return Collections.emptyList();
    }
    
    @Override
    public Element getEnclosingElement() {
        if (this._binding instanceof FieldBinding) {
            return this._env.getFactory().newElement(((FieldBinding)this._binding).declaringClass);
        }
        if (this._binding instanceof AptSourceLocalVariableBinding) {
            return this._env.getFactory().newElement(((AptSourceLocalVariableBinding)this._binding).methodBinding);
        }
        if (this._binding instanceof AptBinaryLocalVariableBinding) {
            return this._env.getFactory().newElement(((AptBinaryLocalVariableBinding)this._binding).methodBinding);
        }
        return null;
    }
    
    @Override
    public ElementKind getKind() {
        if (!(this._binding instanceof FieldBinding)) {
            return ElementKind.PARAMETER;
        }
        if ((((FieldBinding)this._binding).modifiers & 0x4000) != 0x0) {
            return ElementKind.ENUM_CONSTANT;
        }
        return ElementKind.FIELD;
    }
    
    @Override
    public Set<Modifier> getModifiers() {
        if (this._binding instanceof VariableBinding) {
            return Factory.getModifiers(((VariableBinding)this._binding).modifiers, this.getKind());
        }
        return Collections.emptySet();
    }
    
    @Override
    PackageElement getPackage() {
        if (this._binding instanceof FieldBinding) {
            final PackageBinding pkgBinding = ((FieldBinding)this._binding).declaringClass.fPackage;
            return this._env.getFactory().newPackageElement(pkgBinding);
        }
        throw new UnsupportedOperationException("NYI: VariableElmentImpl.getPackage() for method parameter");
    }
    
    @Override
    public Name getSimpleName() {
        return new NameImpl(((VariableBinding)this._binding).name);
    }
    
    @Override
    public boolean hides(final Element hiddenElement) {
        if (!(this._binding instanceof FieldBinding)) {
            return false;
        }
        if (!(((ElementImpl)hiddenElement)._binding instanceof FieldBinding)) {
            return false;
        }
        final FieldBinding hidden = (FieldBinding)((ElementImpl)hiddenElement)._binding;
        if (hidden.isPrivate()) {
            return false;
        }
        final FieldBinding hider = (FieldBinding)this._binding;
        return hidden != hider && CharOperation.equals(hider.name, hidden.name) && hider.declaringClass.findSuperTypeOriginatingFrom(hidden.declaringClass) != null;
    }
    
    @Override
    public String toString() {
        return new String(((VariableBinding)this._binding).name);
    }
}
