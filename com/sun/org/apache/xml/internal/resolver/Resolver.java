package com.sun.org.apache.xml.internal.resolver;

import java.io.FileNotFoundException;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import java.util.Vector;
import java.net.URLConnection;
import java.io.InputStream;
import java.net.URL;
import java.io.IOException;
import java.net.MalformedURLException;
import java.util.Enumeration;
import javax.xml.parsers.SAXParserFactory;
import com.sun.org.apache.xml.internal.resolver.readers.TR9401CatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.CatalogReader;
import com.sun.org.apache.xml.internal.resolver.readers.SAXCatalogReader;
import jdk.xml.internal.JdkXmlUtils;

public class Resolver extends Catalog
{
    public static final int URISUFFIX;
    public static final int SYSTEMSUFFIX;
    public static final int RESOLVER;
    public static final int SYSTEMREVERSE;
    
    @Override
    public void setupReaders() {
        final SAXParserFactory spf = JdkXmlUtils.getSAXFactory(this.catalogManager.overrideDefaultParser());
        spf.setValidating(false);
        final SAXCatalogReader saxReader = new SAXCatalogReader(spf);
        saxReader.setCatalogParser(null, "XMLCatalog", "com.sun.org.apache.xml.internal.resolver.readers.XCatalogReader");
        saxReader.setCatalogParser("urn:oasis:names:tc:entity:xmlns:xml:catalog", "catalog", "com.sun.org.apache.xml.internal.resolver.readers.ExtendedXMLCatalogReader");
        this.addReader("application/xml", saxReader);
        final TR9401CatalogReader textReader = new TR9401CatalogReader();
        this.addReader("text/plain", textReader);
    }
    
    @Override
    public void addEntry(final CatalogEntry entry) {
        final int type = entry.getEntryType();
        if (type == Resolver.URISUFFIX) {
            final String suffix = this.normalizeURI(entry.getEntryArg(0));
            final String fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(1, fsi);
            this.catalogManager.debug.message(4, "URISUFFIX", suffix, fsi);
        }
        else if (type == Resolver.SYSTEMSUFFIX) {
            final String suffix = this.normalizeURI(entry.getEntryArg(0));
            final String fsi = this.makeAbsolute(this.normalizeURI(entry.getEntryArg(1)));
            entry.setEntryArg(1, fsi);
            this.catalogManager.debug.message(4, "SYSTEMSUFFIX", suffix, fsi);
        }
        super.addEntry(entry);
    }
    
    @Override
    public String resolveURI(final String uri) throws MalformedURLException, IOException {
        String resolved = super.resolveURI(uri);
        if (resolved != null) {
            return resolved;
        }
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Resolver.RESOLVER) {
                resolved = this.resolveExternalSystem(uri, e.getEntryArg(0));
                if (resolved != null) {
                    return resolved;
                }
                continue;
            }
            else {
                if (e.getEntryType() != Resolver.URISUFFIX) {
                    continue;
                }
                final String suffix = e.getEntryArg(0);
                final String result = e.getEntryArg(1);
                if (suffix.length() <= uri.length() && uri.substring(uri.length() - suffix.length()).equals(suffix)) {
                    return result;
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.URI, null, null, uri);
    }
    
    @Override
    public String resolveSystem(final String systemId) throws MalformedURLException, IOException {
        String resolved = super.resolveSystem(systemId);
        if (resolved != null) {
            return resolved;
        }
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Resolver.RESOLVER) {
                resolved = this.resolveExternalSystem(systemId, e.getEntryArg(0));
                if (resolved != null) {
                    return resolved;
                }
                continue;
            }
            else {
                if (e.getEntryType() != Resolver.SYSTEMSUFFIX) {
                    continue;
                }
                final String suffix = e.getEntryArg(0);
                final String result = e.getEntryArg(1);
                if (suffix.length() <= systemId.length() && systemId.substring(systemId.length() - suffix.length()).equals(suffix)) {
                    return result;
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.SYSTEM, null, null, systemId);
    }
    
    @Override
    public String resolvePublic(final String publicId, final String systemId) throws MalformedURLException, IOException {
        String resolved = super.resolvePublic(publicId, systemId);
        if (resolved != null) {
            return resolved;
        }
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Resolver.RESOLVER) {
                if (systemId != null) {
                    resolved = this.resolveExternalSystem(systemId, e.getEntryArg(0));
                    if (resolved != null) {
                        return resolved;
                    }
                }
                resolved = this.resolveExternalPublic(publicId, e.getEntryArg(0));
                if (resolved != null) {
                    return resolved;
                }
                continue;
            }
        }
        return this.resolveSubordinateCatalogs(Catalog.PUBLIC, null, publicId, systemId);
    }
    
    protected String resolveExternalSystem(final String systemId, final String resolver) throws MalformedURLException, IOException {
        final Resolver r = this.queryResolver(resolver, "i2l", systemId, null);
        if (r != null) {
            return r.resolveSystem(systemId);
        }
        return null;
    }
    
    protected String resolveExternalPublic(final String publicId, final String resolver) throws MalformedURLException, IOException {
        final Resolver r = this.queryResolver(resolver, "fpi2l", publicId, null);
        if (r != null) {
            return r.resolvePublic(publicId, null);
        }
        return null;
    }
    
    protected Resolver queryResolver(final String resolver, final String command, final String arg1, final String arg2) {
        final InputStream iStream = null;
        final String RFC2483 = resolver + "?command=" + command + "&format=tr9401&uri=" + arg1 + "&uri2=" + arg2;
        final String line = null;
        try {
            final URL url = new URL(RFC2483);
            final URLConnection urlCon = url.openConnection();
            urlCon.setUseCaches(false);
            final Resolver r = (Resolver)this.newCatalog();
            String cType = urlCon.getContentType();
            if (cType.indexOf(";") > 0) {
                cType = cType.substring(0, cType.indexOf(";"));
            }
            r.parseCatalog(cType, urlCon.getInputStream());
            return r;
        }
        catch (final CatalogException cex) {
            if (cex.getExceptionType() == 6) {
                this.catalogManager.debug.message(1, "Unparseable catalog: " + RFC2483);
            }
            else if (cex.getExceptionType() == 5) {
                this.catalogManager.debug.message(1, "Unknown catalog format: " + RFC2483);
            }
            return null;
        }
        catch (final MalformedURLException mue) {
            this.catalogManager.debug.message(1, "Malformed resolver URL: " + RFC2483);
            return null;
        }
        catch (final IOException ie) {
            this.catalogManager.debug.message(1, "I/O Exception opening resolver: " + RFC2483);
            return null;
        }
    }
    
    private Vector appendVector(final Vector vec, final Vector appvec) {
        if (appvec != null) {
            for (int count = 0; count < appvec.size(); ++count) {
                vec.addElement(appvec.elementAt(count));
            }
        }
        return vec;
    }
    
    public Vector resolveAllSystemReverse(final String systemId) throws MalformedURLException, IOException {
        Vector resolved = new Vector();
        if (systemId != null) {
            final Vector localResolved = this.resolveLocalSystemReverse(systemId);
            resolved = this.appendVector(resolved, localResolved);
        }
        final Vector subResolved = this.resolveAllSubordinateCatalogs(Resolver.SYSTEMREVERSE, null, null, systemId);
        return this.appendVector(resolved, subResolved);
    }
    
    public String resolveSystemReverse(final String systemId) throws MalformedURLException, IOException {
        final Vector resolved = this.resolveAllSystemReverse(systemId);
        if (resolved != null && resolved.size() > 0) {
            return resolved.elementAt(0);
        }
        return null;
    }
    
    public Vector resolveAllSystem(final String systemId) throws MalformedURLException, IOException {
        Vector resolutions = new Vector();
        if (systemId != null) {
            final Vector localResolutions = this.resolveAllLocalSystem(systemId);
            resolutions = this.appendVector(resolutions, localResolutions);
        }
        final Vector subResolutions = this.resolveAllSubordinateCatalogs(Resolver.SYSTEM, null, null, systemId);
        resolutions = this.appendVector(resolutions, subResolutions);
        if (resolutions.size() > 0) {
            return resolutions;
        }
        return null;
    }
    
    private Vector resolveAllLocalSystem(final String systemId) {
        final Vector map = new Vector();
        final String osname = SecuritySupport.getSystemProperty("os.name");
        final boolean windows = osname.indexOf("Windows") >= 0;
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Resolver.SYSTEM && (e.getEntryArg(0).equals(systemId) || (windows && e.getEntryArg(0).equalsIgnoreCase(systemId)))) {
                map.addElement(e.getEntryArg(1));
            }
        }
        if (map.size() == 0) {
            return null;
        }
        return map;
    }
    
    private Vector resolveLocalSystemReverse(final String systemId) {
        final Vector map = new Vector();
        final String osname = SecuritySupport.getSystemProperty("os.name");
        final boolean windows = osname.indexOf("Windows") >= 0;
        final Enumeration en = this.catalogEntries.elements();
        while (en.hasMoreElements()) {
            final CatalogEntry e = en.nextElement();
            if (e.getEntryType() == Resolver.SYSTEM && (e.getEntryArg(1).equals(systemId) || (windows && e.getEntryArg(1).equalsIgnoreCase(systemId)))) {
                map.addElement(e.getEntryArg(0));
            }
        }
        if (map.size() == 0) {
            return null;
        }
        return map;
    }
    
    private synchronized Vector resolveAllSubordinateCatalogs(final int entityType, final String entityName, final String publicId, final String systemId) throws MalformedURLException, IOException {
        Vector resolutions = new Vector();
        for (int catPos = 0; catPos < this.catalogs.size(); ++catPos) {
            Resolver c = null;
            try {
                c = this.catalogs.elementAt(catPos);
            }
            catch (final ClassCastException e) {
                final String catfile = this.catalogs.elementAt(catPos);
                c = (Resolver)this.newCatalog();
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
            if (entityType == Resolver.DOCTYPE) {
                resolved = c.resolveDoctype(entityName, publicId, systemId);
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            }
            else if (entityType == Resolver.DOCUMENT) {
                resolved = c.resolveDocument();
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            }
            else if (entityType == Resolver.ENTITY) {
                resolved = c.resolveEntity(entityName, publicId, systemId);
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            }
            else if (entityType == Resolver.NOTATION) {
                resolved = c.resolveNotation(entityName, publicId, systemId);
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            }
            else if (entityType == Resolver.PUBLIC) {
                resolved = c.resolvePublic(publicId, systemId);
                if (resolved != null) {
                    resolutions.addElement(resolved);
                    return resolutions;
                }
            }
            else {
                if (entityType == Resolver.SYSTEM) {
                    final Vector localResolutions = c.resolveAllSystem(systemId);
                    resolutions = this.appendVector(resolutions, localResolutions);
                    break;
                }
                if (entityType == Resolver.SYSTEMREVERSE) {
                    final Vector localResolutions = c.resolveAllSystemReverse(systemId);
                    resolutions = this.appendVector(resolutions, localResolutions);
                }
            }
        }
        if (resolutions != null) {
            return resolutions;
        }
        return null;
    }
    
    static {
        URISUFFIX = CatalogEntry.addEntryType("URISUFFIX", 2);
        SYSTEMSUFFIX = CatalogEntry.addEntryType("SYSTEMSUFFIX", 2);
        RESOLVER = CatalogEntry.addEntryType("RESOLVER", 1);
        SYSTEMREVERSE = CatalogEntry.addEntryType("SYSTEMREVERSE", 1);
    }
}
