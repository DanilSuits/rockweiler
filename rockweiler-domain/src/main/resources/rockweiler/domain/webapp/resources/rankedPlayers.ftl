<#if dto["rankedPlayers"]??>

<table>
    <tr><th>rank</th><th>name</th></tr>
    <#list dto["rankedPlayers"] as player>

        <tr><th>${player?counter}</th>
            <td>${player}</td></tr>

    </#list>
</table>
</#if>