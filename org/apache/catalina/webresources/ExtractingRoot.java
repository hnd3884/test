package org.apache.catalina.webresources;

import org.apache.catalina.startup.ExpandWar;
import java.io.OutputStream;
import java.io.InputStream;
import org.apache.catalina.WebResource;
import java.io.IOException;
import org.apache.catalina.WebResourceRoot;
import org.apache.catalina.util.IOTools;
import java.io.FileOutputStream;
import java.io.File;
import org.apache.catalina.LifecycleException;
import org.apache.tomcat.util.res.StringManager;

public class ExtractingRoot extends StandardRoot
{
    private static final StringManager sm;
    private static final String APPLICATION_JARS_DIR = "application-jars";
    
    @Override
    protected void processWebInfLib() throws LifecycleException {
        if (!super.isPackedWarFile()) {
            super.processWebInfLib();
            return;
        }
        final File expansionTarget = this.getExpansionTarget();
        if (!expansionTarget.isDirectory() && !expansionTarget.mkdirs()) {
            throw new LifecycleException(ExtractingRoot.sm.getString("extractingRoot.targetFailed", new Object[] { expansionTarget }));
        }
        final WebResource[] arr$;
        final WebResource[] possibleJars = arr$ = this.listResources("/WEB-INF/lib", false);
        for (final WebResource possibleJar : arr$) {
            if (possibleJar.isFile() && possibleJar.getName().endsWith(".jar")) {
                try {
                    File dest = new File(expansionTarget, possibleJar.getName());
                    dest = dest.getCanonicalFile();
                    try (final InputStream sourceStream = possibleJar.getInputStream();
                         final OutputStream destStream = new FileOutputStream(dest)) {
                        IOTools.flow(sourceStream, destStream);
                    }
                    this.createWebResourceSet(WebResourceRoot.ResourceSetType.CLASSES_JAR, "/WEB-INF/classes", dest.toURI().toURL(), "/");
                }
                catch (final IOException ioe) {
                    throw new LifecycleException(ExtractingRoot.sm.getString("extractingRoot.jarFailed", new Object[] { possibleJar.getName() }), ioe);
                }
            }
        }
    }
    
    private File getExpansionTarget() {
        final File tmpDir = (File)this.getContext().getServletContext().getAttribute("javax.servlet.context.tempdir");
        final File expansionTarget = new File(tmpDir, "application-jars");
        return expansionTarget;
    }
    
    @Override
    protected boolean isPackedWarFile() {
        return false;
    }
    
    @Override
    protected void stopInternal() throws LifecycleException {
        super.stopInternal();
        if (super.isPackedWarFile()) {
            final File expansionTarget = this.getExpansionTarget();
            ExpandWar.delete(expansionTarget);
        }
    }
    
    static {
        sm = StringManager.getManager((Class)ExtractingRoot.class);
    }
}
