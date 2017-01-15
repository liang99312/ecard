/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * DeptPxCodePanel.java
 *
 * Created on 2013-1-28, 10:40:34
 */
package org.jhrcore.client.dept;

import com.foundercy.pf.control.table.FTable;
import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragGestureEvent;
import java.awt.dnd.DragGestureListener;
import java.awt.dnd.DragSource;
import java.awt.dnd.DropTarget;
import java.awt.dnd.DropTargetAdapter;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jhrcore.comm.BeanManager;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.DeptImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.dept.DeptMngMsg;
import org.jhrcore.mutil.DeptUtil;
import org.jhrcore.client.system.comm.DragAndDropDragSourceListener;
import org.jhrcore.client.system.comm.DragAndDropTransferable;
import org.jhrcore.ui.DeptPanel;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class DeptPxCodePanel extends javax.swing.JPanel implements IModuleCode{

    private DeptPanel deptPanel;
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("待排序部门");
    private DefaultMutableTreeNode curNode;
    private JTree tmpTree;
    private JMenuItem miMoveUp = new JMenuItem("上移");
    private JMenuItem miMoveDown = new JMenuItem("下移");
    private JMenuItem miMoveFirst = new JMenuItem("最前");
    private JMenuItem miMoveLast = new JMenuItem("最后");
    private JMenuItem miSaveSort = new JMenuItem("保存排序");
    private JMenu mRecovery = new JMenu("还原默认排序");
    private JMenuItem miRecoveryCurNode = new JMenuItem("还原当前部门直接下级排序");
    private JMenuItem miRecoveryCurAllNode = new JMenuItem("还原当前部门所有下级排序");
    private JMenuItem miRecoveryAll = new JMenuItem("还原所有部门排序");
    private FTable ftable;
    private HashMap<String, String> pxCodeKeys = new HashMap();
    private String module_code = "DeptMng.miChangePxCode";
    private DeptCode curDept = null;

    /** Creates new form DeptPxCodePanel */
    public DeptPxCodePanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        ComponentUtil.setSysFuntion(this, module_code);
        deptPanel = new DeptPanel(UserContext.getDepts(false));
        pnlLeft.add(deptPanel);
        tmpTree = new JTree(rootNode);
        HRRendererView.getDeptMap(tmpTree).initTree(tmpTree);
        pnlTreeView.add(new JScrollPane(tmpTree));
        DragSource dragSource = DragSource.getDefaultDragSource(); // 创建拖拽源
        dragSource.createDefaultDragGestureRecognizer(tmpTree,
                DnDConstants.ACTION_COPY_OR_MOVE,
                new DragAndDropDragGestureListener()); // 建立拖拽源和事件的联系
        DropTarget dropTarget = new DropTarget(tmpTree,
                new DragAndDropDropTargetListener());
        List<String> fields = Arrays.asList(new String[]{"dept_code", "content", "px_code"});
        List<String> disFields = Arrays.asList(new String[]{"dept_code", "content"});
        ftable = new FTable(DeptCode.class, fields, false, false, false, module_code);
        ftable.setDisable_fields(disFields);
        ftable.setEditable(true);
        pnlTableView.add(ftable);
    }

    private void setupEvents() {
        tmpTree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == e.BUTTON3) {
                    createPop(e.getPoint());
                }
            }
        });
        deptPanel.getDeptTree().addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                if (e.getPath() == null || e.getPath().getLastPathComponent() == null) {
                    return;
                }
                curNode = (DefaultMutableTreeNode) e.getPath().getLastPathComponent();
                refreshMainUI(curNode);
            }
        });
        jtpMain.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                refreshMainUI(curNode);
            }
        });
        ActionListener alUp = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                moveNode(-1, false);
            }
        };
        btnMoveUp.addActionListener(alUp);
        miMoveUp.addActionListener(alUp);
        ActionListener alFirst = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                moveNode(-1, true);
            }
        };
        btnMoveFirst.addActionListener(alFirst);
        miMoveFirst.addActionListener(alFirst);
        ActionListener alDown = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                moveNode(1, false);
            }
        };
        btnMoveDown.addActionListener(alDown);
        miMoveDown.addActionListener(alDown);
        ActionListener alLast = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                moveNode(1, true);
            }
        };
        btnMoveLast.addActionListener(alLast);
        miMoveLast.addActionListener(alLast);
        ActionListener alSave = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveSort();
            }
        };
        btnSaveSort.addActionListener(alSave);
        miSaveSort.addActionListener(alSave);
        btnRecovery.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                JPopupMenu mm = new JPopupMenu();
                mm.add(miRecoveryCurNode);
                mm.add(miRecoveryCurAllNode);
                mm.add(miRecoveryAll);
                mm.show(btnRecovery, 0, 25);
            }
        });
        miRecoveryAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                recoverySort("all");
            }
        });
        miRecoveryCurNode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                recoverySort("cur");
            }
        });
        miRecoveryCurAllNode.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                recoverySort("curAll");
            }
        });
        btnClose.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ModelFrame.close();
            }
        });
        ftable.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                changePxCode();
                if (curDept == ftable.getCurrentRow()) {
                    return;
                }
                curDept = (DeptCode) ftable.getCurrentRow();
            }
        });
        ComponentUtil.setSysFuntion(this, module_code);
        miSaveSort.setEnabled(btnSaveSort.isEnabled());
        miMoveUp.setEnabled(btnMoveUp.isEnabled());
        miMoveDown.setEnabled(btnMoveDown.isEnabled());
        miMoveLast.setEnabled(btnMoveLast.isEnabled());
        miMoveFirst.setEnabled(btnMoveFirst.isEnabled());
        mRecovery.setEnabled(btnRecovery.isEnabled());
    }

    private void createPop(Point p) {
        JPopupMenu pp = new JPopupMenu();
        pp.add(miMoveUp);
        pp.add(miMoveDown);
        pp.addSeparator();
        pp.add(miMoveFirst);
        pp.add(miMoveLast);
        pp.addSeparator();
        pp.add(miSaveSort);
        pp.add(mRecovery);
        mRecovery.add(miRecoveryCurNode);
        mRecovery.add(miRecoveryCurAllNode);
        mRecovery.add(miRecoveryAll);
        pp.show(tmpTree, p.x, p.y);
    }

    private void refreshMainUI(DefaultMutableTreeNode node) {
        if (node == null) {
            return;
        }
        DeptCode dc = (DeptCode) node.getUserObject();
        List list = CommUtil.fetchEntities("from DeptCode d where d.del_flag=0 and d.parent_code='" + dc.getDept_code() + "' and (" + UserContext.getDept_right_rea_str("d") + ") order by d.px_code");
        if (jtpMain.getSelectedIndex() == 0) {
            refreshTmpTree(list);
        } else {
            refreshTmpTable(list);
        }
    }

    private void refreshTmpTable(List data) {
        ftable.deleteAllRows();
        List list = new ArrayList();
        for (Object obj : data) {
            DeptCode dc = (DeptCode) obj;
            list.add(dc);
            pxCodeKeys.put(dc.getDeptCode_key(), dc.getPx_code());
        }
        ftable.setObjects(list);
    }

    private void refreshTmpTree(List data) {
        rootNode.removeAllChildren();
        for (Object obj : data) {
            rootNode.add(new DefaultMutableTreeNode(obj));
        }
        ComponentUtil.initTreeSelection(tmpTree);
    }

    private void moveNode(int step, boolean all) {
        DefaultMutableTreeNode node = (DefaultMutableTreeNode) tmpTree.getLastSelectedPathComponent();
        if (node == null) {
            return;
        }
        if (rootNode.getChildCount() <= 1) {
            return;
        }
        if (all) {
            node.removeFromParent();
            if (step == 1) {
                rootNode.add(node);
            } else {
                rootNode.insert(node, 0);
            }
        } else {
            DefaultMutableTreeNode tmpNode;
            if (step == 1) {
                tmpNode = node.getNextSibling();
            } else {
                tmpNode = node.getPreviousSibling();
            }
            if (tmpNode == null) {
                return;
            }
            int ind = rootNode.getIndex(tmpNode);
            node.removeFromParent();
            rootNode.insert(node, ind);
        }
        tmpTree.updateUI();
    }

    private boolean changePxCode() {
        if (!BeanManager.isChanged(curDept)) {
            return false;
        }
        String newPxCode = curDept.getPx_code();
        String msg = DeptUtil.validateDeptPxCode(curDept);
        if (!msg.equals("")) {
//            JOptionPane.showMessageDialog(null, msg);
            MsgUtil.showInfoMsg(msg);
            curDept.setPx_code(pxCodeKeys.get(curDept.getDeptCode_key()));
            int ind = ftable.getObjects().indexOf(curDept);
            ftable.setRowSelectionInterval(ind, ind);
            ftable.updateUI();
            return false;
        }
        String prePx = newPxCode.substring(0, curDept.getParent_code().length());
        int parentLen = prePx.length();
        int levelLen = newPxCode.length() - parentLen;
        int i = 1;
        List list = ftable.getObjects();
        int ind = SysUtil.objToInt(newPxCode.substring(parentLen));
        list.remove(curDept);
        for (Object obj : list) {
            DeptCode dc = (DeptCode) obj;
            if (ind == i) {
                i++;
            }
            dc.setPx_code(prePx + SysUtil.getNewCode(i, levelLen));
            i++;
        }
        if (ind >= list.size()) {
            ind = list.size();
        }
        if (ind > 0) {
            ind--;
        }
        list.add(curDept);
        SysUtil.sortListByStr(list, "px_code");
        ftable.setObjects(list);
        ftable.setRowSelectionInterval(ind, ind);
        ftable.updateUI();
        return true;
    }

    private void saveSort() {
        List<String[]> childdepts = new ArrayList();
        Object firstObj = curNode.getUserObject();
        String parent_code = ((DeptCode) firstObj).getDept_code();
        if (jtpMain.getSelectedIndex() == 0) {
            Enumeration enumt = rootNode.children();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                childdepts.add(new String[]{((DeptCode) node.getUserObject()).getDept_code(), ""});
            }
        } else {
            ftable.editingStopped();
            if (changePxCode()) {
                for (Object obj : ftable.getObjects()) {
                    DeptCode dc = (DeptCode) obj;
                    childdepts.add(new String[]{dc.getDept_code(), dc.getPx_code()});
                }
            }
        }
        if (childdepts.isEmpty()) {
            return;
        }
        ValidateSQLResult result = DeptImpl.saveDeptSort(parent_code, childdepts);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    private void recoverySort(String method) {
        String parent_code = "";
        if (!method.equals("all")) {
            if (rootNode.getChildCount() == 0) {
                return;
            }
            Object firstObj = ((DefaultMutableTreeNode) rootNode.getFirstChild()).getUserObject();
            parent_code = ((DeptCode) firstObj).getParent_code();
        }
        ValidateSQLResult result = DeptImpl.recoveryDeptSort(parent_code, method);
        if (result.getResult() == 0) {
            MsgUtil.showInfoMsg(DeptMngMsg.msgReductionSortSucce);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }
    private DefaultMutableTreeNode the_node_to_move;

    @Override
    public String getModuleCode() {
        return module_code;
    }

    class DragAndDropDragGestureListener implements DragGestureListener {

        @Override
        public void dragGestureRecognized(DragGestureEvent dge) {
            // 将数据存储到Transferable中，然后通知组件开始调用startDrag()初始化
            JTree tree = (JTree) dge.getComponent();
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node_to_move = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (!(node_to_move.getUserObject() instanceof DeptCode)) {
                    return;
                }
                the_node_to_move = node_to_move;
                DragAndDropTransferable dragAndDropTransferable = new DragAndDropTransferable(
                        node_to_move);// dept_to_move);

                dge.startDrag(DragSource.DefaultMoveDrop,// .DefaultCopyDrop,
                        dragAndDropTransferable,
                        new DragAndDropDragSourceListener());
            }
        }
    }
//

    class DragAndDropDropTargetListener extends DropTargetAdapter {

        @Override
        public void drop(DropTargetDropEvent event) {
            Transferable tr = event.getTransferable();// 使用该函数从Transferable对象中获取有用的数据
            DefaultMutableTreeNode src_node = null;
            try {
                if (tr.isDataFlavorSupported(DataFlavor.stringFlavor)) {
                    src_node = (DefaultMutableTreeNode) tr.getTransferData(DataFlavor.stringFlavor);
                }
            } catch (Exception ex) {
                ex.printStackTrace();
            }
            DropTarget c = (DropTarget) event.getSource();
            JTree targetTree = (JTree) c.getComponent();

            TreePath pathForLocation = targetTree.getPathForLocation(event.getLocation().x, event.getLocation().y);
            DefaultMutableTreeNode dst_node = null;
            if (pathForLocation != null) {
                dst_node = (DefaultMutableTreeNode) pathForLocation.getLastPathComponent();
            }
            moveNode(src_node, dst_node);
        }
//

        @SuppressWarnings("unchecked")
        private void moveNode(DefaultMutableTreeNode src_node, DefaultMutableTreeNode dst_node) {
            if (src_node == null || dst_node == null) {
                return;
            }
            if (src_node == dst_node) {
                return;
            }
            if (!(src_node.getUserObject() instanceof DeptCode || dst_node.getUserObject() instanceof DeptCode)) {
                return;
            }
            src_node = the_node_to_move;
            rootNode.insert(the_node_to_move, rootNode.getIndex(dst_node));
            tmpTree.updateUI();
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

        jSplitPane1 = new javax.swing.JSplitPane();
        pnlLeft = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnMoveUp = new javax.swing.JButton();
        btnMoveDown = new javax.swing.JButton();
        btnMoveFirst = new javax.swing.JButton();
        btnMoveLast = new javax.swing.JButton();
        jSeparator2 = new javax.swing.JToolBar.Separator();
        btnSaveSort = new javax.swing.JButton();
        btnRecovery = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        btnClose = new javax.swing.JButton();
        jtpMain = new javax.swing.JTabbedPane();
        pnlTreeView = new javax.swing.JPanel();
        pnlTableView = new javax.swing.JPanel();

        jSplitPane1.setDividerLocation(200);

        pnlLeft.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlLeft);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnMoveUp.setText("上移");
        btnMoveUp.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveUp.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnMoveUp);

        btnMoveDown.setText("下移");
        btnMoveDown.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveDown.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnMoveDown);

        btnMoveFirst.setText("最前");
        btnMoveFirst.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveFirst.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnMoveFirst);

        btnMoveLast.setText("最后");
        btnMoveLast.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnMoveLast.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnMoveLast);
        jToolBar1.add(jSeparator2);

        btnSaveSort.setText("保存排序");
        btnSaveSort.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSaveSort.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSaveSort);

        btnRecovery.setText("还原默认排序");
        btnRecovery.setFocusable(false);
        btnRecovery.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnRecovery.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnRecovery);
        jToolBar1.add(jSeparator1);

        btnClose.setText("退出");
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnClose);

        pnlTreeView.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("树状显示", pnlTreeView);

        pnlTableView.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        pnlTableView.setLayout(new java.awt.BorderLayout());
        jtpMain.addTab("网格显示", pnlTableView);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 349, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 390, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 555, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 423, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnMoveDown;
    private javax.swing.JButton btnMoveFirst;
    private javax.swing.JButton btnMoveLast;
    private javax.swing.JButton btnMoveUp;
    private javax.swing.JButton btnRecovery;
    private javax.swing.JButton btnSaveSort;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JToolBar.Separator jSeparator2;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JPanel pnlLeft;
    private javax.swing.JPanel pnlTableView;
    private javax.swing.JPanel pnlTreeView;
    // End of variables declaration//GEN-END:variables
}
