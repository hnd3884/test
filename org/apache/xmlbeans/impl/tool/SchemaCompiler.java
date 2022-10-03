package org.apache.xmlbeans.impl.tool;

import org.apache.xmlbeans.SimpleValue;
import java.util.Iterator;
import org.apache.xmlbeans.impl.common.JarHelper;
import org.apache.xmlbeans.Filer;
import org.apache.xmlbeans.impl.util.FilerImpl;
import org.apache.xmlbeans.impl.schema.PathResourceLoader;
import java.util.HashMap;
import org.apache.xmlbeans.SystemProperties;
import org.apache.xmlbeans.impl.common.ResolverUtil;
import org.apache.xmlbeans.impl.repackage.Repackager;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemImpl;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.config.BindingConfigImpl;
import org.apache.xmlbeans.impl.schema.SchemaTypeSystemCompiler;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.impl.schema.SchemaTypeLoaderImpl;
import org.apache.xmlbeans.impl.xb.xmlconfig.Extensionconfig;
import org.apache.xmlbeans.impl.xb.xmlconfig.ConfigDocument;
import org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.impl.schema.StscState;
import org.apache.xmlbeans.impl.common.XmlErrorWatcher;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.xml.sax.EntityResolver;
import org.apache.xmlbeans.ResourceLoader;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Set;
import org.apache.xmlbeans.impl.common.XmlErrorPrinter;
import org.apache.xmlbeans.impl.common.IOUtil;
import java.io.IOException;
import java.io.File;
import java.util.StringTokenizer;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.xmlbeans.impl.values.XmlListImpl;
import java.util.Collections;
import org.apache.xmlbeans.SchemaCodePrinter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;

public class SchemaCompiler
{
    private static final String CONFIG_URI = "http://xml.apache.org/xmlbeans/2004/02/xbean/config";
    private static final String COMPATIBILITY_CONFIG_URI = "http://www.bea.com/2002/09/xbean/config";
    private static final Map MAP_COMPATIBILITY_CONFIG_URIS;
    
    public static void printUsage() {
        System.out.println("Compiles a schema into XML Bean classes and metadata.");
        System.out.println("Usage: scomp [opts] [dirs]* [schema.xsd]* [service.wsdl]* [config.xsdconfig]*");
        System.out.println("Options include:");
        System.out.println("    -cp [a;b;c] - classpath");
        System.out.println("    -d [dir] - target binary directory for .class and .xsb files");
        System.out.println("    -src [dir] - target directory for generated .java files");
        System.out.println("    -srconly - do not compile .java files or jar the output.");
        System.out.println("    -out [xmltypes.jar] - the name of the output jar");
        System.out.println("    -dl - permit network downloads for imports and includes (default is off)");
        System.out.println("    -noupa - do not enforce the unique particle attribution rule");
        System.out.println("    -nopvr - do not enforce the particle valid (restriction) rule");
        System.out.println("    -noann - ignore annotations");
        System.out.println("    -novdoc - do not validate contents of <documentation>");
        System.out.println("    -noext - ignore all extension (Pre/Post and Interface) found in .xsdconfig files");
        System.out.println("    -compiler - path to external java compiler");
        System.out.println("    -javasource [version] - generate java source compatible for a Java version (1.4 or 1.5)");
        System.out.println("    -ms - initial memory for external java compiler (default '" + CodeGenUtil.DEFAULT_MEM_START + "')");
        System.out.println("    -mx - maximum memory for external java compiler (default '" + CodeGenUtil.DEFAULT_MEM_MAX + "')");
        System.out.println("    -debug - compile with debug symbols");
        System.out.println("    -quiet - print fewer informational messages");
        System.out.println("    -verbose - print more informational messages");
        System.out.println("    -version - prints version information");
        System.out.println("    -license - prints license information");
        System.out.println("    -allowmdef \"[ns] [ns] [ns]\" - ignores multiple defs in given namespaces (use ##local for no-namespace)");
        System.out.println("    -catalog [file] -  catalog file for org.apache.xml.resolver.tools.CatalogResolver. (Note: needs resolver.jar from http://xml.apache.org/commons/components/resolver/index.html)");
        System.out.println();
    }
    
    public static void main(String[] args) {
        if (args.length == 0) {
            printUsage();
            System.exit(0);
            return;
        }
        final Set flags = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("quiet");
        flags.add("verbose");
        flags.add("version");
        flags.add("dl");
        flags.add("noupa");
        flags.add("nopvr");
        flags.add("noann");
        flags.add("novdoc");
        flags.add("noext");
        flags.add("srconly");
        flags.add("debug");
        final Set opts = new HashSet();
        opts.add("out");
        opts.add("name");
        opts.add("src");
        opts.add("d");
        opts.add("cp");
        opts.add("compiler");
        opts.add("javasource");
        opts.add("jar");
        opts.add("ms");
        opts.add("mx");
        opts.add("repackage");
        opts.add("schemaCodePrinter");
        opts.add("extension");
        opts.add("extensionParms");
        opts.add("allowmdef");
        opts.add("catalog");
        final CommandLine cl = new CommandLine(args, flags, opts);
        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null) {
            printUsage();
            System.exit(0);
            return;
        }
        final String[] badopts = cl.getBadOpts();
        if (badopts.length > 0) {
            for (int i = 0; i < badopts.length; ++i) {
                System.out.println("Unrecognized option: " + badopts[i]);
            }
            printUsage();
            System.exit(0);
            return;
        }
        if (cl.getOpt("license") != null) {
            CommandLine.printLicense();
            System.exit(0);
            return;
        }
        if (cl.getOpt("version") != null) {
            CommandLine.printVersion();
            System.exit(0);
            return;
        }
        args = cl.args();
        final boolean verbose = cl.getOpt("verbose") != null;
        boolean quiet = cl.getOpt("quiet") != null;
        if (verbose) {
            quiet = false;
        }
        if (verbose) {
            CommandLine.printVersion();
        }
        String outputfilename = cl.getOpt("out");
        final String repackage = cl.getOpt("repackage");
        final String codePrinterClass = cl.getOpt("schemaCodePrinter");
        SchemaCodePrinter codePrinter = null;
        if (codePrinterClass != null) {
            try {
                codePrinter = (SchemaCodePrinter)Class.forName(codePrinterClass).newInstance();
            }
            catch (final Exception e) {
                System.err.println("Failed to load SchemaCodePrinter class " + codePrinterClass + "; proceeding with default printer");
            }
        }
        final String name = cl.getOpt("name");
        final boolean download = cl.getOpt("dl") != null;
        final boolean noUpa = cl.getOpt("noupa") != null;
        final boolean noPvr = cl.getOpt("nopvr") != null;
        final boolean noAnn = cl.getOpt("noann") != null;
        final boolean noVDoc = cl.getOpt("novdoc") != null;
        final boolean noExt = cl.getOpt("noext") != null;
        final boolean nojavac = cl.getOpt("srconly") != null;
        final boolean debug = cl.getOpt("debug") != null;
        final String allowmdef = cl.getOpt("allowmdef");
        final Set mdefNamespaces = (allowmdef == null) ? Collections.EMPTY_SET : new HashSet(Arrays.asList(XmlListImpl.split_list(allowmdef)));
        final List extensions = new ArrayList();
        if (cl.getOpt("extension") != null) {
            try {
                final Extension e2 = new Extension();
                e2.setClassName(Class.forName(cl.getOpt("extension"), false, Thread.currentThread().getContextClassLoader()));
                extensions.add(e2);
            }
            catch (final ClassNotFoundException e3) {
                System.err.println("Could not find extension class: " + cl.getOpt("extension") + "  Is it on your classpath?");
                System.exit(1);
            }
        }
        if (extensions.size() > 0 && cl.getOpt("extensionParms") != null) {
            final Extension e2 = extensions.get(0);
            final StringTokenizer parmTokens = new StringTokenizer(cl.getOpt("extensionParms"), ";");
            while (parmTokens.hasMoreTokens()) {
                final String nvPair = parmTokens.nextToken();
                final int index = nvPair.indexOf(61);
                if (index < 0) {
                    System.err.println("extensionParms should be name=value;name=value");
                    System.exit(1);
                }
                final String n = nvPair.substring(0, index);
                final String v = nvPair.substring(index + 1);
                final Extension.Param param = e2.createParam();
                param.setName(n);
                param.setValue(v);
            }
        }
        final String classesdir = cl.getOpt("d");
        File classes = null;
        if (classesdir != null) {
            classes = new File(classesdir);
        }
        final String srcdir = cl.getOpt("src");
        File src = null;
        if (srcdir != null) {
            src = new File(srcdir);
        }
        if (nojavac && srcdir == null && classes != null) {
            src = classes;
        }
        File tempdir = null;
        Label_1123: {
            if (src != null) {
                if (classes != null) {
                    break Label_1123;
                }
            }
            try {
                tempdir = SchemaCodeGenerator.createTempDir();
            }
            catch (final IOException e4) {
                System.err.println("Error creating temp dir " + e4);
                System.exit(1);
            }
        }
        File jarfile = null;
        if (outputfilename == null && classes == null && !nojavac) {
            outputfilename = "xmltypes.jar";
        }
        if (outputfilename != null) {
            jarfile = new File(outputfilename);
        }
        if (src == null) {
            src = IOUtil.createDir(tempdir, "src");
        }
        if (classes == null) {
            classes = IOUtil.createDir(tempdir, "classes");
        }
        File[] classpath = null;
        final String cpString = cl.getOpt("cp");
        if (cpString != null) {
            final String[] cpparts = cpString.split(File.pathSeparator);
            final List cpList = new ArrayList();
            for (int j = 0; j < cpparts.length; ++j) {
                cpList.add(new File(cpparts[j]));
            }
            classpath = cpList.toArray(new File[cpList.size()]);
        }
        else {
            classpath = CodeGenUtil.systemClasspath();
        }
        final String javasource = cl.getOpt("javasource");
        final String compiler = cl.getOpt("compiler");
        final String jar = cl.getOpt("jar");
        if (verbose && jar != null) {
            System.out.println("The 'jar' option is no longer supported.");
        }
        final String memoryInitialSize = cl.getOpt("ms");
        final String memoryMaximumSize = cl.getOpt("mx");
        final File[] xsdFiles = cl.filesEndingWith(".xsd");
        final File[] wsdlFiles = cl.filesEndingWith(".wsdl");
        final File[] javaFiles = cl.filesEndingWith(".java");
        final File[] configFiles = cl.filesEndingWith(".xsdconfig");
        final URL[] urlFiles = cl.getURLs();
        if (xsdFiles.length + wsdlFiles.length + urlFiles.length == 0) {
            System.out.println("Could not find any xsd or wsdl files to process.");
            System.exit(0);
        }
        final File baseDir = cl.getBaseDir();
        final URI baseURI = (baseDir == null) ? null : baseDir.toURI();
        final XmlErrorPrinter err = new XmlErrorPrinter(verbose, baseURI);
        final String catString = cl.getOpt("catalog");
        final Parameters params = new Parameters();
        params.setBaseDir(baseDir);
        params.setXsdFiles(xsdFiles);
        params.setWsdlFiles(wsdlFiles);
        params.setJavaFiles(javaFiles);
        params.setConfigFiles(configFiles);
        params.setUrlFiles(urlFiles);
        params.setClasspath(classpath);
        params.setOutputJar(jarfile);
        params.setName(name);
        params.setSrcDir(src);
        params.setClassesDir(classes);
        params.setCompiler(compiler);
        params.setJavaSource(javasource);
        params.setMemoryInitialSize(memoryInitialSize);
        params.setMemoryMaximumSize(memoryMaximumSize);
        params.setNojavac(nojavac);
        params.setQuiet(quiet);
        params.setVerbose(verbose);
        params.setDownload(download);
        params.setNoUpa(noUpa);
        params.setNoPvr(noPvr);
        params.setNoAnn(noAnn);
        params.setNoVDoc(noVDoc);
        params.setNoExt(noExt);
        params.setDebug(debug);
        params.setErrorListener(err);
        params.setRepackage(repackage);
        params.setExtensions(extensions);
        params.setMdefNamespaces(mdefNamespaces);
        params.setCatalogFile(catString);
        params.setSchemaCodePrinter(codePrinter);
        final boolean result = compile(params);
        if (tempdir != null) {
            SchemaCodeGenerator.tryHardToDelete(tempdir);
        }
        if (!result) {
            System.exit(1);
        }
        System.exit(0);
    }
    
    private static SchemaTypeSystem loadTypeSystem(final String name, final File[] xsdFiles, final File[] wsdlFiles, final URL[] urlFiles, final File[] configFiles, final File[] javaFiles, final ResourceLoader cpResourceLoader, final boolean download, final boolean noUpa, final boolean noPvr, final boolean noAnn, final boolean noVDoc, final boolean noExt, final Set mdefNamespaces, final File baseDir, final Map sourcesToCopyMap, final Collection outerErrorListener, final File schemasDir, final EntityResolver entResolver, final File[] classpath, final String javasource) {
        final XmlErrorWatcher errorListener = new XmlErrorWatcher(outerErrorListener);
        final StscState state = StscState.start();
        try {
            state.setErrorListener(errorListener);
            final SchemaTypeLoader loader = XmlBeans.typeLoaderForClassLoader(SchemaDocument.class.getClassLoader());
            final ArrayList scontentlist = new ArrayList();
            if (xsdFiles != null) {
                for (int i = 0; i < xsdFiles.length; ++i) {
                    try {
                        final XmlOptions options = new XmlOptions();
                        options.setLoadLineNumbers();
                        options.setLoadMessageDigest();
                        options.setEntityResolver(entResolver);
                        final XmlObject schemadoc = loader.parse(xsdFiles[i], null, options);
                        if (!(schemadoc instanceof SchemaDocument)) {
                            StscState.addError(errorListener, "invalid.document.type", new Object[] { xsdFiles[i], "schema" }, schemadoc);
                        }
                        else {
                            addSchema(xsdFiles[i].toString(), (SchemaDocument)schemadoc, errorListener, noVDoc, scontentlist);
                        }
                    }
                    catch (final XmlException e) {
                        errorListener.add(e.getError());
                    }
                    catch (final Exception e2) {
                        StscState.addError(errorListener, "cannot.load.file", new Object[] { "xsd", xsdFiles[i], e2.getMessage() }, xsdFiles[i]);
                    }
                }
            }
            if (wsdlFiles != null) {
                for (int i = 0; i < wsdlFiles.length; ++i) {
                    try {
                        final XmlOptions options = new XmlOptions();
                        options.setLoadLineNumbers();
                        options.setLoadSubstituteNamespaces(Collections.singletonMap("http://schemas.xmlsoap.org/wsdl/", "http://www.apache.org/internal/xmlbeans/wsdlsubst"));
                        options.setEntityResolver(entResolver);
                        final XmlObject wsdldoc = loader.parse(wsdlFiles[i], null, options);
                        if (!(wsdldoc instanceof DefinitionsDocument)) {
                            StscState.addError(errorListener, "invalid.document.type", new Object[] { wsdlFiles[i], "wsdl" }, wsdldoc);
                        }
                        else {
                            addWsdlSchemas(wsdlFiles[i].toString(), (DefinitionsDocument)wsdldoc, errorListener, noVDoc, scontentlist);
                        }
                    }
                    catch (final XmlException e) {
                        errorListener.add(e.getError());
                    }
                    catch (final Exception e2) {
                        StscState.addError(errorListener, "cannot.load.file", new Object[] { "wsdl", wsdlFiles[i], e2.getMessage() }, wsdlFiles[i]);
                    }
                }
            }
            if (urlFiles != null) {
                for (int i = 0; i < urlFiles.length; ++i) {
                    try {
                        final XmlOptions options = new XmlOptions();
                        options.setLoadLineNumbers();
                        options.setLoadSubstituteNamespaces(Collections.singletonMap("http://schemas.xmlsoap.org/wsdl/", "http://www.apache.org/internal/xmlbeans/wsdlsubst"));
                        options.setEntityResolver(entResolver);
                        final XmlObject urldoc = loader.parse(urlFiles[i], null, options);
                        if (urldoc instanceof DefinitionsDocument) {
                            addWsdlSchemas(urlFiles[i].toString(), (DefinitionsDocument)urldoc, errorListener, noVDoc, scontentlist);
                        }
                        else if (urldoc instanceof SchemaDocument) {
                            addSchema(urlFiles[i].toString(), (SchemaDocument)urldoc, errorListener, noVDoc, scontentlist);
                        }
                        else {
                            StscState.addError(errorListener, "invalid.document.type", new Object[] { urlFiles[i], "wsdl or schema" }, urldoc);
                        }
                    }
                    catch (final XmlException e) {
                        errorListener.add(e.getError());
                    }
                    catch (final Exception e2) {
                        StscState.addError(errorListener, "cannot.load.file", new Object[] { "url", urlFiles[i], e2.getMessage() }, urlFiles[i]);
                    }
                }
            }
            final SchemaDocument.Schema[] sdocs = scontentlist.toArray(new SchemaDocument.Schema[scontentlist.size()]);
            final ArrayList cdoclist = new ArrayList();
            if (configFiles != null) {
                if (noExt) {
                    System.out.println("Pre/Post and Interface extensions will be ignored.");
                }
                for (int j = 0; j < configFiles.length; ++j) {
                    try {
                        final XmlOptions options2 = new XmlOptions();
                        options2.put("LOAD_LINE_NUMBERS");
                        options2.setEntityResolver(entResolver);
                        options2.setLoadSubstituteNamespaces(SchemaCompiler.MAP_COMPATIBILITY_CONFIG_URIS);
                        final XmlObject configdoc = loader.parse(configFiles[j], null, options2);
                        if (!(configdoc instanceof ConfigDocument)) {
                            StscState.addError(errorListener, "invalid.document.type", new Object[] { configFiles[j], "xsd config" }, configdoc);
                        }
                        else {
                            StscState.addInfo(errorListener, "Loading config file " + configFiles[j]);
                            if (configdoc.validate(new XmlOptions().setErrorListener(errorListener))) {
                                final ConfigDocument.Config config = ((ConfigDocument)configdoc).getConfig();
                                cdoclist.add(config);
                                if (noExt) {
                                    config.setExtensionArray(new Extensionconfig[0]);
                                }
                            }
                        }
                    }
                    catch (final XmlException e3) {
                        errorListener.add(e3.getError());
                    }
                    catch (final Exception e4) {
                        StscState.addError(errorListener, "cannot.load.file", new Object[] { "xsd config", configFiles[j], e4.getMessage() }, configFiles[j]);
                    }
                }
            }
            final ConfigDocument.Config[] cdocs = cdoclist.toArray(new ConfigDocument.Config[cdoclist.size()]);
            final SchemaTypeLoader linkTo = SchemaTypeLoaderImpl.build(null, cpResourceLoader, null);
            URI baseURI = null;
            if (baseDir != null) {
                baseURI = baseDir.toURI();
            }
            final XmlOptions opts = new XmlOptions();
            if (download) {
                opts.setCompileDownloadUrls();
            }
            if (noUpa) {
                opts.setCompileNoUpaRule();
            }
            if (noPvr) {
                opts.setCompileNoPvrRule();
            }
            if (noAnn) {
                opts.setCompileNoAnnotations();
            }
            if (mdefNamespaces != null) {
                opts.setCompileMdefNamespaces(mdefNamespaces);
            }
            opts.setCompileNoValidation();
            opts.setEntityResolver(entResolver);
            if (javasource != null) {
                opts.setGenerateJavaVersion(javasource);
            }
            final SchemaTypeSystemCompiler.Parameters params = new SchemaTypeSystemCompiler.Parameters();
            params.setName(name);
            params.setSchemas(sdocs);
            params.setConfig(BindingConfigImpl.forConfigDocuments(cdocs, javaFiles, classpath));
            params.setLinkTo(linkTo);
            params.setOptions(opts);
            params.setErrorListener(errorListener);
            params.setJavaize(true);
            params.setBaseURI(baseURI);
            params.setSourcesToCopyMap(sourcesToCopyMap);
            params.setSchemasDir(schemasDir);
            return SchemaTypeSystemCompiler.compile(params);
        }
        finally {
            StscState.end();
        }
    }
    
    private static void addSchema(final String name, final SchemaDocument schemadoc, final XmlErrorWatcher errorListener, final boolean noVDoc, final List scontentlist) {
        StscState.addInfo(errorListener, "Loading schema file " + name);
        final XmlOptions opts = new XmlOptions().setErrorListener(errorListener);
        if (noVDoc) {
            opts.setValidateTreatLaxAsSkip();
        }
        if (schemadoc.validate(opts)) {
            scontentlist.add(schemadoc.getSchema());
        }
    }
    
    private static void addWsdlSchemas(final String name, final DefinitionsDocument wsdldoc, final XmlErrorWatcher errorListener, final boolean noVDoc, final List scontentlist) {
        if (wsdlContainsEncoded(wsdldoc)) {
            StscState.addWarning(errorListener, "The WSDL " + name + " uses SOAP encoding. SOAP encoding is not compatible with literal XML Schema.", 60, wsdldoc);
        }
        StscState.addInfo(errorListener, "Loading wsdl file " + name);
        final XmlOptions opts = new XmlOptions().setErrorListener(errorListener);
        if (noVDoc) {
            opts.setValidateTreatLaxAsSkip();
        }
        final XmlObject[] types = wsdldoc.getDefinitions().getTypesArray();
        int count = 0;
        for (int j = 0; j < types.length; ++j) {
            final XmlObject[] schemas = types[j].selectPath("declare namespace xs=\"http://www.w3.org/2001/XMLSchema\" xs:schema");
            if (schemas.length == 0) {
                StscState.addWarning(errorListener, "The WSDL " + name + " did not have any schema documents in namespace 'http://www.w3.org/2001/XMLSchema'", 60, wsdldoc);
            }
            else {
                for (int k = 0; k < schemas.length; ++k) {
                    if (schemas[k] instanceof SchemaDocument.Schema && schemas[k].validate(opts)) {
                        ++count;
                        scontentlist.add(schemas[k]);
                    }
                }
            }
        }
        StscState.addInfo(errorListener, "Processing " + count + " schema(s) in " + name);
    }
    
    public static boolean compile(final Parameters params) {
        File baseDir = params.getBaseDir();
        final File[] xsdFiles = params.getXsdFiles();
        final File[] wsdlFiles = params.getWsdlFiles();
        final URL[] urlFiles = params.getUrlFiles();
        final File[] javaFiles = params.getJavaFiles();
        final File[] configFiles = params.getConfigFiles();
        final File[] classpath = params.getClasspath();
        final File outputJar = params.getOutputJar();
        final String name = params.getName();
        final File srcDir = params.getSrcDir();
        final File classesDir = params.getClassesDir();
        final String compiler = params.getCompiler();
        final String javasource = params.getJavaSource();
        final String memoryInitialSize = params.getMemoryInitialSize();
        final String memoryMaximumSize = params.getMemoryMaximumSize();
        final boolean nojavac = params.isNojavac();
        final boolean debug = params.isDebug();
        final boolean verbose = params.isVerbose();
        final boolean quiet = params.isQuiet();
        final boolean download = params.isDownload();
        final boolean noUpa = params.isNoUpa();
        final boolean noPvr = params.isNoPvr();
        final boolean noAnn = params.isNoAnn();
        final boolean noVDoc = params.isNoVDoc();
        final boolean noExt = params.isNoExt();
        final boolean incrSrcGen = params.isIncrementalSrcGen();
        final Collection outerErrorListener = params.getErrorListener();
        final String repackage = params.getRepackage();
        if (repackage != null) {
            SchemaTypeLoaderImpl.METADATA_PACKAGE_LOAD = SchemaTypeSystemImpl.METADATA_PACKAGE_GEN;
            final String stsPackage = SchemaTypeSystem.class.getPackage().getName();
            final Repackager repackager = new Repackager(repackage);
            SchemaTypeSystemImpl.METADATA_PACKAGE_GEN = repackager.repackage(new StringBuffer(stsPackage)).toString().replace('.', '_');
            System.out.println("\n\n\n" + stsPackage + ".SchemaCompiler  Metadata LOAD:" + SchemaTypeLoaderImpl.METADATA_PACKAGE_LOAD + " GEN:" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN);
        }
        final SchemaCodePrinter codePrinter = params.getSchemaCodePrinter();
        final List extensions = params.getExtensions();
        final Set mdefNamespaces = params.getMdefNamespaces();
        final EntityResolver cmdLineEntRes = (params.getEntityResolver() == null) ? ResolverUtil.resolverForCatalog(params.getCatalogFile()) : params.getEntityResolver();
        if (srcDir == null || classesDir == null) {
            throw new IllegalArgumentException("src and class gen directories may not be null.");
        }
        long start = System.currentTimeMillis();
        if (baseDir == null) {
            baseDir = new File(SystemProperties.getProperty("user.dir"));
        }
        ResourceLoader cpResourceLoader = null;
        final Map sourcesToCopyMap = new HashMap();
        if (classpath != null) {
            cpResourceLoader = new PathResourceLoader(classpath);
        }
        boolean result = true;
        final File schemasDir = IOUtil.createDir(classesDir, "schema" + SchemaTypeSystemImpl.METADATA_PACKAGE_GEN + "/src");
        final XmlErrorWatcher errorListener = new XmlErrorWatcher(outerErrorListener);
        final SchemaTypeSystem system = loadTypeSystem(name, xsdFiles, wsdlFiles, urlFiles, configFiles, javaFiles, cpResourceLoader, download, noUpa, noPvr, noAnn, noVDoc, noExt, mdefNamespaces, baseDir, sourcesToCopyMap, errorListener, schemasDir, cmdLineEntRes, classpath, javasource);
        if (errorListener.hasError()) {
            result = false;
        }
        long finish = System.currentTimeMillis();
        if (!quiet) {
            System.out.println("Time to build schema type system: " + (finish - start) / 1000.0 + " seconds");
        }
        if (result && system != null) {
            start = System.currentTimeMillis();
            final Repackager repackager2 = (repackage == null) ? null : new Repackager(repackage);
            final FilerImpl filer = new FilerImpl(classesDir, srcDir, repackager2, verbose, incrSrcGen);
            final XmlOptions options = new XmlOptions();
            if (codePrinter != null) {
                options.setSchemaCodePrinter(codePrinter);
            }
            if (javasource != null) {
                options.setGenerateJavaVersion(javasource);
            }
            system.save(filer);
            result &= SchemaTypeSystemCompiler.generateTypes(system, filer, options);
            if (incrSrcGen) {
                SchemaCodeGenerator.deleteObsoleteFiles(srcDir, srcDir, new HashSet(filer.getSourceFiles()));
            }
            if (result) {
                finish = System.currentTimeMillis();
                if (!quiet) {
                    System.out.println("Time to generate code: " + (finish - start) / 1000.0 + " seconds");
                }
            }
            if (result && !nojavac) {
                start = System.currentTimeMillis();
                final List sourcefiles = filer.getSourceFiles();
                if (javaFiles != null) {
                    sourcefiles.addAll(Arrays.asList(javaFiles));
                }
                if (!CodeGenUtil.externalCompile(sourcefiles, classesDir, classpath, debug, compiler, javasource, memoryInitialSize, memoryMaximumSize, quiet, verbose)) {
                    result = false;
                }
                finish = System.currentTimeMillis();
                if (result && !params.isQuiet()) {
                    System.out.println("Time to compile code: " + (finish - start) / 1000.0 + " seconds");
                }
                if (result && outputJar != null) {
                    try {
                        new JarHelper().jarDir(classesDir, outputJar);
                    }
                    catch (final IOException e) {
                        System.err.println("IO Error " + e);
                        result = false;
                    }
                    if (result && !params.isQuiet()) {
                        System.out.println("Compiled types to: " + outputJar);
                    }
                }
            }
        }
        if (!result && !quiet) {
            System.out.println("BUILD FAILED");
        }
        else {
            runExtensions(extensions, system, classesDir);
        }
        if (cpResourceLoader != null) {
            cpResourceLoader.close();
        }
        return result;
    }
    
    private static void runExtensions(final List extensions, final SchemaTypeSystem system, final File classesDir) {
        if (extensions != null && extensions.size() > 0) {
            SchemaCompilerExtension sce = null;
            final Iterator i = extensions.iterator();
            Map extensionParms = null;
            String classesDirName = null;
            try {
                classesDirName = classesDir.getCanonicalPath();
            }
            catch (final IOException e) {
                System.out.println("WARNING: Unable to get the path for schema jar file");
                classesDirName = classesDir.getAbsolutePath();
            }
            while (i.hasNext()) {
                final Extension extension = i.next();
                try {
                    sce = extension.getClassName().newInstance();
                }
                catch (final InstantiationException e2) {
                    System.out.println("UNABLE to instantiate schema compiler extension:" + extension.getClassName().getName());
                    System.out.println("EXTENSION Class was not run");
                    break;
                }
                catch (final IllegalAccessException e3) {
                    System.out.println("ILLEGAL ACCESS Exception when attempting to instantiate schema compiler extension: " + extension.getClassName().getName());
                    System.out.println("EXTENSION Class was not run");
                    break;
                }
                System.out.println("Running Extension: " + sce.getExtensionName());
                extensionParms = new HashMap();
                for (final Extension.Param p : extension.getParams()) {
                    extensionParms.put(p.getName(), p.getValue());
                }
                extensionParms.put("classesDir", classesDirName);
                sce.schemaCompilerExtension(system, extensionParms);
            }
        }
    }
    
    private static boolean wsdlContainsEncoded(final XmlObject wsdldoc) {
        final XmlObject[] useAttrs = wsdldoc.selectPath("declare namespace soap='http://schemas.xmlsoap.org/wsdl/soap/' .//soap:body/@use|.//soap:header/@use|.//soap:fault/@use");
        for (int i = 0; i < useAttrs.length; ++i) {
            if ("encoded".equals(((SimpleValue)useAttrs[i]).getStringValue())) {
                return true;
            }
        }
        return false;
    }
    
    static {
        (MAP_COMPATIBILITY_CONFIG_URIS = new HashMap()).put("http://www.bea.com/2002/09/xbean/config", "http://xml.apache.org/xmlbeans/2004/02/xbean/config");
    }
    
    public static class Parameters
    {
        private File baseDir;
        private File[] xsdFiles;
        private File[] wsdlFiles;
        private File[] javaFiles;
        private File[] configFiles;
        private URL[] urlFiles;
        private File[] classpath;
        private File outputJar;
        private String name;
        private File srcDir;
        private File classesDir;
        private String memoryInitialSize;
        private String memoryMaximumSize;
        private String compiler;
        private String javasource;
        private boolean nojavac;
        private boolean quiet;
        private boolean verbose;
        private boolean download;
        private Collection errorListener;
        private boolean noUpa;
        private boolean noPvr;
        private boolean noAnn;
        private boolean noVDoc;
        private boolean noExt;
        private boolean debug;
        private boolean incrementalSrcGen;
        private String repackage;
        private List extensions;
        private Set mdefNamespaces;
        private String catalogFile;
        private SchemaCodePrinter schemaCodePrinter;
        private EntityResolver entityResolver;
        
        public Parameters() {
            this.extensions = Collections.EMPTY_LIST;
            this.mdefNamespaces = Collections.EMPTY_SET;
        }
        
        public File getBaseDir() {
            return this.baseDir;
        }
        
        public void setBaseDir(final File baseDir) {
            this.baseDir = baseDir;
        }
        
        public File[] getXsdFiles() {
            return this.xsdFiles;
        }
        
        public void setXsdFiles(final File[] xsdFiles) {
            this.xsdFiles = xsdFiles;
        }
        
        public File[] getWsdlFiles() {
            return this.wsdlFiles;
        }
        
        public void setWsdlFiles(final File[] wsdlFiles) {
            this.wsdlFiles = wsdlFiles;
        }
        
        public File[] getJavaFiles() {
            return this.javaFiles;
        }
        
        public void setJavaFiles(final File[] javaFiles) {
            this.javaFiles = javaFiles;
        }
        
        public File[] getConfigFiles() {
            return this.configFiles;
        }
        
        public void setConfigFiles(final File[] configFiles) {
            this.configFiles = configFiles;
        }
        
        public URL[] getUrlFiles() {
            return this.urlFiles;
        }
        
        public void setUrlFiles(final URL[] urlFiles) {
            this.urlFiles = urlFiles;
        }
        
        public File[] getClasspath() {
            return this.classpath;
        }
        
        public void setClasspath(final File[] classpath) {
            this.classpath = classpath;
        }
        
        public File getOutputJar() {
            return this.outputJar;
        }
        
        public void setOutputJar(final File outputJar) {
            this.outputJar = outputJar;
        }
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public File getSrcDir() {
            return this.srcDir;
        }
        
        public void setSrcDir(final File srcDir) {
            this.srcDir = srcDir;
        }
        
        public File getClassesDir() {
            return this.classesDir;
        }
        
        public void setClassesDir(final File classesDir) {
            this.classesDir = classesDir;
        }
        
        public boolean isNojavac() {
            return this.nojavac;
        }
        
        public void setNojavac(final boolean nojavac) {
            this.nojavac = nojavac;
        }
        
        public boolean isQuiet() {
            return this.quiet;
        }
        
        public void setQuiet(final boolean quiet) {
            this.quiet = quiet;
        }
        
        public boolean isVerbose() {
            return this.verbose;
        }
        
        public void setVerbose(final boolean verbose) {
            this.verbose = verbose;
        }
        
        public boolean isDownload() {
            return this.download;
        }
        
        public void setDownload(final boolean download) {
            this.download = download;
        }
        
        public boolean isNoUpa() {
            return this.noUpa;
        }
        
        public void setNoUpa(final boolean noUpa) {
            this.noUpa = noUpa;
        }
        
        public boolean isNoPvr() {
            return this.noPvr;
        }
        
        public void setNoPvr(final boolean noPvr) {
            this.noPvr = noPvr;
        }
        
        public boolean isNoAnn() {
            return this.noAnn;
        }
        
        public void setNoAnn(final boolean noAnn) {
            this.noAnn = noAnn;
        }
        
        public boolean isNoVDoc() {
            return this.noVDoc;
        }
        
        public void setNoVDoc(final boolean newNoVDoc) {
            this.noVDoc = newNoVDoc;
        }
        
        public boolean isNoExt() {
            return this.noExt;
        }
        
        public void setNoExt(final boolean newNoExt) {
            this.noExt = newNoExt;
        }
        
        public boolean isIncrementalSrcGen() {
            return this.incrementalSrcGen;
        }
        
        public void setIncrementalSrcGen(final boolean incrSrcGen) {
            this.incrementalSrcGen = incrSrcGen;
        }
        
        public boolean isDebug() {
            return this.debug;
        }
        
        public void setDebug(final boolean debug) {
            this.debug = debug;
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
        
        public String getCompiler() {
            return this.compiler;
        }
        
        public void setCompiler(final String compiler) {
            this.compiler = compiler;
        }
        
        public String getJavaSource() {
            return this.javasource;
        }
        
        public void setJavaSource(final String javasource) {
            this.javasource = javasource;
        }
        
        @Deprecated
        public String getJar() {
            return null;
        }
        
        @Deprecated
        public void setJar(final String jar) {
        }
        
        public Collection getErrorListener() {
            return this.errorListener;
        }
        
        public void setErrorListener(final Collection errorListener) {
            this.errorListener = errorListener;
        }
        
        public String getRepackage() {
            return this.repackage;
        }
        
        public void setRepackage(final String newRepackage) {
            this.repackage = newRepackage;
        }
        
        public List getExtensions() {
            return this.extensions;
        }
        
        public void setExtensions(final List extensions) {
            this.extensions = extensions;
        }
        
        public Set getMdefNamespaces() {
            return this.mdefNamespaces;
        }
        
        public void setMdefNamespaces(final Set mdefNamespaces) {
            this.mdefNamespaces = mdefNamespaces;
        }
        
        public String getCatalogFile() {
            return this.catalogFile;
        }
        
        public void setCatalogFile(final String catalogPropFile) {
            this.catalogFile = catalogPropFile;
        }
        
        public SchemaCodePrinter getSchemaCodePrinter() {
            return this.schemaCodePrinter;
        }
        
        public void setSchemaCodePrinter(final SchemaCodePrinter schemaCodePrinter) {
            this.schemaCodePrinter = schemaCodePrinter;
        }
        
        public EntityResolver getEntityResolver() {
            return this.entityResolver;
        }
        
        public void setEntityResolver(final EntityResolver entityResolver) {
            this.entityResolver = entityResolver;
        }
    }
}
