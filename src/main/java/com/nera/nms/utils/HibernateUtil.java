package com.nera.nms.utils;

import java.util.Properties;

import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

import com.nera.nms.constants.CommonConstants;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class HibernateUtil {

    private HibernateUtil(){}

    //XML based configuration
    private static SessionFactory sessionFactory;

    private static SessionFactory buildSessionFactory() {
        try {
            // Create the SessionFactory from application.properties
            Configuration configuration = new Configuration();
            Properties properties = new Properties();
            properties.load(Thread.currentThread().getContextClassLoader().getResourceAsStream(CommonConstants.HIBERNATE_CONFIG_PROPERTIES));
            configuration.setProperties(properties);
            log.info("Hibernate Configuration loaded");

            ServiceRegistry serviceRegistry = new StandardServiceRegistryBuilder().applySettings(configuration.getProperties()).build();
            log.info("Hibernate serviceRegistry created");

            return configuration.buildSessionFactory(serviceRegistry);
        }
        catch (Exception ex) {
            // Make sure you log the exception, as it might be swallowed
            log.error("Initial SessionFactory creation failed." + ex);
            throw new ExceptionInInitializerError(ex);
        }
    }

    public static SessionFactory getSessionFactory() {
        if(sessionFactory == null) sessionFactory = buildSessionFactory();
        return sessionFactory;
    }

}