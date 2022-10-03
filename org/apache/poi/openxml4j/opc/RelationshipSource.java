package org.apache.poi.openxml4j.opc;

import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;

public interface RelationshipSource
{
    PackageRelationship addRelationship(final PackagePartName p0, final TargetMode p1, final String p2);
    
    PackageRelationship addRelationship(final PackagePartName p0, final TargetMode p1, final String p2, final String p3);
    
    PackageRelationship addExternalRelationship(final String p0, final String p1);
    
    PackageRelationship addExternalRelationship(final String p0, final String p1, final String p2);
    
    void clearRelationships();
    
    void removeRelationship(final String p0);
    
    PackageRelationshipCollection getRelationships() throws InvalidFormatException, OpenXML4JException;
    
    PackageRelationship getRelationship(final String p0);
    
    PackageRelationshipCollection getRelationshipsByType(final String p0) throws InvalidFormatException, IllegalArgumentException, OpenXML4JException;
    
    boolean hasRelationships();
    
    boolean isRelationshipExists(final PackageRelationship p0);
}
