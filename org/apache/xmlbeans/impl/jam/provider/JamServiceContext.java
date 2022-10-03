package org.apache.xmlbeans.impl.jam.provider;

import org.apache.xmlbeans.impl.jam.annotation.JavadocTagParser;
import java.io.IOException;
import java.io.File;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;

public interface JamServiceContext extends JamLogger
{
    ResourcePath getInputClasspath();
    
    ResourcePath getInputSourcepath();
    
    ResourcePath getToolClasspath();
    
    String getProperty(final String p0);
    
    MVisitor getInitializer();
    
    ClassLoader[] getReflectionClassLoaders();
    
    File[] getSourceFiles() throws IOException;
    
    String[] getAllClassnames() throws IOException;
    
    JamLogger getLogger();
    
    JamClassBuilder getBaseBuilder();
    
    JavadocTagParser getTagParser();
    
    boolean is14WarningsEnabled();
}
