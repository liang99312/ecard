/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * CodePanel2.java
 *
 * Created on 2008-11-1, 16:05:20
 */
package org.jhrcore.client.system;

import org.jhrcore.client.system.comm.ExportCodeSelectDlg;
import org.jhrcore.client.system.comm.IPickCodeAddListener;
import com.foundercy.pf.control.listener.IPickQueryExListener;
import com.foundercy.pf.control.table.FTable;
import com.foundercy.pf.control.table.ITableCellEditable;
import com.foundercy.pf.control.table.RowChangeListner;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.PinYinMa;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.system.comm.AddCodeDlg;
import org.jhrcore.comm.CodeManager;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.Code;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.FieldDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.query.QueryScheme;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.client.system.comm.CodeTreeModel;
import org.jhrcore.iservice.impl.SysImpl;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.SearchTreeFieldDialog;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author Administrator
 */
public class SysCodePanel extends javax.swing.JPanel implements IModulePanel {

    private JPopupMenu jpopmenu = new JPopupMenu();
    private JTree codeTree;
    private DefaultMutableTreeNode curNode = null;
    private JButton btnAdd = new JButton("增加");
    private JButton btnEdit = new JButton("编辑");
    private JButton btnView = new JButton("浏览");
    private JButton btnDel = new JButton("删除");
    private JButton btnSave = new JButton("保存");
    private JButton btnCancel = new JButton("取消");
    private JButton btnChangeUsed = new JButton("已选");
    private JButton btnExportCode = new JButton("导出编码");
    private JButton b_pinyinma = new JButton("生成拼音码");
    private JButton btnCorrectGrade = new JButton("调整级数");
    private JMenuItem miAdd = new JMenuItem("增加");
    private JMenuItem miDel = new JMenuItem("删除");
    private JMenuItem miChangeUsed = new JMenuItem("已选");
    private boolean editState = false;
    private CodeTreeModel codeTreeModel;
    private List<DefaultMutableTreeNode> select_nodes;
    private FTable ftable;
    private FTable link_ftable;
    private Code change_code;
    private Code cur_code;
    private String code_order_sql = "c.code_tag";
    private String code_type = "";
    private int cur_index = 0;
    public static final String module_code = "SysCode";

    @Override
    public void setFunctionRight() {
        ComponentUtil.setSysCompFuntion(miAdd, "SysCode.btnAdd");
        ComponentUtil.setSysCompFuntion(miDel, "SysCode.btnDel");
        changeEditState(editState);
    }

    private void changeEditState(boolean isEditting) {
        this.editState = isEditting;
        btnEdit.setEnabled(!isEditting && UserContext.hasFunctionRight("SysCode.btnEdit"));
        btnView.setEnabled(isEditting && UserContext.hasFunctionRight("SysCode.btnView"));
        btnDel.setEnabled(!isEditting && UserContext.hasFunctionRight("SysCode.btnDel"));
        btnSave.setEnabled(isEditting && UserContext.hasFunctionRight("SysCode.btnSave"));
        btnCancel.setEnabled(isEditting && UserContext.hasFunctionRight("SysCode.btnCancel"));
        miDel.setEnabled(!isEditting && UserContext.hasFunctionRight("SysCode.btnDel"));
        miChangeUsed.setEnabled(!isEditting && curNode != null && curNode.getLevel() == 2 && UserContext.hasFunctionRight("SysCode.btnChangeUsed"));
        btnChangeUsed.setEnabled(!isEditting && curNode != null && curNode.getLevel() == 2 && UserContext.hasFunctionRight("SysCode.btnChangeUsed"));
        if (curNode != null && curNode.getLevel() == 2) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) curNode.getParent();
            if (node.getUserObject().equals("已选")) {
                btnChangeUsed.setText("转移到备选");
                miChangeUsed.setText("转移到备选");
            } else {
                btnChangeUsed.setText("转移到已选");
                miChangeUsed.setText("转移到已选");
            }
            btnChangeUsed.setVisible(true);
            miChangeUsed.setVisible(true);
        } else {
            btnChangeUsed.setVisible(false);
            miChangeUsed.setVisible(false);
        }
        miAdd.setVisible(curNode != null && curNode.getLevel() >= 1);
        miDel.setVisible(curNode != null && curNode.getLevel() > 1);
        ftable.setEditable(editState);
    }

    /** Creates new form CodePanel2 */
    public SysCodePanel() {
        initComponents();
        initOthers();
        setupEvents();
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
        mainpnl_left = new javax.swing.JPanel();
        pnl_sear = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        search = new javax.swing.JTextField();
        pnl_left = new javax.swing.JPanel();
        pnl = new javax.swing.JPanel();
        toolbar = new javax.swing.JToolBar();
        jTabbedPane1 = new javax.swing.JTabbedPane();
        pnl_right = new javax.swing.JPanel();
        pnlLink = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setName("jSplitPane1"); // NOI18N
        jSplitPane1.setOneTouchExpandable(true);

        mainpnl_left.setName("mainpnl_left"); // NOI18N

        pnl_sear.setName("pnl_sear"); // NOI18N

        jLabel1.setText("查找：");
        jLabel1.setName("jLabel1"); // NOI18N

        search.setName("search"); // NOI18N

        javax.swing.GroupLayout pnl_searLayout = new javax.swing.GroupLayout(pnl_sear);
        pnl_sear.setLayout(pnl_searLayout);
        pnl_searLayout.setHorizontalGroup(
            pnl_searLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_searLayout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(20, Short.MAX_VALUE))
        );
        pnl_searLayout.setVerticalGroup(
            pnl_searLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnl_searLayout.createSequentialGroup()
                .addGroup(pnl_searLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(search, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        pnl_left.setName("pnl_left"); // NOI18N
        pnl_left.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout mainpnl_leftLayout = new javax.swing.GroupLayout(mainpnl_left);
        mainpnl_left.setLayout(mainpnl_leftLayout);
        mainpnl_leftLayout.setHorizontalGroup(
            mainpnl_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnl_sear, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(pnl_left, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        mainpnl_leftLayout.setVerticalGroup(
            mainpnl_leftLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainpnl_leftLayout.createSequentialGroup()
                .addComponent(pnl_sear, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnl_left, javax.swing.GroupLayout.DEFAULT_SIZE, 372, Short.MAX_VALUE))
        );

        jSplitPane1.setLeftComponent(mainpnl_left);

        pnl.setName("pnl"); // NOI18N

        toolbar.setFloatable(false);
        toolbar.setRollover(true);
        toolbar.setName("toolbar"); // NOI18N

        jTabbedPane1.setName("jTabbedPane1"); // NOI18N

        pnl_right.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnl_right.setName("pnl_right"); // NOI18N
        pnl_right.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("编码基本信息", pnl_right);

        pnlLink.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlLink.setName("pnlLink"); // NOI18N
        pnlLink.setLayout(new java.awt.BorderLayout());
        jTabbedPane1.addTab("编码关联信息", pnlLink);

        javax.swing.GroupLayout pnlLayout = new javax.swing.GroupLayout(pnl);
        pnl.setLayout(pnlLayout);
        pnlLayout.setHorizontalGroup(
            pnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
            .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 254, Short.MAX_VALUE)
        );
        pnlLayout.setVerticalGroup(
            pnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLayout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jTabbedPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 368, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(pnl);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 458, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 405, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JLabel jLabel1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JTabbedPane jTabbedPane1;
    private javax.swing.JPanel mainpnl_left;
    private javax.swing.JPanel pnl;
    private javax.swing.JPanel pnlLink;
    private javax.swing.JPanel pnl_left;
    private javax.swing.JPanel pnl_right;
    private javax.swing.JPanel pnl_sear;
    private javax.swing.JTextField search;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        initToolBar();
        codeTreeModel = new CodeTreeModel();
        codeTree = new JTree(codeTreeModel);
        HRRendererView.getRebuildMap(codeTree).initTree(codeTree);
        codeTree.setRootVisible(false);
        codeTree.setShowsRootHandles(true);
        select_nodes = codeTreeModel.getSelect_nodes();
        jSplitPane1.setDividerSize(8);
        pnl_left.add(new JScrollPane(codeTree), BorderLayout.CENTER);
        jpopmenu.add(miAdd);
        jpopmenu.add(miDel);
        jpopmenu.add(miChangeUsed);
        ftable = new FTable(Code.class, false, true, false, module_code);
        ftable.setITableCellEditable(new ITableCellEditable() {

            @Override
            public int getCellEditable(Object obj, String fileName) {
                if (obj instanceof Code) {
                    Code code = (Code) obj;
                    if (UserContext.hasCodeEditRight(code)) {
                        return 0;
                    }
                }
                return -1;
            }
        });
        ftable.setRight_allow_flag(true);
        ftable.removeItemByCodes("show;order;sum;replace");
        pnl_right.add(ftable, BorderLayout.CENTER);
        link_ftable = new FTable(FieldDef.class, true, true, false, module_code);
        pnlLink.add(link_ftable, BorderLayout.CENTER);
        List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
        List<TempFieldInfo> defaul_fields = new ArrayList<TempFieldInfo>();
        EntityBuilder.buildInfo(EntityDef.class, all_fields, defaul_fields, "entityDef");
        EntityBuilder.buildInfo(FieldDef.class, all_fields, defaul_fields, "");
        link_ftable.setAll_fields(all_fields, defaul_fields, new ArrayList(), module_code);
        link_ftable.setRight_allow_flag(true);
        link_ftable.removeReplaceItem();
    }

    private void initToolBar() {
        toolbar.add(btnAdd);
        toolbar.add(btnEdit);
        toolbar.add(btnView);
        toolbar.add(btnSave);
        toolbar.add(btnCancel);
        toolbar.add(btnDel);
        toolbar.add(b_pinyinma);
        toolbar.add(btnExportCode);
        toolbar.add(btnCorrectGrade);
        toolbar.addSeparator();
        toolbar.add(btnChangeUsed);
    }

    private void updateGrades(DefaultMutableTreeNode parent, int grades) {
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
            Code code = (Code) node.getUserObject();
            code.setGrades(grades);
            updateGrades(node, grades);
        }
    }

    private void updateUsed(DefaultMutableTreeNode parent, boolean isUsed) {
        Code code = (Code) parent.getUserObject();
        code.setUsed(isUsed);
        for (int i = 0; i < parent.getChildCount(); i++) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) parent.getChildAt(i);
            updateUsed(node, isUsed);
        }
    }

    private void setupEvents() {
        jTabbedPane1.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                int a = cur_index;
                if (jTabbedPane1.getSelectedIndex() == a) {
                    return;
                }
                cur_index = jTabbedPane1.getSelectedIndex();
                if (cur_index == 1) {
                    for (Component c : toolbar.getComponents()) {
                        c.setEnabled(false);
                    }
                    btnChangeUsed.setEnabled(curNode != null && curNode.getLevel() == 2 && UserContext.hasFunctionRight("SysCode.btnChangeUsed"));
                } else {
                    setFunctionRight();
                }
            }
        });
        btnExportCode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ExportCodeSelectDlg exportCodeSelectDlg = new ExportCodeSelectDlg();
                exportCodeSelectDlg.setTitle("导出编码");
                ContextManager.locateOnMainScreenCenter(exportCodeSelectDlg);
                exportCodeSelectDlg.setVisible(true);
            }
        });
        b_pinyinma.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                HashSet<Code> set = new HashSet<Code>();
                Enumeration enumt = ((DefaultMutableTreeNode) codeTree.getModel().getRoot()).breadthFirstEnumeration();
                while (enumt.hasMoreElements()) {
                    Object node_obj = ((DefaultMutableTreeNode) enumt.nextElement()).getUserObject();
                    if (node_obj instanceof Code) {
                        Code tmp_code = (Code) node_obj;
                        String str = PinYinMa.ctoE(tmp_code.getCode_name());
                        if (tmp_code.getPym() == null || !tmp_code.getPym().equals(str)) {
                            tmp_code.setPym(str);
                            set.add(tmp_code);
                        }
                    }
                }
                if (set.isEmpty()) {
                    JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "拼音码已存在。",
                            "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                List<String[]> codes = new ArrayList<String[]>();
                for (Code c : set) {
                    codes.add(new String[]{c.getCode_key(), c.getPym()});
                }
                ValidateSQLResult result = SysImpl.buildCodePYM(codes);
                if (result.getResult() == 0) {
                    JOptionPane.showMessageDialog(null, "拼音码生成成功");
                } else {
                    MsgUtil.showHRSaveErrorMsg(result);
                }
            }
        });
        search.addKeyListener(new KeyAdapter() {

            @Override
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == 10) {
                    SearchTreeFieldDialog.getSearchFieldDialog().Locate(1, search.getText());
                }
            }
        });
        codeTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3 && cur_index == 0) {
                    jpopmenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
        ActionListener changeUsed_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeUsed();
            }
        };
        btnChangeUsed.addActionListener(changeUsed_listener);
        miChangeUsed.addActionListener(changeUsed_listener);
        ActionListener addCode_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addCode();
            }
        };
        btnAdd.addActionListener(addCode_listener);
        miAdd.addActionListener(addCode_listener);
        ActionListener edit_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeEditState(true);
            }
        };
        btnEdit.addActionListener(edit_listener);
        btnView.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cancelEdit(true);
                changeEditState(false);
            }
        });
        ActionListener del_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                delCode();
            }
        };
        btnDel.addActionListener(del_listener);
        miDel.addActionListener(del_listener);
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_code == null) {
                    return;
                }
                ftable.editingStopped();
                saveCode(cur_code);
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cancelEdit(false);
            }
        });
        btnCorrectGrade.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                correctGrade();
            }
        });
        ftable.addRowChangeListner(new RowChangeListner() {

            @Override
            public void rowChanged(Object obj) {
                change_code = (Code) obj;
            }
        });
        ftable.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                Runnable run2 = new Runnable() {

                    @Override
                    public void run() {
                        if (change_code != null && ftable.isEditable()) {
                            saveCode(change_code);
                            change_code = null;
                        }
                        if (cur_code == ftable.getCurrentRow()) {
                            return;
                        }
                        cur_code = (Code) ftable.getCurrentRow();
                    }
                };
                SwingUtilities.invokeLater(run2);
            }
        });
        ftable.addPickQueryExListener(new IPickQueryExListener() {

            @Override
            public void pickQuery(QueryScheme qs) {
                freshCodes(qs);
            }
        });
        codeTree.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object obj = e.getPath().getLastPathComponent();
                if (obj == null) {
                    return;
                }
                curNode = (DefaultMutableTreeNode) obj;
                if (curNode.getUserObject() instanceof Code) {
                    Code code = (Code) curNode.getUserObject();
                    String type = code.getCode_type();
                    if (!code_type.equals(type)) {
                        code_type = type;
                        refreshLinkCode();
                    }
                }
                changeEditState(editState);
                if (cur_index == 1) {
                    for (Component c : toolbar.getComponents()) {
                        c.setEnabled(false);
                    }
                    btnChangeUsed.setEnabled(curNode != null && curNode.getLevel() == 2 && UserContext.hasFunctionRight("SysCode.btnChangeUsed"));
                }
                freshCodes(null);
            }
        });
        codeTree.expandRow(0);
        codeTree.setSelectionRow(0);
        SearchTreeFieldDialog.doQuickSearch("编码树", codeTree, jpopmenu);
    }

    private void refreshLinkCode() {
        List list = new ArrayList();
        if ("".equals(code_type)) {
        } else {
            list = CommUtil.fetchEntities("from FieldDef fd join fetch fd.entityDef ed where fd.code_type_name ='" + code_type + "' order by fd.entityDef.entityName");
        }
        link_ftable.setObjects(list);
        link_ftable.updateUI();
    }

    private void cancelEdit(boolean save_flag) {
        ftable.editingStopped();
        if (save_flag && change_code != null) {
            if (JOptionPane.showConfirmDialog(ContextManager.getMainFrame(),
                    "当前编码有改动，是否需要保存", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
                save_flag = false;
            }
        } else {
            save_flag = false;
        }
        if (save_flag) {
            saveCode(change_code);
        } else if (change_code != null) {
            Code code = (Code) ftable.getCurrentRow();
            code = (Code) CommUtil.fetchEntityBy("from Code c where c.code_key='" + code.getCode_key() + "'");
            ftable.setCurrentRow(code);
        }
        change_code = null;
    }
    //编码转移：备选到已选；已选到备选

    private void changeUsed() {
        if (curNode == null) {
            return;
        }
        Code code = (Code) curNode.getUserObject();
        if (code.isUsed() && link_ftable.getObjects().size() > 0) {
            JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "该编码已用于关联，不允许转移到备选",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
            return;
        }
        ValidateSQLResult result = CommUtil.excuteHQL("update Code c set c.used=" + (code.isUsed() ? 0 : 1) + " where c.code_tag like '" + code.getCode_tag() + "%'");
        if (result == null) {
            return;
        }
        if (result.getResult() == 0) {
            DefaultMutableTreeNode tmp_node = curNode;
            DefaultMutableTreeNode next_node = ComponentUtil.getNextNode(tmp_node);
            curNode.removeFromParent();
            if (code.isUsed()) {
                codeTreeModel.getNode_notused().add(tmp_node);
            } else {
                codeTreeModel.getNode_used().add(tmp_node);
            }
            updateUsed(tmp_node, !code.isUsed());
            if (next_node != null) {
                TreePath path = new TreePath(next_node.getPath());
                codeTree.setSelectionPath(path);
                codeTree.scrollPathToVisible(path);
            }
            codeTree.updateUI();
        } else {
            JOptionPane.showMessageDialog(null, "转移失败");
        }

    }
    //增加编码

    private void addCode() {
        if (curNode == null) {
            return;
        }
        String str_root = null;
        if (curNode.getUserObject() instanceof Code) {
            if (!UserContext.hasCodeAddRight((Code) curNode.getUserObject())) {
                JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnSave), "没有新增权限");
                return;
            }
        } else {
            Set<String> set = new HashSet(codeTreeModel.rootName);
            for (char s = 'A'; s < 'Z'; s++) {
                boolean pn = false;
                for (char s2 = 'A'; s2 < 'Z'; s2++) {
                    String str = String.valueOf(s) + String.valueOf(s2);
                    if (!set.contains(str)) {
                        str_root = str;
                        pn = true;
                        break;
                    }
                }
                if (pn) {
                    break;
                }
            }
        }
        AddCodeDlg addCodeDlg = new AddCodeDlg(curNode, select_nodes, str_root);
        addCodeDlg.setTitle("新增编码");
        addCodeDlg.addPickDeptAddListener(new IPickCodeAddListener() {

            @Override
            public void AddCode(Code code) {
                CodeManager.getCodeManager().addCodeToMemory(code);
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(code);
                curNode.add(node);
                TreePath tp = new TreePath(curNode.getPath());
                codeTree.setSelectionPath(tp);
                codeTree.expandPath(tp);
                codeTree.updateUI();
                ftable.addObject(code);
                pnl_right.updateUI();
            }
        });
        ContextManager.locateOnMainScreenCenter(addCodeDlg);
        addCodeDlg.setVisible(true);
    }
    //删除编码

    private void delCode() {
        if (curNode == null) {
            return;
        }
        if (curNode.getUserObject() instanceof Code) {
            if (!UserContext.hasCodeDelRight((Code) curNode.getUserObject())) {
                JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnSave), "没有删除权限");
                return;
            }
        }
        DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) curNode.getParent();
        if (curNode.getUserObject() instanceof Code) {
            Code code = (Code) curNode.getUserObject();
            String tmp_str = "";
            boolean exist_link = CommUtil.exists("select 1 from FieldDef where code_type_name ='" + code.getCode_type() + "'");
            if (exist_link) {
                tmp_str += "该类编码已关联字段，";
                if (code.getParent_id().equals("ROOT")) {
                    tmp_str += "删除同时清空字段表编码关联，";
                } else {
                    exist_link = false;
                }
            }

            if (MsgUtil.showNotConfirmDialog(tmp_str + "确定要删除【" + curNode.getUserObject() + "】吗")) {
                return;
            }
            DefaultMutableTreeNode parent = curNode;
            while (((DefaultMutableTreeNode) parent.getParent()).getUserObject() instanceof Code) {
                parent = (DefaultMutableTreeNode) parent.getParent();
            }
            int grades = parent.getDepth() + 1;
            ValidateSQLResult result = SysImpl.delCode(code, grades);
            if (result.getResult() == 0) {
                curNode.removeFromParent();
                curNode = parent_node;
                ComponentUtil.initTreeSelection(codeTree, curNode);
                if (code.getCode_level() > 1) {
                    if ((parent.getUserObject() instanceof Code) && parent.isLeaf()) {
                        Code parent_code = (Code) parent.getUserObject();
                        parent_code.setEnd_flag(true);
                    }
                    updateGrades(parent, grades);
                }
                changeEditState(false);
                codeTree.updateUI();
                CodeManager.getCodeManager().removeCodeFromMemory(code);
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
        }
    }

    /**
     * 网格数据刷新主函数
     * @param qs：查询方案，如果为空，则表示未使用查询方案
     */
    public void freshCodes(QueryScheme qs) {
        if (curNode == null) {
            ftable.setObjects(new ArrayList());
            return;
        }
        String hql = "from Code c ";
        if (curNode.getUserObject() instanceof Code) {
            Code tmp_code = (Code) curNode.getUserObject();
            hql += "where c.code_tag like '" + tmp_code.getCode_tag() + "%'";
        } else if (curNode.getUserObject().equals("已选")) {
            hql += "where c.used = 1 and c.code_level=1";
        } else if (curNode.getUserObject().equals("备选")) {
            hql += "where c.used = 0 and c.code_level=1";
        }
        if (qs != null) {
            hql = qs.buildHql(hql, "c");
        }
        hql += " order by " + code_order_sql;
        PublicUtil.getProps_value().setProperty(Code.class.getName(), hql.substring(0, hql.indexOf("where")) + "where c.code_key in");
        List<String> keys = (List<String>) CommUtil.fetchEntities("select c.code_key " + hql);
        ftable.setObjects(keys);
        ContextManager.setStatusBar(ftable.getObjects().size());
    }

    public void saveCode(Code code) {
        if (CommUtil.exists("select 1 from Code where code_type ='" + code.getCode_type() + "' and code_level=" + code.getCode_level() + " and code_key <>'" + code.getCode_key() + "' and code_name ='" + code.getCode_name() + "' and parent_id='" + code.getParent_id() + "'")) {
            JOptionPane.showMessageDialog(
                    ContextManager.getMainFrame(), "编码名称重复！", // message
                    "错误", // title
                    JOptionPane.ERROR_MESSAGE);
            return;
        }
        ValidateSQLResult result = SysImpl.updateCode(code);
        boolean up_flag = code.getCode_level() == 1 && !code.getCode_name().equals(code.getCode_type());
        if (result.getResult() == 0) {
            Enumeration deptEnum = curNode.depthFirstEnumeration();
            while (deptEnum.hasMoreElements()) {
                DefaultMutableTreeNode tmp_node = (DefaultMutableTreeNode) deptEnum.nextElement();
                if (tmp_node.getUserObject() instanceof Code) {
                    Code tmp_code = (Code) tmp_node.getUserObject();
                    if (code.getCode_key().equals(tmp_code.getCode_key())) {
                        tmp_code.setCode_name(code.getCode_name());
                    }
                }
            }
            MsgUtil.showHRSaveSuccessMsg(ContextManager.getMainFrame());
            CodeManager.getCodeManager().addCodeToMemory(code);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
        codeTree.updateUI();
        change_code = null;
        if (up_flag) {
            JOptionPane.showMessageDialog(null, "编码类型改变，请重启hrserver服务");
            freshCodes(null);
        }
    }

    private void correctGrade() {
        DefaultMutableTreeNode pNode = null;
        for (DefaultMutableTreeNode node : select_nodes) {
            if (node.getUserObject().equals("已选")) {
                pNode = node;
            }
        }
        Enumeration enumt = pNode.children();
        List<DefaultMutableTreeNode> codeParentNodes = new ArrayList();
        Hashtable<String, List<Code>> codeKeys = new Hashtable();
        Hashtable<String, Integer> gradeKeys = new Hashtable();
        while (enumt.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
            codeParentNodes.add(node);
        }
        for (DefaultMutableTreeNode parentNode : codeParentNodes) {
            Code c = (Code) parentNode.getUserObject();
            int grades = 0;
            String tag = c.getCode_tag();
            gradeKeys.put(tag, grades);
            List codes = new ArrayList();
            codeKeys.put(tag, codes);
            Enumeration enumt1 = parentNode.breadthFirstEnumeration();
            while (enumt1.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt1.nextElement();
                codes.add(node.getUserObject());
                if ((node.getLevel() - 1) > grades) {
                    grades = node.getLevel() - 1;
                    gradeKeys.put(tag, grades);
                }
            }
        }
        List<Code> errorCodes = new ArrayList();
        for (String key : codeKeys.keySet()) {
            List<Code> codes = codeKeys.get(key);
            int grade = gradeKeys.get(key);
            for (Code c : codes) {
                if (grade != c.getGrades()) {
                    c.setGrades(grade);
                    errorCodes.add(c);
                }
            }
        }
        if (errorCodes.isEmpty()) {
            MsgUtil.showInfoMsg("所有编码级数都是正确的");
            return;
        }
        FTable ftableError = new FTable(Code.class);
        ftableError.setObjects(errorCodes);
        if (ModalDialog.doModal(ContextManager.getMainFrame(), ftableError, "级数错误编码：")) {
            List<String[]> changes = new ArrayList();
            for (Code c : errorCodes) {
                changes.add(new String[]{"'" + c.getCode_key() + "'", c.getGrades() + ""});
            }
            ValidateSQLResult result = SysImpl.changeCodeGrade(changes);
            if (result.getResult() == 0) {
                MsgUtil.showInfoMsg("调整成功，请重启HR服务");
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
        }
    }

    @Override
    public void pickClose() {
    }

    @Override
    public void refresh() {
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
