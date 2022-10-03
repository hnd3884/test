package com.sun.media.sound;

import javax.sound.midi.MetaMessage;
import javax.sound.midi.MidiEvent;
import java.util.WeakHashMap;
import javax.sound.midi.Transmitter;
import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.Synthesizer;
import javax.sound.midi.ControllerEventListener;
import javax.sound.midi.MetaEventListener;
import javax.sound.midi.Track;
import java.io.IOException;
import javax.sound.midi.MidiSystem;
import java.io.InputStream;
import javax.sound.midi.InvalidMidiDataException;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.Receiver;
import java.util.ArrayList;
import java.util.List;
import javax.sound.midi.Sequence;
import java.util.Map;
import javax.sound.midi.Sequencer;

final class RealTimeSequencer extends AbstractMidiDevice implements Sequencer, AutoConnectSequencer
{
    private static final boolean DEBUG_PUMP = false;
    private static final boolean DEBUG_PUMP_ALL = false;
    private static final Map<ThreadGroup, EventDispatcher> dispatchers;
    static final RealTimeSequencerInfo info;
    private static final SyncMode[] masterSyncModes;
    private static final SyncMode[] slaveSyncModes;
    private static final SyncMode masterSyncMode;
    private static final SyncMode slaveSyncMode;
    private Sequence sequence;
    private double cacheTempoMPQ;
    private float cacheTempoFactor;
    private boolean[] trackMuted;
    private boolean[] trackSolo;
    private final MidiUtils.TempoCache tempoCache;
    private volatile boolean running;
    private PlayThread playThread;
    private volatile boolean recording;
    private final List recordingTracks;
    private long loopStart;
    private long loopEnd;
    private int loopCount;
    private final ArrayList metaEventListeners;
    private final ArrayList controllerEventListeners;
    private boolean autoConnect;
    private boolean doAutoConnectAtNextOpen;
    Receiver autoConnectedReceiver;
    
    RealTimeSequencer() throws MidiUnavailableException {
        super(RealTimeSequencer.info);
        this.sequence = null;
        this.cacheTempoMPQ = -1.0;
        this.cacheTempoFactor = -1.0f;
        this.trackMuted = null;
        this.trackSolo = null;
        this.tempoCache = new MidiUtils.TempoCache();
        this.recordingTracks = new ArrayList();
        this.loopStart = 0L;
        this.loopEnd = -1L;
        this.loopCount = 0;
        this.metaEventListeners = new ArrayList();
        this.controllerEventListeners = new ArrayList();
        this.autoConnect = false;
        this.doAutoConnectAtNextOpen = false;
        this.autoConnectedReceiver = null;
    }
    
    @Override
    public synchronized void setSequence(final Sequence sequence) throws InvalidMidiDataException {
        if (sequence != this.sequence) {
            if (this.sequence != null && sequence == null) {
                this.setCaches();
                this.stop();
                this.trackMuted = null;
                this.trackSolo = null;
                this.loopStart = 0L;
                this.loopEnd = -1L;
                this.loopCount = 0;
                if (this.getDataPump() != null) {
                    this.getDataPump().setTickPos(0L);
                    this.getDataPump().resetLoopCount();
                }
            }
            if (this.playThread != null) {
                this.playThread.setSequence(sequence);
            }
            if ((this.sequence = sequence) != null) {
                this.tempoCache.refresh(sequence);
                this.setTickPosition(0L);
                this.propagateCaches();
            }
        }
        else if (sequence != null) {
            this.tempoCache.refresh(sequence);
            if (this.playThread != null) {
                this.playThread.setSequence(sequence);
            }
        }
    }
    
    @Override
    public synchronized void setSequence(final InputStream inputStream) throws IOException, InvalidMidiDataException {
        if (inputStream == null) {
            this.setSequence((Sequence)null);
            return;
        }
        this.setSequence(MidiSystem.getSequence(inputStream));
    }
    
    @Override
    public Sequence getSequence() {
        return this.sequence;
    }
    
    @Override
    public synchronized void start() {
        if (!this.isOpen()) {
            throw new IllegalStateException("sequencer not open");
        }
        if (this.sequence == null) {
            throw new IllegalStateException("sequence not set");
        }
        if (this.running) {
            return;
        }
        this.implStart();
    }
    
    @Override
    public synchronized void stop() {
        if (!this.isOpen()) {
            throw new IllegalStateException("sequencer not open");
        }
        this.stopRecording();
        if (!this.running) {
            return;
        }
        this.implStop();
    }
    
    @Override
    public boolean isRunning() {
        return this.running;
    }
    
    @Override
    public void startRecording() {
        if (!this.isOpen()) {
            throw new IllegalStateException("Sequencer not open");
        }
        this.start();
        this.recording = true;
    }
    
    @Override
    public void stopRecording() {
        if (!this.isOpen()) {
            throw new IllegalStateException("Sequencer not open");
        }
        this.recording = false;
    }
    
    @Override
    public boolean isRecording() {
        return this.recording;
    }
    
    @Override
    public void recordEnable(final Track track, final int n) {
        if (!this.findTrack(track)) {
            throw new IllegalArgumentException("Track does not exist in the current sequence");
        }
        synchronized (this.recordingTracks) {
            final RecordingTrack value = RecordingTrack.get(this.recordingTracks, track);
            if (value != null) {
                value.channel = n;
            }
            else {
                this.recordingTracks.add(new RecordingTrack(track, n));
            }
        }
    }
    
    @Override
    public void recordDisable(final Track track) {
        synchronized (this.recordingTracks) {
            final RecordingTrack value = RecordingTrack.get(this.recordingTracks, track);
            if (value != null) {
                this.recordingTracks.remove(value);
            }
        }
    }
    
    private boolean findTrack(final Track track) {
        boolean b = false;
        if (this.sequence != null) {
            final Track[] tracks = this.sequence.getTracks();
            for (int i = 0; i < tracks.length; ++i) {
                if (track == tracks[i]) {
                    b = true;
                    break;
                }
            }
        }
        return b;
    }
    
    @Override
    public float getTempoInBPM() {
        return (float)MidiUtils.convertTempo(this.getTempoInMPQ());
    }
    
    @Override
    public void setTempoInBPM(float n) {
        if (n <= 0.0f) {
            n = 1.0f;
        }
        this.setTempoInMPQ((float)MidiUtils.convertTempo(n));
    }
    
    @Override
    public float getTempoInMPQ() {
        if (!this.needCaching()) {
            return this.getDataPump().getTempoMPQ();
        }
        if (this.cacheTempoMPQ != -1.0) {
            return (float)this.cacheTempoMPQ;
        }
        if (this.sequence != null) {
            return this.tempoCache.getTempoMPQAt(this.getTickPosition());
        }
        return 500000.0f;
    }
    
    @Override
    public void setTempoInMPQ(float tempoMPQ) {
        if (tempoMPQ <= 0.0f) {
            tempoMPQ = 1.0f;
        }
        if (this.needCaching()) {
            this.cacheTempoMPQ = tempoMPQ;
        }
        else {
            this.getDataPump().setTempoMPQ(tempoMPQ);
            this.cacheTempoMPQ = -1.0;
        }
    }
    
    @Override
    public void setTempoFactor(final float n) {
        if (n <= 0.0f) {
            return;
        }
        if (this.needCaching()) {
            this.cacheTempoFactor = n;
        }
        else {
            this.getDataPump().setTempoFactor(n);
            this.cacheTempoFactor = -1.0f;
        }
    }
    
    @Override
    public float getTempoFactor() {
        if (!this.needCaching()) {
            return this.getDataPump().getTempoFactor();
        }
        if (this.cacheTempoFactor != -1.0f) {
            return this.cacheTempoFactor;
        }
        return 1.0f;
    }
    
    @Override
    public long getTickLength() {
        if (this.sequence == null) {
            return 0L;
        }
        return this.sequence.getTickLength();
    }
    
    @Override
    public synchronized long getTickPosition() {
        if (this.getDataPump() == null || this.sequence == null) {
            return 0L;
        }
        return this.getDataPump().getTickPos();
    }
    
    @Override
    public synchronized void setTickPosition(final long tickPos) {
        if (tickPos < 0L) {
            return;
        }
        if (this.getDataPump() == null) {
            if (tickPos != 0L) {}
        }
        else if (this.sequence == null) {
            if (tickPos != 0L) {}
        }
        else {
            this.getDataPump().setTickPos(tickPos);
        }
    }
    
    @Override
    public long getMicrosecondLength() {
        if (this.sequence == null) {
            return 0L;
        }
        return this.sequence.getMicrosecondLength();
    }
    
    @Override
    public long getMicrosecondPosition() {
        if (this.getDataPump() == null || this.sequence == null) {
            return 0L;
        }
        synchronized (this.tempoCache) {
            return MidiUtils.tick2microsecond(this.sequence, this.getDataPump().getTickPos(), this.tempoCache);
        }
    }
    
    @Override
    public void setMicrosecondPosition(final long n) {
        if (n < 0L) {
            return;
        }
        if (this.getDataPump() == null) {
            if (n != 0L) {}
        }
        else if (this.sequence == null) {
            if (n != 0L) {}
        }
        else {
            synchronized (this.tempoCache) {
                this.setTickPosition(MidiUtils.microsecond2tick(this.sequence, n, this.tempoCache));
            }
        }
    }
    
    @Override
    public void setMasterSyncMode(final SyncMode syncMode) {
    }
    
    @Override
    public SyncMode getMasterSyncMode() {
        return RealTimeSequencer.masterSyncMode;
    }
    
    @Override
    public SyncMode[] getMasterSyncModes() {
        final SyncMode[] array = new SyncMode[RealTimeSequencer.masterSyncModes.length];
        System.arraycopy(RealTimeSequencer.masterSyncModes, 0, array, 0, RealTimeSequencer.masterSyncModes.length);
        return array;
    }
    
    @Override
    public void setSlaveSyncMode(final SyncMode syncMode) {
    }
    
    @Override
    public SyncMode getSlaveSyncMode() {
        return RealTimeSequencer.slaveSyncMode;
    }
    
    @Override
    public SyncMode[] getSlaveSyncModes() {
        final SyncMode[] array = new SyncMode[RealTimeSequencer.slaveSyncModes.length];
        System.arraycopy(RealTimeSequencer.slaveSyncModes, 0, array, 0, RealTimeSequencer.slaveSyncModes.length);
        return array;
    }
    
    int getTrackCount() {
        if (this.getSequence() != null) {
            return this.sequence.getTracks().length;
        }
        return 0;
    }
    
    @Override
    public synchronized void setTrackMute(final int n, final boolean b) {
        final int trackCount = this.getTrackCount();
        if (n < 0 || n >= this.getTrackCount()) {
            return;
        }
        (this.trackMuted = ensureBoolArraySize(this.trackMuted, trackCount))[n] = b;
        if (this.getDataPump() != null) {
            this.getDataPump().muteSoloChanged();
        }
    }
    
    @Override
    public synchronized boolean getTrackMute(final int n) {
        return n >= 0 && n < this.getTrackCount() && this.trackMuted != null && this.trackMuted.length > n && this.trackMuted[n];
    }
    
    @Override
    public synchronized void setTrackSolo(final int n, final boolean b) {
        final int trackCount = this.getTrackCount();
        if (n < 0 || n >= this.getTrackCount()) {
            return;
        }
        (this.trackSolo = ensureBoolArraySize(this.trackSolo, trackCount))[n] = b;
        if (this.getDataPump() != null) {
            this.getDataPump().muteSoloChanged();
        }
    }
    
    @Override
    public synchronized boolean getTrackSolo(final int n) {
        return n >= 0 && n < this.getTrackCount() && this.trackSolo != null && this.trackSolo.length > n && this.trackSolo[n];
    }
    
    @Override
    public boolean addMetaEventListener(final MetaEventListener metaEventListener) {
        synchronized (this.metaEventListeners) {
            if (!this.metaEventListeners.contains(metaEventListener)) {
                this.metaEventListeners.add(metaEventListener);
            }
            return true;
        }
    }
    
    @Override
    public void removeMetaEventListener(final MetaEventListener metaEventListener) {
        synchronized (this.metaEventListeners) {
            final int index = this.metaEventListeners.indexOf(metaEventListener);
            if (index >= 0) {
                this.metaEventListeners.remove(index);
            }
        }
    }
    
    @Override
    public int[] addControllerEventListener(final ControllerEventListener controllerEventListener, final int[] array) {
        synchronized (this.controllerEventListeners) {
            ControllerListElement controllerListElement = null;
            boolean b = false;
            for (int i = 0; i < this.controllerEventListeners.size(); ++i) {
                controllerListElement = (ControllerListElement)this.controllerEventListeners.get(i);
                if (controllerListElement.listener.equals(controllerEventListener)) {
                    controllerListElement.addControllers(array);
                    b = true;
                    break;
                }
            }
            if (!b) {
                controllerListElement = new ControllerListElement(controllerEventListener, array);
                this.controllerEventListeners.add(controllerListElement);
            }
            return controllerListElement.getControllers();
        }
    }
    
    @Override
    public int[] removeControllerEventListener(final ControllerEventListener controllerEventListener, final int[] array) {
        synchronized (this.controllerEventListeners) {
            ControllerListElement controllerListElement = null;
            boolean b = false;
            for (int i = 0; i < this.controllerEventListeners.size(); ++i) {
                controllerListElement = (ControllerListElement)this.controllerEventListeners.get(i);
                if (controllerListElement.listener.equals(controllerEventListener)) {
                    controllerListElement.removeControllers(array);
                    b = true;
                    break;
                }
            }
            if (!b) {
                return new int[0];
            }
            if (array == null) {
                final int index = this.controllerEventListeners.indexOf(controllerListElement);
                if (index >= 0) {
                    this.controllerEventListeners.remove(index);
                }
                return new int[0];
            }
            return controllerListElement.getControllers();
        }
    }
    
    @Override
    public void setLoopStartPoint(final long loopStart) {
        if (loopStart > this.getTickLength() || (this.loopEnd != -1L && loopStart > this.loopEnd) || loopStart < 0L) {
            throw new IllegalArgumentException("invalid loop start point: " + loopStart);
        }
        this.loopStart = loopStart;
    }
    
    @Override
    public long getLoopStartPoint() {
        return this.loopStart;
    }
    
    @Override
    public void setLoopEndPoint(final long loopEnd) {
        if (loopEnd > this.getTickLength() || (this.loopStart > loopEnd && loopEnd != -1L) || loopEnd < -1L) {
            throw new IllegalArgumentException("invalid loop end point: " + loopEnd);
        }
        this.loopEnd = loopEnd;
    }
    
    @Override
    public long getLoopEndPoint() {
        return this.loopEnd;
    }
    
    @Override
    public void setLoopCount(final int loopCount) {
        if (loopCount != -1 && loopCount < 0) {
            throw new IllegalArgumentException("illegal value for loop count: " + loopCount);
        }
        this.loopCount = loopCount;
        if (this.getDataPump() != null) {
            this.getDataPump().resetLoopCount();
        }
    }
    
    @Override
    public int getLoopCount() {
        return this.loopCount;
    }
    
    @Override
    protected void implOpen() throws MidiUnavailableException {
        this.playThread = new PlayThread();
        if (this.sequence != null) {
            this.playThread.setSequence(this.sequence);
        }
        this.propagateCaches();
        if (this.doAutoConnectAtNextOpen) {
            this.doAutoConnect();
        }
    }
    
    private void doAutoConnect() {
        Receiver receiver = null;
        try {
            final Synthesizer synthesizer = MidiSystem.getSynthesizer();
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
        catch (final Exception ex) {}
        if (receiver == null) {
            try {
                receiver = MidiSystem.getReceiver();
            }
            catch (final Exception ex2) {}
        }
        if (receiver != null) {
            this.autoConnectedReceiver = receiver;
            try {
                this.getTransmitter().setReceiver(receiver);
            }
            catch (final Exception ex3) {}
        }
    }
    
    private synchronized void propagateCaches() {
        if (this.sequence != null && this.isOpen()) {
            if (this.cacheTempoFactor != -1.0f) {
                this.setTempoFactor(this.cacheTempoFactor);
            }
            if (this.cacheTempoMPQ == -1.0) {
                this.setTempoInMPQ(new MidiUtils.TempoCache(this.sequence).getTempoMPQAt(this.getTickPosition()));
            }
            else {
                this.setTempoInMPQ((float)this.cacheTempoMPQ);
            }
        }
    }
    
    private synchronized void setCaches() {
        this.cacheTempoFactor = this.getTempoFactor();
        this.cacheTempoMPQ = this.getTempoInMPQ();
    }
    
    @Override
    protected synchronized void implClose() {
        if (this.playThread != null) {
            this.playThread.close();
            this.playThread = null;
        }
        super.implClose();
        this.sequence = null;
        this.running = false;
        this.cacheTempoMPQ = -1.0;
        this.cacheTempoFactor = -1.0f;
        this.trackMuted = null;
        this.trackSolo = null;
        this.loopStart = 0L;
        this.loopEnd = -1L;
        this.loopCount = 0;
        this.doAutoConnectAtNextOpen = this.autoConnect;
        if (this.autoConnectedReceiver != null) {
            try {
                this.autoConnectedReceiver.close();
            }
            catch (final Exception ex) {}
            this.autoConnectedReceiver = null;
        }
    }
    
    void implStart() {
        if (this.playThread == null) {
            return;
        }
        this.tempoCache.refresh(this.sequence);
        if (!this.running) {
            this.running = true;
            this.playThread.start();
        }
    }
    
    void implStop() {
        if (this.playThread == null) {
            return;
        }
        this.recording = false;
        if (this.running) {
            this.running = false;
            this.playThread.stop();
        }
    }
    
    private static EventDispatcher getEventDispatcher() {
        final ThreadGroup threadGroup = Thread.currentThread().getThreadGroup();
        synchronized (RealTimeSequencer.dispatchers) {
            EventDispatcher eventDispatcher = RealTimeSequencer.dispatchers.get(threadGroup);
            if (eventDispatcher == null) {
                eventDispatcher = new EventDispatcher();
                RealTimeSequencer.dispatchers.put(threadGroup, eventDispatcher);
                eventDispatcher.start();
            }
            return eventDispatcher;
        }
    }
    
    void sendMetaEvents(final MidiMessage midiMessage) {
        if (this.metaEventListeners.size() == 0) {
            return;
        }
        getEventDispatcher().sendAudioEvents(midiMessage, this.metaEventListeners);
    }
    
    void sendControllerEvents(final MidiMessage midiMessage) {
        final int size = this.controllerEventListeners.size();
        if (size == 0) {
            return;
        }
        if (!(midiMessage instanceof ShortMessage)) {
            return;
        }
        final int data1 = ((ShortMessage)midiMessage).getData1();
        final ArrayList list = new ArrayList();
        for (int i = 0; i < size; ++i) {
            final ControllerListElement controllerListElement = this.controllerEventListeners.get(i);
            for (int j = 0; j < controllerListElement.controllers.length; ++j) {
                if (controllerListElement.controllers[j] == data1) {
                    list.add(controllerListElement.listener);
                    break;
                }
            }
        }
        getEventDispatcher().sendAudioEvents(midiMessage, list);
    }
    
    private boolean needCaching() {
        return !this.isOpen() || this.sequence == null || this.playThread == null;
    }
    
    private DataPump getDataPump() {
        if (this.playThread != null) {
            return this.playThread.getDataPump();
        }
        return null;
    }
    
    private MidiUtils.TempoCache getTempoCache() {
        return this.tempoCache;
    }
    
    private static boolean[] ensureBoolArraySize(final boolean[] array, final int n) {
        if (array == null) {
            return new boolean[n];
        }
        if (array.length < n) {
            final boolean[] array2 = new boolean[n];
            System.arraycopy(array, 0, array2, 0, array.length);
            return array2;
        }
        return array;
    }
    
    @Override
    protected boolean hasReceivers() {
        return true;
    }
    
    @Override
    protected Receiver createReceiver() throws MidiUnavailableException {
        return new SequencerReceiver();
    }
    
    @Override
    protected boolean hasTransmitters() {
        return true;
    }
    
    @Override
    protected Transmitter createTransmitter() throws MidiUnavailableException {
        return new SequencerTransmitter();
    }
    
    @Override
    public void setAutoConnect(final Receiver autoConnectedReceiver) {
        this.autoConnect = (autoConnectedReceiver != null);
        this.autoConnectedReceiver = autoConnectedReceiver;
    }
    
    static {
        dispatchers = new WeakHashMap<ThreadGroup, EventDispatcher>();
        info = new RealTimeSequencerInfo();
        masterSyncModes = new SyncMode[] { SyncMode.INTERNAL_CLOCK };
        slaveSyncModes = new SyncMode[] { SyncMode.NO_SYNC };
        masterSyncMode = SyncMode.INTERNAL_CLOCK;
        slaveSyncMode = SyncMode.NO_SYNC;
    }
    
    private class SequencerTransmitter extends BasicTransmitter
    {
    }
    
    final class SequencerReceiver extends AbstractReceiver
    {
        @Override
        void implSend(MidiMessage midiMessage, final long n) {
            if (RealTimeSequencer.this.recording) {
                long n2 = 0L;
                if (n < 0L) {
                    n2 = RealTimeSequencer.this.getTickPosition();
                }
                else {
                    synchronized (RealTimeSequencer.this.tempoCache) {
                        n2 = MidiUtils.microsecond2tick(RealTimeSequencer.this.sequence, n, RealTimeSequencer.this.tempoCache);
                    }
                }
                Track track = null;
                if (midiMessage.getLength() > 1) {
                    if (midiMessage instanceof ShortMessage) {
                        final ShortMessage shortMessage = (ShortMessage)midiMessage;
                        if ((shortMessage.getStatus() & 0xF0) != 0xF0) {
                            track = RecordingTrack.get(RealTimeSequencer.this.recordingTracks, shortMessage.getChannel());
                        }
                    }
                    else {
                        track = RecordingTrack.get(RealTimeSequencer.this.recordingTracks, -1);
                    }
                    if (track != null) {
                        if (midiMessage instanceof ShortMessage) {
                            midiMessage = new FastShortMessage((ShortMessage)midiMessage);
                        }
                        else {
                            midiMessage = (MidiMessage)midiMessage.clone();
                        }
                        track.add(new MidiEvent(midiMessage, n2));
                    }
                }
            }
        }
    }
    
    private static class RealTimeSequencerInfo extends MidiDevice.Info
    {
        private static final String name = "Real Time Sequencer";
        private static final String vendor = "Oracle Corporation";
        private static final String description = "Software sequencer";
        private static final String version = "Version 1.0";
        
        private RealTimeSequencerInfo() {
            super("Real Time Sequencer", "Oracle Corporation", "Software sequencer", "Version 1.0");
        }
    }
    
    private class ControllerListElement
    {
        int[] controllers;
        final ControllerEventListener listener;
        
        private ControllerListElement(final ControllerEventListener listener, int[] controllers) {
            this.listener = listener;
            if (controllers == null) {
                controllers = new int[128];
                for (int i = 0; i < 128; ++i) {
                    controllers[i] = i;
                }
            }
            this.controllers = controllers;
        }
        
        private void addControllers(final int[] array) {
            if (array == null) {
                this.controllers = new int[128];
                for (int i = 0; i < 128; ++i) {
                    this.controllers[i] = i;
                }
                return;
            }
            final int[] array2 = new int[this.controllers.length + array.length];
            for (int j = 0; j < this.controllers.length; ++j) {
                array2[j] = this.controllers[j];
            }
            int length = this.controllers.length;
            for (int k = 0; k < array.length; ++k) {
                boolean b = false;
                for (int l = 0; l < this.controllers.length; ++l) {
                    if (array[k] == this.controllers[l]) {
                        b = true;
                        break;
                    }
                }
                if (!b) {
                    array2[length++] = array[k];
                }
            }
            final int[] controllers = new int[length];
            for (int n = 0; n < length; ++n) {
                controllers[n] = array2[n];
            }
            this.controllers = controllers;
        }
        
        private void removeControllers(final int[] array) {
            if (array == null) {
                this.controllers = new int[0];
            }
            else {
                final int[] array2 = new int[this.controllers.length];
                int n = 0;
                for (int i = 0; i < this.controllers.length; ++i) {
                    boolean b = false;
                    for (int j = 0; j < array.length; ++j) {
                        if (this.controllers[i] == array[j]) {
                            b = true;
                            break;
                        }
                    }
                    if (!b) {
                        array2[n++] = this.controllers[i];
                    }
                }
                final int[] controllers = new int[n];
                for (int k = 0; k < n; ++k) {
                    controllers[k] = array2[k];
                }
                this.controllers = controllers;
            }
        }
        
        private int[] getControllers() {
            if (this.controllers == null) {
                return null;
            }
            final int[] array = new int[this.controllers.length];
            for (int i = 0; i < this.controllers.length; ++i) {
                array[i] = this.controllers[i];
            }
            return array;
        }
    }
    
    static class RecordingTrack
    {
        private final Track track;
        private int channel;
        
        RecordingTrack(final Track track, final int channel) {
            this.track = track;
            this.channel = channel;
        }
        
        static RecordingTrack get(final List list, final Track track) {
            synchronized (list) {
                for (int size = list.size(), i = 0; i < size; ++i) {
                    final RecordingTrack recordingTrack = list.get(i);
                    if (recordingTrack.track == track) {
                        return recordingTrack;
                    }
                }
            }
            return null;
        }
        
        static Track get(final List list, final int n) {
            synchronized (list) {
                for (int size = list.size(), i = 0; i < size; ++i) {
                    final RecordingTrack recordingTrack = list.get(i);
                    if (recordingTrack.channel == n || recordingTrack.channel == -1) {
                        return recordingTrack.track;
                    }
                }
            }
            return null;
        }
    }
    
    final class PlayThread implements Runnable
    {
        private Thread thread;
        private final Object lock;
        boolean interrupted;
        boolean isPumping;
        private final DataPump dataPump;
        
        PlayThread() {
            this.lock = new Object();
            this.interrupted = false;
            this.isPumping = false;
            this.dataPump = new DataPump();
            this.thread = JSSecurityManager.createThread(this, "Java Sound Sequencer", false, 8, true);
        }
        
        DataPump getDataPump() {
            return this.dataPump;
        }
        
        synchronized void setSequence(final Sequence sequence) {
            this.dataPump.setSequence(sequence);
        }
        
        synchronized void start() {
            RealTimeSequencer.this.running = true;
            if (!this.dataPump.hasCachedTempo()) {
                this.dataPump.setTempoMPQ(RealTimeSequencer.this.tempoCache.getTempoMPQAt(RealTimeSequencer.this.getTickPosition()));
            }
            this.dataPump.checkPointMillis = 0L;
            this.dataPump.clearNoteOnCache();
            this.dataPump.needReindex = true;
            this.dataPump.resetLoopCount();
            synchronized (this.lock) {
                this.lock.notifyAll();
            }
        }
        
        synchronized void stop() {
            this.playThreadImplStop();
            final long n = System.nanoTime() / 1000000L;
            while (this.isPumping) {
                synchronized (this.lock) {
                    try {
                        this.lock.wait(2000L);
                    }
                    catch (final InterruptedException ex) {}
                }
                if (System.nanoTime() / 1000000L - n > 1900L) {
                    continue;
                }
            }
        }
        
        void playThreadImplStop() {
            RealTimeSequencer.this.running = false;
            synchronized (this.lock) {
                this.lock.notifyAll();
            }
        }
        
        void close() {
            Thread thread = null;
            synchronized (this) {
                this.interrupted = true;
                thread = this.thread;
                this.thread = null;
            }
            if (thread != null) {
                synchronized (this.lock) {
                    this.lock.notifyAll();
                }
            }
            if (thread != null) {
                try {
                    thread.join(2000L);
                }
                catch (final InterruptedException ex) {}
            }
        }
        
        @Override
        public void run() {
            while (!this.interrupted) {
                boolean pump = false;
                final boolean access$1100 = RealTimeSequencer.this.running;
                this.isPumping = (!this.interrupted && RealTimeSequencer.this.running);
                while (!pump && !this.interrupted && RealTimeSequencer.this.running) {
                    pump = this.dataPump.pump();
                    try {
                        Thread.sleep(1L);
                    }
                    catch (final InterruptedException ex) {}
                }
                this.playThreadImplStop();
                if (access$1100) {
                    this.dataPump.notesOff(true);
                }
                if (pump) {
                    this.dataPump.setTickPos(RealTimeSequencer.this.sequence.getTickLength());
                    final MetaMessage metaMessage = new MetaMessage();
                    try {
                        metaMessage.setMessage(47, new byte[0], 0);
                    }
                    catch (final InvalidMidiDataException ex2) {}
                    RealTimeSequencer.this.sendMetaEvents(metaMessage);
                }
                synchronized (this.lock) {
                    this.isPumping = false;
                    this.lock.notifyAll();
                    while (!RealTimeSequencer.this.running && !this.interrupted) {
                        try {
                            this.lock.wait();
                        }
                        catch (final Exception ex3) {}
                    }
                }
            }
        }
    }
    
    private class DataPump
    {
        private float currTempo;
        private float tempoFactor;
        private float inverseTempoFactor;
        private long ignoreTempoEventAt;
        private int resolution;
        private float divisionType;
        private long checkPointMillis;
        private long checkPointTick;
        private int[] noteOnCache;
        private Track[] tracks;
        private boolean[] trackDisabled;
        private int[] trackReadPos;
        private long lastTick;
        private boolean needReindex;
        private int currLoopCounter;
        
        DataPump() {
            this.needReindex = false;
            this.currLoopCounter = 0;
            this.init();
        }
        
        synchronized void init() {
            this.ignoreTempoEventAt = -1L;
            this.tempoFactor = 1.0f;
            this.inverseTempoFactor = 1.0f;
            this.noteOnCache = new int[128];
            this.tracks = null;
            this.trackDisabled = null;
        }
        
        synchronized void setTickPos(final long lastTick) {
            this.lastTick = lastTick;
            if (RealTimeSequencer.this.running) {
                this.notesOff(false);
            }
            if (RealTimeSequencer.this.running || lastTick > 0L) {
                this.chaseEvents(lastTick, lastTick);
            }
            else {
                this.needReindex = true;
            }
            if (!this.hasCachedTempo()) {
                this.setTempoMPQ(RealTimeSequencer.this.getTempoCache().getTempoMPQAt(this.lastTick, this.currTempo));
                this.ignoreTempoEventAt = -1L;
            }
            this.checkPointMillis = 0L;
        }
        
        long getTickPos() {
            return this.lastTick;
        }
        
        boolean hasCachedTempo() {
            if (this.ignoreTempoEventAt != this.lastTick) {
                this.ignoreTempoEventAt = -1L;
            }
            return this.ignoreTempoEventAt >= 0L;
        }
        
        synchronized void setTempoMPQ(final float currTempo) {
            if (currTempo > 0.0f && currTempo != this.currTempo) {
                this.ignoreTempoEventAt = this.lastTick;
                this.currTempo = currTempo;
                this.checkPointMillis = 0L;
            }
        }
        
        float getTempoMPQ() {
            return this.currTempo;
        }
        
        synchronized void setTempoFactor(final float tempoFactor) {
            if (tempoFactor > 0.0f && tempoFactor != this.tempoFactor) {
                this.tempoFactor = tempoFactor;
                this.inverseTempoFactor = 1.0f / tempoFactor;
                this.checkPointMillis = 0L;
            }
        }
        
        float getTempoFactor() {
            return this.tempoFactor;
        }
        
        synchronized void muteSoloChanged() {
            final boolean[] disabledArray = this.makeDisabledArray();
            if (RealTimeSequencer.this.running) {
                this.applyDisabledTracks(this.trackDisabled, disabledArray);
            }
            this.trackDisabled = disabledArray;
        }
        
        synchronized void setSequence(final Sequence sequence) {
            if (sequence == null) {
                this.init();
                return;
            }
            this.tracks = sequence.getTracks();
            this.muteSoloChanged();
            this.resolution = sequence.getResolution();
            this.divisionType = sequence.getDivisionType();
            this.trackReadPos = new int[this.tracks.length];
            this.checkPointMillis = 0L;
            this.needReindex = true;
        }
        
        synchronized void resetLoopCount() {
            this.currLoopCounter = RealTimeSequencer.this.loopCount;
        }
        
        void clearNoteOnCache() {
            for (int i = 0; i < 128; ++i) {
                this.noteOnCache[i] = 0;
            }
        }
        
        void notesOff(final boolean b) {
            int n = 0;
            for (int i = 0; i < 16; ++i) {
                final int n2 = 1 << i;
                for (int j = 0; j < 128; ++j) {
                    if ((this.noteOnCache[j] & n2) != 0x0) {
                        final int[] noteOnCache = this.noteOnCache;
                        final int n3 = j;
                        noteOnCache[n3] ^= n2;
                        RealTimeSequencer.this.getTransmitterList().sendMessage(0x90 | i | j << 8, -1L);
                        ++n;
                    }
                }
                RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | i | 0x7B00, -1L);
                RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | i | 0x4000, -1L);
                if (b) {
                    RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | i | 0x7900, -1L);
                    ++n;
                }
            }
        }
        
        private boolean[] makeDisabledArray() {
            if (this.tracks == null) {
                return null;
            }
            final boolean[] array = new boolean[this.tracks.length];
            final boolean[] access$1600;
            final boolean[] access$1601;
            synchronized (RealTimeSequencer.this) {
                access$1600 = RealTimeSequencer.this.trackMuted;
                access$1601 = RealTimeSequencer.this.trackSolo;
            }
            boolean b = false;
            if (access$1601 != null) {
                for (int i = 0; i < access$1601.length; ++i) {
                    if (access$1601[i]) {
                        b = true;
                        break;
                    }
                }
            }
            if (b) {
                for (int j = 0; j < array.length; ++j) {
                    array[j] = (j >= access$1601.length || !access$1601[j]);
                }
            }
            else {
                for (int k = 0; k < array.length; ++k) {
                    array[k] = (access$1600 != null && k < access$1600.length && access$1600[k]);
                }
            }
            return array;
        }
        
        private void sendNoteOffIfOn(final Track track, final long n) {
            final int size = track.size();
            int n2 = 0;
            try {
                for (int i = 0; i < size; ++i) {
                    final MidiEvent value = track.get(i);
                    if (value.getTick() > n) {
                        break;
                    }
                    final MidiMessage message = value.getMessage();
                    final int status = message.getStatus();
                    if (message.getLength() == 3 && (status & 0xF0) == 0x90) {
                        int data1 = -1;
                        if (message instanceof ShortMessage) {
                            final ShortMessage shortMessage = (ShortMessage)message;
                            if (shortMessage.getData2() > 0) {
                                data1 = shortMessage.getData1();
                            }
                        }
                        else {
                            final byte[] message2 = message.getMessage();
                            if ((message2[2] & 0x7F) > 0) {
                                data1 = (message2[1] & 0x7F);
                            }
                        }
                        if (data1 >= 0) {
                            final int n3 = 1 << (status & 0xF);
                            if ((this.noteOnCache[data1] & n3) != 0x0) {
                                RealTimeSequencer.this.getTransmitterList().sendMessage(status | data1 << 8, -1L);
                                final int[] noteOnCache = this.noteOnCache;
                                final int n4 = data1;
                                noteOnCache[n4] &= (0xFFFF ^ n3);
                                ++n2;
                            }
                        }
                    }
                }
            }
            catch (final ArrayIndexOutOfBoundsException ex) {}
        }
        
        private void applyDisabledTracks(final boolean[] array, final boolean[] array2) {
            byte[][] array3 = null;
            synchronized (RealTimeSequencer.this) {
                for (int i = 0; i < array2.length; ++i) {
                    if ((array == null || i >= array.length || !array[i]) && array2[i]) {
                        if (this.tracks.length > i) {
                            this.sendNoteOffIfOn(this.tracks[i], this.lastTick);
                        }
                    }
                    else if (array != null && i < array.length && array[i] && !array2[i]) {
                        if (array3 == null) {
                            array3 = new byte[128][16];
                        }
                        this.chaseTrackEvents(i, 0L, this.lastTick, true, array3);
                    }
                }
            }
        }
        
        private void chaseTrackEvents(final int n, long n2, final long n3, final boolean b, final byte[][] array) {
            if (n2 > n3) {
                n2 = 0L;
            }
            final byte[] array2 = new byte[16];
            for (int i = 0; i < 16; ++i) {
                array2[i] = -1;
                for (int j = 0; j < 128; ++j) {
                    array[j][i] = -1;
                }
            }
            final Track track = this.tracks[n];
            final int size = track.size();
            try {
                int k = 0;
                while (k < size) {
                    final MidiEvent value = track.get(k);
                    if (value.getTick() >= n3) {
                        if (b && n < this.trackReadPos.length) {
                            this.trackReadPos[n] = ((k > 0) ? (k - 1) : 0);
                            break;
                        }
                        break;
                    }
                    else {
                        final MidiMessage message = value.getMessage();
                        final int status = message.getStatus();
                        final int length = message.getLength();
                        if (length == 3 && (status & 0xF0) == 0xB0) {
                            if (message instanceof ShortMessage) {
                                final ShortMessage shortMessage = (ShortMessage)message;
                                array[shortMessage.getData1() & 0x7F][status & 0xF] = (byte)shortMessage.getData2();
                            }
                            else {
                                final byte[] message2 = message.getMessage();
                                array[message2[1] & 0x7F][status & 0xF] = message2[2];
                            }
                        }
                        if (length == 2 && (status & 0xF0) == 0xC0) {
                            if (message instanceof ShortMessage) {
                                array2[status & 0xF] = (byte)((ShortMessage)message).getData1();
                            }
                            else {
                                array2[status & 0xF] = message.getMessage()[1];
                            }
                        }
                        ++k;
                    }
                }
            }
            catch (final ArrayIndexOutOfBoundsException ex) {}
            int n4 = 0;
            for (int l = 0; l < 16; ++l) {
                for (int n5 = 0; n5 < 128; ++n5) {
                    final byte b2 = array[n5][l];
                    if (b2 >= 0) {
                        RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | l | n5 << 8 | b2 << 16, -1L);
                        ++n4;
                    }
                }
                if (array2[l] >= 0) {
                    RealTimeSequencer.this.getTransmitterList().sendMessage(0xC0 | l | array2[l] << 8, -1L);
                }
                if (array2[l] >= 0 || n2 == 0L || n3 == 0L) {
                    RealTimeSequencer.this.getTransmitterList().sendMessage(0xE0 | l | 0x400000, -1L);
                    RealTimeSequencer.this.getTransmitterList().sendMessage(0xB0 | l | 0x4000, -1L);
                }
            }
        }
        
        synchronized void chaseEvents(final long n, final long n2) {
            final byte[][] array = new byte[128][16];
            for (int i = 0; i < this.tracks.length; ++i) {
                if (this.trackDisabled == null || this.trackDisabled.length <= i || !this.trackDisabled[i]) {
                    this.chaseTrackEvents(i, n, n2, true, array);
                }
            }
        }
        
        private long getCurrentTimeMillis() {
            return System.nanoTime() / 1000000L;
        }
        
        private long millis2tick(final long n) {
            if (this.divisionType != 0.0f) {
                return (long)(n * (double)this.tempoFactor * this.divisionType * this.resolution / 1000.0);
            }
            return MidiUtils.microsec2ticks(n * 1000L, this.currTempo * this.inverseTempoFactor, this.resolution);
        }
        
        private long tick2millis(final long n) {
            if (this.divisionType != 0.0f) {
                return (long)(n * 1000.0 / (this.tempoFactor * (double)this.divisionType * this.resolution));
            }
            return MidiUtils.ticks2microsec(n, this.currTempo * this.inverseTempoFactor, this.resolution) / 1000L;
        }
        
        private void ReindexTrack(final int n, final long n2) {
            if (n < this.trackReadPos.length && n < this.tracks.length) {
                this.trackReadPos[n] = MidiUtils.tick2index(this.tracks[n], n2);
            }
        }
        
        private boolean dispatchMessage(final int n, final MidiEvent midiEvent) {
            boolean b = false;
            final MidiMessage message = midiEvent.getMessage();
            final int status = message.getStatus();
            final int length = message.getLength();
            if (status == 255 && length >= 2) {
                if (n == 0) {
                    final int tempoMPQ = MidiUtils.getTempoMPQ(message);
                    if (tempoMPQ > 0) {
                        if (midiEvent.getTick() != this.ignoreTempoEventAt) {
                            this.setTempoMPQ((float)tempoMPQ);
                            b = true;
                        }
                        this.ignoreTempoEventAt = -1L;
                    }
                }
                RealTimeSequencer.this.sendMetaEvents(message);
            }
            else {
                RealTimeSequencer.this.getTransmitterList().sendMessage(message, -1L);
                switch (status & 0xF0) {
                    case 128: {
                        final int n2 = ((ShortMessage)message).getData1() & 0x7F;
                        final int[] noteOnCache = this.noteOnCache;
                        final int n3 = n2;
                        noteOnCache[n3] &= (0xFFFF ^ 1 << (status & 0xF));
                        break;
                    }
                    case 144: {
                        final ShortMessage shortMessage = (ShortMessage)message;
                        final int n4 = shortMessage.getData1() & 0x7F;
                        if ((shortMessage.getData2() & 0x7F) > 0) {
                            final int[] noteOnCache2 = this.noteOnCache;
                            final int n5 = n4;
                            noteOnCache2[n5] |= 1 << (status & 0xF);
                            break;
                        }
                        final int[] noteOnCache3 = this.noteOnCache;
                        final int n6 = n4;
                        noteOnCache3[n6] &= (0xFFFF ^ 1 << (status & 0xF));
                        break;
                    }
                    case 176: {
                        RealTimeSequencer.this.sendControllerEvents(message);
                        break;
                    }
                }
            }
            return b;
        }
        
        synchronized boolean pump() {
            long n = this.lastTick;
            int n2 = 0;
            long checkPointMillis = this.getCurrentTimeMillis();
            int i;
            boolean b2;
            do {
                i = 0;
                if (this.needReindex) {
                    if (this.trackReadPos.length < this.tracks.length) {
                        this.trackReadPos = new int[this.tracks.length];
                    }
                    for (int j = 0; j < this.tracks.length; ++j) {
                        this.ReindexTrack(j, n);
                    }
                    this.needReindex = false;
                    this.checkPointMillis = 0L;
                }
                if (this.checkPointMillis == 0L) {
                    checkPointMillis = this.getCurrentTimeMillis();
                    this.checkPointMillis = checkPointMillis;
                    n = this.lastTick;
                    this.checkPointTick = n;
                }
                else {
                    n = this.checkPointTick + this.millis2tick(checkPointMillis - this.checkPointMillis);
                    if (RealTimeSequencer.this.loopEnd != -1L && ((RealTimeSequencer.this.loopCount > 0 && this.currLoopCounter > 0) || RealTimeSequencer.this.loopCount == -1) && this.lastTick <= RealTimeSequencer.this.loopEnd && n >= RealTimeSequencer.this.loopEnd) {
                        n = RealTimeSequencer.this.loopEnd - 1L;
                        n2 = 1;
                    }
                    this.lastTick = n;
                }
                int n3 = 0;
                for (int k = 0; k < this.tracks.length; ++k) {
                    try {
                        final boolean b = this.trackDisabled[k];
                        final Track track = this.tracks[k];
                        int n4 = this.trackReadPos[k];
                        final int size = track.size();
                        MidiEvent value;
                        while (i == 0 && n4 < size && (value = track.get(n4)).getTick() <= n) {
                            if (n4 == size - 1 && MidiUtils.isMetaEndOfTrack(value.getMessage())) {
                                n4 = size;
                                break;
                            }
                            ++n4;
                            if (b && (k != 0 || !MidiUtils.isMetaTempo(value.getMessage()))) {
                                continue;
                            }
                            i = (this.dispatchMessage(k, value) ? 1 : 0);
                        }
                        if (n4 >= size) {
                            ++n3;
                        }
                        this.trackReadPos[k] = n4;
                    }
                    catch (final Exception ex) {
                        if (ex instanceof ArrayIndexOutOfBoundsException) {
                            this.needReindex = true;
                            i = 1;
                        }
                    }
                    if (i != 0) {
                        break;
                    }
                }
                b2 = (n3 == this.tracks.length);
                if (n2 != 0 || (((RealTimeSequencer.this.loopCount > 0 && this.currLoopCounter > 0) || RealTimeSequencer.this.loopCount == -1) && i == 0 && RealTimeSequencer.this.loopEnd == -1L && b2)) {
                    final long checkPointMillis2 = this.checkPointMillis;
                    long n5 = RealTimeSequencer.this.loopEnd;
                    if (n5 == -1L) {
                        n5 = this.lastTick;
                    }
                    if (RealTimeSequencer.this.loopCount != -1) {
                        --this.currLoopCounter;
                    }
                    this.setTickPos(RealTimeSequencer.this.loopStart);
                    this.checkPointMillis = checkPointMillis2 + this.tick2millis(n5 - this.checkPointTick);
                    this.checkPointTick = RealTimeSequencer.this.loopStart;
                    this.needReindex = false;
                    i = 0;
                    n2 = 0;
                    b2 = false;
                }
            } while (i != 0);
            return b2;
        }
    }
}
