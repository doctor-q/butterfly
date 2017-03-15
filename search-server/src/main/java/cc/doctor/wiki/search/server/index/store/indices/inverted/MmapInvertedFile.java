package cc.doctor.wiki.search.server.index.store.indices.inverted;

import cc.doctor.wiki.search.server.index.store.mm.MmapFile;

/**
 * Created by doctor on 2017/3/7.
 */
public class MmapInvertedFile extends InvertedFile {
    MmapFile mmapFile;

    @Override
    public InvertedTable getInvertedTable(WordInfo.InvertedNode invertedNode) {
        return mmapFile.readObject(invertedNode.getPosition(), invertedNode.getSize());
    }

    @Override
    public boolean writeInvertedTable(InvertedTable invertedTable) {
        int length = mmapFile.writeObject(invertedTable);
        return length > 0;
    }
}
