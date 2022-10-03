package org.owasp.esapi.configuration;

import java.io.File;
import java.util.Properties;

public abstract class AbstractPrioritizedPropertyLoader implements EsapiPropertyLoader, Comparable<AbstractPrioritizedPropertyLoader>
{
    protected final String filename;
    protected Properties properties;
    private final int priority;
    
    public AbstractPrioritizedPropertyLoader(final String filename, final int priority) {
        this.priority = priority;
        this.filename = filename;
        this.initProperties();
    }
    
    public int priority() {
        return this.priority;
    }
    
    @Override
    public int compareTo(final AbstractPrioritizedPropertyLoader compared) {
        if (this.priority > compared.priority()) {
            return 1;
        }
        if (this.priority < compared.priority()) {
            return -1;
        }
        return 0;
    }
    
    public String name() {
        return this.filename;
    }
    
    protected void initProperties() {
        this.properties = new Properties();
        final File file = new File(this.filename);
        if (file.exists() && file.isFile()) {
            this.loadPropertiesFromFile(file);
        }
        else {
            System.err.println("Configuration file " + this.filename + " does not exist");
        }
    }
    
    protected abstract void loadPropertiesFromFile(final File p0);
}
