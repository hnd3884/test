package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class ParentLocationPath extends RelativeLocationPath
{
    private Expression _step;
    private final RelativeLocationPath _path;
    private Type stype;
    private boolean _orderNodes;
    private boolean _axisMismatch;
    
    public ParentLocationPath(final RelativeLocationPath path, final Expression step) {
        this._orderNodes = false;
        this._axisMismatch = false;
        this._path = path;
        this._step = step;
        this._path.setParent(this);
        this._step.setParent(this);
        if (this._step instanceof Step) {
            this._axisMismatch = this.checkAxisMismatch();
        }
    }
    
    @Override
    public void setAxis(final int axis) {
        this._path.setAxis(axis);
    }
    
    @Override
    public int getAxis() {
        return this._path.getAxis();
    }
    
    public RelativeLocationPath getPath() {
        return this._path;
    }
    
    public Expression getStep() {
        return this._step;
    }
    
    public void setParser(final Parser parser) {
        super.setParser(parser);
        this._step.setParser(parser);
        this._path.setParser(parser);
    }
    
    @Override
    public String toString() {
        return "ParentLocationPath(" + this._path + ", " + this._step + ')';
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this.stype = this._step.typeCheck(stable);
        this._path.typeCheck(stable);
        if (this._axisMismatch) {
            this.enableNodeOrdering();
        }
        return this._type = Type.NodeSet;
    }
    
    public void enableNodeOrdering() {
        final SyntaxTreeNode parent = this.getParent();
        if (parent instanceof ParentLocationPath) {
            ((ParentLocationPath)parent).enableNodeOrdering();
        }
        else {
            this._orderNodes = true;
        }
    }
    
    public boolean checkAxisMismatch() {
        final int left = this._path.getAxis();
        final int right = ((Step)this._step).getAxis();
        if ((left == 0 || left == 1) && (right == 3 || right == 4 || right == 5 || right == 10 || right == 11 || right == 12)) {
            return true;
        }
        if ((left == 3 && right == 0) || right == 1 || right == 10 || right == 11) {
            return true;
        }
        if (left == 4 || left == 5) {
            return true;
        }
        if ((left == 6 || left == 7) && (right == 6 || right == 10 || right == 11 || right == 12)) {
            return true;
        }
        if ((left == 11 || left == 12) && (right == 4 || right == 5 || right == 6 || right == 7 || right == 10 || right == 11 || right == 12)) {
            return true;
        }
        if (right == 6 && left == 3 && this._path instanceof Step) {
            final int type = ((Step)this._path).getNodeType();
            if (type == 2) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this._path.translate(classGen, methodGen);
        this.translateStep(classGen, methodGen);
    }
    
    public void translateStep(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final LocalVariableGen pathTemp = methodGen.addLocalVariable("parent_location_path_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        pathTemp.setStart(il.append(new ASTORE(pathTemp.getIndex())));
        this._step.translate(classGen, methodGen);
        final LocalVariableGen stepTemp = methodGen.addLocalVariable("parent_location_path_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        stepTemp.setStart(il.append(new ASTORE(stepTemp.getIndex())));
        final int initSI = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)V");
        il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.StepIterator")));
        il.append(ParentLocationPath.DUP);
        pathTemp.setEnd(il.append(new ALOAD(pathTemp.getIndex())));
        stepTemp.setEnd(il.append(new ALOAD(stepTemp.getIndex())));
        il.append(new INVOKESPECIAL(initSI));
        Expression stp = this._step;
        if (stp instanceof ParentLocationPath) {
            stp = ((ParentLocationPath)stp).getStep();
        }
        if (this._path instanceof Step && stp instanceof Step) {
            final int path = ((Step)this._path).getAxis();
            final int step = ((Step)stp).getAxis();
            if ((path == 5 && step == 3) || (path == 11 && step == 10)) {
                final int incl = cpg.addMethodref("com.sun.org.apache.xml.internal.dtm.ref.DTMAxisIteratorBase", "includeSelf", "()Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
                il.append(new INVOKEVIRTUAL(incl));
            }
        }
        if (this._orderNodes) {
            final int order = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "orderNodes", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append(ParentLocationPath.SWAP);
            il.append(methodGen.loadContextNode());
            il.append(new INVOKEINTERFACE(order, 3));
        }
    }
}
