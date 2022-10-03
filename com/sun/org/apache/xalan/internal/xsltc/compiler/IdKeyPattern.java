package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IFNE;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

abstract class IdKeyPattern extends LocationPathPattern
{
    protected RelativePathPattern _left;
    private String _index;
    private String _value;
    
    public IdKeyPattern(final String index, final String value) {
        this._left = null;
        this._index = null;
        this._value = null;
        this._index = index;
        this._value = value;
    }
    
    public String getIndexName() {
        return this._index;
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return Type.NodeSet;
    }
    
    @Override
    public boolean isWildcard() {
        return false;
    }
    
    public void setLeft(final RelativePathPattern left) {
        this._left = left;
    }
    
    @Override
    public StepPattern getKernelPattern() {
        return null;
    }
    
    @Override
    public void reduceKernelPattern() {
    }
    
    @Override
    public String toString() {
        return "id/keyPattern(" + this._index + ", " + this._value + ')';
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int getKeyIndex = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "getKeyIndex", "(Ljava/lang/String;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex;");
        final int lookupId = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "containsID", "(ILjava/lang/Object;)I");
        final int lookupKey = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/KeyIndex", "containsKey", "(ILjava/lang/Object;)I");
        final int getNodeIdent = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getNodeIdent", "(I)I");
        il.append(classGen.loadTranslet());
        il.append(new PUSH(cpg, this._index));
        il.append(new INVOKEVIRTUAL(getKeyIndex));
        il.append(IdKeyPattern.SWAP);
        il.append(new PUSH(cpg, this._value));
        if (this instanceof IdPattern) {
            il.append(new INVOKEVIRTUAL(lookupId));
        }
        else {
            il.append(new INVOKEVIRTUAL(lookupKey));
        }
        this._trueList.add(il.append(new IFNE(null)));
        this._falseList.add(il.append(new GOTO(null)));
    }
}
