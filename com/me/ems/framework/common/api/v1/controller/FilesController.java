package com.me.ems.framework.common.api.v1.controller;

import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import javax.ws.rs.Produces;
import javax.ws.rs.POST;
import com.adventnet.iam.security.UploadedFileItem;
import java.util.Map;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.devicemanagement.framework.webclient.factory.WebclientAPIFactoryProvider;
import java.io.File;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import com.me.ems.framework.common.api.v1.service.FileService;
import javax.ws.rs.Path;

@Path("files")
public class FilesController
{
    FileService fileService;
    @Context
    ContainerRequestContext requestContext;
    
    public FilesController() {
        this.fileService = new FileService();
    }
    
    private Long getCustomerID() {
        final String customerIDStr = (String)this.requestContext.getProperty("X-Customer");
        return (!customerIDStr.isEmpty() && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
    }
    
    @POST
    @Produces({ "application/files.v1+json" })
    @CustomerSegmented
    public List<File> uploadFile(@Context final HttpServletRequest request, @Context final ContainerRequestContext requestContext) throws APIException {
        final Map<String, UploadedFileItem> uploadedFileItemMap = WebclientAPIFactoryProvider.getFormFileAPI().getAllUploadedFileItem(request);
        final User user = (User)requestContext.getSecurityContext().getUserPrincipal();
        final String moduleHeader = request.getHeader("Module");
        final Long customerID = this.getCustomerID();
        if (moduleHeader == null || moduleHeader.isEmpty()) {
            throw new APIException("GENERIC0004", null, new String[] { "Module" });
        }
        if (uploadedFileItemMap != null && !uploadedFileItemMap.isEmpty()) {
            return this.fileService.saveUploadedFile(uploadedFileItemMap, customerID, user, moduleHeader);
        }
        throw new APIException("FILE0001");
    }
}
