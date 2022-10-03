package org.apache.xmlbeans.impl.tool;

import org.apache.xmlbeans.impl.xb.xsdschema.SchemaDocument;
import org.apache.xmlbeans.XmlBeans;
import java.net.URLConnection;
import java.io.ByteArrayInputStream;
import org.apache.xmlbeans.impl.util.HexBin;
import java.io.OutputStream;
import org.apache.xmlbeans.impl.common.IOUtil;
import java.net.URL;
import java.io.ByteArrayOutputStream;
import java.net.URISyntaxException;
import java.net.URI;
import java.security.NoSuchAlgorithmException;
import java.security.MessageDigest;
import java.security.DigestInputStream;
import java.util.Iterator;
import java.io.InputStream;
import org.apache.xmlbeans.XmlOptions;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemaEntry;
import java.io.IOException;
import java.util.HashSet;
import java.util.HashMap;
import java.util.Set;
import java.util.Map;
import org.apache.xmlbeans.impl.xb.xsdownload.DownloadedSchemasDocument;

public abstract class BaseSchemaResourceManager extends SchemaImportResolver
{
    private static final String USER_AGENT;
    private String _defaultCopyDirectory;
    private DownloadedSchemasDocument _importsDoc;
    private Map _resourceForFilename;
    private Map _resourceForURL;
    private Map _resourceForNamespace;
    private Map _resourceForDigest;
    private Map _resourceForCacheEntry;
    private Set _redownloadSet;
    
    protected BaseSchemaResourceManager() {
        this._resourceForFilename = new HashMap();
        this._resourceForURL = new HashMap();
        this._resourceForNamespace = new HashMap();
        this._resourceForDigest = new HashMap();
        this._resourceForCacheEntry = new HashMap();
        this._redownloadSet = new HashSet();
    }
    
    protected final void init() {
        if (this.fileExists(this.getIndexFilename())) {
            try {
                this._importsDoc = DownloadedSchemasDocument.Factory.parse(this.inputStreamForFile(this.getIndexFilename()));
            }
            catch (final IOException e) {
                this._importsDoc = null;
            }
            catch (final Exception e2) {
                throw (IllegalStateException)new IllegalStateException("Problem reading xsdownload.xml: please fix or delete this file").initCause(e2);
            }
        }
        if (this._importsDoc == null) {
            try {
                this._importsDoc = DownloadedSchemasDocument.Factory.parse("<dls:downloaded-schemas xmlns:dls='http://www.bea.com/2003/01/xmlbean/xsdownload' defaultDirectory='" + this.getDefaultSchemaDir() + "'/>");
            }
            catch (final Exception e2) {
                throw (IllegalStateException)new IllegalStateException().initCause(e2);
            }
        }
        String defaultDir = this._importsDoc.getDownloadedSchemas().getDefaultDirectory();
        if (defaultDir == null) {
            defaultDir = this.getDefaultSchemaDir();
        }
        this._defaultCopyDirectory = defaultDir;
        final DownloadedSchemaEntry[] entries = this._importsDoc.getDownloadedSchemas().getEntryArray();
        for (int i = 0; i < entries.length; ++i) {
            this.updateResource(entries[i]);
        }
    }
    
    public final void writeCache() throws IOException {
        final InputStream input = this._importsDoc.newInputStream(new XmlOptions().setSavePrettyPrint());
        this.writeInputStreamToFile(input, this.getIndexFilename());
    }
    
    public final void processAll(final boolean sync, final boolean refresh, final boolean imports) {
        if (refresh) {
            this._redownloadSet = new HashSet();
        }
        else {
            this._redownloadSet = null;
        }
        final String[] allFilenames = this.getAllXSDFilenames();
        if (sync) {
            this.syncCacheWithLocalXsdFiles(allFilenames, false);
        }
        final SchemaResource[] starters = (SchemaResource[])this._resourceForFilename.values().toArray(new SchemaResource[0]);
        if (refresh) {
            this.redownloadEntries(starters);
        }
        if (imports) {
            this.resolveImports(starters);
        }
        this._redownloadSet = null;
    }
    
    public final void process(final String[] uris, final String[] filenames, final boolean sync, final boolean refresh, final boolean imports) {
        if (refresh) {
            this._redownloadSet = new HashSet();
        }
        else {
            this._redownloadSet = null;
        }
        if (filenames.length > 0) {
            this.syncCacheWithLocalXsdFiles(filenames, true);
        }
        else if (sync) {
            this.syncCacheWithLocalXsdFiles(this.getAllXSDFilenames(), false);
        }
        final Set starterset = new HashSet();
        for (int i = 0; i < uris.length; ++i) {
            final SchemaResource resource = (SchemaResource)this.lookupResource(null, uris[i]);
            if (resource != null) {
                starterset.add(resource);
            }
        }
        for (int i = 0; i < filenames.length; ++i) {
            final SchemaResource resource = this._resourceForFilename.get(filenames);
            if (resource != null) {
                starterset.add(resource);
            }
        }
        final SchemaResource[] starters = starterset.toArray(new SchemaResource[0]);
        if (refresh) {
            this.redownloadEntries(starters);
        }
        if (imports) {
            this.resolveImports(starters);
        }
        this._redownloadSet = null;
    }
    
    public final void syncCacheWithLocalXsdFiles(final String[] filenames, final boolean deleteOnlyMentioned) {
        final Set seenResources = new HashSet();
        final Set vanishedResources = new HashSet();
        for (int i = 0; i < filenames.length; ++i) {
            final String filename = filenames[i];
            SchemaResource resource = this._resourceForFilename.get(filename);
            if (resource != null) {
                if (this.fileExists(filename)) {
                    seenResources.add(resource);
                }
                else {
                    vanishedResources.add(resource);
                }
            }
            else {
                String digest = null;
                try {
                    digest = this.shaDigestForFile(filename);
                    resource = this._resourceForDigest.get(digest);
                    if (resource != null) {
                        final String oldFilename = resource.getFilename();
                        if (!this.fileExists(oldFilename)) {
                            this.warning("File " + filename + " is a rename of " + oldFilename);
                            resource.setFilename(filename);
                            seenResources.add(resource);
                            if (this._resourceForFilename.get(oldFilename) == resource) {
                                this._resourceForFilename.remove(oldFilename);
                            }
                            if (this._resourceForFilename.containsKey(filename)) {
                                this._resourceForFilename.put(filename, resource);
                            }
                            continue;
                        }
                    }
                }
                catch (final IOException ex) {}
                final DownloadedSchemaEntry newEntry = this.addNewEntry();
                newEntry.setFilename(filename);
                this.warning("Caching information on new local file " + filename);
                if (digest != null) {
                    newEntry.setSha1(digest);
                }
                seenResources.add(this.updateResource(newEntry));
            }
        }
        if (deleteOnlyMentioned) {
            this.deleteResourcesInSet(vanishedResources, true);
        }
        else {
            this.deleteResourcesInSet(seenResources, false);
        }
    }
    
    private void redownloadEntries(final SchemaResource[] resources) {
        for (int i = 0; i < resources.length; ++i) {
            this.redownloadResource(resources[i]);
        }
    }
    
    private void deleteResourcesInSet(final Set seenResources, final boolean setToDelete) {
        final Set seenCacheEntries = new HashSet();
        for (final SchemaResource resource : seenResources) {
            seenCacheEntries.add(resource._cacheEntry);
        }
        final DownloadedSchemasDocument.DownloadedSchemas downloadedSchemas = this._importsDoc.getDownloadedSchemas();
        for (int j = 0; j < downloadedSchemas.sizeOfEntryArray(); ++j) {
            final DownloadedSchemaEntry cacheEntry = downloadedSchemas.getEntryArray(j);
            if (seenCacheEntries.contains(cacheEntry) == setToDelete) {
                final SchemaResource resource2 = this._resourceForCacheEntry.get(cacheEntry);
                this.warning("Removing obsolete cache entry for " + resource2.getFilename());
                if (resource2 != null) {
                    this._resourceForCacheEntry.remove(cacheEntry);
                    if (resource2 == this._resourceForFilename.get(resource2.getFilename())) {
                        this._resourceForFilename.remove(resource2.getFilename());
                    }
                    if (resource2 == this._resourceForDigest.get(resource2.getSha1())) {
                        this._resourceForDigest.remove(resource2.getSha1());
                    }
                    if (resource2 == this._resourceForNamespace.get(resource2.getNamespace())) {
                        this._resourceForNamespace.remove(resource2.getNamespace());
                    }
                    final String[] urls = resource2.getSchemaLocationArray();
                    for (int k = 0; k < urls.length; ++k) {
                        if (resource2 == this._resourceForURL.get(urls[k])) {
                            this._resourceForURL.remove(urls[k]);
                        }
                    }
                }
                downloadedSchemas.removeEntry(j);
                --j;
            }
        }
    }
    
    private SchemaResource updateResource(final DownloadedSchemaEntry entry) {
        final String filename = entry.getFilename();
        if (filename == null) {
            return null;
        }
        final SchemaResource resource = new SchemaResource(entry);
        this._resourceForCacheEntry.put(entry, resource);
        if (!this._resourceForFilename.containsKey(filename)) {
            this._resourceForFilename.put(filename, resource);
        }
        final String digest = resource.getSha1();
        if (digest != null && !this._resourceForDigest.containsKey(digest)) {
            this._resourceForDigest.put(digest, resource);
        }
        final String namespace = resource.getNamespace();
        if (namespace != null && !this._resourceForNamespace.containsKey(namespace)) {
            this._resourceForNamespace.put(namespace, resource);
        }
        final String[] urls = resource.getSchemaLocationArray();
        for (int j = 0; j < urls.length; ++j) {
            if (!this._resourceForURL.containsKey(urls[j])) {
                this._resourceForURL.put(urls[j], resource);
            }
        }
        return resource;
    }
    
    private static DigestInputStream digestInputStream(final InputStream input) {
        MessageDigest sha;
        try {
            sha = MessageDigest.getInstance("SHA");
        }
        catch (final NoSuchAlgorithmException e) {
            throw (IllegalStateException)new IllegalStateException().initCause(e);
        }
        final DigestInputStream str = new DigestInputStream(input, sha);
        return str;
    }
    
    private DownloadedSchemaEntry addNewEntry() {
        return this._importsDoc.getDownloadedSchemas().addNewEntry();
    }
    
    @Override
    public SchemaImportResolver.SchemaResource lookupResource(final String nsURI, final String schemaLocation) {
        SchemaResource result = this.fetchFromCache(nsURI, schemaLocation);
        if (result != null) {
            if (this._redownloadSet != null) {
                this.redownloadResource(result);
            }
            return result;
        }
        if (schemaLocation == null) {
            this.warning("No cached schema for namespace '" + nsURI + "', and no url specified");
            return null;
        }
        result = this.copyOrIdentifyDuplicateURL(schemaLocation, nsURI);
        if (this._redownloadSet != null) {
            this._redownloadSet.add(result);
        }
        return result;
    }
    
    private SchemaResource fetchFromCache(final String nsURI, final String schemaLocation) {
        if (schemaLocation != null) {
            final SchemaResource result = this._resourceForURL.get(schemaLocation);
            if (result != null) {
                return result;
            }
        }
        if (nsURI != null) {
            final SchemaResource result = this._resourceForNamespace.get(nsURI);
            if (result != null) {
                return result;
            }
        }
        return null;
    }
    
    private String uniqueFilenameForURI(final String schemaLocation) throws IOException, URISyntaxException {
        String localFilename = new URI(schemaLocation).getRawPath();
        final int i = localFilename.lastIndexOf(47);
        if (i >= 0) {
            localFilename = localFilename.substring(i + 1);
        }
        if (localFilename.endsWith(".xsd")) {
            localFilename = localFilename.substring(0, localFilename.length() - 4);
        }
        if (localFilename.length() == 0) {
            localFilename = "schema";
        }
        String candidateFilename = localFilename;
        for (int suffix = 1; suffix < 1000; ++suffix, candidateFilename = localFilename + suffix) {
            final String candidate = this._defaultCopyDirectory + "/" + candidateFilename + ".xsd";
            if (!this.fileExists(candidate)) {
                return candidate;
            }
        }
        throw new IOException("Problem with filename " + localFilename + ".xsd");
    }
    
    private void redownloadResource(final SchemaResource resource) {
        if (this._redownloadSet != null) {
            if (this._redownloadSet.contains(resource)) {
                return;
            }
            this._redownloadSet.add(resource);
        }
        final String filename = resource.getFilename();
        final String schemaLocation = resource.getSchemaLocation();
        String digest = null;
        if (schemaLocation == null || filename == null) {
            return;
        }
        final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        try {
            final URL url = new URL(schemaLocation);
            final URLConnection conn = url.openConnection();
            conn.addRequestProperty("User-Agent", BaseSchemaResourceManager.USER_AGENT);
            conn.addRequestProperty("Accept", "application/xml, text/xml, */*");
            final DigestInputStream input = digestInputStream(conn.getInputStream());
            IOUtil.copyCompletely(input, buffer);
            digest = HexBin.bytesToString(input.getMessageDigest().digest());
        }
        catch (final Exception e) {
            this.warning("Could not copy remote resource " + schemaLocation + ":" + e.getMessage());
            return;
        }
        if (digest.equals(resource.getSha1()) && this.fileExists(filename)) {
            this.warning("Resource " + filename + " is unchanged from " + schemaLocation + ".");
            return;
        }
        try {
            final InputStream source = new ByteArrayInputStream(buffer.toByteArray());
            this.writeInputStreamToFile(source, filename);
        }
        catch (final IOException e2) {
            this.warning("Could not write to file " + filename + " for " + schemaLocation + ":" + e2.getMessage());
            return;
        }
        this.warning("Refreshed " + filename + " from " + schemaLocation);
    }
    
    private SchemaResource copyOrIdentifyDuplicateURL(final String schemaLocation, final String namespace) {
        String targetFilename;
        try {
            targetFilename = this.uniqueFilenameForURI(schemaLocation);
        }
        catch (final URISyntaxException e) {
            this.warning("Invalid URI '" + schemaLocation + "':" + e.getMessage());
            return null;
        }
        catch (final IOException e2) {
            this.warning("Could not create local file for " + schemaLocation + ":" + e2.getMessage());
            return null;
        }
        String digest;
        try {
            final URL url = new URL(schemaLocation);
            final DigestInputStream input = digestInputStream(url.openStream());
            this.writeInputStreamToFile(input, targetFilename);
            digest = HexBin.bytesToString(input.getMessageDigest().digest());
        }
        catch (final Exception e3) {
            this.warning("Could not copy remote resource " + schemaLocation + ":" + e3.getMessage());
            return null;
        }
        final SchemaResource result = this._resourceForDigest.get(digest);
        if (result != null) {
            this.deleteFile(targetFilename);
            result.addSchemaLocation(schemaLocation);
            if (!this._resourceForURL.containsKey(schemaLocation)) {
                this._resourceForURL.put(schemaLocation, result);
            }
            return result;
        }
        this.warning("Downloaded " + schemaLocation + " to " + targetFilename);
        final DownloadedSchemaEntry newEntry = this.addNewEntry();
        newEntry.setFilename(targetFilename);
        newEntry.setSha1(digest);
        if (namespace != null) {
            newEntry.setNamespace(namespace);
        }
        newEntry.addSchemaLocation(schemaLocation);
        return this.updateResource(newEntry);
    }
    
    @Override
    public void reportActualNamespace(final SchemaImportResolver.SchemaResource rresource, final String actualNamespace) {
        final SchemaResource resource = (SchemaResource)rresource;
        final String oldNamespace = resource.getNamespace();
        if (oldNamespace != null && this._resourceForNamespace.get(oldNamespace) == resource) {
            this._resourceForNamespace.remove(oldNamespace);
        }
        if (!this._resourceForNamespace.containsKey(actualNamespace)) {
            this._resourceForNamespace.put(actualNamespace, resource);
        }
        resource.setNamespace(actualNamespace);
    }
    
    private String shaDigestForFile(final String filename) throws IOException {
        final DigestInputStream str = digestInputStream(this.inputStreamForFile(filename));
        final byte[] dummy = new byte[4096];
        for (int i = 1; i > 0; i = str.read(dummy)) {}
        str.close();
        return HexBin.bytesToString(str.getMessageDigest().digest());
    }
    
    protected String getIndexFilename() {
        return "./xsdownload.xml";
    }
    
    protected String getDefaultSchemaDir() {
        return "./schema";
    }
    
    protected abstract void warning(final String p0);
    
    protected abstract boolean fileExists(final String p0);
    
    protected abstract InputStream inputStreamForFile(final String p0) throws IOException;
    
    protected abstract void writeInputStreamToFile(final InputStream p0, final String p1) throws IOException;
    
    protected abstract void deleteFile(final String p0);
    
    protected abstract String[] getAllXSDFilenames();
    
    static {
        USER_AGENT = "XMLBeans/" + XmlBeans.getVersion() + " (" + XmlBeans.getTitle() + ")";
    }
    
    private class SchemaResource implements SchemaImportResolver.SchemaResource
    {
        DownloadedSchemaEntry _cacheEntry;
        
        SchemaResource(final DownloadedSchemaEntry entry) {
            this._cacheEntry = entry;
        }
        
        public void setFilename(final String filename) {
            this._cacheEntry.setFilename(filename);
        }
        
        public String getFilename() {
            return this._cacheEntry.getFilename();
        }
        
        @Override
        public SchemaDocument.Schema getSchema() {
            if (!BaseSchemaResourceManager.this.fileExists(this.getFilename())) {
                BaseSchemaResourceManager.this.redownloadResource(this);
            }
            try {
                return SchemaDocument.Factory.parse(BaseSchemaResourceManager.this.inputStreamForFile(this.getFilename())).getSchema();
            }
            catch (final Exception e) {
                return null;
            }
        }
        
        public String getSha1() {
            return this._cacheEntry.getSha1();
        }
        
        @Override
        public String getNamespace() {
            return this._cacheEntry.getNamespace();
        }
        
        public void setNamespace(final String namespace) {
            this._cacheEntry.setNamespace(namespace);
        }
        
        @Override
        public String getSchemaLocation() {
            if (this._cacheEntry.sizeOfSchemaLocationArray() > 0) {
                return this._cacheEntry.getSchemaLocationArray(0);
            }
            return null;
        }
        
        public String[] getSchemaLocationArray() {
            return this._cacheEntry.getSchemaLocationArray();
        }
        
        @Override
        public int hashCode() {
            return this.getFilename().hashCode();
        }
        
        @Override
        public boolean equals(final Object obj) {
            return this == obj || this.getFilename().equals(((SchemaResource)obj).getFilename());
        }
        
        public void addSchemaLocation(final String schemaLocation) {
            this._cacheEntry.addSchemaLocation(schemaLocation);
        }
    }
}
