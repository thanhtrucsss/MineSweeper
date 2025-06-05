package src;

import java.time.Duration;
import java.time.Instant;

public class Time {

    private Instant signalA;
    private Instant signalB;

    public void receiveSignalA() {                          
        signalA = Instant.now();
        System.out.println("Received signal A at: " + signalA);
    }

    public void receiveSignalB() {                          
        signalB = Instant.now();
        System.out.println("Received signal B at: " + signalB);
    }

    public Duration calculateTimeDifference() {         
        if (signalA != null && signalB != null) {
            return Duration.between(signalA, signalB);
        } else {
            throw new IllegalStateException("Both signals have not been received yet");
        }
    }

    public long getTimeDifferenceInSeconds() {          
        Duration timeDifference = calculateTimeDifference();
        return timeDifference.toSeconds();
    }
}
