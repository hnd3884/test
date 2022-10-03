package com.lowagie.text;

import java.net.URLConnection;
import java.io.InputStream;
import java.security.CodeSource;
import java.security.ProtectionDomain;
import java.util.jar.JarInputStream;
import java.net.JarURLConnection;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.MessageFormat;
import java.util.jar.Manifest;
import java.util.jar.Attributes;

final class VersionBean
{
    public static final Version VERSION;
    
    public String getVendor() {
        return VersionBean.VERSION.getImplementationVendor();
    }
    
    public String getTitle() {
        return VersionBean.VERSION.getImplementationTitle();
    }
    
    public String getTimestamp() {
        return VersionBean.VERSION.getScmTimestamp();
    }
    
    public Version getVersion() {
        return VersionBean.VERSION;
    }
    
    @Override
    public String toString() {
        return VersionBean.VERSION.toString();
    }
    
    static {
        VERSION = new Version();
    }
    
    public static class Version
    {
        private static final String UNKNOWN = "";
        private String implementationVendor;
        private String implementationVersion;
        private String bundleVersion;
        private String implementationTitle;
        private String scmTimestamp;
        private String fullVersionString;
        private boolean containsDataFromManifest;
        
        public Version() {
            this.implementationVendor = "";
            this.implementationVersion = "1.0.0";
            this.bundleVersion = "1.0.0";
            this.implementationTitle = "";
            this.scmTimestamp = "";
            this.fullVersionString = "";
            this.containsDataFromManifest = false;
            this.initialize();
        }
        
        private String getAttributeValueOrDefault(final Attributes attributes, final String name) {
            String value = attributes.getValue(name);
            if (value == null) {
                value = "";
            }
            return value;
        }
        
        private void initialize() {
            Manifest manifest = null;
            try {
                manifest = this.readManifest();
                if (manifest != null) {
                    this.initializePropertiesFromManifest(manifest);
                    this.initializeDerivativeProperties();
                }
            }
            catch (final Exception ex) {}
        }
        
        private void initializePropertiesFromManifest(final Manifest manifest) {
            this.containsDataFromManifest = true;
            final Attributes attributes = manifest.getMainAttributes();
            this.implementationVendor = this.getAttributeValueOrDefault(attributes, "Implementation-Vendor");
            this.implementationVersion = this.getAttributeValueOrDefault(attributes, "Implementation-Version");
            this.bundleVersion = this.getAttributeValueOrDefault(attributes, "Bundle-Version");
            this.implementationTitle = this.getAttributeValueOrDefault(attributes, "Implementation-Title");
            this.scmTimestamp = this.getAttributeValueOrDefault(attributes, "SCM-Timestamp");
            if (this.isEmpty(this.implementationVersion) && !this.isEmpty(this.bundleVersion)) {
                this.implementationVersion = this.bundleVersion;
            }
        }
        
        private boolean isEmpty(final String value) {
            return value == null || value.length() == 0;
        }
        
        private void initializeDerivativeProperties() {
            this.fullVersionString = MessageFormat.format("{0}", this.implementationVersion);
        }
        
        private Manifest readManifest() {
            final ProtectionDomain domain = VersionBean.class.getProtectionDomain();
            if (domain != null) {
                final CodeSource codeSource = domain.getCodeSource();
                if (codeSource != null) {
                    final URL url = codeSource.getLocation();
                    if (url != null) {
                        InputStream manifestStream = null;
                        try {
                            URL manifestFileUrl;
                            if ("vfs".equals(url.getProtocol())) {
                                final String manifestFile = String.format("%s/%s", url.toExternalForm(), "META-INF/MANIFEST.MF");
                                manifestFileUrl = new URL(manifestFile);
                            }
                            else {
                                manifestFileUrl = new URL(url, "META-INF/MANIFEST.MF");
                            }
                            manifestStream = urlToStream(manifestFileUrl);
                            return new Manifest(manifestStream);
                        }
                        catch (final MalformedURLException ex) {}
                        catch (final IOException ex2) {}
                        finally {
                            if (manifestStream != null) {
                                try {
                                    manifestStream.close();
                                }
                                catch (final IOException ex3) {}
                            }
                        }
                        JarInputStream jis = null;
                        try {
                            final URLConnection urlConnection = url.openConnection();
                            urlConnection.setUseCaches(false);
                            if (urlConnection instanceof JarURLConnection) {
                                final JarURLConnection jarUrlConnection = (JarURLConnection)urlConnection;
                                return jarUrlConnection.getManifest();
                            }
                            jis = new JarInputStream(urlConnection.getInputStream());
                            return jis.getManifest();
                        }
                        catch (final IOException ex4) {}
                        finally {
                            if (jis != null) {
                                try {
                                    jis.close();
                                }
                                catch (final IOException ex5) {}
                            }
                        }
                    }
                }
            }
            return null;
        }
        
        private static InputStream urlToStream(final URL url) throws IOException {
            if (url != null) {
                final URLConnection connection = url.openConnection();
                try {
                    connection.setUseCaches(false);
                }
                catch (final IllegalArgumentException ex) {}
                return connection.getInputStream();
            }
            return null;
        }
        
        boolean containsDataFromManifest() {
            return this.containsDataFromManifest;
        }
        
        public String getVersion() {
            return this.fullVersionString;
        }
        
        public String getImplementationTitle() {
            return this.implementationTitle;
        }
        
        public String getImplementationVendor() {
            return this.implementationVendor;
        }
        
        public String getImplementationVersion() {
            return this.implementationVersion;
        }
        
        public String getScmTimestamp() {
            return this.scmTimestamp;
        }
        
        @Override
        public String toString() {
            if (this.containsDataFromManifest()) {
                return this.getImplementationTitle() + " by " + this.getImplementationVendor() + ", version " + this.getVersion();
            }
            return this.getVersion();
        }
    }
}
