var source = null; // used to open Server Sent Event connection
var warningAlreadyDisplayed = false;

$(document).ready(
		function() {

			$("#joinButton").click(function() {
				joinGame();
			});
			
			$("span.close").click(function() {
				$("#modalWindow").css("display", "none");
			});
			
			$("#modifyButton").click(function() {
				applyMapChanges();
			});
			
			$("#customRadio").click(function() {
				$("#difficultyDiv").attr("class", "hidden");
			});
			
			$("#defaultRadio").click(function() {
				$("#difficultyDiv").attr("class", "shown");
			});

			$("#exitButton").click(function() {
				exitGame();
			});

			$("#readyButton").click(function() {
				setReady();
			});

			$("#notReadyButton").click(function() {
				setNotReady();
			});

			$("#textName").keyup(function() {
				var name = $("#textName").val();
				if (name.trim() != "")
					$('#joinButton').removeAttr("disabled");
				else
					$('#joinButton').attr("disabled", "disabled");
			});

			function setReady() {
				$.get("./match/ready", function(result) {
						$("div.modal-body").html("<h2>" + result.responseMessage + "</h2>");
						$("#modalWindow").css("display", "block");
						$("#readyButton").attr("class", "hidden");
						$("#notReadyButton").attr("class", "shown");
					}
				);
			}
			
			function applyMapChanges() {
				var isMapDefault = $('input[name=mapConfigRadio]:checked', '#mapForm').val() == "default";
				console.log(isMapDefault);
				var jsonGameConfig;
				if (isMapDefault) {
					jsonGameConfig = {'difficulty': $("#difficultySelect").val()};
					jsonGameConfig['territories'] = [];
					jsonGameConfig['continents'] = [];
					jsonGameConfig['membership'] = [];
					jsonGameConfig['neighbourhood'] = [];
				} else {
					jsonGameConfig = {'difficulty': 'custom'};
					jsonGameConfig['territories'] = [];
					jsonGameConfig['continents'] = [];
					jsonGameConfig['membership'] = [];
					jsonGameConfig['neighbourhood'] = [];
				}

				$.ajax({
					type : "POST",
					url : "./match/gameConfig",
					contentType: 'application/json',
		            dataType: 'json',
					data: JSON.stringify(jsonGameConfig)
				});
			}

			function setNotReady() {
				$.get("./match/notready", function(result) {
					$("#notReadyButton").attr("class", "hidden");
					$("#readyButton").attr("class", "shown");
				});
				warningAlreadyDisplayed = false;
			}

			function joinGame() {
				var matchStarted = true;
				$.ajax({
					type : "POST",
					url : "./match/join",
					data : $("#nameForm").serialize(),
					success : function(result) {
						$("div.modal-body").html("<h2>" + result.responseMessage + "</h2>");
						$("#modalWindow").css("display", "block");
						if (result.responseCode != -1) {
							matchStarted = false;
							source = new EventSource("./match/info");
							source.onmessage = function(evt) {
								var playersArray = JSON.parse(evt.data).players;
								var isMapReady = JSON.parse(evt.data).mapReady;
								if (isEveryoneReady(playersArray) && isMapReady && areThereTwoPlayers(playersArray)) 
									location.replace("http://localhost:8080/drisk/pages/game.html");
								else if (!warningAlreadyDisplayed && isEveryoneReady(playersArray) && !isMapReady){
									$("div.modal-body").html("<h2>Everyone is ready but the map hasn't been created yet</h2>");
									$("#modalWindow").css("display", "block");
									warningAlreadyDisplayed = true;
								}
								else if(!warningAlreadyDisplayed && isEveryoneReady(playersArray) && !areThereTwoPlayers(playersArray)) {
									$("div.modal-body").html("<h2>Waiting another player ready</h2>");
									$("#modalWindow").css("display", "block");
									warningAlreadyDisplayed = true;
								}
								refreshPlayersTable(playersArray);
							};	
						}
						if (!matchStarted) {
							$("#joinButton").attr("class", "hidden");
							$("#exitButton").attr("class", "shown");
							$("#readyButton").attr("class", "shown");
							$("#textName").attr("class", "hidden");
							$("#playersTable").attr("class", "shown");
							$("#rightContentDiv").attr("class", "shown");
						}
					}
				});				
			}
			
			function isEveryoneReady(playersArray) {
				var ready = true;
				for (var i = 0; i < playersArray.length; ++i)
					if (!playersArray[i].ready)
						ready = false;
				return ready;
			}
			
			function areThereTwoPlayers(playersArray) {
				return playersArray.length >= 2;
			}
			
			function refreshPlayersTable(playersArray) {
				$("tr").remove();
				$("#playersTable").append("<tr><th>Name</th><th>Color</th><th>Ready</th></tr>");
				for (var i = 0; i < playersArray.length; ++i) {
					$("#playersTable").append(
							"<tr><td>" + playersArray[i].nickname + "</td>" +
							"<td>" + playersArray[i].color + "</td>" +
							"<td>" + playersArray[i].ready + "</td></tr>"
					);
				}
			}

			function exitGame() {
				$.get("./match/exit");
				if (source != null)
					source.close();
				$("tr").remove(); //clear players table
				$("#joinButton").attr("class", "shown");
				$("#textName").attr("class", "shown");
				$("#exitButton").attr("class", "hidden");
				$("#readyButton").attr("class", "hidden");
				$("#notReadyButton").attr("class", "hidden");
				$("#readyButton").attr("class", "hidden");
				$("#playersTable").attr("class", "hidden");
				$("#rightContentDiv").attr("class", "hidden");
			}
		});