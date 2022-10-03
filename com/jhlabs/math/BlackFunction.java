package com.jhlabs.math;

public class BlackFunction implements BinaryFunction
{
    public boolean isBlack(final int rgb) {
        return rgb == -16777216;
    }
}
