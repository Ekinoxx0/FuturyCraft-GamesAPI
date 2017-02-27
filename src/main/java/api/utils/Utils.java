package api.utils;

import java.io.*;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by loucass003 on 26/11/16.
 */
public class Utils
{

	public static void writeText(File f, String content)
	{

		try
		{
			FileOutputStream fOut = new FileOutputStream(f);
			OutputStreamWriter myOutWriter = new OutputStreamWriter(fOut);
			myOutWriter.append(content);
			myOutWriter.close();
			fOut.close();
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}

	}

	public static String readFile(File f)
	{
		try
		{
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
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
		return null;
	}

	public static String getContainerID()
	{
		try
		{
			InetAddress address = InetAddress.getLocalHost();
			return address.getHostName();
		}
		catch (UnknownHostException e)
		{
			throw new RuntimeException("Cannot get container ID", e);
		}
	}
}
