package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class TestGenerator extends MethodGenerator
{
    private static int CONTEXT_NODE_INDEX;
    private static int CURRENT_NODE_INDEX;
    private static int ITERATOR_INDEX;
    private Instruction _aloadDom;
    private final Instruction _iloadCurrent;
    private final Instruction _iloadContext;
    private final Instruction _istoreCurrent;
    private final Instruction _istoreContext;
    private final Instruction _astoreIterator;
    private final Instruction _aloadIterator;
    
    public TestGenerator(final int access_flags, final Type return_type, final Type[] arg_types, final String[] arg_names, final String method_name, final String class_name, final InstructionList il, final ConstantPoolGen cp) {
        super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cp);
        this._iloadCurrent = new ILOAD(TestGenerator.CURRENT_NODE_INDEX);
        this._istoreCurrent = new ISTORE(TestGenerator.CURRENT_NODE_INDEX);
        this._iloadContext = new ILOAD(TestGenerator.CONTEXT_NODE_INDEX);
        this._istoreContext = new ILOAD(TestGenerator.CONTEXT_NODE_INDEX);
        this._astoreIterator = new ASTORE(TestGenerator.ITERATOR_INDEX);
        this._aloadIterator = new ALOAD(TestGenerator.ITERATOR_INDEX);
    }
    
    public int getHandlerIndex() {
        return -1;
    }
    
    public int getIteratorIndex() {
        return TestGenerator.ITERATOR_INDEX;
    }
    
    public void setDomIndex(final int domIndex) {
        this._aloadDom = new ALOAD(domIndex);
    }
    
    @Override
    public Instruction loadDOM() {
        return this._aloadDom;
    }
    
    @Override
    public Instruction loadCurrentNode() {
        return this._iloadCurrent;
    }
    
    @Override
    public Instruction loadContextNode() {
        return this._iloadContext;
    }
    
    @Override
    public Instruction storeContextNode() {
        return this._istoreContext;
    }
    
    @Override
    public Instruction storeCurrentNode() {
        return this._istoreCurrent;
    }
    
    @Override
    public Instruction storeIterator() {
        return this._astoreIterator;
    }
    
    @Override
    public Instruction loadIterator() {
        return this._aloadIterator;
    }
    
    @Override
    public int getLocalIndex(final String name) {
        if (name.equals("current")) {
            return TestGenerator.CURRENT_NODE_INDEX;
        }
        return super.getLocalIndex(name);
    }
    
    static {
        TestGenerator.CONTEXT_NODE_INDEX = 1;
        TestGenerator.CURRENT_NODE_INDEX = 4;
        TestGenerator.ITERATOR_INDEX = 6;
    }
}
