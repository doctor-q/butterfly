package cc.doctor.search.store.indices.inverted.merge;

import cc.doctor.search.store.indices.inverted.InvertedTable;

/**
 * Created by doctor on 2017/3/16.
 */
public interface InvertedDocMerger {
    Iterable<InvertedTable.InvertedDoc> merge(Iterable<Iterable<InvertedTable.InvertedDoc>> invertedDocsList);
}
