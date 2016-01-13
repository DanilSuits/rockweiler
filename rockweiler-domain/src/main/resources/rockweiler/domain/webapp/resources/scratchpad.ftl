<#-- @ftlvariable name="" type="rockweiler.domain.webapp.resources.ScratchpadView" -->
<html>
<body>

<#list links?keys as link>
<li><a rel="${link}" href="${links[link]}">${link}</a>
</#list>

<form method="POST" action="${links['create']}">
    <label for="name">Name: </label>
    <input  type="text" name="name" maxlength="50" size="30">
    <input type="submit" value="Submit">
</form>


</body>
</html>