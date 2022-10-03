package com.sun.org.apache.xalan.internal.xsltc.compiler.util;

import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.ACONST_NULL;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ISTORE;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.generic.Instruction;

public final class CompareGenerator extends MethodGenerator
{
    private static int DOM_INDEX;
    private static int CURRENT_INDEX;
    private static int LEVEL_INDEX;
    private static int TRANSLET_INDEX;
    private static int LAST_INDEX;
    private int ITERATOR_INDEX;
    private final Instruction _iloadCurrent;
    private final Instruction _istoreCurrent;
    private final Instruction _aloadDom;
    private final Instruction _iloadLast;
    private final Instruction _aloadIterator;
    private final Instruction _astoreIterator;
    
    public CompareGenerator(final int access_flags, final Type return_type, final Type[] arg_types, final String[] arg_names, final String method_name, final String class_name, final InstructionList il, final ConstantPoolGen cp) {
        super(access_flags, return_type, arg_types, arg_names, method_name, class_name, il, cp);
        this.ITERATOR_INDEX = 6;
        this._iloadCurrent = new ILOAD(CompareGenerator.CURRENT_INDEX);
        this._istoreCurrent = new ISTORE(CompareGenerator.CURRENT_INDEX);
        this._aloadDom = new ALOAD(CompareGenerator.DOM_INDEX);
        this._iloadLast = new ILOAD(CompareGenerator.LAST_INDEX);
        final LocalVariableGen iterator = this.addLocalVariable("iterator", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        this.ITERATOR_INDEX = iterator.getIndex();
        this._aloadIterator = new ALOAD(this.ITERATOR_INDEX);
        this._astoreIterator = new ASTORE(this.ITERATOR_INDEX);
        il.append(new ACONST_NULL());
        il.append(this.storeIterator());
    }
    
    public Instruction loadLastNode() {
        return this._iloadLast;
    }
    
    @Override
    public Instruction loadCurrentNode() {
        return this._iloadCurrent;
    }
    
    @Override
    public Instruction storeCurrentNode() {
        return this._istoreCurrent;
    }
    
    @Override
    public Instruction loadDOM() {
        return this._aloadDom;
    }
    
    public int getHandlerIndex() {
        return -1;
    }
    
    public int getIteratorIndex() {
        return -1;
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
            return CompareGenerator.CURRENT_INDEX;
        }
        return super.getLocalIndex(name);
    }
    
    static {
        CompareGenerator.DOM_INDEX = 1;
        CompareGenerator.CURRENT_INDEX = 2;
        CompareGenerator.LEVEL_INDEX = 3;
        CompareGenerator.TRANSLET_INDEX = 4;
        CompareGenerator.LAST_INDEX = 5;
    }
}
