package org.apache.axiom.ext.stax.datahandler;

import java.io.IOException;
import javax.activation.DataHandler;

public interface DataHandlerProvider
{
    boolean isLoaded();
    
    DataHandler getDataHandler() throws IOException;
}
