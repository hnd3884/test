package com.adventnet.iam.security;

import org.json.JSONObject;
import java.io.IOException;
import java.util.logging.Level;
import com.zoho.security.util.HashUtil;
import com.zoho.security.util.CommonUtil;
import java.io.File;
import java.util.logging.Logger;

public class SecurityXML
{
    private static final Logger LOGGER;
    private static final String PATH = "PATH";
    private static final String HASH = "HASH";
    private String fileName;
    private String path;
    private File file;
    private String hash;
    
    public SecurityXML(final String fileName, final File file) {
        this.fileName = null;
        this.path = null;
        this.file = null;
        this.hash = null;
        this.fileName = fileName;
        this.file = file;
        String fileContent = null;
        try {
            this.path = file.getCanonicalPath();
            fileContent = CommonUtil.convertFileToString(file);
            this.hash = HashUtil.SHA512(fileContent);
        }
        catch (final IOException e) {
            SecurityXML.LOGGER.log(Level.WARNING, "Exception while calculating hash value for File : {0}, Exception : {1}", new Object[] { fileName, e.getMessage() });
        }
    }
    
    public String getFileName() {
        return this.fileName;
    }
    
    public String getHash() {
        return this.hash;
    }
    
    public File getFile() {
        return this.file;
    }
    
    public JSONObject toJSONObject() {
        final JSONObject object = new JSONObject();
        object.put("PATH", (Object)this.path);
        object.put("HASH", (Object)this.hash);
        return object;
    }
    
    static {
        LOGGER = Logger.getLogger(SecurityXML.class.getName());
    }
}
