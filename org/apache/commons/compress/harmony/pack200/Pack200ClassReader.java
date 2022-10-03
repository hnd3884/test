package org.apache.commons.compress.harmony.pack200;

import org.objectweb.asm.ClassReader;

public class Pack200ClassReader extends ClassReader
{
    private boolean lastConstantHadWideIndex;
    private int lastUnsignedShort;
    private boolean anySyntheticAttributes;
    private String fileName;
    
    public Pack200ClassReader(final byte[] b) {
        super(b);
    }
    
    public int readUnsignedShort(final int index) {
        final int unsignedShort = super.readUnsignedShort(index);
        if (this.b[index - 1] == 19) {
            this.lastUnsignedShort = unsignedShort;
        }
        else {
            this.lastUnsignedShort = -32768;
        }
        return unsignedShort;
    }
    
    public Object readConst(final int item, final char[] buf) {
        this.lastConstantHadWideIndex = (item == this.lastUnsignedShort);
        return super.readConst(item, buf);
    }
    
    public String readUTF8(final int arg0, final char[] arg1) {
        final String utf8 = super.readUTF8(arg0, arg1);
        if (!this.anySyntheticAttributes && "Synthetic".equals(utf8)) {
            this.anySyntheticAttributes = true;
        }
        return utf8;
    }
    
    public boolean lastConstantHadWideIndex() {
        return this.lastConstantHadWideIndex;
    }
    
    public boolean hasSyntheticAttributes() {
        return this.anySyntheticAttributes;
    }
    
    public void setFileName(final String name) {
        this.fileName = name;
    }
    
    public String getFileName() {
        return this.fileName;
    }
}
