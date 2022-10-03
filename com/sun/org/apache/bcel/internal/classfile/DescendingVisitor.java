package com.sun.org.apache.bcel.internal.classfile;

import java.util.Stack;

public class DescendingVisitor implements Visitor
{
    private JavaClass clazz;
    private Visitor visitor;
    private Stack stack;
    
    public Object predecessor() {
        return this.predecessor(0);
    }
    
    public Object predecessor(final int level) {
        final int size = this.stack.size();
        if (size < 2 || level < 0) {
            return null;
        }
        return this.stack.elementAt(size - (level + 2));
    }
    
    public Object current() {
        return this.stack.peek();
    }
    
    public DescendingVisitor(final JavaClass clazz, final Visitor visitor) {
        this.stack = new Stack();
        this.clazz = clazz;
        this.visitor = visitor;
    }
    
    public void visit() {
        this.clazz.accept(this);
    }
    
    @Override
    public void visitJavaClass(final JavaClass clazz) {
        this.stack.push(clazz);
        clazz.accept(this.visitor);
        final Field[] fields = clazz.getFields();
        for (int i = 0; i < fields.length; ++i) {
            fields[i].accept(this);
        }
        final Method[] methods = clazz.getMethods();
        for (int j = 0; j < methods.length; ++j) {
            methods[j].accept(this);
        }
        final Attribute[] attributes = clazz.getAttributes();
        for (int k = 0; k < attributes.length; ++k) {
            attributes[k].accept(this);
        }
        clazz.getConstantPool().accept(this);
        this.stack.pop();
    }
    
    @Override
    public void visitField(final Field field) {
        this.stack.push(field);
        field.accept(this.visitor);
        final Attribute[] attributes = field.getAttributes();
        for (int i = 0; i < attributes.length; ++i) {
            attributes[i].accept(this);
        }
        this.stack.pop();
    }
    
    @Override
    public void visitConstantValue(final ConstantValue cv) {
        this.stack.push(cv);
        cv.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitMethod(final Method method) {
        this.stack.push(method);
        method.accept(this.visitor);
        final Attribute[] attributes = method.getAttributes();
        for (int i = 0; i < attributes.length; ++i) {
            attributes[i].accept(this);
        }
        this.stack.pop();
    }
    
    @Override
    public void visitExceptionTable(final ExceptionTable table) {
        this.stack.push(table);
        table.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitCode(final Code code) {
        this.stack.push(code);
        code.accept(this.visitor);
        final CodeException[] table = code.getExceptionTable();
        for (int i = 0; i < table.length; ++i) {
            table[i].accept(this);
        }
        final Attribute[] attributes = code.getAttributes();
        for (int j = 0; j < attributes.length; ++j) {
            attributes[j].accept(this);
        }
        this.stack.pop();
    }
    
    @Override
    public void visitCodeException(final CodeException ce) {
        this.stack.push(ce);
        ce.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitLineNumberTable(final LineNumberTable table) {
        this.stack.push(table);
        table.accept(this.visitor);
        final LineNumber[] numbers = table.getLineNumberTable();
        for (int i = 0; i < numbers.length; ++i) {
            numbers[i].accept(this);
        }
        this.stack.pop();
    }
    
    @Override
    public void visitLineNumber(final LineNumber number) {
        this.stack.push(number);
        number.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitLocalVariableTable(final LocalVariableTable table) {
        this.stack.push(table);
        table.accept(this.visitor);
        final LocalVariable[] vars = table.getLocalVariableTable();
        for (int i = 0; i < vars.length; ++i) {
            vars[i].accept(this);
        }
        this.stack.pop();
    }
    
    @Override
    public void visitLocalVariableTypeTable(final LocalVariableTypeTable obj) {
        this.stack.push(obj);
        obj.accept(this.visitor);
        final LocalVariable[] vars = obj.getLocalVariableTypeTable();
        for (int i = 0; i < vars.length; ++i) {
            vars[i].accept(this);
        }
        this.stack.pop();
    }
    
    @Override
    public void visitStackMap(final StackMap table) {
        this.stack.push(table);
        table.accept(this.visitor);
        final StackMapEntry[] vars = table.getStackMap();
        for (int i = 0; i < vars.length; ++i) {
            vars[i].accept(this);
        }
        this.stack.pop();
    }
    
    @Override
    public void visitStackMapEntry(final StackMapEntry var) {
        this.stack.push(var);
        var.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitLocalVariable(final LocalVariable var) {
        this.stack.push(var);
        var.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantPool(final ConstantPool cp) {
        this.stack.push(cp);
        cp.accept(this.visitor);
        final Constant[] constants = cp.getConstantPool();
        for (int i = 1; i < constants.length; ++i) {
            if (constants[i] != null) {
                constants[i].accept(this);
            }
        }
        this.stack.pop();
    }
    
    @Override
    public void visitConstantClass(final ConstantClass constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantDouble(final ConstantDouble constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantFieldref(final ConstantFieldref constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantFloat(final ConstantFloat constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantInteger(final ConstantInteger constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantInterfaceMethodref(final ConstantInterfaceMethodref constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantLong(final ConstantLong constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantMethodref(final ConstantMethodref constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantNameAndType(final ConstantNameAndType constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantString(final ConstantString constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitConstantUtf8(final ConstantUtf8 constant) {
        this.stack.push(constant);
        constant.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitInnerClasses(final InnerClasses ic) {
        this.stack.push(ic);
        ic.accept(this.visitor);
        final InnerClass[] ics = ic.getInnerClasses();
        for (int i = 0; i < ics.length; ++i) {
            ics[i].accept(this);
        }
        this.stack.pop();
    }
    
    @Override
    public void visitInnerClass(final InnerClass inner) {
        this.stack.push(inner);
        inner.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitDeprecated(final Deprecated attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitSignature(final Signature attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitSourceFile(final SourceFile attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitSynthetic(final Synthetic attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }
    
    @Override
    public void visitUnknown(final Unknown attribute) {
        this.stack.push(attribute);
        attribute.accept(this.visitor);
        this.stack.pop();
    }
}
