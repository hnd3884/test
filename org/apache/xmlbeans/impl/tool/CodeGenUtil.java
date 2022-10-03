package org.apache.xmlbeans.impl.tool;

import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import org.apache.xmlbeans.SystemProperties;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Arrays;
import java.io.FileFilter;
import java.io.File;
import java.util.List;
import java.net.URISyntaxException;
import java.net.URI;

public class CodeGenUtil
{
    public static String DEFAULT_MEM_START;
    public static String DEFAULT_MEM_MAX;
    public static String DEFAULT_COMPILER;
    public static String DEFAULT_JAR;
    
    public static URI resolve(final URI base, final URI child) {
        URI ruri = base.resolve(child);
        if ("file".equals(ruri.getScheme()) && !child.equals(ruri) && base.getPath().startsWith("//") && !ruri.getPath().startsWith("//")) {
            final String path = "///".concat(ruri.getPath());
            try {
                ruri = new URI("file", null, path, ruri.getQuery(), ruri.getFragment());
            }
            catch (final URISyntaxException ex) {}
        }
        return ruri;
    }
    
    static void addAllJavaFiles(final List srcFiles, final List args) {
        for (final File f : srcFiles) {
            if (!f.isDirectory()) {
                args.add(quoteAndEscapeFilename(f.getAbsolutePath()));
            }
            else {
                final List inside = Arrays.asList(f.listFiles(new FileFilter() {
                    @Override
                    public boolean accept(final File file) {
                        return (file.isFile() && file.getName().endsWith(".java")) || file.isDirectory();
                    }
                }));
                addAllJavaFiles(inside, args);
            }
        }
    }
    
    private static String quoteAndEscapeFilename(final String filename) {
        if (filename.indexOf(" ") < 0) {
            return filename;
        }
        return "\"" + filename.replaceAll("\\\\", "\\\\\\\\") + "\"";
    }
    
    private static String quoteNoEscapeFilename(final String filename) {
        if (filename.indexOf(" ") < 0 || File.separatorChar == '/') {
            return filename;
        }
        return "\"" + filename + "\"";
    }
    
    @Deprecated
    public static boolean externalCompile(final List srcFiles, final File outdir, final File[] cp, final boolean debug) {
        return externalCompile(srcFiles, outdir, cp, debug, CodeGenUtil.DEFAULT_COMPILER, null, CodeGenUtil.DEFAULT_MEM_START, CodeGenUtil.DEFAULT_MEM_MAX, false, false);
    }
    
    public static boolean externalCompile(final List srcFiles, final File outdir, final File[] cp, final boolean debug, final String javacPath, final String memStart, final String memMax, final boolean quiet, final boolean verbose) {
        return externalCompile(srcFiles, outdir, cp, debug, javacPath, null, memStart, memMax, quiet, verbose);
    }
    
    public static boolean externalCompile(final List srcFiles, File outdir, File[] cp, final boolean debug, final String javacPath, String genver, final String memStart, final String memMax, final boolean quiet, final boolean verbose) {
        List args = new ArrayList();
        final File javac = findJavaTool((javacPath == null) ? CodeGenUtil.DEFAULT_COMPILER : javacPath);
        assert javac.exists() : "compiler not found " + javac;
        args.add(javac.getAbsolutePath());
        if (outdir == null) {
            outdir = new File(".");
        }
        else {
            args.add("-d");
            args.add(quoteAndEscapeFilename(outdir.getAbsolutePath()));
        }
        if (cp == null) {
            cp = systemClasspath();
        }
        if (cp.length > 0) {
            final StringBuffer classPath = new StringBuffer();
            classPath.append(outdir.getAbsolutePath());
            for (int i = 0; i < cp.length; ++i) {
                classPath.append(File.pathSeparator);
                classPath.append(cp[i].getAbsolutePath());
            }
            args.add("-classpath");
            args.add(quoteAndEscapeFilename(classPath.toString()));
        }
        if (genver == null) {
            genver = "1.6";
        }
        args.add("-source");
        args.add(genver);
        args.add("-target");
        args.add(genver);
        args.add(debug ? "-g" : "-g:none");
        if (verbose) {
            args.add("-verbose");
        }
        addAllJavaFiles(srcFiles, args);
        File clFile = null;
        try {
            clFile = File.createTempFile("javac", "");
            final FileWriter fw = new FileWriter(clFile);
            final Iterator j = args.iterator();
            j.next();
            while (j.hasNext()) {
                final String arg = j.next();
                fw.write(arg);
                fw.write(10);
            }
            fw.close();
            final List newargs = new ArrayList();
            newargs.add(args.get(0));
            if (memStart != null && memStart.length() != 0) {
                newargs.add("-J-Xms" + memStart);
            }
            if (memMax != null && memMax.length() != 0) {
                newargs.add("-J-Xmx" + memMax);
            }
            newargs.add("@" + clFile.getAbsolutePath());
            args = newargs;
        }
        catch (final Exception e) {
            System.err.println("Could not create command-line file for javac");
        }
        try {
            final String[] strArgs = args.toArray(new String[args.size()]);
            if (verbose) {
                System.out.print("compile command:");
                for (int k = 0; k < strArgs.length; ++k) {
                    System.out.print(" " + strArgs[k]);
                }
                System.out.println();
            }
            final Process proc = Runtime.getRuntime().exec(strArgs);
            final StringBuffer errorBuffer = new StringBuffer();
            final StringBuffer outputBuffer = new StringBuffer();
            final ThreadedReader out = new ThreadedReader(proc.getInputStream(), outputBuffer);
            final ThreadedReader err = new ThreadedReader(proc.getErrorStream(), errorBuffer);
            proc.waitFor();
            if (verbose || proc.exitValue() != 0) {
                if (outputBuffer.length() > 0) {
                    System.out.println(outputBuffer.toString());
                    System.out.flush();
                }
                if (errorBuffer.length() > 0) {
                    System.err.println(errorBuffer.toString());
                    System.err.flush();
                }
                if (proc.exitValue() != 0) {
                    return false;
                }
            }
        }
        catch (final Throwable e2) {
            System.err.println(e2.toString());
            System.err.println(e2.getCause());
            e2.printStackTrace(System.err);
            return false;
        }
        if (clFile != null) {
            clFile.delete();
        }
        return true;
    }
    
    public static File[] systemClasspath() {
        final List cp = new ArrayList();
        final String[] systemcp = SystemProperties.getProperty("java.class.path").split(File.pathSeparator);
        for (int i = 0; i < systemcp.length; ++i) {
            cp.add(new File(systemcp[i]));
        }
        return cp.toArray(new File[cp.size()]);
    }
    
    @Deprecated
    public static boolean externalJar(final File srcdir, final File outfile) {
        return externalJar(srcdir, outfile, CodeGenUtil.DEFAULT_JAR, false, false);
    }
    
    @Deprecated
    public static boolean externalJar(final File srcdir, final File outfile, final String jarPath, final boolean quiet, final boolean verbose) {
        final List args = new ArrayList();
        final File jar = findJavaTool((jarPath == null) ? CodeGenUtil.DEFAULT_JAR : jarPath);
        assert jar.exists() : "jar not found " + jar;
        args.add(jar.getAbsolutePath());
        args.add("cf");
        args.add(quoteNoEscapeFilename(outfile.getAbsolutePath()));
        args.add("-C");
        args.add(quoteNoEscapeFilename(srcdir.getAbsolutePath()));
        args.add(".");
        try {
            final String[] strArgs = args.toArray(new String[args.size()]);
            if (verbose) {
                System.out.print("jar command:");
                for (int i = 0; i < strArgs.length; ++i) {
                    System.out.print(" " + strArgs[i]);
                }
                System.out.println();
            }
            final Process proc = Runtime.getRuntime().exec(strArgs);
            final StringBuffer errorBuffer = new StringBuffer();
            final StringBuffer outputBuffer = new StringBuffer();
            final ThreadedReader out = new ThreadedReader(proc.getInputStream(), outputBuffer);
            final ThreadedReader err = new ThreadedReader(proc.getErrorStream(), errorBuffer);
            proc.waitFor();
            if (verbose || proc.exitValue() != 0) {
                if (outputBuffer.length() > 0) {
                    System.out.println(outputBuffer.toString());
                    System.out.flush();
                }
                if (errorBuffer.length() > 0) {
                    System.err.println(errorBuffer.toString());
                    System.err.flush();
                }
                if (proc.exitValue() != 0) {
                    return false;
                }
            }
        }
        catch (final Throwable e) {
            e.printStackTrace(System.err);
            return false;
        }
        return true;
    }
    
    private static File findJavaTool(final String tool) {
        final File toolFile = new File(tool);
        if (toolFile.isFile()) {
            return toolFile;
        }
        File result = new File(tool + ".exe");
        if (result.isFile()) {
            return result;
        }
        final String home = SystemProperties.getProperty("java.home");
        final String sep = File.separator;
        result = new File(home + sep + ".." + sep + "bin", tool);
        if (result.isFile()) {
            return result;
        }
        result = new File(result.getPath() + ".exe");
        if (result.isFile()) {
            return result;
        }
        result = new File(home + sep + "bin", tool);
        if (result.isFile()) {
            return result;
        }
        result = new File(result.getPath() + ".exe");
        if (result.isFile()) {
            return result;
        }
        return toolFile;
    }
    
    static {
        CodeGenUtil.DEFAULT_MEM_START = "8m";
        CodeGenUtil.DEFAULT_MEM_MAX = "256m";
        CodeGenUtil.DEFAULT_COMPILER = "javac";
        CodeGenUtil.DEFAULT_JAR = "jar";
    }
    
    private static class ThreadedReader
    {
        public ThreadedReader(final InputStream stream, final StringBuffer output) {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
            final Thread readerThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        String s;
                        while ((s = reader.readLine()) != null) {
                            output.append(s + "\n");
                        }
                    }
                    catch (final Exception ex) {}
                }
            });
            readerThread.start();
        }
    }
}
