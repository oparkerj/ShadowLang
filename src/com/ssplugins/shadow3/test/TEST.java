package com.ssplugins.shadow3.test;

import com.ssplugins.shadow3.Shadow;
import com.ssplugins.shadow3.ShadowCommons;
import com.ssplugins.shadow3.parsing.ShadowParser;
import com.ssplugins.shadow3.section.Compound;
import com.ssplugins.shadow3.section.ShadowSection;
import com.ssplugins.shadow3.util.OperatorTree;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class TEST {
    
    public static void main(String[] args) throws IOException {
        
        InputStream stream = TEST.class.getResourceAsStream("/com/ssplugins/shadow3/test/testy.shd");
        List<String> lines = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        }
    
        ShadowParser parser = new ShadowParser(ShadowCommons.create());
        Shadow shadow = parser.parse(lines);
        shadow.firstBlock("main").ifPresent(block -> {
            block.run();
//            Compound compound = (Compound) block.getContents().getFirst().getArguments().get(0);
//            displayState(compound);
        });
        
    }
    
    public static void displayState(Compound compound) {
        DefaultMutableTreeNode root = buildTree(compound.getOpTree().getRoot());
        JFrame frame = new JFrame("Tree View");
        frame.setMinimumSize(new Dimension(400, 400));
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        JTree tree = new JTree(root);
        expandNodes(tree, 0, tree.getRowCount());
        JScrollPane pane = new JScrollPane(tree);
        frame.add(pane);
        frame.pack();
        frame.setVisible(true);
        Object lock = new Object();
        Thread thread = new Thread(() -> {
            synchronized (lock) {
                while (frame.isVisible()) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        thread.start();
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                synchronized (lock) {
                    frame.setVisible(false);
                    lock.notify();
                }
            }
        });
        try {
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
    
    public static DefaultMutableTreeNode buildTree(OperatorTree.Node node) {
        ShadowSection section;
        if (node instanceof OperatorTree.OpNode) section = ((OperatorTree.OpNode) node).getValue();
        else {
            section = ((OperatorTree.SectionNode) node).getSection();
            if (section instanceof Compound) return buildTree(((Compound) section).getOpTree().getRoot());
        }
        DefaultMutableTreeNode treeNode = new DefaultMutableTreeNode(section.toObject(null).toString());
        for (OperatorTree.Node child : node.getChildren()) {
            if (child == null) treeNode.add(new DefaultMutableTreeNode("null"));
            else treeNode.add(buildTree(child));
        }
        return treeNode;
    }
    
    public static void expandNodes(JTree tree, int start, int count) {
        for (int i = start; i < count; ++i) {
            tree.expandRow(i);
        }
        if (tree.getRowCount() != count) expandNodes(tree, count, tree.getRowCount());
    }
    
}
