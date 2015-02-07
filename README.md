# Perf Demos

What better teaching material than creating actual useable modules? For the performance matters series, I plan on recuperating various public project code (from Hackathons I participated in with fellow GDG members).

For various perf videos, the idea will be to create a feature branch showing a potential pitfall, and each commit to the given branch bringing us towards efficiency or improvements.

## Custom Views and Profiling Tools

This is the companion code to my in-progress Custom-views + Profiling tool content.

Key classes:

- class **WelcomeActivity.java**
- class **FractionCustomView.java**

Good links: 

- [Android Performance Pattern G+ community](https://plus.google.com/u/0/communities/116342551728637785407)
- [Presentation 1](https://www.parleys.com/play/514892290364bc17fc56c533/chapter0/about)
- [Presentation 2](https://developers.google.com/events/io/2012/sessions/gooio2012/109/)


## 'Ballot' App

[Will re-use code from WearWolf project here.] 

Easy voting for groups, using mobile devices. Registration via QR code. 

Android TV client, Mobile client, should have slight differences, but probably should mirror each other feature wise.

Try out Google Cloud as a Server, if time is available.

Top level features:

- Ballot creation
- QR Code subscription (and app install)
- Voting
- Results

## NVScene Demo

[Adapted and extended code from various Renderscript and OpenGL shader examples/talks I've done last year.]

Focus on animations, and how to analyze OpenGL pipelines, etc.

First phase is to create a few animations around optical effects, and learn how to transition from one animation to another, all while staying on beat (using Fourrier transform and hooking certain inputs on bass/mid/treble.

