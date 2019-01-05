package com.drisk.controller;


import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.drisk.domain.Color;
import com.drisk.domain.MatchManager;
import com.drisk.technicalservice.JsonHelper;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

@Controller
public class MatchController {
	
    private ExecutorService nonBlockingService = Executors.newCachedThreadPool();
    private static final String SESSION_ATTRIBUTE_COLOR = "color";
	
	@PostMapping(value="/join")
	@ResponseBody
	public synchronized JsonObject join(HttpServletRequest request) {
		MatchManager mm = MatchManager.getInstance();
		if(mm.isMatchStarted())
			return JsonHelper.createResponseJson(-1, "The match has already started!");
		else if(mm.isMatchFull())
			return JsonHelper.createResponseJson(-1, "There are enough players!");
		else {
			HttpSession session = request.getSession(false);
			if(session == null) {
				mm.joinGame(request.getParameter("name").trim());
				session = request.getSession();
				session.setAttribute(SESSION_ATTRIBUTE_COLOR, mm.findLastPlayerColor());
				return JsonHelper.createResponseJson(0, "You've joined the game!");
			}
			else
				return JsonHelper.createResponseJson(0, "Welcome back to joining room");
		}
	}
	
	@PostMapping("/gameConfig")
	@ResponseBody
	public JsonObject gameConfig(HttpServletRequest request) {
		try {
			String body = request.getReader().lines().collect(Collectors.joining());
			MatchManager.getInstance().setGameConfig(JsonHelper.parseJson(body));
			return JsonHelper.createResponseJson(0, "gameConfig correctly parsed");
		} 
		catch (JsonSyntaxException e)
		{
			return JsonHelper.createResponseJson(-1, "Syntax error: cannot parse json object");
		}
		catch (Exception e) {
			return JsonHelper.createResponseJson(-1, e.getMessage());
		}
	}
    	     
	@GetMapping("/info")
    public SseEmitter handleSse() {
		SseEmitter emitter = new SseEmitter();
		nonBlockingService.execute(() -> {
			try {
				emitter.send(MatchManager.getInstance().toJson());
				emitter.complete();	
			} catch (Exception ex) {
				emitter.completeWithError(ex);
			}
		});
		return emitter;
	}
	
	@GetMapping(value="/exit")
	@ResponseBody
	public synchronized String exit(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		MatchManager.getInstance().exitGame((Color) session.getAttribute(SESSION_ATTRIBUTE_COLOR));
		session.invalidate();
		return "You've exited from the game!";
	}
	
	private synchronized void tryToStartGame(){
		MatchManager mm = MatchManager.getInstance();
		if (mm.isEveryoneReady() && mm.isGameConfigured() && mm.areThereAtLeastTwoPlayers())
			MatchManager.getInstance().initGame();
	}
	
	@GetMapping(value="/ready")
	@ResponseBody
	public synchronized JsonObject ready(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		MatchManager.getInstance().setPlayerReady((Color) session.getAttribute(SESSION_ATTRIBUTE_COLOR), true);
		tryToStartGame();
		return JsonHelper.createResponseJson(0, "The game will start when everyone is ready!");
	}
	
	@GetMapping(value="/notready")
	@ResponseBody
	public synchronized JsonObject notReady(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		MatchManager.getInstance().setPlayerReady((Color) session.getAttribute(SESSION_ATTRIBUTE_COLOR), false);
		return JsonHelper.createResponseJson(0, "The game will start when everyone is ready!");
	}
	
}
