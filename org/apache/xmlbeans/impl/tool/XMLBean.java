package org.apache.xmlbeans.impl.tool;

import java.util.Collections;
import org.apache.xmlbeans.XmlError;
import java.util.AbstractCollection;
import org.apache.tools.ant.Project;
import java.io.IOException;
import java.net.URI;
import java.util.StringTokenizer;
import org.apache.tools.ant.types.Reference;
import java.util.Iterator;
import org.apache.tools.ant.FileScanner;
import java.io.Writer;
import java.io.PrintWriter;
import java.io.StringWriter;
import org.apache.tools.ant.taskdefs.Jar;
import org.apache.tools.ant.taskdefs.Javac;
import java.util.Collection;
import org.apache.xmlbeans.impl.common.IOUtil;
import org.apache.tools.ant.types.FileSet;
import java.util.HashSet;
import org.apache.tools.ant.BuildException;
import java.util.HashMap;
import java.util.List;
import java.io.File;
import org.apache.tools.ant.types.Path;
import java.util.Set;
import java.util.ArrayList;
import org.apache.tools.ant.taskdefs.MatchingTask;

public class XMLBean extends MatchingTask
{
    private ArrayList schemas;
    private Set mdefnamespaces;
    private Path classpath;
    private File destfile;
    private File schema;
    private File srcgendir;
    private File classgendir;
    private boolean quiet;
    private boolean verbose;
    private boolean debug;
    private boolean optimize;
    private boolean download;
    private boolean srconly;
    private boolean noupa;
    private boolean nopvr;
    private boolean noann;
    private boolean novdoc;
    private boolean noext;
    private boolean failonerror;
    private boolean fork;
    private boolean includeAntRuntime;
    private boolean noSrcRegen;
    private boolean includeJavaRuntime;
    private boolean nowarn;
    private String typesystemname;
    private String forkedExecutable;
    private String compiler;
    private String debugLevel;
    private String memoryInitialSize;
    private String memoryMaximumSize;
    private String catalog;
    private String javasource;
    private List extensions;
    private HashMap _extRouter;
    private static final String XSD = ".xsd";
    private static final String WSDL = ".wsdl";
    private static final String JAVA = ".java";
    private static final String XSDCONFIG = ".xsdconfig";
    private String source;
    
    public XMLBean() {
        this.schemas = new ArrayList();
        this.noext = false;
        this.failonerror = true;
        this.fork = true;
        this.includeAntRuntime = true;
        this.includeJavaRuntime = false;
        this.nowarn = false;
        this.extensions = new ArrayList();
        this._extRouter = new HashMap(5);
        this.source = null;
    }
    
    public void execute() throws BuildException {
        if (this.schemas.size() == 0 && this.schema == null && this.fileset.getDir(this.project) == null) {
            final String msg = "The 'schema' or 'dir' attribute or a nested fileset is required.";
            if (this.failonerror) {
                throw new BuildException(msg);
            }
            this.log(msg, 0);
        }
        else {
            this._extRouter.put(".xsd", new HashSet());
            this._extRouter.put(".wsdl", new HashSet());
            this._extRouter.put(".java", new HashSet());
            this._extRouter.put(".xsdconfig", new HashSet());
            File theBasedir = this.schema;
            if (this.schema != null) {
                if (this.schema.isDirectory()) {
                    final FileScanner scanner = (FileScanner)this.getDirectoryScanner(this.schema);
                    final String[] paths = scanner.getIncludedFiles();
                    this.processPaths(paths, scanner.getBasedir());
                }
                else {
                    theBasedir = this.schema.getParentFile();
                    this.processPaths(new String[] { this.schema.getName() }, theBasedir);
                }
            }
            if (this.fileset.getDir(this.project) != null) {
                this.schemas.add(this.fileset);
            }
            for (final FileSet fs : this.schemas) {
                final FileScanner scanner2 = (FileScanner)fs.getDirectoryScanner(this.project);
                final File basedir = scanner2.getBasedir();
                final String[] paths2 = scanner2.getIncludedFiles();
                this.processPaths(paths2, basedir);
            }
            final Set xsdList = this._extRouter.get(".xsd");
            final Set wsdlList = this._extRouter.get(".wsdl");
            if (xsdList.size() + wsdlList.size() == 0) {
                this.log("Could not find any xsd or wsdl files to process.", 1);
                return;
            }
            final Set javaList = this._extRouter.get(".java");
            final Set xsdconfigList = this._extRouter.get(".xsdconfig");
            if (this.srcgendir == null && this.srconly) {
                this.srcgendir = this.classgendir;
            }
            if (this.destfile == null && this.classgendir == null && !this.srconly) {
                this.destfile = new File("xmltypes.jar");
            }
            if (this.verbose) {
                this.quiet = false;
            }
            final File[] xsdArray = xsdList.toArray(new File[xsdList.size()]);
            final File[] wsdlArray = wsdlList.toArray(new File[wsdlList.size()]);
            final File[] javaArray = javaList.toArray(new File[javaList.size()]);
            final File[] xsdconfigArray = xsdconfigList.toArray(new File[xsdconfigList.size()]);
            final ErrorLogger err = new ErrorLogger(this.verbose);
            boolean success = false;
            try {
                File tmpdir = null;
                if (this.srcgendir == null || this.classgendir == null) {
                    tmpdir = SchemaCodeGenerator.createTempDir();
                }
                if (this.srcgendir == null) {
                    this.srcgendir = IOUtil.createDir(tmpdir, "src");
                }
                if (this.classgendir == null) {
                    this.classgendir = IOUtil.createDir(tmpdir, "classes");
                }
                if (this.classpath == null) {
                    (this.classpath = new Path(this.project)).concatSystemClasspath();
                }
                final Path.PathElement pathElement = this.classpath.createPathElement();
                pathElement.setLocation(this.classgendir);
                final String[] paths3 = this.classpath.list();
                final File[] cp = new File[paths3.length];
                for (int i = 0; i < paths3.length; ++i) {
                    cp[i] = new File(paths3[i]);
                }
                final SchemaCompiler.Parameters params = new SchemaCompiler.Parameters();
                params.setBaseDir(theBasedir);
                params.setXsdFiles(xsdArray);
                params.setWsdlFiles(wsdlArray);
                params.setJavaFiles(javaArray);
                params.setConfigFiles(xsdconfigArray);
                params.setClasspath(cp);
                params.setName(this.typesystemname);
                params.setSrcDir(this.srcgendir);
                params.setClassesDir(this.classgendir);
                params.setNojavac(true);
                params.setDebug(this.debug);
                params.setVerbose(this.verbose);
                params.setQuiet(this.quiet);
                params.setDownload(this.download);
                params.setExtensions(this.extensions);
                params.setErrorListener(err);
                params.setCatalogFile(this.catalog);
                params.setIncrementalSrcGen(this.noSrcRegen);
                params.setMdefNamespaces(this.mdefnamespaces);
                params.setNoUpa(this.noupa);
                params.setNoPvr(this.nopvr);
                params.setNoAnn(this.noann);
                params.setNoVDoc(this.novdoc);
                params.setNoExt(this.noext);
                params.setJavaSource(this.javasource);
                success = SchemaCompiler.compile(params);
                if (success && !this.srconly) {
                    final long start = System.currentTimeMillis();
                    final Javac javac = new Javac();
                    javac.setProject(this.project);
                    javac.setTaskName(this.getTaskName());
                    javac.setClasspath(this.classpath);
                    if (this.compiler != null) {
                        javac.setCompiler(this.compiler);
                    }
                    javac.setDebug(this.debug);
                    if (this.debugLevel != null) {
                        javac.setDebugLevel(this.debugLevel);
                    }
                    javac.setDestdir(this.classgendir);
                    javac.setExecutable(this.forkedExecutable);
                    javac.setFailonerror(this.failonerror);
                    javac.setFork(this.fork);
                    if (this.javasource != null) {
                        javac.setSource(this.javasource);
                        javac.setTarget(this.javasource);
                    }
                    else {
                        javac.setSource("1.4");
                        javac.setTarget("1.4");
                    }
                    javac.setIncludeantruntime(this.includeAntRuntime);
                    javac.setIncludejavaruntime(this.includeJavaRuntime);
                    javac.setNowarn(this.nowarn);
                    javac.setSrcdir(new Path(this.project, this.srcgendir.getAbsolutePath()));
                    if (this.memoryInitialSize != null) {
                        javac.setMemoryInitialSize(this.memoryInitialSize);
                    }
                    if (this.memoryMaximumSize != null) {
                        javac.setMemoryMaximumSize(this.memoryMaximumSize);
                    }
                    javac.setOptimize(this.optimize);
                    javac.setVerbose(this.verbose);
                    javac.execute();
                    final long finish = System.currentTimeMillis();
                    if (!this.quiet) {
                        this.log("Time to compile code: " + (finish - start) / 1000.0 + " seconds");
                    }
                    if (this.destfile != null) {
                        final Jar jar = new Jar();
                        jar.setProject(this.project);
                        jar.setTaskName(this.getTaskName());
                        jar.setBasedir(this.classgendir);
                        jar.setDestFile(this.destfile);
                        jar.execute();
                    }
                }
                if (tmpdir != null) {
                    SchemaCodeGenerator.tryHardToDelete(tmpdir);
                }
            }
            catch (final BuildException e) {
                throw e;
            }
            catch (final Throwable e2) {
                if (e2 instanceof InterruptedException || this.failonerror) {
                    throw new BuildException(e2);
                }
                this.log("Exception while building schemas: " + e2.getMessage(), 0);
                final StringWriter sw = new StringWriter();
                e2.printStackTrace(new PrintWriter(sw));
                this.log(sw.toString(), 3);
            }
            if (!success && this.failonerror) {
                throw new BuildException();
            }
        }
    }
    
    private void processPaths(final String[] paths, final File baseDir) {
        for (int i = 0; i < paths.length; ++i) {
            final int dot = paths[i].lastIndexOf(46);
            if (dot > -1) {
                final String path = paths[i];
                final String possExt = path.substring(dot).toLowerCase();
                final Set set = this._extRouter.get(possExt);
                if (set != null) {
                    set.add(new File(baseDir, path));
                }
            }
        }
    }
    
    public void addFileset(final FileSet fileset) {
        this.schemas.add(fileset);
    }
    
    public File getSchema() {
        return this.schema;
    }
    
    public void setSchema(final File schema) {
        this.schema = schema;
    }
    
    public void setClasspath(final Path classpath) {
        if (this.classpath != null) {
            this.classpath.append(classpath);
        }
        else {
            this.classpath = classpath;
        }
    }
    
    public Path createClasspath() {
        if (this.classpath == null) {
            this.classpath = new Path(this.project);
        }
        return this.classpath.createPath();
    }
    
    public void setClasspathRef(final Reference classpathref) {
        if (this.classpath == null) {
            this.classpath = new Path(this.project);
        }
        this.classpath.createPath().setRefid(classpathref);
    }
    
    public Path getClasspath() {
        return this.classpath;
    }
    
    public File getDestfile() {
        return this.destfile;
    }
    
    public void setDestfile(final File destfile) {
        this.destfile = destfile;
    }
    
    public File getSrcgendir() {
        return this.srcgendir;
    }
    
    public void setSrcgendir(final File srcgendir) {
        this.srcgendir = srcgendir;
    }
    
    public File getClassgendir() {
        return this.classgendir;
    }
    
    public void setClassgendir(final File classgendir) {
        this.classgendir = classgendir;
    }
    
    public void setCompiler(final String compiler) {
        this.compiler = compiler;
    }
    
    public boolean isDownload() {
        return this.download;
    }
    
    public void setDownload(final boolean download) {
        this.download = download;
    }
    
    public void setOptimize(final boolean optimize) {
        this.optimize = optimize;
    }
    
    public boolean getOptimize() {
        return this.optimize;
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    public boolean isQuiet() {
        return this.quiet;
    }
    
    public void setQuiet(final boolean quiet) {
        this.quiet = quiet;
    }
    
    public boolean isDebug() {
        return this.debug;
    }
    
    public String getDebugLevel() {
        return this.debugLevel;
    }
    
    public void setDebugLevel(final String v) {
        this.debugLevel = v;
    }
    
    public void setDebug(final boolean debug) {
        this.debug = debug;
    }
    
    public void setFork(final boolean f) {
        this.fork = f;
    }
    
    public void setExecutable(final String forkExec) {
        this.forkedExecutable = forkExec;
    }
    
    public String getExecutable() {
        return this.forkedExecutable;
    }
    
    public boolean isSrconly() {
        return this.srconly;
    }
    
    public void setSrconly(final boolean srconly) {
        this.srconly = srconly;
    }
    
    public String getTypesystemname() {
        return this.typesystemname;
    }
    
    public Extension createExtension() {
        final Extension e = new Extension();
        this.extensions.add(e);
        return e;
    }
    
    public void setIgnoreDuplicatesInNamespaces(final String namespaces) {
        this.mdefnamespaces = new HashSet();
        final StringTokenizer st = new StringTokenizer(namespaces, ",");
        while (st.hasMoreTokens()) {
            final String namespace = st.nextToken().trim();
            this.mdefnamespaces.add(namespace);
        }
    }
    
    public String getIgnoreDuplicatesInNamespaces() {
        if (this.mdefnamespaces == null) {
            return null;
        }
        final StringBuffer buf = new StringBuffer();
        final Iterator i = this.mdefnamespaces.iterator();
        while (i.hasNext()) {
            buf.append(i.next());
            if (i.hasNext()) {
                buf.append(",");
            }
        }
        return buf.toString();
    }
    
    public void setTypesystemname(final String typesystemname) {
        this.typesystemname = typesystemname;
    }
    
    public boolean isFailonerror() {
        return this.failonerror;
    }
    
    public void setFailonerror(final boolean failonerror) {
        this.failonerror = failonerror;
    }
    
    public boolean isIncludeAntRuntime() {
        return this.includeAntRuntime;
    }
    
    public void setIncludeAntRuntime(final boolean includeAntRuntime) {
        this.includeAntRuntime = includeAntRuntime;
    }
    
    public boolean isIncludeJavaRuntime() {
        return this.includeJavaRuntime;
    }
    
    public void setIncludeJavaRuntime(final boolean includeJavaRuntime) {
        this.includeJavaRuntime = includeJavaRuntime;
    }
    
    public boolean isNowarn() {
        return this.nowarn;
    }
    
    public void setNowarn(final boolean nowarn) {
        this.nowarn = nowarn;
    }
    
    public boolean isNoSrcRegen() {
        return this.noSrcRegen;
    }
    
    public void setNoSrcRegen(final boolean noSrcRegen) {
        this.noSrcRegen = noSrcRegen;
    }
    
    public String getMemoryInitialSize() {
        return this.memoryInitialSize;
    }
    
    public void setMemoryInitialSize(final String memoryInitialSize) {
        this.memoryInitialSize = memoryInitialSize;
    }
    
    public String getMemoryMaximumSize() {
        return this.memoryMaximumSize;
    }
    
    public void setMemoryMaximumSize(final String memoryMaximumSize) {
        this.memoryMaximumSize = memoryMaximumSize;
    }
    
    public void setNoUpa(final boolean noupa) {
        this.noupa = noupa;
    }
    
    public boolean isNoUpa() {
        return this.noupa;
    }
    
    public void setNoPvr(final boolean nopvr) {
        this.nopvr = nopvr;
    }
    
    public boolean isNoPvr() {
        return this.nopvr;
    }
    
    public void setNoAnnotations(final boolean noann) {
        this.noann = noann;
    }
    
    public boolean isNoAnnotations() {
        return this.noann;
    }
    
    public void setNoValidateDoc(final boolean novdoc) {
        this.novdoc = novdoc;
    }
    
    public boolean isNoValidateDoc() {
        return this.novdoc;
    }
    
    public void setNoExt(final boolean noext) {
        this.noext = noext;
    }
    
    public boolean isNoExt() {
        return this.noext;
    }
    
    public void setJavaSource(final String javasource) {
        this.javasource = javasource;
    }
    
    public String getJavaSource() {
        return this.javasource;
    }
    
    public void setSource(final String s) {
        this.source = s;
    }
    
    public String getCatalog() {
        return this.catalog;
    }
    
    public void setCatalog(final String catalog) {
        this.catalog = catalog;
    }
    
    private static URI uriFromFile(final File f) {
        if (f == null) {
            return null;
        }
        try {
            return f.getCanonicalFile().toURI();
        }
        catch (final IOException e) {
            return f.getAbsoluteFile().toURI();
        }
    }
    
    public class ErrorLogger extends AbstractCollection
    {
        private boolean _noisy;
        private URI _baseURI;
        
        public ErrorLogger(final boolean noisy) {
            this._noisy = noisy;
            this._baseURI = uriFromFile(XMLBean.this.project.getBaseDir());
        }
        
        @Override
        public boolean add(final Object o) {
            if (o instanceof XmlError) {
                final XmlError err = (XmlError)o;
                if (err.getSeverity() == 0) {
                    XMLBean.this.log(err.toString(this._baseURI), 0);
                }
                else if (err.getSeverity() == 1) {
                    XMLBean.this.log(err.toString(this._baseURI), 1);
                }
                else if (this._noisy) {
                    XMLBean.this.log(err.toString(this._baseURI), 2);
                }
            }
            return false;
        }
        
        @Override
        public Iterator iterator() {
            return Collections.EMPTY_LIST.iterator();
        }
        
        @Override
        public int size() {
            return 0;
        }
    }
}
