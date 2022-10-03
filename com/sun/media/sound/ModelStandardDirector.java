package com.sun.media.sound;

import java.util.Arrays;

public final class ModelStandardDirector implements ModelDirector
{
    private final ModelPerformer[] performers;
    private final ModelDirectedPlayer player;
    private boolean noteOnUsed;
    private boolean noteOffUsed;
    
    public ModelStandardDirector(final ModelPerformer[] array, final ModelDirectedPlayer player) {
        this.noteOnUsed = false;
        this.noteOffUsed = false;
        this.performers = Arrays.copyOf(array, array.length);
        this.player = player;
        final ModelPerformer[] performers = this.performers;
        for (int length = performers.length, i = 0; i < length; ++i) {
            if (performers[i].isReleaseTriggered()) {
                this.noteOffUsed = true;
            }
            else {
                this.noteOnUsed = true;
            }
        }
    }
    
    @Override
    public void close() {
    }
    
    @Override
    public void noteOff(final int n, final int n2) {
        if (!this.noteOffUsed) {
            return;
        }
        for (int i = 0; i < this.performers.length; ++i) {
            final ModelPerformer modelPerformer = this.performers[i];
            if (modelPerformer.getKeyFrom() <= n && modelPerformer.getKeyTo() >= n && modelPerformer.getVelFrom() <= n2 && modelPerformer.getVelTo() >= n2 && modelPerformer.isReleaseTriggered()) {
                this.player.play(i, null);
            }
        }
    }
    
    @Override
    public void noteOn(final int n, final int n2) {
        if (!this.noteOnUsed) {
            return;
        }
        for (int i = 0; i < this.performers.length; ++i) {
            final ModelPerformer modelPerformer = this.performers[i];
            if (modelPerformer.getKeyFrom() <= n && modelPerformer.getKeyTo() >= n && modelPerformer.getVelFrom() <= n2 && modelPerformer.getVelTo() >= n2 && !modelPerformer.isReleaseTriggered()) {
                this.player.play(i, null);
            }
        }
    }
}
