/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.formula;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.apache.log4j.Logger;
import org.jhrcore.entity.annotation.ClassAnnotation;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.rebuild.EntityBuilder;

/**
 *
 * @author DB2INST3
 */
public class FormulaParaModel extends DefaultTreeModel {

    private Class<?> cur_class;
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("参数列表");
    private Hashtable<String, String> k_keywords = new Hashtable<String, String>();
    private Hashtable<String, List> lookups = new Hashtable<String, List>();
    private Hashtable<String, String> keyword_groups = new Hashtable<String, String>();
//    private String para_code = "";
    private Hashtable<String, List<TempFieldInfo>> pay_infos = new Hashtable<String, List<TempFieldInfo>>();
    private List entity_list;
    private List para_list;
    private Logger log = Logger.getLogger(FormulaParaModel.class.getName());

    public FormulaParaModel() {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
    }

    public Hashtable<String, String> getK_keywords() {
        return k_keywords;
    }

    public Hashtable<String, String> getKeyword_groups() {
        return keyword_groups;
    }

    public void setKeyword_groups(Hashtable<String, String> keyword_groups) {
        this.keyword_groups = keyword_groups;
    }

    public Hashtable<String, List> getLookups() {
        return lookups;
    }

    public void setLookups(Hashtable<String, List> lookups) {
        this.lookups = lookups;
    }

    public FormulaParaModel(Hashtable<String, List<TempFieldInfo>> pay_infos, List entity_list, List para_list) {
        super(new DefaultMutableTreeNode());
        this.pay_infos = pay_infos;
        this.entity_list = entity_list;
        this.para_list = para_list;
        this.setRoot(rootNode);
        buildTree(entity_list, para_list);
    }

    public FormulaParaModel(List entity_list, List para_list) {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
        buildTree(entity_list, para_list);
    }

    public FormulaParaModel(List entity_list, List para_list, String para_code) {
        super(new DefaultMutableTreeNode());
//        this.para_code = para_code;
        this.setRoot(rootNode);
        buildTree(entity_list, para_list);
    }

    public void buildPayNode(String scheme_type) {
        buildTree(entity_list, para_list);
        Hashtable<String, DefaultMutableTreeNode> pay_nodes = new Hashtable<String, DefaultMutableTreeNode>();
        Hashtable<String, List> pay_keys = new Hashtable<String, List>();
        DefaultMutableTreeNode parent;
        List tmp_infos;
        for (String key : pay_infos.keySet()) {
            if (key.equals(scheme_type)) {
                List<TempFieldInfo> fields = pay_infos.get(key);
                for (TempFieldInfo tfi : fields) {
                    if (!tfi.getEntity_caption().startsWith(scheme_type)) {
                        continue;
                    }
                    parent = pay_nodes.get(tfi.getEntity_caption());
                    tmp_infos = pay_keys.get(tfi.getEntity_caption());
                    if (parent == null) {
                        parent = new DefaultMutableTreeNode(tfi.getEntity_caption());
                        pay_nodes.put(tfi.getEntity_caption(), parent);
                        rootNode.insert(parent, 0);
                        tmp_infos = new ArrayList();
                    }
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(tfi);
                    parent.add(node);
                    keyword_groups.put("[" + tfi.getCaption_name() + "]", tfi.getEntity_caption());
                    k_keywords.put("[" + tfi.getCaption_name() + "]", (tfi.getEntity_name().equals("") ? "" : (tfi.getEntity_name() + "_")) + tfi.getField_name().replace("_code_", ""));
                    tmp_infos.add(tfi);
                    pay_keys.put(tfi.getEntity_caption(), tmp_infos);
                }
            }
        }
        for (String key1 : pay_keys.keySet()) {
            lookups.put(key1, pay_keys.get(key1));
        }
    }

    public void buildTree(List entity_list, List para_list) {
        rootNode.removeAllChildren();
        if (entity_list != null) {
            for (Object obj : entity_list) {
                String entityCaption;
                DefaultMutableTreeNode tmpNode;
                if (obj instanceof EntityDef) {
                    EntityDef ed = (EntityDef) obj;
                    String packageName = EntityBuilder.getPackage(ed);
                    try {
                        cur_class = Class.forName(packageName + ed.getEntityName());
                    } catch (ClassNotFoundException ex) {
                        ex.printStackTrace();
                        log.error(ex);
                    }
                    entityCaption = ed.getEntityCaption();
                    tmpNode = new DefaultMutableTreeNode(ed);
                }else{
                    cur_class = (Class)obj;
                    ClassAnnotation ca =cur_class.getAnnotation(ClassAnnotation.class);
                    entityCaption = ca.displayName();
                    tmpNode = new DefaultMutableTreeNode(entityCaption);
                }
                List tmp_list = lookups.get(entityCaption);
                if (tmp_list == null) {
                    tmp_list = new ArrayList();
                }
                List<TempFieldInfo> field_list = EntityBuilder.getCommFieldInfoListOf(cur_class, EntityBuilder.COMM_FIELD_VISIBLE);
                if (field_list != null) {
                    for (TempFieldInfo tfi : field_list) {
                        if (tfi.getField_name().equals("deptCode") || tfi.getField_name().equals("payDeptBack") || tfi.getField_name().equals("payDeptMonthBack")) {
                            continue;
                        }
                        DefaultMutableTreeNode tmpNode1 = new DefaultMutableTreeNode(tfi);
                        tmpNode.add(tmpNode1);
                        String entity_name = tfi.getEntity_name();
                        if (keyword_groups.get("[" + entityCaption + "." + tfi.getCaption_name() + "]") != null) {
                            continue;
                        }
                        keyword_groups.put("[" + entityCaption + "." + tfi.getCaption_name() + "]", entityCaption);
                        k_keywords.put("[" + entityCaption + "." + tfi.getCaption_name() + "]", entity_name + "." + tfi.getField_name().replace("_code_", ""));
                        tmp_list.add(tfi);
                    }
                }
                lookups.put(entityCaption, tmp_list);
                rootNode.add(tmpNode);
            }
        }
        List tmp_list = new ArrayList();
        DefaultMutableTreeNode para_node = new DefaultMutableTreeNode("常量参数");
        if (para_list != null) {
            for (Object obj : para_list) {
                DefaultMutableTreeNode para_node1 = new DefaultMutableTreeNode(obj);
                para_node.add(para_node1);
                k_keywords.put(obj.toString(), obj.toString());
                keyword_groups.put(obj.toString(), "常量参数");
                tmp_list.add(obj);
            }
        }
        lookups.put("常量参数", tmp_list);
        rootNode.add(para_node);
    }
}
