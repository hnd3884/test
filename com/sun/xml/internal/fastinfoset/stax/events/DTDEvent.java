package com.sun.xml.internal.fastinfoset.stax.events;

import java.util.List;
import javax.xml.stream.events.DTD;

public class DTDEvent extends EventBase implements DTD
{
    private String _dtd;
    private List _notations;
    private List _entities;
    
    public DTDEvent() {
        this.setEventType(11);
    }
    
    public DTDEvent(final String dtd) {
        this.setEventType(11);
        this._dtd = dtd;
    }
    
    @Override
    public String getDocumentTypeDeclaration() {
        return this._dtd;
    }
    
    public void setDTD(final String dtd) {
        this._dtd = dtd;
    }
    
    @Override
    public List getEntities() {
        return this._entities;
    }
    
    @Override
    public List getNotations() {
        return this._notations;
    }
    
    @Override
    public Object getProcessedDTD() {
        return null;
    }
    
    public void setEntities(final List entites) {
        this._entities = entites;
    }
    
    public void setNotations(final List notations) {
        this._notations = notations;
    }
    
    @Override
    public String toString() {
        return this._dtd;
    }
}
