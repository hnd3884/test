package com.sun.media.sound;

import java.lang.ref.WeakReference;
import javax.sound.midi.Transmitter;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.AudioSystem;
import javax.sound.midi.MidiUnavailableException;
import java.util.StringTokenizer;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import java.util.Properties;
import java.util.Comparator;
import java.util.Arrays;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;
import javax.sound.midi.MidiSystem;
import java.io.BufferedInputStream;
import java.security.AccessController;
import java.io.FileInputStream;
import java.io.File;
import java.io.InputStream;
import java.security.PrivilegedAction;
import javax.sound.midi.VoiceStatus;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Instrument;
import java.io.IOException;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.HashMap;
import javax.sound.midi.Receiver;
import java.util.ArrayList;
import java.util.Map;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioFormat;
import javax.sound.midi.Soundbank;
import javax.sound.sampled.SourceDataLine;
import javax.sound.midi.MidiDevice;

public final class SoftSynthesizer implements AudioSynthesizer, ReferenceCountingDevice
{
    static final String INFO_NAME = "Gervill";
    static final String INFO_VENDOR = "OpenJDK";
    static final String INFO_DESCRIPTION = "Software MIDI Synthesizer";
    static final String INFO_VERSION = "1.0";
    static final MidiDevice.Info info;
    private static SourceDataLine testline;
    private static Soundbank defaultSoundBank;
    WeakAudioStream weakstream;
    final Object control_mutex;
    int voiceIDCounter;
    int voice_allocation_mode;
    boolean load_default_soundbank;
    boolean reverb_light;
    boolean reverb_on;
    boolean chorus_on;
    boolean agc_on;
    SoftChannel[] channels;
    SoftChannelProxy[] external_channels;
    private boolean largemode;
    private int gmmode;
    private int deviceid;
    private AudioFormat format;
    private SourceDataLine sourceDataLine;
    private SoftAudioPusher pusher;
    private AudioInputStream pusher_stream;
    private float controlrate;
    private boolean open;
    private boolean implicitOpen;
    private String resamplerType;
    private SoftResampler resampler;
    private int number_of_midi_channels;
    private int maxpoly;
    private long latency;
    private boolean jitter_correction;
    private SoftMainMixer mainmixer;
    private SoftVoice[] voices;
    private Map<String, SoftTuning> tunings;
    private Map<String, SoftInstrument> inslist;
    private Map<String, ModelInstrument> loadedlist;
    private ArrayList<Receiver> recvslist;
    
    public SoftSynthesizer() {
        this.weakstream = null;
        this.control_mutex = this;
        this.voiceIDCounter = 0;
        this.voice_allocation_mode = 0;
        this.load_default_soundbank = false;
        this.reverb_light = true;
        this.reverb_on = true;
        this.chorus_on = true;
        this.agc_on = true;
        this.external_channels = null;
        this.largemode = false;
        this.gmmode = 0;
        this.deviceid = 0;
        this.format = new AudioFormat(44100.0f, 16, 2, true, false);
        this.sourceDataLine = null;
        this.pusher = null;
        this.pusher_stream = null;
        this.controlrate = 147.0f;
        this.open = false;
        this.implicitOpen = false;
        this.resamplerType = "linear";
        this.resampler = new SoftLinearResampler();
        this.number_of_midi_channels = 16;
        this.maxpoly = 64;
        this.latency = 200000L;
        this.jitter_correction = false;
        this.tunings = new HashMap<String, SoftTuning>();
        this.inslist = new HashMap<String, SoftInstrument>();
        this.loadedlist = new HashMap<String, ModelInstrument>();
        this.recvslist = new ArrayList<Receiver>();
    }
    
    private void getBuffers(final ModelInstrument modelInstrument, final List<ModelByteBuffer> list) {
        for (final ModelPerformer modelPerformer : modelInstrument.getPerformers()) {
            if (modelPerformer.getOscillators() != null) {
                for (final ModelOscillator modelOscillator : modelPerformer.getOscillators()) {
                    if (modelOscillator instanceof ModelByteBufferWavetable) {
                        final ModelByteBufferWavetable modelByteBufferWavetable = (ModelByteBufferWavetable)modelOscillator;
                        final ModelByteBuffer buffer = modelByteBufferWavetable.getBuffer();
                        if (buffer != null) {
                            list.add(buffer);
                        }
                        final ModelByteBuffer get8BitExtensionBuffer = modelByteBufferWavetable.get8BitExtensionBuffer();
                        if (get8BitExtensionBuffer == null) {
                            continue;
                        }
                        list.add(get8BitExtensionBuffer);
                    }
                }
            }
        }
    }
    
    private boolean loadSamples(final List<ModelInstrument> list) {
        if (this.largemode) {
            return true;
        }
        final ArrayList list2 = new ArrayList();
        final Iterator<ModelInstrument> iterator = list.iterator();
        while (iterator.hasNext()) {
            this.getBuffers(iterator.next(), list2);
        }
        try {
            ModelByteBuffer.loadAll(list2);
        }
        catch (final IOException ex) {
            return false;
        }
        return true;
    }
    
    private boolean loadInstruments(final List<ModelInstrument> list) {
        if (!this.isOpen()) {
            return false;
        }
        if (!this.loadSamples(list)) {
            return false;
        }
        synchronized (this.control_mutex) {
            if (this.channels != null) {
                for (final SoftChannel softChannel : this.channels) {
                    softChannel.current_instrument = null;
                    softChannel.current_director = null;
                }
            }
            for (final Instrument instrument : list) {
                final String patchToString = this.patchToString(instrument.getPatch());
                this.inslist.put(patchToString, new SoftInstrument((ModelInstrument)instrument));
                this.loadedlist.put(patchToString, (ModelInstrument)instrument);
            }
        }
        return true;
    }
    
    private void processPropertyInfo(final Map<String, Object> map) {
        final AudioSynthesizerPropertyInfo[] propertyInfo = this.getPropertyInfo(map);
        final String s = (String)propertyInfo[0].value;
        if (s.equalsIgnoreCase("point")) {
            this.resampler = new SoftPointResampler();
            this.resamplerType = "point";
        }
        else if (s.equalsIgnoreCase("linear")) {
            this.resampler = new SoftLinearResampler2();
            this.resamplerType = "linear";
        }
        else if (s.equalsIgnoreCase("linear1")) {
            this.resampler = new SoftLinearResampler();
            this.resamplerType = "linear1";
        }
        else if (s.equalsIgnoreCase("linear2")) {
            this.resampler = new SoftLinearResampler2();
            this.resamplerType = "linear2";
        }
        else if (s.equalsIgnoreCase("cubic")) {
            this.resampler = new SoftCubicResampler();
            this.resamplerType = "cubic";
        }
        else if (s.equalsIgnoreCase("lanczos")) {
            this.resampler = new SoftLanczosResampler();
            this.resamplerType = "lanczos";
        }
        else if (s.equalsIgnoreCase("sinc")) {
            this.resampler = new SoftSincResampler();
            this.resamplerType = "sinc";
        }
        this.setFormat((AudioFormat)propertyInfo[2].value);
        this.controlrate = (float)propertyInfo[1].value;
        this.latency = (long)propertyInfo[3].value;
        this.deviceid = (int)propertyInfo[4].value;
        this.maxpoly = (int)propertyInfo[5].value;
        this.reverb_on = (boolean)propertyInfo[6].value;
        this.chorus_on = (boolean)propertyInfo[7].value;
        this.agc_on = (boolean)propertyInfo[8].value;
        this.largemode = (boolean)propertyInfo[9].value;
        this.number_of_midi_channels = (int)propertyInfo[10].value;
        this.jitter_correction = (boolean)propertyInfo[11].value;
        this.reverb_light = (boolean)propertyInfo[12].value;
        this.load_default_soundbank = (boolean)propertyInfo[13].value;
    }
    
    private String patchToString(final Patch patch) {
        if (patch instanceof ModelPatch && ((ModelPatch)patch).isPercussion()) {
            return "p." + patch.getProgram() + "." + patch.getBank();
        }
        return patch.getProgram() + "." + patch.getBank();
    }
    
    private void setFormat(final AudioFormat format) {
        if (format.getChannels() > 2) {
            throw new IllegalArgumentException("Only mono and stereo audio supported.");
        }
        if (AudioFloatConverter.getConverter(format) == null) {
            throw new IllegalArgumentException("Audio format not supported.");
        }
        this.format = format;
    }
    
    void removeReceiver(final Receiver receiver) {
        boolean b = false;
        synchronized (this.control_mutex) {
            if (this.recvslist.remove(receiver) && this.implicitOpen && this.recvslist.isEmpty()) {
                b = true;
            }
        }
        if (b) {
            this.close();
        }
    }
    
    SoftMainMixer getMainMixer() {
        if (!this.isOpen()) {
            return null;
        }
        return this.mainmixer;
    }
    
    SoftInstrument findInstrument(final int n, final int n2, final int n3) {
        if (n2 >> 7 == 120 || n2 >> 7 == 121) {
            final SoftInstrument softInstrument = this.inslist.get(n + "." + n2);
            if (softInstrument != null) {
                return softInstrument;
            }
            String s;
            if (n2 >> 7 == 120) {
                s = "p.";
            }
            else {
                s = "";
            }
            final SoftInstrument softInstrument2 = this.inslist.get(s + n + "." + ((n2 & 0x80) << 7));
            if (softInstrument2 != null) {
                return softInstrument2;
            }
            final SoftInstrument softInstrument3 = this.inslist.get(s + n + "." + (n2 & 0x80));
            if (softInstrument3 != null) {
                return softInstrument3;
            }
            final SoftInstrument softInstrument4 = this.inslist.get(s + n + ".0");
            if (softInstrument4 != null) {
                return softInstrument4;
            }
            final SoftInstrument softInstrument5 = this.inslist.get(s + n + "0.0");
            if (softInstrument5 != null) {
                return softInstrument5;
            }
            return null;
        }
        else {
            String s2;
            if (n3 == 9) {
                s2 = "p.";
            }
            else {
                s2 = "";
            }
            final SoftInstrument softInstrument6 = this.inslist.get(s2 + n + "." + n2);
            if (softInstrument6 != null) {
                return softInstrument6;
            }
            final SoftInstrument softInstrument7 = this.inslist.get(s2 + n + ".0");
            if (softInstrument7 != null) {
                return softInstrument7;
            }
            final SoftInstrument softInstrument8 = this.inslist.get(s2 + "0.0");
            if (softInstrument8 != null) {
                return softInstrument8;
            }
            return null;
        }
    }
    
    int getVoiceAllocationMode() {
        return this.voice_allocation_mode;
    }
    
    int getGeneralMidiMode() {
        return this.gmmode;
    }
    
    void setGeneralMidiMode(final int gmmode) {
        this.gmmode = gmmode;
    }
    
    int getDeviceID() {
        return this.deviceid;
    }
    
    float getControlRate() {
        return this.controlrate;
    }
    
    SoftVoice[] getVoices() {
        return this.voices;
    }
    
    SoftTuning getTuning(final Patch patch) {
        final String patchToString = this.patchToString(patch);
        SoftTuning softTuning = this.tunings.get(patchToString);
        if (softTuning == null) {
            softTuning = new SoftTuning(patch);
            this.tunings.put(patchToString, softTuning);
        }
        return softTuning;
    }
    
    @Override
    public long getLatency() {
        synchronized (this.control_mutex) {
            return this.latency;
        }
    }
    
    @Override
    public AudioFormat getFormat() {
        synchronized (this.control_mutex) {
            return this.format;
        }
    }
    
    @Override
    public int getMaxPolyphony() {
        synchronized (this.control_mutex) {
            return this.maxpoly;
        }
    }
    
    @Override
    public MidiChannel[] getChannels() {
        synchronized (this.control_mutex) {
            if (this.external_channels == null) {
                this.external_channels = new SoftChannelProxy[16];
                for (int i = 0; i < this.external_channels.length; ++i) {
                    this.external_channels[i] = new SoftChannelProxy();
                }
            }
            MidiChannel[] array;
            if (this.isOpen()) {
                array = new MidiChannel[this.channels.length];
            }
            else {
                array = new MidiChannel[16];
            }
            for (int j = 0; j < array.length; ++j) {
                array[j] = this.external_channels[j];
            }
            return array;
        }
    }
    
    @Override
    public VoiceStatus[] getVoiceStatus() {
        if (!this.isOpen()) {
            final VoiceStatus[] array = new VoiceStatus[this.getMaxPolyphony()];
            for (int i = 0; i < array.length; ++i) {
                final VoiceStatus voiceStatus = new VoiceStatus();
                voiceStatus.active = false;
                voiceStatus.bank = 0;
                voiceStatus.channel = 0;
                voiceStatus.note = 0;
                voiceStatus.program = 0;
                voiceStatus.volume = 0;
                array[i] = voiceStatus;
            }
            return array;
        }
        synchronized (this.control_mutex) {
            final VoiceStatus[] array2 = new VoiceStatus[this.voices.length];
            for (int j = 0; j < this.voices.length; ++j) {
                final SoftVoice softVoice = this.voices[j];
                final VoiceStatus voiceStatus2 = new VoiceStatus();
                voiceStatus2.active = softVoice.active;
                voiceStatus2.bank = softVoice.bank;
                voiceStatus2.channel = softVoice.channel;
                voiceStatus2.note = softVoice.note;
                voiceStatus2.program = softVoice.program;
                voiceStatus2.volume = softVoice.volume;
                array2[j] = voiceStatus2;
            }
            return array2;
        }
    }
    
    @Override
    public boolean isSoundbankSupported(final Soundbank soundbank) {
        final Instrument[] instruments = soundbank.getInstruments();
        for (int length = instruments.length, i = 0; i < length; ++i) {
            if (!(instruments[i] instanceof ModelInstrument)) {
                return false;
            }
        }
        return true;
    }
    
    @Override
    public boolean loadInstrument(final Instrument instrument) {
        if (instrument == null || !(instrument instanceof ModelInstrument)) {
            throw new IllegalArgumentException("Unsupported instrument: " + instrument);
        }
        final ArrayList list = new ArrayList();
        list.add(instrument);
        return this.loadInstruments(list);
    }
    
    @Override
    public void unloadInstrument(final Instrument instrument) {
        if (instrument == null || !(instrument instanceof ModelInstrument)) {
            throw new IllegalArgumentException("Unsupported instrument: " + instrument);
        }
        if (!this.isOpen()) {
            return;
        }
        final String patchToString = this.patchToString(instrument.getPatch());
        synchronized (this.control_mutex) {
            final SoftChannel[] channels = this.channels;
            for (int length = channels.length, i = 0; i < length; ++i) {
                channels[i].current_instrument = null;
            }
            this.inslist.remove(patchToString);
            this.loadedlist.remove(patchToString);
            for (int j = 0; j < this.channels.length; ++j) {
                this.channels[j].allSoundOff();
            }
        }
    }
    
    @Override
    public boolean remapInstrument(final Instrument instrument, final Instrument instrument2) {
        if (instrument == null) {
            throw new NullPointerException();
        }
        if (instrument2 == null) {
            throw new NullPointerException();
        }
        if (!(instrument instanceof ModelInstrument)) {
            throw new IllegalArgumentException("Unsupported instrument: " + instrument.toString());
        }
        if (!(instrument2 instanceof ModelInstrument)) {
            throw new IllegalArgumentException("Unsupported instrument: " + instrument2.toString());
        }
        if (!this.isOpen()) {
            return false;
        }
        synchronized (this.control_mutex) {
            if (!this.loadedlist.containsValue(instrument2)) {
                throw new IllegalArgumentException("Instrument to is not loaded.");
            }
            this.unloadInstrument(instrument);
            return this.loadInstrument(new ModelMappedInstrument((ModelInstrument)instrument2, instrument.getPatch()));
        }
    }
    
    @Override
    public Soundbank getDefaultSoundbank() {
        synchronized (SoftSynthesizer.class) {
            if (SoftSynthesizer.defaultSoundBank != null) {
                return SoftSynthesizer.defaultSoundBank;
            }
            final ArrayList list = new ArrayList();
            list.add(new PrivilegedAction<InputStream>() {
                @Override
                public InputStream run() {
                    final File file = new File(new File(new File(System.getProperties().getProperty("java.home")), "lib"), "audio");
                    if (file.exists()) {
                        File file2 = null;
                        final File[] listFiles = file.listFiles();
                        if (listFiles != null) {
                            for (int i = 0; i < listFiles.length; ++i) {
                                final File file3 = listFiles[i];
                                if (file3.isFile()) {
                                    final String lowerCase = file3.getName().toLowerCase();
                                    if ((lowerCase.endsWith(".sf2") || lowerCase.endsWith(".dls")) && (file2 == null || file3.length() > file2.length())) {
                                        file2 = file3;
                                    }
                                }
                            }
                        }
                        if (file2 != null) {
                            try {
                                return new FileInputStream(file2);
                            }
                            catch (final IOException ex) {}
                        }
                    }
                    return null;
                }
            });
            list.add(new PrivilegedAction<InputStream>() {
                @Override
                public InputStream run() {
                    if (System.getProperties().getProperty("os.name").startsWith("Linux")) {
                        for (final File file : new File[] { new File("/usr/share/soundfonts/"), new File("/usr/local/share/soundfonts/"), new File("/usr/share/sounds/sf2/"), new File("/usr/local/share/sounds/sf2/") }) {
                            if (file.exists()) {
                                final File file2 = new File(file, "default.sf2");
                                if (file2.exists()) {
                                    try {
                                        return new FileInputStream(file2);
                                    }
                                    catch (final IOException ex) {}
                                }
                            }
                        }
                    }
                    return null;
                }
            });
            list.add(new PrivilegedAction<InputStream>() {
                @Override
                public InputStream run() {
                    if (System.getProperties().getProperty("os.name").startsWith("Windows")) {
                        final File file = new File(System.getenv("SystemRoot") + "\\system32\\drivers\\gm.dls");
                        if (file.exists()) {
                            try {
                                return new FileInputStream(file);
                            }
                            catch (final IOException ex) {}
                        }
                    }
                    return null;
                }
            });
            list.add(new PrivilegedAction<InputStream>() {
                @Override
                public InputStream run() {
                    final File file = new File(new File(System.getProperty("user.home"), ".gervill"), "soundbank-emg.sf2");
                    if (file.exists()) {
                        try {
                            return new FileInputStream(file);
                        }
                        catch (final IOException ex) {}
                    }
                    return null;
                }
            });
            for (final PrivilegedAction privilegedAction : list) {
                try {
                    final InputStream inputStream = AccessController.doPrivileged((PrivilegedAction<InputStream>)privilegedAction);
                    if (inputStream == null) {
                        continue;
                    }
                    Soundbank soundbank;
                    try {
                        soundbank = MidiSystem.getSoundbank(new BufferedInputStream(inputStream));
                    }
                    finally {
                        inputStream.close();
                    }
                    if (soundbank != null) {
                        return SoftSynthesizer.defaultSoundBank = soundbank;
                    }
                    continue;
                }
                catch (final Exception ex) {}
            }
            try {
                SoftSynthesizer.defaultSoundBank = EmergencySoundbank.createSoundbank();
            }
            catch (final Exception ex2) {}
            if (SoftSynthesizer.defaultSoundBank != null) {
                final OutputStream outputStream = AccessController.doPrivileged(() -> {
                    try {
                        final File file = new File(System.getProperty("user.home"), ".gervill");
                        if (!file.exists()) {
                            file.mkdirs();
                        }
                        final File file2 = new File(file, "soundbank-emg.sf2");
                        if (file2.exists()) {
                            return null;
                        }
                        else {
                            return new FileOutputStream(file2);
                        }
                    }
                    catch (final FileNotFoundException ex4) {
                        return null;
                    }
                });
                if (outputStream != null) {
                    try {
                        ((SF2Soundbank)SoftSynthesizer.defaultSoundBank).save(outputStream);
                        outputStream.close();
                    }
                    catch (final IOException ex3) {}
                }
            }
        }
        return SoftSynthesizer.defaultSoundBank;
    }
    
    @Override
    public Instrument[] getAvailableInstruments() {
        final Soundbank defaultSoundbank = this.getDefaultSoundbank();
        if (defaultSoundbank == null) {
            return new Instrument[0];
        }
        final Instrument[] instruments = defaultSoundbank.getInstruments();
        Arrays.sort(instruments, new ModelInstrumentComparator());
        return instruments;
    }
    
    @Override
    public Instrument[] getLoadedInstruments() {
        if (!this.isOpen()) {
            return new Instrument[0];
        }
        synchronized (this.control_mutex) {
            final ModelInstrument[] array = new ModelInstrument[this.loadedlist.values().size()];
            this.loadedlist.values().toArray(array);
            Arrays.sort(array, new ModelInstrumentComparator());
            return array;
        }
    }
    
    @Override
    public boolean loadAllInstruments(final Soundbank soundbank) {
        final ArrayList list = new ArrayList();
        for (final Instrument instrument : soundbank.getInstruments()) {
            if (instrument == null || !(instrument instanceof ModelInstrument)) {
                throw new IllegalArgumentException("Unsupported instrument: " + instrument);
            }
            list.add(instrument);
        }
        return this.loadInstruments(list);
    }
    
    @Override
    public void unloadAllInstruments(final Soundbank soundbank) {
        if (soundbank == null || !this.isSoundbankSupported(soundbank)) {
            throw new IllegalArgumentException("Unsupported soundbank: " + soundbank);
        }
        if (!this.isOpen()) {
            return;
        }
        for (final Instrument instrument : soundbank.getInstruments()) {
            if (instrument instanceof ModelInstrument) {
                this.unloadInstrument(instrument);
            }
        }
    }
    
    @Override
    public boolean loadInstruments(final Soundbank soundbank, final Patch[] array) {
        final ArrayList list = new ArrayList();
        for (int length = array.length, i = 0; i < length; ++i) {
            final Instrument instrument = soundbank.getInstrument(array[i]);
            if (instrument == null || !(instrument instanceof ModelInstrument)) {
                throw new IllegalArgumentException("Unsupported instrument: " + instrument);
            }
            list.add(instrument);
        }
        return this.loadInstruments(list);
    }
    
    @Override
    public void unloadInstruments(final Soundbank soundbank, final Patch[] array) {
        if (soundbank == null || !this.isSoundbankSupported(soundbank)) {
            throw new IllegalArgumentException("Unsupported soundbank: " + soundbank);
        }
        if (!this.isOpen()) {
            return;
        }
        for (int length = array.length, i = 0; i < length; ++i) {
            final Instrument instrument = soundbank.getInstrument(array[i]);
            if (instrument instanceof ModelInstrument) {
                this.unloadInstrument(instrument);
            }
        }
    }
    
    @Override
    public MidiDevice.Info getDeviceInfo() {
        return SoftSynthesizer.info;
    }
    
    private Properties getStoredProperties() {
        return AccessController.doPrivileged(() -> {
            final Properties properties = new Properties();
            try {
                Preferences.userRoot();
                final Preferences preferences;
                final String s;
                if (preferences.nodeExists(s)) {
                    preferences.node(s);
                    final Preferences preferences2;
                    preferences2.keys();
                    final String[] array;
                    int i = 0;
                    for (int length = array.length; i < length; ++i) {
                        final String s2 = array[i];
                        preferences2.get(s2, null);
                        final String s3;
                        if (s3 != null) {
                            properties.setProperty(s2, s3);
                        }
                    }
                }
            }
            catch (final BackingStoreException ex) {}
            return properties;
        });
    }
    
    @Override
    public AudioSynthesizerPropertyInfo[] getPropertyInfo(final Map<String, Object> map) {
        final ArrayList list = new ArrayList();
        final boolean b = map == null && this.open;
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo = new AudioSynthesizerPropertyInfo("interpolation", b ? this.resamplerType : "linear");
        audioSynthesizerPropertyInfo.choices = new String[] { "linear", "linear1", "linear2", "cubic", "lanczos", "sinc", "point" };
        audioSynthesizerPropertyInfo.description = "Interpolation method";
        list.add(audioSynthesizerPropertyInfo);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo2 = new AudioSynthesizerPropertyInfo("control rate", b ? this.controlrate : 147.0f);
        audioSynthesizerPropertyInfo2.description = "Control rate";
        list.add(audioSynthesizerPropertyInfo2);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo3 = new AudioSynthesizerPropertyInfo("format", b ? this.format : new AudioFormat(44100.0f, 16, 2, true, false));
        audioSynthesizerPropertyInfo3.description = "Default audio format";
        list.add(audioSynthesizerPropertyInfo3);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo4 = new AudioSynthesizerPropertyInfo("latency", b ? this.latency : 120000L);
        audioSynthesizerPropertyInfo4.description = "Default latency";
        list.add(audioSynthesizerPropertyInfo4);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo5 = new AudioSynthesizerPropertyInfo("device id", b ? this.deviceid : 0);
        audioSynthesizerPropertyInfo5.description = "Device ID for SysEx Messages";
        list.add(audioSynthesizerPropertyInfo5);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo6 = new AudioSynthesizerPropertyInfo("max polyphony", b ? this.maxpoly : 64);
        audioSynthesizerPropertyInfo6.description = "Maximum polyphony";
        list.add(audioSynthesizerPropertyInfo6);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo7 = new AudioSynthesizerPropertyInfo("reverb", !b || this.reverb_on);
        audioSynthesizerPropertyInfo7.description = "Turn reverb effect on or off";
        list.add(audioSynthesizerPropertyInfo7);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo8 = new AudioSynthesizerPropertyInfo("chorus", !b || this.chorus_on);
        audioSynthesizerPropertyInfo8.description = "Turn chorus effect on or off";
        list.add(audioSynthesizerPropertyInfo8);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo9 = new AudioSynthesizerPropertyInfo("auto gain control", !b || this.agc_on);
        audioSynthesizerPropertyInfo9.description = "Turn auto gain control on or off";
        list.add(audioSynthesizerPropertyInfo9);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo10 = new AudioSynthesizerPropertyInfo("large mode", b && this.largemode);
        audioSynthesizerPropertyInfo10.description = "Turn large mode on or off.";
        list.add(audioSynthesizerPropertyInfo10);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo11 = new AudioSynthesizerPropertyInfo("midi channels", b ? this.channels.length : 16);
        audioSynthesizerPropertyInfo11.description = "Number of midi channels.";
        list.add(audioSynthesizerPropertyInfo11);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo12 = new AudioSynthesizerPropertyInfo("jitter correction", !b || this.jitter_correction);
        audioSynthesizerPropertyInfo12.description = "Turn jitter correction on or off.";
        list.add(audioSynthesizerPropertyInfo12);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo13 = new AudioSynthesizerPropertyInfo("light reverb", !b || this.reverb_light);
        audioSynthesizerPropertyInfo13.description = "Turn light reverb mode on or off";
        list.add(audioSynthesizerPropertyInfo13);
        final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo14 = new AudioSynthesizerPropertyInfo("load default soundbank", !b || this.load_default_soundbank);
        audioSynthesizerPropertyInfo14.description = "Enabled/disable loading default soundbank";
        list.add(audioSynthesizerPropertyInfo14);
        final AudioSynthesizerPropertyInfo[] array = (AudioSynthesizerPropertyInfo[])list.toArray(new AudioSynthesizerPropertyInfo[list.size()]);
        final Properties storedProperties = this.getStoredProperties();
        for (final AudioSynthesizerPropertyInfo audioSynthesizerPropertyInfo15 : array) {
            final String s = (map == null) ? null : map.get(audioSynthesizerPropertyInfo15.name);
            final String value = (s != null) ? s : storedProperties.getProperty(audioSynthesizerPropertyInfo15.name);
            if (value != null) {
                final Class valueClass = audioSynthesizerPropertyInfo15.valueClass;
                if (valueClass.isInstance(value)) {
                    audioSynthesizerPropertyInfo15.value = value;
                }
                else if (value instanceof String) {
                    final String s2 = value;
                    if (valueClass == Boolean.class) {
                        if (s2.equalsIgnoreCase("true")) {
                            audioSynthesizerPropertyInfo15.value = Boolean.TRUE;
                        }
                        if (s2.equalsIgnoreCase("false")) {
                            audioSynthesizerPropertyInfo15.value = Boolean.FALSE;
                        }
                    }
                    else if (valueClass == AudioFormat.class) {
                        int int1 = 2;
                        boolean b2 = true;
                        boolean b3 = false;
                        int int2 = 16;
                        float float1 = 44100.0f;
                        try {
                            final StringTokenizer stringTokenizer = new StringTokenizer(s2, ", ");
                            String s3 = "";
                            while (stringTokenizer.hasMoreTokens()) {
                                final String lowerCase = stringTokenizer.nextToken().toLowerCase();
                                if (lowerCase.equals("mono")) {
                                    int1 = 1;
                                }
                                if (lowerCase.startsWith("channel")) {
                                    int1 = Integer.parseInt(s3);
                                }
                                if (lowerCase.contains("unsigned")) {
                                    b2 = false;
                                }
                                if (lowerCase.equals("big-endian")) {
                                    b3 = true;
                                }
                                if (lowerCase.equals("bit")) {
                                    int2 = Integer.parseInt(s3);
                                }
                                if (lowerCase.equals("hz")) {
                                    float1 = Float.parseFloat(s3);
                                }
                                s3 = lowerCase;
                            }
                            audioSynthesizerPropertyInfo15.value = new AudioFormat(float1, int2, int1, b2, b3);
                        }
                        catch (final NumberFormatException ex) {}
                    }
                    else {
                        try {
                            if (valueClass == Byte.class) {
                                audioSynthesizerPropertyInfo15.value = Byte.valueOf(s2);
                            }
                            else if (valueClass == Short.class) {
                                audioSynthesizerPropertyInfo15.value = Short.valueOf(s2);
                            }
                            else if (valueClass == Integer.class) {
                                audioSynthesizerPropertyInfo15.value = Integer.valueOf(s2);
                            }
                            else if (valueClass == Long.class) {
                                audioSynthesizerPropertyInfo15.value = Long.valueOf(s2);
                            }
                            else if (valueClass == Float.class) {
                                audioSynthesizerPropertyInfo15.value = Float.valueOf(s2);
                            }
                            else if (valueClass == Double.class) {
                                audioSynthesizerPropertyInfo15.value = Double.valueOf(s2);
                            }
                        }
                        catch (final NumberFormatException ex2) {}
                    }
                }
                else if (value instanceof Number) {
                    final Number n = (Number)value;
                    if (valueClass == Byte.class) {
                        audioSynthesizerPropertyInfo15.value = n.byteValue();
                    }
                    if (valueClass == Short.class) {
                        audioSynthesizerPropertyInfo15.value = n.shortValue();
                    }
                    if (valueClass == Integer.class) {
                        audioSynthesizerPropertyInfo15.value = n.intValue();
                    }
                    if (valueClass == Long.class) {
                        audioSynthesizerPropertyInfo15.value = n.longValue();
                    }
                    if (valueClass == Float.class) {
                        audioSynthesizerPropertyInfo15.value = n.floatValue();
                    }
                    if (valueClass == Double.class) {
                        audioSynthesizerPropertyInfo15.value = n.doubleValue();
                    }
                }
            }
        }
        return array;
    }
    
    @Override
    public void open() throws MidiUnavailableException {
        if (this.isOpen()) {
            synchronized (this.control_mutex) {
                this.implicitOpen = false;
            }
            return;
        }
        this.open(null, null);
    }
    
    @Override
    public void open(SourceDataLine sourceDataLine, final Map<String, Object> map) throws MidiUnavailableException {
        if (this.isOpen()) {
            synchronized (this.control_mutex) {
                this.implicitOpen = false;
            }
            return;
        }
        synchronized (this.control_mutex) {
            try {
                if (sourceDataLine != null) {
                    this.setFormat(sourceDataLine.getFormat());
                }
                this.weakstream = new WeakAudioStream(this.openStream(this.getFormat(), map));
                AudioInputStream audioInputStream = this.weakstream.getAudioInputStream();
                if (sourceDataLine == null) {
                    if (SoftSynthesizer.testline != null) {
                        sourceDataLine = SoftSynthesizer.testline;
                    }
                    else {
                        sourceDataLine = AudioSystem.getSourceDataLine(this.getFormat());
                    }
                }
                final double n = (double)this.latency;
                if (!sourceDataLine.isOpen()) {
                    sourceDataLine.open(this.getFormat(), this.getFormat().getFrameSize() * (int)(this.getFormat().getFrameRate() * (n / 1000000.0)));
                    this.sourceDataLine = sourceDataLine;
                }
                if (!sourceDataLine.isActive()) {
                    sourceDataLine.start();
                }
                int available = 512;
                try {
                    available = audioInputStream.available();
                }
                catch (final IOException ex) {}
                final int bufferSize = sourceDataLine.getBufferSize();
                int n2 = bufferSize - bufferSize % available;
                if (n2 < 3 * available) {
                    n2 = 3 * available;
                }
                if (this.jitter_correction) {
                    audioInputStream = new SoftJitterCorrector(audioInputStream, n2, available);
                    if (this.weakstream != null) {
                        this.weakstream.jitter_stream = audioInputStream;
                    }
                }
                this.pusher = new SoftAudioPusher(sourceDataLine, audioInputStream, available);
                this.pusher_stream = audioInputStream;
                this.pusher.start();
                if (this.weakstream != null) {
                    this.weakstream.pusher = this.pusher;
                    this.weakstream.sourceDataLine = this.sourceDataLine;
                }
            }
            catch (final LineUnavailableException | SecurityException | IllegalArgumentException ex2) {
                if (this.isOpen()) {
                    this.close();
                }
                final MidiUnavailableException ex3 = new MidiUnavailableException("Can not open line");
                ex3.initCause((Throwable)ex2);
                throw ex3;
            }
        }
    }
    
    @Override
    public AudioInputStream openStream(final AudioFormat format, final Map<String, Object> map) throws MidiUnavailableException {
        if (this.isOpen()) {
            throw new MidiUnavailableException("Synthesizer is already open");
        }
        synchronized (this.control_mutex) {
            this.gmmode = 0;
            this.voice_allocation_mode = 0;
            this.processPropertyInfo(map);
            this.open = true;
            this.implicitOpen = false;
            if (format != null) {
                this.setFormat(format);
            }
            if (this.load_default_soundbank) {
                final Soundbank defaultSoundbank = this.getDefaultSoundbank();
                if (defaultSoundbank != null) {
                    this.loadAllInstruments(defaultSoundbank);
                }
            }
            this.voices = new SoftVoice[this.maxpoly];
            for (int i = 0; i < this.maxpoly; ++i) {
                this.voices[i] = new SoftVoice(this);
            }
            this.mainmixer = new SoftMainMixer(this);
            this.channels = new SoftChannel[this.number_of_midi_channels];
            for (int j = 0; j < this.channels.length; ++j) {
                this.channels[j] = new SoftChannel(this, j);
            }
            if (this.external_channels == null) {
                if (this.channels.length < 16) {
                    this.external_channels = new SoftChannelProxy[16];
                }
                else {
                    this.external_channels = new SoftChannelProxy[this.channels.length];
                }
                for (int k = 0; k < this.external_channels.length; ++k) {
                    this.external_channels[k] = new SoftChannelProxy();
                }
            }
            else if (this.channels.length > this.external_channels.length) {
                final SoftChannelProxy[] array = new SoftChannelProxy[this.channels.length];
                for (int l = 0; l < this.external_channels.length; ++l) {
                    array[l] = this.external_channels[l];
                }
                for (int length = this.external_channels.length; length < array.length; ++length) {
                    array[length] = new SoftChannelProxy();
                }
            }
            for (int n = 0; n < this.channels.length; ++n) {
                this.external_channels[n].setChannel(this.channels[n]);
            }
            final SoftVoice[] voices = this.getVoices();
            for (int length2 = voices.length, n2 = 0; n2 < length2; ++n2) {
                voices[n2].resampler = this.resampler.openStreamer();
            }
            for (final SoftReceiver softReceiver : this.getReceivers()) {
                softReceiver.open = this.open;
                softReceiver.mainmixer = this.mainmixer;
                softReceiver.midimessages = this.mainmixer.midimessages;
            }
            return this.mainmixer.getInputStream();
        }
    }
    
    @Override
    public void close() {
        if (!this.isOpen()) {
            return;
        }
        SoftAudioPusher pusher = null;
        AudioInputStream pusher_stream = null;
        synchronized (this.control_mutex) {
            if (this.pusher != null) {
                pusher = this.pusher;
                pusher_stream = this.pusher_stream;
                this.pusher = null;
                this.pusher_stream = null;
            }
        }
        if (pusher != null) {
            pusher.stop();
            try {
                pusher_stream.close();
            }
            catch (final IOException ex) {}
        }
        synchronized (this.control_mutex) {
            if (this.mainmixer != null) {
                this.mainmixer.close();
            }
            this.open = false;
            this.implicitOpen = false;
            this.mainmixer = null;
            this.voices = null;
            this.channels = null;
            if (this.external_channels != null) {
                for (int i = 0; i < this.external_channels.length; ++i) {
                    this.external_channels[i].setChannel(null);
                }
            }
            if (this.sourceDataLine != null) {
                this.sourceDataLine.close();
                this.sourceDataLine = null;
            }
            this.inslist.clear();
            this.loadedlist.clear();
            this.tunings.clear();
            while (this.recvslist.size() != 0) {
                this.recvslist.get(this.recvslist.size() - 1).close();
            }
        }
    }
    
    @Override
    public boolean isOpen() {
        synchronized (this.control_mutex) {
            return this.open;
        }
    }
    
    @Override
    public long getMicrosecondPosition() {
        if (!this.isOpen()) {
            return 0L;
        }
        synchronized (this.control_mutex) {
            return this.mainmixer.getMicrosecondPosition();
        }
    }
    
    @Override
    public int getMaxReceivers() {
        return -1;
    }
    
    @Override
    public int getMaxTransmitters() {
        return 0;
    }
    
    @Override
    public Receiver getReceiver() throws MidiUnavailableException {
        synchronized (this.control_mutex) {
            final SoftReceiver softReceiver = new SoftReceiver(this);
            softReceiver.open = this.open;
            this.recvslist.add(softReceiver);
            return softReceiver;
        }
    }
    
    @Override
    public List<Receiver> getReceivers() {
        synchronized (this.control_mutex) {
            final ArrayList list = new ArrayList();
            list.addAll(this.recvslist);
            return list;
        }
    }
    
    @Override
    public Transmitter getTransmitter() throws MidiUnavailableException {
        throw new MidiUnavailableException("No transmitter available");
    }
    
    @Override
    public List<Transmitter> getTransmitters() {
        return new ArrayList<Transmitter>();
    }
    
    @Override
    public Receiver getReceiverReferenceCounting() throws MidiUnavailableException {
        if (!this.isOpen()) {
            this.open();
            synchronized (this.control_mutex) {
                this.implicitOpen = true;
            }
        }
        return this.getReceiver();
    }
    
    @Override
    public Transmitter getTransmitterReferenceCounting() throws MidiUnavailableException {
        throw new MidiUnavailableException("No transmitter available");
    }
    
    static {
        info = new Info();
        SoftSynthesizer.testline = null;
        SoftSynthesizer.defaultSoundBank = null;
    }
    
    protected static final class WeakAudioStream extends InputStream
    {
        private volatile AudioInputStream stream;
        public SoftAudioPusher pusher;
        public AudioInputStream jitter_stream;
        public SourceDataLine sourceDataLine;
        public volatile long silent_samples;
        private int framesize;
        private WeakReference<AudioInputStream> weak_stream_link;
        private AudioFloatConverter converter;
        private float[] silentbuffer;
        private int samplesize;
        
        public void setInputStream(final AudioInputStream stream) {
            this.stream = stream;
        }
        
        @Override
        public int available() throws IOException {
            final AudioInputStream stream = this.stream;
            if (stream != null) {
                return stream.available();
            }
            return 0;
        }
        
        @Override
        public int read() throws IOException {
            final byte[] array = { 0 };
            if (this.read(array) == -1) {
                return -1;
            }
            return array[0] & 0xFF;
        }
        
        @Override
        public int read(final byte[] array, final int n, final int n2) throws IOException {
            final AudioInputStream stream = this.stream;
            if (stream != null) {
                return stream.read(array, n, n2);
            }
            final int n3 = n2 / this.samplesize;
            if (this.silentbuffer == null || this.silentbuffer.length < n3) {
                this.silentbuffer = new float[n3];
            }
            this.converter.toByteArray(this.silentbuffer, n3, array, n);
            this.silent_samples += n2 / this.framesize;
            if (this.pusher != null && this.weak_stream_link.get() == null) {
                final Runnable runnable = new Runnable() {
                    SoftAudioPusher _pusher = WeakAudioStream.this.pusher;
                    AudioInputStream _jitter_stream = WeakAudioStream.this.jitter_stream;
                    SourceDataLine _sourceDataLine = WeakAudioStream.this.sourceDataLine;
                    
                    @Override
                    public void run() {
                        this._pusher.stop();
                        if (this._jitter_stream != null) {
                            try {
                                this._jitter_stream.close();
                            }
                            catch (final IOException ex) {
                                ex.printStackTrace();
                            }
                        }
                        if (this._sourceDataLine != null) {
                            this._sourceDataLine.close();
                        }
                    }
                };
                this.pusher = null;
                this.jitter_stream = null;
                this.sourceDataLine = null;
                new Thread(runnable).start();
            }
            return n2;
        }
        
        public WeakAudioStream(final AudioInputStream stream) {
            this.pusher = null;
            this.jitter_stream = null;
            this.sourceDataLine = null;
            this.silent_samples = 0L;
            this.framesize = 0;
            this.silentbuffer = null;
            this.stream = stream;
            this.weak_stream_link = new WeakReference<AudioInputStream>(stream);
            this.converter = AudioFloatConverter.getConverter(stream.getFormat());
            this.samplesize = stream.getFormat().getFrameSize() / stream.getFormat().getChannels();
            this.framesize = stream.getFormat().getFrameSize();
        }
        
        public AudioInputStream getAudioInputStream() {
            return new AudioInputStream(this, this.stream.getFormat(), -1L);
        }
        
        @Override
        public void close() throws IOException {
            try (final AudioInputStream audioInputStream = this.weak_stream_link.get()) {}
        }
    }
    
    private static class Info extends MidiDevice.Info
    {
        Info() {
            super("Gervill", "OpenJDK", "Software MIDI Synthesizer", "1.0");
        }
    }
}
