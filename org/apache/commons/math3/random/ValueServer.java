package org.apache.commons.math3.random;

import java.io.Reader;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import org.apache.commons.math3.exception.NullArgumentException;
import org.apache.commons.math3.exception.ZeroException;
import org.apache.commons.math3.exception.MathIllegalArgumentException;
import java.io.IOException;
import org.apache.commons.math3.exception.util.Localizable;
import org.apache.commons.math3.exception.MathIllegalStateException;
import org.apache.commons.math3.exception.util.LocalizedFormats;
import java.io.BufferedReader;
import java.net.URL;

public class ValueServer
{
    public static final int DIGEST_MODE = 0;
    public static final int REPLAY_MODE = 1;
    public static final int UNIFORM_MODE = 2;
    public static final int EXPONENTIAL_MODE = 3;
    public static final int GAUSSIAN_MODE = 4;
    public static final int CONSTANT_MODE = 5;
    private int mode;
    private URL valuesFileURL;
    private double mu;
    private double sigma;
    private EmpiricalDistribution empiricalDistribution;
    private BufferedReader filePointer;
    private final RandomDataGenerator randomData;
    
    public ValueServer() {
        this.mode = 5;
        this.valuesFileURL = null;
        this.mu = 0.0;
        this.sigma = 0.0;
        this.empiricalDistribution = null;
        this.filePointer = null;
        this.randomData = new RandomDataGenerator();
    }
    
    @Deprecated
    public ValueServer(final RandomDataImpl randomData) {
        this.mode = 5;
        this.valuesFileURL = null;
        this.mu = 0.0;
        this.sigma = 0.0;
        this.empiricalDistribution = null;
        this.filePointer = null;
        this.randomData = randomData.getDelegate();
    }
    
    public ValueServer(final RandomGenerator generator) {
        this.mode = 5;
        this.valuesFileURL = null;
        this.mu = 0.0;
        this.sigma = 0.0;
        this.empiricalDistribution = null;
        this.filePointer = null;
        this.randomData = new RandomDataGenerator(generator);
    }
    
    public double getNext() throws IOException, MathIllegalStateException, MathIllegalArgumentException {
        switch (this.mode) {
            case 0: {
                return this.getNextDigest();
            }
            case 1: {
                return this.getNextReplay();
            }
            case 2: {
                return this.getNextUniform();
            }
            case 3: {
                return this.getNextExponential();
            }
            case 4: {
                return this.getNextGaussian();
            }
            case 5: {
                return this.mu;
            }
            default: {
                throw new MathIllegalStateException(LocalizedFormats.UNKNOWN_MODE, new Object[] { this.mode, "DIGEST_MODE", 0, "REPLAY_MODE", 1, "UNIFORM_MODE", 2, "EXPONENTIAL_MODE", 3, "GAUSSIAN_MODE", 4, "CONSTANT_MODE", 5 });
            }
        }
    }
    
    public void fill(final double[] values) throws IOException, MathIllegalStateException, MathIllegalArgumentException {
        for (int i = 0; i < values.length; ++i) {
            values[i] = this.getNext();
        }
    }
    
    public double[] fill(final int length) throws IOException, MathIllegalStateException, MathIllegalArgumentException {
        final double[] out = new double[length];
        for (int i = 0; i < length; ++i) {
            out[i] = this.getNext();
        }
        return out;
    }
    
    public void computeDistribution() throws IOException, ZeroException, NullArgumentException {
        this.computeDistribution(1000);
    }
    
    public void computeDistribution(final int binCount) throws NullArgumentException, IOException, ZeroException {
        (this.empiricalDistribution = new EmpiricalDistribution(binCount, this.randomData.getRandomGenerator())).load(this.valuesFileURL);
        this.mu = this.empiricalDistribution.getSampleStats().getMean();
        this.sigma = this.empiricalDistribution.getSampleStats().getStandardDeviation();
    }
    
    public int getMode() {
        return this.mode;
    }
    
    public void setMode(final int mode) {
        this.mode = mode;
    }
    
    public URL getValuesFileURL() {
        return this.valuesFileURL;
    }
    
    public void setValuesFileURL(final String url) throws MalformedURLException {
        this.valuesFileURL = new URL(url);
    }
    
    public void setValuesFileURL(final URL url) {
        this.valuesFileURL = url;
    }
    
    public EmpiricalDistribution getEmpiricalDistribution() {
        return this.empiricalDistribution;
    }
    
    public void resetReplayFile() throws IOException {
        if (this.filePointer != null) {
            try {
                this.filePointer.close();
                this.filePointer = null;
            }
            catch (final IOException ex) {}
        }
        this.filePointer = new BufferedReader(new InputStreamReader(this.valuesFileURL.openStream(), "UTF-8"));
    }
    
    public void closeReplayFile() throws IOException {
        if (this.filePointer != null) {
            this.filePointer.close();
            this.filePointer = null;
        }
    }
    
    public double getMu() {
        return this.mu;
    }
    
    public void setMu(final double mu) {
        this.mu = mu;
    }
    
    public double getSigma() {
        return this.sigma;
    }
    
    public void setSigma(final double sigma) {
        this.sigma = sigma;
    }
    
    public void reSeed(final long seed) {
        this.randomData.reSeed(seed);
    }
    
    private double getNextDigest() throws MathIllegalStateException {
        if (this.empiricalDistribution == null || this.empiricalDistribution.getBinStats().size() == 0) {
            throw new MathIllegalStateException(LocalizedFormats.DIGEST_NOT_INITIALIZED, new Object[0]);
        }
        return this.empiricalDistribution.getNextValue();
    }
    
    private double getNextReplay() throws IOException, MathIllegalStateException {
        String str = null;
        if (this.filePointer == null) {
            this.resetReplayFile();
        }
        if ((str = this.filePointer.readLine()) == null) {
            this.closeReplayFile();
            this.resetReplayFile();
            if ((str = this.filePointer.readLine()) == null) {
                throw new MathIllegalStateException(LocalizedFormats.URL_CONTAINS_NO_DATA, new Object[] { this.valuesFileURL });
            }
        }
        return Double.parseDouble(str);
    }
    
    private double getNextUniform() throws MathIllegalArgumentException {
        return this.randomData.nextUniform(0.0, 2.0 * this.mu);
    }
    
    private double getNextExponential() throws MathIllegalArgumentException {
        return this.randomData.nextExponential(this.mu);
    }
    
    private double getNextGaussian() throws MathIllegalArgumentException {
        return this.randomData.nextGaussian(this.mu, this.sigma);
    }
}
