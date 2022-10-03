package com.sun.jndi.ldap.sasl;

import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.sasl.RealmCallback;
import javax.security.sasl.RealmChoiceCallback;
import javax.security.auth.callback.PasswordCallback;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.Callback;
import java.io.IOException;
import javax.security.auth.callback.CallbackHandler;

final class DefaultCallbackHandler implements CallbackHandler
{
    private char[] passwd;
    private String authenticationID;
    private String authRealm;
    
    DefaultCallbackHandler(final String authenticationID, final Object o, final String authRealm) throws IOException {
        this.authenticationID = authenticationID;
        this.authRealm = authRealm;
        if (o instanceof String) {
            this.passwd = ((String)o).toCharArray();
        }
        else if (o instanceof char[]) {
            this.passwd = ((char[])o).clone();
        }
        else if (o != null) {
            this.passwd = new String((byte[])o, "UTF8").toCharArray();
        }
    }
    
    @Override
    public void handle(final Callback[] array) throws IOException, UnsupportedCallbackException {
        for (int i = 0; i < array.length; ++i) {
            if (array[i] instanceof NameCallback) {
                ((NameCallback)array[i]).setName(this.authenticationID);
            }
            else if (array[i] instanceof PasswordCallback) {
                ((PasswordCallback)array[i]).setPassword(this.passwd);
            }
            else if (array[i] instanceof RealmChoiceCallback) {
                final String[] choices = ((RealmChoiceCallback)array[i]).getChoices();
                int selectedIndex = 0;
                if (this.authRealm != null && this.authRealm.length() > 0) {
                    selectedIndex = -1;
                    for (int j = 0; j < choices.length; ++j) {
                        if (choices[j].equals(this.authRealm)) {
                            selectedIndex = j;
                        }
                    }
                    if (selectedIndex == -1) {
                        final StringBuffer sb = new StringBuffer();
                        for (int k = 0; k < choices.length; ++k) {
                            sb.append(choices[k] + ",");
                        }
                        throw new IOException("Cannot match 'java.naming.security.sasl.realm' property value, '" + this.authRealm + "' with choices " + (Object)sb + "in RealmChoiceCallback");
                    }
                }
                ((RealmChoiceCallback)array[i]).setSelectedIndex(selectedIndex);
            }
            else {
                if (!(array[i] instanceof RealmCallback)) {
                    throw new UnsupportedCallbackException(array[i]);
                }
                final RealmCallback realmCallback = (RealmCallback)array[i];
                if (this.authRealm != null) {
                    realmCallback.setText(this.authRealm);
                }
                else {
                    final String defaultText = realmCallback.getDefaultText();
                    if (defaultText != null) {
                        realmCallback.setText(defaultText);
                    }
                    else {
                        realmCallback.setText("");
                    }
                }
            }
        }
    }
    
    void clearPassword() {
        if (this.passwd != null) {
            for (int i = 0; i < this.passwd.length; ++i) {
                this.passwd[i] = '\0';
            }
            this.passwd = null;
        }
    }
    
    @Override
    protected void finalize() throws Throwable {
        this.clearPassword();
    }
}
