package org.apache.tomcat.util.bcel.classfile;

import java.io.IOException;
import java.io.DataInput;

public class Annotations
{
    private final AnnotationEntry[] annotationTable;
    
    Annotations(final DataInput input, final ConstantPool constantPool) throws IOException {
        final int annotation_table_length = input.readUnsignedShort();
        this.annotationTable = new AnnotationEntry[annotation_table_length];
        for (int i = 0; i < annotation_table_length; ++i) {
            this.annotationTable[i] = new AnnotationEntry(input, constantPool);
        }
    }
    
    public AnnotationEntry[] getAnnotationEntries() {
        return this.annotationTable;
    }
}
