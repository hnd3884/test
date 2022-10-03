package javax.sound.midi;

import com.sun.media.sound.JDK13Services;
import java.io.OutputStream;
import java.util.Iterator;
import javax.sound.midi.spi.MidiFileWriter;
import java.util.HashSet;
import javax.sound.midi.spi.MidiFileReader;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import javax.sound.midi.spi.SoundbankReader;
import java.io.InputStream;
import com.sun.media.sound.AutoConnectSequencer;
import com.sun.media.sound.MidiDeviceTransmitterEnvelope;
import com.sun.media.sound.MidiDeviceReceiverEnvelope;
import com.sun.media.sound.ReferenceCountingDevice;
import java.util.List;
import javax.sound.midi.spi.MidiDeviceProvider;
import java.util.ArrayList;

public class MidiSystem
{
    private MidiSystem() {
    }
    
    public static MidiDevice.Info[] getMidiDeviceInfo() {
        final ArrayList list = new ArrayList();
        final List midiDeviceProviders = getMidiDeviceProviders();
        for (int i = 0; i < midiDeviceProviders.size(); ++i) {
            final MidiDevice.Info[] deviceInfo = midiDeviceProviders.get(i).getDeviceInfo();
            for (int j = 0; j < deviceInfo.length; ++j) {
                list.add(deviceInfo[j]);
            }
        }
        return (MidiDevice.Info[])list.toArray(new MidiDevice.Info[0]);
    }
    
    public static MidiDevice getMidiDevice(final MidiDevice.Info info) throws MidiUnavailableException {
        final List midiDeviceProviders = getMidiDeviceProviders();
        for (int i = 0; i < midiDeviceProviders.size(); ++i) {
            final MidiDeviceProvider midiDeviceProvider = midiDeviceProviders.get(i);
            if (midiDeviceProvider.isDeviceSupported(info)) {
                return midiDeviceProvider.getDevice(info);
            }
        }
        throw new IllegalArgumentException("Requested device not installed: " + info);
    }
    
    public static Receiver getReceiver() throws MidiUnavailableException {
        final MidiDevice defaultDeviceWrapper = getDefaultDeviceWrapper(Receiver.class);
        Receiver receiver;
        if (defaultDeviceWrapper instanceof ReferenceCountingDevice) {
            receiver = ((ReferenceCountingDevice)defaultDeviceWrapper).getReceiverReferenceCounting();
        }
        else {
            receiver = defaultDeviceWrapper.getReceiver();
        }
        if (!(receiver instanceof MidiDeviceReceiver)) {
            receiver = new MidiDeviceReceiverEnvelope(defaultDeviceWrapper, receiver);
        }
        return receiver;
    }
    
    public static Transmitter getTransmitter() throws MidiUnavailableException {
        final MidiDevice defaultDeviceWrapper = getDefaultDeviceWrapper(Transmitter.class);
        Transmitter transmitter;
        if (defaultDeviceWrapper instanceof ReferenceCountingDevice) {
            transmitter = ((ReferenceCountingDevice)defaultDeviceWrapper).getTransmitterReferenceCounting();
        }
        else {
            transmitter = defaultDeviceWrapper.getTransmitter();
        }
        if (!(transmitter instanceof MidiDeviceTransmitter)) {
            transmitter = new MidiDeviceTransmitterEnvelope(defaultDeviceWrapper, transmitter);
        }
        return transmitter;
    }
    
    public static Synthesizer getSynthesizer() throws MidiUnavailableException {
        return (Synthesizer)getDefaultDeviceWrapper(Synthesizer.class);
    }
    
    public static Sequencer getSequencer() throws MidiUnavailableException {
        return getSequencer(true);
    }
    
    public static Sequencer getSequencer(final boolean b) throws MidiUnavailableException {
        final MidiDevice midiDevice = getDefaultDeviceWrapper(Sequencer.class);
        if (b) {
            Receiver receiver = null;
            MidiUnavailableException ex = null;
            try {
                final Synthesizer synthesizer = getSynthesizer();
                if (synthesizer instanceof ReferenceCountingDevice) {
                    receiver = ((ReferenceCountingDevice)synthesizer).getReceiverReferenceCounting();
                }
                else {
                    synthesizer.open();
                    try {
                        receiver = synthesizer.getReceiver();
                    }
                    finally {
                        if (receiver == null) {
                            synthesizer.close();
                        }
                    }
                }
            }
            catch (final MidiUnavailableException ex2) {
                if (ex2 instanceof MidiUnavailableException) {
                    ex = ex2;
                }
            }
            if (receiver == null) {
                try {
                    receiver = getReceiver();
                }
                catch (final Exception ex3) {
                    if (ex3 instanceof MidiUnavailableException) {
                        ex = (MidiUnavailableException)ex3;
                    }
                }
            }
            if (receiver != null) {
                midiDevice.getTransmitter().setReceiver(receiver);
                if (midiDevice instanceof AutoConnectSequencer) {
                    ((AutoConnectSequencer)midiDevice).setAutoConnect(receiver);
                }
            }
            else {
                if (ex != null) {
                    throw ex;
                }
                throw new MidiUnavailableException("no receiver available");
            }
        }
        return (Sequencer)midiDevice;
    }
    
    public static Soundbank getSoundbank(final InputStream inputStream) throws InvalidMidiDataException, IOException {
        final List soundbankReaders = getSoundbankReaders();
        for (int i = 0; i < soundbankReaders.size(); ++i) {
            final Soundbank soundbank = soundbankReaders.get(i).getSoundbank(inputStream);
            if (soundbank != null) {
                return soundbank;
            }
        }
        throw new InvalidMidiDataException("cannot get soundbank from stream");
    }
    
    public static Soundbank getSoundbank(final URL url) throws InvalidMidiDataException, IOException {
        final List soundbankReaders = getSoundbankReaders();
        for (int i = 0; i < soundbankReaders.size(); ++i) {
            final Soundbank soundbank = soundbankReaders.get(i).getSoundbank(url);
            if (soundbank != null) {
                return soundbank;
            }
        }
        throw new InvalidMidiDataException("cannot get soundbank from stream");
    }
    
    public static Soundbank getSoundbank(final File file) throws InvalidMidiDataException, IOException {
        final List soundbankReaders = getSoundbankReaders();
        for (int i = 0; i < soundbankReaders.size(); ++i) {
            final Soundbank soundbank = soundbankReaders.get(i).getSoundbank(file);
            if (soundbank != null) {
                return soundbank;
            }
        }
        throw new InvalidMidiDataException("cannot get soundbank from stream");
    }
    
    public static MidiFileFormat getMidiFileFormat(final InputStream inputStream) throws InvalidMidiDataException, IOException {
        final List midiFileReaders = getMidiFileReaders();
        MidiFileFormat midiFileFormat = null;
        int i = 0;
        while (i < midiFileReaders.size()) {
            final MidiFileReader midiFileReader = midiFileReaders.get(i);
            try {
                midiFileFormat = midiFileReader.getMidiFileFormat(inputStream);
            }
            catch (final InvalidMidiDataException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (midiFileFormat == null) {
            throw new InvalidMidiDataException("input stream is not a supported file type");
        }
        return midiFileFormat;
    }
    
    public static MidiFileFormat getMidiFileFormat(final URL url) throws InvalidMidiDataException, IOException {
        final List midiFileReaders = getMidiFileReaders();
        MidiFileFormat midiFileFormat = null;
        int i = 0;
        while (i < midiFileReaders.size()) {
            final MidiFileReader midiFileReader = midiFileReaders.get(i);
            try {
                midiFileFormat = midiFileReader.getMidiFileFormat(url);
            }
            catch (final InvalidMidiDataException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (midiFileFormat == null) {
            throw new InvalidMidiDataException("url is not a supported file type");
        }
        return midiFileFormat;
    }
    
    public static MidiFileFormat getMidiFileFormat(final File file) throws InvalidMidiDataException, IOException {
        final List midiFileReaders = getMidiFileReaders();
        MidiFileFormat midiFileFormat = null;
        int i = 0;
        while (i < midiFileReaders.size()) {
            final MidiFileReader midiFileReader = midiFileReaders.get(i);
            try {
                midiFileFormat = midiFileReader.getMidiFileFormat(file);
            }
            catch (final InvalidMidiDataException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (midiFileFormat == null) {
            throw new InvalidMidiDataException("file is not a supported file type");
        }
        return midiFileFormat;
    }
    
    public static Sequence getSequence(final InputStream inputStream) throws InvalidMidiDataException, IOException {
        final List midiFileReaders = getMidiFileReaders();
        Sequence sequence = null;
        int i = 0;
        while (i < midiFileReaders.size()) {
            final MidiFileReader midiFileReader = midiFileReaders.get(i);
            try {
                sequence = midiFileReader.getSequence(inputStream);
            }
            catch (final InvalidMidiDataException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (sequence == null) {
            throw new InvalidMidiDataException("could not get sequence from input stream");
        }
        return sequence;
    }
    
    public static Sequence getSequence(final URL url) throws InvalidMidiDataException, IOException {
        final List midiFileReaders = getMidiFileReaders();
        Sequence sequence = null;
        int i = 0;
        while (i < midiFileReaders.size()) {
            final MidiFileReader midiFileReader = midiFileReaders.get(i);
            try {
                sequence = midiFileReader.getSequence(url);
            }
            catch (final InvalidMidiDataException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (sequence == null) {
            throw new InvalidMidiDataException("could not get sequence from URL");
        }
        return sequence;
    }
    
    public static Sequence getSequence(final File file) throws InvalidMidiDataException, IOException {
        final List midiFileReaders = getMidiFileReaders();
        Sequence sequence = null;
        int i = 0;
        while (i < midiFileReaders.size()) {
            final MidiFileReader midiFileReader = midiFileReaders.get(i);
            try {
                sequence = midiFileReader.getSequence(file);
            }
            catch (final InvalidMidiDataException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (sequence == null) {
            throw new InvalidMidiDataException("could not get sequence from file");
        }
        return sequence;
    }
    
    public static int[] getMidiFileTypes() {
        final List midiFileWriters = getMidiFileWriters();
        final HashSet set = new HashSet();
        for (int i = 0; i < midiFileWriters.size(); ++i) {
            final int[] midiFileTypes = midiFileWriters.get(i).getMidiFileTypes();
            for (int j = 0; j < midiFileTypes.length; ++j) {
                set.add(new Integer(midiFileTypes[j]));
            }
        }
        final int[] array = new int[set.size()];
        int n = 0;
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            array[n++] = (int)iterator.next();
        }
        return array;
    }
    
    public static boolean isFileTypeSupported(final int n) {
        final List midiFileWriters = getMidiFileWriters();
        for (int i = 0; i < midiFileWriters.size(); ++i) {
            if (((MidiFileWriter)midiFileWriters.get(i)).isFileTypeSupported(n)) {
                return true;
            }
        }
        return false;
    }
    
    public static int[] getMidiFileTypes(final Sequence sequence) {
        final List midiFileWriters = getMidiFileWriters();
        final HashSet set = new HashSet();
        for (int i = 0; i < midiFileWriters.size(); ++i) {
            final int[] midiFileTypes = midiFileWriters.get(i).getMidiFileTypes(sequence);
            for (int j = 0; j < midiFileTypes.length; ++j) {
                set.add(new Integer(midiFileTypes[j]));
            }
        }
        final int[] array = new int[set.size()];
        int n = 0;
        final Iterator iterator = set.iterator();
        while (iterator.hasNext()) {
            array[n++] = (int)iterator.next();
        }
        return array;
    }
    
    public static boolean isFileTypeSupported(final int n, final Sequence sequence) {
        final List midiFileWriters = getMidiFileWriters();
        for (int i = 0; i < midiFileWriters.size(); ++i) {
            if (((MidiFileWriter)midiFileWriters.get(i)).isFileTypeSupported(n, sequence)) {
                return true;
            }
        }
        return false;
    }
    
    public static int write(final Sequence sequence, final int n, final OutputStream outputStream) throws IOException {
        final List midiFileWriters = getMidiFileWriters();
        int write = -2;
        for (int i = 0; i < midiFileWriters.size(); ++i) {
            final MidiFileWriter midiFileWriter = midiFileWriters.get(i);
            if (midiFileWriter.isFileTypeSupported(n, sequence)) {
                write = midiFileWriter.write(sequence, n, outputStream);
                break;
            }
        }
        if (write == -2) {
            throw new IllegalArgumentException("MIDI file type is not supported");
        }
        return write;
    }
    
    public static int write(final Sequence sequence, final int n, final File file) throws IOException {
        final List midiFileWriters = getMidiFileWriters();
        int write = -2;
        for (int i = 0; i < midiFileWriters.size(); ++i) {
            final MidiFileWriter midiFileWriter = midiFileWriters.get(i);
            if (midiFileWriter.isFileTypeSupported(n, sequence)) {
                write = midiFileWriter.write(sequence, n, file);
                break;
            }
        }
        if (write == -2) {
            throw new IllegalArgumentException("MIDI file type is not supported");
        }
        return write;
    }
    
    private static List getMidiDeviceProviders() {
        return getProviders(MidiDeviceProvider.class);
    }
    
    private static List getSoundbankReaders() {
        return getProviders(SoundbankReader.class);
    }
    
    private static List getMidiFileWriters() {
        return getProviders(MidiFileWriter.class);
    }
    
    private static List getMidiFileReaders() {
        return getProviders(MidiFileReader.class);
    }
    
    private static MidiDevice getDefaultDeviceWrapper(final Class clazz) throws MidiUnavailableException {
        try {
            return getDefaultDevice(clazz);
        }
        catch (final IllegalArgumentException ex) {
            final MidiUnavailableException ex2 = new MidiUnavailableException();
            ex2.initCause(ex);
            throw ex2;
        }
    }
    
    private static MidiDevice getDefaultDevice(final Class clazz) {
        final List midiDeviceProviders = getMidiDeviceProviders();
        final String defaultProviderClassName = JDK13Services.getDefaultProviderClassName(clazz);
        final String defaultInstanceName = JDK13Services.getDefaultInstanceName(clazz);
        if (defaultProviderClassName != null) {
            final MidiDeviceProvider namedProvider = getNamedProvider(defaultProviderClassName, midiDeviceProviders);
            if (namedProvider != null) {
                if (defaultInstanceName != null) {
                    final MidiDevice namedDevice = getNamedDevice(defaultInstanceName, namedProvider, clazz);
                    if (namedDevice != null) {
                        return namedDevice;
                    }
                }
                final MidiDevice firstDevice = getFirstDevice(namedProvider, clazz);
                if (firstDevice != null) {
                    return firstDevice;
                }
            }
        }
        if (defaultInstanceName != null) {
            final MidiDevice namedDevice2 = getNamedDevice(defaultInstanceName, midiDeviceProviders, clazz);
            if (namedDevice2 != null) {
                return namedDevice2;
            }
        }
        final MidiDevice firstDevice2 = getFirstDevice(midiDeviceProviders, clazz);
        if (firstDevice2 != null) {
            return firstDevice2;
        }
        throw new IllegalArgumentException("Requested device not installed");
    }
    
    private static MidiDeviceProvider getNamedProvider(final String s, final List list) {
        for (int i = 0; i < list.size(); ++i) {
            final MidiDeviceProvider midiDeviceProvider = list.get(i);
            if (midiDeviceProvider.getClass().getName().equals(s)) {
                return midiDeviceProvider;
            }
        }
        return null;
    }
    
    private static MidiDevice getNamedDevice(final String s, final MidiDeviceProvider midiDeviceProvider, final Class clazz) {
        final MidiDevice namedDevice = getNamedDevice(s, midiDeviceProvider, clazz, false, false);
        if (namedDevice != null) {
            return namedDevice;
        }
        if (clazz == Receiver.class) {
            final MidiDevice namedDevice2 = getNamedDevice(s, midiDeviceProvider, clazz, true, false);
            if (namedDevice2 != null) {
                return namedDevice2;
            }
        }
        return null;
    }
    
    private static MidiDevice getNamedDevice(final String s, final MidiDeviceProvider midiDeviceProvider, final Class clazz, final boolean b, final boolean b2) {
        final MidiDevice.Info[] deviceInfo = midiDeviceProvider.getDeviceInfo();
        for (int i = 0; i < deviceInfo.length; ++i) {
            if (deviceInfo[i].getName().equals(s)) {
                final MidiDevice device = midiDeviceProvider.getDevice(deviceInfo[i]);
                if (isAppropriateDevice(device, clazz, b, b2)) {
                    return device;
                }
            }
        }
        return null;
    }
    
    private static MidiDevice getNamedDevice(final String s, final List list, final Class clazz) {
        final MidiDevice namedDevice = getNamedDevice(s, list, clazz, false, false);
        if (namedDevice != null) {
            return namedDevice;
        }
        if (clazz == Receiver.class) {
            final MidiDevice namedDevice2 = getNamedDevice(s, list, clazz, true, false);
            if (namedDevice2 != null) {
                return namedDevice2;
            }
        }
        return null;
    }
    
    private static MidiDevice getNamedDevice(final String s, final List list, final Class clazz, final boolean b, final boolean b2) {
        for (int i = 0; i < list.size(); ++i) {
            final MidiDevice namedDevice = getNamedDevice(s, list.get(i), clazz, b, b2);
            if (namedDevice != null) {
                return namedDevice;
            }
        }
        return null;
    }
    
    private static MidiDevice getFirstDevice(final MidiDeviceProvider midiDeviceProvider, final Class clazz) {
        final MidiDevice firstDevice = getFirstDevice(midiDeviceProvider, clazz, false, false);
        if (firstDevice != null) {
            return firstDevice;
        }
        if (clazz == Receiver.class) {
            final MidiDevice firstDevice2 = getFirstDevice(midiDeviceProvider, clazz, true, false);
            if (firstDevice2 != null) {
                return firstDevice2;
            }
        }
        return null;
    }
    
    private static MidiDevice getFirstDevice(final MidiDeviceProvider midiDeviceProvider, final Class clazz, final boolean b, final boolean b2) {
        final MidiDevice.Info[] deviceInfo = midiDeviceProvider.getDeviceInfo();
        for (int i = 0; i < deviceInfo.length; ++i) {
            final MidiDevice device = midiDeviceProvider.getDevice(deviceInfo[i]);
            if (isAppropriateDevice(device, clazz, b, b2)) {
                return device;
            }
        }
        return null;
    }
    
    private static MidiDevice getFirstDevice(final List list, final Class clazz) {
        final MidiDevice firstDevice = getFirstDevice(list, clazz, false, false);
        if (firstDevice != null) {
            return firstDevice;
        }
        if (clazz == Receiver.class) {
            final MidiDevice firstDevice2 = getFirstDevice(list, clazz, true, false);
            if (firstDevice2 != null) {
                return firstDevice2;
            }
        }
        return null;
    }
    
    private static MidiDevice getFirstDevice(final List list, final Class clazz, final boolean b, final boolean b2) {
        for (int i = 0; i < list.size(); ++i) {
            final MidiDevice firstDevice = getFirstDevice(list.get(i), clazz, b, b2);
            if (firstDevice != null) {
                return firstDevice;
            }
        }
        return null;
    }
    
    private static boolean isAppropriateDevice(final MidiDevice midiDevice, final Class clazz, final boolean b, final boolean b2) {
        return clazz.isInstance(midiDevice) || (((!(midiDevice instanceof Sequencer) && !(midiDevice instanceof Synthesizer)) || (midiDevice instanceof Sequencer && b2) || (midiDevice instanceof Synthesizer && b)) && ((clazz == Receiver.class && midiDevice.getMaxReceivers() != 0) || (clazz == Transmitter.class && midiDevice.getMaxTransmitters() != 0)));
    }
    
    private static List getProviders(final Class clazz) {
        return JDK13Services.getProviders(clazz);
    }
}
