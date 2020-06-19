package cc.doctor.search.store.indices.inverted;

import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.utils.FileUtils;
import cc.doctor.search.store.StoreConfigs;
import cc.doctor.search.store.indices.indexer.IndexerService;
import cc.doctor.search.store.mm.MmapFile;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by doctor on 2017/3/29.
 * 词典文件,每个分片一个词典
 */
public class DictFile {
    private IndexerService indexerService;
    private MmapFile mmapFile;
    public static final int dictFileSize = StoreConfigs.DICT_FILE_SIZE_DEFAULT;
    private MmapFile fieldPositionFile;
    private static final int fieldPositionFileSize = 10 * 1024 * 1024;
    private HashMap<String, Tuple<Integer, Integer>> fieldPosition = new HashMap<>();
    private int position = 0;

    public DictFile(IndexerService indexerService) {
        this.indexerService = indexerService;
        try {
            String dictIndexFilePath = StoreConfigs.DICT_INDEX_FILE_NAME;
            if (!FileUtils.exists(dictIndexFilePath)) {
                FileUtils.createFileRecursion(dictIndexFilePath);
            }
            fieldPositionFile = new MmapFile(dictIndexFilePath, fieldPositionFileSize);
            String dictFilePath = StoreConfigs.DICT_FILE_NAME;
            if (!FileUtils.exists(dictFilePath)) {
                FileUtils.createFileRecursion(dictFilePath);
            }
            mmapFile = new MmapFile(dictFilePath, dictFileSize);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public HashMap<String, Tuple<Integer, Integer>> getFieldPosition() {
        return fieldPosition;
    }

    public <T extends Serializable> void writeDict(Map<String, T> fieldDict) {
        for (String field : fieldDict.keySet()) {
            int length = mmapFile.writeObject(fieldDict.get(field));
            fieldPosition.put(field, new Tuple<>(position, length));
            position += length;
        }
    }

    public void flushFieldPosition() {
        fieldPositionFile.writeObject(fieldPosition);
        fieldPositionFile.commit();
    }

    public void readFieldPosition() {
        fieldPosition = fieldPositionFile.readObject(0, fieldPositionFileSize);
    }

    public <T extends Serializable> Map<String, T> readDict() {
        Map<String, T> fieldDict = new HashMap<>();
        if (fieldPosition.size() == 0) {
            readFieldPosition();
        }
        if (fieldPosition != null) {
            for (String field : fieldPosition.keySet()) {
                Tuple<Integer, Integer> positionLength = fieldPosition.get(field);
                T t = mmapFile.readObject(positionLength.getT1(), positionLength.getT2());
                fieldDict.put(field, t);
            }
        } else {    //init dict index
            fieldPosition = new HashMap<>();
        }
        return fieldDict;
    }
}
