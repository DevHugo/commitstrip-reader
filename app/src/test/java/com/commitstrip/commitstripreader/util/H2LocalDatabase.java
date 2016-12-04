package com.commitstrip.commitstripreader.util;

import com.commitstrip.commitstripreader.data.source.local.Models;

import org.h2.jdbcx.JdbcDataSource;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import io.requery.Persistable;
import io.requery.meta.EntityModel;
import io.requery.reactivex.ReactiveEntityStore;
import io.requery.reactivex.ReactiveSupport;
import io.requery.sql.Configuration;
import io.requery.sql.ConfigurationBuilder;
import io.requery.sql.ConnectionProvider;
import io.requery.sql.EntityDataStore;
import io.requery.sql.SchemaModifier;
import io.requery.sql.TableCreationMode;

public class H2LocalDatabase {

    public static ReactiveEntityStore<Persistable> getConnection() {

        try {
            Class.forName("org.h2.Driver");

            Connection conn = DriverManager.getConnection("jdbc:h2:database-test/test", "sa", "sa");

            ConnectionProvider connectionProvider = new ConnectionProvider() {
                @Override
                public Connection getConnection() throws SQLException {
                    return conn;
                }
            };

            EntityModel model = Models.DEFAULT;
            Configuration configuration = new ConfigurationBuilder(connectionProvider, model)
                    .useDefaultLogging()
                    .build();

            new SchemaModifier(configuration).createTables(TableCreationMode.DROP_CREATE);

            return ReactiveSupport.toReactiveStore(new EntityDataStore<Persistable>(configuration));

        } catch (SQLException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

}
