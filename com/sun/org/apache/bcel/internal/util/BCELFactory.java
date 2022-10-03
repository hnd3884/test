package com.sun.org.apache.bcel.internal.util;

import com.sun.org.apache.bcel.internal.generic.CodeExceptionGen;
import java.util.Iterator;
import com.sun.org.apache.bcel.internal.generic.RET;
import com.sun.org.apache.bcel.internal.generic.Select;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.INSTANCEOF;
import com.sun.org.apache.bcel.internal.generic.LDC2_W;
import com.sun.org.apache.bcel.internal.generic.LDC;
import com.sun.org.apache.bcel.internal.classfile.Utility;
import com.sun.org.apache.bcel.internal.generic.MULTIANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ObjectType;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.CPInstruction;
import com.sun.org.apache.bcel.internal.generic.AllocationInstruction;
import com.sun.org.apache.bcel.internal.generic.InvokeInstruction;
import com.sun.org.apache.bcel.internal.Constants;
import com.sun.org.apache.bcel.internal.generic.FieldInstruction;
import com.sun.org.apache.bcel.internal.generic.ArrayInstruction;
import com.sun.org.apache.bcel.internal.generic.Type;
import com.sun.org.apache.bcel.internal.generic.IINC;
import com.sun.org.apache.bcel.internal.generic.LocalVariableInstruction;
import com.sun.org.apache.bcel.internal.generic.ReturnInstruction;
import com.sun.org.apache.bcel.internal.generic.ConstantPushInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionConstants;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.Visitor;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import java.util.ArrayList;
import java.util.HashMap;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import java.io.PrintWriter;
import com.sun.org.apache.bcel.internal.generic.MethodGen;
import com.sun.org.apache.bcel.internal.generic.EmptyVisitor;

class BCELFactory extends EmptyVisitor
{
    private MethodGen _mg;
    private PrintWriter _out;
    private ConstantPoolGen _cp;
    private HashMap branch_map;
    private ArrayList branches;
    
    BCELFactory(final MethodGen mg, final PrintWriter out) {
        this.branch_map = new HashMap();
        this.branches = new ArrayList();
        this._mg = mg;
        this._cp = mg.getConstantPool();
        this._out = out;
    }
    
    public void start() {
        if (!this._mg.isAbstract() && !this._mg.isNative()) {
            for (InstructionHandle ih = this._mg.getInstructionList().getStart(); ih != null; ih = ih.getNext()) {
                final Instruction i = ih.getInstruction();
                if (i instanceof BranchInstruction) {
                    this.branch_map.put(i, ih);
                }
                if (ih.hasTargeters()) {
                    if (i instanceof BranchInstruction) {
                        this._out.println("    InstructionHandle ih_" + ih.getPosition() + ";");
                    }
                    else {
                        this._out.print("    InstructionHandle ih_" + ih.getPosition() + " = ");
                    }
                }
                else {
                    this._out.print("    ");
                }
                if (!this.visitInstruction(i)) {
                    i.accept(this);
                }
            }
            this.updateBranchTargets();
            this.updateExceptionHandlers();
        }
    }
    
    private boolean visitInstruction(final Instruction i) {
        final short opcode = i.getOpcode();
        if (InstructionConstants.INSTRUCTIONS[opcode] != null && !(i instanceof ConstantPushInstruction) && !(i instanceof ReturnInstruction)) {
            this._out.println("il.append(InstructionConstants." + i.getName().toUpperCase() + ");");
            return true;
        }
        return false;
    }
    
    @Override
    public void visitLocalVariableInstruction(final LocalVariableInstruction i) {
        final short opcode = i.getOpcode();
        final Type type = i.getType(this._cp);
        if (opcode == 132) {
            this._out.println("il.append(new IINC(" + i.getIndex() + ", " + ((IINC)i).getIncrement() + "));");
        }
        else {
            final String kind = (opcode < 54) ? "Load" : "Store";
            this._out.println("il.append(_factory.create" + kind + "(" + BCELifier.printType(type) + ", " + i.getIndex() + "));");
        }
    }
    
    @Override
    public void visitArrayInstruction(final ArrayInstruction i) {
        final short opcode = i.getOpcode();
        final Type type = i.getType(this._cp);
        final String kind = (opcode < 79) ? "Load" : "Store";
        this._out.println("il.append(_factory.createArray" + kind + "(" + BCELifier.printType(type) + "));");
    }
    
    @Override
    public void visitFieldInstruction(final FieldInstruction i) {
        final short opcode = i.getOpcode();
        final String class_name = i.getClassName(this._cp);
        final String field_name = i.getFieldName(this._cp);
        final Type type = i.getFieldType(this._cp);
        this._out.println("il.append(_factory.createFieldAccess(\"" + class_name + "\", \"" + field_name + "\", " + BCELifier.printType(type) + ", Constants." + Constants.OPCODE_NAMES[opcode].toUpperCase() + "));");
    }
    
    @Override
    public void visitInvokeInstruction(final InvokeInstruction i) {
        final short opcode = i.getOpcode();
        final String class_name = i.getClassName(this._cp);
        final String method_name = i.getMethodName(this._cp);
        final Type type = i.getReturnType(this._cp);
        final Type[] arg_types = i.getArgumentTypes(this._cp);
        this._out.println("il.append(_factory.createInvoke(\"" + class_name + "\", \"" + method_name + "\", " + BCELifier.printType(type) + ", " + BCELifier.printArgumentTypes(arg_types) + ", Constants." + Constants.OPCODE_NAMES[opcode].toUpperCase() + "));");
    }
    
    @Override
    public void visitAllocationInstruction(final AllocationInstruction i) {
        Type type;
        if (i instanceof CPInstruction) {
            type = ((CPInstruction)i).getType(this._cp);
        }
        else {
            type = ((NEWARRAY)i).getType();
        }
        final short opcode = ((Instruction)i).getOpcode();
        int dim = 1;
        switch (opcode) {
            case 187: {
                this._out.println("il.append(_factory.createNew(\"" + ((ObjectType)type).getClassName() + "\"));");
                break;
            }
            case 197: {
                dim = ((MULTIANEWARRAY)i).getDimensions();
            }
            case 188:
            case 189: {
                this._out.println("il.append(_factory.createNewArray(" + BCELifier.printType(type) + ", (short) " + dim + "));");
                break;
            }
            default: {
                throw new RuntimeException("Oops: " + opcode);
            }
        }
    }
    
    private void createConstant(final Object value) {
        String embed = value.toString();
        if (value instanceof String) {
            embed = '\"' + Utility.convertString(value.toString()) + '\"';
        }
        else if (value instanceof Character) {
            embed = "(char)0x" + Integer.toHexString((char)value);
        }
        this._out.println("il.append(new PUSH(_cp, " + embed + "));");
    }
    
    @Override
    public void visitLDC(final LDC i) {
        this.createConstant(i.getValue(this._cp));
    }
    
    @Override
    public void visitLDC2_W(final LDC2_W i) {
        this.createConstant(i.getValue(this._cp));
    }
    
    @Override
    public void visitConstantPushInstruction(final ConstantPushInstruction i) {
        this.createConstant(i.getValue());
    }
    
    @Override
    public void visitINSTANCEOF(final INSTANCEOF i) {
        final Type type = i.getType(this._cp);
        this._out.println("il.append(new INSTANCEOF(_cp.addClass(" + BCELifier.printType(type) + ")));");
    }
    
    @Override
    public void visitCHECKCAST(final CHECKCAST i) {
        final Type type = i.getType(this._cp);
        this._out.println("il.append(_factory.createCheckCast(" + BCELifier.printType(type) + "));");
    }
    
    @Override
    public void visitReturnInstruction(final ReturnInstruction i) {
        final Type type = i.getType(this._cp);
        this._out.println("il.append(_factory.createReturn(" + BCELifier.printType(type) + "));");
    }
    
    @Override
    public void visitBranchInstruction(final BranchInstruction bi) {
        final BranchHandle bh = this.branch_map.get(bi);
        final int pos = bh.getPosition();
        final String name = bi.getName() + "_" + pos;
        if (bi instanceof Select) {
            final Select s = (Select)bi;
            this.branches.add(bi);
            final StringBuffer args = new StringBuffer("new int[] { ");
            final int[] matchs = s.getMatchs();
            for (int i = 0; i < matchs.length; ++i) {
                args.append(matchs[i]);
                if (i < matchs.length - 1) {
                    args.append(", ");
                }
            }
            args.append(" }");
            this._out.print("    Select " + name + " = new " + bi.getName().toUpperCase() + "(" + (Object)args + ", new InstructionHandle[] { ");
            for (int i = 0; i < matchs.length; ++i) {
                this._out.print("null");
                if (i < matchs.length - 1) {
                    this._out.print(", ");
                }
            }
            this._out.println(");");
        }
        else {
            final int t_pos = bh.getTarget().getPosition();
            String target;
            if (pos > t_pos) {
                target = "ih_" + t_pos;
            }
            else {
                this.branches.add(bi);
                target = "null";
            }
            this._out.println("    BranchInstruction " + name + " = _factory.createBranchInstruction(Constants." + bi.getName().toUpperCase() + ", " + target + ");");
        }
        if (bh.hasTargeters()) {
            this._out.println("    ih_" + pos + " = il.append(" + name + ");");
        }
        else {
            this._out.println("    il.append(" + name + ");");
        }
    }
    
    @Override
    public void visitRET(final RET i) {
        this._out.println("il.append(new RET(" + i.getIndex() + ")));");
    }
    
    private void updateBranchTargets() {
        for (final BranchInstruction bi : this.branches) {
            final BranchHandle bh = this.branch_map.get(bi);
            final int pos = bh.getPosition();
            final String name = bi.getName() + "_" + pos;
            int t_pos = bh.getTarget().getPosition();
            this._out.println("    " + name + ".setTarget(ih_" + t_pos + ");");
            if (bi instanceof Select) {
                final InstructionHandle[] ihs = ((Select)bi).getTargets();
                for (int j = 0; j < ihs.length; ++j) {
                    t_pos = ihs[j].getPosition();
                    this._out.println("    " + name + ".setTarget(" + j + ", ih_" + t_pos + ");");
                }
            }
        }
    }
    
    private void updateExceptionHandlers() {
        final CodeExceptionGen[] handlers = this._mg.getExceptionHandlers();
        for (int i = 0; i < handlers.length; ++i) {
            final CodeExceptionGen h = handlers[i];
            final String type = (h.getCatchType() == null) ? "null" : BCELifier.printType(h.getCatchType());
            this._out.println("    method.addExceptionHandler(ih_" + h.getStartPC().getPosition() + ", ih_" + h.getEndPC().getPosition() + ", ih_" + h.getHandlerPC().getPosition() + ", " + type + ");");
        }
    }
}
