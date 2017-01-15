/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RegisterPanel.java
 *
 * Created on 2009-4-17, 16:54:51
 */
package org.jhrcore.client.personnel.register;

import com.foundercy.pf.control.table.FTable;
import com.foundercy.pf.control.table.FTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.util.PinYinMa;
import org.jhrcore.util.SysUtil;
import org.jhrcore.util.TransferAccessory;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.personnel.IDCardRead;
import org.jhrcore.client.personnel.comm.IPickRegisterListener;
import org.jhrcore.comm.CodeManager;
import org.jhrcore.util.PublicUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.BasePersonAppendix;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.RyChgLog;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.annotation.ClassAnnotation;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.entity.showstyle.ShowScheme;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpRegisterMsg;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptSelectDlg;
import org.jhrcore.ui.EnterToTab;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.ValidateEntity;
import org.jhrcore.util.FileChooserUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class RegisterPanel extends javax.swing.JPanel implements IModuleCode {

    private A01 person = (A01) UtilTool.createUIDEntity(A01.class);
    private DeptCode dept;
    private Class empClass;
    private Class<?> appendixClass;
    private File photo_url = null;
    private List<FTable> list_appendix = new ArrayList();
    private List person_reg_set = new ArrayList();
    private List<String> person_reg_append = new ArrayList<String>();
    private BeanPanel beanPanel = new BeanPanel();
    private List<TempFieldInfo> all_fields = new ArrayList<TempFieldInfo>();
    private List<IPickRegisterListener> iPickRegisterListeners = new ArrayList<IPickRegisterListener>();
    private SysParameter register_class_para;
    private SysParameter register_appendix_para;
    private SysParameter register_id_para;
    private SysParameter register_field_para;
    private SysParameter register_a0177_para;
    private Hashtable<String, String> id_update_keys = new Hashtable<String, String>();
    private Hashtable<String, TempFieldInfo> a01_field_keys = new Hashtable<String, TempFieldInfo>();
    private String sex_code_type = "性别";
    private String person_cardNo = "";
    private ShowScheme showScheme;
    private String person_class_str;
    private String person_type = "";
    private List<TempFieldInfo> a01_fields = new ArrayList<TempFieldInfo>();
    private A01 old_obj;
    private Set<String> disable_fields = new HashSet<String>();
    private List<String> date_fields = new ArrayList<String>();
    private HrLog log = new HrLog("EmpRegister.入职人员编辑");
    private String autono = "";
    private Hashtable<String, SysParameter> sys_paras;
    private Hashtable<String, ShowScheme> scheme_keys;
    private List<String> editable_fields = null;
    private List allow_depts = null;
    private int pass_flag = 0;
    private FocusAdapter focusAdapter = null;
    private boolean e_flag = true;
    private String module_code = "EmpRegister.btnEdit";

    public void addPickRegisterListener(IPickRegisterListener listener) {
        iPickRegisterListeners.add(listener);
    }

    public void delPickRegisterListener(IPickRegisterListener listener) {
        iPickRegisterListeners.remove(listener);
    }
    //入职登记编辑

    public RegisterPanel(A01 a01, Hashtable<String, SysParameter> sys_paras, Hashtable<String, ShowScheme> scheme_keys, boolean e_flag) {
        this.person = a01;
        this.dept = person.getDeptCode();
        this.sys_paras = sys_paras;
        this.scheme_keys = scheme_keys;
        this.e_flag = e_flag;
        initComponents();
        initOther();
        setupEvents();
        chBoxClear.setEnabled(false);
        this.removeAll();
        this.setLayout(new BorderLayout());
        String[] person_classes = register_class_para.getSysparameter_value().split(";");
        for (String tmp : person_classes) {
            try {
                Class c = Class.forName("org.jhrcore.entity." + tmp);
                ClassAnnotation ca = (ClassAnnotation) c.getAnnotation(ClassAnnotation.class);
                if (ca.displayName().equals(person.getA0191())) {
                    showScheme = scheme_keys.get("RegisterDesign." + tmp);
                    break;
                }
            } catch (ClassNotFoundException ex) {
                log.error(ex);
            }
        }
        selectShowStyle();
        for (FTable ftable : list_appendix) {
            List data = CommUtil.fetchEntities("from " + ((FTableModel) ftable.getModel()).getEntityClass().getSimpleName() + " where a01.a01_key='" + person.getA01_key() + "' order by a_id");
            if (data != null && data.size() > 0) {
                ftable.setObjects(data);
            }
        }
        this.add(jScrollPane2, BorderLayout.CENTER);
        this.add(jPanel2, BorderLayout.SOUTH);
        BufferedImage tmpImage = TransferAccessory.downloadPicture(person.getPic_path());
        Icon tmpIcon = null;
        if (tmpImage != null) {
            tmpIcon = new ImageIcon(tmpImage.getScaledInstance(120, 150, Image.SCALE_DEFAULT));
        }
        JLabel picLabel = new JLabel(tmpIcon);
        picLabel.setPreferredSize(new Dimension(120, 150));
        pnlPhoto.add(picLabel, BorderLayout.CENTER);
        pnlPhoto.updateUI();
        this.updateUI();
    }
    //入职登记新增

    public RegisterPanel(DeptCode dept, String personClass, Hashtable<String, SysParameter> sys_paras, Hashtable<String, ShowScheme> scheme_keys, List allow_depts) {
        this.person_class_str = personClass;
        this.dept = dept;
        this.sys_paras = sys_paras;
        this.scheme_keys = scheme_keys;
        this.allow_depts = allow_depts;
        old_obj = new A01();
        initComponents();
        initOther();
        setupEvents();
    }

    public RegisterPanel() {
        initComponents();
        initOther();
        setupEvents();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jPanel1 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        txtDeptName = new javax.swing.JTextField();
        btnDeptSelect = new javax.swing.JButton();
        jLabel2 = new javax.swing.JLabel();
        tFieldCardNo = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        cmbPersonType = new javax.swing.JComboBox();
        jLabel4 = new javax.swing.JLabel();
        jTextField1 = new javax.swing.JTextField();
        btnG10 = new javax.swing.JButton();
        jButton2 = new javax.swing.JButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        pnlMain = new javax.swing.JPanel();
        pnlBean = new javax.swing.JPanel();
        btnClearPhoto = new javax.swing.JButton();
        btnAddPhoto = new javax.swing.JButton();
        pnlPhoto = new javax.swing.JPanel();
        pnlPersonAppendix = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        btnCancel = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        chBoxClear = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();

        jPanel1.setPreferredSize(new java.awt.Dimension(699, 80));

        jLabel1.setText("部    门：");

        btnDeptSelect.setText("...");

        jLabel2.setText("身份证号：");

        jLabel3.setText("人员类别：");

        jLabel4.setText("岗    位：");

        btnG10.setText("...");

        jButton2.setText("...");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel2)
                    .addComponent(jLabel1))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(tFieldCardNo)
                    .addComponent(txtDeptName, javax.swing.GroupLayout.DEFAULT_SIZE, 136, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 23, Short.MAX_VALUE)
                    .addComponent(btnDeptSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 23, Short.MAX_VALUE))
                .addGap(52, 52, 52)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                        .addComponent(jTextField1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(btnG10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(cmbPersonType, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(291, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtDeptName, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDeptSelect, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnG10, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(tFieldCardNo, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(cmbPersonType, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        pnlMain.setMinimumSize(new java.awt.Dimension(500, 500));
        pnlMain.setLayout(null);

        pnlBean.setBackground(new java.awt.Color(204, 204, 255));
        pnlBean.setToolTipText("其它");
        pnlBean.setMinimumSize(new java.awt.Dimension(0, 200));
        pnlBean.setPreferredSize(new java.awt.Dimension(699, 240));
        pnlBean.setLayout(new java.awt.BorderLayout());
        pnlMain.add(pnlBean);
        pnlBean.setBounds(0, 10, 570, 240);

        btnClearPhoto.setText("清除");
        btnClearPhoto.setPreferredSize(new java.awt.Dimension(60, 23));
        pnlMain.add(btnClearPhoto);
        btnClearPhoto.setBounds(670, 10, 60, 23);

        btnAddPhoto.setText("照片");
        btnAddPhoto.setPreferredSize(new java.awt.Dimension(60, 23));
        pnlMain.add(btnAddPhoto);
        btnAddPhoto.setBounds(600, 10, 60, 23);

        pnlPhoto.setBackground(new java.awt.Color(204, 204, 255));
        pnlPhoto.setPreferredSize(new java.awt.Dimension(125, 125));
        pnlPhoto.setLayout(new java.awt.BorderLayout());
        pnlMain.add(pnlPhoto);
        pnlPhoto.setBounds(610, 40, 130, 150);

        javax.swing.GroupLayout pnlPersonAppendixLayout = new javax.swing.GroupLayout(pnlPersonAppendix);
        pnlPersonAppendix.setLayout(pnlPersonAppendixLayout);
        pnlPersonAppendixLayout.setHorizontalGroup(
            pnlPersonAppendixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 770, Short.MAX_VALUE)
        );
        pnlPersonAppendixLayout.setVerticalGroup(
            pnlPersonAppendixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 260, Short.MAX_VALUE)
        );

        pnlMain.add(pnlPersonAppendix);
        pnlPersonAppendix.setBounds(0, 261, 770, 260);

        jScrollPane2.setViewportView(pnlMain);

        btnCancel.setText("取消");

        btnSave.setText("保存");

        chBoxClear.setText("保存时清除界面数据(日期型数据除外)");

        jLabel5.setText("（提示：人员附表，按F2添加一条附表记录，按F5删除一条附表记录）");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addGap(37, 37, 37)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(jLabel5)
                        .addGap(69, 69, 69)
                        .addComponent(btnSave)
                        .addGap(18, 18, 18)
                        .addComponent(btnCancel))
                    .addComponent(chBoxClear))
                .addContainerGap(166, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel2Layout.createSequentialGroup()
                .addContainerGap(12, Short.MAX_VALUE)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chBoxClear)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addGap(25, 25, 25)
                        .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(btnCancel)
                            .addComponent(jLabel5)
                            .addComponent(btnSave))))
                .addContainerGap())
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
            .addComponent(jScrollPane2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 776, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddPhoto;
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnClearPhoto;
    private javax.swing.JButton btnDeptSelect;
    private javax.swing.JButton btnG10;
    private javax.swing.JButton btnSave;
    private javax.swing.JCheckBox chBoxClear;
    private javax.swing.JComboBox cmbPersonType;
    private javax.swing.JButton jButton2;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JPanel pnlBean;
    private javax.swing.JPanel pnlMain;
    private javax.swing.JPanel pnlPersonAppendix;
    private javax.swing.JPanel pnlPhoto;
    private javax.swing.JTextField tFieldCardNo;
    private javax.swing.JTextField txtDeptName;
    // End of variables declaration//GEN-END:variables

    private void initOther() {
        for (TempFieldInfo tfi : EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_ALL)) {
            if (tfi.getField_type().equals("Date")) {
                date_fields.add(tfi.getField_name());
            }
        }
        register_field_para = sys_paras.get("Register.field_flag");
        register_a0177_para = sys_paras.get("Register.a0177_flag");
        register_appendix_para = sys_paras.get("Register.appendix");
        register_class_para = sys_paras.get("Register.person_class");
        register_id_para = sys_paras.get("Register.id_cmp");
        if (register_id_para == null) {
            register_id_para = new SysParameter();
            register_id_para.setSysParameter_key("Register.id_cmp");
            register_id_para.setSysparameter_code("Register.id_cmp");
            register_id_para.setSysparameter_name("二代证与人员信息对应标识");
            register_id_para.setSysparameter_value("personName:a0101;personSex:a0107;personBorn:a0111;personIDCardNo:a0177");
            CommUtil.saveOrUpdate(register_id_para);
        }
        String strs = register_id_para.getSysparameter_value();
        if (strs != null) {
            String[] tmp_strs = strs.split(";");
            for (String tmp_s : tmp_strs) {
                String[] field_strs = tmp_s.split(":");
                id_update_keys.put(field_strs[0], field_strs[1]);
            }
        }
        Date date = new Date();
        Calendar cl = Calendar.getInstance();
        cl.setTime(date);
        if (person.getNew_flag() == 1) {
            String[] person_classes = register_class_para.getSysparameter_value().split(";");
            for (String tmp : person_classes) {
                try {
                    Class c = Class.forName("org.jhrcore.entity." + tmp);
                    ClassAnnotation ca = (ClassAnnotation) c.getAnnotation(ClassAnnotation.class);
                    EntityDef prs = new EntityDef();
                    prs.setEntity_key(tmp);
                    prs.setEntityName(tmp);
                    prs.setEntityCaption(ca.displayName());
                    if (UserContext.hasEntityViewRight(tmp)) {
                        person_reg_set.add(prs);
                    }
                } catch (ClassNotFoundException ex) {
                    log.error(ex);
                }
            }
            txtDeptName.setText(dept == null ? "" : dept.getContent());
            txtDeptName.setEditable(false);
        }
        String[] person_appendixs = (register_appendix_para.getSysparameter_value() == null ? "" : register_appendix_para.getSysparameter_value()).split(";");
        for (String tmp : person_appendixs) {
            person_reg_append.add(tmp);
        }
        JComboBoxBinding cmBoxBinding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ, person_reg_set, cmbPersonType);
        cmBoxBinding.bind();
        pnlPersonAppendix.setLayout(new GridLayout(person_reg_append.size(), 1));
        pnlPersonAppendix.setSize(this.getWidth() - 55, person_reg_append.size() * 150);
        pnlPersonAppendix.setPreferredSize(pnlPersonAppendix.getSize());
        pnlMain.setSize(this.getWidth() - 20, pnlMain.getHeight());
        pnlMain.setPreferredSize(pnlMain.getSize());
        jScrollPane2.setSize(this.getWidth(), jScrollPane2.getHeight());
        jScrollPane2.setPreferredSize(jScrollPane2.getSize());
        FTable beanTablePanel3;
        for (String entity_name : person_reg_append) {
            try {
                JPanel panel = new JPanel();
                panel.setLayout(new BorderLayout());
                appendixClass = Class.forName("org.jhrcore.entity." + entity_name);
                ClassAnnotation ca = appendixClass.getAnnotation(ClassAnnotation.class);
                panel.setBorder(javax.swing.BorderFactory.createTitledBorder(ca.displayName()));
                beanTablePanel3 = new FTable(appendixClass, false, false, false, "RegisterPanel");
                list_appendix.add(beanTablePanel3);
                beanTablePanel3.setEditable(true);
                beanTablePanel3.setPreferredSize(new Dimension(panel.getWidth(), 120));
                final FTable ftable = beanTablePanel3;
                ftable.addKeyListener(new KeyAdapter() {

                    @Override
                    public void keyPressed(KeyEvent e) {
                        if (e.getKeyCode() == KeyEvent.VK_F2) {
                            for (Object obj : ftable.getObjects()) {
                                BasePersonAppendix tmp_bpa = (BasePersonAppendix) obj;
                                tmp_bpa.setLast_flag("");
                            }
                            BasePersonAppendix temp = (BasePersonAppendix) UtilTool.createUIDEntity(((FTableModel) ftable.getModel()).getEntityClass());
                            temp.setA_id(ftable.getObjects().size() + 1);
                            temp.setLast_flag("最新");
                            temp.setA01(person);
                            ftable.addObject(temp);
                            ftable.setRowSelectionInterval(ftable.getRowCount() - 1, ftable.getRowCount() - 1);
                        } else if (e.getKeyCode() == KeyEvent.VK_F5) {
                            if (ftable.getObjects().isEmpty()) {
                                return;
                            }
                            ftable.deleteRow(ftable.getObjects().size() - 1);
                            if (ftable.getObjects().isEmpty()) {
                                return;
                            }
                            BasePersonAppendix tmp_bpa = (BasePersonAppendix) ftable.getObjects().get(ftable.getRowCount() - 1);
                            tmp_bpa.setLast_flag("最新");
                            ftable.setRowSelectionInterval(ftable.getRowCount() - 1, ftable.getRowCount() - 1);
                        }
                    }
                });
                panel.add(new JScrollPane(beanTablePanel3), BorderLayout.CENTER);
                pnlPersonAppendix.add(panel);
            } catch (ClassNotFoundException ex) {
                log.error(ex);
            }
        }
        pnlPhoto.setLayout(new BorderLayout());
        a01_fields = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : a01_fields) {
            if (tfi.getField_name().equals("a0107")) {
                sex_code_type = tfi.getCode_type_name();
            }
            a01_field_keys.put(tfi.getField_name().replace("_code_", "").replace(" ", ""), tfi);
        }
    }

    private String getPersonNo(DeptCode dc, EntityDef prs, int inc_no) {
        List data = CommUtil.fetchEntities("select autoNoRule_id from AutoNoRule anr where anr.autoNoRule_id like 'PersonNo_%' and (anr.autoNoRule_id like '%_" + prs.getEntityName() + "' or anr.autoNoRule_id like '%_A01')");
        String rule_key = "";
        String rule_all_key = "";
        String start_name = dc.getDept_code();
        for (Object obj : data) {
            String[] row_data = obj.toString().split("_");
            if (start_name.startsWith(row_data[1])) {
                if (row_data[2].equals(prs.getEntityName())) {
                    rule_key = obj.toString();
                    break;
                } else if (row_data[2].equals("A01")) {
                    rule_all_key = obj.toString();
                }
            }
        }
        if (rule_key.equals("")) {
            rule_key = rule_all_key;
        }
        if (rule_key.equals("")) {
            return "";
        }
        Hashtable<String, String> params = new Hashtable<String, String>();
        params.put("@部门代码", "'" + dc.getDept_code() + "'");
        autono = CommUtil.fetchNewNoBy(rule_key, inc_no, params);
        boolean exist_flag = false;
        while (!exist_flag) {
            inc_no++;
            if (CommUtil.exists("select 1 from A01 where a0190='" + autono + "'")) {
                autono = CommUtil.fetchNewNoBy(rule_key, inc_no, params);
            } else {
                exist_flag = true;
            }
        }
        return autono;

    }

    private void changePersonType() {
        if (cmbPersonType.getSelectedIndex() < 0) {
            return;
        }
        PublicUtil.copyProperties(person, old_obj, date_fields, date_fields);
        EntityDef prs = (EntityDef) cmbPersonType.getSelectedItem();
        person_type = prs.getEntityCaption().replace(" ", "");
        try {
            empClass = Class.forName("org.jhrcore.entity." + prs.getEntityName());
        } catch (ClassNotFoundException ex) {
            ex.printStackTrace();
            log.error(ex);
        }
        all_fields.clear();
        List<TempFieldInfo> field_infos = EntityBuilder.getCommFieldInfoListOf(empClass, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : field_infos) {
            all_fields.add(tfi);
        }
        person = (A01) UtilTool.createUIDEntity(empClass);
        if (dept != null) {
            person.setDeptCode(dept);
            person.setA0190(getPersonNo(dept, prs, 0));
        }
        person.assignEntityKey(person.getA01_key());
        showScheme = scheme_keys.get("RegisterDesign." + empClass.getSimpleName());
        selectShowStyle();
    }

    private void setupEvents() {
        jButton2.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                IDCardRead idc = new IDCardRead();
                if (!idc.getLinkM()) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(jButton2),
//                            "没有找到读卡器",
//                            "提示",
//                            JOptionPane.INFORMATION_MESSAGE);
                    MsgUtil.showInfoMsg(EmpRegisterMsg.msg038);
                    log.info("提示:没有找到读卡器");
                    return;
                }
                String personName = idc.getPersonName().replace(" ", "");
                if ("".equals(personName)) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(jButton2),
//                            "读卡失败",
//                            "提示",
//                            JOptionPane.INFORMATION_MESSAGE);
                    MsgUtil.showInfoMsg(EmpRegisterMsg.msg039);
                    return;
                }
                String cardNo = idc.getPersonIDCardNo().replace(" ", "").toUpperCase();
                tFieldCardNo.setText(cardNo);
                pass_flag = updatePerson(idc);
                log.info("读身份证号:" + cardNo);
                idc = null;
            }
        });
        btnG10.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (person.getDeptCode() == null) {
                    return;
                }
                log.info(e);
            }
        });
        btnClearPhoto.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                clearPhoto();
            }
        });
        focusAdapter = new FocusAdapter() {

            @Override
            public void focusLost(FocusEvent e) {
                String tmp_str = tFieldCardNo.getText().replace(" ", "").toUpperCase();
                if (person_cardNo.equals(tmp_str) && pass_flag == 1) {
                    return;
                }
                person_cardNo = tmp_str;
                pass_flag = updatePerson(null);
            }
        };
        tFieldCardNo.addFocusListener(focusAdapter);
        cmbPersonType.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                changePersonType();
            }
        });
        btnDeptSelect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                ValidateEntity ve = new ValidateEntity() {

                    @Override
                    public boolean isEntityValidate(Object obj) {
                        if (!EmpUtil.isAllowRegister(allow_depts, ((DeptCode) obj))) {
//                            JOptionPane.showMessageDialog(null, "选择的部门不在允许入职部门范围内");
                            MsgUtil.showInfoMsg(EmpRegisterMsg.msg041);
                            return false;
                        }
                        return true;
                    }
                };
                DeptSelectDlg dlg = new DeptSelectDlg(UserContext.getDepts(false), dept, TreeSelectMod.leafSelectMod, ve);
                ContextManager.locateOnScreenCenter(dlg);
                dlg.setVisible(true);
                if (dlg.isClick_ok()) {
                    DeptCode dc = dlg.getCurDept();
                    selectDept(dc);
                    log.info("选择部门:" + dc.getDept_full_name());
                }
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                save();
            }
        });
        this.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                pnlPersonAppendix.setSize(getWidth() - 55, person_reg_append.size() * 150);
                pnlPersonAppendix.setPreferredSize(pnlPersonAppendix.getSize());
                pnlMain.setSize(getWidth() - 20, pnlMain.getHeight());
                pnlMain.setPreferredSize(pnlMain.getSize());
                jScrollPane2.setSize(getWidth(), jScrollPane2.getHeight());
                jScrollPane2.setPreferredSize(jScrollPane2.getSize());
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                ModelFrame.close();
            }
        });
        btnAddPhoto.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                File file = FileChooserUtil.getPICFile(CommMsg.SELECTPIC_MESSAGE);
                if (file == null) {
                    return;
                }
                addPhoto(file);
            }
        });
        btnAddPhoto.setEnabled(e_flag);
        btnClearPhoto.setEnabled(e_flag);
        btnDeptSelect.setEnabled(e_flag);
        btnG10.setEnabled(e_flag);
        btnSave.setEnabled(e_flag);
        beanPanel.setEnabled(e_flag);
        Object obj3 = CommUtil.fetchEntityBy("from SysParameter sp where sp.sysParameter_key='Register.com_field_edit'");
        if (obj3 != null) {
            SysParameter sp = (SysParameter) obj3;
            if ("1".equals(sp.getSysparameter_value())) {
                Set<String> set = id_update_keys.keySet();
                for (Iterator<String> it = set.iterator(); it.hasNext();) {
                    String key = it.next();
                    String val = id_update_keys.get(key);
                    if (val != null) {
                        disable_fields.add(val);
                    }
                }
                tFieldCardNo.setEditable(false);
            }
        }
        if (person.getNew_flag() == 1) {
            if (person_reg_set.size() > 0) {
                EntityDef selectObj = null;
                for (Object obj : person_reg_set) {
                    EntityDef prs = (EntityDef) obj;
                    if (prs.getEntityName().equals(person_class_str)) {
                        selectObj = prs;
                        break;
                    }
                }
                if (selectObj != null) {
                    cmbPersonType.setSelectedItem(selectObj);
                } else {
                    cmbPersonType.setSelectedIndex(0);
                }
            } else {
//                JOptionPane.showMessageDialog(null, "您不拥有任何入职人员类别的权限，不允许进行入职登记！");
                MsgUtil.showInfoMsg(EmpRegisterMsg.msg037);
                JOptionPane.getFrameForComponent(btnAddPhoto).dispose();
            }
        }
    }

    private void clearPhoto() {
        photo_url = null;
        pnlPhoto.removeAll();
        pnlPhoto.updateUI();
    }

    private int updatePerson(IDCardRead idcard) {
        int flag = 1;
        if (person.getNew_flag() == 1) {
            boolean repeatA0177 = "1".equals(register_a0177_para.getSysparameter_value());
            String text = tFieldCardNo.getText().toUpperCase();
            String other_id = "";
            if (text.length() == 18) {
                other_id += text.substring(0, 6);
                other_id += text.substring(8, 17);
            } else {
                other_id = text;
            }
            String hql = "select 1 from A01 where (1=0 ";
            String append_sql = "";
            if (text.length() >= 15) {
                append_sql += " or a0177='" + text + "'";
            }
            if (!other_id.equals("")) {
                append_sql += " or a0177='" + other_id + "'";
            }
            if (person.getA0177() != null && person.getA0177().length() >= 15) {
                append_sql += " or a0177='" + person.getA0177() + "'";
            }
            hql += append_sql + ")";
            String a0191_str = EmpUtil.getA0191_string();
            if (!"('-1')".equals(a0191_str)) {
                hql += " and a0191 in " + a0191_str;
            }
            System.out.println("hql:" + hql);
            if (append_sql.length() != 0 && CommUtil.exists(hql)) {
                log.info("该身份证号码已存在");
                RegisterQuestDialog rqDlg = new RegisterQuestDialog(EmpRegisterMsg.ttl005.toString(), !repeatA0177);
                ContextManager.locateOnMainScreenCenter(rqDlg);
                rqDlg.setVisible(true);
                int result = rqDlg.getResult();
                if (result == 2) {
                   
                } else if (result == 0) {
                    return 0;
                }
            }
            if (idcard != null) {
                person.setA0101(idcard.getPersonName().replace(" ", ""));
                List<String> src_fields = new ArrayList<String>();
                List<String> dst_fields = new ArrayList<String>();
                for (String key : id_update_keys.keySet()) {
                    String value = id_update_keys.get(key);
                    if (key.equals("personSex") || key.equals("personBorn") || key.equals("personIDCardNo") || key.equals("personName")) {
                        continue;
                    }
                    TempFieldInfo tfi = a01_field_keys.get(value.replace("_code_", ""));
                    if (tfi == null) {
                        continue;
                    }
                    src_fields.add(key);
                    dst_fields.add(tfi.getField_name());
                }
                PublicUtil.person_copyProperties(idcard, person, src_fields, dst_fields, a01_fields);
                addPersonPhoto(idcard.getPersonPhotoFileName().replace(" ", ""));
                idcard = null;
            } else if (!"".equals(text) && !SysUtil.isRightIdentity(text)) {
//                JOptionPane.showMessageDialog(this,
//                        "身份证号码错误!", // message
//                        "错误", // title
//                        JOptionPane.ERROR_MESSAGE);
                MsgUtil.showErrorMsg(EmpRegisterMsg.msg036);
                log.info("身份证号码错误");
                return -1;
            }
            if (!"".equals(text)) {
                person.setA0177(text);
                person.setA0112(new Integer(SysUtil.getAgeFromIdentityCard(text)));
                person.setA0107(CodeManager.getCodeManager().getCodeIdBy(sex_code_type, SysUtil.getSexFromIdentityCard(text)));
                person.setA0111(SysUtil.getBirthFromIdentityCard(text));
            }
            beanPanel.bind();
        }
        pnlBean.removeAll();
        pnlBean.add(beanPanel, BorderLayout.CENTER);
        pnlBean.updateUI();
        return flag;
    }

    private void selectShowStyle() {
        PublicUtil.copyProperties(old_obj, person, date_fields, date_fields);
        beanPanel.setBean(person);
        beanPanel.setShow_scheme(showScheme);
        beanPanel.setEditable(true && e_flag);
        if ("1".equals(register_field_para.getSysparameter_value())) {
            if (editable_fields == null) {
                List list = CommUtil.selectSQL("select s.field_name from system s,tabname t where s.entity_key=t.entity_key and t.entityname in('A01','" + showScheme.getEntity_name().replace("RegisterDesign.", "") + "') and editable=1 and editablenew=1 ");
                editable_fields = new ArrayList<String>();
                editable_fields.addAll(list);
                editable_fields.remove("a0177");
            }
            beanPanel.setEditable_fields(editable_fields);
        }
        disable_fields.add("a0177");
        beanPanel.setDisable_fields(new ArrayList(disable_fields));
        beanPanel.bind();
        pnlBean.setPreferredSize(beanPanel.getPreferredSize());
        pnlBean.setSize(beanPanel.getPreferredSize());
        if (pnlBean.getHeight() < 200) {
            pnlBean.setPreferredSize(new Dimension(pnlBean.getWidth(), 200));
            pnlBean.setSize(new Dimension(pnlBean.getWidth(), 200));
        }
        pnlBean.removeAll();
        pnlBean.add(beanPanel, BorderLayout.CENTER);
        new EnterToTab(tFieldCardNo);
        new EnterToTab(pnlBean);
        pnlBean.updateUI();
        btnAddPhoto.setLocation(pnlBean.getX() + pnlBean.getWidth() + 10, btnAddPhoto.getY());
        btnClearPhoto.setLocation(btnAddPhoto.getX() + btnAddPhoto.getWidth() + 10, btnClearPhoto.getY());
        pnlPhoto.setLocation(pnlBean.getX() + pnlBean.getWidth() + 10, pnlPhoto.getY());
        pnlPersonAppendix.setLocation(pnlPersonAppendix.getX(), pnlBean.getHeight() + 10);
        pnlMain.setSize(jPanel2.getWidth() - 20, pnlPersonAppendix.getY() + pnlPersonAppendix.getHeight());
        pnlMain.setPreferredSize(pnlMain.getSize());
        jScrollPane2.setSize(jPanel2.getWidth(), jScrollPane2.getHeight());
        jScrollPane2.setPreferredSize(jScrollPane2.getSize());
    }

    private void selectDept(DeptCode dept) {
        this.dept = dept;
        txtDeptName.setText(dept.getContent());
        person.setDeptCode(this.dept);
        person.setA0190(getPersonNo(dept, (EntityDef) cmbPersonType.getSelectedItem(), 0));
        
        beanPanel.bind();
    }

    private void addPersonPhoto(String photoPath) {
        photoPath = photoPath.replace("\\", "/");
        photoPath = photoPath.replace("ProgramFiles", "Program Files");
        String newPath = photoPath.replace(".bmp", "new.jpg");
        File oldPhoto = new File(photoPath);
        File photo = new File(newPath);
        if (photo.exists()) {
            photo.delete();
        }
        oldPhoto.renameTo(photo);
        if (photo.isFile() && photo.exists()) {
            addPhoto(photo);
        }
    }

    public void addPhoto(File selectedFile) {
        int flag = TransferAccessory.checkPic(selectedFile);
        if (flag > 0) {
//            JOptionPane.showMessageDialog(this,
//                    "图片大小不能超过" + flag + "K!", // message
//                    "错误", // title
//                    JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpRegisterMsg.msg035);
            log.info("图片大小不能超过" + flag + "K!");
        } else if (flag == -2) {
//            JOptionPane.showMessageDialog(this,
//                    "图片格式必须为JPG、PNG、GIF、BMP中一种!", // message
//                    "错误", // title
//                    JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpRegisterMsg.msg034);
            log.info("图片格式必须为JPG、PNG、GIF、BMP中一种");
        } else {
            pnlPhoto.removeAll();
            Icon tmpIcon = null;
            try {
                tmpIcon = new ImageIcon(new ImageIcon(ImageIO.read(selectedFile)).getImage().getScaledInstance(120, 150, Image.SCALE_DEFAULT));
                photo_url = selectedFile;
                JLabel picLabel = new JLabel(tmpIcon);
                picLabel.setPreferredSize(new Dimension(102, 126));
                pnlPhoto.add(picLabel, BorderLayout.CENTER);
            } catch (IOException ex) {
                log.error(ex);
            }
            pnlPhoto.updateUI();
        }

    }

    private void save() {
        if (pass_flag != 1) {
            if (updatePerson(null) != 1) {
                return;
            }
        }
        beanPanel.bind();
        if (person.getDeptCode() == null) {
            MsgUtil.showErrorMsg(EmpRegisterMsg.msg033);
            return;
        }
        if (!person.getDeptCode().isEnd_flag()) {
            MsgUtil.showErrorMsg(EmpRegisterMsg.msg032);
            return;
        }
        if (person.getA0101() == null || person.getA0101().trim().equals("")) {
            MsgUtil.showErrorMsg(EmpRegisterMsg.msg031);
            return;
        }
        if (person.getA0190() == null || person.getA0190().trim().equals("")) {
            MsgUtil.showErrorMsg(EmpRegisterMsg.msg030);
            return;
        }
        person.setPydm(PinYinMa.ctoE(person.getA0101()));
        if (person.getA0191() == null || person.getA0191().trim().equals("")) {
            person.setA0191(person_type);
        }
        if (person.getNew_flag() == 1) {
            if (person.getA0190().equals(autono)) {
                person.setA0190(getPersonNo(dept, (EntityDef) cmbPersonType.getSelectedItem(), -1));
            }
        }
        if (photo_url != null) {
            person.setPic_path(dept.getDept_code() + "/" + person.getA0190().trim() + photo_url.getName().substring(photo_url.getName().lastIndexOf(".")));
        }
        RyChgLog rcl = null;
        if (person.getNew_flag() == 1) {
            person.setA0193(2);
            rcl = EmpUtil.getCommRyAddLog();
            rcl.setA01_key(person.getA01_key());
            rcl.setDept_name(person.getDeptCode().getContent());
            rcl.setA0101(person.getA0101());
            rcl.setA0190(person.getA0190());
        }
        List appendix = new ArrayList();
        for (FTable table : list_appendix) {
            table.editingStopped();
            appendix.addAll(table.getObjects());
        }
        boolean isNew = person.getNew_flag() == 1;
        ValidateSQLResult result1 = CommUtil.entity_triger(person, isNew);
        if (result1 != null) {
            beanPanel.bind();
            return;
        }
        for (Object obj : appendix) {
            result1 = CommUtil.entity_triger(obj, isNew);
            if (result1 != null) {
                return;
            }
        }
        String entityName = "";
        if (person.getNew_flag() == 1) {
            entityName = ((EntityDef) cmbPersonType.getSelectedItem()).getEntityName();
        } else {
            entityName = (String) CommUtil.fetchEntityBy("select entityName from EntityDef ed where ed.entityCaption='" + person.getA0191() + "'");
        }
        if (entityName == null || entityName.equals("")) {
            return;
        }
        if (person.getA0177() != null) {
            person.setA0177(person.getA0177().toUpperCase());
        }
        ValidateSQLResult result = RSImpl.saveEmpReigster(person, appendix, rcl, entityName);
        if (result.getResult() == 0) {
            if (photo_url != null) {
                TransferAccessory.uploadPicture(photo_url, person.getPic_path());
            }
        } else {
            MsgUtil.showHRSaveErrorMsg(result);
            return;
        }
        pass_flag = 0;
        log.info("入职登记成功");
        PublicUtil.copyProperties(person, old_obj, date_fields, date_fields);
        if (chBoxClear.isSelected()) {
            clearPhoto();
            tFieldCardNo.setText("");
            changePersonType();
        } else {
            for (IPickRegisterListener listener : iPickRegisterListeners) {
                listener.pickRegister();
            }
            ModelFrame.close();
        }
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
