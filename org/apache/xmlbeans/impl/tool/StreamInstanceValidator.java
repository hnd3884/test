package org.apache.xmlbeans.impl.tool;

import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.SchemaType;
import java.io.InputStream;
import java.io.FileInputStream;
import org.apache.xmlbeans.impl.common.StaxHelper;
import org.apache.xmlbeans.XmlOptionsBean;
import org.apache.xmlbeans.impl.validator.ValidatingXMLStreamReader;
import java.util.Iterator;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.util.List;
import java.io.File;
import java.util.Set;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class StreamInstanceValidator
{
    public static void printUsage() {
        System.out.println("Validates the specified instance against the specified schema.");
        System.out.println("A streaming validation useful for validating very large instance ");
        System.out.println("documents with less memory. Contrast with the validate tool.");
        System.out.println("Usage: svalidate [-dl] [-nopvr] [-noupa] [-license] schema.xsd instance.xml");
        System.out.println("Options:");
        System.out.println("    -dl - permit network downloads for imports and includes (default is off)");
        System.out.println("    -noupa - do not enforce the unique particle attribution rule");
        System.out.println("    -nopvr - do not enforce the particle valid (restriction) rule");
        System.out.println("    -license - prints license information");
    }
    
    public static void main(final String[] args) {
        final Set flags = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("dl");
        flags.add("noupr");
        flags.add("noupa");
        final CommandLine cl = new CommandLine(args, flags, Collections.EMPTY_SET);
        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null || args.length < 1) {
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
        final boolean dl = cl.getOpt("dl") != null;
        final boolean nopvr = cl.getOpt("nopvr") != null;
        final boolean noupa = cl.getOpt("noupa") != null;
        final File[] schemaFiles = cl.filesEndingWith(".xsd");
        final File[] instanceFiles = cl.filesEndingWith(".xml");
        final File[] jarFiles = cl.filesEndingWith(".jar");
        final List sdocs = new ArrayList();
        final XmlOptions options = new XmlOptions().setLoadLineNumbers();
        for (int j = 0; j < schemaFiles.length; ++j) {
            try {
                sdocs.add(XmlObject.Factory.parse(schemaFiles[j], options.setLoadMessageDigest()));
            }
            catch (final Exception e) {
                System.err.println(schemaFiles[j] + " not loadable: " + e);
            }
        }
        final XmlObject[] schemas = sdocs.toArray(new XmlObject[0]);
        SchemaTypeLoader sLoader = null;
        final Collection compErrors = new ArrayList();
        final XmlOptions schemaOptions = new XmlOptions();
        schemaOptions.setErrorListener(compErrors);
        if (dl) {
            schemaOptions.setCompileDownloadUrls();
        }
        if (nopvr) {
            schemaOptions.setCompileNoPvrRule();
        }
        if (noupa) {
            schemaOptions.setCompileNoUpaRule();
        }
        if (jarFiles != null && jarFiles.length > 0) {
            sLoader = XmlBeans.typeLoaderForResource(XmlBeans.resourceLoaderForPath(jarFiles));
        }
        try {
            if (schemas != null && schemas.length > 0) {
                sLoader = XmlBeans.compileXsd(schemas, sLoader, schemaOptions);
            }
        }
        catch (final Exception e2) {
            if (compErrors.isEmpty() || !(e2 instanceof XmlException)) {
                e2.printStackTrace(System.err);
            }
            System.out.println("Schema invalid");
            final Iterator k = compErrors.iterator();
            while (k.hasNext()) {
                System.out.println(k.next());
            }
            return;
        }
        validateFiles(instanceFiles, sLoader, options);
    }
    
    public static void validateFiles(final File[] instanceFiles, final SchemaTypeLoader sLoader, final XmlOptions options) {
        final ValidatingXMLStreamReader vsr = new ValidatingXMLStreamReader();
        final Collection errors = new ArrayList();
        for (int i = 0; i < instanceFiles.length; ++i) {
            final File file = instanceFiles[i];
            final String path = file.getPath();
            long time = 0L;
            errors.clear();
            try {
                final XMLInputFactory xmlInputFactory = StaxHelper.newXMLInputFactory(new XmlOptionsBean(options));
                final FileInputStream fis = new FileInputStream(file);
                final XMLStreamReader rdr = xmlInputFactory.createXMLStreamReader(path, fis);
                while (!rdr.isStartElement()) {
                    rdr.next();
                }
                time = System.currentTimeMillis();
                vsr.init(rdr, true, null, sLoader, options, errors);
                while (vsr.hasNext()) {
                    vsr.next();
                }
                time = System.currentTimeMillis() - time;
                vsr.close();
                fis.close();
            }
            catch (final XMLStreamException xse) {
                final Location loc = xse.getLocation();
                final XmlError e = XmlError.forLocation(xse.getMessage(), path, loc.getLineNumber(), loc.getColumnNumber(), loc.getCharacterOffset());
                errors.add(e);
            }
            catch (final Exception e2) {
                System.err.println("error for file: " + file + ": " + e2);
                e2.printStackTrace(System.err);
                continue;
            }
            if (errors.isEmpty()) {
                System.out.println(file + " valid. (" + time + " ms)");
            }
            else {
                System.out.println(file + " NOT valid (" + time + " ms):");
                for (final XmlError err : errors) {
                    System.out.println(stringFromError(err, path));
                }
            }
        }
    }
    
    private static String stringFromError(final XmlError err, final String path) {
        final String s = XmlError.severityAsString(err.getSeverity()) + ": " + path + ":" + err.getLine() + ":" + err.getColumn() + " " + err.getMessage() + " ";
        return s;
    }
}
