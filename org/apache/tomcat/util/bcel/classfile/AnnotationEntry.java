package org.apache.tomcat.util.bcel.classfile;

import java.io.IOException;
import java.util.ArrayList;
import java.io.DataInput;
import java.util.List;

public class AnnotationEntry
{
    private final int typeIndex;
    private final ConstantPool constantPool;
    private final List<ElementValuePair> element_value_pairs;
    
    AnnotationEntry(final DataInput input, final ConstantPool constantPool) throws IOException {
        this.constantPool = constantPool;
        this.typeIndex = input.readUnsignedShort();
        final int num_element_value_pairs = input.readUnsignedShort();
        this.element_value_pairs = new ArrayList<ElementValuePair>(num_element_value_pairs);
        for (int i = 0; i < num_element_value_pairs; ++i) {
            this.element_value_pairs.add(new ElementValuePair(input, constantPool));
        }
    }
    
    public String getAnnotationType() {
        final ConstantUtf8 c = (ConstantUtf8)this.constantPool.getConstant(this.typeIndex, (byte)1);
        return c.getBytes();
    }
    
    public List<ElementValuePair> getElementValuePairs() {
        return this.element_value_pairs;
    }
}
