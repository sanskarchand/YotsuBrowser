package yb.graphpacks;

import javax.swing.*;
import javax.swing.event.*;
import java.util.*;
import java.awt.Dimension;

public class GraphModule extends JPanel implements ListSelectionListener {
    
    private static final Dimension SCREEN_SIZE = new Dimension(640, 480);
    private static final int DIVIDER_LOC = 180;
    private static final Dimension L_MIN_SIZE = new Dimension(100, 50);
    
    private JList list;
    private JSplitPane split_pane;
    private String[] board_names;
    //private String[] board_names = { "/a/-Anime and Manga",
    //                           "/g/-Technology", "/m/-Mecha"};

    public GraphModule(String[] m_board_names) {
        
        board_names = m_board_names;
        list = new JList(board_names);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        
        list.setSelectedIndex(0); //NB: adding persistence?
        list.addListSelectionListener(this);

        JScrollPane list_scroll_pane = new JScrollPane(list);
        //the right panel
        JPanel right_panel = new JPanel();
        JScrollPane right_scroll_pane = new JScrollPane(right_panel);


        split_pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        list_scroll_pane, right_scroll_pane);
        split_pane.setOneTouchExpandable(true);
        split_pane.setDividerLocation(DIVIDER_LOC);


        list_scroll_pane.setMinimumSize(L_MIN_SIZE);
        right_scroll_pane.setMinimumSize(L_MIN_SIZE);

        split_pane.setPreferredSize(SCREEN_SIZE);
        updateLabel(board_names[list.getSelectedIndex()]);

    }

    public void valueChanged(ListSelectionEvent e) {
        JList list = (JList)e.getSource();
        updateLabel(board_names[list.getSelectedIndex()]);
    }

    public JSplitPane getSplitPane() {
        return split_pane;
    }

    protected void updateLabel(String name) {
        ;
    }

    private void initGUI( ) {
        JFrame frame = new JFrame("DEMO");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //GraphModule mod = new GraphModule();
        frame.getContentPane().add(this.getSplitPane());
        
        frame.pack();
        frame.setVisible(true);

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
