<#-- @ftlvariable name="" type="rockweiler.domain.webapp.resources.ScratchpadView" -->
<html>
<body>

<#list dto["links"]?keys as link>
<li><a rel="${link}" href="${dto["links"][link]}">${link}</a>
</#list>

<form method="POST">
    <label for="name">Name: </label>
    <input  type="text" name="name" maxlength="50" size="30" autofocus>
    <input type="submit" value="Submit">
</form>

<ol>
<#list dto["rankedPlayers"] as player>
<li>${player?counter} ${player.name}
</#list>
</ol>


<#include "debug.ftl">

</body>
</html>