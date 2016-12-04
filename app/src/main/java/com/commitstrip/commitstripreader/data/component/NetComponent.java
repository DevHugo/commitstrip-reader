package com.commitstrip.commitstripreader.data.component;

import com.commitstrip.commitstripreader.data.module.NetModule;

import dagger.Component;
import retrofit2.Retrofit;

/**
 * This is a Dagger component. Refer to {@link com.commitstrip.commitstripreader.data} for the list of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the {@link com.commitstrip.commitstripreader.data.source.StripRepositoryComponent}, which is a singleton, a
 * scope must be specified.
 */
@Component(modules = {NetModule.class})
public interface NetComponent {

    Retrofit getRetrofit();

}
