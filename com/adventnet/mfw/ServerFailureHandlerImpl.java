package com.adventnet.mfw;

import java.util.logging.Level;
import java.util.logging.Logger;

public class ServerFailureHandlerImpl implements ServerFailureHandler
{
    private static final Logger OUT;
    
    @Override
    public void handle(final ServerFailureException exception) {
        ServerFailureHandlerImpl.OUT.log(Level.INFO, "Error Code :: {0}", exception.getErrorCode());
        exception.printStackTrace();
    }
    
    static {
        OUT = Logger.getLogger(ServerFailureHandlerImpl.class.getName());
    }
}
