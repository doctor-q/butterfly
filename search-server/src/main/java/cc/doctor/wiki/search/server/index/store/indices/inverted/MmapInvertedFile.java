package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.common.Action;
import cc.doctor.wiki.search.server.common.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.store.mm.MmapScrollFile;
import cc.doctor.wiki.search.server.index.store.mm.ScrollFile;
import cc.doctor.wiki.utils.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.TreeMap;

import static cc.doctor.wiki.search.server.common.config.Settings.settings;
import static cc.doctor.wiki.search.server.index.store.mm.ScrollFile.AutoIncrementScrollFileNameStrategy.autoIncrementScrollFileNameStrategy;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件,每个域拥有一个倒排
 */
public class MmapInvertedFile extends InvertedFile {
    private static final Logger log = LoggerFactory.getLogger(InvertedFile.class);
    private ScrollFile scrollFile;
    private ScrollFile.ScrollFileNameStrategy scrollFileNameStrategy = autoIncrementScrollFileNameStrategy;

    MmapInvertedFile() {
        scrollFile = new MmapScrollFile(settings.get(GlobalConfig.INVERTED_FILE_NAME),
                Integer.parseInt(settings.get(GlobalConfig.INVERTED_FILE_SIZE_NAME)), autoIncrementScrollFileNameStrategy);
        scrollFile.onWriteFileCheck(new Action() {
            @Override
            public void doAction() {
                flushInvertedTable();
            }
        });
    }

    @Override
    public InvertedTable getInvertedTable(WordInfo.InvertedNode invertedNode) {
        return (InvertedTable) scrollFile.readSerializable(invertedNode.getPosition()).getT2();
    }

    @Override
    public long writeInvertedTable(InvertedTable invertedTable) {
        return scrollFile.writeSerializable(invertedTable);
    }

    @Override
    public void flushInvertedTable() {

    }

    /**
     * 合并倒排文件,因为写入的时候扩容后会生成不同版本号的倒排表,保留最新版本号的倒排表而删除原来的
     * 取出除了当前写文件所有的文件进行合并,重新生成新的文件
     * todo 字符串前缀相同的倒排链保存在相邻位置
     */
    public void mergeInvertedTables(List<WordInfo.InvertedNode> invertedNodes) {
        ScrollFile newScrollFile = new MmapScrollFile(scrollFile.root() + "/new", scrollFile.scrollSize(), scrollFileNameStrategy);
        List<String> files = scrollFile.files();
        int mergeFileNum = files.size() - 1;
        long maxPosition = mergeFileNum * scrollFile.scrollSize();
        //按position排序顺序读取
        TreeMap<Long, WordInfo.InvertedNode> invertedNodeMap = new TreeMap<>();
        for (WordInfo.InvertedNode invertedNode : invertedNodes) {
            invertedNodeMap.put(invertedNode.getPosition(), invertedNode);
        }
        for (WordInfo.InvertedNode invertedNode : invertedNodeMap.values()) {
            if (invertedNode.getPosition() < maxPosition) {
                InvertedTable invertedTable = getInvertedTable(invertedNode);
                long position = newScrollFile.writeSerializable(invertedTable);
                invertedNode.setPosition(position);
            }
        }
        //lock
        scrollFile.readLock();
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
        scrollFile = new MmapScrollFile(scrollFile.root(), scrollFile.scrollSize(), scrollFileNameStrategy,
                (files.size() - mergeFileNum) * scrollFile.scrollSize() + scrollFile.position() % scrollFile.scrollSize());
    }
}
