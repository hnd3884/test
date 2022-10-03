package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import org.json.JSONObject;
import com.me.ems.framework.common.api.utils.APIException;
import java.util.logging.Level;
import javax.ws.rs.core.Response;
import java.util.Map;
import com.me.ems.framework.common.api.v1.service.FileUploadStatusService;
import java.util.logging.Logger;
import javax.ws.rs.Path;

@Path("fileupload")
public class FileUploadStatusController
{
    private static final Logger LOGGER;
    FileUploadStatusService fileUploadStatusService;
    
    public FileUploadStatusController() {
        this.fileUploadStatusService = new FileUploadStatusService();
    }
    
    @POST
    @Path("/status")
    @Produces({ "application/json" })
    public Response getFileUploadStatus(final Map requestData) throws APIException {
        try {
            final JSONObject response = this.fileUploadStatusService.getFileUploadStatus(requestData);
            FileUploadStatusController.LOGGER.log(Level.INFO, "FileUploadStatus Response : {0}", response);
            return Response.status(Response.Status.OK).entity((Object)response.toString()).build();
        }
        catch (final Exception exception) {
            FileUploadStatusController.LOGGER.log(Level.SEVERE, "Exception occurred in FileUploadStatusController while getting file upload status response - {0}", exception);
            throw new APIException(Response.Status.INTERNAL_SERVER_ERROR, "GENERIC0005", "ems.rest.api.internal_error");
        }
    }
    
    static {
        LOGGER = Logger.getLogger(FileUploadStatusController.class.getName());
    }
}
