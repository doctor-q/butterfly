package cc.doctor.search.server.index.store.indices.inverted;

import cc.doctor.search.server.common.config.GlobalConfig;
import cc.doctor.search.server.common.config.Settings;
import cc.doctor.search.server.index.store.indices.indexer.IndexerMediator;
import cc.doctor.search.server.index.store.mm.MmapFile;
import cc.doctor.search.common.entity.Tuple;
import cc.doctor.search.common.utils.FileUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by doctor on 2017/3/29.
 * 词典文件,每个分片一个词典
 */
public class DictFile {
    private IndexerMediator indexerMediator;
    private MmapFile mmapFile;
    public static final int dictFileSize = Settings.settings.getInt(GlobalConfig.DICT_FILE_SIZE);
    private MmapFile fieldPositionFile;
    private static final int fieldPositionFileSize = 10 * 1024 * 1024;
    private HashMap<String, Tuple<Integer, Integer>> fieldPosition = new HashMap<>();
    private int position = 0;

    public DictFile(IndexerMediator indexerMediator) {
        this.indexerMediator = indexerMediator;
        try {
            String dictIndexFilePath = indexerMediator.getShardRoot() + "/" + GlobalConfig.DICT_INDEX_FILE_NAME;
            if (!FileUtils.exists(dictIndexFilePath)) {
                FileUtils.createFileRecursion(dictIndexFilePath);
            }
            fieldPositionFile = new MmapFile(dictIndexFilePath, fieldPositionFileSize);
            String dictFilePath = indexerMediator.getShardRoot() + "/" + GlobalConfig.DICT_FILE_NAME;
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
