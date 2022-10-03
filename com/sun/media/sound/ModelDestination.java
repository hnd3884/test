package com.sun.media.sound;

public final class ModelDestination
{
    public static final ModelIdentifier DESTINATION_NONE;
    public static final ModelIdentifier DESTINATION_KEYNUMBER;
    public static final ModelIdentifier DESTINATION_VELOCITY;
    public static final ModelIdentifier DESTINATION_PITCH;
    public static final ModelIdentifier DESTINATION_GAIN;
    public static final ModelIdentifier DESTINATION_PAN;
    public static final ModelIdentifier DESTINATION_REVERB;
    public static final ModelIdentifier DESTINATION_CHORUS;
    public static final ModelIdentifier DESTINATION_LFO1_DELAY;
    public static final ModelIdentifier DESTINATION_LFO1_FREQ;
    public static final ModelIdentifier DESTINATION_LFO2_DELAY;
    public static final ModelIdentifier DESTINATION_LFO2_FREQ;
    public static final ModelIdentifier DESTINATION_EG1_DELAY;
    public static final ModelIdentifier DESTINATION_EG1_ATTACK;
    public static final ModelIdentifier DESTINATION_EG1_HOLD;
    public static final ModelIdentifier DESTINATION_EG1_DECAY;
    public static final ModelIdentifier DESTINATION_EG1_SUSTAIN;
    public static final ModelIdentifier DESTINATION_EG1_RELEASE;
    public static final ModelIdentifier DESTINATION_EG1_SHUTDOWN;
    public static final ModelIdentifier DESTINATION_EG2_DELAY;
    public static final ModelIdentifier DESTINATION_EG2_ATTACK;
    public static final ModelIdentifier DESTINATION_EG2_HOLD;
    public static final ModelIdentifier DESTINATION_EG2_DECAY;
    public static final ModelIdentifier DESTINATION_EG2_SUSTAIN;
    public static final ModelIdentifier DESTINATION_EG2_RELEASE;
    public static final ModelIdentifier DESTINATION_EG2_SHUTDOWN;
    public static final ModelIdentifier DESTINATION_FILTER_FREQ;
    public static final ModelIdentifier DESTINATION_FILTER_Q;
    private ModelIdentifier destination;
    private ModelTransform transform;
    
    public ModelDestination() {
        this.destination = ModelDestination.DESTINATION_NONE;
        this.transform = new ModelStandardTransform();
    }
    
    public ModelDestination(final ModelIdentifier destination) {
        this.destination = ModelDestination.DESTINATION_NONE;
        this.transform = new ModelStandardTransform();
        this.destination = destination;
    }
    
    public ModelIdentifier getIdentifier() {
        return this.destination;
    }
    
    public void setIdentifier(final ModelIdentifier destination) {
        this.destination = destination;
    }
    
    public ModelTransform getTransform() {
        return this.transform;
    }
    
    public void setTransform(final ModelTransform transform) {
        this.transform = transform;
    }
    
    static {
        DESTINATION_NONE = null;
        DESTINATION_KEYNUMBER = new ModelIdentifier("noteon", "keynumber");
        DESTINATION_VELOCITY = new ModelIdentifier("noteon", "velocity");
        DESTINATION_PITCH = new ModelIdentifier("osc", "pitch");
        DESTINATION_GAIN = new ModelIdentifier("mixer", "gain");
        DESTINATION_PAN = new ModelIdentifier("mixer", "pan");
        DESTINATION_REVERB = new ModelIdentifier("mixer", "reverb");
        DESTINATION_CHORUS = new ModelIdentifier("mixer", "chorus");
        DESTINATION_LFO1_DELAY = new ModelIdentifier("lfo", "delay", 0);
        DESTINATION_LFO1_FREQ = new ModelIdentifier("lfo", "freq", 0);
        DESTINATION_LFO2_DELAY = new ModelIdentifier("lfo", "delay", 1);
        DESTINATION_LFO2_FREQ = new ModelIdentifier("lfo", "freq", 1);
        DESTINATION_EG1_DELAY = new ModelIdentifier("eg", "delay", 0);
        DESTINATION_EG1_ATTACK = new ModelIdentifier("eg", "attack", 0);
        DESTINATION_EG1_HOLD = new ModelIdentifier("eg", "hold", 0);
        DESTINATION_EG1_DECAY = new ModelIdentifier("eg", "decay", 0);
        DESTINATION_EG1_SUSTAIN = new ModelIdentifier("eg", "sustain", 0);
        DESTINATION_EG1_RELEASE = new ModelIdentifier("eg", "release", 0);
        DESTINATION_EG1_SHUTDOWN = new ModelIdentifier("eg", "shutdown", 0);
        DESTINATION_EG2_DELAY = new ModelIdentifier("eg", "delay", 1);
        DESTINATION_EG2_ATTACK = new ModelIdentifier("eg", "attack", 1);
        DESTINATION_EG2_HOLD = new ModelIdentifier("eg", "hold", 1);
        DESTINATION_EG2_DECAY = new ModelIdentifier("eg", "decay", 1);
        DESTINATION_EG2_SUSTAIN = new ModelIdentifier("eg", "sustain", 1);
        DESTINATION_EG2_RELEASE = new ModelIdentifier("eg", "release", 1);
        DESTINATION_EG2_SHUTDOWN = new ModelIdentifier("eg", "shutdown", 1);
        DESTINATION_FILTER_FREQ = new ModelIdentifier("filter", "freq", 0);
        DESTINATION_FILTER_Q = new ModelIdentifier("filter", "q", 0);
    }
}
