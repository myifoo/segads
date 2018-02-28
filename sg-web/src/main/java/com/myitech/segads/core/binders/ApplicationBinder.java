package com.myitech.segads.core.binders;

import com.google.common.eventbus.EventBus;
import com.myitech.segads.core.WebServer;
import com.myitech.segads.core.db.CassandraDatastore;
import com.myitech.segads.core.db.CassandraSchema;
import com.myitech.segads.core.db.Datastore;
import com.myitech.segads.services.DatapointsService;
import com.myitech.segads.services.SegadsService;
import com.myitech.segads.services.SessionService;
import com.myitech.segads.services.impl.DatapointsServiceImpl;
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
        bind(DefaultTopicDistributionService.class).to(TopicDistributionService.class).in(Singleton.class);
        bind(CassandraSchema.class).in(Singleton.class);
        bind(DatapointsServiceImpl.class).to(DatapointsService.class).in(Singleton.class);
        bind(EventBus.class).in(Singleton.class);
        bind(WebServer.getInstance());
    }
}
