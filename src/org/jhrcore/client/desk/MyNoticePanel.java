/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MyNoticePanel.java
 *
 * Created on 2013-9-30, 11:34:00
 */
package org.jhrcore.client.desk;

import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JWebBrowser;
import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.index.IndexUtil;
import org.jhrcore.entity.SysNotice;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.task.IWaitWork;

/**
 *
 * @author mxliteboss
 */
public class MyNoticePanel extends JPanel implements IModulePanel, IWaitWork {

    private JListBinding notice_list_binding;
    private SysNotice notice;
    public static String module_code = "MyNotice";
    private ListSelectionListener row_listener;
    private List list_notice = new ArrayList();

    /** Creates new form MyNoticePanel */
    public MyNoticePanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        notice_list_binding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, list_notice, jList1);
        notice_list_binding.bind();
    }

    private void setupEvents() {
        row_listener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                setNotice(jList1.getSelectedValue());
            }
        };
        List list = IndexUtil.getNoticeData(false);
        initForWait(list, list.isEmpty() ? null : list.get(0));
    }

    private void setNotice(Object obj) {
        if (obj == null || notice == obj) {
            return;
        }
        notice = (SysNotice) obj;
        pnlRight.removeAll();
        String url = "";
        if ("超链接".equals(notice.getType()) && notice.isOpen_flag()) {
            url = notice.getUrl();
        } else {
            url = "http://" + CommUtil.getWebServerIp() + ":" + CommUtil.getWebServerPort() + "/webhr/customer/notice/viewNotice.do?sysNotice_key=" + notice.getSysNotice_key();
        }
        navigateURL(url);
    }

    private void navigateURL(final String url) {
        NativeInterface.open();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                JWebBrowser webBrowser = new JWebBrowser();
                webBrowser.navigate(url);
                webBrowser.setBarsVisible(false);
                pnlRight.add(webBrowser, BorderLayout.CENTER);
                pnlRight.updateUI();
            }
        });
        NativeInterface.runEventPump();

    }

//    private void navigateURL(String url) {
//        JWebBrowser webBrowser = new JWebBrowser();
//        webBrowser.setBarsVisible(false);
//        pnlRight.add(webBrowser, BorderLayout.CENTER);
//        webBrowser.navigate(url); // 网址首页
//        NativeInterface.open(); // 在SWT的实现使用母语的Swing框架。
//        NativeInterface.runEventPump();
//    }
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jList1 = new javax.swing.JList();
        pnlRight = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setDividerSize(2);

        jList1.setBorder(javax.swing.BorderFactory.createTitledBorder("公告列表："));
        jScrollPane1.setViewportView(jList1);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 199, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 199, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 397, Short.MAX_VALUE)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 397, Short.MAX_VALUE)
        );

        jSplitPane1.setLeftComponent(jPanel1);

        pnlRight.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setRightComponent(pnlRight);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 399, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JList jList1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JPanel pnlRight;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setFunctionRight() {
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

    private void initData(List data) {
        list_notice.clear();
        for (Object obj : data) {
            list_notice.add(((Object[]) obj)[2]);
        }
    }

    @Override
    public void initForWait(List data, Object row) {
        initData(data);
        jList1.removeListSelectionListener(row_listener);
        notice_list_binding.unbind();
        notice_list_binding.bind();
        if (!data.isEmpty()) {
            jList1.setSelectedIndex(data.indexOf(row));
        }
        jList1.addListSelectionListener(row_listener);
        row_listener.valueChanged(null);
    }
}
