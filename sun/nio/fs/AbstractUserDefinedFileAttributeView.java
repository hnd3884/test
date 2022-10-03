package sun.nio.fs;

import java.util.Iterator;
import java.util.List;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.security.Permission;
import java.nio.file.attribute.UserDefinedFileAttributeView;

abstract class AbstractUserDefinedFileAttributeView implements UserDefinedFileAttributeView, DynamicFileAttributeView
{
    protected AbstractUserDefinedFileAttributeView() {
    }
    
    protected void checkAccess(final String s, final boolean b, final boolean b2) {
        assert b || b2;
        final SecurityManager securityManager = System.getSecurityManager();
        if (securityManager != null) {
            if (b) {
                securityManager.checkRead(s);
            }
            if (b2) {
                securityManager.checkWrite(s);
            }
            securityManager.checkPermission(new RuntimePermission("accessUserDefinedAttributes"));
        }
    }
    
    @Override
    public final String name() {
        return "user";
    }
    
    @Override
    public final void setAttribute(final String s, final Object o) throws IOException {
        ByteBuffer wrap;
        if (o instanceof byte[]) {
            wrap = ByteBuffer.wrap((byte[])o);
        }
        else {
            wrap = (ByteBuffer)o;
        }
        this.write(s, wrap);
    }
    
    @Override
    public final Map<String, Object> readAttributes(final String[] array) throws IOException {
        List<String> list = new ArrayList<String>();
        for (final String s : array) {
            if (s.equals("*")) {
                list = this.list();
                break;
            }
            if (s.length() == 0) {
                throw new IllegalArgumentException();
            }
            list.add(s);
        }
        final HashMap hashMap = new HashMap();
        for (final String s2 : list) {
            final int size = this.size(s2);
            final byte[] array2 = new byte[size];
            final int read = this.read(s2, ByteBuffer.wrap(array2));
            hashMap.put(s2, (read == size) ? array2 : Arrays.copyOf(array2, read));
        }
        return hashMap;
    }
}
