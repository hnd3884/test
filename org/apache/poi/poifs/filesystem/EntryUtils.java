package org.apache.poi.poifs.filesystem;

import java.io.EOFException;
import org.apache.poi.hpsf.NoPropertySetStreamException;
import org.apache.poi.hpsf.MarkUnsupportedException;
import org.apache.poi.hpsf.PropertySetFactory;
import org.apache.poi.hpsf.PropertySet;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;
import java.util.Map;
import java.util.Collection;
import java.util.List;
import java.io.IOException;
import java.util.Iterator;
import java.io.InputStream;
import org.apache.poi.util.Internal;

@Internal
public final class EntryUtils
{
    private EntryUtils() {
    }
    
    @Internal
    public static void copyNodeRecursively(final Entry entry, final DirectoryEntry target) throws IOException {
        if (entry.isDirectoryEntry()) {
            final DirectoryEntry dirEntry = (DirectoryEntry)entry;
            final DirectoryEntry newTarget = target.createDirectory(entry.getName());
            newTarget.setStorageClsid(dirEntry.getStorageClsid());
            final Iterator<Entry> entries = dirEntry.getEntries();
            while (entries.hasNext()) {
                copyNodeRecursively(entries.next(), newTarget);
            }
        }
        else {
            final DocumentEntry dentry = (DocumentEntry)entry;
            final DocumentInputStream dstream = new DocumentInputStream(dentry);
            target.createDocument(dentry.getName(), dstream);
            dstream.close();
        }
    }
    
    public static void copyNodes(final DirectoryEntry sourceRoot, final DirectoryEntry targetRoot) throws IOException {
        for (final Entry entry : sourceRoot) {
            copyNodeRecursively(entry, targetRoot);
        }
    }
    
    public static void copyNodes(final POIFSFileSystem source, final POIFSFileSystem target) throws IOException {
        copyNodes(source.getRoot(), target.getRoot());
    }
    
    public static void copyNodes(final POIFSFileSystem source, final POIFSFileSystem target, final List<String> excepts) throws IOException {
        copyNodes(new FilteringDirectoryNode(source.getRoot(), excepts), new FilteringDirectoryNode(target.getRoot(), excepts));
    }
    
    public static boolean areDirectoriesIdentical(final DirectoryEntry dirA, final DirectoryEntry dirB) {
        return new DirectoryDelegate(dirA).equals(new DirectoryDelegate(dirB));
    }
    
    public static boolean areDocumentsIdentical(final DocumentEntry docA, final DocumentEntry docB) throws IOException {
        try {
            return new DocumentDelegate(docA).equals(new DocumentDelegate(docB));
        }
        catch (final RuntimeException e) {
            if (e.getCause() instanceof IOException) {
                throw (IOException)e.getCause();
            }
            throw e;
        }
    }
    
    private static class DirectoryDelegate implements POIDelegate
    {
        final DirectoryEntry dir;
        
        DirectoryDelegate(final DirectoryEntry dir) {
            this.dir = dir;
        }
        
        private Map<String, POIDelegate> entries() {
            return StreamSupport.stream(this.dir.spliterator(), false).collect(Collectors.toMap((Function<? super Entry, ? extends String>)Entry::getName, (Function<? super Entry, ? extends POIDelegate>)DirectoryDelegate::toDelegate));
        }
        
        private static POIDelegate toDelegate(final Entry entry) {
            return entry.isDirectoryEntry() ? new DirectoryDelegate((DirectoryEntry)entry) : new DocumentDelegate((DocumentEntry)entry);
        }
        
        @Override
        public int hashCode() {
            return this.dir.getName().hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof DirectoryDelegate)) {
                return false;
            }
            final DirectoryDelegate dd = (DirectoryDelegate)other;
            return this == dd || (Objects.equals(this.dir.getName(), dd.dir.getName()) && this.dir.getEntryCount() == dd.dir.getEntryCount() && this.entries().equals(dd.entries()));
        }
    }
    
    private static class DocumentDelegate implements POIDelegate
    {
        final DocumentEntry doc;
        
        DocumentDelegate(final DocumentEntry doc) {
            this.doc = doc;
        }
        
        @Override
        public int hashCode() {
            return this.doc.getName().hashCode();
        }
        
        @Override
        public boolean equals(final Object other) {
            if (!(other instanceof DocumentDelegate)) {
                return false;
            }
            final DocumentDelegate dd = (DocumentDelegate)other;
            if (this == dd) {
                return true;
            }
            if (!Objects.equals(this.doc.getName(), dd.doc.getName())) {
                return false;
            }
            try (final DocumentInputStream inpA = new DocumentInputStream(this.doc);
                 final DocumentInputStream inpB = new DocumentInputStream(dd.doc)) {
                if (PropertySet.isPropertySetStream(inpA) && PropertySet.isPropertySetStream(inpB)) {
                    final PropertySet ps1 = PropertySetFactory.create(inpA);
                    final PropertySet ps2 = PropertySetFactory.create(inpB);
                    return ps1.equals(ps2);
                }
                return isEqual(inpA, inpB);
            }
            catch (final MarkUnsupportedException | NoPropertySetStreamException | IOException ex) {
                throw new RuntimeException(ex);
            }
        }
        
        private static boolean isEqual(final DocumentInputStream i1, final DocumentInputStream i2) throws IOException {
            final byte[] buf1 = new byte[4096];
            final byte[] buf2 = new byte[4096];
            try {
                int len;
                while ((len = i1.read(buf1)) > 0) {
                    i2.readFully(buf2, 0, len);
                    for (int j = 0; j < len; ++j) {
                        if (buf1[j] != buf2[j]) {
                            return false;
                        }
                    }
                }
                return i2.read() < 0;
            }
            catch (final EOFException | RuntimeException ioe) {
                return false;
            }
        }
    }
    
    private interface POIDelegate
    {
    }
}
