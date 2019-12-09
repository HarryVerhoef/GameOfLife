# Game Of Life

To start the game of life, make whatever changes you want to `map.txt` such that a 5x5 map with a cross in the middle is represented as so:

0 0 1 0 0
0 0 1 0 0
1 1 1 1 1
0 0 1 0 0
0 0 1 0 0

Of course, you are free to design whatever sort of map you please (the one above, when computed, is actually very boring).

To run the game of life, execute the following command in the command-line:

    java GameOfLife /path/to/map/ <n>
where n represents the maximum number of iterations you want to do. (May be less, due to potential mitigated cycles).

Enjoy!

**Note that Java >=8 is required to run this game.**
