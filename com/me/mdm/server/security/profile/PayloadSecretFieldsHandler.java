package com.me.mdm.server.security.profile;

import java.util.regex.Matcher;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;

public class PayloadSecretFieldsHandler extends PayloadSecretFieldsReplacer
{
    private static PayloadSecretFieldsHandler payloadSecretFieldsHandler;
    
    public static PayloadSecretFieldsHandler getInstance() {
        if (PayloadSecretFieldsHandler.payloadSecretFieldsHandler == null) {
            PayloadSecretFieldsHandler.payloadSecretFieldsHandler = new PayloadSecretFieldsHandler();
        }
        return PayloadSecretFieldsHandler.payloadSecretFieldsHandler;
    }
    
    @Override
    public String escapeSpecialCharactersInPassword(String password) {
        password = Matcher.quoteReplacement(MDMStringUtils.escapeJson(password));
        return password;
    }
    
    static {
        PayloadSecretFieldsHandler.payloadSecretFieldsHandler = null;
    }
}
