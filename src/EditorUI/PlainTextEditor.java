package EditorUI;

import java.awt.BorderLayout;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

import javax.swing.*;
import javax.swing.JPanel;
import javax.swing.JTextArea;

public class PlainTextEditor extends JPanel{
    private JScrollPane page;
    private String name;
    public PlainTextEditor(String name){
        this.name = name;
        initPage("");
    }

    public PlainTextEditor(String name, String content){
        this.name = name;
        initPage(content);
    }

    public PlainTextEditor(String name, File file) throws FileNotFoundException, IllegalArgumentException{
        if (!file.getName().endsWith(".txt")) throw new IllegalArgumentException("File must be a .txt file");
        this.name = name;
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

    public String getName(){
        return this.name;
    }
}