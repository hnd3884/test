package com.sun.org.apache.xerces.internal.dom;

import org.w3c.dom.ProcessingInstruction;

public class ProcessingInstructionImpl extends CharacterDataImpl implements ProcessingInstruction
{
    static final long serialVersionUID = 7554435174099981510L;
    protected String target;
    
    public ProcessingInstructionImpl(final CoreDocumentImpl ownerDoc, final String target, final String data) {
        super(ownerDoc, data);
        this.target = target;
    }
    
    @Override
    public short getNodeType() {
        return 7;
    }
    
    @Override
    public String getNodeName() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.target;
    }
    
    @Override
    public String getTarget() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.target;
    }
    
    @Override
    public String getData() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.data;
    }
    
    @Override
    public void setData(final String data) {
        this.setNodeValue(data);
    }
    
    @Override
    public String getBaseURI() {
        if (this.needsSyncData()) {
            this.synchronizeData();
        }
        return this.ownerNode.getBaseURI();
    }
}
