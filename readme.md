# Public Transport routing
This project consists of sample public transport routing application which takes in list of routes and when queried for best path between two stations or nearby stations within time limit responds accordingly.

# Sample input and output
Inputs are provided via text file.
### Query 1
`route A -> B`

`A-> C-> B`

### Query 2
`nearby A, 130`

`C:70, D:120, B:130`

# Setup steps
1. Download scala and sbt
2. Enter the public-transport-routing folder and run following command
   ```sbt run```

## Approach
1. Created a sample sbt project
2. Created the text file with all necessary inputs
3. Sourced the text file and read the input data
4. Created a list to hold all station names and a map to store station with a value. This map is useful in later stages
5. For Query1, approach was to take the source and find out distances(time taken in this case) for all the neighbouring stations
6. Created ListBuffer and ArrayBuffer to keep track of visited/unvisited stations along with distance from source and shortest path
7. First we start at source and update the distance and path arrays of the neighbours to which source was connected (the index mapping was used here as each of station is mapped to an index)
8. Once done, we move source to visitedStations and remove it from unvisitedStations
9. Now we move to next station from the list of unvisited stations which is at closest distance from the source and repeat the same process of updating distance, path is the newly calculated effective distance is less than the previously assigned distance for that neighbour
10. We repeat this step until the unVisited stations become empty
11. Now we have the summary of all the stations with their distance from the source as well as shortest path leading to that distance
12. We just print out the corresponding path for destination queried
13. For Query2, in case the source specified is different from routing source, then steps 5 to 11 will be repeated else we can use the existing data itself
14. We then query which neighbours have travel time less than or equal to provided max travel time and print them in our result

## What can be improved?
1. Variable usage can be further simplified and code could be more concise
2. Test cases to be included