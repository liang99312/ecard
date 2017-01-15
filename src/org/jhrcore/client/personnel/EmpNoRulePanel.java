/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * EmpNoRulePanel.java
 *
 * Created on 2010-6-9, 9:48:54
 */
package org.jhrcore.client.personnel;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.Binding.SyncFailure;
import org.jdesktop.beansbinding.BindingGroup;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.AutoNoRule;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpRegisterMsg;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptSelectDlg;
import org.jhrcore.ui.EditorFactory;
import org.jhrcore.ui.HrTextPane;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class EmpNoRulePanel extends javax.swing.JPanel implements IModuleCode {

    private FTable ftable;
    private JComboBoxBinding person_class_binding;
    private AutoNoRule cur_rule;
    private boolean editable = false;
    private ListSelectionListener listener;
    private BindingGroup bindingGroupC = new BindingGroup();
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("参数列表");
    private JTree para_tree;
    private HrTextPane jtaFormulaText;
    private DocumentListener doc_listener;
    private boolean perfix_change_flag = false;
    private String rule_Code;
    private Hashtable<String, String> dept_keys = new Hashtable<String, String>();
    private Hashtable<String, String> a0191_keys = new Hashtable<String, String>();
    private HrLog log = new HrLog("EmpRegister.人员编号生成规则设置");
    private String module_code = "ContractPara.mi_personCodeRule";
    private boolean close_flag = true;
    private BindingListener bind_listener = new BindingListener() {

        @Override
        public void bindingBecameBound(Binding arg0) {
        }

        @Override
        public void bindingBecameUnbound(Binding arg0) {
        }

        @Override
        public void syncFailed(Binding arg0, SyncFailure arg1) {
        }

        @Override
        public void synced(Binding arg0) {
        }

        @Override
        public void sourceChanged(Binding arg0, PropertyStateEvent arg1) {
        }

        @Override
        public void targetChanged(Binding arg0, PropertyStateEvent arg1) {
            perfix_change_flag = true;
        }
    };

    public EmpNoRulePanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    /** Creates new form EmpNoRulePanel */
    public EmpNoRulePanel(String rule_code,boolean close_flag) {
        this.rule_Code = rule_code;
        this.close_flag=close_flag;
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        btnClose.setVisible(close_flag);
        ftable = new FTable(AutoNoRule.class, false, false, false, "EmpNoRulePanel");
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnl.add(ftable, BorderLayout.CENTER);
        person_class_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ, SysUtil.getPersonClass(), jcbPersonClass);
        person_class_binding.bind();
        jPanel1.add(pnl, BorderLayout.CENTER);
        String[] paras = new String[]{"@部门代码", "@年份", "@月份", "@日期"};
        Hashtable<String, List> lookups = new Hashtable<String, List>();
        Hashtable<String, String> keyword_groups = new Hashtable<String, String>();
        Hashtable<String, String> k_keywords = new Hashtable<String, String>();
        List tmp_list = new ArrayList();
        for (String para : paras) {
            rootNode.add(new DefaultMutableTreeNode(para));
            k_keywords.put(para, para);
            keyword_groups.put(para, "常量参数");
            tmp_list.add(para);
        }
        lookups.put("常量参数", tmp_list);
        para_tree = new JTree(rootNode);
        HRRendererView.getCommMap().initTree(para_tree);
        pnlPara.add(new JScrollPane(para_tree), BorderLayout.CENTER);
        jtaFormulaText = new HrTextPane();
        jtaFormulaText.revokeDocumentKeys(lookups, keyword_groups, k_keywords);
        pnlEditor.add(jtaFormulaText);
        for (DeptCode dc : UserContext.getDepts(true)) {
            dept_keys.put(dc.getDept_code(), dc.getContent() + "{" + dc.getDept_code() + "}");
        }
        List list = CommUtil.selectSQL("select t.entityName,t.entityCaption from tabname t,entityclass ec where t.entityclass_key=ec.entityclass_key and ec.entitytype_code='CLASS' ");
        for (Object obj : list) {
            Object[] objs = (Object[]) obj;
            a0191_keys.put(objs[0].toString(), objs[1].toString());
        }
        a0191_keys.put("A01", "所有人员");
    }

    private void setupEvents() {
        doc_listener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                perfix_change_flag = true;
                cur_rule.setPerfix(jtaFormulaText.getText());
            }
        };
        jtaFormulaText.getDocument().addDocumentListener(doc_listener);
        para_tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (!jtaFormulaText.isEnabled()) {
                    return;
                }
                if (e.getClickCount() >= 2) {
                    if (para_tree.getSelectionPath() == null) {
                        return;
                    }

                    if (para_tree.getSelectionPath().getLastPathComponent() == para_tree.getModel().getRoot()) {
                        return;
                    }
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) para_tree.getSelectionPath().getLastPathComponent();
                    int tmp = jtaFormulaText.getSelectionStart();
                    String operator = node.getUserObject().toString();
                    jtaFormulaText.replaceSelection(operator);
                    jtaFormulaText.setCaretPosition(tmp + operator.length());
                    jtaFormulaText.requestFocus();
                }
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                AutoNoRule anr = (AutoNoRule) UtilTool.createUIDEntity(AutoNoRule.class);
                int size = ftable.getObjects().size();
                ftable.removeListSelectionListener(listener);
                ftable.addObject(anr);
                cur_rule = anr;
                refreshBeanUI(anr, editable);
                ftable.setRowSelectionInterval(size, size);
                ftable.addListSelectionListener(listener);
            }
        });
        listener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                cur_rule = (AutoNoRule) ftable.getCurrentRow();
                refreshBeanUI(cur_rule, editable);
            }
        };
        ftable.addListSelectionListener(listener);
        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshBeanUI(cur_rule, true);
            }
        });
        btnView.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cancelEdit(true);
                refreshBeanUI(cur_rule, false);
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cancelEdit(false);
                refreshBeanUI(cur_rule, editable);
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = ftable.getSelectObjects();
                if (list.size() == 0) {
                    return;
                }
                if (MsgUtil.showNotConfirmDialog(EmpRegisterMsg.msg014)) {
                    return;
                }
                StringBuffer ex_sqls = new StringBuffer();
                String key = "'-1'";
                for (Object obj : list) {
                    AutoNoRule anr = (AutoNoRule) obj;
                    ex_sqls.append("delete from AutoNo where autoNo_key like '");
                    ex_sqls.append(anr.getAutoNoRule_id());
                    ex_sqls.append("%';");
                    key += ",'" + anr.getAutoNoRule_key() + "'";
                }
                ex_sqls.append("delete from AutoNoRule where AutoNoRule_key in (" + key + ")");
                ValidateSQLResult result = CommUtil.excuteSQLs(ex_sqls.toString(), ";");
                if (result.getResult() == 0) {
                    MsgUtil.showInfoMsg(CommMsg.DELSUCCESS_MESSAGE);
                    ftable.deleteSelectedRows();
                } else {
                    MsgUtil.showHRDelErrorMsg( result);
                }
            }
        });
        btnDept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                selectDept();
            }
        });
        jComboBox1.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_rule == null) {
                    return;
                }
                cur_rule.setNo_unit(jComboBox1.getSelectedItem().toString());
            }
        });
        
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_rule == null) {
                    return;
                }
                log.info(e);
                saveObj(cur_rule);
                log.info("保存规则.规则名称:" + cur_rule.getAutoNoRule_name());
            }
        });
        btnPriview.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_rule == null) {
                    return;
                }
                log.info(e);
                cur_rule.setAdd_perfix(true);
                String tmp = CommUtil.fetchNewNoBy(cur_rule, getParam());
                if (tmp == null) {
//                    JOptionPane.showMessageDialog(null, "前缀存在语法错误，无法生成新编码，保存失败!");
                    MsgUtil.showInfoMsg(EmpRegisterMsg.msg015);
                    log.info("前缀存在语法错误，无法生成新编码，保存失败");
                    return;
                }
                String msg = EmpRegisterMsg.msg016.toString() + "\n";
                msg += EmpRegisterMsg.msg017.toString() + cur_rule.getInit_no() + EmpRegisterMsg.msg018.toString() + cur_rule.getNo_lenth() + EmpRegisterMsg.msg019.toString() + cur_rule.getInc_no() + EmpRegisterMsg.msg020.toString() + cur_rule.getNo_unit() + "\n";
                msg += EmpRegisterMsg.msg021.toString() + "\n";
                msg += "      " + tmp + "\n";
                String new_no = tmp.substring(tmp.length() - cur_rule.getNo_lenth());
                int no = SysUtil.objToInt(new_no);
                no = no + cur_rule.getInc_no();
                new_no = no + "";
                int i = new_no.length();
                while (i < cur_rule.getNo_lenth()) {
                    new_no = "0" + new_no;
                    i++;
                }
                msg += "      " + tmp.substring(0, tmp.length() - cur_rule.getNo_lenth()) + new_no + "\n      ...";
//                JOptionPane.showMessageDialog(null, msg);
                MsgUtil.showInfoMsg(msg);
            }
        });
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ModelFrame.close();
            }
        });
        ComponentUtil.refreshJSplitPane(jSplitPane1, "EmpNoRulePanel.jSplitPane1");
        ComponentUtil.refreshJSplitPane(jSplitPane2, "EmpNoRulePanel.jSplitPane2");
        List list = CommUtil.fetchEntities("from AutoNoRule anr where anr.autoNoRule_id like '" + rule_Code + "_%'");
        HashSet<String> dept_codes = new HashSet<String>();
        for (Object obj : list) {
            AutoNoRule anr = (AutoNoRule) obj;
            String dept_code = anr.getAutoNoRule_id().split("_")[1];
            if (dept_keys.get(dept_code) == null) {
                dept_codes.add(dept_code);
            }
        }
        List<String> data = new ArrayList<String>();
        data.addAll(dept_codes);
        if (data.size() > 0) {
            List depts = CommUtil.selectSQL("select dept_code,content from DeptCode where dept_code in ", data);
            for (Object obj : depts) {
                Object[] objs = (Object[]) obj;
                dept_keys.put(objs[0].toString(), objs[1] + "{" + objs[0] + "}");
            }
        }
        for (Object obj : list) {
            refreshObj((AutoNoRule) obj);
        }
        ftable.setObjects(list);
        ComponentUtil.setSysFuntionNew(this, false);
    }

    private void refreshObj(AutoNoRule anr) {
        if (anr == null) {
            return;
        }
        String key = anr.getAutoNoRule_id();
        if (key != null) {
            String dept_code = key.substring(key.indexOf("_") + 1, key.lastIndexOf("_"));
            String entityName = key.substring(key.lastIndexOf("_") + 1);
            anr.setDept_name(dept_keys.get(dept_code));
            anr.setPerson_class(a0191_keys.get(entityName));
            jcbPersonClass.setSelectedItem(a0191_keys.get(entityName));
        }
    }

    /**
     * 该方法用于取消编辑，并根据标识决定是否保存
     * @param save_flag：是否保存
     */
    private void cancelEdit(boolean save_flag) {
        if (cur_rule == null) {
            return;
        }
        if (cur_rule.getNew_flag() == 1) {
            perfix_change_flag = true;
        }
        if (perfix_change_flag && save_flag) {
            if (MsgUtil.showNotConfirmDialog(EmpRegisterMsg.msg022)) {
                save_flag = false;
            }
        } else {
            save_flag = false;
        }
        if (save_flag) {
            saveObj(cur_rule);
        } else {
            if (cur_rule.getNew_flag() == 1) {
                ftable.deleteRow(ftable.getCurrentRowIndex());
            } else {
                AutoNoRule anr = (AutoNoRule) CommUtil.fetchEntityBy("from AutoNoRule anr where anr.autoNoRule_key='" + cur_rule.getAutoNoRule_key() + "'");
                cur_rule = anr;
                ftable.setCurrentRow(anr);
                refreshBeanUI(anr, editable);
            }
        }
    }

    /**
     * 该方法用于返回常用参数hashtable表
     * @return
     */
    private Hashtable<String, String> getParam() {
        Hashtable<String, String> params = new Hashtable<String, String>();
        String sql = "select dept_code from deptcode where grade = (select max(grade) from deptcode) ";
        if (cur_rule.getDept_name() != null && !cur_rule.getDept_name().trim().equals("")) {
            sql += " and dept_code like '" + cur_rule.getDept_name().substring(cur_rule.getDept_name().indexOf("{") + 1, cur_rule.getDept_name().indexOf("}")) + "%'";
//            params.put("@部门代码", "'" + cur_rule.getDept_name().substring(cur_rule.getDept_name().indexOf("{") + 1, cur_rule.getDept_name().indexOf("}")) + "'");
        }
//        else {
//            String dept_code = CommUtil.selectSQL("select dept_code from deptcode where grade = (select max(grade) from deptcode)", 1).get(0).toString();
//            params.put("@部门代码", "'" + dept_code + "'");
//        }
        List list = CommUtil.selectSQL(sql);
        if (list.size() == 0) {
            list = CommUtil.selectSQL("select dept_code from deptcode where grade = (select max(grade) from deptcode)");
        }
        params.put("@部门代码", "'" + list.get(0).toString() + "'");
        return params;
    }

    /**
     * 该方法用于保存指定规则
     * @param anr：当前规则
     */
    private void saveObj(AutoNoRule anr) {
        String dept_code = anr.getDept_name();
        if (dept_code == null || dept_code.trim().equals("")) {
//            JOptionPane.showMessageDialog(null, "未设置部门");
            MsgUtil.showInfoMsg(EmpRegisterMsg.msg023);
            return;
        }
        dept_code = dept_code.substring(dept_code.indexOf("{") + 1);
        dept_code = dept_code.substring(0, dept_code.length() - 1);
        Object obj = jcbPersonClass.getSelectedItem();
        if (obj == null) {
//            JOptionPane.showMessageDialog(null, "未设置人员类别");
            MsgUtil.showInfoMsg(EmpRegisterMsg.msg024);
            return;
        }
        EntityDef ed = (EntityDef) obj;
        String person_class = ed.getEntityName();
        String old_key = anr.getAutoNoRule_id();
        anr.setAutoNoRule_id(rule_Code + "_" + dept_code + "_" + person_class);
        anr.setPerfix(jtaFormulaText.getText());
        anr.setPerson_class(ed.getEntityCaption());
        anr.setAdd_perfix(true);
        String preview_no = CommUtil.fetchNewNoBy(anr, getParam());
        if (preview_no == null) {
            log.info("前缀存在语法错误，无法生成新编码，保存失败");
//            JOptionPane.showMessageDialog(null, "前缀存在语法错误，无法生成新编码，保存失败!");
            MsgUtil.showInfoMsg(EmpRegisterMsg.msg015);
            return;
        } else if (preview_no.startsWith("@@@")) {
//            JOptionPane.showMessageDialog(null, "当前初始值A：" + anr.getInit_no() + ",数据库当前值B:" + preview_no.substring(3) + ",A不能小于B，否则导致生成序号重复!");
            MsgUtil.showInfoMsg(EmpRegisterMsg.msg025);
            return;
        }
        ValidateSQLResult result = RSImpl.saveEmpNoRule(anr, old_key);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
            log.info("保存成功");
            refreshBeanUI(cur_rule, editable);
        } else {
            log.info("保存失败");
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    /**
     * 该方法用于（重新）绑定当前规则到界面
     * @param anr：当前规则
     */
    private void refreshBinding(AutoNoRule anr) {
        bindingGroupC.removeBindingListener(bind_listener);
        bindingGroupC.unbind();
        for (Binding bd : bindingGroupC.getBindings()) {
            bindingGroupC.removeBinding(bd);
        }
        bindingGroupC.addBinding(EditorFactory.createComponentBinding(anr, "no_lenth", jtfLen, "text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroupC.addBinding(EditorFactory.createComponentBinding(anr, "init_no", jtfInit_no, "text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroupC.addBinding(EditorFactory.createComponentBinding(anr, "inc_no", jtfInc_no, "text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroupC.addBinding(EditorFactory.createComponentBinding(anr, "dept_name", jtfDept, "text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroupC.bind();
        bindingGroupC.addBindingListener(bind_listener);
    }

    /**
     * 该方法用于刷新主界面状态
     * @param anr：当前编辑的规则
     * @param editable：是否可编辑
     */
    private void refreshBeanUI(AutoNoRule anr, boolean editable) {
        if (anr == null) {
            return;
        }
        this.editable = editable;
        refreshBinding(anr);
        refreshObj(anr);
        String key = anr.getAutoNoRule_key();
        if (key != null) {
            String entityName = anr.getPerson_class();
            for (EntityDef ed : SysUtil.getPersonClass()) {
                if (ed.getEntityName().equals(entityName)) {
                    jcbPersonClass.setSelectedItem(ed);
                    break;
                }
            }
        }
        jComboBox1.setSelectedItem(anr.getNo_unit());
        boolean editting = editable || anr.getNew_flag() == 1;
        jtaFormulaText.setEditable(editting);
        jtaFormulaText.setText(anr.getPerfix());
        jtfLen.setEditable(editting);
        jtfInit_no.setEditable(editting);
        jtfInc_no.setEditable(editting);
        jcbPersonClass.setEnabled(editting);
        btnDept.setEnabled(editting);
        jComboBox1.setEnabled(editting);
//        btnEdit.setEnabled(!editable);
//        btnView.setEnabled(editable);
        setMainState(editable);
        ftable.updateUI();
        jtaFormulaText.updateUI();
        perfix_change_flag = false;
    }
    
    private void setMainState(boolean editable){
        ComponentUtil.setCompEnable(this, btnEdit, !editable);
        ComponentUtil.setCompEnable(this, btnView, editable);
        ComponentUtil.setCompEnable(this, btnAdd, !editable);
        ComponentUtil.setCompEnable(this, btnDel, !editable);
        ComponentUtil.setCompEnable(this, btnSave, editable);
        ComponentUtil.setCompEnable(this, btnCancel, editable);
    }
    
    /**
     * 该方法用于部门树选择
     */
    private void selectDept() {
        DeptSelectDlg dlg = new DeptSelectDlg(UserContext.getDepts(false));
        ContextManager.locateOnMainScreenCenter(dlg);
        dlg.setVisible(true);
        if (dlg.isClick_ok()) {
            jtfDept.setText(dlg.getCurDept().getContent() + "{" + dlg.getCurDept().getDept_code() + "}");
            if (!jtfDept.getText().equals(cur_rule.getDept_name())) {
                perfix_change_flag = true;
                cur_rule.setDept_name(jtfDept.getText());
                log.info("编码规则制定部门:" + jtfDept.getText());
            }
        }
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jToolBar1 = new javax.swing.JToolBar();
        btnAdd = new javax.swing.JButton();
        btnEdit = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jtfDept = new javax.swing.JTextField();
        btnDept = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        jcbPersonClass = new javax.swing.JComboBox();
        jPanel6 = new javax.swing.JPanel();
        jLabel5 = new javax.swing.JLabel();
        jtfInit_no = new javax.swing.JTextField();
        jLabel6 = new javax.swing.JLabel();
        jtfInc_no = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jComboBox1 = new javax.swing.JComboBox();
        jLabel7 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtfLen = new javax.swing.JTextField();
        pnlEditor = new javax.swing.JPanel();
        pnlPara = new javax.swing.JPanel();
        jPanel3 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnPriview = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAdd.setText("新增");
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnAdd);

        btnEdit.setText("编辑");
        btnEdit.setFocusable(false);
        btnEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnEdit);

        btnView.setText("浏览");
        btnView.setFocusable(false);
        btnView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnView);

        btnSave.setText("保存");
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSave);

        btnCancel.setText("取消");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnCancel);

        btnDel.setText("删除");
        btnDel.setFocusable(false);
        btnDel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnDel);

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(2);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder("已有规则："));
        jPanel1.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setTopComponent(jPanel1);

        jSplitPane2.setDividerLocation(500);
        jSplitPane2.setDividerSize(2);

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("适用对象："));

        jLabel1.setText("部门代码 ");

        jtfDept.setEditable(false);

        btnDept.setText("...");

        jLabel2.setText(" 人员类别 ");

        jcbPersonClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtfDept, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addComponent(btnDept, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbPersonClass, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(22, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfDept, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbPersonClass, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDept, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("规则设定："));

        jLabel5.setText("初始值  ");

        jtfInit_no.setBorder(javax.swing.BorderFactory.createBevelBorder(1));

        jLabel6.setText("增长值");

        jtfInc_no.setBorder(javax.swing.BorderFactory.createBevelBorder(1));

        jLabel3.setText("前缀 ");

        jComboBox1.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "顺序编码", "按年", "按月", "按天" }));

        jLabel7.setText(" 编码方式");

        jLabel4.setText(" 序号位数");

        jtfLen.setBorder(javax.swing.BorderFactory.createBevelBorder(1));

        pnlEditor.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, 60, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(jtfInc_no, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE)
                            .addComponent(jtfInit_no, javax.swing.GroupLayout.DEFAULT_SIZE, 182, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE)
                            .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, 70, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jComboBox1, 0, 135, Short.MAX_VALUE)
                            .addComponent(jtfLen, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE)))
                    .addComponent(pnlEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 395, Short.MAX_VALUE))
                .addContainerGap(24, Short.MAX_VALUE))
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfInit_no, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfLen, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(13, 13, 13)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfInc_no, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jComboBox1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel6Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(58, 58, 58))
                    .addComponent(pnlEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 80, Short.MAX_VALUE)))
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane2.setLeftComponent(jPanel4);

        pnlPara.setBorder(javax.swing.BorderFactory.createTitledBorder("常用前缀参数："));
        pnlPara.setLayout(new java.awt.BorderLayout());
        jSplitPane2.setRightComponent(pnlPara);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 645, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2)
        );

        jSplitPane1.setRightComponent(jPanel2);

        btnPriview.setText("预览编号");

        btnClose.setText("关闭");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addContainerGap(470, Short.MAX_VALUE)
                .addComponent(btnPriview)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnClose)
                .addGap(33, 33, 33))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnPriview)
                    .addComponent(btnClose))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 647, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 457, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnDept;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnPriview;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnView;
    private javax.swing.JComboBox jComboBox1;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JComboBox jcbPersonClass;
    private javax.swing.JTextField jtfDept;
    private javax.swing.JTextField jtfInc_no;
    private javax.swing.JTextField jtfInit_no;
    private javax.swing.JTextField jtfLen;
    private javax.swing.JPanel pnlEditor;
    private javax.swing.JPanel pnlPara;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
