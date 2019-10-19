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
    
    public static final String URL_BOARDS = "http://a.4cdn.org/boards.json";
    public static final String PATH_JSON = "yb/json";
    public static final String FILE_BOARDS = "boards.json";
    public static final String URL_BASE = "http://a.4cdn.org/";
    public static final String URL_IMG_BASE = "http://i.4cdn.org/";
    public static final String PATH_TEMP = "yb/tmp";
    public static final String PATH_TEMP_IMG = "yb/tmp/img";


    public static final int THUMB_SIZE_X = 64;
    public static final int THUMB_SIZE_Y = 64;


    private static CatalogModel cur_catalog;
    private static String cur_board_name;
    
    private static String DISPLAY_MODE;
    private static int cur_page_no;

    private static ParseMod pmod;
    private static NetModule nmod;
    private static GraphModule gmod;


    private static void initApplication() {

        nmod = new NetModule();

        cur_catalog = new CatalogModel();
        cur_board_name = "<UNDEF>";
        
        //STEP: save boards file
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

        // convert List<String> to String[]
        // I coould have used the same format for both
        
        gmod = new GraphModule( board_names.toArray(new String[board_names.size()]) );
            
        gmod.runGUI();  // using EDT
        
    }

    private static List<ThreadModel> getAllThreads( String board_name, String catalog_file_path ) {

        List<ThreadModel> thread_list = new ArrayList<ThreadModel>();


        //List<Integer> threadno_list = pmod.getThreadNumbers( catalog_file_path );
        

        thread_list = pmod.getInitialThreads( board_name, catalog_file_path );

        return thread_list;
    
    }
    
    private static int downloadThreadImage( String board_name, String local_fname ) {
        
        String img_url =  URL_IMG_BASE +  board_name + "/" + local_fname;
        System.out.println("requesting: " + img_url);

        try {
            nmod.saveLinkToFile( img_url, PATH_TEMP_IMG );
        } catch ( IOException e ) {

            System.out.println("ChanBrowser: downloadThreadImage: " + e.getMessage());
            return -1;
        }

        return 1;
    }

    private static void actuallyDisplayCatalog( CatalogModel cat, List<ThreadModel> init_threads, int page_no ) {
        
        // need to delete previous content, too
        gmod.deletePreviousContent();

        for ( ThreadModel thread_model : init_threads ) {
            if ( thread_model.getPageNo() != page_no ) { continue; }
            
            System.out.println("adding thread to catalog");

            int success = downloadThreadImage( cat.getBoardName(), thread_model.getThreadImageLocalFname() );
            gmod.addCatalogThread( thread_model.getThreadSubject(), thread_model.getThreadImageLocalFname() ); 
        }
        
        gmod.refreshThis();

    }

    private static void populateCatalog( CatalogModel cat ) {
        
        String board_name = cat.getBoardName();
        String file_path = null;
        List<ThreadModel> thread_list = null;

        try {
            file_path = PATH_TEMP;
            nmod.saveLinkToFile(URL_BASE + board_name + "/catalog.json", file_path);
            System.out.println("ChanBrowser nmod: catalog downloaded for " + board_name);
        } catch (IOException e) {
            System.out.println("ChanBrowser: CatalogDLError: " + e.getMessage());
        }

        if ( file_path != null ) {
            thread_list = pmod.getInitialThreads( board_name, file_path + "/catalog.json");
            
        }
        
        // display first page only
        System.out.println("Displaying the first page");

        //NOTABENE: surely cur_catalog could replace this?
        actuallyDisplayCatalog( cat, thread_list, 1 );
    }

    private static void mainLoop() {

        String tmp_bname = gmod.getSelectedBoard();

        System.out.println("Selected board = " + tmp_bname + "---Current board = " + cur_board_name + "---");

        if ( tmp_bname != "<UNDEF>" && tmp_bname != cur_board_name) {
                
            //_DEBUG System.out.println("ACTIVATED");
            // change the catalog ( create a new catalog ) 
            cur_catalog = new CatalogModel();
            cur_catalog.setBoardName( tmp_bname );
            populateCatalog( cur_catalog );

            cur_board_name = tmp_bname;
        }

    }

    public static void main( String[] args ) {
        
        initApplication();

        while (true) {
            mainLoop();
        }

    }
}
