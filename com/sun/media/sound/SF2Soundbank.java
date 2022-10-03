package com.sun.media.sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import java.util.Comparator;
import java.util.Arrays;
import javax.sound.midi.SoundbankResource;
import java.util.Map;
import java.io.OutputStream;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.io.File;
import javax.sound.midi.Soundbank;

public final class SF2Soundbank implements Soundbank
{
    int major;
    int minor;
    String targetEngine;
    String name;
    String romName;
    int romVersionMajor;
    int romVersionMinor;
    String creationDate;
    String engineers;
    String product;
    String copyright;
    String comments;
    String tools;
    private ModelByteBuffer sampleData;
    private ModelByteBuffer sampleData24;
    private File sampleFile;
    private boolean largeFormat;
    private final List<SF2Instrument> instruments;
    private final List<SF2Layer> layers;
    private final List<SF2Sample> samples;
    
    public SF2Soundbank() {
        this.major = 2;
        this.minor = 1;
        this.targetEngine = "EMU8000";
        this.name = "untitled";
        this.romName = null;
        this.romVersionMajor = -1;
        this.romVersionMinor = -1;
        this.creationDate = null;
        this.engineers = null;
        this.product = null;
        this.copyright = null;
        this.comments = null;
        this.tools = null;
        this.sampleData = null;
        this.sampleData24 = null;
        this.sampleFile = null;
        this.largeFormat = false;
        this.instruments = new ArrayList<SF2Instrument>();
        this.layers = new ArrayList<SF2Layer>();
        this.samples = new ArrayList<SF2Sample>();
    }
    
    public SF2Soundbank(final URL url) throws IOException {
        this.major = 2;
        this.minor = 1;
        this.targetEngine = "EMU8000";
        this.name = "untitled";
        this.romName = null;
        this.romVersionMajor = -1;
        this.romVersionMinor = -1;
        this.creationDate = null;
        this.engineers = null;
        this.product = null;
        this.copyright = null;
        this.comments = null;
        this.tools = null;
        this.sampleData = null;
        this.sampleData24 = null;
        this.sampleFile = null;
        this.largeFormat = false;
        this.instruments = new ArrayList<SF2Instrument>();
        this.layers = new ArrayList<SF2Layer>();
        this.samples = new ArrayList<SF2Sample>();
        final InputStream openStream = url.openStream();
        try {
            this.readSoundbank(openStream);
        }
        finally {
            openStream.close();
        }
    }
    
    public SF2Soundbank(final File sampleFile) throws IOException {
        this.major = 2;
        this.minor = 1;
        this.targetEngine = "EMU8000";
        this.name = "untitled";
        this.romName = null;
        this.romVersionMajor = -1;
        this.romVersionMinor = -1;
        this.creationDate = null;
        this.engineers = null;
        this.product = null;
        this.copyright = null;
        this.comments = null;
        this.tools = null;
        this.sampleData = null;
        this.sampleData24 = null;
        this.sampleFile = null;
        this.largeFormat = false;
        this.instruments = new ArrayList<SF2Instrument>();
        this.layers = new ArrayList<SF2Layer>();
        this.samples = new ArrayList<SF2Sample>();
        this.largeFormat = true;
        this.sampleFile = sampleFile;
        final FileInputStream fileInputStream = new FileInputStream(sampleFile);
        try {
            this.readSoundbank(fileInputStream);
        }
        finally {
            fileInputStream.close();
        }
    }
    
    public SF2Soundbank(final InputStream inputStream) throws IOException {
        this.major = 2;
        this.minor = 1;
        this.targetEngine = "EMU8000";
        this.name = "untitled";
        this.romName = null;
        this.romVersionMajor = -1;
        this.romVersionMinor = -1;
        this.creationDate = null;
        this.engineers = null;
        this.product = null;
        this.copyright = null;
        this.comments = null;
        this.tools = null;
        this.sampleData = null;
        this.sampleData24 = null;
        this.sampleFile = null;
        this.largeFormat = false;
        this.instruments = new ArrayList<SF2Instrument>();
        this.layers = new ArrayList<SF2Layer>();
        this.samples = new ArrayList<SF2Sample>();
        this.readSoundbank(inputStream);
    }
    
    private void readSoundbank(final InputStream inputStream) throws IOException {
        final RIFFReader riffReader = new RIFFReader(inputStream);
        if (!riffReader.getFormat().equals("RIFF")) {
            throw new RIFFInvalidFormatException("Input stream is not a valid RIFF stream!");
        }
        if (!riffReader.getType().equals("sfbk")) {
            throw new RIFFInvalidFormatException("Input stream is not a valid SoundFont!");
        }
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            if (nextChunk.getFormat().equals("LIST")) {
                if (nextChunk.getType().equals("INFO")) {
                    this.readInfoChunk(nextChunk);
                }
                if (nextChunk.getType().equals("sdta")) {
                    this.readSdtaChunk(nextChunk);
                }
                if (!nextChunk.getType().equals("pdta")) {
                    continue;
                }
                this.readPdtaChunk(nextChunk);
            }
        }
    }
    
    private void readInfoChunk(final RIFFReader riffReader) throws IOException {
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            final String format = nextChunk.getFormat();
            if (format.equals("ifil")) {
                this.major = nextChunk.readUnsignedShort();
                this.minor = nextChunk.readUnsignedShort();
            }
            else if (format.equals("isng")) {
                this.targetEngine = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("INAM")) {
                this.name = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("irom")) {
                this.romName = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("iver")) {
                this.romVersionMajor = nextChunk.readUnsignedShort();
                this.romVersionMinor = nextChunk.readUnsignedShort();
            }
            else if (format.equals("ICRD")) {
                this.creationDate = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IENG")) {
                this.engineers = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IPRD")) {
                this.product = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICOP")) {
                this.copyright = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICMT")) {
                this.comments = nextChunk.readString(nextChunk.available());
            }
            else {
                if (!format.equals("ISFT")) {
                    continue;
                }
                this.tools = nextChunk.readString(nextChunk.available());
            }
        }
    }
    
    private void readSdtaChunk(final RIFFReader riffReader) throws IOException {
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            if (nextChunk.getFormat().equals("smpl")) {
                if (!this.largeFormat) {
                    final byte[] array = new byte[nextChunk.available()];
                    int i = 0;
                    final int available = nextChunk.available();
                    while (i != available) {
                        if (available - i > 65536) {
                            nextChunk.readFully(array, i, 65536);
                            i += 65536;
                        }
                        else {
                            nextChunk.readFully(array, i, available - i);
                            i = available;
                        }
                    }
                    this.sampleData = new ModelByteBuffer(array);
                }
                else {
                    this.sampleData = new ModelByteBuffer(this.sampleFile, nextChunk.getFilePointer(), nextChunk.available());
                }
            }
            if (nextChunk.getFormat().equals("sm24")) {
                if (!this.largeFormat) {
                    final byte[] array2 = new byte[nextChunk.available()];
                    int j = 0;
                    final int available2 = nextChunk.available();
                    while (j != available2) {
                        if (available2 - j > 65536) {
                            nextChunk.readFully(array2, j, 65536);
                            j += 65536;
                        }
                        else {
                            nextChunk.readFully(array2, j, available2 - j);
                            j = available2;
                        }
                    }
                    this.sampleData24 = new ModelByteBuffer(array2);
                }
                else {
                    this.sampleData24 = new ModelByteBuffer(this.sampleFile, nextChunk.getFilePointer(), nextChunk.available());
                }
            }
        }
    }
    
    private void readPdtaChunk(final RIFFReader riffReader) throws IOException {
        final ArrayList list = new ArrayList();
        final ArrayList list2 = new ArrayList();
        final ArrayList list3 = new ArrayList();
        final ArrayList list4 = new ArrayList();
        final ArrayList list5 = new ArrayList();
        final ArrayList list6 = new ArrayList();
        final ArrayList list7 = new ArrayList();
        final ArrayList list8 = new ArrayList();
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            final String format = nextChunk.getFormat();
            if (format.equals("phdr")) {
                if (nextChunk.available() % 38 != 0) {
                    throw new RIFFInvalidDataException();
                }
                for (int n = nextChunk.available() / 38, i = 0; i < n; ++i) {
                    final SF2Instrument sf2Instrument = new SF2Instrument(this);
                    sf2Instrument.name = nextChunk.readString(20);
                    sf2Instrument.preset = nextChunk.readUnsignedShort();
                    sf2Instrument.bank = nextChunk.readUnsignedShort();
                    list2.add(nextChunk.readUnsignedShort());
                    sf2Instrument.library = nextChunk.readUnsignedInt();
                    sf2Instrument.genre = nextChunk.readUnsignedInt();
                    sf2Instrument.morphology = nextChunk.readUnsignedInt();
                    list.add(sf2Instrument);
                    if (i != n - 1) {
                        this.instruments.add(sf2Instrument);
                    }
                }
            }
            else if (format.equals("pbag")) {
                if (nextChunk.available() % 4 != 0) {
                    throw new RIFFInvalidDataException();
                }
                int n2 = nextChunk.available() / 4;
                final int unsignedShort = nextChunk.readUnsignedShort();
                final int unsignedShort2 = nextChunk.readUnsignedShort();
                while (list3.size() < unsignedShort) {
                    list3.add(null);
                }
                while (list4.size() < unsignedShort2) {
                    list4.add(null);
                }
                --n2;
                if (list2.isEmpty()) {
                    throw new RIFFInvalidDataException();
                }
                for (int intValue = (int)list2.get(0), j = 0; j < intValue; ++j) {
                    if (n2 == 0) {
                        throw new RIFFInvalidDataException();
                    }
                    final int unsignedShort3 = nextChunk.readUnsignedShort();
                    final int unsignedShort4 = nextChunk.readUnsignedShort();
                    while (list3.size() < unsignedShort3) {
                        list3.add(null);
                    }
                    while (list4.size() < unsignedShort4) {
                        list4.add(null);
                    }
                    --n2;
                }
                for (int k = 0; k < list2.size() - 1; ++k) {
                    final int n3 = (int)list2.get(k + 1) - (int)list2.get(k);
                    final SF2Instrument sf2Instrument2 = (SF2Instrument)list.get(k);
                    for (int l = 0; l < n3; ++l) {
                        if (n2 == 0) {
                            throw new RIFFInvalidDataException();
                        }
                        final int unsignedShort5 = nextChunk.readUnsignedShort();
                        final int unsignedShort6 = nextChunk.readUnsignedShort();
                        final SF2InstrumentRegion sf2InstrumentRegion = new SF2InstrumentRegion();
                        sf2Instrument2.regions.add(sf2InstrumentRegion);
                        while (list3.size() < unsignedShort5) {
                            list3.add(sf2InstrumentRegion);
                        }
                        while (list4.size() < unsignedShort6) {
                            list4.add(sf2InstrumentRegion);
                        }
                        --n2;
                    }
                }
            }
            else if (format.equals("pmod")) {
                for (int n4 = 0; n4 < list4.size(); ++n4) {
                    final SF2Modulator sf2Modulator = new SF2Modulator();
                    sf2Modulator.sourceOperator = nextChunk.readUnsignedShort();
                    sf2Modulator.destinationOperator = nextChunk.readUnsignedShort();
                    sf2Modulator.amount = nextChunk.readShort();
                    sf2Modulator.amountSourceOperator = nextChunk.readUnsignedShort();
                    sf2Modulator.transportOperator = nextChunk.readUnsignedShort();
                    final SF2InstrumentRegion sf2InstrumentRegion2 = (SF2InstrumentRegion)list4.get(n4);
                    if (sf2InstrumentRegion2 != null) {
                        sf2InstrumentRegion2.modulators.add(sf2Modulator);
                    }
                }
            }
            else if (format.equals("pgen")) {
                for (int n5 = 0; n5 < list3.size(); ++n5) {
                    final int unsignedShort7 = nextChunk.readUnsignedShort();
                    final short short1 = nextChunk.readShort();
                    final SF2InstrumentRegion sf2InstrumentRegion3 = (SF2InstrumentRegion)list3.get(n5);
                    if (sf2InstrumentRegion3 != null) {
                        sf2InstrumentRegion3.generators.put(unsignedShort7, short1);
                    }
                }
            }
            else if (format.equals("inst")) {
                if (nextChunk.available() % 22 != 0) {
                    throw new RIFFInvalidDataException();
                }
                for (int n6 = nextChunk.available() / 22, n7 = 0; n7 < n6; ++n7) {
                    final SF2Layer sf2Layer = new SF2Layer(this);
                    sf2Layer.name = nextChunk.readString(20);
                    list6.add(nextChunk.readUnsignedShort());
                    list5.add(sf2Layer);
                    if (n7 != n6 - 1) {
                        this.layers.add(sf2Layer);
                    }
                }
            }
            else if (format.equals("ibag")) {
                if (nextChunk.available() % 4 != 0) {
                    throw new RIFFInvalidDataException();
                }
                int n8 = nextChunk.available() / 4;
                final int unsignedShort8 = nextChunk.readUnsignedShort();
                final int unsignedShort9 = nextChunk.readUnsignedShort();
                while (list7.size() < unsignedShort8) {
                    list7.add(null);
                }
                while (list8.size() < unsignedShort9) {
                    list8.add(null);
                }
                --n8;
                if (list6.isEmpty()) {
                    throw new RIFFInvalidDataException();
                }
                for (int intValue2 = (int)list6.get(0), n9 = 0; n9 < intValue2; ++n9) {
                    if (n8 == 0) {
                        throw new RIFFInvalidDataException();
                    }
                    final int unsignedShort10 = nextChunk.readUnsignedShort();
                    final int unsignedShort11 = nextChunk.readUnsignedShort();
                    while (list7.size() < unsignedShort10) {
                        list7.add(null);
                    }
                    while (list8.size() < unsignedShort11) {
                        list8.add(null);
                    }
                    --n8;
                }
                for (int n10 = 0; n10 < list6.size() - 1; ++n10) {
                    final int n11 = (int)list6.get(n10 + 1) - (int)list6.get(n10);
                    final SF2Layer sf2Layer2 = this.layers.get(n10);
                    for (int n12 = 0; n12 < n11; ++n12) {
                        if (n8 == 0) {
                            throw new RIFFInvalidDataException();
                        }
                        final int unsignedShort12 = nextChunk.readUnsignedShort();
                        final int unsignedShort13 = nextChunk.readUnsignedShort();
                        final SF2LayerRegion sf2LayerRegion = new SF2LayerRegion();
                        sf2Layer2.regions.add(sf2LayerRegion);
                        while (list7.size() < unsignedShort12) {
                            list7.add(sf2LayerRegion);
                        }
                        while (list8.size() < unsignedShort13) {
                            list8.add(sf2LayerRegion);
                        }
                        --n8;
                    }
                }
            }
            else if (format.equals("imod")) {
                for (int n13 = 0; n13 < list8.size(); ++n13) {
                    final SF2Modulator sf2Modulator2 = new SF2Modulator();
                    sf2Modulator2.sourceOperator = nextChunk.readUnsignedShort();
                    sf2Modulator2.destinationOperator = nextChunk.readUnsignedShort();
                    sf2Modulator2.amount = nextChunk.readShort();
                    sf2Modulator2.amountSourceOperator = nextChunk.readUnsignedShort();
                    sf2Modulator2.transportOperator = nextChunk.readUnsignedShort();
                    if (n13 < 0 || n13 >= list7.size()) {
                        throw new RIFFInvalidDataException();
                    }
                    final SF2LayerRegion sf2LayerRegion2 = (SF2LayerRegion)list7.get(n13);
                    if (sf2LayerRegion2 != null) {
                        sf2LayerRegion2.modulators.add(sf2Modulator2);
                    }
                }
            }
            else if (format.equals("igen")) {
                for (int n14 = 0; n14 < list7.size(); ++n14) {
                    final int unsignedShort14 = nextChunk.readUnsignedShort();
                    final short short2 = nextChunk.readShort();
                    final SF2LayerRegion sf2LayerRegion3 = (SF2LayerRegion)list7.get(n14);
                    if (sf2LayerRegion3 != null) {
                        sf2LayerRegion3.generators.put(unsignedShort14, short2);
                    }
                }
            }
            else {
                if (!format.equals("shdr")) {
                    continue;
                }
                if (nextChunk.available() % 46 != 0) {
                    throw new RIFFInvalidDataException();
                }
                for (int n15 = nextChunk.available() / 46, n16 = 0; n16 < n15; ++n16) {
                    final SF2Sample sf2Sample = new SF2Sample(this);
                    sf2Sample.name = nextChunk.readString(20);
                    final long unsignedInt = nextChunk.readUnsignedInt();
                    final long unsignedInt2 = nextChunk.readUnsignedInt();
                    if (this.sampleData != null) {
                        sf2Sample.data = this.sampleData.subbuffer(unsignedInt * 2L, unsignedInt2 * 2L, true);
                    }
                    if (this.sampleData24 != null) {
                        sf2Sample.data24 = this.sampleData24.subbuffer(unsignedInt, unsignedInt2, true);
                    }
                    sf2Sample.startLoop = nextChunk.readUnsignedInt() - unsignedInt;
                    sf2Sample.endLoop = nextChunk.readUnsignedInt() - unsignedInt;
                    if (sf2Sample.startLoop < 0L) {
                        sf2Sample.startLoop = -1L;
                    }
                    if (sf2Sample.endLoop < 0L) {
                        sf2Sample.endLoop = -1L;
                    }
                    sf2Sample.sampleRate = nextChunk.readUnsignedInt();
                    sf2Sample.originalPitch = nextChunk.readUnsignedByte();
                    sf2Sample.pitchCorrection = nextChunk.readByte();
                    sf2Sample.sampleLink = nextChunk.readUnsignedShort();
                    sf2Sample.sampleType = nextChunk.readUnsignedShort();
                    if (n16 != n15 - 1) {
                        this.samples.add(sf2Sample);
                    }
                }
            }
        }
        for (final SF2Layer sf2Layer3 : this.layers) {
            final Iterator<SF2LayerRegion> iterator2 = sf2Layer3.regions.iterator();
            SF2Region sf2Region = null;
            while (iterator2.hasNext()) {
                final SF2LayerRegion sf2LayerRegion4 = iterator2.next();
                if (sf2LayerRegion4.generators.get(53) != null) {
                    final short shortValue = sf2LayerRegion4.generators.get(53);
                    sf2LayerRegion4.generators.remove(53);
                    if (shortValue < 0 || shortValue >= this.samples.size()) {
                        throw new RIFFInvalidDataException();
                    }
                    sf2LayerRegion4.sample = this.samples.get(shortValue);
                }
                else {
                    sf2Region = sf2LayerRegion4;
                }
            }
            if (sf2Region != null) {
                sf2Layer3.getRegions().remove(sf2Region);
                final SF2GlobalRegion globalZone = new SF2GlobalRegion();
                globalZone.generators = sf2Region.generators;
                globalZone.modulators = sf2Region.modulators;
                sf2Layer3.setGlobalZone(globalZone);
            }
        }
        for (final SF2Instrument sf2Instrument3 : this.instruments) {
            final Iterator<SF2InstrumentRegion> iterator4 = sf2Instrument3.regions.iterator();
            SF2Region sf2Region2 = null;
            while (iterator4.hasNext()) {
                final SF2InstrumentRegion sf2InstrumentRegion4 = iterator4.next();
                if (sf2InstrumentRegion4.generators.get(41) != null) {
                    final short shortValue2 = sf2InstrumentRegion4.generators.get(41);
                    sf2InstrumentRegion4.generators.remove(41);
                    if (shortValue2 < 0 || shortValue2 >= this.layers.size()) {
                        throw new RIFFInvalidDataException();
                    }
                    sf2InstrumentRegion4.layer = this.layers.get(shortValue2);
                }
                else {
                    sf2Region2 = sf2InstrumentRegion4;
                }
            }
            if (sf2Region2 != null) {
                sf2Instrument3.getRegions().remove(sf2Region2);
                final SF2GlobalRegion globalZone2 = new SF2GlobalRegion();
                globalZone2.generators = sf2Region2.generators;
                globalZone2.modulators = sf2Region2.modulators;
                sf2Instrument3.setGlobalZone(globalZone2);
            }
        }
    }
    
    public void save(final String s) throws IOException {
        this.writeSoundbank(new RIFFWriter(s, "sfbk"));
    }
    
    public void save(final File file) throws IOException {
        this.writeSoundbank(new RIFFWriter(file, "sfbk"));
    }
    
    public void save(final OutputStream outputStream) throws IOException {
        this.writeSoundbank(new RIFFWriter(outputStream, "sfbk"));
    }
    
    private void writeSoundbank(final RIFFWriter riffWriter) throws IOException {
        this.writeInfo(riffWriter.writeList("INFO"));
        this.writeSdtaChunk(riffWriter.writeList("sdta"));
        this.writePdtaChunk(riffWriter.writeList("pdta"));
        riffWriter.close();
    }
    
    private void writeInfoStringChunk(final RIFFWriter riffWriter, final String s, final String s2) throws IOException {
        if (s2 == null) {
            return;
        }
        final RIFFWriter writeChunk = riffWriter.writeChunk(s);
        writeChunk.writeString(s2);
        int length = s2.getBytes("ascii").length;
        writeChunk.write(0);
        if (++length % 2 != 0) {
            writeChunk.write(0);
        }
    }
    
    private void writeInfo(final RIFFWriter riffWriter) throws IOException {
        if (this.targetEngine == null) {
            this.targetEngine = "EMU8000";
        }
        if (this.name == null) {
            this.name = "";
        }
        final RIFFWriter writeChunk = riffWriter.writeChunk("ifil");
        writeChunk.writeUnsignedShort(this.major);
        writeChunk.writeUnsignedShort(this.minor);
        this.writeInfoStringChunk(riffWriter, "isng", this.targetEngine);
        this.writeInfoStringChunk(riffWriter, "INAM", this.name);
        this.writeInfoStringChunk(riffWriter, "irom", this.romName);
        if (this.romVersionMajor != -1) {
            final RIFFWriter writeChunk2 = riffWriter.writeChunk("iver");
            writeChunk2.writeUnsignedShort(this.romVersionMajor);
            writeChunk2.writeUnsignedShort(this.romVersionMinor);
        }
        this.writeInfoStringChunk(riffWriter, "ICRD", this.creationDate);
        this.writeInfoStringChunk(riffWriter, "IENG", this.engineers);
        this.writeInfoStringChunk(riffWriter, "IPRD", this.product);
        this.writeInfoStringChunk(riffWriter, "ICOP", this.copyright);
        this.writeInfoStringChunk(riffWriter, "ICMT", this.comments);
        this.writeInfoStringChunk(riffWriter, "ISFT", this.tools);
        riffWriter.close();
    }
    
    private void writeSdtaChunk(final RIFFWriter riffWriter) throws IOException {
        final byte[] array = new byte[32];
        final RIFFWriter writeChunk = riffWriter.writeChunk("smpl");
        final Iterator<SF2Sample> iterator = this.samples.iterator();
        while (iterator.hasNext()) {
            iterator.next().getDataBuffer().writeTo(writeChunk);
            writeChunk.write(array);
            writeChunk.write(array);
        }
        if (this.major < 2) {
            return;
        }
        if (this.major == 2 && this.minor < 4) {
            return;
        }
        final Iterator<SF2Sample> iterator2 = this.samples.iterator();
        while (iterator2.hasNext()) {
            if (iterator2.next().getData24Buffer() == null) {
                return;
            }
        }
        final RIFFWriter writeChunk2 = riffWriter.writeChunk("sm24");
        final Iterator<SF2Sample> iterator3 = this.samples.iterator();
        while (iterator3.hasNext()) {
            iterator3.next().getData24Buffer().writeTo(writeChunk2);
            writeChunk.write(array);
        }
    }
    
    private void writeModulators(final RIFFWriter riffWriter, final List<SF2Modulator> list) throws IOException {
        for (final SF2Modulator sf2Modulator : list) {
            riffWriter.writeUnsignedShort(sf2Modulator.sourceOperator);
            riffWriter.writeUnsignedShort(sf2Modulator.destinationOperator);
            riffWriter.writeShort(sf2Modulator.amount);
            riffWriter.writeUnsignedShort(sf2Modulator.amountSourceOperator);
            riffWriter.writeUnsignedShort(sf2Modulator.transportOperator);
        }
    }
    
    private void writeGenerators(final RIFFWriter riffWriter, final Map<Integer, Short> map) throws IOException {
        final Short n = map.get(43);
        final Short n2 = map.get(44);
        if (n != null) {
            riffWriter.writeUnsignedShort(43);
            riffWriter.writeShort(n);
        }
        if (n2 != null) {
            riffWriter.writeUnsignedShort(44);
            riffWriter.writeShort(n2);
        }
        for (final Map.Entry entry : map.entrySet()) {
            if ((int)entry.getKey() == 43) {
                continue;
            }
            if ((int)entry.getKey() == 44) {
                continue;
            }
            riffWriter.writeUnsignedShort((int)entry.getKey());
            riffWriter.writeShort((short)entry.getValue());
        }
    }
    
    private void writePdtaChunk(final RIFFWriter riffWriter) throws IOException {
        final RIFFWriter writeChunk = riffWriter.writeChunk("phdr");
        int n = 0;
        for (final SF2Instrument sf2Instrument : this.instruments) {
            writeChunk.writeString(sf2Instrument.name, 20);
            writeChunk.writeUnsignedShort(sf2Instrument.preset);
            writeChunk.writeUnsignedShort(sf2Instrument.bank);
            writeChunk.writeUnsignedShort(n);
            if (sf2Instrument.getGlobalRegion() != null) {
                ++n;
            }
            n += sf2Instrument.getRegions().size();
            writeChunk.writeUnsignedInt(sf2Instrument.library);
            writeChunk.writeUnsignedInt(sf2Instrument.genre);
            writeChunk.writeUnsignedInt(sf2Instrument.morphology);
        }
        writeChunk.writeString("EOP", 20);
        writeChunk.writeUnsignedShort(0);
        writeChunk.writeUnsignedShort(0);
        writeChunk.writeUnsignedShort(n);
        writeChunk.writeUnsignedInt(0L);
        writeChunk.writeUnsignedInt(0L);
        writeChunk.writeUnsignedInt(0L);
        final RIFFWriter writeChunk2 = riffWriter.writeChunk("pbag");
        int n2 = 0;
        int n3 = 0;
        for (final SF2Instrument sf2Instrument2 : this.instruments) {
            if (sf2Instrument2.getGlobalRegion() != null) {
                writeChunk2.writeUnsignedShort(n2);
                writeChunk2.writeUnsignedShort(n3);
                n2 += sf2Instrument2.getGlobalRegion().getGenerators().size();
                n3 += sf2Instrument2.getGlobalRegion().getModulators().size();
            }
            for (final SF2InstrumentRegion sf2InstrumentRegion : sf2Instrument2.getRegions()) {
                writeChunk2.writeUnsignedShort(n2);
                writeChunk2.writeUnsignedShort(n3);
                if (this.layers.indexOf(sf2InstrumentRegion.layer) != -1) {
                    ++n2;
                }
                n2 += sf2InstrumentRegion.getGenerators().size();
                n3 += sf2InstrumentRegion.getModulators().size();
            }
        }
        writeChunk2.writeUnsignedShort(n2);
        writeChunk2.writeUnsignedShort(n3);
        final RIFFWriter writeChunk3 = riffWriter.writeChunk("pmod");
        for (final SF2Instrument sf2Instrument3 : this.instruments) {
            if (sf2Instrument3.getGlobalRegion() != null) {
                this.writeModulators(writeChunk3, sf2Instrument3.getGlobalRegion().getModulators());
            }
            final Iterator<SF2InstrumentRegion> iterator5 = sf2Instrument3.getRegions().iterator();
            while (iterator5.hasNext()) {
                this.writeModulators(writeChunk3, iterator5.next().getModulators());
            }
        }
        writeChunk3.write(new byte[10]);
        final RIFFWriter writeChunk4 = riffWriter.writeChunk("pgen");
        for (final SF2Instrument sf2Instrument4 : this.instruments) {
            if (sf2Instrument4.getGlobalRegion() != null) {
                this.writeGenerators(writeChunk4, sf2Instrument4.getGlobalRegion().getGenerators());
            }
            for (final SF2InstrumentRegion sf2InstrumentRegion2 : sf2Instrument4.getRegions()) {
                this.writeGenerators(writeChunk4, sf2InstrumentRegion2.getGenerators());
                final int index = this.layers.indexOf(sf2InstrumentRegion2.layer);
                if (index != -1) {
                    writeChunk4.writeUnsignedShort(41);
                    writeChunk4.writeShort((short)index);
                }
            }
        }
        writeChunk4.write(new byte[4]);
        final RIFFWriter writeChunk5 = riffWriter.writeChunk("inst");
        int n4 = 0;
        for (final SF2Layer sf2Layer : this.layers) {
            writeChunk5.writeString(sf2Layer.name, 20);
            writeChunk5.writeUnsignedShort(n4);
            if (sf2Layer.getGlobalRegion() != null) {
                ++n4;
            }
            n4 += sf2Layer.getRegions().size();
        }
        writeChunk5.writeString("EOI", 20);
        writeChunk5.writeUnsignedShort(n4);
        final RIFFWriter writeChunk6 = riffWriter.writeChunk("ibag");
        int n5 = 0;
        int n6 = 0;
        for (final SF2Layer sf2Layer2 : this.layers) {
            if (sf2Layer2.getGlobalRegion() != null) {
                writeChunk6.writeUnsignedShort(n5);
                writeChunk6.writeUnsignedShort(n6);
                n5 += sf2Layer2.getGlobalRegion().getGenerators().size();
                n6 += sf2Layer2.getGlobalRegion().getModulators().size();
            }
            for (final SF2LayerRegion sf2LayerRegion : sf2Layer2.getRegions()) {
                writeChunk6.writeUnsignedShort(n5);
                writeChunk6.writeUnsignedShort(n6);
                if (this.samples.indexOf(sf2LayerRegion.sample) != -1) {
                    ++n5;
                }
                n5 += sf2LayerRegion.getGenerators().size();
                n6 += sf2LayerRegion.getModulators().size();
            }
        }
        writeChunk6.writeUnsignedShort(n5);
        writeChunk6.writeUnsignedShort(n6);
        final RIFFWriter writeChunk7 = riffWriter.writeChunk("imod");
        for (final SF2Layer sf2Layer3 : this.layers) {
            if (sf2Layer3.getGlobalRegion() != null) {
                this.writeModulators(writeChunk7, sf2Layer3.getGlobalRegion().getModulators());
            }
            final Iterator<SF2LayerRegion> iterator12 = sf2Layer3.getRegions().iterator();
            while (iterator12.hasNext()) {
                this.writeModulators(writeChunk7, iterator12.next().getModulators());
            }
        }
        writeChunk7.write(new byte[10]);
        final RIFFWriter writeChunk8 = riffWriter.writeChunk("igen");
        for (final SF2Layer sf2Layer4 : this.layers) {
            if (sf2Layer4.getGlobalRegion() != null) {
                this.writeGenerators(writeChunk8, sf2Layer4.getGlobalRegion().getGenerators());
            }
            for (final SF2LayerRegion sf2LayerRegion2 : sf2Layer4.getRegions()) {
                this.writeGenerators(writeChunk8, sf2LayerRegion2.getGenerators());
                final int index2 = this.samples.indexOf(sf2LayerRegion2.sample);
                if (index2 != -1) {
                    writeChunk8.writeUnsignedShort(53);
                    writeChunk8.writeShort((short)index2);
                }
            }
        }
        writeChunk8.write(new byte[4]);
        final RIFFWriter writeChunk9 = riffWriter.writeChunk("shdr");
        long n7 = 0L;
        for (final SF2Sample sf2Sample : this.samples) {
            writeChunk9.writeString(sf2Sample.name, 20);
            final long n8 = n7;
            final long n10;
            final long n9 = n10 = n7 + sf2Sample.data.capacity() / 2L;
            long n11 = sf2Sample.startLoop + n8;
            long n12 = sf2Sample.endLoop + n8;
            if (n11 < n8) {
                n11 = n8;
            }
            if (n12 > n10) {
                n12 = n10;
            }
            writeChunk9.writeUnsignedInt(n8);
            writeChunk9.writeUnsignedInt(n10);
            writeChunk9.writeUnsignedInt(n11);
            writeChunk9.writeUnsignedInt(n12);
            writeChunk9.writeUnsignedInt(sf2Sample.sampleRate);
            writeChunk9.writeUnsignedByte(sf2Sample.originalPitch);
            writeChunk9.writeByte(sf2Sample.pitchCorrection);
            writeChunk9.writeUnsignedShort(sf2Sample.sampleLink);
            writeChunk9.writeUnsignedShort(sf2Sample.sampleType);
            n7 = n9 + 32L;
        }
        writeChunk9.writeString("EOS", 20);
        writeChunk9.write(new byte[26]);
    }
    
    @Override
    public String getName() {
        return this.name;
    }
    
    @Override
    public String getVersion() {
        return this.major + "." + this.minor;
    }
    
    @Override
    public String getVendor() {
        return this.engineers;
    }
    
    @Override
    public String getDescription() {
        return this.comments;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public void setVendor(final String engineers) {
        this.engineers = engineers;
    }
    
    public void setDescription(final String comments) {
        this.comments = comments;
    }
    
    @Override
    public SoundbankResource[] getResources() {
        final SoundbankResource[] array = new SoundbankResource[this.layers.size() + this.samples.size()];
        int n = 0;
        for (int i = 0; i < this.layers.size(); ++i) {
            array[n++] = this.layers.get(i);
        }
        for (int j = 0; j < this.samples.size(); ++j) {
            array[n++] = this.samples.get(j);
        }
        return array;
    }
    
    @Override
    public SF2Instrument[] getInstruments() {
        final SF2Instrument[] array = this.instruments.toArray(new SF2Instrument[this.instruments.size()]);
        Arrays.sort(array, new ModelInstrumentComparator());
        return array;
    }
    
    public SF2Layer[] getLayers() {
        return this.layers.toArray(new SF2Layer[this.layers.size()]);
    }
    
    public SF2Sample[] getSamples() {
        return this.samples.toArray(new SF2Sample[this.samples.size()]);
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
    
    public String getCreationDate() {
        return this.creationDate;
    }
    
    public void setCreationDate(final String creationDate) {
        this.creationDate = creationDate;
    }
    
    public String getProduct() {
        return this.product;
    }
    
    public void setProduct(final String product) {
        this.product = product;
    }
    
    public String getRomName() {
        return this.romName;
    }
    
    public void setRomName(final String romName) {
        this.romName = romName;
    }
    
    public int getRomVersionMajor() {
        return this.romVersionMajor;
    }
    
    public void setRomVersionMajor(final int romVersionMajor) {
        this.romVersionMajor = romVersionMajor;
    }
    
    public int getRomVersionMinor() {
        return this.romVersionMinor;
    }
    
    public void setRomVersionMinor(final int romVersionMinor) {
        this.romVersionMinor = romVersionMinor;
    }
    
    public String getTargetEngine() {
        return this.targetEngine;
    }
    
    public void setTargetEngine(final String targetEngine) {
        this.targetEngine = targetEngine;
    }
    
    public String getTools() {
        return this.tools;
    }
    
    public void setTools(final String tools) {
        this.tools = tools;
    }
    
    public void addResource(final SoundbankResource soundbankResource) {
        if (soundbankResource instanceof SF2Instrument) {
            this.instruments.add((SF2Instrument)soundbankResource);
        }
        if (soundbankResource instanceof SF2Layer) {
            this.layers.add((SF2Layer)soundbankResource);
        }
        if (soundbankResource instanceof SF2Sample) {
            this.samples.add((SF2Sample)soundbankResource);
        }
    }
    
    public void removeResource(final SoundbankResource soundbankResource) {
        if (soundbankResource instanceof SF2Instrument) {
            this.instruments.remove(soundbankResource);
        }
        if (soundbankResource instanceof SF2Layer) {
            this.layers.remove(soundbankResource);
        }
        if (soundbankResource instanceof SF2Sample) {
            this.samples.remove(soundbankResource);
        }
    }
    
    public void addInstrument(final SF2Instrument sf2Instrument) {
        this.instruments.add(sf2Instrument);
    }
    
    public void removeInstrument(final SF2Instrument sf2Instrument) {
        this.instruments.remove(sf2Instrument);
    }
}
