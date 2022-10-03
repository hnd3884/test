package com.me.mdm.api.common.filter;

import com.fasterxml.jackson.databind.PropertyNamingStrategy;

public class UpperCaseStrategy extends PropertyNamingStrategy.PropertyNamingStrategyBase
{
    public String translate(final String input) {
        return input.toUpperCase();
    }
}
