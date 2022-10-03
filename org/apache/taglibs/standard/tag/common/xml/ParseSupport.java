package org.apache.taglibs.standard.tag.common.xml;

import org.apache.taglibs.standard.tag.common.core.Util;
import javax.servlet.jsp.PageContext;
import javax.xml.parsers.DocumentBuilder;
import org.xml.sax.EntityResolver;
import javax.xml.transform.sax.TransformerHandler;
import org.xml.sax.XMLReader;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.TransformerConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.ContentHandler;
import javax.xml.transform.Result;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMResult;
import javax.servlet.jsp.JspException;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import java.io.Reader;
import java.io.StringReader;
import org.apache.taglibs.standard.util.XmlUtil;
import org.xml.sax.XMLFilter;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class ParseSupport extends BodyTagSupport
{
    protected Object xml;
    protected String systemId;
    protected XMLFilter filter;
    private String var;
    private String varDom;
    private int scope;
    private int scopeDom;
    private XmlUtil.JstlEntityResolver entityResolver;
    
    public ParseSupport() {
        this.init();
    }
    
    private void init() {
        final String s = null;
        this.varDom = s;
        this.var = s;
        this.xml = null;
        this.systemId = null;
        this.filter = null;
        this.scope = 1;
        this.scopeDom = 1;
    }
    
    public int doEndTag() throws JspException {
        Object xmlText = this.xml;
        if (xmlText == null) {
            if (this.bodyContent != null && this.bodyContent.getString() != null) {
                xmlText = this.bodyContent.getString().trim();
            }
            else {
                xmlText = "";
            }
        }
        if (xmlText instanceof String) {
            xmlText = new StringReader((String)xmlText);
        }
        if (!(xmlText instanceof Reader)) {
            throw new JspTagException(Resources.getMessage("PARSE_INVALID_SOURCE"));
        }
        final InputSource source = XmlUtil.newInputSource((Reader)xmlText, this.systemId);
        Document d;
        if (this.filter != null) {
            d = this.parseInputSourceWithFilter(source, this.filter);
        }
        else {
            d = this.parseInputSource(source);
        }
        if (this.var != null) {
            this.pageContext.setAttribute(this.var, (Object)d, this.scope);
        }
        if (this.varDom != null) {
            this.pageContext.setAttribute(this.varDom, (Object)d, this.scopeDom);
        }
        return 6;
    }
    
    public void release() {
        this.init();
    }
    
    private Document parseInputSourceWithFilter(final InputSource s, final XMLFilter f) throws JspException {
        try {
            final XMLReader xr = XmlUtil.newXMLReader(this.entityResolver);
            f.setParent(xr);
            final TransformerHandler th = XmlUtil.newTransformerHandler();
            final Document o = XmlUtil.newEmptyDocument();
            th.setResult(new DOMResult(o));
            f.setContentHandler(th);
            f.parse(s);
            return o;
        }
        catch (final IOException e) {
            throw new JspException((Throwable)e);
        }
        catch (final SAXException e2) {
            throw new JspException((Throwable)e2);
        }
        catch (final TransformerConfigurationException e3) {
            throw new JspException((Throwable)e3);
        }
        catch (final ParserConfigurationException e4) {
            throw new JspException((Throwable)e4);
        }
    }
    
    private Document parseInputSource(final InputSource s) throws JspException {
        try {
            final DocumentBuilder db = XmlUtil.newDocumentBuilder();
            db.setEntityResolver(this.entityResolver);
            return db.parse(s);
        }
        catch (final SAXException e) {
            throw new JspException((Throwable)e);
        }
        catch (final IOException e2) {
            throw new JspException((Throwable)e2);
        }
    }
    
    public void setPageContext(final PageContext pageContext) {
        super.setPageContext(pageContext);
        this.entityResolver = ((pageContext == null) ? null : new XmlUtil.JstlEntityResolver(pageContext));
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setVarDom(final String varDom) {
        this.varDom = varDom;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
    
    public void setScopeDom(final String scopeDom) {
        this.scopeDom = Util.getScope(scopeDom);
    }
}
