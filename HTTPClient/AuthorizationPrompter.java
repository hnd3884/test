package HTTPClient;

public interface AuthorizationPrompter
{
    NVPair getUsernamePassword(final AuthorizationInfo p0, final boolean p1);
}
