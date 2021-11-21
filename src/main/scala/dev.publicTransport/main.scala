package dev.publicTransport

import scala.collection.mutable
import scala.collection.mutable.ListBuffer
import scala.io.Source

object main extends App {

  case class Connection(source: String, destination: String, travelTime: Int)

  // to store all input data
  var connectionList = ListBuffer[Connection]()

  // a map to store station to index data
  var stationToIndexMap = mutable.Map[String, Int]()
  var stationList = ListBuffer[String]()
  var stationIndex = 0

  // read data from a file
  val bufferedSource = Source.fromFile("src/main/scala/resources/data.txt")
  val lines = bufferedSource.getLines().toList
  bufferedSource.close()

  val numEdges = lines.head.toInt

  // populating stationToIndexMap, stationList
  for(i <- 1 to numEdges){
    val connection = lines(i)
    println(connection)
    val source = connection.split("->")(0).replaceAll("\\s","")
    val destination = (connection.split("->")(1)).split(":")(0).replaceAll("\\s","")
    val travelTime = (connection.split("->")(1)).split(":")(1).replaceAll("\\s","").toInt

    // check if the source is already present in stationToIndexMap
    if(!stationToIndexMap.keySet.contains(source)){
      stationToIndexMap += (source->stationIndex)
      if(!stationList.contains(source)){
        stationList += source
      }
      stationIndex += 1
    }

    // check if the destination is already present in stationToIndexMap
    if(!stationToIndexMap.keySet.contains(destination)){
      stationToIndexMap += (destination->stationIndex)
      if(!stationList.contains(destination)){
        stationList += destination
      }
      stationIndex += 1
    }

    // storing the data because we don't know number of stations(vertices) yet
    connectionList += Connection(source, destination, travelTime)

  }

  val numStations = stationToIndexMap.size

  // routing query variables
  val routingSource = lines(numEdges+1).split("->")(0).replaceAll("route","").replaceAll("\\s","")
  val routingDestination = lines(numEdges+1).split("->")(1).replaceAll("\\s","")

  // actual algo
  var visitedStations = new ListBuffer[String]()
  var unVisitedStations = stationList
  var timeTakenFromSource = Array.fill(numStations)(Int.MaxValue)
  var shortestPathFromSource = Array.fill(numStations)("")
  var connectionFound: Boolean = false

  val routingSourceIndex: Int = stationToIndexMap.getOrElse(routingSource, -1)
  if(routingSourceIndex == -1){
    println("routing Source station not found in the station list")
  }

  // set time taken from source for source index to 0
  timeTakenFromSource(routingSourceIndex) = 0

  while(unVisitedStations.nonEmpty){
    // obtain current station
    var currentStation = ""
    var currentStationIndex = -1
    var minimumTimeTakenFromCurrentStation = Int.MaxValue
    // should obtain current station from unvisited stations
    for(station <- unVisitedStations){
      val stationIndex = stationToIndexMap.getOrElse(station, -1)
      if( minimumTimeTakenFromCurrentStation >  timeTakenFromSource(stationIndex)){
        minimumTimeTakenFromCurrentStation = timeTakenFromSource(stationIndex)
        currentStation = station
        currentStationIndex = stationIndex
      }
    }

    // now remove current station from unvisited and add to visited
    visitedStations += currentStation
    unVisitedStations -= currentStation

    // loop through unvisited stations
    for(unVisitedStation <- unVisitedStations){
      val unVisitedStationIndex = stationToIndexMap.getOrElse(unVisitedStation, -1)
      var effectiveTimeTaken: Int = timeTakenFromSource(currentStationIndex)
      connectionFound = false
      for(connection <- connectionList){
        if(connection.source == currentStation && connection.destination == unVisitedStation){
          val travelTimeBetweenSourceToDestination: Int = connection.travelTime
          effectiveTimeTaken += travelTimeBetweenSourceToDestination
          connectionFound = true
        }
      }
      if(!connectionFound){
        effectiveTimeTaken = Int.MaxValue
      }

      // update time taken from source if the effective time taken is found to be
      if(effectiveTimeTaken < timeTakenFromSource(unVisitedStationIndex)){
        timeTakenFromSource(unVisitedStationIndex) = effectiveTimeTaken
        if(shortestPathFromSource(currentStationIndex).isEmpty){
          shortestPathFromSource(currentStationIndex) = routingSource
        }
        shortestPathFromSource(unVisitedStationIndex) = shortestPathFromSource(currentStationIndex) + "-> " + unVisitedStation
      }
    }
  }

  val routingDestinationIndex = stationToIndexMap.getOrElse(routingDestination, -1)
  println(s"Shortest path from ${routingSource} to ${routingDestination} is")
  println(shortestPathFromSource(routingDestinationIndex))

  // for 2nd query

  // nearby query variables
  val nearbySource = lines(numEdges+2).split(",")(0).replaceAll("nearby","").replaceAll("\\s","")
  val maxTravelTime = lines(numEdges+2).split(",")(1).replaceAll("\\s","").toInt

  println(s"Nearby source: ${nearbySource}, max travel time: ${maxTravelTime}")
  var queryResult = ""
  val destinationStations = visitedStations

  if(nearbySource!=nearbySource){
    // have to run algo for nearbySource instead of routing source for query 2
    unVisitedStations = destinationStations
    visitedStations = new ListBuffer[String]()

    timeTakenFromSource = Array.fill(numStations)(Int.MaxValue)
    shortestPathFromSource = Array.fill(numStations)("")

    val routingSourceIndex: Int = stationToIndexMap.getOrElse(nearbySource, -1)
    if(routingSourceIndex == -1){
      println("routing Source station not found in the station list")
    }

    // set time taken  from source for source index to 0
    timeTakenFromSource(routingSourceIndex) = 0

    while(unVisitedStations.nonEmpty){
      // obtain current station
      var currentStation = ""
      var currentStationIndex = -1
      var minimumTimeTakenFromCurrentStation = Int.MaxValue
      // should obtain current station from unvisited stations
      for(station <- unVisitedStations){
        val stationIndex = stationToIndexMap.getOrElse(station, -1)
        if( minimumTimeTakenFromCurrentStation >  timeTakenFromSource(stationIndex)){
          minimumTimeTakenFromCurrentStation = timeTakenFromSource(stationIndex)
          currentStation = station
          currentStationIndex = stationIndex
        }
      }

      // now remove current station from unvisited and add to visited
      visitedStations += currentStation
      unVisitedStations -= currentStation

      // loop through unvisited stations
      for(unVisitedStation <- unVisitedStations){
        val unVisitedStationIndex = stationToIndexMap.getOrElse(unVisitedStation, -1)
        var effectiveTimeTaken: Int = timeTakenFromSource(currentStationIndex)
        connectionFound = false
        for(connection <- connectionList){
          if(connection.source == currentStation && connection.destination == unVisitedStation){
            val travelTimeBetweenSourceToDestination: Int = connection.travelTime
            effectiveTimeTaken += travelTimeBetweenSourceToDestination
            connectionFound = true
          }
        }
        if(!connectionFound){
          effectiveTimeTaken = Int.MaxValue
        }

        // update time taken from source if the effective time taken is found to be
        if(effectiveTimeTaken < timeTakenFromSource(unVisitedStationIndex)){
          timeTakenFromSource(unVisitedStationIndex) = effectiveTimeTaken
          if(shortestPathFromSource(currentStationIndex).isEmpty){
            shortestPathFromSource(currentStationIndex) = nearbySource
          }
          shortestPathFromSource(unVisitedStationIndex) = shortestPathFromSource(currentStationIndex) + "-> " + unVisitedStation
        }
      }
    }

  }


  for(destination <- destinationStations){
    if(destination != nearbySource){
      val destinationIndex = stationToIndexMap.getOrElse(destination,-1)
      if(timeTakenFromSource(destinationIndex) <= maxTravelTime ){
        if(queryResult.isEmpty){
          queryResult = destination + ":" + timeTakenFromSource(destinationIndex)
        } else {
          queryResult = queryResult+ ", " + destination + ":" + timeTakenFromSource(destinationIndex)
        }

      }
    }
  }

  println(queryResult)

}