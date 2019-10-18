package yb;

public class PostModel{

    public int m_post_no;
    public String m_now;
    public String m_com;
    public int m_tim;   // image no.
    public String m_image_fname;
    public String m_image_ext;

    
    // not going to bother with getters and setters for this
    

    public PostModel( int p, String n, String c) {

        m_post_no = p;
        m_now = n;
        m_com = c;
    }

    public PostModel( int p, String n, String c, int tim, String fname, String ext) {

        m_post_no = p;
        m_now = n;
        m_com = c;
        m_tim = tim;
        m_image_fname = fname;
        m_image_ext = ext;
    }



}
