package com.sun.org.apache.xml.internal.resolver;

import java.io.UnsupportedEncodingException;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.helpers.PublicId;
import com.sun.org.apache.xml.internal.resolver.helpers.FileURL;
import java.util.Enumeration;
import java.io.FileNotFoundException;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Iterator;
import javax.xml.parsers.SAXParserFactory;
import com.sun.org.apache.xml.internal.resolver.readers.TR9401CatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.CatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import jdk.xml.internal.JdkXmlUtils;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;
import java.net.URL;

public class Catalog
{
    public static final int BASE;
    public static final int CATALOG;
    public static final int DOCUMENT;
    public static final int OVERRIDE;
    public static final int SGMLDECL;
    public static final int DELEGATE_PUBLIC;
    public static final int DELEGATE_SYSTEM;
    public static final int DELEGATE_URI;
    public static final int DOCTYPE;
    public static final int DTDDECL;
    public static final int ENTITY;
    public static final int LINKTYPE;
    public static final int NOTATION;
    public static final int PUBLIC;
    public static final int SYSTEM;
    public static final int URI;
    public static final int REWRITE_SYSTEM;
    public static final int REWRITE_URI;
    public static final int SYSTEM_SUFFIX;
    public static final int URI_SUFFIX;
    protected URL base;
    protected URL catalogCwd;
    protected Vector catalogEntries;
    protected boolean default_override;
    protected CatalogManager catalogManager;
    protected Vector catalogFiles;
    protected Vector localCatalogFiles;
    protected Vector catalogs;
    protected Vector localDelegate;
    protected Map<String, Integer> readerMap;
    protected Vector readerArr;
    
    public Catalog() {
        this.catalogEntries = new Vector();
        this.default_override = true;
        this.catalogManager = CatalogManager.getStaticManager();
        this.catalogFiles = new Vector();
        this.localCatalogFiles = new Vector();
        this.catalogs = new Vector();
        this.localDelegate = new Vector();
        this.readerMap = new HashMap<String, Integer>();
        this.readerArr = new Vector();
    }
    
    public Catalog(final CatalogManager manager) {
        this.catalogEntries = new Vector();
        this.default_override = true;
        this.catalogManager = CatalogManager.getStaticManager();
        this.catalogFiles = new Vector();
        this.localCatalogFiles = new Vector();
        this.catalogs = new Vector();
        this.localDelegate = new Vector();
        this.readerMap = new HashMap<String, Integer>();
        this.readerArr = new Vector();
        this.catalogManager = manager;
    }
    
    public CatalogManager getCatalogManager() {
        return this.catalogManager;
    }
    
    public void setCatalogManager(final CatalogManager manager) {
        this.catalogManager = manager;
    }
    
    public void setupReaders() {
        final SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
        spf.setValidating(false);
        final SAXCatalogReader saxReader = new SAXCatalogReader(spf);
        saxReader.setCatalogParser(null, "XMLCatalog", "com.sun.org.apache.xml.internal.resolver.readers.XCatalogReader");
        saxReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "com.sun.org.apache.xml.internal.resolver.readers.OASISXMLCatalogReader");
        this.addReader("application/xml", saxReader);
        final TR9401CatalogReader textReader = new TR9401CatalogReader();
        this.addReader("text/plain", textReader);
    }
    
    public void addReader(final String mimeType, final CatalogReader reader) {
        if (this.readerMap.containsKey(mimeType)) {
            final Integer pos = this.readerMap.get(mimeType);
            this.readerArr.set(pos, reader);
        }
        else {
            this.readerArr.add(reader);
            final Integer pos = this.readerArr.size() - 1;
            this.readerMap.put(mimeType, pos);
        }
    }
    
    protected void copyReaders(final Catalog newCatalog) {
        final Vector mapArr = new Vector(this.readerMap.size());
        for (int count = 0; count < this.readerMap.size(); ++count) {
            mapArr.add(null);
        }
        for (final Map.Entry<String, Integer> entry : this.readerMap.entrySet()) {
            mapArr.set(entry.getValue(), entry.getKey());
        }
        for (int count = 0; count < mapArr.size(); ++count) {
            final String mimeType = mapArr.get(count);
            final Integer pos = this.readerMap.get(mimeType);
            newCatalog.addReader(mimeType, this.readerArr.get(pos));
        }
    }
    
    protected Catalog newCatalog() {
        final String catalogClass = this.getClass().getName();
        try {
            final Catalog c = (Catalog)Class.forName(catalogClass).newInstance();
            c.setCatalogManager(this.catalogManager);
            this.copyReaders(c);
            return c;
        }
        catch (final ClassNotFoundException cnfe) {
            this.catalogManager.debug.message(1, "Class Not Found Exception: " + catalogClass);
        }
        catch (final IllegalAccessException iae) {
            this.catalogManager.debug.message(1, "Illegal Access Exception: " + catalogClass);
        }
        catch (final InstantiationException ie) {
            this.catalogManager.debug.message(1, "Instantiation Exception: " + catalogClass);
        }
        catch (final ClassCastException cce) {
            this.catalogManager.debug.message(1, "Class Cast Exception: " + catalogClass);
        }
        catch (final Exception e) {
            this.catalogManager.debug.message(1, "Other Exception: " + catalogClass);
        }
        final Catalog c = new Catalog();
        c.setCatalogManager(this.catalogManager);
        this.copyReaders(c);
        return c;
    }
    
    public String getCurrentBase() {
        return this.base.toString();
    }
    
    public String getDefaultOverride() {
        if (this.default_override) {
            return "yes";
        }
        return "no";
    }
    
    public void loadSystemCatalogs() throws MalformedURLException, IOException {
        final Vector catalogs = this.catalogManager.getCatalogFiles();
        if (catalogs != null) {
            for (int count = 0; count < catalogs.size(); ++count) {
                this.catalogFiles.addElement(catalogs.elementAt(count));
            }
        }
        if (this.catalogFiles.size() > 0) {
            final String catfile = this.catalogFiles.lastElement();
            this.catalogFiles.removeElement(catfile);
            this.parseCatalog(catfile);
        }
    }
    
    public synchronized void parseCatalog(final String fileName) throws MalformedURLException, IOException {
        this.default_override = this.catalogManager.getPreferPublic();
        this.catalogManager.debug.message(4, "Parse catalog: " + fileName);
        this.catalogFiles.addElement(fileName);
        this.parsePendingCatalogs();
    }
    
    public synchronized void parseCatalog(final String mimeType, final InputStream is) throws IOException, CatalogException {
        this.default_override = this.catalogManager.getPreferPublic();
        this.catalogManager.debug.message(4, "Parse " + mimeType + " catalog on input stream");
        CatalogReader reader = null;
        if (this.readerMap.containsKey(mimeType)) {
            final int arrayPos = this.readerMap.get(mimeType);
            reader = this.readerArr.get(arrayPos);
        }
        if (reader == null) {
            final String msg = "No CatalogReader for MIME type: " + mimeType;
            this.catalogManager.debug.message(2, msg);
            throw new CatalogException(6, msg);
        }
        reader.readCatalog(this, is);
        this.parsePendingCatalogs();
    }
    
    public synchronized void parseCatalog(final URL aUrl) throws IOException {
        this.catalogCwd = aUrl;
        this.base = aUrl;
        this.default_override = this.catalogManager.getPreferPublic();
        this.catalogManager.debug.message(4, "Parse catalog: " + aUrl.toString());
        DataInputStream inStream = null;
        boolean parsed = false;
        for (int count = 0; !parsed && count < this.readerArr.size(); ++count) {
            final CatalogReader reader = this.readerArr.get(count);
            try {
                inStream = new DataInputStream(aUrl.openStream());
            }
            catch (final FileNotFoundException fnfe) {
                break;
            }
            try {
                reader.readCatalog(this, inStream);
                parsed = true;
            }
            catch (final CatalogException ce) {
                if (ce.getExceptionType() == 7) {
                    break;
                }
            }
            try {
                inStream.close();
            }
            catch (final IOException ex) {}
        }
        if (parsed) {
            this.parsePendingCatalogs();
        }
    }
    
    protected synchronized void parsePendingCatalogs() throws MalformedURLException, IOException {
        if (!this.localCatalogFiles.isEmpty()) {
            final Vector newQueue = new Vector();
            final Enumeration q = this.localCatalogFiles.elements();
            while (q.hasMoreElements()) {
                newQueue.addElement(q.nextElement());
            }
            for (int curCat = 0; curCat < this.catalogFiles.size(); ++curCat) {
                final String catfile = this.catalogFiles.elementAt(curCat);
                newQueue.addElement(catfile);
            }
            this.catalogFiles = newQueue;
            this.localCatalogFiles.clear();
        }
        if (this.catalogFiles.isEmpty() && !this.localDelegate.isEmpty()) {
            final Enumeration e = this.localDelegate.elements();
            while (e.hasMoreElements()) {
                this.catalogEntries.addElement(e.nextElement());
            }
            this.localDelegate.clear();
        }
        while (!this.catalogFiles.isEmpty()) {
            String catfile2 = this.catalogFiles.elementAt(0);
            try {
                this.catalogFiles.remove(0);
            }
            catch (final ArrayIndexOutOfBoundsException ex) {}
            if (this.catalogEntries.size() == 0 && this.catalogs.size() == 0) {
                try {
                    this.parseCatalogFile(catfile2);
                }
                catch (final CatalogException ce) {
                    System.out.println("FIXME: " + ce.toString());
                }
            }
            else {
                this.catalogs.addElement(catfile2);
            }
            if (!this.localCatalogFiles.isEmpty()) {
                final Vector newQueue2 = new Vector();
                final Enumeration q2 = this.localCatalogFiles.elements();
                while (q2.hasMoreElements()) {
                    newQueue2.addElement(q2.nextElement());
                }
                for (int curCat2 = 0; curCat2 < this.catalogFiles.size(); ++curCat2) {
                    catfile2 = this.catalogFiles.elementAt(curCat2);
                    newQueue2.addElement(catfile2);
                }
                this.catalogFiles = newQueue2;
                this.localCatalogFiles.clear();
            }
            if (!this.localDelegate.isEmpty()) {
                final Enumeration e2 = this.localDelegate.elements();
                while (e2.hasMoreElements()) {
                    this.catalogEntries.addElement(e2.nextElement());
                }
                this.localDelegate.clear();
            }
        }
        this.catalogFiles.clear();
    }
    
    protected synchronized void parseCatalogFile(String fileName) throws MalformedURLException, IOException, CatalogException {
        try {
            this.catalogCwd = FileURL.makeURL("basename");
        }
        catch (final MalformedURLException e) {
            this.catalogManager.debug.message(1, "Malformed URL on cwd", "user.dir");
            this.catalogCwd = null;
        }
        try {
            this.base = new URL(this.catalogCwd, this.fixSlashes(fileName));
        }
        catch (final MalformedURLException e) {
            try {
                this.base = new URL("file:" + this.fixSlashes(fileName));
            }
            catch (final MalformedURLException e2) {
                this.catalogManager.debug.message(1, "Malformed URL on catalog filename", this.fixSlashes(fileName));
                this.base = null;
            }
        }
        this.catalogManager.debug.message(2, "Loading catalog", fileName);
        this.catalogManager.debug.message(4, "Default BASE", this.base.toString());
        fileName = this.base.toString();
        DataInputStream inStream = null;
        boolean parsed = false;
        boolean notFound = false;
        for (int count = 0; !parsed && count < this.readerArr.size(); ++count) {
            final CatalogReader reader = this.readerArr.get(count);
            try {
                notFound = false;
                inStream = new DataInputStream(this.base.openStream());
            }
            catch (final FileNotFoundException fnfe) {
                notFound = true;
                break;
            }
            try {
                reader.readCatalog(this, inStream);
                parsed = true;
            }
            catch (final CatalogException ce) {
                if (ce.getExceptionType() == 7) {
                    break;
                }
            }
            try {
                inStream.close();
            }
            catch (final IOException ex) {}
        }
        if (!parsed) {
            if (notFound) {
                this.catalogManager.debug.message(3, "Catalog does not exist", fileName);
            }
            else {
                this.catalogManager.debug.message(1, "Failed to parse catalog", fileName);
            }
        }
    }
    
    public void addEntry(final CatalogEntry entry) {
        final int type = entry.getEntryType();
        if (type == Catalog.BASE) {
            String value = entry.getEntryArg(0);
            URL newbase = null;
            if (this.base == null) {
                this.catalogManager.debug.message(5, "BASE CUR", "null");
            }
            else {
                this.catalogManager.debug.message(5, "BASE CUR", this.base.toString());
            }
            this.catalogManager.debug.message(4, "BASE STR", value);
            try {
                value = this.fixSlashes(value);
                newbase = new URL(this.base, value);
            }
            catch (final MalformedURLException e) {
                try {
                    newbase = new URL("file:" + value);
                }
                catch (final MalformedURLException e2) {
                    this.catalogManager.debug.message(1, "Malformed URL on base", value);
                    newbase = null;
                }
            }
            if (newbase != null) {
                this.base = newbase;
            }
            this.catalogManager.debug.message(5, "BASE NEW", this.base.toString());
        }
        else if (type == Catalog.CATALOG) {
            final String fsi = this.makeAbsolute(entry.getEntryArg(0));
            this.catalogManager.debug.message(4, "CATALOG", fsi);
            this.localCatalogFiles.addElement(fsi);
        }
        else if (type == Catalog.PUBLIC) {
            final String publicid = PublicId.normalize(entry.getEntryArg(0));
            final String systemid = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(0, publicid);
            entry.setEntryArg(1, systemid);
            this.catalogManager.debug.message(4, "PUBLIC", publicid, systemid);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.SYSTEM) {
            final String systemid2 = this.normalizeURI(entry.getEntryArg(0));
            final String fsi2 = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(1, fsi2);
            this.catalogManager.debug.message(4, "SYSTEM", systemid2, fsi2);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.URI) {
            final String uri = this.normalizeURI(entry.getEntryArg(0));
            final String altURI = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(1, altURI);
            this.catalogManager.debug.message(4, "URI", uri, altURI);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.DOCUMENT) {
            final String fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(0)));
            entry.setEntryArg(0, fsi);
            this.catalogManager.debug.message(4, "DOCUMENT", fsi);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.OVERRIDE) {
            this.catalogManager.debug.message(4, "OVERRIDE", entry.getEntryArg(0));
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.SGMLDECL) {
            final String fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(0)));
            entry.setEntryArg(0, fsi);
            this.catalogManager.debug.message(4, "SGMLDECL", fsi);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.DELEGATE_PUBLIC) {
            final String ppi = PublicId.normalize(entry.getEntryArg(0));
            final String fsi2 = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(0, ppi);
            entry.setEntryArg(1, fsi2);
            this.catalogManager.debug.message(4, "DELEGATE_PUBLIC", ppi, fsi2);
            this.addDelegate(entry);
        }
        else if (type == Catalog.DELEGATE_SYSTEM) {
            final String psi = this.normalizeURI(entry.getEntryArg(0));
            final String fsi2 = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(0, psi);
            entry.setEntryArg(1, fsi2);
            this.catalogManager.debug.message(4, "DELEGATE_SYSTEM", psi, fsi2);
            this.addDelegate(entry);
        }
        else if (type == Catalog.DELEGATE_URI) {
            final String pui = this.normalizeURI(entry.getEntryArg(0));
            final String fsi2 = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(0, pui);
            entry.setEntryArg(1, fsi2);
            this.catalogManager.debug.message(4, "DELEGATE_URI", pui, fsi2);
            this.addDelegate(entry);
        }
        else if (type == Catalog.REWRITE_SYSTEM) {
            final String psi = this.normalizeURI(entry.getEntryArg(0));
            final String rpx = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(0, psi);
            entry.setEntryArg(1, rpx);
            this.catalogManager.debug.message(4, "REWRITE_SYSTEM", psi, rpx);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.REWRITE_URI) {
            final String pui = this.normalizeURI(entry.getEntryArg(0));
            final String upx = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(0, pui);
            entry.setEntryArg(1, upx);
            this.catalogManager.debug.message(4, "REWRITE_URI", pui, upx);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.SYSTEM_SUFFIX) {
            final String pui = this.normalizeURI(entry.getEntryArg(0));
            final String upx = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(0, pui);
            entry.setEntryArg(1, upx);
            this.catalogManager.debug.message(4, "SYSTEM_SUFFIX", pui, upx);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.URI_SUFFIX) {
            final String pui = this.normalizeURI(entry.getEntryArg(0));
            final String upx = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(0, pui);
            entry.setEntryArg(1, upx);
            this.catalogManager.debug.message(4, "URI_SUFFIX", pui, upx);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.DOCTYPE) {
            final String fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(1, fsi);
            this.catalogManager.debug.message(4, "DOCTYPE", entry.getEntryArg(0), fsi);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.DTDDECL) {
            final String fpi = PublicId.normalize(entry.getEntryArg(0));
            entry.setEntryArg(0, fpi);
            final String fsi2 = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(1, fsi2);
            this.catalogManager.debug.message(4, "DTDDECL", fpi, fsi2);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.ENTITY) {
            final String fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(1, fsi);
            this.catalogManager.debug.message(4, "ENTITY", entry.getEntryArg(0), fsi);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.LINKTYPE) {
            final String fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(1, fsi);
            this.catalogManager.debug.message(4, "LINKTYPE", entry.getEntryArg(0), fsi);
            this.catalogEntries.addElement(entry);
        }
        else if (type == Catalog.NOTATION) {
            final String fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(1, fsi);
            this.catalogManager.debug.message(4, "NOTATION", entry.getEntryArg(0), fsi);
            this.catalogEntries.addElement(entry);
        }
        else {
            this.catalogEntries.addElement(entry);
        }
    }
    
    public void unknownEntry(final Vector strings) {
        if (strings != null && strings.size() > 0) {
            final String keyword = strings.elementAt(0);
            this.catalogManager.debug.message(2, "Unrecognized token parsing catalog", keyword);
        }
    }
    
    public void parseAllCatalogs() throws MalformedURLException, IOException {
        for (int catPos = 0; catPos < this.catalogs.size(); ++catPos) {
            Catalog c = null;
            try {
                c = this.catalogs.elementAt(catPos);
            }
            catch (final ClassCastException e) {
                final String catfile = this.catalogs.elementAt(catPos);
                c = this.newCatalog();
                c.parseCatalog(catfile);
                this.catalogs.setElementAt(c, catPos);
                c.parseAllCatalogs();
            }
        }
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e2 = en.nextElement();
            if (e2.getEntryType() == Catalog.DELEGATE_PUBLIC || e2.getEntryType() == Catalog.DELEGATE_SYSTEM || e2.getEntryType() == Catalog.DELEGATE_URI) {
                final Catalog dcat = this.newCatalog();
                dcat.parseCatalog(e2.getEntryArg(1));
            }
        }
    }
    
    public String resolveDoctype(final String entityName, String publicId, String systemId) throws MalformedURLException, IOException {
        String resolved = null;
        this.catalogManager.debug.message(3, "resolveDoctype(" + entityName + "," + publicId + "," + systemId + ")");
        systemId = this.normalizeURI(systemId);
        if (publicId != null && publicId.startsWith("urn:publicid:")) {
            publicId = PublicId.decodeURN(publicId);
        }
        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            systemId = PublicId.decodeURN(systemId);
            if (publicId != null && !publicId.equals(systemId)) {
                this.catalogManager.debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
                systemId = null;
            }
            else {
                publicId = systemId;
                systemId = null;
            }
        }
        if (systemId != null) {
            resolved = this.resolveLocalSystem(systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        if (publicId != null) {
            resolved = this.resolveLocalPublic(Catalog.DOCTYPE, entityName, publicId, systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        boolean over = this.default_override;
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Catalog.OVERRIDE) {
                over = e.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (e.getEntryType() == Catalog.DOCTYPE && e.getEntryArg(0).equals(entityName) && (over || systemId == null)) {
                    return e.getEntryArg(1);
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.DOCTYPE, entityName, publicId, systemId);
    }
    
    public String resolveDocument() throws MalformedURLException, IOException {
        this.catalogManager.debug.message(3, "resolveDocument");
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Catalog.DOCUMENT) {
                return e.getEntryArg(0);
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.DOCUMENT, null, null, null);
    }
    
    public String resolveEntity(final String entityName, String publicId, String systemId) throws MalformedURLException, IOException {
        String resolved = null;
        this.catalogManager.debug.message(3, "resolveEntity(" + entityName + "," + publicId + "," + systemId + ")");
        systemId = this.normalizeURI(systemId);
        if (publicId != null && publicId.startsWith("urn:publicid:")) {
            publicId = PublicId.decodeURN(publicId);
        }
        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            systemId = PublicId.decodeURN(systemId);
            if (publicId != null && !publicId.equals(systemId)) {
                this.catalogManager.debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
                systemId = null;
            }
            else {
                publicId = systemId;
                systemId = null;
            }
        }
        if (systemId != null) {
            resolved = this.resolveLocalSystem(systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        if (publicId != null) {
            resolved = this.resolveLocalPublic(Catalog.ENTITY, entityName, publicId, systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        boolean over = this.default_override;
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Catalog.OVERRIDE) {
                over = e.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (e.getEntryType() == Catalog.ENTITY && e.getEntryArg(0).equals(entityName) && (over || systemId == null)) {
                    return e.getEntryArg(1);
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.ENTITY, entityName, publicId, systemId);
    }
    
    public String resolveNotation(final String notationName, String publicId, String systemId) throws MalformedURLException, IOException {
        String resolved = null;
        this.catalogManager.debug.message(3, "resolveNotation(" + notationName + "," + publicId + "," + systemId + ")");
        systemId = this.normalizeURI(systemId);
        if (publicId != null && publicId.startsWith("urn:publicid:")) {
            publicId = PublicId.decodeURN(publicId);
        }
        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            systemId = PublicId.decodeURN(systemId);
            if (publicId != null && !publicId.equals(systemId)) {
                this.catalogManager.debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
                systemId = null;
            }
            else {
                publicId = systemId;
                systemId = null;
            }
        }
        if (systemId != null) {
            resolved = this.resolveLocalSystem(systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        if (publicId != null) {
            resolved = this.resolveLocalPublic(Catalog.NOTATION, notationName, publicId, systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        boolean over = this.default_override;
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Catalog.OVERRIDE) {
                over = e.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (e.getEntryType() == Catalog.NOTATION && e.getEntryArg(0).equals(notationName) && (over || systemId == null)) {
                    return e.getEntryArg(1);
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.NOTATION, notationName, publicId, systemId);
    }
    
    public String resolvePublic(String publicId, String systemId) throws MalformedURLException, IOException {
        this.catalogManager.debug.message(3, "resolvePublic(" + publicId + "," + systemId + ")");
        systemId = this.normalizeURI(systemId);
        if (publicId != null && publicId.startsWith("urn:publicid:")) {
            publicId = PublicId.decodeURN(publicId);
        }
        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            systemId = PublicId.decodeURN(systemId);
            if (publicId != null && !publicId.equals(systemId)) {
                this.catalogManager.debug.message(1, "urn:publicid: system identifier differs from public identifier; using public identifier");
                systemId = null;
            }
            else {
                publicId = systemId;
                systemId = null;
            }
        }
        if (systemId != null) {
            final String resolved = this.resolveLocalSystem(systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        final String resolved = this.resolveLocalPublic(Catalog.PUBLIC, null, publicId, systemId);
        if (resolved != null) {
            return resolved;
        }
        return this.resolveSubordinateCatalogs(Catalog.PUBLIC, null, publicId, systemId);
    }
    
    protected synchronized String resolveLocalPublic(final int entityType, final String entityName, String publicId, final String systemId) throws MalformedURLException, IOException {
        publicId = PublicId.normalize(publicId);
        if (systemId != null) {
            final String resolved = this.resolveLocalSystem(systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        boolean over = this.default_override;
        Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Catalog.OVERRIDE) {
                over = e.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (e.getEntryType() == Catalog.PUBLIC && e.getEntryArg(0).equals(publicId) && (over || systemId == null)) {
                    return e.getEntryArg(1);
                }
                continue;
            }
        }
        over = this.default_override;
        en = this.catalogEntries.elements();
        final Vector delCats = new Vector();
        while (en.hasMoreElements()) {
            final CatalogEntry e2 = en.nextElement();
            if (e2.getEntryType() == Catalog.OVERRIDE) {
                over = e2.getEntryArg(0).equalsIgnoreCase("YES");
            }
            else {
                if (e2.getEntryType() != Catalog.DELEGATE_PUBLIC || (!over && systemId != null)) {
                    continue;
                }
                final String p = e2.getEntryArg(0);
                if (p.length() > publicId.length() || !p.equals(publicId.substring(0, p.length()))) {
                    continue;
                }
                delCats.addElement(e2.getEntryArg(1));
            }
        }
        if (delCats.size() > 0) {
            Enumeration enCats = delCats.elements();
            if (this.catalogManager.debug.getDebug() > 1) {
                this.catalogManager.debug.message(2, "Switching to delegated catalog(s):");
                while (enCats.hasMoreElements()) {
                    final String delegatedCatalog = enCats.nextElement();
                    this.catalogManager.debug.message(2, "\t" + delegatedCatalog);
                }
            }
            final Catalog dcat = this.newCatalog();
            enCats = delCats.elements();
            while (enCats.hasMoreElements()) {
                final String delegatedCatalog2 = enCats.nextElement();
                dcat.parseCatalog(delegatedCatalog2);
            }
            return dcat.resolvePublic(publicId, null);
        }
        return null;
    }
    
    public String resolveSystem(String systemId) throws MalformedURLException, IOException {
        this.catalogManager.debug.message(3, "resolveSystem(" + systemId + ")");
        systemId = this.normalizeURI(systemId);
        if (systemId != null && systemId.startsWith("urn:publicid:")) {
            systemId = PublicId.decodeURN(systemId);
            return this.resolvePublic(systemId, null);
        }
        if (systemId != null) {
            final String resolved = this.resolveLocalSystem(systemId);
            if (resolved != null) {
                return resolved;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.SYSTEM, null, null, systemId);
    }
    
    protected String resolveLocalSystem(final String systemId) throws MalformedURLException, IOException {
        final String osname = SecuritySupport.getSystemProperty("os.name");
        final boolean windows = osname.indexOf("Windows") >= 0;
        Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Catalog.SYSTEM && (e.getEntryArg(0).equals(systemId) || (windows && e.getEntryArg(0).equalsIgnoreCase(systemId)))) {
                return e.getEntryArg(1);
            }
        }
        en = this.catalogEntries.elements();
        String startString = null;
        String prefix = null;
        while (en.hasMoreElements()) {
            final CatalogEntry e2 = en.nextElement();
            if (e2.getEntryType() == Catalog.REWRITE_SYSTEM) {
                final String p = e2.getEntryArg(0);
                if (p.length() > systemId.length() || !p.equals(systemId.substring(0, p.length())) || (startString != null && p.length() <= startString.length())) {
                    continue;
                }
                startString = p;
                prefix = e2.getEntryArg(1);
            }
        }
        if (prefix != null) {
            return prefix + systemId.substring(startString.length());
        }
        en = this.catalogEntries.elements();
        String suffixString = null;
        String suffixURI = null;
        while (en.hasMoreElements()) {
            final CatalogEntry e3 = en.nextElement();
            if (e3.getEntryType() == Catalog.SYSTEM_SUFFIX) {
                final String p2 = e3.getEntryArg(0);
                if (p2.length() > systemId.length() || !systemId.endsWith(p2) || (suffixString != null && p2.length() <= suffixString.length())) {
                    continue;
                }
                suffixString = p2;
                suffixURI = e3.getEntryArg(1);
            }
        }
        if (suffixURI != null) {
            return suffixURI;
        }
        en = this.catalogEntries.elements();
        final Vector delCats = new Vector();
        while (en.hasMoreElements()) {
            final CatalogEntry e4 = en.nextElement();
            if (e4.getEntryType() == Catalog.DELEGATE_SYSTEM) {
                final String p3 = e4.getEntryArg(0);
                if (p3.length() > systemId.length() || !p3.equals(systemId.substring(0, p3.length()))) {
                    continue;
                }
                delCats.addElement(e4.getEntryArg(1));
            }
        }
        if (delCats.size() > 0) {
            Enumeration enCats = delCats.elements();
            if (this.catalogManager.debug.getDebug() > 1) {
                this.catalogManager.debug.message(2, "Switching to delegated catalog(s):");
                while (enCats.hasMoreElements()) {
                    final String delegatedCatalog = enCats.nextElement();
                    this.catalogManager.debug.message(2, "\t" + delegatedCatalog);
                }
            }
            final Catalog dcat = this.newCatalog();
            enCats = delCats.elements();
            while (enCats.hasMoreElements()) {
                final String delegatedCatalog2 = enCats.nextElement();
                dcat.parseCatalog(delegatedCatalog2);
            }
            return dcat.resolveSystem(systemId);
        }
        return null;
    }
    
    public String resolveURI(String uri) throws MalformedURLException, IOException {
        this.catalogManager.debug.message(3, "resolveURI(" + uri + ")");
        uri = this.normalizeURI(uri);
        if (uri != null && uri.startsWith("urn:publicid:")) {
            uri = PublicId.decodeURN(uri);
            return this.resolvePublic(uri, null);
        }
        if (uri != null) {
            final String resolved = this.resolveLocalURI(uri);
            if (resolved != null) {
                return resolved;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.URI, null, null, uri);
    }
    
    protected String resolveLocalURI(final String uri) throws MalformedURLException, IOException {
        Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Catalog.URI && e.getEntryArg(0).equals(uri)) {
                return e.getEntryArg(1);
            }
        }
        en = this.catalogEntries.elements();
        String startString = null;
        String prefix = null;
        while (en.hasMoreElements()) {
            final CatalogEntry e2 = en.nextElement();
            if (e2.getEntryType() == Catalog.REWRITE_URI) {
                final String p = e2.getEntryArg(0);
                if (p.length() > uri.length() || !p.equals(uri.substring(0, p.length())) || (startString != null && p.length() <= startString.length())) {
                    continue;
                }
                startString = p;
                prefix = e2.getEntryArg(1);
            }
        }
        if (prefix != null) {
            return prefix + uri.substring(startString.length());
        }
        en = this.catalogEntries.elements();
        String suffixString = null;
        String suffixURI = null;
        while (en.hasMoreElements()) {
            final CatalogEntry e3 = en.nextElement();
            if (e3.getEntryType() == Catalog.URI_SUFFIX) {
                final String p2 = e3.getEntryArg(0);
                if (p2.length() > uri.length() || !uri.endsWith(p2) || (suffixString != null && p2.length() <= suffixString.length())) {
                    continue;
                }
                suffixString = p2;
                suffixURI = e3.getEntryArg(1);
            }
        }
        if (suffixURI != null) {
            return suffixURI;
        }
        en = this.catalogEntries.elements();
        final Vector delCats = new Vector();
        while (en.hasMoreElements()) {
            final CatalogEntry e4 = en.nextElement();
            if (e4.getEntryType() == Catalog.DELEGATE_URI) {
                final String p3 = e4.getEntryArg(0);
                if (p3.length() > uri.length() || !p3.equals(uri.substring(0, p3.length()))) {
                    continue;
                }
                delCats.addElement(e4.getEntryArg(1));
            }
        }
        if (delCats.size() > 0) {
            Enumeration enCats = delCats.elements();
            if (this.catalogManager.debug.getDebug() > 1) {
                this.catalogManager.debug.message(2, "Switching to delegated catalog(s):");
                while (enCats.hasMoreElements()) {
                    final String delegatedCatalog = enCats.nextElement();
                    this.catalogManager.debug.message(2, "\t" + delegatedCatalog);
                }
            }
            final Catalog dcat = this.newCatalog();
            enCats = delCats.elements();
            while (enCats.hasMoreElements()) {
                final String delegatedCatalog2 = enCats.nextElement();
                dcat.parseCatalog(delegatedCatalog2);
            }
            return dcat.resolveURI(uri);
        }
        return null;
    }
    
    protected synchronized String resolveSubordinateCatalogs(final int entityType, final String entityName, final String publicId, final String systemId) throws MalformedURLException, IOException {
        for (int catPos = 0; catPos < this.catalogs.size(); ++catPos) {
            Catalog c = null;
            try {
                c = this.catalogs.elementAt(catPos);
            }
            catch (final ClassCastException e) {
                final String catfile = this.catalogs.elementAt(catPos);
                c = this.newCatalog();
                try {
                    c.parseCatalog(catfile);
                }
                catch (final MalformedURLException mue) {
                    this.catalogManager.debug.message(1, "Malformed Catalog URL", catfile);
                }
                catch (final FileNotFoundException fnfe) {
                    this.catalogManager.debug.message(1, "Failed to load catalog, file not found", catfile);
                }
                catch (final IOException ioe) {
                    this.catalogManager.debug.message(1, "Failed to load catalog, I/O error", catfile);
                }
                this.catalogs.setElementAt(c, catPos);
            }
            String resolved = null;
            if (entityType == Catalog.DOCTYPE) {
                resolved = c.resolveDoctype(entityName, publicId, systemId);
            }
            else if (entityType == Catalog.DOCUMENT) {
                resolved = c.resolveDocument();
            }
            else if (entityType == Catalog.ENTITY) {
                resolved = c.resolveEntity(entityName, publicId, systemId);
            }
            else if (entityType == Catalog.NOTATION) {
                resolved = c.resolveNotation(entityName, publicId, systemId);
            }
            else if (entityType == Catalog.PUBLIC) {
                resolved = c.resolvePublic(publicId, systemId);
            }
            else if (entityType == Catalog.SYSTEM) {
                resolved = c.resolveSystem(systemId);
            }
            else if (entityType == Catalog.URI) {
                resolved = c.resolveURI(systemId);
            }
            if (resolved != null) {
                return resolved;
            }
        }
        return null;
    }
    
    protected String fixSlashes(final String sysid) {
        return sysid.replace('\\', '/');
    }
    
    protected String makeAbsolute(String sysid) {
        URL local = null;
        sysid = this.fixSlashes(sysid);
        try {
            local = new URL(this.base, sysid);
        }
        catch (final MalformedURLException e) {
            this.catalogManager.debug.message(1, "Malformed URL on system identifier", sysid);
        }
        if (local != null) {
            return local.toString();
        }
        return sysid;
    }
    
    protected String normalizeURI(final String uriref) {
        if (uriref == null) {
            return null;
        }
        byte[] bytes;
        try {
            bytes = uriref.getBytes("UTF-8");
        }
        catch (final UnsupportedEncodingException uee) {
            this.catalogManager.debug.message(1, "UTF-8 is an unsupported encoding!?");
            return uriref;
        }
        final StringBuilder newRef = new StringBuilder(bytes.length);
        for (int count = 0; count < bytes.length; ++count) {
            final int ch = bytes[count] & 0xFF;
            if (ch <= 32 || ch > 127 || ch == 34 || ch == 60 || ch == 62 || ch == 92 || ch == 94 || ch == 96 || ch == 123 || ch == 124 || ch == 125 || ch == 127) {
                newRef.append(this.encodedByte(ch));
            }
            else {
                newRef.append((char)bytes[count]);
            }
        }
        return newRef.toString();
    }
    
    protected String encodedByte(final int b) {
        final String hex = Integer.toHexString(b).toUpperCase();
        if (hex.length() < 2) {
            return "%0" + hex;
        }
        return "%" + hex;
    }
    
    protected void addDelegate(final CatalogEntry entry) {
        int pos = 0;
        final String partial = entry.getEntryArg(0);
        final Enumeration local = this.localDelegate.elements();
        while (local.hasMoreElements()) {
            final CatalogEntry dpe = local.nextElement();
            final String dp = dpe.getEntryArg(0);
            if (dp.equals(partial)) {
                return;
            }
            if (dp.length() > partial.length()) {
                ++pos;
            }
            if (dp.length() < partial.length()) {
                break;
            }
        }
        if (this.localDelegate.size() == 0) {
            this.localDelegate.addElement(entry);
        }
        else {
            this.localDelegate.insertElementAt(entry, pos);
        }
    }
    
    static {
        BASE = CatalogEntry.addEntryType("BASE", 1);
        CATALOG = CatalogEntry.addEntryType("CATALOG", 1);
        DOCUMENT = CatalogEntry.addEntryType("DOCUMENT", 1);
        OVERRIDE = CatalogEntry.addEntryType("OVERRIDE", 1);
        SGMLDECL = CatalogEntry.addEntryType("SGMLDECL", 1);
        DELEGATE_PUBLIC = CatalogEntry.addEntryType("DELEGATE_PUBLIC", 2);
        DELEGATE_SYSTEM = CatalogEntry.addEntryType("DELEGATE_SYSTEM", 2);
        DELEGATE_URI = CatalogEntry.addEntryType("DELEGATE_URI", 2);
        DOCTYPE = CatalogEntry.addEntryType("DOCTYPE", 2);
        DTDDECL = CatalogEntry.addEntryType("DTDDECL", 2);
        ENTITY = CatalogEntry.addEntryType("ENTITY", 2);
        LINKTYPE = CatalogEntry.addEntryType("LINKTYPE", 2);
        NOTATION = CatalogEntry.addEntryType("NOTATION", 2);
        PUBLIC = CatalogEntry.addEntryType("PUBLIC", 2);
        SYSTEM = CatalogEntry.addEntryType("SYSTEM", 2);
        URI = CatalogEntry.addEntryType("URI", 2);
        REWRITE_SYSTEM = CatalogEntry.addEntryType("REWRITE_SYSTEM", 2);
        REWRITE_URI = CatalogEntry.addEntryType("REWRITE_URI", 2);
        SYSTEM_SUFFIX = CatalogEntry.addEntryType("SYSTEM_SUFFIX", 2);
        URI_SUFFIX = CatalogEntry.addEntryType("URI_SUFFIX", 2);
    }
}
