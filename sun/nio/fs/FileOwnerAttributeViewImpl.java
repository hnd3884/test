package sun.nio.fs;

import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.attribute.PosixFileAttributeView;
import java.nio.file.attribute.FileAttributeView;
import java.nio.file.attribute.FileOwnerAttributeView;

final class FileOwnerAttributeViewImpl implements FileOwnerAttributeView, DynamicFileAttributeView
{
    private static final String OWNER_NAME = "owner";
    private final FileAttributeView view;
    private final boolean isPosixView;
    
    FileOwnerAttributeViewImpl(final PosixFileAttributeView view) {
        this.view = view;
        this.isPosixView = true;
    }
    
    FileOwnerAttributeViewImpl(final AclFileAttributeView view) {
        this.view = view;
        this.isPosixView = false;
    }
    
    @Override
    public String name() {
        return "owner";
    }
    
    @Override
    public void setAttribute(final String s, final Object o) throws IOException {
        if (s.equals("owner")) {
            this.setOwner((UserPrincipal)o);
            return;
        }
        throw new IllegalArgumentException("'" + this.name() + ":" + s + "' not recognized");
    }
    
    @Override
    public Map<String, Object> readAttributes(final String[] array) throws IOException {
        final HashMap hashMap = new HashMap();
        for (final String s : array) {
            if (!s.equals("*") && !s.equals("owner")) {
                throw new IllegalArgumentException("'" + this.name() + ":" + s + "' not recognized");
            }
            hashMap.put("owner", this.getOwner());
        }
        return hashMap;
    }
    
    @Override
    public UserPrincipal getOwner() throws IOException {
        if (this.isPosixView) {
            return ((PosixFileAttributeView)this.view).readAttributes().owner();
        }
        return ((AclFileAttributeView)this.view).getOwner();
    }
    
    @Override
    public void setOwner(final UserPrincipal userPrincipal) throws IOException {
        if (this.isPosixView) {
            ((PosixFileAttributeView)this.view).setOwner(userPrincipal);
        }
        else {
            ((AclFileAttributeView)this.view).setOwner(userPrincipal);
        }
    }
}
