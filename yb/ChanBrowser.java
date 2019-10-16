package yb;

import yb.netpack.NetModule;
import yb.utilpack.ParseMod;

//import GraphPack.GraphModule;
import java.io.IOException;

public class ChanBrowser {
    
    public static final String URL_BOARDS = "http://a.4cdn.org/boards.json";
    public static final String PATH_JSON = "yb/json";

    public static void main(String[] args) {

        NetModule nmod = new NetModule();
        
        //STEP: save boards file
        try {
            nmod.saveLinkToFile(URL_BOARDS, PATH_JSON);
        } catch (IOException e) {
            System.out.println(e.getMessage());
        }

        ParseMod pmod = new ParseMod();
        pmod.callMe();
        //STEP: parse board names from file
        //GraphModule gmod = new GraphModule(

    }
}
