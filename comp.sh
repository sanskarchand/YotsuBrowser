#!/bin/bash

javac -cp .:/usr/share/java/javax.json-1.0.jar yb/ChanBrowser.java yb/CatalogModel.java yb/ThreadModel.java yb/PostModel.java yb/netpack/NetModule.java yb/graphpack/GraphModule.java yb/utilpack/ParseMod.java  yb/utilpack/FilterMod.java -Xlint:unchecked
