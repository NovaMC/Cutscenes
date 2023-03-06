package xyz.novaserver.cutscenes.api.util;

public class FileUtils {

    public static String removeExtension(String fileName) {
        int lastPeriod = fileName.lastIndexOf('.');
        if (lastPeriod <= 0) {
            return fileName;
        } else {
            return fileName.substring(0, lastPeriod);
        }
    }
}
