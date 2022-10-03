package org.apache.poi.poifs.macros;

public interface Module
{
    String getContent();
    
    ModuleType geModuleType();
    
    public enum ModuleType
    {
        Document, 
        Module, 
        Class;
    }
}
