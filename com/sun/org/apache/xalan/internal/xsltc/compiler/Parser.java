package com.sun.org.apache.xalan.internal.xsltc.compiler;

import java.util.StringTokenizer;
import com.sun.java_cup.internal.runtime.Symbol;
import com.sun.java_cup.internal.runtime.Scanner;
import java.io.Reader;
import java.io.StringReader;
import com.sun.org.apache.xalan.internal.utils.ObjectFactory;
import org.xml.sax.Attributes;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodType;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.io.File;
import java.util.List;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.serializer.utils.SystemIDResolver;
import com.sun.org.apache.xalan.internal.utils.XMLSecurityManager;
import jdk.xml.internal.JdkXmlUtils;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;
import java.util.Iterator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import org.xml.sax.helpers.AttributesImpl;
import java.util.Properties;
import java.util.HashMap;
import java.util.Stack;
import java.util.Map;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.util.ArrayList;
import org.xml.sax.Locator;
import org.xml.sax.ContentHandler;

public class Parser implements Constants, ContentHandler
{
    private static final String XSL = "xsl";
    private static final String TRANSLET = "translet";
    private Locator _locator;
    private XSLTC _xsltc;
    private XPathParser _xpathParser;
    private ArrayList<ErrorMsg> _errors;
    private ArrayList<ErrorMsg> _warnings;
    private Map<String, String> _instructionClasses;
    private Map<String, String[]> _instructionAttrs;
    private Map<String, QName> _qNames;
    private Map<String, Map<String, QName>> _namespaces;
    private QName _useAttributeSets;
    private QName _excludeResultPrefixes;
    private QName _extensionElementPrefixes;
    private Map<String, Object> _variableScope;
    private Stylesheet _currentStylesheet;
    private SymbolTable _symbolTable;
    private Output _output;
    private Template _template;
    private boolean _rootNamespaceDef;
    private SyntaxTreeNode _root;
    private String _target;
    private int _currentImportPrecedence;
    private boolean _overrideDefaultParser;
    private String _PImedia;
    private String _PItitle;
    private String _PIcharset;
    private int _templateIndex;
    private boolean versionIsOne;
    private Stack<SyntaxTreeNode> _parentStack;
    private Map<String, String> _prefixMapping;
    
    public Parser(final XSLTC xsltc, final boolean useOverrideDefaultParser) {
        this._locator = null;
        this._PImedia = null;
        this._PItitle = null;
        this._PIcharset = null;
        this._templateIndex = 0;
        this.versionIsOne = true;
        this._parentStack = null;
        this._prefixMapping = null;
        this._xsltc = xsltc;
        this._overrideDefaultParser = useOverrideDefaultParser;
    }
    
    public void init() {
        this._qNames = new HashMap<String, QName>(512);
        this._namespaces = new HashMap<String, Map<String, QName>>();
        this._instructionClasses = new HashMap<String, String>();
        this._instructionAttrs = new HashMap<String, String[]>();
        this._variableScope = new HashMap<String, Object>();
        this._template = null;
        this._errors = new ArrayList<ErrorMsg>();
        this._warnings = new ArrayList<ErrorMsg>();
        this._symbolTable = new SymbolTable();
        this._xpathParser = new XPathParser(this);
        this._currentStylesheet = null;
        this._output = null;
        this._root = null;
        this._rootNamespaceDef = false;
        this._currentImportPrecedence = 1;
        this.initStdClasses();
        this.initInstructionAttrs();
        this.initExtClasses();
        this.initSymbolTable();
        this._useAttributeSets = this.getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "use-attribute-sets");
        this._excludeResultPrefixes = this.getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "exclude-result-prefixes");
        this._extensionElementPrefixes = this.getQName("http://www.w3.org/1999/XSL/Transform", "xsl", "extension-element-prefixes");
    }
    
    public void setOutput(final Output output) {
        if (this._output != null) {
            if (this._output.getImportPrecedence() <= output.getImportPrecedence()) {
                output.mergeOutput(this._output);
                this._output.disable();
                this._output = output;
            }
            else {
                output.disable();
            }
        }
        else {
            this._output = output;
        }
    }
    
    public Output getOutput() {
        return this._output;
    }
    
    public Properties getOutputProperties() {
        return this.getTopLevelStylesheet().getOutputProperties();
    }
    
    public void addVariable(final Variable var) {
        this.addVariableOrParam(var);
    }
    
    public void addParameter(final Param param) {
        this.addVariableOrParam(param);
    }
    
    private void addVariableOrParam(final VariableBase var) {
        final Object existing = this._variableScope.get(var.getName().getStringRep());
        if (existing != null) {
            if (existing instanceof Stack) {
                final Stack<VariableBase> stack = (Stack<VariableBase>)existing;
                stack.push(var);
            }
            else if (existing instanceof VariableBase) {
                final Stack<VariableBase> stack = new Stack<VariableBase>();
                stack.push((VariableBase)existing);
                stack.push(var);
                this._variableScope.put(var.getName().getStringRep(), stack);
            }
        }
        else {
            this._variableScope.put(var.getName().getStringRep(), var);
        }
    }
    
    public void removeVariable(final QName name) {
        final Object existing = this._variableScope.get(name.getStringRep());
        if (existing instanceof Stack) {
            final Stack<VariableBase> stack = (Stack<VariableBase>)existing;
            if (!stack.isEmpty()) {
                stack.pop();
            }
            if (!stack.isEmpty()) {
                return;
            }
        }
        this._variableScope.remove(name.getStringRep());
    }
    
    public VariableBase lookupVariable(final QName name) {
        final Object existing = this._variableScope.get(name.getStringRep());
        if (existing instanceof VariableBase) {
            return (VariableBase)existing;
        }
        if (existing instanceof Stack) {
            final Stack<VariableBase> stack = (Stack<VariableBase>)existing;
            return stack.peek();
        }
        return null;
    }
    
    public void setXSLTC(final XSLTC xsltc) {
        this._xsltc = xsltc;
    }
    
    public XSLTC getXSLTC() {
        return this._xsltc;
    }
    
    public int getCurrentImportPrecedence() {
        return this._currentImportPrecedence;
    }
    
    public int getNextImportPrecedence() {
        return ++this._currentImportPrecedence;
    }
    
    public void setCurrentStylesheet(final Stylesheet stylesheet) {
        this._currentStylesheet = stylesheet;
    }
    
    public Stylesheet getCurrentStylesheet() {
        return this._currentStylesheet;
    }
    
    public Stylesheet getTopLevelStylesheet() {
        return this._xsltc.getStylesheet();
    }
    
    public QName getQNameSafe(final String stringRep) {
        final int colon = stringRep.lastIndexOf(58);
        if (colon != -1) {
            final String prefix = stringRep.substring(0, colon);
            final String localname = stringRep.substring(colon + 1);
            String namespace = null;
            if (!prefix.equals("xmlns")) {
                namespace = this._symbolTable.lookupNamespace(prefix);
                if (namespace == null) {
                    namespace = "";
                }
            }
            return this.getQName(namespace, prefix, localname);
        }
        final String uri = stringRep.equals("xmlns") ? null : this._symbolTable.lookupNamespace("");
        return this.getQName(uri, null, stringRep);
    }
    
    public QName getQName(final String stringRep) {
        return this.getQName(stringRep, true, false);
    }
    
    public QName getQNameIgnoreDefaultNs(final String stringRep) {
        return this.getQName(stringRep, true, true);
    }
    
    public QName getQName(final String stringRep, final boolean reportError) {
        return this.getQName(stringRep, reportError, false);
    }
    
    private QName getQName(final String stringRep, final boolean reportError, boolean ignoreDefaultNs) {
        final int colon = stringRep.lastIndexOf(58);
        if (colon != -1) {
            final String prefix = stringRep.substring(0, colon);
            final String localname = stringRep.substring(colon + 1);
            String namespace = null;
            if (!prefix.equals("xmlns")) {
                namespace = this._symbolTable.lookupNamespace(prefix);
                if (namespace == null && reportError) {
                    final int line = this.getLineNumber();
                    final ErrorMsg err = new ErrorMsg("NAMESPACE_UNDEF_ERR", line, prefix);
                    this.reportError(3, err);
                }
            }
            return this.getQName(namespace, prefix, localname);
        }
        if (stringRep.equals("xmlns")) {
            ignoreDefaultNs = true;
        }
        final String defURI = ignoreDefaultNs ? null : this._symbolTable.lookupNamespace("");
        return this.getQName(defURI, null, stringRep);
    }
    
    public QName getQName(final String namespace, final String prefix, final String localname) {
        if (namespace == null || namespace.equals("")) {
            QName name = this._qNames.get(localname);
            if (name == null) {
                name = new QName(null, prefix, localname);
                this._qNames.put(localname, name);
            }
            return name;
        }
        Map<String, QName> space = this._namespaces.get(namespace);
        final String lexicalQName = (prefix == null || prefix.length() == 0) ? localname : (prefix + ':' + localname);
        if (space == null) {
            final QName name2 = new QName(namespace, prefix, localname);
            this._namespaces.put(namespace, space = new HashMap<String, QName>());
            space.put(lexicalQName, name2);
            return name2;
        }
        QName name2 = space.get(lexicalQName);
        if (name2 == null) {
            name2 = new QName(namespace, prefix, localname);
            space.put(lexicalQName, name2);
        }
        return name2;
    }
    
    public QName getQName(final String scope, final String name) {
        return this.getQName(scope + name);
    }
    
    public QName getQName(final QName scope, final QName name) {
        return this.getQName(scope.toString() + name.toString());
    }
    
    public QName getUseAttributeSets() {
        return this._useAttributeSets;
    }
    
    public QName getExtensionElementPrefixes() {
        return this._extensionElementPrefixes;
    }
    
    public QName getExcludeResultPrefixes() {
        return this._excludeResultPrefixes;
    }
    
    public Stylesheet makeStylesheet(final SyntaxTreeNode element) throws CompilerException {
        try {
            Stylesheet stylesheet;
            if (element instanceof Stylesheet) {
                stylesheet = (Stylesheet)element;
            }
            else {
                stylesheet = new Stylesheet();
                stylesheet.setSimplified();
                stylesheet.addElement(element);
                stylesheet.setAttributes((AttributesImpl)element.getAttributes());
                if (element.lookupNamespace("") == null) {
                    element.addPrefixMapping("", "");
                }
            }
            stylesheet.setParser(this);
            return stylesheet;
        }
        catch (final ClassCastException e) {
            final ErrorMsg err = new ErrorMsg("NOT_STYLESHEET_ERR", element);
            throw new CompilerException(err.toString());
        }
    }
    
    public void createAST(final Stylesheet stylesheet) {
        try {
            if (stylesheet != null) {
                stylesheet.parseContents(this);
                final Iterator<SyntaxTreeNode> elements = stylesheet.elements();
                while (elements.hasNext()) {
                    final SyntaxTreeNode child = elements.next();
                    if (child instanceof Text) {
                        final int l = this.getLineNumber();
                        final ErrorMsg err = new ErrorMsg("ILLEGAL_TEXT_NODE_ERR", l, (Object)null);
                        this.reportError(3, err);
                    }
                }
                if (!this.errorsFound()) {
                    stylesheet.typeCheck(this._symbolTable);
                }
            }
        }
        catch (final TypeCheckError e) {
            this.reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e));
        }
    }
    
    public SyntaxTreeNode parse(final XMLReader reader, final InputSource input) {
        try {
            reader.setContentHandler(this);
            reader.parse(input);
            return this.getStylesheet(this._root);
        }
        catch (final IOException e) {
            if (this._xsltc.debug()) {
                e.printStackTrace();
            }
            this.reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e));
        }
        catch (final SAXException e2) {
            final Throwable ex = e2.getException();
            if (this._xsltc.debug()) {
                e2.printStackTrace();
                if (ex != null) {
                    ex.printStackTrace();
                }
            }
            this.reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e2));
        }
        catch (final CompilerException e3) {
            if (this._xsltc.debug()) {
                e3.printStackTrace();
            }
            this.reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e3));
        }
        catch (final Exception e4) {
            if (this._xsltc.debug()) {
                e4.printStackTrace();
            }
            this.reportError(3, new ErrorMsg("JAXP_COMPILE_ERR", e4));
        }
        return null;
    }
    
    public SyntaxTreeNode parse(final InputSource input) {
        final XMLReader reader = JdkXmlUtils.getXMLReader(this._overrideDefaultParser, this._xsltc.isSecureProcessing());
        JdkXmlUtils.setXMLReaderPropertyIfSupport(reader, "http://javax.xml.XMLConstants/property/accessExternalDTD", this._xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalDTD"), true);
        String lastProperty = "";
        try {
            final XMLSecurityManager securityManager = (XMLSecurityManager)this._xsltc.getProperty("http://apache.org/xml/properties/security-manager");
            for (final XMLSecurityManager.Limit limit : XMLSecurityManager.Limit.values()) {
                lastProperty = limit.apiProperty();
                reader.setProperty(lastProperty, securityManager.getLimitValueAsString(limit));
            }
            if (securityManager.printEntityCountInfo()) {
                lastProperty = "http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo";
                reader.setProperty("http://www.oracle.com/xml/jaxp/properties/getEntityCountInfo", "yes");
            }
        }
        catch (final SAXException se) {
            XMLSecurityManager.printWarning(reader.getClass().getName(), lastProperty, se);
        }
        return this.parse(reader, input);
    }
    
    public SyntaxTreeNode getDocumentRoot() {
        return this._root;
    }
    
    protected void setPIParameters(final String media, final String title, final String charset) {
        this._PImedia = media;
        this._PItitle = title;
        this._PIcharset = charset;
    }
    
    private SyntaxTreeNode getStylesheet(final SyntaxTreeNode root) throws CompilerException {
        if (this._target == null) {
            if (!this._rootNamespaceDef) {
                final ErrorMsg msg = new ErrorMsg("MISSING_XSLT_URI_ERR");
                throw new CompilerException(msg.toString());
            }
            return root;
        }
        else {
            if (this._target.charAt(0) != '#') {
                try {
                    String path = this._target;
                    if (path.indexOf(":") == -1) {
                        path = "file:" + path;
                    }
                    path = SystemIDResolver.getAbsoluteURI(path);
                    final String accessError = SecuritySupport.checkAccess(path, (String)this._xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet"), "all");
                    if (accessError != null) {
                        final ErrorMsg msg2 = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(this._target), accessError, root);
                        throw new CompilerException(msg2.toString());
                    }
                }
                catch (final IOException ex) {
                    throw new CompilerException(ex);
                }
                return this.loadExternalStylesheet(this._target);
            }
            final SyntaxTreeNode element = this.findStylesheet(root, this._target.substring(1));
            if (element == null) {
                final ErrorMsg msg3 = new ErrorMsg("MISSING_XSLT_TARGET_ERR", this._target, root);
                throw new CompilerException(msg3.toString());
            }
            return element;
        }
    }
    
    private SyntaxTreeNode findStylesheet(final SyntaxTreeNode root, final String href) {
        if (root == null) {
            return null;
        }
        if (root instanceof Stylesheet) {
            final String id = root.getAttribute("id");
            if (id.equals(href)) {
                return root;
            }
        }
        final List<SyntaxTreeNode> children = root.getContents();
        if (children != null) {
            for (int count = children.size(), i = 0; i < count; ++i) {
                final SyntaxTreeNode child = children.get(i);
                final SyntaxTreeNode node = this.findStylesheet(child, href);
                if (node != null) {
                    return node;
                }
            }
        }
        return null;
    }
    
    private SyntaxTreeNode loadExternalStylesheet(final String location) throws CompilerException {
        InputSource source;
        if (new File(location).exists()) {
            source = new InputSource("file:" + location);
        }
        else {
            source = new InputSource(location);
        }
        final SyntaxTreeNode external = this.parse(source);
        return external;
    }
    
    private void initAttrTable(final String elementName, final String[] attrs) {
        this._instructionAttrs.put(this.getQName("http://www.w3.org/1999/XSL/Transform", "xsl", elementName).getStringRep(), attrs);
    }
    
    private void initInstructionAttrs() {
        this.initAttrTable("template", new String[] { "match", "name", "priority", "mode" });
        this.initAttrTable("stylesheet", new String[] { "id", "version", "extension-element-prefixes", "exclude-result-prefixes" });
        this.initAttrTable("transform", new String[] { "id", "version", "extension-element-prefixes", "exclude-result-prefixes" });
        this.initAttrTable("text", new String[] { "disable-output-escaping" });
        this.initAttrTable("if", new String[] { "test" });
        this.initAttrTable("choose", new String[0]);
        this.initAttrTable("when", new String[] { "test" });
        this.initAttrTable("otherwise", new String[0]);
        this.initAttrTable("for-each", new String[] { "select" });
        this.initAttrTable("message", new String[] { "terminate" });
        this.initAttrTable("number", new String[] { "level", "count", "from", "value", "format", "lang", "letter-value", "grouping-separator", "grouping-size" });
        this.initAttrTable("comment", new String[0]);
        this.initAttrTable("copy", new String[] { "use-attribute-sets" });
        this.initAttrTable("copy-of", new String[] { "select" });
        this.initAttrTable("param", new String[] { "name", "select" });
        this.initAttrTable("with-param", new String[] { "name", "select" });
        this.initAttrTable("variable", new String[] { "name", "select" });
        this.initAttrTable("output", new String[] { "method", "version", "encoding", "omit-xml-declaration", "standalone", "doctype-public", "doctype-system", "cdata-section-elements", "indent", "media-type" });
        this.initAttrTable("sort", new String[] { "select", "order", "case-order", "lang", "data-type" });
        this.initAttrTable("key", new String[] { "name", "match", "use" });
        this.initAttrTable("fallback", new String[0]);
        this.initAttrTable("attribute", new String[] { "name", "namespace" });
        this.initAttrTable("attribute-set", new String[] { "name", "use-attribute-sets" });
        this.initAttrTable("value-of", new String[] { "select", "disable-output-escaping" });
        this.initAttrTable("element", new String[] { "name", "namespace", "use-attribute-sets" });
        this.initAttrTable("call-template", new String[] { "name" });
        this.initAttrTable("apply-templates", new String[] { "select", "mode" });
        this.initAttrTable("apply-imports", new String[0]);
        this.initAttrTable("decimal-format", new String[] { "name", "decimal-separator", "grouping-separator", "infinity", "minus-sign", "NaN", "percent", "per-mille", "zero-digit", "digit", "pattern-separator" });
        this.initAttrTable("import", new String[] { "href" });
        this.initAttrTable("include", new String[] { "href" });
        this.initAttrTable("strip-space", new String[] { "elements" });
        this.initAttrTable("preserve-space", new String[] { "elements" });
        this.initAttrTable("processing-instruction", new String[] { "name" });
        this.initAttrTable("namespace-alias", new String[] { "stylesheet-prefix", "result-prefix" });
    }
    
    private void initStdClasses() {
        this.initStdClass("template", "Template");
        this.initStdClass("stylesheet", "Stylesheet");
        this.initStdClass("transform", "Stylesheet");
        this.initStdClass("text", "Text");
        this.initStdClass("if", "If");
        this.initStdClass("choose", "Choose");
        this.initStdClass("when", "When");
        this.initStdClass("otherwise", "Otherwise");
        this.initStdClass("for-each", "ForEach");
        this.initStdClass("message", "Message");
        this.initStdClass("number", "Number");
        this.initStdClass("comment", "Comment");
        this.initStdClass("copy", "Copy");
        this.initStdClass("copy-of", "CopyOf");
        this.initStdClass("param", "Param");
        this.initStdClass("with-param", "WithParam");
        this.initStdClass("variable", "Variable");
        this.initStdClass("output", "Output");
        this.initStdClass("sort", "Sort");
        this.initStdClass("key", "Key");
        this.initStdClass("fallback", "Fallback");
        this.initStdClass("attribute", "XslAttribute");
        this.initStdClass("attribute-set", "AttributeSet");
        this.initStdClass("value-of", "ValueOf");
        this.initStdClass("element", "XslElement");
        this.initStdClass("call-template", "CallTemplate");
        this.initStdClass("apply-templates", "ApplyTemplates");
        this.initStdClass("apply-imports", "ApplyImports");
        this.initStdClass("decimal-format", "DecimalFormatting");
        this.initStdClass("import", "Import");
        this.initStdClass("include", "Include");
        this.initStdClass("strip-space", "Whitespace");
        this.initStdClass("preserve-space", "Whitespace");
        this.initStdClass("processing-instruction", "ProcessingInstruction");
        this.initStdClass("namespace-alias", "NamespaceAlias");
    }
    
    private void initStdClass(final String elementName, final String className) {
        this._instructionClasses.put(this.getQName("http://www.w3.org/1999/XSL/Transform", "xsl", elementName).getStringRep(), "com.sun.org.apache.xalan.internal.xsltc.compiler." + className);
    }
    
    public boolean elementSupported(final String namespace, final String localName) {
        return this._instructionClasses.get(this.getQName(namespace, "xsl", localName).getStringRep()) != null;
    }
    
    public boolean functionSupported(final String fname) {
        return this._symbolTable.lookupPrimop(fname) != null;
    }
    
    private void initExtClasses() {
        this.initExtClass("output", "TransletOutput");
        this.initExtClass("http://xml.apache.org/xalan/redirect", "write", "TransletOutput");
    }
    
    private void initExtClass(final String elementName, final String className) {
        this._instructionClasses.put(this.getQName("http://xml.apache.org/xalan/xsltc", "translet", elementName).getStringRep(), "com.sun.org.apache.xalan.internal.xsltc.compiler." + className);
    }
    
    private void initExtClass(final String namespace, final String elementName, final String className) {
        this._instructionClasses.put(this.getQName(namespace, "translet", elementName).getStringRep(), "com.sun.org.apache.xalan.internal.xsltc.compiler." + className);
    }
    
    private void initSymbolTable() {
        final MethodType I_V = new MethodType(Type.Int, Type.Void);
        final MethodType I_R = new MethodType(Type.Int, Type.Real);
        final MethodType I_S = new MethodType(Type.Int, Type.String);
        final MethodType I_D = new MethodType(Type.Int, Type.NodeSet);
        final MethodType R_I = new MethodType(Type.Real, Type.Int);
        final MethodType R_V = new MethodType(Type.Real, Type.Void);
        final MethodType R_R = new MethodType(Type.Real, Type.Real);
        final MethodType R_D = new MethodType(Type.Real, Type.NodeSet);
        final MethodType R_O = new MethodType(Type.Real, Type.Reference);
        final MethodType I_I = new MethodType(Type.Int, Type.Int);
        final MethodType D_O = new MethodType(Type.NodeSet, Type.Reference);
        final MethodType D_V = new MethodType(Type.NodeSet, Type.Void);
        final MethodType D_S = new MethodType(Type.NodeSet, Type.String);
        final MethodType D_D = new MethodType(Type.NodeSet, Type.NodeSet);
        final MethodType A_V = new MethodType(Type.Node, Type.Void);
        final MethodType S_V = new MethodType(Type.String, Type.Void);
        final MethodType S_S = new MethodType(Type.String, Type.String);
        final MethodType S_A = new MethodType(Type.String, Type.Node);
        final MethodType S_D = new MethodType(Type.String, Type.NodeSet);
        final MethodType S_O = new MethodType(Type.String, Type.Reference);
        final MethodType B_O = new MethodType(Type.Boolean, Type.Reference);
        final MethodType B_V = new MethodType(Type.Boolean, Type.Void);
        final MethodType B_B = new MethodType(Type.Boolean, Type.Boolean);
        final MethodType B_S = new MethodType(Type.Boolean, Type.String);
        final MethodType D_X = new MethodType(Type.NodeSet, Type.Object);
        final MethodType R_RR = new MethodType(Type.Real, Type.Real, Type.Real);
        final MethodType I_II = new MethodType(Type.Int, Type.Int, Type.Int);
        final MethodType B_RR = new MethodType(Type.Boolean, Type.Real, Type.Real);
        final MethodType B_II = new MethodType(Type.Boolean, Type.Int, Type.Int);
        final MethodType S_SS = new MethodType(Type.String, Type.String, Type.String);
        final MethodType S_DS = new MethodType(Type.String, Type.Real, Type.String);
        final MethodType S_SR = new MethodType(Type.String, Type.String, Type.Real);
        final MethodType O_SO = new MethodType(Type.Reference, Type.String, Type.Reference);
        final MethodType D_SS = new MethodType(Type.NodeSet, Type.String, Type.String);
        final MethodType D_SD = new MethodType(Type.NodeSet, Type.String, Type.NodeSet);
        final MethodType B_BB = new MethodType(Type.Boolean, Type.Boolean, Type.Boolean);
        final MethodType B_SS = new MethodType(Type.Boolean, Type.String, Type.String);
        final MethodType S_SD = new MethodType(Type.String, Type.String, Type.NodeSet);
        final MethodType S_DSS = new MethodType(Type.String, Type.Real, Type.String, Type.String);
        final MethodType S_SRR = new MethodType(Type.String, Type.String, Type.Real, Type.Real);
        final MethodType S_SSS = new MethodType(Type.String, Type.String, Type.String, Type.String);
        this._symbolTable.addPrimop("current", A_V);
        this._symbolTable.addPrimop("last", I_V);
        this._symbolTable.addPrimop("position", I_V);
        this._symbolTable.addPrimop("true", B_V);
        this._symbolTable.addPrimop("false", B_V);
        this._symbolTable.addPrimop("not", B_B);
        this._symbolTable.addPrimop("name", S_V);
        this._symbolTable.addPrimop("name", S_A);
        this._symbolTable.addPrimop("generate-id", S_V);
        this._symbolTable.addPrimop("generate-id", S_A);
        this._symbolTable.addPrimop("ceiling", R_R);
        this._symbolTable.addPrimop("floor", R_R);
        this._symbolTable.addPrimop("round", R_R);
        this._symbolTable.addPrimop("contains", B_SS);
        this._symbolTable.addPrimop("number", R_O);
        this._symbolTable.addPrimop("number", R_V);
        this._symbolTable.addPrimop("boolean", B_O);
        this._symbolTable.addPrimop("string", S_O);
        this._symbolTable.addPrimop("string", S_V);
        this._symbolTable.addPrimop("translate", S_SSS);
        this._symbolTable.addPrimop("string-length", I_V);
        this._symbolTable.addPrimop("string-length", I_S);
        this._symbolTable.addPrimop("starts-with", B_SS);
        this._symbolTable.addPrimop("format-number", S_DS);
        this._symbolTable.addPrimop("format-number", S_DSS);
        this._symbolTable.addPrimop("unparsed-entity-uri", S_S);
        this._symbolTable.addPrimop("key", D_SS);
        this._symbolTable.addPrimop("key", D_SD);
        this._symbolTable.addPrimop("id", D_S);
        this._symbolTable.addPrimop("id", D_D);
        this._symbolTable.addPrimop("namespace-uri", S_V);
        this._symbolTable.addPrimop("function-available", B_S);
        this._symbolTable.addPrimop("element-available", B_S);
        this._symbolTable.addPrimop("document", D_S);
        this._symbolTable.addPrimop("document", D_V);
        this._symbolTable.addPrimop("count", I_D);
        this._symbolTable.addPrimop("sum", R_D);
        this._symbolTable.addPrimop("local-name", S_V);
        this._symbolTable.addPrimop("local-name", S_D);
        this._symbolTable.addPrimop("namespace-uri", S_V);
        this._symbolTable.addPrimop("namespace-uri", S_D);
        this._symbolTable.addPrimop("substring", S_SR);
        this._symbolTable.addPrimop("substring", S_SRR);
        this._symbolTable.addPrimop("substring-after", S_SS);
        this._symbolTable.addPrimop("substring-before", S_SS);
        this._symbolTable.addPrimop("normalize-space", S_V);
        this._symbolTable.addPrimop("normalize-space", S_S);
        this._symbolTable.addPrimop("system-property", S_S);
        this._symbolTable.addPrimop("nodeset", D_O);
        this._symbolTable.addPrimop("objectType", S_O);
        this._symbolTable.addPrimop("cast", O_SO);
        this._symbolTable.addPrimop("+", R_RR);
        this._symbolTable.addPrimop("-", R_RR);
        this._symbolTable.addPrimop("*", R_RR);
        this._symbolTable.addPrimop("/", R_RR);
        this._symbolTable.addPrimop("%", R_RR);
        this._symbolTable.addPrimop("+", I_II);
        this._symbolTable.addPrimop("-", I_II);
        this._symbolTable.addPrimop("*", I_II);
        this._symbolTable.addPrimop("<", B_RR);
        this._symbolTable.addPrimop("<=", B_RR);
        this._symbolTable.addPrimop(">", B_RR);
        this._symbolTable.addPrimop(">=", B_RR);
        this._symbolTable.addPrimop("<", B_II);
        this._symbolTable.addPrimop("<=", B_II);
        this._symbolTable.addPrimop(">", B_II);
        this._symbolTable.addPrimop(">=", B_II);
        this._symbolTable.addPrimop("<", B_BB);
        this._symbolTable.addPrimop("<=", B_BB);
        this._symbolTable.addPrimop(">", B_BB);
        this._symbolTable.addPrimop(">=", B_BB);
        this._symbolTable.addPrimop("or", B_BB);
        this._symbolTable.addPrimop("and", B_BB);
        this._symbolTable.addPrimop("u-", R_R);
        this._symbolTable.addPrimop("u-", I_I);
    }
    
    public SymbolTable getSymbolTable() {
        return this._symbolTable;
    }
    
    public Template getTemplate() {
        return this._template;
    }
    
    public void setTemplate(final Template template) {
        this._template = template;
    }
    
    public int getTemplateIndex() {
        return this._templateIndex++;
    }
    
    public SyntaxTreeNode makeInstance(final String uri, final String prefix, final String local, final Attributes attributes) {
        SyntaxTreeNode node = null;
        final QName qname = this.getQName(uri, prefix, local);
        final String className = this._instructionClasses.get(qname.getStringRep());
        if (className != null) {
            try {
                final Class<?> clazz = ObjectFactory.findProviderClass(className, true);
                node = (SyntaxTreeNode)clazz.newInstance();
                node.setQName(qname);
                node.setParser(this);
                if (this._locator != null) {
                    node.setLineNumber(this.getLineNumber());
                }
                if (node instanceof Stylesheet) {
                    this._xsltc.setStylesheet((Stylesheet)node);
                }
                this.checkForSuperfluousAttributes(node, attributes);
            }
            catch (final ClassNotFoundException e) {
                final ErrorMsg err = new ErrorMsg("CLASS_NOT_FOUND_ERR", node);
                this.reportError(3, err);
            }
            catch (final Exception e2) {
                final ErrorMsg err = new ErrorMsg("INTERNAL_ERR", e2.getMessage(), node);
                this.reportError(2, err);
            }
        }
        else {
            if (uri != null) {
                if (uri.equals("http://www.w3.org/1999/XSL/Transform")) {
                    node = new UnsupportedElement(uri, prefix, local, false);
                    final UnsupportedElement element = (UnsupportedElement)node;
                    final ErrorMsg msg = new ErrorMsg("UNSUPPORTED_XSL_ERR", this.getLineNumber(), local);
                    element.setErrorMessage(msg);
                    if (this.versionIsOne) {
                        this.reportError(1, msg);
                    }
                }
                else if (uri.equals("http://xml.apache.org/xalan/xsltc")) {
                    node = new UnsupportedElement(uri, prefix, local, true);
                    final UnsupportedElement element = (UnsupportedElement)node;
                    final ErrorMsg msg = new ErrorMsg("UNSUPPORTED_EXT_ERR", this.getLineNumber(), local);
                    element.setErrorMessage(msg);
                }
                else {
                    final Stylesheet sheet = this._xsltc.getStylesheet();
                    if (sheet != null && sheet.isExtension(uri) && sheet != this._parentStack.peek()) {
                        node = new UnsupportedElement(uri, prefix, local, true);
                        final UnsupportedElement elem = (UnsupportedElement)node;
                        final ErrorMsg msg2 = new ErrorMsg("UNSUPPORTED_EXT_ERR", this.getLineNumber(), prefix + ":" + local);
                        elem.setErrorMessage(msg2);
                    }
                }
            }
            if (node == null) {
                node = new LiteralElement();
                node.setLineNumber(this.getLineNumber());
            }
        }
        if (node != null && node instanceof LiteralElement) {
            node.setQName(qname);
        }
        return node;
    }
    
    private void checkForSuperfluousAttributes(final SyntaxTreeNode node, final Attributes attrs) {
        final QName qname = node.getQName();
        final boolean isStylesheet = node instanceof Stylesheet;
        final String[] legal = this._instructionAttrs.get(qname.getStringRep());
        if (this.versionIsOne && legal != null) {
            for (int n = attrs.getLength(), i = 0; i < n; ++i) {
                final String attrQName = attrs.getQName(i);
                if (isStylesheet && attrQName.equals("version")) {
                    this.versionIsOne = attrs.getValue(i).equals("1.0");
                }
                if (!attrQName.startsWith("xml")) {
                    if (attrQName.indexOf(58) <= 0) {
                        int j;
                        for (j = 0; j < legal.length && !attrQName.equalsIgnoreCase(legal[j]); ++j) {}
                        if (j == legal.length) {
                            final ErrorMsg err = new ErrorMsg("ILLEGAL_ATTRIBUTE_ERR", attrQName, node);
                            err.setWarningError(true);
                            this.reportError(4, err);
                        }
                    }
                }
            }
        }
    }
    
    public Expression parseExpression(final SyntaxTreeNode parent, final String exp) {
        return (Expression)this.parseTopLevel(parent, "<EXPRESSION>" + exp, null);
    }
    
    public Expression parseExpression(final SyntaxTreeNode parent, final String attr, final String def) {
        String exp = parent.getAttribute(attr);
        if (exp.length() == 0 && def != null) {
            exp = def;
        }
        return (Expression)this.parseTopLevel(parent, "<EXPRESSION>" + exp, exp);
    }
    
    public Pattern parsePattern(final SyntaxTreeNode parent, final String pattern) {
        return (Pattern)this.parseTopLevel(parent, "<PATTERN>" + pattern, pattern);
    }
    
    public Pattern parsePattern(final SyntaxTreeNode parent, final String attr, final String def) {
        String pattern = parent.getAttribute(attr);
        if (pattern.length() == 0 && def != null) {
            pattern = def;
        }
        return (Pattern)this.parseTopLevel(parent, "<PATTERN>" + pattern, pattern);
    }
    
    private SyntaxTreeNode parseTopLevel(final SyntaxTreeNode parent, final String text, final String expression) {
        final int line = this.getLineNumber();
        try {
            this._xpathParser.setScanner(new XPathLexer(new StringReader(text)));
            final Symbol result = this._xpathParser.parse(expression, line);
            if (result != null) {
                final SyntaxTreeNode node = (SyntaxTreeNode)result.value;
                if (node != null) {
                    node.setParser(this);
                    node.setParent(parent);
                    node.setLineNumber(line);
                    return node;
                }
            }
            this.reportError(3, new ErrorMsg("XPATH_PARSER_ERR", expression, parent));
        }
        catch (final Exception e) {
            if (this._xsltc.debug()) {
                e.printStackTrace();
            }
            this.reportError(3, new ErrorMsg("XPATH_PARSER_ERR", expression, parent));
        }
        SyntaxTreeNode.Dummy.setParser(this);
        return SyntaxTreeNode.Dummy;
    }
    
    public boolean errorsFound() {
        return this._errors.size() > 0;
    }
    
    public void printErrors() {
        final int size = this._errors.size();
        if (size > 0) {
            System.err.println(new ErrorMsg("COMPILER_ERROR_KEY"));
            for (int i = 0; i < size; ++i) {
                System.err.println("  " + this._errors.get(i));
            }
        }
    }
    
    public void printWarnings() {
        final int size = this._warnings.size();
        if (size > 0) {
            System.err.println(new ErrorMsg("COMPILER_WARNING_KEY"));
            for (int i = 0; i < size; ++i) {
                System.err.println("  " + this._warnings.get(i));
            }
        }
    }
    
    public void reportError(final int category, final ErrorMsg error) {
        switch (category) {
            case 0: {
                this._errors.add(error);
                break;
            }
            case 1: {
                this._errors.add(error);
                break;
            }
            case 2: {
                this._errors.add(error);
                break;
            }
            case 3: {
                this._errors.add(error);
                break;
            }
            case 4: {
                this._warnings.add(error);
                break;
            }
        }
    }
    
    public ArrayList<ErrorMsg> getErrors() {
        return this._errors;
    }
    
    public ArrayList<ErrorMsg> getWarnings() {
        return this._warnings;
    }
    
    @Override
    public void startDocument() {
        this._root = null;
        this._target = null;
        this._prefixMapping = null;
        this._parentStack = new Stack<SyntaxTreeNode>();
    }
    
    @Override
    public void endDocument() {
    }
    
    @Override
    public void startPrefixMapping(final String prefix, final String uri) {
        if (this._prefixMapping == null) {
            this._prefixMapping = new HashMap<String, String>();
        }
        this._prefixMapping.put(prefix, uri);
    }
    
    @Override
    public void endPrefixMapping(final String prefix) {
    }
    
    @Override
    public void startElement(final String uri, final String localname, final String qname, final Attributes attributes) throws SAXException {
        final int col = qname.lastIndexOf(58);
        final String prefix = (col == -1) ? null : qname.substring(0, col);
        final SyntaxTreeNode element = this.makeInstance(uri, prefix, localname, attributes);
        if (element == null) {
            final ErrorMsg err = new ErrorMsg("ELEMENT_PARSE_ERR", prefix + ':' + localname);
            throw new SAXException(err.toString());
        }
        if (this._root == null) {
            if (this._prefixMapping == null || !this._prefixMapping.containsValue("http://www.w3.org/1999/XSL/Transform")) {
                this._rootNamespaceDef = false;
            }
            else {
                this._rootNamespaceDef = true;
            }
            this._root = element;
        }
        else {
            final SyntaxTreeNode parent = this._parentStack.peek();
            parent.addElement(element);
            element.setParent(parent);
        }
        element.setAttributes(new AttributesImpl(attributes));
        element.setPrefixMapping(this._prefixMapping);
        if (element instanceof Stylesheet) {
            this.getSymbolTable().setCurrentNode(element);
            ((Stylesheet)element).declareExtensionPrefixes(this);
        }
        this._prefixMapping = null;
        this._parentStack.push(element);
    }
    
    @Override
    public void endElement(final String uri, final String localname, final String qname) {
        this._parentStack.pop();
    }
    
    @Override
    public void characters(final char[] ch, final int start, final int length) {
        final String string = new String(ch, start, length);
        final SyntaxTreeNode parent = this._parentStack.peek();
        if (string.length() == 0) {
            return;
        }
        if (parent instanceof Text) {
            ((Text)parent).setText(string);
            return;
        }
        if (parent instanceof Stylesheet) {
            return;
        }
        final SyntaxTreeNode bro = parent.lastChild();
        if (bro != null && bro instanceof Text) {
            final Text text = (Text)bro;
            if (!text.isTextElement() && (length > 1 || ch[0] < '\u0100')) {
                text.setText(string);
                return;
            }
        }
        parent.addElement(new Text(string));
    }
    
    private String getTokenValue(final String token) {
        final int start = token.indexOf(34);
        final int stop = token.lastIndexOf(34);
        return token.substring(start + 1, stop);
    }
    
    @Override
    public void processingInstruction(final String name, final String value) {
        if (this._target == null && name.equals("xml-stylesheet")) {
            String href = null;
            String media = null;
            String title = null;
            String charset = null;
            final StringTokenizer tokens = new StringTokenizer(value);
            while (tokens.hasMoreElements()) {
                final String token = (String)tokens.nextElement();
                if (token.startsWith("href")) {
                    href = this.getTokenValue(token);
                }
                else if (token.startsWith("media")) {
                    media = this.getTokenValue(token);
                }
                else if (token.startsWith("title")) {
                    title = this.getTokenValue(token);
                }
                else {
                    if (!token.startsWith("charset")) {
                        continue;
                    }
                    charset = this.getTokenValue(token);
                }
            }
            if ((this._PImedia == null || this._PImedia.equals(media)) && (this._PItitle == null || this._PImedia.equals(title)) && (this._PIcharset == null || this._PImedia.equals(charset))) {
                this._target = href;
            }
        }
    }
    
    @Override
    public void ignorableWhitespace(final char[] ch, final int start, final int length) {
    }
    
    @Override
    public void skippedEntity(final String name) {
    }
    
    @Override
    public void setDocumentLocator(final Locator locator) {
        this._locator = locator;
    }
    
    private int getLineNumber() {
        int line = 0;
        if (this._locator != null) {
            line = this._locator.getLineNumber();
        }
        return line;
    }
}
