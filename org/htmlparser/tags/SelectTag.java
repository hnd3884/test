package org.htmlparser.tags;

import org.htmlparser.util.NodeList;
import org.htmlparser.Node;

public class SelectTag extends CompositeTag
{
    private static final String[] mIds;
    private static final String[] mEnders;
    private static final String[] mEndTagEnders;
    
    public String[] getIds() {
        return SelectTag.mIds;
    }
    
    public String[] getEnders() {
        return SelectTag.mEnders;
    }
    
    public String[] getEndTagEnders() {
        return SelectTag.mEndTagEnders;
    }
    
    public OptionTag[] getOptionTags() {
        final NodeList list = this.searchFor(OptionTag.class, true);
        final OptionTag[] ret = new OptionTag[list.size()];
        list.copyToNodeArray(ret);
        return ret;
    }
    
    static {
        mIds = new String[] { "SELECT" };
        mEnders = new String[] { "INPUT", "TEXTAREA", "SELECT" };
        mEndTagEnders = new String[] { "FORM", "BODY", "HTML" };
    }
}
