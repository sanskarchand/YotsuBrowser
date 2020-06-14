package yb;

import yb.netpack.NetModule;
import yb.utilpack.ParseMod;
import yb.graphpack.GraphModule;
import yb.CatalogModel;
import yb.ThreadModel;

//import GraphPack.GraphModule;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.ArrayList;

public class ChanBrowser {
    
    public static final boolean DEBUG = true;
    public static final String URL_BOARDS = "https://a.4cdn.org/boards.json";
    public static final String PATH_JSON = "yb/json";
    public static final String FILE_BOARDS = "boards.json";
    public static final String FILE_CATALOG = "catalog.json";
    public static final String URL_BASE = "https://a.4cdn.org/";
    public static final String URL_IMG_BASE = "https://i.4cdn.org/";
    public static final String URL_THUMB_BASE = "https://t.4cdn.org/";
    public static final String PATH_TEMP = "yb/tmp/";
    public static final String PATH_TEMP_IMG = "yb/tmp/img/";
    
    public static final String PATH_NO_THUMB_IMG = "yb/res/yb_no_thumb.png";

    public static final int THUMB_SIZE_X = 64;
    public static final int THUMB_SIZE_Y = 64;
    public static final int PREVIEW_LIMIT = 84;
    
    public static final int POLICY_USE_IMG_FOR_THUMB = 1;

    private CatalogModel cur_catalog;
    private String cur_board_name;
    
    private String DISPLAY_MODE;
    private int cur_page_no;

    private ParseMod pmod;
    private NetModule nmod;
    private GraphModule gmod;
    
    public static void DEBUG_PRINT( String str ) {
        if ( DEBUG ) {
            System.out.println( str );
        }
    }

    private void initApplication() {

        nmod = new NetModule();

        cur_catalog = new CatalogModel();
        cur_board_name = "<UNDEF>";
        
        File f = new File(PATH_JSON + "/" + FILE_BOARDS);
        if ( !f.exists() ) {
            try {
                nmod.saveLinkToFile(URL_BOARDS, PATH_JSON);
            } catch (IOException e) {
                System.out.println("ChanBrowser: downloadError: " + e.getMessage());
            }
        }

        pmod = new ParseMod();
        List<String> board_names = pmod.getBoardsList();

        gmod = new GraphModule( board_names.toArray(new String[board_names.size()]), this );
        gmod.runGUI();  // using EDT
        
    }

    private List<ThreadModel> getAllThreads( String board_name, String catalog_file_path ) {
        
        List<ThreadModel> thread_list = new ArrayList<ThreadModel>();
        //List<Integer> threadno_list = pmod.getThreadNumbers( catalog_file_path );

        return thread_list;
    }
    
    /* Download image */
    private int downloadThreadImage( String board_name, String local_fname ) {
        
        String img_url =  URL_IMG_BASE +  board_name + "/" + local_fname;
        String img_direc = PATH_TEMP_IMG + board_name + "/";

        File directory = new File(img_direc);
        if (! directory.exists()){
            directory.mkdir();
        }

        DEBUG_PRINT ("requesting: " + img_url);

        try {
            int status = nmod.saveLinkToFile( img_url, img_direc);

            if (status == 404) {
                // apply POLICY_USE_IMG_FOR_THUMB
                ;
            }
        } catch ( IOException e ) {
            System.out.println("ChanBrowser: downloadThreadImage: " + e.getMessage());
            return -1;
        }

        return 1;
    }

    /* Download thumbnail: different URL */
    private int downloadThreadThumbnail( String board_name, String local_fname ) {
        /* defunct? */
        String img_url =  URL_THUMB_BASE +  board_name + "/" + local_fname;
        DEBUG_PRINT ("requesting: " + img_url);

        try {
            nmod.saveLinkToFile( img_url, PATH_TEMP_IMG );
        } catch ( IOException e ) {
            System.out.println("ChanBrowser: downloadThreadImage: " + e.getMessage());
            return -1;
        }

        return 1;
    }


    private void actuallyDisplayCatalog( CatalogModel cat, List<ThreadModel> init_threads, int page_no ) {
        
        // need to delete previous content, too
        gmod.deletePreviousContent();
        gmod.changeStatus("Downloading init threads...");
        gmod.refreshThis();

        for ( ThreadModel thread_model : init_threads ) {
            if ( thread_model.getPageNo() != page_no ) { continue; }
            
            DEBUG_PRINT("adding thread to catalog");
            
            //int success = downloadThreadImage( cat.getBoardName(), thread_model.getThreadImageLocalFname() );
            int success = downloadThreadImage(cat.getBoardName(), thread_model.getThreadThumbnailLocalFname());
            /*
            gmod.addCatalogThread( thread_model.getThreadNo(), thread_model.getThreadSubject(), 
                                    thread_model.getThreadImageLocalFname() ); 
            */
            
            gmod.addCatalogThread(thread_model.getThreadNo(), thread_model.getThreadSubject(),
                                    thread_model.getThreadPost(),
                                    thread_model.getThreadThumbnailLocalFname());
            System.out.println("adding " + thread_model.getThreadSubject());
            gmod.setSelectedPage( page_no );
        }
        
        //gmod.refreshThis();

    }

    private void populateCatalog( CatalogModel cat ) {
        
        String board_name = cat.getBoardName();
        String catalog_url = URL_BASE + board_name + "/" + FILE_CATALOG;
        
        String file_name = "catalog_" + board_name + ".json";
        String save_dir = PATH_TEMP;
        String file_path = save_dir + file_name;

        
        List<ThreadModel> thread_list = null;
        
        
        // NOTABENE: Use If-Modified-Since
        File  f = new File(file_path);
        if (f.isFile()) {
            f.delete();
        } 

        try {
            nmod.saveLinkToFile(URL_BASE + board_name + "/" + FILE_CATALOG, save_dir, file_name);
            System.out.println("ChanBrowser nmod: catalog downloaded for " + board_name);
        } catch (IOException e) {
            System.out.println("ChanBrowser: CatalogDLError: " + e.getMessage());
        }

        if ( file_path != null ) {
            thread_list = pmod.getInitialThreads( board_name, file_path);
            
        }
        
        // display first page only
        System.out.println("Displaying the first page");
        
        //NOTABENE: surely cur_catalog could replace this?
        actuallyDisplayCatalog( cat, thread_list, 1 );
    }



    public void boardChanged(String new_board) {

        //String tmp_bname = gmod.getSelectedBoard();
        String tmp_bname = new_board;
        System.out.println("Selected board = " + tmp_bname + "---Current board = " + cur_board_name + "---");
        
        cur_catalog = new CatalogModel();
        cur_catalog.setBoardName( tmp_bname );
        populateCatalog( cur_catalog );

        cur_board_name = tmp_bname;

    }

    public static void main( String[] args ) {
        ChanBrowser app = new ChanBrowser(); 
        app.initApplication();
        /*
        while ( true ) {
            app.boardChanged();
        }
        */

    }
}
