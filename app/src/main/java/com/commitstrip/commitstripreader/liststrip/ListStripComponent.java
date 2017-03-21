package com.commitstrip.commitstripreader.liststrip;

import com.commitstrip.commitstripreader.common.liststrip.ListStripPresenterModule;
import com.commitstrip.commitstripreader.data.component.DataSourceComponent;
import com.commitstrip.commitstripreader.util.di.FragmentScoped;
import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link com.commitstrip.commitstripreader.data.source.StripRepositorySingleton} for the list of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the {@link com.commitstrip.commitstripreader.data.source.DataSourceComponent}, which is a singleton, a
 * scope must be specified.
 */
@FragmentScoped
@Component(dependencies = DataSourceComponent.class, modules = ListStripPresenterModule.class)
public interface ListStripComponent {

  void inject(ListStripActivity activity);
}
