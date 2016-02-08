<#if dto["debug"]??>
<h1>Debug</h1>
<#if dto["debug"]["query"]??>
<table>
    <tr><th>name</th><th>value</th></tr>
    <#list dto["debug"]["query"]?keys as param>
    <tr><th>${param}</th>
        <#list dto["debug"]["query"][param] as v>
            <td>${v}</td></tr>
        </#list>
    </#list>
</table>
</#if>
</#if>