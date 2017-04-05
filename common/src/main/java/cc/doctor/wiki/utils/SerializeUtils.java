package cc.doctor.wiki.utils;

import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.lang.reflect.Type;
import java.util.List;

/**
 * Created by doctor on 2017/3/8.
 */
public class SerializeUtils {
    private static final Logger log = LoggerFactory.getLogger(SerializeUtils.class);

    //序列化
    public static <T extends Serializable> byte[] serialize(T serializable) throws IOException {
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            ObjectOutput objectOutput = new ObjectOutputStream(outputStream);
            objectOutput.writeObject(serializable);
            return outputStream.toByteArray();
        } catch (IOException e) {
            log.error("", e);
            throw e;
        }
    }

    //反序列化
    public static <T extends Serializable> T deserialize(byte[] bytes) throws IOException, ClassNotFoundException {
        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
        try {
            ObjectInput objectInput = new ObjectInputStream(byteArrayInputStream);
            return (T) objectInput.readObject();
        } catch (Exception e) {
            log.error("", e);
            throw e;
        }
    }

    public static <T> T jsonToObject(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, type);
    }

    public static <T> List<T> jsonToList(String json, Type type) {
        Gson gson = new Gson();
        return gson.fromJson(json, List.class);
    }

    public static <T> String objectToJson(T t) {
        Gson gson = new Gson();
        return gson.toJson(t);
    }
}
