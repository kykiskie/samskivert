//
// $Id: ListUtil.java,v 1.1 2001/08/15 04:02:42 mdb Exp $

package com.samskivert.util;

/**
 * List util is for the times when you just can't bear the overhead of an
 * {@link java.util.ArrayList} object to manage your list of objects. It
 * provides a suite of list management routines that operate on bare
 * {@link java.lang.Object} arrays. Some of those routines mimic the
 * behavior of array lists, others provide other more specialized
 * (generally faster but making requirements of the caller) list behavior.
 *
 * <p> An example is probably in order:
 *
 * <pre>
 * Object[] list = null;
 * String foo = "foo";
 * String bar = "bar";
 *
 * // add our objects to a list
 * list = ListUtil.add(list, foo);
 * list = ListUtil.add(list, bar);
 *
 * // remove foo from the list (does so by clearing out that index, but it
 * // doesn't slide subsequent elements down)
 * ListUtil.clear(list, foo);
 *
 * // use the version of remove that calls equals() rather than just
 * // checking for equality of references
 * String anotherBar = "bar";
 * ListUtil.clearEqual(list, anotherBar);
 *
 * // append our objects to the end of the list letting list util know
 * // that we're tracking the list size
 * list = ListUtil.add(list, 0, foo);
 * list = ListUtil.add(list, 1, bar);
 *
 * // remove the elements from the list, compacting it to preserve
 * // element continuity
 * ListUtil.remove(list, 0);
 * ListUtil.remove(list, bar);
 * </pre>
 *
 * See the documentation for the individual functions for their exact
 * behavior.
 */
public class ListUtil
{
    /**
     * Adds the specified element to the first empty slot in the specified
     * list. Begins searching for empty slots at zeroth index.
     *
     * @param list the list to which to add the element. Can be null.
     * @param element the element to add.
     *
     * @return a reference to the list with element added (might not be
     * the list you passed in due to expansion, or allocation).
     */
    public static Object[] add (Object[] list, Object element)
    {
        return add(list, 0, element);
    }

    /**
     * Adds the specified element to the next empty slot in the specified
     * list. Begins searching for empty slots at the specified index. This
     * can be used to quickly add elements to a list that preserves
     * consecutivity by calling it with the size of the list as the first
     * index to check.
     *
     * @param list the list to which to add the element. Can be null.
     * @param startIdx the index at which to start looking for a spot.
     * @param element the element to add.
     *
     * @return a reference to the list with element added (might not be
     * the list you passed in due to expansion, or allocation).
     */
    public static Object[] add (Object[] list, int startIdx, Object element)
    {
        // make sure we've got a list to work with
        if (list == null) {
            list = new Object[DEFAULT_LIST_SIZE];
        }

        // search for a spot to insert yon element; assuming we'll insert
        // it at the end of the list if we don't find one
        int index = list.length;
        int llength = list.length; // no optimizing bastards
        for (int i = startIdx; i < llength; i++) {
            if (list[i] == null) {
                index = i;
                break;
            }
        }

        // expand the list if necessary
        if (index >= list.length) {
            list = accomodate(list, index);
        }

        // stick the element on in
        list[index] = element;

        return list;
    }

    /**
     * Clears out the first element that is referentially equal to the
     * supplied element (<code>list[idx] == element</code>). Passing a
     * null <code>element</code> to this function will cleverly tell you
     * the index of the first null element in the array which it will have
     * kindly overwritten with null just for good measure.
     *
     * @return the element that was removed or null if it was not found.
     */
    public static Object clear (Object[] list, Object element)
    {
        int llength = list.length; // no optimizing bastards
        for (int i = 0; i < llength; i++) {
            Object elem = list[i];
            if (elem == element) {
                list[i] = null;
                return elem;
            }
        }
        return null;
    }

    /**
     * Clears out the first element that is functionally equal to the
     * supplied element (<code>list[idx].equals(element)</code>). Passing
     * a null <code>element</code> to this function will call
     * <code>equals(null)</code> on all objects in the list which may
     * cause them to choke, so don't do that unless you mean it.
     *
     * @return the object that was cleared from the array or null if no
     * matching object was found.
     */
    public static Object clearEqual (Object[] list, Object element)
    {
        int llength = list.length; // no optimizing bastards
        for (int i = 0; i < llength; i++) {
            Object elem = list[i];
            if (elem != null && elem.equals(element)) {
                list[i] = null;
                return elem;
            }
        }
        return null;
    }

    /**
     * Removes the first element that is referentially equal to the
     * supplied element (<code>list[idx] == element</code>). The elements
     * after the removed element will be slid down the array one spot to
     * fill the place of the removed element.
     *
     * @return the object that was removed from the array or null if no
     * matching object was found.
     */
    public static Object remove (Object[] list, Object element)
    {
        int llength = list.length; // no optimizing bastards
        for (int i = 0; i < llength; i++) {
            Object elem = list[i];
            if (elem == element) {
                System.arraycopy(list, i+1, list, i, llength-(i+1));
                return elem;
            }
        }
        return null;
    }

    /**
     * Removes the first element that is functionally equal to the
     * supplied element (<code>list[idx].equals(element)</code>). The
     * elements after the removed element will be slid down the array one
     * spot to fill the place of the removed element. Passing a null
     * <code>element</code> to this function will call
     * <code>equals(null)</code> on all objects in the list which may
     * cause them to choke, so don't do that unless you mean it.
     *
     * @return the object that was removed from the array or null if no
     * matching object was found.
     */
    public static Object removeEqual (Object[] list, Object element)
    {
        int llength = list.length; // no optimizing bastards
        for (int i = 0; i < llength; i++) {
            Object elem = list[i];
            if (elem != null && elem.equals(element)) {
                System.arraycopy(list, i+1, list, i, llength-(i+1));
                return elem;
            }
        }
        return null;
    }

    /**
     * Removes the element at the specified index. The elements after the
     * removed element will be slid down the array one spot to fill the
     * place of the removed element. If a null array is supplied or one
     * that is not large enough to accomodate this index, null is
     * returned.
     *
     * @return the object that was removed from the array or null if no
     * object existed at that location.
     */
    public static Object remove (Object[] list, int index)
    {
        int llength = list.length;
        if (list == null || llength <= index) {
            return null;
        }

        Object elem = list[index];
        System.arraycopy(list, index+1, list, index, llength-(index+1));
        return elem;
    }

    /**
     * Creates a new list that will accomodate the specified index and
     * copies the contents of the old list to the first.
     */
    protected static Object[] accomodate (Object[] list, int index)
    {
        int size = list.length;
        // expand size by powers of two until we're big enough
        while (size <= index) {
            size *= 2;
        }

        // create a new list and copy the contents
        Object[] newlist = new Object[size];
        System.arraycopy(list, 0, newlist, 0, list.length);
        return newlist;
    }

    /**
     * Run some tests.
     */
    public static void main (String[] args)
    {
        Object[] list = null;
        String foo = "foo";
        String bar = "bar";

        list = ListUtil.add(list, foo);
        System.out.println("Added foo: " + StringUtil.toString(list));

        list = ListUtil.add(list, bar);
        System.out.println("Added bar: " + StringUtil.toString(list));

        ListUtil.clear(list, foo);
        System.out.println("Cleared foo: " + StringUtil.toString(list));

        String newBar = "bar";
        ListUtil.clearEqual(list, newBar);
        System.out.println("Cleared newBar: " + StringUtil.toString(list));

        list = ListUtil.add(list, 0, foo);
        list = ListUtil.add(list, 1, bar);
        System.out.println("Added foo+bar: " + StringUtil.toString(list));

        ListUtil.remove(list, foo);
        System.out.println("Removed foo: " + StringUtil.toString(list));

        list = ListUtil.add(list, 0, foo);
        list = ListUtil.add(list, 1, bar);
        System.out.println("Added foo+bar: " + StringUtil.toString(list));

        ListUtil.remove(list, 0);
        System.out.println("Removed 0: " + StringUtil.toString(list));
    }

    /**
     * The size of a list to create if we have to create one entirely
     * from scratch rather than just expand it.
     */
    protected static final int DEFAULT_LIST_SIZE = 4;
}
