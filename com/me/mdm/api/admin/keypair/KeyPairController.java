package com.me.mdm.api.admin.keypair;

import javax.ws.rs.POST;
import com.me.mdm.api.error.APIHTTPException;
import com.me.mdm.api.model.BaseAPIModel;
import java.util.logging.Level;
import javax.ws.rs.core.Context;
import javax.ws.rs.container.ContainerRequestContext;
import java.util.logging.Logger;
import javax.ws.rs.Path;
import com.me.mdm.api.common.controller.BaseController;

@Path("keypair")
public class KeyPairController extends BaseController
{
    private final Logger logger;
    
    public KeyPairController() {
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @POST
    public KeyPairResponseModel generateKeyPair(@Context final ContainerRequestContext requestContext, final KeyPairRequestModel request) throws APIHTTPException {
        try {
            this.logger.log(Level.INFO, "Creating RSA key pair for {0}", request.getFeatureName());
            final BaseAPIModel baseAPIModel = new BaseAPIModel();
            baseAPIModel.setCustomerUserDetails(requestContext);
            return new KeyPairService().createKeyPair(baseAPIModel, request.getFeatureName());
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Exception in GeneratingKeyPair", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
}
