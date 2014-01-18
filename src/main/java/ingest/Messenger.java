package ingest;

import EZWX.core.Properties;
import EZWX.mq.MQSender;
import EZWX.mq.MQArgs;

public class Messenger extends MQSender {

    public Messenger() throws Exception{
        super();
    }

    public MQArgs getMessage() throws Exception {

        MQArgs args = new MQArgs();
        args.setQueueHost(Properties.getValue("queueHost"));
        args.setQueueName(Properties.getValue("queueNameOut"));
        args.setMessage(Properties.getValue("memCachedKey"));
        return args;

    }

}
