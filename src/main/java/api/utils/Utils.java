package api.utils;

import com.google.common.primitives.Primitives;

import java.io.*;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by loucass003 on 26/11/16.
 */
public class Utils
{

	public static final String NMS_PACKAGE = "net.minecraft.server";

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

	public static Integer isNumeric(String str)
	{
		try
		{
			return Integer.valueOf(str);
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static <T> T getField(String fieldName, Class clazz, Object instance, Class<T> to, boolean isPrivate)
	{
		try
		{
			Field field = clazz.getDeclaredField(fieldName);
			if (isPrivate)
				field.setAccessible(true);
			return Primitives.wrap(to).cast(field.get(instance));
		}
		catch (NoSuchFieldException | IllegalAccessException e)
		{
			e.printStackTrace();
		}

		return null;
	}

	public static <T> T getPrivateField(String fieldName, Class clazz, Class<T> to)
	{
		return getField(fieldName, clazz, null, to, true);
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
