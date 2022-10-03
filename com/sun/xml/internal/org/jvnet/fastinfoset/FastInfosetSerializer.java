package com.sun.xml.internal.org.jvnet.fastinfoset;

import java.io.OutputStream;
import java.util.Map;

public interface FastInfosetSerializer
{
    public static final String IGNORE_DTD_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/DTD";
    public static final String IGNORE_COMMENTS_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/comments";
    public static final String IGNORE_PROCESSING_INSTRUCTIONS_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/processingInstructions";
    public static final String IGNORE_WHITE_SPACE_TEXT_CONTENT_FEATURE = "http://jvnet.org/fastinfoset/serializer/feature/ignore/whiteSpaceTextContent";
    public static final String BUFFER_SIZE_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/buffer-size";
    public static final String REGISTERED_ENCODING_ALGORITHMS_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/registered-encoding-algorithms";
    public static final String EXTERNAL_VOCABULARIES_PROPERTY = "http://jvnet.org/fastinfoset/parser/properties/external-vocabularies";
    public static final int MIN_CHARACTER_CONTENT_CHUNK_SIZE = 0;
    public static final int MAX_CHARACTER_CONTENT_CHUNK_SIZE = 32;
    public static final int CHARACTER_CONTENT_CHUNK_MAP_MEMORY_CONSTRAINT = Integer.MAX_VALUE;
    public static final int MIN_ATTRIBUTE_VALUE_SIZE = 0;
    public static final int MAX_ATTRIBUTE_VALUE_SIZE = 32;
    public static final int ATTRIBUTE_VALUE_MAP_MEMORY_CONSTRAINT = Integer.MAX_VALUE;
    public static final String UTF_8 = "UTF-8";
    public static final String UTF_16BE = "UTF-16BE";
    
    void setIgnoreDTD(final boolean p0);
    
    boolean getIgnoreDTD();
    
    void setIgnoreComments(final boolean p0);
    
    boolean getIgnoreComments();
    
    void setIgnoreProcesingInstructions(final boolean p0);
    
    boolean getIgnoreProcesingInstructions();
    
    void setIgnoreWhiteSpaceTextContent(final boolean p0);
    
    boolean getIgnoreWhiteSpaceTextContent();
    
    void setCharacterEncodingScheme(final String p0);
    
    String getCharacterEncodingScheme();
    
    void setRegisteredEncodingAlgorithms(final Map p0);
    
    Map getRegisteredEncodingAlgorithms();
    
    int getMinCharacterContentChunkSize();
    
    void setMinCharacterContentChunkSize(final int p0);
    
    int getMaxCharacterContentChunkSize();
    
    void setMaxCharacterContentChunkSize(final int p0);
    
    int getCharacterContentChunkMapMemoryLimit();
    
    void setCharacterContentChunkMapMemoryLimit(final int p0);
    
    int getMinAttributeValueSize();
    
    void setMinAttributeValueSize(final int p0);
    
    int getMaxAttributeValueSize();
    
    void setMaxAttributeValueSize(final int p0);
    
    int getAttributeValueMapMemoryLimit();
    
    void setAttributeValueMapMemoryLimit(final int p0);
    
    void setExternalVocabulary(final ExternalVocabulary p0);
    
    void setVocabularyApplicationData(final VocabularyApplicationData p0);
    
    VocabularyApplicationData getVocabularyApplicationData();
    
    void reset();
    
    void setOutputStream(final OutputStream p0);
}
