package yb.netpack;

import java.net.*;
import java.io.*;
import yb.ChanBrowser;

public class NetModule
{
    public Integer saveLinkToFile (String url, String save_dir, String file_name) throws IOException {


        ChanBrowser.DEBUG_PRINT("NetMod: URL: save to file: " + url);

        URL chan_url = null;
        HttpURLConnection chan_conn = null;

        final int BUFFER_SIZE = 1024;
        int bytes_read = -1;
        byte[] buffer=  new byte[BUFFER_SIZE];
        

        //NOTABENE: should use if-modified-since 
        chan_url = new URL(url);
        chan_conn = (HttpURLConnection) chan_url.openConnection(); 
        int resp_code = chan_conn.getResponseCode();

        ChanBrowser.DEBUG_PRINT("Req-code: " + resp_code);

        if (resp_code == HttpURLConnection.HTTP_OK) {

            InputStream istream = chan_conn.getInputStream();
            String file_path = save_dir + File.separator + file_name;
            
            FileOutputStream ostream = new FileOutputStream(file_path);

            while ((bytes_read = istream.read(buffer)) != -1) {
                ostream.write(buffer, 0, bytes_read);
            }

            istream.close();
            ostream.close();

        } else {
            /* most likely 404 */
            return resp_code;
        }

        return 1;
    }

    // overloaded
    public Integer saveLinkToFile(String m_URL, String m_save_dir) throws IOException {
       
        String fname = m_URL.substring(m_URL.lastIndexOf("/") + 1, m_URL.length());
        return saveLinkToFile(m_URL, m_save_dir, fname);

    }

}
