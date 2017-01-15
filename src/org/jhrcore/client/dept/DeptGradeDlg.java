package org.jhrcore.client.dept;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.DbUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.DeptChgLog;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.DeptImpl;
import org.jhrcore.msg.dept.DeptMngMsg;
import org.jhrcore.mutil.DeptUtil;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ShowProcessDlg;
import org.jhrcore.ui.action.CloseAction;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

public class DeptGradeDlg extends JDialog implements IModuleCode {

    private static final long serialVersionUID = 1L;
    private final String module_code = "DeptMng.btnSetGrade";
    private SysParameter deptGrade;
    private JButton btnCancel = new JButton("关闭");
    private JButton btnModify = new JButton("修改");
    private JTextField textField;
    private JList list;
    private JScrollPane scrollPane;
    private ArrayList<IPickWindowCloseListener> listeners = new ArrayList<IPickWindowCloseListener>();

    public void addPickChangeDeptGradeListener(IPickWindowCloseListener listener) {
        listeners.add(listener);
    }

    public void delPickChangeDeptGradeListener(IPickWindowCloseListener listener) {
        listeners.remove(listener);
    }

    public DeptGradeDlg() {
        super((JFrame) null, "部门级次设置");
        setSize(400, 400);
        setModal(true);
        deptGrade = DeptUtil.getDeptGrade();
        initUI();
        setupEvents();
    }

    private void setupEvents() {
        list.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent arg0) {
                String tmp = (String) list.getSelectedValue();
                if (tmp == null) {
                    return;
                }
                textField.setText(tmp.split("=")[1]);
            }
        });
        CloseAction.doCloseAction(btnCancel);
        btnModify.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                if (textField.getText().length() != 1) {
                    return;
                }
                if (textField.getText().charAt(0) <= '0' || textField.getText().charAt(0) > '9') {
                    return;
                }
                String db_type = UserContext.sql_dialect;
                final int i_tmp = list.getSelectedIndex();
                final String new_len = textField.getText().trim();
                final String old_len = deptGrade.getSysparameter_value().substring(i_tmp * 2, i_tmp * 2 + 1);
                if (!new_len.equals(old_len)) {
                    if (Integer.valueOf(new_len) < Integer.valueOf(old_len)) {
                        int left_ind = 0;
                        String[] lens = deptGrade.getSysparameter_value().split(";");
                        for (int i = 0; i < i_tmp; i++) {
                            left_ind = left_ind + Integer.valueOf(lens[i]);
                        }
                        left_ind = left_ind + 1;
                        String selsql = "select " + DbUtil.getNull_strForDB(db_type) + "(max(" + DbUtil.getSubStr(db_type) + "(dept_code," + left_ind + "," + old_len + ")),'@') from Deptcode where grade =" + (i_tmp + 1);
                        List list = CommUtil.selectSQL(selsql);
                        String max_code = list.get(0).toString();
                        if (!max_code.equals("@")) {
                            if ((Integer.valueOf(max_code) + "").length() > Integer.valueOf(new_len)) {
                                MsgUtil.showErrorMsg(DeptMngMsg.msg012);
                                return;
                            }
                        }
                    }
                    DeptGradeDlg.this.setEnabled(false);
                    ShowProcessDlg.startProcess(DeptGradeDlg.this);
                    Runnable run = new Runnable() {

                        @Override
                        public void run() {
                            DeptChgLog dcl = (DeptChgLog) UtilTool.createUIDEntity(DeptChgLog.class);
                            dcl.setChg_user(UserContext.person_code);
                            dcl.setChg_caption(DeptMngMsg.msg016.toString());
                            dcl.setChg_type(DeptMngMsg.msg016.toString());
                            dcl.setChg_ip(UserContext.getPerson_ip());
                            dcl.setChg_mac(UserContext.getPerson_mac());
                            dcl.setChg_date(new Date());
                            ValidateSQLResult validateSQLResult = DeptImpl.setGrade(i_tmp + 1, Integer.valueOf(old_len), Integer.valueOf(new_len), dcl);
                            ShowProcessDlg.endProcess();
                            if (validateSQLResult.getResult() == 0) {
                                MsgUtil.showInfoMsg(DeptMngMsg.msgDeptLevelSetsucc);
                                deptGrade.setSysparameter_value(deptGrade.getSysparameter_value().substring(0, i_tmp * 2) + new_len + deptGrade.getSysparameter_value().substring(i_tmp * 2 + 1));
                                list.setListData(getListArray());
                                for (IPickWindowCloseListener listener : listeners) {
                                    listener.pickClose();
                                }
                            } else {
                                MsgUtil.showHRSaveErrorMsg(validateSQLResult);
                            }
                            DeptGradeDlg.this.setEnabled(true);
                            DeptGradeDlg.this.setVisible(true);
                        }
                    };
                    new Thread(run).start();
                }
            }
        });
    }

    private void initUI() {
        JPanel pnl2 = new JPanel(new BorderLayout());
        JPanel pnl3 = new JPanel(new BorderLayout());
        JTextArea textArea = new JTextArea(
                DeptMngMsg.msg017.toString());
        textArea.setLineWrap(true);
        textArea.setEditable(false);
        textArea.setPreferredSize(new Dimension(100, 100));
        pnl2.add(textArea, BorderLayout.EAST);
        list = new JList(getListArray());
        scrollPane = new JScrollPane(list);
        pnl3.add(scrollPane, BorderLayout.CENTER);
        textField = new JTextField();
        pnl3.add(textField, BorderLayout.SOUTH);
        pnl2.add(pnl3, BorderLayout.CENTER);
        JPanel pnlComm = new JPanel(new FlowLayout());
        pnlComm.add(btnModify);
        pnlComm.add(btnCancel);
        add(pnl2, BorderLayout.CENTER);
        add(pnlComm, BorderLayout.SOUTH);
        ComponentUtil.setSysFuntion(this, module_code);
    }

    private Object[] getListArray() {
        String[] tmp_array = deptGrade.getSysparameter_value().split(";");
        List<String> tmp_list = new ArrayList<String>();
        for (int i = 0; i < 20; i++) {
            if (i < tmp_array.length) {
                tmp_list.add("" + (i + 1) + "级=" + tmp_array[i]);
            } else {
                tmp_list.add("" + (i + 1) + "级=0");
            }
        }
        return tmp_list.toArray();
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
