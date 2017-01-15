/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.comm;

import java.util.ArrayList;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.Code;

/**
 *
 * @author yangzhou
 */
public class CodeTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = 1L;
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("全部");
    private DefaultMutableTreeNode node_used;
    private DefaultMutableTreeNode node_notused;
    private List all_codes = new ArrayList();
    private List<DefaultMutableTreeNode> select_nodes = new ArrayList<DefaultMutableTreeNode>();
    public List<String> rootName = new ArrayList();

    public CodeTreeModel() {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
        buildTree();
    }

    public List<DefaultMutableTreeNode> getSelect_nodes() {
        return select_nodes;
    }

    private void buildTree(List<Code> list, DefaultMutableTreeNode cur_node) {
        DefaultMutableTreeNode root_node = cur_node;
        for (Code code : list) {
            DefaultMutableTreeNode tn2 = new DefaultMutableTreeNode(code);
            while (true) {
                if (!(cur_node.getUserObject() instanceof Code)) {
                    break;
                }
                if (code.getParent_id().equals("ROOT")) {
                    cur_node = root_node;
                    break;
                }
                Code parent_code = (Code) cur_node.getUserObject();
                if (code.getParent_id().toUpperCase().equals(parent_code.getCode_id().toUpperCase()) && code.getCode_tag().toUpperCase().equals((parent_code.getCode_tag().substring(0, 2).toUpperCase() + code.getCode_id().toUpperCase()))) {
                    break;
                }
                cur_node = (DefaultMutableTreeNode) cur_node.getParent();
            }
            cur_node.add(tn2);
            cur_node = tn2;
        }
    }

    public DefaultMutableTreeNode getNode_notused() {
        return node_notused;
    }

    public void setNode_notused(DefaultMutableTreeNode node_notused) {
        this.node_notused = node_notused;
    }

    public DefaultMutableTreeNode getNode_used() {
        return node_used;
    }

    public void setNode_used(DefaultMutableTreeNode node_used) {
        this.node_used = node_used;
    }

    private void buildTree() {
        rootNode.removeAllChildren();
        List<Code> select_codes = new ArrayList<Code>();
        List<Code> unselect_codes = new ArrayList<Code>();
        all_codes.addAll(CommUtil.fetchEntities("from Code cd  order by cd.code_tag"));
        for (Object obj : all_codes) {
            Code c = (Code) obj;
            if (!UserContext.hasCodeViewRight(c)) {
                continue;
            }
            if ("ROOT".equals(c.getParent_id())) {
                rootName.add(c.getCode_id());
            }
            if (c.isUsed()) {
                select_codes.add(c);
            } else {
                unselect_codes.add(c);
            }
        }
        DefaultMutableTreeNode cur_node = new DefaultMutableTreeNode("已选");
        select_nodes.add(cur_node);
        node_used = cur_node;
        rootNode.add(cur_node);
        buildTree(select_codes, cur_node);
        cur_node = new DefaultMutableTreeNode("备选");
        select_nodes.add(cur_node);
        node_notused = cur_node;
        rootNode.add(cur_node);
        buildTree(unselect_codes, cur_node);
    }
}
