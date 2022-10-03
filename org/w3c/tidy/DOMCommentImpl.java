package org.w3c.tidy;

import org.w3c.dom.Comment;

public class DOMCommentImpl extends DOMCharacterDataImpl implements Comment
{
    protected DOMCommentImpl(final org.w3c.tidy.Node node) {
        super(node);
    }
    
    public String getNodeName() {
        return "#comment";
    }
    
    public short getNodeType() {
        return 8;
    }
}
