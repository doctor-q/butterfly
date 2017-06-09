package cc.doctor.search.server.query.merge;

import cc.doctor.search.server.index.store.indices.inverted.InvertedTable;
import cc.doctor.search.common.exceptions.index.IndexException;
import cc.doctor.search.common.utils.CollectionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.concurrent.RecursiveTask;

/**
 * Created by doctor on 2017/3/16.
 */
public class ForkJoinInvertedDocMerger implements InvertedDocMerger {
    private static final Logger log = LoggerFactory.getLogger(InvertedDocMerger.class);
    private ForkJoinPool forkJoinPool = new ForkJoinPool();
    public static final ForkJoinInvertedDocMerger forkJoinInvertedDocMerger = new ForkJoinInvertedDocMerger();

    private ForkJoinInvertedDocMerger() {
    }

    /**
     * 合并倒排链
     *
     * @param iterables 倒排链列表
     * @return 合并后的倒排链列表
     */
    public Iterable<InvertedTable.InvertedDoc> merge(Iterable<Iterable<InvertedTable.InvertedDoc>> iterables) {
        ForkJoinTask<Iterable<InvertedTable.InvertedDoc>> mergeDocTask = forkJoinPool.submit(new MergeDocTask(CollectionUtils.iterableToList(iterables)));
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
            docFrequencyMap.put(invertedDoc.getDocId(), invertedDoc.getDocFrequency());
        }
        for (InvertedTable.InvertedDoc invertedDoc : right) {
            if (docFrequencyMap.get(invertedDoc.getDocId()) != null) {
                docFrequencyMap.put(invertedDoc.getDocId(), docFrequencyMap.get(invertedDoc.getDocId()) + invertedDoc.getDocFrequency());
            } else {
                docFrequencyMap.put(invertedDoc.getDocId(), invertedDoc.getDocFrequency());
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
