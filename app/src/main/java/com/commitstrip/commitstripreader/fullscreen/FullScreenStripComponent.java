package com.commitstrip.commitstripreader.fullscreen;

import com.commitstrip.commitstripreader.data.component.DataSourceComponent;
import com.commitstrip.commitstripreader.util.di.ActivityScoped;
import dagger.Component;

@ActivityScoped
@Component(dependencies = DataSourceComponent.class, modules = FullScreenStripPresenterModule.class)
public interface FullScreenStripComponent {

  void inject(FullScreenStripActivity activity);

}
