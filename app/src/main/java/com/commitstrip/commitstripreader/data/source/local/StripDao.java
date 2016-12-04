package com.commitstrip.commitstripreader.data.source.local;

import java.util.Date;

import io.requery.Entity;
import io.requery.Generated;
import io.requery.Key;

/**
 * Used by the orm to map object -> table
 */
@Entity
public abstract class StripDao {

    @Key
    Long id; // Have to be the same than the id pushed by the server.
    String title;
    Date date;
    String thumbnail;
    String content;
    String url;
    boolean isFavorite;
    Long previous; // Id to the previous strip (published before the current one)
    Long next;  // Id to the next strip (published after this one)

}
