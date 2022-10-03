package com.google.chromedevicemanagement.v1.model;

import com.google.api.client.util.GenericData;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class UserPolicy extends GenericJson
{
    @Key
    private AllowToUpdateDeviceAttribute allowToUpdateDeviceAttribute;
    @Key
    private AndroidApplicationsSettings androidApplicationsSettings;
    @Key
    private ArcCertificatesSync arcCertificatesSync;
    @Key
    private ArcEnabled arcEnabled;
    @Key
    private AutoSelectCertificateForUrls autoSelectCertificateForUrls;
    @Key
    private BlockThirdPartyCookies blockThirdPartyCookies;
    @Key
    private BookmarksBarEnabled bookmarksBarEnabled;
    @Key
    private ChromeApplicationsSettings chromeApplicationsSettings;
    @Key
    private CookiesAllowedForUrls cookiesAllowedForUrls;
    @Key
    private CookiesBlockedForUrls cookiesBlockedForUrls;
    @Key
    private CookiesSessionOnlyForUrls cookiesSessionOnlyForUrls;
    @Key
    private CrostiniAllowed crostiniAllowed;
    @Key
    private DefaultCookiesSetting defaultCookiesSettings;
    @Key
    private DeletingBrowserHistoryDisabled deletingBrowserHistoryDisabled;
    @Key
    private DeviceEnrollmentPermissions deviceEnrollmentPermissions;
    @Key
    private DisableSafeBrowsingProceedAnyway disableSafeBrowsingProceedAnyway;
    @Key
    private DisplayName displayName;
    @Key
    private EditBookmarksDisabled editBookmarksDisabled;
    @Key
    private ExtensionInstallSources extensionInstallSources;
    @Key
    private ExternalStorageAccessibility externalStorageAccessibility;
    @Key
    private HomepageSettings homepageSettings;
    @Key
    private IncognitoModeAvailability incognitoModeAvailability;
    @Key
    private ManagedBookmarks managedBookmarks;
    @Key
    private OpenNetworkConfig openNetworkConfig;
    @Key
    private PasswordManagerEnabled passwordManagerEnabled;
    @Key
    private PopupsAllowedForUrls popupsAllowedForUrls;
    @Key
    private PopupsBlockedForUrls popupsBlockedForUrls;
    @Key
    private PopupsDefaultSettings popupsDefaultSettings;
    @Key
    private PowerManagementIdleSettings powerManagementIdleSettings;
    @Key
    private PrintingDisabled printingDisabled;
    @Key
    private QuicDisallowed quicDisallowed;
    @Key
    private RemoteAccessHostClientDomainList remoteAccessHostClientDomainList;
    @Key
    private RestoreOnStartupUrls restoreOnStartupUrls;
    @Key
    private SafeBrowsingEnabled safeBrowsingEnabled;
    @Key
    private SavingBrowserHistoryDisabled savingBrowserHistoryDisabled;
    @Key
    private ScreenLockDisabled screenLockDisabled;
    @Key
    private SessionLengthLimit sessionLengthLimit;
    @Key
    private ShowHomeButton showHomeButton;
    @Key
    private TaskManagerEndProcessDisabled taskManagerEndProcessDisabled;
    @Key
    private TermsOfServiceURL termsOfServiceUrl;
    @Key
    private UrlAllowlist urlAllowlist;
    @Key
    private UrlBlacklist urlBlacklist;
    @Key
    private UrlBlocklist urlBlocklist;
    @Key
    private UrlWhitelist urlWhitelist;
    @Key
    private UserAttestationEnabled userAttestationEnabled;
    @Key
    private UserAvatarImage userAvatarImage;
    @Key
    private UserVerifiedAccessControl userVerifiedAccessControl;
    @Key
    private UserVerifiedModeRequired userVerifiedModeRequired;
    @Key
    private WallpaperImage wallpaperImage;
    @Key
    private WebRtcUdpPortRange webRtcUdpPortRange;
    
    public AllowToUpdateDeviceAttribute getAllowToUpdateDeviceAttribute() {
        return this.allowToUpdateDeviceAttribute;
    }
    
    public UserPolicy setAllowToUpdateDeviceAttribute(final AllowToUpdateDeviceAttribute allowToUpdateDeviceAttribute) {
        this.allowToUpdateDeviceAttribute = allowToUpdateDeviceAttribute;
        return this;
    }
    
    public AndroidApplicationsSettings getAndroidApplicationsSettings() {
        return this.androidApplicationsSettings;
    }
    
    public UserPolicy setAndroidApplicationsSettings(final AndroidApplicationsSettings androidApplicationsSettings) {
        this.androidApplicationsSettings = androidApplicationsSettings;
        return this;
    }
    
    public ArcCertificatesSync getArcCertificatesSync() {
        return this.arcCertificatesSync;
    }
    
    public UserPolicy setArcCertificatesSync(final ArcCertificatesSync arcCertificatesSync) {
        this.arcCertificatesSync = arcCertificatesSync;
        return this;
    }
    
    public ArcEnabled getArcEnabled() {
        return this.arcEnabled;
    }
    
    public UserPolicy setArcEnabled(final ArcEnabled arcEnabled) {
        this.arcEnabled = arcEnabled;
        return this;
    }
    
    public AutoSelectCertificateForUrls getAutoSelectCertificateForUrls() {
        return this.autoSelectCertificateForUrls;
    }
    
    public UserPolicy setAutoSelectCertificateForUrls(final AutoSelectCertificateForUrls autoSelectCertificateForUrls) {
        this.autoSelectCertificateForUrls = autoSelectCertificateForUrls;
        return this;
    }
    
    public BlockThirdPartyCookies getBlockThirdPartyCookies() {
        return this.blockThirdPartyCookies;
    }
    
    public UserPolicy setBlockThirdPartyCookies(final BlockThirdPartyCookies blockThirdPartyCookies) {
        this.blockThirdPartyCookies = blockThirdPartyCookies;
        return this;
    }
    
    public BookmarksBarEnabled getBookmarksBarEnabled() {
        return this.bookmarksBarEnabled;
    }
    
    public UserPolicy setBookmarksBarEnabled(final BookmarksBarEnabled bookmarksBarEnabled) {
        this.bookmarksBarEnabled = bookmarksBarEnabled;
        return this;
    }
    
    public ChromeApplicationsSettings getChromeApplicationsSettings() {
        return this.chromeApplicationsSettings;
    }
    
    public UserPolicy setChromeApplicationsSettings(final ChromeApplicationsSettings chromeApplicationsSettings) {
        this.chromeApplicationsSettings = chromeApplicationsSettings;
        return this;
    }
    
    public CookiesAllowedForUrls getCookiesAllowedForUrls() {
        return this.cookiesAllowedForUrls;
    }
    
    public UserPolicy setCookiesAllowedForUrls(final CookiesAllowedForUrls cookiesAllowedForUrls) {
        this.cookiesAllowedForUrls = cookiesAllowedForUrls;
        return this;
    }
    
    public CookiesBlockedForUrls getCookiesBlockedForUrls() {
        return this.cookiesBlockedForUrls;
    }
    
    public UserPolicy setCookiesBlockedForUrls(final CookiesBlockedForUrls cookiesBlockedForUrls) {
        this.cookiesBlockedForUrls = cookiesBlockedForUrls;
        return this;
    }
    
    public CookiesSessionOnlyForUrls getCookiesSessionOnlyForUrls() {
        return this.cookiesSessionOnlyForUrls;
    }
    
    public UserPolicy setCookiesSessionOnlyForUrls(final CookiesSessionOnlyForUrls cookiesSessionOnlyForUrls) {
        this.cookiesSessionOnlyForUrls = cookiesSessionOnlyForUrls;
        return this;
    }
    
    public CrostiniAllowed getCrostiniAllowed() {
        return this.crostiniAllowed;
    }
    
    public UserPolicy setCrostiniAllowed(final CrostiniAllowed crostiniAllowed) {
        this.crostiniAllowed = crostiniAllowed;
        return this;
    }
    
    public DefaultCookiesSetting getDefaultCookiesSettings() {
        return this.defaultCookiesSettings;
    }
    
    public UserPolicy setDefaultCookiesSettings(final DefaultCookiesSetting defaultCookiesSettings) {
        this.defaultCookiesSettings = defaultCookiesSettings;
        return this;
    }
    
    public DeletingBrowserHistoryDisabled getDeletingBrowserHistoryDisabled() {
        return this.deletingBrowserHistoryDisabled;
    }
    
    public UserPolicy setDeletingBrowserHistoryDisabled(final DeletingBrowserHistoryDisabled deletingBrowserHistoryDisabled) {
        this.deletingBrowserHistoryDisabled = deletingBrowserHistoryDisabled;
        return this;
    }
    
    public DeviceEnrollmentPermissions getDeviceEnrollmentPermissions() {
        return this.deviceEnrollmentPermissions;
    }
    
    public UserPolicy setDeviceEnrollmentPermissions(final DeviceEnrollmentPermissions deviceEnrollmentPermissions) {
        this.deviceEnrollmentPermissions = deviceEnrollmentPermissions;
        return this;
    }
    
    public DisableSafeBrowsingProceedAnyway getDisableSafeBrowsingProceedAnyway() {
        return this.disableSafeBrowsingProceedAnyway;
    }
    
    public UserPolicy setDisableSafeBrowsingProceedAnyway(final DisableSafeBrowsingProceedAnyway disableSafeBrowsingProceedAnyway) {
        this.disableSafeBrowsingProceedAnyway = disableSafeBrowsingProceedAnyway;
        return this;
    }
    
    public DisplayName getDisplayName() {
        return this.displayName;
    }
    
    public UserPolicy setDisplayName(final DisplayName displayName) {
        this.displayName = displayName;
        return this;
    }
    
    public EditBookmarksDisabled getEditBookmarksDisabled() {
        return this.editBookmarksDisabled;
    }
    
    public UserPolicy setEditBookmarksDisabled(final EditBookmarksDisabled editBookmarksDisabled) {
        this.editBookmarksDisabled = editBookmarksDisabled;
        return this;
    }
    
    public ExtensionInstallSources getExtensionInstallSources() {
        return this.extensionInstallSources;
    }
    
    public UserPolicy setExtensionInstallSources(final ExtensionInstallSources extensionInstallSources) {
        this.extensionInstallSources = extensionInstallSources;
        return this;
    }
    
    public ExternalStorageAccessibility getExternalStorageAccessibility() {
        return this.externalStorageAccessibility;
    }
    
    public UserPolicy setExternalStorageAccessibility(final ExternalStorageAccessibility externalStorageAccessibility) {
        this.externalStorageAccessibility = externalStorageAccessibility;
        return this;
    }
    
    public HomepageSettings getHomepageSettings() {
        return this.homepageSettings;
    }
    
    public UserPolicy setHomepageSettings(final HomepageSettings homepageSettings) {
        this.homepageSettings = homepageSettings;
        return this;
    }
    
    public IncognitoModeAvailability getIncognitoModeAvailability() {
        return this.incognitoModeAvailability;
    }
    
    public UserPolicy setIncognitoModeAvailability(final IncognitoModeAvailability incognitoModeAvailability) {
        this.incognitoModeAvailability = incognitoModeAvailability;
        return this;
    }
    
    public ManagedBookmarks getManagedBookmarks() {
        return this.managedBookmarks;
    }
    
    public UserPolicy setManagedBookmarks(final ManagedBookmarks managedBookmarks) {
        this.managedBookmarks = managedBookmarks;
        return this;
    }
    
    public OpenNetworkConfig getOpenNetworkConfig() {
        return this.openNetworkConfig;
    }
    
    public UserPolicy setOpenNetworkConfig(final OpenNetworkConfig openNetworkConfig) {
        this.openNetworkConfig = openNetworkConfig;
        return this;
    }
    
    public PasswordManagerEnabled getPasswordManagerEnabled() {
        return this.passwordManagerEnabled;
    }
    
    public UserPolicy setPasswordManagerEnabled(final PasswordManagerEnabled passwordManagerEnabled) {
        this.passwordManagerEnabled = passwordManagerEnabled;
        return this;
    }
    
    public PopupsAllowedForUrls getPopupsAllowedForUrls() {
        return this.popupsAllowedForUrls;
    }
    
    public UserPolicy setPopupsAllowedForUrls(final PopupsAllowedForUrls popupsAllowedForUrls) {
        this.popupsAllowedForUrls = popupsAllowedForUrls;
        return this;
    }
    
    public PopupsBlockedForUrls getPopupsBlockedForUrls() {
        return this.popupsBlockedForUrls;
    }
    
    public UserPolicy setPopupsBlockedForUrls(final PopupsBlockedForUrls popupsBlockedForUrls) {
        this.popupsBlockedForUrls = popupsBlockedForUrls;
        return this;
    }
    
    public PopupsDefaultSettings getPopupsDefaultSettings() {
        return this.popupsDefaultSettings;
    }
    
    public UserPolicy setPopupsDefaultSettings(final PopupsDefaultSettings popupsDefaultSettings) {
        this.popupsDefaultSettings = popupsDefaultSettings;
        return this;
    }
    
    public PowerManagementIdleSettings getPowerManagementIdleSettings() {
        return this.powerManagementIdleSettings;
    }
    
    public UserPolicy setPowerManagementIdleSettings(final PowerManagementIdleSettings powerManagementIdleSettings) {
        this.powerManagementIdleSettings = powerManagementIdleSettings;
        return this;
    }
    
    public PrintingDisabled getPrintingDisabled() {
        return this.printingDisabled;
    }
    
    public UserPolicy setPrintingDisabled(final PrintingDisabled printingDisabled) {
        this.printingDisabled = printingDisabled;
        return this;
    }
    
    public QuicDisallowed getQuicDisallowed() {
        return this.quicDisallowed;
    }
    
    public UserPolicy setQuicDisallowed(final QuicDisallowed quicDisallowed) {
        this.quicDisallowed = quicDisallowed;
        return this;
    }
    
    public RemoteAccessHostClientDomainList getRemoteAccessHostClientDomainList() {
        return this.remoteAccessHostClientDomainList;
    }
    
    public UserPolicy setRemoteAccessHostClientDomainList(final RemoteAccessHostClientDomainList remoteAccessHostClientDomainList) {
        this.remoteAccessHostClientDomainList = remoteAccessHostClientDomainList;
        return this;
    }
    
    public RestoreOnStartupUrls getRestoreOnStartupUrls() {
        return this.restoreOnStartupUrls;
    }
    
    public UserPolicy setRestoreOnStartupUrls(final RestoreOnStartupUrls restoreOnStartupUrls) {
        this.restoreOnStartupUrls = restoreOnStartupUrls;
        return this;
    }
    
    public SafeBrowsingEnabled getSafeBrowsingEnabled() {
        return this.safeBrowsingEnabled;
    }
    
    public UserPolicy setSafeBrowsingEnabled(final SafeBrowsingEnabled safeBrowsingEnabled) {
        this.safeBrowsingEnabled = safeBrowsingEnabled;
        return this;
    }
    
    public SavingBrowserHistoryDisabled getSavingBrowserHistoryDisabled() {
        return this.savingBrowserHistoryDisabled;
    }
    
    public UserPolicy setSavingBrowserHistoryDisabled(final SavingBrowserHistoryDisabled savingBrowserHistoryDisabled) {
        this.savingBrowserHistoryDisabled = savingBrowserHistoryDisabled;
        return this;
    }
    
    public ScreenLockDisabled getScreenLockDisabled() {
        return this.screenLockDisabled;
    }
    
    public UserPolicy setScreenLockDisabled(final ScreenLockDisabled screenLockDisabled) {
        this.screenLockDisabled = screenLockDisabled;
        return this;
    }
    
    public SessionLengthLimit getSessionLengthLimit() {
        return this.sessionLengthLimit;
    }
    
    public UserPolicy setSessionLengthLimit(final SessionLengthLimit sessionLengthLimit) {
        this.sessionLengthLimit = sessionLengthLimit;
        return this;
    }
    
    public ShowHomeButton getShowHomeButton() {
        return this.showHomeButton;
    }
    
    public UserPolicy setShowHomeButton(final ShowHomeButton showHomeButton) {
        this.showHomeButton = showHomeButton;
        return this;
    }
    
    public TaskManagerEndProcessDisabled getTaskManagerEndProcessDisabled() {
        return this.taskManagerEndProcessDisabled;
    }
    
    public UserPolicy setTaskManagerEndProcessDisabled(final TaskManagerEndProcessDisabled taskManagerEndProcessDisabled) {
        this.taskManagerEndProcessDisabled = taskManagerEndProcessDisabled;
        return this;
    }
    
    public TermsOfServiceURL getTermsOfServiceUrl() {
        return this.termsOfServiceUrl;
    }
    
    public UserPolicy setTermsOfServiceUrl(final TermsOfServiceURL termsOfServiceUrl) {
        this.termsOfServiceUrl = termsOfServiceUrl;
        return this;
    }
    
    public UrlAllowlist getUrlAllowlist() {
        return this.urlAllowlist;
    }
    
    public UserPolicy setUrlAllowlist(final UrlAllowlist urlAllowlist) {
        this.urlAllowlist = urlAllowlist;
        return this;
    }
    
    public UrlBlacklist getUrlBlacklist() {
        return this.urlBlacklist;
    }
    
    public UserPolicy setUrlBlacklist(final UrlBlacklist urlBlacklist) {
        this.urlBlacklist = urlBlacklist;
        return this;
    }
    
    public UrlBlocklist getUrlBlocklist() {
        return this.urlBlocklist;
    }
    
    public UserPolicy setUrlBlocklist(final UrlBlocklist urlBlocklist) {
        this.urlBlocklist = urlBlocklist;
        return this;
    }
    
    public UrlWhitelist getUrlWhitelist() {
        return this.urlWhitelist;
    }
    
    public UserPolicy setUrlWhitelist(final UrlWhitelist urlWhitelist) {
        this.urlWhitelist = urlWhitelist;
        return this;
    }
    
    public UserAttestationEnabled getUserAttestationEnabled() {
        return this.userAttestationEnabled;
    }
    
    public UserPolicy setUserAttestationEnabled(final UserAttestationEnabled userAttestationEnabled) {
        this.userAttestationEnabled = userAttestationEnabled;
        return this;
    }
    
    public UserAvatarImage getUserAvatarImage() {
        return this.userAvatarImage;
    }
    
    public UserPolicy setUserAvatarImage(final UserAvatarImage userAvatarImage) {
        this.userAvatarImage = userAvatarImage;
        return this;
    }
    
    public UserVerifiedAccessControl getUserVerifiedAccessControl() {
        return this.userVerifiedAccessControl;
    }
    
    public UserPolicy setUserVerifiedAccessControl(final UserVerifiedAccessControl userVerifiedAccessControl) {
        this.userVerifiedAccessControl = userVerifiedAccessControl;
        return this;
    }
    
    public UserVerifiedModeRequired getUserVerifiedModeRequired() {
        return this.userVerifiedModeRequired;
    }
    
    public UserPolicy setUserVerifiedModeRequired(final UserVerifiedModeRequired userVerifiedModeRequired) {
        this.userVerifiedModeRequired = userVerifiedModeRequired;
        return this;
    }
    
    public WallpaperImage getWallpaperImage() {
        return this.wallpaperImage;
    }
    
    public UserPolicy setWallpaperImage(final WallpaperImage wallpaperImage) {
        this.wallpaperImage = wallpaperImage;
        return this;
    }
    
    public WebRtcUdpPortRange getWebRtcUdpPortRange() {
        return this.webRtcUdpPortRange;
    }
    
    public UserPolicy setWebRtcUdpPortRange(final WebRtcUdpPortRange webRtcUdpPortRange) {
        this.webRtcUdpPortRange = webRtcUdpPortRange;
        return this;
    }
    
    public UserPolicy set(final String s, final Object o) {
        return (UserPolicy)super.set(s, o);
    }
    
    public UserPolicy clone() {
        return (UserPolicy)super.clone();
    }
}
