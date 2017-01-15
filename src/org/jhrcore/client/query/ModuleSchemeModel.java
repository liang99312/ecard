/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.query;

import java.util.Hashtable;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.client.CommUtil;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.query.QueryAnalysisScheme;
import org.jhrcore.iservice.impl.CommImpl;

/**
 *
 * @author mxliteboss
 */
public class ModuleSchemeModel extends DefaultTreeModel {

    private final String str = "所有方案";
    private DefaultMutableTreeNode rootScheme = new DefaultMutableTreeNode(str);
    private ModuleInfo cur_module = null;

    public ModuleSchemeModel() {
        super(new DefaultMutableTreeNode());
        setRoot(rootScheme);
        buildTree();
    }
    //新建 针对模块的查询 参数：模块编号列表

    public ModuleSchemeModel(List list_mouleCode) {
        super(new DefaultMutableTreeNode());
        setRoot(rootScheme);
        buildTree(list_mouleCode);
    }

    public ModuleSchemeModel(ModuleInfo mi) {
        super(new DefaultMutableTreeNode());
        this.cur_module = mi;
        this.setRoot(rootScheme);
        buildTree();
    }

    public void buildTree() {
        rootScheme.removeAllChildren();
        Hashtable<String, DefaultMutableTreeNode> ht_nodes = new Hashtable<String, DefaultMutableTreeNode>();
        if (cur_module == null) {
            List list = CommImpl.getSysModule(false, false, false);//CommUtil.fetchEntities("from ModuleInfo m");
            for (Object obj : list) {
                ModuleInfo tt = (ModuleInfo) obj;
                if (tt.getModule_code().equals("ssjk")) {
                    continue;//过滤实时监控
                }
                DefaultMutableTreeNode temp = new DefaultMutableTreeNode(tt);
                ht_nodes.put(tt.getModule_key() + "_", temp);
                this.rootScheme.add(temp);
            }
        } else {
            DefaultMutableTreeNode temp = new DefaultMutableTreeNode(cur_module);
            ht_nodes.put(cur_module.getModule_key() + "_", temp);
            this.rootScheme.add(temp);
        }
        String hql = "from QueryAnalysisScheme ed join fetch ed.moduleInfo left join fetch ed.queryAnalysisFields  ";
        if (cur_module != null) {
            hql += " where ed.moduleInfo.module_key='" + cur_module.getModule_key() + "'";
        }
        System.out.println("hql:" + hql);
        List listTemp = CommUtil.fetchEntities(hql);
        System.out.println("ssssssssssssss:");
        for (Object obj : listTemp) {
            QueryAnalysisScheme ttp = (QueryAnalysisScheme) obj;
            if (ttp.getModuleInfo().getModule_code().equals("ssjk")) {
                continue;//过滤实时监控
            }
            DefaultMutableTreeNode parent_type = ht_nodes.get(ttp.getModuleInfo().getModule_key() + "_" + (ttp.getQueryAnalysisScheme_type() == null ? "" : ttp.getQueryAnalysisScheme_type()));
            if (parent_type == null) {
                DefaultMutableTreeNode parent_module = ht_nodes.get(ttp.getModuleInfo().getModule_key() + "_");
                if (parent_module == null) {
                    parent_module = new DefaultMutableTreeNode(ttp);
                    ht_nodes.put(ttp.getModuleInfo().getModule_key() + "_", parent_module);
                    this.rootScheme.add(parent_module);
                }
                parent_type = new DefaultMutableTreeNode(ttp.getQueryAnalysisScheme_type());
                ht_nodes.put(ttp.getModuleInfo().getModule_key() + "_" + (ttp.getQueryAnalysisScheme_type() == null ? "" : ttp.getQueryAnalysisScheme_type()), parent_type);
                parent_module.add(parent_type);
            }
            parent_type.add(new DefaultMutableTreeNode(ttp));
        }
    }

    public void buildTree(List list_moduleCode) {
        rootScheme.removeAllChildren();
        Hashtable<String, DefaultMutableTreeNode> ht_nodes = new Hashtable<String, DefaultMutableTreeNode>();
        String hql = "from ModuleInfo m ";
        StringBuffer buffer = new StringBuffer();
        if (list_moduleCode.size() > 0) {
            buffer.append("'-1'");
            for (Object moduleCode : list_moduleCode) {
                buffer.append(",'");
                buffer.append(moduleCode);
                buffer.append("'");
            }
            hql += " where m.module_code in ( " + buffer.toString() + " )";
        }
        List list = CommUtil.fetchEntities(hql);
        buffer = new StringBuffer();
        buffer.append("'@@@@@@'");
        for (Object obj : list) {
            ModuleInfo tt = (ModuleInfo) obj;
            DefaultMutableTreeNode temp = new DefaultMutableTreeNode(tt);
            ht_nodes.put(tt.getModule_key() + "_", temp);
            this.rootScheme.add(temp);
            buffer.append(",'");
            buffer.append(tt.getModule_key());
            buffer.append("'");

        }
        hql = "from QueryAnalysisScheme ed join fetch ed.moduleInfo  left join fetch ed.queryAnalysisFields  ";
        if (list_moduleCode.size() > 0) {
            hql += " where ed.moduleInfo.module_key in (" + buffer.toString() + " )";
        }
//        System.out.println("hql:"+hql);
        List listTemp = CommUtil.fetchEntities(hql);
        for (Object obj : listTemp) {
            QueryAnalysisScheme ttp = (QueryAnalysisScheme) obj;
            DefaultMutableTreeNode parent_type = ht_nodes.get(ttp.getModuleInfo().getModule_key() + "_" + (ttp.getQueryAnalysisScheme_type() == null ? "" : ttp.getQueryAnalysisScheme_type()));
            if (parent_type == null) {
                DefaultMutableTreeNode parent_module = ht_nodes.get(ttp.getModuleInfo().getModule_key() + "_");
                if (parent_module == null) {
                    parent_module = new DefaultMutableTreeNode(ttp);
                    ht_nodes.put(ttp.getModuleInfo().getModule_key() + "_", parent_module);
                    this.rootScheme.add(parent_module);
                }
                parent_type = new DefaultMutableTreeNode(ttp.getQueryAnalysisScheme_type());
                ht_nodes.put(ttp.getModuleInfo().getModule_key() + "_" + (ttp.getQueryAnalysisScheme_type() == null ? "" : ttp.getQueryAnalysisScheme_type()), parent_type);
                parent_module.add(parent_type);
            }
            parent_type.add(new DefaultMutableTreeNode(ttp));
        }
    }
}
