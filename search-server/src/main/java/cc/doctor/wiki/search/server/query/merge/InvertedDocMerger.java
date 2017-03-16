package cc.doctor.wiki.search.server.query.merge;

import cc.doctor.wiki.search.server.index.store.indices.inverted.InvertedTable;

/**
 * Created by doctor on 2017/3/16.
 */
public interface InvertedDocMerger {
    Iterable<InvertedTable.InvertedDoc> merge(Iterable<Iterable<InvertedTable.InvertedDoc>> invertedDocsList);
}
