package sun.reflect.generics.factory;

import java.lang.reflect.WildcardType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import sun.reflect.generics.tree.FieldTypeSignature;

public interface GenericsFactory
{
    TypeVariable<?> makeTypeVariable(final String p0, final FieldTypeSignature[] p1);
    
    ParameterizedType makeParameterizedType(final Type p0, final Type[] p1, final Type p2);
    
    TypeVariable<?> findTypeVariable(final String p0);
    
    WildcardType makeWildcard(final FieldTypeSignature[] p0, final FieldTypeSignature[] p1);
    
    Type makeNamedType(final String p0);
    
    Type makeArrayType(final Type p0);
    
    Type makeByte();
    
    Type makeBool();
    
    Type makeShort();
    
    Type makeChar();
    
    Type makeInt();
    
    Type makeLong();
    
    Type makeFloat();
    
    Type makeDouble();
    
    Type makeVoid();
}
