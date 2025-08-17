package com.agrilink.rwanda;

import com.agrilink.rwanda.config.AsyncSyncConfiguration;
import com.agrilink.rwanda.config.EmbeddedElasticsearch;
import com.agrilink.rwanda.config.EmbeddedSQL;
import com.agrilink.rwanda.config.JacksonConfiguration;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * Base composite annotation for integration tests.
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@SpringBootTest(classes = { AgriLinkRwandaApp.class, JacksonConfiguration.class, AsyncSyncConfiguration.class })
@EmbeddedElasticsearch
@EmbeddedSQL
public @interface IntegrationTest {}
