package javax.xml.transform.sax;

import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.ContentHandler;
import javax.xml.transform.Result;

public class SAXResult implements Result
{
    public static final String FEATURE = "http://javax.xml.transform.sax.SAXResult/feature";
    private ContentHandler handler;
    private LexicalHandler lexhandler;
    private String systemId;
    
    public SAXResult() {
    }
    
    public SAXResult(final ContentHandler handler) {
        this.setHandler(handler);
    }
    
    public void setHandler(final ContentHandler handler) {
        this.handler = handler;
    }
    
    public ContentHandler getHandler() {
        return this.handler;
    }
    
    public void setLexicalHandler(final LexicalHandler lexhandler) {
        this.lexhandler = lexhandler;
    }
    
    public LexicalHandler getLexicalHandler() {
        return this.lexhandler;
    }
    
    public void setSystemId(final String systemId) {
        this.systemId = systemId;
    }
    
    public String getSystemId() {
        return this.systemId;
    }
}
