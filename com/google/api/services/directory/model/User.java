package com.google.api.services.directory.model;

import com.google.api.client.util.GenericData;
import java.util.Map;
import com.google.api.client.util.DateTime;
import java.util.List;
import com.google.api.client.util.Key;
import com.google.api.client.json.GenericJson;

public final class User extends GenericJson
{
    @Key
    private Object addresses;
    @Key
    private Boolean agreedToTerms;
    @Key
    private List<String> aliases;
    @Key
    private Boolean archived;
    @Key
    private Boolean changePasswordAtNextLogin;
    @Key
    private DateTime creationTime;
    @Key
    private Map<String, Map<String, Object>> customSchemas;
    @Key
    private String customerId;
    @Key
    private DateTime deletionTime;
    @Key
    private Object emails;
    @Key
    private String etag;
    @Key
    private Object externalIds;
    @Key
    private Object gender;
    @Key
    private String hashFunction;
    @Key
    private String id;
    @Key
    private Object ims;
    @Key
    private Boolean includeInGlobalAddressList;
    @Key
    private Boolean ipWhitelisted;
    @Key
    private Boolean isAdmin;
    @Key
    private Boolean isDelegatedAdmin;
    @Key
    private Boolean isEnforcedIn2Sv;
    @Key
    private Boolean isEnrolledIn2Sv;
    @Key
    private Boolean isMailboxSetup;
    @Key
    private Object keywords;
    @Key
    private String kind;
    @Key
    private Object languages;
    @Key
    private DateTime lastLoginTime;
    @Key
    private Object locations;
    @Key
    private UserName name;
    @Key
    private List<String> nonEditableAliases;
    @Key
    private Object notes;
    @Key
    private String orgUnitPath;
    @Key
    private Object organizations;
    @Key
    private String password;
    @Key
    private Object phones;
    @Key
    private Object posixAccounts;
    @Key
    private String primaryEmail;
    @Key
    private String recoveryEmail;
    @Key
    private String recoveryPhone;
    @Key
    private Object relations;
    @Key
    private Object sshPublicKeys;
    @Key
    private Boolean suspended;
    @Key
    private String suspensionReason;
    @Key
    private String thumbnailPhotoEtag;
    @Key
    private String thumbnailPhotoUrl;
    @Key
    private Object websites;
    
    public Object getAddresses() {
        return this.addresses;
    }
    
    public User setAddresses(final Object addresses) {
        this.addresses = addresses;
        return this;
    }
    
    public Boolean getAgreedToTerms() {
        return this.agreedToTerms;
    }
    
    public User setAgreedToTerms(final Boolean agreedToTerms) {
        this.agreedToTerms = agreedToTerms;
        return this;
    }
    
    public List<String> getAliases() {
        return this.aliases;
    }
    
    public User setAliases(final List<String> aliases) {
        this.aliases = aliases;
        return this;
    }
    
    public Boolean getArchived() {
        return this.archived;
    }
    
    public User setArchived(final Boolean archived) {
        this.archived = archived;
        return this;
    }
    
    public Boolean getChangePasswordAtNextLogin() {
        return this.changePasswordAtNextLogin;
    }
    
    public User setChangePasswordAtNextLogin(final Boolean changePasswordAtNextLogin) {
        this.changePasswordAtNextLogin = changePasswordAtNextLogin;
        return this;
    }
    
    public DateTime getCreationTime() {
        return this.creationTime;
    }
    
    public User setCreationTime(final DateTime creationTime) {
        this.creationTime = creationTime;
        return this;
    }
    
    public Map<String, Map<String, Object>> getCustomSchemas() {
        return this.customSchemas;
    }
    
    public User setCustomSchemas(final Map<String, Map<String, Object>> customSchemas) {
        this.customSchemas = customSchemas;
        return this;
    }
    
    public String getCustomerId() {
        return this.customerId;
    }
    
    public User setCustomerId(final String customerId) {
        this.customerId = customerId;
        return this;
    }
    
    public DateTime getDeletionTime() {
        return this.deletionTime;
    }
    
    public User setDeletionTime(final DateTime deletionTime) {
        this.deletionTime = deletionTime;
        return this;
    }
    
    public Object getEmails() {
        return this.emails;
    }
    
    public User setEmails(final Object emails) {
        this.emails = emails;
        return this;
    }
    
    public String getEtag() {
        return this.etag;
    }
    
    public User setEtag(final String etag) {
        this.etag = etag;
        return this;
    }
    
    public Object getExternalIds() {
        return this.externalIds;
    }
    
    public User setExternalIds(final Object externalIds) {
        this.externalIds = externalIds;
        return this;
    }
    
    public Object getGender() {
        return this.gender;
    }
    
    public User setGender(final Object gender) {
        this.gender = gender;
        return this;
    }
    
    public String getHashFunction() {
        return this.hashFunction;
    }
    
    public User setHashFunction(final String hashFunction) {
        this.hashFunction = hashFunction;
        return this;
    }
    
    public String getId() {
        return this.id;
    }
    
    public User setId(final String id) {
        this.id = id;
        return this;
    }
    
    public Object getIms() {
        return this.ims;
    }
    
    public User setIms(final Object ims) {
        this.ims = ims;
        return this;
    }
    
    public Boolean getIncludeInGlobalAddressList() {
        return this.includeInGlobalAddressList;
    }
    
    public User setIncludeInGlobalAddressList(final Boolean includeInGlobalAddressList) {
        this.includeInGlobalAddressList = includeInGlobalAddressList;
        return this;
    }
    
    public Boolean getIpWhitelisted() {
        return this.ipWhitelisted;
    }
    
    public User setIpWhitelisted(final Boolean ipWhitelisted) {
        this.ipWhitelisted = ipWhitelisted;
        return this;
    }
    
    public Boolean getIsAdmin() {
        return this.isAdmin;
    }
    
    public User setIsAdmin(final Boolean isAdmin) {
        this.isAdmin = isAdmin;
        return this;
    }
    
    public Boolean getIsDelegatedAdmin() {
        return this.isDelegatedAdmin;
    }
    
    public User setIsDelegatedAdmin(final Boolean isDelegatedAdmin) {
        this.isDelegatedAdmin = isDelegatedAdmin;
        return this;
    }
    
    public Boolean getIsEnforcedIn2Sv() {
        return this.isEnforcedIn2Sv;
    }
    
    public User setIsEnforcedIn2Sv(final Boolean isEnforcedIn2Sv) {
        this.isEnforcedIn2Sv = isEnforcedIn2Sv;
        return this;
    }
    
    public Boolean getIsEnrolledIn2Sv() {
        return this.isEnrolledIn2Sv;
    }
    
    public User setIsEnrolledIn2Sv(final Boolean isEnrolledIn2Sv) {
        this.isEnrolledIn2Sv = isEnrolledIn2Sv;
        return this;
    }
    
    public Boolean getIsMailboxSetup() {
        return this.isMailboxSetup;
    }
    
    public User setIsMailboxSetup(final Boolean isMailboxSetup) {
        this.isMailboxSetup = isMailboxSetup;
        return this;
    }
    
    public Object getKeywords() {
        return this.keywords;
    }
    
    public User setKeywords(final Object keywords) {
        this.keywords = keywords;
        return this;
    }
    
    public String getKind() {
        return this.kind;
    }
    
    public User setKind(final String kind) {
        this.kind = kind;
        return this;
    }
    
    public Object getLanguages() {
        return this.languages;
    }
    
    public User setLanguages(final Object languages) {
        this.languages = languages;
        return this;
    }
    
    public DateTime getLastLoginTime() {
        return this.lastLoginTime;
    }
    
    public User setLastLoginTime(final DateTime lastLoginTime) {
        this.lastLoginTime = lastLoginTime;
        return this;
    }
    
    public Object getLocations() {
        return this.locations;
    }
    
    public User setLocations(final Object locations) {
        this.locations = locations;
        return this;
    }
    
    public UserName getName() {
        return this.name;
    }
    
    public User setName(final UserName name) {
        this.name = name;
        return this;
    }
    
    public List<String> getNonEditableAliases() {
        return this.nonEditableAliases;
    }
    
    public User setNonEditableAliases(final List<String> nonEditableAliases) {
        this.nonEditableAliases = nonEditableAliases;
        return this;
    }
    
    public Object getNotes() {
        return this.notes;
    }
    
    public User setNotes(final Object notes) {
        this.notes = notes;
        return this;
    }
    
    public String getOrgUnitPath() {
        return this.orgUnitPath;
    }
    
    public User setOrgUnitPath(final String orgUnitPath) {
        this.orgUnitPath = orgUnitPath;
        return this;
    }
    
    public Object getOrganizations() {
        return this.organizations;
    }
    
    public User setOrganizations(final Object organizations) {
        this.organizations = organizations;
        return this;
    }
    
    public String getPassword() {
        return this.password;
    }
    
    public User setPassword(final String password) {
        this.password = password;
        return this;
    }
    
    public Object getPhones() {
        return this.phones;
    }
    
    public User setPhones(final Object phones) {
        this.phones = phones;
        return this;
    }
    
    public Object getPosixAccounts() {
        return this.posixAccounts;
    }
    
    public User setPosixAccounts(final Object posixAccounts) {
        this.posixAccounts = posixAccounts;
        return this;
    }
    
    public String getPrimaryEmail() {
        return this.primaryEmail;
    }
    
    public User setPrimaryEmail(final String primaryEmail) {
        this.primaryEmail = primaryEmail;
        return this;
    }
    
    public String getRecoveryEmail() {
        return this.recoveryEmail;
    }
    
    public User setRecoveryEmail(final String recoveryEmail) {
        this.recoveryEmail = recoveryEmail;
        return this;
    }
    
    public String getRecoveryPhone() {
        return this.recoveryPhone;
    }
    
    public User setRecoveryPhone(final String recoveryPhone) {
        this.recoveryPhone = recoveryPhone;
        return this;
    }
    
    public Object getRelations() {
        return this.relations;
    }
    
    public User setRelations(final Object relations) {
        this.relations = relations;
        return this;
    }
    
    public Object getSshPublicKeys() {
        return this.sshPublicKeys;
    }
    
    public User setSshPublicKeys(final Object sshPublicKeys) {
        this.sshPublicKeys = sshPublicKeys;
        return this;
    }
    
    public Boolean getSuspended() {
        return this.suspended;
    }
    
    public User setSuspended(final Boolean suspended) {
        this.suspended = suspended;
        return this;
    }
    
    public String getSuspensionReason() {
        return this.suspensionReason;
    }
    
    public User setSuspensionReason(final String suspensionReason) {
        this.suspensionReason = suspensionReason;
        return this;
    }
    
    public String getThumbnailPhotoEtag() {
        return this.thumbnailPhotoEtag;
    }
    
    public User setThumbnailPhotoEtag(final String thumbnailPhotoEtag) {
        this.thumbnailPhotoEtag = thumbnailPhotoEtag;
        return this;
    }
    
    public String getThumbnailPhotoUrl() {
        return this.thumbnailPhotoUrl;
    }
    
    public User setThumbnailPhotoUrl(final String thumbnailPhotoUrl) {
        this.thumbnailPhotoUrl = thumbnailPhotoUrl;
        return this;
    }
    
    public Object getWebsites() {
        return this.websites;
    }
    
    public User setWebsites(final Object websites) {
        this.websites = websites;
        return this;
    }
    
    public User set(final String fieldName, final Object value) {
        return (User)super.set(fieldName, value);
    }
    
    public User clone() {
        return (User)super.clone();
    }
}
