package com.me.mdm.webclient.i18n;

import java.util.logging.Level;
import java.text.MessageFormat;
import com.adventnet.sym.server.mdm.util.MDMUtil;
import java.util.logging.Logger;
import com.adventnet.i18n.I18N;

public class MDMI18N extends I18N
{
    private static final Logger LOGGER;
    public static final String I18N_LINKS_DELIMITER = "@@@<l>";
    
    public static String getMsg(final String keyWithArg, final boolean isPlainText) throws Exception {
        return getMsg(keyWithArg, isPlainText, true);
    }
    
    public static String getMsg(final String keyWithArg, final boolean isPlainText, final boolean isHtmlNeeded) throws Exception {
        final String plainKey = extractPlainText(keyWithArg, "@@@");
        final String i18nValue = getMsg(plainKey, new Object[0]);
        String textWithArg = keyWithArg.replace(plainKey, i18nValue);
        textWithArg = replaceLinkArgs(textWithArg, isPlainText, isHtmlNeeded);
        final String finalText = replaceDynaArgs(textWithArg, isPlainText, isHtmlNeeded);
        return MDMUtil.replaceProductUrlLoaderValuesinText(finalText, null);
    }
    
    private static String replaceLinkArgs(final String keyWithArg, final boolean isPlainText, final boolean isHtmlNeeded) throws Exception {
        return replaceArgInText(keyWithArg, isPlainText, "@@@<l>", isHtmlNeeded);
    }
    
    private static String replaceDynaArgs(final String data, final boolean isPlainText, final boolean isHtmlNeeded) throws Exception {
        return replaceArgInText(data, isPlainText, "@@@", isHtmlNeeded);
    }
    
    private static String replaceArgInText(final String textWithArg, final boolean isPlainText, final String delimiter, final boolean isHtmlNeeded) throws Exception {
        String text = textWithArg;
        final int indexOfFirstDelimiter = textWithArg.indexOf(delimiter);
        if (indexOfFirstDelimiter != -1) {
            boolean replaceArg = false;
            final String argsString = textWithArg.substring(indexOfFirstDelimiter + delimiter.length());
            final Object[] args = argsString.split(delimiter);
            for (int i = 0; i < args.length; ++i) {
                final String message = I18N.getMsg((String)args[i], new Object[0]);
                if (!message.equalsIgnoreCase((String)args[i])) {
                    replaceArg = true;
                    args[i] = message;
                }
            }
            if (isHtmlNeeded) {
                text = textWithArg.substring(0, indexOfFirstDelimiter);
                text = replaceArgInText(text, args, "@@@<l>".equals(delimiter), isPlainText);
            }
            if (replaceArg) {
                final Object[] keyForReplace = argsString.split(delimiter);
                for (int j = 0; j < keyForReplace.length; ++j) {
                    text = text.replace((CharSequence)keyForReplace[j], (CharSequence)args[j]);
                }
            }
        }
        return text;
    }
    
    private static String replaceArgInText(String data, final Object[] arguments, final boolean isLink, final boolean isPlainText) throws Exception {
        if (isLink) {
            if (isPlainText) {
                if (arguments != null && arguments.length > 0) {
                    data = data.replaceAll("</?l\\d+>", "");
                }
            }
            else {
                for (int index = 0; index < arguments.length; ++index) {
                    final String anchorString = getAnchorText((String)arguments[index]);
                    data = data.replace("<l" + index + ">", anchorString);
                    data = data.replace("</l" + index + ">", "</a>");
                }
            }
            return data;
        }
        return MessageFormat.format(data, arguments);
    }
    
    private static String getAnchorText(final String link) {
        return "<a href=\"" + link + "\" target=\"_blank\" class=\"blueTxt\">";
    }
    
    private static String extractPlainText(final String textWithArg, final String delimiter) {
        final int indexOfFirstDelimiter = textWithArg.indexOf(delimiter);
        if (indexOfFirstDelimiter != -1) {
            return textWithArg.substring(0, indexOfFirstDelimiter);
        }
        return textWithArg;
    }
    
    public static String getI18Nmsg(final String i18nKey) {
        try {
            return I18N.getMsg(i18nKey, new Object[0]);
        }
        catch (final Exception e) {
            MDMI18N.LOGGER.log(Level.SEVERE, null, e);
            return i18nKey;
        }
    }
    
    public static String getI18Nmsg(final String i18nKey, final Object[] args) {
        try {
            return I18N.getMsg(i18nKey, args);
        }
        catch (final Exception e) {
            MDMI18N.LOGGER.log(Level.SEVERE, null, e);
            return i18nKey;
        }
    }
    
    static {
        LOGGER = Logger.getLogger("MDMLogger");
    }
}
