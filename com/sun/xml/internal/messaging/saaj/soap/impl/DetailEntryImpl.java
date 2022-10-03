package com.sun.xml.internal.messaging.saaj.soap.impl;

import javax.xml.namespace.QName;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import javax.xml.soap.DetailEntry;

public abstract class DetailEntryImpl extends ElementImpl implements DetailEntry
{
    public DetailEntryImpl(final SOAPDocumentImpl ownerDoc, final Name qname) {
        super(ownerDoc, qname);
    }
    
    public DetailEntryImpl(final SOAPDocumentImpl ownerDoc, final QName qname) {
        super(ownerDoc, qname);
    }
}
