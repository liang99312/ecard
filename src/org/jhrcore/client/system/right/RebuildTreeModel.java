/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.right;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.base.EntityClass;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.FieldDef;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.right.Role;
import org.jhrcore.entity.right.RoleRightTemp;

/**
 *
 * @author yangzhou
 */
public class RebuildTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = 1L;
    private DefaultMutableTreeNode rootNode = getRootNode();

    public DefaultMutableTreeNode getRootNode() {
        RoleRightTemp roleRightTemp = new RoleRightTemp();
        roleRightTemp.setTemp_name("各模块业务字段表");
        roleRightTemp.setFun_flag(0);
        return new DefaultMutableTreeNode(roleRightTemp);
    }

    public RebuildTreeModel(Role role,List list) {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
        buildTree(role,list);
    }
    private void buildTree(Role role,List list) {
        List<String> set_fun = null;
        if(role!=null&&!role.getRole_code().equalsIgnoreCase("ROOT")){
            set_fun = (List<String>) CommUtil.selectSQL("select field_name from RoleField rf where role_key='"+role.getRole_key()+"'");
        }
        rootNode.removeAllChildren();
        for (Object obj : list) {
            ModuleInfo mi = (ModuleInfo) obj;
            DefaultMutableTreeNode tn = new DefaultMutableTreeNode(mi);
            rootNode.add(tn);
            for (int k = 0; k < mi.getEntityClasss().size(); k++) {
                EntityClass et = (EntityClass) mi.getEntityClasss().toArray()[k];
                for (int i = 0; i < et.getEntityDefs().size(); i++) {
                    EntityDef ed = (EntityDef) et.getEntityDefs().toArray()[i];
                    if (UserContext.hasEntityViewRight(ed.getEntityName())) {
                        DefaultMutableTreeNode tn2 = new DefaultMutableTreeNode(ed);
                        tn.add(tn2);
                        for (int j = 0; j < ed.getFieldDefs().size(); j++) {
                            FieldDef fd = (FieldDef) ed.getFieldDefs().toArray()[j];
                            String tmp_str = ed.getEntityName() + "." + fd.getField_name();
                            if(set_fun!=null&&!set_fun.contains(tmp_str)){
                                continue;
                            }
                            if (UserContext.hasFieldRight(fd.getEntityDef().getEntityName().toUpperCase() + "." + fd.getField_name().toUpperCase())) {
                                DefaultMutableTreeNode tn3 = new DefaultMutableTreeNode(
                                        fd);
                                tn2.add(tn3);
                            }
                        }
                        if(tn2.getChildCount() == 0){
                            tn2.removeFromParent();
                        }
                    }

                }
            }
            if(tn.getChildCount() == 0){
                tn.removeFromParent();
            }
        }
    }
}
    

