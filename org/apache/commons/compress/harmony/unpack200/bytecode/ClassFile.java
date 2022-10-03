package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;

public class ClassFile
{
    public int major;
    public int minor;
    private final int magic = -889275714;
    public ClassConstantPool pool;
    public int accessFlags;
    public int thisClass;
    public int superClass;
    public int[] interfaces;
    public ClassFileEntry[] fields;
    public ClassFileEntry[] methods;
    public Attribute[] attributes;
    
    public ClassFile() {
        this.pool = new ClassConstantPool();
    }
    
    public void write(final DataOutputStream dos) throws IOException {
        dos.writeInt(-889275714);
        dos.writeShort(this.minor);
        dos.writeShort(this.major);
        dos.writeShort(this.pool.size() + 1);
        for (int i = 1; i <= this.pool.size(); ++i) {
            final ConstantPoolEntry entry;
            (entry = (ConstantPoolEntry)this.pool.get(i)).doWrite(dos);
            if (entry.getTag() == 6 || entry.getTag() == 5) {
                ++i;
            }
        }
        dos.writeShort(this.accessFlags);
        dos.writeShort(this.thisClass);
        dos.writeShort(this.superClass);
        dos.writeShort(this.interfaces.length);
        for (int i = 0; i < this.interfaces.length; ++i) {
            dos.writeShort(this.interfaces[i]);
        }
        dos.writeShort(this.fields.length);
        for (int i = 0; i < this.fields.length; ++i) {
            this.fields[i].write(dos);
        }
        dos.writeShort(this.methods.length);
        for (int i = 0; i < this.methods.length; ++i) {
            this.methods[i].write(dos);
        }
        dos.writeShort(this.attributes.length);
        for (int i = 0; i < this.attributes.length; ++i) {
            this.attributes[i].write(dos);
        }
    }
}
