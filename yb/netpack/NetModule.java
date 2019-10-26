package yb.netpack;

import java.net.*;
import java.io.*;
import yb.ChanBrowser;

public class NetModule
{
    public Integer saveLinkToFile (String url, String save_dir) throws IOException {

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
            String fname = url.substring(url.lastIndexOf("/") + 1, url.length());

            InputStream istream = chan_conn.getInputStream();
            String file_path = save_dir + File.separator + fname;
            
            FileOutputStream ostream = new FileOutputStream(file_path);

            while ((bytes_read = istream.read(buffer)) != -1) {
                ostream.write(buffer, 0, bytes_read);
            }

            istream.close();
            ostream.close();

        }

        return 1;
    }

}
