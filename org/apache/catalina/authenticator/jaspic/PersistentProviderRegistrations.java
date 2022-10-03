package org.apache.catalina.authenticator.jaspic;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Iterator;
import java.io.Writer;
import java.io.OutputStream;
import org.apache.juli.logging.LogFactory;
import java.util.Map;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.io.FileOutputStream;
import java.io.InputStream;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import org.xml.sax.SAXException;
import org.apache.tomcat.util.digester.Digester;
import java.io.FileInputStream;
import java.io.File;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

final class PersistentProviderRegistrations
{
    private static final Log log;
    private static final StringManager sm;
    
    private PersistentProviderRegistrations() {
    }
    
    static Providers loadProviders(final File configFile) {
        try (final InputStream is = new FileInputStream(configFile)) {
            final Digester digester = new Digester();
            try {
                digester.setFeature("http://apache.org/xml/features/allow-java-encodings", true);
            }
            catch (final SAXException se) {
                PersistentProviderRegistrations.log.warn((Object)PersistentProviderRegistrations.sm.getString("persistentProviderRegistrations.xmlFeatureEncoding"), (Throwable)se);
            }
            digester.setValidating(true);
            digester.setNamespaceAware(true);
            final Providers result = new Providers();
            digester.push((Object)result);
            digester.addObjectCreate("jaspic-providers/provider", Provider.class.getName());
            digester.addSetProperties("jaspic-providers/provider");
            digester.addSetNext("jaspic-providers/provider", "addProvider", Provider.class.getName());
            digester.addObjectCreate("jaspic-providers/provider/property", Property.class.getName());
            digester.addSetProperties("jaspic-providers/provider/property");
            digester.addSetNext("jaspic-providers/provider/property", "addProperty", Property.class.getName());
            digester.parse(is);
            return result;
        }
        catch (final IOException | ParserConfigurationException | SAXException e) {
            throw new SecurityException(e);
        }
    }
    
    static void writeProviders(final Providers providers, final File configFile) {
        final File configFileOld = new File(configFile.getAbsolutePath() + ".old");
        final File configFileNew = new File(configFile.getAbsolutePath() + ".new");
        if (configFileOld.exists() && configFileOld.delete()) {
            throw new SecurityException(PersistentProviderRegistrations.sm.getString("persistentProviderRegistrations.existsDeleteFail", new Object[] { configFileOld.getAbsolutePath() }));
        }
        if (configFileNew.exists() && configFileNew.delete()) {
            throw new SecurityException(PersistentProviderRegistrations.sm.getString("persistentProviderRegistrations.existsDeleteFail", new Object[] { configFileNew.getAbsolutePath() }));
        }
        try (final OutputStream fos = new FileOutputStream(configFileNew);
             final Writer writer = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            writer.write("<?xml version='1.0' encoding='utf-8'?>\n<jaspic-providers\n    xmlns=\"http://tomcat.apache.org/xml\"\n    xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n    xsi:schemaLocation=\"http://tomcat.apache.org/xml jaspic-providers.xsd\"\n    version=\"1.0\">\n");
            for (final Provider provider : providers.providers) {
                writer.write("  <provider");
                writeOptional("className", provider.getClassName(), writer);
                writeOptional("layer", provider.getLayer(), writer);
                writeOptional("appContext", provider.getAppContext(), writer);
                writeOptional("description", provider.getDescription(), writer);
                writer.write(">\n");
                for (final Map.Entry<String, String> entry : provider.getProperties().entrySet()) {
                    writer.write("    <property name=\"");
                    writer.write(entry.getKey());
                    writer.write("\" value=\"");
                    writer.write(entry.getValue());
                    writer.write("\"/>\n");
                }
                writer.write("  </provider>\n");
            }
            writer.write("</jaspic-providers>\n");
        }
        catch (final IOException e) {
            if (!configFileNew.delete()) {
                final Log log = LogFactory.getLog((Class)PersistentProviderRegistrations.class);
                log.warn((Object)PersistentProviderRegistrations.sm.getString("persistentProviderRegistrations.deleteFail", new Object[] { configFileNew.getAbsolutePath() }));
            }
            throw new SecurityException(e);
        }
        if (configFile.isFile() && !configFile.renameTo(configFileOld)) {
            throw new SecurityException(PersistentProviderRegistrations.sm.getString("persistentProviderRegistrations.moveFail", new Object[] { configFile.getAbsolutePath(), configFileOld.getAbsolutePath() }));
        }
        if (!configFileNew.renameTo(configFile)) {
            throw new SecurityException(PersistentProviderRegistrations.sm.getString("persistentProviderRegistrations.moveFail", new Object[] { configFileNew.getAbsolutePath(), configFile.getAbsolutePath() }));
        }
        if (configFileOld.exists() && !configFileOld.delete()) {
            final Log log2 = LogFactory.getLog((Class)PersistentProviderRegistrations.class);
            log2.warn((Object)PersistentProviderRegistrations.sm.getString("persistentProviderRegistrations.deleteFail", new Object[] { configFileOld.getAbsolutePath() }));
        }
    }
    
    private static void writeOptional(final String name, final String value, final Writer writer) throws IOException {
        if (value != null) {
            writer.write(" " + name + "=\"");
            writer.write(value);
            writer.write("\"");
        }
    }
    
    static {
        log = LogFactory.getLog((Class)PersistentProviderRegistrations.class);
        sm = StringManager.getManager((Class)PersistentProviderRegistrations.class);
    }
    
    public static class Providers
    {
        private final List<Provider> providers;
        
        public Providers() {
            this.providers = new ArrayList<Provider>();
        }
        
        public void addProvider(final Provider provider) {
            this.providers.add(provider);
        }
        
        public List<Provider> getProviders() {
            return this.providers;
        }
    }
    
    public static class Provider
    {
        private String className;
        private String layer;
        private String appContext;
        private String description;
        private final Map<String, String> properties;
        
        public Provider() {
            this.properties = new HashMap<String, String>();
        }
        
        public String getClassName() {
            return this.className;
        }
        
        public void setClassName(final String className) {
            this.className = className;
        }
        
        public String getLayer() {
            return this.layer;
        }
        
        public void setLayer(final String layer) {
            this.layer = layer;
        }
        
        public String getAppContext() {
            return this.appContext;
        }
        
        public void setAppContext(final String appContext) {
            this.appContext = appContext;
        }
        
        public String getDescription() {
            return this.description;
        }
        
        public void setDescription(final String description) {
            this.description = description;
        }
        
        public void addProperty(final Property property) {
            this.properties.put(property.getName(), property.getValue());
        }
        
        void addProperty(final String name, final String value) {
            this.properties.put(name, value);
        }
        
        public Map<String, String> getProperties() {
            return this.properties;
        }
    }
    
    public static class Property
    {
        private String name;
        private String value;
        
        public String getName() {
            return this.name;
        }
        
        public void setName(final String name) {
            this.name = name;
        }
        
        public String getValue() {
            return this.value;
        }
        
        public void setValue(final String value) {
            this.value = value;
        }
    }
}
