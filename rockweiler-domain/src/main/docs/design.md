The general plot is to re-create my old command line tools, using better
technologies and better design principles.  This new design is intended to
be very heavily influenced by CQRS, Event Sourcing, Domain Driven Design,
REST, and so forth.

# Minimum Viable Product #

In a drafting tool, the main question I want answered is "which available
player should I draft next?"

If I have a list of the guys I want to track, and a collection of the
players that have been drafted, and I can view the list excluding the
members of the collection, then I'm in good shape.

So I need to be able to make lists.  That's a data entry problem.  I
need to be able to find matches.  So the entries in the need a normalized
form.  I need a query that filters the list.

After several failed prototypes, I recognize that trying to constrain the
entries to normlized form makes the program really hard to use.  I need to
be able to create the lists in a smooth flow, then go back and mitigate
any mistakes that have been made in the data entry.

I also need to be able to improve the protype without requiring that "the
history" be manually modified.

List making, and entity normalization, is a very CRUD concern.  It may be
a while before I find the point where I should be deferring to a domain
model.






