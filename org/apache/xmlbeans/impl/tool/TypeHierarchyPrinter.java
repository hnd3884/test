package org.apache.xmlbeans.impl.tool;

import java.util.Map;
import java.util.Iterator;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SchemaTypeLoader;
import java.util.List;
import java.io.File;
import java.util.Set;
import org.apache.xmlbeans.impl.common.QNameHelper;
import org.apache.xmlbeans.SchemaType;
import java.util.Arrays;
import java.util.HashMap;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.XmlBeans;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.XmlOptions;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;

public class TypeHierarchyPrinter
{
    public static void printUsage() {
        System.out.println("Prints the inheritance hierarchy of types defined in a schema.\n");
        System.out.println("Usage: xsdtree [-noanon] [-nopvr] [-noupa] [-partial] [-license] schemafile.xsd*");
        System.out.println("    -noanon - Don't include anonymous types in the tree.");
        System.out.println("    -noupa - do not enforce the unique particle attribution rule");
        System.out.println("    -nopvr - do not enforce the particle valid (restriction) rule");
        System.out.println("    -partial - Print only part of the hierarchy.");
        System.out.println("    -license - prints license information");
        System.out.println("    schemafile.xsd - File containing the schema for which to print a tree.");
        System.out.println();
    }
    
    public static void main(final String[] args) throws Exception {
        final Set flags = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("noanon");
        flags.add("noupr");
        flags.add("noupa");
        flags.add("partial");
        final CommandLine cl = new CommandLine(args, flags, Collections.EMPTY_SET);
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
        final boolean noanon = cl.getOpt("noanon") != null;
        final boolean nopvr = cl.getOpt("nopvr") != null;
        final boolean noupa = cl.getOpt("noupa") != null;
        final boolean partial = cl.getOpt("partial") != null;
        final File[] schemaFiles = cl.filesEndingWith(".xsd");
        final File[] jarFiles = cl.filesEndingWith(".jar");
        final List sdocs = new ArrayList();
        for (int j = 0; j < schemaFiles.length; ++j) {
            try {
                sdocs.add(SchemaDocument.Factory.parse(schemaFiles[j], new XmlOptions().setLoadLineNumbers()));
            }
            catch (final Exception e) {
                System.err.println(schemaFiles[j] + " not loadable: " + e);
            }
        }
        final XmlObject[] schemas = sdocs.toArray(new XmlObject[0]);
        SchemaTypeLoader linkTo = null;
        final Collection compErrors = new ArrayList();
        final XmlOptions schemaOptions = new XmlOptions();
        schemaOptions.setErrorListener(compErrors);
        schemaOptions.setCompileDownloadUrls();
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
            linkTo = XmlBeans.typeLoaderForResource(XmlBeans.resourceLoaderForPath(jarFiles));
        }
        SchemaTypeSystem typeSystem;
        try {
            typeSystem = XmlBeans.compileXsd(schemas, linkTo, schemaOptions);
        }
        catch (final XmlException e2) {
            System.out.println("Schema invalid:" + (partial ? " couldn't recover from errors" : ""));
            if (compErrors.isEmpty()) {
                System.out.println(e2.getMessage());
            }
            else {
                final Iterator k = compErrors.iterator();
                while (k.hasNext()) {
                    System.out.println(k.next());
                }
            }
            return;
        }
        if (partial && !compErrors.isEmpty()) {
            System.out.println("Schema invalid: partial schema type system recovered");
            final Iterator l = compErrors.iterator();
            while (l.hasNext()) {
                System.out.println(l.next());
            }
        }
        final Map prefixes = new HashMap();
        prefixes.put("http://www.w3.org/XML/1998/namespace", "xml");
        prefixes.put("http://www.w3.org/2001/XMLSchema", "xs");
        System.out.println("xmlns:xs=\"http://www.w3.org/2001/XMLSchema\"");
        final Map childTypes = new HashMap();
        final List allSeenTypes = new ArrayList();
        allSeenTypes.addAll(Arrays.asList(typeSystem.documentTypes()));
        allSeenTypes.addAll(Arrays.asList(typeSystem.attributeTypes()));
        allSeenTypes.addAll(Arrays.asList(typeSystem.globalTypes()));
        for (int m = 0; m < allSeenTypes.size(); ++m) {
            final SchemaType sType = allSeenTypes.get(m);
            if (!noanon) {
                allSeenTypes.addAll(Arrays.asList(sType.getAnonymousTypes()));
            }
            if (!sType.isDocumentType() && !sType.isAttributeType()) {
                if (sType != XmlObject.type) {
                    noteNamespace(prefixes, sType);
                    Collection children = childTypes.get(sType.getBaseType());
                    if (children == null) {
                        children = new ArrayList();
                        childTypes.put(sType.getBaseType(), children);
                        if (sType.getBaseType().isBuiltinType()) {
                            allSeenTypes.add(sType.getBaseType());
                        }
                    }
                    children.add(sType);
                }
            }
        }
        final List typesToPrint = new ArrayList();
        typesToPrint.add(XmlObject.type);
        final StringBuffer spaces = new StringBuffer();
        while (!typesToPrint.isEmpty()) {
            final SchemaType sType2 = typesToPrint.remove(typesToPrint.size() - 1);
            if (sType2 == null) {
                spaces.setLength(Math.max(0, spaces.length() - 2));
            }
            else {
                System.out.println((Object)spaces + "+-" + QNameHelper.readable(sType2, prefixes) + notes(sType2));
                final Collection children2 = childTypes.get(sType2);
                if (children2 == null || children2.size() <= 0) {
                    continue;
                }
                spaces.append((typesToPrint.size() == 0 || typesToPrint.get(typesToPrint.size() - 1) == null) ? "  " : "| ");
                typesToPrint.add(null);
                typesToPrint.addAll(children2);
            }
        }
    }
    
    private static String notes(final SchemaType sType) {
        if (sType.isBuiltinType()) {
            return " (builtin)";
        }
        if (sType.isSimpleType()) {
            switch (sType.getSimpleVariety()) {
                case 3: {
                    return " (list)";
                }
                case 2: {
                    return " (union)";
                }
                default: {
                    if (sType.getEnumerationValues() != null) {
                        return " (enumeration)";
                    }
                    return "";
                }
            }
        }
        else {
            switch (sType.getContentType()) {
                case 4: {
                    return " (mixed)";
                }
                case 2: {
                    return " (complex)";
                }
                default: {
                    return "";
                }
            }
        }
    }
    
    private static void noteNamespace(final Map prefixes, final SchemaType sType) {
        final String namespace = QNameHelper.namespace(sType);
        if (namespace.equals("") || prefixes.containsKey(namespace)) {
            return;
        }
        String result;
        final String base = result = QNameHelper.suggestPrefix(namespace);
        for (int n = 0; prefixes.containsValue(result); result = base + n, ++n) {}
        prefixes.put(namespace, result);
        System.out.println("xmlns:" + result + "=\"" + namespace + "\"");
    }
}
