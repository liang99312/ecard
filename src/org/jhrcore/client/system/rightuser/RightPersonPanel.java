/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RightPersonPanel.java
 *
 * Created on 2011-7-24, 17:11:42
 */
package org.jhrcore.client.system.rightuser;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import org.jhrcore.client.CommUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.RightImpl;
import org.jhrcore.mutil.RightUtil;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class RightPersonPanel extends javax.swing.JPanel implements IModulePanel {

    public static final String module_code = "SysUser";
    private IPickRightPersonListener cur_listener = null;
    private List roles;
    private JButton btnAddUser = new JButton("新增用户");
    private JButton btnDelUser = new JButton("删除用户");
    private JButton btnRole = new JButton("角色分配");
    private JButton btnSearch = new JButton("查询");
    private JButton btnRefresh = new JButton("刷新");
    private JButton btnSetPass = new JButton("重新设置用户密码");
    private JButton btnCrypt = new JButton("密码加密");
    private JButton btnFinger = new JButton("指纹采集");
    private JLabel lbl = new JLabel("查找");
    private JTextField jtfSearch = new JTextField();
    private HrLog log = new HrLog(module_code);

    /**
     * Creates new form RightPersonPanel
     */
    public RightPersonPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        initToolBar();
        roles = RightUtil.getUserRoles();
        jtpMain.add("角色对应人员", new RightRolePanel());
        jtpMain.add("人员对应角色", new RightUserPanel());
    }

    private void setupEvents() {
        jtpMain.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                setFunctionRight();
                cur_listener = (IPickRightPersonListener) jtpMain.getSelectedComponent();
                cur_listener.fetchData(roles, "", false);
            }
        });
        btnDelUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cur_listener.delUser();
            }
        });
        btnAddUser.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cur_listener.addUser();
            }
        });
        ActionListener al_query = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = SysUtil.getQuickSearchText(jtfSearch.getText());
                cur_listener.fetchData(roles, text, true);
            }
        };
        jtfSearch.addActionListener(al_query);
        btnSearch.addActionListener(al_query);
        btnRefresh.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cur_listener.fetchData(roles, "", true);
            }
        });
        btnRole.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                cur_listener.defineRight(roles);
            }
        });
        btnSetPass.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                cur_listener.setPass();
            }
        });
        btnCrypt.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                crypt();
            }
        });
        btnFinger.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                cur_listener.registerFinger();
            }
        });
        cur_listener = (IPickRightPersonListener) jtpMain.getSelectedComponent();
        cur_listener.fetchData(roles, "", true);
    }

    private void initToolBar() {
        toolbar.add(btnAddUser);
        toolbar.add(btnDelUser);
        toolbar.addSeparator();
        toolbar.add(btnRole);
        if (UserContext.isSA) {
            toolbar.add(btnSetPass);
            toolbar.add(btnCrypt);
        }
        toolbar.add(btnFinger);
        toolbar.addSeparator();
        toolbar.add(lbl);
        toolbar.add(jtfSearch);
        toolbar.add(btnSearch);
        toolbar.add(btnRefresh);
        ComponentUtil.setSize(jtfSearch, 120, 22);
    }

    private void crypt() {
        if (CommUtil.exists("from SysParameter s where s.sysParameter_key='cryptA01PassWord'")) {
            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnCrypt), "用户密码已加密");
            return;
        }
        String msg = "确定要将所有用户的密码进行加密？";
        if (JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(btnCrypt), msg, "询问", JOptionPane.YES_NO_OPTION) != 0) {
            return;
        }
        ValidateSQLResult result = RightImpl.cryptA01PassWord();
        if (result.getResult() == 0) {
            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnCrypt), "加密成功");
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        toolbar = new javax.swing.JToolBar();
        jtpMain = new javax.swing.JTabbedPane();

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setFunctionRight() {
        ComponentUtil.setSysFuntion(this, module_code);
        btnRole.setEnabled(jtpMain.getSelectedIndex() == 1 && UserContext.hasFunctionRight(module_code + ".btnRole"));
        btnDelUser.setEnabled(jtpMain.getSelectedIndex() == 0 && UserContext.hasFunctionRight(module_code + ".btnDelUser"));
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
