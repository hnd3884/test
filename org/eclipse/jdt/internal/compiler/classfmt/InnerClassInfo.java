package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.env.IBinaryNestedType;

public class InnerClassInfo extends ClassFileStruct implements IBinaryNestedType
{
    int innerClassNameIndex;
    int outerClassNameIndex;
    int innerNameIndex;
    private char[] innerClassName;
    private char[] outerClassName;
    private char[] innerName;
    private int accessFlags;
    private boolean readInnerClassName;
    private boolean readOuterClassName;
    private boolean readInnerName;
    
    public InnerClassInfo(final byte[] classFileBytes, final int[] offsets, final int offset) {
        super(classFileBytes, offsets, offset);
        this.innerClassNameIndex = -1;
        this.outerClassNameIndex = -1;
        this.innerNameIndex = -1;
        this.accessFlags = -1;
        this.readInnerClassName = false;
        this.readOuterClassName = false;
        this.readInnerName = false;
        this.innerClassNameIndex = this.u2At(0);
        this.outerClassNameIndex = this.u2At(2);
        this.innerNameIndex = this.u2At(4);
    }
    
    @Override
    public char[] getEnclosingTypeName() {
        if (!this.readOuterClassName) {
            this.readOuterClassName = true;
            if (this.outerClassNameIndex != 0) {
                final int utf8Offset = this.constantPoolOffsets[this.u2At(this.constantPoolOffsets[this.outerClassNameIndex] - this.structOffset + 1)] - this.structOffset;
                this.outerClassName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            }
        }
        return this.outerClassName;
    }
    
    @Override
    public int getModifiers() {
        if (this.accessFlags == -1) {
            this.accessFlags = this.u2At(6);
        }
        return this.accessFlags;
    }
    
    @Override
    public char[] getName() {
        if (!this.readInnerClassName) {
            this.readInnerClassName = true;
            if (this.innerClassNameIndex != 0) {
                final int classOffset = this.constantPoolOffsets[this.innerClassNameIndex] - this.structOffset;
                final int utf8Offset = this.constantPoolOffsets[this.u2At(classOffset + 1)] - this.structOffset;
                this.innerClassName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            }
        }
        return this.innerClassName;
    }
    
    public char[] getSourceName() {
        if (!this.readInnerName) {
            this.readInnerName = true;
            if (this.innerNameIndex != 0) {
                final int utf8Offset = this.constantPoolOffsets[this.innerNameIndex] - this.structOffset;
                this.innerName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            }
        }
        return this.innerName;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        if (this.getName() != null) {
            buffer.append(this.getName());
        }
        buffer.append("\n");
        if (this.getEnclosingTypeName() != null) {
            buffer.append(this.getEnclosingTypeName());
        }
        buffer.append("\n");
        if (this.getSourceName() != null) {
            buffer.append(this.getSourceName());
        }
        return buffer.toString();
    }
    
    void initialize() {
        this.getModifiers();
        this.getName();
        this.getSourceName();
        this.getEnclosingTypeName();
        this.reset();
    }
}
