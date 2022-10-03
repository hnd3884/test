package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.StringTokenizer;
import java.util.Vector;

final class UseAttributeSets extends Instruction
{
    private static final String ATTR_SET_NOT_FOUND = "";
    private final Vector _sets;
    
    public UseAttributeSets(final String setNames, final Parser parser) {
        this._sets = new Vector(2);
        this.setParser(parser);
        this.addAttributeSets(setNames);
    }
    
    public void addAttributeSets(final String setNames) {
        if (setNames != null && !setNames.equals("")) {
            final StringTokenizer tokens = new StringTokenizer(setNames);
            while (tokens.hasMoreTokens()) {
                final QName qname = this.getParser().getQNameIgnoreDefaultNs(tokens.nextToken());
                this._sets.add(qname);
            }
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final SymbolTable symbolTable = this.getParser().getSymbolTable();
        for (int i = 0; i < this._sets.size(); ++i) {
            final QName name = this._sets.elementAt(i);
            final AttributeSet attrs = symbolTable.lookupAttributeSet(name);
            if (attrs != null) {
                final String methodName = attrs.getMethodName();
                il.append(classGen.loadTranslet());
                il.append(methodGen.loadDOM());
                il.append(methodGen.loadIterator());
                il.append(methodGen.loadHandler());
                il.append(methodGen.loadCurrentNode());
                final int method = cpg.addMethodref(classGen.getClassName(), methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V");
                il.append(new INVOKESPECIAL(method));
            }
            else {
                final Parser parser = this.getParser();
                final String atrs = name.toString();
                this.reportError(this, parser, "ATTRIBSET_UNDEF_ERR", atrs);
            }
        }
    }
}
