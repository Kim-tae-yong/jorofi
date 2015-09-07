## Jogging/Running route finder from geo-graphe ##
Want to find a jogging route for 4<x<4.5km ? 20<x<21km ?

**JoRoFi** takes _GPX_ routes and find all possible ways until some are found matching criteria.


(_GPX_ is Geographic _XML_ data, storing routes, waypoints, tracks)


Heres is how it is possible:
  1. Create a _GPX_ file (e.g.: [Mapsource](Mapsource.md))
  1. Execute JoRoFi on this _GPX_ file
  1. Get the resulting _GPX_ file with some new routes.
The output is a _GPX_ file, which could be open in _Mapsource_, _Google Earth_.

Notes:
  * This is a java program, executable on Linux, Mac, Windows.
  * Yet no downloadable version, please check sources.


Input file format:
  * Each waypoint with name starting with "start-" is a starting point for jogging
  * Add some routes, which could composed by be two or more lines
  * A proposed jogging route is made by the concatenation of 2-n routes


## News ##
17th march 2013: added a simple Swing GUI.
5th march 2013: initial upload of the source code.


## Soon in JoRoFi ? ##
  * Import-export Google Earth, maps
  * Input-Output could be easily adapted to more format.
  * Core graph search could be improved to more criteria (nice view, best ways, imperative points...
  * Your ideas here ?

See more information on the [Wiki](https://code.google.com/p/jorofi/wiki/main)

## Mapsource before-after GPX ##
![https://jorofi.googlecode.com/svn/trunk/design/mapsource.png](https://jorofi.googlecode.com/svn/trunk/design/mapsource.png)

## JoRoFi GUI ##
![https://jorofi.googlecode.com/svn/trunk/design/jorofi-20130317.png](https://jorofi.googlecode.com/svn/trunk/design/jorofi-20130317.png)