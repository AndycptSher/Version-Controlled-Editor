package src;

import javax.swing.*;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

public class JavaEditor extends JFrame{
    
    public JavaEditor(){
        JTabbedPane contentPane = new JTabbedPane();
        this.setContentPane(contentPane);
        
        this.setSize(new Dimension(400, 200));
        this.setVisible(true);
    }

    public void addTab(EditorSpace component){
        JTabbedPane contentPane = ((JTabbedPane)getContentPane());
        contentPane.addTab(component.name, component);
        contentPane.setTabComponentAt(contentPane.getTabCount()-1, new TabComponent(contentPane));
    }
    
    public static void main(String[] args){
        JavaEditor display = new JavaEditor();
        display.addTab(new EditorSpace("heyyy", new JEditorPane()));
        System.out.println("Hello world!");
    }
}

class EditorSpace extends JPanel{
    /*
     * favor composition over inheritance
     */
    String name;
    JTextComponent component;
    EditorSpace(String name, JTextComponent component){
        this.name = name;
        this.component = component;
        add(component);
    }
}

class TabComponent extends JPanel{
    /*
     * component created at top of tab
     * giving x to close tab feature
     */
    final JTabbedPane parent;
    private class deleteButton extends JButton{
        public deleteButton(){
            super("x");
            setToolTipText("Close this tab");
            setContentAreaFilled(false);
            setBorderPainted(false);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    // button is pressed, delete/close tab
                    int index = getTabIndex();
                    parent.remove(index);
                }
            });
        }

        // @Override
        // public void actionPerformed(ActionEvent e){
        //     // button is pressed, delete/close tab
        //     int index = getTabIndex();
        //     parent.remove(index);
        // }
    }
    public TabComponent(JTabbedPane parent){
        this.parent = parent;
        
        this.add(new JLabel(){
            public String getText(){
                int index = getTabIndex();
                // make it past initialisation
                if (index == -1){
                    return null;
                }
                return parent.getTitleAt(index);
            }
        });
        
        // JButton deleteTabButton = new deleteButton();
        this.add(new deleteButton());
        this.setOpaque(false);
    }
    private int getTabIndex(){
        return this.parent.indexOfTabComponent(this);
    }
}
