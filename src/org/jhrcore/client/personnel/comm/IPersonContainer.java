/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.comm;

import org.jhrcore.entity.A01;

/**
 * 选择人员容器人员时的接口，需要获得人员容器选择人员的事件则实现该接口。
 * 比如人事模块，需要实现该接口，以便在人员容器选择人员的时候显示该人员的
 * 卡片。
 * @author Administrator
 */
public interface IPersonContainer {

    public void locateDeptAndType(A01 bp);

    public void showContainerInfo();

//    public void getContainerData();
//
//    public DeptCode getCurDept();
}
