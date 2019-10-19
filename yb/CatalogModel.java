package yb;

import yb.ThreadModel;
import java.util.List;

public class CatalogModel {

    
    private String m_board_name; 
    private String m_board_title;
    public List<ThreadModel> m_threads;

    public CatalogModel() {
        m_board_name = "<UNDEF>";
    }

    public void addThread( ThreadModel tm ) {
        m_threads.add( tm );
    }

    public void setBoardName( String  bname ) {
        m_board_name = bname;
    }

    public void setBoardTitle( String btitle ) {
        m_board_title = btitle; 
    }

    public String getBoardName() {
        return m_board_name;
    }

    public String getBoardTitle() {
        return m_board_title;
    }

    // re-set data from parser
    public void updateModel() {

    }
}

