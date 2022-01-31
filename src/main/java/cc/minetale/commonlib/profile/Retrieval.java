package cc.minetale.commonlib.profile;

public record Retrieval(Response response, Profile profile) {

    public static Retrieval NOT_FOUND = new Retrieval(Response.NOT_FOUND, null);
    public static Retrieval FAILED = new Retrieval(Response.FAILURE, null);

    public enum Response {
        RETRIEVED,
        NOT_FOUND,
        FAILURE
    }

}
