package com.sun.xml.internal.fastinfoset.stax.events;

import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent extends EventBase implements ProcessingInstruction
{
    private String targetName;
    private String _data;
    
    public ProcessingInstructionEvent() {
        this.init();
    }
    
    public ProcessingInstructionEvent(final String targetName, final String data) {
        this.targetName = targetName;
        this._data = data;
        this.init();
    }
    
    protected void init() {
        this.setEventType(3);
    }
    
    @Override
    public String getTarget() {
        return this.targetName;
    }
    
    public void setTarget(final String targetName) {
        this.targetName = targetName;
    }
    
    public void setData(final String data) {
        this._data = data;
    }
    
    @Override
    public String getData() {
        return this._data;
    }
    
    @Override
    public String toString() {
        if (this._data != null && this.targetName != null) {
            return "<?" + this.targetName + " " + this._data + "?>";
        }
        if (this.targetName != null) {
            return "<?" + this.targetName + "?>";
        }
        if (this._data != null) {
            return "<?" + this._data + "?>";
        }
        return "<??>";
    }
}
