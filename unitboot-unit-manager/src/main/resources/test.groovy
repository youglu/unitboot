def listFile(String filePath){
    println "************************************"
    File fileDir = new File(filePath);
    if(!fileDir.isDirectory()){
        return;
    }
    File[] files1 = fileDir.listFiles();
    for(File file:files1){
        println("lib/"+file.name+",");
    }
    println "************************************"
}
def addToBundleList(){
    println "addToBundleList************************************"
    // File fileDir = new File("../unitboot-1.0/unit/spring/");
    File target = new File("./target");
    if(!target.isDirectory()){
        return;
    }
    File[] files1 = target.listFiles();
    for(File file:files1){
        if(file.name.endsWith(".jar"))
        println("reference:file:../unit/spring/"+file.name+"@6\\:start,\\");
    }
    println "************************************"
}

listFile("./target/classes/lib")
addToBundleList()