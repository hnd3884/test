package org.apache.lucene.queryparser.flexible.messages;

import java.util.Locale;

public interface Message
{
    String getKey();
    
    Object[] getArguments();
    
    String getLocalizedMessage();
    
    String getLocalizedMessage(final Locale p0);
}
