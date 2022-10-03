package org.apache.poi.poifs.filesystem;

import java.util.List;
import java.util.Collection;
import org.apache.poi.hpsf.ClassID;
import java.io.InputStream;
import java.io.FileNotFoundException;
import java.util.Set;
import java.io.IOException;
import java.util.Iterator;
import org.apache.poi.poifs.property.DocumentProperty;
import java.util.HashMap;
import org.apache.poi.poifs.property.Property;
import org.apache.poi.poifs.property.DirectoryProperty;
import java.util.ArrayList;
import java.util.Map;
import org.apache.poi.poifs.dev.POIFSViewable;

public class DirectoryNode extends EntryNode implements DirectoryEntry, POIFSViewable, Iterable<Entry>
{
    private final Map<String, Entry> _byname;
    private final ArrayList<Entry> _entries;
    private final POIFSFileSystem _filesystem;
    private final POIFSDocumentPath _path;
    
    DirectoryNode(final DirectoryProperty property, final POIFSFileSystem filesystem, final DirectoryNode parent) {
        super(property, parent);
        this._byname = new HashMap<String, Entry>();
        this._entries = new ArrayList<Entry>();
        this._filesystem = filesystem;
        if (parent == null) {
            this._path = new POIFSDocumentPath();
        }
        else {
            this._path = new POIFSDocumentPath(parent._path, new String[] { property.getName() });
        }
        final Iterator<Property> iter = property.getChildren();
        while (iter.hasNext()) {
            final Property child = iter.next();
            Entry childNode;
            if (child.isDirectory()) {
                final DirectoryProperty childDir = (DirectoryProperty)child;
                childNode = new DirectoryNode(childDir, this._filesystem, this);
            }
            else {
                childNode = new DocumentNode((DocumentProperty)child, this);
            }
            this._entries.add(childNode);
            this._byname.put(childNode.getName(), childNode);
        }
    }
    
    public POIFSDocumentPath getPath() {
        return this._path;
    }
    
    public POIFSFileSystem getFileSystem() {
        return this._filesystem;
    }
    
    public DocumentInputStream createDocumentInputStream(final String documentName) throws IOException {
        return this.createDocumentInputStream(this.getEntry(documentName));
    }
    
    public DocumentInputStream createDocumentInputStream(final Entry document) throws IOException {
        if (!document.isDocumentEntry()) {
            throw new IOException("Entry '" + document.getName() + "' is not a DocumentEntry");
        }
        final DocumentEntry entry = (DocumentEntry)document;
        return new DocumentInputStream(entry);
    }
    
    DocumentEntry createDocument(final POIFSDocument document) throws IOException {
        final DocumentProperty property = document.getDocumentProperty();
        final DocumentNode rval = new DocumentNode(property, this);
        ((DirectoryProperty)this.getProperty()).addChild(property);
        this._filesystem.addDocument(document);
        this._entries.add(rval);
        this._byname.put(property.getName(), rval);
        return rval;
    }
    
    boolean changeName(final String oldName, final String newName) {
        boolean rval = false;
        final EntryNode child = this._byname.get(oldName);
        if (child != null) {
            rval = ((DirectoryProperty)this.getProperty()).changeName(child.getProperty(), newName);
            if (rval) {
                this._byname.remove(oldName);
                this._byname.put(child.getProperty().getName(), child);
            }
        }
        return rval;
    }
    
    boolean deleteEntry(final EntryNode entry) {
        final boolean rval = ((DirectoryProperty)this.getProperty()).deleteChild(entry.getProperty());
        if (rval) {
            this._entries.remove(entry);
            this._byname.remove(entry.getName());
            try {
                this._filesystem.remove(entry);
            }
            catch (final IOException e) {
                throw new RuntimeException(e);
            }
        }
        return rval;
    }
    
    @Override
    public Iterator<Entry> getEntries() {
        return this._entries.iterator();
    }
    
    @Override
    public Set<String> getEntryNames() {
        return this._byname.keySet();
    }
    
    @Override
    public boolean isEmpty() {
        return this._entries.isEmpty();
    }
    
    @Override
    public int getEntryCount() {
        return this._entries.size();
    }
    
    @Override
    public boolean hasEntry(final String name) {
        return name != null && this._byname.containsKey(name);
    }
    
    @Override
    public Entry getEntry(final String name) throws FileNotFoundException {
        Entry rval = null;
        if (name != null) {
            rval = this._byname.get(name);
        }
        if (rval != null) {
            return rval;
        }
        if (this._byname.containsKey("Workbook")) {
            throw new IllegalArgumentException("The document is really a XLS file");
        }
        if (this._byname.containsKey("PowerPoint Document")) {
            throw new IllegalArgumentException("The document is really a PPT file");
        }
        if (this._byname.containsKey("VisioDocument")) {
            throw new IllegalArgumentException("The document is really a VSD file");
        }
        throw new FileNotFoundException("no such entry: \"" + name + "\", had: " + this._byname.keySet());
    }
    
    @Override
    public DocumentEntry createDocument(final String name, final InputStream stream) throws IOException {
        return this.createDocument(new POIFSDocument(name, this._filesystem, stream));
    }
    
    @Override
    public DocumentEntry createDocument(final String name, final int size, final POIFSWriterListener writer) throws IOException {
        return this.createDocument(new POIFSDocument(name, size, this._filesystem, writer));
    }
    
    @Override
    public DirectoryEntry createDirectory(final String name) throws IOException {
        final DirectoryProperty property = new DirectoryProperty(name);
        final DirectoryNode rval = new DirectoryNode(property, this._filesystem, this);
        this._filesystem.addDirectory(property);
        ((DirectoryProperty)this.getProperty()).addChild(property);
        this._entries.add(rval);
        this._byname.put(name, rval);
        return rval;
    }
    
    public DocumentEntry createOrUpdateDocument(final String name, final InputStream stream) throws IOException {
        if (!this.hasEntry(name)) {
            return this.createDocument(name, stream);
        }
        final DocumentNode existing = (DocumentNode)this.getEntry(name);
        final POIFSDocument nDoc = new POIFSDocument(existing);
        nDoc.replaceContents(stream);
        return existing;
    }
    
    @Override
    public ClassID getStorageClsid() {
        return this.getProperty().getStorageClsid();
    }
    
    @Override
    public void setStorageClsid(final ClassID clsidStorage) {
        this.getProperty().setStorageClsid(clsidStorage);
    }
    
    @Override
    public boolean isDirectoryEntry() {
        return true;
    }
    
    @Override
    protected boolean isDeleteOK() {
        return this.isEmpty();
    }
    
    @Override
    public Object[] getViewableArray() {
        return new Object[0];
    }
    
    @Override
    public Iterator<Object> getViewableIterator() {
        final List<Object> components = new ArrayList<Object>();
        components.add(this.getProperty());
        components.addAll(this._entries);
        return components.iterator();
    }
    
    @Override
    public boolean preferArray() {
        return false;
    }
    
    @Override
    public String getShortDescription() {
        return this.getName();
    }
    
    @Override
    public Iterator<Entry> iterator() {
        return this.getEntries();
    }
}
