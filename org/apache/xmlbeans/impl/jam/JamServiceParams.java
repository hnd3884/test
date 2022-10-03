package org.apache.xmlbeans.impl.jam;

import org.apache.xmlbeans.impl.jam.provider.JamClassBuilder;
import org.apache.xmlbeans.impl.jam.annotation.JavadocTagParser;
import org.apache.xmlbeans.impl.jam.visitor.MVisitor;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;
import java.io.PrintWriter;
import java.io.File;

public interface JamServiceParams
{
    void includeSourceFile(final File p0);
    
    void includeSourcePattern(final File[] p0, final String p1);
    
    void excludeSourcePattern(final File[] p0, final String p1);
    
    void includeClassPattern(final File[] p0, final String p1);
    
    void excludeClassPattern(final File[] p0, final String p1);
    
    void includeSourceFile(final File[] p0, final File p1);
    
    void excludeSourceFile(final File[] p0, final File p1);
    
    void includeClassFile(final File[] p0, final File p1);
    
    void excludeClassFile(final File[] p0, final File p1);
    
    void includeClass(final String p0);
    
    void excludeClass(final String p0);
    
    void addSourcepath(final File p0);
    
    void addClasspath(final File p0);
    
    void setLoggerWriter(final PrintWriter p0);
    
    void setJamLogger(final JamLogger p0);
    
    void setVerbose(final Class p0);
    
    void setShowWarnings(final boolean p0);
    
    void setParentClassLoader(final JamClassLoader p0);
    
    void addToolClasspath(final File p0);
    
    void setPropertyInitializer(final MVisitor p0);
    
    void addInitializer(final MVisitor p0);
    
    void setJavadocTagParser(final JavadocTagParser p0);
    
    void setUseSystemClasspath(final boolean p0);
    
    void addClassBuilder(final JamClassBuilder p0);
    
    void addClassLoader(final ClassLoader p0);
    
    void setProperty(final String p0, final String p1);
    
    void set14WarningsEnabled(final boolean p0);
    
    @Deprecated
    void setVerbose(final boolean p0);
}
