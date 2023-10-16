import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class DTest {
    static String datasStr = "2022-05-12 23:04:40\n" +
            "2022-05-12 08:31:19\n" +
            "2022-05-11 23:45:36\n" +
            "2022-05-11 23:02:43\n" +
            "2022-05-11 19:44:44\n" +
            "2022-05-11 08:34:09\n" +
            "2022-05-10 23:56:42\n" +
            "2022-05-10 23:04:05\n" +
            "2022-05-10 19:46:49\n" +
            "2022-05-10 08:07:42\n" +
            "2022-05-09 23:54:22\n" +
            "2022-05-09 23:03:19\n" +
            "2022-05-09 19:43:42\n" +
            "2022-05-09 08:31:45\n" +
            "2022-05-08 23:59:29\n" +
            "2022-05-08 23:02:14\n" +
            "2022-05-08 19:40:01\n" +
            "2022-05-07 23:57:51\n" +
            "2022-05-07 23:02:40\n" +
            "2022-05-07 19:41:40";
    public static void main(String[] args) throws ParseException {
        String[] datas = datasStr.split("\n");
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        List<Long> times = new ArrayList<>();
        for(String dataStr:datas){
            times.add(dateFormat.parse(dataStr).getTime());
            //System.out.println(dateFormat.parse(dataStr).getTime() - 1652367880000L);
        }
        //times.sort(new Comparator<Long>() {  public int compare(Long a, Long b) { return a<b?1:0;  } });

        dateFormat = new SimpleDateFormat("HH.mmss");
        Collections.sort(times);
        for(Long time:times){
            Double tempTime = Double.valueOf(time-times.get(0));
            if(tempTime > 0){
                tempTime = tempTime/1000/1000/60;
            }
            System.out.print("{"+tempTime+","+dateFormat.format(new Date(time))+"},");
        }
    }
}
