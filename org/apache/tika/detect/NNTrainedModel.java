package org.apache.tika.detect;

public class NNTrainedModel extends TrainedModel
{
    private final int numOfInputs;
    private final int numOfHidden;
    private final int numOfOutputs;
    private final float[][] Theta1;
    private final float[][] Theta2;
    
    public NNTrainedModel(final int nInput, final int nHidden, final int nOutput, final float[] nn_params) {
        this.numOfInputs = nInput;
        this.numOfHidden = nHidden;
        this.numOfOutputs = nOutput;
        this.Theta1 = new float[this.numOfHidden][this.numOfInputs + 1];
        this.Theta2 = new float[this.numOfOutputs][this.numOfHidden + 1];
        this.populateThetas(nn_params);
    }
    
    private void populateThetas(final float[] nn_params) {
        int m = this.Theta1.length;
        int n = this.Theta1[0].length;
        int k = 0;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                this.Theta1[j][i] = nn_params[k];
                ++k;
            }
        }
        m = this.Theta2.length;
        n = this.Theta2[0].length;
        for (int i = 0; i < n; ++i) {
            for (int j = 0; j < m; ++j) {
                this.Theta2[j][i] = nn_params[k];
                ++k;
            }
        }
    }
    
    @Override
    public double predict(final double[] unseen) {
        return 0.0;
    }
    
    @Override
    public float predict(final float[] unseen) {
        int m = this.Theta1.length;
        int n = this.Theta1[0].length;
        final float[] hh = new float[m + 1];
        hh[0] = 1.0f;
        for (int i = 0; i < m; ++i) {
            double h = 0.0;
            for (int j = 0; j < n; ++j) {
                h += this.Theta1[i][j] * unseen[j];
            }
            h = 1.0 / (1.0 + Math.exp(-h));
            hh[i + 1] = (float)h;
        }
        m = this.Theta2.length;
        n = this.Theta2[0].length;
        final float[] oo = new float[m];
        for (int i = 0; i < m; ++i) {
            double o = 0.0;
            for (int j = 0; j < n; ++j) {
                o += this.Theta2[i][j] * hh[j];
            }
            o = 1.0 / (1.0 + Math.exp(-o));
            oo[i] = (float)o;
        }
        return oo[0];
    }
}
