package com.commitstrip.commitstripreader.util;


import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

import javax.inject.Qualifier;
import javax.inject.Scope;

/**
 * In Dagger, an unscoped component cannot depend on a scoped component. As
 * {@link com.commitstrip.commitstripreader.data.module.LocalStorageModule} is a scoped component ({@code @Singleton}, we create a custom
 * scope to be used by all fragment components. Additionally, a component with a specific scope
 * cannot have a sub component with the same scope.
 */
@Documented
@Qualifier
@Retention(RetentionPolicy.RUNTIME)
public @interface ExternalStorage {
}
