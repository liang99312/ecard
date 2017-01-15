/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.param;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.List;
import java.util.Properties;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextField;
import javax.swing.JTree;
import org.jdesktop.beansbinding.AutoBinding;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JListBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.comm.HrLog;
import org.jhrcore.comm.ConfigManager;
import org.jhrcore.entity.A01;
import org.jhrcore.entity.SysParameter;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.TempFieldInfo;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.iservice.impl.RSImpl;
import org.jhrcore.msg.CommMsg;
import org.jhrcore.msg.emp.EmpParamMsg;
import org.jhrcore.mutil.EmpUtil;
import org.jhrcore.rebuild.EntityBuilder;
import org.jhrcore.ui.CheckTreeNode;
import org.jhrcore.ui.ContextManager;
import org.jhrcore.ui.JCheckBoxList;
import org.jhrcore.ui.task.IModulePanel;
import org.jhrcore.ui.ModalDialog;
import org.jhrcore.ui.SearchTreeFieldDialog;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.ComponentUtil;
import org.jhrcore.util.MsgUtil;
import org.jhrcore.util.SysUtil;

/**
 *
 * @author Administrator
 */
public class EmpParamPanel extends javax.swing.JPanel implements IModulePanel {

    private Properties properties;
    private FTable ftable;
    private Hashtable<String, String> a01_field_keys = new Hashtable<String, String>();
    private Hashtable<String, String> id_field_keys = new Hashtable<String, String>();
    private SysParameter paraIDFields = null;
    private SysParameter paraIDSavePhoto = null;
    private SysParameter paraIDFieldEdit = null;
    private SysParameter paraICNOField = null;
    private SysParameter paraICGWField = null;
    private HrLog log = new HrLog("EmpParam.设置二代证信息与人员表对应");
    public static final String module_code = "EmpParam";
    private JListBinding a01_binding;
    private JListBinding id_binding;
    private List<TempFieldInfo> id_fields = new ArrayList<TempFieldInfo>();
    private List idcardList = Arrays.asList(new String[]{"新中新DKQ", "国腾ICR100"});
    private List iccardList = Arrays.asList(new String[]{"IC卡读写器", "MF-800读写器"});
    private JCheckBoxList cbList;

    /**
     * Creates new form EmpParamPanel
     */
    public EmpParamPanel() {
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        id_fields.add(getNewInfo("personName", "姓名"));
        id_fields.add(getNewInfo("personSex", "性别"));
        id_fields.add(getNewInfo("personBorn", "出生日期"));
        id_fields.add(getNewInfo("personIDCardNo", "身份证号码"));
        id_fields.add(getNewInfo("personAddress", "住址"));
        id_fields.add(getNewInfo("personNation", "民族"));
        id_fields.add(getNewInfo("PersonGrantDept", "签发机关"));
        id_fields.add(getNewInfo("PersonUserLifeBegin", "起始有效时间"));
        id_fields.add(getNewInfo("PersonUserLifeEnd", "失效时间"));
        id_binding = SwingBindings.createJListBinding(UpdateStrategy.READ_WRITE, id_fields, jlsIDField);
        id_binding.bind();
        List<TempFieldInfo> person_fields = EntityBuilder.getCommFieldInfoListOf(A01.class, EntityBuilder.COMM_FIELD_VISIBLE);
        for (TempFieldInfo tfi : person_fields) {
            a01_field_keys.put(SysUtil.tranField(tfi.getField_name()), tfi.getCaption_name());
        }
        a01_binding = SwingBindings.createJListBinding(UpdateStrategy.READ, person_fields, jlsPersonField);
        a01_binding.bind();
        Hashtable<String, SysParameter> paras = EmpUtil.getParas();
        paraIDFields = paras.get(EmpUtil.IDToEmpFields);
        paraIDSavePhoto = paras.get(EmpUtil.IDToEmpPhoto);
        paraIDFieldEdit = paras.get(EmpUtil.IDFieldEditable);
        paraICNOField = paras.get(EmpUtil.ICNOField);
        paraICGWField = paras.get(EmpUtil.ICGWField);
        jcbSavePhoto.setSelected("1".equals(paraIDSavePhoto.getSysparameter_value()));
        jcbFieldEdit.setSelected("1".equals(paraIDFieldEdit.getSysparameter_value()));
        List<String> fields = new ArrayList<String>();
        fields.add("二代证信息");
        fields.add("人员表字段名");
        fields.add("人员表字段描述");
        ftable = new FTable(fields);
        ftable.setBorder(javax.swing.BorderFactory.createEtchedBorder());
        refreshDatas();
        pnlIDToEmp.add(ftable, BorderLayout.CENTER);
        properties = ConfigManager.getConfigManager().getProps();
        initCard();
        if (UserContext.isSA) {
            List appendixList = new ArrayList();
            List entity_list = CommUtil.fetchEntities("from EntityDef ed where ed.entityClass.entityType_code='ANNEX' order by ed.order_no");
            for (Object obj : entity_list) {
                EntityDef tmp_def = (EntityDef) obj;
                if (UserContext.hasEntityViewRight(tmp_def.getEntityName())) {
                    appendixList.add(tmp_def);
                }
            }
            cbList = new JCheckBoxList(appendixList);
            List<String> checkAppendixs = EmpUtil.getCheckAppendixTable();
            for (int i = 0; i < appendixList.size(); i++) {
                EntityDef ed = (EntityDef) appendixList.get(i);
                if (checkAppendixs.contains(ed.getEntityName())) {
                    cbList.CheckedItem(i);
                }
            }
            pnlTable.add(new JScrollPane(cbList));
        } else {
            jtpMain.remove(pnlR);
            jtpMain.remove(pnlAppendix);
        }
    }

    private void setupEvents() {
        jcbAll.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                if (jcbAll.isSelected()) {
                    cbList.SelectAll();
                } else {
                    cbList.ClearSelectAll();
                }
                cbList.updateUI();
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                addField();
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                log.info(e);
                ftable.deleteSelectedRows();
            }
        });
        btnSave.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                savePara();
            }
        });
        btnICNoField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setICField(jtfICNOField);
            }
        });
        btnICGWField.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setICField(jtfICGWField);
            }
        });
    }

    private TempFieldInfo getNewInfo(String fieldName, String fieldCaption) {
        TempFieldInfo tfi = new TempFieldInfo();
        tfi.setField_name(fieldName);
        tfi.setCaption_name(fieldCaption);
        id_field_keys.put(fieldName, fieldCaption);
        return tfi;
    }

    private void initCard() {
        String index = properties.getProperty("dukaqi_ic_card_type");
        SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, iccardList, jcbbIC).bind();
        jcbbIC.setSelectedIndex(SysUtil.objToInt(index));
        jtfICPort.setText(SysUtil.objToStr(properties.getProperty("ic_card_port")));
        jtfICNOField.setText(paraICNOField.getSysparameter_value());
        jtfICGWField.setText(paraICGWField.getSysparameter_value());
        index = properties.getProperty("id_card_type");
        SwingBindings.createJComboBoxBinding(AutoBinding.UpdateStrategy.READ_WRITE, idcardList, jcbbID).bind();
        jcbbID.setSelectedIndex(SysUtil.objToInt(index));
        jtfIDPort.setText(SysUtil.objToStr(properties.getProperty("id_card_port")));
    }

    private void setICField(JTextField jtf) {
        String text = jtf.getText();
        List<TempFieldInfo> infos = new ArrayList();
        EntityBuilder.buildInfo(A01.class, infos, null);
        CheckTreeNode selectNode = null;
        CheckTreeNode rootNode = new CheckTreeNode(EmpParamMsg.ttl001);
        for (TempFieldInfo tfi : infos) {
            String fieldType = tfi.getField_type().toLowerCase();
            if (fieldType.equals("boolean") || fieldType.equals("date") || fieldType.equals("float")) {
                continue;
            }
            CheckTreeNode node = new CheckTreeNode(tfi);
            rootNode.add(node);
            if (tfi.getField_name().equals(text)) {
                selectNode = node;
            }
        }
        JTree tree = new JTree(rootNode);
        HRRendererView.getCommMap().initTree(tree);
        ComponentUtil.initTreeSelection(tree, selectNode);
        JPanel pnl = new JPanel(new BorderLayout());
        pnl.add(new JScrollPane(tree));
        SearchTreeFieldDialog.doQuickSearch(EmpParamMsg.ttl001.toString(), tree);
        pnl.setPreferredSize(new Dimension(300, 350));
        if (ModalDialog.doModal(ContextManager.getMainFrame(), pnl, EmpParamMsg.ttl002, tree, null)) {
            CheckTreeNode node = (CheckTreeNode) tree.getLastSelectedPathComponent();
            if (node.getUserObject() instanceof TempFieldInfo) {
                jtf.setText(((TempFieldInfo) node.getUserObject()).getField_name());
            }
        }
    }

    private void addField() {
        TempFieldInfo tmp_card_tfi = (TempFieldInfo) jlsIDField.getSelectedValue();
        TempFieldInfo tmp_per_tfi = (TempFieldInfo) jlsPersonField.getSelectedValue();
        String tmp_per = SysUtil.tranField(tmp_per_tfi.getField_name());
        Object[] exist_objs = null;
        for (Object obj : ftable.getObjects()) {
            Object[] objs = (Object[]) obj;
            if (objs[1].equals(tmp_per)) {
                exist_objs = objs;
                break;
            }
        }
        if (exist_objs == null) {
            exist_objs = new Object[4];
            ftable.addObject(exist_objs);
        }
        exist_objs[0] = tmp_card_tfi.getCaption_name().replace(" ", "");
        exist_objs[1] = tmp_per;
        exist_objs[2] = tmp_per_tfi.getCaption_name();
        exist_objs[3] = tmp_card_tfi.getField_name();
        pnlIDToEmp.updateUI();
    }

    private void savePara() {
        int ind = jtpMain.getSelectedIndex();
        if (ind == 0) {
            saveIC();
        } else if (ind == 1) {
            List up_list = new ArrayList();
            paraIDSavePhoto.setSysparameter_value(jcbSavePhoto.isSelected() ? "1" : "0");
            paraIDFieldEdit.setSysparameter_value(jcbFieldEdit.isSelected() ? "1" : "0");
            paraICNOField.setSysparameter_value(jtfICNOField.getText());
            paraICGWField.setSysparameter_value(jtfICGWField.getText());
            String sysparameter_value = "";
            for (Object obj : ftable.getObjects()) {
                Object[] objs = (Object[]) obj;
                sysparameter_value = sysparameter_value + objs[3] + ":" + objs[1] + ";";
            }
            paraIDFields.setSysparameter_value(sysparameter_value);
            up_list.add(paraIDSavePhoto);
            up_list.add(paraIDFieldEdit);
            up_list.add(paraICNOField);
            up_list.add(paraICGWField);
            up_list.add(paraIDFields);
            ValidateSQLResult result = CommUtil.saveParameters(up_list);
            if (result.getResult() == 0) {
                MsgUtil.showInfoMsg(CommMsg.SAVESUCCESS_MESSAGE);
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
        } else {
            ValidateSQLResult result = RSImpl.saveAnnexCheck(cbList.getCheckedObjects());
            if (result.getResult() == 0) {
                MsgUtil.showInfoMsg(CommMsg.RESTARTSERVER);
            } else {
                MsgUtil.showHRSaveErrorMsg(result);
            }
        }
    }

    private void saveIC() {
        properties.setProperty("dukaqi_ic_card_type", jcbbIC.getSelectedIndex() + "");
        properties.setProperty("ic_card_port", jtfICPort.getText().trim());
        properties.setProperty("id_card_type", jcbbID.getSelectedIndex() + "");
        properties.setProperty("id_card_port", jtfIDPort.getText().trim());
        ConfigManager.getConfigManager().save2();
        MsgUtil.showInfoMsg(EmpParamMsg.msg001);
    }

    private void refreshDatas() {
        if ("1".equals(paraIDSavePhoto.getSysparameter_value())) {
            jcbSavePhoto.setSelected(true);
        }
        if (paraIDFields.getSysparameter_value() == null || paraIDFields.getSysparameter_value().trim().equals("")) {
            return;
        }
        String[] value = paraIDFields.getSysparameter_value().split("\\;");
        for (String key : value) {
            String[] row_key = key.split("\\:");
            if (row_key.length < 2) {
                continue;
            }
            Object[] objs = new Object[4];
            String name = a01_field_keys.get(row_key[1].toString().replace("_code_", ""));
            if (name == null) {
                continue;
            }
            objs[0] = id_field_keys.get(row_key[0].toString());
            objs[1] = row_key[1];
            objs[2] = name;
            objs[3] = row_key[0];
            ftable.addObject(objs);
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

        jToolBar3 = new javax.swing.JToolBar();
        btnSave = new javax.swing.JButton();
        jtpMain = new javax.swing.JTabbedPane();
        jPanel1 = new javax.swing.JPanel();
        jPanel2 = new javax.swing.JPanel();
        lblId1 = new javax.swing.JLabel();
        jcbbID = new javax.swing.JComboBox();
        jLabel5 = new javax.swing.JLabel();
        jtfIDPort = new javax.swing.JTextField();
        jPanel3 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        jtfICPort = new javax.swing.JTextField();
        jcbbIC = new javax.swing.JComboBox();
        pnlR = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        jcbSavePhoto = new javax.swing.JCheckBox();
        jcbFieldEdit = new javax.swing.JCheckBox();
        jPanel9 = new javax.swing.JPanel();
        jPanel10 = new javax.swing.JPanel();
        jScrollPane1 = new javax.swing.JScrollPane();
        jlsIDField = new javax.swing.JList();
        jScrollPane3 = new javax.swing.JScrollPane();
        jlsPersonField = new javax.swing.JList();
        jPanel11 = new javax.swing.JPanel();
        btnAdd = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        pnlIDToEmp = new javax.swing.JPanel();
        jPanel7 = new javax.swing.JPanel();
        jLabel8 = new javax.swing.JLabel();
        jtfICNOField = new javax.swing.JTextField();
        btnICNoField = new javax.swing.JButton();
        jLabel9 = new javax.swing.JLabel();
        jtfICGWField = new javax.swing.JTextField();
        btnICGWField = new javax.swing.JButton();
        pnlAppendix = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        jLabel3 = new javax.swing.JLabel();
        jcbAll = new javax.swing.JCheckBox();
        pnlTable = new javax.swing.JPanel();

        jToolBar3.setFloatable(false);
        jToolBar3.setRollover(true);

        btnSave.setText("保存");
        btnSave.setFocusable(false);
        btnSave.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSave.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar3.add(btnSave);

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder("二代证读卡器设置："));

        lblId1.setText("读卡器选择：");

        jLabel5.setText("  读卡器端口：");

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(lblId1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbbID, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtfIDPort, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(238, Short.MAX_VALUE))
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblId1)
                    .addComponent(jcbbID, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel5)
                    .addComponent(jtfIDPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(34, Short.MAX_VALUE))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("IC卡读卡器设置："));

        jLabel1.setText("读卡器选择：");

        jLabel2.setText("IC卡端口设置：");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jcbbIC, javax.swing.GroupLayout.PREFERRED_SIZE, 131, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtfICPort, javax.swing.GroupLayout.PREFERRED_SIZE, 128, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(235, Short.MAX_VALUE))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2)
                    .addComponent(jcbbIC, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jtfICPort, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(379, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jtpMain.addTab("读卡器设置", jPanel1);

        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("二代证更新策略："));

        jcbSavePhoto.setText("更新人员信息同时更新照片");
        jcbSavePhoto.setFocusable(false);
        jcbSavePhoto.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jcbSavePhoto.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        jcbFieldEdit.setText("对应人员表字段不可编辑");
        jcbFieldEdit.setFocusable(false);
        jcbFieldEdit.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jcbFieldEdit.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(17, 17, 17)
                .addComponent(jcbSavePhoto)
                .addGap(18, 18, 18)
                .addComponent(jcbFieldEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 165, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(309, Short.MAX_VALUE))
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jcbFieldEdit, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbSavePhoto, javax.swing.GroupLayout.PREFERRED_SIZE, 15, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(10, Short.MAX_VALUE))
        );

        jPanel9.setBorder(javax.swing.BorderFactory.createTitledBorder("二代证人事字段对应："));

        jlsIDField.setBorder(javax.swing.BorderFactory.createTitledBorder("二代证信息："));
        jScrollPane1.setViewportView(jlsIDField);

        jlsPersonField.setBorder(javax.swing.BorderFactory.createTitledBorder("人员基本信息："));
        jScrollPane3.setViewportView(jlsPersonField);

        javax.swing.GroupLayout jPanel10Layout = new javax.swing.GroupLayout(jPanel10);
        jPanel10.setLayout(jPanel10Layout);
        jPanel10Layout.setHorizontalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel10Layout.createSequentialGroup()
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 144, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 155, Short.MAX_VALUE))
        );
        jPanel10Layout.setVerticalGroup(
            jPanel10Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
            .addComponent(jScrollPane3, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
        );

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/select_one.png"))); // NOI18N

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/remove_one.png"))); // NOI18N

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(43, 43, 43)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(268, Short.MAX_VALUE))
        );

        pnlIDToEmp.setBorder(javax.swing.BorderFactory.createTitledBorder("对应信息（读二代证时写入人员表）："));
        pnlIDToEmp.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 678, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addGap(7, 7, 7)
                    .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(jPanel11, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                    .addComponent(pnlIDToEmp, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 383, Short.MAX_VALUE)
            .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel9Layout.createSequentialGroup()
                    .addContainerGap()
                    .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addComponent(pnlIDToEmp, javax.swing.GroupLayout.DEFAULT_SIZE, 373, Short.MAX_VALUE)
                        .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jPanel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))))
        );

        jPanel7.setBorder(javax.swing.BorderFactory.createTitledBorder("IC卡对应人事岗位字段："));

        jLabel8.setText("卡号字段设置：");

        jtfICNOField.setEditable(false);

        btnICNoField.setText("...");
        btnICNoField.setPreferredSize(new java.awt.Dimension(24, 23));

        jLabel9.setText("岗位字段设置：");

        jtfICGWField.setEditable(false);

        btnICGWField.setText("...");
        btnICGWField.setPreferredSize(new java.awt.Dimension(24, 23));

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jtfICNOField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnICNoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel9, javax.swing.GroupLayout.PREFERRED_SIZE, 98, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jtfICGWField, javax.swing.GroupLayout.PREFERRED_SIZE, 120, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnICGWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(154, 154, 154))
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel8)
                    .addComponent(jtfICNOField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnICNoField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel9)
                    .addComponent(jtfICGWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnICGWField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel7, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jPanel7, javax.swing.GroupLayout.PREFERRED_SIZE, 55, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout pnlRLayout = new javax.swing.GroupLayout(pnlR);
        pnlR.setLayout(pnlRLayout);
        pnlRLayout.setHorizontalGroup(
            pnlRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        pnlRLayout.setVerticalGroup(
            pnlRLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        jtpMain.addTab("读卡设置", pnlR);

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        jLabel3.setText("请选择需要审批的附表");
        jToolBar1.add(jLabel3);

        jcbAll.setText("全选");
        jcbAll.setFocusable(false);
        jcbAll.setHorizontalTextPosition(javax.swing.SwingConstants.RIGHT);
        jcbAll.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(jcbAll);

        pnlTable.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout pnlAppendixLayout = new javax.swing.GroupLayout(pnlAppendix);
        pnlAppendix.setLayout(pnlAppendixLayout);
        pnlAppendixLayout.setHorizontalGroup(
            pnlAppendixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
            .addComponent(pnlTable, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 690, Short.MAX_VALUE)
        );
        pnlAppendixLayout.setVerticalGroup(
            pnlAppendixLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(pnlAppendixLayout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlTable, javax.swing.GroupLayout.DEFAULT_SIZE, 502, Short.MAX_VALUE))
        );

        jtpMain.addTab("附表审批设置", pnlAppendix);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
            .addComponent(jToolBar3, javax.swing.GroupLayout.DEFAULT_SIZE, 695, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addComponent(jToolBar3, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jtpMain, javax.swing.GroupLayout.DEFAULT_SIZE, 562, Short.MAX_VALUE))
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnICGWField;
    private javax.swing.JButton btnICNoField;
    private javax.swing.JButton btnSave;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JToolBar jToolBar3;
    private javax.swing.JCheckBox jcbAll;
    private javax.swing.JCheckBox jcbFieldEdit;
    private javax.swing.JCheckBox jcbSavePhoto;
    private javax.swing.JComboBox jcbbIC;
    private javax.swing.JComboBox jcbbID;
    private javax.swing.JList jlsIDField;
    private javax.swing.JList jlsPersonField;
    private javax.swing.JTextField jtfICGWField;
    private javax.swing.JTextField jtfICNOField;
    private javax.swing.JTextField jtfICPort;
    private javax.swing.JTextField jtfIDPort;
    private javax.swing.JTabbedPane jtpMain;
    private javax.swing.JLabel lblId1;
    private javax.swing.JPanel pnlAppendix;
    private javax.swing.JPanel pnlIDToEmp;
    private javax.swing.JPanel pnlR;
    private javax.swing.JPanel pnlTable;
    // End of variables declaration//GEN-END:variables

    @Override
    public void setFunctionRight() {
        ComponentUtil.setSysFuntionNew(this);
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
