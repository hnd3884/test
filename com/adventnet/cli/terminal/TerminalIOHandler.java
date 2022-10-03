package com.adventnet.cli.terminal;

import java.util.Hashtable;
import java.net.URL;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;

public class TerminalIOHandler
{
    String terminalHandlerFileName;
    String transformationClassName;
    String translationClassName;
    TransformationHandler trfHandler;
    TranslationHandler traHandler;
    Properties terminalProperties;
    
    public void setTransformationHandlerClassName(final String transformationClassName) {
        this.transformationClassName = transformationClassName;
    }
    
    public String getTransformationHandlerClassName() {
        return this.transformationClassName;
    }
    
    public void setTranslationHandlerClassName(final String translationClassName) {
        this.translationClassName = translationClassName;
    }
    
    public String getTranslationHandlerClassName() {
        return this.translationClassName;
    }
    
    public TerminalIOHandler() throws TerminalException {
        this.terminalHandlerFileName = "/terminal.conf";
        this.transformationClassName = null;
        this.translationClassName = null;
        this.trfHandler = null;
        this.traHandler = null;
        this.terminalProperties = null;
        try {
            this.initTerminalHandlers();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
            throw new TerminalException(ex.getMessage());
        }
        this.terminalProperties = new Properties();
    }
    
    public byte[] transform(final byte[] array) throws TerminalException {
        if (this.trfHandler != null) {
            return this.trfHandler.transform(array);
        }
        return array;
    }
    
    public byte translate(final byte b) throws TerminalException {
        if (this.traHandler != null) {
            return this.traHandler.translate(b);
        }
        return b;
    }
    
    public byte inverseTranslate(final byte b) throws TerminalException {
        if (this.traHandler != null) {
            return this.traHandler.inverseTranslate(b);
        }
        return b;
    }
    
    public void useTranslationTable(final String s) throws TerminalException {
        this.traHandler.useTranslationTable(s);
    }
    
    public void useTransformationTable(final String s) throws TerminalException {
        this.trfHandler.useTransformationTable(s);
    }
    
    public void init(final String s, final String s2) throws Exception {
        if (this.translationClassName != null && s != null) {
            (this.traHandler = (TranslationHandler)Class.forName(this.translationClassName).newInstance()).readTranslationTables(s);
        }
        if (this.transformationClassName != null && s2 != null) {
            (this.trfHandler = (TransformationHandler)Class.forName(this.transformationClassName).newInstance()).readTransformationTables(s2);
        }
    }
    
    void initTerminalHandlers() throws Exception {
        final URL resource = this.getClass().getResource(this.terminalHandlerFileName);
        if (resource == null) {
            throw new Exception("URL Invalid");
        }
        try {
            final BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(resource.openStream()));
            while (true) {
                String line;
                try {
                    line = bufferedReader.readLine();
                }
                catch (final Exception ex) {
                    throw new Exception("Cannot read file");
                }
                if (line == null) {
                    break;
                }
                if (line.trim().startsWith(" ") || line.trim().equals("")) {
                    continue;
                }
                if (line.trim().startsWith("#")) {
                    continue;
                }
                final StringTokenizer stringTokenizer = new StringTokenizer(line);
                if (!stringTokenizer.hasMoreTokens()) {
                    break;
                }
                final String trim = stringTokenizer.nextToken().trim();
                if (trim.toLowerCase().equals("translation")) {
                    if (this.translationClassName != null) {
                        continue;
                    }
                    this.translationClassName = stringTokenizer.nextToken().trim();
                }
                else {
                    if (!trim.toLowerCase().equals("transformation") || this.transformationClassName != null) {
                        continue;
                    }
                    this.transformationClassName = stringTokenizer.nextToken().trim();
                }
            }
        }
        catch (final Exception ex2) {
            throw new Exception("Invalid File Name ");
        }
    }
    
    void setTerminalProperty(final String s, final String s2) {
        ((Hashtable<String, String>)this.terminalProperties).put(s, s2);
    }
    
    String getTerminalProperty(final String s) {
        return this.terminalProperties.getProperty(s);
    }
    
    public String[] getTransformationTableNames() throws TerminalException {
        String[] transformationTableNames = null;
        if (this.trfHandler != null) {
            transformationTableNames = this.trfHandler.getTransformationTableNames();
        }
        return transformationTableNames;
    }
    
    public String[] getTranslationTableNames() throws TerminalException {
        String[] translationTableNames = null;
        if (this.traHandler != null) {
            translationTableNames = this.traHandler.getTranslationTableNames();
        }
        return translationTableNames;
    }
}
