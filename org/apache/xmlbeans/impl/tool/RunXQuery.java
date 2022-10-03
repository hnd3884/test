package org.apache.xmlbeans.impl.tool;

import java.util.Set;
import java.io.OutputStream;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlBeans;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.FileInputStream;
import java.io.File;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;

public class RunXQuery
{
    public static void printUsage() {
        System.out.println("Run an XQuery against an XML instance");
        System.out.println("Usage:");
        System.out.println("xquery [-verbose] [-pretty] [-q <query> | -qf query.xq] [file.xml]*");
        System.out.println(" -q <query> to specify a query on the command-line");
        System.out.println(" -qf <query> to specify a file containing a query");
        System.out.println(" -pretty pretty-prints the results");
        System.out.println(" -license prints license information");
        System.out.println(" the query is run on each XML file specified");
        System.out.println("");
    }
    
    public static void main(String[] args) throws Exception {
        final Set flags = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("verbose");
        flags.add("pretty");
        final CommandLine cl = new CommandLine(args, flags, Arrays.asList("q", "qf"));
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
        if (args.length == 0) {
            printUsage();
            System.exit(0);
            return;
        }
        final boolean verbose = cl.getOpt("verbose") != null;
        final boolean pretty = cl.getOpt("pretty") != null;
        String query = cl.getOpt("q");
        final String queryfile = cl.getOpt("qf");
        if (query == null && queryfile == null) {
            System.err.println("No query specified");
            System.exit(0);
            return;
        }
        if (query != null && queryfile != null) {
            System.err.println("Specify -qf or -q, not both.");
            System.exit(0);
            return;
        }
        try {
            if (queryfile != null) {
                final File queryFile = new File(queryfile);
                final FileInputStream is = new FileInputStream(queryFile);
                final InputStreamReader r = new InputStreamReader(is);
                final StringBuffer sb = new StringBuffer();
                while (true) {
                    final int ch = r.read();
                    if (ch < 0) {
                        break;
                    }
                    sb.append((char)ch);
                }
                r.close();
                is.close();
                query = sb.toString();
            }
        }
        catch (final Throwable e) {
            System.err.println("Cannot read query file: " + e.getMessage());
            System.exit(1);
            return;
        }
        if (verbose) {
            System.out.println("Compile Query:");
            System.out.println(query);
            System.out.println();
        }
        try {
            query = XmlBeans.compileQuery(query);
        }
        catch (final Exception e2) {
            System.err.println("Error compiling query: " + e2.getMessage());
            System.exit(1);
            return;
        }
        final File[] files = cl.getFiles();
        for (int j = 0; j < files.length; ++j) {
            XmlObject x;
            try {
                if (verbose) {
                    final InputStream is2 = new FileInputStream(files[j]);
                    while (true) {
                        final int ch = is2.read();
                        if (ch < 0) {
                            break;
                        }
                        System.out.write(ch);
                    }
                    is2.close();
                    System.out.println();
                }
                x = XmlObject.Factory.parse(files[j]);
            }
            catch (final Throwable e3) {
                System.err.println("Error parsing instance: " + e3.getMessage());
                System.exit(1);
                return;
            }
            if (verbose) {
                System.out.println("Executing Query...");
                System.err.println();
            }
            XmlObject[] result = null;
            try {
                result = x.execQuery(query);
            }
            catch (final Throwable e4) {
                System.err.println("Error executing query: " + e4.getMessage());
                System.exit(1);
                return;
            }
            if (verbose) {
                System.out.println("Query Result:");
            }
            final XmlOptions opts = new XmlOptions();
            opts.setSaveOuter();
            if (pretty) {
                opts.setSavePrettyPrint();
            }
            for (int k = 0; k < result.length; ++k) {
                result[k].save(System.out, opts);
                System.out.println();
            }
        }
    }
}
