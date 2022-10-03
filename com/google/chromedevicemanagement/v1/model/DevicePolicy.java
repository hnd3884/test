package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.Data;
import com.google.api.client.util.GenericData;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class DevicePolicy extends GenericJson
{
    @Key
    private AlertContactInfo alertContactInfo;
    @Key
    private List<ChromeApp> applicationSettings;
    @Key
    private AutoLaunchedAppSettings autoLaunchedAppSettings;
    @Key
    private AutoUpdateSettings autoUpdateSettings;
    @Key
    private DeviceAttestationEnabled deviceAttestationEnabled;
    @Key
    private DeviceDisabledMessage deviceDisabledMessage;
    @Key
    private DeviceHeartbeatSettings deviceHeartbeatSettings;
    @Key
    private DeviceLogUploadSettings deviceLogUploadSettings;
    @Key
    private DeviceLoginScreenAutoSelectCertificateForUrls deviceLoginScreenAutoSelectCertificateForUrls;
    @Key
    private DeviceReporting deviceReporting;
    @Key
    private DeviceStatusAlertDelivery deviceStatusAlertDelivery;
    @Key
    private DeviceUnaffiliatedCrostiniAllowed deviceUnaffiliatedCrostiniAllowed;
    @Key
    private DeviceVerifiedAccessControl deviceVerifiedAccessControl;
    @Key
    private DeviceVerifiedModeRequired deviceVerifiedModeRequired;
    @Key
    private DeviceWallpaperImage deviceWallpaperImage;
    @Key
    private EphemeralUsersEnabled ephemeralUsersEnabled;
    @Key
    private ForcedReenrollment forcedReenrollment;
    @Key
    private GuestModeDisabled guestModeDisabled;
    @Key
    private LoginScreenDomainAutoComplete loginScreenDomainAutoComplete;
    @Key
    private OpenNetworkConfig openNetworkConfig;
    @Key
    private PublicSession publicSession;
    @Key
    private RedirectToSamlIdpAllowed redirectToSamlIdpAllowed;
    @Key
    private ReleaseChannel releaseChannel;
    @Key
    private SamlSettings samlSettings;
    @Key
    private ShowUserNamesOnSignin showUserNames;
    @Key
    private SystemTimezoneSettings systemTimezoneSettings;
    @Key
    private UserAllowlist userAllowlist;
    @Key
    private UserWhitelist userWhitelist;
    @Key
    private VirtualMachinesAllowed virtualMachinesAllowed;
    
    public AlertContactInfo getAlertContactInfo() {
        return this.alertContactInfo;
    }
    
    public DevicePolicy setAlertContactInfo(final AlertContactInfo alertContactInfo) {
        this.alertContactInfo = alertContactInfo;
        return this;
    }
    
    public List<ChromeApp> getApplicationSettings() {
        return this.applicationSettings;
    }
    
    public DevicePolicy setApplicationSettings(final List<ChromeApp> applicationSettings) {
        this.applicationSettings = applicationSettings;
        return this;
    }
    
    public AutoLaunchedAppSettings getAutoLaunchedAppSettings() {
        return this.autoLaunchedAppSettings;
    }
    
    public DevicePolicy setAutoLaunchedAppSettings(final AutoLaunchedAppSettings autoLaunchedAppSettings) {
        this.autoLaunchedAppSettings = autoLaunchedAppSettings;
        return this;
    }
    
    public AutoUpdateSettings getAutoUpdateSettings() {
        return this.autoUpdateSettings;
    }
    
    public DevicePolicy setAutoUpdateSettings(final AutoUpdateSettings autoUpdateSettings) {
        this.autoUpdateSettings = autoUpdateSettings;
        return this;
    }
    
    public DeviceAttestationEnabled getDeviceAttestationEnabled() {
        return this.deviceAttestationEnabled;
    }
    
    public DevicePolicy setDeviceAttestationEnabled(final DeviceAttestationEnabled deviceAttestationEnabled) {
        this.deviceAttestationEnabled = deviceAttestationEnabled;
        return this;
    }
    
    public DeviceDisabledMessage getDeviceDisabledMessage() {
        return this.deviceDisabledMessage;
    }
    
    public DevicePolicy setDeviceDisabledMessage(final DeviceDisabledMessage deviceDisabledMessage) {
        this.deviceDisabledMessage = deviceDisabledMessage;
        return this;
    }
    
    public DeviceHeartbeatSettings getDeviceHeartbeatSettings() {
        return this.deviceHeartbeatSettings;
    }
    
    public DevicePolicy setDeviceHeartbeatSettings(final DeviceHeartbeatSettings deviceHeartbeatSettings) {
        this.deviceHeartbeatSettings = deviceHeartbeatSettings;
        return this;
    }
    
    public DeviceLogUploadSettings getDeviceLogUploadSettings() {
        return this.deviceLogUploadSettings;
    }
    
    public DevicePolicy setDeviceLogUploadSettings(final DeviceLogUploadSettings deviceLogUploadSettings) {
        this.deviceLogUploadSettings = deviceLogUploadSettings;
        return this;
    }
    
    public DeviceLoginScreenAutoSelectCertificateForUrls getDeviceLoginScreenAutoSelectCertificateForUrls() {
        return this.deviceLoginScreenAutoSelectCertificateForUrls;
    }
    
    public DevicePolicy setDeviceLoginScreenAutoSelectCertificateForUrls(final DeviceLoginScreenAutoSelectCertificateForUrls deviceLoginScreenAutoSelectCertificateForUrls) {
        this.deviceLoginScreenAutoSelectCertificateForUrls = deviceLoginScreenAutoSelectCertificateForUrls;
        return this;
    }
    
    public DeviceReporting getDeviceReporting() {
        return this.deviceReporting;
    }
    
    public DevicePolicy setDeviceReporting(final DeviceReporting deviceReporting) {
        this.deviceReporting = deviceReporting;
        return this;
    }
    
    public DeviceStatusAlertDelivery getDeviceStatusAlertDelivery() {
        return this.deviceStatusAlertDelivery;
    }
    
    public DevicePolicy setDeviceStatusAlertDelivery(final DeviceStatusAlertDelivery deviceStatusAlertDelivery) {
        this.deviceStatusAlertDelivery = deviceStatusAlertDelivery;
        return this;
    }
    
    public DeviceUnaffiliatedCrostiniAllowed getDeviceUnaffiliatedCrostiniAllowed() {
        return this.deviceUnaffiliatedCrostiniAllowed;
    }
    
    public DevicePolicy setDeviceUnaffiliatedCrostiniAllowed(final DeviceUnaffiliatedCrostiniAllowed deviceUnaffiliatedCrostiniAllowed) {
        this.deviceUnaffiliatedCrostiniAllowed = deviceUnaffiliatedCrostiniAllowed;
        return this;
    }
    
    public DeviceVerifiedAccessControl getDeviceVerifiedAccessControl() {
        return this.deviceVerifiedAccessControl;
    }
    
    public DevicePolicy setDeviceVerifiedAccessControl(final DeviceVerifiedAccessControl deviceVerifiedAccessControl) {
        this.deviceVerifiedAccessControl = deviceVerifiedAccessControl;
        return this;
    }
    
    public DeviceVerifiedModeRequired getDeviceVerifiedModeRequired() {
        return this.deviceVerifiedModeRequired;
    }
    
    public DevicePolicy setDeviceVerifiedModeRequired(final DeviceVerifiedModeRequired deviceVerifiedModeRequired) {
        this.deviceVerifiedModeRequired = deviceVerifiedModeRequired;
        return this;
    }
    
    public DeviceWallpaperImage getDeviceWallpaperImage() {
        return this.deviceWallpaperImage;
    }
    
    public DevicePolicy setDeviceWallpaperImage(final DeviceWallpaperImage deviceWallpaperImage) {
        this.deviceWallpaperImage = deviceWallpaperImage;
        return this;
    }
    
    public EphemeralUsersEnabled getEphemeralUsersEnabled() {
        return this.ephemeralUsersEnabled;
    }
    
    public DevicePolicy setEphemeralUsersEnabled(final EphemeralUsersEnabled ephemeralUsersEnabled) {
        this.ephemeralUsersEnabled = ephemeralUsersEnabled;
        return this;
    }
    
    public ForcedReenrollment getForcedReenrollment() {
        return this.forcedReenrollment;
    }
    
    public DevicePolicy setForcedReenrollment(final ForcedReenrollment forcedReenrollment) {
        this.forcedReenrollment = forcedReenrollment;
        return this;
    }
    
    public GuestModeDisabled getGuestModeDisabled() {
        return this.guestModeDisabled;
    }
    
    public DevicePolicy setGuestModeDisabled(final GuestModeDisabled guestModeDisabled) {
        this.guestModeDisabled = guestModeDisabled;
        return this;
    }
    
    public LoginScreenDomainAutoComplete getLoginScreenDomainAutoComplete() {
        return this.loginScreenDomainAutoComplete;
    }
    
    public DevicePolicy setLoginScreenDomainAutoComplete(final LoginScreenDomainAutoComplete loginScreenDomainAutoComplete) {
        this.loginScreenDomainAutoComplete = loginScreenDomainAutoComplete;
        return this;
    }
    
    public OpenNetworkConfig getOpenNetworkConfig() {
        return this.openNetworkConfig;
    }
    
    public DevicePolicy setOpenNetworkConfig(final OpenNetworkConfig openNetworkConfig) {
        this.openNetworkConfig = openNetworkConfig;
        return this;
    }
    
    public PublicSession getPublicSession() {
        return this.publicSession;
    }
    
    public DevicePolicy setPublicSession(final PublicSession publicSession) {
        this.publicSession = publicSession;
        return this;
    }
    
    public RedirectToSamlIdpAllowed getRedirectToSamlIdpAllowed() {
        return this.redirectToSamlIdpAllowed;
    }
    
    public DevicePolicy setRedirectToSamlIdpAllowed(final RedirectToSamlIdpAllowed redirectToSamlIdpAllowed) {
        this.redirectToSamlIdpAllowed = redirectToSamlIdpAllowed;
        return this;
    }
    
    public ReleaseChannel getReleaseChannel() {
        return this.releaseChannel;
    }
    
    public DevicePolicy setReleaseChannel(final ReleaseChannel releaseChannel) {
        this.releaseChannel = releaseChannel;
        return this;
    }
    
    public SamlSettings getSamlSettings() {
        return this.samlSettings;
    }
    
    public DevicePolicy setSamlSettings(final SamlSettings samlSettings) {
        this.samlSettings = samlSettings;
        return this;
    }
    
    public ShowUserNamesOnSignin getShowUserNames() {
        return this.showUserNames;
    }
    
    public DevicePolicy setShowUserNames(final ShowUserNamesOnSignin showUserNames) {
        this.showUserNames = showUserNames;
        return this;
    }
    
    public SystemTimezoneSettings getSystemTimezoneSettings() {
        return this.systemTimezoneSettings;
    }
    
    public DevicePolicy setSystemTimezoneSettings(final SystemTimezoneSettings systemTimezoneSettings) {
        this.systemTimezoneSettings = systemTimezoneSettings;
        return this;
    }
    
    public UserAllowlist getUserAllowlist() {
        return this.userAllowlist;
    }
    
    public DevicePolicy setUserAllowlist(final UserAllowlist userAllowlist) {
        this.userAllowlist = userAllowlist;
        return this;
    }
    
    public UserWhitelist getUserWhitelist() {
        return this.userWhitelist;
    }
    
    public DevicePolicy setUserWhitelist(final UserWhitelist userWhitelist) {
        this.userWhitelist = userWhitelist;
        return this;
    }
    
    public VirtualMachinesAllowed getVirtualMachinesAllowed() {
        return this.virtualMachinesAllowed;
    }
    
    public DevicePolicy setVirtualMachinesAllowed(final VirtualMachinesAllowed virtualMachinesAllowed) {
        this.virtualMachinesAllowed = virtualMachinesAllowed;
        return this;
    }
    
    public DevicePolicy set(final String s, final Object o) {
        return (DevicePolicy)super.set(s, o);
    }
    
    public DevicePolicy clone() {
        return (DevicePolicy)super.clone();
    }
    
    static {
        Data.nullOf((Class)ChromeApp.class);
    }
}
