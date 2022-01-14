package cc.minetale.commonlib.util;

import lombok.Getter;

import java.text.DecimalFormat;
import java.time.Duration;

@Getter
public class Cooldown {

    private final Duration duration;
    private long lastUpdate;

    private static final DecimalFormat format = new DecimalFormat("0.00");

    public Cooldown(Duration duration) {
        this.duration = duration;
        this.lastUpdate = 0L;
    }

    public void refresh() {
        this.lastUpdate = System.currentTimeMillis();
    }

    public boolean isReady() {
        return !hasCooldown();
    }

    public String getSecondsRemaining() {
        return format.format(getMillisRemaining() / 1000.0);
    }

    public long getMillisRemaining() {
        return (this.lastUpdate + this.duration.toMillis()) - System.currentTimeMillis();
    }

    public boolean hasCooldown() {
        return getMillisRemaining() > 0;
    }

}
