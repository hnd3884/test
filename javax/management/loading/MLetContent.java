package javax.management.loading;

import java.net.MalformedURLException;
import java.util.Collections;
import java.net.URL;
import java.util.List;
import java.util.Map;

public class MLetContent
{
    private Map<String, String> attributes;
    private List<String> types;
    private List<String> values;
    private URL documentURL;
    private URL baseURL;
    
    public MLetContent(final URL documentURL, final Map<String, String> map, final List<String> list, final List<String> list2) {
        this.documentURL = documentURL;
        this.attributes = Collections.unmodifiableMap((Map<? extends String, ? extends String>)map);
        this.types = Collections.unmodifiableList((List<? extends String>)list);
        this.values = Collections.unmodifiableList((List<? extends String>)list2);
        String s = this.getParameter("codebase");
        if (s != null) {
            if (!s.endsWith("/")) {
                s += "/";
            }
            try {
                this.baseURL = new URL(this.documentURL, s);
            }
            catch (final MalformedURLException ex) {}
        }
        if (this.baseURL == null) {
            final String file = this.documentURL.getFile();
            final int lastIndex = file.lastIndexOf(47);
            if (lastIndex >= 0 && lastIndex < file.length() - 1) {
                try {
                    this.baseURL = new URL(this.documentURL, file.substring(0, lastIndex + 1));
                }
                catch (final MalformedURLException ex2) {}
            }
        }
        if (this.baseURL == null) {
            this.baseURL = this.documentURL;
        }
    }
    
    public Map<String, String> getAttributes() {
        return this.attributes;
    }
    
    public URL getDocumentBase() {
        return this.documentURL;
    }
    
    public URL getCodeBase() {
        return this.baseURL;
    }
    
    public String getJarFiles() {
        return this.getParameter("archive");
    }
    
    public String getCode() {
        return this.getParameter("code");
    }
    
    public String getSerializedObject() {
        return this.getParameter("object");
    }
    
    public String getName() {
        return this.getParameter("name");
    }
    
    public String getVersion() {
        return this.getParameter("version");
    }
    
    public List<String> getParameterTypes() {
        return this.types;
    }
    
    public List<String> getParameterValues() {
        return this.values;
    }
    
    private String getParameter(final String s) {
        return this.attributes.get(s.toLowerCase());
    }
}
