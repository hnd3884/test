package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Enumeration;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import java.util.Iterator;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IFEQ;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Choose extends Instruction
{
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("Choose");
        this.indent(indent + 4);
        this.displayContents(indent + 4);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final Vector whenElements = new Vector();
        Otherwise otherwise = null;
        final Iterator<SyntaxTreeNode> elements = this.elements();
        ErrorMsg error = null;
        final int line = this.getLineNumber();
        while (elements.hasNext()) {
            final SyntaxTreeNode element = elements.next();
            if (element instanceof When) {
                whenElements.addElement(element);
            }
            else if (element instanceof Otherwise) {
                if (otherwise == null) {
                    otherwise = (Otherwise)element;
                }
                else {
                    error = new ErrorMsg("MULTIPLE_OTHERWISE_ERR", this);
                    this.getParser().reportError(3, error);
                }
            }
            else if (element instanceof Text) {
                ((Text)element).ignore();
            }
            else {
                error = new ErrorMsg("WHEN_ELEMENT_ERR", this);
                this.getParser().reportError(3, error);
            }
        }
        if (whenElements.size() == 0) {
            error = new ErrorMsg("MISSING_WHEN_ERR", this);
            this.getParser().reportError(3, error);
            return;
        }
        final InstructionList il = methodGen.getInstructionList();
        BranchHandle nextElement = null;
        final Vector exitHandles = new Vector();
        InstructionHandle exit = null;
        final Enumeration whens = whenElements.elements();
        while (whens.hasMoreElements()) {
            final When when = whens.nextElement();
            final Expression test = when.getTest();
            InstructionHandle truec = il.getEnd();
            if (nextElement != null) {
                nextElement.setTarget(il.append(Choose.NOP));
            }
            test.translateDesynthesized(classGen, methodGen);
            if (test instanceof FunctionCall) {
                final FunctionCall call = (FunctionCall)test;
                try {
                    final Type type = call.typeCheck(this.getParser().getSymbolTable());
                    if (type != Type.Boolean) {
                        test._falseList.add(il.append(new IFEQ(null)));
                    }
                }
                catch (final TypeCheckError typeCheckError) {}
            }
            truec = il.getEnd();
            if (!when.ignore()) {
                when.translateContents(classGen, methodGen);
            }
            exitHandles.addElement(il.append(new GOTO(null)));
            if (whens.hasMoreElements() || otherwise != null) {
                nextElement = il.append(new GOTO(null));
                test.backPatchFalseList(nextElement);
            }
            else {
                test.backPatchFalseList(exit = il.append(Choose.NOP));
            }
            test.backPatchTrueList(truec.getNext());
        }
        if (otherwise != null) {
            nextElement.setTarget(il.append(Choose.NOP));
            otherwise.translateContents(classGen, methodGen);
            exit = il.append(Choose.NOP);
        }
        final Enumeration exitGotos = exitHandles.elements();
        while (exitGotos.hasMoreElements()) {
            final BranchHandle gotoExit = exitGotos.nextElement();
            gotoExit.setTarget(exit);
        }
    }
}
