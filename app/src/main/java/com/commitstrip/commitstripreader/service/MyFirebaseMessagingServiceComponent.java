package com.commitstrip.commitstripreader.service;

import com.commitstrip.commitstripreader.data.source.StripRepositoryComponent;
import com.commitstrip.commitstripreader.util.FragmentScoped;


import javax.inject.Singleton;

import dagger.Component;

@FragmentScoped
@Component(dependencies = StripRepositoryComponent.class)
public interface MyFirebaseMessagingServiceComponent {

    void inject(MyFirebaseMessagingService myFirebaseMessagingService);

}
