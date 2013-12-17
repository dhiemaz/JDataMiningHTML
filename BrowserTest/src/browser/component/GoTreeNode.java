package browser.component;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import com.nwoods.jgo.*;


/**
 * A GoTreeNode is a JGoArea that is composed of one JGoTree and a collection
 * of JGoPorts, one for each TreePath or TreeNode in the JGoTree's TreeModel.
 * In fact, the JGoTree is actually implemented by the GoTree class, and
 * the ports are implemented by the GoTreeNodePort class.
 * <p>
 * After creating a GoTreeNode, you should call initialize to specify a TreeModel.
 * The initialization will automatically create GoTreeNodePorts for each TreePath
 * in the TreeModel.
 * <p>
 * You can get the port for any path using the getPort method.  GoTreeNodePort
 * also has a TreePath property so that you can identify which port it is.
 * <p>
 * The LinkSource and LinkDestination properties control the default behavior
 * for whether links come from or go to the GoTreeNodePorts of this GoTreeNode.
 * The AllowingCrossLevelLinks property controls whether links that connect
 * GoTreeNodePorts at different levels in their trees are valid.
 * To control how many links can connect to a particular port, see the MaxLinks
 * property on GoTreeNodePort.
 *
 * @see GoTreeNodePort
 * @see GoTree
 */
public class GoTreeNode extends JGoNode
{
  public GoTreeNode() {}

  // internal for now...
  void initialize(TreeModel model, boolean linkSrc, boolean linkDst)
  {
    if (myTree == null) {
      myTree = new GoTree();
      myTree.setTreeModel(model);
      addObjectAtTail(myTree);
    }
    setLinkSource(linkSrc);
    setLinkDestination(linkDst);
  }

  public void initialize(TreeModel model, boolean linkSrc, boolean linkDst, JGoView view)
  {
    initialize(model, linkSrc, linkDst);

    // this produces all the initial ports as a side effect of creating the JTree
    // in the view the first time
    myTree.getJTree(view);
  }

  /**
   * WARNING: Copying a GoTreeNode instance will not work unless:
   * <ul>
   * <li> the underlying TreeModel of the JGoTree child is copied correctly
   * <li> the expanded/collapsed and scrolled state of the JGoTree's JTree is copied faithfully
   * </ul>
   */
  public JGoObject copyObject(JGoCopyEnvironment env)
  {
    GoTreeNode newobj = (GoTreeNode)super.copyObject(env);
    if (newobj != null) {
      // myTree will be copied in copyChildren
      newobj.myLinkSource = myLinkSource;
      newobj.myLinkDestination = myLinkDestination;
      // myMap will be initialized by copyChildren
    }
    return newobj;
  }

  /**
   * Copying all of the GoTreeNodePorts is accomplished by the GoTreeNodePort copyObject
   * method, which maintains the internal tree relationship between the ports.
   */
  protected void copyChildren(JGoArea newarea, JGoCopyEnvironment env)
  {
    GoTreeNode tn = (GoTreeNode)newarea;
    tn.myTree = (GoTree)myTree.copyObject(env);
    tn.addObjectAtTail(tn.myTree);

    JGoListPosition pos = getFirstObjectPos();
    while (pos != null) {
      JGoObject obj = getObjectAtPos(pos);
      pos = getNextObjectPos(pos);

      if (obj == myTree) continue;

      // see if the GoTreeNodePort was already copied
      Object newobj = env.get(obj);
      if (newobj == null) {
        JGoObject newchild = obj.copyObject(env);
        newarea.addObjectAtTail(newchild);
      }
    }
  }

  /**
   * This override calls layoutChildren() if the width or height of the area
   * have changed.  Otherwise, it just uses the default behavior for dealing
   * with an area that has been moved.
   */
  protected void geometryChange(Rectangle prevRect)
  {
    if (prevRect.width != getWidth() ||
        prevRect.height != getHeight()) {
      layoutChildren();
    } else {
      super.geometryChange(prevRect);
    }
  }

  /**
   * When the area's bounding rectangle has been changed,
   * this method updates the position and size of all of the
   * children.  It first makes the GoTree the same size as
   * this area, and then calls layoutPorts().
   */
  protected void layoutChildren()
  {
    // here we assume the ports do not "stick out" from the bounds of the JGoTree
    Rectangle bounds = getBoundingRect();
    myTree.setBoundingRect(bounds);
    layoutPorts();
  }

  /**
   * Resynchronize this area's collection of ports with all of the TreePaths.
   * This method will remove any ports that are no longer needed, because
   * their corresponding paths have been removed from the TreeModel.
   */
  protected void resetAllPorts()
  {
    myOldMap = myMap;
    myMap = new HashMap();
    layoutPorts();

    Iterator i = myOldMap.values().iterator();
    while (i.hasNext()) {
      Object obj = i.next();
      if (!myMap.containsKey(obj) && obj instanceof TreePath) {
        TreePath path = (TreePath)obj;
        GoTreeNodePort p = (GoTreeNodePort)myMap.get(path);
        removeObject(p);
      }
    }
    myOldMap = null;
  }

  /**
   * Synchronize this area's collection of ports with the collection and positions
   * of all of the TreePaths.  Assume that no paths have been removed, so ports may
   * need to be created but not removed.
   * <p>
   * Since this requires a JTree, and therefore a JGoView, in order to be successful,
   * this area must already have been added to a visible document or to a view.
   */
  protected void layoutPorts()
  {
    JTree tree = myTree.getJTree(null);
    JGoView view = null;
    int offsetx = 0;
    int offsety = 0;
    if (tree != null) {
      view = myTree.getFirstView();
      Point treePnt = tree.getLocationOnScreen();
      Point viewPnt = view.getCanvas().getLocationOnScreen();
      offsetx = treePnt.x - viewPnt.x;
      offsety = treePnt.y - viewPnt.y;
    }

    TreeModel model = myTree.getTreeModel();
    if (model != null) {
      Object root = model.getRoot();
      if (root != null) {
        traverseTree(model, new TreePath(root), view, tree, offsetx, offsety, 0, 0);
      }
    }
  }

  /**
   * Perform a tree-walk of the given TreeModel starting at the given TreePath.
   * It makes sure there is a GoTreeNodePort for each path, located at the same bounds
   * as the TreePath in the JTree.
   * <p>
   * To adjust for the different coordinate systems, the JGoView and the position of the
   * JTree in the JGoView (offsetx, offsety) are passed in.
   * <p>
   * To help position hidden/invisible paths, either because the path is scrolled away
   * or because the path has a collapsed parent, the last visible path bounds are also
   * passed in.  Because we assume the tree is laid out vertically, we actually only need
   * the last Y and Height (lasty and lasth).
   */
  protected GoTreeNodePort traverseTree(TreeModel model, TreePath path, JGoView view, JTree tree,
                                        int offsetx, int offsety, int lasty, int lasth)
  {
    GoTreeNodePort parent = getPort(path);
    if (view != null && tree != null) {
      Rectangle bounds = getBoundingRect();
      if (tree.isVisible(path)) {
        Rectangle rect = tree.getPathBounds(path);
        if (rect != null) {
          rect.x += offsetx;  // change from tree to view coordinates
          rect.y += offsety;
          view.convertViewToDoc(rect);  // change to doc coordinates
          parent.setLabelLeft(rect.x);
          parent.setLabelRight(rect.x + rect.width);
          // for now, assume port is full width of tree
          rect.x = bounds.x;
          rect.width = bounds.width-1;
          // position carefully vertically
          if (rect.y < bounds.y) {
            if (rect.y + rect.height < bounds.y) {
              rect.height = 0;
            } else {
              rect.height = rect.y + rect.height - bounds.y;
            }
            rect.y = bounds.y;
          } else if (rect.y + rect.height > bounds.y + bounds.height -1) {
            if (rect.y > bounds.y) {
              rect.height = 0;
            } else {
              rect.height = bounds.y + bounds.height - 1 - rect.y;
            }
            rect.y = bounds.y + bounds.height - 1 - rect.height;
          }
          parent.setVisible(true);
          parent.setBoundingRect(rect);
          if (myMergeInvisiblePorts) {
            lasty = rect.y;
            lasth = rect.height;
          } else {
            lasty = rect.y + rect.height;
            lasth = 0;
          }
        }
      } else {
        parent.setBoundingRect(bounds.x, lasty, bounds.width-1, lasth);
        parent.setVisible(false);
      }
    }

    Object p = path.getLastPathComponent();
    int numchildren = model.getChildCount(p);
    parent.setChildCount(numchildren);
    for (int i = 0; i < numchildren; i++) {
      Object c = model.getChild(p, i);
      TreePath childpath = path.pathByAddingChild(c);
      GoTreeNodePort child = traverseTree(model, childpath, view, tree, offsetx, offsety, lasty, lasth);
      parent.setChild(i, child);
      if (myMergeInvisiblePorts) {
        lasty = child.getTop();
        lasth = child.getHeight();
      } else {
        lasty = child.getTop() + child.getHeight();
        lasth = 0;
      }
    }

    return parent;
  }

  /**
   * Remove the given port from this area after recursively
   * removing all of its children ports.
   */
  public void removePort(GoTreeNodePort port)
  {
    if (port == null) return;
    for (int i = port.getChildCount()-1; i >= 0; i--) {
      GoTreeNodePort p = port.getChild(i);
      removePort(p);
      port.removeChild(p);
    }
    mapPathToPort(port.getTreePath(), null);  // remove from hashtable
    removeObject(port);
  }
  

  /** Return the one GoTree that this area contains. */
  public GoTree getTree() { return myTree; }


  // Associating TreePaths with GoTreeNodePorts

  /**
   * Return the GoTreeNodePort associated with a given TreePath.
   * This will return null if there is no such port.
   */
  public GoTreeNodePort findPort(TreePath path)
  {
    GoTreeNodePort p = (GoTreeNodePort)myMap.get(path);
    if (p != null)
      return p;
    if (myOldMap != null)
      p = (GoTreeNodePort)myOldMap.get(path);
    return p;
  }

  /**
   * Return the GoTreeNodePort associated with a given TreePath.
   * This will create one, if needed, by calling makePort.
   */
  public GoTreeNodePort getPort(TreePath path)
  {
    if (path == null)
      return null;
    GoTreeNodePort p = findPort(path);
    if (p == null) {
      p = makePort(path);
    }
    return p;
  }

  // internal for now...
  void mapPathToPort(TreePath path, GoTreeNodePort port)
  {
    if (port == null)
      myMap.remove(path);
    else
      myMap.put(path, port);
  }

  /**
   * Return a new GoTreeNodePort associated with a given TreePath.
   * The port is already added to this area.
   * <p>
   * This will remember the association between the TreePath and the
   * new port, so that later calls to findPort with the same TreePath
   * will return that port.
   */
  public GoTreeNodePort makePort(TreePath path)
  {
    GoTreeNodePort p = new GoTreeNodePort(path);
    mapPathToPort(path, p);
    p.setValidDestination(isLinkDestination());
    p.setValidSource(isLinkSource());
    p.setBrush(null);
    p.setLocation(getTopLeft());
    addObjectAtTail(p);
    return p;
  }

  /** Return the initial setting for newly created GoTreeNodePort's isValidSource(). */
  public boolean isLinkSource() { return myLinkSource; }

  /** Change the initial setting for newly created GoTreeNodePort's isValidSource(). */
  public void setLinkSource(boolean b)
  {
    boolean old = myLinkSource;
    if (b != old) {
      myLinkSource = b;
      update(ChangedLinkSource, old ? 1 : 0, null);
    }
  }

  /** Return the initial setting for newly created GoTreeNodePort's isValidDestination(). */
  public boolean isLinkDestination() { return myLinkDestination; }

  /** Change the initial setting for newly created GoTreeNodePort's isValidDestination(). */
  public void setLinkDestination(boolean b)
  {
    boolean old = myLinkDestination;
    if (b != old) {
      myLinkDestination = b;
      update(ChangedLinkDestination, old ? 1 : 0, null);
    }
  }

  /**
   * Return whether a link is valid if it connects two GoTreeNodePorts at
   * different levels in their respective trees.
   */
  public boolean isAllowingCrossLevelLinks() { return myAllowingCrossLevelLinks; }

  /**
   * Change whether a link is valid if it connects two GoTreeNodePorts at
   * different levels in their respective trees.  Both GoTreeNodes must disallow
   * cross level links for a link to be invalid.
   */
  public void setAllowingCrossLevelLinks(boolean b)
  {
    boolean old = myAllowingCrossLevelLinks;
    if (b != old) {
      myAllowingCrossLevelLinks = b;
      update(ChangedAllowingCrossLevelLinks, old ? 1 : 0, null);
    }
  }

  public void copyNewValueForRedo(JGoDocumentChangedEdit e)
  {
    switch (e.getFlags()) {
      case ChangedLinkSource:
        e.setNewValueBoolean(isLinkSource());
        return;
      case ChangedLinkDestination:
        e.setNewValueBoolean(isLinkDestination());
        return;
      case ChangedAllowingCrossLevelLinks:
        e.setNewValueBoolean(isLinkSource());
        return;
      default:
        super.copyNewValueForRedo(e);
        return;
    }
  }

  public void changeValue(JGoDocumentChangedEdit e, boolean undo)
  {
    switch (e.getFlags()) {
      case ChangedLinkSource:
        setLinkSource(e.getValueBoolean(undo));
        return;
      case ChangedLinkDestination:
        setLinkDestination(e.getValueBoolean(undo));
        return;
      case ChangedAllowingCrossLevelLinks:
        setAllowingCrossLevelLinks(e.getValueBoolean(undo));
        return;
      default:
        super.changeValue(e, undo);
        return;
    }
  }

  public static final int ChangedLinkSource = 1020;
  public static final int ChangedLinkDestination = 1021;
  public static final int ChangedAllowingCrossLevelLinks = 1022;

  private GoTree myTree = null;
  private HashMap myMap = new HashMap();
  private HashMap myOldMap = null;
  private boolean myLinkSource = true;
  private boolean myLinkDestination = false;
  private boolean myAllowingCrossLevelLinks = false;
  private boolean myMergeInvisiblePorts = true;
}
