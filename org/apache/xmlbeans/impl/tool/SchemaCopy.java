package org.apache.xmlbeans.impl.tool;

import org.apache.xmlbeans.impl.xb.substwsdl.TImport;
import org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;
import java.net.URISyntaxException;
import org.apache.xmlbeans.XmlCursor;
import java.net.URL;
import java.util.Collections;
import org.apache.xmlbeans.impl.xb.substwsdl.DefinitionsDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.XmlObject;
import java.util.LinkedList;
import java.util.LinkedHashMap;
import java.util.Iterator;
import org.apache.xmlbeans.impl.common.IOUtil;
import java.util.Map;
import java.io.File;
import java.net.URI;
import org.apache.xmlbeans.XmlOptions;

public class SchemaCopy
{
    private static final XmlOptions loadOptions;
    
    public static void printUsage() {
        System.out.println("Copies the XML schema at the specified URL to the specified file.");
        System.out.println("Usage: scopy sourceurl [targetfile]");
        System.out.println("    sourceurl - The URL at which the schema is located.");
        System.out.println("    targetfile - The file to which the schema should be copied.");
        System.out.println();
    }
    
    public static void main(final String[] args) {
        if (args.length < 1 || args.length > 2) {
            printUsage();
            return;
        }
        URI source = null;
        URI target = null;
        try {
            if (args[0].compareToIgnoreCase("-usage") == 0) {
                printUsage();
                return;
            }
            source = new URI(args[0]);
            source.toURL();
        }
        catch (final Exception e) {
            System.err.println("Badly formed URL " + source);
            return;
        }
        Label_0223: {
            if (args.length < 2) {
                try {
                    final URI dir = new File(".").getCanonicalFile().toURI();
                    String lastPart = source.getPath();
                    lastPart = lastPart.substring(lastPart.lastIndexOf(47) + 1);
                    target = CodeGenUtil.resolve(dir, URI.create(lastPart));
                    break Label_0223;
                }
                catch (final Exception e) {
                    System.err.println("Cannot canonicalize current directory");
                    return;
                }
            }
            try {
                target = new URI(args[1]);
                if (!target.isAbsolute()) {
                    target = null;
                }
                else if (!target.getScheme().equals("file")) {
                    target = null;
                }
            }
            catch (final Exception e) {
                target = null;
            }
            if (target == null) {
                try {
                    target = new File(target).getCanonicalFile().toURI();
                }
                catch (final Exception e) {
                    System.err.println("Cannot canonicalize current directory");
                    return;
                }
            }
        }
        final Map thingsToCopy = findAllRelative(source, target);
        copyAll(thingsToCopy, true);
    }
    
    private static void copyAll(final Map uriMap, final boolean stdout) {
        for (final URI source : uriMap.keySet()) {
            final URI target = uriMap.get(source);
            try {
                IOUtil.copyCompletely(source, target);
            }
            catch (final Exception e) {
                if (!stdout) {
                    continue;
                }
                System.out.println("Could not copy " + source + " -> " + target);
                continue;
            }
            if (stdout) {
                System.out.println("Copied " + source + " -> " + target);
            }
        }
    }
    
    public static Map findAllRelative(final URI source, final URI target) {
        final Map result = new LinkedHashMap();
        result.put(source, target);
        final LinkedList process = new LinkedList();
        process.add(source);
        while (!process.isEmpty()) {
            final URI nextSource = process.removeFirst();
            final URI nextTarget = result.get(nextSource);
            final Map nextResults = findRelativeInOne(nextSource, nextTarget);
            for (final URI newSource : nextResults.keySet()) {
                if (result.containsKey(newSource)) {
                    continue;
                }
                result.put(newSource, nextResults.get(newSource));
                process.add(newSource);
            }
        }
        return result;
    }
    
    private static Map findRelativeInOne(final URI source, final URI target) {
        try {
            final URL sourceURL = source.toURL();
            final XmlObject xobj = XmlObject.Factory.parse(sourceURL, SchemaCopy.loadOptions);
            final XmlCursor xcur = xobj.newCursor();
            xcur.toFirstChild();
            final Map result = new LinkedHashMap();
            if (xobj instanceof SchemaDocument) {
                putMappingsFromSchema(result, source, target, ((SchemaDocument)xobj).getSchema());
            }
            else if (xobj instanceof DefinitionsDocument) {
                putMappingsFromWsdl(result, source, target, ((DefinitionsDocument)xobj).getDefinitions());
            }
            return result;
        }
        catch (final Exception e) {
            return Collections.EMPTY_MAP;
        }
    }
    
    private static void putNewMapping(final Map result, final URI origSource, final URI origTarget, final String literalURI) {
        try {
            if (literalURI == null) {
                return;
            }
            final URI newRelative = new URI(literalURI);
            if (newRelative.isAbsolute()) {
                return;
            }
            final URI newSource = CodeGenUtil.resolve(origSource, newRelative);
            final URI newTarget = CodeGenUtil.resolve(origTarget, newRelative);
            result.put(newSource, newTarget);
        }
        catch (final URISyntaxException ex) {}
    }
    
    private static void putMappingsFromSchema(final Map result, final URI source, final URI target, final SchemaDocument.Schema schema) {
        final ImportDocument.Import[] imports = schema.getImportArray();
        for (int i = 0; i < imports.length; ++i) {
            putNewMapping(result, source, target, imports[i].getSchemaLocation());
        }
        final IncludeDocument.Include[] includes = schema.getIncludeArray();
        for (int j = 0; j < includes.length; ++j) {
            putNewMapping(result, source, target, includes[j].getSchemaLocation());
        }
    }
    
    private static void putMappingsFromWsdl(final Map result, final URI source, final URI target, final DefinitionsDocument.Definitions wdoc) {
        final XmlObject[] types = wdoc.getTypesArray();
        for (int i = 0; i < types.length; ++i) {
            final SchemaDocument.Schema[] schemas = (SchemaDocument.Schema[])types[i].selectPath("declare namespace xs='http://www.w3.org/2001/XMLSchema' xs:schema");
            for (int j = 0; j < schemas.length; ++j) {
                putMappingsFromSchema(result, source, target, schemas[j]);
            }
        }
        final TImport[] imports = wdoc.getImportArray();
        for (int k = 0; k < imports.length; ++k) {
            putNewMapping(result, source, target, imports[k].getLocation());
        }
    }
    
    static {
        loadOptions = new XmlOptions().setLoadSubstituteNamespaces(Collections.singletonMap("http://schemas.xmlsoap.org/wsdl/", "http://www.apache.org/internal/xmlbeans/wsdlsubst"));
    }
}
