/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.personnel.comm;

import org.jhrcore.entity.A01;

/**
 * ѡ����Ա������Աʱ�Ľӿڣ���Ҫ�����Ա����ѡ����Ա���¼���ʵ�ָýӿڡ�
 * ��������ģ�飬��Ҫʵ�ָýӿڣ��Ա�����Ա����ѡ����Ա��ʱ����ʾ����Ա��
 * ��Ƭ��
 * @author Administrator
 */
public interface IPersonContainer {

    public void locateDeptAndType(A01 bp);

    public void showContainerInfo();

//    public void getContainerData();
//
//    public DeptCode getCurDept();
}
