/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * FormulaPanel.java
 *
 * Created on 2009-3-9, 11:15:09
 */
package org.jhrcore.client.formula;

import org.jhrcore.comm.HrLog;
import org.jhrcore.util.DbUtil;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.ui.listener.IPickFieldSelectListener;
import org.jhrcore.ui.FormulaParaFieldSelectPanel;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseEvent;
import javax.swing.event.DocumentEvent;
import javax.swing.event.ListSelectionEvent;
import org.jhrcore.client.*;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.MouseAdapter;
import java.io.File;
import java.io.IOException;
import java.rmi.server.UID;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import java.util.Set;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTree;
import javax.swing.SwingUtilities;
import javax.swing.event.DocumentListener;
import javax.swing.event.ListSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import jxl.Workbook;
import jxl.format.BorderLineStyle;
import jxl.format.Colour;
import jxl.format.UnderlineStyle;
import jxl.format.Alignment;
import jxl.format.Border;
import jxl.write.Label;
import jxl.write.WritableCellFeatures;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.beansbinding.BeanProperty;
import org.jdesktop.beansbinding.Binding;
import org.jdesktop.beansbinding.BindingListener;
import org.jdesktop.beansbinding.ELProperty;
import org.jdesktop.beansbinding.Property;
import org.jdesktop.beansbinding.PropertyStateEvent;
import org.jdesktop.swingbinding.JTableBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.contract.IPickRefreshDataListener;
import org.jhrcore.ui.WizardDialog;
import org.jhrcore.entity.Code;
import org.jhrcore.comm.CodeManager;
import org.jhrcore.entity.ExportDetail;
import org.jhrcore.entity.FormulaDetail;
import org.jhrcore.entity.FormulaScheme;
import org.jhrcore.util.UtilTool;
import org.jhrcore.entity.annotation.ObjectListHint;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.CommImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.ui.CodeSelectDialog;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.BeanPanel;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.EditorFactory;
import org.jhrcore.ui.FormulaEditorPanel;
import org.jhrcore.ui.HrTextPane;
import org.jhrcore.ui.listener.IPickWindowCloseListener;
import org.jhrcore.ui.ModelFrame;
import org.jhrcore.ui.SearchTreeFieldDialog;
import org.jhrcore.ui.TreeSelectMod;
import org.jhrcore.ui.listener.BindingAdapter;
import org.jhrcore.ui.listener.IPickFormulaEditorListener;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.FileChooserUtil;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author mxliteboss
 */
public class FormulaPanel extends javax.swing.JPanel implements IPickWindowCloseListener {

    private JButton btnAdd = new JButton("新增公式套");
    private JButton btnEditName = new JButton("改方案名");
    private JButton btnSaveAs = new JButton("公式套另存");
    private JButton btnOutFormula = new JButton("输出公式");
    private JButton btnSaveScheme = new JButton("保存方案");
    private JButton btnDel = new JButton("删除公式套");
    private JButton btnCancel = new JButton("退出");
    private FormulaEditorPanel pnlEditor = new FormulaEditorPanel();
    private List formulaSchemes = new ArrayList();
    private List formulaDetails = new ArrayList();
    private JTableBinding detail_binding;
    private JTable formula_table = new JTable();
    private JTableBinding formula_binding;
    private JTable detail_table = new JTable();
    private FormulaScheme cur_scheme;
    private String scheme_code;
    private String scheme_id;
    private FormulaParaModel para_model;
    private JTree para_tree;
    private List entity_list;
    private List para_list;
    private List list_item;
    private List list_item1;
    private HrTextPane jtaFormulaText;
    private boolean change_flag = false;//当前是否存在变化的detail，如果存在，则会提示需要保存
    private Set save_details = new HashSet();
    private FormulaDetail cur_detail;
    private boolean detail_change_flag = false;//当前detail是否变化，如果变化，则在切换或者关闭时会验证该detail
    private DocumentListener doc_listener;
    private int sys_no = 0;
    private JScrollPane scrollPane = null;
    private ListSelectionListener detail_listener;
    private ListSelectionListener formula_listener;
    private Hashtable<String, List<TempFieldInfo>> pay_infos;
    private List<IPickRefreshDataListener> listeners = new ArrayList<IPickRefreshDataListener>();
    private HrLog log = new HrLog("PaySystem.公式编辑");

    public void addIPickRefreshDataListener(IPickRefreshDataListener listener) {
        listeners.remove(listener);
    }

    public void delIPickRefreshDataListener(IPickRefreshDataListener listener) {
        listeners.remove(listener);
    }
    private BindingListener formula_bindingListener = new BindingAdapter() {

        @Override
        public void targetChanged(Binding arg0, PropertyStateEvent arg1) {
            if (cur_scheme == null) {
                return;
            }
            Object obj = arg1.getNewValue();
            cur_scheme.setUsed(new Boolean(obj.toString()).booleanValue());
            CommUtil.updateEntity(cur_scheme);
        }
    };
    private BindingListener detail_bindingListener = new BindingAdapter() {

        @Override
        public void targetChanged(Binding arg0, PropertyStateEvent arg1) {
            if (cur_detail != null) {
                Object obj = arg1.getNewValue();
                cur_detail.setUse_flag(new Boolean(obj.toString()).booleanValue());
                SwingUtilities.invokeLater(new Runnable() {

                    @Override
                    public void run() {
                        saveCurDetail(true, cur_detail);
                    }
                });
            }
            change_flag = true;
        }
    };

    public FormulaPanel(String scheme_code, String scheme_id, Hashtable<String, List<TempFieldInfo>> pay_infos, List entity_list, List para_list, List list_item, List list_item1) {
        this.scheme_code = scheme_code;
        this.scheme_id = scheme_id;
        this.entity_list = entity_list;
        this.para_list = para_list;
        this.list_item = list_item;
        this.list_item1 = list_item1;
        this.pay_infos = pay_infos;
        initComponents();
        initOthers();
        setupEvents();
    }

    /**
     * 将公式中的参数转换成计算值
     *
     * @param formula_meaning：目标公式SQL
     * @param scheme_code：
     * @return
     */
    private String transfer_para_to_SQL(String formula_meaning, String scheme_code) {
        String tmp = formula_meaning;
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        String gz_count = "1";
        String paySystem_key = "" + scheme_id;
        String begin_date = "2009-03-01";//     = "'" + df.format(ps.getGz_start()) + "'";
        String end_date = "2009-03-31";//       = "'" + df.format(ps.getGz_end()) + "'";
        Calendar c = Calendar.getInstance();
        Date date1 = new Date();
        c.setTime(date1);
        c.add(Calendar.MONTH, 1);
        begin_date = "'" + df.format(date1) + "'";
        end_date = "'" + df.format(c.getTime()) + "'";
        df = new SimpleDateFormat("yyyyMM");
        String month = "'" + df.format(date1) + "'";
        tmp = tmp.replace("@发放次数", gz_count);
        tmp = tmp.replace("@薪酬体系id", paySystem_key);
        tmp = tmp.replace("@开始日期", begin_date);
        tmp = tmp.replace("@结束日期", end_date);
        tmp = tmp.replace("@计算月份", month);
        if ("K_leave_standard".equals(scheme_code)) {
            tmp = tmp.replace("计算年份", "2000").replace("计算月份", "200001");
        }
        return tmp;
    }

    /**
     * 该方法将计算公式转换成为sql语句，
     * @param formula_meaning：sql框中用户输入的Sql语句
     * @param tran_param:等于0的时候表示转换成保存到数据库的语句，这个时候参数名不进行转换，在计算的时候在进行转换。
     * 等于1的时候表示转换成显示或者是校验的语句，这个时候参数名要用进行转换，转换的时候使用一些固定的参数， 比如日期使用当前日期，薪酬体系id为1等
     * @param scheme_code:当前表名或标识
     * @return
     */
    private String transfer_to_SQL(String formula_meaning, String scheme_code, FormulaDetail formulaDetail) {
        if (formulaDetail == null) {
            return "";
        }
        String entity_name = "";
        if ("Pay".equals(scheme_code)) {
            if ("人员工资".equals(cur_scheme.getScheme_type())) {
                entity_name = "PAYSYSTEM_" + sys_no;
            } else {
                entity_name = "PAYDEPTSYSTEM_" + sys_no;
            }
        } else if ("PayDept".equals(scheme_code)) {
            entity_name = "CALSYSTEM_" + sys_no;
        } else if ("DayResult".equals(scheme_code)) {
            entity_name = "K_day_temp";
        } else if ("BDayResult".equals(scheme_code)) {
            entity_name = "K_bday_temp";
        } else if ("MonthResult".equals(scheme_code)) {
            entity_name = "K_month_temp";
        } else if ("LeaveStandard".equals(scheme_code)) {
            entity_name = "K_leave_standard";
        } else if ("In_bill".equals(scheme_code)) {
            entity_name = "In_bill";
        } else if (scheme_code.startsWith("In")) {
            entity_name = scheme_code;
        } else if ("PersonDayRecord".equals(scheme_code)) {
            entity_name = "PersonDayRecord_temp";
        } else if ("PersonMonthRecord".equals(scheme_code)) {
            entity_name = "PersonMonthRecord_temp";
        } else if ("K_leave_standard".equals(scheme_code)) {
            entity_name = "K_leave_standard_temp";
        } else if ("Dd_DayDetail".equals(scheme_code)) {
            entity_name = "Dd_detail_temp";
        } else if ("Twpy_main".equals(scheme_code)) {
            entity_name = "Twpy_a01_temp";
        } else if ("Twfd_butget".equals(scheme_code)) {
            entity_name = "Twfd_butget_temp";
        } else if ("Twfd_sum".equals(scheme_code)) {
            entity_name = "Twfd_sum_temp";
        } else if ("Twfd_detail".equals(scheme_code)) {
            entity_name = "Twfd_detail_temp";
        } else if ("Twfd_osum".equals(scheme_code)) {
            entity_name = "Twfd_osum_temp";
        } else if ("Twfd_odetail".equals(scheme_code)) {
            entity_name = "Twfd_odetail_temp";
        } else if ("Sales_record".equals(formulaDetail.getEntity_name())) {
            entity_name = "Sales_record_temp";
        } else if ("Sales_detail".equals(formulaDetail.getEntity_name())) {
            entity_name = "Sales_detail_temp";
        } else if ("Sales_record_ym".equals(formulaDetail.getEntity_name())) {
            entity_name = "Sales_record_ym_temp";
        } else if ("Gxjx_main".equals(scheme_code)) {
            entity_name = "Gxjx_a01_temp";
        } else {
            entity_name = scheme_code;
        }
        String tmp = "update " + entity_name + " set \n" + formulaDetail.getDetail_name() + " = " + formula_meaning;
        Hashtable<String, String> k_keywords = jtaFormulaText.getK_keywords();
        if ("DayResult".equals(scheme_code)) {
            tmp = tmp.replace("K_day.", "K_day_temp.");
        } else if ("BDayResult".equals(scheme_code)) {
            tmp = tmp.replace("K_bday.", "K_bday_temp.");
        } else if ("MonthResult".equals(scheme_code)) {
            tmp = tmp.replace("K_month.", "K_month_temp.");
        } else if ("PersonDayRecord".equals(scheme_code)) {
            tmp = tmp.replace("PersonDayRecord.", "PersonDayRecord_temp.");
        } else if ("PersonMonthRecord".equals(scheme_code)) {
            tmp = tmp.replace("PersonMonthRecord.", "PersonMonthRecord_temp.");
        } else if ("K_leave_standard".equals(scheme_code)) {
            tmp = tmp.replace("K_leave_standard.", "K_leave_standard_temp.");
        } else if ("Dd_DayDetail".equals(scheme_code)) {
            tmp = tmp.replace("Dd_detail.", "Dd_detail_temp.");
        } else if ("Twfd_butget".equals(scheme_code)) {
            tmp = tmp.replace("Twfd_butget.", "Twfd_butget_temp.");
        } else if ("Twfd_sum".equals(scheme_code)) {
            tmp = tmp.replace("Twfd_sum.", "Twfd_sum_temp.");
        } else if ("Twfd_detail".equals(scheme_code)) {
            tmp = tmp.replace("Twfd_detail.", "Twfd_detail_temp.");
        } else if ("Twfd_osum".equals(scheme_code)) {
            tmp = tmp.replace("Twfd_osum.", "Twfd_osum_temp.");
        } else if ("Twfd_odetail".equals(scheme_code)) {
            tmp = tmp.replace("Twfd_odetail.", "Twfd_odetail_temp.");
        } else if ("Twpy_main".equals(scheme_code)) {
            tmp = tmp.replace("Twpy_a01.", "Twpy_a01_temp.");
        } else if ("Gxjx_main".equals(scheme_code)) {
            tmp = tmp.replace("Gxjx_a01.", "");
        }
        for (String key : k_keywords.keySet()) {
            tmp = tmp.replace(key, k_keywords.get(key));
        }
        if ("Twpy_main".equals(scheme_code)) {
            tmp = tmp.replace("Twpy_a01.", "");
        }
        if ("Gxjx_main".equals(scheme_code)) {
            tmp = tmp.replace("Gxjx_a01.", "");
        }
        return tmp;
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
        toolbar = new javax.swing.JToolBar();
        jPanel2 = new javax.swing.JPanel();
        jSplitPane3 = new javax.swing.JSplitPane();
        jPanel4 = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        btnAddDetail = new javax.swing.JButton();
        btnDelDetail = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JSeparator();
        jPanel3 = new javax.swing.JPanel();
        jSplitPane2 = new javax.swing.JSplitPane();
        jPanel6 = new javax.swing.JPanel();
        pnlFormulas = new javax.swing.JPanel();
        btnUpScheme = new javax.swing.JButton();
        btnDownScheme = new javax.swing.JButton();
        pnl = new javax.swing.JPanel();
        pnlDetails = new javax.swing.JPanel();
        btnUp = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jSplitPane1 = new javax.swing.JSplitPane();
        pnlFormula = new javax.swing.JPanel();
        jPanel8 = new javax.swing.JPanel();
        btnClear = new javax.swing.JButton();
        btnCheck = new javax.swing.JButton();
        jPanel9 = new javax.swing.JPanel();
        pnlForEditor = new javax.swing.JPanel();
        pnlPara = new javax.swing.JPanel();
        btnShowSQL = new javax.swing.JButton();
        btnFunction = new javax.swing.JButton();

        toolbar.setFloatable(false);
        toolbar.setRollover(true);

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(toolbar, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(toolbar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jSplitPane3.setDividerLocation(180);
        jSplitPane3.setDividerSize(4);

        btnAddDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/add_formula.png"))); // NOI18N

        btnDelDetail.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/del_formula.png"))); // NOI18N

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(btnAddDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDelDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(49, Short.MAX_VALUE))
            .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel7Layout.createSequentialGroup()
                .addComponent(jSeparator1, javax.swing.GroupLayout.DEFAULT_SIZE, 2, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnAddDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDelDetail, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSplitPane2.setDividerLocation(150);
        jSplitPane2.setDividerSize(3);
        jSplitPane2.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlFormulas.setLayout(new java.awt.BorderLayout());

        btnUpScheme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/move_up.png"))); // NOI18N

        btnDownScheme.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/move_down.png"))); // NOI18N

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnUpScheme, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDownScheme, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(105, Short.MAX_VALUE))
            .addComponent(pnlFormulas, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel6Layout.createSequentialGroup()
                .addComponent(pnlFormulas, javax.swing.GroupLayout.DEFAULT_SIZE, 121, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnUpScheme, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDownScheme, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSplitPane2.setTopComponent(jPanel6);

        pnlDetails.setLayout(new java.awt.BorderLayout());

        btnUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/move_up.png"))); // NOI18N

        btnDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/move_down.png"))); // NOI18N

        javax.swing.GroupLayout pnlLayout = new javax.swing.GroupLayout(pnl);
        pnl.setLayout(pnlLayout);
        pnlLayout.setHorizontalGroup(
            pnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(btnUp, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDown, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(105, Short.MAX_VALUE))
            .addComponent(pnlDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 177, Short.MAX_VALUE)
        );
        pnlLayout.setVerticalGroup(
            pnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, pnlLayout.createSequentialGroup()
                .addComponent(pnlDetails, javax.swing.GroupLayout.DEFAULT_SIZE, 338, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(pnlLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(btnUp, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDown, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        jSplitPane2.setRightComponent(pnl);

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 179, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane2, javax.swing.GroupLayout.DEFAULT_SIZE, 520, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        jSplitPane3.setLeftComponent(jPanel4);

        jSplitPane1.setDividerLocation(250);
        jSplitPane1.setDividerSize(3);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        pnlFormula.setMinimumSize(new java.awt.Dimension(20, 20));
        pnlFormula.setPreferredSize(new java.awt.Dimension(509, 300));
        pnlFormula.setLayout(new java.awt.BorderLayout());
        jSplitPane1.setLeftComponent(pnlFormula);

        jPanel8.setPreferredSize(new java.awt.Dimension(577, 230));

        btnClear.setText("清空");

        btnCheck.setText("确认校验");

        jPanel9.setPreferredSize(new java.awt.Dimension(577, 190));

        pnlForEditor.setLayout(new java.awt.BorderLayout());

        pnlPara.setBorder(javax.swing.BorderFactory.createTitledBorder("字段属性"));
        pnlPara.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addComponent(pnlForEditor, javax.swing.GroupLayout.PREFERRED_SIZE, 160, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlPara, javax.swing.GroupLayout.DEFAULT_SIZE, 402, Short.MAX_VALUE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(pnlForEditor, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
            .addComponent(pnlPara, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE)
        );

        btnShowSQL.setText("SQL");

        btnFunction.setText("函数");

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGap(31, 31, 31)
                .addComponent(btnFunction)
                .addGap(30, 30, 30)
                .addComponent(btnClear)
                .addGap(29, 29, 29)
                .addComponent(btnCheck)
                .addGap(31, 31, 31)
                .addComponent(btnShowSQL)
                .addContainerGap(201, Short.MAX_VALUE))
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 568, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel8Layout.createSequentialGroup()
                .addGroup(jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnCheck)
                    .addComponent(btnClear)
                    .addComponent(btnFunction)
                    .addComponent(btnShowSQL))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, 273, Short.MAX_VALUE))
        );

        jSplitPane1.setRightComponent(jPanel8);

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 570, Short.MAX_VALUE)
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 556, Short.MAX_VALUE)
        );

        jSplitPane3.setRightComponent(jPanel5);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 755, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 558, Short.MAX_VALUE)
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, 31, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAddDetail;
    private javax.swing.JButton btnCheck;
    private javax.swing.JButton btnClear;
    private javax.swing.JButton btnDelDetail;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnDownScheme;
    private javax.swing.JButton btnFunction;
    private javax.swing.JButton btnShowSQL;
    private javax.swing.JButton btnUp;
    private javax.swing.JButton btnUpScheme;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSplitPane jSplitPane1;
    private javax.swing.JSplitPane jSplitPane2;
    private javax.swing.JSplitPane jSplitPane3;
    private javax.swing.JPanel pnl;
    private javax.swing.JPanel pnlDetails;
    private javax.swing.JPanel pnlForEditor;
    private javax.swing.JPanel pnlFormula;
    private javax.swing.JPanel pnlFormulas;
    private javax.swing.JPanel pnlPara;
    private javax.swing.JToolBar toolbar;
    // End of variables declaration//GEN-END:variables

    private void initOthers() {
        if ("Pay".equals(scheme_code)) {
            sys_no = Integer.valueOf(CommUtil.fetchEntityBy("select ps.system_no from PaySystem ps where ps.paySystem_key='" + scheme_id + "'").toString());
        } else if ("PayDept".equals(scheme_code)) {
            sys_no = Integer.valueOf(CommUtil.fetchEntityBy("select ps.system_no from CalSystem ps where ps.calSystem_key='" + scheme_id + "'").toString());
        }
        jtaFormulaText = new HrTextPane();
        pnlFormula.add(jtaFormulaText, BorderLayout.CENTER);
        buildToolBar();
        List fss = CommUtil.fetchEntities("from FormulaScheme fs  where fs.scheme_code='" + scheme_code + "' "
                + "and fs.scheme_id='" + scheme_id + "' order by fs.order_no");
        if ("Pay".equals(scheme_code)) {
            
        } else {
            formulaSchemes.addAll(fss);
        }
        formula_binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, formulaSchemes, formula_table);
        BeanProperty formula_use_prop = BeanProperty.create("used");
        formula_binding.addColumnBinding(formula_use_prop).setColumnName("启用标识").setColumnClass(Boolean.class).setEditable(true);
        Property formula_propertySelected = ELProperty.create("${order_no}: ${scheme_name} ");
        formula_binding.addColumnBinding(formula_propertySelected).setColumnName("方案名称").setColumnClass(String.class).setEditable(false);
        formula_binding.addBindingListener(formula_bindingListener);
        formula_binding.bind();
        pnlFormulas.add(new JScrollPane(formula_table), BorderLayout.CENTER);
        detail_binding = SwingBindings.createJTableBinding(UpdateStrategy.READ_WRITE, formulaDetails, detail_table);
        BeanProperty use_prop = BeanProperty.create("use_flag");
        detail_binding.addColumnBinding(use_prop).setColumnName("启用").setColumnClass(Boolean.class).setEditable(true);
        Property propertySelected = ELProperty.create("${order_no}: ${detail_caption} ");
        detail_binding.addColumnBinding(propertySelected).setColumnName("公式名称").setEditable(false);
        detail_binding.addBindingListener(detail_bindingListener);
        detail_binding.bind();
        scrollPane = new JScrollPane(detail_table);
        pnlDetails.add(scrollPane, BorderLayout.CENTER);
        pnlForEditor.add(pnlEditor, BorderLayout.CENTER);
        para_model = new FormulaParaModel(pay_infos, entity_list, para_list);
        para_tree = new JTree(para_model);
        HRRendererView.getParaFieldTypeMap(para_tree).initTree(para_tree);
        SearchTreeFieldDialog.doQuickSearch("字段属性", para_tree);
        pnlPara.add(new JScrollPane(para_tree), BorderLayout.CENTER);
        jtaFormulaText.revokeDocumentKeys(para_model.getLookups(), para_model.getKeyword_groups(), para_model.getK_keywords());

    }
    //验证SQL的合法性

    private boolean validateSQL(String sql_text) {
        ValidateSQLResult validateSQLResult = null;
        if (scheme_code.startsWith("In") || "In_bill".equals(scheme_code)) {
            validateSQLResult = CommUtil.validateSQL(appendSql(sql_text), true);
        } else {
            validateSQLResult = CommUtil.validateSQL(sql_text, true);
        }
        String sql_msg = sql_text;
        boolean result = validateSQLResult.getResult() == 0;
        if (!result) {
            sql_msg += ";\n错误提示：\n    " + validateSQLResult.getMsg();
        }
        MsgUtil.showHRValidateMsg(sql_msg, "", result);
        return result;
    }

    private String appendSql(String sql_text) {
        if (sql_text == null || sql_text.isEmpty()) {
            return sql_text;
        }
        String t_sql = "";
        for (String sql : sql_text.split(";")) {
            String tmp = sql;
            if (sql.contains(")")) {
                sql = sql.substring(sql.lastIndexOf(")"));
            }
            if (sql.contains("where")) {
                tmp += " and 1=0 ";
            } else {
                tmp += " where 1=0 ";
            }
            t_sql += tmp + ";";
        }
        return t_sql;
    }

    private void setupEvents() {
        btnOutFormula.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                exportFormula();
            }
        });
        btnFunction.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                Enumeration enumt = ((DefaultMutableTreeNode) para_model.getRoot()).breadthFirstEnumeration();
                List<TempFieldInfo> function_fields = new ArrayList<TempFieldInfo>();
                while (enumt.hasMoreElements()) {
                    DefaultMutableTreeNode node = (DefaultMutableTreeNode) enumt.nextElement();
                    if (node.getUserObject() instanceof TempFieldInfo) {
                        if (((DefaultMutableTreeNode) node.getParent()).getUserObject().toString().endsWith("所有项目")) {
                            function_fields.add((TempFieldInfo) node.getUserObject());
                        }
                    }
                }
                FormulaFunctionWizardModel ffmModel = new FormulaFunctionWizardModel(function_fields);
                if (WizardDialog.showWizard(ffmModel)) {
                    CreateNewFunctionParam para = ffmModel.getCreateNewFunctionParam();
                    if (para.getFunctionModel().getFuntion_code().equals("Tax")) {
                        String tax_str = "case \n    when (@安互金 - @安全奖) between 0.01 and 500 then (@安互金 - @安全奖) * 0.05 \n";
                        tax_str += "    when (@安互金 - @安全奖) between 500.01 and 2000 then (@安互金 - @安全奖) * 0.1 - 25 \n";
                        tax_str += "    when (@安互金 - @安全奖) between 2000.01 and 5000 then (@安互金 - @安全奖) * 0.15 - 125 \n";
                        tax_str += "    when (@安互金 - @安全奖) between 5000.01 and 20000 then (@安互金 - @安全奖) * 0.20 - 375 \n";
                        tax_str += "    when (@安互金 - @安全奖) between 20000.01 and 40000 then (@安互金 - @安全奖) * 0.25 - 1375 \n";
                        tax_str += "    when (@安互金 - @安全奖) between 40000.01 and 60000 then (@安互金 - @安全奖) * 0.30 - 3375 \n";
                        tax_str += "    when (@安互金 - @安全奖) between 60000.01 and 80000 then (@安互金 - @安全奖) * 0.35 - 6375 \n";
                        tax_str += "    when (@安互金 - @安全奖) between 80000.01 and 100000 then (@安互金 - @安全奖) * 0.40 - 10375 \n";
                        tax_str += "    when (@安互金 - @安全奖) >= 100000 then (@安互金 - @安全奖) * 0.45 - 15375 \n";
                        tax_str += "    else 0 \n end ";
                        if (para.getFun_para().toString().equals("不含税级距")) {
                            tax_str = " case \n    when (@安互金 - @安全奖) between 0.01 and 475 then (@安互金 - @安全奖) * 0.05 \n";
                            tax_str += "    when (@安互金 - @安全奖) between 475.01 and 1825 then (@安互金 - @安全奖) * 0.1 - 25 \n";
                            tax_str += "    when (@安互金 - @安全奖) between 1825.01 and 4375 then (@安互金 - @安全奖) * 0.15 - 125 \n";
                            tax_str += "    when (@安互金 - @安全奖) between 4375.01 and 16375 then (@安互金 - @安全奖) * 0.20 - 375 \n";
                            tax_str += "    when (@安互金 - @安全奖) between 16375.01 and 31375 then (@安互金 - @安全奖) * 0.25 - 1375 \n";
                            tax_str += "    when (@安互金 - @安全奖) between 31375.01 and 45375 then (@安互金 - @安全奖) * 0.30 - 3375 \n";
                            tax_str += "    when (@安互金 - @安全奖) between 45375.01 and 58375 then (@安互金 - @安全奖) * 0.35 - 6375 \n";
                            tax_str += "    when (@安互金 - @安全奖) between 58375.01 and 70375 then (@安互金 - @安全奖) * 0.40 - 10375 \n";
                            tax_str += "    when (@安互金 - @安全奖) >= 70375 then (@安互金 - @安全奖) * 0.45 - 15375 \n";
                            tax_str += "    else 0 \n end ";
                        }
                        Object obj = para.getFun_infos().get(0);
                        if (obj instanceof TempFieldInfo) {
                            tax_str = tax_str.replace("@安互金", "[" + obj.toString() + "]");
                        } else {
                            tax_str = tax_str.replace("@安互金", obj.toString());
                        }
                        obj = para.getFun_infos().get(1);
                        if (obj instanceof TempFieldInfo) {
                            tax_str = tax_str.replace("@安全奖", "[" + obj.toString() + "]");
                        } else {
                            tax_str = tax_str.replace("@安全奖", obj.toString());
                        }
                        int tmp = jtaFormulaText.getSelectionStart();
                        jtaFormulaText.replaceSelection(tax_str);
                        jtaFormulaText.setCaretPosition(tmp + tax_str.length());
                        jtaFormulaText.requestFocus();
                    }
                }
            }
        });
        formula_listener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                pickClose();
                if (formula_table.getSelectedRow() < 0) {
                    return;
                }
                if (cur_scheme != null) {
                    String cur_key = cur_scheme.getFormulaScheme_key();
                    String new_key = ((FormulaScheme) formulaSchemes.get(formula_table.getSelectedRow())).getFormulaScheme_key();
                    if (cur_key.equals(new_key)) {
                        return;
                    }
                }
                if (cur_scheme == formulaSchemes.get(formula_table.getSelectedRow())) {
                    return;
                }
                cur_scheme = (FormulaScheme) formulaSchemes.get(formula_table.getSelectedRow());
                cur_scheme = (FormulaScheme) CommUtil.fetchEntityBy("from FormulaScheme fs left join fetch fs.formulaDetails fds where fs.scheme_code='" + scheme_code + "' "
                        + "and fs.formulaScheme_key='" + cur_scheme.getFormulaScheme_key() + "' order by fs.order_no");
                para_model.buildPayNode(cur_scheme.getScheme_type());
                jtaFormulaText.revokeDocumentKeys(para_model.getLookups(), para_model.getKeyword_groups(), para_model.getK_keywords());
                para_tree.updateUI();
                formulaDetails.clear();
                if (cur_scheme.getFormulaDetails() != null) {
                    formulaDetails.addAll(cur_scheme.getFormulaDetails());
                }
                reBindDetail(0);
            }
        };
        formula_table.getSelectionModel().addListSelectionListener(formula_listener);
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_scheme == null) {
                    return;
                }
                if (JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(btnDel),
                        "确定要删除该方案吗", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
                    return;
                }
                log.info(e);
                String key = "." + scheme_id + "_" + cur_scheme.getFormulaScheme_key();
                ValidateSQLResult vs = CommUtil.deleteEntity(cur_scheme);
                if (vs.getResult() == 0) {
                    String sql = "delete from rolefuntion where funtionright_key in(select funtionright_key from funtionright where fun_module_flag='" + key + "');";
                    sql += "delete from funtionright where fun_module_flag='" + key + "'";
                    CommUtil.excuteSQLs(sql, ";");
                    log.info("提示:删除方案成功，方案名称:" + cur_scheme.getScheme_name());
                } else {
                    MsgUtil.showHRDelErrorMsg(vs);
                    return;
                }
                if (formulaSchemes.size() == 1) {
                    formulaSchemes.clear();
                    formula_binding.unbind();
                    formula_binding.bind();
                    formulaDetails.clear();
                    detail_binding.unbind();
                    detail_binding.bind();
                } else {
                    int i = formula_table.getSelectedRow();
                    if (i > 0) {
                        i--;
                    }
                    formulaSchemes.remove(cur_scheme);
                    if (formulaSchemes.isEmpty()) {
                        i = -1;
                        formulaDetails.clear();
                        detail_binding.unbind();
                        detail_binding.bind();
                    }
                    reBindScheme(i);
                }
            }
        });
        btnSaveAs.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_scheme == null) {
                    return;
                }
                Object scheme_name = JOptionPane.showInputDialog(JOptionPane.getFrameForComponent(btnEditName), "", "请输入方案名：", JOptionPane.INFORMATION_MESSAGE, null, null, "");
                if (scheme_name == null) {
                    return;
                }
                if (scheme_name.toString().trim().equals("")) {
                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnEditName), "方案名不可为空");
                    return;
                }
                String scheme_uid = new UID().toString();
                String ex_sql = "";
                String db_type = UserContext.sql_dialect;
                ex_sql = "insert into FormulaScheme (formulascheme_key,scheme_name,scheme_user,used,scheme_code,order_no,scheme_id,scheme_type)values(";
                ex_sql += "'" + scheme_uid + "','" + scheme_name.toString() + "','" + UserContext.person_code + "',1,'" + scheme_code + "'," + formulaSchemes.size() + ",'" + scheme_id + "','" + cur_scheme.getScheme_type() + "');";
                ex_sql += "insert into FormulaDetail(FormulaDetail_key,detail_caption,detail_name,entity_caption,entity_name,order_no,use_flag,formulascheme_key,formula,formula_meaning) select " + DbUtil.getUIDForDb(db_type) + ",detail_caption,detail_name,entity_caption,entity_name,order_no,use_flag,'" + scheme_uid + "',formula,formula_meaning from formulaDetail where formulascheme_key='" + cur_scheme.getFormulaScheme_key() + "'";
                ValidateSQLResult result = CommUtil.excuteSQLs(ex_sql, ";");
                if (result.getResult() == 0) {
                    FormulaScheme fs = (FormulaScheme) CommUtil.fetchEntityBy("from FormulaScheme fs left join fetch fs.formulaDetails where fs.formulaScheme_key='" + scheme_uid + "'");
                    addSchemeFuntion(fs);
                    log.info("点击" + btnSaveAs.getText() + "#提示:操作成功");
                    formulaSchemes.add(fs);
                    reBindScheme(formulaSchemes.size() - 1);
                } else {
                    log.info("点击" + btnSaveAs.getText() + "#提示:另存方案失败");
                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnSaveAs), "另存方案失败", "错误", JOptionPane.ERROR_MESSAGE);
                }

            }
        });
        jSplitPane1.addComponentListener(new ComponentAdapter() {

            @Override
            public void componentResized(ComponentEvent e) {
                int height = jSplitPane1.getHeight();
                jSplitPane1.setDividerLocation(height - 270);
            }
        });
        doc_listener = new DocumentListener() {

            @Override
            public void insertUpdate(DocumentEvent e) {
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
            }

            @Override
            public void changedUpdate(DocumentEvent e) {
                detail_change_flag = true;
                change_flag = true;
            }
        };
        jtaFormulaText.getDocument().addDocumentListener(doc_listener);
        detail_listener = new ListSelectionListener() {

            @Override
            public void valueChanged(ListSelectionEvent e) {
                saveCurDetail(false, cur_detail);
                if (detail_table.getSelectedRow() < 0) {
                    cur_detail = null;
                    jtaFormulaText.setText("");
                    return;
                }
                if (cur_detail == formulaDetails.get(detail_table.getSelectedRow())) {
                    return;
                }
                cur_detail = (FormulaDetail) formulaDetails.get(detail_table.getSelectedRow());
                jtaFormulaText.getDocument().removeDocumentListener(doc_listener);
                jtaFormulaText.setText(cur_detail.getFormula_meaning());
                jtaFormulaText.getDocument().addDocumentListener(doc_listener);
            }
        };
        detail_table.getSelectionModel().addListSelectionListener(detail_listener);
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                pickClose();
                FormulaScheme fs = (FormulaScheme) UtilTool.createUIDEntity(FormulaScheme.class);
                fs.setScheme_code(scheme_code);
                fs.setOrder_no(formulaSchemes.size() + 1);
                fs.setScheme_id(scheme_id);
                fs.setUse_flag(true);
                fs.setUsed(true);
                List<String> fields = new ArrayList<String>();
                fields.add("scheme_name");
                if ("Pay".equals(scheme_code)) {
                    fields.add("scheme_type");
                    fs.setScheme_type("人员工资");
                } else if ("PayDept".equals(scheme_code)) {
                    fs.setScheme_type("部门工资");
                } else if ("In_bill".equals(scheme_code)) {
                    fs.setScheme_type("保险账户");
                } else {
                    fs.setScheme_type(scheme_code);
                }
                if (BeanPanel.edit(fs, fields, "新增方案", null)) {
                    ValidateSQLResult result = CommUtil.saveEntity(fs);
                    if (result.getResult() == 0) {
                        addSchemeFuntion(fs);
                        log.info("新增方案，名称:" + fs.getScheme_name());
                        fs.setNew_flag(0);
                        formulaSchemes.add(fs);
                        reBindScheme(formulaSchemes.size() - 1);
                    } else {
                        MsgUtil.showHRSaveErrorMsg(result);
                    }
                }
            }
        });
        btnAddDetail.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                final int row = detail_table.getSelectedRow();
                FormulaFieldSelectDialog pfsDialog;
                if ("人员工资".equals(cur_scheme.getScheme_type())) {
                    pfsDialog = new FormulaFieldSelectDialog("所有项目", list_item);
                } else {
                    pfsDialog = new FormulaFieldSelectDialog("所有项目", list_item1);
                }
                pfsDialog.addPickFormulaDetailListener(new IPickFormulaDetailListener() {

                    @Override
                    public void pickFormulaDetail(FormulaDetail fd) {
                        change_flag = true;
                        fd.setFormulaScheme(cur_scheme);
                        fd.setOrder_no(row + 2);
                        fd.setUse_flag(false);
                        cur_scheme.getFormulaDetails().add(fd);
                        for (int i = row + 1; i < formulaDetails.size(); i++) {
                            FormulaDetail fd1 = (FormulaDetail) formulaDetails.get(i);
                            fd1.setOrder_no(i + 2);
                            save_details.add(fd1);
                        }
                        if (row + 1 < formulaDetails.size()) {
                            formulaDetails.add(row + 1, fd);
                        } else {
                            formulaDetails.add(fd);
                        }
                        reBindDetail(row + 1);
                    }
                });
                ContextManager.locateOnMainScreenCenter(pfsDialog);
                pfsDialog.setVisible(true);
            }
        });
        btnEditName.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_scheme == null) {
                    return;
                }
                Object scheme_name = JOptionPane.showInputDialog(JOptionPane.getFrameForComponent(btnEditName), "", "请输入方案名：", JOptionPane.INFORMATION_MESSAGE, null, null, cur_scheme.getScheme_name());
                if (scheme_name == null) {
                    return;
                }
                if (scheme_name.toString().trim().equals("")) {
                    JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnEditName), "方案名不可为空");
                    return;
                }
                cur_scheme.setScheme_name(scheme_name.toString());
                if (cur_scheme.getNew_flag() == 0) {
                    ValidateSQLResult result = CommUtil.updateEntity(cur_scheme);
                    if (result.getResult() == 0) {
                        addSchemeFuntion(cur_scheme);
                        JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnEditName), "修改成功");
                    } else {
                        MsgUtil.showHRSaveErrorMsg(result);
                    }
                }
                int ind = formula_table.getSelectedRow();
                formulaSchemes.set(ind, cur_scheme);
                cur_scheme = null;
                reBindScheme(ind);
            }
        });
        btnUpScheme.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pickClose();
                moveSchemePosition(-1);
            }
        });
        btnDownScheme.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pickClose();
                moveSchemePosition(1);
            }
        });
        btnUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                movePosition(-1);
            }
        });
        btnDown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                movePosition(1);
            }
        });
        btnCheck.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_detail == null) {
                    return;
                }
                int ind = detail_table.getSelectedRow();
                cur_detail.setFormula_meaning(jtaFormulaText.getText());
                String sql_text = transfer_to_SQL(cur_detail.getFormula_meaning(), scheme_code, cur_detail);
                cur_detail.setFormula(sql_text);
                cur_detail.setFormulaScheme(cur_scheme);
                sql_text = transfer_para_to_SQL(sql_text, scheme_code);
                cur_detail.setUse_flag(validateSQL(sql_text));
                if (cur_detail.isUse_flag()) {
                    detail_change_flag = false;
                }
                formulaDetails.set(ind, cur_detail);
                save_details.add(cur_detail);
                reBindDetail(ind);
            }
        });
        btnClear.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                jtaFormulaText.setText("");
            }
        });
        btnDelDetail.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (detail_table.getSelectedRow() < 0) {
                    return;
                }
                if (JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(btnDelDetail),
                        "确定要删除该公式吗", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) != JOptionPane.OK_OPTION) {
                    return;
                }
                change_flag = true;
                int ind = detail_table.getSelectedRow();
                jtaFormulaText.setText("");
                int[] select_indexs = detail_table.getSelectedRows();
                List<String> del_fds = new ArrayList();
                List<FormulaDetail> remove_fds = new ArrayList();
                for (int i = 0; i < select_indexs.length; i++) {
                    del_fds.add(((FormulaDetail) formulaDetails.get(select_indexs[i])).getFormulaDetail_key());
                    remove_fds.add((FormulaDetail) formulaDetails.get(select_indexs[i]));
                }
                ValidateSQLResult result = CommImpl.delFormulaDetail(cur_scheme.getFormulaScheme_key(), del_fds);
                if (result.getResult() == 0) {
                    save_details.removeAll(remove_fds);
                    formulaDetails.removeAll(remove_fds);
                    cur_scheme.getFormulaDetails().removeAll(remove_fds);
                    int len = formulaDetails.size();
                    for (int i = 1; i <= len; i++) {
                        FormulaDetail fd = (FormulaDetail) formulaDetails.get(i - 1);
                        if (fd.getOrder_no() != i) {
                            save_details.add(fd);
                            fd.setOrder_no(i);
                        }
                    }
                    reBindDetail(Math.min(ind, formulaDetails.size() - 1));
                }
            }
        });
        pnlEditor.addPickFormulaEditorListener(new IPickFormulaEditorListener() {

            @Override
            public void pickEditor(String operator) {
                //operator:公式编辑器Panel返回的运算符,如：where
                int tmp = jtaFormulaText.getSelectionStart();
                jtaFormulaText.replaceSelection(operator.toLowerCase());
                jtaFormulaText.setCaretPosition(tmp + operator.length());
                jtaFormulaText.requestFocus();
            }
        });
        para_tree.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() < 2) {
                    return;
                }

                if (para_tree.getSelectionPath() == null) {
                    return;
                }

                if (para_tree.getSelectionPath().getLastPathComponent() == para_tree.getModel().getRoot()) {
                    return;
                }
                DefaultMutableTreeNode node = (DefaultMutableTreeNode) para_tree.getSelectionPath().getLastPathComponent();
                Object obj = node.getUserObject();
                if (obj == null) {
                    return;
                }
                DefaultMutableTreeNode parent = (DefaultMutableTreeNode) node.getParent();
                final int tmp = jtaFormulaText.getSelectionStart();
                boolean isPara = obj.toString().startsWith("@");
                if ("常量参数".equals(obj.toString())) {
                    return;
                }
                String operator = "";
                if ((obj instanceof String || obj instanceof EntityDef) && !isPara) {
                    if (node.getLevel() == 0) {
                        return;
                    }
                    List<TempFieldInfo> fields = new ArrayList<TempFieldInfo>();
                    Enumeration enumt = node.children();
                    while (enumt.hasMoreElements()) {
                        DefaultMutableTreeNode child = (DefaultMutableTreeNode) enumt.nextElement();
                        fields.add((TempFieldInfo) child.getUserObject());
                    }

                    FormulaParaFieldSelectPanel pnlFpfs = new FormulaParaFieldSelectPanel(obj.toString(), fields);
                    pnlFpfs.addPickFieldSelectListener(new IPickFieldSelectListener() {

                        @Override
                        public void pickFieldString(String text) {
                            jtaFormulaText.replaceSelection(text);
                            jtaFormulaText.setCaretPosition(tmp + text.length());
                            jtaFormulaText.requestFocus();
                        }
                    });
                    ModelFrame.showModel((JFrame) JOptionPane.getFrameForComponent(jtaFormulaText), pnlFpfs, true, "请选择字段:", 750, 650);
                    return;
                }
                String entity_name = "[" + parent.getUserObject().toString() + ".";
                if ("[人员工资所有项目.".equals(entity_name) || "[部门工资所有项目.".equals(entity_name) || "[常量参数.".equals(entity_name)) {
                    entity_name = "[";
                }
                if (entity_name.endsWith("所有项目.")) {
                    entity_name = "[";
                }
                if (obj instanceof TempFieldInfo && ((TempFieldInfo) obj).getField_name().endsWith("_code_")) {
                    TempFieldInfo tfi = (TempFieldInfo) obj;
                    ObjectListHint objHint = tfi.getField().getAnnotation(ObjectListHint.class);
                    if (objHint != null && objHint.hqlForObjectList().startsWith("from Code ")) {
                        String hql = objHint.hqlForObjectList();
                        String code_type = hql.substring(hql.indexOf("=") + 1);
                        CodeSelectDialog csmDlg = new CodeSelectDialog(CodeManager.getCodeManager().getCodeListBy(code_type), code_type, null, TreeSelectMod.nodeCheckMod);
                        ContextManager.locateOnMainScreenCenter(csmDlg);
                        csmDlg.setVisible(true);
                        if (csmDlg.isClick_ok()) {
                            List<Code> codes = csmDlg.getSelectCodes(false);
                            if (codes.size() == 0) {
                                return;
                            }
                            String str = "";
                            List<String> like_str = new ArrayList<String>();
                            for (Code c : codes) {
                                if (c.isEnd_flag()) {
                                    str += "'[" + code_type + "." + c.getCode_name() + "]',";
                                } else {
                                    like_str.add("[" + code_type + "." + c.getCode_name() + "]");
                                }
                            }
                            if (!str.equals("")) {
                                str = str.substring(0, str.length() - 1);
                            }
                            for (String s : like_str) {
                                operator += " " + entity_name + tfi.getCaption_name() + "] like '" + s + "%' or ";
                            }
                            if (!str.equals("")) {
                                operator += " " + entity_name + tfi.getCaption_name() + "] in(" + str + ") or ";
                            }
                            operator = operator.substring(0, operator.length() - 3);
                        }
                    } else {
                        operator = " " + entity_name + obj.toString() + "] ";
                    }
                } else {
                    operator = " " + entity_name + obj.toString() + "] ";
                }
                if (isPara) {
                    operator = operator.replace("[", "");
                    operator = operator.replace("]", "");
                }
                jtaFormulaText.replaceSelection(operator);
                jtaFormulaText.setCaretPosition(tmp + operator.length());
                jtaFormulaText.requestFocus();
            }
        });
        btnShowSQL.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (cur_detail == null) {
                    return;
                }
                String sql_text = transfer_to_SQL(jtaFormulaText.getText(), scheme_code, cur_detail);
                sql_text = transfer_para_to_SQL(sql_text, scheme_code);
                String syntax_msg = "未通过验证";
                if (cur_detail.isUse_flag()) {
                    syntax_msg = "已通过验证";
                }
                MsgUtil.showHRValidateMsg(sql_text, "", cur_detail.isUse_flag());
            }
        });
        btnCancel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                pickClose();
                ModelFrame.close();
            }
        });
        btnSaveScheme.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                saveObject();
            }
        });
        ComponentUtil.refreshJSplitPane(jSplitPane1, "FormulaPanel.jSplitPane1");
        ComponentUtil.refreshJSplitPane(jSplitPane2, "FormulaPanel.jSplitPane2");
        ComponentUtil.refreshJSplitPane(jSplitPane3, "FormulaPanel.jSplitPane3");
        reBindScheme(0);
    }

    /**
     * 该方法用于重新绑定scheme，并选中到指定行
     * @param row：行号
     */
    private void reBindScheme(int row) {
        formula_table.getSelectionModel().removeListSelectionListener(formula_listener);
        formula_binding.unbind();
        formula_binding.bind();
        detail_binding.unbind();
        detail_binding.bind();
        if (formulaSchemes.size() > row) {
            formula_table.setRowSelectionInterval(row, row);
        }
        formula_table.getSelectionModel().addListSelectionListener(formula_listener);
        formula_listener.valueChanged(null);
        updateTableWidth();
    }

    /**
     * 该方法用于重新绑定detail，并选中到指定行
     * @param row：行号
     */
    private void reBindDetail(int row) {
        detail_table.getSelectionModel().removeListSelectionListener(detail_listener);
        detail_binding.unbind();
        detail_binding.bind();
        if (!formulaDetails.isEmpty() && formulaDetails.size() > row) {
            detail_table.setRowSelectionInterval(row, row);
        } else if (formulaDetails.isEmpty()) {
            cur_detail = null;
        }
        detail_table.getSelectionModel().addListSelectionListener(detail_listener);
        detail_listener.valueChanged(null);
        updateTableWidth();
        btnAddDetail.setEnabled(formulaSchemes.size() > 0);
        btnDelDetail.setEnabled(formulaSchemes.size() > 0);
    }

    //更新JTABLE界面宽度
    private void updateTableWidth() {
        formula_table.getColumnModel().getColumn(0).setMaxWidth(50);
        formula_table.getColumnModel().getColumn(0).setPreferredWidth(50);
        formula_table.updateUI();
        detail_table.getColumnModel().getColumn(0).setMaxWidth(50);
        detail_table.getColumnModel().getColumn(0).setPreferredWidth(50);
        detail_table.updateUI();
        pnlDetails.updateUI();
    }

    /**
     *此方法用于移动方案更改其排序号
     * @param step：1，表示下移，-1，表示上移
     */
    private void moveSchemePosition(int step) {
        if (formula_table.getSelectedRow() < 0) {
            return;
        }
        int ind = formula_table.getSelectedRow();
        if (ind + step < 0 || ind + step >= formulaSchemes.size()) {
            return;
        }
        int ind2 = ind + step;
        Object obj1 = formulaSchemes.get(ind);
        Object obj2 = formulaSchemes.get(ind2);
        formulaSchemes.set(ind2, obj1);
        formulaSchemes.set(ind, obj2);
        String str = "";
        for (int i = 1; i <= formulaSchemes.size(); i++) {
            FormulaScheme formulaScheme = (FormulaScheme) formulaSchemes.get(i - 1);
            formulaScheme.setOrder_no(i);
            str += "update formulaScheme set order_no=" + i + " where formulaScheme_key='" + formulaScheme.getFormulaScheme_key() + "';";
        }
        CommUtil.excuteSQLs(str, ";");
        reBindScheme(ind2);
    }

    /**
     * 该方法用于处理当前detail的变化，当detail的formula变化或use_flag变化时，重新翻译SQL文，并在use_flag为true时验证
     * @param change_flag：use_flag是否变化
     */
    private void saveCurDetail(boolean change_flag, FormulaDetail cur_detail) {
        if (detail_change_flag || (change_flag && cur_detail.isUse_flag())) {
            if (cur_detail != null) {
                String sql_text = transfer_to_SQL(jtaFormulaText.getText(), scheme_code, cur_detail);
                cur_detail.setFormula_meaning(jtaFormulaText.getText());
                cur_detail.setFormula(sql_text);
                sql_text = transfer_para_to_SQL(sql_text, scheme_code);
                if (cur_detail.isUse_flag() && validateSQL(sql_text)) {
                    cur_detail.setUse_flag(true);
                } else {
                    cur_detail.setUse_flag(false);
                }
                save_details.add(cur_detail);
            }
            detail_change_flag = false;
        }
    }

    /**
     *此方法用于移动公式更改其排序号
     * @param step：1，表示下移，-1，表示上移
     */
    private void movePosition(int step) {
        if (detail_table.getSelectedRow() < 0) {
            return;
        }
        change_flag = true;
        int ind = detail_table.getSelectedRow();
        int ind2 = ind + step;
        if (ind2 == -1) {
            ind2 = formulaDetails.size() - 1;
        }
        if (ind2 == formulaDetails.size()) {
            ind2 = 0;
        }
        Object obj1 = formulaDetails.get(ind);
        Object obj2 = formulaDetails.get(ind2);
        formulaDetails.set(ind2, obj1);
        formulaDetails.set(ind, obj2);
        for (int i = 1; i <= formulaDetails.size(); i++) {
            FormulaDetail formulaDetail = (FormulaDetail) formulaDetails.get(i - 1);
            formulaDetail.setOrder_no(i);
            save_details.add(formulaDetail);
        }
        reBindDetail(ind2);
        scrollPane.getVerticalScrollBar().setValue(ind2 * detail_table.getRowHeight());
        scrollPane.updateUI();
    }
    //导出公式

    private void exportFormula() {
        File file = FileChooserUtil.getXLSExportFile(CommMsg.SELECTXLSFILE_MESSAGE);
        if (file == null) {
            return;
        }
        String file_path;
        WritableWorkbook workbook;
        List<ExportDetail> exportDetails = new ArrayList<ExportDetail>();
        List<TempFieldInfo> formula_infos = EntityBuilder.getCommFieldInfoListOf(FormulaDetail.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : formula_infos) {
            ExportDetail exportDetail = new ExportDetail();
            exportDetail.setField_name(tfi.getField_name());
            exportDetail.setField_caption(tfi.getCaption_name());
            exportDetail.setEntity_name(tfi.getEntity_name());
            exportDetails.add(exportDetail);
        }
        try {
            workbook = Workbook.createWorkbook(file);
            WritableSheet sheet = workbook.createSheet("First Sheet", 0);
            WritableFont wfc = new WritableFont(WritableFont.ARIAL, 20,
                    jxl.write.WritableFont.BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
            wfc.setUnderlineStyle(UnderlineStyle.NO_UNDERLINE);
            wfc.setColour(jxl.format.Colour.BLACK);
            wfc.setItalic(false);
            jxl.write.WritableCellFormat wcfFc = new jxl.write.WritableCellFormat(wfc);
            wcfFc.setAlignment(Alignment.CENTRE);
            wcfFc.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            Label label = new Label(0, 0, "公式导出", wcfFc);
            WritableCellFeatures cellFeatures = new WritableCellFeatures();

            cellFeatures.setComment(scheme_code);
            label.setCellFeatures(cellFeatures);
            sheet.addCell(label);
            sheet.mergeCells(0, 0, Math.max(exportDetails.size() - 1, 0), 0);

            int j = 0;
            wfc = new jxl.write.WritableFont(WritableFont.ARIAL, 12,
                    jxl.write.WritableFont.BOLD, false,
                    UnderlineStyle.NO_UNDERLINE, jxl.format.Colour.BLACK);
            wfc.setUnderlineStyle(UnderlineStyle.NO_UNDERLINE);
            wfc.setColour(jxl.format.Colour.BLACK);
            wfc.setItalic(false);
            wcfFc = new jxl.write.WritableCellFormat(wfc);
            wcfFc.setAlignment(Alignment.CENTRE);
            wcfFc.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
            for (ExportDetail exportDetail : exportDetails) {
                if (exportDetail.getField_name().equals("detail_caption") || exportDetail.getField_name().equals("use_flag")) {
                    sheet.setColumnView(j, 15);
                } else {
                    sheet.setColumnView(j, 50);
                }
                label = new Label(j, 1, exportDetail.getField_caption(), wcfFc);
                cellFeatures = new WritableCellFeatures();

                cellFeatures.setComment(exportDetail.getField_name());
                label.setCellFeatures(cellFeatures);
                sheet.addCell(label);
                j++;
            }

            int i = 2;
            for (Object obj : cur_scheme.getFormulaDetails()) {
                int col = 0;
                for (ExportDetail exportDetail : exportDetails) {
                    Object tmp_obj = EditorFactory.getValueBy(obj, exportDetail.getField_name());
                    WritableCellFormat format = new WritableCellFormat();
                    format.setBorder(Border.ALL, BorderLineStyle.THIN, Colour.BLACK);
                    label = new Label(col, i, tmp_obj == null ? "" : tmp_obj.toString(), format);
                    sheet.addCell(label);
                    col++;
                }
                i++;
            }
            workbook.write();
            workbook.close();
            Runtime.getRuntime().exec("cmd /c \"" + file.getPath() + "\"");
        } catch (WriteException ex) {
            log.error(ex);
        } catch (IOException ex) {
            log.error(ex);
        }
    }

    private void buildToolBar() {
        toolbar.add(btnAdd);
        toolbar.add(btnEditName);
        toolbar.add(btnSaveAs);
        toolbar.add(btnDel);
        toolbar.add(btnSaveScheme);
        toolbar.add(btnOutFormula);
        toolbar.add(btnCancel);
        toolbar.setFloatable(false);
    }

    public void saveObject() {
        saveCurDetail(change_flag, cur_detail);
        if (cur_detail != null) {
            save_details.add(cur_detail);
        }
        change_flag = false;
        if (save_details != null && save_details.size() > 0) {
            ValidateSQLResult result = CommImpl.saveFormulaDetail(save_details);
            if (result.getResult() == 0) {
                for (Object fd : save_details) {
                    ((FormulaDetail) fd).setNew_flag(0);
                }
                save_details.clear();
                JOptionPane.showMessageDialog(JOptionPane.getFrameForComponent(btnCheck), "保存成功！");
                for (IPickRefreshDataListener listener : listeners) {
                    listener.refreshData();
                }
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
        }
    }

    private void addSchemeFuntion(FormulaScheme fs) {
        if (!"Pay".equals(scheme_code)) {
            return;
        }
        
    }

    @Override
    public void pickClose() {
        if (change_flag) {
            if (cur_detail != null) {
                if (JOptionPane.showConfirmDialog(JOptionPane.getFrameForComponent(btnCheck), "方案有改动，是否保存", "询问", JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE) == JOptionPane.OK_OPTION) {
                    saveObject();
                }
            }
            change_flag = false;
        }
    }
}
