package com.sun.org.apache.xerces.internal.util;

import java.util.Collection;
import java.util.Arrays;
import com.sun.org.apache.xerces.internal.xni.parser.XMLConfigurationException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import com.sun.org.apache.xerces.internal.xni.parser.XMLComponentManager;

public class ParserConfigurationSettings implements XMLComponentManager
{
    protected static final String PARSER_SETTINGS = "http://apache.org/xml/features/internal/parser-settings";
    protected Set<String> fRecognizedProperties;
    protected Map<String, Object> fProperties;
    protected Set<String> fRecognizedFeatures;
    protected Map<String, Boolean> fFeatures;
    protected XMLComponentManager fParentSettings;
    
    public ParserConfigurationSettings() {
        this(null);
    }
    
    public ParserConfigurationSettings(final XMLComponentManager parent) {
        this.fRecognizedFeatures = new HashSet<String>();
        this.fRecognizedProperties = new HashSet<String>();
        this.fFeatures = new HashMap<String, Boolean>();
        this.fProperties = new HashMap<String, Object>();
        this.fParentSettings = parent;
    }
    
    public void addRecognizedFeatures(final String[] featureIds) {
        for (int featureIdsCount = (featureIds != null) ? featureIds.length : 0, i = 0; i < featureIdsCount; ++i) {
            final String featureId = featureIds[i];
            if (!this.fRecognizedFeatures.contains(featureId)) {
                this.fRecognizedFeatures.add(featureId);
            }
        }
    }
    
    public void setFeature(final String featureId, final boolean state) throws XMLConfigurationException {
        final FeatureState checkState = this.checkFeature(featureId);
        if (checkState.isExceptional()) {
            throw new XMLConfigurationException(checkState.status, featureId);
        }
        this.fFeatures.put(featureId, state);
    }
    
    public void addRecognizedProperties(final String[] propertyIds) {
        this.fRecognizedProperties.addAll(Arrays.asList(propertyIds));
    }
    
    public void setProperty(final String propertyId, final Object value) throws XMLConfigurationException {
        final PropertyState checkState = this.checkProperty(propertyId);
        if (checkState.isExceptional()) {
            throw new XMLConfigurationException(checkState.status, propertyId);
        }
        this.fProperties.put(propertyId, value);
    }
    
    @Override
    public final boolean getFeature(final String featureId) throws XMLConfigurationException {
        final FeatureState state = this.getFeatureState(featureId);
        if (state.isExceptional()) {
            throw new XMLConfigurationException(state.status, featureId);
        }
        return state.state;
    }
    
    @Override
    public final boolean getFeature(final String featureId, final boolean defaultValue) {
        final FeatureState state = this.getFeatureState(featureId);
        if (state.isExceptional()) {
            return defaultValue;
        }
        return state.state;
    }
    
    @Override
    public FeatureState getFeatureState(final String featureId) {
        final Boolean state = this.fFeatures.get(featureId);
        if (state != null) {
            return FeatureState.is(state);
        }
        final FeatureState checkState = this.checkFeature(featureId);
        if (checkState.isExceptional()) {
            return checkState;
        }
        return FeatureState.is(false);
    }
    
    @Override
    public final Object getProperty(final String propertyId) throws XMLConfigurationException {
        final PropertyState state = this.getPropertyState(propertyId);
        if (state.isExceptional()) {
            throw new XMLConfigurationException(state.status, propertyId);
        }
        return state.state;
    }
    
    @Override
    public final Object getProperty(final String propertyId, final Object defaultValue) {
        final PropertyState state = this.getPropertyState(propertyId);
        if (state.isExceptional()) {
            return defaultValue;
        }
        return state.state;
    }
    
    @Override
    public PropertyState getPropertyState(final String propertyId) {
        final Object propertyValue = this.fProperties.get(propertyId);
        if (propertyValue == null) {
            final PropertyState state = this.checkProperty(propertyId);
            if (state.isExceptional()) {
                return state;
            }
        }
        return PropertyState.is(propertyValue);
    }
    
    protected FeatureState checkFeature(final String featureId) throws XMLConfigurationException {
        if (this.fRecognizedFeatures.contains(featureId)) {
            return FeatureState.RECOGNIZED;
        }
        if (this.fParentSettings != null) {
            return this.fParentSettings.getFeatureState(featureId);
        }
        return FeatureState.NOT_RECOGNIZED;
    }
    
    protected PropertyState checkProperty(final String propertyId) throws XMLConfigurationException {
        if (!this.fRecognizedProperties.contains(propertyId)) {
            if (this.fParentSettings == null) {
                return PropertyState.NOT_RECOGNIZED;
            }
            final PropertyState state = this.fParentSettings.getPropertyState(propertyId);
            if (state.isExceptional()) {
                return state;
            }
        }
        return PropertyState.RECOGNIZED;
    }
}
