package javax.annotation.processing;

public class Completions
{
    private Completions() {
    }
    
    public static Completion of(final String s, final String s2) {
        return new SimpleCompletion(s, s2);
    }
    
    public static Completion of(final String s) {
        return new SimpleCompletion(s, "");
    }
    
    private static class SimpleCompletion implements Completion
    {
        private String value;
        private String message;
        
        SimpleCompletion(final String value, final String message) {
            if (value == null || message == null) {
                throw new NullPointerException("Null completion strings not accepted.");
            }
            this.value = value;
            this.message = message;
        }
        
        @Override
        public String getValue() {
            return this.value;
        }
        
        @Override
        public String getMessage() {
            return this.message;
        }
        
        @Override
        public String toString() {
            return "[\"" + this.value + "\", \"" + this.message + "\"]";
        }
    }
}
