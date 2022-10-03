package com.azul.crs.com.fasterxml.jackson.databind.introspect;

import java.lang.annotation.Annotation;
import java.lang.reflect.Modifier;
import com.azul.crs.com.fasterxml.jackson.databind.util.ClassUtil;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.List;
import com.azul.crs.com.fasterxml.jackson.databind.JavaType;
import com.azul.crs.com.fasterxml.jackson.databind.type.TypeFactory;
import com.azul.crs.com.fasterxml.jackson.databind.AnnotationIntrospector;

public class AnnotatedMethodCollector extends CollectorBase
{
    private final ClassIntrospector.MixInResolver _mixInResolver;
    private final boolean _collectAnnotations;
    
    AnnotatedMethodCollector(final AnnotationIntrospector intr, final ClassIntrospector.MixInResolver mixins, final boolean collectAnnotations) {
        super(intr);
        this._mixInResolver = ((intr == null) ? null : mixins);
        this._collectAnnotations = collectAnnotations;
    }
    
    public static AnnotatedMethodMap collectMethods(final AnnotationIntrospector intr, final TypeResolutionContext tc, final ClassIntrospector.MixInResolver mixins, final TypeFactory types, final JavaType type, final List<JavaType> superTypes, final Class<?> primaryMixIn, final boolean collectAnnotations) {
        return new AnnotatedMethodCollector(intr, mixins, collectAnnotations).collect(types, tc, type, superTypes, primaryMixIn);
    }
    
    AnnotatedMethodMap collect(final TypeFactory typeFactory, final TypeResolutionContext tc, final JavaType mainType, final List<JavaType> superTypes, final Class<?> primaryMixIn) {
        final Map<MemberKey, MethodBuilder> methods = new LinkedHashMap<MemberKey, MethodBuilder>();
        this._addMemberMethods(tc, mainType.getRawClass(), methods, primaryMixIn);
        for (final JavaType type : superTypes) {
            final Class<?> mixin = (this._mixInResolver == null) ? null : this._mixInResolver.findMixInClassFor(type.getRawClass());
            this._addMemberMethods(new TypeResolutionContext.Basic(typeFactory, type.getBindings()), type.getRawClass(), methods, mixin);
        }
        boolean checkJavaLangObject = false;
        if (this._mixInResolver != null) {
            final Class<?> mixin2 = this._mixInResolver.findMixInClassFor(Object.class);
            if (mixin2 != null) {
                this._addMethodMixIns(tc, mainType.getRawClass(), methods, mixin2);
                checkJavaLangObject = true;
            }
        }
        if (checkJavaLangObject && this._intr != null && !methods.isEmpty()) {
            for (final Map.Entry<MemberKey, MethodBuilder> entry : methods.entrySet()) {
                final MemberKey k = entry.getKey();
                if ("hashCode".equals(k.getName())) {
                    if (0 != k.argCount()) {
                        continue;
                    }
                    try {
                        final Method m = Object.class.getDeclaredMethod(k.getName(), (Class<?>[])new Class[0]);
                        if (m == null) {
                            continue;
                        }
                        final MethodBuilder b = entry.getValue();
                        b.annotations = this.collectDefaultAnnotations(b.annotations, m.getDeclaredAnnotations());
                        b.method = m;
                    }
                    catch (final Exception ex) {}
                }
            }
        }
        if (methods.isEmpty()) {
            return new AnnotatedMethodMap();
        }
        final Map<MemberKey, AnnotatedMethod> actual = new LinkedHashMap<MemberKey, AnnotatedMethod>(methods.size());
        for (final Map.Entry<MemberKey, MethodBuilder> entry2 : methods.entrySet()) {
            final AnnotatedMethod am = entry2.getValue().build();
            if (am != null) {
                actual.put(entry2.getKey(), am);
            }
        }
        return new AnnotatedMethodMap(actual);
    }
    
    private void _addMemberMethods(final TypeResolutionContext tc, final Class<?> cls, final Map<MemberKey, MethodBuilder> methods, final Class<?> mixInCls) {
        if (mixInCls != null) {
            this._addMethodMixIns(tc, cls, methods, mixInCls);
        }
        if (cls == null) {
            return;
        }
        for (final Method m : ClassUtil.getClassMethods(cls)) {
            if (_isIncludableMemberMethod(m)) {
                final MemberKey key = new MemberKey(m);
                final MethodBuilder b = methods.get(key);
                if (b == null) {
                    final AnnotationCollector c = (this._intr == null) ? AnnotationCollector.emptyCollector() : this.collectAnnotations(m.getDeclaredAnnotations());
                    methods.put(key, new MethodBuilder(tc, m, c));
                }
                else {
                    if (this._collectAnnotations) {
                        b.annotations = this.collectDefaultAnnotations(b.annotations, m.getDeclaredAnnotations());
                    }
                    final Method old = b.method;
                    if (old == null) {
                        b.method = m;
                    }
                    else if (Modifier.isAbstract(old.getModifiers()) && !Modifier.isAbstract(m.getModifiers())) {
                        b.method = m;
                        b.typeContext = tc;
                    }
                }
            }
        }
    }
    
    protected void _addMethodMixIns(final TypeResolutionContext tc, final Class<?> targetClass, final Map<MemberKey, MethodBuilder> methods, final Class<?> mixInCls) {
        if (this._intr == null) {
            return;
        }
        for (final Class<?> mixin : ClassUtil.findRawSuperTypes(mixInCls, targetClass, true)) {
            for (final Method m : mixin.getDeclaredMethods()) {
                if (_isIncludableMemberMethod(m)) {
                    final MemberKey key = new MemberKey(m);
                    final MethodBuilder b = methods.get(key);
                    final Annotation[] anns = m.getDeclaredAnnotations();
                    if (b == null) {
                        methods.put(key, new MethodBuilder(tc, null, this.collectAnnotations(anns)));
                    }
                    else {
                        b.annotations = this.collectDefaultAnnotations(b.annotations, anns);
                    }
                }
            }
        }
    }
    
    private static boolean _isIncludableMemberMethod(final Method m) {
        if (Modifier.isStatic(m.getModifiers()) || m.isSynthetic() || m.isBridge()) {
            return false;
        }
        final int pcount = m.getParameterTypes().length;
        return pcount <= 2;
    }
    
    private static final class MethodBuilder
    {
        public TypeResolutionContext typeContext;
        public Method method;
        public AnnotationCollector annotations;
        
        public MethodBuilder(final TypeResolutionContext tc, final Method m, final AnnotationCollector ann) {
            this.typeContext = tc;
            this.method = m;
            this.annotations = ann;
        }
        
        public AnnotatedMethod build() {
            if (this.method == null) {
                return null;
            }
            return new AnnotatedMethod(this.typeContext, this.method, this.annotations.asAnnotationMap(), null);
        }
    }
}
