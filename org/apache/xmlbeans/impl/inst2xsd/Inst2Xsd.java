package org.apache.xmlbeans.impl.inst2xsd;

import java.util.Iterator;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlError;
import org.apache.xmlbeans.XmlBeans;
import java.util.ArrayList;
import org.apache.xmlbeans.impl.inst2xsd.util.TypeSystemHolder;
import java.io.Reader;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import java.util.Set;
import org.apache.xmlbeans.XmlOptions;
import java.io.IOException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlObject;
import java.io.File;
import java.util.Collection;
import org.apache.xmlbeans.impl.tool.CommandLine;
import java.util.HashSet;

public class Inst2Xsd
{
    public static void main(final String[] args) {
        if (args == null || args.length == 0) {
            printHelp();
            System.exit(0);
            return;
        }
        final Set flags = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("verbose");
        flags.add("validate");
        final Set opts = new HashSet();
        opts.add("design");
        opts.add("simple-content-types");
        opts.add("enumerations");
        opts.add("outDir");
        opts.add("outPrefix");
        final CommandLine cl = new CommandLine(args, flags, opts);
        final Inst2XsdOptions inst2XsdOptions = new Inst2XsdOptions();
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
        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null) {
            printHelp();
            System.exit(0);
            return;
        }
        final String[] badopts = cl.getBadOpts();
        if (badopts.length > 0) {
            for (int i = 0; i < badopts.length; ++i) {
                System.out.println("Unrecognized option: " + badopts[i]);
            }
            printHelp();
            System.exit(0);
            return;
        }
        final String design = cl.getOpt("design");
        if (design != null) {
            if (design.equals("vb")) {
                inst2XsdOptions.setDesign(3);
            }
            else if (design.equals("rd")) {
                inst2XsdOptions.setDesign(1);
            }
            else {
                if (!design.equals("ss")) {
                    printHelp();
                    System.exit(0);
                    return;
                }
                inst2XsdOptions.setDesign(2);
            }
        }
        final String simpleContent = cl.getOpt("simple-content-types");
        if (simpleContent != null) {
            if (simpleContent.equals("smart")) {
                inst2XsdOptions.setSimpleContentTypes(1);
            }
            else {
                if (!simpleContent.equals("string")) {
                    printHelp();
                    System.exit(0);
                    return;
                }
                inst2XsdOptions.setSimpleContentTypes(2);
            }
        }
        final String enumerations = cl.getOpt("enumerations");
        if (enumerations != null) {
            if (enumerations.equals("never")) {
                inst2XsdOptions.setUseEnumerations(1);
            }
            else {
                try {
                    final int intVal = Integer.parseInt(enumerations);
                    inst2XsdOptions.setUseEnumerations(intVal);
                }
                catch (final NumberFormatException e) {
                    printHelp();
                    System.exit(0);
                    return;
                }
            }
        }
        final File outDir = new File((cl.getOpt("outDir") == null) ? "." : cl.getOpt("outDir"));
        String outPrefix = cl.getOpt("outPrefix");
        if (outPrefix == null) {
            outPrefix = "schema";
        }
        inst2XsdOptions.setVerbose(cl.getOpt("verbose") != null);
        final boolean validate = cl.getOpt("validate") != null;
        final File[] xmlFiles = cl.filesEndingWith(".xml");
        final XmlObject[] xmlInstances = new XmlObject[xmlFiles.length];
        if (xmlInstances.length == 0) {
            printHelp();
            System.exit(0);
            return;
        }
        int j = 0;
        try {
            for (j = 0; j < xmlFiles.length; ++j) {
                xmlInstances[j] = XmlObject.Factory.parse(xmlFiles[j]);
            }
        }
        catch (final XmlException e2) {
            System.err.println("Invalid xml file: '" + xmlFiles[j].getName() + "'. " + e2.getMessage());
            return;
        }
        catch (final IOException e3) {
            System.err.println("Could not read file: '" + xmlFiles[j].getName() + "'. " + e3.getMessage());
            return;
        }
        final SchemaDocument[] schemaDocs = inst2xsd(xmlInstances, inst2XsdOptions);
        try {
            for (j = 0; j < schemaDocs.length; ++j) {
                final SchemaDocument schema = schemaDocs[j];
                if (inst2XsdOptions.isVerbose()) {
                    System.out.println("----------------------\n\n" + schema);
                }
                schema.save(new File(outDir, outPrefix + j + ".xsd"), new XmlOptions().setSavePrettyPrint());
            }
        }
        catch (final IOException e4) {
            System.err.println("Could not write file: '" + outDir + File.pathSeparator + outPrefix + j + ".xsd" + "'. " + e4.getMessage());
            return;
        }
        if (validate) {
            validateInstances(schemaDocs, xmlInstances);
        }
    }
    
    private static void printHelp() {
        System.out.println("Generates XMLSchema from instance xml documents.");
        System.out.println("Usage: inst2xsd [opts] [instance.xml]*");
        System.out.println("Options include:");
        System.out.println("    -design [rd|ss|vb] - XMLSchema design type");
        System.out.println("             rd  - Russian Doll Design - local elements and local types");
        System.out.println("             ss  - Salami Slice Design - global elements and local types");
        System.out.println("             vb  - Venetian Blind Design (default) - local elements and global complex types");
        System.out.println("    -simple-content-types [smart|string] - Simple content types detection (leaf text). Smart is the default");
        System.out.println("    -enumerations [never|NUMBER] - Use enumerations. Default value is 10.");
        System.out.println("    -outDir [dir] - Directory for output files. Default is '.'");
        System.out.println("    -outPrefix [file_name_prefix] - Prefix for output file names. Default is 'schema'");
        System.out.println("    -validate - Validates input instances agaist generated schemas.");
        System.out.println("    -verbose - print more informational messages");
        System.out.println("    -license - print license information");
        System.out.println("    -help - help imformation");
    }
    
    private Inst2Xsd() {
    }
    
    public static SchemaDocument[] inst2xsd(final Reader[] instReaders, final Inst2XsdOptions options) throws IOException, XmlException {
        final XmlObject[] instances = new XmlObject[instReaders.length];
        for (int i = 0; i < instReaders.length; ++i) {
            instances[i] = XmlObject.Factory.parse(instReaders[i]);
        }
        return inst2xsd(instances, options);
    }
    
    public static SchemaDocument[] inst2xsd(final XmlObject[] instances, Inst2XsdOptions options) {
        if (options == null) {
            options = new Inst2XsdOptions();
        }
        final TypeSystemHolder typeSystemHolder = new TypeSystemHolder();
        XsdGenStrategy strategy = null;
        switch (options.getDesign()) {
            case 1: {
                strategy = new RussianDollStrategy();
                break;
            }
            case 2: {
                strategy = new SalamiSliceStrategy();
                break;
            }
            case 3: {
                strategy = new VenetianBlindStrategy();
                break;
            }
            default: {
                throw new IllegalArgumentException("Unknown design.");
            }
        }
        strategy.processDoc(instances, options, typeSystemHolder);
        if (options.isVerbose()) {
            System.out.println("typeSystemHolder.toString(): " + typeSystemHolder);
        }
        final SchemaDocument[] sDocs = typeSystemHolder.getSchemaDocuments();
        return sDocs;
    }
    
    private static boolean validateInstances(final SchemaDocument[] sDocs, final XmlObject[] instances) {
        final Collection compErrors = new ArrayList();
        final XmlOptions schemaOptions = new XmlOptions();
        schemaOptions.setErrorListener(compErrors);
        SchemaTypeLoader sLoader;
        try {
            sLoader = XmlBeans.loadXsd(sDocs, schemaOptions);
        }
        catch (final Exception e) {
            if (compErrors.isEmpty() || !(e instanceof XmlException)) {
                e.printStackTrace(System.out);
            }
            System.out.println("\n-------------------\n\nInvalid schemas.");
            for (final XmlError xe : compErrors) {
                System.out.println(xe.getLine() + ":" + xe.getColumn() + " " + xe.getMessage());
            }
            return false;
        }
        System.out.println("\n-------------------");
        boolean result = true;
        for (int i = 0; i < instances.length; ++i) {
            XmlObject xobj;
            try {
                xobj = sLoader.parse(instances[i].newXMLStreamReader(), null, new XmlOptions().setLoadLineNumbers());
            }
            catch (final XmlException e2) {
                System.out.println("Error:\n" + instances[i].documentProperties().getSourceName() + " not loadable: " + e2);
                e2.printStackTrace(System.out);
                result = false;
                continue;
            }
            final Collection errors2 = new ArrayList();
            if (xobj.schemaType() == XmlObject.type) {
                System.out.println(instances[i].documentProperties().getSourceName() + " NOT valid.  ");
                System.out.println("  Document type not found.");
                result = false;
            }
            else if (xobj.validate(new XmlOptions().setErrorListener(errors2))) {
                System.out.println("Instance[" + i + "] valid - " + instances[i].documentProperties().getSourceName());
            }
            else {
                System.out.println("Instance[" + i + "] NOT valid - " + instances[i].documentProperties().getSourceName());
                for (final XmlError xe2 : errors2) {
                    System.out.println(xe2.getLine() + ":" + xe2.getColumn() + " " + xe2.getMessage());
                }
                result = false;
            }
        }
        return result;
    }
}
