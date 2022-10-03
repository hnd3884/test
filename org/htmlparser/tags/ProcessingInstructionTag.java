package org.htmlparser.tags;

import org.htmlparser.nodes.TagNode;

public class ProcessingInstructionTag extends TagNode
{
    private static final String[] mIds;
    
    public String[] getIds() {
        return ProcessingInstructionTag.mIds;
    }
    
    public String toString() {
        String guts = this.toHtml();
        guts = guts.substring(1, guts.length() - 2);
        return "Processing Instruction : " + guts + "; begins at : " + this.getStartPosition() + "; ends at : " + this.getEndPosition();
    }
    
    static {
        mIds = new String[] { "?" };
    }
}
