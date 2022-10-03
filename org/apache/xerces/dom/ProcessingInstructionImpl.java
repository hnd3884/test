package org.apache.xerces.dom;

import org.w3c.dom.ProcessingInstruction;

public class ProcessingInstructionImpl extends CharacterDataImpl implements ProcessingInstruction
{
    static final long serialVersionUID = 7554435174099981510L;
    protected String target;
    
    public ProcessingInstructionImpl(final CoreDocumentImpl coreDocumentImpl, final String target, final String s) {
        super(coreDocumentImpl, s);
        this.target = target;
    }
    
    public short getNodeType() {
        return 7;
    }
    
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.target;
    }
    
    public String getTarget() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.target;
    }
    
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.ownerNode.getBaseURI();
    }
}
