package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.Location;
import javax.xml.stream.events.ProcessingInstruction;

public class ProcessingInstructionEvent extends DummyEvent implements ProcessingInstruction
{
    private String fName;
    private String fContent;
    
    public ProcessingInstructionEvent() {
        this.init();
    }
    
    public ProcessingInstructionEvent(final String targetName, final String data) {
        this(targetName, data, null);
    }
    
    public ProcessingInstructionEvent(final String targetName, final String data, final Location loc) {
        this.init();
        this.fName = targetName;
        this.fContent = data;
        this.setLocation(loc);
    }
    
    protected void init() {
        this.setEventType(3);
    }
    
    @Override
    public String getTarget() {
        return this.fName;
    }
    
    public void setTarget(final String targetName) {
        this.fName = targetName;
    }
    
    public void setData(final String data) {
        this.fContent = data;
    }
    
    @Override
    public String getData() {
        return this.fContent;
    }
    
    @Override
    public String toString() {
        if (this.fContent != null && this.fName != null) {
            return "<?" + this.fName + " " + this.fContent + "?>";
        }
        if (this.fName != null) {
            return "<?" + this.fName + "?>";
        }
        if (this.fContent != null) {
            return "<?" + this.fContent + "?>";
        }
        return "<??>";
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write(this.toString());
    }
}
