package api.utils;

import java.io.*;
/**
 * Created by loucass003 on 26/11/16.
 */
public class Utils
{

    public static void writeText(File f, String content) {

        try {
            FileOutputStream fOut = new FileOutputStream(f);
            OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
            myOutWriter.append(content);
            myOutWriter.close();
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public static String readFile(File f)
    {
        try {
            BufferedReader br = new BufferedReader(new FileReader(f));
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null)
            {
                sb.append(line);
                line = br.readLine();
            }

            String everything = sb.toString();
            br.close();
            return everything;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Integer isNumeric(String str)
    {
        try
        {
            return Integer.valueOf(str);
        } catch (Exception e) {
            return null;
        }
    }
}
