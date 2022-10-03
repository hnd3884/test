package com.zoho.mickey.startup;

public interface ServerStartupHooks
{
    void preStartServer();
    
    void postStartServer();
}
