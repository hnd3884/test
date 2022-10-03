package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.NamedMethodGenerator;
import com.sun.org.apache.bcel.internal.generic.Instruction;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.List;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Vector;

public final class Template extends TopLevelElement
{
    private QName _name;
    private QName _mode;
    private Pattern _pattern;
    private double _priority;
    private int _position;
    private boolean _disabled;
    private boolean _compiled;
    private boolean _simplified;
    private boolean _isSimpleNamedTemplate;
    private Vector<Param> _parameters;
    private Stylesheet _stylesheet;
    
    public Template() {
        this._disabled = false;
        this._compiled = false;
        this._simplified = false;
        this._isSimpleNamedTemplate = false;
        this._parameters = new Vector<Param>();
        this._stylesheet = null;
    }
    
    public boolean hasParams() {
        return this._parameters.size() > 0;
    }
    
    public boolean isSimplified() {
        return this._simplified;
    }
    
    public void setSimplified() {
        this._simplified = true;
    }
    
    public boolean isSimpleNamedTemplate() {
        return this._isSimpleNamedTemplate;
    }
    
    public void addParameter(final Param param) {
        this._parameters.addElement(param);
    }
    
    public Vector<Param> getParameters() {
        return this._parameters;
    }
    
    public void disable() {
        this._disabled = true;
    }
    
    public boolean disabled() {
        return this._disabled;
    }
    
    public double getPriority() {
        return this._priority;
    }
    
    public int getPosition() {
        return this._position;
    }
    
    public boolean isNamed() {
        return this._name != null;
    }
    
    public Pattern getPattern() {
        return this._pattern;
    }
    
    public QName getName() {
        return this._name;
    }
    
    public void setName(final QName qname) {
        if (this._name == null) {
            this._name = qname;
        }
    }
    
    public QName getModeName() {
        return this._mode;
    }
    
    public int compareTo(final Object template) {
        final Template other = (Template)template;
        if (this._priority > other._priority) {
            return 1;
        }
        if (this._priority < other._priority) {
            return -1;
        }
        if (this._position > other._position) {
            return 1;
        }
        if (this._position < other._position) {
            return -1;
        }
        return 0;
    }
    
    @Override
    public void display(final int indent) {
        Util.println('\n');
        this.indent(indent);
        if (this._name != null) {
            this.indent(indent);
            Util.println("name = " + this._name);
        }
        else if (this._pattern != null) {
            this.indent(indent);
            Util.println("match = " + this._pattern.toString());
        }
        if (this._mode != null) {
            this.indent(indent);
            Util.println("mode = " + this._mode);
        }
        this.displayContents(indent + 4);
    }
    
    private boolean resolveNamedTemplates(final Template other, final Parser parser) {
        if (other == null) {
            return true;
        }
        final SymbolTable stable = parser.getSymbolTable();
        final int us = this.getImportPrecedence();
        final int them = other.getImportPrecedence();
        if (us > them) {
            other.disable();
            return true;
        }
        if (us < them) {
            stable.addTemplate(other);
            this.disable();
            return true;
        }
        return false;
    }
    
    @Override
    public Stylesheet getStylesheet() {
        return this._stylesheet;
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final String name = this.getAttribute("name");
        final String mode = this.getAttribute("mode");
        final String match = this.getAttribute("match");
        final String priority = this.getAttribute("priority");
        this._stylesheet = super.getStylesheet();
        if (name.length() > 0) {
            if (!XML11Char.isXML11ValidQName(name)) {
                final ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", name, this);
                parser.reportError(3, err);
            }
            this._name = parser.getQNameIgnoreDefaultNs(name);
        }
        if (mode.length() > 0) {
            if (!XML11Char.isXML11ValidQName(mode)) {
                final ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", mode, this);
                parser.reportError(3, err);
            }
            this._mode = parser.getQNameIgnoreDefaultNs(mode);
        }
        if (match.length() > 0) {
            this._pattern = parser.parsePattern(this, "match", null);
        }
        if (priority.length() > 0) {
            this._priority = Double.parseDouble(priority);
        }
        else if (this._pattern != null) {
            this._priority = this._pattern.getPriority();
        }
        else {
            this._priority = Double.NaN;
        }
        this._position = parser.getTemplateIndex();
        if (this._name != null) {
            final Template other = parser.getSymbolTable().addTemplate(this);
            if (!this.resolveNamedTemplates(other, parser)) {
                final ErrorMsg err2 = new ErrorMsg("TEMPLATE_REDEF_ERR", this._name, this);
                parser.reportError(3, err2);
            }
            if (this._pattern == null && this._mode == null) {
                this._isSimpleNamedTemplate = true;
            }
        }
        if (this._parent instanceof Stylesheet) {
            ((Stylesheet)this._parent).addTemplate(this);
        }
        parser.setTemplate(this);
        this.parseChildren(parser);
        parser.setTemplate(null);
    }
    
    public void parseSimplified(final Stylesheet stylesheet, final Parser parser) {
        this.setParent(this._stylesheet = stylesheet);
        this._name = null;
        this._mode = null;
        this._priority = Double.NaN;
        this._pattern = parser.parsePattern(this, "/");
        final List<SyntaxTreeNode> contents = this._stylesheet.getContents();
        final SyntaxTreeNode root = contents.get(0);
        if (root instanceof LiteralElement) {
            this.addElement(root);
            root.setParent(this);
            contents.set(0, this);
            parser.setTemplate(this);
            root.parseContents(parser);
            parser.setTemplate(null);
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._pattern != null) {
            this._pattern.typeCheck(stable);
        }
        return this.typeCheckContents(stable);
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (this._disabled) {
            return;
        }
        final String className = classGen.getClassName();
        if (this._compiled && this.isNamed()) {
            final String methodName = Util.escape(this._name.toString());
            il.append(classGen.loadTranslet());
            il.append(methodGen.loadDOM());
            il.append(methodGen.loadIterator());
            il.append(methodGen.loadHandler());
            il.append(methodGen.loadCurrentNode());
            il.append(new INVOKEVIRTUAL(cpg.addMethodref(className, methodName, "(Lcom/sun/org/apache/xalan/internal/xsltc/DOM;Lcom/sun/org/apache/xml/internal/dtm/DTMAxisIterator;Lcom/sun/org/apache/xml/internal/serializer/SerializationHandler;I)V")));
            return;
        }
        if (this._compiled) {
            return;
        }
        this._compiled = true;
        if (this._isSimpleNamedTemplate && methodGen instanceof NamedMethodGenerator) {
            final int numParams = this._parameters.size();
            final NamedMethodGenerator namedMethodGen = (NamedMethodGenerator)methodGen;
            for (int i = 0; i < numParams; ++i) {
                final Param param = this._parameters.elementAt(i);
                param.setLoadInstruction(namedMethodGen.loadParameter(i));
                param.setStoreInstruction(namedMethodGen.storeParameter(i));
            }
        }
        this.translateContents(classGen, methodGen);
        il.setPositions(true);
    }
}
