package sun.net.www.protocol.file;

import java.io.FilePermission;
import java.io.File;
import java.net.URL;
import java.security.Permission;

final class UNCFileURLConnection extends FileURLConnection
{
    private final String effectivePath;
    private volatile Permission permission;
    
    UNCFileURLConnection(final URL url, final File file, final String effectivePath) {
        super(url, file);
        this.effectivePath = effectivePath;
    }
    
    @Override
    public Permission getPermission() {
        Permission permission = this.permission;
        if (permission == null) {
            permission = (this.permission = new FilePermission(this.effectivePath, "read"));
        }
        return permission;
    }
}
