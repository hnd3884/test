package sun.nio.fs;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.nio.file.attribute.BasicFileAttributes;
import java.io.IOException;
import java.nio.file.attribute.FileTime;
import java.util.Set;
import java.nio.file.attribute.BasicFileAttributeView;

abstract class AbstractBasicFileAttributeView implements BasicFileAttributeView, DynamicFileAttributeView
{
    private static final String SIZE_NAME = "size";
    private static final String CREATION_TIME_NAME = "creationTime";
    private static final String LAST_ACCESS_TIME_NAME = "lastAccessTime";
    private static final String LAST_MODIFIED_TIME_NAME = "lastModifiedTime";
    private static final String FILE_KEY_NAME = "fileKey";
    private static final String IS_DIRECTORY_NAME = "isDirectory";
    private static final String IS_REGULAR_FILE_NAME = "isRegularFile";
    private static final String IS_SYMBOLIC_LINK_NAME = "isSymbolicLink";
    private static final String IS_OTHER_NAME = "isOther";
    static final Set<String> basicAttributeNames;
    
    protected AbstractBasicFileAttributeView() {
    }
    
    @Override
    public String name() {
        return "basic";
    }
    
    @Override
    public void setAttribute(final String s, final Object o) throws IOException {
        if (s.equals("lastModifiedTime")) {
            this.setTimes((FileTime)o, null, null);
            return;
        }
        if (s.equals("lastAccessTime")) {
            this.setTimes(null, (FileTime)o, null);
            return;
        }
        if (s.equals("creationTime")) {
            this.setTimes(null, null, (FileTime)o);
            return;
        }
        throw new IllegalArgumentException("'" + this.name() + ":" + s + "' not recognized");
    }
    
    final void addRequestedBasicAttributes(final BasicFileAttributes basicFileAttributes, final AttributesBuilder attributesBuilder) {
        if (attributesBuilder.match("size")) {
            attributesBuilder.add("size", basicFileAttributes.size());
        }
        if (attributesBuilder.match("creationTime")) {
            attributesBuilder.add("creationTime", basicFileAttributes.creationTime());
        }
        if (attributesBuilder.match("lastAccessTime")) {
            attributesBuilder.add("lastAccessTime", basicFileAttributes.lastAccessTime());
        }
        if (attributesBuilder.match("lastModifiedTime")) {
            attributesBuilder.add("lastModifiedTime", basicFileAttributes.lastModifiedTime());
        }
        if (attributesBuilder.match("fileKey")) {
            attributesBuilder.add("fileKey", basicFileAttributes.fileKey());
        }
        if (attributesBuilder.match("isDirectory")) {
            attributesBuilder.add("isDirectory", basicFileAttributes.isDirectory());
        }
        if (attributesBuilder.match("isRegularFile")) {
            attributesBuilder.add("isRegularFile", basicFileAttributes.isRegularFile());
        }
        if (attributesBuilder.match("isSymbolicLink")) {
            attributesBuilder.add("isSymbolicLink", basicFileAttributes.isSymbolicLink());
        }
        if (attributesBuilder.match("isOther")) {
            attributesBuilder.add("isOther", basicFileAttributes.isOther());
        }
    }
    
    @Override
    public Map<String, Object> readAttributes(final String[] array) throws IOException {
        final AttributesBuilder create = AttributesBuilder.create(AbstractBasicFileAttributeView.basicAttributeNames, array);
        this.addRequestedBasicAttributes(this.readAttributes(), create);
        return create.unmodifiableMap();
    }
    
    static {
        basicAttributeNames = Util.newSet("size", "creationTime", "lastAccessTime", "lastModifiedTime", "fileKey", "isDirectory", "isRegularFile", "isSymbolicLink", "isOther");
    }
    
    static class AttributesBuilder
    {
        private Set<String> names;
        private Map<String, Object> map;
        private boolean copyAll;
        
        private AttributesBuilder(final Set<String> set, final String[] array) {
            this.names = new HashSet<String>();
            this.map = new HashMap<String, Object>();
            for (final String s : array) {
                if (s.equals("*")) {
                    this.copyAll = true;
                }
                else {
                    if (!set.contains(s)) {
                        throw new IllegalArgumentException("'" + s + "' not recognized");
                    }
                    this.names.add(s);
                }
            }
        }
        
        static AttributesBuilder create(final Set<String> set, final String[] array) {
            return new AttributesBuilder(set, array);
        }
        
        boolean match(final String s) {
            return this.copyAll || this.names.contains(s);
        }
        
        void add(final String s, final Object o) {
            this.map.put(s, o);
        }
        
        Map<String, Object> unmodifiableMap() {
            return Collections.unmodifiableMap((Map<? extends String, ?>)this.map);
        }
    }
}
