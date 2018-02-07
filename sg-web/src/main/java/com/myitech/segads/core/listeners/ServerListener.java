package com.myitech.segads.core.listeners;

import com.google.common.eventbus.Subscribe;
import com.myitech.segads.core.events.StartFailedEvent;
import com.myitech.segads.core.events.StartSuccessEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.*;

/**
 * Description:
 *
 *      目前只有一个 web server，以后管理的server多了，可以添加 order 控制子类的。
 *
 * <p>
 * Created by A.T on 2018/02/01
 */
public class ServerListener {
    private Logger logger = LoggerFactory.getLogger(ServerListener.class);

    /**
     * 对于发送 StartFailedEvent 的线程，该方法会延迟1分钟后，再度执行！
     *
     * @param event
     */
     @Subscribe
    public void listenOnFailed(final StartFailedEvent event) {
        logger.info("Receive new start failed event: {}", event.getName());

        // 延迟一分钟重新执行
         Timer timer = new Timer();
         timer.schedule(new TimerTask() {
             @Override
             public void run() {
                 new Thread(event.getServer(), event.getName());
             }
         }, 60*1000);
    }
}
