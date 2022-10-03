package com.me.devicemanagement.onpremise.server.util;

import java.util.Iterator;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.util.ProductCodeMapping;
import org.json.JSONObject;
import com.me.devicemanagement.framework.utils.JsonUtils;
import java.util.HashSet;
import java.util.Map;
import java.util.logging.Logger;
import com.me.devicemanagement.onpremise.server.license.factory.thresholdvalidator.LicenseThresholdValidator;

public class DCLicenseThresholdValidator implements LicenseThresholdValidator
{
    private static Logger logger;
    private final String ENDPOINTS = "endpoints";
    private final String TECHNICIANS = "technicians";
    
    @Override
    public boolean isLicenseThresholdExceeded(final Map<String, Object> licenseDetails) {
        String productName = "";
        int endpointsThresholdLimit = Integer.MAX_VALUE;
        int techniciansThresholdLimit = Integer.MAX_VALUE;
        try {
            DCLicenseThresholdValidator.logger.info("License Threshold validation started");
            final int numberOfEndPointsFromLicense = licenseDetails.get("noOfEndPoints");
            final int numberOfTechniciansFromLicense = licenseDetails.get("NumberOfTechnicians");
            productName = licenseDetails.get("productName");
            final Set<String> moduleSet = licenseDetails.get("moduleComponents");
            final JSONObject licenseThresholdJson = JsonUtils.loadJsonFromOrderProperties((String)null, "license.threshold.json.order.", (String)null, (JSONObject)null);
            DCLicenseThresholdValidator.logger.info("MaxLicenseCountDetails : " + licenseThresholdJson.toString());
            final String productCode = ProductCodeMapping.getSingleProductCode(productName);
            if (productCode == null) {
                DCLicenseThresholdValidator.logger.log(Level.SEVERE, "License threshold validation error, Cant get productCode for productName:{0}", productName);
                return false;
            }
            DCLicenseThresholdValidator.logger.log(Level.INFO, "License Details numberOfEndPointsFromLicense:{0}, numberOfTechniciansFromLicense:{1}, productName:{2}, productCode:{3}, moduleSet:{4}", new Object[] { numberOfEndPointsFromLicense, numberOfTechniciansFromLicense, productName, productCode, moduleSet });
            if (!licenseThresholdJson.has(productCode)) {
                DCLicenseThresholdValidator.logger.log(Level.SEVERE, "License threshold validation error, Max License restriction details are not present in  LicenseThresholdDetails_fw.json for productCode: {0}", productName);
                return false;
            }
            DCLicenseThresholdValidator.logger.log(Level.INFO, "Getting threshold limit for product : {0}", new Object[] { productCode });
            final HashMap<String, Integer> result = this.getThresholdLimitCount(licenseThresholdJson, productCode, moduleSet);
            endpointsThresholdLimit = result.get("endpoints");
            techniciansThresholdLimit = result.get("technicians");
            if (numberOfTechniciansFromLicense > techniciansThresholdLimit || numberOfEndPointsFromLicense > endpointsThresholdLimit) {
                DCLicenseThresholdValidator.logger.log(Level.INFO, "Invalid license count , stopping license upgrade , due to license limit exceed => numberOfEndPointsFromLicense:{0},endpointsThresholdLimit:{1},numberOfTechniciansFromLicense:{2},techniciansThresholdLimit:{3}", new Object[] { numberOfEndPointsFromLicense, endpointsThresholdLimit, numberOfTechniciansFromLicense, techniciansThresholdLimit });
                return true;
            }
        }
        catch (final Exception e) {
            DCLicenseThresholdValidator.logger.log(Level.SEVERE, "error while reading LicenseThresholdDetails_fw.json file", e);
        }
        return false;
    }
    
    private HashMap<String, Integer> getThresholdLimitCount(final JSONObject licenseThresholdJson, final String productCode, final Set<String> moduleSet) {
        int endpointLimit = Integer.MAX_VALUE;
        int technicianLimit = Integer.MAX_VALUE;
        final JSONObject productThresholdJson = licenseThresholdJson.getJSONObject(productCode);
        if (productThresholdJson.has("endpoints") && productThresholdJson.has("technicians")) {
            endpointLimit = productThresholdJson.getInt("endpoints");
            technicianLimit = productThresholdJson.getInt("technicians");
        }
        else {
            for (String module : moduleSet) {
                if (module.equalsIgnoreCase("EndpointManagement")) {
                    module = "Endpoint";
                }
                if (productThresholdJson.has(module)) {
                    final JSONObject moduleDetails = productThresholdJson.getJSONObject(module);
                    final int currentEndpointLimit = moduleDetails.getInt("endpoints");
                    final int currentTechniciansLimit = moduleDetails.getInt("technicians");
                    if (currentEndpointLimit < endpointLimit) {
                        DCLicenseThresholdValidator.logger.log(Level.INFO, "setting maxNumberOfSomDevices limit from {0} to {1} for {2} Module", new Object[] { endpointLimit, currentEndpointLimit, module });
                        endpointLimit = currentEndpointLimit;
                    }
                    if (currentTechniciansLimit >= technicianLimit) {
                        continue;
                    }
                    DCLicenseThresholdValidator.logger.log(Level.INFO, "setting technicianLimit limit from {0} to {1} for {2} Module", new Object[] { technicianLimit, currentTechniciansLimit, module });
                    technicianLimit = currentTechniciansLimit;
                }
            }
        }
        final HashMap<String, Integer> thresholdLimitCount = new HashMap<String, Integer>();
        thresholdLimitCount.put("endpoints", endpointLimit);
        thresholdLimitCount.put("technicians", technicianLimit);
        return thresholdLimitCount;
    }
    
    static {
        DCLicenseThresholdValidator.logger = Logger.getLogger("LicenseLogger");
    }
}
