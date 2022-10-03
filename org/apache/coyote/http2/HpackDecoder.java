package org.apache.coyote.http2;

import org.apache.juli.logging.LogFactory;
import java.nio.ByteBuffer;
import org.apache.tomcat.util.res.StringManager;
import org.apache.juli.logging.Log;

public class HpackDecoder
{
    private static final Log log;
    private static final StringManager sm;
    private static final int DEFAULT_RING_BUFFER_SIZE = 10;
    private HeaderEmitter headerEmitter;
    private Hpack.HeaderField[] headerTable;
    private int firstSlotPosition;
    private int filledTableSlots;
    private int currentMemorySize;
    private int maxMemorySizeHard;
    private int maxMemorySizeSoft;
    private int maxHeaderCount;
    private int maxHeaderSize;
    private volatile int headerCount;
    private volatile boolean countedCookie;
    private volatile int headerSize;
    
    HpackDecoder(final int maxMemorySize) {
        this.firstSlotPosition = 0;
        this.filledTableSlots = 0;
        this.currentMemorySize = 0;
        this.maxHeaderCount = 100;
        this.maxHeaderSize = 8192;
        this.headerCount = 0;
        this.headerSize = 0;
        this.maxMemorySizeHard = maxMemorySize;
        this.maxMemorySizeSoft = maxMemorySize;
        this.headerTable = new Hpack.HeaderField[10];
    }
    
    HpackDecoder() {
        this(4096);
    }
    
    void decode(final ByteBuffer buffer) throws HpackException {
        while (buffer.hasRemaining()) {
            final int originalPos = buffer.position();
            final byte b = buffer.get();
            if ((b & 0x80) != 0x0) {
                buffer.position(buffer.position() - 1);
                final int index = Hpack.decodeInteger(buffer, 7);
                if (index == -1) {
                    buffer.position(originalPos);
                    return;
                }
                if (index == 0) {
                    throw new HpackException(HpackDecoder.sm.getString("hpackdecoder.zeroNotValidHeaderTableIndex"));
                }
                this.handleIndex(index);
            }
            else if ((b & 0x40) != 0x0) {
                final String headerName = this.readHeaderName(buffer, 6);
                if (headerName == null) {
                    buffer.position(originalPos);
                    return;
                }
                final String headerValue = this.readHpackString(buffer);
                if (headerValue == null) {
                    buffer.position(originalPos);
                    return;
                }
                this.emitHeader(headerName, headerValue);
                this.addEntryToHeaderTable(new Hpack.HeaderField(headerName, headerValue));
            }
            else if ((b & 0xF0) == 0x0) {
                final String headerName = this.readHeaderName(buffer, 4);
                if (headerName == null) {
                    buffer.position(originalPos);
                    return;
                }
                final String headerValue = this.readHpackString(buffer);
                if (headerValue == null) {
                    buffer.position(originalPos);
                    return;
                }
                this.emitHeader(headerName, headerValue);
            }
            else if ((b & 0xF0) == 0x10) {
                final String headerName = this.readHeaderName(buffer, 4);
                if (headerName == null) {
                    buffer.position(originalPos);
                    return;
                }
                final String headerValue = this.readHpackString(buffer);
                if (headerValue == null) {
                    buffer.position(originalPos);
                    return;
                }
                this.emitHeader(headerName, headerValue);
            }
            else {
                if ((b & 0xE0) != 0x20) {
                    throw new RuntimeException(HpackDecoder.sm.getString("hpackdecoder.notImplemented"));
                }
                if (!this.handleMaxMemorySizeChange(buffer, originalPos)) {
                    return;
                }
                continue;
            }
        }
    }
    
    private boolean handleMaxMemorySizeChange(final ByteBuffer buffer, final int originalPos) throws HpackException {
        if (this.headerCount != 0) {
            throw new HpackException(HpackDecoder.sm.getString("hpackdecoder.tableSizeUpdateNotAtStart"));
        }
        buffer.position(buffer.position() - 1);
        final int size = Hpack.decodeInteger(buffer, 5);
        if (size == -1) {
            buffer.position(originalPos);
            return false;
        }
        if (size > this.maxMemorySizeHard) {
            throw new HpackException(HpackDecoder.sm.getString("hpackdecoder.maxMemorySizeExceeded", new Object[] { size, this.maxMemorySizeHard }));
        }
        this.maxMemorySizeSoft = size;
        if (this.currentMemorySize > this.maxMemorySizeSoft) {
            int newTableSlots = this.filledTableSlots;
            final int tableLength = this.headerTable.length;
            int newSize;
            Hpack.HeaderField oldData;
            for (newSize = this.currentMemorySize; newSize > this.maxMemorySizeSoft; newSize -= oldData.size, --newTableSlots) {
                final int clearIndex = this.firstSlotPosition;
                ++this.firstSlotPosition;
                if (this.firstSlotPosition == tableLength) {
                    this.firstSlotPosition = 0;
                }
                oldData = this.headerTable[clearIndex];
                this.headerTable[clearIndex] = null;
            }
            this.filledTableSlots = newTableSlots;
            this.currentMemorySize = newSize;
        }
        return true;
    }
    
    private String readHeaderName(final ByteBuffer buffer, final int prefixLength) throws HpackException {
        buffer.position(buffer.position() - 1);
        final int index = Hpack.decodeInteger(buffer, prefixLength);
        if (index == -1) {
            return null;
        }
        if (index != 0) {
            return this.handleIndexedHeaderName(index);
        }
        return this.readHpackString(buffer);
    }
    
    private String readHpackString(final ByteBuffer buffer) throws HpackException {
        if (!buffer.hasRemaining()) {
            return null;
        }
        final byte data = buffer.get(buffer.position());
        final int length = Hpack.decodeInteger(buffer, 7);
        if (buffer.remaining() < length || length == -1) {
            return null;
        }
        final boolean huffman = (data & 0x80) != 0x0;
        if (huffman) {
            return this.readHuffmanString(length, buffer);
        }
        final StringBuilder stringBuilder = new StringBuilder(length);
        for (int i = 0; i < length; ++i) {
            stringBuilder.append((char)buffer.get());
        }
        return stringBuilder.toString();
    }
    
    private String readHuffmanString(final int length, final ByteBuffer buffer) throws HpackException {
        final StringBuilder stringBuilder = new StringBuilder(length);
        HPackHuffman.decode(buffer, length, stringBuilder);
        return stringBuilder.toString();
    }
    
    private String handleIndexedHeaderName(final int index) throws HpackException {
        if (index <= Hpack.STATIC_TABLE_LENGTH) {
            return Hpack.STATIC_TABLE[index].name;
        }
        if (index > Hpack.STATIC_TABLE_LENGTH + this.filledTableSlots) {
            throw new HpackException(HpackDecoder.sm.getString("hpackdecoder.headerTableIndexInvalid", new Object[] { index, Hpack.STATIC_TABLE_LENGTH, this.filledTableSlots }));
        }
        final int adjustedIndex = this.getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
        final Hpack.HeaderField res = this.headerTable[adjustedIndex];
        if (res == null) {
            throw new HpackException(HpackDecoder.sm.getString("hpackdecoder.nullHeader", new Object[] { index }));
        }
        return res.name;
    }
    
    private void handleIndex(final int index) throws HpackException {
        if (index <= Hpack.STATIC_TABLE_LENGTH) {
            this.addStaticTableEntry(index);
        }
        else {
            final int adjustedIndex = this.getRealIndex(index - Hpack.STATIC_TABLE_LENGTH);
            if (HpackDecoder.log.isDebugEnabled()) {
                HpackDecoder.log.debug((Object)HpackDecoder.sm.getString("hpackdecoder.useDynamic", new Object[] { adjustedIndex }));
            }
            final Hpack.HeaderField headerField = this.headerTable[adjustedIndex];
            this.emitHeader(headerField.name, headerField.value);
        }
    }
    
    int getRealIndex(final int index) throws HpackException {
        final int realIndex = (this.firstSlotPosition + (this.filledTableSlots - index)) % this.headerTable.length;
        if (realIndex < 0) {
            throw new HpackException(HpackDecoder.sm.getString("hpackdecoder.headerTableIndexInvalid", new Object[] { index, Hpack.STATIC_TABLE_LENGTH, this.filledTableSlots }));
        }
        return realIndex;
    }
    
    private void addStaticTableEntry(final int index) throws HpackException {
        if (HpackDecoder.log.isDebugEnabled()) {
            HpackDecoder.log.debug((Object)HpackDecoder.sm.getString("hpackdecoder.useStatic", new Object[] { index }));
        }
        final Hpack.HeaderField entry = Hpack.STATIC_TABLE[index];
        this.emitHeader(entry.name, (entry.value == null) ? "" : entry.value);
    }
    
    private void addEntryToHeaderTable(final Hpack.HeaderField entry) {
        if (entry.size > this.maxMemorySizeSoft) {
            if (HpackDecoder.log.isDebugEnabled()) {
                HpackDecoder.log.debug((Object)HpackDecoder.sm.getString("hpackdecoder.clearDynamic"));
            }
            while (this.filledTableSlots > 0) {
                this.headerTable[this.firstSlotPosition] = null;
                ++this.firstSlotPosition;
                if (this.firstSlotPosition == this.headerTable.length) {
                    this.firstSlotPosition = 0;
                }
                --this.filledTableSlots;
            }
            this.currentMemorySize = 0;
            return;
        }
        this.resizeIfRequired();
        int newTableSlots = this.filledTableSlots + 1;
        final int tableLength = this.headerTable.length;
        final int index = (this.firstSlotPosition + this.filledTableSlots) % tableLength;
        if (HpackDecoder.log.isDebugEnabled()) {
            HpackDecoder.log.debug((Object)HpackDecoder.sm.getString("hpackdecoder.addDynamic", new Object[] { index, entry.name, entry.value }));
        }
        this.headerTable[index] = entry;
        int newSize;
        Hpack.HeaderField oldData;
        for (newSize = this.currentMemorySize + entry.size; newSize > this.maxMemorySizeSoft; newSize -= oldData.size, --newTableSlots) {
            final int clearIndex = this.firstSlotPosition;
            ++this.firstSlotPosition;
            if (this.firstSlotPosition == tableLength) {
                this.firstSlotPosition = 0;
            }
            oldData = this.headerTable[clearIndex];
            this.headerTable[clearIndex] = null;
        }
        this.filledTableSlots = newTableSlots;
        this.currentMemorySize = newSize;
    }
    
    private void resizeIfRequired() {
        if (this.filledTableSlots == this.headerTable.length) {
            final Hpack.HeaderField[] newArray = new Hpack.HeaderField[this.headerTable.length + 10];
            for (int i = 0; i < this.headerTable.length; ++i) {
                newArray[i] = this.headerTable[(this.firstSlotPosition + i) % this.headerTable.length];
            }
            this.firstSlotPosition = 0;
            this.headerTable = newArray;
        }
    }
    
    HeaderEmitter getHeaderEmitter() {
        return this.headerEmitter;
    }
    
    void setHeaderEmitter(final HeaderEmitter headerEmitter) {
        this.headerEmitter = headerEmitter;
        this.headerCount = 0;
        this.countedCookie = false;
        this.headerSize = 0;
    }
    
    void setMaxHeaderCount(final int maxHeaderCount) {
        this.maxHeaderCount = maxHeaderCount;
    }
    
    void setMaxHeaderSize(final int maxHeaderSize) {
        this.maxHeaderSize = maxHeaderSize;
    }
    
    private void emitHeader(final String name, final String value) throws HpackException {
        if ("cookie".equals(name)) {
            if (!this.countedCookie) {
                ++this.headerCount;
                this.countedCookie = true;
            }
        }
        else {
            ++this.headerCount;
        }
        final int inc = 3 + name.length() + value.length();
        this.headerSize += inc;
        if (!this.isHeaderCountExceeded() && !this.isHeaderSizeExceeded(0)) {
            if (HpackDecoder.log.isDebugEnabled()) {
                HpackDecoder.log.debug((Object)HpackDecoder.sm.getString("hpackdecoder.emitHeader", new Object[] { name, value }));
            }
            this.headerEmitter.emitHeader(name, value);
        }
    }
    
    boolean isHeaderCountExceeded() {
        return this.maxHeaderCount >= 0 && this.headerCount > this.maxHeaderCount;
    }
    
    boolean isHeaderSizeExceeded(final int unreadSize) {
        return this.maxHeaderSize >= 0 && this.headerSize + unreadSize > this.maxHeaderSize;
    }
    
    boolean isHeaderSwallowSizeExceeded(final int unreadSize) {
        return this.maxHeaderSize >= 0 && this.headerSize + unreadSize > 2 * this.maxHeaderSize;
    }
    
    int getFirstSlotPosition() {
        return this.firstSlotPosition;
    }
    
    Hpack.HeaderField[] getHeaderTable() {
        return this.headerTable;
    }
    
    int getFilledTableSlots() {
        return this.filledTableSlots;
    }
    
    int getCurrentMemorySize() {
        return this.currentMemorySize;
    }
    
    int getMaxMemorySizeSoft() {
        return this.maxMemorySizeSoft;
    }
    
    static {
        log = LogFactory.getLog((Class)HpackDecoder.class);
        sm = StringManager.getManager((Class)HpackDecoder.class);
    }
    
    interface HeaderEmitter
    {
        void emitHeader(final String p0, final String p1) throws HpackException;
        
        void setHeaderException(final StreamException p0);
        
        void validateHeaders() throws StreamException;
    }
}
