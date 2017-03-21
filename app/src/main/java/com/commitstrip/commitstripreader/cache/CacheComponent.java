package com.commitstrip.commitstripreader.cache;

import com.commitstrip.commitstripreader.data.component.DataSourceComponent;
import com.commitstrip.commitstripreader.util.di.ActivityScoped;

import dagger.Component;

@ActivityScoped
@Component(dependencies = DataSourceComponent.class, modules = CachePresenterModule.class)
public interface CacheComponent {

    void inject (CacheActivity cacheActivity);
}
