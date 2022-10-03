package com.me.mdm.core.windows.commands;

import java.util.Comparator;
import java.util.TreeMap;
import com.me.mdm.framework.syncml.requestcmds.GetRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.ReplaceRequestCommand;
import java.util.Iterator;
import org.apache.commons.lang3.StringUtils;
import com.me.mdm.core.windows.xmlbeans.WpAppPackageDetails;
import com.me.mdm.core.windows.xmlbeans.WpAppPackages;
import com.me.mdm.core.xmlparser.XmlBeanUtil;
import com.me.mdm.core.windows.xmlbeans.WpInstalledAppInventoryQueryBean;
import com.me.mdm.framework.syncml.core.data.Meta;
import com.me.mdm.framework.syncml.core.data.Location;
import java.util.ArrayList;
import java.util.List;
import java.net.URLDecoder;
import com.me.mdm.framework.syncml.core.data.Item;
import com.me.mdm.framework.syncml.responsecmds.ResultsResponseCommand;
import com.me.mdm.framework.syncml.core.SyncMLResponseCommand;
import java.util.logging.Level;
import com.me.mdm.framework.syncml.core.SyncMLRequestCommand;
import com.me.mdm.framework.syncml.requestcmds.SequenceRequestCommand;
import org.json.JSONObject;
import com.me.mdm.framework.syncml.core.SyncMLMessage;
import java.util.Map;
import java.util.logging.Logger;

public class WinMobileInstalledAppListCommand extends WpInstalledApplicationListCommand
{
    protected String baseLocationURI;
    private Logger logger;
    public static final String OUTPUT_TYPE_FILTER = "OUTPUT_TYPE_FILTER";
    public static final String PACKAGE_TYPE_FILTER = "PACKAGE_TYPE_FILTER";
    public static final String APP_INSTALLED_SOURCE_FILTER = "APP_INSTALLED_SOURCE";
    public static final String PUBLISHER_NAME_FILTER = "PUBLISHER_NAME_FILTER";
    public static final String PACKAGE_FAMILY_NAME_FILTER = "PACKAGE_FAMILY_NAME_FILTER";
    public static final String SOURCE_FILTER_VALUE_SYSTEM = "System";
    public static final String SOURCE_FILTER_VALUE_NON_STORE = "nonStore";
    public static final String SOURCE_FILTER_VALUE_STORE = "Store";
    private static Map<String, String> productIDToAppName;
    
    public WinMobileInstalledAppListCommand() {
        this.baseLocationURI = "./Device/Vendor/MSFT/EnterpriseModernAppManagement/AppManagement/";
        this.logger = Logger.getLogger("MDMLogger");
    }
    
    @Override
    public void processRequest(final SyncMLMessage responseSyncML, final JSONObject jsonObject) {
        try {
            final SequenceRequestCommand sequenceRequestCommand = new SequenceRequestCommand();
            sequenceRequestCommand.setRequestCmdId(String.valueOf(jsonObject.get("COMMAND_UUID")));
            this.addAppInventoryQueryCommands(jsonObject, sequenceRequestCommand);
            if (String.valueOf(jsonObject.get("COMMAND_UUID")).equalsIgnoreCase("InstalledApplicationList")) {
                final JSONObject nonStoreAppsJson = new JSONObject();
                nonStoreAppsJson.put("COMMAND_UUID", (Object)String.valueOf(jsonObject.get("COMMAND_UUID")));
                nonStoreAppsJson.put("APP_INSTALLED_SOURCE", (Object)"nonStore");
                this.addAppInventoryQueryCommands(nonStoreAppsJson, sequenceRequestCommand);
            }
            responseSyncML.getSyncBody().addRequestCmd(sequenceRequestCommand);
        }
        catch (final Exception exp) {
            this.logger.log(Level.SEVERE, "Exception in creating Windows 10 App Inventory query", exp);
        }
    }
    
    public JSONObject processResponse(final SyncMLMessage requestSyncML) {
        JSONObject appListJson = new JSONObject();
        String invResultString = null;
        try {
            final List<SyncMLResponseCommand> responseCmds = requestSyncML.getSyncBody().getResponseCmds();
            for (int i = 0; i < responseCmds.size(); ++i) {
                final SyncMLResponseCommand resultCommand = responseCmds.get(i);
                if (resultCommand instanceof ResultsResponseCommand) {
                    final ArrayList itemList = resultCommand.getResponseItems();
                    for (int j = 0; j < itemList.size(); ++j) {
                        final Item item = itemList.get(j);
                        String productUri = item.getSource().getLocUri();
                        productUri = URLDecoder.decode(productUri, "utf-8");
                        if (productUri.contains("/AppManagement/AppInventoryResults")) {
                            invResultString = item.getData().toString();
                            appListJson = this.fetchAppListJSONFromXML(invResultString, appListJson);
                        }
                    }
                }
            }
            return appListJson;
        }
        catch (final Exception exp) {
            this.logger.log(Level.WARNING, "Exception occurred during processing of parseInstalledApplicationList", exp);
            return appListJson;
        }
    }
    
    protected Item createCommandItemTagElement(final String sLocationURI, final String sValue) {
        final Item commandItem = new Item();
        commandItem.setTarget(new Location(sLocationURI));
        commandItem.setData(sValue);
        return commandItem;
    }
    
    protected Item createCommandItemTagElement(final String sLocationURI, final String sValue, final String sFormat) {
        final Item cmdItem = this.createCommandItemTagElement(sLocationURI, sValue);
        final Meta metaTag = new Meta();
        metaTag.setFormat(sFormat);
        cmdItem.setMeta(metaTag);
        return cmdItem;
    }
    
    protected String getInventoryQueryString(final JSONObject jsonObject) throws Exception {
        final WpInstalledAppInventoryQueryBean appQueryBean = new WpInstalledAppInventoryQueryBean();
        appQueryBean.setResultDataSet(jsonObject.optString("OUTPUT_TYPE_FILTER", "PackageDetails"));
        appQueryBean.setPackageTypeFilter(jsonObject.optString("PACKAGE_TYPE_FILTER", "Main|Bundle"));
        appQueryBean.setAppInstallionSourceFilter(jsonObject.optString("APP_INSTALLED_SOURCE", "AppStore"));
        appQueryBean.setPackageFamilyNameFilter(jsonObject.optString("PACKAGE_FAMILY_NAME_FILTER", (String)null));
        appQueryBean.setPublisherNameFilter(jsonObject.optString("PUBLISHER_NAME_FILTER", (String)null));
        final JSONObject beanUtilJSON = new JSONObject();
        beanUtilJSON.put("BEAN_OBJECT", (Object)appQueryBean);
        final XmlBeanUtil<WpInstalledAppInventoryQueryBean> xmlBeanUtil = new XmlBeanUtil<WpInstalledAppInventoryQueryBean>(beanUtilJSON);
        final String invQueryString = xmlBeanUtil.beanToXmlString();
        return invQueryString;
    }
    
    private JSONObject fetchAppListJSONFromXML(final String xmlString, final JSONObject appListJson) throws Exception {
        final JSONObject xmlBeanUtilJSON = new JSONObject();
        WpAppPackages wpAppPackagesData = new WpAppPackages();
        xmlBeanUtilJSON.put("BEAN_OBJECT", (Object)wpAppPackagesData);
        final XmlBeanUtil<WpAppPackages> xmlBeanUtil = new XmlBeanUtil<WpAppPackages>(xmlBeanUtilJSON);
        wpAppPackagesData = xmlBeanUtil.xmlStringToBean(xmlString);
        final ArrayList<WpAppPackageDetails> wpAppPackageDetails = wpAppPackagesData.getListOfPackages();
        for (final WpAppPackageDetails wpAppPackageDetail : wpAppPackageDetails) {
            String packageFamilyName = wpAppPackageDetail.getPackageFamilyName();
            packageFamilyName = StringUtils.strip(packageFamilyName, "{}");
            String packageFullName = wpAppPackageDetail.getPackageFullName();
            packageFullName = StringUtils.strip(packageFullName, "{}");
            String name = wpAppPackageDetail.getName();
            if (WinMobileInstalledAppListCommand.productIDToAppName.containsKey(packageFamilyName)) {
                name = WinMobileInstalledAppListCommand.productIDToAppName.get(packageFamilyName);
            }
            else if (WinMobileInstalledAppListCommand.productIDToAppName.containsKey(name)) {
                name = WinMobileInstalledAppListCommand.productIDToAppName.get(name);
            }
            final JSONObject appDetailsJSON = new JSONObject();
            appDetailsJSON.put("Identifier", (Object)packageFamilyName);
            appDetailsJSON.put("Version", (Object)wpAppPackageDetail.getVersion());
            appDetailsJSON.put("packageIdentifier", (Object)packageFullName);
            appDetailsJSON.put("Name", (Object)name);
            appDetailsJSON.put("InstallDate", (Object)wpAppPackageDetail.getInstallDate());
            appDetailsJSON.put("Publisher", (Object)wpAppPackageDetail.getPublisher());
            appDetailsJSON.put("IsBundle", (Object)wpAppPackageDetail.getIsBundle());
            final JSONObject alreadyExitApp = appListJson.optJSONObject(packageFamilyName);
            if (alreadyExitApp == null || (alreadyExitApp != null && alreadyExitApp.get("IsBundle").equals("0"))) {
                appListJson.put(packageFamilyName, (Object)appDetailsJSON);
            }
        }
        return appListJson;
    }
    
    private SequenceRequestCommand addAppInventoryQueryCommands(final JSONObject jsonObject, final SequenceRequestCommand sequenceRequestCommand) throws Exception {
        final ReplaceRequestCommand appsQueryFilterReplaceRequestCommand = new ReplaceRequestCommand();
        appsQueryFilterReplaceRequestCommand.setRequestCmdId(String.valueOf(jsonObject.get("COMMAND_UUID")));
        final String appsQueryString = this.getInventoryQueryString(jsonObject);
        appsQueryFilterReplaceRequestCommand.addRequestItem(this.createCommandItemTagElement(this.baseLocationURI + "AppInventoryQuery", appsQueryString, "xml"));
        final GetRequestCommand appsQueryGetCommand = new GetRequestCommand();
        appsQueryGetCommand.setRequestCmdId(String.valueOf(jsonObject.get("COMMAND_UUID")));
        appsQueryGetCommand.addRequestItem(this.createCommandItemTagElement(this.baseLocationURI + "AppInventoryResults", null));
        sequenceRequestCommand.addRequestCmd(appsQueryFilterReplaceRequestCommand);
        sequenceRequestCommand.addRequestCmd(appsQueryGetCommand);
        return sequenceRequestCommand;
    }
    
    static {
        WinMobileInstalledAppListCommand.productIDToAppName = new TreeMap<String, String>(String.CASE_INSENSITIVE_ORDER) {
            {
                this.put("e5f8b2c4-75ae-45ee-9be8-212e34f77747", "Work or School account");
                this.put("39cf127b-8c67-c149-539a-c02271d07060", "Email and Accounts");
                this.put("microsoft.accountsControl", "Email and accounts");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5608", "SettingsPageKeyboard");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea560c", "SettingsPageTimeRegion");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5620", "SettingsPagePCSystemBluetooth");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5621", "SettingsPageNetworkAirplaneMode");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5623", "SettingsPageNetworkWiFi");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5629", "SettingsPageNetworkInternetSharing");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea562a", "SettingsPageAccountsWorkplace");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5640", "SettingsPageRestoreUpdate");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5802", "SettingsPageKidsCorner");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5804", "SettingsPageDrivingMode");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5808", "SettingsPageTimeLanguage");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea580a", "SettingsPageAppsCorner");
                this.put("b0894dfd-4671-4bb9-bc17-a8b39947ffb6", "SettingsPagePhoneNfc");
                this.put("b6e3e590-9fa5-40c0-86ac-ef475de98e88", "Advanced Info");
                this.put("09296e27-c9f3-4ab9-aa76-ecc4497d94bb", "Age out worker");
                this.put("44f7d2b4-553d-4bec-a8b7-634ce897ed5f", "Alarms and Clock");
                this.put("Microsoft.WindowsAlarms", "Alarms and Clock");
                this.put("20bf77a0-19c7-4daa-8db5-bc3dfdfa44ac", "App Downloads");
                this.put("b84f4722-313e-4f85-8f41-cf5417c9c5cb", "Assigned access lock app");
                this.put("5f28c179-2780-41df-b966-27807b8de02c", "Bing lock images");
                this.put("59553c14-5701-49a2-9909-264d034deb3d", "Block and filter");
                this.put("b58171c6-c70c-4266-a2e8-8f9c994f4456", "Calculator");
                this.put("Microsoft.WindowsCalculator", "Calculator");
                this.put("f0d8fefd-31cd-43a1-a45a-d0276db069f1", "Camera");
                this.put("Microsoft.WindowsCamera", "Camera");
                this.put("4c4ad968-7100-49de-8cd1-402e198d869e", "CertInstaller");
                this.put("b08997ca-60ab-4dce-b088-f92e9c7994f3", "Colour profile");
                this.put("af7d2801-56c0-4eb1-824b-dd91cdf7ece5", "Connect");
                this.put("Microsoft.DevicesFlow", "Connect");
                this.put("0db5fcff-4544-458a-b320-e352dfd9ca2b", "Contact Support");
                this.put("Windows.ContactSupport", "Contact Support");
                this.put("fd68dcf4-166f-4c55-a4ca-348020f71b94", "Cortana");
                this.put("Microsoft.Windows.Cortana", "Cortana");
                this.put("da52fa01-ac0f-479d-957f-bfe4595941cb", "Enterprise install app");
                this.put("373cb76e-7f6c-45aa-8633-b00e85c73261", "Equalizer");
                this.put("ead3e7c0-fae6-4603-8699-6a448138f4dc", "Excel");
                this.put("Microsoft.Office.Excel", "Excel");
                this.put("82a23635-5bd9-df11-a844-00237de2db9e", "Facebook");
                this.put("Microsoft.MSFacebook", "Facebook");
                this.put("73c58570-d5a7-46f8-b1b2-2a90024fc29c", "Field Medic");
                this.put("c5e2524a-ea46-4f67-841f-6a9465d9d515", "File Explorer");
                this.put("f725010e-455d-4c09-ac48-bcdef0d4b626", "FM Radio");
                this.put("b3726308-3d74-4a14-a84c-867c8c735c3c", "Get Started");
                this.put("Microsoft.Getstarted", "Get Started");
                this.put("106e0a97-8b19-42cf-8879-a8ed2598fcbb", "Glance");
                this.put("d2b6a184-da39-4c9a-9e0a-8b589b03dec0", "Groove Music");
                this.put("Microsoft.ZuneMusic", "Groove Music");
                this.put("df6c9621-e873-4e86-bb56-93e9f21b1d6f", "Hands-Free Activation");
                this.put("72803bd5-4f36-41a4-a349-e83e027c4722", "Hands-Free Activation");
                this.put("73c73cdd-4dea-462c-bd83-fa983056a4ef", "HAP update background worker");
                this.put("8fc25fd2-4e2e-4873-be44-20e57f6ec52b", "Lumia motion data");
                this.put("ed27a07e-af57-416b-bc0c-2596b622ef7d", "Maps");
                this.put("Microsoft.WindowsMaps", "Maps");
                this.put("27e26f40-e031-48a6-b130-d1f20388991a", "Messaging");
                this.put("Microsoft.Messaging", "Messaging");
                this.put("3a4fae89-7b7e-44b4-867b-f7e2772b8253", "Microsoft account");
                this.put("Microsoft.CloudExperienceHost", "Microsoft account");
                this.put("395589fb-5884-4709-b9df-f7d558663ffd", "Microsoft Edge");
                this.put("Microsoft.MicrosoftEdge", "Microsoft Edge");
                this.put("906beeda-b7e6-4ddc-ba8d-ad5031223ef9", "MiracastView");
                this.put("1e0440f1-7abf-4b9a-863d-177970eefb5e", "Money");
                this.put("Microsoft.BingFinance", "Money");
                this.put("6affe59e-0467-4701-851f-7ac026e21665", "Movies and TV");
                this.put("Microsoft.ZuneVideo", "Movies and TV");
                this.put("3da8a0c1-f7e5-47c0-a680-be8fd013f747", "Music downloads");
                this.put("2cd23676-8f68-4d07-8dd2-e693d4b01279", "Navigation bar");
                this.put("62f172d1-f552-4749-871c-2afd1c95c245", "Network services");
                this.put("9c3e8cad-6702-4842-8f61-b8b33cc9caf1", "News");
                this.put("Microsoft.BingNews", "News");
                this.put("ad543082-80ec-45bb-aa02-ffe7f4182ba8", "OneDrive");
                this.put("Microsoft.MicrosoftSkydrive", "OneDrive");
                this.put("ca05b3ab-f157-450c-8c49-a1f127f5e71d", "OneNote");
                this.put("Microsoft.Office.OneNote", "OneNote");
                this.put("a558feba-85d7-4665-b5d8-a2ff9c19799b", "Outlook Calendar and Mail");
                this.put("Microsoft.WindowsCommunicationsApps", "Outlook Calendar and Mail");
                this.put("60be1fb8-3291-4b21-bd39-2221ab166481", "People");
                this.put("Microsoft.People", "People");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5611", "Phone");
                this.put("f41b5d0e-ee94-4f47-9cfe-3d3934c5a2c7", "Phone (dialer)");
                this.put("Microsoft.CommsPhone", "Phone (dialer)");
                this.put("2864278d-09b5-46f7-b502-1c24139ecbdd", "Phone reset dialog");
                this.put("fca55e1b-b9a4-4289-882f-084ef4145005", "Photos");
                this.put("Microsoft.Windows.Photos", "Photos");
                this.put("c3215724-b279-4206-8c3e-61d1a9d63ed3", "Podcasts");
                this.put("Microsoft.MSPodcast", "Podcasts");
                this.put("063773e7-f26f-4a92-81f0-aa71a1161e30", "Posdcast downloads");
                this.put("b50483c4-8046-4e1b-81ba-590b24935798", "Powerpoint");
                this.put("Microsoft.Office.PowerPoint", "Powerpoint");
                this.put("0d32eeb1-32f0-40da-8558-cea6fcbec4a4", "PrintDialog");
                this.put("Microsoft.PrintDialog", "PrintDialog");
                this.put("c60e79ca-063b-4e5d-9177-1309357b2c3f", "Purchase dialog");
                this.put("aec3bfad-e38c-4994-9c32-50bd030730ec", "Rate your device");
                this.put("3e962450-486b-406b-abb5-d38b4ee7e6fe", "RingtoneApp.WindowsPhone");
                this.put("Microsoft.Tonepicker", "RingtoneApp.WindowsPhone");
                this.put("d8cf8ec7-ec6d-4892-aab9-1e3a4b5fa24b", "Save ringtone");
                this.put("2a4e62d8-8809-4787-89f8-69d0f01654fb", "Settings");
                this.put("07d87655-e4f0-474b-895a-773790ad4a32", "Setup wizard");
                this.put("c3f8e570-68b3-4d6a-bdbb-c0a3f4360a51", "Skype");
                this.put("Microsoft.SkypeApp", "Skype");
                this.put("0f4c8c7e-7114-4e1e-a84c-50664db13b17", "Sports");
                this.put("Microsoft.BingSports", "Sports");
                this.put("e232aa77-2b6d-442c-b0c3-f3bb9788af2a", "SSMHost");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5602", "Start");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea564d", "Storage");
                this.put("7d47d89a-7900-47c5-93f2-46eb6d94c159", "Store");
                this.put("Microsoft.WindowsStore", "Store");
                this.put("bbc57c87-46af-4c2c-824e-ac8104cceb38", "Touch (gestures and touch)");
                this.put("7311b9c5-a4e9-4c74-bc3c-55b06ba95ad0", "Voice recorder");
                this.put("Microsoft.WindowsSoundRecorder", "Voice recorder");
                this.put("587a4577-7868-4745-a29e-f996203f1462", "Wallet");
                this.put("Microsoft.MicrosoftWallet", "Wallet");
                this.put("63c2a117-8604-44e7-8cef-df10be3a57c8", "Weather");
                this.put("Microsoft.BingWeather", "Weather");
                this.put("cdd63e31-9307-4ccb-ab62-1ffa5721b503", "Windows default lock screen");
                this.put("7604089d-d13f-4a2d-9998-33fc02b63ce3", "Windows Feedback");
                this.put("Microsoft.WindowsFeedback", "Windows Feedback");
                this.put("258f115c-48f4-4adb-9a68-1387e634459b", "Word");
                this.put("Microsoft.Office.Word", "Word");
                this.put("Microsoft.AAD.BrokerPlugin", "Work or school account");
                this.put("b806836f-eebe-41c9-8669-19e243b81b83", "Xbox");
                this.put("Microsoft.XboxApp", "Xbox");
                this.put("ba88225b-059a-45a2-a8eb-d3580283e49d", "Xbox identity provider");
                this.put("Microsoft.XboxIdentityProvider", "Xbox identity provider");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5607", "Phone Lock Settings");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea561f", "Cell Settings");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5624", "OBExParser");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5803", "Kids Corner Settings");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea562c", "Sim Applications");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5647", "Backup Settings");
                this.put("5b04b775-356b-4aa0-aaf8-6491ffea5648", "Backup");
            }
        };
    }
}
