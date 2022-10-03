package com.sun.media.sound;

import java.util.Arrays;
import javax.sound.midi.MidiChannel;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.sound.midi.VoiceStatus;

public final class SoftVoice extends VoiceStatus
{
    public int exclusiveClass;
    public boolean releaseTriggered;
    private int noteOn_noteNumber;
    private int noteOn_velocity;
    private int noteOff_velocity;
    private int delay;
    ModelChannelMixer channelmixer;
    double tunedKey;
    SoftTuning tuning;
    SoftChannel stealer_channel;
    ModelConnectionBlock[] stealer_extendedConnectionBlocks;
    SoftPerformer stealer_performer;
    ModelChannelMixer stealer_channelmixer;
    int stealer_voiceID;
    int stealer_noteNumber;
    int stealer_velocity;
    boolean stealer_releaseTriggered;
    int voiceID;
    boolean sustain;
    boolean sostenuto;
    boolean portamento;
    private final SoftFilter filter_left;
    private final SoftFilter filter_right;
    private final SoftProcess eg;
    private final SoftProcess lfo;
    Map<String, SoftControl> objects;
    SoftSynthesizer synthesizer;
    SoftInstrument instrument;
    SoftPerformer performer;
    SoftChannel softchannel;
    boolean on;
    private boolean audiostarted;
    private boolean started;
    private boolean stopping;
    private float osc_attenuation;
    private ModelOscillatorStream osc_stream;
    private int osc_stream_nrofchannels;
    private float[][] osc_buff;
    private boolean osc_stream_off_transmitted;
    private boolean out_mixer_end;
    private float out_mixer_left;
    private float out_mixer_right;
    private float out_mixer_effect1;
    private float out_mixer_effect2;
    private float last_out_mixer_left;
    private float last_out_mixer_right;
    private float last_out_mixer_effect1;
    private float last_out_mixer_effect2;
    ModelConnectionBlock[] extendedConnectionBlocks;
    private ModelConnectionBlock[] connections;
    private double[] connections_last;
    private double[][][] connections_src;
    private int[][] connections_src_kc;
    private double[][] connections_dst;
    private boolean soundoff;
    private float lastMuteValue;
    private float lastSoloMuteValue;
    double[] co_noteon_keynumber;
    double[] co_noteon_velocity;
    double[] co_noteon_on;
    private final SoftControl co_noteon;
    private final double[] co_mixer_active;
    private final double[] co_mixer_gain;
    private final double[] co_mixer_pan;
    private final double[] co_mixer_balance;
    private final double[] co_mixer_reverb;
    private final double[] co_mixer_chorus;
    private final SoftControl co_mixer;
    private final double[] co_osc_pitch;
    private final SoftControl co_osc;
    private final double[] co_filter_freq;
    private final double[] co_filter_type;
    private final double[] co_filter_q;
    private final SoftControl co_filter;
    SoftResamplerStreamer resampler;
    private final int nrofchannels;
    
    public SoftVoice(final SoftSynthesizer synthesizer) {
        this.exclusiveClass = 0;
        this.releaseTriggered = false;
        this.noteOn_noteNumber = 0;
        this.noteOn_velocity = 0;
        this.noteOff_velocity = 0;
        this.delay = 0;
        this.channelmixer = null;
        this.tunedKey = 0.0;
        this.tuning = null;
        this.stealer_channel = null;
        this.stealer_extendedConnectionBlocks = null;
        this.stealer_performer = null;
        this.stealer_channelmixer = null;
        this.stealer_voiceID = -1;
        this.stealer_noteNumber = 0;
        this.stealer_velocity = 0;
        this.stealer_releaseTriggered = false;
        this.voiceID = -1;
        this.sustain = false;
        this.sostenuto = false;
        this.portamento = false;
        this.eg = new SoftEnvelopeGenerator();
        this.lfo = new SoftLowFrequencyOscillator();
        this.objects = new HashMap<String, SoftControl>();
        this.softchannel = null;
        this.on = false;
        this.audiostarted = false;
        this.started = false;
        this.stopping = false;
        this.osc_attenuation = 0.0f;
        this.osc_buff = new float[2][];
        this.osc_stream_off_transmitted = false;
        this.out_mixer_end = false;
        this.out_mixer_left = 0.0f;
        this.out_mixer_right = 0.0f;
        this.out_mixer_effect1 = 0.0f;
        this.out_mixer_effect2 = 0.0f;
        this.last_out_mixer_left = 0.0f;
        this.last_out_mixer_right = 0.0f;
        this.last_out_mixer_effect1 = 0.0f;
        this.last_out_mixer_effect2 = 0.0f;
        this.extendedConnectionBlocks = null;
        this.connections_last = new double[50];
        this.connections_src = new double[50][3][];
        this.connections_src_kc = new int[50][3];
        this.connections_dst = new double[50][];
        this.soundoff = false;
        this.lastMuteValue = 0.0f;
        this.lastSoloMuteValue = 0.0f;
        this.co_noteon_keynumber = new double[1];
        this.co_noteon_velocity = new double[1];
        this.co_noteon_on = new double[1];
        this.co_noteon = new SoftControl() {
            double[] keynumber = SoftVoice.this.co_noteon_keynumber;
            double[] velocity = SoftVoice.this.co_noteon_velocity;
            double[] on = SoftVoice.this.co_noteon_on;
            
            @Override
            public double[] get(final int n, final String s) {
                if (s == null) {
                    return null;
                }
                if (s.equals("keynumber")) {
                    return this.keynumber;
                }
                if (s.equals("velocity")) {
                    return this.velocity;
                }
                if (s.equals("on")) {
                    return this.on;
                }
                return null;
            }
        };
        this.co_mixer_active = new double[1];
        this.co_mixer_gain = new double[1];
        this.co_mixer_pan = new double[1];
        this.co_mixer_balance = new double[1];
        this.co_mixer_reverb = new double[1];
        this.co_mixer_chorus = new double[1];
        this.co_mixer = new SoftControl() {
            double[] active = SoftVoice.this.co_mixer_active;
            double[] gain = SoftVoice.this.co_mixer_gain;
            double[] pan = SoftVoice.this.co_mixer_pan;
            double[] balance = SoftVoice.this.co_mixer_balance;
            double[] reverb = SoftVoice.this.co_mixer_reverb;
            double[] chorus = SoftVoice.this.co_mixer_chorus;
            
            @Override
            public double[] get(final int n, final String s) {
                if (s == null) {
                    return null;
                }
                if (s.equals("active")) {
                    return this.active;
                }
                if (s.equals("gain")) {
                    return this.gain;
                }
                if (s.equals("pan")) {
                    return this.pan;
                }
                if (s.equals("balance")) {
                    return this.balance;
                }
                if (s.equals("reverb")) {
                    return this.reverb;
                }
                if (s.equals("chorus")) {
                    return this.chorus;
                }
                return null;
            }
        };
        this.co_osc_pitch = new double[1];
        this.co_osc = new SoftControl() {
            double[] pitch = SoftVoice.this.co_osc_pitch;
            
            @Override
            public double[] get(final int n, final String s) {
                if (s == null) {
                    return null;
                }
                if (s.equals("pitch")) {
                    return this.pitch;
                }
                return null;
            }
        };
        this.co_filter_freq = new double[1];
        this.co_filter_type = new double[1];
        this.co_filter_q = new double[1];
        this.co_filter = new SoftControl() {
            double[] freq = SoftVoice.this.co_filter_freq;
            double[] ftype = SoftVoice.this.co_filter_type;
            double[] q = SoftVoice.this.co_filter_q;
            
            @Override
            public double[] get(final int n, final String s) {
                if (s == null) {
                    return null;
                }
                if (s.equals("freq")) {
                    return this.freq;
                }
                if (s.equals("type")) {
                    return this.ftype;
                }
                if (s.equals("q")) {
                    return this.q;
                }
                return null;
            }
        };
        this.synthesizer = synthesizer;
        this.filter_left = new SoftFilter(synthesizer.getFormat().getSampleRate());
        this.filter_right = new SoftFilter(synthesizer.getFormat().getSampleRate());
        this.nrofchannels = synthesizer.getFormat().getChannels();
    }
    
    private int getValueKC(final ModelIdentifier modelIdentifier) {
        if (modelIdentifier.getObject().equals("midi_cc")) {
            final int int1 = Integer.parseInt(modelIdentifier.getVariable());
            if (int1 != 0 && int1 != 32 && int1 < 120) {
                return int1;
            }
        }
        else if (modelIdentifier.getObject().equals("midi_rpn")) {
            if (modelIdentifier.getVariable().equals("1")) {
                return 120;
            }
            if (modelIdentifier.getVariable().equals("2")) {
                return 121;
            }
        }
        return -1;
    }
    
    private double[] getValue(final ModelIdentifier modelIdentifier) {
        final SoftControl softControl = this.objects.get(modelIdentifier.getObject());
        if (softControl == null) {
            return null;
        }
        return softControl.get(modelIdentifier.getInstance(), modelIdentifier.getVariable());
    }
    
    private double transformValue(final double n, final ModelSource modelSource) {
        if (modelSource.getTransform() != null) {
            return modelSource.getTransform().transform(n);
        }
        return n;
    }
    
    private double transformValue(final double n, final ModelDestination modelDestination) {
        if (modelDestination.getTransform() != null) {
            return modelDestination.getTransform().transform(n);
        }
        return n;
    }
    
    private double processKeyBasedController(double n, final int n2) {
        if (n2 == -1) {
            return n;
        }
        if (this.softchannel.keybasedcontroller_active != null && this.softchannel.keybasedcontroller_active[this.note] != null && this.softchannel.keybasedcontroller_active[this.note][n2]) {
            final double n3 = this.softchannel.keybasedcontroller_value[this.note][n2];
            if (n2 == 10 || n2 == 91 || n2 == 93) {
                return n3;
            }
            n += n3 * 2.0 - 1.0;
            if (n > 1.0) {
                n = 1.0;
            }
            else if (n < 0.0) {
                n = 0.0;
            }
        }
        return n;
    }
    
    private void processConnection(final int n) {
        final ModelConnectionBlock modelConnectionBlock = this.connections[n];
        final double[][] array = this.connections_src[n];
        final double[] array2 = this.connections_dst[n];
        if (array2 == null || Double.isInfinite(array2[0])) {
            return;
        }
        double scale = modelConnectionBlock.getScale();
        if (this.softchannel.keybasedcontroller_active == null) {
            final ModelSource[] sources = modelConnectionBlock.getSources();
            for (int i = 0; i < sources.length; ++i) {
                scale *= this.transformValue(array[i][0], sources[i]);
                if (scale == 0.0) {
                    break;
                }
            }
        }
        else {
            final ModelSource[] sources2 = modelConnectionBlock.getSources();
            final int[] array3 = this.connections_src_kc[n];
            for (int j = 0; j < sources2.length; ++j) {
                scale *= this.transformValue(this.processKeyBasedController(array[j][0], array3[j]), sources2[j]);
                if (scale == 0.0) {
                    break;
                }
            }
        }
        final double transformValue = this.transformValue(scale, modelConnectionBlock.getDestination());
        array2[0] = array2[0] - this.connections_last[n] + transformValue;
        this.connections_last[n] = transformValue;
    }
    
    void updateTuning(final SoftTuning tuning) {
        this.tuning = tuning;
        this.tunedKey = this.tuning.getTuning(this.note) / 100.0;
        if (!this.portamento) {
            this.co_noteon_keynumber[0] = this.tunedKey * 0.0078125;
            if (this.performer == null) {
                return;
            }
            final int[] array = this.performer.midi_connections[4];
            if (array == null) {
                return;
            }
            for (int i = 0; i < array.length; ++i) {
                this.processConnection(array[i]);
            }
        }
    }
    
    void setNote(final int note) {
        this.note = note;
        this.tunedKey = this.tuning.getTuning(note) / 100.0;
    }
    
    void noteOn(final int n, final int noteOn_velocity, final int delay) {
        this.sustain = false;
        this.sostenuto = false;
        this.portamento = false;
        this.soundoff = false;
        this.on = true;
        this.active = true;
        this.started = true;
        this.noteOn_noteNumber = n;
        this.noteOn_velocity = noteOn_velocity;
        this.delay = delay;
        this.lastMuteValue = 0.0f;
        this.lastSoloMuteValue = 0.0f;
        this.setNote(n);
        if (this.performer.forcedKeynumber) {
            this.co_noteon_keynumber[0] = 0.0;
        }
        else {
            this.co_noteon_keynumber[0] = this.tunedKey * 0.0078125;
        }
        if (this.performer.forcedVelocity) {
            this.co_noteon_velocity[0] = 0.0;
        }
        else {
            this.co_noteon_velocity[0] = noteOn_velocity * 0.0078125f;
        }
        this.co_mixer_active[0] = 0.0;
        this.co_mixer_gain[0] = 0.0;
        this.co_mixer_pan[0] = 0.0;
        this.co_mixer_balance[0] = 0.0;
        this.co_mixer_reverb[0] = 0.0;
        this.co_mixer_chorus[0] = 0.0;
        this.co_osc_pitch[0] = 0.0;
        this.co_filter_freq[0] = 0.0;
        this.co_filter_q[0] = 0.0;
        this.co_filter_type[0] = 0.0;
        this.co_noteon_on[0] = 1.0;
        this.eg.reset();
        this.lfo.reset();
        this.filter_left.reset();
        this.filter_right.reset();
        this.objects.put("master", this.synthesizer.getMainMixer().co_master);
        this.objects.put("eg", this.eg);
        this.objects.put("lfo", this.lfo);
        this.objects.put("noteon", this.co_noteon);
        this.objects.put("osc", this.co_osc);
        this.objects.put("mixer", this.co_mixer);
        this.objects.put("filter", this.co_filter);
        this.connections = this.performer.connections;
        if (this.connections_last == null || this.connections_last.length < this.connections.length) {
            this.connections_last = new double[this.connections.length];
        }
        if (this.connections_src == null || this.connections_src.length < this.connections.length) {
            this.connections_src = new double[this.connections.length][][];
            this.connections_src_kc = new int[this.connections.length][];
        }
        if (this.connections_dst == null || this.connections_dst.length < this.connections.length) {
            this.connections_dst = new double[this.connections.length][];
        }
        for (int i = 0; i < this.connections.length; ++i) {
            final ModelConnectionBlock modelConnectionBlock = this.connections[i];
            this.connections_last[i] = 0.0;
            if (modelConnectionBlock.getSources() != null) {
                final ModelSource[] sources = modelConnectionBlock.getSources();
                if (this.connections_src[i] == null || this.connections_src[i].length < sources.length) {
                    this.connections_src[i] = new double[sources.length][];
                    this.connections_src_kc[i] = new int[sources.length];
                }
                final double[][] array = this.connections_src[i];
                final int[] array2 = this.connections_src_kc[i];
                this.connections_src[i] = array;
                for (int j = 0; j < sources.length; ++j) {
                    array2[j] = this.getValueKC(sources[j].getIdentifier());
                    array[j] = this.getValue(sources[j].getIdentifier());
                }
            }
            if (modelConnectionBlock.getDestination() != null) {
                this.connections_dst[i] = this.getValue(modelConnectionBlock.getDestination().getIdentifier());
            }
            else {
                this.connections_dst[i] = null;
            }
        }
        for (int k = 0; k < this.connections.length; ++k) {
            this.processConnection(k);
        }
        if (this.extendedConnectionBlocks != null) {
            for (final ModelConnectionBlock modelConnectionBlock2 : this.extendedConnectionBlocks) {
                double transform = 0.0;
                if (this.softchannel.keybasedcontroller_active == null) {
                    for (final ModelSource modelSource : modelConnectionBlock2.getSources()) {
                        final double n3 = this.getValue(modelSource.getIdentifier())[0];
                        final ModelTransform transform2 = modelSource.getTransform();
                        if (transform2 == null) {
                            transform += n3;
                        }
                        else {
                            transform += transform2.transform(n3);
                        }
                    }
                }
                else {
                    for (final ModelSource modelSource2 : modelConnectionBlock2.getSources()) {
                        final double processKeyBasedController = this.processKeyBasedController(this.getValue(modelSource2.getIdentifier())[0], this.getValueKC(modelSource2.getIdentifier()));
                        final ModelTransform transform3 = modelSource2.getTransform();
                        if (transform3 == null) {
                            transform += processKeyBasedController;
                        }
                        else {
                            transform += transform3.transform(processKeyBasedController);
                        }
                    }
                }
                final ModelDestination destination = modelConnectionBlock2.getDestination();
                final ModelTransform transform4 = destination.getTransform();
                if (transform4 != null) {
                    transform = transform4.transform(transform);
                }
                final double[] value = this.getValue(destination.getIdentifier());
                final int n5 = 0;
                value[n5] += transform;
            }
        }
        this.eg.init(this.synthesizer);
        this.lfo.init(this.synthesizer);
    }
    
    void setPolyPressure(final int n) {
        if (this.performer == null) {
            return;
        }
        final int[] array = this.performer.midi_connections[2];
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.processConnection(array[i]);
        }
    }
    
    void setChannelPressure(final int n) {
        if (this.performer == null) {
            return;
        }
        final int[] array = this.performer.midi_connections[1];
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.processConnection(array[i]);
        }
    }
    
    void controlChange(final int n, final int n2) {
        if (this.performer == null) {
            return;
        }
        final int[] array = this.performer.midi_ctrl_connections[n];
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.processConnection(array[i]);
        }
    }
    
    void nrpnChange(final int n, final int n2) {
        if (this.performer == null) {
            return;
        }
        final int[] array = this.performer.midi_nrpn_connections.get(n);
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.processConnection(array[i]);
        }
    }
    
    void rpnChange(final int n, final int n2) {
        if (this.performer == null) {
            return;
        }
        final int[] array = this.performer.midi_rpn_connections.get(n);
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.processConnection(array[i]);
        }
    }
    
    void setPitchBend(final int n) {
        if (this.performer == null) {
            return;
        }
        final int[] array = this.performer.midi_connections[0];
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.processConnection(array[i]);
        }
    }
    
    void setMute(final boolean b) {
        final double[] co_mixer_gain = this.co_mixer_gain;
        final int n = 0;
        co_mixer_gain[n] -= this.lastMuteValue;
        this.lastMuteValue = (b ? -960.0f : 0.0f);
        final double[] co_mixer_gain2 = this.co_mixer_gain;
        final int n2 = 0;
        co_mixer_gain2[n2] += this.lastMuteValue;
    }
    
    void setSoloMute(final boolean b) {
        final double[] co_mixer_gain = this.co_mixer_gain;
        final int n = 0;
        co_mixer_gain[n] -= this.lastSoloMuteValue;
        this.lastSoloMuteValue = (b ? -960.0f : 0.0f);
        final double[] co_mixer_gain2 = this.co_mixer_gain;
        final int n2 = 0;
        co_mixer_gain2[n2] += this.lastSoloMuteValue;
    }
    
    void shutdown() {
        if (this.co_noteon_on[0] < -0.5) {
            return;
        }
        this.on = false;
        this.co_noteon_on[0] = -1.0;
        if (this.performer == null) {
            return;
        }
        final int[] array = this.performer.midi_connections[3];
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.processConnection(array[i]);
        }
    }
    
    void soundOff() {
        this.on = false;
        this.soundoff = true;
    }
    
    void noteOff(final int noteOff_velocity) {
        if (!this.on) {
            return;
        }
        this.on = false;
        this.noteOff_velocity = noteOff_velocity;
        if (this.softchannel.sustain) {
            this.sustain = true;
            return;
        }
        if (this.sostenuto) {
            return;
        }
        this.co_noteon_on[0] = 0.0;
        if (this.performer == null) {
            return;
        }
        final int[] array = this.performer.midi_connections[3];
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.processConnection(array[i]);
        }
    }
    
    void redamp() {
        if (this.co_noteon_on[0] > 0.5) {
            return;
        }
        if (this.co_noteon_on[0] < -0.5) {
            return;
        }
        this.sustain = true;
        this.co_noteon_on[0] = 1.0;
        if (this.performer == null) {
            return;
        }
        final int[] array = this.performer.midi_connections[3];
        if (array == null) {
            return;
        }
        for (int i = 0; i < array.length; ++i) {
            this.processConnection(array[i]);
        }
    }
    
    void processControlLogic() {
        if (this.stopping) {
            this.active = false;
            this.stopping = false;
            this.audiostarted = false;
            this.instrument = null;
            this.performer = null;
            this.connections = null;
            this.extendedConnectionBlocks = null;
            this.channelmixer = null;
            if (this.osc_stream != null) {
                try {
                    this.osc_stream.close();
                }
                catch (final IOException ex) {}
            }
            if (this.stealer_channel != null) {
                this.stealer_channel.initVoice(this, this.stealer_performer, this.stealer_voiceID, this.stealer_noteNumber, this.stealer_velocity, 0, this.stealer_extendedConnectionBlocks, this.stealer_channelmixer, this.stealer_releaseTriggered);
                this.stealer_releaseTriggered = false;
                this.stealer_channel = null;
                this.stealer_performer = null;
                this.stealer_voiceID = -1;
                this.stealer_noteNumber = 0;
                this.stealer_velocity = 0;
                this.stealer_extendedConnectionBlocks = null;
                this.stealer_channelmixer = null;
            }
        }
        if (this.started) {
            this.audiostarted = true;
            final ModelOscillator modelOscillator = this.performer.oscillators[0];
            this.osc_stream_off_transmitted = false;
            if (modelOscillator instanceof ModelWavetable) {
                try {
                    this.resampler.open((ModelWavetable)modelOscillator, this.synthesizer.getFormat().getSampleRate());
                    this.osc_stream = this.resampler;
                }
                catch (final IOException ex2) {}
            }
            else {
                this.osc_stream = modelOscillator.open(this.synthesizer.getFormat().getSampleRate());
            }
            this.osc_attenuation = modelOscillator.getAttenuation();
            this.osc_stream_nrofchannels = modelOscillator.getChannels();
            if (this.osc_buff == null || this.osc_buff.length < this.osc_stream_nrofchannels) {
                this.osc_buff = new float[this.osc_stream_nrofchannels][];
            }
            if (this.osc_stream != null) {
                this.osc_stream.noteOn(this.softchannel, this, this.noteOn_noteNumber, this.noteOn_velocity);
            }
        }
        if (this.audiostarted) {
            if (this.portamento) {
                double n = this.tunedKey - this.co_noteon_keynumber[0] * 128.0;
                final double abs = Math.abs(n);
                if (abs < 1.0E-10) {
                    this.co_noteon_keynumber[0] = this.tunedKey * 0.0078125;
                    this.portamento = false;
                }
                else {
                    if (abs > this.softchannel.portamento_time) {
                        n = Math.signum(n) * this.softchannel.portamento_time;
                    }
                    final double[] co_noteon_keynumber = this.co_noteon_keynumber;
                    final int n2 = 0;
                    co_noteon_keynumber[n2] += n * 0.0078125;
                }
                final int[] array = this.performer.midi_connections[4];
                if (array == null) {
                    return;
                }
                for (int i = 0; i < array.length; ++i) {
                    this.processConnection(array[i]);
                }
            }
            this.eg.processControlLogic();
            this.lfo.processControlLogic();
            for (int j = 0; j < this.performer.ctrl_connections.length; ++j) {
                this.processConnection(this.performer.ctrl_connections[j]);
            }
            this.osc_stream.setPitch((float)this.co_osc_pitch[0]);
            final int n3 = (int)this.co_filter_type[0];
            double n4;
            if (this.co_filter_freq[0] == 13500.0) {
                n4 = 19912.126958213175;
            }
            else {
                n4 = 440.0 * Math.exp((this.co_filter_freq[0] - 6900.0) * (Math.log(2.0) / 1200.0));
            }
            final double n5 = this.co_filter_q[0] / 10.0;
            this.filter_left.setFilterType(n3);
            this.filter_left.setFrequency(n4);
            this.filter_left.setResonance(n5);
            this.filter_right.setFilterType(n3);
            this.filter_right.setFrequency(n4);
            this.filter_right.setResonance(n5);
            float n6 = (float)Math.exp((-this.osc_attenuation + this.co_mixer_gain[0]) * (Math.log(10.0) / 200.0));
            if (this.co_mixer_gain[0] <= -960.0) {
                n6 = 0.0f;
            }
            if (this.soundoff) {
                this.stopping = true;
                n6 = 0.0f;
            }
            this.volume = (int)(Math.sqrt(n6) * 128.0);
            double n7 = this.co_mixer_pan[0] * 0.001;
            if (n7 < 0.0) {
                n7 = 0.0;
            }
            else if (n7 > 1.0) {
                n7 = 1.0;
            }
            if (n7 == 0.5) {
                this.out_mixer_left = n6 * 0.70710677f;
                this.out_mixer_right = this.out_mixer_left;
            }
            else {
                this.out_mixer_left = n6 * (float)Math.cos(n7 * 3.141592653589793 * 0.5);
                this.out_mixer_right = n6 * (float)Math.sin(n7 * 3.141592653589793 * 0.5);
            }
            final double n8 = this.co_mixer_balance[0] * 0.001;
            if (n8 != 0.5) {
                if (n8 > 0.5) {
                    this.out_mixer_left *= (float)((1.0 - n8) * 2.0);
                }
                else {
                    this.out_mixer_right *= (float)(n8 * 2.0);
                }
            }
            if (this.synthesizer.reverb_on) {
                this.out_mixer_effect1 = (float)(this.co_mixer_reverb[0] * 0.001);
                this.out_mixer_effect1 *= n6;
            }
            else {
                this.out_mixer_effect1 = 0.0f;
            }
            if (this.synthesizer.chorus_on) {
                this.out_mixer_effect2 = (float)(this.co_mixer_chorus[0] * 0.001);
                this.out_mixer_effect2 *= n6;
            }
            else {
                this.out_mixer_effect2 = 0.0f;
            }
            this.out_mixer_end = (this.co_mixer_active[0] < 0.5);
            if (!this.on && !this.osc_stream_off_transmitted) {
                this.osc_stream_off_transmitted = true;
                if (this.osc_stream != null) {
                    this.osc_stream.noteOff(this.noteOff_velocity);
                }
            }
        }
        if (this.started) {
            this.last_out_mixer_left = this.out_mixer_left;
            this.last_out_mixer_right = this.out_mixer_right;
            this.last_out_mixer_effect1 = this.out_mixer_effect1;
            this.last_out_mixer_effect2 = this.out_mixer_effect2;
            this.started = false;
        }
    }
    
    void mixAudioStream(final SoftAudioBuffer softAudioBuffer, final SoftAudioBuffer softAudioBuffer2, final SoftAudioBuffer softAudioBuffer3, final float n, final float n2) {
        final int size = softAudioBuffer.getSize();
        if (n < 1.0E-9 && n2 < 1.0E-9) {
            return;
        }
        if (softAudioBuffer3 != null && this.delay != 0) {
            if (n == n2) {
                final float[] array = softAudioBuffer2.array();
                final float[] array2 = softAudioBuffer.array();
                int n3 = 0;
                for (int i = this.delay; i < size; ++i) {
                    final float[] array3 = array;
                    final int n4 = i;
                    array3[n4] += array2[n3++] * n2;
                }
                final float[] array4 = softAudioBuffer3.array();
                for (int j = 0; j < this.delay; ++j) {
                    final float[] array5 = array4;
                    final int n5 = j;
                    array5[n5] += array2[n3++] * n2;
                }
            }
            else {
                float n6 = n;
                final float n7 = (n2 - n) / size;
                final float[] array6 = softAudioBuffer2.array();
                final float[] array7 = softAudioBuffer.array();
                int n8 = 0;
                for (int k = this.delay; k < size; ++k) {
                    n6 += n7;
                    final float[] array8 = array6;
                    final int n9 = k;
                    array8[n9] += array7[n8++] * n6;
                }
                final float[] array9 = softAudioBuffer3.array();
                for (int l = 0; l < this.delay; ++l) {
                    n6 += n7;
                    final float[] array10 = array9;
                    final int n10 = l;
                    array10[n10] += array7[n8++] * n6;
                }
            }
        }
        else if (n == n2) {
            final float[] array11 = softAudioBuffer2.array();
            final float[] array12 = softAudioBuffer.array();
            for (int n11 = 0; n11 < size; ++n11) {
                final float[] array13 = array11;
                final int n12 = n11;
                array13[n12] += array12[n11] * n2;
            }
        }
        else {
            float n13 = n;
            final float n14 = (n2 - n) / size;
            final float[] array14 = softAudioBuffer2.array();
            final float[] array15 = softAudioBuffer.array();
            for (int n15 = 0; n15 < size; ++n15) {
                n13 += n14;
                final float[] array16 = array14;
                final int n16 = n15;
                array16[n16] += array15[n15] * n13;
            }
        }
    }
    
    void processAudioLogic(final SoftAudioBuffer[] array) {
        if (!this.audiostarted) {
            return;
        }
        final int size = array[0].getSize();
        try {
            this.osc_buff[0] = array[10].array();
            if (this.nrofchannels != 1) {
                this.osc_buff[1] = array[11].array();
            }
            final int read = this.osc_stream.read(this.osc_buff, 0, size);
            if (read == -1) {
                this.stopping = true;
                return;
            }
            if (read != size) {
                Arrays.fill(this.osc_buff[0], read, size, 0.0f);
                if (this.nrofchannels != 1) {
                    Arrays.fill(this.osc_buff[1], read, size, 0.0f);
                }
            }
        }
        catch (final IOException ex) {}
        final SoftAudioBuffer softAudioBuffer = array[0];
        final SoftAudioBuffer softAudioBuffer2 = array[1];
        final SoftAudioBuffer softAudioBuffer3 = array[2];
        final SoftAudioBuffer softAudioBuffer4 = array[6];
        final SoftAudioBuffer softAudioBuffer5 = array[7];
        final SoftAudioBuffer softAudioBuffer6 = array[3];
        final SoftAudioBuffer softAudioBuffer7 = array[4];
        final SoftAudioBuffer softAudioBuffer8 = array[5];
        final SoftAudioBuffer softAudioBuffer9 = array[8];
        final SoftAudioBuffer softAudioBuffer10 = array[9];
        final SoftAudioBuffer softAudioBuffer11 = array[10];
        SoftAudioBuffer softAudioBuffer12 = array[11];
        if (this.osc_stream_nrofchannels == 1) {
            softAudioBuffer12 = null;
        }
        if (!Double.isInfinite(this.co_filter_freq[0])) {
            this.filter_left.processAudio(softAudioBuffer11);
            if (softAudioBuffer12 != null) {
                this.filter_right.processAudio(softAudioBuffer12);
            }
        }
        if (this.nrofchannels == 1) {
            this.out_mixer_left = (this.out_mixer_left + this.out_mixer_right) / 2.0f;
            this.mixAudioStream(softAudioBuffer11, softAudioBuffer, softAudioBuffer6, this.last_out_mixer_left, this.out_mixer_left);
            if (softAudioBuffer12 != null) {
                this.mixAudioStream(softAudioBuffer12, softAudioBuffer, softAudioBuffer6, this.last_out_mixer_left, this.out_mixer_left);
            }
        }
        else if (softAudioBuffer12 == null && this.last_out_mixer_left == this.last_out_mixer_right && this.out_mixer_left == this.out_mixer_right) {
            this.mixAudioStream(softAudioBuffer11, softAudioBuffer3, softAudioBuffer8, this.last_out_mixer_left, this.out_mixer_left);
        }
        else {
            this.mixAudioStream(softAudioBuffer11, softAudioBuffer, softAudioBuffer6, this.last_out_mixer_left, this.out_mixer_left);
            if (softAudioBuffer12 != null) {
                this.mixAudioStream(softAudioBuffer12, softAudioBuffer2, softAudioBuffer7, this.last_out_mixer_right, this.out_mixer_right);
            }
            else {
                this.mixAudioStream(softAudioBuffer11, softAudioBuffer2, softAudioBuffer7, this.last_out_mixer_right, this.out_mixer_right);
            }
        }
        if (softAudioBuffer12 == null) {
            this.mixAudioStream(softAudioBuffer11, softAudioBuffer4, softAudioBuffer9, this.last_out_mixer_effect1, this.out_mixer_effect1);
            this.mixAudioStream(softAudioBuffer11, softAudioBuffer5, softAudioBuffer10, this.last_out_mixer_effect2, this.out_mixer_effect2);
        }
        else {
            this.mixAudioStream(softAudioBuffer11, softAudioBuffer4, softAudioBuffer9, this.last_out_mixer_effect1 * 0.5f, this.out_mixer_effect1 * 0.5f);
            this.mixAudioStream(softAudioBuffer11, softAudioBuffer5, softAudioBuffer10, this.last_out_mixer_effect2 * 0.5f, this.out_mixer_effect2 * 0.5f);
            this.mixAudioStream(softAudioBuffer12, softAudioBuffer4, softAudioBuffer9, this.last_out_mixer_effect1 * 0.5f, this.out_mixer_effect1 * 0.5f);
            this.mixAudioStream(softAudioBuffer12, softAudioBuffer5, softAudioBuffer10, this.last_out_mixer_effect2 * 0.5f, this.out_mixer_effect2 * 0.5f);
        }
        this.last_out_mixer_left = this.out_mixer_left;
        this.last_out_mixer_right = this.out_mixer_right;
        this.last_out_mixer_effect1 = this.out_mixer_effect1;
        this.last_out_mixer_effect2 = this.out_mixer_effect2;
        if (this.out_mixer_end) {
            this.stopping = true;
        }
    }
}
