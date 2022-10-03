package org.htmlparser.tags;

import org.htmlparser.nodes.TagNode;

public class InputTag extends TagNode
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return InputTag.mIds;
    }
    
    static {
        mIds = new String[] { "INPUT" };
    }
}
