package EditorUI;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;

import java.awt.print.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;


/*
 * TODO: add features
 * - font selection
 * - font size
 * - status bar at bottom (word/character count, font selection, ...)
 * - Ctrl + F -> find (and replace)
 * - Ctrl + T -> new tab + set focus to new tab
 * - Ctrl + G -> goto line
 * - Ctrl + Z history of edits
 */

public class PlainTextEditor extends JPanel{
    private JScrollPane page;
    private String title;
    
    public PlainTextEditor(String title){
        this.title = title;
        initPage("");
    }

    public PlainTextEditor(String title, String content){
        this.title = title;
        initPage(content);
    }

    public PlainTextEditor(String title, File file) throws FileNotFoundException, IllegalArgumentException{
        if (!file.getName().endsWith(".txt")) throw new IllegalArgumentException("File must be a .txt file");
        this.title = title;
        
        // copy over file content to editor
        String contents = "";
        try(Scanner scanner = new Scanner(file);){
            while (scanner.hasNextLine()){
                contents+=scanner.nextLine()+"\n";
            }
        }
        catch (FileNotFoundException err){
            throw err;
        }
        
        initPage(contents);
    }

    private void initPage(String intialText){
        /*
         * helper method to initalize the editor page
         */
        // fill to fit panel
        this.setLayout(new BorderLayout());

        // toolbar for options users can select
        JToolBar bar = new JToolBar();
        bar.setFloatable(false);

        JCheckBox wrapped = new JCheckBox("Softwrap");
        wrapped.setHorizontalTextPosition(SwingConstants.LEFT);
        bar.add(wrapped);
        
        this.add(bar, BorderLayout.NORTH);
        
        JTextArea textArea = new JTextArea(intialText);
        textArea.setSelectedTextColor(null); // sets the colour of the text
        // textArea.setFont
        // button for wordwrapping

        // linewrap toggle logic
        wrapped.addItemListener(new ItemListener() {
            @Override
            public void itemStateChanged(ItemEvent e){
                switch (e.getStateChange()){
                    case ItemEvent.SELECTED:
                        textArea.setLineWrap(true);
                        break;
                    case ItemEvent.DESELECTED:
                        textArea.setLineWrap(false);
                        break;
                }
            }
        });
        // scrolling
        this.page = new JScrollPane(textArea);

        // print page
        this.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_P, InputEvent.CTRL_DOWN_MASK), "print");
        this.getActionMap().put("print", new AbstractAction() {

            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("ehy");
                try{
                    textArea.print();
                }
                catch (PrinterException _e) {
                    JOptionPane.showMessageDialog(PlainTextEditor.this, e.toString());
                }
                return;
            }  
        });

        this.add(this.page);
    }

    public String getTitle(){
        return this.title;
    }
}