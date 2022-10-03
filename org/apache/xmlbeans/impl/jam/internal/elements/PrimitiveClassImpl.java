package org.apache.xmlbeans.impl.jam.internal.elements;

import java.util.HashMap;
import org.apache.xmlbeans.impl.jam.JClass;
import java.util.Map;

public final class PrimitiveClassImpl extends BuiltinClassImpl
{
    private static final Object[][] PRIMITIVES;
    private static final Map NAME_TO_FD;
    private static final Map NAME_TO_CLASS;
    
    public static void mapNameToPrimitive(final ElementContext ctx, final Map out) {
        for (int i = 0; i < PrimitiveClassImpl.PRIMITIVES.length; ++i) {
            final JClass c = new PrimitiveClassImpl(ctx, (String)PrimitiveClassImpl.PRIMITIVES[i][0]);
            out.put(PrimitiveClassImpl.PRIMITIVES[i][0], c);
            out.put(PrimitiveClassImpl.PRIMITIVES[i][1], c);
        }
    }
    
    public static String getPrimitiveClassForName(final String named) {
        return PrimitiveClassImpl.NAME_TO_FD.get(named);
    }
    
    public static boolean isPrimitive(final String name) {
        return PrimitiveClassImpl.NAME_TO_FD.get(name) != null;
    }
    
    public static final String getFieldDescriptor(final String classname) {
        return PrimitiveClassImpl.NAME_TO_FD.get(classname);
    }
    
    public static final Class getPrimitiveClass(final String classname) {
        return PrimitiveClassImpl.NAME_TO_CLASS.get(classname);
    }
    
    private PrimitiveClassImpl(final ElementContext ctx, final String name) {
        super(ctx);
        if (name == null) {
            throw new IllegalArgumentException("null name");
        }
        if (!PrimitiveClassImpl.NAME_TO_FD.containsKey(name)) {
            throw new IllegalArgumentException("Unknown primitive class '" + name + "'");
        }
        this.reallySetSimpleName(name);
    }
    
    @Override
    public String getQualifiedName() {
        return this.getSimpleName();
    }
    
    @Override
    public String getFieldDescriptor() {
        return PrimitiveClassImpl.NAME_TO_FD.get(this.getSimpleName());
    }
    
    @Override
    public boolean isAssignableFrom(final JClass c) {
        return c.isPrimitiveType() && c.getSimpleName().equals(this.getSimpleName());
    }
    
    @Override
    public boolean isPrimitiveType() {
        return true;
    }
    
    @Override
    public Class getPrimitiveClass() {
        return PrimitiveClassImpl.NAME_TO_CLASS.get(this.getSimpleName());
    }
    
    static {
        PRIMITIVES = new Object[][] { { "int", "I", Integer.TYPE }, { "long", "J", Long.TYPE }, { "boolean", "Z", Boolean.TYPE }, { "short", "S", Short.TYPE }, { "byte", "B", Byte.TYPE }, { "char", "C", Character.TYPE }, { "float", "F", Float.TYPE }, { "double", "D", Double.TYPE } };
        NAME_TO_FD = new HashMap();
        NAME_TO_CLASS = new HashMap();
        for (int i = 0; i < PrimitiveClassImpl.PRIMITIVES.length; ++i) {
            PrimitiveClassImpl.NAME_TO_FD.put(PrimitiveClassImpl.PRIMITIVES[i][0], PrimitiveClassImpl.PRIMITIVES[i][1]);
            PrimitiveClassImpl.NAME_TO_CLASS.put(PrimitiveClassImpl.PRIMITIVES[i][0], PrimitiveClassImpl.PRIMITIVES[i][2]);
        }
    }
}
