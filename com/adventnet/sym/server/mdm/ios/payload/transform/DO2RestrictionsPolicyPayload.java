package com.adventnet.sym.server.mdm.ios.payload.transform;

import org.json.JSONException;
import java.util.List;
import org.json.JSONObject;
import java.util.ArrayList;
import com.adventnet.sym.server.mdm.apps.ios.IOSModifiedEnterpriseAppsUtil;
import com.me.mdm.server.util.MDMFeatureParamsHandler;
import com.adventnet.sym.server.mdm.apps.AppsUtil;
import com.me.mdm.server.profiles.kiosk.IOSKioskProfileDataHandler;
import com.adventnet.persistence.DataAccessException;
import java.util.Iterator;
import java.util.logging.Level;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.RestrictionsPayload;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;
import java.util.HashMap;
import java.util.logging.Logger;

public class DO2RestrictionsPolicyPayload implements DO2Payload
{
    public static Logger logger;
    public static HashMap<String, String> restrictionMapping;
    
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        RestrictionsPayload payload = null;
        final RestrictionsPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("RestrictionsPolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                final boolean allowAddingGameCenterFriends = (boolean)row.get("ALLOW_ADD_GAME_CENTER_FRIEND");
                final boolean allowAppInstallation = (boolean)row.get("ALLOW_APP_INSTALLATION");
                final boolean allowAppRemoval = (boolean)row.get("ALLOW_APP_REMOVAL");
                final boolean allowAssistant = (boolean)row.get("ALLOW_ASSISTANT");
                final boolean allowAssistantWhenLocked = (boolean)row.get("ALLOW_ASSISTANT_WHEN_LOCKED");
                final boolean forceAssistantProfanityFilter = (boolean)row.get("FORCE_ASSIST_PROFANITY_FILTER");
                final boolean allowPassbookWhenLocked = (boolean)row.get("ALLOW_PASSBOOK_WHEN_LOCKED");
                final boolean allowCamera = (boolean)row.get("ALLOW_USE_OF_CAMERA");
                final boolean allowCloudBackup = (boolean)row.get("ALLOW_CLOUD_BACKUP");
                final boolean allowCloudDocumentSync = (boolean)row.get("ALLOW_CLOUD_DOCUMENT_SYNC");
                final boolean allowDiagnosticSubmission = (boolean)row.get("ALLOW_DIAGNOSTIC_SUBMISSION");
                final boolean allowExplicitContent = (boolean)row.get("ALLOW_EXPLICIT_CONTENT");
                final boolean allowGlobalBackgroundFetchWhenRoaming = (boolean)row.get("ALLOW_SYNC_WHILE_ROAMING");
                final boolean allowInAppPurchases = (boolean)row.get("ALLOW_INAPP_PURCHASE");
                final boolean allowGameCenter = (boolean)row.get("ALLOW_GAME_CENTER");
                final boolean allowMultiplayerGaming = (boolean)row.get("ALLOW_MULTIPLAYER_GAMING");
                final boolean allowPhotoStream = (boolean)row.get("ALLOW_PHOTO_STREAM");
                final boolean allowSharedStream = (boolean)row.get("ALLOW_SHARED_STREAM");
                final boolean allowSafari = (boolean)row.get("ALLOW_SAFARI");
                final boolean allowiMessage = (boolean)row.get("ALLOW_IMESSAGE");
                final boolean allowScreenShot = (boolean)row.get("ALLOW_SCREEN_CAPTURE");
                final boolean allowUntrustedTLSPrompt = (boolean)row.get("ALLOW_UNTRUSTED_TLS_PROMPT");
                final boolean allowVideoConferencing = (boolean)row.get("ALLOW_FACE_TIME");
                final boolean allowVoiceDialing = (boolean)row.get("ALLOW_VOICE_DIALING");
                final boolean allowYoutube = (boolean)row.get("ALLOW_USE_OF_YOUTUBE");
                final boolean allowIBookstore = (boolean)row.get("ALLOW_USE_OF_IBOOKSTORE");
                final boolean allowIBookstoreEroticaMedia = (boolean)row.get("ALLOW_IBOOKSTORE_EROTICA_MEDIA");
                final boolean allowiTunes = (boolean)row.get("ALLOW_ITUNES");
                final boolean forceEncryptedBackup = (boolean)row.get("FORCE_ENCRYPTED_BACKUP");
                final boolean allowProfileInstallation = (boolean)row.get("ALLOW_PROFILE_INSTALLATION");
                final boolean forceITunesStorePasswordEntry = (boolean)row.get("FORCE_ISTORE_PWD_ENTRY");
                final Integer safariAcceptCookies = (Integer)row.get("SAFARI_ACCEPT_COOKIES");
                final boolean safariAllowAutoFill = (boolean)row.get("SAFARI_ALLOW_AUTOFILL");
                final boolean safariAllowJavaScript = (boolean)row.get("SAFARI_ALLOW_JAVASCRIPT");
                final boolean safariAllowPopups = (boolean)row.get("SAFARI_ALLOW_POPUPS");
                final boolean safariForceFraudWarning = (boolean)row.get("SAFARI_FORCE_FRAUD_WARNING");
                final boolean isRatingEnabled = (boolean)row.get("IS_RATING_ENABLED");
                final boolean allowAirDrop = (boolean)row.get("ALLOW_AIRDROP");
                final boolean allowUserContentInSiri = (boolean)row.get("ALLOW_ASSISTANT_USER_CONTENT");
                final boolean allowTouchID = (boolean)row.get("ALLOW_TOUCH_ID");
                final boolean showControlCenter = (boolean)row.get("SHOW_CONTROL_CENTER");
                final boolean showNotificationCenter = (boolean)row.get("SHOW_NOTIFICATION_CENTER");
                final boolean showTodayView = (boolean)row.get("SHOW_TODAY_VIEW");
                final boolean allowAutomaticUpdatesForCerti = (boolean)row.get("ALLOW_OTA_PKI_UPDATES");
                final boolean forceLimitedAdTracking = (boolean)row.get("FORCE_LIMITED_AD_TRACKING");
                final boolean allowAccountModification = (boolean)row.get("ALLOW_ACCOUNT_MODIFICATION");
                final boolean allowFindMyFriendsMod = (boolean)row.get("ALLOW_FIND_MY_FRIENDS_MOD");
                final boolean allowHostPairing = (boolean)row.get("ALLOW_HOST_PAIRING");
                final boolean allowAppCellData = (boolean)row.get("ALLOW_APP_CELLULAR_DATA");
                final boolean allowOpenDocInUnmanaged = (boolean)row.get("ALLOW_OPEN_DOC_IN_UNMANAGED");
                final boolean allowOpenDocInManaged = (boolean)row.get("ALLOW_OPEN_DOC_IN_MANAGED");
                final boolean allowManagedAppCloudSync = (boolean)row.get("ALLOW_MANAGED_APP_CLOUD_SYNC");
                final boolean allowEnterpriseBookBackup = (boolean)row.get("ALLOW_MANAGED_BOOK_BACKUP");
                final boolean allowEnterpriseBookMetadataSync = (boolean)row.get("ALLOW_MANAGED_BOOK_SYNC");
                final boolean allowActivityContinuation = (boolean)row.get("ALLOW_ACTIVITY_CONTINUATION");
                final boolean allowEraseContentSettings = (boolean)row.get("ALLOW_ERASE_CONTENT_SETTINGS");
                final boolean allowEnablingRestriction = (boolean)row.get("ALLOW_ENABLING_RESTRICTION");
                final boolean allowSpotlightResult = (boolean)row.get("ALLOW_SPOTLIGHT_RESULT");
                final boolean allowCloudKeychainSync = (boolean)row.get("ALLOW_CLOUD_KEYCHAIN_SYNC");
                final boolean forceAirplayoutgoingRequestPassword = (boolean)row.get("FORCE_AIRPLAY_OUTGOING_PWD");
                final boolean forceAirplayIncomigRequestPassword = (boolean)row.get("FORCE_AIRPLAY_INCOMING_PWD");
                final boolean allowPodcasts = (boolean)row.get("ALLOW_PODCASTS");
                final boolean allowDefnLookup = (boolean)row.get("ALLOW_DICTIONARY_LOOKUP");
                final boolean allowPredictiveKeyboard = (boolean)row.get("ALLOW_PREDICTIVE_KEYBOARD");
                final boolean allowAutoCorrection = (boolean)row.get("ALLOW_AUTO_CORRECTION");
                final boolean allowSpellcheck = (boolean)row.get("ALLOW_SPELLCHECK");
                final boolean forceWatchWristDetection = (boolean)row.get("FORCE_WATCH_WRIST_DETECT");
                final boolean allowModifyTouchId = (boolean)row.get("ALLOW_MODIFY_TOUCH_ID");
                final boolean allowAirPlayIncomingRequests = (boolean)row.get("AIRPLAY_INCOMING_REQUEST");
                final boolean allowDeviceSleep = (boolean)row.get("ALLOW_DEVICE_SLEEP");
                final boolean allowRemoteAppPairing = (boolean)row.get("ALLOW_REMOTE_APP_PAIRING");
                final boolean forceAirDropUnmanaged = (boolean)row.get("FORCE_AIRDROP_UNMANAGED");
                final boolean allowKeyboardShortcuts = (boolean)row.get("ALLOW_KEYBOARD_SHORTCUT");
                final boolean allowPairedWatch = (boolean)row.get("ALLOW_PAIRED_WATCH");
                final boolean allowPasscodeModification = (boolean)row.get("ALLOW_MODIFI_PASSCODE");
                final boolean allowDeviceNameModification = (boolean)row.get("ALLOW_MODIFI_DEVICE_NAME");
                final boolean allowWallpaperModification = (boolean)row.get("ALLOW_MODIFI_WALLPAPER");
                final boolean allowAutomaticAppDownloads = (boolean)row.get("ALLOW_AUTO_APP_DOWNLOAD");
                final boolean allowEnterpriseAppTrust = (boolean)row.get("ALLOW_MANAGED_APP_TRUST");
                final boolean allowCloudPhotoLibrary = (boolean)row.get("ALLOW_CLOUD_PHOTO_LIB");
                final boolean allowNews = (boolean)row.get("ALLOW_NEWS");
                final boolean allowMusicService = (boolean)row.get("ALLOW_MUSIC_SERVICE");
                final boolean allowRadioService = (boolean)row.get("ALLOW_RADIO_SERVICE");
                final boolean allowDiagnosticSubmissionModification = (boolean)row.get("ALLOW_DIAG_SUB_MODIFICATION");
                final boolean allowBluetoothModification = (boolean)row.get("ALLOW_BLUETOOTH_MODIFICATION");
                final boolean forceWiFiWhitelisting = (boolean)row.get("FORCE_WIFI_WHITELISTING");
                final boolean allowDictation = (boolean)row.get("ALLOW_DICTATION");
                final boolean allowAirPrint = (boolean)row.get("ALLOW_AIRPRINT");
                final boolean allowVPNCreation = (boolean)row.get("ALLOW_VPN_CREATION");
                final boolean allowAirPrintCredential = (boolean)row.get("ALLOW_AIRPRINT_CREDENTIAL_STORAGE");
                final boolean forceAirprintTLS = (boolean)row.get("FORCE_AIRPRINT_TLS");
                final boolean allowAirPrintBeaconDiscovery = (boolean)row.get("ALLOW_AIRPRINT_IBEACON_DISCOVERY");
                final boolean allowRemoteScreen = (boolean)row.get("ALLOW_CLASSROOM_REMOTEVIEW");
                final boolean forceClassRoomAutoJoin = (boolean)row.get("FORCE_CLASSROOM_AUTO_JOIN");
                final boolean forceClassRoomUnpromptAppAndDeviceLock = (boolean)row.get("FORCE_CLASSROOM_APPDEVICELOCK");
                final boolean forceClassRoomUnpromptForScreenObservation = (boolean)row.get("FORCE_CLASSROOM_REMOTEVIEW");
                final boolean requestToLeaveClassroom = (boolean)row.get("REQUEST_TO_LEAVE_CLASSROOM");
                final boolean allowProximityForNewDevice = (boolean)row.get("ALLOW_PROXIMITY_FOR_NEWDEVICE");
                final boolean forceAuthenticationOnAutofill = (boolean)row.get("AUTHENTICATE_BEFORE_AUTOFILL");
                final boolean forceDateAndTime = (boolean)row.get("FORCE_DATE_TIME");
                final boolean preventPasswordAutofill = (boolean)row.get("ALLOW_PASSWORD_AUTOFILL");
                final boolean allowPasswordSharing = (boolean)row.get("ALLOW_PASSWORD_SHARING");
                final boolean allowPasswordProximity = (boolean)row.get("ALLOW_PASSWORD_PROXIMITY");
                final boolean allowContactWrite = (boolean)row.get("ALLOW_MANAGED_WRITE_UNMANAGED_CONTACT");
                final boolean allowContactRead = (boolean)row.get("ALLOW_UNMANAGED_READ_MANAGED_CONTACT");
                final boolean usbRestrictionMode = (boolean)row.get("ALLOW_USB_RESTRICTION_MODE");
                final boolean allowContentCaching = (boolean)row.get("ALLOW_CONTENT_CACHING");
                final boolean allowItuneFileSharing = (boolean)row.get("ALLOW_ITUNES_FILE_SHARING");
                final boolean allowAutoUnlock = (boolean)row.get("ALLOW_AUTO_UNLOCK");
                final boolean allowCloudDesktopAndDocument = (boolean)row.get("ALLOW_CLOUD_DESKTOP_DOCUMENT");
                final boolean allowCloudBookmark = (boolean)row.get("ALLOW_CLOUD_BOOKMARKS");
                final boolean allowCloudMails = (boolean)row.get("ALLOW_CLOUD_MAIL");
                final boolean allowCloudCalender = (boolean)row.get("ALLOW_CLOUD_CALENDER");
                final boolean allowCloudReminder = (boolean)row.get("ALLOW_CLOUD_REMINDERS");
                final boolean allowCloudAddressBook = (boolean)row.get("ALLOW_CLOUD_ADDRESSBOOK");
                final boolean allowCloudNotes = (boolean)row.get("ALLOW_CLOUD_NOTES");
                final boolean restrictHotspot = (boolean)row.get("ALLOW_HOTSPOT_MODIFICATION");
                final boolean allowEsimModification = (boolean)row.get("ALLOW_ESIM_MODIFICATION");
                final boolean allowSiriLogging = (boolean)row.get("ALLOW_SIRI_LOGGING");
                final boolean allowContinuousPath = (boolean)row.get("ALLOW_CONTINUOUS_PATH_KEYBOARD");
                final boolean allowFindMyFriend = (boolean)row.get("ALLOW_FIND_MY_FRIEND");
                final boolean allowFindMyDevice = (boolean)row.get("ALLOW_FIND_MY_DEVICE");
                final boolean allowUSBFileDrive = (boolean)row.get("ALLOW_USB_FILE_DRIVE");
                final boolean forceWifiOn = (boolean)row.get("FORCE_WIFI_ON");
                final boolean allowSystemApp = (boolean)row.get("ALLOW_SYSTEM_APP_REMOVAL");
                final boolean allowNotificationModification = (boolean)row.get("ALLOW_NOTIFICATION_MODIFICATION");
                final boolean allowAppClips = (boolean)row.get("ALLOW_APP_CLIPS");
                final boolean allowApplePersonalizedAds = (boolean)row.get("ALLOW_APPLE_PERSONALIZED_ADS");
                final boolean allowCellularPlanModification = (boolean)row.get("ALLOW_CELLULAR_PLAN_MODIFICATION");
                final boolean allowFileNetworkDriveAccess = (boolean)row.get("ALLOW_FILE_NETWORK_DRIVE_ACCESS");
                final boolean allowGuestAccount = (boolean)row.get("ALLOW_SHARED_DEVICE_GUEST_ACCOUNT");
                final boolean isAllowNfc = (boolean)row.get("ALLOW_NFC");
                final boolean isAllowUnpairedExternalBootToRecovery = (boolean)row.get("ALLOW_UNPAIRED_EXTERNAL_BOOT_TO_RECOVERY");
                final boolean isForceOnDeviceOnlyDictation = (boolean)row.get("FORCE_ON_DEVICE_ONLY_DICTATION");
                final boolean isForceOnDeviceOnlyTranslation = (boolean)row.get("FORCE_ON_DEVICE_ONLY_TRANSLATION");
                final boolean isRequireManagedPasteBoard = (boolean)row.get("REQUIRE_MANAGED_PASTEBOARD");
                final boolean isAllowCloudPrivateRelay = (boolean)row.get("ALLOW_CLOUD_PRIVATE_RELAY");
                final boolean allowMailPrivacyProtection = (boolean)row.get("ALLOW_MAIL_PRIVACY_PROTECTION");
                final boolean allowUniversalControl = (boolean)row.get("ALLOW_UNIVERSAL_CONTROL");
                final boolean allowAutomaticScreenSaver = (boolean)row.get("ALLOW_AUTOMATIC_SCREEN_SAVER");
                final boolean allowRapidSecurityResponseInstallation = (boolean)row.get("ALLOW_RAPID_SECURITY_RESPONSE_INSTALLATION");
                final boolean allowRapidSecurityResponseRemoval = (boolean)row.get("ALLOW_RAPID_SECURITY_RESPONSE_REMOVAL");
                payload = new RestrictionsPayload(1, "MDM", "com.mdm.mobiledevice.restrictions", "Restriction Policy");
                payload.setAllowAutomaticAppDownloads(allowAutomaticAppDownloads);
                payload.setAllowAppInstallation(allowAppInstallation);
                payload.setAllowUIAppInstallation(allowAppInstallation);
                payload.setAllowAppRemoval(allowAppRemoval);
                payload.setAllowEnterpriseAppTrust(allowEnterpriseAppTrust);
                payload.setAllowCamera(allowCamera);
                if (allowCamera) {
                    payload.setAllowVideoConferencing(allowVideoConferencing);
                }
                payload.setAllowIMessage(allowiMessage);
                payload.setAllowScreenShot(allowScreenShot);
                payload.setAllowGlobalBackgroundFetchWhenRoaming(allowGlobalBackgroundFetchWhenRoaming);
                payload.setAllowAssistant(allowAssistant);
                if (allowAssistant) {
                    payload.setAllowAssistantWhileLocked(allowAssistantWhenLocked);
                    payload.setForceAssistantProfanityFilter(forceAssistantProfanityFilter);
                    payload.setAllowUserContentInSiri(allowUserContentInSiri);
                }
                payload.setAllowPassbookWhileLocked(allowPassbookWhenLocked);
                payload.setAllowVoiceDialing(allowVoiceDialing);
                payload.setAllowInAppPurchases(allowInAppPurchases);
                payload.setForceITunesStorePasswordEntry(forceITunesStorePasswordEntry);
                payload.setAllowGameCenter(allowGameCenter);
                if (allowGameCenter) {
                    payload.setAllowMultiplayerGaming(allowMultiplayerGaming);
                    payload.setAllowAddingGameCenterFriends(allowAddingGameCenterFriends);
                }
                payload.setAllowYoutube(allowYoutube);
                payload.setAllowIBookstore(allowIBookstore);
                if (allowIBookstore) {
                    payload.setAllowIBookstoreEroticaMedia(allowIBookstoreEroticaMedia);
                }
                payload.setAllowiTunes(allowiTunes);
                payload.setAllowSafari(allowSafari);
                if (allowSafari) {
                    payload.setSafariAcceptCookies(safariAcceptCookies);
                    payload.setSafariAllowAutoFill(safariAllowAutoFill);
                    payload.setSafariAllowJavaScript(safariAllowJavaScript);
                    payload.setSafariAllowPopups(safariAllowPopups);
                    payload.setSafariForceFraudWarning(safariForceFraudWarning);
                }
                payload.setAllowNews(allowNews);
                payload.setAllowCloudBackup(allowCloudBackup);
                payload.setAllowCloudDocumentSync(allowCloudDocumentSync);
                payload.setAllowCloudPhotoLibrary(allowCloudPhotoLibrary);
                payload.setAllowPhotoStream(allowPhotoStream);
                payload.setAllowSharedStream(allowSharedStream);
                payload.setAllowDiagnosticSubmission(allowDiagnosticSubmission);
                payload.setAllowUntrustedTLSPrompt(allowUntrustedTLSPrompt);
                payload.setForceEncryptedBackup(forceEncryptedBackup);
                payload.setAllowProfileInstallation(allowProfileInstallation);
                payload.setAllowExplicitContent(allowExplicitContent);
                if (isRatingEnabled) {
                    final String countryCode = (String)row.get("COUNTRY_CODE");
                    final Integer movieRatings = (Integer)row.get("MOVIES_RATING_VALUE");
                    final Integer tvShowRatings = (Integer)row.get("TV_SHOWS_RATING_VALUE");
                    final Integer appsRatings = (Integer)row.get("APPS_RATING_VALUE");
                    payload.setCountrycode(countryCode);
                    payload.setMovieRatings(movieRatings);
                    payload.setTvShowRatings(tvShowRatings);
                    payload.setAppsRatings(appsRatings);
                }
                payload.setAllowAirDrop(allowAirDrop);
                if (allowAirDrop) {
                    payload.setForceAirDropUnmanaged(forceAirDropUnmanaged);
                }
                payload.setAllowTouchID(allowTouchID);
                payload.setShowControlCenter(showControlCenter);
                payload.setShowNotificationCenter(showNotificationCenter);
                payload.setShowTodayView(showTodayView);
                payload.setAllowAutomaticUpdatesForCerti(allowAutomaticUpdatesForCerti);
                payload.setForceLimitedAdTracking(forceLimitedAdTracking);
                payload.setAllowAccountModification(allowAccountModification);
                payload.setAllowFindMyFriendsMod(allowFindMyFriendsMod);
                payload.setAllowHostPairing(allowHostPairing);
                payload.setAllowAppCellData(allowAppCellData);
                payload.setAllowOpenDocInUnmanaged(allowOpenDocInUnmanaged);
                payload.setAllowOpenDocInManaged(allowOpenDocInManaged);
                payload.setAllowManagedAppCloudSync(allowManagedAppCloudSync);
                payload.setAllowEnterpriseBookBackup(allowEnterpriseBookBackup);
                payload.setAllowEnterpriseBookMetadataSync(allowEnterpriseBookMetadataSync);
                payload.setAllowActivityContinuation(allowActivityContinuation);
                payload.setAllowEraseContentAndSettings(allowEraseContentSettings);
                payload.setAllowEnablingRestriction(allowEnablingRestriction);
                payload.setAllowSpotlightInternetResults(allowSpotlightResult);
                payload.setAllowCloudKeychainSync(allowCloudKeychainSync);
                payload.setForceAirPlayOutgoingRequestPairingPassword(forceAirplayoutgoingRequestPassword);
                payload.setForceAirPlayIncomingRequestPairingPassword(forceAirplayIncomigRequestPassword);
                payload.setAllowPodcasts(allowPodcasts);
                payload.setAllowDefinitionLookup(allowDefnLookup);
                payload.setAllowPredictiveKeyboard(allowPredictiveKeyboard);
                payload.setAllowAutoCorrection(allowAutoCorrection);
                payload.setAllowSpellCheck(allowSpellcheck);
                payload.setAllowKeyboardShortcuts(allowKeyboardShortcuts);
                payload.setAllowAirPlayIncomingRequests(allowAirPlayIncomingRequests);
                payload.setAllowDeviceSleep(allowDeviceSleep);
                payload.setAllowRemoteAppPairing(allowRemoteAppPairing);
                payload.setAllowModifyTouchId(allowModifyTouchId);
                payload.setAllowPasscodeModification(allowPasscodeModification);
                payload.setAllowWallpaperModification(allowWallpaperModification);
                payload.setForceWatchWristDetection(forceWatchWristDetection);
                payload.setAllowPairedWatch(allowPairedWatch);
                payload.setForceWiFiWhitelisting(forceWiFiWhitelisting);
                payload.setAllowDictation(allowDictation);
                payload.setAllowMusicService(allowMusicService);
                payload.setAllowRadioService(allowRadioService);
                if (allowDiagnosticSubmission) {
                    payload.setAllowDiagnosticSubmissionModification(allowDiagnosticSubmissionModification);
                }
                payload.setAllowBluetoothModification(allowBluetoothModification);
                payload.setAllowHotspotRestriction(restrictHotspot);
                payload.setAllowSiriLogging(allowSiriLogging);
                payload.setAllowEsimModification(allowEsimModification);
                payload.setAllowAirPrint(allowAirPrint);
                if (allowAirPrint) {
                    payload.setAllowAirPrintCredential(allowAirPrintCredential);
                    payload.setForceAirprintTLS(forceAirprintTLS);
                    payload.setAllowAirprintDiscovery(allowAirPrintBeaconDiscovery);
                }
                payload.setAllowVpnCreation(allowVPNCreation);
                payload.setAllowRemoteScreenObservation(allowRemoteScreen);
                if (allowRemoteScreen) {
                    payload.setForceClassroomUnpromptedScreenObservation(forceClassRoomUnpromptForScreenObservation);
                }
                payload.setForceClassroomAutomaticallyJoinClasses(forceClassRoomAutoJoin);
                payload.setForceClassroomUnpromptedAppAndDeviceLock(forceClassRoomUnpromptAppAndDeviceLock);
                payload.setRequestToLeaveClassRoom(requestToLeaveClassroom);
                payload.setProximityForNewDevice(allowProximityForNewDevice);
                payload.setForceAuthenticationOnAutofill(forceAuthenticationOnAutofill);
                payload.setForceDateAndTime(forceDateAndTime);
                payload.setPreventAutofillPassword(preventPasswordAutofill);
                payload.setAllowPasswordSharing(allowPasswordSharing);
                payload.setAllowPasswordProximity(allowPasswordProximity);
                payload.setAllowManagedWriteUnmanagedContact(allowContactWrite);
                payload.setAllowUnmanagedReadManagedContact(allowContactRead);
                payload.setUSBRestrictedMode(usbRestrictionMode);
                payload.setAllowContentCaching(allowContentCaching);
                payload.setAllowiTuneFileSharing(allowItuneFileSharing);
                payload.setAllowAutoUnlock(allowAutoUnlock);
                payload.setAllowCloudDesktopDocument(allowCloudDesktopAndDocument);
                payload.setAllowCloudBookmark(allowCloudBookmark);
                payload.setAllowCloudMail(allowCloudMails);
                payload.setAllowCloudCalender(allowCloudCalender);
                payload.setAllowCloudReminder(allowCloudReminder);
                payload.setAllowCloudAddressBook(allowCloudAddressBook);
                payload.setAllowCloudNotes(allowCloudNotes);
                payload.setAllowContinuousPath(allowContinuousPath);
                payload.setFindMyFriend(allowFindMyFriend);
                payload.setFindMyDevice(allowFindMyDevice);
                payload.setWifiPowerModification(forceWifiOn);
                payload.setUSBFileDrive(allowUSBFileDrive);
                payload.setAllowSystemApp(allowSystemApp);
                payload.setAllowNotificationModification(allowNotificationModification);
                payload.setSharedIpadGuestAccount(allowGuestAccount);
                payload.setAllowNfc(isAllowNfc);
                payload.setAllowUnpairedExternalBootToRecovery(isAllowUnpairedExternalBootToRecovery);
                payload.setForceOnDeviceOnlyDictation(isForceOnDeviceOnlyDictation);
                payload.setForceOnDeviceOnlyTranslation(isForceOnDeviceOnlyTranslation);
                payload.setRequireManagedPasteBoard(isRequireManagedPasteBoard);
                payload.setAllowCloudPrivateRelay(isAllowCloudPrivateRelay);
                payload.setAllowMailPrivacyProtection(allowMailPrivacyProtection);
                payload.setAllowUniversalControl(allowUniversalControl);
                payload.setAllowAutomaticScreenSaver(allowAutomaticScreenSaver);
                payload.setAllowRapidSecurityResponseInstallation(allowRapidSecurityResponseInstallation);
                payload.setAllowRapidSecurityResponseRemoval(allowRapidSecurityResponseRemoval);
            }
        }
        catch (final Exception ex) {
            DO2RestrictionsPolicyPayload.logger.log(Level.SEVERE, "Exception in restriction payload", ex);
        }
        payloadArray[0] = payload;
        return payloadArray;
    }
    
    public IOSPayload autoCreateRestrictionPayload(final int configID, final DataObject dataObject) throws DataAccessException {
        final HashMap restrictionListMap = this.profileWiseRestrict(configID, dataObject);
        final RestrictionsPayload restrictPayload = new RestrictionsPayload(1, "MDM", "com.mdm.mobiledevice.restrictions", "Restriction Policy");
        for (final String restrictionType : restrictionListMap.keySet()) {
            restrictPayload.setProfileWiseRestrictionCommand(restrictionType, restrictionListMap.get(restrictionType));
        }
        return restrictPayload;
    }
    
    public HashMap profileWiseRestrict(final int configid, final DataObject dataObject) throws DataAccessException {
        final HashMap restrictionHashMap = new HashMap();
        switch (configid) {
            case 517: {
                restrictionHashMap.put("allowOpenFromManagedToUnmanaged", false);
                restrictionHashMap.put("forceAirDropUnmanaged", true);
                break;
            }
            case 522: {
                restrictionHashMap.put("allowWallpaperModification", false);
                break;
            }
            case 518: {
                final Row wallpaperRow = dataObject.getRow("MDMWallpaperPolicy");
                final boolean wallpaperChange = (boolean)wallpaperRow.get("ALLOW_WALLPAPER_CHANGE");
                restrictionHashMap.put("allowWallpaperModification", wallpaperChange);
                break;
            }
        }
        return restrictionHashMap;
    }
    
    public IOSPayload kioskRestrictionPayload(final Long collectionId, final Long customerId) {
        try {
            final IOSKioskProfileDataHandler kioskHandler = new IOSKioskProfileDataHandler();
            final JSONObject appDetails = kioskHandler.isProfileApplicableForIOSKioskAutomation(collectionId, customerId);
            final Long appGroupId = appDetails.optLong("APP_GROUP_ID");
            String identifier = null;
            if (appGroupId != null && appGroupId != 0L) {
                identifier = AppsUtil.getInstance().getIdentifierFromAppGroupID(appGroupId);
            }
            if (MDMFeatureParamsHandler.getInstance().isFeatureEnabled("AllowSameBundleIDStoreAndEnterpriseAppForIOS")) {
                identifier = IOSModifiedEnterpriseAppsUtil.getOriginalBundleIDOfEnterpriseApp(identifier);
            }
            final List whitelistedApps = new ArrayList();
            whitelistedApps.add(identifier);
            whitelistedApps.add("com.apple.webapp");
            whitelistedApps.add("com.manageengine.mdm.iosagent");
            final RestrictionsPayload restrictionsPayload = new RestrictionsPayload(1, "MDM", "com.mdm.mobiledevice.restrictions", "Restriction Policy");
            restrictionsPayload.setAllowAppRemoval(false);
            restrictionsPayload.setAllowAccountModification(false);
            restrictionsPayload.setAllowVpnCreation(false);
            restrictionsPayload.setAllowEraseContentAndSettings(false);
            restrictionsPayload.setAllowEnablingRestriction(false);
            restrictionsPayload.setWhitelistedAppBundleIDs(whitelistedApps);
            return restrictionsPayload;
        }
        catch (final Exception e) {
            DO2RestrictionsPolicyPayload.logger.log(Level.SEVERE, "Exception in Default restriction payload creation", e);
            return null;
        }
    }
    
    public IOSPayload createPasscodeRestrictionPayload(final boolean passcodeRestriction) {
        RestrictionsPayload payload = null;
        try {
            payload = new RestrictionsPayload(1, "MDM", "com.mdm.mobiledevice.passcoderestrictions", "Passcode Restriction Policy");
            payload.setAllowPasscodeModification(!passcodeRestriction);
            return payload;
        }
        catch (final Exception e) {
            DO2RestrictionsPolicyPayload.logger.log(Level.SEVERE, "Exception in creating passcode restriction", e);
            return null;
        }
    }
    
    public IOSPayload createOSUpdateRestrictionPayload(final DataObject dataObject) {
        RestrictionsPayload payload = null;
        try {
            final Iterator iterator = dataObject.getRows("OSUpdatePolicy");
            while (iterator.hasNext()) {
                final Row row = iterator.next();
                payload = new RestrictionsPayload(1, "MDM", "com.mdm.mobiledevice.osupadterestriction", "OS Update Restriction Policy");
                final Integer deferDays = (Integer)row.get("DEFER_DAYS");
                if (deferDays != null && deferDays > 0) {
                    payload.setOSUpdateDelay(deferDays);
                }
                else {
                    payload.setOSUpdateDelay(1);
                }
                payload.setOSUpdateRestrict(true);
            }
            return payload;
        }
        catch (final Exception ex) {
            DO2RestrictionsPolicyPayload.logger.log(Level.SEVERE, "Exception while creating OSUpdate restriction", ex);
            return null;
        }
    }
    
    public IOSPayload createRestrictionFromJSON(final JSONObject restrictionObject, final String payloadIdentifier, final String payloadDisplayName) {
        final RestrictionsPayload restrictionsPayload = new RestrictionsPayload(1, "MDM", payloadIdentifier, payloadDisplayName);
        try {
            if (restrictionObject != null && restrictionObject.length() != 0) {
                final Iterator iterator = restrictionObject.keys();
                while (iterator.hasNext()) {
                    final String restrictionKey = iterator.next();
                    final String apiKey = DO2RestrictionsPolicyPayload.restrictionMapping.get(restrictionKey);
                    final Object restObject = restrictionObject.get(restrictionKey);
                    if (restObject instanceof Boolean) {
                        restrictionsPayload.setProfileWiseRestrictionCommand(apiKey, (boolean)restObject);
                    }
                    else if (restObject instanceof Integer) {
                        restrictionsPayload.setProfileWiseRestrictionCommand(apiKey, (Integer)restObject);
                    }
                    else {
                        if (!(restObject instanceof String)) {
                            continue;
                        }
                        restrictionsPayload.setProfileWiseRestrictionCommand(apiKey, (String)restObject);
                    }
                }
            }
        }
        catch (final JSONException e) {
            DO2RestrictionsPolicyPayload.logger.log(Level.SEVERE, "Exception while processing json for restriction", (Throwable)e);
        }
        return restrictionsPayload;
    }
    
    static {
        DO2RestrictionsPolicyPayload.logger = Logger.getLogger("MDMConfigLogger");
        DO2RestrictionsPolicyPayload.restrictionMapping = new HashMap<String, String>() {
            {
                this.put("ALLOW_MODIFI_DEVICE_NAME", "allowDeviceNameModification");
                this.put("ALLOW_APP_REMOVAL", "allowAppRemoval");
            }
        };
    }
}
