/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * testPnl.java
 *
 * Created on 2013-4-8, 16:24:15
 */
package org.jhrcore.client.index;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import org.jhrcore.client.CommUtil;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.SysNoticeType;
import org.jhrcore.msg.sys.SysIndexMsg;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author Administrator
 */
public class IndexPnl extends javax.swing.JPanel implements IModuleCode {

    public static final String module_code = "SysIndex";
    private JPopupMenu popupMenu = new JPopupMenu();
    private JMenuItem define = new JMenuItem("界面显示");
    private Hashtable<String, SysNoticeType> indexClasses = new Hashtable();
    private List chartList = new ArrayList();
    private GridLayout layout = new GridLayout();
    private String[] order;
    private List indexTypes = new ArrayList();

    /** Creates new form testPnl */
    public IndexPnl() {
        initComponents();
        initUI();
        setupEvents();
    }

    private void initUI() {
        indexTypes.addAll(getSysNoticeType());
        popupMenu.add(define);
        lblMng.setCursor(new Cursor(Cursor.HAND_CURSOR));
        lblRefresh.setCursor(new Cursor(Cursor.HAND_CURSOR));
        layout.setColumns(3);
        layout.setHgap(8);
        layout.setRows(2);
        layout.setVgap(8);
        pnlMain.setBackground(new Color(238, 238, 238));
        refreshIndexPnl();
    }

    private void setupEvents() {
        lblRefresh.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                refreshIndex();
            }
        });
        lblMng.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                popupMenu.show(lblMng, 0, 25);
            }
        });
        define.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                IndexManPanel pnlIm = new IndexManPanel(indexTypes);
                ModelFrame mf = ModelFrame.showModel(ContextManager.getMainFrame(), pnlIm, true, "模板管理", 400, 450, false);
                mf.addIPickWindowCloseListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        refreshIndexPnl();
                    }
                });
                mf.setVisible(true);
            }
        });
        refreshIndex();
    }

    private void refreshIndex() {
        jpbProcess.setVisible(true);
        lblMsg.setVisible(true);
        jpbProcess.setMinimum(0);
        jpbProcess.setMaximum(chartList.size());
        jpbProcess.setValue(0);
        Runnable run = new Runnable() {

            @Override
            public void run() {
                for (Object obj : chartList) {
                    lblMsg.setText(((IndexInnerPnl) obj).getTitle() + "(" + jpbProcess.getValue() + "/" + chartList.size() + ")");
                    ((IndexInnerPnl) obj).refreshData();
                    jpbProcess.setValue(jpbProcess.getValue() + 1);
                    lblMsg.updateUI();
                    jpbProcess.updateUI();
                }
                jpbProcess.setVisible(false);
                lblMsg.setVisible(false);
            }
        };
        new Thread(run).start();
    }

    private void refreshIndexPnl() {
        pnlMain.removeAll();
        String key = ConfigManager.getConfigManager().getProperty("UI.indexOrder");
        if (key.equals("") || null == key) {
            key = "personchange;wake;notice;mail;msg;";
        }
        order = key.split(";");
        int size = order.length;
        int row = size / 2 + (size % 2 == 0 ? 0 : 1);
        layout.setRows(row);
        pnlMain.setLayout(layout);
        for (int i = 0; i < size; i++) {
            SysNoticeType snt = indexClasses.get(order[i]);
            if (snt == null) {
                continue;
            }
            try {
                Class c = Class.forName(snt.getForm_class());
                JPanel pnl = (JPanel) c.newInstance();
                ((IndexInnerPnl) pnl).setTitle(snt.getContent());
                chartList.add(pnl);
                pnlMain.add(pnl);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        pnlMain.updateUI();
    }

    private List getSysNoticeType() {
        String hql = "from SysNoticeType order by order_no";
        List list = CommUtil.fetchEntities(hql);
        initSysNoticeType(list, "wake", SysIndexMsg.msgprompting, 2, IToolTipPanel.class);
        initSysNoticeType(list, "notice", SysIndexMsg.ttlCompnotice, 3, INoticePanel.class);
        initSysNoticeType(list, "report", SysIndexMsg.ttlReport, 6, IReportPanel.class);
        return list;
    }

    private void initSysNoticeType(List list, String key, Object value, int order, Class cs) {
        SysNoticeType curSnt = null;
        for (Object obj : list) {
            SysNoticeType snt = (SysNoticeType) obj;
            if (snt.getCode().equals(key)) {
                curSnt = snt;
                break;
            }
        }
        if (curSnt == null) {
            curSnt = new SysNoticeType();
            curSnt.setSysNoticeType_key(key);
            curSnt.setCode(key);
            curSnt.setOrder_no(order);
            curSnt.setContent(SysUtil.objToStr(value));
            curSnt.setSys_type(true);
            CommUtil.saveOrUpdate(curSnt);
            list.add(curSnt);
        }
        curSnt.setForm_class(cs.getName());
        indexClasses.put(key, curSnt);
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
        jToolBar1 = new javax.swing.JToolBar();
        lblMng = new javax.swing.JLabel();
        lblRefresh = new javax.swing.JLabel();
        jpbProcess = new javax.swing.JProgressBar();
        lblMsg = new javax.swing.JLabel();
        pnlMain = new javax.swing.JPanel();

        jPanel1.setBackground(new java.awt.Color(238, 238, 238));

        jToolBar1.setBackground(new java.awt.Color(255, 255, 255));
        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        lblMng.setFont(new java.awt.Font("宋体", 0, 13));
        lblMng.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/oper_bank3.gif"))); // NOI18N
        lblMng.setText("管理");
        jToolBar1.add(lblMng);

        lblRefresh.setFont(new java.awt.Font("宋体", 0, 13));
        lblRefresh.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/oper_bank3.gif"))); // NOI18N
        lblRefresh.setText("刷新");
        jToolBar1.add(lblRefresh);

        jpbProcess.setMaximumSize(new java.awt.Dimension(150, 14));
        jToolBar1.add(jpbProcess);

        lblMsg.setMaximumSize(new java.awt.Dimension(200, 15));
        jToolBar1.add(lblMsg);

        pnlMain.setBackground(new java.awt.Color(238, 238, 238));

        javax.swing.GroupLayout pnlMainLayout = new javax.swing.GroupLayout(pnlMain);
        pnlMain.setLayout(pnlMainLayout);
        pnlMainLayout.setHorizontalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        pnlMainLayout.setVerticalGroup(
            pnlMainLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 269, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 400, Short.MAX_VALUE)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel jPanel1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JProgressBar jpbProcess;
    private javax.swing.JLabel lblMng;
    private javax.swing.JLabel lblMsg;
    private javax.swing.JLabel lblRefresh;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
