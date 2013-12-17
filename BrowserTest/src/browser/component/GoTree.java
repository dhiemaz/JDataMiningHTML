package browser.component;

import java.awt.event.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import com.nwoods.jgo.*;
import com.nwoods.jgo.examples.jtreeapp.JGoTree;


/**
 * GoTree is an extension of JGoTree that is meant to work with the
 * GoTreeNode class.
 * <p>
 * A TreeExpansionListener updates the positions of the
 * GoTreeNodePorts that are associated with the TreeNodes that may
 * have moved or changed visibility.
 * <p>
 * A ComponentListener updates the positions of the ports when the JTree
 * is moved.
 * <p>
 * The TreeModelListener methods invoke appropriate GoTreeNode methods
 * to add or remove the ports associated with TreeNodes that were
 * inserted or removed.
 *
 * @see GoTreeNode
 */
public class GoTree extends JGoTree
{
  public GoTree() { setSelectable(false); }

  /**
   * This overrides JGoTree's createComponent in order to turn on
   * autoscrolling and add a bunch of listeners so that it can keep the
   * ports associated with TreePaths up-to-date.
   */
  public JComponent createComponent(JGoView view)
  {
    JComponent comp = super.createComponent(view);

    JTree tree = null;
    if (comp instanceof JTree) {
      tree = (JTree)comp;
    } else if (comp instanceof JScrollPane) {
      JScrollPane pane = (JScrollPane)comp;
      tree = (JTree)pane.getViewport().getView();
    }

    if (tree != null) {
      tree.setAutoscrolls(true);

      // due to problems with the order of listeners, we have to remove the
      // mouse and drag gesture listeners here, and add them again at the end.
      enableListeners(false, tree, view);

      // handle changes in the tree shape
      GoTreeNode parent = getGoTreeNode();
      tree.addTreeExpansionListener(new JTListener(parent, view));

      SwingUtilities.updateComponentTreeUI(tree);  // needed for pre-JDK1.3 listener re-ordering

      tree.addComponentListener(new CListener(parent, view));

      enableListeners(true, tree, view);
    }

    return comp;
  }


  /** This convenience method returns this port's parent GoTreeNode area. */
  public GoTreeNode getGoTreeNode() { return (GoTreeNode)getParent(); }


  // TreeModelListener methods

  /**
   * This TreeModelListener method handles new TreeNodes by laying out all
   * the ports again.
   * <p>
   * Someday a more efficient method can be implemented that only does
   * the necessary incremental work.
   */
  public void treeNodesInserted(TreeModelEvent evt)
  {
    getGoTreeNode().layoutPorts();
  }

  /**
   * This TreeModelListener method handles removed TreeNodes by removing
   * the respective ports (recursively including child ports), and then
   * laying out all the remaining ports again.
   * <p>
   * Someday a more efficient method can be implemented that only does
   * the necessary incremental work.
   */
  public void treeNodesRemoved(TreeModelEvent evt)
  {
    TreePath path = evt.getTreePath();
    Object[] children = evt.getChildren();
    TreeModel model = getTreeModel();
    GoTreeNode gonode = getGoTreeNode();
    for (int i = 0; i < children.length; i++) {
      Object child = children[i];
      TreePath childpath = path.pathByAddingChild(child);
      GoTreeNodePort p = gonode.findPort(childpath);
      gonode.removePort(p);
    }
    gonode.layoutPorts();
  }

  /**
   * This TreeModelListener method handles arbitrary tree changes by
   * calling the GoTreeNode.resetAllPorts method.
   */
  public void treeStructureChanged(TreeModelEvent evt)
  {
    getGoTreeNode().resetAllPorts();
  }
   
    // these are not anonymous inner classes to avoid a bug with Visual Age for Java 3.0 & 3.5
    static class JTListener implements TreeExpansionListener
    {
      public JTListener(GoTreeNode t, JGoView v) { myTreeNode = t; myView = v; }

      public void treeCollapsed(TreeExpansionEvent evt)
      {
        myTreeNode.layoutPorts();
      }
      public void treeExpanded(TreeExpansionEvent evt)
      {
        myTreeNode.layoutPorts();
      }

      private GoTreeNode myTreeNode;
      private JGoView myView;
    }

    static class CListener extends ComponentAdapter
    {
      public CListener(GoTreeNode t, JGoView v) { myTreeNode = t; myView = v; }

      public void componentMoved(ComponentEvent e)
      {
        myTreeNode.layoutPorts();
      }

      private GoTreeNode myTreeNode;
      private JGoView myView;
    }
}
