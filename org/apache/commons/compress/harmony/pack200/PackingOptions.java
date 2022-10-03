package org.apache.commons.compress.harmony.pack200;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Iterator;
import org.objectweb.asm.Attribute;
import java.util.Map;
import java.util.List;

public class PackingOptions
{
    public static final String STRIP = "strip";
    public static final String ERROR = "error";
    public static final String PASS = "pass";
    public static final String KEEP = "keep";
    private boolean gzip;
    private boolean stripDebug;
    private boolean keepFileOrder;
    private long segmentLimit;
    private int effort;
    private String deflateHint;
    private String modificationTime;
    private List passFiles;
    private String unknownAttributeAction;
    private Map classAttributeActions;
    private Map fieldAttributeActions;
    private Map methodAttributeActions;
    private Map codeAttributeActions;
    private boolean verbose;
    private String logFile;
    private Attribute[] unknownAttributeTypes;
    
    public PackingOptions() {
        this.gzip = true;
        this.stripDebug = false;
        this.keepFileOrder = true;
        this.segmentLimit = 1000000L;
        this.effort = 5;
        this.deflateHint = "keep";
        this.modificationTime = "keep";
        this.unknownAttributeAction = "pass";
        this.verbose = false;
    }
    
    public boolean isGzip() {
        return this.gzip;
    }
    
    public void setGzip(final boolean gzip) {
        this.gzip = gzip;
    }
    
    public boolean isStripDebug() {
        return this.stripDebug;
    }
    
    public void setStripDebug(final boolean stripDebug) {
        this.stripDebug = stripDebug;
    }
    
    public boolean isKeepFileOrder() {
        return this.keepFileOrder;
    }
    
    public void setKeepFileOrder(final boolean keepFileOrder) {
        this.keepFileOrder = keepFileOrder;
    }
    
    public long getSegmentLimit() {
        return this.segmentLimit;
    }
    
    public void setSegmentLimit(final long segmentLimit) {
        this.segmentLimit = segmentLimit;
    }
    
    public int getEffort() {
        return this.effort;
    }
    
    public void setEffort(final int effort) {
        this.effort = effort;
    }
    
    public String getDeflateHint() {
        return this.deflateHint;
    }
    
    public boolean isKeepDeflateHint() {
        return "keep".equals(this.deflateHint);
    }
    
    public void setDeflateHint(final String deflateHint) {
        if (!"keep".equals(deflateHint) && !"true".equals(deflateHint) && !"false".equals(deflateHint)) {
            throw new IllegalArgumentException("Bad argument: -H " + deflateHint + " ? deflate hint should be either true, false or keep (default)");
        }
        this.deflateHint = deflateHint;
    }
    
    public String getModificationTime() {
        return this.modificationTime;
    }
    
    public void setModificationTime(final String modificationTime) {
        if (!"keep".equals(modificationTime) && !"latest".equals(modificationTime)) {
            throw new IllegalArgumentException("Bad argument: -m " + modificationTime + " ? transmit modtimes should be either latest or keep (default)");
        }
        this.modificationTime = modificationTime;
    }
    
    public boolean isPassFile(final String passFileName) {
        if (this.passFiles != null) {
            for (String pass : this.passFiles) {
                if (passFileName.equals(pass)) {
                    return true;
                }
                if (!pass.endsWith(".class")) {
                    if (!pass.endsWith("/")) {
                        pass += "/";
                    }
                    return passFileName.startsWith(pass);
                }
            }
        }
        return false;
    }
    
    public void addPassFile(String passFileName) {
        if (this.passFiles == null) {
            this.passFiles = new ArrayList();
        }
        String fileSeparator = System.getProperty("file.separator");
        if (fileSeparator.equals("\\")) {
            fileSeparator += "\\";
        }
        passFileName = passFileName.replaceAll(fileSeparator, "/");
        this.passFiles.add(passFileName);
    }
    
    public void removePassFile(final String passFileName) {
        this.passFiles.remove(passFileName);
    }
    
    public String getUnknownAttributeAction() {
        return this.unknownAttributeAction;
    }
    
    public void setUnknownAttributeAction(final String unknownAttributeAction) {
        this.unknownAttributeAction = unknownAttributeAction;
        if (!"pass".equals(unknownAttributeAction) && !"error".equals(unknownAttributeAction) && !"strip".equals(unknownAttributeAction)) {
            throw new RuntimeException("Incorrect option for -U, " + unknownAttributeAction);
        }
    }
    
    public void addClassAttributeAction(final String attributeName, final String action) {
        if (this.classAttributeActions == null) {
            this.classAttributeActions = new HashMap();
        }
        this.classAttributeActions.put(attributeName, action);
    }
    
    public void addFieldAttributeAction(final String attributeName, final String action) {
        if (this.fieldAttributeActions == null) {
            this.fieldAttributeActions = new HashMap();
        }
        this.fieldAttributeActions.put(attributeName, action);
    }
    
    public void addMethodAttributeAction(final String attributeName, final String action) {
        if (this.methodAttributeActions == null) {
            this.methodAttributeActions = new HashMap();
        }
        this.methodAttributeActions.put(attributeName, action);
    }
    
    public void addCodeAttributeAction(final String attributeName, final String action) {
        if (this.codeAttributeActions == null) {
            this.codeAttributeActions = new HashMap();
        }
        this.codeAttributeActions.put(attributeName, action);
    }
    
    public boolean isVerbose() {
        return this.verbose;
    }
    
    public void setVerbose(final boolean verbose) {
        this.verbose = verbose;
    }
    
    public void setQuiet(final boolean quiet) {
        this.verbose = !quiet;
    }
    
    public String getLogFile() {
        return this.logFile;
    }
    
    public void setLogFile(final String logFile) {
        this.logFile = logFile;
    }
    
    private void addOrUpdateAttributeActions(final List prototypes, final Map attributeActions, final int tag) {
        if (attributeActions != null && attributeActions.size() > 0) {
            for (final String name : attributeActions.keySet()) {
                final String action = attributeActions.get(name);
                boolean prototypeExists = false;
                for (final NewAttribute newAttribute : prototypes) {
                    if (newAttribute.type.equals(name)) {
                        newAttribute.addContext(tag);
                        prototypeExists = true;
                        break;
                    }
                }
                if (!prototypeExists) {
                    NewAttribute newAttribute;
                    if ("error".equals(action)) {
                        newAttribute = new NewAttribute.ErrorAttribute(name, tag);
                    }
                    else if ("strip".equals(action)) {
                        newAttribute = new NewAttribute.StripAttribute(name, tag);
                    }
                    else if ("pass".equals(action)) {
                        newAttribute = new NewAttribute.PassAttribute(name, tag);
                    }
                    else {
                        newAttribute = new NewAttribute(name, action, tag);
                    }
                    prototypes.add(newAttribute);
                }
            }
        }
    }
    
    public Attribute[] getUnknownAttributePrototypes() {
        if (this.unknownAttributeTypes == null) {
            final List prototypes = new ArrayList();
            this.addOrUpdateAttributeActions(prototypes, this.classAttributeActions, 0);
            this.addOrUpdateAttributeActions(prototypes, this.methodAttributeActions, 2);
            this.addOrUpdateAttributeActions(prototypes, this.fieldAttributeActions, 1);
            this.addOrUpdateAttributeActions(prototypes, this.codeAttributeActions, 3);
            this.unknownAttributeTypes = prototypes.toArray(new Attribute[0]);
        }
        return this.unknownAttributeTypes;
    }
    
    public String getUnknownClassAttributeAction(final String type) {
        if (this.classAttributeActions == null) {
            return this.unknownAttributeAction;
        }
        String action = this.classAttributeActions.get(type);
        if (action == null) {
            action = this.unknownAttributeAction;
        }
        return action;
    }
    
    public String getUnknownMethodAttributeAction(final String type) {
        if (this.methodAttributeActions == null) {
            return this.unknownAttributeAction;
        }
        String action = this.methodAttributeActions.get(type);
        if (action == null) {
            action = this.unknownAttributeAction;
        }
        return action;
    }
    
    public String getUnknownFieldAttributeAction(final String type) {
        if (this.fieldAttributeActions == null) {
            return this.unknownAttributeAction;
        }
        String action = this.fieldAttributeActions.get(type);
        if (action == null) {
            action = this.unknownAttributeAction;
        }
        return action;
    }
    
    public String getUnknownCodeAttributeAction(final String type) {
        if (this.codeAttributeActions == null) {
            return this.unknownAttributeAction;
        }
        String action = this.codeAttributeActions.get(type);
        if (action == null) {
            action = this.unknownAttributeAction;
        }
        return action;
    }
}
