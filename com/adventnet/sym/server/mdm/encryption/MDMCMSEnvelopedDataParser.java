package com.adventnet.sym.server.mdm.encryption;

import java.util.Iterator;
import java.util.Collection;
import java.util.Enumeration;
import com.me.mdm.server.factory.MDMApiFactoryProvider;
import java.security.KeyStore;
import java.security.PrivateKey;
import java.io.InputStream;
import org.bouncycastle.cms.Recipient;
import org.bouncycastle.cms.RecipientInformation;
import java.io.IOException;
import org.bouncycastle.cms.CMSException;
import org.apache.commons.io.IOUtils;
import org.bouncycastle.cms.jcajce.JceKeyTransEnvelopedRecipient;
import org.bouncycastle.cms.CMSEnvelopedDataParser;
import org.json.JSONObject;
import org.json.JSONException;
import java.util.logging.Level;
import java.io.File;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import com.adventnet.sym.server.mdm.config.ProfileCertificateUtil;
import java.util.logging.Logger;

public class MDMCMSEnvelopedDataParser
{
    public static Logger logger;
    
    public static byte[] decodeWithMDMCertificate(final byte[] cMSEnvelopedRawData, final Long certificateID) {
        try {
            final JSONObject certificateFIleJSON = ProfileCertificateUtil.getCertificateFileJSON(certificateID);
            final Long customerID = certificateFIleJSON.getLong("CUSTOMER_ID");
            final String fileName = String.valueOf(certificateFIleJSON.get("CERTIFICATE_FILE_NAME"));
            final String filePassword = String.valueOf(certificateFIleJSON.get("CERTIFICATE_PASSWORD"));
            final String filePath = MDMUtil.getCredentialCertificateFolder(customerID);
            final String completeFilePath = filePath + File.separator + fileName;
            final byte[] decodedData = decryptData(cMSEnvelopedRawData, completeFilePath, filePassword);
            return decodedData;
        }
        catch (final JSONException ex) {
            MDMCMSEnvelopedDataParser.logger.log(Level.SEVERE, "FileVaultLog: JSONException in decodeWithMDMCertificate() ", (Throwable)ex);
        }
        catch (final Exception ex2) {
            MDMCMSEnvelopedDataParser.logger.log(Level.SEVERE, "FileVaultLog: Exception in decodeWithMDMCertificate() ", ex2);
        }
        return null;
    }
    
    public static byte[] decryptData(final byte[] cMSEnvelopedRawData, final String pkcs12FileCompletePathWithName, final String pkcs12FilePassword) throws Exception {
        final byte[] parsedOutPut = new byte[cMSEnvelopedRawData.length];
        try {
            final CMSEnvelopedDataParser parser = new CMSEnvelopedDataParser(cMSEnvelopedRawData);
            final RecipientInformation recInfo = getSingleRecipient(parser);
            MDMCMSEnvelopedDataParser.logger.log(Level.INFO, "FileVaultLog: Going to decode personalRecovery key with file :{0}", pkcs12FileCompletePathWithName);
            final Recipient recipient = (Recipient)new JceKeyTransEnvelopedRecipient(getPrivateKey(pkcs12FileCompletePathWithName, pkcs12FilePassword));
            final InputStream decryptedStream = recInfo.getContentStream(recipient).getContentStream();
            Throwable localThrowable3 = null;
            try {
                final String b = IOUtils.toString(decryptedStream);
                MDMCMSEnvelopedDataParser.logger.log(Level.INFO, "FileVaultLog: Sucessfully decoded PersonalRecoveryKey from CMS blob...");
                return b.getBytes();
            }
            catch (final Throwable localThrowable4) {
                localThrowable3 = localThrowable4;
                MDMCMSEnvelopedDataParser.logger.log(Level.SEVERE, "FileVaultLog: Exception we converting bytes to string{0}", localThrowable3);
                throw localThrowable4;
            }
            finally {
                if (decryptedStream != null) {
                    if (localThrowable3 != null) {
                        try {
                            decryptedStream.close();
                        }
                        catch (final Throwable localThrowable5) {
                            localThrowable3.addSuppressed(localThrowable5);
                        }
                    }
                    else {
                        decryptedStream.close();
                    }
                }
            }
        }
        catch (final CMSException ex) {
            MDMCMSEnvelopedDataParser.logger.log(Level.SEVERE, "FileVaultLog: CMSException in decodeWithMDMCertificate() ", (Throwable)ex);
        }
        catch (final IOException ex2) {
            MDMCMSEnvelopedDataParser.logger.log(Level.SEVERE, "FileVaultLog: IOException in decodeWithMDMCertificate() ", ex2);
        }
        MDMCMSEnvelopedDataParser.logger.log(Level.INFO, "FileVaultLog: Unable to read CMS Signed Personal recovery key , returning null..");
        return null;
    }
    
    private static PrivateKey getPrivateKey(final String fileNamewithPath, final String password) throws Exception {
        InputStream fis = null;
        final KeyStore ks = KeyStore.getInstance("PKCS12");
        try {
            fis = MDMApiFactoryProvider.getFileAccessAPI().getInputStream(fileNamewithPath);
        }
        catch (final Exception ex) {
            MDMCMSEnvelopedDataParser.logger.log(Level.INFO, "FileVaultLog: Unable to read file for decoding PersonalRecoveryKey:{0}", fileNamewithPath);
        }
        if (fis == null) {
            return null;
        }
        Throwable localThrowable3 = null;
        try {
            ks.load(fis, password.toCharArray());
        }
        catch (final Throwable localThrowable4) {
            localThrowable3 = localThrowable4;
            throw localThrowable4;
        }
        finally {
            if (fis != null) {
                if (localThrowable3 != null) {
                    try {
                        fis.close();
                    }
                    catch (final Throwable localThrowable5) {
                        localThrowable3.addSuppressed(localThrowable5);
                    }
                }
                else {
                    fis.close();
                }
            }
        }
        final Enumeration<String> aliases = ks.aliases();
        final String alias = aliases.nextElement();
        return (PrivateKey)ks.getKey(alias, password.toCharArray());
    }
    
    private static RecipientInformation getSingleRecipient(final CMSEnvelopedDataParser parser) throws Exception {
        final Collection recInfos = parser.getRecipientInfos().getRecipients();
        final Iterator recipientIterator = recInfos.iterator();
        if (!recipientIterator.hasNext()) {
            throw new Exception("could_not_find_recipient");
        }
        return recipientIterator.next();
    }
    
    static {
        MDMCMSEnvelopedDataParser.logger = Logger.getLogger("MDMConfigLogger");
    }
}
