
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package io.permazen.change;

/**
 * Notification object that gets passed to {@link io.permazen.annotation.OnChange &#64;OnChange}-annotated methods
 * when a list field changes.
 *
 * @param <T> the type of the object containing the changed field
 */
public abstract class ListFieldChange<T> extends FieldChange<T> {

    /**
     * Constructor.
     *
     * @param jobj Java object containing the list field that changed
     * @param storageId the storage ID of the affected field
     * @param fieldName the name of the field that changed
     * @throws IllegalArgumentException if {@code jobj} or {@code fieldName} is null
     */
    protected ListFieldChange(T jobj, int storageId, String fieldName) {
        super(jobj, storageId, fieldName);
    }
}

