package com.google.zxing.client.j2se;

import java.util.ArrayList;
import java.util.List;

final class Inputs
{
    private final List<String> inputs;
    private int position;
    
    Inputs() {
        this.inputs = new ArrayList<String>(10);
        this.position = 0;
    }
    
    public synchronized void addInput(final String pathOrUrl) {
        this.inputs.add(pathOrUrl);
    }
    
    public synchronized String getNextInput() {
        if (this.position < this.inputs.size()) {
            final String result = this.inputs.get(this.position);
            ++this.position;
            return result;
        }
        return null;
    }
    
    public synchronized int getInputCount() {
        return this.inputs.size();
    }
}
