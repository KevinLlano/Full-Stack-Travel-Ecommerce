package com.assessment.demo.config;

import com.assessment.demo.entities.*;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.rest.core.config.RepositoryRestConfiguration;
import org.springframework.data.rest.webmvc.config.RepositoryRestConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

//This class is used to expose the entity IDs in the REST API responses
//and to configure CORS settings for the REST API.
//This is important for frontend applications that need to reference specific entities by their IDs.
@Configuration
public class RestDataConfig implements RepositoryRestConfigurer {


    @Override
    public void configureRepositoryRestConfiguration(RepositoryRestConfiguration config,  CorsRegistry cors) {
        // expose entity IDs
        config.exposeIdsFor(
        Country.class,
        Customer.class,
        Division.class,
        Excursion.class,
        Vacation.class
        );

        // configure CORS
        config.setDefaultPageSize(Integer.MAX_VALUE);
        config.setMaxPageSize(Integer.MAX_VALUE);

    }
}

