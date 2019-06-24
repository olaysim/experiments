package dk.syslab.proxy.util;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.AbstractMatcherFilter;
import ch.qos.logback.core.spi.FilterReply;
import org.slf4j.Marker;
import org.slf4j.MarkerFactory;

public class MarkerFilter extends AbstractMatcherFilter<ILoggingEvent> {

    Marker markerToMatch;

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (!isStarted()) {
            return FilterReply.NEUTRAL;
        }

        Marker marker = event.getMarker();
        if (marker == null) {
            return onMismatch;
        }
        if (marker.contains(markerToMatch)) {
            return onMatch;
        } else {
            return onMismatch;
        }
    }


    public void setMarker(String marker) {
        if (marker != null) {
            markerToMatch = MarkerFactory.getMarker(marker);
        }
    }

    public void start() {
        if (this.markerToMatch != null) {
            super.start();
        }
    }
}
