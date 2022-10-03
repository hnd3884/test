package com.sun.beans.decoder;

final class CharElementHandler extends StringElementHandler
{
    @Override
    public void addAttribute(final String s, final String s2) {
        if (s.equals("code")) {
            final char[] chars = Character.toChars(Integer.decode(s2));
            for (int length = chars.length, i = 0; i < length; ++i) {
                this.addCharacter(chars[i]);
            }
        }
        else {
            super.addAttribute(s, s2);
        }
    }
    
    public Object getValue(final String s) {
        if (s.length() != 1) {
            throw new IllegalArgumentException("Wrong characters count");
        }
        return s.charAt(0);
    }
}
