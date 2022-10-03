package org.htmlparser;

import org.htmlparser.tags.Html;
import org.htmlparser.tags.HeadTag;
import org.htmlparser.tags.BodyTag;
import org.htmlparser.tags.Span;
import org.htmlparser.tags.Div;
import org.htmlparser.tags.TitleTag;
import org.htmlparser.tags.TextareaTag;
import org.htmlparser.tags.TableTag;
import org.htmlparser.tags.TableRow;
import org.htmlparser.tags.TableHeader;
import org.htmlparser.tags.TableColumn;
import org.htmlparser.tags.StyleTag;
import org.htmlparser.tags.SelectTag;
import org.htmlparser.tags.ScriptTag;
import org.htmlparser.tags.ProcessingInstructionTag;
import org.htmlparser.tags.ParagraphTag;
import org.htmlparser.tags.OptionTag;
import org.htmlparser.tags.ObjectTag;
import org.htmlparser.tags.MetaTag;
import org.htmlparser.tags.LinkTag;
import org.htmlparser.tags.LabelTag;
import org.htmlparser.tags.JspTag;
import org.htmlparser.tags.InputTag;
import org.htmlparser.tags.ImageTag;
import org.htmlparser.tags.HeadingTag;
import org.htmlparser.tags.FrameTag;
import org.htmlparser.tags.FrameSetTag;
import org.htmlparser.tags.FormTag;
import org.htmlparser.tags.DoctypeTag;
import org.htmlparser.tags.DefinitionListBullet;
import org.htmlparser.tags.DefinitionList;
import org.htmlparser.tags.BulletList;
import org.htmlparser.tags.Bullet;
import org.htmlparser.tags.BaseHrefTag;
import org.htmlparser.tags.AppletTag;
import java.util.Locale;
import java.util.Set;
import java.util.Hashtable;
import java.util.Vector;
import org.htmlparser.nodes.TagNode;
import org.htmlparser.nodes.RemarkNode;
import org.htmlparser.lexer.Page;
import org.htmlparser.nodes.TextNode;
import java.util.Map;
import java.io.Serializable;

public class PrototypicalNodeFactory implements Serializable, NodeFactory
{
    protected Text mText;
    protected Remark mRemark;
    protected Tag mTag;
    protected Map mBlastocyst;
    
    public PrototypicalNodeFactory() {
        this(false);
    }
    
    public PrototypicalNodeFactory(final boolean empty) {
        this.clear();
        this.mText = new TextNode(null, 0, 0);
        this.mRemark = new RemarkNode(null, 0, 0);
        this.mTag = new TagNode(null, 0, 0, null);
        if (!empty) {
            this.registerTags();
        }
    }
    
    public PrototypicalNodeFactory(final Tag tag) {
        this(true);
        this.registerTag(tag);
    }
    
    public PrototypicalNodeFactory(final Tag[] tags) {
        this(true);
        for (int i = 0; i < tags.length; ++i) {
            this.registerTag(tags[i]);
        }
    }
    
    public Tag put(final String id, final Tag tag) {
        return this.mBlastocyst.put(id, tag);
    }
    
    public Tag get(final String id) {
        return this.mBlastocyst.get(id);
    }
    
    public Tag remove(final String id) {
        return this.mBlastocyst.remove(id);
    }
    
    public void clear() {
        this.mBlastocyst = new Hashtable();
    }
    
    public Set getTagNames() {
        return this.mBlastocyst.keySet();
    }
    
    public void registerTag(final Tag tag) {
        final String[] ids = tag.getIds();
        for (int i = 0; i < ids.length; ++i) {
            this.put(ids[i].toUpperCase(Locale.ENGLISH), tag);
        }
    }
    
    public void unregisterTag(final Tag tag) {
        final String[] ids = tag.getIds();
        for (int i = 0; i < ids.length; ++i) {
            this.remove(ids[i].toUpperCase(Locale.ENGLISH));
        }
    }
    
    public PrototypicalNodeFactory registerTags() {
        this.registerTag(new AppletTag());
        this.registerTag(new BaseHrefTag());
        this.registerTag(new Bullet());
        this.registerTag(new BulletList());
        this.registerTag(new DefinitionList());
        this.registerTag(new DefinitionListBullet());
        this.registerTag(new DoctypeTag());
        this.registerTag(new FormTag());
        this.registerTag(new FrameSetTag());
        this.registerTag(new FrameTag());
        this.registerTag(new HeadingTag());
        this.registerTag(new ImageTag());
        this.registerTag(new InputTag());
        this.registerTag(new JspTag());
        this.registerTag(new LabelTag());
        this.registerTag(new LinkTag());
        this.registerTag(new MetaTag());
        this.registerTag(new ObjectTag());
        this.registerTag(new OptionTag());
        this.registerTag(new ParagraphTag());
        this.registerTag(new ProcessingInstructionTag());
        this.registerTag(new ScriptTag());
        this.registerTag(new SelectTag());
        this.registerTag(new StyleTag());
        this.registerTag(new TableColumn());
        this.registerTag(new TableHeader());
        this.registerTag(new TableRow());
        this.registerTag(new TableTag());
        this.registerTag(new TextareaTag());
        this.registerTag(new TitleTag());
        this.registerTag(new Div());
        this.registerTag(new Span());
        this.registerTag(new BodyTag());
        this.registerTag(new HeadTag());
        this.registerTag(new Html());
        return this;
    }
    
    public Text getTextPrototype() {
        return this.mText;
    }
    
    public void setTextPrototype(final Text text) {
        if (null == text) {
            this.mText = new TextNode(null, 0, 0);
        }
        else {
            this.mText = text;
        }
    }
    
    public Remark getRemarkPrototype() {
        return this.mRemark;
    }
    
    public void setRemarkPrototype(final Remark remark) {
        if (null == remark) {
            this.mRemark = new RemarkNode(null, 0, 0);
        }
        else {
            this.mRemark = remark;
        }
    }
    
    public Tag getTagPrototype() {
        return this.mTag;
    }
    
    public void setTagPrototype(final Tag tag) {
        if (null == tag) {
            this.mTag = new TagNode(null, 0, 0, null);
        }
        else {
            this.mTag = tag;
        }
    }
    
    public Text createStringNode(final Page page, final int start, final int end) {
        Text ret;
        try {
            ret = (Text)this.getTextPrototype().clone();
            ret.setPage(page);
            ret.setStartPosition(start);
            ret.setEndPosition(end);
        }
        catch (final CloneNotSupportedException cnse) {
            ret = new TextNode(page, start, end);
        }
        return ret;
    }
    
    public Remark createRemarkNode(final Page page, final int start, final int end) {
        Remark ret;
        try {
            ret = (Remark)this.getRemarkPrototype().clone();
            ret.setPage(page);
            ret.setStartPosition(start);
            ret.setEndPosition(end);
        }
        catch (final CloneNotSupportedException cnse) {
            ret = new RemarkNode(page, start, end);
        }
        return ret;
    }
    
    public Tag createTagNode(final Page page, final int start, final int end, final Vector attributes) {
        Tag ret = null;
        if (0 != attributes.size()) {
            final Attribute attribute = attributes.elementAt(0);
            String id = attribute.getName();
            if (null != id) {
                try {
                    id = id.toUpperCase(Locale.ENGLISH);
                    if (!id.startsWith("/")) {
                        if (id.endsWith("/")) {
                            id = id.substring(0, id.length() - 1);
                        }
                        final Tag prototype = this.mBlastocyst.get(id);
                        if (null != prototype) {
                            ret = (Tag)prototype.clone();
                            ret.setPage(page);
                            ret.setStartPosition(start);
                            ret.setEndPosition(end);
                            ret.setAttributesEx(attributes);
                        }
                    }
                }
                catch (final CloneNotSupportedException ex) {}
            }
        }
        if (null == ret) {
            try {
                ret = (Tag)this.getTagPrototype().clone();
                ret.setPage(page);
                ret.setStartPosition(start);
                ret.setEndPosition(end);
                ret.setAttributesEx(attributes);
            }
            catch (final CloneNotSupportedException cnse) {
                ret = new TagNode(page, start, end, attributes);
            }
        }
        return ret;
    }
}
