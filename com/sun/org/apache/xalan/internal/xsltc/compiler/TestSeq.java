package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.GOTO_W;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import java.util.Map;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import java.util.Vector;

final class TestSeq
{
    private int _kernelType;
    private Vector _patterns;
    private Mode _mode;
    private Template _default;
    private InstructionList _instructionList;
    private InstructionHandle _start;
    
    public TestSeq(final Vector patterns, final Mode mode) {
        this(patterns, -2, mode);
    }
    
    public TestSeq(final Vector patterns, final int kernelType, final Mode mode) {
        this._patterns = null;
        this._mode = null;
        this._default = null;
        this._start = null;
        this._patterns = patterns;
        this._kernelType = kernelType;
        this._mode = mode;
    }
    
    @Override
    public String toString() {
        final int count = this._patterns.size();
        final StringBuffer result = new StringBuffer();
        for (int i = 0; i < count; ++i) {
            final LocationPathPattern pattern = this._patterns.elementAt(i);
            if (i == 0) {
                result.append("Testseq for kernel ").append(this._kernelType).append('\n');
            }
            result.append("   pattern ").append(i).append(": ").append(pattern.toString()).append('\n');
        }
        return result.toString();
    }
    
    public InstructionList getInstructionList() {
        return this._instructionList;
    }
    
    public double getPriority() {
        final Template template = (this._patterns.size() == 0) ? this._default : this._patterns.elementAt(0).getTemplate();
        return template.getPriority();
    }
    
    public int getPosition() {
        final Template template = (this._patterns.size() == 0) ? this._default : this._patterns.elementAt(0).getTemplate();
        return template.getPosition();
    }
    
    public void reduce() {
        final Vector newPatterns = new Vector();
        for (int count = this._patterns.size(), i = 0; i < count; ++i) {
            final LocationPathPattern pattern = this._patterns.elementAt(i);
            pattern.reduceKernelPattern();
            if (pattern.isWildcard()) {
                this._default = pattern.getTemplate();
                break;
            }
            newPatterns.addElement(pattern);
        }
        this._patterns = newPatterns;
    }
    
    public void findTemplates(final Map<Template, Object> templates) {
        if (this._default != null) {
            templates.put(this._default, this);
        }
        for (int i = 0; i < this._patterns.size(); ++i) {
            final LocationPathPattern pattern = this._patterns.elementAt(i);
            templates.put(pattern.getTemplate(), this);
        }
    }
    
    private InstructionHandle getTemplateHandle(final Template template) {
        return this._mode.getTemplateInstructionHandle(template);
    }
    
    private LocationPathPattern getPattern(final int n) {
        return this._patterns.elementAt(n);
    }
    
    public InstructionHandle compile(final ClassGenerator classGen, final MethodGenerator methodGen, final InstructionHandle continuation) {
        if (this._start != null) {
            return this._start;
        }
        final int count = this._patterns.size();
        if (count == 0) {
            return this._start = this.getTemplateHandle(this._default);
        }
        InstructionHandle fail = (this._default == null) ? continuation : this.getTemplateHandle(this._default);
        for (int n = count - 1; n >= 0; --n) {
            final LocationPathPattern pattern = this.getPattern(n);
            final Template template = pattern.getTemplate();
            final InstructionList il = new InstructionList();
            il.append(methodGen.loadCurrentNode());
            InstructionList ilist = methodGen.getInstructionList(pattern);
            if (ilist == null) {
                ilist = pattern.compile(classGen, methodGen);
                methodGen.addInstructionList(pattern, ilist);
            }
            final InstructionList copyOfilist = ilist.copy();
            FlowList trueList = pattern.getTrueList();
            if (trueList != null) {
                trueList = trueList.copyAndRedirect(ilist, copyOfilist);
            }
            FlowList falseList = pattern.getFalseList();
            if (falseList != null) {
                falseList = falseList.copyAndRedirect(ilist, copyOfilist);
            }
            il.append(copyOfilist);
            final InstructionHandle gtmpl = this.getTemplateHandle(template);
            final InstructionHandle success = il.append(new GOTO_W(gtmpl));
            if (trueList != null) {
                trueList.backPatch(success);
            }
            if (falseList != null) {
                falseList.backPatch(fail);
            }
            fail = il.getStart();
            if (this._instructionList != null) {
                il.append(this._instructionList);
            }
            this._instructionList = il;
        }
        return this._start = fail;
    }
}
