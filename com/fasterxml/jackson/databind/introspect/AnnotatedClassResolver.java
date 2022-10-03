package com.fasterxml.jackson.databind.introspect;

import java.util.Map;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.annotation.Annotation;
import java.util.Iterator;
import java.util.Collections;
import java.util.List;
import java.util.ArrayList;
import com.fasterxml.jackson.databind.util.ClassUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.type.TypeBindings;
import com.fasterxml.jackson.databind.AnnotationIntrospector;
import com.fasterxml.jackson.databind.cfg.MapperConfig;
import com.fasterxml.jackson.databind.util.Annotations;

public class AnnotatedClassResolver
{
    private static final Annotations NO_ANNOTATIONS;
    private static final Class<?> CLS_OBJECT;
    private static final Class<?> CLS_ENUM;
    private static final Class<?> CLS_LIST;
    private static final Class<?> CLS_MAP;
    private final MapperConfig<?> _config;
    private final AnnotationIntrospector _intr;
    private final ClassIntrospector.MixInResolver _mixInResolver;
    private final TypeBindings _bindings;
    private final JavaType _type;
    private final Class<?> _class;
    private final Class<?> _primaryMixin;
    private final boolean _collectAnnotations;
    
    AnnotatedClassResolver(final MapperConfig<?> config, final JavaType type, final ClassIntrospector.MixInResolver r) {
        this._config = config;
        this._type = type;
        this._class = type.getRawClass();
        this._mixInResolver = r;
        this._bindings = type.getBindings();
        this._intr = (config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null);
        this._primaryMixin = ((r == null) ? null : r.findMixInClassFor(this._class));
        this._collectAnnotations = (this._intr != null && (!ClassUtil.isJDKClass(this._class) || !this._type.isContainerType()));
    }
    
    AnnotatedClassResolver(final MapperConfig<?> config, final Class<?> cls, final ClassIntrospector.MixInResolver r) {
        this._config = config;
        this._type = null;
        this._class = cls;
        this._mixInResolver = r;
        this._bindings = TypeBindings.emptyBindings();
        if (config == null) {
            this._intr = null;
            this._primaryMixin = null;
        }
        else {
            this._intr = (config.isAnnotationProcessingEnabled() ? config.getAnnotationIntrospector() : null);
            this._primaryMixin = ((r == null) ? null : r.findMixInClassFor(this._class));
        }
        this._collectAnnotations = (this._intr != null);
    }
    
    public static AnnotatedClass resolve(final MapperConfig<?> config, final JavaType forType, final ClassIntrospector.MixInResolver r) {
        if (forType.isArrayType() && skippableArray(config, forType.getRawClass())) {
            return createArrayType(config, forType.getRawClass());
        }
        return new AnnotatedClassResolver(config, forType, r).resolveFully();
    }
    
    public static AnnotatedClass resolveWithoutSuperTypes(final MapperConfig<?> config, final Class<?> forType) {
        return resolveWithoutSuperTypes(config, forType, config);
    }
    
    public static AnnotatedClass resolveWithoutSuperTypes(final MapperConfig<?> config, final JavaType forType, final ClassIntrospector.MixInResolver r) {
        if (forType.isArrayType() && skippableArray(config, forType.getRawClass())) {
            return createArrayType(config, forType.getRawClass());
        }
        return new AnnotatedClassResolver(config, forType, r).resolveWithoutSuperTypes();
    }
    
    public static AnnotatedClass resolveWithoutSuperTypes(final MapperConfig<?> config, final Class<?> forType, final ClassIntrospector.MixInResolver r) {
        if (forType.isArray() && skippableArray(config, forType)) {
            return createArrayType(config, forType);
        }
        return new AnnotatedClassResolver(config, forType, r).resolveWithoutSuperTypes();
    }
    
    private static boolean skippableArray(final MapperConfig<?> config, final Class<?> type) {
        return config == null || config.findMixInClassFor(type) == null;
    }
    
    static AnnotatedClass createPrimordial(final Class<?> raw) {
        return new AnnotatedClass(raw);
    }
    
    static AnnotatedClass createArrayType(final MapperConfig<?> config, final Class<?> raw) {
        return new AnnotatedClass(raw);
    }
    
    AnnotatedClass resolveFully() {
        final List<JavaType> superTypes = new ArrayList<JavaType>(8);
        if (!this._type.hasRawClass(Object.class)) {
            if (this._type.isInterface()) {
                _addSuperInterfaces(this._type, superTypes, false);
            }
            else {
                _addSuperTypes(this._type, superTypes, false);
            }
        }
        return new AnnotatedClass(this._type, this._class, superTypes, this._primaryMixin, this.resolveClassAnnotations(superTypes), this._bindings, this._intr, this._mixInResolver, this._config.getTypeFactory(), this._collectAnnotations);
    }
    
    AnnotatedClass resolveWithoutSuperTypes() {
        final List<JavaType> superTypes = Collections.emptyList();
        return new AnnotatedClass(null, this._class, superTypes, this._primaryMixin, this.resolveClassAnnotations(superTypes), this._bindings, this._intr, this._mixInResolver, this._config.getTypeFactory(), this._collectAnnotations);
    }
    
    private static void _addSuperTypes(final JavaType type, final List<JavaType> result, final boolean addClassItself) {
        final Class<?> cls = type.getRawClass();
        if (cls == AnnotatedClassResolver.CLS_OBJECT || cls == AnnotatedClassResolver.CLS_ENUM) {
            return;
        }
        if (addClassItself) {
            if (_contains(result, cls)) {
                return;
            }
            result.add(type);
        }
        for (final JavaType intCls : type.getInterfaces()) {
            _addSuperInterfaces(intCls, result, true);
        }
        final JavaType superType = type.getSuperClass();
        if (superType != null) {
            _addSuperTypes(superType, result, true);
        }
    }
    
    private static void _addSuperInterfaces(final JavaType type, final List<JavaType> result, final boolean addClassItself) {
        final Class<?> cls = type.getRawClass();
        if (addClassItself) {
            if (_contains(result, cls)) {
                return;
            }
            result.add(type);
            if (cls == AnnotatedClassResolver.CLS_LIST || cls == AnnotatedClassResolver.CLS_MAP) {
                return;
            }
        }
        for (final JavaType intCls : type.getInterfaces()) {
            _addSuperInterfaces(intCls, result, true);
        }
    }
    
    private static boolean _contains(final List<JavaType> found, final Class<?> raw) {
        for (int i = 0, end = found.size(); i < end; ++i) {
            if (found.get(i).getRawClass() == raw) {
                return true;
            }
        }
        return false;
    }
    
    private Annotations resolveClassAnnotations(final List<JavaType> superTypes) {
        if (this._intr == null) {
            return AnnotatedClassResolver.NO_ANNOTATIONS;
        }
        final boolean checkMixIns = this._mixInResolver != null && (!(this._mixInResolver instanceof SimpleMixInResolver) || ((SimpleMixInResolver)this._mixInResolver).hasMixIns());
        if (!checkMixIns && !this._collectAnnotations) {
            return AnnotatedClassResolver.NO_ANNOTATIONS;
        }
        AnnotationCollector resolvedCA = AnnotationCollector.emptyCollector();
        if (this._primaryMixin != null) {
            resolvedCA = this._addClassMixIns(resolvedCA, this._class, this._primaryMixin);
        }
        if (this._collectAnnotations) {
            resolvedCA = this._addAnnotationsIfNotPresent(resolvedCA, ClassUtil.findClassAnnotations(this._class));
        }
        for (final JavaType type : superTypes) {
            if (checkMixIns) {
                final Class<?> cls = type.getRawClass();
                resolvedCA = this._addClassMixIns(resolvedCA, cls, this._mixInResolver.findMixInClassFor(cls));
            }
            if (this._collectAnnotations) {
                resolvedCA = this._addAnnotationsIfNotPresent(resolvedCA, ClassUtil.findClassAnnotations(type.getRawClass()));
            }
        }
        if (checkMixIns) {
            resolvedCA = this._addClassMixIns(resolvedCA, Object.class, this._mixInResolver.findMixInClassFor(Object.class));
        }
        return resolvedCA.asAnnotations();
    }
    
    private AnnotationCollector _addClassMixIns(AnnotationCollector annotations, final Class<?> target, final Class<?> mixin) {
        if (mixin != null) {
            annotations = this._addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(mixin));
            for (final Class<?> parent : ClassUtil.findSuperClasses(mixin, target, false)) {
                annotations = this._addAnnotationsIfNotPresent(annotations, ClassUtil.findClassAnnotations(parent));
            }
        }
        return annotations;
    }
    
    private AnnotationCollector _addAnnotationsIfNotPresent(AnnotationCollector c, final Annotation[] anns) {
        if (anns != null) {
            for (final Annotation ann : anns) {
                if (!c.isPresent(ann)) {
                    c = c.addOrOverride(ann);
                    if (this._intr.isAnnotationBundle(ann)) {
                        c = this._addFromBundleIfNotPresent(c, ann);
                    }
                }
            }
        }
        return c;
    }
    
    private AnnotationCollector _addFromBundleIfNotPresent(AnnotationCollector c, final Annotation bundle) {
        for (final Annotation ann : ClassUtil.findClassAnnotations(bundle.annotationType())) {
            if (!(ann instanceof Target)) {
                if (!(ann instanceof Retention)) {
                    if (!c.isPresent(ann)) {
                        c = c.addOrOverride(ann);
                        if (this._intr.isAnnotationBundle(ann)) {
                            c = this._addFromBundleIfNotPresent(c, ann);
                        }
                    }
                }
            }
        }
        return c;
    }
    
    static {
        NO_ANNOTATIONS = AnnotationCollector.emptyAnnotations();
        CLS_OBJECT = Object.class;
        CLS_ENUM = Enum.class;
        CLS_LIST = List.class;
        CLS_MAP = Map.class;
    }
}
