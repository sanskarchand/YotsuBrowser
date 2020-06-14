package yb;

import yb.PostModel;
import java.util.List;
import java.util.ArrayList;

public class ThreadModel {
    
    // primary properties
    private int m_thread_no;
    public String m_subject;
    public String m_timestamp;
    private int m_page_no;
    private String m_thread_post; // toplevel comment
    private String m_board;
    
    // secondary properties
    private long m_image_no;
    private String m_img_filename;
    private String m_img_ext;

    public List<PostModel> m_posts;

    public ThreadModel( String board, int page_no, int thread_no, String subject, String now, String com ) {
        m_posts = new ArrayList<PostModel>();
        
        m_board = board;
        m_page_no = page_no;
        m_thread_no = thread_no;
        m_subject = subject;
        m_timestamp = now;
        m_thread_post = com;

        m_image_no = -1;
        m_img_filename = null;
        m_img_ext = null;
    }

    public void  setTopImage( long image_no, String i_fname, String i_ext ) {
        m_image_no = image_no;     
        m_img_filename = i_fname;
        m_img_ext = i_ext;
    }

    public void addPost( PostModel p )
    {
        m_posts.add( p );

    }

    public String getThreadPost() {
        return m_thread_post;
    }

    public String getThreadSubject() {
        return m_subject;
    }

    public int getPageNo() {
        return m_page_no;
    }

    public int getThreadNo() {
        return m_thread_no;
    }

    public String getThreadImageLocalFname() {
        if ( m_image_no == -1 ) {
            return "<UNDEF>";
        } 
        return Long.toString(m_image_no) + m_img_ext; 
    }

    public String getThreadThumbnailLocalFname() {
        if (m_image_no == -1) {
            return "<UNDEF>"; 
        }

        return Long.toString(m_image_no) + "s" + m_img_ext;
    }
    
}
