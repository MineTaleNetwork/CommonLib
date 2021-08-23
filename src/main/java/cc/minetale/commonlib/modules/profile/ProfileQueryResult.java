package cc.minetale.commonlib.modules.profile;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum ProfileQueryResult {
    RETRIEVED(true),
    UPDATED_PROFILE(true),
    CREATED_PROFILE(true),
    PROFILE_EXISTS(false),
    NOT_FOUND(false),
    FAILURE(false);

    private final boolean successful;
}
