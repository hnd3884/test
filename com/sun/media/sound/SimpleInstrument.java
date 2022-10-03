package com.sun.media.sound;

import java.util.Iterator;
import java.util.Collection;
import java.util.ArrayList;
import javax.sound.midi.Patch;
import javax.sound.midi.Soundbank;
import java.util.List;

public class SimpleInstrument extends ModelInstrument
{
    protected int preset;
    protected int bank;
    protected boolean percussion;
    protected String name;
    protected List<SimpleInstrumentPart> parts;
    
    public SimpleInstrument() {
        super(null, null, null, null);
        this.preset = 0;
        this.bank = 0;
        this.percussion = false;
        this.name = "";
        this.parts = new ArrayList<SimpleInstrumentPart>();
    }
    
    public void clear() {
        this.parts.clear();
    }
    
    public void add(final ModelPerformer[] performers, final int keyFrom, final int keyTo, final int velFrom, final int velTo, final int exclusiveClass) {
        final SimpleInstrumentPart simpleInstrumentPart = new SimpleInstrumentPart();
        simpleInstrumentPart.performers = performers;
        simpleInstrumentPart.keyFrom = keyFrom;
        simpleInstrumentPart.keyTo = keyTo;
        simpleInstrumentPart.velFrom = velFrom;
        simpleInstrumentPart.velTo = velTo;
        simpleInstrumentPart.exclusiveClass = exclusiveClass;
        this.parts.add(simpleInstrumentPart);
    }
    
    public void add(final ModelPerformer[] array, final int n, final int n2, final int n3, final int n4) {
        this.add(array, n, n2, n3, n4, -1);
    }
    
    public void add(final ModelPerformer[] array, final int n, final int n2) {
        this.add(array, n, n2, 0, 127, -1);
    }
    
    public void add(final ModelPerformer[] array) {
        this.add(array, 0, 127, 0, 127, -1);
    }
    
    public void add(final ModelPerformer modelPerformer, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.add(new ModelPerformer[] { modelPerformer }, n, n2, n3, n4, n5);
    }
    
    public void add(final ModelPerformer modelPerformer, final int n, final int n2, final int n3, final int n4) {
        this.add(new ModelPerformer[] { modelPerformer }, n, n2, n3, n4);
    }
    
    public void add(final ModelPerformer modelPerformer, final int n, final int n2) {
        this.add(new ModelPerformer[] { modelPerformer }, n, n2);
    }
    
    public void add(final ModelPerformer modelPerformer) {
        this.add(new ModelPerformer[] { modelPerformer });
    }
    
    public void add(final ModelInstrument modelInstrument, final int n, final int n2, final int n3, final int n4, final int n5) {
        this.add(modelInstrument.getPerformers(), n, n2, n3, n4, n5);
    }
    
    public void add(final ModelInstrument modelInstrument, final int n, final int n2, final int n3, final int n4) {
        this.add(modelInstrument.getPerformers(), n, n2, n3, n4);
    }
    
    public void add(final ModelInstrument modelInstrument, final int n, final int n2) {
        this.add(modelInstrument.getPerformers(), n, n2);
    }
    
    public void add(final ModelInstrument modelInstrument) {
        this.add(modelInstrument.getPerformers());
    }
    
    @Override
    public ModelPerformer[] getPerformers() {
        int n = 0;
        for (final SimpleInstrumentPart simpleInstrumentPart : this.parts) {
            if (simpleInstrumentPart.performers != null) {
                n += simpleInstrumentPart.performers.length;
            }
        }
        final ModelPerformer[] array = new ModelPerformer[n];
        int n2 = 0;
        for (final SimpleInstrumentPart simpleInstrumentPart2 : this.parts) {
            if (simpleInstrumentPart2.performers != null) {
                for (final ModelPerformer modelPerformer : simpleInstrumentPart2.performers) {
                    final ModelPerformer modelPerformer2 = new ModelPerformer();
                    modelPerformer2.setName(this.getName());
                    (array[n2++] = modelPerformer2).setDefaultConnectionsEnabled(modelPerformer.isDefaultConnectionsEnabled());
                    modelPerformer2.setKeyFrom(modelPerformer.getKeyFrom());
                    modelPerformer2.setKeyTo(modelPerformer.getKeyTo());
                    modelPerformer2.setVelFrom(modelPerformer.getVelFrom());
                    modelPerformer2.setVelTo(modelPerformer.getVelTo());
                    modelPerformer2.setExclusiveClass(modelPerformer.getExclusiveClass());
                    modelPerformer2.setSelfNonExclusive(modelPerformer.isSelfNonExclusive());
                    modelPerformer2.setReleaseTriggered(modelPerformer.isReleaseTriggered());
                    if (simpleInstrumentPart2.exclusiveClass != -1) {
                        modelPerformer2.setExclusiveClass(simpleInstrumentPart2.exclusiveClass);
                    }
                    if (simpleInstrumentPart2.keyFrom > modelPerformer2.getKeyFrom()) {
                        modelPerformer2.setKeyFrom(simpleInstrumentPart2.keyFrom);
                    }
                    if (simpleInstrumentPart2.keyTo < modelPerformer2.getKeyTo()) {
                        modelPerformer2.setKeyTo(simpleInstrumentPart2.keyTo);
                    }
                    if (simpleInstrumentPart2.velFrom > modelPerformer2.getVelFrom()) {
                        modelPerformer2.setVelFrom(simpleInstrumentPart2.velFrom);
                    }
                    if (simpleInstrumentPart2.velTo < modelPerformer2.getVelTo()) {
                        modelPerformer2.setVelTo(simpleInstrumentPart2.velTo);
                    }
                    modelPerformer2.getOscillators().addAll(modelPerformer.getOscillators());
                    modelPerformer2.getConnectionBlocks().addAll(modelPerformer.getConnectionBlocks());
                }
            }
        }
        return array;
    }
    
    @Override
    public Object getData() {
        return null;
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    @Override
    public ModelPatch getPatch() {
        return new ModelPatch(this.bank, this.preset, this.percussion);
    }
    
    public void setPatch(final Patch patch) {
        if (patch instanceof ModelPatch && ((ModelPatch)patch).isPercussion()) {
            this.percussion = true;
            this.bank = patch.getBank();
            this.preset = patch.getProgram();
        }
        else {
            this.percussion = false;
            this.bank = patch.getBank();
            this.preset = patch.getProgram();
        }
    }
    
    private static class SimpleInstrumentPart
    {
        ModelPerformer[] performers;
        int keyFrom;
        int keyTo;
        int velFrom;
        int velTo;
        int exclusiveClass;
    }
}
