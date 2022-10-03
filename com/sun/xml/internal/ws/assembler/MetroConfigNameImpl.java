package com.sun.xml.internal.ws.assembler;

public class MetroConfigNameImpl implements MetroConfigName
{
    private final String defaultFileName;
    private final String appFileName;
    
    public MetroConfigNameImpl(final String defaultFileName, final String appFileName) {
        this.defaultFileName = defaultFileName;
        this.appFileName = appFileName;
    }
    
    @Override
    public String getDefaultFileName() {
        return this.defaultFileName;
    }
    
    @Override
    public String getAppFileName() {
        return this.appFileName;
    }
}
