package com.sun.org.apache.xml.internal.utils;

import org.xml.sax.Attributes;
import javax.xml.transform.TransformerException;
import org.xml.sax.SAXException;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import java.util.StringTokenizer;
import javax.xml.transform.Source;
import javax.xml.transform.URIResolver;
import java.util.Vector;
import org.xml.sax.helpers.DefaultHandler;

public class StylesheetPIHandler extends DefaultHandler
{
    String m_baseID;
    String m_media;
    String m_title;
    String m_charset;
    Vector m_stylesheets;
    URIResolver m_uriResolver;
    
    public void setURIResolver(final URIResolver resolver) {
        this.m_uriResolver = resolver;
    }
    
    public URIResolver getURIResolver() {
        return this.m_uriResolver;
    }
    
    public StylesheetPIHandler(final String baseID, final String media, final String title, final String charset) {
        this.m_stylesheets = new Vector();
        this.m_baseID = baseID;
        this.m_media = media;
        this.m_title = title;
        this.m_charset = charset;
    }
    
    public Source getAssociatedStylesheet() {
        final int sz = this.m_stylesheets.size();
        if (sz > 0) {
            final Source source = this.m_stylesheets.elementAt(sz - 1);
            return source;
        }
        return null;
    }
    
    @Override
    public void processingInstruction(final String target, final String data) throws SAXException {
        if (target.equals("xml-stylesheet")) {
            String href = null;
            String type = null;
            String title = null;
            String media = null;
            String charset = null;
            boolean alternate = false;
            final StringTokenizer tokenizer = new StringTokenizer(data, " \t=\n", true);
            boolean lookedAhead = false;
            Source source = null;
            String token = "";
            while (tokenizer.hasMoreTokens()) {
                if (!lookedAhead) {
                    token = tokenizer.nextToken();
                }
                else {
                    lookedAhead = false;
                }
                if (tokenizer.hasMoreTokens()) {
                    if (token.equals(" ") || token.equals("\t")) {
                        continue;
                    }
                    if (token.equals("=")) {
                        continue;
                    }
                }
                final String name = token;
                if (name.equals("type")) {
                    for (token = tokenizer.nextToken(); tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("=")); token = tokenizer.nextToken()) {}
                    type = token.substring(1, token.length() - 1);
                }
                else {
                    if (name.equals("href")) {
                        for (token = tokenizer.nextToken(); tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("=")); token = tokenizer.nextToken()) {}
                        href = token;
                        if (tokenizer.hasMoreTokens()) {
                            for (token = tokenizer.nextToken(); token.equals("=") && tokenizer.hasMoreTokens(); token = tokenizer.nextToken(), lookedAhead = true) {
                                href = href + token + tokenizer.nextToken();
                                if (!tokenizer.hasMoreTokens()) {
                                    break;
                                }
                            }
                        }
                        href = href.substring(1, href.length() - 1);
                        try {
                            if (this.m_uriResolver != null) {
                                source = this.m_uriResolver.resolve(href, this.m_baseID);
                            }
                            else {
                                href = SystemIDResolver.getAbsoluteURI(href, this.m_baseID);
                                source = new SAXSource(new InputSource(href));
                            }
                            continue;
                        }
                        catch (final TransformerException te) {
                            throw new SAXException(te);
                        }
                    }
                    if (name.equals("title")) {
                        for (token = tokenizer.nextToken(); tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("=")); token = tokenizer.nextToken()) {}
                        title = token.substring(1, token.length() - 1);
                    }
                    else if (name.equals("media")) {
                        for (token = tokenizer.nextToken(); tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("=")); token = tokenizer.nextToken()) {}
                        media = token.substring(1, token.length() - 1);
                    }
                    else if (name.equals("charset")) {
                        for (token = tokenizer.nextToken(); tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("=")); token = tokenizer.nextToken()) {}
                        charset = token.substring(1, token.length() - 1);
                    }
                    else {
                        if (!name.equals("alternate")) {
                            continue;
                        }
                        for (token = tokenizer.nextToken(); tokenizer.hasMoreTokens() && (token.equals(" ") || token.equals("\t") || token.equals("=")); token = tokenizer.nextToken()) {}
                        alternate = token.substring(1, token.length() - 1).equals("yes");
                    }
                }
            }
            if (null != type && (type.equals("text/xsl") || type.equals("text/xml") || type.equals("application/xml+xslt")) && null != href) {
                if (null != this.m_media) {
                    if (null == media) {
                        return;
                    }
                    if (!media.equals(this.m_media)) {
                        return;
                    }
                }
                if (null != this.m_charset) {
                    if (null == charset) {
                        return;
                    }
                    if (!charset.equals(this.m_charset)) {
                        return;
                    }
                }
                if (null != this.m_title) {
                    if (null == title) {
                        return;
                    }
                    if (!title.equals(this.m_title)) {
                        return;
                    }
                }
                this.m_stylesheets.addElement(source);
            }
        }
    }
    
    @Override
    public void startElement(final String namespaceURI, final String localName, final String qName, final Attributes atts) throws SAXException {
        throw new StopParseException();
    }
    
    public void setBaseId(final String baseId) {
        this.m_baseID = baseId;
    }
    
    public String getBaseId() {
        return this.m_baseID;
    }
}
