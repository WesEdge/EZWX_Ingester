package ingest;

import java.io.IOException;
import java.net.URL;

import EZWX.core.Properties;
import EZWX.interfaces.MQActor;
import EZWX.mq.MQIngestConsumer;
import EZWX.mq.MQArgs;
import EZWX.core.Downloader;
import net.rubyeye.xmemcached.MemcachedClient;
import net.rubyeye.xmemcached.XMemcachedClient;

public class Ingester implements MQActor  {

    public Ingester() throws Exception {
        new MQIngestConsumer(this);
    }

    public void execute(MQArgs args) throws Exception{

        // download the file
        URL url = args.getUrl();
        byte[] bytes = Downloader.get(url);
        //Object file = EZWX.core.Serializer.deserialize(bytes);

        // save to memcached
        String memCachedHost = Properties.getValue("memCachedHost");
        int memCachedPort = Integer.parseInt(Properties.getValue("memCachedPort"));
        String memCachedKey = Properties.getValue("memCachedKey");
        MemcachedClient client = new XMemcachedClient(memCachedHost, memCachedPort);
        client.set(memCachedKey, 3600, bytes); // cache for 1 hour

        //broadcast the event
        (new Messenger()).send();

    }

}
