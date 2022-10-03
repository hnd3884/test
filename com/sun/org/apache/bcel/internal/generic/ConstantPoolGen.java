package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.ConstantMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantDouble;
import com.sun.org.apache.bcel.internal.classfile.ConstantLong;
import com.sun.org.apache.bcel.internal.classfile.ConstantFloat;
import com.sun.org.apache.bcel.internal.classfile.ConstantInteger;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantFieldref;
import com.sun.org.apache.bcel.internal.classfile.ConstantInterfaceMethodref;
import com.sun.org.apache.bcel.internal.classfile.ConstantCP;
import com.sun.org.apache.bcel.internal.classfile.ConstantNameAndType;
import com.sun.org.apache.bcel.internal.classfile.ConstantClass;
import com.sun.org.apache.bcel.internal.classfile.ConstantUtf8;
import com.sun.org.apache.bcel.internal.classfile.ConstantString;
import java.util.HashMap;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import java.io.Serializable;

public class ConstantPoolGen implements Serializable
{
    protected int size;
    protected Constant[] constants;
    protected int index;
    private static final String METHODREF_DELIM = ":";
    private static final String IMETHODREF_DELIM = "#";
    private static final String FIELDREF_DELIM = "&";
    private static final String NAT_DELIM = "%";
    private HashMap string_table;
    private HashMap class_table;
    private HashMap utf8_table;
    private HashMap n_a_t_table;
    private HashMap cp_table;
    
    public ConstantPoolGen(final Constant[] cs) {
        this.size = 1024;
        this.constants = new Constant[this.size];
        this.index = 1;
        this.string_table = new HashMap();
        this.class_table = new HashMap();
        this.utf8_table = new HashMap();
        this.n_a_t_table = new HashMap();
        this.cp_table = new HashMap();
        if (cs.length > this.size) {
            this.size = cs.length;
            this.constants = new Constant[this.size];
        }
        System.arraycopy(cs, 0, this.constants, 0, cs.length);
        if (cs.length > 0) {
            this.index = cs.length;
        }
        for (int i = 1; i < this.index; ++i) {
            final Constant c = this.constants[i];
            if (c instanceof ConstantString) {
                final ConstantString s = (ConstantString)c;
                final ConstantUtf8 u8 = (ConstantUtf8)this.constants[s.getStringIndex()];
                this.string_table.put(u8.getBytes(), new Index(i));
            }
            else if (c instanceof ConstantClass) {
                final ConstantClass s2 = (ConstantClass)c;
                final ConstantUtf8 u8 = (ConstantUtf8)this.constants[s2.getNameIndex()];
                this.class_table.put(u8.getBytes(), new Index(i));
            }
            else if (c instanceof ConstantNameAndType) {
                final ConstantNameAndType n = (ConstantNameAndType)c;
                final ConstantUtf8 u8 = (ConstantUtf8)this.constants[n.getNameIndex()];
                final ConstantUtf8 u8_2 = (ConstantUtf8)this.constants[n.getSignatureIndex()];
                this.n_a_t_table.put(u8.getBytes() + "%" + u8_2.getBytes(), new Index(i));
            }
            else if (c instanceof ConstantUtf8) {
                final ConstantUtf8 u9 = (ConstantUtf8)c;
                this.utf8_table.put(u9.getBytes(), new Index(i));
            }
            else if (c instanceof ConstantCP) {
                final ConstantCP m = (ConstantCP)c;
                final ConstantClass clazz = (ConstantClass)this.constants[m.getClassIndex()];
                final ConstantNameAndType n2 = (ConstantNameAndType)this.constants[m.getNameAndTypeIndex()];
                ConstantUtf8 u10 = (ConstantUtf8)this.constants[clazz.getNameIndex()];
                final String class_name = u10.getBytes().replace('/', '.');
                u10 = (ConstantUtf8)this.constants[n2.getNameIndex()];
                final String method_name = u10.getBytes();
                u10 = (ConstantUtf8)this.constants[n2.getSignatureIndex()];
                final String signature = u10.getBytes();
                String delim = ":";
                if (c instanceof ConstantInterfaceMethodref) {
                    delim = "#";
                }
                else if (c instanceof ConstantFieldref) {
                    delim = "&";
                }
                this.cp_table.put(class_name + delim + method_name + delim + signature, new Index(i));
            }
        }
    }
    
    public ConstantPoolGen(final ConstantPool cp) {
        this(cp.getConstantPool());
    }
    
    public ConstantPoolGen() {
        this.size = 1024;
        this.constants = new Constant[this.size];
        this.index = 1;
        this.string_table = new HashMap();
        this.class_table = new HashMap();
        this.utf8_table = new HashMap();
        this.n_a_t_table = new HashMap();
        this.cp_table = new HashMap();
    }
    
    protected void adjustSize() {
        if (this.index + 3 >= this.size) {
            final Constant[] cs = this.constants;
            this.size *= 2;
            System.arraycopy(cs, 0, this.constants = new Constant[this.size], 0, this.index);
        }
    }
    
    public int lookupString(final String str) {
        final Index index = this.string_table.get(str);
        return (index != null) ? index.index : -1;
    }
    
    public int addString(final String str) {
        int ret;
        if ((ret = this.lookupString(str)) != -1) {
            return ret;
        }
        final int utf8 = this.addUtf8(str);
        this.adjustSize();
        final ConstantString s = new ConstantString(utf8);
        ret = this.index;
        this.constants[this.index++] = s;
        this.string_table.put(str, new Index(ret));
        return ret;
    }
    
    public int lookupClass(final String str) {
        final Index index = this.class_table.get(str.replace('.', '/'));
        return (index != null) ? index.index : -1;
    }
    
    private int addClass_(final String clazz) {
        int ret;
        if ((ret = this.lookupClass(clazz)) != -1) {
            return ret;
        }
        this.adjustSize();
        final ConstantClass c = new ConstantClass(this.addUtf8(clazz));
        ret = this.index;
        this.constants[this.index++] = c;
        this.class_table.put(clazz, new Index(ret));
        return ret;
    }
    
    public int addClass(final String str) {
        return this.addClass_(str.replace('.', '/'));
    }
    
    public int addClass(final ObjectType type) {
        return this.addClass(type.getClassName());
    }
    
    public int addArrayClass(final ArrayType type) {
        return this.addClass_(type.getSignature());
    }
    
    public int lookupInteger(final int n) {
        for (int i = 1; i < this.index; ++i) {
            if (this.constants[i] instanceof ConstantInteger) {
                final ConstantInteger c = (ConstantInteger)this.constants[i];
                if (c.getBytes() == n) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int addInteger(final int n) {
        int ret;
        if ((ret = this.lookupInteger(n)) != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index++] = new ConstantInteger(n);
        return ret;
    }
    
    public int lookupFloat(final float n) {
        final int bits = Float.floatToIntBits(n);
        for (int i = 1; i < this.index; ++i) {
            if (this.constants[i] instanceof ConstantFloat) {
                final ConstantFloat c = (ConstantFloat)this.constants[i];
                if (Float.floatToIntBits(c.getBytes()) == bits) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int addFloat(final float n) {
        int ret;
        if ((ret = this.lookupFloat(n)) != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index++] = new ConstantFloat(n);
        return ret;
    }
    
    public int lookupUtf8(final String n) {
        final Index index = this.utf8_table.get(n);
        return (index != null) ? index.index : -1;
    }
    
    public int addUtf8(final String n) {
        int ret;
        if ((ret = this.lookupUtf8(n)) != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index++] = new ConstantUtf8(n);
        this.utf8_table.put(n, new Index(ret));
        return ret;
    }
    
    public int lookupLong(final long n) {
        for (int i = 1; i < this.index; ++i) {
            if (this.constants[i] instanceof ConstantLong) {
                final ConstantLong c = (ConstantLong)this.constants[i];
                if (c.getBytes() == n) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int addLong(final long n) {
        int ret;
        if ((ret = this.lookupLong(n)) != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index] = new ConstantLong(n);
        this.index += 2;
        return ret;
    }
    
    public int lookupDouble(final double n) {
        final long bits = Double.doubleToLongBits(n);
        for (int i = 1; i < this.index; ++i) {
            if (this.constants[i] instanceof ConstantDouble) {
                final ConstantDouble c = (ConstantDouble)this.constants[i];
                if (Double.doubleToLongBits(c.getBytes()) == bits) {
                    return i;
                }
            }
        }
        return -1;
    }
    
    public int addDouble(final double n) {
        int ret;
        if ((ret = this.lookupDouble(n)) != -1) {
            return ret;
        }
        this.adjustSize();
        ret = this.index;
        this.constants[this.index] = new ConstantDouble(n);
        this.index += 2;
        return ret;
    }
    
    public int lookupNameAndType(final String name, final String signature) {
        final Index index = this.n_a_t_table.get(name + "%" + signature);
        return (index != null) ? index.index : -1;
    }
    
    public int addNameAndType(final String name, final String signature) {
        int ret;
        if ((ret = this.lookupNameAndType(name, signature)) != -1) {
            return ret;
        }
        this.adjustSize();
        final int name_index = this.addUtf8(name);
        final int signature_index = this.addUtf8(signature);
        ret = this.index;
        this.constants[this.index++] = new ConstantNameAndType(name_index, signature_index);
        this.n_a_t_table.put(name + "%" + signature, new Index(ret));
        return ret;
    }
    
    public int lookupMethodref(final String class_name, final String method_name, final String signature) {
        final Index index = this.cp_table.get(class_name + ":" + method_name + ":" + signature);
        return (index != null) ? index.index : -1;
    }
    
    public int lookupMethodref(final MethodGen method) {
        return this.lookupMethodref(method.getClassName(), method.getName(), method.getSignature());
    }
    
    public int addMethodref(final String class_name, final String method_name, final String signature) {
        int ret;
        if ((ret = this.lookupMethodref(class_name, method_name, signature)) != -1) {
            return ret;
        }
        this.adjustSize();
        final int name_and_type_index = this.addNameAndType(method_name, signature);
        final int class_index = this.addClass(class_name);
        ret = this.index;
        this.constants[this.index++] = new ConstantMethodref(class_index, name_and_type_index);
        this.cp_table.put(class_name + ":" + method_name + ":" + signature, new Index(ret));
        return ret;
    }
    
    public int addMethodref(final MethodGen method) {
        return this.addMethodref(method.getClassName(), method.getName(), method.getSignature());
    }
    
    public int lookupInterfaceMethodref(final String class_name, final String method_name, final String signature) {
        final Index index = this.cp_table.get(class_name + "#" + method_name + "#" + signature);
        return (index != null) ? index.index : -1;
    }
    
    public int lookupInterfaceMethodref(final MethodGen method) {
        return this.lookupInterfaceMethodref(method.getClassName(), method.getName(), method.getSignature());
    }
    
    public int addInterfaceMethodref(final String class_name, final String method_name, final String signature) {
        int ret;
        if ((ret = this.lookupInterfaceMethodref(class_name, method_name, signature)) != -1) {
            return ret;
        }
        this.adjustSize();
        final int class_index = this.addClass(class_name);
        final int name_and_type_index = this.addNameAndType(method_name, signature);
        ret = this.index;
        this.constants[this.index++] = new ConstantInterfaceMethodref(class_index, name_and_type_index);
        this.cp_table.put(class_name + "#" + method_name + "#" + signature, new Index(ret));
        return ret;
    }
    
    public int addInterfaceMethodref(final MethodGen method) {
        return this.addInterfaceMethodref(method.getClassName(), method.getName(), method.getSignature());
    }
    
    public int lookupFieldref(final String class_name, final String field_name, final String signature) {
        final Index index = this.cp_table.get(class_name + "&" + field_name + "&" + signature);
        return (index != null) ? index.index : -1;
    }
    
    public int addFieldref(final String class_name, final String field_name, final String signature) {
        int ret;
        if ((ret = this.lookupFieldref(class_name, field_name, signature)) != -1) {
            return ret;
        }
        this.adjustSize();
        final int class_index = this.addClass(class_name);
        final int name_and_type_index = this.addNameAndType(field_name, signature);
        ret = this.index;
        this.constants[this.index++] = new ConstantFieldref(class_index, name_and_type_index);
        this.cp_table.put(class_name + "&" + field_name + "&" + signature, new Index(ret));
        return ret;
    }
    
    public Constant getConstant(final int i) {
        return this.constants[i];
    }
    
    public void setConstant(final int i, final Constant c) {
        this.constants[i] = c;
    }
    
    public ConstantPool getConstantPool() {
        return new ConstantPool(this.constants);
    }
    
    public int getSize() {
        return this.index;
    }
    
    public ConstantPool getFinalConstantPool() {
        final Constant[] cs = new Constant[this.index];
        System.arraycopy(this.constants, 0, cs, 0, this.index);
        return new ConstantPool(cs);
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer();
        for (int i = 1; i < this.index; ++i) {
            buf.append(i + ")" + this.constants[i] + "\n");
        }
        return buf.toString();
    }
    
    public int addConstant(final Constant c, final ConstantPoolGen cp) {
        final Constant[] constants = cp.getConstantPool().getConstantPool();
        switch (c.getTag()) {
            case 8: {
                final ConstantString s = (ConstantString)c;
                final ConstantUtf8 u8 = (ConstantUtf8)constants[s.getStringIndex()];
                return this.addString(u8.getBytes());
            }
            case 7: {
                final ConstantClass s2 = (ConstantClass)c;
                final ConstantUtf8 u8 = (ConstantUtf8)constants[s2.getNameIndex()];
                return this.addClass(u8.getBytes());
            }
            case 12: {
                final ConstantNameAndType n = (ConstantNameAndType)c;
                final ConstantUtf8 u8 = (ConstantUtf8)constants[n.getNameIndex()];
                final ConstantUtf8 u8_2 = (ConstantUtf8)constants[n.getSignatureIndex()];
                return this.addNameAndType(u8.getBytes(), u8_2.getBytes());
            }
            case 1: {
                return this.addUtf8(((ConstantUtf8)c).getBytes());
            }
            case 6: {
                return this.addDouble(((ConstantDouble)c).getBytes());
            }
            case 4: {
                return this.addFloat(((ConstantFloat)c).getBytes());
            }
            case 5: {
                return this.addLong(((ConstantLong)c).getBytes());
            }
            case 3: {
                return this.addInteger(((ConstantInteger)c).getBytes());
            }
            case 9:
            case 10:
            case 11: {
                final ConstantCP m = (ConstantCP)c;
                final ConstantClass clazz = (ConstantClass)constants[m.getClassIndex()];
                final ConstantNameAndType n2 = (ConstantNameAndType)constants[m.getNameAndTypeIndex()];
                ConstantUtf8 u9 = (ConstantUtf8)constants[clazz.getNameIndex()];
                final String class_name = u9.getBytes().replace('/', '.');
                u9 = (ConstantUtf8)constants[n2.getNameIndex()];
                final String name = u9.getBytes();
                u9 = (ConstantUtf8)constants[n2.getSignatureIndex()];
                final String signature = u9.getBytes();
                switch (c.getTag()) {
                    case 11: {
                        return this.addInterfaceMethodref(class_name, name, signature);
                    }
                    case 10: {
                        return this.addMethodref(class_name, name, signature);
                    }
                    case 9: {
                        return this.addFieldref(class_name, name, signature);
                    }
                    default: {
                        throw new RuntimeException("Unknown constant type " + c);
                    }
                }
                break;
            }
            default: {
                throw new RuntimeException("Unknown constant type " + c);
            }
        }
    }
    
    private static class Index implements Serializable
    {
        int index;
        
        Index(final int i) {
            this.index = i;
        }
    }
}
