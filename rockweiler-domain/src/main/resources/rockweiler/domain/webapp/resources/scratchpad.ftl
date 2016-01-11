<#-- @ftlvariable name="" type="rockweiler.domain.webapp.resources.ScratchpadView" -->
<html>
<body>
<h1>${ranking.historyId}</h1>

<#list links as link>
<li><a rel="${link.rel}" href="${link.uri}">${link.rel}</a>
</#list>

<form method="POST">
    <label for="name">Name: </label>
    <input  type="text" name="name" maxlength="50" size="30">
    <input type="submit" value="Submit">
</form>


</body>
</html>