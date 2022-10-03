package com.me.ems.onpremise.uac.api.v1.model;

import java.util.Map;
import java.util.List;
import com.me.ems.onpremise.uac.api.annotations.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotEmpty;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserDetails
{
    @NotEmpty
    private String authType;
    @NotEmpty
    private String userName;
    @NotEmpty
    private String roleID;
    private Long userID;
    @Email(message = "Invalid Email")
    private String mailID;
    @Positive
    private String phoneNumber;
    private String domainName;
    @NotEmpty
    private String language;
    @Valid
    private int computerScopeType;
    @Valid
    private int deviceScopeType;
    private List<String> staticComputerGroups;
    private List<String> remoteOfficeGroups;
    private List<String> mobileDeviceGroups;
    private List<String> customerIDs;
    private String spiceUser;
    private int networkDeviceScopeType;
    private List<String> networkDeviceGroups;
    @Valid
    private int probeScopeType;
    private List<String> probeIDs;
    private Map<String, Object> probeHandlerObject;
    private List<Map<String, Object>> probeStaticCustomGroups;
    private List<Map<String, Object>> probeMobileDeviceGroups;
    private String modifyUserProbeAction;
    private boolean isSDPUser;
    
    public String getAuthType() {
        return this.authType;
    }
    
    public void setAuthType(final String authType) {
        this.authType = authType;
    }
    
    public String getUserName() {
        return this.userName;
    }
    
    public void setUserName(final String userName) {
        this.userName = userName;
    }
    
    public String getRoleID() {
        return this.roleID;
    }
    
    public void setRoleID(final String roleID) {
        this.roleID = roleID;
    }
    
    public Long getUserID() {
        return this.userID;
    }
    
    public void setUserID(final Long userID) {
        this.userID = userID;
    }
    
    public String getMailID() {
        return this.mailID;
    }
    
    public void setMailID(final String mailID) {
        this.mailID = mailID;
    }
    
    public String getPhoneNumber() {
        return this.phoneNumber;
    }
    
    public void setPhoneNumber(final String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }
    
    public String getDomainName() {
        return this.domainName;
    }
    
    public void setDomainName(final String domainName) {
        this.domainName = domainName;
    }
    
    public String getLanguage() {
        return this.language;
    }
    
    public void setLanguage(final String language) {
        this.language = language;
    }
    
    public int getComputerScopeType() {
        return this.computerScopeType;
    }
    
    public void setComputerScopeType(final int computerScopeType) {
        this.computerScopeType = computerScopeType;
    }
    
    public int getDeviceScopeType() {
        return this.deviceScopeType;
    }
    
    public void setDeviceScopeType(final int deviceScopeType) {
        this.deviceScopeType = deviceScopeType;
    }
    
    public List<String> getStaticComputerGroups() {
        return this.staticComputerGroups;
    }
    
    public void setStaticComputerGroups(final List<String> staticComputerGroups) {
        this.staticComputerGroups = staticComputerGroups;
    }
    
    public List<String> getRemoteOfficeGroups() {
        return this.remoteOfficeGroups;
    }
    
    public void setRemoteOfficeGroups(final List<String> remoteOfficeGroups) {
        this.remoteOfficeGroups = remoteOfficeGroups;
    }
    
    public List<String> getMobileDeviceGroups() {
        return this.mobileDeviceGroups;
    }
    
    public void setMobileDeviceGroups(final List<String> mobileDeviceGroups) {
        this.mobileDeviceGroups = mobileDeviceGroups;
    }
    
    public String getSpiceUser() {
        return this.spiceUser;
    }
    
    public void setSpiceUser(final String spiceUser) {
        this.spiceUser = spiceUser;
    }
    
    public List<String> getCustomerIDs() {
        return this.customerIDs;
    }
    
    public void setCustomerIDs(final List<String> customerIDs) {
        this.customerIDs = customerIDs;
    }
    
    public int getProbeScopeType() {
        return this.probeScopeType;
    }
    
    public void setProbeScopeType(final int probeScopeType) {
        this.probeScopeType = probeScopeType;
    }
    
    public List<String> getProbeIDs() {
        return this.probeIDs;
    }
    
    public void setProbeIDs(final List<String> probeIDs) {
        this.probeIDs = probeIDs;
    }
    
    public Map<String, Object> getProbeHandlerObject() {
        return this.probeHandlerObject;
    }
    
    public void setProbeHandlerObject(final Map<String, Object> probeHandlerObject) {
        this.probeHandlerObject = probeHandlerObject;
    }
    
    public List<Map<String, Object>> getProbeStaticCustomGroups() {
        return this.probeStaticCustomGroups;
    }
    
    public void setProbeStaticCustomGroups(final List<Map<String, Object>> probeStaticCustomGroups) {
        this.probeStaticCustomGroups = probeStaticCustomGroups;
    }
    
    public List<Map<String, Object>> getProbeMobileDeviceGroups() {
        return this.probeMobileDeviceGroups;
    }
    
    public void setProbeMobileDeviceGroups(final List<Map<String, Object>> probeMobileDeviceGroups) {
        this.probeMobileDeviceGroups = probeMobileDeviceGroups;
    }
    
    public String getModifyUserProbeAction() {
        return this.modifyUserProbeAction;
    }
    
    public void setModifyUserProbeAction(final String modifyUserProbeAction) {
        this.modifyUserProbeAction = modifyUserProbeAction;
    }
    
    public boolean getIsSDPUser() {
        return this.isSDPUser;
    }
    
    public void setIsSDPUser(final boolean isSDPUser) {
        this.isSDPUser = isSDPUser;
    }
    
    public List<String> getNetworkDeviceGroups() {
        return this.networkDeviceGroups;
    }
    
    public void setNetworkDeviceGroups(final List<String> networkDeviceGroups) {
        this.networkDeviceGroups = networkDeviceGroups;
    }
    
    public int getNetworkDeviceScopeType() {
        return this.networkDeviceScopeType;
    }
    
    public void setNetworkDeviceScopeType(final int networkDeviceScopeType) {
        this.networkDeviceScopeType = networkDeviceScopeType;
    }
}
