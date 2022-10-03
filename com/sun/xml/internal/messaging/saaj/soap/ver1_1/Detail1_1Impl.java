package com.sun.xml.internal.messaging.saaj.soap.ver1_1;

import javax.xml.namespace.QName;
import javax.xml.soap.DetailEntry;
import javax.xml.soap.Name;
import com.sun.xml.internal.messaging.saaj.soap.name.NameImpl;
import com.sun.xml.internal.messaging.saaj.soap.SOAPDocumentImpl;
import com.sun.xml.internal.messaging.saaj.soap.impl.DetailImpl;

public class Detail1_1Impl extends DetailImpl
{
    public Detail1_1Impl(final SOAPDocumentImpl ownerDoc, final String prefix) {
        super(ownerDoc, NameImpl.createDetail1_1Name(prefix));
    }
    
    public Detail1_1Impl(final SOAPDocumentImpl ownerDoc) {
        super(ownerDoc, NameImpl.createDetail1_1Name());
    }
    
    @Override
    protected DetailEntry createDetailEntry(final Name name) {
        return new DetailEntry1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), name);
    }
    
    @Override
    protected DetailEntry createDetailEntry(final QName name) {
        return new DetailEntry1_1Impl((SOAPDocumentImpl)this.getOwnerDocument(), name);
    }
}
