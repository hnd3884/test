package org.apache.commons.math3.ml.neuralnet.sofm;

import java.util.Iterator;
import org.apache.commons.math3.ml.neuralnet.Network;

public class KohonenTrainingTask implements Runnable
{
    private final Network net;
    private final Iterator<double[]> featuresIterator;
    private final KohonenUpdateAction updateAction;
    
    public KohonenTrainingTask(final Network net, final Iterator<double[]> featuresIterator, final KohonenUpdateAction updateAction) {
        this.net = net;
        this.featuresIterator = featuresIterator;
        this.updateAction = updateAction;
    }
    
    public void run() {
        while (this.featuresIterator.hasNext()) {
            this.updateAction.update(this.net, this.featuresIterator.next());
        }
    }
}
