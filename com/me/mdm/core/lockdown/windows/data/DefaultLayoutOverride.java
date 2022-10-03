package com.me.mdm.core.lockdown.windows.data;

import org.json.JSONException;
import org.json.JSONArray;
import org.apache.axiom.om.OMAttribute;
import org.apache.axiom.om.OMNode;
import org.apache.axiom.om.OMAbstractFactory;
import org.apache.axiom.om.OMNamespace;
import org.json.JSONObject;
import org.apache.axiom.om.OMElement;

public class DefaultLayoutOverride
{
    OMElement startLayoutCollection;
    
    public DefaultLayoutOverride(final JSONObject params, final OMNamespace start, final OMNamespace defaultLayout) throws JSONException {
        this.startLayoutCollection = OMAbstractFactory.getOMFactory().createOMElement("StartLayoutCollection", (OMNamespace)null);
        final Integer groupCellWidth = params.getInt("group_width");
        final OMAttribute cellWidth = OMAbstractFactory.getOMFactory().createOMAttribute("GroupCellWidth", (OMNamespace)null, groupCellWidth.toString());
        final OMElement startLayout = OMAbstractFactory.getOMFactory().createOMElement("StartLayout", defaultLayout);
        startLayout.addAttribute(cellWidth);
        final JSONArray jsonArray = params.getJSONArray("groups");
        for (int i = 0; i < jsonArray.length(); ++i) {
            final JSONObject group = jsonArray.getJSONObject(i);
            final String groupName = String.valueOf(group.get("group_name"));
            final JSONArray apps = group.getJSONArray("apps");
            final OMElement curGroup = OMAbstractFactory.getOMFactory().createOMElement("Group", start);
            curGroup.addAttribute("Name", groupName, (OMNamespace)null);
            boolean hasApp = false;
            for (int j = 0; j < apps.length(); ++j) {
                hasApp = true;
                final JSONObject app = apps.getJSONObject(j);
                curGroup.addChild((OMNode)this.getTileElement(app, start));
            }
            if (hasApp) {
                startLayout.addChild((OMNode)curGroup);
            }
        }
        this.startLayoutCollection.addChild((OMNode)startLayout);
    }
    
    private OMElement getTileElement(final JSONObject tile, final OMNamespace start) throws JSONException {
        final String appID = String.valueOf(tile.get("app_id"));
        final String size = String.valueOf(tile.get("size"));
        final String row = String.valueOf(tile.get("row"));
        final String column = String.valueOf(tile.get("column"));
        OMElement tileElement = null;
        if (appID.matches("[a-zA-Z0-9]*(\\.[a-zA-Z0-9]*)*_[a-zA-Z0-9]*![a-zA-Z0-9\\.]*")) {
            tileElement = OMAbstractFactory.getOMFactory().createOMElement("Tile", start);
            tileElement.addAttribute("AppUserModelID", appID, (OMNamespace)null);
        }
        else {
            tileElement = OMAbstractFactory.getOMFactory().createOMElement("DesktopApplicationTile", start);
            tileElement.addAttribute("DesktopApplicationLinkPath", appID, (OMNamespace)null);
        }
        tileElement.addAttribute("Row", row, (OMNamespace)null);
        tileElement.addAttribute("Column", column, (OMNamespace)null);
        tileElement.addAttribute("Size", size, (OMNamespace)null);
        return tileElement;
    }
    
    protected OMElement getRootElement() {
        return this.startLayoutCollection;
    }
}
