/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhrcore.client.system.comm;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import javax.swing.tree.DefaultMutableTreeNode;

/**
 *
 * @author hflj
 */
public class DragAndDropTransferable implements Transferable {

    private DefaultMutableTreeNode src_node;

    public DragAndDropTransferable(DefaultMutableTreeNode src_node) {
        this.src_node = src_node;
    }
    final DataFlavor flavors[] = {DataFlavor.stringFlavor};

    @Override
    public DataFlavor[] getTransferDataFlavors() {
        return flavors;
    }

    @Override
    public boolean isDataFlavorSupported(DataFlavor flavor) {
        return true;
    }

    @Override
    public Object getTransferData(DataFlavor flavor)
            throws UnsupportedFlavorException, IOException {
        return src_node;
    }
}
