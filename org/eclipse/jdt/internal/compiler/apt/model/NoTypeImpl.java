package org.eclipse.jdt.internal.compiler.apt.model;

import java.lang.reflect.Array;
import java.lang.annotation.Annotation;
import javax.lang.model.element.AnnotationMirror;
import java.util.List;
import javax.lang.model.type.TypeVisitor;
import javax.lang.model.type.TypeKind;
import javax.lang.model.type.NullType;
import javax.lang.model.type.NoType;

public class NoTypeImpl implements NoType, NullType
{
    private final TypeKind _kind;
    public static final NoType NO_TYPE_NONE;
    public static final NoType NO_TYPE_VOID;
    public static final NoType NO_TYPE_PACKAGE;
    public static final NullType NULL_TYPE;
    
    static {
        NO_TYPE_NONE = new NoTypeImpl(TypeKind.NONE);
        NO_TYPE_VOID = new NoTypeImpl(TypeKind.VOID);
        NO_TYPE_PACKAGE = new NoTypeImpl(TypeKind.PACKAGE);
        NULL_TYPE = new NoTypeImpl(TypeKind.NULL);
    }
    
    private NoTypeImpl(final TypeKind kind) {
        this._kind = kind;
    }
    
    @Override
    public <R, P> R accept(final TypeVisitor<R, P> v, final P p) {
        switch (this.getKind()) {
            case NULL: {
                return v.visitNull(this, p);
            }
            default: {
                return v.visitNoType(this, p);
            }
        }
    }
    
    @Override
    public TypeKind getKind() {
        return this._kind;
    }
    
    @Override
    public String toString() {
        switch (this._kind) {
            default: {
                return "none";
            }
            case NULL: {
                return "null";
            }
            case VOID: {
                return "void";
            }
            case PACKAGE: {
                return "package";
            }
        }
    }
    
    @Override
    public List<? extends AnnotationMirror> getAnnotationMirrors() {
        return Factory.EMPTY_ANNOTATION_MIRRORS;
    }
    
    @Override
    public <A extends Annotation> A getAnnotation(final Class<A> annotationType) {
        return null;
    }
    
    @Override
    public <A extends Annotation> A[] getAnnotationsByType(final Class<A> annotationType) {
        return (A[])Array.newInstance(annotationType, 0);
    }
}
