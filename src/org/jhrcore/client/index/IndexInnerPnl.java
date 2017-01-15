/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.index;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.BorderFactory;
import javax.swing.JPanel;
import javax.swing.table.DefaultTableModel;
import org.jhrcore.zui.index.FreeReportPage;
import org.jhrcore.zui.index.TitleBarPnl;
import org.jhrcore.ui.listener.IPickWindowObjListener;

/**
 *
 * @author mxliteboss
 */
public class IndexInnerPnl extends javax.swing.JPanel {

    private DefaultTableModel model = new DefaultTableModel();
    private JPanel centerPane = new JPanel(new BorderLayout());
    private TitleBarPnl barPane = null;
    private FreeReportPage page = new FreeReportPage();
    private String[] fields;
    private String title;
    private List data;
    private int rows = 5;

    public IndexInnerPnl(String title, String[] fields) {
        this.title = title;
        this.fields = fields;
        initOthers();
        setUpEvent();
    }

    private void initOthers() {
        barPane = new TitleBarPnl(title);
        for (String field : fields) {
            model.addColumn(field);
        }
        page.getTable().setModel(model);
        this.setLayout(new BorderLayout());
        this.add(barPane, BorderLayout.NORTH);
        centerPane.setBackground(new Color(216, 218, 217));
        centerPane.add(page);
        centerPane.setBorder(BorderFactory.createEmptyBorder(0, 1, 1, 1));
        this.add(centerPane, BorderLayout.CENTER);
    }

    private void setUpEvent() {
        page.getTable().addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                if (e.getButton() != MouseEvent.BUTTON1) {
                    return;
                }
                if (e.getClickCount() >= 2) {
                    doClick();
                }
            }
        });
        barPane.addPickWindowObjListener(new IPickWindowObjListener() {

            @Override
            public void pickObj(Object payDef) {
                doMore();
            }
        });
//        setObjects(new ArrayList());
    }

    public void doClick() {
    }

    public void doMore() {
    }

    public void refreshData() {
    }

    public Object[] getCurRowObj() {
        int ind = getCurRowIndex();
        if (ind < 0 || data == null || data.size() <= ind) {
            return null;
        }
        return (Object[]) data.get(ind);
    }

    public int getCurRowIndex() {
        return page.getTable().getSelectedRow();
    }

    public List getObjects() {
        return data;
    }

    public void setObjects(List data) {
        int size = model.getRowCount();
        for (int i = size - 1; i >= 0; i--) {
            model.removeRow(i);
        }
        int i = 0;
        for (Object obj : data) {
//            if (i >= rows) {
//                continue;
//            }
            model.addRow((Object[]) obj);
            i++;
        }
        for (int j = i; j < rows; j++) {
            model.addRow(new Object[]{"", ""});
        }
        this.data = data;
        page.updateUI();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        this.barPane.setTitle(title);
    }
}
