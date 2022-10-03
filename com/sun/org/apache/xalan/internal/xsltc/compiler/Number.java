package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MatchGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeCounterGenerator;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.bcel.internal.generic.BranchHandle;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.GOTO;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.IFNONNULL;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.RealType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.ArrayList;

final class Number extends Instruction implements Closure
{
    private static final int LEVEL_SINGLE = 0;
    private static final int LEVEL_MULTIPLE = 1;
    private static final int LEVEL_ANY = 2;
    private static final String[] ClassNames;
    private static final String[] FieldNames;
    private Pattern _from;
    private Pattern _count;
    private Expression _value;
    private AttributeValueTemplate _lang;
    private AttributeValueTemplate _format;
    private AttributeValueTemplate _letterValue;
    private AttributeValueTemplate _groupingSeparator;
    private AttributeValueTemplate _groupingSize;
    private int _level;
    private boolean _formatNeeded;
    private String _className;
    private ArrayList _closureVars;
    
    Number() {
        this._from = null;
        this._count = null;
        this._value = null;
        this._lang = null;
        this._format = null;
        this._letterValue = null;
        this._groupingSeparator = null;
        this._groupingSize = null;
        this._level = 0;
        this._formatNeeded = false;
        this._className = null;
        this._closureVars = null;
    }
    
    @Override
    public boolean inInnerClass() {
        return this._className != null;
    }
    
    @Override
    public Closure getParentClosure() {
        return null;
    }
    
    @Override
    public String getInnerClassName() {
        return this._className;
    }
    
    @Override
    public void addVariable(final VariableRefBase variableRef) {
        if (this._closureVars == null) {
            this._closureVars = new ArrayList();
        }
        if (!this._closureVars.contains(variableRef)) {
            this._closureVars.add(variableRef);
        }
    }
    
    @Override
    public void parseContents(final Parser parser) {
        for (int count = this._attributes.getLength(), i = 0; i < count; ++i) {
            final String name = this._attributes.getQName(i);
            final String value = this._attributes.getValue(i);
            if (name.equals("value")) {
                this._value = parser.parseExpression(this, name, null);
            }
            else if (name.equals("count")) {
                this._count = parser.parsePattern(this, name, null);
            }
            else if (name.equals("from")) {
                this._from = parser.parsePattern(this, name, null);
            }
            else if (name.equals("level")) {
                if (value.equals("single")) {
                    this._level = 0;
                }
                else if (value.equals("multiple")) {
                    this._level = 1;
                }
                else if (value.equals("any")) {
                    this._level = 2;
                }
            }
            else if (name.equals("format")) {
                this._format = new AttributeValueTemplate(value, parser, this);
                this._formatNeeded = true;
            }
            else if (name.equals("lang")) {
                this._lang = new AttributeValueTemplate(value, parser, this);
                this._formatNeeded = true;
            }
            else if (name.equals("letter-value")) {
                this._letterValue = new AttributeValueTemplate(value, parser, this);
                this._formatNeeded = true;
            }
            else if (name.equals("grouping-separator")) {
                this._groupingSeparator = new AttributeValueTemplate(value, parser, this);
                this._formatNeeded = true;
            }
            else if (name.equals("grouping-size")) {
                this._groupingSize = new AttributeValueTemplate(value, parser, this);
                this._formatNeeded = true;
            }
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._value != null) {
            final Type tvalue = this._value.typeCheck(stable);
            if (!(tvalue instanceof RealType)) {
                this._value = new CastExpr(this._value, Type.Real);
            }
        }
        if (this._count != null) {
            this._count.typeCheck(stable);
        }
        if (this._from != null) {
            this._from.typeCheck(stable);
        }
        if (this._format != null) {
            this._format.typeCheck(stable);
        }
        if (this._lang != null) {
            this._lang.typeCheck(stable);
        }
        if (this._letterValue != null) {
            this._letterValue.typeCheck(stable);
        }
        if (this._groupingSeparator != null) {
            this._groupingSeparator.typeCheck(stable);
        }
        if (this._groupingSize != null) {
            this._groupingSize.typeCheck(stable);
        }
        return Type.Void;
    }
    
    public boolean hasValue() {
        return this._value != null;
    }
    
    public boolean isDefault() {
        return this._from == null && this._count == null;
    }
    
    private void compileDefault(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int[] fieldIndexes = this.getXSLTC().getNumberFieldIndexes();
        if (fieldIndexes[this._level] == -1) {
            final Field defaultNode = new Field(2, cpg.addUtf8(Number.FieldNames[this._level]), cpg.addUtf8("Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;"), null, cpg.getConstantPool());
            classGen.addField(defaultNode);
            fieldIndexes[this._level] = cpg.addFieldref(classGen.getClassName(), Number.FieldNames[this._level], "Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
        }
        il.append(classGen.loadTranslet());
        il.append(new GETFIELD(fieldIndexes[this._level]));
        final BranchHandle ifBlock1 = il.append(new IFNONNULL(null));
        final int index = cpg.addMethodref(Number.ClassNames[this._level], "getDefaultNodeCounter", "(Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
        il.append(classGen.loadTranslet());
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadIterator());
        il.append(new INVOKESTATIC(index));
        il.append(Number.DUP);
        il.append(classGen.loadTranslet());
        il.append(Number.SWAP);
        il.append(new PUTFIELD(fieldIndexes[this._level]));
        final BranchHandle ifBlock2 = il.append(new GOTO(null));
        ifBlock1.setTarget(il.append(classGen.loadTranslet()));
        il.append(new GETFIELD(fieldIndexes[this._level]));
        ifBlock2.setTarget(il.append(Number.NOP));
    }
    
    private void compileConstructor(final ClassGenerator classGen) {
        final InstructionList il = new InstructionList();
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final MethodGenerator cons = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, new com.sun.org.apache.bcel.internal.generic.Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/Translet;"), Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN }, new String[] { "dom", "translet", "iterator", "hasFrom" }, "<init>", this._className, il, cpg);
        il.append(Number.ALOAD_0);
        il.append(Number.ALOAD_1);
        il.append(Number.ALOAD_2);
        il.append(new ALOAD(3));
        il.append(new ILOAD(4));
        final int index = cpg.addMethodref(Number.ClassNames[this._level], "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Z)V");
        il.append(new INVOKESPECIAL(index));
        il.append(Number.RETURN);
        classGen.addMethod(cons);
    }
    
    private void compileLocals(final NodeCounterGenerator nodeCounterGen, final MatchGenerator matchGen, final InstructionList il) {
        final ConstantPoolGen cpg = nodeCounterGen.getConstantPool();
        LocalVariableGen local = matchGen.addLocalVariable("iterator", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        int field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "_iterator", "Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
        il.append(Number.ALOAD_0);
        il.append(new GETFIELD(field));
        local.setStart(il.append(new ASTORE(local.getIndex())));
        matchGen.setIteratorIndex(local.getIndex());
        local = matchGen.addLocalVariable("translet", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), null, null);
        field = cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "_translet", "Lcom/sun/org/apache/xalan/internal/xsltc/Translet;");
        il.append(Number.ALOAD_0);
        il.append(new GETFIELD(field));
        il.append(new CHECKCAST(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet")));
        local.setStart(il.append(new ASTORE(local.getIndex())));
        nodeCounterGen.setTransletIndex(local.getIndex());
        local = matchGen.addLocalVariable("document", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), null, null);
        field = cpg.addFieldref(this._className, "_document", "Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
        il.append(Number.ALOAD_0);
        il.append(new GETFIELD(field));
        local.setStart(il.append(new ASTORE(local.getIndex())));
        matchGen.setDomIndex(local.getIndex());
    }
    
    private void compilePatterns(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this._className = this.getXSLTC().getHelperClassName();
        final NodeCounterGenerator nodeCounterGen = new NodeCounterGenerator(this._className, Number.ClassNames[this._level], this.toString(), 33, null, classGen.getStylesheet());
        InstructionList il = null;
        ConstantPoolGen cpg = nodeCounterGen.getConstantPool();
        final int closureLen = (this._closureVars == null) ? 0 : this._closureVars.size();
        for (int i = 0; i < closureLen; ++i) {
            final VariableBase var = this._closureVars.get(i).getVariable();
            nodeCounterGen.addField(new Field(1, cpg.addUtf8(var.getEscapedName()), cpg.addUtf8(var.getType().toSignature()), null, cpg.getConstantPool()));
        }
        this.compileConstructor(nodeCounterGen);
        if (this._from != null) {
            il = new InstructionList();
            final MatchGenerator matchGen = new MatchGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN, new com.sun.org.apache.bcel.internal.generic.Type[] { com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "node" }, "matchesFrom", this._className, il, cpg);
            this.compileLocals(nodeCounterGen, matchGen, il);
            il.append(matchGen.loadContextNode());
            this._from.translate(nodeCounterGen, matchGen);
            this._from.synthesize(nodeCounterGen, matchGen);
            il.append(Number.IRETURN);
            nodeCounterGen.addMethod(matchGen);
        }
        if (this._count != null) {
            il = new InstructionList();
            final MatchGenerator matchGen = new MatchGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.BOOLEAN, new com.sun.org.apache.bcel.internal.generic.Type[] { com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "node" }, "matchesCount", this._className, il, cpg);
            this.compileLocals(nodeCounterGen, matchGen, il);
            il.append(matchGen.loadContextNode());
            this._count.translate(nodeCounterGen, matchGen);
            this._count.synthesize(nodeCounterGen, matchGen);
            il.append(Number.IRETURN);
            nodeCounterGen.addMethod(matchGen);
        }
        this.getXSLTC().dumpClass(nodeCounterGen.getJavaClass());
        cpg = classGen.getConstantPool();
        il = methodGen.getInstructionList();
        final int index = cpg.addMethodref(this._className, "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/Translet;Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Z)V");
        il.append(new NEW(cpg.addClass(this._className)));
        il.append(Number.DUP);
        il.append(classGen.loadTranslet());
        il.append(methodGen.loadDOM());
        il.append(methodGen.loadIterator());
        il.append((this._from != null) ? Number.ICONST_1 : Number.ICONST_0);
        il.append(new INVOKESPECIAL(index));
        for (int j = 0; j < closureLen; ++j) {
            final VariableRefBase varRef = this._closureVars.get(j);
            final VariableBase var2 = varRef.getVariable();
            final Type varType = var2.getType();
            il.append(Number.DUP);
            il.append(var2.loadInstruction());
            il.append(new PUTFIELD(cpg.addFieldref(this._className, var2.getEscapedName(), varType.toSignature())));
        }
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        il.append(classGen.loadTranslet());
        if (this.hasValue()) {
            this.compileDefault(classGen, methodGen);
            this._value.translate(classGen, methodGen);
            il.append(new PUSH(cpg, 0.5));
            il.append(Number.DADD);
            int index = cpg.addMethodref("java.lang.Math", "floor", "(D)D");
            il.append(new INVOKESTATIC(index));
            index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "setValue", "(D)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
            il.append(new INVOKEVIRTUAL(index));
        }
        else if (this.isDefault()) {
            this.compileDefault(classGen, methodGen);
        }
        else {
            this.compilePatterns(classGen, methodGen);
        }
        if (!this.hasValue()) {
            il.append(methodGen.loadContextNode());
            final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "setStartNode", "(I)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
            il.append(new INVOKEVIRTUAL(index));
        }
        if (this._formatNeeded) {
            if (this._format != null) {
                this._format.translate(classGen, methodGen);
            }
            else {
                il.append(new PUSH(cpg, "1"));
            }
            if (this._lang != null) {
                this._lang.translate(classGen, methodGen);
            }
            else {
                il.append(new PUSH(cpg, "en"));
            }
            if (this._letterValue != null) {
                this._letterValue.translate(classGen, methodGen);
            }
            else {
                il.append(new PUSH(cpg, ""));
            }
            if (this._groupingSeparator != null) {
                this._groupingSeparator.translate(classGen, methodGen);
            }
            else {
                il.append(new PUSH(cpg, ""));
            }
            if (this._groupingSize != null) {
                this._groupingSize.translate(classGen, methodGen);
            }
            else {
                il.append(new PUSH(cpg, "0"));
            }
            final int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "getCounter", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)Ljava/lang/String;");
            il.append(new INVOKEVIRTUAL(index));
        }
        else {
            int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "setDefaultFormatting", "()Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeCounter;");
            il.append(new INVOKEVIRTUAL(index));
            index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeCounter", "getCounter", "()Ljava/lang/String;");
            il.append(new INVOKEVIRTUAL(index));
        }
        il.append(methodGen.loadHandler());
        int index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "characters", "(Ljava/lang/String;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;)V");
        il.append(new INVOKEVIRTUAL(index));
    }
    
    static {
        ClassNames = new String[] { "com.sun.org.apache.xalan.internal.xsltc.dom.SingleNodeCounter", "com.sun.org.apache.xalan.internal.xsltc.dom.MultipleNodeCounter", "com.sun.org.apache.xalan.internal.xsltc.dom.AnyNodeCounter" };
        FieldNames = new String[] { "___single_node_counter", "___multiple_node_counter", "___any_node_counter" };
    }
}
