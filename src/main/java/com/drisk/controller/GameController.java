package com.drisk.controller;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import com.drisk.domain.Color;
import com.drisk.domain.GameManager;
import com.drisk.domain.MapManager;
import com.drisk.domain.MatchManager;
import com.drisk.domain.TurnManager;
import com.google.gson.JsonObject;

@Controller
public class GameController {
	
	private ExecutorService nonBlockingService = Executors.newCachedThreadPool();
	private static final String SESSION_ATTRIBUTE_COLOR = "color";
	
	@GetMapping("/map")
    public SseEmitter handleSseMap() {
		SseEmitter emitter = new SseEmitter();
		nonBlockingService.execute(() -> {
			try {
				JsonObject map = MapManager.getInstance().toJson();
				emitter.send(map);
				emitter.complete();	
			} catch (Exception ex) {
				emitter.completeWithError(ex);
			}
		});
		return emitter;
    }
	
	@GetMapping("/getColorFromSession")
	@ResponseBody
	public JsonObject getColorSession(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		if(session == null)
			return createResponseJson(-1, "You are not a player because you haven't a color assigned");
		else
			return createResponseJson(0, session.getAttribute(SESSION_ATTRIBUTE_COLOR).toString());
	}
	
	private JsonObject createResponseJson(int responseCode, String responseMessage) {
		JsonObject obj = new JsonObject();
		obj.addProperty("responseCode", responseCode);
		obj.addProperty("responseMessage", responseMessage);
		return obj;
	}
	
	@GetMapping("/turn")
    public SseEmitter handleSseTurn() {
		SseEmitter emitter = new SseEmitter();
		nonBlockingService.execute(() -> {
			try {
				emitter.send(GameManager.getInstance().toJson());
				emitter.complete();	
			} catch (Exception ex) {
				emitter.completeWithError(ex);
			}
		});
		return emitter;
    }
	
	@GetMapping("/test")
	@ResponseBody
	public String test(HttpServletRequest request) {
		HttpSession session = request.getSession(false);
		Color c = (Color) session.getAttribute(SESSION_ATTRIBUTE_COLOR);
		return c.toString();
	}
	
	
	@GetMapping(value="/nextPhase")
	@ResponseBody
	public String nextPhase() {
		MatchManager mm = MatchManager.getInstance();
		if (mm.isMatchStarted()) {
			TurnManager.getInstance().getCurrentPhase().nextPhase();
			return "Going to the next phase!";
		}
		return "Couldn't go to the next phase!";
	}
	
	
	@GetMapping(value="/playPhase")
	@ResponseBody
	public String playPhase() {
		MatchManager mm = MatchManager.getInstance();
		if (mm.isMatchStarted()) {
			TurnManager.getInstance().getCurrentPhase().startPhase();
			return "playing the phase...";
		}
		return "Couldn't play the phase!";
	}
	
}
