package org.apache.xmlbeans.impl.xsd2inst;

import org.apache.xmlbeans.SchemaType;
import java.util.Iterator;
import org.apache.xmlbeans.SchemaTypeSystem;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlBeans;
import java.io.IOException;
import org.apache.xmlbeans.XmlException;
import java.io.StringReader;
import java.io.Reader;
import java.util.List;
import java.io.File;
import java.util.Set;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.XmlOptions;
import java.util.ArrayList;
import java.util.Collection;
import org.apache.xmlbeans.impl.tool.CommandLine;
import java.util.HashSet;

public class SchemaInstanceGenerator
{
    public static void printUsage() {
        System.out.println("Generates a document based on the given Schema file");
        System.out.println("having the given element as root.");
        System.out.println("The tool makes reasonable attempts to create a valid document,");
        System.out.println("but this is not always possible since, for example, ");
        System.out.println("there are schemas for which no valid instance document ");
        System.out.println("can be produced.");
        System.out.println("Usage: xsd2inst [flags] schema.xsd -name element_name");
        System.out.println("Flags:");
        System.out.println("    -name    the name of the root element");
        System.out.println("    -dl      enable network downloads for imports and includes");
        System.out.println("    -nopvr   disable particle valid (restriction) rule");
        System.out.println("    -noupa   disable unique particle attribution rule");
        System.out.println("    -license prints license information");
        System.out.println("    -version prints version information");
    }
    
    public static void main(final String[] args) {
        final Set flags = new HashSet();
        final Set opts = new HashSet();
        flags.add("h");
        flags.add("help");
        flags.add("usage");
        flags.add("license");
        flags.add("version");
        flags.add("dl");
        flags.add("noupa");
        flags.add("nopvr");
        flags.add("partial");
        opts.add("name");
        final CommandLine cl = new CommandLine(args, flags, opts);
        if (cl.getOpt("h") != null || cl.getOpt("help") != null || cl.getOpt("usage") != null) {
            printUsage();
            return;
        }
        final String[] badOpts = cl.getBadOpts();
        if (badOpts.length > 0) {
            for (int i = 0; i < badOpts.length; ++i) {
                System.out.println("Unrecognized option: " + badOpts[i]);
            }
            printUsage();
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
        final boolean dl = cl.getOpt("dl") != null;
        final boolean nopvr = cl.getOpt("nopvr") != null;
        final boolean noupa = cl.getOpt("noupa") != null;
        final File[] schemaFiles = cl.filesEndingWith(".xsd");
        final String rootName = cl.getOpt("name");
        if (rootName == null) {
            System.out.println("Required option \"-name\" must be present");
            return;
        }
        final List sdocs = new ArrayList();
        for (int j = 0; j < schemaFiles.length; ++j) {
            try {
                sdocs.add(XmlObject.Factory.parse(schemaFiles[j], new XmlOptions().setLoadLineNumbers().setLoadMessageDigest()));
            }
            catch (final Exception e) {
                System.err.println("Can not load schema file: " + schemaFiles[j] + ": ");
                e.printStackTrace();
            }
        }
        final XmlObject[] schemas = sdocs.toArray(new XmlObject[sdocs.size()]);
        final Xsd2InstOptions options = new Xsd2InstOptions();
        options.setNetworkDownloads(dl);
        options.setNopvr(nopvr);
        options.setNoupa(noupa);
        final String result = xsd2inst(schemas, rootName, options);
        System.out.println(result);
    }
    
    public static String xsd2inst(final String[] xsds, final String rootName, final Xsd2InstOptions options) throws XmlException, IOException {
        final Reader[] instReaders = new Reader[xsds.length];
        for (int i = 0; i < xsds.length; ++i) {
            instReaders[i] = new StringReader(xsds[i]);
        }
        final String res = xsd2inst(instReaders, rootName, options);
        return res;
    }
    
    public static String xsd2inst(final Reader[] schemaReaders, final String rootName, final Xsd2InstOptions options) {
        final List sdocs = new ArrayList();
        for (int i = 0; i < schemaReaders.length; ++i) {
            try {
                sdocs.add(XmlObject.Factory.parse(schemaReaders[i], new XmlOptions().setLoadLineNumbers().setLoadMessageDigest()));
            }
            catch (final Exception e) {
                System.err.println("Can not load schema reader: " + i + "  " + schemaReaders[i] + ": ");
                e.printStackTrace();
            }
        }
        final XmlObject[] schemas = sdocs.toArray(new XmlObject[sdocs.size()]);
        return xsd2inst(schemas, rootName, options);
    }
    
    public static String xsd2inst(final XmlObject[] schemas, final String rootName, final Xsd2InstOptions options) {
        SchemaTypeSystem sts = null;
        if (schemas.length > 0) {
            final Collection errors = new ArrayList();
            final XmlOptions compileOptions = new XmlOptions();
            if (options.isNetworkDownloads()) {
                compileOptions.setCompileDownloadUrls();
            }
            if (options.isNopvr()) {
                compileOptions.setCompileNoPvrRule();
            }
            if (options.isNoupa()) {
                compileOptions.setCompileNoUpaRule();
            }
            try {
                sts = XmlBeans.compileXsd(schemas, XmlBeans.getBuiltinTypeSystem(), compileOptions);
            }
            catch (final Exception e) {
                if (errors.isEmpty() || !(e instanceof XmlException)) {
                    e.printStackTrace();
                }
                System.out.println("Schema compilation errors: ");
                final Iterator i = errors.iterator();
                while (i.hasNext()) {
                    System.out.println(i.next());
                }
            }
        }
        if (sts == null) {
            throw new RuntimeException("No Schemas to process.");
        }
        final SchemaType[] globalElems = sts.documentTypes();
        SchemaType elem = null;
        for (int j = 0; j < globalElems.length; ++j) {
            if (rootName.equals(globalElems[j].getDocumentElementName().getLocalPart())) {
                elem = globalElems[j];
                break;
            }
        }
        if (elem == null) {
            throw new RuntimeException("Could not find a global element with name \"" + rootName + "\"");
        }
        final String result = SampleXmlUtil.createSampleForType(elem);
        return result;
    }
    
    public static class Xsd2InstOptions
    {
        private boolean _downloads;
        private boolean _nopvr;
        private boolean _noupa;
        
        public Xsd2InstOptions() {
            this._downloads = false;
            this._nopvr = false;
            this._noupa = false;
        }
        
        public boolean isNetworkDownloads() {
            return this._downloads;
        }
        
        public void setNetworkDownloads(final boolean downloads) {
            this._downloads = downloads;
        }
        
        public boolean isNopvr() {
            return this._nopvr;
        }
        
        public void setNopvr(final boolean nopvr) {
            this._nopvr = nopvr;
        }
        
        public boolean isNoupa() {
            return this._noupa;
        }
        
        public void setNoupa(final boolean noupa) {
            this._noupa = noupa;
        }
    }
}
