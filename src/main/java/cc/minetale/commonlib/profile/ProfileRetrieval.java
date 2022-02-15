package cc.minetale.commonlib.profile;

public record ProfileRetrieval(Response response, Profile profile) {

    public static ProfileRetrieval NOT_FOUND = new ProfileRetrieval(Response.NOT_FOUND, null);
    public static ProfileRetrieval FAILED = new ProfileRetrieval(Response.FAILURE, null);

    public enum Response {
        RETRIEVED,
        NOT_FOUND,
        FAILURE
    }

}
