package org.apache.xmlbeans.impl.jam.internal.elements;

import java.io.StringWriter;
import org.apache.xmlbeans.impl.jam.JamClassLoader;
import org.apache.xmlbeans.impl.jam.JClass;

public final class ArrayClassImpl extends BuiltinClassImpl
{
    private int mDimensions;
    private JClass mComponentType;
    
    public static JClass createClassForFD(final String arrayFD, final JamClassLoader loader) {
        if (!arrayFD.startsWith("[")) {
            throw new IllegalArgumentException("must be an array type fd: " + arrayFD);
        }
        if (arrayFD.endsWith(";")) {
            final int dims = arrayFD.indexOf("L");
            if (dims != -1 && dims < arrayFD.length() - 2) {
                final String componentType = arrayFD.substring(dims + 1, arrayFD.length() - 1);
                return new ArrayClassImpl(loader.loadClass(componentType), dims);
            }
            throw new IllegalArgumentException("array type field descriptor '" + arrayFD + "' is malformed");
        }
        else {
            final int dims = arrayFD.lastIndexOf("[") + 1;
            final String compFd = arrayFD.substring(dims, dims + 1);
            final JClass primType = loader.loadClass(compFd);
            if (primType == null) {
                throw new IllegalArgumentException("array type field descriptor '" + arrayFD + "' is malformed");
            }
            return new ArrayClassImpl(primType, dims);
        }
    }
    
    public static String normalizeArrayName(final String declaration) {
        if (declaration.startsWith("[")) {
            return declaration;
        }
        if (declaration.endsWith("]")) {
            int bracket = declaration.indexOf(91);
            if (bracket != -1) {
                final String typeName = declaration.substring(0, bracket);
                String fd = PrimitiveClassImpl.getPrimitiveClassForName(typeName);
                if (fd == null) {
                    fd = 'L' + typeName + ';';
                }
                final StringWriter out = new StringWriter();
                do {
                    out.write(91);
                    bracket = declaration.indexOf(91, bracket + 1);
                } while (bracket != -1);
                out.write(fd);
                return out.toString();
            }
        }
        throw new IllegalArgumentException("'" + declaration + "' does not name an array");
    }
    
    private ArrayClassImpl(final JClass componentType, final int dimensions) {
        super(((ElementImpl)componentType).getContext());
        if (dimensions < 1) {
            throw new IllegalArgumentException("dimensions=" + dimensions);
        }
        if (componentType == null) {
            throw new IllegalArgumentException("null componentType");
        }
        this.mComponentType = componentType;
        this.mDimensions = dimensions;
    }
    
    @Override
    public String getSimpleName() {
        final String out = this.getQualifiedName();
        final int lastDot = out.lastIndexOf(46);
        return (lastDot == -1) ? out : out.substring(lastDot + 1);
    }
    
    @Override
    public String getQualifiedName() {
        final StringWriter out = new StringWriter();
        out.write(this.mComponentType.getQualifiedName());
        for (int i = 0; i < this.mDimensions; ++i) {
            out.write("[]");
        }
        return out.toString();
    }
    
    @Override
    public boolean isArrayType() {
        return true;
    }
    
    @Override
    public JClass getArrayComponentType() {
        return this.mComponentType;
    }
    
    @Override
    public int getArrayDimensions() {
        return this.mDimensions;
    }
    
    @Override
    public JClass getSuperclass() {
        return this.getClassLoader().loadClass("java.lang.Object");
    }
    
    @Override
    public boolean isAssignableFrom(final JClass c) {
        return c.isArrayType() && c.getArrayDimensions() == this.mDimensions && this.mComponentType.isAssignableFrom(c.getArrayComponentType());
    }
    
    @Override
    public String getFieldDescriptor() {
        final StringWriter out = new StringWriter();
        for (int i = 0; i < this.mDimensions; ++i) {
            out.write("[");
        }
        if (this.mComponentType.isPrimitiveType()) {
            out.write(this.mComponentType.getFieldDescriptor());
        }
        else {
            out.write("L");
            out.write(this.mComponentType.getQualifiedName());
            out.write(";");
        }
        return out.toString();
    }
}
