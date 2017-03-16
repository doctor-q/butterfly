package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.common.Tuple;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件
 */
public abstract class InvertedFile {
    private static final Logger log = LoggerFactory.getLogger(InvertedFile.class);

    /**
     * 获取倒排文件列表
     *
     * @param invertedNode
     */
    public abstract InvertedTable getInvertedTable(WordInfo.InvertedNode invertedNode);

    public abstract boolean writeInvertedTable(InvertedTable invertedTable);

    /**
     * 合并倒排文件,因为写入的时候扩容后会生成不同版本号的倒排表,保留最新版本号的倒排表而删除原来的
     * todo 字符串前缀相同的倒排链保存在相邻位置
     */
    public void mergeInvertedTables(List<WordInfo.InvertedNode> invertedNodes) {
        //按position排序顺序读取
        TreeMap<Integer, WordInfo.InvertedNode> invertedNodeMap = new TreeMap<>();
        for (WordInfo.InvertedNode invertedNode : invertedNodes) {
            invertedNodeMap.put(invertedNode.getPosition(), invertedNode);
        }
        Map<Object, Tuple<Long, InvertedTable>> invertedTableMap = new HashMap<>();
        for (WordInfo.InvertedNode invertedNode : invertedNodeMap.values()) {
            InvertedTable invertedTable = getInvertedTable(invertedNode);
            //取版本号最大的
            Tuple<Long, InvertedTable> invertedTableTuple = invertedTableMap.get(invertedTable.getInvertedNode().getData());
            if (invertedTableTuple != null) {
                if (invertedTableTuple.getT1() < invertedTable.getInvertedNode().getVersion()) {
                    invertedTableMap.put(invertedTable.getInvertedNode().getData(), new Tuple<>(invertedTable.getInvertedNode().getVersion(), invertedTable));
                }
            } else {
                invertedTableMap.put(invertedTable.getInvertedNode().getData(), new Tuple<>(invertedTable.getInvertedNode().getVersion(), invertedTable));
            }
        }
        //// 重新计算新的位置,写入invertedNodes
        Map<Object, WordInfo.InvertedNode> wordInvertedNodeMap = new HashMap<>();
        for (WordInfo.InvertedNode invertedNode : invertedNodes) {
            wordInvertedNodeMap.put(invertedNode.getData(), invertedNode);
        }
        int position = 0;
        for (Tuple<Long, InvertedTable> invertedTableTuple : invertedTableMap.values()) {
            InvertedTable invertedTable = invertedTableTuple.getT2();
            invertedTable.getInvertedNode().setPosition(position);
            writeInvertedTable(invertedTable);
            WordInfo.InvertedNode invertedNode = wordInvertedNodeMap.get(invertedTable.getInvertedNode().getData());
            invertedNode.setPosition(position);
        }
    }
}
