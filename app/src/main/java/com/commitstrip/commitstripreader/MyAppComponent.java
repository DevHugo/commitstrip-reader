package com.commitstrip.commitstripreader;

import com.commitstrip.commitstripreader.data.component.DataSourceComponent;

import javax.inject.Singleton;

import dagger.Component;

@Component(dependencies = DataSourceComponent.class)
@Singleton
public interface MyAppComponent {

    void inject (MyApp app);

}
