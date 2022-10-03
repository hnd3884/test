package com.sun.org.apache.bcel.internal.classfile;

public interface Visitor
{
    void visitCode(final Code p0);
    
    void visitCodeException(final CodeException p0);
    
    void visitConstantClass(final ConstantClass p0);
    
    void visitConstantDouble(final ConstantDouble p0);
    
    void visitConstantFieldref(final ConstantFieldref p0);
    
    void visitConstantFloat(final ConstantFloat p0);
    
    void visitConstantInteger(final ConstantInteger p0);
    
    void visitConstantInterfaceMethodref(final ConstantInterfaceMethodref p0);
    
    void visitConstantLong(final ConstantLong p0);
    
    void visitConstantMethodref(final ConstantMethodref p0);
    
    void visitConstantNameAndType(final ConstantNameAndType p0);
    
    void visitConstantPool(final ConstantPool p0);
    
    void visitConstantString(final ConstantString p0);
    
    void visitConstantUtf8(final ConstantUtf8 p0);
    
    void visitConstantValue(final ConstantValue p0);
    
    void visitDeprecated(final Deprecated p0);
    
    void visitExceptionTable(final ExceptionTable p0);
    
    void visitField(final Field p0);
    
    void visitInnerClass(final InnerClass p0);
    
    void visitInnerClasses(final InnerClasses p0);
    
    void visitJavaClass(final JavaClass p0);
    
    void visitLineNumber(final LineNumber p0);
    
    void visitLineNumberTable(final LineNumberTable p0);
    
    void visitLocalVariable(final LocalVariable p0);
    
    void visitLocalVariableTable(final LocalVariableTable p0);
    
    void visitLocalVariableTypeTable(final LocalVariableTypeTable p0);
    
    void visitMethod(final Method p0);
    
    void visitSignature(final Signature p0);
    
    void visitSourceFile(final SourceFile p0);
    
    void visitSynthetic(final Synthetic p0);
    
    void visitUnknown(final Unknown p0);
    
    void visitStackMap(final StackMap p0);
    
    void visitStackMapEntry(final StackMapEntry p0);
}
