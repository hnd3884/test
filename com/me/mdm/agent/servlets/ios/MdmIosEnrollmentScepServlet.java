package com.me.mdm.agent.servlets.ios;

import org.bouncycastle.util.encoders.Base64;
import com.adventnet.sym.server.mdm.certificates.scep.request.ScepRequestHandler;
import com.adventnet.sym.server.mdm.certificates.scep.request.ScepServerFactory;
import com.adventnet.sym.server.mdm.certificates.scep.request.MdmScepRequest;
import java.io.InputStream;
import org.apache.commons.io.IOUtils;
import java.io.IOException;
import javax.servlet.ServletException;
import com.adventnet.sym.server.mdm.certificates.scep.response.ScepResponse;
import org.jscep.transaction.OperationFailureException;
import com.adventnet.sym.server.mdm.util.MDMiOSEntrollmentUtil;
import org.jscep.transport.request.Operation;
import java.util.logging.Level;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletRequest;
import java.util.logging.Logger;
import com.me.mdm.webclient.filter.YetToEnrollDeviceAuthenticatorServlet;

public class MdmIosEnrollmentScepServlet extends YetToEnrollDeviceAuthenticatorServlet
{
    private final Logger logger;
    
    public MdmIosEnrollmentScepServlet() {
        this.logger = Logger.getLogger("MDMIosEnrollmentClientCertificateLogger");
    }
    
    @Override
    protected void handleGet(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final String operationType = request.getParameter("operation");
            if (operationType == null || operationType.isEmpty()) {
                this.logger.log(Level.SEVERE, "MdmIosEnrollmentScepServlet: The request URL does not contain operation parameter.");
                response.sendError(400, "Missing \"operation\" parameter.");
                return;
            }
            final Operation operation = Operation.forName(operationType);
            final long erid = Long.parseLong(request.getParameter("erid"));
            final Long customerId = MDMiOSEntrollmentUtil.getInstance().getCustomerIdForErid(erid);
            if (customerId == null) {
                this.logger.log(Level.SEVERE, "No matching customer Id found for Enrollment request Id: {0}", new Object[] { erid });
                throw new Exception("No matching customer Id found for Enrollment request Id: " + erid);
            }
            byte[] pkiMessageBytes = new byte[0];
            if (operation == Operation.PKI_OPERATION) {
                this.logger.log(Level.SEVERE, "MdmIosEnrollmentScepServlet: Device is sending PKI operation in GET {0}, {1}", new Object[] { erid, customerId });
                final String message = request.getParameter("message");
                pkiMessageBytes = this.getPkiMessageBytes(message);
            }
            final ScepResponse scepResponse = this.handleRequest(operation, customerId, erid, pkiMessageBytes);
            this.writeScepResponseToOuput(response, scepResponse);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MdmIosEnrollmentScepServlet: Exception while handling get requests for SCEP server: ", e);
            if (e instanceof OperationFailureException) {
                final String failInfo = ((OperationFailureException)e).getFailInfo().toString();
                this.logger.log(Level.SEVERE, e, () -> "MdmIosEnrollmentScepServlet: Fail info: " + s);
                response.sendError(400, failInfo);
            }
            response.sendError(500);
        }
    }
    
    @Override
    protected void handlePost(final HttpServletRequest request, final HttpServletResponse response) throws ServletException, IOException {
        try {
            final String operationType = request.getParameter("operation");
            if (operationType == null || operationType.isEmpty()) {
                this.logger.log(Level.SEVERE, "MdmIosEnrollmentScepServlet: The request URL does not contain operation parameter.");
                response.sendError(400, "Missing \"operation\" parameter.");
                return;
            }
            final Operation operation = Operation.forName(operationType);
            final long erid = Long.parseLong(request.getParameter("erid"));
            final Long customerId = MDMiOSEntrollmentUtil.getInstance().getCustomerIdForErid(erid);
            if (customerId == null) {
                this.logger.log(Level.SEVERE, "No matching customer Id found for Enrollment request Id: {0}", new Object[] { erid });
                throw new Exception("No matching customer Id found for Enrollment request Id: " + erid);
            }
            final byte[] pkiMessageBytes = IOUtils.toByteArray((InputStream)request.getInputStream());
            final ScepResponse scepResponse = this.handleRequest(operation, customerId, erid, pkiMessageBytes);
            this.writeScepResponseToOuput(response, scepResponse);
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "MdmIosEnrollmentScepServlet: Exception while handling get requests for SCEP server: ", e);
            if (e instanceof OperationFailureException) {
                final String failInfo = ((OperationFailureException)e).getFailInfo().toString();
                this.logger.log(Level.SEVERE, e, () -> "MdmIosEnrollmentScepServlet: Fail info: " + s);
                response.sendError(400, failInfo);
            }
            response.sendError(500);
        }
    }
    
    ScepResponse handleRequest(final Operation operation, final long customerId, final long erid, final byte[] messageBytes) throws Exception {
        this.logger.log(Level.INFO, "================================================================================================================");
        this.logger.log(Level.INFO, "======================= MDM SCEP {0} OPERATION begins for ENROLLMENT REQUEST ID: {1} =====================", new Object[] { operation.getName(), erid });
        final MdmScepRequest mdmScepRequest = new MdmScepRequest(erid, customerId, operation, messageBytes);
        final ScepRequestHandler scepRequestHandler = ScepServerFactory.getRequestHandler(mdmScepRequest);
        final ScepResponse scepResponse = scepRequestHandler.handleRequest();
        this.logger.log(Level.INFO, "MdmIosEnrollmentScepServlet: Content type of SCEP response: {0} for {1}", new Object[] { scepResponse.getContentType(), mdmScepRequest.getEnrollmentRequestId() });
        this.logger.log(Level.INFO, "======================= MDM SCEP {0} OPERATION completed for ENROLLMENT REQUEST ID: {1} =====================", new Object[] { operation.getName(), erid });
        return scepResponse;
    }
    
    private void writeScepResponseToOuput(final HttpServletResponse response, final ScepResponse scepResponse) throws IOException {
        response.setHeader("Content-Type", scepResponse.getContentType());
        this.logger.log(Level.INFO, "MdmIosEnrollmentScepServlet: Content-type of the HttpServletResponse is {0}", new Object[] { response.getCharacterEncoding() });
        this.logger.log(Level.FINE, "MdmIosEnrollmentScepServlet: Writing the SCEP response to output {0}", new Object[] { scepResponse.getResponse() });
        this.logger.log(Level.INFO, "================================================================================================================");
        response.getOutputStream().write(scepResponse.getResponse());
        response.getOutputStream().close();
    }
    
    private byte[] getPkiMessageBytes(final String base64encodedMessage) {
        if (base64encodedMessage.length() == 0) {
            return new byte[0];
        }
        this.logger.log(Level.INFO, "MdmIosEnrollmentScepServlet: Decoding and Retrieving pki message.");
        return Base64.decode(base64encodedMessage.replace(' ', '+'));
    }
    
    @Override
    protected void handleBadRequest(final long enrollmentRequestId) {
        this.logger.log(Level.SEVERE, "MdmIosEnrollmentScepServlet: Invalid Encapi key provided for enrollment request Id: {0}", new Object[] { enrollmentRequestId });
    }
}
