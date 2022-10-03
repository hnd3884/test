package org.apache.tomcat.util.bcel.classfile;

public class AnnotationElementValue extends ElementValue
{
    private final AnnotationEntry annotationEntry;
    
    AnnotationElementValue(final int type, final AnnotationEntry annotationEntry, final ConstantPool cpool) {
        super(type, cpool);
        if (type != 64) {
            throw new IllegalArgumentException("Only element values of type annotation can be built with this ctor - type specified: " + type);
        }
        this.annotationEntry = annotationEntry;
    }
    
    @Override
    public String stringifyValue() {
        return this.annotationEntry.toString();
    }
    
    public AnnotationEntry getAnnotationEntry() {
        return this.annotationEntry;
    }
}
