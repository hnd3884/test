package com.adventnet.sym.server.mdm.certificates.csr;

import java.security.Provider;
import java.security.Security;
import org.bouncycastle.jce.provider.BouncyCastleProvider;
import java.io.InputStream;
import com.adventnet.ds.query.Criteria;
import java.io.ByteArrayOutputStream;
import org.bouncycastle.asn1.x500.style.BCStyle;
import org.bouncycastle.pkcs.PKCS10CertificationRequest;
import org.bouncycastle.operator.ContentSigner;
import java.security.KeyPair;
import org.bouncycastle.asn1.x500.X500NameBuilder;
import java.io.Writer;
import org.bouncycastle.openssl.PEMWriter;
import java.io.StringWriter;
import org.bouncycastle.pkcs.PKCS10CertificationRequestBuilder;
import org.bouncycastle.asn1.x509.SubjectPublicKeyInfo;
import org.bouncycastle.operator.jcajce.JcaContentSignerBuilder;
import java.security.SecureRandom;
import java.security.KeyPairGenerator;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.me.mdm.api.error.APIHTTPException;
import java.util.logging.Level;
import org.json.JSONObject;
import java.util.logging.Logger;

public class MdmCsrHandler
{
    public static Logger logger;
    private static final String CSR_FILE = "Csr.csr";
    
    public static Long createCSR(final JSONObject csrInfo) throws Exception {
        final String csrDirectoryPath = csrInfo.getString("CSR_DIRECTORY");
        final String CSR_FILE_NAME = csrInfo.getString("CSR_FILE_NAME");
        final String PRIVATE_KEY_FILE_NAME = csrInfo.getString("PRIVATE_KEY_FILE_NAME");
        try {
            final JSONObject tempJson = csrInfo.getJSONObject("msg_body");
            if (tempJson.has(CsrConstants.Api.Key.common_name)) {
                csrInfo.put("COMMON_NAME", (Object)tempJson.getString(CsrConstants.Api.Key.common_name));
            }
            if (tempJson.has(CsrConstants.Api.Key.org_email)) {
                csrInfo.put("EMAIL_ADDRESS", (Object)tempJson.getString(CsrConstants.Api.Key.org_email));
            }
            if (tempJson.has(CsrConstants.Api.Key.org_name)) {
                csrInfo.put("ORGANIZATION_NAME", (Object)tempJson.getString(CsrConstants.Api.Key.org_name));
            }
            if (tempJson.has(CsrConstants.Api.Key.org_unit_name)) {
                csrInfo.put("ORGANIZATIONAL_UNIT", (Object)tempJson.getString(CsrConstants.Api.Key.org_unit_name));
            }
            if (tempJson.has(CsrConstants.Api.Key.locality)) {
                csrInfo.put("LOCALITY", (Object)tempJson.getString(CsrConstants.Api.Key.locality));
            }
            if (tempJson.has(CsrConstants.Api.Key.street)) {
                csrInfo.put("STREET", (Object)tempJson.getString(CsrConstants.Api.Key.street));
            }
            if (tempJson.has(CsrConstants.Api.Key.country)) {
                csrInfo.put("COUNTRY", (Object)tempJson.getString(CsrConstants.Api.Key.country));
            }
            csrInfo.put("CSR_ID", tempJson.optLong(CsrConstants.Api.Key.csr_id));
            csrInfo.put("CSR_CREATED_TIME", System.currentTimeMillis());
        }
        catch (final Exception e) {
            MdmCsrHandler.logger.log(Level.FINE, "Exception while retreiving data from json ", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
        try {
            MDMUtil.getUserTransaction().begin();
            MdmCsrHandler.logger.log(Level.FINE, "Beginning the transaction for populating CSR");
            MdmCsrDbHandler.addOrUpdateMdmCsrInfo(csrInfo);
            final String csrLocation = csrDirectoryPath + File.separator + csrInfo.getLong("CSR_ID") + File.separator + CSR_FILE_NAME;
            final String privateKeyLocation = csrDirectoryPath + File.separator + csrInfo.getLong("CSR_ID") + File.separator + PRIVATE_KEY_FILE_NAME;
            csrInfo.put("CSR_LOCATION", (Object)csrLocation);
            csrInfo.put("PRIVATEKEY_LOCATION", (Object)privateKeyLocation);
            writeCsrAndKey(csrInfo);
            MdmCsrDbHandler.addOrUpdateMdmCsrInfo(csrInfo);
            MDMUtil.getUserTransaction().commit();
            MdmCsrHandler.logger.log(Level.FINE, "Populated CSR tables");
        }
        catch (final Exception e) {
            MdmCsrHandler.logger.log(Level.WARNING, "Unable to create CSR ", e);
            throw new APIHTTPException("COM0004", new Object[] { e });
        }
        MdmCsrHandler.logger.log(Level.FINE, "CSR and Private Key files generated");
        return csrInfo.getLong("CSR_ID");
    }
    
    private static void writeCsrAndKey(final JSONObject csrInfo) throws Exception {
        MdmCsrHandler.logger.log(Level.FINE, "Beginning to create CSR");
        final String csrDirName = csrInfo.getString("CSR_DIRECTORY");
        final String csrLocation = csrInfo.getString("CSR_LOCATION");
        final String privateKeyLocation = csrInfo.getString("PRIVATEKEY_LOCATION");
        MdmCsrHandler.logger.log(Level.FINE, "Writing csr");
        generateCsr(csrInfo);
        final String csr = csrInfo.getString("CSR");
        final String privateKey = csrInfo.getString("PRIVATE_KEY");
        ApiFactoryProvider.getFileAccessAPI().createDirectory(csrDirName);
        MdmCsrHandler.logger.log(Level.FINE, "CSR directory created successfully");
        ApiFactoryProvider.getFileAccessAPI().writeFile(csrLocation, csr.getBytes());
        ApiFactoryProvider.getFileAccessAPI().writeFile(privateKeyLocation, privateKey.getBytes());
        MdmCsrHandler.logger.log(Level.FINE, "CSR and private key successfully stored");
    }
    
    private static JSONObject generateCsr(final JSONObject jsonObject) {
        MdmCsrHandler.logger.log(Level.FINE, "Generating CSR");
        try {
            final X500NameBuilder builder = buildX500Name(jsonObject);
            final KeyPairGenerator kpGen = KeyPairGenerator.getInstance("RSA");
            kpGen.initialize(2048, new SecureRandom());
            final JcaContentSignerBuilder signerBuilder = new JcaContentSignerBuilder("SHA256WithRSA");
            final KeyPair keyPair = kpGen.generateKeyPair();
            final SubjectPublicKeyInfo publicKeyInfo = SubjectPublicKeyInfo.getInstance((Object)keyPair.getPublic().getEncoded());
            final PKCS10CertificationRequestBuilder csrBuilder = new PKCS10CertificationRequestBuilder(builder.build(), publicKeyInfo);
            final ContentSigner signer = signerBuilder.build(keyPair.getPrivate());
            final PKCS10CertificationRequest csr = csrBuilder.build(signer);
            final StringWriter csrContent = new StringWriter();
            final PEMWriter csrWriter = new PEMWriter((Writer)csrContent);
            csrWriter.writeObject((Object)csr);
            csrWriter.flush();
            csrWriter.close();
            final StringWriter privateKeyContent = new StringWriter();
            final PEMWriter keyWriter = new PEMWriter((Writer)privateKeyContent);
            keyWriter.writeObject((Object)keyPair.getPrivate());
            keyWriter.flush();
            keyWriter.close();
            jsonObject.put("CSR", (Object)csrContent.getBuffer().toString());
            jsonObject.put("PRIVATE_KEY", (Object)privateKeyContent.getBuffer().toString());
            jsonObject.put("ENCODED_CSR", (Object)csr.getEncoded());
            MdmCsrHandler.logger.log(Level.FINE, "CSR generated successfully");
            return jsonObject;
        }
        catch (final Exception ex) {
            MdmCsrHandler.logger.log(Level.SEVERE, "Exception while generating Csr ", ex);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    public static X500NameBuilder buildX500Name(final JSONObject jsonObject) throws Exception {
        final X500NameBuilder builder = new X500NameBuilder(BCStyle.INSTANCE);
        MdmCsrHandler.logger.log(Level.FINE, "Building X500Name");
        if (jsonObject != null) {
            if (!jsonObject.isNull("COUNTRY")) {
                builder.addRDN(BCStyle.C, jsonObject.getString("COUNTRY"));
            }
            if (!jsonObject.isNull("ORGANIZATION_NAME")) {
                builder.addRDN(BCStyle.O, jsonObject.getString("ORGANIZATION_NAME"));
            }
            if (!jsonObject.isNull("ORGANIZATIONAL_UNIT")) {
                builder.addRDN(BCStyle.OU, jsonObject.getString("ORGANIZATIONAL_UNIT"));
            }
            if (!jsonObject.isNull("COMMON_NAME")) {
                builder.addRDN(BCStyle.CN, jsonObject.getString("COMMON_NAME"));
            }
            if (!jsonObject.isNull("LOCALITY")) {
                builder.addRDN(BCStyle.ST, jsonObject.getString("LOCALITY"));
            }
            if (!jsonObject.isNull("STREET")) {
                builder.addRDN(BCStyle.STREET, jsonObject.getString("STREET"));
            }
            if (!jsonObject.isNull("EMAIL_ADDRESS")) {
                builder.addRDN(BCStyle.E, jsonObject.getString("EMAIL_ADDRESS"));
            }
        }
        MdmCsrHandler.logger.log(Level.FINE, "X500Name Builded");
        return builder;
    }
    
    public static ByteArrayOutputStream downloadCsr(final JSONObject jsonObject) {
        try {
            final Long csrID = jsonObject.getLong(CsrConstants.Api.Key.csrrequest_id);
            final Long customerID = jsonObject.getLong("CUSTOMER_ID");
            MdmCsrHandler.logger.log(Level.FINE, "Downloading CSR for CSR_ID: {0}", csrID);
            final Criteria customerIDCriteria = MdmCsrDbHandler.getMdmCsrInfoCustomerIDCriteria(customerID);
            final Criteria csrIDCriteria = MdmCsrDbHandler.getMdmCsrInfoIDCriteria(csrID);
            final JSONObject csrDetails = MdmCsrDbHandler.getMdmCsrInfo(csrIDCriteria.and(customerIDCriteria));
            final String filePath = (String)csrDetails.get("CSR_LOCATION");
            MdmCsrHandler.logger.log(Level.FINE, "CSR file path: {0}", filePath);
            if (filePath != null) {
                MdmCsrHandler.logger.log(Level.FINE, "Reading CSR");
                final InputStream is = ApiFactoryProvider.getFileAccessAPI().readFile(filePath);
                final byte[] bytes = new byte[4096];
                final ByteArrayOutputStream os = new ByteArrayOutputStream();
                int read;
                while ((read = is.read(bytes)) != -1) {
                    os.write(bytes, 0, read);
                }
                MdmCsrHandler.logger.log(Level.FINE, "CSR Download successful..");
                return os;
            }
            MdmCsrHandler.logger.log(Level.FINE, "CSR download bad request.. ");
            throw new APIHTTPException("COM0014", new Object[0]);
        }
        catch (final Exception e) {
            MdmCsrHandler.logger.log(Level.WARNING, "Exception while downloading CSR..", e);
            throw new APIHTTPException("COM0004", new Object[0]);
        }
    }
    
    static {
        MdmCsrHandler.logger = Logger.getLogger("MdmCertificateIntegLogger");
        Security.addProvider((Provider)new BouncyCastleProvider());
    }
}
