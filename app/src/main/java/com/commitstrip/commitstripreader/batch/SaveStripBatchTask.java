package com.commitstrip.commitstripreader.batch;

import android.support.annotation.NonNull;

import com.commitstrip.commitstripreader.data.source.StripRepository;
import com.commitstrip.commitstripreader.data.source.local.converter.ListStripDaoToListStripDto;
import com.commitstrip.commitstripreader.dto.StripDto;
import com.commitstrip.commitstripreader.util.Preconditions;

import org.reactivestreams.Publisher;
import org.reactivestreams.Subscriber;

import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.List;

import io.reactivex.Completable;
import io.reactivex.Flowable;
import io.reactivex.Maybe;
import io.reactivex.MaybeSource;
import io.reactivex.functions.Function;
import io.reactivex.functions.Predicate;
import io.reactivex.observers.DisposableMaybeObserver;
import io.reactivex.schedulers.Schedulers;

/**
 * Construct a batch task for saving strip's metadata.
 */
public class SaveStripBatchTask {

    @NonNull private static final Integer BUFFER_SIZE = 50;

    @NonNull private SoftReference<StripRepository> mWeakStripRepository;

    /**
     * Construct a batch instance with a StripRepository instance.
     *
     * Since the operation can be time consuming, if you sync all strip metadata, StripRepository
     * instance is save under a SoftReference. If the system reclaims memory, the task can be cancel
     * without any further detail or logging information. The task will be not resume automatically.
     *
     * In all use case, you should not pass a null instance in parameter.
     *
     * @param stripRepository use dependency injection for having a valid strip repository instance
     * @throws NullPointerException is thrown, if StripRepository is null.
     */
    public SaveStripBatchTask(@NonNull StripRepository stripRepository) {
        Preconditions.checkNotNull(stripRepository);

        mWeakStripRepository = new SoftReference<>(stripRepository);
    }

    /**
     * Update or save in the local database all strips pass in parameter.
     *
     * For better performance, the operation is subscribe on a new thread by default and strips will
     * be emit in a buffer of 50 items, before updating or saving them.
     *
     * If the system reclaims memory, the task can be cancel without any further detail.
     * The task will be not resume automatically. Items can be partially saved.
     *
     * @param entities to update or save
     * @return the operation result
     * @throws NullPointerException if entities parameter is null, a NullPointerException is thrown
     */
    public Flowable<StripDto> execute(@NonNull Flowable<StripDto> entities) {
        Preconditions.checkNotNull(entities);

        return entities
                // Since the operation can be time expensive, we should subscribe on a new thread.
                .observeOn(Schedulers.newThread())
                .filter(strip -> {
                    StripRepository stripRepository = mWeakStripRepository.get();

                    if (stripRepository != null) {
                        StripDto stripFromDd =
                                stripRepository.fetchStrip(strip.getId(), false).blockingGet();

                        if (stripFromDd == null) {

                            return true;
                        } else {

                            if (!strip.equals(stripFromDd)) {

                                List<StripDto> strips = new ArrayList<>();
                                strips.add(strip);

                                stripRepository.upsertStrip(strips);

                                return false;
                            }

                            return false;
                        }
                    }

                    return false;
                })
                // Buffer is used for increasing performance, bundle all items at once is really
                // faster way to save data.
                .buffer(BUFFER_SIZE)
                .flatMap(strips -> {
                    StripRepository stripRepository = mWeakStripRepository.get();

                    if (stripRepository != null) {
                        return stripRepository.upsertStrip(strips);
                    }

                    return Flowable.empty();
                })
                .map(new ListStripDaoToListStripDto())
                .flatMap(Flowable::fromIterable);
    }

}
