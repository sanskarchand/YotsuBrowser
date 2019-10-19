package yb.utilpack;

import javax.json.Json;
import javax.json.JsonReader;
import javax.json.JsonValue;
import javax.json.JsonObject;
import javax.json.JsonArray;
import java.io.IOException;
import java.net.URL;
import java.io.InputStream;
import java.io.FileInputStream;
import java.util.List;
import java.util.ArrayList;

import yb.ChanBrowser;
import yb.ThreadModel;

import java.lang.NullPointerException;  // nurupo

public class ParseMod {
    
    
    public List<String> getBoardsList() { //throws IOException {

        List<String> board_names = new  ArrayList<String>();
        InputStream boards_is = null;

        try {
            boards_is = new FileInputStream( yb.ChanBrowser.PATH_JSON + "/" + yb.ChanBrowser.FILE_BOARDS );
        } catch ( IOException e ) {
            System.out.println(e.getMessage());
        }

        /*
        if ( boards_is != null ) {
            exit
        }
        */

        JsonReader boards_reader = Json.createReader( boards_is );
        JsonObject boards_object = boards_reader.readObject();

        boards_reader.close();

        JsonArray boards_array = boards_object.getJsonArray("boards");

        for ( JsonValue b_val : boards_array ) {
            JsonObject board_object = (JsonObject) b_val; 
            //board_names.add( board_object.getString("board") + " - " + board_object.getString("title") );
            board_names.add( board_object.getString("board") );

            //_DEBUG System.out.println( board_names.get(board_names.size() - 1) );
        }

        return board_names;

    }


    public List<Integer>  getThreadNumbers( String file ) {

        List<Integer> thread_nos = new ArrayList<Integer>();
        InputStream is = null;

        try {
            is  =  new FileInputStream( file );
        } catch ( IOException e ) {
            System.out.println( "pmod:" + e.getMessage() );
        }

        // if  is != null, exit
        JsonReader rd =  Json.createReader( is );
        JsonArray array = rd.readArray();
        rd.close();

        for ( JsonValue page_val : array ) {
            JsonObject this_page = (JsonObject) page_val;
            JsonArray my_threads = this_page.getJsonArray("threads");

            for ( JsonValue t_val : my_threads ) {

                JsonObject thread_obj = (JsonObject) t_val;
                thread_nos.add( thread_obj.getInt("no") );
            }

        }

        return thread_nos;

    }

    public List<ThreadModel> getInitialThreads( String board_name, String catalog_file ) {
        
        List<ThreadModel> init_threads = new ArrayList<ThreadModel>();
        InputStream is = null;

        try {
            is  =  new FileInputStream( catalog_file );
        } catch ( IOException e ) {
            System.out.println( "pmod:" + e.getMessage() );
        }

        // if  is != null, exit
        JsonReader rd =  Json.createReader( is );
        JsonArray array = rd.readArray();
        rd.close();

        for ( JsonValue page_val : array ) {
            JsonObject this_page = (JsonObject) page_val;
            
            int page_no  = this_page.getInt("page");
            JsonArray my_threads = this_page.getJsonArray("threads");

            for ( JsonValue t_val : my_threads ) {

                JsonObject thread_obj = (JsonObject) t_val;

                int thread_no = thread_obj.getInt("no");
                
                //this can be empty
                String thread_subject = null;
                try {
                    thread_subject = thread_obj.getString("sub"); 
                } catch ( NullPointerException e ) {
                    thread_subject = "";
                }
                String thread_timestamp = thread_obj.getString("now");

                //this can be empty
                String thread_comment = null;
                try {
                    thread_comment = thread_obj.getString("com");
                } catch ( NullPointerException e ) {
                    thread_comment = ""; 
                }

                // extract secondary data
                long img_no = thread_obj.getJsonNumber("tim").longValue();
                String img_fname = thread_obj.getString("filename");
                String img_ext = thread_obj.getString("ext");

                ThreadModel temp_tm = new ThreadModel(  board_name,  page_no,
                                            thread_no, thread_subject, thread_timestamp, thread_comment );
                
                temp_tm.setTopImage( img_no, img_fname, img_ext );


                init_threads.add( temp_tm );
             
            }

        }


        return init_threads;
    }

}
