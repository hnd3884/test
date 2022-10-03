package org.eclipse.jdt.internal.compiler.apt.model;

import javax.lang.model.element.Name;
import javax.lang.model.element.ElementKind;
import org.eclipse.jdt.internal.compiler.env.INameEnvironment;
import org.eclipse.jdt.internal.compiler.lookup.LookupEnvironment;
import java.util.Collections;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashSet;
import org.eclipse.jdt.internal.compiler.batch.FileSystem;
import javax.lang.model.element.Element;
import java.util.List;
import org.eclipse.jdt.internal.compiler.lookup.ReferenceBinding;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.lookup.TypeConstants;
import org.eclipse.jdt.internal.compiler.lookup.AnnotationBinding;
import javax.lang.model.element.ElementVisitor;
import org.eclipse.jdt.internal.compiler.lookup.Binding;
import org.eclipse.jdt.internal.compiler.lookup.PackageBinding;
import org.eclipse.jdt.internal.compiler.apt.dispatch.BaseProcessingEnvImpl;
import javax.lang.model.element.PackageElement;

public class PackageElementImpl extends ElementImpl implements PackageElement
{
    PackageElementImpl(final BaseProcessingEnvImpl env, final PackageBinding binding) {
        super(env, binding);
    }
    
    @Override
    public <R, P> R accept(final ElementVisitor<R, P> v, final P p) {
        return v.visitPackage(this, p);
    }
    
    @Override
    protected AnnotationBinding[] getAnnotationBindings() {
        final PackageBinding packageBinding = (PackageBinding)this._binding;
        final char[][] compoundName = CharOperation.arrayConcat(packageBinding.compoundName, TypeConstants.PACKAGE_INFO_NAME);
        final ReferenceBinding type = this._env.getLookupEnvironment().getType(compoundName);
        AnnotationBinding[] annotations = null;
        if (type != null && type.isValidBinding()) {
            annotations = type.getAnnotations();
        }
        return annotations;
    }
    
    @Override
    public List<? extends Element> getEnclosedElements() {
        final PackageBinding binding = (PackageBinding)this._binding;
        final LookupEnvironment environment = binding.environment;
        char[][][] typeNames = null;
        final INameEnvironment nameEnvironment = binding.environment.nameEnvironment;
        if (nameEnvironment instanceof FileSystem) {
            typeNames = ((FileSystem)nameEnvironment).findTypeNames(binding.compoundName);
        }
        final HashSet<Element> set = new HashSet<Element>();
        if (typeNames != null) {
            char[][][] array;
            for (int length = (array = typeNames).length, i = 0; i < length; ++i) {
                final char[][] typeName = array[i];
                final ReferenceBinding type = environment.getType(typeName);
                if (type != null && type.isValidBinding()) {
                    set.add(this._env.getFactory().newElement(type));
                }
            }
        }
        final ArrayList<Element> list = new ArrayList<Element>(set.size());
        list.addAll(set);
        return Collections.unmodifiableList((List<? extends Element>)list);
    }
    
    @Override
    public Element getEnclosingElement() {
        return null;
    }
    
    @Override
    public ElementKind getKind() {
        return ElementKind.PACKAGE;
    }
    
    @Override
    PackageElement getPackage() {
        return this;
    }
    
    @Override
    public Name getSimpleName() {
        final char[][] compoundName = ((PackageBinding)this._binding).compoundName;
        final int length = compoundName.length;
        if (length == 0) {
            return new NameImpl(CharOperation.NO_CHAR);
        }
        return new NameImpl(compoundName[length - 1]);
    }
    
    @Override
    public Name getQualifiedName() {
        return new NameImpl(CharOperation.concatWith(((PackageBinding)this._binding).compoundName, '.'));
    }
    
    @Override
    public boolean isUnnamed() {
        final PackageBinding binding = (PackageBinding)this._binding;
        return binding.compoundName == CharOperation.NO_CHAR_CHAR;
    }
}
