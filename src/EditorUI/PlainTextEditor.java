package EditorUI;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;

import org.w3c.dom.events.Event;

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
        Scanner scanner;
        try{
            scanner = new Scanner(file);
        }
        catch (FileNotFoundException err){
            throw err;
        }
        String contents = "";
        while (scanner.hasNextLine()){
            contents+=scanner.nextLine()+"\n";
        }
        initPage(contents);
        scanner.close();
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

        this.add(this.page);
    }

    public String getTitle(){
        return this.title;
    }
}