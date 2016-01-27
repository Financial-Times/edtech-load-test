package utils

import scala.util.Random

object RandomGenerator {

  var persistentStringArray = Array[String]()

  def string(charCount: Int): String ={
    var string = Random.alphanumeric.take(6).mkString

    while(persistentStringArray contains string){
      string=Random.alphanumeric.take(charCount)mkString
    }
    persistentStringArray :+ string

    return string

  }
}
