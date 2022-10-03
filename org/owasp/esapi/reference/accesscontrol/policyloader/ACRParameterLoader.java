package org.owasp.esapi.reference.accesscontrol.policyloader;

import org.apache.commons.configuration.XMLConfiguration;

public interface ACRParameterLoader<T>
{
    T getParameters(final XMLConfiguration p0, final int p1) throws Exception;
}
