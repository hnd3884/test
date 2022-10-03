package com.me.ems.framework.common.api.v1.model.helpermodel;

import org.json.simple.JSONArray;

public class DashboardPreferenceBean
{
    private JSONArray defaultRoute;
    
    public DashboardPreferenceBean() {
        this.defaultRoute = new JSONArray();
    }
    
    public JSONArray getDefaultRoute() {
        return this.defaultRoute;
    }
    
    public void setDefaultRoute(final JSONArray defaultRoute) {
        this.defaultRoute = defaultRoute;
    }
}
