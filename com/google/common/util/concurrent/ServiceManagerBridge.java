package com.google.common.util.concurrent;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.annotations.GwtIncompatible;

@ElementTypesAreNonnullByDefault
@GwtIncompatible
interface ServiceManagerBridge
{
    ImmutableMultimap<Service.State, Service> servicesByState();
}
