package org.glassfish.hk2.utilities;

import org.glassfish.hk2.api.MultiException;
import org.glassfish.hk2.api.ErrorType;
import org.glassfish.hk2.api.ErrorInformation;
import javax.inject.Singleton;
import org.glassfish.hk2.api.ErrorService;

@Singleton
public class RethrowErrorService implements ErrorService
{
    @Override
    public void onFailure(final ErrorInformation errorInformation) throws MultiException {
        if (!ErrorType.FAILURE_TO_REIFY.equals(errorInformation.getErrorType())) {
            return;
        }
        final MultiException me = errorInformation.getAssociatedException();
        if (me == null) {
            return;
        }
        throw me;
    }
}
