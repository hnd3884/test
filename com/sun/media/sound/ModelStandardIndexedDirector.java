package com.sun.media.sound;

import java.util.Arrays;

public final class ModelStandardIndexedDirector implements ModelDirector
{
    private final ModelPerformer[] performers;
    private final ModelDirectedPlayer player;
    private boolean noteOnUsed;
    private boolean noteOffUsed;
    private byte[][] trantables;
    private int[] counters;
    private int[][] mat;
    
    public ModelStandardIndexedDirector(final ModelPerformer[] array, final ModelDirectedPlayer player) {
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
        this.buildindex();
    }
    
    private int[] lookupIndex(final int n, final int n2) {
        if (n >= 0 && n < 128 && n2 >= 0 && n2 < 128) {
            final byte b = this.trantables[0][n];
            final byte b2 = this.trantables[1][n2];
            if (b != -1 && b2 != -1) {
                return this.mat[b + b2 * this.counters[0]];
            }
        }
        return null;
    }
    
    private int restrict(final int n) {
        if (n < 0) {
            return 0;
        }
        if (n > 127) {
            return 127;
        }
        return n;
    }
    
    private void buildindex() {
        this.trantables = new byte[2][129];
        this.counters = new int[this.trantables.length];
        for (final ModelPerformer modelPerformer : this.performers) {
            final int key = modelPerformer.getKeyFrom();
            final int keyTo = modelPerformer.getKeyTo();
            final int vel = modelPerformer.getVelFrom();
            final int velTo = modelPerformer.getVelTo();
            if (key <= keyTo) {
                if (vel <= velTo) {
                    final int restrict = this.restrict(key);
                    final int restrict2 = this.restrict(keyTo);
                    final int restrict3 = this.restrict(vel);
                    final int restrict4 = this.restrict(velTo);
                    this.trantables[0][restrict] = 1;
                    this.trantables[0][restrict2 + 1] = 1;
                    this.trantables[1][restrict3] = 1;
                    this.trantables[1][restrict4 + 1] = 1;
                }
            }
        }
        for (int j = 0; j < this.trantables.length; ++j) {
            final byte[] array = this.trantables[j];
            final int length2 = array.length;
            for (int k = length2 - 1; k >= 0; --k) {
                if (array[k] == 1) {
                    array[k] = -1;
                    break;
                }
                array[k] = -1;
            }
            int n = -1;
            for (int l = 0; l < length2; ++l) {
                if (array[l] != 0) {
                    ++n;
                    if (array[l] == -1) {
                        break;
                    }
                }
                array[l] = (byte)n;
            }
            this.counters[j] = n;
        }
        this.mat = new int[this.counters[0] * this.counters[1]][];
        int n2 = 0;
        for (final ModelPerformer modelPerformer2 : this.performers) {
            final int key2 = modelPerformer2.getKeyFrom();
            final int keyTo2 = modelPerformer2.getKeyTo();
            final int vel2 = modelPerformer2.getVelFrom();
            final int velTo2 = modelPerformer2.getVelTo();
            if (key2 <= keyTo2) {
                if (vel2 <= velTo2) {
                    final int restrict5 = this.restrict(key2);
                    final int restrict6 = this.restrict(keyTo2);
                    final int restrict7 = this.restrict(vel2);
                    final int restrict8 = this.restrict(velTo2);
                    final byte b = this.trantables[0][restrict5];
                    int n4 = this.trantables[0][restrict6 + 1];
                    final byte b2 = this.trantables[1][restrict7];
                    int n5 = this.trantables[1][restrict8 + 1];
                    if (n4 == -1) {
                        n4 = this.counters[0];
                    }
                    if (n5 == -1) {
                        n5 = this.counters[1];
                    }
                    for (int n6 = b2; n6 < n5; ++n6) {
                        int n7 = b + n6 * this.counters[0];
                        for (int n8 = b; n8 < n4; ++n8) {
                            final int[] array2 = this.mat[n7];
                            if (array2 == null) {
                                this.mat[n7] = new int[] { n2 };
                            }
                            else {
                                final int[] array3 = new int[array2.length + 1];
                                array3[array3.length - 1] = n2;
                                for (int n9 = 0; n9 < array2.length; ++n9) {
                                    array3[n9] = array2[n9];
                                }
                                this.mat[n7] = array3;
                            }
                            ++n7;
                        }
                    }
                    ++n2;
                }
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
        final int[] lookupIndex = this.lookupIndex(n, n2);
        if (lookupIndex == null) {
            return;
        }
        for (final int n3 : lookupIndex) {
            if (this.performers[n3].isReleaseTriggered()) {
                this.player.play(n3, null);
            }
        }
    }
    
    @Override
    public void noteOn(final int n, final int n2) {
        if (!this.noteOnUsed) {
            return;
        }
        final int[] lookupIndex = this.lookupIndex(n, n2);
        if (lookupIndex == null) {
            return;
        }
        for (final int n3 : lookupIndex) {
            if (!this.performers[n3].isReleaseTriggered()) {
                this.player.play(n3, null);
            }
        }
    }
}
