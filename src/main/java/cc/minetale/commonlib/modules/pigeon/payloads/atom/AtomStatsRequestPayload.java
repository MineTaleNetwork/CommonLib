package cc.minetale.commonlib.modules.pigeon.payloads.atom;

import cc.minetale.commonlib.modules.network.Server;
import cc.minetale.pigeon.annotations.Payload;
import cc.minetale.pigeon.annotations.RequestConstructor;
import cc.minetale.pigeon.annotations.ResponseConstructor;
import cc.minetale.pigeon.annotations.Transmit;
import cc.minetale.pigeon.feedback.FeedbackState;
import cc.minetale.pigeon.feedback.RequiredState;
import cc.minetale.pigeon.payloads.bases.FeedbackPayload;
import com.sun.management.OperatingSystemMXBean;
import lombok.Getter;

import java.lang.management.ManagementFactory;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;


@Getter @Payload
public class AtomStatsRequestPayload extends FeedbackPayload {

    public AtomStatsRequestPayload() {
        this.payloadId = "atomStatsRequestPayload";
        this.payloadTimeout = 10000;
    }

    @RequestConstructor
    public AtomStatsRequestPayload(Consumer<AtomStatsRequestPayload> feedback) {
        this();
        this.payloadState = FeedbackState.REQUEST;
        this.feedbackID = UUID.randomUUID();

        this.feedback = feedback;
    }

    @Transmit(direction = RequiredState.RESPONSE) Long latency;
    @Transmit(direction = RequiredState.RESPONSE) Long uptime;
    @Transmit(direction = RequiredState.RESPONSE) Long usedMemory;
    @Transmit(direction = RequiredState.RESPONSE) Double usedCpu;
    @Transmit(direction = RequiredState.RESPONSE) List<Server> servers;

    @ResponseConstructor
    public AtomStatsRequestPayload(Long latency) {
        this();
        this.payloadState = FeedbackState.RESPONSE;

        this.latency = latency;
        this.uptime = ManagementFactory.getRuntimeMXBean().getUptime();
        this.usedMemory = Runtime.getRuntime().maxMemory() - Runtime.getRuntime().freeMemory();
        this.usedCpu = ManagementFactory.getPlatformMXBean(OperatingSystemMXBean.class).getProcessCpuLoad();
        this.servers = Server.serverList;
    }

    @Override
    public void receive() {}

}
