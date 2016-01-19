<#assign m = json?eval>
<html>
<body>
<h1>Json</h1>

<#list m as link>
    <li><a rel="${link.rel}" href="${link.uri}">${link.rel}</a>
</#list>

</body>
</html>