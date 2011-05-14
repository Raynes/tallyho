# TallyHOOOOOOOOOOO

Tallyho is a simple score keeping application. There are all sorts of these things in the Android market, but I couldn't find much of anything like it for non-android devices (my macbook). Alas, I've written one.

Tallyho is written in Clojure and uses [seesaw](http://github.com/daveray/seesaw) for most of the painful Swing stuff.

I wrote this because my family and I play a lot of card games and such. Namely UNO and Quiddler. Frequently, I bring my macbook to the table for any number of reasons, but we still have to use a scorepad or my Android phone for scoring. That's too much technology for one table! With Tallyho, we can score on the macbook in the same way we score on the phone.

# Usage

I use cake as my build tool, but Leiningen should work. For best results, use cake.

You can build tallyho by running `cake bin`. That'll set up an executable you can run with `./tallyho`.

Once I get it in a more completed state, I'll post some downloads for the less technically inclined.
