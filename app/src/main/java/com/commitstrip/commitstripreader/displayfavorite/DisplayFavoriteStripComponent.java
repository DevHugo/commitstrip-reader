package com.commitstrip.commitstripreader.displayfavorite;

import com.commitstrip.commitstripreader.data.source.StripRepositoryComponent;
import com.commitstrip.commitstripreader.util.FragmentScoped;

import dagger.Component;

/**
 * This is a Dagger component. Refer to {@link com.commitstrip.commitstripreader.data.source.StripRepositorySingleton} for the list of Dagger components
 * used in this application.
 * <P>
 * Because this component depends on the {@link com.commitstrip.commitstripreader.data.source.StripRepositoryComponent}, which is a singleton, a
 * scope must be specified.
 */
@FragmentScoped
@Component(dependencies = StripRepositoryComponent.class, modules = DisplayFavoriteStripPresenterModule.class)
public interface DisplayFavoriteStripComponent {

    void inject(DisplayFavoriteStripActivity activity);
}
