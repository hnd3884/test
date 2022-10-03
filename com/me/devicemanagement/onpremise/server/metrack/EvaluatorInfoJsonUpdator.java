package com.me.devicemanagement.onpremise.server.metrack;

import org.json.JSONObject;

class EvaluatorInfoJsonUpdator implements JsonUpdator
{
    public boolean isAnyKeyModified;
    
    EvaluatorInfoJsonUpdator() {
        this.isAnyKeyModified = false;
    }
    
    @Override
    public void execute(final JSONObject destination, final JSONObject source, final String key) throws Exception {
        final String destinationVal = String.valueOf(destination.get(key));
        final String sourceVal = String.valueOf(source.get(key));
        if (!destinationVal.equalsIgnoreCase(sourceVal)) {
            this.isAnyKeyModified = true;
        }
    }
}
