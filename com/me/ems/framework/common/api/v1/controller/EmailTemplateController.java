package com.me.ems.framework.common.api.v1.controller;

import javax.ws.rs.Produces;
import com.me.ems.framework.common.api.annotations.CustomerSegmented;
import javax.ws.rs.GET;
import com.me.ems.framework.common.api.utils.APIException;
import com.me.ems.framework.uac.api.v1.model.User;
import com.me.ems.framework.common.api.v1.model.EmailTemplate;
import javax.ws.rs.core.SecurityContext;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.PathParam;
import com.me.ems.framework.common.api.v1.service.EmailTemplateService;
import javax.ws.rs.Path;

@Path("emailTemplates")
public class EmailTemplateController
{
    EmailTemplateService dcEmailTemplateService;
    
    public EmailTemplateController() {
        this.dcEmailTemplateService = new EmailTemplateService();
    }
    
    @GET
    @Path("{alertID}/")
    @CustomerSegmented
    @Produces({ "application/emailTemplates.v1+json" })
    public EmailTemplate fetchEmailTemplate(@PathParam("alertID") final Long alertID, @Context final ContainerRequestContext requestContext, @Context final SecurityContext securityContext) throws APIException {
        EmailTemplate emailTemplate = null;
        try {
            final User user = (User)securityContext.getUserPrincipal();
            final Long loginID = user.getLoginID();
            final String customerIDStr = (String)requestContext.getProperty("X-Customer");
            final Long customerID = (customerIDStr != null && !customerIDStr.equalsIgnoreCase("all")) ? Long.valueOf(Long.parseLong(customerIDStr)) : null;
            final boolean isAdminUser = user.isUserInRole("Common_Write");
            if (isAdminUser || !this.dcEmailTemplateService.isAlertTechnicianSegmented(alertID)) {
                emailTemplate = this.dcEmailTemplateService.getCustomerKeyDescription(customerID, alertID);
            }
            else {
                emailTemplate = this.dcEmailTemplateService.getCustomerKeyDescription(customerID, alertID, loginID);
            }
            emailTemplate.setTemplateKeys(this.dcEmailTemplateService.getTemplateKeys(alertID));
        }
        catch (final Exception ex) {
            throw new APIException("GENERIC0002", ex.getMessage(), new String[0]);
        }
        return emailTemplate;
    }
}
