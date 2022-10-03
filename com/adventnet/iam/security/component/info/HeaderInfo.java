package com.adventnet.iam.security.component.info;

import java.util.Map;
import java.util.HashMap;
import com.adventnet.iam.security.HeaderRule;

public class HeaderInfo
{
    private String value;
    private HeaderRule rule;
    private String infoStr;
    
    public HeaderInfo(final String name, final Object value, final HeaderRule rule) {
        this.value = value.toString();
        this.rule = rule;
    }
    
    @Override
    public String toString() {
        if (this.infoStr == null) {
            final Map<String, Object> infoMap = new HashMap<String, Object>();
            if (this.rule != null) {
                infoMap.put("secret", this.rule.getHeaderRule().isSecret());
            }
            infoMap.put("value", this.value);
            this.infoStr = infoMap.toString();
        }
        return this.infoStr;
    }
}
