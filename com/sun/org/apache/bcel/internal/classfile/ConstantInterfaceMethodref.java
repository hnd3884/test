package com.sun.org.apache.bcel.internal.classfile;

import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantInterfaceMethodref extends ConstantCP
{
    public ConstantInterfaceMethodref(final ConstantInterfaceMethodref c) {
        super((byte)11, c.getClassIndex(), c.getNameAndTypeIndex());
    }
    
    ConstantInterfaceMethodref(final DataInputStream file) throws IOException {
        super((byte)11, file);
    }
    
    public ConstantInterfaceMethodref(final int class_index, final int name_and_type_index) {
        super((byte)11, class_index, name_and_type_index);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantInterfaceMethodref(this);
    }
}
