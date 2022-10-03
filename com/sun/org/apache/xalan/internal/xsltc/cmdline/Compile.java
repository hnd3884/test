package com.sun.org.apache.xalan.internal.xsltc.cmdline;

import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOptsException;
import java.io.File;
import java.net.URL;
import java.util.Vector;
import com.sun.org.apache.xalan.internal.xsltc.compiler.XSLTC;
import jdk.xml.internal.JdkXmlFeatures;
import com.sun.org.apache.xalan.internal.xsltc.cmdline.getopt.GetOpt;
import com.sun.org.apache.xalan.internal.xsltc.compiler.util.ErrorMsg;

public final class Compile
{
    private static int VERSION_MAJOR;
    private static int VERSION_MINOR;
    private static int VERSION_DELTA;
    private static boolean _allowExit;
    
    public static void printUsage() {
        System.err.println("XSLTC version " + Compile.VERSION_MAJOR + "." + Compile.VERSION_MINOR + ((Compile.VERSION_DELTA > 0) ? ("." + Compile.VERSION_DELTA) : "") + "\n" + new ErrorMsg("COMPILE_USAGE_STR"));
        if (Compile._allowExit) {
            System.exit(-1);
        }
    }
    
    public static void main(final String[] args) {
        try {
            boolean inputIsURL = false;
            boolean useStdIn = false;
            boolean classNameSet = false;
            final GetOpt getopt = new GetOpt(args, "o:d:j:p:uxhsinv");
            if (args.length < 1) {
                printUsage();
            }
            final XSLTC xsltc = new XSLTC(new JdkXmlFeatures(false));
            xsltc.init();
            int c;
            while ((c = getopt.getNextOption()) != -1) {
                switch (c) {
                    case 105: {
                        useStdIn = true;
                        continue;
                    }
                    case 111: {
                        xsltc.setClassName(getopt.getOptionArg());
                        classNameSet = true;
                        continue;
                    }
                    case 100: {
                        xsltc.setDestDirectory(getopt.getOptionArg());
                        continue;
                    }
                    case 112: {
                        xsltc.setPackageName(getopt.getOptionArg());
                        continue;
                    }
                    case 106: {
                        xsltc.setJarFileName(getopt.getOptionArg());
                        continue;
                    }
                    case 120: {
                        xsltc.setDebug(true);
                        continue;
                    }
                    case 117: {
                        inputIsURL = true;
                        continue;
                    }
                    case 115: {
                        Compile._allowExit = false;
                        continue;
                    }
                    case 110: {
                        xsltc.setTemplateInlining(true);
                        continue;
                    }
                    default: {
                        printUsage();
                        continue;
                    }
                }
            }
            boolean compileOK;
            if (useStdIn) {
                if (!classNameSet) {
                    System.err.println(new ErrorMsg("COMPILE_STDIN_ERR"));
                    if (Compile._allowExit) {
                        System.exit(-1);
                    }
                }
                compileOK = xsltc.compile(System.in, xsltc.getClassName());
            }
            else {
                final String[] stylesheetNames = getopt.getCmdArgs();
                final Vector stylesheetVector = new Vector();
                for (int i = 0; i < stylesheetNames.length; ++i) {
                    final String name = stylesheetNames[i];
                    URL url;
                    if (inputIsURL) {
                        url = new URL(name);
                    }
                    else {
                        url = new File(name).toURI().toURL();
                    }
                    stylesheetVector.addElement(url);
                }
                compileOK = xsltc.compile(stylesheetVector);
            }
            if (compileOK) {
                xsltc.printWarnings();
                if (xsltc.getJarFileName() != null) {
                    xsltc.outputToJar();
                }
                if (Compile._allowExit) {
                    System.exit(0);
                }
            }
            else {
                xsltc.printWarnings();
                xsltc.printErrors();
                if (Compile._allowExit) {
                    System.exit(-1);
                }
            }
        }
        catch (final GetOptsException ex) {
            System.err.println(ex);
            printUsage();
        }
        catch (final Exception e) {
            e.printStackTrace();
            if (Compile._allowExit) {
                System.exit(-1);
            }
        }
    }
    
    static {
        Compile.VERSION_MAJOR = 1;
        Compile.VERSION_MINOR = 4;
        Compile.VERSION_DELTA = 0;
        Compile._allowExit = true;
    }
}
