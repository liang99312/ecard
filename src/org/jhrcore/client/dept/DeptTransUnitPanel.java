/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DeptTransUnitPanel.java
 *
 * Created on 2010-7-23, 23:24:41
 */
package org.jhrcore.client.dept;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeNode;
import javax.swing.tree.TreePath;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.DeptChgLog;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.DeptImpl;
import org.jhrcore.msg.dept.DeptMngMsg;
import org.jhrcore.mutil.DeptUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptPanel;
import org.jhrcore.ui.DeptSelectDlg;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.action.CloseAction;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class DeptTransUnitPanel extends javax.swing.JPanel implements IModuleCode {

    private String module_code = "DeptMng.miUnit";
    private String type = "trans";
    private String src_code = "";
    private DeptCode src_dept;//源部门
    private DeptCode dst_dept;//目标部门
    private DefaultMutableTreeNode dst_node;
    private SysParameter deptGrade = DeptUtil.getDeptGrade();
    private boolean error = false;
    private List save_objs = new ArrayList();
    private List<IPickWindowCloseListener> listeners = new ArrayList<IPickWindowCloseListener>();
    private boolean flag = true;
    private DefaultMutableTreeNode src_node;
    private DeptChgLog dcl = null;
    private Hashtable<String, String> save_codes = new Hashtable<String, String>();

    public void addPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.add(listener);
    }

    public void delPickWindowCloseListener(IPickWindowCloseListener listener) {
        listeners.remove(listener);
    }

    public DeptTransUnitPanel() {
        initComponents();
        initSrcDept();
        if (type.equals("unit")) {
            this.jLabel1.setText("被合并部门");
        }
        setupEvents();
    }

    /** Creates new form DeptTransUnitPanel */
    public DeptTransUnitPanel(String type, DefaultMutableTreeNode src_node) {
        this.type = type;
        this.src_node = src_node;
        initComponents();
        initSrcDept();
        if (type.equals("unit")) {
            this.jLabel1.setText("被合并部门");
        }
        setupEvents();
    }

    private void setupEvents() {
        btnDept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DeptSelectDlg rdDlg = new DeptSelectDlg(UserContext.getDepts(false), dst_dept, type.equals("unit") ? TreeSelectMod.leafSelectMod : TreeSelectMod.nodeSelectMod);
                ContextManager.locateOnScreenCenter(rdDlg);
                rdDlg.setVisible(true);
                if (rdDlg.isClick_ok()) {
                    flag = true;
                    dst_dept = rdDlg.getCurDept();
                    jtfDst.setText(dst_dept.getContent() + "{" + dst_dept.getDept_code() + "}");
                    previewDeptTree(type);
                }
            }
        });
        CloseAction.doCloseAction(btnCancel);
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                changeDept(type);
                ModelFrame.close();
            }
        });
    }

    private void changeDept(String type) {
        if (dst_dept == null) {
//            JOptionPane.showMessageDialog(null, "未选择目标部门", "错误", JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(DeptMngMsg.msgNotSelectDepart);
            return;
        }
        if (!type.equals("trans") && (src_dept.getDept_code().equals(dst_dept.getDept_code()) || src_dept.getParent_code().equals(dst_dept.getDept_code()))) {
            jc.setText(DeptMngMsg.msgInvalidChange.toString());
            return;
        }
        if (type.equals("trans") && !flag) {
//            JOptionPane.showMessageDialog(null, "无效变更");
            MsgUtil.showInfoMsg(DeptMngMsg.msgInvalidChange);
            flag = true;
            return;
        }
        if (error) {
//            JOptionPane.showMessageDialog(null, "无法分配部门编码");
            MsgUtil.showInfoMsg(DeptMngMsg.msgUnableToallocate);
            return;
        }
        if ("trans".equals(type)) {
            DefaultMutableTreeNode parent_node = dst_node;
            DeptCode parent_dept = (DeptCode) dst_node.getUserObject();
            Enumeration enumt = dst_node.breadthFirstEnumeration();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                if (node == dst_node || !(node.getUserObject() instanceof DeptCode)) {
                    continue;
                }
                DeptCode dept = (DeptCode) node.getUserObject();
                if (!dept.getParent_code().equals(parent_dept.getDept_code())) {
                    parent_node = (DefaultMutableTreeNode) node.getParent();
                }
                parent_dept = (DeptCode) parent_node.getUserObject();
                dept.setDept_full_name(parent_dept.getDept_full_name() + "\\" + dept.getContent());
                if (dept.getDept_full_name().startsWith("\\")) {
                    dept.setDept_full_name(dept.getDept_full_name().substring(1));
                }
            }
        }
        dcl.setChg_ip(UserContext.getPerson_ip());
        dcl.setChg_mac(UserContext.getPerson_mac());
        if (type.equals("unit")) {
            dcl.setChg_type("变更");
            dcl.setChg_caption("合并");
            dcl.setChg_name("del_flag");
        } else if (type.equals("trans")) {
            dcl.setChg_type("变更");
            dcl.setChg_caption("转移");
            dcl.setChg_name("dept_code");
        }
        dcl.setChg_user(UserContext.person_code);
        ValidateSQLResult validateSQLResult = null;
        if (type.equals("trans")) {
            validateSQLResult = DeptImpl.transDept(save_objs, src_code, dst_dept.getDept_code(), dcl, save_codes);
        } else {
            validateSQLResult = DeptImpl.unitDept(save_objs, dcl);
        }
        if (validateSQLResult.getResult() == 0) {
            for (IPickWindowCloseListener listener : listeners) {
                listener.pickClose();
            }
        } else {
            MsgUtil.showHRSaveErrorMsg(validateSQLResult);
        }
    }

    private void previewDeptTree(String type) {
        save_codes.clear();
        Hashtable<String, Integer> code_table = new Hashtable<String, Integer>();
        Hashtable<String, Boolean> code_flag = new Hashtable<String, Boolean>();
        dcl = (DeptChgLog) UtilTool.createUIDEntity(DeptChgLog.class);
        initSrcDept();
        if (src_dept.getDept_code().equals(dst_dept.getDept_code()) || src_dept.getParent_code().equals(dst_dept.getDept_code())) {
            jc.setText(DeptMngMsg.msgInvalidChange.toString());
            flag = false;
            return;
        }
        dcl.setChg_before(src_dept.getContent() + "{" + src_dept.getDept_code() + "}");
        save_objs.clear();
        save_objs.add(src_dept);
        save_objs.add(dst_dept);
        pnlView.removeAll();
        pnlView.setLayout(new BorderLayout());
        List<DeptCode> depts = new ArrayList<DeptCode>();
        for (DeptCode dc : UserContext.getDepts(false)) {
            DeptCode dept = new DeptCode();
            dept.setContent(dc.getContent());
            dept.setDeptCode_key(dc.getDeptCode_key());
            dept.setDept_code(dc.getDept_code());
            dept.setParent_code(dc.getParent_code());
            dept.setEnd_flag(dc.isEnd_flag());
            dept.setVirtual(dc.isVirtual());
            dept.setShow_code_flag(dc.isShow_code_flag());
            dept.setDept_full_name(dc.getDept_full_name());
            dept.setPrechar(dc.getPrechar());
            dept.setPx_code(dc.getPx_code());
            depts.add(dept);
        }
        DeptPanel deptPanel = new DeptPanel(depts);
        JTree tree = deptPanel.getDeptTree();
        DefaultMutableTreeNode selectNode = null;
        if (type.equals("trans")) {//改变源部门的部门代码
            if (dst_dept.isEnd_flag() && CommUtil.exists("select 1 from A01 a01 where a01.deptCode.deptCode_key='" + dst_dept.getDeptCode_key() + "'")) {
//                JOptionPane.showMessageDialog(null, "目标部门为已有人员的末级部门，不允许转移", "错误", JOptionPane.ERROR_MESSAGE);
                MsgUtil.showErrorMsg(DeptMngMsg.msgTargetSector);
                return;
            }
            dst_node = deptPanel.getNodeByDept(dst_dept);
            String new_dcode = SysUtil.getNewChildDeptCode1Of(dst_dept.getDept_code(), dst_node.getLevel(), deptGrade, src_dept.getDept_code());
            if (new_dcode == null) {
                error = true;
                pnlView.updateUI();
                return;
            }
            pnlView.add(deptPanel, BorderLayout.CENTER);
            //改变源部门的位置
            DefaultMutableTreeNode src_node1 = deptPanel.getNodeByDept(src_dept);
            TreeNode parent = src_node1.getParent();
            if (parent.getChildCount() <= 1) {
                ((DeptCode) ((DefaultMutableTreeNode) parent).getUserObject()).setEnd_flag(true);
                save_objs.add((DeptCode) ((DefaultMutableTreeNode) parent).getUserObject());
            }
            Enumeration enumt = src_node1.breadthFirstEnumeration();
            int srcParentLevel = ((DefaultMutableTreeNode) parent).getLevel();
            int deptLevel = 1;
            int dstParentLevel = dst_node.getLevel();
            Hashtable<Integer, List<DefaultMutableTreeNode>> dept_keys = new Hashtable<Integer, List<DefaultMutableTreeNode>>();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                List<DefaultMutableTreeNode> nodes = dept_keys.get(node.getLevel() - srcParentLevel);
                if (nodes == null) {
                    nodes = new ArrayList<DefaultMutableTreeNode>();
                    dept_keys.put(node.getLevel() - srcParentLevel, nodes);
                }
                nodes.add(node);
                if (node.getLevel() <= (deptLevel + srcParentLevel)) {
                    continue;
                }
                deptLevel = node.getLevel() - srcParentLevel;
//                int src_len = DeptUtil.getSingleDeptCodeLength(srcParentLevel + deptLevel);
//                if (DeptUtil.getSingleDeptCodeLength(dstParentLevel + deptLevel) < src_len) {
//                    String dept_code = (String) CommUtil.fetchEntityBy("select max(dept_code) from DeptCode where dept_code like '" + ((DeptCode) node.getUserObject()).getParent_code() + "%'");
//                    int parent_len = ((DeptCode) node.getUserObject()).getParent_code().length();
//                    int max_code = Integer.valueOf(dept_code.substring(parent_len, parent_len + src_len));
//                    if (("" + max_code).length() > DeptUtil.getSingleDeptCodeLength(dstParentLevel + deptLevel)) {
//                        JOptionPane.showMessageDialog(null, "无法生成编码,不允许转移", "错误", JOptionPane.ERROR_MESSAGE);
//                        HrLog.error(this.getClass(), "dept_code:"+dept_code+";p_node:"+ ((DeptCode) node.getUserObject()).getParent_code());
//                        HrLog.error(this.getClass(), "max_code:"+max_code+";p_node:"+ DeptUtil.getSingleDeptCodeLength(dstParentLevel + deptLevel));
//                        error = true;
//                        return;
//                    }
//                }
            }
            src_node1.removeFromParent();
            dst_dept = (DeptCode) dst_node.getUserObject();
            dst_node.add(src_node1);
            dst_dept.setEnd_flag(false);
            dst_node.setUserObject(dst_dept);
            if (save_codes.get(src_dept.getDept_code()) != null) {
                save_codes.remove(src_dept.getDept_code());
            }
            save_codes.put(src_dept.getDept_code(), new_dcode);
            src_dept.setDept_code(new_dcode);
            src_dept.setGrade(src_node1.getLevel());
            src_dept.setParent_code(dst_dept.getDept_code());
            src_node1.setUserObject(src_dept);
            int newLevel = 1;
            while (newLevel < deptLevel) {
                newLevel++;
                List<DefaultMutableTreeNode> nodes = dept_keys.get(newLevel);
                int old_len = DeptUtil.getSingleDeptCodeLength(srcParentLevel + newLevel);
                int new_len = DeptUtil.getSingleDeptCodeLength(dstParentLevel + newLevel);
                String addChar = "";
                int temp = old_len;
                while (temp < new_len) {
                    addChar = "0" + addChar;
                    temp++;
                }
                int sub_len = old_len <= new_len ? old_len : new_len;
                for (DefaultMutableTreeNode node : nodes) {
                    DeptCode dc = (DeptCode) node.getUserObject();
                    String p_code = ((DeptCode) ((DefaultMutableTreeNode) node.getParent()).getUserObject()).getDept_code();
                    if (old_len <= new_len) {
                        String code = dc.getDept_code().substring(dc.getDept_code().length() - sub_len);
                        String n_code = ((DeptCode) ((DefaultMutableTreeNode) node.getParent()).getUserObject()).getDept_code() + addChar + code;
                        if (save_codes.get(dc.getDept_code()) != null) {
                            save_codes.remove(dc.getDept_code());
                        }
                        save_codes.put(dc.getDept_code(), n_code);
                        dc.setDept_code(n_code);
                    } else {
                        String code = dc.getDept_code().substring(dc.getDept_code().length() - old_len);
                        int cub = old_len - new_len;
//                        String cub_str = code.substring(0, cub);
                        boolean f = false;
                        if (code_flag.get(p_code) == null) {
                            f = getBigFlag(node, cub, old_len);
                            code_flag.put(p_code, f);
                        } else {
                            f = code_flag.get(p_code);
                        }
                        if (f) {
                            int c = 1;
                            for (int i = 0; i < new_len; i++) {
                                c = 10 * c;
                            }
                            if (((DefaultMutableTreeNode) node.getParent()).getChildCount() >= c) {
                                int t2 = dstParentLevel + deptLevel + newLevel - 2;
//                                JOptionPane.showMessageDialog(null, "部门级次第" + t2 + "级太小");
                                MsgUtil.showInfoMsg(DeptMngMsg.msgDeptLeavelSmall);
                                return;
                            } else {
                                int s = 1;
                                if (code_table.get(p_code) != null) {
                                    s = code_table.get(p_code);
                                    s++;
                                }
                                String new_code = getNewCode(s, new_len);
                                code_table.remove(p_code);
                                code_table.put(p_code, s);
                                if (save_codes.get(dc.getDept_code()) != null) {
                                    save_codes.remove(dc.getDept_code());
                                }
                                save_codes.put(dc.getDept_code(), p_code + new_code);
                                dc.setDept_code(p_code + new_code);
                            }
                        } else {
                            String n_code = ((DeptCode) ((DefaultMutableTreeNode) node.getParent()).getUserObject()).getDept_code() + code.substring(cub);
                            if (save_codes.get(dc.getDept_code()) != null) {
                                save_codes.remove(dc.getDept_code());
                            }
                            save_codes.put(dc.getDept_code(), n_code);
                            dc.setDept_code(n_code);
                        }
                    }
                    dc.setParent_code(((DeptCode) ((DefaultMutableTreeNode) node.getParent()).getUserObject()).getDept_code());
                    dc.setGrade(((DeptCode) ((DefaultMutableTreeNode) node.getParent()).getUserObject()).getGrade() + 1);
                    save_objs.add(dc);
                }
            }
            selectNode = dst_node;
        } else {
            if (!dst_dept.isEnd_flag()) {
//                JOptionPane.showMessageDialog(null, "目标部门必须为末级部门", "错误", JOptionPane.ERROR_MESSAGE);
                MsgUtil.showErrorMsg(DeptMngMsg.msgTargetisFinal);
                return;
            }
            pnlView.add(deptPanel, BorderLayout.CENTER);
            DefaultMutableTreeNode src_node1 = deptPanel.getNodeByDept(src_dept);
            src_dept.setDel_flag(true);
            src_node1.setUserObject(src_dept);
            dst_node = deptPanel.getNodeByDept(dst_dept);
            selectNode = dst_node;
        }
        if (type.equals("trans")) {
            dcl.setChg_after(src_dept.getContent() + "{" + src_dept.getDept_code() + "}");
        }
        tree.setSelectionPath(new TreePath(selectNode.getPath()));
        tree.expandPath(new TreePath(selectNode.getPath()));
        tree.updateUI();
        pnlView.updateUI();

    }

    private Boolean getBigFlag(DefaultMutableTreeNode node, int cub, int old_len) {
        boolean exist_flag = false;
        DefaultMutableTreeNode p_node = (DefaultMutableTreeNode) node.getParent();
        Enumeration enumt = p_node.children();
        while (enumt.hasMoreElements()) {
            DeptCode dc = (DeptCode) ((DefaultMutableTreeNode) enumt.nextElement()).getUserObject();
            String code = dc.getDept_code().substring(dc.getDept_code().length() - old_len);
            String cub_str = code.substring(0, cub);
            if (Integer.valueOf(cub_str) > 0) {
                exist_flag = true;
                break;
            }
        }
        return exist_flag;
    }

    private String getNewCode(int s, int len) {
        String r = "" + String.valueOf(s);
        while (r.length() < len) {
            r = "0" + r;
        }
        return r;
    }

    private void initSrcDept() {
        ComponentUtil.setSysFuntion(this, module_code);
        this.src_dept = new DeptCode();
        DeptCode dc = (DeptCode) src_node.getUserObject();
        List<String> f_fields = EntityBuilder.getCommFieldNameListOf(DeptCode.class, EntityBuilder.COMM_FIELD_ALL);
        f_fields.remove("fun_flag");
        f_fields.remove("selected_flag");
        f_fields.remove("new_flag");
        f_fields.remove("show_code_flag");
        PublicUtil.copyProperties(dc, src_dept, f_fields, f_fields);
        src_dept.setEnd_flag(dc.isEnd_flag());
        src_dept.setVirtual(dc.isVirtual());
        src_dept.setDel_flag(dc.isDel_flag());
        jtfSrc.setText(src_dept.getContent() + "{" + src_dept.getDept_code() + "}");
        src_code = src_dept.getDept_code();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtfDst = new javax.swing.JTextField();
        jtfSrc = new javax.swing.JTextField();
        btnDept = new javax.swing.JButton();
        pnlView = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jc = new java.awt.Label();

        jLabel1.setText("被转移部门：");

        jLabel2.setText("接收部门：");

        jtfDst.setEditable(false);

        jtfSrc.setEditable(false);

        btnDept.setText("...");

        pnlView.setBorder(javax.swing.BorderFactory.createTitledBorder("变更后部门结构预览："));
        pnlView.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(27, 27, 27)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jtfDst)
                    .addComponent(jtfSrc, javax.swing.GroupLayout.DEFAULT_SIZE, 135, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDept, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(301, Short.MAX_VALUE))
            .addComponent(pnlView, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jtfSrc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfDst, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDept, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(pnlView, javax.swing.GroupLayout.DEFAULT_SIZE, 316, Short.MAX_VALUE))
        );

        btnOk.setText("确定");

        btnCancel.setText("取消");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 567, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 386, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnCancel)
                .addGap(43, 43, 43))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnCancel)
                        .addComponent(btnOk))
                    .addComponent(jc, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jc.getAccessibleContext().setAccessibleName("");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnDept;
    private javax.swing.JButton btnOk;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSeparator jSeparator1;
    private java.awt.Label jc;
    private javax.swing.JTextField jtfDst;
    private javax.swing.JTextField jtfSrc;
    private javax.swing.JPanel pnlView;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
