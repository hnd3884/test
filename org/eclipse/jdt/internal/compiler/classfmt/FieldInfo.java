package org.eclipse.jdt.internal.compiler.classfmt;

import org.eclipse.jdt.internal.compiler.impl.StringConstant;
import org.eclipse.jdt.internal.compiler.impl.LongConstant;
import org.eclipse.jdt.internal.compiler.impl.DoubleConstant;
import org.eclipse.jdt.internal.compiler.impl.FloatConstant;
import org.eclipse.jdt.internal.compiler.impl.ShortConstant;
import org.eclipse.jdt.internal.compiler.impl.ByteConstant;
import org.eclipse.jdt.internal.compiler.impl.CharConstant;
import org.eclipse.jdt.internal.compiler.impl.IntConstant;
import org.eclipse.jdt.internal.compiler.impl.BooleanConstant;
import org.eclipse.jdt.internal.compiler.util.Util;
import org.eclipse.jdt.internal.compiler.env.IBinaryTypeAnnotation;
import org.eclipse.jdt.internal.compiler.env.IBinaryAnnotation;
import org.eclipse.jdt.core.compiler.CharOperation;
import org.eclipse.jdt.internal.compiler.codegen.AttributeNamesConstants;
import org.eclipse.jdt.internal.compiler.impl.Constant;
import org.eclipse.jdt.internal.compiler.env.IBinaryField;

public class FieldInfo extends ClassFileStruct implements IBinaryField, Comparable
{
    protected int accessFlags;
    protected int attributeBytes;
    protected Constant constant;
    protected char[] descriptor;
    protected char[] name;
    protected char[] signature;
    protected int signatureUtf8Offset;
    protected long tagBits;
    protected Object wrappedConstantValue;
    
    public static FieldInfo createField(final byte[] classFileBytes, final int[] offsets, final int offset) {
        final FieldInfo fieldInfo = new FieldInfo(classFileBytes, offsets, offset);
        final int attributesCount = fieldInfo.u2At(6);
        int readOffset = 8;
        AnnotationInfo[] annotations = null;
        TypeAnnotationInfo[] typeAnnotations = null;
        for (int i = 0; i < attributesCount; ++i) {
            final int utf8Offset = fieldInfo.constantPoolOffsets[fieldInfo.u2At(readOffset)] - fieldInfo.structOffset;
            final char[] attributeName = fieldInfo.utf8At(utf8Offset + 3, fieldInfo.u2At(utf8Offset + 1));
            if (attributeName.length > 0) {
                switch (attributeName[0]) {
                    case 'S': {
                        if (CharOperation.equals(AttributeNamesConstants.SignatureName, attributeName)) {
                            fieldInfo.signatureUtf8Offset = fieldInfo.constantPoolOffsets[fieldInfo.u2At(readOffset + 6)] - fieldInfo.structOffset;
                            break;
                        }
                        break;
                    }
                    case 'R': {
                        AnnotationInfo[] decodedAnnotations = null;
                        TypeAnnotationInfo[] decodedTypeAnnotations = null;
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleAnnotationsName)) {
                            decodedAnnotations = fieldInfo.decodeAnnotations(readOffset, true);
                        }
                        else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleAnnotationsName)) {
                            decodedAnnotations = fieldInfo.decodeAnnotations(readOffset, false);
                        }
                        else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeVisibleTypeAnnotationsName)) {
                            decodedTypeAnnotations = fieldInfo.decodeTypeAnnotations(readOffset, true);
                        }
                        else if (CharOperation.equals(attributeName, AttributeNamesConstants.RuntimeInvisibleTypeAnnotationsName)) {
                            decodedTypeAnnotations = fieldInfo.decodeTypeAnnotations(readOffset, false);
                        }
                        if (decodedAnnotations != null) {
                            if (annotations == null) {
                                annotations = decodedAnnotations;
                                break;
                            }
                            final int length = annotations.length;
                            final AnnotationInfo[] combined = new AnnotationInfo[length + decodedAnnotations.length];
                            System.arraycopy(annotations, 0, combined, 0, length);
                            System.arraycopy(decodedAnnotations, 0, combined, length, decodedAnnotations.length);
                            annotations = combined;
                            break;
                        }
                        else {
                            if (decodedTypeAnnotations == null) {
                                break;
                            }
                            if (typeAnnotations == null) {
                                typeAnnotations = decodedTypeAnnotations;
                                break;
                            }
                            final int length = typeAnnotations.length;
                            final TypeAnnotationInfo[] combined2 = new TypeAnnotationInfo[length + decodedTypeAnnotations.length];
                            System.arraycopy(typeAnnotations, 0, combined2, 0, length);
                            System.arraycopy(decodedTypeAnnotations, 0, combined2, length, decodedTypeAnnotations.length);
                            typeAnnotations = combined2;
                            break;
                        }
                        break;
                    }
                }
            }
            readOffset += (int)(6L + fieldInfo.u4At(readOffset + 2));
        }
        fieldInfo.attributeBytes = readOffset;
        if (typeAnnotations != null) {
            return new FieldInfoWithTypeAnnotation(fieldInfo, annotations, typeAnnotations);
        }
        if (annotations != null) {
            return new FieldInfoWithAnnotation(fieldInfo, annotations);
        }
        return fieldInfo;
    }
    
    protected FieldInfo(final byte[] classFileBytes, final int[] offsets, final int offset) {
        super(classFileBytes, offsets, offset);
        this.accessFlags = -1;
        this.signatureUtf8Offset = -1;
    }
    
    private AnnotationInfo[] decodeAnnotations(final int offset, final boolean runtimeVisible) {
        final int numberOfAnnotations = this.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            int readOffset = offset + 8;
            AnnotationInfo[] newInfos = null;
            int newInfoCount = 0;
            for (int i = 0; i < numberOfAnnotations; ++i) {
                final AnnotationInfo newInfo = new AnnotationInfo(this.reference, this.constantPoolOffsets, readOffset + this.structOffset, runtimeVisible, false);
                readOffset += newInfo.readOffset;
                final long standardTagBits = newInfo.standardAnnotationTagBits;
                if (standardTagBits != 0L) {
                    this.tagBits |= standardTagBits;
                }
                else {
                    if (newInfos == null) {
                        newInfos = new AnnotationInfo[numberOfAnnotations - i];
                    }
                    newInfos[newInfoCount++] = newInfo;
                }
            }
            if (newInfos != null) {
                if (newInfoCount != newInfos.length) {
                    System.arraycopy(newInfos, 0, newInfos = new AnnotationInfo[newInfoCount], 0, newInfoCount);
                }
                return newInfos;
            }
        }
        return null;
    }
    
    TypeAnnotationInfo[] decodeTypeAnnotations(final int offset, final boolean runtimeVisible) {
        final int numberOfAnnotations = this.u2At(offset + 6);
        if (numberOfAnnotations > 0) {
            int readOffset = offset + 8;
            final TypeAnnotationInfo[] typeAnnos = new TypeAnnotationInfo[numberOfAnnotations];
            for (int i = 0; i < numberOfAnnotations; ++i) {
                final TypeAnnotationInfo newInfo = new TypeAnnotationInfo(this.reference, this.constantPoolOffsets, readOffset + this.structOffset, runtimeVisible, false);
                readOffset += newInfo.readOffset;
                typeAnnos[i] = newInfo;
            }
            return typeAnnos;
        }
        return null;
    }
    
    @Override
    public int compareTo(final Object o) {
        return new String(this.getName()).compareTo(new String(((FieldInfo)o).getName()));
    }
    
    @Override
    public boolean equals(final Object o) {
        return o instanceof FieldInfo && CharOperation.equals(this.getName(), ((FieldInfo)o).getName());
    }
    
    @Override
    public int hashCode() {
        return CharOperation.hashCode(this.getName());
    }
    
    @Override
    public Constant getConstant() {
        if (this.constant == null) {
            this.readConstantAttribute();
        }
        return this.constant;
    }
    
    @Override
    public char[] getGenericSignature() {
        if (this.signatureUtf8Offset != -1) {
            if (this.signature == null) {
                this.signature = this.utf8At(this.signatureUtf8Offset + 3, this.u2At(this.signatureUtf8Offset + 1));
            }
            return this.signature;
        }
        return null;
    }
    
    @Override
    public int getModifiers() {
        if (this.accessFlags == -1) {
            this.accessFlags = this.u2At(0);
            this.readModifierRelatedAttributes();
        }
        return this.accessFlags;
    }
    
    @Override
    public char[] getName() {
        if (this.name == null) {
            final int utf8Offset = this.constantPoolOffsets[this.u2At(2)] - this.structOffset;
            this.name = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        }
        return this.name;
    }
    
    @Override
    public long getTagBits() {
        return this.tagBits;
    }
    
    @Override
    public char[] getTypeName() {
        if (this.descriptor == null) {
            final int utf8Offset = this.constantPoolOffsets[this.u2At(4)] - this.structOffset;
            this.descriptor = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
        }
        return this.descriptor;
    }
    
    @Override
    public IBinaryAnnotation[] getAnnotations() {
        return null;
    }
    
    @Override
    public IBinaryTypeAnnotation[] getTypeAnnotations() {
        return null;
    }
    
    public Object getWrappedConstantValue() {
        if (this.wrappedConstantValue == null && this.hasConstant()) {
            final Constant fieldConstant = this.getConstant();
            switch (fieldConstant.typeID()) {
                case 10: {
                    this.wrappedConstantValue = fieldConstant.intValue();
                    break;
                }
                case 3: {
                    this.wrappedConstantValue = fieldConstant.byteValue();
                    break;
                }
                case 4: {
                    this.wrappedConstantValue = fieldConstant.shortValue();
                    break;
                }
                case 2: {
                    this.wrappedConstantValue = fieldConstant.charValue();
                    break;
                }
                case 9: {
                    this.wrappedConstantValue = new Float(fieldConstant.floatValue());
                    break;
                }
                case 8: {
                    this.wrappedConstantValue = new Double(fieldConstant.doubleValue());
                    break;
                }
                case 5: {
                    this.wrappedConstantValue = Util.toBoolean(fieldConstant.booleanValue());
                    break;
                }
                case 7: {
                    this.wrappedConstantValue = fieldConstant.longValue();
                    break;
                }
                case 11: {
                    this.wrappedConstantValue = fieldConstant.stringValue();
                    break;
                }
            }
        }
        return this.wrappedConstantValue;
    }
    
    public boolean hasConstant() {
        return this.getConstant() != Constant.NotAConstant;
    }
    
    protected void initialize() {
        this.getModifiers();
        this.getName();
        this.getConstant();
        this.getTypeName();
        this.getGenericSignature();
        this.reset();
    }
    
    public boolean isSynthetic() {
        return (this.getModifiers() & 0x1000) != 0x0;
    }
    
    private void readConstantAttribute() {
        final int attributesCount = this.u2At(6);
        int readOffset = 8;
        boolean isConstant = false;
        for (int i = 0; i < attributesCount; ++i) {
            int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
            final char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            Label_0422: {
                if (CharOperation.equals(attributeName, AttributeNamesConstants.ConstantValueName)) {
                    isConstant = true;
                    final int relativeOffset = this.constantPoolOffsets[this.u2At(readOffset + 6)] - this.structOffset;
                    switch (this.u1At(relativeOffset)) {
                        case 3: {
                            final char[] sign = this.getTypeName();
                            if (sign.length != 1) {
                                this.constant = Constant.NotAConstant;
                                break;
                            }
                            switch (sign[0]) {
                                case 'Z': {
                                    this.constant = BooleanConstant.fromValue(this.i4At(relativeOffset + 1) == 1);
                                    break Label_0422;
                                }
                                case 'I': {
                                    this.constant = IntConstant.fromValue(this.i4At(relativeOffset + 1));
                                    break Label_0422;
                                }
                                case 'C': {
                                    this.constant = CharConstant.fromValue((char)this.i4At(relativeOffset + 1));
                                    break Label_0422;
                                }
                                case 'B': {
                                    this.constant = ByteConstant.fromValue((byte)this.i4At(relativeOffset + 1));
                                    break Label_0422;
                                }
                                case 'S': {
                                    this.constant = ShortConstant.fromValue((short)this.i4At(relativeOffset + 1));
                                    break Label_0422;
                                }
                                default: {
                                    this.constant = Constant.NotAConstant;
                                    break Label_0422;
                                }
                            }
                            break;
                        }
                        case 4: {
                            this.constant = FloatConstant.fromValue(this.floatAt(relativeOffset + 1));
                            break;
                        }
                        case 6: {
                            this.constant = DoubleConstant.fromValue(this.doubleAt(relativeOffset + 1));
                            break;
                        }
                        case 5: {
                            this.constant = LongConstant.fromValue(this.i8At(relativeOffset + 1));
                            break;
                        }
                        case 8: {
                            utf8Offset = this.constantPoolOffsets[this.u2At(relativeOffset + 1)] - this.structOffset;
                            this.constant = StringConstant.fromValue(String.valueOf(this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1))));
                            break;
                        }
                    }
                }
            }
            readOffset += (int)(6L + this.u4At(readOffset + 2));
        }
        if (!isConstant) {
            this.constant = Constant.NotAConstant;
        }
    }
    
    private void readModifierRelatedAttributes() {
        final int attributesCount = this.u2At(6);
        int readOffset = 8;
        for (int i = 0; i < attributesCount; ++i) {
            final int utf8Offset = this.constantPoolOffsets[this.u2At(readOffset)] - this.structOffset;
            final char[] attributeName = this.utf8At(utf8Offset + 3, this.u2At(utf8Offset + 1));
            if (attributeName.length != 0) {
                switch (attributeName[0]) {
                    case 'D': {
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.DeprecatedName)) {
                            this.accessFlags |= 0x100000;
                            break;
                        }
                        break;
                    }
                    case 'S': {
                        if (CharOperation.equals(attributeName, AttributeNamesConstants.SyntheticName)) {
                            this.accessFlags |= 0x1000;
                            break;
                        }
                        break;
                    }
                }
            }
            readOffset += (int)(6L + this.u4At(readOffset + 2));
        }
    }
    
    public int sizeInBytes() {
        return this.attributeBytes;
    }
    
    public void throwFormatException() throws ClassFormatException {
        throw new ClassFormatException(17);
    }
    
    @Override
    public String toString() {
        final StringBuffer buffer = new StringBuffer(this.getClass().getName());
        this.toStringContent(buffer);
        return buffer.toString();
    }
    
    protected void toStringContent(final StringBuffer buffer) {
        final int modifiers = this.getModifiers();
        buffer.append('{').append(String.valueOf(((modifiers & 0x100000) != 0x0) ? "deprecated " : Util.EMPTY_STRING) + (((modifiers & 0x1) == 0x1) ? "public " : Util.EMPTY_STRING) + (((modifiers & 0x2) == 0x2) ? "private " : Util.EMPTY_STRING) + (((modifiers & 0x4) == 0x4) ? "protected " : Util.EMPTY_STRING) + (((modifiers & 0x8) == 0x8) ? "static " : Util.EMPTY_STRING) + (((modifiers & 0x10) == 0x10) ? "final " : Util.EMPTY_STRING) + (((modifiers & 0x40) == 0x40) ? "volatile " : Util.EMPTY_STRING) + (((modifiers & 0x80) == 0x80) ? "transient " : Util.EMPTY_STRING)).append(this.getTypeName()).append(' ').append(this.getName()).append(' ').append(this.getConstant()).append('}').toString();
    }
}
