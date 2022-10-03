package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Utility;
import java.util.Iterator;
import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;
import com.sun.org.apache.bcel.internal.classfile.ConstantObject;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.ConstantValue;
import com.sun.org.apache.bcel.internal.classfile.Field;
import java.util.ArrayList;

public class FieldGen extends FieldGenOrMethodGen
{
    private Object value;
    private ArrayList observers;
    
    public FieldGen(final int access_flags, final Type type, final String name, final ConstantPoolGen cp) {
        this.value = null;
        this.setAccessFlags(access_flags);
        this.setType(type);
        this.setName(name);
        this.setConstantPool(cp);
    }
    
    public FieldGen(final Field field, final ConstantPoolGen cp) {
        this(field.getAccessFlags(), Type.getType(field.getSignature()), field.getName(), cp);
        final Attribute[] attrs = field.getAttributes();
        for (int i = 0; i < attrs.length; ++i) {
            if (attrs[i] instanceof ConstantValue) {
                this.setValue(((ConstantValue)attrs[i]).getConstantValueIndex());
            }
            else {
                this.addAttribute(attrs[i]);
            }
        }
    }
    
    private void setValue(final int index) {
        final ConstantPool cp = this.cp.getConstantPool();
        final Constant c = cp.getConstant(index);
        this.value = ((ConstantObject)c).getConstantValue(cp);
    }
    
    public void setInitValue(final String str) {
        this.checkType(new ObjectType("java.lang.String"));
        if (str != null) {
            this.value = str;
        }
    }
    
    public void setInitValue(final long l) {
        this.checkType(Type.LONG);
        if (l != 0L) {
            this.value = new Long(l);
        }
    }
    
    public void setInitValue(final int i) {
        this.checkType(Type.INT);
        if (i != 0) {
            this.value = new Integer(i);
        }
    }
    
    public void setInitValue(final short s) {
        this.checkType(Type.SHORT);
        if (s != 0) {
            this.value = new Integer(s);
        }
    }
    
    public void setInitValue(final char c) {
        this.checkType(Type.CHAR);
        if (c != '\0') {
            this.value = new Integer(c);
        }
    }
    
    public void setInitValue(final byte b) {
        this.checkType(Type.BYTE);
        if (b != 0) {
            this.value = new Integer(b);
        }
    }
    
    public void setInitValue(final boolean b) {
        this.checkType(Type.BOOLEAN);
        if (b) {
            this.value = new Integer(1);
        }
    }
    
    public void setInitValue(final float f) {
        this.checkType(Type.FLOAT);
        if (f != 0.0) {
            this.value = new Float(f);
        }
    }
    
    public void setInitValue(final double d) {
        this.checkType(Type.DOUBLE);
        if (d != 0.0) {
            this.value = new Double(d);
        }
    }
    
    public void cancelInitValue() {
        this.value = null;
    }
    
    private void checkType(final Type atype) {
        if (this.type == null) {
            throw new ClassGenException("You haven't defined the type of the field yet");
        }
        if (!this.isFinal()) {
            throw new ClassGenException("Only final fields may have an initial value!");
        }
        if (!this.type.equals(atype)) {
            throw new ClassGenException("Types are not compatible: " + this.type + " vs. " + atype);
        }
    }
    
    public Field getField() {
        final String signature = this.getSignature();
        final int name_index = this.cp.addUtf8(this.name);
        final int signature_index = this.cp.addUtf8(signature);
        if (this.value != null) {
            this.checkType(this.type);
            final int index = this.addConstant();
            this.addAttribute(new ConstantValue(this.cp.addUtf8("ConstantValue"), 2, index, this.cp.getConstantPool()));
        }
        return new Field(this.access_flags, name_index, signature_index, this.getAttributes(), this.cp.getConstantPool());
    }
    
    private int addConstant() {
        switch (this.type.getType()) {
            case 4:
            case 5:
            case 8:
            case 9:
            case 10: {
                return this.cp.addInteger((int)this.value);
            }
            case 6: {
                return this.cp.addFloat((float)this.value);
            }
            case 7: {
                return this.cp.addDouble((double)this.value);
            }
            case 11: {
                return this.cp.addLong((long)this.value);
            }
            case 14: {
                return this.cp.addString((String)this.value);
            }
            default: {
                throw new RuntimeException("Oops: Unhandled : " + this.type.getType());
            }
        }
    }
    
    @Override
    public String getSignature() {
        return this.type.getSignature();
    }
    
    public void addObserver(final FieldObserver o) {
        if (this.observers == null) {
            this.observers = new ArrayList();
        }
        this.observers.add(o);
    }
    
    public void removeObserver(final FieldObserver o) {
        if (this.observers != null) {
            this.observers.remove(o);
        }
    }
    
    public void update() {
        if (this.observers != null) {
            final Iterator e = this.observers.iterator();
            while (e.hasNext()) {
                e.next().notify(this);
            }
        }
    }
    
    public String getInitValue() {
        if (this.value != null) {
            return this.value.toString();
        }
        return null;
    }
    
    @Override
    public final String toString() {
        String access = Utility.accessToString(this.access_flags);
        access = (access.equals("") ? "" : (access + " "));
        final String signature = this.type.toString();
        final String name = this.getName();
        final StringBuffer buf = new StringBuffer(access + signature + " " + name);
        final String value = this.getInitValue();
        if (value != null) {
            buf.append(" = " + value);
        }
        return buf.toString();
    }
    
    public FieldGen copy(final ConstantPoolGen cp) {
        final FieldGen fg = (FieldGen)this.clone();
        fg.setConstantPool(cp);
        return fg;
    }
}
