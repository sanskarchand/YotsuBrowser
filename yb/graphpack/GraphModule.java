package yb.graphpack;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.Dimension;
import java.awt.FlowLayout;

public class GraphModule extends JPanel implements ListSelectionListener {
    
    private static final Dimension SCREEN_SIZE = new Dimension(640, 480);
    private static final int DIVIDER_LOC = 180;
    private static final Dimension L_MIN_SIZE = new Dimension(100, 50);
    
    private JFrame frame;

    private JList<String> list;
    private JPanel right_panel;     // NOTABENE: recover this from JScrollPane, from split_pane
    private JSplitPane split_pane;
    private String[] board_names;
    private String selected_board; 
    //private String[] board_names = { "/a/-Anime and Manga",
    //                           "/g/-Technology", "/m/-Mecha"};

    public GraphModule(String[] m_board_names) {
        
        selected_board =  "<UNDEF>";
        board_names = m_board_names;
        list = new JList<String>(board_names);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        list.setSelectedIndex(0); //NB: adding persistence?
        list.addListSelectionListener(this);

        JScrollPane list_scroll_pane = new JScrollPane(list);
        //the right panel
        right_panel = new JPanel();
        //right_panel.setLayout( new FlowLayout() );
        JScrollPane right_scroll_pane = new JScrollPane(right_panel);


        split_pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        list_scroll_pane, right_scroll_pane);
        split_pane.setOneTouchExpandable(true);
        split_pane.setDividerLocation(DIVIDER_LOC);


        list_scroll_pane.setMinimumSize(L_MIN_SIZE);
        right_scroll_pane.setMinimumSize(L_MIN_SIZE);

        split_pane.setPreferredSize(SCREEN_SIZE);
        //updateLabel(board_names[list.getSelectedIndex()]);

    }
    
    // implementing a virtual function
    public void valueChanged(ListSelectionEvent e) {
        if ( e.getValueIsAdjusting() ) {
            return;
        }

        JList list = (JList)e.getSource();
        updateLabel(board_names[list.getSelectedIndex()]);
    }

    public JSplitPane getSplitPane() {
        return split_pane;
    }

    public void addCatalogThread( String text, String img_path) {

        JLabel label = new JLabel( "<html>" + text + "<br></html>", JLabel.LEFT);      // already in HTML
        right_panel.add(label);
        right_panel.setVisible(true);

        System.out.println("It's-a me, Common Sensio!");
        frame.setVisible(true);

    }

    protected void updateLabel(String name) {
        selected_board = name;
    }

    public void refreshThis() {

        frame.revalidate();
        frame.repaint();

    }

    private void initGUI( ) {
        //JFrame frame = new JFrame("DEMO");
        frame = new JFrame("DEMO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //GraphModule mod = new GraphModule();
        frame.getContentPane().add(this.getSplitPane());
        
        frame.pack();
        frame.setVisible(true);

    }

    public String getSelectedBoard() {
        return selected_board;
    }

    public void runGUI() {
        // schedule job for event dispatching thread
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                initGUI();
            }
        });
    }
}
