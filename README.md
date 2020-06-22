# progetto-risiko-1-team1
progetto-risiko-1-team1 created by GitHub Classroom

Progetto di Ingengeria informatica 2018-1029
Claudio Rota 816050
Simone Paolo Mottadelli 820786
Matteo Paolella 816933
Stefano Zuccarella 816482

How to play game <br>
The game can be played in a Local Area Network or directly online, using a modern browser, but Microsoft Edge and Internet Explorer do not work properly.
<br>The game supports only a match at once. So there can't be multiple users playing in different matches.
If two players want to play on a single computer, they must use two different browsers (e.g Chrome, Firefox), because a player is identified using a sessions
mechanism.
<br>The game page MUST NOT be reloaded using the browser button or F5, otherwise the player will leave the game and, consequently, lose.
<br><br>
Online <br>
The game supports an online version at https://drisk.herokuapp.com/ . Even if there should not be any problems with the server, if something goes wrong, please contact Matteo Paolella (m.paolella1@campus.unimib.it) who will restart the server for you.
<br><br>
LAN <br>
Right click on the project, move the mouse over Run as and then click on Run Configurations....
Now, write tomcat7:run in Goals, and then click on Run.
Wait until the server is up.
When the server is ready, open cmd.exe, run the command ipconfig and search for your IPv4 address.
Then open Firefox or Chrome and paste the following URL: yourIPv4Address:8080/drisk
Have fun!
