package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class SignatureAttribute extends Attribute
{
    private int signature_index;
    private final CPUTF8 signature;
    private static CPUTF8 attributeName;
    
    public static void setAttributeName(final CPUTF8 cpUTF8Value) {
        SignatureAttribute.attributeName = cpUTF8Value;
    }
    
    public SignatureAttribute(final CPUTF8 value) {
        super(SignatureAttribute.attributeName);
        this.signature = value;
    }
    
    @Override
    protected int getLength() {
        return 2;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        return new ClassFileEntry[] { this.getAttributeName(), this.signature };
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        this.signature.resolve(pool);
        this.signature_index = pool.indexOf(this.signature);
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.signature_index);
    }
    
    @Override
    public String toString() {
        return "Signature: " + this.signature;
    }
}
