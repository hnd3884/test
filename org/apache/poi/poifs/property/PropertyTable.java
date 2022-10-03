package org.apache.poi.poifs.property;

import org.apache.poi.util.POILogFactory;
import java.util.Stack;
import java.io.OutputStream;
import java.util.Iterator;
import org.apache.poi.util.IOUtils;
import java.io.IOException;
import java.nio.ByteBuffer;
import org.apache.poi.poifs.filesystem.BlockStore;
import org.apache.poi.poifs.filesystem.POIFSStream;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import java.util.ArrayList;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import java.util.List;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.util.POILogger;
import org.apache.poi.poifs.filesystem.BATManaged;

public final class PropertyTable implements BATManaged
{
    private static final POILogger _logger;
    private static final int MAX_RECORD_LENGTH = 100000;
    private final HeaderBlock _header_block;
    private final List<Property> _properties;
    private final POIFSBigBlockSize _bigBigBlockSize;
    
    public PropertyTable(final HeaderBlock headerBlock) {
        this._properties = new ArrayList<Property>();
        this._header_block = headerBlock;
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
        this.addProperty(new RootProperty());
    }
    
    public PropertyTable(final HeaderBlock headerBlock, final POIFSFileSystem filesystem) throws IOException {
        this(headerBlock, new POIFSStream(filesystem, headerBlock.getPropertyStart()));
    }
    
    PropertyTable(final HeaderBlock headerBlock, final Iterable<ByteBuffer> dataSource) throws IOException {
        this._properties = new ArrayList<Property>();
        this._header_block = headerBlock;
        this._bigBigBlockSize = headerBlock.getBigBlockSize();
        for (final ByteBuffer bb : dataSource) {
            byte[] data;
            if (bb.hasArray() && bb.arrayOffset() == 0 && bb.array().length == this._bigBigBlockSize.getBigBlockSize()) {
                data = bb.array();
            }
            else {
                data = IOUtils.safelyAllocate(this._bigBigBlockSize.getBigBlockSize(), 100000);
                int toRead = data.length;
                if (bb.remaining() < this._bigBigBlockSize.getBigBlockSize()) {
                    PropertyTable._logger.log(5, "Short Property Block, ", bb.remaining(), " bytes instead of the expected " + this._bigBigBlockSize.getBigBlockSize());
                    toRead = bb.remaining();
                }
                bb.get(data, 0, toRead);
            }
            PropertyFactory.convertToProperties(data, this._properties);
        }
        this.populatePropertyTree(this._properties.get(0));
    }
    
    public void addProperty(final Property property) {
        this._properties.add(property);
    }
    
    public void removeProperty(final Property property) {
        this._properties.remove(property);
    }
    
    public RootProperty getRoot() {
        return this._properties.get(0);
    }
    
    public int getStartBlock() {
        return this._header_block.getPropertyStart();
    }
    
    @Override
    public void setStartBlock(final int index) {
        this._header_block.setPropertyStart(index);
    }
    
    @Override
    public int countBlocks() {
        final long rawSize = this._properties.size() * 128L;
        final int blkSize = this._bigBigBlockSize.getBigBlockSize();
        int numBlocks = (int)(rawSize / blkSize);
        if (rawSize % blkSize != 0L) {
            ++numBlocks;
        }
        return numBlocks;
    }
    
    public void preWrite() {
        final List<Property> pList = new ArrayList<Property>();
        int i = 0;
        for (final Property p : this._properties) {
            if (p == null) {
                continue;
            }
            p.setIndex(i++);
            pList.add(p);
        }
        for (final Property p : pList) {
            p.preWrite();
        }
    }
    
    public void write(final POIFSStream stream) throws IOException {
        final OutputStream os = stream.getOutputStream();
        for (final Property property : this._properties) {
            if (property != null) {
                property.writeData(os);
            }
        }
        os.close();
        if (this.getStartBlock() != stream.getStartBlock()) {
            this.setStartBlock(stream.getStartBlock());
        }
    }
    
    private void populatePropertyTree(final DirectoryProperty root) throws IOException {
        int index = root.getChildIndex();
        if (!Property.isValidIndex(index)) {
            return;
        }
        final Stack<Property> children = new Stack<Property>();
        children.push(this._properties.get(index));
        while (!children.empty()) {
            final Property property = children.pop();
            if (property == null) {
                continue;
            }
            root.addChild(property);
            if (property.isDirectory()) {
                this.populatePropertyTree((DirectoryProperty)property);
            }
            index = property.getPreviousChildIndex();
            if (this.isValidIndex(index)) {
                children.push(this._properties.get(index));
            }
            index = property.getNextChildIndex();
            if (!this.isValidIndex(index)) {
                continue;
            }
            children.push(this._properties.get(index));
        }
    }
    
    private boolean isValidIndex(final int index) {
        if (!Property.isValidIndex(index)) {
            return false;
        }
        if (index < 0 || index >= this._properties.size()) {
            PropertyTable._logger.log(5, "Property index " + index + "outside the valid range 0.." + this._properties.size());
            return false;
        }
        return true;
    }
    
    static {
        _logger = POILogFactory.getLogger(PropertyTable.class);
    }
}
