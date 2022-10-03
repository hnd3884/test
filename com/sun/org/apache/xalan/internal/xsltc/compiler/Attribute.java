package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Util;

final class Attribute extends Instruction
{
    private QName _name;
    
    @Override
    public void display(final int indent) {
        this.indent(indent);
        Util.println("Attribute " + this._name);
        this.displayContents(indent + 4);
    }
    
    @Override
    public void parseContents(final Parser parser) {
        this._name = parser.getQName(this.getAttribute("name"));
        this.parseChildren(parser);
    }
}
