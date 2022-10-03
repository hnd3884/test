package org.apache.xmlbeans.impl.jam.internal.javadoc;

import java.io.IOException;
import java.util.List;
import com.sun.tools.javadoc.Main;
import java.io.Writer;
import java.io.StringWriter;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import java.io.FileNotFoundException;
import com.sun.javadoc.RootDoc;
import org.apache.xmlbeans.impl.jam.provider.JamLogger;
import java.io.PrintWriter;
import java.io.File;
import com.sun.javadoc.Doclet;

public class JavadocRunner extends Doclet
{
    private static final String JAVADOC_RUNNER_150 = "org.apache.xmlbeans.impl.jam.internal.javadoc.JavadocRunner_150";
    
    public static JavadocRunner newInstance() {
        try {
            Class.forName("com.sun.javadoc.AnnotationDesc");
        }
        catch (final ClassNotFoundException e) {
            return new JavadocRunner();
        }
        try {
            final Class onefive = Class.forName("org.apache.xmlbeans.impl.jam.internal.javadoc.JavadocRunner_150");
            return onefive.newInstance();
        }
        catch (final ClassNotFoundException cnfe) {}
        catch (final IllegalAccessException e2) {}
        catch (final InstantiationException ex) {}
        return new JavadocRunner();
    }
    
    synchronized RootDoc run(final File[] files, final PrintWriter out, final String sourcePath, final String classPath, final String[] javadocArgs, final JamLogger logger) throws IOException, FileNotFoundException {
        if (files == null || files.length == 0) {
            throw new FileNotFoundException("No input files found.");
        }
        final List argList = new ArrayList();
        if (javadocArgs != null) {
            argList.addAll(Arrays.asList(javadocArgs));
        }
        argList.add("-private");
        if (sourcePath != null) {
            argList.add("-sourcepath");
            argList.add(sourcePath);
        }
        if (classPath != null) {
            argList.add("-classpath");
            argList.add(classPath);
            argList.add("-docletpath");
            argList.add(classPath);
        }
        for (int i = 0; i < files.length; ++i) {
            argList.add(files[i].toString());
            if (out != null) {
                out.println(files[i].toString());
            }
        }
        final String[] args = new String[argList.size()];
        argList.toArray(args);
        StringWriter spew = null;
        PrintWriter spewWriter;
        if (out == null) {
            spewWriter = new PrintWriter(spew = new StringWriter());
        }
        else {
            spewWriter = out;
        }
        final ClassLoader originalCCL = Thread.currentThread().getContextClassLoader();
        try {
            JavadocResults.prepare();
            if (logger.isVerbose(this)) {
                logger.verbose("Invoking javadoc.  Command line equivalent is: ");
                final StringWriter sw = new StringWriter();
                sw.write("javadoc ");
                for (int j = 0; j < args.length; ++j) {
                    sw.write("'");
                    sw.write(args[j]);
                    sw.write("' ");
                }
                logger.verbose("  " + sw.toString());
            }
            final int result = Main.execute("JAM", spewWriter, spewWriter, spewWriter, this.getClass().getName(), args);
            final RootDoc root = JavadocResults.getRoot();
            if (result == 0 && root != null) {
                return root;
            }
            spewWriter.flush();
            if (JavadocClassloadingException.isClassloadingProblemPresent()) {
                throw new JavadocClassloadingException();
            }
            throw new RuntimeException("Unknown javadoc problem: result=" + result + ", root=" + root + ":\n" + ((spew == null) ? "" : spew.toString()));
        }
        catch (final RuntimeException e) {
            throw e;
        }
        finally {
            Thread.currentThread().setContextClassLoader(originalCCL);
        }
    }
    
    public static boolean start(final RootDoc root) {
        JavadocResults.setRoot(root);
        return true;
    }
}
