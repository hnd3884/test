package org.apache.lucene.queryparser.flexible.standard.config;

import java.util.Map;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfigListener;

public class FieldBoostMapFCListener implements FieldConfigListener
{
    private QueryConfigHandler config;
    
    public FieldBoostMapFCListener(final QueryConfigHandler config) {
        this.config = null;
        this.config = config;
    }
    
    @Override
    public void buildFieldConfig(final FieldConfig fieldConfig) {
        final Map<String, Float> fieldBoostMap = this.config.get(StandardQueryConfigHandler.ConfigurationKeys.FIELD_BOOST_MAP);
        if (fieldBoostMap != null) {
            final Float boost = fieldBoostMap.get(fieldConfig.getField());
            if (boost != null) {
                fieldConfig.set(StandardQueryConfigHandler.ConfigurationKeys.BOOST, boost);
            }
        }
    }
}
