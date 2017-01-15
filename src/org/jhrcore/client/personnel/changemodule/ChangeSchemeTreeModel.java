/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.personnel.changemodule;

import java.util.Hashtable;
import java.util.List;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.task.IModuleCode;

/**
 *
 * @author hflj
 */
public class ChangeSchemeTreeModel extends DefaultTreeModel implements IModuleCode{

    private CheckTreeNode rootNode = new CheckTreeNode("ËùÓÐÄ£°å");
    Hashtable<String, CheckTreeNode> group_nodes = new Hashtable<String, CheckTreeNode>();
    private String module_code = "";

    public ChangeSchemeTreeModel(List lsit_scheme) {
        super(new CheckTreeNode());
        this.setRoot(rootNode);
        buildSchemeTree(lsit_scheme);
    }

    public ChangeSchemeTreeModel() {
        super(new CheckTreeNode());
        this.setRoot(rootNode);
    }

    public void buildSchemeTree(List lsit_scheme) {
        group_nodes.clear();
        rootNode.removeAllChildren();
        for (Object obj : lsit_scheme) {
            addNode((ChangeScheme) obj);
        }
    }

    public void addNode(ChangeScheme cs) {
        CheckTreeNode class_node = new CheckTreeNode(cs);
        if (cs.getChangeScheme_type() == null || cs.getChangeScheme_type().trim().equals("")) {
            rootNode.add(class_node);
        } else {
            String group_name = cs.getChangeScheme_type().trim();
            CheckTreeNode group_node = group_nodes.get(group_name);
            if (group_node == null) {
                group_node = new CheckTreeNode(group_name);
                rootNode.add(group_node);
                group_nodes.put(group_name, group_node);
            }
            group_node.add(class_node);
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
