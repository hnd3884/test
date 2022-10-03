package com.google.api.services.directory;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class DirectoryScopes
{
    public static final String ADMIN_CHROME_PRINTERS = "https://www.googleapis.com/auth/admin.chrome.printers";
    public static final String ADMIN_CHROME_PRINTERS_READONLY = "https://www.googleapis.com/auth/admin.chrome.printers.readonly";
    public static final String ADMIN_DIRECTORY_CUSTOMER = "https://www.googleapis.com/auth/admin.directory.customer";
    public static final String ADMIN_DIRECTORY_CUSTOMER_READONLY = "https://www.googleapis.com/auth/admin.directory.customer.readonly";
    public static final String ADMIN_DIRECTORY_DEVICE_CHROMEOS = "https://www.googleapis.com/auth/admin.directory.device.chromeos";
    public static final String ADMIN_DIRECTORY_DEVICE_CHROMEOS_READONLY = "https://www.googleapis.com/auth/admin.directory.device.chromeos.readonly";
    public static final String ADMIN_DIRECTORY_DEVICE_MOBILE = "https://www.googleapis.com/auth/admin.directory.device.mobile";
    public static final String ADMIN_DIRECTORY_DEVICE_MOBILE_ACTION = "https://www.googleapis.com/auth/admin.directory.device.mobile.action";
    public static final String ADMIN_DIRECTORY_DEVICE_MOBILE_READONLY = "https://www.googleapis.com/auth/admin.directory.device.mobile.readonly";
    public static final String ADMIN_DIRECTORY_DOMAIN = "https://www.googleapis.com/auth/admin.directory.domain";
    public static final String ADMIN_DIRECTORY_DOMAIN_READONLY = "https://www.googleapis.com/auth/admin.directory.domain.readonly";
    public static final String ADMIN_DIRECTORY_GROUP = "https://www.googleapis.com/auth/admin.directory.group";
    public static final String ADMIN_DIRECTORY_GROUP_MEMBER = "https://www.googleapis.com/auth/admin.directory.group.member";
    public static final String ADMIN_DIRECTORY_GROUP_MEMBER_READONLY = "https://www.googleapis.com/auth/admin.directory.group.member.readonly";
    public static final String ADMIN_DIRECTORY_GROUP_READONLY = "https://www.googleapis.com/auth/admin.directory.group.readonly";
    public static final String ADMIN_DIRECTORY_ORGUNIT = "https://www.googleapis.com/auth/admin.directory.orgunit";
    public static final String ADMIN_DIRECTORY_ORGUNIT_READONLY = "https://www.googleapis.com/auth/admin.directory.orgunit.readonly";
    public static final String ADMIN_DIRECTORY_RESOURCE_CALENDAR = "https://www.googleapis.com/auth/admin.directory.resource.calendar";
    public static final String ADMIN_DIRECTORY_RESOURCE_CALENDAR_READONLY = "https://www.googleapis.com/auth/admin.directory.resource.calendar.readonly";
    public static final String ADMIN_DIRECTORY_ROLEMANAGEMENT = "https://www.googleapis.com/auth/admin.directory.rolemanagement";
    public static final String ADMIN_DIRECTORY_ROLEMANAGEMENT_READONLY = "https://www.googleapis.com/auth/admin.directory.rolemanagement.readonly";
    public static final String ADMIN_DIRECTORY_USER = "https://www.googleapis.com/auth/admin.directory.user";
    public static final String ADMIN_DIRECTORY_USER_ALIAS = "https://www.googleapis.com/auth/admin.directory.user.alias";
    public static final String ADMIN_DIRECTORY_USER_ALIAS_READONLY = "https://www.googleapis.com/auth/admin.directory.user.alias.readonly";
    public static final String ADMIN_DIRECTORY_USER_READONLY = "https://www.googleapis.com/auth/admin.directory.user.readonly";
    public static final String ADMIN_DIRECTORY_USER_SECURITY = "https://www.googleapis.com/auth/admin.directory.user.security";
    public static final String ADMIN_DIRECTORY_USERSCHEMA = "https://www.googleapis.com/auth/admin.directory.userschema";
    public static final String ADMIN_DIRECTORY_USERSCHEMA_READONLY = "https://www.googleapis.com/auth/admin.directory.userschema.readonly";
    public static final String CLOUD_PLATFORM = "https://www.googleapis.com/auth/cloud-platform";
    
    public static Set<String> all() {
        final Set<String> set = new HashSet<String>();
        set.add("https://www.googleapis.com/auth/admin.chrome.printers");
        set.add("https://www.googleapis.com/auth/admin.chrome.printers.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.customer");
        set.add("https://www.googleapis.com/auth/admin.directory.customer.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.device.chromeos");
        set.add("https://www.googleapis.com/auth/admin.directory.device.chromeos.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.device.mobile");
        set.add("https://www.googleapis.com/auth/admin.directory.device.mobile.action");
        set.add("https://www.googleapis.com/auth/admin.directory.device.mobile.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.domain");
        set.add("https://www.googleapis.com/auth/admin.directory.domain.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.group");
        set.add("https://www.googleapis.com/auth/admin.directory.group.member");
        set.add("https://www.googleapis.com/auth/admin.directory.group.member.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.group.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.orgunit");
        set.add("https://www.googleapis.com/auth/admin.directory.orgunit.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.resource.calendar");
        set.add("https://www.googleapis.com/auth/admin.directory.resource.calendar.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.rolemanagement");
        set.add("https://www.googleapis.com/auth/admin.directory.rolemanagement.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.user");
        set.add("https://www.googleapis.com/auth/admin.directory.user.alias");
        set.add("https://www.googleapis.com/auth/admin.directory.user.alias.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.user.readonly");
        set.add("https://www.googleapis.com/auth/admin.directory.user.security");
        set.add("https://www.googleapis.com/auth/admin.directory.userschema");
        set.add("https://www.googleapis.com/auth/admin.directory.userschema.readonly");
        set.add("https://www.googleapis.com/auth/cloud-platform");
        return Collections.unmodifiableSet((Set<? extends String>)set);
    }
    
    private DirectoryScopes() {
    }
}
