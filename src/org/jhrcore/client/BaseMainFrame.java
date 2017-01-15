/*                         
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * MainFrame2.java
 *
 * Created on 2009-4-7, 22:37:54
 */
package org.jhrcore.client;

import javax.swing.event.ChangeEvent;
import org.jhrcore.util.TransferAccessory;
import javax.swing.event.TreeSelectionEvent;
import org.jhrcore.util.ComponentUtil;
import java.awt.BorderLayout;
import net.sf.fjreport.statusbar.JStatusBar;
import org.jhrcore.ui.ContextManager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.List;
import javax.imageio.ImageIO;
import javax.imageio.stream.ImageInputStream;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.UIManager;
import javax.swing.event.TreeSelectionListener;
import javax.swing.plaf.DimensionUIResource;
import javax.swing.plaf.InsetsUIResource;
import javax.swing.plaf.PanelUI;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreeCellRenderer;
import org.apache.log4j.Logger;
import org.jdesktop.swingx.JXTaskPane;
import org.jhrcore.client.index.IndexPanelPlugIn;
import org.jhrcore.client.system.LogInfoMngPlugin;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.entity.right.FuntionRight;
import org.jhrcore.entity.right.RoleA01;
import org.jhrcore.client.system.SysCodePlugin;
import org.jhrcore.client.system.LogDataMngPlugin;
import org.jhrcore.client.system.SysRebuildPlugin;
import org.jhrcore.client.system.right.FuntionModifyPlugin;
import org.jhrcore.client.system.right.RightPlugin;
import org.jhrcore.client.system.rightuser.RightPersonPlugin;
import org.jhrcore.client.system.LoginUserMngPlugin;
import org.jhrcore.zui.FreeOutlookPane;
import org.jhrcore.zui.FreeTabbedPane;
import org.jhrcore.zui.FreeUtil;
import java.awt.Font;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.ColorUIResource;
import javax.swing.tree.TreePath;
import org.jhrcore.client.desk.MyNoticePlugin;
import org.jhrcore.client.desk.MyReportPlugin;
import org.jhrcore.client.desk.MyToolTipPlugin;
import org.jhrcore.client.ecard.EcardDataPlugin;
import org.jhrcore.client.ecard.EcardHkPlugin;
import org.jhrcore.client.ecard.EcardLeavePlugin;
import org.jhrcore.client.ecard.EcardMngPlugin;
import org.jhrcore.client.ecard.EcardPosPlugin;
import org.jhrcore.client.ecard.EcardXfPlugin;
import org.jhrcore.client.system.SysNoticePlugIn;
import org.jhrcore.client.system.SysUpdatePlugin;
import org.jhrcore.client.system.autoexcute.AutoExPlugin;
import org.jhrcore.msg.sys.SysIndexMsg;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.FuntionTreeModel;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.property.ClientProperty;
import org.jhrcore.ui.renderer.RenderderMap;
import org.jhrcore.ui.task.CommTask;
import org.jhrcore.uimanager.face.UIFace;
import org.jhrcore.uimanager.lnf.BeautyEyeLNFHelper;
import org.jhrcore.util.ImageUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.zui.PagebackPanel;

/**
 *
 * @author Administrator
 */
public class BaseMainFrame extends javax.swing.JFrame implements IModuleCode {

    protected JToolBar leftToolBar = new JToolBar();
    private FreeOutlookPane outlookPane = new FreeOutlookPane();
    protected JPanel mainPane = new JPanel(new BorderLayout());
    // 当前主窗口显示的面板
    private JPanel cur_Panel = null;
    private static BaseMainFrame baseMainFrame;
    protected Hashtable<String, JXTaskPane> taskPanes = new Hashtable<String, JXTaskPane>();
    private Hashtable<String, JTree> taskTree = new Hashtable<String, JTree>();
    private Hashtable<String, DefaultMutableTreeNode> pathNode = new Hashtable<String, DefaultMutableTreeNode>();
    private JPopupMenu popMenu_right;
    private JMenuItem mi_import_updateable = new JMenuItem("允许导入更新浏览字段");
    private JMenuItem miShowViewModule = new JMenuItem("仅显示权限菜单");
    private JMenuItem mi_save_report = new JMenuItem("保存时是否提示保存成功");
    private JMenuItem mi_refresh = new JMenuItem("刷新缓存");
    private JMenuItem mi_edit_pass = new JMenuItem("修改密码");
    private JMenuItem mi_change_logo = new JMenuItem("设置标题与LOGO");
    private JMenuItem miDefineShowModule = new JMenuItem("自定义显示模块");
    private JMenu mi_select_role = new JMenu("切换角色");
    private JMenu mi_select_language = new JMenu("切换语言");
    private JMenuItem mi_ui_def = new JMenuItem("界面参数设置");
    private JMenuItem miCurRole = null;
    private List<String> showModules = new ArrayList<String>();
    private final String module_code = "Sys";
    private Logger log = Logger.getLogger("主窗口顶部");
    private FreeTabbedPane tab = new FreeTabbedPane();
    private JMenu mSelectFeel = new JMenu("切换皮肤");
    private JLabel btnMyHelp = new JLabel();
    private JLabel btnIndex = new JLabel();
    private JLabel btnHelp = new JLabel();
    private JLabel btnAbout = new JLabel();
    private JLabel btnQuit = new JLabel();
    private JLabel btnMyHelp1 = new JLabel();
    private JLabel btnIndex1 = new JLabel();
    private JLabel btnHelp1 = new JLabel();
    private JLabel btnAbout1 = new JLabel();
    private JLabel btnQuit1 = new JLabel();

    public JPanel getCur_Panel() {
        return cur_Panel;
    }

    public Hashtable<String, JXTaskPane> getTaskPanes() {
        return taskPanes;
    }

    private void addModule(Class plugin_class, String defaultModuleName) {
        Object obj = null;
        try {
            obj = plugin_class.newInstance();
        } catch (Exception e) {
            log.error(e);
            return;
        }
        if (obj != null && obj instanceof CommTask) {
            CommTask ct = (CommTask) obj;
            ct.setDefaultModuleName(defaultModuleName);
            String module_code = ct.getModuleCode();
            if (true || UserContext.hasModuleRight(module_code) && UserContext.hasFunctionRight(module_code) && (showModules.isEmpty() || showModules.contains(module_code))) {
                baseMainFrame.addTaskPlugin(ct);
            }
        }
    }

    public void refreshSession() {
        tab.closeAllTab();
        showModules.clear();
        outlookPane.clearAllBars();
        String modules = ConfigManager.getConfigManager().getProperty("UI.showModules_" + UserContext.role_id);
        if (modules != null && !modules.trim().equals("")) {
            showModules.addAll(Arrays.asList(modules.split(";")));
        }
        UserContext.getUserRight();
        ComponentUtil.setSysFuntionNew(this);
        mi_change_logo.setVisible(UserContext.isSA);
        taskPanes.clear();
        taskTree.clear();
        pathNode.clear();
        addModule(MyNoticePlugin.class, "公司公告");
        addModule(MyToolTipPlugin.class, "我的提醒");
        addModule(MyReportPlugin.class, "常用报表");
        
        //-----------ecard-----------
        addModule(EcardLeavePlugin.class,"假日设置");
        addModule(EcardPosPlugin.class,"Pos机设置");
        addModule(EcardMngPlugin.class,"基本信息");
        addModule(EcardHkPlugin.class,"汇款明细");
        addModule(EcardXfPlugin.class,"消费明细");
        addModule(EcardDataPlugin.class,"数据查询");
//        addModule(EcardHkHzPlugin.class,"汇款汇总");
        
        //----------------查询中心----------------------
        //addModule(QueryMngPlugin.class, "Query");
        //---------------报表中心----------------------
//        addModule(ReportPlugin.class, "ReportMng");
//        addModule(ReportNoPlugin.class, "ReportNo");
//        addModule(ReportUsePlugin.class, "ReportUse"); 

        //----------------系统维护----------------------
        addModule(SysRebuildPlugin.class, "数据重构");
        addModule(SysCodePlugin.class, "编码维护");
        addModule(FuntionModifyPlugin.class, "功能菜单");
        addModule(RightPlugin.class, "角色管理");
        addModule(RightPersonPlugin.class, "用户管理");
        addModule(AutoExPlugin.class, "警戒提示");
        addModule(SysNoticePlugIn.class, "信息发布");
        addModule(LoginUserMngPlugin.class, "在线用户");
        addModule(LogInfoMngPlugin.class, "功能日志");
        addModule(LogDataMngPlugin.class, "数据日志");
        addModule(SysUpdatePlugin.class, "版本更新");       

        if (miCurRole != null) {
            refreshRoleMenu(miCurRole);
        }
        for (String key : taskTree.keySet()) {
            JTree tree = taskTree.get(key);
            DefaultMutableTreeNode rootNode = (DefaultMutableTreeNode) tree.getModel().getRoot();
            tree.expandPath(new TreePath(rootNode.getPath()));
            tree.scrollPathToVisible(new TreePath(rootNode.getPath()));
            tree.updateUI();
        }
        changeLanguage(UserContext.language, false);
        initPara();
        UserContext.initRights();
    }

    public JXTaskPane addTaskPlugin(final CommTask plugin) {
        JXTaskPane tp = taskPanes.get(plugin.getGroupName());
        if (tp == null) {
            tp = new JXTaskPane();
            tp.setLayout(new BorderLayout(0, 0));
            tp.setTitle(plugin.getGroupName());
            taskPanes.put(plugin.getGroupName(), tp);
            outlookPane.addBar(tp); //添加菜单分组
        }

        buildTaskTree(tp, plugin);
        return tp;
    }

    private void buildTaskTree(JXTaskPane tp, final CommTask plugin) {
        JTree tree = taskTree.get(plugin.getGroupName());
        String className = SysUtil.objToStr(plugin.getClassName());
        if (tree == null) {
            DefaultMutableTreeNode root = new DefaultMutableTreeNode(plugin.getGroupName());
            DefaultTreeModel treeModel = new DefaultTreeModel(root);
            tree = new JTree(treeModel);
            tree.setRootVisible(false);
            tree.setShowsRootHandles(true);
            tree.setCellRenderer(new TaskTreeRenderer());
            pathNode.put(plugin.getGroupName(), root);
            if (className.equals("")) {
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(plugin);
                root.add(node);
            } else {
                DefaultMutableTreeNode node;
                DefaultMutableTreeNode temp = root;
                String[] nodes = className.split("@");
                for (int i = 0; i < nodes.length; i++) {
                    String text = nodes[i];
                    node = new DefaultMutableTreeNode(text);
                    temp.add(node);
                    temp = node;
                    String key = plugin.getGroupName();
                    for (int j = 0; j <= i; j++) {
                        key = key + "@" + nodes[j];
                    }
                    pathNode.put(key, node);
                }
                node = new DefaultMutableTreeNode(plugin);
                temp.add(node);
            }
            final JTree tree1 = tree;
            tree1.setCursor(new Cursor(Cursor.HAND_CURSOR));
            tree1.addTreeSelectionListener(new TreeSelectionListener() {

                @Override
                public void valueChanged(TreeSelectionEvent e) {
                    Object obj = ComponentUtil.getSelectObj(tree1);
                    
                    if (!(obj instanceof CommTask)) {
                        return;
                    }
                    modulePlugInAction((CommTask) obj);
//                    if(obj instanceof IndexInnerPnl){
//                        ((IndexInnerPnl)obj).refreshData();
//                        ((IndexInnerPnl)obj).doMore();
//                     }
                }
            });
            tree1.addMouseListener(new MouseAdapter() {

                @Override
                public void mouseClicked(MouseEvent e) {
                    Object obj = ComponentUtil.getSelectObj(tree1);
                    if (!(obj instanceof CommTask)) {
                        DefaultMutableTreeNode node = ComponentUtil.getSelectNode(tree1);
                        if (node == null) {
                            return;
                        }
                        TreePath tp = new TreePath(node.getPath());
                        if (tree1.isCollapsed(tp)) {
                            tree1.expandPath(tp);
                        } else {
                            tree1.collapsePath(tp);
                        }
                        return;
                    }
                    modulePlugInAction((CommTask) obj);
                   
                }
            });
            tp.add(tree1);
            taskTree.put(plugin.getGroupName(), tree1);
        } else {
            if (className.equals("")) {
                DefaultMutableTreeNode temp = pathNode.get(plugin.getGroupName());
                DefaultMutableTreeNode node = new DefaultMutableTreeNode(plugin);
                temp.add(node);
            } else {
                String[] nodes = className.split("@");
                int index = 0;
                DefaultMutableTreeNode temp = null;
                for (int i = nodes.length - 1; i <= 0; i--) {
                    String key = plugin.getGroupName();
                    for (int j = 0; j <= i; j++) {
                        key = key + "@" + nodes[j];
                    }
                    temp = pathNode.get(key);
                    if (temp != null) {
                        index = i;
                        break;
                    }
                }
                if (temp == null) {
                    temp = pathNode.get(plugin.getGroupName());
                }
                DefaultMutableTreeNode node;
                for (int i = index; i < nodes.length; i++) {
                    String key = plugin.getGroupName();
                    for (int j = 0; j <= i; j++) {
                        key = key + "@" + nodes[j];
                    }
                    node = pathNode.get(key);
                    if (node == null) {
                        String text = nodes[i];
                        node = new DefaultMutableTreeNode(text);
                        temp.add(node);
                        pathNode.put(key, node);
                    }
                    temp = node;
                }
                node = new DefaultMutableTreeNode(plugin);
                temp.add(node);
            }
        }
    }

    public void modulePlugInAction(CommTask plugin) {
        String model_key = plugin.getGroupName() + "." + plugin.getClassName() + "." + plugin.toString();
        if (tab.getPanelKey().get(model_key) != null) {
            cur_Panel = (JPanel) tab.getPanelKey().get(model_key);
        } else {
            JPanel mp = plugin.getModulePanel();
            cur_Panel = mp;
            if (cur_Panel instanceof IModulePanel) {
                ComponentUtil.setSysFuntionNew(mp);
                ((IModulePanel) cur_Panel).setFunctionRight();
            }
            tab.addTab(plugin.toString(), model_key, cur_Panel);
        }
        tab.setSelectedComponent(cur_Panel);
        Logger.getLogger(plugin.getGroupName() + "." + plugin.toString()).info("$进入#" + plugin.getGroupName() + "." + plugin.toString() + "模块");
    }

    private void buildRoleMenu() {
        ContextManager.getStatusBar().setContent(0, "当前用户：" + UserContext.person_name);
        if (UserContext.roles.size() > 0) {
            popMenu_right.remove(mi_select_role);
            popMenu_right.add(mi_select_role);
            for (final RoleA01 r : UserContext.roles) {
                final JMenuItem miRole = new JMenuItem(r.getRole().getRole_name());
                miRole.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        miCurRole = miRole;
                        UserContext.role_id = r.getRole().getRole_key();
                        refreshRoleMenu(miRole);
                        BaseMainFrame.this.refreshSession();
                        UserContext.getMemoryDept(true);
                        BaseMainFrame.this.setEnabled(true);
                    }
                });
                mi_select_role.add(miRole);
                if (r.getRole().getRole_key().equals(UserContext.role_id)) {
                    miCurRole = miRole;
                }
            }
        }
    }

    private void buildLanguageMenu() {
        List list = CommUtil.fetchEntities("select p.locale from InternationLang p order by p.orderNum");
        List languages = new ArrayList();
        languages.add(UserContext.language_CN);
        for (Object obj : list) {
            languages.add(obj.toString());
        }
        if (languages.size() > 1) {
            UserContext.languages.addAll(languages);
            String language = ConfigManager.getConfigManager().getProperty("UI.language");
            if (language == null) {
                language = UserContext.language_CN;
            }
            popMenu_right.add(mi_select_language);
            for (final Object obj : languages) {
                final JMenuItem mi = new JMenuItem(obj.toString());
                ComponentUtil.setIcon(mi, "blank");
                mi.addActionListener(new ActionListener() {

                    @Override
                    public void actionPerformed(ActionEvent e) {
                        changeLanguage(obj.toString(), true);
                    }
                });
                mi_select_language.add(mi);
            }
            changeLanguage(language, false);
        }
    }

    private void changeLanguage(String language, boolean refresh) {
        JMenuItem miSelect = null;
        for (int i = 0; i < mi_select_language.getItemCount(); i++) {
            JMenuItem mi = mi_select_language.getItem(i);
            ComponentUtil.setIcon(mi_select_language.getItem(i), "blank");
            if (mi.getText().equals(language)) {
                miSelect = mi;
            }
        }
        ComponentUtil.setIcon(miSelect, "select");
        UserContext.setLanguage(language);
        if (refresh) {
            BaseMainFrame.this.refreshSession();
            BaseMainFrame.this.setEnabled(true);
        }
        ConfigManager.getConfigManager().setProperty("UI.language", language);
        ConfigManager.getConfigManager().save2();
        initBaseMainUI();
    }

    private void initBaseMainUI() {
        btnMyHelp.setText(SysIndexMsg.msgSelfhelp.toString());

        btnIndex.setText(SysIndexMsg.msgIndex.toString());

        btnHelp.setText(SysIndexMsg.msgHelp.toString());

        btnAbout.setText(SysIndexMsg.msgAbout.toString());

        btnQuit.setText(SysIndexMsg.msgCancel.toString());

        labLog.setText(SysIndexMsg.msgIndexLog.toString());
    }

    public void refreshRoleMenu(JMenuItem mi) {
        RoleA01 r = null;
        for (final RoleA01 ra : UserContext.roles) {
            if (ra.getRole().getRole_key().equals(UserContext.role_id)) {
                r = ra;
                break;
            }
        }
        if (r == null) {
            return;
        }
        int size = this.mi_select_role.getItemCount();
        for (int i = 0; i < size; i++) {
            ComponentUtil.setIcon(this.mi_select_role.getItem(i), "blank");
        }
        ComponentUtil.setIcon(mi, "select");
        UserContext.role_id = r.getRole().getRole_key();
        UserContext.cur_role = r.getRole();
        UserContext.rolea01_key = r.getRoleA01_key();
        ConfigManager.getConfigManager().setProperty("User.role_id", UserContext.role_id);
        ConfigManager.getConfigManager().save2();
        ContextManager.getStatusBar().setContent(0, "当前用户：" + UserContext.person_name + " 编号：" + UserContext.person_code + " 角色：" + UserContext.cur_role.getRole_name());
    }

    public JToolBar getLeftToolBar() {
        return leftToolBar;
    }

    public JPanel getUpperPane() {
        return pnlTop;
    }

    public void addPluginToToolbar(final CommTask plugin) {
        JButton btn = new JButton(plugin.toString(), ImageUtil.getIcon(plugin.getIconName()));
        btn.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                modulePlugInAction(plugin);
            }
        });
    }

    /** Creates new form MainFrame2 */
    public BaseMainFrame(String title) {
        super(title);
        baseMainFrame = this;
        initComponents();
        initOthers();
        setupEvents();
        updateTopUI();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">                          
    private void initComponents() {

        pnlBottom = new javax.swing.JPanel();
        pnlTop = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        labLog = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        javax.swing.GroupLayout pnlBottomLayout = new javax.swing.GroupLayout(pnlBottom);
        pnlBottom.setLayout(pnlBottomLayout);
        pnlBottomLayout.setHorizontalGroup(
                pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 918, Short.MAX_VALUE));
        pnlBottomLayout.setVerticalGroup(
                pnlBottomLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGap(0, 528, Short.MAX_VALUE));

        pnlTop.setPreferredSize(new java.awt.Dimension(876, 52));

        jPanel1.setPreferredSize(new java.awt.Dimension(281, 36));

        jLabel1.setFont(new java.awt.Font("黑体", 0, 18));
        jLabel1.setForeground(new java.awt.Color(255, 255, 255));
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/hr_logo_3.png"))); // NOI18N
        jLabel1.setPreferredSize(new java.awt.Dimension(195, 20));

        labLog.setFont(new java.awt.Font("黑体", 1, 18));
        labLog.setForeground(new java.awt.Color(255, 255, 255));
        labLog.setText("信用卡管理系统");

        javax.swing.GroupLayout pnlTopLayout = new javax.swing.GroupLayout(pnlTop);
        pnlTop.setLayout(pnlTopLayout);
        pnlTopLayout.setHorizontalGroup(
                pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(pnlTopLayout.createSequentialGroup().addContainerGap().addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 150, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(labLog, javax.swing.GroupLayout.PREFERRED_SIZE, 377, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 89, Short.MAX_VALUE).addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 320, javax.swing.GroupLayout.PREFERRED_SIZE)));
        pnlTopLayout.setVerticalGroup(
                pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(pnlTopLayout.createSequentialGroup().addContainerGap().addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(pnlTopLayout.createSequentialGroup().addGroup(pnlTopLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING).addComponent(labLog, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE).addComponent(jLabel1, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE)).addGap(11, 11, 11)).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlTopLayout.createSequentialGroup().addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE).addContainerGap()))));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addComponent(pnlTop, javax.swing.GroupLayout.DEFAULT_SIZE, 952, Short.MAX_VALUE).addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup().addGap(24, 24, 24).addComponent(pnlBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE).addContainerGap()));
        layout.setVerticalGroup(
                layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING).addGroup(layout.createSequentialGroup().addComponent(pnlTop, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE).addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED).addComponent(pnlBottom, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)));

        pack();
    }// </editor-fold>                        

    public static BaseMainFrame getBaseMainFrame() {
        return baseMainFrame;
    }
    // Variables declaration - do not modify                     
    private javax.swing.JLabel jLabel1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JLabel labLog;
    private javax.swing.JPanel pnlBottom;
    private javax.swing.JPanel pnlTop;
    // End of variables declaration                   

    private void initOthers() {
        this.remove(pnlTop);
        this.remove(pnlBottom);
        this.setLayout(new BorderLayout());
        this.add(pnlBottom, BorderLayout.CENTER);
        this.add(pnlTop, BorderLayout.NORTH);
        pnlBottom.setLayout(new BorderLayout());
        ContextManager.getStatusBar().setContent(4, JStatusBar.CLOCK);
        this.add(pnlTop, BorderLayout.NORTH);//首页顶部
        JPanel centerPane = new JPanel(new BorderLayout());
        centerPane.setOpaque(true);
        centerPane.setBorder(BorderFactory.createEmptyBorder(2, 0, 0, 0));
        pnlBottom.add(centerPane, BorderLayout.CENTER);
        centerPane.add(outlookPane, BorderLayout.WEST);  //左侧树菜单
        PagebackPanel pageBack = new PagebackPanel();
        centerPane.add(pageBack, BorderLayout.CENTER);
        pageBack.add(tab);
        pnlBottom.add(ContextManager.getStatusBar(), "South");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gds = ge.getScreenDevices();
        if (gds.length >= 1) {
            setSize(gds[0].getDefaultConfiguration().getBounds().getSize());
        }
        setLocation(0, 0);
        this.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        popMenu_right = new JPopupMenu();
        popMenu_right.add(mi_import_updateable);
        popMenu_right.add(mi_save_report);
        popMenu_right.add(miShowViewModule);
        popMenu_right.addSeparator();
        buildRoleMenu();
        buildLanguageMenu();
        buildLookMenu();
        initBaseMainUI();
        popMenu_right.add(mi_refresh);
        popMenu_right.add(mi_edit_pass);
        popMenu_right.add(mi_change_logo);
        popMenu_right.add(miDefineShowModule);
        popMenu_right.add(mi_ui_def);
        btnIndex.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnMyHelp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnHelp.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnQuit.setCursor(new Cursor(Cursor.HAND_CURSOR));
        btnAbout.setCursor(new Cursor(Cursor.HAND_CURSOR));
        initLogo();
        jPanel1.add(btnMyHelp1);
        jPanel1.add(btnMyHelp);
        jPanel1.add(new JLabel("  "));
        jPanel1.add(btnIndex1);
        jPanel1.add(btnIndex);
        jPanel1.add(new JLabel("  "));
        jPanel1.add(btnHelp1);
        jPanel1.add(btnHelp);
        jPanel1.add(new JLabel("  "));
        jPanel1.add(btnAbout1);
        jPanel1.add(btnAbout);
        jPanel1.add(new JLabel("  "));
        jPanel1.add(btnQuit1);
        jPanel1.add(btnQuit);
        jPanel1.add(new JLabel("  "));
    }

    private void updateTopUI() {
        ImageInputStream iis = null;
        try {
            iis = ImageIO.createImageInputStream(UIContext.getCurrenFace().getTopBackIconStream());
            final BufferedImage img_toplogo = ImageIO.read(iis);
            pnlTop.setUI(new PanelUI() {

                @Override
                public void update(Graphics g, JComponent c) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.drawImage(img_toplogo, 0, 0, c.getWidth(), c.getHeight(), null);
                    paint(g, c);
                }
            });
        } catch (IOException e1) {
            e1.printStackTrace();
            log.error(e1);
        }
        try {
            iis = ImageIO.createImageInputStream(UIContext.getCurrenFace().getTopTitleIconStream());
            final BufferedImage img_toplogo1 = ImageIO.read(iis);
            jPanel1.setUI(new PanelUI() {

                @Override
                public void update(Graphics g, JComponent c) {
                    Graphics2D g2 = (Graphics2D) g;
                    g2.drawImage(img_toplogo1, 0, 0, c.getWidth(), c.getHeight(), null);
                    paint(g, c);
                }
            });
        } catch (IOException e1) {
            log.error(e1);
        }
        ComponentUtil.setIcon(btnMyHelp1, UIContext.getCurrenFace().getMyHelpIcon());
        ComponentUtil.setIcon(btnHelp1, UIContext.getCurrenFace().getHelpIcon());
        ComponentUtil.setIcon(btnAbout1, UIContext.getCurrenFace().getAboutIcon());
        ComponentUtil.setIcon(btnIndex1, UIContext.getCurrenFace().getIndexIcon());
        ComponentUtil.setIcon(btnQuit1, UIContext.getCurrenFace().getQuitIcon());
        initLogo();
        Color color = UIContext.getCurrenFace().getIndexToolForegound();
        btnMyHelp.setForeground(color);
        btnAbout.setForeground(color);
        btnHelp.setForeground(color);
        btnIndex.setForeground(color);
        btnQuit.setForeground(color);
        baseMainFrame.update(baseMainFrame.getGraphics());
    }

    public void initLogo() {
        Icon icon = TransferAccessory.getIconFromBufferImage(TransferAccessory.downloadPicture("@Logo"), 150, 30);
        if (icon != null) {
            jLabel1.setIcon(icon);
        } else {
            jLabel1.setIcon(UIContext.getCurrenFace().getLogoIcon());
        }
        this.setIconImage(ImageUtil.getIconImage());
    }

    public static void refreshUI() {
        UIManager.put("InternalFrame.icon", ImageUtil.getIcon("frame_icon.png"));
        BeautyEyeLNFHelper.implLNF();
        List<SysParameter> prop = ClientProperty.getInstance().getAllParas();
        for (SysParameter sp : prop) {
            String keyStr = sp.getSysparameter_code();
            String value = sp.getSysparameter_value();
            if (value.equals("")) {
                continue;
            }
            if (keyStr.startsWith("UIManager")) {
                UIManager.put(keyStr.substring(10), SysUtil.objToInt(value));
            }
        }
        try {
            String font_size_str = ClientProperty.getInstance().getUI_Font_size().getSysparameter_value();
            Font font = FreeUtil.getFontBySize("宋体", font_size_str);
            java.util.Enumeration keys = UIManager.getDefaults().keys();
            while (keys.hasMoreElements()) {
                Object key = keys.nextElement();
                Object value = UIManager.get(key);
                if (value instanceof javax.swing.plaf.FontUIResource) {
                    UIManager.put(key, font);
                }
            }
            UIManager.put("ComboBox.background", new ColorUIResource(Color.WHITE));
            UIManager.put("ComboBox.foreground", new ColorUIResource(Color.BLACK));
            UIManager.put("ComboBox.disabledForeground", new ColorUIResource(Color.BLACK));
            UIManager.put("ComboBox.disabledBackground", new ColorUIResource(Color.WHITE));
            UIManager.put("ToolBar.separatorSize", new DimensionUIResource(30, 10));
            UIManager.put("TabbedPane.tabAreaInsets", FreeUtil.ZERO_INSETS);
            UIManager.put("TabbedPane.contentBorderInsets", new InsetsUIResource(1, 0, 0, 0));
            UIManager.put("TabbedPane.selectedTabPadInsets", FreeUtil.ZERO_INSETS);
            UIManager.put("TabbedPane.tabInsets", new InsetsUIResource(5, 5, 1, 5));
            UIManager.put("Tree.expandedIcon", ImageUtil.getIcon("treeMinus.png"));
            UIManager.put("Tree.collapsedIcon", ImageUtil.getIcon("treePlus.png"));
            if (baseMainFrame == null) {
                return;
            }
            baseMainFrame.update(baseMainFrame.getGraphics());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

    private void setupEvents() {
        mi_ui_def.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                UIDefinePnl pnl = new UIDefinePnl();
                ModelFrame.showModel(baseMainFrame, pnl, true, "界面参数设置：");
            }
        });
        mi_change_logo.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                ChangeLogoDialog dlg = new ChangeLogoDialog(ContextManager.getMainFrame(), true);
                ContextManager.locateOnMainScreenCenter(dlg);
                dlg.setVisible(true);
            }
        });
//        mi_use_ukey.addActionListener(new ActionListener() {
//
//            @Override
//            public void actionPerformed(ActionEvent e) {
//                log.info(e);
////                useUkey_para.setSysparameter_value("0".equals(useUkey_para.getSysparameter_value()) ? "1" : "0");
////                CommUtil.saveOrUpdate(useUkey_para);
////                updateItemUI();
//
////                UKeySetDialog uKeySetDialog = new UKeySetDialog(JOptionPane.getFrameForComponent(mi_use_ukey), false);
////                ContextManager.locateOnScreenCenter(uKeySetDialog);
//
//
//                java.awt.EventQueue.invokeLater(new Runnable() {
//
//                    public void run() {
//                        UKeySetDialog dialog = new UKeySetDialog(JOptionPane.getFrameForComponent(mi_use_ukey), true);
//                        ContextManager.locateOnScreenCenter(dialog);
//                        dialog.setVisible(true);
//                    }
//                });
//            }
//        });
//        
        mi_refresh.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                UserContext.getMemoryDept(true);
                UserContext.initRights();
            }
        });
        tab.addChangeListener(new ChangeListener() {

            @Override
            public void stateChanged(ChangeEvent e) {
                cur_Panel = (JPanel) tab.getSelectedComponent();
                if (cur_Panel instanceof IModulePanel) {
                    ContextManager.refreshStatusBar();
                    ((IModulePanel) cur_Panel).refresh();
                }
            }
        });
        btnAbout.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                log.info(e);
                new AboutAction().actionPerformed(null);
            }
        });
        this.addWindowListener(new WindowAdapter() {

            @Override
            public void windowClosing(WindowEvent e) {
                quitHR();
            }
        });
        btnIndex.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                log.info(e);
                modulePlugInAction(new IndexPanelPlugIn());
            }
        });
        btnQuit.addMouseListener(new MouseAdapter() {

            public void mouseClicked(MouseEvent e) {
                if (JOptionPane.showConfirmDialog(ContextManager.getMainFrame(),
                        "确定要退出系统吗?", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
                    return;
                }
                quitHR();
            }
        });
        pnlTop.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() == MouseEvent.BUTTON3) {
                    popMenu_right.show((Component) e.getSource(), e.getX() - 15, e.getY());
                }
            }
        });
        mi_import_updateable.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                SysParameter sp = UserContext.getSys_para(UserContext.codeImportViewField);
                sp.setSysparameter_value(sp.getSysparameter_value().equals("0") ? "1" : "0");
                CommUtil.saveOrUpdate(sp);
                ComponentUtil.setBooleanIcon(mi_import_updateable, "1".equals(sp.getSysparameter_value()));
            }
        });
        mi_edit_pass.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                EditPassDialog epDlg = new EditPassDialog(BaseMainFrame.this, true);
                ContextManager.locateOnMainScreenCenter(epDlg);
                epDlg.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                epDlg.setVisible(true);
            }
        });
        miDefineShowModule.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                defineShowModule();
            }
        });
    }

    private void defineShowModule() {
        List checkObjs = new ArrayList();
        List list = new ArrayList();
        for (Object obj : UserContext.funtion_list) {
            FuntionRight fr = (FuntionRight) obj;
            if (fr.isModule_flag() || fr.getFun_code().length() < 5) {
                list.add(obj);
                if (showModules.contains(fr.getFun_module_flag())) {
                    checkObjs.add(fr);
                }
            }
        }
        FuntionTreeModel funtionTreeModel = new FuntionTreeModel(list);
        JTree m_tree = new JTree(funtionTreeModel);
        new RenderderMap().initTree(m_tree, TreeSelectMod.nodeCheckChildFollowMod);
        Enumeration enumt = ((CheckTreeNode) funtionTreeModel.getRoot()).breadthFirstEnumeration();
        while (enumt.hasMoreElements()) {
            CheckTreeNode cnode = (CheckTreeNode) enumt.nextElement();
            if (checkObjs.contains(cnode.getUserObject())) {
                cnode.setSelected(true);
            }
        }
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(new JScrollPane(m_tree));
        pnl.setPreferredSize(new Dimension(400, 350));
        if (ModalDialog.doModal(ContextManager.getMainFrame(), pnl, "自定义显示模块")) {
            List modules = ComponentUtil.getCheckedObjs(m_tree);
            StringBuilder str = new StringBuilder();
            for (Object obj : modules) {
                if (obj instanceof FuntionRight) {
                    str.append(((FuntionRight) obj).getFun_module_flag()).append(";");
                }
            }
            ConfigManager.getConfigManager().setProperty("UI.showModules_" + UserContext.role_id, str.toString());
            ConfigManager.getConfigManager().save2();
            if (miCurRole != null) {
                refreshRoleMenu(miCurRole);
            }
            BaseMainFrame.this.refreshSession();
            BaseMainFrame.this.setEnabled(true);
        }
    }

    private void initPara() {
        if (UserContext.getSys_para(UserContext.codeImportViewField) != null) {
            return;
        }
        ComponentUtil.setBooleanIcon(miShowViewModule, UserContext.codeShowViewModule);
        ComponentUtil.setBooleanIcon(mi_save_report, UserContext.codeShowSaveReport);
        List list = CommUtil.fetchEntities("from SysParameter sp where sp.sysParameter_key='" + UserContext.codeImportViewField + "'");
        SysParameter import_update_para = null;
        if (list.size() > 0) {
            import_update_para = (SysParameter) list.get(0);
        }
        if (import_update_para == null) {
            import_update_para = new SysParameter();
            import_update_para.setSysParameter_key(UserContext.codeImportViewField);
            import_update_para.setSysparameter_value("0");
            import_update_para.setSysparameter_code(UserContext.codeImportViewField);
            CommUtil.saveOrUpdate(import_update_para);
        }
        ComponentUtil.setBooleanIcon(mi_import_updateable, "1".equals(import_update_para.getSysparameter_value()));
        UserContext.putSys_para(import_update_para.getSysparameter_code(), import_update_para);
    }

    private void buildLookMenu() {
        popMenu_right.add(mSelectFeel);
        List faces = UIContext.getFaces();
        for (final Object obj : faces) {
            JMenuItem mi = new JMenuItem(obj.toString());
            mi.addActionListener(new ActionListener() {

                @Override
                public void actionPerformed(ActionEvent e) {
                    UIContext.setCurrenFace((UIFace) obj);
                    updateTopUI();
                }
            });
            UIContext.setCurrenFace((UIFace) obj);
            mSelectFeel.add(mi);
        }
    }

    private void quitHR() {
        String user_code = UserContext.person_key + "|" + UserContext.getPerson_ip() + "|" + UserContext.getPerson_mac() + "|" + UserContext.person_code + "|" + AppHrClient.loginCode;
        CommUtil.connectServer("quit", user_code);
        System.exit(0);
    }

    // 该类为业务应用模块的可停靠包装类
    @Override
    public String getModuleCode() {
        return module_code;
    }

    class TaskTreeRenderer extends JLabel implements TreeCellRenderer {

        private final Icon moduleIcon = ImageUtil.getIcon("leaf.gif");
        private final Icon classIcon = ImageUtil.getIcon("folderClosedN.gif");
        private final Icon classIcon_opend = ImageUtil.getIcon("folderOpenN.gif");

        @Override
        public Component getTreeCellRendererComponent(JTree tree, Object value, boolean selected, boolean expanded, boolean leaf, int row, boolean hasFocus) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) value;
            Object obj = node.getUserObject();
            JLabel label = new JLabel();
            tree.setRowHeight(30);
            tree.putClientProperty("JTree.lineStyle", "None");   //隐藏树连接线
            tree.setShowsRootHandles(false);
            tree.getParent().setBackground(FreeUtil.TASKCOLOR);
            ((JPanel) tree.getParent()).setBorder(null);
            tree.setBackground(FreeUtil.TASKCOLOR);
            label.setText(" " + obj.toString());
            if (obj instanceof String) {
                if (!expanded) {
                    label.setIcon(classIcon);
                } else {
                    label.setIcon(classIcon_opend);
                }
            } else if (obj instanceof CommTask) {
                label.setIcon(moduleIcon);
            }
            if (selected) {
                label.setBackground(new Color(184, 207, 229));
                label.setForeground(new Color(6, 63, 133));
                label.setOpaque(true);
            }
            return label;
        }
    }
}
