package org.apache.axiom.util.stax.xop;

import java.io.IOException;
import javax.activation.DataHandler;

public interface MimePartProvider
{
    boolean isLoaded(final String p0);
    
    DataHandler getDataHandler(final String p0) throws IOException;
}
