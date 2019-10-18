package yb;

import yb.ThreadModel;
import java.util.List;

public class CatalogModel {

    
    private String m_board_name; 
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

    public String getBoardName() {
        return m_board_name;
    }

    // re-set data from parser
    public void updateModel() {

    }
}

