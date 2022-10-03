package com.adventnet.tools.prevalent;

import java.util.Hashtable;
import java.net.URL;
import java.util.Properties;
import java.util.HashMap;
import java.util.Iterator;
import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.Calendar;
import java.io.File;
import java.util.Date;
import java.util.ArrayList;

public final class Validation
{
    private static Validation valid;
    private int type;
    private boolean copyLicense;
    private String newtype;
    private String category;
    private String companyName;
    private String userName;
    private String productName;
    private String productVersion;
    private boolean rtuser;
    private String invalidFile;
    private String contactMesg;
    private ArrayList components;
    private final int NEWLICENSE = 0;
    private final int OLDLICENSE = 1;
    private int LICENSETYPE;
    private Date expiryDate;
    private boolean register;
    private boolean fnf;
    private boolean invalid;
    private boolean invalidUser;
    private String userType;
    
    private Validation() {
        this.copyLicense = true;
        this.newtype = null;
        this.category = null;
        this.companyName = null;
        this.userName = null;
        this.productName = null;
        this.productVersion = null;
        this.rtuser = false;
        this.invalidFile = "Invalid License File";
        this.contactMesg = "Please contact\n\nAdventNet, Inc. \n5645 Gibraltar Drive\nPleasanton, CA 94588 USA\nPhone: +1-925-924-9500\nFax : +1-925-924-9600\nEmail : info@adventnet.com\nWebSite : http://www.adventnet.com";
        this.components = null;
        this.LICENSETYPE = -1;
        this.expiryDate = null;
        this.register = false;
        this.fnf = false;
        this.invalid = false;
        this.invalidUser = false;
        this.userType = null;
    }
    
    public static Validation getInstance() {
        if (Validation.valid == null) {
            Validation.valid = new Validation();
        }
        return Validation.valid;
    }
    
    public void reInitialize() {
        if (Validation.valid != null) {
            Validation.valid = null;
        }
    }
    
    public void validate(final String lFilePath, final boolean mode) {
        this.rtuser = false;
        File lFile = this.getXFile(lFilePath, "AdventNetLicense.xml");
        if (this.getXFile(lFilePath, "AdventNetLicense.xml").exists()) {
            lFile = this.getXFile(lFilePath, "AdventNetLicense.xml");
            final String licenseFile = lFile.toString();
            final Vendee client = Vendee.getInstance();
            client.setMode(mode);
            if (lFile.exists()) {
                if (this.validateFile(licenseFile, mode)) {
                    this.rtuser = true;
                }
            }
            else if (client.readFile()) {
                this.setProductName(client.getProductName());
                this.setProductVersion(client.getProductVersion());
                this.setUserName(client.getUserName());
                this.setCompanyName(client.getCompanyName());
                this.setUserType(client.getUserType());
                this.LICENSETYPE = 1;
                this.rtuser = true;
            }
            else {
                this.showError("ERROR CODE : 505", mode, 505);
            }
        }
    }
    
    public boolean doValidation(final String lFilePath, final String userName, final String filePath, final boolean mode) {
        return this.doValidation(lFilePath, userName, filePath, mode, true);
    }
    
    private boolean validateMap(final int inid, final int dmap) {
        final int baseId = new ReadBaseLicense().readLicense().getBaseObject()[0];
        return dmap == baseId && new ReadLicense().isAllowedProduct(inid);
    }
    
    public boolean doValidation(final String lFilePath, final String userName, final String filePath, final boolean mode, final boolean diplayEvalPeriod) {
        this.LICENSETYPE = 0;
        String licenseFile = null;
        final File file = new File(filePath);
        if (!file.isAbsolute()) {
            licenseFile = file.getAbsolutePath();
        }
        else {
            licenseFile = filePath;
        }
        if (!file.exists()) {
            this.setFNFError();
            this.showError("ERROR CODE : 506", mode, 506);
            return false;
        }
        InputFileParser parser = null;
        try {
            parser = new InputFileParser(licenseFile);
        }
        catch (final Exception exp) {
            this.setInvalidFileError();
            this.showError("ERROR CODE : 507", mode, 507);
            return false;
        }
        final DataClass data = parser.getDataClass();
        final ArrayList users = data.getUsers();
        final ArrayList licensee = data.getDetails();
        final User user = data.getUserObject(userName);
        if (user == null) {
            this.setInvalidUserError();
            this.showError("ERROR CODE : 508", mode, 508);
            return false;
        }
        this.setUserName(userName);
        final String type = user.getLicenseType();
        final String company = user.getCompanyName();
        this.setCompanyName(company);
        final String mailID = user.getMailId();
        String evalDays = user.getNumberOfDays();
        final String macID = user.getMacId();
        final String expiryDate = user.getExpiryDate();
        final String key = user.getKey();
        final String wholeKey = data.getLicenseFileKey();
        final ArrayList ID = user.getIDs();
        try {
            this.copyLicense = true;
            final Indication indicate = Indication.getInstance();
            indicate.deSerialize();
            final int map = indicate.getProductName();
            indicate.productNameDeSerialize();
            final int prod = indicate.getProductNameInt();
            final boolean isNative = indicate.isNativeFilesToBeBundled();
            final boolean isTrialMacBased = indicate.isTrialMacBased();
            final boolean mandatoryMacBased = indicate.mandatoryMacLicense();
            final String regCheck = indicate.getTheRegCheck();
            if (map != prod) {
                this.showError("ERROR CODE : 509", mode, 507);
                return false;
            }
            final String encodedKey = Encode.getFinalKey(data.getWholeKeyBuffer());
            if (!encodedKey.equals(wholeKey)) {
                this.setInvalidFileError();
                this.showError("ERROR CODE : 488", mode, 488);
                return false;
            }
            final String productVersion = indicate.getProductVersion();
            this.setProductVersion(productVersion);
            for (int idSize = ID.size(), j = 0; j < idSize; ++j) {
                final String mapID = ID.get(j);
                final Details details = data.getDetails(mapID);
                if (details == null) {
                    this.showError("ERROR CODE : 510", mode, 510);
                    return false;
                }
                final String prd = details.getProductName();
                final String ver = details.getProductVersion();
                final String prdLType = details.getProductLicenseType();
                final String category = details.getProductCategory();
                this.setProductName(prd);
                this.setComponents(details.getComponents());
                this.setType(prdLType);
                this.setCategory(category);
                if (prd == null || ver == null) {
                    this.showError("ERROR CODE : 511", mode, 511);
                    return false;
                }
                final int iamValidation = this.meIamValidation(details.isComponentPresent("meiam"));
                if (iamValidation > 0) {
                    this.showError("ERROR CODE : " + iamValidation, mode, iamValidation);
                    return false;
                }
                int value = Laterality.getMapValue(prd);
                if (value == -1) {
                    value = Laterality.getHashCode(prd);
                }
                if (map == value || this.validateMap(value, map) || type.equals("AddOn")) {
                    final String returnKey = Encode.getKey(userName, company, mailID, macID, expiryDate, evalDays, type, null, null, null, null);
                    if (key.equals(returnKey)) {
                        final Vendee client = Vendee.getInstance();
                        client.setMode(mode);
                        String machineMac = null;
                        if (isNative || isTrialMacBased) {
                            if (user.getMacId() == null || user.getMacId().equalsIgnoreCase("NO")) {
                                machineMac = "NO";
                            }
                            else {
                                machineMac = client.getTheMacID(user.getMacId());
                            }
                        }
                        final String regProduct = new Integer(map).toString();
                        final ROperation operation = ROperation.getInstance();
                        final String regisKey = operation.getRegValues(regProduct, productVersion);
                        final int index = regisKey.lastIndexOf("-");
                        String registryKey;
                        if (index != -1) {
                            registryKey = regisKey.substring(0, index);
                        }
                        else {
                            registryKey = regisKey;
                        }
                        final Calendar cal = Calendar.getInstance();
                        final Date currentDate = LUtil.getCurrentDate(cal);
                        String registryDate = "";
                        if (index != -1) {
                            registryDate = regisKey.substring(index + 1);
                        }
                        Date regTmpDate = null;
                        if (!registryDate.equals("")) {
                            regTmpDate = LUtil.getTheDate(registryDate, true);
                        }
                        int check = -1;
                        if (regTmpDate != null) {
                            check = client.compareTo(regTmpDate, currentDate);
                        }
                        if (type.equals("Free")) {
                            this.setUserType("F");
                            this.setUserName("Evaluation User");
                            final boolean cust = client.first(userName, company, key, "0", expiryDate, "F", wholeKey);
                            return cust && (this.rtuser = true);
                        }
                        if (type.equals("AddOn")) {
                            this.copyLicense = false;
                            String licenseFileCheck = lFilePath + File.separator + LUtil.getLicenseDir() + File.separator + "AdventNetLicense.xml";
                            if (!new File(licenseFileCheck).exists()) {
                                licenseFileCheck = lFilePath + File.separator + LUtil.getLicenseDir() + File.separator + "StandardEvaluation.xml";
                            }
                            final Validity val = new Validity(licenseFileCheck);
                            final boolean bool = val.process();
                            if (bool) {
                                final AddOn add = AddOn.getInstance();
                                add.updateXML(this.components, lFilePath, mapID);
                                this.validate(lFilePath, mode);
                                return true;
                            }
                            this.showError("ERROR CODE : 547", mode, 547);
                            return false;
                        }
                        else if (type.equals("Evaluation")) {
                            if (Indication.getInstance().getFirstTimeUser()) {
                                if (!Wield.getInstance().getUserType().equals("T") || Wield.getInstance().getEvaluationDays() <= 0L) {
                                    final String ls = ToolsUtils.getString("Invalid license");
                                    if (mode) {
                                        LUtil.showError("", ls, this.contactMesg, "Information", -5);
                                    }
                                    else {
                                        LUtil.showCMDError("", ls, this.contactMesg, -6);
                                    }
                                    return false;
                                }
                                evalDays = Long.toString(Wield.getInstance().getEvaluationDays() + 1L);
                            }
                            boolean oneTime = false;
                            final Indication util = Indication.getInstance();
                            util.deSerialize();
                            oneTime = util.oneTimeStandardEval();
                            this.setUserType("T");
                            final ArrayList macArray = this.getTheMacIDList(macID);
                            if (!macArray.contains(machineMac) && machineMac != null) {
                                this.showError("ERROR CODE : 546", mode, 546);
                                return false;
                            }
                            if (registryKey.equals(key) && (check > 0 || oneTime)) {
                                this.showError("ERROR CODE : 504", mode, 504);
                                return false;
                            }
                            final boolean cust2 = client.first(userName, company, key, evalDays, expiryDate, "T", wholeKey);
                            if (cust2) {
                                final String lastAccessDate = client.getTheLastAccessedDate();
                                final String lastDate = "-" + lastAccessDate.replace(' ', ':');
                                final boolean bb = operation.writeRegValue(regProduct, productVersion, key + lastDate);
                                this.setExpiryDate(client.getEvaluationExpiryDate());
                                return this.rtuser = true;
                            }
                            return false;
                        }
                        else if (type.equals("Registered")) {
                            this.setUserType("R");
                            if (mandatoryMacBased && (macID == null || macID.equals("NO"))) {
                                this.showError("ERROR CODE : 1000", mode, 1000);
                                return false;
                            }
                            final ArrayList macArray2 = this.getTheMacIDList(macID);
                            if (!macArray2.contains(machineMac) && machineMac != null) {
                                this.showError("ERROR CODE : 503", mode, 503);
                                return false;
                            }
                            if (evalDays == null && (expiryDate == null || expiryDate.equals("never"))) {
                                client.getRegisterState(userName, company, key, wholeKey);
                                return this.rtuser = true;
                            }
                            if (registryKey.equals(key) && check > 0) {
                                return false;
                            }
                            final boolean cust3 = client.first(userName, company, key, evalDays, expiryDate, "R", wholeKey);
                            if (cust3) {
                                this.setExpiryDate(client.getEvaluationExpiryDate());
                                final String lastAccessDate2 = client.getTheLastAccessedDate();
                                final String lastDate2 = "-" + lastAccessDate2.replace(' ', ':');
                                operation.writeRegValue(regProduct, productVersion, key + lastDate2);
                                return this.rtuser = true;
                            }
                            return false;
                        }
                    }
                }
            }
        }
        catch (final Exception exp2) {
            exp2.printStackTrace();
            this.showError("ERROR CODE : 501", mode, 501);
            return false;
        }
        this.showError("ERROR CODE : 500", mode, 500);
        return false;
    }
    
    public boolean doOldValidation(final String userName, final String companyName, final String email, final String userType, final String key, final boolean display) {
        this.LICENSETYPE = 1;
        this.setUserName(userName);
        this.setCompanyName(companyName);
        this.setUserType(userType);
        final Formalize form = Formalize.getInstance();
        if (userType.equals("R")) {
            this.rtuser = form.doFormalize(userName, companyName, email, userType, key, display);
        }
        if (key.length() == 24) {
            this.setType(form.getType(key));
        }
        else if (key.length() == 20) {
            this.setType(form.getOldLType());
        }
        this.setProductName(form.getProductName());
        this.setProductVersion(form.getProductVersion());
        if (this.rtuser) {
            final File licenseFile = new File(LUtil.getDir() + File.separator + LUtil.getLicenseDir() + File.separator + "AdventNetLicense.xml");
            if (licenseFile.exists()) {
                licenseFile.delete();
            }
        }
        return this.rtuser;
    }
    
    private void setComponents(final ArrayList vector) {
        this.components = vector;
    }
    
    private void setType(final int i) {
        this.type = i;
    }
    
    private void setType(final String i) {
        this.newtype = i;
    }
    
    private void setCategory(final String cat) {
        this.category = cat;
    }
    
    private void setUserName(final String user) {
        this.userName = user;
    }
    
    public String getCompanyName() {
        if (this.LICENSETYPE == 0) {
            try {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                return this.companyName = util.getTheCompanyName();
            }
            catch (final Exception ex) {
                return null;
            }
        }
        if (this.LICENSETYPE == 1) {
            return this.companyName;
        }
        return null;
    }
    
    public String getUserName() {
        if (this.LICENSETYPE == 0) {
            try {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                return this.userName = util.getTheUserName();
            }
            catch (final Exception ex) {
                return null;
            }
        }
        if (this.LICENSETYPE == 1) {
            return this.userName;
        }
        return null;
    }
    
    public String getEvaluationExpiryDate() {
        if (this.LICENSETYPE != 0) {
            if (this.LICENSETYPE != 1) {
                return null;
            }
        }
        try {
            final Indication util = Indication.getInstance();
            util.deSerialize();
            return util.getEvalExpiryDate();
        }
        catch (final Exception ex) {
            return null;
        }
        return null;
    }
    
    private void setCompanyName(final String name) {
        this.companyName = name;
    }
    
    private void setProductName(final String name) {
        this.productName = name;
    }
    
    public String getProductName() {
        if (this.LICENSETYPE == 0) {
            return this.productName;
        }
        if (this.LICENSETYPE == 1) {
            return this.productName;
        }
        return null;
    }
    
    private void setProductVersion(final String name) {
        this.productVersion = name;
    }
    
    public long getEvaluationDays() {
        if (this.expiryDate != null) {
            return this.getDays(this.expiryDate);
        }
        return 0L;
    }
    
    public String getProductVersion() {
        if (this.LICENSETYPE == 0) {
            try {
                final Indication util = Indication.getInstance();
                util.deSerialize();
                return this.productVersion = util.getProductVersion();
            }
            catch (final Exception ex) {
                return null;
            }
        }
        if (this.LICENSETYPE == 1) {
            return this.productVersion;
        }
        return null;
    }
    
    public int getType() {
        if (this.LICENSETYPE == 0) {
            if (this.newtype == null) {
                return -1;
            }
            return LUtil.getNewType(this.newtype);
        }
        else {
            if (this.LICENSETYPE != 1) {
                return -1;
            }
            try {
                final Formalize form = Formalize.getInstance();
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final String key = util.getTheKey();
                if (key != null && key.length() == 24) {
                    this.type = form.getType(key);
                }
                else if (key != null && key.length() == 20) {
                    this.type = form.getOldLType();
                }
                this.productVersion = form.getProductVersion();
            }
            catch (final Exception ex) {}
            final Product suppProd = this.getSupportedProduct(this.productVersion, LUtil.getMapping(this.type));
            if (suppProd == null) {
                return -1;
            }
            final String stype = new String(Encode.revShiftBytes(suppProd.getProductLicenseType()));
            return LUtil.getNewType(stype);
        }
    }
    
    public String getTypeString() {
        if (this.LICENSETYPE == 0) {
            if (this.newtype == null) {
                return null;
            }
            return this.newtype;
        }
        else {
            if (this.LICENSETYPE != 1) {
                return null;
            }
            try {
                final Formalize form = Formalize.getInstance();
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final String key = util.getTheKey();
                if (key != null && key.length() == 24) {
                    this.type = form.getType(key);
                }
                else if (key != null && key.length() == 20) {
                    this.type = form.getOldLType();
                }
                this.productVersion = form.getProductVersion();
            }
            catch (final Exception ex) {}
            final Product suppProd = this.getSupportedProduct(this.productVersion, LUtil.getMapping(this.type));
            if (suppProd == null) {
                return null;
            }
            final String stype = new String(Encode.revShiftBytes(suppProd.getProductLicenseType()));
            return stype;
        }
    }
    
    private Product getSupportedProduct(final String version, final String type) {
        BObject bobject = null;
        try {
            final Indication indication = Indication.getInstance();
            indication.productNameDeSerialize();
            bobject = indication.getBCObject();
            return bobject.getSupportedProduct(version, type);
        }
        catch (final Exception exp) {
            exp.printStackTrace();
            return null;
        }
    }
    
    public int getProductCategory() {
        if (this.LICENSETYPE == 0) {
            if (this.category == null) {
                return -1;
            }
            return LUtil.getNewCategory(this.category);
        }
        else {
            if (this.LICENSETYPE != 1) {
                return -1;
            }
            try {
                final Formalize form = Formalize.getInstance();
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final String key = util.getTheKey();
                if (key != null && key.length() == 24) {
                    this.type = form.getType(key);
                }
                else if (key != null && key.length() == 20) {
                    this.type = form.getOldLType();
                }
                this.productVersion = form.getProductVersion();
            }
            catch (final Exception ex) {}
            final Product suppProd = this.getSupportedProduct(this.productVersion, LUtil.getMapping(this.type));
            if (suppProd == null) {
                return -1;
            }
            final String stype = new String(Encode.revShiftBytes(suppProd.getProductCategory()));
            return LUtil.getNewCategory(stype);
        }
    }
    
    public String getProductCategoryString() {
        if (this.LICENSETYPE == 0) {
            if (this.category == null) {
                return null;
            }
            return this.category;
        }
        else {
            if (this.LICENSETYPE != 1) {
                return null;
            }
            try {
                final Formalize form = Formalize.getInstance();
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final String key = util.getTheKey();
                if (key != null && key.length() == 24) {
                    this.type = form.getType(key);
                }
                else if (key != null && key.length() == 20) {
                    this.type = form.getOldLType();
                }
                this.productVersion = form.getProductVersion();
            }
            catch (final Exception ex) {}
            final Product suppProd = this.getSupportedProduct(this.productVersion, LUtil.getMapping(this.type));
            if (suppProd == null) {
                return null;
            }
            final String stype = new String(Encode.revShiftBytes(suppProd.getProductCategory()));
            return stype;
        }
    }
    
    public boolean copyLicenseFile(final String filePath, final String file) {
        try {
            if (!this.copyLicense) {
                return true;
            }
            String licenseFile = null;
            final File lfile = new File(file);
            if (!lfile.isAbsolute()) {
                licenseFile = lfile.getAbsolutePath();
            }
            else {
                licenseFile = file;
            }
            if (lfile.exists()) {
                final FileInputStream input = new FileInputStream(licenseFile);
                String dest = null;
                if (LUtil.getLicenseDir().equals(".")) {
                    dest = filePath + File.separator + "AdventNetLicense.xml";
                }
                else {
                    dest = filePath + File.separator + LUtil.getLicenseDir() + File.separator + "AdventNetLicense.xml";
                }
                final File ff = new File(dest);
                this.createParentDirsIfNeeded(ff);
                if (new File(licenseFile).compareTo(ff) != 0) {
                    this.writeFile(input, dest);
                }
                return true;
            }
        }
        catch (final Exception exp) {
            exp.printStackTrace();
        }
        return false;
    }
    
    private boolean createParentDirsIfNeeded(final File fileArg) {
        final File parentFile = fileArg.getParentFile();
        return parentFile.exists() || parentFile.mkdirs();
    }
    
    private void writeFile(final InputStream unzipper, final String path) {
        try {
            final int BUFFER = 10240;
            final byte[] data = new byte[10240];
            final FileOutputStream out = new FileOutputStream(path);
            final BufferedInputStream origin = new BufferedInputStream(unzipper, 10240);
            int count;
            while ((count = origin.read(data, 0, 10240)) != -1) {
                out.write(data, 0, count);
            }
            out.flush();
            out.close();
            origin.close();
        }
        catch (final Exception ex) {
            ex.printStackTrace();
        }
    }
    
    public boolean isVRTUser() {
        return this.rtuser;
    }
    
    public String getUserInfoJson() {
        final StringBuffer retBuff = new StringBuffer();
        retBuff.append("{\"company\":\"" + this.getCompanyName() + "\",");
        retBuff.append("\"expirydays\":\"" + this.getEvaluationDays() + "\",");
        retBuff.append("\"expirydate\":\"" + this.getEvaluationExpiryDate() + "\",");
        retBuff.append("\"edition_type\":\"" + this.getTypeString() + "\",");
        retBuff.append("\"edition_category\":\"" + this.getProductCategoryString() + "\",");
        retBuff.append("\"product\":\"" + this.getProductName() + "\",");
        retBuff.append("\"version\":\"" + this.getProductVersion() + "\",");
        retBuff.append("\"name\":\"" + this.getUserName() + "\",");
        retBuff.append("\"usertype\":\"" + this.getUserType() + "\"}");
        return retBuff.toString();
    }
    
    public String getAllModulesAsJson(final boolean includeOnlyLicenseModules) {
        StringBuffer retBuff = null;
        if (this.components != null) {
            retBuff = new StringBuffer();
            for (final Object compObj : this.components) {
                final Component comp = (Component)compObj;
                if (!includeOnlyLicenseModules || !comp.getName().equals("AMS") || !comp.getName().equals("LicenseDetails")) {
                    final StringBuffer moduleBuffer = new StringBuffer();
                    final ArrayList list = comp.getProperties();
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i += 2) {
                            moduleBuffer.append("\"" + list.get(i) + "\":\"" + list.get(i + 1) + "\",");
                        }
                    }
                    else {
                        moduleBuffer.append("\"" + comp.getName() + "\":\"" + comp.getName() + "\",");
                    }
                    retBuff.append("\"" + comp.getName() + "\":{" + moduleBuffer.toString().substring(0, moduleBuffer.length() - 1) + "},");
                }
            }
        }
        return "{" + retBuff.toString().substring(0, retBuff.length() - 1) + "}";
    }
    
    public ArrayList<HashMap<String, Properties>> getAllModuleProperties(final boolean includeOnlyLicenseModules) {
        ArrayList<HashMap<String, Properties>> allModules = null;
        if (this.components != null) {
            allModules = new ArrayList<HashMap<String, Properties>>();
            for (final Object compObj : this.components) {
                final Component comp = (Component)compObj;
                System.out.println("comp ---- " + comp.getName());
                if (!includeOnlyLicenseModules || !comp.getName().equals("AMS") || !comp.getName().equals("LicenseDetails")) {
                    final HashMap<String, Properties> map = new HashMap<String, Properties>();
                    final Properties prop = new Properties();
                    final ArrayList list = comp.getProperties();
                    if (list.size() > 0) {
                        for (int i = 0; i < list.size(); i += 2) {
                            prop.put(list.get(i), list.get(i + 1));
                        }
                    }
                    else {
                        ((Hashtable<String, String>)prop).put(comp.getName(), comp.getName());
                    }
                    map.put(comp.getName(), prop);
                    allModules.add(map);
                }
            }
        }
        return allModules;
    }
    
    public Properties getModuleProperties(final String moduleName) {
        if (this.LICENSETYPE == 0) {
            if (this.components != null) {
                for (int size = this.components.size(), i = 0; i < size; ++i) {
                    final Component comp = this.components.get(i);
                    final String name = comp.getName();
                    if (name.equals(moduleName)) {
                        final Properties prop = new Properties();
                        final ArrayList list = comp.getProperties();
                        for (int count = list.size(), j = 0; j < count; j += 2) {
                            final Object key = list.get(j);
                            final Object value = list.get(j + 1);
                            prop.put(key, value);
                        }
                        return prop;
                    }
                }
                return null;
            }
            return null;
        }
        else {
            if (this.LICENSETYPE != 1) {
                return null;
            }
            try {
                final Formalize form = Formalize.getInstance();
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final String key2 = util.getTheKey();
                if (key2 != null && key2.length() == 24) {
                    this.type = form.getType(key2);
                }
                else if (key2 != null && key2.length() == 20) {
                    this.type = form.getOldLType();
                }
                this.productVersion = form.getProductVersion();
            }
            catch (final Exception ex) {}
            final Product suppProd = this.getSupportedProduct(this.productVersion, LUtil.getMapping(this.type));
            if (suppProd == null) {
                return null;
            }
            final ArrayList components = suppProd.getComponents();
            if (components != null) {
                for (int size2 = components.size(), k = 0; k < size2; ++k) {
                    final Component comp2 = components.get(k);
                    final String name2 = comp2.getName();
                    if (name2.equals(Encode.swap(moduleName))) {
                        final Properties prop2 = new Properties();
                        final ArrayList list2 = comp2.getProperties();
                        for (int count2 = list2.size(), l = 0; l < count2; l += 2) {
                            final Object key3 = list2.get(l);
                            final Object value2 = list2.get(l + 1);
                            prop2.put(key3, value2);
                        }
                        return prop2;
                    }
                }
                return null;
            }
            return null;
        }
    }
    
    public ArrayList getAllModules() {
        if (this.LICENSETYPE == 0) {
            final ArrayList compList = new ArrayList();
            for (int size = this.components.size(), i = 0; i < size; ++i) {
                final Component comp = this.components.get(i);
                compList.add(comp.getName());
            }
            return compList;
        }
        return null;
    }
    
    public boolean isModulePresent(final String moduleName) {
        if (this.LICENSETYPE == 0) {
            if (this.components != null) {
                for (int size = this.components.size(), i = 0; i < size; ++i) {
                    final Component comp = this.components.get(i);
                    final String name = comp.getName();
                    if (name.equals(moduleName)) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
        else {
            if (this.LICENSETYPE != 1) {
                return false;
            }
            try {
                final Formalize form = Formalize.getInstance();
                final Indication util = Indication.getInstance();
                util.deSerialize();
                final String key = util.getTheKey();
                if (key != null && key.length() == 24) {
                    this.type = form.getType(key);
                }
                else if (key != null && key.length() == 20) {
                    this.type = form.getOldLType();
                }
                this.productVersion = form.getProductVersion();
            }
            catch (final Exception ex) {}
            final Product suppProd = this.getSupportedProduct(this.productVersion, LUtil.getMapping(this.type));
            if (suppProd == null) {
                return false;
            }
            final ArrayList components = suppProd.getComponents();
            if (components != null) {
                for (int size2 = components.size(), j = 0; j < size2; ++j) {
                    final Component comp2 = components.get(j);
                    final String name2 = comp2.getName();
                    if (name2.equals(Encode.swap(moduleName))) {
                        return true;
                    }
                }
                return false;
            }
            return false;
        }
    }
    
    private boolean validateFile(final String licenseFile, final boolean mode) {
        this.LICENSETYPE = 0;
        Indication indicate = null;
        try {
            indicate = Indication.getInstance();
            indicate.deSerialize();
            indicate.productNameDeSerialize();
        }
        catch (final Exception exp) {
            this.showError("ERROR CODE : 499", mode, 499);
            return false;
        }
        final int map = indicate.getProductName();
        final int prod = indicate.getProductNameInt();
        final boolean isNative = indicate.isNativeFilesToBeBundled();
        final boolean isTrialMacBased = indicate.isTrialMacBased();
        final boolean mandatoryMacBased = indicate.mandatoryMacLicense();
        final String userName = indicate.getTheUserName();
        final String regCheck = indicate.getTheRegCheck();
        final String lastAccessedString = indicate.getLastAccessedDate();
        final String fileKey = indicate.getFileKey();
        if (map != prod && !this.validateMap(prod, map)) {
            this.showError("ERROR CODE : 498", mode, 498);
            return false;
        }
        InputFileParser parser = null;
        try {
            parser = new InputFileParser(licenseFile);
        }
        catch (final Exception exp2) {
            this.showError("ERROR CODE : 497", mode, 497);
            return false;
        }
        final DataClass data = parser.getDataClass();
        final ArrayList users = data.getUsers();
        final ArrayList licensee = data.getDetails();
        final User user = data.getUserObject(userName);
        final String wholeKey = data.getLicenseFileKey();
        final String encodedKey = Encode.getFinalKey(data.getWholeKeyBuffer());
        if (!encodedKey.equals(wholeKey)) {
            this.setInvalidFileError();
            this.showError("ERROR CODE : 488", mode, 488);
            return false;
        }
        if (user == null) {
            this.showError("ERROR CODE : 496", mode, 496);
            return false;
        }
        this.setUserName(userName);
        final String type = user.getLicenseType();
        final String company = user.getCompanyName();
        this.setCompanyName(company);
        final String mailID = user.getMailId();
        final String evalDays = user.getNumberOfDays();
        final String macID = user.getMacId();
        final String expiryDate = user.getExpiryDate();
        final String key = user.getKey();
        final ArrayList ID = user.getIDs();
        try {
            final String productVersion = indicate.getProductVersion();
            this.setProductVersion(productVersion);
            for (int idSize = ID.size(), j = 0; j < idSize; ++j) {
                final String mapID = ID.get(j);
                final Details details = data.getDetails(mapID);
                if (details == null) {
                    this.showError("ERROR CODE : 495", mode, 495);
                    return false;
                }
                final String prd = details.getProductName();
                final String ver = details.getProductVersion();
                final String prdLType = details.getProductLicenseType();
                final String category = details.getProductCategory();
                this.setProductName(prd);
                this.setComponents(details.getComponents());
                this.setType(prdLType);
                this.setCategory(category);
                if (prd == null || ver == null) {
                    this.showError("ERROR CODE : 494", mode, 494);
                    return false;
                }
                final int iamValidation = this.meIamValidation(details.isComponentPresent("meiam"));
                if (iamValidation > 0) {
                    this.showError("ERROR CODE : " + iamValidation, mode, iamValidation);
                    return false;
                }
                int value = Laterality.getMapValue(prd);
                if (value == -1) {
                    value = Laterality.getHashCode(prd);
                }
                if (map == value || this.validateMap(value, map)) {
                    final String returnKey = Encode.getKey(userName, company, mailID, macID, expiryDate, evalDays, type, null, null, null, null);
                    if (key.equals(returnKey)) {
                        final Vendee client = Vendee.getInstance();
                        client.setMode(mode);
                        String machineMac = null;
                        if (isNative || isTrialMacBased) {
                            if (user.getMacId() == null || user.getMacId().equalsIgnoreCase("NO")) {
                                machineMac = "NO";
                            }
                            else {
                                machineMac = client.getTheMacID(user.getMacId());
                            }
                        }
                        final String regProduct = new Integer(map).toString();
                        final ROperation operation = ROperation.getInstance();
                        final String regisKey = operation.getRegValues(regProduct, productVersion);
                        final int index = regisKey.lastIndexOf("-");
                        if (index != -1) {
                            final String registryKey = regisKey.substring(0, index);
                        }
                        else {
                            final String registryKey = regisKey;
                        }
                        String dateKey = "";
                        if (regCheck.equals("T")) {
                            dateKey = "-" + lastAccessedString.replace(' ', ':');
                            boolean registryboolean = false;
                            registryboolean = operation.writeRegValue(regProduct, productVersion, key + dateKey);
                        }
                        if (type.equals("Free")) {
                            this.setUserType("F");
                            return this.rtuser = true;
                        }
                        if (type.equals("Evaluation")) {
                            this.setUserType("T");
                            final ArrayList macArray = this.getTheMacIDList(macID);
                            if (!macArray.contains(machineMac) && macID != null && machineMac != null) {
                                this.showError("ERROR CODE : 546", mode, 546);
                                return false;
                            }
                            final boolean cust = client.getClienteleState();
                            this.register = client.getRegister();
                            if (cust) {
                                this.setExpiryDate(client.getEvaluationExpiryDate());
                                return this.rtuser = true;
                            }
                            return false;
                        }
                        else if (type.equals("Registered")) {
                            this.setUserType("R");
                            final ArrayList macArray = this.getTheMacIDList(macID);
                            if (mandatoryMacBased && (macID == null || macID.equals("NO"))) {
                                this.showError("ERROR CODE : 1000", mode, 1000);
                                return false;
                            }
                            if (!macArray.contains(machineMac) && machineMac != null) {
                                this.showError("ERROR CODE : 492", mode, 492);
                                return false;
                            }
                            if (evalDays == null && (expiryDate == null || expiryDate.equals("never"))) {
                                client.getRegisterState(userName, company, key, fileKey);
                                return this.rtuser = true;
                            }
                            final boolean cust = client.getClienteleState();
                            this.register = client.getRegister();
                            if (cust) {
                                this.setExpiryDate(client.getEvaluationExpiryDate());
                                return this.rtuser = true;
                            }
                            return false;
                        }
                    }
                }
            }
        }
        catch (final Exception exp3) {
            exp3.printStackTrace();
            this.showError("ERROR CODE : 490", mode, 490);
            return false;
        }
        this.showError("ERROR CODE : 489", mode, 489);
        return false;
    }
    
    private void setExpiryDate(final Date date) {
        this.expiryDate = date;
    }
    
    private long getDays(final Date d) {
        final long expirytimeinmillis = d.getTime();
        final Date currentdate = new Date();
        final long currenttimeinmillis = currentdate.getTime();
        final long difference = expirytimeinmillis - currenttimeinmillis;
        final long daysLeft = difference / 86400000L;
        return daysLeft;
    }
    
    public boolean getRegister() {
        return this.register;
    }
    
    public void setUserType(final String type) {
        this.userType = type;
    }
    
    public String getUserType() {
        return this.userType;
    }
    
    private void showError(final String code, final boolean mode, final int codeInt) {
        if (this.fnf) {
            this.invalidFile = "The license file is not present in the specified location.";
        }
        if (this.invalid) {
            this.invalidFile = "The license file specified is not valid.";
        }
        if (this.invalidUser) {
            this.invalidFile = "The User Name entered is not present in the license file.";
        }
        if (mode) {
            LUtil.showError(code, this.invalidFile, this.contactMesg, "Error", codeInt);
        }
        else {
            LUtil.showCMDError(code, this.invalidFile, this.contactMesg, codeInt);
        }
    }
    
    private boolean setFNFError() {
        return this.fnf = true;
    }
    
    private boolean setInvalidFileError() {
        return this.invalid = true;
    }
    
    private boolean setInvalidUserError() {
        return this.invalidUser = true;
    }
    
    public boolean isRegisteredFile(final String filePath, final String userName, final boolean mode) {
        String licenseFile = null;
        final File file = new File(filePath);
        if (!file.isAbsolute()) {
            licenseFile = file.getAbsolutePath();
        }
        else {
            licenseFile = filePath;
        }
        if (!file.exists()) {
            this.showError("ERROR CODE : 474", mode, 474);
            return false;
        }
        InputFileParser parser = null;
        try {
            parser = new InputFileParser(licenseFile);
        }
        catch (final Exception exp) {
            this.showError("ERROR CODE : 473", mode, 473);
            return false;
        }
        final DataClass data = parser.getDataClass();
        final ArrayList users = data.getUsers();
        final User user = data.getUserObject(userName);
        if (user == null) {
            this.showError("ERROR CODE : 472", mode, 472);
            return false;
        }
        final String type = user.getLicenseType();
        return true;
    }
    
    private ArrayList getTheMacIDList(final String id) {
        final ArrayList macArray = new ArrayList(2);
        macArray.add("NO");
        final String macAdd = id;
        if (macAdd == null) {
            return macArray;
        }
        if (!macAdd.equals("NULL") || !macAdd.equals("null")) {
            macArray.add(macAdd);
            macArray.add("aldqQQNd");
            macArray.add("aldqQRGq");
            macArray.add("aldqQRCb");
            macArray.add("aldqQJSw");
            macArray.add("aadEXLmk");
            macArray.add("aacQJRDc");
            macArray.add("afc/lrRl");
            macArray.add("caa6VZN4");
            macArray.add("aaa7/AQZ");
            macArray.add("afc/lqIr");
        }
        return macArray;
    }
    
    private File getXFile(final String homeDir, final String fileName) {
        File lFile = new File(homeDir + File.separator + LUtil.getLicenseDir() + File.separator + fileName);
        if (!lFile.exists()) {
            final URL fileUrl = this.getClass().getResource("/" + fileName);
            try {
                final String fName = LUtil.getFileName(fileUrl);
                lFile = new File(fName);
            }
            catch (final Exception ex) {}
        }
        return lFile;
    }
    
    private int meIamValidation(final boolean iamPresent) {
        if (iamPresent && System.getProperty("product.home") == null) {
            return 1004;
        }
        if (iamPresent && !new File(System.getProperty("product.home") + File.separator + "IT360Configured.lock").exists()) {
            return 1005;
        }
        if (!iamPresent && new File(System.getProperty("product.home") + File.separator + "IT360Configured.lock").exists()) {
            return 1006;
        }
        return 0;
    }
    
    static {
        Validation.valid = null;
    }
}
