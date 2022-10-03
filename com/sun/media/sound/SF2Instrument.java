package com.sun.media.sound;

import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import java.util.List;

public final class SF2Instrument extends ModelInstrument
{
    String name;
    int preset;
    int bank;
    long library;
    long genre;
    long morphology;
    SF2GlobalRegion globalregion;
    List<SF2InstrumentRegion> regions;
    
    public SF2Instrument() {
        super(null, null, null, null);
        this.name = "";
        this.preset = 0;
        this.bank = 0;
        this.library = 0L;
        this.genre = 0L;
        this.morphology = 0L;
        this.globalregion = null;
        this.regions = new ArrayList<SF2InstrumentRegion>();
    }
    
    public SF2Instrument(final SF2Soundbank sf2Soundbank) {
        super(sf2Soundbank, null, null, null);
        this.name = "";
        this.preset = 0;
        this.bank = 0;
        this.library = 0L;
        this.genre = 0L;
        this.morphology = 0L;
        this.globalregion = null;
        this.regions = new ArrayList<SF2InstrumentRegion>();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public Patch getPatch() {
        if (this.bank == 128) {
            return new ModelPatch(0, this.preset, true);
        }
        return new ModelPatch(this.bank << 7, this.preset, false);
    }
    
    public void setPatch(final Patch patch) {
        if (patch instanceof ModelPatch && ((ModelPatch)patch).isPercussion()) {
            this.bank = 128;
            this.preset = patch.getProgram();
        }
        else {
            this.bank = patch.getBank() >> 7;
            this.preset = patch.getProgram();
        }
    }
    
    @Override
    public Object getData() {
        return null;
    }
    
    public long getGenre() {
        return this.genre;
    }
    
    public void setGenre(final long genre) {
        this.genre = genre;
    }
    
    public long getLibrary() {
        return this.library;
    }
    
    public void setLibrary(final long library) {
        this.library = library;
    }
    
    public long getMorphology() {
        return this.morphology;
    }
    
    public void setMorphology(final long morphology) {
        this.morphology = morphology;
    }
    
    public List<SF2InstrumentRegion> getRegions() {
        return this.regions;
    }
    
    public SF2GlobalRegion getGlobalRegion() {
        return this.globalregion;
    }
    
    public void setGlobalZone(final SF2GlobalRegion globalregion) {
        this.globalregion = globalregion;
    }
    
    @Override
    public String toString() {
        if (this.bank == 128) {
            return "Drumkit: " + this.name + " preset #" + this.preset;
        }
        return "Instrument: " + this.name + " bank #" + this.bank + " preset #" + this.preset;
    }
    
    @Override
    public ModelPerformer[] getPerformers() {
        int n = 0;
        final Iterator<SF2InstrumentRegion> iterator = this.regions.iterator();
        while (iterator.hasNext()) {
            n += iterator.next().getLayer().getRegions().size();
        }
        final ModelPerformer[] array = new ModelPerformer[n];
        int n2 = 0;
        final SF2GlobalRegion globalregion = this.globalregion;
        for (final SF2InstrumentRegion sf2InstrumentRegion : this.regions) {
            final HashMap hashMap = new HashMap();
            hashMap.putAll(sf2InstrumentRegion.getGenerators());
            if (globalregion != null) {
                hashMap.putAll(globalregion.getGenerators());
            }
            final SF2Layer layer = sf2InstrumentRegion.getLayer();
            final SF2GlobalRegion globalRegion = layer.getGlobalRegion();
            for (final SF2LayerRegion sf2LayerRegion : layer.getRegions()) {
                final ModelPerformer modelPerformer = new ModelPerformer();
                if (sf2LayerRegion.getSample() != null) {
                    modelPerformer.setName(sf2LayerRegion.getSample().getName());
                }
                else {
                    modelPerformer.setName(layer.getName());
                }
                array[n2++] = modelPerformer;
                byte keyFrom = 0;
                byte keyTo = 127;
                byte velFrom = 0;
                byte velTo = 127;
                if (sf2LayerRegion.contains(57)) {
                    modelPerformer.setExclusiveClass(sf2LayerRegion.getInteger(57));
                }
                if (sf2LayerRegion.contains(43)) {
                    final byte[] bytes = sf2LayerRegion.getBytes(43);
                    if (bytes[0] >= 0 && bytes[0] > keyFrom) {
                        keyFrom = bytes[0];
                    }
                    if (bytes[1] >= 0 && bytes[1] < keyTo) {
                        keyTo = bytes[1];
                    }
                }
                if (sf2LayerRegion.contains(44)) {
                    final byte[] bytes2 = sf2LayerRegion.getBytes(44);
                    if (bytes2[0] >= 0 && bytes2[0] > velFrom) {
                        velFrom = bytes2[0];
                    }
                    if (bytes2[1] >= 0 && bytes2[1] < velTo) {
                        velTo = bytes2[1];
                    }
                }
                if (sf2InstrumentRegion.contains(43)) {
                    final byte[] bytes3 = sf2InstrumentRegion.getBytes(43);
                    if (bytes3[0] > keyFrom) {
                        keyFrom = bytes3[0];
                    }
                    if (bytes3[1] < keyTo) {
                        keyTo = bytes3[1];
                    }
                }
                if (sf2InstrumentRegion.contains(44)) {
                    final byte[] bytes4 = sf2InstrumentRegion.getBytes(44);
                    if (bytes4[0] > velFrom) {
                        velFrom = bytes4[0];
                    }
                    if (bytes4[1] < velTo) {
                        velTo = bytes4[1];
                    }
                }
                modelPerformer.setKeyFrom(keyFrom);
                modelPerformer.setKeyTo(keyTo);
                modelPerformer.setVelFrom(velFrom);
                modelPerformer.setVelTo(velTo);
                final short short1 = sf2LayerRegion.getShort(0);
                final short short2 = sf2LayerRegion.getShort(1);
                final short short3 = sf2LayerRegion.getShort(2);
                final short short4 = sf2LayerRegion.getShort(3);
                final int n3 = short1 + sf2LayerRegion.getShort(4) * 32768;
                final int n4 = short2 + sf2LayerRegion.getShort(12) * 32768;
                final int n5 = short3 + sf2LayerRegion.getShort(45) * 32768;
                final int n6 = short4 + sf2LayerRegion.getShort(50) * 32768;
                final int n7 = n5 - n3;
                final int n8 = n6 - n3;
                final SF2Sample sample = sf2LayerRegion.getSample();
                int n9 = sample.originalPitch;
                if (sf2LayerRegion.getShort(58) != -1) {
                    n9 = sf2LayerRegion.getShort(58);
                }
                final float n10 = (float)(-n9 * 100 + sample.pitchCorrection);
                ModelByteBuffer modelByteBuffer = sample.getDataBuffer();
                ModelByteBuffer modelByteBuffer2 = sample.getData24Buffer();
                if (n3 != 0 || n4 != 0) {
                    modelByteBuffer = modelByteBuffer.subbuffer(n3 * 2, modelByteBuffer.capacity() + n4 * 2);
                    if (modelByteBuffer2 != null) {
                        modelByteBuffer2 = modelByteBuffer2.subbuffer(n3, modelByteBuffer2.capacity() + n4);
                    }
                }
                final ModelByteBufferWavetable modelByteBufferWavetable = new ModelByteBufferWavetable(modelByteBuffer, sample.getFormat(), n10);
                if (modelByteBuffer2 != null) {
                    modelByteBufferWavetable.set8BitExtensionBuffer(modelByteBuffer2);
                }
                final HashMap hashMap2 = new HashMap<Integer, Short>();
                if (globalRegion != null) {
                    hashMap2.putAll(globalRegion.getGenerators());
                }
                hashMap2.putAll(sf2LayerRegion.getGenerators());
                for (final Map.Entry entry : hashMap.entrySet()) {
                    short n11;
                    if (!hashMap2.containsKey(entry.getKey())) {
                        n11 = sf2LayerRegion.getShort((int)entry.getKey());
                    }
                    else {
                        n11 = (short)hashMap2.get(entry.getKey());
                    }
                    hashMap2.put(entry.getKey(), (short)(n11 + (short)entry.getValue()));
                }
                final short generatorValue = this.getGeneratorValue(hashMap2, 54);
                if ((generatorValue == 1 || generatorValue == 3) && sample.startLoop >= 0L && sample.endLoop > 0L) {
                    modelByteBufferWavetable.setLoopStart((float)(int)(sample.startLoop + n7));
                    modelByteBufferWavetable.setLoopLength((float)(int)(sample.endLoop - sample.startLoop + n8 - n7));
                    if (generatorValue == 1) {
                        modelByteBufferWavetable.setLoopType(1);
                    }
                    if (generatorValue == 3) {
                        modelByteBufferWavetable.setLoopType(2);
                    }
                }
                modelPerformer.getOscillators().add(modelByteBufferWavetable);
                final short generatorValue2 = this.getGeneratorValue(hashMap2, 33);
                final short generatorValue3 = this.getGeneratorValue(hashMap2, 34);
                short generatorValue4 = this.getGeneratorValue(hashMap2, 35);
                short generatorValue5 = this.getGeneratorValue(hashMap2, 36);
                final short generatorValue6 = this.getGeneratorValue(hashMap2, 37);
                final short generatorValue7 = this.getGeneratorValue(hashMap2, 38);
                if (generatorValue4 != -12000) {
                    final short generatorValue8 = this.getGeneratorValue(hashMap2, 39);
                    generatorValue4 += (short)(60 * generatorValue8);
                    modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_NOTEON_KEYNUMBER), (float)(-generatorValue8 * 128), new ModelDestination(ModelDestination.DESTINATION_EG1_HOLD)));
                }
                if (generatorValue5 != -12000) {
                    final short generatorValue9 = this.getGeneratorValue(hashMap2, 40);
                    generatorValue5 += (short)(60 * generatorValue9);
                    modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_NOTEON_KEYNUMBER), (float)(-generatorValue9 * 128), new ModelDestination(ModelDestination.DESTINATION_EG1_DECAY)));
                }
                this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_DELAY, generatorValue2);
                this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_ATTACK, generatorValue3);
                this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_HOLD, generatorValue4);
                this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_DECAY, generatorValue5);
                short n12 = (short)(1000 - generatorValue6);
                if (n12 < 0) {
                    n12 = 0;
                }
                if (n12 > 1000) {
                    n12 = 1000;
                }
                this.addValue(modelPerformer, ModelDestination.DESTINATION_EG1_SUSTAIN, n12);
                this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG1_RELEASE, generatorValue7);
                if (this.getGeneratorValue(hashMap2, 11) != 0 || this.getGeneratorValue(hashMap2, 7) != 0) {
                    final short generatorValue10 = this.getGeneratorValue(hashMap2, 25);
                    final short generatorValue11 = this.getGeneratorValue(hashMap2, 26);
                    short generatorValue12 = this.getGeneratorValue(hashMap2, 27);
                    short generatorValue13 = this.getGeneratorValue(hashMap2, 28);
                    int generatorValue14 = this.getGeneratorValue(hashMap2, 29);
                    final short generatorValue15 = this.getGeneratorValue(hashMap2, 30);
                    if (generatorValue12 != -12000) {
                        final short generatorValue16 = this.getGeneratorValue(hashMap2, 31);
                        generatorValue12 += (short)(60 * generatorValue16);
                        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_NOTEON_KEYNUMBER), (float)(-generatorValue16 * 128), new ModelDestination(ModelDestination.DESTINATION_EG2_HOLD)));
                    }
                    if (generatorValue13 != -12000) {
                        final short generatorValue17 = this.getGeneratorValue(hashMap2, 32);
                        generatorValue13 += (short)(60 * generatorValue17);
                        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_NOTEON_KEYNUMBER), (float)(-generatorValue17 * 128), new ModelDestination(ModelDestination.DESTINATION_EG2_DECAY)));
                    }
                    this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_DELAY, generatorValue10);
                    this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_ATTACK, generatorValue11);
                    this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_HOLD, generatorValue12);
                    this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_DECAY, generatorValue13);
                    if (generatorValue14 < 0) {
                        generatorValue14 = 0;
                    }
                    if (generatorValue14 > 1000) {
                        generatorValue14 = 1000;
                    }
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_EG2_SUSTAIN, 1000 - generatorValue14);
                    this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_EG2_RELEASE, generatorValue15);
                    if (this.getGeneratorValue(hashMap2, 11) != 0) {
                        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_EG2), this.getGeneratorValue(hashMap2, 11), new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ)));
                    }
                    if (this.getGeneratorValue(hashMap2, 7) != 0) {
                        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_EG2), this.getGeneratorValue(hashMap2, 7), new ModelDestination(ModelDestination.DESTINATION_PITCH)));
                    }
                }
                if (this.getGeneratorValue(hashMap2, 10) != 0 || this.getGeneratorValue(hashMap2, 5) != 0 || this.getGeneratorValue(hashMap2, 13) != 0) {
                    final short generatorValue18 = this.getGeneratorValue(hashMap2, 22);
                    this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_LFO1_DELAY, this.getGeneratorValue(hashMap2, 21));
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_LFO1_FREQ, generatorValue18);
                }
                final short generatorValue19 = this.getGeneratorValue(hashMap2, 24);
                this.addTimecentValue(modelPerformer, ModelDestination.DESTINATION_LFO2_DELAY, this.getGeneratorValue(hashMap2, 23));
                this.addValue(modelPerformer, ModelDestination.DESTINATION_LFO2_FREQ, generatorValue19);
                if (this.getGeneratorValue(hashMap2, 6) != 0) {
                    modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO2, false, true), this.getGeneratorValue(hashMap2, 6), new ModelDestination(ModelDestination.DESTINATION_PITCH)));
                }
                if (this.getGeneratorValue(hashMap2, 10) != 0) {
                    modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true), this.getGeneratorValue(hashMap2, 10), new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ)));
                }
                if (this.getGeneratorValue(hashMap2, 5) != 0) {
                    modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true), this.getGeneratorValue(hashMap2, 5), new ModelDestination(ModelDestination.DESTINATION_PITCH)));
                }
                if (this.getGeneratorValue(hashMap2, 13) != 0) {
                    modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO1, false, true), this.getGeneratorValue(hashMap2, 13), new ModelDestination(ModelDestination.DESTINATION_GAIN)));
                }
                if (sf2LayerRegion.getShort(46) != -1) {
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_KEYNUMBER, sf2LayerRegion.getShort(46) / 128.0);
                }
                if (sf2LayerRegion.getShort(47) != -1) {
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_VELOCITY, sf2LayerRegion.getShort(47) / 128.0);
                }
                if (this.getGeneratorValue(hashMap2, 8) < 13500) {
                    final short generatorValue20 = this.getGeneratorValue(hashMap2, 8);
                    final short generatorValue21 = this.getGeneratorValue(hashMap2, 9);
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_FILTER_FREQ, generatorValue20);
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_FILTER_Q, generatorValue21);
                }
                final int n13 = 100 * this.getGeneratorValue(hashMap2, 51) + this.getGeneratorValue(hashMap2, 52);
                if (n13 != 0) {
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_PITCH, (short)n13);
                }
                if (this.getGeneratorValue(hashMap2, 17) != 0) {
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_PAN, this.getGeneratorValue(hashMap2, 17));
                }
                if (this.getGeneratorValue(hashMap2, 48) != 0) {
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_GAIN, -0.376287f * this.getGeneratorValue(hashMap2, 48));
                }
                if (this.getGeneratorValue(hashMap2, 15) != 0) {
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_CHORUS, this.getGeneratorValue(hashMap2, 15));
                }
                if (this.getGeneratorValue(hashMap2, 16) != 0) {
                    this.addValue(modelPerformer, ModelDestination.DESTINATION_REVERB, this.getGeneratorValue(hashMap2, 16));
                }
                if (this.getGeneratorValue(hashMap2, 56) != 100) {
                    final short generatorValue22 = this.getGeneratorValue(hashMap2, 56);
                    if (generatorValue22 == 0) {
                        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(null, n9 * 100, new ModelDestination(ModelDestination.DESTINATION_PITCH)));
                    }
                    else {
                        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(null, n9 * (100 - generatorValue22), new ModelDestination(ModelDestination.DESTINATION_PITCH)));
                    }
                    modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_NOTEON_KEYNUMBER), 128 * generatorValue22, new ModelDestination(ModelDestination.DESTINATION_PITCH)));
                }
                modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_NOTEON_VELOCITY, new ModelTransform() {
                    @Override
                    public double transform(final double n) {
                        if (n < 0.5) {
                            return 1.0 - n * 2.0;
                        }
                        return 0.0;
                    }
                }), -2400.0, new ModelDestination(ModelDestination.DESTINATION_FILTER_FREQ)));
                modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(new ModelSource(ModelSource.SOURCE_LFO2, false, true, 0), new ModelSource(new ModelIdentifier("midi_cc", "1", 0), false, false, 0), 50.0, new ModelDestination(ModelDestination.DESTINATION_PITCH)));
                if (layer.getGlobalRegion() != null) {
                    final Iterator<SF2Modulator> iterator5 = layer.getGlobalRegion().getModulators().iterator();
                    while (iterator5.hasNext()) {
                        this.convertModulator(modelPerformer, iterator5.next());
                    }
                }
                final Iterator<SF2Modulator> iterator6 = sf2LayerRegion.getModulators().iterator();
                while (iterator6.hasNext()) {
                    this.convertModulator(modelPerformer, iterator6.next());
                }
                if (globalregion != null) {
                    final Iterator<SF2Modulator> iterator7 = globalregion.getModulators().iterator();
                    while (iterator7.hasNext()) {
                        this.convertModulator(modelPerformer, iterator7.next());
                    }
                }
                final Iterator<SF2Modulator> iterator8 = sf2InstrumentRegion.getModulators().iterator();
                while (iterator8.hasNext()) {
                    this.convertModulator(modelPerformer, iterator8.next());
                }
            }
        }
        return array;
    }
    
    private void convertModulator(final ModelPerformer modelPerformer, final SF2Modulator sf2Modulator) {
        final ModelSource convertSource = convertSource(sf2Modulator.getSourceOperator());
        final ModelSource convertSource2 = convertSource(sf2Modulator.getAmountSourceOperator());
        if (convertSource == null && sf2Modulator.getSourceOperator() != 0) {
            return;
        }
        if (convertSource2 == null && sf2Modulator.getAmountSourceOperator() != 0) {
            return;
        }
        final double n = sf2Modulator.getAmount();
        final double[] array = { 0.0 };
        final ModelSource[] array2 = { null };
        array[0] = 1.0;
        final ModelDestination convertDestination = convertDestination(sf2Modulator.getDestinationOperator(), array, array2);
        final double n2 = n * array[0];
        if (convertDestination == null) {
            return;
        }
        if (sf2Modulator.getTransportOperator() == 2) {
            ((ModelStandardTransform)convertDestination.getTransform()).setTransform(4);
        }
        final ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock(convertSource, convertSource2, n2, convertDestination);
        if (array2[0] != null) {
            modelConnectionBlock.addSource(array2[0]);
        }
        modelPerformer.getConnectionBlocks().add(modelConnectionBlock);
    }
    
    private static ModelSource convertSource(final int n) {
        if (n == 0) {
            return null;
        }
        ModelIdentifier modelIdentifier = null;
        final int n2 = n & 0x7F;
        if ((n & 0x80) != 0x0) {
            modelIdentifier = new ModelIdentifier("midi_cc", Integer.toString(n2));
        }
        else {
            if (n2 == 2) {
                modelIdentifier = ModelSource.SOURCE_NOTEON_VELOCITY;
            }
            if (n2 == 3) {
                modelIdentifier = ModelSource.SOURCE_NOTEON_KEYNUMBER;
            }
            if (n2 == 10) {
                modelIdentifier = ModelSource.SOURCE_MIDI_POLY_PRESSURE;
            }
            if (n2 == 13) {
                modelIdentifier = ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE;
            }
            if (n2 == 14) {
                modelIdentifier = ModelSource.SOURCE_MIDI_PITCH;
            }
            if (n2 == 16) {
                modelIdentifier = new ModelIdentifier("midi_rpn", "0");
            }
        }
        if (modelIdentifier == null) {
            return null;
        }
        final ModelSource modelSource = new ModelSource(modelIdentifier);
        final ModelStandardTransform modelStandardTransform = (ModelStandardTransform)modelSource.getTransform();
        if ((0x100 & n) != 0x0) {
            modelStandardTransform.setDirection(true);
        }
        else {
            modelStandardTransform.setDirection(false);
        }
        if ((0x200 & n) != 0x0) {
            modelStandardTransform.setPolarity(true);
        }
        else {
            modelStandardTransform.setPolarity(false);
        }
        if ((0x400 & n) != 0x0) {
            modelStandardTransform.setTransform(1);
        }
        if ((0x800 & n) != 0x0) {
            modelStandardTransform.setTransform(2);
        }
        if ((0xC00 & n) != 0x0) {
            modelStandardTransform.setTransform(3);
        }
        return modelSource;
    }
    
    static ModelDestination convertDestination(final int n, final double[] array, final ModelSource[] array2) {
        ModelIdentifier modelIdentifier = null;
        switch (n) {
            case 8: {
                modelIdentifier = ModelDestination.DESTINATION_FILTER_FREQ;
                break;
            }
            case 9: {
                modelIdentifier = ModelDestination.DESTINATION_FILTER_Q;
                break;
            }
            case 15: {
                modelIdentifier = ModelDestination.DESTINATION_CHORUS;
                break;
            }
            case 16: {
                modelIdentifier = ModelDestination.DESTINATION_REVERB;
                break;
            }
            case 17: {
                modelIdentifier = ModelDestination.DESTINATION_PAN;
                break;
            }
            case 21: {
                modelIdentifier = ModelDestination.DESTINATION_LFO1_DELAY;
                break;
            }
            case 22: {
                modelIdentifier = ModelDestination.DESTINATION_LFO1_FREQ;
                break;
            }
            case 23: {
                modelIdentifier = ModelDestination.DESTINATION_LFO2_DELAY;
                break;
            }
            case 24: {
                modelIdentifier = ModelDestination.DESTINATION_LFO2_FREQ;
                break;
            }
            case 25: {
                modelIdentifier = ModelDestination.DESTINATION_EG2_DELAY;
                break;
            }
            case 26: {
                modelIdentifier = ModelDestination.DESTINATION_EG2_ATTACK;
                break;
            }
            case 27: {
                modelIdentifier = ModelDestination.DESTINATION_EG2_HOLD;
                break;
            }
            case 28: {
                modelIdentifier = ModelDestination.DESTINATION_EG2_DECAY;
                break;
            }
            case 29: {
                modelIdentifier = ModelDestination.DESTINATION_EG2_SUSTAIN;
                array[0] = -1.0;
                break;
            }
            case 30: {
                modelIdentifier = ModelDestination.DESTINATION_EG2_RELEASE;
                break;
            }
            case 33: {
                modelIdentifier = ModelDestination.DESTINATION_EG1_DELAY;
                break;
            }
            case 34: {
                modelIdentifier = ModelDestination.DESTINATION_EG1_ATTACK;
                break;
            }
            case 35: {
                modelIdentifier = ModelDestination.DESTINATION_EG1_HOLD;
                break;
            }
            case 36: {
                modelIdentifier = ModelDestination.DESTINATION_EG1_DECAY;
                break;
            }
            case 37: {
                modelIdentifier = ModelDestination.DESTINATION_EG1_SUSTAIN;
                array[0] = -1.0;
                break;
            }
            case 38: {
                modelIdentifier = ModelDestination.DESTINATION_EG1_RELEASE;
                break;
            }
            case 46: {
                modelIdentifier = ModelDestination.DESTINATION_KEYNUMBER;
                break;
            }
            case 47: {
                modelIdentifier = ModelDestination.DESTINATION_VELOCITY;
                break;
            }
            case 51: {
                array[0] = 100.0;
                modelIdentifier = ModelDestination.DESTINATION_PITCH;
                break;
            }
            case 52: {
                modelIdentifier = ModelDestination.DESTINATION_PITCH;
                break;
            }
            case 48: {
                modelIdentifier = ModelDestination.DESTINATION_GAIN;
                array[0] = -0.3762870132923126;
                break;
            }
            case 6: {
                modelIdentifier = ModelDestination.DESTINATION_PITCH;
                array2[0] = new ModelSource(ModelSource.SOURCE_LFO2, false, true);
                break;
            }
            case 5: {
                modelIdentifier = ModelDestination.DESTINATION_PITCH;
                array2[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
                break;
            }
            case 10: {
                modelIdentifier = ModelDestination.DESTINATION_FILTER_FREQ;
                array2[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
                break;
            }
            case 13: {
                modelIdentifier = ModelDestination.DESTINATION_GAIN;
                array[0] = -0.3762870132923126;
                array2[0] = new ModelSource(ModelSource.SOURCE_LFO1, false, true);
                break;
            }
            case 7: {
                modelIdentifier = ModelDestination.DESTINATION_PITCH;
                array2[0] = new ModelSource(ModelSource.SOURCE_EG2, false, true);
                break;
            }
            case 11: {
                modelIdentifier = ModelDestination.DESTINATION_FILTER_FREQ;
                array2[0] = new ModelSource(ModelSource.SOURCE_EG2, false, true);
                break;
            }
        }
        if (modelIdentifier != null) {
            return new ModelDestination(modelIdentifier);
        }
        return null;
    }
    
    private void addTimecentValue(final ModelPerformer modelPerformer, final ModelIdentifier modelIdentifier, final short n) {
        double n2;
        if (n == -12000) {
            n2 = Double.NEGATIVE_INFINITY;
        }
        else {
            n2 = n;
        }
        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(n2, new ModelDestination(modelIdentifier)));
    }
    
    private void addValue(final ModelPerformer modelPerformer, final ModelIdentifier modelIdentifier, final short n) {
        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(n, new ModelDestination(modelIdentifier)));
    }
    
    private void addValue(final ModelPerformer modelPerformer, final ModelIdentifier modelIdentifier, final double n) {
        modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(n, new ModelDestination(modelIdentifier)));
    }
    
    private short getGeneratorValue(final Map<Integer, Short> map, final int n) {
        if (map.containsKey(n)) {
            return map.get(n);
        }
        return SF2Region.getDefaultValue(n);
    }
}
