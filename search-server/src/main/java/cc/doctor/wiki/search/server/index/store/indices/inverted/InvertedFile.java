package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.exceptions.index.IndexException;
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
 * Created by doctor on 2017/3/7.
 * 倒排文件
 */
public abstract class InvertedFile {
    private static final Logger log = LoggerFactory.getLogger(InvertedFile.class);
    //使用forkjoin框架进行doc合并
    private ForkJoinPool forkJoinPool = new ForkJoinPool();

    /**
     * 获取倒排文件列表
     *
     * @param positions
     */
    public abstract Iterable<InvertedDoc> getInvertedDocs(List<Long> positions);

    /**
     * 合并倒排链
     * @param iterables 倒排链列表
     * @return 合并后的倒排链列表
     */
    public Iterable<InvertedDoc> mergeDocs(List<Iterable<InvertedDoc>> iterables) {
        ForkJoinTask<Iterable<InvertedDoc>> mergeDocTask = forkJoinPool.submit(new MergeDocTask(iterables));
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
    public class MergeDocTask extends RecursiveTask<Iterable<InvertedDoc>> {
        List<Iterable<InvertedDoc>> iterables;

        public MergeDocTask(List<Iterable<InvertedDoc>> iterables) {
            this.iterables = iterables;
        }

        private static final long serialVersionUID = 5382360215324978123L;

        @Override
        protected Iterable<InvertedDoc> compute() {
            if (iterables.size() == 1) {
                return iterables.get(0);
            } else if (iterables.size() == 2) {
                return merge(iterables.get(0), iterables.get(1));
            } else {
                MergeDocTask leftMergeDocTask = new MergeDocTask(iterables.subList(0, iterables.size() / 2));
                MergeDocTask rightMergeDocTask = new MergeDocTask(iterables.subList(iterables.size() / 2, iterables.size()));
                leftMergeDocTask.fork();
                rightMergeDocTask.fork();
                Iterable<InvertedDoc> leftDocs = leftMergeDocTask.join();
                Iterable<InvertedDoc> rightDocs = rightMergeDocTask.join();
                return merge(leftDocs, rightDocs);
            }
        }
    }

    protected Iterable<InvertedDoc> merge(Iterable<InvertedDoc> left, Iterable<InvertedDoc> right) {
        final Map<Long, Long> docFrequencyMap = new HashMap<>();
        for (InvertedDoc invertedDoc : left) {
            docFrequencyMap.put(invertedDoc.docId, invertedDoc.docFrequency);
        }
        for (InvertedDoc invertedDoc : right) {
            if (docFrequencyMap.get(invertedDoc.docId) != null) {
                docFrequencyMap.put(invertedDoc.docId, docFrequencyMap.get(invertedDoc.docId) + invertedDoc.docFrequency);
            } else {
                docFrequencyMap.put(invertedDoc.docId, invertedDoc.docFrequency);
            }
        }
        final Iterator<Long> keyIterator = docFrequencyMap.keySet().iterator();
        return new Iterable<InvertedDoc>() {
            @Override
            public Iterator<InvertedDoc> iterator() {
                return new Iterator<InvertedDoc>() {
                    @Override
                    public boolean hasNext() {
                        return keyIterator.hasNext();
                    }

                    @Override
                    public InvertedDoc next() {
                        Long key = keyIterator.next();
                        return new InvertedDoc(key, docFrequencyMap.get(key));
                    }

                    @Override
                    public void remove() {

                    }
                };
            }
        };
    }
}
