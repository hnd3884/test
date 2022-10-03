package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSetType;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;

final class ParameterRef extends VariableRefBase
{
    QName _name;
    
    public ParameterRef(final Param param) {
        super(param);
        this._name = null;
        this._name = param._name;
    }
    
    @Override
    public String toString() {
        return "parameter-ref(" + this._variable.getName() + '/' + this._variable.getType() + ')';
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final String name = BasisLibrary.mapQNameToJavaName(this._name.toString());
        final String signature = this._type.toSignature();
        if (this._variable.isLocal()) {
            if (classGen.isExternal()) {
                Closure variableClosure;
                for (variableClosure = this._closure; variableClosure != null && !variableClosure.inInnerClass(); variableClosure = variableClosure.getParentClosure()) {}
                if (variableClosure != null) {
                    il.append(ParameterRef.ALOAD_0);
                    il.append(new GETFIELD(cpg.addFieldref(variableClosure.getInnerClassName(), name, signature)));
                }
                else {
                    il.append(this._variable.loadInstruction());
                }
            }
            else {
                il.append(this._variable.loadInstruction());
            }
        }
        else {
            final String className = classGen.getClassName();
            il.append(classGen.loadTranslet());
            if (classGen.isExternal()) {
                il.append(new CHECKCAST(cpg.addClass(className)));
            }
            il.append(new GETFIELD(cpg.addFieldref(className, name, signature)));
        }
        if (this._variable.getType() instanceof NodeSetType) {
            final int clone = cpg.addInterfaceMethodref("com.sun.org.apache.xml.internal.dtm.DTMAxisIterator", "cloneIterator", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(new INVOKEINTERFACE(clone, 1));
        }
    }
}
