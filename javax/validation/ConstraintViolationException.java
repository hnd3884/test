package javax.validation;

import java.util.stream.Collector;
import java.util.stream.Collectors;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class ConstraintViolationException extends ValidationException
{
    private final Set<ConstraintViolation<?>> constraintViolations;
    
    public ConstraintViolationException(final String message, final Set<? extends ConstraintViolation<?>> constraintViolations) {
        super(message);
        if (constraintViolations == null) {
            this.constraintViolations = null;
        }
        else {
            this.constraintViolations = new HashSet<ConstraintViolation<?>>(constraintViolations);
        }
    }
    
    public ConstraintViolationException(final Set<? extends ConstraintViolation<?>> constraintViolations) {
        this((constraintViolations != null) ? toString(constraintViolations) : null, constraintViolations);
    }
    
    public Set<ConstraintViolation<?>> getConstraintViolations() {
        return this.constraintViolations;
    }
    
    private static String toString(final Set<? extends ConstraintViolation<?>> constraintViolations) {
        return constraintViolations.stream().map(cv -> {
            String string;
            if (cv == null) {
                string = "null";
            }
            else {
                string = cv.getPropertyPath() + ": " + cv.getMessage();
            }
            return string;
        }).collect((Collector<? super Object, ?, String>)Collectors.joining(", "));
    }
}
