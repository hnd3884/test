package org.apache.commons.compress.utils;

import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.io.File;
import java.util.Arrays;
import java.nio.channels.NonWritableChannelException;
import java.util.Iterator;
import java.io.IOException;
import java.nio.channels.ClosedChannelException;
import java.nio.ByteBuffer;
import java.util.Collections;
import java.util.ArrayList;
import java.util.Objects;
import java.util.Collection;
import java.util.List;
import java.nio.channels.SeekableByteChannel;

public class MultiReadOnlySeekableByteChannel implements SeekableByteChannel
{
    private final List<SeekableByteChannel> channels;
    private long globalPosition;
    private int currentChannelIdx;
    
    public MultiReadOnlySeekableByteChannel(final List<SeekableByteChannel> channels) {
        this.channels = Collections.unmodifiableList((List<? extends SeekableByteChannel>)new ArrayList<SeekableByteChannel>(Objects.requireNonNull(channels, "channels must not be null")));
    }
    
    @Override
    public synchronized int read(final ByteBuffer dst) throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        if (!dst.hasRemaining()) {
            return 0;
        }
        int totalBytesRead = 0;
        while (dst.hasRemaining() && this.currentChannelIdx < this.channels.size()) {
            final SeekableByteChannel currentChannel = this.channels.get(this.currentChannelIdx);
            final int newBytesRead = currentChannel.read(dst);
            if (newBytesRead == -1) {
                ++this.currentChannelIdx;
            }
            else {
                if (currentChannel.position() >= currentChannel.size()) {
                    ++this.currentChannelIdx;
                }
                totalBytesRead += newBytesRead;
            }
        }
        if (totalBytesRead > 0) {
            this.globalPosition += totalBytesRead;
            return totalBytesRead;
        }
        return -1;
    }
    
    @Override
    public void close() throws IOException {
        IOException first = null;
        for (final SeekableByteChannel ch : this.channels) {
            try {
                ch.close();
            }
            catch (final IOException ex) {
                if (first != null) {
                    continue;
                }
                first = ex;
            }
        }
        if (first != null) {
            throw new IOException("failed to close wrapped channel", first);
        }
    }
    
    @Override
    public boolean isOpen() {
        for (final SeekableByteChannel ch : this.channels) {
            if (!ch.isOpen()) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public long position() {
        return this.globalPosition;
    }
    
    public synchronized SeekableByteChannel position(final long channelNumber, final long relativeOffset) throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        long globalPosition = relativeOffset;
        for (int i = 0; i < channelNumber; ++i) {
            globalPosition += this.channels.get(i).size();
        }
        return this.position(globalPosition);
    }
    
    @Override
    public long size() throws IOException {
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        long acc = 0L;
        for (final SeekableByteChannel ch : this.channels) {
            acc += ch.size();
        }
        return acc;
    }
    
    @Override
    public SeekableByteChannel truncate(final long size) {
        throw new NonWritableChannelException();
    }
    
    @Override
    public int write(final ByteBuffer src) {
        throw new NonWritableChannelException();
    }
    
    @Override
    public synchronized SeekableByteChannel position(final long newPosition) throws IOException {
        if (newPosition < 0L) {
            throw new IOException("Negative position: " + newPosition);
        }
        if (!this.isOpen()) {
            throw new ClosedChannelException();
        }
        this.globalPosition = newPosition;
        long pos = newPosition;
        for (int i = 0; i < this.channels.size(); ++i) {
            final SeekableByteChannel currentChannel = this.channels.get(i);
            final long size = currentChannel.size();
            long newChannelPos;
            if (pos == -1L) {
                newChannelPos = 0L;
            }
            else if (pos <= size) {
                this.currentChannelIdx = i;
                final long tmp = pos;
                pos = -1L;
                newChannelPos = tmp;
            }
            else {
                pos -= size;
                newChannelPos = size;
            }
            currentChannel.position(newChannelPos);
        }
        return this;
    }
    
    public static SeekableByteChannel forSeekableByteChannels(final SeekableByteChannel... channels) {
        if (Objects.requireNonNull(channels, "channels must not be null").length == 1) {
            return channels[0];
        }
        return new MultiReadOnlySeekableByteChannel(Arrays.asList(channels));
    }
    
    public static SeekableByteChannel forFiles(final File... files) throws IOException {
        final List<SeekableByteChannel> channels = new ArrayList<SeekableByteChannel>();
        for (final File f : Objects.requireNonNull(files, "files must not be null")) {
            channels.add(Files.newByteChannel(f.toPath(), StandardOpenOption.READ));
        }
        if (channels.size() == 1) {
            return channels.get(0);
        }
        return new MultiReadOnlySeekableByteChannel(channels);
    }
}
