package com.sun.xml.internal.messaging.saaj.soap;

import java.io.IOException;
import java.io.OutputStream;
import javax.xml.transform.Source;
import javax.xml.soap.SOAPEnvelope;

public interface Envelope extends SOAPEnvelope
{
    Source getContent();
    
    void output(final OutputStream p0) throws IOException;
    
    void output(final OutputStream p0, final boolean p1) throws IOException;
}
