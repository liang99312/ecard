/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.autoexcute;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.client.CommUtil;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.CheckTreeNode;

/**
 *
 * @author DB2INST3
 */
public class ExcuteTreeModel extends DefaultTreeModel {

    private CheckTreeNode rootNode = new CheckTreeNode("ROOT");
    private Hashtable<String, String> k_keywords = new Hashtable<String, String>();
    private Hashtable<String, List> lookups = new Hashtable<String, List>();
    private Hashtable<String, String> keyword_groups = new Hashtable<String, String>();

    public Hashtable<String, String> getK_keywords() {
        return k_keywords;
    }

    public Hashtable<String, String> getKeyword_groups() {
        return keyword_groups;
    }

    public Hashtable<String, List> getLookups() {
        return lookups;
    }

    public ExcuteTreeModel() {
        super(new CheckTreeNode());
        this.setRoot(rootNode);
    }

    public ExcuteTreeModel(String module_code) {
        super(new CheckTreeNode());
        this.setRoot(rootNode);
        buildTree(module_code);
    }

    public void buildTree(String module_code) {
        k_keywords.clear();
        lookups.clear();
        keyword_groups.clear();
        rootNode.removeAllChildren();
        List list = CommUtil.fetchEntities("from EntityDef ed join fetch ed.entityClass ec join fetch ec.moduleInfo mi where mi.module_code='" + module_code + "'");
        Class c;
        for (Object obj : list) {
            try {
                EntityDef ed = (EntityDef) obj;
                c = Class.forName(EntityBuilder.getPackage(ed) + ed.getEntityName());
                String entityCaption = ed.getEntityCaption();
                List tmp_list = lookups.get(entityCaption);
                if (tmp_list == null) {
                    tmp_list = new ArrayList();
                }
                CheckTreeNode node = new CheckTreeNode(ed);
                List<TempFieldInfo> field_infos = EntityBuilder.getCommFieldInfoListOf(c, EntityBuilder.COMM_FIELD_VISIBLE);
                for (TempFieldInfo tfi : field_infos) {
                    if (tfi.getField_name().equals("deptCode")) {
                        continue;
                    }
                    CheckTreeNode child = new CheckTreeNode(tfi);
                    node.add(child);
                    keyword_groups.put("[" + entityCaption + "." + tfi.getCaption_name() + "]", entityCaption);
                    //c_keywords.add("[" + entityCaption + "." + tfi.getCaption_name() + "]");
                    k_keywords.put("[" + entityCaption + "." + tfi.getCaption_name() + "]", tfi.getEntity_name() + "." + tfi.getField_name().replace("_code_", ""));
                    tmp_list.add(tfi);
                }
                rootNode.add(node);
                lookups.put(entityCaption, tmp_list);
            } catch (ClassNotFoundException ex) {
                ex.printStackTrace();
                continue;
            }
        }
    }
}
