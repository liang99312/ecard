/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.swing.table.AbstractTableModel;

/**
 *
 * @author Administrator
 */
public class QATableModel extends AbstractTableModel {
    private List<Object[]> objects = new ArrayList<Object[]>();
    
    private String[] names = new String[]{};

    public String[] getNames() {
        return names;
    }

    public void setNames(String[] names) {
        this.names = names;
    }

    public List<Object[]> getObjects() {
        return objects;
    }

    public void setObjects(List<Object[]> objects) {
        this.objects = objects;
    }
    public void addObject(Object[] objs){
        this.objects.add(objs);
        this.fireTableDataChanged();
    }
    public void deleteObject(int index){
        if(index<0||index>=objects.size())return;
        this.objects.remove(index);
        this.fireTableDataChanged();
    }
    public Object[] getObjectAt(int index){
        if(index<0||index>=objects.size())return null;
        return objects.get(index);
    }
    public void setObject(int index,Object[] obj){
        if(index<0||index>=objects.size())return;
        this.objects.set(index, obj);
    }
    @Override
    public String getColumnName(int columnIndex){
        return names[columnIndex];
    }
            
    @Override
    public int getRowCount() {
        return objects.size();
    }

    @Override
    public int getColumnCount() {
        return names.length;
    }
    public Object getCurrentObject(int index){
        if(index<0||index>=objects.size())return null;
        return objects.get(index);
    }
    @Override
    public Object getValueAt(int rowIndex, int columnIndex) {
        if (!(objects.get(rowIndex) instanceof Object[]))
            return objects.get(rowIndex);
        Object[] objs = objects.get(rowIndex);
        Object value = objs[columnIndex] == null? "" : objs[columnIndex];
        if (value instanceof String && value.toString().indexOf("?code_id") >= 0){
            value = value.toString().substring(0, value.toString().indexOf("?code_id"));
        }else if(value instanceof Date){
            Date date=(Date) value;
            SimpleDateFormat sp1=new SimpleDateFormat("yyyy-MM-dd");
            SimpleDateFormat sp2=new SimpleDateFormat("HH:mm:ss");
            if(sp2.format(date).equals("00:00:00")){
                value=sp1.format(date);
            }else{
                value=sp2.format(date);
            }
        }
        return value;
    }

}
