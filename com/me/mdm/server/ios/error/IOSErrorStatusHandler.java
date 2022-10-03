package com.me.mdm.server.ios.error;

import java.util.Arrays;
import com.dd.plist.NSNumber;
import com.dd.plist.NSString;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.dd.plist.NSArray;
import java.util.logging.Level;
import com.dd.plist.NSDictionary;
import com.adventnet.sym.server.mdm.PlistWrapper;
import org.json.JSONObject;
import java.util.List;
import java.util.HashMap;
import java.util.logging.Logger;

public class IOSErrorStatusHandler
{
    static IOSErrorStatusHandler iosErrorStatus;
    private static Logger logger;
    public static final String IOS_MC_MDM_ERROR_DOMAIN = "MCMDMErrorDomain";
    private static final String IOS_SCEP_ERROR_DOMAIN = "MCSCEPErrorDomain";
    private static final String IOS_OS_STATUS_ERROR_DOMAIN = "NSOSStatusErrorDomain";
    private static final String MAC_AD_BIND_ERROR_DOMAIN = "";
    private static final String IOS_PROFILE_INSTALLATION_ERROR_DOMAIN = "MCInstallationErrorDomain";
    private static final HashMap<String, Integer> PRODUCT_ERROR_CODE;
    private static final HashMap<Integer, String> PRODUCT_ERROR_REMARKS;
    private static final HashMap<String, Integer> PRODUCT_ERROR_DOMAIN_CODE;
    private static final List<String> ERROR_DOMAIN_LIST;
    public static final List<Integer> IOS_PROFILE_ERROR_CODE_KB_LIST;
    
    public JSONObject getIOSSettingError(final String responseData) {
        JSONObject errorMsg = new JSONObject();
        try {
            final NSArray nsarr = PlistWrapper.getInstance().getArrayForKey("Settings", responseData);
            JSONObject commandFormatMsg = new JSONObject();
            for (int i = 0; i < nsarr.count(); ++i) {
                final JSONObject eachSetting = new JSONObject();
                final NSDictionary nsdict = (NSDictionary)nsarr.objectAtIndex(i);
                for (final String allKey : nsdict.allKeys()) {
                    final String value = nsdict.objectForKey(allKey).toString();
                    eachSetting.put(allKey, (Object)value);
                }
                final String strStatus = eachSetting.optString("Status");
                if (strStatus.equalsIgnoreCase("Error")) {
                    errorMsg = this.getIOSErrors(strStatus, nsdict.toXMLPropertyList(), strStatus);
                    errorMsg.put("Status", (Object)strStatus);
                    errorMsg.put("Item", (Object)eachSetting.optString("Item"));
                    break;
                }
                if (strStatus.equalsIgnoreCase("CommandFormatError")) {
                    commandFormatMsg = this.getIOSErrors(strStatus, nsdict.toXMLPropertyList(), strStatus);
                    commandFormatMsg.put("Status", (Object)strStatus);
                    commandFormatMsg.put("Item", (Object)eachSetting.optString("Item"));
                }
            }
            if (errorMsg.length() == 0 && commandFormatMsg.length() != 0) {
                errorMsg = commandFormatMsg;
            }
            if (errorMsg.length() == 0) {
                errorMsg.put("Status", (Object)"Acknowledged");
            }
        }
        catch (final Exception ex) {
            IOSErrorStatusHandler.logger.log(Level.SEVERE, null, ex);
        }
        return errorMsg;
    }
    
    public JSONObject getIOSErrors(final List<String> preferredDomain, final List<Long> preferredErrorCode, final String strData) {
        try {
            final NSArray nsArr = PlistWrapper.getInstance().getArrayForKey("ErrorChain", strData);
            final int pointerToErr = this.getPointerToError(preferredDomain, preferredErrorCode, nsArr);
            return this.getIOSErrorJson((NSDictionary)nsArr.objectAtIndex(pointerToErr));
        }
        catch (final Exception ex) {
            IOSErrorStatusHandler.logger.log(Level.WARNING, "Exception occurred in getIOSErrors()", ex);
            return null;
        }
    }
    
    private JSONObject getIOSErrorJson(final NSDictionary errorChainDict) {
        final String remarks = null;
        String strErrorCode = null;
        String strErrorDomain = null;
        String strLocalizedDescription = null;
        String strUSEnglishDescription = null;
        JSONObject iOSErrorDetails = new JSONObject();
        try {
            final JSONObject errorResponse = new JSONObject();
            final HashMap errorHash = PlistWrapper.getInstance().getHashFromDict(errorChainDict);
            strErrorCode = errorHash.get("ErrorCode");
            strErrorDomain = errorHash.get("ErrorDomain");
            strLocalizedDescription = errorHash.get("LocalizedDescription");
            strUSEnglishDescription = errorHash.get("USEnglishDescription");
            if (strUSEnglishDescription == null) {
                strUSEnglishDescription = strLocalizedDescription;
            }
            errorResponse.put("ErrorCode", (Object)strErrorCode);
            errorResponse.put("ErrorDomain", (Object)strErrorDomain);
            errorResponse.put("LocalizedRemarks", (Object)strLocalizedDescription);
            errorResponse.put("EnglishRemarks", (Object)strUSEnglishDescription);
            iOSErrorDetails = this.getProductErrorCodeAndRemark(errorResponse);
            if (iOSErrorDetails == null) {
                return errorResponse;
            }
        }
        catch (final Exception ex) {
            IOSErrorStatusHandler.logger.log(Level.SEVERE, "Exception in getIOSErrorJson", ex);
        }
        return iOSErrorDetails;
    }
    
    public JSONObject getIOSErrors(final String strUDID, final String strData, final String strStatus) {
        String remarks = null;
        JSONObject iOSErrorDetails = new JSONObject();
        try {
            if (strStatus.equals("Error")) {
                final NSArray nsArr = PlistWrapper.getInstance().getArrayForKey("ErrorChain", strData);
                final NSDictionary errorChainDict = (NSDictionary)nsArr.lastObject();
                iOSErrorDetails = this.getIOSErrorJson(errorChainDict);
                final Integer errorCode = this.getErrorcodeForDomain(nsArr);
                if (errorCode != null) {
                    iOSErrorDetails = this.getIOSErrorDetailsFromProductErrorCode(errorCode, iOSErrorDetails);
                }
            }
            else if (strStatus.equals("CommandFormatError")) {
                remarks = "Given command format is wrong";
                iOSErrorDetails.put("EnglishRemarks", (Object)remarks);
                iOSErrorDetails.put("ErrorCode", 9000);
                iOSErrorDetails.put("LocalizedRemarks", (Object)"dc.db.mdm.scanStaus.command_format_wrong");
            }
        }
        catch (final Exception ex) {
            IOSErrorStatusHandler.logger.log(Level.SEVERE, "Exception in getIOSErrors", ex);
        }
        return iOSErrorDetails;
    }
    
    private JSONObject getProductErrorCodeAndRemark(final JSONObject errorResponse) {
        JSONObject productErrorRemark = new JSONObject();
        try {
            productErrorRemark = this.initErrorCodeAndRemark(errorResponse);
        }
        catch (final Exception ex) {
            IOSErrorStatusHandler.logger.log(Level.SEVERE, null, ex);
        }
        return productErrorRemark;
    }
    
    private JSONObject getIOSErrorDetailsFromProductErrorCode(final Integer productErrorCode, final JSONObject errorResponse) {
        try {
            if (productErrorCode != null) {
                String remark = IOSErrorStatusHandler.PRODUCT_ERROR_REMARKS.get(productErrorCode);
                remark = this.getErrorRemark(productErrorCode, remark, errorResponse);
                errorResponse.put("ErrorCode", (Object)productErrorCode.toString());
                if (!MDMStringUtils.isEmpty(remark)) {
                    errorResponse.put("LocalizedRemarks", (Object)remark);
                    errorResponse.put("EnglishRemarks", (Object)remark);
                }
            }
        }
        catch (final Exception ex) {
            IOSErrorStatusHandler.logger.log(Level.SEVERE, null, ex);
        }
        return errorResponse;
    }
    
    private JSONObject initErrorCodeAndRemark(JSONObject errorResponse) {
        try {
            final String errorCode = errorResponse.optString("ErrorCode");
            final Integer productErrorCode = IOSErrorStatusHandler.PRODUCT_ERROR_CODE.get(errorCode);
            errorResponse = this.getIOSErrorDetailsFromProductErrorCode(productErrorCode, errorResponse);
        }
        catch (final Exception ex) {
            IOSErrorStatusHandler.logger.log(Level.SEVERE, null, ex);
        }
        return errorResponse;
    }
    
    private String getErrorRemark(final Integer errorCode, String remark, final JSONObject errorResponse) {
        switch (errorCode) {
            case 21009: {
                final String englishRemarks = errorResponse.optString("EnglishRemarks");
                final String appIdenifier = englishRemarks.substring(englishRemarks.indexOf("\u201c") + 1, englishRemarks.indexOf("\u201d"));
                remark = remark + "@@@" + appIdenifier;
                break;
            }
        }
        return remark;
    }
    
    private Integer getErrorcodeForDomain(final NSArray errorArrayChain) {
        final int errorPointer = this.getPointerToError(IOSErrorStatusHandler.ERROR_DOMAIN_LIST, new ArrayList(), errorArrayChain);
        final NSDictionary errorChainDict = (NSDictionary)errorArrayChain.objectAtIndex(errorPointer);
        final NSString errorString = (NSString)errorChainDict.objectForKey("ErrorDomain");
        final Integer errorCode = IOSErrorStatusHandler.PRODUCT_ERROR_DOMAIN_CODE.get(errorString.toString());
        return errorCode;
    }
    
    private Integer getPointerToError(final List preferredDomain, final List preferredErrorCode, final NSArray nsArr) {
        int prevIndexErrDomain = -1;
        int prevIndexErrcode = -1;
        int lenghtOfErrDomain = -1;
        int lenghtOfErrcode = -1;
        final int nsArrLenght = nsArr.count();
        for (int pointer = nsArrLenght - 1; pointer >= 0; --pointer) {
            final NSDictionary errorChainDict = (NSDictionary)nsArr.objectAtIndex(pointer);
            final NSString errorString = (NSString)errorChainDict.objectForKey("ErrorDomain");
            final int index = preferredDomain.indexOf(errorString.toString());
            if (prevIndexErrDomain < index) {
                prevIndexErrDomain = index;
                lenghtOfErrDomain = pointer;
            }
            final NSNumber errorCode = (NSNumber)errorChainDict.objectForKey("ErrorCode");
            final int indexErrCode = preferredErrorCode.indexOf(errorCode.toString());
            if (prevIndexErrcode < indexErrCode) {
                prevIndexErrcode = indexErrCode;
                lenghtOfErrcode = pointer;
            }
        }
        final int pointerToErr = (lenghtOfErrDomain != -1) ? lenghtOfErrDomain : ((lenghtOfErrcode != -1) ? lenghtOfErrcode : (nsArrLenght - 1));
        return pointerToErr;
    }
    
    static {
        IOSErrorStatusHandler.iosErrorStatus = null;
        IOSErrorStatusHandler.logger = Logger.getLogger("MDMCommandsLogger");
        PRODUCT_ERROR_CODE = new HashMap<String, Integer>() {
            {
                this.put("28001", 21006);
                this.put("28002", 21006);
                this.put("2500", 21006);
                this.put("12084", 21007);
                this.put("21005", 21005);
                this.put("4018", 4018);
                this.put("4019", 4019);
                this.put("48000", 48000);
                this.put("3002", 3002);
                this.put("12038", 21009);
                this.put("4026", 48001);
                this.put("4027", 48002);
                this.put("-43", 12144);
            }
        };
        PRODUCT_ERROR_REMARKS = new HashMap<Integer, String>() {
            {
                this.put(21006, "mdm.profile.wallpaper.error.invalidResponseImage");
                this.put(21007, "mdm.apps.ios.kiosk.installApp");
                this.put(21005, "dc.mdm.identical_profile_already_exist");
                this.put(4018, "mdm.profile.apn_already_exist_error_msg");
                this.put(4019, "dc.mdm.kiosk.error.msg.multiple.kiosk.payloads");
                this.put(48000, "dc.mdm.kiosk.conflicting.kiosk.payload");
                this.put(3002, "dc.mdm.kiosk.conflicting.kiosk.payload");
                this.put(21009, "mdm.profile.perappVPN.error");
                this.put(48001, "mdm.profile.passcode.error.disabled");
                this.put(12144, "mdm.ios.user.account.not.exist");
                this.put(48002, "mdm.profile.ios.kiosk.homescreen_multiple_failure");
                this.put(48003, "mdm.profile.ios.profile_installation_payload");
            }
        };
        PRODUCT_ERROR_DOMAIN_CODE = new HashMap<String, Integer>() {
            {
                this.put("MCSCEPErrorDomain", 21010);
                this.put("", 21011);
                this.put("NSOSStatusErrorDomain", 12144);
                this.put("MCInstallationErrorDomain", 48003);
            }
        };
        ERROR_DOMAIN_LIST = Arrays.asList("MCSCEPErrorDomain");
        IOS_PROFILE_ERROR_CODE_KB_LIST = Arrays.asList(21010, 29000, 9000, 21011, 21005);
    }
}
