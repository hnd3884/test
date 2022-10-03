package org.apache.taglibs.standard.tlv;

import java.util.Set;
import java.util.HashSet;
import java.util.StringTokenizer;
import java.util.HashMap;
import javax.servlet.jsp.tagext.TagData;
import org.xml.sax.Attributes;
import java.io.InputStream;
import org.xml.sax.XMLReader;
import javax.xml.parsers.ParserConfigurationException;
import org.xml.sax.SAXException;
import java.io.IOException;
import org.xml.sax.InputSource;
import org.xml.sax.ContentHandler;
import org.apache.taglibs.standard.util.XmlUtil;
import java.util.NoSuchElementException;
import org.apache.taglibs.standard.resources.Resources;
import javax.servlet.jsp.tagext.ValidationMessage;
import javax.servlet.jsp.tagext.PageData;
import org.xml.sax.helpers.DefaultHandler;
import java.util.Map;
import java.util.Vector;
import javax.servlet.jsp.tagext.TagLibraryValidator;

public abstract class JstlBaseTLV extends TagLibraryValidator
{
    private static final String EXP_ATT_PARAM = "expressionAttributes";
    protected static final String VAR = "var";
    protected static final String SCOPE = "scope";
    protected static final String PAGE_SCOPE = "page";
    protected static final String REQUEST_SCOPE = "request";
    protected static final String SESSION_SCOPE = "session";
    protected static final String APPLICATION_SCOPE = "application";
    protected final String JSP = "http://java.sun.com/JSP/Page";
    private static final int TYPE_UNDEFINED = 0;
    protected static final int TYPE_CORE = 1;
    protected static final int TYPE_FMT = 2;
    protected static final int TYPE_SQL = 3;
    protected static final int TYPE_XML = 4;
    private int tlvType;
    protected String uri;
    protected String prefix;
    protected Vector messageVector;
    protected Map config;
    protected boolean failed;
    protected String lastElementId;
    
    protected abstract DefaultHandler getHandler();
    
    public JstlBaseTLV() {
        this.tlvType = 0;
        this.init();
    }
    
    private void init() {
        this.messageVector = null;
        this.prefix = null;
        this.config = null;
    }
    
    public void release() {
        super.release();
        this.init();
    }
    
    public synchronized ValidationMessage[] validate(final int type, final String prefix, final String uri, final PageData page) {
        try {
            this.tlvType = type;
            this.uri = uri;
            this.messageVector = new Vector();
            this.prefix = prefix;
            try {
                if (this.config == null) {
                    this.configure(this.getInitParameters().get("expressionAttributes"));
                }
            }
            catch (final NoSuchElementException ex) {
                return vmFromString(Resources.getMessage("TLV_PARAMETER_ERROR", "expressionAttributes"));
            }
            final DefaultHandler h = this.getHandler();
            final XMLReader xmlReader = XmlUtil.newXMLReader(null);
            xmlReader.setContentHandler(h);
            final InputStream inputStream = page.getInputStream();
            try {
                xmlReader.parse(new InputSource(inputStream));
            }
            finally {
                try {
                    inputStream.close();
                }
                catch (final IOException ex5) {}
            }
            if (this.messageVector.size() == 0) {
                return null;
            }
            return vmFromVector(this.messageVector);
        }
        catch (final SAXException ex2) {
            return vmFromString(ex2.toString());
        }
        catch (final IOException ex3) {
            return vmFromString(ex3.toString());
        }
        catch (final ParserConfigurationException ex4) {
            return vmFromString(ex4.toString());
        }
    }
    
    @Deprecated
    protected String validateExpression(final String elem, final String att, final String expr) {
        return null;
    }
    
    protected boolean isTag(final String tagUri, final String tagLn, final String matchUri, final String matchLn) {
        if (tagUri == null || tagUri.length() == 0 || tagLn == null || matchUri == null || matchLn == null) {
            return false;
        }
        if (tagUri.length() > matchUri.length()) {
            return tagUri.startsWith(matchUri) && tagLn.equals(matchLn);
        }
        return matchUri.startsWith(tagUri) && tagLn.equals(matchLn);
    }
    
    protected boolean isJspTag(final String tagUri, final String tagLn, final String target) {
        return this.isTag(tagUri, tagLn, "http://java.sun.com/JSP/Page", target);
    }
    
    private boolean isTag(final int type, final String tagUri, final String tagLn, final String target) {
        return this.tlvType == type && this.isTag(tagUri, tagLn, this.uri, target);
    }
    
    protected boolean isCoreTag(final String tagUri, final String tagLn, final String target) {
        return this.isTag(1, tagUri, tagLn, target);
    }
    
    protected boolean isFmtTag(final String tagUri, final String tagLn, final String target) {
        return this.isTag(2, tagUri, tagLn, target);
    }
    
    protected boolean isSqlTag(final String tagUri, final String tagLn, final String target) {
        return this.isTag(3, tagUri, tagLn, target);
    }
    
    protected boolean isXmlTag(final String tagUri, final String tagLn, final String target) {
        return this.isTag(4, tagUri, tagLn, target);
    }
    
    protected boolean hasAttribute(final Attributes a, final String att) {
        return a.getValue(att) != null;
    }
    
    protected void fail(final String message) {
        this.failed = true;
        this.messageVector.add(new ValidationMessage(this.lastElementId, message));
    }
    
    protected boolean isSpecified(final TagData data, final String attributeName) {
        return data.getAttribute(attributeName) != null;
    }
    
    protected boolean hasNoInvalidScope(final Attributes a) {
        final String scope = a.getValue("scope");
        return scope == null || scope.equals("page") || scope.equals("request") || scope.equals("session") || scope.equals("application");
    }
    
    protected boolean hasEmptyVar(final Attributes a) {
        return "".equals(a.getValue("var"));
    }
    
    protected boolean hasDanglingScope(final Attributes a) {
        return a.getValue("scope") != null && a.getValue("var") == null;
    }
    
    protected String getLocalPart(final String qname) {
        final int colon = qname.indexOf(":");
        if (colon == -1) {
            return qname;
        }
        return qname.substring(colon + 1);
    }
    
    private void configure(final String info) {
        this.config = new HashMap();
        if (info == null) {
            return;
        }
        final StringTokenizer st = new StringTokenizer(info);
        while (st.hasMoreTokens()) {
            final String pair = st.nextToken();
            final StringTokenizer pairTokens = new StringTokenizer(pair, ":");
            final String element = pairTokens.nextToken();
            final String attribute = pairTokens.nextToken();
            Object atts = this.config.get(element);
            if (atts == null) {
                atts = new HashSet();
                this.config.put(element, atts);
            }
            ((Set)atts).add(attribute);
        }
    }
    
    static ValidationMessage[] vmFromString(final String message) {
        return new ValidationMessage[] { new ValidationMessage((String)null, message) };
    }
    
    static ValidationMessage[] vmFromVector(final Vector v) {
        final ValidationMessage[] vm = new ValidationMessage[v.size()];
        for (int i = 0; i < vm.length; ++i) {
            vm[i] = v.get(i);
        }
        return vm;
    }
}
