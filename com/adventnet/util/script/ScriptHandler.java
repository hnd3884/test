package com.adventnet.util.script;

import java.net.URL;
import java.util.StringTokenizer;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.Properties;
import java.util.Hashtable;

public class ScriptHandler
{
    private static Hashtable scriptRunnerTable;
    private String scriptHandlerFileName;
    private RunScriptInterface scriptRunner;
    private String scriptType;
    private static String firstScriptType;
    
    public RunScriptInterface getRunScriptIfcImpl() {
        return this.scriptRunner;
    }
    
    public void setRunScriptIfcImpl(final RunScriptInterface scriptRunner) {
        this.scriptRunner = scriptRunner;
    }
    
    public void setRunScriptIfcImpl(final String s) throws Exception {
        if (s != null) {
            this.scriptRunner = ScriptHandler.scriptRunnerTable.get(s);
            if (this.scriptRunner == null) {
                throw new Exception("no script runner for " + s);
            }
        }
    }
    
    public void addRunScriptIfcImplClassName(final String s, final String s2) throws Exception {
        if (s2 != null) {
            final RunScriptInterface runScriptIfc = this.getRunScriptIfc(s2);
            runScriptIfc.init(null);
            if (s != null) {
                ScriptHandler.scriptRunnerTable.put(s, runScriptIfc);
            }
        }
    }
    
    private String getConfFileName() {
        return this.scriptHandlerFileName;
    }
    
    public String getScriptType() {
        return this.scriptType;
    }
    
    public void setScriptType(final String scriptType) {
        this.scriptType = scriptType;
    }
    
    public ScriptHandler() throws Exception {
        this.scriptHandlerFileName = null;
        this.scriptRunner = null;
        this.scriptType = null;
        this.initScriptHandler(null);
    }
    
    public ScriptHandler(final Properties[] array) throws Exception {
        this.scriptHandlerFileName = null;
        this.scriptRunner = null;
        this.scriptType = null;
        this.initScriptHandler(array);
    }
    
    public void executeScriptFromFile(final String s) throws ScriptHandlerException {
        this.executeScriptFromFile(s, (String)null);
    }
    
    public void executeScriptFromFile(final String s, final String s2) throws ScriptHandlerException {
        try {
            if (s2 != null) {
                this.setScriptType(s2);
                this.setRunScriptIfcImpl(s2);
            }
            this.scriptRunner.executeScript(s);
        }
        catch (final Exception ex) {
            throw new ScriptHandlerException(ex.getMessage() + ex);
        }
    }
    
    public void executeScriptFromFile(final String s, final String[] array) throws ScriptHandlerException {
        this.executeScriptFromFile(s, array, null);
    }
    
    public void executeScriptFromFile(final String s, final String[] array, final String s2) throws ScriptHandlerException {
        try {
            if (s2 != null) {
                this.setScriptType(s2);
                this.setRunScriptIfcImpl(s2);
            }
            this.scriptRunner.executeScript(s, array);
        }
        catch (final Exception ex) {
            throw new ScriptHandlerException(ex.getMessage() + ex);
        }
    }
    
    public void executeLine(final String s) throws ScriptHandlerException {
        this.scriptRunner.executeLine(s);
    }
    
    private void initScriptHandler(final Properties[] array) throws Exception {
        if (ScriptHandler.scriptRunnerTable == null) {
            ScriptHandler.scriptRunnerTable = new Hashtable();
            this.getScriptTableInfo(this.scriptHandlerFileName = "/scripthandler.conf", array);
        }
        this.setScriptType(ScriptHandler.firstScriptType);
        this.setRunScriptIfcImpl(this.scriptType);
    }
    
    private void getScriptTableInfo(final String s, final Properties[] array) throws Exception {
        System.out.println(" file name " + s);
        final URL resource = this.getClass().getResource(s);
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
                final String trim = stringTokenizer.nextToken().trim();
                final RunScriptInterface runScriptIfc = this.getRunScriptIfc(stringTokenizer.nextToken().trim());
                if (ScriptHandler.firstScriptType == null) {
                    ScriptHandler.firstScriptType = trim;
                }
                final int size = ScriptHandler.scriptRunnerTable.size();
                if (array != null && array.length > size) {
                    runScriptIfc.init(array[size]);
                }
                else {
                    runScriptIfc.init(null);
                }
                ScriptHandler.scriptRunnerTable.put(trim, runScriptIfc);
            }
        }
        catch (final Exception ex2) {
            throw new Exception("Invalid File Name " + ex2);
        }
    }
    
    private RunScriptInterface getRunScriptIfc(final String s) throws Exception {
        try {
            final Class<?> forName = Class.forName(s.trim());
            if (forName == null) {
                System.err.println("No such class found: " + s);
                throw new Exception("No class " + s + " found");
            }
            final RunScriptInterface runScriptInterface = (RunScriptInterface)forName.newInstance();
            if (runScriptInterface == null) {
                System.err.println("Cannot instantiate class: " + s);
                throw new Exception("cannot instantiate " + s);
            }
            return runScriptInterface;
        }
        catch (final Exception ex) {
            System.err.println(" Exception instantiating scriptRunner: " + s + ": " + ex);
            throw new Exception(ex.getMessage());
        }
        catch (final Error error) {
            System.err.println(" Error instantiating scriptRunner: " + s + ": " + error);
            throw new Error(error.getMessage());
        }
    }
    
    public void loadScriptProperties(final String s, final Properties properties) throws Exception {
        if (s != null) {
            final RunScriptInterface runScriptInterface = ScriptHandler.scriptRunnerTable.get(s);
            if (runScriptInterface == null) {
                throw new Exception("no script runner for " + s);
            }
            runScriptInterface.init(properties);
        }
    }
    
    static {
        ScriptHandler.scriptRunnerTable = null;
        ScriptHandler.firstScriptType = null;
    }
}
