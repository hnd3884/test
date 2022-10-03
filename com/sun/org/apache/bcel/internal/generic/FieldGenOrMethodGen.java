package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Attribute;
import java.util.ArrayList;
import com.sun.org.apache.bcel.internal.classfile.AccessFlags;

public abstract class FieldGenOrMethodGen extends AccessFlags implements NamedAndTyped, Cloneable
{
    protected String name;
    protected Type type;
    protected ConstantPoolGen cp;
    private ArrayList attribute_vec;
    
    protected FieldGenOrMethodGen() {
        this.attribute_vec = new ArrayList();
    }
    
    @Override
    public void setType(final Type type) {
        if (type.getType() == 16) {
            throw new IllegalArgumentException("Type can not be " + type);
        }
        this.type = type;
    }
    
    @Override
    public Type getType() {
        return this.type;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public void setName(final String name) {
        this.name = name;
    }
    
    public ConstantPoolGen getConstantPool() {
        return this.cp;
    }
    
    public void setConstantPool(final ConstantPoolGen cp) {
        this.cp = cp;
    }
    
    public void addAttribute(final Attribute a) {
        this.attribute_vec.add(a);
    }
    
    public void removeAttribute(final Attribute a) {
        this.attribute_vec.remove(a);
    }
    
    public void removeAttributes() {
        this.attribute_vec.clear();
    }
    
    public Attribute[] getAttributes() {
        final Attribute[] attributes = new Attribute[this.attribute_vec.size()];
        this.attribute_vec.toArray(attributes);
        return attributes;
    }
    
    public abstract String getSignature();
    
    public Object clone() {
        try {
            return super.clone();
        }
        catch (final CloneNotSupportedException e) {
            System.err.println(e);
            return null;
        }
    }
}
