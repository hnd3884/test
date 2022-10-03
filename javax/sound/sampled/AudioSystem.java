package javax.sound.sampled;

import java.util.ArrayList;
import com.sun.media.sound.JDK13Services;
import java.io.OutputStream;
import javax.sound.sampled.spi.AudioFileWriter;
import java.util.HashSet;
import java.io.File;
import java.net.URL;
import java.io.IOException;
import javax.sound.sampled.spi.AudioFileReader;
import java.io.InputStream;
import javax.sound.sampled.spi.FormatConversionProvider;
import java.util.Vector;
import javax.sound.sampled.spi.MixerProvider;
import java.util.List;

public class AudioSystem
{
    public static final int NOT_SPECIFIED = -1;
    
    private AudioSystem() {
    }
    
    public static Mixer.Info[] getMixerInfo() {
        final List mixerInfoList = getMixerInfoList();
        return mixerInfoList.toArray(new Mixer.Info[mixerInfoList.size()]);
    }
    
    public static Mixer getMixer(final Mixer.Info info) {
        final List mixerProviders = getMixerProviders();
        for (int i = 0; i < mixerProviders.size(); ++i) {
            try {
                return ((MixerProvider)mixerProviders.get(i)).getMixer(info);
            }
            catch (final IllegalArgumentException ex) {}
            catch (final NullPointerException ex2) {}
        }
        if (info == null) {
            for (int j = 0; j < mixerProviders.size(); ++j) {
                try {
                    final MixerProvider mixerProvider = mixerProviders.get(j);
                    final Mixer.Info[] mixerInfo = mixerProvider.getMixerInfo();
                    int k = 0;
                    while (k < mixerInfo.length) {
                        try {
                            return mixerProvider.getMixer(mixerInfo[k]);
                        }
                        catch (final IllegalArgumentException ex3) {
                            ++k;
                            continue;
                        }
                        break;
                    }
                }
                catch (final IllegalArgumentException ex4) {}
                catch (final NullPointerException ex5) {}
            }
        }
        throw new IllegalArgumentException("Mixer not supported: " + ((info != null) ? info.toString() : "null"));
    }
    
    public static Line.Info[] getSourceLineInfo(final Line.Info info) {
        final Vector vector = new Vector();
        final Mixer.Info[] mixerInfo = getMixerInfo();
        for (int i = 0; i < mixerInfo.length; ++i) {
            final Line.Info[] sourceLineInfo = getMixer(mixerInfo[i]).getSourceLineInfo(info);
            for (int j = 0; j < sourceLineInfo.length; ++j) {
                vector.addElement(sourceLineInfo[j]);
            }
        }
        final Line.Info[] array = new Line.Info[vector.size()];
        for (int k = 0; k < array.length; ++k) {
            array[k] = (Line.Info)vector.get(k);
        }
        return array;
    }
    
    public static Line.Info[] getTargetLineInfo(final Line.Info info) {
        final Vector vector = new Vector();
        final Mixer.Info[] mixerInfo = getMixerInfo();
        for (int i = 0; i < mixerInfo.length; ++i) {
            final Line.Info[] targetLineInfo = getMixer(mixerInfo[i]).getTargetLineInfo(info);
            for (int j = 0; j < targetLineInfo.length; ++j) {
                vector.addElement(targetLineInfo[j]);
            }
        }
        final Line.Info[] array = new Line.Info[vector.size()];
        for (int k = 0; k < array.length; ++k) {
            array[k] = (Line.Info)vector.get(k);
        }
        return array;
    }
    
    public static boolean isLineSupported(final Line.Info info) {
        final Mixer.Info[] mixerInfo = getMixerInfo();
        for (int i = 0; i < mixerInfo.length; ++i) {
            if (mixerInfo[i] != null && getMixer(mixerInfo[i]).isLineSupported(info)) {
                return true;
            }
        }
        return false;
    }
    
    public static Line getLine(final Line.Info info) throws LineUnavailableException {
        LineUnavailableException ex = null;
        final List mixerProviders = getMixerProviders();
        try {
            final Mixer defaultMixer = getDefaultMixer(mixerProviders, info);
            if (defaultMixer != null && defaultMixer.isLineSupported(info)) {
                return defaultMixer.getLine(info);
            }
        }
        catch (final LineUnavailableException ex2) {
            ex = ex2;
        }
        catch (final IllegalArgumentException ex3) {}
        for (int i = 0; i < mixerProviders.size(); ++i) {
            final MixerProvider mixerProvider = mixerProviders.get(i);
            final Mixer.Info[] mixerInfo = mixerProvider.getMixerInfo();
            for (int j = 0; j < mixerInfo.length; ++j) {
                try {
                    final Mixer mixer = mixerProvider.getMixer(mixerInfo[j]);
                    if (isAppropriateMixer(mixer, info, true)) {
                        return mixer.getLine(info);
                    }
                }
                catch (final LineUnavailableException ex4) {
                    ex = ex4;
                }
                catch (final IllegalArgumentException ex5) {}
            }
        }
        for (int k = 0; k < mixerProviders.size(); ++k) {
            final MixerProvider mixerProvider2 = mixerProviders.get(k);
            final Mixer.Info[] mixerInfo2 = mixerProvider2.getMixerInfo();
            for (int l = 0; l < mixerInfo2.length; ++l) {
                try {
                    final Mixer mixer2 = mixerProvider2.getMixer(mixerInfo2[l]);
                    if (isAppropriateMixer(mixer2, info, false)) {
                        return mixer2.getLine(info);
                    }
                }
                catch (final LineUnavailableException ex6) {
                    ex = ex6;
                }
                catch (final IllegalArgumentException ex7) {}
            }
        }
        if (ex != null) {
            throw ex;
        }
        throw new IllegalArgumentException("No line matching " + info.toString() + " is supported.");
    }
    
    public static Clip getClip() throws LineUnavailableException {
        return (Clip)getLine(new DataLine.Info(Clip.class, new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0f, 16, 2, 4, -1.0f, true)));
    }
    
    public static Clip getClip(final Mixer.Info info) throws LineUnavailableException {
        return (Clip)getMixer(info).getLine(new DataLine.Info(Clip.class, new AudioFormat(AudioFormat.Encoding.PCM_SIGNED, -1.0f, 16, 2, 4, -1.0f, true)));
    }
    
    public static SourceDataLine getSourceDataLine(final AudioFormat audioFormat) throws LineUnavailableException {
        return (SourceDataLine)getLine(new DataLine.Info(SourceDataLine.class, audioFormat));
    }
    
    public static SourceDataLine getSourceDataLine(final AudioFormat audioFormat, final Mixer.Info info) throws LineUnavailableException {
        return (SourceDataLine)getMixer(info).getLine(new DataLine.Info(SourceDataLine.class, audioFormat));
    }
    
    public static TargetDataLine getTargetDataLine(final AudioFormat audioFormat) throws LineUnavailableException {
        return (TargetDataLine)getLine(new DataLine.Info(TargetDataLine.class, audioFormat));
    }
    
    public static TargetDataLine getTargetDataLine(final AudioFormat audioFormat, final Mixer.Info info) throws LineUnavailableException {
        return (TargetDataLine)getMixer(info).getLine(new DataLine.Info(TargetDataLine.class, audioFormat));
    }
    
    public static AudioFormat.Encoding[] getTargetEncodings(final AudioFormat.Encoding encoding) {
        final List formatConversionProviders = getFormatConversionProviders();
        final Vector vector = new Vector();
        for (int i = 0; i < formatConversionProviders.size(); ++i) {
            final FormatConversionProvider formatConversionProvider = formatConversionProviders.get(i);
            if (formatConversionProvider.isSourceEncodingSupported(encoding)) {
                final AudioFormat.Encoding[] targetEncodings = formatConversionProvider.getTargetEncodings();
                for (int j = 0; j < targetEncodings.length; ++j) {
                    vector.addElement(targetEncodings[j]);
                }
            }
        }
        return vector.toArray(new AudioFormat.Encoding[0]);
    }
    
    public static AudioFormat.Encoding[] getTargetEncodings(final AudioFormat audioFormat) {
        final List formatConversionProviders = getFormatConversionProviders();
        final Vector vector = new Vector();
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < formatConversionProviders.size(); ++i) {
            final AudioFormat.Encoding[] targetEncodings = formatConversionProviders.get(i).getTargetEncodings(audioFormat);
            n += targetEncodings.length;
            vector.addElement(targetEncodings);
        }
        final AudioFormat.Encoding[] array = new AudioFormat.Encoding[n];
        for (int j = 0; j < vector.size(); ++j) {
            final AudioFormat.Encoding[] array2 = vector.get(j);
            for (int k = 0; k < array2.length; ++k) {
                array[n2++] = array2[k];
            }
        }
        return array;
    }
    
    public static boolean isConversionSupported(final AudioFormat.Encoding encoding, final AudioFormat audioFormat) {
        final List formatConversionProviders = getFormatConversionProviders();
        for (int i = 0; i < formatConversionProviders.size(); ++i) {
            if (((FormatConversionProvider)formatConversionProviders.get(i)).isConversionSupported(encoding, audioFormat)) {
                return true;
            }
        }
        return false;
    }
    
    public static AudioInputStream getAudioInputStream(final AudioFormat.Encoding encoding, final AudioInputStream audioInputStream) {
        final List formatConversionProviders = getFormatConversionProviders();
        for (int i = 0; i < formatConversionProviders.size(); ++i) {
            final FormatConversionProvider formatConversionProvider = formatConversionProviders.get(i);
            if (formatConversionProvider.isConversionSupported(encoding, audioInputStream.getFormat())) {
                return formatConversionProvider.getAudioInputStream(encoding, audioInputStream);
            }
        }
        throw new IllegalArgumentException("Unsupported conversion: " + encoding + " from " + audioInputStream.getFormat());
    }
    
    public static AudioFormat[] getTargetFormats(final AudioFormat.Encoding encoding, final AudioFormat audioFormat) {
        final List formatConversionProviders = getFormatConversionProviders();
        final Vector vector = new Vector();
        int n = 0;
        int n2 = 0;
        for (int i = 0; i < formatConversionProviders.size(); ++i) {
            final AudioFormat[] targetFormats = formatConversionProviders.get(i).getTargetFormats(encoding, audioFormat);
            n += targetFormats.length;
            vector.addElement(targetFormats);
        }
        final AudioFormat[] array = new AudioFormat[n];
        for (int j = 0; j < vector.size(); ++j) {
            final AudioFormat[] array2 = vector.get(j);
            for (int k = 0; k < array2.length; ++k) {
                array[n2++] = array2[k];
            }
        }
        return array;
    }
    
    public static boolean isConversionSupported(final AudioFormat audioFormat, final AudioFormat audioFormat2) {
        final List formatConversionProviders = getFormatConversionProviders();
        for (int i = 0; i < formatConversionProviders.size(); ++i) {
            if (((FormatConversionProvider)formatConversionProviders.get(i)).isConversionSupported(audioFormat, audioFormat2)) {
                return true;
            }
        }
        return false;
    }
    
    public static AudioInputStream getAudioInputStream(final AudioFormat audioFormat, final AudioInputStream audioInputStream) {
        if (audioInputStream.getFormat().matches(audioFormat)) {
            return audioInputStream;
        }
        final List formatConversionProviders = getFormatConversionProviders();
        for (int i = 0; i < formatConversionProviders.size(); ++i) {
            final FormatConversionProvider formatConversionProvider = formatConversionProviders.get(i);
            if (formatConversionProvider.isConversionSupported(audioFormat, audioInputStream.getFormat())) {
                return formatConversionProvider.getAudioInputStream(audioFormat, audioInputStream);
            }
        }
        throw new IllegalArgumentException("Unsupported conversion: " + audioFormat + " from " + audioInputStream.getFormat());
    }
    
    public static AudioFileFormat getAudioFileFormat(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final List audioFileReaders = getAudioFileReaders();
        AudioFileFormat audioFileFormat = null;
        int i = 0;
        while (i < audioFileReaders.size()) {
            final AudioFileReader audioFileReader = audioFileReaders.get(i);
            try {
                audioFileFormat = audioFileReader.getAudioFileFormat(inputStream);
            }
            catch (final UnsupportedAudioFileException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (audioFileFormat == null) {
            throw new UnsupportedAudioFileException("file is not a supported file type");
        }
        return audioFileFormat;
    }
    
    public static AudioFileFormat getAudioFileFormat(final URL url) throws UnsupportedAudioFileException, IOException {
        final List audioFileReaders = getAudioFileReaders();
        AudioFileFormat audioFileFormat = null;
        int i = 0;
        while (i < audioFileReaders.size()) {
            final AudioFileReader audioFileReader = audioFileReaders.get(i);
            try {
                audioFileFormat = audioFileReader.getAudioFileFormat(url);
            }
            catch (final UnsupportedAudioFileException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (audioFileFormat == null) {
            throw new UnsupportedAudioFileException("file is not a supported file type");
        }
        return audioFileFormat;
    }
    
    public static AudioFileFormat getAudioFileFormat(final File file) throws UnsupportedAudioFileException, IOException {
        final List audioFileReaders = getAudioFileReaders();
        AudioFileFormat audioFileFormat = null;
        int i = 0;
        while (i < audioFileReaders.size()) {
            final AudioFileReader audioFileReader = audioFileReaders.get(i);
            try {
                audioFileFormat = audioFileReader.getAudioFileFormat(file);
            }
            catch (final UnsupportedAudioFileException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (audioFileFormat == null) {
            throw new UnsupportedAudioFileException("file is not a supported file type");
        }
        return audioFileFormat;
    }
    
    public static AudioInputStream getAudioInputStream(final InputStream inputStream) throws UnsupportedAudioFileException, IOException {
        final List audioFileReaders = getAudioFileReaders();
        AudioInputStream audioInputStream = null;
        int i = 0;
        while (i < audioFileReaders.size()) {
            final AudioFileReader audioFileReader = audioFileReaders.get(i);
            try {
                audioInputStream = audioFileReader.getAudioInputStream(inputStream);
            }
            catch (final UnsupportedAudioFileException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (audioInputStream == null) {
            throw new UnsupportedAudioFileException("could not get audio input stream from input stream");
        }
        return audioInputStream;
    }
    
    public static AudioInputStream getAudioInputStream(final URL url) throws UnsupportedAudioFileException, IOException {
        final List audioFileReaders = getAudioFileReaders();
        AudioInputStream audioInputStream = null;
        int i = 0;
        while (i < audioFileReaders.size()) {
            final AudioFileReader audioFileReader = audioFileReaders.get(i);
            try {
                audioInputStream = audioFileReader.getAudioInputStream(url);
            }
            catch (final UnsupportedAudioFileException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (audioInputStream == null) {
            throw new UnsupportedAudioFileException("could not get audio input stream from input URL");
        }
        return audioInputStream;
    }
    
    public static AudioInputStream getAudioInputStream(final File file) throws UnsupportedAudioFileException, IOException {
        final List audioFileReaders = getAudioFileReaders();
        AudioInputStream audioInputStream = null;
        int i = 0;
        while (i < audioFileReaders.size()) {
            final AudioFileReader audioFileReader = audioFileReaders.get(i);
            try {
                audioInputStream = audioFileReader.getAudioInputStream(file);
            }
            catch (final UnsupportedAudioFileException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (audioInputStream == null) {
            throw new UnsupportedAudioFileException("could not get audio input stream from input file");
        }
        return audioInputStream;
    }
    
    public static AudioFileFormat.Type[] getAudioFileTypes() {
        final List audioFileWriters = getAudioFileWriters();
        final HashSet set = new HashSet();
        for (int i = 0; i < audioFileWriters.size(); ++i) {
            final AudioFileFormat.Type[] audioFileTypes = audioFileWriters.get(i).getAudioFileTypes();
            for (int j = 0; j < audioFileTypes.length; ++j) {
                set.add(audioFileTypes[j]);
            }
        }
        return (AudioFileFormat.Type[])set.toArray(new AudioFileFormat.Type[0]);
    }
    
    public static boolean isFileTypeSupported(final AudioFileFormat.Type type) {
        final List audioFileWriters = getAudioFileWriters();
        for (int i = 0; i < audioFileWriters.size(); ++i) {
            if (((AudioFileWriter)audioFileWriters.get(i)).isFileTypeSupported(type)) {
                return true;
            }
        }
        return false;
    }
    
    public static AudioFileFormat.Type[] getAudioFileTypes(final AudioInputStream audioInputStream) {
        final List audioFileWriters = getAudioFileWriters();
        final HashSet set = new HashSet();
        for (int i = 0; i < audioFileWriters.size(); ++i) {
            final AudioFileFormat.Type[] audioFileTypes = audioFileWriters.get(i).getAudioFileTypes(audioInputStream);
            for (int j = 0; j < audioFileTypes.length; ++j) {
                set.add(audioFileTypes[j]);
            }
        }
        return (AudioFileFormat.Type[])set.toArray(new AudioFileFormat.Type[0]);
    }
    
    public static boolean isFileTypeSupported(final AudioFileFormat.Type type, final AudioInputStream audioInputStream) {
        final List audioFileWriters = getAudioFileWriters();
        for (int i = 0; i < audioFileWriters.size(); ++i) {
            if (((AudioFileWriter)audioFileWriters.get(i)).isFileTypeSupported(type, audioInputStream)) {
                return true;
            }
        }
        return false;
    }
    
    public static int write(final AudioInputStream audioInputStream, final AudioFileFormat.Type type, final OutputStream outputStream) throws IOException {
        final List audioFileWriters = getAudioFileWriters();
        int write = 0;
        boolean b = false;
        int i = 0;
        while (i < audioFileWriters.size()) {
            final AudioFileWriter audioFileWriter = audioFileWriters.get(i);
            try {
                write = audioFileWriter.write(audioInputStream, type, outputStream);
                b = true;
            }
            catch (final IllegalArgumentException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (!b) {
            throw new IllegalArgumentException("could not write audio file: file type not supported: " + type);
        }
        return write;
    }
    
    public static int write(final AudioInputStream audioInputStream, final AudioFileFormat.Type type, final File file) throws IOException {
        final List audioFileWriters = getAudioFileWriters();
        int write = 0;
        boolean b = false;
        int i = 0;
        while (i < audioFileWriters.size()) {
            final AudioFileWriter audioFileWriter = audioFileWriters.get(i);
            try {
                write = audioFileWriter.write(audioInputStream, type, file);
                b = true;
            }
            catch (final IllegalArgumentException ex) {
                ++i;
                continue;
            }
            break;
        }
        if (!b) {
            throw new IllegalArgumentException("could not write audio file: file type not supported: " + type);
        }
        return write;
    }
    
    private static List getMixerProviders() {
        return getProviders(MixerProvider.class);
    }
    
    private static List getFormatConversionProviders() {
        return getProviders(FormatConversionProvider.class);
    }
    
    private static List getAudioFileReaders() {
        return getProviders(AudioFileReader.class);
    }
    
    private static List getAudioFileWriters() {
        return getProviders(AudioFileWriter.class);
    }
    
    private static Mixer getDefaultMixer(final List list, final Line.Info info) {
        final Class<?> lineClass = info.getLineClass();
        final String defaultProviderClassName = JDK13Services.getDefaultProviderClassName(lineClass);
        final String defaultInstanceName = JDK13Services.getDefaultInstanceName(lineClass);
        if (defaultProviderClassName != null) {
            final MixerProvider namedProvider = getNamedProvider(defaultProviderClassName, list);
            if (namedProvider != null) {
                if (defaultInstanceName != null) {
                    final Mixer namedMixer = getNamedMixer(defaultInstanceName, namedProvider, info);
                    if (namedMixer != null) {
                        return namedMixer;
                    }
                }
                else {
                    final Mixer firstMixer = getFirstMixer(namedProvider, info, false);
                    if (firstMixer != null) {
                        return firstMixer;
                    }
                }
            }
        }
        if (defaultInstanceName != null) {
            final Mixer namedMixer2 = getNamedMixer(defaultInstanceName, list, info);
            if (namedMixer2 != null) {
                return namedMixer2;
            }
        }
        return null;
    }
    
    private static MixerProvider getNamedProvider(final String s, final List list) {
        for (int i = 0; i < list.size(); ++i) {
            final MixerProvider mixerProvider = list.get(i);
            if (mixerProvider.getClass().getName().equals(s)) {
                return mixerProvider;
            }
        }
        return null;
    }
    
    private static Mixer getNamedMixer(final String s, final MixerProvider mixerProvider, final Line.Info info) {
        final Mixer.Info[] mixerInfo = mixerProvider.getMixerInfo();
        for (int i = 0; i < mixerInfo.length; ++i) {
            if (mixerInfo[i].getName().equals(s)) {
                final Mixer mixer = mixerProvider.getMixer(mixerInfo[i]);
                if (isAppropriateMixer(mixer, info, false)) {
                    return mixer;
                }
            }
        }
        return null;
    }
    
    private static Mixer getNamedMixer(final String s, final List list, final Line.Info info) {
        for (int i = 0; i < list.size(); ++i) {
            final Mixer namedMixer = getNamedMixer(s, list.get(i), info);
            if (namedMixer != null) {
                return namedMixer;
            }
        }
        return null;
    }
    
    private static Mixer getFirstMixer(final MixerProvider mixerProvider, final Line.Info info, final boolean b) {
        final Mixer.Info[] mixerInfo = mixerProvider.getMixerInfo();
        for (int i = 0; i < mixerInfo.length; ++i) {
            final Mixer mixer = mixerProvider.getMixer(mixerInfo[i]);
            if (isAppropriateMixer(mixer, info, b)) {
                return mixer;
            }
        }
        return null;
    }
    
    private static boolean isAppropriateMixer(final Mixer mixer, final Line.Info info, final boolean b) {
        if (!mixer.isLineSupported(info)) {
            return false;
        }
        final Class<?> lineClass = info.getLineClass();
        if (b && (SourceDataLine.class.isAssignableFrom(lineClass) || Clip.class.isAssignableFrom(lineClass))) {
            final int maxLines = mixer.getMaxLines(info);
            return maxLines == -1 || maxLines > 1;
        }
        return true;
    }
    
    private static List getMixerInfoList() {
        return getMixerInfoList(getMixerProviders());
    }
    
    private static List getMixerInfoList(final List list) {
        final ArrayList list2 = new ArrayList();
        for (int i = 0; i < list.size(); ++i) {
            final Mixer.Info[] array = list.get(i).getMixerInfo();
            for (int j = 0; j < array.length; ++j) {
                list2.add(array[j]);
            }
        }
        return list2;
    }
    
    private static List getProviders(final Class clazz) {
        return JDK13Services.getProviders(clazz);
    }
}
