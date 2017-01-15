/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.comm;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.apache.log4j.Logger;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.rebuild.EntityBuilder;

/**
 *
 * @author mxliteboss
 */
public class FieldRegulaTreeModel extends DefaultTreeModel {

    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("ROOT");
    private Hashtable<String, String> k_keywords = new Hashtable<String, String>();
    private Hashtable<String, List> lookups = new Hashtable<String, List>();
    private Hashtable<String, String> keyword_groups = new Hashtable<String, String>();
    private EntityDef entityDef;
    private Logger log = Logger.getLogger(FieldRegulaTreeModel.class.getName());
    public Hashtable<String, String> getK_keywords() {
        return k_keywords;
    }

    public Hashtable<String, String> getKeyword_groups() {
        return keyword_groups;
    }

    public Hashtable<String, List> getLookups() {
        return lookups;
    }

    public FieldRegulaTreeModel(EntityDef ed) {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
        this.entityDef = ed;
        buildTree(entityDef);
    }

    public void buildTree(EntityDef ed) {
        k_keywords.clear();
        lookups.clear();
        keyword_groups.clear();
        rootNode.removeAllChildren();
        Class c;
        try {
            c = Class.forName(EntityBuilder.getPackage(ed) + ed.getEntityName());
            String entityCaption = ed.getEntityCaption();
            List tmp_list = lookups.get(entityCaption);
            if (tmp_list == null) {
                tmp_list = new ArrayList();
            }
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(ed);
            List<TempFieldInfo> field_infos = EntityBuilder.getCommFieldInfoListOf(c, EntityBuilder.COMM_FIELD_VISIBLE);
            for (TempFieldInfo tfi : field_infos) {
                DefaultMutableTreeNode child = new DefaultMutableTreeNode(tfi);
                node.add(child);
                keyword_groups.put("[" + entityCaption + "." + tfi.getCaption_name() + "]", entityCaption);
                k_keywords.put("[" + entityCaption + "." + tfi.getCaption_name() + "]", tfi.getEntity_name() + "." + tfi.getField_name().replace("_code_", ""));
                tmp_list.add(tfi);
            }
            rootNode.add(node);
            lookups.put(entityCaption, tmp_list);
        } catch (ClassNotFoundException ex) {
            log.error(ex);
        }
    }
}

