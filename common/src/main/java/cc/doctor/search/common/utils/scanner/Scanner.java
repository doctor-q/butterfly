package cc.doctor.search.common.utils.scanner;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.net.URL;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by doctor on 2017/3/12.
 */
public class Scanner {
    private static final Logger log = LoggerFactory.getLogger(Scanner.class);
    private ConcurrentHashMap<String, Class> scanClass = new ConcurrentHashMap<>();
    public static final URL base = Scanner.class.getClass().getResource("/");
    private String scanPackage;

    public Scanner(String scanPackage) {
        this.scanPackage = scanPackage;
    }

    public ConcurrentHashMap<String, Class> getScanClass() {
        return scanClass;
    }

    public void doScan() {
        loadPackage(scanPackage);
    }

    public void loadPackage(String packageName) {
        String packagePath = base.getPath() + packageName.replace(".", "/");
        loadPath(packagePath, packageName);
    }

    public void loadPath(String packagePath, String packageName) {
        File pack = new File(packagePath);
        if (!pack.exists() || pack.isFile()) {
            return;
        }
        String[] files = pack.list();
        if (files == null) {
            return;
        }
        for (String file : files) {
            File subFile = new File(packagePath + "/" + file);
            if (subFile.exists()) {
                if (subFile.isFile()) {
                    String className = file.substring(0, file.indexOf("."));
                    try {
                        Class<?> aClass = Class.forName(packageName + "." + className);
                        scanClass.put(packageName + "." + className, aClass);
                    } catch (ClassNotFoundException e) {
                        log.error("", e);
                    }
                } else {
                    loadPackage(packageName + "." + file);
                }
            }
        }
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner("cc.doctor.wiki.schedule");
        scanner.doScan();
        System.out.println(scanner.getScanClass());
    }
}
