package org.apache.poi.poifs.filesystem;

import org.apache.poi.util.HexDump;
import java.util.Collections;
import java.util.Iterator;
import java.io.OutputStream;
import java.util.Arrays;
import org.apache.poi.util.IOUtils;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.poifs.property.DocumentProperty;
import java.nio.ByteBuffer;
import org.apache.poi.poifs.dev.POIFSViewable;

public final class POIFSDocument implements POIFSViewable, Iterable<ByteBuffer>
{
    private static final int MAX_RECORD_LENGTH = 100000;
    private DocumentProperty _property;
    private POIFSFileSystem _filesystem;
    private POIFSStream _stream;
    private int _block_size;
    
    public POIFSDocument(final DocumentNode document) {
        this((DocumentProperty)document.getProperty(), ((DirectoryNode)document.getParent()).getFileSystem());
    }
    
    public POIFSDocument(final DocumentProperty property, final POIFSFileSystem filesystem) {
        this._property = property;
        this._filesystem = filesystem;
        if (property.getSize() < 4096) {
            this._stream = new POIFSStream(this._filesystem.getMiniStore(), property.getStartBlock());
            this._block_size = this._filesystem.getMiniStore().getBlockStoreBlockSize();
        }
        else {
            this._stream = new POIFSStream(this._filesystem, property.getStartBlock());
            this._block_size = this._filesystem.getBlockStoreBlockSize();
        }
    }
    
    public POIFSDocument(final String name, final POIFSFileSystem filesystem, final InputStream stream) throws IOException {
        this._filesystem = filesystem;
        final int length = this.store(stream);
        (this._property = new DocumentProperty(name, length)).setStartBlock(this._stream.getStartBlock());
        this._property.setDocument(this);
    }
    
    public POIFSDocument(final String name, final int size, final POIFSFileSystem filesystem, final POIFSWriterListener writer) throws IOException {
        this._filesystem = filesystem;
        if (size < 4096) {
            this._stream = new POIFSStream(filesystem.getMiniStore());
            this._block_size = this._filesystem.getMiniStore().getBlockStoreBlockSize();
        }
        else {
            this._stream = new POIFSStream(filesystem);
            this._block_size = this._filesystem.getBlockStoreBlockSize();
        }
        (this._property = new DocumentProperty(name, size)).setStartBlock(this._stream.getStartBlock());
        this._property.setDocument(this);
        try (final DocumentOutputStream os = new DocumentOutputStream(this, size)) {
            final POIFSDocumentPath path = new POIFSDocumentPath(name.split("\\\\"));
            final String docName = path.getComponent(path.length() - 1);
            final POIFSWriterEvent event = new POIFSWriterEvent(os, path, docName, size);
            writer.processPOIFSWriterEvent(event);
        }
    }
    
    private int store(final InputStream stream) throws IOException {
        final int bigBlockSize = 4096;
        final BufferedInputStream bis = new BufferedInputStream(stream, 4097);
        bis.mark(4096);
        final long streamBlockSize = IOUtils.skipFully(bis, 4096L);
        if (streamBlockSize < 4096L) {
            this._stream = new POIFSStream(this._filesystem.getMiniStore());
            this._block_size = this._filesystem.getMiniStore().getBlockStoreBlockSize();
        }
        else {
            this._stream = new POIFSStream(this._filesystem);
            this._block_size = this._filesystem.getBlockStoreBlockSize();
        }
        bis.reset();
        long length;
        try (final OutputStream os = this._stream.getOutputStream()) {
            length = IOUtils.copy(bis, os);
            final int usedInBlock = (int)(length % this._block_size);
            if (usedInBlock != 0 && usedInBlock != this._block_size) {
                final int toBlockEnd = this._block_size - usedInBlock;
                final byte[] padding = IOUtils.safelyAllocate(toBlockEnd, 100000);
                Arrays.fill(padding, (byte)(-1));
                os.write(padding);
            }
        }
        return Math.toIntExact(length);
    }
    
    void free() throws IOException {
        this._stream.free();
        this._property.setStartBlock(-2);
    }
    
    POIFSFileSystem getFileSystem() {
        return this._filesystem;
    }
    
    int getDocumentBlockSize() {
        return this._block_size;
    }
    
    @Override
    public Iterator<ByteBuffer> iterator() {
        return this.getBlockIterator();
    }
    
    Iterator<ByteBuffer> getBlockIterator() {
        return ((Iterable<ByteBuffer>)((this.getSize() > 0) ? this._stream : Collections.emptyList())).iterator();
    }
    
    public int getSize() {
        return this._property.getSize();
    }
    
    public void replaceContents(final InputStream stream) throws IOException {
        this.free();
        final int size = this.store(stream);
        this._property.setStartBlock(this._stream.getStartBlock());
        this._property.updateSize(size);
    }
    
    DocumentProperty getDocumentProperty() {
        return this._property;
    }
    
    @Override
    public Object[] getViewableArray() {
        String result = "<NO DATA>";
        if (this.getSize() > 0) {
            final byte[] data = IOUtils.safelyAllocate(this.getSize(), 100000);
            int offset = 0;
            for (final ByteBuffer buffer : this._stream) {
                final int length = Math.min(this._block_size, data.length - offset);
                buffer.get(data, offset, length);
                offset += length;
            }
            result = HexDump.dump(data, 0L, 0);
        }
        return new String[] { result };
    }
    
    @Override
    public Iterator<Object> getViewableIterator() {
        return Collections.emptyList().iterator();
    }
    
    @Override
    public boolean preferArray() {
        return true;
    }
    
    @Override
    public String getShortDescription() {
        return "Document: \"" + this._property.getName() + "\" size = " + this.getSize();
    }
}
