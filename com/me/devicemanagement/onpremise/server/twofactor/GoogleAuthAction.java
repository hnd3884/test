package com.me.devicemanagement.onpremise.server.twofactor;

import com.adventnet.ds.query.UpdateQuery;
import com.adventnet.ds.query.UpdateQueryImpl;
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import org.apache.commons.codec.binary.Base64;
import java.util.Arrays;
import java.security.SecureRandom;
import com.me.devicemanagement.framework.server.factory.ApiFactoryProvider;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.Writer;
import java.awt.image.RenderedImage;
import javax.imageio.ImageIO;
import java.io.File;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.qrcode.QRCodeWriter;
import java.net.URLEncoder;
import java.awt.image.BufferedImage;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.persistence.DataObject;
import com.adventnet.ds.query.SelectQuery;
import com.adventnet.ds.query.Criteria;
import com.adventnet.ds.query.Column;
import com.adventnet.ds.query.SelectQueryImpl;
import com.adventnet.ds.query.Table;
import com.me.devicemanagement.framework.server.authentication.DMUserHandler;
import com.me.devicemanagement.framework.webclient.common.ProductUrlLoader;
import com.me.ems.summaryserver.factory.ProbeMgmtFactoryProvider;
import com.me.devicemanagement.framework.server.util.SyMUtil;
import javax.crypto.Cipher;
import java.util.logging.Logger;

public class GoogleAuthAction
{
    private static Logger logger;
    private String secret;
    private static Cipher cipher;
    private String algorithm;
    private int keyLength;
    private int timePeriod;
    private String keyLabel;
    private long userId;
    private boolean firstTimeStatus;
    private boolean isFirstTime;
    private static final String UTF8 = "UTF-8";
    static final int NUMOFSCRATCHCODES = 5;
    static final int SCRATCHCODESIZE = 8;
    private static final int ENCRYPT_MODE = 1;
    private static final int DECRYPT_MODE = 2;
    private static final String PRODUCTNAME;
    
    private static String getProductName() {
        if (SyMUtil.isProbeServer()) {
            final String probeName = ProbeMgmtFactoryProvider.getProbeDetailsAPI().getCurrentProbeServerDetail().get("PROBE_NAME");
            return probeName.replaceAll("\\s+", "");
        }
        return ProductUrlLoader.getInstance().getValue("displayname").replaceAll("\\s+", "");
    }
    
    public GoogleAuthAction(final long userId) throws Exception {
        this.algorithm = "HmacSHA1";
        this.keyLength = 10;
        this.timePeriod = 30;
        this.setUserId(userId);
        final Long loginID = DMUserHandler.getLoginIdForUserId(Long.valueOf(userId));
        String name = "";
        final SelectQuery selQuery = (SelectQuery)new SelectQueryImpl(new Table("GoogleAuthentication"));
        final Column col = new Column("GoogleAuthentication", "*");
        selQuery.addSelectColumn(col);
        final Column column = new Column("GoogleAuthentication", "LOGIN_ID");
        final Criteria crit = new Criteria(column, (Object)loginID, 0);
        selQuery.setCriteria(crit);
        final DataObject dobj = SyMUtil.getPersistence().get(selQuery);
        if (dobj.containsTable("GoogleAuthentication")) {
            final Row row = dobj.getFirstRow("GoogleAuthentication");
            this.secret = (String)row.get("SECRET_KEY");
            this.keyLabel = (String)row.get("KEY_LABEL");
            this.firstTimeStatus = (boolean)row.get("FIRST_LOGIN_STATUS");
        }
        else {
            this.isFirstTime = true;
            name = DMUserHandler.getUserName(loginID);
            final String label = GoogleAuthAction.PRODUCTNAME + ":" + name;
            this.keyLabel = label;
            this.firstTimeStatus = false;
            this.secret = this.generateSecretKey();
            this.UpdateGoogleTwoFactorDetails();
        }
    }
    
    public boolean isFirstTimeStatus() {
        return this.firstTimeStatus;
    }
    
    public void setFirstTimeStatus(final boolean firstTimeStatus) {
        this.firstTimeStatus = firstTimeStatus;
    }
    
    public String getSecret() {
        return this.secret;
    }
    
    public void setSecret(final String secret) {
        this.secret = secret;
    }
    
    public int getTimePeriod() {
        return this.timePeriod;
    }
    
    public String getKeyLabel() {
        return this.keyLabel;
    }
    
    public void setKeyLabel(final String keyLabel) {
        this.keyLabel = keyLabel;
    }
    
    public long getUserId() {
        return this.userId;
    }
    
    public void setUserId(final long userId) {
        this.userId = userId;
    }
    
    public boolean isFirstTimeKey() {
        return this.isFirstTime;
    }
    
    public String getQRBarPath() {
        String path = "";
        try {
            path = ConvertImageToBase64(generateQRBarCode(this));
        }
        catch (final Exception ex) {
            GoogleAuthAction.logger.log(Level.SEVERE, "Exception in getting QRBarPath" + ex);
        }
        return path;
    }
    
    public static BufferedImage generateQRBarCode(final GoogleAuthAction secKey) {
        BufferedImage bufferedImage = null;
        try {
            final String str = "otpauth://totp/%s?secret=%s&period=%s&issuer=" + GoogleAuthAction.PRODUCTNAME;
            final String frmStr = String.format(str, URLEncoder.encode(secKey.getKeyLabel(), "UTF-8"), secKey.getSecret(), secKey.getTimePeriod());
            final int h = 200;
            final int w = 200;
            final Writer writer = (Writer)new QRCodeWriter();
            final BitMatrix matrix = writer.encode(frmStr, BarcodeFormat.QR_CODE, h, w);
            final BufferedImage bi;
            bufferedImage = (bi = MatrixToImageWriter.toBufferedImage(matrix));
            final File outputfile = new File("saved.png");
            ImageIO.write(bi, "png", outputfile);
        }
        catch (final Exception ex) {
            GoogleAuthAction.logger.log(Level.SEVERE, "Exception in generating QRBarCode" + ex);
        }
        return bufferedImage;
    }
    
    public String getQRImage(final Long userID) {
        String userNameQR = "";
        try {
            final GoogleAuthAction googleAuthAction = new GoogleAuthAction(userID);
            userNameQR = DMUserHandler.getUserName(DMUserHandler.getLoginIdForUserId(userID));
            final String destinationFolder = SyMUtil.getInstallationDir() + File.separator + "webapps" + File.separator + "DesktopCentral" + File.separator + "images" + File.separator + "TwoFactor";
            ApiFactoryProvider.getFileAccessAPI().createDirectory(destinationFolder);
            final String fileName = destinationFolder + File.separator + userNameQR + ".png";
            final BufferedImage bi = generateQRBarCode(googleAuthAction);
            final File outputfile = new File(fileName);
            ImageIO.write(bi, "png", outputfile);
        }
        catch (final Exception ex) {
            GoogleAuthAction.logger.log(Level.SEVERE, "Exception in generating QRImage" + ex);
        }
        return userNameQR;
    }
    
    private String generateSecretKey() {
        final byte[] buffer = new byte[this.keyLength + 40];
        new SecureRandom().nextBytes(buffer);
        final SecretKeyEncoder codec = new SecretKeyEncoder();
        final byte[] secretKey = Arrays.copyOf(buffer, this.keyLength);
        final String encodedKey = SecretKeyEncoder.encode(secretKey);
        return encodedKey;
    }
    
    public void UpdateGoogleTwoFactorDetails() {
        final Row row = new Row("GoogleAuthentication");
        try {
            final Long loginID = DMUserHandler.getLoginIdForUserId(Long.valueOf(this.getUserId()));
            row.set("LOGIN_ID", (Object)loginID);
            row.set("SECRET_KEY", (Object)this.getSecret());
            row.set("KEY_LABEL", (Object)this.getKeyLabel());
            row.set("FIRST_LOGIN_STATUS", (Object)this.isFirstTimeStatus());
            final DataObject dobj = SyMUtil.getPersistence().get("GoogleAuthentication", (Criteria)null);
            if (this.isFirstTimeKey()) {
                dobj.addRow(row);
                GoogleAuthAction.logger.log(Level.INFO, "Row Added in GoogleAuth successfully");
            }
            else {
                dobj.updateRow(row);
                GoogleAuthAction.logger.log(Level.INFO, "Row Updated in GoogleAuth successfully");
            }
            SyMUtil.getPersistence().update(dobj);
        }
        catch (final Exception e) {
            GoogleAuthAction.logger.log(Level.SEVERE, "Exception in updating Google Two Factor Details" + e);
        }
    }
    
    public static String ConvertImageToBase64(final BufferedImage image) {
        final String encodedString = null;
        try {
            final Base64 encoder = new Base64();
            final ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(image, "png", baos);
            final String encodedImage = new String(Base64.encodeBase64(baos.toByteArray()));
            if (encodedImage != null) {
                return encodedImage;
            }
        }
        catch (final Exception e) {
            GoogleAuthAction.logger.log(Level.SEVERE, "Exception in converting image to Base64" + e);
        }
        return encodedString;
    }
    
    public void updateUserFirstTimeStatus(final long userId, final boolean isVerified) throws Exception {
        final UpdateQueryImpl query = new UpdateQueryImpl("GoogleAuthentication");
        final Column column = new Column("GoogleAuthentication", "LOGIN_ID");
        final Long loginID = DMUserHandler.getLoginIdForUserId(Long.valueOf(userId));
        final Criteria crit = new Criteria(column, (Object)loginID, 0);
        query.setCriteria(crit);
        query.setUpdateColumn("FIRST_LOGIN_STATUS", (Object)isVerified);
        SyMUtil.getPersistence().update((UpdateQuery)query);
        GoogleAuthAction.logger.log(Level.INFO, "User totp Status updated successfully");
    }
    
    static {
        GoogleAuthAction.logger = Logger.getLogger("UserManagementLogger");
        PRODUCTNAME = getProductName();
    }
}
