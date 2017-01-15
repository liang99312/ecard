    /*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client;

import com.fr.cell.ReportPane;
import com.fr.design.data.source.TableDataCardPane;
import com.fr.report.CellElement;
import com.fr.report.DefaultCellElement;
import com.fr.report.cellElement.core.DSColumn;
import java.util.logging.Level;
import java.util.logging.Logger;
import javassist.CannotCompileException;
import javassist.ClassPool;
import javassist.CtClass;
import javassist.CtConstructor;
import javassist.CtMethod;
import javassist.NotFoundException;
import org.jhrcore.client.report.componenteditor.EditorPara;

/**
 *
 * @author wangzhenhua
 */
public class FR_Rebuilder { 

    public static String conn_string = "+";

    public static void build_fr() {
        try {

            //用于取得字节码类，必须在当前的classpath中，使用全称
            CtClass ctClass = ClassPool.getDefault().get("com.fr.design.gui.core.DesignUtils");
            //需要修改的方法名称
            String mname;
            CtMethod mold;
//            mname = "getEditingReport";
//            mold = ctClass.getDeclaredMethod(mname);
//            mold.setBody("return editingSubReport;");
/*
            mname = "getReportletParameterTypeArray";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{if (($1))" +
            "return new int[] { 10, 7, 0, 4, 1, 2, 3, 5 };" +
            "return new int[] { 0, 7, 4, 1, 2, 3, 5 };}");
             */
            mname = "getEditingReportPane";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{return org.jhrcore.client.report.ReportPanel.getJWorkSheet();}");

            mname = "getEditingReportFrame";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{return org.jhrcore.client.report.FtReportUtil.getLocalJReportInternalFrame();}");

            ctClass.toClass();

            ctClass = ClassPool.getDefault().get("com.fr.design.actions.ReportAction");
            mname = "getEditingReportPane";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{return org.jhrcore.client.report.ReportPanel.getJWorkSheet();}");
            ctClass.toClass();

            ctClass = ClassPool.getDefault().get("com.fr.design.actions.UndoableAction");
            mname = "actionPerformed";
            mold = ctClass.getDeclaredMethod(mname);
            mold.insertBefore(//"System.out.println(\"OOOOOOOOOOOOOOOOOOOOP:\" + this);" +
                    "if (!(this instanceof com.fr.design.actions.insert.InsertCrossReportAction || this instanceof com.fr.design.actions.insert.InsertGroupingReportAction))"
                    + "org.jhrcore.client.report.ReportPanel.enable_locate_dict=false;");
            mold.insertAfter(//"System.out.println(\"OOOOOOOOOOOOOOOOOOOO:\" + org.jhrcore.client.report.ReportPanel.enable_locate_dict);" +
                    "org.jhrcore.client.report.ReportPanel.enable_locate_dict=true;");
            ctClass.toClass();

            changeTextCellEditor();
            changeParameterInputDialog();
//            changeReportExcute();
            changeDBTableData();
            changePreviewPane();
//            changeTableDataCardPane();
            changeParameterPane();
            changeNameTableDataDialog();
            //changeParameter();
            changeGUICoreUtils();
            changeItemEditableComboBoxPanel();
            changeDBTableDataPane();
            changeDataUtils();
            changeJReportInternalFrame();
            changeTableDataTreePane();
            changeCellElement();
            changeReportPaneDropTarget();
            changeTableChartData("com.fr.data.impl.TableChartData");
            changeDataSeries();
            changeReportChartData();
            changeValueEditorPaneFactory();

//            changeTableDataDefinition();
            changeTableDataDefinition();
//             changeTableChartData("com.fr.data.impl.ReportChartData");
//             changeTableChartData("com.fr.data.impl.TopChartData");
            changeTableDataTree();
            changeGrid();
            changeSE();

//            changeAbstractStyleAction();
//            changeReportFontNameAction();
//            changeFRCoreContext();

            /* ctClass = ClassPool.getDefault().get("com.fr.design.actions.core.DeprecatedActionManager");
            mname = "getCellMenu";
            mold = ctClass.getDeclaredMethod(mname);
            mold.insertBefore("System.out.println(\"AAAAAAAAAAAAAAA\"); ($1) = org.jhrcore.client.report.ReportPanel.getJWorkSheet();" );
            ctClass.toClass();*/
            /*
            ctClass = ClassPool.getDefault().get("com.fr.design.format.core.CellEditorDefPane.EditorDefinePane");
            mold = ctClass.getDeclaredMethod("update");
            mold.setModifiers(Modifier.PUBLIC);
            mold = ctClass.getDeclaredMethod("populate");
            mold.setModifiers(Modifier.PUBLIC);
            ctClass.toClass();*/

            ctClass = ClassPool.getDefault().get("com.fr.design.format.core.CellEditorDefPane");
            /*            ctClass.getDeclaredField("currentEditorDefinePane").setType(ClassPool.getDefault().get("javax.swing.JPanel"));
            mname = "update";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{" +
            "if (this.currentEditorDefinePane == null)" +
            "return null;" +
            " com.fr.report.cellElement.CellEditorDef localCellEditorDef = null;" +
            "if (this.currentEditorDefinePane instanceof org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane){" +
            "org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane deptEditorDefinePane = (org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane)this.currentEditorDefinePane;" +
            "localCellEditorDef = deptEditorDefinePane.update();" +
            "}" +
            " else if (this.currentEditorDefinePane instanceof org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane){" +
            "org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane codeEditorDefinePane = (org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane)this.currentEditorDefinePane;" +
            "localCellEditorDef = codeEditorDefinePane.update();" +
            "}" +
            "else {" +
            "com.fr.design.format.core.CellEditorDefPane.EditorDefinePane editorDefinePane = (com.fr.design.format.core.CellEditorDefPane.EditorDefinePane) this.currentEditorDefinePane;" +
            "localCellEditorDef=editorDefinePane.update();" +
            "}" +
            " ((com.fr.report.cellElement.AbstractCellEditorDef)localCellEditorDef).setAllowBlank(this.allowBlankCheckBox.isSelected());" +
            " return localCellEditorDef;}");
            
            
            mname = "refreshEditorPropertyPanel";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{" +
            "if (this.currentEditorDefinePane != null){" +
            "if (this.currentEditorDefinePane instanceof org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane){" +
            "org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane deptEditorDefinePane = (org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane)this.currentEditorDefinePane;" +
            "deptEditorDefinePane.update();" +//localCellEditorDef =
            "}" +
            " else if (this.currentEditorDefinePane instanceof org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane){" +
            "org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane codeEditorDefinePane = (org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane)this.currentEditorDefinePane;" +
            "codeEditorDefinePane.update();" +//localCellEditorDef =
            "}" +
            "else {" +
            "com.fr.design.format.core.CellEditorDefPane.EditorDefinePane editorDefinePane = (com.fr.design.format.core.CellEditorDefPane.EditorDefinePane) this.currentEditorDefinePane;" +
            "editorDefinePane.update();" +//localCellEditorDef=
            "}" +
            "}" +
            //"this.currentEditorDefinePane.update();" +
            //"this.cardLayout.show(this.editorPropertyPanel, \"None\");" +
            " this.currentEditorDefinePane = this.noneEditorDefinePane;" +
            "  if (($1) instanceof org.jhrcore.client.fr.cellEditorDef.DeptCodeCellEditorDef)" +
            " {" +
            "   this.cardLayout.show(this.editorPropertyPanel, \"Dept\");" +
            "   this.currentEditorDefinePane = org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane.getDeptEditorDefinePane();" +
            " }" +
            " else if (($1) instanceof org.jhrcore.client.fr.cellEditorDef.CodeCellEditorDef)" +
            " {" +
            "   this.cardLayout.show(this.editorPropertyPanel, \"Code\");" +
            "   this.currentEditorDefinePane = org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane.getCodeEditorDefinePane();" +
            " }" +
            " else if (($1) instanceof com.fr.report.cellElement.CellEditorDef)" +
            "{" +
            " if (($1) instanceof com.fr.report.cellElement.TextCellEditorDef)" +
            " {" +
            "   this.cardLayout.show(this.editorPropertyPanel, \"Text\");" +
            "   this.currentEditorDefinePane = this.textEditorDefinePane;" +
            " }" +
            " else if (($1) instanceof com.fr.report.cellElement.NumberCellEditorDef)" +
            " {" +
            "   this.cardLayout.show(this.editorPropertyPanel, \"Number\");" +
            "   this.currentEditorDefinePane = this.numberEditorDefinePane;" +
            " }" +
            " else if (($1) instanceof com.fr.report.cellElement.DateTimeCellEditorDef)" +
            " {" +
            "   this.cardLayout.show(this.editorPropertyPanel, \"DateTime\");" +
            "   this.currentEditorDefinePane = this.dateTimeEditorDefinePane;" +
            " }" +
            " else if (($1) instanceof com.fr.report.cellElement.DateCellEditorDef)" +
            " {" +
            "   this.cardLayout.show(this.editorPropertyPanel, \"Date\");" +
            "   this.currentEditorDefinePane = this.dateEditorDefinePane;" +
            " }" +
            " else if (($1) instanceof com.fr.report.cellElement.CheckBoxCellEditorDef)" +
            " {" +
            "   this.cardLayout.show(this.editorPropertyPanel, \"CCRDict\");" +
            "   this.currentEditorDefinePane = this.ccrDictEditorDefinePane;" +
            " }" +
            " else if (($1) instanceof com.fr.report.cellElement.ComboBoxCellEditorDef)" +
            " {" +
            "   this.cardLayout.show(this.editorPropertyPanel, \"CCRDict\");" +
            "   this.currentEditorDefinePane = this.ccrDictEditorDefinePane;" +
            " }" +
            " else if (($1) instanceof com.fr.report.cellElement.FileCellEditorDef)" +
            " {" +
            "   this.cardLayout.show(this.editorPropertyPanel, \"None\");" +
            "   this.currentEditorDefinePane = this.noneEditorDefinePane;" +
            " }" +
            "}" +
            "else" +
            "{" +
            "  this.cardLayout.show(this.editorPropertyPanel, \"None\");" +
            "  this.currentEditorDefinePane = this.noneEditorDefinePane;" +
            "}" +
            "this.allowBlankCheckBox.setSelected(((com.fr.report.cellElement.AbstractCellEditorDef)($1)).isAllowBlank());" +
            "if (this.currentEditorDefinePane instanceof org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane){" +
            "org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane deptEditorDefinePane = (org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane)this.currentEditorDefinePane;" +
            "deptEditorDefinePane.populate(($1));" +//localCellEditorDef =
            "}" +
            " else if (this.currentEditorDefinePane instanceof org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane){" +
            "org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane codeEditorDefinePane = (org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane)this.currentEditorDefinePane;" +
            "codeEditorDefinePane.populate(($1));" +//localCellEditorDef =
            "}" +
            "else {" +
            "com.fr.design.format.core.CellEditorDefPane.EditorDefinePane editorDefinePane = (com.fr.design.format.core.CellEditorDefPane.EditorDefinePane) this.currentEditorDefinePane;" +
            "editorDefinePane.populate(($1));" +//localCellEditorDef=
            "}" +
            //"this.currentEditorDefinePane.populate(($1));" +
            "}");
            
            
            mname = "initComponents";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{" +
            " org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.cellEditorDefPane=this;" +
            "setLayout(new java.awt.BorderLayout());" +
            "javax.swing.JPanel localJPanel = new javax.swing.JPanel();" +
            "add(localJPanel, \"North\");" +
            "localJPanel.setLayout(new java.awt.FlowLayout(0));" +
            "localJPanel.add(new javax.swing.JLabel(com.fr.base.Inter.getLocText(\"Type\") + \":\"));" +
            "org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.createEditorTypeComboBox2();" +
            "localJPanel.add(org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2);" +
            //                    "for (int i = 0; i < 5; i++){}" +
            //    "java.awt.event.ActionListener al = new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent ae){System.out.println(\"\");}};" +
            //     "org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.addActionListener(" +
            //                                    "new java.awt.event.ActionListener(){public void actionPerformed(java.awt.event.ActionEvent paramActionEvent) " +
            //    "{" +
            //"com.fr.report.cellElement.CellEditorDef localCellEditorDef = org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.getCellEditorDef();" +
            //" CellEditorDefPane.this.refreshEditorPropertyPanel(localCellEditorDef);" +
            //                    "}}" +
            //                    ");" +
            //    "this.editorTypeComboBox = new EditorTypeComboBox();" +
            //    "localJPanel.add(this.editorTypeComboBox);" +
            //    "this.editorTypeComboBox.addActionListener(this.eidtorTypeActionListener);" +
            "this.allowBlankCheckBox = new javax.swing.JCheckBox(\"多选\", false);" +
            "localJPanel.add(this.allowBlankCheckBox);" +
            "this.editorPropertyPanel = new javax.swing.JPanel();" +
            "add(this.editorPropertyPanel, \"Center\");" +
            "this.cardLayout = new java.awt.CardLayout();" +
            "this.editorPropertyPanel.setLayout(this.cardLayout);" +
            "this.editorPropertyPanel.setBorder(javax.swing.BorderFactory.createTitledBorder(com.fr.base.Inter.getLocText(\"Property\")));" +
            "this.noneEditorDefinePane = new com.fr.design.format.core.CellEditorDefPane.NoneEditorDefinePane();" +
            "this.textEditorDefinePane = new com.fr.design.format.core.CellEditorDefPane.TextEditorDefinePane();" +
            "this.numberEditorDefinePane = new com.fr.design.format.core.CellEditorDefPane.NumberEditorDefinePane();" +
            "this.dateEditorDefinePane = new com.fr.design.format.core.CellEditorDefPane.DateEditorDefinePane();" +
            "this.dateTimeEditorDefinePane = new com.fr.design.format.core.CellEditorDefPane.DateTimeEditorDefinePane();" +
            "this.ccrDictEditorDefinePane = new com.fr.design.format.core.CellEditorDefPane.DictEditorDefinePane();" +
            "this.editorPropertyPanel.add(\"None\", this.noneEditorDefinePane);" +
            "this.editorPropertyPanel.add(\"Text\", this.textEditorDefinePane);" +
            "this.editorPropertyPanel.add(\"Number\", this.numberEditorDefinePane);" +
            "this.editorPropertyPanel.add(\"Date\", this.dateEditorDefinePane);" +
            "this.editorPropertyPanel.add(\"DateTime\", this.dateTimeEditorDefinePane);" +
            "this.editorPropertyPanel.add(\"CCRDict\", this.ccrDictEditorDefinePane);" +
            "this.editorPropertyPanel.add(\"Dept\", org.jhrcore.client.fr.cellEditorDef.DeptEditorDefinePane.getDeptEditorDefinePane());" +
            "this.editorPropertyPanel.add(\"Code\", org.jhrcore.client.fr.cellEditorDef.CodeEditorDefinePane.getCodeEditorDefinePane());" +
            "}");
            mname = "populate";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{" +
            "if (($1) == null)" +
            "  ($1) = new com.fr.report.cellElement.TextCellEditorDef();" +
            "this.currentEditorDefinePane = null;" +
            "if (($1) instanceof com.fr.report.cellElement.TextCellEditorDef)" +
            "  org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.setSelectedIndex(0);" +
            "else if (($1) instanceof com.fr.report.cellElement.CheckBoxCellEditorDef)" +
            "  org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.setSelectedIndex(5);" +
            " else if (($1) instanceof com.fr.report.cellElement.ComboBoxCellEditorDef)" +
            "  org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.setSelectedIndex(1);" +
            "else if (($1) instanceof com.fr.report.cellElement.NumberCellEditorDef)" +
            " org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.setSelectedIndex(2);" +
            "else if (($1) instanceof com.fr.report.cellElement.DateTimeCellEditorDef)" +
            " org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.setSelectedIndex(4);" +
            "else if (($1) instanceof com.fr.report.cellElement.DateCellEditorDef)" +
            " org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.setSelectedIndex(3);" +
            "else if (($1) instanceof com.fr.report.cellElement.FileCellEditorDef)" +
            " org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.setSelectedIndex(6);" +
            "else if (($1) instanceof org.jhrcore.client.fr.cellEditorDef.DeptCodeCellEditorDef)" +
            " org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.setSelectedIndex(7);" +
            "else if (($1) instanceof org.jhrcore.client.fr.cellEditorDef.CodeCellEditorDef)" +
            " org.jhrcore.client.fr.cellEditorDef.EditorTypeComboBox2.editorTypeComboBox2.setSelectedIndex(8);" +
            "this.allowBlankCheckBox.setSelected(((com.fr.report.cellElement.AbstractCellEditorDef)($1)).isAllowBlank());" +
            "refreshEditorPropertyPanel(($1));" +
            "}");
            ctClass.toClass();
            
            
            ctClass = ClassPool.getDefault().get("com.fr.cell.ReportPane");
            mname = "fireReportDataChangeListener";
            mold = ctClass.getDeclaredMethod(mname);
            
            mold.setBody("{" +
            "Object aobj[] = reportDataChangeListenerList.getListenerList();" +
            "for(int i = aobj.length - 2; i >= 0; i -= 2){" +
            "if(reportDataChangeEvent == null) reportDataChangeEvent = new com.fr.cell.event.ReportDataChangeEvent(this);" +
            "((com.fr.cell.event.ReportDataChangeListener)aobj[i + 1]).reportDataChanged(reportDataChangeEvent);}" +
            "}");
            ctClass.toClass();
             */


            /*
            ctClass = ClassPool.getDefault().get("com.fr.design.actions.format.style.AlignmentCenterAction");
            mname = "executeStyle";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{($1) = ($1).deriveHorizontalAlignment(0);" +
            "return ($1);}");
            ctClass.toClass();
            
            ctClass = ClassPool.getDefault().get("com.fr.design.actions.format.style.AlignmentLeftAction");
            mname = "executeStyle";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{($1) = ($1).deriveHorizontalAlignment(2);" +
            "return ($1);}");
            ctClass.toClass();
            
            ctClass = ClassPool.getDefault().get("com.fr.design.actions.format.style.AlignmentRightAction");
            mname = "executeStyle";
            mold = ctClass.getDeclaredMethod(mname);
            mold.setBody("{($1) = ($1).deriveHorizontalAlignment(4);" +
            "return ($1);}");
            ctClass.toClass();
             */
            /*
            ctClass = ClassPool.getDefault().get("com.fr.design.chart.basis.ChartDataDetailsPane");
            mname = "populate";
            mold = ctClass.getDeclaredMethod(mname, new CtClass[]{ClassPool.getDefault().get("com.fr.data.core.define.FilterDefinition")});
            //System.out.println();
            //mold.setBody("{return;}");
            
            
            mold.setBody("{" +
            "javax.swing.DefaultComboBoxModel localDefaultComboBoxModel;" +
            "Object localObject2;" +
            "int k;" +
            "if (($1) instanceof com.fr.data.core.define.ChartDataDefinition)" +
            "this.chartDataDefinition = ((com.fr.data.core.define.ChartDataDefinition)($1));" +
            "else" +
            " this.chartDataDefinition = new com.fr.data.core.define.ChartDataDefinition();" +
            "com.fr.data.TableData localTableData1 = this.chartDataDefinition.getTableData();" +
            " javax.swing.DefaultListModel localDefaultListModel = (javax.swing.DefaultListModel)this.availableColumnList.getModel();" +
            "localDefaultListModel.removeAllElements();" +
            //"DesignerFrame localDesignerFrame = DesignerContext.getDesignerFrame();"+
            //"JReportInternalFrame localJReportInternalFrame = DesignUtils.getReportFrame(localDesignerFrame);"+
            //" if (localJReportInternalFrame == null)"+
            //" return;"+
            //"if (localJReportInternalFrame.getReportPane() != null)"+
            //"{"+
            "com.fr.cell.ReportPane localObject1 = org.jhrcore.client.report.ReportPanel.getJWorkSheet();" +
            " if (((com.fr.cell.ReportPane)localObject1).getReport() != null)" +
            "{" +
            " com.fr.report.Report localReport = ((com.fr.cell.ReportPane)localObject1).getReport();" +
            "java.util.Map localMap = null;" +
            "localDefaultComboBoxModel = (javax.swing.DefaultComboBoxModel)this.tableNameComboBox.getModel();" +
            " localDefaultComboBoxModel.removeAllElements();" +
            "if (localReport != null)" +
            "{" +
            " localObject2 = new com.fr.design.data.AllTableDataOP2(localReport);" +
            "localMap = ((com.fr.design.data.TableDataOP)localObject2).init();" +
            "java.util.Iterator localIterator = localMap.entrySet().iterator();" +
            "while (localIterator.hasNext())" +
            "{" +
            "java.util.Map.Entry localEntry = (java.util.Map.Entry)localIterator.next();" +
            "String str = (String)localEntry.getKey();" +
            "com.fr.data.TableData localTableData2 = (com.fr.data.TableData)localEntry.getValue();" +
            "localDefaultComboBoxModel.addElement(new com.fr.design.gui.NameObject(str, localTableData2));" +
            "}" +
            "}" +
            "calculateColumnNameList();" +
            "if (localTableData1 instanceof com.fr.data.impl.NameTableData)" +
            "{" +
            "localObject2 = ((com.fr.data.impl.NameTableData)localTableData1).getName();" +
            "for (k = 0; k < localDefaultComboBoxModel.getSize(); ++k)" +
            "if ((localDefaultComboBoxModel.getElementAt(k) instanceof com.fr.design.gui.NameObject) && (((com.fr.design.gui.NameObject)localDefaultComboBoxModel.getElementAt(k)).getName().equals(localObject2)))" +
            "this.tableNameComboBox.setSelectedIndex(k);" +
            "}" +
            "}" +
            //"}"+
            "localDefaultListModel = (javax.swing.DefaultListModel)this.onChangeColumnList.getModel();" +
            "localDefaultListModel.clear();" +
            "Object[] localObject1 = this.chartDataDefinition.getOnChangeColumnArray();" +
            "if (localObject1 != null)" +
            "{" +
            "for (int i = 0; i < localObject1.length; ++i)" +
            "localDefaultListModel.addElement(localObject1[i]);" +
            "if (localDefaultListModel.getSize() > 0)" +
            "this.onChangeColumnList.setSelectedIndex(0);" +
            "}" +
            "localDefaultListModel = (javax.swing.DefaultListModel)this.summaryColumnList.getModel();" +
            "localDefaultListModel.clear();" +
            " com.fr.data.core.define.ChartSummaryColumn[] arrayOfChartSummaryColumn = this.chartDataDefinition.getChartSummaryColumnArray();" +
            "if (arrayOfChartSummaryColumn != null)" +
            "{" +
            "for (int j = 0; j < arrayOfChartSummaryColumn.length; ++j)" +
            "localDefaultListModel.addElement(arrayOfChartSummaryColumn[j]);" +
            "if (localDefaultListModel.getSize() > 0)" +
            " this.summaryColumnList.setSelectedIndex(0);" +
            "}" +
            "}");
            ctClass.toClass();
             */
            /*
            File file = new File(BaseCoreUtils.pathJoin(new String[] {
            path, "resources", s
            }));
            System.out.println("AAAAAAAAAA:" + file);
            ctClass = ClassPool.getDefault().get("com.fr.base.dav.LocalEnv");
            mname = "writeResource";
            mold = ctClass.getDeclaredMethod(mname);
            System.out.println("File file2 = new File(BaseCoreUtils.pathJoin(new String[] { path, " +
            "\"resources\", s}));" + "System.out.println(\"AAAAAAAAAA:\" + file2.getAbsolutePath());" + mold);
            mold.insertBefore("{File file2 = new File(BaseCoreUtils.pathJoin(new String[] { path, " +
            "\"resources\", s}));" + "System.out.println(\"AAAAAAAAAA:\" + file2.getAbsolutePath());}");
            ctClass.toClass();*/
        } catch (NotFoundException ex) {
            Logger.getLogger(AppHrClient.class.getName()).log(Level.SEVERE, null, ex);
        } catch (CannotCompileException ex) {
            Logger.getLogger(AppHrClient.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    // 在参数值前后增加"'"
    // 注意修改的语句：
    // stringbuffer.append(obj == null ? \"''\" : \"'\" + obj + \"'\");
    private static void changeDBTableData() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.data.impl.DBTableData");
        String mname = "getNewQuery";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{System.out.println(\"VVVVVVVVVV:\" + parameters);"
                + "return org.jhrcore.client.report.ReportPanel.getNewQuery(getQuery(), parameters);}");


        //       " FRContext.getLogger().log(Level.INFO, \"Query SQL of DBTableData: \n\" + stringbuffer.toString());"+
//                "return \"select * from A01 where a0190='000002'\";}");


//        mname = "initConnectionAndResult";
//        mold = ctClass.getDeclaredMethod(mname);
//        mold.insertBefore("System.out.println(\"MKIHTGFRD11:\");");
//        mold.insertAfter("resultSet.next();" +
//                "System.out.println(\"AAAAAAAAAASSSSCCCFFFFNN11:\" + resultSet.getRow());");

        mname = "setQuery";
        mold = ctClass.getDeclaredMethod(mname);
//        mold.insertBefore("System.out.println(\"JJJJJJJJJJJKK:\" + ($1));");

        mname = "getNewQuery2";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{"
                + "String s = getNewQuery();"
                + "s = org.jhrcore.client.FR_Rebuilder.parseSQL(s);"
                + "System.out.println(\"\");"
                + "return s;"
                + "}");
        /*        mname = "checkInColumn";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{" +
        "if(columnNameArray != null)" +
        "return;" +
        "Object aobj[] = {" +
        "    new String[0], new int[0], new String[0]" +
        "};" +
        "String s = getNewQuery();" +
        "s = org.jhrcore.client.FR_Rebuilder.parseSQL(s);" +
        "java.sql.Connection connection1 = null;" +
        "try" +
        "{" +
        "    connection1 = database.createConnection();" +
        "    aobj = com.fr.data.core.db.DBUtils.checkInColumn(connection1, s);" +
        "}" +
        "catch(Exception exception)" +
        "{" +
        "    throw new com.fr.data.TableDataException(\"Query: \" + s + \"\\n \" + exception.getMessage(), exception);" +
        "}" +
        "finally" +
        "{" +
        "    if(connection1 != null)" +
        "        try" +
        "        {" +
        "            connection1.close();" +
        "        }" +
        "        catch(java.sql.SQLException sqlexception)" +
        "        {" +
        "            sqlexception.printStackTrace();" +
        "        }" +
        "}" +
        "columnNameArray = (String[])aobj[0];" +
        "columnTypeArray = (int[])aobj[1];" +
        "columnTypeNameArray = (String[])aobj[2];" +
        "System.out.println(\"SSQQLL:\" + s);" +
        "}");
         */
        ctClass.toClass();
    }

    private static void changeDataUtils() throws NotFoundException, CannotCompileException {
        if (1 == 1) {
            return;
        }
        CtClass ctClass = ClassPool.getDefault().get("com.fr.data.core.DataUtils");
        String mname = "getResultSetObject";
        CtMethod[] molds = ctClass.getDeclaredMethods();
        for (CtMethod mold : molds) {
            if (!mold.getName().equals(mname) || mold.getParameterTypes().length != 5) {
                continue;
            }
            System.out.println("JJJJJJJJJJJ:" + mold.getParameterTypes().length);
            mold.insertBefore(
                    "String tmp_01 = org.jhrcore.client.report.FtReportUtil.getCodeType(\"@\" + ($3));"
                    + "if (tmp_01 != null){"
                    + "    Object obj2 = getResultSetObject($1, $2, $3);"
                    + " if (obj2 != null)"
                    + "    return org.jhrcore.comm.CodeManager.getCodeManager().getCodeNameBy(tmp_01, obj2.toString());"
                    + "}" //                "System.out.println(\"RRRRRRRRRRRRRRRR999999999999:\" + tmp_01);"// + tmp
                    );
        }
        ctClass.toClass();
        System.out.println("KKKKKKKKKKKKKKKKKKKKKKKKUUUUUUUUUUUUUUUIIIIII");
    }

    private static void changeJReportInternalFrame() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.mainframe.JReportInternalFrame");
        for (CtConstructor constructorct : ctClass.getConstructors()) {
            //constructorct.insertAfter("this.reportPane.removeReportDataChangeListener(this.reportDataChangeListener);");
            constructorct.setBody("{"
                    + "this.reportPane = ($1); }");
        }
        ctClass.toClass();
    }

    private static void changeTableDataTreePane() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.data.TableDataTreePane");
        for (CtConstructor constructorct : ctClass.getConstructors()) {
            constructorct.insertAfter("this.tableDataTree.setSortable(false);");
        }


        String mname = "after_new_DBTableData";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        String s = "{"
                + "org.jhrcore.client.report.ReportPanel.after_add_dbtabledata(this, ($1));"
                + "}";
        mold.setBody(s);
        System.out.println(">>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>>");
        System.out.println(s);
        System.out.println("<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<<");
        ctClass.toClass();
    }

    private static void changeDBTableDataPane() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.data.source.DBTableDataPane");
        for (CtConstructor constructorct : ctClass.getConstructors()) {
            //= ctClass.getConstructor("");
            constructorct.insertAfter("String str1 = this.connectionComboBox.getSelectedItem();"
                    + "if (str1 instanceof String)"
                    + "{"
                    + " String str2 = (String)str1;"
                    + //          " this.tableProcedueTree.populate(new String[] { str2 });" + 
                    "}"
                    //        + "this.dataParaTabbedPane.remove(this.dbParameterViewPane);"
                    + "this.pnl_param.add(new org.jhrcore.client.report.ParamPickerPanel(this.sqlTextPane), java.awt.BorderLayout.CENTER);");
        }

        String mname = "createToolBar";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{"
                + "com.fr.design.config.ToolBarDef localToolBarDef = new com.fr.design.config.ToolBarDef();"
                + //    "this.previewAction = new com.fr.design.data.source.DBTableDataPane.PreviewAction();" +
                //    "localToolBarDef.addShortCut(new org.jhrcore.client.report.FrPreviewAction(this));
                //                "previewAction = new com.fr.design.data.source.DBTableDataPane.PreviewAction();" +
                "localToolBarDef.addShortCut(previewAction);"
                + "localToolBarDef.addShortCut(new com.fr.design.config.SeparatorDef());"
                + "localToolBarDef.addShortCut(this.sqlTextPane.getUndoAction());"
                + "localToolBarDef.addShortCut(this.sqlTextPane.getRedoAction());"
                + "localToolBarDef.addShortCut(new com.fr.design.config.SeparatorDef());"
                + "localToolBarDef.addShortCut(this.sqlTextPane.getCutAction());"
                + "localToolBarDef.addShortCut(this.sqlTextPane.getCopyAction());"
                + "localToolBarDef.addShortCut(this.sqlTextPane.getPasteAction());"
                + "localToolBarDef.addShortCut(new com.fr.design.config.SeparatorDef());"
                + //    "this.sqlWizardAction = new SQLWizardAction();" +
                "localToolBarDef.addShortCut(new org.jhrcore.client.report.FrSQLBuilderWizardAction(this));"
                + "javax.swing.JToolBar localJToolBar = com.fr.design.config.ToolBarDef.createJToolBar();"
                + "localToolBarDef.updateToolBar(localJToolBar);"
                + "return localJToolBar;"
                + "}");

        mname = "getParamList";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{"
                + "return org.jhrcore.client.report.ParamPickerPanel.getParamPickerPanel().getLocalArrayList();"
                + "}");

        ctClass.toClass();
    }

    private static void changeGUICoreUtils() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.cell.core.GUICoreUtils");
        String mname = "setWindowCenter";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore("($1) = org.jhrcore.client.BaseMainFrame.getBaseMainFrame();" // + "if (($1) == null || ($2) == null) return;"
                );
        ctClass.toClass();
    }

    private static void changeItemEditableComboBoxPanel() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.gui.xpane.ItemEditableComboBoxPanel");
        String mname = "refreshItems";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{"
                + //"System.out.println(\"AAAAAAAAAAAAAAAAAAADDDFF\");" +
                "Object localObject = this.itemComboBox.getSelectedItem();"
                + " javax.swing.DefaultComboBoxModel localDefaultComboBoxModel = (javax.swing.DefaultComboBoxModel)this.itemComboBox.getModel();"
                + " localDefaultComboBoxModel.removeAllElements();"
                + //   " localDefaultComboBoxModel.addElement(EMPTY);"+
                " java.util.Iterator localIterator = items();"
                + " while (localIterator.hasNext())"
                + "   localDefaultComboBoxModel.addElement(localIterator.next());"
                + " int i = localDefaultComboBoxModel.getIndexOf(localObject);"
                + " if (i < 0)"
                + "   i = 0;"
                + " this.itemComboBox.setSelectedIndex(i);}");
        ctClass.toClass();
    }

    // 判断sql是否符合权限要求
    public static void checkSQLRight(TableDataCardPane centralPanel) throws Exception {
        if (UserContext.isSA) {
            return;
        }
        String sql = centralPanel.dbPanel.sqlTextPane.getText();
        checkSQLRight(sql);
//        int ind1 = tmp_sql.indexOf(" FROM ");
//        int ind2 = tmp_sql.indexOf(" WHERE ");
//        if (ind2 < 0) ind2 = tmp_sql.length();
//        ind1 = Math.min(ind1, ind2);
//        String tmp_sql_from = tmp_sql.substring(ind1 + 6, ind2);
//        if (tmp_sql_from.length() > 0){
//            List list = CommUtil.selectSQL("" +
//                "select entityName from tabname ed " +
//                "where ed.entity_key not in " +
//                "(select rc.entity_key from RoleEntity rc where rc.role_key='" + UserContext.role_id + "' and rc.view_flag = 1)" );
//            for (Object tmp_obj : list){
//                System.out.println("tmp_obj:" + tmp_obj);
//                String tmp = (String)tmp_obj;
//
//                if (tmp_sql_from.contains(" " + tmp.toUpperCase()) || tmp_sql_from.contains("," + tmp.toUpperCase())){
//                    //如果不符合权限要求，抛出异常
//                    throw new Exception("非系统管理员，报表语句中不能包含未授权访问的表：" + tmp + "\n" + sql);
//                }
//            }
//        }
//        if (tmp_sql_from.length() > 0){
//            List list = CommUtil.selectSQL(
//                    //t.entityname+'.'+s.field_name
//                    //t.entityname+'.'+
//                    "select s.field_name from tabname t,system s where s.entity_key=t.entity_key and s.visible=1 and not exists(select 1 from rolefield rf where (rf.fun_flag=1 or rf.fun_flag=2) and "
//                    + "rf.field_name = t.entityname+'.'+s.field_name and rf.role_key='"+UserContext.role_id+"')"
//                );
//            String tmp_sql2 = tmp_sql;
//            int ind = tmp_sql.lastIndexOf(" FROM ");
//            if (ind > -1){
//                tmp_sql2 = tmp_sql.substring(0, ind);
//            }
//            String[] tmp_tabs = tmp_sql_from.replaceAll(" AS ", " ").split(",");
//            for (String tmp_tab : tmp_tabs){
//                String[] tmp_tabs2 = tmp_tab.split(" ");
//                for (int i = 0; i < tmp_tabs2.length; i++){
//                    tmp_tabs2[i] = tmp_tabs2[i].trim();
//                }
//                if (tmp_tabs2.length >= 2){
//                    tmp_sql2 = tmp_sql2.replaceAll(" " + tmp_tabs2[1] + ".", " " + tmp_tabs2[0] + ".")
//                            .replaceAll("," + tmp_tabs2[1] + ".", "," + tmp_tabs2[0] + ".")
//                            .replaceAll("+" + tmp_tabs2[1] + ".", "+" + tmp_tabs2[0] + ".")
//                            .replaceAll("|" + tmp_tabs2[1] + ".", "|" + tmp_tabs2[0] + ".");
//                }
//            }
//            for (Object tmp_obj : list){
//                String tmp = (String)tmp_obj;
//                if (tmp_sql2.contains("" + tmp.toUpperCase())
//                         || tmp_sql2.contains("+" + tmp.toUpperCase())
//                        || tmp_sql2.contains("|" + tmp.toUpperCase())
//                        || tmp_sql2.contains("(" + tmp.toUpperCase())
//                        || tmp_sql2.contains("," + tmp.toUpperCase())){
//                    //如果不符合权限要求，抛出异常
//                    throw new Exception("非系统管理员，报表语句中不能包含未授权访问的字段：" + tmp + "\n" + sql);
//                }
//            }
//        }
//        如果不符合权限要求，抛出异常
//        throw new Exception(sql);
    }

    public static void checkSQLRight(String sql) throws Exception {
        if (UserContext.isSA) {
            return;
        }
        String tmp_sql = sql.toUpperCase().replaceAll("\n", " ");
        String invalid_str = "USE/CREATE/DROP/ALTER/REFRESH/INTO/"
                + "GRANT/ROLLBACK/COMMIT/BACKUP/DBCC/DENY/KILL/"
                + "RECONFIGURE/RESTORE/REVOKE/SHUTDOWN/EXEC/"
                + "EXECUTE/READTEXT/REFERENCES/SETUSER/UPDATETEXT/"
                + "WRITETEXT/TRANSACTION/SET/NOAUDIT/QUIT/DISCONNECT/"
                + "INSERT/DELETE/UPDATE/TRUNCATE"
                + "/CALL";                    // ORACLE
        for (String tmp : invalid_str.split("/")) {
            if (tmp_sql.contains(tmp + " ")) {
                //如果不符合权限要求，抛出异常
                throw new Exception("非系统管理员，报表语句中不能包含：" + tmp + "\n" + sql);
            }
        }
    }

    private static void changeNameTableDataDialog() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.data.source.NameTableDataDialog");
        String mname = "checkValid";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore("if(1 == 1){"
                + "org.jhrcore.client.FR_Rebuilder.checkSQLRight(centralPanel);"
                + " return;}");

        mname = "initComponents";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertAfter("setSize(new java.awt.Dimension(this.getToolkit().getScreenSize().width - 80, this.getToolkit().getScreenSize().height - 80));"
                + "com.fr.cell.GUIUtils.centerWindow(this);");
        ctClass.toClass();
    }

    private static void changeParameter() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.report.parameter.Parameter");
        String mname = "writeXML";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        //mold.insertBefore(
        //    "System.out.println(\"DDDDD:\" +  com.fr.base.core.BaseXMLUtils.xmlAttrEncode(displayName));");
        mold.setBody("{($1).println(\"<Attributes name=\\\"\" + com.fr.base.core.BaseXMLUtils.xmlAttrEncode(getName()) + \"\\\"\");"
                + /*                "if(displayName != null){" +
                "try{" +
                "displayName = new String(displayName.getBytes(), \"GBK\");" +
                " } catch(Exception e){" +
                "}" +
                 */ //        "    ($1).println(\" displayName=\\\"\" + com.fr.base.core.BaseXMLUtils.xmlAttrEncode(displayName) + \"\\\"\");"+
                "    ($1).println(\"displayName=\\\"\" + displayName + \"\\\"\");"
                + //                "}" +
                "System.out.println(\"DDDDD:\" + displayName);"
                + "($1).println(\" type=\\\"\" + getType() + \"\\\"\");"
                + "($1).println(\" shown=\\\"\" + isShown() + \"\\\" />\");"
                + "com.fr.report.io.xml.ReportXMLUtils.writeObject(($1), getValue());"
                + "if(cellEditorDef != null)"
                + "    com.fr.report.io.xml.ReportXMLUtils.writeCellEditorDef(($1), getCellEditorDef());}");

        mname = "readXML";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{String s;"
                + "if((s = com.fr.base.BaseUtils.getAttrValue(($1), \"name\")) != null)"
                + "    setName(s);"
                + "if((s = com.fr.base.BaseUtils.getAttrValue(($1), \"type\")) != null)"
                + "    setType(Integer.parseInt(s));"
                + "org.w3c.dom.NodeList nodelist = ($1).getChildNodes();"
                + "for(int i = 0; i < nodelist.getLength(); i++)"
                + "{"
                + "    org.w3c.dom.Node node = nodelist.item(i);"
                + "    if(!(node instanceof org.w3c.dom.Element))"
                + "        continue;"
                + "    org.w3c.dom.Element element1 = (org.w3c.dom.Element)node;"
                + "    String s3 = element1.getNodeName();"
                + "    if(\"Object\".equals(s3) || \"O\".equals(s3))"
                + "    {"
                + "        setValue(com.fr.report.io.xml.ReportXMLUtils.readObject(element1));"
                + "        continue;"
                + "    }"
                + "    if(\"Attributes\".equals(s3))"
                + "    {"
                + "        String s1;"
                + "        if((s1 = com.fr.base.BaseUtils.getAttrValue(element1, \"name\")) != null)"
                + "            setName(s1);"
                + "        if((s1 = com.fr.base.BaseUtils.getAttrValue(element1, \"nameToEndUser\")) != null)"
                + "            setDisplayName(s1);"
                + "        if((s1 = com.fr.base.BaseUtils.getAttrValue(element1, \"displayName\")) != null){"
                + //                "try{" +
                //                "s1 = new String(s1.getBytes(\"utf8\"), \"GBK\");" +
                //                " } catch(Exception e){" +
                //                "}" +
                "System.out.println(\"DDDDD2:\" + s1);"
                + "            setDisplayName(s1);"
                + "}"
                + "        if((s1 = com.fr.base.BaseUtils.getAttrValue(element1, \"type\")) != null)"
                + "            setType(Integer.parseInt(s1));"
                + "        if((s1 = com.fr.base.BaseUtils.getAttrValue(element1, \"pop\")) != null)"
                + "            setShown(Boolean.valueOf(s1) == Boolean.TRUE);"
                + "        if((s1 = com.fr.base.BaseUtils.getAttrValue(element1, \"shown\")) != null)"
                + "            setShown(Boolean.valueOf(s1) == Boolean.TRUE);"
                + "        continue;"
                + "    }"
                + "    if(s3.equals(\"CellEditorDef\"))"
                + "    {"
                + "        setCellEditorDef(com.fr.report.io.xml.ReportXMLUtils.readCellEditorDef(element1));"
                + "        continue;"
                + "    }"
                + "    String s2;"
                + "    if((s2 = com.fr.base.BaseUtils.getElementValue(element1)) == null)"
                + "        continue;"
                + "    if(s3.equals(\"String\"))"
                + "    {"
                + "        setValue(s2);"
                + "        continue;"
                + "    }"
                + "    if(s3.equals(\"Boolean\"))"
                + "    {"
                + "        setValue(Boolean.valueOf(s2));"
                + "        continue;"
                + "    }"
                + "    if(s3.equals(\"Integer\"))"
                + "    {"
                + "        setValue(new Integer(s2));"
                + "        continue;"
                + "    }"
                + "    if(s3.equals(\"Float\"))"
                + "    {"
                + "        setValue(new Float(s2));"
                + "        continue;"
                + "    }"
                + "    if(s3.equals(\"Double\"))"
                + "    {"
                + "        setValue(new Double(s2));"
                + "        continue;"
                + "    }"
                + "    if(s3.equals(\"Date\"))"
                + "        setValue(new java.util.Date(Long.parseLong(s2)));"
                + "}"
                + "}");
        ctClass.toClass();
    }

    private static void changeParameterPane() throws NotFoundException, CannotCompileException {
        //if (1==1) return;
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.parameter.ParameterPane");
        String mname = "initComponents";
        CtMethod mold = ctClass.getDeclaredMethod(mname);

        String s = "{"
                + "com.jgoodies.forms.layout.FormLayout layout = new com.jgoodies.forms.layout.FormLayout("
                + "	\"r:p, 4dlu, 200dlu\", " +// 3columns
                "	\"p,   4dlu,  p,   4dlu,  p,   4dlu,  p,   4dlu,  p,   4dlu,  p,   4dlu,  p\" " +// 6rows
                "	);"
                + "com.jgoodies.forms.builder.PanelBuilder builder = new com.jgoodies.forms.builder.PanelBuilder(layout);"
                + "com.jgoodies.forms.layout.CellConstraints cc = new com.jgoodies.forms.layout.CellConstraints();"
                + "builder.add(new javax.swing.JLabel(\"名称：\"), cc.xy(1,1));"
                + "builder.add(nameTextField, cc.xy(3,1));"
                + "builder.add(new javax.swing.JLabel(\"类型：\"), cc.xy(1,3));"
                + "builder.add(cmb_param_type, cc.xy(3,3));"
                + "builder.add(new javax.swing.JLabel(\"多选：\"), cc.xy(1,5));"
                + "builder.add(cb_multi, cc.xy(3,5));"
                + "builder.add(new javax.swing.JLabel(\"默认值：\"), cc.xy(1,7));"
                + "builder.add(valueTextField, cc.xy(3,7));"
                + "builder.add(new javax.swing.JLabel(\"值类型：\"), cc.xy(1,9));"
                + "builder.add(cmb_param_type2, cc.xy(3,9));"
                + "builder.add(new javax.swing.JLabel(\"编码类型：\"), cc.xy(1,11));"
                + "builder.add(cmb_param_type3, cc.xy(3,11));"
                + "builder.add(new javax.swing.JLabel(\"格式：\"), cc.xy(1,13));"
                + "builder.add(formatTextField, cc.xy(3,13));"
                + "setLayout(new java.awt.BorderLayout());"
                + "add(java.awt.BorderLayout.CENTER, builder.getPanel());"
                + "java.util.List list = org.jhrcore.client.CommUtil.fetchEntities(\"select c.code_type from Code c where c.code_level=1 and c.used=1\");"
                + "org.jdesktop.swingbinding.SwingBindings.createJComboBoxBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, list, cmb_param_type3).bind();"
                + "}";

        System.out.println(s);
        mold.setBody(s);
        ctClass.toClass();
    }

    private static void changePreviewPane() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.view.PreviewPane");
        String mname = "print";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        mold.insertAfter("for(int j = 0; j < com.fr.base.FRContext.getCurrentEnv().getTemplateParameters(currentTemplate).length; j++)"
                + "{System.out.println(\"ABCDEFG111:\" + com.fr.base.FRContext.getCurrentEnv().getTemplateParameters(currentTemplate)[j].getName());"
                + "System.out.println(\"ABCDEFG222:\" +  com.fr.base.FRContext.getCurrentEnv().getTemplateParameters(currentTemplate)[j].getValue());}");
        ctClass.toClass();
    }

    private static void changeReportExcute() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.report.core.BSHUtils");
        String mname = "executeScript";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{if(($1) == null)"
                + "return;"
                + "String s = ($1).getPaginateScript();"
                + "System.out.println(\"ABCDEFG:\" + s);"
                + "if(s != null && s.length() > 0)"
                + "   try"
                + "  {"
                + "     bsh.Interpreter interpreter = getInitBSHInterpreter();"
                + "     interpreter.set(\"curReport\", ($1));"
                + "     java.util.Map.Entry entry;"
                + "     for(java.util.Iterator iterator = ($2).entrySet().iterator(); iterator.hasNext(); interpreter.set(\"$\" + entry.getKey(), entry.getValue()))"
                + "         entry = (java.util.Map.Entry)iterator.next();"
                + "     interpreter.eval(s);"
                + "}"
                + " catch(Throwable throwable)"
                + " {"
                + "     com.fr.base.FRContext.getLogger().log(java.util.logging.Level.WARNING, throwable.getMessage(), throwable);"
                + " }}");
        ctClass.toClass();
    }

    private static void changeParameterInputDialog() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.dialog.core.ParameterInputDialog");
        String mname = "initComponents";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore(//.setBody("{" +
                "parameters = ($1);"
                + "super.initComponents();"
                + "javax.swing.JPanel jpanel = (javax.swing.JPanel)getContentPane();"
                + "javax.swing.JPanel jpanel1 = new javax.swing.JPanel();"
                + "jpanel.add(jpanel1, \"Center\");"
                + "jpanel1.setLayout(new org.jhrcore.ui.VerticalSizableLayout());"
                + //                "jpanel1.setLayout(new javax.swing.BoxLayout(jpanel1, 1));" +
                //                "jpanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(com.fr.base.Inter.getLocText(\"Parameters\") + \":\"));" +
                //                "com.fr.dialog.core.ParameterInputDialog.FlowTableLayoutHelper flowtablelayouthelper = new com.fr.dialog.core.ParameterInputDialog.FlowTableLayoutHelper();" +
                "System.out.println(\"AAAAAAAAAAAAAAAAAAAAA1\");"
                + "int pre_height = 0;"
                + "if(parameters != null && parameters.length > 0)"
                + "{"
                + /*        "    java.util.Arrays.sort(parameters, new java.util.Comparator() {"+
                
                "        public int compare(Object obj, Object obj1)"+
                "       {"+
                "          com.fr.report.parameter.Parameter parameter1 = (com.fr.report.parameter.Parameter)obj;"+
                "          com.fr.report.parameter.Parameter parameter2 = (com.fr.report.parameter.Parameter)obj1;"+
                "          return com.fr.base.core.ComparatorUtils.compare(parameter1.getName(), parameter2.getName());"+
                "      }"+
                
                "   });"+
                
                 */ "   for(int i = 0; i < parameters.length; i++)"
                + "   {"
                + "System.out.println(\"AAAAAAAAAAAAAAAAAAAAA2\");"
                + "       com.fr.report.parameter.Parameter parameter = parameters[i];"
                + "       boolean bexists = false;"
                + "       for (int j = 0; j < i; j++){"
                + "           if (com.fr.base.core.ComparatorUtils.equals(parameter.getName(), parameters[j].getName())){"
                + "               bexists = true; break;"
                + "           }"
                + "       }"
                + "       if (bexists) continue;"
                + //                "       if(i > 0 && com.fr.base.core.ComparatorUtils.equals(parameter.getName(), parameters[i - 1].getName()))" +
                //                "           continue;" +
                "System.out.println(\"AAAAAAAAAAAAAAAAAAAAA3\");"
                + "       javax.swing.JTextField jtextfield = new javax.swing.JTextField();"
                + //         "       parameter.getCellEditorDef().createCellEditor().getCellEditorComponent(null, null);"+
                "       jtextfield.setText(\"\" + parameter.getValue());"
                + "       javax.swing.JPanel jpanel2 = new javax.swing.JPanel();"
                + "       jpanel2.setLayout(com.fr.cell.core.layout.LayoutFactory.createBorderLayout());"
                + "System.out.println(\"222ccDDD:\" + parameter);"
                + "jpanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(parameter.getName() + \":\"));"
                + //"System.out.println(\"222ccVVVVVVVVVVVVVVVVVVVDDDDT:\" + parameter.getCellEditorDef());" +
                //                "if (parameter.getCellEditorDef() == null) continue;" +
                "com.fr.cell.editor.AbstractCellEditor cellEditor_0 = org.jhrcore.client.report.FtReportUtil.createParameterEditor(parameter);"
                + //                "System.out.println(\"222bb:\" + parameter.getCellEditorDef().createCellEditor());" +
                "       jpanel2.add(cellEditor_0.getCellEditorComponent(null, null), \"Center\");"
                + "       jpanel2.setPreferredSize(new java.awt.Dimension(" + EditorPara.editorWidth + ", jpanel2.getPreferredSize().height));"
                + //                "       String s = parameter.getName();" +
                //                "       if(parameter.getDisplayName() != null && parameter.getDisplayName().trim().length() > 0)" +
                //                "           s = parameter.getDisplayName();" +
                //                "       jpanel1.add(flowtablelayouthelper.createLabelFlowPane(s + \":\", jpanel2));" +
                " jpanel1.add(jpanel2, parameter.isMulti()? \"L2\" : \"single_line\");"
                + "pre_height = pre_height + (parameter.isMulti()? " + EditorPara.multiHeight + " : " + EditorPara.singleHeight + ") + 5;"
                + "       nameHash.put(parameter.getName(), cellEditor_0);"
                + "   }"
                + "}"
                + //                "flowtablelayouthelper.adjustLabelWidth();" +
                "jpanel.add(createControlButtonPane(), \"South\");"
                + "       jpanel1.setPreferredSize(new java.awt.Dimension(" + (EditorPara.editorWidth + 10) + ", pre_height));"
                + "setModal(true);"
                + "setTitle(com.fr.base.Inter.getLocText(\"Parameters\"));"
                + "pack();"
                + "com.fr.cell.core.GUICoreUtils.setWindowCenter(getOwner(), this);"
                + //"setVisible(true);" +
                "");//}");

        mold = ctClass.getDeclaredMethod("update");
        mold.setBody("{"
                + " for(int i = 0; i < parameters.length; i++)"
                + "{"
                + "   com.fr.report.parameter.Parameter parameter = parameters[i];"
                + "    if(parameter == null)"
                + "        continue;"
                + "System.out.println(\"WWWWWWWWWWWWWWWWWWWWWWWWW0\" + parameter.getName());"
                + "    com.fr.cell.editor.CellEditor cellEditor_0 = (com.fr.cell.editor.CellEditor)nameHash.get(parameter.getName());"
                + "System.out.println(\"WWWWWWWWWWWWWWWWWWWWWWWWW1\" + cellEditor_0.getCellEditorValue());"
                + "    if(cellEditor_0 != null){"
                + //                "        if (cellEditor_0.getCellEditorValue() instanceof Object[])" +
                "            parameter.setValue(cellEditor_0.getCellEditorValue());"
                + //                "        else" +
                //                "        parameter.setValue(parameter.revertReality(cellEditor_0.getCellEditorValue() == null? \"\" : cellEditor_0.getCellEditorValue().toString()));" +
                "System.out.println(\"WWWWWWWWWWWWWWWWWWWWWWWWW2\" + parameter.getValue());"
                + "}"
                + "}"
                + "java.util.HashMap hashmap = new java.util.HashMap();"
                + "for(int i = 0; i < parameters.length; i++) "
                + "  hashmap.put(parameters[i].getName(), parameters[i].getValue());"
                + "return hashmap;"
                + "}");

        //mold.insertBefore("System.out.println(\"WWWWWWWWWWWWWWWWWWWWWWWWW\");");
        ctClass.toClass();
    }

    private static void changeTableDataCardPane() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.data.source.TableDataCardPane");
        String mname = "TableDataCardPane";
        //CtConstructor mold = ctClass.getConstructor(mname);
        for (CtConstructor mold : ctClass.getConstructors()) //CtMethod mold = ctClass.getDeclaredMethod(mname);
        {
            mold.insertAfter("this.tabbedPane.remove(this.selectMultiRolePane);"
                    + "this.tabbedPane.remove(this.semanticPane);");
        }
        ctClass.toClass();
    }

    private static void changeTextCellEditor() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.cell.editor.TextCellEditor");
        String mname = "ajustTextStyle";
        CtMethod mold = ctClass.getDeclaredMethod(mname);
        String s = "{if(($2) == null)"
                + "($2) = com.fr.base.Style.DEFAULT_STYLE;"
                + "int i = ($2).getHorizontalAlignment();"
                + "if(i == 2)"
                + "   textField.setHorizontalAlignment(2);"
                + "else "
                + "if(i == 0)"
                + "   textField.setHorizontalAlignment(0);"
                + "else "
                + "if(i == 4)"
                + "   textField.setHorizontalAlignment(4);"
                + "else "
                + "   textField.setHorizontalAlignment(2);"
                + "com.fr.base.FRFont frfont = ($2).getFRFont();"
                + "textField.setFont(new java.awt.Font(frfont.getFontName(), frfont.getStyle(), (int)((double)frfont.getSize())));" +// * ($1).getReportPane().getScale())));
                "textField.setForeground(($2).getFRFont().getForeground());"
                + "if(($2).getBackground() instanceof com.fr.base.background.ColorBackground)"
                + "   textField.setBackground(((com.fr.base.background.ColorBackground)($2).getBackground()).getColor());"
                + "else "
                + "   textField.setBackground(java.awt.Color.white);}";
        mold.setBody(s);
        ctClass.toClass();
    }

    private static void changeCellElement() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.report.AbstractCellElement");
        String mname;
        CtMethod mold;
        mname = "setValue";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{"
                + //                "System.out.println(\"BBBBBBBBBBBBBBBBBBBBBBBGGGGGGGGGGGGGG:\" + org.jhrcore.client.report.ReportPanel.enable_locate_dict);" +
                "Object old_val = value;"
                + "value = ($1);"
                //+ "System.out.println(\"VVVVVVVVVVVVVVVVVVVVVVVVVV:\" + value);"
                + //                "System.out.println(\"org.jhrcore.client.report.ReportPanel.enable_locate_dict:\" + org.jhrcore.client.report.ReportPanel.enable_locate_dict);" +
                "if (!org.jhrcore.client.report.ReportPanel.enable_locate_dict) {"
                //+ "System.out.println(\"enable_locate_dict is false\");"
                + "return;}"
                //+ "System.out.println(\"Value:\" + ($1));"
                + "if (!(($1) instanceof com.fr.report.cellElement.core.DSColumn)) {"
                //+ "System.out.println(\"is not DSColumn\");"
                + "return;}"
                + "if (old_val==value) {"
                //+ "System.out.println(\"old_val==value\");"
                + "return;}"
                + "com.fr.report.cellElement.core.DSColumn dsc = (com.fr.report.cellElement.core.DSColumn)($1);"
                + "com.fr.data.TableData tabledata = org.jhrcore.client.report.ReportPanel.getJWorkSheet().getEditingReport().getWorkBook().getTableData(dsc.getColumnName());"
                + "if (tabledata == null) {"
                //+ "System.out.println(\"tabledata is null\");"
                + "return;}"
                + "com.fr.dialog.NameObject no = new com.fr.dialog.NameObject(dsc.getColumnName(), tabledata);"
                + "com.fr.data.impl.TableDataDictionary tabledatadictionary = new com.fr.data.impl.TableDataDictionary();"
                + "tabledatadictionary.setKeyColumnIndex(0);"
                + "tabledatadictionary.setValueColumnIndex(1);"
                //+ "System.out.println(\"1111111111111111111111111111111111111C\");"
                + "if (no  instanceof com.fr.dialog.NameObject){"
                //+ "System.out.println(\"2222222222222222222222222222222222222C\");"
                + "tabledatadictionary.setTableData"
                + "(new com.fr.data.impl.NameTableData(((com.fr.dialog.NameObject)no).getName()));"
                + //"(tabledata);" +
                "}"
                + //"(new com.fr.data.impl.NameTableData(((com.fr.design.gui.NameObject)tabledata).getName()));" +
                //                "com.fr.data.Dictionary dict = com.fr.base.FRContext.getDatasourceManager().getDeprecatedDictionaryManager().getDictionary(dsc.getColumnName());" +
                //                "org.jhrcore.client.report.ReportPanel.getJWorkSheet().getReport().getDictionary(dsc.getColumnName());" +
                //                "if (dict == null) return;" +
                "setPresent(new com.fr.report.cellElement.DictPresent(tabledatadictionary));"
                + //                "System.out.println(\"setValueCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCCC:\" + dsc.getColumnName());" +
                "}");

        mname = "getValue";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{"
                + "if (getCellGUIAttr()!= null && getCellGUIAttr().isShowAsImage() && value != null && value instanceof java.lang.String){"
                + "value = org.jhrcore.util.TransferAccessory.downloadPicture(value.toString());"
                + "}"
                + "return value;"
                + "}");

        mname = "readXML";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertAfter("if (getOptionalAttributes() != null && getOptionalAttributes() != com.fr.report.cellElement.core.EM.EMPTY_ATTR"
                + ") getOptionalAttributes().put(com.fr.report.cellElement.core.CellElementAttribute.USER_TYPE1, com.fr.base.ColumnRow.valueOf(getColumn(), getRow()));");
        ctClass.toClass();
    }

    private static void changeReportPaneDropTarget() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.mainframe.drag.ReportPaneDropTarget");
        String mname;
        CtMethod mold;
        mname = "paintDropCellElement";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertAfter("if (($1) == (doubleArray.length - 1))" + //this.arrayList.size()
                " org.jhrcore.client.FR_Rebuilder.setColumnTitle(this.gridSelection.getFirstCellRectangle(), this.gridSelection.getLastCellRectangle(), reportPane);");
        //      "System.out.println(\"CCCCCCCCVVBBMM1:\" + ($1) + \"::\" + this.arrayList.size());"                
        //        "System.out.println(\"CCCCCCCCVVBBMM2:\" + this.gridSelection.getFirstCellRectangle());"
        //        +"System.out.println(\"CCCCCCCCVVBBMM2:\" + this.gridSelection.getLastCellRectangle());"

        ctClass.toClass();
    }

    // 分析出最后一条返回数据集的select语句，并且把条件转换成1=2
    // 注意最后一条select语句不是作为条件的语句
    public static String parseSQL(String sql) {
        // 去除--注释
        while (sql.contains("--")) {
            int tmp_ind = sql.indexOf("--");
            int tmp_ind2 = sql.indexOf("\n", tmp_ind + 1);
            String tmp_sss = sql.substring(0, tmp_ind);
            if (tmp_ind2 > 0) {
                tmp_sss = tmp_sss + sql.substring(tmp_ind2);
            }
            sql = tmp_sss;
        }

        String tmp_sql = sql.toLowerCase();

        int first_sel_ind = tmp_sql.indexOf("select");
        int last_sel_ind = tmp_sql.lastIndexOf("select");

        if (first_sel_ind < 0) {
            tmp_sql = sql;
        } else if (first_sel_ind == last_sel_ind) {
            tmp_sql = sql.substring(first_sel_ind);
        } else {
            while (last_sel_ind > first_sel_ind) {
                tmp_sql = tmp_sql.substring(0, last_sel_ind);

                String tmp_sql2 = tmp_sql.replaceAll(" ", "").replaceAll("\n", "").toLowerCase();
                if (!(tmp_sql2.endsWith("in(")
                        || tmp_sql2.endsWith("=(")
                        || tmp_sql2.endsWith("join(")
                        || tmp_sql2.endsWith("join")
                        || tmp_sql2.endsWith("from(")
                        || tmp_sql2.endsWith("in")
                        || tmp_sql2.endsWith("=")
                        || tmp_sql2.endsWith(",("))) {
                    break;
                }

                last_sel_ind = tmp_sql.lastIndexOf("select");
            }
            tmp_sql = sql.substring(last_sel_ind);
        }

        String tmp_sql2 = tmp_sql.replace(" ", "").replaceAll("\n", "").toLowerCase();
        if (tmp_sql2.contains("join(")
                || tmp_sql2.contains("joinselect")) {
            return tmp_sql;
        }



        /*
        if (tmp_sql.replaceAll("\n", " ").toLowerCase().contains(" where ")
        && !tmp_sql2.contains("join(")
        && !tmp_sql2.contains("joinselect")){
        String gb = "";
        if (tmp_sql.toLowerCase().indexOf("group by") >= 0)
        gb = tmp_sql.substring(tmp_sql.toLowerCase().indexOf("group by"));
        tmp_sql = tmp_sql.substring(0, tmp_sql.replaceAll("\n", " ").toLowerCase().indexOf(" where ") + 7);
        tmp_sql = tmp_sql + "1=2 " + gb;
        }
         */
        tmp_sql = tmp_sql.replaceAll("\n", " ");
        while (tmp_sql.contains("  ")) {
            tmp_sql = tmp_sql.replaceAll("  ", " ");
        }
        tmp_sql = tmp_sql.replaceAll("\\( ", "\\(");

        while (tmp_sql.toLowerCase().indexOf("(select") >= 0) {
            int tmp_i = tmp_sql.toLowerCase().indexOf("(select");
            int tmp_i2 = 0;
            int count = 1;
            for (int i = tmp_i + 2; i < tmp_sql.length(); i++) {
                if (tmp_sql.charAt(i) == '(') {
                    count++;
                }
                if (tmp_sql.charAt(i) == ')') {
                    count--;
                }
                if (count == 0) {
                    tmp_i2 = i;
                    break;
                }
            }
            if (tmp_i2 > tmp_i) {
                tmp_sql = tmp_sql.substring(0, tmp_i) + "( " + abate_where(tmp_sql.substring(tmp_i + 1, tmp_i2)) + tmp_sql.substring(tmp_i2);
            } else {
                tmp_sql = tmp_sql.substring(0, tmp_i) + "( select" + tmp_sql.substring(tmp_i + 8);
            }
        }

        return abate_where(tmp_sql);
    }

    private static int locate_where_index(String tmp_sql, String tmp_key) {
        int last_where_index = tmp_sql.replaceAll("\n", " ").toLowerCase().lastIndexOf(tmp_key);
        if (last_where_index < 0) {
            return last_where_index;
        }
        while (!match_bracket(tmp_sql.substring(0, last_where_index))) {
            tmp_sql = tmp_sql.substring(0, last_where_index);
            last_where_index = tmp_sql.replaceAll("\n", " ").toLowerCase().lastIndexOf(tmp_key);
            if (last_where_index < 0) {
                return last_where_index;
            }
        }
        return last_where_index;
    }

    private static boolean match_bracket(String tmp_sql) {
        int count = 0;
        for (int i = 0; i < tmp_sql.length(); i++) {
            if (tmp_sql.charAt(i) == '(') {
                count++;
            }
            if (tmp_sql.charAt(i) == ')') {
                count--;
            }
        }
        return count == 0;
    }

    private static String abate_where(String tmp_sql) {
        System.out.println("abate_where1:" + tmp_sql);
        String gb = "";
        int last_group_index = tmp_sql.toLowerCase().lastIndexOf("group by");

        int last_where_index = locate_where_index(tmp_sql, " where ");

        int last_from_index = locate_where_index(tmp_sql, " from ");//tmp_sql.replaceAll("\n", " ").toLowerCase().lastIndexOf(" from ");
        if (last_where_index > last_from_index) {
            if (last_group_index >= last_where_index) {
                gb = tmp_sql.substring(last_group_index);
            }
            tmp_sql = tmp_sql.substring(0, last_where_index + 7);
            tmp_sql = tmp_sql + "1=2 " + gb;
        }
        System.out.println("abate_where2:" + tmp_sql);
        return tmp_sql;
    }

    public static void main(String[] args) {
//        System.out.println("aaaaaaa1:" + abate_where("select k_kqj_key,k_kaoqin_a01_key,CONVERT(DATETIME,(CONVERT(VARCHAR(11),K_CARD_DATE,120))+CONVERT(VARCHAR(8),K_CARD_TIME,108)) as kssj   from k_card  where CONVERT(DATETIME,(CONVERT(VARCHAR(11),k_card_date,120)+CONVERT(VARCHAR(8),k_card_time,108)))<=@jzsj    and CONVERT(DATETIME,(CONVERT(VARCHAR(11),k_card_date,120)+CONVERT(VARCHAR(8),k_card_time,108)))>=@kssj"));
//        System.out.println("aaaaaaa2:" + abate_where("select sysParameter_code, count(1) from SysParameter sp where sp.sysParameter_key='SysManPass' group by sysParameter_code"));

        String tmp_sql = "select a.dept_code,a.content,b.a0190,b.a0101 "
                + "from deptcode a,a01 b,k_kaoqin_a01 d,k_kqj e,"
                + "(select k_kqj_key,k_kaoqin_a01_key,CONVERT(DATETIME,(CONVERT(VARCHAR(11),K_CARD_DATE,120))+CONVERT(VARCHAR(8),K_CARD_TIME,108)) as kssj"
                + "   from k_card"
                + "  where CONVERT(DATETIME,(CONVERT(VARCHAR(11),k_card_date,120)+CONVERT(VARCHAR(8),k_card_time,108)))<=@jzsj"
                + "    and CONVERT(DATETIME,(CONVERT(VARCHAR(11),k_card_date,120)+CONVERT(VARCHAR(8),k_card_time,108)))>=@kssj"
                + ") c "
                + "where a.deptcode_key = b.deptcode_key"
                + "  and b.a01_key = d.a01_key"
                + "  and d.k_kaoqin_a01_key = c.k_kaoqin_a01_key"
                + "  and c.k_kqj_key = e.k_kqj_key"
                + "  and e.k_kqj_type = '上班机'";
        tmp_sql = tmp_sql.replaceAll("\n", " ");
        while (tmp_sql.contains("  ")) {
            tmp_sql = tmp_sql.replaceAll("  ", " ");
        }
        tmp_sql = tmp_sql.replaceAll("\\( ", "\\(");

        while (tmp_sql.toLowerCase().indexOf("(select") >= 0) {
            int tmp_i = tmp_sql.toLowerCase().indexOf("(select");
            int tmp_i2 = 0;
            int count = 1;
            for (int i = tmp_i + 2; i < tmp_sql.length(); i++) {
                if (tmp_sql.charAt(i) == '(') {
                    count++;
                }
                if (tmp_sql.charAt(i) == ')') {
                    count--;
                }
                if (count == 0) {
                    tmp_i2 = i;
                    break;
                }
            }
            if (tmp_i2 > tmp_i) {
                tmp_sql = tmp_sql.substring(0, tmp_i) + "( " + abate_where(tmp_sql.substring(tmp_i + 1, tmp_i2 - 1)) + tmp_sql.substring(tmp_i2);
            } else {
                tmp_sql = tmp_sql.substring(0, tmp_i) + "( select" + tmp_sql.substring(tmp_i + 8);
            }
        }

        System.out.println(" abate_where(tmp_sql):" + abate_where(tmp_sql));
    }

    public static void setColumnTitle(java.awt.Rectangle startrect, java.awt.Rectangle endrect, ReportPane reportPane) {
        // 给数据列增加标题
        if (startrect.y != endrect.y) {
            // 上下扩展
            for (int i = Math.min(startrect.y, endrect.y); i <= Math.max(startrect.y, endrect.y); i++) {
                CellElement cellElement = reportPane.getEditingReport().getCellElement(startrect.x, i);
                if (cellElement != null && cellElement.getValue() != null) {
                    if (cellElement.getValue() instanceof DSColumn) {
                        DSColumn dSColumn = (DSColumn) cellElement.getValue();
                        CellElement cellElement2 = new DefaultCellElement(startrect.x - 1, i, 1, 1, dSColumn.getColumnName());
                        cellElement2.setStyle(cellElement2.getStyle().deriveHorizontalAlignment(4));
                        reportPane.getEditingReport().addCellElement(cellElement2);
                    }
                }
            }
        } else {
            // 左右扩展
            for (int i = Math.min(startrect.x, endrect.x); i <= Math.max(startrect.x, endrect.x); i++) {
                CellElement cellElement = reportPane.getEditingReport().getCellElement(i, startrect.y);
                if (cellElement != null && cellElement.getValue() != null) {
                    if (cellElement.getValue() instanceof DSColumn) {
                        DSColumn dSColumn = (DSColumn) cellElement.getValue();
                        CellElement cellElement2 = new DefaultCellElement(i, startrect.y - 1, 1, 1, dSColumn.getColumnName());
                        cellElement2.setStyle(cellElement2.getStyle().deriveHorizontalAlignment(0));
                        reportPane.getEditingReport().addCellElement(cellElement2);
                    }
                }
            }
        }
    }

    private static void changeTableChartData(String class_name) throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get(class_name);
        String mname;
        CtMethod mold;
        mname = "getCategoryLabel";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody(
                "{"
                + "checkInData();"
                + "Object obj = ((com.fr.data.impl.TableChartData.ObjectName)categoryLabelListSet.get(($1))).getObjectThing();"
                + "String column_name = baseTableData.getColumnName( categoryLabelIndex);"
                + "obj = org.jhrcore.client.report.ReportPanel.changeLabel(column_name, obj);"
                + "return obj == null ? \"\" : obj;}");

        mname = "getSeriesLabel";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody(
                "{"
                + "checkInData();"
                + "Object obj = ((com.fr.data.impl.TableChartData.ObjectName)seriesLabelListSet.get(($1))).getObjectThing();"
                + "String column_name = baseTableData.getColumnName( seriesIndex);"
                + "obj = org.jhrcore.client.report.ReportPanel.changeLabel(column_name, obj);"
                + "return obj==null ? \"\" : obj;}");
        ctClass.toClass();
    }

    private static void changeTableDataTree() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.data.TableDataTree");
        String mname;
        CtMethod mold;
        mname = "loadChildTreeNodes";
        mold = ctClass.getDeclaredMethod(mname);
        //mold.insertAt(307, "org.jhrcore.ui.FormulaTextDialog.showMsg(localTableDataException.getMessage());" +
        //        "if (1==0)");
        mold.setBody("{Object localObject1 = ($1).getUserObject();"
                + "if (localObject1 instanceof com.fr.design.data.TableDataOP)"
                + "  return op2TreeNode((com.fr.design.data.TableDataOP)localObject1);"
                + "if (!(localObject1 instanceof com.fr.dialog.NameObject))"
                + "  return new com.fr.cell.comp.ExpandMutableTreeNode[0];"
                + "com.fr.dialog.NameObject localNameObject = (com.fr.dialog.NameObject)localObject1;"
                + "Object localObject2 = localNameObject.getObject();"
                + "if (!(localObject2 instanceof com.fr.data.TableData))"
                + "  return new com.fr.cell.comp.ExpandMutableTreeNode[0];"
                + "com.fr.data.TableData localTableData1 = (com.fr.data.TableData)localObject2;"
                + "com.fr.data.TableData localTableData2 = null;"
                + "try"
                + "{"
                + "  localTableData2 = com.fr.base.FRContext.getCurrentEnv().previewTableData(localTableData1, java.util.Collections.EMPTY_MAP, 0);"
                + "}"
                + "catch (Exception localException)"
                + "{"
                + "  com.fr.base.FRContext.getLogger().log(java.util.logging.Level.WARNING, localException.getMessage(), localException);"
                + "}"
                + "java.util.ArrayList localArrayList = new java.util.ArrayList();"
                + "try"
                + "{"
                + "  int i = localTableData2.getColumnCount();"
                + "  for (int j = 0; j < i; ++j)"
                + "  {"
                + "    String str = localTableData2.getColumnName(j);"
                + "    localArrayList.add(str);"
                + "  }"
                + "}"
                + "catch (com.fr.data.TableDataException localTableDataException)"
                + "{"
                + "  com.fr.base.FRContext.getLogger().log(java.util.logging.Level.WARNING, localTableDataException.getMessage(), localTableDataException);"
                + "org.jhrcore.util.MsgUtil.showHRMsg(localTableDataException.getMessage(),\"执行错误,错误原因如下：\");"
                + //    "  JOptionPane.showMessageDialog(SwingUtilities.getWindowAncestor(this), localTableDataException.getMessage(), Inter.getLocText("Error"), 0);" +
                "}"
                + "if (isSortable())"
                + "  java.util.Collections.sort(localArrayList);"
                + "com.fr.cell.comp.ExpandMutableTreeNode[] arrayOfExpandMutableTreeNode = new com.fr.cell.comp.ExpandMutableTreeNode[localArrayList.size()];"
                + "for (int j = 0; j < arrayOfExpandMutableTreeNode.length; ++j)"
                + "  arrayOfExpandMutableTreeNode[j] = new com.fr.cell.comp.ExpandMutableTreeNode(localArrayList.get(j));"
                + "return arrayOfExpandMutableTreeNode;}");
        ctClass.toClass();
    }

    private static void changeFRCoreContext() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.base.core.FRCoreContext");
        String mname;
        CtMethod mold;
        mname = "recordCalculator";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore("System.out.println(\"recordCalculator:\" + System.currentTimeMillis());");

        mname = "releaseCalculator";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore("System.out.println(\"releaseCalculator:\" + System.currentTimeMillis());");


        ctClass.toClass();

        ctClass = ClassPool.getDefault().get("com.fr.report.core.SE");
        mname = "ex";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore("System.out.println(\"ex1:\" + System.currentTimeMillis());");
        mname = "buildGenealogy";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore("System.out.println(\"buildGenealogy:\" + System.currentTimeMillis());");
        mname = "initBEB";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore("System.out.println(\"initBEB:\" + System.currentTimeMillis());");
        mname = "calculateCellElement";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore("System.out.println(\"calculateCellElement:\" + ($1) + System.currentTimeMillis());");
        ctClass.toClass();



        ctClass = ClassPool.getDefault().get("com.fr.report.AbstractReport");
        mname = "getTableDataNameIterator";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore("System.out.println(\"getTableDataNameIterator:\" + System.currentTimeMillis());");
        ctClass.toClass();
    }

    private static void changeGrid() throws NotFoundException, CannotCompileException {
        /*CtClass ctClass = ClassPool.getDefault().get("com.fr.cell.ReportPane");
        String mname;
        CtMethod mold;
        mname = "before_undoRecord";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{" +
        "org.jhrcore.client.report.ReportPanel.enable_locate_dict = false;" +
        "}");
        
        mname = "after_undoRecord";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{" +
        "org.jhrcore.client.report.ReportPanel.enable_locate_dict = true;" +
        "}");
        ctClass.toClass();*/
    }

    private static void changeDataSeries() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.chart.plot.DataSeries");
        String mname;
        CtMethod mold;
        mname = "getSeriesName";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{"
                + "System.out.println(\"getSeriesName:\" + this.seriesName);"
                + "return this.seriesName;"
                + "}");
//"String column_name = baseTableData.getColumnName( categoryLabelIndex);" +
//                "obj = org.jhrcore.client.report.ReportPanel.changeLabel(column_name, obj);" +
        ctClass.toClass();
    }

    private static void changeReportChartData() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.data.impl.ReportChartData");
        String mname;
        CtMethod mold;
        mname = "getCategoryLabel";
        mold = ctClass.getDeclaredMethod(mname);
        mold.insertBefore(
                "System.out.println(\"getCategoryLabel:\" + getCategory_array()[($1)]);");
        ctClass.toClass();
    }

    private static void changeTableDataDefinition() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.data.core.define.TableDataDefinition");
        String mname;
        CtMethod mold;
        mname = "changeSeriesNameArray";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{"
                + "Object[] paramList2 = ($1);"
                + "String categoryName2 = ($2);"
                + "for (int i = 0; i < paramList2.length; i++){"
                + "   paramList2[i] = org.jhrcore.client.report.ReportPanel.changeLabel(categoryName2, paramList2[i]);"
                + "}"
                + "}");

        mname = "changeCategoryNameArray";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody("{"
                + "Object[] paramList2 = ($1);"
                + "String categoryName2 = ($2);"
                + "for (int i = 0; i < paramList2.length; i++){"
                + "   paramList2[i] = org.jhrcore.client.report.ReportPanel.changeLabel(categoryName2, paramList2[i]);"
                + "}"
                + "}");
        ctClass.toClass();
    }

    private static void changeSE() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.report.core.cal.SE");
        for (CtConstructor constructorct : ctClass.getConstructors()) {
            constructorct.insertAfter("for (int i = 1; i < 200; i++){"
                    + "for (int j = 1; j < 30; j++){"
                    + "calculator.set(\"$$page_sum\" + com.fr.report.ReportPage.to_string(i) + j, \"$$page_sum\" + com.fr.report.ReportPage.to_string(i) + j);"
                    + "calculator.set(\"$$page_count\" + com.fr.report.ReportPage.to_string(i) + j, \"$$page_count\" + com.fr.report.ReportPage.to_string(i) + j);}"
                    + "calculator.set(\"$$user_name\", \"$$user_name\");"
                    + "}"); //com.fr.report.ReportPage.to_string(i)
        }
        ctClass.toClass();
    }

    private static void changeValueEditorPaneFactory() throws NotFoundException, CannotCompileException {
        CtClass ctClass = ClassPool.getDefault().get("com.fr.design.mainframe.ValueEditorPaneFactory");
        String mname;
        CtMethod mold;
        mname = "allEditors";
        mold = ctClass.getDeclaredMethod(mname);
        mold.setBody(
                "{"
                + "com.fr.design.gui.core.componenteditor.FormulaEditor formulaeditor = new com.fr.design.gui.core.componenteditor.FormulaEditor(com.fr.base.Inter.getLocText(\"Parameter-Formula\"));"
                + " formulaeditor.setEnabled(true);"
                + " return (new com.fr.design.gui.core.componenteditor.Editor[] {"
                + "    new com.fr.design.gui.core.componenteditor.TextEditor(com.fr.base.Inter.getLocText(\"Parameter-String\")), "
                + "new com.fr.design.gui.core.componenteditor.NumberEditor(new Integer(1), com.fr.base.Inter.getLocText(\"Parameter-Integer\")), "
                + "new com.fr.design.gui.core.componenteditor.NumberEditor(new Double(1.0D), com.fr.base.Inter.getLocText(\"Parameter-Double\")), "
                + "new com.fr.design.gui.core.componenteditor.DateEditor(true, com.fr.base.Inter.getLocText(\"Date\")), "
                + "new com.fr.design.gui.core.componenteditor.BooleanEditor(com.fr.base.Inter.getLocText(\"Parameter-Boolean\")), "
                + "new org.jhrcore.client.report.componenteditor.DeptCodeEditor(),"
                + "new org.jhrcore.client.report.componenteditor.CodeEditor(),"
                + "formulaeditor, "
                + "new com.fr.design.gui.core.componenteditor.ParameterEditor(com.fr.base.Inter.getLocText(\"Parameter\")), "
                + "new com.fr.design.gui.core.componenteditor.ColumnRowEditor(com.fr.base.Inter.getLocText(\"Cell\")), "
                + "new com.fr.design.gui.core.componenteditor.DSColumnEditor(com.fr.base.Inter.getLocText(\"DataColumn\"))"
                + "});"
                + "}");
        ctClass.toClass();
    }
    /*
    private static void changeAbstractStyleAction() throws NotFoundException, CannotCompileException {
    CtClass ctClass = ClassPool.getDefault().get("com.fr.design.actions.cell.style.AbstractStyleAction");
    String mname;
    CtMethod mold;
    mname = "update";
    mold = ctClass.getDeclaredMethod(mname);
    mold.insertBefore(
    "System.out.println(\"AbstractStyleAction.update:\");"
    );
    ctClass.toClass();
    }
    
    private static void changeReportFontNameAction() throws NotFoundException, CannotCompileException {
    CtClass ctClass = ClassPool.getDefault().get("com.fr.design.actions.cell.style.ReportFontNameAction");
    String mname;
    CtMethod mold;
    mname = "updateStyle";
    mold = ctClass.getDeclaredMethod(mname);
    mold.insertBefore(
    "System.out.println(\"ReportFontNameAction.updateStyle:\");"
    );
    ctClass.toClass();
    }*/
}
