package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.Comment;
import org.w3c.dom.CharacterData;

public class CommentImpl extends CharacterDataImpl implements CharacterData, Comment
{
    static final long serialVersionUID = -2685736833408134044L;
    
    public CommentImpl(final CoreDocumentImpl ownerDoc, final String data) {
        super(ownerDoc, data);
    }
    
    @Override
    public short getNodeType() {
        return 8;
    }
    
    @Override
    public String getNodeName() {
        return "#comment";
    }
}
