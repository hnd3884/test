package javax.lang.model.util;

import javax.lang.model.type.DeclaredType;
import javax.lang.model.type.WildcardType;
import javax.lang.model.type.ArrayType;
import javax.lang.model.type.NoType;
import javax.lang.model.type.NullType;
import javax.lang.model.type.TypeKind;
import javax.lang.model.element.TypeElement;
import javax.lang.model.type.PrimitiveType;
import java.util.List;
import javax.lang.model.type.ExecutableType;
import javax.lang.model.element.Element;
import javax.lang.model.type.TypeMirror;

public interface Types
{
    Element asElement(final TypeMirror p0);
    
    boolean isSameType(final TypeMirror p0, final TypeMirror p1);
    
    boolean isSubtype(final TypeMirror p0, final TypeMirror p1);
    
    boolean isAssignable(final TypeMirror p0, final TypeMirror p1);
    
    boolean contains(final TypeMirror p0, final TypeMirror p1);
    
    boolean isSubsignature(final ExecutableType p0, final ExecutableType p1);
    
    List<? extends TypeMirror> directSupertypes(final TypeMirror p0);
    
    TypeMirror erasure(final TypeMirror p0);
    
    TypeElement boxedClass(final PrimitiveType p0);
    
    PrimitiveType unboxedType(final TypeMirror p0);
    
    TypeMirror capture(final TypeMirror p0);
    
    PrimitiveType getPrimitiveType(final TypeKind p0);
    
    NullType getNullType();
    
    NoType getNoType(final TypeKind p0);
    
    ArrayType getArrayType(final TypeMirror p0);
    
    WildcardType getWildcardType(final TypeMirror p0, final TypeMirror p1);
    
    DeclaredType getDeclaredType(final TypeElement p0, final TypeMirror... p1);
    
    DeclaredType getDeclaredType(final DeclaredType p0, final TypeElement p1, final TypeMirror... p2);
    
    TypeMirror asMemberOf(final DeclaredType p0, final Element p1);
}
