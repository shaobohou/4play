package net.swiftkey.fourplay;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import com.google.gson.Gson;

/**
 * Client stub for the tournament server.
 * All functions are synchronous and blocking.
 */
public class ServiceStub {
	
	private String mHost;
	private int mPort;
	
	public ServiceStub(String host, int port) {
		mHost = host;
		mPort = port;
	}
	
	/**
	 * Start a new game with the tournament server. Returns
	 * the game ID, or -1 if the server isn't cooperating.
	 * 
	 * @param playerName Give the player a name for reporting.
	 */
	public int newGame(String playerName) {
		int id = -1;
		Response r = request("new-game", null, NewGameResponse.class);
		if (r instanceof NewGameResponse) {
			id = ((NewGameResponse) r).id;
		}
		
		return id;
	}

	/**
	 * Check the status of the given game.
	 * 
	 * @param gameId The game ID to check with the server.
	 */
	public GameState poll(int gameId) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", Integer.toString(gameId)));
		
		Response r = request("poll", params, PollResponse.class);
		if (r instanceof PollResponse) {
			PollResponse pr = (PollResponse) r;
			return new GameState(pr.state, pr.board.state, pr.board.rows, pr.board.cols);
		} else {
			return null;
		}
	}
	
	/**
	 * Place a token to make your move!
	 * 
	 * @param gameId game to make a move in.
	 * @param column column (0-based index) to place a token, in the
	 *                current game 
	 */
	public void move(int gameId, int column) {
		List<NameValuePair> params = new ArrayList<NameValuePair>();
		params.add(new BasicNameValuePair("id", Integer.toString(gameId)));
		params.add(new BasicNameValuePair("move-index", Integer.toString(column)));
		httpPost("move", params);
	}
	
	private String makeUrl(String endPoint) {
		return String.format("http://%s:%d/%s", mHost, mPort, endPoint);
	}
	
	private Response request(
			String endPoint, 
			List<NameValuePair> params, 
			Class<? extends Response> responseClass) {
		
		Gson gson = new Gson();
		HttpEntity entity = httpPost(endPoint, params);
		Response r = new BadThing();
		
		InputStream content;
		try {
			content = entity.getContent();
			r = gson.fromJson(new InputStreamReader(content), responseClass);
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return r;
	}
	
	private HttpEntity httpPost(String endPoint, List<NameValuePair> params) {
		DefaultHttpClient client = new DefaultHttpClient();
		HttpPost post = new HttpPost(makeUrl(endPoint));		
		HttpResponse response = null;
		
		try {
			if (params != null) {
				post.setEntity(new UrlEncodedFormEntity(params));
			}
			
			response = client.execute(post);
		} catch (ClientProtocolException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		return response.getEntity();
	}
	
	private class Response {}
	private class BadThing extends Response {}
	
	private class NewGameResponse extends Response {
		public int id;
	}
	
	private class BoardState {
		int rows;
		int cols;
		int[] state;
	}
	
	private class PollResponse extends Response {
		public String state;
		public BoardState board;
	}
}
