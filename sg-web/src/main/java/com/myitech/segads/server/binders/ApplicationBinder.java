package com.myitech.segads.server.binders;

import com.google.common.eventbus.EventBus;
import com.myitech.segads.server.WebServer;
import com.myitech.segads.db.CassandraDatastore;
import com.myitech.segads.db.CassandraSchema;
import com.myitech.segads.db.Datastore;
import com.myitech.segads.core.DataService;
import com.myitech.segads.core.ModelService;
import com.myitech.segads.core.SegadsService;
import com.myitech.segads.core.SessionService;
import com.myitech.segads.core.handler.DataServiceImpl;
import com.myitech.segads.core.handler.ModelServiceImpl;
import com.myitech.segads.core.handler.SegadsServiceImpl;
import com.myitech.segads.core.handler.SessionServiceImpl;
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
        bind(DefaultTopicDistributionService.class).to(TopicDistributionService.class).in(Singleton.class);
        bind(CassandraSchema.class).in(Singleton.class);
        bind(DataServiceImpl.class).to(DataService.class).in(Singleton.class);
        bind(ModelServiceImpl.class).to(ModelService.class).in(Singleton.class);
        bind(EventBus.class).in(Singleton.class);
        bind(WebServer.getInstance());
    }
}
