package com.me.mdm.server.deployment;

import com.adventnet.persistence.DataAccessException;
import org.json.JSONException;
import org.json.JSONObject;

public interface DeploymentPolicyDataInterface
{
    void addOrUpdateDeploymentPolicy(final Long p0, final JSONObject p1) throws JSONException, DataAccessException;
    
    JSONObject getDeploymentDataPolicy(final Long p0) throws JSONException, DataAccessException;
    
    JSONObject getEffectiveDeploymentDataPolicy(final Long p0, final Long p1) throws JSONException, DataAccessException;
}
