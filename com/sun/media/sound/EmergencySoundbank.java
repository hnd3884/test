package com.sun.media.sound;

import javax.sound.midi.Soundbank;
import javax.sound.sampled.AudioFormat;
import javax.sound.midi.SoundbankResource;
import java.util.Random;
import javax.sound.midi.Patch;

public final class EmergencySoundbank
{
    private static final String[] general_midi_instruments;
    
    public static SF2Soundbank createSoundbank() throws Exception {
        final SF2Soundbank sf2Soundbank = new SF2Soundbank();
        sf2Soundbank.setName("Emergency GM sound set");
        sf2Soundbank.setVendor("Generated");
        sf2Soundbank.setDescription("Emergency generated soundbank");
        final SF2Layer new_bass_drum = new_bass_drum(sf2Soundbank);
        final SF2Layer new_snare_drum = new_snare_drum(sf2Soundbank);
        final SF2Layer new_tom = new_tom(sf2Soundbank);
        final SF2Layer new_open_hihat = new_open_hihat(sf2Soundbank);
        final SF2Layer new_closed_hihat = new_closed_hihat(sf2Soundbank);
        final SF2Layer new_crash_cymbal = new_crash_cymbal(sf2Soundbank);
        final SF2Layer new_side_stick = new_side_stick(sf2Soundbank);
        final SF2Layer[] array = new SF2Layer[128];
        array[36] = (array[35] = new_bass_drum);
        array[40] = (array[38] = new_snare_drum);
        array[43] = (array[41] = new_tom);
        array[47] = (array[45] = new_tom);
        array[50] = (array[48] = new_tom);
        array[44] = (array[42] = new_closed_hihat);
        array[46] = new_open_hihat;
        array[51] = (array[49] = new_crash_cymbal);
        array[55] = (array[52] = new_crash_cymbal);
        array[59] = (array[57] = new_crash_cymbal);
        array[39] = (array[37] = new_side_stick);
        array[54] = (array[53] = new_side_stick);
        array[58] = (array[56] = new_side_stick);
        array[70] = (array[69] = new_side_stick);
        array[60] = (array[75] = new_side_stick);
        array[62] = (array[61] = new_side_stick);
        array[64] = (array[63] = new_side_stick);
        array[66] = (array[65] = new_side_stick);
        array[68] = (array[67] = new_side_stick);
        array[72] = (array[71] = new_side_stick);
        array[74] = (array[73] = new_side_stick);
        array[77] = (array[76] = new_side_stick);
        array[79] = (array[78] = new_side_stick);
        array[81] = (array[80] = new_side_stick);
        final SF2Instrument sf2Instrument = new SF2Instrument(sf2Soundbank);
        sf2Instrument.setName("Standard Kit");
        sf2Instrument.setPatch(new ModelPatch(0, 0, true));
        sf2Soundbank.addInstrument(sf2Instrument);
        for (int i = 0; i < array.length; ++i) {
            if (array[i] != null) {
                final SF2InstrumentRegion sf2InstrumentRegion = new SF2InstrumentRegion();
                sf2InstrumentRegion.setLayer(array[i]);
                sf2InstrumentRegion.putBytes(43, new byte[] { (byte)i, (byte)i });
                sf2Instrument.getRegions().add(sf2InstrumentRegion);
            }
        }
        final SF2Layer new_gpiano = new_gpiano(sf2Soundbank);
        final SF2Layer new_gpiano2 = new_gpiano2(sf2Soundbank);
        final SF2Layer new_piano_hammer = new_piano_hammer(sf2Soundbank);
        final SF2Layer new_piano1 = new_piano1(sf2Soundbank);
        final SF2Layer new_epiano1 = new_epiano1(sf2Soundbank);
        final SF2Layer new_epiano2 = new_epiano2(sf2Soundbank);
        final SF2Layer new_guitar1 = new_guitar1(sf2Soundbank);
        final SF2Layer new_guitar_pick = new_guitar_pick(sf2Soundbank);
        final SF2Layer new_guitar_dist = new_guitar_dist(sf2Soundbank);
        final SF2Layer new_bass1 = new_bass1(sf2Soundbank);
        final SF2Layer new_bass2 = new_bass2(sf2Soundbank);
        final SF2Layer new_synthbass = new_synthbass(sf2Soundbank);
        final SF2Layer new_string2 = new_string2(sf2Soundbank);
        final SF2Layer new_orchhit = new_orchhit(sf2Soundbank);
        final SF2Layer new_choir = new_choir(sf2Soundbank);
        final SF2Layer new_solostring = new_solostring(sf2Soundbank);
        final SF2Layer new_organ = new_organ(sf2Soundbank);
        final SF2Layer new_ch_organ = new_ch_organ(sf2Soundbank);
        final SF2Layer new_bell = new_bell(sf2Soundbank);
        final SF2Layer new_flute = new_flute(sf2Soundbank);
        final SF2Layer new_timpani = new_timpani(sf2Soundbank);
        final SF2Layer new_melodic_toms = new_melodic_toms(sf2Soundbank);
        final SF2Layer new_trumpet = new_trumpet(sf2Soundbank);
        final SF2Layer new_trombone = new_trombone(sf2Soundbank);
        final SF2Layer new_brass_section = new_brass_section(sf2Soundbank);
        final SF2Layer new_horn = new_horn(sf2Soundbank);
        final SF2Layer new_sax = new_sax(sf2Soundbank);
        final SF2Layer new_oboe = new_oboe(sf2Soundbank);
        final SF2Layer new_bassoon = new_bassoon(sf2Soundbank);
        final SF2Layer new_clarinet = new_clarinet(sf2Soundbank);
        final SF2Layer new_reverse_cymbal = new_reverse_cymbal(sf2Soundbank);
        final SF2Layer sf2Layer = new_piano1;
        newInstrument(sf2Soundbank, "Piano", new Patch(0, 0), new_gpiano, new_piano_hammer);
        newInstrument(sf2Soundbank, "Piano", new Patch(0, 1), new_gpiano2, new_piano_hammer);
        newInstrument(sf2Soundbank, "Piano", new Patch(0, 2), new_piano1);
        final SF2Instrument instrument = newInstrument(sf2Soundbank, "Honky-tonk Piano", new Patch(0, 3), new_piano1, new_piano1);
        final SF2InstrumentRegion sf2InstrumentRegion2 = instrument.getRegions().get(0);
        sf2InstrumentRegion2.putInteger(8, 80);
        sf2InstrumentRegion2.putInteger(52, 30);
        instrument.getRegions().get(1).putInteger(8, 30);
        newInstrument(sf2Soundbank, "Rhodes", new Patch(0, 4), new_epiano2);
        newInstrument(sf2Soundbank, "Rhodes", new Patch(0, 5), new_epiano2);
        newInstrument(sf2Soundbank, "Clavinet", new Patch(0, 6), new_epiano1);
        newInstrument(sf2Soundbank, "Clavinet", new Patch(0, 7), new_epiano1);
        newInstrument(sf2Soundbank, "Rhodes", new Patch(0, 8), new_epiano2);
        newInstrument(sf2Soundbank, "Bell", new Patch(0, 9), new_bell);
        newInstrument(sf2Soundbank, "Bell", new Patch(0, 10), new_bell);
        newInstrument(sf2Soundbank, "Vibraphone", new Patch(0, 11), new_bell);
        newInstrument(sf2Soundbank, "Marimba", new Patch(0, 12), new_bell);
        newInstrument(sf2Soundbank, "Marimba", new Patch(0, 13), new_bell);
        newInstrument(sf2Soundbank, "Bell", new Patch(0, 14), new_bell);
        newInstrument(sf2Soundbank, "Rock Organ", new Patch(0, 15), new_organ);
        newInstrument(sf2Soundbank, "Rock Organ", new Patch(0, 16), new_organ);
        newInstrument(sf2Soundbank, "Perc Organ", new Patch(0, 17), new_organ);
        newInstrument(sf2Soundbank, "Rock Organ", new Patch(0, 18), new_organ);
        newInstrument(sf2Soundbank, "Church Organ", new Patch(0, 19), new_ch_organ);
        newInstrument(sf2Soundbank, "Accordion", new Patch(0, 20), new_organ);
        newInstrument(sf2Soundbank, "Accordion", new Patch(0, 21), new_organ);
        newInstrument(sf2Soundbank, "Accordion", new Patch(0, 22), new_organ);
        newInstrument(sf2Soundbank, "Accordion", new Patch(0, 23), new_organ);
        newInstrument(sf2Soundbank, "Guitar", new Patch(0, 24), new_guitar1, new_guitar_pick);
        newInstrument(sf2Soundbank, "Guitar", new Patch(0, 25), new_guitar1, new_guitar_pick);
        newInstrument(sf2Soundbank, "Guitar", new Patch(0, 26), new_guitar1, new_guitar_pick);
        newInstrument(sf2Soundbank, "Guitar", new Patch(0, 27), new_guitar1, new_guitar_pick);
        newInstrument(sf2Soundbank, "Guitar", new Patch(0, 28), new_guitar1, new_guitar_pick);
        newInstrument(sf2Soundbank, "Distorted Guitar", new Patch(0, 29), new_guitar_dist);
        newInstrument(sf2Soundbank, "Distorted Guitar", new Patch(0, 30), new_guitar_dist);
        newInstrument(sf2Soundbank, "Guitar", new Patch(0, 31), new_guitar1, new_guitar_pick);
        newInstrument(sf2Soundbank, "Finger Bass", new Patch(0, 32), new_bass1);
        newInstrument(sf2Soundbank, "Finger Bass", new Patch(0, 33), new_bass1);
        newInstrument(sf2Soundbank, "Finger Bass", new Patch(0, 34), new_bass1);
        newInstrument(sf2Soundbank, "Frettless Bass", new Patch(0, 35), new_bass2);
        newInstrument(sf2Soundbank, "Frettless Bass", new Patch(0, 36), new_bass2);
        newInstrument(sf2Soundbank, "Frettless Bass", new Patch(0, 37), new_bass2);
        newInstrument(sf2Soundbank, "Synth Bass1", new Patch(0, 38), new_synthbass);
        newInstrument(sf2Soundbank, "Synth Bass2", new Patch(0, 39), new_synthbass);
        newInstrument(sf2Soundbank, "Solo String", new Patch(0, 40), new_string2, new_solostring);
        newInstrument(sf2Soundbank, "Solo String", new Patch(0, 41), new_string2, new_solostring);
        newInstrument(sf2Soundbank, "Solo String", new Patch(0, 42), new_string2, new_solostring);
        newInstrument(sf2Soundbank, "Solo String", new Patch(0, 43), new_string2, new_solostring);
        newInstrument(sf2Soundbank, "Solo String", new Patch(0, 44), new_string2, new_solostring);
        newInstrument(sf2Soundbank, "Def", new Patch(0, 45), sf2Layer);
        newInstrument(sf2Soundbank, "Harp", new Patch(0, 46), new_bell);
        newInstrument(sf2Soundbank, "Timpani", new Patch(0, 47), new_timpani);
        newInstrument(sf2Soundbank, "Strings", new Patch(0, 48), new_string2);
        final SF2InstrumentRegion sf2InstrumentRegion3 = newInstrument(sf2Soundbank, "Slow Strings", new Patch(0, 49), new_string2).getRegions().get(0);
        sf2InstrumentRegion3.putInteger(34, 2500);
        sf2InstrumentRegion3.putInteger(38, 2000);
        newInstrument(sf2Soundbank, "Synth Strings", new Patch(0, 50), new_string2);
        newInstrument(sf2Soundbank, "Synth Strings", new Patch(0, 51), new_string2);
        newInstrument(sf2Soundbank, "Choir", new Patch(0, 52), new_choir);
        newInstrument(sf2Soundbank, "Choir", new Patch(0, 53), new_choir);
        newInstrument(sf2Soundbank, "Choir", new Patch(0, 54), new_choir);
        final SF2InstrumentRegion sf2InstrumentRegion4 = newInstrument(sf2Soundbank, "Orch Hit", new Patch(0, 55), new_orchhit, new_orchhit, new_timpani).getRegions().get(0);
        sf2InstrumentRegion4.putInteger(51, -12);
        sf2InstrumentRegion4.putInteger(48, -100);
        newInstrument(sf2Soundbank, "Trumpet", new Patch(0, 56), new_trumpet);
        newInstrument(sf2Soundbank, "Trombone", new Patch(0, 57), new_trombone);
        newInstrument(sf2Soundbank, "Trombone", new Patch(0, 58), new_trombone);
        newInstrument(sf2Soundbank, "Trumpet", new Patch(0, 59), new_trumpet);
        newInstrument(sf2Soundbank, "Horn", new Patch(0, 60), new_horn);
        newInstrument(sf2Soundbank, "Brass Section", new Patch(0, 61), new_brass_section);
        newInstrument(sf2Soundbank, "Brass Section", new Patch(0, 62), new_brass_section);
        newInstrument(sf2Soundbank, "Brass Section", new Patch(0, 63), new_brass_section);
        newInstrument(sf2Soundbank, "Sax", new Patch(0, 64), new_sax);
        newInstrument(sf2Soundbank, "Sax", new Patch(0, 65), new_sax);
        newInstrument(sf2Soundbank, "Sax", new Patch(0, 66), new_sax);
        newInstrument(sf2Soundbank, "Sax", new Patch(0, 67), new_sax);
        newInstrument(sf2Soundbank, "Oboe", new Patch(0, 68), new_oboe);
        newInstrument(sf2Soundbank, "Horn", new Patch(0, 69), new_horn);
        newInstrument(sf2Soundbank, "Bassoon", new Patch(0, 70), new_bassoon);
        newInstrument(sf2Soundbank, "Clarinet", new Patch(0, 71), new_clarinet);
        newInstrument(sf2Soundbank, "Flute", new Patch(0, 72), new_flute);
        newInstrument(sf2Soundbank, "Flute", new Patch(0, 73), new_flute);
        newInstrument(sf2Soundbank, "Flute", new Patch(0, 74), new_flute);
        newInstrument(sf2Soundbank, "Flute", new Patch(0, 75), new_flute);
        newInstrument(sf2Soundbank, "Flute", new Patch(0, 76), new_flute);
        newInstrument(sf2Soundbank, "Flute", new Patch(0, 77), new_flute);
        newInstrument(sf2Soundbank, "Flute", new Patch(0, 78), new_flute);
        newInstrument(sf2Soundbank, "Flute", new Patch(0, 79), new_flute);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 80), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 81), new_organ);
        newInstrument(sf2Soundbank, "Flute", new Patch(0, 82), new_flute);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 83), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 84), new_organ);
        newInstrument(sf2Soundbank, "Choir", new Patch(0, 85), new_choir);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 86), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 87), new_organ);
        newInstrument(sf2Soundbank, "Synth Strings", new Patch(0, 88), new_string2);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 89), new_organ);
        newInstrument(sf2Soundbank, "Def", new Patch(0, 90), sf2Layer);
        newInstrument(sf2Soundbank, "Choir", new Patch(0, 91), new_choir);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 92), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 93), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 94), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 95), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 96), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 97), new_organ);
        newInstrument(sf2Soundbank, "Bell", new Patch(0, 98), new_bell);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 99), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 100), new_organ);
        newInstrument(sf2Soundbank, "Organ", new Patch(0, 101), new_organ);
        newInstrument(sf2Soundbank, "Def", new Patch(0, 102), sf2Layer);
        newInstrument(sf2Soundbank, "Synth Strings", new Patch(0, 103), new_string2);
        newInstrument(sf2Soundbank, "Def", new Patch(0, 104), sf2Layer);
        newInstrument(sf2Soundbank, "Def", new Patch(0, 105), sf2Layer);
        newInstrument(sf2Soundbank, "Def", new Patch(0, 106), sf2Layer);
        newInstrument(sf2Soundbank, "Def", new Patch(0, 107), sf2Layer);
        newInstrument(sf2Soundbank, "Marimba", new Patch(0, 108), new_bell);
        newInstrument(sf2Soundbank, "Sax", new Patch(0, 109), new_sax);
        newInstrument(sf2Soundbank, "Solo String", new Patch(0, 110), new_string2, new_solostring);
        newInstrument(sf2Soundbank, "Oboe", new Patch(0, 111), new_oboe);
        newInstrument(sf2Soundbank, "Bell", new Patch(0, 112), new_bell);
        newInstrument(sf2Soundbank, "Melodic Toms", new Patch(0, 113), new_melodic_toms);
        newInstrument(sf2Soundbank, "Marimba", new Patch(0, 114), new_bell);
        newInstrument(sf2Soundbank, "Melodic Toms", new Patch(0, 115), new_melodic_toms);
        newInstrument(sf2Soundbank, "Melodic Toms", new Patch(0, 116), new_melodic_toms);
        newInstrument(sf2Soundbank, "Melodic Toms", new Patch(0, 117), new_melodic_toms);
        newInstrument(sf2Soundbank, "Reverse Cymbal", new Patch(0, 118), new_reverse_cymbal);
        newInstrument(sf2Soundbank, "Reverse Cymbal", new Patch(0, 119), new_reverse_cymbal);
        newInstrument(sf2Soundbank, "Guitar", new Patch(0, 120), new_guitar1);
        newInstrument(sf2Soundbank, "Def", new Patch(0, 121), sf2Layer);
        final SF2InstrumentRegion sf2InstrumentRegion5 = newInstrument(sf2Soundbank, "Seashore/Reverse Cymbal", new Patch(0, 122), new_reverse_cymbal).getRegions().get(0);
        sf2InstrumentRegion5.putInteger(37, 1000);
        sf2InstrumentRegion5.putInteger(36, 18500);
        sf2InstrumentRegion5.putInteger(38, 4500);
        sf2InstrumentRegion5.putInteger(8, -4500);
        final SF2InstrumentRegion sf2InstrumentRegion6 = newInstrument(sf2Soundbank, "Bird/Flute", new Patch(0, 123), new_flute).getRegions().get(0);
        sf2InstrumentRegion6.putInteger(51, 24);
        sf2InstrumentRegion6.putInteger(36, -3000);
        sf2InstrumentRegion6.putInteger(37, 1000);
        newInstrument(sf2Soundbank, "Def", new Patch(0, 124), new_side_stick);
        final SF2InstrumentRegion sf2InstrumentRegion7 = newInstrument(sf2Soundbank, "Seashore/Reverse Cymbal", new Patch(0, 125), new_reverse_cymbal).getRegions().get(0);
        sf2InstrumentRegion7.putInteger(37, 1000);
        sf2InstrumentRegion7.putInteger(36, 18500);
        sf2InstrumentRegion7.putInteger(38, 4500);
        sf2InstrumentRegion7.putInteger(8, -4500);
        newInstrument(sf2Soundbank, "Applause/crash_cymbal", new Patch(0, 126), new_crash_cymbal);
        newInstrument(sf2Soundbank, "Gunshot/side_stick", new Patch(0, 127), new_side_stick);
        for (final SF2Instrument sf2Instrument2 : sf2Soundbank.getInstruments()) {
            final Patch patch = sf2Instrument2.getPatch();
            if (!(patch instanceof ModelPatch) || !((ModelPatch)patch).isPercussion()) {
                sf2Instrument2.setName(EmergencySoundbank.general_midi_instruments[patch.getProgram()]);
            }
        }
        return sf2Soundbank;
    }
    
    public static SF2Layer new_bell(final SF2Soundbank sf2Soundbank) {
        final Random random = new Random(102030201L);
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.01;
        final double n4 = 0.05;
        final double n5 = 0.2;
        final double n6 = 1.0E-5;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        for (int i = 0; i < 40; ++i) {
            complexGaussianDist(array, n2 * (i + 1) * (1.0 + (random.nextDouble() * 2.0 - 1.0) * 0.01), n3 + (n4 - n3) * (i / 40.0), n7);
            n7 *= pow;
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "EPiano", newSimpleFFTSample(sf2Soundbank, "EPiano", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -12000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, 1200);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, -9000);
        sf2Region.putInteger(8, 16000);
        return layer;
    }
    
    public static SF2Layer new_guitar1(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.01;
        final double n4 = 0.01;
        final double n5 = 2.0;
        final double n6 = 0.01;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        final double[] array2 = new double[40];
        for (int i = 0; i < 40; ++i) {
            array2[i] = n7;
            n7 *= pow;
        }
        array2[0] = 2.0;
        array2[1] = 0.5;
        array2[2] = 0.45;
        array2[3] = 0.2;
        array2[4] = 1.0;
        array2[5] = 0.5;
        array2[6] = 2.0;
        array2[7] = 1.0;
        array2[8] = 0.5;
        array2[9] = 1.0;
        array2[9] = 0.5;
        array2[10] = 0.2;
        array2[11] = 1.0;
        array2[12] = 0.7;
        array2[13] = 0.5;
        array2[14] = 1.0;
        for (int j = 0; j < 40; ++j) {
            complexGaussianDist(array, n2 * (j + 1), n3 + (n4 - n3) * (j / 40.0), array2[j]);
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Guitar", newSimpleFFTSample(sf2Soundbank, "Guitar", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -12000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 2400);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, -100);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, -6000);
        sf2Region.putInteger(8, 16000);
        sf2Region.putInteger(48, -20);
        return layer;
    }
    
    public static SF2Layer new_guitar_dist(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.01;
        final double n4 = 0.01;
        final double n5 = 2.0;
        final double n6 = 0.01;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        final double[] array2 = new double[40];
        for (int i = 0; i < 40; ++i) {
            array2[i] = n7;
            n7 *= pow;
        }
        array2[0] = 5.0;
        array2[1] = 2.0;
        array2[2] = 0.45;
        array2[3] = 0.2;
        array2[4] = 1.0;
        array2[5] = 0.5;
        array2[6] = 2.0;
        array2[7] = 1.0;
        array2[8] = 0.5;
        array2[9] = 1.0;
        array2[9] = 0.5;
        array2[10] = 0.2;
        array2[11] = 1.0;
        array2[12] = 0.7;
        array2[13] = 0.5;
        array2[14] = 1.0;
        for (int j = 0; j < 40; ++j) {
            complexGaussianDist(array, n2 * (j + 1), n3 + (n4 - n3) * (j / 40.0), array2[j]);
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Distorted Guitar", newSimpleFFTSample_dist(sf2Soundbank, "Distorted Guitar", array, n2, 10000.0));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -12000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(8, 8000);
        return layer;
    }
    
    public static SF2Layer new_guitar_pick(final SF2Soundbank sf2Soundbank) {
        final int n = 2;
        final int n2 = 4096 * n;
        final double[] array = new double[2 * n2];
        final Random random = new Random(3049912L);
        for (int i = 0; i < array.length; i += 2) {
            array[i] = 2.0 * (random.nextDouble() - 0.5);
        }
        fft(array);
        for (int j = n2 / 2; j < array.length; ++j) {
            array[j] = 0.0;
        }
        for (int k = 0; k < 2048 * n; ++k) {
            final double[] array2 = array;
            final int n3 = k;
            array2[n3] *= Math.exp(-Math.abs((k - 23) / (double)n) * 1.2) + Math.exp(-Math.abs((k - 40) / (double)n) * 0.9);
        }
        randomPhase(array, new Random(3049912L));
        ifft(array);
        normalize(array, 0.8);
        final double[] realPart = realPart(array);
        double n4 = 1.0;
        for (int l = 0; l < realPart.length; ++l) {
            final double[] array3 = realPart;
            final int n5 = l;
            array3[n5] *= n4;
            n4 *= 0.9994;
        }
        final double[] array4 = realPart;
        fadeUp(realPart, 80);
        final SF2Sample simpleDrumSample = newSimpleDrumSample(sf2Soundbank, "Guitar Noise", array4);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Guitar Noise");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(38, 12000);
        sf2LayerRegion.setSample(simpleDrumSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_gpiano(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.2;
        final double n4 = 0.001;
        double n5 = n3;
        final double pow = Math.pow(n4 / n3, 0.06666666666666667);
        final double[] array2 = new double[30];
        for (int i = 0; i < 30; ++i) {
            array2[i] = n5;
            n5 *= pow;
        }
        final double[] array3 = array2;
        final int n6 = 0;
        array3[n6] *= 2.0;
        final double[] array4 = array2;
        final int n7 = 4;
        array4[n7] *= 2.0;
        final double[] array5 = array2;
        final int n8 = 12;
        array5[n8] *= 0.9;
        final double[] array6 = array2;
        final int n9 = 13;
        array6[n9] *= 0.7;
        for (int j = 14; j < 30; ++j) {
            final double[] array7 = array2;
            final int n10 = j;
            array7[n10] *= 0.5;
        }
        for (int k = 0; k < 30; ++k) {
            double n11 = 0.2;
            double n12 = array2[k];
            if (k > 10) {
                n11 = 5.0;
                n12 *= 10.0;
            }
            int n13 = 0;
            if (k > 5) {
                n13 = (k - 5) * 7;
            }
            complexGaussianDist(array, n2 * (k + 1) + n13, n11, n12);
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Grand Piano", newSimpleFFTSample(sf2Soundbank, "Grand Piano", array, n2, 200));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -7000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, -6000);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, -5500);
        sf2Region.putInteger(8, 18000);
        return layer;
    }
    
    public static SF2Layer new_gpiano2(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.2;
        final double n4 = 0.001;
        double n5 = n3;
        final double pow = Math.pow(n4 / n3, 0.05);
        final double[] array2 = new double[30];
        for (int i = 0; i < 30; ++i) {
            array2[i] = n5;
            n5 *= pow;
        }
        final double[] array3 = array2;
        final int n6 = 0;
        array3[n6] *= 1.0;
        final double[] array4 = array2;
        final int n7 = 4;
        array4[n7] *= 2.0;
        final double[] array5 = array2;
        final int n8 = 12;
        array5[n8] *= 0.9;
        final double[] array6 = array2;
        final int n9 = 13;
        array6[n9] *= 0.7;
        for (int j = 14; j < 30; ++j) {
            final double[] array7 = array2;
            final int n10 = j;
            array7[n10] *= 0.5;
        }
        for (int k = 0; k < 30; ++k) {
            double n11 = 0.2;
            double n12 = array2[k];
            if (k > 10) {
                n11 = 5.0;
                n12 *= 10.0;
            }
            int n13 = 0;
            if (k > 5) {
                n13 = (k - 5) * 7;
            }
            complexGaussianDist(array, n2 * (k + 1) + n13, n11, n12);
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Grand Piano", newSimpleFFTSample(sf2Soundbank, "Grand Piano", array, n2, 200));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -7000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, -6000);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, -5500);
        sf2Region.putInteger(8, 18000);
        return layer;
    }
    
    public static SF2Layer new_piano_hammer(final SF2Soundbank sf2Soundbank) {
        final int n = 2;
        final int n2 = 4096 * n;
        final double[] array = new double[2 * n2];
        final Random random = new Random(3049912L);
        for (int i = 0; i < array.length; i += 2) {
            array[i] = 2.0 * (random.nextDouble() - 0.5);
        }
        fft(array);
        for (int j = n2 / 2; j < array.length; ++j) {
            array[j] = 0.0;
        }
        for (int k = 0; k < 2048 * n; ++k) {
            final double[] array2 = array;
            final int n3 = k;
            array2[n3] *= Math.exp(-Math.abs((k - 37) / (double)n) * 0.05);
        }
        randomPhase(array, new Random(3049912L));
        ifft(array);
        normalize(array, 0.6);
        final double[] realPart = realPart(array);
        double n4 = 1.0;
        for (int l = 0; l < realPart.length; ++l) {
            final double[] array3 = realPart;
            final int n5 = l;
            array3[n5] *= n4;
            n4 *= 0.9997;
        }
        final double[] array4 = realPart;
        fadeUp(realPart, 80);
        final SF2Sample simpleDrumSample = newSimpleDrumSample(sf2Soundbank, "Piano Hammer", array4);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Piano Hammer");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(38, 12000);
        sf2LayerRegion.setSample(simpleDrumSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_piano1(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.2;
        final double n4 = 1.0E-4;
        double n5 = n3;
        final double pow = Math.pow(n4 / n3, 0.025);
        final double[] array2 = new double[30];
        for (int i = 0; i < 30; ++i) {
            array2[i] = n5;
            n5 *= pow;
        }
        final double[] array3 = array2;
        final int n6 = 0;
        array3[n6] *= 5.0;
        final double[] array4 = array2;
        final int n7 = 2;
        array4[n7] *= 0.1;
        final double[] array5 = array2;
        final int n8 = 7;
        array5[n8] *= 5.0;
        for (int j = 0; j < 30; ++j) {
            double n9 = 0.2;
            double n10 = array2[j];
            if (j > 12) {
                n9 = 5.0;
                n10 *= 10.0;
            }
            int n11 = 0;
            if (j > 5) {
                n11 = (j - 5) * 7;
            }
            complexGaussianDist(array, n2 * (j + 1) + n11, n9, n10);
        }
        complexGaussianDist(array, n2 * 15.5, 1.0, 0.1);
        complexGaussianDist(array, n2 * 17.5, 1.0, 0.01);
        final SF2Layer layer = newLayer(sf2Soundbank, "EPiano", newSimpleFFTSample(sf2Soundbank, "EPiano", array, n2, 200));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -12000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, -1200);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, -5500);
        sf2Region.putInteger(8, 16000);
        return layer;
    }
    
    public static SF2Layer new_epiano1(final SF2Soundbank sf2Soundbank) {
        final Random random = new Random(302030201L);
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.05;
        final double n4 = 0.05;
        final double n5 = 0.2;
        final double n6 = 1.0E-4;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        for (int i = 0; i < 40; ++i) {
            complexGaussianDist(array, n2 * (i + 1) * (1.0 + (random.nextDouble() * 2.0 - 1.0) * 1.0E-4), n3 + (n4 - n3) * (i / 40.0), n7);
            n7 *= pow;
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "EPiano", newSimpleFFTSample(sf2Soundbank, "EPiano", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -12000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, 1200);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, -9000);
        sf2Region.putInteger(8, 16000);
        return layer;
    }
    
    public static SF2Layer new_epiano2(final SF2Soundbank sf2Soundbank) {
        final Random random = new Random(302030201L);
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.01;
        final double n4 = 0.05;
        final double n5 = 0.2;
        final double n6 = 1.0E-5;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        for (int i = 0; i < 40; ++i) {
            complexGaussianDist(array, n2 * (i + 1) * (1.0 + (random.nextDouble() * 2.0 - 1.0) * 1.0E-4), n3 + (n4 - n3) * (i / 40.0), n7);
            n7 *= pow;
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "EPiano", newSimpleFFTSample(sf2Soundbank, "EPiano", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -12000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 8000);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, 2400);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, -9000);
        sf2Region.putInteger(8, 16000);
        sf2Region.putInteger(48, -100);
        return layer;
    }
    
    public static SF2Layer new_bass1(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.05;
        final double n4 = 0.05;
        final double n5 = 0.2;
        final double n6 = 0.02;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.04);
        final double[] array2 = new double[25];
        for (int i = 0; i < 25; ++i) {
            array2[i] = n7;
            n7 *= pow;
        }
        final double[] array3 = array2;
        final int n8 = 0;
        array3[n8] *= 8.0;
        final double[] array4 = array2;
        final int n9 = 1;
        array4[n9] *= 4.0;
        final double[] array5 = array2;
        final int n10 = 3;
        array5[n10] *= 8.0;
        final double[] array6 = array2;
        final int n11 = 5;
        array6[n11] *= 8.0;
        for (int j = 0; j < 25; ++j) {
            complexGaussianDist(array, n2 * (j + 1), n3 + (n4 - n3) * (j / 40.0), array2[j]);
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Bass", newSimpleFFTSample(sf2Soundbank, "Bass", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -12000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, -3000);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, -5000);
        sf2Region.putInteger(8, 11000);
        sf2Region.putInteger(48, -100);
        return layer;
    }
    
    public static SF2Layer new_synthbass(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.05;
        final double n4 = 0.05;
        final double n5 = 0.2;
        final double n6 = 0.02;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.04);
        final double[] array2 = new double[25];
        for (int i = 0; i < 25; ++i) {
            array2[i] = n7;
            n7 *= pow;
        }
        final double[] array3 = array2;
        final int n8 = 0;
        array3[n8] *= 16.0;
        final double[] array4 = array2;
        final int n9 = 1;
        array4[n9] *= 4.0;
        final double[] array5 = array2;
        final int n10 = 3;
        array5[n10] *= 16.0;
        final double[] array6 = array2;
        final int n11 = 5;
        array6[n11] *= 8.0;
        for (int j = 0; j < 25; ++j) {
            complexGaussianDist(array, n2 * (j + 1), n3 + (n4 - n3) * (j / 40.0), array2[j]);
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Bass", newSimpleFFTSample(sf2Soundbank, "Bass", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -12000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, -3000);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, -3000);
        sf2Region.putInteger(9, 100);
        sf2Region.putInteger(8, 8000);
        sf2Region.putInteger(48, -100);
        return layer;
    }
    
    public static SF2Layer new_bass2(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 0.05;
        final double n4 = 0.05;
        final double n5 = 0.2;
        final double n6 = 0.002;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.04);
        final double[] array2 = new double[25];
        for (int i = 0; i < 25; ++i) {
            array2[i] = n7;
            n7 *= pow;
        }
        final double[] array3 = array2;
        final int n8 = 0;
        array3[n8] *= 8.0;
        final double[] array4 = array2;
        final int n9 = 1;
        array4[n9] *= 4.0;
        final double[] array5 = array2;
        final int n10 = 3;
        array5[n10] *= 8.0;
        final double[] array6 = array2;
        final int n11 = 5;
        array6[n11] *= 8.0;
        for (int j = 0; j < 25; ++j) {
            complexGaussianDist(array, n2 * (j + 1), n3 + (n4 - n3) * (j / 40.0), array2[j]);
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Bass2", newSimpleFFTSample(sf2Soundbank, "Bass2", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -8000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(26, -6000);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(8, 5000);
        sf2Region.putInteger(48, -100);
        return layer;
    }
    
    public static SF2Layer new_solostring(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 2.0;
        final double n4 = 2.0;
        final double n5 = 0.2;
        final double n6 = 0.01;
        final double[] array2 = new double[18];
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        for (int i = 0; i < array2.length; ++i) {
            n7 *= pow;
            array2[i] = n7;
        }
        final double[] array3 = array2;
        final int n8 = 0;
        array3[n8] *= 5.0;
        final double[] array4 = array2;
        final int n9 = 1;
        array4[n9] *= 5.0;
        final double[] array5 = array2;
        final int n10 = 2;
        array5[n10] *= 5.0;
        final double[] array6 = array2;
        final int n11 = 3;
        array6[n11] *= 4.0;
        final double[] array7 = array2;
        final int n12 = 4;
        array7[n12] *= 4.0;
        final double[] array8 = array2;
        final int n13 = 5;
        array8[n13] *= 3.0;
        final double[] array9 = array2;
        final int n14 = 6;
        array9[n14] *= 3.0;
        final double[] array10 = array2;
        final int n15 = 7;
        array10[n15] *= 2.0;
        for (int j = 0; j < array2.length; ++j) {
            complexGaussianDist(array, n2 * (j + 1), n3 + (n4 - n3) * (j / 40.0), n7);
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Strings", newSimpleFFTSample(sf2Soundbank, "Strings", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -5000);
        sf2Region.putInteger(38, 1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(8, 9500);
        sf2Region.putInteger(24, -1000);
        sf2Region.putInteger(6, 15);
        return layer;
    }
    
    public static SF2Layer new_orchhit(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 2.0;
        final double n4 = 80.0;
        final double n5 = 0.2;
        final double n6 = 0.001;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        for (int i = 0; i < 40; ++i) {
            complexGaussianDist(array, n2 * (i + 1), n3 + (n4 - n3) * (i / 40.0), n7);
            n7 *= pow;
        }
        complexGaussianDist(array, n2 * 4.0, 300.0, 1.0);
        final SF2Layer layer = newLayer(sf2Soundbank, "Och Strings", newSimpleFFTSample(sf2Soundbank, "Och Strings", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -5000);
        sf2Region.putInteger(38, 200);
        sf2Region.putInteger(36, 200);
        sf2Region.putInteger(37, 1000);
        sf2Region.putInteger(8, 9500);
        return layer;
    }
    
    public static SF2Layer new_string2(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 2.0;
        final double n4 = 80.0;
        final double n5 = 0.2;
        final double n6 = 0.001;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        for (int i = 0; i < 40; ++i) {
            complexGaussianDist(array, n2 * (i + 1), n3 + (n4 - n3) * (i / 40.0), n7);
            n7 *= pow;
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Strings", newSimpleFFTSample(sf2Soundbank, "Strings", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -5000);
        sf2Region.putInteger(38, 1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(8, 9500);
        return layer;
    }
    
    public static SF2Layer new_choir(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 25;
        final double n3 = 2.0;
        final double n4 = 80.0;
        final double n5 = 0.2;
        final double n6 = 0.001;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        final double[] array2 = new double[40];
        for (int i = 0; i < array2.length; ++i) {
            n7 *= pow;
            array2[i] = n7;
        }
        final double[] array3 = array2;
        final int n8 = 5;
        array3[n8] *= 0.1;
        final double[] array4 = array2;
        final int n9 = 6;
        array4[n9] *= 0.01;
        final double[] array5 = array2;
        final int n10 = 7;
        array5[n10] *= 0.1;
        final double[] array6 = array2;
        final int n11 = 8;
        array6[n11] *= 0.1;
        for (int j = 0; j < array2.length; ++j) {
            complexGaussianDist(array, n2 * (j + 1), n3 + (n4 - n3) * (j / 40.0), array2[j]);
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Strings", newSimpleFFTSample(sf2Soundbank, "Strings", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -5000);
        sf2Region.putInteger(38, 1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(8, 9500);
        return layer;
    }
    
    public static SF2Layer new_organ(final SF2Soundbank sf2Soundbank) {
        final Random random = new Random(102030201L);
        final int n = 1;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        final double n3 = 0.01;
        final double n4 = 0.01;
        final double n5 = 0.2;
        final double n6 = 0.001;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.025);
        for (int i = 0; i < 12; ++i) {
            complexGaussianDist(array, n2 * (i + 1), n3 + (n4 - n3) * (i / 40.0), n7 * (0.5 + 3.0 * random.nextDouble()));
            n7 *= pow;
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Organ", newSimpleFFTSample(sf2Soundbank, "Organ", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -6000);
        sf2Region.putInteger(38, -1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(8, 9500);
        return layer;
    }
    
    public static SF2Layer new_ch_organ(final SF2Soundbank sf2Soundbank) {
        final int n = 1;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        final double n3 = 0.01;
        final double n4 = 0.01;
        final double n5 = 0.2;
        final double n6 = 0.001;
        double n7 = n5;
        final double pow = Math.pow(n6 / n5, 0.016666666666666666);
        final double[] array2 = new double[60];
        for (int i = 0; i < array2.length; ++i) {
            n7 *= pow;
            array2[i] = n7;
        }
        final double[] array3 = array2;
        final int n8 = 0;
        array3[n8] *= 5.0;
        final double[] array4 = array2;
        final int n9 = 1;
        array4[n9] *= 2.0;
        array2[2] = 0.0;
        array2[5] = (array2[4] = 0.0);
        final double[] array5 = array2;
        final int n10 = 7;
        array5[n10] *= 7.0;
        array2[9] = 0.0;
        array2[12] = (array2[10] = 0.0);
        final double[] array6 = array2;
        final int n11 = 15;
        array6[n11] *= 7.0;
        array2[18] = 0.0;
        array2[24] = (array2[20] = 0.0);
        final double[] array7 = array2;
        final int n12 = 27;
        array7[n12] *= 5.0;
        array2[29] = 0.0;
        array2[33] = (array2[30] = 0.0);
        final double[] array8 = array2;
        final int n13 = 36;
        array8[n13] *= 4.0;
        array2[37] = 0.0;
        array2[42] = (array2[39] = 0.0);
        array2[47] = (array2[43] = 0.0);
        final double[] array9 = array2;
        final int n14 = 50;
        array9[n14] *= 4.0;
        array2[52] = 0.0;
        array2[57] = (array2[55] = 0.0);
        final double[] array10 = array2;
        final int n15 = 10;
        array10[n15] *= 0.1;
        final double[] array11 = array2;
        final int n16 = 11;
        array11[n16] *= 0.1;
        final double[] array12 = array2;
        final int n17 = 12;
        array12[n17] *= 0.1;
        final double[] array13 = array2;
        final int n18 = 13;
        array13[n18] *= 0.1;
        final double[] array14 = array2;
        final int n19 = 17;
        array14[n19] *= 0.1;
        final double[] array15 = array2;
        final int n20 = 18;
        array15[n20] *= 0.1;
        final double[] array16 = array2;
        final int n21 = 19;
        array16[n21] *= 0.1;
        final double[] array17 = array2;
        final int n22 = 20;
        array17[n22] *= 0.1;
        for (int j = 0; j < 60; ++j) {
            complexGaussianDist(array, n2 * (j + 1), n3 + (n4 - n3) * (j / 40.0), array2[j]);
            n7 *= pow;
        }
        final SF2Layer layer = newLayer(sf2Soundbank, "Organ", newSimpleFFTSample(sf2Soundbank, "Organ", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -10000);
        sf2Region.putInteger(38, -1000);
        return layer;
    }
    
    public static SF2Layer new_flute(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        complexGaussianDist(array, n2 * 1.0, 0.001, 0.5);
        complexGaussianDist(array, n2 * 2.0, 0.001, 0.5);
        complexGaussianDist(array, n2 * 3.0, 0.001, 0.5);
        complexGaussianDist(array, n2 * 4.0, 0.01, 0.5);
        complexGaussianDist(array, n2 * 4.0, 100.0, 120.0);
        complexGaussianDist(array, n2 * 6.0, 100.0, 40.0);
        complexGaussianDist(array, n2 * 8.0, 100.0, 80.0);
        complexGaussianDist(array, n2 * 5.0, 0.001, 0.05);
        complexGaussianDist(array, n2 * 6.0, 0.001, 0.06);
        complexGaussianDist(array, n2 * 7.0, 0.001, 0.04);
        complexGaussianDist(array, n2 * 8.0, 0.005, 0.06);
        complexGaussianDist(array, n2 * 9.0, 0.005, 0.06);
        complexGaussianDist(array, n2 * 10.0, 0.01, 0.1);
        complexGaussianDist(array, n2 * 11.0, 0.08, 0.7);
        complexGaussianDist(array, n2 * 12.0, 0.08, 0.6);
        complexGaussianDist(array, n2 * 13.0, 0.08, 0.6);
        complexGaussianDist(array, n2 * 14.0, 0.08, 0.6);
        complexGaussianDist(array, n2 * 15.0, 0.08, 0.5);
        complexGaussianDist(array, n2 * 16.0, 0.08, 0.5);
        complexGaussianDist(array, n2 * 17.0, 0.08, 0.2);
        complexGaussianDist(array, n2 * 1.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 2.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 3.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 4.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 5.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 6.0, 20.0, 9.0);
        complexGaussianDist(array, n2 * 7.0, 20.0, 9.0);
        complexGaussianDist(array, n2 * 8.0, 20.0, 9.0);
        complexGaussianDist(array, n2 * 9.0, 20.0, 8.0);
        complexGaussianDist(array, n2 * 10.0, 30.0, 8.0);
        complexGaussianDist(array, n2 * 11.0, 30.0, 9.0);
        complexGaussianDist(array, n2 * 12.0, 30.0, 9.0);
        complexGaussianDist(array, n2 * 13.0, 30.0, 8.0);
        complexGaussianDist(array, n2 * 14.0, 30.0, 8.0);
        complexGaussianDist(array, n2 * 15.0, 30.0, 7.0);
        complexGaussianDist(array, n2 * 16.0, 30.0, 7.0);
        complexGaussianDist(array, n2 * 17.0, 30.0, 6.0);
        final SF2Layer layer = newLayer(sf2Soundbank, "Flute", newSimpleFFTSample(sf2Soundbank, "Flute", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -6000);
        sf2Region.putInteger(38, -1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(8, 9500);
        return layer;
    }
    
    public static SF2Layer new_horn(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        final double n3 = 0.5;
        final double n4 = 1.0E-11;
        double n5 = n3;
        final double pow = Math.pow(n4 / n3, 0.025);
        for (int i = 0; i < 40; ++i) {
            if (i == 0) {
                complexGaussianDist(array, n2 * (i + 1), 0.1, n5 * 0.2);
            }
            else {
                complexGaussianDist(array, n2 * (i + 1), 0.1, n5);
            }
            n5 *= pow;
        }
        complexGaussianDist(array, n2 * 2.0, 100.0, 1.0);
        final SF2Layer layer = newLayer(sf2Soundbank, "Horn", newSimpleFFTSample(sf2Soundbank, "Horn", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -6000);
        sf2Region.putInteger(38, -1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(26, -500);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, 5000);
        sf2Region.putInteger(8, 4500);
        return layer;
    }
    
    public static SF2Layer new_trumpet(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        final double n3 = 0.5;
        final double n4 = 1.0E-5;
        double n5 = n3;
        final double pow = Math.pow(n4 / n3, 0.0125);
        final double[] array2 = new double[80];
        for (int i = 0; i < 80; ++i) {
            array2[i] = n5;
            n5 *= pow;
        }
        final double[] array3 = array2;
        final int n6 = 0;
        array3[n6] *= 0.05;
        final double[] array4 = array2;
        final int n7 = 1;
        array4[n7] *= 0.2;
        final double[] array5 = array2;
        final int n8 = 2;
        array5[n8] *= 0.5;
        final double[] array6 = array2;
        final int n9 = 3;
        array6[n9] *= 0.85;
        for (int j = 0; j < 80; ++j) {
            complexGaussianDist(array, n2 * (j + 1), 0.1, array2[j]);
        }
        complexGaussianDist(array, n2 * 5.0, 300.0, 3.0);
        final SF2Layer layer = newLayer(sf2Soundbank, "Trumpet", newSimpleFFTSample(sf2Soundbank, "Trumpet", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -10000);
        sf2Region.putInteger(38, 0);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(26, -4000);
        sf2Region.putInteger(30, -2500);
        sf2Region.putInteger(11, 5000);
        sf2Region.putInteger(8, 4500);
        sf2Region.putInteger(9, 10);
        return layer;
    }
    
    public static SF2Layer new_brass_section(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        final double n3 = 0.5;
        final double n4 = 0.005;
        double n5 = n3;
        final double pow = Math.pow(n4 / n3, 0.03333333333333333);
        final double[] array2 = new double[30];
        for (int i = 0; i < 30; ++i) {
            array2[i] = n5;
            n5 *= pow;
        }
        final double[] array3 = array2;
        final int n6 = 0;
        array3[n6] *= 0.8;
        final double[] array4 = array2;
        final int n7 = 1;
        array4[n7] *= 0.9;
        double n8 = 5.0;
        for (int j = 0; j < 30; ++j) {
            complexGaussianDist(array, n2 * (j + 1), 0.1 * n8, array2[j] * n8);
            n8 += 6.0;
        }
        complexGaussianDist(array, n2 * 6.0, 300.0, 2.0);
        final SF2Layer layer = newLayer(sf2Soundbank, "Brass Section", newSimpleFFTSample(sf2Soundbank, "Brass Section", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -9200);
        sf2Region.putInteger(38, -1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(26, -3000);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, 5000);
        sf2Region.putInteger(8, 4500);
        return layer;
    }
    
    public static SF2Layer new_trombone(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        final double n3 = 0.5;
        final double n4 = 0.001;
        double n5 = n3;
        final double pow = Math.pow(n4 / n3, 0.0125);
        final double[] array2 = new double[80];
        for (int i = 0; i < 80; ++i) {
            array2[i] = n5;
            n5 *= pow;
        }
        final double[] array3 = array2;
        final int n6 = 0;
        array3[n6] *= 0.3;
        final double[] array4 = array2;
        final int n7 = 1;
        array4[n7] *= 0.7;
        for (int j = 0; j < 80; ++j) {
            complexGaussianDist(array, n2 * (j + 1), 0.1, array2[j]);
        }
        complexGaussianDist(array, n2 * 6.0, 300.0, 2.0);
        final SF2Layer layer = newLayer(sf2Soundbank, "Trombone", newSimpleFFTSample(sf2Soundbank, "Trombone", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -8000);
        sf2Region.putInteger(38, -1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(26, -2000);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, 5000);
        sf2Region.putInteger(8, 4500);
        sf2Region.putInteger(9, 10);
        return layer;
    }
    
    public static SF2Layer new_sax(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        final double n3 = 0.5;
        final double n4 = 0.01;
        double n5 = n3;
        final double pow = Math.pow(n4 / n3, 0.025);
        for (int i = 0; i < 40; ++i) {
            if (i == 0 || i == 2) {
                complexGaussianDist(array, n2 * (i + 1), 0.1, n5 * 4.0);
            }
            else {
                complexGaussianDist(array, n2 * (i + 1), 0.1, n5);
            }
            n5 *= pow;
        }
        complexGaussianDist(array, n2 * 4.0, 200.0, 1.0);
        final SF2Layer layer = newLayer(sf2Soundbank, "Sax", newSimpleFFTSample(sf2Soundbank, "Sax", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -6000);
        sf2Region.putInteger(38, -1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(26, -3000);
        sf2Region.putInteger(30, 12000);
        sf2Region.putInteger(11, 5000);
        sf2Region.putInteger(8, 4500);
        return layer;
    }
    
    public static SF2Layer new_oboe(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        complexGaussianDist(array, n2 * 5.0, 100.0, 80.0);
        complexGaussianDist(array, n2 * 1.0, 0.01, 0.53);
        complexGaussianDist(array, n2 * 2.0, 0.01, 0.51);
        complexGaussianDist(array, n2 * 3.0, 0.01, 0.48);
        complexGaussianDist(array, n2 * 4.0, 0.01, 0.49);
        complexGaussianDist(array, n2 * 5.0, 0.01, 5.0);
        complexGaussianDist(array, n2 * 6.0, 0.01, 0.51);
        complexGaussianDist(array, n2 * 7.0, 0.01, 0.5);
        complexGaussianDist(array, n2 * 8.0, 0.01, 0.59);
        complexGaussianDist(array, n2 * 9.0, 0.01, 0.61);
        complexGaussianDist(array, n2 * 10.0, 0.01, 0.52);
        complexGaussianDist(array, n2 * 11.0, 0.01, 0.49);
        complexGaussianDist(array, n2 * 12.0, 0.01, 0.51);
        complexGaussianDist(array, n2 * 13.0, 0.01, 0.48);
        complexGaussianDist(array, n2 * 14.0, 0.01, 0.51);
        complexGaussianDist(array, n2 * 15.0, 0.01, 0.46);
        complexGaussianDist(array, n2 * 16.0, 0.01, 0.35);
        complexGaussianDist(array, n2 * 17.0, 0.01, 0.2);
        complexGaussianDist(array, n2 * 18.0, 0.01, 0.1);
        complexGaussianDist(array, n2 * 19.0, 0.01, 0.5);
        complexGaussianDist(array, n2 * 20.0, 0.01, 0.1);
        final SF2Layer layer = newLayer(sf2Soundbank, "Oboe", newSimpleFFTSample(sf2Soundbank, "Oboe", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -6000);
        sf2Region.putInteger(38, -1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(8, 9500);
        return layer;
    }
    
    public static SF2Layer new_bassoon(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        complexGaussianDist(array, n2 * 2.0, 100.0, 40.0);
        complexGaussianDist(array, n2 * 4.0, 100.0, 20.0);
        complexGaussianDist(array, n2 * 1.0, 0.01, 0.53);
        complexGaussianDist(array, n2 * 2.0, 0.01, 5.0);
        complexGaussianDist(array, n2 * 3.0, 0.01, 0.51);
        complexGaussianDist(array, n2 * 4.0, 0.01, 0.48);
        complexGaussianDist(array, n2 * 5.0, 0.01, 1.49);
        complexGaussianDist(array, n2 * 6.0, 0.01, 0.51);
        complexGaussianDist(array, n2 * 7.0, 0.01, 0.5);
        complexGaussianDist(array, n2 * 8.0, 0.01, 0.59);
        complexGaussianDist(array, n2 * 9.0, 0.01, 0.61);
        complexGaussianDist(array, n2 * 10.0, 0.01, 0.52);
        complexGaussianDist(array, n2 * 11.0, 0.01, 0.49);
        complexGaussianDist(array, n2 * 12.0, 0.01, 0.51);
        complexGaussianDist(array, n2 * 13.0, 0.01, 0.48);
        complexGaussianDist(array, n2 * 14.0, 0.01, 0.51);
        complexGaussianDist(array, n2 * 15.0, 0.01, 0.46);
        complexGaussianDist(array, n2 * 16.0, 0.01, 0.35);
        complexGaussianDist(array, n2 * 17.0, 0.01, 0.2);
        complexGaussianDist(array, n2 * 18.0, 0.01, 0.1);
        complexGaussianDist(array, n2 * 19.0, 0.01, 0.5);
        complexGaussianDist(array, n2 * 20.0, 0.01, 0.1);
        final SF2Layer layer = newLayer(sf2Soundbank, "Flute", newSimpleFFTSample(sf2Soundbank, "Flute", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -6000);
        sf2Region.putInteger(38, -1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(8, 9500);
        return layer;
    }
    
    public static SF2Layer new_clarinet(final SF2Soundbank sf2Soundbank) {
        final int n = 8;
        final double[] array = new double[4096 * n * 2];
        final double n2 = n * 15;
        complexGaussianDist(array, n2 * 1.0, 0.001, 0.5);
        complexGaussianDist(array, n2 * 2.0, 0.001, 0.02);
        complexGaussianDist(array, n2 * 3.0, 0.001, 0.2);
        complexGaussianDist(array, n2 * 4.0, 0.01, 0.1);
        complexGaussianDist(array, n2 * 4.0, 100.0, 60.0);
        complexGaussianDist(array, n2 * 6.0, 100.0, 20.0);
        complexGaussianDist(array, n2 * 8.0, 100.0, 20.0);
        complexGaussianDist(array, n2 * 5.0, 0.001, 0.1);
        complexGaussianDist(array, n2 * 6.0, 0.001, 0.09);
        complexGaussianDist(array, n2 * 7.0, 0.001, 0.02);
        complexGaussianDist(array, n2 * 8.0, 0.005, 0.16);
        complexGaussianDist(array, n2 * 9.0, 0.005, 0.96);
        complexGaussianDist(array, n2 * 10.0, 0.01, 0.9);
        complexGaussianDist(array, n2 * 11.0, 0.08, 1.2);
        complexGaussianDist(array, n2 * 12.0, 0.08, 1.8);
        complexGaussianDist(array, n2 * 13.0, 0.08, 1.6);
        complexGaussianDist(array, n2 * 14.0, 0.08, 1.2);
        complexGaussianDist(array, n2 * 15.0, 0.08, 0.9);
        complexGaussianDist(array, n2 * 16.0, 0.08, 0.5);
        complexGaussianDist(array, n2 * 17.0, 0.08, 0.2);
        complexGaussianDist(array, n2 * 1.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 2.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 3.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 4.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 5.0, 10.0, 8.0);
        complexGaussianDist(array, n2 * 6.0, 20.0, 9.0);
        complexGaussianDist(array, n2 * 7.0, 20.0, 9.0);
        complexGaussianDist(array, n2 * 8.0, 20.0, 9.0);
        complexGaussianDist(array, n2 * 9.0, 20.0, 8.0);
        complexGaussianDist(array, n2 * 10.0, 30.0, 8.0);
        complexGaussianDist(array, n2 * 11.0, 30.0, 9.0);
        complexGaussianDist(array, n2 * 12.0, 30.0, 9.0);
        complexGaussianDist(array, n2 * 13.0, 30.0, 8.0);
        complexGaussianDist(array, n2 * 14.0, 30.0, 8.0);
        complexGaussianDist(array, n2 * 15.0, 30.0, 7.0);
        complexGaussianDist(array, n2 * 16.0, 30.0, 7.0);
        complexGaussianDist(array, n2 * 17.0, 30.0, 6.0);
        final SF2Layer layer = newLayer(sf2Soundbank, "Clarinet", newSimpleFFTSample(sf2Soundbank, "Clarinet", array, n2));
        final SF2Region sf2Region = layer.getRegions().get(0);
        sf2Region.putInteger(54, 1);
        sf2Region.putInteger(34, -6000);
        sf2Region.putInteger(38, -1000);
        sf2Region.putInteger(36, 4000);
        sf2Region.putInteger(37, -100);
        sf2Region.putInteger(8, 9500);
        return layer;
    }
    
    public static SF2Layer new_timpani(final SF2Soundbank sf2Soundbank) {
        final double[] array = new double[2 * 32768];
        final double n = 48.0;
        complexGaussianDist(array, n * 2.0, 0.2, 1.0);
        complexGaussianDist(array, n * 3.0, 0.2, 0.7);
        complexGaussianDist(array, n * 5.0, 10.0, 1.0);
        complexGaussianDist(array, n * 6.0, 9.0, 1.0);
        complexGaussianDist(array, n * 8.0, 15.0, 1.0);
        complexGaussianDist(array, n * 9.0, 18.0, 0.8);
        complexGaussianDist(array, n * 11.0, 21.0, 0.5);
        complexGaussianDist(array, n * 13.0, 28.0, 0.3);
        complexGaussianDist(array, n * 14.0, 22.0, 0.1);
        randomPhase(array, new Random(3049912L));
        ifft(array);
        normalize(array, 0.5);
        final double[] realPart = realPart(array);
        final double n2 = realPart.length;
        for (int i = 0; i < realPart.length; ++i) {
            final double n3 = 1.0 - i / n2;
            final double[] array2 = realPart;
            final int n4 = i;
            array2[n4] *= n3 * n3;
        }
        fadeUp(realPart, 40);
        final double[] array3 = realPart;
        final int n5 = 16384;
        final double[] array4 = new double[2 * n5];
        final Random random = new Random(3049912L);
        for (int j = 0; j < array4.length; j += 2) {
            array4[j] = 2.0 * (random.nextDouble() - 0.5) * 0.1;
        }
        fft(array4);
        for (int k = n5 / 2; k < array4.length; ++k) {
            array4[k] = 0.0;
        }
        for (int l = 4096; l < 8192; ++l) {
            array4[l] = 1.0 - (l - 4096) / 4096.0;
        }
        for (int n6 = 0; n6 < 300; ++n6) {
            final double n7 = 1.0 - n6 / 300.0;
            final double[] array5 = array4;
            final int n8 = n6;
            array5[n8] *= 1.0 + 20.0 * n7 * n7;
        }
        for (int n9 = 0; n9 < 24; ++n9) {
            array4[n9] = 0.0;
        }
        randomPhase(array4, new Random(3049912L));
        ifft(array4);
        normalize(array4, 0.9);
        final double[] realPart2 = realPart(array4);
        double n10 = 1.0;
        for (int n11 = 0; n11 < realPart2.length; ++n11) {
            final double[] array6 = realPart2;
            final int n12 = n11;
            array6[n12] *= n10;
            n10 *= 0.9998;
        }
        final double[] array7 = realPart2;
        for (int n13 = 0; n13 < array7.length; ++n13) {
            final double[] array8 = array3;
            final int n14 = n13;
            array8[n14] += array7[n13] * 0.02;
        }
        normalize(array3, 0.9);
        final SF2Sample simpleDrumSample = newSimpleDrumSample(sf2Soundbank, "Timpani", array3);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Timpani");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(38, 12000);
        sf2LayerRegion.putInteger(48, -100);
        sf2LayerRegion.setSample(simpleDrumSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_melodic_toms(final SF2Soundbank sf2Soundbank) {
        final double[] array = new double[2 * 16384];
        complexGaussianDist(array, 30.0, 0.5, 1.0);
        randomPhase(array, new Random(3049912L));
        ifft(array);
        normalize(array, 0.8);
        final double[] realPart = realPart(array);
        final double n = realPart.length;
        for (int i = 0; i < realPart.length; ++i) {
            final double[] array2 = realPart;
            final int n2 = i;
            array2[n2] *= 1.0 - i / n;
        }
        final double[] array3 = realPart;
        final int n3 = 16384;
        final double[] array4 = new double[2 * n3];
        final Random random = new Random(3049912L);
        for (int j = 0; j < array4.length; j += 2) {
            array4[j] = 2.0 * (random.nextDouble() - 0.5) * 0.1;
        }
        fft(array4);
        for (int k = n3 / 2; k < array4.length; ++k) {
            array4[k] = 0.0;
        }
        for (int l = 4096; l < 8192; ++l) {
            array4[l] = 1.0 - (l - 4096) / 4096.0;
        }
        for (int n4 = 0; n4 < 200; ++n4) {
            final double n5 = 1.0 - n4 / 200.0;
            final double[] array5 = array4;
            final int n6 = n4;
            array5[n6] *= 1.0 + 20.0 * n5 * n5;
        }
        for (int n7 = 0; n7 < 30; ++n7) {
            array4[n7] = 0.0;
        }
        randomPhase(array4, new Random(3049912L));
        ifft(array4);
        normalize(array4, 0.9);
        final double[] realPart2 = realPart(array4);
        double n8 = 1.0;
        for (int n9 = 0; n9 < realPart2.length; ++n9) {
            final double[] array6 = realPart2;
            final int n10 = n9;
            array6[n10] *= n8;
            n8 *= 0.9996;
        }
        final double[] array7 = realPart2;
        for (int n11 = 0; n11 < array7.length; ++n11) {
            final double[] array8 = array3;
            final int n12 = n11;
            array8[n12] += array7[n11] * 0.5;
        }
        for (int n13 = 0; n13 < 5; ++n13) {
            final double[] array9 = array3;
            final int n14 = n13;
            array9[n14] *= n13 / 5.0;
        }
        normalize(array3, 0.99);
        final SF2Sample simpleDrumSample = newSimpleDrumSample(sf2Soundbank, "Melodic Toms", array3);
        simpleDrumSample.setOriginalPitch(63);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Melodic Toms");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(38, 12000);
        sf2LayerRegion.putInteger(48, -100);
        sf2LayerRegion.setSample(simpleDrumSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_reverse_cymbal(final SF2Soundbank sf2Soundbank) {
        final int n = 16384;
        final double[] array = new double[2 * n];
        final Random random = new Random(3049912L);
        for (int i = 0; i < array.length; i += 2) {
            array[i] = 2.0 * (random.nextDouble() - 0.5);
        }
        for (int j = n / 2; j < array.length; ++j) {
            array[j] = 0.0;
        }
        for (int k = 0; k < 100; ++k) {
            array[k] = 0.0;
        }
        for (int l = 0; l < 1024; ++l) {
            array[l] = 1.0 - l / 1024.0;
        }
        final SF2Sample simpleFFTSample = newSimpleFFTSample(sf2Soundbank, "Reverse Cymbal", array, 100.0, 20);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Reverse Cymbal");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(34, -200);
        sf2LayerRegion.putInteger(36, -12000);
        sf2LayerRegion.putInteger(54, 1);
        sf2LayerRegion.putInteger(38, -1000);
        sf2LayerRegion.putInteger(37, 1000);
        sf2LayerRegion.setSample(simpleFFTSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_snare_drum(final SF2Soundbank sf2Soundbank) {
        final double[] array = new double[2 * 16384];
        complexGaussianDist(array, 24.0, 0.5, 1.0);
        randomPhase(array, new Random(3049912L));
        ifft(array);
        normalize(array, 0.5);
        final double[] realPart = realPart(array);
        final double n = realPart.length;
        for (int i = 0; i < realPart.length; ++i) {
            final double[] array2 = realPart;
            final int n2 = i;
            array2[n2] *= 1.0 - i / n;
        }
        final double[] array3 = realPart;
        final int n3 = 16384;
        final double[] array4 = new double[2 * n3];
        final Random random = new Random(3049912L);
        for (int j = 0; j < array4.length; j += 2) {
            array4[j] = 2.0 * (random.nextDouble() - 0.5) * 0.1;
        }
        fft(array4);
        for (int k = n3 / 2; k < array4.length; ++k) {
            array4[k] = 0.0;
        }
        for (int l = 4096; l < 8192; ++l) {
            array4[l] = 1.0 - (l - 4096) / 4096.0;
        }
        for (int n4 = 0; n4 < 300; ++n4) {
            final double n5 = 1.0 - n4 / 300.0;
            final double[] array5 = array4;
            final int n6 = n4;
            array5[n6] *= 1.0 + 20.0 * n5 * n5;
        }
        for (int n7 = 0; n7 < 24; ++n7) {
            array4[n7] = 0.0;
        }
        randomPhase(array4, new Random(3049912L));
        ifft(array4);
        normalize(array4, 0.9);
        final double[] realPart2 = realPart(array4);
        double n8 = 1.0;
        for (int n9 = 0; n9 < realPart2.length; ++n9) {
            final double[] array6 = realPart2;
            final int n10 = n9;
            array6[n10] *= n8;
            n8 *= 0.9998;
        }
        final double[] array7 = realPart2;
        for (int n11 = 0; n11 < array7.length; ++n11) {
            final double[] array8 = array3;
            final int n12 = n11;
            array8[n12] += array7[n11];
        }
        for (int n13 = 0; n13 < 5; ++n13) {
            final double[] array9 = array3;
            final int n14 = n13;
            array9[n14] *= n13 / 5.0;
        }
        final SF2Sample simpleDrumSample = newSimpleDrumSample(sf2Soundbank, "Snare Drum", array3);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Snare Drum");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(38, 12000);
        sf2LayerRegion.putInteger(56, 0);
        sf2LayerRegion.putInteger(48, -100);
        sf2LayerRegion.setSample(simpleDrumSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_bass_drum(final SF2Soundbank sf2Soundbank) {
        final double[] array = new double[2 * 16384];
        complexGaussianDist(array, 10.0, 2.0, 1.0);
        complexGaussianDist(array, 17.2, 2.0, 1.0);
        randomPhase(array, new Random(3049912L));
        ifft(array);
        normalize(array, 0.9);
        final double[] realPart = realPart(array);
        final double n = realPart.length;
        for (int i = 0; i < realPart.length; ++i) {
            final double[] array2 = realPart;
            final int n2 = i;
            array2[n2] *= 1.0 - i / n;
        }
        final double[] array3 = realPart;
        final int n3 = 4096;
        final double[] array4 = new double[2 * n3];
        final Random random = new Random(3049912L);
        for (int j = 0; j < array4.length; j += 2) {
            array4[j] = 2.0 * (random.nextDouble() - 0.5) * 0.1;
        }
        fft(array4);
        for (int k = n3 / 2; k < array4.length; ++k) {
            array4[k] = 0.0;
        }
        for (int l = 1024; l < 2048; ++l) {
            array4[l] = 1.0 - (l - 1024) / 1024.0;
        }
        for (int n4 = 0; n4 < 512; ++n4) {
            array4[n4] = 10 * n4 / 512.0;
        }
        for (int n5 = 0; n5 < 10; ++n5) {
            array4[n5] = 0.0;
        }
        randomPhase(array4, new Random(3049912L));
        ifft(array4);
        normalize(array4, 0.9);
        final double[] realPart2 = realPart(array4);
        double n6 = 1.0;
        for (int n7 = 0; n7 < realPart2.length; ++n7) {
            final double[] array5 = realPart2;
            final int n8 = n7;
            array5[n8] *= n6;
            n6 *= 0.999;
        }
        final double[] array6 = realPart2;
        for (int n9 = 0; n9 < array6.length; ++n9) {
            final double[] array7 = array3;
            final int n10 = n9;
            array7[n10] += array6[n9] * 0.5;
        }
        for (int n11 = 0; n11 < 5; ++n11) {
            final double[] array8 = array3;
            final int n12 = n11;
            array8[n12] *= n11 / 5.0;
        }
        final SF2Sample simpleDrumSample = newSimpleDrumSample(sf2Soundbank, "Bass Drum", array3);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Bass Drum");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(38, 12000);
        sf2LayerRegion.putInteger(56, 0);
        sf2LayerRegion.putInteger(48, -100);
        sf2LayerRegion.setSample(simpleDrumSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_tom(final SF2Soundbank sf2Soundbank) {
        final double[] array = new double[2 * 16384];
        complexGaussianDist(array, 30.0, 0.5, 1.0);
        randomPhase(array, new Random(3049912L));
        ifft(array);
        normalize(array, 0.8);
        final double[] realPart = realPart(array);
        final double n = realPart.length;
        for (int i = 0; i < realPart.length; ++i) {
            final double[] array2 = realPart;
            final int n2 = i;
            array2[n2] *= 1.0 - i / n;
        }
        final double[] array3 = realPart;
        final int n3 = 16384;
        final double[] array4 = new double[2 * n3];
        final Random random = new Random(3049912L);
        for (int j = 0; j < array4.length; j += 2) {
            array4[j] = 2.0 * (random.nextDouble() - 0.5) * 0.1;
        }
        fft(array4);
        for (int k = n3 / 2; k < array4.length; ++k) {
            array4[k] = 0.0;
        }
        for (int l = 4096; l < 8192; ++l) {
            array4[l] = 1.0 - (l - 4096) / 4096.0;
        }
        for (int n4 = 0; n4 < 200; ++n4) {
            final double n5 = 1.0 - n4 / 200.0;
            final double[] array5 = array4;
            final int n6 = n4;
            array5[n6] *= 1.0 + 20.0 * n5 * n5;
        }
        for (int n7 = 0; n7 < 30; ++n7) {
            array4[n7] = 0.0;
        }
        randomPhase(array4, new Random(3049912L));
        ifft(array4);
        normalize(array4, 0.9);
        final double[] realPart2 = realPart(array4);
        double n8 = 1.0;
        for (int n9 = 0; n9 < realPart2.length; ++n9) {
            final double[] array6 = realPart2;
            final int n10 = n9;
            array6[n10] *= n8;
            n8 *= 0.9996;
        }
        final double[] array7 = realPart2;
        for (int n11 = 0; n11 < array7.length; ++n11) {
            final double[] array8 = array3;
            final int n12 = n11;
            array8[n12] += array7[n11] * 0.5;
        }
        for (int n13 = 0; n13 < 5; ++n13) {
            final double[] array9 = array3;
            final int n14 = n13;
            array9[n14] *= n13 / 5.0;
        }
        normalize(array3, 0.99);
        final SF2Sample simpleDrumSample = newSimpleDrumSample(sf2Soundbank, "Tom", array3);
        simpleDrumSample.setOriginalPitch(50);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Tom");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(38, 12000);
        sf2LayerRegion.putInteger(48, -100);
        sf2LayerRegion.setSample(simpleDrumSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_closed_hihat(final SF2Soundbank sf2Soundbank) {
        final int n = 16384;
        final double[] array = new double[2 * n];
        final Random random = new Random(3049912L);
        for (int i = 0; i < array.length; i += 2) {
            array[i] = 2.0 * (random.nextDouble() - 0.5) * 0.1;
        }
        fft(array);
        for (int j = n / 2; j < array.length; ++j) {
            array[j] = 0.0;
        }
        for (int k = 4096; k < 8192; ++k) {
            array[k] = 1.0 - (k - 4096) / 4096.0;
        }
        for (int l = 0; l < 2048; ++l) {
            array[l] = 0.2 + 0.8 * (l / 2048.0);
        }
        randomPhase(array, new Random(3049912L));
        ifft(array);
        normalize(array, 0.9);
        final double[] realPart = realPart(array);
        double n2 = 1.0;
        for (int n3 = 0; n3 < realPart.length; ++n3) {
            final double[] array2 = realPart;
            final int n4 = n3;
            array2[n4] *= n2;
            n2 *= 0.9996;
        }
        final double[] array3 = realPart;
        for (int n5 = 0; n5 < 5; ++n5) {
            final double[] array4 = array3;
            final int n6 = n5;
            array4[n6] *= n5 / 5.0;
        }
        final SF2Sample simpleDrumSample = newSimpleDrumSample(sf2Soundbank, "Closed Hi-Hat", array3);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Closed Hi-Hat");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(38, 12000);
        sf2LayerRegion.putInteger(56, 0);
        sf2LayerRegion.putInteger(57, 1);
        sf2LayerRegion.setSample(simpleDrumSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_open_hihat(final SF2Soundbank sf2Soundbank) {
        final int n = 16384;
        final double[] array = new double[2 * n];
        final Random random = new Random(3049912L);
        for (int i = 0; i < array.length; i += 2) {
            array[i] = 2.0 * (random.nextDouble() - 0.5);
        }
        for (int j = n / 2; j < array.length; ++j) {
            array[j] = 0.0;
        }
        for (int k = 0; k < 200; ++k) {
            array[k] = 0.0;
        }
        for (int l = 0; l < 8192; ++l) {
            array[l] = l / 8192.0;
        }
        final SF2Sample simpleFFTSample = newSimpleFFTSample(sf2Soundbank, "Open Hi-Hat", array, 1000.0, 5);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Open Hi-Hat");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(36, 1500);
        sf2LayerRegion.putInteger(54, 1);
        sf2LayerRegion.putInteger(38, 1500);
        sf2LayerRegion.putInteger(37, 1000);
        sf2LayerRegion.putInteger(56, 0);
        sf2LayerRegion.putInteger(57, 1);
        sf2LayerRegion.setSample(simpleFFTSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_crash_cymbal(final SF2Soundbank sf2Soundbank) {
        final int n = 16384;
        final double[] array = new double[2 * n];
        final Random random = new Random(3049912L);
        for (int i = 0; i < array.length; i += 2) {
            array[i] = 2.0 * (random.nextDouble() - 0.5);
        }
        for (int j = n / 2; j < array.length; ++j) {
            array[j] = 0.0;
        }
        for (int k = 0; k < 100; ++k) {
            array[k] = 0.0;
        }
        for (int l = 0; l < 1024; ++l) {
            array[l] = l / 1024.0;
        }
        final SF2Sample simpleFFTSample = newSimpleFFTSample(sf2Soundbank, "Crash Cymbal", array, 1000.0, 5);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Crash Cymbal");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(36, 1800);
        sf2LayerRegion.putInteger(54, 1);
        sf2LayerRegion.putInteger(38, 1800);
        sf2LayerRegion.putInteger(37, 1000);
        sf2LayerRegion.putInteger(56, 0);
        sf2LayerRegion.setSample(simpleFFTSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Layer new_side_stick(final SF2Soundbank sf2Soundbank) {
        final int n = 16384;
        final double[] array = new double[2 * n];
        final Random random = new Random(3049912L);
        for (int i = 0; i < array.length; i += 2) {
            array[i] = 2.0 * (random.nextDouble() - 0.5) * 0.1;
        }
        fft(array);
        for (int j = n / 2; j < array.length; ++j) {
            array[j] = 0.0;
        }
        for (int k = 4096; k < 8192; ++k) {
            array[k] = 1.0 - (k - 4096) / 4096.0;
        }
        for (int l = 0; l < 200; ++l) {
            final double n2 = 1.0 - l / 200.0;
            final double[] array2 = array;
            final int n3 = l;
            array2[n3] *= 1.0 + 20.0 * n2 * n2;
        }
        for (int n4 = 0; n4 < 30; ++n4) {
            array[n4] = 0.0;
        }
        randomPhase(array, new Random(3049912L));
        ifft(array);
        normalize(array, 0.9);
        final double[] realPart = realPart(array);
        double n5 = 1.0;
        for (int n6 = 0; n6 < realPart.length; ++n6) {
            final double[] array3 = realPart;
            final int n7 = n6;
            array3[n7] *= n5;
            n5 *= 0.9996;
        }
        final double[] array4 = realPart;
        for (int n8 = 0; n8 < 10; ++n8) {
            final double[] array5 = array4;
            final int n9 = n8;
            array5[n9] *= n8 / 10.0;
        }
        final SF2Sample simpleDrumSample = newSimpleDrumSample(sf2Soundbank, "Side Stick", array4);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName("Side Stick");
        sf2Layer.setGlobalZone(new SF2GlobalRegion());
        sf2Soundbank.addResource(sf2Layer);
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.putInteger(38, 12000);
        sf2LayerRegion.putInteger(56, 0);
        sf2LayerRegion.putInteger(48, -50);
        sf2LayerRegion.setSample(simpleDrumSample);
        sf2Layer.getRegions().add(sf2LayerRegion);
        return sf2Layer;
    }
    
    public static SF2Sample newSimpleFFTSample(final SF2Soundbank sf2Soundbank, final String s, final double[] array, final double n) {
        return newSimpleFFTSample(sf2Soundbank, s, array, n, 10);
    }
    
    public static SF2Sample newSimpleFFTSample(final SF2Soundbank sf2Soundbank, final String name, double[] realPart, final double n, final int n2) {
        final int n3 = realPart.length / 2;
        final AudioFormat audioFormat = new AudioFormat(44100.0f, 16, 1, true, false);
        final double n4 = n / n3 * audioFormat.getSampleRate() * 0.5;
        randomPhase(realPart);
        ifft(realPart);
        realPart = realPart(realPart);
        normalize(realPart, 0.9);
        final float[] float1 = toFloat(realPart);
        final float[] loopExtend = loopExtend(float1, float1.length + 512);
        fadeUp(loopExtend, n2);
        final byte[] bytes = toBytes(loopExtend, audioFormat);
        final SF2Sample sf2Sample = new SF2Sample(sf2Soundbank);
        sf2Sample.setName(name);
        sf2Sample.setData(bytes);
        sf2Sample.setStartLoop(256L);
        sf2Sample.setEndLoop(n3 + 256);
        sf2Sample.setSampleRate((long)audioFormat.getSampleRate());
        final double n5 = 81.0 + 12.0 * Math.log(n4 / 440.0) / Math.log(2.0);
        sf2Sample.setOriginalPitch((int)n5);
        sf2Sample.setPitchCorrection((byte)(-(n5 - (int)n5) * 100.0));
        sf2Soundbank.addResource(sf2Sample);
        return sf2Sample;
    }
    
    public static SF2Sample newSimpleFFTSample_dist(final SF2Soundbank sf2Soundbank, final String name, double[] realPart, final double n, final double n2) {
        final int n3 = realPart.length / 2;
        final AudioFormat audioFormat = new AudioFormat(44100.0f, 16, 1, true, false);
        final double n4 = n / n3 * audioFormat.getSampleRate() * 0.5;
        randomPhase(realPart);
        ifft(realPart);
        realPart = realPart(realPart);
        for (int i = 0; i < realPart.length; ++i) {
            realPart[i] = (1.0 - Math.exp(-Math.abs(realPart[i] * n2))) * Math.signum(realPart[i]);
        }
        normalize(realPart, 0.9);
        final float[] float1 = toFloat(realPart);
        final float[] loopExtend = loopExtend(float1, float1.length + 512);
        fadeUp(loopExtend, 80);
        final byte[] bytes = toBytes(loopExtend, audioFormat);
        final SF2Sample sf2Sample = new SF2Sample(sf2Soundbank);
        sf2Sample.setName(name);
        sf2Sample.setData(bytes);
        sf2Sample.setStartLoop(256L);
        sf2Sample.setEndLoop(n3 + 256);
        sf2Sample.setSampleRate((long)audioFormat.getSampleRate());
        final double n5 = 81.0 + 12.0 * Math.log(n4 / 440.0) / Math.log(2.0);
        sf2Sample.setOriginalPitch((int)n5);
        sf2Sample.setPitchCorrection((byte)(-(n5 - (int)n5) * 100.0));
        sf2Soundbank.addResource(sf2Sample);
        return sf2Sample;
    }
    
    public static SF2Sample newSimpleDrumSample(final SF2Soundbank sf2Soundbank, final String name, final double[] array) {
        final int length = array.length;
        final AudioFormat audioFormat = new AudioFormat(44100.0f, 16, 1, true, false);
        final byte[] bytes = toBytes(toFloat(realPart(array)), audioFormat);
        final SF2Sample sf2Sample = new SF2Sample(sf2Soundbank);
        sf2Sample.setName(name);
        sf2Sample.setData(bytes);
        sf2Sample.setStartLoop(256L);
        sf2Sample.setEndLoop(length + 256);
        sf2Sample.setSampleRate((long)audioFormat.getSampleRate());
        sf2Sample.setOriginalPitch(60);
        sf2Soundbank.addResource(sf2Sample);
        return sf2Sample;
    }
    
    public static SF2Layer newLayer(final SF2Soundbank sf2Soundbank, final String name, final SF2Sample sample) {
        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
        sf2LayerRegion.setSample(sample);
        final SF2Layer sf2Layer = new SF2Layer(sf2Soundbank);
        sf2Layer.setName(name);
        sf2Layer.getRegions().add(sf2LayerRegion);
        sf2Soundbank.addResource(sf2Layer);
        return sf2Layer;
    }
    
    public static SF2Instrument newInstrument(final SF2Soundbank sf2Soundbank, final String name, final Patch patch, final SF2Layer... array) {
        final SF2Instrument sf2Instrument = new SF2Instrument(sf2Soundbank);
        sf2Instrument.setPatch(patch);
        sf2Instrument.setName(name);
        sf2Soundbank.addInstrument(sf2Instrument);
        for (int i = 0; i < array.length; ++i) {
            final SF2InstrumentRegion sf2InstrumentRegion = new SF2InstrumentRegion();
            sf2InstrumentRegion.setLayer(array[i]);
            sf2Instrument.getRegions().add(sf2InstrumentRegion);
        }
        return sf2Instrument;
    }
    
    public static void ifft(final double[] array) {
        new FFT(array.length / 2, 1).transform(array);
    }
    
    public static void fft(final double[] array) {
        new FFT(array.length / 2, -1).transform(array);
    }
    
    public static void complexGaussianDist(final double[] array, final double n, final double n2, final double n3) {
        for (int i = 0; i < array.length / 4; ++i) {
            final int n4 = i * 2;
            array[n4] += n3 * (1.0 / (n2 * Math.sqrt(6.283185307179586)) * Math.exp(-0.5 * Math.pow((i - n) / n2, 2.0)));
        }
    }
    
    public static void randomPhase(final double[] array) {
        for (int i = 0; i < array.length; i += 2) {
            final double n = Math.random() * 2.0 * 3.141592653589793;
            final double n2 = array[i];
            array[i] = Math.sin(n) * n2;
            array[i + 1] = Math.cos(n) * n2;
        }
    }
    
    public static void randomPhase(final double[] array, final Random random) {
        for (int i = 0; i < array.length; i += 2) {
            final double n = random.nextDouble() * 2.0 * 3.141592653589793;
            final double n2 = array[i];
            array[i] = Math.sin(n) * n2;
            array[i + 1] = Math.cos(n) * n2;
        }
    }
    
    public static void normalize(final double[] array, final double n) {
        double n2 = 0.0;
        for (int i = 0; i < array.length; ++i) {
            if (array[i] > n2) {
                n2 = array[i];
            }
            if (-array[i] > n2) {
                n2 = -array[i];
            }
        }
        if (n2 == 0.0) {
            return;
        }
        final double n3 = n / n2;
        for (int j = 0; j < array.length; ++j) {
            final int n4 = j;
            array[n4] *= n3;
        }
    }
    
    public static void normalize(final float[] array, final double n) {
        double n2 = 0.5;
        for (int i = 0; i < array.length; ++i) {
            if (array[i * 2] > n2) {
                n2 = array[i * 2];
            }
            if (-array[i * 2] > n2) {
                n2 = -array[i * 2];
            }
        }
        final double n3 = n / n2;
        for (int j = 0; j < array.length; ++j) {
            final int n4 = j * 2;
            array[n4] *= (float)n3;
        }
    }
    
    public static double[] realPart(final double[] array) {
        final double[] array2 = new double[array.length / 2];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[i * 2];
        }
        return array2;
    }
    
    public static double[] imgPart(final double[] array) {
        final double[] array2 = new double[array.length / 2];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[i * 2];
        }
        return array2;
    }
    
    public static float[] toFloat(final double[] array) {
        final float[] array2 = new float[array.length];
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = (float)array[i];
        }
        return array2;
    }
    
    public static byte[] toBytes(final float[] array, final AudioFormat audioFormat) {
        return AudioFloatConverter.getConverter(audioFormat).toByteArray(array, new byte[array.length * audioFormat.getFrameSize()]);
    }
    
    public static void fadeUp(final double[] array, final int n) {
        final double n2 = n;
        for (int i = 0; i < n; ++i) {
            final int n3 = i;
            array[n3] *= i / n2;
        }
    }
    
    public static void fadeUp(final float[] array, final int n) {
        final double n2 = n;
        for (int i = 0; i < n; ++i) {
            final int n3 = i;
            array[n3] *= (float)(i / n2);
        }
    }
    
    public static double[] loopExtend(final double[] array, final int n) {
        final double[] array2 = new double[n];
        final int length = array.length;
        int n2 = 0;
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[n2];
            if (++n2 == length) {
                n2 = 0;
            }
        }
        return array2;
    }
    
    public static float[] loopExtend(final float[] array, final int n) {
        final float[] array2 = new float[n];
        final int length = array.length;
        int n2 = 0;
        for (int i = 0; i < array2.length; ++i) {
            array2[i] = array[n2];
            if (++n2 == length) {
                n2 = 0;
            }
        }
        return array2;
    }
    
    static {
        general_midi_instruments = new String[] { "Acoustic Grand Piano", "Bright Acoustic Piano", "Electric Grand Piano", "Honky-tonk Piano", "Electric Piano 1", "Electric Piano 2", "Harpsichord", "Clavi", "Celesta", "Glockenspiel", "Music Box", "Vibraphone", "Marimba", "Xylophone", "Tubular Bells", "Dulcimer", "Drawbar Organ", "Percussive Organ", "Rock Organ", "Church Organ", "Reed Organ", "Accordion", "Harmonica", "Tango Accordion", "Acoustic Guitar (nylon)", "Acoustic Guitar (steel)", "Electric Guitar (jazz)", "Electric Guitar (clean)", "Electric Guitar (muted)", "Overdriven Guitar", "Distortion Guitar", "Guitar harmonics", "Acoustic Bass", "Electric Bass (finger)", "Electric Bass (pick)", "Fretless Bass", "Slap Bass 1", "Slap Bass 2", "Synth Bass 1", "Synth Bass 2", "Violin", "Viola", "Cello", "Contrabass", "Tremolo Strings", "Pizzicato Strings", "Orchestral Harp", "Timpani", "String Ensemble 1", "String Ensemble 2", "SynthStrings 1", "SynthStrings 2", "Choir Aahs", "Voice Oohs", "Synth Voice", "Orchestra Hit", "Trumpet", "Trombone", "Tuba", "Muted Trumpet", "French Horn", "Brass Section", "SynthBrass 1", "SynthBrass 2", "Soprano Sax", "Alto Sax", "Tenor Sax", "Baritone Sax", "Oboe", "English Horn", "Bassoon", "Clarinet", "Piccolo", "Flute", "Recorder", "Pan Flute", "Blown Bottle", "Shakuhachi", "Whistle", "Ocarina", "Lead 1 (square)", "Lead 2 (sawtooth)", "Lead 3 (calliope)", "Lead 4 (chiff)", "Lead 5 (charang)", "Lead 6 (voice)", "Lead 7 (fifths)", "Lead 8 (bass + lead)", "Pad 1 (new age)", "Pad 2 (warm)", "Pad 3 (polysynth)", "Pad 4 (choir)", "Pad 5 (bowed)", "Pad 6 (metallic)", "Pad 7 (halo)", "Pad 8 (sweep)", "FX 1 (rain)", "FX 2 (soundtrack)", "FX 3 (crystal)", "FX 4 (atmosphere)", "FX 5 (brightness)", "FX 6 (goblins)", "FX 7 (echoes)", "FX 8 (sci-fi)", "Sitar", "Banjo", "Shamisen", "Koto", "Kalimba", "Bag pipe", "Fiddle", "Shanai", "Tinkle Bell", "Agogo", "Steel Drums", "Woodblock", "Taiko Drum", "Melodic Tom", "Synth Drum", "Reverse Cymbal", "Guitar Fret Noise", "Breath Noise", "Seashore", "Bird Tweet", "Telephone Ring", "Helicopter", "Applause", "Gunshot" };
    }
}
