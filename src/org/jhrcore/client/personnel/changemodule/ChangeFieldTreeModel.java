/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.changemodule;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.BasePersonChange;
import org.jhrcore.entity.annotation.ClassAnnotation;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeField;
import org.jhrcore.entity.change.ChangeItem;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.msg.emp.EmpChangeSchemeMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.task.IModuleCode;

/**
 *
 * @author mxliteboss
 */
public class ChangeFieldTreeModel extends DefaultTreeModel implements IModuleCode{

    private static final long serialVersionUID = 1L;
    private ChangeScheme changeScheme;
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("全部");
    private String module_code = "";

    @SuppressWarnings("unchecked")
    
    
    public ChangeFieldTreeModel(ChangeScheme changeScheme) {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
        this.changeScheme = changeScheme;
        buildTree();
    }

    public ChangeFieldTreeModel() {
        super(new DefaultMutableTreeNode());
        buildTree();
    }

    private void buildTree() {
        rootNode.removeAllChildren();
        ClassAnnotation ca = A01.class.getAnnotation(ClassAnnotation.class);
        DefaultMutableTreeNode node_a01 = new DefaultMutableTreeNode(ca.displayName());
        rootNode.add(node_a01);
        List<TempFieldInfo> a01_fields = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : a01_fields) {
            node_a01.add(new DefaultMutableTreeNode(tfi));
        }
//        DefaultMutableTreeNode node_main_change = new DefaultMutableTreeNode("变动主表");
        DefaultMutableTreeNode node_main_change = new DefaultMutableTreeNode(EmpChangeSchemeMsg.ttl002.toString());
        rootNode.add(node_main_change);
        List<TempFieldInfo> fields = EntityBuilder.getDeclareFieldInfoListOf(BasePersonChange.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : fields) {
            node_main_change.add(new DefaultMutableTreeNode(tfi));
        }
        DefaultMutableTreeNode node_new_item = new DefaultMutableTreeNode(EmpChangeSchemeMsg.ttl003.toString());
        rootNode.add(node_new_item);
        for (ChangeItem ci : changeScheme.getChangeItems()) {
            TempFieldInfo tfi = new TempFieldInfo();
            tfi.setCaption_name(EmpChangeSchemeMsg.msg026 + ci.getDisplayName());
            tfi.setField_name("old_" + ci.getFieldName());
            tfi.setEntity_name("BasePersonChange");
            tfi.setEntity_caption(EmpChangeSchemeMsg.ttl003.toString());
            tfi.setField_type(ci.getField_type());
            node_new_item.add(new DefaultMutableTreeNode(tfi));
            tfi = new TempFieldInfo();
            tfi.setCaption_name(EmpChangeSchemeMsg.msg027 + ci.getDisplayName());
            tfi.setField_name("new_" + ci.getFieldName());
            tfi.setEntity_name("BasePersonChange");
            tfi.setEntity_caption(EmpChangeSchemeMsg.ttl003.toString());
            tfi.setField_type(ci.getField_type());
            node_new_item.add(new DefaultMutableTreeNode(tfi));
        }
        for (ChangeField changeField : changeScheme.getChangeFields()) {
            if (changeField.isFrom_import() || (EmpChangeSchemeMsg.msg028.toString()).equals(changeField.getC_type())) {
                continue;
            }
            DefaultMutableTreeNode node_Appendix = null;
            for (int i = 0; i < rootNode.getChildCount(); i++) {
                DefaultMutableTreeNode tmp_node = (DefaultMutableTreeNode) rootNode.getChildAt(i);
                if (tmp_node.getUserObject().equals(changeField.getAppendix_displayname())) {
                    node_Appendix = tmp_node;
                    break;
                }
            }
            if (node_Appendix == null) {
                node_Appendix = new DefaultMutableTreeNode(changeField.getAppendix_displayname());
                rootNode.add(node_Appendix);
            }
            TempFieldInfo tfi = new TempFieldInfo();
            tfi.setField_name(changeField.getAppendix_field());
            tfi.setCaption_name(changeField.getAppendix_field_displayName());
            tfi.setEntity_name(changeField.getAppendix_name());
            tfi.setEntity_caption(changeField.getAppendix_displayname());
            tfi.setField_type(changeField.getField_type());
            DefaultMutableTreeNode node_AppendixDetail = new DefaultMutableTreeNode(tfi);
            node_Appendix.add(node_AppendixDetail);
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}