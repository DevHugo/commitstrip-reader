package com.commitstrip.commitstripreader.util;


import com.commitstrip.commitstripreader.data.source.local.StripDaoEntity;

public class RobolectricClearDatabaseRule extends RobolectricDatabaseRule {

    @Override
    protected void before() throws Throwable {
        super.before();

        getLocalDatabase().delete(StripDaoEntity.class).get().call();
    }
}
