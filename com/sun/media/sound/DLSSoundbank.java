package com.sun.media.sound;

import javax.sound.midi.Instrument;
import javax.sound.midi.Patch;
import java.util.Comparator;
import java.util.Arrays;
import javax.sound.midi.SoundbankResource;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.AudioInputStream;
import java.io.OutputStream;
import javax.sound.sampled.AudioFormat;
import java.util.Collection;
import java.util.Stack;
import java.util.Iterator;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Map;
import java.io.File;
import java.util.List;
import javax.sound.midi.Soundbank;

public final class DLSSoundbank implements Soundbank
{
    private static final int DLS_CDL_AND = 1;
    private static final int DLS_CDL_OR = 2;
    private static final int DLS_CDL_XOR = 3;
    private static final int DLS_CDL_ADD = 4;
    private static final int DLS_CDL_SUBTRACT = 5;
    private static final int DLS_CDL_MULTIPLY = 6;
    private static final int DLS_CDL_DIVIDE = 7;
    private static final int DLS_CDL_LOGICAL_AND = 8;
    private static final int DLS_CDL_LOGICAL_OR = 9;
    private static final int DLS_CDL_LT = 10;
    private static final int DLS_CDL_LE = 11;
    private static final int DLS_CDL_GT = 12;
    private static final int DLS_CDL_GE = 13;
    private static final int DLS_CDL_EQ = 14;
    private static final int DLS_CDL_NOT = 15;
    private static final int DLS_CDL_CONST = 16;
    private static final int DLS_CDL_QUERY = 17;
    private static final int DLS_CDL_QUERYSUPPORTED = 18;
    private static final DLSID DLSID_GMInHardware;
    private static final DLSID DLSID_GSInHardware;
    private static final DLSID DLSID_XGInHardware;
    private static final DLSID DLSID_SupportsDLS1;
    private static final DLSID DLSID_SupportsDLS2;
    private static final DLSID DLSID_SampleMemorySize;
    private static final DLSID DLSID_ManufacturersID;
    private static final DLSID DLSID_ProductID;
    private static final DLSID DLSID_SamplePlaybackRate;
    private long major;
    private long minor;
    private final DLSInfo info;
    private final List<DLSInstrument> instruments;
    private final List<DLSSample> samples;
    private boolean largeFormat;
    private File sampleFile;
    private Map<DLSRegion, Long> temp_rgnassign;
    
    public DLSSoundbank() {
        this.major = -1L;
        this.minor = -1L;
        this.info = new DLSInfo();
        this.instruments = new ArrayList<DLSInstrument>();
        this.samples = new ArrayList<DLSSample>();
        this.largeFormat = false;
        this.temp_rgnassign = new HashMap<DLSRegion, Long>();
    }
    
    public DLSSoundbank(final URL url) throws IOException {
        this.major = -1L;
        this.minor = -1L;
        this.info = new DLSInfo();
        this.instruments = new ArrayList<DLSInstrument>();
        this.samples = new ArrayList<DLSSample>();
        this.largeFormat = false;
        this.temp_rgnassign = new HashMap<DLSRegion, Long>();
        final InputStream openStream = url.openStream();
        try {
            this.readSoundbank(openStream);
        }
        finally {
            openStream.close();
        }
    }
    
    public DLSSoundbank(final File sampleFile) throws IOException {
        this.major = -1L;
        this.minor = -1L;
        this.info = new DLSInfo();
        this.instruments = new ArrayList<DLSInstrument>();
        this.samples = new ArrayList<DLSSample>();
        this.largeFormat = false;
        this.temp_rgnassign = new HashMap<DLSRegion, Long>();
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
    
    public DLSSoundbank(final InputStream inputStream) throws IOException {
        this.major = -1L;
        this.minor = -1L;
        this.info = new DLSInfo();
        this.instruments = new ArrayList<DLSInstrument>();
        this.samples = new ArrayList<DLSSample>();
        this.largeFormat = false;
        this.temp_rgnassign = new HashMap<DLSRegion, Long>();
        this.readSoundbank(inputStream);
    }
    
    private void readSoundbank(final InputStream inputStream) throws IOException {
        final RIFFReader riffReader = new RIFFReader(inputStream);
        if (!riffReader.getFormat().equals("RIFF")) {
            throw new RIFFInvalidFormatException("Input stream is not a valid RIFF stream!");
        }
        if (!riffReader.getType().equals("DLS ")) {
            throw new RIFFInvalidFormatException("Input stream is not a valid DLS soundbank!");
        }
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            if (nextChunk.getFormat().equals("LIST")) {
                if (nextChunk.getType().equals("INFO")) {
                    this.readInfoChunk(nextChunk);
                }
                if (nextChunk.getType().equals("lins")) {
                    this.readLinsChunk(nextChunk);
                }
                if (!nextChunk.getType().equals("wvpl")) {
                    continue;
                }
                this.readWvplChunk(nextChunk);
            }
            else {
                if (nextChunk.getFormat().equals("cdl ") && !this.readCdlChunk(nextChunk)) {
                    throw new RIFFInvalidFormatException("DLS file isn't supported!");
                }
                if (nextChunk.getFormat().equals("colh")) {}
                if (nextChunk.getFormat().equals("ptbl")) {}
                if (!nextChunk.getFormat().equals("vers")) {
                    continue;
                }
                this.major = nextChunk.readUnsignedInt();
                this.minor = nextChunk.readUnsignedInt();
            }
        }
        for (final Map.Entry entry : this.temp_rgnassign.entrySet()) {
            ((DLSRegion)entry.getKey()).sample = this.samples.get((int)(long)entry.getValue());
        }
        this.temp_rgnassign = null;
    }
    
    private boolean cdlIsQuerySupported(final DLSID dlsid) {
        return dlsid.equals(DLSSoundbank.DLSID_GMInHardware) || dlsid.equals(DLSSoundbank.DLSID_GSInHardware) || dlsid.equals(DLSSoundbank.DLSID_XGInHardware) || dlsid.equals(DLSSoundbank.DLSID_SupportsDLS1) || dlsid.equals(DLSSoundbank.DLSID_SupportsDLS2) || dlsid.equals(DLSSoundbank.DLSID_SampleMemorySize) || dlsid.equals(DLSSoundbank.DLSID_ManufacturersID) || dlsid.equals(DLSSoundbank.DLSID_ProductID) || dlsid.equals(DLSSoundbank.DLSID_SamplePlaybackRate);
    }
    
    private long cdlQuery(final DLSID dlsid) {
        if (dlsid.equals(DLSSoundbank.DLSID_GMInHardware)) {
            return 1L;
        }
        if (dlsid.equals(DLSSoundbank.DLSID_GSInHardware)) {
            return 0L;
        }
        if (dlsid.equals(DLSSoundbank.DLSID_XGInHardware)) {
            return 0L;
        }
        if (dlsid.equals(DLSSoundbank.DLSID_SupportsDLS1)) {
            return 1L;
        }
        if (dlsid.equals(DLSSoundbank.DLSID_SupportsDLS2)) {
            return 1L;
        }
        if (dlsid.equals(DLSSoundbank.DLSID_SampleMemorySize)) {
            return Runtime.getRuntime().totalMemory();
        }
        if (dlsid.equals(DLSSoundbank.DLSID_ManufacturersID)) {
            return 0L;
        }
        if (dlsid.equals(DLSSoundbank.DLSID_ProductID)) {
            return 0L;
        }
        if (dlsid.equals(DLSSoundbank.DLSID_SamplePlaybackRate)) {
            return 44100L;
        }
        return 0L;
    }
    
    private boolean readCdlChunk(final RIFFReader riffReader) throws IOException {
        final Stack stack = new Stack();
        while (riffReader.available() != 0) {
            switch (riffReader.readUnsignedShort()) {
                case 1: {
                    final long longValue = stack.pop();
                    final long longValue2 = stack.pop();
                    stack.push((long)((longValue != 0L && longValue2 != 0L) ? 1 : 0));
                    continue;
                }
                case 2: {
                    final long longValue3 = stack.pop();
                    final long longValue4 = stack.pop();
                    stack.push((long)((longValue3 != 0L || longValue4 != 0L) ? 1 : 0));
                    continue;
                }
                case 3: {
                    stack.push((long)(((long)stack.pop() != 0L ^ (long)stack.pop() != 0L) ? 1 : 0));
                    continue;
                }
                case 4: {
                    stack.push((long)stack.pop() + (long)stack.pop());
                    continue;
                }
                case 5: {
                    stack.push((long)stack.pop() - (long)stack.pop());
                    continue;
                }
                case 6: {
                    stack.push((long)stack.pop() * (long)stack.pop());
                    continue;
                }
                case 7: {
                    stack.push((long)stack.pop() / (long)stack.pop());
                    continue;
                }
                case 8: {
                    final long longValue5 = stack.pop();
                    final long longValue6 = stack.pop();
                    stack.push((long)((longValue5 != 0L && longValue6 != 0L) ? 1 : 0));
                    continue;
                }
                case 9: {
                    final long longValue7 = stack.pop();
                    final long longValue8 = stack.pop();
                    stack.push((long)((longValue7 != 0L || longValue8 != 0L) ? 1 : 0));
                    continue;
                }
                case 10: {
                    stack.push((long)(((long)stack.pop() < (long)stack.pop()) ? 1 : 0));
                    continue;
                }
                case 11: {
                    stack.push((long)(((long)stack.pop() <= (long)stack.pop()) ? 1 : 0));
                    continue;
                }
                case 12: {
                    stack.push((long)(((long)stack.pop() > (long)stack.pop()) ? 1 : 0));
                    continue;
                }
                case 13: {
                    stack.push((long)(((long)stack.pop() >= (long)stack.pop()) ? 1 : 0));
                    continue;
                }
                case 14: {
                    stack.push((long)(((long)stack.pop() == (long)stack.pop()) ? 1 : 0));
                    continue;
                }
                case 15: {
                    final long longValue9 = stack.pop();
                    ((Long)stack.pop()).longValue();
                    stack.push((long)((longValue9 == 0L) ? 1 : 0));
                    continue;
                }
                case 16: {
                    stack.push(riffReader.readUnsignedInt());
                    continue;
                }
                case 17: {
                    stack.push(this.cdlQuery(DLSID.read(riffReader)));
                    continue;
                }
                case 18: {
                    stack.push((long)(this.cdlIsQuerySupported(DLSID.read(riffReader)) ? 1 : 0));
                    continue;
                }
            }
        }
        return !stack.isEmpty() && (long)stack.pop() == 1L;
    }
    
    private void readInfoChunk(final RIFFReader riffReader) throws IOException {
        this.info.name = null;
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            final String format = nextChunk.getFormat();
            if (format.equals("INAM")) {
                this.info.name = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICRD")) {
                this.info.creationDate = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IENG")) {
                this.info.engineers = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IPRD")) {
                this.info.product = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICOP")) {
                this.info.copyright = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICMT")) {
                this.info.comments = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISFT")) {
                this.info.tools = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IARL")) {
                this.info.archival_location = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IART")) {
                this.info.artist = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICMS")) {
                this.info.commissioned = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IGNR")) {
                this.info.genre = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IKEY")) {
                this.info.keywords = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IMED")) {
                this.info.medium = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISBJ")) {
                this.info.subject = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISRC")) {
                this.info.source = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISRF")) {
                this.info.source_form = nextChunk.readString(nextChunk.available());
            }
            else {
                if (!format.equals("ITCH")) {
                    continue;
                }
                this.info.technician = nextChunk.readString(nextChunk.available());
            }
        }
    }
    
    private void readLinsChunk(final RIFFReader riffReader) throws IOException {
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            if (nextChunk.getFormat().equals("LIST") && nextChunk.getType().equals("ins ")) {
                this.readInsChunk(nextChunk);
            }
        }
    }
    
    private void readInsChunk(final RIFFReader riffReader) throws IOException {
        final DLSInstrument dlsInstrument = new DLSInstrument(this);
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            final String format = nextChunk.getFormat();
            if (format.equals("LIST")) {
                if (nextChunk.getType().equals("INFO")) {
                    this.readInsInfoChunk(dlsInstrument, nextChunk);
                }
                if (nextChunk.getType().equals("lrgn")) {
                    while (nextChunk.hasNextChunk()) {
                        final RIFFReader nextChunk2 = nextChunk.nextChunk();
                        if (nextChunk2.getFormat().equals("LIST")) {
                            if (nextChunk2.getType().equals("rgn ")) {
                                final DLSRegion dlsRegion = new DLSRegion();
                                if (this.readRgnChunk(dlsRegion, nextChunk2)) {
                                    dlsInstrument.getRegions().add(dlsRegion);
                                }
                            }
                            if (!nextChunk2.getType().equals("rgn2")) {
                                continue;
                            }
                            final DLSRegion dlsRegion2 = new DLSRegion();
                            if (!this.readRgnChunk(dlsRegion2, nextChunk2)) {
                                continue;
                            }
                            dlsInstrument.getRegions().add(dlsRegion2);
                        }
                    }
                }
                if (nextChunk.getType().equals("lart")) {
                    final ArrayList list = new ArrayList();
                    while (nextChunk.hasNextChunk()) {
                        final RIFFReader nextChunk3 = nextChunk.nextChunk();
                        if (nextChunk.getFormat().equals("cdl ") && !this.readCdlChunk(nextChunk)) {
                            list.clear();
                            break;
                        }
                        if (!nextChunk3.getFormat().equals("art1")) {
                            continue;
                        }
                        this.readArt1Chunk(list, nextChunk3);
                    }
                    dlsInstrument.getModulators().addAll(list);
                }
                if (!nextChunk.getType().equals("lar2")) {
                    continue;
                }
                final ArrayList list2 = new ArrayList();
                while (nextChunk.hasNextChunk()) {
                    final RIFFReader nextChunk4 = nextChunk.nextChunk();
                    if (nextChunk.getFormat().equals("cdl ") && !this.readCdlChunk(nextChunk)) {
                        list2.clear();
                        break;
                    }
                    if (!nextChunk4.getFormat().equals("art2")) {
                        continue;
                    }
                    this.readArt2Chunk(list2, nextChunk4);
                }
                dlsInstrument.getModulators().addAll(list2);
            }
            else {
                if (format.equals("dlid")) {
                    nextChunk.readFully(dlsInstrument.guid = new byte[16]);
                }
                if (!format.equals("insh")) {
                    continue;
                }
                nextChunk.readUnsignedInt();
                final int bank = nextChunk.read() + ((nextChunk.read() & 0x7F) << 7);
                nextChunk.read();
                final int read = nextChunk.read();
                final int preset = nextChunk.read() & 0x7F;
                nextChunk.read();
                nextChunk.read();
                nextChunk.read();
                dlsInstrument.bank = bank;
                dlsInstrument.preset = preset;
                dlsInstrument.druminstrument = ((read & 0x80) > 0);
            }
        }
        this.instruments.add(dlsInstrument);
    }
    
    private void readArt1Chunk(final List<DLSModulator> list, final RIFFReader riffReader) throws IOException {
        final long unsignedInt = riffReader.readUnsignedInt();
        final long unsignedInt2 = riffReader.readUnsignedInt();
        if (unsignedInt - 8L != 0L) {
            riffReader.skip(unsignedInt - 8L);
        }
        for (int n = 0; n < unsignedInt2; ++n) {
            final DLSModulator dlsModulator = new DLSModulator();
            dlsModulator.version = 1;
            dlsModulator.source = riffReader.readUnsignedShort();
            dlsModulator.control = riffReader.readUnsignedShort();
            dlsModulator.destination = riffReader.readUnsignedShort();
            dlsModulator.transform = riffReader.readUnsignedShort();
            dlsModulator.scale = riffReader.readInt();
            list.add(dlsModulator);
        }
    }
    
    private void readArt2Chunk(final List<DLSModulator> list, final RIFFReader riffReader) throws IOException {
        final long unsignedInt = riffReader.readUnsignedInt();
        final long unsignedInt2 = riffReader.readUnsignedInt();
        if (unsignedInt - 8L != 0L) {
            riffReader.skip(unsignedInt - 8L);
        }
        for (int n = 0; n < unsignedInt2; ++n) {
            final DLSModulator dlsModulator = new DLSModulator();
            dlsModulator.version = 2;
            dlsModulator.source = riffReader.readUnsignedShort();
            dlsModulator.control = riffReader.readUnsignedShort();
            dlsModulator.destination = riffReader.readUnsignedShort();
            dlsModulator.transform = riffReader.readUnsignedShort();
            dlsModulator.scale = riffReader.readInt();
            list.add(dlsModulator);
        }
    }
    
    private boolean readRgnChunk(final DLSRegion dlsRegion, final RIFFReader riffReader) throws IOException {
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            final String format = nextChunk.getFormat();
            if (format.equals("LIST")) {
                if (nextChunk.getType().equals("lart")) {
                    final ArrayList list = new ArrayList();
                    while (nextChunk.hasNextChunk()) {
                        final RIFFReader nextChunk2 = nextChunk.nextChunk();
                        if (nextChunk.getFormat().equals("cdl ") && !this.readCdlChunk(nextChunk)) {
                            list.clear();
                            break;
                        }
                        if (!nextChunk2.getFormat().equals("art1")) {
                            continue;
                        }
                        this.readArt1Chunk(list, nextChunk2);
                    }
                    dlsRegion.getModulators().addAll(list);
                }
                if (!nextChunk.getType().equals("lar2")) {
                    continue;
                }
                final ArrayList list2 = new ArrayList();
                while (nextChunk.hasNextChunk()) {
                    final RIFFReader nextChunk3 = nextChunk.nextChunk();
                    if (nextChunk.getFormat().equals("cdl ") && !this.readCdlChunk(nextChunk)) {
                        list2.clear();
                        break;
                    }
                    if (!nextChunk3.getFormat().equals("art2")) {
                        continue;
                    }
                    this.readArt2Chunk(list2, nextChunk3);
                }
                dlsRegion.getModulators().addAll(list2);
            }
            else {
                if (format.equals("cdl ") && !this.readCdlChunk(nextChunk)) {
                    return false;
                }
                if (format.equals("rgnh")) {
                    dlsRegion.keyfrom = nextChunk.readUnsignedShort();
                    dlsRegion.keyto = nextChunk.readUnsignedShort();
                    dlsRegion.velfrom = nextChunk.readUnsignedShort();
                    dlsRegion.velto = nextChunk.readUnsignedShort();
                    dlsRegion.options = nextChunk.readUnsignedShort();
                    dlsRegion.exclusiveClass = nextChunk.readUnsignedShort();
                }
                if (format.equals("wlnk")) {
                    dlsRegion.fusoptions = nextChunk.readUnsignedShort();
                    dlsRegion.phasegroup = nextChunk.readUnsignedShort();
                    dlsRegion.channel = nextChunk.readUnsignedInt();
                    this.temp_rgnassign.put(dlsRegion, nextChunk.readUnsignedInt());
                }
                if (!format.equals("wsmp")) {
                    continue;
                }
                this.readWsmpChunk(dlsRegion.sampleoptions = new DLSSampleOptions(), nextChunk);
            }
        }
        return true;
    }
    
    private void readWsmpChunk(final DLSSampleOptions dlsSampleOptions, final RIFFReader riffReader) throws IOException {
        final long unsignedInt = riffReader.readUnsignedInt();
        dlsSampleOptions.unitynote = riffReader.readUnsignedShort();
        dlsSampleOptions.finetune = riffReader.readShort();
        dlsSampleOptions.attenuation = riffReader.readInt();
        dlsSampleOptions.options = riffReader.readUnsignedInt();
        final long n = riffReader.readInt();
        if (unsignedInt > 20L) {
            riffReader.skip(unsignedInt - 20L);
        }
        for (int n2 = 0; n2 < n; ++n2) {
            final DLSSampleLoop dlsSampleLoop = new DLSSampleLoop();
            final long unsignedInt2 = riffReader.readUnsignedInt();
            dlsSampleLoop.type = riffReader.readUnsignedInt();
            dlsSampleLoop.start = riffReader.readUnsignedInt();
            dlsSampleLoop.length = riffReader.readUnsignedInt();
            dlsSampleOptions.loops.add(dlsSampleLoop);
            if (unsignedInt2 > 16L) {
                riffReader.skip(unsignedInt2 - 16L);
            }
        }
    }
    
    private void readInsInfoChunk(final DLSInstrument dlsInstrument, final RIFFReader riffReader) throws IOException {
        dlsInstrument.info.name = null;
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            final String format = nextChunk.getFormat();
            if (format.equals("INAM")) {
                dlsInstrument.info.name = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICRD")) {
                dlsInstrument.info.creationDate = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IENG")) {
                dlsInstrument.info.engineers = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IPRD")) {
                dlsInstrument.info.product = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICOP")) {
                dlsInstrument.info.copyright = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICMT")) {
                dlsInstrument.info.comments = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISFT")) {
                dlsInstrument.info.tools = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IARL")) {
                dlsInstrument.info.archival_location = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IART")) {
                dlsInstrument.info.artist = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICMS")) {
                dlsInstrument.info.commissioned = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IGNR")) {
                dlsInstrument.info.genre = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IKEY")) {
                dlsInstrument.info.keywords = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IMED")) {
                dlsInstrument.info.medium = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISBJ")) {
                dlsInstrument.info.subject = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISRC")) {
                dlsInstrument.info.source = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISRF")) {
                dlsInstrument.info.source_form = nextChunk.readString(nextChunk.available());
            }
            else {
                if (!format.equals("ITCH")) {
                    continue;
                }
                dlsInstrument.info.technician = nextChunk.readString(nextChunk.available());
            }
        }
    }
    
    private void readWvplChunk(final RIFFReader riffReader) throws IOException {
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            if (nextChunk.getFormat().equals("LIST") && nextChunk.getType().equals("wave")) {
                this.readWaveChunk(nextChunk);
            }
        }
    }
    
    private void readWaveChunk(final RIFFReader riffReader) throws IOException {
        final DLSSample dlsSample = new DLSSample(this);
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            final String format = nextChunk.getFormat();
            if (format.equals("LIST")) {
                if (!nextChunk.getType().equals("INFO")) {
                    continue;
                }
                this.readWaveInfoChunk(dlsSample, nextChunk);
            }
            else {
                if (format.equals("dlid")) {
                    nextChunk.readFully(dlsSample.guid = new byte[16]);
                }
                if (format.equals("fmt ")) {
                    final int unsignedShort = nextChunk.readUnsignedShort();
                    if (unsignedShort != 1 && unsignedShort != 3) {
                        throw new RIFFInvalidDataException("Only PCM samples are supported!");
                    }
                    final int unsignedShort2 = nextChunk.readUnsignedShort();
                    final long unsignedInt = nextChunk.readUnsignedInt();
                    nextChunk.readUnsignedInt();
                    final int unsignedShort3 = nextChunk.readUnsignedShort();
                    final int unsignedShort4 = nextChunk.readUnsignedShort();
                    AudioFormat format2 = null;
                    if (unsignedShort == 1) {
                        if (unsignedShort4 == 8) {
                            format2 = new AudioFormat(AudioFormat.Encoding.PCM_UNSIGNED, (float)unsignedInt, unsignedShort4, unsignedShort2, unsignedShort3, (float)unsignedInt, false);
                        }
                        else {
                            format2 = new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, (float)unsignedInt, unsignedShort4, unsignedShort2, unsignedShort3, (float)unsignedInt, false);
                        }
                    }
                    if (unsignedShort == 3) {
                        format2 = new AudioFormat(AudioFormat.Encoding.PCM_FLOAT, (float)unsignedInt, unsignedShort4, unsignedShort2, unsignedShort3, (float)unsignedInt, false);
                    }
                    dlsSample.format = format2;
                }
                if (format.equals("data")) {
                    if (this.largeFormat) {
                        dlsSample.setData(new ModelByteBuffer(this.sampleFile, nextChunk.getFilePointer(), nextChunk.available()));
                    }
                    else {
                        final byte[] data = new byte[nextChunk.available()];
                        dlsSample.setData(data);
                        int i = 0;
                        final int available = nextChunk.available();
                        while (i != available) {
                            if (available - i > 65536) {
                                nextChunk.readFully(data, i, 65536);
                                i += 65536;
                            }
                            else {
                                nextChunk.readFully(data, i, available - i);
                                i = available;
                            }
                        }
                    }
                }
                if (!format.equals("wsmp")) {
                    continue;
                }
                this.readWsmpChunk(dlsSample.sampleoptions = new DLSSampleOptions(), nextChunk);
            }
        }
        this.samples.add(dlsSample);
    }
    
    private void readWaveInfoChunk(final DLSSample dlsSample, final RIFFReader riffReader) throws IOException {
        dlsSample.info.name = null;
        while (riffReader.hasNextChunk()) {
            final RIFFReader nextChunk = riffReader.nextChunk();
            final String format = nextChunk.getFormat();
            if (format.equals("INAM")) {
                dlsSample.info.name = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICRD")) {
                dlsSample.info.creationDate = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IENG")) {
                dlsSample.info.engineers = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IPRD")) {
                dlsSample.info.product = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICOP")) {
                dlsSample.info.copyright = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICMT")) {
                dlsSample.info.comments = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISFT")) {
                dlsSample.info.tools = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IARL")) {
                dlsSample.info.archival_location = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IART")) {
                dlsSample.info.artist = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ICMS")) {
                dlsSample.info.commissioned = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IGNR")) {
                dlsSample.info.genre = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IKEY")) {
                dlsSample.info.keywords = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("IMED")) {
                dlsSample.info.medium = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISBJ")) {
                dlsSample.info.subject = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISRC")) {
                dlsSample.info.source = nextChunk.readString(nextChunk.available());
            }
            else if (format.equals("ISRF")) {
                dlsSample.info.source_form = nextChunk.readString(nextChunk.available());
            }
            else {
                if (!format.equals("ITCH")) {
                    continue;
                }
                dlsSample.info.technician = nextChunk.readString(nextChunk.available());
            }
        }
    }
    
    public void save(final String s) throws IOException {
        this.writeSoundbank(new RIFFWriter(s, "DLS "));
    }
    
    public void save(final File file) throws IOException {
        this.writeSoundbank(new RIFFWriter(file, "DLS "));
    }
    
    public void save(final OutputStream outputStream) throws IOException {
        this.writeSoundbank(new RIFFWriter(outputStream, "DLS "));
    }
    
    private void writeSoundbank(final RIFFWriter riffWriter) throws IOException {
        riffWriter.writeChunk("colh").writeUnsignedInt(this.instruments.size());
        if (this.major != -1L && this.minor != -1L) {
            final RIFFWriter writeChunk = riffWriter.writeChunk("vers");
            writeChunk.writeUnsignedInt(this.major);
            writeChunk.writeUnsignedInt(this.minor);
        }
        this.writeInstruments(riffWriter.writeList("lins"));
        final RIFFWriter writeChunk2 = riffWriter.writeChunk("ptbl");
        writeChunk2.writeUnsignedInt(8L);
        writeChunk2.writeUnsignedInt(this.samples.size());
        final long filePointer = riffWriter.getFilePointer();
        for (int i = 0; i < this.samples.size(); ++i) {
            writeChunk2.writeUnsignedInt(0L);
        }
        final RIFFWriter writeList = riffWriter.writeList("wvpl");
        final long filePointer2 = writeList.getFilePointer();
        final ArrayList list = new ArrayList();
        for (final DLSSample dlsSample : this.samples) {
            list.add(writeList.getFilePointer() - filePointer2);
            this.writeSample(writeList.writeList("wave"), dlsSample);
        }
        final long filePointer3 = riffWriter.getFilePointer();
        riffWriter.seek(filePointer);
        riffWriter.setWriteOverride(true);
        final Iterator iterator2 = list.iterator();
        while (iterator2.hasNext()) {
            riffWriter.writeUnsignedInt((long)iterator2.next());
        }
        riffWriter.setWriteOverride(false);
        riffWriter.seek(filePointer3);
        this.writeInfo(riffWriter.writeList("INFO"), this.info);
        riffWriter.close();
    }
    
    private void writeSample(final RIFFWriter riffWriter, final DLSSample dlsSample) throws IOException {
        AudioFormat format = dlsSample.getFormat();
        AudioFormat.Encoding encoding = format.getEncoding();
        final float sampleRate = format.getSampleRate();
        final int sampleSizeInBits = format.getSampleSizeInBits();
        final int channels = format.getChannels();
        final int frameSize = format.getFrameSize();
        final float frameRate = format.getFrameRate();
        boolean bigEndian = format.isBigEndian();
        boolean b = false;
        if (format.getSampleSizeInBits() == 8) {
            if (!encoding.equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
                encoding = AudioFormat.Encoding.PCM_UNSIGNED;
                b = true;
            }
        }
        else {
            if (!encoding.equals(AudioFormat.Encoding.PCM_SIGNED)) {
                encoding = AudioFormat.Encoding.PCM_SIGNED;
                b = true;
            }
            if (bigEndian) {
                bigEndian = false;
                b = true;
            }
        }
        if (b) {
            format = new AudioFormat(encoding, sampleRate, sampleSizeInBits, channels, frameSize, frameRate, bigEndian);
        }
        final RIFFWriter writeChunk = riffWriter.writeChunk("fmt ");
        int n = 0;
        if (format.getEncoding().equals(AudioFormat.Encoding.PCM_UNSIGNED)) {
            n = 1;
        }
        else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_SIGNED)) {
            n = 1;
        }
        else if (format.getEncoding().equals(AudioFormat.Encoding.PCM_FLOAT)) {
            n = 3;
        }
        writeChunk.writeUnsignedShort(n);
        writeChunk.writeUnsignedShort(format.getChannels());
        writeChunk.writeUnsignedInt((long)format.getSampleRate());
        writeChunk.writeUnsignedInt((long)format.getFrameRate() * format.getFrameSize());
        writeChunk.writeUnsignedShort(format.getFrameSize());
        writeChunk.writeUnsignedShort(format.getSampleSizeInBits());
        writeChunk.write(0);
        writeChunk.write(0);
        this.writeSampleOptions(riffWriter.writeChunk("wsmp"), dlsSample.sampleoptions);
        if (b) {
            final RIFFWriter writeChunk2 = riffWriter.writeChunk("data");
            final AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(format, (AudioInputStream)dlsSample.getData());
            final byte[] array = new byte[1024];
            int read;
            while ((read = audioInputStream.read(array)) != -1) {
                writeChunk2.write(array, 0, read);
            }
        }
        else {
            dlsSample.getDataBuffer().writeTo(riffWriter.writeChunk("data"));
        }
        this.writeInfo(riffWriter.writeList("INFO"), dlsSample.info);
    }
    
    private void writeInstruments(final RIFFWriter riffWriter) throws IOException {
        final Iterator<DLSInstrument> iterator = this.instruments.iterator();
        while (iterator.hasNext()) {
            this.writeInstrument(riffWriter.writeList("ins "), iterator.next());
        }
    }
    
    private void writeInstrument(final RIFFWriter riffWriter, final DLSInstrument dlsInstrument) throws IOException {
        int n = 0;
        int n2 = 0;
        for (final DLSModulator dlsModulator : dlsInstrument.getModulators()) {
            if (dlsModulator.version == 1) {
                ++n;
            }
            if (dlsModulator.version == 2) {
                ++n2;
            }
        }
        final Iterator<DLSRegion> iterator2 = dlsInstrument.regions.iterator();
        while (iterator2.hasNext()) {
            for (final DLSModulator dlsModulator2 : iterator2.next().getModulators()) {
                if (dlsModulator2.version == 1) {
                    ++n;
                }
                if (dlsModulator2.version == 2) {
                    ++n2;
                }
            }
        }
        int n3 = 1;
        if (n2 > 0) {
            n3 = 2;
        }
        final RIFFWriter writeChunk = riffWriter.writeChunk("insh");
        writeChunk.writeUnsignedInt(dlsInstrument.getRegions().size());
        writeChunk.writeUnsignedInt(dlsInstrument.bank + (dlsInstrument.druminstrument ? 2147483648L : 0L));
        writeChunk.writeUnsignedInt(dlsInstrument.preset);
        final RIFFWriter writeList = riffWriter.writeList("lrgn");
        final Iterator<DLSRegion> iterator4 = dlsInstrument.regions.iterator();
        while (iterator4.hasNext()) {
            this.writeRegion(writeList, iterator4.next(), n3);
        }
        this.writeArticulators(riffWriter, dlsInstrument.getModulators());
        this.writeInfo(riffWriter.writeList("INFO"), dlsInstrument.info);
    }
    
    private void writeArticulators(final RIFFWriter riffWriter, final List<DLSModulator> list) throws IOException {
        int n = 0;
        int n2 = 0;
        for (final DLSModulator dlsModulator : list) {
            if (dlsModulator.version == 1) {
                ++n;
            }
            if (dlsModulator.version == 2) {
                ++n2;
            }
        }
        if (n > 0) {
            final RIFFWriter writeChunk = riffWriter.writeList("lart").writeChunk("art1");
            writeChunk.writeUnsignedInt(8L);
            writeChunk.writeUnsignedInt(n);
            for (final DLSModulator dlsModulator2 : list) {
                if (dlsModulator2.version == 1) {
                    writeChunk.writeUnsignedShort(dlsModulator2.source);
                    writeChunk.writeUnsignedShort(dlsModulator2.control);
                    writeChunk.writeUnsignedShort(dlsModulator2.destination);
                    writeChunk.writeUnsignedShort(dlsModulator2.transform);
                    writeChunk.writeInt(dlsModulator2.scale);
                }
            }
        }
        if (n2 > 0) {
            final RIFFWriter writeChunk2 = riffWriter.writeList("lar2").writeChunk("art2");
            writeChunk2.writeUnsignedInt(8L);
            writeChunk2.writeUnsignedInt(n2);
            for (final DLSModulator dlsModulator3 : list) {
                if (dlsModulator3.version == 2) {
                    writeChunk2.writeUnsignedShort(dlsModulator3.source);
                    writeChunk2.writeUnsignedShort(dlsModulator3.control);
                    writeChunk2.writeUnsignedShort(dlsModulator3.destination);
                    writeChunk2.writeUnsignedShort(dlsModulator3.transform);
                    writeChunk2.writeInt(dlsModulator3.scale);
                }
            }
        }
    }
    
    private void writeRegion(final RIFFWriter riffWriter, final DLSRegion dlsRegion, final int n) throws IOException {
        RIFFWriter riffWriter2 = null;
        if (n == 1) {
            riffWriter2 = riffWriter.writeList("rgn ");
        }
        if (n == 2) {
            riffWriter2 = riffWriter.writeList("rgn2");
        }
        if (riffWriter2 == null) {
            return;
        }
        final RIFFWriter writeChunk = riffWriter2.writeChunk("rgnh");
        writeChunk.writeUnsignedShort(dlsRegion.keyfrom);
        writeChunk.writeUnsignedShort(dlsRegion.keyto);
        writeChunk.writeUnsignedShort(dlsRegion.velfrom);
        writeChunk.writeUnsignedShort(dlsRegion.velto);
        writeChunk.writeUnsignedShort(dlsRegion.options);
        writeChunk.writeUnsignedShort(dlsRegion.exclusiveClass);
        if (dlsRegion.sampleoptions != null) {
            this.writeSampleOptions(riffWriter2.writeChunk("wsmp"), dlsRegion.sampleoptions);
        }
        if (dlsRegion.sample != null && this.samples.indexOf(dlsRegion.sample) != -1) {
            final RIFFWriter writeChunk2 = riffWriter2.writeChunk("wlnk");
            writeChunk2.writeUnsignedShort(dlsRegion.fusoptions);
            writeChunk2.writeUnsignedShort(dlsRegion.phasegroup);
            writeChunk2.writeUnsignedInt(dlsRegion.channel);
            writeChunk2.writeUnsignedInt(this.samples.indexOf(dlsRegion.sample));
        }
        this.writeArticulators(riffWriter2, dlsRegion.getModulators());
        riffWriter2.close();
    }
    
    private void writeSampleOptions(final RIFFWriter riffWriter, final DLSSampleOptions dlsSampleOptions) throws IOException {
        riffWriter.writeUnsignedInt(20L);
        riffWriter.writeUnsignedShort(dlsSampleOptions.unitynote);
        riffWriter.writeShort(dlsSampleOptions.finetune);
        riffWriter.writeInt(dlsSampleOptions.attenuation);
        riffWriter.writeUnsignedInt(dlsSampleOptions.options);
        riffWriter.writeInt(dlsSampleOptions.loops.size());
        for (final DLSSampleLoop dlsSampleLoop : dlsSampleOptions.loops) {
            riffWriter.writeUnsignedInt(16L);
            riffWriter.writeUnsignedInt(dlsSampleLoop.type);
            riffWriter.writeUnsignedInt(dlsSampleLoop.start);
            riffWriter.writeUnsignedInt(dlsSampleLoop.length);
        }
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
    
    private void writeInfo(final RIFFWriter riffWriter, final DLSInfo dlsInfo) throws IOException {
        this.writeInfoStringChunk(riffWriter, "INAM", dlsInfo.name);
        this.writeInfoStringChunk(riffWriter, "ICRD", dlsInfo.creationDate);
        this.writeInfoStringChunk(riffWriter, "IENG", dlsInfo.engineers);
        this.writeInfoStringChunk(riffWriter, "IPRD", dlsInfo.product);
        this.writeInfoStringChunk(riffWriter, "ICOP", dlsInfo.copyright);
        this.writeInfoStringChunk(riffWriter, "ICMT", dlsInfo.comments);
        this.writeInfoStringChunk(riffWriter, "ISFT", dlsInfo.tools);
        this.writeInfoStringChunk(riffWriter, "IARL", dlsInfo.archival_location);
        this.writeInfoStringChunk(riffWriter, "IART", dlsInfo.artist);
        this.writeInfoStringChunk(riffWriter, "ICMS", dlsInfo.commissioned);
        this.writeInfoStringChunk(riffWriter, "IGNR", dlsInfo.genre);
        this.writeInfoStringChunk(riffWriter, "IKEY", dlsInfo.keywords);
        this.writeInfoStringChunk(riffWriter, "IMED", dlsInfo.medium);
        this.writeInfoStringChunk(riffWriter, "ISBJ", dlsInfo.subject);
        this.writeInfoStringChunk(riffWriter, "ISRC", dlsInfo.source);
        this.writeInfoStringChunk(riffWriter, "ISRF", dlsInfo.source_form);
        this.writeInfoStringChunk(riffWriter, "ITCH", dlsInfo.technician);
    }
    
    public DLSInfo getInfo() {
        return this.info;
    }
    
    @Override
    public String getName() {
        return this.info.name;
    }
    
    @Override
    public String getVersion() {
        return this.major + "." + this.minor;
    }
    
    @Override
    public String getVendor() {
        return this.info.engineers;
    }
    
    @Override
    public String getDescription() {
        return this.info.comments;
    }
    
    public void setName(final String name) {
        this.info.name = name;
    }
    
    public void setVendor(final String engineers) {
        this.info.engineers = engineers;
    }
    
    public void setDescription(final String comments) {
        this.info.comments = comments;
    }
    
    @Override
    public SoundbankResource[] getResources() {
        final SoundbankResource[] array = new SoundbankResource[this.samples.size()];
        int n = 0;
        for (int i = 0; i < this.samples.size(); ++i) {
            array[n++] = this.samples.get(i);
        }
        return array;
    }
    
    @Override
    public DLSInstrument[] getInstruments() {
        final DLSInstrument[] array = this.instruments.toArray(new DLSInstrument[this.instruments.size()]);
        Arrays.sort(array, new ModelInstrumentComparator());
        return array;
    }
    
    public DLSSample[] getSamples() {
        return this.samples.toArray(new DLSSample[this.samples.size()]);
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
        if (soundbankResource instanceof DLSInstrument) {
            this.instruments.add((DLSInstrument)soundbankResource);
        }
        if (soundbankResource instanceof DLSSample) {
            this.samples.add((DLSSample)soundbankResource);
        }
    }
    
    public void removeResource(final SoundbankResource soundbankResource) {
        if (soundbankResource instanceof DLSInstrument) {
            this.instruments.remove(soundbankResource);
        }
        if (soundbankResource instanceof DLSSample) {
            this.samples.remove(soundbankResource);
        }
    }
    
    public void addInstrument(final DLSInstrument dlsInstrument) {
        this.instruments.add(dlsInstrument);
    }
    
    public void removeInstrument(final DLSInstrument dlsInstrument) {
        this.instruments.remove(dlsInstrument);
    }
    
    public long getMajor() {
        return this.major;
    }
    
    public void setMajor(final long major) {
        this.major = major;
    }
    
    public long getMinor() {
        return this.minor;
    }
    
    public void setMinor(final long minor) {
        this.minor = minor;
    }
    
    static {
        DLSID_GMInHardware = new DLSID(395259684L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
        DLSID_GSInHardware = new DLSID(395259685L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
        DLSID_XGInHardware = new DLSID(395259686L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
        DLSID_SupportsDLS1 = new DLSID(395259687L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
        DLSID_SupportsDLS2 = new DLSID(-247096859L, 18057, 4562, 175, 166, 0, 170, 0, 36, 216, 182);
        DLSID_SampleMemorySize = new DLSID(395259688L, 50020, 4561, 167, 96, 0, 0, 248, 117, 172, 18);
        DLSID_ManufacturersID = new DLSID(-1338109567L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
        DLSID_ProductID = new DLSID(-1338109566L, 32917, 4562, 161, 239, 0, 96, 8, 51, 219, 216);
        DLSID_SamplePlaybackRate = new DLSID(714209043L, 42175, 4562, 187, 223, 0, 96, 8, 51, 219, 216);
    }
    
    private static class DLSID
    {
        long i1;
        int s1;
        int s2;
        int x1;
        int x2;
        int x3;
        int x4;
        int x5;
        int x6;
        int x7;
        int x8;
        
        private DLSID() {
        }
        
        DLSID(final long i1, final int s1, final int s2, final int x1, final int x2, final int x3, final int x4, final int x5, final int x6, final int x7, final int x8) {
            this.i1 = i1;
            this.s1 = s1;
            this.s2 = s2;
            this.x1 = x1;
            this.x2 = x2;
            this.x3 = x3;
            this.x4 = x4;
            this.x5 = x5;
            this.x6 = x6;
            this.x7 = x7;
            this.x8 = x8;
        }
        
        public static DLSID read(final RIFFReader riffReader) throws IOException {
            final DLSID dlsid = new DLSID();
            dlsid.i1 = riffReader.readUnsignedInt();
            dlsid.s1 = riffReader.readUnsignedShort();
            dlsid.s2 = riffReader.readUnsignedShort();
            dlsid.x1 = riffReader.readUnsignedByte();
            dlsid.x2 = riffReader.readUnsignedByte();
            dlsid.x3 = riffReader.readUnsignedByte();
            dlsid.x4 = riffReader.readUnsignedByte();
            dlsid.x5 = riffReader.readUnsignedByte();
            dlsid.x6 = riffReader.readUnsignedByte();
            dlsid.x7 = riffReader.readUnsignedByte();
            dlsid.x8 = riffReader.readUnsignedByte();
            return dlsid;
        }
        
        @Override
        public int hashCode() {
            return (int)this.i1;
        }
        
        @Override
        public boolean equals(final Object o) {
            if (!(o instanceof DLSID)) {
                return false;
            }
            final DLSID dlsid = (DLSID)o;
            return this.i1 == dlsid.i1 && this.s1 == dlsid.s1 && this.s2 == dlsid.s2 && this.x1 == dlsid.x1 && this.x2 == dlsid.x2 && this.x3 == dlsid.x3 && this.x4 == dlsid.x4 && this.x5 == dlsid.x5 && this.x6 == dlsid.x6 && this.x7 == dlsid.x7 && this.x8 == dlsid.x8;
        }
    }
}
