package cc.minetale.commonlib.util;

import org.apache.commons.lang3.RandomStringUtils;

public class Util {

    public static String generateId() {
        return RandomStringUtils.randomAlphanumeric(8).toUpperCase();
    }

}
