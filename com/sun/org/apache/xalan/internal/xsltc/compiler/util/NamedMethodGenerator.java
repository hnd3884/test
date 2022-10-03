package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;

public final class NamedMethodGenerator extends MethodGenerator
{
    protected static final int CURRENT_INDEX = 4;
    private static final int PARAM_START_INDEX = 5;
    
    public NamedMethodGenerator(final int access_flags, final Type return_type, final Type[] arg_types, final String[] arg_names, final String method_name, final String class_name, final InstructionList il, final ConstantPoolGen cp) {
        super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cp);
    }
    
    @Override
    public int getLocalIndex(final String name) {
        if (name.equals("current")) {
            return 4;
        }
        return super.getLocalIndex(name);
    }
    
    public Instruction loadParameter(final int index) {
        return new ALOAD(index + 5);
    }
    
    public Instruction storeParameter(final int index) {
        return new ASTORE(index + 5);
    }
}
