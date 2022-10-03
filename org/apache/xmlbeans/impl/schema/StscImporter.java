package org.apache.xmlbeans.impl.schema;

import java.util.Arrays;
import java.io.CharArrayWriter;
import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.ByteArrayInputStream;
import java.io.Writer;
import java.io.CharArrayReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.FileOutputStream;
import org.apache.xmlbeans.impl.common.XmlEncodingSniffer;
import org.apache.xmlbeans.impl.common.IOUtil;
import java.io.File;
import org.apache.xmlbeans.impl.xb.xsdschema.IncludeDocument;
import org.apache.xmlbeans.impl.xb.xsdschema.ImportDocument;
import java.util.HashMap;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.xml.sax.EntityResolver;
import java.net.URL;
import org.apache.xmlbeans.SchemaType;
import org.xml.sax.SAXException;
import org.apache.xmlbeans.SchemaTypeLoader;
import org.apache.xmlbeans.XmlException;
import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.xmlbeans.XmlOptions;
import java.util.LinkedList;
import java.util.Map;
import java.util.Iterator;
import java.util.HashSet;
import org.apache.xmlbeans.impl.xb.xsdschema.RedefineDocument;
import java.util.ArrayList;
import java.util.Set;
import java.util.List;
import java.net.URISyntaxException;
import java.net.URI;
import org.apache.xmlbeans.XmlObject;
import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;

public class StscImporter
{
    private static final String PROJECT_URL_PREFIX = "project://local";
    
    public static SchemaToProcess[] resolveImportsAndIncludes(final SchemaDocument.Schema[] startWith, final boolean forceSrcSave) {
        final DownloadTable engine = new DownloadTable(startWith);
        return engine.resolveImportsAndIncludes(forceSrcSave);
    }
    
    private static String baseURLForDoc(final XmlObject obj) {
        final String path = obj.documentProperties().getSourceName();
        if (path == null) {
            return null;
        }
        if (path.startsWith("/")) {
            return "project://local" + path.replace('\\', '/');
        }
        final int colon = path.indexOf(58);
        if (colon > 1 && path.substring(0, colon).matches("^\\w+$")) {
            return path;
        }
        return "project://local/" + path.replace('\\', '/');
    }
    
    private static URI parseURI(final String s) {
        if (s == null) {
            return null;
        }
        try {
            return new URI(s);
        }
        catch (final URISyntaxException syntax) {
            return null;
        }
    }
    
    public static URI resolve(final URI base, final String child) throws URISyntaxException {
        final URI childUri = new URI(child);
        URI ruri = base.resolve(childUri);
        if (childUri.equals(ruri) && !childUri.isAbsolute() && (base.getScheme().equals("jar") || base.getScheme().equals("zip"))) {
            String r = base.toString();
            final int lastslash = r.lastIndexOf(47);
            r = r.substring(0, lastslash) + "/" + childUri;
            final int exclPointSlashIndex = r.lastIndexOf("!/");
            if (exclPointSlashIndex > 0) {
                for (int slashDotDotIndex = r.indexOf("/..", exclPointSlashIndex); slashDotDotIndex > 0; slashDotDotIndex = r.indexOf("/..", exclPointSlashIndex)) {
                    final int prevSlashIndex = r.lastIndexOf("/", slashDotDotIndex - 1);
                    if (prevSlashIndex >= exclPointSlashIndex) {
                        final String temp = r.substring(slashDotDotIndex + 3);
                        r = r.substring(0, prevSlashIndex).concat(temp);
                    }
                }
            }
            return URI.create(r);
        }
        if ("file".equals(ruri.getScheme()) && !child.equals(ruri) && base.getPath().startsWith("//") && !ruri.getPath().startsWith("//")) {
            final String path = "///".concat(ruri.getPath());
            try {
                ruri = new URI("file", null, path, ruri.getQuery(), ruri.getFragment());
            }
            catch (final URISyntaxException ex) {}
        }
        return ruri;
    }
    
    public static class SchemaToProcess
    {
        private SchemaDocument.Schema schema;
        private String chameleonNamespace;
        private List includes;
        private List redefines;
        private List redefineObjects;
        private Set indirectIncludes;
        private Set indirectIncludedBy;
        
        public SchemaToProcess(final SchemaDocument.Schema schema, final String chameleonNamespace) {
            this.schema = schema;
            this.chameleonNamespace = chameleonNamespace;
        }
        
        public SchemaDocument.Schema getSchema() {
            return this.schema;
        }
        
        public String getSourceName() {
            return this.schema.documentProperties().getSourceName();
        }
        
        public String getChameleonNamespace() {
            return this.chameleonNamespace;
        }
        
        public List getRedefines() {
            return this.redefines;
        }
        
        public List getRedefineObjects() {
            return this.redefineObjects;
        }
        
        private void addInclude(final SchemaToProcess include) {
            if (this.includes == null) {
                this.includes = new ArrayList();
            }
            this.includes.add(include);
        }
        
        private void addRedefine(final SchemaToProcess redefine, final RedefineDocument.Redefine object) {
            if (this.redefines == null || this.redefineObjects == null) {
                this.redefines = new ArrayList();
                this.redefineObjects = new ArrayList();
            }
            this.redefines.add(redefine);
            this.redefineObjects.add(object);
        }
        
        private void buildIndirectReferences() {
            if (this.includes != null) {
                for (int i = 0; i < this.includes.size(); ++i) {
                    final SchemaToProcess schemaToProcess = this.includes.get(i);
                    this.addIndirectIncludes(schemaToProcess);
                }
            }
            if (this.redefines != null) {
                for (int i = 0; i < this.redefines.size(); ++i) {
                    final SchemaToProcess schemaToProcess = this.redefines.get(i);
                    this.addIndirectIncludes(schemaToProcess);
                }
            }
        }
        
        private void addIndirectIncludes(final SchemaToProcess schemaToProcess) {
            if (this.indirectIncludes == null) {
                this.indirectIncludes = new HashSet();
            }
            this.indirectIncludes.add(schemaToProcess);
            if (schemaToProcess.indirectIncludedBy == null) {
                schemaToProcess.indirectIncludedBy = new HashSet();
            }
            schemaToProcess.indirectIncludedBy.add(this);
            addIndirectIncludesHelper(this, schemaToProcess);
            if (this.indirectIncludedBy != null) {
                for (final SchemaToProcess stp : this.indirectIncludedBy) {
                    stp.indirectIncludes.add(schemaToProcess);
                    schemaToProcess.indirectIncludedBy.add(stp);
                    addIndirectIncludesHelper(stp, schemaToProcess);
                }
            }
        }
        
        private static void addIndirectIncludesHelper(final SchemaToProcess including, final SchemaToProcess schemaToProcess) {
            if (schemaToProcess.indirectIncludes != null) {
                for (final SchemaToProcess stp : schemaToProcess.indirectIncludes) {
                    including.indirectIncludes.add(stp);
                    stp.indirectIncludedBy.add(including);
                }
            }
        }
        
        public boolean indirectIncludes(final SchemaToProcess schemaToProcess) {
            return this.indirectIncludes != null && this.indirectIncludes.contains(schemaToProcess);
        }
        
        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (!(o instanceof SchemaToProcess)) {
                return false;
            }
            final SchemaToProcess schemaToProcess = (SchemaToProcess)o;
            if (this.chameleonNamespace != null) {
                if (this.chameleonNamespace.equals(schemaToProcess.chameleonNamespace)) {
                    return this.schema == schemaToProcess.schema;
                }
            }
            else if (schemaToProcess.chameleonNamespace == null) {
                return this.schema == schemaToProcess.schema;
            }
            return false;
        }
        
        @Override
        public int hashCode() {
            int result = this.schema.hashCode();
            result = 29 * result + ((this.chameleonNamespace != null) ? this.chameleonNamespace.hashCode() : 0);
            return result;
        }
    }
    
    public static class DownloadTable
    {
        private Map schemaByNsLocPair;
        private Map schemaByDigestKey;
        private LinkedList scanNeeded;
        private Set emptyNamespaceSchemas;
        private Map scannedAlready;
        private Set failedDownloads;
        
        private SchemaDocument.Schema downloadSchema(final XmlObject referencedBy, final String targetNamespace, final String locationURL) {
            if (locationURL == null) {
                return null;
            }
            final StscState state = StscState.get();
            final URI baseURI = parseURI(baseURLForDoc(referencedBy));
            String absoluteURL = null;
            try {
                absoluteURL = ((baseURI == null) ? locationURL : StscImporter.resolve(baseURI, locationURL).toString());
            }
            catch (final URISyntaxException e) {
                state.error("Could not find resource - invalid location URL: " + e.getMessage(), 56, referencedBy);
                return null;
            }
            if (state.isFileProcessed(absoluteURL)) {
                return null;
            }
            if (absoluteURL != null && targetNamespace != null) {
                final SchemaDocument.Schema result = this.schemaByNsLocPair.get(new NsLocPair(targetNamespace, absoluteURL));
                if (result != null) {
                    return result;
                }
            }
            if (targetNamespace != null && !targetNamespace.equals("")) {
                if (!state.shouldDownloadURI(absoluteURL)) {
                    final SchemaDocument.Schema result = this.schemaByNsLocPair.get(new NsLocPair(targetNamespace, null));
                    if (result != null) {
                        return result;
                    }
                }
                if (state.linkerDefinesNamespace(targetNamespace)) {
                    return null;
                }
            }
            if (absoluteURL != null) {
                final SchemaDocument.Schema result = this.schemaByNsLocPair.get(new NsLocPair(null, absoluteURL));
                if (result != null) {
                    return result;
                }
            }
            if (absoluteURL == null) {
                state.error("Could not find resource - no valid location URL.", 56, referencedBy);
                return null;
            }
            if (this.previouslyFailedToDownload(absoluteURL)) {
                return null;
            }
            if (!state.shouldDownloadURI(absoluteURL)) {
                state.error("Could not load resource \"" + absoluteURL + "\" (network downloads disabled).", 56, referencedBy);
                this.addFailedDownload(absoluteURL);
                return null;
            }
            Label_0653: {
                try {
                    final XmlObject xdoc = downloadDocument(state.getS4SLoader(), targetNamespace, absoluteURL);
                    SchemaDocument.Schema result2 = this.findMatchByDigest(xdoc);
                    final String shortname = state.relativize(absoluteURL);
                    if (result2 != null) {
                        final String dupname = state.relativize(result2.documentProperties().getSourceName());
                        if (dupname != null) {
                            state.info(shortname + " is the same as " + dupname + " (ignoring the duplicate file)");
                        }
                        else {
                            state.info(shortname + " is the same as another schema");
                        }
                    }
                    else {
                        final XmlOptions voptions = new XmlOptions();
                        voptions.setErrorListener(state.getErrorListener());
                        if (!(xdoc instanceof SchemaDocument) || !xdoc.validate(voptions)) {
                            state.error("Referenced document is not a valid schema", 56, referencedBy);
                            break Label_0653;
                        }
                        final SchemaDocument sDoc = (SchemaDocument)xdoc;
                        result2 = sDoc.getSchema();
                        state.info("Loading referenced file " + shortname);
                    }
                    final NsLocPair key = new NsLocPair(emptyStringIfNull(result2.getTargetNamespace()), absoluteURL);
                    this.addSuccessfulDownload(key, result2);
                    return result2;
                }
                catch (final MalformedURLException malformed) {
                    state.error("URL \"" + absoluteURL + "\" is not well-formed", 56, referencedBy);
                }
                catch (final IOException connectionProblem) {
                    state.error(connectionProblem.toString(), 56, referencedBy);
                }
                catch (final XmlException e2) {
                    state.error("Problem parsing referenced XML resource - " + e2.getMessage(), 56, referencedBy);
                }
            }
            this.addFailedDownload(absoluteURL);
            return null;
        }
        
        static XmlObject downloadDocument(final SchemaTypeLoader loader, final String namespace, final String absoluteURL) throws MalformedURLException, IOException, XmlException {
            final StscState state = StscState.get();
            final EntityResolver resolver = state.getEntityResolver();
            if (resolver != null) {
                InputSource source = null;
                try {
                    source = resolver.resolveEntity(namespace, absoluteURL);
                }
                catch (final SAXException e) {
                    throw new XmlException(e);
                }
                if (source != null) {
                    state.addSourceUri(absoluteURL, null);
                    Reader reader = source.getCharacterStream();
                    if (reader != null) {
                        reader = copySchemaSource(absoluteURL, reader, state);
                        final XmlOptions options = new XmlOptions();
                        options.setLoadLineNumbers();
                        options.setDocumentSourceName(absoluteURL);
                        return loader.parse(reader, null, options);
                    }
                    InputStream bytes = source.getByteStream();
                    if (bytes != null) {
                        bytes = copySchemaSource(absoluteURL, bytes, state);
                        final String encoding = source.getEncoding();
                        final XmlOptions options2 = new XmlOptions();
                        options2.setLoadLineNumbers();
                        options2.setLoadMessageDigest();
                        options2.setDocumentSourceName(absoluteURL);
                        if (encoding != null) {
                            options2.setCharacterEncoding(encoding);
                        }
                        return loader.parse(bytes, null, options2);
                    }
                    final String urlToLoad = source.getSystemId();
                    if (urlToLoad == null) {
                        throw new IOException("EntityResolver unable to resolve " + absoluteURL + " (for namespace " + namespace + ")");
                    }
                    copySchemaSource(absoluteURL, state, false);
                    final XmlOptions options2 = new XmlOptions();
                    options2.setLoadLineNumbers();
                    options2.setLoadMessageDigest();
                    options2.setDocumentSourceName(absoluteURL);
                    final URL urlDownload = new URL(urlToLoad);
                    return loader.parse(urlDownload, null, options2);
                }
            }
            state.addSourceUri(absoluteURL, null);
            copySchemaSource(absoluteURL, state, false);
            final XmlOptions options3 = new XmlOptions();
            options3.setLoadLineNumbers();
            options3.setLoadMessageDigest();
            final URL urlDownload2 = new URL(absoluteURL);
            return loader.parse(urlDownload2, null, options3);
        }
        
        private void addSuccessfulDownload(final NsLocPair key, final SchemaDocument.Schema schema) {
            final byte[] digest = schema.documentProperties().getMessageDigest();
            if (digest == null) {
                StscState.get().addSchemaDigest(null);
            }
            else {
                final DigestKey dk = new DigestKey(digest);
                if (!this.schemaByDigestKey.containsKey(dk)) {
                    this.schemaByDigestKey.put(new DigestKey(digest), schema);
                    StscState.get().addSchemaDigest(digest);
                }
            }
            this.schemaByNsLocPair.put(key, schema);
            final NsLocPair key2 = new NsLocPair(key.getNamespaceURI(), null);
            if (!this.schemaByNsLocPair.containsKey(key2)) {
                this.schemaByNsLocPair.put(key2, schema);
            }
            final NsLocPair key3 = new NsLocPair(null, key.getLocationURL());
            if (!this.schemaByNsLocPair.containsKey(key3)) {
                this.schemaByNsLocPair.put(key3, schema);
            }
        }
        
        private SchemaDocument.Schema findMatchByDigest(final XmlObject original) {
            final byte[] digest = original.documentProperties().getMessageDigest();
            if (digest == null) {
                return null;
            }
            return this.schemaByDigestKey.get(new DigestKey(digest));
        }
        
        private void addFailedDownload(final String locationURL) {
            this.failedDownloads.add(locationURL);
        }
        
        private boolean previouslyFailedToDownload(final String locationURL) {
            return this.failedDownloads.contains(locationURL);
        }
        
        private static boolean nullableStringsMatch(final String s1, final String s2) {
            return (s1 == null && s2 == null) || (s1 != null && s2 != null && s1.equals(s2));
        }
        
        private static String emptyStringIfNull(final String s) {
            if (s == null) {
                return "";
            }
            return s;
        }
        
        private SchemaToProcess addScanNeeded(final SchemaToProcess stp) {
            if (!this.scannedAlready.containsKey(stp)) {
                this.scannedAlready.put(stp, stp);
                this.scanNeeded.add(stp);
                return stp;
            }
            return this.scannedAlready.get(stp);
        }
        
        private void addEmptyNamespaceSchema(final SchemaDocument.Schema s) {
            this.emptyNamespaceSchemas.add(s);
        }
        
        private void usedEmptyNamespaceSchema(final SchemaDocument.Schema s) {
            this.emptyNamespaceSchemas.remove(s);
        }
        
        private boolean fetchRemainingEmptyNamespaceSchemas() {
            if (this.emptyNamespaceSchemas.isEmpty()) {
                return false;
            }
            for (final SchemaDocument.Schema schema : this.emptyNamespaceSchemas) {
                this.addScanNeeded(new SchemaToProcess(schema, null));
            }
            this.emptyNamespaceSchemas.clear();
            return true;
        }
        
        private boolean hasNextToScan() {
            return !this.scanNeeded.isEmpty();
        }
        
        private SchemaToProcess nextToScan() {
            final SchemaToProcess next = this.scanNeeded.removeFirst();
            return next;
        }
        
        public DownloadTable(final SchemaDocument.Schema[] startWith) {
            this.schemaByNsLocPair = new HashMap();
            this.schemaByDigestKey = new HashMap();
            this.scanNeeded = new LinkedList();
            this.emptyNamespaceSchemas = new HashSet();
            this.scannedAlready = new HashMap();
            this.failedDownloads = new HashSet();
            for (int i = 0; i < startWith.length; ++i) {
                final String targetNamespace = startWith[i].getTargetNamespace();
                final NsLocPair key = new NsLocPair(targetNamespace, baseURLForDoc(startWith[i]));
                this.addSuccessfulDownload(key, startWith[i]);
                if (targetNamespace != null) {
                    this.addScanNeeded(new SchemaToProcess(startWith[i], null));
                }
                else {
                    this.addEmptyNamespaceSchema(startWith[i]);
                }
            }
        }
        
        public SchemaToProcess[] resolveImportsAndIncludes(final boolean forceSave) {
            final StscState state = StscState.get();
            final List result = new ArrayList();
            boolean hasRedefinitions = false;
            while (true) {
                if (this.hasNextToScan()) {
                    final SchemaToProcess stp = this.nextToScan();
                    final String uri = stp.getSourceName();
                    state.addSourceUri(uri, null);
                    result.add(stp);
                    copySchemaSource(uri, state, forceSave);
                    final ImportDocument.Import[] imports = stp.getSchema().getImportArray();
                    for (int i = 0; i < imports.length; ++i) {
                        final SchemaDocument.Schema imported = this.downloadSchema(imports[i], emptyStringIfNull(imports[i].getNamespace()), imports[i].getSchemaLocation());
                        if (imported != null) {
                            if (!nullableStringsMatch(imported.getTargetNamespace(), imports[i].getNamespace())) {
                                StscState.get().error("Imported schema has a target namespace \"" + imported.getTargetNamespace() + "\" that does not match the specified \"" + imports[i].getNamespace() + "\"", 4, imports[i]);
                            }
                            else {
                                this.addScanNeeded(new SchemaToProcess(imported, null));
                            }
                        }
                    }
                    final IncludeDocument.Include[] includes = stp.getSchema().getIncludeArray();
                    String sourceNamespace = stp.getChameleonNamespace();
                    if (sourceNamespace == null) {
                        sourceNamespace = emptyStringIfNull(stp.getSchema().getTargetNamespace());
                    }
                    for (int j = 0; j < includes.length; ++j) {
                        final SchemaDocument.Schema included = this.downloadSchema(includes[j], null, includes[j].getSchemaLocation());
                        if (included != null) {
                            if (emptyStringIfNull(included.getTargetNamespace()).equals(sourceNamespace)) {
                                final SchemaToProcess s = this.addScanNeeded(new SchemaToProcess(included, null));
                                stp.addInclude(s);
                            }
                            else if (included.getTargetNamespace() != null) {
                                StscState.get().error("Included schema has a target namespace \"" + included.getTargetNamespace() + "\" that does not match the source namespace \"" + sourceNamespace + "\"", 4, includes[j]);
                            }
                            else {
                                final SchemaToProcess s = this.addScanNeeded(new SchemaToProcess(included, sourceNamespace));
                                stp.addInclude(s);
                                this.usedEmptyNamespaceSchema(included);
                            }
                        }
                    }
                    final RedefineDocument.Redefine[] redefines = stp.getSchema().getRedefineArray();
                    sourceNamespace = stp.getChameleonNamespace();
                    if (sourceNamespace == null) {
                        sourceNamespace = emptyStringIfNull(stp.getSchema().getTargetNamespace());
                    }
                    for (int j = 0; j < redefines.length; ++j) {
                        final SchemaDocument.Schema redefined = this.downloadSchema(redefines[j], null, redefines[j].getSchemaLocation());
                        if (redefined != null) {
                            if (emptyStringIfNull(redefined.getTargetNamespace()).equals(sourceNamespace)) {
                                final SchemaToProcess s = this.addScanNeeded(new SchemaToProcess(redefined, null));
                                stp.addRedefine(s, redefines[j]);
                                hasRedefinitions = true;
                            }
                            else if (redefined.getTargetNamespace() != null) {
                                StscState.get().error("Redefined schema has a target namespace \"" + redefined.getTargetNamespace() + "\" that does not match the source namespace \"" + sourceNamespace + "\"", 4, redefines[j]);
                            }
                            else {
                                final SchemaToProcess s = this.addScanNeeded(new SchemaToProcess(redefined, sourceNamespace));
                                stp.addRedefine(s, redefines[j]);
                                this.usedEmptyNamespaceSchema(redefined);
                                hasRedefinitions = true;
                            }
                        }
                    }
                }
                else {
                    if (!this.fetchRemainingEmptyNamespaceSchemas()) {
                        break;
                    }
                    continue;
                }
            }
            if (hasRedefinitions) {
                for (int k = 0; k < result.size(); ++k) {
                    final SchemaToProcess schemaToProcess = result.get(k);
                    schemaToProcess.buildIndirectReferences();
                }
            }
            return result.toArray(new SchemaToProcess[result.size()]);
        }
        
        private static Reader copySchemaSource(final String url, final Reader reader, final StscState state) {
            if (state.getSchemasDir() == null) {
                return reader;
            }
            final String schemalocation = state.sourceNameForUri(url);
            final File targetFile = new File(state.getSchemasDir(), schemalocation);
            if (targetFile.exists()) {
                return reader;
            }
            try {
                final File parentDir = new File(targetFile.getParent());
                IOUtil.createDir(parentDir, null);
                final CharArrayReader car = copy(reader);
                final XmlEncodingSniffer xes = new XmlEncodingSniffer(car, null);
                final Writer out = new OutputStreamWriter(new FileOutputStream(targetFile), xes.getXmlEncoding());
                IOUtil.copyCompletely(car, out);
                car.reset();
                return car;
            }
            catch (final IOException e) {
                System.err.println("IO Error " + e);
                return reader;
            }
        }
        
        private static InputStream copySchemaSource(final String url, final InputStream bytes, final StscState state) {
            if (state.getSchemasDir() == null) {
                return bytes;
            }
            final String schemalocation = state.sourceNameForUri(url);
            final File targetFile = new File(state.getSchemasDir(), schemalocation);
            if (targetFile.exists()) {
                return bytes;
            }
            try {
                final File parentDir = new File(targetFile.getParent());
                IOUtil.createDir(parentDir, null);
                final ByteArrayInputStream bais = copy(bytes);
                final FileOutputStream out = new FileOutputStream(targetFile);
                IOUtil.copyCompletely(bais, out);
                bais.reset();
                return bais;
            }
            catch (final IOException e) {
                System.err.println("IO Error " + e);
                return bytes;
            }
        }
        
        private static void copySchemaSource(final String urlLoc, final StscState state, final boolean forceCopy) {
            if (state.getSchemasDir() != null) {
                final String schemalocation = state.sourceNameForUri(urlLoc);
                final File targetFile = new File(state.getSchemasDir(), schemalocation);
                if (!forceCopy) {
                    if (targetFile.exists()) {
                        return;
                    }
                }
                try {
                    final File parentDir = new File(targetFile.getParent());
                    IOUtil.createDir(parentDir, null);
                    InputStream in = null;
                    final URL url = new URL(urlLoc);
                    try {
                        in = url.openStream();
                    }
                    catch (final FileNotFoundException fnfe) {
                        if (!forceCopy || !targetFile.exists()) {
                            throw fnfe;
                        }
                        targetFile.delete();
                    }
                    if (in != null) {
                        final FileOutputStream out = new FileOutputStream(targetFile);
                        IOUtil.copyCompletely(in, out);
                    }
                }
                catch (final IOException e) {
                    System.err.println("IO Error " + e);
                }
            }
        }
        
        private static ByteArrayInputStream copy(final InputStream is) throws IOException {
            final byte[] buf = new byte[1024];
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            int bytesRead;
            while ((bytesRead = is.read(buf, 0, 1024)) > 0) {
                baos.write(buf, 0, bytesRead);
            }
            return new ByteArrayInputStream(baos.toByteArray());
        }
        
        private static CharArrayReader copy(final Reader is) throws IOException {
            final char[] buf = new char[1024];
            final CharArrayWriter baos = new CharArrayWriter();
            int bytesRead;
            while ((bytesRead = is.read(buf, 0, 1024)) > 0) {
                baos.write(buf, 0, bytesRead);
            }
            return new CharArrayReader(baos.toCharArray());
        }
        
        private static class NsLocPair
        {
            private String namespaceURI;
            private String locationURL;
            
            public NsLocPair(final String namespaceURI, final String locationURL) {
                this.namespaceURI = namespaceURI;
                this.locationURL = locationURL;
            }
            
            public String getNamespaceURI() {
                return this.namespaceURI;
            }
            
            public String getLocationURL() {
                return this.locationURL;
            }
            
            @Override
            public boolean equals(final Object o) {
                if (this == o) {
                    return true;
                }
                if (!(o instanceof NsLocPair)) {
                    return false;
                }
                final NsLocPair nsLocPair = (NsLocPair)o;
                Label_0054: {
                    if (this.locationURL != null) {
                        if (this.locationURL.equals(nsLocPair.locationURL)) {
                            break Label_0054;
                        }
                    }
                    else if (nsLocPair.locationURL == null) {
                        break Label_0054;
                    }
                    return false;
                }
                if (this.namespaceURI != null) {
                    if (this.namespaceURI.equals(nsLocPair.namespaceURI)) {
                        return true;
                    }
                }
                else if (nsLocPair.namespaceURI == null) {
                    return true;
                }
                return false;
            }
            
            @Override
            public int hashCode() {
                int result = (this.namespaceURI != null) ? this.namespaceURI.hashCode() : 0;
                result = 29 * result + ((this.locationURL != null) ? this.locationURL.hashCode() : 0);
                return result;
            }
        }
        
        private static class DigestKey
        {
            byte[] _digest;
            int _hashCode;
            
            DigestKey(final byte[] digest) {
                this._digest = digest;
                for (int i = 0; i < 4 && i < digest.length; ++i) {
                    this._hashCode <<= 8;
                    this._hashCode += digest[i];
                }
            }
            
            @Override
            public boolean equals(final Object o) {
                return this == o || (o instanceof DigestKey && Arrays.equals(this._digest, ((DigestKey)o)._digest));
            }
            
            @Override
            public int hashCode() {
                return this._hashCode;
            }
        }
    }
}
