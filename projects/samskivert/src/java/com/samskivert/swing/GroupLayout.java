//
// $Id: GroupLayout.java,v 1.8 2004/02/25 13:17:41 mdb Exp $
//
// samskivert library - useful routines for java programs
// Copyright (C) 2001 Michael Bayne
// 
// This library is free software; you can redistribute it and/or modify it
// under the terms of the GNU Lesser General Public License as published
// by the Free Software Foundation; either version 2.1 of the License, or
// (at your option) any later version.
//
// This library is distributed in the hope that it will be useful,
// but WITHOUT ANY WARRANTY; without even the implied warranty of
// MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
// Lesser General Public License for more details.
//
// You should have received a copy of the GNU Lesser General Public
// License along with this library; if not, write to the Free Software
// Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA 02111-1307 USA

package com.samskivert.swing;

import java.awt.*;

import javax.swing.JComponent;
import javax.swing.JPanel;

import java.util.HashMap;

/**
 * Group layout managers lay out widgets in horizontal or vertical groups.
 */
public abstract class GroupLayout
    implements LayoutManager2
{
    /**
     * The group layout managers supports two constraints: fixedness
     * and weight. A fixed component will not be stretched along the major
     * axis of the group. Those components that are stretched will have
     * the extra space divided among them according to their weight
     * (specifically receiving the ratio of their weight to the total
     * weight of all of the free components in the container).
     *
     * <p/> If a constraints object is constructed with fixedness set to
     * true and with a weight, the weight will be ignored.
     */
    public static class Constraints
    {
	/** Whether or not this component is fixed. */
	public boolean fixed = false;

	/**
	 * The weight of this component relative to the other components
	 * in the container.
	 */
	public int weight = 1;

	/**
	 * Constructs a new constraints object with the specified
	 * fixedness and weight.
	 */
	public Constraints (boolean fixed)
	{
	    this.fixed = fixed;
	}

	/**
	 * Constructs a new constraints object with the specified
	 * fixedness and weight.
	 */
	public Constraints (int weight)
	{
	    this.weight = weight;
	}
    }

    /** A class used to make our policy constants type-safe. */
    public static class Policy
    {
        int code;

        public Policy (int code)
        {
            this.code = code;
        }
    }

    /** A class used to make our policy constants type-safe. */
    public static class Justification
    {
        int code;

        public Justification (int code)
        {
            this.code = code;
        }
    }

    /**
     * A constraints object that indicates that the component should be
     * fixed and have the default weight of one. This is so commonly used
     * that we create and make this object available here.
     */
    public final static Constraints FIXED = new Constraints(true);

    /**
     * Do not adjust the widgets on this axis.
     */
    public final static Policy NONE = new Policy(0);

    /**
     * Stretch all the widgets to their maximum possible size on this
     * axis.
     */
    public final static Policy STRETCH = new Policy(1);

    /**
     * Stretch all the widgets to be equal to the size of the largest
     * widget on this axis.
     */
    public final static Policy EQUALIZE = new Policy(2);

    /**
     * Only valid for off-axis policy, this leaves widgets alone unless
     * they are larger in the off-axis direction than their container, in
     * which case it constrains them to fit on the off-axis.
     */
    public final static Policy CONSTRAIN = new Policy(3);

    /** A justification constant. */
    public final static Justification CENTER = new Justification(0);

    /** A justification constant. */
    public final static Justification LEFT = new Justification(1);

    /** A justification constant. */
    public final static Justification RIGHT = new Justification(2);

    /** A justification constant. */
    public final static Justification TOP = new Justification(3);

    /** A justification constant. */
    public final static Justification BOTTOM = new Justification(4);

    public void setPolicy (Policy policy)
    {
	_policy = policy;
    }

    public Policy getPolicy ()
    {
	return _policy;
    }

    public void setOffAxisPolicy (Policy offpolicy)
    {
	_offpolicy = offpolicy;
    }

    public Policy getOffAxisPolicy ()
    {
	return _offpolicy;
    }

    public void setGap (int gap)
    {
	_gap = gap;
    }

    public int getGap ()
    {
	return _gap;
    }

    public void setJustification (Justification justification)
    {
	_justification = justification;
    }

    public Justification getJustification ()
    {
	return _justification;
    }

    public void setOffAxisJustification (Justification justification)
    {
	_offjust = justification;
    }

    public Justification getOffAxisJustification ()
    {
	return _offjust;
    }

    public void addLayoutComponent (String name, Component comp)
    {
	// nothing to do here
    }

    public void removeLayoutComponent (Component comp)
    {
	if (_constraints != null) {
	    _constraints.remove(comp);
	}
    }

    public void addLayoutComponent (Component comp, Object constraints)
    {
	if (constraints != null) {
	    if (constraints instanceof Constraints) {
		if (_constraints == null) {
		    _constraints = new HashMap();
		}
		_constraints.put(comp, constraints);

	    } else {
		throw new RuntimeException("GroupLayout constraints " +
					   "object must be of type " +
					   "GroupLayout.Constraints");
	    }
	}
    }

    public float getLayoutAlignmentX (Container target)
    {
	// we don't support alignment like this
	return 0f;
    }

    public float getLayoutAlignmentY (Container target)
    {
	// we don't support alignment like this
	return 0f;
    }

    public Dimension minimumLayoutSize (Container parent)
    {
	return getLayoutSize(parent, MINIMUM);
    }

    public Dimension preferredLayoutSize (Container parent)
    {
	return getLayoutSize(parent, PREFERRED);
    }

    public Dimension maximumLayoutSize (Container parent)
    {
	return getLayoutSize(parent, MAXIMUM);
    }

    protected abstract Dimension getLayoutSize (Container parent, int type);

    public abstract void layoutContainer (Container parent);

    public void invalidateLayout (Container target)
    {
	// nothing to do here
    }

    protected boolean isFixed (Component child)
    {
	if (_constraints == null) {
	    return false;
	}

	Constraints c = (Constraints)_constraints.get(child);
	if (c != null) {
	    return c.fixed;
	}

	return false;
    }

    protected int getWeight (Component child)
    {
	if (_constraints == null) {
	    return 1;
	}

	Constraints c = (Constraints)_constraints.get(child);
	if (c != null) {
	    return c.weight;
	}

	return 1;
    }

    /**
     * Computes dimensions of the children widgets that are useful for the
     * group layout managers.
     */
    protected DimenInfo computeDimens (Container parent, int type)
    {
	int count = parent.getComponentCount();
	DimenInfo info = new DimenInfo();
	info.dimens = new Dimension[count];

	for (int i = 0; i < count; i++) {
	    Component child = parent.getComponent(i);
	    if (!child.isVisible()) {
		continue;
	    }

	    Dimension csize;
	    switch  (type) {
	    case MINIMUM:
		csize = child.getMinimumSize();
		break;

	    case MAXIMUM:
		csize = child.getMaximumSize();
		break;

	    default:
		csize = child.getPreferredSize();
		break;
	    }

	    info.count++;
	    info.totwid += csize.width;
	    info.tothei += csize.height;

	    if (csize.width > info.maxwid) {
		info.maxwid = csize.width;
	    }
	    if (csize.height > info.maxhei) {
		info.maxhei = csize.height;
	    }

	    if (isFixed(child)) {
		info.fixwid += csize.width;
		info.fixhei += csize.height;
		info.numfix++;

	    } else {
		info.totweight += getWeight(child);

                if (csize.width > info.maxfreewid) {
                    info.maxfreewid = csize.width;
                }
                if (csize.height > info.maxfreehei) {
                    info.maxfreehei = csize.height;
                }
	    }

	    info.dimens[i] = csize;
	}

	return info;
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * HGroupLayout} with a configuration conducive to containing a row of
     * buttons.
     */
    public static JPanel makeButtonBox (Justification justification)
    {
        return new JPanel(new HGroupLayout(NONE, justification));
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * HGroupLayout} with a configuration conducive to containing a row of
     * buttons. The supplied button is added to the box.
     */
    public static JPanel makeButtonBox (
        Justification justification, JComponent button)
    {
        JPanel box = new JPanel(new HGroupLayout(NONE, justification));
        box.add(button);
        box.setOpaque(false);
        return box;
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * HGroupLayout} with the default configuration.
     */
    public static JPanel makeHBox ()
    {
        return new JPanel(new HGroupLayout());
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * HGroupLayout} with a configuration that stretches in both
     * directions, with the specified gap.
     */
    public static JPanel makeHStretchBox (int gap)
    {
        return new JPanel(new HGroupLayout(STRETCH, STRETCH, gap, CENTER));
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * HGroupLayout} with the specified on-axis policy (default
     * configuration otherwise).
     */
    public static JPanel makeHBox (Policy policy)
    {
        return new JPanel(new HGroupLayout(policy));
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * HGroupLayout} with the specified on-axis policy and justification
     * (default configuration otherwise).
     */
    public static JPanel makeHBox (Policy policy, Justification justification)
    {
        return new JPanel(new HGroupLayout(policy, justification));
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * HGroupLayout} with the specified on-axis policy, justification and
     * off-axis policy (default configuration otherwise).
     */
    public static JPanel makeHBox (Policy policy, Justification justification,
                                   Policy offAxisPolicy)
    {
        return new JPanel(new HGroupLayout(policy, offAxisPolicy,
                                           DEFAULT_GAP, justification));
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * VGroupLayout} with the default configuration.
     */
    public static JPanel makeVBox ()
    {
        return new JPanel(new VGroupLayout());
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * VGroupLayout} with the specified on-axis policy (default
     * configuration otherwise).
     */
    public static JPanel makeVBox (Policy policy)
    {
        return new JPanel(new VGroupLayout(policy));
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * VGroupLayout} with the specified on-axis policy and justification
     * (default configuration otherwise).
     */
    public static JPanel makeVBox (Policy policy, Justification justification)
    {
        return new JPanel(new VGroupLayout(policy, justification));
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * VGroupLayout} with the specified on-axis policy, justification and
     * off-axis policy (default configuration otherwise).
     */
    public static JPanel makeVBox (Policy policy, Justification justification,
                                   Policy offAxisPolicy)
    {
        return new JPanel(new VGroupLayout(policy, offAxisPolicy,
                                           DEFAULT_GAP, justification));
    }

    /**
     * Creates a {@link JPanel} that is configured with an {@link
     * VGroupLayout} with a configuration that stretches in both
     * directions, with the specified gap.
     */
    public static JPanel makeVStretchBox (int gap)
    {
        return new JPanel(new VGroupLayout(STRETCH, STRETCH, gap, CENTER));
    }

    protected Policy _policy = NONE;
    protected Policy _offpolicy = CONSTRAIN;
    protected int _gap = DEFAULT_GAP;
    protected Justification _justification = CENTER;
    protected Justification _offjust = CENTER;

    protected HashMap _constraints;

    protected static final int MINIMUM = 0;
    protected static final int PREFERRED = 1;
    protected static final int MAXIMUM = 2;

    protected static final int DEFAULT_GAP = 5;
}
