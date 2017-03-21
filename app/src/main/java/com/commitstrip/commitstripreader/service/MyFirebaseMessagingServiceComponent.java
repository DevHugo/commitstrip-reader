package com.commitstrip.commitstripreader.service;

import com.commitstrip.commitstripreader.data.component.DataSourceComponent;
import com.commitstrip.commitstripreader.util.di.FragmentScoped;


import dagger.Component;

@FragmentScoped
@Component(dependencies = DataSourceComponent.class)
public interface MyFirebaseMessagingServiceComponent {

    void inject(MyFirebaseMessagingService myFirebaseMessagingService);

}
