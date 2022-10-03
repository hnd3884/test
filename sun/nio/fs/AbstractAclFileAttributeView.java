package sun.nio.fs;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.io.IOException;
import java.nio.file.attribute.AclEntry;
import java.util.List;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.attribute.AclFileAttributeView;

abstract class AbstractAclFileAttributeView implements AclFileAttributeView, DynamicFileAttributeView
{
    private static final String OWNER_NAME = "owner";
    private static final String ACL_NAME = "acl";
    
    @Override
    public final String name() {
        return "acl";
    }
    
    @Override
    public final void setAttribute(final String s, final Object o) throws IOException {
        if (s.equals("owner")) {
            this.setOwner((UserPrincipal)o);
            return;
        }
        if (s.equals("acl")) {
            this.setAcl((List<AclEntry>)o);
            return;
        }
        throw new IllegalArgumentException("'" + this.name() + ":" + s + "' not recognized");
    }
    
    @Override
    public final Map<String, Object> readAttributes(final String[] array) throws IOException {
        boolean b = false;
        boolean b2 = false;
        for (final String s : array) {
            if (s.equals("*")) {
                b2 = true;
                b = true;
            }
            else if (s.equals("acl")) {
                b = true;
            }
            else {
                if (!s.equals("owner")) {
                    throw new IllegalArgumentException("'" + this.name() + ":" + s + "' not recognized");
                }
                b2 = true;
            }
        }
        final HashMap hashMap = new HashMap(2);
        if (b) {
            hashMap.put("acl", this.getAcl());
        }
        if (b2) {
            hashMap.put("owner", this.getOwner());
        }
        return (Map<String, Object>)Collections.unmodifiableMap((Map<?, ?>)hashMap);
    }
}
