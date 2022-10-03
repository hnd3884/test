package com.me.mdm.server.deployment;

import java.util.HashMap;

public class DeploymentConfigTypeConstants
{
    public static final int APP_DEPLOYMENT_POLICY_ID = 301;
    public static final int CM_DEPLOYMENT_POLICY_ID = 401;
    public static final String APP_DEPLOYMENT_POLICY = "APP_DEPLOYMENT_POLICY";
    public static final String CM_DEPLOYMENT_POLICY = "CM_DEPLOYMENT_POLICY";
    public static final HashMap<String, Integer> POLICY_ID_MAP;
    public static final HashMap<Integer, String> CLASS_MAP;
    
    static {
        POLICY_ID_MAP = new HashMap<String, Integer>();
        (CLASS_MAP = new HashMap<Integer, String>()).put(301, "com.me.mdm.server.deployment.policy.AppDeploymentPolicyImpl");
        DeploymentConfigTypeConstants.CLASS_MAP.put(401, "com.me.mdm.server.doc.policy.CmDeploymentPolicyImpl");
        DeploymentConfigTypeConstants.POLICY_ID_MAP.put("APP_DEPLOYMENT_POLICY", 301);
        DeploymentConfigTypeConstants.POLICY_ID_MAP.put("CM_DEPLOYMENT_POLICY", 401);
    }
}
