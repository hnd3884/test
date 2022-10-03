package org.apache.tika.detect;

import java.io.File;
import java.io.Writer;
import java.nio.file.Path;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.nio.charset.StandardCharsets;
import org.apache.tika.io.TemporaryResources;
import java.nio.channels.ReadableByteChannel;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.io.IOException;
import java.util.Iterator;
import org.apache.tika.metadata.Metadata;
import java.io.InputStream;
import java.util.HashMap;
import org.apache.tika.mime.MediaType;
import java.util.Map;

public abstract class TrainedModelDetector implements Detector
{
    private static final long serialVersionUID = 1L;
    private final Map<MediaType, TrainedModel> MODEL_MAP;
    
    public TrainedModelDetector() {
        this.MODEL_MAP = new HashMap<MediaType, TrainedModel>();
        this.loadDefaultModels(this.getClass().getClassLoader());
    }
    
    public int getMinLength() {
        return Integer.MAX_VALUE;
    }
    
    @Override
    public MediaType detect(final InputStream input, final Metadata metadata) throws IOException {
        if (input != null) {
            input.mark(this.getMinLength());
            final float[] histogram = this.readByteFrequencies(input);
            final Iterator<MediaType> iter = this.MODEL_MAP.keySet().iterator();
            float maxprob;
            final float threshold = maxprob = 0.5f;
            MediaType maxType = MediaType.OCTET_STREAM;
            while (iter.hasNext()) {
                final MediaType key = iter.next();
                final TrainedModel model = this.MODEL_MAP.get(key);
                final float prob = model.predict(histogram);
                if (maxprob < prob) {
                    maxprob = prob;
                    maxType = key;
                }
            }
            input.reset();
            return maxType;
        }
        return null;
    }
    
    protected float[] readByteFrequencies(final InputStream input) throws IOException {
        final ReadableByteChannel inputChannel = Channels.newChannel(input);
        final float[] histogram = new float[257];
        histogram[0] = 1.0f;
        final ByteBuffer buf = ByteBuffer.allocate(5120);
        int bytesRead = inputChannel.read(buf);
        float max = -1.0f;
        while (bytesRead != -1) {
            buf.flip();
            while (buf.hasRemaining()) {
                int idx;
                final byte byt = (byte)(idx = buf.get());
                ++idx;
                if (byt < 0) {
                    idx += 256;
                    final float[] array = histogram;
                    final int n = idx;
                    ++array[n];
                }
                else {
                    final float[] array2 = histogram;
                    final int n2 = idx;
                    ++array2[n2];
                }
                max = Math.max(max, histogram[idx]);
            }
            buf.clear();
            bytesRead = inputChannel.read(buf);
        }
        for (int i = 1; i < histogram.length; ++i) {
            final float[] array3 = histogram;
            final int n3 = i;
            array3[n3] /= max;
            histogram[i] = (float)Math.sqrt(histogram[i]);
        }
        return histogram;
    }
    
    private void writeHisto(final float[] histogram) throws IOException {
        final Path histPath = new TemporaryResources().createTempFile();
        try (final Writer writer = Files.newBufferedWriter(histPath, StandardCharsets.UTF_8, new OpenOption[0])) {
            for (final float bin : histogram) {
                writer.write(bin + "\t");
            }
            writer.write("\r\n");
        }
    }
    
    public void loadDefaultModels(final Path modelFile) {
        try (final InputStream in = Files.newInputStream(modelFile, new OpenOption[0])) {
            this.loadDefaultModels(in);
        }
        catch (final IOException e) {
            throw new RuntimeException("Unable to read the default media type registry", e);
        }
    }
    
    public void loadDefaultModels(final File modelFile) {
        this.loadDefaultModels(modelFile.toPath());
    }
    
    public abstract void loadDefaultModels(final InputStream p0);
    
    public abstract void loadDefaultModels(final ClassLoader p0);
    
    protected void registerModels(final MediaType type, final TrainedModel model) {
        this.MODEL_MAP.put(type, model);
    }
}
