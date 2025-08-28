package utilities;

public class OSValidator {
    private static final ThreadLocal<String> shellType = new ThreadLocal<>();
    private static final ThreadLocal<String> delimiter = new ThreadLocal<>();

    public static void setPropValues(String OS) {
        if (isWindows(OS)) {
            shellType.set("cmd");
            delimiter.set("\\");
        } else if (isMac(OS)) {
            shellType.set("/bin/bash");
            delimiter.set("/");
        } else if (isUnix(OS)) {
            shellType.set("/bin/sh");
            delimiter.set("/");
        } else {
            shellType.set("cmd");
            delimiter.set("\\");
        }
    }

    public static String getShellType() {
        return shellType.get();
    }
    public static String getDelimiter() {
        return delimiter.get();
    }
    private static boolean isWindows(String OS) {
        return (OS.contains("win"));
    }

    private static boolean isMac(String OS) {
        return (OS.contains("mac"));
    }

    private static boolean isUnix(String OS) {
        return (OS.contains("nix") || OS.contains("nux") || OS.indexOf("aix") > 0);
    }
}
