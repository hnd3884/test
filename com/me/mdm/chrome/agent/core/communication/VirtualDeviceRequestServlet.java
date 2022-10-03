package com.me.mdm.chrome.agent.core.communication;

import org.json.JSONException;
import java.util.Map;
import java.util.HashMap;

public interface VirtualDeviceRequestServlet
{
    void doVirtualPost(final VirtualHttpServletRequest p0, final VirtualHttpServletResponse p1);
    
    public static class VirtualHttpServletRequest
    {
        String requestData;
        HashMap<String, String> params;
        
        public VirtualHttpServletRequest(final String data, final HashMap<String, String> map) {
            this.requestData = data;
            this.params = new HashMap<String, String>(map);
        }
        
        public String getRequestData() {
            return this.requestData;
        }
        
        public HashMap<String, String> getParams() {
            return this.params;
        }
    }
    
    public static class VirtualHttpServletResponse
    {
        String responseData;
        
        public void writeResponse(final String data) throws JSONException {
            this.responseData = data;
        }
        
        public String getResponseData() {
            return this.responseData;
        }
    }
}
