/*
 *  Copyright (c) Northwoods Software Corporation, 1998-2008. All Rights
 *  Reserved.
 *
 *  Restricted Rights: Use, duplication, or disclosure by the U.S.
 *  Government is subject to restrictions as set forth in subparagraph
 *  (c) (1) (ii) of DFARS 252.227-7013, or in FAR 52.227-19, or in FAR
 *  52.227-14 Alt. III, as applicable.
 *
 */

package com.nwoods.jgo.examples.jtreeapp;

import java.awt.*;
import java.awt.event.*;
import java.awt.dnd.*;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import com.nwoods.jgo.*;


/**
 * A JGoControl containing a JTree.  A JGoTree allows a JTree component
 * to be drawn along with other JGoObjects.  The JTree displayed by this
 * JGoObject supports Autoscroll.  This JGoTree also acts as a
 * TreeModelListener on the TreeModel.
 * <p>
 * Note that, like all other JGoControls, the JComponents created
 * to be displayed as the representation of these JGoObjects will
 * appear in front of (on top of) all other JGoObjects.  Thus it
 * is wise to either use these objects as view objects or make sure
 * they don't overlap with other document objects.
 */
public class JGoTree extends JGoControl implements TreeModelListener
{
    //==============================================================
    // Construction
    //==============================================================

  /**
   * Constructs a new JGoTree.
   * Set the TreeModel before adding to a document or view.
   */
  public JGoTree()
  {
    super();
  }

  /**
   * Create a new tree control with the given bounding rectangle.
   * Set the TreeModel before adding to a document or view.
   *
   * @param rect the bounding rectangle
   */
  public JGoTree(Rectangle rect)
  {
    super(rect);
  }

  /**
   * Construct a tree at a given location with a given size.
   * Set the TreeModel before adding to a document or view.
   *
   * @param loc the upper left corner, in document coordinates
   * @param size the dimensions of the rectangle, in document coordinates
   */
  public JGoTree(Point location, Dimension size)
  {
    super(location, size);
  }


  /**
   * WARNING: Copying a GoTreeNode instance will not work unless:
   * <ul>
   * <li> the TreeModel is copied correctly
   * <li> the expanded/collapsed and scrolled state of the JTree is copied faithfully
   * </ul>
   */
  public JGoObject copyObject(JGoCopyEnvironment env)
  {
    JGoTree newobj = (JGoTree)super.copyObject(env);
    if (newobj != null) {
      newobj.myModel = myModel;  // share models???
    }
    return newobj;
  }

  /**
   * Return a Swing JTree that will represent this control on
   * the screen.  Create the JTree if not already created.
   * The JTree will use (share) the JGoTree's TreeModel property,
   * so that all the JTrees, in different views, for a particular
   * JGoTree instance will share the same model.
   * <p>
   * By default the returned JComponent is not a JTree but a
   * JScrollPane that has a JTree in its viewport.
   *
   * @param view the view for which this control should be created
   * @return the Swing JComponent that will represent this control
   */
  public JComponent createComponent(JGoView view)
  {
    JGoJTree tree;
    TreeModel model = getTreeModel();
    if (model != null) {
      tree = new JGoJTree(model);
    } else {
      tree = new JGoJTree();
      model = tree.getModel();
      setTreeModel(model);
    }
    tree.setJGoTree(this);

    enableListeners(true, tree, view);

    // make the tree "transparent" to dragging
    // Need to account for the differing coordinate systems, because the
    // DropTargetEvent's Location is in the JTree's coordinates,
    // not in the JGoView's coordinates.
    new DropTarget(tree, new DTListener(tree, view));

    JScrollPane pane = new JScrollPane(tree);
    tree.setScrollPane(pane);

    // make the scroll bars transparent to drops
    JScrollBar bar = pane.getHorizontalScrollBar();
    if (bar != null)
      new DropTarget(bar, new DTListener(bar, view));
    bar = pane.getVerticalScrollBar();
    if (bar != null)
      new DropTarget(bar, new DTListener(bar, view));

    return pane;
  }

  /**
   * Because this TreeModelListener may depend on the layout of the
   * paths in the tree, we must wait until the JTree has been created
   * and added to a view before we make sure all the appropriate
   * GoTreeNodePorts have been created by calling treeNodesInserted.
   */
  public JComponent getComponent(JGoView view)
  {
    if (myFirstView == null) {
      myFirstView = view;
      JComponent comp = super.getComponent(view);

      TreeModel model = getTreeModel();
      Object root = model.getRoot();
      Object[] rootpath = new Object[1];
      rootpath[0] = root;
      TreeModelEvent tme = new TreeModelEvent(root, rootpath);
      treeNodesInserted(tme);

      return comp;
    } else {
      return super.getComponent(view);
    }
  }

  /**
   * This method is needed to make sure the mouse and drag gesture listeners
   * are ordered correctly compared to the standard ones set up by the TreeUI.
   */
  public void enableListeners(boolean enable, JTree tree, JGoView view)
  {
    if (myMListener == null)
      myMListener = new MListener(tree, view);
    if (myDGListener == null)
      myDGListener = new DGListener(tree, view);

    if (enable && !myListenersInitialized) {
      // make the tree a transparent mouse listener
      tree.addMouseListener(myMListener);
      tree.addMouseMotionListener(myMListener);
      myDGRecognizer = DragSource.getDefaultDragSource().createDefaultDragGestureRecognizer(tree,
                                DnDConstants.ACTION_COPY_OR_MOVE, myDGListener);
      myListenersInitialized = true;
    } else if (myDGRecognizer != null) {
      tree.removeMouseListener(myMListener);
      tree.removeMouseMotionListener(myMListener);
      myDGRecognizer.removeDragGestureListener(myDGListener);
      myListenersInitialized = false;
    }
  }


  /**
   * This extension of JTree implements Autoscroll and wraps the standard
   * TreeModelListener.  The latter is needed so that the JGoTree TreeModelListener
   * methods get called after the regular JTree TreeModelListener methods are called.
   */
  public static class JGoJTree extends JTree implements Autoscroll
  {
    public JGoJTree() {}
    public JGoJTree(TreeModel m) { super(m); }

    void setJGoTree(JGoTree t)
    {
      myJGoTree = t;
      if (treeModelListener != null && treeModelListener instanceof TMListener)
        ((TMListener)treeModelListener).setJGoTree(t);
    }

    protected TreeModelListener createTreeModelListener()
    {
      TreeModelListener tml = super.createTreeModelListener();
      return new TMListener(myJGoTree, tml);
    }

    void setScrollPane(JScrollPane pane) { myScrollPane = pane; }

    /**
     * Return the Insets needed to make autoscrolling work correctly within a JScrollPane.
     * The size of each inset is determined by JGoTree.getAutoscrollMargin().
     */
    public Insets getAutoscrollInsets()
    {
      if (myScrollPane == null) return new Insets(0, 0, 0, 0);

      int margin = myJGoTree.getAutoscrollMargin();
      Dimension size = getSize();
      Dimension vsize = myScrollPane.getViewport().getSize();
      Point vpos = myScrollPane.getViewport().getViewPosition();
      return new Insets(vpos.y + margin, vpos.x + margin,
                        (size.height - (vpos.y + vsize.height)) + margin,
                        (size.width - (vpos.x + vsize.width)) + margin);
    }

    /**
     * Autoscroll occurs when the point (during drag-and-drop dragging)
     * is within the margin (as specified by JGoTree.getAutoscrollMargin)
     * along the outside of the JTree.  When the pointer is within the
     * outermost fourth of that margin, the JTree is scrolled by the block
     * increment; otherwise it is scrolled by the unit increment.
     */
    public void autoscroll(Point location)
    {
      if (myScrollPane == null) return;

      int margin = myJGoTree.getAutoscrollMargin();
      Dimension size = getSize();
      Rectangle rect = myScrollPane.getViewport().getViewRect();

      int oldy = rect.y;
      int newy = oldy;
      if (location.y < rect.y + margin/4) {
        newy = oldy - getScrollableBlockIncrement(rect, SwingConstants.VERTICAL, -1);
        newy = Math.max(newy, 0);
      } else if (location.y > rect.y + rect.height - margin/4) {
        newy = oldy + getScrollableBlockIncrement(rect, SwingConstants.VERTICAL, 1);
        newy = Math.min(newy, size.height - rect.height);
      } else if (location.y < rect.y + margin) {
        newy = oldy - getScrollableUnitIncrement(rect, SwingConstants.VERTICAL, -1);
        newy = Math.max(newy, 0);
      } else if (location.y > rect.y + rect.height - margin) {
        newy = oldy + getScrollableUnitIncrement(rect, SwingConstants.VERTICAL, 1);
        newy = Math.min(newy, size.height - rect.height);
      }

      int oldx = rect.x;
      int newx = oldx;
      if (location.x < rect.x + margin/4) {
        newx = oldx - getScrollableBlockIncrement(rect, SwingConstants.HORIZONTAL, -1);
        newx = Math.max(newx, 0);
      } else if (location.x > rect.x + rect.width - margin/4) {
        newx = oldx + getScrollableBlockIncrement(rect, SwingConstants.HORIZONTAL, 1);
        newx = Math.min(newx, size.width - rect.width);
      } else if (location.x < rect.x + margin) {
        newx = oldx - getScrollableUnitIncrement(rect, SwingConstants.HORIZONTAL, -1);
        newx = Math.max(newx, 0);
      } else if (location.x > rect.x + rect.width - margin) {
        newx = oldx + getScrollableUnitIncrement(rect, SwingConstants.HORIZONTAL, 1);
        newx = Math.min(newx, size.width - rect.width);
      }

      if (newy - oldy != 0 ||
          newx - oldx != 0) {
        Point pt = new Point(rect.x + (newx-oldx), rect.y + (newy-oldy));
        myScrollPane.getViewport().setViewPosition(pt);
      }
    }

    private JScrollPane myScrollPane = null;
    private JGoTree myJGoTree = null;
    private TreeModelListener myOrigTML = null;

    static class TMListener implements TreeModelListener
    {
      public TMListener(JGoTree t, TreeModelListener tml ) { myJGoTree = t; myOrigTML = tml; }

      void setJGoTree(JGoTree t) { myJGoTree = t; }

      public void treeNodesChanged(TreeModelEvent evt)
      {
        myOrigTML.treeNodesChanged(evt);
        myJGoTree.treeNodesChanged(evt);
      }
      public void treeNodesInserted(TreeModelEvent evt)
      {
        myOrigTML.treeNodesInserted(evt);
        myJGoTree.treeNodesInserted(evt);
      }
      public void treeNodesRemoved(TreeModelEvent evt)
      {
        myOrigTML.treeNodesRemoved(evt);
        myJGoTree.treeNodesRemoved(evt);
      }
      public void treeStructureChanged(TreeModelEvent evt)
      {
        myOrigTML.treeStructureChanged(evt);
        myJGoTree.treeStructureChanged(evt);
      }

      private JGoTree myJGoTree = null;
      private TreeModelListener myOrigTML = null;
    }
  }

  static class MListener implements MouseListener, MouseMotionListener
  {
    public MListener(JComponent c, JGoView v) { myComp = c; myView = v; }

    public void mouseClicked(MouseEvent e) {}
    public void mousePressed(MouseEvent e) { myView.requestFocus(); myView.getCanvas().mousePressed(makeMouseEvent(e)); }
    public void mouseReleased(MouseEvent e) { myView.getCanvas().mouseReleased(makeMouseEvent(e)); }
    public void mouseEntered(MouseEvent e) {}
    public void mouseExited(MouseEvent e) {}

    public void mouseMoved(MouseEvent e) { myView.getCanvas().mouseMoved(makeMouseEvent(e)); }
    public void mouseDragged(MouseEvent e) { myView.getCanvas().mouseDragged(makeMouseEvent(e)); }

    public MouseEvent makeMouseEvent(MouseEvent e)
    {
      Point p = adjustPoint(e.getPoint());
      return new MouseEvent(e.getComponent(), e.getID(), e.getWhen(), e.getModifiers(),
                            p.x, p.y, e.getClickCount(), e.isPopupTrigger());
    }

    public Point adjustPoint(Point orig)
    {
      Point viewPnt = myComp.getLocationOnScreen();
      Point viewLoc = myView.getCanvas().getLocationOnScreen();
      return new Point(viewPnt.x - viewLoc.x + orig.x,
                       viewPnt.y - viewLoc.y + orig.y);
    }

    private JComponent myComp;
    private JGoView myView;
  }

  static class DGListener implements DragGestureListener
  {
    public DGListener(JComponent o, JGoView v) { myComp = o; myView = v; }

    // DragGestureListener support
    public void dragGestureRecognized(DragGestureEvent e)
    {
      myView.onDragGestureRecognized(makeDragGestureEvent(e));
    }

    public DragGestureEvent makeDragGestureEvent(DragGestureEvent e)
    {
      myView.requestFocus();
      Point viewPnt = adjustPoint(e.getDragOrigin());
      Point docPnt = myView.viewToDocCoords(viewPnt);

      // for JDK < 1.5:
      java.util.List evts = Arrays.asList(e.toArray());

      // for JDK >= 1.5:
      //java.util.List<InputEvent> evts = new ArrayList<InputEvent>();
      //Iterator<InputEvent> i = e.iterator();
      //while (i.hasNext()) evts.add(i.next());

      return new DragGestureEvent(e.getSourceAsDragGestureRecognizer(),
                                  e.getDragAction(),
                                  viewPnt,
                                  evts);
    }

    public Point adjustPoint(Point orig)
    {
      Point viewPnt = myComp.getLocationOnScreen();
      Point viewLoc = myView.getCanvas().getLocationOnScreen();
      return new Point(viewPnt.x - viewLoc.x + orig.x,
                       viewPnt.y - viewLoc.y + orig.y);
    }

    private JComponent myComp;
    private JGoView myView;
  }

  static class DTListener implements DropTargetListener
  {
    public DTListener(JComponent o, JGoView v) { myComp = o; myView = v; }

    public void dragEnter(DropTargetDragEvent e) { myView.onDragEnter(makeDragEvent(e)); }
    public void dragOver(DropTargetDragEvent e) { myView.onDragOver(makeDragEvent(e)); }
    public void dropActionChanged(DropTargetDragEvent e) { myView.onDropActionChanged(makeDragEvent(e)); }
    public void dragExit(DropTargetEvent e) { myView.onDragExit(e); }
    public void drop(DropTargetDropEvent e) { myView.onDrop(makeDropEvent(e)); }

    public DropTargetDragEvent makeDragEvent(DropTargetDragEvent e)
    {
      return new DropTargetDragEvent(e.getDropTargetContext(), 
                                     adjustPoint(e.getLocation()),
                                     e.getDropAction(), e.getSourceActions());
    }

    public DropTargetDropEvent makeDropEvent(DropTargetDropEvent e)
    {
      return new DropTargetDropEvent(e.getDropTargetContext(), 
                                     adjustPoint(e.getLocation()),
                                     e.getDropAction(), e.getSourceActions());
    }

    public Point adjustPoint(Point orig)
    {
      Point viewPnt = myComp.getLocationOnScreen();
      Point viewLoc = myView.getCanvas().getLocationOnScreen();
      return new Point(viewPnt.x - viewLoc.x + orig.x,
                       viewPnt.y - viewLoc.y + orig.y);
    }

    private JComponent myComp;
    private JGoView myView;
  }

  /**
   * Return the JTree in a particular view.  This is useful because the
   * JTree is normally inside a JScrollPane.
   */
  public JTree getJTree(JGoView view)
  {
    if (view == null) view = myFirstView;
    if (view == null) return null;
    JComponent comp = getComponent(view);
    if (comp instanceof JScrollPane) {
      JScrollPane pane = (JScrollPane)comp;
      return (JTree)pane.getViewport().getView();
    } else {
      return (JTree)comp;
    }
  }

  public JGoView getFirstView() { return myFirstView; }


  /**
   * Return the TreeModel that will be associated with each JTree
   * for this JGoTree (one JTree per JGoView).
   */
  public TreeModel getTreeModel()
  {
    return myModel;
  }

  /**
   * Change the TreeModel associated with all of the JTrees for this JGoTree.
   * The model can only be set once, before any instances have been added to
   * any documents or views.
   */
  public void setTreeModel(TreeModel model)
  {
    TreeModel oldmodel = myModel;
    if (oldmodel == null && model != null) {
      myModel = model;
/* ??? can't re-set model due to listener ordering problems
      Iterator it = getIterator();
      while (it.hasNext()) {
        Map.Entry pair = (Map.Entry)it.next();
        JComponent comp = (JComponent)pair.getValue();
        JTree tree = null;
        if (comp instanceof JScrollPane) {
          JScrollPane pane = (JScrollPane)comp;
          tree = (JTree)pane.getViewport().getView();
        } else if (comp instanceof JTree) {
          tree = (JTree)comp;
        }
        if (tree != null) {
          tree.setModel(myModel);
        }
      }
*/
    }
  }

  // JGoTree does not have any child JGoObjects that it
  // needs to keep in sync with the TreeModel, so these
  // methods are no-ops.  But other classes might have such objects.

  /** By default this TreeModelListener method is a no-op. */
  public void treeNodesChanged(TreeModelEvent evt) {}

  /** By default this TreeModelListener method is a no-op. */
  public void treeNodesInserted(TreeModelEvent evt) {}

  /** By default this TreeModelListener method is a no-op. */
  public void treeNodesRemoved(TreeModelEvent evt) {}

  /** By default this TreeModelListener method is a no-op. */
  public void treeStructureChanged(TreeModelEvent evt) {}


  /**
   * Return the width of the region in each JTree where a drag-and-drop
   * dragging will automatically cause the JTree to be scrolled in the JScrollPane.
   */
  public int getAutoscrollMargin()
  {
    return myAutoscrollMargin;
  }

  /**
   * Change the autoscroll margin width for all of this control's JTrees.
   *
   * @param m the new width, which must be non-negative
   */
  public void setAutoscrollMargin(int m)
  {
    int old = myAutoscrollMargin;
    if (m >= 0 && m != old) {
      myAutoscrollMargin = m;
      update(ChangedAutoscrollMargin, old, null);
    }
  }

  public void copyNewValueForRedo(JGoDocumentChangedEdit e)
  {
    switch (e.getFlags()) {
      case ChangedAutoscrollMargin:
        e.setNewValueInt(getAutoscrollMargin());
        return;
      default:
        super.copyNewValueForRedo(e);
        return;
    }
  }

  public void changeValue(JGoDocumentChangedEdit e, boolean undo)
  {
    switch (e.getFlags()) {
      case ChangedAutoscrollMargin:
        setAutoscrollMargin(e.getValueInt(undo));
        return;
      default:
        super.changeValue(e, undo);
        return;
    }
  }

  public static final int ChangedAutoscrollMargin = 1011;

  private TreeModel myModel = null;
  private int myAutoscrollMargin = 12;

  transient private JGoView myFirstView = null;
  transient private boolean myListenersInitialized = false;
  transient private MListener myMListener = null;
  transient private DGListener myDGListener = null;
  transient private DragGestureRecognizer myDGRecognizer = null;
}
