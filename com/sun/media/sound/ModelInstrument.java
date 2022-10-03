package com.sun.media.sound;

import javax.sound.sampled.AudioFormat;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import javax.sound.midi.Instrument;

public abstract class ModelInstrument extends Instrument
{
    protected ModelInstrument(final Soundbank soundbank, final Patch patch, final String s, final Class<?> clazz) {
        super(soundbank, patch, s, clazz);
    }
    
    public ModelDirector getDirector(final ModelPerformer[] array, final MidiChannel midiChannel, final ModelDirectedPlayer modelDirectedPlayer) {
        return new ModelStandardIndexedDirector(array, modelDirectedPlayer);
    }
    
    public ModelPerformer[] getPerformers() {
        return new ModelPerformer[0];
    }
    
    public ModelChannelMixer getChannelMixer(final MidiChannel midiChannel, final AudioFormat audioFormat) {
        return null;
    }
    
    public final Patch getPatchAlias() {
        final Patch patch = this.getPatch();
        final int program = patch.getProgram();
        if (patch.getBank() != 0) {
            return patch;
        }
        boolean percussion = false;
        if (this.getPatch() instanceof ModelPatch) {
            percussion = ((ModelPatch)this.getPatch()).isPercussion();
        }
        if (percussion) {
            return new Patch(15360, program);
        }
        return new Patch(15488, program);
    }
    
    public final String[] getKeys() {
        final String[] array = new String[128];
        for (final ModelPerformer modelPerformer : this.getPerformers()) {
            for (int j = modelPerformer.getKeyFrom(); j <= modelPerformer.getKeyTo(); ++j) {
                if (j >= 0 && j < 128 && array[j] == null) {
                    String name = modelPerformer.getName();
                    if (name == null) {
                        name = "untitled";
                    }
                    array[j] = name;
                }
            }
        }
        return array;
    }
    
    public final boolean[] getChannels() {
        boolean percussion = false;
        if (this.getPatch() instanceof ModelPatch) {
            percussion = ((ModelPatch)this.getPatch()).isPercussion();
        }
        if (percussion) {
            final boolean[] array = new boolean[16];
            for (int i = 0; i < array.length; ++i) {
                array[i] = false;
            }
            array[9] = true;
            return array;
        }
        final int bank = this.getPatch().getBank();
        if (bank >> 7 == 120 || bank >> 7 == 121) {
            final boolean[] array2 = new boolean[16];
            for (int j = 0; j < array2.length; ++j) {
                array2[j] = true;
            }
            return array2;
        }
        final boolean[] array3 = new boolean[16];
        for (int k = 0; k < array3.length; ++k) {
            array3[k] = true;
        }
        array3[9] = false;
        return array3;
    }
}
