<#-- @ftlvariable name="" type="rockweiler.domain.webapp.resources.DashboardView" -->
<html>
<body>
<h1>${title}</h1>

<#list links as link>
    <li><a rel="${link.rel}" href="${link.uri}">${link.rel}</a>
</#list>

</body>
</html>