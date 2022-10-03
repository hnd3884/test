package org.apache.lucene.queryparser.flexible.standard.config;

import org.apache.lucene.document.DateTools;
import java.util.Map;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfigListener;

public class FieldDateResolutionFCListener implements FieldConfigListener
{
    private QueryConfigHandler config;
    
    public FieldDateResolutionFCListener(final QueryConfigHandler config) {
        this.config = null;
        this.config = config;
    }
    
    @Override
    public void buildFieldConfig(final FieldConfig fieldConfig) {
        DateTools.Resolution dateRes = null;
        final Map<CharSequence, DateTools.Resolution> dateResMap = this.config.get(StandardQueryConfigHandler.ConfigurationKeys.FIELD_DATE_RESOLUTION_MAP);
        if (dateResMap != null) {
            dateRes = dateResMap.get(fieldConfig.getField());
        }
        if (dateRes == null) {
            dateRes = this.config.get(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION);
        }
        if (dateRes != null) {
            fieldConfig.set(StandardQueryConfigHandler.ConfigurationKeys.DATE_RESOLUTION, dateRes);
        }
    }
}
