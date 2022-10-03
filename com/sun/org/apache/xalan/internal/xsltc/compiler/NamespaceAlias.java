package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;

final class NamespaceAlias extends TopLevelElement
{
    private String sPrefix;
    private String rPrefix;
    
    @Override
    public void parseContents(final Parser parser) {
        this.sPrefix = this.getAttribute("stylesheet-prefix");
        this.rPrefix = this.getAttribute("result-prefix");
        parser.getSymbolTable().addPrefixAlias(this.sPrefix, this.rPrefix);
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
    }
}
