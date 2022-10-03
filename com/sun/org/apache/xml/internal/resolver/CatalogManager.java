package com.sun.org.apache.xml.internal.resolver;

import sun.reflect.misc.ReflectUtil;
import java.net.MalformedURLException;
import java.util.StringTokenizer;
import java.util.Vector;
import java.io.InputStream;
import java.io.IOException;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import com.sun.org.apache.xerces.internal.utils.SecuritySupport;
import com.sun.org.apache.xml.internal.resolver.helpers.Debug;
import java.net.URL;
import java.util.ResourceBundle;
import com.sun.org.apache.xml.internal.resolver.helpers.BootstrapResolver;

public class CatalogManager
{
    private static String pFiles;
    private static String pVerbosity;
    private static String pPrefer;
    private static String pStatic;
    private static String pAllowPI;
    private static String pClassname;
    private static String pIgnoreMissing;
    private static CatalogManager staticManager;
    private BootstrapResolver bResolver;
    private boolean ignoreMissingProperties;
    private ResourceBundle resources;
    private String propertyFile;
    private URL propertyFileURI;
    private String defaultCatalogFiles;
    private String catalogFiles;
    private boolean fromPropertiesFile;
    private int defaultVerbosity;
    private Integer verbosity;
    private boolean defaultPreferPublic;
    private Boolean preferPublic;
    private boolean defaultUseStaticCatalog;
    private Boolean useStaticCatalog;
    private static Catalog staticCatalog;
    private boolean defaultOasisXMLCatalogPI;
    private Boolean oasisXMLCatalogPI;
    private boolean defaultRelativeCatalogs;
    private Boolean relativeCatalogs;
    private String catalogClassName;
    private boolean overrideDefaultParser;
    public Debug debug;
    
    public CatalogManager() {
        this.bResolver = new BootstrapResolver();
        this.ignoreMissingProperties = (SecuritySupport.getSystemProperty(CatalogManager.pIgnoreMissing) != null || SecuritySupport.getSystemProperty(CatalogManager.pFiles) != null);
        this.propertyFile = "CatalogManager.properties";
        this.propertyFileURI = null;
        this.defaultCatalogFiles = "./xcatalog";
        this.catalogFiles = null;
        this.fromPropertiesFile = false;
        this.defaultVerbosity = 1;
        this.verbosity = null;
        this.defaultPreferPublic = true;
        this.preferPublic = null;
        this.defaultUseStaticCatalog = true;
        this.useStaticCatalog = null;
        this.defaultOasisXMLCatalogPI = true;
        this.oasisXMLCatalogPI = null;
        this.defaultRelativeCatalogs = true;
        this.relativeCatalogs = null;
        this.catalogClassName = null;
        this.debug = null;
        this.init();
    }
    
    public CatalogManager(final String propertyFile) {
        this.bResolver = new BootstrapResolver();
        this.ignoreMissingProperties = (SecuritySupport.getSystemProperty(CatalogManager.pIgnoreMissing) != null || SecuritySupport.getSystemProperty(CatalogManager.pFiles) != null);
        this.propertyFile = "CatalogManager.properties";
        this.propertyFileURI = null;
        this.defaultCatalogFiles = "./xcatalog";
        this.catalogFiles = null;
        this.fromPropertiesFile = false;
        this.defaultVerbosity = 1;
        this.verbosity = null;
        this.defaultPreferPublic = true;
        this.preferPublic = null;
        this.defaultUseStaticCatalog = true;
        this.useStaticCatalog = null;
        this.defaultOasisXMLCatalogPI = true;
        this.oasisXMLCatalogPI = null;
        this.defaultRelativeCatalogs = true;
        this.relativeCatalogs = null;
        this.catalogClassName = null;
        this.debug = null;
        this.propertyFile = propertyFile;
        this.init();
    }
    
    private void init() {
        this.debug = new Debug();
        if (System.getSecurityManager() == null) {
            this.overrideDefaultParser = true;
        }
    }
    
    public void setBootstrapResolver(final BootstrapResolver resolver) {
        this.bResolver = resolver;
    }
    
    public BootstrapResolver getBootstrapResolver() {
        return this.bResolver;
    }
    
    private synchronized void readProperties() {
        try {
            this.propertyFileURI = CatalogManager.class.getResource("/" + this.propertyFile);
            final InputStream in = CatalogManager.class.getResourceAsStream("/" + this.propertyFile);
            if (in == null) {
                if (!this.ignoreMissingProperties) {
                    System.err.println("Cannot find " + this.propertyFile);
                    this.ignoreMissingProperties = true;
                }
                return;
            }
            this.resources = new PropertyResourceBundle(in);
        }
        catch (final MissingResourceException mre) {
            if (!this.ignoreMissingProperties) {
                System.err.println("Cannot read " + this.propertyFile);
            }
        }
        catch (final IOException e) {
            if (!this.ignoreMissingProperties) {
                System.err.println("Failure trying to read " + this.propertyFile);
            }
        }
        if (this.verbosity == null) {
            try {
                final String verbStr = this.resources.getString("verbosity");
                final int verb = Integer.parseInt(verbStr.trim());
                this.debug.setDebug(verb);
                this.verbosity = new Integer(verb);
            }
            catch (final Exception ex) {}
        }
    }
    
    public static CatalogManager getStaticManager() {
        return CatalogManager.staticManager;
    }
    
    public boolean getIgnoreMissingProperties() {
        return this.ignoreMissingProperties;
    }
    
    public void setIgnoreMissingProperties(final boolean ignore) {
        this.ignoreMissingProperties = ignore;
    }
    
    @Deprecated
    public void ignoreMissingProperties(final boolean ignore) {
        this.setIgnoreMissingProperties(ignore);
    }
    
    private int queryVerbosity() {
        final String defaultVerbStr = Integer.toString(this.defaultVerbosity);
        String verbStr = SecuritySupport.getSystemProperty(CatalogManager.pVerbosity);
        if (verbStr == null) {
            if (this.resources == null) {
                this.readProperties();
            }
            if (this.resources != null) {
                try {
                    verbStr = this.resources.getString("verbosity");
                }
                catch (final MissingResourceException e) {
                    verbStr = defaultVerbStr;
                }
            }
            else {
                verbStr = defaultVerbStr;
            }
        }
        int verb = this.defaultVerbosity;
        try {
            verb = Integer.parseInt(verbStr.trim());
        }
        catch (final Exception e2) {
            System.err.println("Cannot parse verbosity: \"" + verbStr + "\"");
        }
        if (this.verbosity == null) {
            this.debug.setDebug(verb);
            this.verbosity = new Integer(verb);
        }
        return verb;
    }
    
    public int getVerbosity() {
        if (this.verbosity == null) {
            this.verbosity = new Integer(this.queryVerbosity());
        }
        return this.verbosity;
    }
    
    public void setVerbosity(final int verbosity) {
        this.verbosity = new Integer(verbosity);
        this.debug.setDebug(verbosity);
    }
    
    @Deprecated
    public int verbosity() {
        return this.getVerbosity();
    }
    
    private boolean queryRelativeCatalogs() {
        if (this.resources == null) {
            this.readProperties();
        }
        if (this.resources == null) {
            return this.defaultRelativeCatalogs;
        }
        try {
            final String allow = this.resources.getString("relative-catalogs");
            return allow.equalsIgnoreCase("true") || allow.equalsIgnoreCase("yes") || allow.equalsIgnoreCase("1");
        }
        catch (final MissingResourceException e) {
            return this.defaultRelativeCatalogs;
        }
    }
    
    public boolean getRelativeCatalogs() {
        if (this.relativeCatalogs == null) {
            this.relativeCatalogs = new Boolean(this.queryRelativeCatalogs());
        }
        return this.relativeCatalogs;
    }
    
    public void setRelativeCatalogs(final boolean relative) {
        this.relativeCatalogs = new Boolean(relative);
    }
    
    @Deprecated
    public boolean relativeCatalogs() {
        return this.getRelativeCatalogs();
    }
    
    private String queryCatalogFiles() {
        String catalogList = SecuritySupport.getSystemProperty(CatalogManager.pFiles);
        this.fromPropertiesFile = false;
        if (catalogList == null) {
            if (this.resources == null) {
                this.readProperties();
            }
            if (this.resources != null) {
                try {
                    catalogList = this.resources.getString("catalogs");
                    this.fromPropertiesFile = true;
                }
                catch (final MissingResourceException e) {
                    System.err.println(this.propertyFile + ": catalogs not found.");
                    catalogList = null;
                }
            }
        }
        if (catalogList == null) {
            catalogList = this.defaultCatalogFiles;
        }
        return catalogList;
    }
    
    public Vector getCatalogFiles() {
        if (this.catalogFiles == null) {
            this.catalogFiles = this.queryCatalogFiles();
        }
        final StringTokenizer files = new StringTokenizer(this.catalogFiles, ";");
        final Vector catalogs = new Vector();
        while (files.hasMoreTokens()) {
            String catalogFile = files.nextToken();
            URL absURI = null;
            if (this.fromPropertiesFile && !this.relativeCatalogs()) {
                try {
                    absURI = new URL(this.propertyFileURI, catalogFile);
                    catalogFile = absURI.toString();
                }
                catch (final MalformedURLException mue) {
                    absURI = null;
                }
            }
            catalogs.add(catalogFile);
        }
        return catalogs;
    }
    
    public void setCatalogFiles(final String fileList) {
        this.catalogFiles = fileList;
        this.fromPropertiesFile = false;
    }
    
    @Deprecated
    public Vector catalogFiles() {
        return this.getCatalogFiles();
    }
    
    private boolean queryPreferPublic() {
        String prefer = SecuritySupport.getSystemProperty(CatalogManager.pPrefer);
        if (prefer == null) {
            if (this.resources == null) {
                this.readProperties();
            }
            if (this.resources == null) {
                return this.defaultPreferPublic;
            }
            try {
                prefer = this.resources.getString("prefer");
            }
            catch (final MissingResourceException e) {
                return this.defaultPreferPublic;
            }
        }
        if (prefer == null) {
            return this.defaultPreferPublic;
        }
        return prefer.equalsIgnoreCase("public");
    }
    
    public boolean getPreferPublic() {
        if (this.preferPublic == null) {
            this.preferPublic = new Boolean(this.queryPreferPublic());
        }
        return this.preferPublic;
    }
    
    public void setPreferPublic(final boolean preferPublic) {
        this.preferPublic = new Boolean(preferPublic);
    }
    
    @Deprecated
    public boolean preferPublic() {
        return this.getPreferPublic();
    }
    
    private boolean queryUseStaticCatalog() {
        String staticCatalog = SecuritySupport.getSystemProperty(CatalogManager.pStatic);
        if (staticCatalog == null) {
            if (this.resources == null) {
                this.readProperties();
            }
            if (this.resources == null) {
                return this.defaultUseStaticCatalog;
            }
            try {
                staticCatalog = this.resources.getString("static-catalog");
            }
            catch (final MissingResourceException e) {
                return this.defaultUseStaticCatalog;
            }
        }
        if (staticCatalog == null) {
            return this.defaultUseStaticCatalog;
        }
        return staticCatalog.equalsIgnoreCase("true") || staticCatalog.equalsIgnoreCase("yes") || staticCatalog.equalsIgnoreCase("1");
    }
    
    public boolean getUseStaticCatalog() {
        if (this.useStaticCatalog == null) {
            this.useStaticCatalog = new Boolean(this.queryUseStaticCatalog());
        }
        return this.useStaticCatalog;
    }
    
    public void setUseStaticCatalog(final boolean useStatic) {
        this.useStaticCatalog = new Boolean(useStatic);
    }
    
    @Deprecated
    public boolean staticCatalog() {
        return this.getUseStaticCatalog();
    }
    
    public Catalog getPrivateCatalog() {
        Catalog catalog = CatalogManager.staticCatalog;
        if (this.useStaticCatalog == null) {
            this.useStaticCatalog = new Boolean(this.getUseStaticCatalog());
        }
        if (catalog != null) {
            if (this.useStaticCatalog) {
                return catalog;
            }
        }
        try {
            final String catalogClassName = this.getCatalogClassName();
            if (catalogClassName == null) {
                catalog = new Catalog();
            }
            else {
                try {
                    catalog = (Catalog)ReflectUtil.forName(catalogClassName).newInstance();
                }
                catch (final ClassNotFoundException cnfe) {
                    this.debug.message(1, "Catalog class named '" + catalogClassName + "' could not be found. Using default.");
                    catalog = new Catalog();
                }
                catch (final ClassCastException cnfe2) {
                    this.debug.message(1, "Class named '" + catalogClassName + "' is not a Catalog. Using default.");
                    catalog = new Catalog();
                }
            }
            catalog.setCatalogManager(this);
            catalog.setupReaders();
            catalog.loadSystemCatalogs();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
        if (this.useStaticCatalog) {
            CatalogManager.staticCatalog = catalog;
        }
        return catalog;
    }
    
    public Catalog getCatalog() {
        Catalog catalog = CatalogManager.staticCatalog;
        if (this.useStaticCatalog == null) {
            this.useStaticCatalog = new Boolean(this.getUseStaticCatalog());
        }
        if (catalog == null || !this.useStaticCatalog) {
            catalog = this.getPrivateCatalog();
            if (this.useStaticCatalog) {
                CatalogManager.staticCatalog = catalog;
            }
        }
        return catalog;
    }
    
    public boolean queryAllowOasisXMLCatalogPI() {
        String allow = SecuritySupport.getSystemProperty(CatalogManager.pAllowPI);
        if (allow == null) {
            if (this.resources == null) {
                this.readProperties();
            }
            if (this.resources == null) {
                return this.defaultOasisXMLCatalogPI;
            }
            try {
                allow = this.resources.getString("allow-oasis-xml-catalog-pi");
            }
            catch (final MissingResourceException e) {
                return this.defaultOasisXMLCatalogPI;
            }
        }
        if (allow == null) {
            return this.defaultOasisXMLCatalogPI;
        }
        return allow.equalsIgnoreCase("true") || allow.equalsIgnoreCase("yes") || allow.equalsIgnoreCase("1");
    }
    
    public boolean getAllowOasisXMLCatalogPI() {
        if (this.oasisXMLCatalogPI == null) {
            this.oasisXMLCatalogPI = new Boolean(this.queryAllowOasisXMLCatalogPI());
        }
        return this.oasisXMLCatalogPI;
    }
    
    public boolean overrideDefaultParser() {
        return this.overrideDefaultParser;
    }
    
    public void setAllowOasisXMLCatalogPI(final boolean allowPI) {
        this.oasisXMLCatalogPI = new Boolean(allowPI);
    }
    
    @Deprecated
    public boolean allowOasisXMLCatalogPI() {
        return this.getAllowOasisXMLCatalogPI();
    }
    
    public String queryCatalogClassName() {
        final String className = SecuritySupport.getSystemProperty(CatalogManager.pClassname);
        if (className == null) {
            if (this.resources == null) {
                this.readProperties();
            }
            if (this.resources == null) {
                return null;
            }
            try {
                return this.resources.getString("catalog-class-name");
            }
            catch (final MissingResourceException e) {
                return null;
            }
        }
        return className;
    }
    
    public String getCatalogClassName() {
        if (this.catalogClassName == null) {
            this.catalogClassName = this.queryCatalogClassName();
        }
        return this.catalogClassName;
    }
    
    public void setCatalogClassName(final String className) {
        this.catalogClassName = className;
    }
    
    @Deprecated
    public String catalogClassName() {
        return this.getCatalogClassName();
    }
    
    static {
        CatalogManager.pFiles = "xml.catalog.files";
        CatalogManager.pVerbosity = "xml.catalog.verbosity";
        CatalogManager.pPrefer = "xml.catalog.prefer";
        CatalogManager.pStatic = "xml.catalog.staticCatalog";
        CatalogManager.pAllowPI = "xml.catalog.allowPI";
        CatalogManager.pClassname = "xml.catalog.className";
        CatalogManager.pIgnoreMissing = "xml.catalog.ignoreMissing";
        CatalogManager.staticManager = new CatalogManager();
        CatalogManager.staticCatalog = null;
    }
}
