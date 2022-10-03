package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xml.internal.serializer.ElemDesc;
import com.sun.org.apache.bcel.internal.generic.LocalVariableGen;
import com.sun.org.apache.bcel.internal.generic.InstructionList;
import com.sun.org.apache.bcel.internal.generic.ConstantPoolGen;
import com.sun.org.apache.bcel.internal.generic.INVOKEVIRTUAL;
import com.sun.org.apache.bcel.internal.generic.GETFIELD;
import com.sun.org.apache.bcel.internal.generic.INVOKESTATIC;
import com.sun.org.apache.bcel.internal.generic.ALOAD;
import com.sun.org.apache.bcel.internal.generic.ASTORE;
import com.sun.org.apache.bcel.internal.generic.InstructionHandle;
import com.sun.org.apache.bcel.internal.generic.CompoundInstruction;
import com.sun.org.apache.bcel.internal.generic.PUSH;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.List;
import com.sun.org.apache.xml.internal.utils.XML11Char;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class XslAttribute extends Instruction
{
    private String _prefix;
    private AttributeValue _name;
    private AttributeValueTemplate _namespace;
    private boolean _ignore;
    private boolean _isLiteral;
    
    XslAttribute() {
        this._namespace = null;
        this._ignore = false;
        this._isLiteral = false;
    }
    
    public AttributeValue getName() {
        return this._name;
    }
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("Attribute " + this._name);
        this.displayContents(indent + 4);
    }
    
    @Override
    public void parseContents(final Parser parser) {
        boolean generated = false;
        final SymbolTable stable = parser.getSymbolTable();
        String name = this.getAttribute("name");
        String namespace = this.getAttribute("namespace");
        final QName qname = parser.getQName(name, false);
        final String prefix = qname.getPrefix();
        if ((prefix != null && prefix.equals("xmlns")) || name.equals("xmlns")) {
            this.reportError(this, parser, "ILLEGAL_ATTR_NAME_ERR", name);
            return;
        }
        this._isLiteral = Util.isLiteral(name);
        if (this._isLiteral && !XML11Char.isXML11ValidQName(name)) {
            this.reportError(this, parser, "ILLEGAL_ATTR_NAME_ERR", name);
            return;
        }
        final SyntaxTreeNode parent = this.getParent();
        final List<SyntaxTreeNode> siblings = parent.getContents();
        for (int i = 0; i < parent.elementCount(); ++i) {
            final SyntaxTreeNode item = siblings.get(i);
            if (item == this) {
                break;
            }
            if (!(item instanceof XslAttribute)) {
                if (!(item instanceof UseAttributeSets)) {
                    if (!(item instanceof LiteralAttribute)) {
                        if (!(item instanceof Text)) {
                            if (!(item instanceof If)) {
                                if (!(item instanceof Choose)) {
                                    if (!(item instanceof CopyOf)) {
                                        if (!(item instanceof VariableBase)) {
                                            this.reportWarning(this, parser, "STRAY_ATTRIBUTE_ERR", name);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        if (namespace != null && namespace != "") {
            this._prefix = this.lookupPrefix(namespace);
            this._namespace = new AttributeValueTemplate(namespace, parser, this);
        }
        else if (prefix != null && prefix != "") {
            this._prefix = prefix;
            namespace = this.lookupNamespace(prefix);
            if (namespace != null) {
                this._namespace = new AttributeValueTemplate(namespace, parser, this);
            }
        }
        if (this._namespace != null) {
            if (this._prefix == null || this._prefix == "") {
                if (prefix != null) {
                    this._prefix = prefix;
                }
                else {
                    this._prefix = stable.generateNamespacePrefix();
                    generated = true;
                }
            }
            else if (prefix != null && !prefix.equals(this._prefix)) {
                this._prefix = prefix;
            }
            name = this._prefix + ":" + qname.getLocalPart();
            if (parent instanceof LiteralElement && !generated) {
                ((LiteralElement)parent).registerNamespace(this._prefix, namespace, stable, false);
            }
        }
        if (parent instanceof LiteralElement) {
            ((LiteralElement)parent).addAttribute(this);
        }
        this._name = AttributeValue.create(this, name, parser);
        this.parseChildren(parser);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        if (!this._ignore) {
            this._name.typeCheck(stable);
            if (this._namespace != null) {
                this._namespace.typeCheck(stable);
            }
            this.typeCheckContents(stable);
        }
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
        final ConstantPoolGen cpg = classGen.getConstantPool();
        final InstructionList il = methodGen.getInstructionList();
        if (this._ignore) {
            return;
        }
        this._ignore = true;
        if (this._namespace != null) {
            il.append(methodGen.loadHandler());
            il.append(new PUSH(cpg, this._prefix));
            this._namespace.translate(classGen, methodGen);
            il.append(methodGen.namespace());
        }
        if (!this._isLiteral) {
            final LocalVariableGen nameValue = methodGen.addLocalVariable2("nameValue", Util.getJCRefType("Ljava/lang/String;"), null);
            this._name.translate(classGen, methodGen);
            nameValue.setStart(il.append(new ASTORE(nameValue.getIndex())));
            il.append(new ALOAD(nameValue.getIndex()));
            final int check = cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.BasisLibrary", "checkAttribQName", "(Ljava/lang/String;)V");
            il.append(new INVOKESTATIC(check));
            il.append(methodGen.loadHandler());
            il.append(XslAttribute.DUP);
            nameValue.setEnd(il.append(new ALOAD(nameValue.getIndex())));
        }
        else {
            il.append(methodGen.loadHandler());
            il.append(XslAttribute.DUP);
            this._name.translate(classGen, methodGen);
        }
        if (this.elementCount() == 1 && this.elementAt(0) instanceof Text) {
            il.append(new PUSH(cpg, ((Text)this.elementAt(0)).getText()));
        }
        else {
            il.append(classGen.loadTranslet());
            il.append(new GETFIELD(cpg.addFieldref("com.sun.org.apache.xalan.internal.xsltc.runtime.AbstractTranslet", "stringValueHandler", "Lcom/sun/org/apache/xalan/internal/xsltc/runtime/StringValueHandler;")));
            il.append(XslAttribute.DUP);
            il.append(methodGen.storeHandler());
            this.translateContents(classGen, methodGen);
            il.append(new INVOKEVIRTUAL(cpg.addMethodref("com.sun.org.apache.xalan.internal.xsltc.runtime.StringValueHandler", "getValue", "()Ljava/lang/String;")));
        }
        final SyntaxTreeNode parent = this.getParent();
        if (parent instanceof LiteralElement && ((LiteralElement)parent).allAttributesUnique()) {
            int flags = 0;
            final ElemDesc elemDesc = ((LiteralElement)parent).getElemDesc();
            if (elemDesc != null && this._name instanceof SimpleAttributeValue) {
                final String attrName = ((SimpleAttributeValue)this._name).toString();
                if (elemDesc.isAttrFlagSet(attrName, 4)) {
                    flags |= 0x2;
                }
                else if (elemDesc.isAttrFlagSet(attrName, 2)) {
                    flags |= 0x4;
                }
            }
            il.append(new PUSH(cpg, flags));
            il.append(methodGen.uniqueAttribute());
        }
        else {
            il.append(methodGen.attribute());
        }
        il.append(methodGen.storeHandler());
    }
}
