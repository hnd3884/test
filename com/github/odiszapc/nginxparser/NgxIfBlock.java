package com.github.odiszapc.nginxparser;

public class NgxIfBlock extends NgxBlock
{
    @Override
    public String toString() {
        final String string = super.toString();
        final StringBuilder insert = new StringBuilder(string).insert(string.indexOf("if") + 2, " (");
        return insert.insert(insert.lastIndexOf("{"), ")").toString();
    }
}
