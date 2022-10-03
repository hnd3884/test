package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.util.LinkedHashSet;
import java.util.Set;

public class Vocabulary
{
    public final Set restrictedAlphabets;
    public final Set encodingAlgorithms;
    public final Set prefixes;
    public final Set namespaceNames;
    public final Set localNames;
    public final Set otherNCNames;
    public final Set otherURIs;
    public final Set attributeValues;
    public final Set otherStrings;
    public final Set characterContentChunks;
    public final Set elements;
    public final Set attributes;
    
    public Vocabulary() {
        this.restrictedAlphabets = new LinkedHashSet();
        this.encodingAlgorithms = new LinkedHashSet();
        this.prefixes = new LinkedHashSet();
        this.namespaceNames = new LinkedHashSet();
        this.localNames = new LinkedHashSet();
        this.otherNCNames = new LinkedHashSet();
        this.otherURIs = new LinkedHashSet();
        this.attributeValues = new LinkedHashSet();
        this.otherStrings = new LinkedHashSet();
        this.characterContentChunks = new LinkedHashSet();
        this.elements = new LinkedHashSet();
        this.attributes = new LinkedHashSet();
    }
}
