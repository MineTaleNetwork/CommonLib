package cc.minetale.commonlib.party;

import java.util.UUID;

public record PartyRequest(UUID partyUuid, UUID playerUuid, long ttl) {}
