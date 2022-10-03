package com.me.mdm.server.security.profile;

import com.adventnet.sym.server.mdm.util.MDMStringUtils;

public class ApplePayloadSecretFieldsHandler extends PayloadSecretFieldsReplacer
{
    private static ApplePayloadSecretFieldsHandler applePayloadSecretFieldsHandler;
    
    public static ApplePayloadSecretFieldsHandler getInstance() {
        if (ApplePayloadSecretFieldsHandler.applePayloadSecretFieldsHandler == null) {
            ApplePayloadSecretFieldsHandler.applePayloadSecretFieldsHandler = new ApplePayloadSecretFieldsHandler();
        }
        return ApplePayloadSecretFieldsHandler.applePayloadSecretFieldsHandler;
    }
    
    @Override
    public String escapeSpecialCharactersInPassword(String password) {
        password = new MDMStringUtils().escapeMetaCharacters(password);
        if (password.contains("&") || password.contains("<") || password.contains(">")) {
            password = "<![CDATA[" + password + "]]>";
        }
        return password;
    }
    
    static {
        ApplePayloadSecretFieldsHandler.applePayloadSecretFieldsHandler = null;
    }
}
