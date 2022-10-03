package com.me.mdm.core.lockdown.windows.data;

import org.json.JSONException;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMAbstractFactory;
import org.json.JSONObject;
import org.apache.axiom.om.OMElement;

public class LayoutOptions
{
    OMElement options;
    
    public LayoutOptions(final JSONObject params) throws JSONException {
        final Integer startWidth = (Integer)params.get("group_width");
        this.options = OMAbstractFactory.getOMFactory().createOMElement("LayoutOptions", (OMNamespace)null);
        final OMAttribute GroupWidthAttribute = OMAbstractFactory.getOMFactory().createOMAttribute("StartTileGroupCellWidth", (OMNamespace)null, startWidth.toString());
        this.options.addAttribute(GroupWidthAttribute);
    }
    
    protected OMElement getRootElement() {
        return this.options;
    }
}
