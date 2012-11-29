package com.yahoo.research.bcn;

import static java.util.concurrent.TimeUnit.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Tracks the progress of your long running computation. Tracking is printed on a class Logger at INFO level.
 */
public class ProgressTracker {
    private static final Logger LOG = LoggerFactory.getLogger(ProgressTracker.class);
    private long tick;
    private int count, prevCount, totalIterations;

    /**
     * Builds a progress tracker that gives an estimate time of arrival for the computation, given the current speed.
     * 
     * @param estimatedIterations
     *            the expected number of iterations to run.
     */
    public ProgressTracker(int estimatedIterations) {
        tick = System.currentTimeMillis();
        count = prevCount = 0;
        totalIterations = estimatedIterations;
    }

    /**
     * Logs the progress.
     */
    public void progress() {
        count++;
        if (count % 1000 == 0 && LOG.isInfoEnabled()) {
            final double percent = 100 * (count / (double) totalIterations);
            final long tock = System.currentTimeMillis();
            final long timeInterval = tock - tick;
            final long linesPerSec = (count - prevCount) * 1000 / timeInterval;
            tick = tock;
            prevCount = count;
            final int etaSeconds = (int) ((totalIterations - count) / linesPerSec);
            final long hours = SECONDS.toHours(etaSeconds);
            final long minutes = SECONDS.toMinutes(etaSeconds - HOURS.toSeconds(hours));
            final long seconds = SECONDS.toSeconds(etaSeconds - MINUTES.toSeconds(minutes));
            LOG.info(String.format("[%3.0f%%] Completed %d iterations of %d total input. %d iters/s. ETA %02d:%02d:%02d", percent, count, totalIterations,
                    linesPerSec, hours, minutes, seconds));
        }
    }
}
