package org.jfree.base.modules;

import java.util.ArrayList;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.IOException;
import java.io.InputStream;
import org.jfree.util.ObjectUtilities;

public abstract class AbstractModule extends DefaultModuleInfo implements Module
{
    private ModuleInfo[] requiredModules;
    private ModuleInfo[] optionalModules;
    private String name;
    private String description;
    private String producer;
    private String subsystem;
    
    public AbstractModule() {
        this.setModuleClass(this.getClass().getName());
    }
    
    public void configure(final SubSystem subSystem) {
        final InputStream in = ObjectUtilities.getResourceRelativeAsStream("configuration.properties", this.getClass());
        if (in == null) {
            return;
        }
        subSystem.getPackageManager().getPackageConfiguration().load(in);
    }
    
    public String getDescription() {
        return this.description;
    }
    
    public String getName() {
        return this.name;
    }
    
    public ModuleInfo[] getOptionalModules() {
        final ModuleInfo[] retval = new ModuleInfo[this.optionalModules.length];
        System.arraycopy(this.optionalModules, 0, retval, 0, this.optionalModules.length);
        return retval;
    }
    
    public String getProducer() {
        return this.producer;
    }
    
    public ModuleInfo[] getRequiredModules() {
        final ModuleInfo[] retval = new ModuleInfo[this.requiredModules.length];
        System.arraycopy(this.requiredModules, 0, retval, 0, this.requiredModules.length);
        return retval;
    }
    
    public String getSubSystem() {
        if (this.subsystem == null) {
            return this.getName();
        }
        return this.subsystem;
    }
    
    public abstract void initialize(final SubSystem p0) throws ModuleInitializeException;
    
    protected static boolean isClassLoadable(final String name) {
        try {
            Thread.currentThread().getContextClassLoader().loadClass(name);
            return true;
        }
        catch (final Exception ex) {
            return false;
        }
    }
    
    private boolean isNextLineValueLine(final ReaderHelper reader) throws IOException {
        if (!reader.hasNext()) {
            return false;
        }
        final String firstLine = reader.next();
        if (firstLine == null) {
            return false;
        }
        if (this.parseKey(firstLine) != null) {
            reader.pushBack(firstLine);
            return false;
        }
        reader.pushBack(firstLine);
        return true;
    }
    
    protected void loadModuleInfo() throws ModuleInitializeException {
        final InputStream in = ObjectUtilities.getResourceRelativeAsStream("module.properties", this.getClass());
        if (in == null) {
            throw new ModuleInitializeException("File 'module.properties' not found in module package.");
        }
        this.loadModuleInfo(in);
    }
    
    protected void loadModuleInfo(final InputStream in) throws ModuleInitializeException {
        try {
            if (in == null) {
                throw new NullPointerException("Given InputStream is null.");
            }
            final ReaderHelper rh = new ReaderHelper(new BufferedReader(new InputStreamReader(in, "ISO-8859-1")));
            final ArrayList optionalModules = new ArrayList();
            final ArrayList dependendModules = new ArrayList();
            while (rh.hasNext()) {
                final String lastLineRead = rh.next();
                if (lastLineRead.startsWith("module-info:")) {
                    this.readModuleInfo(rh);
                }
                else if (lastLineRead.startsWith("depends:")) {
                    dependendModules.add(this.readExternalModule(rh));
                }
                else {
                    if (!lastLineRead.startsWith("optional:")) {
                        continue;
                    }
                    optionalModules.add(this.readExternalModule(rh));
                }
            }
            rh.close();
            this.optionalModules = optionalModules.toArray(new ModuleInfo[optionalModules.size()]);
            this.requiredModules = dependendModules.toArray(new ModuleInfo[dependendModules.size()]);
        }
        catch (final IOException e) {
            throw new ModuleInitializeException("Failed to load properties", e);
        }
    }
    
    private String parseKey(final String line) {
        final int idx = line.indexOf(58);
        if (idx == -1) {
            return null;
        }
        return line.substring(0, idx);
    }
    
    private String parseValue(final String line) {
        final int idx = line.indexOf(58);
        if (idx == -1) {
            return line;
        }
        if (idx + 1 == line.length()) {
            return "";
        }
        return line.substring(idx + 1);
    }
    
    protected void performExternalInitialize(final String classname) throws ModuleInitializeException {
        try {
            final Class c = Thread.currentThread().getContextClassLoader().loadClass(classname);
            final ModuleInitializer mi = c.newInstance();
            mi.performInit();
        }
        catch (final ModuleInitializeException mie) {
            throw mie;
        }
        catch (final Exception e) {
            throw new ModuleInitializeException("Failed to load specified initializer class.", e);
        }
    }
    
    private DefaultModuleInfo readExternalModule(final ReaderHelper reader) throws IOException {
        final DefaultModuleInfo mi = new DefaultModuleInfo();
        while (reader.hasNext()) {
            final String lastLineRead = reader.next();
            if (!Character.isWhitespace(lastLineRead.charAt(0))) {
                reader.pushBack(lastLineRead);
                return mi;
            }
            final String line = lastLineRead.trim();
            final String key = this.parseKey(line);
            if (key == null) {
                continue;
            }
            final String b = this.readValue(reader, this.parseValue(line));
            if (key.equals("module")) {
                mi.setModuleClass(b);
            }
            else if (key.equals("version.major")) {
                mi.setMajorVersion(b);
            }
            else if (key.equals("version.minor")) {
                mi.setMinorVersion(b);
            }
            else {
                if (!key.equals("version.patchlevel")) {
                    continue;
                }
                mi.setPatchLevel(b);
            }
        }
        return mi;
    }
    
    private void readModuleInfo(final ReaderHelper reader) throws IOException {
        while (reader.hasNext()) {
            final String lastLineRead = reader.next();
            if (!Character.isWhitespace(lastLineRead.charAt(0))) {
                reader.pushBack(lastLineRead);
                return;
            }
            final String line = lastLineRead.trim();
            final String key = this.parseKey(line);
            if (key == null) {
                continue;
            }
            final String b = this.readValue(reader, this.parseValue(line.trim()));
            if (key.equals("name")) {
                this.setName(b);
            }
            else if (key.equals("producer")) {
                this.setProducer(b);
            }
            else if (key.equals("description")) {
                this.setDescription(b);
            }
            else if (key.equals("subsystem")) {
                this.setSubSystem(b);
            }
            else if (key.equals("version.major")) {
                this.setMajorVersion(b);
            }
            else if (key.equals("version.minor")) {
                this.setMinorVersion(b);
            }
            else {
                if (!key.equals("version.patchlevel")) {
                    continue;
                }
                this.setPatchLevel(b);
            }
        }
    }
    
    private String readValue(final ReaderHelper reader, String firstLine) throws IOException {
        final StringBuffer b = new StringBuffer(firstLine.trim());
        boolean newLine = true;
        while (this.isNextLineValueLine(reader)) {
            firstLine = reader.next();
            final String trimedLine = firstLine.trim();
            if (trimedLine.length() == 0 && !newLine) {
                b.append("\n");
                newLine = true;
            }
            else {
                if (!newLine) {
                    b.append(" ");
                }
                b.append(this.parseValue(trimedLine));
                newLine = false;
            }
        }
        return b.toString();
    }
    
    protected void setDescription(final String description) {
        this.description = description;
    }
    
    protected void setName(final String name) {
        this.name = name;
    }
    
    public void setOptionalModules(final ModuleInfo[] optionalModules) {
        System.arraycopy(optionalModules, 0, this.optionalModules = new ModuleInfo[optionalModules.length], 0, optionalModules.length);
    }
    
    protected void setProducer(final String producer) {
        this.producer = producer;
    }
    
    protected void setRequiredModules(final ModuleInfo[] requiredModules) {
        System.arraycopy(requiredModules, 0, this.requiredModules = new ModuleInfo[requiredModules.length], 0, requiredModules.length);
    }
    
    protected void setSubSystem(final String name) {
        this.subsystem = name;
    }
    
    public String toString() {
        final StringBuffer buffer = new StringBuffer();
        buffer.append("Module : ");
        buffer.append(this.getName());
        buffer.append("\n");
        buffer.append("ModuleClass : ");
        buffer.append(this.getModuleClass());
        buffer.append("\n");
        buffer.append("Version: ");
        buffer.append(this.getMajorVersion());
        buffer.append(".");
        buffer.append(this.getMinorVersion());
        buffer.append(".");
        buffer.append(this.getPatchLevel());
        buffer.append("\n");
        buffer.append("Producer: ");
        buffer.append(this.getProducer());
        buffer.append("\n");
        buffer.append("Description: ");
        buffer.append(this.getDescription());
        buffer.append("\n");
        return buffer.toString();
    }
    
    private static class ReaderHelper
    {
        private String buffer;
        private final BufferedReader reader;
        
        public ReaderHelper(final BufferedReader reader) {
            this.reader = reader;
        }
        
        public void close() throws IOException {
            this.reader.close();
        }
        
        public boolean hasNext() throws IOException {
            if (this.buffer == null) {
                this.buffer = this.readLine();
            }
            return this.buffer != null;
        }
        
        public String next() {
            final String line = this.buffer;
            this.buffer = null;
            return line;
        }
        
        public void pushBack(final String line) {
            this.buffer = line;
        }
        
        protected String readLine() throws IOException {
            String line;
            for (line = this.reader.readLine(); line != null && (line.length() == 0 || line.startsWith("#")); line = this.reader.readLine()) {}
            return line;
        }
    }
}
