package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.BranchInstruction;
import com.sun.org.apache.bcel.internal.generic.TABLESWITCH;
import com.sun.org.apache.bcel.internal.generic.NOP;
import com.sun.org.apache.bcel.internal.generic.ILOAD;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.CompareGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSortRecordGenerator;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.classfile.Attribute;
import com.sun.org.apache.bcel.internal.classfile.Field;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NodeSortRecordFactGenerator;
import com.sun.org.apache.bcel.internal.generic.PUTFIELD;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.StringType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.IntType;
import java.util.ArrayList;

final class Sort extends Instruction implements Closure
{
    private Expression _select;
    private AttributeValue _order;
    private AttributeValue _caseOrder;
    private AttributeValue _dataType;
    private AttributeValue _lang;
    private String _className;
    private ArrayList<VariableRefBase> _closureVars;
    private boolean _needsSortRecordFactory;
    
    Sort() {
        this._className = null;
        this._closureVars = null;
        this._needsSortRecordFactory = false;
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
            this._closureVars = new ArrayList<VariableRefBase>();
        }
        if (!this._closureVars.contains(variableRef)) {
            this._closureVars.add(variableRef);
            this._needsSortRecordFactory = true;
        }
    }
    
    private void setInnerClassName(final String className) {
        this._className = className;
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final SyntaxTreeNode parent = this.getParent();
        if (!(parent instanceof ApplyTemplates) && !(parent instanceof ForEach)) {
            this.reportError(this, parser, "STRAY_SORT_ERR", null);
            return;
        }
        this._select = parser.parseExpression(this, "select", "string(.)");
        String val = this.getAttribute("order");
        if (val.length() == 0) {
            val = "ascending";
        }
        this._order = AttributeValue.create(this, val, parser);
        val = this.getAttribute("data-type");
        if (val.length() == 0) {
            try {
                final Type type = this._select.typeCheck(parser.getSymbolTable());
                if (type instanceof IntType) {
                    val = "number";
                }
                else {
                    val = "text";
                }
            }
            catch (final TypeCheckError e) {
                val = "text";
            }
        }
        this._dataType = AttributeValue.create(this, val, parser);
        val = this.getAttribute("lang");
        this._lang = AttributeValue.create(this, val, parser);
        val = this.getAttribute("case-order");
        this._caseOrder = AttributeValue.create(this, val, parser);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        final Type tselect = this._select.typeCheck(stable);
        if (!(tselect instanceof StringType)) {
            this._select = new CastExpr(this._select, Type.String);
        }
        this._order.typeCheck(stable);
        this._caseOrder.typeCheck(stable);
        this._dataType.typeCheck(stable);
        this._lang.typeCheck(stable);
        return Type.Void;
    }
    
    public void translateSortType(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this._dataType.translate(classGen, methodGen);
    }
    
    public void translateSortOrder(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this._order.translate(classGen, methodGen);
    }
    
    public void translateCaseOrder(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this._caseOrder.translate(classGen, methodGen);
    }
    
    public void translateLang(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this._lang.translate(classGen, methodGen);
    }
    
    public void translateSelect(final ClassGenerator classGen, final MethodGenerator methodGen) {
        this._select.translate(classGen, methodGen);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
    }
    
    public static void translateSortIterator(final ClassGenerator classGen, final MethodGenerator methodGen, final Expression nodeSet, final Vector<Sort> sortObjects) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final int init = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.SortingIterator", "<init>", "(Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory;)V");
        final LocalVariableGen nodesTemp = methodGen.addLocalVariable("sort_tmp1", Util.getJCRefType("Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;"), null, null);
        final LocalVariableGen sortRecordFactoryTemp = methodGen.addLocalVariable("sort_tmp2", Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory;"), null, null);
        if (nodeSet == null) {
            final int children = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getAxisIterator", "(I)Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;");
            il.append(methodGen.loadDOM());
            il.append(new PUSH(cpg, 3));
            il.append(new INVOKEINTERFACE(children, 2));
        }
        else {
            nodeSet.translate(classGen, methodGen);
        }
        nodesTemp.setStart(il.append(new ASTORE(nodesTemp.getIndex())));
        compileSortRecordFactory(sortObjects, classGen, methodGen);
        sortRecordFactoryTemp.setStart(il.append(new ASTORE(sortRecordFactoryTemp.getIndex())));
        il.append(new NEW(cpg.addClass("com.sun.org.apache.xalan.internal.xsltc.dom.SortingIterator")));
        il.append(Sort.DUP);
        nodesTemp.setEnd(il.append(new ALOAD(nodesTemp.getIndex())));
        sortRecordFactoryTemp.setEnd(il.append(new ALOAD(sortRecordFactoryTemp.getIndex())));
        il.append(new INVOKESPECIAL(init));
    }
    
    public static void compileSortRecordFactory(final Vector<Sort> sortObjects, final ClassGenerator classGen, final MethodGenerator methodGen) {
        final String sortRecordClass = compileSortRecord(sortObjects, classGen, methodGen);
        boolean needsSortRecordFactory = false;
        final int nsorts = sortObjects.size();
        for (int i = 0; i < nsorts; ++i) {
            final Sort sort = sortObjects.elementAt(i);
            needsSortRecordFactory |= sort._needsSortRecordFactory;
        }
        String sortRecordFactoryClass = "com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory";
        if (needsSortRecordFactory) {
            sortRecordFactoryClass = compileSortRecordFactory(sortObjects, classGen, methodGen, sortRecordClass);
        }
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final LocalVariableGen sortOrderTemp = methodGen.addLocalVariable("sort_order_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
        il.append(new PUSH(cpg, nsorts));
        il.append(new ANEWARRAY(cpg.addClass("java.lang.String")));
        for (int level = 0; level < nsorts; ++level) {
            final Sort sort2 = sortObjects.elementAt(level);
            il.append(Sort.DUP);
            il.append(new PUSH(cpg, level));
            sort2.translateSortOrder(classGen, methodGen);
            il.append(Sort.AASTORE);
        }
        sortOrderTemp.setStart(il.append(new ASTORE(sortOrderTemp.getIndex())));
        final LocalVariableGen sortTypeTemp = methodGen.addLocalVariable("sort_type_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
        il.append(new PUSH(cpg, nsorts));
        il.append(new ANEWARRAY(cpg.addClass("java.lang.String")));
        for (int level2 = 0; level2 < nsorts; ++level2) {
            final Sort sort3 = sortObjects.elementAt(level2);
            il.append(Sort.DUP);
            il.append(new PUSH(cpg, level2));
            sort3.translateSortType(classGen, methodGen);
            il.append(Sort.AASTORE);
        }
        sortTypeTemp.setStart(il.append(new ASTORE(sortTypeTemp.getIndex())));
        final LocalVariableGen sortLangTemp = methodGen.addLocalVariable("sort_lang_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
        il.append(new PUSH(cpg, nsorts));
        il.append(new ANEWARRAY(cpg.addClass("java.lang.String")));
        for (int level3 = 0; level3 < nsorts; ++level3) {
            final Sort sort4 = sortObjects.elementAt(level3);
            il.append(Sort.DUP);
            il.append(new PUSH(cpg, level3));
            sort4.translateLang(classGen, methodGen);
            il.append(Sort.AASTORE);
        }
        sortLangTemp.setStart(il.append(new ASTORE(sortLangTemp.getIndex())));
        final LocalVariableGen sortCaseOrderTemp = methodGen.addLocalVariable("sort_case_order_tmp", Util.getJCRefType("[Ljava/lang/String;"), null, null);
        il.append(new PUSH(cpg, nsorts));
        il.append(new ANEWARRAY(cpg.addClass("java.lang.String")));
        for (int level4 = 0; level4 < nsorts; ++level4) {
            final Sort sort5 = sortObjects.elementAt(level4);
            il.append(Sort.DUP);
            il.append(new PUSH(cpg, level4));
            sort5.translateCaseOrder(classGen, methodGen);
            il.append(Sort.AASTORE);
        }
        sortCaseOrderTemp.setStart(il.append(new ASTORE(sortCaseOrderTemp.getIndex())));
        il.append(new NEW(cpg.addClass(sortRecordFactoryClass)));
        il.append(Sort.DUP);
        il.append(methodGen.loadDOM());
        il.append(new PUSH(cpg, sortRecordClass));
        il.append(classGen.loadTranslet());
        sortOrderTemp.setEnd(il.append(new ALOAD(sortOrderTemp.getIndex())));
        sortTypeTemp.setEnd(il.append(new ALOAD(sortTypeTemp.getIndex())));
        sortLangTemp.setEnd(il.append(new ALOAD(sortLangTemp.getIndex())));
        sortCaseOrderTemp.setEnd(il.append(new ALOAD(sortCaseOrderTemp.getIndex())));
        il.append(new INVOKESPECIAL(cpg.addMethodref(sortRecordFactoryClass, "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V")));
        final ArrayList<VariableRefBase> dups = new ArrayList<VariableRefBase>();
        for (int j = 0; j < nsorts; ++j) {
            final Sort sort6 = sortObjects.get(j);
            for (int length = (sort6._closureVars == null) ? 0 : sort6._closureVars.size(), k = 0; k < length; ++k) {
                final VariableRefBase varRef = sort6._closureVars.get(k);
                if (!dups.contains(varRef)) {
                    final VariableBase var = varRef.getVariable();
                    il.append(Sort.DUP);
                    il.append(var.loadInstruction());
                    il.append(new PUTFIELD(cpg.addFieldref(sortRecordFactoryClass, var.getEscapedName(), var.getType().toSignature())));
                    dups.add(varRef);
                }
            }
        }
    }
    
    public static String compileSortRecordFactory(final Vector<Sort> sortObjects, final ClassGenerator classGen, final MethodGenerator methodGen, final String sortRecordClass) {
        final XSLTC xsltc = sortObjects.firstElement().getXSLTC();
        final String className = xsltc.getHelperClassName();
        final NodeSortRecordFactGenerator sortRecordFactory = new NodeSortRecordFactGenerator(className, "com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory", className + ".java", 49, new String[0], classGen.getStylesheet());
        final ConstantPoolGen cpg = sortRecordFactory.getConstantPool();
        final int nsorts = sortObjects.size();
        final ArrayList<VariableRefBase> dups = new ArrayList<VariableRefBase>();
        for (int j = 0; j < nsorts; ++j) {
            final Sort sort = sortObjects.get(j);
            for (int length = (sort._closureVars == null) ? 0 : sort._closureVars.size(), i = 0; i < length; ++i) {
                final VariableRefBase varRef = sort._closureVars.get(i);
                if (!dups.contains(varRef)) {
                    final VariableBase var = varRef.getVariable();
                    sortRecordFactory.addField(new Field(1, cpg.addUtf8(var.getEscapedName()), cpg.addUtf8(var.getType().toSignature()), null, cpg.getConstantPool()));
                    dups.add(varRef);
                }
            }
        }
        final com.sun.org.apache.bcel.internal.generic.Type[] argTypes = { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), Util.getJCRefType("Ljava/lang/String;"), Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/Translet;"), Util.getJCRefType("[Ljava/lang/String;"), Util.getJCRefType("[Ljava/lang/String;"), Util.getJCRefType("[Ljava/lang/String;"), Util.getJCRefType("[Ljava/lang/String;") };
        final String[] argNames = { "document", "className", "translet", "order", "type", "lang", "case_order" };
        InstructionList il = new InstructionList();
        final MethodGenerator constructor = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, argTypes, argNames, "<init>", className, il, cpg);
        il.append(Sort.ALOAD_0);
        il.append(Sort.ALOAD_1);
        il.append(Sort.ALOAD_2);
        il.append(new ALOAD(3));
        il.append(new ALOAD(4));
        il.append(new ALOAD(5));
        il.append(new ALOAD(6));
        il.append(new ALOAD(7));
        il.append(new INVOKESPECIAL(cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Ljava/lang/String;Lcom/sun/org/apache/xalan/internal/xsltc/Translet;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;[Ljava/lang/String;)V")));
        il.append(Sort.RETURN);
        il = new InstructionList();
        final MethodGenerator makeNodeSortRecord = new MethodGenerator(1, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecord;"), new com.sun.org.apache.bcel.internal.generic.Type[] { com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "node", "last" }, "makeNodeSortRecord", className, il, cpg);
        il.append(Sort.ALOAD_0);
        il.append(Sort.ILOAD_1);
        il.append(Sort.ILOAD_2);
        il.append(new INVOKESPECIAL(cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecordFactory", "makeNodeSortRecord", "(II)Lcom/sun/org/apache/xalan/internal/xsltc/dom/NodeSortRecord;")));
        il.append(Sort.DUP);
        il.append(new CHECKCAST(cpg.addClass(sortRecordClass)));
        for (int ndups = dups.size(), k = 0; k < ndups; ++k) {
            final VariableRefBase varRef2 = dups.get(k);
            final VariableBase var2 = varRef2.getVariable();
            final Type varType = var2.getType();
            il.append(Sort.DUP);
            il.append(Sort.ALOAD_0);
            il.append(new GETFIELD(cpg.addFieldref(className, var2.getEscapedName(), varType.toSignature())));
            il.append(new PUTFIELD(cpg.addFieldref(sortRecordClass, var2.getEscapedName(), varType.toSignature())));
        }
        il.append(Sort.POP);
        il.append(Sort.ARETURN);
        constructor.setMaxLocals();
        constructor.setMaxStack();
        sortRecordFactory.addMethod(constructor);
        makeNodeSortRecord.setMaxLocals();
        makeNodeSortRecord.setMaxStack();
        sortRecordFactory.addMethod(makeNodeSortRecord);
        xsltc.dumpClass(sortRecordFactory.getJavaClass());
        return className;
    }
    
    private static String compileSortRecord(final Vector<Sort> sortObjects, final ClassGenerator classGen, final MethodGenerator methodGen) {
        final XSLTC xsltc = sortObjects.firstElement().getXSLTC();
        final String className = xsltc.getHelperClassName();
        final NodeSortRecordGenerator sortRecord = new NodeSortRecordGenerator(className, "com.sun.org.apache.xalan.internal.xsltc.dom.NodeSortRecord", "sort$0.java", 49, new String[0], classGen.getStylesheet());
        final ConstantPoolGen cpg = sortRecord.getConstantPool();
        final int nsorts = sortObjects.size();
        final ArrayList<VariableRefBase> dups = new ArrayList<VariableRefBase>();
        for (int j = 0; j < nsorts; ++j) {
            final Sort sort = sortObjects.get(j);
            sort.setInnerClassName(className);
            for (int length = (sort._closureVars == null) ? 0 : sort._closureVars.size(), i = 0; i < length; ++i) {
                final VariableRefBase varRef = sort._closureVars.get(i);
                if (!dups.contains(varRef)) {
                    final VariableBase var = varRef.getVariable();
                    sortRecord.addField(new Field(1, cpg.addUtf8(var.getEscapedName()), cpg.addUtf8(var.getType().toSignature()), null, cpg.getConstantPool()));
                    dups.add(varRef);
                }
            }
        }
        final MethodGenerator init = compileInit(sortRecord, cpg, className);
        final MethodGenerator extract = compileExtract(sortObjects, sortRecord, cpg, className);
        sortRecord.addMethod(init);
        sortRecord.addMethod(extract);
        xsltc.dumpClass(sortRecord.getJavaClass());
        return className;
    }
    
    private static MethodGenerator compileInit(final NodeSortRecordGenerator sortRecord, final ConstantPoolGen cpg, final String className) {
        final InstructionList il = new InstructionList();
        final MethodGenerator init = new MethodGenerator(1, com.sun.org.apache.bcel.internal.generic.Type.VOID, null, null, "<init>", className, il, cpg);
        il.append(Sort.ALOAD_0);
        il.append(new INVOKESPECIAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.NodeSortRecord", "<init>", "()V")));
        il.append(Sort.RETURN);
        return init;
    }
    
    private static MethodGenerator compileExtract(final Vector<Sort> sortObjects, final NodeSortRecordGenerator sortRecord, final ConstantPoolGen cpg, final String className) {
        final InstructionList il = new InstructionList();
        final CompareGenerator extractMethod = new CompareGenerator(17, com.sun.org.apache.bcel.internal.generic.Type.STRING, new com.sun.org.apache.bcel.internal.generic.Type[] { Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/DOM;"), com.sun.org.apache.bcel.internal.generic.Type.INT, com.sun.org.apache.bcel.internal.generic.Type.INT, Util.getJCRefType("Lcom/sun/org/apache/xalan/internal/xsltc/runtime/AbstractTranslet;"), com.sun.org.apache.bcel.internal.generic.Type.INT }, new String[] { "dom", "current", "level", "translet", "last" }, "extractValueFromDOM", className, il, cpg);
        final int levels = sortObjects.size();
        final int[] match = new int[levels];
        final InstructionHandle[] target = new InstructionHandle[levels];
        InstructionHandle tblswitch = null;
        if (levels > 1) {
            il.append(new ILOAD(extractMethod.getLocalIndex("level")));
            tblswitch = il.append(new NOP());
        }
        for (int level = 0; level < levels; ++level) {
            match[level] = level;
            final Sort sort = sortObjects.elementAt(level);
            target[level] = il.append(Sort.NOP);
            sort.translateSelect(sortRecord, extractMethod);
            il.append(Sort.ARETURN);
        }
        if (levels > 1) {
            final InstructionHandle defaultTarget = il.append(new PUSH(cpg, ""));
            il.insert(tblswitch, new TABLESWITCH(match, target, defaultTarget));
            il.append(Sort.ARETURN);
        }
        return extractMethod;
    }
}
