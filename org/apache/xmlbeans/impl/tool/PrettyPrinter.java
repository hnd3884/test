package org.apache.xmlbeans.impl.tool;

import org.apache.xmlbeans.XmlException;
import java.io.Writer;
import java.io.StringWriter;
import java.io.File;
import java.util.Set;
import java.io.IOException;
import java.io.OutputStream;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class PrettyPrinter
{
    private static final int DEFAULT_INDENT = 2;
    
    public static void printUsage() {
        System.out.println("Pretty prints XML files.");
        System.out.println("Usage: xpretty [switches] file.xml");
        System.out.println("Switches:");
        System.out.println("    -indent #   use the given indent");
        System.out.println("    -license prints license information");
    }
    
    public static void main(final String[] args) {
        final Set flags = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        final CommandLine cl = new CommandLine(args, flags, Collections.singleton("indent"));
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
        if (cl.args().length == 0) {
            printUsage();
            return;
        }
        final String indentStr = cl.getOpt("indent");
        int indent;
        if (indentStr == null) {
            indent = 2;
        }
        else {
            indent = Integer.parseInt(indentStr);
        }
        final File[] files = cl.getFiles();
        for (int j = 0; j < files.length; ++j) {
            XmlObject doc;
            try {
                doc = XmlObject.Factory.parse(files[j], new XmlOptions().setLoadLineNumbers());
            }
            catch (final Exception e) {
                System.err.println(files[j] + " not loadable: " + e.getMessage());
                continue;
            }
            try {
                doc.save(System.out, new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(indent));
            }
            catch (final IOException e2) {
                System.err.println("Unable to pretty print " + files[j] + ": " + e2.getMessage());
            }
        }
    }
    
    public static String indent(final String xmldoc) throws IOException, XmlException {
        final StringWriter sw = new StringWriter();
        final XmlObject doc = XmlObject.Factory.parse(xmldoc, new XmlOptions().setLoadLineNumbers());
        doc.save(sw, new XmlOptions().setSavePrettyPrint().setSavePrettyPrintIndent(2));
        sw.close();
        return sw.getBuffer().toString();
    }
}
