package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class EnclosingMethodAttribute extends Attribute
{
    private int class_index;
    private int method_index;
    private final CPClass cpClass;
    private final CPNameAndType method;
    private static CPUTF8 attributeName;
    
    public static void setAttributeName(final CPUTF8 cpUTF8Value) {
        EnclosingMethodAttribute.attributeName = cpUTF8Value;
    }
    
    public EnclosingMethodAttribute(final CPClass cpClass, final CPNameAndType method) {
        super(EnclosingMethodAttribute.attributeName);
        this.cpClass = cpClass;
        this.method = method;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        if (this.method != null) {
            return new ClassFileEntry[] { EnclosingMethodAttribute.attributeName, this.cpClass, this.method };
        }
        return new ClassFileEntry[] { EnclosingMethodAttribute.attributeName, this.cpClass };
    }
    
    @Override
    protected int getLength() {
        return 4;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.cpClass.resolve(pool);
        this.class_index = pool.indexOf(this.cpClass);
        if (this.method != null) {
            this.method.resolve(pool);
            this.method_index = pool.indexOf(this.method);
        }
        else {
            this.method_index = 0;
        }
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.class_index);
        dos.writeShort(this.method_index);
    }
    
    @Override
    public String toString() {
        return "EnclosingMethod";
    }
}
