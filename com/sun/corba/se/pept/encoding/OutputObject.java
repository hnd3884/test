package com.sun.corba.se.pept.encoding;

import java.io.IOException;
import com.sun.corba.se.pept.protocol.MessageMediator;

public interface OutputObject
{
    void setMessageMediator(final MessageMediator p0);
    
    MessageMediator getMessageMediator();
    
    void close() throws IOException;
}
