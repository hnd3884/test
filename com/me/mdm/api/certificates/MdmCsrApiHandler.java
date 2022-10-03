package com.me.mdm.api.certificates;

import com.adventnet.persistence.DataAccessException;
import com.adventnet.sym.server.mdm.certificates.csr.MdmCsrDbHandler;
import com.adventnet.sym.server.mdm.certificates.csr.DigicertCsrHandler;
import java.io.ByteArrayOutputStream;
import com.me.mdm.api.error.APIHTTPException;
import com.adventnet.sym.server.mdm.certificates.csr.MdmCsrHandler;
import com.me.mdm.api.APIUtil;
import com.adventnet.sym.server.mdm.certificates.csr.CsrConstants;
import java.util.logging.Level;
import org.json.JSONObject;
import com.me.mdm.api.APIRequest;
import java.util.logging.Logger;
import com.me.mdm.api.ApiRequestHandler;

public class MdmCsrApiHandler extends ApiRequestHandler
{
    private Logger logger;
    
    public MdmCsrApiHandler() {
        this.logger = Logger.getLogger("MdmCertificateIntegLogger");
    }
    
    @Override
    public Object doGet(final APIRequest apiRequest) throws APIHTTPException {
        final JSONObject response = new JSONObject();
        try {
            this.logger.log(Level.FINE, "Downloading csr..");
            apiRequest.urlStartKey = CsrConstants.Api.Key.csrrequests;
            final JSONObject jsonObject = apiRequest.toJSONObject().getJSONObject("msg_header");
            apiRequest.httpServletResponse.setContentType("application/pkcs10");
            apiRequest.httpServletResponse.setHeader("Content-Disposition", "attachment;filename=Csr.csr");
            jsonObject.put(CsrConstants.Api.Key.csrrequest_id, jsonObject.getJSONObject("resource_identifier").getLong(CsrConstants.Api.Key.csrrequest_id));
            jsonObject.put("CUSTOMER_ID", (Object)APIUtil.getCustomerID(apiRequest.toJSONObject()));
            final ByteArrayOutputStream byteArrayOutputStream = MdmCsrHandler.downloadCsr(jsonObject);
            final String csr = byteArrayOutputStream.toString();
            final JSONObject responseJSON = new JSONObject();
            responseJSON.put("payload_content", (Object)csr);
            response.put("status", 200);
            response.put("RESPONSE", (Object)responseJSON);
            return response;
        }
        catch (final APIHTTPException e) {
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception while downloading CSR..", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doPost(final APIRequest apiRequest) throws APIHTTPException {
        try {
            this.logger.log(Level.FINE, "Creating csr...");
            final JSONObject responseDetails = new JSONObject();
            final JSONObject csrInfo = new JSONObject();
            csrInfo.put("CUSTOMER_ID", (Object)APIUtil.optCustomerID(apiRequest.toJSONObject()));
            final Integer type = apiRequest.toJSONObject().getJSONObject("msg_body").getInt("csr_type");
            csrInfo.put("msg_body", (Object)apiRequest.toJSONObject().getJSONObject("msg_body"));
            if (type == 1) {
                this.logger.log(Level.FINE, "Request received for creating Digicert CSR");
                final Long csrID = DigicertCsrHandler.createCSR(csrInfo);
                responseDetails.put("RESPONSE", (Object)new JSONObject().put("CSR_ID", (Object)csrID));
                this.logger.log(Level.FINE, "CSR created, CSR_ID = {0}", csrID);
                responseDetails.put("status", 200);
                return responseDetails;
            }
            this.logger.log(Level.WARNING, "Invalid CSR type");
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final APIHTTPException e) {
            this.logger.log(Level.WARNING, "Exception while CSR creation", e);
            throw e;
        }
        catch (final Exception e2) {
            this.logger.log(Level.WARNING, "Exception while CSR creation..", e2);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    @Override
    public Object doDelete(final APIRequest apiRequest) {
        apiRequest.urlStartKey = CsrConstants.Api.Key.csrrequests;
        final JSONObject jsonObject = apiRequest.toJSONObject().getJSONObject("msg_header");
        final long csrRequestId = jsonObject.getJSONObject("resource_identifier").getLong(CsrConstants.Api.Key.csrrequest_id);
        try {
            MdmCsrDbHandler.deleteMdmCsr(csrRequestId);
        }
        catch (final DataAccessException e) {
            this.logger.log(Level.SEVERE, (Throwable)e, () -> "Exception while deleting csr" + n);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
        final JSONObject response = new JSONObject();
        response.put("status", 202);
        return response;
    }
}
