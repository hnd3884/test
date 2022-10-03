package com.sun.beans.editors;

import java.beans.PropertyEditorSupport;

public class StringEditor extends PropertyEditorSupport
{
    @Override
    public String getJavaInitializationString() {
        final Object value = this.getValue();
        if (value == null) {
            return "null";
        }
        final String string = value.toString();
        final int length = string.length();
        final StringBuilder sb = new StringBuilder(length + 2);
        sb.append('\"');
        for (int i = 0; i < length; ++i) {
            final char char1 = string.charAt(i);
            switch (char1) {
                case 8: {
                    sb.append("\\b");
                    break;
                }
                case 9: {
                    sb.append("\\t");
                    break;
                }
                case 10: {
                    sb.append("\\n");
                    break;
                }
                case 12: {
                    sb.append("\\f");
                    break;
                }
                case 13: {
                    sb.append("\\r");
                    break;
                }
                case 34: {
                    sb.append("\\\"");
                    break;
                }
                case 92: {
                    sb.append("\\\\");
                    break;
                }
                default: {
                    if (char1 < ' ' || char1 > '~') {
                        sb.append("\\u");
                        final String hexString = Integer.toHexString(char1);
                        for (int j = hexString.length(); j < 4; ++j) {
                            sb.append('0');
                        }
                        sb.append(hexString);
                        break;
                    }
                    sb.append(char1);
                    break;
                }
            }
        }
        sb.append('\"');
        return sb.toString();
    }
    
    @Override
    public void setAsText(final String value) {
        this.setValue(value);
    }
}
