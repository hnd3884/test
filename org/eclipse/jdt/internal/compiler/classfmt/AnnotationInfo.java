package org.eclipse.jdt.internal.compiler.classfmt;

import java.util.Arrays;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.ast.Annotation;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.ConstantPool;
import org.eclipse.jdt.internal.compiler.env.IBinaryElementValuePair;
import org.eclipse.jdt.internal.compiler.env.ClassSignature;
import org.eclipse.jdt.internal.compiler.env.EnumConstantSignature;
import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.impl.LongConstant;
import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
import org.eclipse.jdt.internal.compiler.impl.ShortConstant;
import org.eclipse.jdt.internal.compiler.impl.ByteConstant;
import org.eclipse.jdt.internal.compiler.impl.CharConstant;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;

public class AnnotationInfo extends ClassFileStruct implements IBinaryAnnotation
{
    private char[] typename;
    private ElementValuePairInfo[] pairs;
    long standardAnnotationTagBits;
    int readOffset;
    static Object[] EmptyValueArray;
    
    static {
        AnnotationInfo.EmptyValueArray = new Object[0];
    }
    
    AnnotationInfo(final byte[] classFileBytes, final int[] contantPoolOffsets, final int offset) {
        super(classFileBytes, contantPoolOffsets, offset);
        this.standardAnnotationTagBits = 0L;
        this.readOffset = 0;
    }
    
    AnnotationInfo(final byte[] classFileBytes, final int[] contantPoolOffsets, final int offset, final boolean runtimeVisible, final boolean populate) {
        this(classFileBytes, contantPoolOffsets, offset);
        if (populate) {
            this.decodeAnnotation();
        }
        else {
            this.readOffset = this.scanAnnotation(0, runtimeVisible, true);
        }
    }
    
    private void decodeAnnotation() {
        this.readOffset = 0;
        int utf8Offset = this.constantPoolOffsets[this.u2At(0)] - this.structOffset;
        this.typename = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        final int numberOfPairs = this.u2At(2);
        this.readOffset += 4;
        this.pairs = ((numberOfPairs == 0) ? ElementValuePairInfo.NoMembers : new ElementValuePairInfo[numberOfPairs]);
        for (int i = 0; i < numberOfPairs; ++i) {
            utf8Offset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
            final char[] membername = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            this.readOffset += 2;
            final Object value = this.decodeDefaultValue();
            this.pairs[i] = new ElementValuePairInfo(membername, value);
        }
    }
    
    Object decodeDefaultValue() {
        Object value = null;
        final int tag = this.u1At(this.readOffset);
        ++this.readOffset;
        int constValueOffset = -1;
        switch (tag) {
            case 90: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = BooleanConstant.fromValue(this.i4At(constValueOffset + 1) == 1);
                this.readOffset += 2;
                break;
            }
            case 73: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = IntConstant.fromValue(this.i4At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 67: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = CharConstant.fromValue((char)this.i4At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 66: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = ByteConstant.fromValue((byte)this.i4At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 83: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = ShortConstant.fromValue((short)this.i4At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 68: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = DoubleConstant.fromValue(this.doubleAt(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 70: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = FloatConstant.fromValue(this.floatAt(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 74: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = LongConstant.fromValue(this.i8At(constValueOffset + 1));
                this.readOffset += 2;
                break;
            }
            case 115: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                value = StringConstant.fromValue(String.valueOf(this.utf8At(constValueOffset + 3, this.u2At(constValueOffset + 1))));
                this.readOffset += 2;
                break;
            }
            case 101: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                final char[] typeName = this.utf8At(constValueOffset + 3, this.u2At(constValueOffset + 1));
                this.readOffset += 2;
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                final char[] constName = this.utf8At(constValueOffset + 3, this.u2At(constValueOffset + 1));
                this.readOffset += 2;
                value = new EnumConstantSignature(typeName, constName);
                break;
            }
            case 99: {
                constValueOffset = this.constantPoolOffsets[this.u2At(this.readOffset)] - this.structOffset;
                final char[] className = this.utf8At(constValueOffset + 3, this.u2At(constValueOffset + 1));
                value = new ClassSignature(className);
                this.readOffset += 2;
                break;
            }
            case 64: {
                value = new AnnotationInfo(this.reference, this.constantPoolOffsets, this.readOffset + this.structOffset, false, true);
                this.readOffset += ((AnnotationInfo)value).readOffset;
                break;
            }
            case 91: {
                final int numberOfValues = this.u2At(this.readOffset);
                this.readOffset += 2;
                if (numberOfValues == 0) {
                    value = AnnotationInfo.EmptyValueArray;
                    break;
                }
                final Object[] arrayElements = (Object[])(value = new Object[numberOfValues]);
                for (int i = 0; i < numberOfValues; ++i) {
                    arrayElements[i] = this.decodeDefaultValue();
                }
                break;
            }
            default: {
                throw new IllegalStateException("Unrecognized tag " + (char)tag);
            }
        }
        return value;
    }
    
    @Override
    public IBinaryElementValuePair[] getElementValuePairs() {
        if (this.pairs == null) {
            this.initialize();
        }
        return this.pairs;
    }
    
    @Override
    public char[] getTypeName() {
        return this.typename;
    }
    
    void initialize() {
        if (this.pairs == null) {
            this.decodeAnnotation();
        }
    }
    
    private int readRetentionPolicy(final int offset) {
        int currentOffset = offset;
        final int tag = this.u1At(currentOffset);
        ++currentOffset;
        switch (tag) {
            case 101: {
                int utf8Offset = this.constantPoolOffsets[this.u2At(currentOffset)] - this.structOffset;
                final char[] typeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                currentOffset += 2;
                if (typeName.length == 38 && CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_RETENTIONPOLICY)) {
                    utf8Offset = this.constantPoolOffsets[this.u2At(currentOffset)] - this.structOffset;
                    final char[] constName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                    this.standardAnnotationTagBits |= Annotation.getRetentionPolicy(constName);
                }
                currentOffset += 2;
                break;
            }
            case 66:
            case 67:
            case 68:
            case 70:
            case 73:
            case 74:
            case 83:
            case 90:
            case 99:
            case 115: {
                currentOffset += 2;
                break;
            }
            case 64: {
                currentOffset = this.scanAnnotation(currentOffset, false, false);
                break;
            }
            case 91: {
                final int numberOfValues = this.u2At(currentOffset);
                currentOffset += 2;
                for (int i = 0; i < numberOfValues; ++i) {
                    currentOffset = this.scanElementValue(currentOffset);
                }
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return currentOffset;
    }
    
    private int readTargetValue(final int offset) {
        int currentOffset = offset;
        final int tag = this.u1At(currentOffset);
        ++currentOffset;
        switch (tag) {
            case 101: {
                int utf8Offset = this.constantPoolOffsets[this.u2At(currentOffset)] - this.structOffset;
                final char[] typeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                currentOffset += 2;
                if (typeName.length == 34 && CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_ELEMENTTYPE)) {
                    utf8Offset = this.constantPoolOffsets[this.u2At(currentOffset)] - this.structOffset;
                    final char[] constName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
                    this.standardAnnotationTagBits |= Annotation.getTargetElementType(constName);
                }
                currentOffset += 2;
                break;
            }
            case 66:
            case 67:
            case 68:
            case 70:
            case 73:
            case 74:
            case 83:
            case 90:
            case 99:
            case 115: {
                currentOffset += 2;
                break;
            }
            case 64: {
                currentOffset = this.scanAnnotation(currentOffset, false, false);
                break;
            }
            case 91: {
                final int numberOfValues = this.u2At(currentOffset);
                currentOffset += 2;
                if (numberOfValues == 0) {
                    this.standardAnnotationTagBits |= 0x800000000L;
                    break;
                }
                for (int i = 0; i < numberOfValues; ++i) {
                    currentOffset = this.readTargetValue(currentOffset);
                }
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return currentOffset;
    }
    
    private int scanAnnotation(final int offset, final boolean expectRuntimeVisibleAnno, final boolean toplevel) {
        int currentOffset = offset;
        final int utf8Offset = this.constantPoolOffsets[this.u2At(offset)] - this.structOffset;
        final char[] typeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        if (toplevel) {
            this.typename = typeName;
        }
        final int numberOfPairs = this.u2At(offset + 2);
        currentOffset += 4;
        if (expectRuntimeVisibleAnno && toplevel) {
            switch (typeName.length) {
                case 22: {
                    if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_DEPRECATED)) {
                        this.standardAnnotationTagBits |= 0x400000000000L;
                        return currentOffset;
                    }
                    break;
                }
                case 23: {
                    if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_SAFEVARARGS)) {
                        this.standardAnnotationTagBits |= 0x8000000000000L;
                        return currentOffset;
                    }
                    break;
                }
                case 29: {
                    if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_TARGET)) {
                        currentOffset += 2;
                        return this.readTargetValue(currentOffset);
                    }
                    break;
                }
                case 32: {
                    if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_RETENTION)) {
                        currentOffset += 2;
                        return this.readRetentionPolicy(currentOffset);
                    }
                    if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_INHERITED)) {
                        this.standardAnnotationTagBits |= 0x1000000000000L;
                        return currentOffset;
                    }
                    break;
                }
                case 33: {
                    if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_ANNOTATION_DOCUMENTED)) {
                        this.standardAnnotationTagBits |= 0x800000000000L;
                        return currentOffset;
                    }
                    break;
                }
                case 52: {
                    if (CharOperation.equals(typeName, ConstantPool.JAVA_LANG_INVOKE_METHODHANDLE_POLYMORPHICSIGNATURE)) {
                        this.standardAnnotationTagBits |= 0x10000000000000L;
                        return currentOffset;
                    }
                    break;
                }
            }
        }
        for (int i = 0; i < numberOfPairs; ++i) {
            currentOffset += 2;
            currentOffset = this.scanElementValue(currentOffset);
        }
        return currentOffset;
    }
    
    private int scanElementValue(final int offset) {
        int currentOffset = offset;
        final int tag = this.u1At(currentOffset);
        ++currentOffset;
        switch (tag) {
            case 66:
            case 67:
            case 68:
            case 70:
            case 73:
            case 74:
            case 83:
            case 90:
            case 99:
            case 115: {
                currentOffset += 2;
                break;
            }
            case 101: {
                currentOffset += 4;
                break;
            }
            case 64: {
                currentOffset = this.scanAnnotation(currentOffset, false, false);
                break;
            }
            case 91: {
                final int numberOfValues = this.u2At(currentOffset);
                currentOffset += 2;
                for (int i = 0; i < numberOfValues; ++i) {
                    currentOffset = this.scanElementValue(currentOffset);
                }
                break;
            }
            default: {
                throw new IllegalStateException();
            }
        }
        return currentOffset;
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append('@');
        buffer.append(this.typename);
        if (this.pairs != null) {
            buffer.append('(');
            buffer.append("\n\t");
            for (int i = 0, len = this.pairs.length; i < len; ++i) {
                if (i > 0) {
                    buffer.append(",\n\t");
                }
                buffer.append(this.pairs[i]);
            }
            buffer.append(')');
        }
        return buffer.toString();
    }
    
    @Override
    public int hashCode() {
        int result = 1;
        result = 31 * result + Util.hashCode(this.pairs);
        result = 31 * result + CharOperation.hashCode(this.typename);
        return result;
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (this.getClass() != obj.getClass()) {
            return false;
        }
        final AnnotationInfo other = (AnnotationInfo)obj;
        return Arrays.equals(this.pairs, other.pairs) && Arrays.equals(this.typename, other.typename);
    }
}
