package com.sun.media.sound;

import java.util.Iterator;
import javax.sound.midi.Patch;
import java.util.Comparator;
import java.util.Arrays;
import java.util.ArrayList;
import javax.sound.midi.Instrument;
import javax.sound.midi.SoundbankResource;
import java.util.List;
import javax.sound.midi.Soundbank;

public class SimpleSoundbank implements Soundbank
{
    String name;
    String version;
    String vendor;
    String description;
    List<SoundbankResource> resources;
    List<Instrument> instruments;
    
    public SimpleSoundbank() {
        this.name = "";
        this.version = "";
        this.vendor = "";
        this.description = "";
        this.resources = new ArrayList<SoundbankResource>();
        this.instruments = new ArrayList<Instrument>();
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getVersion() {
        return this.version;
    }
    
    @Override
    public String getVendor() {
        return this.vendor;
    }
    
    @Override
    public String getDescription() {
        return this.description;
    }
    
    public void setDescription(final String description) {
        this.description = description;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setVendor(final String vendor) {
        this.vendor = vendor;
    }
    
    public void setVersion(final String version) {
        this.version = version;
    }
    
    @Override
    public SoundbankResource[] getResources() {
        return this.resources.toArray(new SoundbankResource[this.resources.size()]);
    }
    
    @Override
    public Instrument[] getInstruments() {
        final Instrument[] array = this.instruments.toArray(new Instrument[this.resources.size()]);
        Arrays.sort(array, new ModelInstrumentComparator());
        return array;
    }
    
    @Override
    public Instrument getInstrument(final Patch patch) {
        final int program = patch.getProgram();
        final int bank = patch.getBank();
        int percussion = 0;
        if (patch instanceof ModelPatch) {
            percussion = (((ModelPatch)patch).isPercussion() ? 1 : 0);
        }
        for (final Instrument instrument : this.instruments) {
            final Patch patch2 = instrument.getPatch();
            final int program2 = patch2.getProgram();
            final int bank2 = patch2.getBank();
            if (program == program2 && bank == bank2) {
                int percussion2 = 0;
                if (patch2 instanceof ModelPatch) {
                    percussion2 = (((ModelPatch)patch2).isPercussion() ? 1 : 0);
                }
                if (percussion == percussion2) {
                    return instrument;
                }
                continue;
            }
        }
        return null;
    }
    
    public void addResource(final SoundbankResource soundbankResource) {
        if (soundbankResource instanceof Instrument) {
            this.instruments.add((Instrument)soundbankResource);
        }
        else {
            this.resources.add(soundbankResource);
        }
    }
    
    public void removeResource(final SoundbankResource soundbankResource) {
        if (soundbankResource instanceof Instrument) {
            this.instruments.remove(soundbankResource);
        }
        else {
            this.resources.remove(soundbankResource);
        }
    }
    
    public void addInstrument(final Instrument instrument) {
        this.instruments.add(instrument);
    }
    
    public void removeInstrument(final Instrument instrument) {
        this.instruments.remove(instrument);
    }
    
    public void addAllInstruments(final Soundbank soundbank) {
        final Instrument[] instruments = soundbank.getInstruments();
        for (int length = instruments.length, i = 0; i < length; ++i) {
            this.addInstrument(instruments[i]);
        }
    }
    
    public void removeAllInstruments(final Soundbank soundbank) {
        final Instrument[] instruments = soundbank.getInstruments();
        for (int length = instruments.length, i = 0; i < length; ++i) {
            this.removeInstrument(instruments[i]);
        }
    }
}
