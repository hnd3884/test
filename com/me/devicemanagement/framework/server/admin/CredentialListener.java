package com.me.devicemanagement.framework.server.admin;

public interface CredentialListener
{
    default void credentialAdded(final Long credentialID) {
    }
    
    void credentialModified(final Long p0);
}
