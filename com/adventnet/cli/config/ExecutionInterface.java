package com.adventnet.cli.config;

import com.adventnet.cli.transport.CLIProtocolOptions;
import com.adventnet.cli.CLIMessage;

public interface ExecutionInterface
{
    String executeCommand(final CLIMessage p0) throws ExecutionException;
    
    void executeScript(final String p0, final String[] p1, final String p2) throws ExecutionException;
    
    void setLoginLevel(final LoginLevel p0) throws ExecutionException;
    
    void login(final CLIProtocolOptions p0) throws ExecutionException;
    
    void close() throws ExecutionException;
}
