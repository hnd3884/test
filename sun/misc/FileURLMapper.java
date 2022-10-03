package sun.misc;

import java.io.File;
import sun.net.www.ParseUtil;
import java.net.URL;

public class FileURLMapper
{
    URL url;
    String file;
    
    public FileURLMapper(final URL url) {
        this.url = url;
    }
    
    public String getPath() {
        if (this.file != null) {
            return this.file;
        }
        final String host = this.url.getHost();
        if (host != null && !host.equals("") && !"localhost".equalsIgnoreCase(host)) {
            this.url.getFile();
            return this.file = "\\\\" + (host + ParseUtil.decode(this.url.getFile())).replace('/', '\\');
        }
        return this.file = ParseUtil.decode(this.url.getFile().replace('/', '\\'));
    }
    
    public boolean exists() {
        return new File(this.getPath()).exists();
    }
}
