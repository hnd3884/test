package com.sun.org.apache.bcel.internal.generic;

import com.sun.org.apache.bcel.internal.classfile.Constant;
import com.sun.org.apache.bcel.internal.Constants;
import java.util.StringTokenizer;
import com.sun.org.apache.bcel.internal.classfile.ConstantPool;

public abstract class InvokeInstruction extends FieldOrMethod implements ExceptionThrower, TypedInstruction, StackConsumer, StackProducer
{
    InvokeInstruction() {
    }
    
    protected InvokeInstruction(final short opcode, final int index) {
        super(opcode, index);
    }
    
    @Override
    public String toString(final ConstantPool cp) {
        final Constant c = cp.getConstant(this.index);
        final StringTokenizer tok = new StringTokenizer(cp.constantToString(c));
        return Constants.OPCODE_NAMES[this.opcode] + " " + tok.nextToken().replace('.', '/') + tok.nextToken();
    }
    
    @Override
    public int consumeStack(final ConstantPoolGen cpg) {
        final String signature = this.getSignature(cpg);
        final Type[] args = Type.getArgumentTypes(signature);
        int sum;
        if (this.opcode == 184) {
            sum = 0;
        }
        else {
            sum = 1;
        }
        for (int n = args.length, i = 0; i < n; ++i) {
            sum += args[i].getSize();
        }
        return sum;
    }
    
    @Override
    public int produceStack(final ConstantPoolGen cpg) {
        return this.getReturnType(cpg).getSize();
    }
    
    @Override
    public Type getType(final ConstantPoolGen cpg) {
        return this.getReturnType(cpg);
    }
    
    public String getMethodName(final ConstantPoolGen cpg) {
        return this.getName(cpg);
    }
    
    public Type getReturnType(final ConstantPoolGen cpg) {
        return Type.getReturnType(this.getSignature(cpg));
    }
    
    public Type[] getArgumentTypes(final ConstantPoolGen cpg) {
        return Type.getArgumentTypes(this.getSignature(cpg));
    }
}
