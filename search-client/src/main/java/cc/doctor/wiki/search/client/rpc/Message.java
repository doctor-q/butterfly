package cc.doctor.wiki.search.client.rpc;

import cc.doctor.wiki.protocol.operation.Operation;
import cc.doctor.wiki.utils.SerializeUtils;

import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by doctor on 2017/3/14.
 */
public class Message implements Serializable{
    private static final long serialVersionUID = 3680058789136640175L;
    private long timestamp;
    private String host;
    private Operation operation;
    private Object data;

    public long getTimestamp() {
        return timestamp;
    }

    public String getHost() {
        return host;
    }

    public Operation getOperation() {
        return operation;
    }

    public Object getData() {
        return data;
    }

    public Message timestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    public Message currentTimestamp() {
        return timestamp(new Date().getTime());
    }

    public Message host(String host) {
        this.host = host;
        return this;
    }

    public Message operation(Operation operation) {
        this.operation = operation;
        return this;
    }

    public Message data(byte[] data) {
        this.data = data;
        return this;
    }

    public <T extends Serializable> Message data(T data) {
        try {
            byte[] bytes = SerializeUtils.serialize(data);
            return data(bytes);
        } catch (IOException e) {
            return this;
        }
    }

    public static Message newMessage() {
        return new Message();
    }

    public byte[] messageBytes() {
        try {
            return SerializeUtils.serialize(this);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
