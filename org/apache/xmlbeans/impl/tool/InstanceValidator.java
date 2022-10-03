package org.apache.xmlbeans.impl.tool;

import java.util.Iterator;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.util.List;
import java.io.File;
import java.util.Set;
import org.apache.xmlbeans.SchemaType;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class InstanceValidator
{
    public static void printUsage() {
        System.out.println("Validates the specified instance against the specified schema.");
        System.out.println("Contrast with the svalidate tool, which validates using a stream.");
        System.out.println("Usage: validate [-dl] [-nopvr] [-noupa] [-license] schema.xsd instance.xml");
        System.out.println("Options:");
        System.out.println("    -dl - permit network downloads for imports and includes (default is off)");
        System.out.println("    -noupa - do not enforce the unique particle attribution rule");
        System.out.println("    -nopvr - do not enforce the particle valid (restriction) rule");
        System.out.println("    -strict - performs strict(er) validation");
        System.out.println("    -partial - allow partial schema type system");
        System.out.println("    -license - prints license information");
    }
    
    public static void main(final String[] args) {
        System.exit(extraMain(args));
    }
    
    public static int extraMain(final String[] args) {
        final Set flags = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("dl");
        flags.add("noupa");
        flags.add("nopvr");
        flags.add("strict");
        flags.add("partial");
        final CommandLine cl = new CommandLine(args, flags, Collections.EMPTY_SET);
        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null || args.length < 1) {
            printUsage();
            return 0;
        }
        final String[] badopts = cl.getBadOpts();
        if (badopts.length > 0) {
            for (int i = 0; i < badopts.length; ++i) {
                System.out.println("Unrecognized option: " + badopts[i]);
            }
            printUsage();
            return 0;
        }
        if (cl.getOpt("license") != null) {
            CommandLine.printLicense();
            return 0;
        }
        if (cl.getOpt("version") != null) {
            CommandLine.printVersion();
            return 0;
        }
        if (cl.args().length == 0) {
            return 0;
        }
        final boolean dl = cl.getOpt("dl") != null;
        final boolean nopvr = cl.getOpt("nopvr") != null;
        final boolean noupa = cl.getOpt("noupa") != null;
        final boolean strict = cl.getOpt("strict") != null;
        final boolean partial = cl.getOpt("partial") != null;
        final File[] schemaFiles = cl.filesEndingWith(".xsd");
        final File[] instanceFiles = cl.filesEndingWith(".xml");
        final File[] jarFiles = cl.filesEndingWith(".jar");
        final List sdocs = new ArrayList();
        for (int j = 0; j < schemaFiles.length; ++j) {
            try {
                sdocs.add(XmlObject.Factory.parse(schemaFiles[j], new XmlOptions().setLoadLineNumbers().setLoadMessageDigest()));
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
        if (partial) {
            schemaOptions.put("COMPILE_PARTIAL_TYPESYSTEM");
        }
        if (jarFiles != null && jarFiles.length > 0) {
            sLoader = XmlBeans.typeLoaderForResource(XmlBeans.resourceLoaderForPath(jarFiles));
        }
        int returnCode = 0;
        try {
            if (schemas != null && schemas.length > 0) {
                sLoader = XmlBeans.compileXsd(schemas, sLoader, schemaOptions);
            }
        }
        catch (final Exception e2) {
            if (compErrors.isEmpty() || !(e2 instanceof XmlException)) {
                e2.printStackTrace(System.err);
            }
            System.out.println("Schema invalid:" + (partial ? " couldn't recover from errors" : ""));
            final Iterator k = compErrors.iterator();
            while (k.hasNext()) {
                System.out.println(k.next());
            }
            returnCode = 10;
            return returnCode;
        }
        if (partial && !compErrors.isEmpty()) {
            returnCode = 11;
            System.out.println("Schema invalid: partial schema type system recovered");
            final Iterator l = compErrors.iterator();
            while (l.hasNext()) {
                System.out.println(l.next());
            }
        }
        if (sLoader == null) {
            sLoader = XmlBeans.getContextTypeLoader();
        }
        for (int m = 0; m < instanceFiles.length; ++m) {
            XmlObject xobj;
            try {
                xobj = sLoader.parse(instanceFiles[m], null, new XmlOptions().setLoadLineNumbers("LOAD_LINE_NUMBERS_END_ELEMENT"));
            }
            catch (final Exception e3) {
                System.err.println(instanceFiles[m] + " not loadable: " + e3);
                e3.printStackTrace(System.err);
                continue;
            }
            final Collection errors = new ArrayList();
            if (xobj.schemaType() == XmlObject.type) {
                System.out.println(instanceFiles[m] + " NOT valid.  ");
                System.out.println("  Document type not found.");
            }
            else if (xobj.validate(strict ? new XmlOptions().setErrorListener(errors).setValidateStrict() : new XmlOptions().setErrorListener(errors))) {
                System.out.println(instanceFiles[m] + " valid.");
            }
            else {
                returnCode = 1;
                System.out.println(instanceFiles[m] + " NOT valid.");
                final Iterator it = errors.iterator();
                while (it.hasNext()) {
                    System.out.println(it.next());
                }
            }
        }
        return returnCode;
    }
}
