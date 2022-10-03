package org.apache.taglibs.standard.tag.common.xml;

import org.apache.taglibs.standard.tag.common.core.Util;
import javax.xml.transform.dom.DOMSource;
import java.util.List;
import javax.servlet.jsp.PageContext;
import org.w3c.dom.Document;
import javax.xml.transform.TransformerException;
import javax.xml.transform.stream.StreamResult;
import java.io.Writer;
import org.apache.taglibs.standard.util.UnclosableWriter;
import org.w3c.dom.Node;
import javax.xml.transform.dom.DOMResult;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.URIResolver;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import javax.servlet.jsp.JspException;
import java.io.Reader;
import java.io.StringReader;
import javax.xml.transform.Source;
import javax.servlet.jsp.JspTagException;
import org.apache.taglibs.standard.resources.Resources;
import org.apache.taglibs.standard.util.XmlUtil;
import javax.xml.transform.Transformer;
import javax.xml.transform.Result;
import javax.servlet.jsp.tagext.BodyTagSupport;

public abstract class TransformSupport extends BodyTagSupport
{
    protected Object xml;
    protected boolean xmlSpecified;
    protected String xmlSystemId;
    protected Object xslt;
    protected String xsltSystemId;
    protected Result result;
    private String var;
    private int scope;
    private Transformer t;
    private XmlUtil.JstlEntityResolver entityResolver;
    private XmlUtil.JstlUriResolver uriResolver;
    
    public TransformSupport() {
        this.init();
    }
    
    private void init() {
        final Object o = null;
        this.xslt = o;
        this.xml = o;
        this.xmlSpecified = false;
        final String s = null;
        this.xsltSystemId = s;
        this.xmlSystemId = s;
        this.var = null;
        this.result = null;
        this.scope = 1;
    }
    
    public int doStartTag() throws JspException {
        if (this.xslt == null) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_XSLT_IS_NULL"));
        }
        Source source;
        try {
            if (this.xslt instanceof Source) {
                source = (Source)this.xslt;
            }
            else if (this.xslt instanceof String) {
                String s = (String)this.xslt;
                s = s.trim();
                if (s.length() == 0) {
                    throw new JspTagException(Resources.getMessage("TRANSFORM_XSLT_IS_EMPTY"));
                }
                source = XmlUtil.newSAXSource(new StringReader(s), this.xsltSystemId, this.entityResolver);
            }
            else {
                if (!(this.xslt instanceof Reader)) {
                    throw new JspTagException(Resources.getMessage("TRANSFORM_XSLT_UNSUPPORTED_TYPE", this.xslt.getClass()));
                }
                source = XmlUtil.newSAXSource((Reader)this.xslt, this.xsltSystemId, this.entityResolver);
            }
        }
        catch (final SAXException e) {
            throw new JspException((Throwable)e);
        }
        catch (final ParserConfigurationException e2) {
            throw new JspException((Throwable)e2);
        }
        try {
            (this.t = XmlUtil.newTransformer(source)).setURIResolver(this.uriResolver);
        }
        catch (final TransformerConfigurationException e3) {
            throw new JspTagException((Throwable)e3);
        }
        return 2;
    }
    
    public int doEndTag() throws JspException {
        try {
            final Source source = this.xmlSpecified ? this.getSourceFromXmlAttribute() : this.getSourceFromBodyContent();
            if (this.var != null) {
                final Document d = XmlUtil.newEmptyDocument();
                final Result doc = new DOMResult(d);
                this.t.transform(source, doc);
                this.pageContext.setAttribute(this.var, (Object)d, this.scope);
            }
            else {
                Result out = this.result;
                if (out == null) {
                    out = new StreamResult(new UnclosableWriter((Writer)this.pageContext.getOut()));
                }
                this.t.transform(source, out);
            }
            return 6;
        }
        catch (final TransformerException ex) {
            throw new JspException((Throwable)ex);
        }
        catch (final SAXException e) {
            throw new JspException((Throwable)e);
        }
        catch (final ParserConfigurationException e2) {
            throw new JspException((Throwable)e2);
        }
        finally {
            this.t = null;
        }
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    public void setPageContext(final PageContext pageContext) {
        super.setPageContext(pageContext);
        this.uriResolver = ((pageContext == null) ? null : new XmlUtil.JstlUriResolver(pageContext));
        this.entityResolver = ((pageContext == null) ? null : new XmlUtil.JstlEntityResolver(pageContext));
    }
    
    public void addParameter(final String name, final Object value) {
        this.t.setParameter(name, value);
    }
    
    Source getSourceFromXmlAttribute() throws JspTagException, SAXException, ParserConfigurationException {
        Object xml = this.xml;
        if (xml == null) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_XML_IS_NULL"));
        }
        if (xml instanceof List) {
            final List<?> list = (List<?>)xml;
            if (list.size() != 1) {
                throw new JspTagException(Resources.getMessage("TRANSFORM_XML_LIST_SIZE"));
            }
            xml = list.get(0);
        }
        if (xml instanceof Source) {
            return (Source)xml;
        }
        if (xml instanceof String) {
            String s = (String)xml;
            s = s.trim();
            if (s.length() == 0) {
                throw new JspTagException(Resources.getMessage("TRANSFORM_XML_IS_EMPTY"));
            }
            return XmlUtil.newSAXSource(new StringReader(s), this.xmlSystemId, this.entityResolver);
        }
        else {
            if (xml instanceof Reader) {
                return XmlUtil.newSAXSource((Reader)xml, this.xmlSystemId, this.entityResolver);
            }
            if (xml instanceof Node) {
                return new DOMSource((Node)xml, this.xmlSystemId);
            }
            throw new JspTagException(Resources.getMessage("TRANSFORM_XML_UNSUPPORTED_TYPE", xml.getClass()));
        }
    }
    
    Source getSourceFromBodyContent() throws JspTagException, SAXException, ParserConfigurationException {
        if (this.bodyContent == null) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_BODY_IS_NULL"));
        }
        String s = this.bodyContent.getString();
        if (s == null) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_BODY_CONTENT_IS_NULL"));
        }
        s = s.trim();
        if (s.length() == 0) {
            throw new JspTagException(Resources.getMessage("TRANSFORM_BODY_IS_EMPTY"));
        }
        return XmlUtil.newSAXSource(new StringReader(s), this.xmlSystemId, this.entityResolver);
    }
    
    public void setVar(final String var) {
        this.var = var;
    }
    
    public void setScope(final String scope) {
        this.scope = Util.getScope(scope);
    }
}
