package org.apache.tika.config;

import org.apache.tika.exception.TikaConfigException;
import java.util.Map;

public interface Initializable
{
    void initialize(final Map<String, Param> p0) throws TikaConfigException;
    
    void checkInitialization(final InitializableProblemHandler p0) throws TikaConfigException;
}
