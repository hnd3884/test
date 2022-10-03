package org.eclipse.jdt.core;

import java.io.IOException;
import org.eclipse.jdt.core.index.JavaIndexer;
import org.apache.tools.ant.BuildException;
import org.eclipse.jdt.internal.antadapter.AntAdapterMessages;
import org.apache.tools.ant.Task;

public class BuildJarIndex extends Task
{
    private String jarPath;
    private String indexPath;
    
    public void execute() throws BuildException {
        if (this.jarPath == null) {
            throw new BuildException(AntAdapterMessages.getString("buildJarIndex.jarFile.cannot.be.null"));
        }
        if (this.indexPath == null) {
            throw new BuildException(AntAdapterMessages.getString("buildJarIndex.indexFile.cannot.be.null"));
        }
        try {
            JavaIndexer.generateIndexForJar(this.jarPath, this.indexPath);
        }
        catch (final IOException e) {
            throw new BuildException(AntAdapterMessages.getString("buildJarIndex.ioexception.occured", e.getLocalizedMessage()));
        }
    }
    
    public void setJarPath(final String path) {
        this.jarPath = path;
    }
    
    public void setIndexPath(final String path) {
        this.indexPath = path;
    }
}
