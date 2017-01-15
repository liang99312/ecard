/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * RebuilIndexPanel.java
 *
 * Created on 2011-1-13, 10:47:36
 */
package org.jhrcore.client.system.comm;

import com.foundercy.pf.control.table.FTable;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;
import javax.swing.JOptionPane;
import javax.swing.JScrollPane;
import javax.swing.JTree;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreePath;
import org.jdesktop.beansbinding.AutoBinding.UpdateStrategy;
import org.jdesktop.swingbinding.JComboBoxBinding;
import org.jdesktop.swingbinding.SwingBindings;
import org.jhrcore.client.CommUtil;
import org.jhrcore.client.UserContext;
import org.jhrcore.entity.base.EntityDef;
import org.jhrcore.entity.base.FieldDef;
import org.jhrcore.entity.base.FieldIndex;
import org.jhrcore.entity.base.ModuleInfo;
import org.jhrcore.entity.salary.ValidateSQLResult;
import org.jhrcore.ui.HrTextPane;
import org.jhrcore.ui.action.CloseAction;
import org.jhrcore.ui.renderer.HRRendererView;
import org.jhrcore.util.MsgUtil;

/**
 *
 * @author hflj
 */
public class RebuilIndexPanel extends javax.swing.JPanel {

    private JComboBoxBinding module_binding;
    private JComboBoxBinding entity_binding;
    private List<ModuleInfo> modules = new ArrayList<ModuleInfo>();
    private List<EntityDef> entitys = new ArrayList<EntityDef>();
    private List<EntityDef> sysEntitys;
    private Hashtable<String, List<EntityDef>> module_entity_keys = new Hashtable<String, List<EntityDef>>();
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("�����ֶ�");
    private JTree sysFieldTree;
    private FTable ftableIndex;
    private HashSet<String> existFields = new HashSet<String>();
    private HrTextPane jtaFormulaText;
    private Hashtable<String, String> entityCaptionKeys = new Hashtable<String, String>();
    private Hashtable<String, String> fieldCaptionKeys = new Hashtable<String, String>();
    private int startInd = 0;

    /** Creates new form RebuilIndexPanel */
    public RebuilIndexPanel(List<EntityDef> entitys) {
        this.sysEntitys = entitys;
        initComponents();
        initOthers();
        setupEvents();
    }

    private void initOthers() {
        HashSet<String> module_keys = new HashSet<String>();
        for (EntityDef ed : sysEntitys) {
            entityCaptionKeys.put(ed.getEntityName().toLowerCase(), ed.getEntityCaption());
            ModuleInfo mi = ed.getEntityClass().getModuleInfo();
            if (!module_keys.contains(mi.getModule_key())) {
                module_keys.add(mi.getModule_key());
                modules.add(mi);
            }
            List<EntityDef> entity_list = module_entity_keys.get(mi.getModule_key());
            if (entity_list == null) {
                entity_list = new ArrayList<EntityDef>();
            }
            entity_list.add(ed);
            for (FieldDef fd : ed.getFieldDefs()) {
                fieldCaptionKeys.put(fd.getField_name().toLowerCase(), fd.getField_caption());
            }
            module_entity_keys.put(mi.getModule_key(), entity_list);
        }
        module_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, modules, jcbModule);
        module_binding.bind();
        entity_binding = SwingBindings.createJComboBoxBinding(UpdateStrategy.READ_WRITE, entitys, jcbEntity);
        entity_binding.bind();
        sysFieldTree = new JTree(rootNode);
        pnlSysField.add(new JScrollPane(sysFieldTree));
        HRRendererView.getRebuildMap(sysFieldTree).initTree(sysFieldTree);
        sysFieldTree.setRootVisible(false);
        sysFieldTree.setShowsRootHandles(true);
        sysFieldTree.expandRow(1);
        ftableIndex = new FTable(FieldIndex.class, false, false);
        ftableIndex.setEditable(true);
        jtaFormulaText = new HrTextPane();
    }

    private void setupEvents() {
        jcbModule.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                Object obj = jcbModule.getSelectedItem();
                if (obj == null) {
                    return;
                }
                ModuleInfo mi = (ModuleInfo) obj;
                List<EntityDef> entity_list = module_entity_keys.get(mi.getModule_key());
                entitys.clear();
                if (entity_list != null) {
                    entitys.addAll(entity_list);
                }
                entity_binding.unbind();
                entity_binding.bind();
                if (entitys.size() > 0) {
                    jcbEntity.setSelectedIndex(0);
                }
            }
        });
        jcbEntity.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                refreshField();
            }
        });
        btnAdd.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                TreePath tp = sysFieldTree.getSelectionPath();
                if (tp == null || tp.getLastPathComponent() == null) {
                    return;
                }
                DefaultMutableTreeNode selNode = (DefaultMutableTreeNode) tp.getLastPathComponent();
                if (selNode.getUserObject() instanceof FieldDef) {
                    FieldDef fd = (FieldDef) selNode.getUserObject();
                    addIndexField(fd.getEntityDef().getEntityName(), fd.getEntityDef().getEntityCaption(), fd.getField_name(), fd.getField_caption(), false, false);
                }
                setMainState();
            }
        });
        btnDel.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                List list = ftableIndex.getSelectObjects();
                for (Object obj : list) {
                    FieldIndex fi = (FieldIndex) obj;
                    existFields.remove(fi.getEntity_name() + "." + fi.getField_name());
                }
                ftableIndex.deleteSelectedRows();
                setMainState();
            }
        });
        btnUp.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ftableIndex.moveRowPosition(-1);
                setMainState();
            }
        });
        btnDown.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                ftableIndex.moveRowPosition(1);
                setMainState();
            }
        });
        btnExcute.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                String text = jtaFormulaText.getText();
                if (jcbDialect.getSelectedItem().toString().equals("oracle")) {
                    String sql = "declare cnt  number;\n begin \n select count(1) into cnt from all_tables where table_name='INDEX_TEMP';";
                    sql += "if (cnt = 0) then \n execute immediate 'create table index_temp(name varchar2(100))';";
                    sql += "\n commit;\n end if;\n end;";
                    CommUtil.excuteSQL(sql);
                }
                ValidateSQLResult result = CommUtil.excuteSQLs_jdbc(text, " GO \n");
                if (result.getResult() == 0) {
                    JOptionPane.showMessageDialog(null, "ִ�гɹ�!");
                } else {
                    MsgUtil.showHRSaveErrorMsg(result);
                }
            }
        });
        jcbDialect.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                createSQL(jcbDialect.getSelectedItem().toString());
            }
        });
        CloseAction.doCloseAction(btnClose);
        btnSysIndex.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                existFields.clear();
                ftableIndex.deleteAllRows();
                startInd = 0;
                if (btnSysIndex.isSelected()) {
                    addSysIndex();
                }
                setMainState();
            }
        });
        btnSQL.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                setMainState();
                createSQL(jcbDialect.getSelectedItem().toString());
            }
        });
        jcbModule.setSelectedIndex(0);
        jcbDialect.setSelectedItem(UserContext.sql_dialect);
        setMainState();
    }

    private void createSQL(String db_type) {
        if (!btnSQL.isSelected()) {
            return;
        }
        HashSet<String> tables = new HashSet<String>();
        StringBuilder ex_sql = new StringBuilder();
        if (db_type.equals("sqlserver")) {
            ex_sql.append("if    exists(select   1   from   sysobjects   where   id=object_id( 'sp_index')   and   xtype= 'P ') \n");
            ex_sql.append("drop procedure sp_index \n GO \n");
            ex_sql.append("create procedure sp_index @objname varchar(255) as \n");
            ex_sql.append("set nocount on \n");
            ex_sql.append("declare @objid int,			-- the object id of the table \n");
            ex_sql.append("@indid smallint,	-- the index id of an index \n");
            ex_sql.append("@groupid smallint,  -- the filegroup id of an index \n");
            ex_sql.append("@indname sysname,");
            ex_sql.append("@groupname sysname,");
            ex_sql.append("@status int, \n");
            ex_sql.append("@keys nvarchar(2126),	--Length (16*max_identifierLength)+(15*2)+(16*3) \n");
            ex_sql.append("@dbname	sysname \n");
            ex_sql.append("select @dbname = parsename(@objname,3) \n");
            ex_sql.append("select @objid = object_id(@objname) \n");
            ex_sql.append("declare ms_crs_ind cursor local static for \n");
            ex_sql.append("select indid, groupid, name, status from sysindexes \n");
            ex_sql.append("where id = @objid and indid > 0 and indid < 255 and (status & 64)=0 order by indid \n");
            ex_sql.append("open ms_crs_ind \n");
            ex_sql.append("fetch ms_crs_ind into @indid, @groupid, @indname, @status \n");
            ex_sql.append("-- IF NO INDEX, QUIT \n");
            ex_sql.append("if @@fetch_status < 0 \n");
            ex_sql.append("begin \n");
            ex_sql.append("deallocate ms_crs_ind \n");
            ex_sql.append("raiserror(15472,-1,-1) --'Object does not have any indexes.' \n");
            //ex_sql.append("return (0) \n");
            ex_sql.append("end \n");
            ex_sql.append("create table #spindtab \n");
            ex_sql.append("( \n");
            ex_sql.append("index_name sysname	collate database_default NOT NULL, \n");
            ex_sql.append("stats	int,groupname	sysname collate database_default NOT NULL, \n");
            ex_sql.append("index_keys			nvarchar(2126)	collate database_default NOT NULL -- see @keys above for length descr \n");
            ex_sql.append(") \n");
            ex_sql.append("while @@fetch_status >= 0 \n");
            ex_sql.append("begin \n");
            ex_sql.append("declare @i int, @thiskey nvarchar(131) -- 128+3 \n");
            ex_sql.append("select @keys = index_col(@objname, @indid, 1), @i = 2 \n");
            ex_sql.append("if (indexkey_property(@objid, @indid, 1, 'isdescending') = 1) \n");
            ex_sql.append("select @keys = @keys  + '(-)' \n");
            ex_sql.append("select @thiskey = index_col(@objname, @indid, @i) \n");
            ex_sql.append("if ((@thiskey is not null) and (indexkey_property(@objid, @indid, @i, 'isdescending') = 1)) \n");
            ex_sql.append("select @thiskey = @thiskey + '(-)' \n");
            ex_sql.append("while (@thiskey is not null ) \n");
            ex_sql.append("begin \n");
            ex_sql.append("select @keys = @keys + ', ' + @thiskey, @i = @i + 1 \n");
            ex_sql.append("select @thiskey = index_col(@objname, @indid, @i) \n");
            ex_sql.append("if ((@thiskey is not null) and (indexkey_property(@objid, @indid, @i, 'isdescending') = 1)) \n");
            ex_sql.append("select @thiskey = @thiskey + '(-)' \n");
            ex_sql.append("end \n");
            ex_sql.append("select @groupname = groupname from sysfilegroups where groupid = @groupid \n");
            ex_sql.append("insert into #spindtab values (@indname, @status, @groupname, @keys) \n");
            ex_sql.append("fetch ms_crs_ind into @indid, @groupid, @indname, @status \n");
            ex_sql.append("end \n");
            ex_sql.append("deallocate ms_crs_ind \n");
            ex_sql.append("declare @name varchar(50),@sql varchar(4000),@J int; \n");
            ex_sql.append("select identity(int,1,1) as xh, \n");
            ex_sql.append("index_name into #sp_temp \n");
            ex_sql.append("from #spindtab where index_name not in (select a.name from sysobjects a,sysobjects B where a.xtype='PK' AND upper(B.name)=@objname and a.PARENT_OBJ = b.id) \n");
            ex_sql.append("set @I = 1; \n");
            ex_sql.append("set @J = (select count(*) from #sp_temp) \n");
            ex_sql.append("while(@I<=@J) \n");
            ex_sql.append("begin \n");
            ex_sql.append("set @name = (select index_name from #sp_temp where xh=@I); \n");
            ex_sql.append("set @sql = 'drop index '+@objname+'.'+@name; \n");
            ex_sql.append("exec (@sql) \n");
            ex_sql.append("set @I = @I+1; \n");
            ex_sql.append("end \n");
            ex_sql.append("drop table #sp_temp; \n");
            ex_sql.append("drop table #spindtab; \n");
            ex_sql.append(" GO \n");
            for (Object obj : ftableIndex.getObjects()) {
                FieldIndex fi = (FieldIndex) obj;
                String entity = fi.getEntity_name();
                entity = entity.toUpperCase();
                ex_sql.append("--").append(entity).append("��").append(fi.getField_name()).append("�� ").append(fi.isPrimary_flag() ? "���� " : " ").append(fi.isUnique_flag() ? "Ψһ " : " ").append("����\n");
                String index_type = fi.isUnique_flag() ? "UNIQUE" : "";
                if (!tables.contains(entity)) {
                    tables.add(entity);
                    ex_sql.append("exec sp_index '").append(entity).append("'; \n");
                }
                if (fi.isPrimary_flag()) {
                    ex_sql.append("IF NOT EXISTS (select * from sysobjects a,sysobjects B where a.xtype='PK' AND upper(B.name)='").append(entity).append("' and a.PARENT_OBJ = b.id) \n");
                    ex_sql.append("BEGIN \n ");
                    ex_sql.append("ALTER TABLE [dbo].[").append(entity).append("] ADD  CONSTRAINT [").append(fi.getIndex_name()).append("] PRIMARY KEY CLUSTERED (").append(fi.getField_name()).append(" ASC) ON [PRIMARY] \n");
                    ex_sql.append("END\n");
                } else {
                    ex_sql.append("CREATE ").append(index_type).append(" INDEX  ").append(fi.getIndex_name()).append(" ON ").append(fi.getEntity_name()).append("(").append(fi.getField_name()).append(")\n");
                }
            }
        } else if (db_type.equals("oracle")) {
            ex_sql.append("declare \n");
            ex_sql.append("cnt  number;\n");
            ex_sql.append("cursor index_c is select name from index_temp; \n");
            ex_sql.append("begin \n");
            ex_sql.append("cnt := 0;\n");
            ex_sql.append("select count(1) into cnt from all_tables where table_name='INDEX_TEMP';\n");
            ex_sql.append("if (cnt = 0) then \n");
            ex_sql.append("execute immediate 'create table index_temp(name varchar2(100))';\n");
            ex_sql.append("end if;\n");
            ex_sql.append("commit;\n");
            for (Object obj : ftableIndex.getObjects()) {
                FieldIndex fi = (FieldIndex) obj;
                String entity = fi.getEntity_name();
                ex_sql.append("--").append(entity).append("��").append(fi.getField_name()).append("�� ").append(fi.isPrimary_flag() ? "���� " : " ").append(fi.isUnique_flag() ? "Ψһ " : " ").append("����\n");
                String index_type = fi.isUnique_flag() ? "UNIQUE" : "";
                if (!tables.contains(entity)) {
                    tables.add(entity);
                    ex_sql.append("cnt := 0;\n");
                    ex_sql.append("delete from index_temp;\n");
                    ex_sql.append("commit;\n");
                    ex_sql.append("insert into index_temp select t.index_name from user_ind_columns t,user_indexes i where t.index_name = i.index_name and t.table_name = i.table_name and t.table_name = '").append(entity.toUpperCase()).append("'").append("and not exists (select 1 from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and au.table_name = '").append(entity.toUpperCase()).append("' and t.index_name=cu.constraint_name);\n");
                    ex_sql.append("select count(1) into cnt from index_temp;\n");
                    ex_sql.append("if (cnt > 0) then \n");
                    ex_sql.append("for index_r in index_c loop \n");
                    ex_sql.append("execute immediate 'drop index ' || index_r.name;\n");
                    ex_sql.append("commit;\n");
                    ex_sql.append("end loop;\n");
                    ex_sql.append("end if;\n");
                }
                if (fi.isPrimary_flag()) {
                    ex_sql.append("cnt := 0;\n");
                    ex_sql.append("select count(1) into cnt from user_cons_columns cu, user_constraints au where cu.constraint_name = au.constraint_name and au.constraint_type = 'P' and au.table_name ='").append(entity.toUpperCase()).append("';\n");
                    ex_sql.append("if (cnt = 0) then \n");
                    ex_sql.append("execute immediate '").append("ALTER TABLE ").append(entity).append(" ADD  CONSTRAINT  ").append(fi.getIndex_name()).append(" PRIMARY KEY (").append(fi.getField_name()).append(")'; \n");
                    ex_sql.append("commit;\n");
                    ex_sql.append("end if;\n");
                    ex_sql.append("cnt := 0;\n");
                    ex_sql.append("select count(1) into cnt from user_ind_columns t,user_indexes i where t.index_name = i.index_name and t.table_name = i.table_name and t.table_name = '").append(entity.toUpperCase()).append("' and column_name='").append(fi.getField_name().toUpperCase()).append("';\n");
                    ex_sql.append("if (cnt = 0) then \n");
                    ex_sql.append("execute immediate '").append("CREATE ").append(index_type).append(" INDEX  ").append(fi.getIndex_name()).append("_1 ON ").append(fi.getEntity_name()).append("(").append(fi.getField_name()).append(")';\n");
                    ex_sql.append("commit; \n");
                    ex_sql.append("end if;\n");
                } else {
                    ex_sql.append("execute immediate '").append("CREATE ").append(index_type).append(" INDEX  ").append(fi.getIndex_name()).append(" ON ").append(fi.getEntity_name()).append("(").append(fi.getField_name()).append(")';\n");
                    ex_sql.append("commit; \n");
                }
            }
            ex_sql.append("end;");
        }
        jtaFormulaText.setText(ex_sql.toString());
    }

    private void addSysIndex() {
        addIndexField("A01", "��Ա������Ϣ��", "a0190", "��Ա���", false, false);
        addIndexField("A01", "��Ա������Ϣ��", "a0101", "����", false, false);
        addIndexField("A01", "��Ա������Ϣ��", "deptCode_key", "����Key", false, false);
        addIndexField("A01Chg", "�䶯����", "basePersonChange_key", "�䶯����Key", false, false);
        addIndexField("A01PassWord", "Ա�����������", "role_key", "��ɫKey", false, false);
        addIndexField("A01PassWord", "Ա�����������", "a01_key", "��ԱKey", false, false);
        addIndexField("A01PassWord", "Ա�����������", "A01PassWord_key", "Ա�����������Key", true, true);
        addIndexField("AutoExcuteScheme", "������ʾ��", "AutoExcuteScheme_key", "������ʾ��Key", true, true);
        addIndexField("AutoNo", "��ż�����", "AutoNo_key", "��ż�����Key", true, true);
        addIndexField("AutoNoRule", "��Ź����", "AutoNoRule_key", "��Ź����Key", true, true);
        addIndexField("BasePersonChange", "�䶯����", "BasePersonChange_key", "�䶯����Key", true, true);
        addIndexField("BasePersonChange", "�䶯����", "a01_key", "��ԱKey", false, false);
        addIndexField("BasePersonChange", "�䶯����", "changescheme_key", "����ģ��Key", false, false);
        addIndexField("BasePersonChange", "�䶯����", "order_no", "���κ�", false, false);
        addIndexField("DeptChgLog", "������־��", "DeptChgLog_key", "������־��Key", true, true);
        addIndexField("DeptChgLog", "������־��", "chg_date", "�䶯����", false, false);
        addIndexField("Code", "�����", "Code_key", "�����Key", true, true);
        addIndexField("Code", "�����", "code_type", "��������", false, false);
        addIndexField("Code", "�����", "code_id", "����id", false, false);
        addIndexField("DeptCode", "���Ż�����Ϣ��", "dept_code", "���Ŵ���", false, false);
        addIndexField("DeptPositionWeave", "���Ÿ�λ����", "DeptPositionWeave_key", "���Ÿ�λ����Key", true, true);
        addIndexField("DeptPositionWeave", "���Ÿ�λ����", "DeptCode_key", "����Key", false, false);
        addIndexField("DeptPositionWeave", "���Ÿ�λ����", "g10_weave_key", "��λKey", false, false);
        addIndexField("DeptPositionWeave", "���Ÿ�λ����", "weaveCyc_key", "���", false, false);
        addIndexField("DeptWeave", "���ű���", "DeptWeave_key", "���ű���Key", true, true);
        addIndexField("DeptWeave", "���ű���", "DeptCode_key", "����Key", false, false);
        addIndexField("DeptWeave", "���ű���", "weave_year", "���", false, false);
        addIndexField("DocuClass", "�����ĵ���Ϣ�����", "class_code", "�������", false, false);
        addIndexField("EmpDocuClass", "�ļ������", "class_code", "�������", false, false);
        addIndexField("ExportScheme", "����������", "entity_name", "����", false, false);
        addIndexField("ExportScheme", "����������", "person_code", "��Ա���", false, false);
        addIndexField("ExportDetail", "���������ֶα�", "ExportScheme_key", "��������Key", false, false);
        addIndexField("FormulaScheme", "��ʽ������", "scheme_id", "����ƥ��ID", false, false);
        addIndexField("G10", "��λ������Ϣ��", "code", "��λ����", false, false);
        addIndexField("HT01", "��ͬ����", "a01_key", "��ԱKey", false, false);
        addIndexField("HT01", "��ͬ����", "deptCode_key", "����Key", false, false);
        addIndexField("Htrelieve", "��ͬ�䶯��", "hT01_key", "��ͬKey", false, false);
        addIndexField("Htrelieve", "��ͬ�䶯��", "a01_key", "��ԱKey", false, false);
        addIndexField("RyChgLog", "��Ա����ɾ����־��", "a01_key", "��ԱKey", false, false);
        addIndexField("RyChgLog", "��Ա����ɾ����־��", "changeScheme_key", "�䶯Key", false, false);
        addIndexField("SysGroup", "ϵͳ�����", "module_code", "ģ���ʶ", false, false);
        addIndexField("SysParameter", "ϵͳ������", "sysparameter_roleid", "��ɫID", false, false);
        addIndexField("SysParameter", "ϵͳ������", "sysparameter_code", "����", false, false);
        addIndexField("WorkFlowA01", "������������", "workFlowDef_key", "����Key", false, false);
        addIndexField("WorkFlowA01", "������������", "a01PassWord_key", "Ա������Key", false, false);
        addIndexField("EntityClass", "ҵ���", "entityType_name", "�������", false, false);
        addIndexField("TabName", "�����Ա�", "entityName", "����", false, false);
        addIndexField("TabName", "�����Ա�", "entityClass_key", "ҵ��Key", false, false);
        addIndexField("System", "�ֶ���Ϣ��", "field_key", "�ֶ�Key", true, true);
        addIndexField("System", "�ֶ���Ϣ��", "field_name", "�ֶ�����", false, false);
        addIndexField("System", "�ֶ���Ϣ��", "entity_key", "������Key", false, false);
        addIndexField("ChangeField", "������������", "changeScheme_key", "��������Key", false, false);
        addIndexField("ChangeItem", "�����ֶα�", "changeScheme_key", "��������Key", false, false);
        addIndexField("ChangeItem", "�����ֶα�", "fieldName", "�����ֶ���", false, false);
        addIndexField("C21", "���ʻ�����Ϣ��", "a0190", "��Ա���", false, false);
        addIndexField("C21", "���ʻ�����Ϣ��", "deptCode_key", "����Key", false, false);
        addIndexField("C21", "���ʻ�����Ϣ��", "payDeptBack_key", "���ݲ���Key", false, false);
        addIndexField("C21", "���ʻ�����Ϣ��", "gz_ym", "��������", false, false);
        addIndexField("C21", "���ʻ�����Ϣ��", "paySystem_key", "н����ϵKey", false, false);
        addIndexField("C21", "���ʻ�����Ϣ��", "A01_key", "��ԱKey", false, false);
        addIndexField("PayA01", "�������±�", "a01_key", "��ԱKey", false, false);
        addIndexField("PayA01", "�������±�", "paySystem_key", "н����ϵKey", false, false);
        addIndexField("PayDef", "������Ŀ��", "paySystem_key", "н����ϵKey", false, false);
        addIndexField("PayDef", "������Ŀ��", "field_name", "�ֶ�����", false, false);
        addIndexField("PayDeptBack", "��ʷ���ű�", "paySystem_key", "н����ϵKey", false, false);
        addIndexField("PayDeptBack", "��ʷ���ű�", "gz_ym", "��������", false, false);
        addIndexField("ShowScheme", "��ʾ������", "entity_name", "ƥ���ʶ", false, false);
        addIndexField("ShowScheme", "��ʾ������", "person_code", "Ա�����", false, false);
        addIndexField("ShowSchemeDetail", "��ʾ�����ֶα�", "ShowScheme_key", "��ʾ����Key", false, false);
        addIndexField("ShowSchemeOrder", "��ʾ���������ֶα�", "ShowScheme_key", "��ʾ����Key", false, false);
        addIndexField("ColumnSum", "���ܷ�����", "entity_name", "ƥ���ʶ", false, false);
        addIndexField("ColumnSum", "���ܷ�����", "user_code", "Ա�����", false, false);
        addIndexField("WfInstance", "������ת��Ϣ��", "wf_no", "���κ�", false, false);
        addIndexField("WfInstance", "������ת��Ϣ��", "workFlowDef_key", "����Key", false, false);
        addIndexField("WFInsLog", "������ת��־��", "wfInstance_key", "������ת��Ϣ��Key", false, false);
        addIndexField("WorkFlowDef", "����ͼ�α�", "workFlowClass_key", "�������Key", false, false);
        for (EntityDef ed : sysEntitys) {
            if (ed.getEntityClass().getEntityType_code().toUpperCase().equals("CLASS")) {
                addIndexField(ed.getEntityName(), ed.getEntityCaption(), "A01_key", ed.getEntityCaption() + "Key", true, true);
                continue;
            }
            if (ed.getEntityClass().getSuper_class() == null || ed.getEntityClass().getSuper_class().trim().equals("")) {
                addIndexField(ed.getEntityName(), ed.getEntityCaption(), ed.getEntityName() + "_key", ed.getEntityCaption() + "Key", true, true);
            } else {
                String entity = ed.getEntityClass().getSuper_class();
                addIndexField(ed.getEntityName(), ed.getEntityCaption(), entity.substring(entity.lastIndexOf(".") + 1) + "_key", ed.getEntityCaption() + "Key", true, true);
            }
        }
        setMainState();
    }

    private void addIndexField(String entity_name, String entity_caption, String field_name, String field_caption, boolean primary, boolean unique) {
        startInd++;
        String index_name = "HRInd_" + entity_name.toUpperCase() + "_" + startInd;
        if (existFields.contains(index_name)) {
            return;
        }
        entity_caption = entityCaptionKeys.get(entity_name.toLowerCase()) == null ? entity_caption : entityCaptionKeys.get(entity_name.toLowerCase());
        field_caption = fieldCaptionKeys.get(field_name.toLowerCase()) == null ? field_caption : fieldCaptionKeys.get(field_name.toLowerCase());
        FieldIndex fi = new FieldIndex();
        fi.setEntity_name(entity_name);
        fi.setEntity_caption(entity_caption);
        fi.setField_name(field_name);
        fi.setField_caption(field_caption);
        fi.setUnique_flag(unique);
        fi.setPrimary_flag(primary);
        fi.setIndex_name(index_name);
        ftableIndex.addObject(fi);
        existFields.add(index_name);
    }

    private void setMainState() {
        pnlIndexField.removeAll();
        pnlIndexField.setLayout(new BorderLayout());
        if (btnSQL.isSelected()) {
            pnlIndexField.add(jtaFormulaText);
        } else {
            ftableIndex.setBorder(javax.swing.BorderFactory.createEtchedBorder());
            pnlIndexField.add(ftableIndex);
        }
        pnlIndexField.updateUI();
        btnExcute.setEnabled(btnSQL.isSelected());
        btnUp.setEnabled(!btnSysIndex.isSelected());
        btnDown.setEnabled(!btnSysIndex.isSelected());
        btnAdd.setEnabled(!btnSysIndex.isSelected());
        btnDel.setEnabled(!btnSysIndex.isSelected());
    }

    private void refreshField() {
        rootNode.removeAllChildren();
        Object obj = jcbEntity.getSelectedItem();
        if (obj != null && obj instanceof EntityDef) {
            EntityDef ed = (EntityDef) obj;
            for (FieldDef fd : ed.getFieldDefs()) {
                rootNode.add(new DefaultMutableTreeNode(fd));
            }
        }
        sysFieldTree.updateUI();
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
        jPanel3 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        jcbModule = new javax.swing.JComboBox();
        jcbEntity = new javax.swing.JComboBox();
        jLabel1 = new javax.swing.JLabel();
        pnlSysField = new javax.swing.JPanel();
        jPanel4 = new javax.swing.JPanel();
        pnlIndexField = new javax.swing.JPanel();
        jToolBar1 = new javax.swing.JToolBar();
        btnSysIndex = new javax.swing.JToggleButton();
        jLabel3 = new javax.swing.JLabel();
        jcbDialect = new javax.swing.JComboBox();
        btnSQL = new javax.swing.JToggleButton();
        btnExcute = new javax.swing.JButton();
        btnClose = new javax.swing.JButton();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        btnAdd = new javax.swing.JButton();
        btnDel = new javax.swing.JButton();
        btnDown = new javax.swing.JButton();
        btnUp = new javax.swing.JButton();

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("ϵͳ�ֶ��б�"));

        jLabel2.setText("ģ�飺");

        jcbModule.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbEntity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("������");

        pnlSysField.setLayout(new java.awt.BorderLayout());

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel2)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbModule, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel1)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jcbEntity, javax.swing.GroupLayout.PREFERRED_SIZE, 137, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addComponent(pnlSysField, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, 187, Short.MAX_VALUE)
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbModule, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jcbEntity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlSysField, javax.swing.GroupLayout.DEFAULT_SIZE, 351, Short.MAX_VALUE))
        );

        pnlIndexField.setBorder(javax.swing.BorderFactory.createTitledBorder("�����ֶΣ�"));
        pnlIndexField.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSysIndex.setText("ϵͳ����");
        btnSysIndex.setFocusable(false);
        btnSysIndex.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSysIndex.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSysIndex);

        jLabel3.setText("���ݿ����ͣ�");
        jToolBar1.add(jLabel3);

        jcbDialect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "sqlserver", "oracle" }));
        jcbDialect.setMaximumSize(new java.awt.Dimension(80, 32767));
        jToolBar1.add(jcbDialect);

        btnSQL.setText("����SQL");
        btnSQL.setFocusable(false);
        btnSQL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSQL.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSQL);

        btnExcute.setText("ִ��");
        btnExcute.setFocusable(false);
        btnExcute.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExcute.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnExcute);

        btnClose.setText("�˳�");
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnClose);

        jLabel4.setText("˵����");

        jLabel5.setText("1�����������������ݿ��Ѵ��ڸ��ֶ���������������");

        jLabel7.setText("2��oracle��SQL�Ļ��Զ���Ӷ�ʱ����������Ա�֤������Ч");

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jToolBar1, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addGap(30, 30, 30)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel5)
                    .addComponent(jLabel7))
                .addGap(18, 18, 18))
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jLabel4)
                .addContainerGap())
            .addComponent(pnlIndexField, javax.swing.GroupLayout.DEFAULT_SIZE, 384, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addComponent(jToolBar1, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(pnlIndexField, javax.swing.GroupLayout.DEFAULT_SIZE, 329, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel4)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel5)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel7)
                .addContainerGap())
        );

        btnAdd.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/select_one.png"))); // NOI18N

        btnDel.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/remove_one.png"))); // NOI18N

        btnDown.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/move_down.png"))); // NOI18N

        btnUp.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/images/move_up.png"))); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                        .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(btnDown, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(btnUp, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addGap(62, 62, 62)
                .addComponent(btnAdd, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnDel, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(44, 44, 44)
                .addComponent(btnDown, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(btnUp, javax.swing.GroupLayout.PREFERRED_SIZE, 22, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(216, 216, 216))
            .addComponent(jPanel3, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
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
    private javax.swing.JButton btnAdd;
    private javax.swing.JButton btnClose;
    private javax.swing.JButton btnDel;
    private javax.swing.JButton btnDown;
    private javax.swing.JButton btnExcute;
    private javax.swing.JToggleButton btnSQL;
    private javax.swing.JToggleButton btnSysIndex;
    private javax.swing.JButton btnUp;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JToolBar jToolBar1;
    private javax.swing.JComboBox jcbDialect;
    private javax.swing.JComboBox jcbEntity;
    private javax.swing.JComboBox jcbModule;
    private javax.swing.JPanel pnlIndexField;
    private javax.swing.JPanel pnlSysField;
    // End of variables declaration//GEN-END:variables
}
