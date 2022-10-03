package org.apache.lucene.queryparser.flexible.standard.config;

import java.util.Map;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfig;
import org.apache.lucene.queryparser.flexible.core.config.QueryConfigHandler;
import org.apache.lucene.queryparser.flexible.core.config.FieldConfigListener;

public class NumericFieldConfigListener implements FieldConfigListener
{
    private final QueryConfigHandler config;
    
    public NumericFieldConfigListener(final QueryConfigHandler config) {
        if (config == null) {
            throw new IllegalArgumentException("config cannot be null!");
        }
        this.config = config;
    }
    
    @Override
    public void buildFieldConfig(final FieldConfig fieldConfig) {
        final Map<String, NumericConfig> numericConfigMap = this.config.get(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG_MAP);
        if (numericConfigMap != null) {
            final NumericConfig numericConfig = numericConfigMap.get(fieldConfig.getField());
            if (numericConfig != null) {
                fieldConfig.set(StandardQueryConfigHandler.ConfigurationKeys.NUMERIC_CONFIG, numericConfig);
            }
        }
    }
}
