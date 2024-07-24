package JavaEditor.src;

import javax.swing.*;
import javax.swing.border.BevelBorder;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import javax.swing.plaf.basic.BasicButtonUI;
import javax.swing.text.JTextComponent;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class JavaEditor extends JFrame{
    
    public JavaEditor(){
        //Create a panel and add components to it.
        // JPanel contentPane = new JPanel(new BorderLayout());
        // contentPane.add(new Button(), BorderLayout.CENTER);
        // contentPane.add(new Button(), BorderLayout.PAGE_END);
        JTabbedPane contentPane = new JTabbedPane();
        this.setContentPane(contentPane);
        
        initMenuBar();
        this.setSize(new Dimension(300, 200));
        this.setVisible(true);
    }

    private void initMenuBar(){
        JMenuBar menuBar = new JMenuBar();
        setJMenuBar(menuBar);

        JMenu menu = new JMenu("File");
        menuBar.add(menu);
        
        JMenuItem open = new JMenuItem("Open file");
        open.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_DOWN_MASK)); // binds to CTRL + O
        open.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                UIManager.put("FileChooser.readOnly", Boolean.TRUE); // stop user from being able to remane files, placed before file selector creation
                JFileChooser chooser = new JFileChooser();
                chooser.setAcceptAllFileFilterUsed(false);
                chooser.setFileFilter(new FileNameExtensionFilter("TEXT FILES", "txt", "text"));
                
                switch(chooser.showOpenDialog(null)){
                    case JFileChooser.APPROVE_OPTION:
                        File selectedFile = chooser.getSelectedFile();
                        Scanner scanner;
                        try{
                            scanner = new Scanner(selectedFile);
                        }
                        catch(FileNotFoundException err){
                            System.err.print("File: "+selectedFile.getName()+" Not Found");
                            break;
                        }
                        while (scanner.hasNextLine()){
                            System.out.println(scanner.nextLine());
                        }
                        break;
                }
                
                
            }
        });
        menu.add(open);

        JMenuItem close = new JMenuItem("Close Current Tab");
        close.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_DOWN_MASK)); // binds CTRL + W
        close.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e){
                JTabbedPane contentPane = ((JTabbedPane)JavaEditor.this.getContentPane());
                contentPane.remove(contentPane.getSelectedComponent());
            }
        });
        menu.add(close);
    }

    public void addTab(EditorSpace component){
        JTabbedPane contentPane = ((JTabbedPane)getContentPane());
        contentPane.addTab(component.name, component);
        contentPane.setTabComponentAt(contentPane.getTabCount()-1, new TabComponent(contentPane));
    }
    
    public static void main(String[] args){
        JavaEditor display = new JavaEditor();
        
        JTextArea panel0 = new JTextArea();
        JEditorPane panel1;
        panel1 = new JEditorPane();
        JTextPane panel2 = new JTextPane();
        JTextComponent[] panels = new JTextComponent[]{panel0, panel1, panel2};
        for (int i=0; i<panels.length;i++){
            JTextComponent p = panels[i];
            display.addTab(new EditorSpace(String.valueOf(i), p));
        }

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
        setLayout(new BorderLayout());
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
    private final JTabbedPane parent;
    private final deleteButton button;

    private final static MouseListener mouseListener = new MouseAdapter() {
        
    public void mouseEntered(MouseEvent e){
        Component component = e.getComponent();
        if(component instanceof deleteButton){
            ((deleteButton)component).setText("⬤");
        }
    }

    public void mouseExited(MouseEvent e){
        Component component = e.getComponent();
        if(component instanceof deleteButton){
                ((deleteButton)component).setText("x");
            }
        }
        
    };
    private class deleteButton extends JButton{
        public deleteButton(){
            super("x");
            setPreferredSize(new Dimension(20, 20));
            setToolTipText("Close this tab");
            setUI(new BasicButtonUI());
            // transparent
            setContentAreaFilled(false);
            setFocusable(false);
            // makes x visible instead of ...
            setBorder(new BevelBorder(ABORT));
            setBorderPainted(false);
            this.addMouseListener(mouseListener);
            // setRolloverEnabled(true);
            addActionListener(new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e){
                    // button is pressed, delete/close tab
                    int index = getTabIndex();
                    parent.remove(index);
                }
            });
        }
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
        
        this.button = new deleteButton();
        this.add(this.button);
        this.setOpaque(false);
    }
    private int getTabIndex(){
        return this.parent.indexOfTabComponent(this);
    }
}
