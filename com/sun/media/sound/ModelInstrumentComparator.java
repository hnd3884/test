package com.sun.media.sound;

import javax.sound.midi.Patch;
import javax.sound.midi.Instrument;
import java.util.Comparator;

public final class ModelInstrumentComparator implements Comparator<Instrument>
{
    @Override
    public int compare(final Instrument instrument, final Instrument instrument2) {
        final Patch patch = instrument.getPatch();
        final Patch patch2 = instrument2.getPatch();
        int n = patch.getBank() * 128 + patch.getProgram();
        int n2 = patch2.getBank() * 128 + patch2.getProgram();
        if (patch instanceof ModelPatch) {
            n += (((ModelPatch)patch).isPercussion() ? 2097152 : 0);
        }
        if (patch2 instanceof ModelPatch) {
            n2 += (((ModelPatch)patch2).isPercussion() ? 2097152 : 0);
        }
        return n - n2;
    }
}
