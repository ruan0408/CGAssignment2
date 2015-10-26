We implemented the following extensions:

1)  Avatar with walking animation. The code for this is at Avatar.draw().

2)  Nigh mode. Press 'N' to switch to night mode. Code can be found at Game.setSpotlightProperties(),
and Game.correctLighting() and Terrain.keyPressed().

3)  Sun movement. Press 'R' and 'T' to rotate the sun. Code at Sun.java and Terrain.keyPressed().

To change the view from 1st person <--> 3rd person, press the space bar. Code at Camera.java.
We are using shaders all the time. We change between them according to if it's night or day (because during)
night we have a spotlight.

The file test8 was used to test the tardis object. All test files, shaders and textures are located at res/