package org.apache.poi.poifs.filesystem;

import org.apache.poi.util.POILogFactory;
import org.apache.poi.util.Internal;
import java.util.Collections;
import org.apache.poi.poifs.property.DocumentProperty;
import java.io.FileOutputStream;
import java.io.FileInputStream;
import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import org.apache.poi.poifs.property.DirectoryProperty;
import org.apache.poi.poifs.property.Property;
import java.util.Iterator;
import java.nio.channels.Channels;
import java.io.InputStream;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import org.apache.poi.poifs.nio.FileBackedDataSource;
import org.apache.poi.EmptyFileException;
import java.nio.channels.FileChannel;
import java.io.IOException;
import java.io.File;
import org.apache.poi.poifs.nio.ByteArrayBackedDataSource;
import org.apache.poi.util.IOUtils;
import org.apache.commons.math3.util.ArithmeticUtils;
import java.util.ArrayList;
import org.apache.poi.poifs.common.POIFSConstants;
import org.apache.poi.poifs.common.POIFSBigBlockSize;
import org.apache.poi.poifs.nio.DataSource;
import org.apache.poi.poifs.storage.HeaderBlock;
import org.apache.poi.poifs.storage.BATBlock;
import java.util.List;
import org.apache.poi.poifs.property.PropertyTable;
import org.apache.poi.util.POILogger;
import java.io.Closeable;
import org.apache.poi.poifs.dev.POIFSViewable;

public class POIFSFileSystem extends BlockStore implements POIFSViewable, Closeable
{
    private static final int MAX_RECORD_LENGTH = 100000;
    private static final POILogger LOG;
    private static final int MAX_BLOCK_COUNT = 65535;
    private POIFSMiniStore _mini_store;
    private PropertyTable _property_table;
    private List<BATBlock> _xbat_blocks;
    private List<BATBlock> _bat_blocks;
    private HeaderBlock _header;
    private DirectoryNode _root;
    protected DataSource _data;
    private POIFSBigBlockSize bigBlockSize;
    
    private POIFSFileSystem(final boolean newFS) {
        this.bigBlockSize = POIFSConstants.SMALLER_BIG_BLOCK_SIZE_DETAILS;
        this._header = new HeaderBlock(this.bigBlockSize);
        this._property_table = new PropertyTable(this._header);
        this._mini_store = new POIFSMiniStore(this, this._property_table.getRoot(), new ArrayList<BATBlock>(), this._header);
        this._xbat_blocks = new ArrayList<BATBlock>();
        this._bat_blocks = new ArrayList<BATBlock>();
        this._root = null;
        if (newFS) {
            this.createNewDataSource();
        }
    }
    
    protected void createNewDataSource() {
        final long blockSize = ArithmeticUtils.mulAndCheck((long)this.bigBlockSize.getBigBlockSize(), 3L);
        this._data = new ByteArrayBackedDataSource(IOUtils.safelyAllocate(blockSize, 100000));
    }
    
    public POIFSFileSystem() {
        this(true);
        this._header.setBATCount(1);
        this._header.setBATArray(new int[] { 1 });
        final BATBlock bb = BATBlock.createEmptyBATBlock(this.bigBlockSize, false);
        bb.setOurBlockIndex(1);
        this._bat_blocks.add(bb);
        this.setNextBlock(0, -2);
        this.setNextBlock(1, -3);
        this._property_table.setStartBlock(0);
    }
    
    public POIFSFileSystem(final File file) throws IOException {
        this(file, true);
    }
    
    public POIFSFileSystem(final File file, final boolean readOnly) throws IOException {
        this(null, file, readOnly, true);
    }
    
    public POIFSFileSystem(final FileChannel channel) throws IOException {
        this(channel, true);
    }
    
    public POIFSFileSystem(final FileChannel channel, final boolean readOnly) throws IOException {
        this(channel, null, readOnly, false);
    }
    
    private POIFSFileSystem(FileChannel channel, final File srcFile, final boolean readOnly, final boolean closeChannelOnError) throws IOException {
        this(false);
        try {
            if (srcFile != null) {
                if (srcFile.length() == 0L) {
                    throw new EmptyFileException();
                }
                final FileBackedDataSource d = new FileBackedDataSource(srcFile, readOnly);
                channel = d.getChannel();
                this._data = d;
            }
            else {
                this._data = new FileBackedDataSource(channel, readOnly);
            }
            final ByteBuffer headerBuffer = ByteBuffer.allocate(512);
            IOUtils.readFully(channel, headerBuffer);
            this._header = new HeaderBlock(headerBuffer);
            this.readCoreContents();
        }
        catch (final IOException | RuntimeException e) {
            if (closeChannelOnError && channel != null) {
                channel.close();
            }
            throw e;
        }
    }
    
    public POIFSFileSystem(final InputStream stream) throws IOException {
        this(false);
        boolean success = false;
        try (final ReadableByteChannel channel = Channels.newChannel(stream)) {
            final ByteBuffer headerBuffer = ByteBuffer.allocate(512);
            IOUtils.readFully(channel, headerBuffer);
            this._header = new HeaderBlock(headerBuffer);
            sanityCheckBlockCount(this._header.getBATCount());
            final long maxSize = BATBlock.calculateMaximumSize(this._header);
            if (maxSize > 2147483647L) {
                throw new IllegalArgumentException("Unable read a >2gb file via an InputStream");
            }
            final ByteBuffer data = ByteBuffer.allocate((int)maxSize);
            headerBuffer.position(0);
            data.put(headerBuffer);
            data.position(headerBuffer.capacity());
            IOUtils.readFully(channel, data);
            success = true;
            this._data = new ByteArrayBackedDataSource(data.array(), data.position());
        }
        finally {
            this.closeInputStream(stream, success);
        }
        this.readCoreContents();
    }
    
    private void closeInputStream(final InputStream stream, final boolean success) {
        try {
            stream.close();
        }
        catch (final IOException e) {
            if (success) {
                throw new RuntimeException(e);
            }
            POIFSFileSystem.LOG.log(7, "can't close input stream", e);
        }
    }
    
    private void readCoreContents() throws IOException {
        this.bigBlockSize = this._header.getBigBlockSize();
        final ChainLoopDetector loopDetector = this.getChainLoopDetector();
        for (final int fatAt : this._header.getBATArray()) {
            this.readBAT(fatAt, loopDetector);
        }
        int remainingFATs = this._header.getBATCount() - this._header.getBATArray().length;
        int nextAt = this._header.getXBATIndex();
        for (int i = 0; i < this._header.getXBATCount(); ++i) {
            loopDetector.claim(nextAt);
            final ByteBuffer fatData = this.getBlockAt(nextAt);
            final BATBlock xfat = BATBlock.createBATBlock(this.bigBlockSize, fatData);
            xfat.setOurBlockIndex(nextAt);
            nextAt = xfat.getValueAt(this.bigBlockSize.getXBATEntriesPerBlock());
            this._xbat_blocks.add(xfat);
            final int xbatFATs = Math.min(remainingFATs, this.bigBlockSize.getXBATEntriesPerBlock());
            for (int j = 0; j < xbatFATs; ++j) {
                final int fatAt2 = xfat.getValueAt(j);
                if (fatAt2 == -1) {
                    break;
                }
                if (fatAt2 == -2) {
                    break;
                }
                this.readBAT(fatAt2, loopDetector);
            }
            remainingFATs -= xbatFATs;
        }
        this._property_table = new PropertyTable(this._header, this);
        final List<BATBlock> sbats = new ArrayList<BATBlock>();
        this._mini_store = new POIFSMiniStore(this, this._property_table.getRoot(), sbats, this._header);
        nextAt = this._header.getSBATStart();
        for (int k = 0; k < this._header.getSBATCount() && nextAt != -2; nextAt = this.getNextBlock(nextAt), ++k) {
            loopDetector.claim(nextAt);
            final ByteBuffer fatData2 = this.getBlockAt(nextAt);
            final BATBlock sfat = BATBlock.createBATBlock(this.bigBlockSize, fatData2);
            sfat.setOurBlockIndex(nextAt);
            sbats.add(sfat);
        }
    }
    
    private void readBAT(final int batAt, final ChainLoopDetector loopDetector) throws IOException {
        loopDetector.claim(batAt);
        final ByteBuffer fatData = this.getBlockAt(batAt);
        final BATBlock bat = BATBlock.createBATBlock(this.bigBlockSize, fatData);
        bat.setOurBlockIndex(batAt);
        this._bat_blocks.add(bat);
    }
    
    private BATBlock createBAT(final int offset, final boolean isBAT) throws IOException {
        final BATBlock newBAT = BATBlock.createEmptyBATBlock(this.bigBlockSize, !isBAT);
        newBAT.setOurBlockIndex(offset);
        final ByteBuffer buffer = ByteBuffer.allocate(this.bigBlockSize.getBigBlockSize());
        final long writeTo = ArithmeticUtils.mulAndCheck(1L + offset, (long)this.bigBlockSize.getBigBlockSize());
        this._data.write(buffer, writeTo);
        return newBAT;
    }
    
    @Override
    protected ByteBuffer getBlockAt(final int offset) throws IOException {
        final long blockWanted = offset + 1L;
        final long startAt = blockWanted * this.bigBlockSize.getBigBlockSize();
        try {
            return this._data.read(this.bigBlockSize.getBigBlockSize(), startAt);
        }
        catch (final IndexOutOfBoundsException e) {
            final IndexOutOfBoundsException wrapped = new IndexOutOfBoundsException("Block " + offset + " not found");
            wrapped.initCause(e);
            throw wrapped;
        }
    }
    
    @Override
    protected ByteBuffer createBlockIfNeeded(final int offset) throws IOException {
        try {
            return this.getBlockAt(offset);
        }
        catch (final IndexOutOfBoundsException e) {
            final long startAt = (offset + 1L) * this.bigBlockSize.getBigBlockSize();
            final ByteBuffer buffer = ByteBuffer.allocate(this.getBigBlockSize());
            this._data.write(buffer, startAt);
            return this.getBlockAt(offset);
        }
    }
    
    @Override
    protected BATBlock.BATBlockAndIndex getBATBlockAndIndex(final int offset) {
        return BATBlock.getBATBlockAndIndex(offset, this._header, this._bat_blocks);
    }
    
    @Override
    protected int getNextBlock(final int offset) {
        final BATBlock.BATBlockAndIndex bai = this.getBATBlockAndIndex(offset);
        return bai.getBlock().getValueAt(bai.getIndex());
    }
    
    @Override
    protected void setNextBlock(final int offset, final int nextBlock) {
        final BATBlock.BATBlockAndIndex bai = this.getBATBlockAndIndex(offset);
        bai.getBlock().setValueAt(bai.getIndex(), nextBlock);
    }
    
    @Override
    protected int getFreeBlock() throws IOException {
        final int numSectors = this.bigBlockSize.getBATEntriesPerBlock();
        int offset = 0;
        for (final BATBlock bat : this._bat_blocks) {
            if (bat.hasFreeSectors()) {
                for (int j = 0; j < numSectors; ++j) {
                    final int batValue = bat.getValueAt(j);
                    if (batValue == -1) {
                        return offset + j;
                    }
                }
            }
            offset += numSectors;
        }
        final BATBlock bat2 = this.createBAT(offset, true);
        bat2.setValueAt(0, -3);
        this._bat_blocks.add(bat2);
        if (this._header.getBATCount() >= 109) {
            BATBlock xbat = null;
            for (final BATBlock x : this._xbat_blocks) {
                if (x.hasFreeSectors()) {
                    xbat = x;
                    break;
                }
            }
            if (xbat == null) {
                xbat = this.createBAT(offset + 1, false);
                xbat.setValueAt(0, offset);
                bat2.setValueAt(1, -4);
                ++offset;
                if (this._xbat_blocks.size() == 0) {
                    this._header.setXBATStart(offset);
                }
                else {
                    this._xbat_blocks.get(this._xbat_blocks.size() - 1).setValueAt(this.bigBlockSize.getXBATEntriesPerBlock(), offset);
                }
                this._xbat_blocks.add(xbat);
                this._header.setXBATCount(this._xbat_blocks.size());
            }
            else {
                for (int i = 0; i < this.bigBlockSize.getXBATEntriesPerBlock(); ++i) {
                    if (xbat.getValueAt(i) == -1) {
                        xbat.setValueAt(i, offset);
                        break;
                    }
                }
            }
        }
        else {
            final int[] newBATs = new int[this._header.getBATCount() + 1];
            System.arraycopy(this._header.getBATArray(), 0, newBATs, 0, newBATs.length - 1);
            newBATs[newBATs.length - 1] = offset;
            this._header.setBATArray(newBATs);
        }
        this._header.setBATCount(this._bat_blocks.size());
        return offset + 1;
    }
    
    protected long size() throws IOException {
        return this._data.size();
    }
    
    @Override
    protected ChainLoopDetector getChainLoopDetector() throws IOException {
        return new ChainLoopDetector(this._data.size());
    }
    
    PropertyTable _get_property_table() {
        return this._property_table;
    }
    
    POIFSMiniStore getMiniStore() {
        return this._mini_store;
    }
    
    void addDocument(final POIFSDocument document) {
        this._property_table.addProperty(document.getDocumentProperty());
    }
    
    void addDirectory(final DirectoryProperty directory) {
        this._property_table.addProperty(directory);
    }
    
    public DocumentEntry createDocument(final InputStream stream, final String name) throws IOException {
        return this.getRoot().createDocument(name, stream);
    }
    
    public DocumentEntry createDocument(final String name, final int size, final POIFSWriterListener writer) throws IOException {
        return this.getRoot().createDocument(name, size, writer);
    }
    
    public DirectoryEntry createDirectory(final String name) throws IOException {
        return this.getRoot().createDirectory(name);
    }
    
    public DocumentEntry createOrUpdateDocument(final InputStream stream, final String name) throws IOException {
        return this.getRoot().createOrUpdateDocument(name, stream);
    }
    
    public boolean isInPlaceWriteable() {
        return this._data instanceof FileBackedDataSource && ((FileBackedDataSource)this._data).isWriteable();
    }
    
    public void writeFilesystem() throws IOException {
        if (!(this._data instanceof FileBackedDataSource)) {
            throw new IllegalArgumentException("POIFS opened from an inputstream, so writeFilesystem() may not be called. Use writeFilesystem(OutputStream) instead");
        }
        if (!((FileBackedDataSource)this._data).isWriteable()) {
            throw new IllegalArgumentException("POIFS opened in read only mode, so writeFilesystem() may not be called. Open the FileSystem in read-write mode first");
        }
        this.syncWithDataSource();
    }
    
    public void writeFilesystem(final OutputStream stream) throws IOException {
        this.syncWithDataSource();
        this._data.copyTo(stream);
    }
    
    private void syncWithDataSource() throws IOException {
        this._mini_store.syncWithDataSource();
        final POIFSStream propStream = new POIFSStream(this, this._header.getPropertyStart());
        this._property_table.preWrite();
        this._property_table.write(propStream);
        final ByteArrayOutputStream baos = new ByteArrayOutputStream(this._header.getBigBlockSize().getBigBlockSize());
        this._header.writeData(baos);
        this.getBlockAt(-1).put(baos.toByteArray());
        for (final BATBlock bat : this._bat_blocks) {
            final ByteBuffer block = this.getBlockAt(bat.getOurBlockIndex());
            bat.writeData(block);
        }
        for (final BATBlock bat : this._xbat_blocks) {
            final ByteBuffer block = this.getBlockAt(bat.getOurBlockIndex());
            bat.writeData(block);
        }
    }
    
    @Override
    public void close() throws IOException {
        this._data.close();
    }
    
    public static void main(final String[] args) throws IOException {
        if (args.length != 2) {
            System.err.println("two arguments required: input filename and output filename");
            System.exit(1);
        }
        try (final FileInputStream istream = new FileInputStream(args[0]);
             final FileOutputStream ostream = new FileOutputStream(args[1]);
             final POIFSFileSystem fs = new POIFSFileSystem(istream)) {
            fs.writeFilesystem(ostream);
        }
    }
    
    public DirectoryNode getRoot() {
        if (this._root == null) {
            this._root = new DirectoryNode(this._property_table.getRoot(), this, null);
        }
        return this._root;
    }
    
    public DocumentInputStream createDocumentInputStream(final String documentName) throws IOException {
        return this.getRoot().createDocumentInputStream(documentName);
    }
    
    void remove(final EntryNode entry) throws IOException {
        if (entry instanceof DocumentEntry) {
            final POIFSDocument doc = new POIFSDocument((DocumentProperty)entry.getProperty(), this);
            doc.free();
        }
        this._property_table.removeProperty(entry.getProperty());
    }
    
    @Override
    public Object[] getViewableArray() {
        if (this.preferArray()) {
            return this.getRoot().getViewableArray();
        }
        return new Object[0];
    }
    
    @Override
    public Iterator<Object> getViewableIterator() {
        if (!this.preferArray()) {
            return this.getRoot().getViewableIterator();
        }
        return Collections.emptyList().iterator();
    }
    
    @Override
    public boolean preferArray() {
        return this.getRoot().preferArray();
    }
    
    @Override
    public String getShortDescription() {
        return "POIFS FileSystem";
    }
    
    public int getBigBlockSize() {
        return this.bigBlockSize.getBigBlockSize();
    }
    
    public POIFSBigBlockSize getBigBlockSizeDetails() {
        return this.bigBlockSize;
    }
    
    public static POIFSFileSystem create(final File file) throws IOException {
        try (final POIFSFileSystem tmp = new POIFSFileSystem();
             final OutputStream out = new FileOutputStream(file)) {
            tmp.writeFilesystem(out);
        }
        return new POIFSFileSystem(file, false);
    }
    
    @Override
    protected int getBlockStoreBlockSize() {
        return this.getBigBlockSize();
    }
    
    @Internal
    public PropertyTable getPropertyTable() {
        return this._property_table;
    }
    
    @Internal
    public HeaderBlock getHeaderBlock() {
        return this._header;
    }
    
    private static void sanityCheckBlockCount(final int block_count) throws IOException {
        if (block_count <= 0) {
            throw new IOException("Illegal block count; minimum count is 1, got " + block_count + " instead");
        }
        if (block_count > 65535) {
            throw new IOException("Block count " + block_count + " is too high. POI maximum is " + 65535 + ".");
        }
    }
    
    static {
        LOG = POILogFactory.getLogger(POIFSFileSystem.class);
    }
}
