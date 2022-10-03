package com.zoho.security.appfirewall;

import java.io.IOException;
import com.adventnet.iam.security.UploadedFileItem;
import com.adventnet.iam.security.SecurityFrameworkUtil;
import java.util.logging.Level;
import org.json.JSONObject;
import org.json.JSONException;
import org.json.JSONArray;
import javax.servlet.http.HttpServletRequest;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class FilesDirective extends AppFirewallDirective
{
    private static final Logger LOGGER;
    List<FileDirective> FileDirectiveList;
    
    public FilesDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) {
        super(configuredDirectives, directive);
        this.loadDirective(configuredDirectives, directive);
    }
    
    public void loadDirective(final List<DirectiveConfiguration> configuredDirectives, final DirectiveConfiguration.Directive directive) {
        this.FileDirectiveList = new ArrayList<FileDirective>();
        for (final DirectiveConfiguration configuredDirective : configuredDirectives) {
            this.FileDirectiveList.add(new FileDirective(configuredDirective, directive));
        }
    }
    
    public JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
        JSONArray filesCollectiveJSON = null;
        for (final FileDirective fileDirective : this.FileDirectiveList) {
            final JSONArray fileDirectiveErrorJSON = fileDirective.findBlackListComponent(request);
            if (fileDirectiveErrorJSON == null) {
                return null;
            }
            if (filesCollectiveJSON == null) {
                filesCollectiveJSON = new JSONArray();
            }
            filesCollectiveJSON.put((Object)fileDirectiveErrorJSON);
        }
        return filesCollectiveJSON;
    }
    
    @Override
    public JSONArray toJSON() {
        final JSONArray headersJSON = new JSONArray();
        try {
            for (final FileDirective headerDirective : this.FileDirectiveList) {
                final JSONObject componentJSON = new JSONObject();
                if (this.id != null) {
                    componentJSON.put("id", (Object)this.id);
                }
                for (final AppFirewallComponent component : headerDirective.getComponentList()) {
                    componentJSON.put(component.getComponentName(), (Object)component.toJSON());
                }
                headersJSON.put((Object)componentJSON);
            }
        }
        catch (final JSONException e) {
            FilesDirective.LOGGER.log(Level.SEVERE, "Exception Occurred while generating ComponentJSON :: Exception :: {0}", e.getMessage());
        }
        return headersJSON;
    }
    
    static {
        LOGGER = Logger.getLogger(FileDirective.class.getName());
    }
    
    class FileDirective extends AppFirewallDirective
    {
        public FileDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
            super(configuredDirective, directive);
            this.loadDirective(configuredDirective, directive);
        }
        
        public void loadDirective(final DirectiveConfiguration configuredDirective, final DirectiveConfiguration.Directive directive) {
            this.initDirectiveComponent(configuredDirective, directive);
        }
        
        public JSONArray findBlackListComponent(final HttpServletRequest request) throws JSONException {
            JSONArray array = null;
            final List<UploadedFileItem> files = SecurityFrameworkUtil.getMultiPartFiles(request);
            if (files != null) {
                for (final UploadedFileItem file : files) {
                    final String fileName = file.getFileName();
                    array = null;
                    boolean isBlackListedFile = true;
                    for (final AppFirewallComponent component : this.getComponentList()) {
                        final String componentName = component.getComponentName();
                        String valueFromRequest = null;
                        final String s = componentName;
                        switch (s) {
                            case "name": {
                                valueFromRequest = fileName;
                                isBlackListedFile = (isBlackListedFile && component.isBlackListed(fileName));
                                break;
                            }
                            case "content": {
                                valueFromRequest = this.findBlackListedFileContent(request, file, component);
                                isBlackListedFile = (isBlackListedFile && valueFromRequest != null);
                                break;
                            }
                        }
                        if (!isBlackListedFile) {
                            break;
                        }
                        array = this.getComponentErrorJSON(array, component, valueFromRequest);
                    }
                    if (isBlackListedFile) {
                        return array;
                    }
                }
            }
            return null;
        }
        
        String findBlackListedFileContent(final HttpServletRequest request, final UploadedFileItem file, final AppFirewallComponent component) {
            boolean matchFound = false;
            String fileContent = null;
            try {
                fileContent = SecurityFrameworkUtil.getFileContentforScanning(file);
            }
            catch (final IOException e) {
                FilesDirective.LOGGER.log(Level.INFO, "Error Occurred while fetching the content of the file ::: Exception ::: {0}", e.getMessage());
            }
            if (fileContent != null) {
                matchFound = component.isBlackListed(fileContent);
                if (matchFound) {
                    return fileContent;
                }
            }
            return null;
        }
    }
}
