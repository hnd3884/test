package com.google.api.client.util.store;

import java.io.OutputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.HashMap;
import com.google.api.client.util.Maps;
import java.util.Locale;
import com.google.common.base.StandardSystemProperty;
import java.nio.file.attribute.UserPrincipal;
import java.nio.file.Path;
import java.util.List;
import com.google.common.collect.ImmutableList;
import java.nio.file.attribute.AclEntryType;
import java.nio.file.attribute.AclEntry;
import com.google.common.collect.ImmutableSet;
import java.nio.file.attribute.AclEntryPermission;
import java.nio.file.attribute.AclFileAttributeView;
import java.nio.file.LinkOption;
import java.nio.file.attribute.FileOwnerAttributeView;
import java.util.Set;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.attribute.PosixFilePermission;
import java.util.HashSet;
import java.io.Serializable;
import java.io.IOException;
import com.google.api.client.util.IOUtils;
import java.io.File;

public class FileDataStoreFactory extends AbstractDataStoreFactory
{
    private static final boolean IS_WINDOWS;
    private final File dataDirectory;
    
    public FileDataStoreFactory(File dataDirectory) throws IOException {
        dataDirectory = dataDirectory.getCanonicalFile();
        if (IOUtils.isSymbolicLink(dataDirectory)) {
            throw new IOException("unable to use a symbolic link: " + dataDirectory);
        }
        if (!dataDirectory.exists() && !dataDirectory.mkdirs()) {
            throw new IOException("unable to create directory: " + dataDirectory);
        }
        this.dataDirectory = dataDirectory;
        if (FileDataStoreFactory.IS_WINDOWS) {
            setPermissionsToOwnerOnlyWindows(dataDirectory);
        }
        else {
            setPermissionsToOwnerOnly(dataDirectory);
        }
    }
    
    public final File getDataDirectory() {
        return this.dataDirectory;
    }
    
    @Override
    protected <V extends Serializable> DataStore<V> createDataStore(final String id) throws IOException {
        return new FileDataStore<V>(this, this.dataDirectory, id);
    }
    
    private static void setPermissionsToOwnerOnly(final File file) throws IOException {
        final Set permissions = new HashSet();
        permissions.add(PosixFilePermission.OWNER_READ);
        permissions.add(PosixFilePermission.OWNER_WRITE);
        permissions.add(PosixFilePermission.OWNER_EXECUTE);
        try {
            Files.setPosixFilePermissions(Paths.get(file.getAbsolutePath(), new String[0]), permissions);
        }
        catch (final RuntimeException exception) {
            throw new IOException("Unable to set permissions for " + file, exception);
        }
    }
    
    private static void setPermissionsToOwnerOnlyWindows(final File file) throws IOException {
        final Path path = Paths.get(file.getAbsolutePath(), new String[0]);
        final FileOwnerAttributeView fileAttributeView = Files.getFileAttributeView(path, FileOwnerAttributeView.class, new LinkOption[0]);
        final UserPrincipal owner = fileAttributeView.getOwner();
        final AclFileAttributeView view = Files.getFileAttributeView(path, AclFileAttributeView.class, new LinkOption[0]);
        final Set<AclEntryPermission> permissions = (Set<AclEntryPermission>)ImmutableSet.of((Object)AclEntryPermission.APPEND_DATA, (Object)AclEntryPermission.DELETE, (Object)AclEntryPermission.DELETE_CHILD, (Object)AclEntryPermission.READ_ACL, (Object)AclEntryPermission.READ_ATTRIBUTES, (Object)AclEntryPermission.READ_DATA, (Object[])new AclEntryPermission[] { AclEntryPermission.READ_NAMED_ATTRS, AclEntryPermission.SYNCHRONIZE, AclEntryPermission.WRITE_ACL, AclEntryPermission.WRITE_ATTRIBUTES, AclEntryPermission.WRITE_DATA, AclEntryPermission.WRITE_NAMED_ATTRS, AclEntryPermission.WRITE_OWNER });
        final AclEntry entry = AclEntry.newBuilder().setType(AclEntryType.ALLOW).setPrincipal(owner).setPermissions(permissions).build();
        try {
            view.setAcl((List<AclEntry>)ImmutableList.of((Object)entry));
        }
        catch (final SecurityException ex) {
            throw new IOException("Unable to set permissions for " + file, ex);
        }
    }
    
    static {
        IS_WINDOWS = StandardSystemProperty.OS_NAME.value().toLowerCase(Locale.ENGLISH).startsWith("windows");
    }
    
    static class FileDataStore<V extends Serializable> extends AbstractMemoryDataStore<V>
    {
        private final File dataFile;
        
        FileDataStore(final FileDataStoreFactory dataStore, final File dataDirectory, final String id) throws IOException {
            super(dataStore, id);
            this.dataFile = new File(dataDirectory, id);
            if (IOUtils.isSymbolicLink(this.dataFile)) {
                throw new IOException("unable to use a symbolic link: " + this.dataFile);
            }
            if (this.dataFile.createNewFile()) {
                this.keyValueMap = Maps.newHashMap();
                this.save();
            }
            else {
                this.keyValueMap = IOUtils.deserialize(new FileInputStream(this.dataFile));
            }
        }
        
        @Override
        public void save() throws IOException {
            IOUtils.serialize(this.keyValueMap, new FileOutputStream(this.dataFile));
        }
        
        @Override
        public FileDataStoreFactory getDataStoreFactory() {
            return (FileDataStoreFactory)super.getDataStoreFactory();
        }
    }
}
