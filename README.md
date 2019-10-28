Borgy
=====

A tool for borg measurements

See https://en.wikipedia.org/wiki/Rating_of_perceived_exertion

Requirements
============

* OpenJDK 12
* node 10.16.3
* yarn

Setup
=====

    ```./gradlew borgy_init```
    
Run (in dev mode)
===

start backend

    ```./gradlew start_back```
    
start frontend

    ```./gradlew start_front```
    
notes
=====
[This](https://github.com/toddlucas/react-tsx-starter) seems like a good react typescript example


Application users
=================
researcher - this person will be in charge of starting a trial, ending a trial and perhaps monitoring a trial.
subject - This is the person for who the trial is being run. They will press the buttons during the trial to show how tired (etc) that they are.

