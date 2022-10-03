package com.sun.media.sound;

import javax.sound.midi.MidiChannel;
import javax.sound.midi.Instrument;

public final class SoftInstrument extends Instrument
{
    private SoftPerformer[] performers;
    private ModelPerformer[] modelperformers;
    private final Object data;
    private final ModelInstrument ins;
    
    public SoftInstrument(final ModelInstrument ins) {
        super(ins.getSoundbank(), ins.getPatch(), ins.getName(), ins.getDataClass());
        this.data = ins.getData();
        this.ins = ins;
        this.initPerformers(ins.getPerformers());
    }
    
    public SoftInstrument(final ModelInstrument ins, final ModelPerformer[] array) {
        super(ins.getSoundbank(), ins.getPatch(), ins.getName(), ins.getDataClass());
        this.data = ins.getData();
        this.ins = ins;
        this.initPerformers(array);
    }
    
    private void initPerformers(final ModelPerformer[] modelperformers) {
        this.modelperformers = modelperformers;
        this.performers = new SoftPerformer[modelperformers.length];
        for (int i = 0; i < modelperformers.length; ++i) {
            this.performers[i] = new SoftPerformer(modelperformers[i]);
        }
    }
    
    public ModelDirector getDirector(final MidiChannel midiChannel, final ModelDirectedPlayer modelDirectedPlayer) {
        return this.ins.getDirector(this.modelperformers, midiChannel, modelDirectedPlayer);
    }
    
    public ModelInstrument getSourceInstrument() {
        return this.ins;
    }
    
    @Override
    public Object getData() {
        return this.data;
    }
    
    public SoftPerformer getPerformer(final int n) {
        return this.performers[n];
    }
}
