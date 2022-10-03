package com.adventnet.sym.server.mdm.ios.payload;

import com.dd.plist.NSArray;
import com.dd.plist.NSObject;
import com.adventnet.sym.server.mdm.ios.MDMNSArray;
import java.util.List;

public class RestrictionsPayload extends IOSPayload
{
    public RestrictionsPayload(final int payloadVersion, final String payloadOrganization, final String payloadIdentifier, final String payloadDisplayName) {
        super(payloadVersion, "com.apple.applicationaccess", payloadOrganization, payloadIdentifier, payloadDisplayName);
    }
    
    public void setAllowAddingGameCenterFriends(final boolean value) {
        this.getPayloadDict().put("allowAddingGameCenterFriends", (Object)value);
    }
    
    public void setAllowAppInstallation(final boolean value) {
        this.getPayloadDict().put("allowAppInstallation", (Object)value);
    }
    
    public void setAllowAppRemoval(final boolean value) {
        this.getPayloadDict().put("allowAppRemoval", (Object)value);
    }
    
    public void setAllowAssistant(final boolean value) {
        this.getPayloadDict().put("allowAssistant", (Object)value);
    }
    
    public void setAllowAssistantWhileLocked(final boolean value) {
        this.getPayloadDict().put("allowAssistantWhileLocked", (Object)value);
    }
    
    public void setForceAssistantProfanityFilter(final boolean value) {
        this.getPayloadDict().put("forceAssistantProfanityFilter", (Object)value);
    }
    
    public void setAllowPassbookWhileLocked(final boolean value) {
        this.getPayloadDict().put("allowPassbookWhileLocked", (Object)value);
    }
    
    public void setAllowCamera(final boolean value) {
        this.getPayloadDict().put("allowCamera", (Object)value);
    }
    
    public void setAllowCloudBackup(final boolean value) {
        this.getPayloadDict().put("allowCloudBackup", (Object)value);
    }
    
    public void setAllowCloudDocumentSync(final boolean value) {
        this.getPayloadDict().put("allowCloudDocumentSync", (Object)value);
    }
    
    public void setAllowDiagnosticSubmission(final boolean value) {
        this.getPayloadDict().put("allowDiagnosticSubmission", (Object)value);
    }
    
    public void setAllowExplicitContent(final boolean value) {
        this.getPayloadDict().put("allowExplicitContent", (Object)value);
    }
    
    public void setAllowGlobalBackgroundFetchWhenRoaming(final boolean value) {
        this.getPayloadDict().put("allowGlobalBackgroundFetchWhenRoaming", (Object)value);
    }
    
    public void setAllowInAppPurchases(final boolean value) {
        this.getPayloadDict().put("allowInAppPurchases", (Object)value);
    }
    
    public void setAllowGameCenter(final boolean value) {
        this.getPayloadDict().put("allowGameCenter", (Object)value);
    }
    
    public void setAllowMultiplayerGaming(final boolean value) {
        this.getPayloadDict().put("allowMultiplayerGaming", (Object)value);
    }
    
    public void setAllowPhotoStream(final boolean value) {
        this.getPayloadDict().put("allowPhotoStream", (Object)value);
    }
    
    public void setAllowSharedStream(final boolean value) {
        this.getPayloadDict().put("allowSharedStream", (Object)value);
    }
    
    public void setAllowSafari(final boolean value) {
        this.getPayloadDict().put("allowSafari", (Object)value);
    }
    
    public void setAllowIMessage(final boolean value) {
        this.getPayloadDict().put("allowChat", (Object)value);
    }
    
    public void setAllowScreenShot(final boolean value) {
        this.getPayloadDict().put("allowScreenShot", (Object)value);
    }
    
    public void setAllowUntrustedTLSPrompt(final boolean value) {
        this.getPayloadDict().put("allowUntrustedTLSPrompt", (Object)value);
    }
    
    public void setAllowVideoConferencing(final boolean value) {
        this.getPayloadDict().put("allowVideoConferencing", (Object)value);
    }
    
    public void setAllowVoiceDialing(final boolean value) {
        this.getPayloadDict().put("allowVoiceDialing", (Object)value);
    }
    
    public void setAllowYoutube(final boolean value) {
        this.getPayloadDict().put("allowYouTube", (Object)value);
    }
    
    public void setAllowIBookstore(final boolean value) {
        this.getPayloadDict().put("allowBookstore", (Object)value);
    }
    
    public void setAllowIBookstoreEroticaMedia(final boolean value) {
        this.getPayloadDict().put("allowBookstoreErotica", (Object)value);
    }
    
    public void setAllowiTunes(final boolean value) {
        this.getPayloadDict().put("allowiTunes", (Object)value);
    }
    
    public void setForceEncryptedBackup(final boolean value) {
        this.getPayloadDict().put("forceEncryptedBackup", (Object)value);
    }
    
    public void setAllowProfileInstallation(final boolean value) {
        this.getPayloadDict().put("allowUIConfigurationProfileInstallation", (Object)value);
    }
    
    public void setForceITunesStorePasswordEntry(final boolean value) {
        this.getPayloadDict().put("forceITunesStorePasswordEntry", (Object)value);
    }
    
    public void setSafariAcceptCookies(final int value) {
        this.getPayloadDict().put("safariAcceptCookies", (Object)value);
    }
    
    public void setSafariAllowAutoFill(final boolean value) {
        this.getPayloadDict().put("safariAllowAutoFill", (Object)value);
    }
    
    public void setSafariAllowJavaScript(final boolean value) {
        this.getPayloadDict().put("safariAllowJavaScript", (Object)value);
    }
    
    public void setSafariAllowPopups(final boolean value) {
        this.getPayloadDict().put("safariAllowPopups", (Object)value);
    }
    
    public void setSafariForceFraudWarning(final boolean value) {
        this.getPayloadDict().put("safariForceFraudWarning", (Object)value);
    }
    
    public void setCountrycode(final String value) {
        this.getPayloadDict().put("ratingRegion", (Object)value);
    }
    
    public void setMovieRatings(final Integer value) {
        this.getPayloadDict().put("ratingMovies", (Object)value);
    }
    
    public void setTvShowRatings(final Integer value) {
        this.getPayloadDict().put("ratingTVShows", (Object)value);
    }
    
    public void setAppsRatings(final Integer value) {
        this.getPayloadDict().put("ratingApps", (Object)value);
    }
    
    public void setAllowAirDrop(final boolean value) {
        this.getPayloadDict().put("allowAirDrop", (Object)value);
    }
    
    public void setAllowUserContentInSiri(final boolean value) {
        this.getPayloadDict().put("allowAssistantUserGeneratedContent", (Object)value);
    }
    
    public void setAllowTouchID(final boolean value) {
        this.getPayloadDict().put("allowFingerprintForUnlock", (Object)value);
    }
    
    public void setShowControlCenter(final boolean value) {
        this.getPayloadDict().put("allowLockScreenControlCenter", (Object)value);
    }
    
    public void setShowNotificationCenter(final boolean value) {
        this.getPayloadDict().put("allowLockScreenNotificationsView", (Object)value);
    }
    
    public void setShowTodayView(final boolean value) {
        this.getPayloadDict().put("allowLockScreenTodayView", (Object)value);
    }
    
    public void setAllowAutomaticUpdatesForCerti(final boolean value) {
        this.getPayloadDict().put("allowOTAPKIUpdates", (Object)value);
    }
    
    public void setForceLimitedAdTracking(final boolean value) {
        this.getPayloadDict().put("forceLimitAdTracking", (Object)value);
    }
    
    public void setAllowAccountModification(final boolean value) {
        this.getPayloadDict().put("allowAccountModification", (Object)value);
    }
    
    public void setAllowFindMyFriendsMod(final boolean value) {
        this.getPayloadDict().put("allowFindMyFriendsModification", (Object)value);
    }
    
    public void setAllowHostPairing(final boolean value) {
        this.getPayloadDict().put("allowHostPairing", (Object)value);
    }
    
    public void setAllowAppCellData(final boolean value) {
        this.getPayloadDict().put("allowAppCellularDataModification", (Object)value);
    }
    
    public void setAllowOpenDocInUnmanaged(final boolean value) {
        this.getPayloadDict().put("allowOpenFromManagedToUnmanaged", (Object)value);
    }
    
    public void setAllowOpenDocInManaged(final boolean value) {
        this.getPayloadDict().put("allowOpenFromUnmanagedToManaged", (Object)value);
    }
    
    public void setAllowManagedAppCloudSync(final boolean value) {
        this.getPayloadDict().put("allowManagedAppsCloudSync", (Object)value);
    }
    
    public void setAllowEnterpriseBookBackup(final boolean value) {
        this.getPayloadDict().put("allowEnterpriseBookBackup", (Object)value);
    }
    
    public void setAllowEnterpriseBookMetadataSync(final boolean value) {
        this.getPayloadDict().put("allowEnterpriseBookMetadataSync", (Object)value);
    }
    
    public void setAllowActivityContinuation(final boolean value) {
        this.getPayloadDict().put("allowActivityContinuation", (Object)value);
    }
    
    public void setAllowEraseContentAndSettings(final boolean value) {
        this.getPayloadDict().put("allowEraseContentAndSettings", (Object)value);
    }
    
    public void setAllowEnablingRestriction(final boolean value) {
        this.getPayloadDict().put("allowEnablingRestrictions", (Object)value);
    }
    
    public void setAllowSpotlightInternetResults(final boolean value) {
        this.getPayloadDict().put("allowSpotlightInternetResults", (Object)value);
    }
    
    public void setAllowCloudKeychainSync(final boolean value) {
        this.getPayloadDict().put("allowCloudKeychainSync", (Object)value);
    }
    
    public void setForceAirPlayOutgoingRequestPairingPassword(final boolean value) {
        this.getPayloadDict().put("forceAirPlayOutgoingRequestsPairingPassword", (Object)value);
    }
    
    public void setForceAirPlayIncomingRequestPairingPassword(final boolean value) {
        this.getPayloadDict().put("forceAirPlayIncomingRequestsPairingPassword", (Object)value);
    }
    
    public void setAllowPodcasts(final boolean value) {
        this.getPayloadDict().put("allowPodcasts", (Object)value);
    }
    
    public void setAllowDefinitionLookup(final boolean value) {
        this.getPayloadDict().put("allowDefinitionLookup", (Object)value);
    }
    
    public void setAllowPredictiveKeyboard(final boolean value) {
        this.getPayloadDict().put("allowPredictiveKeyboard", (Object)value);
    }
    
    public void setAllowAutoCorrection(final boolean value) {
        this.getPayloadDict().put("allowAutoCorrection", (Object)value);
    }
    
    public void setAllowSpellCheck(final boolean value) {
        this.getPayloadDict().put("allowSpellCheck", (Object)value);
    }
    
    public void setForceWatchWristDetection(final boolean value) {
        this.getPayloadDict().put("forceWatchWristDetection", (Object)value);
    }
    
    public void setAllowModifyTouchId(final boolean value) {
        this.getPayloadDict().put("allowFingerprintModification", (Object)value);
    }
    
    public void setAllowCloudPhotoLibrary(final boolean value) {
        this.getPayloadDict().put("allowCloudPhotoLibrary", (Object)value);
    }
    
    public void setForceAirDropUnmanaged(final boolean value) {
        this.getPayloadDict().put("forceAirDropUnmanaged", (Object)value);
    }
    
    public void setAllowKeyboardShortcuts(final boolean value) {
        this.getPayloadDict().put("allowKeyboardShortcuts", (Object)value);
    }
    
    public void setAllowPairedWatch(final boolean value) {
        this.getPayloadDict().put("allowPairedWatch", (Object)value);
    }
    
    public void setAllowPasscodeModification(final boolean value) {
        this.getPayloadDict().put("allowPasscodeModification", (Object)value);
    }
    
    public void setAllowDeviceNameModification(final boolean value) {
        this.getPayloadDict().put("allowDeviceNameModification", (Object)value);
    }
    
    public void setAllowWallpaperModification(final boolean value) {
        this.getPayloadDict().put("allowWallpaperModification", (Object)value);
    }
    
    public void setAllowAutomaticAppDownloads(final boolean value) {
        this.getPayloadDict().put("allowAutomaticAppDownloads", (Object)value);
    }
    
    public void setAllowEnterpriseAppTrust(final boolean value) {
        this.getPayloadDict().put("allowEnterpriseAppTrust", (Object)value);
    }
    
    public void setAllowNews(final boolean value) {
        this.getPayloadDict().put("allowNews", (Object)value);
    }
    
    public void setAllowUIAppInstallation(final boolean value) {
        this.getPayloadDict().put("allowUIAppInstallation", (Object)value);
    }
    
    public void setAllowDictation(final boolean value) {
        this.getPayloadDict().put("allowDictation", (Object)value);
    }
    
    public void setAllowMusicService(final boolean value) {
        this.getPayloadDict().put("allowMusicService", (Object)value);
    }
    
    public void setAllowRadioService(final boolean value) {
        this.getPayloadDict().put("allowRadioService", (Object)value);
    }
    
    public void setAllowDiagnosticSubmissionModification(final boolean value) {
        this.getPayloadDict().put("allowDiagnosticSubmissionModification", (Object)value);
    }
    
    public void setAllowBluetoothModification(final boolean value) {
        this.getPayloadDict().put("allowBluetoothModification", (Object)value);
    }
    
    public void setAllowHotspotRestriction(final boolean value) {
        this.getPayloadDict().put("allowPersonalHotspotModification", (Object)value);
    }
    
    public void setAllowEsimModification(final boolean value) {
        this.getPayloadDict().put("allowESIMModification", (Object)value);
    }
    
    public void setAllowSiriLogging(final boolean value) {
        this.getPayloadDict().put("allowSiriServerLogging", (Object)value);
    }
    
    public void setForceWiFiWhitelisting(final boolean value) {
        this.getPayloadDict().put("forceWiFiWhitelisting", (Object)value);
        this.getPayloadDict().put("forceWiFiToAllowedNetworksOnly", (Object)value);
    }
    
    public void setWhitelistedAppBundleIDs(final List bundleIds) {
        this.getPayloadDict().put("whitelistedAppBundleIDs", (NSObject)MDMNSArray.getNSArrayFromList(bundleIds));
        this.getPayloadDict().put("allowListedAppBundleIDs ", (NSObject)MDMNSArray.getNSArrayFromList(bundleIds));
    }
    
    public void setAutonomousSingleAppModePermittedAppIDs(final List bundleIds) {
        this.getPayloadDict().put("autonomousSingleAppModePermittedAppIDs", (NSObject)MDMNSArray.getNSArrayFromList(bundleIds));
    }
    
    public void setAllowAirPrint(final boolean value) {
        this.getPayloadDict().put("allowAirPrint", (Object)value);
    }
    
    public void setAllowAirPlayIncomingRequests(final boolean value) {
        this.getPayloadDict().put("allowAirPlayIncomingRequests", (Object)value);
    }
    
    public void setAllowDeviceSleep(final boolean value) {
        this.getPayloadDict().put("allowDeviceSleep", (Object)value);
    }
    
    public void setAllowRemoteAppPairing(final boolean value) {
        this.getPayloadDict().put("allowRemoteAppPairing", (Object)value);
    }
    
    public void setAllowVpnCreation(final boolean value) {
        this.getPayloadDict().put("allowVPNCreation", (Object)value);
    }
    
    public void setAllowAirPrintCredential(final boolean value) {
        this.getPayloadDict().put("allowAirPrintCredentialsStorage", (Object)value);
    }
    
    public void setForceAirprintTLS(final boolean value) {
        this.getPayloadDict().put("forceAirPrintTrustedTLSRequirement", (Object)value);
    }
    
    public void setAllowAirprintDiscovery(final boolean value) {
        this.getPayloadDict().put("allowAirPrintiBeaconDiscovery", (Object)value);
    }
    
    public void setForceClassroomAutomaticallyJoinClasses(final boolean value) {
        this.getPayloadDict().put("forceClassroomAutomaticallyJoinClasses", (Object)value);
    }
    
    public void setForceClassroomUnpromptedAppAndDeviceLock(final boolean value) {
        this.getPayloadDict().put("forceClassroomUnpromptedAppAndDeviceLock", (Object)value);
    }
    
    public void setForceClassroomUnpromptedScreenObservation(final boolean value) {
        this.getPayloadDict().put("forceClassroomUnpromptedScreenObservation", (Object)value);
    }
    
    public void setAllowRemoteScreenObservation(final boolean value) {
        this.getPayloadDict().put("allowRemoteScreenObservation", (Object)value);
    }
    
    public void setAllowAppClip(final boolean value) {
        this.getPayloadDict().put("allowAppClips", (Object)value);
    }
    
    public void setAllowApplePersonalizedAdvertising(final boolean value) {
        this.getPayloadDict().put("allowApplePersonalizedAdvertising", (Object)value);
    }
    
    public void setAllowCellularPlanModification(final boolean value) {
        this.getPayloadDict().put("allowCellularPlanModification", (Object)value);
    }
    
    public void setAllowFilesNetworkDriveAccess(final boolean value) {
        this.getPayloadDict().put("allowFilesNetworkDriveAccess", (Object)value);
    }
    
    public void setRequestToLeaveClassRoom(final boolean value) {
        this.getPayloadDict().put("forceClassroomRequestPermissionToLeaveClasses", (Object)value);
    }
    
    public void setProximityForNewDevice(final boolean value) {
        this.getPayloadDict().put("allowProximitySetupToNewDevice", (Object)value);
    }
    
    public void setForceAuthenticationOnAutofill(final boolean value) {
        this.getPayloadDict().put("forceAuthenticationBeforeAutoFill", (Object)value);
    }
    
    public void setForceDateAndTime(final boolean value) {
        this.getPayloadDict().put("forceAutomaticDateAndTime", (Object)value);
    }
    
    public void setPreventAutofillPassword(final boolean value) {
        this.getPayloadDict().put("allowPasswordAutoFill", (Object)value);
    }
    
    public void setAllowPasswordProximity(final boolean value) {
        this.getPayloadDict().put("allowPasswordProximityRequests", (Object)value);
    }
    
    public void setAllowPasswordSharing(final boolean value) {
        this.getPayloadDict().put("allowPasswordSharing", (Object)value);
    }
    
    public void setAllowManagedWriteUnmanagedContact(final boolean value) {
        this.getPayloadDict().put("allowManagedToWriteUnmanagedContacts", (Object)value);
    }
    
    public void setAllowUnmanagedReadManagedContact(final boolean value) {
        this.getPayloadDict().put("allowUnmanagedToReadManagedContacts", (Object)value);
    }
    
    public void setUSBRestrictedMode(final boolean value) {
        this.getPayloadDict().put("allowUSBRestrictedMode", (Object)value);
    }
    
    public void setOSUpdateDelay(final Integer value) {
        this.getPayloadDict().put("enforcedSoftwareUpdateDelay", (Object)value);
    }
    
    public void setOSUpdateRestrict(final boolean value) {
        this.getPayloadDict().put("forceDelayedSoftwareUpdates", (Object)value);
    }
    
    public void setProfileWiseRestrictionCommand(final String restrictionType, final boolean value) {
        this.getPayloadDict().put(restrictionType, (Object)value);
    }
    
    public void setBlacklistedAppBundleIDs(final List bundleIds) {
        final NSArray bundleIDsArray = MDMNSArray.getNSArrayFromList(bundleIds);
        this.getPayloadDict().put("blacklistedAppBundleIDs", (NSObject)bundleIDsArray);
        this.getPayloadDict().put("blockedAppBundleIDs", (NSObject)bundleIDsArray);
    }
    
    public void setProfileWiseRestrictionCommand(final String restrictionType, final Integer value) {
        this.getPayloadDict().put(restrictionType, (Object)value);
    }
    
    public void setProfileWiseRestrictionCommand(final String restrictionType, final String value) {
        this.getPayloadDict().put(restrictionType, (Object)value);
    }
    
    public void setAllowContentCaching(final boolean value) {
        this.getPayloadDict().put("allowContentCaching", (Object)value);
    }
    
    public void setAllowiTuneFileSharing(final boolean value) {
        this.getPayloadDict().put("allowiTunesFileSharing", (Object)value);
    }
    
    public void setAllowAutoUnlock(final boolean value) {
        this.getPayloadDict().put("allowAutoUnlock", (Object)value);
    }
    
    public void setAllowCloudDesktopDocument(final boolean value) {
        this.getPayloadDict().put("allowCloudDesktopAndDocuments", (Object)value);
    }
    
    public void setAllowCloudBookmark(final boolean value) {
        this.getPayloadDict().put("allowCloudBookmarks", (Object)value);
    }
    
    public void setAllowCloudMail(final boolean value) {
        this.getPayloadDict().put("allowCloudMail", (Object)value);
    }
    
    public void setAllowCloudCalender(final boolean value) {
        this.getPayloadDict().put("allowCloudCalendar", (Object)value);
    }
    
    public void setAllowCloudReminder(final boolean value) {
        this.getPayloadDict().put("allowCloudReminders", (Object)value);
    }
    
    public void setAllowCloudAddressBook(final boolean value) {
        this.getPayloadDict().put("allowCloudAddressBook", (Object)value);
    }
    
    public void setAllowCloudNotes(final boolean value) {
        this.getPayloadDict().put("allowCloudNotes", (Object)value);
    }
    
    public void setAllowContinuousPath(final boolean value) {
        this.getPayloadDict().put("allowContinuousPathKeyboard", (Object)value);
    }
    
    public void setFindMyFriend(final boolean value) {
        this.getPayloadDict().put("allowFindMyFriends", (Object)value);
    }
    
    public void setFindMyDevice(final boolean value) {
        this.getPayloadDict().put("allowFindMyDevice", (Object)value);
    }
    
    public void setWifiPowerModification(final boolean value) {
        this.getPayloadDict().put("forceWiFiPowerOn", (Object)value);
    }
    
    public void setUSBFileDrive(final boolean value) {
        this.getPayloadDict().put("allowFilesUSBDriveAccess", (Object)value);
    }
    
    public void setAllowSystemApp(final boolean value) {
        this.getPayloadDict().put("allowSystemAppRemoval", (Object)value);
    }
    
    public void setAllowNotificationModification(final boolean value) {
        this.getPayloadDict().put("allowNotificationsModification", (Object)value);
    }
    
    public void setSharedIpadGuestAccount(final boolean value) {
        this.getPayloadDict().put("allowSharedDeviceTemporarySession", (Object)value);
    }
    
    public void setAllowNfc(final boolean value) {
        this.getPayloadDict().put("allowNFC", (Object)value);
    }
    
    public void setAllowUnpairedExternalBootToRecovery(final boolean value) {
        this.getPayloadDict().put("allowUnpairedExternalBootToRecovery", (Object)value);
    }
    
    public void setForceOnDeviceOnlyDictation(final boolean value) {
        this.getPayloadDict().put("forceOnDeviceOnlyDictation", (Object)value);
    }
    
    public void setForceOnDeviceOnlyTranslation(final boolean value) {
        this.getPayloadDict().put("forceOnDeviceOnlyTranslation", (Object)value);
    }
    
    public void setRequireManagedPasteBoard(final boolean value) {
        this.getPayloadDict().put("requireManagedPasteboard", (Object)value);
    }
    
    public void setAllowCloudPrivateRelay(final boolean value) {
        this.getPayloadDict().put("allowCloudPrivateRelay", (Object)value);
    }
    
    public void setAllowMailPrivacyProtection(final boolean value) {
        this.getPayloadDict().put("allowMailPrivacyProtection", (Object)value);
    }
    
    public void setAllowUniversalControl(final boolean value) {
        this.getPayloadDict().put("allowUniversalControl", (Object)value);
    }
    
    public void setAllowAutomaticScreenSaver(final boolean value) {
        this.getPayloadDict().put("allowAutomaticScreenSaver", (Object)value);
    }
    
    public void setAllowRapidSecurityResponseInstallation(final boolean value) {
        this.getPayloadDict().put("allowRapidSecurityResponseInstallation", (Object)value);
    }
    
    public void setAllowRapidSecurityResponseRemoval(final boolean value) {
        this.getPayloadDict().put("allowRapidSecurityResponseRemoval", (Object)value);
    }
}
