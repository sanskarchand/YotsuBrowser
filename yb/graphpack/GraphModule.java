package yb.graphpack;

import javax.swing.*;
import javax.swing.plaf.basic.BasicArrowButton;
import javax.swing.event.*;
import javax.swing.border.BevelBorder;
import javax.swing.border.Border;

import java.util.*;
import java.awt.Dimension;
import java.awt.Image;
import java.awt.BorderLayout;
import java.awt.Color;

import java.io.File;

import yb.ChanBrowser;
import yb.utilpack.FilterMod;

public class GraphModule extends JPanel implements ListSelectionListener {
    
    private static final Dimension SCREEN_SIZE = new Dimension(640, 480);
    private static final int DIVIDER_LOC = 160;
    private static final Dimension L_MIN_SIZE = new Dimension(100, 50);
    private static final int STATUS_HEIGHT = 16; 
    private static final int VERT_SCROLL_SPEED = 16;

    private JFrame frame;

    private JList<String> list;     // list of boards

    private DefaultListModel<Integer> threads_model;
    private JList<Integer> threads_list; // list of thread ids 

    private String status_string = "<STATUS>"; 
    private JPanel status_panel;
    private JLabel status_label;
    
    private JPanel right_parent_panel;
    private JPanel right_panel;     // NOTABENE: recover this from JScrollPane, from split_pane
    
    private JSplitPane split_pane;
    private JPanel r_control_panel; // control panel inside right_panel
    private BasicArrowButton r_next_but;
    private BasicArrowButton r_prev_but;
    
    private String[] board_names;
    private String selected_board; 
    private int selected_page;
    private ChanBrowser cb_app;
    //private String[] board_names = { "/a/-Anime and Manga",
    //                           "/g/-Technology", "/m/-Mecha"};

    Border border_black_line = BorderFactory.createLineBorder(Color.black); 
    
    public GraphModule(String[] m_board_names, ChanBrowser main_app ) {
        
        cb_app = main_app;
        selected_board =  "<UNDEF>";
        selected_page = 0;
        board_names = m_board_names;

        list = new JList<String>(board_names);
        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setSelectedIndex(0); //NB: adding persistence?
        list.addListSelectionListener(this);
        
        threads_model = new DefaultListModel<>();
        threads_list = new JList<>( threads_model );
        threads_list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        threads_list.setSelectedIndex(0); 
        threads_list.addListSelectionListener(this);

        
        r_next_but = new BasicArrowButton ( BasicArrowButton.EAST );
        r_prev_but = new BasicArrowButton ( BasicArrowButton.WEST );

        //bind to listeners
        
        r_control_panel = new JPanel();
        r_control_panel.setLayout( new BoxLayout(r_control_panel, BoxLayout.X_AXIS) );
        r_control_panel.add( r_prev_but );
        r_control_panel.add( r_next_but );

        JScrollPane list_scroll_pane = new JScrollPane(list);
        
        //the right panels
        right_panel = new JPanel();
        right_parent_panel = new JPanel();
       
        // add PANEL to SCROLLPANE
        JScrollPane right_scroll_pane = new JScrollPane(right_panel);
        
        //right_panel.setLayout( new BoxLayout(right_panel, BoxLayout.Y_AXIS) );
        right_parent_panel.setLayout(new BoxLayout(right_parent_panel, BoxLayout.Y_AXIS));
        right_panel.setLayout(new BoxLayout(right_panel, BoxLayout.Y_AXIS));
        
        // split between left and right;
        split_pane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,
                        list_scroll_pane, right_parent_panel);

        split_pane.setOneTouchExpandable(true);
        split_pane.setDividerLocation(DIVIDER_LOC);


        list_scroll_pane.setMinimumSize(L_MIN_SIZE);
        right_scroll_pane.setMinimumSize(L_MIN_SIZE);
        right_scroll_pane.getVerticalScrollBar().setUnitIncrement(VERT_SCROLL_SPEED);

        split_pane.setPreferredSize(SCREEN_SIZE);
        //updateLabel(board_names[list.getSelectedIndex()]);
        
        // the status box
        status_panel = new JPanel();
        status_panel.setBorder(new BevelBorder(BevelBorder.LOWERED));
        //right_parent_panel.add(status_panel, BorderLayout.SOUTH);
        status_panel.setPreferredSize(new Dimension(right_parent_panel.getWidth(), 16));
        status_panel.setLayout(new BoxLayout(status_panel, BoxLayout.X_AXIS));
        
        status_label = new JLabel(status_string);
        status_label.setHorizontalAlignment(SwingConstants.LEFT);
        status_panel.add(status_label);

        right_parent_panel.add(status_panel);
        right_parent_panel.add(right_scroll_pane);
    
    }
    
    // implementing a virtual function
    public void valueChanged(ListSelectionEvent e) {
        if ( e.getValueIsAdjusting() ) {
            return;
        }

        //JList list = (JList)e.getSource();
        Object src = e.getSource();

        if (src.equals( list ) ) {
            updateLabel(board_names[list.getSelectedIndex()]);
            cb_app.boardChanged(selected_board);
        }
    }

    public JSplitPane getSplitPane() {
        return split_pane;
    }


    public void addCatalogThread( int thread_no, String thread_subject, String thread_post, String img_fname ) {
        
        threads_model.addElement( thread_no );

        javax.swing.SwingUtilities.invokeLater(new Runnable() {  
            
            public void run() {
                
                ImageIcon orig_image;
                JLabel label;
                String lable_text = thread_subject + ": "
                                    + FilterMod.reduceAndSuspendText(thread_post, yb.ChanBrowser.PREVIEW_LIMIT);

                    
                System.out.println("GRAPH DEBUG: " + FilterMod.reduceAndSuspendText(thread_post, yb.ChanBrowser.PREVIEW_LIMIT));

                //NOTABENE: impossible for img_fname to be "<UNDEF>", right?
                String imgPath = yb.ChanBrowser.PATH_TEMP_IMG + "/" + selected_board + "/" + img_fname;
                File thumb_file = new File(imgPath);
                if (thumb_file.isFile()) {
                    orig_image = new ImageIcon(imgPath); // gc? ref?
                } else {
                    orig_image = new ImageIcon(yb.ChanBrowser.PATH_NO_THUMB_IMG);
                }

                Image scaled_img = orig_image.getImage().getScaledInstance( yb.ChanBrowser.THUMB_SIZE_X, 
                                        yb.ChanBrowser.THUMB_SIZE_Y, java.awt.Image.SCALE_SMOOTH );
                ImageIcon thread_thumb = new ImageIcon (scaled_img);
                


                label = new JLabel( "<html>" + thread_subject + "</html>", thread_thumb, JLabel.LEFT);      // already in HTML
                
                label.setBorder(border_black_line);

                right_panel.add(label);
                right_panel.setVisible(true);
                
                //System.out.println("It's-a me, Common Sensio!");
                frame.setVisible(true);
            }
        });

    }

    protected void updateLabel(String name) {
        selected_board = name;
    }

    public void changeStatus(String new_status) {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                status_string = new_status; 
                status_label.setText(status_string);
            }

        });
    }

    public void  deletePreviousContent() {

        javax.swing.SwingUtilities.invokeLater(new Runnable() {
        
            public void run() {
                right_panel.removeAll();
            }
        });
    }

    public void drawControlPanel() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run()  {
                /*
                right_panel.add( r_control_panel );
                right_panel.setVisible( true );
                frame.setVisible( true );
                */
                right_parent_panel.add(r_control_panel);
                right_parent_panel.setVisible(true);
                frame.setVisible(true);
            }
        });
    }
    public void refreshThis() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                frame.revalidate(); // recalculate layout
                frame.repaint();    // redraw
                right_panel.revalidate();
                drawControlPanel();
            }
        });


    }

    private void initGUI( ) {
        frame = new JFrame("YotsuBrowser");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        //GraphModule mod = new GraphModule();
        frame.getContentPane().add(this.getSplitPane());
        
        frame.pack();
        frame.setVisible(true);

    }

    public String getSelectedBoard() {
        return selected_board;
    }

    public void setSelectedPage( int page_no ) {
        selected_page = page_no; 
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
