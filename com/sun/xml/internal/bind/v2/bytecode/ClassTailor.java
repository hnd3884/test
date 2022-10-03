package com.sun.xml.internal.bind.v2.bytecode;

import com.sun.xml.internal.bind.Util;
import java.io.IOException;
import java.util.logging.Level;
import java.io.OutputStream;
import java.io.DataOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.util.logging.Logger;

public final class ClassTailor
{
    private static final Logger logger;
    
    private ClassTailor() {
    }
    
    public static String toVMClassName(final Class c) {
        assert !c.isPrimitive();
        if (c.isArray()) {
            return toVMTypeName(c);
        }
        return c.getName().replace('.', '/');
    }
    
    public static String toVMTypeName(final Class c) {
        if (c.isArray()) {
            return '[' + toVMTypeName(c.getComponentType());
        }
        if (!c.isPrimitive()) {
            return 'L' + c.getName().replace('.', '/') + ';';
        }
        if (c == Boolean.TYPE) {
            return "Z";
        }
        if (c == Character.TYPE) {
            return "C";
        }
        if (c == Byte.TYPE) {
            return "B";
        }
        if (c == Double.TYPE) {
            return "D";
        }
        if (c == Float.TYPE) {
            return "F";
        }
        if (c == Integer.TYPE) {
            return "I";
        }
        if (c == Long.TYPE) {
            return "J";
        }
        if (c == Short.TYPE) {
            return "S";
        }
        throw new IllegalArgumentException(c.getName());
    }
    
    public static byte[] tailor(final Class templateClass, final String newClassName, final String... replacements) {
        final String vmname = toVMClassName(templateClass);
        return tailor(SecureLoader.getClassClassLoader(templateClass).getResourceAsStream(vmname + ".class"), vmname, newClassName, replacements);
    }
    
    public static byte[] tailor(final InputStream image, final String templateClassName, final String newClassName, final String... replacements) {
        final DataInputStream in = new DataInputStream(image);
        try {
            final ByteArrayOutputStream baos = new ByteArrayOutputStream(1024);
            final DataOutputStream out = new DataOutputStream(baos);
            final long l = in.readLong();
            out.writeLong(l);
            final short count = in.readShort();
            out.writeShort(count);
            for (int i = 0; i < count; ++i) {
                final byte tag = in.readByte();
                out.writeByte(tag);
                switch (tag) {
                    case 0: {
                        break;
                    }
                    case 1: {
                        String value = in.readUTF();
                        if (value.equals(templateClassName)) {
                            value = newClassName;
                        }
                        else {
                            for (int j = 0; j < replacements.length; j += 2) {
                                if (value.equals(replacements[j])) {
                                    value = replacements[j + 1];
                                    break;
                                }
                            }
                        }
                        out.writeUTF(value);
                        break;
                    }
                    case 3:
                    case 4: {
                        out.writeInt(in.readInt());
                        break;
                    }
                    case 5:
                    case 6: {
                        ++i;
                        out.writeLong(in.readLong());
                        break;
                    }
                    case 7:
                    case 8: {
                        out.writeShort(in.readShort());
                        break;
                    }
                    case 9:
                    case 10:
                    case 11:
                    case 12: {
                        out.writeInt(in.readInt());
                        break;
                    }
                    default: {
                        throw new IllegalArgumentException("Unknown constant type " + tag);
                    }
                }
            }
            final byte[] buf = new byte[512];
            int len;
            while ((len = in.read(buf)) > 0) {
                out.write(buf, 0, len);
            }
            in.close();
            out.close();
            return baos.toByteArray();
        }
        catch (final IOException e) {
            ClassTailor.logger.log(Level.WARNING, "failed to tailor", e);
            return null;
        }
    }
    
    static {
        logger = Util.getClassLogger();
    }
}
