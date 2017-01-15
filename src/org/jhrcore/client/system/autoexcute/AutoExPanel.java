/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * AutoExPanel.java
 *
 * Created on 2013-9-24, 17:08:02
 */
package org.jhrcore.client.system.autoexcute;

import org.jhrcore.client.system.comm.AddCommMapUserPnl;
import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.FR_Rebuilder;
import org.jhrcore.client.UserContext;
import org.jhrcore.comm.CodeManager;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.AutoExcuteScheme;
import org.jhrcore.entity.Code;
import org.jhrcore.entity.CommMap;
import org.jhrcore.entity.annotation.ObjectListHint;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.right.FuntionRight;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.SysImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.CodeSelectDialog;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.FormulaEditorPanel;
import org.jhrcore.ui.HrTextPane;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.ValidateEntity;
import org.jhrcore.ui.listener.CommEditAction;
import org.jhrcore.ui.listener.IPickBeanPanelEditListener;
import org.jhrcore.ui.listener.IPickFormulaEditorListener;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.util.UtilTool;

/**
 *
 * @author mxliteboss
 */
public class AutoExPanel extends javax.swing.JPanel implements IModulePanel {

    public static String module_code = "SysAutoExcute";
    private BeanPanel beanPanel = new BeanPanel();
    private HrTextPane htpContent = null;
    private HrTextPane htpText = null;
    private HrTextPane htpCal = null;
    private FormulaEditorPanel pnlEditor = new FormulaEditorPanel();
    private JTree schemeTree;
    private JTree para_tree = null;
    private AutoExSchemeModel schemeModel;
    private ExcuteTreeModel para_model;
    private AutoExcuteScheme curScheme = null;
    private CheckTreeNode cur_node = null;
    private int isCal = 0;
    private boolean edit = false;
    private FTable usrTable = null;
    private List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
    private List<TempFieldInfo> default_fields = new ArrayList<TempFieldInfo>();
    private String order_sql = "A01.a0190";
    private int tabIndex = 0;
    private DocumentListener doc_listener;
    private boolean change_flag = false;
//    private ExecPlanDetailPanel pnlPlanDetail;

    /** Creates new form AutoExPanel */
    public AutoExPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        schemeModel = new AutoExSchemeModel();
        schemeTree = new JTree(schemeModel);
        schemeTree.setRootVisible(false);
        schemeTree.setShowsRootHandles(true);
        pnlScheme.add(new JScrollPane(schemeTree), BorderLayout.CENTER);
        HRRendererView.getCommMap().initTree(schemeTree);
        beanPanel.setBean(new AutoExcuteScheme(), EntityBuilder.getCommFieldNameListOf(AutoExcuteScheme.class, EntityBuilder.COMM_FIELD_VISIBLE_ALL));
        //  beanPanel.setEditable(true);
        List<String> dis_list = new ArrayList<String>();
        dis_list.add("scheme_type");
        beanPanel.setDisable_fields(dis_list);
        beanPanel.bind();
        pnlBean.add(new JScrollPane(beanPanel));
        htpContent = new HrTextPane();
        pnlContent.add(htpContent);
        htpText = new HrTextPane();
        pnlText.add(htpText);
        htpCal = new HrTextPane();
        pnlCalContent.add(htpCal);
        pnlForEditor.add(pnlEditor);
        para_model = new ExcuteTreeModel(module_code);
        para_tree = new JTree(para_model);
        HRRendererView.getParaFieldTypeMap(para_tree).initTree(para_tree);
        para_tree.setRootVisible(false);
        para_tree.setShowsRootHandles(true);
        pnlField.add(new JScrollPane(para_tree));
//        pnlPlanDetail = new ExecPlanDetailPanel();
//        pnlPlan.add(pnlPlanDetail);
        usrTable = new FTable(CommMap.class, false, false, false, module_code);
        EntityBuilder.buildInfo(A01.class, all_fields, default_fields, "#A01");
        all_fields.addAll(EntityBuilder.getCommFieldInfoListOf(CommMap.class, EntityBuilder.COMM_FIELD_VISIBLE));
        EntityBuilder.buildInfoToDefault(all_fields, default_fields, "c_user;c_date");
        usrTable.getOther_entitys().put("A01", "A01 A01,A01PassWord A01PassWord,RoleA01 RoleA01,CommMap com");
        usrTable.getOther_entity_keys().put("A01", "A01.a01_key=A01PassWord.a01_key and A01PassWord.a01password_key=RoleA01.a01password_key and RoleA01.rolea01_key=com.user_key and com.commMap_key ");
        usrTable.setAll_fields(all_fields, default_fields, module_code);
        tabUserPnl.add(usrTable, BorderLayout.CENTER);
    }

    private void setupEvents() {
        schemeTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (e.getPath() == null) {
                    return;
                }
                if (schemeTree.getSelectionPath() == null) {
                    return;
                }
                cur_node = (CheckTreeNode) schemeTree.getSelectionPath().getLastPathComponent();
                pickClose();
                refreshUI(cur_node);
                if (tabIndex == 1) {
                    reshUserTab(null);
                }
                change_flag = false;
            }
        });
        ActionListener alSearch = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                reshUserTab(jtfSearch.getText());
            }
        };
        btnSearch.addActionListener(alSearch);
        jtfSearch.addActionListener(alSearch);
        jTabbedPane1.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                pickClose();
                tabIndex = jTabbedPane1.getSelectedIndex();
                if (tabIndex == 1) {
                    reshUserTab(null);
                }
                change_flag = false;
            }
        });
        pnlEditor.addPickFormulaEditorListener(new IPickFormulaEditorListener() {

            @Override
            public void pickEditor(String operator) {
                int tem = getCurTextPane().getSelectionStart();
                getCurTextPane().replaceSelection(operator.toLowerCase());
                getCurTextPane().setCaretPosition(tem + operator.length());
                getCurTextPane().requestFocus();
            }
        });
        para_tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() < 2) {
                    return;
                }

                if (para_tree.getSelectionPath() == null) {
                    return;
                }

                if (para_tree.getSelectionPath().getLastPathComponent() == para_tree.getModel().getRoot()) {
                    return;
                }
                CheckTreeNode node = (CheckTreeNode) para_tree.getSelectionPath().getLastPathComponent();
                Object obj = node.getUserObject();
                if (obj instanceof String || obj instanceof EntityDef) {
                    return;
                }
                CheckTreeNode parent = (CheckTreeNode) node.getParent();
                int tmp = getCurTextPane().getSelectionStart();
                String operator = "";
                String entity_name = "[" + parent.getUserObject().toString() + ".";
                boolean isPara = obj.toString().startsWith("@");
                if (obj instanceof TempFieldInfo && ((TempFieldInfo) obj).getField_name().endsWith("_code_")) {
                    TempFieldInfo tfi = (TempFieldInfo) obj;
                    ObjectListHint objHint = tfi.getField().getAnnotation(ObjectListHint.class);
                    if (objHint != null && objHint.hqlForObjectList().startsWith("from Code ")) {
                        String hql = objHint.hqlForObjectList();
                        String code_type = hql.substring(hql.indexOf("=") + 1);
                        CodeSelectDialog csmDlg = new CodeSelectDialog(CodeManager.getCodeManager().getCodeListBy(code_type), code_type, null, TreeSelectMod.nodeCheckMod);
                        ContextManager.locateOnMainScreenCenter(csmDlg);
                        csmDlg.setVisible(true);
                        if (csmDlg.isClick_ok()) {
                            List<Code> codes = csmDlg.getSelectCodes(false);
                            if (codes.size() == 0) {
                                return;
                            }
                            String str = "";
                            List<String> like_str = new ArrayList<String>();
                            for (Code c : codes) {
                                if (c.isEnd_flag()) {
                                    str += "'[" + code_type + "." + c.getCode_name() + "]',";
                                } else {
                                    like_str.add("[" + code_type + "." + c.getCode_name() + "]");
                                }
                            }
                            if (!str.equals("")) {
                                str = str.substring(0, str.length() - 1);
                            }
                            for (String s : like_str) {
                                operator += " " + entity_name + tfi.getCaption_name() + "] like '" + s + "%' or ";
                            }
                            if (!str.equals("")) {
                                operator += " " + entity_name + tfi.getCaption_name() + "] in(" + str + ") or ";
                            }
                            operator = operator.substring(0, operator.length() - 3);
                        }
                    } else {
                        operator = " " + entity_name + obj.toString() + "] ";
                    }
                } else {
                    operator = " " + entity_name + obj.toString() + "] ";
                }
                if (isPara) {
                    operator = operator.replace("[", "");
                    operator = operator.replace("]", "");
                }
                getCurTextPane().replaceSelection(operator);
                getCurTextPane().setCaretPosition(tmp + operator.length());
                getCurTextPane().requestFocus();
            }
        });

        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addAutoExcuteScheme();
            }
        });
        btnEdit.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                edit = true;
                setMainState();
            }
        });
        btnView.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                edit = false;
                setMainState();
                //  setData();
                CommEditAction.doViewAction(curScheme, null, beanPanel);
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveObject();
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                // setData();
                CommEditAction.doCancelAction(curScheme, null, beanPanel);
            }
        });
        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                htpText.setText("");
            }
        });

        btnSQL.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (curScheme == null) {
                    return;
                }
                String sql_text = transfer_to_SQL(htpText.getText());
                MsgUtil.showHRValidateMsg(sql_text, "", curScheme.isUsed_flag());
            }
        });
        btnCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveObject();
            }
        });

        btnCalClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                htpCal.setText("");
            }
        });

        btnCalSQL.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (curScheme == null) {
                    return;
                }
                String sql_text = transfer_to_SQL(htpCal.getText());
                MsgUtil.showHRValidateMsg(sql_text, "", curScheme.isUsed_flag());
            }
        });
        btnCalCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveObject();
            }
        });

        jcb_fujia.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                curScheme.setData_flag(jcb_fujia.isSelected());
                curScheme.setShow_flag(jcb_only.isSelected());
                setMainState();
            }
        });
        jcb_only.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                curScheme.setShow_flag(jcb_only.isSelected());
            }
        });
        btnSetUsed.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setUserType(true);
            }
        });
        btnStopUse.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setUserType(false);
            }
        });
        btnAddUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (curScheme == null) {
                    return;
                }
                CommMap cm = (CommMap) UtilTool.createUIDEntity(CommMap.class);
                cm.setC_user_key(UserContext.rolea01_key);
                cm.setC_user(UserContext.getCurPerson());
                cm.setMap_name(curScheme.getScheme_name());
                cm.setMap_type(curScheme.getScheme_type());
                cm.setMap_key(curScheme.getAutoExcuteScheme_key());
                AddCommMapUserPnl pnl = new AddCommMapUserPnl(cm);
                pnl.addPickWindowCloseListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        reshUserTab(null);
                    }
                });
                ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(btnAddUser), pnl, true, "挑选用户", 600, 600);
            }
        });
        btnDelUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (usrTable.getSelectKeys().isEmpty()) {
                    return;
                }
                if (MsgUtil.showNotConfirmDialog("你确定删除所选项吗？")) {
                    return;
                }
                ValidateSQLResult result = CommUtil.excuteSQLs("delete from CommMap where commMap_key in", usrTable.getSelectKeys());
                if (result.getResult() == 0) {
                    usrTable.deleteSelectedRows();
                    MsgUtil.showHRSaveSuccessMsg(null);
                } else {
                    MsgUtil.showHRSaveErrorMsg(result.getMsg());
                }
            }
        });
        doc_listener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                change_flag = true;
            }
        };

        htpText.getDocument().addDocumentListener(doc_listener);
        htpCal.getDocument().addDocumentListener(doc_listener);
        ComponentUtil.initTreeSelection(schemeTree);
    }

    private void setUserType(boolean boo) {
        if (curScheme == null) {
            return;
        }
        if (boo && curScheme.isUsed_flag()) {
            MsgUtil.showInfoMsg("请选择已停用的方案进行启用");
            return;
        }
        if (!boo && !curScheme.isUsed_flag()) {
            MsgUtil.showInfoMsg("请选择正在启用的方案进行停用");
            return;
        }
        curScheme.setUsed_flag(boo);
        ValidateSQLResult result = CommUtil.updateEntity(curScheme);
        if (result.getResult() == 0) {
            cur_node.removeFromParent();
            CheckTreeNode pNode = schemeModel.getRootType(curScheme);
            CheckTreeNode node = new CheckTreeNode(curScheme);
            pNode.add(node);
            ComponentUtil.initTreeSelection(schemeTree, node);
        }
    }

    private String transfer_to_SQL(String formula_meaning) {
        Hashtable<String, String> k_keywords = getCurTextPane().getK_keywords();
        for (String key : k_keywords.keySet()) {
            formula_meaning = formula_meaning.replace(key, k_keywords.get(key));
        }
        return formula_meaning;
    }

    public void saveObject() {
        if (curScheme == null) {
            return;
        }
        if (!"自动计算".equals(curScheme.getScheme_type())) {
            curScheme.setData_flag(jcb_fujia.isSelected());
            curScheme.setShow_flag(jcb_only.isSelected());
            curScheme.setContent(htpContent.getText());
        }
        curScheme.setFormula_meaning(getCurTextPane().getText());
        curScheme.setFormula(transfer_to_SQL(getCurTextPane().getText()));
        curScheme.setUsed_flag(false);
        try {
            FR_Rebuilder.checkSQLRight(curScheme.getFormula());
        } catch (Exception e) {
            MsgUtil.showHRValidateMsg(e.getMessage(), "", false);
            return;
        }
        String sql = curScheme.getFormula();
        boolean isUpdate = curScheme.getScheme_type().equals("自动计算");
        ValidateSQLResult result = CommUtil.validateSQL(sql, isUpdate);
        String sql_msg1 = sql;
        if (result.getResult() == 0) {
            curScheme.setUsed_flag(true);
        } else {
            sql_msg1 += ";\n错误提示：\n    " + result.getMsg();
        }
        MsgUtil.showHRValidateMsg(sql_msg1, "", result.getResult() == 0);
        ValidateSQLResult vs = CommUtil.saveOrUpdate(curScheme);
        if (vs.getResult() == 0) {
            CommMap cm = (CommMap) UtilTool.createUIDEntity(CommMap.class);
            cm.setC_user_key(UserContext.rolea01_key);
            cm.setC_user(UserContext.getCurPerson());
            cm.setMap_name(curScheme.getScheme_name());
            cm.setMap_type(curScheme.getScheme_type());
            cm.setMap_key(curScheme.getAutoExcuteScheme_key());
            SysImpl.saveUsers(Arrays.asList(new String[]{UserContext.rolea01_key}), cm);
            MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
        } else {
            MsgUtil.showHRSaveErrorMsg(vs);
        }
    }

    private HrTextPane getCurTextPane() {
        if (isCal <= 1) {
            return htpCal;
        }
        return htpText;
    }

    private void refreshUI(CheckTreeNode node) {
        curScheme = null;
        jtpContent.removeAll();
        Object obj = node.getUserObject();
        if (obj instanceof AutoExcuteScheme) {
            curScheme = (AutoExcuteScheme) obj;
            isCal = curScheme.getScheme_type().equals("自动计算") ? 1 : 2;
            beanPanel.setBean(curScheme);
        }
        if (curScheme == null) {
            return;
        }
        //   beanPanel.setEditable(isCal > 0);
        beanPanel.bind();
        if (isCal > 0) {
            FuntionRight fr = curScheme.getFuntionRight();
            String code = "";
            if (fr != null) {
                String sql = "select fun_module_flag from FuntionRight fr where fr.fun_parent_code='1' and ";
                if (UserContext.sql_dialect.equals("sqlserver")) {
                    sql += " charindex(fr.fun_module_flag,'" + fr.getFun_module_flag() + "')=1";
                } else {
                    sql += " instr('" + fr.getFun_module_flag() + "',fr.fun_module_flag)=1";
                }
                List list = CommUtil.selectSQL(sql);
                if (list.size() > 0) {
                    code = list.get(0).toString();
                }
            }
            para_model.buildTree(code);
            para_tree.updateUI();
        }
        if (isCal <= 1) {
            jtpContent.add("计算脚本", pnlCal);
            htpCal.setEditable(isCal == 1);
        } else {
            htpContent.setText(curScheme.getContent());
            jtpContent.add("提示内容", pnlWake);
            jcb_fujia.setSelected(curScheme.isData_flag());
            jcb_only.setSelected(curScheme.isShow_flag());
        }
        getCurTextPane().revokeDocumentKeys(para_model.getLookups(), para_model.getKeyword_groups(), para_model.getK_keywords());
        getCurTextPane().setText(curScheme.getFormula_meaning());
        jtpContent.updateUI();
    }

    private void reshUserTab(String quickStr) {
        if (curScheme == null) {
            return;
        }
        String sql = "from CommMap com,RoleA01 RoleA01,Role Role,A01PassWord A01PassWord,A01 A01 where A01.a01_key=A01PassWord.a01_key and A01PassWord.a01password_key=RoleA01.a01password_key and Role.role_key=RoleA01.role_key and RoleA01.rolea01_key=com.user_key and com.map_key='" + curScheme.getAutoExcuteScheme_key() + "'";
        quickStr = SysUtil.objToStr(quickStr);
        if (!quickStr.equals("")) {
            sql += "and " + SysUtil.getA01SearchSQL(quickStr);
        }
        sql += " order by " + order_sql;
        PublicUtil.getProps_value().setProperty(CommMap.class.getName(), "from CommMap com where com.commMap_key in");
        usrTable.setObjects(CommUtil.selectSQL("select com.commMap_key " + sql));
    }

    private void addAutoExcuteScheme() {
        List<String> sfields = new ArrayList();
        sfields.addAll(EntityBuilder.getCommFieldNameListOf(AutoExcuteScheme.class, EntityBuilder.COMM_FIELD_VISIBLE_ALL));
        sfields.remove("used_flag");
        sfields.remove("content");
        ValidateEntity ve = new ValidateEntity() {

            @Override
            public boolean isEntityValidate(Object obj) {
                AutoExcuteScheme auto = (AutoExcuteScheme) obj;
                String msg = "";
                if (null == auto.getScheme_name() || "".equals(auto.getScheme_name())) {
                    msg = "方案名称不能为空！";
                } else if (null == auto.getScheme_type() || "".equals(auto.getScheme_type())) {
                    msg = "方案类型不能为空！";
                } else if (null == auto.getFuntionRight()) {
                    msg = "所属模块不能为空！";
                } else {
                    auto.setScheme_code(auto.getFuntionRight().getFun_module_flag());
                    auto.setScheme_code(auto.getFuntionRight().getFun_name());
                    if (CommUtil.exists("select 1 from AutoExcuteScheme where scheme_name='" + auto.getScheme_name() + "' and scheme_type='" + auto.getScheme_type() + "' and autoExcuteScheme_key!='" + auto.getAutoExcuteScheme_key() + "'")) {
                        msg = "同种方案类型不能有相同名称的方案名称！";
                    }
                }
                if (!msg.equals("")) {
                    MsgUtil.showErrorMsg(msg);
                    return false;
                }
                return true;
            }
        };
        IPickBeanPanelEditListener listener = new IPickBeanPanelEditListener() {

            @Override
            public void pickSave(Object obj) {
                AutoExcuteScheme auto = (AutoExcuteScheme) obj;
                ValidateSQLResult result = CommUtil.saveEntity(auto);
                if (result.getResult() == 0) {
                    CheckTreeNode pNode = schemeModel.getRootType(auto);
                    CheckTreeNode node = new CheckTreeNode(auto);
                    pNode.add(node);
                    ComponentUtil.initTreeSelection(schemeTree, node);
                    MsgUtil.showInfoMsg("新增成功");
                } else {
                    MsgUtil.showErrorMsg(result.getMsg());
                }
            }

            @Override
            public Object getNew() {
                AutoExcuteScheme auto = (AutoExcuteScheme) UtilTool.createUIDEntity(AutoExcuteScheme.class);
                auto.setUsed_flag(true);
                return auto;
            }
        };
        BeanPanel.editForRepeat(ContextManager.getMainFrame(), sfields, "新增方案：", ve, listener);
    }

    private void setMainState() {
        ComponentUtil.setCompEnable(this, btnEdit, !edit);
        ComponentUtil.setCompEnable(this, btnView, edit);
        ComponentUtil.setCompEnable(this, btnSave, edit);
        ComponentUtil.setCompEnable(this, btnCancel, edit);
        beanPanel.setEditable(edit);
        beanPanel.bind();
        htpContent.setEditable(edit);
        htpText.setEditable(edit && jcb_fujia.isSelected());
        htpCal.setEditable(edit);
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel2 = new javax.swing.JPanel();
        toolBar = new javax.swing.JToolBar();
        btnAdd = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnEdit = new javax.swing.JButton();
        btnView = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnSetUsed = new javax.swing.JButton();
        btnStopUse = new javax.swing.JButton();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        jPanel3 = new javax.swing.JPanel();
        pnlBean = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jTabbedPane2 = new javax.swing.JTabbedPane();
        pnlField = new javax.swing.JPanel();
        pnlForEditor = new javax.swing.JPanel();
        jtpContent = new javax.swing.JTabbedPane();
        pnlWake = new javax.swing.JPanel();
        pnlContent = new javax.swing.JPanel();
        jcb_fujia = new javax.swing.JCheckBox();
        jcb_only = new javax.swing.JCheckBox();
        pnlText = new javax.swing.JPanel();
        btnClear = new javax.swing.JButton();
        btnCheck = new javax.swing.JButton();
        btnSQL = new javax.swing.JButton();
        pnlCal = new javax.swing.JPanel();
        btnCalClear = new javax.swing.JButton();
        btnCalCheck = new javax.swing.JButton();
        btnCalSQL = new javax.swing.JButton();
        pnlCalContent = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnAddUser = new javax.swing.JButton();
        btnDelUser = new javax.swing.JButton();
        jSeparator3 = new javax.swing.JToolBar.Separator();
        jLabel1 = new javax.swing.JLabel();
        jtfSearch = new javax.swing.JTextField();
        btnSearch = new javax.swing.JButton();
        tabUserPnl = new javax.swing.JPanel();
        pnlScheme = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);

        toolBar.setFloatable(false);
        toolBar.setRollover(true);

        btnAdd.setText("新增");
        btnAdd.setFocusable(false);
        btnAdd.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAdd.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnAdd);
        toolBar.add(jSeparator1);

        btnEdit.setText("编辑");
        btnEdit.setFocusable(false);
        btnEdit.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnEdit);

        btnView.setText("浏览");
        btnView.setFocusable(false);
        btnView.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnView.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnView);

        btnSave.setText("保存");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnSave);

        btnCancel.setText("取消");
        btnCancel.setFocusable(false);
        btnCancel.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnCancel.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnCancel);
        toolBar.add(jSeparator2);

        btnSetUsed.setText("启用");
        btnSetUsed.setFocusable(false);
        btnSetUsed.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSetUsed.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnSetUsed);

        btnStopUse.setText("停用");
        btnStopUse.setFocusable(false);
        btnStopUse.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnStopUse.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        toolBar.add(btnStopUse);

        pnlBean.setLayout(new java.awt.BorderLayout());

        jSplitPane2.setDividerLocation(400);
        jSplitPane2.setDividerSize(2);

        pnlField.setLayout(new java.awt.BorderLayout());
        jTabbedPane2.addTab("字段列表", pnlField);

        pnlForEditor.setLayout(new java.awt.BorderLayout());
        jTabbedPane2.addTab("运算符", pnlForEditor);

        jSplitPane2.setRightComponent(jTabbedPane2);

        pnlContent.setLayout(new java.awt.BorderLayout());

        jcb_fujia.setText("附加数据内容");

        jcb_only.setText("只有当有数据内容时提示");

        pnlText.setLayout(new java.awt.BorderLayout());

        btnClear.setText("清除");

        btnCheck.setText("确认校验");

        btnSQL.setText("SQL");

        javax.swing.GroupLayout pnlWakeLayout = new javax.swing.GroupLayout(pnlWake);
        pnlWake.setLayout(pnlWakeLayout);
        pnlWakeLayout.setHorizontalGroup(
            pnlWakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWakeLayout.createSequentialGroup()
                .addGroup(pnlWakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(pnlWakeLayout.createSequentialGroup()
                        .addComponent(jcb_fujia)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcb_only))
                    .addGroup(pnlWakeLayout.createSequentialGroup()
                        .addGap(4, 4, 4)
                        .addComponent(btnClear)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnCheck)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnSQL)))
                .addContainerGap(140, Short.MAX_VALUE))
            .addComponent(pnlContent, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
            .addComponent(pnlText, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
        );
        pnlWakeLayout.setVerticalGroup(
            pnlWakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlWakeLayout.createSequentialGroup()
                .addComponent(pnlContent, javax.swing.GroupLayout.PREFERRED_SIZE, 70, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcb_fujia)
                    .addComponent(jcb_only))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlWakeLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCheck)
                    .addComponent(btnSQL)
                    .addComponent(btnClear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlText, javax.swing.GroupLayout.DEFAULT_SIZE, 119, Short.MAX_VALUE))
        );

        jtpContent.addTab("提示内容", pnlWake);

        btnCalClear.setText("清除");

        btnCalCheck.setText("确认校验");

        btnCalSQL.setText("SQL");

        pnlCalContent.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pnlCalLayout = new javax.swing.GroupLayout(pnlCal);
        pnlCal.setLayout(pnlCalLayout);
        pnlCalLayout.setHorizontalGroup(
            pnlCalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCalLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnCalClear)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCalCheck)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCalSQL)
                .addContainerGap(183, Short.MAX_VALUE))
            .addComponent(pnlCalContent, javax.swing.GroupLayout.DEFAULT_SIZE, 394, Short.MAX_VALUE)
        );
        pnlCalLayout.setVerticalGroup(
            pnlCalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlCalLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(pnlCalLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCalCheck)
                    .addComponent(btnCalSQL)
                    .addComponent(btnCalClear))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlCalContent, javax.swing.GroupLayout.DEFAULT_SIZE, 206, Short.MAX_VALUE))
        );

        jtpContent.addTab("计算脚本", pnlCal);

        jSplitPane2.setLeftComponent(jtpContent);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
            .addComponent(pnlBean, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(pnlBean, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 276, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("方案设置", jPanel3);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnAddUser.setText("增加");
        btnAddUser.setFocusable(false);
        btnAddUser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnAddUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnAddUser);

        btnDelUser.setText("删除");
        btnDelUser.setFocusable(false);
        btnDelUser.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnDelUser.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnDelUser);
        jToolBar1.add(jSeparator3);

        jLabel1.setText("查找：");
        jToolBar1.add(jLabel1);

        jtfSearch.setMaximumSize(new java.awt.Dimension(120, 2147483647));
        jToolBar1.add(jtfSearch);

        btnSearch.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/search.png"))); // NOI18N
        btnSearch.setFocusable(false);
        btnSearch.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSearch.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSearch);

        tabUserPnl.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
            .addComponent(tabUserPnl, javax.swing.GroupLayout.DEFAULT_SIZE, 577, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(tabUserPnl, javax.swing.GroupLayout.DEFAULT_SIZE, 371, Short.MAX_VALUE))
        );

        jTabbedPane1.addTab("员工设置", jPanel4);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolBar, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 582, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(toolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 431, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        pnlScheme.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlScheme);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 788, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 464, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnAddUser;
    private javax.swing.JButton btnCalCheck;
    private javax.swing.JButton btnCalClear;
    private javax.swing.JButton btnCalSQL;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelUser;
    private javax.swing.JButton btnEdit;
    private javax.swing.JButton btnSQL;
    private javax.swing.JButton btnSave;
    private javax.swing.JButton btnSearch;
    private javax.swing.JButton btnSetUsed;
    private javax.swing.JButton btnStopUse;
    private javax.swing.JButton btnView;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JToolBar.Separator jSeparator3;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JTabbedPane jTabbedPane2;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JCheckBox jcb_fujia;
    private javax.swing.JCheckBox jcb_only;
    private javax.swing.JTextField jtfSearch;
    private javax.swing.JTabbedPane jtpContent;
    private javax.swing.JPanel pnlBean;
    private javax.swing.JPanel pnlCal;
    private javax.swing.JPanel pnlCalContent;
    private javax.swing.JPanel pnlContent;
    private javax.swing.JPanel pnlField;
    private javax.swing.JPanel pnlForEditor;
    private javax.swing.JPanel pnlScheme;
    private javax.swing.JPanel pnlText;
    private javax.swing.JPanel pnlWake;
    private javax.swing.JPanel tabUserPnl;
    private javax.swing.JToolBar toolBar;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setFunctionRight() {
        setMainState();
    }

    @Override
    public void pickClose() {
        if (change_flag) {
            if (curScheme == null) {
                return;
            }
            if (JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(btnSave), "方案有改动，是否保存", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                saveObject();
            }
            change_flag = false;
        }
    }

    @Override
    public void refresh() {
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
