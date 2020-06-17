package cc.doctor.search.store.indices.inverted;

import cc.doctor.search.common.entity.Action;
import cc.doctor.search.common.utils.FileUtils;
import cc.doctor.search.store.StoreConfigs;
import cc.doctor.search.store.indices.indexer.IndexerMediator;
import cc.doctor.search.store.mm.MmapScrollFile;
import cc.doctor.search.store.mm.ScrollFileNameStrategy;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;

import static cc.doctor.search.store.mm.AutoIncrementScrollFileNameStrategy.autoIncrementScrollFileNameStrategy;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件,每个域拥有一个倒排
 */
@Slf4j
public class MmapInvertedFile extends InvertedFile {
    private MmapScrollFile scrollFile;
    private ScrollFileNameStrategy scrollFileNameStrategy = autoIncrementScrollFileNameStrategy;
    private List<InvertedTable> memInvertedTables = new LinkedList<>();
    private static AtomicInteger memInvertedTableNum = new AtomicInteger();
    public static final int flushTableNum = StoreConfigs.FLUSH_INVERTED_TABLE_NUM_DEFAULT;

    public MmapInvertedFile(IndexerMediator indexerMediator) {
        super(indexerMediator);
        scrollFile = new MmapScrollFile(StoreConfigs.INVERTED_FILE_PATH_NAME,
                StoreConfigs.INVERTED_FILE_SIZE_DEFAULT, autoIncrementScrollFileNameStrategy);
        scrollFile.onWriteFileCheck(new Action() {
            @Override
            public void doAction() {
                flushInvertedTable();
            }
        });
    }

    @Override
    public InvertedTable getInvertedTable(WordInfo wordInfo) {
        return (InvertedTable) scrollFile.readObject(wordInfo.getPosition(), 0);
    }

    /**
     * 写倒排表,先写到缓存,如果有倒排表被换出,将被换出的倒排表加入待flush的列表中,如果待flush的倒排表数量达到上限则flush到磁盘
     *
     * @param invertedTable 倒排表
     */
    @Override
    public void writeInvertedTable(InvertedTable invertedTable) {
        InvertedTable removedInvertedTable = invertedTableCache.put(invertedTable.getWordInfo().getPosition(), invertedTable);
        if (removedInvertedTable != null) {
            if (memInvertedTableNum.incrementAndGet() == flushTableNum) {
                flushInvertedTable();
            } else {
                memInvertedTables.add(removedInvertedTable);
            }
        }
    }

    @Override
    public void flushInvertedTable() {
        Collections.sort(memInvertedTables, new Comparator<InvertedTable>() {
            @Override
            public int compare(InvertedTable o1, InvertedTable o2) {
                return ((Long) o1.getWordInfo().getPosition()).compareTo(o2.getWordInfo().getPosition());
            }
        });
        for (InvertedTable mmInvertedTable : memInvertedTables) {
            long position = scrollFile.writeObject(mmInvertedTable);
            WordInfo wordInfo = mmInvertedTable.getWordInfo();
            wordInfo.setPosition(position);
            updateWordInfo(mmInvertedTable.getField(), wordInfo);
        }
        memInvertedTables.clear();
        memInvertedTableNum.set(0);
    }

    /**
     * 合并倒排文件,因为写入的时候扩容后会生成不同版本号的倒排表,保留最新版本号的倒排表而删除原来的
     * 取出除了当前写文件所有的文件进行合并,重新生成新的文件
     * todo 字符串前缀相同的倒排链保存在相邻位置
     */
    public void mergeInvertedTables(List<WordInfo> wordInfos) {
        MmapScrollFile newScrollFile = new MmapScrollFile(scrollFile.root() + "/new", scrollFile.scrollSize(), scrollFileNameStrategy);
        List<String> files = scrollFile.files();
        int mergeFileNum = files.size() - 1;
        long maxPosition = mergeFileNum * scrollFile.scrollSize();
        //按position排序顺序读取
        TreeMap<Long, WordInfo> wordInfoMap = new TreeMap<>();
        for (WordInfo wordInfo : wordInfos) {
            wordInfoMap.put(wordInfo.getPosition(), wordInfo);
        }
        for (WordInfo wordInfo : wordInfoMap.values()) {
            if (wordInfo.getPosition() < maxPosition) {
                InvertedTable invertedTable = getInvertedTable(wordInfo);
                long position = newScrollFile.writeObject(invertedTable);
                wordInfo.setPosition(position);
            }
        }
        //删除合并过的文件
        for (int i = 0; i < files.size() - 1; i++) {
            FileUtils.removeFile(scrollFile.root() + "/" + files.get(i));
        }
        //move 合并生成的文件到当前目录
        List<String> newScrollFiles = newScrollFile.files();
        for (String file : newScrollFiles) {
            FileUtils.move(scrollFile.root() + "/new/" + file, scrollFile.root());
        }
        //设置当前写的文件,重新计算position
        String current = newScrollFiles.get(newScrollFiles.size() - 1);
        for (int i = mergeFileNum; i < files.size(); i++) {
            String next = scrollFileNameStrategy.next(current);
            FileUtils.move(scrollFile.root() + "/" + scrollFile.files().get(i), scrollFile.root() + "/" + next);
            current = next;
        }
        scrollFile = new MmapScrollFile(scrollFile.root(), scrollFile.scrollSize(), scrollFileNameStrategy);
    }
}
