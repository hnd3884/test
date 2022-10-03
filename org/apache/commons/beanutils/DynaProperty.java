package org.apache.commons.beanutils;

import java.io.StreamCorruptedException;
import java.io.ObjectInputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Map;
import java.util.List;
import java.io.Serializable;

public class DynaProperty implements Serializable
{
    private static final int BOOLEAN_TYPE = 1;
    private static final int BYTE_TYPE = 2;
    private static final int CHAR_TYPE = 3;
    private static final int DOUBLE_TYPE = 4;
    private static final int FLOAT_TYPE = 5;
    private static final int INT_TYPE = 6;
    private static final int LONG_TYPE = 7;
    private static final int SHORT_TYPE = 8;
    protected String name;
    protected transient Class<?> type;
    protected transient Class<?> contentType;
    
    public DynaProperty(final String name) {
        this(name, Object.class);
    }
    
    public DynaProperty(final String name, final Class<?> type) {
        this.name = null;
        this.type = null;
        this.name = name;
        this.type = type;
        if (type != null && type.isArray()) {
            this.contentType = type.getComponentType();
        }
    }
    
    public DynaProperty(final String name, final Class<?> type, final Class<?> contentType) {
        this.name = null;
        this.type = null;
        this.name = name;
        this.type = type;
        this.contentType = contentType;
    }
    
    public String getName() {
        return this.name;
    }
    
    public Class<?> getType() {
        return this.type;
    }
    
    public Class<?> getContentType() {
        return this.contentType;
    }
    
    public boolean isIndexed() {
        return this.type != null && (this.type.isArray() || List.class.isAssignableFrom(this.type));
    }
    
    public boolean isMapped() {
        return this.type != null && Map.class.isAssignableFrom(this.type);
    }
    
    @Override
    public boolean equals(final Object obj) {
        boolean result = false;
        result = (obj == this);
        if (!result && obj instanceof DynaProperty) {
            final DynaProperty that = (DynaProperty)obj;
            boolean b = false;
            Label_0127: {
                Label_0126: {
                    if (this.name == null) {
                        if (that.name != null) {
                            break Label_0126;
                        }
                    }
                    else if (!this.name.equals(that.name)) {
                        break Label_0126;
                    }
                    if (this.type == null) {
                        if (that.type != null) {
                            break Label_0126;
                        }
                    }
                    else if (!this.type.equals(that.type)) {
                        break Label_0126;
                    }
                    if ((this.contentType != null) ? this.contentType.equals(that.contentType) : (that.contentType == null)) {
                        b = true;
                        break Label_0127;
                    }
                }
                b = false;
            }
            result = b;
        }
        return result;
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = result * 31 + ((this.name == null) ? 0 : this.name.hashCode());
        result = result * 31 + ((this.type == null) ? 0 : this.type.hashCode());
        result = result * 31 + ((this.contentType == null) ? 0 : this.contentType.hashCode());
        return result;
    }
    
    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("DynaProperty[name=");
        sb.append(this.name);
        sb.append(",type=");
        sb.append(this.type);
        if (this.isMapped() || this.isIndexed()) {
            sb.append(" <").append(this.contentType).append(">");
        }
        sb.append("]");
        return sb.toString();
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        this.writeAnyClass(this.type, out);
        if (this.isMapped() || this.isIndexed()) {
            this.writeAnyClass(this.contentType, out);
        }
        out.defaultWriteObject();
    }
    
    private void writeAnyClass(final Class<?> clazz, final ObjectOutputStream out) throws IOException {
        int primitiveType = 0;
        if (Boolean.TYPE.equals(clazz)) {
            primitiveType = 1;
        }
        else if (Byte.TYPE.equals(clazz)) {
            primitiveType = 2;
        }
        else if (Character.TYPE.equals(clazz)) {
            primitiveType = 3;
        }
        else if (Double.TYPE.equals(clazz)) {
            primitiveType = 4;
        }
        else if (Float.TYPE.equals(clazz)) {
            primitiveType = 5;
        }
        else if (Integer.TYPE.equals(clazz)) {
            primitiveType = 6;
        }
        else if (Long.TYPE.equals(clazz)) {
            primitiveType = 7;
        }
        else if (Short.TYPE.equals(clazz)) {
            primitiveType = 8;
        }
        if (primitiveType == 0) {
            out.writeBoolean(false);
            out.writeObject(clazz);
        }
        else {
            out.writeBoolean(true);
            out.writeInt(primitiveType);
        }
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.type = this.readAnyClass(in);
        if (this.isMapped() || this.isIndexed()) {
            this.contentType = this.readAnyClass(in);
        }
        in.defaultReadObject();
    }
    
    private Class<?> readAnyClass(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        if (!in.readBoolean()) {
            return (Class)in.readObject();
        }
        switch (in.readInt()) {
            case 1: {
                return Boolean.TYPE;
            }
            case 2: {
                return Byte.TYPE;
            }
            case 3: {
                return Character.TYPE;
            }
            case 4: {
                return Double.TYPE;
            }
            case 5: {
                return Float.TYPE;
            }
            case 6: {
                return Integer.TYPE;
            }
            case 7: {
                return Long.TYPE;
            }
            case 8: {
                return Short.TYPE;
            }
            default: {
                throw new StreamCorruptedException("Invalid primitive type. Check version of beanutils used to serialize is compatible.");
            }
        }
    }
}
