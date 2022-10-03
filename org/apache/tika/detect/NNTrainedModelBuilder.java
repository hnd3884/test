package org.apache.tika.detect;

import org.apache.tika.mime.MediaType;

public class NNTrainedModelBuilder
{
    private MediaType type;
    private int numOfInputs;
    private int numOfHidden;
    private int numOfOutputs;
    private float[] params;
    
    public MediaType getType() {
        return this.type;
    }
    
    public void setType(final MediaType type) {
        this.type = type;
    }
    
    public int getNumOfInputs() {
        return this.numOfInputs;
    }
    
    public void setNumOfInputs(final int numOfInputs) {
        this.numOfInputs = numOfInputs;
    }
    
    public int getNumOfHidden() {
        return this.numOfHidden;
    }
    
    public void setNumOfHidden(final int numOfHidden) {
        this.numOfHidden = numOfHidden;
    }
    
    public int getNumOfOutputs() {
        return this.numOfOutputs;
    }
    
    public void setNumOfOutputs(final int numOfOutputs) {
        this.numOfOutputs = numOfOutputs;
    }
    
    public float[] getParams() {
        return this.params;
    }
    
    public void setParams(final float[] params) {
        this.params = params;
    }
    
    public NNTrainedModel build() {
        return new NNTrainedModel(this.numOfInputs, this.numOfHidden, this.numOfOutputs, this.params);
    }
}
