package com.sun.xml.internal.ws.policy;

interface PolicyMapKeyHandler
{
    boolean areEqual(final PolicyMapKey p0, final PolicyMapKey p1);
    
    int generateHashCode(final PolicyMapKey p0);
}
