package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class RtMethodGenerator extends MethodGenerator
{
    private static final int HANDLER_INDEX = 2;
    private final Instruction _astoreHandler;
    private final Instruction _aloadHandler;
    
    public RtMethodGenerator(final int access_flags, final Type return_type, final Type[] arg_types, final String[] arg_names, final String method_name, final String class_name, final InstructionList il, final ConstantPoolGen cp) {
        super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cp);
        this._astoreHandler = new ASTORE(2);
        this._aloadHandler = new ALOAD(2);
    }
    
    public int getIteratorIndex() {
        return -1;
    }
    
    @Override
    public final Instruction storeHandler() {
        return this._astoreHandler;
    }
    
    @Override
    public final Instruction loadHandler() {
        return this._aloadHandler;
    }
    
    @Override
    public int getLocalIndex(final String name) {
        return -1;
    }
}
