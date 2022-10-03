package com.sun.media.sound;

import javax.sound.midi.ShortMessage;
import javax.sound.midi.MidiMessage;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import javax.sound.midi.Patch;
import java.util.Set;
import javax.sound.sampled.AudioInputStream;
import java.util.TreeMap;

public final class SoftMainMixer
{
    public static final int CHANNEL_LEFT = 0;
    public static final int CHANNEL_RIGHT = 1;
    public static final int CHANNEL_MONO = 2;
    public static final int CHANNEL_DELAY_LEFT = 3;
    public static final int CHANNEL_DELAY_RIGHT = 4;
    public static final int CHANNEL_DELAY_MONO = 5;
    public static final int CHANNEL_EFFECT1 = 6;
    public static final int CHANNEL_EFFECT2 = 7;
    public static final int CHANNEL_DELAY_EFFECT1 = 8;
    public static final int CHANNEL_DELAY_EFFECT2 = 9;
    public static final int CHANNEL_LEFT_DRY = 10;
    public static final int CHANNEL_RIGHT_DRY = 11;
    public static final int CHANNEL_SCRATCH1 = 12;
    public static final int CHANNEL_SCRATCH2 = 13;
    boolean active_sensing_on;
    private long msec_last_activity;
    private boolean pusher_silent;
    private int pusher_silent_count;
    private long sample_pos;
    boolean readfully;
    private final Object control_mutex;
    private SoftSynthesizer synth;
    private float samplerate;
    private int nrofchannels;
    private SoftVoice[] voicestatus;
    private SoftAudioBuffer[] buffers;
    private SoftReverb reverb;
    private SoftAudioProcessor chorus;
    private SoftAudioProcessor agc;
    private long msec_buffer_len;
    private int buffer_len;
    TreeMap<Long, Object> midimessages;
    private int delay_midievent;
    private int max_delay_midievent;
    double last_volume_left;
    double last_volume_right;
    private double[] co_master_balance;
    private double[] co_master_volume;
    private double[] co_master_coarse_tuning;
    private double[] co_master_fine_tuning;
    private AudioInputStream ais;
    private Set<SoftChannelMixerContainer> registeredMixers;
    private Set<ModelChannelMixer> stoppedMixers;
    private SoftChannelMixerContainer[] cur_registeredMixers;
    SoftControl co_master;
    
    private void processSystemExclusiveMessage(final byte[] array) {
        synchronized (this.synth.control_mutex) {
            this.activity();
            Label_0525: {
                if ((array[1] & 0xFF) == 0x7E) {
                    final int n = array[2] & 0xFF;
                    if (n == 127 || n == this.synth.getDeviceID()) {
                        switch (array[3] & 0xFF) {
                            case 8: {
                                switch (array[4] & 0xFF) {
                                    case 1: {
                                        this.synth.getTuning(new Patch(0, array[5] & 0xFF)).load(array);
                                        break Label_0525;
                                    }
                                    case 4:
                                    case 5:
                                    case 6:
                                    case 7: {
                                        this.synth.getTuning(new Patch(array[5] & 0xFF, array[6] & 0xFF)).load(array);
                                        break Label_0525;
                                    }
                                    case 8:
                                    case 9: {
                                        final SoftTuning tuning = new SoftTuning(array);
                                        final int n2 = (array[5] & 0xFF) * 16384 + (array[6] & 0xFF) * 128 + (array[7] & 0xFF);
                                        final SoftChannel[] channels = this.synth.channels;
                                        for (int i = 0; i < channels.length; ++i) {
                                            if ((n2 & 1 << i) != 0x0) {
                                                channels[i].tuning = tuning;
                                            }
                                        }
                                        break Label_0525;
                                    }
                                    default: {
                                        break Label_0525;
                                    }
                                }
                                break;
                            }
                            case 9: {
                                switch (array[4] & 0xFF) {
                                    case 1: {
                                        this.synth.setGeneralMidiMode(1);
                                        this.reset();
                                        break Label_0525;
                                    }
                                    case 2: {
                                        this.synth.setGeneralMidiMode(0);
                                        this.reset();
                                        break Label_0525;
                                    }
                                    case 3: {
                                        this.synth.setGeneralMidiMode(2);
                                        this.reset();
                                        break Label_0525;
                                    }
                                    default: {
                                        break Label_0525;
                                    }
                                }
                                break;
                            }
                            case 10: {
                                switch (array[4] & 0xFF) {
                                    case 1: {
                                        if (this.synth.getGeneralMidiMode() == 0) {
                                            this.synth.setGeneralMidiMode(1);
                                        }
                                        this.synth.voice_allocation_mode = 1;
                                        this.reset();
                                        break Label_0525;
                                    }
                                    case 2: {
                                        this.synth.setGeneralMidiMode(0);
                                        this.synth.voice_allocation_mode = 0;
                                        this.reset();
                                        break Label_0525;
                                    }
                                    case 3: {
                                        this.synth.voice_allocation_mode = 0;
                                        break Label_0525;
                                    }
                                    case 4: {
                                        this.synth.voice_allocation_mode = 1;
                                        break Label_0525;
                                    }
                                    default: {
                                        break Label_0525;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
            Label_1901: {
                if ((array[1] & 0xFF) == 0x7F) {
                    final int n3 = array[2] & 0xFF;
                    if (n3 == 127 || n3 == this.synth.getDeviceID()) {
                        switch (array[3] & 0xFF) {
                            case 4: {
                                final int n4 = array[4] & 0xFF;
                                switch (n4) {
                                    case 1:
                                    case 2:
                                    case 3:
                                    case 4: {
                                        final int n5 = (array[5] & 0x7F) + (array[6] & 0x7F) * 128;
                                        if (n4 == 1) {
                                            this.setVolume(n5);
                                            break Label_1901;
                                        }
                                        if (n4 == 2) {
                                            this.setBalance(n5);
                                            break Label_1901;
                                        }
                                        if (n4 == 3) {
                                            this.setFineTuning(n5);
                                            break Label_1901;
                                        }
                                        if (n4 == 4) {
                                            this.setCoarseTuning(n5);
                                            break Label_1901;
                                        }
                                        break Label_1901;
                                    }
                                    case 5: {
                                        int n6 = 5;
                                        final int n7 = array[n6++] & 0xFF;
                                        final int n8 = array[n6++] & 0xFF;
                                        final int n9 = array[n6++] & 0xFF;
                                        final int[] array2 = new int[n7];
                                        for (int j = 0; j < n7; ++j) {
                                            array2[j] = (array[n6++] & 0xFF) * 128 + (array[n6++] & 0xFF);
                                        }
                                        final int n10 = (array.length - 1 - n6) / (n8 + n9);
                                        final long[] array3 = new long[n10];
                                        final long[] array4 = new long[n10];
                                        for (int k = 0; k < n10; ++k) {
                                            array4[k] = 0L;
                                            for (int l = 0; l < n8; ++l) {
                                                array3[k] = array3[k] * 128L + (array[n6++] & 0xFF);
                                            }
                                            for (int n11 = 0; n11 < n9; ++n11) {
                                                array4[k] = array4[k] * 128L + (array[n6++] & 0xFF);
                                            }
                                        }
                                        this.globalParameterControlChange(array2, array3, array4);
                                        break Label_1901;
                                    }
                                    default: {
                                        break Label_1901;
                                    }
                                }
                                break;
                            }
                            case 8: {
                                switch (array[4] & 0xFF) {
                                    case 2: {
                                        final SoftTuning tuning2 = this.synth.getTuning(new Patch(0, array[5] & 0xFF));
                                        tuning2.load(array);
                                        final SoftVoice[] voices = this.synth.getVoices();
                                        for (int n12 = 0; n12 < voices.length; ++n12) {
                                            if (voices[n12].active && voices[n12].tuning == tuning2) {
                                                voices[n12].updateTuning(tuning2);
                                            }
                                        }
                                        break Label_1901;
                                    }
                                    case 7: {
                                        final SoftTuning tuning3 = this.synth.getTuning(new Patch(array[5] & 0xFF, array[6] & 0xFF));
                                        tuning3.load(array);
                                        final SoftVoice[] voices2 = this.synth.getVoices();
                                        for (int n13 = 0; n13 < voices2.length; ++n13) {
                                            if (voices2[n13].active && voices2[n13].tuning == tuning3) {
                                                voices2[n13].updateTuning(tuning3);
                                            }
                                        }
                                        break Label_1901;
                                    }
                                    case 8:
                                    case 9: {
                                        final SoftTuning tuning4 = new SoftTuning(array);
                                        final int n14 = (array[5] & 0xFF) * 16384 + (array[6] & 0xFF) * 128 + (array[7] & 0xFF);
                                        final SoftChannel[] channels2 = this.synth.channels;
                                        for (int n15 = 0; n15 < channels2.length; ++n15) {
                                            if ((n14 & 1 << n15) != 0x0) {
                                                channels2[n15].tuning = tuning4;
                                            }
                                        }
                                        final SoftVoice[] voices3 = this.synth.getVoices();
                                        for (int n16 = 0; n16 < voices3.length; ++n16) {
                                            if (voices3[n16].active && (n14 & 1 << voices3[n16].channel) != 0x0) {
                                                voices3[n16].updateTuning(tuning4);
                                            }
                                        }
                                        break Label_1901;
                                    }
                                    default: {
                                        break Label_1901;
                                    }
                                }
                                break;
                            }
                            case 9: {
                                switch (array[4] & 0xFF) {
                                    case 1: {
                                        final int[] array5 = new int[(array.length - 7) / 2];
                                        final int[] array6 = new int[(array.length - 7) / 2];
                                        int n17 = 0;
                                        for (int n18 = 6; n18 < array.length - 1; n18 += 2) {
                                            array5[n17] = (array[n18] & 0xFF);
                                            array6[n17] = (array[n18 + 1] & 0xFF);
                                            ++n17;
                                        }
                                        this.synth.channels[array[5] & 0xFF].mapChannelPressureToDestination(array5, array6);
                                        break Label_1901;
                                    }
                                    case 2: {
                                        final int[] array7 = new int[(array.length - 7) / 2];
                                        final int[] array8 = new int[(array.length - 7) / 2];
                                        int n19 = 0;
                                        for (int n20 = 6; n20 < array.length - 1; n20 += 2) {
                                            array7[n19] = (array[n20] & 0xFF);
                                            array8[n19] = (array[n20 + 1] & 0xFF);
                                            ++n19;
                                        }
                                        this.synth.channels[array[5] & 0xFF].mapPolyPressureToDestination(array7, array8);
                                        break Label_1901;
                                    }
                                    case 3: {
                                        final int[] array9 = new int[(array.length - 7) / 2];
                                        final int[] array10 = new int[(array.length - 7) / 2];
                                        int n21 = 0;
                                        for (int n22 = 7; n22 < array.length - 1; n22 += 2) {
                                            array9[n21] = (array[n22] & 0xFF);
                                            array10[n21] = (array[n22 + 1] & 0xFF);
                                            ++n21;
                                        }
                                        this.synth.channels[array[5] & 0xFF].mapControlToDestination(array[6] & 0xFF, array9, array10);
                                        break Label_1901;
                                    }
                                    default: {
                                        break Label_1901;
                                    }
                                }
                                break;
                            }
                            case 10: {
                                switch (array[4] & 0xFF) {
                                    case 1: {
                                        final int n23 = array[5] & 0xFF;
                                        final int n24 = array[6] & 0xFF;
                                        final SoftChannel softChannel = this.synth.channels[n23];
                                        for (int n25 = 7; n25 < array.length - 1; n25 += 2) {
                                            softChannel.controlChangePerNote(n24, array[n25] & 0xFF, array[n25 + 1] & 0xFF);
                                        }
                                        break Label_1901;
                                    }
                                    default: {
                                        break Label_1901;
                                    }
                                }
                                break;
                            }
                        }
                    }
                }
            }
        }
    }
    
    private void processMessages(final long n) {
        final Iterator<Map.Entry<Long, Object>> iterator = this.midimessages.entrySet().iterator();
        while (iterator.hasNext()) {
            final Map.Entry entry = iterator.next();
            if ((long)entry.getKey() >= n + this.msec_buffer_len) {
                return;
            }
            this.delay_midievent = (int)(((long)entry.getKey() - n) * (this.samplerate / 1000000.0) + 0.5);
            if (this.delay_midievent > this.max_delay_midievent) {
                this.delay_midievent = this.max_delay_midievent;
            }
            if (this.delay_midievent < 0) {
                this.delay_midievent = 0;
            }
            this.processMessage(entry.getValue());
            iterator.remove();
        }
        this.delay_midievent = 0;
    }
    
    void processAudioBuffers() {
        // 
        // This method could not be decompiled.
        // 
        // Original Bytecode:
        // 
        //     4: getfield        com/sun/media/sound/SoftSynthesizer.weakstream:Lcom/sun/media/sound/SoftSynthesizer$WeakAudioStream;
        //     7: ifnull          55
        //    10: aload_0        
        //    11: getfield        com/sun/media/sound/SoftMainMixer.synth:Lcom/sun/media/sound/SoftSynthesizer;
        //    14: getfield        com/sun/media/sound/SoftSynthesizer.weakstream:Lcom/sun/media/sound/SoftSynthesizer$WeakAudioStream;
        //    17: getfield        com/sun/media/sound/SoftSynthesizer$WeakAudioStream.silent_samples:J
        //    20: lconst_0       
        //    21: lcmp           
        //    22: ifeq            55
        //    25: aload_0        
        //    26: dup            
        //    27: getfield        com/sun/media/sound/SoftMainMixer.sample_pos:J
        //    30: aload_0        
        //    31: getfield        com/sun/media/sound/SoftMainMixer.synth:Lcom/sun/media/sound/SoftSynthesizer;
        //    34: getfield        com/sun/media/sound/SoftSynthesizer.weakstream:Lcom/sun/media/sound/SoftSynthesizer$WeakAudioStream;
        //    37: getfield        com/sun/media/sound/SoftSynthesizer$WeakAudioStream.silent_samples:J
        //    40: ladd           
        //    41: putfield        com/sun/media/sound/SoftMainMixer.sample_pos:J
        //    44: aload_0        
        //    45: getfield        com/sun/media/sound/SoftMainMixer.synth:Lcom/sun/media/sound/SoftSynthesizer;
        //    48: getfield        com/sun/media/sound/SoftSynthesizer.weakstream:Lcom/sun/media/sound/SoftSynthesizer$WeakAudioStream;
        //    51: lconst_0       
        //    52: putfield        com/sun/media/sound/SoftSynthesizer$WeakAudioStream.silent_samples:J
        //    55: iconst_0       
        //    56: istore_1       
        //    57: iload_1        
        //    58: aload_0        
        //    59: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //    62: arraylength    
        //    63: if_icmpge       108
        //    66: iload_1        
        //    67: iconst_3       
        //    68: if_icmpeq       102
        //    71: iload_1        
        //    72: iconst_4       
        //    73: if_icmpeq       102
        //    76: iload_1        
        //    77: iconst_5       
        //    78: if_icmpeq       102
        //    81: iload_1        
        //    82: bipush          8
        //    84: if_icmpeq       102
        //    87: iload_1        
        //    88: bipush          9
        //    90: if_icmpeq       102
        //    93: aload_0        
        //    94: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //    97: iload_1        
        //    98: aaload         
        //    99: invokevirtual   com/sun/media/sound/SoftAudioBuffer.clear:()V
        //   102: iinc            1, 1
        //   105: goto            57
        //   108: aload_0        
        //   109: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   112: iconst_3       
        //   113: aaload         
        //   114: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //   117: ifne            135
        //   120: aload_0        
        //   121: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   124: iconst_0       
        //   125: aaload         
        //   126: aload_0        
        //   127: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   130: iconst_3       
        //   131: aaload         
        //   132: invokevirtual   com/sun/media/sound/SoftAudioBuffer.swap:(Lcom/sun/media/sound/SoftAudioBuffer;)V
        //   135: aload_0        
        //   136: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   139: iconst_4       
        //   140: aaload         
        //   141: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //   144: ifne            162
        //   147: aload_0        
        //   148: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   151: iconst_1       
        //   152: aaload         
        //   153: aload_0        
        //   154: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   157: iconst_4       
        //   158: aaload         
        //   159: invokevirtual   com/sun/media/sound/SoftAudioBuffer.swap:(Lcom/sun/media/sound/SoftAudioBuffer;)V
        //   162: aload_0        
        //   163: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   166: iconst_5       
        //   167: aaload         
        //   168: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //   171: ifne            189
        //   174: aload_0        
        //   175: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   178: iconst_2       
        //   179: aaload         
        //   180: aload_0        
        //   181: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   184: iconst_5       
        //   185: aaload         
        //   186: invokevirtual   com/sun/media/sound/SoftAudioBuffer.swap:(Lcom/sun/media/sound/SoftAudioBuffer;)V
        //   189: aload_0        
        //   190: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   193: bipush          8
        //   195: aaload         
        //   196: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //   199: ifne            219
        //   202: aload_0        
        //   203: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   206: bipush          6
        //   208: aaload         
        //   209: aload_0        
        //   210: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   213: bipush          8
        //   215: aaload         
        //   216: invokevirtual   com/sun/media/sound/SoftAudioBuffer.swap:(Lcom/sun/media/sound/SoftAudioBuffer;)V
        //   219: aload_0        
        //   220: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   223: bipush          9
        //   225: aaload         
        //   226: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //   229: ifne            249
        //   232: aload_0        
        //   233: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   236: bipush          7
        //   238: aaload         
        //   239: aload_0        
        //   240: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   243: bipush          9
        //   245: aaload         
        //   246: invokevirtual   com/sun/media/sound/SoftAudioBuffer.swap:(Lcom/sun/media/sound/SoftAudioBuffer;)V
        //   249: aload_0        
        //   250: getfield        com/sun/media/sound/SoftMainMixer.control_mutex:Ljava/lang/Object;
        //   253: dup            
        //   254: astore          6
        //   256: monitorenter   
        //   257: aload_0        
        //   258: getfield        com/sun/media/sound/SoftMainMixer.sample_pos:J
        //   261: l2d            
        //   262: ldc2_w          1000000.0
        //   265: aload_0        
        //   266: getfield        com/sun/media/sound/SoftMainMixer.samplerate:F
        //   269: f2d            
        //   270: ddiv           
        //   271: dmul           
        //   272: d2l            
        //   273: lstore          7
        //   275: aload_0        
        //   276: lload           7
        //   278: invokespecial   com/sun/media/sound/SoftMainMixer.processMessages:(J)V
        //   281: aload_0        
        //   282: getfield        com/sun/media/sound/SoftMainMixer.active_sensing_on:Z
        //   285: ifeq            349
        //   288: lload           7
        //   290: aload_0        
        //   291: getfield        com/sun/media/sound/SoftMainMixer.msec_last_activity:J
        //   294: lsub           
        //   295: ldc2_w          1000000
        //   298: lcmp           
        //   299: ifle            349
        //   302: aload_0        
        //   303: iconst_0       
        //   304: putfield        com/sun/media/sound/SoftMainMixer.active_sensing_on:Z
        //   307: aload_0        
        //   308: getfield        com/sun/media/sound/SoftMainMixer.synth:Lcom/sun/media/sound/SoftSynthesizer;
        //   311: getfield        com/sun/media/sound/SoftSynthesizer.channels:[Lcom/sun/media/sound/SoftChannel;
        //   314: astore          9
        //   316: aload           9
        //   318: arraylength    
        //   319: istore          10
        //   321: iconst_0       
        //   322: istore          11
        //   324: iload           11
        //   326: iload           10
        //   328: if_icmpge       349
        //   331: aload           9
        //   333: iload           11
        //   335: aaload         
        //   336: astore          12
        //   338: aload           12
        //   340: invokevirtual   com/sun/media/sound/SoftChannel.allSoundOff:()V
        //   343: iinc            11, 1
        //   346: goto            324
        //   349: iconst_0       
        //   350: istore          9
        //   352: iload           9
        //   354: aload_0        
        //   355: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //   358: arraylength    
        //   359: if_icmpge       391
        //   362: aload_0        
        //   363: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //   366: iload           9
        //   368: aaload         
        //   369: getfield        com/sun/media/sound/SoftVoice.active:Z
        //   372: ifeq            385
        //   375: aload_0        
        //   376: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //   379: iload           9
        //   381: aaload         
        //   382: invokevirtual   com/sun/media/sound/SoftVoice.processControlLogic:()V
        //   385: iinc            9, 1
        //   388: goto            352
        //   391: aload_0        
        //   392: dup            
        //   393: getfield        com/sun/media/sound/SoftMainMixer.sample_pos:J
        //   396: aload_0        
        //   397: getfield        com/sun/media/sound/SoftMainMixer.buffer_len:I
        //   400: i2l            
        //   401: ladd           
        //   402: putfield        com/sun/media/sound/SoftMainMixer.sample_pos:J
        //   405: aload_0        
        //   406: getfield        com/sun/media/sound/SoftMainMixer.co_master_volume:[D
        //   409: iconst_0       
        //   410: daload         
        //   411: dstore          9
        //   413: dload           9
        //   415: dstore_1       
        //   416: dload           9
        //   418: dstore_3       
        //   419: aload_0        
        //   420: getfield        com/sun/media/sound/SoftMainMixer.co_master_balance:[D
        //   423: iconst_0       
        //   424: daload         
        //   425: dstore          11
        //   427: dload           11
        //   429: ldc2_w          0.5
        //   432: dcmpl          
        //   433: ifle            450
        //   436: dload_1        
        //   437: dconst_1       
        //   438: dload           11
        //   440: dsub           
        //   441: ldc2_w          2.0
        //   444: dmul           
        //   445: dmul           
        //   446: dstore_1       
        //   447: goto            459
        //   450: dload_3        
        //   451: dload           11
        //   453: ldc2_w          2.0
        //   456: dmul           
        //   457: dmul           
        //   458: dstore_3       
        //   459: aload_0        
        //   460: getfield        com/sun/media/sound/SoftMainMixer.chorus:Lcom/sun/media/sound/SoftAudioProcessor;
        //   463: invokeinterface com/sun/media/sound/SoftAudioProcessor.processControlLogic:()V
        //   468: aload_0        
        //   469: getfield        com/sun/media/sound/SoftMainMixer.reverb:Lcom/sun/media/sound/SoftReverb;
        //   472: invokevirtual   com/sun/media/sound/SoftReverb.processControlLogic:()V
        //   475: aload_0        
        //   476: getfield        com/sun/media/sound/SoftMainMixer.agc:Lcom/sun/media/sound/SoftAudioProcessor;
        //   479: invokeinterface com/sun/media/sound/SoftAudioProcessor.processControlLogic:()V
        //   484: aload_0        
        //   485: getfield        com/sun/media/sound/SoftMainMixer.cur_registeredMixers:[Lcom/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer;
        //   488: ifnonnull       528
        //   491: aload_0        
        //   492: getfield        com/sun/media/sound/SoftMainMixer.registeredMixers:Ljava/util/Set;
        //   495: ifnull          528
        //   498: aload_0        
        //   499: aload_0        
        //   500: getfield        com/sun/media/sound/SoftMainMixer.registeredMixers:Ljava/util/Set;
        //   503: invokeinterface java/util/Set.size:()I
        //   508: anewarray       Lcom/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer;
        //   511: putfield        com/sun/media/sound/SoftMainMixer.cur_registeredMixers:[Lcom/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer;
        //   514: aload_0        
        //   515: getfield        com/sun/media/sound/SoftMainMixer.registeredMixers:Ljava/util/Set;
        //   518: aload_0        
        //   519: getfield        com/sun/media/sound/SoftMainMixer.cur_registeredMixers:[Lcom/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer;
        //   522: invokeinterface java/util/Set.toArray:([Ljava/lang/Object;)[Ljava/lang/Object;
        //   527: pop            
        //   528: aload_0        
        //   529: getfield        com/sun/media/sound/SoftMainMixer.cur_registeredMixers:[Lcom/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer;
        //   532: astore          5
        //   534: aload           5
        //   536: ifnull          548
        //   539: aload           5
        //   541: arraylength    
        //   542: ifne            548
        //   545: aconst_null    
        //   546: astore          5
        //   548: aload           6
        //   550: monitorexit    
        //   551: goto            562
        //   554: astore          13
        //   556: aload           6
        //   558: monitorexit    
        //   559: aload           13
        //   561: athrow         
        //   562: aload           5
        //   564: ifnull          1355
        //   567: aload_0        
        //   568: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   571: iconst_0       
        //   572: aaload         
        //   573: astore          6
        //   575: aload_0        
        //   576: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   579: iconst_1       
        //   580: aaload         
        //   581: astore          7
        //   583: aload_0        
        //   584: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   587: iconst_2       
        //   588: aaload         
        //   589: astore          8
        //   591: aload_0        
        //   592: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   595: iconst_3       
        //   596: aaload         
        //   597: astore          9
        //   599: aload_0        
        //   600: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   603: iconst_4       
        //   604: aaload         
        //   605: astore          10
        //   607: aload_0        
        //   608: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   611: iconst_5       
        //   612: aaload         
        //   613: astore          11
        //   615: aload_0        
        //   616: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   619: iconst_0       
        //   620: aaload         
        //   621: invokevirtual   com/sun/media/sound/SoftAudioBuffer.getSize:()I
        //   624: istore          12
        //   626: aload_0        
        //   627: getfield        com/sun/media/sound/SoftMainMixer.nrofchannels:I
        //   630: anewarray       [F
        //   633: astore          13
        //   635: aload_0        
        //   636: getfield        com/sun/media/sound/SoftMainMixer.nrofchannels:I
        //   639: anewarray       [F
        //   642: astore          14
        //   644: aload           14
        //   646: iconst_0       
        //   647: aload           6
        //   649: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //   652: aastore        
        //   653: aload_0        
        //   654: getfield        com/sun/media/sound/SoftMainMixer.nrofchannels:I
        //   657: iconst_1       
        //   658: if_icmpeq       670
        //   661: aload           14
        //   663: iconst_1       
        //   664: aload           7
        //   666: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //   669: aastore        
        //   670: aload           5
        //   672: astore          15
        //   674: aload           15
        //   676: arraylength    
        //   677: istore          16
        //   679: iconst_0       
        //   680: istore          17
        //   682: iload           17
        //   684: iload           16
        //   686: if_icmpge       1307
        //   689: aload           15
        //   691: iload           17
        //   693: aaload         
        //   694: astore          18
        //   696: aload_0        
        //   697: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   700: iconst_0       
        //   701: aload           18
        //   703: getfield        com/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   706: iconst_0       
        //   707: aaload         
        //   708: aastore        
        //   709: aload_0        
        //   710: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   713: iconst_1       
        //   714: aload           18
        //   716: getfield        com/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   719: iconst_1       
        //   720: aaload         
        //   721: aastore        
        //   722: aload_0        
        //   723: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   726: iconst_2       
        //   727: aload           18
        //   729: getfield        com/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   732: iconst_2       
        //   733: aaload         
        //   734: aastore        
        //   735: aload_0        
        //   736: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   739: iconst_3       
        //   740: aload           18
        //   742: getfield        com/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   745: iconst_3       
        //   746: aaload         
        //   747: aastore        
        //   748: aload_0        
        //   749: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   752: iconst_4       
        //   753: aload           18
        //   755: getfield        com/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   758: iconst_4       
        //   759: aaload         
        //   760: aastore        
        //   761: aload_0        
        //   762: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   765: iconst_5       
        //   766: aload           18
        //   768: getfield        com/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   771: iconst_5       
        //   772: aaload         
        //   773: aastore        
        //   774: aload_0        
        //   775: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   778: iconst_0       
        //   779: aaload         
        //   780: invokevirtual   com/sun/media/sound/SoftAudioBuffer.clear:()V
        //   783: aload_0        
        //   784: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   787: iconst_1       
        //   788: aaload         
        //   789: invokevirtual   com/sun/media/sound/SoftAudioBuffer.clear:()V
        //   792: aload_0        
        //   793: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   796: iconst_2       
        //   797: aaload         
        //   798: invokevirtual   com/sun/media/sound/SoftAudioBuffer.clear:()V
        //   801: aload_0        
        //   802: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   805: iconst_3       
        //   806: aaload         
        //   807: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //   810: ifne            828
        //   813: aload_0        
        //   814: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   817: iconst_0       
        //   818: aaload         
        //   819: aload_0        
        //   820: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   823: iconst_3       
        //   824: aaload         
        //   825: invokevirtual   com/sun/media/sound/SoftAudioBuffer.swap:(Lcom/sun/media/sound/SoftAudioBuffer;)V
        //   828: aload_0        
        //   829: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   832: iconst_4       
        //   833: aaload         
        //   834: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //   837: ifne            855
        //   840: aload_0        
        //   841: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   844: iconst_1       
        //   845: aaload         
        //   846: aload_0        
        //   847: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   850: iconst_4       
        //   851: aaload         
        //   852: invokevirtual   com/sun/media/sound/SoftAudioBuffer.swap:(Lcom/sun/media/sound/SoftAudioBuffer;)V
        //   855: aload_0        
        //   856: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   859: iconst_5       
        //   860: aaload         
        //   861: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //   864: ifne            882
        //   867: aload_0        
        //   868: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   871: iconst_2       
        //   872: aaload         
        //   873: aload_0        
        //   874: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   877: iconst_5       
        //   878: aaload         
        //   879: invokevirtual   com/sun/media/sound/SoftAudioBuffer.swap:(Lcom/sun/media/sound/SoftAudioBuffer;)V
        //   882: aload           13
        //   884: iconst_0       
        //   885: aload_0        
        //   886: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   889: iconst_0       
        //   890: aaload         
        //   891: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //   894: aastore        
        //   895: aload_0        
        //   896: getfield        com/sun/media/sound/SoftMainMixer.nrofchannels:I
        //   899: iconst_1       
        //   900: if_icmpeq       916
        //   903: aload           13
        //   905: iconst_1       
        //   906: aload_0        
        //   907: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   910: iconst_1       
        //   911: aaload         
        //   912: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //   915: aastore        
        //   916: iconst_0       
        //   917: istore          19
        //   919: iconst_0       
        //   920: istore          20
        //   922: iload           20
        //   924: aload_0        
        //   925: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //   928: arraylength    
        //   929: if_icmpge       986
        //   932: aload_0        
        //   933: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //   936: iload           20
        //   938: aaload         
        //   939: getfield        com/sun/media/sound/SoftVoice.active:Z
        //   942: ifeq            980
        //   945: aload_0        
        //   946: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //   949: iload           20
        //   951: aaload         
        //   952: getfield        com/sun/media/sound/SoftVoice.channelmixer:Lcom/sun/media/sound/ModelChannelMixer;
        //   955: aload           18
        //   957: getfield        com/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer.mixer:Lcom/sun/media/sound/ModelChannelMixer;
        //   960: if_acmpne       980
        //   963: aload_0        
        //   964: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //   967: iload           20
        //   969: aaload         
        //   970: aload_0        
        //   971: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   974: invokevirtual   com/sun/media/sound/SoftVoice.processAudioLogic:([Lcom/sun/media/sound/SoftAudioBuffer;)V
        //   977: iconst_1       
        //   978: istore          19
        //   980: iinc            20, 1
        //   983: goto            922
        //   986: aload_0        
        //   987: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //   990: iconst_2       
        //   991: aaload         
        //   992: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //   995: ifne            1114
        //   998: aload_0        
        //   999: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1002: iconst_2       
        //  1003: aaload         
        //  1004: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1007: astore          20
        //  1009: aload_0        
        //  1010: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1013: iconst_0       
        //  1014: aaload         
        //  1015: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1018: astore          21
        //  1020: aload_0        
        //  1021: getfield        com/sun/media/sound/SoftMainMixer.nrofchannels:I
        //  1024: iconst_1       
        //  1025: if_icmpeq       1085
        //  1028: aload_0        
        //  1029: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1032: iconst_1       
        //  1033: aaload         
        //  1034: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1037: astore          22
        //  1039: iconst_0       
        //  1040: istore          23
        //  1042: iload           23
        //  1044: iload           12
        //  1046: if_icmpge       1082
        //  1049: aload           20
        //  1051: iload           23
        //  1053: faload         
        //  1054: fstore          24
        //  1056: aload           21
        //  1058: iload           23
        //  1060: dup2           
        //  1061: faload         
        //  1062: fload           24
        //  1064: fadd           
        //  1065: fastore        
        //  1066: aload           22
        //  1068: iload           23
        //  1070: dup2           
        //  1071: faload         
        //  1072: fload           24
        //  1074: fadd           
        //  1075: fastore        
        //  1076: iinc            23, 1
        //  1079: goto            1042
        //  1082: goto            1114
        //  1085: iconst_0       
        //  1086: istore          22
        //  1088: iload           22
        //  1090: iload           12
        //  1092: if_icmpge       1114
        //  1095: aload           21
        //  1097: iload           22
        //  1099: dup2           
        //  1100: faload         
        //  1101: aload           20
        //  1103: iload           22
        //  1105: faload         
        //  1106: fadd           
        //  1107: fastore        
        //  1108: iinc            22, 1
        //  1111: goto            1088
        //  1114: aload           18
        //  1116: getfield        com/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer.mixer:Lcom/sun/media/sound/ModelChannelMixer;
        //  1119: aload           13
        //  1121: iconst_0       
        //  1122: iload           12
        //  1124: invokeinterface com/sun/media/sound/ModelChannelMixer.process:([[FII)Z
        //  1129: ifne            1171
        //  1132: aload_0        
        //  1133: getfield        com/sun/media/sound/SoftMainMixer.control_mutex:Ljava/lang/Object;
        //  1136: dup            
        //  1137: astore          20
        //  1139: monitorenter   
        //  1140: aload_0        
        //  1141: getfield        com/sun/media/sound/SoftMainMixer.registeredMixers:Ljava/util/Set;
        //  1144: aload           18
        //  1146: invokeinterface java/util/Set.remove:(Ljava/lang/Object;)Z
        //  1151: pop            
        //  1152: aload_0        
        //  1153: aconst_null    
        //  1154: putfield        com/sun/media/sound/SoftMainMixer.cur_registeredMixers:[Lcom/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer;
        //  1157: aload           20
        //  1159: monitorexit    
        //  1160: goto            1171
        //  1163: astore          25
        //  1165: aload           20
        //  1167: monitorexit    
        //  1168: aload           25
        //  1170: athrow         
        //  1171: iconst_0       
        //  1172: istore          20
        //  1174: iload           20
        //  1176: aload           13
        //  1178: arraylength    
        //  1179: if_icmpge       1231
        //  1182: aload           13
        //  1184: iload           20
        //  1186: aaload         
        //  1187: astore          21
        //  1189: aload           14
        //  1191: iload           20
        //  1193: aaload         
        //  1194: astore          22
        //  1196: iconst_0       
        //  1197: istore          23
        //  1199: iload           23
        //  1201: iload           12
        //  1203: if_icmpge       1225
        //  1206: aload           22
        //  1208: iload           23
        //  1210: dup2           
        //  1211: faload         
        //  1212: aload           21
        //  1214: iload           23
        //  1216: faload         
        //  1217: fadd           
        //  1218: fastore        
        //  1219: iinc            23, 1
        //  1222: goto            1199
        //  1225: iinc            20, 1
        //  1228: goto            1174
        //  1231: iload           19
        //  1233: ifne            1301
        //  1236: aload_0        
        //  1237: getfield        com/sun/media/sound/SoftMainMixer.control_mutex:Ljava/lang/Object;
        //  1240: dup            
        //  1241: astore          20
        //  1243: monitorenter   
        //  1244: aload_0        
        //  1245: getfield        com/sun/media/sound/SoftMainMixer.stoppedMixers:Ljava/util/Set;
        //  1248: ifnull          1287
        //  1251: aload_0        
        //  1252: getfield        com/sun/media/sound/SoftMainMixer.stoppedMixers:Ljava/util/Set;
        //  1255: aload           18
        //  1257: invokeinterface java/util/Set.contains:(Ljava/lang/Object;)Z
        //  1262: ifeq            1287
        //  1265: aload_0        
        //  1266: getfield        com/sun/media/sound/SoftMainMixer.stoppedMixers:Ljava/util/Set;
        //  1269: aload           18
        //  1271: invokeinterface java/util/Set.remove:(Ljava/lang/Object;)Z
        //  1276: pop            
        //  1277: aload           18
        //  1279: getfield        com/sun/media/sound/SoftMainMixer$SoftChannelMixerContainer.mixer:Lcom/sun/media/sound/ModelChannelMixer;
        //  1282: invokeinterface com/sun/media/sound/ModelChannelMixer.stop:()V
        //  1287: aload           20
        //  1289: monitorexit    
        //  1290: goto            1301
        //  1293: astore          26
        //  1295: aload           20
        //  1297: monitorexit    
        //  1298: aload           26
        //  1300: athrow         
        //  1301: iinc            17, 1
        //  1304: goto            682
        //  1307: aload_0        
        //  1308: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1311: iconst_0       
        //  1312: aload           6
        //  1314: aastore        
        //  1315: aload_0        
        //  1316: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1319: iconst_1       
        //  1320: aload           7
        //  1322: aastore        
        //  1323: aload_0        
        //  1324: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1327: iconst_2       
        //  1328: aload           8
        //  1330: aastore        
        //  1331: aload_0        
        //  1332: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1335: iconst_3       
        //  1336: aload           9
        //  1338: aastore        
        //  1339: aload_0        
        //  1340: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1343: iconst_4       
        //  1344: aload           10
        //  1346: aastore        
        //  1347: aload_0        
        //  1348: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1351: iconst_5       
        //  1352: aload           11
        //  1354: aastore        
        //  1355: iconst_0       
        //  1356: istore          6
        //  1358: iload           6
        //  1360: aload_0        
        //  1361: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //  1364: arraylength    
        //  1365: if_icmpge       1414
        //  1368: aload_0        
        //  1369: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //  1372: iload           6
        //  1374: aaload         
        //  1375: getfield        com/sun/media/sound/SoftVoice.active:Z
        //  1378: ifeq            1408
        //  1381: aload_0        
        //  1382: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //  1385: iload           6
        //  1387: aaload         
        //  1388: getfield        com/sun/media/sound/SoftVoice.channelmixer:Lcom/sun/media/sound/ModelChannelMixer;
        //  1391: ifnonnull       1408
        //  1394: aload_0        
        //  1395: getfield        com/sun/media/sound/SoftMainMixer.voicestatus:[Lcom/sun/media/sound/SoftVoice;
        //  1398: iload           6
        //  1400: aaload         
        //  1401: aload_0        
        //  1402: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1405: invokevirtual   com/sun/media/sound/SoftVoice.processAudioLogic:([Lcom/sun/media/sound/SoftAudioBuffer;)V
        //  1408: iinc            6, 1
        //  1411: goto            1358
        //  1414: aload_0        
        //  1415: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1418: iconst_2       
        //  1419: aaload         
        //  1420: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //  1423: ifne            1553
        //  1426: aload_0        
        //  1427: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1430: iconst_2       
        //  1431: aaload         
        //  1432: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1435: astore          6
        //  1437: aload_0        
        //  1438: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1441: iconst_0       
        //  1442: aaload         
        //  1443: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1446: astore          7
        //  1448: aload_0        
        //  1449: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1452: iconst_0       
        //  1453: aaload         
        //  1454: invokevirtual   com/sun/media/sound/SoftAudioBuffer.getSize:()I
        //  1457: istore          8
        //  1459: aload_0        
        //  1460: getfield        com/sun/media/sound/SoftMainMixer.nrofchannels:I
        //  1463: iconst_1       
        //  1464: if_icmpeq       1524
        //  1467: aload_0        
        //  1468: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1471: iconst_1       
        //  1472: aaload         
        //  1473: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1476: astore          9
        //  1478: iconst_0       
        //  1479: istore          10
        //  1481: iload           10
        //  1483: iload           8
        //  1485: if_icmpge       1521
        //  1488: aload           6
        //  1490: iload           10
        //  1492: faload         
        //  1493: fstore          11
        //  1495: aload           7
        //  1497: iload           10
        //  1499: dup2           
        //  1500: faload         
        //  1501: fload           11
        //  1503: fadd           
        //  1504: fastore        
        //  1505: aload           9
        //  1507: iload           10
        //  1509: dup2           
        //  1510: faload         
        //  1511: fload           11
        //  1513: fadd           
        //  1514: fastore        
        //  1515: iinc            10, 1
        //  1518: goto            1481
        //  1521: goto            1553
        //  1524: iconst_0       
        //  1525: istore          9
        //  1527: iload           9
        //  1529: iload           8
        //  1531: if_icmpge       1553
        //  1534: aload           7
        //  1536: iload           9
        //  1538: dup2           
        //  1539: faload         
        //  1540: aload           6
        //  1542: iload           9
        //  1544: faload         
        //  1545: fadd           
        //  1546: fastore        
        //  1547: iinc            9, 1
        //  1550: goto            1527
        //  1553: aload_0        
        //  1554: getfield        com/sun/media/sound/SoftMainMixer.synth:Lcom/sun/media/sound/SoftSynthesizer;
        //  1557: getfield        com/sun/media/sound/SoftSynthesizer.chorus_on:Z
        //  1560: ifeq            1572
        //  1563: aload_0        
        //  1564: getfield        com/sun/media/sound/SoftMainMixer.chorus:Lcom/sun/media/sound/SoftAudioProcessor;
        //  1567: invokeinterface com/sun/media/sound/SoftAudioProcessor.processAudio:()V
        //  1572: aload_0        
        //  1573: getfield        com/sun/media/sound/SoftMainMixer.synth:Lcom/sun/media/sound/SoftSynthesizer;
        //  1576: getfield        com/sun/media/sound/SoftSynthesizer.reverb_on:Z
        //  1579: ifeq            1589
        //  1582: aload_0        
        //  1583: getfield        com/sun/media/sound/SoftMainMixer.reverb:Lcom/sun/media/sound/SoftReverb;
        //  1586: invokevirtual   com/sun/media/sound/SoftReverb.processAudio:()V
        //  1589: aload_0        
        //  1590: getfield        com/sun/media/sound/SoftMainMixer.nrofchannels:I
        //  1593: iconst_1       
        //  1594: if_icmpne       1605
        //  1597: dload_1        
        //  1598: dload_3        
        //  1599: dadd           
        //  1600: ldc2_w          2.0
        //  1603: ddiv           
        //  1604: dstore_1       
        //  1605: aload_0        
        //  1606: getfield        com/sun/media/sound/SoftMainMixer.last_volume_left:D
        //  1609: dload_1        
        //  1610: dcmpl          
        //  1611: ifne            1623
        //  1614: aload_0        
        //  1615: getfield        com/sun/media/sound/SoftMainMixer.last_volume_right:D
        //  1618: dload_3        
        //  1619: dcmpl          
        //  1620: ifeq            1796
        //  1623: aload_0        
        //  1624: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1627: iconst_0       
        //  1628: aaload         
        //  1629: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1632: astore          6
        //  1634: aload_0        
        //  1635: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1638: iconst_1       
        //  1639: aaload         
        //  1640: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1643: astore          7
        //  1645: aload_0        
        //  1646: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1649: iconst_0       
        //  1650: aaload         
        //  1651: invokevirtual   com/sun/media/sound/SoftAudioBuffer.getSize:()I
        //  1654: istore          8
        //  1656: aload_0        
        //  1657: getfield        com/sun/media/sound/SoftMainMixer.last_volume_left:D
        //  1660: aload_0        
        //  1661: getfield        com/sun/media/sound/SoftMainMixer.last_volume_left:D
        //  1664: dmul           
        //  1665: d2f            
        //  1666: fstore          9
        //  1668: dload_1        
        //  1669: dload_1        
        //  1670: dmul           
        //  1671: fload           9
        //  1673: f2d            
        //  1674: dsub           
        //  1675: iload           8
        //  1677: i2d            
        //  1678: ddiv           
        //  1679: d2f            
        //  1680: fstore          10
        //  1682: iconst_0       
        //  1683: istore          11
        //  1685: iload           11
        //  1687: iload           8
        //  1689: if_icmpge       1715
        //  1692: fload           9
        //  1694: fload           10
        //  1696: fadd           
        //  1697: fstore          9
        //  1699: aload           6
        //  1701: iload           11
        //  1703: dup2           
        //  1704: faload         
        //  1705: fload           9
        //  1707: fmul           
        //  1708: fastore        
        //  1709: iinc            11, 1
        //  1712: goto            1685
        //  1715: aload_0        
        //  1716: getfield        com/sun/media/sound/SoftMainMixer.nrofchannels:I
        //  1719: iconst_1       
        //  1720: if_icmpeq       1783
        //  1723: aload_0        
        //  1724: getfield        com/sun/media/sound/SoftMainMixer.last_volume_right:D
        //  1727: aload_0        
        //  1728: getfield        com/sun/media/sound/SoftMainMixer.last_volume_right:D
        //  1731: dmul           
        //  1732: d2f            
        //  1733: fstore          9
        //  1735: dload_3        
        //  1736: dload_3        
        //  1737: dmul           
        //  1738: fload           9
        //  1740: f2d            
        //  1741: dsub           
        //  1742: iload           8
        //  1744: i2d            
        //  1745: ddiv           
        //  1746: d2f            
        //  1747: fstore          10
        //  1749: iconst_0       
        //  1750: istore          11
        //  1752: iload           11
        //  1754: iload           8
        //  1756: if_icmpge       1783
        //  1759: fload           9
        //  1761: fload           10
        //  1763: fadd           
        //  1764: fstore          9
        //  1766: aload           7
        //  1768: iload           11
        //  1770: dup2           
        //  1771: faload         
        //  1772: f2d            
        //  1773: dload_3        
        //  1774: dmul           
        //  1775: d2f            
        //  1776: fastore        
        //  1777: iinc            11, 1
        //  1780: goto            1752
        //  1783: aload_0        
        //  1784: dload_1        
        //  1785: putfield        com/sun/media/sound/SoftMainMixer.last_volume_left:D
        //  1788: aload_0        
        //  1789: dload_3        
        //  1790: putfield        com/sun/media/sound/SoftMainMixer.last_volume_right:D
        //  1793: goto            1913
        //  1796: dload_1        
        //  1797: dconst_1       
        //  1798: dcmpl          
        //  1799: ifne            1808
        //  1802: dload_3        
        //  1803: dconst_1       
        //  1804: dcmpl          
        //  1805: ifeq            1913
        //  1808: aload_0        
        //  1809: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1812: iconst_0       
        //  1813: aaload         
        //  1814: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1817: astore          6
        //  1819: aload_0        
        //  1820: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1823: iconst_1       
        //  1824: aaload         
        //  1825: invokevirtual   com/sun/media/sound/SoftAudioBuffer.array:()[F
        //  1828: astore          7
        //  1830: aload_0        
        //  1831: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1834: iconst_0       
        //  1835: aaload         
        //  1836: invokevirtual   com/sun/media/sound/SoftAudioBuffer.getSize:()I
        //  1839: istore          8
        //  1841: dload_1        
        //  1842: dload_1        
        //  1843: dmul           
        //  1844: d2f            
        //  1845: fstore          9
        //  1847: iconst_0       
        //  1848: istore          10
        //  1850: iload           10
        //  1852: iload           8
        //  1854: if_icmpge       1873
        //  1857: aload           6
        //  1859: iload           10
        //  1861: dup2           
        //  1862: faload         
        //  1863: fload           9
        //  1865: fmul           
        //  1866: fastore        
        //  1867: iinc            10, 1
        //  1870: goto            1850
        //  1873: aload_0        
        //  1874: getfield        com/sun/media/sound/SoftMainMixer.nrofchannels:I
        //  1877: iconst_1       
        //  1878: if_icmpeq       1913
        //  1881: dload_3        
        //  1882: dload_3        
        //  1883: dmul           
        //  1884: d2f            
        //  1885: fstore          9
        //  1887: iconst_0       
        //  1888: istore          10
        //  1890: iload           10
        //  1892: iload           8
        //  1894: if_icmpge       1913
        //  1897: aload           7
        //  1899: iload           10
        //  1901: dup2           
        //  1902: faload         
        //  1903: fload           9
        //  1905: fmul           
        //  1906: fastore        
        //  1907: iinc            10, 1
        //  1910: goto            1890
        //  1913: aload_0        
        //  1914: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1917: iconst_0       
        //  1918: aaload         
        //  1919: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //  1922: ifeq            2047
        //  1925: aload_0        
        //  1926: getfield        com/sun/media/sound/SoftMainMixer.buffers:[Lcom/sun/media/sound/SoftAudioBuffer;
        //  1929: iconst_1       
        //  1930: aaload         
        //  1931: invokevirtual   com/sun/media/sound/SoftAudioBuffer.isSilent:()Z
        //  1934: ifeq            2047
        //  1937: aload_0        
        //  1938: getfield        com/sun/media/sound/SoftMainMixer.control_mutex:Ljava/lang/Object;
        //  1941: dup            
        //  1942: astore          7
        //  1944: monitorenter   
        //  1945: aload_0        
        //  1946: getfield        com/sun/media/sound/SoftMainMixer.midimessages:Ljava/util/TreeMap;
        //  1949: invokevirtual   java/util/TreeMap.size:()I
        //  1952: istore          6
        //  1954: aload           7
        //  1956: monitorexit    
        //  1957: goto            1968
        //  1960: astore          27
        //  1962: aload           7
        //  1964: monitorexit    
        //  1965: aload           27
        //  1967: athrow         
        //  1968: iload           6
        //  1970: ifne            2044
        //  1973: aload_0        
        //  1974: dup            
        //  1975: getfield        com/sun/media/sound/SoftMainMixer.pusher_silent_count:I
        //  1978: iconst_1       
        //  1979: iadd           
        //  1980: putfield        com/sun/media/sound/SoftMainMixer.pusher_silent_count:I
        //  1983: aload_0        
        //  1984: getfield        com/sun/media/sound/SoftMainMixer.pusher_silent_count:I
        //  1987: iconst_5       
        //  1988: if_icmple       2044
        //  1991: aload_0        
        //  1992: iconst_0       
        //  1993: putfield        com/sun/media/sound/SoftMainMixer.pusher_silent_count:I
        //  1996: aload_0        
        //  1997: getfield        com/sun/media/sound/SoftMainMixer.control_mutex:Ljava/lang/Object;
        //  2000: dup            
        //  2001: astore          7
        //  2003: monitorenter   
        //  2004: aload_0        
        //  2005: iconst_1       
        //  2006: putfield        com/sun/media/sound/SoftMainMixer.pusher_silent:Z
        //  2009: aload_0        
        //  2010: getfield        com/sun/media/sound/SoftMainMixer.synth:Lcom/sun/media/sound/SoftSynthesizer;
        //  2013: getfield        com/sun/media/sound/SoftSynthesizer.weakstream:Lcom/sun/media/sound/SoftSynthesizer$WeakAudioStream;
        //  2016: ifnull          2030
        //  2019: aload_0        
        //  2020: getfield        com/sun/media/sound/SoftMainMixer.synth:Lcom/sun/media/sound/SoftSynthesizer;
        //  2023: getfield        com/sun/media/sound/SoftSynthesizer.weakstream:Lcom/sun/media/sound/SoftSynthesizer$WeakAudioStream;
        //  2026: aconst_null    
        //  2027: invokevirtual   com/sun/media/sound/SoftSynthesizer$WeakAudioStream.setInputStream:(Ljavax/sound/sampled/AudioInputStream;)V
        //  2030: aload           7
        //  2032: monitorexit    
        //  2033: goto            2044
        //  2036: astore          28
        //  2038: aload           7
        //  2040: monitorexit    
        //  2041: aload           28
        //  2043: athrow         
        //  2044: goto            2052
        //  2047: aload_0        
        //  2048: iconst_0       
        //  2049: putfield        com/sun/media/sound/SoftMainMixer.pusher_silent_count:I
        //  2052: aload_0        
        //  2053: getfield        com/sun/media/sound/SoftMainMixer.synth:Lcom/sun/media/sound/SoftSynthesizer;
        //  2056: getfield        com/sun/media/sound/SoftSynthesizer.agc_on:Z
        //  2059: ifeq            2071
        //  2062: aload_0        
        //  2063: getfield        com/sun/media/sound/SoftMainMixer.agc:Lcom/sun/media/sound/SoftAudioProcessor;
        //  2066: invokeinterface com/sun/media/sound/SoftAudioProcessor.processAudio:()V
        //  2071: return         
        //    StackMapTable: 00 4B 37 FC 00 01 01 2C FA 00 05 1A 1A 1A 1D 1D FF 00 4A 00 0B 07 01 11 00 00 00 00 00 07 01 10 04 07 01 13 01 01 00 00 F8 00 18 FC 00 02 01 20 FA 00 05 FF 00 3A 00 08 07 01 11 03 03 00 07 01 10 04 03 03 00 00 08 FB 00 44 FF 00 13 00 05 07 01 11 03 03 07 01 1F 07 01 10 00 00 FF 00 05 00 07 07 01 11 00 00 00 00 00 07 01 10 00 01 07 01 18 FF 00 07 00 04 07 01 11 03 03 07 01 1F 00 00 FF 00 6B 00 0D 07 01 11 03 03 07 01 1F 07 01 20 07 01 20 07 01 20 07 01 20 07 01 20 07 01 20 01 07 01 21 07 01 21 00 00 FE 00 0B 07 01 1F 01 01 FC 00 91 07 01 22 1A 1A 21 FD 00 05 01 01 39 FA 00 05 FF 00 37 00 16 07 01 11 03 03 07 01 1F 07 01 20 07 01 20 07 01 20 07 01 20 07 01 20 07 01 20 01 07 01 21 07 01 21 07 01 1F 01 01 07 01 22 01 07 00 59 07 00 59 07 00 59 01 00 00 F9 00 27 02 FC 00 02 01 F8 00 19 FF 00 30 00 13 07 01 11 03 03 07 01 1F 07 01 20 07 01 20 07 01 20 07 01 20 07 01 20 07 01 20 01 07 01 21 07 01 21 07 01 1F 01 01 07 01 22 01 07 01 10 00 01 07 01 18 FA 00 07 FC 00 02 01 FE 00 18 07 00 59 07 00 59 01 F8 00 19 FA 00 05 FC 00 37 07 01 10 45 07 01 18 F8 00 07 F8 00 05 FF 00 2F 00 04 07 01 11 03 03 07 01 1F 00 00 FC 00 02 01 31 FA 00 05 FF 00 42 00 09 07 01 11 03 03 07 01 1F 07 00 59 07 00 59 01 07 00 59 01 00 00 F9 00 27 02 FC 00 02 01 FF 00 19 00 04 07 01 11 03 03 07 01 1F 00 00 12 10 0F 11 FF 00 3D 00 0A 07 01 11 03 03 07 01 1F 07 00 59 07 00 59 01 02 02 01 00 00 FA 00 1D FC 00 24 01 FA 00 1E FF 00 0C 00 04 07 01 11 03 03 07 01 1F 00 00 0B FF 00 29 00 09 07 01 11 03 03 07 01 1F 07 00 59 07 00 59 01 02 01 00 00 FA 00 16 FC 00 10 01 FF 00 16 00 04 07 01 11 03 03 07 01 1F 00 00 FF 00 2E 00 06 07 01 11 03 03 07 01 1F 00 07 01 10 00 01 07 01 18 FF 00 07 00 05 07 01 11 03 03 07 01 1F 01 00 00 FC 00 3D 07 01 10 45 07 01 18 F9 00 07 02 04 12
        //    Exceptions:
        //  Try           Handler
        //  Start  End    Start  End    Type
        //  -----  -----  -----  -----  ----
        //  257    551    554    562    Any
        //  554    559    554    562    Any
        //  1140   1160   1163   1171   Any
        //  1163   1168   1163   1171   Any
        //  1244   1290   1293   1301   Any
        //  1293   1298   1293   1301   Any
        //  1945   1957   1960   1968   Any
        //  1960   1965   1960   1968   Any
        //  2004   2033   2036   2044   Any
        //  2036   2041   2036   2044   Any
        // 
        // The error that occurred was:
        // 
        // java.lang.IllegalArgumentException: Argument 'offset' must be in the range [0, 0], but value was: 1.
        //     at com.strobel.core.VerifyArgument.inRange(VerifyArgument.java:356)
        //     at com.strobel.assembler.ir.StackMappingVisitor.getStackValue(StackMappingVisitor.java:67)
        //     at com.strobel.decompiler.ast.AstBuilder.createModifiedStack(AstBuilder.java:2633)
        //     at com.strobel.decompiler.ast.AstBuilder.performStackAnalysis(AstBuilder.java:2088)
        //     at com.strobel.decompiler.ast.AstBuilder.build(AstBuilder.java:108)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:203)
        //     at com.strobel.decompiler.languages.java.ast.AstMethodBodyBuilder.createMethodBody(AstMethodBodyBuilder.java:93)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethodBody(AstBuilder.java:868)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createMethod(AstBuilder.java:761)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addTypeMembers(AstBuilder.java:638)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeCore(AstBuilder.java:605)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createTypeNoCache(AstBuilder.java:195)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.createType(AstBuilder.java:162)
        //     at com.strobel.decompiler.languages.java.ast.AstBuilder.addType(AstBuilder.java:137)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.buildAst(JavaLanguage.java:71)
        //     at com.strobel.decompiler.languages.java.JavaLanguage.decompileType(JavaLanguage.java:59)
        //     at com.strobel.decompiler.DecompilerDriver.decompileType(DecompilerDriver.java:333)
        //     at com.strobel.decompiler.DecompilerDriver.decompileJar(DecompilerDriver.java:254)
        //     at com.strobel.decompiler.DecompilerDriver.main(DecompilerDriver.java:144)
        // 
        throw new IllegalStateException("An error occurred while decompiling this method.");
    }
    
    public void activity() {
        long silent_samples = 0L;
        if (this.pusher_silent) {
            this.pusher_silent = false;
            if (this.synth.weakstream != null) {
                this.synth.weakstream.setInputStream(this.ais);
                silent_samples = this.synth.weakstream.silent_samples;
            }
        }
        this.msec_last_activity = (long)((this.sample_pos + silent_samples) * (1000000.0 / this.samplerate));
    }
    
    public void stopMixer(final ModelChannelMixer modelChannelMixer) {
        if (this.stoppedMixers == null) {
            this.stoppedMixers = new HashSet<ModelChannelMixer>();
        }
        this.stoppedMixers.add(modelChannelMixer);
    }
    
    public void registerMixer(final ModelChannelMixer mixer) {
        if (this.registeredMixers == null) {
            this.registeredMixers = new HashSet<SoftChannelMixerContainer>();
        }
        final SoftChannelMixerContainer softChannelMixerContainer = new SoftChannelMixerContainer();
        softChannelMixerContainer.buffers = new SoftAudioBuffer[6];
        for (int i = 0; i < softChannelMixerContainer.buffers.length; ++i) {
            softChannelMixerContainer.buffers[i] = new SoftAudioBuffer(this.buffer_len, this.synth.getFormat());
        }
        softChannelMixerContainer.mixer = mixer;
        this.registeredMixers.add(softChannelMixerContainer);
        this.cur_registeredMixers = null;
    }
    
    public SoftMainMixer(final SoftSynthesizer synth) {
        this.active_sensing_on = false;
        this.msec_last_activity = -1L;
        this.pusher_silent = false;
        this.pusher_silent_count = 0;
        this.sample_pos = 0L;
        this.readfully = true;
        this.samplerate = 44100.0f;
        this.nrofchannels = 2;
        this.voicestatus = null;
        this.msec_buffer_len = 0L;
        this.buffer_len = 0;
        this.midimessages = new TreeMap<Long, Object>();
        this.delay_midievent = 0;
        this.max_delay_midievent = 0;
        this.last_volume_left = 1.0;
        this.last_volume_right = 1.0;
        this.co_master_balance = new double[1];
        this.co_master_volume = new double[1];
        this.co_master_coarse_tuning = new double[1];
        this.co_master_fine_tuning = new double[1];
        this.registeredMixers = null;
        this.stoppedMixers = null;
        this.cur_registeredMixers = null;
        this.co_master = new SoftControl() {
            double[] balance = SoftMainMixer.this.co_master_balance;
            double[] volume = SoftMainMixer.this.co_master_volume;
            double[] coarse_tuning = SoftMainMixer.this.co_master_coarse_tuning;
            double[] fine_tuning = SoftMainMixer.this.co_master_fine_tuning;
            
            @Override
            public double[] get(final int n, final String s) {
                if (s == null) {
                    return null;
                }
                if (s.equals("balance")) {
                    return this.balance;
                }
                if (s.equals("volume")) {
                    return this.volume;
                }
                if (s.equals("coarse_tuning")) {
                    return this.coarse_tuning;
                }
                if (s.equals("fine_tuning")) {
                    return this.fine_tuning;
                }
                return null;
            }
        };
        this.synth = synth;
        this.sample_pos = 0L;
        this.co_master_balance[0] = 0.5;
        this.co_master_volume[0] = 1.0;
        this.co_master_coarse_tuning[0] = 0.5;
        this.co_master_fine_tuning[0] = 0.5;
        this.msec_buffer_len = (long)(1000000.0 / synth.getControlRate());
        this.samplerate = synth.getFormat().getSampleRate();
        this.nrofchannels = synth.getFormat().getChannels();
        final int n = (int)(synth.getFormat().getSampleRate() / synth.getControlRate());
        this.buffer_len = n;
        this.max_delay_midievent = n;
        this.control_mutex = synth.control_mutex;
        this.buffers = new SoftAudioBuffer[14];
        for (int i = 0; i < this.buffers.length; ++i) {
            this.buffers[i] = new SoftAudioBuffer(n, synth.getFormat());
        }
        this.voicestatus = synth.getVoices();
        this.reverb = new SoftReverb();
        this.chorus = new SoftChorus();
        this.agc = new SoftLimiter();
        final float sampleRate = synth.getFormat().getSampleRate();
        final float controlRate = synth.getControlRate();
        this.reverb.init(sampleRate, controlRate);
        this.chorus.init(sampleRate, controlRate);
        this.agc.init(sampleRate, controlRate);
        this.reverb.setLightMode(synth.reverb_light);
        this.reverb.setMixMode(true);
        this.chorus.setMixMode(true);
        this.agc.setMixMode(false);
        this.chorus.setInput(0, this.buffers[7]);
        this.chorus.setOutput(0, this.buffers[0]);
        if (this.nrofchannels != 1) {
            this.chorus.setOutput(1, this.buffers[1]);
        }
        this.chorus.setOutput(2, this.buffers[6]);
        this.reverb.setInput(0, this.buffers[6]);
        this.reverb.setOutput(0, this.buffers[0]);
        if (this.nrofchannels != 1) {
            this.reverb.setOutput(1, this.buffers[1]);
        }
        this.agc.setInput(0, this.buffers[0]);
        if (this.nrofchannels != 1) {
            this.agc.setInput(1, this.buffers[1]);
        }
        this.agc.setOutput(0, this.buffers[0]);
        if (this.nrofchannels != 1) {
            this.agc.setOutput(1, this.buffers[1]);
        }
        this.ais = new AudioInputStream(new InputStream() {
            private final SoftAudioBuffer[] buffers = SoftMainMixer.this.buffers;
            private final int nrofchannels = SoftMainMixer.this.synth.getFormat().getChannels();
            private final int buffersize = this.buffers[0].getSize();
            private final byte[] bbuffer = new byte[this.buffersize * (SoftMainMixer.this.synth.getFormat().getSampleSizeInBits() / 8) * this.nrofchannels];
            private int bbuffer_pos = 0;
            private final byte[] single = new byte[1];
            
            public void fillBuffer() {
                SoftMainMixer.this.processAudioBuffers();
                for (int i = 0; i < this.nrofchannels; ++i) {
                    this.buffers[i].get(this.bbuffer, i);
                }
                this.bbuffer_pos = 0;
            }
            
            @Override
            public int read(final byte[] array, int i, final int n) {
                final int length = this.bbuffer.length;
                final int n2 = i + n;
                final int n3 = i;
                final byte[] bbuffer = this.bbuffer;
                while (i < n2) {
                    if (this.available() == 0) {
                        this.fillBuffer();
                    }
                    else {
                        int bbuffer_pos;
                        for (bbuffer_pos = this.bbuffer_pos; i < n2 && bbuffer_pos < length; array[i++] = bbuffer[bbuffer_pos++]) {}
                        this.bbuffer_pos = bbuffer_pos;
                        if (!SoftMainMixer.this.readfully) {
                            return i - n3;
                        }
                        continue;
                    }
                }
                return n;
            }
            
            @Override
            public int read() throws IOException {
                if (this.read(this.single) == -1) {
                    return -1;
                }
                return this.single[0] & 0xFF;
            }
            
            @Override
            public int available() {
                return this.bbuffer.length - this.bbuffer_pos;
            }
            
            @Override
            public void close() {
                SoftMainMixer.this.synth.close();
            }
        }, synth.getFormat(), -1L);
    }
    
    public AudioInputStream getInputStream() {
        return this.ais;
    }
    
    public void reset() {
        final SoftChannel[] channels = this.synth.channels;
        for (int i = 0; i < channels.length; ++i) {
            channels[i].allSoundOff();
            channels[i].resetAllControllers(true);
            if (this.synth.getGeneralMidiMode() == 2) {
                if (i == 9) {
                    channels[i].programChange(0, 15360);
                }
                else {
                    channels[i].programChange(0, 15488);
                }
            }
            else {
                channels[i].programChange(0, 0);
            }
        }
        this.setVolume(16383);
        this.setBalance(8192);
        this.setCoarseTuning(8192);
        this.setFineTuning(8192);
        this.globalParameterControlChange(new int[] { 129 }, new long[] { 0L }, new long[] { 4L });
        this.globalParameterControlChange(new int[] { 130 }, new long[] { 0L }, new long[] { 2L });
    }
    
    public void setVolume(final int n) {
        synchronized (this.control_mutex) {
            this.co_master_volume[0] = n / 16384.0;
        }
    }
    
    public void setBalance(final int n) {
        synchronized (this.control_mutex) {
            this.co_master_balance[0] = n / 16384.0;
        }
    }
    
    public void setFineTuning(final int n) {
        synchronized (this.control_mutex) {
            this.co_master_fine_tuning[0] = n / 16384.0;
        }
    }
    
    public void setCoarseTuning(final int n) {
        synchronized (this.control_mutex) {
            this.co_master_coarse_tuning[0] = n / 16384.0;
        }
    }
    
    public int getVolume() {
        synchronized (this.control_mutex) {
            return (int)(this.co_master_volume[0] * 16384.0);
        }
    }
    
    public int getBalance() {
        synchronized (this.control_mutex) {
            return (int)(this.co_master_balance[0] * 16384.0);
        }
    }
    
    public int getFineTuning() {
        synchronized (this.control_mutex) {
            return (int)(this.co_master_fine_tuning[0] * 16384.0);
        }
    }
    
    public int getCoarseTuning() {
        synchronized (this.control_mutex) {
            return (int)(this.co_master_coarse_tuning[0] * 16384.0);
        }
    }
    
    public void globalParameterControlChange(final int[] array, final long[] array2, final long[] array3) {
        if (array.length == 0) {
            return;
        }
        synchronized (this.control_mutex) {
            if (array[0] == 129) {
                for (int i = 0; i < array3.length; ++i) {
                    this.reverb.globalParameterControlChange(array, array2[i], array3[i]);
                }
            }
            if (array[0] == 130) {
                for (int j = 0; j < array3.length; ++j) {
                    this.chorus.globalParameterControlChange(array, array2[j], array3[j]);
                }
            }
        }
    }
    
    public void processMessage(final Object o) {
        if (o instanceof byte[]) {
            this.processMessage((byte[])o);
        }
        if (o instanceof MidiMessage) {
            this.processMessage((MidiMessage)o);
        }
    }
    
    public void processMessage(final MidiMessage midiMessage) {
        if (midiMessage instanceof ShortMessage) {
            final ShortMessage shortMessage = (ShortMessage)midiMessage;
            this.processMessage(shortMessage.getChannel(), shortMessage.getCommand(), shortMessage.getData1(), shortMessage.getData2());
            return;
        }
        this.processMessage(midiMessage.getMessage());
    }
    
    public void processMessage(final byte[] array) {
        int n = 0;
        if (array.length > 0) {
            n = (array[0] & 0xFF);
        }
        if (n == 240) {
            this.processSystemExclusiveMessage(array);
            return;
        }
        final int n2 = n & 0xF0;
        final int n3 = n & 0xF;
        int n4;
        if (array.length > 1) {
            n4 = (array[1] & 0xFF);
        }
        else {
            n4 = 0;
        }
        int n5;
        if (array.length > 2) {
            n5 = (array[2] & 0xFF);
        }
        else {
            n5 = 0;
        }
        this.processMessage(n3, n2, n4, n5);
    }
    
    public void processMessage(final int n, final int n2, final int channelPressure, final int n3) {
        synchronized (this.synth.control_mutex) {
            this.activity();
        }
        if (n2 == 240) {
            switch (n2 | n) {
                case 254: {
                    synchronized (this.synth.control_mutex) {
                        this.active_sensing_on = true;
                    }
                    break;
                }
            }
            return;
        }
        final SoftChannel[] channels = this.synth.channels;
        if (n >= channels.length) {
            return;
        }
        final SoftChannel softChannel = channels[n];
        switch (n2) {
            case 144: {
                if (this.delay_midievent != 0) {
                    softChannel.noteOn(channelPressure, n3, this.delay_midievent);
                    break;
                }
                softChannel.noteOn(channelPressure, n3);
                break;
            }
            case 128: {
                softChannel.noteOff(channelPressure, n3);
                break;
            }
            case 160: {
                softChannel.setPolyPressure(channelPressure, n3);
                break;
            }
            case 176: {
                softChannel.controlChange(channelPressure, n3);
                break;
            }
            case 192: {
                softChannel.programChange(channelPressure);
                break;
            }
            case 208: {
                softChannel.setChannelPressure(channelPressure);
                break;
            }
            case 224: {
                softChannel.setPitchBend(channelPressure + n3 * 128);
                break;
            }
        }
    }
    
    public long getMicrosecondPosition() {
        if (this.pusher_silent && this.synth.weakstream != null) {
            return (long)((this.sample_pos + this.synth.weakstream.silent_samples) * (1000000.0 / this.samplerate));
        }
        return (long)(this.sample_pos * (1000000.0 / this.samplerate));
    }
    
    public void close() {
    }
    
    private class SoftChannelMixerContainer
    {
        ModelChannelMixer mixer;
        SoftAudioBuffer[] buffers;
    }
}
