
/*
 * Copyright (C) 2015 Archie L. Cobbs. All rights reserved.
 */

package io.permazen.change;

/**
 * Adpater class for the {@link ChangeSwitch} interface.
 *
 * @param <R> method return type
 */
public class ChangeAdapter<R> implements ChangeSwitch<R> {

    /**
     * Handle a {@link ObjectCreate} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseChange caseChange()}.
     */
    @Override
    public <T> R caseObjectCreate(ObjectCreate<T> change) {
        return this.caseChange(change);
    }

    /**
     * Handle a {@link ObjectDelete} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseChange caseChange()}.
     */
    @Override
    public <T> R caseObjectDelete(ObjectDelete<T> change) {
        return this.caseChange(change);
    }

    /**
     * Handle a {@link ListFieldAdd} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseListFieldChange caseListFieldChange()}.
     */
    @Override
    public <T, E> R caseListFieldAdd(ListFieldAdd<T, E> change) {
        return this.caseListFieldChange(change);
    }

    /**
     * Handle a {@link ListFieldClear} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseListFieldChange caseListFieldChange()}.
     */
    @Override
    public <T> R caseListFieldClear(ListFieldClear<T> change) {
        return this.caseListFieldChange(change);
    }

    /**
     * Handle a {@link ListFieldRemove} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseListFieldChange caseListFieldChange()}.
     */
    @Override
    public <T, E> R caseListFieldRemove(ListFieldRemove<T, E> change) {
        return this.caseListFieldChange(change);
    }

    /**
     * Handle a {@link ListFieldReplace} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseListFieldChange caseListFieldChange()}.
     */
    @Override
    public <T, E> R caseListFieldReplace(ListFieldReplace<T, E> change) {
        return this.caseListFieldChange(change);
    }

    /**
     * Handle a {@link MapFieldAdd} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseMapFieldChange caseMapFieldChange()}.
     */
    @Override
    public <T, K, V> R caseMapFieldAdd(MapFieldAdd<T, K, V> change) {
        return this.caseMapFieldChange(change);
    }

    /**
     * Handle a {@link MapFieldClear} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseMapFieldChange caseMapFieldChange()}.
     */
    @Override
    public <T> R caseMapFieldClear(MapFieldClear<T> change) {
        return this.caseMapFieldChange(change);
    }

    /**
     * Handle a {@link MapFieldRemove} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseMapFieldChange caseMapFieldChange()}.
     */
    @Override
    public <T, K, V> R caseMapFieldRemove(MapFieldRemove<T, K, V> change) {
        return this.caseMapFieldChange(change);
    }

    /**
     * Handle a {@link MapFieldReplace} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseMapFieldChange caseMapFieldChange()}.
     */
    @Override
    public <T, K, V> R caseMapFieldReplace(MapFieldReplace<T, K, V> change) {
        return this.caseMapFieldChange(change);
    }

    /**
     * Handle a {@link SetFieldAdd} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseSetFieldChange caseSetFieldChange()}.
     */
    @Override
    public <T, E> R caseSetFieldAdd(SetFieldAdd<T, E> change) {
        return this.caseSetFieldChange(change);
    }

    /**
     * Handle a {@link SetFieldClear} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseSetFieldChange caseSetFieldChange()}.
     */
    @Override
    public <T> R caseSetFieldClear(SetFieldClear<T> change) {
        return this.caseSetFieldChange(change);
    }

    /**
     * Handle a {@link SetFieldRemove} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseSetFieldChange caseSetFieldChange()}.
     */
    @Override
    public <T, E> R caseSetFieldRemove(SetFieldRemove<T, E> change) {
        return this.caseSetFieldChange(change);
    }

    /**
     * Handle a {@link SimpleFieldChange} event.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseSetFieldChange caseFieldChange()}.
     */
    @Override
    public <T, V> R caseSimpleFieldChange(SimpleFieldChange<T, V> change) {
        return this.caseFieldChange(change);
    }

// Roll-Up Methods

    /**
     * Internal roll-up method.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseFieldChange caseFieldChange()}.
     *
     * @param change visiting change
     * @param <T> changed object type
     * @return visitor return value
     */
    protected <T> R caseListFieldChange(ListFieldChange<T> change) {
        return this.caseFieldChange(change);
    }

    /**
     * Internal roll-up method.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseFieldChange caseFieldChange()}.
     *
     * @param change visiting change
     * @param <T> changed object type
     * @return visitor return value
     */
    protected <T> R caseMapFieldChange(MapFieldChange<T> change) {
        return this.caseFieldChange(change);
    }

    /**
     * Internal roll-up method.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseFieldChange caseFieldChange()}.
     *
     * @param change visiting change
     * @param <T> changed object type
     * @return visitor return value
     */
    protected <T> R caseSetFieldChange(SetFieldChange<T> change) {
        return this.caseFieldChange(change);
    }

    /**
     * Internal roll-up method.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} delegates to {@link #caseChange caseChange()}.
     *
     * @param change visiting change
     * @param <T> changed object type
     * @return visitor return value
     */
    protected <T> R caseFieldChange(FieldChange<T> change) {
        return this.caseChange(change);
    }

    /**
     * Internal roll-up method.
     *
     * <p>
     * The implementation in {@link ChangeAdapter} returns null.
     *
     * @param change visiting change
     * @param <T> changed object type
     * @return visitor return value
     */
    protected <T> R caseChange(Change<T> change) {
        return null;
    }
}

