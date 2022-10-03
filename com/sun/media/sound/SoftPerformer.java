package com.sun.media.sound;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Comparator;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public final class SoftPerformer
{
    static ModelConnectionBlock[] defaultconnections;
    public int keyFrom;
    public int keyTo;
    public int velFrom;
    public int velTo;
    public int exclusiveClass;
    public boolean selfNonExclusive;
    public boolean forcedVelocity;
    public boolean forcedKeynumber;
    public ModelPerformer performer;
    public ModelConnectionBlock[] connections;
    public ModelOscillator[] oscillators;
    public Map<Integer, int[]> midi_rpn_connections;
    public Map<Integer, int[]> midi_nrpn_connections;
    public int[][] midi_ctrl_connections;
    public int[][] midi_connections;
    public int[] ctrl_connections;
    private List<Integer> ctrl_connections_list;
    private static KeySortComparator keySortComparator;
    
    private String extractKeys(final ModelConnectionBlock modelConnectionBlock) {
        final StringBuffer sb = new StringBuffer();
        if (modelConnectionBlock.getSources() != null) {
            sb.append("[");
            final ModelSource[] sources = modelConnectionBlock.getSources();
            final ModelSource[] array = new ModelSource[sources.length];
            for (int i = 0; i < sources.length; ++i) {
                array[i] = sources[i];
            }
            Arrays.sort(array, SoftPerformer.keySortComparator);
            for (int j = 0; j < sources.length; ++j) {
                sb.append(sources[j].getIdentifier());
                sb.append(";");
            }
            sb.append("]");
        }
        sb.append(";");
        if (modelConnectionBlock.getDestination() != null) {
            sb.append(modelConnectionBlock.getDestination().getIdentifier());
        }
        sb.append(";");
        return sb.toString();
    }
    
    private void processSource(final ModelSource modelSource, final int n) {
        final String object = modelSource.getIdentifier().getObject();
        if (object.equals("midi_cc")) {
            this.processMidiControlSource(modelSource, n);
        }
        else if (object.equals("midi_rpn")) {
            this.processMidiRpnSource(modelSource, n);
        }
        else if (object.equals("midi_nrpn")) {
            this.processMidiNrpnSource(modelSource, n);
        }
        else if (object.equals("midi")) {
            this.processMidiSource(modelSource, n);
        }
        else if (object.equals("noteon")) {
            this.processNoteOnSource(modelSource, n);
        }
        else {
            if (object.equals("osc")) {
                return;
            }
            if (object.equals("mixer")) {
                return;
            }
            this.ctrl_connections_list.add(n);
        }
    }
    
    private void processMidiControlSource(final ModelSource modelSource, final int n) {
        final String variable = modelSource.getIdentifier().getVariable();
        if (variable == null) {
            return;
        }
        final int int1 = Integer.parseInt(variable);
        if (this.midi_ctrl_connections[int1] == null) {
            this.midi_ctrl_connections[int1] = new int[] { n };
        }
        else {
            final int[] array = this.midi_ctrl_connections[int1];
            final int[] array2 = new int[array.length + 1];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = array[i];
            }
            array2[array2.length - 1] = n;
            this.midi_ctrl_connections[int1] = array2;
        }
    }
    
    private void processNoteOnSource(final ModelSource modelSource, final int n) {
        final String variable = modelSource.getIdentifier().getVariable();
        int n2 = -1;
        if (variable.equals("on")) {
            n2 = 3;
        }
        if (variable.equals("keynumber")) {
            n2 = 4;
        }
        if (n2 == -1) {
            return;
        }
        if (this.midi_connections[n2] == null) {
            this.midi_connections[n2] = new int[] { n };
        }
        else {
            final int[] array = this.midi_connections[n2];
            final int[] array2 = new int[array.length + 1];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = array[i];
            }
            array2[array2.length - 1] = n;
            this.midi_connections[n2] = array2;
        }
    }
    
    private void processMidiSource(final ModelSource modelSource, final int n) {
        final String variable = modelSource.getIdentifier().getVariable();
        int n2 = -1;
        if (variable.equals("pitch")) {
            n2 = 0;
        }
        if (variable.equals("channel_pressure")) {
            n2 = 1;
        }
        if (variable.equals("poly_pressure")) {
            n2 = 2;
        }
        if (n2 == -1) {
            return;
        }
        if (this.midi_connections[n2] == null) {
            this.midi_connections[n2] = new int[] { n };
        }
        else {
            final int[] array = this.midi_connections[n2];
            final int[] array2 = new int[array.length + 1];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = array[i];
            }
            array2[array2.length - 1] = n;
            this.midi_connections[n2] = array2;
        }
    }
    
    private void processMidiRpnSource(final ModelSource modelSource, final int n) {
        final String variable = modelSource.getIdentifier().getVariable();
        if (variable == null) {
            return;
        }
        final int int1 = Integer.parseInt(variable);
        if (this.midi_rpn_connections.get(int1) == null) {
            this.midi_rpn_connections.put(int1, new int[] { n });
        }
        else {
            final int[] array = this.midi_rpn_connections.get(int1);
            final int[] array2 = new int[array.length + 1];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = array[i];
            }
            array2[array2.length - 1] = n;
            this.midi_rpn_connections.put(int1, array2);
        }
    }
    
    private void processMidiNrpnSource(final ModelSource modelSource, final int n) {
        final String variable = modelSource.getIdentifier().getVariable();
        if (variable == null) {
            return;
        }
        final int int1 = Integer.parseInt(variable);
        if (this.midi_nrpn_connections.get(int1) == null) {
            this.midi_nrpn_connections.put(int1, new int[] { n });
        }
        else {
            final int[] array = this.midi_nrpn_connections.get(int1);
            final int[] array2 = new int[array.length + 1];
            for (int i = 0; i < array.length; ++i) {
                array2[i] = array[i];
            }
            array2[array2.length - 1] = n;
            this.midi_nrpn_connections.put(int1, array2);
        }
    }
    
    public SoftPerformer(final ModelPerformer performer) {
        this.keyFrom = 0;
        this.keyTo = 127;
        this.velFrom = 0;
        this.velTo = 127;
        this.exclusiveClass = 0;
        this.selfNonExclusive = false;
        this.forcedVelocity = false;
        this.forcedKeynumber = false;
        this.midi_rpn_connections = new HashMap<Integer, int[]>();
        this.midi_nrpn_connections = new HashMap<Integer, int[]>();
        this.ctrl_connections_list = new ArrayList<Integer>();
        this.performer = performer;
        this.keyFrom = performer.getKeyFrom();
        this.keyTo = performer.getKeyTo();
        this.velFrom = performer.getVelFrom();
        this.velTo = performer.getVelTo();
        this.exclusiveClass = performer.getExclusiveClass();
        this.selfNonExclusive = performer.isSelfNonExclusive();
        final HashMap hashMap = new HashMap();
        final ArrayList list = new ArrayList();
        list.addAll(performer.getConnectionBlocks());
        if (performer.isDefaultConnectionsEnabled()) {
            boolean b = false;
            for (int i = 0; i < list.size(); ++i) {
                final ModelConnectionBlock modelConnectionBlock = (ModelConnectionBlock)list.get(i);
                final ModelSource[] sources = modelConnectionBlock.getSources();
                final ModelDestination destination = modelConnectionBlock.getDestination();
                boolean b2 = false;
                if (destination != null && sources != null && sources.length > 1) {
                    for (int j = 0; j < sources.length; ++j) {
                        if (sources[j].getIdentifier().getObject().equals("midi_cc") && sources[j].getIdentifier().getVariable().equals("1")) {
                            b2 = true;
                            b = true;
                            break;
                        }
                    }
                }
                if (b2) {
                    final ModelConnectionBlock modelConnectionBlock2 = new ModelConnectionBlock();
                    modelConnectionBlock2.setSources(modelConnectionBlock.getSources());
                    modelConnectionBlock2.setDestination(modelConnectionBlock.getDestination());
                    modelConnectionBlock2.addSource(new ModelSource(new ModelIdentifier("midi_rpn", "5")));
                    modelConnectionBlock2.setScale(modelConnectionBlock.getScale() * 256.0);
                    list.set(i, modelConnectionBlock2);
                }
            }
            if (!b) {
                final ModelConnectionBlock modelConnectionBlock3 = new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true, 0), new ModelSource(new ModelIdentifier("midi_cc", "1", 0), false, false, 0), 50.0, new ModelDestination(ModelDestination.DESTINATION_PITCH));
                modelConnectionBlock3.addSource(new ModelSource(new ModelIdentifier("midi_rpn", "5")));
                modelConnectionBlock3.setScale(modelConnectionBlock3.getScale() * 256.0);
                list.add(modelConnectionBlock3);
            }
            boolean b3 = false;
            boolean b4 = false;
            ModelConnectionBlock modelConnectionBlock4 = null;
            int n = 0;
            for (final ModelConnectionBlock modelConnectionBlock5 : list) {
                final ModelSource[] sources2 = modelConnectionBlock5.getSources();
                if (modelConnectionBlock5.getDestination() != null && sources2 != null) {
                    for (int k = 0; k < sources2.length; ++k) {
                        final ModelIdentifier identifier = sources2[k].getIdentifier();
                        if (identifier.getObject().equals("midi_cc") && identifier.getVariable().equals("1")) {
                            modelConnectionBlock4 = modelConnectionBlock5;
                            n = k;
                        }
                        if (identifier.getObject().equals("midi")) {
                            if (identifier.getVariable().equals("channel_pressure")) {
                                b3 = true;
                            }
                            if (identifier.getVariable().equals("poly_pressure")) {
                                b4 = true;
                            }
                        }
                    }
                }
            }
            if (modelConnectionBlock4 != null) {
                if (!b3) {
                    final ModelConnectionBlock modelConnectionBlock6 = new ModelConnectionBlock();
                    modelConnectionBlock6.setDestination(modelConnectionBlock4.getDestination());
                    modelConnectionBlock6.setScale(modelConnectionBlock4.getScale());
                    final ModelSource[] sources3 = modelConnectionBlock4.getSources();
                    final ModelSource[] sources4 = new ModelSource[sources3.length];
                    for (int l = 0; l < sources4.length; ++l) {
                        sources4[l] = sources3[l];
                    }
                    sources4[n] = new ModelSource(new ModelIdentifier("midi", "channel_pressure"));
                    modelConnectionBlock6.setSources(sources4);
                    hashMap.put(this.extractKeys(modelConnectionBlock6), modelConnectionBlock6);
                }
                if (!b4) {
                    final ModelConnectionBlock modelConnectionBlock7 = new ModelConnectionBlock();
                    modelConnectionBlock7.setDestination(modelConnectionBlock4.getDestination());
                    modelConnectionBlock7.setScale(modelConnectionBlock4.getScale());
                    final ModelSource[] sources5 = modelConnectionBlock4.getSources();
                    final ModelSource[] sources6 = new ModelSource[sources5.length];
                    for (int n2 = 0; n2 < sources6.length; ++n2) {
                        sources6[n2] = sources5[n2];
                    }
                    sources6[n] = new ModelSource(new ModelIdentifier("midi", "poly_pressure"));
                    modelConnectionBlock7.setSources(sources6);
                    hashMap.put(this.extractKeys(modelConnectionBlock7), modelConnectionBlock7);
                }
            }
            ModelConnectionBlock modelConnectionBlock8 = null;
            for (final ModelConnectionBlock modelConnectionBlock9 : list) {
                final ModelSource[] sources7 = modelConnectionBlock9.getSources();
                if (sources7.length != 0 && sources7[0].getIdentifier().getObject().equals("lfo") && modelConnectionBlock9.getDestination().getIdentifier().equals(ModelDestination.DESTINATION_PITCH)) {
                    if (modelConnectionBlock8 == null) {
                        modelConnectionBlock8 = modelConnectionBlock9;
                    }
                    else if (modelConnectionBlock8.getSources().length > sources7.length) {
                        modelConnectionBlock8 = modelConnectionBlock9;
                    }
                    else {
                        if (modelConnectionBlock8.getSources()[0].getIdentifier().getInstance() >= 1 || modelConnectionBlock8.getSources()[0].getIdentifier().getInstance() <= sources7[0].getIdentifier().getInstance()) {
                            continue;
                        }
                        modelConnectionBlock8 = modelConnectionBlock9;
                    }
                }
            }
            int instance = 1;
            if (modelConnectionBlock8 != null) {
                instance = modelConnectionBlock8.getSources()[0].getIdentifier().getInstance();
            }
            final ModelConnectionBlock modelConnectionBlock10 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "78"), false, true, 0), 2000.0, new ModelDestination(new ModelIdentifier("lfo", "delay2", instance)));
            hashMap.put(this.extractKeys(modelConnectionBlock10), modelConnectionBlock10);
            final ModelConnectionBlock modelConnectionBlock11 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("lfo", instance)), new ModelSource(new ModelIdentifier("midi_cc", "77"), new ModelTransform() {
                double s = this.val$scale;
                final /* synthetic */ double val$scale = (modelConnectionBlock8 == null) ? 0.0 : modelConnectionBlock8.getScale();
                
                @Override
                public double transform(double n) {
                    n = n * 2.0 - 1.0;
                    n *= 600.0;
                    if (this.s == 0.0) {
                        return n;
                    }
                    if (this.s > 0.0) {
                        if (n < -this.s) {
                            n = -this.s;
                        }
                        return n;
                    }
                    if (n < this.s) {
                        n = -this.s;
                    }
                    return -n;
                }
            }), new ModelDestination(ModelDestination.DESTINATION_PITCH));
            hashMap.put(this.extractKeys(modelConnectionBlock11), modelConnectionBlock11);
            final ModelConnectionBlock modelConnectionBlock12 = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "76"), false, true, 0), 2400.0, new ModelDestination(new ModelIdentifier("lfo", "freq", instance)));
            hashMap.put(this.extractKeys(modelConnectionBlock12), modelConnectionBlock12);
        }
        if (performer.isDefaultConnectionsEnabled()) {
            for (final ModelConnectionBlock modelConnectionBlock13 : SoftPerformer.defaultconnections) {
                hashMap.put(this.extractKeys(modelConnectionBlock13), modelConnectionBlock13);
            }
        }
        for (final ModelConnectionBlock modelConnectionBlock14 : list) {
            hashMap.put(this.extractKeys(modelConnectionBlock14), modelConnectionBlock14);
        }
        final ArrayList list2 = new ArrayList();
        this.midi_ctrl_connections = new int[128][];
        for (int n4 = 0; n4 < this.midi_ctrl_connections.length; ++n4) {
            this.midi_ctrl_connections[n4] = null;
        }
        this.midi_connections = new int[5][];
        for (int n5 = 0; n5 < this.midi_connections.length; ++n5) {
            this.midi_connections[n5] = null;
        }
        int n6 = 0;
        int n7 = 0;
        for (final ModelConnectionBlock modelConnectionBlock15 : hashMap.values()) {
            if (modelConnectionBlock15.getDestination() != null) {
                final ModelIdentifier identifier2 = modelConnectionBlock15.getDestination().getIdentifier();
                if (identifier2.getObject().equals("noteon")) {
                    n7 = 1;
                    if (identifier2.getVariable().equals("keynumber")) {
                        this.forcedKeynumber = true;
                    }
                    if (identifier2.getVariable().equals("velocity")) {
                        this.forcedVelocity = true;
                    }
                }
            }
            if (n7 != 0) {
                list2.add(0, modelConnectionBlock15);
                n7 = 0;
            }
            else {
                list2.add(modelConnectionBlock15);
            }
        }
        for (final ModelConnectionBlock modelConnectionBlock16 : list2) {
            if (modelConnectionBlock16.getSources() != null) {
                final ModelSource[] sources8 = modelConnectionBlock16.getSources();
                for (int n8 = 0; n8 < sources8.length; ++n8) {
                    this.processSource(sources8[n8], n6);
                }
            }
            ++n6;
        }
        list2.toArray(this.connections = new ModelConnectionBlock[list2.size()]);
        this.ctrl_connections = new int[this.ctrl_connections_list.size()];
        for (int n9 = 0; n9 < this.ctrl_connections.length; ++n9) {
            this.ctrl_connections[n9] = this.ctrl_connections_list.get(n9);
        }
        this.oscillators = new ModelOscillator[performer.getOscillators().size()];
        performer.getOscillators().toArray(this.oscillators);
        for (final ModelConnectionBlock modelConnectionBlock17 : list2) {
            if (modelConnectionBlock17.getDestination() != null && isUnnecessaryTransform(modelConnectionBlock17.getDestination().getTransform())) {
                modelConnectionBlock17.getDestination().setTransform(null);
            }
            if (modelConnectionBlock17.getSources() != null) {
                for (final ModelSource modelSource : modelConnectionBlock17.getSources()) {
                    if (isUnnecessaryTransform(modelSource.getTransform())) {
                        modelSource.setTransform(null);
                    }
                }
            }
        }
    }
    
    private static boolean isUnnecessaryTransform(final ModelTransform modelTransform) {
        if (modelTransform == null) {
            return false;
        }
        if (!(modelTransform instanceof ModelStandardTransform)) {
            return false;
        }
        final ModelStandardTransform modelStandardTransform = (ModelStandardTransform)modelTransform;
        return !modelStandardTransform.getDirection() && !modelStandardTransform.getPolarity() && modelStandardTransform.getTransform() != 0 && false;
    }
    
    static {
        SoftPerformer.defaultconnections = new ModelConnectionBlock[42];
        int n = 0;
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "on", 0), false, false, 0), 1.0, new ModelDestination(new ModelIdentifier("eg", "on", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "on", 0), false, false, 0), 1.0, new ModelDestination(new ModelIdentifier("eg", "on", 1)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("eg", "active", 0), false, false, 0), 1.0, new ModelDestination(new ModelIdentifier("mixer", "active", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("eg", 0), true, false, 0), -960.0, new ModelDestination(new ModelIdentifier("mixer", "gain")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "velocity"), true, false, 1), -960.0, new ModelDestination(new ModelIdentifier("mixer", "gain")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi", "pitch"), false, true, 0), new ModelSource(new ModelIdentifier("midi_rpn", "0"), new ModelTransform() {
            @Override
            public double transform(final double n) {
                final int n2 = (int)(n * 16384.0);
                return (n2 >> 7) * 100 + (n2 & 0x7F);
            }
        }), new ModelDestination(new ModelIdentifier("osc", "pitch")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("noteon", "keynumber"), false, false, 0), 12800.0, new ModelDestination(new ModelIdentifier("osc", "pitch")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "7"), true, false, 1), -960.0, new ModelDestination(new ModelIdentifier("mixer", "gain")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "8"), false, false, 0), 1000.0, new ModelDestination(new ModelIdentifier("mixer", "balance")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "10"), false, false, 0), 1000.0, new ModelDestination(new ModelIdentifier("mixer", "pan")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "11"), true, false, 1), -960.0, new ModelDestination(new ModelIdentifier("mixer", "gain")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "91"), false, false, 0), 1000.0, new ModelDestination(new ModelIdentifier("mixer", "reverb")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "93"), false, false, 0), 1000.0, new ModelDestination(new ModelIdentifier("mixer", "chorus")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "71"), false, true, 0), 200.0, new ModelDestination(new ModelIdentifier("filter", "q")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "74"), false, true, 0), 9600.0, new ModelDestination(new ModelIdentifier("filter", "freq")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "72"), false, true, 0), 6000.0, new ModelDestination(new ModelIdentifier("eg", "release2")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "73"), false, true, 0), 2000.0, new ModelDestination(new ModelIdentifier("eg", "attack2")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "75"), false, true, 0), 6000.0, new ModelDestination(new ModelIdentifier("eg", "decay2")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "67"), false, false, 3), -50.0, new ModelDestination(ModelDestination.DESTINATION_GAIN));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_cc", "67"), false, false, 3), -2400.0, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_rpn", "1"), false, true, 0), 100.0, new ModelDestination(new ModelIdentifier("osc", "pitch")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("midi_rpn", "2"), false, true, 0), 12800.0, new ModelDestination(new ModelIdentifier("osc", "pitch")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("master", "fine_tuning"), false, true, 0), 100.0, new ModelDestination(new ModelIdentifier("osc", "pitch")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(new ModelSource(new ModelIdentifier("master", "coarse_tuning"), false, true, 0), 12800.0, new ModelDestination(new ModelIdentifier("osc", "pitch")));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(13500.0, new ModelDestination(new ModelIdentifier("filter", "freq", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "delay", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "attack", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "hold", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "decay", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(1000.0, new ModelDestination(new ModelIdentifier("eg", "sustain", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "release", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(1200.0 * Math.log(0.015) / Math.log(2.0), new ModelDestination(new ModelIdentifier("eg", "shutdown", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "delay", 1)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "attack", 1)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "hold", 1)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "decay", 1)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(1000.0, new ModelDestination(new ModelIdentifier("eg", "sustain", 1)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("eg", "release", 1)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(-8.51318, new ModelDestination(new ModelIdentifier("lfo", "freq", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("lfo", "delay", 0)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(-8.51318, new ModelDestination(new ModelIdentifier("lfo", "freq", 1)));
        SoftPerformer.defaultconnections[n++] = new ModelConnectionBlock(Double.NEGATIVE_INFINITY, new ModelDestination(new ModelIdentifier("lfo", "delay", 1)));
        SoftPerformer.keySortComparator = new KeySortComparator();
    }
    
    private static class KeySortComparator implements Comparator<ModelSource>
    {
        @Override
        public int compare(final ModelSource modelSource, final ModelSource modelSource2) {
            return modelSource.getIdentifier().toString().compareTo(modelSource2.getIdentifier().toString());
        }
    }
}
