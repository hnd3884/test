package com.sun.media.sound;

import javax.sound.midi.MidiEvent;
import java.io.ByteArrayInputStream;
import javax.sound.midi.MetaMessage;
import javax.sound.midi.SysexMessage;
import javax.sound.midi.ShortMessage;
import java.io.ByteArrayOutputStream;
import javax.sound.midi.Track;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.io.SequenceInputStream;
import javax.sound.midi.InvalidMidiDataException;
import java.io.FileOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.sound.midi.Sequence;
import java.io.DataOutputStream;
import javax.sound.midi.spi.MidiFileWriter;

public final class StandardMidiFileWriter extends MidiFileWriter
{
    private static final int MThd_MAGIC = 1297377380;
    private static final int MTrk_MAGIC = 1297379947;
    private static final int ONE_BYTE = 1;
    private static final int TWO_BYTE = 2;
    private static final int SYSEX = 3;
    private static final int META = 4;
    private static final int ERROR = 5;
    private static final int IGNORE = 6;
    private static final int MIDI_TYPE_0 = 0;
    private static final int MIDI_TYPE_1 = 1;
    private static final int bufferSize = 16384;
    private DataOutputStream tddos;
    private static final int[] types;
    private static final long mask = 127L;
    
    @Override
    public int[] getMidiFileTypes() {
        final int[] array = new int[StandardMidiFileWriter.types.length];
        System.arraycopy(StandardMidiFileWriter.types, 0, array, 0, StandardMidiFileWriter.types.length);
        return array;
    }
    
    @Override
    public int[] getMidiFileTypes(final Sequence sequence) {
        int[] array;
        if (sequence.getTracks().length == 1) {
            array = new int[] { 0, 1 };
        }
        else {
            array = new int[] { 1 };
        }
        return array;
    }
    
    @Override
    public boolean isFileTypeSupported(final int n) {
        for (int i = 0; i < StandardMidiFileWriter.types.length; ++i) {
            if (n == StandardMidiFileWriter.types[i]) {
                return true;
            }
        }
        return false;
    }
    
    @Override
    public int write(final Sequence sequence, final int n, final OutputStream outputStream) throws IOException {
        long n2 = 0L;
        if (!this.isFileTypeSupported(n, sequence)) {
            throw new IllegalArgumentException("Could not write MIDI file");
        }
        final InputStream fileStream = this.getFileStream(n, sequence);
        if (fileStream == null) {
            throw new IllegalArgumentException("Could not write MIDI file");
        }
        final byte[] array = new byte[16384];
        int read;
        while ((read = fileStream.read(array)) >= 0) {
            outputStream.write(array, 0, read);
            n2 += read;
        }
        return (int)n2;
    }
    
    @Override
    public int write(final Sequence sequence, final int n, final File file) throws IOException {
        final FileOutputStream fileOutputStream = new FileOutputStream(file);
        final int write = this.write(sequence, n, fileOutputStream);
        fileOutputStream.close();
        return write;
    }
    
    private InputStream getFileStream(int n, final Sequence sequence) throws IOException {
        final Track[] tracks = sequence.getTracks();
        final int n2 = 14;
        if (n == 0) {
            if (tracks.length != 1) {
                return null;
            }
        }
        else if (n == 1) {
            if (tracks.length < 1) {
                return null;
            }
        }
        else if (tracks.length == 1) {
            n = 0;
        }
        else {
            if (tracks.length <= 1) {
                return null;
            }
            n = 1;
        }
        final InputStream[] array = new InputStream[tracks.length];
        int n3 = 0;
        for (int i = 0; i < tracks.length; ++i) {
            try {
                array[n3] = this.writeTrack(tracks[i], n);
                ++n3;
            }
            catch (final InvalidMidiDataException ex) {}
        }
        InputStream inputStream;
        if (n3 == 1) {
            inputStream = array[0];
        }
        else {
            if (n3 <= 1) {
                throw new IllegalArgumentException("invalid MIDI data in sequence");
            }
            inputStream = array[0];
            for (int j = 1; j < tracks.length; ++j) {
                if (array[j] != null) {
                    inputStream = new SequenceInputStream(inputStream, array[j]);
                }
            }
        }
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(pipedOutputStream);
        final PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        dataOutputStream.writeInt(1297377380);
        dataOutputStream.writeInt(n2 - 8);
        if (n == 0) {
            dataOutputStream.writeShort(0);
        }
        else {
            dataOutputStream.writeShort(1);
        }
        dataOutputStream.writeShort((short)n3);
        final float divisionType = sequence.getDivisionType();
        int resolution;
        if (divisionType == 0.0f) {
            resolution = sequence.getResolution();
        }
        else if (divisionType == 24.0f) {
            resolution = -6144 + (sequence.getResolution() & 0xFF);
        }
        else if (divisionType == 25.0f) {
            resolution = -6400 + (sequence.getResolution() & 0xFF);
        }
        else if (divisionType == 29.97f) {
            resolution = -7424 + (sequence.getResolution() & 0xFF);
        }
        else {
            if (divisionType != 30.0f) {
                return null;
            }
            resolution = -7680 + (sequence.getResolution() & 0xFF);
        }
        dataOutputStream.writeShort(resolution);
        final SequenceInputStream sequenceInputStream = new SequenceInputStream(pipedInputStream, inputStream);
        dataOutputStream.close();
        return sequenceInputStream;
    }
    
    private int getType(final int n) {
        if ((n & 0xF0) == 0xF0) {
            switch (n) {
                case 240:
                case 247: {
                    return 3;
                }
                case 255: {
                    return 4;
                }
                default: {
                    return 6;
                }
            }
        }
        else {
            switch (n & 0xF0) {
                case 128:
                case 144:
                case 160:
                case 176:
                case 224: {
                    return 2;
                }
                case 192:
                case 208: {
                    return 1;
                }
                default: {
                    return 5;
                }
            }
        }
    }
    
    private int writeVarInt(final long n) throws IOException {
        int n2 = 1;
        int i;
        for (i = 63; i > 0 && (n & 127L << i) == 0x0L; i -= 7) {}
        while (i > 0) {
            this.tddos.writeByte((int)((n & 127L << i) >> i | 0x80L));
            i -= 7;
            ++n2;
        }
        this.tddos.writeByte((int)(n & 0x7FL));
        return n2;
    }
    
    private InputStream writeTrack(final Track track, final int n) throws IOException, InvalidMidiDataException {
        int n2 = 0;
        final int size = track.size();
        final PipedOutputStream pipedOutputStream = new PipedOutputStream();
        final DataOutputStream dataOutputStream = new DataOutputStream(pipedOutputStream);
        final PipedInputStream pipedInputStream = new PipedInputStream(pipedOutputStream);
        final ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        this.tddos = new DataOutputStream(byteArrayOutputStream);
        long tick = 0L;
        int n3 = -1;
        for (int i = 0; i < size; ++i) {
            final MidiEvent value = track.get(i);
            value.getTick();
            final long n4 = value.getTick() - tick;
            tick = value.getTick();
            final int status = value.getMessage().getStatus();
            switch (this.getType(status)) {
                case 1: {
                    final int data1 = ((ShortMessage)value.getMessage()).getData1();
                    n2 += this.writeVarInt(n4);
                    if (status != n3) {
                        n3 = status;
                        this.tddos.writeByte(status);
                        ++n2;
                    }
                    this.tddos.writeByte(data1);
                    ++n2;
                    break;
                }
                case 2: {
                    final ShortMessage shortMessage = (ShortMessage)value.getMessage();
                    final int data2 = shortMessage.getData1();
                    final int data3 = shortMessage.getData2();
                    n2 += this.writeVarInt(n4);
                    if (status != n3) {
                        n3 = status;
                        this.tddos.writeByte(status);
                        ++n2;
                    }
                    this.tddos.writeByte(data2);
                    ++n2;
                    this.tddos.writeByte(data3);
                    ++n2;
                    break;
                }
                case 3: {
                    final SysexMessage sysexMessage = (SysexMessage)value.getMessage();
                    sysexMessage.getLength();
                    final byte[] message = sysexMessage.getMessage();
                    int n5 = n2 + this.writeVarInt(n4);
                    n3 = status;
                    this.tddos.writeByte(message[0]);
                    final int n6 = ++n5 + this.writeVarInt(message.length - 1);
                    this.tddos.write(message, 1, message.length - 1);
                    n2 = n6 + (message.length - 1);
                    break;
                }
                case 4: {
                    final MetaMessage metaMessage = (MetaMessage)value.getMessage();
                    metaMessage.getLength();
                    final byte[] message2 = metaMessage.getMessage();
                    final int n7 = n2 + this.writeVarInt(n4);
                    n3 = status;
                    this.tddos.write(message2, 0, message2.length);
                    n2 = n7 + message2.length;
                    break;
                }
                case 6: {
                    break;
                }
                case 5: {
                    break;
                }
                default: {
                    throw new InvalidMidiDataException("internal file writer error");
                }
            }
        }
        dataOutputStream.writeInt(1297379947);
        dataOutputStream.writeInt(n2);
        n2 += 8;
        final SequenceInputStream sequenceInputStream = new SequenceInputStream(pipedInputStream, new ByteArrayInputStream(byteArrayOutputStream.toByteArray()));
        dataOutputStream.close();
        this.tddos.close();
        return sequenceInputStream;
    }
    
    static {
        types = new int[] { 0, 1 };
    }
}
