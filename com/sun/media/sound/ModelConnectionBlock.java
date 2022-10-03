package com.sun.media.sound;

import java.util.Arrays;

public final class ModelConnectionBlock
{
    private static final ModelSource[] no_sources;
    private ModelSource[] sources;
    private double scale;
    private ModelDestination destination;
    
    public ModelConnectionBlock() {
        this.sources = ModelConnectionBlock.no_sources;
        this.scale = 1.0;
    }
    
    public ModelConnectionBlock(final double scale, final ModelDestination destination) {
        this.sources = ModelConnectionBlock.no_sources;
        this.scale = 1.0;
        this.scale = scale;
        this.destination = destination;
    }
    
    public ModelConnectionBlock(final ModelSource modelSource, final ModelDestination destination) {
        this.sources = ModelConnectionBlock.no_sources;
        this.scale = 1.0;
        if (modelSource != null) {
            (this.sources = new ModelSource[1])[0] = modelSource;
        }
        this.destination = destination;
    }
    
    public ModelConnectionBlock(final ModelSource modelSource, final double scale, final ModelDestination destination) {
        this.sources = ModelConnectionBlock.no_sources;
        this.scale = 1.0;
        if (modelSource != null) {
            (this.sources = new ModelSource[1])[0] = modelSource;
        }
        this.scale = scale;
        this.destination = destination;
    }
    
    public ModelConnectionBlock(final ModelSource modelSource, final ModelSource modelSource2, final ModelDestination destination) {
        this.sources = ModelConnectionBlock.no_sources;
        this.scale = 1.0;
        if (modelSource != null) {
            if (modelSource2 == null) {
                (this.sources = new ModelSource[1])[0] = modelSource;
            }
            else {
                (this.sources = new ModelSource[2])[0] = modelSource;
                this.sources[1] = modelSource2;
            }
        }
        this.destination = destination;
    }
    
    public ModelConnectionBlock(final ModelSource modelSource, final ModelSource modelSource2, final double scale, final ModelDestination destination) {
        this.sources = ModelConnectionBlock.no_sources;
        this.scale = 1.0;
        if (modelSource != null) {
            if (modelSource2 == null) {
                (this.sources = new ModelSource[1])[0] = modelSource;
            }
            else {
                (this.sources = new ModelSource[2])[0] = modelSource;
                this.sources[1] = modelSource2;
            }
        }
        this.scale = scale;
        this.destination = destination;
    }
    
    public ModelDestination getDestination() {
        return this.destination;
    }
    
    public void setDestination(final ModelDestination destination) {
        this.destination = destination;
    }
    
    public double getScale() {
        return this.scale;
    }
    
    public void setScale(final double scale) {
        this.scale = scale;
    }
    
    public ModelSource[] getSources() {
        return Arrays.copyOf(this.sources, this.sources.length);
    }
    
    public void setSources(final ModelSource[] array) {
        this.sources = ((array == null) ? ModelConnectionBlock.no_sources : Arrays.copyOf(array, array.length));
    }
    
    public void addSource(final ModelSource modelSource) {
        final ModelSource[] sources = this.sources;
        System.arraycopy(sources, 0, this.sources = new ModelSource[sources.length + 1], 0, sources.length);
        this.sources[this.sources.length - 1] = modelSource;
    }
    
    static {
        no_sources = new ModelSource[0];
    }
}
