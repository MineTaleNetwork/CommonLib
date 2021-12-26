package cc.minetale.commonlib.util;

import cc.minetale.pigeon.Pigeon;
import cc.minetale.pigeon.PostalUnit;
import cc.minetale.pigeon.payloads.bases.BasePayload;
import lombok.AllArgsConstructor;
import lombok.Getter;

public class PigeonUtil {

    public static void sendTo(BasePayload payload, PostalUnit target) {
        Pigeon.getPigeon().sendTo(payload, target);
    }

    public static void broadcast(BasePayload payload) {
        Pigeon.getPigeon().broadcast(payload);
    }

    @Getter @AllArgsConstructor
    public enum GeneralUnits {
        BLITZ(new PostalUnit("blitz"));

        private final PostalUnit unit;
    }

}
