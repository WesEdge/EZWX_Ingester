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
import net.rubyeye.xmemcached.transcoders.SerializingTranscoder;
import ucar.grib.GribChecker;

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
        int memCachedMaxObjectSizeMB = Integer.parseInt(Properties.getValue("memCachedMaxObjectSizeMB"));
        int memCachedCacheDurationMinutes = Integer.parseInt(Properties.getValue("memCachedCacheDurationMinutes"));
        String memCachedKey = Properties.getValue("memCachedKey");
        MemcachedClient client = new XMemcachedClient(memCachedHost, memCachedPort);
        //set maximum to 10MB
        client.setTranscoder(new SerializingTranscoder(memCachedMaxObjectSizeMB*1024*1024));
        client.set(memCachedKey, memCachedCacheDurationMinutes * (3600/60), bytes); // cache for 1 hour

        //broadcast the event
        (new Messenger()).send();

    }

}
