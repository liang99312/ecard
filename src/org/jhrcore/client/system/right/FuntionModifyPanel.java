/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.right;

import javax.swing.JPanel;
import java.awt.BorderLayout;
import java.util.List;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.DbUtil;
import org.jhrcore.entity.right.FuntionRight;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.renderer.HRRendererView;

public class FuntionModifyPanel extends JPanel {

    private static final long serialVersionUID = 1L;
    private JTree funTree;
    private FunTreeModel funTreeModel;
    private boolean justSecondModule = false;

    public FuntionModifyPanel() {
        this(false);
    }

    public FuntionModifyPanel(boolean flag) {
        super(new BorderLayout());
        this.justSecondModule = flag;
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        funTreeModel = new FunTreeModel(justSecondModule);
        funTree = new JTree(funTreeModel);
        funTree.setSelectionRow(1);
        funTree.expandRow(1);
        HRRendererView.getFunMap(funTree).initTree(funTree);
        JScrollPane scrollPane = new JScrollPane(funTree);
        scrollPane.setSize(300, 600);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    private void setupEvents() {
    }

    class FunTreeModel extends DefaultTreeModel {

        private static final long serialVersionUID = 1L;
        private CheckTreeNode rootNode;

        public CheckTreeNode getRootNode() {
            return new CheckTreeNode(CommUtil.fetchEntityBy("from FuntionRight fr where fr.fun_parent_code='ROOT'"));
        }

        public FunTreeModel(boolean justSecondModule) {
            super(new CheckTreeNode());
            rootNode = getRootNode();
            this.setRoot(rootNode);
            buildTree(justSecondModule);
        }

        public void buildTree(boolean justSecondNode) {
            rootNode.removeAllChildren();
            String hql = "from FuntionRight where fun_parent_code !='ROOT' and granted=1";
            if (justSecondNode) {
                String lenName = DbUtil.getLength_strForDB(CommUtil.getSQL_dialect());
                hql += " and " + lenName + "(fun_code)<=6 ";
            }
            hql += " order by fun_code";
            List<?> list = CommUtil.fetchEntities(hql);
            CheckTreeNode tmp = rootNode;
            for (Object obj : list) {
                FuntionRight funtionRight = (FuntionRight) obj;
                //若非业务模块，且级数大于三级 则判定不需要显示在树中
                if (justSecondNode && !funtionRight.isModule_flag()) {
                    continue;
                }
                while (tmp != rootNode && !((FuntionRight) tmp.getUserObject()).getFun_code().equals(
                        funtionRight.getFun_parent_code())) {
                    tmp = (CheckTreeNode) tmp.getParent();
                }
                CheckTreeNode cur = new CheckTreeNode(funtionRight);
                tmp.add(cur);
                tmp = cur;
            }
        }
    }

    public JTree getFunTree() {
        return funTree;
    }
}
