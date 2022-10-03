package org.apache.jasper.compiler.tagplugin;

public interface TagPluginContext
{
    boolean isScriptless();
    
    boolean isAttributeSpecified(final String p0);
    
    String getTemporaryVariableName();
    
    void generateImport(final String p0);
    
    void generateDeclaration(final String p0, final String p1);
    
    void generateJavaSource(final String p0);
    
    boolean isConstantAttribute(final String p0);
    
    String getConstantAttribute(final String p0);
    
    void generateAttribute(final String p0);
    
    void generateBody();
    
    void dontUseTagPlugin();
    
    TagPluginContext getParentContext();
    
    void setPluginAttribute(final String p0, final Object p1);
    
    Object getPluginAttribute(final String p0);
    
    boolean isTagFile();
}
