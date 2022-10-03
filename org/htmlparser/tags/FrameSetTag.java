package org.htmlparser.tags;

import org.htmlparser.Node;
import org.htmlparser.util.SimpleNodeIterator;
import java.util.Locale;
import org.htmlparser.util.NodeList;

public class FrameSetTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return FrameSetTag.mIds;
    }
    
    public String[] getEndTagEnders() {
        return FrameSetTag.mEndTagEnders;
    }
    
    public String toString() {
        return "FRAMESET TAG : begins at : " + this.getStartPosition() + "; ends at : " + this.getEndPosition();
    }
    
    public NodeList getFrames() {
        return this.getChildren();
    }
    
    public FrameTag getFrame(final String name) {
        return this.getFrame(name, Locale.ENGLISH);
    }
    
    public FrameTag getFrame(String name, final Locale locale) {
        FrameTag ret = null;
        name = name.toUpperCase(locale);
        final SimpleNodeIterator e = this.getFrames().elements();
        while (e.hasMoreNodes() && null == ret) {
            final Node node = e.nextNode();
            if (node instanceof FrameTag) {
                ret = (FrameTag)node;
                if (ret.getFrameName().toUpperCase(locale).equals(name)) {
                    continue;
                }
                ret = null;
            }
        }
        return ret;
    }
    
    public void setFrames(final NodeList frames) {
        this.setChildren(frames);
    }
    
    static {
        mIds = new String[] { "FRAMESET" };
        mEndTagEnders = new String[] { "HTML" };
    }
}
