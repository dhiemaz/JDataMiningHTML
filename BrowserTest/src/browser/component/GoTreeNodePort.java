/*
 *  Copyright (c) Northwoods Software Corporation, 2001-2008. All Rights
 *  Reserved.
 *
 *  Restricted Rights: Use, duplication, or disclosure by the U.S.
 *  Government is subject to restrictions as set forth in subparagraph
 *  (c) (1) (ii) of DFARS 252.227-7013, or in FAR 52.227-19, or in FAR
 *  52.227-14 Alt. III, as applicable.
 *
 */

package browser.component;

import java.awt.*;
import java.util.Vector;
import javax.swing.tree.*;
import com.nwoods.jgo.*;

/**
 * GoTreeNodePort is a JGoPort that is meant to work with the GoTreeNode class.
 * <p>
 * Each GoTreeNodePort has a TreePath distinguishing it, as well a parent
 * GoTreeNodePort (that will be null for the port corresponding to the root of
 * the tree) and some number of children GoTreeNodePorts.  There are methods
 * for getting and setting the TreePath and ParentPort properties, and there
 * are methods for manipulating the list of child ports.
 * <p>
 * Each GoTreeNodePort has a MinLinks and a MaxLinks property, which indicate
 * the "proper" bounds on the number of GoTreeNodePort to GoTreeNodePort links.
 * Furthermore, GoTreeNodePort does not allow linking to another GoTreeNodePort
 * on the same GoTreeNode, nor to one on another node that is at a different
 * level in the tree.  The isValidSource, isValidDestination, and validLink
 * methods are overridden to enforce the MaxLinks property and to disallow
 * level crossing links.  To allow the user to start connecting an unconnected
 * graph, however, the MinLinks property is not considered for link validation.
 *
 * @see GoTreeNode
 */
public class GoTreeNodePort extends JGoPort
{
  public GoTreeNodePort()
  {
    setStyle(GoTreeNodePort.StyleRectangle);
  }
  public GoTreeNodePort(TreePath path)
  {
    setStyle(GoTreeNodePort.StyleRectangle);
    myPath = path;
  }

  public JGoObject copyObject(JGoCopyEnvironment env)
  {
    GoTreeNodePort newtnp = (GoTreeNodePort)super.copyObject(env);
    if (newtnp != null) {
      newtnp.myPath = myPath;
      newtnp.myMinLinks = myMinLinks;
      newtnp.myMaxLinks = myMaxLinks;

      // copy myParentPort and myChildPorts by first registering this
      // port and then copying the other ones
      GoTreeNode oldtn = getGoTreeNode();
      GoTreeNode newtn = (GoTreeNode)env.get(oldtn);
      if (newtn != null) {
        newtn.mapPathToPort(newtnp.getTreePath(), newtnp);

        GoTreeNodePort parent = findCopiedTNP(env, newtn, getParentPort());

        int numchildren = getChildCount();
        newtnp.setChildCount(numchildren);
        for (int i = 0; i < numchildren; i++) {
          GoTreeNodePort child = findCopiedTNP(env, newtn, getChild(i));
          newtnp.setChild(i, child);
        }
      }
    }
    return newtnp;
  }

  private GoTreeNodePort findCopiedTNP(JGoCopyEnvironment env, GoTreeNode newtn, GoTreeNodePort oldtnp)
  {
    if (oldtnp == null)
      return null;
    Object newobj = env.get(oldtnp);
    if (newobj != null && newobj instanceof GoTreeNodePort) {
      return (GoTreeNodePort)newobj;
    } else {
      GoTreeNodePort newtnp = (GoTreeNodePort)oldtnp.copyObject(env);
      if (newtnp.getParent() != newtn)
        newtn.addObjectAtTail(newtnp);
      return newtnp;
    }
  }

  /**
   * We override the normal definition of isPointInObj in order to
   * avoid a user mouse press starting to draw a link in the tree-structure
   * area of the row to the left of the label (icon and text), particularly
   * where the expand/collapse action occurs.
   */
  public boolean isPointInObj(Point pnt)
  {
    return pnt.x > getLabelLeft() &&
           getBoundingRect().contains(pnt.x, pnt.y);
  }

  /** This convenience method returns this port's parent GoTreeNode area. */
  public GoTreeNode getGoTreeNode() { return (GoTreeNode)getParent(); }

  /** Return the TreePath that this port is associated with in the TreeModel. */
  public TreePath getTreePath() { return myPath; }

  /** Change the TreePath that this port is associated with in the TreeModel. */
  public void setTreePath(TreePath path)
  {
    TreePath old = myPath;
    if (!old.equals(path)) {
      GoTreeNode tn = getGoTreeNode();
      if (old != null && tn != null) {
        tn.mapPathToPort(old, null);
      }
      myPath = path;
      if (path != null && tn != null) {
        tn.mapPathToPort(path, this);
      }
      update(ChangedTreePath, 0, old);
    }
  }

  /** Return the GoTreeNodePort that is associated with this port's parent TreeNode. */
  public GoTreeNodePort getParentPort() { return myParentPort; }


  public int getChildCount()
  {
    if (myChildPorts != null)
      return myChildPorts.size();
    else
      return 0;
  }
  public void setChildCount(int n)
  {
    if (n <= 0) {
      myChildPorts = null;
    } else {
      if (myChildPorts == null) myChildPorts = new Vector();
      myChildPorts.setSize(n);
    }
  }
  public GoTreeNodePort getChild(int index)
  {
    if (myChildPorts != null)
      return (GoTreeNodePort)myChildPorts.get(index);
    else
      return null;
  }
  public void setChild(int index, GoTreeNodePort p)
  {
    if (myChildPorts == null) myChildPorts = new Vector(index+1);
    if (index >= myChildPorts.size()) myChildPorts.setSize(index+1);
    myChildPorts.set(index, p);
    p.myParentPort = this;
  }
  public void removeChild(GoTreeNodePort p)
  {
    if (p == null) return;
    if (p.getParentPort() != this) return;
    if (myChildPorts != null) {
      myChildPorts.remove(p);
      p.myParentPort = null;
    }
  }
  public void removeChild(int index) { removeChild(getChild(index)); }

  /**
   * Return how deep this port is in the tree, or how many parents it has.
   * The port for the tree's root will return zero.
   */
  public int getLevel()
  {
    GoTreeNodePort p = getParentPort();
    if (p == null)
      return 0;
    else
      return 1 + p.getLevel();
  }

  /** Return the minimum number of links that this port should have. */
  public int getMinLinks() { return myMinLinks; }

  /** Change the minimum number of links that this port should have. */
  public void setMinLinks(int n)
  {
    int old = myMinLinks;
    if (old != n && n >= 0) {
      myMinLinks = n;
      update(ChangedMinLinks, old, null);
    }
  }

  /** Return the maximum number of links that this port should have. */
  public int getMaxLinks() { return myMaxLinks; }

  /** Change the maximum number of links that this port should have. */
  public void setMaxLinks(int n)
  {
    int old = myMaxLinks;
    if (old != n && n >= 0) {
      myMaxLinks = n;
      update(ChangedMaxLinks, old, null);
    }
  }

  /**
   * Return the number of real links that are connected
   * to this port.  This is more meaningful than the getNumLinks method
   * because it ignores any temporary or invisible links.
   */
  public int getNumValidLinks()
  {
    int num = 0;
    JGoListPosition pos = getFirstLinkPos();
    while (pos != null) {
      JGoLink link = getLinkAtPos(pos);
      pos = getNextLinkPos(pos);
      if (link.isVisible() &&
          link.getDocument() != null &&
          link.getFromPort().getDocument() != null &&
          link.getToPort().getDocument() != null)
        num++;
    }
    return num;
  }

  /**
   * This override ensures users cannot make more than getMaxLinks() links
   * at this port.  This method uses the getNumValidLinks() method to
   * count how many real links there are at this port.
   * <p>
   * If getMaxLinks() returns zero, no linking is permitted from this port--
   * JGoView.startNewLink() will return false.
   */
  public boolean isValidSource()
  {
    return getMaxLinks() > 0 &&
           getNumValidLinks() < getMaxLinks() &&
           super.isValidSource();
  }

  /**
   * This override ensures users cannot make more than getMaxLinks() links
   * at this port.  This method uses the getNumValidLinks() method to
   * count how many real links there are at this port.
   * <p>
   * If getMaxLinks() returns zero, no linking is permitted from this port--
   * JGoView.startNewLink() will return false.
   */
  public boolean isValidDestination()
  {
    return getMaxLinks() > 0 &&
           getNumValidLinks() < getMaxLinks() &&
           super.isValidDestination();
  }

  /**
   * This implements some additional policies on linking:
   * <ul>
   * <li> don't allow linking from this port to another port on the same node
   * <li> if there are already getMaxLinks() on the TO port, don't permit more
   * <li> don't allow linking to another GoTreeNodePort at a different level in the tree
   *        if both GoTreeNodes don't allow cross level links
   * </ul>
   */
  public boolean validLink(JGoPort to)
  {
    if (getParent() == to.getParent()) return false;
    if (to instanceof GoTreeNodePort) {
      GoTreeNode thistn = getGoTreeNode();
      GoTreeNodePort totnp = (GoTreeNodePort)to;
      GoTreeNode totn = totnp.getGoTreeNode();
      if (thistn != null &&
          !thistn.isAllowingCrossLevelLinks() &&
          totn != null &&
          !totn.isAllowingCrossLevelLinks() &&
          getLevel() != totnp.getLevel())
        return false;
    }
    return super.validLink(to);
  }

  /** This override returns the TreePath for this port, as a string. */
  public String getToolTipText()
  {
    if (getTreePath() != null)
      return getTreePath().toString();
    else
      return null;
  }

  /** Return the left bound of the label in the JTree for this port. */
  public final int getLabelLeft() { return myLabelLeft; }

  /**
   * Change the left bound of the label in the JTree for this port.
   * This is not considered to be part of the state of the port.
   */
  public final void setLabelLeft(int x) { myLabelLeft = x; }

  /** Return the right bound of the label in the JTree for this port. */
  public final int getLabelRight() { return myLabelRight; }

  /**
   * Change the right bound of the label in the JTree for this port.
   * This is not considered to be part of the state of the port.
   */
  public final void setLabelRight(int x) { myLabelRight = x; }

  public void copyNewValueForRedo(JGoDocumentChangedEdit e)
  {
    switch (e.getFlags()) {
      case ChangedTreePath:
        e.setNewValue(getTreePath());
        return;
      case ChangedMinLinks:
        e.setNewValueInt(getMinLinks());
        return;
      case ChangedMaxLinks:
        e.setNewValueInt(getMaxLinks());
        return;
      default:
        super.copyNewValueForRedo(e);
        return;
    }
  }

  public void changeValue(JGoDocumentChangedEdit e, boolean undo)
  {
    switch (e.getFlags()) {
      case ChangedTreePath:
        setTreePath((TreePath)e.getValue(undo));
        return;
      case ChangedMinLinks:
        setMinLinks(e.getValueInt(undo));
        return;
      case ChangedMaxLinks:
        setMaxLinks(e.getValueInt(undo));
        return;
      default:
        super.changeValue(e, undo);
        return;
    }
  }

  public static final int ChangedTreePath = 1020;
  public static final int ChangedMinLinks = 1021;
  public static final int ChangedMaxLinks = 1022;

  private TreePath myPath = null;
  private GoTreeNodePort myParentPort = null;
  private Vector myChildPorts = null;
  private int myMinLinks = 0;
  private int myMaxLinks = 999999;
  private int myLabelLeft = 0;
  private int myLabelRight = 0;
}
