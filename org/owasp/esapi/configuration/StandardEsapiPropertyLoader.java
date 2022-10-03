package org.owasp.esapi.configuration;

import java.io.InputStream;
import java.io.FileInputStream;
import java.io.File;
import java.io.IOException;
import org.owasp.esapi.ESAPI;
import org.owasp.esapi.errors.ConfigurationException;

public class StandardEsapiPropertyLoader extends AbstractPrioritizedPropertyLoader
{
    public StandardEsapiPropertyLoader(final String filename, final int priority) {
        super(filename, priority);
    }
    
    @Override
    public int getIntProp(final String propertyName) throws ConfigurationException {
        final String property = this.properties.getProperty(propertyName);
        if (property == null) {
            throw new ConfigurationException("Property : " + propertyName + "not found in configuration");
        }
        try {
            return Integer.parseInt(property);
        }
        catch (final NumberFormatException e) {
            throw new ConfigurationException("Incorrect type of : " + propertyName + ". Value " + property + "cannot be converted to integer", e);
        }
    }
    
    @Override
    public byte[] getByteArrayProp(final String propertyName) throws ConfigurationException {
        final String property = this.properties.getProperty(propertyName);
        if (property == null) {
            throw new ConfigurationException("Property : " + propertyName + "not found in default configuration");
        }
        try {
            return ESAPI.encoder().decodeFromBase64(property);
        }
        catch (final IOException e) {
            throw new ConfigurationException("Incorrect type of : " + propertyName + ". Value " + property + "cannot be converted to byte array", e);
        }
    }
    
    @Override
    public Boolean getBooleanProp(final String propertyName) throws ConfigurationException {
        final String property = this.properties.getProperty(propertyName);
        if (property == null) {
            throw new ConfigurationException("Property : " + propertyName + "not found in default configuration");
        }
        if (property.equalsIgnoreCase("true") || property.equalsIgnoreCase("yes")) {
            return true;
        }
        if (property.equalsIgnoreCase("false") || property.equalsIgnoreCase("no")) {
            return false;
        }
        throw new ConfigurationException("Incorrect type of : " + propertyName + ". Value " + property + "cannot be converted to boolean");
    }
    
    @Override
    public String getStringProp(final String propertyName) throws ConfigurationException {
        final String property = this.properties.getProperty(propertyName);
        if (property == null) {
            throw new ConfigurationException("Property : " + propertyName + "not found in default configuration");
        }
        return property;
    }
    
    @Override
    protected void loadPropertiesFromFile(final File file) {
        InputStream input = null;
        try {
            input = new FileInputStream(file);
            this.properties.load(input);
        }
        catch (final IOException ex) {
            System.err.println("Loading " + file.getName() + " via file I/O failed. Exception was: " + ex);
            if (input != null) {
                try {
                    input.close();
                }
                catch (final IOException e) {
                    System.err.println("Could not close stream");
                }
            }
        }
        finally {
            if (input != null) {
                try {
                    input.close();
                }
                catch (final IOException e2) {
                    System.err.println("Could not close stream");
                }
            }
        }
    }
}
