The primary purpose of the league is to manage rosters.  A team
aggregate is an insufficient choice here, because the validity
of a claim depends on the composition of multiple rosters (we need
to validate that the player is not under another team's control)

Note: I'm here distinguishing the problem of rosters (which
players are controlled by which teams) and lineups (the weekly
assignment of players for scoring purposes).  Roster management
is relevant during a larger time span (the offseason and the
draft, do not have lineups)

I want to start with the simplest possible model for
managing rosters during the season.



So limit the changes in events to the movement of players
in the first pass.

PlayerKept
PlayerDrafted
PlayerClaimed
PlayerTraded
PlayerWaived

In each of these cases, we're talking about an identified
player.  Any ambiguities should be resolved in the application
layer before the transaction commands reach the model.  This
is an acceptable restriction because players can be added
automatically to the player repository as soon as they appear
in their first MLB game.  The only exception to this rule is
the draft, which has enough complications that it should be
managed as a separate model.

All roster transactions have an effective date, which will
typically be draft day, or a roster submission deadline.