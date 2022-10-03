package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xml.internal.serializer.ToHTMLStream;
import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;
import java.util.HashMap;
import java.util.Set;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import java.util.Iterator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.ArrayList;
import java.util.Hashtable;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;
import java.util.Map;
import java.util.List;

final class LiteralElement extends Instruction
{
    private String _name;
    private LiteralElement _literalElemParent;
    private List<SyntaxTreeNode> _attributeElements;
    private Map<String, String> _accessedPrefixes;
    private boolean _allAttributesUnique;
    
    LiteralElement() {
        this._literalElemParent = null;
        this._attributeElements = null;
        this._accessedPrefixes = null;
        this._allAttributesUnique = false;
    }
    
    public QName getName() {
        return this._qname;
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("LiteralElement name = " + this._name);
        this.displayContents(indent + 4);
    }
    
    private String accessedNamespace(final String prefix) {
        if (this._literalElemParent != null) {
            final String result = this._literalElemParent.accessedNamespace(prefix);
            if (result != null) {
                return result;
            }
        }
        return (this._accessedPrefixes != null) ? this._accessedPrefixes.get(prefix) : null;
    }
    
    public void registerNamespace(String prefix, final String uri, final SymbolTable stable, final boolean declared) {
        if (this._literalElemParent != null) {
            final String parentUri = this._literalElemParent.accessedNamespace(prefix);
            if (parentUri != null && parentUri.equals(uri)) {
                return;
            }
        }
        if (this._accessedPrefixes == null) {
            this._accessedPrefixes = new Hashtable<String, String>();
        }
        else if (!declared) {
            final String old = this._accessedPrefixes.get(prefix);
            if (old != null) {
                if (old.equals(uri)) {
                    return;
                }
                prefix = stable.generateNamespacePrefix();
            }
        }
        if (!prefix.equals("xml")) {
            this._accessedPrefixes.put(prefix, uri);
        }
    }
    
    private String translateQName(final QName qname, final SymbolTable stable) {
        final String localname = qname.getLocalPart();
        String prefix = qname.getPrefix();
        if (prefix == null) {
            prefix = "";
        }
        else if (prefix.equals("xmlns")) {
            return "xmlns";
        }
        final String alternative = stable.lookupPrefixAlias(prefix);
        if (alternative != null) {
            stable.excludeNamespaces(prefix);
            prefix = alternative;
        }
        final String uri = this.lookupNamespace(prefix);
        if (uri == null) {
            return localname;
        }
        this.registerNamespace(prefix, uri, stable, false);
        if (prefix != "") {
            return prefix + ":" + localname;
        }
        return localname;
    }
    
    public void addAttribute(final SyntaxTreeNode attribute) {
        if (this._attributeElements == null) {
            this._attributeElements = new ArrayList<SyntaxTreeNode>(2);
        }
        this._attributeElements.add(attribute);
    }
    
    public void setFirstAttribute(final SyntaxTreeNode attribute) {
        if (this._attributeElements == null) {
            this._attributeElements = new ArrayList<SyntaxTreeNode>(2);
        }
        this._attributeElements.add(0, attribute);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (this._attributeElements != null) {
            for (final SyntaxTreeNode node : this._attributeElements) {
                node.typeCheck(stable);
            }
        }
        this.typeCheckContents(stable);
        return Type.Void;
    }
    
    public Set<Map.Entry<String, String>> getNamespaceScope(SyntaxTreeNode node) {
        final Map<String, String> all = new HashMap<String, String>();
        while (node != null) {
            final Map<String, String> mapping = node.getPrefixMapping();
            if (mapping != null) {
                for (final String prefix : mapping.keySet()) {
                    if (!all.containsKey(prefix)) {
                        all.put(prefix, mapping.get(prefix));
                    }
                }
            }
            node = node.getParent();
        }
        return all.entrySet();
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final SymbolTable stable = parser.getSymbolTable();
        stable.setCurrentNode(this);
        final SyntaxTreeNode parent = this.getParent();
        if (parent != null && parent instanceof LiteralElement) {
            this._literalElemParent = (LiteralElement)parent;
        }
        this._name = this.translateQName(this._qname, stable);
        final int count = this._attributes.getLength();
        for (int i = 0; i < count; ++i) {
            final QName qname = parser.getQName(this._attributes.getQName(i));
            final String uri = qname.getNamespace();
            final String val = this._attributes.getValue(i);
            if (qname.equals(parser.getUseAttributeSets())) {
                if (!Util.isValidQNames(val)) {
                    final ErrorMsg err = new ErrorMsg("INVALID_QNAME_ERR", val, this);
                    parser.reportError(3, err);
                }
                this.setFirstAttribute(new UseAttributeSets(val, parser));
            }
            else if (qname.equals(parser.getExtensionElementPrefixes())) {
                stable.excludeNamespaces(val);
            }
            else if (qname.equals(parser.getExcludeResultPrefixes())) {
                stable.excludeNamespaces(val);
            }
            else {
                final String prefix = qname.getPrefix();
                if ((prefix == null || !prefix.equals("xmlns")) && (prefix != null || !qname.getLocalPart().equals("xmlns"))) {
                    if (uri == null || !uri.equals("http://www.w3.org/1999/XSL/Transform")) {
                        final String name = this.translateQName(qname, stable);
                        final LiteralAttribute attr = new LiteralAttribute(name, val, parser, this);
                        this.addAttribute(attr);
                        attr.setParent(this);
                        attr.parseContents(parser);
                    }
                }
            }
        }
        final Set<Map.Entry<String, String>> include = this.getNamespaceScope(this);
        for (final Map.Entry<String, String> entry : include) {
            final String prefix2 = entry.getKey();
            if (!prefix2.equals("xml")) {
                final String uri2 = this.lookupNamespace(prefix2);
                if (uri2 == null || stable.isExcludedNamespace(uri2)) {
                    continue;
                }
                this.registerNamespace(prefix2, uri2, stable, true);
            }
        }
        this.parseChildren(parser);
        for (int j = 0; j < count; ++j) {
            final QName qname2 = parser.getQName(this._attributes.getQName(j));
            final String val = this._attributes.getValue(j);
            if (qname2.equals(parser.getExtensionElementPrefixes())) {
                stable.unExcludeNamespaces(val);
            }
            else if (qname2.equals(parser.getExcludeResultPrefixes())) {
                stable.unExcludeNamespaces(val);
            }
        }
    }
    
    @Override
    protected boolean contextDependent() {
        return this.dependentContents();
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        this._allAttributesUnique = this.checkAttributesUnique();
        il.append(methodGen.loadHandler());
        il.append(new PUSH(cpg, this._name));
        il.append(LiteralElement.DUP2);
        il.append(methodGen.startElement());
        for (int j = 0; j < this.elementCount(); ++j) {
            final SyntaxTreeNode item = this.elementAt(j);
            if (item instanceof Variable) {
                item.translate(classGen, methodGen);
            }
        }
        if (this._accessedPrefixes != null) {
            for (final Map.Entry<String, String> entry : this._accessedPrefixes.entrySet()) {
                final String prefix = entry.getKey();
                final String uri = entry.getValue();
                il.append(methodGen.loadHandler());
                il.append(new PUSH(cpg, prefix));
                il.append(new PUSH(cpg, uri));
                il.append(methodGen.namespace());
            }
        }
        if (this._attributeElements != null) {
            for (final SyntaxTreeNode node : this._attributeElements) {
                if (!(node instanceof XslAttribute)) {
                    node.translate(classGen, methodGen);
                }
            }
        }
        this.translateContents(classGen, methodGen);
        il.append(methodGen.endElement());
    }
    
    private boolean isHTMLOutput() {
        return this.getStylesheet().getOutputMethod() == 2;
    }
    
    public ElemDesc getElemDesc() {
        if (this.isHTMLOutput()) {
            return ToHTMLStream.getElemDesc(this._name);
        }
        return null;
    }
    
    public boolean allAttributesUnique() {
        return this._allAttributesUnique;
    }
    
    private boolean checkAttributesUnique() {
        final boolean hasHiddenXslAttribute = this.canProduceAttributeNodes(this, true);
        if (hasHiddenXslAttribute) {
            return false;
        }
        if (this._attributeElements != null) {
            final int numAttrs = this._attributeElements.size();
            Map<String, SyntaxTreeNode> attrsTable = null;
            for (int i = 0; i < numAttrs; ++i) {
                final SyntaxTreeNode node = this._attributeElements.get(i);
                if (node instanceof UseAttributeSets) {
                    return false;
                }
                if (node instanceof XslAttribute) {
                    if (attrsTable == null) {
                        attrsTable = new HashMap<String, SyntaxTreeNode>();
                        for (int k = 0; k < i; ++k) {
                            final SyntaxTreeNode n = this._attributeElements.get(k);
                            if (n instanceof LiteralAttribute) {
                                final LiteralAttribute literalAttr = (LiteralAttribute)n;
                                attrsTable.put(literalAttr.getName(), literalAttr);
                            }
                        }
                    }
                    final XslAttribute xslAttr = (XslAttribute)node;
                    final AttributeValue attrName = xslAttr.getName();
                    if (attrName instanceof AttributeValueTemplate) {
                        return false;
                    }
                    if (attrName instanceof SimpleAttributeValue) {
                        final SimpleAttributeValue simpleAttr = (SimpleAttributeValue)attrName;
                        final String name = simpleAttr.toString();
                        if (name != null && attrsTable.get(name) != null) {
                            return false;
                        }
                        if (name != null) {
                            attrsTable.put(name, xslAttr);
                        }
                    }
                }
            }
        }
        return true;
    }
    
    private boolean canProduceAttributeNodes(final SyntaxTreeNode node, final boolean ignoreXslAttribute) {
        final List<SyntaxTreeNode> contents = node.getContents();
        for (final SyntaxTreeNode child : contents) {
            if (child instanceof Text) {
                final Text text = (Text)child;
                if (text.isIgnore()) {
                    continue;
                }
                return false;
            }
            else {
                if (child instanceof LiteralElement || child instanceof ValueOf || child instanceof XslElement || child instanceof Comment || child instanceof Number || child instanceof ProcessingInstruction) {
                    return false;
                }
                if (child instanceof XslAttribute) {
                    if (ignoreXslAttribute) {
                        continue;
                    }
                    return true;
                }
                else {
                    if (child instanceof CallTemplate || child instanceof ApplyTemplates || child instanceof Copy || child instanceof CopyOf) {
                        return true;
                    }
                    if ((child instanceof If || child instanceof ForEach) && this.canProduceAttributeNodes(child, false)) {
                        return true;
                    }
                    if (!(child instanceof Choose)) {
                        continue;
                    }
                    final List<SyntaxTreeNode> chooseContents = child.getContents();
                    for (final SyntaxTreeNode chooseChild : chooseContents) {
                        if ((chooseChild instanceof When || chooseChild instanceof Otherwise) && this.canProduceAttributeNodes(chooseChild, false)) {
                            return true;
                        }
                    }
                }
            }
        }
        return false;
    }
}
