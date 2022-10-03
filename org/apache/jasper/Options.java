package org.apache.jasper;

import javax.servlet.jsp.tagext.TagLibraryInfo;
import java.util.Map;
import org.apache.jasper.compiler.TagPluginManager;
import org.apache.jasper.compiler.JspConfig;
import org.apache.jasper.compiler.TldCache;
import java.io.File;

public interface Options
{
    boolean getErrorOnUseBeanInvalidClassAttribute();
    
    boolean getKeepGenerated();
    
    boolean isPoolingEnabled();
    
    boolean getMappedFile();
    
    boolean getClassDebugInfo();
    
    int getCheckInterval();
    
    boolean getDevelopment();
    
    boolean getDisplaySourceFragment();
    
    boolean isSmapSuppressed();
    
    boolean isSmapDumped();
    
    boolean getTrimSpaces();
    
    String getIeClassId();
    
    File getScratchDir();
    
    String getClassPath();
    
    String getCompiler();
    
    String getCompilerTargetVM();
    
    String getCompilerSourceVM();
    
    String getCompilerClassName();
    
    TldCache getTldCache();
    
    String getJavaEncoding();
    
    boolean getFork();
    
    JspConfig getJspConfig();
    
    boolean isXpoweredBy();
    
    TagPluginManager getTagPluginManager();
    
    boolean genStringAsCharArray();
    
    int getModificationTestInterval();
    
    boolean getRecompileOnFail();
    
    boolean isCaching();
    
    Map<String, TagLibraryInfo> getCache();
    
    int getMaxLoadedJsps();
    
    int getJspIdleTimeout();
    
    boolean getStrictQuoteEscaping();
    
    boolean getQuoteAttributeEL();
}
