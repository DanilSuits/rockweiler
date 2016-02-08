<#-- @ftlvariable name="" type="rockweiler.domain.webapp.resources.ScratchpadView" -->
<html>
<body>

<#list dto["links"]?keys as link>
<li><a rel="${link}" href="${dto["links"][link]}">${link}</a>
</#list>

<form method="POST" action="${dto["links"]["events"]}">
    <input name="eventName" type="hidden" value="playerAdded">
    <input name="eventId" type="hidden" value="${dto["event"]["eventId"]}">
    <input name="rankingId" type="hidden" value="${dto["event"]["rankingId"]}">
    <label for="rank">Rank: </label><br>
    <input name="rank" type="hidden" value="${dto["event"]["rank"]}">
    <label for="playerName">Name: </label>
    <input name="playerName" type="text" maxlength="50" size="30" autofocus><br>
    <input type="submit" value="Submit">
</form>


<#include "rankedPlayers.ftl">

<#include "debug.ftl">

</body>
</html>