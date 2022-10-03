package com.unboundid.util;

@ThreadSafety(level = ThreadSafetyLevel.COMPLETELY_THREADSAFE)
public enum HorizontalAlignment
{
    LEFT, 
    CENTER, 
    RIGHT;
    
    public void format(final StringBuilder buffer, final String text, final int width) {
        final int length = text.length();
        if (length >= width) {
            buffer.append(text.substring(0, width));
            return;
        }
        int spacesBefore = 0;
        int spacesAfter = 0;
        switch (this) {
            case LEFT: {
                spacesBefore = 0;
                spacesAfter = width - length;
                break;
            }
            case CENTER: {
                final int totalSpaces = width - length;
                spacesBefore = totalSpaces / 2;
                spacesAfter = totalSpaces - spacesBefore;
                break;
            }
            default: {
                spacesBefore = width - length;
                spacesAfter = 0;
                break;
            }
        }
        for (int i = 0; i < spacesBefore; ++i) {
            buffer.append(' ');
        }
        buffer.append(text);
        for (int i = 0; i < spacesAfter; ++i) {
            buffer.append(' ');
        }
    }
    
    public static HorizontalAlignment forName(final String name) {
        final String lowerCase = StaticUtils.toLowerCase(name);
        switch (lowerCase) {
            case "left": {
                return HorizontalAlignment.LEFT;
            }
            case "center": {
                return HorizontalAlignment.CENTER;
            }
            case "right": {
                return HorizontalAlignment.RIGHT;
            }
            default: {
                return null;
            }
        }
    }
}
