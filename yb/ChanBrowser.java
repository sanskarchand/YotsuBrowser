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


    private static CatalogModel cur_catalog;
    private static String cur_board_name;

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

        for ( ThreadModel thread_model : thread_list ) {
            System.out.println("adding threads to catalog");
            gmod.addCatalogThread( thread_model.getThreadSubject(), thread_model.getThreadImageLocalFname() ); 
        }

        gmod.refreshThis();

    }

    private static void mainLoop() {

        String tmp_bname = gmod.getSelectedBoard();

        System.out.println("Selected board = " + tmp_bname );

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
