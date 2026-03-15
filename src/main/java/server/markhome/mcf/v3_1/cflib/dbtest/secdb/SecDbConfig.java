/*
 *	Mark's Code Fractal CFLib DbTest 3.1 Database Test and Prototyping
 *	
 *	Copyright 2016-2026 Mark Stephen Sobkow
 *
 *	CFLib DbTest was used to exercise and prototype the use of the various database types
 *	in CFLib DbUtil, and to prototype the multi-JPA repository framework needed for the
 *	Code Fractal vision of how code should be structured.
 *
 *	These files are part of Mark's Code Fractal CFLib DbTest.
 *
 *	Mark's Code Fractal CFLib DbTest is available under dual commercial license from
 *	Mark Stephen Sobkow, or under the terms of the GNU Library General Public License,
 *	Version 3 or later.
 *
 *	As a special exception, Mark Sobkow gives you permission to link this library
 *	with independent modules to produce an executable, provided that none of them
 *	conflict with the intent of the LGPLv3; that is, you are not allowed to invoke
 *	the methods of this library from non-LGPLv3-compatibly licensed code.  That said,
 *	code which does not rely on this library is free to specify whatever license its
 *	authors decide to use. Mark Sobkow specifically rejects the infectious nature of
 *	the LGPLv3, and considers the mere act of including LGPLv3 modules in an
 *	executable to be perfectly reasonable given tools like modern Java's single-jar
 *	deployment options.
 *
 *	Mark's Code Fractal CFLib DbTest is free software: you can redistribute it and/or
 *	modify it under the terms of the GNU Library General Public License as published by
 *	the Free Software Foundation, either version 3 of the License, or
 *	(at your option) any later version.
 *
 *	Mark's Code Fractal CFLib DbTest is distributed in the hope that it will be useful,
 *	but WITHOUT ANY WARRANTY; without even the implied warranty of
 *	MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *	GNU Library General Public License for more details.
 *
 *	You should have received a copy of the GNU Library General Public License
 *	along with Mark's Code Fractal CFLib DbTest.  If not, see &lt;https://www.gnu.org/licenses/&gt;.
 *
 *	If you wish to modify and use this code without publishing your changes in order to
 *	tie it to proprietary code, please contact Mark Stephen Sobkow
 *	for a commercial license at mark.sobkow@gmail.com
 */
package server.markhome.mcf.v3_1.cflib.dbtest.secdb;

import javax.sql.DataSource;
import jakarta.persistence.EntityManagerFactory;

import java.util.Properties;
import java.util.concurrent.atomic.AtomicReference;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

import server.markhome.mcf.v3_1.cflib.CFLibDbException;
import server.markhome.mcf.v3_1.cflib.CFLibNullArgumentException;
import server.markhome.mcf.v3_1.cflib.dbtest.DbTest;
import server.markhome.mcf.v3_1.cflib.inz.Inz;

import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.orm.jpa.LocalContainerEntityManagerFactoryBean;
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter;

@Configuration
//@EntityScan(basePackages = "server.markhome.mcf.v3_1.cflib.dbtest.secdb")
@EnableTransactionManagement
@EnableJpaRepositories(
    basePackages = "server.markhome.mcf.v3_1.cflib.dbtest.secdb",
    entityManagerFactoryRef = "secEntityManagerFactory",
    transactionManagerRef = "secTransactionManager"
)
public class SecDbConfig {

    public final static String persistenceUnitName = "SecDbPU";

    private static final AtomicReference<DataSource> refSecDataSource = new AtomicReference<>(null);
    private static final AtomicReference<Properties> secJpaProperties = new AtomicReference<>(null);

    @Bean(name = "secDataSource")
    // @PersistenceContext(unitName = "SecDbPU")
    @Primary
    public DataSource secDataSource() {
        if (refSecDataSource.get() == null) {
            Properties props = DbTest.getMergedProperties();

            HikariConfig config = new HikariConfig();
            config.setDriverClassName(props.getProperty("secdb.jakarta.persistence.jdbc.driver", props.getProperty("jakarta.persistence.jdbc.driver", "org.postgresql.Driver")));
            config.setJdbcUrl(props.getProperty("secdb.jakarta.persistence.jdbc.url", props.getProperty("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/yourdb")));
            config.setUsername(props.getProperty("secdb.jakarta.persistence.jdbc.user", props.getProperty("jakarta.persistence.jdbc.user", "postgres")));
            config.setPassword(props.getProperty("secdb.jakarta.persistence.jdbc.password", props.getProperty("jakarta.persistence.jdbc.password", "pgpassword")));

            config.setMaximumPoolSize(Integer.parseInt(props.getProperty("secdb.hikari.maximumPoolSize", props.getProperty("hikari.maximumPoolSize", "10"))));
            config.setMinimumIdle(Integer.parseInt(props.getProperty("secdb.hikari.minimumIdle", props.getProperty("hikari.minimumIdle", "5"))));
            config.setPoolName(props.getProperty("secdb.hikari.poolName", props.getProperty("hikari.poolName", "SecDbHikariCP")));
            config.setAutoCommit(Boolean.getBoolean(props.getProperty("secdb.hikari.auto-commit", props.getProperty("hikari.auto-commit", "true"))));

            DataSource ds = new HikariDataSource(config);

            refSecDataSource.compareAndSet(null, ds);
        }
        return refSecDataSource.get();
    }

    @Bean(name = "secJpaProperties")
    @Primary
    public Properties secJpaProperties() {
        if (secJpaProperties.get() == null) {
            // Build the effective properties for secdb
            // The persistence unit name must match the one in your persistence.xml, or you can use a dynamic unit
            Properties merged = DbTest.getMergedProperties();
            String jakartaPersistenceJdbcDriver = merged.getProperty("secdb.jakarta.persistence.jdbc.driver", merged.getProperty("jakarta.persistence.jdbc.driver", null));
            String jakartaPersistenceJdbcUrl = merged.getProperty("secdb.jakarta.persistence.jdbc.url", merged.getProperty("jakarta.persistence.jdbc.url", "jdbc:postgresql://localhost:5432/dbtestdb"));
            String jakartaPersistenceJdbcUser = merged.getProperty("secdb.jakarta.persistence.jdbc.user", merged.getProperty("jakarta.persistence.jdbc.user", "postgres"));
            String jakartaPersistenceJdbcPassword = merged.getProperty("secdb.jakarta.persistence.jdbc.password", merged.getProperty("jakarta.persistence.jdbc.password", "pgpassword"));
            String jakartaPersistenceSchemaGenerationDatabaseAction = merged.getProperty("secdb.jakarta.persistence.schema-generation.database.action", merged.getProperty("jakarta.persistence.schema-generation.database.action", null));
            String jakartaPersistenceSchemaGenerationScriptsAction = merged.getProperty("secdb.jakarta.persistence.schema-generation.scripts.action", merged.getProperty("jakarta.persistence.schema-generation.scripts.action", null));
            String jakartaPersistenceSchemaGenerationCreateSource = merged.getProperty("secdb.jakarta.persistence.schema-generation.create-source", merged.getProperty("jakarta.persistence.schema-generation.create-source", "metadata"));
            String jakartaPersistenceSchemaGenerationDropSource = merged.getProperty("secdb.jakarta.persistence.schema-generation.drop-source", merged.getProperty("jakarta.persistence.schema-generation.drop-source", "metadata"));
            String jakartaPersistenceCreateDatabaseSchemas = merged.getProperty("secdb.jakarta.persistence.create-database-schemas", merged.getProperty("jakarta.persistence.create-database-schemas", "true"));
            // String jakartaNonJtaDataSource = merged.getProperty("secdb.jakarta.persistence.nonJtaDataSource", merged.getProperty("jakarta.persistence.nonJtaDataSource", null));
            // String jakartaJtaDataSource = merged.getProperty("secdb.jakarta.persistence.jtaDataSource", merged.getProperty("jakarta.persistence.jtaDataSource", null));
            String hibernateDialect = merged.getProperty("secdb.hibernate.dialect", merged.getProperty("hibernate.dialect", "org.hibernate.dialect.PostgreSQLDialect"));
            String hibernateHbm2ddlAuto = merged.getProperty("secdb.hibernate.hbm2ddl.auto", merged.getProperty("hibernate.hbm2ddl.auto", "update"));
            String hibernateShowSql = merged.getProperty("secdb.hibernate.show_sql", merged.getProperty("hibernate.show_sql", "false"));
            String hibernateFormatSql = merged.getProperty("secdb.hibernate.format_sql", merged.getProperty("hibernate.format_sql", "false"));
            String hibernateConnectionPoolSize = merged.getProperty("secdb.hibernate.connection_pool_size", merged.getProperty("hibernate.connection_pool_size", "10"));
            String hibernateConnectionDatasource = merged.getProperty("secdb.hibernate.connection_datasource", merged.getProperty("hibernate.connection_datasource", null));
            String hibernateCacheRegionFactoryClass = merged.getProperty("secdb.hibernate.cache.region.factory_class", merged.getProperty("hibernate.cache.region.factory_class", null));
            String hibernateDefaultSchema = merged.getProperty("secdb.hibernate.default_schema", "secdb");
            // String hibernateTransactionJTAPlatform = merged.getProperty("secdb.hibernate.transaction.jta.platform", merged.getProperty("hibernate.transaction.jta.platform", "org.hibernate.engine.transaction.jta.platform.internal.SpringJtaPlatform"));

            Properties applicable = new Properties();
            if (persistenceUnitName != null && !persistenceUnitName.isEmpty()) {
                applicable.setProperty("jakarta.persistence.unitName", persistenceUnitName);
            }
            if (jakartaPersistenceJdbcDriver != null && !jakartaPersistenceJdbcDriver.isEmpty()) {
                applicable.setProperty("jakarta.persistence.jdbc.driver", jakartaPersistenceJdbcDriver);
            }
            if (jakartaPersistenceJdbcUrl != null && !jakartaPersistenceJdbcUrl.isEmpty()) {
                applicable.setProperty("jakarta.persistence.jdbc.url", jakartaPersistenceJdbcUrl);
            }
            if (jakartaPersistenceJdbcUser != null && !jakartaPersistenceJdbcUser.isEmpty()) {
                applicable.setProperty("jakarta.persistence.jdbc.user", jakartaPersistenceJdbcUser);
            }
            if (jakartaPersistenceJdbcPassword != null && !jakartaPersistenceJdbcPassword.isEmpty()) {
                applicable.setProperty("jakarta.persistence.jdbc.password", jakartaPersistenceJdbcPassword);
            }
            if (jakartaPersistenceSchemaGenerationDatabaseAction != null && !jakartaPersistenceSchemaGenerationDatabaseAction.isEmpty()) {
                applicable.setProperty("jakarta.persistence.schema-generation.database.action", jakartaPersistenceSchemaGenerationDatabaseAction);
            }
            if (jakartaPersistenceSchemaGenerationScriptsAction != null && !jakartaPersistenceSchemaGenerationScriptsAction.isEmpty()) {
                applicable.setProperty("jakarta.persistence.schema-generation.scripts.action", jakartaPersistenceSchemaGenerationScriptsAction);
            }
            if (jakartaPersistenceSchemaGenerationCreateSource != null && !jakartaPersistenceSchemaGenerationCreateSource.isEmpty()) {
                applicable.setProperty("jakarta.persistence.schema-generation.create-source", jakartaPersistenceSchemaGenerationCreateSource);
            }
            if (jakartaPersistenceSchemaGenerationDropSource != null && !jakartaPersistenceSchemaGenerationDropSource.isEmpty()) {
                applicable.setProperty("jakarta.persistence.schema-generation.drop-source", jakartaPersistenceSchemaGenerationDropSource);
            }
            if (jakartaPersistenceCreateDatabaseSchemas != null && !jakartaPersistenceCreateDatabaseSchemas.isEmpty()) {
                applicable.setProperty("jakarta.persistence.create-database-schemas", jakartaPersistenceCreateDatabaseSchemas);
            }
            if (hibernateDialect != null && !hibernateDialect.isEmpty()) {
                applicable.setProperty("hibernate.dialect", hibernateDialect);
            }
            if (hibernateHbm2ddlAuto != null && !hibernateHbm2ddlAuto.isEmpty()) {
                applicable.setProperty("hibernate.hbm2ddl.auto", hibernateHbm2ddlAuto);
            }
            if (hibernateShowSql != null && !hibernateShowSql.isEmpty()) {
                applicable.setProperty("hibernate.show_sql", hibernateShowSql);
            }
            if (hibernateFormatSql != null && !hibernateFormatSql.isEmpty()) {
                applicable.setProperty("hibernate.format_sql", hibernateFormatSql);
            }
            if (hibernateConnectionPoolSize != null && !hibernateConnectionPoolSize.isEmpty()) {
                applicable.setProperty("hibernate.connection_pool_size", hibernateConnectionPoolSize);
            }
            if (hibernateConnectionDatasource != null && !hibernateConnectionDatasource.isEmpty()) {
                applicable.setProperty("hibernate.connection.datasource", hibernateConnectionDatasource);
            }
            if (hibernateCacheRegionFactoryClass != null && !hibernateCacheRegionFactoryClass.isEmpty()) {
                applicable.setProperty("hibernate.cache.region.factory_class", hibernateCacheRegionFactoryClass);
            }
            if (hibernateDefaultSchema != null && !hibernateDefaultSchema.isEmpty()) {
                applicable.setProperty("hibernate.default_schema", hibernateDefaultSchema);
            }
            // // A JTA implementation is required as a standalone application, but you can use implementations for specific J2EE servers, such as WebLogic, too
            // if (hibernateTransactionJTAPlatform != null && !hibernateTransactionJTAPlatform.isEmpty()) {
            //     applicable.setProperty("hibernate.transaction.jta.platform", hibernateTransactionJTAPlatform);
            // }
            // // If you want to use a JTA DataSource, you can set it here
            // if (jakartaJtaDataSource != null && !jakartaJtaDataSource.isEmpty()) {
            //     applicable.setProperty("jakarta.persistence.jtaDataSource", jakartaJtaDataSource);
            // }
            // // If you want to use a non-JTA DataSource, you can set it here
            // if (jakartaNonJtaDataSource != null && !jakartaNonJtaDataSource.isEmpty()) {
            //     applicable.setProperty("jakarta.persistence.nonJtaDataSource", jakartaNonJtaDataSource);
            // }
 
            secJpaProperties.compareAndSet(null, applicable);
        }
        return secJpaProperties.get();
    }

    @Bean(name = "secEntityManagerFactory")
    @Primary
    // @PersistenceContext(unitName = "SecDbPU")
    public LocalContainerEntityManagerFactoryBean secEntityManagerFactory(
        @Qualifier("secDataSource") DataSource secDataSource,
        @Qualifier("secJpaProperties") Properties secJpaProperties) {
            // Create the EntityManagerFactory using the Jakarta Persistence API
            try {
                System.err.println("Creating secEntityManagerFactory with properties:");
                secJpaProperties.forEach((key, value) -> {
                    if (value instanceof String) {
                        String s = (String)value;
                        System.err.println("  " + key + " = " + s);
                    }
                    else {
                        String classname = value.getClass().getName();
                        System.err.println("  " + key + " = instanceof(" + classname + ")");
                    }
                });

                // Configure EntityManagerFactoryBean
                LocalContainerEntityManagerFactoryBean emfBean = new LocalContainerEntityManagerFactoryBean();
                emfBean.setDataSource(secDataSource);
                emfBean.setPackagesToScan("server.markhome.mcf.v3_1.cflib.dbtest.secdb");
                emfBean.setJpaVendorAdapter(new HibernateJpaVendorAdapter());
                emfBean.setJpaProperties(secJpaProperties);
                emfBean.setPersistenceUnitName(persistenceUnitName);
                return emfBean;
                // refSecEntityManagerFactoryBean.compareAndSet(null, emfBean);
            } catch (Exception e) {
                System.err.println(String.format(Inz.x("cflib.dbtest.SecDbConfig.secEntityManagerFactoryRethrow"), persistenceUnitName, e.getClass().getName(), e.getLocalizedMessage()));
                e.printStackTrace(System.err);
                throw new CFLibDbException(SecDbConfig.class, "secEntityManagerFactory", String.format(Inz.s("cflib.dbtest.SecDbConfig.secEntityManagerFactoryRethrow"), persistenceUnitName, e.getClass().getName(), e.getMessage()), String.format(Inz.x("cflib.dbtest.SecDbConfig.secEntityManagerFactoryRethrow"), persistenceUnitName, e.getClass().getName(), e.getLocalizedMessage()), e);
            }
    }

    @Bean(name = "secTransactionManager")
    @Primary
    // @PersistenceContext(unitName = "SecDbPU")
    public JpaTransactionManager secTransactionManager(
        @Qualifier("secEntityManagerFactory") LocalContainerEntityManagerFactoryBean secEntityManagerFactory) {
            EntityManagerFactory f = secEntityManagerFactory.getObject();
            if (f != null) {
                return new JpaTransactionManager(f);
            }
            else {
                System.err.println("ERROR: SecDbConfig.secTransactionManager() secEntityManagerFactoryBean.getObject() returned null");
                throw new CFLibNullArgumentException(SecDbConfig.class, "secTransactionManager");
            }
    }
}
