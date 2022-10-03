package org.eclipse.jdt.internal.antadapter;

import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.Locale;
import java.util.ResourceBundle;

public class AntAdapterMessages
{
    private static final String BUNDLE_NAME = "org.eclipse.jdt.internal.antadapter.messages";
    private static ResourceBundle RESOURCE_BUNDLE;
    
    static {
        try {
            AntAdapterMessages.RESOURCE_BUNDLE = ResourceBundle.getBundle("org.eclipse.jdt.internal.antadapter.messages", Locale.getDefault());
        }
        catch (final MissingResourceException e) {
            System.out.println("Missing resource : " + "org.eclipse.jdt.internal.antadapter.messages".replace('.', '/') + ".properties for locale " + Locale.getDefault());
            throw e;
        }
    }
    
    private AntAdapterMessages() {
    }
    
    public static String getString(final String key) {
        try {
            return AntAdapterMessages.RESOURCE_BUNDLE.getString(key);
        }
        catch (final MissingResourceException ex) {
            return String.valueOf('!') + key + '!';
        }
    }
    
    public static String getString(final String key, final String argument) {
        try {
            final String message = AntAdapterMessages.RESOURCE_BUNDLE.getString(key);
            final MessageFormat messageFormat = new MessageFormat(message);
            return messageFormat.format(new String[] { argument });
        }
        catch (final MissingResourceException ex) {
            return String.valueOf('!') + key + '!';
        }
    }
}
