package com.sun.media.sound;

public final class AudioSynthesizerPropertyInfo
{
    public String name;
    public String description;
    public Object value;
    public Class valueClass;
    public Object[] choices;
    
    public AudioSynthesizerPropertyInfo(final String name, final Object value) {
        this.description = null;
        this.value = null;
        this.valueClass = null;
        this.choices = null;
        this.name = name;
        if (value instanceof Class) {
            this.valueClass = (Class)value;
        }
        else if ((this.value = value) != null) {
            this.valueClass = value.getClass();
        }
    }
}
