package com.sun.xml.internal.org.jvnet.fastinfoset;

public class ExternalVocabulary
{
    public final String URI;
    public final Vocabulary vocabulary;
    
    public ExternalVocabulary(final String URI, final Vocabulary vocabulary) {
        if (URI == null || vocabulary == null) {
            throw new IllegalArgumentException();
        }
        this.URI = URI;
        this.vocabulary = vocabulary;
    }
}
