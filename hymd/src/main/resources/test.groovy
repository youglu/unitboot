println "test"

def listFile(String filePath){
    println "print file"
    File fileDir = new File(filePath);
    println fileDir
    if(!fileDir.isDirectory()){
        return;
    }
    File[] files1 = fileDir.listFiles();
    for(File file:files1){
        println("lib/"+file.name+",");
    }
}

listFile("./target/classes/lib")