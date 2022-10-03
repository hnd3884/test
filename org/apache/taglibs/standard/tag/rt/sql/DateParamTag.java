package org.apache.taglibs.standard.tag.rt.sql;

import java.util.Date;
import org.apache.taglibs.standard.tag.common.sql.DateParamTagSupport;

public class DateParamTag extends DateParamTagSupport
{
    public void setValue(final Date value) {
        this.value = value;
    }
    
    public void setType(final String type) {
        this.type = type;
    }
}
