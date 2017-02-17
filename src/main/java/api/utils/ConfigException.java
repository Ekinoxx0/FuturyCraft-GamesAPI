package api.utils;

/**
 * Created by SkyBeast on 17/02/17.
 */
public class ConfigException extends RuntimeException
{
	/**
	 * {@inheritDoc}
	 */
	public ConfigException() {}

	/**
	 * {@inheritDoc}
	 */
	public ConfigException(String var1) {super(var1);}

	/**
	 * {@inheritDoc}
	 */
	public ConfigException(String var1, Throwable var2) {super(var1, var2);}

	/**
	 * {@inheritDoc}
	 */
	public ConfigException(Throwable var1) {super(var1);}
}
