/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * ImportA01ExcelPanel.java
 *
 * Created on 2010-6-28, 10:37:32
 */
package org.jhrcore.client.personnel;

import java.awt.Cursor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.SwingUtilities;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.util.DateUtil;
import org.jhrcore.comm.HrLog;
import org.jhrcore.util.SysUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.client.personnel.register.EmpRegisterPanel;
import org.jhrcore.util.ExportUtil;
import org.jhrcore.entity.A01;
import org.jhrcore.comm.CodeManager;
import org.jhrcore.entity.DeptCode;
import org.jhrcore.entity.ExportDetail;
import org.jhrcore.entity.ExportScheme;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.annotation.ObjectListHint;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.ImportImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpMngMsg;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.DeptSelectDlg;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.importxls.ReadXLS;
import org.jhrcore.ui.importxls.XlsImportInfo;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.ui.task.IModuleCode;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.ValidateEntity;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.FileChooserUtil;
import org.jhrcore.util.ImportUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author Administrator
 */
public class ImportA01ExcelPanel extends javax.swing.JPanel implements IModuleCode {

    private A01 cur_a01 = new A01();//用于部门选择框
    private File select_file = null;//当前选择的文件
    private Hashtable<String, DeptCode> dept_keys = new Hashtable<String, DeptCode>();//用于记录权限部门索引
    private Hashtable<String, DeptCode> htDept = new Hashtable<String, DeptCode>();//用于记录权限部门索引
    private Hashtable<String, Class> class_keys = new Hashtable<String, Class>();//用于记录人员类别索引
    private Hashtable<String, TempFieldInfo> field_keys = new Hashtable<String, TempFieldInfo>();
    private HashSet<String> a0177_keys = new HashSet<String>();//用于记录人员身份证索引
    private boolean isCheck = false;//是否进行了校验
    private ExportScheme exportScheme;//当前导入方案
    private HashSet save_objs = new HashSet();//最后插入的对象集合
    private int update_no = 0;//最后更新的对象个数
    private StringBuffer ex_sql;//最终的执行语句
    private Hashtable<Integer, List<String>> error_keys = new Hashtable<Integer, List<String>>();//错误数据索引
    private List<Hashtable<String, String>> repeat_list = new ArrayList<Hashtable<String, String>>();//用于记录XLS中的重复记录
    private List<Hashtable<String, String>> redept_list = new ArrayList<Hashtable<String, String>>();//用于记录XLS中的非末级部门记录
    private List<Hashtable<String, String>> notFind_list = new ArrayList<Hashtable<String, String>>();//用于记录XLS中的找不到匹配记录
    private List<Hashtable<String, String>> error_list = new ArrayList<Hashtable<String, String>>();//用于记录错误数据
    private List<Hashtable<String, String>> insert_list = new ArrayList<Hashtable<String, String>>();//用于记录错误数据
    private DeptCode cur_dept;//当前人员所属默认部门
    private HrLog log = new HrLog("EmpMng.导入人员信息");
    private List<String> update_a01_keys = new ArrayList<String>();
    private List<String> new_a01_keys = new ArrayList<String>();
    private List<String> a0191_a01_keys = new ArrayList<String>();
    private List<TempFieldInfo> person_default_fields;
    private List<TempFieldInfo> person_all_fields;
    private List save_objects = new ArrayList();
    private List<String> n_fields = new ArrayList<String>();
    private List<DeptCode> select_depts = new ArrayList<DeptCode>();
    private Hashtable<String, String> a0191_table = new Hashtable<String, String>();
    private List<TempFieldInfo> a01_fields = new ArrayList<TempFieldInfo>();
    private boolean register_flag = false;//是否用于入职登记
    private List<String> a0191_list = new ArrayList<String>();
    private int leng_exist = 0;
    private boolean bo_hql = true;
    private SysParameter sp_bhxm;//用于保存‘指标为人员编号+姓名’是否选择
    private List<IPickPersonImportListener> listeners = new ArrayList<IPickPersonImportListener>();
    private List allowRegisterDepts = new ArrayList();
    private String module_code = "EmpMng.mi_personInfoIn";

    public void AddPickPersonImportListener(IPickPersonImportListener listener) {
        listeners.add(listener);
    }

    public void DelPickPersonImportListener(IPickPersonImportListener listener) {
        listeners.remove(listener);
    }

    public List<String> getUpdate_keys() {
        return update_a01_keys;
    }

    public List<String> getNew_keys() {
        return new_a01_keys;
    }

    public ImportA01ExcelPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    /** Creates new form ImportExcelDialog */
    public ImportA01ExcelPanel(List<TempFieldInfo> person_all_fields, List<TempFieldInfo> person_default_fields, String module_code) {
        this.person_all_fields = person_all_fields;
        this.person_default_fields = person_default_fields;
        this.module_code = module_code;
        this.register_flag = module_code.startsWith(EmpRegisterPanel.module_code);
        initComponents();
        initOthers();
        setupEvents();
    }

    /** Creates new form ImportA01ExcelPanel */
    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        buttonGroup1 = new javax.swing.ButtonGroup();
        jPanel2 = new javax.swing.JPanel();
        jTextField1 = new javax.swing.JTextField();
        btnFile = new javax.swing.JButton();
        btnCheck = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jcbbPersonClass = new javax.swing.JComboBox();
        jRadioButton1 = new javax.swing.JRadioButton();
        jRadioButton3 = new javax.swing.JRadioButton();
        jLabel3 = new javax.swing.JLabel();
        jcbbField = new javax.swing.JComboBox();
        jcb_checkId = new javax.swing.JCheckBox();
        jcb_repId = new javax.swing.JCheckBox();
        jCheckBox2 = new javax.swing.JCheckBox();
        jCheckBox3 = new javax.swing.JCheckBox();
        cb_bhxm = new javax.swing.JCheckBox();
        jLabel5 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jtfDept = new javax.swing.JTextField();
        btnSelectDept = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jSeparator1 = new javax.swing.JSeparator();
        btnOk = new javax.swing.JButton();
        btnCancel = new javax.swing.JButton();
        jProgressBar1 = new javax.swing.JProgressBar();
        lblInfo = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        btnCantrast = new javax.swing.JButton();
        btnSelectDepts = new javax.swing.JButton();

        setPreferredSize(new java.awt.Dimension(550, 350));

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("选择导入文件："));

        btnFile.setText("..");

        btnCheck.setText("校验");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, 300, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnFile, javax.swing.GroupLayout.PREFERRED_SIZE, 30, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnCheck)
                .addContainerGap(205, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jTextField1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnFile, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnCheck))
                .addContainerGap(20, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("导入选项"));

        jLabel1.setText("指定默认部门：");

        jLabel2.setText("指定默认人员类别：");

        jcbbPersonClass.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        buttonGroup1.add(jRadioButton1);
        jRadioButton1.setText("追加");

        buttonGroup1.add(jRadioButton3);
        jRadioButton3.setSelected(true);
        jRadioButton3.setText("更新（不更新部门、人员类别、人员编号、姓名）");

        jLabel3.setText("指定匹配指标：");

        jcbbField.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        jcbbField.setPreferredSize(new java.awt.Dimension(70, 21));

        jcb_checkId.setSelected(true);
        jcb_checkId.setText("验证身份证有效性");

        jcb_repId.setText("允许身份证重复");

        jCheckBox2.setText("忽略关联代码错误");

        jCheckBox3.setText("数据错误用默认值");

        cb_bhxm.setText("指标为人员编号+姓名");

        jLabel5.setText("导入方式：");

        jLabel4.setText("其他选项：");

        jtfDept.setEditable(false);

        btnSelectDept.setText("...");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(26, 26, 26)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbbPersonClass, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcb_checkId)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcb_repId)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jCheckBox2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(jCheckBox3))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jRadioButton1)
                                .addGap(18, 18, 18)
                                .addComponent(jRadioButton3))
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                                .addComponent(jtfDept, javax.swing.GroupLayout.DEFAULT_SIZE, 132, Short.MAX_VALUE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(btnSelectDept, javax.swing.GroupLayout.PREFERRED_SIZE, 24, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(314, 314, 314))))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 108, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbbField, javax.swing.GroupLayout.PREFERRED_SIZE, 126, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(cb_bhxm)))
                .addContainerGap())
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jRadioButton1)
                    .addComponent(jLabel5, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jRadioButton3))
                .addGap(8, 8, 8)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfDept, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnSelectDept, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(8, 8, 8)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbbPersonClass, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbbField, javax.swing.GroupLayout.PREFERRED_SIZE, 21, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cb_bhxm))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jcb_repId)
                    .addComponent(jCheckBox2)
                    .addComponent(jLabel4, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcb_checkId)
                    .addComponent(jCheckBox3))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        btnOk.setText("确定");

        btnCancel.setText("取消");

        lblInfo.setText("jLabel5");

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSeparator1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 616, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addGap(21, 21, 21)
                .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 162, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(lblInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 124, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 108, Short.MAX_VALUE)
                .addComponent(btnOk)
                .addGap(18, 18, 18)
                .addComponent(btnCancel)
                .addGap(59, 59, 59))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(btnOk)
                        .addComponent(btnCancel))
                    .addComponent(jProgressBar1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblInfo))
                .addContainerGap(22, Short.MAX_VALUE))
        );

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("人员比对"));

        btnCantrast.setText("人员比对");

        btnSelectDepts.setText("比对部门挑选");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnSelectDepts)
                .addGap(18, 18, 18)
                .addComponent(btnCantrast)
                .addContainerGap(390, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnSelectDepts)
                    .addComponent(btnCantrast))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnCancel;
    private javax.swing.JButton btnCantrast;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnFile;
    private javax.swing.JButton btnOk;
    private javax.swing.JButton btnSelectDept;
    private javax.swing.JButton btnSelectDepts;
    private javax.swing.ButtonGroup buttonGroup1;
    private javax.swing.JCheckBox cb_bhxm;
    private javax.swing.JCheckBox jCheckBox2;
    private javax.swing.JCheckBox jCheckBox3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JProgressBar jProgressBar1;
    private javax.swing.JRadioButton jRadioButton1;
    private javax.swing.JRadioButton jRadioButton3;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JTextField jTextField1;
    private javax.swing.JCheckBox jcb_checkId;
    private javax.swing.JCheckBox jcb_repId;
    private javax.swing.JComboBox jcbbField;
    private javax.swing.JComboBox jcbbPersonClass;
    private javax.swing.JTextField jtfDept;
    private javax.swing.JLabel lblInfo;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        if (register_flag) {
            allowRegisterDepts.addAll(CommUtil.selectSQL("select sysparameter_value from SysParameter where sysparameter_code='Register.Dept'"));
            List list = CommUtil.selectSQL("select sysparameter_value from SysParameter where sysparameter_key='Register.person_class'");
            if (list != null && list.size() > 0 && list.get(0) != null) {
                String str = list.get(0).toString();
                String[] s = str.split(";");
                for (String s2 : s) {
                    a0191_list.add(s2);
                }
            }
            jcb_checkId.setEnabled(UserContext.hasFunctionRight("EmpRegister.jcb_checkId"));
            jcb_repId.setEnabled(UserContext.hasFunctionRight("EmpRegister.jcb_repId"));
        }
        jRadioButton1.setEnabled(register_flag);
        jRadioButton1.setSelected(register_flag);
        jRadioButton3.setEnabled(!register_flag);
        jRadioButton3.setSelected(!register_flag);
        jcb_repId.setVisible(register_flag);
        cb_bhxm.setVisible(!register_flag);
//        cb_bhxm.setEnabled(UserContext.hasFunctionRight("EmpMng.cb_bhxm"));
        lblInfo.setText("");
        for (DeptCode dept : UserContext.getDepts(false)) {
            if (register_flag) {
                if (dept.isEnd_flag() && EmpUtil.isAllowRegister(allowRegisterDepts, dept)) {
                    dept_keys.put(dept.getDept_code(), dept);
                }
            } else {
                dept_keys.put(dept.getDept_code(), dept);
            }
            htDept.put(dept.getDeptCode_key(), dept);
        }
        List<EntityDef> person_class_list = SysUtil.getPersonClass();
        List<EntityDef> person_class_list1 = new ArrayList<EntityDef>();
        for (EntityDef ed : person_class_list) {
            if (!ed.getEntityName().equals("A01")) {
                person_class_list1.add(ed);
                try {
                    if (register_flag) {
                        if (a0191_list.contains(ed.getEntityName())) {
                            class_keys.put(ed.getEntityCaption().trim(), Class.forName("org.jhrcore.entity." + ed.getEntityName()));
                        }
                    } else {
                        class_keys.put(ed.getEntityCaption().trim(), Class.forName("org.jhrcore.entity." + ed.getEntityName()));
                    }
                } catch (ClassNotFoundException ex) {
                    log.error(ex);
                }
            }
        }
        JComboBoxBinding person_class_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, person_class_list1, jcbbPersonClass);
        person_class_binding.bind();
        List<TempFieldInfo> fields = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : fields) {
            if (tfi.getField_name().equals("deptCode")) {
                continue;
            }
            if (tfi.getField_name().equals("g10")) {
                continue;
            }
            field_keys.put(tfi.getField_name(), tfi);
            if (tfi.getField_type().equals("Date") || tfi.getField_type().toLowerCase().equals("boolean") || tfi.getField_type().equals("Code") || tfi.getField_type().toLowerCase().equals("float") || tfi.getField_type().toLowerCase().equals("bigdecimal")) {
                continue;
            }
            a01_fields.add(tfi);
        }
        List<TempFieldInfo> dept_infos = EntityBuilder.getCommFieldInfoListOf(DeptCode.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : dept_infos) {
            tfi.setField_name("deptCode." + tfi.getField_name());
            field_keys.put(tfi.getField_name(), tfi);
        }
        JComboBoxBinding person_fields_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, a01_fields, jcbbField);
        person_fields_binding.bind();
        sp_bhxm = (SysParameter) CommUtil.fetchEntityBy("from SysParameter sp where sp.sysParameter_key='sp_bhxm'");
        if (sp_bhxm == null) {
            sp_bhxm = (SysParameter) UtilTool.createUIDEntity(SysParameter.class);
            sp_bhxm.setSysParameter_key("sp_bhxm");
            sp_bhxm.setSysparameter_value(cb_bhxm.isSelected() ? "1" : "0");
            CommUtil.saveEntity(sp_bhxm);
        } else {
            cb_bhxm.setSelected("1".equals(sp_bhxm.getSysparameter_value()));
        }
        jcbbField.setEnabled(!cb_bhxm.isSelected());
        ComponentUtil.setSysFuntionNew(this);
    }

    private void setupEvents() {
        btnSelectDept.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ValidateEntity ve = new ValidateEntity() {

                    @Override
                    public boolean isEntityValidate(Object obj) {
                        if (!(obj instanceof DeptCode)) {
                            MsgUtil.showErrorMsg(EmpMngMsg.msg053);
                            return false;
                        }
                        if (register_flag && !EmpUtil.isAllowRegister(allowRegisterDepts, (DeptCode) obj)) {
                            MsgUtil.showErrorMsg(EmpMngMsg.msg054);
                            return false;
                        }
                        return true;
                    }
                };
                DeptSelectDlg dlg = new DeptSelectDlg(UserContext.getDepts(false), cur_dept, TreeSelectMod.leafSelectMod, ve);
                ContextManager.locateOnMainScreenCenter(dlg);
                dlg.setVisible(true);
                if (dlg.isClick_ok()) {
                    cur_dept = dlg.getCurDept();
                    jtfDept.setText(cur_dept.getContent());
                    cur_a01.setDeptCode(cur_dept);
                }
            }
        });
        cb_bhxm.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                sp_bhxm.setSysparameter_value(cb_bhxm.isSelected() ? "1" : "0");
                CommUtil.updateEntity(sp_bhxm);
                jcbbField.setEnabled(!cb_bhxm.isSelected());
            }
        });
        btnSelectDepts.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                DeptSelectDlg dlg = new DeptSelectDlg(UserContext.getDepts(false), select_depts, TreeSelectMod.nodeCheckMod);
                ContextManager.locateOnMainScreenCenter(dlg);
                dlg.setVisible(true);
                if (dlg.isClick_ok()) {
                    select_depts.clear();
                    select_depts.addAll(dlg.getSelectDepts());
                }
            }
        });
        btnCantrast.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (select_file == null) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "请选择导入文件", "错误", JOptionPane.ERROR_MESSAGE);
                    MsgUtil.showErrorMsg(EmpMngMsg.msg055);
                    return;
                }
                final DeptCode dept = cur_a01.getDeptCode();
                final EntityDef ed = (EntityDef) jcbbPersonClass.getSelectedItem();
                if (ed == null) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "请选择默认人员类别：");
                    MsgUtil.showInfoMsg(EmpMngMsg.msg056);
                    return;
                }
                final TempFieldInfo tfi = (TempFieldInfo) jcbbField.getSelectedItem();
                int update_type = 0;
//                if (jRadioButton2.isSelected()) {
//                    update_type = 1;
//                } else 
                if (jRadioButton3.isSelected()) {
                    update_type = 2;
                }
                final boolean id_check_flag = jcb_checkId.isSelected();
                final boolean id_in_flag = !jcb_repId.isSelected();
                final int tmp_update_type = update_type;
                final int i = check2(select_file.getPath(), dept, ed, tmp_update_type, tfi, id_check_flag, id_in_flag);
                if (i != 0) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "导入文件中不包含匹配列", "错误", JOptionPane.ERROR_MESSAGE);
                    MsgUtil.showErrorMsg(EmpMngMsg.msg057);
                    return;
                }
                ResultImportA01Pnl dlg = new ResultImportA01Pnl(a0191_table, ed.getEntityCaption(), a0191_a01_keys, select_depts, save_objects, new_a01_keys, update_a01_keys, person_all_fields, person_default_fields, n_fields);
                dlg.addIPickRefreshDataListenr(new IPickPersonImportListener() {

                    @Override
                    public void refreshData() {
                        export_insert_data();
                    }

                    @Override
                    public void importPersons() {
                        if (select_file == null) {
//                            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "请选择导入文件", "错误", JOptionPane.ERROR_MESSAGE);
                            MsgUtil.showErrorMsg(EmpMngMsg.msg055);
                            return;
                        }
                        final DeptCode dept = cur_a01.getDeptCode();
                        final EntityDef ed = (EntityDef) jcbbPersonClass.getSelectedItem();
                        if (ed == null) {
//                            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "请选择默认人员类别：");
                            MsgUtil.showInfoMsg(EmpMngMsg.msg056);
                            return;
                        }
                        final TempFieldInfo tfi = (TempFieldInfo) jcbbField.getSelectedItem();
                        int update_type = 0;
//                        if (jRadioButton2.isSelected()) {
//                            update_type = 1;
//                        } else
                        if (jRadioButton3.isSelected()) {
                            update_type = 2;
                        }
                        final int tmp_update_type = update_type;
                        final boolean id_check_flag = jcb_checkId.isSelected();
                        final boolean id_in_flag = !jcb_repId.isSelected();

                        Runnable run = new Runnable() {

                            @Override
                            public void run() {
                                final int i = importXls(select_file.getPath(), dept, ed, tmp_update_type, tfi, id_check_flag, id_in_flag);
                                Runnable tmp_run = new Runnable() {

                                    @Override
                                    public void run() {
                                        report_msg(select_file.getPath(), false, i);
                                        lblInfo.setText("");
                                        jProgressBar1.setValue(0);
                                    }
                                };
                                SwingUtilities.invokeLater(tmp_run);
                            }
                        };
                        new Thread(run).start();
                    }
                });
                ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(btnCheck), dlg, true, EmpMngMsg.ttl015, 800, 600);
            }
        });
        btnCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (select_file == null) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "请选择导入文件", "错误", JOptionPane.ERROR_MESSAGE);
                    MsgUtil.showErrorMsg(EmpMngMsg.msg055);
                    return;
                }
                final DeptCode dept = cur_a01.getDeptCode();
                final EntityDef ed = (EntityDef) jcbbPersonClass.getSelectedItem();
                if (ed == null) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "请选择默认人员类别：");
                    MsgUtil.showInfoMsg(EmpMngMsg.msg056);
                    return;
                }
                final TempFieldInfo tfi = (TempFieldInfo) jcbbField.getSelectedItem();
                int update_type = 0;
//                if (jRadioButton2.isSelected()) {
//                    update_type = 1;
//                } else 
                if (jRadioButton3.isSelected()) {
                    update_type = 2;
                }
                final boolean id_check_flag = jcb_checkId.isSelected();
                final boolean id_in_flag = !jcb_repId.isSelected();
                final int tmp_update_type = update_type;
                Runnable run = new Runnable() {

                    @Override
                    public void run() {
                        final int i = check(select_file.getPath(), dept, ed, tmp_update_type, tfi, id_check_flag, id_in_flag);
                        Runnable tmp_run = new Runnable() {

                            @Override
                            public void run() {
                                report_msg(select_file.getPath(), true, i);
                                lblInfo.setText("");
                                jProgressBar1.setValue(0);
                            }
                        };
                        SwingUtilities.invokeLater(tmp_run);
                    }
                };
                new Thread(run).start();
            }
        });
        btnFile.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                File file = FileChooserUtil.getXLSFile(CommMsg.SELECTXLSFILE_MESSAGE);
                if (file == null) {
                    return;
                }
                isCheck = false;
                select_file = file;//selectedFile;
                jTextField1.setText(select_file.getPath());
            }
        });
        ActionListener listener = new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                isCheck = false;
            }
        };
        jRadioButton1.addActionListener(listener);
//        jRadioButton2.addActionListener(listener);
        jRadioButton3.addActionListener(listener);
        jcbbField.addActionListener(listener);
        jcbbPersonClass.addActionListener(listener);
        jcb_checkId.addActionListener(listener);
        jCheckBox2.addActionListener(listener);
        jCheckBox3.addActionListener(listener);
        btnOk.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                if (select_file == null) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "请选择导入文件", "错误", JOptionPane.ERROR_MESSAGE);
                    MsgUtil.showErrorMsg(EmpMngMsg.msg055);
                    return;
                }
                final DeptCode dept = cur_a01.getDeptCode();
                final EntityDef ed = (EntityDef) jcbbPersonClass.getSelectedItem();
                if (ed == null) {
//                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "请选择默认人员类别：");
                    MsgUtil.showInfoMsg(EmpMngMsg.msg056);
                    return;
                }
                final TempFieldInfo tfi = (TempFieldInfo) jcbbField.getSelectedItem();
                int update_type = 0;
//                if (jRadioButton2.isSelected()) {
//                    update_type = 1;
//                } else
                if (jRadioButton3.isSelected()) {
                    update_type = 2;
                }
                ImportUtil.saveImportField("Emp", tfi.getField_name());
                final int tmp_update_type = update_type;
                final boolean id_check_flag = jcb_checkId.isSelected();
                final boolean id_in_flag = !jcb_repId.isSelected();
                Runnable run = new Runnable() {

                    @Override
                    public void run() {
                        final int i = importXls(select_file.getPath(), dept, ed, tmp_update_type, tfi, id_check_flag, id_in_flag);
                        Runnable tmp_run = new Runnable() {

                            @Override
                            public void run() {
                                report_msg(select_file.getPath(), false, i);
                                lblInfo.setText("");
                                jProgressBar1.setValue(0);
                                if (ContextManager.getMainFrame() != null) {
                                    ContextManager.getMainFrame().setCursor(Cursor.getDefaultCursor());
                                }
                            }
                        };
                        SwingUtilities.invokeLater(tmp_run);
                    }
                };
                new Thread(run).start();
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
//                ModelFrame.close((ModelFrame) JOptionPane.getFrameForComponent(btnCancel));
                ModelFrame.close();
            }
        });
        refreshDefaultField();
        ComponentUtil.setSysFuntionNew(this, false);
    }

    private void refreshDefaultField() {
        String defaultFieldName = ImportUtil.getImportField("Emp");
        for (TempFieldInfo tfi : a01_fields) {
            if (tfi.getField_name().replace("_code_", "").equals(defaultFieldName)) {
                jcbbField.setSelectedItem(tfi);
                break;
            }
        }
    }

    /**
     * 数据校验过程
     * @param fileName：校验文件
     * @param dept：默认部门
     * @param ed：默认类别
     * @param update_type：更新类型：0，追加；1：更新并追加；2：仅更新
     * @param tfi：匹配字段
     * @param a0177_check_flag：是否校验身份证
     */
    private int check(String fileName, DeptCode dept, EntityDef ed, int update_type, TempFieldInfo tfi, boolean a0177_check_flag, boolean id_in_flag) {
        //辅助变量定义部分---------------------------------------------------------------------------------------------
        if (!register_flag) {
            id_in_flag = false;
        }
        error_keys.clear();
        notFind_list.clear();
        redept_list.clear();
        repeat_list.clear();
        error_list.clear();
        save_objs.clear();
        save_objects.clear();
        insert_list.clear();
        n_fields.clear();
        update_a01_keys.clear();
        update_no = 0;
        cur_dept = dept;
        ex_sql = new StringBuffer();
        boolean com_flag = cb_bhxm.isSelected();
        boolean code_flag = jCheckBox2.isSelected();
        boolean data_noerror_flag = jCheckBox3.isSelected();


        XlsImportInfo xlsImportInfo = null;
        //检查读excel
        try {
            xlsImportInfo = ReadXLS.importXls(new File(fileName));
        } catch (Exception e) {
            e.printStackTrace();
            return 6;
        }
        exportScheme = xlsImportInfo.getExportScheme();//当前显示方案
        SysParameter update_para = UserContext.getSys_para("System.import_updateable");//系统更新参数
        if (register_flag) {
            Object p_obj = CommUtil.fetchEntityBy("from SysParameter where sysParameter_key='Register.field_flag'");
            update_para = (SysParameter) p_obj;
        }
        HashSet<String> update_able_fields = new HashSet<String>();//用于记录允许更新的字段
        Hashtable<String, TempFieldInfo> use_fields = new Hashtable<String, TempFieldInfo>();
        Hashtable<String, Hashtable<String, String>> ri_table = new Hashtable<String, Hashtable<String, String>>();
        Hashtable<String, Hashtable<String, String>> ri_table2 = new Hashtable<String, Hashtable<String, String>>();
        boolean a0101_flag = false;//是否存在姓名字段
        boolean a0190_flag = false;//是否存在姓名字段
        boolean compare_flag = false;//是否存在匹配字段
        boolean str_flag = false;//用以区分搜索语句拼接时是否加单引号
        List exist_a01 = new ArrayList();//用于记录数据库中已存在的记录
        final Hashtable<String, Hashtable<String, Object>> result_data_new = new Hashtable<String, Hashtable<String, Object>>();//用于记录读取出来转换成对象的数据
        final Hashtable<String, Hashtable<String, Object>> result_data = new Hashtable<String, Hashtable<String, Object>>();//用于记录读取出来转换成对象的数据
        Hashtable<String, String> exist_keys = new Hashtable<String, String>();//用于记录数据库中已经存在的记录，索引为匹配字段值，值为a01_key(即主键值)
        Hashtable<String, Integer> comp_keys = new Hashtable<String, Integer>();//匹配值和导入数据行对应记录
        Hashtable<String, String> exist_dept_keys = new Hashtable<String, String>();
        String compare_val;//匹配值
        Class assign_class = null;
        final int num_jbp = exportScheme.getExportDetails().size();
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                lblInfo.setText(EmpMngMsg.ttl024.toString());
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(num_jbp);
                jProgressBar1.setValue((num_jbp + 1) / 3);
            }
        });
        if (bo_hql) {
            bo_hql = false;
            String hql = "";
            if (jRadioButton1.isSelected()) {
                String a0191_str = EmpUtil.getA0191_string();
                hql = "select a0177 from A01 where a0191 in " + a0191_str + " and  a0177 is not null";
            } else {
                hql = "select a0177 from A01 where a0177 is not null";
            }
            List id_list = CommUtil.selectSQL(hql);
            for (Object obj : id_list) {
                a0177_keys.add(obj.toString());
            }
        }

        List<ExportDetail> error_details = new ArrayList<ExportDetail>();
        for (ExportDetail exportDetail : exportScheme.getExportDetails()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                }
            });
            String field_name = exportDetail.getField_name();
            n_fields.add(field_name.toLowerCase());
            if (field_name.equals("deptCode.dept_code")) {
                use_fields.put(field_name, field_keys.get("deptCode.dept_code"));
                exportDetail.setField_type("String");
            } else if (field_name.startsWith("deptCode.")) {
                exportDetail.setField_type("String");
                error_details.add(exportDetail);
                continue;
            } else if (field_name.equals("g10.code")) {
                use_fields.put(field_name, field_keys.get("g10.code"));
                exportDetail.setField_type("String");
            } else if (field_name.startsWith("g10.")) {
                exportDetail.setField_type("String");
                error_details.add(exportDetail);
                continue;
            } else {
                if (field_name.equals("a0101")) {
                    exportDetail.setField_type("String");
                    use_fields.put(field_name, field_keys.get("a0101"));
                    a0101_flag = true;
                }
                if (field_name.equals("a0190")) {
                    exportDetail.setField_type("String");
                    use_fields.put(field_name, field_keys.get("a0190"));
                    a0190_flag = true;
                }
                if (!com_flag) {
                    if (field_name.equals(tfi.getField_name())) {
                        exportDetail.setField_type(tfi.getField_type().equals("Code") ? "String" : tfi.getField_type());
                        use_fields.put(field_name, tfi);
                        compare_flag = true;
                    }
                }
                TempFieldInfo type = field_keys.get(field_name);
                if (type != null) {
                    exportDetail.setField_type(tfi.getField_type());
                    use_fields.put(field_name, field_keys.get(field_name));
                } else {
                    type = field_keys.get(field_name + "_code_");
                    if (type != null) {
                        exportDetail.setField_type("String");
                        use_fields.put(field_name, type);
                    } else if (field_name.length() > 6) {
                        type = field_keys.get(field_name.substring(0, field_name.length() - 6));
                        if (type != null) {
                            exportDetail.setField_type("String");
                            use_fields.put(field_name, type);
                        }
                    }
                }
            }
        }
        if (com_flag && !a0101_flag && !a0190_flag) {
            return 1;
        }
        if (!compare_flag && !com_flag) {
            return 1;
        }
        if (!a0101_flag && update_type != 2) {
            return 2;
        }
        //---------获得权限字段----------------------------------------------
        for (String field_name : use_fields.keySet()) {
            String fieldName = field_name.toUpperCase();
            if (field_name.equals("DEPTCODE.DEPT_CODE")) {
                if (UserContext.getFieldRight(fieldName) == 1) {
                    update_able_fields.add(field_name);
                    //} else if (UserContext.getFieldRight(fieldName) == 2 && update_para.getSysparameter_value().equals("1")) {
                } else if (jRadioButton1.isSelected() && UserContext.getFieldRight(fieldName) == 2 && update_para.getSysparameter_value().equals("1")) {
                    update_able_fields.add(field_name);
                }
            } else if (field_name.equals("G10.CODE")) {
                if (UserContext.getFieldRight(fieldName) == 1) {
                    update_able_fields.add(field_name);
                    //} else if (UserContext.getFieldRight(fieldName) == 2 && update_para.getSysparameter_value().equals("1")) {
                } else if (jRadioButton1.isSelected() && UserContext.getFieldRight(fieldName) == 2 && update_para.getSysparameter_value().equals("1")) {
                    update_able_fields.add(field_name);
                }
            } else {
                fieldName = "A01." + fieldName.replace("_CODE_", "");
                if (UserContext.getFieldRight(fieldName) == 1) {
                    update_able_fields.add(field_name);
                    //} else if (UserContext.getFieldRight(fieldName) == 2 && update_para.getSysparameter_value().equals("1")) {
                } else if (jRadioButton1.isSelected() && UserContext.getFieldRight(fieldName) == 2 && update_para.getSysparameter_value().equals("1")) {
                    update_able_fields.add(field_name);
                }
            }
        }

        if (register_flag && !update_able_fields.contains(tfi.getField_name())) {
            return 1;
        }
        if (register_flag && (!update_able_fields.contains("a0190") || !update_able_fields.contains("a0101"))) {
            return 5;
        }
        try {
            assign_class = Class.forName("org.jhrcore.entity." + ed.getEntityName());
        } catch (ClassNotFoundException ex) {
            log.error(ex);
        }
        //原始数据处理部分---------------------------------------------------------------------------------------------
        if (tfi.getField_type().equals("String") || tfi.getField_type().equals("Date")) {
            str_flag = true;
        }
        if (use_fields.get("deptCode.dept_code") == null && dept == null && update_type != 2) {
            return 3;
        }
        if (!compare_flag && !com_flag) {
            return 1;
        }
        final int len = xlsImportInfo.getValues().size();
        int k = 0;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                lblInfo.setText(EmpMngMsg.ttl023.toString());
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(len);
                jProgressBar1.setValue(0);
            }
        });
        List<String> keys = new ArrayList<String>();
        String c_val = "";
        String cm_str = "";
        //     Hashtable<String,Integer> keyCount = new Hashtable<String,Integer>();
        for (int i = 0; i < len; i++) {
            cm_str = "";
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                }
            });
            boolean error_data_flag = false;
            Hashtable<String, String> row_data = xlsImportInfo.getValues().get(i);
            Hashtable<String, Object> result_row_data = new Hashtable<String, Object>();
            if (com_flag) {
                compare_val = row_data.get("a0190") + "_" + row_data.get("a0101");
                c_val = row_data.get("a0190");
                cm_str = row_data.get("a0190");
            } else {
                compare_val = row_data.get(tfi.getField_name());
                c_val = compare_val;
                cm_str = compare_val;
                if ("a0177".equals(tfi.getField_name())) {
                    compare_val = compare_val.toUpperCase();
                    c_val = c_val + "','" + compare_val;
                }
            }
            result_data_new.put(compare_val, result_row_data);
            if (result_data.get(compare_val) == null) {
                List<String> row_error_keys = error_keys.get(k);
                if (row_error_keys == null) {
                    row_error_keys = new ArrayList<String>();
                }
                if (compare_val == null || "".equals(compare_val.replace(" ", ""))) {
                    row_error_keys.add(tfi.getField_name());
                }
                try {
                    for (String field_name : use_fields.keySet()) {
                        Object tmp_obj = row_data.get(field_name);
                        if (field_name.equals("deptCode.dept_code")) {
                            if (tmp_obj == null || tmp_obj.equals("")) {
                                if (dept == null) {//当部门为空并且没有指定默认部门时认为是错误数据
                                    row_error_keys.add(field_name);
                                    continue;
                                }
                                tmp_obj = dept.getDept_code();
                            } else if (dept_keys.get(tmp_obj.toString()) == null) {
                                row_error_keys.add(field_name);
                                continue;
                            }
                        } else if (field_name.equals("g10.code")) {
                            if (tmp_obj == null || tmp_obj.equals("")) {
                                tmp_obj = null;
                                tmp_obj = "-1";
                            }
                        } else if (field_name.startsWith("deptCode.") || field_name.startsWith("g10.")) {
                            continue;
                        } else if (update_able_fields.contains(field_name)) {
                            TempFieldInfo field_info = use_fields.get(field_name);
                            String fieldName = field_info.getField_name();
                            if (fieldName.equals("a0191")) {
                                if (tmp_obj == null || tmp_obj.toString().trim().equals("")) {
                                    tmp_obj = ed.getEntityCaption();
                                } else if (class_keys.get(tmp_obj.toString().trim()) == null) {
                                    row_error_keys.add(field_name);
                                    continue;
                                }
                            } else if (fieldName.endsWith("_code_")) {
                                try {
                                    Field field = field_info.getField();
                                    if (tmp_obj == null || tmp_obj.toString().equals("")) {
                                    } else {
                                        ObjectListHint objHint = field.getAnnotation(ObjectListHint.class);
                                        if (objHint != null && objHint.hqlForObjectList().startsWith("from Code ")) {
                                            String hql = objHint.hqlForObjectList();
                                            String code_id = CodeManager.getCodeManager().getCodeIdBy(hql.substring(hql.indexOf("=") + 1), tmp_obj.toString());
                                            if (code_id == null) {
                                                if (code_flag) {
                                                    continue;
                                                }
                                                row_error_keys.add(field_name);
                                                continue;
                                            }
                                            tmp_obj = code_id;
                                        } else {
                                            row_error_keys.add(field_name);
                                            continue;
                                        }
                                    }
                                } catch (SecurityException ex) {
                                    log.error(ex);
                                }
                            } else if (fieldName.equals("a0177")) {
                                if (tmp_obj == null || tmp_obj.toString().equals("")) {
                                } else {
                                    if (a0177_check_flag && !SysUtil.isRightIdentity(tmp_obj.toString())) {
                                        row_error_keys.add(field_name);
                                        continue;
                                    }
                                    if (jRadioButton1.isSelected() && id_in_flag && !tfi.getField_name().equals("a0177")) {
                                        if (a0177_keys.contains(tmp_obj.toString().toLowerCase()) || a0177_keys.contains(tmp_obj.toString().toUpperCase())) {
                                            row_error_keys.add(field_name);
                                            continue;
                                        }
                                    }
                                }
                            } else if (fieldName.equals("a0101")) {
                                if (tmp_obj == null || tmp_obj.toString().equals("")) {
                                    row_error_keys.add(field_name);
                                    continue;
                                }
                            } else {
                                String type = field_info.getField_type();
                                type = type.toLowerCase();
                                if (type.equals("boolean")) {
                                    if (tmp_obj == null || tmp_obj.toString().equals("")) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }
                                        row_error_keys.add(field_name);
                                        continue;
                                    } else if (tmp_obj.toString().toLowerCase().equals("false") || tmp_obj.toString().equals("否")) {
                                        tmp_obj = "0";
                                    } else if (tmp_obj.toString().toLowerCase().equals("true") || tmp_obj.toString().equals("是")) {
                                        tmp_obj = "1";
                                    } else {
                                        if (data_noerror_flag) {
                                            continue;
                                        }
                                        row_error_keys.add(field_name);
                                        continue;
                                    }
                                } else if (type.equals("date")) {
                                    Object obj = DateUtil.StrToDate(tmp_obj.toString());
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }
                                        row_error_keys.add(field_name);
                                        continue;
                                    }
                                    tmp_obj = obj;
                                } else if (type.equals("int")) {
                                    Object obj = SysUtil.objToInteger(tmp_obj, null);
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }
                                        row_error_keys.add(field_name);
                                        continue;
                                    }
                                    tmp_obj = obj;
                                } else if (type.equals("integer")) {
                                    Object obj = SysUtil.objToInteger(tmp_obj);
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }
                                        row_error_keys.add(field_name);
                                        continue;
                                    }
                                    tmp_obj = obj;
                                } else if (type.equals("float")) {
                                    Object obj = (Float) SysUtil.objToFloat(tmp_obj);
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }
                                        row_error_keys.add(field_name);
                                        continue;
                                    }
                                    tmp_obj = obj;
                                } else if (type.equals("bigdecimal")) {
                                    Object obj = SysUtil.objToBigDecimal(tmp_obj);
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }
                                        row_error_keys.add(field_name);
                                        continue;
                                    }
                                    tmp_obj = (BigDecimal) obj;
                                } else if (type.equals("string")) {
                                    tmp_obj = tmp_obj.toString();
//                                    tmp_obj = SysUtil.subByteStr(tmp_obj.toString(), field_info.getField_width());
                                }
                            }
                        }
                        result_row_data.put(field_name, tmp_obj);
                    }
                    error_data_flag = row_error_keys.size() > 0;
                    if (error_data_flag) {
                        error_keys.put(k, row_error_keys);
                        error_list.add(row_data);
                        k++;
                        error_data_flag = false;
                        continue;
                    }
                    ri_table2.put(compare_val, row_data);
                    ri_table.put(compare_val, row_data);
                    result_data.put(compare_val, result_row_data);
                    // keyCount.put(compare_val, i);
                    keys.add(cm_str);
                    comp_keys.put(compare_val, i);
                } catch (IllegalArgumentException ex) {
                    log.error(ex);
                }
            } else {
                result_data_new.remove(compare_val);
                result_data.remove(compare_val);
                keys.remove(cm_str);
                //去掉重复记录
                repeat_list.add(row_data);
                repeat_list.add(ri_table2.get(compare_val));
                ri_table2.remove(compare_val);
                result_data.remove(compare_val);
            }
        }
        String s_val = tfi.getField_name();
        String s_a0101 = "";

        if (com_flag) {
            s_val = "a0190";
            s_a0101 = ",a0101";
        }
        String dept_right_str = "";//部门权限条件
        String dept_select_str = "";//选择部门
        if (!register_flag && cur_a01.getDeptCode() != null) {
            dept_select_str = " and d.dept_code like '" + cur_a01.getDeptCode().getDept_code() + "%'";
        }
        if (!register_flag && !UserContext.isSA) {
            dept_right_str = " and(" + UserContext.getDept_right_rea_str("") + ")";
        }
        if (jRadioButton1.isSelected()) {
            String a0191_str = EmpUtil.getA0191_string();
            exist_a01.addAll(CommUtil.selectSQL("select " + s_val + ",a01_key,d.dept_code" + s_a0101 + " from A01 a01,DeptCode d "
                    + "where a01.deptCode_key=d.deptCode_key " + dept_select_str + dept_right_str + "and (a01.a0191 in" + a0191_str + ")" + "and a01." + s_val + " in ", keys, !str_flag));
        } else {

            exist_a01.addAll(CommUtil.selectSQL("select " + s_val + ",a01_key,d.dept_code" + s_a0101 + " from A01 a01,DeptCode d "
                    + "where a01.deptCode_key=d.deptCode_key " + dept_select_str + dept_right_str + " and a01." + s_val + " in ", keys, !str_flag));
        }

        //数据更新部分---------------------------------------------------------------------------------------------


        if (exist_a01 != null) {
            leng_exist = 0;
            String com_val = "";
            String f_name = "";
            if (com_flag) {
                f_name = "a0190";
            } else {
                f_name = tfi.getField_name();
            }
            List<String> exits_val = new ArrayList<String>();
            for (String key : result_data.keySet()) {
                exits_val.add(key);
            }
            for (Object obj : exist_a01) {
                Object[] objs = (Object[]) obj;
                if (com_flag) {
                    com_val = objs[0].toString() + "_" + (objs[3] == null ? "" : objs[3].toString());
                } else {
                    if ("a0177".equals(s_val)) {
                        com_val = objs[0].toString().toUpperCase();
                    } else {
                        com_val = objs[0].toString();
                    }
                }
                if (dept_keys.get(objs[2].toString()) == null) {
                    redept_list.add(xlsImportInfo.getValues().get(comp_keys.get(com_val)));
                    result_data.remove(com_val);
                    continue;
                } else if (id_in_flag && !"a0177".equals(f_name) && objs[0] != null && result_data.get(com_val) != null && result_data.get(com_val).get("a0177") != null && a0177_keys.contains(result_data.get(com_val).get("a0177").toString())) {
                    repeat_list.add(xlsImportInfo.getValues().get(comp_keys.get(com_val)));
                    result_data.remove(com_val);
                    result_data.remove(com_val);
                    continue;
                }
                if (register_flag) {
                    for (String key : exits_val) {
                        if (key.equals(com_val)) {
                            repeat_list.add(xlsImportInfo.getValues().get(comp_keys.get(com_val)));
                            result_data.remove(com_val);
                            leng_exist++;
                            continue;
                        }
                    }
                }
                exist_keys.put(com_val, objs[1].toString());
                exist_dept_keys.put(com_val, objs[2].toString());
            }
        }
        String a01_key;
        String a0191;
        Class new_class = null;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                lblInfo.setText(EmpMngMsg.ttl025.toString());
                jProgressBar1.setValue(0);
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(result_data.values().size());
            }
        });
        for (String compare : result_data.keySet()) {
            Hashtable<String, Object> row_data = result_data.get(compare);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                }
            });
            a01_key = exist_keys.get(compare);
            if (a01_key == null) {
                if (update_type == 0 || update_type == 1) {
                    Object person_class = row_data.get("a0191");
                    if (person_class == null || person_class.toString().trim().equals("")) {
                        a0191 = ed.getEntityCaption();
                        new_class = assign_class;
                    } else {
                        a0191 = person_class.toString().trim();
                        new_class = class_keys.get(a0191);
                    }
                    if (new_class == null) {
                        continue;
                    }
                    try {
                        A01 obj = (A01) UtilTool.createUIDEntity(new_class);
                        Method method1 = new_class.getMethod("setA0191", new Class[]{String.class});
                        method1.invoke(obj, new Object[]{a0191});
                        method1 = new_class.getMethod("setDeptCode", new Class[]{DeptCode.class});
                        method1.invoke(obj, new Object[]{dept});
                        for (String field_name : use_fields.keySet()) {
                            TempFieldInfo field_info = use_fields.get(field_name);
                            String field_type = field_info.getField_type();
                            String fieldName = field_info.getField_name();
                            Field field = field_info.getField();
                            if (field_name.equals("deptCode.dept_code")) {
                                DeptCode dept1 = dept_keys.get(row_data.get(field_name).toString());
                                if (dept1 == null) {
                                    continue;
                                }
                                field_name = "deptCode";
                                Method method = new_class.getMethod("set" + field_name.substring(0, 1).toUpperCase() + field_name.substring(1), new Class[]{DeptCode.class});
                                method.invoke(obj, new Object[]{dept1});
                                continue;
                            }
                            //对岗位单独处理
                            if (field_name.equals("g10.code")) {
                                String temp_g10_code = row_data.get(field_name).toString();
                                if ("-1".equals(temp_g10_code)) {
                                    continue;
                                }
                                String temp_d_code = "-1";
                                if (row_data.get("deptCode.dept_code") != null) {
                                    temp_d_code = row_data.get("deptCode.dept_code").toString();
                                } else if (dept != null) {
                                    temp_d_code = dept.getDept_code();
                                }
                                temp_g10_code = temp_d_code + "_" + temp_g10_code;
                                
                                continue;
                            }
                            if (!update_able_fields.contains(field_name)) {
                                continue;
                            }
                            Object tmp_obj = row_data.get(field_name);
                            if ("boolean".equals(field_type)) {
                                tmp_obj = tmp_obj.toString().equals("1");
                            }
                            Class field_class = field.getType();
                            if (fieldName.endsWith("_code_")) {
                                fieldName = fieldName.replace("_code_", "");
                                field_class = String.class;
                            }
                            if (tmp_obj == null) {
                                continue;
                            }
                            try {
                                Method method = new_class.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{field_class});
                                method.invoke(obj, new Object[]{tmp_obj});
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }
                        }
                        obj.setA0193(2);
                        insert_list.add(ri_table.get(compare));
                        save_objs.add(obj);
                        save_objects.add(obj);
                        new_a01_keys.add(((A01) obj).getA01_key());
                    } catch (Exception ex) {
                        log.error(ex);
                    }
                } else if (update_type == 2) {
                    notFind_list.add(xlsImportInfo.getValues().get(comp_keys.get(compare)));
                }
            } else {
                if (update_type == 1 || update_type == 2) {
                    update_no++;
                    ex_sql.append("update a01 set ");
                    for (String field_name : use_fields.keySet()) {
                        String tmp = field_name.trim().toLowerCase();
                        if (tmp.equals("a0190") || tmp.equals("a0191") || tmp.equals("a0191_code_") || tmp.startsWith("deptcode") || tmp.equals("a0101")) {
                            continue;
                        }
                        //对岗位单独处理
                        if (field_name.equals("g10.code")) {
                            String temp_g10_code = row_data.get(field_name).toString();
                            if ("-1".equals(temp_g10_code)) {
                                continue;
                            }
                            String temp_d_code = "-1";
                            if (row_data.get("deptCode.dept_code") != null) {
                                temp_d_code = row_data.get("deptCode.dept_code").toString();
                            } else if (exist_dept_keys.get(compare) != null) {
                                temp_d_code = exist_dept_keys.get(compare);
                            } else if (dept != null) {
                                temp_d_code = dept.getDept_code();
                            }
                            temp_g10_code = temp_d_code + "_" + temp_g10_code;
                            
                            continue;
                        }
                        if (!update_able_fields.contains(field_name)) {
                            continue;
                        }
                        Object value = row_data.get(field_name);
                        TempFieldInfo field_info = use_fields.get(field_name);
                        String type = field_info.getField_type().toLowerCase();
                        if (value == null && !(type.equals("string") || type.equals("code"))) {
                            continue;
                        }
                        ex_sql.append(field_name.replace("_code_", ""));
                        ex_sql.append("=");
                        if (type.equals("date")) {
                            ex_sql.append(DateUtil.toStringForQuery((Date) value));
                            ex_sql.append(",");
                        } else if (type.equals("string") || type.equals("code")) {
                            if (value == null) {
                                ex_sql.append("null,");
                            } else {
                                ex_sql.append("'");
                                String tmp_val = value.toString();
                                if ("a0177".equals(tfi.getField_name())) {
                                    tmp_val = tmp_val.toUpperCase();
                                }
                                ex_sql.append(tmp_val);
                                ex_sql.append("',");
                            }
                        } else {
                            ex_sql.append(value.toString());
                            ex_sql.append(",");
                        }
                    }
                    ex_sql.append("a0191=a0191 where a01_key='");
                    ex_sql.append(a01_key);
                    ex_sql.append("';\n");
                    update_a01_keys.add(a01_key);
                }
            }
        }
        return 0;

    }

    private int check2(String fileName, DeptCode dept, EntityDef ed, int update_type, TempFieldInfo tfi, boolean a0177_check_flag, boolean id_in_flag) {
        //辅助变量定义部分---------------------------------------------------------------------------------------------
        error_keys.clear();
        a0191_a01_keys.clear();
        update_a01_keys.clear();
        repeat_list.clear();
        error_list.clear();
        save_objects.clear();
        insert_list.clear();
        n_fields.clear();
        update_no = 0;
        ex_sql = new StringBuffer();
        boolean code_flag = true;
        boolean data_noerror_flag = true;
        XlsImportInfo xlsImportInfo = null;
        try {
            xlsImportInfo = ReadXLS.importXls(new File(fileName));
        } catch (Exception ex) {
            log.error(ex);
            return 6;
        }
        exportScheme = xlsImportInfo.getExportScheme();//当前显示方案
        SysParameter update_para = UserContext.getSys_para("System.import_updateable");//系统更新参数
        if (register_flag) {
            update_para = UserContext.getSys_para("Register.field_flag");//系统更新参数
        }
//        SysParameter update_para_re = UserContext.getSys_para("Register.field_flag");//系统更新参数
        HashSet<String> update_able_fields = new HashSet<String>();//用于记录允许更新的字段
        Hashtable<String, TempFieldInfo> use_fields = new Hashtable<String, TempFieldInfo>();
        Hashtable<String, Hashtable<String, String>> ri_table = new Hashtable<String, Hashtable<String, String>>();
        boolean compare_flag = false;//是否存在匹配字段
        boolean str_flag = false;//用以区分搜索语句拼接时是否加单引号'
        StringBuffer str = new StringBuffer();
        List exist_a01 = new ArrayList();//用于记录数据库中已存在的记录
        final Hashtable<String, Hashtable<String, Object>> result_data_new = new Hashtable<String, Hashtable<String, Object>>();//用于记录读取出来转换成对象的数据
        final Hashtable<String, Hashtable<String, Object>> result_data = new Hashtable<String, Hashtable<String, Object>>();//用于记录读取出来转换成对象的数据
        Hashtable<String, String> exist_keys = new Hashtable<String, String>();//用于记录数据库中已经存在的记录，索引为匹配字段值，值为a01_key(即主键值)
        Hashtable<String, String> a0191_exist_keys = new Hashtable<String, String>();//用于记录数据库中已经存在的记录，索引为匹配字段值，值为a01_key(即主键值)
        String compare_val;//匹配值
        Class assign_class = null;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                lblInfo.setText(EmpMngMsg.ttl024.toString());
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(exportScheme.getExportDetails().size());
                jProgressBar1.setValue(0);
            }
        });

        List<ExportDetail> error_details = new ArrayList<ExportDetail>();
        for (ExportDetail exportDetail : exportScheme.getExportDetails()) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                }
            });
            String field_name = exportDetail.getField_name();
            n_fields.add(field_name.toLowerCase());
            if (field_name.equals("deptCode.dept_code")) {
                use_fields.put(field_name, field_keys.get("deptCode.dept_code"));
                exportDetail.setField_type("String");
            } else if (field_name.startsWith("deptCode.")) {
                exportDetail.setField_type("String");
                error_details.add(exportDetail);
                continue;
            } else {
                if (field_name.equals("a0101")) {
                    exportDetail.setField_type("String");
                    use_fields.put(field_name, field_keys.get("a0101"));
                }
                if (field_name.equals(tfi.getField_name())) {
                    exportDetail.setField_type(tfi.getField_type().equals("Code") ? "String" : tfi.getField_type());
                    use_fields.put(field_name, tfi);
                    compare_flag = true;
                }
                TempFieldInfo type = field_keys.get(field_name);
                if (type != null) {
                    exportDetail.setField_type(tfi.getField_type());
                    use_fields.put(field_name, field_keys.get(field_name));
                } else {
                    type = field_keys.get(field_name + "_code_");
                    if (type != null) {
                        exportDetail.setField_type("String");
                        use_fields.put(field_name, type);
                    } else if (field_name.length() > 6) {
                        type = field_keys.get(field_name.substring(0, field_name.length() - 6));
                        if (type != null) {
                            exportDetail.setField_type("String");
                            use_fields.put(field_name, type);
                        }
                    }
                }
            }
        }
        if (!compare_flag) {
            return 1;
        }
        //---------获得权限字段----------------------------------------------
        for (String field_name : use_fields.keySet()) {
            String fieldName = field_name.toUpperCase();
            if (field_name.equals("DEPTCODE.DEPT_CODE")) {
                if (UserContext.getFieldRight(fieldName) == 1) {
                    update_able_fields.add(field_name);
                } else if (UserContext.getFieldRight(fieldName) == 2 && update_para.getSysparameter_value().equals("1")) {
                    update_able_fields.add(field_name);
                }
            } else {
                fieldName = "A01." + fieldName.replace("_CODE_", "");
                if (UserContext.getFieldRight(fieldName) == 1) {
                    update_able_fields.add(field_name);
                } else if (UserContext.getFieldRight(fieldName) == 2 && "1".equals(update_para.getSysparameter_value())) {
                    update_able_fields.add(field_name);
                }
            }
        }

        if (register_flag && !update_able_fields.contains(tfi.getField_name())) {
            return 1;
        }
        if (register_flag && (!update_able_fields.contains("a0190") || !update_able_fields.contains("a0101"))) {
            return 5;
        }
        try {
            //匹配值
            assign_class = Class.forName("org.jhrcore.entity." + ed.getEntityName());
        } catch (ClassNotFoundException ex) {
            log.error(ex);
        }
        //原始数据处理部分---------------------------------------------------------------------------------------------
        if (tfi.getField_type().equals("String")) {
            str_flag = true;
        }

        if (!compare_flag) {
            return 1;
        }
        final int len = xlsImportInfo.getValues().size();
        int k = 0;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                lblInfo.setText(EmpMngMsg.ttl023.toString());
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(len);
                jProgressBar1.setValue(0);
            }
        });
        for (int i = 0; i < len; i++) {
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                }
            });
            Hashtable<String, String> row_data = xlsImportInfo.getValues().get(i);
            Hashtable<String, Object> result_row_data = new Hashtable<String, Object>();
            compare_val = row_data.get(tfi.getField_name());
            result_data_new.put(compare_val, result_row_data);
            if (result_data.get(compare_val) == null) {
                try {
                    for (String field_name : use_fields.keySet()) {
                        Object tmp_obj = row_data.get(field_name);
                        if (field_name.equals("deptCode.dept_code")) {
                            if (tmp_obj == null || tmp_obj.equals("")) {
                                if (dept == null) {//当部门为空并且没有指定默认部门时认为是错误数据
                                    tmp_obj = "";
                                } else {
                                    tmp_obj = dept.getDept_code();
                                }

                            } else if (dept_keys.get(tmp_obj.toString()) == null) {
                            }
                        } else if (field_name.startsWith("deptCode.")) {
                            continue;
                        } else if (update_able_fields.contains(field_name)) {
                            TempFieldInfo field_info = use_fields.get(field_name);
                            String fieldName = field_info.getField_name();
                            if (fieldName.equals("a0191")) {
                                if (tmp_obj == null || tmp_obj.toString().trim().equals("")) {
                                    tmp_obj = ed.getEntityCaption();
                                } else if (class_keys.get(tmp_obj.toString().trim()) == null) {
                                }
                            } else if (fieldName.endsWith("_code_")) {
                                try {
                                    Field field = field_info.getField();
                                    if (tmp_obj == null || tmp_obj.toString().equals("")) {
                                    } else {
                                        ObjectListHint objHint = field.getAnnotation(ObjectListHint.class);
                                        if (objHint != null && objHint.hqlForObjectList().startsWith("from Code ")) {
                                            String hql = objHint.hqlForObjectList();
                                            String code_id = CodeManager.getCodeManager().getCodeIdBy(hql.substring(hql.indexOf("=") + 1), tmp_obj.toString());
                                            if (code_id == null) {
                                                if (code_flag) {
                                                    continue;
                                                }
                                            }
                                            tmp_obj = code_id;
                                        } else {
                                        }
                                    }
                                } catch (SecurityException ex) {
                                    log.error(ex);
                                }
                            } else {
                                String type = field_info.getField_type();
                                type = type.toLowerCase();
                                if (type.equals("boolean")) {
                                    if (tmp_obj == null || tmp_obj.toString().equals("")) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }

                                        continue;
                                    } else if (tmp_obj.toString().toLowerCase().equals("false") || tmp_obj.toString().equals("否")) {
                                        tmp_obj = "0";
                                    } else if (tmp_obj.toString().toLowerCase().equals("true") || tmp_obj.toString().equals("是")) {
                                        tmp_obj = "1";
                                    } else {
                                        if (data_noerror_flag) {
                                            continue;
                                        }

                                    }
                                } else if (type.equals("date")) {
                                    Object obj = DateUtil.StrToDate(tmp_obj.toString());
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }

                                    }
                                    tmp_obj = obj;
                                } else if (type.equals("int")) {
                                    Object obj = SysUtil.objToInt(tmp_obj);
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }

                                    }
                                    tmp_obj = obj;
                                } else if (type.equals("integer")) {
                                    Object obj = SysUtil.objToInteger(tmp_obj);
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }

                                    }
                                    tmp_obj = obj;
                                } else if (type.equals("float")) {
                                    Object obj = (Float) SysUtil.objToFloat(tmp_obj);
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }

                                    }
                                    tmp_obj = obj;
                                } else if (type.equals("bigdecimal")) {
                                    Object obj = SysUtil.objToBigDecimal(tmp_obj);
                                    if (obj == null) {
                                        if (data_noerror_flag) {
                                            continue;
                                        }

                                    }
                                    tmp_obj = (BigDecimal) obj;
                                } else if (type.equals("string")) {
                                    tmp_obj = tmp_obj.toString();
//                                    tmp_obj = SysUtil.subByteStr(tmp_obj.toString(), field_info.getField_width());
                                }
                            }
                        }
                        result_row_data.put(field_name, tmp_obj);
                    }
                    ri_table.put(compare_val, row_data);
                    result_data.put(compare_val, result_row_data);
                } catch (IllegalArgumentException ex) {
                    log.error(ex);
                }
            } else {
                result_data_new.remove(compare_val);
                result_data.remove(compare_val);
                repeat_list.add(row_data);
            }
        }
        List<String> keys = new ArrayList<String>();
        keys.addAll(result_data.keySet());


        exist_a01.addAll(CommUtil.selectSQL("select " + tfi.getField_name() + ",a01_key,a0191 from A01 a01  where a01." + tfi.getField_name() + " in ", keys, !str_flag));
        //数据更新部分---------------------------------------------------------------------------------------------
        if (exist_a01 != null) {
            for (Object obj : exist_a01) {
                Object[] objs = (Object[]) obj;
                exist_keys.put(objs[0].toString(), objs[1].toString());
                a0191_exist_keys.put(objs[0].toString(), objs[2].toString());
            }
        }
        String a01_key;
        String a0191;
        Class new_class;
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                lblInfo.setText(EmpMngMsg.ttl020.toString());
                jProgressBar1.setValue(0);
                jProgressBar1.setMinimum(0);
                jProgressBar1.setMaximum(result_data.values().size());
            }
        });
        for (String compare : result_data.keySet()) {
            Hashtable<String, Object> row_data = result_data.get(compare);
            SwingUtilities.invokeLater(new Runnable() {

                @Override
                public void run() {
                    jProgressBar1.setValue(jProgressBar1.getValue() + 1);
                }
            });
            a01_key = exist_keys.get(compare);
            if (a01_key == null) {
                if (update_type == 0 || update_type == 1) {
                    Object person_class = row_data.get("a0191");
                    if (person_class == null || person_class.toString().trim().equals("")) {
                        a0191 = ed.getEntityCaption();
                        new_class = assign_class;
                    } else {
                        a0191 = person_class.toString().trim();
                        new_class = class_keys.get(a0191);
                    }
                    try {
                        Object obj = UtilTool.createUIDEntity(new_class);
                        Method method1 = new_class.getMethod("setA0191", new Class[]{String.class});
                        method1.invoke(obj, new Object[]{a0191});
                        method1 = new_class.getMethod("setDeptCode", new Class[]{DeptCode.class});
                        method1.invoke(obj, new Object[]{dept});
                        for (String field_name : use_fields.keySet()) {
                            TempFieldInfo field_info = use_fields.get(field_name);
                            String field_type = field_info.getField_type();
                            String fieldName = field_info.getField_name();
                            Field field = field_info.getField();
                            if (field_name.equals("deptCode.dept_code")) {
                                DeptCode dept1 = dept_keys.get(row_data.get(field_name).toString());
                                if (dept1 == null) {
                                    continue;
                                }
                                field_name = "deptCode";
                                Method method = new_class.getMethod("set" + field_name.substring(0, 1).toUpperCase() + field_name.substring(1), new Class[]{DeptCode.class});
                                method.invoke(obj, new Object[]{dept1});
                                continue;
                            }
                            Object tmp_obj = row_data.get(field_name);
                            if ("boolean".equals(field_type)) {
                                tmp_obj = tmp_obj.toString().equals("1");
                            }
                            Class field_class = field.getType();
                            if (fieldName.endsWith("_code_")) {
                                fieldName = fieldName.replace("_code_", "");
                                field_class = String.class;
                            }
                            if (tmp_obj == null) {
                                continue;
                            }
                            try {
                                Method method = new_class.getMethod("set" + fieldName.substring(0, 1).toUpperCase() + fieldName.substring(1), new Class[]{field_class});
                                method.invoke(obj, new Object[]{tmp_obj});
                            } catch (Exception e) {
                                //e.printStackTrace();
                            }

                        }
                        insert_list.add(ri_table.get(compare));
                        save_objects.add(obj);
                        new_a01_keys.add(((A01) obj).getA01_key());
                    } catch (SecurityException ex) {
                        log.error(ex);
                    } catch (IllegalArgumentException ex) {
                        log.error(ex);
                    } catch (InvocationTargetException ex) {
                        log.error(ex);
                    } catch (NoSuchMethodException ex) {
                        log.error(ex);
                    } catch (IllegalAccessException ex) {
                        log.error(ex);
                    }
                }
            } else {
                if (!use_fields.keySet().contains("a0191")) {
                    if (!ed.getEntityCaption().replace(" ", "").equals(a0191_exist_keys.get(compare).replace(" ", ""))) {
                        a0191_a01_keys.add(a01_key);
                        a0191_table.put(a01_key, ed.getEntityCaption().replace(" ", ""));
                    }
                } else {
                    Object value = row_data.get("a0191");
                    if ("".equals(value.toString().replace(" ", ""))) {
                        if (!ed.getEntityCaption().replace(" ", "").equals(a0191_exist_keys.get(compare).replace(" ", ""))) {
                            a0191_a01_keys.add(a01_key);
                            a0191_table.put(a01_key, ed.getEntityCaption().replace(" ", ""));
                        }
                    } else {
                        if (!value.toString().replace(" ", "").equals(a0191_exist_keys.get(compare).replace(" ", ""))) {
                            a0191_a01_keys.add(a01_key);
                            a0191_table.put(a01_key, value.toString().replace(" ", ""));
                        }
                    }
                }
                update_a01_keys.add(a01_key);
            }
        }
        return 0;
    }

    private void export_insert_data() {
        String fileName = select_file.getPath();
        fileName = fileName.replace(".xls", "_insert.xls");
        ExportUtil.export(fileName, exportScheme, insert_list);
    }

    /**
     * 用于校验或者更新后产生的消息报告
     * @param fileName：错误数据放置路径
     * @param ischeck：是否校验
     */
    private void report_msg(String fileName, boolean ischeck, int check_flag) {
        if (check_flag == 0) {
            fileName = fileName.replace(".xls", "1.xls");
            repeat_list.addAll(notFind_list);
            ExportUtil.export(fileName, exportScheme, repeat_list, error_list, error_keys);
            String export_msg = EmpMngMsg.msg098.toString() + "\n";
            if (ischeck) {
                export_msg += EmpMngMsg.msg099.toString() + update_no + EmpMngMsg.msg096.toString() + "\n";
                export_msg += EmpMngMsg.msg100.toString() + save_objs.size() + EmpMngMsg.msg096.toString() + "\n";
                export_msg += EmpMngMsg.msg101.toString() + (repeat_list.size() - notFind_list.size() - leng_exist) + EmpMngMsg.msg096.toString() + "\n";
                if (register_flag) {
                    export_msg += EmpMngMsg.msg102.toString() + leng_exist + EmpMngMsg.msg096.toString() + "\n";
                }
                if (!register_flag) {
                    export_msg += EmpMngMsg.msg103.toString() + notFind_list.size() + EmpMngMsg.msg096.toString() + "\n";
                    if (!redept_list.isEmpty()) {
                        export_msg += EmpMngMsg.msg104.toString() + redept_list.size() + EmpMngMsg.msg096.toString() + "\n";
                    }
                }
                export_msg += EmpMngMsg.msg105.toString() + error_list.size() + EmpMngMsg.msg096.toString() + "\n";
            } else {
                export_msg += EmpMngMsg.msg106.toString() + update_no + EmpMngMsg.msg096.toString() + "\n";
                export_msg += EmpMngMsg.msg107.toString() + save_objs.size() + EmpMngMsg.msg096.toString() + "\n";
                export_msg += EmpMngMsg.msg108.toString() + (repeat_list.size() - notFind_list.size() - leng_exist) + EmpMngMsg.msg096.toString() + "\n";
                if (register_flag) {
                    export_msg += EmpMngMsg.msg109.toString() + leng_exist + EmpMngMsg.msg096.toString() + "\n";
                }
                if (!register_flag) {
                    export_msg += EmpMngMsg.msg110.toString() + notFind_list.size() + EmpMngMsg.msg096.toString() + "\n";
                    if (!redept_list.isEmpty()) {
                        export_msg += EmpMngMsg.msg111.toString() + redept_list.size() + EmpMngMsg.msg096.toString() + "\n";
                    }
                }
                export_msg += EmpMngMsg.msg112.toString() + error_list.size() + EmpMngMsg.msg096.toString() + "\n";
//                save_flag = true;
                for (IPickPersonImportListener listener : listeners) {
                    listener.refreshData();
                }
            }
            MsgUtil.showHRValidateReportMsg(export_msg);
        } else if (check_flag == 1) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "导入文件中不包含匹配列或匹配列不在用户权限范围内", "错误", JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpMngMsg.msg059);
        } else if (check_flag == 2) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "导入文件中不包含姓名列", "错误", JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpMngMsg.msg060);
        } else if (check_flag == 3) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "数据表中无部门代码字段，而导入界面中又未设置默认部门", "错误", JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpMngMsg.msg061);
        } else if (check_flag == 4) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "数据库更新错误", "错误", JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpMngMsg.msg062);
        } else if (check_flag == 5) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "人员编号列(a0190)或姓名列(a0101)不在用户权限范围内,不允许导入", "错误", JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpMngMsg.msg063);
        } else if (check_flag == 6) {
//            JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnOk), "导入文件格式有误,请检查表批注、字段批注等", "错误", JOptionPane.ERROR_MESSAGE);
            MsgUtil.showErrorMsg(EmpMngMsg.msg064);
        }

    }

    /**
     *
     * @param fileName:导入的文件路径
     * @param ed：人员类别表
     * @param update_type：更新方式，0：追加；1：更新并追加；2：只更新
     * @param tfi：匹配指标
     * @param id_check_flag：是否验证身份证
     *
     */
    private int importXls(String fileName, DeptCode dept, EntityDef ed, int update_type, TempFieldInfo tfi, boolean a0177_check_flag, boolean id_in_flag) {
        int i = 0;
        if (cur_dept == null) {
            if (dept != null) {
                isCheck = false;
            }
        } else {
            if (dept == null) {
                isCheck = false;
            } else if (!cur_dept.getDeptCode_key().equals(dept.getDeptCode_key())) {
                isCheck = false;
            }
        }
        if (!isCheck) {
            i = check(fileName, dept, ed, update_type, tfi, a0177_check_flag, id_in_flag);
        }
        if (i != 0) {
            return i;
        }
        Runnable tmp_run2 = new Runnable() {

            @Override
            public void run() {
                if (ContextManager.getMainFrame() != null) {
                    ContextManager.getMainFrame().setCursor(Cursor.getPredefinedCursor(Cursor.WAIT_CURSOR));
                }
                lblInfo.setText(EmpMngMsg.msg097.toString());
            }
        };
        SwingUtilities.invokeLater(tmp_run2);
        final ValidateSQLResult validateSQLResult = ImportImpl.importA01Data(ex_sql.toString(), save_objs, EmpUtil.getCommRyAddLog());
        Runnable tmp_run = new Runnable() {

            @Override
            public void run() {
                if (validateSQLResult.getError_result() != 0) {
                    String msg = "";
                    msg += EmpMngMsg.msg093.toString() + validateSQLResult.getInsert_result() + EmpMngMsg.msg096.toString() + "\n";
                    msg += EmpMngMsg.msg094.toString() + validateSQLResult.getUpdate_result() + EmpMngMsg.msg096.toString() + "\n";
                    msg += EmpMngMsg.msg095.toString() + validateSQLResult.getError_result() + EmpMngMsg.msg096.toString() + "\n";
                    msg += validateSQLResult.getMsg();
                    MsgUtil.showHRSaveErrorMsg(msg);
                }
            }
        };
        SwingUtilities.invokeLater(tmp_run);
        if (validateSQLResult.getError_result() != 0) {
            return 4;
        }
        return 0;
    }

    @Override
    public String getModuleCode() {
        return module_code;
    }
}
