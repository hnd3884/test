package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class MatchGenerator extends MethodGenerator
{
    private static int CURRENT_INDEX;
    private int _iteratorIndex;
    private final Instruction _iloadCurrent;
    private final Instruction _istoreCurrent;
    private Instruction _aloadDom;
    
    public MatchGenerator(final int access_flags, final Type return_type, final Type[] arg_types, final String[] arg_names, final String method_name, final String class_name, final InstructionList il, final ConstantPoolGen cp) {
        super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cp);
        this._iteratorIndex = -1;
        this._iloadCurrent = new ILOAD(MatchGenerator.CURRENT_INDEX);
        this._istoreCurrent = new ISTORE(MatchGenerator.CURRENT_INDEX);
    }
    
    @Override
    public Instruction loadCurrentNode() {
        return this._iloadCurrent;
    }
    
    @Override
    public Instruction storeCurrentNode() {
        return this._istoreCurrent;
    }
    
    public int getHandlerIndex() {
        return -1;
    }
    
    @Override
    public Instruction loadDOM() {
        return this._aloadDom;
    }
    
    public void setDomIndex(final int domIndex) {
        this._aloadDom = new ALOAD(domIndex);
    }
    
    public int getIteratorIndex() {
        return this._iteratorIndex;
    }
    
    public void setIteratorIndex(final int iteratorIndex) {
        this._iteratorIndex = iteratorIndex;
    }
    
    @Override
    public int getLocalIndex(final String name) {
        if (name.equals("current")) {
            return MatchGenerator.CURRENT_INDEX;
        }
        return super.getLocalIndex(name);
    }
    
    static {
        MatchGenerator.CURRENT_INDEX = 1;
    }
}
