package com.sun.media.sound;

import javax.sound.midi.Patch;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.MidiChannel;

public final class SoftChannel implements MidiChannel, ModelDirectedPlayer
{
    private static boolean[] dontResetControls;
    private static final int RPN_NULL_VALUE = 16383;
    private int rpn_control;
    private int nrpn_control;
    double portamento_time;
    int[] portamento_lastnote;
    int portamento_lastnote_ix;
    private boolean portamento;
    private boolean mono;
    private boolean mute;
    private boolean solo;
    private boolean solomute;
    private final Object control_mutex;
    private int channel;
    private SoftVoice[] voices;
    private int bank;
    private int program;
    private SoftSynthesizer synthesizer;
    private SoftMainMixer mainmixer;
    private int[] polypressure;
    private int channelpressure;
    private int[] controller;
    private int pitchbend;
    private double[] co_midi_pitch;
    private double[] co_midi_channel_pressure;
    SoftTuning tuning;
    int tuning_bank;
    int tuning_program;
    SoftInstrument current_instrument;
    ModelChannelMixer current_mixer;
    ModelDirector current_director;
    int cds_control_number;
    ModelConnectionBlock[] cds_control_connections;
    ModelConnectionBlock[] cds_channelpressure_connections;
    ModelConnectionBlock[] cds_polypressure_connections;
    boolean sustain;
    boolean[][] keybasedcontroller_active;
    double[][] keybasedcontroller_value;
    private SoftControl[] co_midi;
    private double[][] co_midi_cc_cc;
    private SoftControl co_midi_cc;
    Map<Integer, int[]> co_midi_rpn_rpn_i;
    Map<Integer, double[]> co_midi_rpn_rpn;
    private SoftControl co_midi_rpn;
    Map<Integer, int[]> co_midi_nrpn_nrpn_i;
    Map<Integer, double[]> co_midi_nrpn_nrpn;
    private SoftControl co_midi_nrpn;
    private int[] lastVelocity;
    private int prevVoiceID;
    private boolean firstVoice;
    private int voiceNo;
    private int play_noteNumber;
    private int play_velocity;
    private int play_delay;
    private boolean play_releasetriggered;
    
    private static int restrict7Bit(final int n) {
        if (n < 0) {
            return 0;
        }
        if (n > 127) {
            return 127;
        }
        return n;
    }
    
    private static int restrict14Bit(final int n) {
        if (n < 0) {
            return 0;
        }
        if (n > 16256) {
            return 16256;
        }
        return n;
    }
    
    public SoftChannel(final SoftSynthesizer synthesizer, final int channel) {
        this.rpn_control = 16383;
        this.nrpn_control = 16383;
        this.portamento_time = 1.0;
        this.portamento_lastnote = new int[128];
        this.portamento_lastnote_ix = 0;
        this.portamento = false;
        this.mono = false;
        this.mute = false;
        this.solo = false;
        this.solomute = false;
        this.polypressure = new int[128];
        this.channelpressure = 0;
        this.controller = new int[128];
        this.co_midi_pitch = new double[1];
        this.co_midi_channel_pressure = new double[1];
        this.tuning = new SoftTuning();
        this.tuning_bank = 0;
        this.tuning_program = 0;
        this.current_instrument = null;
        this.current_mixer = null;
        this.current_director = null;
        this.cds_control_number = -1;
        this.cds_control_connections = null;
        this.cds_channelpressure_connections = null;
        this.cds_polypressure_connections = null;
        this.sustain = false;
        this.keybasedcontroller_active = null;
        this.keybasedcontroller_value = null;
        this.co_midi = new SoftControl[128];
        for (int i = 0; i < this.co_midi.length; ++i) {
            this.co_midi[i] = new MidiControlObject();
        }
        this.co_midi_cc_cc = new double[128][1];
        this.co_midi_cc = new SoftControl() {
            double[][] cc = SoftChannel.this.co_midi_cc_cc;
            
            @Override
            public double[] get(final int n, final String s) {
                if (s == null) {
                    return null;
                }
                return this.cc[Integer.parseInt(s)];
            }
        };
        this.co_midi_rpn_rpn_i = new HashMap<Integer, int[]>();
        this.co_midi_rpn_rpn = new HashMap<Integer, double[]>();
        this.co_midi_rpn = new SoftControl() {
            Map<Integer, double[]> rpn = SoftChannel.this.co_midi_rpn_rpn;
            
            @Override
            public double[] get(final int n, final String s) {
                if (s == null) {
                    return null;
                }
                final int int1 = Integer.parseInt(s);
                double[] array = this.rpn.get(int1);
                if (array == null) {
                    array = new double[] { 0.0 };
                    this.rpn.put(int1, array);
                }
                return array;
            }
        };
        this.co_midi_nrpn_nrpn_i = new HashMap<Integer, int[]>();
        this.co_midi_nrpn_nrpn = new HashMap<Integer, double[]>();
        this.co_midi_nrpn = new SoftControl() {
            Map<Integer, double[]> nrpn = SoftChannel.this.co_midi_nrpn_nrpn;
            
            @Override
            public double[] get(final int n, final String s) {
                if (s == null) {
                    return null;
                }
                final int int1 = Integer.parseInt(s);
                double[] array = this.nrpn.get(int1);
                if (array == null) {
                    array = new double[] { 0.0 };
                    this.nrpn.put(int1, array);
                }
                return array;
            }
        };
        this.lastVelocity = new int[128];
        this.firstVoice = true;
        this.voiceNo = 0;
        this.play_noteNumber = 0;
        this.play_velocity = 0;
        this.play_delay = 0;
        this.play_releasetriggered = false;
        this.channel = channel;
        this.voices = synthesizer.getVoices();
        this.synthesizer = synthesizer;
        this.mainmixer = synthesizer.getMainMixer();
        this.control_mutex = synthesizer.control_mutex;
        this.resetAllControllers(true);
    }
    
    private int findFreeVoice(final int n) {
        if (n == -1) {
            return -1;
        }
        for (int i = n; i < this.voices.length; ++i) {
            if (!this.voices[i].active) {
                return i;
            }
        }
        if (this.synthesizer.getVoiceAllocationMode() == 1) {
            int n2 = this.channel;
            for (int j = 0; j < this.voices.length; ++j) {
                if (this.voices[j].stealer_channel == null) {
                    if (n2 == 9) {
                        n2 = this.voices[j].channel;
                    }
                    else if (this.voices[j].channel != 9 && this.voices[j].channel > n2) {
                        n2 = this.voices[j].channel;
                    }
                }
            }
            int n3 = -1;
            SoftVoice softVoice = null;
            for (int k = 0; k < this.voices.length; ++k) {
                if (this.voices[k].channel == n2 && this.voices[k].stealer_channel == null && !this.voices[k].on) {
                    if (softVoice == null) {
                        softVoice = this.voices[k];
                        n3 = k;
                    }
                    if (this.voices[k].voiceID < softVoice.voiceID) {
                        softVoice = this.voices[k];
                        n3 = k;
                    }
                }
            }
            if (n3 == -1) {
                for (int l = 0; l < this.voices.length; ++l) {
                    if (this.voices[l].channel == n2 && this.voices[l].stealer_channel == null) {
                        if (softVoice == null) {
                            softVoice = this.voices[l];
                            n3 = l;
                        }
                        if (this.voices[l].voiceID < softVoice.voiceID) {
                            softVoice = this.voices[l];
                            n3 = l;
                        }
                    }
                }
            }
            return n3;
        }
        int n4 = -1;
        SoftVoice softVoice2 = null;
        for (int n5 = 0; n5 < this.voices.length; ++n5) {
            if (this.voices[n5].stealer_channel == null && !this.voices[n5].on) {
                if (softVoice2 == null) {
                    softVoice2 = this.voices[n5];
                    n4 = n5;
                }
                if (this.voices[n5].voiceID < softVoice2.voiceID) {
                    softVoice2 = this.voices[n5];
                    n4 = n5;
                }
            }
        }
        if (n4 == -1) {
            for (int n6 = 0; n6 < this.voices.length; ++n6) {
                if (this.voices[n6].stealer_channel == null) {
                    if (softVoice2 == null) {
                        softVoice2 = this.voices[n6];
                        n4 = n6;
                    }
                    if (this.voices[n6].voiceID < softVoice2.voiceID) {
                        softVoice2 = this.voices[n6];
                        n4 = n6;
                    }
                }
            }
        }
        return n4;
    }
    
    void initVoice(final SoftVoice softVoice, final SoftPerformer softPerformer, final int n, final int stealer_noteNumber, final int stealer_velocity, final int n2, final ModelConnectionBlock[] array, final ModelChannelMixer modelChannelMixer, final boolean b) {
        if (softVoice.active) {
            softVoice.stealer_channel = this;
            softVoice.stealer_performer = softPerformer;
            softVoice.stealer_voiceID = n;
            softVoice.stealer_noteNumber = stealer_noteNumber;
            softVoice.stealer_velocity = stealer_velocity;
            softVoice.stealer_extendedConnectionBlocks = array;
            softVoice.stealer_channelmixer = modelChannelMixer;
            softVoice.stealer_releaseTriggered = b;
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].active && this.voices[i].voiceID == softVoice.voiceID) {
                    this.voices[i].soundOff();
                }
            }
            return;
        }
        softVoice.extendedConnectionBlocks = array;
        softVoice.channelmixer = modelChannelMixer;
        softVoice.releaseTriggered = b;
        softVoice.voiceID = n;
        softVoice.tuning = this.tuning;
        softVoice.exclusiveClass = softPerformer.exclusiveClass;
        softVoice.softchannel = this;
        softVoice.channel = this.channel;
        softVoice.bank = this.bank;
        softVoice.program = this.program;
        softVoice.instrument = this.current_instrument;
        softVoice.performer = softPerformer;
        softVoice.objects.clear();
        softVoice.objects.put("midi", this.co_midi[stealer_noteNumber]);
        softVoice.objects.put("midi_cc", this.co_midi_cc);
        softVoice.objects.put("midi_rpn", this.co_midi_rpn);
        softVoice.objects.put("midi_nrpn", this.co_midi_nrpn);
        softVoice.noteOn(stealer_noteNumber, stealer_velocity, n2);
        softVoice.setMute(this.mute);
        softVoice.setSoloMute(this.solomute);
        if (b) {
            return;
        }
        if (this.controller[84] != 0) {
            softVoice.co_noteon_keynumber[0] = this.tuning.getTuning(this.controller[84]) / 100.0 * 0.0078125;
            softVoice.portamento = true;
            this.controlChange(84, 0);
        }
        else if (this.portamento) {
            if (this.mono) {
                if (this.portamento_lastnote[0] != -1) {
                    softVoice.co_noteon_keynumber[0] = this.tuning.getTuning(this.portamento_lastnote[0]) / 100.0 * 0.0078125;
                    softVoice.portamento = true;
                    this.controlChange(84, 0);
                }
                this.portamento_lastnote[0] = stealer_noteNumber;
            }
            else if (this.portamento_lastnote_ix != 0) {
                --this.portamento_lastnote_ix;
                softVoice.co_noteon_keynumber[0] = this.tuning.getTuning(this.portamento_lastnote[this.portamento_lastnote_ix]) / 100.0 * 0.0078125;
                softVoice.portamento = true;
            }
        }
    }
    
    @Override
    public void noteOn(final int n, final int n2) {
        this.noteOn(n, n2, 0);
    }
    
    void noteOn(int restrict7Bit, int restrict7Bit2, final int n) {
        restrict7Bit = restrict7Bit(restrict7Bit);
        restrict7Bit2 = restrict7Bit(restrict7Bit2);
        this.noteOn_internal(restrict7Bit, restrict7Bit2, n);
        if (this.current_mixer != null) {
            this.current_mixer.noteOn(restrict7Bit, restrict7Bit2);
        }
    }
    
    private void noteOn_internal(final int play_noteNumber, final int play_velocity, final int play_delay) {
        if (play_velocity == 0) {
            this.noteOff_internal(play_noteNumber, 64);
            return;
        }
        synchronized (this.control_mutex) {
            if (this.sustain) {
                this.sustain = false;
                for (int i = 0; i < this.voices.length; ++i) {
                    if ((this.voices[i].sustain || this.voices[i].on) && this.voices[i].channel == this.channel && this.voices[i].active && this.voices[i].note == play_noteNumber) {
                        this.voices[i].sustain = false;
                        this.voices[i].on = true;
                        this.voices[i].noteOff(0);
                    }
                }
                this.sustain = true;
            }
            this.mainmixer.activity();
            if (this.mono) {
                if (this.portamento) {
                    boolean b = false;
                    for (int j = 0; j < this.voices.length; ++j) {
                        if (this.voices[j].on && this.voices[j].channel == this.channel && this.voices[j].active && !this.voices[j].releaseTriggered) {
                            this.voices[j].portamento = true;
                            this.voices[j].setNote(play_noteNumber);
                            b = true;
                        }
                    }
                    if (b) {
                        this.portamento_lastnote[0] = play_noteNumber;
                        return;
                    }
                }
                if (this.controller[84] != 0) {
                    boolean b2 = false;
                    for (int k = 0; k < this.voices.length; ++k) {
                        if (this.voices[k].on && this.voices[k].channel == this.channel && this.voices[k].active && this.voices[k].note == this.controller[84] && !this.voices[k].releaseTriggered) {
                            this.voices[k].portamento = true;
                            this.voices[k].setNote(play_noteNumber);
                            b2 = true;
                        }
                    }
                    this.controlChange(84, 0);
                    if (b2) {
                        return;
                    }
                }
            }
            if (this.mono) {
                this.allNotesOff();
            }
            if (this.current_instrument == null) {
                this.current_instrument = this.synthesizer.findInstrument(this.program, this.bank, this.channel);
                if (this.current_instrument == null) {
                    return;
                }
                if (this.current_mixer != null) {
                    this.mainmixer.stopMixer(this.current_mixer);
                }
                this.current_mixer = this.current_instrument.getSourceInstrument().getChannelMixer(this, this.synthesizer.getFormat());
                if (this.current_mixer != null) {
                    this.mainmixer.registerMixer(this.current_mixer);
                }
                this.current_director = this.current_instrument.getDirector(this, this);
                this.applyInstrumentCustomization();
            }
            this.prevVoiceID = this.synthesizer.voiceIDCounter++;
            this.firstVoice = true;
            this.voiceNo = 0;
            final int n = (int)Math.round(this.tuning.getTuning(play_noteNumber) / 100.0);
            this.play_noteNumber = play_noteNumber;
            this.play_velocity = play_velocity;
            this.play_delay = play_delay;
            this.play_releasetriggered = false;
            this.lastVelocity[play_noteNumber] = play_velocity;
            this.current_director.noteOn(n, play_velocity);
        }
    }
    
    @Override
    public void noteOff(int restrict7Bit, int restrict7Bit2) {
        restrict7Bit = restrict7Bit(restrict7Bit);
        restrict7Bit2 = restrict7Bit(restrict7Bit2);
        this.noteOff_internal(restrict7Bit, restrict7Bit2);
        if (this.current_mixer != null) {
            this.current_mixer.noteOff(restrict7Bit, restrict7Bit2);
        }
    }
    
    private void noteOff_internal(final int play_noteNumber, final int n) {
        synchronized (this.control_mutex) {
            if (!this.mono && this.portamento && this.portamento_lastnote_ix != 127) {
                this.portamento_lastnote[this.portamento_lastnote_ix] = play_noteNumber;
                ++this.portamento_lastnote_ix;
            }
            this.mainmixer.activity();
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].on && this.voices[i].channel == this.channel && this.voices[i].note == play_noteNumber && !this.voices[i].releaseTriggered) {
                    this.voices[i].noteOff(n);
                }
                if (this.voices[i].stealer_channel == this && this.voices[i].stealer_noteNumber == play_noteNumber) {
                    final SoftVoice softVoice = this.voices[i];
                    softVoice.stealer_releaseTriggered = false;
                    softVoice.stealer_channel = null;
                    softVoice.stealer_performer = null;
                    softVoice.stealer_voiceID = -1;
                    softVoice.stealer_noteNumber = 0;
                    softVoice.stealer_velocity = 0;
                    softVoice.stealer_extendedConnectionBlocks = null;
                    softVoice.stealer_channelmixer = null;
                }
            }
            if (this.current_instrument == null) {
                this.current_instrument = this.synthesizer.findInstrument(this.program, this.bank, this.channel);
                if (this.current_instrument == null) {
                    return;
                }
                if (this.current_mixer != null) {
                    this.mainmixer.stopMixer(this.current_mixer);
                }
                this.current_mixer = this.current_instrument.getSourceInstrument().getChannelMixer(this, this.synthesizer.getFormat());
                if (this.current_mixer != null) {
                    this.mainmixer.registerMixer(this.current_mixer);
                }
                this.current_director = this.current_instrument.getDirector(this, this);
                this.applyInstrumentCustomization();
            }
            this.prevVoiceID = this.synthesizer.voiceIDCounter++;
            this.firstVoice = true;
            this.voiceNo = 0;
            final int n2 = (int)Math.round(this.tuning.getTuning(play_noteNumber) / 100.0);
            this.play_noteNumber = play_noteNumber;
            this.play_velocity = this.lastVelocity[play_noteNumber];
            this.play_releasetriggered = true;
            this.play_delay = 0;
            this.current_director.noteOff(n2, n);
        }
    }
    
    @Override
    public void play(final int n, final ModelConnectionBlock[] array) {
        final int play_noteNumber = this.play_noteNumber;
        final int play_velocity = this.play_velocity;
        final int play_delay = this.play_delay;
        final boolean play_releasetriggered = this.play_releasetriggered;
        final SoftPerformer performer = this.current_instrument.getPerformer(n);
        if (this.firstVoice) {
            this.firstVoice = false;
            if (performer.exclusiveClass != 0) {
                final int exclusiveClass = performer.exclusiveClass;
                for (int i = 0; i < this.voices.length; ++i) {
                    if (this.voices[i].active && this.voices[i].channel == this.channel && this.voices[i].exclusiveClass == exclusiveClass && (!performer.selfNonExclusive || this.voices[i].note != play_noteNumber)) {
                        this.voices[i].shutdown();
                    }
                }
            }
        }
        this.voiceNo = this.findFreeVoice(this.voiceNo);
        if (this.voiceNo == -1) {
            return;
        }
        this.initVoice(this.voices[this.voiceNo], performer, this.prevVoiceID, play_noteNumber, play_velocity, play_delay, array, this.current_mixer, play_releasetriggered);
    }
    
    @Override
    public void noteOff(final int n) {
        if (n < 0 || n > 127) {
            return;
        }
        this.noteOff_internal(n, 64);
    }
    
    @Override
    public void setPolyPressure(int restrict7Bit, int restrict7Bit2) {
        restrict7Bit = restrict7Bit(restrict7Bit);
        restrict7Bit2 = restrict7Bit(restrict7Bit2);
        if (this.current_mixer != null) {
            this.current_mixer.setPolyPressure(restrict7Bit, restrict7Bit2);
        }
        synchronized (this.control_mutex) {
            this.mainmixer.activity();
            this.co_midi[restrict7Bit].get(0, "poly_pressure")[0] = restrict7Bit2 * 0.0078125;
            this.polypressure[restrict7Bit] = restrict7Bit2;
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].active && this.voices[i].note == restrict7Bit) {
                    this.voices[i].setPolyPressure(restrict7Bit2);
                }
            }
        }
    }
    
    @Override
    public int getPolyPressure(final int n) {
        synchronized (this.control_mutex) {
            return this.polypressure[n];
        }
    }
    
    @Override
    public void setChannelPressure(int restrict7Bit) {
        restrict7Bit = restrict7Bit(restrict7Bit);
        if (this.current_mixer != null) {
            this.current_mixer.setChannelPressure(restrict7Bit);
        }
        synchronized (this.control_mutex) {
            this.mainmixer.activity();
            this.co_midi_channel_pressure[0] = restrict7Bit * 0.0078125;
            this.channelpressure = restrict7Bit;
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].active) {
                    this.voices[i].setChannelPressure(restrict7Bit);
                }
            }
        }
    }
    
    @Override
    public int getChannelPressure() {
        synchronized (this.control_mutex) {
            return this.channelpressure;
        }
    }
    
    void applyInstrumentCustomization() {
        if (this.cds_control_connections == null && this.cds_channelpressure_connections == null && this.cds_polypressure_connections == null) {
            return;
        }
        final ModelInstrument sourceInstrument = this.current_instrument.getSourceInstrument();
        final ModelPerformer[] performers = sourceInstrument.getPerformers();
        final ModelPerformer[] array = new ModelPerformer[performers.length];
        for (int i = 0; i < array.length; ++i) {
            final ModelPerformer modelPerformer = performers[i];
            final ModelPerformer modelPerformer2 = new ModelPerformer();
            modelPerformer2.setName(modelPerformer.getName());
            modelPerformer2.setExclusiveClass(modelPerformer.getExclusiveClass());
            modelPerformer2.setKeyFrom(modelPerformer.getKeyFrom());
            modelPerformer2.setKeyTo(modelPerformer.getKeyTo());
            modelPerformer2.setVelFrom(modelPerformer.getVelFrom());
            modelPerformer2.setVelTo(modelPerformer.getVelTo());
            modelPerformer2.getOscillators().addAll(modelPerformer.getOscillators());
            modelPerformer2.getConnectionBlocks().addAll(modelPerformer.getConnectionBlocks());
            array[i] = modelPerformer2;
            final List<ModelConnectionBlock> connectionBlocks = modelPerformer2.getConnectionBlocks();
            if (this.cds_control_connections != null) {
                final String string = Integer.toString(this.cds_control_number);
                final Iterator<ModelConnectionBlock> iterator = connectionBlocks.iterator();
                while (iterator.hasNext()) {
                    final ModelSource[] sources = iterator.next().getSources();
                    boolean b = false;
                    if (sources != null) {
                        for (int j = 0; j < sources.length; ++j) {
                            final ModelSource modelSource = sources[j];
                            if ("midi_cc".equals(modelSource.getIdentifier().getObject()) && string.equals(modelSource.getIdentifier().getVariable())) {
                                b = true;
                            }
                        }
                    }
                    if (b) {
                        iterator.remove();
                    }
                }
                for (int k = 0; k < this.cds_control_connections.length; ++k) {
                    connectionBlocks.add(this.cds_control_connections[k]);
                }
            }
            if (this.cds_polypressure_connections != null) {
                final Iterator<ModelConnectionBlock> iterator2 = connectionBlocks.iterator();
                while (iterator2.hasNext()) {
                    final ModelSource[] sources2 = iterator2.next().getSources();
                    boolean b2 = false;
                    if (sources2 != null) {
                        for (int l = 0; l < sources2.length; ++l) {
                            final ModelSource modelSource2 = sources2[l];
                            if ("midi".equals(modelSource2.getIdentifier().getObject()) && "poly_pressure".equals(modelSource2.getIdentifier().getVariable())) {
                                b2 = true;
                            }
                        }
                    }
                    if (b2) {
                        iterator2.remove();
                    }
                }
                for (int n = 0; n < this.cds_polypressure_connections.length; ++n) {
                    connectionBlocks.add(this.cds_polypressure_connections[n]);
                }
            }
            if (this.cds_channelpressure_connections != null) {
                final Iterator<ModelConnectionBlock> iterator3 = connectionBlocks.iterator();
                while (iterator3.hasNext()) {
                    final ModelSource[] sources3 = iterator3.next().getSources();
                    boolean b3 = false;
                    if (sources3 != null) {
                        for (int n2 = 0; n2 < sources3.length; ++n2) {
                            final ModelIdentifier identifier = sources3[n2].getIdentifier();
                            if ("midi".equals(identifier.getObject()) && "channel_pressure".equals(identifier.getVariable())) {
                                b3 = true;
                            }
                        }
                    }
                    if (b3) {
                        iterator3.remove();
                    }
                }
                for (int n3 = 0; n3 < this.cds_channelpressure_connections.length; ++n3) {
                    connectionBlocks.add(this.cds_channelpressure_connections[n3]);
                }
            }
        }
        this.current_instrument = new SoftInstrument(sourceInstrument, array);
    }
    
    private ModelConnectionBlock[] createModelConnections(final ModelIdentifier modelIdentifier, final int[] array, final int[] array2) {
        final ArrayList list = new ArrayList();
        for (int i = 0; i < array.length; ++i) {
            final int n = array[i];
            final int n2 = array2[i];
            if (n == 0) {
                list.add(new ModelConnectionBlock(new ModelSource(modelIdentifier, false, false, 0), (n2 - 64) * 100, new ModelDestination(new ModelIdentifier("osc", "pitch"))));
            }
            if (n == 1) {
                final double n3 = (n2 / 64.0 - 1.0) * 9600.0;
                ModelConnectionBlock modelConnectionBlock;
                if (n3 > 0.0) {
                    modelConnectionBlock = new ModelConnectionBlock(new ModelSource(modelIdentifier, true, false, 0), -n3, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
                }
                else {
                    modelConnectionBlock = new ModelConnectionBlock(new ModelSource(modelIdentifier, false, false, 0), n3, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
                }
                list.add(modelConnectionBlock);
            }
            if (n == 2) {
                list.add(new ModelConnectionBlock(new ModelSource(modelIdentifier, new ModelTransform() {
                    double s = this.val$scale;
                    final /* synthetic */ double val$scale = n2 / 64.0;
                    
                    @Override
                    public double transform(double n) {
                        if (this.s < 1.0) {
                            n = this.s + n * (1.0 - this.s);
                        }
                        else {
                            if (this.s <= 1.0) {
                                return 0.0;
                            }
                            n = 1.0 + n * (this.s - 1.0);
                        }
                        return -(0.4166666666666667 / Math.log(10.0)) * Math.log(n);
                    }
                }), -960.0, new ModelDestination(ModelDestination.DESTINATION_GAIN)));
            }
            if (n == 3) {
                list.add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(modelIdentifier, false, false, 0), (n2 / 64.0 - 1.0) * 9600.0, new ModelDestination(ModelDestination.DESTINATION_PITCH)));
            }
            if (n == 4) {
                list.add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(modelIdentifier, false, false, 0), n2 / 128.0 * 2400.0, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ)));
            }
            if (n == 5) {
                list.add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, false, 0), new ModelSource(modelIdentifier, new ModelTransform() {
                    double s = this.val$scale;
                    final /* synthetic */ double val$scale = n2 / 127.0;
                    
                    @Override
                    public double transform(final double n) {
                        return -(0.4166666666666667 / Math.log(10.0)) * Math.log(1.0 - n * this.s);
                    }
                }), -960.0, new ModelDestination(ModelDestination.DESTINATION_GAIN)));
            }
        }
        return (ModelConnectionBlock[])list.toArray(new ModelConnectionBlock[list.size()]);
    }
    
    public void mapPolyPressureToDestination(final int[] array, final int[] array2) {
        this.current_instrument = null;
        if (array.length == 0) {
            this.cds_polypressure_connections = null;
            return;
        }
        this.cds_polypressure_connections = this.createModelConnections(new ModelIdentifier("midi", "poly_pressure"), array, array2);
    }
    
    public void mapChannelPressureToDestination(final int[] array, final int[] array2) {
        this.current_instrument = null;
        if (array.length == 0) {
            this.cds_channelpressure_connections = null;
            return;
        }
        this.cds_channelpressure_connections = this.createModelConnections(new ModelIdentifier("midi", "channel_pressure"), array, array2);
    }
    
    public void mapControlToDestination(final int cds_control_number, final int[] array, final int[] array2) {
        if ((cds_control_number < 1 || cds_control_number > 31) && (cds_control_number < 64 || cds_control_number > 95)) {
            this.cds_control_connections = null;
            return;
        }
        this.current_instrument = null;
        this.cds_control_number = cds_control_number;
        if (array.length == 0) {
            this.cds_control_connections = null;
            return;
        }
        this.cds_control_connections = this.createModelConnections(new ModelIdentifier("midi_cc", Integer.toString(cds_control_number)), array, array2);
    }
    
    public void controlChangePerNote(final int n, final int n2, final int n3) {
        if (this.keybasedcontroller_active == null) {
            this.keybasedcontroller_active = new boolean[128][];
            this.keybasedcontroller_value = new double[128][];
        }
        if (this.keybasedcontroller_active[n] == null) {
            Arrays.fill(this.keybasedcontroller_active[n] = new boolean[128], false);
            Arrays.fill(this.keybasedcontroller_value[n] = new double[128], 0.0);
        }
        if (n3 == -1) {
            this.keybasedcontroller_active[n][n2] = false;
        }
        else {
            this.keybasedcontroller_active[n][n2] = true;
            this.keybasedcontroller_value[n][n2] = n3 / 128.0;
        }
        if (n2 < 120) {
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].active) {
                    this.voices[i].controlChange(n2, -1);
                }
            }
        }
        else if (n2 == 120) {
            for (int j = 0; j < this.voices.length; ++j) {
                if (this.voices[j].active) {
                    this.voices[j].rpnChange(1, -1);
                }
            }
        }
        else if (n2 == 121) {
            for (int k = 0; k < this.voices.length; ++k) {
                if (this.voices[k].active) {
                    this.voices[k].rpnChange(2, -1);
                }
            }
        }
    }
    
    public int getControlPerNote(final int n, final int n2) {
        if (this.keybasedcontroller_active == null) {
            return -1;
        }
        if (this.keybasedcontroller_active[n] == null) {
            return -1;
        }
        if (!this.keybasedcontroller_active[n][n2]) {
            return -1;
        }
        return (int)(this.keybasedcontroller_value[n][n2] * 128.0);
    }
    
    @Override
    public void controlChange(int restrict7Bit, int restrict7Bit2) {
        restrict7Bit = restrict7Bit(restrict7Bit);
        restrict7Bit2 = restrict7Bit(restrict7Bit2);
        if (this.current_mixer != null) {
            this.current_mixer.controlChange(restrict7Bit, restrict7Bit2);
        }
        synchronized (this.control_mutex) {
            switch (restrict7Bit) {
                case 5: {
                    this.portamento_time = Math.pow(100000.0, -Math.asin(restrict7Bit2 / 128.0 * 2.0 - 1.0) / 3.141592653589793 + 0.5) / 100.0 / 100.0 * 1000.0 / this.synthesizer.getControlRate();
                    break;
                }
                case 6:
                case 38:
                case 96:
                case 97: {
                    int n = 0;
                    if (this.nrpn_control != 16383) {
                        final int[] array = this.co_midi_nrpn_nrpn_i.get(this.nrpn_control);
                        if (array != null) {
                            n = array[0];
                        }
                    }
                    if (this.rpn_control != 16383) {
                        final int[] array2 = this.co_midi_rpn_rpn_i.get(this.rpn_control);
                        if (array2 != null) {
                            n = array2[0];
                        }
                    }
                    if (restrict7Bit == 6) {
                        n = (n & 0x7F) + (restrict7Bit2 << 7);
                    }
                    else if (restrict7Bit == 38) {
                        n = (n & 0x3F80) + restrict7Bit2;
                    }
                    else if (restrict7Bit == 96 || restrict7Bit == 97) {
                        int n2 = 1;
                        if (this.rpn_control == 2 || this.rpn_control == 3 || this.rpn_control == 4) {
                            n2 = 128;
                        }
                        if (restrict7Bit == 96) {
                            n += n2;
                        }
                        if (restrict7Bit == 97) {
                            n -= n2;
                        }
                    }
                    if (this.nrpn_control != 16383) {
                        this.nrpnChange(this.nrpn_control, n);
                    }
                    if (this.rpn_control != 16383) {
                        this.rpnChange(this.rpn_control, n);
                        break;
                    }
                    break;
                }
                case 64: {
                    final boolean sustain = restrict7Bit2 >= 64;
                    if (this.sustain == sustain) {
                        break;
                    }
                    if (!(this.sustain = sustain)) {
                        for (int i = 0; i < this.voices.length; ++i) {
                            if (this.voices[i].active && this.voices[i].sustain && this.voices[i].channel == this.channel) {
                                this.voices[i].sustain = false;
                                if (!this.voices[i].on) {
                                    this.voices[i].on = true;
                                    this.voices[i].noteOff(0);
                                }
                            }
                        }
                        break;
                    }
                    for (int j = 0; j < this.voices.length; ++j) {
                        if (this.voices[j].active && this.voices[j].channel == this.channel) {
                            this.voices[j].redamp();
                        }
                    }
                    break;
                }
                case 65: {
                    this.portamento = (restrict7Bit2 >= 64);
                    this.portamento_lastnote[0] = -1;
                    this.portamento_lastnote_ix = 0;
                    break;
                }
                case 66: {
                    final boolean b = restrict7Bit2 >= 64;
                    if (b) {
                        for (int k = 0; k < this.voices.length; ++k) {
                            if (this.voices[k].active && this.voices[k].on && this.voices[k].channel == this.channel) {
                                this.voices[k].sostenuto = true;
                            }
                        }
                    }
                    if (!b) {
                        for (int l = 0; l < this.voices.length; ++l) {
                            if (this.voices[l].active && this.voices[l].sostenuto && this.voices[l].channel == this.channel) {
                                this.voices[l].sostenuto = false;
                                if (!this.voices[l].on) {
                                    this.voices[l].on = true;
                                    this.voices[l].noteOff(0);
                                }
                            }
                        }
                        break;
                    }
                    break;
                }
                case 98: {
                    this.nrpn_control = (this.nrpn_control & 0x3F80) + restrict7Bit2;
                    this.rpn_control = 16383;
                    break;
                }
                case 99: {
                    this.nrpn_control = (this.nrpn_control & 0x7F) + (restrict7Bit2 << 7);
                    this.rpn_control = 16383;
                    break;
                }
                case 100: {
                    this.rpn_control = (this.rpn_control & 0x3F80) + restrict7Bit2;
                    this.nrpn_control = 16383;
                    break;
                }
                case 101: {
                    this.rpn_control = (this.rpn_control & 0x7F) + (restrict7Bit2 << 7);
                    this.nrpn_control = 16383;
                    break;
                }
                case 120: {
                    this.allSoundOff();
                    break;
                }
                case 121: {
                    this.resetAllControllers(restrict7Bit2 == 127);
                    break;
                }
                case 122: {
                    this.localControl(restrict7Bit2 >= 64);
                    break;
                }
                case 123: {
                    this.allNotesOff();
                    break;
                }
                case 124: {
                    this.setOmni(false);
                    break;
                }
                case 125: {
                    this.setOmni(true);
                    break;
                }
                case 126: {
                    if (restrict7Bit2 == 1) {
                        this.setMono(true);
                        break;
                    }
                    break;
                }
                case 127: {
                    this.setMono(false);
                    break;
                }
            }
            this.co_midi_cc_cc[restrict7Bit][0] = restrict7Bit2 * 0.0078125;
            if (restrict7Bit == 0) {
                this.bank = restrict7Bit2 << 7;
                return;
            }
            if (restrict7Bit == 32) {
                this.bank = (this.bank & 0x3F80) + restrict7Bit2;
                return;
            }
            this.controller[restrict7Bit] = restrict7Bit2;
            if (restrict7Bit < 32) {
                this.controller[restrict7Bit + 32] = 0;
            }
            for (int n3 = 0; n3 < this.voices.length; ++n3) {
                if (this.voices[n3].active) {
                    this.voices[n3].controlChange(restrict7Bit, restrict7Bit2);
                }
            }
        }
    }
    
    @Override
    public int getController(final int n) {
        synchronized (this.control_mutex) {
            return this.controller[n] & 0x7F;
        }
    }
    
    public void tuningChange(final int n) {
        this.tuningChange(0, n);
    }
    
    public void tuningChange(final int n, final int n2) {
        synchronized (this.control_mutex) {
            this.tuning = this.synthesizer.getTuning(new Patch(n, n2));
        }
    }
    
    @Override
    public void programChange(final int n) {
        this.programChange(this.bank, n);
    }
    
    @Override
    public void programChange(int restrict14Bit, int restrict7Bit) {
        restrict14Bit = restrict14Bit(restrict14Bit);
        restrict7Bit = restrict7Bit(restrict7Bit);
        synchronized (this.control_mutex) {
            this.mainmixer.activity();
            if (this.bank != restrict14Bit || this.program != restrict7Bit) {
                this.bank = restrict14Bit;
                this.program = restrict7Bit;
                this.current_instrument = null;
            }
        }
    }
    
    @Override
    public int getProgram() {
        synchronized (this.control_mutex) {
            return this.program;
        }
    }
    
    @Override
    public void setPitchBend(int restrict14Bit) {
        restrict14Bit = restrict14Bit(restrict14Bit);
        if (this.current_mixer != null) {
            this.current_mixer.setPitchBend(restrict14Bit);
        }
        synchronized (this.control_mutex) {
            this.mainmixer.activity();
            this.co_midi_pitch[0] = restrict14Bit * 6.103515625E-5;
            this.pitchbend = restrict14Bit;
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].active) {
                    this.voices[i].setPitchBend(restrict14Bit);
                }
            }
        }
    }
    
    @Override
    public int getPitchBend() {
        synchronized (this.control_mutex) {
            return this.pitchbend;
        }
    }
    
    public void nrpnChange(final int n, final int n2) {
        if (this.synthesizer.getGeneralMidiMode() == 0) {
            if (n == 136) {
                this.controlChange(76, n2 >> 7);
            }
            if (n == 137) {
                this.controlChange(77, n2 >> 7);
            }
            if (n == 138) {
                this.controlChange(78, n2 >> 7);
            }
            if (n == 160) {
                this.controlChange(74, n2 >> 7);
            }
            if (n == 161) {
                this.controlChange(71, n2 >> 7);
            }
            if (n == 227) {
                this.controlChange(73, n2 >> 7);
            }
            if (n == 228) {
                this.controlChange(75, n2 >> 7);
            }
            if (n == 230) {
                this.controlChange(72, n2 >> 7);
            }
            if (n >> 7 == 24) {
                this.controlChangePerNote(n % 128, 120, n2 >> 7);
            }
            if (n >> 7 == 26) {
                this.controlChangePerNote(n % 128, 7, n2 >> 7);
            }
            if (n >> 7 == 28) {
                this.controlChangePerNote(n % 128, 10, n2 >> 7);
            }
            if (n >> 7 == 29) {
                this.controlChangePerNote(n % 128, 91, n2 >> 7);
            }
            if (n >> 7 == 30) {
                this.controlChangePerNote(n % 128, 93, n2 >> 7);
            }
        }
        int[] array = this.co_midi_nrpn_nrpn_i.get(n);
        double[] array2 = this.co_midi_nrpn_nrpn.get(n);
        if (array == null) {
            array = new int[] { 0 };
            this.co_midi_nrpn_nrpn_i.put(n, array);
        }
        if (array2 == null) {
            array2 = new double[] { 0.0 };
            this.co_midi_nrpn_nrpn.put(n, array2);
        }
        array[0] = n2;
        array2[0] = array[0] * 6.103515625E-5;
        for (int i = 0; i < this.voices.length; ++i) {
            if (this.voices[i].active) {
                this.voices[i].nrpnChange(n, array[0]);
            }
        }
    }
    
    public void rpnChange(final int n, final int n2) {
        if (n == 3) {
            this.tuning_program = (n2 >> 7 & 0x7F);
            this.tuningChange(this.tuning_bank, this.tuning_program);
        }
        if (n == 4) {
            this.tuning_bank = (n2 >> 7 & 0x7F);
        }
        int[] array = this.co_midi_rpn_rpn_i.get(n);
        double[] array2 = this.co_midi_rpn_rpn.get(n);
        if (array == null) {
            array = new int[] { 0 };
            this.co_midi_rpn_rpn_i.put(n, array);
        }
        if (array2 == null) {
            array2 = new double[] { 0.0 };
            this.co_midi_rpn_rpn.put(n, array2);
        }
        array[0] = n2;
        array2[0] = array[0] * 6.103515625E-5;
        for (int i = 0; i < this.voices.length; ++i) {
            if (this.voices[i].active) {
                this.voices[i].rpnChange(n, array[0]);
            }
        }
    }
    
    @Override
    public void resetAllControllers() {
        this.resetAllControllers(false);
    }
    
    public void resetAllControllers(final boolean b) {
        synchronized (this.control_mutex) {
            this.mainmixer.activity();
            for (int i = 0; i < 128; ++i) {
                this.setPolyPressure(i, 0);
            }
            this.setChannelPressure(0);
            this.setPitchBend(8192);
            for (int j = 0; j < 128; ++j) {
                if (!SoftChannel.dontResetControls[j]) {
                    this.controlChange(j, 0);
                }
            }
            this.controlChange(71, 64);
            this.controlChange(72, 64);
            this.controlChange(73, 64);
            this.controlChange(74, 64);
            this.controlChange(75, 64);
            this.controlChange(76, 64);
            this.controlChange(77, 64);
            this.controlChange(78, 64);
            this.controlChange(8, 64);
            this.controlChange(11, 127);
            this.controlChange(98, 127);
            this.controlChange(99, 127);
            this.controlChange(100, 127);
            this.controlChange(101, 127);
            if (b) {
                this.keybasedcontroller_active = null;
                this.keybasedcontroller_value = null;
                this.controlChange(7, 100);
                this.controlChange(10, 64);
                this.controlChange(91, 40);
                for (final int intValue : this.co_midi_rpn_rpn.keySet()) {
                    if (intValue != 3 && intValue != 4) {
                        this.rpnChange(intValue, 0);
                    }
                }
                final Iterator<Integer> iterator2 = this.co_midi_nrpn_nrpn.keySet().iterator();
                while (iterator2.hasNext()) {
                    this.nrpnChange(iterator2.next(), 0);
                }
                this.rpnChange(0, 256);
                this.rpnChange(1, 8192);
                this.rpnChange(2, 8192);
                this.rpnChange(5, 64);
                this.tuning_bank = 0;
                this.tuning_program = 0;
                this.tuning = new SoftTuning();
            }
        }
    }
    
    @Override
    public void allNotesOff() {
        if (this.current_mixer != null) {
            this.current_mixer.allNotesOff();
        }
        synchronized (this.control_mutex) {
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].on && this.voices[i].channel == this.channel && !this.voices[i].releaseTriggered) {
                    this.voices[i].noteOff(0);
                }
            }
        }
    }
    
    @Override
    public void allSoundOff() {
        if (this.current_mixer != null) {
            this.current_mixer.allSoundOff();
        }
        synchronized (this.control_mutex) {
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].on && this.voices[i].channel == this.channel) {
                    this.voices[i].soundOff();
                }
            }
        }
    }
    
    @Override
    public boolean localControl(final boolean b) {
        return false;
    }
    
    @Override
    public void setMono(final boolean b) {
        if (this.current_mixer != null) {
            this.current_mixer.setMono(b);
        }
        synchronized (this.control_mutex) {
            this.allNotesOff();
            this.mono = b;
        }
    }
    
    @Override
    public boolean getMono() {
        synchronized (this.control_mutex) {
            return this.mono;
        }
    }
    
    @Override
    public void setOmni(final boolean omni) {
        if (this.current_mixer != null) {
            this.current_mixer.setOmni(omni);
        }
        this.allNotesOff();
    }
    
    @Override
    public boolean getOmni() {
        return false;
    }
    
    @Override
    public void setMute(final boolean mute) {
        if (this.current_mixer != null) {
            this.current_mixer.setMute(mute);
        }
        synchronized (this.control_mutex) {
            this.mute = mute;
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].active && this.voices[i].channel == this.channel) {
                    this.voices[i].setMute(mute);
                }
            }
        }
    }
    
    @Override
    public boolean getMute() {
        synchronized (this.control_mutex) {
            return this.mute;
        }
    }
    
    @Override
    public void setSolo(final boolean b) {
        if (this.current_mixer != null) {
            this.current_mixer.setSolo(b);
        }
        synchronized (this.control_mutex) {
            this.solo = b;
            boolean b2 = false;
            final SoftChannel[] channels = this.synthesizer.channels;
            for (int length = channels.length, i = 0; i < length; ++i) {
                if (channels[i].solo) {
                    b2 = true;
                    break;
                }
            }
            if (!b2) {
                final SoftChannel[] channels2 = this.synthesizer.channels;
                for (int length2 = channels2.length, j = 0; j < length2; ++j) {
                    channels2[j].setSoloMute(false);
                }
                return;
            }
            for (final SoftChannel softChannel : this.synthesizer.channels) {
                softChannel.setSoloMute(!softChannel.solo);
            }
        }
    }
    
    private void setSoloMute(final boolean solomute) {
        synchronized (this.control_mutex) {
            if (this.solomute == solomute) {
                return;
            }
            this.solomute = solomute;
            for (int i = 0; i < this.voices.length; ++i) {
                if (this.voices[i].active && this.voices[i].channel == this.channel) {
                    this.voices[i].setSoloMute(this.solomute);
                }
            }
        }
    }
    
    @Override
    public boolean getSolo() {
        synchronized (this.control_mutex) {
            return this.solo;
        }
    }
    
    static {
        SoftChannel.dontResetControls = new boolean[128];
        for (int i = 0; i < SoftChannel.dontResetControls.length; ++i) {
            SoftChannel.dontResetControls[i] = false;
        }
        SoftChannel.dontResetControls[0] = true;
        SoftChannel.dontResetControls[32] = true;
        SoftChannel.dontResetControls[7] = true;
        SoftChannel.dontResetControls[8] = true;
        SoftChannel.dontResetControls[10] = true;
        SoftChannel.dontResetControls[11] = true;
        SoftChannel.dontResetControls[91] = true;
        SoftChannel.dontResetControls[92] = true;
        SoftChannel.dontResetControls[93] = true;
        SoftChannel.dontResetControls[94] = true;
        SoftChannel.dontResetControls[95] = true;
        SoftChannel.dontResetControls[70] = true;
        SoftChannel.dontResetControls[71] = true;
        SoftChannel.dontResetControls[72] = true;
        SoftChannel.dontResetControls[73] = true;
        SoftChannel.dontResetControls[74] = true;
        SoftChannel.dontResetControls[75] = true;
        SoftChannel.dontResetControls[76] = true;
        SoftChannel.dontResetControls[77] = true;
        SoftChannel.dontResetControls[78] = true;
        SoftChannel.dontResetControls[79] = true;
        SoftChannel.dontResetControls[120] = true;
        SoftChannel.dontResetControls[121] = true;
        SoftChannel.dontResetControls[122] = true;
        SoftChannel.dontResetControls[123] = true;
        SoftChannel.dontResetControls[124] = true;
        SoftChannel.dontResetControls[125] = true;
        SoftChannel.dontResetControls[126] = true;
        SoftChannel.dontResetControls[127] = true;
        SoftChannel.dontResetControls[6] = true;
        SoftChannel.dontResetControls[38] = true;
        SoftChannel.dontResetControls[96] = true;
        SoftChannel.dontResetControls[97] = true;
        SoftChannel.dontResetControls[98] = true;
        SoftChannel.dontResetControls[99] = true;
        SoftChannel.dontResetControls[100] = true;
        SoftChannel.dontResetControls[101] = true;
    }
    
    private class MidiControlObject implements SoftControl
    {
        double[] pitch;
        double[] channel_pressure;
        double[] poly_pressure;
        
        private MidiControlObject() {
            this.pitch = SoftChannel.this.co_midi_pitch;
            this.channel_pressure = SoftChannel.this.co_midi_channel_pressure;
            this.poly_pressure = new double[1];
        }
        
        @Override
        public double[] get(final int n, final String s) {
            if (s == null) {
                return null;
            }
            if (s.equals("pitch")) {
                return this.pitch;
            }
            if (s.equals("channel_pressure")) {
                return this.channel_pressure;
            }
            if (s.equals("poly_pressure")) {
                return this.poly_pressure;
            }
            return null;
        }
    }
}
