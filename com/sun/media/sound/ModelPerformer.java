package com.sun.media.sound;

import java.util.ArrayList;
import java.util.List;

public final class ModelPerformer
{
    private final List<ModelOscillator> oscillators;
    private List<ModelConnectionBlock> connectionBlocks;
    private int keyFrom;
    private int keyTo;
    private int velFrom;
    private int velTo;
    private int exclusiveClass;
    private boolean releaseTrigger;
    private boolean selfNonExclusive;
    private Object userObject;
    private boolean addDefaultConnections;
    private String name;
    
    public ModelPerformer() {
        this.oscillators = new ArrayList<ModelOscillator>();
        this.connectionBlocks = new ArrayList<ModelConnectionBlock>();
        this.keyFrom = 0;
        this.keyTo = 127;
        this.velFrom = 0;
        this.velTo = 127;
        this.exclusiveClass = 0;
        this.releaseTrigger = false;
        this.selfNonExclusive = false;
        this.userObject = null;
        this.addDefaultConnections = true;
        this.name = null;
    }
    
    public String getName() {
        return this.name;
    }
    
    public void setName(final String name) {
        this.name = name;
    }
    
    public List<ModelConnectionBlock> getConnectionBlocks() {
        return this.connectionBlocks;
    }
    
    public void setConnectionBlocks(final List<ModelConnectionBlock> connectionBlocks) {
        this.connectionBlocks = connectionBlocks;
    }
    
    public List<ModelOscillator> getOscillators() {
        return this.oscillators;
    }
    
    public int getExclusiveClass() {
        return this.exclusiveClass;
    }
    
    public void setExclusiveClass(final int exclusiveClass) {
        this.exclusiveClass = exclusiveClass;
    }
    
    public boolean isSelfNonExclusive() {
        return this.selfNonExclusive;
    }
    
    public void setSelfNonExclusive(final boolean selfNonExclusive) {
        this.selfNonExclusive = selfNonExclusive;
    }
    
    public int getKeyFrom() {
        return this.keyFrom;
    }
    
    public void setKeyFrom(final int keyFrom) {
        this.keyFrom = keyFrom;
    }
    
    public int getKeyTo() {
        return this.keyTo;
    }
    
    public void setKeyTo(final int keyTo) {
        this.keyTo = keyTo;
    }
    
    public int getVelFrom() {
        return this.velFrom;
    }
    
    public void setVelFrom(final int velFrom) {
        this.velFrom = velFrom;
    }
    
    public int getVelTo() {
        return this.velTo;
    }
    
    public void setVelTo(final int velTo) {
        this.velTo = velTo;
    }
    
    public boolean isReleaseTriggered() {
        return this.releaseTrigger;
    }
    
    public void setReleaseTriggered(final boolean releaseTrigger) {
        this.releaseTrigger = releaseTrigger;
    }
    
    public Object getUserObject() {
        return this.userObject;
    }
    
    public void setUserObject(final Object userObject) {
        this.userObject = userObject;
    }
    
    public boolean isDefaultConnectionsEnabled() {
        return this.addDefaultConnections;
    }
    
    public void setDefaultConnectionsEnabled(final boolean addDefaultConnections) {
        this.addDefaultConnections = addDefaultConnections;
    }
}
