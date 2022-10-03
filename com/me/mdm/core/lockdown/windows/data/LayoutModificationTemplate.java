package com.me.mdm.core.lockdown.windows.data;

import org.apache.axiom.om.OMAttribute;
import org.json.JSONException;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAbstractFactory;
import org.json.JSONObject;
import org.apache.axiom.om.OMNamespace;
import org.apache.axiom.om.OMElement;

public class LayoutModificationTemplate
{
    OMElement layoutOptions;
    OMElement defaultLayoutOverride;
    private OMNamespace defaultlayout;
    private OMNamespace start;
    private OMNamespace layoutModificationNS;
    public static final String LAYOUT_OPTIONS = "layout_options";
    public static final String GROUP_WIDTH = "group_width";
    public static final String START_LAYOUT = "start_layout";
    public static final String APP_ID = "app_id";
    public static final String SIZE = "size";
    public static final String ROW = "row";
    public static final String COLUMN = "column";
    public static final String GROUPS = "groups";
    public static final String APPS = "apps";
    public static final String GROUP_NAME = "group_name";
    
    public LayoutModificationTemplate(final JSONObject layout) throws JSONException {
        this.layoutModificationNS = OMAbstractFactory.getOMFactory().createOMNamespace("http://schemas.microsoft.com/Start/2014/LayoutModification", "");
        this.defaultlayout = OMAbstractFactory.getOMFactory().createOMNamespace("http://schemas.microsoft.com/Start/2014/FullDefaultLayout", "defaultlayout");
        this.start = OMAbstractFactory.getOMFactory().createOMNamespace("http://schemas.microsoft.com/Start/2014/StartLayout", "start");
        this.layoutOptions = new LayoutOptions(layout.getJSONObject("layout_options")).getRootElement();
        (this.defaultLayoutOverride = OMAbstractFactory.getOMFactory().createOMElement("DefaultLayoutOverride", (OMNamespace)null)).addChild((OMNode)new DefaultLayoutOverride(layout.getJSONObject("start_layout"), this.start, this.defaultlayout).getRootElement());
    }
    
    public OMElement getRootElement() {
        final OMElement layoutModificationRoot = OMAbstractFactory.getOMFactory().createOMElement("LayoutModificationTemplate", this.layoutModificationNS);
        final OMAttribute version = OMAbstractFactory.getOMFactory().createOMAttribute("Version", (OMNamespace)null, "1");
        layoutModificationRoot.addAttribute(version);
        layoutModificationRoot.declareNamespace(this.defaultlayout);
        layoutModificationRoot.declareNamespace(this.start);
        layoutModificationRoot.addChild((OMNode)this.layoutOptions);
        layoutModificationRoot.addChild((OMNode)this.defaultLayoutOverride);
        return layoutModificationRoot;
    }
}
