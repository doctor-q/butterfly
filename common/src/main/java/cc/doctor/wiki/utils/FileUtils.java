package cc.doctor.wiki.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by doctor on 2017/3/9.
 */
public class FileUtils {
    private static final Logger log = LoggerFactory.getLogger(FileUtils.class);

    public static boolean createFileRecursion(String absolutePath) {
        if (absolutePath == null) {
            return false;
        }
        createDirectoryRecursion(absolutePath.substring(0, absolutePath.lastIndexOf("/")));
        File file = new File(absolutePath);
        if (file.exists()) {
            log.warn("File exist:{}", absolutePath);
            return false;
        } else {
            try {
                return file.createNewFile();
            } catch (IOException e) {
                log.error("", e);
                return false;
            }
        }
    }

    public static boolean createDirectoryRecursion(String absolutePath) {
        if (absolutePath == null) {
            return false;
        }
        String[] dirs = absolutePath.split("/");
        StringBuilder currentDir = new StringBuilder("/");
        for (int i = 0; i < dirs.length; i++) {
            currentDir.append(dirs[i]);
            File file = new File(currentDir.toString());
            if (!file.exists() || !file.isDirectory()) {
                if (!file.mkdir()) {
                    log.warn("Make dir error:{}", currentDir);
                    return false;
                }
            }
            currentDir.append("/");
        }
        return true;
    }

    public static void main(String[] args) {
        FileUtils.dropDirectory("/tmp/es/data/order");
    }

    public static boolean dropDirectory(String directory) {
        if (directory == null) {
            return false;
        }
        File file = new File(directory);
        if (!file.exists()) {
            return true;
        } else if (file.isFile()) {
            return file.delete();
        } else {
            String[] files = file.list();
            if (files != null) {
                for (String fileName : files) {
                    dropDirectory(directory + "/" + fileName);
                }
                return file.delete();
            }
            return false;
        }
    }

    public static boolean exists(String fileName) {
        if (fileName == null) {
            return false;
        }
        File file = new File(fileName);
        return file.exists() && !file.isDirectory();
    }

    public static List<String> list(String dir, String except) {
        LinkedList<String> list = new LinkedList<>();
        if (dir == null) {
            return new LinkedList<>();
        }
        File file = new File(dir);
        if (file.exists() && file.isDirectory()) {
            String[] files = file.list();
            if (files != null) {
                for (String fileName : files) {
                    if (!fileName.equals(except)) {
                        list.add(fileName);
                    }
                }
                return list;
            }
        }
        return list;
    }
}
