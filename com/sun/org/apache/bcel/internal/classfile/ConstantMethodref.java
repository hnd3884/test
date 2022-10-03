package com.sun.org.apache.bcel.internal.classfile;

import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantMethodref extends ConstantCP
{
    public ConstantMethodref(final ConstantMethodref c) {
        super((byte)10, c.getClassIndex(), c.getNameAndTypeIndex());
    }
    
    ConstantMethodref(final DataInputStream file) throws IOException {
        super((byte)10, file);
    }
    
    public ConstantMethodref(final int class_index, final int name_and_type_index) {
        super((byte)10, class_index, name_and_type_index);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantMethodref(this);
    }
}
