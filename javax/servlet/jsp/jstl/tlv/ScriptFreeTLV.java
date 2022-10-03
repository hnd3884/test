package javax.servlet.jsp.jstl.tlv;

import org.xml.sax.Attributes;
import java.io.IOException;
import org.xml.sax.SAXException;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.helpers.DefaultHandler;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.PageData;
import java.util.Map;
import javax.servlet.jsp.tagext.TagLibraryValidator;

public class ScriptFreeTLV extends TagLibraryValidator
{
    private static final PageParser parser;
    private boolean allowDeclarations;
    private boolean allowScriptlets;
    private boolean allowExpressions;
    private boolean allowRTExpressions;
    
    public ScriptFreeTLV() {
        this.allowDeclarations = false;
        this.allowScriptlets = false;
        this.allowExpressions = false;
        this.allowRTExpressions = false;
    }
    
    public void setInitParameters(final Map<String, Object> initParms) {
        super.setInitParameters((Map)initParms);
        final String declarationsParm = initParms.get("allowDeclarations");
        final String scriptletsParm = initParms.get("allowScriptlets");
        final String expressionsParm = initParms.get("allowExpressions");
        final String rtExpressionsParm = initParms.get("allowRTExpressions");
        this.allowDeclarations = "true".equalsIgnoreCase(declarationsParm);
        this.allowScriptlets = "true".equalsIgnoreCase(scriptletsParm);
        this.allowExpressions = "true".equalsIgnoreCase(expressionsParm);
        this.allowRTExpressions = "true".equalsIgnoreCase(rtExpressionsParm);
    }
    
    public ValidationMessage[] validate(final String prefix, final String uri, final PageData page) {
        try {
            final MyContentHandler handler = new MyContentHandler();
            ScriptFreeTLV.parser.parse(page, handler);
            return handler.reportResults();
        }
        catch (final ParserConfigurationException e) {
            return vmFromString(e.toString());
        }
        catch (final SAXException e2) {
            return vmFromString(e2.toString());
        }
        catch (final IOException e3) {
            return vmFromString(e3.toString());
        }
    }
    
    private static ValidationMessage[] vmFromString(final String message) {
        return new ValidationMessage[] { new ValidationMessage((String)null, message) };
    }
    
    static {
        parser = new PageParser(true);
    }
    
    private class MyContentHandler extends DefaultHandler
    {
        private int declarationCount;
        private int scriptletCount;
        private int expressionCount;
        private int rtExpressionCount;
        
        private MyContentHandler() {
            this.declarationCount = 0;
            this.scriptletCount = 0;
            this.expressionCount = 0;
            this.rtExpressionCount = 0;
        }
        
        @Override
        public void startElement(final String namespaceUri, final String localName, final String qualifiedName, final Attributes atts) {
            if (!ScriptFreeTLV.this.allowDeclarations && qualifiedName.equals("jsp:declaration")) {
                ++this.declarationCount;
            }
            else if (!ScriptFreeTLV.this.allowScriptlets && qualifiedName.equals("jsp:scriptlet")) {
                ++this.scriptletCount;
            }
            else if (!ScriptFreeTLV.this.allowExpressions && qualifiedName.equals("jsp:expression")) {
                ++this.expressionCount;
            }
            if (!ScriptFreeTLV.this.allowRTExpressions) {
                this.countRTExpressions(atts);
            }
        }
        
        private void countRTExpressions(final Attributes atts) {
            for (int stop = atts.getLength(), i = 0; i < stop; ++i) {
                final String attval = atts.getValue(i);
                if (attval.startsWith("%=") && attval.endsWith("%")) {
                    ++this.rtExpressionCount;
                }
            }
        }
        
        public ValidationMessage[] reportResults() {
            if (this.declarationCount + this.scriptletCount + this.expressionCount + this.rtExpressionCount > 0) {
                final StringBuilder results = new StringBuilder("JSP page contains ");
                boolean first = true;
                if (this.declarationCount > 0) {
                    results.append(Integer.toString(this.declarationCount));
                    results.append(" declaration");
                    if (this.declarationCount > 1) {
                        results.append('s');
                    }
                    first = false;
                }
                if (this.scriptletCount > 0) {
                    if (!first) {
                        results.append(", ");
                    }
                    results.append(Integer.toString(this.scriptletCount));
                    results.append(" scriptlet");
                    if (this.scriptletCount > 1) {
                        results.append('s');
                    }
                    first = false;
                }
                if (this.expressionCount > 0) {
                    if (!first) {
                        results.append(", ");
                    }
                    results.append(Integer.toString(this.expressionCount));
                    results.append(" expression");
                    if (this.expressionCount > 1) {
                        results.append('s');
                    }
                    first = false;
                }
                if (this.rtExpressionCount > 0) {
                    if (!first) {
                        results.append(", ");
                    }
                    results.append(Integer.toString(this.rtExpressionCount));
                    results.append(" request-time attribute value");
                    if (this.rtExpressionCount > 1) {
                        results.append('s');
                    }
                    first = false;
                }
                results.append(".");
                return vmFromString(results.toString());
            }
            return null;
        }
    }
}
