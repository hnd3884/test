package org.apache.poi.openxml4j.opc;

import java.util.Date;
import java.util.Optional;

public interface PackageProperties
{
    public static final String NAMESPACE_DCTERMS = "http://purl.org/dc/terms/";
    public static final String NAMESPACE_DC = "http://purl.org/dc/elements/1.1/";
    
    Optional<String> getCategoryProperty();
    
    void setCategoryProperty(final String p0);
    
    void setCategoryProperty(final Optional<String> p0);
    
    Optional<String> getContentStatusProperty();
    
    void setContentStatusProperty(final String p0);
    
    void setContentStatusProperty(final Optional<String> p0);
    
    Optional<String> getContentTypeProperty();
    
    void setContentTypeProperty(final String p0);
    
    void setContentTypeProperty(final Optional<String> p0);
    
    Optional<Date> getCreatedProperty();
    
    void setCreatedProperty(final String p0);
    
    void setCreatedProperty(final Optional<Date> p0);
    
    Optional<String> getCreatorProperty();
    
    void setCreatorProperty(final String p0);
    
    void setCreatorProperty(final Optional<String> p0);
    
    Optional<String> getDescriptionProperty();
    
    void setDescriptionProperty(final String p0);
    
    void setDescriptionProperty(final Optional<String> p0);
    
    Optional<String> getIdentifierProperty();
    
    void setIdentifierProperty(final String p0);
    
    void setIdentifierProperty(final Optional<String> p0);
    
    Optional<String> getKeywordsProperty();
    
    void setKeywordsProperty(final String p0);
    
    void setKeywordsProperty(final Optional<String> p0);
    
    Optional<String> getLanguageProperty();
    
    void setLanguageProperty(final String p0);
    
    void setLanguageProperty(final Optional<String> p0);
    
    Optional<String> getLastModifiedByProperty();
    
    void setLastModifiedByProperty(final String p0);
    
    void setLastModifiedByProperty(final Optional<String> p0);
    
    Optional<Date> getLastPrintedProperty();
    
    void setLastPrintedProperty(final String p0);
    
    void setLastPrintedProperty(final Optional<Date> p0);
    
    Optional<Date> getModifiedProperty();
    
    void setModifiedProperty(final String p0);
    
    void setModifiedProperty(final Optional<Date> p0);
    
    Optional<String> getRevisionProperty();
    
    void setRevisionProperty(final String p0);
    
    void setRevisionProperty(final Optional<String> p0);
    
    Optional<String> getSubjectProperty();
    
    void setSubjectProperty(final String p0);
    
    void setSubjectProperty(final Optional<String> p0);
    
    Optional<String> getTitleProperty();
    
    void setTitleProperty(final String p0);
    
    void setTitleProperty(final Optional<String> p0);
    
    Optional<String> getVersionProperty();
    
    void setVersionProperty(final String p0);
    
    void setVersionProperty(final Optional<String> p0);
}
