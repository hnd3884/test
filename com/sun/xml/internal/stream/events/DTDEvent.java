package com.sun.xml.internal.stream.events;

import java.io.IOException;
import java.io.Writer;
import java.util.List;
import javax.xml.stream.events.DTD;

public class DTDEvent extends DummyEvent implements DTD
{
    private String fDoctypeDeclaration;
    private List fNotations;
    private List fEntities;
    
    public DTDEvent() {
        this.init();
    }
    
    public DTDEvent(final String doctypeDeclaration) {
        this.init();
        this.fDoctypeDeclaration = doctypeDeclaration;
    }
    
    public void setDocumentTypeDeclaration(final String doctypeDeclaration) {
        this.fDoctypeDeclaration = doctypeDeclaration;
    }
    
    @Override
    public String getDocumentTypeDeclaration() {
        return this.fDoctypeDeclaration;
    }
    
    public void setEntities(final List entites) {
        this.fEntities = entites;
    }
    
    @Override
    public List getEntities() {
        return this.fEntities;
    }
    
    public void setNotations(final List notations) {
        this.fNotations = notations;
    }
    
    @Override
    public List getNotations() {
        return this.fNotations;
    }
    
    @Override
    public Object getProcessedDTD() {
        return null;
    }
    
    protected void init() {
        this.setEventType(11);
    }
    
    @Override
    public String toString() {
        return this.fDoctypeDeclaration;
    }
    
    @Override
    protected void writeAsEncodedUnicodeEx(final Writer writer) throws IOException {
        writer.write(this.fDoctypeDeclaration);
    }
}
