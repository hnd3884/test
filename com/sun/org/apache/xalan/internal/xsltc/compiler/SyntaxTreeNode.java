package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.CHECKCAST;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESPECIAL;
import com.sun.org.apache.bcel.internal.generic.NEWARRAY;
import com.sun.org.apache.bcel.internal.generic.BasicType;
import com.sun.org.apache.bcel.internal.generic.ANEWARRAY;
import com.sun.org.apache.bcel.internal.generic.ICONST;
import com.sun.org.apache.bcel.internal.generic.DUP_X1;
import com.sun.org.apache.bcel.internal.generic.NEW;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKEINTERFACE;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Iterator;
import java.util.HashMap;
import org.xml.sax.Attributes;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.ArrayList;
import java.util.Map;
import org.xml.sax.helpers.AttributesImpl;
import java.util.List;

public abstract class SyntaxTreeNode implements Constants
{
    private Parser _parser;
    protected SyntaxTreeNode _parent;
    private Stylesheet _stylesheet;
    private Template _template;
    private final List<SyntaxTreeNode> _contents;
    protected QName _qname;
    private int _line;
    protected AttributesImpl _attributes;
    private Map<String, String> _prefixMapping;
    protected static final SyntaxTreeNode Dummy;
    protected static final int IndentIncrement = 4;
    private static final char[] _spaces;
    
    public SyntaxTreeNode() {
        this._contents = new ArrayList<SyntaxTreeNode>(2);
        this._attributes = null;
        this._prefixMapping = null;
        this._line = 0;
        this._qname = null;
    }
    
    public SyntaxTreeNode(final int line) {
        this._contents = new ArrayList<SyntaxTreeNode>(2);
        this._attributes = null;
        this._prefixMapping = null;
        this._line = line;
        this._qname = null;
    }
    
    public SyntaxTreeNode(final String uri, final String prefix, final String local) {
        this._contents = new ArrayList<SyntaxTreeNode>(2);
        this._attributes = null;
        this._prefixMapping = null;
        this._line = 0;
        this.setQName(uri, prefix, local);
    }
    
    protected final void setLineNumber(final int line) {
        this._line = line;
    }
    
    public final int getLineNumber() {
        if (this._line > 0) {
            return this._line;
        }
        final SyntaxTreeNode parent = this.getParent();
        return (parent != null) ? parent.getLineNumber() : 0;
    }
    
    protected void setQName(final QName qname) {
        this._qname = qname;
    }
    
    protected void setQName(final String uri, final String prefix, final String localname) {
        this._qname = new QName(uri, prefix, localname);
    }
    
    protected QName getQName() {
        return this._qname;
    }
    
    protected void setAttributes(final AttributesImpl attributes) {
        this._attributes = attributes;
    }
    
    protected String getAttribute(final String qname) {
        if (this._attributes == null) {
            return "";
        }
        final String value = this._attributes.getValue(qname);
        return (value == null || value.equals("")) ? "" : value;
    }
    
    protected String getAttribute(final String prefix, final String localName) {
        return this.getAttribute(prefix + ':' + localName);
    }
    
    protected boolean hasAttribute(final String qname) {
        return this._attributes != null && this._attributes.getValue(qname) != null;
    }
    
    protected void addAttribute(final String qname, final String value) {
        final int index = this._attributes.getIndex(qname);
        if (index != -1) {
            this._attributes.setAttribute(index, "", Util.getLocalName(qname), qname, "CDATA", value);
        }
        else {
            this._attributes.addAttribute("", Util.getLocalName(qname), qname, "CDATA", value);
        }
    }
    
    protected Attributes getAttributes() {
        return this._attributes;
    }
    
    protected void setPrefixMapping(final Map<String, String> mapping) {
        this._prefixMapping = mapping;
    }
    
    protected Map<String, String> getPrefixMapping() {
        return this._prefixMapping;
    }
    
    protected void addPrefixMapping(final String prefix, final String uri) {
        if (this._prefixMapping == null) {
            this._prefixMapping = new HashMap<String, String>();
        }
        this._prefixMapping.put(prefix, uri);
    }
    
    protected String lookupNamespace(final String prefix) {
        String uri = null;
        if (this._prefixMapping != null) {
            uri = this._prefixMapping.get(prefix);
        }
        if (uri == null && this._parent != null) {
            uri = this._parent.lookupNamespace(prefix);
            if (prefix == "" && uri == null) {
                uri = "";
            }
        }
        return uri;
    }
    
    protected String lookupPrefix(final String uri) {
        String prefix = null;
        if (this._prefixMapping != null && this._prefixMapping.containsValue(uri)) {
            for (final Map.Entry<String, String> entry : this._prefixMapping.entrySet()) {
                prefix = entry.getKey();
                final String mapsTo = entry.getValue();
                if (mapsTo.equals(uri)) {
                    return prefix;
                }
            }
        }
        else if (this._parent != null) {
            prefix = this._parent.lookupPrefix(uri);
            if (uri == "" && prefix == null) {
                prefix = "";
            }
        }
        return prefix;
    }
    
    protected void setParser(final Parser parser) {
        this._parser = parser;
    }
    
    public final Parser getParser() {
        return this._parser;
    }
    
    protected void setParent(final SyntaxTreeNode parent) {
        if (this._parent == null) {
            this._parent = parent;
        }
    }
    
    protected final SyntaxTreeNode getParent() {
        return this._parent;
    }
    
    protected final boolean isDummy() {
        return this == SyntaxTreeNode.Dummy;
    }
    
    protected int getImportPrecedence() {
        final Stylesheet stylesheet = this.getStylesheet();
        if (stylesheet == null) {
            return Integer.MIN_VALUE;
        }
        return stylesheet.getImportPrecedence();
    }
    
    public Stylesheet getStylesheet() {
        if (this._stylesheet == null) {
            SyntaxTreeNode parent;
            for (parent = this; parent != null; parent = parent.getParent()) {
                if (parent instanceof Stylesheet) {
                    return (Stylesheet)parent;
                }
            }
            this._stylesheet = (Stylesheet)parent;
        }
        return this._stylesheet;
    }
    
    protected Template getTemplate() {
        if (this._template == null) {
            SyntaxTreeNode parent;
            for (parent = this; parent != null && !(parent instanceof Template); parent = parent.getParent()) {}
            this._template = (Template)parent;
        }
        return this._template;
    }
    
    protected final XSLTC getXSLTC() {
        return this._parser.getXSLTC();
    }
    
    protected final SymbolTable getSymbolTable() {
        return (this._parser == null) ? null : this._parser.getSymbolTable();
    }
    
    public void parseContents(final Parser parser) {
        this.parseChildren(parser);
    }
    
    protected final void parseChildren(final Parser parser) {
        List<QName> locals = null;
        for (final SyntaxTreeNode child : this._contents) {
            parser.getSymbolTable().setCurrentNode(child);
            child.parseContents(parser);
            final QName varOrParamName = this.updateScope(parser, child);
            if (varOrParamName != null) {
                if (locals == null) {
                    locals = new ArrayList<QName>(2);
                }
                locals.add(varOrParamName);
            }
        }
        parser.getSymbolTable().setCurrentNode(this);
        if (locals != null) {
            for (final QName varOrParamName2 : locals) {
                parser.removeVariable(varOrParamName2);
            }
        }
    }
    
    protected QName updateScope(final Parser parser, final SyntaxTreeNode node) {
        if (node instanceof Variable) {
            final Variable var = (Variable)node;
            parser.addVariable(var);
            return var.getName();
        }
        if (node instanceof Param) {
            final Param param = (Param)node;
            parser.addParameter(param);
            return param.getName();
        }
        return null;
    }
    
    public abstract Type typeCheck(final SymbolTable p0) throws TypeCheckError;
    
    protected Type typeCheckContents(final SymbolTable stable) throws TypeCheckError {
        for (final SyntaxTreeNode item : this._contents) {
            item.typeCheck(stable);
        }
        return Type.Void;
    }
    
    public abstract void translate(final ClassGenerator p0, final MethodGenerator p1);
    
    protected void translateContents(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final int n = this.elementCount();
        for (final SyntaxTreeNode item : this._contents) {
            methodGen.markChunkStart();
            item.translate(classGen, methodGen);
            methodGen.markChunkEnd();
        }
        for (int i = 0; i < n; ++i) {
            if (this._contents.get(i) instanceof VariableBase) {
                final VariableBase var = this._contents.get(i);
                var.unmapRegister(classGen, methodGen);
            }
        }
    }
    
    private boolean isSimpleRTF(final SyntaxTreeNode node) {
        final List<SyntaxTreeNode> contents = node.getContents();
        for (final SyntaxTreeNode item : contents) {
            if (!this.isTextElement(item, false)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isAdaptiveRTF(final SyntaxTreeNode node) {
        final List<SyntaxTreeNode> contents = node.getContents();
        for (final SyntaxTreeNode item : contents) {
            if (!this.isTextElement(item, true)) {
                return false;
            }
        }
        return true;
    }
    
    private boolean isTextElement(final SyntaxTreeNode node, final boolean doExtendedCheck) {
        if (node instanceof ValueOf || node instanceof Number || node instanceof Text) {
            return true;
        }
        if (node instanceof If) {
            return doExtendedCheck ? this.isAdaptiveRTF(node) : this.isSimpleRTF(node);
        }
        if (node instanceof Choose) {
            final List<SyntaxTreeNode> contents = node.getContents();
            for (final SyntaxTreeNode item : contents) {
                if (!(item instanceof Text)) {
                    if (item instanceof When || item instanceof Otherwise) {
                        if (doExtendedCheck && this.isAdaptiveRTF(item)) {
                            continue;
                        }
                        if (!doExtendedCheck && this.isSimpleRTF(item)) {
                            continue;
                        }
                    }
                    return false;
                }
            }
            return true;
        }
        return doExtendedCheck && (node instanceof CallTemplate || node instanceof ApplyTemplates);
    }
    
    protected void compileResultTree(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        final Stylesheet stylesheet = classGen.getStylesheet();
        final boolean isSimple = this.isSimpleRTF(this);
        boolean isAdaptive = false;
        if (!isSimple) {
            isAdaptive = this.isAdaptiveRTF(this);
        }
        final int rtfType = isSimple ? 0 : (isAdaptive ? 1 : 2);
        il.append(methodGen.loadHandler());
        final String DOM_CLASS = classGen.getDOMClass();
        il.append(methodGen.loadDOM());
        int index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getResultTreeFrag", "(IIZ)Lcom/sun/org/apache/xalan/internal/xsltc/DOM;");
        il.append(new PUSH(cpg, 32));
        il.append(new PUSH(cpg, rtfType));
        il.append(new PUSH(cpg, stylesheet.callsNodeset()));
        il.append(new INVOKEINTERFACE(index, 4));
        il.append(SyntaxTreeNode.DUP);
        index = cpg.addInterfaceMethodref("com.sun.org.apache.xalan.internal.xsltc.DOM", "getOutputDomBuilder", "()Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;");
        il.append(new INVOKEINTERFACE(index, 1));
        il.append(SyntaxTreeNode.DUP);
        il.append(methodGen.storeHandler());
        il.append(methodGen.startDocument());
        this.translateContents(classGen, methodGen);
        il.append(methodGen.loadHandler());
        il.append(methodGen.endDocument());
        if (stylesheet.callsNodeset() && !DOM_CLASS.equals("com/sun/org/apache/xalan/internal/xsltc/DOM")) {
            index = cpg.addMethodref("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter", "<init>", "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;[Ljava/lang/String;[Ljava/lang/String;[I[Ljava/lang/String;)V");
            il.append(new NEW(cpg.addClass("com/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter")));
            il.append(new DUP_X1());
            il.append(SyntaxTreeNode.SWAP);
            if (!stylesheet.callsNodeset()) {
                il.append(new ICONST(0));
                il.append(new ANEWARRAY(cpg.addClass("java.lang.String")));
                il.append(SyntaxTreeNode.DUP);
                il.append(SyntaxTreeNode.DUP);
                il.append(new ICONST(0));
                il.append(new NEWARRAY(BasicType.INT));
                il.append(SyntaxTreeNode.SWAP);
                il.append(new INVOKESPECIAL(index));
            }
            else {
                il.append(SyntaxTreeNode.ALOAD_0);
                il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namesArray", "[Ljava/lang/String;")));
                il.append(SyntaxTreeNode.ALOAD_0);
                il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "urisArray", "[Ljava/lang/String;")));
                il.append(SyntaxTreeNode.ALOAD_0);
                il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "typesArray", "[I")));
                il.append(SyntaxTreeNode.ALOAD_0);
                il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "namespaceArray", "[Ljava/lang/String;")));
                il.append(new INVOKESPECIAL(index));
                il.append(SyntaxTreeNode.DUP);
                il.append(methodGen.loadDOM());
                il.append(new CHECKCAST(cpg.addClass(classGen.getDOMClass())));
                il.append(SyntaxTreeNode.SWAP);
                index = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.dom.MultiDOM", "addDOMAdapter", "(Lcom/sun/org/apache/xalan/internal/xsltc/dom/DOMAdapter;)I");
                il.append(new INVOKEVIRTUAL(index));
                il.append(SyntaxTreeNode.POP);
            }
        }
        il.append(SyntaxTreeNode.SWAP);
        il.append(methodGen.storeHandler());
    }
    
    protected boolean contextDependent() {
        return true;
    }
    
    protected boolean dependentContents() {
        for (final SyntaxTreeNode item : this._contents) {
            if (item.contextDependent()) {
                return true;
            }
        }
        return false;
    }
    
    protected final void addElement(final SyntaxTreeNode element) {
        this._contents.add(element);
        element.setParent(this);
    }
    
    protected final void setFirstElement(final SyntaxTreeNode element) {
        this._contents.add(0, element);
        element.setParent(this);
    }
    
    protected final void removeElement(final SyntaxTreeNode element) {
        this._contents.remove(element);
        element.setParent(null);
    }
    
    protected final List<SyntaxTreeNode> getContents() {
        return this._contents;
    }
    
    protected final boolean hasContents() {
        return this.elementCount() > 0;
    }
    
    protected final int elementCount() {
        return this._contents.size();
    }
    
    protected final Iterator<SyntaxTreeNode> elements() {
        return this._contents.iterator();
    }
    
    protected final SyntaxTreeNode elementAt(final int pos) {
        return this._contents.get(pos);
    }
    
    protected final SyntaxTreeNode lastChild() {
        if (this._contents.isEmpty()) {
            return null;
        }
        return this._contents.get(this._contents.size() - 1);
    }
    
    public void display(final int indent) {
        this.displayContents(indent);
    }
    
    protected void displayContents(final int indent) {
        for (final SyntaxTreeNode item : this._contents) {
            item.display(indent);
        }
    }
    
    protected final void indent(final int indent) {
        System.out.print(new String(SyntaxTreeNode._spaces, 0, indent));
    }
    
    protected void reportError(final SyntaxTreeNode element, final Parser parser, final String errorCode, final String message) {
        final ErrorMsg error = new ErrorMsg(errorCode, message, element);
        parser.reportError(3, error);
    }
    
    protected void reportWarning(final SyntaxTreeNode element, final Parser parser, final String errorCode, final String message) {
        final ErrorMsg error = new ErrorMsg(errorCode, message, element);
        parser.reportError(4, error);
    }
    
    static {
        Dummy = new AbsolutePathPattern(null);
        _spaces = "                                                       ".toCharArray();
    }
}
