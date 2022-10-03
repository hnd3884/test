package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;

public final class ModelMappedInstrument extends ModelInstrument
{
    private final ModelInstrument ins;
    
    public ModelMappedInstrument(final ModelInstrument ins, final Patch patch) {
        super(ins.getSoundbank(), patch, ins.getName(), ins.getDataClass());
        this.ins = ins;
    }
    
    @Override
    public Object getData() {
        return this.ins.getData();
    }
    
    @Override
    public ModelPerformer[] getPerformers() {
        return this.ins.getPerformers();
    }
    
    @Override
    public ModelDirector getDirector(final ModelPerformer[] array, final MidiChannel midiChannel, final ModelDirectedPlayer modelDirectedPlayer) {
        return this.ins.getDirector(array, midiChannel, modelDirectedPlayer);
    }
    
    @Override
    public ModelChannelMixer getChannelMixer(final MidiChannel midiChannel, final AudioFormat audioFormat) {
        return this.ins.getChannelMixer(midiChannel, audioFormat);
    }
}
