package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.util.Map;

public interface FastInfosetParser
{
    public static final String STRING_INTERNING_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/string-interning";
    public static final String BUFFER_SIZE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/buffer-size";
    public static final String REGISTERED_ENCODING_ALGORITHMS_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
    public static final String EXTERNAL_VOCABULARIES_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
    public static final String FORCE_STREAM_CLOSE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/force-stream-close";
    
    void setStringInterning(final boolean p0);
    
    boolean getStringInterning();
    
    void setBufferSize(final int p0);
    
    int getBufferSize();
    
    void setRegisteredEncodingAlgorithms(final Map p0);
    
    Map getRegisteredEncodingAlgorithms();
    
    void setExternalVocabularies(final Map p0);
    
    @Deprecated
    Map getExternalVocabularies();
    
    void setParseFragments(final boolean p0);
    
    boolean getParseFragments();
    
    void setForceStreamClose(final boolean p0);
    
    boolean getForceStreamClose();
}
