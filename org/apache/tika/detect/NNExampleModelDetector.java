package org.apache.tika.detect;

import org.slf4j.LoggerFactory;
import org.apache.tika.mime.MediaType;
import java.net.URL;
import java.util.Objects;
import java.io.IOException;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.io.InputStream;
import java.io.File;
import java.nio.file.Path;
import org.slf4j.Logger;

public class NNExampleModelDetector extends TrainedModelDetector
{
    private static final String EXAMPLE_NNMODEL_FILE = "tika-example.nnmodel";
    private static final long serialVersionUID = 1L;
    private static final Logger LOG;
    
    public NNExampleModelDetector() {
    }
    
    public NNExampleModelDetector(final Path modelFile) {
        this.loadDefaultModels(modelFile);
    }
    
    public NNExampleModelDetector(final File modelFile) {
        this.loadDefaultModels(modelFile);
    }
    
    @Override
    public void loadDefaultModels(final InputStream modelStream) {
        final BufferedReader bReader = new BufferedReader(new InputStreamReader(modelStream, StandardCharsets.UTF_8));
        final NNTrainedModelBuilder nnBuilder = new NNTrainedModelBuilder();
        try {
            String line;
            while ((line = bReader.readLine()) != null) {
                line = line.trim();
                if (line.startsWith("#")) {
                    this.readDescription(nnBuilder, line);
                }
                else {
                    this.readNNParams(nnBuilder, line);
                    super.registerModels(nnBuilder.getType(), nnBuilder.build());
                }
            }
        }
        catch (final IOException e) {
            throw new RuntimeException("Unable to read the default media type registry", e);
        }
    }
    
    @Override
    public void loadDefaultModels(ClassLoader classLoader) {
        if (classLoader == null) {
            classLoader = TrainedModelDetector.class.getClassLoader();
        }
        final String classPrefix = TrainedModelDetector.class.getPackage().getName().replace('.', '/') + "/";
        final URL modelURL = classLoader.getResource(classPrefix + "tika-example.nnmodel");
        Objects.requireNonNull(modelURL, "required resource " + classPrefix + "tika-example.nnmodel" + " not found");
        try (final InputStream stream = modelURL.openStream()) {
            this.loadDefaultModels(stream);
        }
        catch (final IOException e) {
            throw new RuntimeException("Unable to read the default media type registry", e);
        }
    }
    
    private void readDescription(final NNTrainedModelBuilder builder, final String line) {
        final String[] sarr = line.split("\t");
        try {
            final MediaType type = MediaType.parse(sarr[1]);
            final int numInputs = Integer.parseInt(sarr[2]);
            final int numHidden = Integer.parseInt(sarr[3]);
            final int numOutputs = Integer.parseInt(sarr[4]);
            builder.setNumOfInputs(numInputs);
            builder.setNumOfHidden(numHidden);
            builder.setNumOfOutputs(numOutputs);
            builder.setType(type);
        }
        catch (final Exception e) {
            NNExampleModelDetector.LOG.warn("Unable to parse the model configuration", (Throwable)e);
            throw new RuntimeException("Unable to parse the model configuration", e);
        }
    }
    
    private void readNNParams(final NNTrainedModelBuilder builder, final String line) {
        final String[] sarr = line.split("\t");
        final int n = sarr.length;
        final float[] params = new float[n];
        try {
            int i = 0;
            for (final String fstr : sarr) {
                params[i] = Float.parseFloat(fstr);
                ++i;
            }
            builder.setParams(params);
        }
        catch (final Exception e) {
            NNExampleModelDetector.LOG.warn("Unable to parse the model configuration", (Throwable)e);
            throw new RuntimeException("Unable to parse the model configuration", e);
        }
    }
    
    static {
        LOG = LoggerFactory.getLogger((Class)NNExampleModelDetector.class);
    }
}
