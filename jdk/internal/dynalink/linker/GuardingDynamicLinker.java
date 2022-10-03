package jdk.internal.dynalink.linker;

public interface GuardingDynamicLinker
{
    GuardedInvocation getGuardedInvocation(final LinkRequest p0, final LinkerServices p1) throws Exception;
}
