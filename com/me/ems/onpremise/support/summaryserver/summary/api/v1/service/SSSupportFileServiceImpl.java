package com.me.ems.onpremise.support.summaryserver.summary.api.v1.service;

import com.me.ems.framework.common.api.utils.APIException;
import javax.ws.rs.core.Response;
import java.util.Map;
import com.me.ems.onpremise.support.factory.SupportFileService;
import com.me.ems.onpremise.support.api.v1.service.SupportFileServiceImpl;

public class SSSupportFileServiceImpl extends SupportFileServiceImpl implements SupportFileService
{
    @Override
    public void validateSupportFileData(final Map supportFileDetails) throws APIException {
        if ((supportFileDetails.containsKey("mdmLogUpload") && Boolean.valueOf(supportFileDetails.getOrDefault("mdmLogUpload", "false"))) || (supportFileDetails.containsKey("agentLogUpload") && Boolean.valueOf(supportFileDetails.getOrDefault("agentLogUpload", "false"))) || (supportFileDetails.containsKey("dsLogUpload") && Boolean.valueOf(supportFileDetails.getOrDefault("agentLogUpload", "false")))) {
            throw new APIException(Response.Status.BAD_REQUEST, "RESOURCE0002", (String)null);
        }
        super.validateSupportFileData(supportFileDetails);
    }
}
