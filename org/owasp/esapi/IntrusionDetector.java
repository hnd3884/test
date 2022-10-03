package org.owasp.esapi;

import org.owasp.esapi.errors.IntrusionException;

public interface IntrusionDetector
{
    void addException(final Exception p0) throws IntrusionException;
    
    void addEvent(final String p0, final String p1) throws IntrusionException;
}
