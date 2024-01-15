/**
 * 用于处理单元的配置文件，目前只是打印lib中的文件，需手工拷到MANIFEST.MF的classpath中
 */

/**
 * 打印指定目入的文件
 * @param filePath
 * @return
 */
def listFile(String filePath){
    println "************************************"
    File fileDir = new File(filePath);
    println fileDir
    if(!fileDir.isDirectory()){
        return;
    }
    File[] files1 = fileDir.listFiles();
    for(File file:files1){
        println("lib/"+file.name+",");
    }
    println "************************************"
}

listFile("./target/classes/lib")