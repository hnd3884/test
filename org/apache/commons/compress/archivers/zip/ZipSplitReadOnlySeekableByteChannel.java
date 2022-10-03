package org.apache.commons.compress.archivers.zip;

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.StandardOpenOption;
import java.nio.file.OpenOption;
import java.util.Comparator;
import java.util.regex.Pattern;
import org.apache.commons.compress.utils.FileNameUtils;
import java.io.File;
import java.util.Iterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Objects;
import java.io.IOException;
import java.nio.channels.SeekableByteChannel;
import java.util.List;
import java.nio.ByteBuffer;
import org.apache.commons.compress.utils.MultiReadOnlySeekableByteChannel;

public class ZipSplitReadOnlySeekableByteChannel extends MultiReadOnlySeekableByteChannel
{
    private static final int ZIP_SPLIT_SIGNATURE_LENGTH = 4;
    private final ByteBuffer zipSplitSignatureByteBuffer;
    
    public ZipSplitReadOnlySeekableByteChannel(final List<SeekableByteChannel> channels) throws IOException {
        super(channels);
        this.zipSplitSignatureByteBuffer = ByteBuffer.allocate(4);
        this.assertSplitSignature(channels);
    }
    
    private void assertSplitSignature(final List<SeekableByteChannel> channels) throws IOException {
        final SeekableByteChannel channel = channels.get(0);
        channel.position(0L);
        this.zipSplitSignatureByteBuffer.rewind();
        channel.read(this.zipSplitSignatureByteBuffer);
        final ZipLong signature = new ZipLong(this.zipSplitSignatureByteBuffer.array());
        if (!signature.equals(ZipLong.DD_SIG)) {
            channel.position(0L);
            throw new IOException("The first zip split segment does not begin with split zip file signature");
        }
        channel.position(0L);
    }
    
    public static SeekableByteChannel forOrderedSeekableByteChannels(final SeekableByteChannel... channels) throws IOException {
        if (Objects.requireNonNull(channels, "channels must not be null").length == 1) {
            return channels[0];
        }
        return new ZipSplitReadOnlySeekableByteChannel(Arrays.asList(channels));
    }
    
    public static SeekableByteChannel forOrderedSeekableByteChannels(final SeekableByteChannel lastSegmentChannel, final Iterable<SeekableByteChannel> channels) throws IOException {
        Objects.requireNonNull(channels, "channels");
        Objects.requireNonNull(lastSegmentChannel, "lastSegmentChannel");
        final List<SeekableByteChannel> channelsList = new ArrayList<SeekableByteChannel>();
        for (final SeekableByteChannel channel : channels) {
            channelsList.add(channel);
        }
        channelsList.add(lastSegmentChannel);
        return forOrderedSeekableByteChannels((SeekableByteChannel[])channelsList.toArray(new SeekableByteChannel[0]));
    }
    
    public static SeekableByteChannel buildFromLastSplitSegment(final File lastSegmentFile) throws IOException {
        final String extension = FileNameUtils.getExtension(lastSegmentFile.getCanonicalPath());
        if (!extension.equalsIgnoreCase("zip")) {
            throw new IllegalArgumentException("The extension of last zip split segment should be .zip");
        }
        final File parent = lastSegmentFile.getParentFile();
        final String fileBaseName = FileNameUtils.getBaseName(lastSegmentFile.getCanonicalPath());
        final ArrayList<File> splitZipSegments = new ArrayList<File>();
        final Pattern pattern = Pattern.compile(Pattern.quote(fileBaseName) + ".[zZ][0-9]+");
        final File[] children = parent.listFiles();
        if (children != null) {
            for (final File file : children) {
                if (pattern.matcher(file.getName()).matches()) {
                    splitZipSegments.add(file);
                }
            }
        }
        splitZipSegments.sort(new ZipSplitSegmentComparator());
        return forFiles(lastSegmentFile, splitZipSegments);
    }
    
    public static SeekableByteChannel forFiles(final File... files) throws IOException {
        final List<SeekableByteChannel> channels = new ArrayList<SeekableByteChannel>();
        for (final File f : Objects.requireNonNull(files, "files must not be null")) {
            channels.add(Files.newByteChannel(f.toPath(), StandardOpenOption.READ));
        }
        if (channels.size() == 1) {
            return channels.get(0);
        }
        return new ZipSplitReadOnlySeekableByteChannel(channels);
    }
    
    public static SeekableByteChannel forFiles(final File lastSegmentFile, final Iterable<File> files) throws IOException {
        Objects.requireNonNull(files, "files");
        Objects.requireNonNull(lastSegmentFile, "lastSegmentFile");
        final List<File> filesList = new ArrayList<File>();
        for (final File f : files) {
            filesList.add(f);
        }
        filesList.add(lastSegmentFile);
        return forFiles((File[])filesList.toArray(new File[0]));
    }
    
    private static class ZipSplitSegmentComparator implements Comparator<File>, Serializable
    {
        private static final long serialVersionUID = 20200123L;
        
        @Override
        public int compare(final File file1, final File file2) {
            final String extension1 = FileNameUtils.getExtension(file1.getPath());
            final String extension2 = FileNameUtils.getExtension(file2.getPath());
            if (!extension1.startsWith("z")) {
                return -1;
            }
            if (!extension2.startsWith("z")) {
                return 1;
            }
            final Integer splitSegmentNumber1 = Integer.parseInt(extension1.substring(1));
            final Integer splitSegmentNumber2 = Integer.parseInt(extension2.substring(1));
            return splitSegmentNumber1.compareTo(splitSegmentNumber2);
        }
    }
}
