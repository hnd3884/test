package com.adventnet.sym.server.mdm.apps.android.apkextractor;

import org.json.JSONException;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.Iterator;
import java.io.FileNotFoundException;
import java.security.cert.CertificateException;
import org.json.JSONArray;
import java.util.regex.Pattern;
import java.io.Reader;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.File;
import java.security.cert.Certificate;
import java.io.InputStream;
import java.security.cert.CertificateFactory;
import java.io.FileInputStream;
import java.util.Collection;
import java.util.Arrays;
import java.util.ArrayList;
import org.json.JSONObject;

public class ApkSignatureParser
{
    private JSONObject signatureInfo;
    private String signatureString;
    private String signatureTxtPath;
    private boolean debugEnabled;
    private JSONObject keyToolSignInfo;
    private String folderPath;
    private String apkPath;
    private String filePath;
    private ArrayList<String> infoList;
    
    public ApkSignatureParser(final String folderPath, final String apkPath, final String filePath) {
        this.debugEnabled = false;
        this.infoList = new ArrayList<String>(Arrays.asList("Version", "Subject", "Signature Algorithm", "Issuer", "SerialNumber", "Algorithm"));
        this.folderPath = folderPath;
        this.apkPath = apkPath;
        this.filePath = filePath;
    }
    
    public void parse() throws FileNotFoundException, IOException, JSONException {
        final String signaturePath = ApkExtractionUtilities.unzip(this.apkPath, this.filePath, this.folderPath, 2);
        if (signaturePath != null) {
            final FileInputStream fis = new FileInputStream(signaturePath);
            try {
                final CertificateFactory cf = CertificateFactory.getInstance("X.509");
                final Collection<? extends Certificate> c = cf.generateCertificates(fis);
                final Iterator<? extends Certificate> iter = c.iterator();
                final Certificate cert = (Certificate)iter.next();
                this.signatureTxtPath = this.folderPath + File.separator + "signature.txt";
                final File file = new File(this.signatureTxtPath);
                final FileWriter fileWriter = new FileWriter(file);
                fileWriter.write(cert.toString());
                fileWriter.flush();
                fileWriter.close();
                final JSONObject signatureObject = new JSONObject();
                final FileReader fileReader = new FileReader(this.signatureTxtPath);
                final BufferedReader bufferedReader = new BufferedReader(fileReader);
                String line = null;
                final Pattern p = Pattern.compile("([ a-zA-Z0-9]+): ?(.+)?");
                while ((line = bufferedReader.readLine()) != null) {
                    final Matcher m = p.matcher(line);
                    if (line.contains("=Android Debug")) {
                        this.debugEnabled = true;
                    }
                    if (m.find()) {
                        if (this.infoList.contains(m.group(1).trim())) {
                            signatureObject.put(m.group(1).trim(), (Object)m.group(2).trim());
                        }
                        else if (line.contains("Valid")) {
                            final JSONObject temp = new JSONObject();
                            final String validFromDate = line.split("From:")[1].replaceAll("[,|\\]]", "").trim();
                            temp.put("from", (Object)validFromDate);
                            line = bufferedReader.readLine();
                            final String validToDate = line.split("To:")[1].replaceAll("[,|\\]]", "").trim();
                            temp.put("to", (Object)validToDate);
                            signatureObject.put("Validity", (Object)temp);
                        }
                        else {
                            if (!line.contains("Signature")) {
                                continue;
                            }
                            String line2 = null;
                            final JSONArray temp2 = new JSONArray();
                            while ((line2 = bufferedReader.readLine()) != null) {
                                if (line2.length() > 2) {
                                    temp2.put((Object)line2.trim());
                                }
                            }
                            signatureObject.put("Signature", (Object)temp2);
                        }
                    }
                }
                fileReader.close();
                bufferedReader.close();
                this.signatureInfo = signatureObject;
            }
            catch (final CertificateException e) {
                e.printStackTrace();
            }
            finally {
                fis.close();
            }
            return;
        }
        throw new FileNotFoundException("Could not find file containing apk signatures.");
    }
    
    public JSONObject getSignatureInfo() {
        return this.signatureInfo;
    }
    
    public JSONObject getKeyToolSignInfo() {
        return this.keyToolSignInfo;
    }
    
    public String getSignatureString() {
        return this.signatureString;
    }
    
    public boolean isDebugEnabled() {
        return this.debugEnabled;
    }
    
    public void parseKeyToolSign() throws IOException {
        final String keyToolSignPath = this.folderPath + File.separator + "keytoolsign.txt";
        FileReader fileReader = null;
        BufferedReader bufferedReader = null;
        try {
            fileReader = new FileReader(keyToolSignPath);
            bufferedReader = new BufferedReader(fileReader);
            String line = null;
            final JSONObject signatureObject = new JSONObject();
            while ((line = bufferedReader.readLine()) != null) {
                if (line.trim().startsWith("Signature algorithm name:")) {
                    final String algorithm = line.substring(line.indexOf(":") + 1);
                    signatureObject.put("algorithm", (Object)algorithm.trim());
                }
                else if (line.trim().startsWith("MD5:")) {
                    final String md5Sign = line.substring(line.indexOf(":") + 1);
                    signatureObject.put("MD5", (Object)md5Sign.trim());
                }
                else if (line.trim().startsWith("SHA1:")) {
                    final String sha1Sign = line.substring(line.indexOf(":") + 1);
                    signatureObject.put("SHA1", (Object)sha1Sign.trim());
                }
                else if (line.trim().startsWith("SHA256:")) {
                    final String sha256Sign = line.substring(line.indexOf(":") + 1);
                    signatureObject.put("SHA256", (Object)sha256Sign.trim());
                }
                else {
                    if (!line.trim().startsWith("Not a signed")) {
                        continue;
                    }
                    signatureObject.put("unsignedAPK", true);
                }
            }
            this.keyToolSignInfo = signatureObject;
        }
        finally {
            if (fileReader != null) {
                fileReader.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }
    }
}
