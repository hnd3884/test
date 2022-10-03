package org.apache.xerces.dom.events;

import org.w3c.dom.Node;
import org.w3c.dom.events.MutationEvent;

public class MutationEventImpl extends EventImpl implements MutationEvent
{
    Node relatedNode;
    String prevValue;
    String newValue;
    String attrName;
    public short attrChange;
    public static final String DOM_SUBTREE_MODIFIED = "DOMSubtreeModified";
    public static final String DOM_NODE_INSERTED = "DOMNodeInserted";
    public static final String DOM_NODE_REMOVED = "DOMNodeRemoved";
    public static final String DOM_NODE_REMOVED_FROM_DOCUMENT = "DOMNodeRemovedFromDocument";
    public static final String DOM_NODE_INSERTED_INTO_DOCUMENT = "DOMNodeInsertedIntoDocument";
    public static final String DOM_ATTR_MODIFIED = "DOMAttrModified";
    public static final String DOM_CHARACTER_DATA_MODIFIED = "DOMCharacterDataModified";
    
    public MutationEventImpl() {
        this.relatedNode = null;
        this.prevValue = null;
        this.newValue = null;
        this.attrName = null;
    }
    
    public String getAttrName() {
        return this.attrName;
    }
    
    public short getAttrChange() {
        return this.attrChange;
    }
    
    public String getNewValue() {
        return this.newValue;
    }
    
    public String getPrevValue() {
        return this.prevValue;
    }
    
    public Node getRelatedNode() {
        return this.relatedNode;
    }
    
    public void initMutationEvent(final String s, final boolean b, final boolean b2, final Node relatedNode, final String prevValue, final String newValue, final String attrName, final short attrChange) {
        this.relatedNode = relatedNode;
        this.prevValue = prevValue;
        this.newValue = newValue;
        this.attrName = attrName;
        this.attrChange = attrChange;
        super.initEvent(s, b, b2);
    }
}
