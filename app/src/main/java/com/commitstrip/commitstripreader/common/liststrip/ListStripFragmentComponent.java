package com.commitstrip.commitstripreader.common.liststrip;

import com.commitstrip.commitstripreader.common.liststrip.ListStripFragment;
import com.commitstrip.commitstripreader.data.component.SharedPreferencesComponent;
import com.commitstrip.commitstripreader.util.di.FragmentScoped;
import com.commitstrip.commitstripreader.util.di.module.ImageUtilsModule;
import com.commitstrip.commitstripreader.util.di.module.StripWithImageDtoToStripDtoModule;

import dagger.Component;

@FragmentScoped
@Component(modules = {ImageUtilsModule.class, StripWithImageDtoToStripDtoModule.class})
public interface ListStripFragmentComponent {

    void inject (ListStripFragment fragment);
}
