package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class ApplyImports extends Instruction
{
    private QName _modeName;
    private int _precedence;
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("ApplyTemplates");
        this.indent(indent + 4);
        if (this._modeName != null) {
            this.indent(indent + 4);
            Util.println("mode " + this._modeName);
        }
    }
    
    public boolean hasWithParams() {
        return this.hasContents();
    }
    
    private int getMinPrecedence(final int max) {
        Stylesheet includeRoot;
        for (includeRoot = this.getStylesheet(); includeRoot._includedFrom != null; includeRoot = includeRoot._includedFrom) {}
        return includeRoot.getMinimumDescendantPrecedence();
    }
    
    @Override
    public void parseContents(final Parser parser) {
        Stylesheet stylesheet = this.getStylesheet();
        stylesheet.setTemplateInlining(false);
        final Template template = this.getTemplate();
        this._modeName = template.getModeName();
        this._precedence = template.getImportPrecedence();
        stylesheet = parser.getTopLevelStylesheet();
        this.parseChildren(parser);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        this.typeCheckContents(stable);
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final Stylesheet stylesheet = classGen.getStylesheet();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int current = methodGen.getLocalIndex("current");
        il.append(classGen.loadTranslet());
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadIterator());
        il.append(methodGen.loadHandler());
        il.append(methodGen.loadCurrentNode());
        if (stylesheet.hasLocalParams()) {
            il.append(classGen.loadTranslet());
            final int pushFrame = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "pushParamFrame", "()V");
            il.append(new INVOKEVIRTUAL(pushFrame));
        }
        final int maxPrecedence = this._precedence;
        final int minPrecedence = this.getMinPrecedence(maxPrecedence);
        final Mode mode = stylesheet.getMode(this._modeName);
        final String functionName = mode.functionName(minPrecedence, maxPrecedence);
        final String className = classGen.getStylesheet().getClassName();
        final String signature = classGen.getApplyTemplatesSigForImport();
        final int applyTemplates = cpg.addMethodref(className, functionName, signature);
        il.append(new INVOKEVIRTUAL(applyTemplates));
        if (stylesheet.hasLocalParams()) {
            il.append(classGen.loadTranslet());
            final int pushFrame2 = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "popParamFrame", "()V");
            il.append(new INVOKEVIRTUAL(pushFrame2));
        }
    }
}
