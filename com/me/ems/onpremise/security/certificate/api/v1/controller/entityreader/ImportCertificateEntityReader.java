package com.me.ems.onpremise.security.certificate.api.v1.controller.entityreader;

import java.io.IOException;
import com.me.ems.framework.common.api.utils.APIException;
import org.json.JSONException;
import com.adventnet.persistence.DataAccessException;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import java.nio.file.Paths;
import com.me.devicemanagement.framework.server.customer.CustomerInfoUtil;
import com.me.ems.framework.common.api.utils.FileAccess;
import javax.ws.rs.WebApplicationException;
import java.util.logging.Level;
import org.json.JSONArray;
import java.nio.file.Path;
import java.util.ArrayList;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import org.json.JSONObject;
import org.apache.commons.io.IOUtils;
import java.io.InputStream;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.MediaType;
import java.lang.annotation.Annotation;
import java.lang.reflect.Type;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.ext.Provider;
import com.me.ems.onpremise.security.certificate.api.model.CertificateFormBean;
import javax.ws.rs.ext.MessageBodyReader;

@Provider
@Consumes({ "application/importCertificateData.v1+json" })
public class ImportCertificateEntityReader implements MessageBodyReader<CertificateFormBean>
{
    Logger logger;
    
    public ImportCertificateEntityReader() {
        this.logger = Logger.getLogger("ImportCertificateLogger");
    }
    
    public boolean isReadable(final Class<?> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType) {
        return type == CertificateFormBean.class;
    }
    
    public CertificateFormBean readFrom(final Class<CertificateFormBean> type, final Type genericType, final Annotation[] annotations, final MediaType mediaType, final MultivaluedMap<String, String> httpHeaders, final InputStream entityStream) throws WebApplicationException {
        final CertificateFormBean certificateFormBean = new CertificateFormBean();
        try {
            final byte[] entityByteArray = IOUtils.toByteArray(entityStream);
            final JSONObject requestJSON = new JSONObject(new String(entityByteArray));
            certificateFormBean.setConfirmedChangeInNatSettings(requestJSON.optBoolean("confirmedChangeInNatSettings"));
            certificateFormBean.setConfirmedSelfSignedCA(requestJSON.optBoolean("isSelfSignedCAConfirmed"));
            final Path certFilePath = getFilePathFromFileId(requestJSON.getJSONObject("serverCertificate"));
            certificateFormBean.setServerCertificateFilePath(certFilePath);
            certificateFormBean.setServerCertificateFileName(certFilePath.getFileName().toString());
            if (certificateFormBean.getServerCertificateFilePath().getFileName().toString().toLowerCase().endsWith(".keystore") || certificateFormBean.getServerCertificateFilePath().getFileName().toString().toLowerCase().endsWith(".jks") || certificateFormBean.getServerCertificateFilePath().getFileName().toString().toLowerCase().endsWith(".pfx")) {
                certificateFormBean.setPfxPassword(SyMUtil.decodeAsUTF16LE(requestJSON.optString("pfxPassword")));
                certificateFormBean.setPFXCertificateUploaded(Boolean.TRUE);
                certificateFormBean.setAutomatic(Boolean.TRUE);
            }
            else if (certificateFormBean.getServerCertificateFilePath().getFileName().toString().toLowerCase().endsWith(".crt") || certificateFormBean.getServerCertificateFilePath().getFileName().toString().toLowerCase().endsWith(".cer") || certificateFormBean.getServerCertificateFilePath().getFileName().toString().toLowerCase().endsWith(".der")) {
                certificateFormBean.setPrivateKeyFilePath(getFilePathFromFileId(requestJSON.getJSONObject("serverKey")));
                final Boolean isAutomatic = requestJSON.getBoolean("isAutomatic");
                certificateFormBean.setAutomatic(isAutomatic);
                certificateFormBean.setPFXCertificateUploaded(Boolean.FALSE);
                if (!isAutomatic) {
                    final ArrayList<Path> intermediateCertificatesArrayList = new ArrayList<Path>();
                    final JSONArray intermediateCertificates = (JSONArray)requestJSON.get("intermediateCertificates");
                    for (Integer index = 0; index < intermediateCertificates.length(); ++index) {
                        intermediateCertificatesArrayList.add(getFilePathFromFileId((JSONObject)intermediateCertificates.get((int)index)));
                    }
                    certificateFormBean.setIntermediateCount(intermediateCertificatesArrayList.size());
                    certificateFormBean.setIntermediateCertificateFilePathList(intermediateCertificatesArrayList);
                }
            }
            this.logger.info(certificateFormBean.toString());
        }
        catch (final Exception exception) {
            this.logger.log(Level.SEVERE, "Exception occurred in ImportCertificateEntityReader ", exception);
        }
        return certificateFormBean;
    }
    
    private static Path getFilePathFromFileId(final JSONObject fileInfoJSON) throws DataAccessException, JSONException, APIException {
        final Logger logger = Logger.getLogger("ImportCertificateLogger");
        logger.info("File Info JSON: " + fileInfoJSON.toString(4));
        Path filePathObject = null;
        final FileAccess fileAccess = new FileAccess();
        final Long fileId = Long.valueOf((String)fileInfoJSON.get("fileID"));
        final Long customerID = CustomerInfoUtil.getInstance().getDefaultCustomer();
        final DataObject fileDO = fileAccess.getFileDetailsDO(fileId, customerID, "Security_Mgmt");
        final Row dcfilesRow = fileDO.getRow("DCFiles");
        filePathObject = Paths.get(String.valueOf(dcfilesRow.get("FILE_SYSTEM_LOCATION")), new String[0]);
        return filePathObject;
    }
}
