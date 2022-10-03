package jdk.internal.dynalink.linker;

public interface TypeBasedGuardingDynamicLinker extends GuardingDynamicLinker
{
    boolean canLinkType(final Class<?> p0);
}
