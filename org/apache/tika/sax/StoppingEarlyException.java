package org.apache.tika.sax;

import org.xml.sax.SAXException;

public class StoppingEarlyException extends SAXException
{
    public static final StoppingEarlyException INSTANCE;
    
    static {
        INSTANCE = new StoppingEarlyException();
    }
}
