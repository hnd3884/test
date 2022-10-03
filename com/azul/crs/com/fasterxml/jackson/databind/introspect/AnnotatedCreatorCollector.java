package com.azul.crs.com.fasterxml.jackson.databind.introspect;

import java.lang.reflect.Constructor;
import java.lang.reflect.AnnotatedElement;
import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import java.lang.reflect.Method;
import java.util.Collections;
import java.util.ArrayList;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeFactory;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;

final class AnnotatedCreatorCollector extends CollectorBase
{
    private final TypeResolutionContext _typeContext;
    private final boolean _collectAnnotations;
    private AnnotatedConstructor _defaultConstructor;
    
    AnnotatedCreatorCollector(final AnnotationIntrospector intr, final TypeResolutionContext tc, final boolean collectAnnotations) {
        super(intr);
        this._typeContext = tc;
        this._collectAnnotations = collectAnnotations;
    }
    
    public static AnnotatedClass.Creators collectCreators(final AnnotationIntrospector intr, final TypeFactory typeFactory, final TypeResolutionContext tc, final JavaType type, final Class<?> primaryMixIn, boolean collectAnnotations) {
        collectAnnotations |= (primaryMixIn != null);
        return new AnnotatedCreatorCollector(intr, tc, collectAnnotations).collect(typeFactory, type, primaryMixIn);
    }
    
    AnnotatedClass.Creators collect(final TypeFactory typeFactory, final JavaType type, final Class<?> primaryMixIn) {
        final List<AnnotatedConstructor> constructors = this._findPotentialConstructors(type, primaryMixIn);
        final List<AnnotatedMethod> factories = this._findPotentialFactories(typeFactory, type, primaryMixIn);
        if (this._collectAnnotations) {
            if (this._defaultConstructor != null && this._intr.hasIgnoreMarker(this._defaultConstructor)) {
                this._defaultConstructor = null;
            }
            int i = constructors.size();
            while (--i >= 0) {
                if (this._intr.hasIgnoreMarker(constructors.get(i))) {
                    constructors.remove(i);
                }
            }
            i = factories.size();
            while (--i >= 0) {
                if (this._intr.hasIgnoreMarker(factories.get(i))) {
                    factories.remove(i);
                }
            }
        }
        return new AnnotatedClass.Creators(this._defaultConstructor, constructors, factories);
    }
    
    private List<AnnotatedConstructor> _findPotentialConstructors(final JavaType type, final Class<?> primaryMixIn) {
        ClassUtil.Ctor defaultCtor = null;
        List<ClassUtil.Ctor> ctors = null;
        if (!type.isEnumType()) {
            final ClassUtil.Ctor[] constructors;
            final ClassUtil.Ctor[] declaredCtors = constructors = ClassUtil.getConstructors(type.getRawClass());
            for (final ClassUtil.Ctor ctor : constructors) {
                if (isIncludableConstructor(ctor.getConstructor())) {
                    if (ctor.getParamCount() == 0) {
                        defaultCtor = ctor;
                    }
                    else {
                        if (ctors == null) {
                            ctors = new ArrayList<ClassUtil.Ctor>();
                        }
                        ctors.add(ctor);
                    }
                }
            }
        }
        List<AnnotatedConstructor> result;
        int ctorCount;
        if (ctors == null) {
            result = Collections.emptyList();
            if (defaultCtor == null) {
                return result;
            }
            ctorCount = 0;
        }
        else {
            ctorCount = ctors.size();
            result = new ArrayList<AnnotatedConstructor>(ctorCount);
            for (int i = 0; i < ctorCount; ++i) {
                result.add(null);
            }
        }
        if (primaryMixIn != null) {
            MemberKey[] ctorKeys = null;
            for (final ClassUtil.Ctor mixinCtor : ClassUtil.getConstructors(primaryMixIn)) {
                if (mixinCtor.getParamCount() == 0) {
                    if (defaultCtor != null) {
                        this._defaultConstructor = this.constructDefaultConstructor(defaultCtor, mixinCtor);
                        defaultCtor = null;
                    }
                }
                else if (ctors != null) {
                    if (ctorKeys == null) {
                        ctorKeys = new MemberKey[ctorCount];
                        for (int j = 0; j < ctorCount; ++j) {
                            ctorKeys[j] = new MemberKey(ctors.get(j).getConstructor());
                        }
                    }
                    final MemberKey key = new MemberKey(mixinCtor.getConstructor());
                    for (int k = 0; k < ctorCount; ++k) {
                        if (key.equals(ctorKeys[k])) {
                            result.set(k, this.constructNonDefaultConstructor(ctors.get(k), mixinCtor));
                            break;
                        }
                    }
                }
            }
        }
        if (defaultCtor != null) {
            this._defaultConstructor = this.constructDefaultConstructor(defaultCtor, null);
        }
        for (int i = 0; i < ctorCount; ++i) {
            final AnnotatedConstructor ctor2 = result.get(i);
            if (ctor2 == null) {
                result.set(i, this.constructNonDefaultConstructor(ctors.get(i), null));
            }
        }
        return result;
    }
    
    private List<AnnotatedMethod> _findPotentialFactories(final TypeFactory typeFactory, final JavaType type, final Class<?> primaryMixIn) {
        List<Method> candidates = null;
        for (final Method m : ClassUtil.getClassMethods(type.getRawClass())) {
            if (_isIncludableFactoryMethod(m)) {
                if (candidates == null) {
                    candidates = new ArrayList<Method>();
                }
                candidates.add(m);
            }
        }
        if (candidates == null) {
            return Collections.emptyList();
        }
        final TypeResolutionContext emptyTypeResCtxt = new TypeResolutionContext.Empty(typeFactory);
        final int factoryCount = candidates.size();
        final List<AnnotatedMethod> result = new ArrayList<AnnotatedMethod>(factoryCount);
        for (int i = 0; i < factoryCount; ++i) {
            result.add(null);
        }
        if (primaryMixIn != null) {
            MemberKey[] methodKeys = null;
            for (final Method mixinFactory : primaryMixIn.getDeclaredMethods()) {
                if (_isIncludableFactoryMethod(mixinFactory)) {
                    if (methodKeys == null) {
                        methodKeys = new MemberKey[factoryCount];
                        for (int j = 0; j < factoryCount; ++j) {
                            methodKeys[j] = new MemberKey(candidates.get(j));
                        }
                    }
                    final MemberKey key = new MemberKey(mixinFactory);
                    for (int k = 0; k < factoryCount; ++k) {
                        if (key.equals(methodKeys[k])) {
                            result.set(k, this.constructFactoryCreator(candidates.get(k), emptyTypeResCtxt, mixinFactory));
                            break;
                        }
                    }
                }
            }
        }
        for (int i = 0; i < factoryCount; ++i) {
            final AnnotatedMethod factory = result.get(i);
            if (factory == null) {
                final Method candidate = candidates.get(i);
                final TypeResolutionContext typeResCtxt = MethodGenericTypeResolver.narrowMethodTypeParameters(candidate, type, typeFactory, emptyTypeResCtxt);
                result.set(i, this.constructFactoryCreator(candidate, typeResCtxt, null));
            }
        }
        return result;
    }
    
    private static boolean _isIncludableFactoryMethod(final Method m) {
        return Modifier.isStatic(m.getModifiers()) && !m.isSynthetic();
    }
    
    protected AnnotatedConstructor constructDefaultConstructor(final ClassUtil.Ctor ctor, final ClassUtil.Ctor mixin) {
        return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), this.collectAnnotations(ctor, mixin), AnnotatedCreatorCollector.NO_ANNOTATION_MAPS);
    }
    
    protected AnnotatedConstructor constructNonDefaultConstructor(final ClassUtil.Ctor ctor, final ClassUtil.Ctor mixin) {
        final int paramCount = ctor.getParamCount();
        if (this._intr == null) {
            return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), CollectorBase._emptyAnnotationMap(), CollectorBase._emptyAnnotationMaps(paramCount));
        }
        if (paramCount == 0) {
            return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), this.collectAnnotations(ctor, mixin), AnnotatedCreatorCollector.NO_ANNOTATION_MAPS);
        }
        Annotation[][] paramAnns = ctor.getParameterAnnotations();
        AnnotationMap[] resolvedAnnotations;
        if (paramCount != paramAnns.length) {
            resolvedAnnotations = null;
            final Class<?> dc = ctor.getDeclaringClass();
            if (ClassUtil.isEnumType(dc) && paramCount == paramAnns.length + 2) {
                final Annotation[][] old = paramAnns;
                paramAnns = new Annotation[old.length + 2][];
                System.arraycopy(old, 0, paramAnns, 2, old.length);
                resolvedAnnotations = this.collectAnnotations(paramAnns, null);
            }
            else if (dc.isMemberClass() && paramCount == paramAnns.length + 1) {
                final Annotation[][] old = paramAnns;
                paramAnns = new Annotation[old.length + 1][];
                System.arraycopy(old, 0, paramAnns, 1, old.length);
                paramAnns[0] = AnnotatedCreatorCollector.NO_ANNOTATIONS;
                resolvedAnnotations = this.collectAnnotations(paramAnns, null);
            }
            if (resolvedAnnotations == null) {
                throw new IllegalStateException(String.format("Internal error: constructor for %s has mismatch: %d parameters; %d sets of annotations", ctor.getDeclaringClass().getName(), paramCount, paramAnns.length));
            }
        }
        else {
            resolvedAnnotations = this.collectAnnotations(paramAnns, (mixin == null) ? ((Annotation[][])null) : mixin.getParameterAnnotations());
        }
        return new AnnotatedConstructor(this._typeContext, ctor.getConstructor(), this.collectAnnotations(ctor, mixin), resolvedAnnotations);
    }
    
    protected AnnotatedMethod constructFactoryCreator(final Method m, final TypeResolutionContext typeResCtxt, final Method mixin) {
        final int paramCount = m.getParameterTypes().length;
        if (this._intr == null) {
            return new AnnotatedMethod(typeResCtxt, m, CollectorBase._emptyAnnotationMap(), CollectorBase._emptyAnnotationMaps(paramCount));
        }
        if (paramCount == 0) {
            return new AnnotatedMethod(typeResCtxt, m, this.collectAnnotations(m, mixin), AnnotatedCreatorCollector.NO_ANNOTATION_MAPS);
        }
        return new AnnotatedMethod(typeResCtxt, m, this.collectAnnotations(m, mixin), this.collectAnnotations(m.getParameterAnnotations(), (mixin == null) ? ((Annotation[][])null) : mixin.getParameterAnnotations()));
    }
    
    private AnnotationMap[] collectAnnotations(final Annotation[][] mainAnns, final Annotation[][] mixinAnns) {
        if (this._collectAnnotations) {
            final int count = mainAnns.length;
            final AnnotationMap[] result = new AnnotationMap[count];
            for (int i = 0; i < count; ++i) {
                AnnotationCollector c = this.collectAnnotations(AnnotationCollector.emptyCollector(), mainAnns[i]);
                if (mixinAnns != null) {
                    c = this.collectAnnotations(c, mixinAnns[i]);
                }
                result[i] = c.asAnnotationMap();
            }
            return result;
        }
        return AnnotatedCreatorCollector.NO_ANNOTATION_MAPS;
    }
    
    private AnnotationMap collectAnnotations(final ClassUtil.Ctor main, final ClassUtil.Ctor mixin) {
        if (this._collectAnnotations) {
            AnnotationCollector c = this.collectAnnotations(main.getDeclaredAnnotations());
            if (mixin != null) {
                c = this.collectAnnotations(c, mixin.getDeclaredAnnotations());
            }
            return c.asAnnotationMap();
        }
        return CollectorBase._emptyAnnotationMap();
    }
    
    private final AnnotationMap collectAnnotations(final AnnotatedElement main, final AnnotatedElement mixin) {
        AnnotationCollector c = this.collectAnnotations(main.getDeclaredAnnotations());
        if (mixin != null) {
            c = this.collectAnnotations(c, mixin.getDeclaredAnnotations());
        }
        return c.asAnnotationMap();
    }
    
    private static boolean isIncludableConstructor(final Constructor<?> c) {
        return !c.isSynthetic();
    }
}
