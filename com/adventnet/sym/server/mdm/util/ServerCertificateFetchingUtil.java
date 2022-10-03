package com.adventnet.sym.server.mdm.util;

import com.dd.plist.Base64;
import java.util.Iterator;
import java.util.ArrayList;
import org.json.JSONException;
import java.security.cert.Certificate;
import org.json.JSONArray;
import java.security.cert.X509Certificate;
import java.util.HashMap;
import com.me.mdm.certificate.CertificateHandler;
import org.json.JSONObject;
import java.util.logging.Level;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.util.logging.Logger;

public class ServerCertificateFetchingUtil
{
    private Logger logger;
    public static ServerCertificateFetchingUtil serverCertificateFetchingUtil;
    
    public ServerCertificateFetchingUtil() {
        this.logger = Logger.getLogger(ServerCertificateFetchingUtil.class.getName());
    }
    
    public static ServerCertificateFetchingUtil getInstance() {
        if (ServerCertificateFetchingUtil.serverCertificateFetchingUtil == null) {
            ServerCertificateFetchingUtil.serverCertificateFetchingUtil = new ServerCertificateFetchingUtil();
        }
        return ServerCertificateFetchingUtil.serverCertificateFetchingUtil;
    }
    
    public boolean isThirdPartySignedCertificate() {
        try {
            return ApiFactoryProvider.getServerSettingsAPI().getCertificateType() == 2;
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Cannot fetch Certificate type {0}", exp.toString());
            return false;
        }
    }
    
    public JSONObject fetchCertificateJSON() {
        try {
            final int certificateType = ApiFactoryProvider.getServerSettingsAPI().getCertificateType();
            switch (certificateType) {
                case 1:
                case 3:
                case 4: {
                    return this.formJsonFromCertificateMap(CertificateHandler.getInstance().getAllCertificates());
                }
                case 2: {
                    return this.JSONCertificateMapFormForThirdPartyCerts(CertificateHandler.getInstance().getAllCertificates());
                }
            }
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception while fetching certificate {0}", exp.toString());
        }
        return null;
    }
    
    public JSONObject formJsonFromCertificateMap(final HashMap<String, X509Certificate> certificateMap) {
        final JSONObject certificateJSON = new JSONObject();
        final JSONArray intermediateCertificate = new JSONArray();
        try {
            certificateJSON.put("ServerCertificate", (Object)this.formEncodedCertificate(certificateMap.get("ServerCertificate")));
            certificateJSON.put("RootCertificate", (Object)this.formEncodedCertificate(certificateMap.get("RootCertificate")));
            if (certificateMap.containsKey("IntermediateCertificate")) {
                final ArrayList<X509Certificate> intermediateCertificates = CertificateHandler.getInstance().splitIntermediateCertificate(certificateMap.get("IntermediateCertificate"));
                for (final X509Certificate certificate : intermediateCertificates) {
                    intermediateCertificate.put((Object)this.formEncodedCertificate(certificate));
                }
                certificateJSON.put("IntermediateCertificate", (Object)intermediateCertificate);
            }
        }
        catch (final JSONException exception) {
            this.logger.log(Level.SEVERE, "Cannot form JSON with certificate {0}", exception.toString());
        }
        return certificateJSON;
    }
    
    private String formEncodedCertificate(final Certificate cert) {
        try {
            final byte[] encodedCertificateBytes = cert.getEncoded();
            final String encodedCertificate = Base64.encodeBytes(encodedCertificateBytes);
            return encodedCertificate;
        }
        catch (final Exception e) {
            this.logger.log(Level.SEVERE, "Cannot convert certificate to string {0}", e.toString());
            return null;
        }
    }
    
    public JSONObject JSONCertificateMapFormForThirdPartyCerts(final HashMap<String, X509Certificate> certificateMap) {
        final JSONObject certificateJSON = new JSONObject();
        final JSONArray intermediateCertificate = new JSONArray();
        try {
            certificateJSON.put("RootCertificate", (Object)this.formEncodedCertificate(certificateMap.get("RootCertificate")));
            if (certificateMap.containsKey("IntermediateCertificate")) {
                final ArrayList<X509Certificate> intermediateCertificates = CertificateHandler.getInstance().splitIntermediateCertificate(certificateMap.get("IntermediateCertificate"));
                for (final X509Certificate certificate : intermediateCertificates) {
                    intermediateCertificate.put((Object)this.formEncodedCertificate(certificate));
                }
                certificateJSON.put("IntermediateCertificate", (Object)intermediateCertificate);
            }
        }
        catch (final JSONException exception) {
            this.logger.log(Level.SEVERE, "Cannot form JSON with certificate {0}", exception.toString());
        }
        return certificateJSON;
    }
    
    static {
        ServerCertificateFetchingUtil.serverCertificateFetchingUtil = null;
    }
}
