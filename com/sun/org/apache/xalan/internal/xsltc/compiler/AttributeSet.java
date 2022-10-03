package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.Iterator;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.AttributeSetMethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.List;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xml.internal.utils.XML11Char;

final class AttributeSet extends TopLevelElement
{
    private static final String AttributeSetPrefix = "$as$";
    private QName _name;
    private UseAttributeSets _useSets;
    private AttributeSet _mergeSet;
    private String _method;
    private boolean _ignore;
    
    AttributeSet() {
        this._ignore = false;
    }
    
    public QName getName() {
        return this._name;
    }
    
    public String getMethodName() {
        return this._method;
    }
    
    public void ignore() {
        this._ignore = true;
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final String name = this.getAttribute("name");
        if (!XML11Char.isXML11ValidQName(name)) {
            final ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", name, this);
            parser.reportError(3, err);
        }
        this._name = parser.getQNameIgnoreDefaultNs(name);
        if (this._name == null || this._name.equals("")) {
            final ErrorMsg msg = new ErrorMsg("UNNAMED_ATTRIBSET_ERR", this);
            parser.reportError(3, msg);
        }
        final String useSets = this.getAttribute("use-attribute-sets");
        if (useSets.length() > 0) {
            if (!Util.isValidQNames(useSets)) {
                final ErrorMsg err2 = new ErrorMsg("INVALID_QNAME_ERR", useSets, this);
                parser.reportError(3, err2);
            }
            this._useSets = new UseAttributeSets(useSets, parser);
        }
        final List<SyntaxTreeNode> contents = this.getContents();
        for (int count = contents.size(), i = 0; i < count; ++i) {
            final SyntaxTreeNode child = contents.get(i);
            if (child instanceof XslAttribute) {
                parser.getSymbolTable().setCurrentNode(child);
                child.parseContents(parser);
            }
            else if (!(child instanceof Text)) {
                final ErrorMsg msg2 = new ErrorMsg("ILLEGAL_CHILD_ERR", this);
                parser.reportError(3, msg2);
            }
        }
        parser.getSymbolTable().setCurrentNode(this);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._ignore) {
            return Type.Void;
        }
        this._mergeSet = stable.addAttributeSet(this);
        this._method = "$as$" + this.getXSLTC().nextAttributeSetSerial();
        if (this._useSets != null) {
            this._useSets.typeCheck(stable);
        }
        this.typeCheckContents(stable);
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, MethodGenerator methodGen) {
        if (this._ignore) {
            return;
        }
        methodGen = new AttributeSetMethodGenerator(this._method, classGen);
        if (this._mergeSet != null) {
            final ConstantPoolGen cpg = classGen.getConstantPool();
            final InstructionList il = methodGen.getInstructionList();
            final String methodName = this._mergeSet.getMethodName();
            il.append(classGen.loadTranslet());
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadIterator());
            il.append(methodGen.loadHandler());
            il.append(methodGen.loadCurrentNode());
            final int method = cpg.addMethodref(classGen.getClassName(), methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V");
            il.append(new INVOKESPECIAL(method));
        }
        if (this._useSets != null) {
            this._useSets.translate(classGen, methodGen);
        }
        final Iterator<SyntaxTreeNode> attributes = this.elements();
        while (attributes.hasNext()) {
            final SyntaxTreeNode element = attributes.next();
            if (element instanceof XslAttribute) {
                final XslAttribute attribute = (XslAttribute)element;
                attribute.translate(classGen, methodGen);
            }
        }
        final InstructionList il = methodGen.getInstructionList();
        il.append(AttributeSet.RETURN);
        classGen.addMethod(methodGen);
    }
    
    @Override
    public String toString() {
        final StringBuffer buf = new StringBuffer("attribute-set: ");
        final Iterator<SyntaxTreeNode> attributes = this.elements();
        while (attributes.hasNext()) {
            final XslAttribute attribute = attributes.next();
            buf.append(attribute);
        }
        return buf.toString();
    }
}
