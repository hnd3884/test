package com.adventnet.db.persistence.metadata.util;

import java.util.List;
import com.adventnet.ds.query.AlterTableQuery;
import com.adventnet.db.persistence.metadata.MetaDataException;
import com.adventnet.db.persistence.metadata.TableDefinition;

public interface TemplateMetaHandler
{
    String getTemplateName(final String p0);
    
    @Deprecated
    boolean isTemplate(final String p0);
    
    void addTemplate(final String p0, final TableDefinition p1) throws MetaDataException;
    
    void removeTemplate(final String p0) throws MetaDataException;
    
    void alterTemplate(final AlterTableQuery p0) throws MetaDataException;
    
    void addTemplateInstance(final String p0, final String p1) throws MetaDataException;
    
    boolean removeTemplateInstance(final String p0, final String p1) throws MetaDataException;
    
    List<String> getTemplateTableInstances(final String p0) throws MetaDataException;
    
    List<String> getTemplateInstancesForBackUp(final String p0);
    
    List<String> getTemplateInstancesForReInitialize(final String p0);
}
