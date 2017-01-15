package org.jhrcore.client.system;

import org.jhrcore.client.system.comm.RebuildFieldRightPanel;
import org.jhrcore.client.system.comm.RebuilIndexPanel;
import org.jhrcore.client.system.comm.FieldRegulaSetPanel;
import org.jhrcore.client.system.comm.FieldFormatSetPanel;
import com.foundercy.pf.control.table.FTable;
import com.foundercy.pf.control.table.FTableModel;
import com.foundercy.pf.control.table.RowChangeListner;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
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
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JToolBar;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;
import javax.swing.tree.TreePath;
import org.apache.log4j.Logger;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.PinYinMa;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.annotation.FieldAnnotation;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.EntityClass;
import org.jhrcore.entity.base.FieldDef;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.base.TempGroup;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.client.system.comm.DragAndDropDragSourceListener;
import org.jhrcore.client.system.comm.DragAndDropTransferable;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.client.system.comm.ModuleTreeModel;
import org.jhrcore.iservice.impl.SysImpl;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.ValidateEntity;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ImageUtil;
import org.jhrcore.util.MsgUtil;
/*
 * 建立重构图标	
 * by yangzhou 2008-6-16
 */

public class SysRebuildPanel extends JPanel implements IModulePanel {

    private static final long serialVersionUID = 1L;
    private JTree treeModule;
    private JButton btnAdd = new JButton("新增");
    private JButton btnCancel = new JButton("取消");
    private JButton btnDel = new JButton("删除");
    private JButton btnSave = new JButton("保存");
    private JButton bPYM = new JButton("生成拼音码");
    private JButton btnFormat = new JButton("格式化设置");
    private JButton btnFieldRegula = new JButton("校验规则设置");
    private JButton btnIndexSet = new JButton("索引设置");
    private JButton btnSearch = new JButton("", ImageUtil.getSearchIcon());
    private JPopupMenu pMenu = new JPopupMenu();
    private JPopupMenu jPopupMenu = new JPopupMenu();
    private JComboBox cbBoxSearch = new JComboBox();
    private ModuleTreeModel moduleTreeModel;
    private JPanel operatePanel;
    private JToolBar toolbar = new JToolBar();
    private JMenuItem miOne = new JMenuItem("当前表");
    private JMenuItem miAll = new JMenuItem("所有表");
    private JMenuItem miAdd = new JMenuItem("新增");
    private JMenuItem miDel = new JMenuItem("删除");
    private JMenuItem miSave = new JMenuItem("保存");
    private JMenuItem miUse = new JMenuItem("恢复");
    private JMenuItem miRight = new JMenuItem("授权");
    private JMenuItem miFieldRegula = new JMenuItem("校验规则设置");
    private Object cur_object = null;
    private DefaultMutableTreeNode cur_node;
    private BeanPanel beanPanel = new BeanPanel();
    private FTable ftable;
    private List<String> disabled_fields = new ArrayList<String>();
    private Hashtable<String, Integer> exist_fields = new Hashtable<String, Integer>();
    private Hashtable<String, HashSet<String>> field_codes = new Hashtable<String, HashSet<String>>();//用于生成表名和字段名
    private Object change_obj = null;
    private PropertyChangeListener propertyChangeListener;
    private EntityDef cur_entityDef = null;
    private String last_search_val = "";
    private int last_locate_position = -1;
    private List<String> fieldDef_fields = new ArrayList<String>();
    public static final String module_code = "SysRebuild";
    private Logger log = Logger.getLogger(SysRebuildPanel.class.getName());

    @Override
    public void setFunctionRight() {
        ComponentUtil.setSysCompFuntion(miAdd, "SysRebuild.btnAdd");
        ComponentUtil.setSysCompFuntion(miDel, "SysRebuild.btnDel");
        ComponentUtil.setSysCompFuntion(miSave, "SysRebuild.btnSave");
        btnCancel.setEnabled(false);
        btnIndexSet.setEnabled(true);
    }
    private RowChangeListner change_listener = new RowChangeListner() {

        @Override
        public void rowChanged(Object obj) {
            ComponentUtil.setCompEnable(this, btnCancel, true);
            change_obj = obj;
            Integer old_order_no = 0;
            Integer new_order_no = 0;
            if (obj instanceof FieldDef) {
                FieldDef fd = (FieldDef) obj;
                if (!fd.isUsed_flag()) {
                    return;
                }
                if (fd.getFun_flag() != 1) {
                    fd.setFun_flag(2);
                }
                old_order_no = exist_fields.get(fd.getEntityDef().getEntityName() + "." + fd.getField_name());
                new_order_no = fd.getOrder_no();
                exist_fields.put(fd.getEntityDef().getEntityName() + "." + fd.getField_name(), fd.getOrder_no());
            } else if (obj instanceof EntityDef) {
                EntityDef ed = (EntityDef) obj;
                if (ed.getFun_flag() != 1) {
                    ed.setFun_flag(2);
                }
                old_order_no = exist_fields.get(ed.getEntityName());
                new_order_no = ed.getOrder_no();
                exist_fields.put(ed.getEntityName(), ed.getOrder_no());
            }
            if (new_order_no.intValue() != old_order_no.intValue()) {
                changeOrder_no(old_order_no, new_order_no, cur_node, change_obj);
            }
        }
    };

    public void setFunctionRight(Object obj) {
        cur_object = obj;
        setEditState();
    }

    private void setEditState() {
        boolean add_flag = false;
        boolean del_flag = false;
        boolean use_flag = false;
        boolean right_flag = false;
        if (cur_object != null) {
            if (cur_object instanceof EntityClass) {
                EntityClass entityClass = (EntityClass) cur_object;
                add_flag = entityClass.getModify_flag();
                del_flag = false;
                right_flag = false;
            } else if (cur_object instanceof EntityDef) {
                right_flag = true;
                EntityDef entityDef = (EntityDef) cur_object;
                if ("系统逻辑表".equals(entityDef.getCanmodify())) {
                    del_flag = false;
                    add_flag = false;
                } else if ("系统业务表".equals(entityDef.getCanmodify())) {
                    del_flag = false;
                    add_flag = true;
                } else if ("用户自定义".equals(entityDef.getCanmodify())) {
                    del_flag = true;
                    add_flag = true;
                }
                disabled_fields.clear();
                disabled_fields.add("format");
                ftable.setDisable_fields(disabled_fields);
            } else if (cur_object instanceof FieldDef) {
                right_flag = true;
                FieldDef fieldDef = (FieldDef) cur_object;
                if ("关键字".equals(fieldDef.getField_mark()) || "系统固定项".equals(fieldDef.getField_mark()) || "自定义固定项".equals(fieldDef.getField_mark())) {
                    del_flag = false;
                } else if ("自定义已选项".equals(fieldDef.getField_mark())) {
                    del_flag = true;
                } else if ("自定义备选项".equals(fieldDef.getField_mark())) {
                    del_flag = true;
                    use_flag = true;
                }
                disabled_fields.clear();
                disabled_fields.add("field_name");
                disabled_fields.add("order_no");
                if (fieldDef.getNew_flag() == 0) {
                    disabled_fields.add("field_type");
                    disabled_fields.add("field_width");
                    disabled_fields.add("field_scale");
                    disabled_fields.add("not_null");
                    if (!fieldDef.getField_type().equals("String")) {
                        disabled_fields.add("code_type_name");
                    }
                    if (fieldDef.getField_type().equals("String") || fieldDef.getField_type().equals("Boolean") || fieldDef.getField_type().equals("Integer")) {
                        disabled_fields.add("format");
                    }
                }
                add_flag = true;
                beanPanel.setDisable_fields(disabled_fields);
                beanPanel.setFields(fieldDef_fields);
            } else if (cur_object instanceof TempGroup) {
                add_flag = true;
                right_flag = true;
            } else {
                right_flag = false;
            }
        }
        ComponentUtil.setCompEnable(this, btnAdd, add_flag);
        ComponentUtil.setCompEnable(this, btnDel, del_flag);
        ComponentUtil.setCompEnable(this, miRight, right_flag);
        ComponentUtil.setCompEnable(this, miUse, use_flag);
        miAdd.setEnabled(UserContext.hasFunctionRight("SysRebuild.btnAdd") && add_flag);
        miDel.setEnabled(UserContext.hasFunctionRight("SysRebuild.btnDel") && del_flag);
    }

    public SysRebuildPanel() {
        super(new BorderLayout());
        initOthers();
        setEvents();
    }

    private void initToolBar() {
        toolbar.setFloatable(false);
        toolbar.setPreferredSize(new Dimension(this.getWidth(), 25));
        toolbar.setMaximumSize(new Dimension(this.getWidth(), 25));
        toolbar.add(btnAdd);
        toolbar.add(btnCancel);
        toolbar.add(btnDel);
        toolbar.add(btnSave);
        toolbar.add(bPYM);
        toolbar.add(btnFormat);
        toolbar.add(btnFieldRegula);
        toolbar.add(btnIndexSet);
        toolbar.add(new JLabel(" 查找:"));
        ComponentUtil.setSize(cbBoxSearch, 130, 22);
        ComponentUtil.setSize(btnSearch, 22, 22);
        cbBoxSearch.setEditable(true);
        toolbar.add(cbBoxSearch);
        toolbar.add(btnSearch);
    }

    private void initOthers() {
        fieldDef_fields.add("order_no");
        fieldDef_fields.add("field_name");
        fieldDef_fields.add("field_caption");
        fieldDef_fields.add("field_type");
        fieldDef_fields.add("field_width");
        fieldDef_fields.add("field_scale");
        fieldDef_fields.add("view_width");
        fieldDef_fields.add("format");
        fieldDef_fields.add("code_type_name");
        fieldDef_fields.add("field_mark");
        fieldDef_fields.add("field_align");
        fieldDef_fields.add("visible");
        fieldDef_fields.add("default_value");
        pMenu.add(miOne);
        pMenu.add(miAll);
        initToolBar();
        moduleTreeModel = new ModuleTreeModel();
        treeModule = new JTree(moduleTreeModel);
        HRRendererView.getRebuildMap(treeModule).initTree(treeModule);
        treeModule.setRootVisible(false);
        treeModule.setShowsRootHandles(true);
        DragSource dragSource = DragSource.getDefaultDragSource(); // 创建拖拽源
        dragSource.createDefaultDragGestureRecognizer(treeModule,
                DnDConstants.ACTION_COPY_OR_MOVE,
                new DragAndDropDragGestureListener()); // 建立拖拽源和事件的联系
        @SuppressWarnings("unused")
        DropTarget dropTarget = new DropTarget(treeModule,
                new DragAndDropDropTargetListener());
        JScrollPane scrollPane = new JScrollPane(treeModule);
        operatePanel = new JPanel(new BorderLayout());
        operatePanel.add(toolbar, BorderLayout.NORTH);
        operatePanel.add(beanPanel, BorderLayout.CENTER);
        JSplitPane sp = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, scrollPane,
                operatePanel);
        sp.setDividerLocation(200);
        sp.setOneTouchExpandable(true);
        sp.setDividerSize(3);
        jPopupMenu.add(miAdd);
        jPopupMenu.addSeparator();
        jPopupMenu.add(miDel);
        jPopupMenu.addSeparator();
        jPopupMenu.add(miSave);
        jPopupMenu.addSeparator();
        jPopupMenu.add(miUse);
        jPopupMenu.addSeparator();
        jPopupMenu.add(miRight);
        jPopupMenu.add(miFieldRegula);
        beanPanel.setColumns(2);
        //初始化已存在表及字段名记录，用于生成表及字段名
        initNodeCode();
        setEditState();
        this.add(sp, BorderLayout.CENTER);
        propertyChangeListener = new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent evt) {
                changeObject(cur_object, evt.getPropertyName(), evt.getNewValue());
            }
        };
    }
    private void initNodeCode() {
        Enumeration enumt = ((DefaultMutableTreeNode) moduleTreeModel.getRoot()).depthFirstEnumeration();
        while (enumt.hasMoreElements()) {
            String key = "";
            String value = "";
            String key1 = "";
            int value1 = 0;
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof String || obj instanceof ModuleInfo || obj instanceof EntityClass) {
                continue;
            }
            if (obj instanceof EntityDef) {
                key1 = ((EntityDef) obj).getEntityName();
                key = "jhr_entity";
                value = ((EntityDef) obj).getEntityName().toUpperCase();
                value1 = ((EntityDef) obj).getOrder_no();
            } else if (obj instanceof FieldDef) {
                FieldDef fd = (FieldDef) obj;
                key1 = fd.getEntityDef().getEntityName() + "." + fd.getField_name();
                key = fd.getEntityDef().getEntityName();
                value = fd.getField_name().toUpperCase();
                value1 = fd.getOrder_no();
            }
            if (key.equals("")) {
                continue;
            }
            exist_fields.put(key1, value1);
            addCodeIndex(key, value);
        }
    }

    private void setEvents() {
        btnIndexSet.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Enumeration enumt = moduleTreeModel.getRootNode().breadthFirstEnumeration();
                List<EntityDef> entitys = new ArrayList<EntityDef>();
                while (enumt.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                    if (node.getUserObject() instanceof EntityDef) {
                        entitys.add((EntityDef) node.getUserObject());
                    }
                }
                RebuilIndexPanel pnlRI = new RebuilIndexPanel(entitys);
                ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(btnIndexSet), pnlRI, true, "系统索引设置:", 800, 600);
            }
        });
        btnFormat.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                FieldFormatSetPanel ffsPanel = new FieldFormatSetPanel();
                ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(btnFormat), ffsPanel, true, "格式化设置", 650, 550);
            }
        });
        ActionListener al = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                locate();
            }
        };
        cbBoxSearch.addActionListener(al);
        btnSearch.addActionListener(al);
        ActionListener regula_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final List<FieldDef> fieldDefs = new ArrayList<FieldDef>();
                String entity_name = "";
                DefaultMutableTreeNode parent_node = null;
                if (cur_object instanceof EntityDef) {
                    entity_name = ((EntityDef) cur_object).getEntityCaption();
                    parent_node = cur_node;
                } else if (cur_object instanceof FieldDef) {
                    parent_node = (DefaultMutableTreeNode) cur_node.getParent();
                    entity_name = ((FieldDef) cur_object).getEntityDef().getEntityCaption();
                } else if (cur_object instanceof TempGroup) {
                    if (cur_object.toString().equals("已选")) {
                        parent_node = cur_node;
                    }
                    entity_name = ((EntityDef) ((DefaultMutableTreeNode) parent_node.getParent()).getUserObject()).getEntityCaption();
                }
                if (parent_node == null) {
                    return;
                }
                Enumeration enumt = parent_node.breadthFirstEnumeration();
                while (enumt.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                    Object obj = node.getUserObject();
                    if (obj instanceof FieldDef) {
                        FieldDef fd = (FieldDef) obj;
                        if (fd.isUsed_flag()) {
                            fieldDefs.add(fd);
                        }
                    }
                }
                if (fieldDefs.size() == 0) {
                    JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "该表没有字段");
                    return;
                }
                for (FieldDef fd : fieldDefs) {
                    if (fd.getNew_flag() == 1) {
                        JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "存在新建字段，请先保存修改");
                        return;
                    }
                }
                FieldRegulaSetPanel frsPanel = new FieldRegulaSetPanel(fieldDefs);
                ModelFrame mf = ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(btnFormat), frsPanel, true, "[" + entity_name + "]表字段关联设置", 800, 650);
                final DefaultMutableTreeNode parent = parent_node;
                mf.addIPickWindowCloseListener(new IPickWindowCloseListener() {

                    @Override
                    public void pickClose() {
                        StringBuffer str = new StringBuffer();
                        str.append("'-1'");
                        for (FieldDef fd : fieldDefs) {
                            str.append(",'");
                            str.append(fd.getField_key());
                            str.append("'");
                        }
                        List<FieldDef> list = (List<FieldDef>) CommUtil.fetchEntities("from FieldDef fd join fetch fd.entityDef ed join fetch ed.entityClass ec join fetch ec.moduleInfo where fd.field_key in(" + str.toString() + ")");
                        Enumeration enumt = parent.breadthFirstEnumeration();
                        while (enumt.hasMoreElements()) {
                            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                            Object obj = node.getUserObject();
                            if (obj instanceof FieldDef) {
                                FieldDef fd = (FieldDef) obj;
                                if (fd.isUsed_flag()) {
                                    for (FieldDef fieldDef : list) {
                                        if (fieldDef.getField_key().equals(fd.getField_key())) {
                                            node.setUserObject(fieldDef);
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                        treeModule.updateUI();
                    }
                });
            }
        };
        btnFieldRegula.addActionListener(regula_listener);
        miFieldRegula.addActionListener(regula_listener);
        bPYM.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pMenu.show(bPYM, 0, 20);
            }
        });
        miOne.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_entityDef == null) {
                    JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "请选择表",
                            "提示", JOptionPane.INFORMATION_MESSAGE);
                    return;
                }
                buildPYM(true);
            }
        });
        miAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                buildPYM(false);
            }
        });
        miRight.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_node == null) {
                    return;
                }
                if (cur_node.getLevel() <= 2) {
                    return;
                }
                if (cur_node.getUserObject().toString().equals("备选") && (cur_node.getUserObject() instanceof TempGroup)) {
                    return;
                }
                RebuildFieldRightPanel rfrPanel = new RebuildFieldRightPanel(cur_node);
                ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(btnFormat), rfrPanel, true, "字段授权", 600, 450);
            }
        });
        treeModule.addTreeSelectionListener(new TreeSelectionListener() {

            @Override
            public void valueChanged(TreeSelectionEvent e) {
                Object obj = e.getPath().getLastPathComponent();
                if (obj == null) {
                    return;
                }
                if (obj instanceof DefaultMutableTreeNode) {
                    cur_node = (DefaultMutableTreeNode) obj;
                    selectNode(cur_node);
                }
            }
        });// 添加到数据中去,点选节点事件
        treeModule.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    jPopupMenu.show(treeModule, e.getX(), e.getY());
                }
            }
        });
        ActionListener al_add = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                addObject();
            }
        };
        btnAdd.addActionListener(al_add);
        miAdd.addActionListener(al_add);
        ActionListener al_del = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                delObject();
            }
        };
        btnDel.addActionListener(al_del);
        miDel.addActionListener(al_del);
        ActionListener al_save = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                saveObject();
            }
        };
        btnSave.addActionListener(al_save);
        miSave.addActionListener(al_save);
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent arg0) {
                cancelObject();
            }
        });
        miUse.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                reuseField();
            }
        });
        selectTreeNode((DefaultMutableTreeNode) treeModule.getModel().getRoot());
    }

    private void locate() {
        if (ftable.getObjects().isEmpty()) {
            return;
        }
        if (cur_object instanceof FieldDef) {
            return;
        }
        if ((ftable.getObjects().get(0)) instanceof FieldDef) {
            String str = cbBoxSearch.getSelectedItem().toString();
            if (str == null) {
                return;
            }
            str = str.replace(" ", "");
            String tmp_str = str.toUpperCase();
            if (!tmp_str.equals(last_search_val)) {
                last_locate_position = -1;
                last_search_val = tmp_str;
            }
            last_locate_position = SysUtil.locateEmp(-1, tmp_str, last_search_val, last_locate_position, ftable, Arrays.asList(new String[]{"field_name", "field_caption", "pym"}));
            if (last_locate_position > -1) {
                ftable.setRowSelectionInterval(last_locate_position, last_locate_position);
                ftable.getVerticalScrollBar().setValue(last_locate_position * ftable.getRowHeight());
                boolean b_contain = false;
                for (int i = 0; i < cbBoxSearch.getItemCount(); i++) {
                    if (cbBoxSearch.getItemAt(i).equals(str)) {
                        b_contain = true;
                        break;
                    }
                }
                if (!b_contain) {
                    cbBoxSearch.addItem(str);
                    if (cbBoxSearch.getItemCount() > 10) {
                        cbBoxSearch.removeItemAt(0);
                    }
                }
                ftable.updateUI();
            }
        }
    }

    private void reuseField() {
        if (cur_object instanceof FieldDef) {
            FieldDef fieldDef = (FieldDef) cur_object;
            fieldDef.setUsed_flag(true);
            fieldDef.setField_mark("自定义已选项");
            ValidateSQLResult result = CommUtil.updateEntity(fieldDef);
            if (result.getResult() == 0) {
                DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) cur_node.getParent().getParent();
                cur_node.removeFromParent();
                ((DefaultMutableTreeNode) parent_node.getFirstChild()).add(new DefaultMutableTreeNode(fieldDef));
                treeModule.updateUI();
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
        }
    }

    /**
     * 该方法用于根据flag值来生成所有/当前表字段的拼音码
     * @param flag：true：当前表；false：所有表
     */
    private void buildPYM(boolean flag) {
        ValidateSQLResult result = SysImpl.buildFieldPYM(flag ? cur_entityDef.getEntity_key() : "@");
        if (result.getResult() == 0) {
            JOptionPane.showMessageDialog(ContextManager.getMainFrame(), "成功生成拼音码。",
                    "提示", JOptionPane.INFORMATION_MESSAGE);
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
        }
    }

    /**
     * 卡片属性变化监听函数
     * @param obj：变化对象
     * @param change_field_name：变化属性名
     * @param new_value：新值
     */
    private void changeObject(Object obj, String change_field_name, Object new_value) {
        if (obj instanceof FieldDef) {
            FieldDef fieldDef = (FieldDef) obj;
            if (fieldDef.getFun_flag() == 0) {//&& saveField_flag == 0) {
                fieldDef.setFun_flag(2);
                DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) cur_node.getParent();
                TempGroup tempGroup = (TempGroup) parent_node.getUserObject();
                tempGroup.setChange_flag(2);
                DefaultMutableTreeNode parent_node1 = (DefaultMutableTreeNode) parent_node.getParent();
                EntityDef tempEntity = (EntityDef) parent_node1.getUserObject();
                tempEntity.setFun_flag(2);
            }
            if ("field_width".equals(change_field_name)) {
                fieldDef.setView_width(Integer.parseInt(new_value.toString()));
            } else if ("field_type".equals(change_field_name)) {
                fieldDef.setField_align("左对齐");
                setEnableForComboBox((JComponent) beanPanel.getComponent_keys().get("code_type_name"), false);
                beanPanel.getComponent_keys().get("field_width").setEnabled(true);
                if (new_value.toString().equals("String")) {
                    fieldDef.setField_width(20);
                    fieldDef.setView_width(20);
                    fieldDef.setField_scale(0);
                    fieldDef.setFormat("");
                    beanPanel.getComponent_keys().get("field_scale").setEnabled(false);
                    beanPanel.getComponent_keys().get("format").setEnabled(false);
                    setEnableForComboBox((JComponent) beanPanel.getComponent_keys().get("code_type_name"), true);
                } else if (new_value.toString().equals("Integer")) {
                    fieldDef.setField_width(10);
                    fieldDef.setView_width(10);
                    fieldDef.setField_scale(0);
                    fieldDef.setField_align("右对齐");
                    fieldDef.setFormat("");
                    beanPanel.getComponent_keys().get("field_scale").setEnabled(true);
                    beanPanel.getComponent_keys().get("format").setEnabled(false);
                } else if (new_value.toString().equals("Float")) {
                    fieldDef.setField_width(19);
                    fieldDef.setView_width(19);
                    fieldDef.setField_scale(2);
                    fieldDef.setField_align("右对齐");
                    fieldDef.setFormat("0.00");
                    beanPanel.getComponent_keys().get("format").setEnabled(true);
                    beanPanel.getComponent_keys().get("field_scale").setEnabled(true);
                } else if (new_value.toString().equals("Date")) {
                    fieldDef.setField_width(20);
                    fieldDef.setView_width(20);
                    fieldDef.setField_scale(0);
                    fieldDef.setFormat("yyyy-MM-dd");
                    beanPanel.getComponent_keys().get("field_scale").setEnabled(false);
                    beanPanel.getComponent_keys().get("field_width").setEnabled(false);
                    beanPanel.getComponent_keys().get("format").setEnabled(true);
                } else if (new_value.toString().equals("Boolean")) {
                    fieldDef.setField_width(4);
                    fieldDef.setView_width(4);
                    fieldDef.setField_scale(0);
                    fieldDef.setFormat("");
                    beanPanel.getComponent_keys().get("field_scale").setEnabled(false);
                    beanPanel.getComponent_keys().get("field_width").setEnabled(false);
                    beanPanel.getComponent_keys().get("format").setEnabled(false);
                }
            }
            cur_node.setUserObject(obj);
            ComponentUtil.setCompEnable(this, btnCancel, true);
        }
        treeModule.updateUI();
    }

    private void setEnableForComboBox(JComponent c, boolean enable) {
        Component[] cs = c.getComponents();
        for (Component c1 : cs) {
            if (c1.getClass().getSimpleName().equals("JButton")) {
                c1.setEnabled(enable);
            }
        }
    }

    /**
     * 用于用户在手动修改排序号时的相关处理，比如：将排序号从1改成3，则需要将2，3的排序号依次减一，并且将当前对象的排序号设为3
     * @param old_order_no：老的排序号
     * @param new_order_no：新的排序号
     * @param parent_node：父节点
     */
    private void changeOrder_no(int old_order_no, int new_order_no, DefaultMutableTreeNode parent_node, Object change_obj) {
        boolean isEntityDef = ((FTableModel) ftable.getModel()).getEntityClass().getSimpleName().equals("EntityDef");//parent_node.getUserObject() instanceof EntityClass;
        List list = ftable.getObjects();
        boolean isExistOrder = false;
        Hashtable<String, Object> change_keys = new Hashtable<String, Object>();
        if (isEntityDef) {
            change_keys.put(((EntityDef) change_obj).getEntityName(), change_obj);
            EntityDef ed;
            String entity_key = ((EntityDef) change_obj).getEntity_key();
            for (Object obj : list) {
                ed = (EntityDef) obj;
                if (ed.getEntity_key().equals(entity_key)) {
                    continue;
                }
                if (ed.getOrder_no() == new_order_no) {
                    isExistOrder = true;
                    break;
                }
            }
            if (!isExistOrder) {
                return;
            }
            if (old_order_no >= new_order_no) {
                for (Object obj : list) {
                    ed = (EntityDef) obj;
                    if (ed.getEntity_key().equals(entity_key)) {
                        continue;
                    }
                    if (ed.getOrder_no() >= old_order_no) {
                        continue;
                    }
                    if (ed.getOrder_no() >= new_order_no) {
                        ed.setOrder_no(ed.getOrder_no() + 1);
                        if (ed.getFun_flag() != 1) {
                            ed.setFun_flag(2);
                        }
                        exist_fields.put(ed.getEntityName(), ed.getOrder_no());
                        change_keys.put(ed.getEntityName(), ed);
                    }
                }
            } else {
                for (Object obj : list) {
                    ed = (EntityDef) obj;
                    if (ed.getEntity_key().equals(entity_key)) {
                        continue;
                    }
                    if (ed.getOrder_no() > new_order_no) {
                        continue;
                    }
                    if (ed.getOrder_no() > old_order_no) {
                        ed.setOrder_no(ed.getOrder_no() - 1);
                        if (ed.getFun_flag() != 1) {
                            ed.setFun_flag(2);
                        }
                        change_keys.put(ed.getEntityName(), ed);
                        exist_fields.put(ed.getEntityName(), ed.getOrder_no());
                    }
                }
            }
        } else {
            change_keys.put(((FieldDef) change_obj).getField_name(), change_obj);
            FieldDef fd;
            String field_key = ((FieldDef) change_obj).getField_key();
            for (Object obj : list) {
                fd = (FieldDef) obj;
                if (fd.getField_key().equals(field_key)) {
                    continue;
                }
                if (!fd.isUsed_flag()) {
                    continue;
                }
                if (fd.getOrder_no() == new_order_no) {
                    isExistOrder = true;
                    break;
                }
            }
            if (!isExistOrder) {
                return;
            }
            if (old_order_no >= new_order_no) {
                for (Object obj : list) {
                    fd = (FieldDef) obj;
                    if (fd.getField_key().equals(field_key)) {
                        continue;
                    }
                    if (!fd.isUsed_flag()) {
                        continue;
                    }
                    if (fd.getOrder_no() >= old_order_no) {
                        continue;
                    }
                    if (fd.getOrder_no() >= new_order_no) {
                        fd.setOrder_no(fd.getOrder_no() + 1);
                        if (fd.getFun_flag() != 1) {
                            fd.setFun_flag(2);
                        }
                        change_keys.put(fd.getField_name(), fd);
                        exist_fields.put(fd.getEntityDef().getEntityName() + "." + fd.getField_name(), fd.getOrder_no());
                    }
                }
            } else {
                for (Object obj : list) {
                    fd = (FieldDef) obj;
                    if (fd.getField_key().equals(field_key)) {
                        continue;
                    }
                    if (!fd.isUsed_flag()) {
                        continue;
                    }
                    if (fd.getOrder_no() > new_order_no) {
                        continue;
                    }
                    if (fd.getOrder_no() > old_order_no) {
                        fd.setOrder_no(fd.getOrder_no() - 1);
                        if (fd.getFun_flag() != 1) {
                            fd.setFun_flag(2);
                        }
                        change_keys.put(fd.getField_name(), fd);
                        exist_fields.put(fd.getEntityDef().getEntityName() + "." + fd.getField_name(), fd.getOrder_no());
                    }
                }
            }
        }
        if (isEntityDef) {
            Enumeration enumt = parent_node.children();
            Object change;
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                EntityDef ed = (EntityDef) node.getUserObject();
                change = change_keys.get(ed.getEntityName());
                if (change != null) {
                    node.setUserObject(change);
                }
            }
            treeModule.updateUI();
        } else {
            Enumeration enumt = null;
            Object change;
            if (parent_node.getUserObject() instanceof EntityDef) {
                enumt = parent_node.getFirstChild().children();
            } else {
                enumt = parent_node.children();
            }
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                FieldDef fd = (FieldDef) node.getUserObject();
                change = change_keys.get(fd.getField_name());
                if (change != null) {
                    node.setUserObject(change);
                }
            }
            treeModule.updateUI();
        }
        SysUtil.sortListByInteger(list, "order_no");
        ftable.setObjects(list);
    }

    protected void delObject() {
        if (treeModule.getSelectionPath() == null || treeModule.getSelectionPath().getLastPathComponent() == moduleTreeModel.getRoot()) {
            return;
        }
        DefaultMutableTreeNode tmp1 = null;
        if (cur_node.getUserObject() instanceof FieldDef) {
            tmp1 = (DefaultMutableTreeNode) cur_node.getParent();
            FieldDef tempFieldDef = (FieldDef) cur_node.getUserObject();
            cur_object = tempFieldDef;
            if (tempFieldDef.getFun_flag() == 3) {
                return;
            } else if (tempFieldDef.getFun_flag() == 1) {
                removeCodeIndex(tempFieldDef.getEntityDef().getEntityName(), tempFieldDef.getField_name());
                DefaultMutableTreeNode tmpNode = cur_node.getPreviousSibling();
                cur_node.removeFromParent();
                cur_node = tmpNode;
                refreshParentState((DefaultMutableTreeNode) tmp1.getParent());
                selectTreeNode(cur_node);
                treeModule.updateUI();
                return;
            } else {
                if (tmp1.getUserObject().toString().equals("已选")) {
                    tempFieldDef.setFun_flag(3);
                    cur_node.setUserObject(tempFieldDef);
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) cur_node.getParent();
                    TempGroup tempGroup = (TempGroup) node.getUserObject();
                    if (tempGroup.getChange_flag() == 0) {
                        tempGroup.setChange_flag(2);
                    }
                    node = (DefaultMutableTreeNode) node.getParent();
                    EntityDef entityDef = (EntityDef) node.getUserObject();
                    if (entityDef.getFun_flag() == 0) {
                        entityDef.setFun_flag(2);
                    }
                    refreshParentState((DefaultMutableTreeNode) tmp1.getParent());
                    selectTreeNode(cur_node);
                    treeModule.updateUI();
                    return;
                } else if (tmp1.getUserObject().toString().equals("备选")) {
                    if (MsgUtil.showNotConfirmDialog("确定要删除:" + tempFieldDef + "吗")) {
                        return;
                    }
                }
            }
        } else {
            EntityDef entityDef = (EntityDef) cur_object;
            if (entityDef.getFun_flag() != 1) {
                if (MsgUtil.showNotConfirmDialog("确定要删除:" + entityDef.getEntityCaption() + "吗")) {
                    return;
                }
            } else {
                return;
            }
        }
        ValidateSQLResult result = SysImpl.delSystemField(cur_object);
        if (result.getResult() == 0) {
            DefaultMutableTreeNode tmpNode = cur_node.getPreviousSibling();
            if (tmpNode == null) {
                tmpNode = (DefaultMutableTreeNode) cur_node.getParent();
            }
            if (cur_object instanceof FieldDef) {
                FieldDef tempFieldDef = (FieldDef) cur_object;
                PublicUtil.removeProperty(tempFieldDef.getEntityDef().getEntityName() + "." + tempFieldDef.getField_name());
                removeCodeIndex(tempFieldDef.getEntityDef().getEntityName(), tempFieldDef.getField_name());
                cur_node.removeFromParent();
                cur_node = tmpNode;
                refreshParentState((DefaultMutableTreeNode) tmp1.getParent());
            } else {
                EntityDef entityDef = (EntityDef) cur_object;
                PublicUtil.removeProperty(entityDef.getEntityName());
                cur_node.removeFromParent();
                cur_node = tmpNode;
                removeCodeIndex("jhr_entity", entityDef.getEntityName());
            }
            selectTreeNode(cur_node);
            treeModule.updateUI();
        } else {
            MsgUtil.showHRDelErrorMsg(result);
        }
    }

    /**
     * 
     * @param node：刷新的依据节点
     * @param refresh_type：刷新类型，0：fielddef;1:tempgroup;2:entity;3:entityclass
     */
    private void refreshTreeNodePosition(DefaultMutableTreeNode node) {
        Enumeration enumt = null;
        DefaultMutableTreeNode parent_node = node;
        if (node.getUserObject() instanceof FieldDef) {
            parent_node = (DefaultMutableTreeNode) node.getParent();
        } else if (node.getUserObject() instanceof EntityDef) {
            parent_node = (DefaultMutableTreeNode) node.getFirstChild();
        }
        enumt = parent_node.children();
        List<DefaultMutableTreeNode> list = new ArrayList<DefaultMutableTreeNode>();
        while (enumt.hasMoreElements()) {
            list.add((DefaultMutableTreeNode) enumt.nextElement());
        }
        Collections.sort(list, new Comparator() {

            @Override
            public int compare(Object arg0, Object arg1) {
                Integer order_no0 = 0;
                Integer order_no1 = 0;
                if (arg0 instanceof EntityDef) {
                    order_no0 = ((EntityDef) ((DefaultMutableTreeNode) arg0).getUserObject()).getOrder_no();
                    order_no1 = ((EntityDef) ((DefaultMutableTreeNode) arg1).getUserObject()).getOrder_no();
                } else if (arg0 instanceof FieldDef) {
                    order_no0 = ((FieldDef) ((DefaultMutableTreeNode) arg0).getUserObject()).getOrder_no();
                    order_no1 = ((FieldDef) ((DefaultMutableTreeNode) arg1).getUserObject()).getOrder_no();
                }
                return order_no0.compareTo(order_no1);
            }
        });
        parent_node.removeAllChildren();
        for (DefaultMutableTreeNode node1 : list) {
            parent_node.add(node1);
        }
    }

    protected void saveObject() {
        //1119-1120  modify by yu 2011-08-09
        ftable.editingStopped();
//        saveChangeObj();
        if (cur_node == null) {
            return;
        }
        cur_object = cur_node.getUserObject();
        DefaultMutableTreeNode c_node = cur_node;
        if (cur_object instanceof FieldDef) {
            FieldDef fieldDef = (FieldDef) cur_object;
            DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) cur_node.getParent().getParent();
            if (fieldDef.getFun_flag() == 0) {
                return;
            } else if (fieldDef.getFun_flag() == 1) {
                if (fieldDef.getEntityDef().getNew_flag() == 1) {
                    c_node = parent_node;
                }
            }
        } else if (cur_object instanceof TempGroup) {
            DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) cur_node.getParent();
            EntityDef entityDef = (EntityDef) parent_node.getUserObject();
            if (entityDef.getNew_flag() == 1) {
                c_node = parent_node;
            }
        } else if (cur_object instanceof EntityDef) {
            EntityDef entityDef = (EntityDef) cur_object;
            if (entityDef.getFun_flag() == 0) {
                return;
            }
        }
        if (c_node == null) {
            return;
        }
        List saveList = new ArrayList();
        List updateList = new ArrayList();
        List<String> delList = new ArrayList<String>();
        if (c_node.getUserObject() instanceof FieldDef) {
            FieldDef fd = (FieldDef) c_node.getUserObject();
            fd.setPym(PinYinMa.ctoE(fd.getField_caption()));
            if (fd.getFun_flag() == 1) {
                saveList.add(fd);
            } else {
                updateList.add(fd);
                if (fd.getFun_flag() == 3) {
                    delList.add(fd.getField_key());
                }
            }
        } else {
            List<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();
            Enumeration enumt = c_node.depthFirstEnumeration();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                if (node.getUserObject() instanceof EntityDef) {
                    if (((EntityDef) node.getUserObject()).getFun_flag() == 0) {
                        continue;
                    }
                    nodes.add(node);
                }
            }
            for (DefaultMutableTreeNode node : nodes) {
                EntityDef ed = (EntityDef) node.getUserObject();
                Enumeration enumt1 = node.breadthFirstEnumeration();
                if (ed.getFun_flag() == 1) {
                    saveList.add(ed);
                    while (enumt1.hasMoreElements()) {
                        DefaultMutableTreeNode child_node = (DefaultMutableTreeNode) enumt1.nextElement();
                        Object obj = child_node.getUserObject();
                        if (obj instanceof FieldDef) {
                            FieldDef fd = (FieldDef) obj;
                            fd.setPym(PinYinMa.ctoE(fd.getField_caption()));
                            saveList.add(fd);
                        }
                    }
                } else {
                    updateList.add(ed);
                    while (enumt1.hasMoreElements()) {
                        DefaultMutableTreeNode child_node = (DefaultMutableTreeNode) enumt1.nextElement();
                        Object obj = child_node.getUserObject();
                        if (obj instanceof FieldDef) {
                            FieldDef fd = (FieldDef) obj;
                            fd.setPym(PinYinMa.ctoE(fd.getField_caption()));
                            if (fd.getFun_flag() == 1) {
                                saveList.add(fd);
                            } else {
                                updateList.add(fd);
                                if (fd.getFun_flag() == 3) {
                                    delList.add(fd.getField_key());
                                }
                            }
                        }
                    }
                }
            }
        }
        ValidateSQLResult vs = SysImpl.saveSystemChange(saveList, updateList, delList);
        if (vs.getResult() == 0) {
            if (c_node.getUserObject() instanceof FieldDef) {
                FieldDef fd = (FieldDef) c_node.getUserObject();
                exist_fields.put(fd.getEntityDef().getEntityName() + "." + fd.getField_name(), fd.getOrder_no());
                if (fd.getFun_flag() == 3) {
                    fd.setUsed_flag(false);
                    fd.setField_mark("自定义备选项");
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) c_node.getParent().getParent();
                    c_node.removeFromParent();
                    ((DefaultMutableTreeNode) node.getChildAfter(node.getFirstChild())).add(c_node);
                }
                fd.setFun_flag(0);
            } else {
                List<DefaultMutableTreeNode> nodes = new ArrayList<DefaultMutableTreeNode>();
                Enumeration enumt = c_node.depthFirstEnumeration();
                while (enumt.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                    if (node.getUserObject() instanceof EntityDef) {
                        if (((EntityDef) node.getUserObject()).getFun_flag() == 0) {
                            continue;
                        }
                        nodes.add(node);
                    }
                }
                for (DefaultMutableTreeNode node : nodes) {
                    EntityDef ed = (EntityDef) node.getUserObject();
                    ed.setFun_flag(0);
                    exist_fields.put(ed.getEntityName(), ed.getOrder_no());
                    Enumeration enumt1 = node.breadthFirstEnumeration();
                    if (ed.getFun_flag() == 1) {
                        while (enumt1.hasMoreElements()) {
                            DefaultMutableTreeNode child_node = (DefaultMutableTreeNode) enumt1.nextElement();
                            Object obj = child_node.getUserObject();
                            if (obj instanceof TempGroup) {
                                ((TempGroup) obj).setChange_flag(0);
                            } else if (obj instanceof FieldDef) {
                                FieldDef fd = (FieldDef) obj;
                                fd.setFun_flag(0);
                                exist_fields.put(fd.getEntityDef().getEntityName() + "." + fd.getField_name(), fd.getOrder_no());
                            }
                        }
                    } else {
                        while (enumt1.hasMoreElements()) {
                            DefaultMutableTreeNode child_node = (DefaultMutableTreeNode) enumt1.nextElement();
                            Object obj = child_node.getUserObject();
                            if (obj instanceof TempGroup) {
                                ((TempGroup) obj).setChange_flag(0);
                            } else if (obj instanceof FieldDef) {
                                FieldDef fd = (FieldDef) obj;
                                exist_fields.put(fd.getEntityDef().getEntityName() + "." + fd.getField_name(), fd.getOrder_no());
                                if (fd.getFun_flag() == 3) {
                                    DefaultMutableTreeNode p_node = (DefaultMutableTreeNode) child_node.getParent().getParent();
                                    child_node.removeFromParent();
                                    ((DefaultMutableTreeNode) p_node.getChildAfter(p_node.getFirstChild())).add(child_node);
                                }
                                fd.setFun_flag(0);
                            }
                        }
                    }
                }
            }
            refreshTreeNodePosition(c_node);
            refreshParentState(c_node);
            treeModule.updateUI();
            MsgUtil.showHRSaveSuccessMsg(this);
        } else {
            MsgUtil.showHRSaveErrorMsg(vs);
        }
    }
    //刷新父节点或表及节点状态

    private void refreshParentState(DefaultMutableTreeNode parent_node) {
        if (parent_node == null || parent_node.getUserObject() == null) {
            return;
        }
        if (parent_node.getUserObject() instanceof FieldDef) {
            parent_node = (DefaultMutableTreeNode) parent_node.getParent();
        }
        Enumeration enumt = parent_node.breadthFirstEnumeration();
        boolean save_flag = true;
        while (enumt.hasMoreElements()) {
            DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
            Object obj = node.getUserObject();
            if (obj instanceof FieldDef) {
                FieldDef fd = (FieldDef) obj;
                if (fd.getFun_flag() != 0) {
                    save_flag = false;
                }
            }
        }
        if (save_flag) {
            if (parent_node.getUserObject() instanceof EntityDef) {
                EntityDef ed = (EntityDef) parent_node.getUserObject();
                ed.setFun_flag(0);
                TempGroup tg = (TempGroup) ((DefaultMutableTreeNode) parent_node.getFirstChild()).getUserObject();
                tg.setChange_flag(0);
            } else if (parent_node.getUserObject() instanceof TempGroup) {
                EntityDef ed = (EntityDef) ((DefaultMutableTreeNode) parent_node.getParent()).getUserObject();
                ed.setFun_flag(0);
                TempGroup tg = (TempGroup) parent_node.getUserObject();
                tg.setChange_flag(0);
            }
        }
        treeModule.updateUI();
    }

    /**
     * 向表及字段编码hashtable中的对应于key的HashSet中加入value值
     * @param key：匹配符
     * @param value：加入值
     */
    private void addCodeIndex(String key, String value) {
        HashSet<String> exist_codes = field_codes.get(key);
        if (exist_codes == null) {
            exist_codes = new HashSet<String>();
        }
        exist_codes.add(value.toUpperCase());
        field_codes.put(key, exist_codes);
    }

    /**
     * 向表及字段编码hashtable中的对应于key的HashSet中移除value值
     * @param key：匹配符
     * @param value：移除值
     */
    private void removeCodeIndex(String key, String value) {
        HashSet<String> exist_codes = field_codes.get(key);
        if (exist_codes != null && exist_codes.contains(value.toUpperCase())) {
            exist_codes.remove(value.toUpperCase());
            field_codes.put(key, exist_codes);
        }
    }

    protected void cancelObject() {
        if (treeModule.getSelectionPath() == null || treeModule.getSelectionPath().getLastPathComponent() == moduleTreeModel.getRoot()) {
            return;
        }
        btnCancel.setEnabled(false);
        DefaultMutableTreeNode tmpNode = null;
        if (cur_object instanceof EntityDef) {
            EntityDef entityDef = (EntityDef) cur_object;
            if (entityDef.getNew_flag() == 1) {
                tmpNode = cur_node.getPreviousSibling();
                removeCodeIndex("jhr_entity", entityDef.getEntityName());
                cur_node.removeFromParent();
            } else {
                entityDef = (EntityDef) CommUtil.fetchEntityBy("from EntityDef ed join fetch ed.fieldDefs where ed.entity_key='" + entityDef.getEntity_key() + "'");
                entityDef.setFun_flag(0);
                cur_node.setUserObject(entityDef);
                Enumeration enumt = cur_node.breadthFirstEnumeration();
                while (enumt.hasMoreElements()) {
                    DefaultMutableTreeNode temp = (DefaultMutableTreeNode) enumt.nextElement();
                    Object obj = temp.getUserObject();
                    if (obj instanceof TempGroup) {
                        ((TempGroup) obj).setChange_flag(0);
                    } else if (obj instanceof FieldDef) {
                        FieldDef fieldDef = (FieldDef) obj;
                        if (fieldDef.getNew_flag() == 1) {
                            removeCodeIndex(fieldDef.getEntityDef().getEntityName(), fieldDef.getField_name());
                            temp.removeFromParent();
                        } else {
                            if (fieldDef.getFun_flag() == 2 || fieldDef.getFun_flag() == 3) {
                                fieldDef = (FieldDef) CommUtil.fetchEntityBy("from FieldDef fd join fetch fd.entityDef where fd.field_key='" + fieldDef.getField_key() + "'");
                                fieldDef.setFun_flag(0);
                                temp.setUserObject(fieldDef);
                            }
                        }
                    }
                }
            }
        } else if (cur_object instanceof FieldDef) {
            FieldDef fieldDef = (FieldDef) cur_object;
            if (fieldDef.getNew_flag() == 1) {
                if (fieldDef.getField_mark().endsWith("固定项")) {
                    return;
                }
                removeCodeIndex(fieldDef.getEntityDef().getEntityName(), fieldDef.getField_name());
                tmpNode = cur_node.getNextNode();
                cur_node.removeFromParent();
            } else {
                fieldDef = (FieldDef) CommUtil.fetchEntityBy("from FieldDef fd join fetch fd.entityDef where fd.field_key='" + fieldDef.getField_key() + "'");
                fieldDef.setFun_flag(0);
                cur_node.setUserObject(fieldDef);
                beanPanel.setBean(fieldDef);
                DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) cur_node.getParent();
                Object parent_obj = parent_node.getUserObject();
                TempGroup parent_group = (TempGroup) parent_obj;
                parent_group.setChange_flag(0);
                Enumeration enumt = parent_node.children();
                while (enumt.hasMoreElements()) {
                    if (((FieldDef) ((DefaultMutableTreeNode) enumt.nextElement()).getUserObject()).getFun_flag() != 0) {
                        parent_group.setChange_flag(2);
                        break;
                    }
                }
                parent_node.setUserObject(parent_group);
                parent_node = (DefaultMutableTreeNode) parent_node.getParent();
                Enumeration enumt1 = parent_node.children();
                EntityDef parent_entityDef = (EntityDef) parent_node.getUserObject();
                parent_entityDef.setFun_flag(0);
                while (enumt1.hasMoreElements()) {
                    if (((TempGroup) ((DefaultMutableTreeNode) enumt1.nextElement()).getUserObject()).getChange_flag() != 0) {
                        parent_entityDef.setFun_flag(2);
                        break;
                    }
                }
                parent_node.setUserObject(parent_entityDef);
            }
        } else if (cur_object instanceof TempGroup) {
            ((TempGroup) cur_object).setChange_flag(0);
            Enumeration enumt = cur_node.children();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode temp = (DefaultMutableTreeNode) enumt.nextElement();
                Object obj = temp.getUserObject();
                if (obj instanceof FieldDef) {
                    FieldDef fieldDef = (FieldDef) obj;
                    if (fieldDef.getNew_flag() == 1) {
                        removeCodeIndex(fieldDef.getEntityDef().getEntityName(), fieldDef.getField_name());
                        temp.removeFromParent();
                    } else {
                        if (fieldDef.getFun_flag() == 2 || fieldDef.getFun_flag() == 3) {
                            fieldDef = (FieldDef) CommUtil.fetchEntityBy("from FieldDef fd join fetch fd.entityDef where fd.field_key='" + fieldDef.getField_key() + "'");
                            fieldDef.setFun_flag(0);
                            temp.setUserObject(fieldDef);
                        }
                    }
                }
            }
            DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) cur_node.getParent();
            EntityDef parent_entity = (EntityDef) parent_node.getUserObject();
            parent_entity.setFun_flag(0);
            Enumeration enumt1 = parent_node.children();
            while (enumt1.hasMoreElements()) {
                if (((TempGroup) ((DefaultMutableTreeNode) enumt1.nextElement()).getUserObject()).getChange_flag() != 0) {
                    parent_entity.setFun_flag(2);
                    break;
                }
            }
            parent_node.setUserObject(parent_entity);
        }
        if (tmpNode != null) {
            selectTreeNode(tmpNode);
        }
        operatePanel.updateUI();
        treeModule.updateUI();
        setEditState();
    }

    /**
     * 该方法用于通过传入相关参数生成字段
     * @param newFieldName：生成的字段名
     * @param orderNo：字段顺序号
     * @param entityDef：所属表
     * @return 新的字段
     */
    private FieldDef createNewFieldDef(String newFieldName, int orderNo, EntityDef entityDef) {
        newFieldName = newFieldName.substring(0, 1).toLowerCase() + newFieldName.substring(1);
        List<String> fieldDefs = new ArrayList<String>();
        fieldDefs.add("field_name");
        fieldDefs.add("field_caption");
        FieldDef fieldDef = (FieldDef) UtilTool.createUIDEntity(FieldDef.class);
        fieldDef.setField_name(newFieldName);
        fieldDef.setField_caption(newFieldName);
        fieldDef.setOrder_no(orderNo);
        fieldDef.setField_mark("自定义已选项");
        fieldDef.setFun_flag(1);
        final EntityDef tmpEntity = entityDef;
        boolean result = BeanPanel.edit(fieldDef, fieldDefs, new ValidateEntity() {

            @Override
            public boolean isEntityValidate(Object obj) {
                FieldDef fd = (FieldDef) obj;
                String msg = "";
                if (fd.getField_name() == null || fd.getField_name().length() == 0) {
                    msg = "字段名不可为空";
                } else if (fd.getField_caption() == null || fd.getField_caption().equals("")) {
                    msg = "字段描述不能为空";
                } else {
                    HashSet<String> exist_fields1 = field_codes.get(tmpEntity.getEntityName());
                    if (exist_fields1 != null && exist_fields1.contains(fd.getField_name().toUpperCase())) {
                        msg = "该字段已存在";
                    }
                }
                if (!msg.equals("")) {
                    MsgUtil.showErrorMsg(msg);
                    return false;
                }
                return true;
            }
        });
        if (result) {
            fieldDef.setField_name(fieldDef.getField_name().toLowerCase());
            addCodeIndex(tmpEntity.getEntityName(), fieldDef.getField_name());
            return fieldDef;
        }
        return null;
    }

    /**
     * 获取新的表或字段名方法
     * @param compare_val：比较符，用于从field_codes中获取已存在的表或字段名，从而比对不会产生已存在的表或字段名
     * @param pre_val：前缀符，对于表名则传入该业务的表名前缀，对于字段名则传入表名
     * @param start_num:起始编码位置，比如人员附表要求从A10开始，而表及字段名均采用16进制，故start_num为10
     * @return：新的表或字段名
     */
    private String getNewEntityOrFieldName(String compare_val, String pre_val, int start_num) {
        String tmp_code = null;
        HashSet<String> entity_codes = field_codes.get(compare_val);
        tmp_code = SysUtil.getNewCode(pre_val, entity_codes, 2, start_num);
        if (!compare_val.equals("jhr_entity")) {
            tmp_code = tmp_code.toLowerCase();
        }
        return tmp_code;
    }

    protected void addObject() {
        ComponentUtil.setCompEnable(this, btnCancel, true);
        Object entity = null;
        DefaultMutableTreeNode parent;
        int order_no = 0;
        String tmp_code;
        String tmp = "";
        if (treeModule.getSelectionPath() == null || treeModule.getSelectionPath().getLastPathComponent() == moduleTreeModel.getRoot()) {
            return;
        }
        HashSet<Integer> orders = new HashSet<Integer>();
        parent = (DefaultMutableTreeNode) treeModule.getSelectionPath().getLastPathComponent();
        if (parent.getUserObject() instanceof ModuleInfo) {
            return;
        } else if (parent.getUserObject() instanceof EntityClass) {
            EntityClass ec = (EntityClass) parent.getUserObject();
            if (!ec.getModify_flag()) {
                return;
            }
            if (ec.getPreEntityName() == null || ec.getPreEntityName().equals("")) {
                JOptionPane.showMessageDialog(null, "为保证数据库规范性，请设置该业务表名前缀后再建表!");
                return;
            }
            order_no = parent.getChildCount();
            Enumeration enumt = parent.children();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                orders.add(((EntityDef) node.getUserObject()).getOrder_no());
            }
            EntityDef entityDef = (EntityDef) UtilTool.createUIDEntity(EntityDef.class);
            entityDef.setEntityClass(ec);
            tmp = getNewEntityOrFieldName("jhr_entity", ec.getPreEntityName(), ec.getStart_num());
            if (tmp == null) {
                return;
            }
            entityDef.setEntityName(tmp);
            entityDef.setEntityCaption(tmp);
            entityDef.setOrder_no(SysUtil.getOrder_no(orders, order_no));
            entityDef.setFun_flag(1);
            entity = entityDef;
        } else {
            EntityDef ed = null;
            if (parent.getUserObject() instanceof EntityDef) {
            } else if (parent.getUserObject() instanceof FieldDef) {
                parent = (DefaultMutableTreeNode) parent.getParent().getParent();
            } else if (parent.getUserObject() instanceof TempGroup) {
                parent = (DefaultMutableTreeNode) parent.getParent();
            }
            ed = (EntityDef) parent.getUserObject();
            if (ed == null) {
                return;
            }
            order_no = parent.getFirstChild().getChildCount();
            Enumeration enumt = parent.getFirstChild().children();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                orders.add(((FieldDef) node.getUserObject()).getOrder_no());
            }
            order_no = SysUtil.getOrder_no(orders, order_no);
            tmp_code = getNewEntityOrFieldName(ed.getEntityName(), ed.getEntityName(), 1);
            if (tmp_code == null) {
                return;
            }
            tmp_code = tmp_code.toLowerCase();
            entity = createNewFieldDef(tmp_code, order_no, ed);
        }
        if (entity != null && entity instanceof FieldDef) {
            ((FieldDef) entity).setEntityDef((EntityDef) parent.getUserObject());
            ((FieldDef) entity).setFun_flag(1);
            DefaultMutableTreeNode tmp1 = new DefaultMutableTreeNode(entity);
            ((DefaultMutableTreeNode) parent.getFirstChild()).add(tmp1);
            if (((EntityDef) parent.getUserObject()).getFun_flag() != 1 && ((EntityDef) parent.getUserObject()).getFun_flag() != 2) {
                ((EntityDef) parent.getUserObject()).setFun_flag(2);
            }
            selectTreeNode(tmp1);
            changeObject(beanPanel.getBean(), "field_type", ((FieldDef) entity).getField_type());
            return;
        }
        if (entity != null) {
            ValidateEntity validateEntity = new ValidateEntity() {

                @Override
                public boolean isEntityValidate(Object entity) {
                    if (entity instanceof EntityDef) {
                        EntityDef ed = (EntityDef) entity;
                        if (ed.getEntityName() == null || ed.getEntityName().length() == 0 || !(ed.getEntityName().charAt(0) >= 'A' && ed.getEntityName().charAt(0) <= 'Z')) {
                            JOptionPane.showMessageDialog(null, "表名第一个字符必须是大写字母", // message
                                    "错误", // title
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        if (ed.getEntityCaption() == null || ed.getEntityCaption().equals("")) {
                            JOptionPane.showMessageDialog(null, "请输入表的中文名", // message
                                    "错误", // title
                                    JOptionPane.ERROR_MESSAGE);
                            return false;
                        }
                        HashSet<String> exist_entities = field_codes.get("jhr_entity");
                        if (exist_entities != null && exist_entities.contains(ed.getEntityName())) {
                            JOptionPane.showMessageDialog(null, "表名已经存在");
                            return false;
                        }
                        if (cur_object instanceof EntityClass) {
                            Enumeration<DefaultMutableTreeNode> e = cur_node.children();
                            while (e.hasMoreElements()) {
                                DefaultMutableTreeNode dmt = e.nextElement();
                                if (dmt.getUserObject() instanceof EntityDef) {
                                    EntityDef ed2 = (EntityDef) dmt.getUserObject();
                                    if (ed2.getEntityCaption().equals(ed.getEntityCaption())) {
                                        JOptionPane.showMessageDialog(null, "相同的表描述已经存在");
                                        return false;
                                    }
                                }
                            }
                        }
                    }
                    return true;
                }
            };
            if (BeanPanel.edit(entity, validateEntity)) {
                if (entity instanceof EntityDef) {
                    ((EntityDef) entity).setEntityClass((EntityClass) parent.getUserObject());
                    addCodeIndex("jhr_entity", ((EntityDef) entity).getEntityName());
                    ((EntityDef) entity).setFun_flag(1);
                    DefaultMutableTreeNode tmp1 = new DefaultMutableTreeNode(entity);
                    TempGroup group1 = new TempGroup();
                    group1.setGroup_name("已选");
                    group1.setChange_flag(0);
                    DefaultMutableTreeNode tmp2 = new DefaultMutableTreeNode(group1);
                    String super_entity_name = ((EntityDef) entity).getEntityClass().getSuper_class();
                    if (super_entity_name != null) {
                        Class super_class = null;
                        try {
                            super_class = Class.forName(super_entity_name);
                        } catch (Exception e) {
                            log.error(e);
                        }
                        if (super_class != null) {
                            List<TempFieldInfo> super_infos = EntityBuilder.getCommFieldInfoListOf(super_class, EntityBuilder.COMM_FIELD_VISIBLE);
                            createNewFieldDefForSuperEntity(super_infos, super_class, entity, tmp2);
                        }
                    } else if ("CLASS".equals(((EntityClass) parent.getUserObject()).getEntityType_code())) {
                        List<TempFieldInfo> super_infos = new ArrayList<TempFieldInfo>();
                        for (TempFieldInfo tfi : EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_ALL)) {
                            if (tfi.getField_name().equals("a01_key")) {
                                super_infos.add(tfi);
                                break;
                            }
                        }
                        createNewFieldDefForSuperEntity(super_infos, A01.class, entity, tmp2);
                    }
                    TempGroup group2 = new TempGroup();
                    group2.setGroup_name("备选");
                    group2.setChange_flag(0);
                    DefaultMutableTreeNode tmp3 = new DefaultMutableTreeNode(group2);
                    tmp1.add(tmp2);
                    tmp1.add(tmp3);
                    parent.add(tmp1);
                    selectTreeNode(tmp1);
                }
            }
        }
    }

    private void createNewFieldDefForSuperEntity(List<TempFieldInfo> super_infos, Class super_class, Object entity, DefaultMutableTreeNode parent_node) {
        int i = 0;//用于生成排序号
        for (TempFieldInfo tfi : super_infos) {
            try {
                Field field = super_class.getField(tfi.getField_name());
                if (field.getGenericType().toString().contains("entity")) {
                    continue;
                }
            } catch (NoSuchFieldException ex) {
                log.error(ex);
                continue;
            } catch (SecurityException ex) {
                log.error(ex);
                continue;
            }
            FieldDef fd = (FieldDef) UtilTool.createUIDEntity(FieldDef.class);
            FieldAnnotation fa = (FieldAnnotation) super_class.getAnnotation(FieldAnnotation.class);
            if (fa != null) {
                fd.setEditableedit(fa.editableWhenEdit());
                fd.setEditable(fa.isEditable());
                fd.setEditablenew(fa.editableWhenNew());
                fd.setVisible(fa.visible());
                fd.setVisibleedit(fa.visibleWhenEdit());
                fd.setVisiblenew(fa.visibleWhenNew());
            }
            fd.setField_name(tfi.getField_name());
            fd.setField_caption(tfi.getCaption_name());
            fd.setEntityDef((EntityDef) entity);
            fd.setField_mark("系统固定项");
            fd.setFun_flag(1);
            fd.setField_type(tfi.getField_type());
            fd.setOrder_no(i);
            i++;
            DefaultMutableTreeNode node = new DefaultMutableTreeNode(fd);
            parent_node.add(node);
            addCodeIndex(((EntityDef) entity).getEntityName(), fd.getField_name());
        }
    }

    private void selectTreeNode(DefaultMutableTreeNode node) {
        treeModule.getSelectionModel().clearSelection();
        cur_node = node;
        treeModule.addSelectionPath(new TreePath(cur_node.getPath()));
        treeModule.updateUI();
    }

    protected void selectNode(DefaultMutableTreeNode node) {
        Object data = node.getUserObject();
        if (data == null) {
            return;
        }
        setFunctionRight(data);
        operatePanel.removeAll();
        operatePanel.add(toolbar, BorderLayout.NORTH);
        if (data instanceof String) {
            cur_entityDef = null;
            ftable = new FTable(ModuleInfo.class, false, false);
            List list = new ArrayList();
            Enumeration enumt = node.children();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) enumt.nextElement();
                ModuleInfo moduleInfo = (ModuleInfo) tmpNode.getUserObject();
                list.add(moduleInfo);
            }
            ftable.setObjects(list);
            operatePanel.add(new JScrollPane(ftable), BorderLayout.CENTER);
        } else if (data instanceof ModuleInfo) {
            cur_entityDef = null;
            ftable = new FTable(EntityClass.class, false, false);
            List list = new ArrayList();
            Enumeration enumt = node.children();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) enumt.nextElement();
                EntityClass moduleInfo = (EntityClass) tmpNode.getUserObject();
                list.add(moduleInfo);
            }
            ftable.setObjects(list);
            operatePanel.add(new JScrollPane(ftable), BorderLayout.CENTER);
        } else if (data instanceof EntityClass) {
            cur_entityDef = null;
            ftable = new FTable(EntityDef.class, false, false);
            ftable.addRowChangeListner(change_listener);
            List list = new ArrayList();
            Enumeration enumt = node.children();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) enumt.nextElement();
                EntityDef moduleInfo = (EntityDef) tmpNode.getUserObject();
                list.add(moduleInfo);
            }
            ftable.setObjects(list);
            ftable.setEditable(true);
            operatePanel.add(new JScrollPane(ftable), BorderLayout.CENTER);
        } else if (data instanceof EntityDef) {
            cur_entityDef = (EntityDef) data;
            disabled_fields.remove("order_no");
            btnCancel.setEnabled(UserContext.hasFunctionRight("SysRebuild.btnCancel") && ((EntityDef) data).getFun_flag() != 0);
            ftable = new FTable(FieldDef.class, false, false);
            ftable.addRowChangeListner(change_listener);
            ftable.setFields(fieldDef_fields);
            ftable.setDisable_fields(disabled_fields);
            ftable.setRight_allow_flag(true);
            ftable.removeItemByCodes("query;order;sum;replace");
            List list = new ArrayList();
            Enumeration enumt = node.breadthFirstEnumeration();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) enumt.nextElement();
                if (tmpNode.getUserObject() instanceof FieldDef) {
                    FieldDef moduleInfo = (FieldDef) tmpNode.getUserObject();
                    list.add(moduleInfo);
                }
            }
            ftable.setObjects(list);
            ftable.setEditable(true);
            operatePanel.add(new JScrollPane(ftable), BorderLayout.CENTER);
        } else if (data instanceof TempGroup) {
            cur_entityDef = (EntityDef) ((DefaultMutableTreeNode) cur_node.getParent()).getUserObject();
            disabled_fields.remove("order_no");
            ftable = new FTable(FieldDef.class, false, false);
            ftable.setFields(fieldDef_fields);
            ftable.addRowChangeListner(change_listener);
            ftable.setRight_allow_flag(true);
            ftable.removeItemByCodes("query;order;sum;replace");
            List list = new ArrayList();
            Enumeration enumt = node.depthFirstEnumeration();
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmpNode = (DefaultMutableTreeNode) enumt.nextElement();
                if (tmpNode.getUserObject() instanceof FieldDef) {
                    FieldDef moduleInfo = (FieldDef) tmpNode.getUserObject();
                    list.add(moduleInfo);
                }
            }
            ftable.setObjects(list);
            ftable.setEditable(true);
            ftable.setDisable_fields(disabled_fields);
            operatePanel.add(new JScrollPane(ftable), BorderLayout.CENTER);
        } else {
            cur_entityDef = ((FieldDef) data).getEntityDef();
            btnCancel.setEnabled(UserContext.hasFunctionRight("SysRebuild.btnCancel") && ((FieldDef) data).getFun_flag() != 0);
            beanPanel.setBean(cur_object);
            setEditState();
            beanPanel.setEditable(true);
            BeanPanel.clearObjHint_dataByEntityName("FieldDef");
            beanPanel.bind();
            beanPanel.getAdapter().addBeanPropertyChangeListener(propertyChangeListener);
            operatePanel.add(new JScrollPane(beanPanel), BorderLayout.CENTER);
        }
        ftable.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() < 2) {
                    return;
                }
                Object obj = ftable.getCurrentRow();

                Enumeration enumt = cur_node.children();
                if (obj instanceof FieldDef) {
                    enumt = cur_node.breadthFirstEnumeration();
                }
                DefaultMutableTreeNode node = getSelectNode(obj, enumt);
                if (node != null) {
                    selectTreeNode(node);
                }
            }
        });
        operatePanel.updateUI();
    }

    private DefaultMutableTreeNode getSelectNode(Object obj, Enumeration enumt) {
        DefaultMutableTreeNode node = null;
        if (obj instanceof ModuleInfo) {
            ModuleInfo mi = (ModuleInfo) obj;
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) enumt.nextElement();
                if (tmp.getUserObject() instanceof ModuleInfo) {
                    if (((ModuleInfo) tmp.getUserObject()).getModule_key().equals(mi.getModule_key())) {
                        node = tmp;
                    }
                }
            }
        } else if (obj instanceof EntityClass) {
            EntityClass ec = (EntityClass) obj;
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) enumt.nextElement();
                if (tmp.getUserObject() instanceof EntityClass) {
                    if (((EntityClass) tmp.getUserObject()).getEntityClass_key().equals(ec.getEntityClass_key())) {
                        node = tmp;
                    }
                }
            }
        } else if (obj instanceof EntityDef) {
            EntityDef ed = (EntityDef) obj;
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) enumt.nextElement();
                if (tmp.getUserObject() instanceof EntityDef) {
                    if (((EntityDef) tmp.getUserObject()).getEntity_key().equals(ed.getEntity_key())) {
                        node = tmp;
                    }
                }
            }
        } else if (obj instanceof FieldDef) {
            FieldDef fd = (FieldDef) obj;
            while (enumt.hasMoreElements()) {
                DefaultMutableTreeNode tmp = (DefaultMutableTreeNode) enumt.nextElement();
                if (tmp.getUserObject() instanceof FieldDef) {
                    if (((FieldDef) tmp.getUserObject()).getField_key().equals(fd.getField_key())) {
                        node = tmp;
                    }
                }
            }
        }
        return node;
    }
    private DefaultMutableTreeNode the_node_to_move;

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

    class DragAndDropDragGestureListener implements DragGestureListener {

        @Override
        public void dragGestureRecognized(DragGestureEvent dge) {
            // 将数据存储到Transferable中，然后通知组件开始调用startDrag()初始化
            JTree tree = (JTree) dge.getComponent();
            TreePath path = tree.getSelectionPath();
            if (path != null) {
                DefaultMutableTreeNode node_to_move = (DefaultMutableTreeNode) path.getLastPathComponent();
                if (!(node_to_move.getUserObject() instanceof FieldDef || node_to_move.getUserObject() instanceof EntityDef)) {
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

        @SuppressWarnings("unchecked")
        private void moveNode(DefaultMutableTreeNode src_node, DefaultMutableTreeNode dst_node) {
            if (src_node == null || dst_node == null) {
                return;
            }
            src_node = the_node_to_move;
            if ((src_node.getUserObject() instanceof EntityDef)
                    && (dst_node.getUserObject() instanceof EntityDef)) {
                // 如果是拖动实体
                changeEntityOrder(src_node, dst_node);
                treeModule.updateUI();
                return;
            }

            // 只允许拖动字段
            if (!(src_node.getUserObject() instanceof FieldDef)) {
                return;
            }

            if (src_node == dst_node) {
                return;
            }

            if (src_node.getParent() == dst_node) {
                return;
            }

            if (dst_node.getLevel() < src_node.getLevel() - 1) {
                return;
            }
            if (src_node.getParent() != dst_node.getParent()) {
                return;
            }
            EntityDef src_entityDef = (EntityDef) ((DefaultMutableTreeNode) src_node.getParent().getParent()).getUserObject();
            EntityDef dst_entityDef = null;
            if (dst_node.getLevel() == src_node.getLevel()) {
                dst_entityDef = (EntityDef) ((DefaultMutableTreeNode) dst_node.getParent().getParent()).getUserObject();
            } else {
                dst_entityDef = (EntityDef) ((DefaultMutableTreeNode) dst_node.getParent()).getUserObject();
            }
            // 如果不是属于同一个表的字段，则退出
            if (!src_entityDef.getEntity_key().equals(dst_entityDef.getEntity_key())) {
                return;
            }
            // 需要重新设置顺序的父节点
            DefaultMutableTreeNode tmp_node1 = (DefaultMutableTreeNode) src_node.getParent();
            DefaultMutableTreeNode tmp_node2 = null;
            if (dst_node.getLevel() == src_node.getLevel()) {
                src_node.removeFromParent();
                moduleTreeModel.insertNodeInto(the_node_to_move, (MutableTreeNode) dst_node.getParent(), dst_node.getParent().getIndex(dst_node));
                tmp_node2 = (DefaultMutableTreeNode) dst_node.getParent();
            } else {
                src_node.removeFromParent();
                dst_node.add(src_node);
                tmp_node2 = dst_node;
            }

            ((FieldDef) src_node.getUserObject()).setUsed_flag(src_node.getParent().toString().equals("已选"));
            adjustOrder(tmp_node1);
            if (tmp_node1 != tmp_node2) {
                adjustOrder(tmp_node2);
            }
            treeModule.updateUI();
        }

        private void changeEntityOrder(DefaultMutableTreeNode src_node, DefaultMutableTreeNode dst_node) {
            if (dst_node == null) {
                return;
            }
            src_node.removeFromParent();
            moduleTreeModel.insertNodeInto(src_node, (MutableTreeNode) dst_node.getParent(), dst_node.getParent().getIndex(dst_node));
            DefaultMutableTreeNode parent_node = (DefaultMutableTreeNode) dst_node.getParent();
            for (int i = 0; i < parent_node.getChildCount(); i++) {
                DefaultMutableTreeNode tmp_node = (DefaultMutableTreeNode) parent_node.getChildAt(i);
                EntityDef entityDef = (EntityDef) tmp_node.getUserObject();
                entityDef.setOrder_no(i);
                if (entityDef.getFun_flag() == 0) {
                    entityDef.setFun_flag(2);
                }
            }
        }

        private void adjustOrder(DefaultMutableTreeNode parent_node) {
            for (int i = 0; i < parent_node.getChildCount(); i++) {
                DefaultMutableTreeNode tmp_node = (DefaultMutableTreeNode) parent_node.getChildAt(i);
                FieldDef fieldDef = (FieldDef) tmp_node.getUserObject();
                fieldDef.setOrder_no(i);
                if (fieldDef.getFun_flag() == 0) {
                    fieldDef.setFun_flag(2);
                }
            }
        }
    }
}
