package com.me.mdm.api.controller;

import javax.ws.rs.POST;
import com.me.mdm.server.enrollment.admin.android.service.GoogleForWorkWebtokenService;
import com.me.mdm.server.enrollment.admin.android.model.GoogleWebtokenEnrollmentResponseModel;
import com.me.mdm.server.enrollment.admin.android.model.GoogleWebtokenEnrollmentModel;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("/googleforwork/webtoken")
public class GoogleForWorkWebtokenController extends BaseController
{
    @POST
    @Path("/enrollment/templates/{template_type}")
    public GoogleWebtokenEnrollmentResponseModel getGoogleForWorkEnrollmentWebToken(@Context final ContainerRequestContext requestContext, @PathParam("template_type") final String templateType, final GoogleWebtokenEnrollmentModel googleWebtokenEnrollmentModel) {
        final GoogleForWorkWebtokenService googleForWorkWebtokenService = new GoogleForWorkWebtokenService();
        googleWebtokenEnrollmentModel.setCustomerUserDetails(requestContext);
        googleWebtokenEnrollmentModel.setTemplateType(Integer.parseInt(templateType));
        return googleForWorkWebtokenService.getGoogleForWorkEnrollmentWebToken(googleWebtokenEnrollmentModel);
    }
}
