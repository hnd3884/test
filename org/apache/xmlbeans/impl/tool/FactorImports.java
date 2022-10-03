package org.apache.xmlbeans.impl.tool;

import javax.xml.namespace.QName;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument;
import java.util.Iterator;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedAttributeGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.NamedGroup;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelAttribute;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelElement;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelSimpleType;
import org.apache.xmlbeans.impl.xb.xsdschema.TopLevelComplexType;
import java.util.Map;
import java.util.Set;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.FormChoice;
import java.io.IOException;
import org.apache.xmlbeans.XmlException;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import java.util.HashMap;
import java.io.File;
import java.util.Collection;
import java.util.Arrays;
import java.util.HashSet;

public class FactorImports
{
    public static void printUsage() {
        System.out.println("Refactors a directory of XSD files to remove name conflicts.");
        System.out.println("Usage: sfactor [-import common.xsd] [-out outputdir] inputdir");
        System.out.println("    -import common.xsd - The XSD file to contain redundant ");
        System.out.println("                         definitions for importing.");
        System.out.println("    -out outputdir - The directory into which to place XSD ");
        System.out.println("                     files resulting from refactoring, ");
        System.out.println("                     plus a commonly imported common.xsd.");
        System.out.println("    inputdir - The directory containing the XSD files with");
        System.out.println("               redundant definitions.");
        System.out.println("    -license - Print license information.");
        System.out.println();
    }
    
    public static void main(String[] args) throws Exception {
        final Set flags = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        final CommandLine cl = new CommandLine(args, flags, Arrays.asList("import", "out"));
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
        args = cl.args();
        if (args.length != 1) {
            System.exit(0);
            return;
        }
        String commonName = cl.getOpt("import");
        if (commonName == null) {
            commonName = "common.xsd";
        }
        String out = cl.getOpt("out");
        if (out == null) {
            System.out.println("Using output directory 'out'");
            out = "out";
        }
        final File outdir = new File(out);
        final File basedir = new File(args[0]);
        final File[] files = cl.getFiles();
        final Map schemaDocs = new HashMap();
        final Set elementNames = new HashSet();
        final Set attributeNames = new HashSet();
        final Set typeNames = new HashSet();
        final Set modelGroupNames = new HashSet();
        final Set attrGroupNames = new HashSet();
        final Set dupeElementNames = new HashSet();
        final Set dupeAttributeNames = new HashSet();
        final Set dupeTypeNames = new HashSet();
        final Set dupeModelGroupNames = new HashSet();
        final Set dupeAttrGroupNames = new HashSet();
        final Set dupeNamespaces = new HashSet();
        for (int j = 0; j < files.length; ++j) {
            try {
                final SchemaDocument doc = SchemaDocument.Factory.parse(files[j]);
                schemaDocs.put(doc, files[j]);
                if (doc.getSchema().sizeOfImportArray() > 0 || doc.getSchema().sizeOfIncludeArray() > 0) {
                    System.out.println("warning: " + files[j] + " contains imports or includes that are being ignored.");
                }
                String targetNamespace = doc.getSchema().getTargetNamespace();
                if (targetNamespace == null) {
                    targetNamespace = "";
                }
                final TopLevelComplexType[] ct = doc.getSchema().getComplexTypeArray();
                for (int k = 0; k < ct.length; ++k) {
                    noteName(ct[k].getName(), targetNamespace, typeNames, dupeTypeNames, dupeNamespaces);
                }
                final TopLevelSimpleType[] st = doc.getSchema().getSimpleTypeArray();
                for (int l = 0; l < st.length; ++l) {
                    noteName(st[l].getName(), targetNamespace, typeNames, dupeTypeNames, dupeNamespaces);
                }
                final TopLevelElement[] el = doc.getSchema().getElementArray();
                for (int m = 0; m < el.length; ++m) {
                    noteName(el[m].getName(), targetNamespace, elementNames, dupeElementNames, dupeNamespaces);
                }
                final TopLevelAttribute[] at = doc.getSchema().getAttributeArray();
                for (int j2 = 0; j2 < at.length; ++j2) {
                    noteName(at[j2].getName(), targetNamespace, attributeNames, dupeAttributeNames, dupeNamespaces);
                }
                final NamedGroup[] gr = doc.getSchema().getGroupArray();
                for (int j3 = 0; j3 < gr.length; ++j3) {
                    noteName(gr[j3].getName(), targetNamespace, modelGroupNames, dupeModelGroupNames, dupeNamespaces);
                }
                final NamedAttributeGroup[] ag = doc.getSchema().getAttributeGroupArray();
                for (int j4 = 0; j4 < ag.length; ++j4) {
                    noteName(ag[j4].getName(), targetNamespace, attrGroupNames, dupeAttrGroupNames, dupeNamespaces);
                }
            }
            catch (final XmlException e) {
                System.out.println("warning: " + files[j] + " is not a schema file - " + e.getError().toString());
            }
            catch (final IOException e2) {
                System.err.println("Unable to load " + files[j] + " - " + e2.getMessage());
                System.exit(1);
                return;
            }
        }
        if (schemaDocs.size() == 0) {
            System.out.println("No schema files found.");
            System.exit(0);
            return;
        }
        if (dupeTypeNames.size() + dupeElementNames.size() + dupeAttributeNames.size() + dupeModelGroupNames.size() + dupeAttrGroupNames.size() == 0) {
            System.out.println("No duplicate names found.");
            System.exit(0);
            return;
        }
        final Map commonDocs = new HashMap();
        final Map commonFiles = new HashMap();
        int count = (dupeNamespaces.size() != 1) ? 1 : 0;
        for (final String namespace : dupeNamespaces) {
            final SchemaDocument commonDoc = SchemaDocument.Factory.parse("<xs:schema xmlns:xs='http://www.w3.org/2001/XMLSchema'/>");
            if (namespace.length() > 0) {
                commonDoc.getSchema().setTargetNamespace(namespace);
            }
            commonDoc.getSchema().setElementFormDefault(FormChoice.QUALIFIED);
            commonDocs.put(namespace, commonDoc);
            commonFiles.put(commonDoc, commonFileFor(commonName, namespace, count++, outdir));
        }
        for (final SchemaDocument doc2 : schemaDocs.keySet()) {
            String targetNamespace2 = doc2.getSchema().getTargetNamespace();
            if (targetNamespace2 == null) {
                targetNamespace2 = "";
            }
            final SchemaDocument commonDoc2 = commonDocs.get(targetNamespace2);
            boolean needImport = false;
            final TopLevelComplexType[] ct2 = doc2.getSchema().getComplexTypeArray();
            for (int j4 = ct2.length - 1; j4 >= 0; --j4) {
                if (isDuplicate(ct2[j4].getName(), targetNamespace2, dupeTypeNames)) {
                    if (isFirstDuplicate(ct2[j4].getName(), targetNamespace2, typeNames, dupeTypeNames)) {
                        commonDoc2.getSchema().addNewComplexType().set(ct2[j4]);
                    }
                    needImport = true;
                    doc2.getSchema().removeComplexType(j4);
                }
            }
            final TopLevelSimpleType[] st2 = doc2.getSchema().getSimpleTypeArray();
            for (int j5 = 0; j5 < st2.length; ++j5) {
                if (isDuplicate(st2[j5].getName(), targetNamespace2, dupeTypeNames)) {
                    if (isFirstDuplicate(st2[j5].getName(), targetNamespace2, typeNames, dupeTypeNames)) {
                        commonDoc2.getSchema().addNewSimpleType().set(st2[j5]);
                    }
                    needImport = true;
                    doc2.getSchema().removeSimpleType(j5);
                }
            }
            final TopLevelElement[] el2 = doc2.getSchema().getElementArray();
            for (int j6 = 0; j6 < el2.length; ++j6) {
                if (isDuplicate(el2[j6].getName(), targetNamespace2, dupeElementNames)) {
                    if (isFirstDuplicate(el2[j6].getName(), targetNamespace2, elementNames, dupeElementNames)) {
                        commonDoc2.getSchema().addNewElement().set(el2[j6]);
                    }
                    needImport = true;
                    doc2.getSchema().removeElement(j6);
                }
            }
            final TopLevelAttribute[] at2 = doc2.getSchema().getAttributeArray();
            for (int j7 = 0; j7 < at2.length; ++j7) {
                if (isDuplicate(at2[j7].getName(), targetNamespace2, dupeAttributeNames)) {
                    if (isFirstDuplicate(at2[j7].getName(), targetNamespace2, attributeNames, dupeAttributeNames)) {
                        commonDoc2.getSchema().addNewElement().set(at2[j7]);
                    }
                    needImport = true;
                    doc2.getSchema().removeElement(j7);
                }
            }
            final NamedGroup[] gr2 = doc2.getSchema().getGroupArray();
            for (int j8 = 0; j8 < gr2.length; ++j8) {
                if (isDuplicate(gr2[j8].getName(), targetNamespace2, dupeModelGroupNames)) {
                    if (isFirstDuplicate(gr2[j8].getName(), targetNamespace2, modelGroupNames, dupeModelGroupNames)) {
                        commonDoc2.getSchema().addNewElement().set(gr2[j8]);
                    }
                    needImport = true;
                    doc2.getSchema().removeElement(j8);
                }
            }
            final NamedAttributeGroup[] ag2 = doc2.getSchema().getAttributeGroupArray();
            for (int j9 = 0; j9 < ag2.length; ++j9) {
                if (isDuplicate(ag2[j9].getName(), targetNamespace2, dupeAttrGroupNames)) {
                    if (isFirstDuplicate(ag2[j9].getName(), targetNamespace2, attrGroupNames, dupeAttrGroupNames)) {
                        commonDoc2.getSchema().addNewElement().set(ag2[j9]);
                    }
                    needImport = true;
                    doc2.getSchema().removeElement(j9);
                }
            }
            if (needImport) {
                final IncludeDocument.Include newInclude = doc2.getSchema().addNewInclude();
                final File inputFile = schemaDocs.get(doc2);
                final File outputFile = outputFileFor(inputFile, basedir, outdir);
                final File commonFile = commonFiles.get(commonDoc2);
                if (targetNamespace2 == null) {
                    continue;
                }
                newInclude.setSchemaLocation(relativeURIFor(outputFile, commonFile));
            }
        }
        if (!outdir.isDirectory() && !outdir.mkdirs()) {
            System.err.println("Unable to makedir " + outdir);
            System.exit(1);
            return;
        }
        for (final SchemaDocument doc2 : schemaDocs.keySet()) {
            final File inputFile2 = schemaDocs.get(doc2);
            final File outputFile2 = outputFileFor(inputFile2, basedir, outdir);
            if (outputFile2 == null) {
                System.out.println("Cannot copy " + inputFile2);
            }
            else {
                doc2.save(outputFile2, new XmlOptions().setSavePrettyPrint().setSaveAggresiveNamespaces());
            }
        }
        for (final SchemaDocument doc2 : commonFiles.keySet()) {
            final File outputFile3 = commonFiles.get(doc2);
            doc2.save(outputFile3, new XmlOptions().setSavePrettyPrint().setSaveAggresiveNamespaces());
        }
    }
    
    private static File outputFileFor(final File file, final File baseDir, final File outdir) {
        final URI base = baseDir.getAbsoluteFile().toURI();
        final URI abs = file.getAbsoluteFile().toURI();
        final URI rel = base.relativize(abs);
        if (rel.isAbsolute()) {
            System.out.println("Cannot relativize " + file);
            return null;
        }
        final URI outbase = outdir.toURI();
        final URI out = CodeGenUtil.resolve(outbase, rel);
        return new File(out);
    }
    
    private static URI commonAncestor(final URI first, final URI second) {
        final String firstStr = first.toString();
        final String secondStr = second.toString();
        int len = firstStr.length();
        if (secondStr.length() < len) {
            len = secondStr.length();
        }
        int i;
        for (i = 0; i < len && firstStr.charAt(i) == secondStr.charAt(i); ++i) {}
        if (--i >= 0) {
            i = firstStr.lastIndexOf(47, i);
        }
        if (i < 0) {
            return null;
        }
        try {
            return new URI(firstStr.substring(0, i));
        }
        catch (final URISyntaxException e) {
            return null;
        }
    }
    
    private static String relativeURIFor(final File source, final File target) {
        final URI base = source.getAbsoluteFile().toURI();
        final URI abs = target.getAbsoluteFile().toURI();
        final URI commonBase = commonAncestor(base, abs);
        if (commonBase == null) {
            return abs.toString();
        }
        final URI baserel = commonBase.relativize(base);
        final URI targetrel = commonBase.relativize(abs);
        if (baserel.isAbsolute() || targetrel.isAbsolute()) {
            return abs.toString();
        }
        String prefix = "";
        final String sourceRel = baserel.toString();
        for (int i = 0; i < sourceRel.length(); ++i) {
            i = sourceRel.indexOf(47, i);
            if (i < 0) {
                break;
            }
            prefix += "../";
        }
        return prefix + targetrel.toString();
    }
    
    private static File commonFileFor(final String commonName, final String namespace, final int i, final File outdir) {
        String name = commonName;
        if (i > 0) {
            int index = commonName.lastIndexOf(46);
            if (index < 0) {
                index = commonName.length();
            }
            name = commonName.substring(0, index) + i + commonName.substring(index);
        }
        return new File(outdir, name);
    }
    
    private static void noteName(final String name, final String targetNamespace, final Set seen, final Set dupes, final Set dupeNamespaces) {
        if (name == null) {
            return;
        }
        final QName qName = new QName(targetNamespace, name);
        if (seen.contains(qName)) {
            dupes.add(qName);
            dupeNamespaces.add(targetNamespace);
        }
        else {
            seen.add(qName);
        }
    }
    
    private static boolean isFirstDuplicate(final String name, final String targetNamespace, final Set notseen, final Set dupes) {
        if (name == null) {
            return false;
        }
        final QName qName = new QName(targetNamespace, name);
        if (dupes.contains(qName) && notseen.contains(qName)) {
            notseen.remove(qName);
            return true;
        }
        return false;
    }
    
    private static boolean isDuplicate(final String name, final String targetNamespace, final Set dupes) {
        if (name == null) {
            return false;
        }
        final QName qName = new QName(targetNamespace, name);
        return dupes.contains(qName);
    }
}
