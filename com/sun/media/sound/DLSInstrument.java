package com.sun.media.sound;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import java.util.List;

public final class DLSInstrument extends ModelInstrument
{
    int preset;
    int bank;
    boolean druminstrument;
    byte[] guid;
    DLSInfo info;
    List<DLSRegion> regions;
    List<DLSModulator> modulators;
    
    public DLSInstrument() {
        super(null, null, null, null);
        this.preset = 0;
        this.bank = 0;
        this.druminstrument = false;
        this.guid = null;
        this.info = new DLSInfo();
        this.regions = new ArrayList<DLSRegion>();
        this.modulators = new ArrayList<DLSModulator>();
    }
    
    public DLSInstrument(final DLSSoundbank dlsSoundbank) {
        super(dlsSoundbank, null, null, null);
        this.preset = 0;
        this.bank = 0;
        this.druminstrument = false;
        this.guid = null;
        this.info = new DLSInfo();
        this.regions = new ArrayList<DLSRegion>();
        this.modulators = new ArrayList<DLSModulator>();
    }
    
    public DLSInfo getInfo() {
        return this.info;
    }
    
    @Override
    public String getName() {
        return this.info.name;
    }
    
    public void setName(final String name) {
        this.info.name = name;
    }
    
    @Override
    public ModelPatch getPatch() {
        return new ModelPatch(this.bank, this.preset, this.druminstrument);
    }
    
    public void setPatch(final Patch patch) {
        if (patch instanceof ModelPatch && ((ModelPatch)patch).isPercussion()) {
            this.druminstrument = true;
            this.bank = patch.getBank();
            this.preset = patch.getProgram();
        }
        else {
            this.druminstrument = false;
            this.bank = patch.getBank();
            this.preset = patch.getProgram();
        }
    }
    
    @Override
    public Object getData() {
        return null;
    }
    
    public List<DLSRegion> getRegions() {
        return this.regions;
    }
    
    public List<DLSModulator> getModulators() {
        return this.modulators;
    }
    
    @Override
    public String toString() {
        if (this.druminstrument) {
            return "Drumkit: " + this.info.name + " bank #" + this.bank + " preset #" + this.preset;
        }
        return "Instrument: " + this.info.name + " bank #" + this.bank + " preset #" + this.preset;
    }
    
    private ModelIdentifier convertToModelDest(final int n) {
        if (n == 0) {
            return null;
        }
        if (n == 1) {
            return ModelDestination.DESTINATION_GAIN;
        }
        if (n == 3) {
            return ModelDestination.DESTINATION_PITCH;
        }
        if (n == 4) {
            return ModelDestination.DESTINATION_PAN;
        }
        if (n == 260) {
            return ModelDestination.DESTINATION_LFO1_FREQ;
        }
        if (n == 261) {
            return ModelDestination.DESTINATION_LFO1_DELAY;
        }
        if (n == 518) {
            return ModelDestination.DESTINATION_EG1_ATTACK;
        }
        if (n == 519) {
            return ModelDestination.DESTINATION_EG1_DECAY;
        }
        if (n == 521) {
            return ModelDestination.DESTINATION_EG1_RELEASE;
        }
        if (n == 522) {
            return ModelDestination.DESTINATION_EG1_SUSTAIN;
        }
        if (n == 778) {
            return ModelDestination.DESTINATION_EG2_ATTACK;
        }
        if (n == 779) {
            return ModelDestination.DESTINATION_EG2_DECAY;
        }
        if (n == 781) {
            return ModelDestination.DESTINATION_EG2_RELEASE;
        }
        if (n == 782) {
            return ModelDestination.DESTINATION_EG2_SUSTAIN;
        }
        if (n == 5) {
            return ModelDestination.DESTINATION_KEYNUMBER;
        }
        if (n == 128) {
            return ModelDestination.DESTINATION_CHORUS;
        }
        if (n == 129) {
            return ModelDestination.DESTINATION_REVERB;
        }
        if (n == 276) {
            return ModelDestination.DESTINATION_LFO2_FREQ;
        }
        if (n == 277) {
            return ModelDestination.DESTINATION_LFO2_DELAY;
        }
        if (n == 523) {
            return ModelDestination.DESTINATION_EG1_DELAY;
        }
        if (n == 524) {
            return ModelDestination.DESTINATION_EG1_HOLD;
        }
        if (n == 525) {
            return ModelDestination.DESTINATION_EG1_SHUTDOWN;
        }
        if (n == 783) {
            return ModelDestination.DESTINATION_EG2_DELAY;
        }
        if (n == 784) {
            return ModelDestination.DESTINATION_EG2_HOLD;
        }
        if (n == 1280) {
            return ModelDestination.DESTINATION_FILTER_FREQ;
        }
        if (n == 1281) {
            return ModelDestination.DESTINATION_FILTER_Q;
        }
        return null;
    }
    
    private ModelIdentifier convertToModelSrc(final int n) {
        if (n == 0) {
            return null;
        }
        if (n == 1) {
            return ModelSource.SOURCE_LFO1;
        }
        if (n == 2) {
            return ModelSource.SOURCE_NOTEON_VELOCITY;
        }
        if (n == 3) {
            return ModelSource.SOURCE_NOTEON_KEYNUMBER;
        }
        if (n == 4) {
            return ModelSource.SOURCE_EG1;
        }
        if (n == 5) {
            return ModelSource.SOURCE_EG2;
        }
        if (n == 6) {
            return ModelSource.SOURCE_MIDI_PITCH;
        }
        if (n == 129) {
            return new ModelIdentifier("midi_cc", "1", 0);
        }
        if (n == 135) {
            return new ModelIdentifier("midi_cc", "7", 0);
        }
        if (n == 138) {
            return new ModelIdentifier("midi_cc", "10", 0);
        }
        if (n == 139) {
            return new ModelIdentifier("midi_cc", "11", 0);
        }
        if (n == 256) {
            return new ModelIdentifier("midi_rpn", "0", 0);
        }
        if (n == 257) {
            return new ModelIdentifier("midi_rpn", "1", 0);
        }
        if (n == 7) {
            return ModelSource.SOURCE_MIDI_POLY_PRESSURE;
        }
        if (n == 8) {
            return ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE;
        }
        if (n == 9) {
            return ModelSource.SOURCE_LFO2;
        }
        if (n == 10) {
            return ModelSource.SOURCE_MIDI_CHANNEL_PRESSURE;
        }
        if (n == 219) {
            return new ModelIdentifier("midi_cc", "91", 0);
        }
        if (n == 221) {
            return new ModelIdentifier("midi_cc", "93", 0);
        }
        return null;
    }
    
    private ModelConnectionBlock convertToModel(final DLSModulator dlsModulator) {
        final ModelIdentifier convertToModelSrc = this.convertToModelSrc(dlsModulator.getSource());
        final ModelIdentifier convertToModelSrc2 = this.convertToModelSrc(dlsModulator.getControl());
        final ModelIdentifier convertToModelDest = this.convertToModelDest(dlsModulator.getDestination());
        final int scale = dlsModulator.getScale();
        double scale2;
        if (scale == Integer.MIN_VALUE) {
            scale2 = Double.NEGATIVE_INFINITY;
        }
        else {
            scale2 = scale / 65536.0;
        }
        if (convertToModelDest != null) {
            ModelSource modelSource = null;
            ModelSource modelSource2 = null;
            final ModelConnectionBlock modelConnectionBlock = new ModelConnectionBlock();
            if (convertToModelSrc2 != null) {
                final ModelSource modelSource3 = new ModelSource();
                if (convertToModelSrc2 == ModelSource.SOURCE_MIDI_PITCH) {
                    ((ModelStandardTransform)modelSource3.getTransform()).setPolarity(true);
                }
                else if (convertToModelSrc2 == ModelSource.SOURCE_LFO1 || convertToModelSrc2 == ModelSource.SOURCE_LFO2) {
                    ((ModelStandardTransform)modelSource3.getTransform()).setPolarity(true);
                }
                modelSource3.setIdentifier(convertToModelSrc2);
                modelConnectionBlock.addSource(modelSource3);
                modelSource2 = modelSource3;
            }
            if (convertToModelSrc != null) {
                final ModelSource modelSource4 = new ModelSource();
                if (convertToModelSrc == ModelSource.SOURCE_MIDI_PITCH) {
                    ((ModelStandardTransform)modelSource4.getTransform()).setPolarity(true);
                }
                else if (convertToModelSrc == ModelSource.SOURCE_LFO1 || convertToModelSrc == ModelSource.SOURCE_LFO2) {
                    ((ModelStandardTransform)modelSource4.getTransform()).setPolarity(true);
                }
                modelSource4.setIdentifier(convertToModelSrc);
                modelConnectionBlock.addSource(modelSource4);
                modelSource = modelSource4;
            }
            final ModelDestination destination = new ModelDestination();
            destination.setIdentifier(convertToModelDest);
            modelConnectionBlock.setDestination(destination);
            if (dlsModulator.getVersion() == 1) {
                if (dlsModulator.getTransform() == 1) {
                    if (modelSource != null) {
                        ((ModelStandardTransform)modelSource.getTransform()).setTransform(1);
                        ((ModelStandardTransform)modelSource.getTransform()).setDirection(true);
                    }
                    if (modelSource2 != null) {
                        ((ModelStandardTransform)modelSource2.getTransform()).setTransform(1);
                        ((ModelStandardTransform)modelSource2.getTransform()).setDirection(true);
                    }
                }
            }
            else if (dlsModulator.getVersion() == 2) {
                final int transform = dlsModulator.getTransform();
                final int n = transform >> 15 & 0x1;
                final int n2 = transform >> 14 & 0x1;
                final int n3 = transform >> 10 & 0x8;
                final int n4 = transform >> 9 & 0x1;
                final int n5 = transform >> 8 & 0x1;
                final int n6 = transform >> 4 & 0x8;
                if (modelSource != null) {
                    int transform2 = 0;
                    if (n3 == 3) {
                        transform2 = 3;
                    }
                    if (n3 == 1) {
                        transform2 = 1;
                    }
                    if (n3 == 2) {
                        transform2 = 2;
                    }
                    ((ModelStandardTransform)modelSource.getTransform()).setTransform(transform2);
                    ((ModelStandardTransform)modelSource.getTransform()).setPolarity(n2 == 1);
                    ((ModelStandardTransform)modelSource.getTransform()).setDirection(n == 1);
                }
                if (modelSource2 != null) {
                    int transform3 = 0;
                    if (n6 == 3) {
                        transform3 = 3;
                    }
                    if (n6 == 1) {
                        transform3 = 1;
                    }
                    if (n6 == 2) {
                        transform3 = 2;
                    }
                    ((ModelStandardTransform)modelSource2.getTransform()).setTransform(transform3);
                    ((ModelStandardTransform)modelSource2.getTransform()).setPolarity(n5 == 1);
                    ((ModelStandardTransform)modelSource2.getTransform()).setDirection(n4 == 1);
                }
            }
            modelConnectionBlock.setScale(scale2);
            return modelConnectionBlock;
        }
        return null;
    }
    
    @Override
    public ModelPerformer[] getPerformers() {
        final ArrayList list = new ArrayList();
        final HashMap hashMap = new HashMap();
        for (final DLSModulator dlsModulator : this.getModulators()) {
            hashMap.put(dlsModulator.getSource() + "x" + dlsModulator.getControl() + "=" + dlsModulator.getDestination(), dlsModulator);
        }
        final HashMap hashMap2 = new HashMap();
        for (final DLSRegion dlsRegion : this.regions) {
            final ModelPerformer modelPerformer = new ModelPerformer();
            modelPerformer.setName(dlsRegion.getSample().getName());
            modelPerformer.setSelfNonExclusive((dlsRegion.getFusoptions() & 0x1) != 0x0);
            modelPerformer.setExclusiveClass(dlsRegion.getExclusiveClass());
            modelPerformer.setKeyFrom(dlsRegion.getKeyfrom());
            modelPerformer.setKeyTo(dlsRegion.getKeyto());
            modelPerformer.setVelFrom(dlsRegion.getVelfrom());
            modelPerformer.setVelTo(dlsRegion.getVelto());
            hashMap2.clear();
            hashMap2.putAll(hashMap);
            for (final DLSModulator dlsModulator2 : dlsRegion.getModulators()) {
                hashMap2.put(dlsModulator2.getSource() + "x" + dlsModulator2.getControl() + "=" + dlsModulator2.getDestination(), dlsModulator2);
            }
            final List<ModelConnectionBlock> connectionBlocks = modelPerformer.getConnectionBlocks();
            final Iterator iterator4 = hashMap2.values().iterator();
            while (iterator4.hasNext()) {
                final ModelConnectionBlock convertToModel = this.convertToModel((DLSModulator)iterator4.next());
                if (convertToModel != null) {
                    connectionBlocks.add(convertToModel);
                }
            }
            final DLSSample sample = dlsRegion.getSample();
            DLSSampleOptions dlsSampleOptions = dlsRegion.getSampleoptions();
            if (dlsSampleOptions == null) {
                dlsSampleOptions = sample.getSampleoptions();
            }
            final ModelByteBufferWavetable modelByteBufferWavetable = new ModelByteBufferWavetable(sample.getDataBuffer(), sample.getFormat(), (float)(-dlsSampleOptions.unitynote * 100 + dlsSampleOptions.finetune));
            modelByteBufferWavetable.setAttenuation(modelByteBufferWavetable.getAttenuation() / 65536.0f);
            if (dlsSampleOptions.getLoops().size() != 0) {
                final DLSSampleLoop dlsSampleLoop = dlsSampleOptions.getLoops().get(0);
                modelByteBufferWavetable.setLoopStart((float)(int)dlsSampleLoop.getStart());
                modelByteBufferWavetable.setLoopLength((float)(int)dlsSampleLoop.getLength());
                if (dlsSampleLoop.getType() == 0L) {
                    modelByteBufferWavetable.setLoopType(1);
                }
                if (dlsSampleLoop.getType() == 1L) {
                    modelByteBufferWavetable.setLoopType(2);
                }
                else {
                    modelByteBufferWavetable.setLoopType(1);
                }
            }
            modelPerformer.getConnectionBlocks().add(new ModelConnectionBlock(1.0, new ModelDestination(new ModelIdentifier("filter", "type", 1))));
            modelPerformer.getOscillators().add(modelByteBufferWavetable);
            list.add(modelPerformer);
        }
        return (ModelPerformer[])list.toArray(new ModelPerformer[list.size()]);
    }
    
    public byte[] getGuid() {
        return (byte[])((this.guid == null) ? null : Arrays.copyOf(this.guid, this.guid.length));
    }
    
    public void setGuid(final byte[] array) {
        this.guid = (byte[])((array == null) ? null : Arrays.copyOf(array, array.length));
    }
}
