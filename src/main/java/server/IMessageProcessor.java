package server;

/**
 * Created by rx on 2017/9/9.
 */
public interface IMessageProcessor {

    public void process(Message message, WriteProxy writeProxy);
}
