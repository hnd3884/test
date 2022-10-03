package org.htmlparser.tags;

import org.htmlparser.nodes.TagNode;

public class FrameTag extends TagNode
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return FrameTag.mIds;
    }
    
    public String getFrameLocation() {
        String ret = this.getAttribute("SRC");
        if (null == ret) {
            ret = "";
        }
        else if (null != this.getPage()) {
            ret = this.getPage().getAbsoluteURL(ret);
        }
        return ret;
    }
    
    public void setFrameLocation(final String url) {
        this.setAttribute("SRC", url);
    }
    
    public String getFrameName() {
        return this.getAttribute("NAME");
    }
    
    public String toString() {
        return "FRAME TAG : Frame " + this.getFrameName() + " at " + this.getFrameLocation() + "; begins at : " + this.getStartPosition() + "; ends at : " + this.getEndPosition();
    }
    
    static {
        mIds = new String[] { "FRAME" };
    }
}
