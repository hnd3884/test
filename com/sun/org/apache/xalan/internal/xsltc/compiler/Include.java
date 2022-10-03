package com.sun.org.apache.xalan.internal.xsltc.compiler;

import com.sun.org.apache.xalan.internal.xsltc.compiler.util.MethodGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ClassGenerator;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.TypeCheckError;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type;
import java.util.Iterator;
import org.xml.sax.XMLReader;
import org.xml.sax.InputSource;
import com.sun.org.apache.xalan.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.utils.SystemIDResolver;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;

final class Include extends TopLevelElement
{
    private Stylesheet _included;
    
    Include() {
        this._included = null;
    }
    
    public Stylesheet getIncludedStylesheet() {
        return this._included;
    }
    
    @Override
    public void parseContents(final Parser parser) {
        final XSLTC xsltc = parser.getXSLTC();
        final Stylesheet context = parser.getCurrentStylesheet();
        String docToLoad = this.getAttribute("href");
        try {
            if (context.checkForLoop(docToLoad)) {
                final ErrorMsg msg = new ErrorMsg("CIRCULAR_INCLUDE_ERR", docToLoad, this);
                parser.reportError(2, msg);
                return;
            }
            InputSource input = null;
            XMLReader reader = null;
            final String currLoadedDoc = context.getSystemId();
            final SourceLoader loader = context.getSourceLoader();
            if (loader != null) {
                input = loader.loadSource(docToLoad, currLoadedDoc, xsltc);
                if (input != null) {
                    docToLoad = input.getSystemId();
                    reader = xsltc.getXMLReader();
                }
                else if (parser.errorsFound()) {
                    return;
                }
            }
            if (input == null) {
                docToLoad = SystemIDResolver.getAbsoluteURI(docToLoad, currLoadedDoc);
                final String accessError = SecuritySupport.checkAccess(docToLoad, (String)xsltc.getProperty("http://javax.xml.XMLConstants/property/accessExternalStylesheet"), "all");
                if (accessError != null) {
                    final ErrorMsg msg2 = new ErrorMsg("ACCESSING_XSLT_TARGET_ERR", SecuritySupport.sanitizePath(docToLoad), accessError, this);
                    parser.reportError(2, msg2);
                    return;
                }
                input = new InputSource(docToLoad);
            }
            if (input == null) {
                final ErrorMsg msg3 = new ErrorMsg("FILE_NOT_FOUND_ERR", docToLoad, this);
                parser.reportError(2, msg3);
                return;
            }
            SyntaxTreeNode root;
            if (reader != null) {
                root = parser.parse(reader, input);
            }
            else {
                root = parser.parse(input);
            }
            if (root == null) {
                return;
            }
            this._included = parser.makeStylesheet(root);
            if (this._included == null) {
                return;
            }
            this._included.setSourceLoader(loader);
            this._included.setSystemId(docToLoad);
            this._included.setParentStylesheet(context);
            this._included.setIncludingStylesheet(context);
            this._included.setTemplateInlining(context.getTemplateInlining());
            final int precedence = context.getImportPrecedence();
            this._included.setImportPrecedence(precedence);
            parser.setCurrentStylesheet(this._included);
            this._included.parseContents(parser);
            final Iterator<SyntaxTreeNode> elements = this._included.elements();
            final Stylesheet topStylesheet = parser.getTopLevelStylesheet();
            while (elements.hasNext()) {
                final SyntaxTreeNode element = elements.next();
                if (element instanceof TopLevelElement) {
                    if (element instanceof Variable) {
                        topStylesheet.addVariable((Variable)element);
                    }
                    else if (element instanceof Param) {
                        topStylesheet.addParam((Param)element);
                    }
                    else {
                        topStylesheet.addElement(element);
                    }
                }
            }
        }
        catch (final Exception e) {
            e.printStackTrace();
        }
        finally {
            parser.setCurrentStylesheet(context);
        }
    }
    
    @Override
    public Type typeCheck(final SymbolTable stable) throws TypeCheckError {
        return Type.Void;
    }
    
    @Override
    public void translate(final ClassGenerator classGen, final MethodGenerator methodGen) {
    }
}
