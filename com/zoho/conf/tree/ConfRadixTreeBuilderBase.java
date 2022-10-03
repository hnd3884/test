package com.zoho.conf.tree;

import java.util.Hashtable;
import java.util.Iterator;
import java.net.URL;
import java.io.IOException;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Properties;
import java.io.File;

class ConfRadixTreeBuilderBase<GeneratorT extends ConfRadixTreeBuilderBase<GeneratorT>>
{
    private ConfTree instance;
    
    protected ConfRadixTreeBuilderBase(final ConfTree aInstance) {
        this.instance = aInstance;
    }
    
    protected ConfTree getInstance() {
        return this.instance;
    }
    
    public GeneratorT fromConfFile(final String filePath) throws IOException {
        final File confFile = new File(filePath);
        if (!confFile.exists()) {
            throw new IllegalArgumentException("File not found." + filePath);
        }
        final Properties properties = new Properties();
        FileInputStream fis = null;
        try {
            fis = new FileInputStream(confFile);
            properties.load(fis);
        }
        finally {
            if (fis != null) {
                fis.close();
            }
        }
        this.withConfigurations(properties);
        return (GeneratorT)this;
    }
    
    public GeneratorT fromConfFile(final String... filePaths) throws IOException {
        for (final String filePath : filePaths) {
            this.fromConfFile(filePath);
        }
        return (GeneratorT)this;
    }
    
    public GeneratorT fromConfFile(final URL filePath) throws IOException {
        final Properties properties = new Properties();
        InputStream is = null;
        try {
            is = filePath.openStream();
            properties.load(is);
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
        this.withConfigurations(properties);
        return (GeneratorT)this;
    }
    
    public GeneratorT fromConfFile(final URL[] filePaths) throws IOException {
        for (final URL filePath : filePaths) {
            this.fromConfFile(filePath);
        }
        return (GeneratorT)this;
    }
    
    public GeneratorT withConfigurations(final Properties properties) {
        for (final Object key : ((Hashtable<Object, V>)properties).keySet()) {
            this.getInstance().put((String)key, properties.getProperty((String)key));
        }
        return (GeneratorT)this;
    }
}
