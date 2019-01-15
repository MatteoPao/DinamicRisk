package com.drisk.controller;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.drisk.domain.game.ColorEnum;
import com.drisk.domain.lobby.LobbyManager;
import com.drisk.domain.map.MapManager;
import com.drisk.technicalservice.JsonHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

@Controller
public class MatchController {

	private static final String SESSION_ATTRIBUTE_COLOR = "color";
	private static final String IS_NOT_A_PLAYER = "You are not a player";
	private JsonHelper helper = new JsonHelper();

	/**
	 * It allows new player to join in the match, or old player to re-join
	 * 
	 * @param request HttpServletRequest with the session if it exists, or empty if
	 *                it doesn't exist
	 * @return JsonObject with welcome message or error
	 */
	@PostMapping("/join")
	@ResponseBody
	public JsonObject join(HttpServletRequest request) {
		LobbyManager mm = LobbyManager.getInstance();
		if (mm.isMatchStarted())
			return helper.createResponseJson(-1, "The match has already started!");
		else if (mm.isMatchFull())
			return helper.createResponseJson(-1, "There are enough players!");
		else {
			HttpSession session = request.getSession(false);
			if (!isAPlayer(session)) {
				mm.joinGame(request.getParameter("name").trim());
				session = request.getSession();
				session.setAttribute(SESSION_ATTRIBUTE_COLOR, mm.findLastPlayerColor());
				return helper.createResponseJson(0, "You've joined the game!");
			} else
				return helper.createResponseJson(0, "Welcome back to joining room");
		}
	}

	/**
	 * It allows player to set his game configurations, like map difficulty,
	 * objective type ...
	 * 
	 * @param request HttpServletRequest with session and game configuration body,
	 *                with map details and modality
	 * @return JsonObject with positive message or with string error
	 */
	@PostMapping("/gameConfig")
	@ResponseBody
	public JsonObject gameConfig(HttpServletRequest request) {
		if (!isAPlayer(request.getSession(false)))
			return helper.createResponseJson(-1, IS_NOT_A_PLAYER);
		try {
			String body = request.getReader().lines().collect(Collectors.joining());
			JsonObject gameConfig = helper.parseJson(body);
			MapManager.getInstance().createMap(gameConfig);
			return helper.createResponseJson(0, "Configuration correctly added");
		} catch (JsonSyntaxException e) {
			return helper.createResponseJson(-1, "Syntax error: cannot parse json object");
		} catch (Exception e) {
			return helper.createResponseJson(-1, e.getMessage());
		}
	}

	/**
	 * It is invoked by the client only one time and it sends to it the information
	 * of the match
	 * 
	 * @return SseEmitter
	 */
	@GetMapping("/info")
	public SseEmitter handleSse() {
		SseEmitter emitter = new SseEmitter();
		JsonObject obj = LobbyManager.getInstance().toJson();
		obj.addProperty("mapReady", MapManager.getInstance().isMapReady());
		try {
			obj.addProperty("serverIp", getIp());
			obj.addProperty("serverPort", 8080);
			emitter.send(obj);
		} catch (Exception ex) {
			emitter.completeWithError(ex);
		}
		emitter.complete();
		return emitter;
	}
	
	private String getIp() {
		try(final DatagramSocket socket = new DatagramSocket()){
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			return socket.getLocalAddress().getHostAddress();
		} catch (Exception e) {
			return null;
		}
	}

	/**
	 * It allows player to leave correctly the match
	 * 
	 * @param request HttpServletRequest with player session
	 * @return JsonObject with message or with error message
	 */
	@GetMapping("/exit")
	@ResponseBody
	public JsonObject exit(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (!isAPlayer(session))
			return helper.createResponseJson(-1, IS_NOT_A_PLAYER);
		LobbyManager.getInstance().exitGame((ColorEnum) session.getAttribute(SESSION_ATTRIBUTE_COLOR));
		session.invalidate();
		return helper.createResponseJson(0, "You've exited from the game!");
	}

	/**
	 * Try to start the game when one player leave the match or when one player is
	 * ready
	 */
	private void tryToStartGame() {
		LobbyManager mm = LobbyManager.getInstance();
		if (mm.isEveryoneReady() && MapManager.getInstance().isMapReady() && mm.areThereAtLeastTwoPlayers())
			LobbyManager.getInstance().initGame();
	}

	/**
	 * It allows player to set his status to ready
	 * 
	 * @param request HttpServletRequest with player session
	 * @return JsonObject with message or error message
	 */
	@GetMapping("/ready")
	@ResponseBody
	public JsonObject ready(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (!isAPlayer(session))
			return helper.createResponseJson(-1, IS_NOT_A_PLAYER);
		LobbyManager.getInstance().setPlayerReady((ColorEnum) session.getAttribute(SESSION_ATTRIBUTE_COLOR), true);
		tryToStartGame();
		return helper.createResponseJson(0, "The game will start when everyone is ready!");
	}

	/**
	 * It allows player to set his status to not ready
	 * 
	 * @param request HttpServletRequest with player session
	 * @return JsonObject with message or error message
	 */
	@GetMapping("/notready")
	@ResponseBody
	public JsonObject notReady(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if (!isAPlayer(session))
			return helper.createResponseJson(-1, IS_NOT_A_PLAYER);
		LobbyManager.getInstance().setPlayerReady((ColorEnum) session.getAttribute(SESSION_ATTRIBUTE_COLOR), false);
		return helper.createResponseJson(0, "The game will start when everyone is ready!");
	}

	/**
	 * Check if session belongs to a player or not
	 * 
	 * @param session - HttpSession with the color of the player
	 * @return true if the session is not null and this player is in LobbyManager
	 *         players list, false otherwise
	 */
	private boolean isAPlayer(HttpSession session) {
		return session != null && LobbyManager.getInstance()
				.findPlayerByColor((ColorEnum) session.getAttribute(SESSION_ATTRIBUTE_COLOR)) != null;
	}

}
