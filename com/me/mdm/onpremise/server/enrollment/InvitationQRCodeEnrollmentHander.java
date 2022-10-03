package com.me.mdm.onpremise.server.enrollment;

import java.net.URLEncoder;
import com.adventnet.sym.server.mdm.enroll.MDMEnrollmentUtil;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import javax.ws.rs.core.UriBuilder;
import com.me.mdm.framework.qr.QRCodeGenerator;
import java.awt.Color;
import java.io.File;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Level;
import com.adventnet.sym.server.mdm.core.EREvent;
import java.util.logging.Logger;

public class InvitationQRCodeEnrollmentHander
{
    private static Logger logger;
    
    public static void generateQRCode(final EREvent erEvent) {
        generateQRCodeForEnrollmentRequest(Long.valueOf(erEvent.enrollmentRequestId), erEvent.otp);
    }
    
    public static void reGenerateQRCode(final EREvent erEvent) {
        generateQRCodeForEnrollmentRequest(Long.valueOf(erEvent.enrollmentRequestId), erEvent.otp);
    }
    
    public static void removeQRCode(final Long enrollmentRequestId) {
        try {
            InvitationQRCodeEnrollmentHander.logger.log(Level.INFO, "Going to delete QR Image for Request:{0}", enrollmentRequestId);
            ApiFactoryProvider.getFileAccessAPI().deleteFile(getQRImagefileNameForEnrollmentRequest(enrollmentRequestId));
            InvitationQRCodeEnrollmentHander.logger.log(Level.INFO, "QR Image deleted from location:{0}", getQRImagefileNameForEnrollmentRequest(enrollmentRequestId));
        }
        catch (final Exception ex) {
            InvitationQRCodeEnrollmentHander.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private static String getQRCodesFolderPath() {
        final String mdmFolder = System.getProperty("user.dir") + "\\..\\mdm\\";
        final String qrCodeFolderPath = mdmFolder + File.separator + "invitationQRCodes";
        try {
            if (!ApiFactoryProvider.getFileAccessAPI().isFileExists(qrCodeFolderPath)) {
                ApiFactoryProvider.getFileAccessAPI().createDirectory(qrCodeFolderPath);
            }
        }
        catch (final Exception e) {
            InvitationQRCodeEnrollmentHander.logger.log(Level.SEVERE, null, e);
        }
        return qrCodeFolderPath;
    }
    
    public static String getQRImagefileNameForEnrollmentRequest(final Long erid) {
        return getQRCodesFolderPath() + File.separator + String.valueOf(erid).concat(".png");
    }
    
    private static void generateQRCodeForEnrollmentRequest(final Long erid, final String otppassword) {
        try {
            final String getQRData = getEnrollmentURLforQR(erid, otppassword, 1);
            final QRCodeGenerator generator = new QRCodeGenerator(Color.WHITE);
            generator.createQRCode(getQRData, getQRImagefileNameForEnrollmentRequest(erid));
            InvitationQRCodeEnrollmentHander.logger.log(Level.INFO, "QR Image saved in location:{0}", getQRImagefileNameForEnrollmentRequest(erid));
        }
        catch (final Exception ex) {
            InvitationQRCodeEnrollmentHander.logger.log(Level.SEVERE, null, ex);
        }
    }
    
    private static String getEnrollmentURLforQR(final Long erid, final String otppassword, final int scanSrc) {
        final String enrollmentURL = getEnrollmentURLForRequest(erid);
        if (enrollmentURL != null && otppassword != null) {
            try {
                final UriBuilder builder = UriBuilder.fromUri(enrollmentURL);
                builder.queryParam("et", new Object[] { "1" });
                builder.queryParam("token", new Object[] { otppassword });
                builder.queryParam("scanSrc", new Object[] { scanSrc });
                final String encodedURL = builder.build(new Object[0]).toURL().toString();
                return encodedURL;
            }
            catch (final Exception ex) {
                InvitationQRCodeEnrollmentHander.logger.log(Level.SEVERE, null, ex);
            }
        }
        return null;
    }
    
    public static String getQREnrollmentImageURL(final Long erid, final String otppassword, final int scanSrc) {
        try {
            final String qrEnrollURL = getEnrollmentURLforQR(erid, otppassword, scanSrc);
            if (!MDMStringUtils.isEmpty(qrEnrollURL)) {
                final UriBuilder builder = UriBuilder.fromUri(MDMEnrollmentUtil.getInstance().getServerBaseURL());
                builder.path("/api/v1/mdm/enroll/qr");
                builder.queryParam("data", new Object[] { URLEncoder.encode(qrEnrollURL, "UTF-8") });
                builder.queryParam("blackbg", new Object[] { true });
                builder.queryParam("frame", new Object[] { false });
                return builder.build(new Object[0]).toURL().toString();
            }
        }
        catch (final Exception ex) {
            InvitationQRCodeEnrollmentHander.logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String getQREnrollmentURLFromContextPath(final Long erid, final String otppassword, final int scanSrc) {
        try {
            final String qrEnrollURL = getEnrollmentURLforQR(erid, otppassword, scanSrc);
            if (!MDMStringUtils.isEmpty(qrEnrollURL)) {
                final UriBuilder builder = UriBuilder.fromPath("/api/v1/mdm/enroll/qr");
                builder.queryParam("data", new Object[] { qrEnrollURL });
                return builder.build(new Object[0]).normalize().toString();
            }
        }
        catch (final Exception ex) {
            InvitationQRCodeEnrollmentHander.logger.log(Level.SEVERE, null, ex);
        }
        return null;
    }
    
    public static String getEnrollmentURLForRequest(final Long enrollmentRequestID) {
        try {
            final String sServerBaseURL = MDMEnrollmentUtil.getInstance().getServerBaseURL();
            final String url = sServerBaseURL + "/mdm/enroll" + "/" + String.valueOf(enrollmentRequestID);
            return url;
        }
        catch (final Exception ex) {
            InvitationQRCodeEnrollmentHander.logger.log(Level.SEVERE, null, ex);
            return null;
        }
    }
    
    static {
        InvitationQRCodeEnrollmentHander.logger = Logger.getLogger("MDMEnrollment");
    }
}
