package com.sun.corba.se.spi.logging;

import java.util.logging.Logger;

public interface LogWrapperFactory
{
    LogWrapperBase create(final Logger p0);
}
