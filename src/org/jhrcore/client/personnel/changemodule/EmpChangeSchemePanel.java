/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.changemodule;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.client.UserContext;
import org.jhrcore.ui.WizardFrame;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.change.ChangeField;
import org.jhrcore.entity.change.ChangeItem;
import org.jhrcore.entity.change.ChangeScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpChangeSchemeMsg;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author Administrator
 */
public class EmpChangeSchemePanel extends JPanel implements IModulePanel {

    private JButton btnAdd = new JButton("增加");
    private JButton btnEdit = new JButton("编辑");
    private JButton btnDel = new JButton("删除");
    private JButton btnFieldEditDefine = new JButton("变动字段编辑性设置");
    private List list_scheme = new ArrayList();
    private JTree schemeTree;
    private FTable beanTablePanel1;
    private FTable ftableAppendix;
    private JPanel pnlRight;
    private JToolBar toolBar;
    private ChangeSchemeTreeModel scheme_model;
    private DefaultMutableTreeNode curNode;
    private JSplitPane jspRight;
    private JSplitPane jspMain;
    private JTabbedPane jtpBottom;
    private BeanPanel beanPanel = new BeanPanel();
    private Hashtable<String, String> fieldKeys = new Hashtable<String, String>();
    private HrLog log = new HrLog("EmpChangeScheme");
    public static final String module_code = "EmpChangeScheme";

    public EmpChangeSchemePanel() {
        super(new BorderLayout());
        initOthers();
        setupEvents();
    }

    @Override
    public void setFunctionRight() {
        btnFieldEditDefine.setEnabled(UserContext.isSA);
        setMainState();
    }

    private void initOthers() {
        pnlRight = new JPanel(new BorderLayout());
        initToolBar();
        list_scheme.addAll(EmpUtil.getViewChangeSchemeList());
        for (TempFieldInfo tfi : EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE_ALL)) {
            fieldKeys.put(tfi.getField_name().replace("_code_", ""), tfi.getCaption_name());
        }
        JTabbedPane tp = new JTabbedPane();
        scheme_model = new ChangeSchemeTreeModel(list_scheme);
        schemeTree = new JTree(scheme_model);
        HRRendererView.getChangeSchemeMap().initTree(schemeTree, TreeSelectMod.nodeSelectMod);
        tp.add("人事变动模板", new JScrollPane(schemeTree));
        beanTablePanel1 = new FTable(ChangeItem.class, new String[]{"fieldName", "displayName", "comm_flag", "field_type"}, false, false, false, "");
        ftableAppendix = new FTable(ChangeField.class, false, false, false, "EmpChangeSchemePanel");
        beanTablePanel1.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        ftableAppendix.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        JPanel pnlTop = new JPanel(new BorderLayout());
        pnlTop.add(beanTablePanel1);
        pnlTop.setBorder(javax.swing.BorderFactory.createTitledBorder("变动字段："));
        jtpBottom = new JTabbedPane();
        jtpBottom.add("模板信息", new JScrollPane(beanPanel));
        jtpBottom.add("附表业务处理", ftableAppendix);
        jspRight = new JSplitPane(JSplitPane.VERTICAL_SPLIT, pnlTop, jtpBottom);
        jspRight.setDividerSize(2);
        pnlRight.add(jspRight, BorderLayout.CENTER);
        jspMain = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, tp, pnlRight);
        jspMain.setOneTouchExpandable(true);
        this.add(jspMain, BorderLayout.CENTER);
    }

    private void initToolBar() {
        toolBar = new JToolBar();
        toolBar.setFloatable(false);
        ComponentUtil.setSize(toolBar, pnlRight.getWidth(), 26);
        toolBar.add(btnAdd);
        toolBar.add(btnEdit);
        toolBar.add(btnDel);
        toolBar.add(btnFieldEditDefine);
        pnlRight.add(toolBar, BorderLayout.NORTH);
    }

    private void setupEvents() {
        btnFieldEditDefine.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                defineFieldEditable();
            }
        });
        schemeTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (e.getPath() == null) {
                    return;
                }
                curNode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                selectChangeScheme(curNode);
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (curNode == null) {
                    return;
                }
                log.info(e);
                ChangeScheme changeScheme = (ChangeScheme) UtilTool.createUIDEntity(ChangeScheme.class);
                if (curNode.getUserObject() instanceof ChangeScheme) {
                    changeScheme.setChangeScheme_type(((ChangeScheme) curNode.getUserObject()).getChangeScheme_type());
                } else if (curNode != schemeTree.getModel().getRoot()) {
                    changeScheme.setChangeScheme_type(curNode.getUserObject().toString());
                }
                editChangeScheme(changeScheme, true);
            }
        });
        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (curNode == null || !(curNode.getUserObject() instanceof ChangeScheme)) {
                    return;
                }
                log.info(e);
                editChangeScheme((ChangeScheme) curNode.getUserObject(), false);
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (curNode == null) {
                    return;
                }
                List<ChangeScheme> del_schemes = new ArrayList<ChangeScheme>();
                Enumeration enumt = curNode.breadthFirstEnumeration();
                while (enumt.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                    if (node.getUserObject() instanceof ChangeScheme) {
                        ChangeScheme cs = (ChangeScheme) node.getUserObject();
                        del_schemes.add(cs);
                    }
                }
                if (del_schemes.isEmpty()) {
                    MsgUtil.showInfoMsg(EmpChangeSchemeMsg.msg001);
                    return;
                }
                if (MsgUtil.showNotConfirmDialog(EmpChangeSchemeMsg.msg002)) {
                    return;
                }
                log.info(e);
                ValidateSQLResult result = RSImpl.delChangeScheme(del_schemes);
                if (result.getResult() == 0) {
                    MsgUtil.showInfoMsg(CommMsg.DELSUCCESS_MESSAGE);
                    list_scheme.removeAll(del_schemes);
                    List<ChangeScheme> session_del_list = new ArrayList<ChangeScheme>();
                    for (ChangeScheme cs : del_schemes) {
                        for (ChangeScheme cs1 : EmpUtil.getChange_scheme_list()) {
                            if (cs1.getChangeScheme_key().equals(cs.getChangeScheme_key())) {
                                session_del_list.add(cs1);
                                break;
                            }
                        }
                    }
                    EmpUtil.getChange_scheme_list().removeAll(session_del_list);
                    DefaultMutableTreeNode next_node = null;
                    if (curNode.getUserObject() instanceof ChangeScheme) {
                        int num = curNode.getChildCount();
                        if (num == 1) {
                            curNode = (DefaultMutableTreeNode) curNode.getParent();
                        }
                    }
                    next_node = curNode.getNextSibling();
                    if (next_node == null) {
                        next_node = curNode.getPreviousSibling();
                    }
                    if (next_node == null) {
                        next_node = (DefaultMutableTreeNode) schemeTree.getModel().getRoot();
                    }
                    curNode.removeFromParent();
                    schemeTree.setSelectionPath(new TreePath(next_node.getPath()));
                    schemeTree.updateUI();
                } else {
                    log.info("删除失败,原因:" + result.getMsg());
                    MsgUtil.showHRDelErrorMsg(result);
                }
            }
        });
        ComponentUtil.initTreeSelection(schemeTree);
    }

    /**
     * 该方法用于设置变动字段的编辑性
     */
    private void defineFieldEditable() {
        JPanel pnl = new JPanel(new BorderLayout());
        FTable ftable = new FTable(ChangeItem.class, new String[]{"fieldName", "displayName", "diseditable"}, false, false, false, "EmpChangeSchemePanel");
        SysParameter sp = (SysParameter) CommUtil.fetchEntityBy("from SysParameter sp where sp.sysParameter_key='EmpChange.disEditableChangeField'");
        if (sp == null) {
            sp = new SysParameter();
            sp.setSysParameter_key("EmpChange.disEditableChangeField");
        }
        String[] disableFields = (sp.getSysparameter_value() == null ? "" : sp.getSysparameter_value().trim()).split(";");
        HashSet<String> disableFieldSet = new HashSet<String>();
        for (String field : disableFields) {
            disableFieldSet.add(field);
        }
        List list = CommUtil.selectSQL("select distinct fieldName from ChangeItem where fieldName<>'a0193'");
        for (Object obj : list) {
            ChangeItem ci = (ChangeItem) UtilTool.createUIDEntity(ChangeItem.class);
            ci.setFieldName(obj.toString());
            if (disableFieldSet.contains(obj.toString())) {
                ci.setDiseditable(true);
            }
            TempFieldInfo tfi = EntityBuilder.getTempFieldInfoByName(A01.class.getName(), ci.getFieldName(), true);
            if (tfi != null) {
                ci.setDisplayName(tfi.getCaption_name());
            }
            ftable.addObject(ci);
        }
        ftable.setEditable(true);
        ftable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnl.add(ftable, BorderLayout.CENTER);
        if (ModalDialog.doModal(JOptionPane.getFrameForComponent(btnFieldEditDefine), pnl, EmpChangeSchemeMsg.ttl001)) {
            ftable.editingStopped();
            String disableFieldStr = "";
            for (Object obj : ftable.getObjects()) {
                ChangeItem ci = (ChangeItem) obj;
                if (ci.isDiseditable()) {
                    disableFieldStr = disableFieldStr + ";" + ci.getFieldName();
                }
            }
            if (!disableFieldStr.equals("")) {
                disableFieldStr = disableFieldStr.substring(1);
            }
            sp.setSysparameter_value(disableFieldStr);
            sp.setSysparameter_code(UserContext.person_code);
            CommUtil.saveOrUpdate(sp);
        }
    }

    /**
     * 该方法用于新增/编辑调配方案
     * @param changeScheme：调配方案
     * @param isNew：是否为新增,true: 新增；false：编辑
     */
    private void editChangeScheme(final ChangeScheme changeScheme, final boolean isNew) {
        final String message = isNew ? EmpChangeSchemeMsg.msg004.toString() : EmpChangeSchemeMsg.msg005.toString();
        final ChangeSchemeWizardModel wizardmodel = new ChangeSchemeWizardModel(changeScheme);
        wizardmodel.addPickWindowCloseListener(new IPickWindowCloseListener() {

            @Override
            public void pickClose() {
                if (wizardmodel.getChangeScheme() == null) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(pnlRight), message + EmpMngMsg.msg021);                   
                    MsgUtil.showInfoMsg(message + EmpChangeSchemeMsg.msg003);
                } else {
                    if (isNew) {
                        list_scheme.add(wizardmodel.getChangeScheme());
                        scheme_model.addNode(wizardmodel.getChangeScheme());
                        schemeTree.updateUI();
                    } else {
                        ChangeScheme oldChangeScheme = (ChangeScheme) EmpUtil.getChangeScheme(changeScheme.getChangeScheme_key());
                        curNode.setUserObject(oldChangeScheme);
                        selectChangeScheme(curNode);
                        schemeTree.updateUI();
                    }
                }
            }
        });
        WizardFrame.showWizard(wizardmodel, (JFrame) JOptionPane.getFrameForComponent(btnAdd), true, message);
    }

    private void selectChangeScheme(DefaultMutableTreeNode node) {
        if (node == null) {
            return;
        }
        beanTablePanel1.deleteAllRows();
        ftableAppendix.deleteAllRows();
        if (node.getUserObject() instanceof ChangeScheme) {
            ChangeScheme changeScheme = (ChangeScheme) node.getUserObject();
            for (ChangeItem ci : changeScheme.getChangeItems()) {
                ci.setDisplayName(fieldKeys.get(ci.getFieldName().replace("_code_", "")));
                beanTablePanel1.addObject(ci);
            }
            for (ChangeField cf : changeScheme.getChangeFields()) {
                ftableAppendix.addObject(cf);
            }
            beanPanel.setBean(changeScheme);
            setMainState();
        }
    }

    private void setMainState() {
        if (beanPanel.getBean() == null) {
            beanPanel.setBean(new ChangeScheme());
        } else {
            ChangeScheme cs = (ChangeScheme) beanPanel.getBean();
            if (cs.getNewPersonClassName() != null && !cs.getNewPersonClassName().trim().equals("")) {
                List a0191s = CommUtil.selectSQL("select t.entityName,t.entityCaption from tabname t,EntityClass ec where t.entityClass_key=ec.entityClass_key and ec.entityType_code='CLASS'");
                Hashtable<String, String> a0191_keys = new Hashtable<String, String>();
                for (Object obj : a0191s) {
                    Object[] objs = (Object[]) obj;
                    a0191_keys.put(objs[0].toString(), objs[1].toString());
                }
                cs.setNewPersonClass(a0191_keys.get(cs.getNewPersonClassName()));
                String old_className = cs.getOldPersonClassName();
                if (old_className != null && !old_className.trim().equals("")) {
                    String[] old_classes = old_className.split(";");
                    String old_class = "";
                    for (String c : old_classes) {
                        old_class = old_class + a0191_keys.get(c) + ";";
                    }
                    cs.setOldPersonClass(old_class);
                }
            }
        }
        List<String> fields = EntityBuilder.getCommFieldNameListOf(ChangeScheme.class, EntityBuilder.COMM_FIELD_VISIBLE);
        fields.add("newPersonClass");
        fields.add("oldPersonClass");
        beanPanel.setFields(fields);
        beanPanel.bind();
    }

    @Override
    public void refresh() {
        ContextManager.setStatusBar(beanTablePanel1.getObjects().size());
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }

    @Override
    public void pickClose() {
    }
}
