package com.sun.org.apache.bcel.internal.classfile;

import java.io.IOException;
import java.io.DataInputStream;

public final class ConstantFieldref extends ConstantCP
{
    public ConstantFieldref(final ConstantFieldref c) {
        super((byte)9, c.getClassIndex(), c.getNameAndTypeIndex());
    }
    
    ConstantFieldref(final DataInputStream file) throws IOException {
        super((byte)9, file);
    }
    
    public ConstantFieldref(final int class_index, final int name_and_type_index) {
        super((byte)9, class_index, name_and_type_index);
    }
    
    @Override
    public void accept(final Visitor v) {
        v.visitConstantFieldref(this);
    }
}
