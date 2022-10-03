package org.apache.axiom.om.impl.common;

import org.apache.axiom.om.OMException;
import org.apache.axiom.core.CoreModelException;

public class AxiomExceptionTranslator
{
    private AxiomExceptionTranslator() {
    }
    
    public static OMException translate(final CoreModelException ex) {
        return new OMException((Throwable)ex);
    }
}
