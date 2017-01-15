/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.ecard.ui;

import java.util.Enumeration;
import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.client.CommUtil;
import org.jhrcore.entity.ecard.Ecard;

/**
 *
 * @author DB2INST3
 */
public class CardTreeModel extends DefaultTreeModel {

    private static final long serialVersionUID = 1L;
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("所有卡");
    private DefaultMutableTreeNode jhNode = new DefaultMutableTreeNode("已激活");
    private DefaultMutableTreeNode tzNode = new DefaultMutableTreeNode("已停止");
    private DefaultMutableTreeNode jpNode = new DefaultMutableTreeNode("普养");
    private DefaultMutableTreeNode jzNode = new DefaultMutableTreeNode("中养");
    private DefaultMutableTreeNode jjNode = new DefaultMutableTreeNode("精养");
    private DefaultMutableTreeNode ypNode = new DefaultMutableTreeNode("普养");
    private DefaultMutableTreeNode yzNode = new DefaultMutableTreeNode("中养");
    private DefaultMutableTreeNode yjNode = new DefaultMutableTreeNode("精养");
    private String sql = "from Ecard order by ecard_code";
    private List list;

    public List getList() {
        return list;
    }

    public CardTreeModel() {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
        buildTree();

    }
    
    public CardTreeModel(String str) {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
        if("noCard".equals(str)){
            buildTree_noCard();
        }else{
            buildTree();
        }
    }
    
    public void buildTree_noCard() {
        rootNode.removeAllChildren();
        rootNode.add(jhNode);
        jhNode.add(jpNode);
        jhNode.add(jzNode);
        jhNode.add(jjNode);
        rootNode.add(tzNode);
        tzNode.add(ypNode);
        tzNode.add(yzNode);
        tzNode.add(yjNode);
    }
    
    public void buildTree(List list) {
        rootNode.removeAllChildren();
        rootNode.add(jhNode);
        jhNode.add(jpNode);
        jhNode.add(jzNode);
        jhNode.add(jjNode);
        rootNode.add(tzNode);
        tzNode.add(ypNode);
        tzNode.add(yzNode);
        tzNode.add(yjNode);
        for (Object obj : list) {
            Ecard d = (Ecard) obj;
            if("已激活".equals(d.getEcard_state())){
                if("精养".equals(d.getEcard_type())){
                    jjNode.add(new DefaultMutableTreeNode(d));
                }else if("中养".equals(d.getEcard_type())){
                    jzNode.add(new DefaultMutableTreeNode(d));
                }else{
                    jpNode.add(new DefaultMutableTreeNode(d));
                }
            }else{
                if("精养".equals(d.getEcard_type())){
                    yjNode.add(new DefaultMutableTreeNode(d));
                }else if("中养".equals(d.getEcard_type())){
                    yzNode.add(new DefaultMutableTreeNode(d));
                }else{
                    ypNode.add(new DefaultMutableTreeNode(d));
                }
            }
        }
    }

    public DefaultMutableTreeNode getNodeByDept(Ecard e) {
        DefaultMutableTreeNode resultNode = null;
        Enumeration deptEnum = rootNode.depthFirstEnumeration();
        String val = e.getEcard_key();
        while (deptEnum.hasMoreElements()) {
            DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) deptEnum.nextElement();
            if (tmpNode.getUserObject() instanceof Ecard) {
                Ecard e1 = (Ecard) tmpNode.getUserObject();
                String field_val = e1.getEcard_key();
                if (val.equals(field_val)) {
                    resultNode = tmpNode;
                    break;
                }
            }
        }
        return resultNode;
    }

    public void buildTree() {
        list = CommUtil.fetchEntities(sql);
        buildTree(list);
    }
}
