package cc.doctor.wiki.search.server.index.store.indices.recovery.operationlog;

import cc.doctor.wiki.search.server.index.config.GlobalConfig;
import cc.doctor.wiki.search.server.index.store.indices.recovery.RecoveryService;
import cc.doctor.wiki.search.server.index.store.mm.MmapFile;
import cc.doctor.wiki.utils.DateUtils;
import cc.doctor.wiki.utils.FileUtils;
import cc.doctor.wiki.utils.SerializeUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by doctor on 2017/3/8.
 * 检查点设置文件,检查点后的文件全部建立索引,从检查点开始恢复
 * checkpoint file first, then operation log
 */
public class CheckPointFile {
    private static final Logger log = LoggerFactory.getLogger(CheckPointFile.class);
    private MmapFile mmapFile;
    private RecoveryService recoveryService;
    private static int checkFileSize = 0;
    private static final CheckPoint constantCheckPoint = new CheckPoint("00000000000000.op", 0);
    private String checkpointFilePath;

    public CheckPointFile(RecoveryService recoveryService) {
        this.recoveryService = recoveryService;
        checkpointFilePath = recoveryService.getOperationLogPath() + "/" + GlobalConfig.CHECKPOINT_FILE_NAME;

        try {
            byte[] bytes = SerializeUtils.serialize(constantCheckPoint);
            checkFileSize = bytes.length;
            if (!FileUtils.exists(checkpointFilePath)) {
                FileUtils.createFileRecursion(checkpointFilePath);
                mmapFile = new MmapFile(checkpointFilePath, checkFileSize);
                initCheckPoint();
            } else {
                mmapFile = new MmapFile(checkpointFilePath, checkFileSize);
            }
        } catch (IOException e) {
            log.error("", e);
            throw new RuntimeException(e);
        }
    }

    public CheckPoint initCheckPoint() {
        CheckPoint checkPoint = new CheckPoint(DateUtils.toYMDHMS(new Date()) + ".op", 0);
        this.setCheckPoint(checkPoint);
        recoveryService.setCheckPoint(checkPoint);
        return checkPoint;
    }

    public void setCheckPoint(CheckPoint checkPoint) {
        mmapFile.writeObject(checkPoint);
        mmapFile.commit();
    }

    public CheckPoint getCheckPoint() {
        return mmapFile.readObject(0, checkFileSize);
    }

    public static class CheckPoint implements Serializable {
        private static final long serialVersionUID = -2991290627465935511L;
        private String fileName;    //file name pattern:20160112072033.op
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
}
