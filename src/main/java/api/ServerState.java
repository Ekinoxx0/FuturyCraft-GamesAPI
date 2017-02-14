package api;

/**
 * Created by SkyBeast on 14/02/17.
 */
public enum ServerState
{
	/**
	 * The server is starting
	 */
	STARTING,

	/**
	 * The server is started
	 */
	STARTED,

	/**
	 * The server is ready to accept players
	 */
	READY,

	/**
	 * NON-LOBBY
	 * The game is waiting for players
	 */
	WAITING_PLAYERS,

	/**
	 * NON-LOBBY
	 * The game is started
	 */
	GAME_STARTED,

	/**
	 * NON-LOBBY
	 * The game is ended but the server is not stopping yet
	 */
	GAME_ENDED,

	/**
	 * The server is stopping because of an error
	 */
	STOPPING_ERROR,

	/**
	 * The server is auto-stopping
	 */
	STOPPING,

	/**
	 * The server is stopping because we told him to do
	 */
	STOPPING_ASKED,

	/**
	 * The server is stopped. Actually not sent but updated.
	 */
	STOPPED
}
