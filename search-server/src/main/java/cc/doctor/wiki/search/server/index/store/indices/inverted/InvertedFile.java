package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.common.Tuple;
import cc.doctor.wiki.exceptions.index.IndexException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by doctor on 2017/3/7.
 * 倒排文件
 */
public abstract class InvertedFile {
    private static final Logger log = LoggerFactory.getLogger(InvertedFile.class);
    //使用fork-join框架进行doc合并
    private ForkJoinPool forkJoinPool = new ForkJoinPool();

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

    /**
     * 合并倒排链
     *
     * @param iterables 倒排链列表
     * @return 合并后的倒排链列表
     */
    public Iterable<InvertedTable.InvertedDoc> mergeDocs(List<Iterable<InvertedTable.InvertedDoc>> iterables) {
        ForkJoinTask<Iterable<InvertedTable.InvertedDoc>> mergeDocTask = forkJoinPool.submit(new MergeDocTask(iterables));
        try {
            return mergeDocTask.get();
        } catch (Exception e) {
            log.error("", e);
            throw new IndexException(e);
        }
    }

    /**
     * 倒排链合并,使用forkjoin框架
     */
    public class MergeDocTask extends RecursiveTask<Iterable<InvertedTable.InvertedDoc>> {
        List<Iterable<InvertedTable.InvertedDoc>> iterables;

        public MergeDocTask(List<Iterable<InvertedTable.InvertedDoc>> iterables) {
            this.iterables = iterables;
        }

        private static final long serialVersionUID = 5382360215324978123L;

        @Override
        protected Iterable<InvertedTable.InvertedDoc> compute() {
            if (iterables.size() == 1) {
                return iterables.get(0);
            } else if (iterables.size() == 2) {
                return merge(iterables.get(0), iterables.get(1));
            } else {
                MergeDocTask leftMergeDocTask = new MergeDocTask(iterables.subList(0, iterables.size() / 2));
                MergeDocTask rightMergeDocTask = new MergeDocTask(iterables.subList(iterables.size() / 2, iterables.size()));
                leftMergeDocTask.fork();
                rightMergeDocTask.fork();
                Iterable<InvertedTable.InvertedDoc> leftDocs = leftMergeDocTask.join();
                Iterable<InvertedTable.InvertedDoc> rightDocs = rightMergeDocTask.join();
                return merge(leftDocs, rightDocs);
            }
        }
    }

    protected Iterable<InvertedTable.InvertedDoc> merge(Iterable<InvertedTable.InvertedDoc> left, Iterable<InvertedTable.InvertedDoc> right) {
        final Map<Long, Long> docFrequencyMap = new HashMap<>();
        for (InvertedTable.InvertedDoc invertedDoc : left) {
            docFrequencyMap.put(invertedDoc.docId, invertedDoc.docFrequency);
        }
        for (InvertedTable.InvertedDoc invertedDoc : right) {
            if (docFrequencyMap.get(invertedDoc.docId) != null) {
                docFrequencyMap.put(invertedDoc.docId, docFrequencyMap.get(invertedDoc.docId) + invertedDoc.docFrequency);
            } else {
                docFrequencyMap.put(invertedDoc.docId, invertedDoc.docFrequency);
            }
        }
        final Iterator<Long> keyIterator = docFrequencyMap.keySet().iterator();
        return new Iterable<InvertedTable.InvertedDoc>() {
            @Override
            public Iterator<InvertedTable.InvertedDoc> iterator() {
                return new Iterator<InvertedTable.InvertedDoc>() {
                    @Override
                    public boolean hasNext() {
                        return keyIterator.hasNext();
                    }

                    @Override
                    public InvertedTable.InvertedDoc next() {
                        Long key = keyIterator.next();
                        return new InvertedTable.InvertedDoc(key, docFrequencyMap.get(key));
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        };
    }
}
