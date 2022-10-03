package org.apache.taglibs.standard.tag.rt.sql;

import org.apache.taglibs.standard.tag.common.sql.ParamTagSupport;

public class ParamTag extends ParamTagSupport
{
    public void setValue(final Object value) {
        this.value = value;
    }
}
