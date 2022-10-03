package org.apache.commons.compress.harmony.unpack200.bytecode;

import java.io.IOException;
import java.io.DataOutputStream;
import java.util.Collection;
import java.util.ArrayList;
import org.apache.commons.compress.harmony.unpack200.Segment;
import java.util.List;

public class CodeAttribute extends BCIRenumberedAttribute
{
    public List attributes;
    public List byteCodeOffsets;
    public List byteCodes;
    public int codeLength;
    public List exceptionTable;
    public int maxLocals;
    public int maxStack;
    private static CPUTF8 attributeName;
    
    public CodeAttribute(final int maxStack, final int maxLocals, final byte[] codePacked, final Segment segment, final OperandManager operandManager, final List exceptionTable) {
        super(CodeAttribute.attributeName);
        this.attributes = new ArrayList();
        this.byteCodeOffsets = new ArrayList();
        this.byteCodes = new ArrayList();
        this.maxLocals = maxLocals;
        this.maxStack = maxStack;
        this.codeLength = 0;
        this.exceptionTable = exceptionTable;
        this.byteCodeOffsets.add(0);
        int byteCodeIndex = 0;
        for (int i = 0; i < codePacked.length; ++i) {
            final ByteCode byteCode = ByteCode.getByteCode(codePacked[i] & 0xFF);
            byteCode.setByteCodeIndex(byteCodeIndex);
            ++byteCodeIndex;
            byteCode.extractOperands(operandManager, segment, this.codeLength);
            this.byteCodes.add(byteCode);
            this.codeLength += byteCode.getLength();
            final int lastBytecodePosition = this.byteCodeOffsets.get(this.byteCodeOffsets.size() - 1);
            if (byteCode.hasMultipleByteCodes()) {
                this.byteCodeOffsets.add(lastBytecodePosition + 1);
                ++byteCodeIndex;
            }
            if (i < codePacked.length - 1) {
                this.byteCodeOffsets.add(lastBytecodePosition + byteCode.getLength());
            }
            if (byteCode.getOpcode() == 196) {
                ++i;
            }
        }
        for (int i = 0; i < this.byteCodes.size(); ++i) {
            final ByteCode byteCode = this.byteCodes.get(i);
            byteCode.applyByteCodeTargetFixup(this);
        }
    }
    
    @Override
    protected int getLength() {
        int attributesSize = 0;
        for (int it = 0; it < this.attributes.size(); ++it) {
            final Attribute attribute = this.attributes.get(it);
            attributesSize += attribute.getLengthIncludingHeader();
        }
        return 8 + this.codeLength + 2 + this.exceptionTable.size() * 8 + 2 + attributesSize;
    }
    
    @Override
    protected ClassFileEntry[] getNestedClassFileEntries() {
        final ArrayList nestedEntries = new ArrayList(this.attributes.size() + this.byteCodes.size() + 10);
        nestedEntries.add(this.getAttributeName());
        nestedEntries.addAll(this.byteCodes);
        nestedEntries.addAll(this.attributes);
        for (int iter = 0; iter < this.exceptionTable.size(); ++iter) {
            final ExceptionTableEntry entry = this.exceptionTable.get(iter);
            final CPClass catchType = entry.getCatchType();
            if (catchType != null) {
                nestedEntries.add(catchType);
            }
        }
        final ClassFileEntry[] nestedEntryArray = new ClassFileEntry[nestedEntries.size()];
        nestedEntries.toArray(nestedEntryArray);
        return nestedEntryArray;
    }
    
    @Override
    protected void resolve(final ClassConstantPool pool) {
        super.resolve(pool);
        for (int it = 0; it < this.attributes.size(); ++it) {
            final Attribute attribute = this.attributes.get(it);
            attribute.resolve(pool);
        }
        for (int it = 0; it < this.byteCodes.size(); ++it) {
            final ByteCode byteCode = this.byteCodes.get(it);
            byteCode.resolve(pool);
        }
        for (int it = 0; it < this.exceptionTable.size(); ++it) {
            final ExceptionTableEntry entry = this.exceptionTable.get(it);
            entry.resolve(pool);
        }
    }
    
    @Override
    public String toString() {
        return "Code: " + this.getLength() + " bytes";
    }
    
    @Override
    protected void writeBody(final DataOutputStream dos) throws IOException {
        dos.writeShort(this.maxStack);
        dos.writeShort(this.maxLocals);
        dos.writeInt(this.codeLength);
        for (int it = 0; it < this.byteCodes.size(); ++it) {
            final ByteCode byteCode = this.byteCodes.get(it);
            byteCode.write(dos);
        }
        dos.writeShort(this.exceptionTable.size());
        for (int it = 0; it < this.exceptionTable.size(); ++it) {
            final ExceptionTableEntry entry = this.exceptionTable.get(it);
            entry.write(dos);
        }
        dos.writeShort(this.attributes.size());
        for (int it = 0; it < this.attributes.size(); ++it) {
            final Attribute attribute = this.attributes.get(it);
            attribute.write(dos);
        }
    }
    
    public void addAttribute(final Attribute attribute) {
        this.attributes.add(attribute);
        if (attribute instanceof LocalVariableTableAttribute) {
            ((LocalVariableTableAttribute)attribute).setCodeLength(this.codeLength);
        }
        if (attribute instanceof LocalVariableTypeTableAttribute) {
            ((LocalVariableTypeTableAttribute)attribute).setCodeLength(this.codeLength);
        }
    }
    
    @Override
    protected int[] getStartPCs() {
        return null;
    }
    
    @Override
    public void renumber(final List byteCodeOffsets) {
        for (int iter = 0; iter < this.exceptionTable.size(); ++iter) {
            final ExceptionTableEntry entry = this.exceptionTable.get(iter);
            entry.renumber(byteCodeOffsets);
        }
    }
    
    public static void setAttributeName(final CPUTF8 attributeName) {
        CodeAttribute.attributeName = attributeName;
    }
}
