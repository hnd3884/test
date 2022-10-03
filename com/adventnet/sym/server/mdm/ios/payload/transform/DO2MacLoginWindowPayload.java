package com.adventnet.sym.server.mdm.ios.payload.transform;

import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;
import com.adventnet.sym.server.mdm.util.MDMStringUtils;
import com.adventnet.sym.server.mdm.ios.payload.mac.MacLoginWindowPayload;
import com.adventnet.persistence.Row;
import com.adventnet.sym.server.mdm.ios.payload.IOSPayload;
import com.adventnet.persistence.DataObject;

public class DO2MacLoginWindowPayload implements DO2Payload
{
    @Override
    public IOSPayload[] createPayload(final DataObject dataObject) {
        final IOSPayload[] payloadArray = { null };
        try {
            final Iterator iterator = dataObject.getRows("MacLoginWindow");
            while (iterator.hasNext()) {
                final Row macLoginWindowRow = iterator.next();
                final Row macLoginWindowSettingRow = dataObject.getRow("MacLoginWindowSettings", macLoginWindowRow);
                final String loginWindowText = (String)macLoginWindowRow.get("LOGIN_WINDOW_TEXT");
                final Boolean showFullName = (Boolean)macLoginWindowRow.get("SHOW_FULL_NAME");
                final Boolean showLocalUsers = (Boolean)macLoginWindowRow.get("SHOW_LOCAL_USERS");
                final Boolean showMobileAccounts = (Boolean)macLoginWindowRow.get("SHOW_MOBILE_ACCOUNTS");
                final Boolean showNetworkUsers = (Boolean)macLoginWindowRow.get("INCLUDE_NETWORK_USERS");
                final Boolean showAdminUsers = (Boolean)macLoginWindowRow.get("SHOW_ADMIN_USERS");
                final Boolean showOthers = (Boolean)macLoginWindowRow.get("SHOW_OTHER_USERS");
                final Boolean showSleepButton = (Boolean)macLoginWindowRow.get("SHOW_SLEEP_BUTTON");
                final Boolean showRestartButton = (Boolean)macLoginWindowRow.get("SHOW_RESTART_BUTTON");
                final Boolean showShutdownButton = (Boolean)macLoginWindowRow.get("SHOW_SHUTDOWN_BUTTON");
                final Boolean isAutoLoginDisabled = (Boolean)macLoginWindowSettingRow.get("DISABLE_AUTO_LOGIN");
                final Boolean isConsoleLoginEnabled = (Boolean)macLoginWindowSettingRow.get("ENABLE_CONSOLE_LOGIN");
                final Boolean adminDisableManagement = (Boolean)macLoginWindowSettingRow.get("ADMIN_MAY_DISABLE_MCX");
                final Boolean CompNameToRecName = (Boolean)macLoginWindowSettingRow.get("COMPUTER_NAME_TO_RECORD_NAME");
                final Boolean isExternalAccEnabled = (Boolean)macLoginWindowSettingRow.get("ENABLE_EXTERNAL_ACCOUNTS");
                final Integer showPasswordHint = (Integer)macLoginWindowSettingRow.get("SHOW_PASSWORD_HINT_AFTER");
                final Boolean disableRestartOnLogin = (Boolean)macLoginWindowRow.get("DISABLE_RESTART_ON_LOGIN");
                final Boolean disabledShutdownOnLogin = (Boolean)macLoginWindowRow.get("DISABLE_SHUTDOWN_ON_LOGIN");
                final Boolean disableImmediateLock = (Boolean)macLoginWindowRow.get("DISABLE_IMMEDIATE_LOCK");
                final Boolean disableLogoutOnLogin = (Boolean)macLoginWindowRow.get("DISABLE_LOGOUT_MENU_ITEM");
                final Boolean showInputMenu = (Boolean)macLoginWindowRow.get("SHOW_INPUT_MENU");
                final Boolean disableFDELogin = (Boolean)macLoginWindowSettingRow.get("DISABLE_FDE_AUTO_LOGIN");
                final MacLoginWindowPayload macLoginWindowPayload = new MacLoginWindowPayload(1, "MDM", "com.apple.loginwindow", "Mac Login Window payload");
                if (!MDMStringUtils.isEmpty(loginWindowText)) {
                    macLoginWindowPayload.setLoginWindowText(loginWindowText);
                }
                macLoginWindowPayload.setShowFullName(showFullName);
                if (!showFullName) {
                    macLoginWindowPayload.setHideLocalUsers(showLocalUsers);
                    macLoginWindowPayload.setHideMobileAccounts(showMobileAccounts);
                    macLoginWindowPayload.setIncludeNetworkUser(showNetworkUsers);
                    macLoginWindowPayload.setHideAdminUsers(showAdminUsers);
                    macLoginWindowPayload.setShowOther(showOthers);
                }
                macLoginWindowPayload.setSleepButtonStatus(showSleepButton);
                macLoginWindowPayload.setRestartButtonStatus(showRestartButton);
                macLoginWindowPayload.setShutdownButtonStatus(showShutdownButton);
                macLoginWindowPayload.setAutoLoginStatus(isAutoLoginDisabled);
                macLoginWindowPayload.setConsoleAccessStatus(isConsoleLoginEnabled);
                macLoginWindowPayload.setAdminMayDisableMCX(adminDisableManagement);
                macLoginWindowPayload.setComputerNameToRecordName(CompNameToRecName);
                macLoginWindowPayload.setExternalAccountStatus(isExternalAccEnabled);
                macLoginWindowPayload.setPasswordHintsAfter(showPasswordHint);
                macLoginWindowPayload.setDisableFDELogin(disableFDELogin);
                macLoginWindowPayload.setShowInputMenu(showInputMenu);
                macLoginWindowPayload.setLogoutDisableOnLogin(disableLogoutOnLogin);
                macLoginWindowPayload.setShutdownDisableOnLogin(disabledShutdownOnLogin);
                macLoginWindowPayload.setRestartDisableOnLogin(disableRestartOnLogin);
                macLoginWindowPayload.setDisableImmediateLockOnLogin(disableImmediateLock);
                payloadArray[0] = macLoginWindowPayload;
            }
        }
        catch (final Exception e) {
            Logger.getLogger("MDMConfigLogger").log(Level.SEVERE, "Exception in login window", e);
        }
        return payloadArray;
    }
}
