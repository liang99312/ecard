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
    private DefaultMutableTreeNode rootNode = new DefaultMutableTreeNode("所有字段");
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
                    JOptionPane.showMessageDialog(null, "执行成功!");
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
                ex_sql.append("--").append(entity).append("表").append(fi.getField_name()).append("列 ").append(fi.isPrimary_flag() ? "主键 " : " ").append(fi.isUnique_flag() ? "唯一 " : " ").append("索引\n");
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
                ex_sql.append("--").append(entity).append("表").append(fi.getField_name()).append("列 ").append(fi.isPrimary_flag() ? "主键 " : " ").append(fi.isUnique_flag() ? "唯一 " : " ").append("索引\n");
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
        addIndexField("A01", "人员基本信息表", "a0190", "人员编号", false, false);
        addIndexField("A01", "人员基本信息表", "a0101", "姓名", false, false);
        addIndexField("A01", "人员基本信息表", "deptCode_key", "部门Key", false, false);
        addIndexField("A01Chg", "变动附表", "basePersonChange_key", "变动主表Key", false, false);
        addIndexField("A01PassWord", "员工个人密码表", "role_key", "角色Key", false, false);
        addIndexField("A01PassWord", "员工个人密码表", "a01_key", "人员Key", false, false);
        addIndexField("A01PassWord", "员工个人密码表", "A01PassWord_key", "员工个人密码表Key", true, true);
        addIndexField("AutoExcuteScheme", "警戒提示表", "AutoExcuteScheme_key", "警戒提示表Key", true, true);
        addIndexField("AutoNo", "序号计数表", "AutoNo_key", "序号计数表Key", true, true);
        addIndexField("AutoNoRule", "序号规则表", "AutoNoRule_key", "序号规则表Key", true, true);
        addIndexField("BasePersonChange", "变动主表", "BasePersonChange_key", "变动主表Key", true, true);
        addIndexField("BasePersonChange", "变动主表", "a01_key", "人员Key", false, false);
        addIndexField("BasePersonChange", "变动主表", "changescheme_key", "调配模板Key", false, false);
        addIndexField("BasePersonChange", "变动主表", "order_no", "批次号", false, false);
        addIndexField("DeptChgLog", "部门日志表", "DeptChgLog_key", "部门日志表Key", true, true);
        addIndexField("DeptChgLog", "部门日志表", "chg_date", "变动日期", false, false);
        addIndexField("Code", "编码表", "Code_key", "编码表Key", true, true);
        addIndexField("Code", "编码表", "code_type", "编码类型", false, false);
        addIndexField("Code", "编码表", "code_id", "编码id", false, false);
        addIndexField("DeptCode", "部门基本信息表", "dept_code", "部门代码", false, false);
        addIndexField("DeptPositionWeave", "部门岗位编制", "DeptPositionWeave_key", "部门岗位编制Key", true, true);
        addIndexField("DeptPositionWeave", "部门岗位编制", "DeptCode_key", "部门Key", false, false);
        addIndexField("DeptPositionWeave", "部门岗位编制", "g10_weave_key", "岗位Key", false, false);
        addIndexField("DeptPositionWeave", "部门岗位编制", "weaveCyc_key", "年份", false, false);
        addIndexField("DeptWeave", "部门编制", "DeptWeave_key", "部门编制Key", true, true);
        addIndexField("DeptWeave", "部门编制", "DeptCode_key", "部门Key", false, false);
        addIndexField("DeptWeave", "部门编制", "weave_year", "年份", false, false);
        addIndexField("DocuClass", "个人文档信息分类表", "class_code", "分类编码", false, false);
        addIndexField("EmpDocuClass", "文件分类表", "class_code", "分类编码", false, false);
        addIndexField("ExportScheme", "导出方案表", "entity_name", "表名", false, false);
        addIndexField("ExportScheme", "导出方案表", "person_code", "人员编号", false, false);
        addIndexField("ExportDetail", "导出方案字段表", "ExportScheme_key", "导出方案Key", false, false);
        addIndexField("FormulaScheme", "公式方案表", "scheme_id", "方案匹配ID", false, false);
        addIndexField("G10", "岗位基本信息表", "code", "岗位编码", false, false);
        addIndexField("HT01", "合同主表", "a01_key", "人员Key", false, false);
        addIndexField("HT01", "合同主表", "deptCode_key", "部门Key", false, false);
        addIndexField("Htrelieve", "合同变动表", "hT01_key", "合同Key", false, false);
        addIndexField("Htrelieve", "合同变动表", "a01_key", "人员Key", false, false);
        addIndexField("RyChgLog", "人员新增删除日志表", "a01_key", "人员Key", false, false);
        addIndexField("RyChgLog", "人员新增删除日志表", "changeScheme_key", "变动Key", false, false);
        addIndexField("SysGroup", "系统分组表", "module_code", "模块标识", false, false);
        addIndexField("SysParameter", "系统参数表", "sysparameter_roleid", "角色ID", false, false);
        addIndexField("SysParameter", "系统参数表", "sysparameter_code", "编码", false, false);
        addIndexField("WorkFlowA01", "工作流名单表", "workFlowDef_key", "流程Key", false, false);
        addIndexField("WorkFlowA01", "工作流名单表", "a01PassWord_key", "员工密码Key", false, false);
        addIndexField("EntityClass", "业务表", "entityType_name", "类别名称", false, false);
        addIndexField("TabName", "表属性表", "entityName", "表名", false, false);
        addIndexField("TabName", "表属性表", "entityClass_key", "业务Key", false, false);
        addIndexField("System", "字段信息表", "field_key", "字段Key", true, true);
        addIndexField("System", "字段信息表", "field_name", "字段名称", false, false);
        addIndexField("System", "字段信息表", "entity_key", "所属表Key", false, false);
        addIndexField("ChangeField", "调动方案附表", "changeScheme_key", "调动方案Key", false, false);
        addIndexField("ChangeItem", "调动字段表", "changeScheme_key", "调动方案Key", false, false);
        addIndexField("ChangeItem", "调动字段表", "fieldName", "调动字段名", false, false);
        addIndexField("C21", "工资基本信息表", "a0190", "人员编号", false, false);
        addIndexField("C21", "工资基本信息表", "deptCode_key", "部门Key", false, false);
        addIndexField("C21", "工资基本信息表", "payDeptBack_key", "备份部门Key", false, false);
        addIndexField("C21", "工资基本信息表", "gz_ym", "工资年月", false, false);
        addIndexField("C21", "工资基本信息表", "paySystem_key", "薪酬体系Key", false, false);
        addIndexField("C21", "工资基本信息表", "A01_key", "人员Key", false, false);
        addIndexField("PayA01", "工资人事表", "a01_key", "人员Key", false, false);
        addIndexField("PayA01", "工资人事表", "paySystem_key", "薪酬体系Key", false, false);
        addIndexField("PayDef", "工资项目表", "paySystem_key", "薪酬体系Key", false, false);
        addIndexField("PayDef", "工资项目表", "field_name", "字段名称", false, false);
        addIndexField("PayDeptBack", "历史部门表", "paySystem_key", "薪酬体系Key", false, false);
        addIndexField("PayDeptBack", "历史部门表", "gz_ym", "工资年月", false, false);
        addIndexField("ShowScheme", "显示方案表", "entity_name", "匹配标识", false, false);
        addIndexField("ShowScheme", "显示方案表", "person_code", "员工编号", false, false);
        addIndexField("ShowSchemeDetail", "显示方案字段表", "ShowScheme_key", "显示方案Key", false, false);
        addIndexField("ShowSchemeOrder", "显示方案排序字段表", "ShowScheme_key", "显示方案Key", false, false);
        addIndexField("ColumnSum", "汇总方案表", "entity_name", "匹配标识", false, false);
        addIndexField("ColumnSum", "汇总方案表", "user_code", "员工编号", false, false);
        addIndexField("WfInstance", "流程流转信息表", "wf_no", "批次号", false, false);
        addIndexField("WfInstance", "流程流转信息表", "workFlowDef_key", "流程Key", false, false);
        addIndexField("WFInsLog", "流程流转日志表", "wfInstance_key", "流程流转信息表Key", false, false);
        addIndexField("WorkFlowDef", "流程图形表", "workFlowClass_key", "流程类别Key", false, false);
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

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder("系统字段列表"));

        jLabel2.setText("模块：");

        jcbModule.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jcbEntity.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));

        jLabel1.setText("表名：");

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

        pnlIndexField.setBorder(javax.swing.BorderFactory.createTitledBorder("索引字段："));
        pnlIndexField.setLayout(new java.awt.BorderLayout());

        jToolBar1.setFloatable(false);
        jToolBar1.setRollover(true);

        btnSysIndex.setText("系统索引");
        btnSysIndex.setFocusable(false);
        btnSysIndex.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSysIndex.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSysIndex);

        jLabel3.setText("数据库类型：");
        jToolBar1.add(jLabel3);

        jcbDialect.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "sqlserver", "oracle" }));
        jcbDialect.setMaximumSize(new java.awt.Dimension(80, 32767));
        jToolBar1.add(jcbDialect);

        btnSQL.setText("生成SQL");
        btnSQL.setFocusable(false);
        btnSQL.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnSQL.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnSQL);

        btnExcute.setText("执行");
        btnExcute.setFocusable(false);
        btnExcute.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnExcute.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnExcute);

        btnClose.setText("退出");
        btnClose.setFocusable(false);
        btnClose.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        btnClose.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        jToolBar1.add(btnClose);

        jLabel4.setText("说明：");

        jLabel5.setText("1。主键索引：当数据库已存在该字段索引，则不做处理");

        jLabel7.setText("2。oracle版SQL文会自动添加定时表分析任务，以保证索引有效");

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
