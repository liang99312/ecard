/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.personnel;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.client.CommUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.FileManager;
import org.jhrcore.ui.task.IModuleCode;

/**
 *
 * @author Administrator
 */
public class DocTreeModel extends DefaultTreeModel implements IModuleCode{
    
    private A01 person;
    private DefaultMutableTreeNode root = new DefaultMutableTreeNode("文档分类");
    private DefaultMutableTreeNode first_chil = new DefaultMutableTreeNode("共享文件");
    private String module_code = "EmpMng.mi_doc.initOthers";

    public DocTreeModel() {
        super(new DefaultMutableTreeNode());
        this.setRoot(root);
    }
    
    
    
    public DocTreeModel(A01 person){
        super(new DefaultMutableTreeNode());
        this.person = person;
        this.setRoot(root);
        buildTree(person);
    }
    public DefaultMutableTreeNode getRoot(){
        return root;
    }
    public DefaultMutableTreeNode getG_Node(){
        return first_chil;
    }
    public void buildTree(A01 per){
        root.removeAllChildren();
        root.add(first_chil);
        List list = CommUtil.fetchEntities("from FileManager fm where fm.file_folder='共享文件'");
        if (list.size() != 0) {
            for (Object obj : list) {
                FileManager fileManager = (FileManager) obj;
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(fileManager);
                first_chil.add(node);
            }
        }
        List list2 = CommUtil.selectSQL("select distinct file_folder from FileManager where file_folder !='共享文件' and manager_code='"+ per.getA0190() + "'");
        if(list2.size() != 0){
            for(Object obj : list2){
                String str = (String) (obj + "");
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(obj.toString());
                List list3 = CommUtil.fetchEntities("from FileManager fm where fm.file_folder='" + str + "' and fm.manager_code='" + per.getA0190() + "'");
                if(list3.size() != 0){
                    for(Object obj2 : list3){
                        FileManager fileManager2 = (FileManager) obj2;
                        DefaultMutableTreeNode addNode = new DefaultMutableTreeNode(fileManager2);
                        node.add(addNode);
                    }
                }
                root.add(node);
            }
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }

}
