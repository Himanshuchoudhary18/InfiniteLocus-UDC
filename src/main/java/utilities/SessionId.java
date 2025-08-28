package utilities;

import java.util.concurrent.ThreadLocalRandom;

public class SessionId {
    private static final char[] CANDIDATES = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789".toCharArray();
    private static final int LENGTH = 10;
    public static String random() {
        StringBuilder sb = new StringBuilder(LENGTH);
        for (int i = 0; i < LENGTH; i++) {
            sb.append(CANDIDATES[ThreadLocalRandom.current().nextInt(CANDIDATES.length)]);
        }
        return sb.toString();
    }
}
