package EditorUI;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JTextArea;

/*
 * TODO: add features
 * - softwrap option
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
        JTextArea textArea = new JTextArea(intialText);
        this.page = new JScrollPane(textArea);
        this.add(this.page);
    }

    public String getTitle(){
        return this.title;
    }
}