package ua_parser;

import java.util.Map;

public class UserAgent
{
    public static final UserAgent OTHER;
    public final String family;
    public final String major;
    public final String minor;
    public final String patch;
    
    public UserAgent(final String family, final String major, final String minor, final String patch) {
        this.family = family;
        this.major = major;
        this.minor = minor;
        this.patch = patch;
    }
    
    public static UserAgent fromMap(final Map<String, String> m) {
        return new UserAgent(m.get("family"), m.get("major"), m.get("minor"), m.get("patch"));
    }
    
    @Override
    public boolean equals(final Object other) {
        if (other == this) {
            return true;
        }
        if (!(other instanceof UserAgent)) {
            return false;
        }
        final UserAgent o = (UserAgent)other;
        return ((this.family != null && this.family.equals(o.family)) || this.family == o.family) && ((this.major != null && this.major.equals(o.major)) || this.major == o.major) && ((this.minor != null && this.minor.equals(o.minor)) || this.minor == o.minor) && ((this.patch != null && this.patch.equals(o.patch)) || this.patch == o.patch);
    }
    
    @Override
    public int hashCode() {
        int h = (this.family == null) ? 0 : this.family.hashCode();
        h += ((this.major == null) ? 0 : this.major.hashCode());
        h += ((this.minor == null) ? 0 : this.minor.hashCode());
        h += ((this.patch == null) ? 0 : this.patch.hashCode());
        return h;
    }
    
    @Override
    public String toString() {
        return String.format("{\"family\": %s, \"major\": %s, \"minor\": %s, \"patch\": %s}", (this.family == null) ? "\"\"" : ('\"' + this.family + '\"'), (this.major == null) ? "\"\"" : ('\"' + this.major + '\"'), (this.minor == null) ? "\"\"" : ('\"' + this.minor + '\"'), (this.patch == null) ? "\"\"" : ('\"' + this.patch + '\"'));
    }
    
    static {
        OTHER = new UserAgent("Other", null, null, null);
    }
}
