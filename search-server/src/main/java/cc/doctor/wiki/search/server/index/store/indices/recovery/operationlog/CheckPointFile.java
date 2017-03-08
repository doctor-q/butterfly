package cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.wiki.search.server.index.store.mm.MmapFile;
import cc.doctor.wiki.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;

/**
 * Created by doctor on 2017/3/8.
 * 检查点设置文件,检查点后的文件全部建立索引,从检查点开始恢复
 */
public class CheckPointFile {
    private static final Logger log = LoggerFactory.getLogger(CheckPointFile.class);
    public final CheckPointFile checkPointFile = new CheckPointFile();
    private MmapFile mmapFile;
    private static int checkFileSize = 0;
    private static final CheckPoint constantCheckPoint = new CheckPoint("00000000000000", 0);

    private CheckPointFile() {
        try {
            byte[] bytes = SerializeUtils.serialize(constantCheckPoint);
            checkFileSize = bytes.length;
            mmapFile = new MmapFile("/tmp/checkpoint", checkFileSize);
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    public void setCheckPoint(String fileName, int position) {
        mmapFile.writeObject(new CheckPoint(fileName, position));
        mmapFile.commit();
    }

    public CheckPoint getCheckPoint() {
        return mmapFile.readObject(0, checkFileSize);
    }

    public static class CheckPoint implements Serializable {
        private static final long serialVersionUID = -2991290627465935511L;
        private String fileName;    //file name pattern:20160112072033
        private int position;

        public String getFileName() {
            return fileName;
        }

        public void setFileName(String fileName) {
            this.fileName = fileName;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public CheckPoint(String fileName, int position) {
            this.fileName = fileName;
            this.position = position;
        }
    }

    public static void main(String[] args) {
        CheckPointFile checkPointFile = new CheckPointFile();
        checkPointFile.setCheckPoint("20170308180000", 100);
        CheckPoint checkPoint = checkPointFile.getCheckPoint();
        System.out.println(checkPoint);
    }
}
