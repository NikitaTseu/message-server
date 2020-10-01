import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.IOException;

class Editor extends JFrame implements ActionListener {
    private JFrame frame = new JFrame("Secured Notepad");
    private JTextArea textArea = new JTextArea();;
    private JPanel pane = new JPanel();

    private CustomHttpClient httpClient = new CustomHttpClient();

    private String currentFilename = "";

    Editor() throws IOException, InterruptedException {
        // 192.168.1.66:8080
        // connect to server
        while(true){
            boolean serverConnected = httpClient.testConnection(JOptionPane.showInputDialog(frame, "Connect to server", "192.168.1.66:8080"));
            if(!serverConnected){
                JOptionPane.showMessageDialog(frame, "Connection failed");
            }
            else{
                httpClient.getSessionKey();
                JOptionPane.showMessageDialog(frame, "Connected to server");
                break;
            }
        }

        JMenuBar menuBar = new JMenuBar();

        JMenu menu = new JMenu("File");
        JMenuItem itemNew = new JMenuItem("New");
        JMenuItem itemOpen = new JMenuItem("Open");
        JMenuItem itemSave = new JMenuItem("Save");
        JMenuItem itemDelete = new JMenuItem("Delete");

        itemNew.addActionListener(this);
        itemOpen.addActionListener(this);
        itemSave.addActionListener(this);
        itemDelete.addActionListener(this);

        menu.add(itemNew);
        menu.add(itemOpen);
        menu.add(itemSave);
        menu.add(itemDelete);

        menuBar.add(menu);

        textArea.setLineWrap(true);
        textArea.setVisible(false);
        textArea.setPreferredSize(new Dimension(980, 590));

        pane.add(textArea);

        frame.add(pane);
        frame.setJMenuBar(menuBar);
        frame.setSize(1000, 600);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.show();
    }

    public void actionPerformed(ActionEvent e) {
        String action = e.getActionCommand();

        if (action.equals("Open")) {
            try {
                String response = httpClient.getFilenames();
                String[] filenames = response.split(",");
                String filename = JOptionPane.showInputDialog(frame, "Server storage", "Open", JOptionPane.PLAIN_MESSAGE, null, filenames, filenames[0]).toString();

                currentFilename = filename;
                String text = httpClient.loadText(filename);
                textArea.setText(text);
                textArea.setVisible(true);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        }

        if (action.equals("Delete")) {
            if(currentFilename != ""){
                try {
                    boolean deleted = httpClient.deleteText(currentFilename);

                    if(deleted){
                        currentFilename = "";
                        textArea.setText("");
                        textArea.setVisible(false);
                    }
                    else{
                        JOptionPane.showMessageDialog(frame, "Sorry, something gone wrong :(");
                    }
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(frame, ex.getMessage());
                }
            }
            else{
                JOptionPane.showMessageDialog(frame, "To delete something you should open something");
            }
        }

        if (action.equals("New")) {
            try {
                String filename = JOptionPane.showInputDialog(frame, "File name");
                currentFilename = filename;
                boolean created = httpClient.createText(filename);

                if(created){
                    textArea.setText("");
                    textArea.setVisible(true);
                }
                else{
                    currentFilename = "";
                    JOptionPane.showMessageDialog(frame, "Sorry, something gone wrong :(");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        }

        if (action.equals("Save")) {
            try {
                boolean saved = httpClient.saveText(currentFilename, textArea.getText());

                if(!saved){
                    JOptionPane.showMessageDialog(frame, "Sorry, something gone wrong :(");
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(frame, ex.getMessage());
            }
        }

    }
}