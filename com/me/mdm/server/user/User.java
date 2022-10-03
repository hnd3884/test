package com.me.mdm.server.user;

import com.google.gson.annotations.SerializedName;

public class User
{
    @SerializedName(value = "user_id", alternate = { "managed_user_id", "resource_id" })
    public Long userID;
    @SerializedName(value = "user_email", alternate = { "email_address" })
    public String userEmail;
    @SerializedName(value = "user_name", alternate = { "name" })
    public String userName;
    @SerializedName(value = "domain_name", alternate = { "domain_netbios_name" })
    public String domainName;
    @SerializedName("phone_number")
    public String phoneNumber;
}
