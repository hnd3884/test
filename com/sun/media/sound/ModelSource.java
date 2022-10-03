package com.sun.media.sound;

public final class ModelSource
{
    public static final ModelIdentifier SOURCE_NONE;
    public static final ModelIdentifier SOURCE_NOTEON_KEYNUMBER;
    public static final ModelIdentifier SOURCE_NOTEON_VELOCITY;
    public static final ModelIdentifier SOURCE_EG1;
    public static final ModelIdentifier SOURCE_EG2;
    public static final ModelIdentifier SOURCE_LFO1;
    public static final ModelIdentifier SOURCE_LFO2;
    public static final ModelIdentifier SOURCE_MIDI_PITCH;
    public static final ModelIdentifier SOURCE_MIDI_CHANNEL_PRESSURE;
    public static final ModelIdentifier SOURCE_MIDI_POLY_PRESSURE;
    public static final ModelIdentifier SOURCE_MIDI_CC_0;
    public static final ModelIdentifier SOURCE_MIDI_RPN_0;
    private ModelIdentifier source;
    private ModelTransform transform;
    
    public ModelSource() {
        this.source = ModelSource.SOURCE_NONE;
        this.transform = new ModelStandardTransform();
    }
    
    public ModelSource(final ModelIdentifier source) {
        this.source = ModelSource.SOURCE_NONE;
        this.source = source;
        this.transform = new ModelStandardTransform();
    }
    
    public ModelSource(final ModelIdentifier source, final boolean b) {
        this.source = ModelSource.SOURCE_NONE;
        this.source = source;
        this.transform = new ModelStandardTransform(b);
    }
    
    public ModelSource(final ModelIdentifier source, final boolean b, final boolean b2) {
        this.source = ModelSource.SOURCE_NONE;
        this.source = source;
        this.transform = new ModelStandardTransform(b, b2);
    }
    
    public ModelSource(final ModelIdentifier source, final boolean b, final boolean b2, final int n) {
        this.source = ModelSource.SOURCE_NONE;
        this.source = source;
        this.transform = new ModelStandardTransform(b, b2, n);
    }
    
    public ModelSource(final ModelIdentifier source, final ModelTransform transform) {
        this.source = ModelSource.SOURCE_NONE;
        this.source = source;
        this.transform = transform;
    }
    
    public ModelIdentifier getIdentifier() {
        return this.source;
    }
    
    public void setIdentifier(final ModelIdentifier source) {
        this.source = source;
    }
    
    public ModelTransform getTransform() {
        return this.transform;
    }
    
    public void setTransform(final ModelTransform transform) {
        this.transform = transform;
    }
    
    static {
        SOURCE_NONE = null;
        SOURCE_NOTEON_KEYNUMBER = new ModelIdentifier("noteon", "keynumber");
        SOURCE_NOTEON_VELOCITY = new ModelIdentifier("noteon", "velocity");
        SOURCE_EG1 = new ModelIdentifier("eg", null, 0);
        SOURCE_EG2 = new ModelIdentifier("eg", null, 1);
        SOURCE_LFO1 = new ModelIdentifier("lfo", null, 0);
        SOURCE_LFO2 = new ModelIdentifier("lfo", null, 1);
        SOURCE_MIDI_PITCH = new ModelIdentifier("midi", "pitch", 0);
        SOURCE_MIDI_CHANNEL_PRESSURE = new ModelIdentifier("midi", "channel_pressure", 0);
        SOURCE_MIDI_POLY_PRESSURE = new ModelIdentifier("midi", "poly_pressure", 0);
        SOURCE_MIDI_CC_0 = new ModelIdentifier("midi_cc", "0", 0);
        SOURCE_MIDI_RPN_0 = new ModelIdentifier("midi_rpn", "0", 0);
    }
}
