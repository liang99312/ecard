/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.comm;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.client.CommUtil;
import org.jhrcore.entity.base.EntityClass;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.FieldDef;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.base.TempGroup;

/**
 *
 * @author hflj
 */
public class ModuleTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = 1L;
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("全部");

    public ModuleTreeModel() {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
        buildTree();
    }

    public DefaultMutableTreeNode getRootNode() {
        return rootNode;
    }

    private void buildTree() {
        rootNode.removeAllChildren();
        List<?> list = CommUtil.fetchEntities("from ModuleInfo mi  left join fetch mi.entityClasss et left join fetch et.entityDefs ed left join fetch ed.fieldDefs where mi.module_key<>'ZHTJ' and used=1 order by mi.order_no");
        for (Object obj : list) {
            ModuleInfo mi = (ModuleInfo) obj;
           if(mi.getModule_code().equals("ssjk"))continue;//过滤实时监控
            DefaultMutableTreeNode tn = new DefaultMutableTreeNode(mi);
            rootNode.add(tn);
            for (int k = 0; k < mi.getEntityClasss().size(); k++) {
                EntityClass et = (EntityClass) mi.getEntityClasss().toArray()[k];
                DefaultMutableTreeNode tn1 = new DefaultMutableTreeNode(et);
                tn.add(tn1);
                for (int i = 0; i < et.getEntityDefs().size(); i++) {
                    EntityDef ed = (EntityDef) et.getEntityDefs().toArray()[i];
                    DefaultMutableTreeNode tn2 = new DefaultMutableTreeNode(
                            ed);
                    TempGroup group1 = new TempGroup();
                    group1.setGroup_name("已选");
                    group1.setChange_flag(0);
                    TempGroup group2 = new TempGroup();
                    group2.setGroup_name("备选");
                    group2.setChange_flag(0);
                    DefaultMutableTreeNode selectNode = new DefaultMutableTreeNode(group1);
                    DefaultMutableTreeNode unSelectNode = new DefaultMutableTreeNode(group2);
                    tn2.add(selectNode);
                    tn2.add(unSelectNode);
                    tn1.add(tn2);
                    if (ed.getFieldDefs() != null) {
                        for (int j = 0; j < ed.getFieldDefs().size(); j++) {
                            FieldDef fd = (FieldDef) ed.getFieldDefs().toArray()[j];
                            if (fd.isVisible()) {
                                if (fd.isUsed_flag()) {
                                    DefaultMutableTreeNode tn3 = new DefaultMutableTreeNode(
                                            fd);
                                    selectNode.add(tn3);
                                } else {
                                    DefaultMutableTreeNode tn4 = new DefaultMutableTreeNode(
                                            fd);
                                    unSelectNode.add(tn4);
                                }
                            }
                        }
                    }

                }
            }
        }
    }
}
