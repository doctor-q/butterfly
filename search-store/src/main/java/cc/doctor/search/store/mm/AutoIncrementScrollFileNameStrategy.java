package cc.doctor.search.store.mm;

import java.util.List;

public class AutoIncrementScrollFileNameStrategy implements ScrollFileNameStrategy {
    public static final AutoIncrementScrollFileNameStrategy autoIncrementScrollFileNameStrategy = new AutoIncrementScrollFileNameStrategy();
    private static final int FILE_NAME_LENGTH = 5;

    private AutoIncrementScrollFileNameStrategy() {
    }

    @Override
    public String first() {
        return createFileName(0);
    }

    @Override
    public String next(String current) {
        int next = Integer.parseInt(current) + 1;
        return createFileName(next);
    }

    @Override
    public String getFileByPosition(long position, int scrollSize) {
        int id = (int) (position / scrollSize);
        return createFileName(id);
    }

    @Override
    public String last(List<String> files) {
        if (files == null || files.isEmpty()) {
            return null;
        }
        files.sort(String::compareTo);
        return files.get(files.size() - 1);
    }

    /**
     * padding file id
     */
    private String createFileName(int id) {
        int fileNumber = id;
        int num = 1;
        while ((fileNumber = fileNumber / 10) != 0) {
            num++;
        }
        if (num > FILE_NAME_LENGTH) {
            return createFileName(0);   //rollback
        }
        StringBuilder stringBuilder = new StringBuilder();
        for (int i = 0; i < FILE_NAME_LENGTH - num; i++) {
            stringBuilder.append("0");
        }
        return stringBuilder.append(id).toString();
    }
}
