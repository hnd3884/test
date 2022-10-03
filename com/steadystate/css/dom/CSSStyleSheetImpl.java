package com.steadystate.css.dom;

import java.net.URISyntaxException;
import java.net.URI;
import org.w3c.dom.css.CSSImportRule;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import com.steadystate.css.util.LangUtils;
import com.steadystate.css.format.CSSFormat;
import org.w3c.css.sac.SACMediaList;
import org.w3c.dom.DOMException;
import java.io.IOException;
import org.w3c.css.sac.CSSException;
import org.w3c.css.sac.ErrorHandler;
import com.steadystate.css.util.ThrowCssExceptionErrorHandler;
import com.steadystate.css.parser.CSSOMParser;
import java.io.Reader;
import org.w3c.css.sac.InputSource;
import java.io.StringReader;
import org.w3c.dom.css.CSSRuleList;
import org.w3c.dom.css.CSSRule;
import org.w3c.dom.stylesheets.MediaList;
import org.w3c.dom.stylesheets.StyleSheet;
import org.w3c.dom.Node;
import java.io.Serializable;
import com.steadystate.css.format.CSSFormatable;
import org.w3c.dom.css.CSSStyleSheet;

public class CSSStyleSheetImpl implements CSSStyleSheet, CSSFormatable, Serializable
{
    private static final long serialVersionUID = -2300541300646796363L;
    private boolean disabled_;
    private Node ownerNode_;
    private StyleSheet parentStyleSheet_;
    private String href_;
    private String title_;
    private MediaList media_;
    private CSSRule ownerRule_;
    private boolean readOnly_;
    private CSSRuleList cssRules_;
    private String baseUri_;
    
    public void setMedia(final MediaList media) {
        this.media_ = media;
    }
    
    private String getBaseUri() {
        return this.baseUri_;
    }
    
    public void setBaseUri(final String baseUri) {
        this.baseUri_ = baseUri;
    }
    
    public String getType() {
        return "text/css";
    }
    
    public boolean getDisabled() {
        return this.disabled_;
    }
    
    public void setDisabled(final boolean disabled) {
        this.disabled_ = disabled;
    }
    
    public Node getOwnerNode() {
        return this.ownerNode_;
    }
    
    public StyleSheet getParentStyleSheet() {
        return this.parentStyleSheet_;
    }
    
    public String getHref() {
        return this.href_;
    }
    
    public String getTitle() {
        return this.title_;
    }
    
    public MediaList getMedia() {
        return this.media_;
    }
    
    public CSSRule getOwnerRule() {
        return this.ownerRule_;
    }
    
    public CSSRuleList getCssRules() {
        if (this.cssRules_ == null) {
            this.cssRules_ = new CSSRuleListImpl();
        }
        return this.cssRules_;
    }
    
    public int insertRule(final String rule, final int index) throws DOMException {
        if (this.readOnly_) {
            throw new DOMExceptionImpl((short)7, 2);
        }
        try {
            final InputSource is = new InputSource((Reader)new StringReader(rule));
            final CSSOMParser parser = new CSSOMParser();
            parser.setParentStyleSheet(this);
            parser.setErrorHandler((ErrorHandler)ThrowCssExceptionErrorHandler.INSTANCE);
            final CSSRule r = parser.parseRule(is);
            if (r == null) {
                throw new DOMExceptionImpl(12, 0, "Parsing rule '" + rule + "' failed.");
            }
            if (this.getCssRules().getLength() > 0) {
                int msg = -1;
                if (r.getType() == 2) {
                    if (index != 0) {
                        msg = 15;
                    }
                    else if (this.getCssRules().item(0).getType() == 2) {
                        msg = 16;
                    }
                }
                else if (r.getType() == 3) {
                    if (index <= this.getCssRules().getLength()) {
                        for (int i = 0; i < index; ++i) {
                            final int rt = this.getCssRules().item(i).getType();
                            if (rt != 2 && rt != 3) {
                                msg = 17;
                                break;
                            }
                        }
                    }
                }
                else if (index <= this.getCssRules().getLength()) {
                    for (int i = index; i < this.getCssRules().getLength(); ++i) {
                        final int rt = this.getCssRules().item(i).getType();
                        if (rt == 2 || rt == 3) {
                            msg = 20;
                            break;
                        }
                    }
                }
                if (msg > -1) {
                    throw new DOMExceptionImpl((short)3, msg);
                }
            }
            ((CSSRuleListImpl)this.getCssRules()).insert(r, index);
        }
        catch (final IndexOutOfBoundsException e) {
            throw new DOMExceptionImpl(1, 1, e.getMessage());
        }
        catch (final CSSException e2) {
            throw new DOMExceptionImpl(12, 0, e2.getMessage());
        }
        catch (final IOException e3) {
            throw new DOMExceptionImpl(12, 0, e3.getMessage());
        }
        return index;
    }
    
    public void deleteRule(final int index) throws DOMException {
        if (this.readOnly_) {
            throw new DOMExceptionImpl((short)7, 2);
        }
        try {
            ((CSSRuleListImpl)this.getCssRules()).delete(index);
        }
        catch (final IndexOutOfBoundsException e) {
            throw new DOMExceptionImpl(1, 1, e.getMessage());
        }
    }
    
    public boolean isReadOnly() {
        return this.readOnly_;
    }
    
    public void setReadOnly(final boolean b) {
        this.readOnly_ = b;
    }
    
    public void setOwnerNode(final Node ownerNode) {
        this.ownerNode_ = ownerNode;
    }
    
    public void setParentStyleSheet(final StyleSheet parentStyleSheet) {
        this.parentStyleSheet_ = parentStyleSheet;
    }
    
    public void setHref(final String href) {
        this.href_ = href;
    }
    
    public void setTitle(final String title) {
        this.title_ = title;
    }
    
    public void setMediaText(final String mediaText) {
        final InputSource source = new InputSource((Reader)new StringReader(mediaText));
        try {
            final CSSOMParser parser = new CSSOMParser();
            final SACMediaList sml = parser.parseMedia(source);
            this.media_ = new MediaListImpl(sml);
        }
        catch (final IOException ex) {}
    }
    
    public void setOwnerRule(final CSSRule ownerRule) {
        this.ownerRule_ = ownerRule;
    }
    
    public void setCssRules(final CSSRuleList rules) {
        this.cssRules_ = rules;
    }
    
    public String getCssText() {
        return this.getCssText(null);
    }
    
    public String getCssText(final CSSFormat format) {
        final CSSRuleList rules = this.getCssRules();
        if (rules instanceof CSSFormatable) {
            return ((CSSRuleListImpl)rules).getCssText(format);
        }
        return this.getCssRules().toString();
    }
    
    @Override
    public String toString() {
        return this.getCssText(null);
    }
    
    @Override
    public boolean equals(final Object obj) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof CSSStyleSheet)) {
            return false;
        }
        final CSSStyleSheet css = (CSSStyleSheet)obj;
        boolean eq = LangUtils.equals(this.getCssRules(), css.getCssRules());
        eq = (eq && this.getDisabled() == css.getDisabled());
        eq = (eq && LangUtils.equals(this.getHref(), css.getHref()));
        eq = (eq && LangUtils.equals(this.getMedia(), css.getMedia()));
        eq = (eq && LangUtils.equals(this.getTitle(), css.getTitle()));
        return eq;
    }
    
    @Override
    public int hashCode() {
        int hash = 17;
        hash = LangUtils.hashCode(hash, this.baseUri_);
        hash = LangUtils.hashCode(hash, this.cssRules_);
        hash = LangUtils.hashCode(hash, this.disabled_);
        hash = LangUtils.hashCode(hash, this.href_);
        hash = LangUtils.hashCode(hash, this.media_);
        hash = LangUtils.hashCode(hash, this.ownerNode_);
        hash = LangUtils.hashCode(hash, this.readOnly_);
        hash = LangUtils.hashCode(hash, this.title_);
        return hash;
    }
    
    private void writeObject(final ObjectOutputStream out) throws IOException {
        out.writeObject(this.baseUri_);
        out.writeObject(this.cssRules_);
        out.writeBoolean(this.disabled_);
        out.writeObject(this.href_);
        out.writeObject(this.media_);
        out.writeBoolean(this.readOnly_);
        out.writeObject(this.title_);
    }
    
    private void readObject(final ObjectInputStream in) throws IOException, ClassNotFoundException {
        this.baseUri_ = (String)in.readObject();
        this.cssRules_ = (CSSRuleList)in.readObject();
        if (this.cssRules_ != null) {
            for (int i = 0; i < this.cssRules_.getLength(); ++i) {
                final CSSRule cssRule = this.cssRules_.item(i);
                if (cssRule instanceof AbstractCSSRuleImpl) {
                    ((AbstractCSSRuleImpl)cssRule).setParentStyleSheet(this);
                }
            }
        }
        this.disabled_ = in.readBoolean();
        this.href_ = (String)in.readObject();
        this.media_ = (MediaList)in.readObject();
        this.readOnly_ = in.readBoolean();
        this.title_ = (String)in.readObject();
    }
    
    public void importImports(final boolean recursive) throws DOMException {
        for (int i = 0; i < this.getCssRules().getLength(); ++i) {
            final CSSRule cssRule = this.getCssRules().item(i);
            if (cssRule.getType() == 3) {
                final CSSImportRule cssImportRule = (CSSImportRule)cssRule;
                try {
                    final URI importURI = new URI(this.getBaseUri()).resolve(cssImportRule.getHref());
                    final CSSStyleSheetImpl importedCSS = (CSSStyleSheetImpl)new CSSOMParser().parseStyleSheet(new InputSource(importURI.toString()), null, importURI.toString());
                    if (recursive) {
                        importedCSS.importImports(recursive);
                    }
                    final MediaList mediaList = cssImportRule.getMedia();
                    if (mediaList.getLength() == 0) {
                        mediaList.appendMedium("all");
                    }
                    final CSSMediaRuleImpl cssMediaRule = new CSSMediaRuleImpl(this, null, mediaList);
                    cssMediaRule.setRuleList((CSSRuleListImpl)importedCSS.getCssRules());
                    this.deleteRule(i);
                    ((CSSRuleListImpl)this.getCssRules()).insert(cssMediaRule, i);
                }
                catch (final URISyntaxException e) {
                    throw new DOMException((short)12, e.getLocalizedMessage());
                }
                catch (final IOException ex) {}
            }
        }
    }
}
