package com.sun.media.sound;

import javax.sound.midi.SoundbankResource;
import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import java.io.IOException;
import javax.sound.midi.VoiceStatus;
import javax.sound.midi.MidiChannel;
import javax.sound.midi.Soundbank;

public abstract class ModelAbstractOscillator implements ModelOscillator, ModelOscillatorStream, Soundbank
{
    protected float pitch;
    protected float samplerate;
    protected MidiChannel channel;
    protected VoiceStatus voice;
    protected int noteNumber;
    protected int velocity;
    protected boolean on;
    
    public ModelAbstractOscillator() {
        this.pitch = 6000.0f;
        this.on = false;
    }
    
    public void init() {
    }
    
    @Override
    public void close() throws IOException {
    }
    
    @Override
    public void noteOff(final int n) {
        this.on = false;
    }
    
    @Override
    public void noteOn(final MidiChannel channel, final VoiceStatus voice, final int noteNumber, final int velocity) {
        this.channel = channel;
        this.voice = voice;
        this.noteNumber = noteNumber;
        this.velocity = velocity;
        this.on = true;
    }
    
    @Override
    public int read(final float[][] array, final int n, final int n2) throws IOException {
        return -1;
    }
    
    public MidiChannel getChannel() {
        return this.channel;
    }
    
    public VoiceStatus getVoice() {
        return this.voice;
    }
    
    public int getNoteNumber() {
        return this.noteNumber;
    }
    
    public int getVelocity() {
        return this.velocity;
    }
    
    public boolean isOn() {
        return this.on;
    }
    
    @Override
    public void setPitch(final float pitch) {
        this.pitch = pitch;
    }
    
    public float getPitch() {
        return this.pitch;
    }
    
    public void setSampleRate(final float samplerate) {
        this.samplerate = samplerate;
    }
    
    public float getSampleRate() {
        return this.samplerate;
    }
    
    @Override
    public float getAttenuation() {
        return 0.0f;
    }
    
    @Override
    public int getChannels() {
        return 1;
    }
    
    @Override
    public String getName() {
        return this.getClass().getName();
    }
    
    public Patch getPatch() {
        return new Patch(0, 0);
    }
    
    @Override
    public ModelOscillatorStream open(final float sampleRate) {
        ModelAbstractOscillator modelAbstractOscillator;
        try {
            modelAbstractOscillator = (ModelAbstractOscillator)this.getClass().newInstance();
        }
        catch (final InstantiationException ex) {
            throw new IllegalArgumentException(ex);
        }
        catch (final IllegalAccessException ex2) {
            throw new IllegalArgumentException(ex2);
        }
        modelAbstractOscillator.setSampleRate(sampleRate);
        modelAbstractOscillator.init();
        return modelAbstractOscillator;
    }
    
    public ModelPerformer getPerformer() {
        final ModelPerformer modelPerformer = new ModelPerformer();
        modelPerformer.getOscillators().add(this);
        return modelPerformer;
    }
    
    public ModelInstrument getInstrument() {
        final SimpleInstrument simpleInstrument = new SimpleInstrument();
        simpleInstrument.setName(this.getName());
        simpleInstrument.add(this.getPerformer());
        simpleInstrument.setPatch(this.getPatch());
        return simpleInstrument;
    }
    
    public Soundbank getSoundBank() {
        final SimpleSoundbank simpleSoundbank = new SimpleSoundbank();
        simpleSoundbank.addInstrument(this.getInstrument());
        return simpleSoundbank;
    }
    
    @Override
    public String getDescription() {
        return this.getName();
    }
    
    @Override
    public Instrument getInstrument(final Patch patch) {
        final ModelInstrument instrument = this.getInstrument();
        final Patch patch2 = instrument.getPatch();
        if (patch2.getBank() != patch.getBank()) {
            return null;
        }
        if (patch2.getProgram() != patch.getProgram()) {
            return null;
        }
        if (patch2 instanceof ModelPatch && patch instanceof ModelPatch && ((ModelPatch)patch2).isPercussion() != ((ModelPatch)patch).isPercussion()) {
            return null;
        }
        return instrument;
    }
    
    @Override
    public Instrument[] getInstruments() {
        return new Instrument[] { this.getInstrument() };
    }
    
    @Override
    public SoundbankResource[] getResources() {
        return new SoundbankResource[0];
    }
    
    @Override
    public String getVendor() {
        return null;
    }
    
    @Override
    public String getVersion() {
        return null;
    }
}
