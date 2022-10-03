package com.adventnet.util.script;

import java.util.Properties;

public interface RunScriptInterface
{
    void executeScript(final String p0) throws ScriptHandlerException;
    
    void executeScript(final String p0, final String[] p1) throws ScriptHandlerException;
    
    void executeLine(final String p0) throws ScriptHandlerException;
    
    void init(final Properties p0) throws ScriptHandlerException;
}
