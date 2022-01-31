package cc.minetale.commonlib.util;

import java.util.UUID;

public record Request(UUID initiator, UUID target, long ttl) {}
