/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.autoexcute;

import java.util.List;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.AutoExcuteScheme;
import org.jhrcore.ui.CheckTreeNode;

/**
 *
 * @author mxliteboss
 */
public class AutoExSchemeModel extends DefaultTreeModel {

    private CheckTreeNode rootNode = new CheckTreeNode("ROOT");
    private CheckTreeNode calNode = new CheckTreeNode("�Զ�����");
    private CheckTreeNode calUseNode = new CheckTreeNode("����");
    private CheckTreeNode calNotNode = new CheckTreeNode("ͣ��");
    private CheckTreeNode wakeNode = new CheckTreeNode("������ʾ");
    private CheckTreeNode wakeUseNode = new CheckTreeNode("����");
    private CheckTreeNode wakeNotNode = new CheckTreeNode("ͣ��");

    public DefaultMutableTreeNode getRoot(String root) {
        return rootNode;
    }

    public AutoExSchemeModel() {
        super(new DefaultMutableTreeNode());
        this.setRoot(rootNode);
        rootNode.add(calNode);
        rootNode.add(wakeNode);
        calNode.add(calUseNode);
        calNode.add(calNotNode);
        wakeNode.add(wakeUseNode);
        wakeNode.add(wakeNotNode);
        buildTree();
    }

    private void buildTree() {
        calUseNode.removeAllChildren();
        calNotNode.removeAllChildren();
        wakeUseNode.removeAllChildren();
        wakeNotNode.removeAllChildren();
        String scheme_hql = "from AutoExcuteScheme aes join fetch aes.funtionRight where 1=1";
        if (!UserContext.isSA) {
            scheme_hql += " and exists(select 1 from CommMap cm where cm.c_user_key='" + UserContext.rolea01_key + "' and cm.map_key=aes.autoExcuteScheme_key)";
        }
        scheme_hql += " order by scheme_type,order_no";
        List scheme_list = CommUtil.fetchEntities(scheme_hql);
        for (Object obj : scheme_list) {
            AutoExcuteScheme aes = (AutoExcuteScheme) obj;
            CheckTreeNode pNode = calNotNode;
            CheckTreeNode node = new CheckTreeNode(aes);
            if ("�Զ�����".equals(aes.getScheme_type())) {
                if (aes.isUsed_flag()) {
                    pNode = calUseNode;
                }
            } else if ("������ʾ".equals(aes.getScheme_type())) {
                if (aes.isUsed_flag()) {
                    pNode = wakeUseNode;
                } else {
                    pNode = wakeNotNode;
                }
            }
            pNode.add(node);
        }
    }

    public CheckTreeNode getRootType(AutoExcuteScheme aes) {
        CheckTreeNode pNode = calNotNode;
        if ("�Զ�����".equals(aes.getScheme_type())) {
            if (aes.isUsed_flag()) {
                pNode = calUseNode;
            }
        } else if ("������ʾ".equals(aes.getScheme_type())) {
            if (aes.isUsed_flag()) {
                pNode = wakeUseNode;
            } else {
                pNode = wakeNotNode;
            }
        }
        return pNode;
    }
}
