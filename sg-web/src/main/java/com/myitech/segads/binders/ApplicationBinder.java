package com.myitech.segads.binders;

import com.google.common.eventbus.EventBus;
import com.myitech.segads.core.WebServer;
import com.myitech.segads.datastore.*;
import com.myitech.segads.services.SegadsService;
import com.myitech.segads.services.SessionService;
import com.myitech.segads.services.impl.SegadsServiceImpl;
import com.myitech.segads.services.impl.SessionServiceImpl;
import org.glassfish.hk2.api.messaging.TopicDistributionService;
import org.glassfish.hk2.extras.events.internal.DefaultTopicDistributionService;
import org.glassfish.jersey.internal.inject.AbstractBinder;

import javax.inject.Singleton;

/**
 * Created by A.T on 2018/1/10.
 */
public class ApplicationBinder extends AbstractBinder {
    @Override
    protected void configure() {
        bind(SegadsServiceImpl.class).to(SegadsService.class).in(Singleton.class);
        bind(SessionServiceImpl.class).to(SessionService.class).in(Singleton.class);
        bind(CassandraDatastore.class).to(Datastore.class).in(Singleton.class);
        bind(CassandraClientImpl.class).to(CassandraClient.class).in(Singleton.class);
        bind(DefaultTopicDistributionService.class).to(TopicDistributionService.class).in(Singleton.class);
        bind(Schema.class).in(Singleton.class);
        bind(EventBus.class).in(Singleton.class);
        bind(WebServer.newInstance(null));
    }
}
