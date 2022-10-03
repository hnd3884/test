package com.octo.captcha.component.word.worddecorator;

public class SpellerWordDecorator implements WordDecorator
{
    private String separtor;
    
    public SpellerWordDecorator(final String separtor) {
        this.separtor = separtor;
    }
    
    public String decorateWord(final String s) {
        final StringBuffer sb = new StringBuffer();
        for (int i = 0; i < s.length(); ++i) {
            sb.append(" ");
            sb.append(s.charAt(i));
            if (i < s.length() - 1) {
                sb.append(this.separtor);
            }
        }
        return sb.toString();
    }
}
