/*
 * PersonContainer.java
 *
 * Created on 2008年9月19日, 上午9:18
 */
package org.jhrcore.client.personnel.comm;

import com.foundercy.pf.control.listener.IPickPopupListener;
import com.foundercy.pf.control.table.FTable;
import javax.swing.JPopupMenu;
import javax.swing.event.ListSelectionEvent;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTabbedPane;
import javax.swing.event.ListSelectionListener;
import org.jhrcore.client.BaseMainFrame;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.msg.emp.EmpMngMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptSelectPanel;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.JCheckBoxList;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.util.ClipboardUtil;
import org.jhrcore.util.ImageUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author  xpchild
 */
public class PersonContainer extends JFrame implements IModuleCode {

    /** Creates new form PersonContainer */
    private static PersonContainer personContainer = null;
    private FTable personTablePanel;
    private JMenuItem miDel = new JMenuItem("删  除");
    private JMenuItem miPaste = new JMenuItem("粘贴复制人员");
    private JMenuItem miExcel = new JMenuItem("导入Excel");
    private A01 cur_person = null;
    private static boolean show_flag = false;
    //用于记录人员表允许显示的所有字段
    private List<TempFieldInfo> person_all_fields = new ArrayList<TempFieldInfo>();
    //用于记录人员表默认显示字段
    private List<TempFieldInfo> person_default_fields = new ArrayList<TempFieldInfo>();
    //用于显示记录数大于1的搜索结果
    private FTable tmp_table = new FTable(A01.class, true, false, false, "PersonModelDialog");
    private int max_size = 800;// 最大允许记录数
    private String matchCode = "PersonContainer.matchfield";
    private String matchFields = ConfigManager.getConfigManager().getStringFromProperty(matchCode);
    private String module_code = "EmpMng.btnContainer";
    private DeptSelectPanel deptSelectPanel;
    private JTabbedPane jtp = new JTabbedPane();

    public static PersonContainer getPersonContainer() {
        if (personContainer == null) {
            personContainer = new PersonContainer();
        }
        show_flag = personContainer.jcbShowContiner.isSelected();
        return personContainer;
    }

    public PersonContainer() {
        this.setTitle("人员容器");
        this.setIconImage(ImageUtil.getIconImage());
        initComponents();
        init();
        setEvents();
    }

    public FTable getFTable() {
        return personTablePanel;
    }

    private void init() {
        this.setAlwaysOnTop(true);
        Dimension screenSize = this.getToolkit().getScreenSize();
        this.setLocation(this.getLocation().x, (screenSize.height - this.getHeight()) / 2);
        EntityBuilder.buildInfo(DeptCode.class, person_all_fields, person_default_fields, "deptCode");
        EntityBuilder.buildInfo(A01.class, person_all_fields, person_default_fields, "");
        personTablePanel = new FTable(A01.class, true, false, false, module_code);
        personTablePanel.setAll_fields(person_all_fields, person_default_fields, module_code);
        personTablePanel.setRight_allow_flag(true);
        personTablePanel.removeAllFunItems();
        personTablePanel.addPickPopupListener(new IPickPopupListener() {

            @Override
            public void addMenuItem(JPopupMenu pp) {
                pp.add(miDel);
                pp.add(miPaste);
                pp.add(miExcel);
            }
        });
        personTablePanel.setRight_allow_flag(true);
        personTablePanel.setPreferredSize(new Dimension(pnlMain.getWidth() - 20, pnlMain.getHeight() - 10));
        pnlMain.setLayout(new BorderLayout());
        deptSelectPanel = new DeptSelectPanel(UserContext.getDepts(false), null, TreeSelectMod.nodeCheckMod);
        jtp.add("人员信息", personTablePanel);
        pnlMain.add(jtp, BorderLayout.CENTER);
        btnShowDept.setForeground(Color.BLUE);
        btnShowDept.setOpaque(true);
        tmp_table.setRight_allow_flag(true);
        tmp_table.setAll_fields(person_all_fields, person_default_fields, new ArrayList(), "PersonModelDialog");
        tmp_table.removeAllFunItems();
        matchFields = SysUtil.objToStr(matchFields);
        matchFields = matchFields.equals("") ? "a0190;a0101;pydm" : matchFields;
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        pnlMain = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        btnAdd = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        jcbShowContiner = new javax.swing.JCheckBox();
        btnShowDept = new javax.swing.JToggleButton();
        lblFieldSet = new javax.swing.JLabel();
        jcbUseDept = new javax.swing.JCheckBox();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setCursor(new java.awt.Cursor(java.awt.Cursor.DEFAULT_CURSOR));

        pnlMain.setLayout(new java.awt.BorderLayout());

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("输入人员编码或姓名或拼音码"));

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add.png"))); // NOI18N

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/del.png"))); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 35, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jPanel3.setPreferredSize(new java.awt.Dimension(231, 70));

        jcbShowContiner.setText("显示容器内人员信息");

        btnShowDept.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/link_person.png"))); // NOI18N
        btnShowDept.setBorderPainted(false);
        btnShowDept.setContentAreaFilled(false);

        lblFieldSet.setForeground(new java.awt.Color(0, 0, 255));
        lblFieldSet.setText("设置匹配字段");

        jcbUseDept.setText("启用查询部门限制");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jcbShowContiner)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFieldSet, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(btnShowDept, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(70, Short.MAX_VALUE))
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jcbUseDept)
                .addContainerGap(94, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcbShowContiner)
                    .addComponent(lblFieldSet, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbUseDept)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnShowDept, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, 0, 215, Short.MAX_VALUE)
            .addComponent(pnlMain, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 215, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlMain, javax.swing.GroupLayout.PREFERRED_SIZE, 297, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, 91, Short.MAX_VALUE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDel;
    private javax.swing.JToggleButton btnShowDept;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JCheckBox jcbShowContiner;
    private javax.swing.JCheckBox jcbUseDept;
    private javax.swing.JLabel lblFieldSet;
    private javax.swing.JPanel pnlMain;
    // End of variables declaration//GEN-END:variables

    private void setEvents() {
        jcbUseDept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                useDept(jcbUseDept.isSelected());
            }
        });
        personTablePanel.addKeyListener(new KeyAdapter() {

            public void keyPressed(KeyEvent ke) {
                if (ke.isControlDown() && ke.getKeyCode() == KeyEvent.VK_V) {
                    paste();
                }
            }
        });
        lblFieldSet.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }

            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() >= 2) {
                    defineMatchField();
                }
            }
        });
        btnShowDept.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseEntered(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
            }

            @Override
            public void mouseExited(MouseEvent e) {
                setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
            }
        });
        miPaste.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                paste();
            }
        });
        btnShowDept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_person == null) {
                    return;
                }
                JPanel pnl = BaseMainFrame.getBaseMainFrame().getCur_Panel();
                if (pnl instanceof IPersonContainer) {
                    ((IPersonContainer) pnl).locateDeptAndType(cur_person);
                }
            }
        });
        personTablePanel.addListSelectionListener(new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                if (cur_person == (A01) personTablePanel.getCurrentRow()) {
                    return;
                }
                cur_person = (A01) personTablePanel.getCurrentRow();
                showContainerInfo();
            }
        });
        jcbShowContiner.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                show_flag = jcbShowContiner.isSelected();
                showContainerInfo();
            }
        });
        ActionListener al_delete = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                personTablePanel.deleteSelectedRows();
            }
        };
        miDel.addActionListener(al_delete);
        btnDel.addActionListener(al_delete);
        ActionListener search_listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String ss = jTextField1.getText().trim().toUpperCase();
                if (ss == null || ss.equals("")) {
                    return;
                }
                ss = SysUtil.getQuickSearchText(ss);
                search(SysUtil.getQuickSearchSQL(matchFields, ss));
            }
        };
        miExcel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                PersonContainerIpExcel per = new PersonContainerIpExcel(PersonContainer.this);
                ContextManager.locateOnMainScreenCenter(per);
                per.setVisible(true);
                if (per.isClick_ok()) {
                    personTablePanel.setObjects(per.lista01s());
                }
            }
        });
        this.jTextField1.addActionListener(search_listener);
        this.btnAdd.addActionListener(search_listener);
        this.addWindowListener(new WindowAdapter() {

            public void windowClosing(WindowEvent e) {
                show_flag = false;
            }
        });
    }

    private void useDept(boolean used) {
        jtp.remove(deptSelectPanel);
        if (used) {
            jtp.add("部门限制", deptSelectPanel);
        }
        jtp.updateUI();
    }

    private void search(String s_where) {
        String hql = "select bp.a01_key from A01 bp where bp.a0193=0 and (" + s_where + ") ";
        hql += " and (" + UserContext.getDept_right_rea_str("bp.deptCode") + ")";
        hql += " and (" + UserContext.getPerson_class_right_str(A01.class, "bp") + ") ";
        if (jcbUseDept.isSelected()) {
            List<DeptCode> dcs = deptSelectPanel.getSelectParentDepts();
            if (dcs.size() > 0) {
                String s_wheres = "";
                for (DeptCode dc : dcs) {
                    s_wheres += " or bp.deptCode.dept_code like '" + dc.getDept_code() + "%'";
                }
                hql += " and (" + s_wheres.substring(4) + ")";
            }
        }
        hql += "order by a0190";
        List ll = CommUtil.fetchEntities(hql);
        PublicUtil.getProps_value().setProperty(A01.class.getName(), "from A01 bp join fetch bp.deptCode left join fetch bp.g10 where bp.a01_key in");
        if (ll.size() > 1) {
            tmp_table.setObjects(ll);
            if (!ModalDialog.doModal(btnAdd, tmp_table, EmpMngMsg.ttl029, tmp_table)) {
                return;
            }
            ll = tmp_table.getSelectObjects();
        }
        addPerson(ll);
    }

    private void showContainerInfo() {
        if (show_flag) {
            JPanel pnl = BaseMainFrame.getBaseMainFrame().getCur_Panel();
            if (pnl instanceof IPersonContainer) {
                ((IPersonContainer) pnl).showContainerInfo();
            }
        }
    }

    public static String getA01KeyStr() {
        List containerPerson = PersonContainer.getPersonContainer().getFTable().getSelectKeys();
        StringBuffer str = new StringBuffer();
        if (containerPerson.isEmpty()) {
            return "'-1'";
        } else {
            for (Object obj : containerPerson) {
                str.append(",'");
                str.append(obj.toString());
                str.append("'");
            }
            return str.toString().substring(1);
        }
    }

    private void paste() {
        String text = null;
        try {
            text = ClipboardUtil.getClipboardText();
        } catch (Exception ex) {
        }
        if (text == null || text.trim().equals("")) {
            MsgUtil.showInfoMsg(EmpMngMsg.msg138);
            return;
        }
        String[] texts = text.split("\n");
        if (texts.length > max_size) {
            MsgUtil.showErrorMsg(EmpMngMsg.msg137.toString() + max_size + EmpMngMsg.msg136.toString());
            return;
        }
        StringBuilder strs = new StringBuilder();
        for (String t : texts) {
            String[] ts = t.split("\t");
            for (String t1 : ts) {
                if (t1.contains(".")) {
                    continue;
                }
                t1 = t1.trim();
                if (t1.length() > 20 || t1.equals("") || t1.equals("false") || t1.equals("true") || t1.length() < 2) {
                    continue;
                }
                strs.append(",'").append(t1).append("'");
            }
        }
        if (strs.toString().equals("")) {
            return;
        }
        String keyStr = strs.toString().substring(1);
        String[] fields = matchFields.split(";");
        String s_where = "";
        for (String field : fields) {
            s_where += " or bp." + field + " in(" + keyStr + ")";
        }
        search(s_where.substring(4));
    }

    public void addPerson(List<?> list) {
        if ((personTablePanel.getObjects().size() + list.size()) >= max_size) {
            MsgUtil.showErrorMsg(EmpMngMsg.msg135.toString() + max_size + EmpMngMsg.msg136.toString());
            return;
        }
        List<String> keys = personTablePanel.getAllKeys();
        List resultData = new ArrayList();
        for (Object obj : list) {
            if (obj instanceof A01) {
                if (keys.contains(((A01) obj).getA01_key())) {
                    continue;
                }
            } else if (keys.contains(obj.toString())) {
                continue;
            }
            resultData.add(obj);
        }
        resultData.addAll(personTablePanel.getObjects());
        personTablePanel.setObjects(resultData);
        jtp.setSelectedIndex(0);
    }

    private void defineMatchField() {
        List<TempFieldInfo> allinfos = EntityBuilder.getCommFieldInfoListOf(A01.class);
        List<TempFieldInfo> bindInfos = new ArrayList();
        for (TempFieldInfo tfi : allinfos) {
            if (!tfi.getField_type().equals("String")) {
                continue;
            }
            bindInfos.add(tfi);
        }
        JCheckBoxList jls = new JCheckBoxList(bindInfos);
        List list = Arrays.asList(matchFields.split(";"));
        for (int i = 0; i < bindInfos.size(); i++) {
            if (list.contains(bindInfos.get(i).getField_name())) {
                jls.CheckedItem(i);
            }
        }
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(new JScrollPane(jls));
        pnl.setPreferredSize(new Dimension(300, 300));
        if (ModalDialog.doModal(PersonContainer.this, pnl, EmpMngMsg.ttl033)) {
            List checks = jls.getCheckedObjects();
            if (checks.isEmpty()) {
                MsgUtil.showInfoMsg(EmpMngMsg.msg134);
                return;
            }
            matchFields = "";
            for (Object obj : checks) {
                TempFieldInfo tfi = (TempFieldInfo) obj;
                matchFields += tfi.getField_name() + ";";
            }
            ConfigManager.getConfigManager().setProperty(matchCode, matchFields);
            ConfigManager.getConfigManager().save2();
        }
    }

    public static boolean isShow_flag() {
        return show_flag;
    }

    public A01 getCur_person() {
        return cur_person;
    }

    public List<A01> getAllPersons() {
        List list = personTablePanel.getAllObjects();
        return (List<A01>) list;
    }

    public List getSelectObjects() {
        return personTablePanel.getSelectObjects();
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
