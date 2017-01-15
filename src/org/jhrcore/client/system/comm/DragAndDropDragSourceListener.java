/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.jhrcore.client.system.comm;

import java.awt.dnd.DnDConstants;
import java.awt.dnd.DragSource;
import java.awt.dnd.DragSourceContext;
import java.awt.dnd.DragSourceDragEvent;
import java.awt.dnd.DragSourceDropEvent;
import java.awt.dnd.DragSourceEvent;
import java.awt.dnd.DragSourceListener;

/**
 *
 * @author hflj
 */
public class DragAndDropDragSourceListener implements DragSourceListener {

        @Override
        public void dragDropEnd(DragSourceDropEvent dragSourceDropEvent) {
            if (dragSourceDropEvent.getDropSuccess()) {
                // 拖拽动作结束的时候打印出移动节点的字符串
                int dropAction = dragSourceDropEvent.getDropAction();
                if (dropAction == DnDConstants.ACTION_MOVE) {
                    System.out.println("MOVE: remove node");
                }
            }
        }

        @Override
        public void dragEnter(DragSourceDragEvent dragSourceDragEvent) {
            DragSourceContext context = dragSourceDragEvent.getDragSourceContext();
            int dropAction = dragSourceDragEvent.getDropAction();
            if ((dropAction & DnDConstants.ACTION_COPY) != 0) {
                context.setCursor(DragSource.DefaultCopyDrop);
            } else if ((dropAction & DnDConstants.ACTION_MOVE) != 0) {
                context.setCursor(DragSource.DefaultMoveDrop);
            } else {
                context.setCursor(DragSource.DefaultCopyNoDrop);
            }
        }

        @Override
        public void dragExit(DragSourceEvent dragSourceEvent) {
        }

        @Override
        public void dragOver(DragSourceDragEvent dragSourceDragEvent) {
        }

        @Override
        public void dropActionChanged(DragSourceDragEvent dragSourceDragEvent) {
        }
    }